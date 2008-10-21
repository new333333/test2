/**
 * The contents of this file are subject to the Common Public Attribution License Version 1.0 (the "CPAL");
 * you may not use this file except in compliance with the CPAL. You may obtain a copy of the CPAL at
 * http://www.opensource.org/licenses/cpal_1.0. The CPAL is based on the Mozilla Public License Version 1.1
 * but Sections 14 and 15 have been added to cover use of software over a computer network and provide for
 * limited attribution for the Original Developer. In addition, Exhibit A has been modified to be
 * consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT WARRANTY OF ANY KIND,
 * either express or implied. See the CPAL for the specific language governing rights and limitations
 * under the CPAL.
 * 
 * The Original Code is ICEcore. The Original Developer is SiteScape, Inc. All portions of the code
 * written by SiteScape, Inc. are Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * 
 * 
 * Attribution Information
 * Attribution Copyright Notice: Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by ICEcore]
 * Attribution URL: [www.icecore.com]
 * Graphic Image as provided in the Covered Code [web/docroot/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are defined in the CPAL as a
 * work which combines Covered Code or portions thereof with code not governed by the terms of the CPAL.
 * 
 * 
 * SITESCAPE and the SiteScape logo are registered trademarks and ICEcore and the ICEcore logos
 * are trademarks of SiteScape, Inc.
 */
package com.sitescape.team.portlet.forum;

import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.springframework.web.portlet.ModelAndView;

