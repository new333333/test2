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
		Long folderId = ActionUtil.getForumId(request);
		String entryId="";
		String action = PortletRequestUtils.getStringParameter(request, WebKeys.ACTION, "");
		//See if the add entry form was submitted
		if (formData.containsKey("okBtn")) {
			//The form was submitted. Go process it
			// Returns a map where key is form field name (String) and value is LiferayFileItem.
			String entryType = PortletRequestUtils.getStringParameter(request, WebKeys.URL_ENTRY_TYPE, "");
			Map fileMap=null;
			if (request instanceof MultipartFileSupport) {
				fileMap = ((MultipartFileSupport) request).getFileMap();
			} else {
				fileMap = new HashMap();
			}
			if (action.equals(WebKeys.ACTION_ADD_ENTRY)) {
				Long id = getFolderModule().addEntry(folderId, entryType, formData, fileMap);
				entryId = id.toString();
			} else if (action.equals(WebKeys.FORUM_ACTION_ADD_REPLY)) {
				Long id = new Long(PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_ENTRY_ID));				
				getFolderModule().addReply(folderId, id, entryType, formData, fileMap );
				entryId = id.toString();
			}
			//response.setRenderParameter(WebKeys.ACTION, WebKeys.ACTION_VIEW_LISTING);
			//response.setRenderParameter(WebKeys.URL_BINDER_ID, folderId.toString());
		} else if (formData.containsKey("cancelBtn")) {
			//response.setRenderParameter(WebKeys.ACTION, WebKeys.ACTION_VIEW_LISTING);
			//response.setRenderParameter(WebKeys.URL_BINDER_ID, folderId.toString());
			if (action.equals(WebKeys.ACTION_ADD_ENTRY)) {
			} else if (action.equals(WebKeys.FORUM_ACTION_ADD_REPLY)) {
				entryId = PortletRequestUtils.getStringParameter(request, WebKeys.URL_ENTRY_ID, "");				
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
			formData.put(WebKeys.URL_ENTRY_ID, entryId);
			try {
				if (entryId.equals("")) {
					return returnToViewForum(request, response, formData, folderId);
				} else {
					model = getShowEntry(entryId, formData, request, response, folderId);
				}
			} catch (NoDefinitionByTheIdException nd) {
				return returnToViewForum(request, response, formData, folderId);
			}
			path = WebKeys.VIEW_LISTING;
			model.put(WebKeys.URL_OPERATION, WebKeys.FORUM_OPERATION_VIEW_ENTRY);
			request.setAttribute(WebKeys.ACTION, WebKeys.ACTION_VIEW_ENTRY);
			PortletURL reloadUrl = response.createRenderURL();
			reloadUrl.setParameter(WebKeys.URL_BINDER_ID, folderId.toString());
			reloadUrl.setParameter(WebKeys.URL_ENTRY_ID, entryId);
			reloadUrl.setParameter(WebKeys.URL_OPERATION, WebKeys.FORUM_OPERATION_VIEW_ENTRY);
			reloadUrl.setParameter(WebKeys.ACTION, WebKeys.ACTION_VIEW_ENTRY);
			request.setAttribute("ssReloadUrl", reloadUrl.toString());
		} else {
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
		}

		return new ModelAndView(path, model);
	}
}


