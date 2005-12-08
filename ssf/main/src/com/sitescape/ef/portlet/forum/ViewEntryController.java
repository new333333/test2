package com.sitescape.ef.portlet.forum;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.springframework.web.servlet.ModelAndView;

import com.sitescape.ef.domain.Entry;
import com.sitescape.ef.domain.FolderEntry;
import com.sitescape.ef.domain.HistoryMap;
import com.sitescape.ef.domain.NoFolderByTheIdException;
import com.sitescape.ef.domain.SeenMap;
import com.sitescape.ef.web.WebKeys;
import com.sitescape.ef.web.util.PortletRequestUtils;
import com.sitescape.ef.web.util.WebHelper;
import com.sitescape.util.Validator;

public class ViewEntryController extends SAbstractForumController {
	public void handleActionRequestInternal(ActionRequest request, ActionResponse response) throws Exception {
		response.setRenderParameters(request.getParameterMap());
	}
	public ModelAndView handleRenderRequestInternal(RenderRequest request, 
			RenderResponse response) throws Exception {
		Map model;		
		PortletSession ses = WebHelper.getRequiredPortletSession(request);
		request.setAttribute(WebKeys.HISTORY_CACHE, ses.getAttribute(WebKeys.HISTORY_CACHE));
		request.setAttribute(WebKeys.ACTION, WebKeys.FORUM_ACTION_VIEW_ENTRY);

		Map formData1 = request.getParameterMap();
		Map formData = new HashMap((Map)formData1);

		Long folderId=null;
		try {
			folderId = ActionUtil.getForumId(request);
		} catch (NoFolderByTheIdException nf) {
			return new ModelAndView(WebKeys.VIEW);
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
			        
		String op = PortletRequestUtils.getStringParameter(request, WebKeys.FORUM_URL_OPERATION, "");
	
		formData.put(WebKeys.SESSION_LAST_ENTRY_VIEWED, ses.getAttribute(WebKeys.SESSION_LAST_ENTRY_VIEWED));
		formData.put(WebKeys.SESSION_LAST_HISTORY_ENTRY_VIEWED, ses.getAttribute(WebKeys.SESSION_LAST_HISTORY_ENTRY_VIEWED));
		String viewPath=WebKeys.VIEW_FORUM;
		String entryId = PortletRequestUtils.getStringParameter(request, WebKeys.FORUM_URL_ENTRY_ID, "");
		model = getForumActionModule().getShowEntry(entryId, formData, request, response, folderId);
		entryId = (String)model.get(WebKeys.ENTRY_ID);
		model.put(WebKeys.FORUM_URL_ENTRY_ID, entryId);
		if (op.equals("")) {
			Object obj = model.get(WebKeys.CONFIG_ELEMENT);
			if ((obj == null) || (obj.equals(""))) 
				return new ModelAndView(WebKeys.VIEW_NO_DEFINITION, model);
			obj = model.get(WebKeys.CONFIG_DEFINITION);
			if ((obj == null) || (obj.equals(""))) 
				return new ModelAndView(WebKeys.VIEW_NO_DEFINITION, model);
			ses.setAttribute(WebKeys.SESSION_LAST_ENTRY_VIEWED, Long.valueOf(entryId));
			setHistorySeen(model, ses, folderId, false); 
			ses.setAttribute(WebKeys.SESSION_LAST_HISTORY_ENTRY_VIEWED, null);
		} else if (op.equals(WebKeys.FORUM_OPERATION_VIEW_ENTRY) && !entryId.equals("")) {
			viewPath=WebKeys.VIEW_FORUM;
		} else if (op.equals(WebKeys.FORUM_OPERATION_VIEW_ENTRY_HISTORY_NEXT) ||
			op.equals(WebKeys.FORUM_OPERATION_VIEW_ENTRY_HISTORY_PREVIOUS) || entryId.equals("")) {
			if (!Validator.isNull(entryId) && !entryId.equals("")) {
				ses.setAttribute(WebKeys.SESSION_LAST_ENTRY_VIEWED, Long.valueOf(entryId));
				setHistorySeen(model, ses, folderId, true);
				ses.setAttribute(WebKeys.SESSION_LAST_HISTORY_ENTRY_VIEWED, Long.valueOf(entryId));
			} else {
				ses.setAttribute(WebKeys.SESSION_LAST_ENTRY_VIEWED, null);
				viewPath = WebKeys.VIEW_NO_ENTRY;
			}
			
		} else if (op.equals(WebKeys.FORUM_OPERATION_VIEW_ENTRY_NEXT) ||
			op.equals(WebKeys.FORUM_OPERATION_VIEW_ENTRY_PREVIOUS)) {
			if (!Validator.isNull(entryId)) {
				ses.setAttribute(WebKeys.SESSION_LAST_ENTRY_VIEWED, Long.valueOf(entryId));
				setHistorySeen(model, ses, folderId, false); 
				ses.setAttribute(WebKeys.SESSION_LAST_HISTORY_ENTRY_VIEWED, null);
			} else {
				ses.setAttribute(WebKeys.SESSION_LAST_ENTRY_VIEWED, null);
				viewPath = WebKeys.VIEW_NO_ENTRY;
			}
		} 


		return new ModelAndView(viewPath, model);
	} 
	protected void setHistorySeen(Map model, PortletSession ses, Long folderId, boolean inPlace) {
		Entry entry = setSeen(model, folderId);
		HistoryMap history = (HistoryMap)model.get(WebKeys.HISTORY_MAP);
		if (history == null) return;
		//cache history - will update itself every 5 minutes
		HistoryCache cache = (HistoryCache)ses.getAttribute(WebKeys.HISTORY_CACHE);
		if (cache == null) {
			cache = new HistoryCache(history);
			ses.setAttribute(WebKeys.HISTORY_CACHE, cache);
		} else if (!cache.getId().equals(history.getId())) {
			ses.removeAttribute(WebKeys.HISTORY_CACHE);
			cache = new HistoryCache(history);
			ses.setAttribute(WebKeys.HISTORY_CACHE, cache);
		}
		//set the cache so it can check whether to flush the history
		Long lastHistoryEntryViewed = (Long) ses.getAttribute(WebKeys.SESSION_LAST_HISTORY_ENTRY_VIEWED);
		if (lastHistoryEntryViewed == null) {
			if (inPlace) {
				cache.setHistorySeenInPlace(entry);
			} else {
				//Don't bother to sort the map, we weren't viewing a history entry
				cache.setHistorySeen(entry);
			}
		} else {
			if (inPlace) {
				//We are looking at history entries. Just mark the new time of viewing
				cache.setHistorySeenInPlace(entry);
			} else {
				//We were just looking at a history entry, so sort the map
				cache.sortAndSetHistorySeen(entry);
			}
		}
	}
	protected Entry setSeen(Map model, Long folderId) {
		SeenMap seen = (SeenMap)model.get(WebKeys.SEEN_MAP);
		FolderEntry entry = (FolderEntry)model.get(WebKeys.FOLDER_ENTRY);
		//only start transaction if necessary
		List replies = new ArrayList((List)model.get(WebKeys.FOLDER_ENTRY_DESCENDANTS));
		if (replies != null)  {
			replies.add(entry);
			for (int i=0; i<replies.size(); i++) {
				Entry reply = (Entry)replies.get(i);
				//if any reply is not seen, add it to list - try to avoid update transaction
				if (!seen.checkIfSeen(reply)) {
					getProfileModule().updateUserSeenEntry(null, folderId, replies);
					break;
				}
			}
		} else if (!seen.checkIfSeen(entry)) {
			getProfileModule().updateUserSeenEntry(null, folderId, entry);
		}
		return entry;
	}
}
