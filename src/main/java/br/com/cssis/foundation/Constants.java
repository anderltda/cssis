package br.com.cssis.foundation;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.beanutils.ConvertUtils;

public class Constants {
	public static final String KEY_EXCEPTION_HANDLER = "exception.handler";
	public static final String KEY_EXCEPTION_HANDLER_INPUT = "exception.handler.input";
	public static final String KEY_EXCEPTION_HANDLER_PS = "exception.handler.persistenceService";
	public static final String KEY_EXCEPTION_HANDLER_ERROR_QUERY = "exception.handler.errorQuery";

	public static final String KEY_QUERY_HANDLER = "query.handler";
	public static final String KEY_QUERY_HANDLER_INPUT = "query.handler.input";

	public static final String KEY_QUERY_RUNNER_FORCE_ST = "query.runner.forceSQLStatements";
	public static final String KEY_QUERY_RUNNER_DEFAULT_COUNT = "query.runner.defaultRecordCount";
	public static final String KEY_QUERY_RUNNER_DEFAULT_TRANSPORTER = "query.runner.defaultTransporterClass";
	public static final String KEY_QUERY_RUNNER_DEFAULT_DS = "query.runner.defaultDS";
	public static final String KEY_QUERY_LIMIT = "query.limit.size";
	public static final String KEY_QUERY_PREPAREDST_PARAMNAME = "query.preparedSt.paramName";
	public static final String KEY_QUERY_PREPAREDST_TYPE = "query.preparedSt.type";

	public static final String KEY_PAGINATOR_DEFAULT_SIZE = "paginator.defaultSize";
	public static final String KEY_PAGINATOR_CACHE_POLICY = "paginator.cachePolicy";
	public static final String KEY_PAGINATOR_FETCHER_NAME = "paginator.fetcher.name";
	public static final String KEY_PAGINATOR_FETCHER_PS = "paginator.fetcher.persistenceService";
	public static final String KEY_PAGINATOR_CUSTOMPAGESIZE_FILE = "paginator.customPageSize.file";

	public static final String KEY_PROPERTY_NAME_SPLITTER = "string.property.name.splitter";
	public static final String KEY_PROPERTY_SET = "string.property.set";
	public static final String KEY_PROPERTY_GET = "string.property.get";

	public static final String KEY_MASCARA_TELEFONE = "string.telefone.mascara";
	public static final String KEY_MASCARA_TELEFONECOM0800 = "string.telefone.0800.mascara";
	public static final String KEY_MASCARA_DDD = "string.telefone.ddd.mascara";

	public static final String KEY_MASCARA_TELEFONE_MSG = "string.telefone.mascara.msg";
	public static final String KEY_MASCARA_TELEFONECOM0800_MSG = "string.telefone.0800.mascara.msg";
	public static final String KEY_MASCARA_DDD_MSG = "string.telefone.ddd.mascara.msg";

	public static final String KEY_QUERY_CUSTOM_TYPES_MAPPING = "query.handler.customTypesMapping";
	public static final String KEY_QUERY_STANDARD_TYPES_MAPPING = "query.handler.standardTypesMapping";

	public static final String KEY_QUERY_LOGGER_ENABLED = "query.logger.enabled";
	public static final String KEY_QUERY_LOGGER_RELOAD_TIME = "query.logger.reloadConfigFileTime";
	public static final String KEY_QUERY_LOGGER_FLUSH_SIZE = "query.logger.flushThreesholdSize";
	public static final String KEY_QUERY_LOGGER_TARGET_FILE = "query.logger.destinationFile";
	public static final String KEY_QUERY_LOGGER_APP_PREVIOUS = "query.logger.appendPreviousContent";
	public static final String KEY_QUERY_LOGGER_TIME_LOG = "query.logger.timeForLogging";
	public static final String KEY_QUERY_LOGGER_LOG_SEPARATOR = "query.logger.defaultLogSeparator";
	public static final String KEY_QUERY_LOGGER_LINE_SEPARATOR = "query.logger.lineSeparator";
	public static final String KEY_QUERY_LOGGER_DATE_FORMAT = "query.logger.dateFormat";
	public static final String KEY_QUERY_LOGGER_LOG_ROTATION_FORMAT = "query.logger.logRotationDateFormat";
	public static final String KEY_QUERY_LOGGER_LOG_ROTATION_SIZE = "query.logger.logSizeRotation";

