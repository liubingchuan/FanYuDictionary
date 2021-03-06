package entity;

import com.google.gson.annotations.Expose;
import java.util.List;
import java.util.Map;

public class User extends Entity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Expose
	private String username;

	@Expose(serialize = false)
	private String password;

	@Expose
	private String displayName;

	@Expose
	private String role;

	@Expose
	private Map<String, Object> dicSequence;

	@Expose
	private Dictionary dictionary;

	@Expose
	private List<Map<String, String>> allowedDictionaries;

	public List<Map<String, String>> getAllowedDictionaries() {
		return this.allowedDictionaries;
	}

	public void setAllowedDictionaries(List<Map<String, String>> allowedDictionaries) {
		this.allowedDictionaries = allowedDictionaries;
	}

	public String getUsername() {
		return this.username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getDisplayName() {
		return this.displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getRole() {
		return this.role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public Map<String, Object> getDicSequence() {
		return this.dicSequence;
	}

	public void setDicSequence(Map<String, Object> dicSequence) {
		this.dicSequence = dicSequence;
	}

	public Dictionary getDictionary() {
		return this.dictionary;
	}

	public void setDictionary(Dictionary dictionary) {
		this.dictionary = dictionary;
	}
}