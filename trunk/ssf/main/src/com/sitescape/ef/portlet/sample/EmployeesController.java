package com.sitescape.ef.portlet.sample;

import java.util.HashMap;
import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.springframework.web.servlet.ModelAndView;

import com.sitescape.ef.portletadapter.AdaptedPortletURL;
import com.sitescape.ef.rss.util.UrlUtil;
import com.sitescape.ef.web.portlet.SAbstractController;
import com.sitescape.ef.web.util.DebugHelper;

public class EmployeesController extends SAbstractController {

	public void handleActionRequestInternal(ActionRequest request, ActionResponse response)
	throws Exception {
		//There is no action. Just go to the render phase
	}
	
	public ModelAndView handleRenderRequestInternal(RenderRequest request, 
			RenderResponse response) throws Exception {
		
		/*
		AdaptedPortletURL url = new AdaptedPortletURL("jongportlet", false);
		url.setSecure(true);
		url.setParameter("firstparam", "10");
		url.setParameter("secondparam", "Testing");
		System.out.println(url.toString());
		*/
		
    	// Print debug information pertaining to cross context session sharing
		DebugHelper.testRequestEnv("EmployeesController", request);
		
		// Get the list of all employees from the business tier 
		// (via EmployeeModule) and create a ModelAndView datastructure
		// that the Spring's PortalMVC expects.
		Map models = new HashMap();
		models.put("employees", getEmployeeModule().getAllEmployees());
		//models.put("forumUrl", "http://localhost:8080/c/portal/layout?p_l_id=PRI.15.1&p_p_id=ss_forum_WAR_ssf_INSTANCE_d6E7&p_p_action=0&p_p_state=maximized&p_p_mode=view&p_p_col_id=column-2&p_p_col_pos=2&p_p_col_count=9&_ss_forum_WAR_ssf_INSTANCE_d6E7_action=view_folder_listing&_ss_forum_WAR_ssf_INSTANCE_d6E7_binderId=74");
		//models.put("forumUrl", "http://localhost:8080/c/portal/layout?p_l_id=PRI.15.2&p_p_id=ss_forum_WAR_ssf_INSTANCE_d6E7&p_p_action=0&p_p_state=maximized&p_p_mode=view&p_p_col_id=column-2&p_p_col_pos=2&p_p_col_count=9&_ss_forum_WAR_ssf_INSTANCE_d6E7_action=view_folder_listing&_ss_forum_WAR_ssf_INSTANCE_d6E7_binderId=74");
		
		return new ModelAndView("sample/employeesView", models);
	}

}
