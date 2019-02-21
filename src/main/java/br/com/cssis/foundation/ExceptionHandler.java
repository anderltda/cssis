package br.com.cssis.foundation;

import java.lang.reflect.Constructor;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.persistence.OptimisticLockException;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.hibernate.JDBCException;
import org.hibernate.PropertyValueException;
import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.exception.GenericJDBCException;
import org.hibernate.id.IdentifierGenerationException;
import org.springframework.orm.hibernate3.HibernateJdbcException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.transaction.TransactionSystemException;

import com.novell.ldap.LDAPException;

import br.com.cssis.foundation.config.FrameworkConfiguration;
import br.com.cssis.foundation.service.PersistenceService;
import br.com.cssis.foundation.service.ServiceException;
import br.com.cssis.foundation.util.ColumnMessageUtil;
import br.com.cssis.foundation.util.NumberHelper;
import br.com.cssis.foundation.util.ReflectionUtil;
import br.com.cssis.foundation.util.ServiceLocator;
import br.com.cssis.foundation.util.StringHelper;
import br.com.cssis.webservice.exception.WSException;
import br.com.cssis.webservice.result.StatusResult;
import br.com.cssis.webservice.result.StatusResult.Status;

@SuppressWarnings("unchecked")
public class ExceptionHandler extends BasicObject {
	private static final long serialVersionUID = 1L;

	private static ExceptionHandler instance = new ExceptionHandler();

	private boolean enabled = false;
	private Map<Long, ErrorMessage> errorMap;
	private Map<String, TratamentoErroConstraint> constraintMap;

	public static ExceptionHandler getInstance() {
		return instance;
	}

	private ExceptionHandler() {
		restart();
	}

	private synchronized void restart() {
		if (errorMap == null) {
			errorMap = new HashMap<Long, ErrorMessage>();
		} else {
			errorMap.clear();
		}
		enabled = FrameworkConfiguration.getInstance().getBooleanValue(Constants.KEY_EXCEPTION_HANDLER);
		log().info("Reloading Exception Handler");
		if (enabled) {
			String inputList[] = FrameworkConfiguration.getInstance().getStringArray(Constants.KEY_EXCEPTION_HANDLER_INPUT);
			if (inputList.length > 0) {
				for (String input : inputList) {
					if (StringHelper.nonEmpty(input)) {
						log().info(input);
						loadExceptionList(input);
					}
				}
			}
			constraintMap = loadConstraintsErrorMapping();
		} else {
			log().info("Exception Handler not enabled");
		}
	}

	private void loadExceptionList(String input) {
		XMLConfiguration config = null;
		try {
			URL url = this.getClass().getResource(input);
			config = new XMLConfiguration(url);
			if (config.isEmpty()) {
				if (log().isErrorEnabled()) {
					log().error("Arquivo de Tratamento de Erros Vazio : " + input);
				}
				return;
			}
			if (log().isDebugEnabled()) {
				log().debug("Parsing exception file : " + input);
			}
		} catch (ConfigurationException e) {
			if (log().isErrorEnabled()) {
				log().error("Arquivo de Tratamento de Erros : " + input);
				log().error(e);
			}
			return;
		}
		int i = 0;
		while (true) {
			try {
				HierarchicalConfiguration sub = config.configurationAt("exception(" + i + ")");
				String id = sub.getString("id");
				String code = sub.getString("code");
				ErrorMessage error = new ErrorMessage(code);
				error.setErrorCode(code);
				error.setMessage(sub.getString("msg"));
				error.setExtraInformation(sub.getString("extra"));
				if (StringHelper.nonEmpty(id)) {
					error.setId(NumberHelper.longValue(id));
					errorMap.put(new Long(error.getId()), error);
					if (log().isDebugEnabled()) {
						log().debug("Adding Exception:" + error);
					}
				} else {
					if (log().isWarnEnabled()) {
						log().warn("Exception id is mandatory. Check the file :" + input + ". Details:" + error);
					}
				}
				i++;
			} catch (Exception ex) {
				break;
			}
		}
	}

	public BasicException generateException(long id) {
		BasicException returnValue = new BasicException();
		ErrorMessage msg = errorMap.get(id);
		if (msg != null) {
			returnValue.setErrorMessage(msg.clone());
			return returnValue;
		} else {
			msg = new ErrorMessage("");
			msg.setId(id);
			returnValue.setErrorMessage(msg.clone());
			return returnValue;
		}
	}

	public BasicException generateException(long id, Throwable t) {
		BasicException returnValue = generateException(id);
		returnValue.getErrorMessage().setCause(t);
		return returnValue;
	}

