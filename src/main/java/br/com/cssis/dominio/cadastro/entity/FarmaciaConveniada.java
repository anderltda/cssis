package br.com.cssis.dominio.cadastro.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import br.com.cssis.foundation.BasicEntityObject;


/**
 * The persistent class for the FARMACIA_CONVENIADA database table.
 * 
 */
@Entity
@Table(name="FARMACIA_CONVENIADA")
public class FarmaciaConveniada extends BasicEntityObject implements CadastroEntity {
	
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="ID_FARMACIA_CONVENIADA")
	private Long id;

	@Column(name="CNPJ")
	private String cnpj;

	@Column(name="CNPJ_PREFIXO")
	private String cnpjPrefixo;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="DATA_ALTERACAO")
	private Date dataAlteracao;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="DATA_CRIACAO")
	private Date dataCriacao;

	@Temporal(TemporalType.DATE)
	@Column(name="DATA_INAUGURACAO")
	private Date dataInauguracao;

	@Column(name="EMAIL")
	private String email;

	@Column(name="ID_USUARIO_ALTERACAO")
	private Long idUsuarioAlteracao;

	@Column(name="ID_USUARIO_CRIACAO")
	private Long idUsuarioCriacao;

	@Column(name="INSCRICAO_ESTADUAL")
	private String inscricaoEstadual;

	@Column(name="INSCRICAO_MUNICIPAL")
	private String inscricaoMunicipal;

	@Column(name="NOME")
	private String nome;

	@Column(name="RAZAO_SOCIAL")
	private String razaoSocial;

	@Column(name="RESPONSAVEL")
	private String responsavel;

	@Column(name="SITE")
	private String site;

	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * @return the cnpj
	 */
	public String getCnpj() {
		return cnpj;
	}

	/**
	 * @param cnpj the cnpj to set
	 */
	public void setCnpj(String cnpj) {
		this.cnpj = cnpj;
	}

	/**
	 * @return the cnpjPrefixo
	 */
	public String getCnpjPrefixo() {
		return cnpjPrefixo;
	}

	/**
	 * @param cnpjPrefixo the cnpjPrefixo to set
	 */
	public void setCnpjPrefixo(String cnpjPrefixo) {
		this.cnpjPrefixo = cnpjPrefixo;
	}

	/**
	 * @return the dataAlteracao
	 */
	public Date getDataAlteracao() {
		return dataAlteracao;
	}

	/**
	 * @param dataAlteracao the dataAlteracao to set
	 */
	public void setDataAlteracao(Date dataAlteracao) {
		this.dataAlteracao = dataAlteracao;
	}

	/**
	 * @return the dataCriacao
	 */
	public Date getDataCriacao() {
		return dataCriacao;
	}

	/**
	 * @param dataCriacao the dataCriacao to set
	 */
	public void setDataCriacao(Date dataCriacao) {
		this.dataCriacao = dataCriacao;
	}

	/**
	 * @return the dataInauguracao
	 */
	public Date getDataInauguracao() {
		return dataInauguracao;
	}

	/**
	 * @param dataInauguracao the dataInauguracao to set
	 */
	public void setDataInauguracao(Date dataInauguracao) {
		this.dataInauguracao = dataInauguracao;
	}

	/**
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * @param email the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * @return the idUsuarioAlteracao
	 */
	public Long getIdUsuarioAlteracao() {
		return idUsuarioAlteracao;
	}

	/**
	 * @param idUsuarioAlteracao the idUsuarioAlteracao to set
	 */
	public void setIdUsuarioAlteracao(Long idUsuarioAlteracao) {
		this.idUsuarioAlteracao = idUsuarioAlteracao;
	}

	/**
	 * @return the idUsuarioCriacao
	 */
	public Long getIdUsuarioCriacao() {
		return idUsuarioCriacao;
	}

	/**
	 * @param idUsuarioCriacao the idUsuarioCriacao to set
	 */
	public void setIdUsuarioCriacao(Long idUsuarioCriacao) {
		this.idUsuarioCriacao = idUsuarioCriacao;
	}

	/**
	 * @return the inscricaoEstadual
	 */
	public String getInscricaoEstadual() {
		return inscricaoEstadual;
	}

	/**
	 * @param inscricaoEstadual the inscricaoEstadual to set
	 */
	public void setInscricaoEstadual(String inscricaoEstadual) {
		this.inscricaoEstadual = inscricaoEstadual;
	}

	/**
	 * @return the inscricaoMunicipal
	 */
	public String getInscricaoMunicipal() {
		return inscricaoMunicipal;
	}

	/**
	 * @param inscricaoMunicipal the inscricaoMunicipal to set
	 */
	public void setInscricaoMunicipal(String inscricaoMunicipal) {
		this.inscricaoMunicipal = inscricaoMunicipal;
	}

	/**
	 * @return the nome
	 */
	public String getNome() {
		return nome;
	}

	/**
	 * @param nome the nome to set
	 */
	public void setNome(String nome) {
		this.nome = nome;
	}

	/**
	 * @return the razaoSocial
	 */
	public String getRazaoSocial() {
		return razaoSocial;
	}

	/**
	 * @param razaoSocial the razaoSocial to set
	 */
	public void setRazaoSocial(String razaoSocial) {
		this.razaoSocial = razaoSocial;
	}

	/**
	 * @return the responsavel
	 */
	public String getResponsavel() {
		return responsavel;
	}

	/**
	 * @param responsavel the responsavel to set
	 */
	public void setResponsavel(String responsavel) {
		this.responsavel = responsavel;
	}

	/**
	 * @return the site
	 */
	public String getSite() {
		return site;
	}

	/**
	 * @param site the site to set
	 */
	public void setSite(String site) {
		this.site = site;
	}
}