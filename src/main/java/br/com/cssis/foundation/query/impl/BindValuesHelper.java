package br.com.cssis.foundation.query.impl;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Date;

import javax.persistence.Query;

import br.com.cssis.foundation.BasicObject;
import oracle.jdbc.OraclePreparedStatement;

public class BindValuesHelper extends BasicObject {
	private static final long serialVersionUID = 1L;

	public static void assignByIndex(PreparedStatement ps, int index, Object value) throws SQLException {
		// tratamento para o nullo
		if (value == null) ps.setNull(index, getSQLType(value));
		else ps.setObject(index, value);
	}

	private static int getSQLType(Object value) {
		if (value instanceof String) return Types.VARCHAR;
		if (value instanceof Long) return Types.BIGINT;
		if (value instanceof Integer) return Types.INTEGER;
		if (value instanceof BigDecimal) return Types.DECIMAL;
		if (value instanceof Double) return Types.DOUBLE;
		if (value instanceof Float) return Types.FLOAT;
		if (value instanceof Boolean) return Types.CHAR;
		if (value instanceof Date) return Types.DATE;
		if (value instanceof Time) return Types.TIME;
		if (value instanceof Timestamp) return Types.TIMESTAMP;
		return Types.OTHER;
	}

	public static void assignByName(OraclePreparedStatement ps, int index, Object value) throws SQLException {
		// tratamento para o nullo
		if (value == null) ps.setNull(index, getSQLType(value));
		else if (FilterCondition.BIND_BY_NAME) {
			if (value instanceof java.util.Date) {
				ps.setTimestampAtName(FilterCondition.getBindName(index), new Timestamp(((Date) value).getTime()));
			} else {
				ps.setObjectAtName(FilterCondition.getBindName(index), value);
			}
		} else {
			if (value instanceof java.util.Date) {
				ps.setTimestamp(index, new Timestamp(((Date) value).getTime()));
			} else {
				ps.setObject(index, value);
			}

		}
	}

	public static void assignByName(OraclePreparedStatement ps, String paramName, Object value) throws SQLException {
		// tratamento para o nullo
		if (value instanceof java.util.Date) {
			ps.setTimestampAtName(paramName, new Timestamp(((Date) value).getTime()));
		} else {
			ps.setObjectAtName(paramName, value);
		}
	}

	public static void assignByIndex(Query query, int index, Object value) throws SQLException {
		// tratamento para o nullo
		query.setParameter(index, value);
	}

}