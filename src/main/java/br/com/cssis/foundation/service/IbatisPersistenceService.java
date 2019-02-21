package br.com.cssis.foundation.service;

import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;

import br.com.cssis.foundation.query.impl.FilterCondition;

/**
 * Interface para servições à chamadas de queries ou stored procedures que foram mapeadas pelo framework iBatis.
 * 
 * @author ter_jesiel.trevisan
 * @since 21/10/2011
 */
public interface IbatisPersistenceService {

	public final static String SERVICE_NAME = "ibatisPersistenceService";
	
	/**
	 * Método responsável por realizar uma chamada queryes ou stored procedures que estão mapeadas pelo framework iBatis.
	 * 
	 * @param queryName Nome do mapeamento.
	 * @param params Parâmetros esperado pela procedure.
	 * @return <T>List<T>
	 * @throws ServiceException
	 */
	<T>List<T> executeQuery(final String queryName, Map<Object,Object> params) throws ServiceException;
	
	<T>List<T> executeQuery(String queryName, List<FilterCondition> conditions) throws ServiceException;
	
	<T> List<T> executeQuery(final String queryName, Map<Object, Object> params, int firstResult, int maxResult) throws ServiceException;
	
	<T>List<T> getPage(String queryName, List<FilterCondition> criteriaList, int page, int pageSize) throws ServiceException;
	
	Object getSingleRecordByQuery(final String queryName, Map<Object,Object> params) throws ServiceException;
	
	public EntityManager getEm();
}