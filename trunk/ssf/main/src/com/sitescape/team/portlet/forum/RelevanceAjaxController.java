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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.dom4j.Document;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.portlet.bind.PortletRequestBindingException;
import org.springframework.web.servlet.ModelAndView;

import com.sitescape.team.ObjectKeys;
import com.sitescape.team.calendar.CalendarViewRangeDates;
import com.sitescape.team.calendar.EventsViewHelper;
import com.sitescape.team.context.request.RequestContextHolder;
import com.sitescape.team.domain.Binder;
import com.sitescape.team.domain.CustomAttribute;
import com.sitescape.team.domain.Definition;
import com.sitescape.team.domain.EntityIdentifier;
import com.sitescape.team.domain.Entry;
import com.sitescape.team.domain.FileAttachment;
import com.sitescape.team.domain.Folder;
import com.sitescape.team.domain.FolderEntry;
import com.sitescape.team.domain.Group;
import com.sitescape.team.domain.Principal;
import com.sitescape.team.domain.ReservedByAnotherUserException;
import com.sitescape.team.domain.SeenMap;
import com.sitescape.team.domain.Subscription;
import com.sitescape.team.domain.TemplateBinder;
import com.sitescape.team.domain.User;
import com.sitescape.team.domain.UserProperties;
import com.sitescape.team.domain.Workspace;
import com.sitescape.team.domain.NoBinderByTheNameException;
import com.sitescape.team.domain.AuditTrail.AuditType;
import com.sitescape.team.domain.EntityIdentifier.EntityType;
import com.sitescape.team.module.binder.BinderModule.BinderOperation;
import com.sitescape.team.module.file.WriteFilesException;
import com.sitescape.team.module.ic.DocumentDownload;
import com.sitescape.team.module.ic.ICBrokerModule;
import com.sitescape.team.module.ic.ICException;
import com.sitescape.team.module.ic.RecordType;
import com.sitescape.team.module.shared.MapInputData;
import com.sitescape.team.portlet.binder.AccessControlController;
import com.sitescape.team.portletadapter.AdaptedPortletURL;
import com.sitescape.team.portletadapter.support.PortletAdapterUtil;
import com.sitescape.team.search.filter.SearchFiltersBuilder;
import com.sitescape.team.security.AccessControlException;
import com.sitescape.team.security.function.OperationAccessControlException;
import com.sitescape.team.ssfs.util.SsfsUtil;
import com.sitescape.team.survey.Question;
import com.sitescape.team.survey.Survey;
import com.sitescape.team.survey.SurveyModel;
import com.sitescape.team.task.TaskHelper;
import com.sitescape.team.util.CalendarHelper;
import com.sitescape.team.util.LongIdUtil;
import com.sitescape.team.util.NLT;
import com.sitescape.team.util.SPropsUtil;
import com.sitescape.team.util.SimpleMultipartFile;
import com.sitescape.team.util.TagUtil;
import com.sitescape.team.util.TempFileUtil;
import com.sitescape.team.web.WebKeys;
import com.sitescape.team.web.portlet.SAbstractControllerRetry;
import com.sitescape.team.web.tree.DomTreeBuilder;
import com.sitescape.team.web.tree.WsDomTreeBuilder;
import com.sitescape.team.web.util.BinderHelper;
import com.sitescape.team.web.util.DefinitionHelper;
import com.sitescape.team.web.util.Favorites;
import com.sitescape.team.web.util.ListFolderHelper;
import com.sitescape.team.web.util.PortletRequestUtils;
import com.sitescape.team.web.util.Tabs;
import com.sitescape.team.web.util.WebHelper;
import com.sitescape.team.web.util.WebStatusTicket;
import com.sitescape.team.web.util.WebUrlUtil;
import com.sitescape.util.Validator;
/**
 * @author Peter Hurley
 *
 */
public class RelevanceAjaxController  extends SAbstractControllerRetry {
	
	
	//caller will retry on OptimisiticLockExceptions
	public void handleActionRequestWithRetry(ActionRequest request, ActionResponse response) throws Exception {
		response.setRenderParameters(request.getParameterMap());
		if (WebHelper.isUserLoggedIn(request)) {
			String op = PortletRequestUtils.getStringParameter(request, WebKeys.URL_OPERATION, "");
		}
	}
	
	public ModelAndView handleRenderRequestInternal(RenderRequest request, 
			RenderResponse response) throws Exception {
		String op = PortletRequestUtils.getStringParameter(request, WebKeys.URL_OPERATION, "");

		if (!WebHelper.isUserLoggedIn(request)) {
			Map model = new HashMap();
			Map statusMap = new HashMap();
			
			//Signal that the user is not logged in. 
			//  The code on the calling page will output the proper translated message.
			statusMap.put(WebKeys.AJAX_STATUS_NOT_LOGGED_IN, new Boolean(true));
			model.put(WebKeys.AJAX_STATUS, statusMap);

			return new ModelAndView("forum/fetch_url_return", model);
		}
		
		//The user is logged in
		if (op.equals(WebKeys.OPERATION_GET_RELEVANCE_DASHBOARD)) {
			return ajaxGetRelevanceDashboard(request, response);
		}

		return new ModelAndView("forum/fetch_url_return");
	} 	
	
	private ModelAndView ajaxGetRelevanceDashboard(RenderRequest request, 
			RenderResponse response) throws Exception {
		String type = PortletRequestUtils.getStringParameter(request, WebKeys.URL_TYPE, "");
		Map model = new HashMap();
		model.put(WebKeys.TYPE, type);
		if (type.equals(WebKeys.RELEVANCE_TYPE_PERSONAL)) {
			setupPersonalBeans(model);
		} else if (type.equals(WebKeys.RELEVANCE_TYPE_FRIENDS)) {
			setupFriendsBeans(model);
		} else if (type.equals(WebKeys.RELEVANCE_TYPE_VISITORS)) {
			setupVisitorsBeans(model);
		}
		return new ModelAndView("forum/relevance_dashboard_ajax", model);
	}
	
	private void setupPersonalBeans(Map model) {
		
	}
	
	private void setupFriendsBeans(Map model) {
		
	}
	
	private void setupVisitorsBeans(Map model) {
		//Who has visited my page?
		User user = RequestContextHolder.getRequestContext().getUser();
		Long workspaceId = user.getWorkspaceId();
		if (workspaceId != null) {
			Binder binder = getWorkspaceModule().getWorkspace(workspaceId);
			if (binder != null) {
				GregorianCalendar start = new GregorianCalendar();
			    //get users over last 2 weeks
			   start.add(java.util.Calendar.HOUR_OF_DAY, -24*14);
			   Collection users = getReportModule().getUsersActivity(binder, AuditType.view, 
					   start.getTime(), new java.util.Date());
			   model.put(WebKeys.USERS, users);
			}
		}
	}
	
}
