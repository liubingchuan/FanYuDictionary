package entity;

import com.google.gson.annotations.Expose;
import java.util.List;
import java.util.Map;

public class Word extends Entity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Expose
	private String word;

	@Expose
	private String cixing;

	@Expose
	private Map<String, Object> dictionary;

	@Expose
	private String xici;

	@Expose
	private List<Map<String, String>> duiyingciList;

	@Expose
	private String cigenlaiyuan;

	@Expose
	private String shiyi;

	@Expose
	private List<String> guanlianciList;

	@Expose
	private String liju;

	@Expose
	private String bianxing;

	@Expose
	private String baike;

	@Expose
	private Map<String, Object> author;

	@Expose
	private String status;

	@Expose
	private boolean importflag;

	@Expose
	private long lastEditDateTime;

	@Expose
	private long createDateTime;

	@Expose
	private String template;

	public String getCigenlaiyuan() {
		return this.cigenlaiyuan;
	}

	public void setCigenlaiyuan(String cigenlaiyuan) {
		this.cigenlaiyuan = cigenlaiyuan;
	}

	public String getCixing() {
		return this.cixing;
	}

	public void setCixing(String cixing) {
		this.cixing = cixing;
	}

	public String getStatus() {
		return this.status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Map<String, Object> getDictionary() {
		return this.dictionary;
	}

	public void setDictionary(Map<String, Object> dictionary) {
		this.dictionary = dictionary;
	}

	public List<Map<String, String>> getDuiyingciList() {
		return this.duiyingciList;
	}

	public void setDuiyingciList(List<Map<String, String>> duiyingciList) {
		this.duiyingciList = duiyingciList;
	}

	public String getShiyi() {
		return this.shiyi;
	}

	public void setShiyi(String shiyi) {
		this.shiyi = shiyi;
	}

	public List<String> getGuanlianciList() {
		return this.guanlianciList;
	}

	public void setGuanlianciList(List<String> guanlianciList) {
		this.guanlianciList = guanlianciList;
	}

	public String getBaike() {
		return this.baike;
	}

	public void setBaike(String baike) {
		this.baike = baike;
	}

	public boolean isImportflag() {
		return this.importflag;
	}

	public void setImportflag(boolean importflag) {
		this.importflag = importflag;
	}

	public String getTemplate() {
		return this.template;
	}

	public void setTemplate(String template) {
		this.template = template;
	}

	public String getWord() {
		return this.word;
	}

	public void setWord(String word) {
		this.word = word;
	}

	public String getBianxing() {
		return this.bianxing;
	}

	public void setBianxing(String bianxing) {
		this.bianxing = bianxing;
	}

	public String getXici() {
		return this.xici;
	}

	public void setXici(String xici) {
		this.xici = xici;
	}

	public Map<String, Object> getAuthor() {
		return this.author;
	}

	public void setAuthor(Map<String, Object> author) {
		this.author = author;
	}

	public long getLastEditDateTime() {
		return this.lastEditDateTime;
	}

	public void setLastEditDateTime(long lastEditDateTime) {
		this.lastEditDateTime = lastEditDateTime;
	}

	public String getLiju() {
		return this.liju;
	}

	public void setLiju(String liju) {
		this.liju = liju;
	}

	public long getCreateDateTime() {
		return this.createDateTime;
	}

	public void setCreateDateTime(long createDateTime) {
		this.createDateTime = createDateTime;
	}
}