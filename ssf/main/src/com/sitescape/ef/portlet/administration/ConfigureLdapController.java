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
			Map input = new HashMap();
			String val; 
			input.put("sessionSync", Boolean.valueOf(PortletRequestUtils.getBooleanParameter(request, "sessionSync", false)));
			input.put("sessionRegister", Boolean.valueOf(PortletRequestUtils.getBooleanParameter(request, "sessionRegister", false)));
			input.put("userDisable", Boolean.valueOf(PortletRequestUtils.getBooleanParameter(request, "userDisable", false)));
			input.put("groupDisable", Boolean.valueOf(PortletRequestUtils.getBooleanParameter(request, "groupDisable", false)));
			input.put("userRegister", Boolean.valueOf(PortletRequestUtils.getBooleanParameter(request, "userRegister", false)));
			input.put("groupRegister", Boolean.valueOf(PortletRequestUtils.getBooleanParameter(request, "groupRegister", false)));
			input.put("userSync", Boolean.valueOf(PortletRequestUtils.getBooleanParameter(request, "userSync", false)));
			input.put("membershipSync", Boolean.valueOf(PortletRequestUtils.getBooleanParameter(request, "membershipSync", false)));
			input.put("scheduleEnabled", Boolean.valueOf(PortletRequestUtils.getBooleanParameter(request, "scheduleEnabled", false)));
			input.put("schedule", ScheduleHelper.getSchedule(request));
			getLdapModule().modifyLdapConfig(input);
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
