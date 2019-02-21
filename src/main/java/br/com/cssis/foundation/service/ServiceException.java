package br.com.cssis.foundation.service;

import br.com.cssis.foundation.BasicException;
import br.com.cssis.foundation.ErrorMessage;

public class ServiceException extends BasicException {
	private static final long serialVersionUID = 1L;

	public ServiceException() {
		super();
	}

	public ServiceException(String message) {
		super(message);
	}
	
	public ServiceException(Throwable t) {
		super(t);
	}

	public ServiceException(String message, Throwable t) {
		super(message, t);
	}
	
	public ServiceException(ErrorMessage errorMessage) {
		super(errorMessage.getCause());
	}
}
