package com.asset.vodafone.npc.core.utils;

import java.math.BigDecimal;

public class DBTypeConverter {

	private DBTypeConverter() {
	}

	public static String toSQLNumber(long number) {
		if (number == -1L)
			return "NULL";
		else
			return String.valueOf(number);
	}

	public static String toSQLNumber(int number) {
		if (number == -1)
			return "NULL";
		else
			return String.valueOf(number);
	}

	public static String toSQLNumber(short number) {
		if (number == -1)
			return "NULL";
		else
			return String.valueOf(number);
	}

	public static String toSQLNumber(BigDecimal number) {
		if (number == null)
			return "NULL";
		else
			return String.valueOf(number.longValue());
	}

	public static String toSQLNumber(String value) {
		if ("".equals(value) || "null".equals(value) || value == null)
			return "NULL";
		try {
			Long.parseLong(value);
		} catch (NumberFormatException ex) {
			return "NULL";
		}
		return String.valueOf(value);
	}

	public static String toSQLVARCHAR2(String text) {
		if ("null".equals(text) || text == null) {
			return "NULL";
		} else {
			text = text.replace("'", "''");
			return "'" + text + "'";
		}
	}

	public static String toSQLVARCHAR2(short number) {
		if (number == -1)
			return "NULL";
		else
			return "'" + number + "'";
	}

	public static String nullStatement(String fieldName, String fieldValue) {
		if ("null".equals(fieldValue) || fieldValue == null || "".equals(fieldValue))
			return fieldName + " IS NULL";
		else
			return fieldName + " = '" + fieldValue + "'";
	}

	public static String inStatement(String commaSeparatedText) {
		String[] values = commaSeparatedText.split("\\s*,\\s*");
		String result = "(";
		StringBuilder sb=new StringBuilder();
		for (int i = 0; i < values.length; i++)
			sb.append(result+ toSQLVARCHAR2(values[i].trim()) + ",");
		result=sb.toString();
		return result.substring(0, result.length() - 1) + ")";
	}
}
