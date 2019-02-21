package br.com.cssis.foundation.query.impl;

import java.util.LinkedHashMap;
import java.util.Map;

import br.com.cssis.foundation.util.StringHelper;

public class Operator {
	public static enum OP {
		EQUAL, GREATHER, LESS, IN, BETWEEN, LIKE, EXISTS, GREATHER_EQUAL, LESS_EQUAL, NULL, NOT_NULL, NOT_EQUAL, GREATHER_NULL, LESS_NULL, EQUAL_NULL, GREATHER_EQUAL_NULL, LESS_EQUAL_NULL, NOT_EQUAL_NULL, CONTAINS 
	};

	public static final OP EQUAL = OP.EQUAL;
	public static final OP NOT_EQUAL = OP.NOT_EQUAL;
	public static final OP GREATHER = OP.GREATHER;
	public static final OP LESS = OP.LESS;
	public static final OP IN = OP.IN;
	public static final OP BETWEEN = OP.BETWEEN;
	public static final OP LIKE = OP.LIKE;
	public static final OP EXISTS = OP.EXISTS;
	public static final OP GREATHER_EQUAL = OP.GREATHER_EQUAL;
	public static final OP LESS_EQUAL = OP.LESS_EQUAL;
	public static final OP NULL = OP.NULL;
	public static final OP NOT_NULL = OP.NOT_NULL;
	public static final OP GREATHER_NULL = OP.GREATHER_NULL;
	public static final OP LESS_NULL = OP.LESS_NULL;
	public static final OP EQUAL_NULL = OP.EQUAL_NULL;
	public static final OP GREATHER_EQUAL_NULL = OP.GREATHER_EQUAL_NULL;
	public static final OP LESS_EQUAL_NULL = OP.LESS_EQUAL_NULL;
	public static final OP NOT_EQUAL_NULL = OP.NOT_EQUAL_NULL;
	public static final OP CONTAINS = OP.CONTAINS;

	public static final String EQUAL_REPRESENTATION = "=";
	public static final String GREATHER_REPRESENTATION = ">";
	public static final String GREATHER_EQUAL_REPRESENTATION = ">=";
	public static final String LESS_REPRESENTATION = "<";
	public static final String LESS_EQUAL_REPRESENTATION = "<=";
	public static final String IN_REPRESENTATION = "IN";
	public static final String LIKE_REPRESENTATION = "LIKE";
	public static final String EXISTS_REPRESENTATION = "EXISTS";
	public static final String BETWEEN_REPRESENTATION = "BETWEEN";
	public static final String NULL_REPRESENTATION = "IS";
	public static final String CONTAINS_REPRESENTATION = "CONTAINS";
	public static final String NOT_CONTAINS_REPRESENTATION = "NOT CONTAINS";
	
	public static final String GREATHER_NULL_REPRESENTATION = "(*) >";
	public static final String LESS_NULL_REPRESENTATION = "(*) <";
	public static final String EQUAL_NULL_REPRESENTATION = "(*) =";
	public static final String GREATHER_EQUAL_NULL_REPRESENTATION = "(*) >=";
	public static final String LESS_EQUAL_NULL_REPRESENTATION = "(*) <=";
	public static final String NOT_EQUAL_NULL_REPRESENTATION = "(*) !=";

	public static final String NOT_EQUAL_REPRESENTATION = "!=";
	public static final String NOT_GREATHER_REPRESENTATION = LESS_EQUAL_REPRESENTATION;
	public static final String NOT_LESS_REPRESENTATION = GREATHER_EQUAL_REPRESENTATION;
	public static final String NOT_IN_REPRESENTATION = "NOT IN";
	public static final String NOT_LIKE_REPRESENTATION = "NOT LIKE";
	public static final String NOT_EXISTS_REPRESENTATION = "NOT EXISTS";
	public static final String NOT_BETWEEN_REPRESENTATION = "NOT BETWEEN";
	public static final String NOT_NULL_REPRESENTATION = "IS NOT";

	private static final String BETWEEN_SEPARATOR_REPRESENTATION = "AND";
	private static final String IN_SEPARATOR_REPRESENTATION = ",";

	public static final Map<String, String> REPRESENTATIONS = new LinkedHashMap<String, String>();

	static {
		REPRESENTATIONS.put(EQUAL_REPRESENTATION, "=");
		REPRESENTATIONS.put(NOT_EQUAL_REPRESENTATION, "!=");
		REPRESENTATIONS.put(GREATHER_REPRESENTATION, ">");
		REPRESENTATIONS.put(GREATHER_EQUAL_REPRESENTATION, (new Character('\u2265')).toString());
		REPRESENTATIONS.put(LESS_REPRESENTATION, "<");
		REPRESENTATIONS.put(LESS_EQUAL_REPRESENTATION, (new Character('\u2264')).toString());
		REPRESENTATIONS.put(IN_REPRESENTATION, "Em");
		REPRESENTATIONS.put(BETWEEN_REPRESENTATION, "Entre");
		REPRESENTATIONS.put(GREATHER_NULL_REPRESENTATION, GREATHER_NULL_REPRESENTATION);
		REPRESENTATIONS.put(GREATHER_EQUAL_NULL_REPRESENTATION, GREATHER_EQUAL_NULL_REPRESENTATION);
		REPRESENTATIONS.put(LESS_NULL_REPRESENTATION, LESS_NULL_REPRESENTATION);
		REPRESENTATIONS.put(LESS_EQUAL_NULL_REPRESENTATION, LESS_EQUAL_NULL_REPRESENTATION);
		REPRESENTATIONS.put(EQUAL_NULL_REPRESENTATION, EQUAL_NULL_REPRESENTATION);
		REPRESENTATIONS.put(NOT_EQUAL_NULL_REPRESENTATION, NOT_EQUAL_NULL_REPRESENTATION);
	}

