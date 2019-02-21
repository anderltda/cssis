package br.com.cssis.dominio.cadastro.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import br.com.cssis.foundation.BasicEntityObject;


/**
 * The persistent class for the FARM_CONV_ENDERECO_HORARIO database table.
 * 
 */
@Entity
@Table(name="FARM_CONV_ENDERECO_HORARIO")
public class FarmConvEnderecoHorario extends BasicEntityObject implements CadastroEntity {
	
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="ID_FARM_CONV_ENDER_HORARIO")
	private Long id;

	@Column(name="HORARIO_FINAL")
	private String horarioFinal;

	@Column(name="HORARIO_INICIAL")
	private String horarioInicial;

	@Column(name="OBSERVACAO")
	private String observacao;

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
	 * @return the horarioFinal
	 */
	public String getHorarioFinal() {
		return horarioFinal;
	}

	/**
	 * @param horarioFinal the horarioFinal to set
	 */
	public void setHorarioFinal(String horarioFinal) {
		this.horarioFinal = horarioFinal;
	}

	/**
	 * @return the horarioInicial
	 */
	public String getHorarioInicial() {
		return horarioInicial;
	}

	/**
	 * @param horarioInicial the horarioInicial to set
	 */
	public void setHorarioInicial(String horarioInicial) {
		this.horarioInicial = horarioInicial;
	}

	/**
	 * @return the observacao
	 */
	public String getObservacao() {
		return observacao;
	}

	/**
	 * @param observacao the observacao to set
	 */
	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}	
}