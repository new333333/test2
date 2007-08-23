/**
 * The contents of this file are governed by the terms of your license
 * with SiteScape, Inc., which includes disclaimers of warranties and
 * limitations on liability. You may not use this file except in accordance
 * with the terms of that license. See the license for the specific language
 * governing your rights and limitations under the license.
 *
 * Copyright (c) 2007 SiteScape, Inc.
 *
 */
package com.sitescape.team.portlet.forum;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.springframework.web.servlet.ModelAndView;

import com.sitescape.team.context.request.RequestContextHolder;
import com.sitescape.team.domain.Binder;
import com.sitescape.team.domain.Definition;
import com.sitescape.team.domain.Description;
import com.sitescape.team.domain.Folder;
import com.sitescape.team.domain.User;
import com.sitescape.team.domain.Workspace;
import com.sitescape.team.domain.EntityIdentifier.EntityType;
import com.sitescape.team.portletadapter.AdaptedPortletURL;
import com.sitescape.team.util.LongIdUtil;
import com.sitescape.team.util.NLT;
import com.sitescape.team.web.WebKeys;
import com.sitescape.team.web.portlet.SAbstractController;
import com.sitescape.team.web.tree.WorkspaceConfigHelper;
import com.sitescape.team.web.util.BinderHelper;
import com.sitescape.team.web.util.PortletRequestUtils;
/**
 * @author Janet McCann
 *
 */
