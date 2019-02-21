package br.com.cssis.foundation.query.impl;


import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import oracle.jdbc.internal.OraclePreparedStatement;

import org.apache.commons.beanutils.ConvertUtils;

import br.com.cssis.foundation.BasicException;
import br.com.cssis.foundation.BasicObject;
import br.com.cssis.foundation.Constants;
import br.com.cssis.foundation.ExceptionConstants;
import br.com.cssis.foundation.ExceptionHandler;
import br.com.cssis.foundation.config.FrameworkConfiguration;
import br.com.cssis.foundation.query.DBTransporter;
import br.com.cssis.foundation.util.SetHelper;
import br.com.cssis.foundation.util.StringHelper;

@SuppressWarnings("unchecked")
public class JDBCQueryRunner extends BasicObject {
	protected static final Class DEFAULT_TRANSPORTER_CLASS = QueryResourceFactory.getDefaultTransporterClass();

	private static final long serialVersionUID = 1L;

	private static JDBCQueryRunner instance;
	protected int defaultRecordsToFetch;

	private boolean forceStatements;

	private JDBCQueryRunner() {
		restart();
	}

	private synchronized void restart() {
		defaultRecordsToFetch = FrameworkConfiguration.getInstance().getIntValue(Constants.KEY_QUERY_RUNNER_DEFAULT_COUNT);
		forceStatements = FrameworkConfiguration.getInstance().getBooleanValue(Constants.KEY_QUERY_RUNNER_FORCE_ST);
	}

	public static JDBCQueryRunner getInstance() {
		if (instance == null) {
			synchronized (JDBCQueryRunner.class) {
				instance = new JDBCQueryRunner();
			}
		}
		return instance;
	}

	public List executeQuery(String queryName, List<FilterCondition> criteriaList, List<SortingCondition> sortingList, int startPosition, int recordsToFetch, Connection conn) throws BasicException {
		// obtem a query
		return executeQuery(QueryHandler.getInstance().checkQuery(queryName), criteriaList, sortingList, startPosition, recordsToFetch, conn);
	}

	public List executeQuery(DatabaseQuery query, List<FilterCondition> criteriaList, List<SortingCondition> sortingList, int startPosition, int recordsToFetch, Connection conn) throws BasicException {
		// cria um wildCardValues
		List<Object> wildCardValues = null;
		if (!forceStatements) wildCardValues = new ArrayList<Object>(SetHelper.nonEmpty(criteriaList) ? criteriaList.size() : 0);

		// trata os criterios para o mapeamento correto
		query.handleConditionList(criteriaList);
		query.handleSortingList(sortingList);

		// gera o sql para ser executado
		String sqlToExecute = query.generateRepresentation(criteriaList, sortingList, wildCardValues);

		if (log().isDebugEnabled() || true) {
			log().info("Query : " + query.getName());
			log().info("SQL: " + sqlToExecute);
			log().info("Start : " + startPosition + " Records to Fetch : " + recordsToFetch);
			log().info("Values : \n" + StringHelper.collectionAsString(wildCardValues));
		}
		// prepara a execução, com log
		Date startTime = new Date();
		List returnValue = runJDBCQuery(sqlToExecute, conn, true, query, wildCardValues, false, startPosition, recordsToFetch, false, true);
		BufferedQueryRunnerLogger.getInstance().addLog(query.getName(), sqlToExecute, startTime, wildCardValues);

		// finalmente retorna o resultado
		return returnValue;
	}

