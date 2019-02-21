package br.com.cssis.foundation.query.impl;

import br.com.cssis.foundation.BasicObject;
import br.com.cssis.foundation.util.StringHelper;

public class SortingCondition extends BasicObject {
	private static final long serialVersionUID = 1L;
	public enum SORTING_ORIENTATION { ASC , DESC };
	private static final String ASC_SQL_REPRESENTATION = " ";
	private static final String DESC_SQL_REPRESENTATION = " DESC ";
	
	public static final SORTING_ORIENTATION ASC = SORTING_ORIENTATION.ASC;
	public static final SORTING_ORIENTATION DESC = SORTING_ORIENTATION.DESC;
	
	private String alias;
	private String sqlMapping;;	
	private SORTING_ORIENTATION orientation;

	public SortingCondition(String name) {
		alias = name;
		orientation = SORTING_ORIENTATION.ASC;
	}
	
	public SortingCondition(String name, SORTING_ORIENTATION orientation) {
		alias = name;
		this.orientation = orientation;
	}	
	
	public SORTING_ORIENTATION getOrientation() {
		return orientation;
	}
	public void setOrientation(SORTING_ORIENTATION orientation) {
		this.orientation = orientation;
	}

	private static String getSQLOrientationRepresentation(SORTING_ORIENTATION orientation) {
		switch (orientation) {
		case ASC : return ASC_SQL_REPRESENTATION;
		case DESC: return DESC_SQL_REPRESENTATION;
		default: return "";
		}	
	}

	public String getSQLRepresentation () {
		if (StringHelper.nonEmpty(getSqlMapping())) {
			return getSqlMapping() + " " + SortingCondition.getSQLOrientationRepresentation(getOrientation());
		}
		else return "";
	}

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
	
	public String getOrientationRepresentation() {
		return SortingCondition.getSQLOrientationRepresentation(getOrientation());
	}
	
	@Override
	public String toString() {
		return alias + " " + getOrientationRepresentation(); 
	}
	
}
