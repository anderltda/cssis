package br.com.cssis.foundation;

import java.sql.SQLException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import br.com.cssis.foundation.util.StringHelper;

public class CustomSQLExceptionHandler {
	public static ErrorMessage handleSQLException(SQLException sqle) {
		if (sqle == null)
			return new ErrorMessage(sqle);
		switch (sqle.getErrorCode()) {
		case OracleErrorConstants.ORA_12899:
			return handleOra12899(sqle);
		case OracleErrorConstants.ORA_01438:
			return handleOra1438(sqle);
		case OracleErrorConstants.ORA_01400:
			return handleOra1400(sqle);
		case OracleErrorConstants.ORA_00001:
			return handleOra00001(sqle);
		case OracleErrorConstants.ORA_20000:
			return handleOra20000(sqle);
		default:
			return handleOra(sqle);
		}
	}

	public static void main(String[] args) {
		System.out.println(StringHelper.getTokens("TEste (MSG ERROR) 123..", "\\((.*?\\))"));
	}
	
	private static ErrorMessage findOraErrorMsg(SQLException sqle) {
		ErrorMessage returnValue = new ErrorMessage(sqle);
		List<String> tokens = StringHelper.getTokens(sqle.getMessage(), "(\".*?\")|\\((.*?\\))");
		String constraintName = "" ;
		if (tokens.size() > 0) {
			constraintName = tokens.get(0).replaceAll("\\(|\\)", "");
		}
		
		TratamentoErroConstraint mappedConstraint = ExceptionHandler.getInstance().getConstraintMap().get(constraintName);
		// Existe tratamento mapeado cadastrado
		if (mappedConstraint != null) {
			StringBuilder builder = new StringBuilder();
			builder.append(mappedConstraint.getMensagem());
			if (StringHelper.nonEmpty(mappedConstraint.getMensagemAdicional())) {
				builder.append(" - ").append(mappedConstraint.getMensagemAdicional());
			}
			returnValue.setMessage(builder.toString());

			builder = new StringBuilder();
			builder.append("Nome da Constraint:").append(mappedConstraint.getNomeConstraint());
			if (StringHelper.nonEmpty(mappedConstraint.getEntidadePai())) {
				builder.append(" Entidade Pai:").append(mappedConstraint.getEntidadePai());
			}
			if (StringHelper.nonEmpty(mappedConstraint.getEntidadeFilha())) {
				builder.append(" Entidade Filha:").append(mappedConstraint.getEntidadeFilha());
			}
			returnValue.setErrorCode("" + sqle.getErrorCode());
			returnValue.setExtraInformation(builder.toString());
		} else {
			// nao existe tramento padrão cadastrado, tratar o erro sql normalmente
			returnValue.setMessage("Constraint :" + constraintName + " violada.");
		}

		return returnValue;
	}
	
	private static ErrorMessage handleOra00001(SQLException sqle) {
		return findOraErrorMsg(sqle);
	}
	
	private static ErrorMessage handleOra20000(SQLException sqle) {
		return findOraErrorMsg(sqle);
	}

	private static ErrorMessage handleOra1400(SQLException sqle) {
		List<String> tokens = StringHelper.getTokens(sqle.getMessage(), "\"(.*?)\"");
		String coluna = "";
		if (tokens.size() > 2) {
			coluna = tokens.get(2);
		}
		coluna = StringHelper.getHumanNameFromDBName(coluna);
		ErrorMessage error = new ErrorMessage(OracleErrorConstants.ORA_01400_MSG.replaceAll(":columnName", coluna), sqle);
		return error;
	}

	private static ErrorMessage handleOra1438(SQLException sqle) {
		ErrorMessage error = new ErrorMessage(OracleErrorConstants.ORA_01438_MSG, sqle);
		return error;
	}

	private static ErrorMessage handleOra(SQLException sqle) {
		return new ErrorMessage(sqle.getMessage().replaceAll("\"", "").replaceAll("\n", ""), sqle);
	}

	private static ErrorMessage handleOra12899(SQLException sqle) {
		List<String> tokens = StringHelper.getTokens(sqle.getMessage(), "\"(.*?)\"");
		String coluna = "";
		String entidade = "";
		if (tokens.size() > 2) {
			coluna = tokens.get(2);
			entidade = tokens.get(1);
		}
		
		tokens = StringHelper.getTokens(sqle.getMessage(), "actual: (.*?),|maximum: (.*?)\\)");
		Pattern pattern = Pattern.compile("actual: (.*?),|maximum: (.*?)\\)");
		Matcher matcher = pattern.matcher(sqle.getMessage());
		int maxAllowed = 0, currentSize = 0;
		int counter = 0;
		if (matcher.groupCount() > 0) {
			while (matcher.find()) {
				for (int i = 1; i <= matcher.groupCount(); i++) {
					if (matcher.group(i) != null) {
						if (counter == 0)
							maxAllowed = Integer.valueOf(matcher.group(i)).intValue();
						else if (counter == 1)
							currentSize = Integer.valueOf(matcher.group(i)).intValue();
					}
				}
				counter++;
			}
		}
		ErrorMessage error = new ErrorMessage(OracleErrorConstants.ORA_12899_MSG.replace(":columnName", coluna).replaceAll(":currentSize", "" + currentSize).replaceAll(":maxSize", "" + maxAllowed).replaceAll(":entityName", entidade), sqle);
		return error;
	}

}
