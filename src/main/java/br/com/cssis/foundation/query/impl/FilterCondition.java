package br.com.cssis.foundation.query.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import br.com.cssis.foundation.BasicException;
import br.com.cssis.foundation.BasicObject;
import br.com.cssis.foundation.Constants;
import br.com.cssis.foundation.config.FrameworkConfiguration;
import br.com.cssis.foundation.query.ConditionValue;
import br.com.cssis.foundation.query.impl.Operator.OP;
import br.com.cssis.foundation.util.DateTimeHelper;
import br.com.cssis.foundation.util.StringHelper;

@SuppressWarnings("unchecked")
public class FilterCondition extends BasicObject {
	private static final long serialVersionUID = 1L;
	private static final String BLANK_SPACE = " ";

	private enum COND {
		AND, OR
	};

	public final static COND OR = COND.OR;
	public final static COND AND = COND.AND;

	public final static String SQL_AND = "AND";
	public final static String SQL_OR = "OR";

	protected SQLProperty property;
	protected ConditionValue sqlConditionValue;
	protected Operator.OP operator;
	protected Operator.OP innerOperator;

	protected Class<?> referenceClass;
	protected Class<?> referenceProperty;
	protected String referencePropertyName;
	protected String modelPrincipalReferenceId;

	protected String labelField;
	protected Object labelValue;

	protected List<FilterCondition> orNestedCondition;
	protected List<FilterCondition> andNestedCondition;

	private boolean forceDenied = false;
	private boolean specialCriteria = false;
	private String otherCriteriaSqlMapping;
	private boolean useOtherCriteria = false;
	private String sqlMergeCondtion = "AND";
	private boolean ignoreCondition = false;

	public static final String PREPAREDST_PARAM_NAME = FrameworkConfiguration.getInstance().getStringValue(Constants.KEY_QUERY_PREPAREDST_PARAMNAME);
	public static final boolean BIND_BY_NAME = FrameworkConfiguration.getInstance().getStringValue(Constants.KEY_QUERY_PREPAREDST_TYPE).equals("name");
	private static final String PREPAREDST_PARAM_NAME_BIND_NAME = ":" + PREPAREDST_PARAM_NAME;

	public FilterCondition(Operator.OP newOperator, SQLProperty newProperty, Object newValue) throws SQLConditionException {
		checkInputValues(newProperty);
		operator = newOperator;
		property = newProperty;
		initConditionValue(newValue);
	}

	public FilterCondition(Operator.OP newOperator, String newProperty, Object newValue, Operator.OP innerOperator) throws SQLConditionException {
		this(newOperator, newProperty, newValue);
		this.innerOperator = innerOperator;
	}

	public FilterCondition(Operator.OP newOperator, String alias, Object newValue) throws SQLConditionException {
		property = new SQLProperty(alias);
		checkInputValues(property);
		operator = newOperator;
		initConditionValue(newValue);
	}

	public FilterCondition(Operator.OP newOperator, String alias, String sqlFunction, Object newValue) throws SQLConditionException {
		property = new SQLProperty(alias);
		property.setSqlFunction(sqlFunction);
		checkInputValues(property);
		operator = newOperator;
		initConditionValue(newValue);
	}

	public FilterCondition(String sqlAlias, Object newValue) throws SQLConditionException {
		this(Operator.EQUAL, new SQLProperty(sqlAlias, sqlAlias, null, null), newValue);
	}

	public FilterCondition(String operator, String alias, Object newValue) throws SQLConditionException {
		Operator.OP op = Operator.getOperator(operator);
		if (op == null) {
			op = Operator.getDeniedOperator(operator);
			if (op == null)
				op = Operator.EQUAL;
			else
				setForceDenied(true);
		}
		property = new SQLProperty(alias);
		checkInputValues(property);
		this.operator = op;
		initConditionValue(newValue);
	}