import com.sitescape.team.ObjectKeys;
import com.sitescape.team.comparator.PrincipalComparator;
import com.sitescape.team.context.request.RequestContextHolder;
import com.sitescape.team.domain.Binder;
import com.sitescape.team.domain.Definition;
import com.sitescape.team.domain.Description;
import com.sitescape.team.domain.Folder;
import com.sitescape.team.domain.TemplateBinder;
import com.sitescape.team.domain.User;
import com.sitescape.team.domain.Workspace;
import com.sitescape.team.module.binder.BinderModule.BinderOperation;
import com.sitescape.team.module.shared.MapInputData;
import com.sitescape.team.util.LongIdUtil;
import com.sitescape.team.util.NLT;
import com.sitescape.team.web.WebKeys;
import com.sitescape.team.web.portlet.SAbstractController;
import com.sitescape.team.web.tree.WorkspaceAddWorkspaceHelper;
import com.sitescape.team.web.util.BinderHelper;
import com.sitescape.team.web.util.PermaLinkUtil;
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
			Long newId = getTemplateModule().addBinder(cfgType, binderId, 
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
						getTemplateModule().addBinder(Long.valueOf(configId), newId, "", null);
					}
				}
				
				//Announce this new workspace?
				if (formData.containsKey("announce")) {
					String messageBody = "<a href=\"";
					String permaLink = PermaLinkUtil.getPermalink(newBinder);
					messageBody += permaLink;
					messageBody += "\">" + newBinder.getTitle() + "</a><br/><br/>";
					String announcementText = PortletRequestUtils.getStringParameter(request, "announcementText", "");
					messageBody += announcementText;
					Set teamMemberIds = newBinder.getTeamMemberIds();
					if (!teamMemberIds.isEmpty()) {
						Map status = getAdminModule().sendMail(teamMemberIds, null, null,
								NLT.get("binder.announcement", new Object[] {user.getTitle(), newBinder.getTitle()}), 
								new Description(messageBody, Description.FORMAT_HTML));
					}
				}
			}
			
			setupReloadOpener(response, newId);
			
		} else if (formData.containsKey("addBtn")) {
			//This is the short form
			String templateName = PortletRequestUtils.getRequiredStringParameter(request, WebKeys.URL_TEMPLATE_NAME);				
			String title = PortletRequestUtils.getStringParameter(request, "title", "");
			TemplateBinder binderTemplate = getTemplateModule().getTemplateByName(templateName);
			Long newBinderId = null;
			if (binderTemplate != null) {
				newBinderId = getTemplateModule().addBinder(binderTemplate.getId(), 
						binderId, title, null);
				Binder newBinder = null;
				if (newBinderId != null) newBinder = getBinderModule().getBinder(newBinderId);
				
				if (newBinder != null) {
					//Inherit team members
					getBinderModule().setTeamMembershipInherited(newBinderId, true);
				}
			}
			if (newBinderId != null) {
				setupReloadOpener(response, newBinderId);
			} else {
				response.setRenderParameters(formData);
			}
			
		} else if (formData.containsKey("cancelBtn") || formData.containsKey("closeBtn")) {
			setupCloseWindow(response);
		} else {
			response.setRenderParameters(formData);
		}
			
	}
	public ModelAndView handleRenderRequestInternal(RenderRequest request, 
			RenderResponse response) throws Exception {
		
		User user = RequestContextHolder.getRequestContext().getUser();
		Map model = new HashMap();
		Long binderId = new Long(PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_BINDER_ID));				
		String templateName = PortletRequestUtils.getStringParameter(request, WebKeys.URL_TEMPLATE_NAME, "");				
		String operation = PortletRequestUtils.getStringParameter(request, WebKeys.URL_OPERATION, "");
		Binder binder = getBinderModule().getBinder(binderId);
		model.put(WebKeys.BINDER, binder); 
		model.put(WebKeys.BINDER_TEMPLATE_NAME, templateName);
		model.put(WebKeys.OPERATION, operation);
		model.put(WebKeys.USER_PRINCIPAL, user);

		//Build the navigation beans
		BinderHelper.buildNavigationLinkBeans(this, binder, model, new WorkspaceAddWorkspaceHelper());
		model.put(WebKeys.DEFINITION_ENTRY, binder);
		model.put(WebKeys.ENTRY, binder);

		//Set the default the team members to the current user
        Comparator c = new PrincipalComparator(user.getLocale());
       	TreeSet<User> memberIds = new TreeSet(c);
		memberIds.add(user);
		model.put(WebKeys.USERS, memberIds);
		
		Map accessControlMap = BinderHelper.getAccessControlMapBean(model);
		if (binder instanceof Workspace) 
			accessControlMap.put("createWorkspace", getBinderModule().testAccess((Workspace)binder, 
						BinderOperation.addWorkspace));

		if (operation.equals(WebKeys.OPERATION_ADD_SUB_FOLDER)) {
			List result = getTemplateModule().getTemplates(Definition.FOLDER_VIEW);
			if (result.isEmpty()) {
				result.add(getTemplateModule().addDefaultTemplate(Definition.FOLDER_VIEW));
			}
			model.put(WebKeys.BINDER_CONFIGS, result);
		} else if (operation.equals(WebKeys.OPERATION_ADD_FOLDER)) {
			List result = getTemplateModule().getTemplates(Definition.FOLDER_VIEW);
			if (result.isEmpty()) {
				result.add(getTemplateModule().addDefaultTemplate(Definition.FOLDER_VIEW));
			}
			model.put(WebKeys.BINDER_CONFIGS, result);
		} else if (operation.equals(WebKeys.OPERATION_ADD_WORKSPACE)) {
			List result = getTemplateModule().getTemplates(Definition.WORKSPACE_VIEW);
			if (result.isEmpty()) {
				result.add(getTemplateModule().addDefaultTemplate(Definition.WORKSPACE_VIEW));	
			}
			model.put(WebKeys.BINDER_CONFIGS, result);
			
			//Get the list of folder types
			result = getTemplateModule().getTemplates(Definition.FOLDER_VIEW);
			if (result.isEmpty()) {
				result.add(getTemplateModule().addDefaultTemplate(Definition.FOLDER_VIEW));
			}
			model.put(WebKeys.FOLDER_CONFIGS, result);
		} else if (operation.equals(WebKeys.OPERATION_ADD_TEAM_WORKSPACE)) {
			List result = getTemplateModule().getTemplates(Definition.FOLDER_VIEW);
			if (result.isEmpty()) {
				result.add(getTemplateModule().addDefaultTemplate(Definition.FOLDER_VIEW));
			}
			model.put(WebKeys.FOLDER_CONFIGS, result);
			model.put("binderConfigId", PortletRequestUtils.getStringParameter(request, WebKeys.URL_BINDER_CONFIG_ID, ""));
		}
	
		return new ModelAndView(WebKeys.VIEW_ADD_BINDER, model);
	}

	private void setupReloadOpener(ActionResponse response, Long binderId) {
		//return to view entry
		response.setRenderParameter(WebKeys.ACTION, WebKeys.ACTION_RELOAD_OPENER);
		response.setRenderParameter(WebKeys.URL_BINDER_ID, binderId.toString());
	}
	private void setupCloseWindow(ActionResponse response) {
		//return to view entry
		response.setRenderParameter(WebKeys.ACTION, WebKeys.ACTION_CLOSE_WINDOW);
	}
}


