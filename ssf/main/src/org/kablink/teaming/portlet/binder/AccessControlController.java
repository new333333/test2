/**
 * Copyright (c) 1998-2015 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2015 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2015 Novell, Inc. All Rights Reserved.
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
package org.kablink.teaming.portlet.binder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletURL;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.dao.util.ShareItemSelectSpec;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.DefinableEntity;
import org.kablink.teaming.domain.EntityIdentifier;
import org.kablink.teaming.domain.Entry;
import org.kablink.teaming.domain.Folder;
import org.kablink.teaming.domain.FolderEntry;
import org.kablink.teaming.domain.Principal;
import org.kablink.teaming.domain.ResourceDriverConfig;
import org.kablink.teaming.domain.ShareItem;
import org.kablink.teaming.domain.TemplateBinder;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.domain.ZoneConfig;
import org.kablink.teaming.fi.auth.AuthSupport;
import org.kablink.teaming.fi.connection.ResourceDriver;
import org.kablink.teaming.fi.connection.ResourceDriverManager;
import org.kablink.teaming.fi.connection.ResourceSession;
import org.kablink.teaming.fi.connection.acl.AclResourceDriver;
import org.kablink.teaming.fi.connection.acl.AclResourceSession;
import org.kablink.teaming.module.admin.AdminModule.AdminOperation;
import org.kablink.teaming.module.folder.FolderModule;
import org.kablink.teaming.module.folder.FolderModule.FolderOperation;
import org.kablink.teaming.module.shared.AccessUtils;
import org.kablink.teaming.module.sharing.SharingModule;
import org.kablink.teaming.security.AccessControlException;
import org.kablink.teaming.security.function.Function;
import org.kablink.teaming.security.function.WorkArea;
import org.kablink.teaming.security.function.WorkAreaFunctionMembership;
import org.kablink.teaming.security.function.WorkAreaOperation;
import org.kablink.teaming.util.AllModulesInjected;
import org.kablink.teaming.util.NLT;
import org.kablink.teaming.util.SPropsUtil;
import org.kablink.teaming.util.SimpleProfiler;
import org.kablink.teaming.util.SpringContextUtil;
import org.kablink.teaming.util.Utils;
import org.kablink.teaming.web.WebKeys;
import org.kablink.teaming.web.util.BinderHelper;
import org.kablink.teaming.web.util.PortletRequestUtils;
import org.kablink.teaming.web.util.WebHelper;
import org.kablink.teaming.web.util.WorkAreaHelper;

import org.springframework.web.portlet.ModelAndView;

/**
 * This controller/jsp is used by administration/ConfigureAccessController
 * Keep in sync
 * 
 * @author Peter Hurley
 */
