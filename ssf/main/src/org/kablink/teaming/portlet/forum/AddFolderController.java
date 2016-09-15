/**
 * Copyright (c) 1998-2014 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2014 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2014 Novell, Inc. All Rights Reserved.
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
import org.kablink.teaming.comparator.PrincipalComparator;
import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.Definition;
import org.kablink.teaming.domain.Description;
import org.kablink.teaming.domain.EntityIdentifier;
import org.kablink.teaming.domain.Group;
import org.kablink.teaming.domain.TemplateBinder;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.domain.Workspace;
import org.kablink.teaming.module.binder.BinderModule.BinderOperation;
import org.kablink.teaming.module.binder.impl.WriteEntryDataException;
import org.kablink.teaming.module.file.WriteFilesException;
import org.kablink.teaming.security.AccessControlException;
import org.kablink.teaming.security.function.WorkAreaOperation;
import org.kablink.teaming.security.runwith.RunWithCallback;
import org.kablink.teaming.security.runwith.RunWithTemplate;
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
 * ?
 * 
 * @author Janet McCann
 */
@SuppressWarnings({"unchecked", "unused"})
public class AddFolderController extends SAbstractController {
	@Override
	public void handleActionRequestAfterValidation(ActionRequest request, ActionResponse response) 
	throws Exception {
		User user = RequestContextHolder.getRequestContext().getUser();
		Map formData = request.getParameterMap();
		Long binderId = new Long(PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_BINDER_ID));	
		Boolean isShortForm = PortletRequestUtils.getBooleanParameter(request, "shortForm", false);				
		String operation = PortletRequestUtils.getStringParameter(request, WebKeys.URL_OPERATION, "");				
		String addEntryFromIFrame = PortletRequestUtils.getStringParameter(request, WebKeys.URL_ADD_DEFAULT_ENTRY_FROM_INFRAME, "");
		String namespace = PortletRequestUtils.getStringParameter(request, WebKeys.URL_NAMESPACE, "");
		if (formData.containsKey("okBtn") && WebHelper.isMethodPost(request)) {
			//The form was submitted. Go process it
			Long cfgType = PortletRequestUtils.getRequiredLongParameter(request, "binderConfigId");
			Long newId = null;
			boolean createTestFolders = BinderHelper.BINDER_DEBUG_ENABLED;
			if (createTestFolders) {
				newId = createDebugFolders(request, response, cfgType, binderId);						
				createTestFolders = (null != newId);
			}
			if (!createTestFolders){
				newId = getTemplateModule().addBinder(cfgType, binderId, 
					PortletRequestUtils.getStringParameter(request, "title", "", false), null).getId();
			}
			Binder newBinder = getBinderModule().getBinder(newId);
			
			//Now process the rest of the form
			if (newBinder != null) {
				//See if there are any team members specified
				final boolean inheritTeamMembership;
				boolean allowExternalUsers = false;
				if (PortletRequestUtils.getStringParameter(request, "inheritFromParent", "").equals("no")) {
					//Save the inheritance state
					inheritTeamMembership = false;
					
					if ( PortletRequestUtils.getBooleanParameter( request, "allowExternalUsers", false ) )
						allowExternalUsers = true;
				}
				else {
					inheritTeamMembership = true;
				}
				
				// If we are still here, it means that the user had enough right to create a new binder.
				// The following method for initializing team membership inheritance for the newly
				// created binder normally requires "binder administration" right. Failing the user on
				// this method who has the right to create a new binder is not our intention. 
				// So, we temporarily grant the user with the "binder administration" right in case the
				// user doesn't have it, so that we can successfully execute the following method without
				// unintended side effect.
				final Long newIdCopy = newId;
				RunWithTemplate.runWith(new RunWithCallback() {
					@Override
					public Object runWith() {
						getBinderModule().setTeamMembershipInherited(newIdCopy, inheritTeamMembership);			
						return null;
					}
				}, new WorkAreaOperation[]{WorkAreaOperation.BINDER_ADMINISTRATION, WorkAreaOperation.CHANGE_ACCESS_CONTROL}, null);
				
				if (!newBinder.isTeamMembershipInherited()) {
					final Set memberIds = new HashSet();
					if (formData.containsKey("users")) memberIds.addAll(LongIdUtil.getIdsAsLongSet(request.getParameterValues("users")));
					if (formData.containsKey("groups")) memberIds.addAll(LongIdUtil.getIdsAsLongSet(request.getParameterValues("groups")));
					
					//Save the team members 
					RunWithTemplate.runWith(new RunWithCallback() {
						@Override
						public Object runWith() {
							getBinderModule().setTeamMembers(newIdCopy, memberIds);
							return null;
						}
					}, new WorkAreaOperation[]{WorkAreaOperation.BINDER_ADMINISTRATION, WorkAreaOperation.CHANGE_ACCESS_CONTROL}, null);

					// Can the team contain external users/groups?
					if ( allowExternalUsers )
					{
						Long groupId;
						
						// Yes
						// Get the id of the team group associated with the binder
						groupId = newBinder.getTeamGroupId();
						
						if ( groupId != null )
						{
							try
							{
								getProfileModule().markGroupAsExternal( groupId );
							}
							catch ( Exception ex )
							{
								logger.error( "Error marking team group as external: " + newBinder.getTitle(), ex );
							}
						}
					}
					else{
						Long groupId;
						
						// Yes
						// Get the id of the team group associated with the binder
						groupId = newBinder.getTeamGroupId();
						
						if ( groupId != null )
						{
							try
							{
								getProfileModule().markGroupAsInternal( groupId );
							}
							catch ( Exception ex )
							{
								logger.error( "Error marking team group as external: " + newBinder.getTitle(), ex );
							}
						}						
					}
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
					String announcementText = PortletRequestUtils.getStringParameter(request, "announcementText", "", false);
					messageBody += announcementText;
					Set emailAddress = new HashSet();
					//See if this user wants to be BCC'd on all mail sent out
					String bccEmailAddress = user.getBccEmailAddress();
					if (bccEmailAddress != null && !bccEmailAddress.equals("")) {
						if (!emailAddress.contains(bccEmailAddress.trim())) {
							//Add the user's chosen bcc email address
							emailAddress.add(bccEmailAddress.trim());
						}
					}
					Set teamMemberIds = getBinderModule().getTeamMemberIds( newBinder );
					if (!teamMemberIds.isEmpty()) {
						Map status = getAdminModule().sendMail(teamMemberIds, null, emailAddress, null, null,
								NLT.get("binder.announcement", new Object[] {Utils.getUserTitle(user), newBinder.getTitle()}), 
								new Description(messageBody, Description.FORMAT_HTML));
					}
				}
			}
			
			if (!addEntryFromIFrame.equals("")) {
				setupReloadBinder(response, binderId);
				response.setRenderParameter(WebKeys.RELOAD_URL_FORCED, "");
				setupReloadOpener(response, binderId);
				response.setRenderParameter(WebKeys.NAMESPACE, namespace);
			}
			else {
				if (isShortForm) {
					setupReloadBinder(response, newId);
				} else {
					setupReloadOpener(response, newId);
				}
				//setupReloadBinder(response, newId);
			}
			
		} else if (formData.containsKey("addBtn") && WebHelper.isMethodPost(request)) {
			//This is the short form
			String templateName = PortletRequestUtils.getRequiredStringParameter(request, WebKeys.URL_TEMPLATE_NAME);				
			String title = PortletRequestUtils.getStringParameter(request, "title", "", false);
			TemplateBinder binderTemplate = getTemplateModule().getTemplateByName(templateName);
			Long newBinderId = null;
			if (binderTemplate != null) {
				newBinderId = getTemplateModule().addBinder(binderTemplate.getId(), 
						binderId, title, null).getId();
				Binder newBinder = null;
				if (newBinderId != null) newBinder = getBinderModule().getBinder(newBinderId);
				
				//Note, by default team membership is inherited. 
				//This is initialized as such during the creation of the folder
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

	@Override
	public ModelAndView handleRenderRequestAfterValidation(RenderRequest request, 
			RenderResponse response) throws Exception {
		
		User user = RequestContextHolder.getRequestContext().getUser();
		Long zoneId = RequestContextHolder.getRequestContext().getZoneId();
		Map model = new HashMap();
		Long binderId = new Long(PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_BINDER_ID));				
		String templateName = PortletRequestUtils.getStringParameter(request, WebKeys.URL_TEMPLATE_NAME, "");				
		String operation = PortletRequestUtils.getStringParameter(request, WebKeys.URL_OPERATION, "");
		Binder binder = getBinderModule().getBinder(binderId);
		model.put(WebKeys.BINDER, binder);
		model.put( WebKeys.BINDER_TEAM_MEMBER_IDS, getBinderModule().getTeamMemberIds( binder ) );
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
			result.addAll(getTemplateModule().getTemplates(Definition.FOLDER_VIEW, binder, true));
			if (result.isEmpty()) {
				result.add(getTemplateModule().addDefaultTemplate(Definition.FOLDER_VIEW));
			}
			model.put(WebKeys.BINDER_CONFIGS, result);
		} else if (operation.equals(WebKeys.OPERATION_ADD_FOLDER)) {
			List result = getTemplateModule().getTemplates(Definition.FOLDER_VIEW);
			result.addAll(getTemplateModule().getTemplates(Definition.FOLDER_VIEW, binder, true));
			if (result.isEmpty()) {
				result.add(getTemplateModule().addDefaultTemplate(Definition.FOLDER_VIEW));
			}
			model.put(WebKeys.BINDER_CONFIGS, result);
		} else if (operation.equals(WebKeys.OPERATION_ADD_WORKSPACE)) {
			List result = getTemplateModule().getTemplates(Definition.WORKSPACE_VIEW);
			result.addAll(getTemplateModule().getTemplates(Definition.WORKSPACE_VIEW, binder, true));
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
			result.addAll(getTemplateModule().getTemplates(Definition.FOLDER_VIEW, binder, true));
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
	
    /*
     * The following method is used to aid in testing folders.  It
     * allows one to easily generate an arbitrary number of folders in
     * the given binder.
     * 
     * Conditions:
     * 1. binders.debug.enabled=true in some ssf*.properties file.
     * 2. An entry titled 'create.binders.<nnn>' is being created where
     *    '<nnn>' is some integer value.
     * 
     * If these conditions are met, <nnn> folders named folder.1,
     * folder.2, ... folder.nnn will be created with the ID of the last
     * folder created being returned.  If these conditions are not met,
     * null is returned.
     */
	private Long createDebugFolders(ActionRequest request, ActionResponse response, Long cfgType, Long binderId) throws AccessControlException, WriteFilesException, WriteEntryDataException {
		// Is binder debugging enabled?
		Long reply = null; 
		if (BinderHelper.BINDER_DEBUG_ENABLED) {
			// Yes!  Does the folder being created have the magic name?
			String title = PortletRequestUtils.getStringParameter(request, "title", "");
			if (null == title) {
				title = "";
			}
			if (title.startsWith(BinderHelper.BINDER_MAGIC_TITLE)) {
				// Yes!  Are we being asked to create 1 or more folders?
				String countS = title.substring(BinderHelper.BINDER_MAGIC_TITLE.length());
				int count;
				try                  {count = Integer.parseInt(countS);}
				catch (Exception ex) {count = 0;}
				if (0 < count) {
					// Yes!  Create them.
					logger.info("AddFolderController.createDebugFolders():  Creating " + count + " folders.");
					for (int i = 1; i <= count; i += 1) {
						logger.info("...creating folder " + i + " of " + count);
						reply = getTemplateModule().addBinder(cfgType, binderId, 
							("folder." + i), null).getId();
					}
				}
			}
		}
		
		// If we get here, reply is null or refers to the ID of the
		// last folder created.  Return it.
		return reply;
	}	
}
