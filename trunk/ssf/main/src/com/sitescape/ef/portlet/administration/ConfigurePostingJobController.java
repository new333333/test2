package com.sitescape.ef.portlet.administration;
import java.util.Map;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletURL;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.WindowState;
import javax.portlet.PortletMode;

import org.springframework.web.servlet.ModelAndView;

import com.sitescape.ef.web.portlet.SAbstractController;
import com.sitescape.ef.web.WebKeys;
import com.sitescape.ef.util.NLT;
import com.sitescape.ef.web.util.PortletRequestUtils;
import com.sitescape.ef.web.util.ScheduleHelper;
import com.sitescape.ef.web.util.Toolbar;
import com.sitescape.util.Validator;

import com.sitescape.ef.jobs.ScheduleInfo;

public class ConfigurePostingJobController extends  SAbstractController  {
	
	public void handleActionRequestInternal(ActionRequest request, ActionResponse response) throws Exception {
		Map formData = request.getParameterMap();
		if (formData.containsKey("okBtn")) {
			ScheduleInfo config = getAdminModule().getPostingSchedule();
			config.setSchedule(ScheduleHelper.getSchedule(request));
			config.setEnabled(PortletRequestUtils.getBooleanParameter(request,  "enabled", false));
			getAdminModule().setPostingSchedule(config);
			
			int pos =0;
			Map updates = new HashMap();
			while (true) {
				if (!formData.containsKey("alias" + pos))
					break;
				String alias = PortletRequestUtils.getStringParameter(request, "alias" + pos, "").trim().toLowerCase();
				String aliasId=null;
				try {
					aliasId = PortletRequestUtils.getStringParameter(request, "aliasId" + pos);
				} catch (Exception ex) {};
				
				if (!formData.containsKey("delete" + pos)) {
					if (!Validator.isNull(alias)) {
						updates.put("aliasName", alias);
						if (!Validator.isNull(aliasId)) {
							getAdminModule().modifyEmailAlias(aliasId, updates);
						} else {
							getAdminModule().addEmailAlias(updates);
						}
						
					}
				} else if (!Validator.isNull(aliasId)) getAdminModule().deleteEmailAlias(aliasId);
				++pos;
				updates.clear();
			}
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
		HashMap model = new HashMap();
		ScheduleInfo config = getAdminModule().getPostingSchedule();
		model.put(WebKeys.SCHEDULE_INFO, config);	
		model.put(WebKeys.EMAIL_ALIASES, getAdminModule().getEmailAliases());
		Toolbar toolbar = new Toolbar();
		PortletURL url = response.createRenderURL();
		url.setParameter(WebKeys.ACTION, WebKeys.POSTING_ACTION_CONFIGURE);
		toolbar.addToolbarMenu("ss_scheduleLink", NLT.get("incoming.toolbar_forums"), url);
		model.put(WebKeys.TOOLBAR, toolbar.getToolbar());
		return new ModelAndView(WebKeys.VIEW_ADMIN_CONFIGURE_POSTING_JOB, model); 
	}

}
