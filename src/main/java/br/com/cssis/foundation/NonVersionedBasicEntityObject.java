package br.com.cssis.foundation;

import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

import org.hibernate.proxy.HibernateProxy;

import br.com.cssis.foundation.util.ReflectionUtil;

@MappedSuperclass
public abstract class NonVersionedBasicEntityObject implements BaseEntity {
	/*
	 * Classe sem o Version. O Hibernate tem um 'bug' com o hard lock
	 * ele gera um select for update com o ora_rowscn que não faz sentido (pois este é um soft lock)
	 * então entidades que forem fazer o hard lock, deve herdar daqui e não do basic.
	 */
	
	
	private static final long serialVersionUID = 1L;

	@Transient
	protected boolean reentrant = false;
	
	@Transient
	protected boolean doNotValidate = false;


	@Transient
	public boolean isReentrant() {
		return reentrant;
	}

	@Transient
	public void setReentrant(boolean reentrant) {
		this.reentrant = reentrant;
	}

	@Transient
	public boolean isIdNull() {
		try {
			Object id = ReflectionUtil.executeGetterMethod(this, "id");
			return id == null;
		} catch (Exception e) {
			return false;
		}
	}

	@Transient
	public boolean isDoNotValidate() {
		return doNotValidate;
	}

	@Transient
	public void setDoNotValidate(boolean doNotValidate) {
		this.doNotValidate = doNotValidate;
	}

	@Transient
	@SuppressWarnings("unchecked")
	public <T extends BaseEntity> Class<T> getConcreteClass() {
		Class<?> targetClass = null;
		if (this instanceof HibernateProxy) {
			HibernateProxy object = (HibernateProxy) this;
			targetClass = object.getHibernateLazyInitializer().getPersistentClass();
		} else {
			targetClass = this.getClass();
		}
		return (Class<T>) targetClass;
	}

	@Transient
	@SuppressWarnings("unchecked")
	public static final <T extends BaseEntity> Class<T> getConcreteClass(Object obj) {
		Class<?> targetClass = null;
		if (obj instanceof HibernateProxy) {
			HibernateProxy object = (HibernateProxy) obj;
			targetClass = object.getHibernateLazyInitializer().getPersistentClass();
		} else {
			targetClass = obj.getClass();
		}
		return (Class<T>) targetClass;
	}

}
