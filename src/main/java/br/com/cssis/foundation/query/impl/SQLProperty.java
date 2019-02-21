package br.com.cssis.foundation.query.impl;

import br.com.cssis.foundation.BasicObject;

public class SQLProperty extends BasicObject implements Cloneable {
	private static final long serialVersionUID = 1L;

	private String alias;
	private String sqlMapping;
	private String queryReference;
	private String sqlFunction;
	private String nonUsedSpecialCriteriaReplacement = "1 = 1";

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public String getSqlMapping() {
		return sqlMapping;
	}

	public void setSqlMapping(String sqlMapping) {
		this.sqlMapping = sqlMapping;
	}

	public SQLProperty(String alias) {
		this.alias = alias;
		this.sqlMapping = alias;
	}

	public SQLProperty(String alias, String sqlMapping, String queryReference, String sqlFunction) {
		this.alias = alias;
		this.sqlMapping = sqlMapping;
		this.queryReference = queryReference;
		this.sqlFunction = sqlFunction;
	}

	public SQLProperty(String alias, String sqlMapping) {
		this(alias, sqlMapping, null, null);
	}

	public String getQueryReference() {
		return queryReference;
	}

	public void setQueryReference(String queryReference) {
		this.queryReference = queryReference;
	}

	@Override
	public SQLProperty clone() {
		SQLProperty clonedObject = new SQLProperty(this.alias, this.sqlMapping, this.queryReference, this.sqlFunction);
		return clonedObject;
	}

	public String getSqlFunction() {
		return sqlFunction;
	}

	public void setSqlFunction(String sqlFunction) {
		this.sqlFunction = sqlFunction;
	}

	public boolean isToValueAlias() {
		return sqlMapping != null && sqlMapping.equalsIgnoreCase("#TO_VALUE#");
	}

	public boolean isToValueSQLAlias() {
		return sqlMapping != null && sqlMapping.contains("#TO_SQL_VALUE#");
	}


	public String getNonUsedSpecialCriteriaReplacement() {
		return nonUsedSpecialCriteriaReplacement;
	}

	public void setNonUsedSpecialCriteriaReplacement(String nonUsedSpecialCriteriaReplacement) {
		this.nonUsedSpecialCriteriaReplacement = nonUsedSpecialCriteriaReplacement;
	}

}
