package service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import entity.User;
import utils.Pagination;

@Service
public class UserService extends BaseService<User> {
	public User save(User user) {
		return (User) super.save(user);
	}

	public User findUserByName(String name) {
		Map<String, String> criteria = new HashMap<String, String>();
		criteria.put("username", name);
		return (User) super.findOne(criteria);
	}

	public List<User> findUsersByAllowedDictionary(String dictionaryId) {
		return super.find(Query.query(Criteria.where("allowedDictionaries.id").is(dictionaryId)));
	}

	public Pagination<User> getPageUser(int pageNo, int pageSize, Query query) {
		return super.getPage(pageNo, pageSize, query);
	}

	public void updateDicSequence(String userId, Map<String, Object> dicSequence) {
		super.findAndModify(Query.query(Criteria.where("id").is(userId)), Update.update("dicSequence", dicSequence));
	}

	public User findUserById(String id) {
		Map<String, String> criteria = new HashMap<String, String>();
		criteria.put("id", id);
		return (User) super.findOne(criteria);
	}

	public void updateById(String id, String jsonToUpdate) {
		super.updateById(id, jsonToUpdate);
	}

	public Pagination<User> getPageUser(int pageNo, int pageSize) {
		return super.getPage(pageNo, pageSize,
				new Query().with(new Sort(Sort.Direction.ASC, new String[] { "username" })));
	}

	public long queryCount() {
		return super.queryCount(new Query());
	}

	public void removeById(String id) {
		super.findAndRemove(Query.query(Criteria.where("id").is(id)));
	}

	public void updateById(String id, Map<String, Object> params) {
		super.updateById(id, params);
	}

	protected Class<User> getEntityClass() {
		return User.class;
	}
}