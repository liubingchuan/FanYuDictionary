package controller;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
import org.apache.shiro.authz.UnauthorizedException;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Order;
import org.springframework.web.bind.annotation.ExceptionHandler;

import entity.Dictionary;
import entity.Word;
import entity.WordOrder;
import enumeration.DomainProperty;
import enumeration.MatchProperty;
import export.Export;
import export.StringExport;
import service.DictionaryService;
import service.OrderService;
import service.WordService;
import utils.CompressUtil;
import utils.Pagination;
import utils.RequestUtil;

@SuppressWarnings("deprecation")
@Path("/word")
public class WordResource {

	@Autowired
	private WordService wordService;

	@Autowired
	private DictionaryService dictionaryService;

	@Autowired
	private OrderService orderService;
	private static final Log LOGGER = LogFactory.getLog(WordResource.class);

	@GET
	public Response getWords(@QueryParam("search") String word, @QueryParam("code") String code, @QueryParam("match") String match,
			@QueryParam("domain") String domain, @QueryParam("dictionaries") String dictionaries,
			@QueryParam("logon") String logon, @QueryParam("period") String period,
			@QueryParam("periodCount") String periodCount, @QueryParam("userId") String userId,
			@QueryParam("page") String page, @QueryParam("pageSize") String pageSize) {
		if ((word == null) || ("".equals(word))) {
			if ((page == null) || ("".equals(page)) || ("undefined".equals(page))) {
				return Response.status(412).entity("HTTP HEADER 中的page参数不能为空").type("text/plain").build();
			}

			if ((pageSize == null) || ("".equals(pageSize)) || ("undefined".equals(pageSize))) {
				return Response.status(412).entity("HTTP HEADER 中的pageSize参数不能为空").type("text/plain").build();
			}

			if ((Integer.valueOf(page).intValue() <= 0) || (Integer.valueOf(pageSize).intValue() <= 0))
				return Response.status(412).entity("HTTP HEADER 中的page 和 pageSize 必须大于0").type("text/plain").build();
		} else {
			if ((match == null) || ("".equals(match))) {
				return Response.status(412).entity("match 参数不允许为空").type("text/plain").build();
			}
			
			if ((code == null) || ("".equals(code))) {
				return Response.status(412).entity("match 参数不允许为空").type("text/plain").build();
			}

			if ((domain == null) || ("".equals(domain))) {
				return Response.status(412).entity("domain 参数不允许为空").type("text/plain").build();
			}

			if ((dictionaries == null) || ("".equals(dictionaries))) {
				return Response.status(412).entity("dictionaries 参数不允许为空").type("text/plain").build();
			}

			if ((logon == null) || ("".equals(logon))) {
				return Response.status(412).entity("logon 参数不允许为空").type("text/plain").build();
			}

			if (!Arrays.asList(MatchProperty.getValues()).contains(match)) {
				return Response.status(412)
						.entity("match参数已超出规定的范围，请在以下查询范围中选择一种查询方式" + Arrays.asList(MatchProperty.getShows()))
						.type("text/plain").build();
			}

			if (!Arrays.asList(DomainProperty.getValues()).contains(domain)) {
				return Response.status(412)
						.entity("domain参数已超出规定的范围，请在以下查询范围中选择一种查询方式" + Arrays.asList(DomainProperty.getShows()))
						.type("text/plain").build();
			}

			if (dictionaries.endsWith("@")) {
				dictionaries = (String) dictionaries.subSequence(0, dictionaries.length() - 1);
			}
			//
			dictionaries = this.dictionaryService.filtByDicGroup(dictionaries);
			
			
			
			// 根据code 选择 HK 模糊 unicode编码方式
			List<String> list = new ArrayList<String>();
			if("mohu".equals(code)) {
				List<String> wordList = this.wordService.replaceByVague(word);
				for(String s: wordList) {
					list.addAll(this.wordService.findByParams(s, match, domain, dictionaries, logon));
				}
			} else if("Unicode".equals(code)) {
				list = this.wordService.findByParams(word, match, domain, dictionaries, logon);
			} else if("HK".equals(code)) {
				word = this.wordService.replaceByHK(word);
				list = this.wordService.findByParams(word, match, domain, dictionaries, logon);
			} 
			
			
			if ((list != null) && (list.size() != 0)) {
				String[] dictionaryArray = dictionaries.split("@");
				String dicGroup = "";
				if ((dictionaryArray != null) && (dictionaryArray.length != 0)) {
					for (String s : dictionaryArray) {
						Dictionary dictionary = this.dictionaryService.findById(s);
						if (dictionary != null) {
							dicGroup = dictionary.getDicGroup();
							break;
						}
					}
				}
				String blockName = this.orderService.getBlockName(dicGroup);
				list = this.orderService.getOrderedWords(list, blockName);
			}

			LOGGER.info("return the words list in getWords ------"
					+ new SimpleDateFormat("yyyy MM dd HH mm ss").format(new Date()));
			return Response.status(200).entity(this.wordService.listToJson(list)).type("application/json").build();
		}

		Pagination<Word> pagination = null;

		int pageCount = 0;

		// 最新词条查询部分
		if ((period != null) && (!"".equals(period)) && (periodCount != null) && (!"".equals(periodCount))) {
			pagination = new Pagination<Word>(0, Integer.valueOf(pageSize).intValue(),
					this.wordService.queryCount(period, periodCount));
			pageCount = pagination.getTotalPage();
			pagination = this.wordService.findByDate(period, periodCount, Integer.valueOf(page).intValue(),
					Integer.valueOf(pageSize).intValue());
			LOGGER.info("return new words list in getWords-------"
					+ new SimpleDateFormat("yyyy MM dd HH mm ss").format(new Date()));
			
			// 遍历相同词条，若一个词条为published，则全部都为published
						List<Word> words = pagination.getDatas();
						// 作为一个当前页已发布词条的缓存
						List<String> publishedWordNameList = new ArrayList<String>();
						for(Word wd: words) {
							// 如果当前词条已经被发布，则直接将当前词条名字放入缓存列表
							if ("published".equals(wd.getStatus())) {
								publishedWordNameList.add(wd.getWord());
								wd.setStatus2("published");
							} else {
								// 如果当前词条的状态为未发布，则先查询当前的缓存列表
								// 如果缓存列表中不存在该词条名称，则查询数据库
								if(!publishedWordNameList.contains(wd.getWord())) {
									Word publishedWord = this.wordService.findWordByMultipleParam(wd.getWord(),
											"published");
									// 如果数据库中已经存在一条已发布的词条，则将该词条名称加入缓存列表，同时将该词条的状态置为published，否则将状态置为unpublished
									if(publishedWord != null && publishedWord.getId() != null) {
										publishedWordNameList.add(wd.getWord());
										wd.setStatus2("published");
									} else {
										wd.setStatus2("unpublished");
									}
								}else {
									// 缓存列表中存在该词条名称，则直接将其状态置为published
									wd.setStatus2("published");
								}
							}
						}
			return Response.status(200).header("Access-Control-Expose-Headers", "pageCount")
					.header("pageCount", Integer.valueOf(pageCount)).header("Access-Control-Expose-Headers", "page")
					.header("page", page).header("Access-Control-Expose-Headers", "pageSize")
					.header("pageSize", pageSize).entity(this.wordService.listToJson(words))
					.type("application/json").build();
		}

		
		// 我的词条查询部分
		if ((userId != null) && (!"".equals(userId))) {
			pagination = new Pagination<Word>(0, Integer.valueOf(pageSize).intValue(),
					this.wordService.queryCount(userId));
			pageCount = pagination.getTotalPage();
			pagination = this.wordService.findWordByUserId(userId, Integer.valueOf(page).intValue(),
					Integer.valueOf(pageSize).intValue());
			LOGGER.info("return word list by userid in getWords---------"
					+ new SimpleDateFormat("yyyy MM dd HH mm ss").format(new Date()));
			
			// 遍历相同词条，若一个词条为published，则全部都为published
			List<Word> words = pagination.getDatas();
			// 作为一个当前页已发布词条的缓存
			List<String> publishedWordNameList = new ArrayList<String>();
			for(Word wd: words) {
				// 如果当前词条已经被发布，则直接将当前词条名字放入缓存列表
				if ("published".equals(wd.getStatus())) {
					publishedWordNameList.add(wd.getWord());
					wd.setStatus2("published");
				} else {
					// 如果当前词条的状态为未发布，则先查询当前的缓存列表
					// 如果缓存列表中不存在该词条名称，则查询数据库
					if(!publishedWordNameList.contains(wd.getWord())) {
						Word publishedWord = this.wordService.findWordByMultipleParam(wd.getWord(),
								"published");
						// 如果数据库中已经存在一条已发布的词条，则将该词条名称加入缓存列表，同时将该词条的状态置为published，否则什么都不做，直接判断下一个词条
						if(publishedWord != null && publishedWord.getId() != null) {
							publishedWordNameList.add(wd.getWord());
							wd.setStatus2("published");
						} else {
							wd.setStatus2("unpublished");
						}
						
					}else {
						// 缓存列表中存在该词条名称，则直接将其状态置为published
						wd.setStatus2("published");
					}
				}
			}
			return Response.status(200).header("Access-Control-Expose-Headers", "pageCount")
					.header("pageCount", Integer.valueOf(pageCount)).header("Access-Control-Expose-Headers", "page")
					.header("Access-Control-Expose-Headers", "pageSize").header("page", page)
					.header("pageSize", pageSize).entity(this.wordService.listToJson(words))
					.type("application/json").build();
		}

		return Response.status(412).entity("您输入的参数不合法").type("text/plain").build();
	}