	public static final String getRepresentation(OP operator, boolean denied) {
		switch (operator) {
		case EQUAL:
		case EQUAL_NULL:
			return getEqualRepresentation(denied);
		case NOT_EQUAL:
		case NOT_EQUAL_NULL:
			return getEqualRepresentation(!denied);
		case LIKE:
			return getLikeRepresentation(denied);
		case IN:
			return getInRepresentation(denied);
		case NULL:
			return getNullRepresentation(denied);
		case GREATHER:
		case GREATHER_NULL:
			return getGreatherRepresentation(denied);
		case LESS:
		case LESS_NULL:
			return getLessRepresentation(denied);
		case EXISTS:
			return getExistsRepresentation(denied);
		case GREATHER_EQUAL:
		case GREATHER_EQUAL_NULL:
			return getLessRepresentation(!denied);
		case LESS_EQUAL:
		case LESS_EQUAL_NULL:
			return getGreatherRepresentation(!denied);
		case BETWEEN:
			return getBetweenRepresentation(denied);
		case CONTAINS:
			return getContainsRepresentation(denied);
			
		default:
			return getEqualRepresentation(denied);
		}
	}

	public static final Operator.OP getOperator(String stringRepresentation) {
		if (StringHelper.isEmpty(stringRepresentation))
			return OP.NULL;
		if (stringRepresentation.equalsIgnoreCase(Operator.EQUAL_REPRESENTATION))
			return OP.EQUAL;
		else if (stringRepresentation.equalsIgnoreCase(Operator.LIKE_REPRESENTATION))
			return OP.LIKE;
		else if (stringRepresentation.equalsIgnoreCase(Operator.GREATHER_REPRESENTATION))
			return OP.GREATHER;
		else if (stringRepresentation.equalsIgnoreCase(Operator.GREATHER_EQUAL_REPRESENTATION))
			return OP.GREATHER_EQUAL;
		else if (stringRepresentation.equalsIgnoreCase(Operator.LESS_REPRESENTATION))
			return OP.LESS;
		else if (stringRepresentation.equalsIgnoreCase(Operator.LESS_EQUAL_REPRESENTATION))
			return OP.LESS_EQUAL;
		else if (stringRepresentation.equalsIgnoreCase(Operator.IN_REPRESENTATION))
			return OP.IN;
		else if (stringRepresentation.equalsIgnoreCase(Operator.BETWEEN_REPRESENTATION))
			return OP.BETWEEN;
		else if (stringRepresentation.equalsIgnoreCase(Operator.GREATHER_NULL_REPRESENTATION))
			return OP.GREATHER_NULL;
		else if (stringRepresentation.equalsIgnoreCase(Operator.LESS_NULL_REPRESENTATION))
			return OP.LESS_NULL;		
		else if (stringRepresentation.equalsIgnoreCase(Operator.EQUAL_NULL_REPRESENTATION))
			return OP.EQUAL_NULL;
		else if (stringRepresentation.equalsIgnoreCase(Operator.GREATHER_EQUAL_NULL_REPRESENTATION))
			return OP.GREATHER_EQUAL_NULL;				
		else if (stringRepresentation.equalsIgnoreCase(Operator.LESS_EQUAL_NULL_REPRESENTATION))
			return OP.LESS_EQUAL_NULL;
		else if (stringRepresentation.equalsIgnoreCase(Operator.NOT_EQUAL_NULL_REPRESENTATION))
			return OP.NOT_EQUAL_NULL;		
		else if (stringRepresentation.equalsIgnoreCase(Operator.EXISTS_REPRESENTATION))
			return OP.EXISTS;		
		else if (stringRepresentation.equalsIgnoreCase(Operator.CONTAINS_REPRESENTATION))
			return OP.CONTAINS;
		else
			return null;
	}
	