	public int executeSqlStatement(Connection conn, String sql, List<String> paramNames, List<Object> paramValues) throws BasicException {
		if (conn == null)
			throw ExceptionHandler.getInstance().generateException(BasicException.class, ExceptionConstants.SQL_GENERAL_ERROR, "Conexão inválida(nula) com o BD.");
		try {
			conn.prepareStatement(sql);
		} catch (SQLException e1) {
			e1.printStackTrace();
			throw ExceptionHandler.getInstance().generateException(BasicException.class, ExceptionConstants.SQL_GENERAL_ERROR, "Conexão inválida(nula) com o BD.");

		}

		PreparedStatement st = null;
		try {
			st = getOraclePreparedStatement(conn.prepareStatement(sql));
			bindWildCardValues(st, paramNames, paramValues);

			if (log().isDebugEnabled() || true) {
				log().info("Sql Statement : " + sql);
				if (SetHelper.nonEmpty(paramNames)) {
					for (int i = 0; i < paramNames.size(); i++) {
						log().info(paramNames.get(i) + "=" + paramValues.get(i));
					}
				}
			}
			return st.executeUpdate();
		} catch (Exception e) {
			throw ExceptionHandler.getInstance().generateException(BasicException.class, e);
		} finally {
			if (st != null) {
				try {
					st.close();
				} catch (SQLException e) {
				}
			}
		}
	}

	public int getCount(String queryName, List<FilterCondition> criteriaList, Connection conn) throws BasicException {
		// obtem a query
		return getCount(QueryHandler.getInstance().checkQuery(queryName), criteriaList, conn);
	}

	public int getCount(DatabaseQuery query, List<FilterCondition> criteriaList, Connection conn) throws BasicException {
		// cria um wildCardValues
		List<Object> wildCardValues = null;
		if (!forceStatements) wildCardValues = new ArrayList<Object>(SetHelper.nonEmpty(criteriaList) ? criteriaList.size() : 0);

		// trata os criterios para o mapeamento correto
		query.handleConditionList(criteriaList);

		// gera o sql para ser executado
		String sqlToExecute = query.generateCountSQL(criteriaList, wildCardValues);

		if (log().isDebugEnabled() || true) {
			log().info("Query : " + query.getName());
			log().info("SQL Count: " + sqlToExecute);
			log().info("Values : \n" + StringHelper.collectionAsString(wildCardValues));
		}
		// prepara a execução, com log
		Date startTime = new Date();
		List<DBCountSQLTransporter> countList = (List<DBCountSQLTransporter>) runJDBCQuery(sqlToExecute, conn, true, query, wildCardValues, false, 1, 1, true, false);
		BufferedQueryRunnerLogger.getInstance().addLog(query.getName() + "(Count)", sqlToExecute, startTime, wildCardValues);

		// finalmente retorna o resultado
		int returnValue = -1;
		if (SetHelper.nonEmpty(countList)) returnValue = countList.get(0).getCount();

		return returnValue;
	}

	private List runJDBCQuery(String sqlToExecute, Connection conn, boolean b, DatabaseQuery query, List<Object> wildCardValues, boolean c, int startPosition, int recordsToFetch, boolean isCount, boolean extractMetaData) throws BasicException {
		// verifica valores necessarios para a execucao
		checkInput(sqlToExecute, conn);
		// check para os wildCards
		boolean usePreparedStatements = SetHelper.nonEmpty(wildCardValues);

		Statement st = null;
		ResultSet rs = null;
		PreparedStatement prepSt = null;
		List returnValue = null;
		try {
			if (usePreparedStatements) {
				prepSt = conn.prepareStatement(sqlToExecute);
				// fazer o bind com os wildCards
				bindWildCardValues(prepSt, wildCardValues);
				rs = prepSt.executeQuery();
			} else {
				st = conn.createStatement();
				rs = st.executeQuery(sqlToExecute);
			}
			// extrai os meta Dados se necessário - ie - somente a primeira vez
			if (extractMetaData == true && (query != null && query.isMetaDataEmpty())) {
				QueryHandler.getInstance().extractMetaData(rs.getMetaData(), query);
			}
			// finalmente faz o fetch dos dados
			returnValue = fetchData(rs, query, startPosition, recordsToFetch, isCount);
		} catch (Exception e) {
			if (log().isDebugEnabled() || true) {
				log().info("Erro ao Executar Método " + this.getClass().getSimpleName() + ".runJDBCQuery(String sqlToExecute, Connection conn, boolean b, DatabaseQuery query, List<Object> wildCardValues, boolean c, int startPosition, int recordsToFetch, boolean isCount, boolean extractMetaData)");
				log().info("sqlToExecute:" + sqlToExecute);
				log().info(e);
			}
			throw ExceptionHandler.getInstance().generateException(ExceptionConstants.SQL_EXECUTION_ERROR, e);
		} finally {
			freeSQLResources(rs, st, prepSt, null);
		}

		return returnValue;
	}

