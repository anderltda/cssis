package br.com.cssis.foundation.query.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import br.com.cssis.foundation.BasicException;
import br.com.cssis.foundation.BasicObject;
import br.com.cssis.foundation.ExceptionConstants;
import br.com.cssis.foundation.ExceptionHandler;
import br.com.cssis.foundation.util.SetHelper;
import br.com.cssis.foundation.util.StringHelper;

@SuppressWarnings("unchecked")
public class DatabaseQuery extends BasicObject {
	private static final String OPEN_P_NESTED = " (";
	private static final String CLOSE_P_NESTED = ")";
	private static final long serialVersionUID = 1L;
	private static final Pattern patDistinct = Pattern.compile(".*SELECT\\s+DISTINCT\\s+.*");

	protected String name;
	protected boolean loadAllRecord = false;
	protected int recordsToFetch = 20;
	protected boolean stopOnTooManyRecord = false;
	protected int maxRecordToFecth = 300;
	protected String transporterClassName = "";
	protected boolean createSQLCountStatement = true;
	protected Class transporterClass = null;
	protected String datasourceName = "";

	protected List<SQLDefiniton> defs;

	protected String customSQLCount = "";

	protected Map<String, SQLProperty> allowedCriteria = new HashMap<String, SQLProperty>();
	protected Set<String> specialCriteria = new HashSet<String>();
	protected List<QueryFieldMetaData> columnMetaData;
	protected boolean populatedMetaData;

	public DatabaseQuery() {
		defs = new ArrayList<SQLDefiniton>(1);
		defs.add(new SQLDefiniton());
	}

	public boolean isLoadAllRecord() {
		return loadAllRecord;
	}

	public void setLoadAllRecord(boolean loadAllRecord) {
		this.loadAllRecord = loadAllRecord;
	}

	public int getRecordsToFetch() {
		return recordsToFetch;
	}

	public void setRecordsToFetch(int recordsToFetch) {
		this.recordsToFetch = recordsToFetch;
	}

	public boolean isStopOnTooManyRecord() {
		return stopOnTooManyRecord;
	}

	public void setStopOnTooManyRecord(boolean stopOnTooManyRecord) {
		this.stopOnTooManyRecord = stopOnTooManyRecord;
	}

	public int getMaxRecordToFecth() {
		return maxRecordToFecth;
	}

	public void setMaxRecordToFecth(int maxRecordToFecth) {
		this.maxRecordToFecth = maxRecordToFecth;
	}

	public String getTransporterClassName() {
		return transporterClassName;
	}

	public void setTransporterClassName(String transporterClassName) {
		this.transporterClassName = transporterClassName;
	}

	public Map<String, SQLProperty> getAllowedCriteria() {
		return allowedCriteria;
	}

	public void setAllowedCriteria(HashMap<String, SQLProperty> allowedCriteria) {
		this.allowedCriteria = allowedCriteria;
	}

	public SQLProperty getAllowedProperty(FilterCondition newCondition) {
		return getAllowedProperty(newCondition.getProperty().getAlias());
	}

	public SQLProperty getAllowedProperty(String alias) {
		SQLProperty allowedProperty = allowedCriteria.get(alias);
		if (allowedProperty != null) {
			return allowedProperty;
		} else {
			return null;
		}
	}

	private String buildOrderClause(List<SortingCondition> sortingCriteriaList, boolean ignoreCurrentOrderClause) throws BasicException {
		// rule: if there are dynamic Sorting...it will replace static Order By
		// otherwise will use static Order By

		if (SetHelper.isEmpty(sortingCriteriaList)) {
			return getOrderClause();
		}
		
		// allowedProperty
		StringBuilder tmpSortClause = new StringBuilder();
		boolean existsOrderClause = false;
		if (!ignoreCurrentOrderClause) {
			if (StringHelper.nonEmpty(getOrderClause())) {
				tmpSortClause.append(getOrderClause()).append(" ").append(",");
				existsOrderClause = true;
			}
		}
		
		// otherwise it will return the dynamic Order by
		if(!existsOrderClause)
			tmpSortClause.append("ORDER BY ");

		for (SortingCondition sortItem : sortingCriteriaList) {
			tmpSortClause.append(sortItem.getSQLRepresentation()).append(",");
		}
		
		tmpSortClause.deleteCharAt(tmpSortClause.length() - 1);
		return tmpSortClause.toString();
	}

