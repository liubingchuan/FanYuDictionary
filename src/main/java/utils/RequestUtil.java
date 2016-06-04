package utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

public class RequestUtil {
	// private static final String IP =
	// "^((1?\\d?\\d|(2([0-4]\\d|5[0-5])))\\.){3}(1?\\d?\\d|(2([0-4]\\d|5[0-5])))$";
	private static Pattern IPPATTERN = Pattern
			.compile("^((1?\\d?\\d|(2([0-4]\\d|5[0-5])))\\.){3}(1?\\d?\\d|(2([0-4]\\d|5[0-5])))$");
	// private static final String DOMAIN =
	// "[a-zA-Z0-9][-a-zA-Z0-9]{0,62}(\\.[a-zA-Z0-9][-a-zA-Z0-9]{0,62})+\\.?";
	private static Pattern DOMAINPATTERN = Pattern
			.compile("[a-zA-Z0-9][-a-zA-Z0-9]{0,62}(\\.[a-zA-Z0-9][-a-zA-Z0-9]{0,62})+\\.?");
	public static final String USER_AGENT = "User-Agent";
	public static final String ACCEPT_LANGUAGE = "Accept-Language";

	public static String getRequestIP(HttpServletRequest request) {
		String ip = request.getHeader("x-forwarded-for");
		if ((ip == null) || (ip.length() == 0) || ("unknown".equalsIgnoreCase(ip))) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if ((ip == null) || (ip.length() == 0) || ("unknown".equalsIgnoreCase(ip))) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if ((ip == null) || (ip.length() == 0) || ("unknown".equalsIgnoreCase(ip))) {
			ip = request.getRemoteAddr();
		}

		if (ip.indexOf(',') > 0) {
			ip = ip.split(",")[0];
		}
		return ip;
	}

	public static String getCookieDomain(HttpServletRequest request) {
		String serverName = request.getServerName();
		Matcher mat = IPPATTERN.matcher(serverName);
		if (mat.find()) {
			return serverName;
		}
		mat = DOMAINPATTERN.matcher(serverName);
		if (mat.find()) {
			String[] paths = serverName.split("\\.");
			if (paths.length >= 3) {
				String path = serverName.substring(serverName.indexOf('.') + 1, serverName.length());
				return path;
			}
		}
		return serverName;
	}

	public static String getCookiePath(HttpServletRequest request) {
		return "/";
	}

	public static void outPrintJSON(HttpServletRequest request, HttpServletResponse response, JSONObject json) {
		outPrint(request, response, json.toString());
	}

	public static void outPrint(HttpServletRequest request, HttpServletResponse response, String str) {
		PrintWriter out = null;
		try {
			out = response.getWriter();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		response.setContentType("text/html; charset=UTF-8");
		out.print(str);
	}

	public static void outStream(HttpServletRequest request, HttpServletResponse response, String streamName,
			InputStream inputStream) {
		outStream(request, response, "application/x-download", streamName, inputStream);
	}

	public static void outStream(HttpServletRequest request, HttpServletResponse response, String contentType,
			String streamName, InputStream inputStream) {
		ServletOutputStream os = null;
		try {
			response.setContentType(contentType);
			String fileName = URLEncoder.encode(streamName, "UTF-8");
			response.addHeader("Content-Disposition", "attachment;filename=" + fileName);

			byte[] buf = new byte[4096];
			os = response.getOutputStream();
			int readLength;
			while ((readLength = inputStream.read(buf)) != -1) {
				os.write(buf, 0, readLength);
			}
			os.flush();
		} catch (IOException e1) {
			e1.printStackTrace();

			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (os != null)
				try {
					os.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (os != null)
				try {
					os.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
	}

	public static Cookie getCookie(HttpServletRequest request, String name) {
		Cookie[] cookies = request.getCookies();
		Cookie returnCookie = null;

		if (cookies == null) {
			return returnCookie;
		}

		for (int i = 0; i < cookies.length; i++) {
			Cookie thisCookie = cookies[i];

			if (thisCookie.getName().equals(name)) {
				if (!thisCookie.getValue().equals("")) {
					returnCookie = thisCookie;

					break;
				}
			}
		}

		return returnCookie;
	}

	public static void setCookie(HttpServletResponse response, String domain, String name, String value, String path) {
		Cookie cookie = new Cookie(name, value);
		cookie.setSecure(false);
		cookie.setPath(path);
		cookie.setMaxAge(2592000);
		cookie.setDomain(domain);
		response.addCookie(cookie);
	}

	public static void setCookie(HttpServletResponse response, String domain, String name, String value) {
		setCookie(response, domain, name, value, "/");
	}

	public static void deleteCookie(HttpServletResponse response, Cookie cookie, String domain, String path) {
		if (cookie != null) {
			cookie.setMaxAge(0);
			cookie.setPath(path);
			cookie.setDomain(domain);
			cookie.setValue("");
			response.addCookie(cookie);
		}
	}
}