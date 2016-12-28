package service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;

import entity.Word;
import enumeration.DomainProperty;
import enumeration.MatchProperty;
import utils.Pagination;

@Service
public class WordService extends BaseService<Word> {
	public Word save(Word word) {
		return (Word) super.save(word);
	}

	@SuppressWarnings("unchecked")
	public List<String> findByParams(String word, String match, String domain, String dictionaries, String logon) {
		String[] dictionaryArray = dictionaries.split("@");

		for (MatchProperty matchEnum : MatchProperty.values()) {
			if (match.equals(matchEnum.getValue())) {
				for (DomainProperty domainEnum : DomainProperty.values()) {
					if (domain.equals(domainEnum.getValue())) {
						String key = "";
						BasicDBObject queryCondition = new BasicDBObject();

						if ("quanwen".equals(domain)) {
							BasicDBList values = new BasicDBList();
							key = DomainProperty.getKey(domainEnum);
							for (String item : key.split("-")) {
								if (logon.equals("N"))
									values.add(new BasicDBObject(item, new BasicDBObject("$regex", ".*" + word + ".*"))
											.append("dictionary.id", new BasicDBObject("$in", dictionaryArray))
											.append("status", "published"));
								else {
									values.add(new BasicDBObject(item, new BasicDBObject("$regex", ".*" + word + ".*"))
											.append("dictionary.id", new BasicDBObject("$in", dictionaryArray)));
								}
							}
							queryCondition.put("$or", values);
						} else if (logon.equals("N")) {
							queryCondition = new BasicDBObject(DomainProperty.getKey(domainEnum),
									new BasicDBObject("$regex", MatchProperty.getRegex(matchEnum, word)))
											.append("dictionary.id", new BasicDBObject("$in", dictionaryArray))
											.append("status", "published");
						} else {
							queryCondition = new BasicDBObject(DomainProperty.getKey(domainEnum),
									new BasicDBObject("$regex", MatchProperty.getRegex(matchEnum, word)))
											.append("dictionary.id", new BasicDBObject("$in", dictionaryArray));
						}

						return this.mongoTemplate.getCollection(getCollectionName()).distinct("word", queryCondition);
					}
				}
			}
		}

		return null;
	}
	
	/**
	 * 根据HK映射表进行替换，大小写敏感
	 * 按照最长子串进行匹配
	 * 
	 */
	public String replaceByHK(String input) {
		
		Map<String, String> normalMap = new HashMap<String, String> ();
		String output = null;
		normalMap.put("A", "ā");
		normalMap.put("I", "ī");
		normalMap.put("U", "ū");
		normalMap.put("R", "ṛ");
		normalMap.put("lR", "ḷ");
		normalMap.put("M", "ṃ");
		normalMap.put("H", "ḥ");
		normalMap.put("G", "ṅ");
		normalMap.put("J", "ñ");
		normalMap.put("T", "ṭ");
		normalMap.put("D", "ḍ");
		normalMap.put("N", "ṇ");
		normalMap.put("z", "ś");
		normalMap.put("S", "ṣ");
		
		Map<String, String> specialMap = new HashMap<String, String> ();
		specialMap.put("RR", "ṝ");
		specialMap.put("lRR", "ḹ");
		
		for(Entry<String, String> entry : specialMap.entrySet()) {
			if (input.contains(entry.getKey())) {
				output = input.replace(entry.getKey(), entry.getValue());
				input = output;
			}
		}
		
		for(Entry<String, String> entry : normalMap.entrySet()) {
			if (input.contains(entry.getKey())) {
				output = input.replace(entry.getKey(), entry.getValue());
				input = output;
			}
		}
		
		return output == null ?input:output;
	}
	
	/**
	 * 根据模糊映射表进行替换，大小写不敏感
	 * 
	 */
	public List<String> replaceByVague(String input) {
		
		
		// 创建一个Map<String, String> 的结构存储映射关系
		Map<String, String> map = new HashMap<String, String>();
		List<String> list = new ArrayList<String>();
		
		map.put("a", "aā");
		map.put("A", "aā");
		map.put("i", "iī");
		map.put("I", "iī");
		map.put("u", "uū");
		map.put("U", "uū");
		map.put("l", "lḷḹ");
		map.put("L", "lḷḹ");
		map.put("m", "mṃ");
		map.put("M", "mṃ");
		map.put("h", "hḥ");
		map.put("H", "hḥ");
		map.put("n", "nṇṅñ");
		map.put("N", "nṇṅñ");
		map.put("t", "tṭ");
		map.put("T", "tṭ");
		map.put("d", "dḍ");
		map.put("D", "dḍ");
		map.put("s", "sśṣ");
		map.put("S", "sśṣ");
		this.replaceNext(input, map, list);
		if (list == null || list.size() ==0) {
			list.add(input);
		}
		return list;
	}
	
