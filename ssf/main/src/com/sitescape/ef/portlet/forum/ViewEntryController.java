package com.sitescape.ef.portlet.forum;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletSession;
import javax.portlet.PortletURL;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.dom4j.Document;
import org.dom4j.Element;
import org.springframework.web.servlet.ModelAndView;

import com.sitescape.ef.ObjectKeys;
import com.sitescape.ef.context.request.RequestContextHolder;
import com.sitescape.ef.domain.Definition;
import com.sitescape.ef.domain.Entry;
import com.sitescape.ef.domain.Folder;
import com.sitescape.ef.domain.FolderEntry;
import com.sitescape.ef.domain.HistoryMap;
import com.sitescape.ef.domain.NoDefinitionByTheIdException;
import com.sitescape.ef.domain.NoFolderByTheIdException;
import com.sitescape.ef.domain.SeenMap;
import com.sitescape.ef.domain.User;
import com.sitescape.ef.module.folder.FolderModule;
import com.sitescape.ef.util.NLT;
import com.sitescape.ef.web.WebKeys;
import com.sitescape.ef.web.util.DefinitionUtils;
import com.sitescape.ef.web.util.PortletRequestUtils;
import com.sitescape.ef.web.util.Toolbar;
import com.sitescape.ef.web.util.WebHelper;
import com.sitescape.util.Validator;

public class ViewEntryController extends SAbstractForumController {
	public void handleActionRequestInternal(ActionRequest request, ActionResponse response) throws Exception {
		response.setRenderParameters(request.getParameterMap());
		Map formData = request.getParameterMap();
		Long folderId = new Long(PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_BINDER_ID));				
		Long entryId = new Long(PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_ENTRY_ID));				
		
		//See if the user asked to change state
		if (formData.containsKey("changeStateBtn")) {
			//Change the state
			//Get the workflow process to change and the name of the new state
	        Long tokenId = new Long(PortletRequestUtils.getRequiredLongParameter(request, "tokenId"));	
			String toState = PortletRequestUtils.getRequiredStringParameter(request, "toState");
			getFolderModule().modifyWorkflowState(folderId, entryId, tokenId, toState);
		}
	}
	public ModelAndView handleRenderRequestInternal(RenderRequest request, 
			RenderResponse response) throws Exception {
		Map model;		
		PortletSession ses = WebHelper.getRequiredPortletSession(request);
		request.setAttribute(WebKeys.ACTION, WebKeys.ACTION_VIEW_ENTRY);

		Map formData1 = request.getParameterMap();
		Map formData = new HashMap((Map)formData1);

		Long folderId=null;
		try {
			folderId = ActionUtil.getForumId(request);
		} catch (NoFolderByTheIdException nf) {
			return new ModelAndView(WebKeys.VIEW_FORUM);
		}

			
        /**
         * This is the main forum dispatcher
         * You can get here for several reasons:
         * 1)  You can get here from a static forum url ".../c/portal/forum?forum=xxx&zone=zzz&op=yyy"
         * 2)  You can get here from the ss_forum portlet with no "op" specified
         * 3)  You can get here from a link specifying a specific "op"
         * 
         * This controller routine will forward to the desired jsp
         */
			        
		String op = PortletRequestUtils.getStringParameter(request, WebKeys.URL_OPERATION, "");
	
		formData.put(WebKeys.SESSION_LAST_ENTRY_VIEWED, ses.getAttribute(WebKeys.SESSION_LAST_ENTRY_VIEWED));
		String viewPath=WebKeys.VIEW_LISTING;
		String entryId = PortletRequestUtils.getStringParameter(request, WebKeys.URL_ENTRY_ID, "");
		model = getShowEntry(entryId, formData, request, response, folderId);
		entryId = (String)model.get(WebKeys.ENTRY_ID);
		model.put(WebKeys.URL_ENTRY_ID, entryId);
		if (op.equals("")) {
			Object obj = model.get(WebKeys.CONFIG_ELEMENT);
			if ((obj == null) || (obj.equals(""))) 
				return new ModelAndView(WebKeys.VIEW_NO_DEFINITION, model);
			obj = model.get(WebKeys.CONFIG_DEFINITION);
			if ((obj == null) || (obj.equals(""))) 
				return new ModelAndView(WebKeys.VIEW_NO_DEFINITION, model);
			ses.setAttribute(WebKeys.SESSION_LAST_ENTRY_VIEWED, Long.valueOf(entryId));
		} else if (op.equals(WebKeys.FORUM_OPERATION_VIEW_ENTRY) && !entryId.equals("")) {
			viewPath=WebKeys.VIEW_LISTING;
		} else if (op.equals(WebKeys.FORUM_OPERATION_VIEW_ENTRY_HISTORY_NEXT) ||
			op.equals(WebKeys.FORUM_OPERATION_VIEW_ENTRY_HISTORY_PREVIOUS) || entryId.equals("")) {
			if (!Validator.isNull(entryId) && !entryId.equals("")) {
				ses.setAttribute(WebKeys.SESSION_LAST_ENTRY_VIEWED, Long.valueOf(entryId));
			} else {
				ses.setAttribute(WebKeys.SESSION_LAST_ENTRY_VIEWED, null);
				viewPath = WebKeys.VIEW_NO_ENTRY;
			}
			
		} else if (op.equals(WebKeys.FORUM_OPERATION_VIEW_ENTRY_NEXT) ||
			op.equals(WebKeys.FORUM_OPERATION_VIEW_ENTRY_PREVIOUS)) {
			if (!Validator.isNull(entryId)) {
				ses.setAttribute(WebKeys.SESSION_LAST_ENTRY_VIEWED, Long.valueOf(entryId));
			} else {
				ses.setAttribute(WebKeys.SESSION_LAST_ENTRY_VIEWED, null);
				viewPath = WebKeys.VIEW_NO_ENTRY;
			}
		} 


		return new ModelAndView(viewPath, model);
	} 

	protected Entry setSeen(Map model, Long folderId) {
		SeenMap seen = (SeenMap)model.get(WebKeys.SEEN_MAP);
		FolderEntry entry = (FolderEntry)model.get(WebKeys.ENTRY);
		//only start transaction if necessary
		List replies = new ArrayList((List)model.get(WebKeys.FOLDER_ENTRY_DESCENDANTS));
		if (replies != null)  {
			replies.add(entry);
			for (int i=0; i<replies.size(); i++) {
				Entry reply = (Entry)replies.get(i);
				//if any reply is not seen, add it to list - try to avoid update transaction
				if (!seen.checkIfSeen(reply)) {
					getProfileModule().updateUserSeenEntry(null, replies);
					break;
				}
			}
		} else if (!seen.checkIfSeen(entry)) {
			getProfileModule().updateUserSeenEntry(null, entry);
		}
		return entry;
	}

	
}
