package br.com.cssis.foundation.query.impl;

import java.sql.Connection;
import java.sql.DriverManager;

import javax.sql.DataSource;

import br.com.cssis.foundation.BasicException;
import br.com.cssis.foundation.BasicObject;
import br.com.cssis.foundation.ExceptionConstants;
import br.com.cssis.foundation.ExceptionHandler;
import br.com.cssis.foundation.util.ServiceLocator;

public class ConnectionFactory extends BasicObject {
	private static final long serialVersionUID = 1L;

	private static ConnectionFactory instance;

	public static ConnectionFactory getInstance() {
		if (instance == null) {
			synchronized (ConnectionFactory.class) {
				instance = new ConnectionFactory();
			}
		}
		return instance;
	}

	private ConnectionFactory() {
		restart();
	}

	private synchronized void restart() {
	}

	public Connection getConnection() throws BasicException {
		Connection conn = null;
		try {
			DataSource ds = (DataSource) ServiceLocator.getInstance().getBean("oracleDataSource");
			conn = ds.getConnection();
		} catch (Exception e) {
			try {
				conn = DriverManager.getConnection("jdbc:oracle:thin:@10.2.0.108:1521:ODONTDEV", "odonto_sis", "odonto_sis123");
			} catch (Exception e1) {
				log().error("Erro ao carregar ConfiguracaoSistema. Constantes e Tratamento de Erro da Central estarão comprometidos. Verificar problema.");
				log().error(e);
				throw ExceptionHandler.getInstance().generateException(BasicException.class, e);
			}
		}
		return conn;
	}

	public Connection getConnection(String dataSourceName) throws BasicException {
		try {
			return getConnection();
		} catch (Exception e) {
			BasicException error = ExceptionHandler.getInstance().generateException(ExceptionConstants.SQL_ERROR_CONNECTION, e);
			if (log().isErrorEnabled()) {
				log().error(e);
			}
			throw error;
		}
	}

}
