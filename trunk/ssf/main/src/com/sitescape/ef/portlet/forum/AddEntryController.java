package com.sitescape.ef.portlet.forum;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletURL;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.Map;

import com.sitescape.ef.portletadapter.MultipartFileSupport;
import com.sitescape.ef.web.WebKeys;
import com.sitescape.ef.web.util.PortletRequestUtils;
import com.sitescape.ef.domain.NoDefinitionByTheIdException;

/**
 * @author Peter Hurley
 *
 */
public class AddEntryController extends SAbstractForumController {
	public void handleActionRequestInternal(ActionRequest request, ActionResponse response) 
	throws Exception {
		Map formData = request.getParameterMap();
		Long folderId = ActionUtil.getForumId(request);
		String entryId="";
		String action = PortletRequestUtils.getStringParameter(request, WebKeys.ACTION, "");
		//See if the add entry form was submitted
		if (formData.containsKey("okBtn")) {
			//The form was submitted. Go process it
			// Returns a map where key is form field name (String) and value is LiferayFileItem.
			String entryType = PortletRequestUtils.getStringParameter(request, WebKeys.FORUM_URL_ENTRY_TYPE, "");
			Map fileMap=null;
			if (request instanceof MultipartFileSupport) {
				fileMap = ((MultipartFileSupport) request).getFileMap();
			} else {
				fileMap = new HashMap();
			}
			if (action.equals(WebKeys.FORUM_ACTION_ADD_ENTRY)) {
				Long id = getFolderModule().addEntry(folderId, entryType, formData, fileMap);
				entryId = id.toString();
			} else if (action.equals(WebKeys.FORUM_ACTION_ADD_REPLY)) {
				Long id = new Long(PortletRequestUtils.getRequiredLongParameter(request, WebKeys.FORUM_URL_ENTRY_ID));				
				getFolderModule().addReply(folderId, id, entryType, formData, fileMap );
				entryId = id.toString();
			}
			//response.setRenderParameter(WebKeys.ACTION, WebKeys.FORUM_ACTION_VIEW_FORUM);
			//response.setRenderParameter(WebKeys.FORUM_URL_FORUM_ID, folderId.toString());
		} else if (formData.containsKey("cancelBtn")) {
			//response.setRenderParameter(WebKeys.ACTION, WebKeys.FORUM_ACTION_VIEW_FORUM);
			//response.setRenderParameter(WebKeys.FORUM_URL_FORUM_ID, folderId.toString());
			if (action.equals(WebKeys.FORUM_ACTION_ADD_ENTRY)) {
			} else if (action.equals(WebKeys.FORUM_ACTION_ADD_REPLY)) {
				entryId = PortletRequestUtils.getStringParameter(request, WebKeys.FORUM_URL_ENTRY_ID, "");				
			}
		}
		
		response.setRenderParameters(formData);
		response.setRenderParameter(WebKeys.ENTRY_ID, entryId);
			
	}

	public ModelAndView handleRenderRequestInternal(RenderRequest request, 
			RenderResponse response) throws Exception {
		
		Map model;
		Map formData1 = request.getParameterMap();
		Map formData = new HashMap((Map)formData1);
		Long folderId = ActionUtil.getForumId(request);
			
		String action = PortletRequestUtils.getStringParameter(request, WebKeys.ACTION, "");
		String path = WebKeys.VIEW_ADD_ENTRY;
		
		//See if the add entry form was submitted
		if (formData.containsKey("okBtn") || formData.containsKey("cancelBtn")) {
			String entryId = request.getParameter(WebKeys.ENTRY_ID);
			formData.put(WebKeys.FORUM_URL_ENTRY_ID, entryId);
			try {
				if (entryId.equals("")) {
					return returnToViewForum(request, response, formData, folderId);
				} else {
					model = getForumActionModule().getShowEntry(entryId, formData, request, response, folderId);
				}
			} catch (NoDefinitionByTheIdException nd) {
				return returnToViewForum(request, response, formData, folderId);
			}
			path = WebKeys.VIEW_FORUM;
			model.put(WebKeys.FORUM_URL_OPERATION, WebKeys.FORUM_OPERATION_VIEW_ENTRY);
			request.setAttribute(WebKeys.ACTION, WebKeys.FORUM_ACTION_VIEW_ENTRY);
			PortletURL reloadUrl = response.createRenderURL();
			reloadUrl.setParameter(WebKeys.FORUM_URL_FORUM_ID, folderId.toString());
			reloadUrl.setParameter(WebKeys.FORUM_URL_ENTRY_ID, entryId);
			reloadUrl.setParameter(WebKeys.FORUM_URL_OPERATION, WebKeys.FORUM_OPERATION_VIEW_ENTRY);
			reloadUrl.setParameter(WebKeys.ACTION, WebKeys.FORUM_ACTION_VIEW_ENTRY);
			request.setAttribute("ssReloadUrl", reloadUrl.toString());
		} else {
			//See if this is an "add entry" or an "add reply" request
			try {
				if (action.equals(WebKeys.FORUM_ACTION_ADD_ENTRY)) {
					model = getForumActionModule().getAddEntry(formData, request, folderId);
				} else {
					model = getForumActionModule().getAddReply(formData, request, folderId);
				}
			} catch (NoDefinitionByTheIdException nd) {
				//Get the jsp objects again, but this time get the "view_forum" values
				return returnToViewForum(request, response, formData, folderId);
			}
		}

		return new ModelAndView(path, model);
	}
}


