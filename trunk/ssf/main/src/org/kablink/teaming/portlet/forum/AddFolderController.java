/**
 * Copyright (c) 1998-2009 Novell, Inc. and its licensors. All rights reserved.
 * 
 * This work is governed by the Common Public Attribution License Version 1.0 (the
 * "CPAL"); you may not use this file except in compliance with the CPAL. You may
 * obtain a copy of the CPAL at http://www.opensource.org/licenses/cpal_1.0. The
 * CPAL is based on the Mozilla Public License Version 1.1 but Sections 14 and 15
 * have been added to cover use of software over a computer network and provide
 * for limited attribution for the Original Developer. In addition, Exhibit A has
 * been modified to be consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT
 * WARRANTY OF ANY KIND, either express or implied. See the CPAL for the specific
 * language governing rights and limitations under the CPAL.
 * 
 * The Original Code is ICEcore, now called Kablink. The Original Developer is
 * Novell, Inc. All portions of the code written by Novell, Inc. are Copyright
 * (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by Kablink]
 * Attribution URL: [www.kablink.org]
 * Graphic Image as provided in the Covered Code
 * [ssf/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are
 * defined in the CPAL as a work which combines Covered Code or portions thereof
 * with code not governed by the terms of the CPAL.
 * 
 * NOVELL and the Novell logo are registered trademarks and Kablink and the
 * Kablink logos are trademarks of Novell, Inc.
 */
package org.kablink.teaming.portlet.forum;

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

import org.dom4j.Element;
import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.comparator.PrincipalComparator;
import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.Definition;
import org.kablink.teaming.domain.Description;
import org.kablink.teaming.domain.Folder;
import org.kablink.teaming.domain.TemplateBinder;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.domain.Workspace;
import org.kablink.teaming.module.binder.BinderModule.BinderOperation;
import org.kablink.teaming.module.shared.MapInputData;
import org.kablink.teaming.util.LongIdUtil;
import org.kablink.teaming.util.NLT;
import org.kablink.teaming.util.Utils;
import org.kablink.teaming.web.WebKeys;
import org.kablink.teaming.web.portlet.SAbstractController;
import org.kablink.teaming.web.tree.WorkspaceAddWorkspaceHelper;
import org.kablink.teaming.web.util.BinderHelper;
import org.kablink.teaming.web.util.PermaLinkUtil;
import org.kablink.teaming.web.util.PortletRequestUtils;
import org.kablink.teaming.web.util.WebHelper;
import org.springframework.web.portlet.ModelAndView;

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
		Boolean isShortForm = PortletRequestUtils.getBooleanParameter(request, "shortForm", false);				
		String operation = PortletRequestUtils.getStringParameter(request, WebKeys.URL_OPERATION, "");				
		if (formData.containsKey("okBtn") && WebHelper.isMethodPost(request)) {
			//The form was submitted. Go process it
			Long cfgType = PortletRequestUtils.getRequiredLongParameter(request, "binderConfigId");
			Long newId = getTemplateModule().addBinder(cfgType, binderId, 
						PortletRequestUtils.getStringParameter(request, "title", ""), null).getId();
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
						Map status = getAdminModule().sendMail(teamMemberIds, null, null, null, null,
								NLT.get("binder.announcement", new Object[] {Utils.getUserTitle(user), newBinder.getTitle()}), 
								new Description(messageBody, Description.FORMAT_HTML));
					}
				}
			}
			
			if (isShortForm || operation.equals("add_team_workspace")) {
				setupReloadBinder(response, newId);
			} else {
				setupReloadOpener(response, newId);
			}
			
		} else if (formData.containsKey("addBtn") && WebHelper.isMethodPost(request)) {
			//This is the short form
			String templateName = PortletRequestUtils.getRequiredStringParameter(request, WebKeys.URL_TEMPLATE_NAME);				
			String title = PortletRequestUtils.getStringParameter(request, "title", "");
			TemplateBinder binderTemplate = getTemplateModule().getTemplateByName(templateName);
			Long newBinderId = null;
			if (binderTemplate != null) {
				newBinderId = getTemplateModule().addBinder(binderTemplate.getId(), 
						binderId, title, null).getId();
				Binder newBinder = null;
				if (newBinderId != null) newBinder = getBinderModule().getBinder(newBinderId);
				
				if (newBinder != null) {
					//Inherit team members
					getBinderModule().setTeamMembershipInherited(newBinderId, true);
				}
			}
			if (newBinderId != null) {
				if (isShortForm) {
					setupReloadBinder(response, newBinderId);
				} else {
					setupReloadOpener(response, newBinderId);
				}
			} else {
				response.setRenderParameters(formData);
			}
			
		} else if (formData.containsKey("cancelBtn") || formData.containsKey("closeBtn")) {
			setupCloseWindow(response);
		} else {
			response.setRenderParameters(formData);
		}
			
	}
	public ModelAndView handleRenderRequestAfterValidation(RenderRequest request, 
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

		Element familyProperty = (Element) binder.getDefaultViewDef().getDefinition().getRootElement().selectSingleNode("//properties/property[@name='family']");
		if (familyProperty != null) {
			String family = familyProperty.attributeValue("value", "");
			model.put(WebKeys.DEFINITION_FAMILY, family);
		}

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

	private void setupReloadBinder(ActionResponse response, Long binderId) {
		//return to view entry
		response.setRenderParameter(WebKeys.ACTION, WebKeys.ACTION_RELOAD_BINDER);
		response.setRenderParameter(WebKeys.URL_BINDER_ID, binderId.toString());
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


