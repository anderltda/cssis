package br.com.cssis.foundation;

import java.io.Serializable;

import org.apache.commons.logging.Log;

import br.com.cssis.foundation.util.BooleanHelper;
import br.com.cssis.foundation.util.ReflectionUtil;


public class BasicObject implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private BasicMessage msg;
	private int status;

	public BasicMessage getMsg() {
		return msg;
	}
	public void setMsg(BasicMessage msg) {
		this.msg = msg;
	}
	public int getStatus() {
		return status;
	}
	public String getLoggerMessage() {
		return toString();
	}
	
	public void setStatus(int status) {
		this.status = status;
	}

	public Log log() {
		return LogService.getInstance().getLogger(getClass().getName());
	}
	@Override
	public String toString() {
		return ReflectionUtil.getFieldListAndValues(this);
	}
	
	public static void main(String[] args) {
		BasicObject b = new BasicObject();
		BasicMessage m = new BasicMessage();

		m.setId(1);
		m.setMessage("Mensagem X");
		b.setMsg(m);
		b.setStatus(12);
		
		System.out.println(BooleanHelper.booleanValue("V"));
		System.out.println(BooleanHelper.booleanValue(null));
		System.out.println(BooleanHelper.booleanValue(1));
		System.out.println(BooleanHelper.booleanValue(false));
		System.out.println(BooleanHelper.booleanValue("x"));		
	} 
	
	public long getSerialVersionUID() {
		return serialVersionUID;
	}
}
