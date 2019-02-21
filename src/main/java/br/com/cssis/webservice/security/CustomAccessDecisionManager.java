package br.com.cssis.webservice.security;

import java.util.Collection;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.vote.AffirmativeBased;
import org.springframework.security.core.Authentication;

import br.com.cssis.commons.service.CommonsService;

public class CustomAccessDecisionManager extends AffirmativeBased {
	private static final Log LOG = LogFactory.getLog(CustomAccessDecisionManager.class);

	@Autowired
	private CommonsService commonsService;

	public CustomAccessDecisionManager(List<AccessDecisionVoter<? extends Object>> decisionVoters) {
		super(decisionVoters);
	}
	
	@Override
	public void decide(Authentication authentication, Object object, Collection<ConfigAttribute> configAttributes) throws AccessDeniedException {
		
	}

	public CommonsService getCommonsService() {
		return commonsService;
	}

	public void setCommonsService(CommonsService commonsService) {
		this.commonsService = commonsService;
	}
}
