package br.com.cssis.foundation.util;

import org.apache.commons.beanutils.converters.AbstractConverter;

public class CustomBooleanConverter extends AbstractConverter {
	public CustomBooleanConverter(Class<Boolean> defaultType) {
		super(defaultType);
	}

	private String[] trueStrings = { "true", "v", "s", "sim", "yes", "y", "on", "1", "1.0" };

	private static final char defaultTrueChar = 'V';
	private static final char defaultFalseChar = 'F';

	private static final String defaultTrueString = String.valueOf(defaultTrueChar);
	private static final String defaultFalseString = String.valueOf(defaultFalseChar);

	@SuppressWarnings("unchecked")
	@Override
	protected Object convertToType(Class type, Object value) throws Throwable {
		String stringValue = value.toString().toLowerCase();
		for (int i = 0; i < trueStrings.length; ++i) {
			if (trueStrings[i].equals(stringValue)) {
				return Boolean.TRUE;
			}
		}
		return Boolean.FALSE;
	}

	public static final char getDefaultTrueChar() {
		return defaultTrueChar;
	}

	public static final char getDefaultFalseChar() {
		return defaultFalseChar;
	}

	public static final String getDefaultTrueString() {
		return defaultTrueString;
	}

	public static final String getDefaultFalseString() {
		return defaultFalseString;
	}

	@Override
	protected Class<?> getDefaultType() {
		return String.class;
	}

}
