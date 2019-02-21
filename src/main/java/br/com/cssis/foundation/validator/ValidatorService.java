package br.com.cssis.foundation.validator;

import javax.persistence.EntityManager;
import javax.sql.DataSource;

import br.com.cssis.foundation.BaseEntity;
import br.com.cssis.foundation.service.PersistenceService;
import br.com.cssis.foundation.service.ServiceException;

public interface ValidatorService {
	public static final String K_VALIDATOR_SERVICE_NAME = "validate";

	void validate(Object in) throws ServiceException;

	void validate(Object in, int event) throws ServiceException;

	EntityManager getEM();

	DataSource getDefaultDS();

	PersistenceService<BaseEntity> getService();

	<T extends BaseEntity> void validarDataInicioFim(Class<T> clazz, T value, Long[] referenceIds, String[] referencedIdsName, long constantError) throws ServiceException;

	<T extends BaseEntity> void validarDataInicioFim(Class<T> clazz, T value, String idFieldName, String dataInicioFieldName, String dataFimFieldName, Long[] referenceIds, String[] referencedIdsName, long constantError) throws ServiceException;

	<T extends BaseEntity> void validarFaixaInicioFim(Class<T> clazz, T value, String idFieldName, String quantidadeInicioName, String quantidadeFimName, Long quantidadeInicioValue, Long quantidadeFimValue, Long[] referenceIds, String[] referencedIdsName, long constantError) throws ServiceException;

	<T extends BaseEntity> void validarIntervaloContiguo(Class<T> clazz, T value, String quantidadeInicioName, String quantidadeFimName, Long[] referenceIds, String[] referencedIdsName, long constantError) throws ServiceException;
	
	<T extends BaseEntity> void validarParcelaInicioFim(Class<T> clazz, T value, String idFieldName, String parInicioFieldName, String parFimFieldName, Object[] referenceIds, String[] referencedIdsName, String tpVitalicio, long constantError) throws ServiceException;
	
	<T extends BaseEntity> void validarParcelaIntervalo(Class<T> clazz, T value, String parInicioName, String parFimName, Object[] referenceIds, String[] referencedIdsName, long constantError) throws ServiceException;
}
