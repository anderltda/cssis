package br.com.cssis.foundation.query.impl;

import java.util.Collection;
import java.util.Date;

import org.apache.commons.collections.CollectionUtils;

import br.com.cssis.foundation.BasicException;
import br.com.cssis.foundation.query.ConditionValue;
import br.com.cssis.foundation.util.SoundEx;
import br.com.cssis.foundation.util.StringHelper;

public class SQLSimpleConditionValue extends BasicException implements ConditionValue {
	private static final long serialVersionUID = 1L;
	private static final String NULL_VALUE = "NULL";
	protected Object value;
	protected boolean removeSpecialChar = false;
	protected boolean removeAccent = false;
	protected boolean forceUpperCase = false;
	protected boolean appendWildCardAtEnd = false;
	protected boolean appendWildCardAtBeginning = false;
	protected boolean useSoundex = false;
	protected boolean useOtherCriteria = false;

	public SQLSimpleConditionValue(Object object) {
		this.value = object;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public String getRepresentation() throws BasicException {
		if (isNullValue()) {
			return SimpleConditionHelper.getNullValue();
		} else {
			boolean use_char = (value instanceof String || value instanceof Date); 
			String returnValue = SimpleConditionHelper.getValue(value);
			if (useSoundex) {
				returnValue = SoundEx.encode(returnValue);
			} else {
				if (removeAccent) {
					returnValue = StringHelper.getNonAccentString(returnValue);
				}
				if (removeSpecialChar) {
					returnValue = StringHelper.removeSpecialChar(returnValue);
				}
				if (forceUpperCase) {
					returnValue = returnValue.toUpperCase();
				}
			}
			String open = use_char ? "'" : "";
			return open + (appendWildCardAtBeginning ? "%" : "") + returnValue + (appendWildCardAtEnd ? "%" : "") + open;
		}
	}

	
	public String getRepresentation(int index) throws BasicException {
		if (!( (value instanceof Collection<?>) || 
			   (value instanceof Object[] ) ) ){
			return getRepresentation();
		}
		
		Object idxValue = CollectionUtils.get(value, index);

		if (value == null) {
			throw new BasicException("Erro ao fazer o GetRepresentation para Índice:" + index);
		}
		
		if (idxValue == null) {
			return SimpleConditionHelper.getNullValue();
		} else {
			boolean use_char = (idxValue instanceof String || idxValue instanceof Date); 
			String returnValue = SimpleConditionHelper.getValue(idxValue);
			if (useSoundex) {
				returnValue = SoundEx.encode(returnValue);
			} else {
				if (removeAccent) {
					returnValue = StringHelper.getNonAccentString(returnValue);
				}
				if (removeSpecialChar) {
					returnValue = StringHelper.removeSpecialChar(returnValue);
				}
				if (forceUpperCase) {
					returnValue = returnValue.toUpperCase();
				}
			}
			String open = use_char ? "'" : "";
			return open + (appendWildCardAtBeginning ? "%" : "") + returnValue + (appendWildCardAtEnd ? "%" : "") + open;
		}
	}

	
	public boolean isNullValue() {
		return value == null;
	}

	public boolean allowWildCardRepresentation() {
		return true;
	}

	public String getNullValue() {
		return NULL_VALUE;
	}

	@Override
	public void setRemoveAccent(boolean flag) {
		removeAccent = flag;

	}

	@Override
	public void setRemoveSpecialChar(boolean flag) {
		removeSpecialChar = flag;

	}

	@Override
	public void setForceUpperCase(boolean flag) {
		forceUpperCase = flag;

	}

	@Override
	public void setForceWildCardAtBeginning(boolean flag) {
		appendWildCardAtBeginning = flag;
	}

	@Override
	public void setForceWildCardAtEnd(boolean flag) {
		appendWildCardAtEnd = flag;
	}

	@Override
	public Object getHandledValue() {
		if (value instanceof String) {
			String returnValue = SimpleConditionHelper.getValue(value);
			
			if (useSoundex) {
				returnValue = SoundEx.encode(returnValue); 
			} else {
				if (removeAccent) {
					returnValue = StringHelper.getNonAccentString(returnValue);
				}
				if (removeSpecialChar) {
					returnValue = StringHelper.removeSpecialChar(returnValue);
				}
				if (forceUpperCase) {
					returnValue = returnValue.toUpperCase();
				}
			}
			
			return (appendWildCardAtBeginning ? "%" : "") + returnValue + (appendWildCardAtEnd ? "%" : "");
		}
		else return value;
	}

	@Override
	public void setUseSoundex(boolean flag) {
		useSoundex = flag;
	}

	public boolean isUseOtherCriteria() {
		return useOtherCriteria;
	}

	public void setUseOtherCriteria(boolean useOtherCriteria) {
		this.useOtherCriteria = useOtherCriteria;
	}
	

}
