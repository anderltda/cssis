package br.com.cssis.foundation.config;

import java.io.File;
import java.net.URL;
import java.util.Iterator;
import java.util.Properties;

import org.apache.commons.configuration.AbstractConfiguration;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.lang.StringUtils;

import br.com.cssis.foundation.BasicObject;
import br.com.cssis.foundation.util.BooleanHelper;
import br.com.cssis.foundation.util.StringHelper;

public class FrameworkConfiguration extends BasicObject {
	private static final long serialVersionUID = 1L;
	private static final String DEFAULT_STRING_VALUE = "";
	private static final long DEFAULT_LONG_VALUE = 0L;
	private static final int DEFAULT_INT_VALUE = 0;
	private static final String DEFAULT_BOOLEAN_VALUE = "F";
	private static final String BOOT_STRAP_CONFIG_FILE_NAME = "/framework_config.properties";
	private static final String BOOT_STRAP_NAME = "framework.config";

	private static FrameworkConfiguration instance;
	private boolean enabled = false;
	private Configuration configuration;

	private FrameworkConfiguration() {
		restart();
	}

	@SuppressWarnings("unchecked")
	private synchronized void restart() {
		// try to get a -D that points to the file
		String fileName = System.getProperty(BOOT_STRAP_NAME);
		if (StringHelper.isEmpty(fileName)) {
			fileName = BOOT_STRAP_CONFIG_FILE_NAME;
		}
		try {
			
			Character delimiter = AbstractConfiguration.getDefaultListDelimiter();
			AbstractConfiguration.setDefaultListDelimiter('\0');
			String odontoConfigHome = System.getenv("ODONTO_CONFIG_HOME");
			if (StringUtils.isEmpty(odontoConfigHome)) {
				log().warn("Variavel de ambiente 'ODONTO_CONFIG_HOME' não foi configurada.");
			} else {
				log().debug("ODONTO_CONFIG_HOME: " + odontoConfigHome);
			}
			File filePropertiesExterno = new File((odontoConfigHome != null ? odontoConfigHome : "") + File.separator + fileName);
			
			URL url = this.getClass().getResource(fileName);
			configuration = new PropertiesConfiguration(url);

			if (!StringUtils.isEmpty(odontoConfigHome) && filePropertiesExterno.exists() && configuration == null) {
				configuration = new PropertiesConfiguration(filePropertiesExterno);
			} 
			
			if (configuration.isEmpty()) {
				log().error("Arquivo de Configuração Vazio");
			}
			enabled = true;
			if (log().isDebugEnabled()) {
				Iterator<Object> it = (Iterator<Object>)configuration.getKeys();
				while (it.hasNext()) {
					Object o = it.next();
					log().debug(o + "=" + configuration.getString(o.toString()));
				}
			}
			AbstractConfiguration.setDefaultListDelimiter(delimiter);
		} catch (ConfigurationException e) {
			log().error("Erro ao ler arquivos de configuraçãoes. Valores Default serão utilizados");
			log().error(e);
		}
	}

	public static FrameworkConfiguration getInstance() {
		if (instance == null) {
			instance = new FrameworkConfiguration();
		}
		return instance;
	}

	public static void main(String[] args) {
		FrameworkConfiguration.getInstance();
	}

	public String getStringValue(String propertyName) {
		return enabled ? configuration.getString(propertyName, DEFAULT_STRING_VALUE) : DEFAULT_STRING_VALUE;
	}

	public int getIntValue(String propertyName) {
		return enabled ? configuration.getInt(propertyName, DEFAULT_INT_VALUE) : DEFAULT_INT_VALUE;
	}

	public long getLongValue(String propertyName) {
		return enabled ? configuration.getLong(propertyName, DEFAULT_LONG_VALUE) : DEFAULT_LONG_VALUE;
	}

	public boolean getBooleanValue(String propertyName) {
		if (enabled) {
			String tmpValue = configuration.getString(propertyName, DEFAULT_BOOLEAN_VALUE);
			return BooleanHelper.booleanValue(tmpValue);
		} else
			return BooleanHelper.booleanValue(DEFAULT_BOOLEAN_VALUE);
	}

	public Properties getProperties(String propertyName) {
		return enabled ? configuration.getProperties(propertyName) : new Properties();
	}

	public String[] getStringArray(String propertyName) {
		return enabled ? configuration.getStringArray(propertyName) : null;
	}

}
