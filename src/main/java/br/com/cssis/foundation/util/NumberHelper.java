package br.com.cssis.foundation.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;

import org.apache.commons.beanutils.ConvertUtils;

import br.com.cssis.foundation.BasicObject;
import br.com.cssis.foundation.Constants;

public class NumberHelper extends BasicObject {
	private static final long serialVersionUID = 1L;

	public static final double doubleValue(Object value) {
		if (value == null) return 0;
		Double returnValue = (Double) ConvertUtils.convert(value, Double.class);
		return returnValue.doubleValue();
	}

	public static final double parseDouble(String value) {
		if (value == null) return 0;
		NumberFormat nf = NumberFormat.getNumberInstance();
		try {
			Number number = nf.parse(value);
			return number.doubleValue();
		} catch (ParseException e) {
			Double returnValue = (Double) ConvertUtils.convert(value, Double.class);
			return returnValue;
		}
	}

	public static final Double getDouble(Object value) {
		if (value == null) return null;
		return (Double) ConvertUtils.convert(value, Double.class);
	}

	public static final Short getShort(Object value) {
		if (value == null) return null;
		return (Short) ConvertUtils.convert(value, Short.class);
	}

	public static final Byte getByte(Object value) {
		if (value == null) return null;
		return (Byte) ConvertUtils.convert(value, Byte.class);
	}

	public static final byte byteValue(Object value) {
		if (value == null) return 0;
		Byte returnValue = (Byte) ConvertUtils.convert(value, Byte.class);
		return returnValue.byteValue();
	}

	public static final long longValue(Object value) {
		if (value == null) return 0L;
		Long returnValue = (Long) ConvertUtils.convert(value, Long.class);
		return returnValue.longValue();
	}

	public static final Long getLong(Object value) {
		if (value == null) return null;
		return (Long) ConvertUtils.convert(value, Long.class);
	}

	public static final int intValue(Object value) {
		if (value == null) return 0;
		Integer returnValue = (Integer) ConvertUtils.convert(value, Integer.class);
		return returnValue.intValue();
	}

	public static final Integer getInteger(Object value) {
		if (value == null) return null;
		return (Integer) ConvertUtils.convert(value, Integer.class);
	}

	public static final boolean isPositive(Long longValue) {
		return longValue != null && longValue.longValue() > 0 ? true : false;
	}

	public static final boolean isNumber(String stringValue) {
		try{
			Long.valueOf(stringValue);
			return true;
		}catch(NumberFormatException e){
			return false;
		}
	}

	
	public static final boolean nonPositive(Long longValue) {
		return !isPositive(longValue);
	}

	public static final boolean isPositive(Byte byteValue) {
		return byteValue != null && byteValue.byteValue() > 0 ? true : false;
	}

	public static final boolean nonPositive(Byte byteValue) {
		return !isPositive(byteValue);
	}

	public static final boolean isPositive(Short shortValue) {
		return shortValue != null && shortValue.shortValue() > 0 ? true : false;
	}

	public static final boolean nonPositive(Short shortValue) {
		return !isPositive(shortValue);
	}

	public static final boolean isPositive(Integer intValue) {
		return intValue != null && intValue.intValue() > 0 ? true : false;
	}

	public static final boolean nonPositive(Integer intValue) {
		return !isPositive(intValue);
	}

	public static final boolean nonPositive(Double doubleValue) {
		return !isPositive(doubleValue);
	}

	public static final boolean isPositive(Double doubleValue) {
		return doubleValue != null && doubleValue.doubleValue() > 0 ? true : false;
	}
	
	public static final boolean isPositive(BigDecimal bigDecimalValue) {
		if (bigDecimalValue == null) return false;
		return bigDecimalValue.doubleValue() > 0; 
	}
	

	public static final Float getFloat(Object value) {
		if (value == null) return null;
		return (Float) ConvertUtils.convert(value, Float.class);
	}

	public static final Float floatValue(Object value) {
		if (value == null) return 0f;
		Float returnValue = (Float) ConvertUtils.convert(value, Float.class);
		return returnValue.floatValue();
	}

	public static final short shortValue(Object value) {
		if (value == null) return 0;
		Short returnValue = (Short) ConvertUtils.convert(value, Short.class);
		return returnValue.shortValue();
	}

	public static final BigDecimal getBigDecimalValue(Object value) {
		if (value == null) return null;
		BigDecimal returnValue = (BigDecimal) ConvertUtils.convert(value, BigDecimal.class);
		return returnValue;
	}

	public static final BigDecimal getBigDecimalValueFromDouble(double valor) {
		BigDecimal tmp = new BigDecimal(valor);
		return tmp.setScale(Constants.PRECISAO_CALCULOS, RoundingMode.HALF_UP);
	}

	public static final Double getBigDecimalAsZeroDouble(BigDecimal value) {
		if (value == null) return 0d;
		else return value.doubleValue();
	}

	public static final boolean isBetween(Long value, Long start, Long end) {
		if (value == null || start == null || end == null) return false;
		return value.longValue() >= start.longValue() && value.longValue() <= end.longValue();
	}

	public static final boolean isBetween(Integer value, Integer start, Integer end) {
		if (value == null || start == null || end == null) return false;
		return value.intValue() >= start.intValue() && value.intValue() <= end.intValue();
	}

