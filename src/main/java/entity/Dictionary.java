package entity;

import com.google.gson.annotations.Expose;
import java.util.Map;

public class Dictionary extends Entity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Expose
	private String dicGroup;

	@Expose
	private String displayName;

	@Expose
	private Map<String, Object> author;

	@Expose
	private String status;

	@Expose
	private long createDateTime;

	@Expose
	private String shortName;

	@Expose
	private int copyRight;

	@Expose
	private String copyRightLiteral;

	public int getCopyRight() {
		return this.copyRight;
	}

	public void setCopyRight(int copyRight) {
		this.copyRight = copyRight;
	}

	public String getShortName() {
		return this.shortName;
	}

	public void setShortName(String shortName) {
		this.shortName = shortName;
	}

	public String getDicGroup() {
		return this.dicGroup;
	}

	public void setDicGroup(String dicGroup) {
		this.dicGroup = dicGroup;
	}

	public String getDisplayName() {
		return this.displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public Map<String, Object> getAuthor() {
		return this.author;
	}

	public void setAuthor(Map<String, Object> author) {
		this.author = author;
	}

	public String getStatus() {
		return this.status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public long getCreateDateTime() {
		return this.createDateTime;
	}

	public void setCreateDateTime(long createDateTime) {
		this.createDateTime = createDateTime;
	}

	public String getCopyRightLiteral() {
		return this.copyRightLiteral;
	}

	public void setCopyRightLiteral(String copyRightLiteral) {
		this.copyRightLiteral = copyRightLiteral;
	}
}