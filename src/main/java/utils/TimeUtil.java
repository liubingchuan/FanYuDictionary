package utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TimeUtil {
	public static final String FORMAT_DATE_ONLY_CH = "yyyy年MM月dd日";
	public static final String FORMAT_MONTH_ONLY = "yyyy-MM";
	public static final String FORMAT_DATE_ONLY = "yyyy-MM-dd";
	public static final String FORMAT_DATE_HOUR = "yyyy-MM-dd HH:mm";
	public static final String FORMAT_TIME_ONLY = "HH:mm:ss";
	public static final String FORMAT_COMPACT = "yyMMddHHmmssSSS";
	public static final String FORMAT_NORMAL = "yyyy-MM-dd HH:mm:ss";
	public static final String FORMAT_DETAIL = "yyyy-MM-dd HH:mm:ss.SSS";
	public static final String FORMAT_NORMAL_1 = "yyyy-MM-dd-HH-mm-ss";
	public static final String FORMAT_YEAR = "yyyy";
	public static final String FORMAT_MONTH = "MM";
	public static final String FORMAT_TIME_HOUR = "HH";
	public static final String FORMAT_TIME_MINUTE = "mm";
	public static final long DATE_SECOND = 86400L;
	public static final long DATE_MINUTE = 1440L;
	public static final long MINUTE_SECOND = 60L;

	public static Date parse(String str, String format) {
		try {
			SimpleDateFormat sf = new SimpleDateFormat(format);
			return sf.parse(str);
		} catch (ParseException e) {
		}
		return null;
	}

	public static String format(String format) {
		return format(new Date(), format);
	}

	public static String format(Date date, String format) {
		try {
			SimpleDateFormat sf = new SimpleDateFormat(format);
			return sf.format(date);
		} catch (Exception e) {
		}
		return null;
	}

	public static boolean isExpire(String strTime, String strExpiredTime) {
		Date time = parse(strTime, "yyyy-MM-dd HH:mm:ss");
		Date expiredTime = parse(strExpiredTime, "yyyy-MM-dd HH:mm:ss");

		return time.compareTo(expiredTime) >= 0;
	}

	public static Date createDate(int year, int month, int days) {
		Calendar cal = Calendar.getInstance();
		cal.set(year, month - 1, days, 0, 0, 0);
		cal.set(14, 0);
		return cal.getTime();
	}

	public static long getTimeDifference(String beginTime, String endTime) {
		long between = 0L;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

		Date end = null;
		Date begin = null;
		try {
			end = sdf.parse(endTime);
			begin = sdf.parse(beginTime);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		between = (end.getTime() - begin.getTime()) / 1000L;

		return between;
	}

	public static String getTimeDifference(Date beginTime, Date endTime) {
		if ((beginTime == null) || (endTime == null)) {
			return "未知";
		}
		long between = endTime.getTime() - beginTime.getTime();
		if (between <= 0L) {
			return null;
		}

		between /= 1000L;
		long date = between / 86400L;
		long hour = (between - date * 86400L) / 3600L;
		long minute = (between - date * 86400L - hour * 3600L) / 60L;
		long sec = between - date * 86400L - hour * 3600L - minute * 60L;

		return date + "天" + hour + "小时" + minute + "分钟" + sec + "秒";
	}

	private static boolean compareTo(Calendar datebegin, Calendar dateend) {
		return datebegin.get(5) > dateend.get(5);
	}

	public static int differenceYear(String begin, String end, String format) {
		SimpleDateFormat df = new SimpleDateFormat(format);
		Calendar datebegin = Calendar.getInstance();
		Calendar dateend = Calendar.getInstance();
		try {
			datebegin.setTime(df.parse(begin));
			dateend.setTime(df.parse(end));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return differenceYear(datebegin, dateend);
	}

	public static int differenceMonth(String begin, String end, String format) {
		SimpleDateFormat df = new SimpleDateFormat(format);
		Calendar datebegin = Calendar.getInstance();
		Calendar dateend = Calendar.getInstance();
		try {
			datebegin.setTime(df.parse(begin));
			dateend.setTime(df.parse(end));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return differenceMonth(datebegin, dateend);
	}

	public static int differenceYear(Calendar datebegin, Calendar dateend) {
		int year1 = datebegin.get(1);
		int year2 = dateend.get(1);

		int month1 = datebegin.get(2);
		int month2 = dateend.get(2);
		int year = year2 - year1;
		if (compareTo(datebegin, dateend))
			month2--;
		if (month1 > month2)
			year--;
		return year;
	}

	public static int differenceMonth(Calendar datebegin, Calendar dateend) {
		int month1 = datebegin.get(2);
		int month2 = dateend.get(2);
		int month = 0;
		if (compareTo(datebegin, dateend))
			month2--;
		if (month2 >= month1)
			month = month2 - month1;
		else if (month2 < month1) {
			month = 12 + month2 - month1;
		}
		return month;
	}

	public static String getDifferenceDate(String begin, String end, String format) {
		int year = differenceYear(begin, end, format);
		int month = differenceMonth(begin, end, format);
		StringBuilder sb = new StringBuilder();
		if (year > 0) {
			sb.append(year).append("年");
		}
		if (month > 0) {
			sb.append(month).append("月");
		}
		return sb.toString();
	}

	public static String getDifferenceDate(Calendar datebegin, Calendar dateend) {
		int year = differenceYear(datebegin, dateend);
		int month = differenceMonth(datebegin, dateend);
		StringBuilder sb = new StringBuilder();
		if (year > 0) {
			sb.append(year).append("年");
		}
		if (month > 0) {
			sb.append(month).append("月");
		}
		return sb.toString();
	}
}