	@POST
	@Path("{role}")
	@Produces({ "application/json" })
	public Response saveWord(@PathParam("role") String role, String wordJson ) {
		Word word = (Word) this.wordService.jsonToEntity(wordJson, Word.class);
		Date date = new Date();
		String id = UUID.randomUUID().toString();
		word.setId(id);
		if (role != null && "Y".equals(role)) {
			word.setStatus("published");
		} else {
			word.setStatus("created");
		}
		word.setCreateDateTime(date.getTime());
		word.setLastEditDateTime(date.getTime());
		word.setImportflag(false);
		this.wordService.save(word);
		LOGGER.info("save the word successfully------------"
				+ new SimpleDateFormat("yyyy MM dd HH mm ss").format(new Date()));
		word = this.wordService.findWordById(id);
		return Response.status(200).entity(this.wordService.entityToJson(word)).type("application/json").build();
	}

	@PUT
	@RequiresRoles({ "Admin" })
	@ExceptionHandler({ UnauthorizedException.class })
	@Path("{wordId}/publish")
	public Response publishWord(@PathParam("wordId") String wordId) {
		if ((wordId == null) || ("".equals(wordId))) {
			return Response.status(412).entity("请传入wordId").type("text/plain").build();
		}

		Word word = this.wordService.findWordById(wordId);
		Word publishedWord = this.wordService.findWordByMultipleParam(word.getWord(),
				"published");
		if ((publishedWord != null) && (!"".equals(publishedWord.getId()))) {
			// 为dictionary添加的两个字段所作出的额外比较
			if(word.getDictionary()!=null && publishedWord.getDictionary() != null && word.getDictionary().get("id").equals(publishedWord.getDictionary().get("id"))) {
				return Response.status(200).entity("published").type("text/plain").build();
			}
		}

		word.setAuthor(new HashMap<String, Object>());
		// String id = UUID.randomUUID().toString();
		word.setId(null);
		word.setStatus("published");
		this.wordService.save(word);

		this.wordService.publishWord(wordId);
		LOGGER.info("publish word successfully-----------"
				+ new SimpleDateFormat("yyyy MM dd HH mm ss").format(new Date()));
		return Response.status(200).entity("success").type("text/plain").build();
	}

