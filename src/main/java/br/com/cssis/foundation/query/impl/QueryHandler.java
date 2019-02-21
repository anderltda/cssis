package br.com.cssis.foundation.query.impl;

import java.net.URL;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.configuration.AbstractConfiguration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.configuration.XMLConfiguration;

import br.com.cssis.foundation.BasicException;
import br.com.cssis.foundation.BasicObject;
import br.com.cssis.foundation.Constants;
import br.com.cssis.foundation.ExceptionConstants;
import br.com.cssis.foundation.ExceptionHandler;
import br.com.cssis.foundation.config.FrameworkConfiguration;
import br.com.cssis.foundation.util.BooleanHelper;
import br.com.cssis.foundation.util.JDBCTypesHandler;
import br.com.cssis.foundation.util.NumberHelper;
import br.com.cssis.foundation.util.SetHelper;
import br.com.cssis.foundation.util.StringHelper;

public class QueryHandler extends BasicObject {
	private static final long serialVersionUID = 1L;

	private static QueryHandler instance;

	private boolean enabled = true;
	private Map<String, DatabaseQuery> queryMap;

	public static QueryHandler getInstance() {
		if (instance == null) {
			synchronized (ExceptionHandler.class) {
				instance = new QueryHandler();
			}
		}
		return instance;
	}

	private QueryHandler() {
		restart();
	}

	private synchronized void restart() {
		if (queryMap != null) {
			queryMap.clear();
		} else {
			queryMap = new HashMap<String, DatabaseQuery>();
		}

		log().info("Reloading Query List");
		enabled = FrameworkConfiguration.getInstance().getBooleanValue(Constants.KEY_QUERY_HANDLER);
		log().info("Reloading Query Handler List");

		if (enabled) {
			Character delimiter = AbstractConfiguration.getDefaultListDelimiter();
			AbstractConfiguration.setDefaultListDelimiter('\0');
			String inputList[] = FrameworkConfiguration.getInstance().getStringArray(Constants.KEY_QUERY_HANDLER_INPUT);
			if (inputList.length > 0) {
				for (String input : inputList) {
					if (StringHelper.nonEmpty(input)) {
						log().info(input);
						loadQueryList(input);
					}
				}
			}
			AbstractConfiguration.setDefaultListDelimiter(delimiter);
		} else {
			log().info("Exception Handler not enabled");
		}
	}