	private void initConditionValue(Object value) {
		if (value == null) {
			sqlConditionValue = new SQLSimpleConditionValue(value);
		} else if (value instanceof ConditionValue) {
			sqlConditionValue = (SQLConditionValue) value;
		} else if ((value instanceof Collection) || (value instanceof String) || (value instanceof DatabaseQuery) || (value instanceof Object[])) {
			sqlConditionValue = new SQLConditionValue(value);
			handleLikeOperator();
		} else {
			sqlConditionValue = new SQLSimpleConditionValue(value);
		}

	}

	private void handleLikeOperator() {
		if (operator == Operator.LIKE && getConditionValue().getValue() instanceof String) {
			((SQLConditionValue) sqlConditionValue).setForceWildCardAtEnd(true);
		}
	}

	public FilterCondition(Operator.OP newOperator, SQLProperty newProperty, Object newValue, boolean newForceDenied) throws SQLConditionException {
		this(newOperator, newProperty, newValue);
		forceDenied = newForceDenied;
	}

	private void checkInputValues(SQLProperty property) throws SQLConditionException {
		if (StringHelper.isEmpty(property.getAlias())) {
			throw new SQLConditionException("Property Alias must not be Null/Empty");
		}
	}

	public Operator.OP getOperator() {
		return operator;
	}

	public String getOperatorRepresentation() {
		return Operator.getRepresentation(operator, forceDenied);
	}

	public void setOperator(Operator.OP newOperator) {
		operator = newOperator;
		handleLikeOperator();
	}

	public List<FilterCondition> getOrNestedCondition() {
		return orNestedCondition;
	}

	public void setOrNestedCondition(List<FilterCondition> orNestedCondition) {
		this.orNestedCondition = orNestedCondition;
	}

	public List<FilterCondition> getAndNestedCondition() {
		return andNestedCondition;
	}

	public void setAndNestedCondition(List<FilterCondition> andNestedCondition) {
		this.andNestedCondition = andNestedCondition;
	}

	public String getSQLRepresentation() throws BasicException {
		StringBuilder builder = new StringBuilder();
		builder.append(handleMapping(null)).append(BLANK_SPACE);

		if (!handleNullValue(builder)) {
			handleNonNullValueOperator(builder, null);
		}

		// tratar os nested and
		if (andNestedCondition != null) {
			for (FilterCondition nestedAndCondition : andNestedCondition) {
				builder.append(BLANK_SPACE).append(SQL_AND).append(BLANK_SPACE);
				builder.append(BLANK_SPACE).append("(");
				builder.append(nestedAndCondition.getSQLRepresentation()).append(BLANK_SPACE);
				builder.append(")").append(BLANK_SPACE);
			}
		}

		// tratar os nested or
		if (orNestedCondition != null) {
			for (FilterCondition nestedOrCondition : orNestedCondition) {
				builder.append(BLANK_SPACE).append(SQL_OR).append(BLANK_SPACE);
				builder.append(BLANK_SPACE).append("(");
				builder.append(nestedOrCondition.getSQLRepresentation()).append(BLANK_SPACE);
				builder.append(")").append(BLANK_SPACE);
			}
		}

		return builder.toString();
	}

