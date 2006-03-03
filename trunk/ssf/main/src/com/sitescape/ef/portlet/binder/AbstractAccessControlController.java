package com.sitescape.ef.portlet.binder;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.PortletRequest;
import javax.servlet.ServletRequest;

import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Iterator;
import java.util.Set;
import java.util.HashSet;
import java.util.Collection;

import com.sitescape.ef.ObjectKeys;
import com.sitescape.ef.context.request.RequestContextHolder;
import com.sitescape.ef.domain.Definition;
import com.sitescape.ef.domain.Binder;
import com.sitescape.ef.domain.User;
import com.sitescape.ef.domain.Principal;
import com.sitescape.ef.domain.Group;
import com.sitescape.ef.portlet.forum.SAbstractForumController;
import com.sitescape.ef.web.WebKeys;
import com.sitescape.ef.web.util.DefinitionUtils;
import com.sitescape.ef.web.util.PortletRequestUtils;
import com.sitescape.ef.security.function.WorkAreaFunctionMembership;
import com.sitescape.ef.security.function.Function;
import com.sitescape.ef.util.ResolveIds;
import com.sitescape.util.ParamUtil;

/**
 * @author Peter Hurley
 *
 */
public abstract class AbstractAccessControlController extends SAbstractForumController {
	public void handleActionRequestInternal(ActionRequest request, ActionResponse response) 
	throws Exception {
		Map formData = request.getParameterMap();
		response.setRenderParameters(formData);

		Long binderId = new Long(PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_BINDER_ID));				
			
		Map binderConf = getBinderModule().getBinderFunctionMembership(binderId);
		Binder binder = (Binder)binderConf.get(ObjectKeys.BINDER);
		List membership = (List)binderConf.get(ObjectKeys.FUNCTION_MEMBERSHIP);

		//See if the form was submitted
		if (formData.containsKey("addBtn")) {
			String s_roleId = request.getParameter("roleId");
			if (s_roleId != null && !s_roleId.equals("")) {
				Long roleId = new Long(request.getParameter("roleId"));
				String[] userIds = request.getParameterValues("users");
				String[] groupIds = request.getParameterValues("groups");
				Set memberIds = new HashSet();
				if (userIds != null) {
					for(int i = 0; i < userIds.length; i++) {
						String[] ids = userIds[i].split(" ");
						for(int j = 0; j < ids.length; j++) {
							if(ids[j].length() > 0)
								memberIds.add(Long.valueOf(ids[j]));
						}
					}
				}
				if (groupIds != null) {
					for (int i = 0; i < groupIds.length; i++) {
						String[] ids = groupIds[i].split(" ");
						for(int j = 0; j < ids.length; j++) {
							if(ids[j].length() > 0)
								memberIds.add(Long.valueOf(ids[j]));
						}
					}
				}
				WorkAreaFunctionMembership wfm = null;
				for (int i = 0; i < membership.size(); i++) {
					if (roleId.equals(((WorkAreaFunctionMembership) membership.get(i)).getFunctionId())) {
						//The function already is in use for this workarea.
						wfm = (WorkAreaFunctionMembership) membership.get(i);
						break;
					}
				}
				if (wfm == null) {
					wfm = new WorkAreaFunctionMembership();
					//Build the workarea membership object
					wfm.setFunctionId(roleId);
					wfm.setMemberIds(memberIds);
					getAdminModule().addWorkAreaFunctionMembership(binder, wfm);
				} else {
					//Modify the existing membership
					wfm.setMemberIds(memberIds);
					getAdminModule().modifyWorkAreaFunctionMembership(binder, wfm);
				}
			}
			
		} else if (formData.containsKey("modifyBtn")) {
		} else if (formData.containsKey("cancelBtn") || formData.containsKey("closeBtn")) {
			setResponseOnClose(response, binderId);
		}
	}
	public ModelAndView handleRenderRequestInternal(RenderRequest request, 
			RenderResponse response) throws Exception {
		Long binderId = new Long(PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_BINDER_ID));				
		
		Map model = new HashMap();
		User user = RequestContextHolder.getRequestContext().getUser();
		Map binderConf = getBinderModule().getBinderFunctionMembership(binderId);
		Binder binder = (Binder)binderConf.get(ObjectKeys.BINDER);
		Map functionMap = new HashMap();
		List functions = (List)binderConf.get(ObjectKeys.FUNCTIONS);
		List membership = (List)binderConf.get(ObjectKeys.FUNCTION_MEMBERSHIP);
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
		model.put(WebKeys.FOLDER_WORKFLOW_ASSOCIATIONS, binder.getProperty(ObjectKeys.BINDER_WORKFLOW_ASSOCIATIONS));
		model.put(WebKeys.CONFIG_JSP_STYLE, "view");
		model.put(WebKeys.USER_PROPERTIES, getProfileModule().getUserProperties(user.getId()));
			
		DefinitionUtils.getDefinitions(model);
		DefinitionUtils.getDefinitions(binder, model);
		DefinitionUtils.getDefinitions(Definition.WORKFLOW, WebKeys.PUBLIC_WORKFLOW_DEFINITIONS, model);
	
		return new ModelAndView(WebKeys.VIEW_ACCESS_CONTROL, model);
	}
	protected abstract void setResponseOnClose(ActionResponse responose, Long binderId);


}
