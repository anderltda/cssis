package br.com.cssis.foundation.query;

import br.com.cssis.foundation.BasicException;

public interface ConditionValue {
	boolean isNullValue();
	String getRepresentation() throws BasicException;
	String getRepresentation(int index) throws BasicException;	
	Object getValue();
	String getNullValue();
	boolean allowWildCardRepresentation();
	void setRemoveSpecialChar(boolean flag);
	void setRemoveAccent(boolean flag);
	void setForceUpperCase(boolean flag);
	void setForceWildCardAtEnd(boolean flag);
	void setForceWildCardAtBeginning(boolean flag);
	void setUseSoundex(boolean flag);
	Object getHandledValue();
	void setValue(Object newValue);
}