	public BasicException generateException(long id, String message) {
		BasicException returnValue = generateException(id);
		returnValue.getErrorMessage().setMessage(message);
		return returnValue;
	}

	public BasicException generateException(Throwable t) {
		BasicException returnValue = new BasicException(t);
		return returnValue;
	}

	public BasicException generateException(String message, Throwable t) {
		BasicException returnValue = generateException(t);
		returnValue.getErrorMessage().setMessage(message);
		return returnValue;
	}

	public BasicException generateException(long id, String message, Throwable t) {
		BasicException returnValue = generateException(id, message);
		returnValue.getErrorMessage().setCause(t);
		return returnValue;
	}

	public BasicException generateException(long id, String message, String extraInfo, Throwable t) {
		BasicException returnValue = generateException(id, message, t);
		returnValue.getErrorMessage().setExtraInformation(extraInfo);
		return returnValue;
	}

	public long getId(Exception exception) {
		ErrorMessage errorMessage = getErrorMessage(exception);
		if (errorMessage != null) {
			return errorMessage.getId();
		} else return 0;
	}

	public String getErrorCode(Exception exception) {
		ErrorMessage errorMessage = getErrorMessage(exception);
		if (errorMessage != null) {
			return errorMessage.getErrorCode();
		} else return null;
	}

	public ErrorMessage getErrorMessage(Exception exception) {
		if (exception == null) return null;
		if (!(exception instanceof BasicException)) return null;

		BasicException basicException = (BasicException) exception;
		if (basicException.getErrorMessage() != null) {
			return basicException.getErrorMessage();
		}
		return null;
	}

	public BasicException generateNotImplementedException() {
		BasicException returnValue = generateException(ExceptionConstants.METHOD_NOT_IMPLEMENTED, "Método não Implementado Ainda");
		return returnValue;
	}

	public <T extends BasicException> T generateException(Class<T> targetExceptionType, Throwable error) {
		if (targetExceptionType.isInstance(error)) {
			return (T) error;
		} else {
			Class<?>[] argsClass = new Class[] { String.class, Throwable.class };
			try {
				Constructor<T> c = targetExceptionType.getConstructor(argsClass);
				T exception = c.newInstance(handleException(error).getMessage(), error);
				return exception;
			} catch (Exception e) {
				// situacao de erro, nao deve chegar ate aqui,.
				try {
					T returnValue = targetExceptionType.newInstance();
					return returnValue;
				} catch (Exception e1) {
					return (T) error;
				}
			}
		}
	}

	public <T extends BasicException> T generateException(Class<T> targetExceptionType, long id, Throwable error) {
		if (targetExceptionType.isInstance(error)) {
			((BasicException) error).getErrorMessage().setId(id);
			return (T) error;
		} else {
			Class<?>[] intArgsClass = new Class[] { Throwable.class };
			try {
				Constructor<T> c = targetExceptionType.getConstructor(intArgsClass);
				T returnValue = c.newInstance(error);

				ErrorMessage msg = errorMap.get(id);
				if (msg != null) {
					returnValue.setErrorMessage(msg.clone());
				} else {
					msg = new ErrorMessage("");
					msg.setId(id);
					returnValue.setErrorMessage(msg.clone());
				}
				return returnValue;
			} catch (Exception e) {
				try {
					T returnValue = targetExceptionType.newInstance();
					return returnValue;
				} catch (Exception e1) {
					return (T) error;
				}
			}
		}
	}

	public <T extends BasicException> T generateException(Class<T> targetExceptionType, String message, Throwable error) {
		T returnValue = generateException(targetExceptionType, error);
		returnValue.getErrorMessage().setMessage(message);
		return returnValue;
	}

	public <T extends BasicException> T generateException(Class<T> targetExceptionType, long id, String message, Throwable error) {
		T returnValue = generateException(targetExceptionType, message, error);
		returnValue.getErrorMessage().setId(id);
		return returnValue;
	}

