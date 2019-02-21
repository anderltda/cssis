package br.com.cssis.foundation;

import br.com.cssis.foundation.util.ReflectionUtil;

public class BasicException extends Exception  {
	private static final long serialVersionUID = 1L;

	protected ErrorMessage errorMessage; 
	
	public ErrorMessage getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(ErrorMessage errorMessage) {
		this.errorMessage = errorMessage;
	}

	public BasicException() {
		super();
	}

	public BasicException(String message) {
		super(message);
	}
	
	public BasicException(Throwable t) {
		super(t);
		errorMessage = new ErrorMessage(t);
	}

	public BasicException(String message, Throwable t) {
		super(message, t);
		errorMessage = new ErrorMessage(t);
	}
	
	public BasicException(ErrorMessage errorMessage) {
		super(errorMessage.getCause());
		this.errorMessage = errorMessage;
	}
	@Override
	public String toString() {
		return ReflectionUtil.getFieldListAndValues(this);
	}
}