package com.sitescape.ef.portlet.administration;
import java.util.Map;
import java.util.HashMap;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.WindowState;
import javax.portlet.PortletMode;

import org.springframework.web.servlet.ModelAndView;

import com.sitescape.ef.web.portlet.SAbstractController;
import com.sitescape.ef.web.WebKeys;
import com.sitescape.ef.portlet.forum.ActionUtil;
import com.sitescape.ef.security.function.FunctionManager;
import com.sitescape.ef.module.ldap.LdapConfig;
import com.sitescape.util.GetterUtil;
import com.sitescape.ef.jobs.Schedule;

public class ConfigureRolesController extends  SAbstractController {
	
	public void handleActionRequestInternal(ActionRequest request, ActionResponse response) throws Exception {
		Map formData = request.getParameterMap();
		if (formData.containsKey("okBtn")) {
			Map input = new HashMap();
			String val;
			//val = ActionUtil.getStringValue(formData, LdapConfig.SESSION_SYNC);
			//input.put(LdapConfig.SESSION_SYNC, Boolean.toString(GetterUtil.getBoolean(val, false)));
			//getLdapModule().updateLdapConfig(input);
			response.setRenderParameter(WebKeys.ACTION, "");
			response.setWindowState(WindowState.NORMAL);
			response.setPortletMode(PortletMode.VIEW);
		} else if (formData.containsKey("cancelBtn")) {
			response.setRenderParameter(WebKeys.ACTION, "");
			response.setWindowState(WindowState.NORMAL);
			response.setPortletMode(PortletMode.VIEW);
		} else
			response.setRenderParameters(formData);
		
	}

	public ModelAndView handleRenderRequestInternal(RenderRequest request, 
			RenderResponse response) throws Exception {
		FunctionManager functionManager = getFunctionManager();
		
		return new ModelAndView("administration/configureRoles");
		
	}
}
