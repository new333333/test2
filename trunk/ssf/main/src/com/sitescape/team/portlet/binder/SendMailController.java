package com.sitescape.team.portlet.binder;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.springframework.web.servlet.ModelAndView;

import com.sitescape.team.ObjectKeys;
import com.sitescape.team.context.request.RequestContextHolder;
import com.sitescape.team.domain.Binder;
import com.sitescape.team.domain.Description;
import com.sitescape.team.util.NLT;
import com.sitescape.team.web.WebKeys;
import com.sitescape.team.web.portlet.SAbstractController;
import com.sitescape.team.web.util.FindIdsHelper;
import com.sitescape.team.web.util.PortletRequestUtils;
import com.sitescape.util.StringUtil;

/**
 * @author Janet McCann
 *
 */
public class SendMailController extends SAbstractController {
	public void handleActionRequestAfterValidation(ActionRequest request, ActionResponse response) 
	throws Exception {
		Map formData = request.getParameterMap();
		
		//See if the form was submitted
		if (formData.containsKey("okBtn")) {
			Long binderId = PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_BINDER_ID);
			String subject = PortletRequestUtils.getStringParameter(request, "subject", "");	
			String[] to = StringUtil.split(PortletRequestUtils.getStringParameter(request, "addresses", ""));
			Set emailAddress = new HashSet();
			for (int i=0; i<to.length; ++i) {
				emailAddress.add(to[i]);				
			}
			boolean self = PortletRequestUtils.getBooleanParameter(request, "self", false);
			String body = PortletRequestUtils.getStringParameter(request, "mailBody", "");
			Set memberIds = new HashSet();
			if (self) memberIds.add(RequestContextHolder.getRequestContext().getUserId());
			if (formData.containsKey("users")) memberIds.addAll(FindIdsHelper.getIdsAsLongSet(request.getParameterValues("users")));
			if (formData.containsKey("groups")) memberIds.addAll(FindIdsHelper.getIdsAsLongSet(request.getParameterValues("groups")));
			
			Map status = getAdminModule().sendMail(memberIds, emailAddress, subject, new Description(body, Description.FORMAT_HTML), null);
			int i = 0;
			String result = (String)status.get(ObjectKeys.SENDMAIL_STATUS);
			List errors = (List)status.get(ObjectKeys.SENDMAIL_ERRORS);
			List addrs = (List)status.get(ObjectKeys.SENDMAIL_DISTRIBUTION);
			if (ObjectKeys.SENDMAIL_STATUS_SENT.equals(result)) {
				errors.add(0, NLT.get("sendMail.mailSent"));
				response.setRenderParameter(WebKeys.EMAIL_ADDRESSES, (String[])addrs.toArray( new String[0]));
			} else if (ObjectKeys.SENDMAIL_STATUS_SCHEDULED.equals(result)) {
				errors.add(0, NLT.get("sendMail.mailQueued"));
				response.setRenderParameter(WebKeys.EMAIL_ADDRESSES, (String[])addrs.toArray( new String[0]));
			} else {
				errors.add(0, NLT.get("sendMail.mailFailed"));
			}
			response.setRenderParameter(WebKeys.ERROR_LIST, (String[])errors.toArray( new String[0]));
		} else if (formData.containsKey("closeBtn") || formData.containsKey("cancelBtn")) {
			response.setRenderParameter(WebKeys.ACTION, WebKeys.ACTION_CLOSE_WINDOW);			
		} else {
			response.setRenderParameters(formData);
		}
			
	}
	public ModelAndView handleRenderRequestInternal(RenderRequest request, 
			RenderResponse response) throws Exception {
		String [] errors = request.getParameterValues(WebKeys.ERROR_LIST);
		Map model = new HashMap();
		if (errors != null) {
			model.put(WebKeys.ERROR_LIST, errors);
			model.put(WebKeys.EMAIL_ADDRESSES, request.getParameterValues(WebKeys.EMAIL_ADDRESSES));
			return new ModelAndView(WebKeys.VIEW_BINDER_SENDMAIL, model);
		}
		Long binderId = PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_BINDER_ID);
		Binder binder = getBinderModule().getBinder(binderId);
		model.put(WebKeys.BINDER, binder);
		
		List userIds = PortletRequestUtils.getLongListParameters(request, WebKeys.USER_IDS_TO_ADD);
		model.put(WebKeys.USERS, getProfileModule().getUsers(new HashSet(userIds)));
		
		return new ModelAndView(WebKeys.VIEW_BINDER_SENDMAIL, model);
	}

}
