package br.com.cssis.foundation.service.impl;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.sql.DataSource;

import org.hibernate.LockMode;
import org.hibernate.Session;
import org.hibernate.jdbc.Work;
import org.hibernate.proxy.HibernateProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import br.com.cssis.foundation.BaseEntity;
import br.com.cssis.foundation.BasicEntityObject;
import br.com.cssis.foundation.BasicException;
import br.com.cssis.foundation.Constants;
import br.com.cssis.foundation.ExceptionConstants;
import br.com.cssis.foundation.ExceptionHandler;
import br.com.cssis.foundation.datasource.DataSourceCFG;
import br.com.cssis.foundation.query.impl.DatabaseQuery;
import br.com.cssis.foundation.query.impl.FilterCondition;
import br.com.cssis.foundation.query.impl.JDBCQueryRunner;
import br.com.cssis.foundation.query.impl.Operator;
import br.com.cssis.foundation.query.impl.QueryHandler;
import br.com.cssis.foundation.query.impl.SortingCondition;
import br.com.cssis.foundation.service.BasicServiceHandlerImpl;
import br.com.cssis.foundation.service.IbatisPersistenceService;
import br.com.cssis.foundation.service.PersistenceService;
import br.com.cssis.foundation.service.ServiceException;
import br.com.cssis.foundation.util.NumberHelper;
import br.com.cssis.foundation.util.SetHelper;
import br.com.cssis.foundation.util.StringHelper;
import br.com.cssis.foundation.validator.ValidatorService;
import br.com.cssis.foundation.validator.impl.GlobalColumnValidator;
import oracle.jdbc.OracleTypes;

@SuppressWarnings("unchecked")
@Transactional(rollbackFor = Throwable.class, propagation = Propagation.REQUIRED)
public abstract class PersistenceServiceImpl<I extends BaseEntity> extends BasicServiceHandlerImpl implements PersistenceService<I>  {
	
	private static final long serialVersionUID = 1L;

	@Autowired
	protected ApplicationContext applicationContext;
	
	@PersistenceContext(unitName = "entityManagerFactory")
	protected EntityManager em;

	@Resource(name = "oracleDataSource")
	protected DataSource defaultDS;

	@Autowired
	protected IbatisPersistenceService iBatisPersistenceService;
	
	@Resource(name = "transactionManager")
	protected JpaTransactionManager jtaTM;

	public EntityManager getEM() {
		return em;
	}
	
	public EntityManager getEM(String dataSourceName) {
		if(dataSourceName != null && !dataSourceName.isEmpty()){
			return ((EntityManagerFactory) applicationContext.getBean("entityManagerFactory" + dataSourceName.toUpperCase())).createEntityManager(); 
		}

		return em;
	}

	public JpaTransactionManager getJtaTM() {
		return jtaTM;
	}

	public void setJtaTM(JpaTransactionManager jtaTM) {
		this.jtaTM = jtaTM;
	}

	public DataSource getDefaultDS() {
		return defaultDS;
	}

	public void setDefaultDS(DataSource defaultDS) {
		this.defaultDS = defaultDS;
	}
	
	public IbatisPersistenceService getiBatisPersistenceService() {
		return iBatisPersistenceService;
	}

	public void setiBatisPersistenceService(IbatisPersistenceService iBatisPersistenceService) {
		this.iBatisPersistenceService = iBatisPersistenceService;
	}

	public Long idLoggedUser;
	
	private abstract class CountableWork extends ListableWork{
		private int count;
		private String baseSql;
		private List<String> paramNames;
		private List<Object> paramValues;
		
		public void setCount(int count) {
			this.count = count;
		}
		
		public int getCount() {
			return this.count;
		}

		public String getBaseSql() {
			return baseSql;
		}

		public void setBaseSql(String baseSql) {
			this.baseSql = baseSql;
		}

		public List<String> getParamNames() {
			return paramNames;
		}

		public void setParamNames(List<String> paramNames) {
			this.paramNames = paramNames;
		}

		public List<Object> getParamValues() {
			return paramValues;
		}

		public void setParamValues(List<Object> paramValues) {
			this.paramValues = paramValues;
		}
	}
	