public class AddFolderController extends SAbstractController {
	public void handleActionRequestAfterValidation(ActionRequest request, ActionResponse response) 
	throws Exception {
		User user = RequestContextHolder.getRequestContext().getUser();
		Map formData = request.getParameterMap();
		Long binderId = new Long(PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_BINDER_ID));				
		if (formData.containsKey("okBtn")) {
			//The form was submitted. Go process it
			Long cfgType = PortletRequestUtils.getRequiredLongParameter(request, "binderConfigId");
			Long newId = getAdminModule().addBinderFromTemplate(cfgType, binderId, 
						PortletRequestUtils.getStringParameter(request, "title", ""), null);
			Binder newBinder = getBinderModule().getBinder(newId);
			
			//Now process the rest of the form
			if (newBinder != null) {
				//See if there are any team members specified
				if (PortletRequestUtils.getStringParameter(request, "inheritFromParent", "").equals("no")) {
					//Save the inheritance state
					getBinderModule().setTeamMembershipInherited(newId, false);
				} else {
					getBinderModule().setTeamMembershipInherited(newId, true);
				}
				if (!newBinder.isTeamMembershipInherited()) {
					Set memberIds = new HashSet();
					if (formData.containsKey("users")) memberIds.addAll(LongIdUtil.getIdsAsLongSet(request.getParameterValues("users")));
					if (formData.containsKey("groups")) memberIds.addAll(LongIdUtil.getIdsAsLongSet(request.getParameterValues("groups")));
					//Save the team members 
					getBinderModule().setTeamMembers(newId, memberIds);
				}
				
				//See if there are any folders to be created
				Iterator itFormData = formData.entrySet().iterator();
				while (itFormData.hasNext()) {
					Map.Entry me = (Map.Entry) itFormData.next();
					if (me.getKey().toString().startsWith("folderConfigId_")) {
						String configId = me.getKey().toString().substring(15);
						getAdminModule().addBinderFromTemplate(Long.valueOf(configId), newId, "", null);
					}
				}
				
				//Announce this new workspace?
				if (formData.containsKey("announce")) {
					String messageBody = "<a href=\"";
					AdaptedPortletURL adapterUrl = new AdaptedPortletURL(request, "ss_forum", true);
					adapterUrl.setParameter(WebKeys.ACTION, WebKeys.ACTION_VIEW_PERMALINK);
					adapterUrl.setParameter(WebKeys.URL_BINDER_ID, newId.toString());
					adapterUrl.setParameter(WebKeys.URL_ENTITY_TYPE, newBinder.getEntityType().toString());
					messageBody += adapterUrl.toString();
					messageBody += "\">" + newBinder.getTitle() + "</a><br/><br/>";
					String announcementText = PortletRequestUtils.getStringParameter(request, "announcementText", "");
					messageBody += announcementText;
					Set teamMemberIds = newBinder.getTeamMemberIds();
					if (!teamMemberIds.isEmpty()) {
						Map status = getAdminModule().sendMail(teamMemberIds, null, 
								NLT.get("binder.announcement", new Object[] {user.getTitle(), newBinder.getTitle()}), 
								new Description(messageBody, Description.FORMAT_HTML), 
								null, false);
					}
				}
			}
			
			setupViewBinder(response, newId);
			
		} else if (formData.containsKey("cancelBtn") || formData.containsKey("closeBtn")) {
			setupViewBinder(response, binderId);
		} else {
			response.setRenderParameters(formData);
		}
			
	}
	public ModelAndView handleRenderRequestInternal(RenderRequest request, 
			RenderResponse response) throws Exception {
		
		User user = RequestContextHolder.getRequestContext().getUser();
		Map model = new HashMap();
		Long binderId = new Long(PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_BINDER_ID));				
		String operation = PortletRequestUtils.getStringParameter(request, WebKeys.URL_OPERATION, "");
		Binder binder = getBinderModule().getBinder(binderId);
		model.put(WebKeys.BINDER, binder); 
		model.put(WebKeys.OPERATION, operation);
		model.put(WebKeys.USER_PRINCIPAL, user);

		//Build the navigation beans
		BinderHelper.buildNavigationLinkBeans(this, binder, model, new WorkspaceConfigHelper());
		model.put(WebKeys.DEFINITION_ENTRY, binder);
		model.put(WebKeys.ENTRY, binder);

		//Set the default the team members to the current user
		Set memberIds = new HashSet();
		memberIds.add(user.getId());
		model.put(WebKeys.USERS, getProfileModule().getUsers(memberIds));

		if (operation.equals(WebKeys.OPERATION_ADD_SUB_FOLDER)) {
			List result = getAdminModule().getTemplates(Definition.FOLDER_VIEW);
			if (result.isEmpty()) {
				result.add(getAdminModule().addDefaultTemplate(Definition.FOLDER_VIEW));
			}
			model.put(WebKeys.BINDER_CONFIGS, result);
		} else if (operation.equals(WebKeys.OPERATION_ADD_FOLDER)) {
			List result = getAdminModule().getTemplates(Definition.FOLDER_VIEW);
			if (result.isEmpty()) {
				result.add(getAdminModule().addDefaultTemplate(Definition.FOLDER_VIEW));
			}
			model.put(WebKeys.BINDER_CONFIGS, result);
		} else if (operation.equals(WebKeys.OPERATION_ADD_WORKSPACE)) {
			List result = getAdminModule().getTemplates(Definition.WORKSPACE_VIEW);
			if (result.isEmpty()) {
				result.add(getAdminModule().addDefaultTemplate(Definition.WORKSPACE_VIEW));	
			}
			model.put(WebKeys.BINDER_CONFIGS, result);
			
			//Get the list of folder types
			result = getAdminModule().getTemplates(Definition.FOLDER_VIEW);
			if (result.isEmpty()) {
				result.add(getAdminModule().addDefaultTemplate(Definition.FOLDER_VIEW));
			}
			model.put(WebKeys.FOLDER_CONFIGS, result);
		} else if (operation.equals(WebKeys.OPERATION_ADD_TEAM_WORKSPACE)) {
			List result = getAdminModule().getTemplates(Definition.FOLDER_VIEW);
			if (result.isEmpty()) {
				result.add(getAdminModule().addDefaultTemplate(Definition.FOLDER_VIEW));
			}
			model.put(WebKeys.FOLDER_CONFIGS, result);
			model.put("binderConfigId", PortletRequestUtils.getStringParameter(request, WebKeys.URL_BINDER_CONFIG_ID, ""));
		}
	
		return new ModelAndView(WebKeys.VIEW_ADD_BINDER, model);
	}

	protected void setupViewBinder(ActionResponse response, Long binderId) {
		Binder binder = getBinderModule().getBinder(binderId);
		response.setRenderParameter(WebKeys.URL_BINDER_ID, binder.getId().toString());		
		response.setRenderParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_RELOAD_LISTING);
		if (binder.getEntityType().name().equals(EntityType.folder.name())) {
			response.setRenderParameter(WebKeys.ACTION, WebKeys.ACTION_VIEW_FOLDER_LISTING);
		} else {
			response.setRenderParameter(WebKeys.ACTION, WebKeys.ACTION_VIEW_WS_LISTING);
		}
	}
}