	public static final String getStringOperator(OP operator, boolean denied) {
		switch (operator) {
		case GREATHER_NULL:
			if (!denied) return GREATHER_NULL_REPRESENTATION;
			else return LESS_EQUAL_NULL_REPRESENTATION;
		case LESS_NULL:
			if (!denied) return LESS_NULL_REPRESENTATION;
			else return GREATHER_EQUAL_NULL_REPRESENTATION;
		case EQUAL_NULL:
			if (!denied) return EQUAL_NULL_REPRESENTATION;
			else return NOT_EQUAL_NULL_REPRESENTATION;
		case GREATHER_EQUAL_NULL:
			if (!denied) return GREATHER_EQUAL_NULL_REPRESENTATION;
			else return LESS_NULL_REPRESENTATION;
		case LESS_EQUAL_NULL:
			if (!denied) return LESS_EQUAL_NULL_REPRESENTATION;
			else return GREATHER_NULL_REPRESENTATION;
		case NOT_EQUAL_NULL:
			if (!denied) return NOT_EQUAL_NULL_REPRESENTATION;
			else return EQUAL_NULL_REPRESENTATION;
			
		default:
			return getRepresentation(operator, denied);
		}
	}
	
	

	public static final Operator.OP getDeniedOperator(String stringRepresentation) {
		if (StringHelper.isEmpty(stringRepresentation))
			return OP.NULL;
		if (stringRepresentation.equalsIgnoreCase(Operator.NOT_EQUAL_REPRESENTATION))
			return OP.EQUAL;
		else if (stringRepresentation.equalsIgnoreCase(Operator.NOT_LIKE_REPRESENTATION))
			return OP.LIKE;
		else if (stringRepresentation.equalsIgnoreCase(Operator.NOT_GREATHER_REPRESENTATION))
			return OP.GREATHER;
		else if (stringRepresentation.equalsIgnoreCase(Operator.NOT_LESS_REPRESENTATION))
			return OP.LESS;
		else if (stringRepresentation.equalsIgnoreCase(Operator.NOT_IN_REPRESENTATION))
			return OP.IN;
		else if (stringRepresentation.equalsIgnoreCase(Operator.NOT_BETWEEN_REPRESENTATION))
			return OP.BETWEEN;
		else if (stringRepresentation.equalsIgnoreCase(Operator.NOT_NULL_REPRESENTATION))
			return OP.NULL;
		else
			return null;
	}

	public static String getNullRepresentation(boolean denied) {
		return !denied ? NULL_REPRESENTATION : NOT_NULL_REPRESENTATION;
	}

	public static final String getRepresentation(OP operator) {
		return getRepresentation(operator, false);
	}

	public static String getBetweenRepresentation(boolean denied) {
		return !denied ? BETWEEN_REPRESENTATION : NOT_BETWEEN_REPRESENTATION;
	}

	public static String getExistsRepresentation(boolean denied) {
		return !denied ? EXISTS_REPRESENTATION : NOT_EXISTS_REPRESENTATION;
	}

	public static String getLessRepresentation(boolean denied) {
		return !denied ? LESS_REPRESENTATION : NOT_LESS_REPRESENTATION;
	}

	public static String getGreatherRepresentation(boolean denied) {
		return !denied ? GREATHER_REPRESENTATION : NOT_GREATHER_REPRESENTATION;
	}

	public static String getInRepresentation(boolean denied) {
		return !denied ? IN_REPRESENTATION : NOT_IN_REPRESENTATION;
	}

	public static String getLikeRepresentation(boolean denied) {
		return !denied ? LIKE_REPRESENTATION : NOT_LIKE_REPRESENTATION;
	}

	public static String getEqualRepresentation(boolean denied) {
		return !denied ? EQUAL_REPRESENTATION : NOT_EQUAL_REPRESENTATION;
	}

	public static String getNotEqualRepresentation(boolean denied) {
		return denied ? EQUAL_REPRESENTATION : NOT_EQUAL_REPRESENTATION;
	}

	public static String getContainsRepresentation(boolean denied) {
		return denied ? NOT_CONTAINS_REPRESENTATION : CONTAINS_REPRESENTATION;
	}	

	public static void main(String[] args) {
		for (Operator.OP op : Operator.OP.values()) {
			System.out.println("OPERADOR : " + op);
			System.out.println("REPRESENTATION[" + op + "] :  " + Operator.getRepresentation(op));
			System.out.println("NOT REPRESENTATION[" + op + "] :  " + Operator.getRepresentation(op, true));
			System.out.println("=========================== ");
		}
	}

	public static boolean requiresParanthesis(OP operator) {
		if (operator == EXISTS || operator == IN  || operator == CONTAINS)
			return true;
		else
			return false;
	}

	public static boolean unaryOperator(OP operator) {
		if (operator == BETWEEN )
			return false;
		else
			return true;
	}
	
	public static boolean unfoldNullValue(OP operator) {
		switch (operator) {
		case GREATHER_NULL:
		case LESS_NULL:
		case EQUAL_NULL:
		case GREATHER_EQUAL_NULL:
		case LESS_EQUAL_NULL:
		case NOT_EQUAL_NULL:			
			return true;
		default:
			return false;
		}
	}
	
	
	public static boolean unaryOperator(String operatorRep) {
		Operator.OP op = getOperator(operatorRep);
		return unaryOperator(op);
	}

	public static String getOperatorSeparator(OP operator) {
		switch (operator) {
		case BETWEEN:
			return BETWEEN_SEPARATOR_REPRESENTATION;
		case IN:
			return IN_SEPARATOR_REPRESENTATION;
		default:
			return "";
		}
	}

}
