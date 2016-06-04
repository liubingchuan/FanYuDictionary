package controller;

import entity.User;
import java.util.ArrayList;
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
import javax.xml.bind.DatatypeConverter;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.shiro.authz.UnauthorizedException;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ExceptionHandler;
import service.UserService;
import utils.Pagination;

@Path("/user")
public class UserResource {
	private static final Log LOGGER = LogFactory.getLog(UserResource.class);

	@Autowired
	private UserService userService;

	@GET
	@Produces({ "application/json" })
	@RequiresRoles({ "Admin" })
	@ExceptionHandler({ UnauthorizedException.class })
	public Response getUsers(@QueryParam("page") String page, @QueryParam("pageSize") String pageSize) {
		if ((page == null) || ("".equals(page)) || ("undefined".equals(page))) {
			return Response.status(412).entity("HTTP HEADER 中的page参数不能为空").type("text/plain").build();
		}

		if ((pageSize == null) || ("".equals(pageSize)) || ("undefined".equals(pageSize))) {
			return Response.status(412).entity("HTTP HEADER 中的pageSize参数不能为空").type("text/plain").build();
		}

		if ((Integer.valueOf(page).intValue() <= 0) || (Integer.valueOf(pageSize).intValue() <= 0)) {
			return Response.status(412).entity("HTTP HEADER 中的page 和 pageSize 必须大于0").type("text/plain").build();
		}

		int pageCount = 0;

		Pagination<User> pagination = new Pagination<User>(0, Integer.valueOf(pageSize).intValue(),
				this.userService.queryCount());
		pageCount = pagination.getTotalPage();
		pagination = this.userService.getPageUser(Integer.valueOf(page).intValue(),
				Integer.valueOf(pageSize).intValue());

		LOGGER.info("return user list successfully");
		return Response.status(200).header("Access-Control-Expose-Headers", "pageCount")
				.header("pageCount", Integer.valueOf(pageCount)).header("Access-Control-Expose-Headers", "page")
				.header("page", page).header("Access-Control-Expose-Headers", "pageSize").header("pageSize", pageSize)
				.entity(this.userService.listToJson(pagination.getDatas())).type("application/json").build();
	}

	@POST
	@Produces({ "text/plain" })
	public Response saveUser(String userJson) {
		if ((userJson == null) || ("".equals(userJson))) {
			return Response.status(412).entity("请输入要保存的user对象").type("text/plain").build();
		}
		User user = (User) this.userService.jsonToEntity(userJson, User.class);

		user.setPassword(DigestUtils.md5Hex(user.getPassword()));
		this.userService.save(user);
		LOGGER.info("save user successfully");
		return Response.status(200).entity(user.toString()).type("text/plain").build();
	}

	@PUT
	@Produces({ "application/json" })
	@Path("{userid}")
	public Response updateUser(@PathParam("userid") String userId, String body) {
		if ((userId == null) || ("".equals(userId))) {
			return Response.status(412).entity("请输入userId").type("text/plain").build();
		}
		if ((body == null) || ("".equals(body))) {
			return Response.status(412).entity("请在HTTP BODY中输入要更新的对象").type("text/plain").build();
		}

		User user = (User) this.userService.jsonToEntity(body, User.class);

		if ((user.getPassword() == null) || ("".equals(user.getPassword()))) {
			Map<String, Object> map = user.getDicSequence();
			this.userService.updateDicSequence(user.getId(), map);
		} else {
			user.setPassword(DigestUtils.md5Hex(user.getPassword()));
			body = this.userService.entityToJsonWithoutAnnotation(user);
			if (!body.contains("password")) {
				LOGGER.info("Wow ! password is missing!");
				LOGGER.info("userId is " + userId);
				LOGGER.info("body is " + body);
			}
			this.userService.updateById(userId, body);
		}
		LOGGER.info("update user successfully");
		return Response.status(200).entity(body).type("application/json").build();
	}

	@GET
	@Path("{username}")
	public Response getUserByName(@PathParam("username") String username) {
		if ((username == null) || ("".equals(username))) {
			return Response.status(412).entity("请输入username").type("text/plain").build();
		}
		User user = this.userService.findUserByName(username);
		if (user == null) {
			return Response.status(200).entity("error").type("text/plain").build();
		}
		List<User> list = new ArrayList<User>();
		list.add(user);
		LOGGER.info("return userinfo successfully");
		return Response.status(200).entity(this.userService.listToJson(list)).type("application/json").build();
	}

	@POST
	@Path("{validation}")
	@Produces({ "text/plain" })
	public Response checkAuthentication(@QueryParam("authentication") String authentication) {
		LOGGER.info("LoginResource auth: " + authentication);
		byte[] decoded = DatatypeConverter.parseBase64Binary(authentication);

		String decodedString = new String(decoded);
		String username = decodedString.split(" ")[0];
		String password = DigestUtils.md5Hex(decodedString.split(" ")[1]);

		LOGGER.info("userService username:" + username);
		User loginUser = this.userService.findUserByName(username);

		if ((loginUser == null) || (!password.equals(loginUser.getPassword()))) {
			LOGGER.info("login failed: password not correct");
			return Response.status(200).entity("error").type("text/plain").build();
		}

		return Response.status(200).entity("success").type("text/plain").build();
	}

	@DELETE
	@Produces({ "text/plain" })
	@Path("{userid}")
	public Response delUser(@PathParam("userid") String userId) {
		if ((userId == null) || ("".equals(userId))) {
			return Response.status(412).entity("请输入userId").type("text/plain").build();
		}
		this.userService.removeById(userId);
		LOGGER.info("delete userinfo succesfully");
		return Response.status(200).entity("success").type("text/plain").build();
	}
}