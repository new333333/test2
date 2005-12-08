package com.sitescape.ef.portlet.forum;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.WindowState;

import org.springframework.web.servlet.ModelAndView;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import com.sitescape.ef.domain.Entry;
import com.sitescape.ef.domain.SeenMap;
import com.sitescape.ef.domain.HistoryMap;
import com.sitescape.ef.portlet.PortletKeys;
import com.sitescape.ef.web.portlet.SAbstractController;
import com.sitescape.util.Validator;
import com.sitescape.ef.ObjectKeys;
import com.sitescape.ef.domain.NoFolderByTheIdException;
/**
 * @author Peter Hurley
 *
 */
public class ViewController  extends SAbstractController {
	public void handleActionRequestInternal(ActionRequest request, ActionResponse response) throws Exception {
		response.setRenderParameters(request.getParameterMap());
	}
	public ModelAndView handleRenderRequestInternal(RenderRequest request, 
			RenderResponse response) throws Exception {
		Map model;		
		PortletSession ses = request.getPortletSession();
		request.setAttribute(PortletKeys.HISTORY_CACHE, ses.getAttribute(PortletKeys.HISTORY_CACHE));
		Map formData = request.getParameterMap();
		Long folderId=null;
		try {
			folderId = ActionUtil.getForumId(formData, request);
		} catch (NoFolderByTheIdException nf) {
			return new ModelAndView(PortletKeys.VIEW);
		}
		if (request.getWindowState().equals(WindowState.NORMAL)) {
			return new ModelAndView(PortletKeys.VIEW, PortletKeys.FOLDER, getFolderModule().getFolder(folderId));
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
			        
		String op = ActionUtil.getStringValue(formData, PortletKeys.ACTION);
		request.setAttribute(PortletKeys.ACTION, op);
			
		formData.put(PortletKeys.SESSION_LAST_ENTRY_VIEWED, ses.getAttribute(PortletKeys.SESSION_LAST_ENTRY_VIEWED));
		formData.put(PortletKeys.SESSION_LAST_HISTORY_ENTRY_VIEWED, ses.getAttribute(PortletKeys.SESSION_LAST_HISTORY_ENTRY_VIEWED));
		String viewPath=PortletKeys.VIEW_FORUM;
		if (op.equals(PortletKeys.FORUM_OPERATION_VIEW_ENTRY)) {
			model = getForumActionModule().getShowEntry(formData, request, folderId);
			Object obj = model.get(PortletKeys.CONFIG_ELEMENT);
			if ((obj == null) || (obj.equals(""))) 
				return new ModelAndView(PortletKeys.VIEW_NO_DEFINITION, model);
			obj = model.get(PortletKeys.CONFIG_DEFINITION);
			if ((obj == null) || (obj.equals(""))) 
				return new ModelAndView(PortletKeys.VIEW_NO_DEFINITION, model);
			String entryId = (String)model.get(PortletKeys.ENTRY_ID);
			request.setAttribute(PortletKeys.FORUM_URL_ENTRY_ID, entryId);
			ses.setAttribute(PortletKeys.SESSION_LAST_ENTRY_VIEWED, Long.valueOf(entryId));
			setHistorySeen(model, ses, folderId, false); 
			ses.setAttribute(PortletKeys.SESSION_LAST_HISTORY_ENTRY_VIEWED, null);
		} else if (op.equals("") || op.equals(PortletKeys.FORUM_OPERATION_VIEW_FORUM)) {
			request.setAttribute(PortletKeys.ACTION, PortletKeys.FORUM_OPERATION_VIEW_FORUM);
			model = getForumActionModule().getShowFolder(formData, request, folderId);
			Object obj = model.get(PortletKeys.CONFIG_ELEMENT);
			if ((obj == null) || (obj.equals(""))) 
				return new ModelAndView(PortletKeys.VIEW_NO_DEFINITION, model);
			obj = model.get(PortletKeys.CONFIG_DEFINITION);
			if ((obj == null) || (obj.equals(""))) 
				return new ModelAndView(PortletKeys.VIEW_NO_DEFINITION, model);
		} else if (op.equals(PortletKeys.FORUM_OPERATION_SET_DISPLAY_STYLE)) {
   			String displayStyle = ActionUtil.getStringValue(formData,PortletKeys.FORUM_URL_VALUE);
   			getProfileModule().setUserProperty(null,ObjectKeys.USER_PROPERTY_DISPLAY_STYLE, displayStyle);
			model = getForumActionModule().getShowFolder(formData, request, folderId);
		} else if (op.equals(PortletKeys.FORUM_OPERATION_VIEW_ENTRY_HISTORY_NEXT) ||
			op.equals(PortletKeys.FORUM_OPERATION_VIEW_ENTRY_HISTORY_PREVIOUS)) {
			model = getForumActionModule().getShowEntry(formData, request, folderId);
			String entryId = (String)model.get(PortletKeys.ENTRY_ID);
			request.setAttribute(PortletKeys.ACTION, PortletKeys.FORUM_OPERATION_VIEW_ENTRY);
			request.setAttribute(PortletKeys.FORUM_URL_ENTRY_ID, entryId);
			if (!Validator.isNull(entryId)) {
				ses.setAttribute(PortletKeys.SESSION_LAST_ENTRY_VIEWED, Long.valueOf(entryId));
				setHistorySeen(model, ses, folderId, true);
				ses.setAttribute(PortletKeys.SESSION_LAST_HISTORY_ENTRY_VIEWED, Long.valueOf(entryId));
			} else {
				ses.setAttribute(PortletKeys.SESSION_LAST_ENTRY_VIEWED, null);
				viewPath = PortletKeys.VIEW_NO_ENTRY;
			}
			
		} else if (op.equals(PortletKeys.FORUM_OPERATION_VIEW_ENTRY_NEXT) ||
			op.equals(PortletKeys.FORUM_OPERATION_VIEW_ENTRY_PREVIOUS)) {
			model = getForumActionModule().getShowEntry(formData, request, folderId);
			String entryId = (String)model.get(PortletKeys.ENTRY_ID);
			request.setAttribute(PortletKeys.ACTION, PortletKeys.FORUM_OPERATION_VIEW_ENTRY);
			request.setAttribute(PortletKeys.FORUM_URL_ENTRY_ID, entryId);
			if (!Validator.isNull(entryId)) {
				ses.setAttribute(PortletKeys.SESSION_LAST_ENTRY_VIEWED, Long.valueOf(entryId));
				setHistorySeen(model, ses, folderId, false); 
				ses.setAttribute(PortletKeys.SESSION_LAST_HISTORY_ENTRY_VIEWED, null);
			} else {
				ses.setAttribute(PortletKeys.SESSION_LAST_ENTRY_VIEWED, null);
				viewPath = PortletKeys.VIEW_NO_ENTRY;
			}
		} else {
			model = new HashMap();
			model.put(PortletKeys.FOLDER,	 getFolderModule().getFolder(folderId));
			viewPath = op;					
		}


		return new ModelAndView(viewPath, model);
	} 

	protected void setHistorySeen(Map model, PortletSession ses, Long folderId, boolean inPlace) {
		Entry entry = setSeen(model, folderId);
		HistoryMap history = (HistoryMap)model.get(PortletKeys.HISTORY_MAP);
		if (history == null) return;
		//cache history - will update itself every 5 minutes
		HistoryCache cache = (HistoryCache)ses.getAttribute(PortletKeys.HISTORY_CACHE);
		if (cache == null) {
			cache = new HistoryCache(history);
			ses.setAttribute(PortletKeys.HISTORY_CACHE, cache);
		} else if (!cache.getId().equals(history.getId())) {
			ses.removeAttribute(PortletKeys.HISTORY_CACHE);
			cache = new HistoryCache(history);
			ses.setAttribute(PortletKeys.HISTORY_CACHE, cache);
		}
		//set the cache so it can check whether to flush the history
		Long lastHistoryEntryViewed = (Long) ses.getAttribute(PortletKeys.SESSION_LAST_HISTORY_ENTRY_VIEWED);
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
		SeenMap seen = (SeenMap)model.get(PortletKeys.SEEN_MAP);
		Entry entry = (Entry)model.get(PortletKeys.FOLDER_ENTRY);
		//only start transaction if necessary
		List replies = new ArrayList((List)model.get("ss_forum_entry_descendants"));
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
