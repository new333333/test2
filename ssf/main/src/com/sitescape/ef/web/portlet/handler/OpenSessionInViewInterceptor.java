package com.sitescape.ef.web.portlet.handler;

import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;

import com.sitescape.ef.module.workflow.impl.WorkflowFactory;

import org.springframework.dao.DataAccessException;

/**
 * We are going to share the hibernate session that is opened at the start of the request wit
 * the JBPM workflow engine.  The spring-jbpm module, tries to close the jbpm session at 
 * the end of a transaction.  This in turn tries to close the hibernate session.  We don't 
 * want the session closed until the request ends.
 * 
 * This interceptor will close the jbpm session if it is registered with
 * TransactionSynchronization.  We don't pre-open a jbpm session since it is not
 * always needed, but once it is opened it remains so until the request is terminated.
 * before we clos
 * @author Janet McCann
 *
 */
public class OpenSessionInViewInterceptor extends 
	org.springframework.web.portlet.support.hibernate3.OpenSessionInViewInterceptor {
    private WorkflowFactory workflowFactory;
	
	public void setWorkflowFactory(WorkflowFactory workflowFactory) {
	        this.workflowFactory = workflowFactory;
	}
	public void afterCompletion(
			PortletRequest request, PortletResponse response, Object handler, Exception ex)
			throws DataAccessException {
		super.afterCompletion(request, response, handler, ex);
		workflowFactory.releaseSession();
		
	}

}
