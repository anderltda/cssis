package br.com.cssis.foundation;

import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

@MappedSuperclass
public abstract class BasicEntityObject extends NonVersionedBasicEntityObject {
	
	private static final long serialVersionUID = 1L;

	@Transient
	public boolean isOutOfDate(BasicEntityObject otherObject) {
		return false;
	}

	@Transient
	public boolean isOutOfDate(Long otherVersion) {
		return false;
	}

}
