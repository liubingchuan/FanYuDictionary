package utils;

import java.util.List;

public class Pagination<T> {
	private int pageSize = 20;
	private int pageNo;
	private int upPage;
	private int nextPage;
	private long totalCount;
	private int totalPage;
	private List<T> datas;
	private String pageUrl;

	public int getFirstResult() {
		return (getPageNo() - 1) * getPageSize();
	}

	public int getLastResult() {
		return getPageNo() * getPageSize();
	}

	public void setTotalPage() {
		this.totalPage = ((int) (this.totalCount % this.pageSize > 0L ? this.totalCount / this.pageSize + 1L
				: this.totalCount / this.pageSize));
	}

	public void setUpPage() {
		this.upPage = (this.pageNo > 1 ? this.pageNo - 1 : this.pageNo);
	}

	public void setNextPage() {
		this.nextPage = (this.pageNo == this.totalPage ? this.pageNo : this.pageNo + 1);
	}

	public int getNextPage() {
		return this.nextPage;
	}

	public int getTotalPage() {
		return this.totalPage;
	}

	public int getUpPage() {
		return this.upPage;
	}

	public int getPageSize() {
		return this.pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public int getPageNo() {
		return this.pageNo;
	}

	public void setPageNo(int pageNo) {
		this.pageNo = pageNo;
	}

	public long getTotalCount() {
		return this.totalCount;
	}

	public void setTotalCount(long totalCount) {
		this.totalCount = totalCount;
	}

	public List<T> getDatas() {
		return this.datas;
	}

	public void setDatas(List<T> datas) {
		this.datas = datas;
	}

	public String getPageUrl() {
		return this.pageUrl;
	}

	public void setPageUrl(String pageUrl) {
		this.pageUrl = pageUrl;
	}

	public Pagination(int pageNo, int pageSize, long totalCount) {
		setPageNo(pageNo);
		setPageSize(pageSize);
		setTotalCount(totalCount);
		init();
	}

	private void init() {
		setTotalPage();
		setUpPage();
		setNextPage();
	}
}