	public <T extends BasicException> T generateException(Class<T> targetExceptionType, long id) {
		try {
			T returnValue = targetExceptionType.newInstance();
			ErrorMessage msg = errorMap.get(id);
			if (msg != null) {
				returnValue.setErrorMessage(msg.clone());
			} else {
				msg = new ErrorMessage("");
				msg.setId(id);
				returnValue.setErrorMessage(msg.clone());
			}
			return returnValue;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public <T extends BasicException> T generateException(Class<T> targetExceptionType, long id, String message) {
		T returnValue = generateException(targetExceptionType, id);
		returnValue.getErrorMessage().setMessage(message);
		return returnValue;
	}
	
	public ServiceException generateServiceExceptionInvalidField(String message) {
		return generateException(ServiceException.class, ExceptionConstants.K_INVALID_FIELD_VALUE, message);
	}

	public static void main(String[] args) {
		try {
			String teste = "ORA-12899: value too large for column \"ODONTO\".\"LIVRETO\".\"NOME_LIVRETO\" (actual: 602, maximum: 100)";
			Pattern pattern = Pattern.compile("\"(.*?)\"");
			Matcher matcher = pattern.matcher(teste);
			if (matcher.groupCount() > 0) {
				int i = 0;
				while (matcher.find()) {
					switch (i) {
					case 1:
						System.out.println("Entidade:" + matcher.group().replaceAll("\"", ""));
						break;
					case 2:
						System.out.println("Coluna:" + matcher.group().replaceAll("\"", ""));
						break;
					default:
						break;
					}
					i++;
				}
			}

			/*
			 * pattern = Pattern.compile("actual: (.*?),"); matcher = pattern.matcher(teste); if (matcher.groupCount() > 0) { while (matcher.find()) { for (int i = 1; i <= matcher.groupCount(); i++) { System.out.println("XX:" + i + " " + matcher.group(i) ); } } }
			 * 
			 * pattern = Pattern.compile("maximum: (.*?)\\)"); matcher = pattern.matcher(teste); if (matcher.groupCount() > 0) { while (matcher.find()) { for (int i = 1; i <= matcher.groupCount(); i++) { System.out.println("YY:" + i + " " + matcher.group(i) ); } } }
			 */

			pattern = Pattern.compile("actual: (.*?),|maximum: (.*?)\\)");
			matcher = pattern.matcher(teste);
			int maxAllowed = 0, currentSize = 0;
			int counter = 0;
			if (matcher.groupCount() > 0) {
				while (matcher.find()) {
					for (int i = 1; i <= matcher.groupCount(); i++) {
						if (matcher.group(i) != null) {
							if (counter == 0) maxAllowed = Integer.valueOf(matcher.group(i)).intValue();
							else if (counter == 1) currentSize = Integer.valueOf(matcher.group(i)).intValue();
						}
					}
					counter++;
				}
			}

			System.out.println(maxAllowed + " - " + currentSize);

			System.out.println(teste);
		} catch (Exception e) {
			ServiceException ex = ExceptionHandler.getInstance().generateException(ServiceException.class, e);
			System.out.println(ex.toString());
		}
	}

	public ErrorMessage handleException(Throwable e, boolean format) {
		ErrorMessage returnValue = null;
		if (e instanceof ServiceException) {
			returnValue = handleServiceException((ServiceException) e);
		} else if (e instanceof ConstraintViolationException) {
			returnValue = handleConstraintViolationException((ConstraintViolationException) e);
		} else if (e instanceof BasicException) {
			returnValue = handleBasicException((BasicException) e);
		} else if (e instanceof JDBCException) {
			returnValue = handleJDBCException((JDBCException) e);
		} else if (e instanceof LDAPException) {
			returnValue = handleLDAPException((LDAPException) e);
		} else if (e instanceof TransactionSystemException) {
			returnValue = handleTransactionSystemException((TransactionSystemException) e);
		} else if (e instanceof OptimisticLockException) {
			returnValue = handleOptimisticLockException((OptimisticLockException) e);
		} else if (e instanceof HibernateJdbcException) {
			returnValue = handleHibernateJdbcException((HibernateJdbcException) e);
		} else if (e instanceof PropertyValueException) {
			returnValue = handlePropertyValueException((PropertyValueException) e);
		} else if (e instanceof IdentifierGenerationException) {
			returnValue = handleIdentifierGenerationException((IdentifierGenerationException) e);
		} else if (e instanceof GenericJDBCException) {
			returnValue = handleGenericJDBCException((GenericJDBCException) e);
		} else {
			returnValue = handleGenericException(e);
		}

		if (format && returnValue != null && StringHelper.nonEmptyTrim(returnValue.getMessage()) && returnValue.getMessage().indexOf(Constants.K_FORMAT_ERROR_LI_START) < 0) {
			returnValue.setMessage(Constants.K_FORMAT_ERROR_LI_START + returnValue.getMessage() + Constants.K_FORMAT_ERROR_LI_END);
		}
		if (!format && returnValue != null && returnValue.getMessage() != null) {
			returnValue.setMessage(returnValue.getMessage().replace(Constants.K_FORMAT_ERROR_LI_START, "").replace(Constants.K_FORMAT_ERROR_LI_END, ""));
		}
		// org.hibernate.StaleObjectStateException
		return returnValue;		
	}

	public ErrorMessage handleException(Throwable e) {
		return handleException(e, true);
	}

	private ErrorMessage handleGenericJDBCException(GenericJDBCException e) {
		return CustomSQLExceptionHandler.handleSQLException(e.getSQLException());
	}

	private ErrorMessage handleBasicException(BasicException e) {
		if ((e.getCause() != null) && !(e.getCause() instanceof ServiceException)) {
			return handleException(e.getCause());
		}
		return e.getErrorMessage();
	}

	private ErrorMessage handleIdentifierGenerationException(IdentifierGenerationException e) {
		ErrorMessage error = new ErrorMessage("O campo ID (identificado) é obrigatório", e);
		return error;
	}

	private ErrorMessage handleHibernateJdbcException(HibernateJdbcException e) {
		return CustomSQLExceptionHandler.handleSQLException(e.getSQLException());
	}

	private ErrorMessage handlePropertyValueException(PropertyValueException e) {
		String msgCustomizada = ColumnMessageUtil.getInstance().getMensagem(e.getEntityName(), e.getPropertyName());
		if (StringHelper.isEmptyTrim(msgCustomizada)) {
			msgCustomizada = "O campo " + StringHelper.getHumanNameFromJavaName(e.getPropertyName()) + " é obrigatório.";
		}
		ErrorMessage error = new ErrorMessage(msgCustomizada, e);
		return error;
	}

	public ErrorMessage handleOptimisticLockException(OptimisticLockException cause) {
		ErrorMessage returnValue = new ErrorMessage(cause);
		returnValue.setId(ExceptionConstants.K_VALUE_OBJECT_OUT_OF_DATE);
		returnValue.setErrorCode("" + ExceptionConstants.K_VALUE_OBJECT_OUT_OF_DATE);
		returnValue.setMessage("Objeto desatualizado.");
		returnValue.setExtraInformation("Entity:" + cause.getEntity().toString());
		return returnValue;
	}

	private ErrorMessage handleTransactionSystemException(TransactionSystemException cause) {
		ServiceException serviceException = traverseException(ServiceException.class, cause);
		if (serviceException != null) {
			return serviceException.getErrorMessage();
		}

		Throwable sourceCause;
		if (cause.getApplicationException() != null && !(cause.getApplicationException() instanceof TransactionSystemException)) {
			return handleException(cause.getApplicationException());
		} else {
			sourceCause = ExceptionUtils.getCause(cause);
			ErrorMessage returnValue = new ErrorMessage(sourceCause);
			return returnValue;
		}
	}

	private ErrorMessage handleLDAPException(LDAPException cause) {
		ErrorMessage returnValue = new ErrorMessage(cause);
		returnValue.setErrorCode("" + cause.getResultCode());
		returnValue.setMessage(cause.getMessage() + " - " + cause.getLDAPErrorMessage());
		returnValue.setExtraInformation(cause.getLDAPErrorMessage() + " DN = " + cause.getMatchedDN());
		return returnValue;
	}

	private ErrorMessage handleServiceException(ServiceException cause) {
		if ((cause.getCause() != null) && !(cause.getCause() instanceof ServiceException)) {
			return handleException(cause.getCause());
		}
		ErrorMessage errorMessage = cause.getErrorMessage();
		
		if(errorMessage == null)
			errorMessage = new ErrorMessage(cause.getMessage(), cause);
		
		return errorMessage;
	}

	private ErrorMessage handleJDBCException(JDBCException cause) {
		if (cause.getSQLException() != null) {
			return CustomSQLExceptionHandler.handleSQLException(cause.getSQLException());
		} else {
			ErrorMessage returnValue = new ErrorMessage(cause);
			cause.getSQLException().getErrorCode();
			returnValue.setErrorCode("" + cause.getErrorCode());
			returnValue.setMessage(cause.getSQLException().getMessage());
			returnValue.setExtraInformation(" SQL Executado:" + cause.getSQL());
			return returnValue;
		}

	}

	private ErrorMessage handleConstraintViolationException(ConstraintViolationException constraintError) {
		ErrorMessage returnValue = new ErrorMessage(constraintError);
		TratamentoErroConstraint mappedConstraint = getConstraintMap().get(constraintError.getConstraintName());
		
		if (mappedConstraint == null
				&& (constraintError.getSQLException() != null && constraintError
						.getSQLException().getMessage() != null)) {
			Pattern pattern = Pattern.compile("\\((.*?)\\)");
			Matcher match = pattern.matcher(constraintError.getSQLException().getMessage());
			match.find();
			mappedConstraint = getConstraintMap().get(match.group(1));
		}		
		
		// Existe tratamento mapeado cadastrado
		if (mappedConstraint != null) {
			StringBuilder builder = new StringBuilder();
			builder.append(mappedConstraint.getMensagem());
			if (StringHelper.nonEmpty(mappedConstraint.getMensagemAdicional())) {
				builder.append(" - ").append(mappedConstraint.getMensagemAdicional());
			}
			returnValue.setMessage(builder.toString());

			builder = new StringBuilder();
			builder.append("Nome da Constraint:").append(mappedConstraint.getNomeConstraint());
			if (StringHelper.nonEmpty(mappedConstraint.getEntidadePai())) {
				builder.append(" Entidade Pai:").append(mappedConstraint.getEntidadePai());
			}
			if (StringHelper.nonEmpty(mappedConstraint.getEntidadeFilha())) {
				builder.append(" Entidade Filha:").append(mappedConstraint.getEntidadeFilha());
			}
			returnValue.setErrorCode("" + constraintError.getErrorCode());
			builder.append(" SQL Executado:").append(constraintError.getSQL());
			returnValue.setExtraInformation(builder.toString());
		} else {
			// nao existe tramento padráo cadastrado, tratar o erro sql normalmente
			return CustomSQLExceptionHandler.handleSQLException(constraintError.getSQLException());
		}

		return returnValue;
	}

	private Map<String, TratamentoErroConstraint> loadConstraintsErrorMapping() {
		Map<String, TratamentoErroConstraint> returnValue = new HashMap<String, TratamentoErroConstraint>();
		String persistenceServiceName = FrameworkConfiguration.getInstance().getStringValue(Constants.KEY_EXCEPTION_HANDLER_PS);

		PersistenceService ps = null;
		try {
			ps = (PersistenceService) ServiceLocator.getInstance().getBean(persistenceServiceName);
		} catch (Throwable e) {
			return returnValue;
		}

		if (ps == null) return returnValue;
		String errorQuery = FrameworkConfiguration.getInstance().getStringValue(Constants.KEY_EXCEPTION_HANDLER_ERROR_QUERY);
		List<TratamentoErroConstraint> listaErros;
		try {
			listaErros = ps.findByQuery(errorQuery, null);
		} catch (ServiceException e) {
			if (log().isErrorEnabled()) {
				log().error("Erro ao mapear os tramentos de Errors Customizados. Metodo: ExceptionHandler.loadConstraintsErrorMapping");
				log().error(e);
			}
			return returnValue;
		}
		for (TratamentoErroConstraint erro : listaErros) {
			returnValue.put(erro.getNomeConstraint(), erro);
		}
		return returnValue;
	}

	public Map<String, TratamentoErroConstraint> getConstraintMap() {
		return constraintMap;
	}

	public <T extends Throwable> T traverseException(Class<T> targetExceptionType, Throwable source) {
		// varre a lista de exceptions
		Throwable cause = source.getCause();

		while (cause != null && !targetExceptionType.isInstance(cause)) {
			if (cause.equals(cause.getCause())) {
				// não achou :(
				return null;
			}
			cause = cause.getCause();
		}
		return (T) cause;
	}

	private <T extends Throwable> ErrorMessage handleGenericException(T e) {
		Throwable cause = e.getCause();
		if (cause != null && !e.getClass().isInstance(cause)) {
			return handleException(cause);
		} else {
			return new ErrorMessage(e.getMessage(), e);
		}
	}
	
	public <P extends StatusResult> P tratarException(Exception e, P baseResult) {
		if (e instanceof AccessDeniedException) {
			baseResult.setStatus(Status.ERROR);
			baseResult.setMessage("Acesso não permitido.");
		} else {
			baseResult.setStatus(Status.ERROR);
			baseResult.setMessage(e.getMessage());
		}
		return baseResult;
	}
	
	public <T extends StatusResult> T anexarErrosResult(Class<T> clazzResult, Throwable throwable) {

		T retorno = null;
		T status = null;
		
		try {
			
			if (throwable instanceof WSException) {
				status = (T) ((WSException) throwable).getBaseResult();
			} else {
				Exception ex = (Exception) throwable;
				status = (T) new StatusResult(ex.getMessage(), Status.ERROR);
			}

			retorno = clazzResult.newInstance();
			ReflectionUtil.executeSetterMethod(retorno, "status", status.getStatus());
			ReflectionUtil.executeSetterMethod(retorno, "message", status.getMessage());
			
			throwable.printStackTrace();
			
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		} 
		
		return retorno;
	}
}
