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
import com.sitescape.ef.module.ldap.LdapConfig;
import com.sitescape.ef.web.util.PortletRequestUtils;
import com.sitescape.ef.web.util.ScheduleHelper;

public class ConfigureLdapController extends  SAbstractController {
	
	public void handleActionRequestInternal(ActionRequest request, ActionResponse response) throws Exception {
		Map formData = request.getParameterMap();
		if (formData.containsKey("okBtn")) {
			LdapConfig config = getLdapModule().getLdapConfig();
			config.setSchedule(ScheduleHelper.getSchedule(request));
			config.setEnabled(PortletRequestUtils.getBooleanParameter(request,  "enabled", false));	
			config.setSessionSync(PortletRequestUtils.getBooleanParameter(request, "sessionSync", false));
			config.setSessionRegister(PortletRequestUtils.getBooleanParameter(request, "sessionRegister", false));
			config.setUserDisable(PortletRequestUtils.getBooleanParameter(request, "userDisable", false));
			config.setGroupDisable(PortletRequestUtils.getBooleanParameter(request, "groupDisable", false));
			config.setUserRegister(PortletRequestUtils.getBooleanParameter(request, "userRegister", false));
			config.setGroupRegister(PortletRequestUtils.getBooleanParameter(request, "groupRegister", false));
			config.setUserSync(PortletRequestUtils.getBooleanParameter(request, "userSync", false));
			config.setMembershipSync(PortletRequestUtils.getBooleanParameter(request, "membershipSync", false));
			getLdapModule().setLdapConfig(config);
			response.setRenderParameters(formData);
		} else if (formData.containsKey("cancelBtn")) {
			response.setRenderParameter(WebKeys.ACTION, "");
			response.setWindowState(WindowState.NORMAL);
			response.setPortletMode(PortletMode.VIEW);
		} else
			response.setRenderParameters(formData);
		
	}

	public ModelAndView handleRenderRequestInternal(RenderRequest request, 
			RenderResponse response) throws Exception {
		LdapConfig config = getLdapModule().getLdapConfig();
		return new ModelAndView(WebKeys.VIEW_ADMIN_CONFIGURE_LDAP, WebKeys.LDAP_CONFIG, config);
		
	}
}