	private List fetchData(ResultSet rs, DatabaseQuery query, int startPosition, int recordsToFetch, boolean isCount) throws BasicException {
		// sempre existe um transporter, pelo menos o default
		List returnValue;
		try {
			// posiciona o resultSet
			if (startPosition <= 0) startPosition = 1;
			if (startPosition > 1) {
				for (int tmpStartPositionCounting = 1; tmpStartPositionCounting <= startPosition; tmpStartPositionCounting++) {
					rs.next();
				}
			}
			// verifica se é para trazer tudo
			boolean fetchAll = false;
			if (recordsToFetch < 0) {
				fetchAll = true;
			}

			// verfica se é o generic transporter
			boolean useGenericTransporter = false;
			List<String> fieldNames = null;

			if (!isCount) {
				if (query.getTransporterClass().equals(GenericDBTransporter.class)) {
					useGenericTransporter = true;
					fieldNames = query.getColumnNameList();
					returnValue = (List<GenericDBTransporter>) new ArrayList();
				} else {
					returnValue = new ArrayList();
				}
			} else {
				returnValue = (List<DBCountSQLTransporter>) new ArrayList();
			}

			int i = 0;
			while (rs.next() && (fetchAll || (i < recordsToFetch))) {
				if (isCount) {
					DBCountSQLTransporter transporter = new DBCountSQLTransporter();
					((DBCountSQLTransporter) transporter).populate(rs);
					returnValue.add(transporter);
				} else {
					Object transporter = query.getTransporterClass().newInstance();
					if (useGenericTransporter) {
						DBTransporter concreteTransporter = (DBTransporter) transporter;
						if (SetHelper.nonEmpty(query.getColumnNameList())) concreteTransporter.setFieldList(fieldNames);
						concreteTransporter.populate(rs);
					} else {
						populateByReflection(rs, transporter, query.getColumnMetaData());
					}
					returnValue.add(transporter);
				}
				i++;
			}
		} catch (Exception e) {
			if (log().isDebugEnabled() || true) {
				log().info(e);
			}
			throw ExceptionHandler.getInstance().generateException(ExceptionConstants.SQL_EXECUTION_ERROR, e.getMessage(), e);
		}

		return returnValue;
	}

	private void populateByReflection(ResultSet rs, Object transporter, List<QueryFieldMetaData> columnMetaDataList) throws SQLException {
		for (QueryFieldMetaData columnMetaData : columnMetaDataList) {
			Object value = rs.getObject(columnMetaData.getLabel());
			boolean usingPrimitive = false;
			try {
				Method m = null;
				try {
					m = transporter.getClass().getMethod(columnMetaData.getSetterName(), columnMetaData.getJavaType());
				} catch (Exception e) {
					try {
						// tratamento para buscar os primitivos
						if (m == null && columnMetaData.getPrimitiveJavaType() != null) {
							// tentar buscar com o primitivo deste
							m = transporter.getClass().getDeclaredMethod(columnMetaData.getSetterName(), columnMetaData.getPrimitiveJavaType());
							usingPrimitive = true;
						}
					} catch (Exception e1) {
						throw e;
					}
				}
				if (value != null) {
					if (!usingPrimitive) {
						m.invoke(transporter, ConvertUtils.convert(value, columnMetaData.getJavaType()));
					} else {
						m.invoke(transporter, ConvertUtils.convert(value, columnMetaData.getPrimitiveJavaType()));
					}
				} else {
					// não pode invocar primitivos com nulo, o autobox faia
					if (!usingPrimitive) m.invoke(transporter, value);
				}
			} catch (Exception e) {
				log().info("Erro ao popular Transporter : " + transporter.getClass().getName());
				log().info("Método : " + columnMetaData.getSetterName() + " com parametro : " + columnMetaData.getJavaType() + " não encontrado");
				log().info(e);
			}
		}
	}

