package br.com.cssis.webservice.result;

public class MensagemRC extends BaseDTO {
	private static final long serialVersionUID = 1665390542624488261L;

	/**
	 * <b>RC:</b><br>
	 * PN = Parametro Nulo(Obrigatório), PI = Parametro Invalido, EP = Erro Processamento<br>
	 */

	// ******* ERROS DE PROCESSAMENTO *******
	public static final String RC_PROC_ERRO_GENERICO = "EP001";
	public static final String RC_PROC_ACESSO_NEGADO = "EP002";	
	public static final String RC_PROC_ERRO_IMPLANTACAO_TIPO_FORMA_PAGAMENTO_NAO_CONFIGURADO = "EP003";
	public static final String RC_PROC_ERRO_IMPLANTACAO = "EP004";
	public static final String RC_PROC_ERRO_IMPLANTACAO_JA_REALIZADA_ANTERIORMENTE = "EP005";
	public static final String RC_PROC_ERRO_IMPLANTACAO_NAO_REALIZADA = "EP006";
	public static final String RC_PROC_ERRO_IMPLANTACAO_DIFERENTE_PF_NAO_AUTORIZADA = "EP007";
	public static final String RC_PROC_ERRO_QUANTIDADE_VIDAS_IMPLANTACAO_NAO_ATINGIDA = "EP008";
	public static final String RC_PROC_ERRO_GRUPO_FAMILIAR_CANCELADO = "EP009";
	public static final String RC_PROC_ERRO_BUSCA_PROPOSTA_COMERCIAL = "EP010";
	public static final String RC_PROC_ERRO_GRUPO_FAMILIAR_MOVIMENTACAO_FUTURA_CANCELAMENTO = "EP011";
	
	// ******* ERROS DE VALIDAÇÕES *******	
	public static final String RC_PARM_OBRIG_ID_OPERADORA = "PN001";
	public static final String RC_PARM_INVAL_ID_OPERADORA = "PI001";
	public static final String RC_PARM_OBRIG_ID_SUBESTIPULANTE = "PN002";
	public static final String RC_PARM_INVAL_ID_SUBESTIPULANTE = "PI002";
	public static final String RC_PARM_OBRIG_CNPJ = "PN003";
	public static final String RC_PARM_INVAL_CNPJ = "PI003";
	public static final String RC_PARM_OBRIG_MATRICULA_EXTERNA = "PN004";
	public static final String RC_PARM_INVAL_MATRICULA_EXTERNA = "PI004";
	public static final String RC_PARM_OBRIG_CODIGO_SEGURADO = "PN005";
	public static final String RC_PARM_INVAL_CODIGO_SEGURADO = "PI005";
	public static final String RC_PARM_OBRIG_DATA_INICIO = "PN006";
	public static final String RC_PARM_INVAL_DATA_INICIO = "PI006";
	public static final String RC_PARM_OBRIG_MOTIVO = "PN007";
	public static final String RC_PARM_INVAL_MOTIVO = "PI007";
	public static final String RC_PARM_OBRIG_ID_PLANO = "PN008";
	public static final String RC_PARM_INVAL_ID_PLANO = "PI008";
	public static final String RC_PARM_OBRIG_ID_TIPO_CARENCIA = "PN009";
	public static final String RC_PARM_INVAL_ID_TIPO_CARENCIA = "PI009";
	public static final String RC_PARM_OBRIG_ID_TIPO_CALC_MENS = "PN010";
	public static final String RC_PARM_INVAL_ID_TIPO_CALC_MENS = "PI010";
	public static final String RC_PARM_OBRIG_ID_TIPO_FORMA_PAGTO = "PN011";
	public static final String RC_PARM_INVAL_ID_TIPO_FORMA_PAGTO = "PI011";
	public static final String RC_PARM_OBRIG_DATA_INICIO_VIGENCIA = "PN012";
	public static final String RC_PARM_INVAL_DATA_INICIO_VIGENCIA = "PI012";
	public static final String RC_PARM_OBRIG_DATA_FIM_VIGENCIA = "PN013";
	public static final String RC_PARM_INVAL_DATA_FIM_VIGENCIA = "PI013";
	public static final String RC_PARM_OBRIG_ID_TIPO_PARENTESCO = "PN014";
	public static final String RC_PARM_INVAL_ID_TIPO_PARENTESCO = "PI014";
	public static final String RC_PARM_OBRIG_ISENTO_CARENCIA = "PN015";
	public static final String RC_PARM_INVAL_ISENTO_CARENCIA = "PI015";
	public static final String RC_PARM_OBRIG_REATIVAR_DEPENDENTES = "PN016";
	public static final String RC_PARM_INVAL_REATIVAR_DEPENDENTES = "PI016";
	public static final String RC_PARM_OBRIG_DATA_INICIO_VIGENCIA_OU_DATA_FIM_VIGENCIA = "PN017";
	public static final String RC_PARM_INVAL_DATA_INICIO_VIGENCIA_OU_DATA_FIM_VIGENCIA = "PI017";
	
	public static final String RC_PARM_OBRIG_CPF_SEGURADO = "PN018";
	public static final String RC_PARM_INVAL_CPF_SEGURADO = "PI018";
	public static final String RC_PARM_OBRIG_CPF_SEGURADO_ID_SUBESTIPULANTE = "PN020";
	public static final String RC_PARM_INVAL_CPF_SEGURADO_ID_SUBESTIPULANTE = "PI020";
	public static final String RC_PARM_OBRIG_MATRICULA_EXTERNA_E_ID_SUBESTIPULANTE = "PN021";
	public static final String RC_PARM_INVAL_MATRICULA_EXTERNA_E_ID_SUBESTIPULANTE = "PI021";

	private String codigo;
	private String mensagem;

	public MensagemRC() {
	}

	public MensagemRC(String codigo, String mensagem) {
		super();
		this.codigo = codigo;
		this.mensagem = mensagem;
	}

	public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	public String getMensagem() {
		return mensagem;
	}

	public void setMensagem(String mensagem) {
		this.mensagem = mensagem;
	}

	@Override
	public String toString() {
		StringBuilder s = new StringBuilder();
		s.append(descreverBean());
		return s.toString();
	}
}
