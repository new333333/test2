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
import com.sitescape.ef.module.ldap.LdapConfig;
import com.sitescape.util.GetterUtil;
import com.sitescape.ef.web.util.ScheduleHelper;

public class ConfigureLdapController extends  SAbstractController {
	
	public void handleActionRequestInternal(ActionRequest request, ActionResponse response) throws Exception {
		Map formData = request.getParameterMap();
		if (formData.containsKey("okBtn")) {
			Map input = new HashMap();
			String val; 
			val = ActionUtil.getStringValue(formData, "sessionSync");
			input.put("sessionSync", Boolean.valueOf(GetterUtil.getBoolean(val, false)));
			val = ActionUtil.getStringValue(formData, "sessionRegister");
			input.put("sessionRegister", Boolean.valueOf(GetterUtil.getBoolean(val, false)));
			val = ActionUtil.getStringValue(formData, "userDisable");
			input.put("userDisable", Boolean.valueOf(GetterUtil.getBoolean(val, false)));
			val = ActionUtil.getStringValue(formData, "groupDisable");
			input.put("groupDisable", Boolean.valueOf(GetterUtil.getBoolean(val, false)));
			val = ActionUtil.getStringValue(formData, "userRegister");
			input.put("userRegister", Boolean.valueOf(GetterUtil.getBoolean(val, false)));
			val = ActionUtil.getStringValue(formData, "groupRegister");
			input.put("groupRegister", Boolean.valueOf(GetterUtil.getBoolean(val, false)));
			val = ActionUtil.getStringValue(formData, "userSync");
			input.put("userSync", Boolean.valueOf(GetterUtil.getBoolean(val, false)));
			val = ActionUtil.getStringValue(formData, "membershipSync");
			input.put("membershipSync", Boolean.valueOf(GetterUtil.getBoolean(val, false)));
			val = ActionUtil.getStringValue(formData, "scheduleEnabled");
			input.put("scheduleEnabled", Boolean.valueOf(GetterUtil.getBoolean(val, false)));
			input.put("schedule", ScheduleHelper.getSchedule(formData));
			getLdapModule().updateLdapConfig(input);
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
