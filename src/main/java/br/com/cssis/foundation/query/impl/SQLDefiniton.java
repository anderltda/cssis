package br.com.cssis.foundation.query.impl;

import br.com.cssis.foundation.BasicObject;

public class SQLDefiniton extends BasicObject {
	private static final long serialVersionUID = 1L;
	
	protected String selectClause = "";
	protected String fromClause = "";
	protected String whereClause = "";
	protected String havingClause = "";
	protected String groupClause = "";
	protected String orderClause = "";
	protected String mergeSQL = "";
	
	public String getMergeSQL() {
		return mergeSQL;
	}
	public void setMergeSQL(String mergeSQL) {
		this.mergeSQL = mergeSQL;
	}
	public String getSelectClause() {
		return selectClause;
	}
	public void setSelectClause(String selectClause) {
		this.selectClause = selectClause;
	}
	public String getFromClause() {
		return fromClause;
	}
	public void setFromClause(String fromClause) {
		this.fromClause = fromClause;
	}
	public String getWhereClause() {
		return whereClause;
	}
	public void setWhereClause(String whereClause) {
		this.whereClause = whereClause;
	}
	public String getHavingClause() {
		return havingClause;
	}
	public void setHavingClause(String havingClause) {
		this.havingClause = havingClause;
	}
	public String getGroupClause() {
		return groupClause;
	}
	public void setGroupClause(String groupClause) {
		this.groupClause = groupClause;
	}
	public String getOrderClause() {
		return orderClause;
	}
	public void setOrderClause(String orderClause) {
		this.orderClause = orderClause;
	}
	
}
