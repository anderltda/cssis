package br.com.cssis.webservice.exception;

import br.com.cssis.foundation.ErrorMessage;
import br.com.cssis.foundation.service.ServiceException;
import br.com.cssis.webservice.result.StatusResult;

public class WSException extends ServiceException {
	
	private static final long serialVersionUID = 1206513357009905267L;

	private StatusResult statusResult;

	public WSException() {
		super();
	}

	public WSException(String message) {
		super(message);
	}

	public WSException(String message, StatusResult statusResult) {
		super(message);
		this.statusResult = statusResult;
	}
	
	public WSException(StatusResult statusResult) {
		this.statusResult = statusResult;
	}

	public WSException(Throwable t) {
		super(t);
	}

	public WSException(Throwable t, StatusResult statusResult) {
		super(t);
		this.statusResult = statusResult;
	}

	public WSException(String message, Throwable t) {
		super(message, t);
	}

	public WSException(String message, Throwable t, StatusResult statusResult) {
		super(message, t);
		this.statusResult = statusResult;
	}

	public WSException(ErrorMessage errorMessage) {
		super(errorMessage);
	}

	public WSException(ErrorMessage errorMessage, StatusResult statusResult) {
		super(errorMessage);
		this.statusResult = statusResult;
	}

	public StatusResult getBaseResult() {
		return statusResult;
	}

	public void setBaseResult(StatusResult statusResult) {
		this.statusResult = statusResult;
	}
}
