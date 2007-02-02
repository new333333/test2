package com.sitescape.ef.portlet.binder;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Iterator;
import java.util.Set;
import java.util.HashSet;

import com.sitescape.ef.ObjectKeys;
import com.sitescape.ef.domain.Binder;
import com.sitescape.ef.web.WebKeys;
import com.sitescape.ef.web.util.BinderHelper;
import com.sitescape.ef.web.util.PortletRequestUtils;
import com.sitescape.ef.security.function.WorkArea;
import com.sitescape.team.util.AllBusinessServicesInjected;

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
		String binderType = PortletRequestUtils.getRequiredStringParameter(request, WebKeys.URL_BINDER_TYPE);	
		Binder binder = getBinderModule().getBinder(binderId);
		request.setAttribute("roleId", "");
		response.setRenderParameter(WebKeys.URL_BINDER_ID, binderId.toString());
		
		//See if the form was submitted
		if (formData.containsKey("okBtn")) {
			Map functionMemberships = new HashMap();
			getAccessResults(request, functionMemberships);
			getAdminModule().setWorkAreaFunctionMemberships((WorkArea) binder, functionMemberships);
			
		} else if (formData.containsKey("inheritanceBtn")) {
			boolean inherit = PortletRequestUtils.getBooleanParameter(request, "inherit", false);
			getAdminModule().setWorkAreaFunctionMembershipInherited(binder,inherit);			
		
		} else if (formData.containsKey("cancelBtn") || formData.containsKey("closeBtn")) {
			setupViewBinder(response, binderId, binderType);
			
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
	public static void setupAccess(AllBusinessServicesInjected bs, RenderRequest request, RenderResponse response, Binder binder, Map model) {
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
		
	}

}