	public static final String KEY_MAPLINK_ADDRESSFINDER_URL = "maplink.addressfinder.url";
	public static final String KEY_MAPLINK_ADDRESSFINDER_TOKEN = "maplink.addressfinder.token";

	public static final String KEY_GLOBAL_COLUMN_VALIDATOR = "validator.GlobalColumnValidator.on";
	public static final String KEY_AUDITING_ON = "auditing.on";
	public static final String KEY_AUDITING_SERVICE = "auditing.serviceName";

	public static final String KEY_MATRIZ_OPERADORA_INPUT = "matrizOperadora.customCodes.file";

	public static final int K_BEFORE_INSERT = 1;
	public static final int K_AFTER_INSERT = 2;
	public static final int K_BEFORE_UPDATE = 4;
	public static final int K_AFTER_UPDATE = 8;
	public static final int K_BEFORE_DELETE = 16;
	public static final int K_AFTER_DELETE = 32;
	public static final int K_BEFORE_GET = 64;
	public static final int K_AFTER_GET = 128;

	public static final int K_ANY = 255;

	public static final String K_INSERT = "I";
	public static final String K_UPDATE = "U";
	public static final String K_DELETE = "D";
	public static final String K_GET = "G";

	public static final String K_INSERT_ACTION = "create";
	public static final String K_UPDATE_ACTION = "edit";
	public static final String K_DELETE_ACTION = "delete";
	public static final String K_GET_ACTION = "view";

	public static final String K_LOGGED_USER = "LOGGED_USER";
	public static final String K_LOGGED_USER_ORIGEM_OPERACAO = "LOGGED_USER_ORIGEM_OPERACAO";
	public static final String K_LOGGED_USER_PERMITIR_REEMBOLSO = "K_LOGGED_USER_PERMITIR_REEMBOLSO";
	public static final String K_LOGGED_USER_BLOCKED_ACCESS = "LOGGED_USER_BLOCKED_ACCESS";
	public static final String LOGGED_USER_CONTRATOS = "LOGGED_USER_CONTRATOS";
	public static final String K_LOGGED_USER_OPERADORAS = "LOGGED_USER_OPERADORAS";
	public static final String K_LOGGED_USER_DOMAIN = "LOGGED_USER_DOMAIN";
	public static final String K_LOGGED_USER_OPERADORA = "LOGGED_USER_OPERADORA";
	public static final String K_LOGGED_USER_MATRIZOPERADORA = "LOGGED_USER_MATRIZOPERADORA";
	public static final String K_LOGGED_USER_RESTRICOES = "LOGGED_USER_RESTRICOES";
	public static final String K_LOGGED_USER_SISTEMA = "LOGGED_USER_SISTEMA";
	public static final String K_LOGGED_USER_MODULO = "LOGGED_USER_MODULO";
	public static final String K_LOGGED_USER_CACHEDTRANSACTIONS = "LOGGED_USER_CACHEDTRANSACTIONS";
	public static final String K_LOGGED_USER_LOGOTOP = "LOGGED_USER_LOGODOMINIO";
	public static final String K_LOGGED_USER_ERROR = "LOGGED_USED_ERROR";
	public static final String K_LOGGED_USER_MENU = "LOGGED_USED_MENU";
	public static final String K_LOGGED_USER_ATALHORAPIDO = "LOGGED_USED_ATALHORAPIDO";
	public static final String K_LOGGED_USER_NROPA = "LOGGED_USER_NROPA";//Número do PA do usuário logado.
	
	public static final String K_FORMAT_ERROR_LI_START = "<li class='error'>";
	public static final String K_FORMAT_ERROR_LI_END = "</li>";

	public static final String K_LOGGED_USER_LOGOTOP_PADRAO = "n_top02.jpg";

	public static final String K_CHOOSED_SUBESTIP = "CHOOSED_SUBESTIP";
	public static final String K_CHOOSED_SUBESTIP_NAME = "CHOOSED_SUBESTIP_NAME";

	public static final List<Long> EMPTY_LONG_LIST = new ArrayList<Long>();
	public static final Set<Long> EMPTY_LONG_SET = new HashSet<Long>();
	public static final List<Long> NOID_EMPTY_LONG_LIST;

	public static final byte BUSCA_CONTAINS_TAMANHO_MIM_TOKEN = 5;
	public static final byte BUSCA_CONTAINS_TAMANHO_MIM_TOKEN_CORINGA = 3;
	
