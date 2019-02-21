package br.com.cssis.foundation;

import br.com.cssis.foundation.util.ReflectionUtil;
import br.com.cssis.foundation.util.StringHelper;

public class ErrorMessage extends BasicMessage implements Cloneable {
	private static final long serialVersionUID = 1L;
	
	protected String errorCode;
	protected Throwable cause;

	public ErrorMessage(String errorCode) {
		super();
		this.errorCode = errorCode;
	}
	
	public ErrorMessage(Throwable cause) {
		super();
		this.cause = cause;
		if (StringHelper.nonEmpty(cause.getMessage())) {
			setMessage(cause.getMessage());
		}
	}
	
	public ErrorMessage(String message, Throwable cause) {
		super(message);
		this.cause = cause;
	}
	
	public ErrorMessage(String message, String errorcode, Throwable cause) {
		super(message);
		this.cause = cause;
		this.errorCode = errorcode;
	}	
	
	public String getErrorCode() {
		return errorCode;
	}
	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}
	public Throwable getCause() {
		return cause;
	}
	public void setCause(Throwable cause) {
		this.cause = cause;
	}
	@Override
	public String toString() {
		return ReflectionUtil.getFieldListAndValues(this);
	}
	@Override
	public ErrorMessage clone() {
		ErrorMessage cloned = new ErrorMessage(errorCode);
		cloned.setCause(getCause());
		cloned.setId(getId());
		cloned.setErrorCode(getErrorCode());
		cloned.setExtraInformation(getExtraInformation());
		cloned.setMessage(getMessage());
		return cloned;
	}
}