	private void handleNonNullValueOperator(StringBuilder builder, List<Object> wildCardValues) throws BasicException {
		boolean handleWildCardsValues = wildCardValues != null;

		// tratamento diferenciado para o #TO_VALUE# ==> simples substituição de valores
		if (getProperty().isToValueAlias()) {
			builder.delete(0, builder.length() - 1);
			if (handleWildCardsValues) {
				wildCardValues.add(sqlConditionValue.getHandledValue());
				builder.append(getParamName(wildCardValues.size()));
			} else {
				builder.append(sqlConditionValue.getRepresentation());
			}
			return;
		} else if (getProperty().isToValueSQLAlias()) {
			builder.delete(0, builder.length() - 1);
			builder.append(getProperty().getSqlMapping().replaceAll("#TO_SQL_VALUE#", ""));
			return;
		}

		boolean unfoldNullOperator = (Operator.unfoldNullValue(operator));

		if (unfoldNullOperator) {
			builder.append(Operator.getRepresentation(Operator.NULL, forceDenied)).append(BLANK_SPACE);
			builder.append(sqlConditionValue.getNullValue()).append(BLANK_SPACE);
			builder.append(SQL_OR).append(BLANK_SPACE);
			builder.append(handleMapping(wildCardValues)).append(BLANK_SPACE);
		}

		String openP = Operator.requiresParanthesis(operator) ? "(" : "";
		String closeP = Operator.requiresParanthesis(operator) ? ")" : "";

		if (operator == Operator.CONTAINS) {
			// limpar o que estava lá para trocar, comportamento diferente
			// comportamento é o seguinte: fazer o contains fazendo split e usando o & e depois com um no que veio

			// atenção: contains precisa ser parentisado - bug oracle com contains e ands em outras colunas
			// foi tratado no operador (apenas para documentar)

			builder.delete(0, builder.length() - 1);
			builder.append(openP);
			builder.append(Operator.getRepresentation(operator, forceDenied)).append("(");
			builder.append(getProperty().getSqlMapping()).append(",");

			String tokens[] = ContainsHelper.quebrarTokensContains(sqlConditionValue.getHandledValue().toString());

			StringBuilder containsBuilder = new StringBuilder();
			StringBuilder likeBuilder = new StringBuilder();
			int qtdeTokensValidos = 0;
			for (int i = 0; i < tokens.length; i++) {
				if (tokens[i].length() > 0) {
					containsBuilder.append(tokens[i]);
					qtdeTokensValidos++;
					if ((i + 1) < tokens.length)
						containsBuilder.append(" & ");
				}
				likeBuilder.append("%").append(tokens[i]).append("%");
			}

			if (handleWildCardsValues) {
				wildCardValues.add("%" + containsBuilder.toString() + "%");
				builder.append(getParamName(wildCardValues.size()));
			} else {
				builder.append("%" + containsBuilder.toString() + "%");
			}
			builder.append(") > 0 ");

			// agora gerar um like para garantir a ordem passada, mas somente nos casos de busca com multiplos tokens
			if (qtdeTokensValidos > 1) {
				builder.append("AND ").append(getProperty().getSqlMapping()).append(" LIKE ");
				if (handleWildCardsValues) {
					wildCardValues.add(likeBuilder.toString().replaceAll("([\\%])\\1{0,}", "%"));
					builder.append(getParamName(wildCardValues.size()));
				} else {
					builder.append(likeBuilder.toString().replaceAll("([\\%])\\1{0,}", "%"));
				}
			}
			builder.append(BLANK_SPACE).append(closeP).append(BLANK_SPACE);
			// if contains...
		} else {
			builder.append(Operator.getRepresentation(operator, forceDenied)).append(BLANK_SPACE);
			builder.append(openP);

			if (Operator.unaryOperator(operator)) {
				if (sqlConditionValue.getValue() instanceof Collection || sqlConditionValue.getValue() instanceof Object[]) {
					Object valueList[] = null;
					if (sqlConditionValue.getValue() instanceof Collection) {
						valueList = ((Collection) sqlConditionValue.getValue()).toArray();
					} else {
						valueList = (Object[]) sqlConditionValue.getValue();
					}
					for (int i = 0; i < valueList.length; i++) {
						if (handleWildCardsValues) {
							wildCardValues.add(valueList[i]);
							builder.append(getParamName(wildCardValues.size()));
						} else {
							builder.append(SimpleConditionHelper.getValue(valueList[i]));
						}

						if (i != (valueList.length - 1)) {
							builder.append(Operator.getOperatorSeparator(operator));
						}
					}
				} else if (sqlConditionValue.getValue() instanceof DatabaseQuery) {
					throw new SQLConditionException("Nao implementado ainda");
				} else {
					if (isUseOtherCriteria()) {
						// pegar o mapeamento do outro, já deve ter sido feito anteriormente
						if (getOtherCriteriaSqlMapping() == null) {
							// usa o proprio value que foi passado
							builder.append(sqlConditionValue.getValue());
						} else {
							builder.append(getOtherCriteriaSqlMapping());
						}
					} else {
						if (handleWildCardsValues) {
							wildCardValues.add(sqlConditionValue.getHandledValue());
							builder.append(getParamName(wildCardValues.size()));
						} else {
							builder.append(sqlConditionValue.getRepresentation());
						}
					}
				}
				// if unary...
			} else {
				if (sqlConditionValue.getValue() instanceof Collection || sqlConditionValue.getValue() instanceof Object[]) {
					Object valueList[];
					if (sqlConditionValue.getValue() instanceof Collection) {
						valueList = ((Collection) sqlConditionValue.getValue()).toArray();
					} else {
						valueList = (Object[]) sqlConditionValue.getValue();
					}

					if (handleWildCardsValues) {
						wildCardValues.add(valueList[0]);
						builder.append(getParamName(wildCardValues.size()));
						builder.append(BLANK_SPACE).append(SQL_AND).append(BLANK_SPACE);

						wildCardValues.add(valueList[1]);
						builder.append(getParamName(wildCardValues.size()));
						builder.append(BLANK_SPACE);
					} else {
						builder.append(sqlConditionValue.getRepresentation(0));
						builder.append(BLANK_SPACE).append(SQL_AND).append(BLANK_SPACE);
						builder.append(sqlConditionValue.getRepresentation(1));
					}

				} else {
					// erro tentou usar um between sem os parametros -- dar exececao
					throw new SQLConditionException("Passed a binary operator with no parameters");
				}
			}
			builder.append(BLANK_SPACE);
			builder.append(closeP);
		}

	}

