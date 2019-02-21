package br.com.cssis.foundation.query.impl;


@SuppressWarnings("unchecked")
public class CustomMappingDefinition {
	protected int scale;
	protected int precision;
	protected int jdbcType;
	protected int length;
	
	protected Class javaType;

	
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
	public int getJdbcType() {
		return jdbcType;
	}
	public void setJdbcType(int jdbcType) {
		this.jdbcType = jdbcType;
	}
	public Class getJavaType() {
		return javaType;
	}
	public void setJavaType(Class javaType) {
		this.javaType = javaType;
	}
	public CustomMappingDefinition(int scale, int precision, int jdbcType, int length, Class javaType) {
		super();
		this.scale = scale;
		this.precision = precision;
		this.jdbcType = jdbcType;
		this.javaType = javaType;
		this.length = length;
	}

	public CustomMappingDefinition() {
	}
	public String toString() {
		return " hashCode=" + hashCode() +" scale=" + scale + " precision=" + precision + " jdbcType=" + jdbcType + " length=" + length + " javaType=" + javaType;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + jdbcType;
		result = prime * result + length;
		result = prime * result + precision;
		result = prime * result + scale;
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final CustomMappingDefinition other = (CustomMappingDefinition) obj;
		if (jdbcType != other.jdbcType)
			return false;
		if (length != other.length)
			return false;
		if (precision != other.precision)
			return false;
		if (scale != other.scale)
			return false;
		return true;
	}
	public int getLength() {
		return length;
	}
	public void setLength(int length) {
		this.length = length;
	}
	
	
}