	private abstract class BaseWork implements Work{
		private List<FilterCondition> criteriaList;
		private List<SortingCondition> sortingList;
		
		public void setCriteriaList(List<FilterCondition> criteriaList) {
			this.criteriaList = criteriaList;
		}
		
		public List<FilterCondition> getCriteriaList() {
			return criteriaList;
		}

		public List<SortingCondition> getSortingList() {
			return sortingList;
		}

		public void setSortingList(List<SortingCondition> sortingList) {
			this.sortingList = sortingList;
		}
	}
	
	private abstract class ListableWork extends BaseWork{
		private List list;
		private int pageSize;
		private String queryName;
		private int page;
		private DatabaseQuery query;
		
		public void setPage(int page) {
			this.page = page;
		}

		public int getPage() {
			return page;
		}

		public void setQueryName(String queryName) {
			this.queryName = queryName;
		}
		
		public void setQuery(DatabaseQuery query) {
			this.query = query;
		}

		public void setPageSize(int pageSize) {
			this.pageSize = pageSize;
		}
		
		public int getPageSize() {
			return pageSize;
		}

		public List getList() {
			return list;
		}

		public void setList(List list) {
			this.list = list;
		}

		public String getQueryName() {
			return queryName;
		}

		public DatabaseQuery getQuery() {
			return query;
		}
	}

	@Override
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public <T extends I, ID extends Serializable> T findById(Class<T> persistenceClass, ID id) throws ServiceException {
		try {
			EntityManager em = null;
			DataSourceCFG dataSourceCFG = (DataSourceCFG)persistenceClass.getAnnotation(DataSourceCFG.class);
			if(dataSourceCFG != null){
				em = getEM(dataSourceCFG.name());
			}else{
				em = getEM(null);
			}
			
			T value = em.find(persistenceClass, id);
			autorize(persistenceClass, value, Constants.K_AFTER_GET);
			return value;
		} catch (Exception e) {
			if (log().isDebugEnabled()) {
				log().info("Erro Ao Executar Método:" + this.getClass().getSimpleName() + ".findById(Class<T> persistenceClass, ID id)");
				log().info("persistenceClass:" + persistenceClass.getClass().getSimpleName() + " ID:" + id);
				log().info(e);
			}
			throw ExceptionHandler.getInstance().generateException(ServiceException.class, e);
		}

	}

