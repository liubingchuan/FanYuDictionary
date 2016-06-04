package service;

import java.util.List;

import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import entity.Dictionary;

@Service
public class DictionaryService extends BaseService<Dictionary> {
	public Dictionary save(Dictionary dictionary) {
		return (Dictionary) super.save(dictionary);
	}

	public List<Dictionary> findAll() {
		return this.mongoTemplate.findAll(getEntityClass());
	}

	public void removeById(String id) {
		super.findAndRemove(Query.query(Criteria.where("id").is(id)));
	}

	public Dictionary findById(String id) {
		return (Dictionary) super.findOne(Query.query(Criteria.where("id").is(id)));
	}

	public void updateById(String id, String jsonToUpdate) {
		super.updateById(id, jsonToUpdate);
	}

	public Dictionary findByDisplayName(String displayName) {
		return (Dictionary) super.findOne(Query.query(Criteria.where("displayName").is(displayName)));
	}

	public Dictionary findByShortName(String shortName) {
		return (Dictionary) super.findOne(Query.query(Criteria.where("shortName").is(shortName)));
	}

	public String filtByDicGroup(String dictionaries) {
		String[] dicArray = dictionaries.split("@");
		String dicGroup = "";
		String afterFilter = "";
		for (int i = dicArray.length - 1; i >= 0; i--) {
			String dictionaryId = dicArray[i];
			Dictionary dictionary = findById(dictionaryId);
			if (dictionary != null) {
				if (i == dicArray.length - 1) {
					dicGroup = dictionary.getDicGroup();
					afterFilter = dicArray[i];
				} else {
					if (!dicGroup.equals(dictionary.getDicGroup()))
						break;
					if (!afterFilter.contains(dicArray[i])) {
						afterFilter = afterFilter + "@";
						afterFilter = afterFilter + dicArray[i];
					}
				}

			}

		}

		return afterFilter;
	}

	protected Class<Dictionary> getEntityClass() {
		return Dictionary.class;
	}
}