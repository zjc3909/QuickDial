package com.zhuang.quickcall.utils;

import java.util.regex.Pattern;

public class NumberHelper {

	public static String LeftPad_Tow_Zero(int str) {
		java.text.DecimalFormat format = new java.text.DecimalFormat("00");
		return format.format(str);
	}
	
	public static String LeftPad_Tow_Zero(long str) {
		java.text.DecimalFormat format = new java.text.DecimalFormat("00");
		return format.format(str);
	}
	
	public static boolean isNumeric(String str) {
		Pattern pattern = Pattern.compile("[0-9]*");
		return pattern.matcher(str).matches();
	}

	public static String formatNumber(String str){
		return str.replaceAll("\\D+", "");
	}
	
	public static String formatPhoneNumber(String str){
		return str.replaceAll("[\\s\\+\\-\\(\\)]", "");
	}
}
