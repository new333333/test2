package com.sitescape.team.portlet.binder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletURL;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.springframework.web.servlet.ModelAndView;

import com.sitescape.team.domain.User;
import com.sitescape.team.ObjectKeys;
import com.sitescape.team.context.request.RequestContextHolder;
import com.sitescape.team.domain.Binder;
import com.sitescape.team.domain.Description;
import com.sitescape.team.domain.Entry;
import com.sitescape.team.domain.Principal;
import com.sitescape.team.portletadapter.AdaptedPortletURL;
import com.sitescape.team.security.AccessControlException;
import com.sitescape.team.util.NLT;
import com.sitescape.team.web.WebKeys;
import com.sitescape.team.web.portlet.SAbstractController;
import com.sitescape.team.web.util.Clipboard;
import com.sitescape.team.web.util.FindIdsHelper;
import com.sitescape.team.web.util.PortletRequestUtils;
import com.sitescape.util.StringUtil;

/**
 * @author Janet McCann
 * 
 */
public class MeetingController  extends SAbstractController {

	public void handleActionRequestAfterValidation(ActionRequest request,
			ActionResponse response) throws Exception {
		response.setRenderParameters(request.getParameterMap());
	}

	public ModelAndView handleRenderRequestInternal(RenderRequest request,
			RenderResponse response) throws Exception {
		String[] errors = request.getParameterValues(WebKeys.ERROR_LIST);
		Map model = new HashMap();
		if (errors != null) {
			model.put(WebKeys.ERROR_LIST, errors);
			return new ModelAndView(WebKeys.VIEW_BINDER_MEETING, model);
		}
		Long binderId = PortletRequestUtils.getRequiredLongParameter(request,
				WebKeys.URL_BINDER_ID);
		Long entryId = PortletRequestUtils.getLongParameter(request,
				WebKeys.URL_ENTRY_ID);
		List userIds = PortletRequestUtils.getLongListParameters(request, WebKeys.USER_IDS_TO_ADD);


		Binder binder = getBinderModule().getBinder(binderId);		
		model.put(WebKeys.BINDER, binder);
		
		
		if (entryId != null) {
			Entry entry = getFolderModule().getEntry(binderId, entryId);
			model.put(WebKeys.ENTRY, entry);
		}
		
		model.put(WebKeys.USERS, getProfileModule().getUsers(new HashSet(userIds)));
		
		model.put(WebKeys.ACTION, PortletRequestUtils.getStringParameter(request,
				WebKeys.ACTION));
		
		return new ModelAndView(WebKeys.VIEW_BINDER_MEETING, model);
	}

}