	@Override
	public <T extends I> T save(T transientInstance) throws ServiceException {
		try {
			if (transientInstance == null) return transientInstance;
			if (transientInstance.isIdNull()) {
				validate(transientInstance, Constants.K_BEFORE_INSERT);
				//this.getTriggerService().trigger(transientInstance, Constants.K_BEFORE_INSERT);
				em.persist(transientInstance);
				//this.getTriggerService().trigger(transientInstance, Constants.K_AFTER_INSERT);
				validate(transientInstance, Constants.K_AFTER_INSERT);
				return transientInstance;
			} else {
				return update(transientInstance);
			}
		} catch (Exception e) {
			if (log().isDebugEnabled()) {
				log().info("Erro Ao Executar Método:" + this.getClass().getSimpleName() + ".save(T transientInstance)");
				log().info("persistenceClass:" + transientInstance.getClass().getSimpleName());
				log().info(e);
			}
			throw ExceptionHandler.getInstance().generateException(ServiceException.class, e);
		}
	}
	
	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Throwable.class)
	public <T extends BasicEntityObject> T saveUseNewTransaction(T transientInstance) throws ServiceException {
		try {
			if (transientInstance == null)
				return transientInstance;
			if (transientInstance.isIdNull()) {
				validate(transientInstance, Constants.K_BEFORE_INSERT);
				em.persist(transientInstance);
				validate(transientInstance, Constants.K_AFTER_INSERT);
				return transientInstance;
			} else {
				return updateUseNewTransaction(transientInstance);
			}
		} catch (Exception e) {
			if (log().isDebugEnabled()) {
				log().info("Erro Ao Executar Método:" + this.getClass().getSimpleName() + ".saveNewTransaction(T transientInstance)");
				log().info("persistenceClass:" + transientInstance.getClass().getSimpleName());
				log().info(e);
			}
			throw ExceptionHandler.getInstance().generateException(ServiceException.class, e);
		}
	}

	@Override
	public <T extends I> T update(T detachedInstance) throws ServiceException {
		try {
			validate(detachedInstance, Constants.K_BEFORE_UPDATE);
			T returnValue = em.merge(detachedInstance);
			validate(detachedInstance, Constants.K_AFTER_UPDATE);
			return returnValue;
		} catch (Exception e) {
			if (log().isDebugEnabled()) {
				log().info("Erro Ao Executar Método:" + this.getClass().getSimpleName() + ".update(T detachedInstance)");
				log().info("persistenceClass:" + detachedInstance.getClass().getSimpleName());
				log().info(e);
			}
			throw ExceptionHandler.getInstance().generateException(ServiceException.class, e);
		}
	}
	
	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Throwable.class)
	public <T extends BasicEntityObject> T updateUseNewTransaction(T detachedInstance) throws ServiceException {
		try {
			validate(detachedInstance, Constants.K_BEFORE_UPDATE);
			T returnValue = em.merge(detachedInstance);
			validate(detachedInstance, Constants.K_AFTER_UPDATE);
			return returnValue;
		} catch (Exception e) {
			if (log().isDebugEnabled()) {
				log().info("Erro Ao Executar Método:" + this.getClass().getSimpleName() + ".updateUseNewTransaction(T detachedInstance)");
				log().info("persistenceClass:" + detachedInstance.getClass().getSimpleName());
				log().info(e);
			}
			throw ExceptionHandler.getInstance().generateException(ServiceException.class, e);
		}
	}

	@Override
	public <T extends I, ID extends Serializable> void delete(Class<T> persistenceClass, ID id) throws ServiceException {
		try {
			T ent = em.find(persistenceClass, id);
			validate(ent, Constants.K_BEFORE_DELETE);
			em.remove(ent);
			validate(ent, Constants.K_AFTER_DELETE);
		} catch (Exception e) {
			if (log().isDebugEnabled()) {
				log().info("Erro Ao Executar Método:" + this.getClass().getSimpleName() + ".delete(Class<T> persistenceClass, ID id)");
				log().info("persistenceClass:" + persistenceClass.getClass().getSimpleName() + " ID:" + id);
				log().info(e);
			}
			throw ExceptionHandler.getInstance().generateException(ServiceException.class, e);
		}
	}

	@Override
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public <T extends I> void delete(T persistenceObject) throws ServiceException {
		try {
			validate(persistenceObject, Constants.K_BEFORE_DELETE);
			em.remove(persistenceObject);
			validate(persistenceObject, Constants.K_AFTER_DELETE);
		} catch (Exception e) {
			if (log().isDebugEnabled()) {
				log().info("Erro Ao Executar Método:" + this.getClass().getSimpleName() + ".delete(T persistenceObject)");
				log().info("persistenceClass:" + persistenceObject.getClass().getSimpleName() + " object:" + persistenceObject);
				log().info(e);
			}
			throw ExceptionHandler.getInstance().generateException(ServiceException.class, e);
		}
	}
	
	@Override
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public <T> List<T> getPage(String queryName, List<FilterCondition> criteriaList, List<SortingCondition> sortingList, int page, int pageSize) throws ServiceException {
		try{
			ListableWork work = new ListableWork() {
				@Override
				public void execute(Connection conn) throws SQLException {
					int firstResult = calculateStartPosition(this.getPage(), this.getPageSize());
					try {
						this.setList(JDBCQueryRunner.getInstance().executeQuery(this.getQueryName(), this.getCriteriaList(), this.getSortingList(), firstResult, this.getPageSize(), conn));
					} catch (Exception e) {
						if (log().isDebugEnabled()) {
							log().info("Erro Ao Executar Método:" + this.getClass().getSimpleName() + ".getPage(String queryName, List<FilterCondition> criteriaList, List<SortingCondition> sortingList, int page, int pageSize)");
							log().info("queryName:" + this.getQueryName() + " criteriaList:" + StringHelper.collectionAsString(this.getCriteriaList()) + " sortingList:" + StringHelper.collectionAsString(this.getSortingList()) + " page:" + this.getPage() + " pageSize:" + this.getPageSize());
			
							log().info(e);
						}
						throw new SQLException(ExceptionHandler.getInstance().handleException(e).getMessage(), e);
					}
				}
			};
			
			work.setPageSize(pageSize);
			work.setQueryName(queryName);
			work.setCriteriaList(criteriaList);
			work.setSortingList(sortingList);
			work.setPage(page);
			
			((Session) getEM(QueryHandler.getInstance().getQuery(queryName).getDatasourceName()).getDelegate()).doWork(work);
			
			return work.getList();
		}catch (Exception e){
			throw new ServiceException(e);
		}
	}
	
	@Override
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public <T> List<T> getPage(DatabaseQuery databaseQuery, List<FilterCondition> criteriaList, List<SortingCondition> sortingList, int page, int pageSize) throws ServiceException {
		try{
			ListableWork work = new ListableWork() {
				@Override
				public void execute(Connection conn) throws SQLException {
					int firstResult = calculateStartPosition(this.getPage(), this.getPageSize());
					try {
						this.setList(JDBCQueryRunner.getInstance().executeQuery(this.getQuery() , this.getCriteriaList(), this.getSortingList(), firstResult, this.getPageSize(), conn));
					} catch (Exception e) {
						if (log().isDebugEnabled()) {
							log().info("Erro Ao Executar Método:" + this.getClass().getSimpleName() + ".getPage(String queryName, List<FilterCondition> criteriaList, List<SortingCondition> sortingList, int page, int pageSize)");
							log().info("queryName:" + this.getQuery().getName() + " criteriaList:" + StringHelper.collectionAsString(this.getCriteriaList()) + " sortingList:" + StringHelper.collectionAsString(this.getSortingList()) + " page:" + this.getPage() + " pageSize:" + this.getPageSize());
			
							log().info(e);
						}
						new SQLException(ExceptionHandler.getInstance().handleException(e).getMessage(), e);
					}
				}
			};
			
			work.setPageSize(pageSize);
			work.setQuery(databaseQuery);
			work.setCriteriaList(criteriaList);
			work.setSortingList(sortingList);
			work.setPage(page);
			
			((Session) getEM(databaseQuery.getDatasourceName()).getDelegate()).doWork(work);
			
			return work.getList();
		}catch (Exception e){
			throw new ServiceException(e);
		}
	}

	@Override
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public <T> List<T> getPage(Class<T> persistenceClass, List<FilterCondition> criteriaList, List<SortingCondition> sortingList, int page, int pageSize) throws ServiceException {
		if (page <= 0) {
			page = 1;
		}
		int firstResult = calculateStartPosition(page, pageSize);

		try {
			Query q = buildQuery("select model from " + persistenceClass.getName() + " model where 1=1", criteriaList, sortingList, persistenceClass);
			if (pageSize > 0) {
				q.setFirstResult(firstResult);
				q.setMaxResults(pageSize);
			}
			List<T> result = q.getResultList();
			return result;
		} catch (Exception e) {
			if (log().isDebugEnabled()) {
				log().info("Erro Ao Executar Método:" + this.getClass().getSimpleName() + ".getPage(Class<T> persistenceClass, List<FilterCondition> criteriaList, List<SortingCondition> sortingList, int page, int pageSize)");
				log().info("persistenceClass:" + persistenceClass.getClass().getSimpleName() + " criteriaList:" + StringHelper.collectionAsString(criteriaList) + " sortingList:" + StringHelper.collectionAsString(sortingList) + " page:" + page + " pageSize:" + pageSize);

				log().info(e);
			}
			throw ExceptionHandler.getInstance().generateException(ServiceException.class, e);
		}
	}

	private int calculateStartPosition(int page, int pageSize) {
		int firstResult = page > 1 ? (page * pageSize) - pageSize : 0;
		return firstResult;
	}

	protected Query buildQuery(String queryString, List<FilterCondition> criteriaList, List<SortingCondition> sortingList, Class<?> persistenceClass) {
		EntityManager em = null;
		DataSourceCFG dataSourceCFG = (DataSourceCFG)persistenceClass.getAnnotation(DataSourceCFG.class);
		if(dataSourceCFG != null){
			em = getEM(dataSourceCFG.name());
		}else{
			em = getEM(null);
		}
		
		Query q = null;
		StringBuilder whereString = new StringBuilder();
		ArrayList<Object> wildCardValues = new ArrayList<Object>();
		if (SetHelper.nonEmpty(criteriaList)) {
			for (FilterCondition cond : criteriaList) {
				if (cond.getOperator() == Operator.EXISTS && cond.getReferenceProperty() != null) {
					cond.setReferenceClass(persistenceClass);
				}

				try {
					whereString.append(" and ").append("(").append(cond.getSQLWildCardRepresentation(wildCardValues)).append(")");
				} catch (BasicException e) {
					log().error(e);
					// intencionamente colocado.
				}
			}
		}

		if (SetHelper.nonEmpty(sortingList)) {
			whereString.append(" order by ");
			for (SortingCondition sortCond : sortingList) {
				whereString.append(sortCond.getAlias()).append(sortCond.getOrientationRepresentation()).append(",");
			}
			StringHelper.removeLast(whereString);
		}

		q = em.createQuery(queryString + whereString.toString());
		if (SetHelper.nonEmpty(criteriaList)) {
			for (int i = 0; i < wildCardValues.size(); i++) {
				if (FilterCondition.BIND_BY_NAME) {
					q.setParameter(FilterCondition.getBindName(i + 1), wildCardValues.get(i));
				} else {
					q.setParameter(i + 1, wildCardValues.get(i));
				}

			}
		}
		return q;

	}

	
	public void testCallProc() throws ServiceException {
		
		try {
			Connection conn = this.getDefaultDS().getConnection();
			String proc3StoredProcedure = "{call ODONTO_SIS.pk_test_jesiel.prc_test_jesiel2(?, ?) }";
			CallableStatement cs = conn.prepareCall(proc3StoredProcedure);
			cs.setFloat(1, new Float(1));
			cs.registerOutParameter(2, OracleTypes.VARCHAR);		
			
			cs.execute();
			
			System.out.println(cs.getFloat(1));
			conn.close();
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public <T> List<T> findByQuery(String queryName, List<FilterCondition> criteriaList, List<SortingCondition> sortingList) throws ServiceException {
		try{
			ListableWork work = new ListableWork() {
				@Override
				public void execute(Connection conn) throws SQLException {
					int firstResult = calculateStartPosition(1, 1);
					try {
						// se nao conseguiu determinar, devera trazer tudo
						int recordsToFetch = -1;
	
						this.setList(JDBCQueryRunner.getInstance().executeQuery(this.getQueryName(), this.getCriteriaList(), this.getSortingList(), firstResult, recordsToFetch, conn));
					} catch (Exception e) {
						if (log().isDebugEnabled()) {
							log().info("Erro Ao Executar Método:" + this.getClass().getSimpleName() + ".findByQuery(String queryName, List<FilterCondition> criteriaList, List<SortingCondition> sortingList)");
							log().info("queryName:" + this.getQueryName() + " criteriaList:" + StringHelper.collectionAsString(this.getCriteriaList()) + " sortingList:" + StringHelper.collectionAsString(this.getSortingList()));
							log().info(e);
						}
					}
				}
			};
			
			work.setQueryName(queryName);
			work.setCriteriaList(criteriaList);
			work.setSortingList(sortingList);
			
			((Session) getEM(QueryHandler.getInstance().getQuery(queryName).getDatasourceName()).getDelegate()).doWork(work);
			
			return work.getList();
		}catch (Exception e){
			throw new ServiceException(e);
		}
	}
	
	@Override
	public <T> List<T> findByQuery(DatabaseQuery databaseQuery, List<FilterCondition> criteriaList, List<SortingCondition> sortingList) throws ServiceException {
		try{
			ListableWork work = new ListableWork() {
				@Override
				public void execute(Connection conn) throws SQLException {		
					int firstResult = calculateStartPosition(1, 1);
					try {
						// se nao conseguiu determinar, devera trazer tudo
						int recordsToFetch = -1;
			
						this.setList(JDBCQueryRunner.getInstance().executeQuery(this.getQuery(), this.getCriteriaList(), this.getSortingList(), firstResult, recordsToFetch, conn));
					} catch (Exception e) {
						if (log().isDebugEnabled()) {
							log().info("Erro Ao Executar Método:" + this.getClass().getSimpleName() + ".findByQuery(String queryName, List<FilterCondition> criteriaList, List<SortingCondition> sortingList)");
							log().info("queryName:" + this.getQuery().getName() + " criteriaList:" + StringHelper.collectionAsString(this.getCriteriaList()) + " sortingList:" + StringHelper.collectionAsString(this.getSortingList()));
							log().info(e);
						}
						throw new SQLException(ExceptionHandler.getInstance().handleException(e).getMessage(), e);
					}
				}
			};
			
			work.setQuery(databaseQuery);
			work.setCriteriaList(criteriaList);
			work.setSortingList(sortingList);
			
			((Session) getEM(databaseQuery.getDatasourceName()).getDelegate()).doWork(work);
			
			return work.getList();
		}catch (Exception e){
			throw new ServiceException(e);
		}
	}

	@Override
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public <T> List<T> findByQuery(String queryName, List<FilterCondition> criteriaList) throws ServiceException {
		try {
			return findByQuery(queryName, criteriaList, null);
		} catch (ServiceException e) {
			if (log().isDebugEnabled()) {
				log().info("Erro Ao Executar Método:" + this.getClass().getSimpleName() + ".findByQuery(String queryName, List<FilterCondition> criteriaList)");
				log().info("Call method: findByQuery(queryName, criteriaList, null)");
			}
			throw e;
		}
	}

	@Override
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getCountByQuery(String queryName, List<FilterCondition> criteriaList) throws ServiceException {
		try{
			CountableWork work = new CountableWork() {
				@Override
				public void execute(Connection conn) throws SQLException {
					try {
						// faz o count
						this.setCount(JDBCQueryRunner.getInstance().getCount(this.getQueryName(), this.getCriteriaList(), conn));
					} catch (Exception e) {
						if (log().isDebugEnabled()) {
							log().info("Erro Ao Executar Método:" + this.getClass().getSimpleName() + "getCountByQuery(String queryName, List<FilterCondition> criteriaList)");
							log().info("queryName:" + this.getQueryName() + " criteriaList:" + StringHelper.collectionAsString(this.getCriteriaList()));
							log().info(e);
						}
						throw new SQLException(ExceptionHandler.getInstance().handleException(e).getMessage(), e);
					}
				}
			};
			
			work.setQueryName(queryName);
			work.setCriteriaList(criteriaList);
	
			((Session) getEM(QueryHandler.getInstance().getQuery(queryName).getDatasourceName()).getDelegate()).doWork(work);
			
			return work.getCount();
		}catch (Exception e){
			throw new ServiceException(e);
		}
	}

	@Override
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public <T extends I> int getCountByProperties(Class<T> persistenceClass, List<FilterCondition> criteriaList) throws ServiceException {
		try {
			Query qCount = buildQuery("select count(*) from " + persistenceClass.getName() + " model where 1=1", criteriaList, null, persistenceClass);
			int count = NumberHelper.intValue(qCount.getSingleResult());
			return count;
		} catch (Exception e) {
			if (log().isDebugEnabled()) {
				log().info("Erro Ao Executar Método:" + this.getClass().getSimpleName() + "getCountByProperties(Class<T> persistenceClass, List<FilterCondition> criteriaList");
				log().info("persistenceClass:" + this.getClass() + " criteriaList:" + StringHelper.collectionAsString(criteriaList));
				log().info(e);
			}
			throw ExceptionHandler.getInstance().generateException(ServiceException.class, e);
		}
	}

	@Override
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public <T> List<T> findByProperties(Class<T> persistenceClass, List<FilterCondition> criteriaList, List<SortingCondition> sortingList) throws ServiceException {
		try {
			Query q = buildQuery("select model from " + persistenceClass.getName() + " model where 1=1", criteriaList, sortingList, persistenceClass);
			List<T> result = q.getResultList();
			return result;
		} catch (Exception e) {
			if (log().isDebugEnabled()) {
				log().info("Erro Ao Executar Método:" + this.getClass().getSimpleName() + ".findByProperties(Class<T> persistenceClass, List<FilterCondition> criteriaList, List<SortingCondition> sortingList)");
				log().info("persistenceClass:" + persistenceClass.getClass().getCanonicalName() + " criteriaList:" + StringHelper.collectionAsString(criteriaList) + " sortingList:" + StringHelper.collectionAsString(sortingList));
				log().info(e);
			}
			throw ExceptionHandler.getInstance().generateException(ServiceException.class, e);
		}
	}

	@Override
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public <T extends I> T getSingleRecordByProperties(Class<T> persistenceClass, List<FilterCondition> criteriaList) throws ServiceException {
		try {
			Query q = buildQuery("select model from " + persistenceClass.getName() + " model where 1=1", criteriaList, null, persistenceClass);
			List<T> result = q.getResultList();
			switch (result.size()) {
			case 0:
				return null;
			case 1:
				return result.get(0);
			default:
				throw ExceptionHandler.getInstance().generateException(ServiceException.class, ExceptionConstants.K_NOSINGLE_RECORDFOUND, "Quantidade Inválida de Registros para o método getSingleRecordByProperties");
			}
		} catch (Exception e) {
			if (log().isDebugEnabled()) {
				log().info("Erro Ao Executar Método:" + this.getClass().getSimpleName() + ".getSingleRecordByProperties(Class<T> persistenceClass, List<FilterCondition> criteriaList)");
				log().info("persistenceClass:" + persistenceClass.getClass().getCanonicalName() + " criteriaList:" + StringHelper.collectionAsString(criteriaList));
				log().info(e);
			}
			throw ExceptionHandler.getInstance().generateException(ServiceException.class, e);
		}
	}

	@Override
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public <T> T getSingleRecordByQuery(String queryName, List<FilterCondition> criteriaList) throws ServiceException {
		try {
			List<T> result = findByQuery(queryName, criteriaList, null);
			switch (result.size()) {
			case 0:
				return null;
			case 1:
				return result.get(0);
			default:
				throw ExceptionHandler.getInstance().generateException(ServiceException.class, ExceptionConstants.K_NOSINGLE_RECORDFOUND, "Quantidade Inválida de Registros para o método getSingleRecordByProperties");
			}
		} catch (ServiceException e) {
			if (log().isDebugEnabled()) {
				log().info("Erro Ao Executar Método:" + this.getClass().getSimpleName() + ".getSingleRecordByQuery(String queryName, List<FilterCondition> criteriaList)");
				log().info("Call method: findByQuery(queryName, criteriaList, null)");
			}
			throw e;
		}
	}

	public abstract ValidatorService getValidatorService();

	public abstract <T extends BaseEntity> Method getValidatorMethod(T target, int event);

	@Override
	public <T extends BaseEntity> void validate(T target) throws ServiceException {
		validate(target, Constants.K_ANY);
	}

	@Override
	public <T extends BaseEntity> void validate(T target, int event) throws ServiceException {
		if (target.isReentrant() || target.isDoNotValidate()) return;

		// validação de coluns no before update/create
		if (Constants.isBeforeInsert(event) || Constants.isBeforeUpdate(event)) {
			String errors = GlobalColumnValidator.getInstance().validate(target);
			if (StringHelper.nonEmpty(errors)) {
				throw ExceptionHandler.getInstance().generateException(ServiceException.class, ExceptionConstants.K_INVALID_FIELD_VALUE, errors);
			}
		}

		Method m = getValidatorMethod(target, event);
		if (m == null) return;
		Object[] paramValues = { target, event };
		try {
			target.setReentrant(true);
			if(getValidatorService() != null){
				m.invoke(getValidatorService(), paramValues);
			}
		} catch (Exception e) {
			throw ExceptionHandler.getInstance().generateException(ServiceException.class, e);
		} finally {
			target.setReentrant(false);
		}

		// validação de segurança posso fazer?
		autorize(getTargetClass(target), target, event);
	}
	
	@Override
	public int executeSqlStatement(String baseSql, List<String> paramNames, List<Object> paramValues) throws ServiceException {
		try{
			CountableWork work = new CountableWork() {
				@Override
				public void execute(Connection conn) throws SQLException {
					try {
						// faz o count
						this.setCount(JDBCQueryRunner.getInstance().executeSqlStatement(conn, this.getBaseSql(), this.getParamNames(), this.getParamValues()));
					} catch (Exception e) {
						e.printStackTrace();
						if (log().isDebugEnabled()) {
							log().info("Erro Ao Executar Método:" + this.getClass().getSimpleName() + ".executeSqlStatement(String baseSql, List<String> paramNames, List<Object> paramValues)");
							log().info("Sql Base :" + this.getBaseSql() + " paramNames:" + StringHelper.collectionAsString(this.getParamNames()) + " paramValues:" + StringHelper.collectionAsString(this.getParamValues()));
							log().info(e);
						}
						throw new SQLException(ExceptionHandler.getInstance().handleException(e).getMessage(), e);
					}
				}
			};
			
			work.setBaseSql(baseSql);
			work.setParamNames(paramNames);
			work.setParamValues(paramValues);
			
			((Session) getEM().getDelegate()).doWork(work);
			
			return work.getCount();
		}catch (Exception e){
			throw new ServiceException(e);
		}
	}

	private <T> void autorize(Class<T> targetClass, T target, int event) throws ServiceException {
		return;
		/*
		 * BasicSegurancaAutorizador autorizador =
		 * CredenciaisSeguranca.getInstance().getAutorizador(); if (autorizador
		 * != null && target != null) { ResultadoAutorizacao ra =
		 * autorizador.isAutorizado(targetClass, target); if (ra != null &&
		 * ra.isAutorizado() == false) { if (ra.getMensagem() != null) { throw
		 * ExceptionHandler
		 * .getInstance().generateException(ServiceException.class,
		 * ExceptionConstants.K_NOCREDENTIALS, ra.getMensagem()); } else { throw
		 * ExceptionHandler
		 * .getInstance().generateException(ServiceException.class,
		 * ExceptionConstants.K_NOCREDENTIALS, "Acesso Não Autorizado."); } } }
		 */
	}

	@Override
	public Long getIdLoggedUser() {
		return idLoggedUser;
	}

	@Override
	public void setIdLoggedUser(Long idLoggedUser) {
		this.idLoggedUser = idLoggedUser;
	}

	public <T extends BaseEntity> Class<T> getTargetClass(T target) {
		Class<?> targetClass = null;
		if (target instanceof HibernateProxy) {
			HibernateProxy object = (HibernateProxy) target;
			targetClass = object.getHibernateLazyInitializer().getPersistentClass();
		} else {
			targetClass = target.getClass();
		}
		return (Class<T>) targetClass;
	}

	private Connection getJTAConnection(EntityManager em) throws ServiceException {
		Connection conn;
		try {
			conn = this.getJtaTM().getJpaDialect().getJdbcConnection(em, false).getConnection();
			return conn;
		} catch (Exception e) {
			throw ExceptionHandler.getInstance().generateException(ServiceException.class, e);
		}
	}
	@Override
	public Session getSession() throws ServiceException {
		Session session = (Session) em.getDelegate();
		return session;
	}

	@Override
	public <T extends BaseEntity> void lockRecord(T target) throws ServiceException {
		if (target != null && !target.isIdNull()) getSession().lock(target, LockMode.UPGRADE);
	}

	/**
	 * Método responsável por executar uma instrução SQL ou chamada de stores procedures que 
	 * estão mapeadas utilizando o framework iBatis.
	 * 
	 * @param procedureName Nome do mapeamento do iBatis, podendo ser uma instrução SQL ou stored procedure.
	 * @param conditions Lista de filtros.
	 * @return <T> List<T>
	 * @throws ServiceException
	 */
	@Override
	public <T> List<T> executeQuery(String mapName, List<FilterCondition> conditions)throws ServiceException{	
		Map<Object,Object> params = FilterCondition.getIbatisParamsFromFilterConditions(conditions);
		return iBatisPersistenceService.executeQuery(mapName, params);
	}
	
	@Override
	public <T> List<T> executeQueryIbatis(final String mapName, Map<Object, Object> params) throws ServiceException {
		return iBatisPersistenceService.executeQuery(mapName, params);
	}
	
	@Override
	public Date getDateDB() {
		Query query = getEM().createNativeQuery("select sysdate from dual");
		Date dataBD = (Date) query.getSingleResult();
		return dataBD;
	}	
}