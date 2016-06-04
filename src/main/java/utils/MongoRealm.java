package utils;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;

import entity.User;
import service.UserService;

public class MongoRealm extends AuthorizingRealm {

	@Autowired
	private UserService userService;

	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
		SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
		System.out.println("doGetAuthorizationInfo");
		String username = (String) principals.getPrimaryPrincipal();
		System.out.println(username);
		User currentUser = this.userService.findUserByName(username);

		info.addRole(currentUser.getRole());

		return info;
	}

	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
		System.out.println("doGetAuthenticationInfo");
		UsernamePasswordToken authToken = (UsernamePasswordToken) token;

		return new SimpleAuthenticationInfo(authToken.getUsername(), authToken.getPassword(), getName());
	}
}