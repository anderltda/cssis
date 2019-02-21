package br.com.cssis.foundation.service.impl;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.logging.Log;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import br.com.cssis.foundation.ExceptionHandler;
import br.com.cssis.foundation.LogService;
import br.com.cssis.foundation.query.impl.FilterCondition;
import br.com.cssis.foundation.service.IbatisPersistenceService;
import br.com.cssis.foundation.service.ServiceException;

@Service(value = IbatisPersistenceService.SERVICE_NAME)
@Transactional(rollbackFor = Throwable.class, propagation = Propagation.REQUIRED)
public class IbatisPersistenceServiceImpl implements IbatisPersistenceService, Serializable {

	private static final long serialVersionUID = -5835791092078270036L;

	@PersistenceContext(unitName = "entityManagerFactory")
	private EntityManager em;

	@Autowired
	private SqlSessionFactory sqlSessionFactory;

	public SqlSessionFactory getSqlSessionFactory() {
		return sqlSessionFactory;
	}

	public void setSqlSessionFactory(SqlSessionFactory sqlSessionFactory) {
		this.sqlSessionFactory = sqlSessionFactory;
	}
	
	public SqlSession getSqlSession(){
		return this.getSqlSessionFactory().openSession();
	}

	public EntityManager getEm() {
		return em;
	}

	public void setEm(EntityManager em) {
		this.em = em;
	}

	@Override
	public <T> List<T> executeQuery(String queryName, Map<Object, Object> params) throws ServiceException {
		try {
			return this.getSqlSession().selectList(queryName, params);
		} catch (Exception e) {
			throw ExceptionHandler.getInstance().generateException(ServiceException.class, e);
		}
	}

	@Override
	public <T> List<T> executeQuery(String queryName, Map<Object, Object> params, int firstResult, int maxResult) throws ServiceException {
		try {
			return this.getSqlSession().selectList(queryName, params, new RowBounds(firstResult, maxResult));
		} catch (Exception e) {
			throw ExceptionHandler.getInstance().generateException(ServiceException.class, e);
		}
	}

	@Override
	public Object getSingleRecordByQuery(String queryName, Map<Object, Object> params) throws ServiceException {
		try {
			return this.getSqlSession().selectOne(queryName, params);
		} catch (Exception e) {
			throw ExceptionHandler.getInstance().generateException(ServiceException.class, e);
		}
	}

	@Override
	public <T> List<T> executeQuery(String queryName, List<FilterCondition> conditions) throws ServiceException {
		try {
			Map<Object, Object> params = FilterCondition.getIbatisParamsFromFilterConditions(conditions);
			return executeQuery(queryName, params);
		} catch (Exception e) {
			throw ExceptionHandler.getInstance().generateException(ServiceException.class, e);
		}
	}

	public <T> List<T> getPage(String queryName, List<FilterCondition> criteriaList, int page, int pageSize) throws ServiceException {
		try {
			Map<Object, Object> params = FilterCondition.getIbatisParamsFromFilterConditions(criteriaList);
			int skip = pageSize * (page - 1);
			return executeQuery(queryName, params, skip, pageSize);
		} catch (Exception e) {
			if (log().isDebugEnabled()) {
				log().info("Erro Ao Executar Método:" + this.getClass().getSimpleName() + ".getPage(String queryName, List<FilterCondition> criteriaList, List<SortingCondition> sortingList, int page, int pageSize)");
				log().info(e);
			}
			throw ExceptionHandler.getInstance().generateException(ServiceException.class, e);
		}
	}

	public Log log() {
		return LogService.getInstance().getLogger(getClass().getName());
	}
}
