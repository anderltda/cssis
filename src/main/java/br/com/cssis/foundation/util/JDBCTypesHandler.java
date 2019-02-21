package br.com.cssis.foundation.util;

import java.net.URL;
import java.sql.Connection;
import java.sql.Types;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.SubnodeConfiguration;
import org.apache.commons.configuration.XMLConfiguration;

import br.com.cssis.foundation.BasicObject;
import br.com.cssis.foundation.Constants;
import br.com.cssis.foundation.config.FrameworkConfiguration;
import br.com.cssis.foundation.query.impl.ConnectionFactory;
import br.com.cssis.foundation.query.impl.CustomMappingDefinition;
import br.com.cssis.foundation.query.impl.JDBCQueryRunner;

@SuppressWarnings("unchecked")
public class JDBCTypesHandler extends BasicObject {
	public static final int DEFAULT_NULL_PRECISION = -1;
	public static final int DEFAULT_NULL_SCALE = -1;
	public static final int DEFAULT_NULL_LENGTH = -1;

	private static final long serialVersionUID = 1L;

	public static Map<String, Class> primitiveTypes;
	public static Map<String, Integer> jdbcNameTypes;

	static {
		primitiveTypes = new HashMap<String, Class>(7);
		primitiveTypes.put("boolean", boolean.class);
		primitiveTypes.put("byte", byte.class);
		primitiveTypes.put("short", short.class);
		primitiveTypes.put("int", int.class);
		primitiveTypes.put("long", long.class);
		primitiveTypes.put("float", float.class);
		primitiveTypes.put("double", double.class);

		jdbcNameTypes = new HashMap<String, Integer>(21);
		jdbcNameTypes.put("ARRAY", Types.ARRAY);
		jdbcNameTypes.put("BIGINT", Types.BIGINT);
		jdbcNameTypes.put("BINARY", Types.BINARY);
		jdbcNameTypes.put("BIT", Types.BIT);
		jdbcNameTypes.put("BLOB", Types.BLOB);
		jdbcNameTypes.put("BOOLEAN", Types.BOOLEAN);
		jdbcNameTypes.put("CHAR", Types.CHAR);
		jdbcNameTypes.put("CLOB", Types.CLOB);
		jdbcNameTypes.put("DATALINK", Types.DATALINK);
		jdbcNameTypes.put("DATE", Types.DATE);
		jdbcNameTypes.put("DECIMAL", Types.DECIMAL);
		jdbcNameTypes.put("DISTINCT", Types.DISTINCT);
		jdbcNameTypes.put("DOUBLE", Types.DOUBLE);
		jdbcNameTypes.put("FLOAT", Types.FLOAT);
		jdbcNameTypes.put("INTEGER", Types.INTEGER);
		jdbcNameTypes.put("JAVA_OBJECT", Types.JAVA_OBJECT);
		jdbcNameTypes.put("LONGVARBINARY", Types.LONGVARBINARY);
		jdbcNameTypes.put("LONGVARCHAR", Types.LONGVARCHAR);
		jdbcNameTypes.put("NULL", Types.NULL);
		jdbcNameTypes.put("NUMERIC", Types.NUMERIC);
		jdbcNameTypes.put("OTHER", Types.OTHER);
		jdbcNameTypes.put("REAL", Types.REAL);
		jdbcNameTypes.put("REF", Types.REF);
		jdbcNameTypes.put("SMALLINT", Types.SMALLINT);
		jdbcNameTypes.put("STRUCT", Types.STRUCT);
		jdbcNameTypes.put("TIME", Types.TIME);
		jdbcNameTypes.put("TIMESTAMP", Types.TIMESTAMP);
		jdbcNameTypes.put("TINYINT", Types.TINYINT);
		jdbcNameTypes.put("VARBINARY", Types.VARBINARY);
		jdbcNameTypes.put("VARCHAR", Types.VARCHAR);
	}

	private static JDBCTypesHandler instance = new JDBCTypesHandler();

	public Map<Integer, CustomMappingDefinition> standardJavaConversion;
	public Map<Integer, CustomMappingDefinition> customJavaDefinitions;

	private JDBCTypesHandler() {
		restart();
	}

	private synchronized void restart() {
		initStandardTypes();
		initCustomTypes();
	}