	private void replaceNext(String input, Map<String, String> map, List<String> list) {
		this.replaceNext(input, map, null, list);
	}
	
	
	private void replaceNext(String input, Map<String, String> map, String prefix, List<String> list) {
		String currentChar = String.valueOf(input.charAt(0));
		if (prefix == null) {
			prefix = "";
		} 
		if(map.containsKey(currentChar)){
			char[] cr = map.get(currentChar).toCharArray();
			for(int i=0; i<cr.length;i++) {
				
				String nextLayerPrefix = prefix + String.valueOf(cr[i]);
				if(input.length()>1) {
					replaceNext(input.substring(1, input.length()), map, nextLayerPrefix, list);
				}else {
					list.add(nextLayerPrefix);
				} 
			}
		}else {
			prefix += currentChar;
			if (input.length() == 1) {
				list.add(prefix);
				return;
			}
			replaceNext(input.substring(1, input.length()), map, prefix, list);
		}
	}

	public Pagination<Word> findByDate(String period, String pCount, int page, int pageSize) {
		Query query = queryByDate(period, pCount);

		if (query != null) {
			return getPageWord(page, pageSize, query);
		}
		return null;
	}

	public Pagination<Word> findWordByUserId(String userId, int page, int pageSize) {
		return getPageWord(page, pageSize, Query.query(Criteria.where("author.id").is(userId))
				.with(new Sort(Sort.Direction.DESC, new String[] { "lastEditDateTime" })));
	}

	private Query queryByDate(String period, String pCount) {
		Calendar cal = Calendar.getInstance();
		Date now = new Date();
		cal.setTime(now);
		if ((period != null) && (!"".equals(period)) && (pCount != null) && (!"".equals(pCount))) {
			if (period.equals("Week"))
				cal.add(8, -Integer.valueOf(pCount).intValue());
			else if (period.equals("Month"))
				cal.add(2, -Integer.valueOf(pCount).intValue());
			else if (period.equals("Year")) {
				cal.add(1, -Integer.valueOf(pCount).intValue());
			}
			return Query.query(Criteria.where("lastEditDateTime").gte(Long.valueOf(cal.getTimeInMillis())).and("importflag").is(false))
					.with(new Sort(Sort.Direction.DESC, new String[] { "lastEditDateTime" }));
		}
		return null;
	}

	public long queryCount(String period, String pCount) {
		Query query = queryByDate(period, pCount);

		if (query != null) {
			return queryCount(query);
		}

		return 0L;
	}

	public long queryCount(String userId) {
		return queryCount(Query.query(Criteria.where("author.id").is(userId)));
	}

	public Pagination<Word> getPageWord(int pageNo, int pageSize, Query query) {
		return super.getPage(pageNo, pageSize, query);
	}

	public void updateById(String id, String jsonToUpdate) {
		super.updateById(id, jsonToUpdate);
	}

	public void updateMulti(Map<String, Object> map, Word word) {
		super.updateMulti(map, word);
	}

	public void publishWord(String id) {
		super.findAndModify(Query.query(Criteria.where("id").is(id)), Update.update("status", "checked"));
	}

	public Word findWordById(String id) {
		return (Word) super.findById(id);
	}

	public List<Word> findWordByName(String word, String logon) {
		if ("N".equals(logon)) {
			return super.find(Query.query(Criteria.where("word").is(word).and("status").is("published")));
		}
		return super.find(Query.query(Criteria.where("word").is(word)));
	}

	public List<Word> findWordsByDictionaryName(String dictionaryName) {
		if (dictionaryName.contains("_")) {
			dictionaryName = dictionaryName.replace("_", " ");
		}
		return super.find(Query.query(Criteria.where("dictionary.displayName").is(dictionaryName)));
	}

	public Word findWordByMultipleParam(String word, String status) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("word", word);
//		params.put("dictionary", dictionary);
		params.put("status", status);
		return (Word) super.findOne(params);
	}

	public long queryCount(Query query) {
		return super.queryCount(query);
	}

	public void insertAll(Collection<Word> objects) {
		super.insertAll(objects);
	}

	public void removeByDictionaryId(String dictionaryId) {
		super.removeByCriteria(Query.query(Criteria.where("dictionary.id").is(dictionaryId)));
	}

	public void removeById(String id) {
		super.removeByCriteria(Query.query(Criteria.where("id").is(id)));
	}

	protected Class<Word> getEntityClass() {
		return Word.class;
	}
}