	private void checkInput(String sql, Connection conn) throws BasicException {
		if (StringHelper.isEmpty(sql)) {
			if (log().isDebugEnabled() || true) {
				log().info("SQL Vazio passado como parametro");
			}
			throw ExceptionHandler.getInstance().generateException(ExceptionConstants.SQL_INVALID_SQL_TEXT);
		}

		if (conn == null) {
			if (log().isDebugEnabled() || true) {
				log().info("Conexao invalida. Sql passado: " + sql);
			}
			throw ExceptionHandler.getInstance().generateException(ExceptionConstants.SQL_INVALID_CONNECTION);
		}
	}

	private void freeSQLResources(ResultSet rs, Statement st, PreparedStatement prepSt, Connection conn) {
		try {
			if (rs != null) rs.close();
		} catch (SQLException e) {
			if (log().isDebugEnabled() || true) {
				log().info(e);
			}
		}

		try {
			if (st != null) st.close();
		} catch (SQLException e) {
			if (log().isDebugEnabled() || true) {
				log().info(e);
			}
		}

		try {
			if (prepSt != null) prepSt.close();
		} catch (SQLException e) {
			if (log().isDebugEnabled() || true) {
				log().info(e);
			}
		}

		try {
			if (conn != null) conn.close();
		} catch (SQLException e) {
			if (log().isDebugEnabled() || true) {
				log().info(e);
			}
		}
	}
	
	private OraclePreparedStatement getOraclePreparedStatement(PreparedStatement prepSt) {
//		if (prepSt instanceof OraclePreparedStatement) {
			return (OraclePreparedStatement) prepSt;
//		} else {
//			DelegatingPreparedStatement preparedStatement = (DelegatingPreparedStatement) prepSt;
//			return (OraclePreparedStatement) preparedStatement.getInnermostDelegate();
//		
//		}
	}

	public void bindWildCardValues(PreparedStatement prepSt, List<Object> wildCardValues) throws BasicException {
		if (SetHelper.isEmpty(wildCardValues))
			return;

		OraclePreparedStatement oraclePS = getOraclePreparedStatement(prepSt);

		for (int i = 0; i < wildCardValues.size(); i++) {
			try {
				BindValuesHelper.assignByName(oraclePS, i + 1, wildCardValues.get(i));
			} catch (SQLException e) {
				StringBuilder msg = new StringBuilder();
				msg.append("Erro ao Fazer o bind dos Parametros. Index: ").append(i).append(" Valor:").append(wildCardValues.get(i));
				if (log().isErrorEnabled()) {
					log().error(msg.toString());
					log().error(e);
				}
				throw ExceptionHandler.getInstance().generateException(ExceptionConstants.SQL_BIND_ERROR, msg.toString(), e);
			}
		}
	}

	public void bindWildCardValues(PreparedStatement prepSt, List<String> paramNames, List<Object> wildCardValues) throws BasicException {
		if (SetHelper.isEmpty(wildCardValues)) return;

		if (paramNames.size() != wildCardValues.size()) {
			throw ExceptionHandler.getInstance().generateException(BasicException.class, ExceptionConstants.SQL_BIND_ERROR, "Erro ao fazer o bind por nomes. Tamanho do arrays de nomes diferente do de valores");
		}

		for (int i = 0; i < wildCardValues.size(); i++) {
			try {
				OraclePreparedStatement oraclePS = (OraclePreparedStatement) prepSt;
				BindValuesHelper.assignByName(oraclePS, paramNames.get(i), wildCardValues.get(i));
			} catch (SQLException e) {
				StringBuilder msg = new StringBuilder();
				msg.append("Erro ao Fazer o bind dos Parametros. Index: ").append(i).append(" Valor:").append(wildCardValues.get(i));
				if (log().isErrorEnabled()) {
					log().error(msg.toString());
					log().error(e);
				}
				throw ExceptionHandler.getInstance().generateException(ExceptionConstants.SQL_BIND_ERROR, msg.toString(), e);
			}
		}
	}

