package br.com.cssis.dominio.cadastro.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import br.com.cssis.foundation.BasicEntityObject;


/**
 * The persistent class for the BAIRRO database table.
 * 
 */
@Entity
public class Bairro extends BasicEntityObject implements CadastroEntity {
	
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="ID_BAIRRO")
	private Long id;

	@Column(name="ID_BAIRRO_CORREIO")
	private String idBairroCorreio;

	@Column(name="ID_SUPER_BAIRRO")
	private Long idSuperBairro;

	@Column(name="NOME_ABREVIADO_BAIRRO")
	private String nomeAbreviadoBairro;

	@Column(name="NOME_BAIRRO")
	private String nome;

	@Column(name="NOME_PESQUISA")
	private String nomePesquisa;

	@Column(name="NOME_SOUNDEX")
	private String nomeSoundex;

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
	 * @return the idBairroCorreio
	 */
	public String getIdBairroCorreio() {
		return idBairroCorreio;
	}

	/**
	 * @param idBairroCorreio the idBairroCorreio to set
	 */
	public void setIdBairroCorreio(String idBairroCorreio) {
		this.idBairroCorreio = idBairroCorreio;
	}

	/**
	 * @return the idSuperBairro
	 */
	public Long getIdSuperBairro() {
		return idSuperBairro;
	}

	/**
	 * @param idSuperBairro the idSuperBairro to set
	 */
	public void setIdSuperBairro(Long idSuperBairro) {
		this.idSuperBairro = idSuperBairro;
	}

	/**
	 * @return the nomeAbreviadoBairro
	 */
	public String getNomeAbreviadoBairro() {
		return nomeAbreviadoBairro;
	}

	/**
	 * @param nomeAbreviadoBairro the nomeAbreviadoBairro to set
	 */
	public void setNomeAbreviadoBairro(String nomeAbreviadoBairro) {
		this.nomeAbreviadoBairro = nomeAbreviadoBairro;
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
}