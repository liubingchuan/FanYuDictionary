package export;

import java.io.InputStream;

public abstract interface Export {
	public abstract InputStream doExport(String paramString);

	public abstract String exportFileName();
}