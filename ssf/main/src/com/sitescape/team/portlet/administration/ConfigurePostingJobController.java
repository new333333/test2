package com.sitescape.team.portlet.administration;
import java.util.HashMap;
import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletURL;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.springframework.web.servlet.ModelAndView;

import com.sitescape.team.jobs.ScheduleInfo;
import com.sitescape.team.util.NLT;
import com.sitescape.team.web.WebKeys;
import com.sitescape.team.web.portlet.SAbstractController;
import com.sitescape.team.web.util.PortletRequestUtils;
import com.sitescape.team.web.util.ScheduleHelper;
import com.sitescape.team.web.util.Toolbar;
import com.sitescape.util.StringUtil;
import com.sitescape.util.Validator;

public class ConfigurePostingJobController extends  SAbstractController  {
	
	public void handleActionRequestAfterValidation(ActionRequest request, ActionResponse response) throws Exception {
		Map formData = request.getParameterMap();
		if (formData.containsKey("okBtn")) {
			ScheduleInfo config = getAdminModule().getPostingSchedule();
			config.setSchedule(ScheduleHelper.getSchedule(request));
			config.setEnabled(PortletRequestUtils.getBooleanParameter(request, "enabled", false));
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
						updates.put("emailAddress", alias);						
					}
				} else if (!Validator.isNull(aliasId)) getAdminModule().deletePosting(aliasId);
				++pos;
				updates.clear();
			}
			updates.clear();
			String[] emailAddress = StringUtil.split(PortletRequestUtils.getStringParameter(request, "addresses", ""));
			for (int i=0; i<emailAddress.length; ++i) {
				String addr = emailAddress[i].trim();
				updates.put("emailAddress", addr);
				getAdminModule().addPosting(updates);
				
			}
		response.setRenderParameters(formData);
	} else if (formData.containsKey("closeBtn") || (formData.containsKey("cancelBtn"))) {
		response.setRenderParameter("redirect", "true");
	} else
		response.setRenderParameters(formData);
		
	}

	public ModelAndView handleRenderRequestInternal(RenderRequest request, 
			RenderResponse response) throws Exception {
		if (!Validator.isNull(request.getParameter("redirect"))) {
			return new ModelAndView(WebKeys.VIEW_ADMIN_REDIRECT);
		}
		HashMap model = new HashMap();
		ScheduleInfo config = getAdminModule().getPostingSchedule();
		model.put(WebKeys.SCHEDULE_INFO, config);	
		model.put(WebKeys.POSTINGS, getAdminModule().getPostings());
		return new ModelAndView(WebKeys.VIEW_ADMIN_CONFIGURE_POSTING_JOB, model);
	}

}
