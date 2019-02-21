package br.com.cssis.foundation.validator.impl;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.JoinColumn;

import br.com.cssis.foundation.BasicEntityObject;
import br.com.cssis.foundation.BasicObject;
import br.com.cssis.foundation.Constants;
import br.com.cssis.foundation.config.FrameworkConfiguration;
import br.com.cssis.foundation.util.ColumnMessageUtil;
import br.com.cssis.foundation.util.ReflectionUtil;
import br.com.cssis.foundation.util.StringHelper;

public class GlobalColumnValidator extends BasicObject {
	private static final long serialVersionUID = 1L;
	private static GlobalColumnValidator instance = new GlobalColumnValidator();

	private Map<Class<?>, EntityClassValidator> validationMap;
	private Set<Class<?>> notValidationMap;
	private boolean on;

	private GlobalColumnValidator() {
		restart();
	}

	private void restart() {
		validationMap = new HashMap<Class<?>, EntityClassValidator>();
		notValidationMap = new HashSet<Class<?>>();
		on = FrameworkConfiguration.getInstance().getBooleanValue(Constants.KEY_GLOBAL_COLUMN_VALIDATOR);
	}

	public static GlobalColumnValidator getInstance() {
		return instance;
	}

	public String validate(Object value) {
		if (!on)
			return null;
		if (value == null)
			return null;
		Class<?> clazz = value.getClass();
		if (hasValidator(clazz)) {
			return applyValidatorRules(validationMap.get(clazz), value);
		} else
			return null;
	}

	private String applyValidatorRules(EntityClassValidator validator, Object value) {
		return validator.validate(value);
	}

	private boolean hasValidator(Class<?> clazz) {
		if (notValidationMap.contains(clazz)) {
			return false;
		}
		if (validationMap.get(clazz) != null) {
			return true;
		} else {
			initValidatorRulez(clazz);
			return hasValidator(clazz);
		}
	}

	private synchronized void initValidatorRulez(Class<?> clazz) {
		Field[] fieldList = clazz.getDeclaredFields();
		boolean[] validationFlags = new boolean[fieldList.length];
		ColumnValidator[] columnValidators = new ColumnValidator[fieldList.length];
		boolean hasValidator = false;

		boolean validarEntidade = !ColumnMessageUtil.getInstance().naoValidar(clazz.getName());
		if (validarEntidade) {
			for (int i = 0; i < fieldList.length; i++) {
				Class<?> type = fieldList[i].getType();
				Column col = fieldList[i].getAnnotation(Column.class);
				JoinColumn joinColun = null;
				if (col == null) {
					Method method = ReflectionUtil.getGetterMethod(clazz, fieldList[i].getName());
					if (method != null) {
						col = method.getAnnotation(Column.class);
						type = method.getReturnType();
						if (col == null) {
							joinColun = method.getAnnotation(JoinColumn.class);
						}
					}
				}

				boolean naoValidarColuna = ColumnMessageUtil.getInstance().naoValidar(clazz.getName(), fieldList[i].getName());

				if (naoValidarColuna) {
					validationFlags[i] = false;
					columnValidators[i] = null;
				} else {
					if (col != null && !ReflectionUtil.isId(clazz, fieldList[i])) {
						validationFlags[i] = true;
						ColumnValidator colValidator = new ColumnValidator(clazz.getName(), fieldList[i].getName(), type, col.precision(), col.scale(), col.length(), col.nullable());
						columnValidators[i] = colValidator;
						hasValidator = true;
					} else {
						if (joinColun != null && !joinColun.nullable()) {
							validationFlags[i] = true;
							ColumnValidator colValidator = new ColumnValidator(clazz.getName(), fieldList[i].getName(), type, joinColun.nullable(), true);
							columnValidators[i] = colValidator;
							hasValidator = true;
						} else {
							validationFlags[i] = false;
							columnValidators[i] = null;
						}
					}
				}
			}
		}

		if (hasValidator && validarEntidade) {
			validationMap.put(clazz, new EntityClassValidator(clazz.getName(), validationFlags, columnValidators));
		} else {
			notValidationMap.add(clazz);
		}
	}
}

class EntityClassValidator {
	String className;
	boolean[] validationFlags;
	ColumnValidator[] columnValidators;

	public EntityClassValidator(String className, boolean[] validationFlags, ColumnValidator[] columnValidators) {
		super();
		this.className = className;
		this.validationFlags = validationFlags;
		this.columnValidators = columnValidators;
	}

