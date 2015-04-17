package koc.common.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONObject;

public class StringUtils {
	public static boolean isMobileNO(String mobiles) {
		Pattern p = Pattern
				.compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");
		Matcher m = p.matcher(mobiles);
		return m.matches();
	}

	public static JSONObject ToJSONObject(Object value) {
		try {
			value = value.toString().substring(value.toString().indexOf("{"))
					.replace("\r\n", "\n");
			return new JSONObject(value.toString());
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static boolean isNumeric(String str) {
		Pattern pattern = Pattern.compile("[0-9]*");
		return pattern.matcher(str).matches();
	}

	public static int ToInt(Object value) {
		return ToInt(value, -1);
	}

	public static int ToInt(Object value, int defaultValue) {
		try {
			return Integer.parseInt(String.valueOf(value));
		} catch (Exception e) {
			e.printStackTrace();
			return defaultValue;
		}
	}

	/*
	 * public static String ToDate(Date value) { return ToDate(value,
	 * "yyyy-MM-dd HH:mm:ss.SSS"); }
	 * 
	 * public static String ToDate(Date value, String format) { try { return
	 * (new SimpleDateFormat(format, new Locale("zh", "CN"))) .format(value); }
	 * catch (Exception e) { e.printStackTrace(); return null; } }
	 * 
	 * public static Date ToDate(String value) { return ToDate(value,
	 * "yyyy-MM-dd HH:mm:ss.SSS"); }
	 * 
	 * public static Date ToDate(String value, String format) { try { return
	 * (new SimpleDateFormat(format, new Locale("zh", "CN"))) .parse(value); }
	 * catch (Exception e) { e.printStackTrace(); return null; } }
	 */
}