	@PUT
	@Produces({ "text/plain" })
	@Path("{wordId}")
	@RequiresRoles(value = { "Editor", "Admin" }, logical = Logical.OR)
	public Response updateWord(@PathParam("wordId") String wordId, String body) {
		if ((wordId == null) || ("".equals(wordId))) {
			return Response.status(412).entity("请传入wordId").type("text/plain").build();
		}
		Word word = (Word) this.wordService.jsonToEntity(body, Word.class);

		body = this.wordService.entityToJson(word);
		this.wordService.updateById(wordId, body);
		LOGGER.info("update word successfully-------------"
				+ new SimpleDateFormat("yyyy MM dd HH mm ss").format(new Date()));
		return Response.status(200).entity("success").type("text/plain").build();
	}

	@GET
	@Path("{wordName}/{logon}")
	public Response getWordByName(@PathParam("wordName") String wordName, @PathParam("logon") String logon) {
		if ((wordName == null) || ("".equals(wordName))) {
			return Response.status(200).entity("").type("text/plain").build();
		}
		List<Word> list = this.wordService.findWordByName(wordName, logon);
		boolean published = false;
		for(Word word: list) {
			if("published".equals(word.getStatus())) {
				published = true;
				break;
			}
		}
		if (published == true) {
			for(Word word: list) {
				word.setStatus2("published");
			}
		}else {
			for(Word word: list) {
				word.setStatus2("unpublished");
			}
		}

		LOGGER.info(
				"return the words detail info------" + new SimpleDateFormat("yyyy MM dd HH mm ss").format(new Date()));
		String body = this.wordService.listToJson(list);

		return Response.status(200).entity(body).type("application/json").build();
	}

