package br.com.cssis.webservice.security;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;

import br.com.cssis.commons.service.CommonsService;


public class UserDetailsAuthenticationProvider extends AbstractUserDetailsAuthenticationProvider {

	private Log log = LogFactory.getLog(UserDetailsAuthenticationProvider.class);

	@Autowired
	private CommonsService commonsService;

	@Override
	protected void additionalAuthenticationChecks(UserDetails userDetails, UsernamePasswordAuthenticationToken upat) throws AuthenticationException {
		if (userDetails == null) {
			throw new AuthenticationCredentialsNotFoundException("Credenciais inválidas.");
		}
		if (!userDetails.isAccountNonExpired()) {
			throw new AccountExpiredException("Usuário expirado.");
		}
		if (!userDetails.isAccountNonLocked()) {
			throw new LockedException("Usuário bloqueado.");
		}
		if (!userDetails.isEnabled()) {
			throw new DisabledException("Usuário desativado.");
		}

	}

	@Override
	protected UserDetails retrieveUser(String user, UsernamePasswordAuthenticationToken upat) throws AuthenticationException {
		return null;
	}

	public CommonsService getCommonsService() {
		return commonsService;
	}

	public void setCommonsService(CommonsService commonsService) {
		this.commonsService = commonsService;
	}	
}
