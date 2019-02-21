package br.com.cssis.foundation.service;

import java.util.List;

import br.com.cssis.foundation.query.impl.DatabaseQuery;
import br.com.cssis.foundation.query.impl.FilterCondition;
import br.com.cssis.foundation.query.impl.SortingCondition;

public interface SearchPersistenceService<I> {

	<T> List<T> getPage(Class<T> persistenceClass, List<FilterCondition> criteriaList, List<SortingCondition> sortingList, int page, int pageSize) throws ServiceException;
	
	<T> List<T> getPage(String queryName, List<FilterCondition> criteriaList, List<SortingCondition> sortingList, int page, int pageSize) throws ServiceException;

	<T> List<T> findByQuery(String queryName, List<FilterCondition> criteriaList, List<SortingCondition> sortingList) throws ServiceException;

	<T> List<T> findByProperties(Class<T> persistenceClass, List<FilterCondition> criteriaList, List<SortingCondition> sortingList) throws ServiceException;
	
	<T> List<T> getPage(DatabaseQuery databaseQuery, List<FilterCondition> criteriaList, List<SortingCondition> sortingList, int page, int pageSize) throws ServiceException;
	
	<T> List<T> findByQuery(DatabaseQuery databaseQuery, List<FilterCondition> criteriaList, List<SortingCondition> sortingList) throws ServiceException;
}