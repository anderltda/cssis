package br.com.cssis.dominio.cadastro.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import br.com.cssis.foundation.BasicEntityObject;


/**
 * The persistent class for the LOCALIDADE database table.
 * 
 */
@Entity
public class Localidade extends BasicEntityObject implements CadastroEntity {
	
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="ID_LOCALIDADE")
	private Long id;

	@Column(name="CEP")
	private String cep;

	@Column(name="CODIGO_IBGE")
	private String codigoIbge;

	@Column(name="DDD")
	private String ddd;

	@Column(name="DISPONIVEL_FILTRO_REGIAO")
	private String disponivelFiltroRegiao;

	@Column(name="ID_LOCALIDADE_CORREIO")
	private String idLocalidadeCorreio;

	@Column(name="ID_LOCALIDADE_PAI")
	private Long idLocalidadePai;

	@Column(name="ID_REGIONALIZACAO")
	private Long idRegionalizacao;

	@Column(name="ID_TIPO_LOCALIDADE")
	private String idTipoLocalidade;

	@Column(name = "LATITUDE")
	private Double latitude;

	@Column(name = "LONGITUDE")
	private Double longitude;

	@Column(name="NOME_LOCALIDADE")
	private String nome;

	@Column(name="NOME_PESQUISA")
	private String nomePesquisa;

	@Column(name="NOME_SOUNDEX")
	private String nomeSoundex;

	@Column(name="NOME_SUB")
	private String nomeSub;

	@Column(name="UF")
	private String uf;

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
	 * @return the codigoIbge
	 */
	public String getCodigoIbge() {
		return codigoIbge;
	}

	/**
	 * @param codigoIbge the codigoIbge to set
	 */
	public void setCodigoIbge(String codigoIbge) {
		this.codigoIbge = codigoIbge;
	}

	/**
	 * @return the ddd
	 */
	public String getDdd() {
		return ddd;
	}

	/**
	 * @param ddd the ddd to set
	 */
	public void setDdd(String ddd) {
		this.ddd = ddd;
	}

	/**
	 * @return the disponivelFiltroRegiao
	 */
	public String getDisponivelFiltroRegiao() {
		return disponivelFiltroRegiao;
	}

	/**
	 * @param disponivelFiltroRegiao the disponivelFiltroRegiao to set
	 */
	public void setDisponivelFiltroRegiao(String disponivelFiltroRegiao) {
		this.disponivelFiltroRegiao = disponivelFiltroRegiao;
	}

	/**
	 * @return the idLocalidadeCorreio
	 */
	public String getIdLocalidadeCorreio() {
		return idLocalidadeCorreio;
	}

	/**
	 * @param idLocalidadeCorreio the idLocalidadeCorreio to set
	 */
	public void setIdLocalidadeCorreio(String idLocalidadeCorreio) {
		this.idLocalidadeCorreio = idLocalidadeCorreio;
	}

	/**
	 * @return the idLocalidadePai
	 */
	public Long getIdLocalidadePai() {
		return idLocalidadePai;
	}

	/**
	 * @param idLocalidadePai the idLocalidadePai to set
	 */
	public void setIdLocalidadePai(Long idLocalidadePai) {
		this.idLocalidadePai = idLocalidadePai;
	}

	/**
	 * @return the idRegionalizacao
	 */
	public Long getIdRegionalizacao() {
		return idRegionalizacao;
	}

	/**
	 * @param idRegionalizacao the idRegionalizacao to set
	 */
	public void setIdRegionalizacao(Long idRegionalizacao) {
		this.idRegionalizacao = idRegionalizacao;
	}

	/**
	 * @return the idTipoLocalidade
	 */
	public String getIdTipoLocalidade() {
		return idTipoLocalidade;
	}

	/**
	 * @param idTipoLocalidade the idTipoLocalidade to set
	 */
	public void setIdTipoLocalidade(String idTipoLocalidade) {
		this.idTipoLocalidade = idTipoLocalidade;
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
	 * @return the nomePesquisa
	 */
	public String getNomePesquisa() {
		return nomePesquisa;
	}

	/**
	 * @param nomePesquisa the nomePesquisa to set
	 */
	public void setNomePesquisa(String nomePesquisa) {
		this.nomePesquisa = nomePesquisa;
	}

	/**
	 * @return the nomeSoundex
	 */
	public String getNomeSoundex() {
		return nomeSoundex;
	}

	/**
	 * @param nomeSoundex the nomeSoundex to set
	 */
	public void setNomeSoundex(String nomeSoundex) {
		this.nomeSoundex = nomeSoundex;
	}

	/**
	 * @return the nomeSub
	 */
	public String getNomeSub() {
		return nomeSub;
	}

	/**
	 * @param nomeSub the nomeSub to set
	 */
	public void setNomeSub(String nomeSub) {
		this.nomeSub = nomeSub;
	}

	/**
	 * @return the uf
	 */
	public String getUf() {
		return uf;
	}

	/**
	 * @param uf the uf to set
	 */
	public void setUf(String uf) {
		this.uf = uf;
	}
}