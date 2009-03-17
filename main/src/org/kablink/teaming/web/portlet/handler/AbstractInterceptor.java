package org.kablink.teaming.web.portlet.handler;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.springframework.web.portlet.HandlerInterceptor;
import org.springframework.web.portlet.ModelAndView;

public class AbstractInterceptor implements HandlerInterceptor {

	public boolean preHandleAction(ActionRequest request, ActionResponse response, Object handler)
	    throws Exception {
		return true;
	}

	public void afterActionCompletion(
			ActionRequest request, ActionResponse response, Object handler, Exception ex)
	    throws Exception {
		
	}

	public boolean preHandleRender(RenderRequest request, RenderResponse response, Object handler)
	    throws Exception {
		return true;
	}

	public void postHandleRender(
			RenderRequest request, RenderResponse response, Object handler, ModelAndView modelAndView)
			throws Exception {
		
	}

	public void afterRenderCompletion(
			RenderRequest request, RenderResponse response, Object handler, Exception ex)
			throws Exception {
		
	}

}
