package br.com.cssis.dominio.seguranca.entity;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.BatchSize;

import br.com.cssis.foundation.util.ReflectionUtil;
import br.com.cssis.foundation.util.StringHelper;


/**
 * Usuario entity.
 * @author anderson.nascimento
 */
@Entity
@Table(name = "USUARIO")
@BatchSize(size = 10)
public class Usuario extends BasicLogableEntityObject {
	
	public static final Long ID_USUARIO_ODONTO_UTILIS = 1L;
	public static final Long ID_USUARIO_PROCESSAMENTOCARGA = 17475L;
	public static final Long ID_USUARIO_OU_QUALIFICACAO_CADASTRAL = 76264L;
	public static final Long ID_USUARIO_BATCH_QUARTZ_ADIANTAMENTO = 77991L;
	public static final Long ID_USUARIO_GERACAO_LOTE_COBRANCA = 77992L;
	public static final Long ID_USUARIO_FATURAMENTO_AUTOMATICO = 78200L;
	public static final Long ID_USUARIO_QUARTZ_DESCREDENC_CONT_PREST = 84904L;
	public static final Long ID_USUARIO_QUARTZ_GENERICO = 84906L;
	public static final Long ID_USUARIO_INTEGRACAO_SAP_COBRANCA = 1000028185L;
	public static final Long ID_USAURIO_UPLOAD_ARQUIVO = 93807L;

	private static final long serialVersionUID = 1L;
	private Long id;
	private String nome;
	private String usuario;
	private Boolean habilitado;
	private Date dataUltimoLogin;
	private String email;
	private String cpf;
	private String usuarioWindows;
	private String role;
	private String usuarioOrigem;
	private Long idExterno;
	private String baseDN;

	@Id
	@Column(name = "ID_USUARIO", unique = true, nullable = false, insertable = true, updatable = true, precision = 10, scale = 0)
	@SequenceGenerator(allocationSize=1, name = "SEQUENCE_USUARIO", sequenceName = "SQ_USUARIO")
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator = "SEQUENCE_USUARIO")
	public Long getId() {
		return this.id;
	}

	public void setId(Long idUsuario) {
		this.id = idUsuario;
	}

	@Column(name = "NOME", unique = false, nullable = false, insertable = true, updatable = true, length = 100)
	public String getNome() {
		return this.nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	@Column(name = "USUARIO", unique = false, nullable = false, insertable = true, updatable = true, length = 100)
	public String getUsuario() {
		return this.usuario;
	}

	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}

	@Column(name = "HABILITADO", unique = false, nullable = false, insertable = true, updatable = true, precision = 1, scale = 0)
	public Boolean getHabilitado() {
		return this.habilitado;
	}

	public void setHabilitado(Boolean habilitado) {
		this.habilitado = habilitado;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DATA_ULTIMO_LOGIN", unique = false, nullable = true, insertable = true, updatable = true, length = 7)
	public Date getDataUltimoLogin() {
		return this.dataUltimoLogin;
	}

	public void setDataUltimoLogin(Date dataUltimoLogin) {
		this.dataUltimoLogin = dataUltimoLogin;
	}

	@Column(name = "EMAIL", unique = false, nullable = true, insertable = true, updatable = true, length = 100)
	public String getEmail() {
		return this.email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@Column(name = "USUARIO_WINDOWS", unique = false, nullable = true, insertable = true, updatable = true, length = 100)
	public String getUsuarioWindows() {
		return this.usuarioWindows;
	}

	public void setUsuarioWindows(String usuarioWindows) {
		this.usuarioWindows = usuarioWindows;
	}

	@Column(name = "ROLE", unique = false, nullable = true, insertable = true, updatable = true, length = 100)
	public String getRole() {
		return this.role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	@Column(name = "USUARIO_ORIGEM", unique = false, nullable = true, insertable = true, updatable = true, length = 100)
	public String getUsuarioOrigem() {
		return this.usuarioOrigem;
	}

	public void setUsuarioOrigem(String usuarioOrigem) {
		this.usuarioOrigem = usuarioOrigem;
	}

	@Column(name = "ID_EXTERNO", unique = false, nullable = true, insertable = true, updatable = true, precision = 10, scale = 0)
	public Long getIdExterno() {
		return this.idExterno;
	}

	public void setIdExterno(Long idExterno) {
		this.idExterno = idExterno;
	}

	@Column(name = "BASE_DN", unique = false, nullable = true, insertable = true, updatable = true, length = 100)
	public String getBaseDN() {
		return baseDN;
	}

	public void setBaseDN(String baseDN) {
		this.baseDN = baseDN;
	}

	@Column(name = "CPF", length = 11)
	public String getCpf() {
		return this.cpf;
	}
	
	public void setCpf(String cpf) {
		this.cpf = StringHelper.removeNonNumbers(cpf);
	}

	@Transient
	public String getCpfFormatado() {
		return StringHelper.formataCnpjCpf(cpf);
	}

	public String toString() {
		return ReflectionUtil.getBasicFieldsAsString(this);
	}

	private String passwd;

	@Transient
	public String getPasswd() {
		return passwd;
	}

	@Transient
	public void setPasswd(String passwd) {
		this.passwd = passwd;
	}

	private String nomeCompleto;

	@Transient
	public String getNomeCompleto() {
		return this.nomeCompleto;
	}

	@Transient
	public void setNomeCompleto(String nomeCompleto) {
		this.nomeCompleto = nomeCompleto;
	}

	private List<Long> idsSubs;

	@Transient
	public List<Long> getIdsSubs() {
		return idsSubs;
	}

	@Transient
	public void setIdsSubs(List<Long> idsSubs) {
		this.idsSubs = idsSubs;
	}
}