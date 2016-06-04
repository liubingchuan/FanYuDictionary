package export;

import utils.TimeUtil;

public abstract class AbstractExport implements Export {
	public String exportFileName() {
		return fileName() + fileSuffixName();
	}

	public String fileName() {
		return TimeUtil.format("yyyy-MM-dd") + "_ExportData.";
	}

	public abstract String fileSuffixName();
}