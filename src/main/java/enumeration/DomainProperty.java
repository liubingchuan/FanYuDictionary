package enumeration;

public enum DomainProperty implements BaseEnum {
	DUIYINGCI("duiyingci", "对应词"),

	BIANXING("bianxing", "变形"),

	LIJU("liju", "例句"),

	DANCI("danci", "单词 "),

	QUANWEN("quanwen", "全文");

	private final String value;
	private final String show;

	private DomainProperty(String value, String show) {
		this.value = value;
		this.show = show;
	}

	public static DomainProperty getInstance(String name) {
		DomainProperty answer = null;
		if (name == null) {
			answer = null;
		} else if ("duiyingci".equals(name)) {
			answer = DUIYINGCI;
		} else if ("bianxing".equals(name)) {
			answer = BIANXING;
		} else if ("liju".equals(name)) {
			answer = LIJU;
		} else if ("danci".equals(name)) {
			answer = DANCI;
		} else if ("quanwen".equals(name)) {
			answer = QUANWEN;
		}
		return answer;
	}

	public static String getKey(DomainProperty name) {
		String answer = null;
		if (name == null) {
			answer = null;
		} else if (DUIYINGCI.equals(name)) {
			answer = "duiyingciList.value";
		} else if (BIANXING.equals(name)) {
			answer = "bianxing";
		} else if (LIJU.equals(name)) {
			answer = "liju";
		} else if (DANCI.equals(name)) {
			answer = "word";
		} else if (QUANWEN.equals(name)) {
			StringBuilder sb = new StringBuilder();
			sb.append("duiyingciList.value").append("-");
			sb.append("bianxing").append("-");
			sb.append("liju").append("-");
			sb.append("word");
			answer = sb.toString();
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
		for (DomainProperty property : values()) {
			valueArray[i] = property.getValue();
			i++;
		}

		return valueArray;
	}

	public static String[] getShows() {
		String[] showArray = new String[values().length];
		int i = 0;
		for (DomainProperty property : values()) {
			showArray[i] = property.getShow();
			i++;
		}

		return showArray;
	}
}