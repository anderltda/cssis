package br.com.cssis.foundation.query.impl;

import java.math.BigDecimal;
import java.util.Date;

import br.com.cssis.foundation.util.DateTimeHelper;


public class SimpleConditionHelper {
	public static final String NULL_STRING = "NULL";
	public static final String WILDCARD_STRING = "?";
	
	public static final String getValue(Integer intValue) {
		if (intValue != null) {
			return intValue.toString();
		}
		else return NULL_STRING;
	}

	public static final String getValue(Long longValue) {
		if (longValue != null) {
			return longValue.toString();
		}
		else return NULL_STRING;
	}

	public static final String getValue(Double doubleValue) {
		if (doubleValue != null) {
			return doubleValue.toString();
		}
		else return NULL_STRING;
	}

	public static final String getValue(Float floatValue) {
		if (floatValue != null) {
			return floatValue.toString();
		}
		else return NULL_STRING;
	}
	
	public static final String getValue(BigDecimal bdValue) {
		if (bdValue != null) {
			return bdValue.toString();
		}
		else return NULL_STRING;
	}
	
	public static final String getValue(Date dateValue) {
		if (dateValue != null) {
			return dateValue.toString();
		}
		else return NULL_STRING;
	}
	
	public static final String getValue(Object objectValue) {
		if (objectValue != null) {
			if (objectValue instanceof Date ) {
				return DateTimeHelper.formatDateNonSafe((Date) objectValue); 
			} else 
			return objectValue.toString();
		}
		else return NULL_STRING;
	}	
	
	public static final String getNullValue() {
		return NULL_STRING;
	}
			
}