	// private String utf8Togb2312(String str)
	// {
	// StringBuffer sb = new StringBuffer();
	//
	// for (int i = 0; i < str.length(); i++)
	// {
	// char c = str.charAt(i);
	//
	// switch (c)
	// {
	// case '+':
	// sb.append(' ');
	//
	// break;
	// case '%':
	// try
	// {
	// sb.append((char)Integer.parseInt(
	// str.substring(i + 1, i + 3), 16));
	// }
	// catch (NumberFormatException e)
	// {
	// throw new IllegalArgumentException();
	// }
	//
	// i += 2;
	//
	// break;
	// default:
	// sb.append(c);
	// }
	//
	// }
	//
	// String result = sb.toString();
	//
	// String res = null;
	// try
	// {
	// byte[] inputBytes = result.getBytes("8859_1");
	//
	// res = new String(inputBytes, "UTF-8");
	// }
	// catch (Exception localException)
	// {
	// }
	//
	// return res;
	// }
	//
	// private String getEncoding(String str)
	// {
	// String encode = "GB2312";
	// try {
	// if (str.equals(new String(str.getBytes(encode), encode))) {
	// return encode;
	// }
	// }
	// catch (Exception localException)
	// {
	// encode = "ISO-8859-1";
	// try {
	// if (str.equals(new String(str.getBytes(encode), encode))) {
	// return encode;
	// }
	// }
	// catch (Exception localException1)
	// {
	// encode = "UTF-8";
	// try {
	// if (str.equals(new String(str.getBytes(encode), encode))) {
	// return encode;
	// }
	// }
	// catch (Exception localException2)
	// {
	// encode = "GBK";
	// try {
	// if (str.equals(new String(str.getBytes(encode), encode)))
	// return encode;
	// } catch (Exception localException3) { }
	//
	// }
	// }
	// }
	// return ""; }
	@PUT
	@Path("/delete")
	@RequiresRoles({ "Admin" })
	@ExceptionHandler({ UnauthorizedException.class })
	public Response delWords(String deleteList) {
		if ((deleteList == null) || ("".equals(deleteList))) {
			return Response.status(412).entity("请传入deleteList").type("text/plain").build();
		}

		String[] ids = deleteList.substring(1, deleteList.length() - 1).split(",");
		for (String id : ids) {
			id = id.replaceAll("\"", "");
			this.wordService.removeById(id);
		}

		LOGGER.info("delete word info in batch----------------"
				+ new SimpleDateFormat("yyyy MM dd HH mm ss").format(new Date()));

		return Response.status(200).entity("success").type("text/plain").build();
	}

