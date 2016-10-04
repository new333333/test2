/**
 * Copyright (c) 1998-2011 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2011 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2011 Novell, Inc. All Rights Reserved.
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
package org.kablink.teaming.portlet.administration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.Group;
import org.kablink.teaming.domain.Principal;
import org.kablink.teaming.domain.ResourceDriverConfig;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.domain.ZoneConfig;
import org.kablink.teaming.domain.ResourceDriverConfig.DriverType;
import org.kablink.teaming.module.admin.AdminModule.AdminOperation;
import org.kablink.teaming.module.resourcedriver.RDException;
import org.kablink.teaming.security.function.Function;
import org.kablink.teaming.security.function.WorkAreaFunctionMembership;
import org.kablink.teaming.util.LongIdUtil;
import org.kablink.teaming.util.ResolveIds;
import org.kablink.teaming.util.Utils;
import org.kablink.teaming.web.WebKeys;
import org.kablink.teaming.web.portlet.SAbstractController;
import org.kablink.teaming.web.util.PortletRequestUtils;
import org.kablink.teaming.web.util.WebHelper;
import org.springframework.web.portlet.ModelAndView;


@SuppressWarnings({"unchecked","unused"})
public class ManageResourceDriverController extends SAbstractController {

	public void handleActionRequestAfterValidation(ActionRequest request, ActionResponse response) throws Exception {
		response.setRenderParameters(request.getParameterMap());
		Map formData = request.getParameterMap();
		getAdminModule().checkAccess(AdminOperation.manageResourceDrivers);
		if ((formData.containsKey("okBtn") || formData.containsKey("addBtn") || 
				formData.containsKey("modifyBtn") || 
				formData.containsKey("deleteBtn")) && WebHelper.isMethodPost(request)) {
			if (getAdminModule().testAccess(AdminOperation.manageResourceDrivers)) {
				if (formData.containsKey("deleteBtn")) {
					String name = PortletRequestUtils.getStringParameter(request, "nameToModify");
					//Delete this resource driver
					try {
						getResourceDriverModule().deleteResourceDriver(name);
					} catch(RDException rde) {
			    		response.setRenderParameter(WebKeys.ERROR_MESSAGE, rde.getMessage());
					}
				} else if (formData.containsKey("addBtn")) {
					String name = PortletRequestUtils.getStringParameter(request, "driverName");
					String driverType = PortletRequestUtils.getStringParameter(request, "driverType", DriverType.filesystem.name());
					String rootPath = PortletRequestUtils.getStringParameter(request, "rootPath", false);
					Map options = new HashMap();
					
					//Is this Read Only?
					Boolean readonly = PortletRequestUtils.getBooleanParameter(request, "readonly", Boolean.FALSE);
					options.put(ObjectKeys.RESOURCE_DRIVER_READ_ONLY, readonly);
					
					//Is there a Host URL 
					String hostUrl = PortletRequestUtils.getStringParameter(request, "hostUrl_"+driverType, "");
					options.put(ObjectKeys.RESOURCE_DRIVER_HOST_URL, hostUrl);
					
					//Allow self signed certificates? 
					Boolean allowSelfSignedCertificate = PortletRequestUtils.getBooleanParameter(request, "allowSelfSignedCertificate_"+driverType, Boolean.FALSE);
					options.put(ObjectKeys.RESOURCE_DRIVER_ALLOW_SELF_SIGNED_CERTIFICATE, allowSelfSignedCertificate);
					
					//Always prevent the top level folder from being deleted
					//  This is forced so that the folder could not accidentally be deleted if the 
					//  external disk was offline
					options.put(ObjectKeys.RESOURCE_DRIVER_SYNCH_TOP_DELETE, Boolean.FALSE);
					
					//Is there an account name 
					String accountName = PortletRequestUtils.getStringParameter(request, "accountName_"+driverType, "", false);
					options.put(ObjectKeys.RESOURCE_DRIVER_ACCOUNT_NAME, accountName);
					
					//Is there a password 
					String password = PortletRequestUtils.getStringParameter(request, "password_"+driverType, "", false);
					if (!password.equals("")) {
						options.put(ObjectKeys.RESOURCE_DRIVER_PASSWORD, password);
					}
					
					//Is there a server name 
					String serverName = PortletRequestUtils.getStringParameter(request, "serverName_"+driverType, "", false);
					options.put(ObjectKeys.RESOURCE_DRIVER_SERVER_NAME, serverName);
					
					//Is there a server IP address 
					String serverIP = PortletRequestUtils.getStringParameter(request, "serverIP_"+driverType, "");
					options.put(ObjectKeys.RESOURCE_DRIVER_SERVER_IP, serverIP);
					
					//Is there an share name 
					String shareName = PortletRequestUtils.getStringParameter(request, "shareName_"+driverType, "", false);
					options.put(ObjectKeys.RESOURCE_DRIVER_SHARE_NAME, shareName);
					
					//Is there an volume name 
					String volume = PortletRequestUtils.getStringParameter(request, "volume_"+driverType, "", false);
					options.put(ObjectKeys.RESOURCE_DRIVER_VOLUME, volume);
					
					//Get who is allowed to manage this 
					Set<Long> groupIds = LongIdUtil.getIdsAsLongSet(request.getParameterValues("addedGroups"));
					Set<Long> userIds = LongIdUtil.getIdsAsLongSet(request.getParameterValues("addedUsers"));
					Set<Long> memberIds = new HashSet<Long>();
					memberIds.addAll(userIds);
					memberIds.addAll(groupIds);
					
					//Add this resource driver
					try {
						getResourceDriverModule().addResourceDriver(name, DriverType.valueOf(driverType), 
							rootPath, memberIds, options);
					} catch(RDException rde) {
			    		response.setRenderParameter(WebKeys.ERROR_MESSAGE, rde.getMessage());
					}
				
				} else if (formData.containsKey("modifyBtn")) {
					//See if a selected driver needs to be modified
					String name = PortletRequestUtils.getStringParameter(request, "nameToModify");
					String driverType = PortletRequestUtils.getStringParameter(request, "driverType", DriverType.filesystem.name());
					String rootPath = PortletRequestUtils.getStringParameter(request, "rootPath", false);
					Map options = new HashMap();
					
					//Is this Read Only?
					Boolean readonly = PortletRequestUtils.getBooleanParameter(request, "readonly", Boolean.FALSE);
					options.put(ObjectKeys.RESOURCE_DRIVER_READ_ONLY, readonly);
					
					//Is there a Host URL 
					String hostUrl = PortletRequestUtils.getStringParameter(request, "hostUrl_"+driverType, "");
					options.put(ObjectKeys.RESOURCE_DRIVER_HOST_URL, hostUrl);
					
					//Allow self signed certificates? 
					Boolean allowSelfSignedCertificate = PortletRequestUtils.getBooleanParameter(request, "allowSelfSignedCertificate_"+driverType, Boolean.FALSE);
					options.put(ObjectKeys.RESOURCE_DRIVER_ALLOW_SELF_SIGNED_CERTIFICATE, allowSelfSignedCertificate);
					
					//Always prevent the top level folder from being deleted
					//  This is forced so that the folder could not accidentally be deleted if the 
					//  external disk was offline
					options.put(ObjectKeys.RESOURCE_DRIVER_SYNCH_TOP_DELETE, Boolean.FALSE);
					
					//Is there an account name 
					String accountName = PortletRequestUtils.getStringParameter(request, "accountName_"+driverType, "", false);
					options.put(ObjectKeys.RESOURCE_DRIVER_ACCOUNT_NAME, accountName);
					
					//Is there a password 
					String password = PortletRequestUtils.getStringParameter(request, "password_"+driverType, "", false);
					Boolean changePassword = PortletRequestUtils.getBooleanParameter(request, "changePassword_"+driverType, Boolean.FALSE);
					if (changePassword) {
						options.put(ObjectKeys.RESOURCE_DRIVER_PASSWORD, password);
					}
					
					//Is there a server name 
					String serverName = PortletRequestUtils.getStringParameter(request, "serverName_"+driverType, "", false);
					options.put(ObjectKeys.RESOURCE_DRIVER_SERVER_NAME, serverName);
					
					//Is there a server IP address 
					String serverIP = PortletRequestUtils.getStringParameter(request, "serverIP_"+driverType, "");
					options.put(ObjectKeys.RESOURCE_DRIVER_SERVER_IP, serverIP);
					
					//Is there an share name 
					String shareName = PortletRequestUtils.getStringParameter(request, "shareName_"+driverType, "", false);
					options.put(ObjectKeys.RESOURCE_DRIVER_SHARE_NAME, shareName);
					
					//Is there an volume name 
					String volume = PortletRequestUtils.getStringParameter(request, "volume_"+driverType, "", false);
					options.put(ObjectKeys.RESOURCE_DRIVER_VOLUME, volume);
					
					//Get who is allowed to create file spaces using this driver
					Set<Long> groupIds = LongIdUtil.getIdsAsLongSet(request.getParameterValues("addedGroups"));
					Set<Long> userIds = LongIdUtil.getIdsAsLongSet(request.getParameterValues("addedUsers"));
					Set<Long> memberIds = new HashSet<Long>();
					memberIds.addAll(userIds);
					memberIds.addAll(groupIds);

					try {
						getResourceDriverModule().modifyResourceDriver(name, DriverType.valueOf(driverType), 
							rootPath, memberIds, options);
					} catch(RDException rde) {
			    		response.setRenderParameter(WebKeys.ERROR_MESSAGE, rde.getMessage());
					}
				}
			}

		} else {
			response.setRenderParameters(formData);
		}
	}

	public ModelAndView handleRenderRequestAfterValidation(RenderRequest request, 
			RenderResponse response) throws Exception {
		Map model = new HashMap();
		model.put(WebKeys.ERROR_MESSAGE, request.getParameter(WebKeys.ERROR_MESSAGE));
		Map formData = request.getParameterMap();
		getAdminModule().checkAccess(AdminOperation.manageFunction);
		
		//Get a list of the currently defines Filesace Roots
		List<ResourceDriverConfig> drivers = getResourceDriverModule().getAllResourceDriverConfigs();
		model.put(WebKeys.FILESPACE_ROOTS, drivers);
		
		Map<String,Map<String,Set<Principal>>> functionMap = new HashMap<String,Map<String,Set<Principal>>>();
		List<Function> functions = getAdminModule().getFunctions(ObjectKeys.ROLE_TYPE_ZONE);
		for (ResourceDriverConfig driver : drivers) {
			List<WorkAreaFunctionMembership> memberships = getAdminModule().getWorkAreaFunctionMemberships(driver);
			WorkAreaFunctionMembership membership = null;
			for (Function f : functions) {
				if (ObjectKeys.FUNCTION_CREATE_FILESPACES_INTERNALID.equals(f.getInternalId())) {
					for (WorkAreaFunctionMembership m : memberships) {
						if (f.getId().equals(m.getFunctionId())) {
							membership = m;
							break;
						}
					}
				}
			}
			Set<Principal> users = new HashSet<Principal>();
			Set<Principal> groups = new HashSet<Principal>();
			List<Principal> members = new ArrayList<Principal>();
			if (membership != null) {
				members = ResolveIds.getPrincipals(membership.getMemberIds());
			}
			for (Principal p : members) {
				if (p instanceof User) {
					users.add(p);
				} else if (p instanceof Group) {
					groups.add(p);
				}
			}
			Map<String,Set<Principal>> ugSets = new HashMap<String,Set<Principal>>();
			ugSets.put("users", users);
			ugSets.put("groups", groups);
			functionMap.put(driver.getName(), ugSets);
		}
		model.put(WebKeys.FUNCTION_MAP, functionMap);

		return new ModelAndView(WebKeys.VIEW_ADMIN_MANAGE_RESOURCE_DRIVERS, model);

	}

}
