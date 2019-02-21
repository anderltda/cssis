package br.com.cssis.foundation.query.impl;

import java.lang.reflect.Method;

import br.com.cssis.foundation.BasicObject;

@SuppressWarnings("unchecked")
public class QueryFieldMetaData extends BasicObject {
	private static final long serialVersionUID = 1L;

	protected int scale;
	protected int precision;
	protected int jdbcType;
	protected int length;
	protected Class javaType;
	protected Class primitiveJavaType;

	protected String label;
	protected String jdbcTypeName;

	protected String propertyName;
	protected String setterName;
	protected String getterName;

	public int getScale() {
		return scale;
	}

	public void setScale(int scale) {
		this.scale = scale;
	}

	public int getPrecision() {
		return precision;
	}

	public void setPrecision(int precision) {
		this.precision = precision;
	}

	public String getJdbcTypeName() {
		return jdbcTypeName;
	}

	public void setJdbcTypeName(String jdbcTypeName) {
		this.jdbcTypeName = jdbcTypeName;
	}

	public int getJdbcType() {
		return jdbcType;
	}

	public void setJdbcType(int jdbcType) {
		this.jdbcType = jdbcType;
	}

	public void teste(Object a) {
		try {
			Method m = this.getClass().getDeclaredMethod("setName", String.class);
			System.out.println(m.toString());
			//this.convertedJavaType = String.class;
			//m = this.getClass().getDeclaredMethod("setName", this.convertedJavaType);
			m.invoke(this, "aaaa");

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getPropertyName() {
		return propertyName;
	}

	public void setPropertyName(String propertyName) {
		this.propertyName = propertyName;
	}

	public String getSetterName() {
		return setterName;
	}

	public void setSetterName(String setterName) {
		this.setterName = setterName;
	}

	public String getGetterName() {
		return getterName;
	}

	public void setGetterName(String getterName) {
		this.getterName = getterName;
	}

	public Class getJavaType() {
		return javaType;
	}

	public void setJavaType(Class javaType) {
		this.javaType = javaType;
		if (javaType != null) {
			if (javaType == Long.class) { primitiveJavaType = Long.TYPE; }
			else if (javaType == Double.class) { primitiveJavaType = Double.TYPE; }
			else if (javaType == Integer.class) { primitiveJavaType = Integer.TYPE; }
			else if (javaType == Short.class) { primitiveJavaType = Short.TYPE; }
			else if (javaType == Float.class) { primitiveJavaType = Float.TYPE; }
			else if (javaType == Byte.class) { primitiveJavaType = Byte.TYPE; }
			else if (javaType == Boolean.class) { primitiveJavaType = Boolean.TYPE; };
		}
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public Class getPrimitiveJavaType() {
		return primitiveJavaType;
	}

}