	@SuppressWarnings("unchecked")
	private void loadQueryList(String input) {
		XMLConfiguration config = null;
		try {
			URL url = this.getClass().getResource(input);
			config = new XMLConfiguration(url);
			if (config.isEmpty()) {
				if (log().isErrorEnabled()) {
					log().error("Empty Query List File : " + input);
				}
				return;
			}
			if (log().isDebugEnabled()) {
				log().debug("Parsing Query List File : " + input);
			}
		} catch (ConfigurationException e) {
			if (log().isErrorEnabled()) {
				log().error("Arquivo de Tratamento de Query List : " + input);
				log().error(e);
			}
			return;
		}
		int i = 0;
		while (true) {
			try {
				HierarchicalConfiguration sub = config.configurationAt("query(" + i + ")");
				String name = sub.getString("name");
				String loadAllRecord = sub.getString("loadAllRecord");
				int recordsToFetch = NumberHelper.intValue(sub.getString("recordsToFetch"));
				String stopOnTooManyRecord = sub.getString("stopOnTooManyRecord");
				int maxRecordToFecth = NumberHelper.intValue(sub.getString("maxRecordToFecth"));
				String transporterClassName = sub.getString("transporterClassName");
				String datasourceName = sub.getString("datasourceName");
				String selectClause = sub.getString("selectClause");
				String fromClause = sub.getString("fromClause");
				String whereClause = sub.getString("whereClause");
				String havingClause = sub.getString("havingClause");
				String groupClause = sub.getString("groupClause");
				String orderClause = sub.getString("orderClause");
				String customSQLCount = sub.getString("customSQLCount");
				String customQueryClass = sub.getString("customQueryClass");

				Set<String> specialCriterias = new HashSet<String>();
				specialCriterias.addAll(getSpecialCriterias(selectClause));
				specialCriterias.addAll(getSpecialCriterias(fromClause));
				specialCriterias.addAll(getSpecialCriterias(whereClause));
				specialCriterias.addAll(getSpecialCriterias(havingClause));
				specialCriterias.addAll(getSpecialCriterias(groupClause));
				specialCriterias.addAll(getSpecialCriterias(orderClause));

				ArrayList<HierarchicalConfiguration> conditionNodes = (ArrayList) config.configurationsAt("query(" + i + ").condition");
				ArrayList<SQLProperty> conditionList = new ArrayList<SQLProperty>(conditionNodes.size());
				for (HierarchicalConfiguration condition : conditionNodes) {
					String alias = condition.getString("alias");
					String sqlMapping = condition.getString("sqlMapping");
					if (StringHelper.isEmptyTrim(sqlMapping)) sqlMapping = alias;
					String queryReference = condition.getString("queryReference");
					String nonUsedReplacement = condition.getString("nonUsedReplacement");

					if (StringHelper.nonEmpty(alias)) {
						SQLProperty newProperty = new SQLProperty(alias, sqlMapping, queryReference, null);
						if (StringHelper.nonEmptyTrim(nonUsedReplacement)) newProperty.setNonUsedSpecialCriteriaReplacement(nonUsedReplacement);
						conditionList.add(newProperty);
					}
					
				}

				ArrayList<HierarchicalConfiguration> additionSQLNodes = (ArrayList) config.configurationsAt("query(" + i + ").additional-sql");
				ArrayList<SQLDefiniton> additionalSQLList = new ArrayList<SQLDefiniton>(additionSQLNodes.size());
				for (HierarchicalConfiguration sqlDef : additionSQLNodes) {
					String additionalSelectClause = sqlDef.getString("selectClause");
					String additionalFromClause = sqlDef.getString("fromClause");
					String additionalWhereClause = sqlDef.getString("whereClause");
					String additionalHavingClause = sqlDef.getString("havingClause");
					String additionalGroupClause = sqlDef.getString("groupClause");
					String additionalOrderClause = sqlDef.getString("orderClause");
					String additionalMergeClause = sqlDef.getString("mergeClause");
					SQLDefiniton newSQLDefiniton = new SQLDefiniton();

					if (StringHelper.nonEmpty(additionalSelectClause))
						newSQLDefiniton.setSelectClause(additionalSelectClause);
					if (StringHelper.nonEmpty(additionalFromClause))
						newSQLDefiniton.setFromClause(additionalFromClause);
					if (StringHelper.nonEmpty(additionalWhereClause))
						newSQLDefiniton.setWhereClause(additionalWhereClause);
					if (StringHelper.nonEmpty(additionalHavingClause))
						newSQLDefiniton.setHavingClause(additionalHavingClause);
					if (StringHelper.nonEmpty(additionalGroupClause))
						newSQLDefiniton.setGroupClause(additionalGroupClause);
					if (StringHelper.nonEmpty(additionalOrderClause))
						newSQLDefiniton.setOrderClause(additionalOrderClause);
					if (StringHelper.nonEmpty(additionalMergeClause))
						newSQLDefiniton.setMergeSQL(additionalMergeClause);

					additionalSQLList.add(newSQLDefiniton);

					specialCriterias.addAll(getSpecialCriterias(additionalSelectClause));
					specialCriterias.addAll(getSpecialCriterias(additionalFromClause));
					specialCriterias.addAll(getSpecialCriterias(additionalWhereClause));
					specialCriterias.addAll(getSpecialCriterias(additionalHavingClause));
					specialCriterias.addAll(getSpecialCriterias(additionalGroupClause));
					specialCriterias.addAll(getSpecialCriterias(additionalOrderClause));
				}

				if (StringHelper.isEmpty(name)) {
					if (log().isWarnEnabled()) {
						log().warn("Query Name is mandatory. Skipping this entry:" + i);
					}
				} else {
					DatabaseQuery query = null;
					if (StringHelper.nonEmpty(customQueryClass)) {
						try {
							query = (DatabaseQuery) Class.forName(customQueryClass).newInstance();
						} catch (Exception e) {
							if (log().isErrorEnabled()) {
								log().error("Erro ao carregar Classe Para Query: " + name + " usando Classes Padrao");
								log().error(e);
							}
							query = createQueryClass(additionalSQLList);
						}
					} else {
						query = createQueryClass(additionalSQLList);
					}

					query.setLoadAllRecord(BooleanHelper.booleanValue(loadAllRecord));
					query.setName(name);
					if (recordsToFetch > 0)
						query.setRecordsToFetch(recordsToFetch);
					if (StringHelper.nonEmpty(stopOnTooManyRecord))
						query.setStopOnTooManyRecord(BooleanHelper.booleanValue(stopOnTooManyRecord));
					if (maxRecordToFecth > 0)
						query.setMaxRecordToFecth(maxRecordToFecth);
					if (StringHelper.nonEmpty(transporterClassName))
						query.setTransporterClassName(transporterClassName);
					if (StringHelper.nonEmpty(datasourceName))
						query.setDatasourceName(datasourceName);
					if (StringHelper.nonEmpty(selectClause))
						query.setSelectClause(selectClause);
					if (StringHelper.nonEmpty(fromClause))
						query.setFromClause(fromClause);
					if (StringHelper.nonEmpty(whereClause))
						query.setWhereClause(whereClause);
					if (StringHelper.nonEmpty(havingClause))
						query.setHavingClause(havingClause);
					if (StringHelper.nonEmpty(groupClause))
						query.setGroupClause(groupClause);
					if (StringHelper.nonEmpty(orderClause))
						query.setOrderClause(orderClause);
					if (StringHelper.nonEmpty(customSQLCount))
						query.setCustomSQLCount(customSQLCount);

					for (SQLProperty property : conditionList) {
						query.allowedCriteria.put(property.getAlias(), property);
					}

					if (log().isDebugEnabled())
						log().debug(query.toString());

					// get the proper transporter class for this query
					query.setTransporterClass(QueryResourceFactory.getTransporterClass(query.getTransporterClassName()));

					if (SetHelper.nonEmpty(additionalSQLList)) {
						((ComplexDatabaseQuery) query).addSQLDefinition(additionalSQLList);
					}
					query.setSpecialCriteria(specialCriterias);
					queryMap.put(name, query);
				}
				i++;
			} catch (Exception ex) {
				break;
			}
		}
	}

