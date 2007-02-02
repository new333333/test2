package com.sitescape.team.portlet.administration;
import java.util.HashMap;
import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.springframework.web.servlet.ModelAndView;

import com.sitescape.team.module.ldap.LdapConfig;
import com.sitescape.team.web.WebKeys;
import com.sitescape.team.web.portlet.SAbstractController;
import com.sitescape.team.web.util.PortletRequestUtils;
import com.sitescape.team.web.util.ScheduleHelper;
import com.sitescape.util.StringUtil;
import com.sitescape.util.Validator;

public class ConfigureLdapController extends  SAbstractController {
	
	public void handleActionRequestAfterValidation(ActionRequest request, ActionResponse response) throws Exception {
		Map formData = request.getParameterMap();
		if (formData.containsKey("okBtn")) {
			LdapConfig config = getLdapModule().getLdapConfig();
			config.setSchedule(ScheduleHelper.getSchedule(request));
			config.setEnabled(PortletRequestUtils.getBooleanParameter(request,  "enabled", false));	
			config.setUserDisable(PortletRequestUtils.getBooleanParameter(request, "userDisable", false));
			config.setGroupDisable(PortletRequestUtils.getBooleanParameter(request, "groupDisable", false));
			config.setUserRegister(PortletRequestUtils.getBooleanParameter(request, "userRegister", false));
			config.setGroupRegister(PortletRequestUtils.getBooleanParameter(request, "groupRegister", false));
			config.setUserSync(PortletRequestUtils.getBooleanParameter(request, "userSync", false));
			config.setGroupSync(PortletRequestUtils.getBooleanParameter(request, "groupSync", false));
			config.setMembershipSync(PortletRequestUtils.getBooleanParameter(request, "membershipSync", false));
			config.setUserUrl(PortletRequestUtils.getStringParameter(request, "userUrl", ""));
			config.setUserPrincipal(PortletRequestUtils.getStringParameter(request, "userPrincipal", ""));
			config.setUserCredential(PortletRequestUtils.getStringParameter(request, "userCredential", ""));
			config.setUserIdMapping(PortletRequestUtils.getStringParameter(request, "userIdMapping", ""));
			String[] mappings = StringUtil.split(PortletRequestUtils.getStringParameter(request, "userMappings", ""), "\n");
			Map maps = new HashMap();
			for (int i=0; i<mappings.length; ++i) {
				String m = mappings[i];
				if (Validator.isNull(m)) continue;
				String[] vals = StringUtil.split(m, "=");
				if (vals.length != 2) continue;
				maps.put(vals[1].trim(), vals[0].trim());
			}
			config.setUserMappings(maps);
			getLdapModule().setLdapConfig(config);
			response.setRenderParameters(formData);
		} else if (formData.containsKey("cancelBtn") || formData.containsKey("closeBtn")) {
			response.setRenderParameter("redirect", "true");
		} else
			response.setRenderParameters(formData);
		
	}

	public ModelAndView handleRenderRequestInternal(RenderRequest request, 
			RenderResponse response) throws Exception {

		if (!Validator.isNull(request.getParameter("redirect"))) {
			return new ModelAndView(WebKeys.VIEW_ADMIN_REDIRECT);
		}

		LdapConfig config = getLdapModule().getLdapConfig();
		return new ModelAndView(WebKeys.VIEW_ADMIN_CONFIGURE_LDAP, WebKeys.LDAP_CONFIG, config);
		
	}
}