	@DELETE
	@Path("{wordId}")
	public Response delWord(@PathParam("wordId") String wordId) {
		if ((wordId == null) || ("".equals(wordId))) {
			Response.status(412).entity("请输入正确的wordId").type("text/plain").build();
		}
		this.wordService.removeById(wordId);
		LOGGER.info("delete word info--------" + new SimpleDateFormat("yyyy MM dd HH mm ss").format(new Date()));
		return Response.status(200).entity("success").type("text/plain").build();
	}

	@GET
	@Path("import/{dictionaryName}")
	public Response inport(@PathParam("dictionaryName") String dictionaryName) {
		if ((dictionaryName == null) || ("".equals(dictionaryName))) {
			return Response.status(404).entity("DICTIONARY NAME CAN NOT BE EMPTY").type("text/plain").build();
		}

		if (dictionaryName.contains("_")) {
			dictionaryName = dictionaryName.replace("_", " ");
		}

		Dictionary dictionary = this.dictionaryService.findByDisplayName(dictionaryName);
		if ((dictionary == null) || ("".equals(dictionary.getId()))) {
			return Response.status(404)
					.entity("THE DICTIONARY NAME OF " + dictionaryName
							+ " IS NOT IN THE DATABASE, PLEASE DOUBLE CHECK THE DICTIONARY NAME AND IMPORT LATER")
					.type("text/plain").build();
		}

		List<Word> oldWords = this.wordService.findWordsByDictionaryName(dictionaryName);
		if ((oldWords != null) && (oldWords.size() != 0)) {
			return Response.status(409).entity("PLEASE CLEAN ALL THE VOCABULARY ENTRIES AND THEN IMPORT AGAIN.")
					.type("text/plain").build();
		}

		JSONArray jsonArray = new JSONArray();

		utils.Context context = new utils.Context();
		try {
			long t1 = System.currentTimeMillis();

			jsonArray = context.parseImport(dictionaryName + ".json");
			LOGGER.info("parse dictionary file successfully--------"
					+ new SimpleDateFormat("yyyy MM dd HH mm ss").format(new Date()));
			long t2 = System.currentTimeMillis();
			System.out.println(t2 - t1);
		} catch (IOException e) {
			e.printStackTrace();
			return Response.status(404)
					.entity("PLEASE MAKE SURE THE FILE OF " + dictionaryName + ".JSON IS UPLOADED TO THE SERVER")
					.type("text/plain").build();
		}
		boolean syntax_error = context.isSyntax_error();

		if (syntax_error) {
			return Response.status(412)
					.entity("THE DATA TO BE IMPORTED DOES NOT CONFORM TO THE JSON SPECIFICATION, PLEASE CHECK AND RE - IMPORT, ERROR MESSAGE: "
							+ context.getErrorMessage())
					.type("text/plain").build();
		}

		Worker worker = new Worker(jsonArray, dictionary);
		Thread t = new Thread(worker);
		t.start();

		System.gc();
		return Response.status(200).entity(jsonArray.length() + " ITEMS HAVE BEEN IMPORTED SUCCESSFULLY")
				.type("text/plain").build();
	}

	@GET
	@Path("import/order/{fileName}")
	public Response importOrder(@PathParam("fileName") String fileName) {
		if ((fileName == null) || ("".equals(fileName))) {
			return Response.status(404).entity("FILE NAME CAN NOT BE EMPTY").type("text/plain").build();
		}

		String blockName = "";
		if (!fileName.contains("_")) {
			return Response.status(404)
					.entity("FILE NAME format IS WRONG, IT MUST START WITH THE BLOCK NAME, FOR EXAMPLE FAN_INDEX.TXT")
					.type("text/plain").build();
		}
		String[] arr = fileName.split("_");
		blockName = arr[0];
		blockName = blockName.trim();

		utils.Context context = new utils.Context();
		List<String> words = new LinkedList<String>();
		try {
			long t1 = System.currentTimeMillis();
			words = context.parseOrder(fileName + ".txt");
			long t2 = System.currentTimeMillis();
			System.out.println(t2 - t1);
		} catch (IOException e) {
			e.printStackTrace();
			return Response.status(404)
					.entity("PLEASE MAKE SURE THE FILE OF " + fileName + ".TXT IS UPLOADED TO THE SERVER")
					.type("text/plain").build();
		}

		Worker worker = new Worker(true, words, blockName);
		Thread t = new Thread(worker);
		t.start();

		long i = words.size();
		System.gc();
		return Response.status(200).entity(i + " ITEMS HAVE BEEN IMPORTED SUCCESSFULLY").type("text/plain").build();
	}

