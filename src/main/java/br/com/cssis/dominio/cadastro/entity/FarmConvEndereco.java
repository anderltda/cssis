package br.com.cssis.dominio.cadastro.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import br.com.cssis.foundation.BasicEntityObject;


/**
 * The persistent class for the FARM_CONV_ENDERECO database table.
 * 
 */
@Entity
@Table(name="FARM_CONV_ENDERECO")
public class FarmConvEndereco extends BasicEntityObject implements CadastroEntity {
	
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="ID_FARM_CONV_ENDERECO")
	private Long id;

	@Column(name="ATENDE_ATE_MEIA_NOITE")
	private String atendeAteMeiaNoite;

	@Column(name="ATENDE_SABADOS")
	private String atendeSabados;

	@Column(name="ATENDE_TODO_DIA")
	private String atendeTodoDia;

	@Column(name="CEP")
	private String cep;

	@Column(name="COMPLEMENTO")
	private String complemento;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="DATA_ALTERACAO")
	private Date dataAlteracao;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="DATA_CRIACAO")
	private Date dataCriacao;

	@Column(name="DDD_TELEFONE")
	private String dddTelefone;

	@Column(name="HABILITADO")
	private Integer habilitado;

	@Column(name="ID_USUARIO_ALTERACAO")
	private Long idUsuarioAlteracao;

	@Column(name="ID_USUARIO_CRIACAO")
	private Long idUsuarioCriacao;

	@Column(name = "LATITUDE")
	private Double latitude;

	@Column(name = "LONGITUDE")
	private Double longitude;

	@Column(name = "LOGRADOURO")
	private String logradouro;

	@Column(name = "NUMERO")
	private String numero;

	@Column(name = "REFERENCIA")
	private String referencia;

	@Column(name="TELEFONE_1")
	private String telefone1;

	@Column(name="TELEFONE_2")
	private String telefone2;
	
	@Column(name="BAIRRO")
	private String bairro_;
	
	@ManyToOne
	@JoinColumn(name="ID_FARMACIA_CONVENIADA")
	private FarmaciaConveniada farmaciaConveniada;
	
	@ManyToOne
	@JoinColumn(name="ID_LOCALIDADE")
	private Localidade localidade;

	@ManyToOne
	@JoinColumn(name="ID_BAIRRO")
	private Bairro bairro;

	@ManyToOne
	@JoinColumn(name="ID_TIPO_LOGRADOURO")
	private TipoLogradouro tipoLogradouro;

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
	 * @return the atendeAteMeiaNoite
	 */
	public String getAtendeAteMeiaNoite() {
		return atendeAteMeiaNoite;
	}

	/**
	 * @param atendeAteMeiaNoite the atendeAteMeiaNoite to set
	 */
	public void setAtendeAteMeiaNoite(String atendeAteMeiaNoite) {
		this.atendeAteMeiaNoite = atendeAteMeiaNoite;
	}

	/**
	 * @return the atendeSabados
	 */
	public String getAtendeSabados() {
		return atendeSabados;
	}

	/**
	 * @param atendeSabados the atendeSabados to set
	 */
	public void setAtendeSabados(String atendeSabados) {
		this.atendeSabados = atendeSabados;
	}

	/**
	 * @return the atendeTodoDia
	 */
	public String getAtendeTodoDia() {
		return atendeTodoDia;
	}

	/**
	 * @param atendeTodoDia the atendeTodoDia to set
	 */
	public void setAtendeTodoDia(String atendeTodoDia) {
		this.atendeTodoDia = atendeTodoDia;
	}

	/**
	 * @return the cep
	 */
	public String getCep() {
		return cep;
	}

	/**
	 * @param cep the cep to set
	 */
	public void setCep(String cep) {
		this.cep = cep;
	}

	/**
	 * @return the complemento
	 */
	public String getComplemento() {
		return complemento;
	}

	/**
	 * @param complemento the complemento to set
	 */
	public void setComplemento(String complemento) {
		this.complemento = complemento;
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
	 * @return the dddTelefone
	 */
	public String getDddTelefone() {
		return dddTelefone;
	}

	/**
	 * @param dddTelefone the dddTelefone to set
	 */
	public void setDddTelefone(String dddTelefone) {
		this.dddTelefone = dddTelefone;
	}

	/**
	 * @return the habilitado
	 */
	public Integer getHabilitado() {
		return habilitado;
	}

	/**
	 * @param habilitado the habilitado to set
	 */
	public void setHabilitado(Integer habilitado) {
		this.habilitado = habilitado;
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
	 * @return the latitude
	 */
	public Double getLatitude() {
		return latitude;
	}

	/**
	 * @param latitude the latitude to set
	 */
	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	/**
	 * @return the longitude
	 */
	public Double getLongitude() {
		return longitude;
	}

	/**
	 * @param longitude the longitude to set
	 */
	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}

	/**
	 * @return the logradouro
	 */
	public String getLogradouro() {
		return logradouro;
	}

	/**
	 * @param logradouro the logradouro to set
	 */
	public void setLogradouro(String logradouro) {
		this.logradouro = logradouro;
	}

	/**
	 * @return the numero
	 */
	public String getNumero() {
		return numero;
	}

	/**
	 * @param numero the numero to set
	 */
	public void setNumero(String numero) {
		this.numero = numero;
	}

	/**
	 * @return the referencia
	 */
	public String getReferencia() {
		return referencia;
	}

	/**
	 * @param referencia the referencia to set
	 */
	public void setReferencia(String referencia) {
		this.referencia = referencia;
	}

	/**
	 * @return the telefone1
	 */
	public String getTelefone1() {
		return telefone1;
	}

	/**
	 * @param telefone1 the telefone1 to set
	 */
	public void setTelefone1(String telefone1) {
		this.telefone1 = telefone1;
	}

	/**
	 * @return the telefone2
	 */
	public String getTelefone2() {
		return telefone2;
	}

	/**
	 * @param telefone2 the telefone2 to set
	 */
	public void setTelefone2(String telefone2) {
		this.telefone2 = telefone2;
	}	

	/**
	 * @return the bairro
	 */
	public Bairro getBairro() {
		return bairro;
	}

	/**
	 * @param bairro the bairro to set
	 */
	public void setBairro(Bairro bairro) {
		this.bairro = bairro;
	}

	/**
	 * @return the tipoLogradouro
	 */
	public TipoLogradouro getTipoLogradouro() {
		return tipoLogradouro;
	}

	/**
	 * @param tipoLogradouro the tipoLogradouro to set
	 */
	public void setTipoLogradouro(TipoLogradouro tipoLogradouro) {
		this.tipoLogradouro = tipoLogradouro;
	}

	/**
	 * @return the bairro_
	 */
	public String getBairro_() {
		return bairro_;
	}

	/**
	 * @param bairro_ the bairro_ to set
	 */
	public void setBairro_(String bairro_) {
		this.bairro_ = bairro_;
	}

	/**
	 * @return the farmaciaConveniada
	 */
	public FarmaciaConveniada getFarmaciaConveniada() {
		return farmaciaConveniada;
	}

	/**
	 * @param farmaciaConveniada the farmaciaConveniada to set
	 */
	public void setFarmaciaConveniada(FarmaciaConveniada farmaciaConveniada) {
		this.farmaciaConveniada = farmaciaConveniada;
	}

	/**
	 * @return the localidade
	 */
	public Localidade getLocalidade() {
		return localidade;
	}

	/**
	 * @param localidade the localidade to set
	 */
	public void setLocalidade(Localidade localidade) {
		this.localidade = localidade;
	}	
	
}