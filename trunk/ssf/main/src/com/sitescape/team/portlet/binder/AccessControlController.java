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
package com.sitescape.team.portlet.binder;

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

import org.springframework.web.portlet.ModelAndView;

import com.sitescape.team.ObjectKeys;
import com.sitescape.team.domain.Binder;
import com.sitescape.team.domain.TemplateBinder;
import com.sitescape.team.domain.User;
import com.sitescape.team.module.shared.AccessUtils;
import com.sitescape.team.security.function.WorkArea;
import com.sitescape.team.util.AllModulesInjected;
import com.sitescape.team.util.SimpleProfiler;
import com.sitescape.team.web.WebKeys;
import com.sitescape.team.web.util.BinderHelper;
import com.sitescape.team.web.util.PortletRequestUtils;

/**
 * This controller/jsp is used by administration/ConfigureAccessController
 * Keep in sync
 * @author Peter Hurley
 *
 */
public class AccessControlController extends AbstractBinderController {
	public void handleActionRequestAfterValidation(ActionRequest request, ActionResponse response) 
	throws Exception {
		Map formData = request.getParameterMap();
		Long binderId = new Long(PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_BINDER_ID));				
		Binder binder = getBinderModule().getBinder(binderId);
		request.setAttribute("roleId", "");
		response.setRenderParameter(WebKeys.URL_BINDER_ID, binderId.toString());
		
		//See if the form was submitted
		if (formData.containsKey("okBtn")) {
			SimpleProfiler.setProfiler(new SimpleProfiler("lucene"));
			Map functionMemberships = new HashMap();
			getAccessResults(request, functionMemberships);
			getAdminModule().setWorkAreaFunctionMemberships((WorkArea) binder, functionMemberships);
			if(logger.isDebugEnabled())
				logger.debug(SimpleProfiler.toStr());
			SimpleProfiler.clearProfiler();
		} else if (formData.containsKey("inheritanceBtn")) {
			boolean inherit = PortletRequestUtils.getBooleanParameter(request, "inherit", false);
			getAdminModule().setWorkAreaFunctionMembershipInherited(binder,inherit);			
		
		} else if (formData.containsKey("cancelBtn") || formData.containsKey("closeBtn")) {
			if (binder instanceof TemplateBinder) {
				response.setRenderParameter(WebKeys.ACTION, WebKeys.ACTION_CONFIGURATION);
				response.setRenderParameter(WebKeys.URL_BINDER_ID, binderId.toString());
			} else {
				setupCloseWindow(response);
			}
			
		} else {
			response.setRenderParameters(request.getParameterMap());
		}
	}
	public ModelAndView handleRenderRequestInternal(RenderRequest request, 
			RenderResponse response) throws Exception {
		Long binderId = new Long(PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_BINDER_ID));				
		Binder binder = getBinderModule().getBinder(binderId);
		
		Map model = new HashMap();
		setupAccess(this, request, response, binder, model);
		//Build the navigation beans
		BinderHelper.buildNavigationLinkBeans(this, binder, model);
		model.put(WebKeys.DEFINITION_ENTRY, binder);
		model.put(WebKeys.ENTRY, binder);
		User superUser = AccessUtils.getZoneSuperUser(binder.getZoneId());
		model.put(WebKeys.ACCESS_SUPER_USER, superUser);

		return new ModelAndView(WebKeys.VIEW_ACCESS_CONTROL, model);
	}
	//shared with binderconfig 
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
	public static void setupAccess(AllModulesInjected bs, RenderRequest request, RenderResponse response, Binder binder, Map model) {
		List functions = bs.getAdminModule().getFunctions();
		List membership;
		
		if (binder.isFunctionMembershipInherited()) {
			membership = bs.getAdminModule().getWorkAreaFunctionMembershipsInherited(binder);
		} else {
			membership = bs.getAdminModule().getWorkAreaFunctionMemberships(binder);
		}
		BinderHelper.buildAccessControlTableBeans(request, response, binder, functions, 
				membership, model, false);

		if (!binder.isFunctionMembershipInherited()) {
			Binder parentBinder = binder.getParentBinder();
			if (parentBinder != null) {
				List parentMembership;
				if (parentBinder.isFunctionMembershipInherited()) {
					parentMembership = bs.getAdminModule().getWorkAreaFunctionMembershipsInherited(parentBinder);
				} else {
					parentMembership = bs.getAdminModule().getWorkAreaFunctionMemberships(parentBinder);
				}
				Map modelParent = new HashMap();
				BinderHelper.buildAccessControlTableBeans(request, response, parentBinder, 
						functions, parentMembership, modelParent, true);
				model.put(WebKeys.ACCESS_PARENT, modelParent);
				BinderHelper.mergeAccessControlTableBeans(model);
			}
		}
		
		//Set up the role beans
		BinderHelper.buildAccessControlRoleBeans(bs, model);
	}

}