	public static final byte PRECISAO_CALCULOS = 2;
	public static final byte TAMANHO_NOME_SAP = 35;
	public static final byte TAMANHO_NOME_PESQUISA = 20;
	
	
	public static final String PENDENCIA_CARTEIRINHA_ACOMPANHA_LIVRETO = "N";
	public static final String PENDENCIA_CARTEIRINHA_SOMENTE_LIVRETO = "N";
	
	public static final Long CONVENIO_PADRAO_CAIXA = 20L;
	
	public static BigDecimal K_BD_ZERO;
	
	public static final int LIMITE_CARACTERES_SMS = 147;
	
	public static String[] IGNORE_ERRORS = new String[]{"THE SPECIFIED CALL COUNT IS NOT A NUMBER"};
	
	static {
		NOID_EMPTY_LONG_LIST = new ArrayList<Long>();
		NOID_EMPTY_LONG_LIST.add(-1l);
		
		K_BD_ZERO = (BigDecimal) ConvertUtils.convert(0, BigDecimal.class);
		K_BD_ZERO.setScale(Constants.PRECISAO_CALCULOS, RoundingMode.HALF_UP);
	}

	public static final Map<String, String> GOOGLE_MAPS_KEYS = new HashMap<String, String>();
	static {
		GOOGLE_MAPS_KEYS.put("localhost", "ABQIAAAALJuSakjqNHWBe4Ed8pybGRT2yXp_ZAY8_ufC3CFXhHIE1NvwkxTlY91zKPsHyjDNA4hZ9Hc72GEe4w");
		GOOGLE_MAPS_KEYS.put("10.11.1.2", "ABQIAAAALJuSakjqNHWBe4Ed8pybGRR0iEgNB_N7Tbk1qce1J1LLQTlGmxSFKt5cvY_FPrh0i3k-9O0OJzRMNA");
		GOOGLE_MAPS_KEYS.put("10.5.1.14", "ABQIAAAALJuSakjqNHWBe4Ed8mapybGRQlS701T8h-nYbj5wV8QtgIYjm8MBSIy4mgch820rFzI5yUBCUpsk2WzQ");
		GOOGLE_MAPS_KEYS.put("10.2.2.162", "ABQIAAAA3ZO259scpN2_rYucvDZaCRQaJ37yjfTBRnt-yOt4OuKiwxOC2hT6IavYrL32Xwjr6KZxP0bN8tcn_w");
//		GOOGLE_MAPS_KEYS.put("10.2.2.162", "ABQIAAAALJuSakjqNHWBe4Ed8pybGRQlS701T8h-nYbj5wV8QtgIYjm8MBSIy4mgch820rFzI5yUBCUpsk2WzQ");
//		GOOGLE_MAPS_KEYS.put("10.2.2.162:8180", "ABQIAAAALJuSakjqNHWBe4Ed8pybGRSqAvsPMfddKOm-LtYhjI66vhJ7shQQUARFdTgZdc34SBMkZfBEmBeKZA");
		GOOGLE_MAPS_KEYS.put("10.11.3.11:8080", "ABQIAAAALJuSakjqNHWBe4Ed8pybGRTxyfQSuBjwVn3sBUGkA8Y29Nx91BTfzhIUeyeU8uR97ou-MHYtNkVTzQ");
		GOOGLE_MAPS_KEYS.put("10.11.3.11", "ABQIAAAALJuSakjqNHWBe4Ed8pybGRSqAvsPMfddKOm-LtYhjI66vhJ7shQQUARFdTgZdc34SBMkZfBEmBeKZA");
		GOOGLE_MAPS_KEYS.put("svn.internal", "ABQIAAAA3ZO259scpN2_rYucvDZaCRQaJ37yjfTBRnt-yOt4OuKiwxOC2hT6IavYrL32Xwjr6KZxP0bN8tcn_w");
		GOOGLE_MAPS_KEYS.put("svn.internal:8180", "ABQIAAAA3ZO259scpN2_rYucvDZaCRQaJ37yjfTBRnt-yOt4OuKiwxOC2hT6IavYrL32Xwjr6KZxP0bN8tcn_w");		
//		GOOGLE_MAPS_KEYS.put("svn.internal", "ABQIAAAALJuSakjqNHWBe4Ed8pybGRSDsBkR4q4tMkkFhNrXZ0HhvDZv2RRxDVOKN2tHK_R1vcWM0G_cUXscAg");
//		GOOGLE_MAPS_KEYS.put("svn.internal:8180", "ABQIAAAALJuSakjqNHWBe4Ed8pybGRSE1Q7GA3v7CRXiSzKw65uHHVCz4RQu1-jLcaUUG9FW-WAY2ZzxND8XFw");
		GOOGLE_MAPS_KEYS.put("10.11.4.78", "ABQIAAAAvQBaafP_9DaB4ng8ZXmUHxQ6hZYTQXcFblG4lLGDCHOHKaKABBTvnwNqA4kba8d6Xy698v0ONYg07w");
		GOOGLE_MAPS_KEYS.put("10.2.0.161", "ABQIAAAA3ZO259scpN2_rYucvDZaCRRUGVPkTMX3srTCTD7TQXgzvB0caxRDFCrF609x0EEGs3UIuhrxVXpfDA");		
		//10.2.0.160
		GOOGLE_MAPS_KEYS.put("wsotempo01", "ABQIAAAA3ZO259scpN2_rYucvDZaCRRBbyTSnIjoxEMtaH4-7C_FbGgynhSto_Sepf7CwD2yHADzcNghB4xAuw");		
		GOOGLE_MAPS_KEYS.put("wsotempo02", "ABQIAAAA3ZO259scpN2_rYucvDZaCRSFep2bae2sdIIAg4ApCqeSkyBsSxQM63OHAHx2s_RCNGBmQn9EYIqrag");
		GOOGLE_MAPS_KEYS.put("odontoutilis", "ABQIAAAA3ZO259scpN2_rYucvDZaCRREDr1kSGDaQqbUmcAS7sCeNTtWUhTxSWGWa_e_XPWabGSGjGiBRuiX2g");
		GOOGLE_MAPS_KEYS.put("200.178.83.146", "ABQIAAAALJuSakjqNHWBe4Ed8pybGRTou939qRnKBYCyEHRs7oS8xZ5epRQt5WoZXXFPR_jSUoxIgNvws43xwg");
		GOOGLE_MAPS_KEYS.put("10.2.0.164", "ABQIAAAALJuSakjqNHWBe4Ed8pybGRSZBqidAcMtk6-eOZHz3zhsid6hABRu5abSxuVF_IyhvCPnxTFNP2MfFQ");
		GOOGLE_MAPS_KEYS.put("asotempo01", "ABQIAAAALJuSakjqNHWBe4Ed8pybGRRCYjomTQAAl77lVUsIaKb5Q5oWixRCINqNAa-Xo9m29f7LfX1ERKkKmw");
		GOOGLE_MAPS_KEYS.put("asotempo02", "ABQIAAAALJuSakjqNHWBe4Ed8pybGRS0rtpQwkub1ztv0TxrPypx7mCJHRSQfM6YBPHf4PpWWVIvfIUKv1jqMg");
		GOOGLE_MAPS_KEYS.put("www.odontoutilis.com.br", "ABQIAAAALJuSakjqNHWBe4Ed8pybGRQVP1OXGl7C4j1LUFssdTBhXkiVcxQ7SSzrO_1yp3TJIRde_SRB2mVMZA");
		GOOGLE_MAPS_KEYS.put("200.178.83.198", "ABQIAAAAHV6SE1zqKvbkzDDRqf6wpxSefILRA6d0ebHw9Dwo2vwLhIgXvBRaMaS7Ye7Fe4dN4m-lH0ad8ir06A");
	}

