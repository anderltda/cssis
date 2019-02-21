package br.com.cssis.foundation.util;

import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.Converter;

import br.com.cssis.foundation.BasicObject;


public class BooleanHelper extends BasicObject {
	public static final String SIM = "S";
	public static final String NAO = "N";
	public static final Integer INT_FALSE = 0;
	public static final Integer INT_TRUE = 1;
	private static final long serialVersionUID = 1L;
	static {
		Converter bc = new CustomBooleanConverter(Boolean.class);
		ConvertUtils.register(bc, Boolean.class);
		ConvertUtils.register(bc, Boolean.TYPE);		
	}

	public static final boolean booleanValue(Object value) {
		if (value == null) return false;
		Boolean returnValue = (Boolean) ConvertUtils.convert(value, Boolean.class);
		return returnValue.booleanValue();
	}

	public static final boolean getBoolean(Object value) {
		if (value == null) return new Boolean(false);
		return (Boolean) ConvertUtils.convert(value, Boolean.class);
	}
	
	public static final char getCharValue(boolean bol) {
		return bol == true ? 
			CustomBooleanConverter.getDefaultTrueChar() : 
			CustomBooleanConverter.getDefaultFalseChar(); 
	}
	
	public static final char getCharValue(Boolean bol) {
		return booleanValue(bol) ? 
			CustomBooleanConverter.getDefaultTrueChar() : 
			CustomBooleanConverter.getDefaultFalseChar(); 
	}

	public static final String getYesNo(String value) {
		if (value==null || value.length() == 0) return null;
		if (booleanValue(value)) return BooleanHelper.SIM;
		else return BooleanHelper.NAO;
	}
	
	public static final String getYesNoNull(String value) {
		if (value==null || value.length() == 0) return BooleanHelper.NAO;
		if (booleanValue(value)) return BooleanHelper.SIM;
		else return BooleanHelper.NAO;
	}
	
	
	public static final String getYesNo(boolean value) {
		return value ? "S" : "N";
	}

	public static final boolean getYesNoBoolean(String value) {
		if (value==null || value.length() == 0) return Boolean.FALSE;
		return value.equals("S") ? Boolean.TRUE : Boolean.FALSE;
	}
	
	public static final String getYesNo(Boolean value) {
		return (value != null & value.booleanValue() == true )? "S" : "N";
	}
	
	public static boolean hasPermissionBitwiseAnd(Long targetMasc, Long masc){
		if(targetMasc == null || masc == null)
			return false;

		int targetMascInt = targetMasc.intValue();
		int mascInt = masc.intValue();
		
		int result = targetMascInt & mascInt;
		
		if(result == targetMascInt)
			return true;
		return false;
	}
}