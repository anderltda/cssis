package br.com.cssis.dominio.cadastro.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import br.com.cssis.foundation.BasicEntityObject;


/**
 * The persistent class for the TIPO_LOGRADOURO database table.
 * 
 */
@Entity
@Table(name="TIPO_LOGRADOURO")
public class TipoLogradouro extends BasicEntityObject implements CadastroEntity {
	
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="ID_TIPO_LOGRADOURO")
	private Long id;

	@Column(name="NOME_TIPO_LOGRADOURO")
	private String nome;

	@Column(name="NOME_TIPO_LOGRADOURO_CORREIO")
	private String nomeTipoLogradouroCorreio;

	@Column(name="SIGLA")
	private String sigla;

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
	 * @return the nomeTipoLogradouroCorreio
	 */
	public String getNomeTipoLogradouroCorreio() {
		return nomeTipoLogradouroCorreio;
	}

	/**
	 * @param nomeTipoLogradouroCorreio the nomeTipoLogradouroCorreio to set
	 */
	public void setNomeTipoLogradouroCorreio(String nomeTipoLogradouroCorreio) {
		this.nomeTipoLogradouroCorreio = nomeTipoLogradouroCorreio;
	}

	/**
	 * @return the sigla
	 */
	public String getSigla() {
		return sigla;
	}

	/**
	 * @param sigla the sigla to set
	 */
	public void setSigla(String sigla) {
		this.sigla = sigla;
	}	
}