	public String getSQLWildCardRepresentation(List<Object> wildCardValues) throws BasicException {
		if (wildCardValues == null) {
			throw new SQLConditionException("getSQLWildCardRepresentation cannot receive NULL value as wildCardValue");
		}

		StringBuilder builder = new StringBuilder();
		boolean usesInnerQuery = StringHelper.nonEmptyTrim(getProperty().getQueryReference()) || (getReferenceClass() != null && getReferenceProperty() != null);

		if (usesInnerQuery) {
			if (getReferenceClass() != null && getReferenceProperty() != null) {
				handleQLExists(builder, wildCardValues);
			} else {
				builder.append(Operator.getRepresentation(operator, forceDenied)).append(BLANK_SPACE);
				builder.append("(");
				DatabaseQuery innerQuery = QueryHandler.getInstance().checkQuery(getProperty().getQueryReference());
				List<FilterCondition> selectCriteriaList = new ArrayList<FilterCondition>();
				selectCriteriaList.add(new FilterCondition(innerOperator != null ? innerOperator : Operator.EQUAL, getProperty().getSqlMapping(), getSqlConditionValue().getValue()));
				innerQuery.handleConditionList(selectCriteriaList);
				builder.append(innerQuery.generateWildCardSQL(selectCriteriaList, null, wildCardValues));
				builder.append(")").append(BLANK_SPACE);
			}
		} else {
			if (Operator.unfoldNullValue(operator))
				builder.append("(");
			builder.append(handleMapping(wildCardValues)).append(BLANK_SPACE);

			if (StringHelper.isEmpty(getProperty().getSqlFunction())) {
				if (!handleNullValue(builder)) {
					handleNonNullValueOperator(builder, wildCardValues);
				}
			}

			if (Operator.unfoldNullValue(operator))
				builder.append(")");
		}

		// boolean parentesisAppended = false;
		// tratar os nested and
		if (andNestedCondition != null) {
			for (FilterCondition nestedAndCondition : andNestedCondition) {
				builder.append(BLANK_SPACE).append(SQL_AND).append(BLANK_SPACE);
				builder.append(BLANK_SPACE).append("(");
				builder.append(nestedAndCondition.getSQLWildCardRepresentation(wildCardValues)).append(BLANK_SPACE);
				builder.append(")").append(BLANK_SPACE);
			}
		}

		// tratar os nested or
		if (orNestedCondition != null) {
			for (FilterCondition nestedOrCondition : orNestedCondition) {
				builder.append(BLANK_SPACE).append(SQL_OR).append(BLANK_SPACE);
				builder.append(BLANK_SPACE).append("(");
				builder.append(nestedOrCondition.getSQLWildCardRepresentation(wildCardValues)).append(BLANK_SPACE);
				builder.append(")").append(BLANK_SPACE);
			}
		}

		// if (parentesisAppended) {
		// builder.append(")").append(BLANK_SPACE);
		// }

		return builder.toString();
	}

