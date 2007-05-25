/**
 * The contents of this file are governed by the terms of your license
 * with SiteScape, Inc., which includes disclaimers of warranties and
 * limitations on liability. You may not use this file except in accordance
 * with the terms of that license. See the license for the specific language
 * governing your rights and limitations under the license.
 *
 * Copyright (c) 2007 SiteScape, Inc.
 *
 */
package com.sitescape.team.portlet.administration;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.dom4j.Document;
import org.springframework.web.servlet.ModelAndView;

import com.sitescape.team.context.request.RequestContextHolder;
import com.sitescape.team.domain.Binder;
import com.sitescape.team.domain.Folder;
import com.sitescape.team.domain.Workspace;
import com.sitescape.team.module.shared.MapInputData;
import com.sitescape.team.web.WebKeys;
import com.sitescape.team.portlet.binder.AbstractBinderController;
import com.sitescape.team.web.portlet.SAbstractController;
import com.sitescape.team.web.tree.SearchTreeHelper;
import com.sitescape.team.web.tree.WsDomTreeBuilder;
import com.sitescape.team.web.util.BinderHelper;
import com.sitescape.team.web.util.DateHelper;
import com.sitescape.team.web.util.PortletRequestUtils;

public abstract class AbstractReportController extends  AbstractBinderController {
	
	public void handleActionRequestAfterValidation(ActionRequest request, ActionResponse response) throws Exception {
		Map formData = request.getParameterMap();
		response.setRenderParameters(formData);
		Long binderId = PortletRequestUtils.getLongParameter(request, WebKeys.URL_BINDER_ID, RequestContextHolder.getRequestContext().getZoneId());
		if (formData.containsKey("closeBtn") || formData.containsKey("cancelBtn")) {
			String binderType = PortletRequestUtils.getRequiredStringParameter(request, WebKeys.URL_BINDER_TYPE);	
			setupViewBinder(response, binderId, binderType);
		}
	}
	
	public ModelAndView handleRenderRequestInternal(RenderRequest request, 
			RenderResponse response) throws Exception {

		Map formData = request.getParameterMap();

		if (formData.containsKey("closeBtn") || formData.containsKey("cancelBtn")) {
			return new ModelAndView(WebKeys.VIEW_ADMIN_REDIRECT);
		}
		
		Map model = new HashMap();
		populateModel(request, model);
		return new ModelAndView(chooseView(formData), model);
	}
	
	protected void populateModel(RenderRequest request, Map model)
	{
		Date startDate = null;
		Date endDate = null;
		try {
			MapInputData data = new MapInputData(request.getParameterMap());
			startDate = DateHelper.getDateFromInput(data, WebKeys.URL_START_DATE);
			endDate = DateHelper.getDateFromInput(data, WebKeys.URL_END_DATE);
		} catch(Exception e) {
		}
		if(startDate == null) { startDate = new Date(); }
		if(endDate == null) { endDate = new Date(); }
		model.put(WebKeys.REPORT_START_DATE, startDate);
		model.put(WebKeys.REPORT_END_DATE, endDate);
	}
	
	abstract protected String chooseView(Map formData);
}
