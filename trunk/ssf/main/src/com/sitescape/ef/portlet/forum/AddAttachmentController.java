package com.sitescape.ef.portlet.forum;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.springframework.web.servlet.ModelAndView;

import com.sitescape.ef.domain.FolderEntry;
import com.sitescape.ef.module.shared.MapInputData;
import com.sitescape.ef.portletadapter.MultipartFileSupport;
import com.sitescape.ef.web.WebKeys;
import com.sitescape.ef.web.portlet.SAbstractController;
import com.sitescape.ef.web.util.DefinitionHelper;
import com.sitescape.ef.web.util.PortletRequestUtils;

/**
 * @author Peter Hurley
 *
 */
public class AddAttachmentController extends SAbstractController {
	public void handleActionRequestAfterValidation(ActionRequest request, ActionResponse response) 
	throws Exception {
		
		Map formData = request.getParameterMap();
		Long folderId = new Long(PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_BINDER_ID));				
		Long entryId = new Long(PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_ENTRY_ID));				
		String op = PortletRequestUtils.getStringParameter(request, WebKeys.URL_OPERATION, "");
		
		if (op.equals(WebKeys.OPERATION_ADD_FILES_FROM_APPLET)) {
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
			Boolean filesFromApplet = new Boolean(true);
			getFolderModule().modifyEntry(folderId, entryId, new MapInputData(formData), fileMap, deleteAtts, null, filesFromApplet);
			setupReloadOpener(response, folderId, entryId);
			//flag reload of folder listing
			//response.setRenderParameter("ssReloadUrl", "");
		} else {
			response.setRenderParameters(formData);		
		}
	}

	private void setupReloadOpener(ActionResponse response, Long folderId, Long entryId) {
		//return to view entry
		response.setRenderParameter(WebKeys.ACTION, WebKeys.ACTION_RELOAD_OPENER);
		response.setRenderParameter(WebKeys.URL_BINDER_ID, folderId.toString());
		response.setRenderParameter(WebKeys.URL_ENTRY_ID, entryId.toString());
	}
	private void setupCloseWindow(ActionResponse response) {
		//return to view entry
		response.setRenderParameter(WebKeys.ACTION, WebKeys.ACTION_CLOSE_WINDOW);
	}
	private void setupViewEntry(ActionResponse response, Long folderId, Long entryId) {
		response.setRenderParameter(WebKeys.URL_BINDER_ID, folderId.toString());		
		response.setRenderParameter(WebKeys.URL_ENTRY_ID, entryId.toString());		
		response.setRenderParameter(WebKeys.ACTION, WebKeys.ACTION_VIEW_FOLDER_ENTRY);
		response.setRenderParameter(WebKeys.IS_REFRESH, "1");
	}
	private void setupViewFolder(ActionResponse response, Long folderId) {
		response.setRenderParameter(WebKeys.URL_BINDER_ID, folderId.toString());		
		response.setRenderParameter(WebKeys.ACTION, WebKeys.ACTION_VIEW_FOLDER_LISTING);
		response.setRenderParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_RELOAD_LISTING);
		
	}
		
	public ModelAndView handleRenderRequestInternal(RenderRequest request, 
		RenderResponse response) throws Exception {
		Long folderId = new Long(PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_BINDER_ID));				

		Map model = new HashMap();	
		String action = PortletRequestUtils.getStringParameter(request, WebKeys.ACTION, "");
		model.put(WebKeys.OPERATION, action);
		String op = PortletRequestUtils.getStringParameter(request, WebKeys.URL_OPERATION, "");
		String path;
		FolderEntry entry=null;
		if (op.equals(WebKeys.OPERATION_MOVE)) {
			Long entryId = new Long(PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_ENTRY_ID));
			entry  = getFolderModule().getEntry(folderId, entryId);
			model.put(WebKeys.ENTRY, entry);
			model.put(WebKeys.BINDER, entry.getParentFolder());
			path = WebKeys.VIEW_MOVE_ENTRY;
		} else {
			Long entryId = new Long(PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_ENTRY_ID));
			entry  = getFolderModule().getEntry(folderId, entryId);
				
			model.put(WebKeys.ENTRY, entry);
			model.put(WebKeys.FOLDER, entry.getParentFolder());
			model.put(WebKeys.CONFIG_JSP_STYLE, "form");
			DefinitionHelper.getDefinition(entry.getEntryDef(), model, "//item[@type='form']");
			path = WebKeys.VIEW_MODIFY_ENTRY;
		} 
			
		return new ModelAndView(path, model);
	}
}

