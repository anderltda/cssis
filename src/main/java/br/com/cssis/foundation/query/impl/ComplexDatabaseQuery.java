package br.com.cssis.foundation.query.impl;

import java.util.List;

import br.com.cssis.foundation.BasicException;
import br.com.cssis.foundation.util.SetHelper;
import br.com.cssis.foundation.util.StringHelper;

public class ComplexDatabaseQuery extends DatabaseQuery {
	private static final long serialVersionUID = 1L;

	public void addSQLDefinition(SQLDefiniton def) {
		defs.add(def);
	}

	public void addSQLDefinition(List<SQLDefiniton> definitionList) {
		defs.addAll(definitionList);
	}

	public String getWhereClause(int index) {
		if (defs.size() <= index) return "";
		else return defs.get(index).getWhereClause();
	}

	public String getOrderClause(int index) {
		if (defs.size() <= index) return "";
		else return defs.get(index).getOrderClause();
	}

	@Override
	public String buildWhereClause(List<FilterCondition> selectCriteriaList, List<Object> wildCardValues) throws BasicException {

		StringBuilder returnValue = new StringBuilder();
		for (int i = 0; i < defs.size(); i++) {
			returnValue.append(buildWhereClause(selectCriteriaList, wildCardValues, i)).append(" ");
		}

		return returnValue.toString();
	}

	protected String buildWhereClause(List<FilterCondition> selectCriteriaList, List<Object> wildCardValues, int index) throws BasicException {
		boolean useWildCard = (wildCardValues != null);

		if (SetHelper.isEmpty(selectCriteriaList)) {
			return getWhereClause(index);
		}

		// allowedProperty
		StringBuilder tmpWhereClause = new StringBuilder();
		boolean existsWhereClause = false;
		if (StringHelper.nonEmpty(getWhereClause(index))) {
			tmpWhereClause.append(getWhereClause(index)).append(" ");
			existsWhereClause = true;
		}

		for (FilterCondition conditionItem : selectCriteriaList) {
			if (conditionItem.isSpecialCriteria()) continue;
			if (existsWhereClause) {
				if (useWildCard) {

					tmpWhereClause.append(FilterCondition.SQL_AND).append(" ").append(getConditionRepresentation(conditionItem, wildCardValues));
				} else {
					tmpWhereClause.append(FilterCondition.SQL_AND).append(" ").append(getConditionRepresentation(conditionItem));
				}
			} else {
				if (useWildCard) {
					tmpWhereClause.append("WHERE ").append(getConditionRepresentation(conditionItem, wildCardValues));
				} else {
					tmpWhereClause.append("WHERE ").append(getConditionRepresentation(conditionItem));
				}
				existsWhereClause = true;
			}
		}
		return tmpWhereClause.toString();

	}

	@Override
	public String generateCountSQL(List<FilterCondition> selectCriteriaList, List<Object> wildCardValues) throws BasicException {
		StringBuilder countSQL = new StringBuilder();
		if (useCustomSQLCount()) {
			// caso seja um SQLCustomCount, ai precisa ignorar as clausulas where existentes e apenas concatenar os valores
			countSQL.append(getCustomSQLCount());
			boolean useSQLAnd = true;
			if (getCustomSQLCount().indexOf("WHERE") < 0) {
				countSQL.append(" WHERE ");
				useSQLAnd = false;
			}
			boolean useWildCard = wildCardValues != null;
			for (FilterCondition conditionItem : selectCriteriaList) {
				if (useSQLAnd) {
					if (useWildCard) {
						countSQL.append(FilterCondition.SQL_AND).append(" ").append(conditionItem.getSQLWildCardRepresentation(wildCardValues));
					} else {
						countSQL.append(FilterCondition.SQL_AND).append(" ").append(conditionItem.getSQLRepresentation());
					}
				} else {
					if (useWildCard) {
						countSQL.append(conditionItem.getSQLWildCardRepresentation(wildCardValues));
					} else {
						countSQL.append(conditionItem.getSQLRepresentation());
					}
					useSQLAnd = true;
				}
			}
		} else {
			countSQL.append("SELECT COUNT(*) FROM (").append(generateRepresentation(selectCriteriaList, null, wildCardValues)).append(")");
		}
		return countSQL.toString();
	}

	protected String buildOrderClause(List<SortingCondition> sortingCriteriaList, int index) throws BasicException {
		// rule: if there are dynamic Sorting...it will replace static Order By
		// otherwise will use static Order By
		if (sortingCriteriaList == null || sortingCriteriaList.size() == 0) {
			return getOrderClause(index);
		}
		// otherwise it will return the dynamic Order by
		StringBuilder tmpSortClause = new StringBuilder().append("ORDER BY ");

		for (SortingCondition sortItem : sortingCriteriaList) {
			tmpSortClause.append(sortItem.getSQLRepresentation()).append(",");
		}

		tmpSortClause.deleteCharAt(tmpSortClause.length() - 1);
		return tmpSortClause.toString();
	}

	@Override
	public String generateRepresentation(List<FilterCondition> selectCriteriaList, List<SortingCondition> sortingCriteriaList, List<Object> wildCardValues) throws BasicException {
		StringBuilder sqlRepresentation = new StringBuilder();

		for (int i = 0; i < defs.size(); i++) {
			sqlRepresentation.append(buildMergeClause(i)).append("\n").append(buildSelectClause(i)).append(" ").append(buildFromClause(i)).append(" ").append(buildWhereClause(selectCriteriaList, wildCardValues, i)).append(" ").append(buildGroupClause(i)).append(" ").append(buildHavingClause(i)).append(" ");
		}

		String tmp = handleSpecialParameter(sqlRepresentation, selectCriteriaList, wildCardValues);

		// adicionar o order by do 1o. no final
		return tmp + buildOrderClause(sortingCriteriaList, 0);
	}

	private Object buildMergeClause(int index) {
		if ((defs.size() <= index) || (index == 0)) return "";
		else {
			String sql = defs.get(index).getMergeSQL();
			if (StringHelper.isEmpty(sql)) sql = " UNION ALL ";
			return sql;
		}
	}

	private Object buildHavingClause(int index) {
		if (defs.size() <= index) return "";
		else return defs.get(index).getHavingClause();
	}

	private Object buildGroupClause(int index) {
		if (defs.size() <= index) return "";
		else return defs.get(index).getGroupClause();
	}

	private Object buildFromClause(int index) {
		if (defs.size() <= index) return "";
		else return defs.get(index).getFromClause();
	}

	private Object buildSelectClause(int index) {
		if (defs.size() <= index) return "";
		else return defs.get(index).getSelectClause();
	}

}