	protected void handleSortingList(List<SortingCondition> sortingList) throws BasicException {
		if (SetHelper.isEmpty(sortingList))
			return;
		for (SortingCondition sortingCondition : sortingList) {
			SQLProperty concreteAllowedProperty = getAllowedProperty(sortingCondition.getAlias());
			if (concreteAllowedProperty == null) {
				StringBuilder builder = new StringBuilder().append("Invalid SQLSortingCondition. Property named :").append(sortingCondition.getAlias()).append(" is not accepted");
				log().debug(builder.toString());
				throw ExceptionHandler.getInstance().generateException(ExceptionConstants.SQL_INVALID_CRITERIA, builder.toString());
			}
			sortingCondition.setSqlMapping(concreteAllowedProperty.getSqlMapping());
		}

	}

	protected void handleConditionList(List<FilterCondition> criteriaList) throws BasicException {
		if (SetHelper.isEmpty(criteriaList))
			return;

		for (FilterCondition condition : criteriaList) {
			SQLProperty concreteAllowedProperty = getAllowedProperty(condition.getProperty().getAlias());
			if (concreteAllowedProperty == null) {
				StringBuilder builder = new StringBuilder().append("Invalid SQLCondition. Property named :").append(condition.getProperty().getAlias()).append(" is not accepted");
				log().debug(builder.toString());
				throw ExceptionHandler.getInstance().generateException(ExceptionConstants.SQL_INVALID_CRITERIA, builder.toString());
			}
			condition.getProperty().setAlias(concreteAllowedProperty.getAlias());
			condition.getProperty().setQueryReference(concreteAllowedProperty.getQueryReference());
			condition.getProperty().setSqlMapping(concreteAllowedProperty.getSqlMapping());

			if (condition.isUseOtherCriteria()) {
				SQLProperty other = getAllowedProperty(condition.getConditionValue().getValue().toString());
				if (other == null) {
					StringBuilder builder = new StringBuilder().append("Invalid Criteria-Reference. Property named :").append(condition.getConditionValue().getValue().toString()).append(" is not accepted");
					log().debug(builder.toString());
					throw ExceptionHandler.getInstance().generateException(ExceptionConstants.SQL_INVALID_CRITERIA, builder.toString());
				} else {
					condition.setOtherCriteriaSqlMapping(other.getSqlMapping() != null ? other.getSqlMapping() : other.getAlias());
				}
			}

			if (SetHelper.nonEmpty(condition.getAndNestedCondition())) {
				handleConditionList(condition.getAndNestedCondition());
			}

			if (SetHelper.nonEmpty(condition.getOrNestedCondition())) {
				handleConditionList(condition.getOrNestedCondition());
			}

			// verifica os special criterias
			if (SetHelper.nonEmpty(getSpecialCriteria())) {
				String tmp = condition.getProperty().getAlias();
				if (getSpecialCriteria().contains(tmp)) {
					condition.setSpecialCriteria(true);
				}
			}

		}
	}

	private String buildWhereClause(List<FilterCondition> selectCriteriaList, List<Object> wildCardValues, boolean ignoreCurrentWhereClause, boolean forcedEmptyClause) throws BasicException {
		boolean useWildCard = (wildCardValues != null);

		if (SetHelper.isEmpty(selectCriteriaList)) {
			return getWhereClause();
		}
		// allowedProperty
		StringBuilder tmpWhereClause = new StringBuilder();
		boolean existsWhereClause = false;
		if (ignoreCurrentWhereClause) {
			existsWhereClause = false;
		} else {
			if (StringHelper.nonEmpty(getWhereClause())) {
				tmpWhereClause.append(getWhereClause()).append(" ");
				existsWhereClause = true;
			}
		}

		for (FilterCondition conditionItem : selectCriteriaList) {
			if (conditionItem.isSpecialCriteria())
				continue;
			boolean hasNested = conditionItem.hasNestedConditions();
			if(!conditionItem.isIgnoreCondition()){
				if (existsWhereClause && (!forcedEmptyClause)) {
					if (useWildCard) {
						if(!conditionItem.getProperty().isToValueAlias()){
							tmpWhereClause.append(conditionItem.getSqlMergeCondtion()).append(hasNested ? OPEN_P_NESTED : " ").append(getConditionRepresentation(conditionItem, wildCardValues)).append(hasNested ? CLOSE_P_NESTED : " ");
						}
					} else {
						tmpWhereClause.append(conditionItem.getSqlMergeCondtion()).append(hasNested ? OPEN_P_NESTED : " ").append(getConditionRepresentation(conditionItem)).append(hasNested ? CLOSE_P_NESTED : " ");
					}
				} else {
					if (useWildCard) {
						tmpWhereClause.append("WHERE ").append(hasNested ? OPEN_P_NESTED : " ").append(getConditionRepresentation(conditionItem, wildCardValues)).append(hasNested ? CLOSE_P_NESTED : " ");
					} else {
						tmpWhereClause.append("WHERE ").append(hasNested ? OPEN_P_NESTED : " ").append(getConditionRepresentation(conditionItem)).append(hasNested ? CLOSE_P_NESTED : " ");
					}
	
					existsWhereClause = true;
					forcedEmptyClause = false;
				}
			}
		}
		return tmpWhereClause.toString();

	}

