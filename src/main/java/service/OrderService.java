package service;

import entity.WordOrder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

@Service
public class OrderService extends BaseService<WordOrder> {
	public List<String> getOrderedWords(List<String> words, String blockName) {
		List<WordOrder> list = findOrderByWords(words, blockName);
		Collections.sort(list);
		List<String> typos = new ArrayList<String>();
		if (list == null) {
			return null;
		}

		Iterator<WordOrder> iter = list.iterator();
		while (iter.hasNext()) {
			WordOrder wordOrder = (WordOrder) iter.next();
			if (wordOrder.getOrder().equals(Long.valueOf(0L))) {
				typos.add(wordOrder.getWord());
			}
		}

		List<String> orderedWords = new LinkedList<String>();
		for (WordOrder word : list) {
			orderedWords.add(word.getWord());
		}

		List<String> cloneWords = new LinkedList<String>();

		cloneWords.addAll(orderedWords);
		Collections.sort(cloneWords);

		if (typos.size() == words.size()) {
			return cloneWords;
		}

		for (String typo : typos) {
			int index = cloneWords.indexOf(typo);
			if (index == 0) {
				orderedWords.remove(typo);
				orderedWords.add(0, typo);
			} else if (index >= 1) {
				String beforeTypo = (String) cloneWords.get(index - 1);
				index = orderedWords.indexOf(beforeTypo);
				int typoIndex = orderedWords.indexOf(typo);
				orderedWords.remove(typo);
				if (typoIndex < index)
					orderedWords.add(index, typo);
				else {
					orderedWords.add(index + 1, typo);
				}
			}
		}

		return orderedWords;
	}

	public void insertAll(Collection<WordOrder> objects) {
		super.insertAll(objects);
	}

	private List<WordOrder> findOrderByWords(List<String> words, String blockName) {
		List<WordOrder> wordOrders = new LinkedList<WordOrder>();
		for (int i = 0; i < words.size(); i++) {
			WordOrder wordOrder = findOneByWord((String) words.get(i), blockName);
			if (wordOrder == null) {
				wordOrder = new WordOrder();
				wordOrder.setWord((String) words.get(i));
				wordOrder.setOrder(Long.valueOf(0L));
			}
			wordOrders.add(wordOrder);
		}
		return wordOrders;
	}

	private WordOrder findOneByWord(String word, String blockName) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("word", word);
		params.put("blockName", blockName);
		return (WordOrder) super.findOne(params);
	}

	@SuppressWarnings("unused")
	private List<WordOrder> findMultiByWord(String word) {
		return super.find(Query.query(Criteria.where("word").is(word)));
	}

	protected Class<WordOrder> getEntityClass() {
		return WordOrder.class;
	}

	public void removeAll() {
		super.removeAll();
	}

	public void removeByBlockName(String blockName) {
		super.removeByProperty(Query.query(Criteria.where("blockName").is(blockName)));
	}

	/**
	 * 获取相应区块的索引前缀名
	 * 
	 * @return
	 */
	public String getBlockName(String dicGroup) {

		String blockName = "";
		switch (dicGroup) {
		case "巴":
			blockName = "pali";
			break;
		case "藏":
			blockName = "tib";
			break;
		case "汉":
			blockName = "chn";
			break;
		default:
			blockName = "skt";
		}
		return blockName;
	}

	public WordOrder save(WordOrder order) {
		return (WordOrder) super.save(order);
	}
}