@SuppressWarnings({"unchecked", "unused"})
public class AccessControlController extends AbstractBinderController {
	@Override
	public void handleActionRequestAfterValidation(ActionRequest request, ActionResponse response) 
	throws Exception {
        User user = RequestContextHolder.getRequestContext().getUser();
		Map formData = request.getParameterMap();
		response.setRenderParameters(request.getParameterMap());
		String operation = PortletRequestUtils.getStringParameter(request, WebKeys.URL_OPERATION, "");	

		//navigation links still use binderId
		Long workAreaId = PortletRequestUtils.getLongParameter(request, WebKeys.URL_BINDER_ID);
		if (workAreaId == null) workAreaId = new Long(PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_WORKAREA_ID));				
		WorkArea workArea = null;
		request.setAttribute("roleId", "");
		String type = PortletRequestUtils.getStringParameter(request, WebKeys.URL_WORKAREA_TYPE, "");	
		if (ZoneConfig.WORKAREA_TYPE.equals(type)) {
			workArea = getZoneModule().getZoneConfig(workAreaId);
		} else if (EntityIdentifier.EntityType.folderEntry.name().equals(type)) {
			FolderEntry entry = getFolderModule().getEntry(null, workAreaId);
			workArea = entry;
		} else {
			workArea = getBinderModule().getBinder(workAreaId);
		}
		response.setRenderParameter(WebKeys.URL_WORKAREA_ID, workArea.getWorkAreaId().toString());
		response.setRenderParameter(WebKeys.URL_WORKAREA_TYPE, workArea.getWorkAreaType());
		//The form is only used in Vibe. But we allow it for informational purposes only in Filr (i.e., no changes allowed).
		if (Utils.checkIfVibe() || Utils.checkIfKablink() || SPropsUtil.getBoolean("keepFilrRolesAndRightsInVibe", false)) {
			//Note: setting "keepFilrRolesAndRightsInVibe" to true in ssf-ext.properties will also allow the Vibe access control form to be 
			//  used to change access settings. But only if you know the access control page URL. Caution, this means that regular users 
			//  could then set various sharing rights on their owned folders
			//See if the form was submitted
			if (formData.containsKey("okBtn") && WebHelper.isMethodPost(request)) {
				if (!(workArea instanceof FolderEntry) || ((FolderEntry)workArea).isTop()) {
					Map functionMemberships = new HashMap();
					getAccessResults(request, functionMemberships);
					if (workArea instanceof Entry) {
						Boolean includeFolderAcl = PortletRequestUtils.getBooleanParameter(request, "includeFolderAcl", false);
						if (!((Entry)workArea).hasEntryAcl() && !formData.containsKey("includeFolderAcl")) {
							//When transitioning from no ACL to having an ACL, start with including the folder ACL
							includeFolderAcl = Boolean.TRUE;
						}
						getAdminModule().setEntryHasAcl(workArea, Boolean.TRUE, includeFolderAcl);
					}
					getAdminModule().setWorkAreaFunctionMemberships(workArea, functionMemberships);
					//SimpleProfiler.done(logger);
				}
			} else if (formData.containsKey("delBtn") && WebHelper.isMethodPost(request)) {
				if (!(workArea instanceof FolderEntry) || ((FolderEntry)workArea).isTop()) {
					if (workArea instanceof Entry) {
						getAdminModule().setEntryHasAcl(workArea, Boolean.FALSE, Boolean.TRUE);
						Map functionMemberships = new HashMap();
						getAdminModule().setWorkAreaFunctionMemberships(workArea, functionMemberships);
						//SimpleProfiler.done(logger);
					}
				}
				setupCloseWindow(response);
			} else if (formData.containsKey("inheritanceBtn") && WebHelper.isMethodPost(request)) {
				boolean inherit = PortletRequestUtils.getBooleanParameter(request, "inherit", false);
				getAdminModule().setWorkAreaFunctionMembershipInherited(workArea,inherit);			
			
			} else if (formData.containsKey("aclSelectionBtn") && WebHelper.isMethodPost(request)) {
				if ((workArea instanceof FolderEntry) && ((FolderEntry)workArea).isTop()) {
					String aclType = PortletRequestUtils.getStringParameter(request, "aclSelection", "entry");	
					if (aclType.equals("folder")) {
						getAdminModule().setEntryHasAcl(workArea, Boolean.FALSE, Boolean.TRUE);
						//Clear out the old entry acl
						Map functionMemberships = new HashMap();
						getAdminModule().setWorkAreaFunctionMemberships(workArea, functionMemberships, Boolean.FALSE);
						//SimpleProfiler.done(logger);
					} else if (aclType.equals("entry")) {
						//Set the entry acl
						Boolean includeFolderAcl = PortletRequestUtils.getBooleanParameter(request, "includeFolderAcl", false);
						if (!((FolderEntry)workArea).hasEntryAcl() && !formData.containsKey("includeFolderAcl")) {
							//When transitioning from no ACL to having an ACL, start with including the folder ACL
							includeFolderAcl = Boolean.TRUE;
						}
						Map functionMemberships = new HashMap();
						getAccessResults(request, functionMemberships);
						getAdminModule().setWorkAreaFunctionMemberships(workArea, functionMemberships);
						getAdminModule().setEntryHasAcl(workArea, Boolean.TRUE, includeFolderAcl);
						//SimpleProfiler.done(logger);
					}
				}
			
			} else if (formData.containsKey("revokeBtn") && WebHelper.isMethodPost(request)) {
				Long shareItemId = PortletRequestUtils.getLongParameter(request, WebKeys.SHARE_ITEM_ID);
				if (shareItemId != null) {
					getSharingModule().deleteShareItem(shareItemId);
				}
			
			} else if (formData.containsKey("cancelBtn") || formData.containsKey("closeBtn")) {
				if (workArea instanceof TemplateBinder) {
					response.setRenderParameter(WebKeys.ACTION, WebKeys.ACTION_CONFIGURATION);
					response.setRenderParameter(WebKeys.URL_BINDER_ID, workAreaId.toString());
				} else if (operation.equals(WebKeys.OPERATION_MANAGE_ACCESS_SHARING)){
					response.setRenderParameter(WebKeys.URL_OPERATION, "");
				} else {
					setupCloseWindow(response);
				}
			}
		} else {
			//Always allow the close or cancel button
			if (formData.containsKey("cancelBtn") || formData.containsKey("closeBtn")) {
				if (workArea instanceof TemplateBinder) {
					response.setRenderParameter(WebKeys.ACTION, WebKeys.ACTION_CONFIGURATION);
					response.setRenderParameter(WebKeys.URL_BINDER_ID, workAreaId.toString());
				} else if (operation.equals(WebKeys.OPERATION_MANAGE_ACCESS_SHARING)){
					response.setRenderParameter(WebKeys.URL_OPERATION, "");
				} else {
					setupCloseWindow(response);
				}
			}
		}
	}
	@Override
	public ModelAndView handleRenderRequestAfterValidation(RenderRequest request, 
			RenderResponse response) throws Exception {
        User user = RequestContextHolder.getRequestContext().getUser();
		//navigation links still use binderId
		Long workAreaId = PortletRequestUtils.getLongParameter(request, WebKeys.URL_BINDER_ID);
		if (workAreaId == null) workAreaId = new Long(PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_WORKAREA_ID));				
		String type = PortletRequestUtils.getStringParameter(request, WebKeys.URL_WORKAREA_TYPE);	
		String operation = PortletRequestUtils.getStringParameter(request, WebKeys.URL_OPERATION, "");	
		String operation2 = PortletRequestUtils.getStringParameter(request, WebKeys.URL_OPERATION2, "");	
		WorkArea wArea=null;
		Map model = new HashMap();
		Map formData = request.getParameterMap();
		try {
			if (ZoneConfig.WORKAREA_TYPE.equals(type)) {
				ZoneConfig zone = getZoneModule().getZoneConfig(workAreaId);
				model.put(WebKeys.ACCESS_SUPER_USER, AccessUtils.getZoneSuperUser(zone.getZoneId()));
				wArea = zone;
				model.put(WebKeys.ACCESS_CONTROL_CONFIGURE_ALLOWED, 
						getAdminModule().testAccess(zone, AdminOperation.manageFunctionMembership));
			} else if (EntityIdentifier.EntityType.folderEntry.name().equals(type)) {
				FolderEntry entry = getFolderModule().getEntry(null, workAreaId);
				model.put(WebKeys.ENTRY_HAS_ENTRY_ACL, entry.hasEntryAcl());
				wArea = entry;
				model.put(WebKeys.ACCESS_SUPER_USER, AccessUtils.getZoneSuperUser(entry.getZoneId()));
				Boolean configureAccess = false;
				if (entry.hasEntryAcl()) {
					configureAccess = getAdminModule().testAccess(entry, AdminOperation.manageFunctionMembership);
					if (!configureAccess && entry.isIncludeFolderAcl()) {
						configureAccess = getFolderModule().testAccess(entry.getParentFolder(), FolderOperation.setEntryAcl);
					}
					if (!configureAccess && entry.isIncludeFolderAcl() && entry.getOwnerId().equals(user.getId())) {
						configureAccess = getFolderModule().testAccess(entry.getParentFolder(), FolderOperation.entryOwnerSetAcl);
					}
				} else {
					if (!configureAccess) {
						configureAccess = getAdminModule().testAccess(entry.getParentFolder(), AdminOperation.manageFunctionMembership);
					}
					if (!configureAccess) {
						configureAccess = getFolderModule().testAccess(entry.getParentFolder(), FolderOperation.setEntryAcl);
					}
					if (!configureAccess && entry.getOwnerId().equals(user.getId())) {
						configureAccess = getFolderModule().testAccess(entry.getParentFolder(), FolderOperation.entryOwnerSetAcl);
					}
				}
				model.put(WebKeys.ACCESS_CONTROL_CONFIGURE_ALLOWED, configureAccess);
				model.put(WebKeys.DEFINITION_ENTRY, entry);
				model.put(WebKeys.BINDER, entry.getParentBinder());
				model.put( WebKeys.BINDER_TEAM_MEMBER_IDS, getBinderModule().getTeamMemberIds( entry.getParentBinder() ) );
				
			} else {
				Binder binder = getBinderModule().getBinder(workAreaId);			
				//Build the navigation beans
				BinderHelper.buildNavigationLinkBeans(this, binder, model);
				wArea = binder;
				model.put(WebKeys.BINDER, binder);
				model.put( WebKeys.BINDER_TEAM_MEMBER_IDS, getBinderModule().getTeamMemberIds( binder ) );
				model.put(WebKeys.DEFINITION_ENTRY, binder);
				model.put(WebKeys.ACCESS_SUPER_USER, AccessUtils.getZoneSuperUser(binder.getZoneId()));
				model.put(WebKeys.ACCESS_CONTROL_CONFIGURE_ALLOWED, 
						getAdminModule().testAccess(binder, AdminOperation.manageFunctionMembership));
				
				
				//Gather net folder data if this is a net folder
				if (binder.isAclExternallyControlled() && binder instanceof Folder) {
					model.put(WebKeys.ACCESS_NET_FOLDER_MAP, getFolderModule().getNetFolderAccessData((Folder)binder));
					model.put(WebKeys.URL_OPERATION2, operation2);
					PortletURL url = response.createRenderURL();
					url.setParameter(WebKeys.ACTION, WebKeys.ACTION_ACCESS_CONTROL);
					url.setParameter(WebKeys.URL_WORKAREA_ID, binder.getWorkAreaId().toString());
					url.setParameter(WebKeys.URL_WORKAREA_TYPE, binder.getWorkAreaType());
					url.setParameter(WebKeys.URL_OPERATION2, "debug");
					model.put(WebKeys.ACCESS_NET_FOLDER_URL, url);
				}
			}
		} catch(AccessControlException e) {
			model.put(WebKeys.ACCESS_CONTROL_EXCEPTION, Boolean.TRUE);
			return new ModelAndView(WebKeys.VIEW_ACCESS_CONTROL_NO_MORE, model);
		}
		if (formData.containsKey("okBtn") || formData.containsKey("inheritanceBtn") ||
				formData.containsKey("aclSelectionBtn") || formData.containsKey("delBtn")) {
			//A form was submitted, make sure the user can still perform an access control change
			if (!(Boolean)model.get(WebKeys.ACCESS_CONTROL_CONFIGURE_ALLOWED)) {
				//The user no longer has access, give an appropriat error message'
				return new ModelAndView(WebKeys.VIEW_ACCESS_CONTROL_NO_MORE, model);
			}
		}
		
		setupAccess(this, request, response, wArea, model);
		model.put(WebKeys.ACCESS_ALL_USERS_GROUP, Utils.getAllUsersGroupId());
		model.put(WebKeys.ACCESS_WORKAREA_IS_PERSONAL, Utils.isWorkareaInProfilesTree(wArea));		
		
		//Set up the beans for shared requests
		setupSharedBeans(this, wArea, model);

		if (ObjectKeys.GUEST_USER_INTERNALID.equals(user.getInternalId())) {
			//Cannot do these things as guest
			model.put(WebKeys.ERROR_MESSAGE, NLT.get("errorcode.access.denied"));
			return new ModelAndView(WebKeys.VIEW_ERROR_RETURN, model);
		}
		if (operation.equals(WebKeys.OPERATION_VIEW_ACCESS)) {
			if (operation2.equals("debug")) {
				return new ModelAndView(WebKeys.VIEW_ACCESS_TO_NET_FOLDER, model);
			} else {
				return new ModelAndView(WebKeys.VIEW_ACCESS_TO_BINDER, model);
			}
		} else if (operation.equals(WebKeys.OPERATION_MANAGE_ACCESS_SHARING)) {
			return new ModelAndView(WebKeys.VIEW_ACCESS_CONTROL_SHARING, model);
		} else {
			if (wArea instanceof Entry) {
				return new ModelAndView(WebKeys.VIEW_ACCESS_CONTROL_ENTRY, model);
			} else {
				if (operation2.equals("debug")) {
					return new ModelAndView(WebKeys.VIEW_ACCESS_TO_NET_FOLDER, model);
				} else {
					return new ModelAndView(WebKeys.VIEW_ACCESS_CONTROL, model);
				}
			}
		}
	}
	//shared with binder config 
	public static void getAccessResults(ActionRequest request, Map functionMemberships) {
		Map formData = request.getParameterMap();

		if (formData.containsKey("roleIds")) {
			String[] roleIds = (String[]) formData.get("roleIds");
			for (int i = 0; i < roleIds.length; i++) {
				if (!roleIds[i].equals("")) {
					Long roleId = Long.valueOf(roleIds[i]);
					if (!functionMemberships.containsKey(roleId)) {
						functionMemberships.put(roleId, new HashSet());
					}
				}
			}
		}
		//Look for role settings (e.g., role_id..._...)
		Iterator itFormData = formData.entrySet().iterator();
		while (itFormData.hasNext()) {
			Map.Entry me = (Map.Entry)itFormData.next();
			String key = (String)me.getKey();
			if (key.length() >= 8 && key.substring(0,7).equals("role_id")) {
				String[] s_roleId = key.substring(7).split("_");
				if (s_roleId.length == 2) {
					Long roleId = Long.valueOf(s_roleId[0]);
					Long memberId = null;
					if (s_roleId[1].equals("owner")) {
						memberId = ObjectKeys.OWNER_USER_ID;
					} else if (s_roleId[1].equals("teamMember")) {
						memberId = ObjectKeys.TEAM_MEMBER_ID;
					} else {
						memberId = Long.valueOf(s_roleId[1]);
					}
						Set members = (Set)functionMemberships.get(roleId);
					if (!members.contains(memberId)) members.add(memberId);
				}
			}
		}

	}
	
	//used by ajax controller
	public static void setupAccess(AllModulesInjected bs, RenderRequest request, RenderResponse response, WorkArea wArea, Map model) {
		setupAccessImpl(bs, request, response, wArea, model);
	}
	public static void setupAccess(AllModulesInjected bs, WorkArea wArea, Map model) {
		setupAccessImpl(bs, null, null, wArea, model);
	}
	private static void setupAccessImpl(AllModulesInjected bs, RenderRequest request, RenderResponse response, WorkArea wArea, Map model) {
		String scope = ObjectKeys.ROLE_TYPE_BINDER;
		List<Function> extraFunctions = new ArrayList<Function>();
		if (wArea instanceof ZoneConfig) scope = ObjectKeys.ROLE_TYPE_ZONE;
		if (wArea instanceof Entry) {
			scope = ObjectKeys.ROLE_TYPE_ENTRY;
			//See if this entry's ACL is externally controlled
			Binder parentBinder = ((Entry) wArea).getParentBinder();
			if (((Entry) wArea).hasEntryExternalAcl() || parentBinder instanceof AclResourceDriver) {
				//This entry has its ACL controlled externally
				model.put(WebKeys.WORKAREA_IS_EXTERNAL_ACLS, Boolean.TRUE);
				AclResourceDriver ard = (AclResourceDriver)parentBinder.getResourceDriver();
				scope = ard.getRegisteredRoleTypeName();
				//Get the list of other entry functions that can also be used with this WorkArea
				//These roles cannot have any rights that are being controlled externally
				List<Function> entryFunctions = bs.getAdminModule().getFunctions(ObjectKeys.ROLE_TYPE_ENTRY);
				List<WorkAreaOperation> ardWaos = wArea.getExternallyControlledRights();
				//Now check if this role is OK to be used
				for (Function f : entryFunctions) {
					//If there are no operations (aka rights) that are being controlled by the external ACL 
					//  in this function, then it is OK to be included in the list
					boolean addThisFunction = true;
					Set<WorkAreaOperation> waos = f.getOperations();
					for (WorkAreaOperation wao : waos) {
						if (ardWaos.contains(wao)) {
							addThisFunction = false;
							break;
						}
					}
					if (addThisFunction) {
						//This function is ok to use
						extraFunctions.add(f);
					}
				}
			}
		}
		if (wArea instanceof Binder && ((Binder)wArea).isMirrored()) {
			//This is a mirrored folder. See if it is an external ACL controlled folder
			Binder binder = (Binder) wArea;
			if (binder.getResourceDriver() instanceof AclResourceDriver) {
				//This is a mirrored folder with external ACLs
				model.put(WebKeys.WORKAREA_IS_EXTERNAL_ACLS, Boolean.TRUE);
				AclResourceDriver ard = (AclResourceDriver)binder.getResourceDriver();
				scope = ard.getRegisteredRoleTypeName();
				//Get the list of other binder functions that can also be used with this WorkArea
				//These roles cannot have any rights that are being controlled externally
				List<Function> binderFunctions = bs.getAdminModule().getFunctions(ObjectKeys.ROLE_TYPE_BINDER);
				List<WorkAreaOperation> ardWaos = wArea.getExternallyControlledRights();
				List<WorkAreaFunctionMembership> memberships = bs.getAdminModule().getWorkAreaFunctionMemberships(wArea);
				//Now check if this role is OK to be used
				for (Function f : binderFunctions) {
					//If there are no operations (aka rights) that are being controlled by the external ACL 
					//  in this function, then it is OK to be included in the list
					boolean addThisFunction = true;
					Set<WorkAreaOperation> waos = f.getOperations();
					for (WorkAreaOperation wao : waos) {
						if (ardWaos.contains(wao)) {
							addThisFunction = false;
							if ((null != request) && "true".equals(request.getParameter("showAll"))) {
								//Check if there are members in this function
								for (WorkAreaFunctionMembership membership : memberships) {
									if (membership.getFunctionId() == f.getId() && !(membership.getMemberIds().isEmpty())) {
										//Override the rule if there is a membership in this function. We don't want it to be invisible
										addThisFunction = true;
									}
								}
							}
							break;
						}
					}
					if (addThisFunction) {
						//This function is ok to use
						extraFunctions.add(f);
					}
				}
			}
		}
		List functions = bs.getAdminModule().getFunctions(scope);
		if (!extraFunctions.isEmpty()) {
			for (Function f : extraFunctions) {
				if (!functions.contains(f)) functions.add(f);
			}
		}
		List membership;
		boolean zoneWide = wArea.getWorkAreaType().equals(ZoneConfig.WORKAREA_TYPE);
		if (wArea.isFunctionMembershipInherited()) {
			membership = bs.getAdminModule().getWorkAreaFunctionMembershipsInherited(wArea);
		} else {
			membership = bs.getAdminModule().getWorkAreaFunctionMemberships(wArea);
		}
		if (null == request)
			 WorkAreaHelper.buildAccessControlTableBeans(bs,                    wArea, functions, membership, model       );
		else WorkAreaHelper.buildAccessControlTableBeans(bs, request, response, wArea, functions, membership, model, false);

		Object	hiO = model.get(WebKeys.ACCESS_HONOR_INHERITANCE);
		boolean hi  = ((null != hiO) && (hiO instanceof Boolean) && ((Boolean) hiO));
		boolean checkInherited;
		if (hi)
		     checkInherited =    wArea.isFunctionMembershipInherited();
		else checkInherited = (!(wArea.isFunctionMembershipInherited()));
		if (checkInherited) {
			WorkArea parentArea = wArea.getParentWorkArea();
			if (parentArea != null) {
				List parentMembership;
				if (parentArea.isFunctionMembershipInherited()) {
					parentMembership = bs.getAdminModule().getWorkAreaFunctionMembershipsInherited(parentArea);
				} else {
					parentMembership = bs.getAdminModule().getWorkAreaFunctionMemberships(parentArea);
				}
				Map modelParent = new HashMap();
				if (null == request)
				     WorkAreaHelper.buildAccessControlTableBeans(bs,                    parentArea, functions, parentMembership, modelParent      );
				else WorkAreaHelper.buildAccessControlTableBeans(bs, request, response, parentArea, functions, parentMembership, modelParent, true);
				model.put(WebKeys.ACCESS_PARENT, modelParent);
				WorkAreaHelper.mergeAccessControlTableBeans(model);
			}
		}
		
		//Set up the role beans
		WorkAreaHelper.buildAccessControlRoleBeans(bs, model, zoneWide);
	}
	
	//Routine to build the beans for the report of share requests for this workarea
	public void setupSharedBeans(AllModulesInjected bs, WorkArea wArea, Map model) {
		//Get the list of ShareItems that reference this workarea
		if (wArea instanceof DefinableEntity) {
			List sortedUsersAll = (List)model.get(WebKeys.ACCESS_SORTED_USERS_ALL);
			List sortedGroupsAll = (List)model.get(WebKeys.ACCESS_SORTED_GROUPS_ALL);
			ShareItemSelectSpec spec = new ShareItemSelectSpec();
			spec.setSharedEntityIdentifier(((DefinableEntity)wArea).getEntityIdentifier());
			List<ShareItem> shareItems = bs.getSharingModule().getShareItems(spec);
			model.put(WebKeys.ACCESS_CONTROL_SHARE_ITEMS, shareItems);
			
			//Now load in the recipient objects
			Map<Long, Boolean> deleteRights = new HashMap<Long, Boolean>();
			Map<Long, DefinableEntity> recipients = new HashMap<Long, DefinableEntity>();
			for (ShareItem shareItem : shareItems) {
				recipients.put(shareItem.getId(), getSharingModule().getSharedRecipient(shareItem));
				deleteRights.put(shareItem.getId(), getSharingModule().testAccess(shareItem, SharingModule.SharingOperation.deleteShareItem));
				Principal p = bs.getProfileModule().getEntry(shareItem.getRecipientId());
				if (shareItem.getRecipientType().equals(ShareItem.RecipientType.user)) {
					if (!sortedUsersAll.contains(p)) sortedUsersAll.add(p);
				} else {
					if (!sortedGroupsAll.contains(p)) sortedGroupsAll.add(p);
				}
			}
			model.put(WebKeys.ACCESS_CONTROL_SHARE_ITEM_RECIPIENTS, recipients);
			model.put(WebKeys.ACCESS_CONTROL_SHARE_ITEM_DELETE_RIGHTS, deleteRights);
		}
	}
}
