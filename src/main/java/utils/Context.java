package utils;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;

public class Context {
	private Properties properties = null;

	private List<JSONObject> inport = null;

	private Map<String, JSONObject> export = null;

	private boolean syntax_error = false;
	private String errorMessage;

	public Properties getProperties() {
		return this.properties;
	}

	public void setProperties(Properties properties) {
		this.properties = properties;
	}

	public boolean isSyntax_error() {
		return this.syntax_error;
	}

	public String getErrorMessage() {
		return this.errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public void setSyntax_error(boolean syntax_error) {
		this.syntax_error = syntax_error;
	}

	public Context() {
		Resource resource = null;
		try {
			DefaultResourceLoader defaultResourceLoader = new DefaultResourceLoader();
			resource = defaultResourceLoader.getResource("config-context/framework.properties.xml");
			this.properties = loadFile(resource.getFile());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public JSONArray parseImport(String string) throws IOException {
		String sjson = null;
		string = string.trim();
		DefaultResourceLoader defaultResourceLoader = new DefaultResourceLoader();
		File file = defaultResourceLoader.getResource("config-context/" + string).getFile();
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
		String line = null;
		StringBuilder sb = new StringBuilder();
		while ((line = reader.readLine()) != null) {
			sb.append(line).append("\n");
		}
		reader.close();
		sjson = sb.toString();

		JSONTokener jt = null;
		JSONArray ja = null;

		if (sjson != null) {
			try {
				long t1 = System.currentTimeMillis();
				jt = new JSONTokener(sjson);
				ja = new JSONArray(jt);

				long t2 = System.currentTimeMillis();
				System.out.println(t2 - t1);
			} catch (JSONException e) {
				e.printStackTrace();
				this.syntax_error = true;
				setErrorMessage(e.getMessage());
				return null;
			}

		}

		return ja;
	}

	public List<String> parseOrder(String fileName) throws IOException {
		List<String> words = new LinkedList<String>();
		fileName = fileName.trim();
		DefaultResourceLoader defaultResourceLoader = new DefaultResourceLoader();
		File file = defaultResourceLoader.getResource("config-context/" + fileName).getFile();
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
		String line = null;
		while ((line = reader.readLine()) != null) {
			words.add(line);
		}
		reader.close();
		return words;
	}

	public void parseExport(String string) {
		String sjson = null;
		if (string.contains("export.json"))
			try {
				string = string.trim();
				DefaultResourceLoader defaultResourceLoader = new DefaultResourceLoader();
				File file = defaultResourceLoader.getResource("config-context/" + string).getFile();
				BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
				String line = null;
				StringBuilder sb = new StringBuilder();
				while ((line = reader.readLine()) != null) {
					sb.append(line).append("\n");
				}
				reader.close();
				sjson = sb.toString();
			} catch (Exception e) {
				e.printStackTrace();
			}
		else {
			sjson = string;
		}
		if (sjson != null) {
			this.export = new LinkedHashMap<String, JSONObject>();

			JSONArray ja = new JSONArray(sjson);
			int i = 0;
			for (int size = ja.length(); i < size; i++) {
				JSONObject json = ja.getJSONObject(i);
				String id = null;
				if (json.has("id"))
					id = json.getString("id");
				else {
					id = json.hashCode() + "";
				}
				json.put("id", id);
				this.export.put(id, json);
			}
		}
	}

	private static Properties loadFile(File path) throws Exception {
		Properties prop = null;
		BufferedInputStream inBuff = null;
		try {
			inBuff = new BufferedInputStream(new FileInputStream(path));
			prop = new Properties();
			if (path.getName().endsWith(".xml"))
				prop.loadFromXML(inBuff);
			else
				prop.load(inBuff);
		} finally {
			if (inBuff != null) {
				inBuff.close();
			}
		}
		return prop;
	}

	public void appendProperties(Properties properties) {
		this.properties.putAll(properties);
	}

	public String getEntryValue(String key) {
		return this.properties.getProperty(key);
	}

	public int getIntEntryValue(String key) {
		int answer = -1;
		String tmp = this.properties.getProperty(key);
		if ((tmp != null) && (tmp.length() > 0)) {
			try {
				answer = Integer.parseInt(tmp);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return answer;
	}

	public String getEntryValue(String key, String defaultValue) {
		return this.properties.getProperty(key, defaultValue);
	}

	public List<JSONObject> getInport() {
		return this.inport;
	}

	public Map<String, JSONObject> getExport() {
		return this.export;
	}
}