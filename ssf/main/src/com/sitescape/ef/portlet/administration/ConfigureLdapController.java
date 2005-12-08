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
import com.sitescape.ef.jobs.Schedule;

public class ConfigureLdapController extends  SAbstractController {
	
	public void handleActionRequestInternal(ActionRequest request, ActionResponse response) throws Exception {
		Map formData = request.getParameterMap();
		if (formData.containsKey("okBtn")) {
			Map input = new HashMap();
			String val;
			val = ActionUtil.getStringValue(formData, LdapConfig.SESSION_SYNC);
			input.put(LdapConfig.SESSION_SYNC, Boolean.toString(GetterUtil.getBoolean(val, false)));
			val = ActionUtil.getStringValue(formData, LdapConfig.SESSION_REGISTER_USERS);
			input.put(LdapConfig.SESSION_REGISTER_USERS, Boolean.toString(GetterUtil.getBoolean(val, false)));
			val = ActionUtil.getStringValue(formData, LdapConfig.DISABLE_USERS);
			input.put(LdapConfig.DISABLE_USERS, Boolean.toString(GetterUtil.getBoolean(val, false)));
			val = ActionUtil.getStringValue(formData, LdapConfig.DISABLE_GROUPS);
			input.put(LdapConfig.DISABLE_GROUPS, Boolean.toString(GetterUtil.getBoolean(val, false)));
			val = ActionUtil.getStringValue(formData, LdapConfig.REGISTER_USERS);
			input.put(LdapConfig.REGISTER_USERS, Boolean.toString(GetterUtil.getBoolean(val, false)));
			val = ActionUtil.getStringValue(formData, LdapConfig.REGISTER_GROUPS);
			input.put(LdapConfig.REGISTER_GROUPS, Boolean.toString(GetterUtil.getBoolean(val, false)));
			val = ActionUtil.getStringValue(formData, LdapConfig.SYNC_USERS);
			input.put(LdapConfig.SYNC_USERS, Boolean.toString(GetterUtil.getBoolean(val, false)));
			val = ActionUtil.getStringValue(formData, LdapConfig.SYNC_MEMBERSHIP);
			input.put(LdapConfig.SYNC_MEMBERSHIP, Boolean.toString(GetterUtil.getBoolean(val, false)));
			val = ActionUtil.getStringValue(formData, LdapConfig.ENABLE_SCHEDULE);
			input.put(LdapConfig.ENABLE_SCHEDULE, Boolean.toString(GetterUtil.getBoolean(val, false)));
			Schedule schedule = new Schedule();
			val = ActionUtil.getStringValue(formData, "schedTime");			
			if (!val.equals("")) {
				schedule.setHoursMinutes(val);
				val = ActionUtil.getStringValue(formData, "schedType");
				if (val.equals("daily")) {
					schedule.setDaily(true);
				} else {
					schedule.setDaily(false);
					if (formData.containsKey("onday_sun")) schedule.setOnSunday(true);
					if (formData.containsKey("onday_mon")) schedule.setOnMonday(true);
					if (formData.containsKey("onday_tue")) schedule.setOnTuesday(true);
					if (formData.containsKey("onday_wed")) schedule.setOnWednesday(true);
					if (formData.containsKey("onday_thu")) schedule.setOnThursday(true);
					if (formData.containsKey("onday_fri")) schedule.setOnFriday(true);
					if (formData.containsKey("onday_sat")) schedule.setOnSaturday(true);
				}
				
			}
			input.put(LdapConfig.SCHEDULE, schedule);
			getLdapModule().updateLdapConfig(input);
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
		LdapConfig config = getLdapModule().getLdapConfig();
		return new ModelAndView("administration/configureLdap", "ssLdapConfig", config);
		
	}
}