	private void handleQLExists(StringBuilder builder, List<Object> wildCardValues) throws BasicException {
		// metodo para fazer exists com QL
		builder.append(Operator.getRepresentation(operator, forceDenied)).append(BLANK_SPACE);
		String referenceProperty = getReferenceProperty().getSimpleName();
		String qlReferenceProperty = "_" + referenceProperty;
		String referenceClass = StringHelper.firstLower(getReferenceClass().getSimpleName());

		builder.append("(");
		builder.append("from ").append(referenceProperty).append(BLANK_SPACE).append(qlReferenceProperty).append(BLANK_SPACE);		
		builder.append("where ");
		if (StringUtils.isEmpty(getModelPrincipalReferenceId())) {
			builder.append(qlReferenceProperty).append(".").append(referenceClass).append(".id = ").append("model.id");
		} else {
			builder.append(qlReferenceProperty).append(".").append(getModelPrincipalReferenceId()).append(" = ").append("model.id");
		}
		builder.append(" and ");

		Operator.OP originalOperator = getOperator();
		setOperator(getInnerOperator() == null ? Operator.EQUAL : innerOperator);
		builder.append(qlReferenceProperty + "." + getProperty().getSqlMapping()).append(BLANK_SPACE);
		if (!handleNullValue(builder)) {
			handleNonNullValueOperator(builder, wildCardValues);
		}

		setOperator(originalOperator);
		builder.append(")").append(BLANK_SPACE);
	}

	public boolean isForceDenied() {
		return forceDenied;
	}

	public void setForceDenied(boolean forceDenied) {
		this.forceDenied = forceDenied;
	}

	public void addCondition(COND sqlConditionType, FilterCondition newCondition) throws SQLConditionException {
		if (newCondition == this)
			return;

		if (checkRecursiveInclusion(newCondition)) {
			throw new SQLConditionException("Recursive Conditions are not allowed");
		}
		switch (sqlConditionType) {
		case AND:
			if (andNestedCondition == null)
				andNestedCondition = new ArrayList<FilterCondition>(1);
			andNestedCondition.add(newCondition);
			break;
		case OR:
			if (orNestedCondition == null)
				orNestedCondition = new ArrayList<FilterCondition>(1);
			orNestedCondition.add(newCondition);
			break;
		}
	}

	public int countConditions() {
		// contar o self e tambem os nested, recursivamente
		int returnValue = 1;

		if (sqlConditionValue.isNullValue()) {
			returnValue--;
		}

		if (andNestedCondition != null) {
			for (FilterCondition nestedAndCondition : andNestedCondition) {
				returnValue += nestedAndCondition.countConditions();
			}
		}
		if (orNestedCondition != null) {
			for (FilterCondition nestedOrCondition : orNestedCondition) {
				returnValue += nestedOrCondition.countConditions();
			}
		}
		return returnValue;
	}

	public SQLProperty getProperty() {
		return property;
	}

	public void setProperty(SQLProperty property) {
		this.property = property;
	}

	public ConditionValue getSqlConditionValue() {
		return sqlConditionValue;
	}

	public ConditionValue getConditionValue() {
		return sqlConditionValue;
	}

	public void setSqlConditionValue(ConditionValue sqlConditionValue) {
		this.sqlConditionValue = sqlConditionValue;
	}

	private boolean checkRecursiveInclusion(FilterCondition newCondition) {
		if (this == newCondition)
			return true;
		List<FilterCondition> parentChildList = newCondition.getChildConditionList();
		if (parentChildList.contains(this)) {
			return true;
		}
		return false;
	}

