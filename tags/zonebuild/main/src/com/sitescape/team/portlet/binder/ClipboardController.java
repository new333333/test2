package com.sitescape.team.portlet.binder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.springframework.web.servlet.ModelAndView;

import com.sitescape.team.security.AccessControlException;
import com.sitescape.team.util.LongIdUtil;
import com.sitescape.team.web.WebKeys;
import com.sitescape.team.web.portlet.SAbstractControllerRetry;
import com.sitescape.team.web.util.Clipboard;
import com.sitescape.team.web.util.PortletRequestUtils;
import com.sitescape.team.web.util.WebHelper;
/**
 * Controller to handle ajax requests for the clipboard
 * @author Janet
 *
 */
public class ClipboardController extends SAbstractControllerRetry {
	//caller will retry on OptimisiticLockExceptions
	public void handleActionRequestWithRetry(ActionRequest request, ActionResponse response) throws Exception {
		response.setRenderParameters(request.getParameterMap());
		if (WebHelper.isUserLoggedIn(request)) {
			String op = PortletRequestUtils.getStringParameter(request, WebKeys.URL_OPERATION, "");
			if (op.equals(WebKeys.OPERATION_ADD_TO_CLIPBOARD)) {
				ajaxAddToClipboard(request, response);
			} else if (op.equals(WebKeys.OPERATION_CLEAR_CLIPBOARD)) {
				ajaxClearClipboard(request, response);
			} else if (op.equals(WebKeys.OPERATION_REMOVE_FROM_CLIPBOARD)) {
				ajaxRemoveFromClipboard(request, response);
			}
		}
	}
	
	public ModelAndView handleRenderRequestInternal(RenderRequest request, 
			RenderResponse response) throws Exception {
		String op = PortletRequestUtils.getStringParameter(request, WebKeys.URL_OPERATION, "");
		response.setContentType("text/json");

		if (!WebHelper.isUserLoggedIn(request)) {
			Map model = new HashMap();
			model.put(WebKeys.AJAX_ERROR_MESSAGE, "general.notLoggedIn");	
			return new ModelAndView("common/json_ajax_return", model);
		}
		
		//The user is logged in
		if (op.equals(WebKeys.OPERATION_GET_CLIPBOARD_USERS)) {
			return ajaxGetClipboardUsers(request, response);
		} else {
			return new ModelAndView("common/json_ajax_return");			
		}
	}
	private void ajaxAddToClipboard(ActionRequest request, 
			ActionResponse response) throws Exception {
		String musterClass = PortletRequestUtils.getStringParameter(request, WebKeys.URL_MUSTER_CLASS, "");
		List musterIds = PortletRequestUtils.getLongListParameters(request, WebKeys.URL_MUSTER_IDS);
		
		Clipboard clipboard = new Clipboard(request);
		clipboard.add(musterClass, musterIds);
				
		Boolean addTeamMembers = PortletRequestUtils.getBooleanParameter(request, "add_team_members", false);
		if (addTeamMembers) {
			Long binderId = PortletRequestUtils.getLongParameter(request, WebKeys.URL_BINDER_ID);
			try {
				Collection teamMemberIds = getBinderModule().getTeamMemberIds(binderId, true);
				clipboard.add(Clipboard.USERS, teamMemberIds);				
			} catch (AccessControlException ac) {} //no access, just skip
		}
	}

	private void ajaxRemoveFromClipboard(ActionRequest request, 
			ActionResponse response) throws Exception {
		String musterClass = PortletRequestUtils.getStringParameter(request, WebKeys.URL_MUSTER_CLASS, "");
		String[] musterIds = new String[0];
		if (PortletRequestUtils.getStringParameters(request, WebKeys.URL_MUSTER_IDS) != null) {
			musterIds = PortletRequestUtils.getStringParameters(request, WebKeys.URL_MUSTER_IDS);
		}
		Clipboard clipboard = new Clipboard(request);		
		clipboard.remove(musterClass, LongIdUtil.getIdsAsLongSet(musterIds));
	}
	
	private void ajaxClearClipboard(ActionRequest request, 
			ActionResponse response) throws Exception {
		Clipboard clipboard = new Clipboard(request);
		String musterClass = PortletRequestUtils.getStringParameter(request, WebKeys.URL_MUSTER_CLASS, "");
		String[] musterClasses = musterClass.split(" ");
		for (int i = 0; i < musterClasses.length; i++) {
			if (!musterClasses[i].equals("")) {
				clipboard.clear(musterClasses[i]);
			}
		}
	}
	
	private ModelAndView ajaxGetClipboardUsers(RenderRequest request, 
			RenderResponse response) throws Exception {
		Map model = new HashMap();
		
		Clipboard clipboard = new Clipboard(request);
		Set clipboardUsers = clipboard.get(Clipboard.USERS);
		model.put(WebKeys.CLIPBOARD_PRINCIPALS , getProfileModule().getUsersFromPrincipals(
					clipboardUsers));
		
		return new ModelAndView("forum/clipboard_users", model);
	}


}
