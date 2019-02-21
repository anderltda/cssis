package br.com.cssis.foundation;

import java.io.Serializable;

public interface BaseEntity extends Serializable {
	boolean isReentrant();
	void setReentrant(boolean flag);
	boolean isIdNull();
	boolean isDoNotValidate();
}

