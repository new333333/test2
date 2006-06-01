package com.sitescape.ef.portlet.forum;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Iterator;

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
import com.sitescape.ef.domain.Definition;
import com.sitescape.ef.domain.Entry;
import com.sitescape.ef.domain.Folder;
import com.sitescape.ef.domain.FolderEntry;
import com.sitescape.ef.domain.NoDefinitionByTheIdException;
import com.sitescape.ef.domain.NoFolderByTheIdException;
import com.sitescape.ef.domain.SeenMap;
import com.sitescape.ef.domain.WorkflowState;
import com.sitescape.ef.module.folder.FolderModule;
import com.sitescape.ef.util.NLT;
import com.sitescape.ef.web.WebKeys;
import com.sitescape.ef.web.portlet.SAbstractController;
import com.sitescape.ef.web.util.BinderHelper;
import com.sitescape.ef.module.workflow.WorkflowUtils;
import com.sitescape.ef.web.util.DefinitionUtils;
import com.sitescape.ef.web.util.PortletRequestUtils;
import com.sitescape.ef.web.util.Toolbar;
import com.sitescape.ef.web.util.WebHelper;
import com.sitescape.util.Validator;
import com.sitescape.ef.security.AccessControlException;


public class ViewEntryController extends  SAbstractController {
	public void handleActionRequestInternal(ActionRequest request, ActionResponse response) throws Exception {
		response.setRenderParameters(request.getParameterMap());
		Map formData = request.getParameterMap();
		Long folderId = new Long(PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_BINDER_ID));				
		Long entryId = new Long(PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_ENTRY_ID));				
		
		//See if the user asked to change state
		if (formData.containsKey("changeStateBtn")) {
			//Change the state
			//Get the workflow process to change and the name of the new state
			Long replyId = new Long(PortletRequestUtils.getLongParameter(request, "replyId"));
			if (replyId == null) replyId = entryId;
	        Long tokenId = new Long(PortletRequestUtils.getRequiredLongParameter(request, "tokenId"));	
			String toState = PortletRequestUtils.getRequiredStringParameter(request, "toState");
			getFolderModule().modifyWorkflowState(folderId, replyId, tokenId, toState);
		}
		response.setRenderParameters(formData);
	}
	public ModelAndView handleRenderRequestInternal(RenderRequest request, 
			RenderResponse response) throws Exception {
		Long folderId = new Long(PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_BINDER_ID));				

		Map formData = request.getParameterMap();
		String viewPath = BinderHelper.getViewListingJsp();
		String entryId = PortletRequestUtils.getStringParameter(request, WebKeys.URL_ENTRY_ID, "");
		Map model = getShowEntry(entryId, formData, request, response, folderId);
		entryId = (String)model.get(WebKeys.ENTRY_ID);
		model.put(WebKeys.URL_ENTRY_ID, entryId);
		model.put(WebKeys.ACTION, WebKeys.ACTION_VIEW_FOLDER_ENTRY);

		if (formData.containsKey("ssReloadUrl")) {
			PortletURL reloadUrl = response.createRenderURL();
			reloadUrl.setParameter(WebKeys.URL_BINDER_ID, folderId.toString());
			reloadUrl.setParameter(WebKeys.URL_ENTRY_ID, entryId);
			reloadUrl.setParameter(WebKeys.URL_OPERATION, WebKeys.FORUM_OPERATION_VIEW_ENTRY);
			reloadUrl.setParameter(WebKeys.ACTION, WebKeys.ACTION_VIEW_FOLDER_ENTRY);
			request.setAttribute("ssReloadUrl", reloadUrl.toString());			
		} else {
			Object obj = model.get(WebKeys.CONFIG_ELEMENT);
			if ((obj == null) || (obj.equals(""))) 
				return new ModelAndView(WebKeys.VIEW_NO_DEFINITION, model);
			obj = model.get(WebKeys.CONFIG_DEFINITION);
			if ((obj == null) || (obj.equals(""))) 
				return new ModelAndView(WebKeys.VIEW_NO_DEFINITION, model);
		}

		return new ModelAndView(viewPath, model);
	} 

	protected Toolbar buildEntryToolbar(RenderResponse response, Map model, String folderId, String entryId) {
		Element entryViewElement = (Element)model.get(WebKeys.CONFIG_ELEMENT);
		Document entryView = entryViewElement.getDocument();
		Definition def = (Definition)model.get(WebKeys.ENTRY_DEFINITION);
		String entryDefId="";
		if (def != null)
			entryDefId= def.getId().toString();
	    //Build the toolbar array
		Toolbar toolbar = new Toolbar();
	    //The "Reply" menu
		List replyStyles = entryView.getRootElement().selectNodes("properties/property[@name='replyStyle']");
		PortletURL url;
		FolderEntry entry = (FolderEntry)model.get(WebKeys.ENTRY);
		if (!replyStyles.isEmpty()) {
			try {
				getFolderModule().checkAddReplyAllowed(entry);
				if (replyStyles.size() == 1) {
					//There is only one reply style, so show it not as a drop down menu
					String replyStyleId = ((Element)replyStyles.get(0)).attributeValue("value", "");
					if (!replyStyleId.equals("")) {
						Map params = new HashMap();
						params.put(WebKeys.ACTION, WebKeys.ACTION_ADD_FOLDER_REPLY);
						params.put(WebKeys.URL_BINDER_ID, folderId);
						params.put(WebKeys.URL_ENTRY_TYPE, replyStyleId);
						params.put(WebKeys.URL_ENTRY_ID, entryId);
						Map qualifiers = new HashMap();
						qualifiers.put("popup", new Boolean(true));
						toolbar.addToolbarMenu("1_reply", NLT.get("toolbar.reply"), params, qualifiers);
					}
				} else {
					toolbar.addToolbarMenu("1_reply", NLT.get("toolbar.reply"));
					for (int i = 0; i < replyStyles.size(); i++) {
						String replyStyleId = ((Element)replyStyles.get(i)).attributeValue("value", "");
						try {
							Definition replyDef = getDefinitionModule().getDefinition(replyStyleId);
							url = response.createActionURL();
							url.setParameter(WebKeys.ACTION, WebKeys.ACTION_ADD_FOLDER_REPLY);
							url.setParameter(WebKeys.URL_BINDER_ID, folderId);
							url.setParameter(WebKeys.URL_ENTRY_TYPE, replyStyleId);
							url.setParameter(WebKeys.URL_ENTRY_ID, entryId);
							toolbar.addToolbarMenuItem("1_reply", "replies", replyDef.getTitle(), url);
						} catch (NoDefinitionByTheIdException e) {
							continue;
						}
					}
				} 
			} catch (AccessControlException ac) {};
		}
	    
		try {
			getFolderModule().checkModifyEntryAllowed(entry);
			//The "Modify" menu
			url = response.createActionURL();
			url.setParameter(WebKeys.ACTION, WebKeys.ACTION_MODIFY_FOLDER_ENTRY);
			url.setParameter(WebKeys.URL_BINDER_ID, folderId);
			url.setParameter(WebKeys.URL_ENTRY_TYPE, entryDefId);
			url.setParameter(WebKeys.URL_ENTRY_ID, entryId);
			toolbar.addToolbarMenu("2_modify", NLT.get("toolbar.modify"), url);
			//The "Move" menu
			url = response.createActionURL();
			url.setParameter(WebKeys.ACTION, WebKeys.ACTION_MODIFY_FOLDER_ENTRY);
			url.setParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_MOVE);
			url.setParameter(WebKeys.URL_BINDER_ID, folderId);
			url.setParameter(WebKeys.URL_ENTRY_ID, entryId);
			toolbar.addToolbarMenu("3_move", NLT.get("toolbar.move"), url);
		} catch (AccessControlException ac) {};
		
	    
		try {
			getFolderModule().checkDeleteEntryAllowed(entry);
			//The "Delete" menu
			url = response.createActionURL();
			url.setParameter(WebKeys.ACTION, WebKeys.ACTION_MODIFY_FOLDER_ENTRY);
			url.setParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_DELETE);
			url.setParameter(WebKeys.URL_BINDER_ID, folderId);
			url.setParameter(WebKeys.URL_ENTRY_TYPE, entryDefId);
			url.setParameter(WebKeys.URL_ENTRY_ID, entryId); 
			toolbar.addToolbarMenu("4_delete", NLT.get("toolbar.delete"), url);
		} catch (AccessControlException ac) {};
	    
		return toolbar;
	}

	protected Map getShowEntry(String entryId, Map formData, RenderRequest req, RenderResponse response, Long folderId)  {
		Map model = new HashMap();
		Folder folder = null;
		FolderEntry entry = null;
		Map folderEntries = null;
		if (!entryId.equals("")) {
			folderEntries  = getFolderModule().getEntryTree(folderId, Long.valueOf(entryId));
			if (folderEntries != null) {
				entry = (FolderEntry)folderEntries.get(ObjectKeys.FOLDER_ENTRY);
				folder = entry.getParentFolder();
				model.put(WebKeys.FOLDER_ENTRY_DESCENDANTS, folderEntries.get(ObjectKeys.FOLDER_ENTRY_DESCENDANTS));
				model.put(WebKeys.FOLDER_ENTRY_ANCESTORS, folderEntries.get(ObjectKeys.FOLDER_ENTRY_ANCESTORS));
			}
		} else {
			folder = getFolderModule().getFolder(folderId);
		}
		SeenMap seen = getProfileModule().getUserSeenMap(null);
		model.put(WebKeys.ENTRY_ID, entryId);
		model.put(WebKeys.SEEN_MAP, seen);
		model.put(WebKeys.ENTRY, entry);
		model.put(WebKeys.DEFINITION_ENTRY, entry);
		model.put(WebKeys.FOLDER, folder);
		model.put(WebKeys.CONFIG_JSP_STYLE, "view");
		model.put(WebKeys.USER_PROPERTIES, getProfileModule().getUserProperties(null).getProperties());
		if (entry == null) {
			DefinitionUtils.getDefinition(null, model, "//item[@name='entryView']");
			return model;
		}
		if (DefinitionUtils.getDefinition(entry.getEntryDef(), model, "//item[@name='entryView']") == false) {
			DefinitionUtils.getDefaultEntryView(entry, model);
		}
		if (!entryId.equals("")) {
			model.put(WebKeys.FOLDER_ENTRY_TOOLBAR, buildEntryToolbar(response, model, folderId.toString(), entryId).getToolbar());
		}
		//only start transaction if necessary
		List replies = new ArrayList((List)model.get(WebKeys.FOLDER_ENTRY_DESCENDANTS));
		if (replies != null)  {
			replies.add(entry);
			for (int i=0; i<replies.size(); i++) {
				FolderEntry reply = (FolderEntry)replies.get(i);
				//if any reply is not seen, add it to list - try to avoid update transaction
				if (!seen.checkIfSeen(reply)) {
					getProfileModule().setSeen(null, replies);
					break;
				}
			}
		} else if (!seen.checkIfSeen(entry)) {
			getProfileModule().setSeen(null, entry);
		}
		Map captionMap = new HashMap();
		Map transitionMap = new HashMap();
		for (int i=0; i<replies.size(); i++) {
			FolderEntry reply = (FolderEntry)replies.get(i);
			Set states = reply.getWorkflowStates();
			for (Iterator iter=states.iterator(); iter.hasNext();) {
				WorkflowState ws = (WorkflowState)iter.next();
				//store the UI caption for each state
				captionMap.put(ws.getTokenId(), WorkflowUtils.getStateCaption(ws.getDefinition(), ws.getState()));
				try {
					//See if user can transition out of this state
					getFolderModule().checkTransitionOutStateAllowed(reply, ws.getTokenId());
					//get all manual transitions
					Map trans = getFolderModule().getManualTransitions(reply, ws.getTokenId());
					transitionMap.put(ws.getTokenId(), trans);
				} catch (AccessControlException ac) {}
					
			}
		}
		model.put(WebKeys.WORKFLOW_CAPTIONS, captionMap);
		model.put(WebKeys.WORKFLOW_TRANSITIONS, transitionMap);
		
		return model;
	}
	
	
}
