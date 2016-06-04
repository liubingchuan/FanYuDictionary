package enumeration;

public enum MatchProperty {
	SHOU("shou", "首"),

	ZHONG("zhong", "中"),

	WEI("wei", "尾 "),

	JINGQUE("jingque", "精确");

	private final String value;
	private final String show;

	private MatchProperty(String value, String show) {
		this.value = value;
		this.show = show;
	}

	public static MatchProperty getInstance(String name) {
		MatchProperty answer = null;
		if (name == null) {
			answer = null;
		} else if ("shou".equals(name)) {
			answer = SHOU;
		} else if ("zhong".equals(name)) {
			answer = ZHONG;
		} else if ("wei".equals(name)) {
			answer = WEI;
		} else if ("jingque".equals(name)) {
			answer = JINGQUE;
		}
		return answer;
	}

	public static String getRegex(MatchProperty name, String word) {
		String answer = null;

		if (name == null) {
			answer = null;
		} else if (SHOU.equals(name)) {
			answer = "^" + word;
		} else if (ZHONG.equals(name)) {
			answer = ".+" + word + ".+";
		} else if (WEI.equals(name)) {
			answer = word + "$";
		} else if (JINGQUE.equals(name)) {
			answer = "^" + word + "$";
		}

		return answer;
	}

	public final String getValue() {
		return this.value;
	}

	public final String getShow() {
		return this.show;
	}

	public String toString() {
		return this.value;
	}

	public static String[] getValues() {
		String[] valueArray = new String[values().length];
		int i = 0;
		for (MatchProperty property : values()) {
			valueArray[i] = property.getValue();
			i++;
		}

		return valueArray;
	}

	public static String[] getShows() {
		String[] showArray = new String[values().length];
		int i = 0;
		for (MatchProperty property : values()) {
			showArray[i] = property.getShow();
			i++;
		}

		return showArray;
	}
}