	public static final boolean isBetween(Double value, Double start, Double end) {
		if (value == null || start == null || end == null) return false;
		return value.doubleValue() >= start.doubleValue() && value.doubleValue() <= end.doubleValue();
	}

	public static final boolean isPercentageValid(Double value) {
		return isBetween(value, 0d, 100d);
	}

	public static final boolean equals(Double... args) {
		if (args == null) return true;
		if (args.length == 1) return true;
		for (int i = 1; i < args.length; i++) {
			if (doubleValue(args[i]) != doubleValue(args[i - 1])) return false;
		}
		return true;
	}

	public static final Double getDoubleComPrecisao(Object value) {
		Double dblVal = getDouble(value);
		if (dblVal != null) {
			return getBigDecimalValueFromDouble(dblVal).doubleValue();
		} else return null;
	}

	public static final Double getDoubleComPrecisao(Double value) {
		if (value != null) {
			return getBigDecimalValueFromDouble(value.doubleValue()).doubleValue();
		} else return new Double(0);
	}

	public static final BigDecimal getBigDecimalComPrecisao(Object value) {
		if (value != null) {
			BigDecimal tmp = (BigDecimal) ConvertUtils.convert(value, BigDecimal.class);
			return tmp.setScale(Constants.PRECISAO_CALCULOS, RoundingMode.HALF_UP);
		} else return null;
	}

	public static final BigDecimal getBigDecimal(BigDecimal value) {
		if (value != null) {
			return value.setScale(Constants.PRECISAO_CALCULOS, RoundingMode.HALF_UP);
		} else return value;
	}

	public static final String numberAsCurrency(Object value) {
		NumberFormat nf = NumberFormat.getCurrencyInstance();
		return nf.format(value);
	}

	public static final BigDecimal add(BigDecimal n1, BigDecimal n2) {
		if (n1 == null) {
			if (n2 == null) {
				return Constants.K_BD_ZERO;
			} else return n2;
		} else {
			if (n2 == null) {
				return n1;
			} else return n1.add(n2);
		}
	}

	public static final BigDecimal add(BigDecimal n1, double n2) {
		if (n1 == null) {
			if (n2 == 0) {
				return Constants.K_BD_ZERO;
			} else return getBigDecimalComPrecisao(n2);
		} else {
			if (n2 == 0) {
				return n1;
			} else return n1.add(NumberHelper.getBigDecimalValueFromDouble(n2));
		}
	}
	
	public static final <T extends Object> T defaultIfNull(T valor, T defaultValue) {
		if (valor == null) {
			return defaultValue;
		}
		return valor;
	}

	public static void main(String[] args) {
		double valor = 0.325;
		double valor1 = 0.335;
		
		System.out.println(formatDouble(new Double(1.235689)));
		
		/*
		System.out.println(getDoubleComPrecisao(valor));
		System.out.println(getDoubleComPrecisao(valor1));
		System.out.println("Valor " + numberAsCurrency(valor));
		System.out.println("Valor " + numberAsCurrency(new BigDecimal(1.1)));
		System.out.println("Valor " + numberAsCurrency(new Long(12)));
		System.out.println("Valor " + numberAsCurrency(new Double(2.1)));
		System.out.println("Valor " + numberAsCurrency(new Float(678.8787)));
		*/

		/*
		 * MathContext mc = new MathContext(3, RoundingMode.HALF_UP); BigDecimal a = new BigDecimal(0.45123456789, mc); BigDecimal b = a.setScale(3, RoundingMode.HALF_UP);
		 * 
		 * System.out.println(a + " - " + b); long a1 = a.movePointRight(3).longValue();
		 * 
		 * if (a1 % 10 == 0) { System.out.println(a.setScale(2, RoundingMode.DOWN)); } else { System.out.println(a.setScale(2, RoundingMode.CEILING)); } System.out.println(a1 + " - " + a1 % 10 + " - " + a1 / 10);
		 * 
		 * 
		 * System.out.println(getDouble(0.453456456, 2)); System.out.println(getDouble(0.453006456, 2)); Double a = 0.453456456d Math.pow(10, 2 + 1); System.out.println(a.longValue());
		 */
	}
	
	public static String formatCasasDecimais(Object valor, int qtdCasas){
		String format = "0.";
		for (int i = 0; i<qtdCasas; i ++ ){
			format = format + "#";
		}
		DecimalFormat df = new DecimalFormat(format); 
		String val = df.format(valor); 
		int posicaoVirgula = val.lastIndexOf(",");
		int t = val.length(); 
		if(posicaoVirgula == -1){
			val = val + ",";
			for(int i = 0; i<qtdCasas; i++){
				val = val + "0";
			}
		}
		else{
			int casasDecimais = t-(posicaoVirgula+1);
			for(int i = casasDecimais; i<qtdCasas; i++){
				val = val + "0";
			}
		}
		return val;
	}

	public static String formatMoney(Double valor) {
		if (valor == null) {
			return null;
		}
		DecimalFormat formt = new DecimalFormat("#,##0.00");
		return formt.format(valor);
	}
	
	public static String formatDouble(Double valor) {
		if (valor == null) {
			return null;
		}
		DecimalFormat formt = new DecimalFormat("#,######0.000000");
		return formt.format(valor);
	}
	
	public static String formatNumber(Integer valor) {
		DecimalFormat formt = new DecimalFormat("###,###"); 		
		return formt.format(valor);
	}
}
