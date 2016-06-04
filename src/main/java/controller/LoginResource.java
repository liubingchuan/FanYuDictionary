package controller;

import entity.User;
import java.util.Date;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import javax.xml.bind.DatatypeConverter;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import service.UserService;
import utils.DESEncrypt;

@Path("/login")
public class LoginResource {
	private static final Log LOGGER = LogFactory.getLog(LoginResource.class);

	@Autowired
	private UserService userService;

	@POST
	@Produces({ "text/plain" })
	public Response login(@QueryParam("authentication") String authentication) {
		System.out.println("LoginResource auth: " + authentication);
		byte[] decoded = DatatypeConverter.parseBase64Binary(authentication);

		String decodedString = new String(decoded);
		String username = decodedString.split(" ")[0];
		String password = DigestUtils.md5Hex(decodedString.split(" ")[1]);

		System.out.println("userService username:" + username);
		User loginUser = this.userService.findUserByName(username);

		if ((loginUser == null) || (!password.equals(loginUser.getPassword()))) {
			LOGGER.info("login failed: password not correct");
			return Response.status(401).entity("用户名或密码不正确").type("text/plain").build();
		}

		System.out.println("username:" + username + " password:" + password);
		Date currentTime = new Date();
		long currentLongTime = currentTime.getTime();
		long expireLongTime = currentLongTime + 604800000L;
		String tokenEncrypted = "";
		String tokenUncode = username + " " + password + " " + expireLongTime;
		try {
			tokenEncrypted = DESEncrypt.encrypt(tokenUncode, "hanbowenKey&*)^%");
		} catch (Exception e) {
			e.printStackTrace();
		}

		String jsonContext = this.userService.entityToJson(loginUser);

		LOGGER.info("login successfully");
		return Response.status(200).entity(jsonContext).header("Access-Control-Expose-Headers", "token")
				.header("token", tokenEncrypted).type("text/plain").build();
	}
}