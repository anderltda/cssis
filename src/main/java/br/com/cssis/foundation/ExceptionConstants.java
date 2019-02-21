package br.com.cssis.foundation;

public class ExceptionConstants {
	public static final long METHOD_NOT_IMPLEMENTED = 50;

	
	public static final long SQL_INVALID_CRITERIA = 100;
	public static final long SQL_ERROR_CONNECTION = 101;	
	public static final long SQL_INVALID_QUERY_NAME = 102;
	public static final long SQL_INVALID_SQL_TEXT = 103;
	public static final long SQL_INVALID_CONNECTION = 104;
	public static final long SQL_EXECUTION_ERROR = 105;
	public static final long SQL_TRANSPORTER_ERROR = 106;
	public static final long SQL_GENERAL_ERROR = 107;
	public static final long SQL_INVALID_COLUMN_NAME = 108;
	public static final long SQL_BIND_ERROR = 109;	
	
	public static final long PAGINATOR_INVALID_PAGE = 110;
	
	public static final long K_INVALID_PK_VALUE = 111;
	public static final long K_INVALID_FIELD_VALUE = 112;
	public static final long K_TOO_MANY_RECORDS = 113;
	public static final long K_EMPTY_PARAMETERS_SEARCH = 114;
	public static final long K_VALUE_OBJECT_OUT_OF_DATE = 115;
	public static final long K_STMT_RETURNED_NO_ROWS = 116;
	
	public static final long K_NOCREDENTIALS = 117;
	public static final String K_NOCREDENTIALS_MSG = "Para acessar esta transação é necessário escolher uma Operadora";
	public static final String K_NOCREDENTIALS_WRONGDOMAIN = "Erro de Segurança. Acesso negado tentando acesso à Operadora diferente da Operadora logado.";
	
	public static final long K_NOSINGLE_RECORDFOUND = 118;
	public static final long K_DUPLICATE_RECORD = 119;
	public static final long HTTP_COMUNICATION_ERROR = 120;
}