	@GET
	@Path("export/{dictionaryName}")
	public void export(@PathParam("dictionaryName") String dictionaryName,
			@javax.ws.rs.core.Context HttpServletRequest request,
			@javax.ws.rs.core.Context HttpServletResponse response) {
		utils.Context context = new utils.Context();
		context.parseExport(context.getProperties().getProperty("export"));
		Map<String, JSONObject> mapConfig = context.getExport();
		Map<String, InputStream> inmap = new HashMap<String, InputStream>();
		try {
			JSONObject config = (JSONObject) mapConfig.get("word");
			JSONArray ignoredArray = null;
			if (!config.isNull("exportIgnored")) {
				ignoredArray = config.getJSONArray("exportIgnored");
			}

			String[] keys = null;
			if (ignoredArray != null) {
				int len = ignoredArray.length();
				keys = new String[len];
				for (int i = 0; i < len; i++) {
					keys[i] = ignoredArray.get(i).toString();
				}
			}

			List<Word> list = this.wordService.findWordsByDictionaryName(dictionaryName);
			String json = this.wordService.listToJsonPretty(list, keys);
			inmap.put(config.getString("name").trim() + ".json", exportInputStream(json));
		} catch (Exception e) {
			e.printStackTrace();
		}

		ByteArrayOutputStream out = CompressUtil.compress(inmap);
		InputStream in = null;
		try {
			byte[] b = out.toByteArray();
			in = new ByteArrayInputStream(b);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		RequestUtil.outStream(request, response, "export" + new Date().getTime() + ".zip", in);
	}

	@GET
	@Path("delete/all/indexes")
	public void removeAllTheIndex() {
		this.orderService.removeAll();
	}

	private InputStream exportInputStream(String inputStr) {
		InputStream answer = null;
		Export export = new StringExport();
		answer = export.doExport(inputStr);
		return answer;
	}

	@PUT
	@Path("/publish")
	@RequiresRoles({ "Admin" })
	@ExceptionHandler({ UnauthorizedException.class })
	public Response publishAll(String publishList) {
		if ((publishList == null) || ("".equals(publishList))) {
			return Response.status(412).entity("请传入publishList").type("text/plain").build();
		}

		String[] ids = publishList.substring(1, publishList.length() - 1).split(",");

		for (String id : ids) {
			id = id.replaceAll("\"", "");
			Word word = this.wordService.findWordById(id);
			Word publishedWord = this.wordService.findWordByMultipleParam(word.getWord(),
					"published");
			if ((publishedWord != null) && (!"".equals(publishedWord.getId()))) {
				return Response.status(409).entity("该词条已被发布，不允许再次发布").type("text/plain").build();
			}

			word.setAuthor(new HashMap<String, Object>());
			word.setId("");
			word.setStatus("published");
			this.wordService.save(word);

			this.wordService.publishWord(id);
		}

		LOGGER.info("成功发布信息");

		return Response.status(200).entity("success").type("text/plain").build();
	}

	class Worker implements Runnable {
		private JSONArray jsonArray;
		private Dictionary dictionary;
		private boolean isOrder = false;
		private List<String> words = new LinkedList<String>();
		private String blockName;

		public Worker(JSONArray jsonArray, Dictionary dictionary) {
			this.jsonArray = jsonArray;
			this.dictionary = dictionary;
		}

		public Worker(boolean isOrder, List<String> words, String blockName) {
			this.isOrder = isOrder;
			this.words = words;
			this.blockName = blockName;
		}

		public void run() {
			if (!this.isOrder) {
				Map<String, Object> dicMap = new HashMap<String, Object>();
				dicMap.put("id", this.dictionary.getId());
				dicMap.put("dicGroup", this.dictionary.getDicGroup());
				dicMap.put("displayName", this.dictionary.getDisplayName());
				dicMap.put("shortName", this.dictionary.getShortName());
				dicMap.put("status", this.dictionary.getStatus());
				dicMap.put("createDateTime", Long.valueOf(this.dictionary.getCreateDateTime()));
				List<Word> jsonList = new ArrayList<Word>();

				for (int i = 0; i < this.jsonArray.length(); i++) {
					Date date = new Date();
					JSONObject jsonObject = this.jsonArray.getJSONObject(i);
					jsonObject.put("dictionary", dicMap);
					Word word = (Word) WordResource.this.wordService.jsonToEntity(jsonObject.toString(), Word.class);
					word.setStatus("published");
					word.setImportflag(true);
					word.setCreateDateTime(date.getTime());
					word.setLastEditDateTime(date.getTime());
					jsonList.add(word);
				}
				WordResource.LOGGER.info("reconstruct word list successfully--------"
						+ new SimpleDateFormat("yyyy MM dd HH mm ss").format(new Date()));
				int limit = 10000;
				Integer size = Integer.valueOf(jsonList.size());

				if (limit < size.intValue()) {
					int part = size.intValue() / limit;
					System.out.println("共有:" + size + "条， ！" + "分为: " + part + "批");

					for (int i = 0; i < part; i++) {
						List<Word> listPage = jsonList.subList(0, limit);
						WordResource.this.wordService.insertAll(listPage);
						jsonList.subList(0, limit).clear();
					}

					if (!jsonList.isEmpty()) {
						WordResource.this.wordService.insertAll(jsonList);
					}
					WordResource.LOGGER.info("all the new words are inserted--------"
							+ new SimpleDateFormat("yyyy MM dd HH mm ss").format(new Date()));
				} else {
					WordResource.this.wordService.insertAll(jsonList);
					WordResource.LOGGER.info("all the new words are inserted--------"
							+ new SimpleDateFormat("yyyy MM dd HH mm ss").format(new Date()));
				}
			} else {
				WordResource.this.orderService.removeByBlockName(this.blockName);
				Iterator<String> iter = this.words.iterator();
				Long index = Long.valueOf(1L);
				List<WordOrder> orders = new LinkedList<WordOrder>();
				while (iter.hasNext()) {
					String word = (String) iter.next();
					WordOrder order = new WordOrder();
					order.setOrder(index);
					order.setWord(word);
					order.setBlockName(this.blockName);
					index = Long.valueOf(index.longValue() + 1L);
					orders.add(order);
				}
				WordResource.this.orderService.ensureIndex(WordOrder.class, "word", Order.ASCENDING);

				int limit = 10000;
				Integer size = Integer.valueOf(orders.size());
				if (limit < size.intValue()) {
					int part = size.intValue() / limit;
					System.out.println("共有:" + size + "条， ！" + "分为: " + part + "批");

					for (int i = 0; i < part; i++) {
						List<WordOrder> listPage = orders.subList(0, limit);
						WordResource.this.orderService.insertAll(listPage);
						orders.subList(0, limit).clear();
						System.out.println(limit + "items");
					}

					if (!orders.isEmpty()) {
						WordResource.this.orderService.insertAll(orders);
					}
					WordResource.LOGGER.info("all the new indexes are inserted--------"
							+ new SimpleDateFormat("yyyy MM dd HH mm ss").format(new Date()));
				} else {
					WordResource.this.orderService.insertAll(orders);
					WordResource.LOGGER.info("all the new indexes are inserted--------"
							+ new SimpleDateFormat("yyyy MM dd HH mm ss").format(new Date()));
				}
				System.out.println(orders.size() + "items");
				WordResource.LOGGER.info("all the indexes are inserted--------"
						+ new SimpleDateFormat("yyyy MM dd HH mm ss").format(new Date()));
			}
			System.gc();
		}
	}
}