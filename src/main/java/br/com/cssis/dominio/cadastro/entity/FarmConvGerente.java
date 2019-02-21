package br.com.cssis.dominio.cadastro.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import br.com.cssis.foundation.BasicEntityObject;


/**
 * The persistent class for the FARM_CONV_GERENTE database table.
 * 
 */
@Entity
@Table(name="FARM_CONV_GERENTE")
public class FarmConvGerente extends BasicEntityObject implements CadastroEntity {
	
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="ID_CONV_FARM_GERENTE")
	private Long id;

	@Column(name="NIVEL_CARGO")
	private String nivelCargo;

	@Column(name="NOME")
	private String nome;

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
	 * @return the nivelCargo
	 */
	public String getNivelCargo() {
		return nivelCargo;
	}

	/**
	 * @param nivelCargo the nivelCargo to set
	 */
	public void setNivelCargo(String nivelCargo) {
		this.nivelCargo = nivelCargo;
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
}