package utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipOutputStream;

public class CompressUtil {

	public static ByteArrayOutputStream compress(Map<String, InputStream> map) {
		ByteArrayOutputStream answer = new ByteArrayOutputStream();
		ZipOutputStream zout = null;
		try {
			zout = new ZipOutputStream(new BufferedOutputStream(answer));
			byte[] data = new byte[2048];
			for (Map.Entry<String, InputStream> en : map.entrySet()) {
				BufferedInputStream origin = new BufferedInputStream((InputStream) en.getValue(), 2048);
				zout.putNextEntry(new ZipEntry((String) en.getKey()));
				int count;
				while ((count = origin.read(data, 0, 2048)) != -1) {
					zout.write(data, 0, count);
				}
				origin.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (zout != null) {
				try {
					zout.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return answer;
	}
}