	public static boolean isBeforeInsert(int value) {
		return (value & K_BEFORE_INSERT) == K_BEFORE_INSERT;
	}

	public static boolean isAfterInsert(int value) {
		return (value & K_AFTER_INSERT) == K_AFTER_INSERT;
	}

	public static boolean isBeforeUpdate(int value) {
		return (value & K_BEFORE_UPDATE) == K_BEFORE_UPDATE;
	}

	public static boolean isAfterUpdate(int value) {
		return (value & K_AFTER_UPDATE) == K_AFTER_UPDATE;
	}

	public static boolean isBeforeDelete(int value) {
		return (value & K_BEFORE_DELETE) == K_BEFORE_DELETE;
	}

	public static boolean isAfterDelete(int value) {
		return (value & K_AFTER_DELETE) == K_AFTER_DELETE;
	}

	public static boolean isBeforeGet(int value) {
		return (value & K_BEFORE_GET) == K_BEFORE_GET;
	}

	public static boolean isAfterGet(int value) {
		return (value & K_AFTER_GET) == K_AFTER_GET;
	}

	public static String printEventName(int value) {
		StringBuilder b = new StringBuilder();
		if (isBeforeDelete(value)) b.append("BEFORE_DELETE").append(" ");
		if (isBeforeUpdate(value)) b.append("BEFORE_UPDATE").append(" ");
		if (isBeforeInsert(value)) b.append("BEFORE_INSERT").append(" ");

		if (isAfterDelete(value)) b.append("AFTER_DELETE").append(" ");
		if (isAfterUpdate(value)) b.append("AFTER_UPDATE").append(" ");
		if (isAfterInsert(value)) b.append("AFTER_INSERT").append(" ");

		return b.toString();
	}

