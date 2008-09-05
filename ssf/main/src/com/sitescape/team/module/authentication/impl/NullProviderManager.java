package com.sitescape.team.module.authentication.impl;

import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.MessageSource;
import org.springframework.security.Authentication;
import org.springframework.security.AuthenticationException;
import org.springframework.security.concurrent.ConcurrentSessionController;
import org.springframework.security.providers.ProviderManager;

public class NullProviderManager extends ProviderManager {
	public void afterPropertiesSet() throws Exception {
	}
	public Authentication doAuthentication(Authentication authentication) throws AuthenticationException {
		return null;
	}

	public List getProviders() {
		return new LinkedList();
	}

	public ConcurrentSessionController getSessionController() {
		return null;
	}

	public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
	}

	public void setMessageSource(MessageSource messageSource) {
	}

	public void setProviders(List providers) {
	}

	public void setSessionController(ConcurrentSessionController sessionController) {
	}

	public void setAdditionalExceptionMappings(Properties additionalExceptionMappings) {
	}
}
