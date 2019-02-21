package br.com.cssis.foundation;

public class OracleErrorConstants {
	public static final int ORA_12899 = 12899;
	public static final String ORA_12899_MSG = "Valor muito grande especificado para o campo :columnName (recebido :currentSize, permitido :maxSize) para a Entidade :entityName";
	
	public static final int ORA_01438 = 1438;
	public static final String ORA_01438_MSG = "Valor numérico incorreto. Verificar a precisão/casas decimais.";
	
	public static final String ORA_01400_MSG = "O campo :columnName é obrigatório";
	public static final int ORA_01400 = 1400;
	
	public static final String ORA_20000_MSG = "A alteração solicitada <b>:msg_ora<b> não é permitida.";
	public static final int ORA_20000 = 20000;

	public static final int ORA_00001 = 1;	
}