	public void generateDTOClass(String queryName, String className, String packageName) {
		String classBoby = generateDTOClassBody(queryName, className, packageName);
		BufferedWriter out = null;
		try {
			out = new BufferedWriter(new FileWriter("/temp/" + className + ".java", false));
			out.write(classBoby);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (out != null) {
				try {
					out.flush();
				} catch (IOException ex1) {
				}
				try {
					out.close();
				} catch (IOException ex2) {
				}
			}
		}
	}

	public String generateDTOClassBody(String queryName, String className, String packageName) {
		Connection conn = null;
		try {
			DatabaseQuery query = QueryHandler.getInstance().getQuery(queryName);
			conn = ConnectionFactory.getInstance().getConnection();
			try {
				executeQuery(queryName, null, null, 1, 1, conn);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return generateClassBody(query, className, packageName);
		} catch (BasicException e) {
			e.printStackTrace();
		} finally {
			if (conn != null) try {
				conn.close();
			} catch (SQLException e) {
			}
		}

		return null;
	}

	private String generateClassBody(DatabaseQuery query, String className, String packageName) {
		StringBuilder builder = new StringBuilder();

		builder.append("package ").append(packageName).append(";\n\n");
		builder.append("import br.com.tempopar.foundation.BasicObject;").append("\n");
		builder.append("import br.com.tempopar.foundation.BaseDTO;").append("\n");
		builder.append("import br.com.tempopar.foundation.util.ReflectionUtil;").append("\n");

		builder.append("\n");

		builder.append("public class ").append(className).append(" extends BasicObject implements BaseDTO {").append("\n");

		builder.append("\n");
		builder.append("\tprivate static final long serialVersionUID = 1L;");
		builder.append("\n");
		for (QueryFieldMetaData field : query.getColumnMetaData()) {
			builder.append("\t").append("protected ").append(field.getJavaType().getSimpleName()).append(" ").append(field.getPropertyName()).append(";\n");
		}
		builder.append("\n");
		for (QueryFieldMetaData field : query.getColumnMetaData()) {
			builder.append("\t").append("public ").append(field.getJavaType().getSimpleName()).append(" ").append(field.getGetterName()).append("()").append(" {\n").append("\t\treturn ").append(field.getPropertyName()).append(";\n").append("\t}\n");

			builder.append("\t").append("public ").append("void ").append(field.getSetterName()).append("(").append(field.getJavaType().getSimpleName()).append(" ").append(field.getPropertyName()).append(")").append(" {\n").append("\t\tthis.").append(field.getPropertyName()).append(" = ").append(field.getPropertyName()).append(";\n").append("\t}\n");

			builder.append("\n");
		}

		builder.append("\t@Override\n").append("\tpublic String toString() {\n").append("\t\treturn ReflectionUtil.getFieldListAndValues(this)").append(";\n").append("\t}\n");
		builder.append("}\n");
		return builder.toString();
	}

	public static void main(String[] args) {
		// JDBCQueryRunner.getInstance().generateDTOClass("TransacoesAutorizadasUsuario",
		// "TransacaoUsuarioDTO", "br.com.tempopar.seguranca.dto");
		// JDBCQueryRunner.getInstance().generateDTOClass("ConstraintList",
		// "ConstraintVO", "br.com.tempopar.foundation.util");
		JDBCQueryRunner.getInstance().generateDTOClass("Cadastro.BuscaSegurado", "SeguradoDTO", "br.com.tempopar.odonto.cadastro.dto");
	}

}