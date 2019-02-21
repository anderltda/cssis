package br.com.cssis.foundation.query.impl;

import java.util.ArrayList;
import java.util.Collection;

import br.com.cssis.foundation.BasicException;
import br.com.cssis.foundation.query.ConditionValue;
import br.com.cssis.foundation.util.SoundEx;
import br.com.cssis.foundation.util.StringHelper;

public class SQLConditionValue extends SQLSimpleConditionValue implements Cloneable, ConditionValue {
	private static final long serialVersionUID = 1L;
	public static final String SINGLE_QUOTE = "'";
	public static final String DOUBLE_QUOTE = "\"";
	public static final String WILD_CARD = "%";

	protected boolean forceQuote = false;
	protected boolean singleQuote = true;
	protected String alternateWildCard;
	protected boolean forceUpperCase = false;
	protected boolean forceTrim = true; 
	

	public SQLConditionValue(Object newValue) {
		super(newValue);
	}
	
	public boolean isForceQuote() {
		return forceQuote;
	}
	public void setForceQuote(boolean forceQuote) {
		this.forceQuote = forceQuote;
	}
	public boolean isSingleQuote() {
		return singleQuote;
	}
	public void setSingleQuote(boolean singleQuote) {
		this.singleQuote = singleQuote;
	}
	@Override
	public Object getValue() {
		return value;
	}
	
	public Object getHandledValue() {
		if (value instanceof String) {
			String newValue = ((String)value).replaceAll("%", "");
			
			if (useSoundex) newValue = SoundEx.encode(newValue); 
			else { 
				newValue = getValueAsUpper(getValueTrim((String) value));
				if (removeAccent)
					newValue = StringHelper.getNonAccentString(newValue);
				if (removeSpecialChar)
					newValue = StringHelper.removeSpecialChar(newValue);
			}
			newValue = (isWildCardAtBeginning() ? getWildCard():"") + newValue + (isWildCardAtEnd() ? getWildCard():""); 
			return newValue;
		} else  return value;
	}	
	
	@Override
	public void setValue(Object value) {
		this.value = value;
	}
	public boolean isWildCardAtEnd() {
		return appendWildCardAtEnd;
	}
	public boolean isWildCardAtBeginning() {
		return appendWildCardAtBeginning;
	}	
	public String getAlternateWildCard() {
		return alternateWildCard;
	}
	public void setAlternateWildCard(String alternateWildCard) {
		this.alternateWildCard = alternateWildCard;
	}
	public boolean isForceUpperCase() {
		return forceUpperCase;
	}
	public void setForceUpperCase(boolean forceUpperCase) {
		this.forceUpperCase = forceUpperCase;
	}

	public String getWildCard() {

		if ( StringHelper.nonEmpty(alternateWildCard) ) {
			return alternateWildCard;
		}
		else {
			return WILD_CARD;
		}
	}

	public String getQuote() {
		return isSingleQuote() ? SINGLE_QUOTE : DOUBLE_QUOTE;
	}

	@SuppressWarnings("unchecked")
	@Override
	public String getRepresentation() throws BasicException {		
		if (isNullValue()) {
			return SimpleConditionHelper.getNullValue();
		}

		if (value instanceof DatabaseQuery) {
			return ((DatabaseQuery) value).generateSQL(null,null);
		}

		Collection valueList;
		if (value instanceof Collection) {
			valueList = (Collection) value;
		} else if (value instanceof Object[]) {
			valueList = new ArrayList();
			Object arr[] = (Object[]) value;
			for (int i = 0; i < arr.length; i++) {
				valueList.add(arr[i]);
			}
		}
		else {
			ArrayList arr = new ArrayList(1);
			arr.add(value);
			valueList = arr;
		}

		StringBuilder returnValue = new StringBuilder();
		for (Object itemValue : valueList) {
			if (isForceQuote() || (value instanceof String)) {
				returnValue.append(getQuote());

				if (isWildCardAtBeginning()) {
					returnValue.append(getWildCard());
				}

				returnValue.append(getValueAsUpper(getValueTrim(itemValue.toString())));

				if (isWildCardAtEnd()) {
					returnValue.append(getWildCard());
				}
				returnValue.append(getQuote());
			}
			else {
				returnValue.append(getValueAsUpper(itemValue));
			}
			returnValue.append(",");
		}
		return returnValue.toString().substring(0, returnValue.toString().length()-1);
	}
	
	
	public String getWildCardRepresentation() {
		return "?";
	}
	
	
	private String getValueAsUpper(Object itemValue) {
		if (isForceUpperCase()) {
			return itemValue.toString().toUpperCase();	
		}
		else {
			return itemValue.toString();					
		}
	}
	
	private String getValueTrim(String value) {
		return isForceTrim() ? value.trim() : value; 
	}
	
	public boolean isForceTrim() {
		return forceTrim;
	}
	public void setForceTrim(boolean forceTrim) {
		this.forceTrim = forceTrim;
	}
	@Override
	public SQLConditionValue clone() {
		SQLConditionValue clonedObject = new SQLConditionValue(this.value);
		clonedObject.forceQuote = forceQuote;
		clonedObject.singleQuote = singleQuote;
		clonedObject.appendWildCardAtBeginning = appendWildCardAtBeginning;
		clonedObject.appendWildCardAtEnd = appendWildCardAtEnd;
		clonedObject.alternateWildCard = alternateWildCard;
		clonedObject.forceUpperCase = forceUpperCase;
		clonedObject.forceTrim = forceTrim;
		return clonedObject;
	}
	@Override
	public boolean isNullValue() {
		return getValue() == null;
	}
	@Override
	public boolean allowWildCardRepresentation() {
		if ( value instanceof DatabaseQuery ) {
			return false;
		}
		else return true;
	}
	
	public static final String handleWildCard(String value) {
		return StringHelper.removeWildCards(value) + StringHelper.getWildCard();
	}
}
