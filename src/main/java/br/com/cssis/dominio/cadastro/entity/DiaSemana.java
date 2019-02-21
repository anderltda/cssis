package br.com.cssis.dominio.cadastro.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import br.com.cssis.foundation.BasicEntityObject;


/**
 * The persistent class for the DIA_SEMANA database table.
 * 
 */
@Entity
@Table(name="DIA_SEMANA")
public class DiaSemana extends BasicEntityObject implements CadastroEntity {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="ID_DIA_SEMANA")
	private Long id;

	@Column(name="HABILITADO")
	private String habilitado;

	@Column(name="NOME_DIA_SEMANA")
	private String nome;

	@Column(name="ORDEM")
	private Integer ordem;

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
	 * @return the habilitado
	 */
	public String getHabilitado() {
		return habilitado;
	}

	/**
	 * @param habilitado the habilitado to set
	 */
	public void setHabilitado(String habilitado) {
		this.habilitado = habilitado;
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
	 * @return the ordem
	 */
	public Integer getOrdem() {
		return ordem;
	}

	/**
	 * @param ordem the ordem to set
	 */
	public void setOrdem(Integer ordem) {
		this.ordem = ordem;
	}	
}