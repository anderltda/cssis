package br.com.cssis.foundation.query.impl;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import br.com.cssis.foundation.BasicObject;
import br.com.cssis.foundation.Constants;
import br.com.cssis.foundation.config.FrameworkConfiguration;
import br.com.cssis.foundation.util.SetHelper;
import br.com.cssis.foundation.util.StringHelper;

@SuppressWarnings("unchecked")
public class BufferedQueryRunnerLogger extends BasicObject {
	private static final long serialVersionUID = 1L;

	
	private HashMap buffer;

	private int reloadConfigFileTime = 60000; // 1 minuto
	private int flushThreesholdSize = 5; // de 200 em 200 entradas limpa
	private String destinationFile = "/home/borland/log-queries/log-query.log"; // arquivo de destino
	private boolean appendPreviousContent = true; // concatena no final ou apaga o arquivo
	private int timeForLogging = 2000; // tempo no qual devera logar se o tempo for superior
	private String defaultLogSeparator = ";"; // separador no log
	private String lineSeparator = "\n"; // separador no log
	private String dateFormat = "dd/MM/yyyy HH:mm:ss"; // formato da data
	private String logRotationDateFormat = "yyyyMMddHHmmss"; // formato da data
	private boolean enabled = false;
	private int logSizeRotation = 1024;
	private Date latestConfigReadingTime;


	private static BufferedQueryRunnerLogger instance;

	public static BufferedQueryRunnerLogger getInstance() {
		if (instance == null) {
			synchronized (BufferedQueryRunnerLogger.class) {
				instance = new BufferedQueryRunnerLogger();
			}
		}
		return instance;
	}

	private BufferedQueryRunnerLogger() {
		restart();
	}

	private synchronized void restart() {
		loadConfigFileValues();
		if (buffer != null) {
			buffer.clear();
		} else {
			buffer = new HashMap(flushThreesholdSize + flushThreesholdSize);
		}
		flush();
	}

	public synchronized void flush() {
		Iterator it = buffer.values().iterator();
		ArrayList keysToBeRemoved = new ArrayList();
		while (it.hasNext()) {
			StringBuilder currentLine = (StringBuilder) it.next();
			keysToBeRemoved.add(currentLine);
		}
		
		if (SetHelper.isEmpty(keysToBeRemoved)) return;
		
		try {
			File currentLog = new File(destinationFile);
			if (currentLog.length() > logSizeRotation) {
				// rotates the log
				SimpleDateFormat sd = new SimpleDateFormat(logRotationDateFormat);
				File rotatedLog = new File(currentLog.getParent() + "/" + currentLog.getName() + "." + sd.format(new Date()));
				currentLog.renameTo(rotatedLog);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		boolean errorIO = false;
		BufferedWriter out = null;
		try {
			out = new BufferedWriter(new FileWriter(destinationFile, appendPreviousContent));
		} catch (IOException e) {
			errorIO = true;
		}

		for (int i = 0; i < keysToBeRemoved.size(); i++) {
			StringBuilder currentLine = (StringBuilder) keysToBeRemoved.get(i);
			buffer.remove(currentLine);
			if (!errorIO) {
				try {
					out.write(currentLine.toString());
				} catch (IOException ex) {
					if (log().isErrorEnabled()) {
						log().error(ex);
					}
					errorIO = true;
				}
			}
		}

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

	private void loadConfigFileValues() {
		enabled = FrameworkConfiguration.getInstance().getBooleanValue(Constants.KEY_QUERY_LOGGER_ENABLED);

		int tmpInt = FrameworkConfiguration.getInstance().getIntValue(Constants.KEY_QUERY_LOGGER_RELOAD_TIME);
		if (tmpInt > 0) reloadConfigFileTime = tmpInt;

		tmpInt = FrameworkConfiguration.getInstance().getIntValue(Constants.KEY_QUERY_LOGGER_FLUSH_SIZE);
		if (tmpInt > 0) flushThreesholdSize = tmpInt;

		String tmpString = FrameworkConfiguration.getInstance().getStringValue(Constants.KEY_QUERY_LOGGER_TARGET_FILE);
		if (StringHelper.nonEmpty(tmpString)) destinationFile = tmpString;

		appendPreviousContent = FrameworkConfiguration.getInstance().getBooleanValue(Constants.KEY_QUERY_LOGGER_APP_PREVIOUS);		

		tmpInt = FrameworkConfiguration.getInstance().getIntValue(Constants.KEY_QUERY_LOGGER_TIME_LOG);
		if (tmpInt > 0) timeForLogging = tmpInt;

		tmpString = FrameworkConfiguration.getInstance().getStringValue(Constants.KEY_QUERY_LOGGER_LOG_SEPARATOR);
		if (StringHelper.nonEmpty(tmpString)) defaultLogSeparator = tmpString;

		tmpString = FrameworkConfiguration.getInstance().getStringValue(Constants.KEY_QUERY_LOGGER_LINE_SEPARATOR);
		if (StringHelper.nonEmpty(tmpString)) lineSeparator = tmpString;

		tmpString = FrameworkConfiguration.getInstance().getStringValue(Constants.KEY_QUERY_LOGGER_DATE_FORMAT);
		if (StringHelper.nonEmpty(tmpString)) dateFormat = tmpString;

		tmpString = FrameworkConfiguration.getInstance().getStringValue(Constants.KEY_QUERY_LOGGER_LOG_ROTATION_FORMAT);
		if (StringHelper.nonEmpty(tmpString)) logRotationDateFormat = tmpString;

		tmpInt = FrameworkConfiguration.getInstance().getIntValue(Constants.KEY_QUERY_LOGGER_LOG_ROTATION_SIZE);
		if (tmpInt > 0) logSizeRotation = tmpInt;
		
		latestConfigReadingTime = new Date();
	}

    private void checkConfigFile() {
        Date currentTime = new Date();
        if (currentTime.getTime() - latestConfigReadingTime.getTime() > reloadConfigFileTime) {
            loadConfigFileValues();
        }
    }

    public void addLog(String queryName, String sql, Date initTime, List<Object> wildCardValues) {
    	if (enabled && initTime != null) {
    		long executionTime = new Date().getTime() - initTime.getTime();
    		if (executionTime >= timeForLogging) {
	    		StringBuilder str = new StringBuilder();
	    		SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
	
	    		str.append(sdf.format(initTime)).append(defaultLogSeparator);
	    		str.append(queryName).append(defaultLogSeparator);
	    		str.append(sql.replaceAll("\\n", " ")).append(defaultLogSeparator);
	    		str.append(executionTime).append(defaultLogSeparator);
	    		str.append(printCollection(wildCardValues)).append(defaultLogSeparator);
	    		str.append(lineSeparator);

	    		buffer.put(str, str);
	    		if (buffer.size() >= flushThreesholdSize) {
	    			flush();
	    			checkConfigFile();
	    		}
    		}
    	}
    }
    
    
    private Object printCollection(List col) {
		if (col == null) return "";
		StringBuilder builder = new StringBuilder();
		builder.append("{");
		for (Object object : col) {
			builder.append(object.toString()).append(",");
		}
		StringHelper.removeLast(builder);
		builder.append("}");		
		return builder.toString();
	}

	public boolean isEnabled() {
        return enabled;
    }

    public int getTimeForLogging() {
        return timeForLogging;
    }      
}