	public static String getEventAction(int value) {
		switch (value) {
		case K_BEFORE_GET:
			return K_GET_ACTION;
		case K_BEFORE_UPDATE:
			return K_UPDATE_ACTION;
		case K_BEFORE_INSERT:
			return K_INSERT_ACTION;

		case K_AFTER_DELETE:
			return K_DELETE_ACTION;
		case K_BEFORE_DELETE:
			return K_DELETE_ACTION;
		case K_AFTER_UPDATE:
			return K_UPDATE_ACTION;
		case K_AFTER_INSERT:
			return K_INSERT_ACTION;
		case K_AFTER_GET:
			return K_GET_ACTION;
		default:
			return "";
		}
	}

	//public static final String K_LOGGED_USER_LOGO_TEMPO_DENTAL = "logo_rede_odonto_empresas_New.png";//Melhoria #26769--atualiza logo da Rede Odonto
	public static final String K_LOGGED_USER_LOGO_OPERADORA_OE = "rede_oe_ans.png ";
	public static final String K_LOGGED_USER_LOGO_OPERADORA_PREV = "prevdonto_ans.png";
	public static final String K_LOGGED_USER_LOGO_OPERADORA_CAIXA = "logo-caixa.png";
	//Melhoria #26769 - Adiciona os logos exclusivos do Afinidades
	public static final String K_LOGGED_USER_LOGO_TEMPO_DENTAL = "rede_oe.png";
	public static final String K_LOGGED_USER_LOGO_OPERADORA_OE_BRANCO = "rede_oe_branco.png";
	public static final String K_LOGGED_USER_LOGO_ANS = "ans.png";
	public static final String K_LOGGED_USER_LOGO_CAIXA_SEGURADORA = "logo_caixa_seguros_topo.png";
	public static final String K_LOGGED_USER_LOGO_PROTECAO_TOTAL_CARREFOUR = "protecao_dental_carrefour.png";
	public static final String K_LOGGED_USER_LOGO_ODONTO_TOTAL = "odonto_total.png";
	public static final String K_LOGGED_USER_LOGO_ODONTO_MAXX = "odonto_maxx.png";
	public static final String K_LOGGED_USER_LOGO_NOSSO_ODONTO ="nosso_odonto.png";
	public static final String K_LOGGED_USER_LOGO_LEOLAR_ODONTO ="leolar_odonto.png";
	public static final String K_LOGGED_USER_LOGO_DENTAL_SIMPLES ="dental_simples.png";
	public static final String K_LOGGED_USER_LOGO_DENTAL_PREMIADO ="dental_premiado.png";
	public static final String K_LOGGED_USER_LOGO_DENTAL_LIGHT_LEADER = "dental_light_leader.png";
	public static final String K_LOGGED_USER_LOGO_ASSISTENCIA_ODONTO = "assistencia_odonto.png";
	public static final String K_LOGGED_USER_LOGO_ASSISTENCIA_ODONTO_FSVAS = "assistencia_odonto_fsvas.png";
	
	// ---------------------------------------------------------------------------------------------
	// TIPO_LOCALIDADE
	// ---------------------------------------------------------------------------------------------
	public static final String K_TIPO_LOC_MUNICIPIO = "M";
	public static final String K_TIPO_LOC_DISTRITO = "D";
	public static final String K_TIPO_LOC_POVOADO = "P";
	public static final String K_TIPO_LOC_REGIAO_ADMINISTRATIVA = "R";
}