	public String validate(Object value) {
		if (value == null)
			return null;
		StringBuilder b = new StringBuilder();
		for (int i = 0; i < validationFlags.length; i++) {
			if (validationFlags[i] == false)
				continue;
			b.append(columnValidators[i].validateWithMessage(ReflectionUtil.executeGetterMethod(value, columnValidators[i].columnName)));
		}
		return b.toString();
	}
}

class ColumnValidator {
	String entityName;
	String columnName;
	String humanName;
	Class<?> type;
	int precision;
	int scale;
	int length;
	boolean nullable;
	boolean joinColun;

	public ColumnValidator(String entityName, String columnName, Class<?> type, int precision, int scale, int length, boolean nulls) {
		super();
		this.entityName = entityName;
		this.columnName = columnName;
		this.type = type;
		this.precision = precision;
		this.scale = scale;
		this.length = length;
		this.nullable = nulls;
		this.humanName = StringHelper.getHumanNameFromJavaName(columnName);
	}

	public ColumnValidator(String entityName, String columnName, Class<?> type, boolean nulls, boolean isJoinColun) {
		super();
		this.entityName = entityName;
		this.columnName = columnName;
		this.type = type;
		this.nullable = nulls;
		this.joinColun = true;
	}

	public boolean validate(Object value) {
		return false;
	}

	public String validateWithMessage(Object value) {
		StringBuilder b = new StringBuilder();
		// check de nullo
		if (!nullable) {
			boolean erro = value == null;
			if (joinColun && !erro) {
				// pegar pelo Id
				if (value instanceof BasicEntityObject) {
					erro = ((BasicEntityObject) value).isIdNull();
				}
			}

			if (erro) {
				String msgCustomizada = ColumnMessageUtil.getInstance().getMensagem(entityName, columnName);
				if (StringHelper.isEmptyTrim(msgCustomizada)) {
					msgCustomizada = "O campo " + StringHelper.getHumanNameFromJavaName(columnName) + " é obrigatório.";
				}

				return Constants.K_FORMAT_ERROR_LI_START + msgCustomizada + Constants.K_FORMAT_ERROR_LI_END;
			}
		}

		if (value != null) {
			// dependendo o tipo, verificar a precisão
			if (Double.class == type) {
				checkNumeric(b, (Double) value);
			} else if (Float.class == type) {
				checkNumeric(b, (Float) value);
			} else if (BigDecimal.class == type) {
				checkNumeric(b, (BigDecimal) value);
			} else if (String.class == type) {
				checkString(b, (String) value);
			}
		}
		return b.toString();
	}

	private boolean checkString(StringBuilder b, String value) {
		if (value.trim().length() == 0 && !nullable) {
			String msgCustomizada = ColumnMessageUtil.getInstance().getMensagem(entityName, columnName);
			if (StringHelper.isEmptyTrim(msgCustomizada)) {
				msgCustomizada = "O campo " + StringHelper.getHumanNameFromJavaName(columnName) + " é obrigatório.";
			}
			b.append(Constants.K_FORMAT_ERROR_LI_START).append(msgCustomizada).append(Constants.K_FORMAT_ERROR_LI_END);
			return false;
		} else {
			if (value.length() > length) {
				b.append(Constants.K_FORMAT_ERROR_LI_START);
				b.append("O tamanho máximo para o campo ");
				b.append(humanName);
				b.append(" é ").append(length);
				b.append(" e o tamanho atual é ").append(value.length()).append(".");
				b.append(Constants.K_FORMAT_ERROR_LI_END);
				return false;
			} else
				return true;
		}
	}

	private boolean checkNumeric(StringBuilder b, BigDecimal value) {
		if ((value.precision() - value.scale()) > (precision - scale)) {
			b.append(Constants.K_FORMAT_ERROR_LI_START);
			b.append("Valor numérico inválido para o campo ");
			b.append(humanName);
			b.append(" - a quantidade máxima de casas permitida ").append(precision - scale);
			b.append(" e a quantidade atual é ").append(value.precision() - value.scale()).append(".");
			b.append(Constants.K_FORMAT_ERROR_LI_END);
			return false;
		} else
			return true;
	}

	private void checkNumeric(StringBuilder b, Double value) {
		checkNumeric(b, new BigDecimal(value));
	}

	private void checkNumeric(StringBuilder b, Float value) {
		checkNumeric(b, new BigDecimal(value));
	}

	public static boolean validateThisType(Class<?> type) {
		if (Double.class == type || Float.class == type || BigDecimal.class == type || String.class == type) {
			return true;
		} else {
			return false;
		}

	}
}
