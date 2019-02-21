package br.com.cssis.dominio.cadastro.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * The persistent class for the TMP_BASE_FARMACIAS_CONV database table.
 * 
 */
@Entity
@Table(name = "TMP_BASE_FARMACIAS_CONV")
public class TmpBaseFarmaciasConv implements CadastroEntity {

	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "LOGRADOURO")
	private String logradouro;
	
	@Column(name = "CEP")
	private String cep;

	@Column(name = "ACS")
	private String acs;

	@Column(name = "BAIRRO")
	private String bairro;

	@Column(name = "CIDADE")
	private String cidade;

	@Column(name = "CNPJ")
	private String cnpj;

	@Column(name = "CNPJ_PREFIXO")
	private String cnpjPrefixo;

	@Column(name = "COMPLEMENTO")
	private String complemento;

	@Column(name = "DOM")
	private String dom;

	@Column(name = "E_MAIL")
	private String eMail;

	@Column(name = "FAIXA_ETARIA")
	private String faixaEtaria;

	@Column(name = "GERENTE_GRUPO_DE_LOJA")
	private String gerenteGrupoDeLoja;

	@Column(name = "GERENTE_REGIONAL")
	private String gerenteRegional;

	@Column(name = "INAUGURACAO")
	private String inauguracao;

	@Column(name = "INSC_ESTADUAL")
	private String inscEstadual;

	@Column(name = "NOME_LOJA")
	private String nomeLoja;

	@Column(name = "OBSERVACAO")
	private String observacao;

	@Column(name = "RADIO")
	private String radio;

	@Column(name = "REFERENCIA")
	private String referencia;

	@Column(name = "SAB")
	private String sab;

	@Column(name = "SEG_A_SEX")
	private String segASex;

	@Column(name = "SIGLA_LOJA")
	private String siglaLoja;

	@Column(name = "SITE")
	private String site;

	@Column(name = "TELEFONE_1")
	private String telefone1;

	@Column(name = "TELEFONE_2")
	private String telefone2;

	@Column(name = "UF")
	private String uf;

	public String getAcs() {
		return this.acs;
	}

	public void setAcs(String acs) {
		this.acs = acs;
	}

	public String getBairro() {
		return this.bairro;
	}

	public void setBairro(String bairro) {
		this.bairro = bairro;
	}

	public String getCep() {
		return this.cep;
	}

	public void setCep(String cep) {
		this.cep = cep;
	}

	public String getCidade() {
		return this.cidade;
	}

	public void setCidade(String cidade) {
		this.cidade = cidade;
	}

	public String getCnpj() {
		return this.cnpj;
	}

	public void setCnpj(String cnpj) {
		this.cnpj = cnpj;
	}

	public String getCnpjPrefixo() {
		return this.cnpjPrefixo;
	}

	public void setCnpjPrefixo(String cnpjPrefixo) {
		this.cnpjPrefixo = cnpjPrefixo;
	}

	public String getComplemento() {
		return this.complemento;
	}

	public void setComplemento(String complemento) {
		this.complemento = complemento;
	}

	public String getDom() {
		return this.dom;
	}

	public void setDom(String dom) {
		this.dom = dom;
	}

	public String getEMail() {
		return this.eMail;
	}

	public void setEMail(String eMail) {
		this.eMail = eMail;
	}

	public String getFaixaEtaria() {
		return this.faixaEtaria;
	}

	public void setFaixaEtaria(String faixaEtaria) {
		this.faixaEtaria = faixaEtaria;
	}

	public String getGerenteGrupoDeLoja() {
		return this.gerenteGrupoDeLoja;
	}

	public void setGerenteGrupoDeLoja(String gerenteGrupoDeLoja) {
		this.gerenteGrupoDeLoja = gerenteGrupoDeLoja;
	}

	public String getGerenteRegional() {
		return this.gerenteRegional;
	}

	public void setGerenteRegional(String gerenteRegional) {
		this.gerenteRegional = gerenteRegional;
	}

	public String getInauguracao() {
		return this.inauguracao;
	}

	public void setInauguracao(String inauguracao) {
		this.inauguracao = inauguracao;
	}

	public String getInscEstadual() {
		return this.inscEstadual;
	}

	public void setInscEstadual(String inscEstadual) {
		this.inscEstadual = inscEstadual;
	}

	public String getLogradouro() {
		return this.logradouro;
	}

	public void setLogradouro(String logradouro) {
		this.logradouro = logradouro;
	}

	public String getNomeLoja() {
		return this.nomeLoja;
	}

	public void setNomeLoja(String nomeLoja) {
		this.nomeLoja = nomeLoja;
	}

	public String getObservacao() {
		return this.observacao;
	}

	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}

	public String getRadio() {
		return this.radio;
	}

	public void setRadio(String radio) {
		this.radio = radio;
	}

	public String getReferencia() {
		return this.referencia;
	}

	public void setReferencia(String referencia) {
		this.referencia = referencia;
	}

	public String getSab() {
		return this.sab;
	}

	public void setSab(String sab) {
		this.sab = sab;
	}

	public String getSegASex() {
		return this.segASex;
	}

	public void setSegASex(String segASex) {
		this.segASex = segASex;
	}

	public String getSiglaLoja() {
		return this.siglaLoja;
	}

	public void setSiglaLoja(String siglaLoja) {
		this.siglaLoja = siglaLoja;
	}

	public String getSite() {
		return this.site;
	}

	public void setSite(String site) {
		this.site = site;
	}

	public String getTelefone1() {
		return this.telefone1;
	}

	public void setTelefone1(String telefone1) {
		this.telefone1 = telefone1;
	}

	public String getTelefone2() {
		return this.telefone2;
	}

	public void setTelefone2(String telefone2) {
		this.telefone2 = telefone2;
	}

	public String getUf() {
		return this.uf;
	}

	public void setUf(String uf) {
		this.uf = uf;
	}

	@Override
	public boolean isReentrant() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setReentrant(boolean flag) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isIdNull() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isDoNotValidate() {
		// TODO Auto-generated method stub
		return false;
	}
}