	private void initStandardTypes() {
		if (SetHelper.nonEmpty(standardJavaConversion)) {
			standardJavaConversion.clear();
		} else {
			standardJavaConversion = new HashMap<Integer, CustomMappingDefinition>();
		}
		String standardFileDefintions = FrameworkConfiguration.getInstance().getStringValue(Constants.KEY_QUERY_STANDARD_TYPES_MAPPING);

		XMLConfiguration config = null;
		try {
			URL url = this.getClass().getResource(standardFileDefintions);
			config = new XMLConfiguration(url);
			if (config.isEmpty()) {
				if (log().isErrorEnabled()) {
					log().error("EmptystandardJavaConversion File : " + standardFileDefintions);
				}
				return;
			}
			if (log().isDebugEnabled()) {
				log().debug("Parsing standardJavaConversion File : " + standardFileDefintions);
			}
		} catch (ConfigurationException e) {
			if (log().isErrorEnabled()) {
				log().error("Arquivo de Tratamento de standardJavaConversion List : " + standardFileDefintions);
				log().error(e);
			}
			return;
		}

		List<SubnodeConfiguration> sqlTypeList = config.configurationsAt("sql-type");
		for (SubnodeConfiguration node : sqlTypeList) {
			String javaType = node.getString("[@java-type]");
			String jdbcType = node.getString("[@jdbc-type]");

			String tmp = node.getString("[@precision]");
			int precision = DEFAULT_NULL_PRECISION;
			if (StringHelper.nonEmpty(tmp)) {
				precision = NumberHelper.intValue(tmp);
			}
			tmp = node.getString("[@scale]");
			int scale = DEFAULT_NULL_SCALE;
			if (StringHelper.nonEmpty(tmp)) {
				scale = NumberHelper.intValue(tmp);
			}

			tmp = node.getString("[@length]");
			int length = DEFAULT_NULL_LENGTH;
			if (StringHelper.nonEmpty(tmp)) {
				length = NumberHelper.intValue(tmp);
			}

			if (jdbcNameTypes.get(jdbcType) == null) {
				log().error("Tipo JDBC passado inválido : " + jdbcType);
				continue;
			}

			if (jdbcNameTypes.get(jdbcType) == null) {
				log().error("Tipo JDBC passado inválido : " + jdbcType);
				continue;
			}

			Class concreteJavaTypeClass = checkJavaType(javaType);
			if (concreteJavaTypeClass != null) {
				CustomMappingDefinition cDef = new CustomMappingDefinition(scale, precision, jdbcNameTypes.get(jdbcType).intValue(), length, concreteJavaTypeClass);
				standardJavaConversion.put(cDef.hashCode(), cDef);
			} else {
				log().error("Tipo Java passado inválido : " + javaType);
			}
		}

	}

	private void initCustomTypes() {
		if (SetHelper.nonEmpty(customJavaDefinitions)) {
			customJavaDefinitions.clear();
		} else {
			customJavaDefinitions = new HashMap<Integer, CustomMappingDefinition>();
		}
		String customFileDefintions = FrameworkConfiguration.getInstance().getStringValue(Constants.KEY_QUERY_CUSTOM_TYPES_MAPPING);
		XMLConfiguration config = null;
		try {
			URL url = this.getClass().getResource(customFileDefintions);
			config = new XMLConfiguration(url);
			if (config.isEmpty()) {
				if (log().isErrorEnabled()) {
					log().error("Empty customJavaDefinitions : " + customFileDefintions);
				}
				return;
			}
			if (log().isDebugEnabled()) {
				log().debug("Parsing customJavaDefinitions : " + customFileDefintions);
			}
		} catch (ConfigurationException e) {
			if (log().isErrorEnabled()) {
				log().error("Arquivo de Tratamento de customJavaDefinitions : " + customFileDefintions);
				log().error(e);
			}
			return;
		}

		List<SubnodeConfiguration> sqlTypeList = config.configurationsAt("sql-type");
		for (SubnodeConfiguration node : sqlTypeList) {
			String javaType = node.getString("[@java-type]");
			String jdbcType = node.getString("[@jdbc-type]");
			String tmp = node.getString("[@precision]");
			int precision = DEFAULT_NULL_PRECISION;
			if (StringHelper.nonEmpty(tmp)) {
				precision = NumberHelper.intValue(tmp);
			}
			tmp = node.getString("[@scale]");
			int scale = DEFAULT_NULL_SCALE;
			if (StringHelper.nonEmpty(tmp)) {
				scale = NumberHelper.intValue(tmp);
			}

			tmp = node.getString("[@length]");
			int length = DEFAULT_NULL_LENGTH;
			if (StringHelper.nonEmpty(tmp)) {
				length = NumberHelper.intValue(tmp);
			}

			if (jdbcNameTypes.get(jdbcType) == null) {
				log().error("Tipo JDBC passado inválido : " + jdbcType);
				continue;
			}

			Class concreteJavaTypeClass = checkJavaType(javaType);
			if (concreteJavaTypeClass == null) {
				log().error("Tipo Java passado inválido : " + javaType);
				continue;
			}

			CustomMappingDefinition cDef = new CustomMappingDefinition(scale, precision, jdbcNameTypes.get(jdbcType).intValue(), length, concreteJavaTypeClass);
			customJavaDefinitions.put(cDef.hashCode(), cDef);
		}

	}

