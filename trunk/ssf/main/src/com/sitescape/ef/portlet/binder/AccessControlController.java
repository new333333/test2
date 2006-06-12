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
import java.util.Collection;

import com.sitescape.ef.context.request.RequestContextHolder;
import com.sitescape.ef.domain.Binder;
import com.sitescape.ef.domain.User;
import com.sitescape.ef.domain.Principal;
import com.sitescape.ef.domain.Group;
import com.sitescape.ef.web.WebKeys;
import com.sitescape.ef.web.util.PortletRequestUtils;
import com.sitescape.ef.security.function.WorkAreaFunctionMembership;
import com.sitescape.ef.security.function.Function;
import com.sitescape.ef.util.ResolveIds;
import com.sitescape.util.Validator;

/**
 * @author Peter Hurley
 *
 */
public class AccessControlController extends AbstractBinderController {
	public void handleActionRequestInternal(ActionRequest request, ActionResponse response) 
	throws Exception {
		Map formData = request.getParameterMap();

		Long binderId = new Long(PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_BINDER_ID));				
		String binderType = PortletRequestUtils.getRequiredStringParameter(request, WebKeys.URL_BINDER_TYPE);	
		Binder binder = getBinderModule().getBinder(binderId);
		request.setAttribute("roleId", "");
		response.setRenderParameter(WebKeys.URL_BINDER_ID, binderId.toString());
		
		//See if the form was submitted
		if (formData.containsKey("addBtn")) {
			String s_roleId = request.getParameter("roleId");
			if (!Validator.isNull(s_roleId)) {
				Long roleId = new Long(PortletRequestUtils.getRequiredLongParameter(request, "roleId"));
				//String userIds[] = PortletRequestUtils.getStringParameters(request, "users");
				//String groupIds[] = PortletRequestUtils.getStringParameters(request, "groups");
				String userIds[] = null;
				if (formData.containsKey("users")) userIds = (String[])formData.get("users");
				String groupIds[] = null;
				if (formData.containsKey("groups")) groupIds = (String[])formData.get("groups");
				Set memberIds = new HashSet();
				if (userIds != null) {
					for (int i = 0; i < userIds.length; i++) {
						String[] ids = userIds[i].split(" ");
						for (int j = 0; j < ids.length; j++) {
							if (ids[j].length() > 0) memberIds.add(Long.valueOf(ids[j]));
						}
					}
				}
				if (groupIds != null) {
					for (int i = 0; i < groupIds.length; i++) {
						String[] ids = groupIds[i].split(" ");
						for (int j = 0; j < ids.length; j++) {
							if (ids[j].length() > 0) memberIds.add(Long.valueOf(ids[j]));
						}
					}
				}
				WorkAreaFunctionMembership wfm = getAdminModule().getWorkAreaFunctionMembership(binder, roleId);
				if (wfm == null) {
					getAdminModule().addWorkAreaFunctionMembership(binder, roleId, memberIds);
				} else {
					//Modify the existing membership
					wfm.setMemberIds(memberIds);
					getAdminModule().modifyWorkAreaFunctionMembership(binder, wfm);
				}
			}
			
		} else if (formData.containsKey("modifyBtn")) {
			String s_roleId = request.getParameter("roleId");
			if (!Validator.isNull(s_roleId)) {
				Long roleId = new Long(PortletRequestUtils.getRequiredLongParameter(request, "roleId"));
				//find the function membership
				WorkAreaFunctionMembership wfm = getAdminModule().getWorkAreaFunctionMembership(binder, roleId);
				if (wfm != null) {
					request.setAttribute("roleId", s_roleId);
				}
			}
		} else if (formData.containsKey("deleteBtn")) {
			String s_roleId = request.getParameter("roleId");
			if (!Validator.isNull(s_roleId)) {
				Long roleId = new Long(PortletRequestUtils.getRequiredLongParameter(request, "roleId"));
				//Delete the function membership
				getAdminModule().deleteWorkAreaFunctionMembership(binder, roleId);
			}
			
		} else if (formData.containsKey("inheritanceBtn")) {
			boolean inherit = PortletRequestUtils.getBooleanParameter(request, "inherit", false);
			getAdminModule().setWorkAreaFunctionMembershipInherited(binder,inherit);			
		} else if (formData.containsKey("cancelBtn") || formData.containsKey("closeBtn")) {
			setupViewBinder(response, binderId, binderType);
		}
	}
	public ModelAndView handleRenderRequestInternal(RenderRequest request, 
			RenderResponse response) throws Exception {
		Long binderId = new Long(PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_BINDER_ID));				
		Binder binder = getBinderModule().getBinder(binderId);
		
		Map model = new HashMap();
		User user = RequestContextHolder.getRequestContext().getUser();
		
		Map functionMap = new HashMap();
		List functions = getAdminModule().getFunctions();
		List membership;
		if (binder.isFunctionMembershipInherited()) 
			membership = getAdminModule().getWorkAreaFunctionMembershipsInherited(binder);
		else
			membership = getAdminModule().getWorkAreaFunctionMemberships(binder);
		
		for (int i=0; i<functions.size(); ++i) {
			Function f = (Function)functions.get(i);
			Map pMap = new HashMap();
			functionMap.put(f, pMap);
			Set groups = new HashSet();
			Set users = new HashSet();
			pMap.put(WebKeys.USERS, users);
			pMap.put(WebKeys.GROUPS, groups);
			for (int j=0; j<membership.size(); ++j) {
				WorkAreaFunctionMembership m = (WorkAreaFunctionMembership)membership.get(j);
				if (f.getId().equals(m.getFunctionId())) {
					Collection ids = ResolveIds.getPrincipals(m.getMemberIds());
					for (Iterator iter=ids.iterator(); iter.hasNext();) {
						Principal p = (Principal)iter.next();
						if (p instanceof Group) {
							groups.add(p);
						} else users.add(p);
					}
					break;
				}
			}
		}
		model.put(WebKeys.BINDER, binder);
		model.put(WebKeys.FUNCTION_MAP, functionMap);
		model.put(WebKeys.CONFIG_JSP_STYLE, "view");
		model.put(WebKeys.USER_PROPERTIES, getProfileModule().getUserProperties(user.getId()));
			
//		DefinitionUtils.getDefinitions(model);
//		DefinitionUtils.getDefinitions(binder, model);
//		DefinitionUtils.getDefinitions(Definition.WORKFLOW, WebKeys.PUBLIC_WORKFLOW_DEFINITIONS, model);
	
		return new ModelAndView(WebKeys.VIEW_ACCESS_CONTROL, model);
	}

}
