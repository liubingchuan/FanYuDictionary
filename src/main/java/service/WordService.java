package service;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
			return Query.query(Criteria.where("lastEditDateTime").gte(Long.valueOf(cal.getTimeInMillis())))
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

	public Word findWordByMultipleParam(String word, Map<String, Object> dictionary, String status) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("word", word);
		params.put("dictionary", dictionary);
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