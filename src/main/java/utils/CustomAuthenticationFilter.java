package utils;

import java.util.Date;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.ExcessiveAttemptsException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authz.AuthorizationFilter;

public class CustomAuthenticationFilter extends AuthorizationFilter {
	protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue)
			throws Exception {
		HttpServletRequest req = (HttpServletRequest) request;
		String requestMethod = req.getMethod();
		String requestURL = req.getRequestURL().toString();
		if ((requestMethod.equals("GET"))
				&& ((requestURL.contains("rest/dictionary")) || (requestURL.contains("rest/word")))) {
			return true;
		}

		String tokenString = request.getParameter("token");
		System.out.println("CustomAuthenticationFilter token:" + tokenString);

		String decodedToken = DESEncrypt.decrypt(tokenString, "hanbowenKey&*)^%");
		System.out.println("CustomAuthenticationFilter decode token:" + decodedToken);
		if (decodedToken == null) {
			return false;
		}

		String username = decodedToken.split(" ")[0];
		String password = decodedToken.split(" ")[1];
		long expireTime = Long.parseLong(decodedToken.split(" ")[2]);
		long currentTime = new Date().getTime();

		System.out.println("CustomAuthenticationFilter currentTime:" + currentTime + " expireTime:" + expireTime);
		if (currentTime > expireTime) {
			return false;
		}

		HttpServletResponse res = (HttpServletResponse) response;
		String newToken = username + " " + password + " " + (currentTime + 604800000L);
		res.setHeader("token", DESEncrypt.encrypt(newToken, "hanbowenKey&*)^%"));
		res.setHeader("Access-Control-Expose-Headers", "token");
		System.out.println("CustomAuthenticationFilter NewToken:" + newToken);

		System.out.println("CustomAuthenticationFilter username:" + username + " password:" + password);
		UsernamePasswordToken token = new UsernamePasswordToken(username, password);

		Subject currentUser = SecurityUtils.getSubject();

		System.out.println("CustomAuthenticationFilter Subject created");
		try {
			currentUser.login(token);
		} catch (UnknownAccountException uae) {
			return false;
		} catch (IncorrectCredentialsException ice) {
			return false;
		} catch (LockedAccountException lae) {
			return false;
		} catch (ExcessiveAttemptsException eae) {
			return false;
		}

		System.out.println("CustomAuthenticationFilter return true.");
		return true;
	}
}