	private List<FilterCondition> getChildConditionList() {
		List<FilterCondition> returnValue = new ArrayList<FilterCondition>();

		if (andNestedCondition != null) {
			for (FilterCondition nestedAndCondition : andNestedCondition) {
				returnValue.add(nestedAndCondition);
				List<FilterCondition> childList = nestedAndCondition.getChildConditionList();
				returnValue.addAll(childList);
			}
		}
		if (orNestedCondition != null) {
			for (FilterCondition nestedOrCondition : orNestedCondition) {
				returnValue.add(nestedOrCondition);
				List<FilterCondition> childList = nestedOrCondition.getChildConditionList();
				returnValue.addAll(childList);
			}
		}
		return returnValue;
	}

	private boolean handleNullValue(StringBuilder builder) {
		if (sqlConditionValue.isNullValue()) {
			// se for null o valor...trocar o operador para nullo, porem eh
			// necessario
			// verificar se esta negado
			builder.append(Operator.getRepresentation(Operator.NULL, forceDenied)).append(BLANK_SPACE);
			builder.append(sqlConditionValue.getNullValue());
			return true;
		} else
			return false;
	}

	public boolean isSpecialCriteria() {
		return specialCriteria;
	}

	public void setSpecialCriteria(boolean specialCriteria) {
		this.specialCriteria = specialCriteria;
	}

	public boolean hasNestedConditions() {
		return (andNestedCondition != null && andNestedCondition.size() > 0) || (orNestedCondition != null && orNestedCondition.size() > 0);
	}

	@Override
	public String toString() {
		StringBuilder b = new StringBuilder();
		if (property != null)
			b.append(property.getAlias()).append(" ");
		if (operator != null)
			b.append(Operator.getRepresentation(operator, isForceDenied())).append(" ");
		if (sqlConditionValue != null)
			b.append(sqlConditionValue.getValue());

		return b.toString();
	}

	public static String getParamName(int index) {
		if (BIND_BY_NAME) {
			return PREPAREDST_PARAM_NAME_BIND_NAME + index;
		} else
			return "?";
	}

	public static String getBindName(int index) {
		if (BIND_BY_NAME) {
			return PREPAREDST_PARAM_NAME + index;
		} else
			return "" + index;
	}

	public Operator.OP getInnerOperator() {
		return innerOperator;
	}

	public void setInnerOperator(Operator.OP innerOperator) {
		this.innerOperator = innerOperator;
	}

	public Class<?> getReferenceClass() {
		return referenceClass;
	}

	public void setReferenceClass(Class<?> referenceClass) {
		this.referenceClass = referenceClass;
	}

	public Class<?> getReferenceProperty() {
		return referenceProperty;
	}

	public void setReferenceProperty(Class<?> referenceProperty) {
		this.referenceProperty = referenceProperty;
	}

	public String getReferencePropertyName() {
		return referencePropertyName;
	}

	public void setReferencePropertyName(String referencePropertyName) {
		this.referencePropertyName = referencePropertyName;
	}
	
	public String getModelPrincipalReferenceId() {
		return modelPrincipalReferenceId;
	}

	public void setModelPrincipalReferenceId(String modelPrincipalReferenceId) {
		this.modelPrincipalReferenceId = modelPrincipalReferenceId;
	}

	public Object getLabelValue() {
		return labelValue;
	}

	public void setLabelValue(Object labelValue) {
		this.labelValue = labelValue;
	}

	public String getLabelField() {
		return labelField;
	}

	public void setLabelField(String labelField) {
		this.labelField = labelField;
	}

	public void setSqlFunction(String sqlFunction) {
		getProperty().setSqlFunction(sqlFunction);
	}

	public String getSqlFunction() {
		return getProperty().getSqlFunction();
	}

	private String handleMapping(List<Object> wildCardValues) throws BasicException {
		return SqlFunctionDialects.handleSqlFunction(this, wildCardValues);
	}

	public String getOtherCriteriaSqlMapping() {
		return otherCriteriaSqlMapping;
	}

	public void setOtherCriteriaSqlMapping(String otherCriteriaSqlMapping) {
		this.otherCriteriaSqlMapping = otherCriteriaSqlMapping;
	}

	public boolean isUseOtherCriteria() {
		return useOtherCriteria;
	}

