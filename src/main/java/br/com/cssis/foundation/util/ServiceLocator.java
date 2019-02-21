package br.com.cssis.foundation.util;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.orm.jpa.EntityManagerFactoryUtils;
import org.springframework.stereotype.Service;

import br.com.cssis.foundation.BasicObject;
import br.com.cssis.foundation.validator.ValidatorService;

@Service(value = "ServiceLocator")
public class ServiceLocator extends BasicObject {
	private static final long serialVersionUID = 1L;

	private static ServiceLocator instance;

	private String emfName = "entityManagerFactory";

	@Autowired
	protected ApplicationContext applicationContext;

	public ServiceLocator() {
		instance = this;
	}

	public static ServiceLocator getInstance() {
		return instance;
	}

	public void setApplicationContext(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

	public Object getBean(String name) {
		return applicationContext.getBean(name);
	}

	public EntityManagerFactory getEMF() {
		return (EntityManagerFactory) applicationContext.getBean(getEmfName());
	}

	public EntityManager getEM() {
		return EntityManagerFactoryUtils.getTransactionalEntityManager(ServiceLocator.getInstance().getEMF());
	}

	public ValidatorService getValidatorService(String serviceName) {
		return (ValidatorService) getBean(serviceName);
	}

	public String getEmfName() {
		return emfName;
	}

	public void setEmfName(String emfName) {
		this.emfName = emfName;
	}

	@SuppressWarnings("unchecked")
	public <T extends ValidatorService> T getValidatorService(Class<T> targetValidatorType, String serviceName) {
		return (T) applicationContext.getBean(serviceName, targetValidatorType);
	}

}
