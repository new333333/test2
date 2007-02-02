package com.sitescape.team.portlet.forum;

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

import com.sitescape.team.domain.Definition;
import com.sitescape.team.domain.Folder;
import com.sitescape.team.domain.FolderEntry;
import com.sitescape.team.module.shared.MapInputData;
import com.sitescape.team.portletadapter.MultipartFileSupport;
import com.sitescape.team.web.WebKeys;
import com.sitescape.team.web.portlet.SAbstractController;
import com.sitescape.team.web.util.DefinitionHelper;
import com.sitescape.team.web.util.PortletRequestUtils;

/**
 * @author Peter Hurley
 *
 */
public class AddEntryController extends SAbstractController {
	public void handleActionRequestAfterValidation(ActionRequest request, ActionResponse response) 
	throws Exception {
		Map formData = request.getParameterMap();
		Long folderId = new Long(PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_BINDER_ID));				
		String action = PortletRequestUtils.getStringParameter(request, WebKeys.ACTION, "");
		String blogReply = PortletRequestUtils.getStringParameter(request, WebKeys.URL_BLOG_REPLY, "");
		String namespace = PortletRequestUtils.getStringParameter(request, WebKeys.URL_NAMESPACE, "");
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
				setupReloadOpener(response, folderId, entryId);
			} else if (action.equals(WebKeys.ACTION_ADD_FOLDER_REPLY)) {
				Long id = new Long(PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_ENTRY_ID));				
				entryId = getFolderModule().addReply(folderId, id, entryType, inputData, fileMap );
				//Show the parent entry when this operation finishes
				setupReloadOpener(response, folderId, id);
				if (!blogReply.equals("")) {
			    	FolderEntry entry = getFolderModule().getEntry(folderId, entryId);
					response.setRenderParameter(WebKeys.BLOG_REPLY, "1");
					response.setRenderParameter(WebKeys.NAMESPACE, namespace);
					response.setRenderParameter(WebKeys.ENTRY_ID, entry.getParentEntry().getId().toString());
					response.setRenderParameter(WebKeys.BLOG_REPLY_COUNT, String.valueOf(entry.getParentEntry().getTotalReplyCount()));
				}
			}
			//flag reload of folder listing
			//response.setRenderParameter("ssReloadUrl", "");
		} else if (formData.containsKey("cancelBtn")) {
			if (!blogReply.equals("")) {
				setupReloadOpener(response, folderId, entryId);
		    	FolderEntry entry = getFolderModule().getEntry(folderId, entryId);
				response.setRenderParameter(WebKeys.BLOG_REPLY, "1");
				response.setRenderParameter(WebKeys.NAMESPACE, namespace);
				response.setRenderParameter(WebKeys.ENTRY_ID, entryId.toString());
				response.setRenderParameter(WebKeys.BLOG_REPLY_COUNT, String.valueOf(entry.getTotalReplyCount()));
			} else {
				setupCloseWindow(response);
			}
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
			} else {
				DefinitionHelper.getDefinition(null, model, "//item[@name='entryForm']");
			}
		} else {
	    	Long entryId = new Long(PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_ENTRY_ID));
	    	request.setAttribute(WebKeys.URL_ENTRY_ID,entryId.toString());
	    	FolderEntry entry = getFolderModule().getEntry(folderId, entryId);
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
			} else {
				DefinitionHelper.getDefinition(null, model, "//item[@name='entryForm']");
			}
		}
		return new ModelAndView(path, model);
	}
}


