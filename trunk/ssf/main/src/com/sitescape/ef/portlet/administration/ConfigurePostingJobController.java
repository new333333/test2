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

import com.sitescape.ef.module.mail.PostingConfig;

public class ConfigurePostingJobController extends  SAbstractController  {
	
	public void handleActionRequestInternal(ActionRequest request, ActionResponse response) throws Exception {
		Map formData = request.getParameterMap();
		if (formData.containsKey("okBtn")) {
			PostingConfig config = new PostingConfig();
			Map newAliases = new HashMap();
			config.setSchedule(ScheduleHelper.getSchedule(request));
			config.setEnabled(PortletRequestUtils.getBooleanParameter(request,  "enabled", false));	
			int pos =0;
			while (true) {
				if (!formData.containsKey("alias" + pos))
					break;
				String alias = PortletRequestUtils.getStringParameter(request, "alias" + pos, "").trim().toLowerCase();
				Long aliasId=null;
				try {
					aliasId = PortletRequestUtils.getLongParameter(request, "aliasId" + pos);
				} catch (Exception ex) {};
				
				if (!formData.containsKey("delete" + pos)) {
					if (!Validator.isNull(alias)) {
						if (newAliases.containsKey(alias)) {
							//duplicate name - convert value to list
							Object val = newAliases.get(alias);
							List valList;
							if (val instanceof Long) {
								valList = new ArrayList();
								valList.add(val);
							} else {
								valList = (List)val;
							}
							valList.add(aliasId);
							newAliases.put(alias, valList);
						} else {
							newAliases.put(alias, aliasId);
						}
					}
				}
				++pos;
			}
		config.setAliases(newAliases);
		getAdminModule().setPostingConfig(config);
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
		List posts = getAdminModule().getPostingDefs();
		model.put(WebKeys.POSTINGS, posts);
		Toolbar toolbar = new Toolbar();
		PortletURL url = response.createRenderURL();
		url.setParameter(WebKeys.ACTION, WebKeys.POSTING_ACTION_CONFIGURE);
		toolbar.addToolbarMenu("ss_scheduleLink", NLT.get("incoming.toolbar_forums"), url);
		model.put(WebKeys.TOOLBAR, toolbar.getToolbar());
		return new ModelAndView(WebKeys.VIEW_ADMIN_CONFIGURE_POSTING_JOB, model); 
	}

}
