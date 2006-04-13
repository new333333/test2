package com.sitescape.ef.portlet.forum;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.sitescape.ef.domain.Binder;
import com.sitescape.ef.domain.FolderEntry;
import com.sitescape.ef.domain.NoDefinitionByTheIdException;
import com.sitescape.ef.module.shared.MapInputData;
import com.sitescape.ef.portletadapter.MultipartFileSupport;
import com.sitescape.ef.web.WebKeys;
import com.sitescape.ef.web.util.DefinitionUtils;
import com.sitescape.ef.web.util.PortletRequestUtils;

/**
 * @author Peter Hurley
 *
 */
public class ModifyEntryController extends SAbstractForumController {
	public void handleActionRequestInternal(ActionRequest request, ActionResponse response) 
	throws Exception {

		Map formData = request.getParameterMap();
		Long folderId = new Long(PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_BINDER_ID));				
		Long entryId = new Long(PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_ENTRY_ID));				
		String action = PortletRequestUtils.getStringParameter(request, WebKeys.ACTION, "");
		if (action.equals(WebKeys.ACTION_DELETE_ENTRY)) {
			getFolderModule().deleteEntry(folderId, entryId);
			response.setRenderParameter(WebKeys.URL_BINDER_ID, folderId.toString());		
			response.setRenderParameter(WebKeys.ACTION, WebKeys.ACTION_VIEW_LISTING);
			response.setRenderParameter(WebKeys.URL_OPERATION, WebKeys.FORUM_OPERATION_RELOAD_LISTING);
			response.setRenderParameter("ssReloadUrl", "");
		} else if (formData.containsKey("okBtn")) {
			if (action.equals(WebKeys.ACTION_MODIFY_ENTRY)) {

				//See if the add entry form was submitted
				//The form was submitted. Go process it
				Map fileMap=null;
				if (request instanceof MultipartFileSupport) {
					fileMap = ((MultipartFileSupport) request).getFileMap();
				} else {
					fileMap = new HashMap();
				}
				Set deleteAtts = new HashSet();
				for (Iterator iter=formData.entrySet().iterator(); iter.hasNext();) {
					Map.Entry e = (Map.Entry)iter.next();
					String key = (String)e.getKey();
					if (key.startsWith("_delete_")) {
						deleteAtts.add(key.substring(8));
					}
					
				}
			
				getFolderModule().modifyEntry(folderId, entryId, new MapInputData(formData), fileMap, deleteAtts);
				setupViewEntry(response, folderId, entryId);
			} else if (action.equals(WebKeys.ACTION_MOVE_ENTRY)) {
				//must be move entry
				Long destinationId = new Long(PortletRequestUtils.getRequiredLongParameter(request, "destination"));
				getFolderModule().moveEntry(folderId, entryId, destinationId);
				response.setRenderParameter(WebKeys.URL_BINDER_ID, folderId.toString());		
				response.setRenderParameter(WebKeys.ACTION, WebKeys.ACTION_VIEW_LISTING);
				response.setRenderParameter(WebKeys.URL_OPERATION, WebKeys.FORUM_OPERATION_RELOAD_LISTING);
				response.setRenderParameter("ssReloadUrl", "");
			}
		} else if (formData.containsKey("cancelBtn")) {
			//The user clicked the cancel button
			setupViewEntry(response, folderId, entryId);
		} else {
			response.setRenderParameters(formData);		
		}
	}
	private void setupViewEntry(ActionResponse response, Long folderId, Long entryId) {
		response.setRenderParameter(WebKeys.URL_BINDER_ID, folderId.toString());		
		response.setRenderParameter(WebKeys.URL_ENTRY_ID, entryId.toString());		
		response.setRenderParameter(WebKeys.ACTION, WebKeys.ACTION_VIEW_ENTRY);
	}
		
	public ModelAndView handleRenderRequestInternal(RenderRequest request, 
		RenderResponse response) throws Exception {
		Map formData = request.getParameterMap();
		Long folderId = new Long(PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_BINDER_ID));				

		Map model = new HashMap();	
		String action = PortletRequestUtils.getStringParameter(request, WebKeys.ACTION, "");
		String path;
		FolderEntry entry=null;
		if (action.equals(WebKeys.ACTION_MODIFY_ENTRY)) {
			try {
				Long entryId = new Long(PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_ENTRY_ID));
				entry  = getFolderModule().getEntry(folderId, entryId);
				
				model.put(WebKeys.ENTRY, entry);
				model.put(WebKeys.FOLDER, entry.getParentFolder());
				model.put(WebKeys.CONFIG_JSP_STYLE, "form");
				DefinitionUtils.getDefinition(entry.getEntryDef(), model, "//item[@name='entryForm']");
				path = WebKeys.VIEW_MODIFY_ENTRY;
			} catch (NoDefinitionByTheIdException nd) {
				return returnToViewForum(request, response, formData, folderId);
			}
		} else if (action.equals(WebKeys.ACTION_MOVE_ENTRY)) {
			Long entryId = new Long(PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_ENTRY_ID));
			entry  = getFolderModule().getEntry(folderId, entryId);
			model.put(WebKeys.ENTRY, entry);
			model.put(WebKeys.BINDER, entry.getParentFolder());
			path = WebKeys.VIEW_MOVE_ENTRY;
			
		} else
			return returnToViewForum(request, response, formData, folderId);
			
		return new ModelAndView(path, model);
	}
}

