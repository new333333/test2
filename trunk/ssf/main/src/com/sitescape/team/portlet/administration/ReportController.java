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
package com.sitescape.team.portlet.administration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.Collection;

import javax.activation.FileTypeMap;
import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.web.servlet.ModelAndView;

import com.sitescape.team.context.request.RequestContextHolder;
import com.sitescape.team.domain.ProfileBinder;
import com.sitescape.team.module.report.ReportModule;
import com.sitescape.team.util.NLT;
import com.sitescape.team.util.SpringContextUtil;
import com.sitescape.team.web.WebKeys;
import com.sitescape.team.web.portlet.SAbstractController;
import com.sitescape.team.web.tree.DomTreeBuilder;
import com.sitescape.team.web.tree.SearchTreeHelper;
import com.sitescape.team.web.tree.WsDomTreeBuilder;
import com.sitescape.util.Validator;
public class ReportController extends  SAbstractController {
	
	public void handleActionRequestAfterValidation(ActionRequest request, ActionResponse response) throws Exception {
		response.setRenderParameters(request.getParameterMap());
	}
	
	public ModelAndView handleRenderRequestInternal(RenderRequest request, 
			RenderResponse response) throws Exception {

		Map formData = request.getParameterMap();
		if (formData.containsKey("closeBtn") || formData.containsKey("cancelBtn")) {
			return new ModelAndView(WebKeys.VIEW_ADMIN_REDIRECT);
		}
		
		Map model = new HashMap();
		Document pTree = DocumentHelper.createDocument();
    	Element rootElement = pTree.addElement(DomTreeBuilder.NODE_ROOT);
    	Element users = rootElement.addElement(DomTreeBuilder.NODE_CHILD);
    	ProfileBinder p = getProfileModule().getProfileBinder();
    	users.addAttribute("type", DomTreeBuilder.NODE_TYPE_PEOPLE);
    	users.addAttribute("title", NLT.get("administration.profile.content"));
    	users.addAttribute("id", p.getId().toString());
		String icon = p.getIconName();
		if (Validator.isNull(icon)) {
	    	users.addAttribute("image", "people");
		} else {
			users.addAttribute("image", icon);
			users.addAttribute("imageClass", "ss_twIcon");
		}
		users.addAttribute("url", "");
    	Document wsTree = getWorkspaceModule().getDomWorkspaceTree(RequestContextHolder.getRequestContext().getZoneId(), 
				new WsDomTreeBuilder(null, true, this, new SearchTreeHelper()),1);
    	//merge the trees
    	rootElement.appendAttributes(wsTree.getRootElement());
    	rootElement.appendContent(wsTree.getRootElement());
 		model.put(WebKeys.WORKSPACE_DOM_TREE_BINDER_ID, RequestContextHolder.getRequestContext().getZoneId().toString());
		model.put(WebKeys.WORKSPACE_DOM_TREE, pTree);		
			
		return new ModelAndView(WebKeys.VIEW_ADMIN_REPORT, model);
	}

}
