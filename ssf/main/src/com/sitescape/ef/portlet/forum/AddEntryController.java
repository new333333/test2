package com.sitescape.ef.portlet.forum;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletURL;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.dom4j.Document;
import org.dom4j.Element;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.sitescape.ef.portletadapter.MultipartFileSupport;
import com.sitescape.ef.web.WebKeys;
import com.sitescape.ef.web.util.DefinitionUtils;
import com.sitescape.ef.web.util.PortletRequestUtils;
import com.sitescape.ef.domain.Definition;
import com.sitescape.ef.domain.Folder;
import com.sitescape.ef.domain.FolderEntry;
import com.sitescape.ef.domain.NoDefinitionByTheIdException;

/**
 * @author Peter Hurley
 *
 */
public class AddEntryController extends SAbstractForumController {
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
			if (action.equals(WebKeys.ACTION_ADD_ENTRY)) {
				entryId= getFolderModule().addEntry(folderId, entryType, formData, fileMap);
			} else if (action.equals(WebKeys.FORUM_ACTION_ADD_REPLY)) {
				Long id = new Long(PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_ENTRY_ID));				
				entryId = getFolderModule().addReply(folderId, id, entryType, formData, fileMap );
			}
			setupViewEntry(response, folderId, entryId);
			//flag reload
			response.setRenderParameter("ssReloadUrl", "");
		} else if (formData.containsKey("cancelBtn")) {
			if (action.equals(WebKeys.ACTION_ADD_ENTRY)) {
				response.setRenderParameter(WebKeys.URL_BINDER_ID, folderId.toString());				
				response.setRenderParameter(WebKeys.ACTION, WebKeys.ACTION_VIEW_ENTRY);
				response.setRenderParameter("ssReloadUrl", "");
			} else if (action.equals(WebKeys.FORUM_ACTION_ADD_REPLY)) {
				entryId = new Long(PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_ENTRY_ID));				
				setupViewEntry(response, folderId, entryId);
			}
		} else {
			response.setRenderParameters(formData);
		}
			
	}
	private void setupViewEntry(ActionResponse response, Long folderId, Long entryId) {
		//return to view entry
		response.setRenderParameter(WebKeys.ACTION, WebKeys.ACTION_VIEW_ENTRY);
		response.setRenderParameter(WebKeys.URL_BINDER_ID, folderId.toString());
		response.setRenderParameter(WebKeys.URL_ENTRY_ID, entryId.toString());
		
	}
	public ModelAndView handleRenderRequestInternal(RenderRequest request, 
			RenderResponse response) throws Exception {
		
		Map model;
		Map formData1 = request.getParameterMap();
		Map formData = new HashMap((Map)formData1);
		Long folderId = new Long(PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_BINDER_ID));				
			
		String action = PortletRequestUtils.getStringParameter(request, WebKeys.ACTION, "");
		String path = WebKeys.VIEW_ADD_ENTRY;
		
		//See if this is an "add entry" or an "add reply" request
		try {
			if (action.equals(WebKeys.ACTION_ADD_ENTRY)) {
				model = new HashMap();
				Folder folder = getFolderModule().getFolder(folderId);
				//Adding an entry; get the specific definition
				Map folderEntryDefs = DefinitionUtils.getEntryDefsAsMap(folder);
				String entryType = PortletRequestUtils.getStringParameter(request, WebKeys.URL_ENTRY_TYPE, "");
				model.put(WebKeys.FOLDER, folder);
				model.put(WebKeys.ENTRY_DEFINTION_MAP, folderEntryDefs);
				model.put(WebKeys.CONFIG_JSP_STYLE, "form");
				//Make sure the requested definition is legal
				if (folderEntryDefs.containsKey(entryType)) {
					DefinitionUtils.getDefinition(getDefinitionModule().getDefinition(entryType), model, "//item[@name='entryForm']");
				} else {
					DefinitionUtils.getDefinition(null, model, "//item[@name='entryForm']");
				}
			} else {
		    	Long entryId = new Long(PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_ENTRY_ID));
		    	request.setAttribute(WebKeys.URL_ENTRY_ID,entryId.toString());
		    	model = new HashMap();
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
				Map folderEntryDefs = DefinitionUtils.getEntryDefsAsMap(folder);
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
					DefinitionUtils.getDefinition(getDefinitionModule().getDefinition(entryType), model, "//item[@name='entryForm']");
				} else {
					DefinitionUtils.getDefinition(null, model, "//item[@name='entryForm']");
				}
			}
		} catch (NoDefinitionByTheIdException nd) {
			//Get the jsp objects again, but this time get the "view_forum" values
			return returnToViewForum(request, response, formData, folderId);
		}

		return new ModelAndView(path, model);
	}
}


