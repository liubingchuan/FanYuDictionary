package service;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Order;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import com.google.gson.ExclusionStrategy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.mongodb.BasicDBObject;
import com.mongodb.WriteResult;

import utils.JsonKit;
import utils.Pagination;

@SuppressWarnings("deprecation")
public abstract class BaseService<T> {

	@Autowired
	protected MongoTemplate mongoTemplate;

	public T jsonToEntity(String jsonString, Class<T> clazz) {
		T t = null;
		try {
			Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
			t = gson.fromJson(jsonString, clazz);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return t;
	}

	public String entityToJson(Object entity) {
		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		String str = gson.toJson(entity);
		return str;
	}

	public void ensureIndex(Class<T> clazz, String key, Order order) {
		this.mongoTemplate.indexOps(clazz).ensureIndex(new Index().on(key, order));
	}

	public String entityToJson(Object entity, String[] keys) {
		Gson gson = new GsonBuilder().setExclusionStrategies(new ExclusionStrategy[] { new JsonKit(keys) }).create();
		String str = gson.toJson(entity);
		return str;
	}

	public String entityToJsonWithoutAnnotation(Object entity) {
		Gson gson = new Gson();
		String str = gson.toJson(entity);
		return str;
	}

	public String listToJson(List<?> list) {
		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		return gson.toJson(list);
	}

	@SuppressWarnings("unchecked")
	public List<T> jsonToList(String json) {
		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		List<T> list = (List<T>) gson.fromJson(json, new TypeToken<T>() {
		}.getType());
		return list;
	}

	public String listToJson(List<?> list, String[] keys) {
		Gson gson = new GsonBuilder().setExclusionStrategies(new ExclusionStrategy[] { new JsonKit(keys) }).create();
		return gson.toJson(list);
	}

	public String listToJsonPretty(List<?> list, String[] keys) {
		Gson gson = new GsonBuilder().setExclusionStrategies(new ExclusionStrategy[] { new JsonKit(keys) })
				.disableHtmlEscaping().setPrettyPrinting().create();
		JsonParser jp = new JsonParser();
		JsonElement je = jp.parse(gson.toJson(list));
		return gson.toJson(je);
	}

	protected Pagination<T> getPage(int pageNo, int pageSize, Query query) {
		long totalCount = queryCount(query);
		Pagination<T> page = new Pagination<T>(pageNo, pageSize, totalCount);
		query.skip(page.getFirstResult());
		query.limit(pageSize);
		List<T> datas = find(query);
		page.setDatas(datas);
		return page;
	}

	protected Pagination<T> getPage(int pageNo, int pageSize) {
		return getPage(pageNo, pageSize, new Query());
	}

	public long queryCount(Query query) {
		return this.mongoTemplate.count(query, getEntityClass());
	}

	protected List<T> find(Query query) {
		return this.mongoTemplate.find(query, getEntityClass());
	}

	protected T findOne(Query query) {
		return this.mongoTemplate.findOne(query, getEntityClass());
	}

	protected T findOne(Map<String, ? extends Object> params) {
		Query query = new Query();
		Criteria criteria = new Criteria();
		boolean OK = false;
		for (Map.Entry<String, ? extends Object> entry : params.entrySet()) {
			if (OK) {
				criteria = criteria.and((String) entry.getKey()).is(entry.getValue());
			} else {
				criteria = Criteria.where((String) entry.getKey()).is(entry.getValue());
				OK = true;
			}
		}
		query.addCriteria(criteria);
		return findOne(query);
	}

	protected List<T> findAll() {
		return this.mongoTemplate.findAll(getEntityClass());
	}

	protected T findAndModify(Query query, Update update) {
		return this.mongoTemplate.findAndModify(query, update, getEntityClass());
	}

	protected T findAndRemove(Query query) {
		return this.mongoTemplate.findAndRemove(query, getEntityClass());
	}

	protected void removeByProperty(Query query) {
		this.mongoTemplate.remove(query, getEntityClass());
	}

	protected void updateFirst(Query query, Update update) {
		this.mongoTemplate.updateFirst(query, update, getEntityClass());
	}

	protected void updateMulti(Map<String, Object> map, T bean) {
		boolean start = true;
		Criteria criteria = new Criteria();
		Update update = new Update();
		for (Map.Entry<String, Object> KV : map.entrySet()) {
			char[] array = ((String) KV.getKey()).toCharArray();
			int tmp66_65 = 0;
			char[] tmp66_63 = array;
			tmp66_63[tmp66_65] = ((char) (tmp66_63[tmp66_65] - ' '));
			String methodName = "get" + String.valueOf(array);
			Object value = new Object();
			try {
				value = getEntityClass().getDeclaredMethod(methodName, new Class[0]).invoke(bean, new Object[0]);
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			} catch (SecurityException e) {
				e.printStackTrace();
			}
			if (start) {
				criteria = Criteria.where((String) KV.getKey()).is(KV.getValue());

				update = Update.update((String) KV.getKey(), value);
				start = false;
			} else {
				criteria = criteria.and((String) KV.getKey()).is(KV.getValue());

				update = update.set((String) KV.getKey(), value);
			}
		}
		Query query = new Query(criteria);

		this.mongoTemplate.updateMulti(query, update, getEntityClass());
	}

	protected void updateById(String id, Map<String, Object> params) {
		Update update = new Update();
		for (Map.Entry<String, ? extends Object> entry : params.entrySet()) {
			Update.update((String) entry.getKey(), entry.getValue());
		}
		updateFirst(Query.query(Criteria.where("id").is(id)), update);
	}

	@SuppressWarnings("unchecked")
	protected void updateById(String id, String jsonToUpdate) {
		@SuppressWarnings("rawtypes")
		Map map = (Map) new Gson().fromJson(jsonToUpdate, Map.class);
		StringBuilder sb = new StringBuilder(getCollectionName());
		sb.setCharAt(0, Character.toUpperCase(sb.charAt(0)));
		String str = sb.toString();
		map.put("_class", "entity." + str);
		if (id.contains("-"))
			this.mongoTemplate.getCollection(getCollectionName()).update(new BasicDBObject("_id", id),
					new BasicDBObject(map));
		else
			this.mongoTemplate.getCollection(getCollectionName()).update(new BasicDBObject("_id", new ObjectId(id)),
					new BasicDBObject(map));
	}

	protected T save(T bean) {
		this.mongoTemplate.save(bean);
		return bean;
	}

	protected T findById(String id) {
		return this.mongoTemplate.findById(id, getEntityClass());
	}

	protected T findById(String id, String collectionName) {
		return this.mongoTemplate.findById(id, getEntityClass(), collectionName);
	}

	protected void removeByCriteria(Query query) {
		this.mongoTemplate.remove(query, getEntityClass());
	}

	protected void removeAll() {
		this.mongoTemplate.dropCollection(getEntityClass());
	}

	protected void insertAll(Collection<T> objects) {
		this.mongoTemplate.insertAll(objects);
	}

	protected void insert(List<T> batchToSave) {
		this.mongoTemplate.insert(batchToSave, getEntityClass());
	}

	public WriteResult updateMulti(Query query, Update update) {
		return this.mongoTemplate.updateMulti(query, update, getEntityClass());
	}

	protected abstract Class<T> getEntityClass();

	protected String getCollectionName() {
		return getEntityClass().getSimpleName().toLowerCase();
	}
}