package com.sitescape.ef.web.servlet.handler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.dao.DataAccessException;

import com.sitescape.ef.module.workflow.impl.WorkflowFactory;

/**
 * See com.sitescape.ef.web.portlet.handler.OpenSessionInViewInterceptor for 
 * explanation
 * @author Janet McCann
 *
 */
public class OpenSessionInViewInterceptor extends 
	org.springframework.orm.hibernate3.support.OpenSessionInViewInterceptor {

   private WorkflowFactory workflowFactory;
		
	public void setWorkflowFactory(WorkflowFactory workflowFactory) {
        this.workflowFactory = workflowFactory;
	}
	public void afterCompletion(
		HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
		throws DataAccessException {
		
		super.afterCompletion(request, response, handler, ex);
		workflowFactory.releaseSession();
		
	}
}
