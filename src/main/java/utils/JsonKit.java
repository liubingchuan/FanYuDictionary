package utils;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;

public class JsonKit implements ExclusionStrategy {
	private String[] keys;

	public JsonKit(String[] keys) {
		this.keys = keys;
	}

	public boolean shouldSkipClass(Class<?> arg0) {
		return false;
	}

	public boolean shouldSkipField(FieldAttributes arg0) {
		for (String key : this.keys) {
			if (key.equals(arg0.getName())) {
				return true;
			}
		}
		return false;
	}
}