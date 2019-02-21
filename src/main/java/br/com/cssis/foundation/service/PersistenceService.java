package br.com.cssis.foundation.service;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.sql.DataSource;

import org.hibernate.Session;

import br.com.cssis.foundation.BaseEntity;
import br.com.cssis.foundation.BasicEntityObject;
import br.com.cssis.foundation.query.impl.DatabaseQuery;
import br.com.cssis.foundation.query.impl.FilterCondition;
import br.com.cssis.foundation.query.impl.SortingCondition;

public interface PersistenceService<I extends BaseEntity> extends SearchPersistenceService<I>{
	// metodos de apoio
	EntityManager getEM();
	EntityManager getEM(String dataSourceName);

	DataSource getDefaultDS();

	// metodos CRUD
	<T extends I, ID extends Serializable> T findById(Class<T> persistenceClass, ID id) throws ServiceException;

	<T extends I> T save(T transientInstance) throws ServiceException;

	<T extends I> T update(T detachedInstance) throws ServiceException;

	<T extends I, ID extends Serializable> void delete(Class<T> persistenceClass, ID id) throws ServiceException;

	<T extends I> void delete(T persistenceObject) throws ServiceException;

	<T> List<T> findByQuery(String queryName, List<FilterCondition> criteriaList) throws ServiceException;

	int getCountByQuery(String queryName, List<FilterCondition> criteriaList) throws ServiceException;

	<T extends I> int getCountByProperties(Class<T> persistenceClass, List<FilterCondition> criteriaList) throws ServiceException;

	Long getIdLoggedUser();

	void setIdLoggedUser(Long idLoggedUser);

	Session getSession() throws ServiceException;

	<T extends BaseEntity> void lockRecord(T target) throws ServiceException;

	<T extends BaseEntity> void validate(T target) throws ServiceException;

	<T extends BaseEntity> void validate(T target, int event) throws ServiceException;

	<T extends I> T getSingleRecordByProperties(Class<T> persistenceClass, List<FilterCondition> criteriaList) throws ServiceException;
	
	<T> T getSingleRecordByQuery(String queryName, List<FilterCondition> criteriaList) throws ServiceException;

	int executeSqlStatement(String baseSql, List<String> paramNames, List<Object> paramValues) throws ServiceException;
	
	<T> List<T> executeQuery(String mapName, List<FilterCondition> conditions)throws ServiceException;
	
	<T>List<T> executeQueryIbatis(final String mapName, Map<Object,Object> params) throws ServiceException;

	Date getDateDB();

	/**
	 * Deve ser chamado acessando um service injetado no service chamador.<br>
	 * Exemplo.:<br>
	 * AService tem a propriedade private BService bService;<br>
	 * precisara acessar assim, bService.saveUseNewTransaction(entity)<br>
	 * 
	 * @param transientInstance
	 * @return
	 * @throws ServiceException
	 */
	<T extends BasicEntityObject> T saveUseNewTransaction(T transientInstance) throws ServiceException;

	/**
	 * Deve ser chamado acessando um service injetado no service chamador.<br>
	 * Exemplo.:<br>
	 * AService tem a propriedade private BService bService;<br>
	 * precisara acessar assim, bService.updateUseNewTransaction(entity)<br>
	 * 
	 * @param transientInstance
	 * @return
	 * @throws ServiceException
	 */
	<T extends BasicEntityObject> T updateUseNewTransaction(T detachedInstance) throws ServiceException;
	
	public <T> List<T> getPage(DatabaseQuery databaseQuery, List<FilterCondition> criteriaList, List<SortingCondition> sortingList, int page, int pageSize) throws ServiceException;
	
	public <T> List<T> findByQuery(DatabaseQuery databaseQuery, List<FilterCondition> criteriaList, List<SortingCondition> sortingList) throws ServiceException;
}