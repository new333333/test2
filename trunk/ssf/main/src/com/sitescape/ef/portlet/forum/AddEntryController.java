package com.sitescape.ef.portlet.forum;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.dom4j.Document;
import org.dom4j.Element;
import org.springframework.web.servlet.ModelAndView;

import com.sitescape.ef.domain.Definition;
import com.sitescape.ef.domain.Folder;
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
public class AddEntryController extends SAbstractController {
	public void handleActionRequestInternal(ActionRequest request, ActionResponse response) 
	throws Exception {
		Map formData = request.getParameterMap();
		Long folderId = new Long(PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_BINDER_ID));				
		String action = PortletRequestUtils.getStringParameter(request, WebKeys.ACTION, "");
		//See if the add entry form was submitted
		Long entryId=null;
		if (formData.containsKey("okBtn")) {
			//The form was submitted. Go process it
			String entryType = PortletRequestUtils.getStringParameter(request, WebKeys.URL_ENTRY_TYPE, "");
			Map fileMap=null;
			if (request instanceof MultipartFileSupport) {
				fileMap = ((MultipartFileSupport) request).getFileMap();
			} else {
				fileMap = new HashMap();
			}
			MapInputData inputData = new MapInputData(formData);
			if (action.equals(WebKeys.ACTION_ADD_FOLDER_ENTRY)) {
				entryId= getFolderModule().addEntry(folderId, entryType, inputData, fileMap);
				setupViewEntry(response, folderId, entryId);
			} else if (action.equals(WebKeys.ACTION_ADD_FOLDER_REPLY)) {
				Long id = new Long(PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_ENTRY_ID));				
				entryId = getFolderModule().addReply(folderId, id, entryType, inputData, fileMap );
				//Show the parent entry when this operation finishes
				setupViewEntry(response, folderId, id);
			}
			//flag reload of folder listing
			response.setRenderParameter("ssReloadUrl", "");
		} else if (formData.containsKey("cancelBtn")) {
			if (action.equals(WebKeys.ACTION_ADD_FOLDER_ENTRY)) {
				response.setRenderParameter(WebKeys.URL_BINDER_ID, folderId.toString());				
				response.setRenderParameter(WebKeys.ACTION, WebKeys.ACTION_VIEW_FOLDER_LISTING);
				response.setRenderParameter(WebKeys.URL_OPERATION, WebKeys.FORUM_OPERATION_RELOAD_LISTING);
			} else if (action.equals(WebKeys.ACTION_ADD_FOLDER_REPLY)) {
				entryId = new Long(PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_ENTRY_ID));				
				setupViewEntry(response, folderId, entryId);
			}
		} else {
			response.setRenderParameters(formData);
		}
			
	}
	private void setupViewEntry(ActionResponse response, Long folderId, Long entryId) {
		//return to view entry
		response.setRenderParameter(WebKeys.ACTION, WebKeys.ACTION_VIEW_FOLDER_ENTRY);
		response.setRenderParameter(WebKeys.URL_BINDER_ID, folderId.toString());
		response.setRenderParameter(WebKeys.URL_ENTRY_ID, entryId.toString());
		
	}
	public ModelAndView handleRenderRequestInternal(RenderRequest request, 
			RenderResponse response) throws Exception {
		
		Map model = new HashMap();
		Long folderId = new Long(PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_BINDER_ID));				
			
		String action = PortletRequestUtils.getStringParameter(request, WebKeys.ACTION, "");
		model.put(WebKeys.OPERATION, action);
		String path = WebKeys.VIEW_ADD_ENTRY;
		
		//See if this is an "add entry" or an "add reply" request
		if (action.equals(WebKeys.ACTION_ADD_FOLDER_ENTRY)) {
			Folder folder = getFolderModule().getFolder(folderId);
			//Adding an entry; get the specific definition
			Map folderEntryDefs = DefinitionHelper.getEntryDefsAsMap(folder);
			String entryType = PortletRequestUtils.getStringParameter(request, WebKeys.URL_ENTRY_TYPE, "");
			model.put(WebKeys.FOLDER, folder);
			model.put(WebKeys.ENTRY_DEFINTION_MAP, folderEntryDefs);
			model.put(WebKeys.CONFIG_JSP_STYLE, "form");
			//Make sure the requested definition is legal
			if (folderEntryDefs.containsKey(entryType)) {
				DefinitionHelper.getDefinition(getDefinitionModule().getDefinition(entryType), model, "//item[@type='form']");
			} else if ((folder.getDefinitionType()!=null) && (folder.getDefinitionType().intValue() == Definition.FILE_FOLDER_VIEW)) {
				DefinitionHelper.getDefinition(null, model, "//item[@name='fileEntryForm']");
			} else {
				DefinitionHelper.getDefinition(null, model, "//item[@name='entryForm']");
			}
		} else {
	    	Long entryId = new Long(PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_ENTRY_ID));
	    	request.setAttribute(WebKeys.URL_ENTRY_ID,entryId.toString());
	    	FolderEntry entry = getFolderModule().getEntry(folderId, entryId);
	    	model.put(WebKeys.DEFINITION_ENTRY, entry);
	    	Folder folder = entry.getParentFolder();
	    	model.put(WebKeys.FOLDER, folder); 
				
	    	//Get the legal reply types from the parent entry definition
			Document entryView = null;
			Definition entryDefinition = entry.getEntryDef();
			if (entryDefinition != null) {
				entryView = entryDefinition.getDefinition();
			}
			Iterator replyStyles = null;
			if (entryView != null) {
				//See if there is a reply style for this entry definition
				replyStyles = entryView.getRootElement().selectNodes("properties/property[@name='replyStyle']").iterator();
			}
	   	
	    	//Adding an entry; get the specific definition
			Map folderEntryDefs = DefinitionHelper.getEntryDefsAsMap(folder);
	    	String entryType = PortletRequestUtils.getStringParameter(request, WebKeys.URL_ENTRY_TYPE, "");
	    	model.put(WebKeys.ENTRY_DEFINTION_MAP, folderEntryDefs);
	    	model.put(WebKeys.CONFIG_JSP_STYLE, "form");
	    	
	        //Make sure the requested reply definition is legal
	    	boolean replyStyleIsGood = false;
	    	while (replyStyles.hasNext()) {
	    		if (((String)((Element)replyStyles.next()).attributeValue("value", "")).equals(entryType)) {
	    			replyStyleIsGood = true;
	    			break;
	    		}
	    	}
		    	
			if (replyStyleIsGood) {
				DefinitionHelper.getDefinition(getDefinitionModule().getDefinition(entryType), model, "//item[@type='form']");
			} else if ((entry.getDefinitionType()!=null) && (entry.getDefinitionType().intValue() == Definition.FILE_ENTRY_VIEW)) {
				DefinitionHelper.getDefinition(null, model, "//item[@name='fileEntryForm']");
			} else {
				DefinitionHelper.getDefinition(null, model, "//item[@name='entryForm']");
			}
		}
		return new ModelAndView(path, model);
	}
}


