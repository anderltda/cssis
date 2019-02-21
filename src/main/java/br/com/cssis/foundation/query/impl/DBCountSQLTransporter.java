package br.com.cssis.foundation.query.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;


public class DBCountSQLTransporter extends GenericDBTransporter {
	private static final long serialVersionUID = 1L;
	
	private int count;
	
	public void setCount(int count) {
		this.count = count;
	}
	
	@Override
	protected void addValues(ResultSet row, List<String> fieldList) throws SQLException {
		setCount(row.getInt(1));
	}
	

	public int getCount() {
		return count;
	}
	
		

}
