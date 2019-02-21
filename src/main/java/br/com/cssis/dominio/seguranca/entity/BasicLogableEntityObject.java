package br.com.cssis.dominio.seguranca.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import br.com.cssis.foundation.BasicEntityObject;
import br.com.cssis.foundation.util.DateTimeHelper;

@MappedSuperclass
public abstract class BasicLogableEntityObject extends BasicEntityObject {
	private static final long serialVersionUID = 1L;
	protected Long idUsuarioCriacao;
	protected Date dataCriacao;
	protected Long idUsuarioAlteracao;
	protected Date dataAlteracao;

	@Column(name = "ID_USUARIO_CRIACAO", unique = false, nullable = false, insertable = true, updatable = true, precision = 10, scale = 0)
	public Long getIdUsuarioCriacao() {
		return this.idUsuarioCriacao;
	}

	public void setIdUsuarioCriacao(Long idUsuarioCriacao) {
		this.idUsuarioCriacao = idUsuarioCriacao;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DATA_CRIACAO", unique = false, nullable = false, insertable = true, updatable = true, length = 7)
	public Date getDataCriacao() {
		return this.dataCriacao;
	}

	public void setDataCriacao(Date dataCriacao) {
		this.dataCriacao = dataCriacao;
	}

	@Column(name = "ID_USUARIO_ALTERACAO", unique = false, nullable = false, insertable = true, updatable = true, precision = 10, scale = 0)
	public Long getIdUsuarioAlteracao() {
		return this.idUsuarioAlteracao;
	}

	public void setIdUsuarioAlteracao(Long idUsuarioAlteracao) {
		this.idUsuarioAlteracao = idUsuarioAlteracao;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DATA_ALTERACAO", unique = false, nullable = false, insertable = true, updatable = true, length = 7)
	public Date getDataAlteracao() {
		return this.dataAlteracao;
	}

	public void setDataAlteracao(Date dataAlteracao) {
		this.dataAlteracao = dataAlteracao;
	}

	@Transient
	Usuario usuarioCriacao;

	@ManyToOne(cascade = {}, fetch = FetchType.LAZY)
	@JoinColumn(name = "ID_USUARIO_CRIACAO", unique = false, nullable = true, insertable = false, updatable = false)
	public Usuario getUsuarioCriacao() {
		return this.usuarioCriacao;
	}

	public void setUsuarioCriacao(Usuario usuarioCriacao) {
		this.usuarioCriacao = usuarioCriacao;
	}

	@Transient
	Usuario usuarioAlteracao;

	@ManyToOne(cascade = {}, fetch = FetchType.LAZY)
	@JoinColumn(name = "ID_USUARIO_ALTERACAO", unique = false, nullable = true, insertable = false, updatable = false)
	public Usuario getUsuarioAlteracao() {
		return this.usuarioAlteracao;
	}

	public void setUsuarioAlteracao(Usuario usuarioAlteracao) {
		this.usuarioAlteracao = usuarioAlteracao;
	}

	@Transient
	public String getUsuarioCriacaoCompleto() {
		return getDataCriacaoFormatada() + " " + usuarioCriacao.getNome();
	}

	@Transient
	public String getUsuarioAlteracaoCompleto() {
		return getDataAlteracaoFormatada()  + " " + usuarioAlteracao.getNome();
	}

	@Transient
	public String getDataAlteracaoFormatada() {
		return DateTimeHelper.formatDateTimeNonSafe(dataAlteracao);
	}

	@Transient
	public String getDataCriacaoFormatada() {
		return DateTimeHelper.formatDateTimeNonSafe(dataCriacao);
	}

}