	public String buildWhereClause(List<FilterCondition> selectCriteriaList, List<Object> wildCardValues) throws BasicException {
		return buildWhereClause(selectCriteriaList, wildCardValues, false, false);
	}
	
	public String buildOrderClause(List<SortingCondition> sortingCriteriaList) throws BasicException {
		return buildOrderClause(sortingCriteriaList, false);
	}

	public String generateRepresentation(List<FilterCondition> selectCriteriaList, List<SortingCondition> sortingCriteriaList, List<Object> wildCardValues) throws BasicException {
		StringBuilder sqlRepresentation = new StringBuilder();
		sqlRepresentation.append(buildSelectClause()).append(" ").append(buildFromClause()).append(" ").append(buildWhereClause(selectCriteriaList, wildCardValues)).append(" ").append(buildGroupClause()).append(" ").append(buildHavingClause()).append(" ").append(buildOrderClause(sortingCriteriaList));
		return handleSpecialParameter(sqlRepresentation, selectCriteriaList, wildCardValues);
	}

	public String generateSQL(List<FilterCondition> selectCriteriaList, List<SortingCondition> sortingCriteriaList) throws BasicException {
		return generateRepresentation(selectCriteriaList, sortingCriteriaList, null);
	}

	public String generateWildCardSQL(List<FilterCondition> selectCriteriaList, List<SortingCondition> sortingCriteriaList, List<Object> wildCardValues) throws BasicException {
		return generateRepresentation(selectCriteriaList, sortingCriteriaList, wildCardValues);
	}

	public String generateCountSQL(List<FilterCondition> selectCriteriaList) throws BasicException {
		return generateCountSQL(selectCriteriaList, null);
	}

	public String generateCountSQL(List<FilterCondition> selectCriteriaList, List<Object> wildCardValues) throws BasicException {
		StringBuilder countSQL = new StringBuilder();
		boolean groupByClauseEmpty = StringHelper.isEmpty(buildGroupClause());
		if (useCustomSQLCount()) {
			// caso seja um SQLCustomCount, ai precisa ignorar as clausulas
			// where existentes e apenas concatenar os valores
			if (customSQLCount.indexOf("WHERE") < 0)
				countSQL.append(customSQLCount).append(" ").append(buildWhereClause(selectCriteriaList, wildCardValues, true, true));
			else
				countSQL.append(customSQLCount).append(" ").append(buildWhereClause(selectCriteriaList, wildCardValues, true, false));
		} else {
			if (groupByClauseEmpty && !contemDistinct(getSelectClause().toUpperCase())) {
				countSQL.append("SELECT COUNT(*) ").append(buildFromClause()).append(" ").append(buildWhereClause(selectCriteriaList, wildCardValues)).append(" ").append(buildGroupClause()).append(" ").append(buildHavingClause());
			} else {
				countSQL.append("SELECT COUNT(*) FROM (").append(buildSelectClause()).append(" ").append(buildFromClause()).append(" ").append(buildWhereClause(selectCriteriaList, wildCardValues)).append(" ").append(buildGroupClause()).append(" ").append(buildHavingClause()).append(")");
			}
		}
		return handleSpecialParameter(countSQL, selectCriteriaList, wildCardValues);
	}

	private boolean contemDistinct(String str) {
		Matcher m = patDistinct.matcher(str);
		return m.find();
	}