	public void setUseOtherCriteria(boolean useOtherCriteria) {
		this.useOtherCriteria = useOtherCriteria;
	}

	public String getSqlMergeCondtion() {
		return sqlMergeCondtion;
	}

	public void setSqlMergeCondtion(COND cond) {
		if (cond == OR) {
			sqlMergeCondtion = "OR";
		} else if (cond == AND) {
			sqlMergeCondtion = "AND";
		}
	}

	public static final FilterCondition getCriteriaByName(List<FilterCondition> criteriaList, String name) {
		for (FilterCondition filterCondition : criteriaList) {
			if (name.equalsIgnoreCase(filterCondition.getProperty().getAlias())) {
				return filterCondition;
			}
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public static final int getNumeroDiasFiltro(FilterCondition filter) {
		Operator.OP op = filter.getOperator();
		Object value = filter.getConditionValue().getValue();
		Date dataInicio = null, dataFim = null;
		// para os unários
		if (Operator.unaryOperator(op) && Operator.OP.IN != op) {
			dataInicio = (Date) value;
			if (Operator.OP.EQUAL == op) {
				dataFim = dataInicio;
			} else {
				dataFim = new Date();
			}
		} else {
			if (value instanceof Collection || value instanceof Object[]) {
				Object valueList[];
				if (value instanceof Collection) {
					valueList = ((Collection) value).toArray();
				} else {
					valueList = (Object[]) value;
				}
				if (Operator.OP.IN == op){
					return (Integer) valueList.length;
				}
				dataInicio = (Date) valueList[0];
				dataFim = (Date) valueList[1];
			} else {
				// erro tentou usar um between sem os parametros -- dar exececao
				return 1000;
			}
		}
		return Math.abs(DateTimeHelper.differenctInDays(dataInicio, dataFim));
	}
	
	public static void addCondition(List<FilterCondition> conditions, String sqlName, OP op, Object value) throws SQLConditionException{
		if(sqlName != null && op != null && value != null){
			conditions.add(new FilterCondition(op, sqlName, value));
		}
	}

	public boolean isIgnoreCondition() {
		return ignoreCondition;
	}

	public void setIgnoreCondition(boolean ignoreCondition) {
		this.ignoreCondition = ignoreCondition;
	}
	
	public static void removeCondition(List<FilterCondition> conditions, String conditionName){
		List<FilterCondition> nConditions = new ArrayList<FilterCondition>();
		
		for (FilterCondition filtro : conditions) {
			if (!filtro.getProperty().getAlias().equals(conditionName)) {
				nConditions.add(filtro);
			}
		}
		conditions.clear();
		conditions.addAll(nConditions);
	}
	
	public static void removeCondition(List<FilterCondition> conditions, String... conditionName){
		for (String cn : conditionName) {
			Iterator<FilterCondition> itFilterCondition = conditions.iterator();
			while (itFilterCondition.hasNext()) {
				FilterCondition fc = itFilterCondition.next();
				if (fc.getProperty().getAlias().equals(cn)) {
					itFilterCondition.remove();
				}
			}
		}
	}
	
	/**
	 * Extração dos parâmetros do filtro para uma estrutura de Map, requerida pela framework iBatis.
	 * @param conditions Lista que contém os filtros de pesquisa.
	 * @return Map<Object,Object>
	 */
	public static Map<Object,Object> getIbatisParamsFromFilterConditions(List<FilterCondition> conditions){
		Map<Object,Object> params = null;
		if(conditions != null && conditions.size() > 0){
			params = new HashMap<Object,Object>();
			for (FilterCondition filtro : conditions) {
					if (filtro.sqlConditionValue.getValue() instanceof Set) {
						List lista = new ArrayList();
						for (Object object : (Set) filtro.sqlConditionValue.getValue()) {
							lista.add(object);
						}						
						params.put(filtro.getProperty().getAlias(), lista);
				}else{
					params.put(filtro.getProperty().getAlias(), filtro.getSqlConditionValue().getValue());
				}
			}
		}
		return params;
	}

}
