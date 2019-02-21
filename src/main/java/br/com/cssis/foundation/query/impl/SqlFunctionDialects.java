package br.com.cssis.foundation.query.impl;

import java.util.Collection;
import java.util.List;

import br.com.cssis.foundation.BasicException;
import br.com.cssis.foundation.ExceptionConstants;
import br.com.cssis.foundation.ExceptionHandler;
import br.com.cssis.foundation.util.StringHelper;

public class SqlFunctionDialects {
	public static String handleSqlFunction(FilterCondition condition, List<Object> wildCardValues) throws BasicException {

		if (StringHelper.isEmpty(condition.getProperty().getSqlFunction())) return condition.getProperty().getSqlMapping();

		String returnValue = "";
		String functionToMap = condition.getProperty().getSqlFunction().toUpperCase();
		String mapping = condition.getProperty().getSqlMapping();
		boolean handleWildCard = wildCardValues != null;

		if (functionToMap.equals("BITAND")) {
			// caso tenha vindo um collection de valores será
			// BITAND (MAPPING, VALOR1) = VALOR2
			// BITAND (MAPPING, VALOR1) = VALOR1			
			boolean isCollection = condition.getSqlConditionValue().getValue() instanceof Collection || condition.getSqlConditionValue().getValue() instanceof Object[];
			String op = Operator.getRepresentation(condition.getOperator(), condition.isForceDenied());
			if (handleWildCard) {
				if (isCollection) {
					Object valueList[] = null;
					Object value1 = 0;
					Object value2 = 0;
					if (condition.getSqlConditionValue().getValue() instanceof Collection) {
						valueList = ((Collection<?>) condition.getSqlConditionValue().getValue()).toArray();
					} else {
						valueList = (Object[]) condition.getSqlConditionValue().getValue();
					}
					if (valueList.length <= 1) {
						throw ExceptionHandler.getInstance().generateException(BasicException.class, ExceptionConstants.SQL_INVALID_CRITERIA, "Erro ao usar função BITAND. Número incorreto de parametors para o array");
					} else {
						value1 = valueList[0];
						value2 = valueList[1];
					}
					wildCardValues.add(value1);
					wildCardValues.add(value2);
					returnValue = "BITAND(" + mapping + ", " + FilterCondition.getParamName(wildCardValues.size() - 1) + ") " + op + " " + FilterCondition.getParamName(wildCardValues.size());
				} else {
					wildCardValues.add(condition.getSqlConditionValue().getHandledValue());
					returnValue = "BITAND(" + mapping + ", " + FilterCondition.getParamName(wildCardValues.size()) + ") " + op + " " + FilterCondition.getParamName(wildCardValues.size());
				}
			} else {
				if (isCollection) {
					Object valueList[] = null;
					Object value1 = 0;
					Object value2 = 0;
					if (condition.getSqlConditionValue().getValue() instanceof Collection) {
						valueList = ((Collection<?>) condition.getSqlConditionValue().getValue()).toArray();
					} else {
						valueList = (Object[]) condition.getSqlConditionValue().getValue();
					}
					if (valueList.length <= 1) {
						throw ExceptionHandler.getInstance().generateException(BasicException.class, ExceptionConstants.SQL_INVALID_CRITERIA, "Erro ao usar função BITAND. Número incorreto de parametors para o array");
					} else {
						value1 = valueList[0];
						value2 = valueList[1];
					}
					returnValue = "BITAND(" + mapping + ", " + value1 + ") " + op + " " + value2;
				} else {
					returnValue = "BITAND(" + mapping + ", " + condition.getSqlConditionValue().getRepresentation() + ") " + op + " " + condition.getSqlConditionValue().getRepresentation();
				}
			}
		} else {
			throw ExceptionHandler.getInstance().generateException(BasicException.class, ExceptionConstants.SQL_INVALID_CRITERIA, "Function :" + functionToMap + " is not supported.");
		}
		return returnValue;
	}

}
