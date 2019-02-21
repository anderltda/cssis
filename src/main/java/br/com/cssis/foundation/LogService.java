package br.com.cssis.foundation;

import java.util.HashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class LogService implements java.io.Serializable {
	private static final long serialVersionUID = 1L;

	private HashMap<String, Log> logMap;

	private static LogService instance;

	public static LogService getInstance() {
		if (instance == null) {
			instance = new LogService();
		}
		return instance;
	}

	private void restart() {
		logMap = new HashMap<String, Log>();
	}

	private LogService() {
		restart();
	}

	public Log getLogger(String className) {
		Log logger = logMap.get(className);
		if (logger == null) {
			synchronized (instance) {
				logger = LogFactory.getLog(className);
				logMap.put(className, logger);
			}
		}
		return logger;
	}
}