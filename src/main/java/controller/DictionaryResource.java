package controller;

import entity.Dictionary;
import entity.User;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import service.DictionaryService;
import service.UserService;
import service.WordService;

@Path("/dictionary")
public class DictionaryResource {

	@Autowired
	private DictionaryService dictionaryService;

	@Autowired
	private WordService wordService;

	@Autowired
	private UserService userService;
	private static final Log LOGGER = LogFactory.getLog(DictionaryResource.class);

	@GET
	@Produces({ "application/json" })
	public Response getDictionaries(@QueryParam("logon") String logon) {
		List<Dictionary> dictionaryList = this.dictionaryService.findAll();
		if ("N".equals(logon)) {
			for (int i = 0; i < dictionaryList.size(); i++) {
				if (dictionaryList.get(i).getCopyRight() == 1) {
					dictionaryList.remove(i);
					i--;
				}

			}

		}

		for (int i = 0; i < dictionaryList.size(); i++) {
			if (((Dictionary) dictionaryList.get(i)).getCopyRight() == 1)
				((Dictionary) dictionaryList.get(i)).setCopyRightLiteral("内部");
			else {
				((Dictionary) dictionaryList.get(i)).setCopyRightLiteral("全部");
			}
		}
		LOGGER.info("return dictionary list successfully");
		return Response.status(200).entity(this.dictionaryService.listToJson(dictionaryList)).type("application/json")
				.build();
	}

	@POST
	@Produces({ "text/plain" })
	public Response saveDictionary(String dictionaryJson) {
		Dictionary dictionary = (Dictionary) this.dictionaryService.jsonToEntity(dictionaryJson, Dictionary.class);

		if (dictionary == null) {
			return Response.status(404).entity("HTTP Body 中传入的数据有误").type("text/plain").build();
		}
		String displayName = dictionary.getDisplayName();
		Dictionary persistedDic1 = this.dictionaryService.findByDisplayName(displayName);

		if ((persistedDic1 != null) && (persistedDic1.getId() != null) && (!"".equals(persistedDic1.getId()))) {
			return Response.status(200).entity("error-displayName").type("text/plain").build();
		}

		String shortName = dictionary.getShortName();
		Dictionary persistedDic2 = this.dictionaryService.findByShortName(shortName);

		if ((persistedDic2 != null) && (persistedDic2.getId() != null) && (!"".equals(persistedDic2.getId()))) {
			return Response.status(200).entity("error-shortName").type("text/plain").build();
		}

		Date date = new Date();
		dictionary.setCreateDateTime(date.getTime());
		dictionary.setStatus("active");
		if (dictionary.getCopyRight() == 1)
			dictionary.setCopyRightLiteral("内部");
		else {
			dictionary.setCopyRightLiteral("全部");
		}
		this.dictionaryService.save(dictionary);
		LOGGER.info("save dictionary info successfully");
		return Response.status(200).entity("success").type("text/plain").build();
	}

	@PUT
	@Produces({ "text/plain" })
	@Path("{dictionaryId}")
	public Response updateDictionary(@PathParam("dictionaryId") String dictionaryId, String body) {
		if ((dictionaryId == null) || ("".equals(dictionaryId))) {
			return Response.status(412).entity("dictionaryId 不允许为空").type("text/plain").build();
		}
		if ((body == null) || ("".equals(body))) {
			return Response.status(412).entity("HTTP BODY 不允许为空").type("text/plain").build();
		}
		this.dictionaryService.updateById(dictionaryId, body);
		LOGGER.info("update dictionary sucessfully");
		return Response.status(200).entity("success").type("text/plain").build();
	}

	@DELETE
	@Produces({ "text/plain" })
	@Path("{dictionaryId}")
	public Response delDictionary(@PathParam("dictionaryId") String dictionaryId) {
		if ((dictionaryId == null) || ("".equals(dictionaryId))) {
			return Response.status(412).entity("dictionaryId 不允许为空").type("text/plain").build();
		}

		Dictionary dictionary = this.dictionaryService.findById(dictionaryId);
		if ((dictionary != null) && ("梵汉词汇表".equals(dictionary.getDisplayName()))) {
			return Response.status(200).entity("error").type("text/plain").build();
		}

		this.dictionaryService.removeById(dictionaryId);
		LOGGER.info("delete dictionary info succesfully");
		this.wordService.removeByDictionaryId(dictionaryId);
		LOGGER.info("delete word info related to the dictionary successfully");
		List<User> users = this.userService.findUsersByAllowedDictionary(dictionaryId);
		if ((users != null) && (users.size() != 0)) {
			for (User user : users) {
				List<Map<String, String>> allowedDictionaries = user.getAllowedDictionaries();

				for (int i = 0; i < allowedDictionaries.size(); i++) {
					if (((String) ((Map<String, String>) allowedDictionaries.get(i)).get("id")).equals(dictionaryId)) {
						allowedDictionaries.remove(i);
						break;
					}
				}

				Map<String, Object> map = user.getDicSequence();
				if ((map != null) && (map.size() != 0)) {
					@SuppressWarnings("unchecked")
					Map<String, Object> sqkv = (Map<String, Object>) map.get("sequence");
					if ((sqkv != null) && (sqkv.containsKey(dictionaryId))) {
						sqkv.remove(dictionaryId);
					}
					@SuppressWarnings("unchecked")
					List<Object> clList = (List<Object>) map.get("checkList");
					if ((clList != null) && (clList.contains(dictionaryId))) {
						clList.remove(dictionaryId);
					}
				}
				this.userService.updateById(user.getId(), this.userService.entityToJson(user));
			}
		}

		return Response.status(200).entity("success").type("text/plain").build();
	}

	@DELETE
	@Produces({ "text/plain" })
	@Path("delwords/{dictionaryId}")
	public Response delDictionaryWords(@PathParam("dictionaryId") String dictionaryId) {
		if ((dictionaryId == null) || ("".equals(dictionaryId))) {
			return Response.status(412).entity("dictionaryId 不允许为空").type("text/plain").build();
		}
		this.wordService.removeByDictionaryId(dictionaryId);
		LOGGER.info("成功删除字典所关联的词条");

		return Response.status(200).entity("success").type("text/plain").build();
	}
}