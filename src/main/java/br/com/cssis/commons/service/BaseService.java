package br.com.cssis.commons.service;

import java.util.Date;

import br.com.cssis.foundation.BaseEntity;
import br.com.cssis.foundation.service.PersistenceService;

public interface BaseService<I extends BaseEntity> extends PersistenceService<I> {
	
	Date getDateDB();
}
