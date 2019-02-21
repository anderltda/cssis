package br.com.cssis.foundation;

import java.io.Serializable;

import br.com.cssis.foundation.util.ReflectionUtil;

public class BasicMessage implements Serializable {
	private static final long serialVersionUID = 9129934266672015696L;
	protected long id;
	protected String message;
	protected String extraInformation;
	
	public String getExtraInformation() {
		return extraInformation;
	}

	public void setExtraInformation(String extraInformation) {
		this.extraInformation = extraInformation;
	}

	public BasicMessage() {
	}

	public BasicMessage(String message) {
		this.message = message;
	}
	
	public BasicMessage(String message, long id) {
		this.message = message;
		this.id = id;
	}
	
	public void setId(long id) {
		this.id = id;
	}
	public long getId() {
		return id;
	}

	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	@Override
	public String toString() {
		return ReflectionUtil.getFieldListAndValues(this);
	}	
}
