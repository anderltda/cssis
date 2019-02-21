package br.com.cssis.foundation.query.impl;

import java.sql.Connection;

import br.com.cssis.foundation.BasicException;
import br.com.cssis.foundation.BasicObject;
import br.com.cssis.foundation.Constants;
import br.com.cssis.foundation.config.FrameworkConfiguration;
import br.com.cssis.foundation.query.DBTransporter;
import br.com.cssis.foundation.util.StringHelper;

@SuppressWarnings("unchecked")
public class QueryResourceFactory extends BasicObject {
	private static final long serialVersionUID = 1L;
	public static final String defaultTransporterClassName = FrameworkConfiguration.getInstance().getStringValue(Constants.KEY_QUERY_RUNNER_DEFAULT_TRANSPORTER);

	public static Connection getConnection(String dataSourceName) throws BasicException {
		return ConnectionFactory.getInstance().getConnection();
	}
	
	public static DBTransporter getTransporter(String transporterClassName) {
		if (StringHelper.isEmpty(transporterClassName)) {
			return new GenericDBTransporter();
		}
		else {
			try {
				Class<DBTransporter> newTransporterClass = (Class<DBTransporter>) Class.forName(transporterClassName);
				return newTransporterClass.newInstance();
			} catch (Exception e) {			
				// tratamento padrao
				GenericDBTransporter returnValue = new GenericDBTransporter();
				if (returnValue.log().isErrorEnabled()) {
					returnValue.log().error("Erro tentando criar transporter: " + transporterClassName);
					returnValue.log().error("Usando padrao GenericDBTransporter. Origem do Erro:");
					returnValue.log().error(e);
				}
				return returnValue;
			}
			
		}		
	}
		
	public static Class<DBTransporter> getTransporterClass(String transporterClassName) {
		if (StringHelper.isEmpty(transporterClassName)) {
			return getDefaultTransporterClass();
		}
		else {
			try {
				Class<DBTransporter> newTransporterClass = (Class<DBTransporter>) Class.forName(transporterClassName);
				return newTransporterClass;
			} catch (Exception e) {			
				// tratamento padrao
				BasicObject tmp = new BasicObject();
				if (tmp.log().isErrorEnabled()) {
					tmp.log().error("Erro tentando criar transporter: " + transporterClassName);
					tmp.log().error(e);
				}
				return getDefaultTransporterClass();
			}			
		}				
	}
	
	public static Class getDefaultTransporterClass() {
		try {
			return Class.forName(defaultTransporterClassName);
		} catch (Exception e) {
			GenericDBTransporter returnValue = new GenericDBTransporter();
			if (returnValue.log().isErrorEnabled()) {
				returnValue.log().debug(e);
			}
			return returnValue.getClass();
		}
	}
		
}
