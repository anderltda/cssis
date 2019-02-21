package br.com.cssis.commons.service.impl;

import java.util.Date;

import javax.persistence.Query;

import br.com.cssis.commons.service.BaseService;
import br.com.cssis.foundation.BaseEntity;
import br.com.cssis.foundation.service.impl.PersistenceServiceImpl;

/**
 * @author anderson.nascimento
 *
 * @param <I>
 */
public abstract class BaseServiceImpl<I extends BaseEntity> extends PersistenceServiceImpl<I> implements BaseService<I> {

	private static final long serialVersionUID = 2470072227394809760L;

	@Override
	public Date getDateDB() {
		Query query = getEM().createNativeQuery("select sysdate from dual");
		Date dataBD = (Date) query.getSingleResult();
		return dataBD;
	}

	public String getValorSoundexDB(String valor) {
		Query query = getEM().createNativeQuery("select soundex(:valor) from dual");
		query.setParameter("valor", valor);
		String valorSoundex = (String) query.getSingleResult();
		return valorSoundex;
	}

	public boolean isEqualValorSoundexDB(String valor1, String valor2) {
		Query query = getEM().createNativeQuery("select case when soundex( :valor1 ) = soundex( :valor2 ) then 'S' else 'N' end from dual");
		query.setParameter("valor1", valor1);
		query.setParameter("valor2", valor2);
		Character sIgual = (Character) query.getSingleResult();
		return "S".equalsIgnoreCase(sIgual.toString());
	}

}
