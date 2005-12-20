package com.sitescape.ef.portlet.widget_test;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletMode;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.WindowState;

import org.hibernate.SessionFactory;
import org.springframework.web.servlet.ModelAndView;

import com.sitescape.ef.util.SpringContextUtil;
import com.sitescape.ef.web.WebKeys;
import com.sitescape.ef.web.portlet.SAbstractController;

public class FlushController extends SAbstractController {

	public void handleActionRequestInternal(ActionRequest request, ActionResponse response)
	throws Exception {
		//There is no action. Just go to the render phase
		SessionFactory sF = (SessionFactory)SpringContextUtil.getBean("sessionFactory");
		sF.evict(com.sitescape.ef.domain.Principal.class);
		sF.evict(com.sitescape.ef.domain.Binder.class);
		sF.evictQueries();
		response.setRenderParameter(WebKeys.ACTION, "");
		response.setWindowState(WindowState.NORMAL);
		response.setPortletMode(PortletMode.VIEW);
	}
	

}
