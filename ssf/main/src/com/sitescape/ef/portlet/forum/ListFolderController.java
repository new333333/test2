package com.sitescape.ef.portlet.forum;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletURL;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.PortletSession;

import org.springframework.web.servlet.ModelAndView;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Date;

import com.sitescape.ef.ObjectKeys;
import com.sitescape.ef.module.shared.MapInputData;
import com.sitescape.ef.portletadapter.AdaptedPortletURL;
import com.sitescape.ef.web.WebKeys;
import com.sitescape.ef.web.util.PortletRequestUtils;
import com.sitescape.ef.web.util.WebHelper;
import com.sitescape.ef.web.util.DateHelper;
import com.sitescape.ef.context.request.RequestContextHolder;
import com.sitescape.ef.domain.User;

/**
 * @author Peter Hurley
 *
 */
public class ListFolderController  extends SAbstractForumController {
	public void handleActionRequestInternal(ActionRequest request, ActionResponse response) throws Exception {
        User user = RequestContextHolder.getRequestContext().getUser();
		Map formData = request.getParameterMap();
		Long binderId= PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_BINDER_ID);
		String op = PortletRequestUtils.getStringParameter(request, WebKeys.URL_OPERATION, "");
		
		if (op.equals(WebKeys.FORUM_OPERATION_SET_DISPLAY_STYLE)) {
			Map<String,Object> updates = new HashMap<String,Object>();
			updates.put(ObjectKeys.USER_PROPERTY_DISPLAY_STYLE, 
					PortletRequestUtils.getStringParameter(request,WebKeys.URL_VALUE,""));
			getProfileModule().modifyEntry(user.getParentBinder().getId(), user.getId(), new MapInputData(updates));
		
		} else if (op.equals(WebKeys.FORUM_OPERATION_SET_DISPLAY_DEFINITION)) {
			getProfileModule().setUserProperty(user.getId(), binderId, 
					ObjectKeys.USER_PROPERTY_DISPLAY_DEFINITION, 
					PortletRequestUtils.getStringParameter(request,WebKeys.URL_VALUE,""));
		
		} else if (op.equals(WebKeys.FORUM_OPERATION_SET_CALENDAR_DISPLAY_MODE)) {
			getProfileModule().setUserProperty(user.getId(), binderId, 
					ObjectKeys.USER_PROPERTY_CALENDAR_VIEWMODE, 
					PortletRequestUtils.getStringParameter(request,WebKeys.URL_VALUE,""));
		
		} else if (op.equals(WebKeys.FORUM_OPERATION_SET_CALENDAR_DISPLAY_DATE)) {
			PortletSession ps = WebHelper.getRequiredPortletSession(request);
			String urldate = PortletRequestUtils.getStringParameter(request,WebKeys.CALENDAR_URL_NEWVIEWDATE, "");
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MM_dd");
			Date newdate = sdf.parse(urldate);
			ps.setAttribute(WebKeys.CALENDAR_CURRENT_DATE, newdate);
			String viewMode = PortletRequestUtils.getStringParameter(request,WebKeys.CALENDAR_URL_VIEWMODE, "");
			getProfileModule().setUserProperty(user.getId(), binderId, 
					ObjectKeys.USER_PROPERTY_CALENDAR_VIEWMODE, viewMode);
		
		} else if (op.equals(WebKeys.FORUM_OPERATION_CALENDAR_GOTO_DATE)) {
			PortletSession ps = WebHelper.getRequiredPortletSession(request);
			Date dt = DateHelper.getDateFromInput(new MapInputData(formData), "ss_goto");
			ps.setAttribute(WebKeys.CALENDAR_CURRENT_DATE, dt);
			
		} else if (op.equals(WebKeys.FORUM_OPERATION_SELECT_FILTER)) {
			getProfileModule().setUserProperty(user.getId(), binderId, ObjectKeys.USER_PROPERTY_USER_FILTER, 
					PortletRequestUtils.getStringParameter(request, WebKeys.FORUM_OPERATION_SELECT_FILTER,""));
		}
		response.setRenderParameters(request.getParameterMap());
		
			
	}
	public ModelAndView handleRenderRequestInternal(RenderRequest request, 
			RenderResponse response) throws Exception {
        User user = RequestContextHolder.getRequestContext().getUser();
		Map formData = request.getParameterMap();
		Long binderId= PortletRequestUtils.getLongParameter(request, WebKeys.URL_BINDER_ID);				

		String op = PortletRequestUtils.getStringParameter(request, WebKeys.URL_OPERATION, "");
		if (op.equals(WebKeys.FORUM_OPERATION_RELOAD_LISTING)) {
			//An action is asking us to build the url
			PortletURL reloadUrl = response.createRenderURL();
			reloadUrl.setParameter(WebKeys.URL_BINDER_ID, binderId.toString());
			reloadUrl.setParameter(WebKeys.ACTION, WebKeys.ACTION_VIEW_FOLDER_LISTING);
			request.setAttribute("ssReloadUrl", reloadUrl.toString());			
//			return new ModelAndView(WebKeys.VIEW_LISTING);
		} else if (op.equals(WebKeys.FORUM_OPERATION_VIEW_ENTRY)) {
			String entryId = PortletRequestUtils.getStringParameter(request, WebKeys.URL_ENTRY_ID, "");
			if (!entryId.equals("")) {
				AdaptedPortletURL adapterUrl = new AdaptedPortletURL("ss_forum", true);
				adapterUrl.setParameter(WebKeys.ACTION, WebKeys.ACTION_VIEW_FOLDER_ENTRY);
				adapterUrl.setParameter(WebKeys.URL_BINDER_ID, binderId.toString());
				adapterUrl.setParameter(WebKeys.URL_ENTRY_ID, entryId);
				request.setAttribute("ssLoadEntryUrl", adapterUrl.toString());			
				request.setAttribute("ssLoadEntryId", entryId);			
			}
		}

		return returnToViewForum(request, response, formData, binderId);
	} 

}