	private DatabaseQuery createQueryClass(ArrayList<SQLDefiniton> additionalSQLList) {
		DatabaseQuery query;
		if (SetHelper.isEmpty(additionalSQLList)) {
			query = new DatabaseQuery();
		} else {
			query = new ComplexDatabaseQuery();
		}
		return query;
	}

	public String printQueryMap() {
		return StringHelper.mapAsString(queryMap);
	}

	public DatabaseQuery getQuery(String queryName) {
		return queryMap.get(queryName);
	}

	public synchronized void extractMetaData(ResultSetMetaData metaData, DatabaseQuery query) throws BasicException {
		if ((query != null) && query.isMetaDataEmpty() && (query.isPopulatedMetaData() == false)) {
			query.setPopulatedMetaData(true);
			try {
				List<QueryFieldMetaData> metaDataFieldList = new ArrayList<QueryFieldMetaData>(metaData.getColumnCount());
				for (int i = 1; i <= metaData.getColumnCount(); i++) {
					QueryFieldMetaData fieldMetaData = new QueryFieldMetaData();
					fieldMetaData.setScale(metaData.getScale(i));
					fieldMetaData.setPrecision(metaData.getPrecision(i));
					fieldMetaData.setJdbcTypeName(metaData.getColumnTypeName(i));
					fieldMetaData.setJdbcType(metaData.getColumnType(i));
					fieldMetaData.setLabel(metaData.getColumnLabel(i));
					fieldMetaData.setLength(metaData.getColumnDisplaySize(i));
					fieldMetaData.setJavaType(JDBCTypesHandler.getInstance().getJavaType(fieldMetaData.getJdbcType(), fieldMetaData.getPrecision(), fieldMetaData.getScale(), fieldMetaData.getLength()));
					fieldMetaData.setPropertyName(StringHelper.getPropertyName(fieldMetaData.getLabel()));
					fieldMetaData.setSetterName(StringHelper.getSetterName(fieldMetaData.getLabel()));
					fieldMetaData.setGetterName(StringHelper.getGetterName(fieldMetaData.getLabel()));
					if (fieldMetaData.getJavaType() == null) {
						log().fatal("Erro ao Buscar o Tipo de Dados para o Campo : " + fieldMetaData.toString());
					}
					metaDataFieldList.add(fieldMetaData);
				}
				query.setColumnMetaData(metaDataFieldList);
			} catch (SQLException e) {
				if (log().isDebugEnabled()) {
					log().debug(e);
				}
				throw ExceptionHandler.getInstance().generateException(ExceptionConstants.SQL_GENERAL_ERROR, e.getMessage(), e);
			}
		}
	}

