package com.sitescape.ef.portlet.administration;
import java.util.Map;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
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

import com.sitescape.ef.module.admin.PostingConfig;

public class ConfigurePostingJobController extends  SAbstractController  {
	
	public void handleActionRequestInternal(ActionRequest request, ActionResponse response) throws Exception {
		Map formData = request.getParameterMap();
		if (formData.containsKey("okBtn")) {
			String op = PortletRequestUtils.getStringParameter(request, WebKeys.FORUM_URL_OPERATION, "");
			if (op.equals(WebKeys.OPERATION_POSTING_ALIASES)) {
			} else if (op.equals(WebKeys.OPERATION_ADD_POSTING_ALIASES)) {
			} else if (op.equals(WebKeys.OPERATION_MODIFY_POSTING_ALIASES)) {
			} else {
				PostingConfig config = getAdminModule().getPostingConfig();
				config.setSchedule(ScheduleHelper.getSchedule(request));
				config.setEnabled(PortletRequestUtils.getBooleanParameter(request,  "enabled", false));	
				getAdminModule().setPostingConfig(config);
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
		PostingConfig config = getAdminModule().getPostingConfig();
		model.put(WebKeys.POSTING_CONFIG, config);	
		String op = PortletRequestUtils.getStringParameter(request, WebKeys.FORUM_URL_OPERATION, "");
		if (op.equals(WebKeys.OPERATION_POSTING_ALIASES)) {
//until I get this working
	List aliases = new java.util.ArrayList();
	aliases.add("dummy1@sitescape.com");
	aliases.add("dummy2@sitescape.com");
	config.setAliases(aliases);
			List posts = getAdminModule().getPostingDefs();
			model.put(WebKeys.POSTINGS, posts);
			Toolbar toolbar = new Toolbar();
			PortletURL url = response.createRenderURL();
			url.setParameter(WebKeys.ACTION, WebKeys.POSTINGJOB_ACTION_CONFIGURE);

			toolbar.addToolbarMenu("ss_scheduleLink", NLT.get("toolbar.posting_schedule"), url);
			model.put(WebKeys.TOOLBAR, toolbar.getToolbar());
			return new ModelAndView(WebKeys.VIEW_ADMIN_CONFIGURE_ALIASES, model); 
		} else {
			Toolbar toolbar = new Toolbar();
			PortletURL url = response.createRenderURL();
			url.setParameter(WebKeys.ACTION, WebKeys.POSTINGJOB_ACTION_CONFIGURE);
			url.setParameter(WebKeys.FORUM_URL_OPERATION, WebKeys.OPERATION_POSTING_ALIASES);

			toolbar.addToolbarMenu("ss_aliasLink", NLT.get("toolbar.posting_alias"), url);
			model.put(WebKeys.TOOLBAR, toolbar.getToolbar());
			return new ModelAndView(WebKeys.VIEW_ADMIN_CONFIGURE_POSTING_JOB, model); 
			
		} 
	}

}
