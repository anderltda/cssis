package br.com.cssis.foundation.service;

import org.springframework.stereotype.Service;

import br.com.cssis.foundation.BasicObject;
import br.com.cssis.foundation.util.StringHelper;

public abstract class BasicServiceHandlerImpl extends BasicObject {
	private static final long serialVersionUID = 1L;
	
	protected String getServiceName() {
		Service service = this.getClass().getAnnotation(Service.class);
		if (service != null) {
			if (StringHelper.isEmpty(service.value())) {
				return StringHelper.firstLower(this.getClass().getSimpleName());
			} else
				return service.value();
		} else
			return null;
	}
	
}