	protected String handleSpecialParameter(StringBuilder sql, List<FilterCondition> selectCriteriaList, List<Object> wildCardValues) throws BasicException {
		String tmp = sql.toString();

		if (SetHelper.nonEmpty(selectCriteriaList)) {
			boolean hasWildCards = wildCardValues != null;
			for (FilterCondition filterCondition : selectCriteriaList) {
				if (tmp.indexOf(":" + filterCondition.getProperty().getAlias()) >= 0) {
					if (hasWildCards) {
						tmp = tmp.replace(":" + filterCondition.getProperty().getAlias(), filterCondition.getSQLWildCardRepresentation(wildCardValues));
					} else {
						tmp = tmp.replace(":" + filterCondition.getProperty().getAlias(), filterCondition.getSQLRepresentation());
					}
				}
			}
		}

		for (String special : getSpecialCriteria()) {
			if (tmp.indexOf(":" + special) >= 0) {
				SQLProperty prop = getAllowedCriteria().get(special);
				tmp = tmp.replaceAll(":" + special, prop.getNonUsedSpecialCriteriaReplacement());
			}
		}

		return tmp;
	}

	protected String buildHavingClause() {
		return defs.get(0).getHavingClause();
	}

	protected String buildGroupClause() {
		return defs.get(0).getGroupClause();
	}

	protected String buildFromClause() {
		return defs.get(0).getFromClause();
	}

	protected String buildSelectClause() {
		return defs.get(0).getSelectClause();
	}

	public String getSelectClause() {
		return defs.get(0).getSelectClause();
	}

	public void setSelectClause(String selectClause) {
		defs.get(0).selectClause = selectClause;
	}

	public String getFromClause() {
		return defs.get(0).getFromClause();
	}

	public void setFromClause(String fromClause) {
		defs.get(0).fromClause = fromClause;
	}

	public String getWhereClause() {
		return defs.get(0).getWhereClause();
	}

	public void setWhereClause(String whereClause) {
		defs.get(0).whereClause = whereClause;
	}

	public String getHavingClause() {
		return defs.get(0).getHavingClause();
	}

	public void setHavingClause(String havingClause) {
		defs.get(0).havingClause = havingClause;
	}

	public String getGroupClause() {
		return defs.get(0).getGroupClause();
	}

	public void setGroupClause(String groupClause) {
		defs.get(0).groupClause = groupClause;
	}

	public String getOrderClause() {
		return defs.get(0).getOrderClause();
	}

	public void setOrderClause(String orderClause) {
		defs.get(0).orderClause = orderClause;
	}

	public String getCustomSQLCount() {
		return customSQLCount;
	}

	public void setCustomSQLCount(String customSQLCount) {
		this.customSQLCount = customSQLCount;
	}

	public boolean isCreateSQLCountStatement() {
		return createSQLCountStatement;
	}

	public void setCreateSQLCountStatement(boolean createSQLCountStatement) {
		this.createSQLCountStatement = createSQLCountStatement;
	}

	public Class getTransporterClass() {
		return transporterClass;
	}

	public void setTransporterClass(Class transporterClass) {
		this.transporterClass = transporterClass;
	}

	public String getDatasourceName() {
		return datasourceName;
	}

	public void setDatasourceName(String datasourceName) {
		this.datasourceName = datasourceName;
	}

	@Override
	public String toString() {
		return " Query: " + name;

	}

	public boolean useCustomSQLCount() {
		return StringHelper.nonEmpty(customSQLCount);
	}

	public boolean isMetaDataEmpty() {
		return SetHelper.isEmpty(columnMetaData);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getConditionRepresentation(FilterCondition conditionItem, List<Object> wildCardValues) throws BasicException {
		return conditionItem.getSQLWildCardRepresentation(wildCardValues);
	}

	public String getConditionRepresentation(FilterCondition conditionItem) throws BasicException {
		return conditionItem.getSQLRepresentation();
	}

	public List<QueryFieldMetaData> getColumnMetaData() {
		return columnMetaData;
	}

	public List<String> getColumnNameList() {
		List<String> returnValue = new ArrayList<String>();
		if (SetHelper.nonEmpty(columnMetaData)) {
			for (QueryFieldMetaData column : columnMetaData) {
				returnValue.add(column.getLabel());
			}
		}
		return returnValue;
	}

	public void setColumnMetaData(List<QueryFieldMetaData> columnMetaData) {
		this.columnMetaData = columnMetaData;
	}

	public boolean isPopulatedMetaData() {
		return populatedMetaData;
	}

	public void setPopulatedMetaData(boolean populatedMetaData) {
		this.populatedMetaData = populatedMetaData;
	}

	public void setAllowedCriteria(Map<String, SQLProperty> allowedCriteria) {
		this.allowedCriteria = allowedCriteria;
	}

	public Set<String> getSpecialCriteria() {
		return specialCriteria;
	}

	public void setSpecialCriteria(Set<String> specialCriteria) {
		this.specialCriteria = specialCriteria;
	}

}