	public Class getJavaType(int jdbcType, int precision, int scale, int length) {
		Class returnValue = getScalarJavaType(jdbcType, precision, scale, length);
		if (returnValue == null) {
			return getScalarJavaType(jdbcType, precision, scale, DEFAULT_NULL_LENGTH);
		} else return returnValue;
	}

	private Class getScalarJavaType(int jdbcType, int precision, int scale, int length) {
		CustomMappingDefinition cDef = new CustomMappingDefinition();
		cDef.setJdbcType(jdbcType);
		cDef.setPrecision(precision);
		cDef.setScale(scale);
		cDef.setLength(length);

		CustomMappingDefinition found = searchMaps(cDef);
		if (found != null) {
			return found.getJavaType();
		}

		cDef.setScale(DEFAULT_NULL_SCALE);
		found = searchMaps(cDef);
		if (found != null) {
			return found.getJavaType();
		}
		cDef.setScale(scale); // restaura o valor original e agora troca na busca

		cDef.setPrecision(DEFAULT_NULL_PRECISION);
		found = searchMaps(cDef);
		if (found != null) {
			return found.getJavaType();
		}

		// agora tenta com todos default (para valores escalar)
		cDef.setScale(DEFAULT_NULL_SCALE);
		found = searchMaps(cDef);
		if (found != null) {
			return found.getJavaType();
		}

		// finalmente tenta com length e ai nao liga para scale e precision

		return null;
	}

	private CustomMappingDefinition searchMaps(CustomMappingDefinition cDef) {
		CustomMappingDefinition found = customJavaDefinitions.get(cDef.hashCode());
		if (found != null) {
			return found;
		}

		found = standardJavaConversion.get(cDef.hashCode());
		if (found != null) {
			return found;
		}
		return null;
	}

	private Class checkJavaType(String javaType) {
		try {
			return Class.forName(javaType);
		} catch (Exception e) {
			Class returnValue = primitiveTypes.get(javaType);
			if (returnValue != null) return returnValue;
			log().error("checkJavaType:Invalid Type : " + javaType);
		}
		return null;
	}

	public static JDBCTypesHandler getInstance() {
		return instance;
	}

	public static void main(String[] args) {
		/*
		 * JDBCTypesHandler h = JDBCTypesHandler.getInstance();
		 * System.out.println
		 * (StringHelper.mapAsString(h.customJavaDefinitions));
		 * System.out.println
		 * (StringHelper.mapAsString(h.standardJavaConversion));
		 * 
		 * System.out.println(
		 * h.getJavaType(JDBCTypesHandler.jdbcNameTypes.get("NUMERIC"
		 * ).intValue(), 1, 0, 22));
		 * System.out.println(h.getJavaType(JDBCTypesHandler
		 * .jdbcNameTypes.get("NUMERIC").intValue(), 5, 0, 11));
		 * System.out.println
		 * (h.getJavaType(JDBCTypesHandler.jdbcNameTypes.get("NUMERIC"
		 * ).intValue(), 5, 2, 22));
		 * System.out.println(h.getJavaType(JDBCTypesHandler
		 * .jdbcNameTypes.get("VARCHAR").intValue(), 0, 0, 100));
		 * System.out.println
		 * (h.getJavaType(JDBCTypesHandler.jdbcNameTypes.get("CHAR").intValue(),
		 * 0, 0, 1));
		 * 
		 * try { Connection c = ConnectionFactory.getInstance().getConnection();
		 * Statement st = c.createStatement(); ResultSet rs =
		 * st.executeQuery("SELECT * FROM USUARIO"); ResultSetMetaData metaData
		 * = rs.getMetaData(); metaData.getColumnCount(); rs.close();
		 * st.close(); c.close();
		 * 
		 * } catch (Exception e) { // TODO Auto-generated catch block
		 * e.printStackTrace(); }
		 */

		try {
			Connection c = ConnectionFactory.getInstance().getConnection();
			Object result = JDBCQueryRunner.getInstance().executeQuery("QueryGrupo", null, null, 1, 10, c);

			System.out.println(StringHelper.collectionAsString((Collection) result));

			int valor = JDBCQueryRunner.getInstance().getCount("QueryGrupo", null, c);
			System.out.println("Contador ==>" + valor);

			c.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
