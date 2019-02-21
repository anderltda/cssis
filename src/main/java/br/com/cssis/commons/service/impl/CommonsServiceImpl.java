package br.com.cssis.commons.service.impl;

import java.lang.reflect.Method;

import org.springframework.stereotype.Service;

import br.com.cssis.commons.service.CommonsService;
import br.com.cssis.foundation.BaseEntity;
import br.com.cssis.foundation.validator.ValidatorService;

@Service(value = CommonsService.SERVICE_NAME)
public class CommonsServiceImpl extends BaseServiceImpl<BaseEntity> implements CommonsService {

	private static final long serialVersionUID = 1L;

	@Override
	public ValidatorService getValidatorService() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T extends BaseEntity> Method getValidatorMethod(T target, int event) {
		// TODO Auto-generated method stub
		return null;
	}

	
}