	public DatabaseQuery checkQuery(String queryName) throws BasicException {
		if (StringHelper.isEmpty(queryName)) {
			if (log().isDebugEnabled()) {
				log().debug("queryName vazio passado como parametro");
			}
			throw ExceptionHandler.getInstance().generateException(ExceptionConstants.SQL_INVALID_QUERY_NAME);
		}
		DatabaseQuery query = QueryHandler.getInstance().getQuery(queryName);
		if (query == null) {
			StringBuilder msg = new StringBuilder().append("Query Invalida ").append(queryName).append(" Verifique os XML");
			if (log().isDebugEnabled()) {
				log().debug(msg.toString());
			}
			throw ExceptionHandler.getInstance().generateException(ExceptionConstants.SQL_INVALID_QUERY_NAME, msg.toString());
		}
		return query;
	}

	public Set<String> getSpecialCriterias(String sql) {
		if (StringHelper.isEmpty(sql))
			return new HashSet<String>();
		Set<String> returnValue = new HashSet<String>();
		//Pattern p = Pattern.compile(":(.*?)(\\s|$|\\))");
		Pattern p = Pattern.compile(":(.*?)(\\s|$|\\))");
		Matcher m = p.matcher(sql);
		while (m.find()) {
			if (m.group(1).indexOf(":]") >= 0) continue;
			returnValue.add(m.group(1));
		}
		return returnValue;
	}

	public static void main(String[] args) {
		Set<String>  s = QueryHandler.getInstance().getSpecialCriterias("AND  ( A.REEMBOLSO = 'S' OR (A.REEMBOLSO = 'N' AND :contratoPrestador.id) )");
		System.out.println(StringHelper.collectionAsString(s));

		s = QueryHandler.getInstance().getSpecialCriterias(":p.01 and :p.02 SELECT NVL(MAX(CAST(REGEXP_REPLACE(S.CODIGO_SUBESTIPULANTE,'([^[:digit:]])*' ,'') AS NUMBER(20,0))),0) + 1 AS CODIGO where AND  ( A.REEMBOLSO = 'S' OR (A.REEMBOLSO = 'N' AND :contratoPrestador.id) ) :p03");
		System.out.println(StringHelper.collectionAsString(s));		
		
	}
}
