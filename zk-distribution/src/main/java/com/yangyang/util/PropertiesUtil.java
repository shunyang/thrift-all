package com.yangyang.util;

import java.util.ResourceBundle;

public class PropertiesUtil {
	private static ResourceBundle resourceBundle = ResourceBundle.getBundle("config");

	public static String get(String key) {
		return resourceBundle.getString(key);
	}

}
