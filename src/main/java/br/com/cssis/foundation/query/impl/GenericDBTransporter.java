package br.com.cssis.foundation.query.impl;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.com.cssis.foundation.BasicException;
import br.com.cssis.foundation.BasicObject;
import br.com.cssis.foundation.ExceptionConstants;
import br.com.cssis.foundation.ExceptionHandler;
import br.com.cssis.foundation.query.DBTransporter;
import br.com.cssis.foundation.util.BooleanHelper;
import br.com.cssis.foundation.util.DateTimeHelper;
import br.com.cssis.foundation.util.NumberHelper;
import br.com.cssis.foundation.util.SetHelper;
import br.com.cssis.foundation.util.StringHelper;

public class GenericDBTransporter extends BasicObject implements DBTransporter {
	private static final long serialVersionUID = 1L;
	public Map<String, Object> values;
	public List<String> fieldList;

	@Override
	public void populate(ResultSet row) throws BasicException {
		if (SetHelper.isEmpty(values) && SetHelper.nonEmpty(values)) {
			values.clear();
		} else {
			values = new HashMap<String, Object>();
		}

		if (row == null) return;

		try {
			addValues(row, fieldList);

		} catch (SQLException e) {
			if (log().isDebugEnabled()) {
				log().debug(e);
			}
			throw ExceptionHandler.getInstance().generateException(ExceptionConstants.SQL_INVALID_SQL_TEXT, e.getMessage(), e);
		}
	}

	protected void addValues(ResultSet row, List<String> fieldList) throws SQLException {
		for (String fieldName : fieldList) {
			values.put(getJavaName(fieldName), row.getObject(fieldName));
		}
	}

	public Object getObjectValue(String fieldName) throws BasicException {
		if (values.containsKey(getJavaName(fieldName))) {
			Object value = values.get(getJavaName(fieldName));
			if (value != null) {
				return value;
			} else return null;
		} else {
			String msg = "Nome de coluna Invalido: " + fieldName;
			log().debug(msg);
			throw ExceptionHandler.getInstance().generateException(ExceptionConstants.SQL_INVALID_COLUMN_NAME, msg);
		}
	}

	public String getStringValue(String fieldName) throws BasicException {
		Object objValue = getObjectValue(getJavaName(fieldName));
		if (objValue != null) return objValue.toString();
		else return null;
	}

	public Long getLongValue(String fieldName) throws BasicException {
		return NumberHelper.getLong(getObjectValue(getJavaName(fieldName)));
	}

	public Short getShortValue(String fieldName) throws BasicException {
		return NumberHelper.getShort(getObjectValue(getJavaName(fieldName)));
	}

	public Byte getByteValue(String fieldName) throws BasicException {
		return NumberHelper.getByte(getObjectValue(getJavaName(fieldName)));
	}

	public Integer getIntValue(String fieldName) throws BasicException {
		return NumberHelper.getInteger(getObjectValue(getJavaName(fieldName)));
	}

	public Double getDoubleValue(String fieldName) throws BasicException {
		return NumberHelper.getDouble(getObjectValue(getJavaName(fieldName)));
	}

	public Float getFloatValue(String fieldName) throws BasicException {
		return NumberHelper.getFloat(getObjectValue(getJavaName(fieldName)));
	}

	public Boolean getBooelanValue(String fieldName) throws BasicException {
		return BooleanHelper.getBoolean(getObjectValue(getJavaName(fieldName)));
	}

	public BigDecimal getBigDecimalValue(String fieldName) throws BasicException {
		return NumberHelper.getBigDecimalValue(getObjectValue(getJavaName(fieldName)));
	}

	public Date getDateValue(String fieldName) throws BasicException {
		Object obj = getObjectValue(getJavaName(fieldName));
		if (obj != null) {
			try {
				return DateTimeHelper.getDate(obj.toString());
			} catch (ParseException e) {
				throw new BasicException(e);
			}
		} else return null;

	}

	public String getJavaName(String fieldName) {
		return fieldName;
	}

	public GenericDBTransporter() {

	}

	@Override
	public void setFieldList(List<String> fieldList) {
		this.fieldList = fieldList;

	}

	@Override
	public String toString() {
		return StringHelper.mapAsString(values);
	}
}
