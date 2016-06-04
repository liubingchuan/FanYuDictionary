package entity;

import com.google.gson.annotations.Expose;

public class WordOrder extends Entity implements Comparable<WordOrder> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Expose
	private String word;

	@Expose
	private Long order;

	@Expose
	private String blockName;

	public String getWord() {
		return this.word;
	}

	public void setWord(String word) {
		this.word = word;
	}

	public Long getOrder() {
		return this.order;
	}

	public void setOrder(Long order) {
		this.order = order;
	}

	public String getBlockName() {
		return this.blockName;
	}

	public void setBlockName(String blockName) {
		this.blockName = blockName;
	}

	public int compareTo(WordOrder o) {
		if (getOrder() == null) {
			System.out.println();
		}

		if (o.getOrder() == null) {
			System.out.println();
		}
		return getOrder().compareTo(o.getOrder());
	}
}