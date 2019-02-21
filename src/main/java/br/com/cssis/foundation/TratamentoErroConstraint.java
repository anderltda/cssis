package br.com.cssis.foundation;

import br.com.cssis.foundation.util.ReflectionUtil;
import br.com.cssis.foundation.util.StringHelper;

//CREATE TABLE TRATAMENTO_ERRO_CONSTRAINT (
//		NOME_CONSTRAINT VARCHAR2(100) NOT NULL,
//		MENSAGEM VARCHAR2(400) NOT NULL,
//		MENSAGEM_ADICIONAL VARCHAR2(400) ,
//		ENTIDADE_PAI VARCHAR2(100) ,
//		ENTIDADE_FILHA VARCHAR2(100) ,
//		ID NUMBER(10) )
//ALTER TABLE TRATAMENTO_ERRO_CONSTRAINT
//add constraint PK_TRATAMENTO_ERRO_CONSTRAINT primary key (NOME_CONSTRAINT);
public class TratamentoErroConstraint extends BasicObject {
	private static final long serialVersionUID = 1L;
	protected String nomeConstraint;
	protected String mensagem;
	protected String mensagemAdicional;
	protected String entidadePai;
	protected String entidadeFilha;
	protected Long id;

	public String getNomeConstraint() {
		return nomeConstraint;
	}

	public void setNomeConstraint(String nomeConstraint) {
		this.nomeConstraint = nomeConstraint;
	}

	public String getMensagem() {
		return mensagem;
	}

	public void setMensagem(String mensagem) {
		this.mensagem = StringHelper.removeControlChar(mensagem);
	}

	public String getMensagemAdicional() {
		return mensagemAdicional;
	}

	public void setMensagemAdicional(String mensagemAdicional) {
		this.mensagemAdicional = mensagemAdicional;
	}

	public String getEntidadePai() {
		return entidadePai;
	}

	public void setEntidadePai(String entidadePai) {
		this.entidadePai = entidadePai;
	}

	public String getEntidadeFilha() {
		return entidadeFilha;
	}

	public void setEntidadeFilha(String entidadeFilha) {
		this.entidadeFilha = entidadeFilha;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return ReflectionUtil.getFieldListAndValues(this);
	}
}
