package br.com.cssis.webservice.security;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.filter.DelegatingFilterProxy;

public class SpringSecurityFilterChain extends DelegatingFilterProxy {

	private static final String REGEX_WS_CAIXA = "(.)*ws/caixa(.)*";
	private static final String REGEX_WS_TISS_V30100 = "(.)*ws/tiss/v30100(.)*";
	private static final String HEADER_X_FORWARDED_FOR = "X-FORWARDED-FOR";
	private static final Log LOG = LogFactory.getLog(SpringSecurityFilterChain.class);

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws ServletException, IOException {
		LOG.debug("-----------------------------");
		LOG.debug("-----------------------------");
		super.doFilter(request, response, filterChain);
	}

}
