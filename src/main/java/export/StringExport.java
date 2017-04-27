package export;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

public class StringExport extends AbstractExport {
	public InputStream doExport(String inputStr) {
		InputStream answer = null;
		try {
			answer = new ByteArrayInputStream(inputStr.getBytes("Unicode"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		return answer;
	}

	public String fileSuffixName() {
		return "export_data";
	}
}