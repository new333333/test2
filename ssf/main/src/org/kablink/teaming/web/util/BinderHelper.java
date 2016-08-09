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
package org.kablink.teaming.web.util;

import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletPreferences;
import javax.portlet.PortletRequest;
import javax.portlet.PortletSession;
import javax.portlet.PortletURL;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.WindowState;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.document.DateTools;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import org.kablink.teaming.NoObjectByTheIdException;
import org.kablink.teaming.NotSupportedException;
import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.TextVerificationException;
import org.kablink.teaming.comparator.PrincipalComparator;
import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.dao.CoreDao;
import org.kablink.teaming.dao.FolderDao;
import org.kablink.teaming.domain.Attachment;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.ChangeLog;
import org.kablink.teaming.domain.CommaSeparatedValue;
import org.kablink.teaming.domain.DashboardPortlet;
import org.kablink.teaming.domain.DefinableEntity;
import org.kablink.teaming.domain.Description;
import org.kablink.teaming.domain.EntityIdentifier;
import org.kablink.teaming.domain.Entry;
import org.kablink.teaming.domain.Event;
import org.kablink.teaming.domain.FileAttachment;
import org.kablink.teaming.domain.FileItem;
import org.kablink.teaming.domain.Folder;
import org.kablink.teaming.domain.FolderEntry;
import org.kablink.teaming.domain.HistoryStamp;
import org.kablink.teaming.domain.NoBinderByTheIdException;
import org.kablink.teaming.domain.Principal;
import org.kablink.teaming.domain.ProfileBinder;
import org.kablink.teaming.domain.SSBlobSerializable;
import org.kablink.teaming.domain.SeenMap;
import org.kablink.teaming.domain.SimpleName;
import org.kablink.teaming.domain.TemplateBinder;
import org.kablink.teaming.domain.TitleException;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.domain.UserPrincipal;
import org.kablink.teaming.domain.UserProperties;
import org.kablink.teaming.domain.VersionAttachment;
import org.kablink.teaming.domain.WorkflowState;
import org.kablink.teaming.domain.WorkflowSupport;
import org.kablink.teaming.domain.Workspace;
import org.kablink.teaming.domain.EntityIdentifier.EntityType;
import org.kablink.teaming.module.admin.AdminModule.AdminOperation;
import org.kablink.teaming.module.binder.BinderModule;
import org.kablink.teaming.module.binder.BinderModule.BinderOperation;
import org.kablink.teaming.module.definition.DefinitionUtils;
import org.kablink.teaming.module.file.NewableFileSupport;
import org.kablink.teaming.module.folder.FolderModule;
import org.kablink.teaming.module.folder.FolderModule.FolderOperation;
import org.kablink.teaming.module.profile.ProfileModule;
import org.kablink.teaming.module.report.ReportModule;
import org.kablink.teaming.module.shared.InputDataAccessor;
import org.kablink.teaming.module.shared.MapInputData;
import org.kablink.teaming.module.workflow.WorkflowProcessUtils;
import org.kablink.teaming.module.workflow.WorkflowUtils;
import org.kablink.teaming.portlet.forum.ViewController;
import org.kablink.teaming.portletadapter.AdaptedPortletURL;
import org.kablink.teaming.portletadapter.portlet.HttpServletRequestReachable;
import org.kablink.teaming.portletadapter.support.PortletAdapterUtil;
import org.kablink.teaming.runas.RunasCallback;
import org.kablink.teaming.runas.RunasTemplate;
import org.kablink.teaming.search.SearchUtils;
import org.kablink.teaming.search.filter.SearchFilter;
import org.kablink.teaming.search.filter.SearchFilterRequestParser;
import org.kablink.teaming.search.filter.SearchFilterToMapConverter;
import org.kablink.teaming.security.AccessControlException;
import org.kablink.teaming.ssfs.util.SsfsUtil;
import org.kablink.teaming.survey.Survey;
import org.kablink.teaming.util.AllModulesInjected;
import org.kablink.teaming.util.LongIdUtil;
import org.kablink.teaming.util.NLT;
import org.kablink.teaming.util.ReleaseInfo;
import org.kablink.teaming.util.ResolveIds;
import org.kablink.teaming.util.SimpleMultipartFile;
import org.kablink.teaming.util.SimpleProfiler;
import org.kablink.teaming.util.SpringContextUtil;
import org.kablink.teaming.util.Utils;
import org.kablink.teaming.util.XmlFileUtil;
import org.kablink.teaming.util.SPropsUtil;
import org.kablink.teaming.util.XmlUtil;
import org.kablink.teaming.web.WebKeys;
import org.kablink.teaming.web.tree.DomTreeBuilder;
import org.kablink.teaming.web.tree.DomTreeHelper;
import org.kablink.teaming.web.tree.WsDomTreeBuilder;
import org.kablink.teaming.web.util.FixupFolderDefsThread;
import org.kablink.teaming.domain.Definition;
import org.kablink.teaming.fi.connection.ResourceDriver;
import org.kablink.teaming.fi.connection.ResourceDriverManager;
import org.kablink.util.BrowserSniffer;
import org.kablink.util.Validator;
import org.kablink.util.search.Constants;
import org.kablink.util.search.Criteria;
import org.kablink.util.search.Restrictions;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.portlet.bind.PortletRequestBindingException;
import org.springframework.web.portlet.ModelAndView;

/**
 * ?
 * 
 * @author ?
 */
@SuppressWarnings({"unchecked", "unused"})
public class BinderHelper {
	protected static final Log logger = LogFactory.getLog(Binder.class);
	
	// The following control aspects of code in Vibe that has been
	// added to assist in debugging.
	public static final boolean BINDER_DEBUG_ENABLED = SPropsUtil.getBoolean("binders.debug.enabled", false);
	public static final String  BINDER_MAGIC_TITLE   = "create.binders.";

	// The following controls the default view entry style to use.
	private static final String DEFAULT_VDS_PROP	= (Utils.checkIfFilr() ? ObjectKeys.USER_DISPLAY_STYLE_DEFAULT : SPropsUtil.getString("vibe.default.view.display.style", ObjectKeys.USER_DISPLAY_STYLE_DEFAULT));
	private static final String DEFAULT_VDS;
	static {
		if ((!(DEFAULT_VDS_PROP.equals(ObjectKeys.USER_DISPLAY_STYLE_NEWPAGE))) && (!(DEFAULT_VDS_PROP.equals(ObjectKeys.USER_DISPLAY_STYLE_IFRAME))))
		     DEFAULT_VDS = ObjectKeys.USER_DISPLAY_STYLE_DEFAULT;
		else DEFAULT_VDS = DEFAULT_VDS_PROP;
	}
	
	public static final String RELEVANCE_DASHBOARD_PORTLET="ss_relevance_dashboard";
	public static final String BLOG_SUMMARY_PORTLET="ss_blog";
	public static final String FORUM_PORTLET="ss_forum";
	public static final String GALLERY_PORTLET="ss_gallery";
	public static final String GUESTBOOK_SUMMARY_PORTLET="ss_guestbook";
	public static final String TASK_SUMMARY_PORTLET="ss_task";
	public static final String MOBILE_PORTLET="ss_mobile";
	public static final String PRESENCE_PORTLET="ss_presence";
	public static final String SEARCH_PORTLET="ss_search";
	public static final String TOOLBAR_PORTLET="ss_toolbar";
	public static final String WIKI_PORTLET="ss_wiki";
	public static final String WORKSPACE_PORTLET="ss_workspacetree";
	public static final String WORKAREA_PORTLET="ss_workarea";
	public static final String WELCOME_PORTLET="ss_welcome";
	
	private static final String LOGOUT_SUCCESS_URL_MOBILE;
	private static final String AUTHENTICATION_FAILURE_URL_MOBILE;

	/**
	 * Inner class that returns information about an added mini blog
	 * entry.
	 */
	public static class MiniBlogInfo {
		private boolean m_newMiniBlogFolder;
		private Long    m_entryId;
		private Long    m_folderId;
		
		public MiniBlogInfo(Long entryId, Long folderId, boolean newMiniBlogFolder) {
			// Simply store the parameters.
			m_entryId           = entryId;
			m_folderId          = folderId;
			m_newMiniBlogFolder = newMiniBlogFolder;
		}
		
		public MiniBlogInfo() {
			// Nothing to do.
		}

		/**
		 * Get'er methods.
		 * 
		 * @return
		 */
		public boolean isNewMiniBlogFolder() {return m_newMiniBlogFolder;}
		public Long    getEntryId()          {return m_entryId;          }
		public Long    getFolderId()         {return m_folderId;         }

		/**
		 * Set'er methods.
		 * 
		 * @param newMiniBlogFolder
		 * @param entryId
		 * @param folderId
		 */
		public void setNewMiniBlogFolder(boolean newMiniBlogFolder) {m_newMiniBlogFolder = newMiniBlogFolder;}
		public void setEntryId(          Long    entryId)           {m_entryId           = entryId;          }
		public void setFolderId(         Long    folderId)          {m_folderId          = folderId;         }
	}
	
	static {
		String url = SPropsUtil.getString("mobile.spring.security.logout.success.url", "");
		if(Validator.isNull(url)) {
			url = null;
		}
		else {
			try {
				url = URLEncoder.encode(url, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				logger.debug("::BinderHelper(UnsupportedEncodingException):  1:  Ignored");
				url = null;
			}
		}
		LOGOUT_SUCCESS_URL_MOBILE = url;
		
		url = SPropsUtil.getString("mobile.spring.security.authentication.failure.url", "");
		if(Validator.isNull(url)) {
			url = null;
		}
		else {
			try {
				url = URLEncoder.encode(url, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				logger.debug("::BinderHelper(UnsupportedEncodingException):  2:  Ignored");
				url = null;
			}
		}
		AUTHENTICATION_FAILURE_URL_MOBILE = url;
	}
	
	static public ModelAndView CommonPortletDispatch(AllModulesInjected bs, RenderRequest request, 
			RenderResponse response) throws Exception {
		Map<String,Object> model = new HashMap<String,Object>();
 		model.put(WebKeys.WINDOW_STATE, request.getWindowState());
 		PortletPreferences prefs = null;
 		String ss_initialized = null;
 		try {
 			prefs = request.getPreferences();
 			ss_initialized = PortletPreferencesUtil.getValue(prefs, WebKeys.PORTLET_PREF_INITIALIZED, null);
 		} catch(Exception e) {
			logger.debug("BinderHelper.CommonPortletDispatch(Exception:  '" + MiscUtil.exToString(e) + "'):  1:  Ignored");
 			ss_initialized = "true";
 		}
 		
		if (Validator.isNull(ss_initialized)) {
			//Signal that this is the initialization step
			model.put(WebKeys.PORTLET_INITIALIZATION, "1");
			
			PortletURL url;
			//need action URL to set initialized flag in preferences
			url = response.createActionURL();
			model.put(WebKeys.PORTLET_INITIALIZATION_URL, url);
		}
		
		//Set up the standard beans
		setupStandardBeans(bs, request, response, model);
		
		String displayType = getDisplayType(request);
        User user = RequestContextHolder.getRequestContext().getUser();
		
		if (prefs != null) displayType = PortletPreferencesUtil.getValue(prefs, WebKeys.PORTLET_PREF_TYPE, null);
		if (Validator.isNull(displayType)) {
			displayType = getDisplayType(request);
		}
			
		getBinderAccessibleUrl(bs, null, null, request, response, model);

		if (FORUM_PORTLET.equals(displayType)) {
			//This is the portlet view; get the configured list of folders to show
			String[] preferredBinderIds = new String[0];
			if (prefs != null) preferredBinderIds = PortletPreferencesUtil.getValues(prefs, WebKeys.FORUM_PREF_FORUM_ID_LIST, new String[0]);

			//Build the jsp bean (sorted by folder title)
			List<Long> binderIds = new ArrayList<Long>();
			for (int i = 0; i < preferredBinderIds.length; i++) {
				binderIds.add(new Long(preferredBinderIds[i]));
			}
			//Get sub-binder list including intermediate binders that may be inaccessible
			model.put(WebKeys.FOLDER_LIST, bs.getBinderModule().getBinders(binderIds, Boolean.FALSE));
			try {
				response.setProperty(RenderResponse.EXPIRATION_CACHE,"300");
			} catch(Exception e) {
				logger.debug("BinderHelper.CommonPortletDispatch(Exception:  '" + MiscUtil.exToString(e) + "'):  2:  Ignored");
			}
			return new ModelAndView(WebKeys.VIEW_FORUM, model);
		} else if (WORKSPACE_PORTLET.equals(displayType) || WELCOME_PORTLET.equals(displayType)) {
			String id = null;
			if (prefs != null) id = PortletPreferencesUtil.getValue(prefs, WebKeys.WORKSPACE_PREF_ID, null);
			Workspace binder;
			try {
				binder = bs.getWorkspaceModule().getWorkspace(Long.valueOf(id));
			} catch (Exception ex) {
				logger.debug("BinderHelper.CommonPortletDispatch(Exception:  '" + MiscUtil.exToString(ex) + "'):  3:  Ignored");
				binder = bs.getWorkspaceModule().getTopWorkspace();				
			}
			Document wsTree;
			//when at the top, don't expand
			if (request.getWindowState().equals(WindowState.NORMAL) &&
					!WELCOME_PORTLET.equals(displayType)) {
				wsTree = bs.getBinderModule().getDomBinderTree(binder.getId(), new WsDomTreeBuilder(null, true, bs), 0);
			} else {
				wsTree = bs.getBinderModule().getDomBinderTree(binder.getId(), new WsDomTreeBuilder((Workspace)binder, true, bs), 1);									
			}
			model.put(WebKeys.WORKSPACE_DOM_TREE, wsTree);
			model.put(WebKeys.WORKSPACE_DOM_TREE_BINDER_ID, binder.getId().toString());
				
			if (WELCOME_PORTLET.equals(displayType)) {
				return new ModelAndView("help/welcome", model);
			} else {
				return new ModelAndView("workspacetree/view", model);
			}
		    
		} else if (PRESENCE_PORTLET.equals(displayType)) {
 			Set ids = new HashSet();		
 			if (prefs != null) {
 				ids.addAll(LongIdUtil.getIdsAsLongSet(PortletPreferencesUtil.getValue(prefs, WebKeys.PRESENCE_PREF_USER_LIST, "")));
 	 			ids.addAll(LongIdUtil.getIdsAsLongSet(PortletPreferencesUtil.getValue(prefs, WebKeys.PRESENCE_PREF_GROUP_LIST, "")));
 			}
 			if (ids.isEmpty()) {
 				//Initialize an empty presence list to have the current user as a buddy so there is always something to show
 				ids.add(user.getId());
 			}
 			//This is the portlet view; get the configured list of principals to show
 	        Comparator c = new PrincipalComparator(user.getLocale());
 	       	SortedSet<User> users = new TreeSet(c);
 	       	users.add(user);
 			try {
 				users = bs.getProfileModule().getUsersFromPrincipals(ids);
 			} catch(Exception e) {
 				logger.debug("BinderHelper.CommonPortletDispatch(Exception:  '" + MiscUtil.exToString(e) + "'):  4:  Ignored");
 			}
 			model.put(WebKeys.USERS, users);
 			//if we list groups, then we have issues when a user appears in multiple groups??
 			//how do we update the correct divs??
 			//so, explode the groups and just show members
			try {
				response.setProperty(RenderResponse.EXPIRATION_CACHE,"300");
			} catch(Exception e) {
				logger.debug("BinderHelper.CommonPortletDispatch(Exception:  '" + MiscUtil.exToString(e) + "'):  5:  Ignored");
			}
  			model.put(WebKeys.USER_LIST, LongIdUtil.getIdsAsString(ids));
  			return new ModelAndView(WebKeys.VIEW_PRESENCE, model);				
		} else if (TOOLBAR_PORTLET.equals(displayType)) {
			try {
				Workspace binder = bs.getWorkspaceModule().getTopWorkspace();
				Document wsTree;
				if (request.getWindowState().equals(WindowState.NORMAL)) {
					wsTree = bs.getBinderModule().getDomBinderTree(binder.getId(), new WsDomTreeBuilder(null, true, bs), 1);
				} else {
					wsTree = bs.getBinderModule().getDomBinderTree(binder.getId(), new WsDomTreeBuilder((Workspace)binder, true, bs), 1);									
				}
				model.put(WebKeys.WORKSPACE_DOM_TREE, wsTree);
				model.put(WebKeys.WORKSPACE_DOM_TREE_BINDER_ID, binder.getId().toString());
			} catch(AccessControlException e) {}
 			return new ModelAndView(WebKeys.VIEW_TOOLBAR, model);		
		} else if (RELEVANCE_DASHBOARD_PORTLET.equals(displayType)) {
			model.put(WebKeys.NAMESPACE, response.getNamespace());
	        if (PortletAdapterUtil.isRunByAdapter(request)) {
	        	String namespace = PortletRequestUtils.getStringParameter(request, WebKeys.URL_NAMESPACE, "");
	    		model.put(WebKeys.NAMESPACE, namespace);
	        }
	        Long binderId = PortletRequestUtils.getLongParameter(request, WebKeys.URL_BINDER_ID);
			//Get the dashboard initial tab if one was passed in
			String type = PortletRequestUtils.getStringParameter(request, WebKeys.URL_TYPE, "");
	        RelevanceDashboardHelper.setupRelevanceDashboardBeans(bs, request, response, 
	        		binderId, type, model);
	    	return new ModelAndView(WebKeys.VIEW_RELEVANCE_DASHBOARD, model); 		
		} else if (BLOG_SUMMARY_PORTLET.equals(displayType)) {
			return setupSummaryPortlets(bs, request, prefs, model, WebKeys.VIEW_BLOG_SUMMARY);		
		} else if (WIKI_PORTLET.equals(displayType)) {
			return setupSummaryPortlets(bs, request, prefs, model, WebKeys.VIEW_WIKI);		
		} else if (GUESTBOOK_SUMMARY_PORTLET.equals(displayType)) {
			return setupSummaryPortlets(bs, request, prefs, model, WebKeys.VIEW_GUESTBOOK_SUMMARY);		
		} else if (TASK_SUMMARY_PORTLET.equals(displayType)) {
			return setupSummaryPortlets(bs, request, prefs, model, WebKeys.VIEW_TASK_SUMMARY);		
		} else if (SEARCH_PORTLET.equals(displayType)) {
			return setupSummaryPortlets(bs, request, prefs, model, WebKeys.VIEW_SEARCH);		
		} else if (GALLERY_PORTLET.equals(displayType)) {
			return setupSummaryPortlets(bs, request, prefs, model, WebKeys.VIEW_GALLERY);		
		} else if (MOBILE_PORTLET.equals(displayType)) {
			return setupMobilePortlet(bs, request, response, prefs, model, WebKeys.VIEW_MOBILE);		
		} else if (WORKAREA_PORTLET.equals(displayType)) {
			return setupWorkareaPortlet(bs, request, response, prefs, model, WebKeys.VIEW_WORKAREA);		
		}

		return null;
	}
	
	public static void setupStandardBeans(AllModulesInjected bs, RenderRequest request, 
			RenderResponse response, Map<String,Object> model) {
		Long binderId = null;
		setupStandardBeans(bs, request, response, model, binderId);
		
	}
	public static void setupStandardBeans(AllModulesInjected bs, RenderRequest request, 
			RenderResponse response, Map<String,Object> model, Long binderId) {
		setupStandardBeans(bs, request, response, model, binderId, "ss_forum");
	}
	public static void setupStandardBeans(AllModulesInjected bs, RenderRequest request, 
			RenderResponse response, Map<String,Object> model, Long binderId, String portletName) {
		//Set up the standard beans
		//These have been documented, so don't delete any
		if (request != null) {
			String displayType = getDisplayType(request);
			model.put(WebKeys.DISPLAY_TYPE, displayType);
	 		model.put(WebKeys.WINDOW_STATE, request.getWindowState());
	        HttpSession session = ((HttpServletRequestReachable) request).getHttpServletRequest().getSession();
	        model.put(WebKeys.MAX_INACTIVE_INTERVAL, session.getMaxInactiveInterval());
			
	        //Get the url of the signaling code from the portal (if portal being used)
	        String portalSignalUrl = PortletRequestUtils.getStringParameter(request, WebKeys.URL_PORTAL_SIGNAL, "");
			if (!portalSignalUrl.equals("")) session.setAttribute(WebKeys.PORTAL_SIGNAL_URL, Boolean.valueOf(portalSignalUrl));
			portalSignalUrl = (String)session.getAttribute(WebKeys.PORTAL_SIGNAL_URL);
			if (portalSignalUrl == null) portalSignalUrl = "";
			model.put(WebKeys.PORTAL_SIGNAL_URL, portalSignalUrl);
			
			//See if captive mode is being set
			String s_captive = PortletRequestUtils.getStringParameter(request, WebKeys.URL_CAPTIVE, "");
			if (!s_captive.equals("")) session.setAttribute(WebKeys.CAPTIVE, Boolean.valueOf(s_captive));
			Boolean captive = false;
			if (session.getAttribute(WebKeys.CAPTIVE) != null) 
				captive = (Boolean)session.getAttribute(WebKeys.CAPTIVE);
			model.put(WebKeys.CAPTIVE, captive);
			
			String namespace = PortletRequestUtils.getStringParameter(request, WebKeys.URL_NAMESPACE, "");
			if (!namespace.equals("")) {
				model.put(WebKeys.NAMESPACE, namespace);
			} else {
				model.put(WebKeys.NAMESPACE, response.getNamespace());
			}
			AdaptedPortletURL loginUrl = new AdaptedPortletURL(request, portletName, true);
			loginUrl.setParameter(WebKeys.ACTION, WebKeys.ACTION_LOGIN); 
			model.put(WebKeys.LOGIN_URL, loginUrl.toString());
			String logoutUrl = WebUrlUtil.getServletRootURL(request) + WebKeys.SERVLET_LOGOUT;
			String loginPostUrl = WebUrlUtil.getServletRootURL(request) + WebKeys.SERVLET_LOGIN;
			if(MOBILE_PORTLET.equals(displayType) || MOBILE_PORTLET.equals(portletName)) {
				if(LOGOUT_SUCCESS_URL_MOBILE != null)
					logoutUrl += "?logoutSuccessUrl=" + LOGOUT_SUCCESS_URL_MOBILE;
				if(AUTHENTICATION_FAILURE_URL_MOBILE != null)
					loginPostUrl += "?authenticationFailureUrl=" + AUTHENTICATION_FAILURE_URL_MOBILE;
				//check and set cookie if is a native app
				setupMobileCookie(session, request, response, model);
			}
			model.put(WebKeys.MOBILE_URL, SsfsUtil.getMobileUrl(request));		
			model.put(WebKeys.MOBILE_ACCESS_ENABLED, bs.getAdminModule().isMobileAccessEnabled());
			model.put(WebKeys.LOGOUT_URL, logoutUrl);
			model.put(WebKeys.LOGIN_POST_URL, loginPostUrl);
			model.put(WebKeys.IS_BINDER_MIRRORED_FOLDER, false);
			if (binderId == null) {
				getBinderAccessibleUrl(bs, null, null, request, response, model);
			} else {
				try {
					Binder binder = bs.getBinderModule().getBinder(binderId);
					getBinderAccessibleUrl(bs, binder, null, request, response, model);
					if (binder instanceof Folder) {
						model.put(WebKeys.IS_BINDER_MIRRORED_FOLDER, ((Folder)binder).isMirrored());
					}
					model.put(WebKeys.BINDER_READ_ENTRIES, bs.getBinderModule().testAccess(null, binder, BinderOperation.readEntries, Boolean.TRUE));
					if (SPropsUtil.getBoolean("accessControl.viewBinderTitle.enabled", false)) {
						model.put(WebKeys.BINDER_VIEW_BINDER_TITLE, bs.getBinderModule().testAccess(null, binder, BinderOperation.viewBinderTitle, Boolean.TRUE));
					}
					
					// Add information about whether the binder has team members
					model.put( WebKeys.BINDER_HAS_TEAM_MEMBERS, bs.getBinderModule().doesBinderHaveTeamMembers( binder ) );
					
				} catch(Exception e) {
					logger.debug("BinderHelper.setupStandardBeans(Exception:  '" + MiscUtil.exToString(e) + "')");
					getBinderAccessibleUrl(bs, null, null, request, response, model);
				}
			}
		}
		User user = null;
		model.put(WebKeys.USER_PROPERTIES, new HashMap());
		if (RequestContextHolder.getRequestContext() != null) {
        	user = RequestContextHolder.getRequestContext().getUser();
    		if (user != null) {
    			UserProperties userProperties = bs.getProfileModule().getUserProperties(user.getId());
        		model.put(WebKeys.USER_PRINCIPAL, user);
        		SeenMap seen = bs.getProfileModule().getUserSeenMap(null);
        		model.put(WebKeys.SEEN_MAP, seen);

        		if (userProperties.getProperties() != null) {
        			model.put(WebKeys.USER_PROPERTIES, userProperties.getProperties());
        		} else {
        			model.put(WebKeys.USER_PROPERTIES, new HashMap());
        		}
        		model.put(WebKeys.USER_PROPERTIES_OBJ, userProperties);
    		}
        }
		model.put(WebKeys.PORTAL_URL, getPortalUrl(bs));
		if (binderId != null) {
			model.put(WebKeys.BINDER_ID, binderId.toString());
			if (user != null) {
				UserProperties userFolderProperties = bs.getProfileModule().getUserProperties(user.getId(), binderId);
				if (userFolderProperties == null) userFolderProperties = new UserProperties(user.getId(), binderId);
				Object ufp = userFolderProperties.getProperties();
				if (ufp == null) ufp = new HashMap();
				model.put(WebKeys.USER_FOLDER_PROPERTIES, ufp);
				model.put(WebKeys.USER_FOLDER_PROPERTIES_OBJ, userFolderProperties);
				
				Boolean brandingAccess = true;
				Binder binder = null;
				try {
					binder = bs.getBinderModule().getBinder(binderId);
					brandingAccess = bs.getBinderModule().checkAccess(binder.getBrandingSource().getId(), user);
				} catch(AccessControlException e) {
					brandingAccess = false;
				} catch(Exception e) {}
				model.put(WebKeys.BRANDING_BINDER, binder);
				model.put(WebKeys.ACCESS_BRANDING, brandingAccess);
			}
		} else {
			Boolean brandingAccess = true;
			Binder topBinder = null;
			try {
				topBinder = bs.getWorkspaceModule().getTopWorkspace();
				brandingAccess = bs.getBinderModule().checkAccess(topBinder.getBrandingSource().getId(), user);
			} catch(AccessControlException e) {
				try {
					topBinder = bs.getWorkspaceModule().getWorkspace(user.getWorkspaceId());
				} catch(Exception e2) {
					brandingAccess = false;					
				}
			} catch(Exception e) {}
			model.put(WebKeys.BRANDING_BINDER, topBinder);
			model.put(WebKeys.ACCESS_BRANDING, brandingAccess);
		}
		if ("standalone".equals(SPropsUtil.getString("deployment.portal"))) {
			model.put((WebKeys.STAND_ALONE), true);
		} else {
			model.put((WebKeys.STAND_ALONE), false);
		}

		model.put(WebKeys.QUOTAS_ENABLED, bs.getAdminModule().isQuotaEnabled());
		model.put(WebKeys.QUOTAS_BINDER_ENABLED, bs.getAdminModule().isBinderQuotaEnabled());
		model.put(WebKeys.DISK_QUOTA_EXCEEDED, bs.getProfileModule().isDiskQuotaExceeded());
		model.put(WebKeys.DISK_QUOTA_HIGH_WATER_MARK_EXCEEDED, bs.getProfileModule().isDiskQuotaHighWaterMarkExceeded());
		model.put(WebKeys.DISK_QUOTA_USER_MAXIMUM, bs.getProfileModule().getMaxUserQuota());
		model.put(WebKeys.EFFECTIVE_FILE_LINK_ACTION, AdminHelper.getEffectiveFileLinkAction(bs).name());
		
		model.put(WebKeys.PRODUCT_NAME, SPropsUtil.getString("product.name", ObjectKeys.PRODUCT_NAME_DEFAULT));
		model.put(WebKeys.PRODUCT_TITLE, SPropsUtil.getString("product.title", ObjectKeys.PRODUCT_TITLE_DEFAULT));
		model.put(WebKeys.PRODUCT_CONFERENCING_NAME, SPropsUtil.getString("product.conferencing.name", ObjectKeys.PRODUCT_CONFERENCING_NAME_DEFAULT));
		model.put(WebKeys.PRODUCT_CONFERENCING_TITLE, SPropsUtil.getString("product.conferencing.title", ObjectKeys.PRODUCT_CONFERENCING_TITLE_DEFAULT));
		model.put("releaseInfo", ReleaseInfo.getReleaseInfo());
		Long fileVersionMaxAge = bs.getAdminModule().getFileVersionsMaxAge();
		model.put(WebKeys.ZONE_VERSION_AGING_DAYS, fileVersionMaxAge);
	}
	
	//Set up the beans needed to warn people of quota issues
	public static void setupBinderQuotaBeans(AllModulesInjected bs, RenderRequest request, 
			RenderResponse response, Map<String,Object> model, Long binderId) {
		try {
			Binder binder = bs.getBinderModule().getBinder(binderId);
			model.put((WebKeys.BINDER_QUOTAS_ENABLED), bs.getAdminModule().isBinderQuotaEnabled());
			model.put((WebKeys.BINDER_QUOTAS_EXCEEDED), bs.getBinderModule().isBinderDiskQuotaExceeded(binder));
			model.put((WebKeys.BINDER_QUOTAS_HIGH_WATER_MARK_EXCEEDED), bs.getBinderModule().isBinderDiskHighWaterMarkExceeded(binder));
			model.put((WebKeys.BINDER_QUOTAS_MIN_QUOTA_LEFT), bs.getBinderModule().getMinBinderQuotaLeft(binder));
			model.put((WebKeys.BINDER_QUOTAS_MIN_QUOTA_LEFT_BINDER), bs.getBinderModule().getMinBinderQuotaLeftBinder(binder));
		} catch(Exception e) {
			//We don't really need these beans, so if there is any problem just exit
		}
	}
	
	public static Document getSearchFilter(AllModulesInjected bs, Binder binder, UserProperties userFolderProperties, boolean unescapeName) {
		convertV1Filters(bs, userFolderProperties);  //make sure converted
		//Determine the Search Filter
		String searchFilterName = (String)userFolderProperties.getProperty(ObjectKeys.USER_PROPERTY_USER_FILTER);
		String searchFilterScope = (String)userFolderProperties.getProperty(ObjectKeys.USER_PROPERTY_USER_FILTER_SCOPE);
		Document searchFilter = null;
		if (Validator.isNotNull(searchFilterName)) {
			Map searchFilters = null;
			if (!ObjectKeys.USER_PROPERTY_USER_FILTER_GLOBAL.equals(searchFilterScope)) {
				searchFilters = (Map) userFolderProperties.getProperty(ObjectKeys.USER_PROPERTY_SEARCH_FILTERS);
			} else {
				searchFilters = (Map)binder.getProperty(ObjectKeys.BINDER_PROPERTY_FILTERS);
			}
			if (searchFilters != null) {
				if (unescapeName) {
					searchFilterName = MiscUtil.replace(searchFilterName, "+", " ");
				}
				String searchFilterStr = (String)searchFilters.get(searchFilterName);
				if (Validator.isNotNull(searchFilterStr)) {
					try {
						searchFilter = XmlUtil.parseText(searchFilterStr);
					} catch (Exception ignore) {
						//get rid of it
						logger.debug("BinderHelper.getSearchFilter(Exception:  '" + MiscUtil.exToString(ignore) + "')");
						searchFilters.remove(searchFilterStr);
						bs.getProfileModule().setUserProperty(userFolderProperties.getId().getPrincipalId(), userFolderProperties.getId().getBinderId(), ObjectKeys.USER_PROPERTY_SEARCH_FILTERS, searchFilters);
					}
				}
			}
		}		
		return searchFilter;
	}
	public static Document getSearchFilter(AllModulesInjected bs, Binder binder, UserProperties userFolderProperties) {
		return getSearchFilter(bs, binder, userFolderProperties, false);
	}
	public static Map convertV1Filters(AllModulesInjected bs, UserProperties userFolderProperties) {
		//see if any v1 filters to convert. 
		//where stored as dom objects which cause unnecessary hibernate updates cause .equals doesn't work
		Map v2SearchFilters = (Map)userFolderProperties.getProperty(ObjectKeys.USER_PROPERTY_SEARCH_FILTERS_V1);
		if (v2SearchFilters == null) return new HashMap();
		if (v2SearchFilters.isEmpty()) return v2SearchFilters;
		Map searchFilters = new HashMap();
		for (Iterator iter=v2SearchFilters.entrySet().iterator(); iter.hasNext();) {
			try {
				Map.Entry me = (Map.Entry)iter.next();
				searchFilters.put(me.getKey(), ((Document)me.getValue()).asXML());				
			} catch (Exception ignore) {
				logger.debug("BinderHelper.convertV1Filters(Exception:  '" + MiscUtil.exToString(ignore) + "'):  Ignored");
			};
		}
		bs.getProfileModule().setUserProperty(userFolderProperties.getId().getPrincipalId(), userFolderProperties.getId().getBinderId(), ObjectKeys.USER_PROPERTY_SEARCH_FILTERS, searchFilters);
		bs.getProfileModule().setUserProperty(userFolderProperties.getId().getPrincipalId(), userFolderProperties.getId().getBinderId(), ObjectKeys.USER_PROPERTY_SEARCH_FILTERS_V1, null);
		return searchFilters;
	}
	protected static ModelAndView setupSummaryPortlets(AllModulesInjected bs, RenderRequest request, PortletPreferences prefs, Map model, String view) {
		String gId = null;
		if (prefs != null) gId = PortletPreferencesUtil.getValue(prefs, WebKeys.PORTLET_PREF_DASHBOARD, null);
		if (gId != null) {
			try {
				Map userProperties = (Map) model.get(WebKeys.USER_PROPERTIES);
				DashboardPortlet d = (DashboardPortlet)bs.getDashboardModule().getDashboard(gId);
				model.put(WebKeys.DASHBOARD_PORTLET, d);
				if (request.getWindowState().equals(WindowState.MAXIMIZED))
					model.put(WebKeys.PAGE_SIZE, "20");
				else
					model.put(WebKeys.PAGE_SIZE, "5");						
				DashboardHelper.getDashboardMap(d, userProperties, model, false);
				return new ModelAndView(view, model);		
			} catch (NoObjectByTheIdException no) {
				logger.debug("BinderHelper.setupSummaryPortlets(NoObjectByTheIdException):  Ignored");
			}
		}
		return new ModelAndView(WebKeys.VIEW_NOT_CONFIGURED);
		
	}

	protected static ModelAndView setupMobilePortlet(AllModulesInjected bs, RenderRequest request, 
			RenderResponse response, PortletPreferences prefs, Map model, String view) {
		view = setupMobileFrontPageBeans(bs, request, response, model, view);

		return new ModelAndView(view, model);
	}

	@SuppressWarnings("deprecation")
	public static String setupMobileFrontPageBeans(AllModulesInjected bs, RenderRequest request, 
			RenderResponse response, Map model, String view) {
        User user = RequestContextHolder.getRequestContext().getUser();
		if (!WebHelper.isUserLoggedIn(request) || user.isShared()) {
	        HttpSession session = ((HttpServletRequestReachable) request).getHttpServletRequest().getSession();
	    	AuthenticationException ex = (AuthenticationException) session.getAttribute(AbstractAuthenticationProcessingFilter.SPRING_SECURITY_LAST_EXCEPTION_KEY);
	    	if(ex != null) {
	    		model.put(WebKeys.LOGIN_ERROR, ex.getMessage());
	    		session.removeAttribute(AbstractAuthenticationProcessingFilter.SPRING_SECURITY_LAST_EXCEPTION_KEY);

				if ( ex instanceof TextVerificationException )
	    		{
					// Either the user entered an invalid captcha response or we have detected
					// a brute-force authentication attack.  Either way require captcha on the login dialog.
					model.put( "ssDoTextVerification", "true" );
	    		}
	    	}
			AdaptedPortletURL adapterUrl = new AdaptedPortletURL(request, "ss_mobile", true);
			adapterUrl.setParameter(WebKeys.ACTION, WebKeys.ACTION_MOBILE_AJAX);
			adapterUrl.setParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_MOBILE_SHOW_FRONT_PAGE);
			if (model.containsKey(WebKeys.URL_OPERATION2)) {
				adapterUrl.setParameter(WebKeys.URL_OPERATION2, (String)model.get(WebKeys.URL_OPERATION2));
			}
			model.put(WebKeys.URL, adapterUrl);
			if (bs.getAdminModule().isMobileAccessEnabled()) {
				return "mobile/show_login_form";
			} else {
				return "mobile/not_supported";
			}
		}
		Map userProperties = (Map) bs.getProfileModule().getUserProperties(user.getId()).getProperties();
		if (userProperties == null) userProperties = new HashMap();
		Long binderId = user.getWorkspaceId();
		Binder topBinder = null;
		try {
			if (binderId == null) binderId = bs.getWorkspaceModule().getTopWorkspace().getId();
			topBinder = bs.getWorkspaceModule().getTopWorkspace();
		} catch(Exception e) {}
		Binder myWorkspaceBinder = bs.getBinderModule().getBinder(user.getWorkspaceId());
		Binder binder = null;
		try {
			binder = bs.getBinderModule().getBinder(binderId);
		} catch(Exception e) {}
		setupStandardBeans(bs, request, response, model, binderId, "ss_mobile");
		model.put(WebKeys.BINDER, binder);
		model.put(WebKeys.TOP_WORKSPACE, topBinder);
		
		setupMobileSearchBeans(bs, request, response, model);

		String type = (String)userProperties.get(ObjectKeys.USER_PROPERTY_MOBILE_WHATS_NEW_TYPE);
		if (type == null || type.equals("")) type = ObjectKeys.MOBILE_WHATS_NEW_VIEW_SITE;
		model.put("ss_whatsNewType", type);
      	Integer pageNumber = PortletRequestUtils.getIntParameter(request, WebKeys.URL_PAGE_NUMBER, 0);
      	if (pageNumber == null || pageNumber < 0) pageNumber = 0;
      	int pageSize = SPropsUtil.getInt("relevance.mobile.whatsNewPageSize");
      	int pageStart = pageNumber.intValue() * pageSize;
      	int pageEnd = pageStart + pageSize;
      	String nextPage = "";
      	String prevPage = "";
		Map options = new HashMap();		
      	options.put(ObjectKeys.SEARCH_MAX_HITS, Integer.valueOf(pageSize));
      	options.put(ObjectKeys.SEARCH_OFFSET, Integer.valueOf(pageStart));
		model.put(WebKeys.PAGE_NUMBER, pageNumber.toString());
		model.put(WebKeys.NEXT_PAGE, nextPage);
		model.put(WebKeys.PREV_PAGE, prevPage);
		model.put(WebKeys.PAGE_ENTRIES_PER_PAGE, (Integer) options.get(ObjectKeys.SEARCH_MAX_HITS));

		if (topBinder != null && (type.equals(ObjectKeys.MOBILE_WHATS_NEW_VIEW_TRACKED) || 
				type.equals(ObjectKeys.MOBILE_WHATS_NEW_VIEW_FAVORITES) ||
				type.equals(ObjectKeys.MOBILE_WHATS_NEW_VIEW_TEAMS) ||
				type.equals(ObjectKeys.MOBILE_WHATS_NEW_VIEW_SITE))) {
			setupWhatsNewBinderBeans(bs, myWorkspaceBinder, topBinder.getId(), model, 
					String.valueOf(pageNumber), Integer.valueOf(pageSize), type);
		} else if (type.equals(ObjectKeys.MOBILE_WHATS_NEW_VIEW_MICROBLOG)) {
			RelevanceDashboardHelper.setupMiniblogsBean(bs, myWorkspaceBinder, model);
		}
      	//Get the total records found by the search
      	Integer totalRecords = (Integer)model.get(WebKeys.SEARCH_TOTAL_HITS);
      	if (totalRecords == null) totalRecords = 0;
      	//Get the records returned (which may be more than the page size)
      	if (totalRecords.intValue() < pageStart) {
      		if (pageNumber > 0) prevPage = String.valueOf(pageNumber - 1);
      	} else if (totalRecords.intValue() >= pageEnd) {
      		nextPage = String.valueOf(pageNumber + 1);
      		if (pageNumber > 0) prevPage = String.valueOf(pageNumber - 1);
      	} else {
      		if (pageNumber > 0) prevPage = String.valueOf(pageNumber - 1);
      	}
		model.put(WebKeys.TAB_ID, String.valueOf(model.get(WebKeys.URL_TAB_ID)));
		model.put(WebKeys.PAGE_NUMBER, pageNumber);
		model.put(WebKeys.NEXT_PAGE, nextPage);
		model.put(WebKeys.PREV_PAGE, prevPage);
		
		//Setup the actions menu list
		List actions = new ArrayList();
		//addActionsHome(request, actions);
		addActionsWhatsNew(request, actions, null);
		addActionsWhatsUnseen(request, actions, null);
		addActionsRecentPlaces(request, actions, user.getWorkspaceId());
		addActionsSpacer(request, actions);
		addActionsLogout(request, actions);
		addActionsFullView(bs, request, actions, binderId, null);
		model.put("ss_actions", actions);
		
		return view;
	}
	
	public static void setupMobileSearchBeans(AllModulesInjected bs, RenderRequest request, 
			RenderResponse response, Map model) {
        User user = RequestContextHolder.getRequestContext().getUser();
		Map userProperties = (Map) bs.getProfileModule().getUserProperties(user.getId()).getProperties();
		Long binderId = user.getWorkspaceId();
		if (binderId == null) binderId = bs.getWorkspaceModule().getTopWorkspace().getId();
		try {
			Binder binder = bs.getBinderModule().getBinder(binderId);
			Tabs.TabEntry tab;
			try {
				tab = initTabs(request, binder);
				model.put(WebKeys.TABS, tab.getTabs());		
			} catch (Exception e1) {}
		} catch(Exception e) {}

		Map userQueries = new HashMap();
		if (userProperties != null && userProperties.containsKey(ObjectKeys.USER_PROPERTY_SAVED_SEARCH_QUERIES)) {
			userQueries = (Map)userProperties.get(ObjectKeys.USER_PROPERTY_SAVED_SEARCH_QUERIES);
		}
		model.put("ss_UserQueries", userQueries);

		Map accessControlMap = getAccessControlMapBean(model);
		ProfileBinder profileBinder = null;
		try {
			profileBinder = bs.getProfileModule().getProfileBinder();
		} catch(Exception e) {}
		if (profileBinder != null) {
			accessControlMap.put(WebKeys.CAN_VIEW_USER_PROFILES, true);
		}

		if (userProperties != null) {
			Object obj = userProperties.get(ObjectKeys.USER_PROPERTY_FAVORITES);
			Favorites f;
			if (obj != null && obj instanceof Document) {
				f = new Favorites((Document)obj);
				//fixup - have to store as string cause hibernate equals fails
				bs.getProfileModule().setUserProperty(null, ObjectKeys.USER_PROPERTY_FAVORITES, f.toString());
			} else {		
				f = new Favorites((String)obj);
			}
			List<Map> favList = f.getFavoritesList();
			model.put(WebKeys.MOBILE_FAVORITES_LIST, favList);
		}
	}
	
	public static void setupMobileCookie(HttpSession session, RenderRequest request, RenderResponse response, Map model) {
		//See if this is a native app
		String s_nativeMobile = PortletRequestUtils.getStringParameter(request, WebKeys.URL_NATIVE_MOBILE_APP, "");
		if (!s_nativeMobile.equals("")) session.setAttribute(WebKeys.URL_NATIVE_MOBILE_APP, Boolean.valueOf(s_nativeMobile));
		Boolean nativeMobile = false;
		if (session.getAttribute(WebKeys.URL_NATIVE_MOBILE_APP) != null) 
			nativeMobile = (Boolean)session.getAttribute(WebKeys.URL_NATIVE_MOBILE_APP);
		model.put(WebKeys.URL_NATIVE_MOBILE_APP, nativeMobile);
		if(nativeMobile) {
			boolean addCookie = true;
			Cookie[] cookies = request.getCookies();
			if(cookies != null) {
				for(Cookie cookie:cookies) {
					if(cookie.getName().equals(WebKeys.URL_NATIVE_MOBILE_APP_COOKIE)) {
						addCookie = false;
						break;
					}
				}
			}
			if(addCookie) {
				String op = (String)model.get(WebKeys.URL_OPERATION2);
				Cookie newCookie = new Cookie(WebKeys.URL_NATIVE_MOBILE_APP_COOKIE,op);
				newCookie.setPath("/ssf");
				newCookie.setMaxAge(21278567);
				response.addProperty(newCookie);
			}
		}
	}

	public static String setupTeamingLiveBeans(AllModulesInjected bs, RenderRequest request, 
			RenderResponse response, Map model, String view) throws AccessControlException {
        User user = RequestContextHolder.getRequestContext().getUser();
		if (!WebHelper.isUserLoggedIn(request) || user.isShared()) {
			AdaptedPortletURL adapterUrl = new AdaptedPortletURL(request, "ss_forum", true);
			adapterUrl.setParameter(WebKeys.ACTION, WebKeys.ACTION_MOBILE_AJAX);
			adapterUrl.setParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_VIEW_TEAMING_LIVE);
			model.put(WebKeys.URL, adapterUrl);
			return "mobile/show_teaming_live_login_form";
		}
        HttpSession session = ((HttpServletRequestReachable) request).getHttpServletRequest().getSession();
		Map userProperties = (Map) bs.getProfileModule().getUserProperties(user.getId()).getProperties();
		Long topBinderId = bs.getWorkspaceModule().getTopWorkspaceId();
		Binder topBinder = null;
		try {
			topBinder = bs.getWorkspaceModule().getTopWorkspace();
		} catch(AccessControlException e) {}
		Long binderId = user.getWorkspaceId();
		if (binderId == null && topBinder != null) binderId = topBinder.getId();
		Binder myWorkspaceBinder = null;
		try {
			myWorkspaceBinder = bs.getBinderModule().getBinder(user.getWorkspaceId());
		} catch(AccessControlException e) {}
		Binder binder = null;
		try {
			if (binderId != null) binder = bs.getBinderModule().getBinder(binderId);
		} catch(AccessControlException e) {}
		setupStandardBeans(bs, request, response, model, binderId, "ss_forum");
		model.put(WebKeys.BINDER, binder);
		model.put(WebKeys.TOP_WORKSPACE, topBinder);

		String type = (String)userProperties.get(ObjectKeys.USER_PROPERTY_TEAMING_LIVE_WHATS_NEW_TYPE);
		if (type == null || type.equals("")) type = ObjectKeys.MOBILE_WHATS_NEW_VIEW_TEAMS;
		model.put("ss_whatsNewType", type);
      	Integer pageNumber = PortletRequestUtils.getIntParameter(request, WebKeys.URL_PAGE_NUMBER, 0);
      	if (pageNumber == null || pageNumber < 0) pageNumber = 0;
      	int pageSize = SPropsUtil.getInt("relevance.mobile.whatsNewPageSize");
      	int pageStart = pageNumber.intValue() * pageSize;
      	int pageEnd = pageStart + pageSize;
      	String nextPage = "";
      	String prevPage = "";
		Map options = new HashMap();		
      	options.put(ObjectKeys.SEARCH_MAX_HITS, Integer.valueOf(pageSize));
      	options.put(ObjectKeys.SEARCH_OFFSET, Integer.valueOf(pageStart));
		model.put(WebKeys.PAGE_NUMBER, pageNumber.toString());
		model.put(WebKeys.NEXT_PAGE, nextPage);
		model.put(WebKeys.PREV_PAGE, prevPage);
		model.put(WebKeys.PAGE_ENTRIES_PER_PAGE, (Integer) options.get(ObjectKeys.SEARCH_MAX_HITS));

		Set<Long> trackedBinders = new HashSet<Long>();
		if (type.equals(ObjectKeys.MOBILE_WHATS_NEW_VIEW_TRACKED) || 
				type.equals(ObjectKeys.MOBILE_WHATS_NEW_VIEW_TEAMS) || 
				type.equals(ObjectKeys.MOBILE_WHATS_NEW_VIEW_SITE)) {
			List<Long> tbs = setupWhatsNewBinderBeans(bs, myWorkspaceBinder, topBinderId, model, 
					String.valueOf(pageNumber), Integer.valueOf(pageSize), type);
			for (Long bId : tbs) {
				if (!trackedBinders.contains(bId)) {
					trackedBinders.add(bId);
				}
	    	}
		} else if (type.equals(ObjectKeys.MOBILE_WHATS_NEW_VIEW_MICROBLOG)) {
			List<Long> trackedPeople = RelevanceDashboardHelper.setupMiniblogsBean(bs, myWorkspaceBinder, model);
			Criteria crit = SearchUtils.bindersForTrackedMiniBlogs(trackedPeople);
			Map results = bs.getBinderModule().executeSearchQuery(crit, Constants.SEARCH_MODE_NORMAL, 0, 10000,
					org.kablink.teaming.module.shared.SearchUtils.fieldNamesList(Constants.DOCID_FIELD));
	    	List items = (List) results.get(ObjectKeys.SEARCH_ENTRIES);
	    	if (items != null) {
		    	Iterator it = items.iterator();
		    	while (it.hasNext()) {
		    		Map entry = (Map)it.next();
					String id = (String)entry.get(Constants.DOCID_FIELD);
					if (id != null) {
						Long bId = Long.valueOf(id);
						if (!trackedBinders.contains(bId)) {
							trackedBinders.add(bId);
						}
					}
		    	}
	    	}
		}
		session.setAttribute(ObjectKeys.SESSION_TEAMING_LIVE_TRACKED_BINDER_IDS, trackedBinders);
		session.setAttribute(ObjectKeys.SESSION_TEAMING_LIVE_TRACKED_TYPE, type);
      	//Get the total records found by the search
      	Integer totalRecords = (Integer)model.get(WebKeys.SEARCH_TOTAL_HITS);
      	//Get the records returned (which may be more than the page size)
      	if (totalRecords.intValue() < pageStart) {
      		if (pageNumber > 0) prevPage = String.valueOf(pageNumber - 1);
      	} else if (totalRecords.intValue() >= pageEnd) {
      		nextPage = String.valueOf(pageNumber + 1);
      		if (pageNumber > 0) prevPage = String.valueOf(pageNumber - 1);
      	} else {
      		if (pageNumber > 0) prevPage = String.valueOf(pageNumber - 1);
      	}
		model.put(WebKeys.PAGE_NUMBER, pageNumber);
		model.put(WebKeys.NEXT_PAGE, nextPage);
		model.put(WebKeys.PREV_PAGE, prevPage);

		return view;
	}

	//Routines to add mobile actions
	public static void addActionsHome(RenderRequest request, List actions) {
		Map action = new HashMap();
		action.put("title", NLT.get("mobile.goHome"));
		AdaptedPortletURL adapterUrl = new AdaptedPortletURL(request, "ss_mobile", true);
		adapterUrl.setParameter(WebKeys.ACTION, WebKeys.ACTION_MOBILE_AJAX);
		adapterUrl.setParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_MOBILE_SHOW_FRONT_PAGE);
		action.put("url", adapterUrl.toString());
		actions.add(action);
	}

	public static void addActionsRecentPlaces(RenderRequest request, List actions, Long binderId) {
		Map action = new HashMap();
		action.put("title", NLT.get("sidebar.history"));
		AdaptedPortletURL adapterUrl = new AdaptedPortletURL(request, "ss_mobile", true);
		adapterUrl.setParameter(WebKeys.ACTION, WebKeys.ACTION_MOBILE_AJAX);
		if (binderId != null) adapterUrl.setParameter(WebKeys.URL_BINDER_ID, binderId.toString());
		adapterUrl.setParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_MOBILE_SHOW_RECENT_PLACES);
		action.put("url", adapterUrl.toString());
		actions.add(action);
	}

	public static void addActionsTrackThisBinder(AllModulesInjected bs, RenderRequest request, 
			List actions, Binder binder) {
		if (binder == null) return;
		boolean isTracked = isBinderTracked(bs, binder.getId());
		Map action = new HashMap();
		String type = "add";
		if (isTracked) {
			if (binder.getEntityType().name().equals(EntityType.workspace.name())) {
				if (Integer.valueOf(Definition.USER_WORKSPACE_VIEW).equals(binder.getDefinitionType()) ||
						Integer.valueOf(Definition.EXTERNAL_USER_WORKSPACE_VIEW).equals(binder.getDefinitionType())) {
					action.put("title", NLT.get("relevance.trackThisPersonNot"));
				} else {
					action.put("title", NLT.get("relevance.trackThisWorkspaceNot"));
				}
			} else if (binder.getEntityType().name().equals(EntityType.folder.name())) {
				action.put("title", NLT.get("relevance.trackThisFolderNot"));
			} else {
				action.put("title", NLT.get("relevance.trackThisNot"));
			}
			type = "delete";
		} else {
			if (binder.getEntityType().name().equals(EntityType.workspace.name())) {
				if (Integer.valueOf(Definition.USER_WORKSPACE_VIEW).equals(binder.getDefinitionType()) ||
						Integer.valueOf(Definition.EXTERNAL_USER_WORKSPACE_VIEW).equals(binder.getDefinitionType())) {
					action.put("title", NLT.get("relevance.trackThisPerson"));
				} else {
					action.put("title", NLT.get("relevance.trackThisWorkspace"));
				}
			} else if (binder.getEntityType().name().equals(EntityType.folder.name())) {
				action.put("title", NLT.get("relevance.trackThisFolder"));
			} else {
				action.put("title", NLT.get("relevance.trackThis"));
			}
		}
		AdaptedPortletURL adapterUrl = new AdaptedPortletURL(request, "ss_mobile", true);
		adapterUrl.setParameter(WebKeys.ACTION, WebKeys.ACTION_MOBILE_AJAX);
		adapterUrl.setParameter(WebKeys.URL_BINDER_ID, binder.getId().toString());
		adapterUrl.setParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_MOBILE_TRACK_THIS);
		adapterUrl.setParameter(WebKeys.URL_TYPE, type);
		action.put("url", adapterUrl.toString());
		actions.add(action);
	}

	public static void addActionsWhatsNew(RenderRequest request, List actions, Binder binder) {
		Map action = new HashMap();
		if (binder == null) {
			action.put("title", NLT.get("mobile.whatsNewSiteWide"));
		} else if (binder.getEntityType().name().equals(EntityType.workspace.name())) {
			action.put("title", NLT.get("mobile.whatsNewWorkspace"));
		} else if (binder.getEntityType().name().equals(EntityType.folder.name())) {
			action.put("title", NLT.get("mobile.whatsNewFolder"));
		} else if (binder.getEntityType().name().equals(EntityType.profiles.name())) {
			action.put("title", NLT.get("mobile.whatsNewWorkspace"));
		} else {
			action.put("title", NLT.get("mobile.whatsNew"));
		}
		AdaptedPortletURL adapterUrl = new AdaptedPortletURL(request, "ss_mobile", true);
		adapterUrl.setParameter(WebKeys.ACTION, WebKeys.ACTION_MOBILE_AJAX);
		if (binder != null) adapterUrl.setParameter(WebKeys.URL_BINDER_ID, binder.getId().toString());
		adapterUrl.setParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_MOBILE_WHATS_NEW);
		adapterUrl.setParameter(WebKeys.URL_TYPE, WebKeys.URL_WHATS_NEW);
		action.put("url", adapterUrl.toString());
		actions.add(action);
	}

	public static void addActionsWhatsUnseen(RenderRequest request, List actions, Binder binder) {
		Map action = new HashMap();
		if (binder == null) {
			action.put("title", NLT.get("mobile.whatsUnreadSiteWide"));
		} else if (binder.getEntityType().name().equals(EntityType.workspace.name())) {
			action.put("title", NLT.get("mobile.whatsUnreadWorkspace"));
		} else if (binder.getEntityType().name().equals(EntityType.folder.name())) {
			action.put("title", NLT.get("mobile.whatsUnreadFolder"));
		} else if (binder.getEntityType().name().equals(EntityType.profiles.name())) {
			action.put("title", NLT.get("mobile.whatsUnreadWorkspace"));
		} else {
			action.put("title", NLT.get("mobile.whatsUnread"));
		}
		AdaptedPortletURL adapterUrl = new AdaptedPortletURL(request, "ss_mobile", true);
		adapterUrl.setParameter(WebKeys.ACTION, WebKeys.ACTION_MOBILE_AJAX);
		if (binder != null) adapterUrl.setParameter(WebKeys.URL_BINDER_ID, binder.getId().toString());
		adapterUrl.setParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_MOBILE_WHATS_NEW);
		adapterUrl.setParameter(WebKeys.URL_TYPE, WebKeys.URL_UNSEEN);
		action.put("url", adapterUrl.toString());
		actions.add(action);
	}

	public static void addActionsLogout(RenderRequest request, List actions) {
		
		if(MiscUtil.isNativeMobileApp(request)) 
			return;
		
		addActionsSpacer(request, actions);
		
		Map action = new HashMap();
		action.put("title", NLT.get("logout"));
		action.put("url", "javascript: ;");
		action.put("onclick", "ss_logoff();return false;");
		actions.add(action);
	}

	public static void addActionsFullView(AllModulesInjected bs, RenderRequest request, List actions, 
			Long binderId, Long entryId) {
		if (binderId == null) return;
		Binder binder = null;
		try {
			binder = bs.getBinderModule().getBinder(binderId);
		} catch(AccessControlException ace) {}
		if (binder != null) {
			Map action = new HashMap();
			action.put("title", NLT.get("mobile.teamingUI"));
			AdaptedPortletURL adapterUrl = new AdaptedPortletURL(request, "ss_mobile", true);
			adapterUrl.setParameter(WebKeys.ACTION, WebKeys.ACTION_MOBILE_AJAX);
			if (binder != null) adapterUrl.setParameter(WebKeys.URL_BINDER_ID, binder.getId().toString());
			adapterUrl.setParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_MOBILE_SHOW_FULL_UI);
			if (entryId != null) {
				adapterUrl.setParameter(WebKeys.URL_ENTRY_ID, entryId.toString());
			}
			action.put("url", adapterUrl.toString());
			
			HttpServletRequest req = WebHelper.getHttpServletRequest(request);
			String userAgents = org.kablink.teaming.util.SPropsUtil.getString("mobile.userAgents", "");
			String tabletUserAgents = org.kablink.teaming.util.SPropsUtil.getString("tablet.userAgentRegexp", "");
			Boolean testForAndroid = org.kablink.teaming.util.SPropsUtil.getBoolean("tablet.useDefaultTestForAndroidTablets", false);
			if (BrowserSniffer.is_mobile(req, userAgents) && !BrowserSniffer.is_tablet(req, tabletUserAgents, testForAndroid)) {
				actions.add(action);
			}
		}
	}

	public static void addActionsSpacer(RenderRequest request, List actions) {
		Map action = new HashMap();
		action.put("spacer", true);
		actions.add(action);
	}

	public static void addActionsRefresh(RenderRequest request, List actions) {
		Map action = new HashMap();
		action.put("title", NLT.get("general.Refresh"));
		action.put("url", "javascript: ;");
		action.put("onclick", "self.location.reload(true);return false;");
		actions.add(action);
	}

	public static void addActionsGeneral(RenderRequest request, List actions, 
			String title, String url, String onClick) {
		Map action = new HashMap();
		action.put("title", title);
		action.put("url", url);
		if (!onClick.equals("")) action.put("onclick", onClick);
		actions.add(action);
	}

	protected static ModelAndView setupWorkareaPortlet(AllModulesInjected bs, RenderRequest request, 
			RenderResponse response, PortletPreferences prefs, Map model, String view) throws Exception {
		Boolean showTrash = ((Boolean) model.get(WebKeys.URL_SHOW_TRASH));
		if (null == showTrash) {
			showTrash = Boolean.FALSE;
		}
        User user = RequestContextHolder.getRequestContext().getUser();
		String namespace = response.getNamespace();
        if (PortletAdapterUtil.isRunByAdapter(request)) {
        	namespace = PortletRequestUtils.getStringParameter(request, WebKeys.URL_NAMESPACE, "");
        }
		PortletSession portletSession = WebHelper.getRequiredPortletSession(request);
		Long binderId = (Long) portletSession.getAttribute(WebKeys.LAST_BINDER_VIEWED + namespace, PortletSession.APPLICATION_SCOPE);
		String entityType = (String) portletSession.getAttribute(WebKeys.LAST_BINDER_ENTITY_TYPE + namespace, PortletSession.APPLICATION_SCOPE);
		
		if (binderId != null) {
			if (entityType != null && entityType.equals(EntityType.folder.name()))
				return ListFolderHelper.BuildFolderBeans(bs, request, response, binderId, "", showTrash);
			if (entityType != null && entityType.equals(EntityType.workspace.name()))
				return WorkspaceTreeHelper.setupWorkspaceBeans(bs, binderId, request, response, showTrash);
		}

		//This is the default workarea view. Show the user's workspace
		//Set up the navigation beans
		binderId = user.getWorkspaceId();
		Binder binder = null;
		if (binderId != null) {
			try {
				binder = bs.getBinderModule().getBinder(binderId);
			}
			catch(AccessControlException e) {
				//Set up the standard beans
				setupStandardBeans(bs, request, response, model, binderId);
				if (WebHelper.isUserLoggedIn(request) && !user.isShared()) {
					//Access is not allowed
					String refererUrl = (String)request.getAttribute(WebKeys.REFERER_URL);
					model.put(WebKeys.URL, refererUrl);
					return new ModelAndView(WebKeys.VIEW_ACCESS_DENIED, model);
				} else {
					//Please log in
					String refererUrl = (String)request.getAttribute(WebKeys.REFERER_URL);
					model.put(WebKeys.URL, refererUrl);
					return new ModelAndView(WebKeys.VIEW_LOGIN_PLEASE, model);
				}
			}
			catch(NoBinderByTheIdException e) {
				logger.debug("BinderHelper.setupWorkareaPortlet(NoBinderByTheIdException):  Ignored");
			}
		}
		if (binder != null) {
			if (binder.getEntityType().name().equals(EntityType.folder.name()))
				return ListFolderHelper.BuildFolderBeans(bs, request, response, binderId, "", showTrash);
			if (binder.getEntityType().name().equals(EntityType.workspace.name()))
				return WorkspaceTreeHelper.setupWorkspaceBeans(bs, binderId, request, response, showTrash);
		}
		try {
			binder = bs.getWorkspaceModule().getTopWorkspace();
			Document tree = bs.getBinderModule().getDomBinderTree(binder.getId(), 
					new WsDomTreeBuilder(null, true, bs), 1);
			model.put(WebKeys.WORKSPACE_DOM_TREE, tree);
		} catch(AccessControlException e) {}

		return new ModelAndView(view, model);
		
	}

	protected static ModelAndView setupWorkareaNavigationPortlet(AllModulesInjected bs, 
			RenderRequest request, PortletPreferences prefs, Map model, String view) {
		//This is the workarea navigation view
		try {
			Binder binder = bs.getWorkspaceModule().getTopWorkspace();
			Document tree = bs.getBinderModule().getDomBinderTree(binder.getId(), 
					new WsDomTreeBuilder(null, true, bs), 1);
	
			model.put(WebKeys.WORKSPACE_DOM_TREE, tree);
		} catch(AccessControlException e) {}

		return new ModelAndView(view, model);
		
	}

	/**
	 * 
	 */
	public static Map getDefaultSortOrderForSearch( RenderRequest request )
	{
		String sortBy, sortBySecondary;
   		Map options = new HashMap();

   		// Get the "sort by" value from the request.  If it is not there we default to sort-by-relevence.
		sortBy = PortletRequestUtils.getStringParameter( request, WebKeys.SEARCH_FORM_SORT_BY, ObjectKeys.SEARCH_SORT_BY_RELEVANCE );
		if(ObjectKeys.SEARCH_SORT_BY_RELEVANCE.equals(sortBy)) {
			// Primary sort is by relevance. In this case, if the caller hasn't specified an explicit value
			// for the secondary sort order, default it to order by date.
			sortBySecondary = PortletRequestUtils.getStringParameter( request, WebKeys.SEARCH_FORM_SORT_BY_SECONDARY, ObjectKeys.SEARCH_SORT_BY_DATE );
		}
		else {
			// Primary sort order is something other than relevance. In this case, default the secondary
			// sort order to relevance, unless the caller has specified an explicit value.
			sortBySecondary = PortletRequestUtils.getStringParameter( request, WebKeys.SEARCH_FORM_SORT_BY_SECONDARY, ObjectKeys.SEARCH_SORT_BY_RELEVANCE );
		}

		options.put(ObjectKeys.SEARCH_SORT_BY, sortBy);
		options.put(ObjectKeys.SEARCH_SORT_DESCEND, getDefaultDescend(sortBy));
		options.put(ObjectKeys.SEARCH_SORT_BY_SECONDARY, sortBySecondary);
		options.put(ObjectKeys.SEARCH_SORT_DESCEND_SECONDARY, getDefaultDescend(sortBySecondary));
		
		return options;
	}// end getDefaultSortOrderForSearch()
	
	private static Boolean getDefaultDescend(String sortBy) {
		if(ObjectKeys.SEARCH_SORT_BY_RELEVANCE.equals(sortBy))
			return Boolean.FALSE;
		else
			return Boolean.TRUE;
	}
	
	public static String getDisplayType(PortletRequest request) {
		PortletConfig pConfig = (PortletConfig)request.getAttribute("javax.portlet.config");
		String pName = pConfig.getPortletName();
		//For liferay we use instances and the name will be changed slightly
		//That is why we check for the name with contains
		if (pName.contains(ViewController.FORUM_PORTLET))
			return ViewController.FORUM_PORTLET;
		else if (pName.contains(ViewController.WORKSPACE_PORTLET))
			return ViewController.WORKSPACE_PORTLET;
		else if (pName.contains(ViewController.PRESENCE_PORTLET))
			return ViewController.PRESENCE_PORTLET;
		else if (pName.contains(ViewController.BLOG_SUMMARY_PORTLET))
			return ViewController.BLOG_SUMMARY_PORTLET;
		else if (pName.contains(ViewController.GALLERY_PORTLET))
			return ViewController.GALLERY_PORTLET;
		else if (pName.contains(ViewController.GUESTBOOK_SUMMARY_PORTLET))
			return ViewController.GUESTBOOK_SUMMARY_PORTLET;
		else if (pName.contains(ViewController.TASK_SUMMARY_PORTLET))
			return ViewController.TASK_SUMMARY_PORTLET;
		else if (pName.contains(ViewController.SEARCH_PORTLET))
			return ViewController.SEARCH_PORTLET;
		else if (pName.contains(ViewController.TOOLBAR_PORTLET))
			return ViewController.WORKAREA_PORTLET;
		else if (pName.contains(ViewController.WIKI_PORTLET))
			return ViewController.WIKI_PORTLET;
		else if (pName.contains(ViewController.MOBILE_PORTLET))
			return ViewController.MOBILE_PORTLET;
		else if (pName.contains(ViewController.WORKAREA_PORTLET))
			return ViewController.WORKAREA_PORTLET;
		else if (pName.contains(ViewController.WELCOME_PORTLET))
			return ViewController.WELCOME_PORTLET;
		else if (pName.contains(ViewController.RELEVANCE_DASHBOARD_PORTLET))
			return ViewController.RELEVANCE_DASHBOARD_PORTLET;
		return null;

	}

	static public String getViewType(AllModulesInjected bs, Long binderId) {
		//does read check
		Binder binder = bs.getBinderModule().getBinder(binderId);
		return getViewType(bs, binder);
	}
	static public String getViewType(AllModulesInjected bs, Binder binder) {

		User user = RequestContextHolder.getRequestContext().getUser();
		
		UserProperties userProperties = bs.getProfileModule().getUserProperties(user.getId(), binder.getId()); 
		String displayDefId = (String) userProperties.getProperty(ObjectKeys.USER_PROPERTY_DISPLAY_DEFINITION);
		Definition displayDef = binder.getDefaultViewDef();
		if (Validator.isNotNull(displayDefId)) {
			List<Definition> folderViewDefs = binder.getViewDefinitions();
			for (Definition def: folderViewDefs) {
				//Is this an allowed definition?
				if (displayDefId.equals(def.getId())) {
					//Ok, this definition is allowed
					displayDef = def;
					break;
				}
			}
		}
		String viewType = null;
		if (displayDef != null) viewType = DefinitionUtils.getViewType(displayDef.getDefinition());
		if (viewType == null) return "";
		return viewType;
	}

	public static boolean useJspRenderer(Binder binder) {
		Boolean jspOverride = (Boolean) binder.getProperty(ObjectKeys.BINDER_PROPERTY_RENDER_JSP_VIEW);
		if (jspOverride==null) {
			jspOverride = getForceJspRenderingSettingInDefinition(binder);
		}
		return Boolean.TRUE.equals(jspOverride);
	}

	static public Boolean getForceJspRenderingSettingInDefinition(Binder binder) {
		Boolean jspOverride = null;
		Definition viewDef    = binder.getDefaultViewDef();
		Document viewDefDoc = ((null == viewDef) ? null : viewDef.getDefinition());
		if (null != viewDefDoc) {
			// Yes!  Does it contain any HTML <item>'s?
			String viewName;
			if (binder instanceof Folder)
				viewName = "forumView";
			else viewName = "workspaceView";
			Element reply = (Element) viewDefDoc.selectSingleNode("//item[@name='" + viewName + "']/properties/property[@name='forceJspRenderer']");
			if (reply!=null) {
				jspOverride = "true".equalsIgnoreCase(reply.attributeValue("value"));
			}
		}
		return jspOverride;
	}

	static public String getViewListingJsp(AllModulesInjected bs) {
		return getViewListingJsp(bs, "");
	}

	//The getViewListingJSP function has been overloaded, to check if the displayDefinition is of type
	//search. For the 'search' display defintion, we should not have the display at bottom (vertical)
	//option. So when a user chooses display at bottom option, we will be showing the user a overlay display
	//Along with 'search', we have added 'blog' and 'guestbook' to above check 
	static public String getViewListingJsp(AllModulesInjected bs, String displayDefinition) {
		User user = RequestContextHolder.getRequestContext().getUser();
		String displayStyle = user.getDisplayStyle();
		if (displayStyle == null || displayStyle.equals("")) {
			displayStyle = getDefaultViewDisplayStyle();
		}
		String viewListingJspName;
		boolean accessible_simple_ui = SPropsUtil.getBoolean("accessibility.simple_ui", false);
		if (displayDefinition != null && displayDefinition.equalsIgnoreCase(ObjectKeys.SEARCH_RESULTS_DISPLAY)) {
			if (displayStyle.equals(ObjectKeys.USER_DISPLAY_STYLE_IFRAME)) {
				viewListingJspName = WebKeys.VIEW_LISTING_SEARCH_RESULTS_IFRAME;
			} else if (displayStyle.equals(ObjectKeys.USER_DISPLAY_STYLE_NEWPAGE)) {
				viewListingJspName = WebKeys.VIEW_LISTING_SEARCH_RESULTS_IFRAME;
			} else if (displayStyle.equals(ObjectKeys.USER_DISPLAY_STYLE_POPUP)) {
				viewListingJspName = WebKeys.VIEW_LISTING_SEARCH_RESULTS_IFRAME;
			} else if (displayStyle.equals(ObjectKeys.USER_DISPLAY_STYLE_ACCESSIBLE) && accessible_simple_ui) {
				viewListingJspName = WebKeys.VIEW_LISTING_SEARCH_RESULTS_ACCESSIBLE;
			} else if (displayStyle.equals(ObjectKeys.USER_DISPLAY_STYLE_VERTICAL)) {
				/** Vertical mode has been removed
				//Hemanth: if the the displayStyle has been set to vertical[view at bottom], it must be applied
				//only to the table folder view. For all other folder views we need to use the iframe view.
				if (displayDefinition != null && displayDefinition.equals(Definition.VIEW_STYLE_TABLE)) {
					viewListingJspName = WebKeys.VIEW_LISTING_SEARCH_RESULTS_VERTICAL;
				} else {
					viewListingJspName = WebKeys.VIEW_LISTING_SEARCH_RESULTS_IFRAME;
				}
				*/
				viewListingJspName = WebKeys.VIEW_LISTING_SEARCH_RESULTS_IFRAME;
			} else {
				viewListingJspName = WebKeys.VIEW_LISTING_SEARCH_RESULTS_IFRAME;
			}
		} else if (displayStyle.equals(ObjectKeys.USER_DISPLAY_STYLE_IFRAME)) {
			viewListingJspName = WebKeys.VIEW_LISTING_IFRAME;
		} else if (displayStyle.equals(ObjectKeys.USER_DISPLAY_STYLE_NEWPAGE)) {
			viewListingJspName = WebKeys.VIEW_LISTING_IFRAME;
		} else if (displayStyle.equals(ObjectKeys.USER_DISPLAY_STYLE_POPUP)) {
			viewListingJspName = WebKeys.VIEW_LISTING_IFRAME;
		} else if (displayStyle.equals(ObjectKeys.USER_DISPLAY_STYLE_ACCESSIBLE) && accessible_simple_ui) {
			viewListingJspName = WebKeys.VIEW_LISTING_ACCESSIBLE;
		} else if (displayStyle.equals(ObjectKeys.USER_DISPLAY_STYLE_VERTICAL)) {
			/** Vertical mode has been removed
			//Hemanth: if the the displayStyle has been set to vertical[view at bottom], it must be applied
			//only to the table folder view. For all other folder views we need to use the iframe view.
			if (displayDefinition != null && displayDefinition.equals(Definition.VIEW_STYLE_TABLE)) {
				viewListingJspName = WebKeys.VIEW_LISTING_VERTICAL;
			} else {
				viewListingJspName = WebKeys.VIEW_LISTING_IFRAME;
			}
			*/
			viewListingJspName = WebKeys.VIEW_LISTING_IFRAME;
		} else {
			viewListingJspName = WebKeys.VIEW_LISTING_IFRAME;
		}
		return viewListingJspName;
	}
	
	//Routine to save a generic portal url used to build a url to a binder or entry 
	//  This routine is callable only from a portlet controller
	static public void setBinderPermaLink(AllModulesInjected bs, 
			RenderRequest request, RenderResponse response) {
		if (request.getWindowState().equals(WindowState.MAXIMIZED) || getBinderPermaLink(bs, request).equals("")) {
			User user = RequestContextHolder.getRequestContext().getUser();
			PortletURL url = response.createActionURL();
			try {
				url.setWindowState(WindowState.MAXIMIZED);
			} catch(Exception e) {
				logger.debug("BinderHelper.setBinderPermaLink(Exception:  '" + MiscUtil.exToString(e) + "'):  Ignored");
			};
			url.setParameter(WebKeys.ACTION, WebKeys.URL_ACTION_PLACE_HOLDER);
			url.setParameter(WebKeys.URL_ENTITY_TYPE, WebKeys.URL_ENTITY_TYPE_PLACE_HOLDER);
			url.setParameter(WebKeys.URL_BINDER_ID, WebKeys.URL_BINDER_ID_PLACE_HOLDER);
			url.setParameter(WebKeys.URL_ENTRY_ID, WebKeys.URL_ENTRY_ID_PLACE_HOLDER);
			url.setParameter(WebKeys.URL_NEW_TAB, WebKeys.URL_NEW_TAB_PLACE_HOLDER);
			url.setParameter(WebKeys.URL_ENTRY_TITLE, WebKeys.URL_ENTRY_TITLE_PLACE_HOLDER);
			if (!url.toString().equals(getBinderPermaLink(bs, request)))
				bs.getProfileModule().setUserProperty(user.getId(), 
						ObjectKeys.USER_PROPERTY_PERMALINK_URL, url.toString());
		}
	}
	
	//Routine to get the user's portal url 
	//  This routine is callable from an adaptor controller
	static public String getPortalUrl(AllModulesInjected bs) {
		return SPropsUtil.getString("permalink.fallback.url");
	}
	
	//Routine to get a portal url that points to a binder or entry 
	//  This routine is callable from an adaptor controller
	static public String getBinderPermaLink(AllModulesInjected bs, PortletRequest request) {
		AdaptedPortletURL url = new AdaptedPortletURL(request, "ss_forum", true);
		url.setParameter(WebKeys.ACTION, WebKeys.URL_ACTION_PLACE_HOLDER);
		url.setParameter(WebKeys.URL_ENTITY_TYPE, WebKeys.URL_ENTITY_TYPE_PLACE_HOLDER);
		url.setParameter(WebKeys.URL_BINDER_ID, WebKeys.URL_BINDER_ID_PLACE_HOLDER);
		url.setParameter(WebKeys.URL_ENTRY_ID, WebKeys.URL_ENTRY_ID_PLACE_HOLDER);
		url.setParameter(WebKeys.URL_NEW_TAB, WebKeys.URL_NEW_TAB_PLACE_HOLDER);
		url.setParameter(WebKeys.URL_ENTRY_TITLE, WebKeys.URL_ENTRY_TITLE_PLACE_HOLDER);
		return url.toString();
		/**
		User user = null;
		try {
			user = RequestContextHolder.getRequestContext().getUser();
		} catch(Exception e) {
			//TODO If there is no user, then get the permalink of the guest account
			return "";
		}
		UserProperties userProperties = (UserProperties) bs.getProfileModule().getUserProperties(user.getId());
		String url = (String)userProperties.getProperty(ObjectKeys.USER_PROPERTY_PERMALINK_URL);
		if (url == null) url = "";
		return url;
		*/
	}
	
	static public void getBinderAccessibleUrl(AllModulesInjected bs, Binder binder, Long entryId,
			RenderRequest request, RenderResponse response, Map model) {
		
		User user = RequestContextHolder.getRequestContext().getUser();
		String displayStyle = user.getDisplayStyle();
		if (displayStyle == null || displayStyle.equals("") || 
				(displayStyle.equals(ObjectKeys.USER_DISPLAY_STYLE_ACCESSIBLE) &&
				user.isShared())) {
			displayStyle = getDefaultViewDisplayStyle();
		}
		model.put(WebKeys.DISPLAY_STYLE, displayStyle);
		
		PortletURL url = response.createActionURL();
		if (binder != null) {
			url = response.createActionURL();
			if (binder.getEntityType().equals(EntityType.folder)) url.setParameter(WebKeys.ACTION, WebKeys.ACTION_VIEW_FOLDER_LISTING);
			else if (binder.getEntityType().equals(EntityType.workspace)) url.setParameter(WebKeys.ACTION, WebKeys.ACTION_VIEW_WS_LISTING);
			else if (binder.getEntityType().equals(EntityType.profiles)) url.setParameter(WebKeys.ACTION, WebKeys.ACTION_VIEW_PROFILE_LISTING);
			url.setParameter(WebKeys.URL_BINDER_ID, binder.getId().toString());
			if (entryId != null) url.setParameter(WebKeys.URL_ENTRY_ID, entryId.toString());
			url.setParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_SET_DISPLAY_STYLE);
			if (displayStyle.equals(ObjectKeys.USER_DISPLAY_STYLE_ACCESSIBLE) || 
					user.isShared()) {
				url.setParameter(WebKeys.URL_VALUE, getDefaultViewDisplayStyle());
			} else {
				url.setParameter(WebKeys.URL_VALUE, ObjectKeys.USER_DISPLAY_STYLE_ACCESSIBLE);
			}
			model.put(WebKeys.ACCESSIBLE_URL, url.toString());
		}
	}

	static public Map getAccessControlMapBean(Map model) {
		//Initialize the acl bean
		if (!model.containsKey(WebKeys.ACCESS_CONTROL_MAP)) 
			model.put(WebKeys.ACCESS_CONTROL_MAP, new HashMap());
		return (Map)model.get(WebKeys.ACCESS_CONTROL_MAP);
	}
	
	static public Map getAccessControlEntityMapBean(Map model, DefinableEntity entity) {
		Map accessControlMap = getAccessControlMapBean(model);
		if (!accessControlMap.containsKey(entity.getId())) 
			accessControlMap.put(entity.getId(), new HashMap());
		return (Map)accessControlMap.get(entity.getId());
	}

	static public void setRepliesAccessControl(AllModulesInjected bs, Map model, FolderEntry entry) {
		User user = RequestContextHolder.getRequestContext().getUser();
		Map accessControlMap = (Map) model.get(WebKeys.ACCESS_CONTROL_MAP);
		HashMap entryAccessMap = new HashMap();
		if (accessControlMap.containsKey(entry.getId())) {
			entryAccessMap = (HashMap) accessControlMap.get(entry.getId());
		}
		
		List replies = new ArrayList((List)model.get(WebKeys.FOLDER_ENTRY_DESCENDANTS));
		if (replies != null)  {
			for (int i=0; i<replies.size(); i++) {
				FolderEntry reply = (FolderEntry)replies.get(i);
				Map accessControlEntryMap = getAccessControlEntityMapBean(model, reply);
				boolean reserveAccessCheck = false;
				boolean isUserBinderAdministrator = false;
				boolean isEntryReserved = false;
				boolean isLockedByAndLoginUserSame = false;

				if (bs.getFolderModule().testAccess(reply, FolderOperation.reserveEntry)) {
					reserveAccessCheck = true;
				}
				if (bs.getFolderModule().testAccess(reply, FolderOperation.overrideReserveEntry)) {
					isUserBinderAdministrator = true;
				}
				
				HistoryStamp historyStamp = reply.getReservation();
				if (historyStamp != null) isEntryReserved = true;

				if (isEntryReserved) {
					Principal lockedByUser = historyStamp.getPrincipal();
					if (lockedByUser.getId().equals(user.getId())) {
						isLockedByAndLoginUserSame = true;
					}
				}
				if (bs.getFolderModule().testAccess(reply, FolderOperation.addReply)) {
					accessControlEntryMap.put("addReply", new Boolean(true));
				} else {
					accessControlEntryMap.remove("addReply");
				}
				if (bs.getFolderModule().testAccess(reply, FolderOperation.modifyEntry)) {
					if (reserveAccessCheck && isEntryReserved && !(isUserBinderAdministrator || isLockedByAndLoginUserSame) ) {
						accessControlEntryMap.remove("modifyEntry");
					} else {
						accessControlEntryMap.put("modifyEntry", new Boolean(true));
					}
				} else {
					accessControlEntryMap.remove("modifyEntry");
				}
				if (bs.getFolderModule().testAccess(reply, FolderOperation.deleteEntry)) {
					if (reserveAccessCheck && isEntryReserved && !(isUserBinderAdministrator || isLockedByAndLoginUserSame) ) {
						accessControlEntryMap.remove("deleteEntry");
					} else {
						accessControlEntryMap.put("deleteEntry", new Boolean(true));
					}
				} else {
					accessControlEntryMap.remove("deleteEntry");
				}
			}
		}
		
	}
	

	/**
	 * Finds the nearest containing Binder of binder that's a workspace.
	 * 
	 * @param binder The Binder whose Workspace is being queried.
	 * 
	 * @return The nearest Workspace containing binder.
	 */
	public static Workspace getBinderWorkspace(Binder binder) {
       	Workspace binderWs;
		if (binder instanceof Workspace) {
			binderWs = ((Workspace) binder);   				
		} else  {
			Folder topFolder = ((Folder) binder).getTopFolder();
			if (topFolder == null) topFolder = ((Folder) binder);
			binderWs = ((Workspace) topFolder.getParentBinder());
		}
		return binderWs;
	}

	/**
	 * Finds the nearest containing Binder of binder that's a workspace
	 * that the logged in user has at least read acccess to.
	 * 
	 * @param binder
	 * 
	 * @return
	 */
	public static Workspace getBinderWorkspaceWithAccess(Binder binder) {
		Workspace reply = getBinderWorkspace(binder);
		BinderModule bm = getBinderModule();
		while ((null != reply) && (!(bm.testAccess(reply, BinderOperation.readEntries)))) {
			reply = ((Workspace) reply.getParentBinder());
		}
		return reply;
	}

	/**
	 * Determines whether a binder is a Team workspace.
	 * 
	 * @param binder The Binder being queried for being a Team
	 *                 workspace.
	 *                 
	 * @return true -> binder is a Team workspace.  false -> It isn't.
	 */
	static public boolean isBinderTeamWorkspace(Binder binder) {
		String view = getBinderDefaultViewName(binder);
		return (MiscUtil.hasString(view) && view.equals("_team_workspace"));
	}

	/**
	 * Determines whether a binder is a User workspace.
	 * 
	 * @param binder The Binder being queried for being a User 
	 *                 workspace.
	 *                 
	 * @return true -> binder is a User workspace.  false -> It isn't.
	 */
	static public boolean isBinderUserWorkspace(Binder binder) {
		// If binder is a Workspace...
		boolean isUserWs = ((null != binder) && (binder instanceof Workspace));
		if (isUserWs) {
			// ...we consider it a User workspace if its type is
			// ...user workspace view.
  		   	Integer type = binder.getDefinitionType();
  		   	isUserWs = ((type != null) && ((type.intValue() == Definition.USER_WORKSPACE_VIEW) ||
  		   		(type.intValue() == Definition.EXTERNAL_USER_WORKSPACE_VIEW)));
		}
		return isUserWs;
	}
	
	static public boolean isBinderUserWorkspace(AllModulesInjected bs, Long binderId) {
		return isBinderUserWorkspace(bs.getBinderModule().getBinderWithoutAccessCheck(binderId));
	}

	/**
	 * Determines whether a binder is being viewed as a calendar.
	 *
	 * @param bs
	 * @param binder
	 * 
	 * @return
	 */
	static public boolean isBinderCalendar(AllModulesInjected bs, Binder binder) {
		// Is the binder a folder?
		boolean isCalendar = (EntityIdentifier.EntityType.folder == binder.getEntityType());
		if (isCalendar) {
			// Yes!  Does the user have a view definition selected for
			// it?
			Definition def = getFolderDefinitionFromView(bs, binder);
			if (null == def) {
				// No!  Just use it's default view.
				def = binder.getDefaultViewDef();
			}
			
			// Use the family from the definition to determine if the
			// folder is a calendar.
			String dFamily = getFamilyNameFromDef(def);
			isCalendar = (MiscUtil.hasString(dFamily) && dFamily.equalsIgnoreCase("calendar"));
		}
		return isCalendar;
	}
	
	/**
	 * Determines whether a binder is a personal workspace.
	 * 
	 * @param binder
	 * 
	 * @return
	 */
	static public boolean isBinderPersonalWorkspace(Binder binder) {
		boolean isPersonalWS = (EntityIdentifier.EntityType.workspace == binder.getEntityType());
		if (isPersonalWS) {
			String dFamily = getBinderDefaultFamilyName(binder);
			if (MiscUtil.hasString(dFamily)) {
				isPersonalWS = dFamily.equalsIgnoreCase("user");
			}
		}
		return isPersonalWS;
	}
	
	/**
	 * Determines whether a binder is being viewed as a task folder.
	 * 
	 * @param bs
	 * @param binder
	 * 
	 * @return
	 */
	static public boolean isBinderTask(AllModulesInjected bs, Binder binder) {
		// Is the binder a folder?
		boolean isTask = (EntityIdentifier.EntityType.folder == binder.getEntityType());
		if (isTask) {
			// Yes!  Does the user have a view definition selected for
			// it?
			Definition def = null;
			if (bs != null) {
				def = getFolderDefinitionFromView(bs, binder);
			}
			if (null == def) {
				// No!  Just use it's default view.
				def = binder.getDefaultViewDef();
			}
			
			// Use the family from the definition to determine if the
			// folder is a task folder.
			String dFamily = getFamilyNameFromDef(def);
			isTask = (MiscUtil.hasString(dFamily) && dFamily.equalsIgnoreCase("task"));
		}
		return isTask;
	}
	
	/**
	 * Determines whether a binder is a Wiki.
	 * 
	 * @param binder
	 * 
	 * @return
	 */
	static public boolean isBinderWiki(Binder binder) {
		boolean isWiki = (EntityIdentifier.EntityType.folder == binder.getEntityType());
		if (isWiki) {
			String dFamily = getBinderDefaultFamilyName(binder);
			if (MiscUtil.hasString(dFamily)) {
				isWiki = dFamily.equalsIgnoreCase("wiki");
			}
		}
		return isWiki;
	}
	
	static public void buildWorkspaceTreeBean(AllModulesInjected bs, Binder binder, Map model, DomTreeHelper helper) {
		if (binder instanceof TemplateBinder) return;
		Binder workspaceBinder = binder;
		Document tree = null;
		try {
			if (workspaceBinder.getEntityType().equals(EntityIdentifier.EntityType.workspace)) {
				tree = bs.getBinderModule().getDomBinderTree(workspaceBinder.getId(), 
						new WsDomTreeBuilder(null, true, bs, helper), 1);
			} else if (workspaceBinder.getEntityType().equals(EntityIdentifier.EntityType.folder)) {
				tree = bs.getBinderModule().getDomBinderTree(workspaceBinder.getId(), 
						new WsDomTreeBuilder(null, true, bs, helper), 1);
			} else if (workspaceBinder.getEntityType().equals(EntityIdentifier.EntityType.profiles)) {
				tree = bs.getBinderModule().getDomBinderTree(workspaceBinder.getId(), 
						new WsDomTreeBuilder(null, true, bs, helper), 0);
			}
		} catch (AccessControlException ac) {}

		model.put(WebKeys.SIDEBAR_WORKSPACE_TREE, tree);

		// Record the workspace passed in as the "current workspace" for
		// the sidebar.  This is used as the default workspace context
		// when adding a workspace from the sidebar.
		// It turns out that the calculation of this for the workspace
		// tree is exactly what is needed, so we piggyback this code
		// for this bean.
		
		model.put(WebKeys.SIDEBAR_CURRENT_WORKSPACE, workspaceBinder);

	}
	
	static public void buildNavigationLinkBeans(AllModulesInjected bs, Binder binder, Map model) {
		if (binder instanceof TemplateBinder)
			buildNavigationLinkBeans(bs, (TemplateBinder)binder, model, new ConfigHelper(""));
		else
			buildNavigationLinkBeans(bs, binder, model, null);
	}
	static public void buildNavigationLinkBeans(AllModulesInjected bs, Binder binder, Map model, DomTreeHelper helper) {
		if (binder instanceof TemplateBinder) {
			buildNavigationLinkBeans(bs, (TemplateBinder)binder, model, helper);
		} else {
			Binder parentBinder = binder;
			Map navigationLinkMap;
			if (model.containsKey(WebKeys.NAVIGATION_LINK_TREE)) 
				navigationLinkMap = (Map)model.get(WebKeys.NAVIGATION_LINK_TREE);
			else {
				navigationLinkMap = new HashMap();
				model.put(WebKeys.NAVIGATION_LINK_TREE, navigationLinkMap);
			}
			while (parentBinder != null) {
				Document tree = null;
				try {
					tree = bs.getBinderModule().getDomBinderTree(parentBinder.getId(), 
							new WsDomTreeBuilder(null, true, bs, helper),0);
					navigationLinkMap.put(parentBinder.getId(), tree);
				} catch (AccessControlException ac) {}
				parentBinder = ((Binder)parentBinder).getParentBinder();
			}
		}
	}

	static public void buildNavigationLinkBeans(AllModulesInjected bs, TemplateBinder config, Map model, DomTreeHelper helper) {
		TemplateBinder parentConfig = config;
		Map navigationLinkMap;
		if (model.containsKey(WebKeys.NAVIGATION_LINK_TREE)) 
			navigationLinkMap = (Map)model.get(WebKeys.NAVIGATION_LINK_TREE);
		else {
			navigationLinkMap = new HashMap();
			model.put(WebKeys.NAVIGATION_LINK_TREE, navigationLinkMap);
		}
    	while (parentConfig != null && parentConfig instanceof TemplateBinder) {
        	Document tree = buildTemplateTreeRoot(bs, parentConfig, helper);
 			navigationLinkMap.put(parentConfig.getId(), tree);
 			if (parentConfig.getParentBinder() instanceof TemplateBinder) {
 				parentConfig = (TemplateBinder)parentConfig.getParentBinder();
 			} else {
 				//This template may be owned by a real folder. If so, stop there.
 				break;
 			}
		}
	}
	
	static public void buildSimpleUrlBeans(AllModulesInjected bs,  PortletRequest request, Binder binder, Map model) {
		//Build the simple URL beans
		String[] s = SPropsUtil.getStringArray("simpleUrl.globalKeywords", ",");
		if (Utils.checkIfFilr()) {
			s = SPropsUtil.getStringArray("simpleUrl.globalKeywordsForFilr", ",");
		}
		model.put(WebKeys.SIMPLE_URL_GLOBAL_KEYWORDS, s);
		model.put(WebKeys.SIMPLE_URL_PREFIX, WebUrlUtil.getSimpleURLContextRootURL(request));
		model.put(WebKeys.SIMPLE_WEBDAV_PREFIX, WebUrlUtil.getSimpleWebdavRootURL(request));
		List<SimpleName> simpleNames = bs.getBinderModule().getSimpleNames(binder.getId());
		model.put(WebKeys.SIMPLE_URL_NAMES, simpleNames);
		model.put(WebKeys.SIMPLE_URL_CHANGE_ACCESS, 
				bs.getBinderModule().testAccess(binder,BinderOperation.manageSimpleName));
		if (bs.getAdminModule().testAccess(AdminOperation.manageFunction)) 
			model.put(WebKeys.IS_SITE_ADMIN, true);
		model.put(WebKeys.SIMPLE_URL_NAME_EXISTS_ERROR, 
				PortletRequestUtils.getStringParameter(request, WebKeys.SIMPLE_URL_NAME_EXISTS_ERROR, ""));	
		model.put(WebKeys.SIMPLE_URL_EMAIL_NAME_EXISTS_ERROR, 
				PortletRequestUtils.getStringParameter(request, WebKeys.SIMPLE_URL_EMAIL_NAME_EXISTS_ERROR, ""));	
		model.put(WebKeys.SIMPLE_URL_NAME_NOT_ALLOWED_ERROR, 
				PortletRequestUtils.getStringParameter(request, WebKeys.SIMPLE_URL_NAME_NOT_ALLOWED_ERROR, ""));	

		String hostname = bs.getZoneModule().getVirtualHost(RequestContextHolder.getRequestContext().getZoneName());
		if(hostname == null) {
			try {
		        InetAddress addr = InetAddress.getLocalHost();
		        // Get hostname
		        hostname = addr.getHostName();
		    } catch (UnknownHostException e) {
				logger.debug("BinderHelper.buildSimpleUrlBeans(UnknownHostException):  Using localhost");
				hostname = "localhost";
		    }
		}
		model.put(WebKeys.SIMPLE_EMAIL_HOSTNAME, hostname);		
	}
	
	//trees should not be deep - do entire thing
	static public Document buildTemplateTreeRoot(AllModulesInjected bs, TemplateBinder config, DomTreeHelper helper) {
       	Document tree = DocumentHelper.createDocument();
    	Element element = tree.addElement(DomTreeBuilder.NODE_ROOT);
    	//only need this information if this is the bottom of the tree
    	buildTemplateChildren(element, config, helper);
    	return tree;
	}
	//trees should not be deep - do entire thing
	static public Document buildTemplateTreeRoot(AllModulesInjected bs, List configs, DomTreeHelper helper) {
       	Document tree = DocumentHelper.createDocument();
    	Element element = tree.addElement(DomTreeBuilder.NODE_ROOT);
	   	element.addAttribute("title", NLT.get("administration.configure_cfg"));
    	element.addAttribute("displayOnly", "true");
    	if (!configs.isEmpty()) {
			element.addAttribute("hasChildren", "true");
			for (int i=0; i<configs.size(); ++i) {
				TemplateBinder child = (TemplateBinder)configs.get(i);
    			Element cElement = element.addElement(DomTreeBuilder.NODE_CHILD);
    			buildTemplateChildren(cElement, child, helper);
    		}
    	} else 	element.addAttribute("hasChildren", "false");

    	return tree;
	}
	static void buildTemplateChildren(Element element, TemplateBinder config, DomTreeHelper helper) {
		buildTemplateElement(element, config, helper);
    	List<TemplateBinder> children = config.getBinders();
    	for (TemplateBinder child: children) {
    		Element cElement = element.addElement(DomTreeBuilder.NODE_CHILD);
    		buildTemplateChildren(cElement, child, helper);
    	}
	}
	static void buildTemplateElement(Element element, TemplateBinder config, DomTreeHelper helper) {
	   	element.addAttribute("title", NLT.getDef(config.getTitle()));
    	element.addAttribute("id", config.getId().toString());
 		
    	if (!config.getBinders().isEmpty()) {
			element.addAttribute("hasChildren", "true");
		} else
			element.addAttribute("hasChildren", "false");
			
		if (config.getEntityType().equals(EntityType.workspace)) {
			String icon = config.getIconName();
			String imageClass = "ss_twImg";
			if (icon == null || icon.equals("")) {
				icon = "/icons/workspace.gif";
				imageClass = "ss_twImg";
			}
			element.addAttribute("type", DomTreeBuilder.NODE_TYPE_WORKSPACE);
			element.addAttribute("image", Utils.getIconNameTranslated(icon));
			element.addAttribute("imageClass", imageClass);
			element.addAttribute("action", helper.getAction(DomTreeBuilder.TYPE_TEMPLATE, config));
			element.addAttribute("displayOnly", helper.getDisplayOnly(DomTreeBuilder.TYPE_TEMPLATE, config));
					
		} else {
			String icon = config.getIconName();
			if (icon == null || icon.equals("")) icon = "/icons/folder.png";
			element.addAttribute("image", Utils.getIconNameTranslated(icon));
			element.addAttribute("imageClass", "ss_twIcon");
			element.addAttribute("type", DomTreeBuilder.NODE_TYPE_FOLDER);
			element.addAttribute("action", helper.getAction(DomTreeBuilder.TYPE_TEMPLATE, config));
			element.addAttribute("displayOnly", helper.getDisplayOnly(DomTreeBuilder.TYPE_TEMPLATE, config));
		} 
		
	}

    public static Map getCommonEntryElements() {
    	Map entryElements = new HashMap();
    	Map itemData;
    	//Build a map of common elements for use in search filters
    	//  Each map has a "type" and a "caption". Types can be: title, text, user_list, or date.
    	
    	//title
    	itemData = new HashMap();
    	itemData.put("type", "title");
    	itemData.put("caption", NLT.get("filter.title"));
    	entryElements.put("title", itemData);
    	
    	//author
    	itemData = new HashMap();
    	itemData.put("type", "user_list");
    	itemData.put("caption", NLT.get("filter.author"));
    	// entryElements.put(EntityIndexUtils.CREATORID_FIELD, itemData);
    	// entryElements.put(EntityIndexUtils.CREATOR_NAME_FIELD, itemData);
    	entryElements.put(Constants.CREATOR_TITLE_FIELD, itemData);
    	
    	//creation date
    	itemData = new HashMap();
    	itemData.put("type", "date");
    	itemData.put("caption", NLT.get("filter.creationDate"));
    	entryElements.put("creation", itemData);
    	
    	//modification date
    	itemData = new HashMap();
    	itemData.put("type", "date");
    	itemData.put("caption", NLT.get("filter.modificationDate"));
    	entryElements.put("modification", itemData);
    	
    	return entryElements;
    }
       	
	// This method reads thru the results from a search, finds the tags, 
	// and places them into an array in a alphabetic order.
	public static List sortCommunityTags(List entries) {
		return sortCommunityTags(entries, "");
	}
	public static List sortCommunityTags(List entries, String wordRoot) {
		HashMap tagMap = new HashMap();
		ArrayList tagList = new ArrayList();
		// first go thru the original search results and 
		// find all the unique principals.  Keep a count to see
		// if any are more active than others.
		for (int i = 0; i < entries.size(); i++) {
			Map entry = (Map)entries.get(i);
			String strTags = (String)entry.get(WebKeys.SEARCH_TAG_ID);
			if (strTags == null || "".equals(strTags)) continue;
			
		    String [] strTagArray = strTags.split("\\s");
		    for (int j = 0; j < strTagArray.length; j++) {
		    	String strTag = strTagArray[j];

		    	if (strTag.equals("")) continue;
		    	
		    	//See if this must match a specific word root
		    	if (!wordRoot.equals("") && !strTag.toLowerCase().startsWith(wordRoot.toLowerCase())) continue;
		    	
		    	Integer tagCount = (Integer) tagMap.get(strTag);
		    	if (tagCount == null) {
		    		tagMap.put(strTag, new Integer(1));
		    	}
		    	else {
		    		int intTagCount = tagCount.intValue();
		    		tagMap.put(strTag, new Integer(intTagCount+1));
		    	}
		    }
		}
		
		//sort the tags string
		Collection collection = tagMap.keySet();
		Object[] array = collection.toArray();
		Arrays.sort(array);
		
		for (int j = 0; j < array.length; j++) {
			HashMap tags = new HashMap();
			String strTag = (String) array[j];
			tags.put(WebKeys.TAG_NAME, strTag);
			tags.put(WebKeys.SEARCH_RESULTS_COUNT, (Integer) tagMap.get(strTag));
			tagList.add(tags);
		}
		return tagList;
	}
	
	//This method rates the community tags
	public static List rateCommunityTags(List entries, int intMaxHits) {
		//Same rating algorithm is used for both community and personal tags
		return rateTags(entries, intMaxHits);
	}
	
	//This method identifies if we need a + or - sign infront of the
	//tags being displayed in the tags tab in the search tab
	public static List determineSignBeforeTag(List entries, String tabTagTitle) {
		ArrayList tagList = new ArrayList();
		for (int i = 0; i < entries.size(); i++) {
			String strTabTitle = tabTagTitle;
			Map tag = (Map) entries.get(i);
			String strTagName = (String) tag.get(WebKeys.TAG_NAME);
			if (strTabTitle != null && !strTabTitle.equals("")) {
				if ( (strTabTitle.indexOf(strTagName+ " ") != -1) || (strTabTitle.indexOf(" " + strTagName) != -1) ) {
					tag.put(WebKeys.TAG_SIGN, "-");
					
					int intFirstIndex = strTabTitle.indexOf(strTagName+ " ");
					int intFirstLength = (strTagName+ " ").length();
					
					if (intFirstIndex != -1) {
						String strFirstPart = "";
						String strLastPart = "";
						
						if (intFirstIndex != 0) {
							strFirstPart = strTabTitle.substring(0, (intFirstIndex));
						}
						if ( strTabTitle.length() !=  (intFirstIndex+1+intFirstLength) ) {
							strLastPart = strTabTitle.substring(intFirstIndex+intFirstLength, strTabTitle.length());
						}
						strTabTitle = strFirstPart + strLastPart;
					}
					
					int intLastIndex = strTabTitle.indexOf(" " + strTagName);
					int intLastLength = (" " + strTagName).length();

					if (intLastIndex != -1) {
						String strFirstPart = "";
						String strLastPart = "";
						
						if (intLastIndex != 0) {
							strFirstPart = strTabTitle.substring(0, (intLastIndex));
						}
						if ( strTabTitle.length() !=  (intLastIndex+intLastLength) ) {
							strLastPart = strTabTitle.substring(intLastIndex+intLastLength, strTabTitle.length());
						}
						strTabTitle = strFirstPart + strLastPart;
					}
					tag.put(WebKeys.TAG_SEARCH_TEXT, strTabTitle);					
				}
				else if (strTabTitle.equals(strTagName)) {
					tag.put(WebKeys.TAG_SIGN, "-");
					tag.put(WebKeys.TAG_SEARCH_TEXT, "");
				}
				else {
					tag.put(WebKeys.TAG_SIGN, "+");
					tag.put(WebKeys.TAG_SEARCH_TEXT, strTabTitle + " " + strTagName);
				}
			}
			else {
				tag.put(WebKeys.TAG_SIGN, "+");
				tag.put(WebKeys.TAG_SEARCH_TEXT, strTagName);
			}
			tagList.add(tag);
		}
		return tagList;
	}

	// This method reads thru the results from a search, finds the personal tags, 
	// and places them into an array in a alphabetic order.
	public static List sortPersonalTags(List entries) {
		HashMap tagMap = new HashMap();
		ArrayList tagList = new ArrayList();
		for (int i = 0; i < entries.size(); i++) {
			Map entry = (Map)entries.get(i);
			String strTags = (String)entry.get(WebKeys.SEARCH_ACL_TAG_ID);
			if (strTags == null || "".equals(strTags)) continue;
			
		    String [] strTagArray = strTags.split("ACL");
		    for (int j = 0; j < strTagArray.length; j++) {
		    	String strTag = strTagArray[j].trim();
		    	if (strTag.equals("")) continue;
		    	
		    	String strFirstSixChars = "";
		    	if (strTag.length() >= 6) {
		    		strFirstSixChars = strTag.substring(0, 6);
		    	}
		    	//Ignore these entries as they refer to community entries.
		    	if (strFirstSixChars.equals("allTAG")) continue;

		    	User user = RequestContextHolder.getRequestContext().getUser();
		    	long userId = user.getId();
		    	
		    	String strUserIdTag = String.valueOf(userId) + "TAG";
		    	if (strTag.length() >= strUserIdTag.length()) {
			    	String strValueToCompare = strTag.substring(0, strUserIdTag.length());
			    	
			    	//We are going to get only the personal tags relating to the user
			    	if (strValueToCompare.equals(strUserIdTag)) {
			    		String strTagValues = strTag.substring(strUserIdTag.length());
					    String [] strIntTagArray = strTagValues.split("\\s");
					    for (int k = 0; k < strIntTagArray.length; k++) {
					    	String strIntTag = strIntTagArray[k].trim();
					    	if (strIntTag.equals("")) continue;
					    	
					    	Integer tagCount = (Integer) tagMap.get(strIntTag);
					    	if (tagCount == null) {
					    		tagMap.put(strIntTag, new Integer(1));
					    	} else {
					    		int intTagCount = tagCount.intValue();
					    		tagMap.put(strIntTag, new Integer(intTagCount+1));
					    	}
					    }
			    	} else {
			    		continue;
			    	}
		    	}
		    }
		}

		//sort the tags string
		Collection collection = tagMap.keySet();
		Object[] array = collection.toArray();
		Arrays.sort(array);
		
		for (int j = 0; j < array.length; j++) {
			HashMap tags = new HashMap();
			String strTag = (String) array[j];
			tags.put(WebKeys.TAG_NAME, strTag);
			tags.put(WebKeys.SEARCH_RESULTS_COUNT, (Integer) tagMap.get(strTag));
			tagList.add(tags);
		}
		return tagList;
	}

	//This method rates the personal tags
	public static List ratePersonalTags(List entries, int intMaxHits) {
		//Same rating algorithm is used for both community and personal tags
		return rateTags(entries, intMaxHits);
	}	

	//This method provides ratings for the tags
	public static List rateTags(List entries, int intMaxHits) {
		List ratedList = new ArrayList();
		int intMaxHitsPerFolder = intMaxHits;
		/*
		for (int i = 0; i < entries.size(); i++) {
			Map tag = (Map) entries.get(i);
			Integer resultCount = (Integer) tag.get(WebKeys.SEARCH_RESULTS_COUNT);
			if (resultCount.intValue() > intMaxHitsPerFolder) {
				intMaxHitsPerFolder = resultCount.intValue();
			}
		}
		*/
		for (int i = 0; i < entries.size(); i++) {
			Map tag = (Map) entries.get(i);
			Integer resultCount = (Integer) tag.get(WebKeys.SEARCH_RESULTS_COUNT);
			int intResultCount = resultCount.intValue();
			Double DblRatingForFolder = ((double)intResultCount/intMaxHitsPerFolder) * 100;
			int intRatingForFolder = DblRatingForFolder.intValue();
			tag.put(WebKeys.SEARCH_RESULTS_RATING, new Integer(DblRatingForFolder.intValue()));
			if (intRatingForFolder > 80 && intRatingForFolder <= 100) {
				tag.put(WebKeys.SEARCH_RESULTS_RATING_CSS, "ss_largerprint");
			}
			else if (intRatingForFolder > 50 && intRatingForFolder <= 80) {
				tag.put(WebKeys.SEARCH_RESULTS_RATING_CSS, "ss_largeprint");
			}
			else if (intRatingForFolder > 20 && intRatingForFolder <= 50) {
				tag.put(WebKeys.SEARCH_RESULTS_RATING_CSS, "ss_normalprint");
			}
			else if (intRatingForFolder > 10 && intRatingForFolder <= 20) {
				tag.put(WebKeys.SEARCH_RESULTS_RATING_CSS, "ss_smallprint");
			}
			else if (intRatingForFolder >= 0 && intRatingForFolder <= 10) {
				tag.put(WebKeys.SEARCH_RESULTS_RATING_CSS, "ss_fineprint");
			}
			ratedList.add(tag);
		}
	
		return ratedList;		
	}
	
	public static int getMaxHitsPerTag(List entries) {
		int intMaxHitsPerFolder = 0;
		for (int i = 0; i < entries.size(); i++) {
			Map tag = (Map) entries.get(i);
			Integer resultCount = (Integer) tag.get(WebKeys.SEARCH_RESULTS_COUNT);
			if (resultCount.intValue() > intMaxHitsPerFolder) {
				intMaxHitsPerFolder = resultCount.intValue();
			}
		}
		return intMaxHitsPerFolder;
	}
	

	public static class ConfigHelper implements DomTreeHelper {
		String action;
		String page;
		public ConfigHelper(String action) {
			this.action = action;
		}
		public ConfigHelper(String action, String page) {
			this.action = action;
			this.page = page;
		}
		@Override
		public boolean supportsType(int type, Object source) {
			if (type == DomTreeBuilder.TYPE_TEMPLATE) {return true;}
			return false;
		}
		@Override
		public boolean hasChildren(AllModulesInjected bs, Object source, int type) {
			TemplateBinder config = (TemplateBinder)source;
			return !config.getBinders().isEmpty();
		}
	
		@Override
		public String getAction(int type, Object source) {
			return action;
		}
		@Override
		public String getURL(int type, Object source) {return null;}
		@Override
		public String getDisplayOnly(int type, Object source) {
			return "false";
		}
		@Override
		public String getTreeNameKey() {return null;}
		@Override
		public String getPage() {return page;}
		@Override
		public void customize(AllModulesInjected bs, Object source, int type, Element element) {};
		
	}
	// Walk the list of entries returned by the search engine.  If
	// an entry has doctype=attachment then see if it's entry is
	// already in the list, and add this attachment to it's map.
	// if not, see if there is an attachment for the same entry
	// already in the list.  If so, add this as an attachment to that 
	// attachment.
	//
	// Note that if an entry was on this list, then a new key/value pair of
	// (WebKeys.ENTRY_HAS_META_HIT, true) will be added to it's map.
	//
	// if attachments are found for an entry, then they will be taken off
	// the entry list, and added to the list of attachments for that entry
	// in the list. (key value - WebKeys.ENTRY_ATTACHMENTS
	//
	// if attachments are found, but not the entry they're associated
	// with, then leave an attachment on the list, and if there are 
	// mulitple attachments for the same entry, then the attachment
	// will contain a map entry (WebKeys.ENTRY_ATTACHMENTS), which
	// contains all the attachments for that entry.
	public static List filterEntryAttachmentResults(List entries) {

		for (int count = 0; count < entries.size(); count++) {
			Map entry = (Map)entries.get(count);
			String type = (String)entry.get(Constants.DOC_TYPE_FIELD);
			// if it's an entry, see if there's already an attachment in the list for it.
			String docId = (String)entry.get(Constants.DOCID_FIELD);
			String entityType = (String)entry.get(Constants.ENTITY_FIELD);
			if (type.equalsIgnoreCase(Constants.DOC_TYPE_ENTRY)) {
				int i = 0;
				for (i=0; i < count; i++) {
					String d = (String)((Map)entries.get(i)).get(Constants.DOCID_FIELD);
					String e = (String)((Map)entries.get(i)).get(Constants.ENTITY_FIELD);
					if (d.equalsIgnoreCase(docId) && e.equalsIgnoreCase(entityType)) {
						// if it's already in the list, then it's an attachment, 
						// so insert ourselves in here, add the attachment to 
						// the entry, and delete this attachment from the list.
						Map att = (Map)entries.get(i);
						// see if the attachment has other attachments added to it.
						// if it does, then add those to this entry.
						List attachments = (List)att.get(WebKeys.ENTRY_ATTACHMENTS);
						if (attachments != null) 
							entry.put(WebKeys.ENTRY_ATTACHMENTS, attachments);
						else
							entry.put(WebKeys.ENTRY_ATTACHMENTS, att);
						entry.put(WebKeys.ENTRY_HAS_META_HIT, true);
						entries.remove(i);
						count--;
					}
				}
				if (i == count || count == 1) {
					entry.put(WebKeys.ENTRY_HAS_META_HIT, true);
				}
			} else if (type.equalsIgnoreCase(Constants.DOC_TYPE_ATTACHMENT)) {

				for (int i = 0; i < count; i++) {
					String d = (String) ((Map) entries.get(i)).get(Constants.DOCID_FIELD);
					String e = (String)((Map)entries.get(i)).get(Constants.ENTITY_FIELD);
					if (d.equalsIgnoreCase(docId) && e.equalsIgnoreCase(entityType)) {
						// if it's already in the list, then check if it's an
						// entry. If it is an entry, then add this attachment to
						// the entry, and delete this attachment from the list.
						// if it's an attachment, then if the attachment already
						// has an attachments map, add this to it. Otherwise,
						// create the attachments map, and add the attachment, and this
						// entry to it.
						Map ent = (Map) entries.get(i);
						String typ = (String) ent.get(Constants.DOC_TYPE_FIELD);

						// entry = (Map)entries.get(count);
						// see if this entry already has attachments
						List attachments = (List) ent.get(WebKeys.ENTRY_ATTACHMENTS);
						if (attachments == null) {
							attachments = new ArrayList();
						}
						if (typ.equalsIgnoreCase(Constants.DOC_TYPE_ATTACHMENT)) {
							attachments.add(ent);
						}
						attachments.add(entry);
						ent.put(WebKeys.ENTRY_ATTACHMENTS, attachments);
					
						entries.remove(count);
						count--;
						break;
					}
				}
			}
		}
		return entries;		
	}
	
	//Routine to build the beans for displaying entry versions
	//  Each ChangeLog document is exploded into a map of values
	public static List BuildChangeLogBeans(AllModulesInjected bs, DefinableEntity entity, 
			List changeLogs, Map<Long,FolderEntry> folderEntries) {
		return BuildChangeLogBeans(bs, entity, changeLogs, folderEntries, null);
	}
	public static List BuildChangeLogBeans(AllModulesInjected bs, DefinableEntity entity, 
			List changeLogs, Map<Long,FolderEntry> folderEntries, Long version) {
		return BuildChangeLogBeans(bs, entity, changeLogs, folderEntries, version, false);
	}
	public static List BuildChangeLogBeans(AllModulesInjected bs, DefinableEntity entity, 
			List changeLogs, Map<Long,FolderEntry> folderEntries, Long version, boolean showAll) {
		List changeList = new ArrayList();
		if (changeLogs == null) return changeList;

		for (int i = 0; i < changeLogs.size(); i++) {
			ChangeLog log = (ChangeLog) changeLogs.get(i);
			Document doc = log.getDocument();
			Element root = doc.getRootElement();
			Long logVersion = Long.valueOf(root.attributeValue("logVersion", "0"));
			Map changeMap = new HashMap(); // doc.asXML()
			changeMap.put("changeLog", log);
			changeMap.put("operation", root.attributeValue("operation", ""));
			if (version != null && version.equals(log.getVersion()) || 
					version == null) {
				if (!folderEntries.containsKey(logVersion) || showAll) changeList.add(changeMap);
				
				//Build a pseudo entry object
				FolderEntry fe = getEntryVersion(bs, entity, doc, folderEntries);
				if (fe == null) continue;
				changeMap.put("changeLogEntry", fe);
				
				//Get name of rootElement (e.g., folderEntry) and build a map of its elements
				Map rootMap = new HashMap();
				changeMap.put(root.getName(), rootMap);
				Map attributeMap = new HashMap();
				rootMap.put("attributes", attributeMap);
				Iterator itAttr = root.attributeIterator();
				while (itAttr.hasNext()) {
					Attribute attr = (Attribute) itAttr.next();
					attributeMap.put(attr.getName(),attr.getValue());
				}
	
				Iterator itRoot = root.elementIterator();
				while (itRoot.hasNext()) {
					Element ele = (Element) itRoot.next();
					Map eleMap = (Map) rootMap.get(ele.getName());
					if (eleMap == null) {
						eleMap = new HashMap();
						rootMap.put(ele.getName(), eleMap);
					}
					//Add the attributes
					String name = ele.attributeValue("name");
					attributeMap = new HashMap();
					itAttr = ele.attributeIterator();
					while (itAttr.hasNext()) {
						Attribute attr = (Attribute) itAttr.next();
						attributeMap.put(attr.getName(), attr.getValue());
					}
					//Add the data
					if (Validator.isNull(name)) {
						eleMap.put("attributes", attributeMap);
						continue; //no way to add it
					}
					Map dataMap = new HashMap();
					dataMap.put("attributes", attributeMap);
					dataMap.put("value", ele.getData());
					eleMap.put(name, dataMap);
				}
			}
		}
		return changeList;
	}
	
	//Routing to make a fake entry based on a change log
	public static FolderEntry getEntryVersion(AllModulesInjected bs, DefinableEntity entity, 
			Document doc, Map<Long,FolderEntry> folderEntries) {
		if (entity == null) return null;
		Element root = doc.getRootElement();  // doc.asXML()
		Long logVersion = Long.valueOf(root.attributeValue("logVersion", "0"));
		FolderEntry entry = folderEntries.get(logVersion);
		if (entry == null) entry = new FolderEntry();
		folderEntries.put(logVersion, entry);
		entry.setId(entity.getId());
		entry.setParentBinder(entity.getParentBinder());
		entry.setDefinitionType(entity.getDefinitionType());
		entry.setEntryDefId(entity.getEntryDefId());
		entry.setLogVersion(logVersion);
		if (entity instanceof FolderEntry) {
			entry.setParentEntry(((FolderEntry)entity).getParentEntry());
			entry.setParentFolder(((FolderEntry)entity).getParentFolder());
			entry.setTopEntry(((FolderEntry)entity).getTopEntry());
			entry.setHKey(((FolderEntry)entity).getHKey());
			entry.setAverageRating(((FolderEntry)entity).getAverageRating());
		}
		
		Document defDoc = entity.getEntryDefDoc();
		
		//Set the owner of the entry
		Element hs = (Element)root.selectSingleNode("//historyStamp[@name='created']");
		if (hs == null) return null;
		String authorId = hs.attributeValue("author", "");
		if (authorId.equals("")) return null;
		Set ids = new HashSet();
		ids.add(Long.valueOf(authorId));
		SortedSet pList = bs.getProfileModule().getPrincipals(ids);
		if (pList.isEmpty()) return null;
		entry.setOwner((Principal)pList.first());
		HistoryStamp creation = new HistoryStamp();
		creation.setPrincipal((UserPrincipal)pList.first());
		String when = hs.attributeValue("when", "");
		SimpleDateFormat sdf = getSimpleDateFormat();
		Date date;
		try {
			date = sdf.parse(when);
		} catch (ParseException e) {
			return null;
		}
		creation.setDate(date);
		entry.setCreation(creation);
		entry.setModification(creation);
		
		//Set the modifier of the entry
		hs = (Element)root.selectSingleNode("//historyStamp[@name='modified']");
		if (hs != null) {
			authorId = hs.attributeValue("author", "");
			if (!authorId.equals("")) {
				ids = new HashSet();
				ids.add(Long.valueOf(authorId));
				pList = bs.getProfileModule().getPrincipals(ids);
				if (!pList.isEmpty()) {
					entry.setOwner((Principal)pList.first());
					HistoryStamp modification = new HistoryStamp();
					modification.setPrincipal((UserPrincipal)pList.first());
					when = hs.attributeValue("when", "");
					sdf = getSimpleDateFormat();
					try {
						date = sdf.parse(when);
						modification.setDate(date);
						entry.setModification(modification);
					} catch (ParseException e) {}
				}
			}
		}
		
		//Set the workflow modifier of the entry
		hs = (Element)root.selectSingleNode("//historyStamp[@name='workflowChange']");
		if (hs != null) {
			authorId = hs.attributeValue("author", "");
			if (!authorId.equals("")) {
				ids = new HashSet();
				ids.add(Long.valueOf(authorId));
				pList = bs.getProfileModule().getPrincipals(ids);
				if (!pList.isEmpty()) {
					entry.setOwner((Principal)pList.first());
					HistoryStamp workflowChange = new HistoryStamp();
					workflowChange.setPrincipal((UserPrincipal)pList.first());
					when = hs.attributeValue("when", "");
					sdf = getSimpleDateFormat();
					try {
						date = sdf.parse(when);
						workflowChange.setDate(date);
						entry.setWorkflowChange(workflowChange);
					} catch (ParseException e) {}
				}
			}
		}
		
		//Set up the file attachments
		List<Element> fileAttachments = new ArrayList<Element>();
		
		Iterator itFileAttachments = root.selectNodes("//fileAttachment").iterator();
		while (itFileAttachments.hasNext()) {
			fileAttachments.add((Element)itFileAttachments.next());
		}
		if (fileAttachments.size() == 0) {
			//No file attachments found, Look to see if this is a version attachment
			Iterator itVersionAttachments = root.selectNodes("//versionAttachment").iterator();
			while (itVersionAttachments.hasNext()) {
				fileAttachments.add((Element)itVersionAttachments.next());
			}
		}
		
		for (Element fileAttachment : fileAttachments) {
			String fileAttId = fileAttachment.attributeValue(ObjectKeys.XTAG_ATTRIBUTE_DATABASEID, "");
			FileAttachment fa = new FileAttachment();
			FileItem fi = new FileItem();
			fa.setFileItem(fi);
			fa.setId(fileAttId);
			fa.setFileExists(false);	//Assume file does not exist until we actually find it.
			
			//Set the owner and modifier of the attachment
			Element hs2 = (Element)fileAttachment.selectSingleNode("./historyStamp[@name='created']");
			if (hs2 == null) return null;
			String authorId2 = hs2.attributeValue("author", "");
			if (authorId2.equals("")) return null;
			Set ids2 = new HashSet();
			ids2.add(Long.valueOf(authorId2));
			SortedSet pList2 = bs.getProfileModule().getPrincipals(ids2);
			if (pList2.isEmpty()) return null;
			HistoryStamp creation2 = new HistoryStamp();
			creation2.setPrincipal((UserPrincipal)pList2.first());
			String when2 = hs2.attributeValue("when", "");
			SimpleDateFormat sdf2 = getSimpleDateFormat();
			Date date2;
			try {
				date2 = sdf2.parse(when2);
			} catch (ParseException e) {
				continue;
			}
			creation2.setDate(date2);
			fa.setCreation(creation2);
			//Set the owner of the attachment
			hs2 = (Element)fileAttachment.selectSingleNode("./historyStamp[@name='modified']");
			if (hs2 == null) return null;
			authorId2 = hs2.attributeValue("author", "");
			if (authorId2.equals("")) return null;
			ids2 = new HashSet();
			ids2.add(Long.valueOf(authorId2));
			pList2 = bs.getProfileModule().getPrincipals(ids2);
			if (pList2.isEmpty()) return null;
			HistoryStamp modification2 = new HistoryStamp();
			modification2.setPrincipal((UserPrincipal)pList2.first());
			when2 = hs2.attributeValue("when", "");
			sdf2 = getSimpleDateFormat();
			try {
				date2 = sdf2.parse(when2);
			} catch (ParseException e) {
				continue;
			}
			modification2.setDate(date2);
			fa.setModification(modification2);
			
			//Initialize fa in case it doesn't have a major and minor version
			Element lastVersionEle = (Element) fileAttachment.selectSingleNode("./property[@name='lastVersion']");
			if (lastVersionEle != null) {
				String lv = lastVersionEle.getText();
				fa.setMajorVersion(1);
				if (!lv.equals("")) fa.setMinorVersion(Integer.valueOf(lv) - 1);
			}

			Iterator itProperties = fileAttachment.selectNodes("./property").iterator();
			while (itProperties.hasNext()) {
				Element prop = (Element) itProperties.next();
				String name = prop.attributeValue("name");
				String value = prop.getText();
				if (name.equals("fileName")) {
					fi.setName(value);
				} else if (name.equals("fileLength")) {
					fi.setLength(Long.valueOf(value));
				} else if (name.equals("fileDescription")) {
					fi.setDescription(value);
				} else if (name.equals("repository")) {
					fa.setRepositoryName(value);
				} else if (name.equals("lastVersion")) {
					fa.setLastVersion(Integer.valueOf(value));
				} else if (name.equals("majorVersion")) {
					fa.setMajorVersion(Integer.valueOf(value));
				} else if (name.equals("minorVersion")) {
					fa.setMinorVersion(Integer.valueOf(value));
				} else if (name.equals("fileStatus")) {
					fa.setFileStatus(Integer.valueOf(value));
				}
			}
			//Finally, see if we can find the actual file version
			Set<Attachment> attachments = entity.getAttachments();
			FileAttachment fileAtt = null;
			for (Attachment attachment : attachments) {
				if (attachment instanceof FileAttachment) {
					if (attachment.getId().equals(fa.getId())) {
						fileAtt = (FileAttachment)attachment;
						break;
					}
				}
			}
			if (fileAtt != null) {
				//Found the attachment file, look for a version match
				Set<VersionAttachment> fileVersions = fileAtt.getFileVersions();
				for (VersionAttachment fv : fileVersions) {
					if (fv.getMajorVersion().equals(fa.getMajorVersion()) && 
							fv.getMinorVersion().equals(fa.getMinorVersion())) {
						fa.setId(fv.getId());
						fa.setFileExists(true);
						break;
					}
				}
			}
			//See if this file attachment already exists in the entry
			Iterator itAtts = entry.getAttachments().iterator();
			boolean attFound = false;
			while (itAtts.hasNext()) {
				Attachment att = (Attachment)itAtts.next();
				if (att instanceof FileAttachment) {
					FileAttachment fAtt = (FileAttachment) att;
					if (fa.getFileItem().getName().equals(fAtt.getFileItem().getName())) {
						attFound = true;
						//The file names are the same. See if this is a higher version
						if (fa.getMajorVersion() > fAtt.getMajorVersion() ||
								(fa.getMajorVersion() == fAtt.getMajorVersion() && fa.getMinorVersion() > fAtt.getMinorVersion())) {
							//This attachment is newer, replace the older one with this
							entry.removeAttachment(att);
							entry.addAttachment(fa);
							break;
						}
					}
				}
			}
			if (!attFound) {
				//The attachment wasn't found, so add this one
				entry.addAttachment(fa);
			}
			//See if this file attachment already exists in the entity
			itAtts = entity.getAttachments().iterator();
			attFound = false;
			while (itAtts.hasNext()) {
				Attachment att = (Attachment)itAtts.next();
				if (att instanceof FileAttachment) {
					FileAttachment fAtt = (FileAttachment) att;
					if (fa.getFileItem().getName().equals(fAtt.getFileItem().getName())) {
						attFound = true;
						fa.setEncrypted(fAtt.getEncrypted());
						fa.setEncryptionKey(fAtt.getEncryptionKey());
						break;
					}
				}
			}
			if (!attFound) {
				//The attachment wasn't found, so mark it as non-existent
				fa.setFileExists(false);
			}
		}
		
		//Build the title, description and all of the Custom Attributes 
		Map attributeMap = new HashMap();
		Iterator itAttr = root.attributeIterator();
		while (itAttr.hasNext()) {
			Attribute attr = (Attribute) itAttr.next();
			attributeMap.put(attr.getName(),attr.getValue());
		}

		Iterator itAttributes = root.selectNodes("//attribute").iterator();
		while (itAttributes.hasNext()) {
			Element ele = (Element) itAttributes.next();
			//Add the attributes
			String name = (String)ele.attributeValue("name");
			String type = (String)ele.attributeValue("type");
			attributeMap = new HashMap();
			itAttr = ele.attributeIterator();
			while (itAttr.hasNext()) {
				Attribute attr = (Attribute) itAttr.next();
				attributeMap.put(attr.getName(), attr.getValue());
			}
			//Add the data
			if (!Validator.isNull(name)) {
				String itemType = DefinitionHelper.findAttributeType(name, defDoc);
				if (name.equals("title")) {
					entry.setTitle((String)ele.getData());
				} else if (name.equals("description")) {
					Description desc = new Description((String)ele.getData());
					entry.setDescription(desc);
				} else {
					//This must be a custom attribute
					Object attrValue = getAttributeValueFromChangeLog(type, itemType, (String)ele.getData());
					if (type.equals("file") || type.equals("graphic")) {
						String fileAttachmentId = (String) attrValue;
						Set<FileAttachment> fAtts = entity.getFileAttachments();
						for (FileAttachment fa : fAtts) {
							if (fa.getId().equals(fileAttachmentId)) {
								Set faSet = new HashSet();
								//See if the file already exists as an attachment
								Set<FileAttachment> entry_fAtts = entry.getFileAttachments();
								for (FileAttachment entry_fa : entry_fAtts) {
									if (entry_fa.getFileItem().getName().equals(fa.getFileItem().getName())) {
										fa = entry_fa;
										break;
									}
								}
								faSet.add(fa);
								if (entry.getCustomAttribute(name) == null) entry.addCustomAttribute(name, faSet);
								break;
							}
						}
					} else {
						if (entry.getCustomAttribute(name) == null) entry.addCustomAttribute(name, attrValue);
					}
				}
			}
		}
		
		Iterator itEvents = root.selectNodes("//event").iterator();
		while (itEvents.hasNext()) {
			Element ele = (Element) itEvents.next();
			//Add the events
			Event event = new Event();
			Iterator itProperties = ele.selectNodes("./property").iterator();
			while (itProperties.hasNext()) {
				Element prop = (Element) itProperties.next();
				String name = prop.attributeValue("name");
				String value = prop.getText();
				if (name.equals("start")) {
					if (MiscUtil.hasString(value)) {
						event.setDtStart(value);
					}
				} else if (name.equals("calcStart")) {
					if (MiscUtil.hasString(value)) {
						event.setDtCalcStart(value);
					}
				} else if (name.equals("end")) {
					if (MiscUtil.hasString(value)) {
						event.setDtEnd(value);
					}
				} else if (name.equals("calcEnd")) {
					if (MiscUtil.hasString(value)) {
						event.setDtCalcEnd(value);
					}
				} else if (name.equals("duration")) {
					event.setDuration(value);
				} else if (name.equals("count")) {
					event.setCount(value);
				} else if (name.equals("until")) {
					event.setUntil(value);
				} else if (name.equals("frequency")) {
					event.setFrequency(value);
				} else if (name.equals("interval")) {
					event.setInterval(value);
				} else if (name.equals("timeZoneSensitive")) {
					event.setTimeZoneSensitive(Boolean.valueOf(value));
				} else if (name.equals("uid")) {
					event.setUid(value);
				} else if (name.equals("freeBusy")) {
					event.setFreeBusy(Event.FreeBusyType.valueOf(value));
				} else if (name.equals("bySecond")) {
					event.setBySecond(value);
				} else if (name.equals("byMinute")) {
					event.setByMinute(value);
				} else if (name.equals("byHour")) {
					event.setByHour(value);
				} else if (name.equals("byDay")) {
					event.setByDay(value);
				} else if (name.equals("byMonthDay")) {
					event.setByMonthDay(value);
				} else if (name.equals("byYearDay")) {
					event.setByYearDay(value);
				} else if (name.equals("byWeekNo")) {
					event.setByWeekNo(value);
				} else if (name.equals("byMonth")) {
					event.setByMonth(value);
				}
			}
			String eventId = ele.attributeValue("id", "");
			event.setId(eventId);
			//Try to find the event from its id
			Iterator itEntityEvents = entity.getEvents().iterator();
			while (itEntityEvents.hasNext()) {
				Event e = (Event)itEntityEvents.next();
				if (e.getId().equals(eventId)) {
					//Add the event
					event.setTimeZone(e.getTimeZone());
					if (entry.getCustomAttribute(e.getName()) == null) entry.addCustomAttribute(e.getName(), event);
					break;
				}
			}
		}
		//Make sure there is a title if it is missing
		if (Validator.isNull(entry.getTitle())) entry.setTitle(entity.getTitle());
		return entry;
	}

    public static Object getAttributeValueFromChangeLog(String type, String itemType, String value) {
    	if (type.equals(ObjectKeys.XTAG_TYPE_STRING)) {
    		if (itemType.equals("survey")) {
    			return new Survey(value);
    		} else {
    			return value;
    		}
    	} else if (type.equals(ObjectKeys.XTAG_TYPE_DESCRIPTION)) {
    		return new Description(value);
    	} else if (type.equals(ObjectKeys.XTAG_TYPE_COMMASEPARATED)) {
    		CommaSeparatedValue csv = new CommaSeparatedValue();
    		csv.setValue(value);
    		return csv;
    	} else if (type.equals(ObjectKeys.XTAG_TYPE_BOOLEAN)) {
    		return Boolean.valueOf(value);
    	} else if (type.equals(ObjectKeys.XTAG_TYPE_LONG)) {
    		return Long.valueOf(value);
    	} else if (type.equals(ObjectKeys.XTAG_TYPE_DATE)) {
    		SimpleDateFormat sdf = new SimpleDateFormat("MMM dd HH:mm:ss 'GMT' yyyy");  //Fri Nov 12 05:00:00 GMT 2010
    		Date date;
    		try {
    			date = sdf.parse(value.substring(4));
    			return date;
    		} catch (ParseException e) {
    			return null;
    		}
    	} else if (type.equals(ObjectKeys.XTAG_TYPE_SERIALIZED)) {
    		return new SSBlobSerializable(value);
    	} else if (type.equals(ObjectKeys.XTAG_TYPE_SERIALIZED)) {
    		try {
				return XmlFileUtil.generateXMLFromString(value);
			} catch (Exception e) {
				return null;
			}
    	} else {
    		return value;
    	}
    }

	public static Tabs.TabEntry initTabs(Tabs tabs, Binder binder, boolean clearData) throws Exception {
		return tabs.findTab(binder, clearData);
	}

	public static Tabs.TabEntry initTabs(PortletRequest request, Binder binder) throws Exception {
		//Set up the tabs
		String newTab = PortletRequestUtils.getStringParameter(request, WebKeys.URL_NEW_TAB, "1");
		return initTabs(Tabs.getTabs(request), binder, (!("0".equals(newTab))));
	}
	
	public static boolean isWebdavSupported(HttpServletRequest req) {
		//Is this ie8
		if (BrowserSniffer.is_ie_8(req)) return SPropsUtil.getBoolean("webdav.ie.8", false);
		
		//Is this ie7
		if (BrowserSniffer.is_ie_7(req)) return SPropsUtil.getBoolean("webdav.ie.7", false);
		
		//Is this ie6
		if (BrowserSniffer.is_ie_6(req)) return SPropsUtil.getBoolean("webdav.ie.6", false);
		
		//Is this moz5
		if (BrowserSniffer.is_mozilla_5(req)) return SPropsUtil.getBoolean("webdav.moz.5", false);
		
		return false;
	}
	
	public static void buildDashboardToolbar(RenderRequest request, RenderResponse response, 
			AllModulesInjected bs, Binder binder, Toolbar dashboardToolbar, Map model) {
		//	The "Manage dashboard" menu
		//See if the dashboard is being shown in the definition
		PortletURL url;
		boolean sharedUser = RequestContextHolder.getRequestContext().getUser().isShared();
		Integer	binderType;
		
		// We want to build a dashboard toolbar if the binder has the dashboard canvas in its definition
		// or the user is looking at a user workspace.
		binderType = binder.getDefinitionType();
		if (DefinitionHelper.checkIfBinderShowingDashboard(binder) ||
			(binderType != null && 
			 (binderType.intValue() == Definition.USER_WORKSPACE_VIEW ||
			  binderType.intValue() == Definition.EXTERNAL_USER_WORKSPACE_VIEW)) )
		{
			Map ssDashboard = (Map)model.get(WebKeys.DASHBOARD);
			boolean dashboardContentExists = DashboardHelper.checkIfAnyContentExists(ssDashboard);
			
			//This folder is showing the dashboard
			Map qualifiers = new HashMap();
			qualifiers.put(WebKeys.HELP_SPOT, "helpSpot.manageDashboard");
			qualifiers.put("linkclass", "ss_dashboard_config_control");
			dashboardToolbar.addToolbarMenu("3_manageDashboard", NLT.get("__dashboard_canvas"), "", qualifiers);
			if (!sharedUser || bs.getBinderModule().testAccess(binder, BinderOperation.setProperty)) {
				qualifiers = new HashMap();
				qualifiers.put("onClick", "ss_addDashboardComponents('" + response.getNamespace() + "_dashboardAddContentPanel');return false;");
				dashboardToolbar.addToolbarMenuItem("3_manageDashboard", "dashboard", NLT.get("toolbar.addPenlets"), "#", qualifiers);
			}
			if (dashboardContentExists) {
				qualifiers = new HashMap();
				qualifiers.put("textId", response.getNamespace() + "_dashboard_menu_controls");
				qualifiers.put("onClick", "ss_toggle_dashboard_hidden_controls('" + response.getNamespace() + "');return false;");
				dashboardToolbar.addToolbarMenuItem("3_manageDashboard", "dashboard", NLT.get("dashboard.showHiddenControls"), "#", qualifiers);
	
				if (!(binder instanceof TemplateBinder) && !sharedUser) {
					url = response.createActionURL();
					url.setParameter(WebKeys.ACTION, WebKeys.ACTION_MODIFY_DASHBOARD);
					url.setParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_SET_DASHBOARD_TITLE);
					url.setParameter(WebKeys.URL_BINDER_ID, binder.getId().toString());
					url.setParameter("_scope", "local");
					dashboardToolbar.addToolbarMenuItem("3_manageDashboard", "dashboard", NLT.get("dashboard.setTitle"), url);
	
					url = response.createActionURL();
					url.setParameter(WebKeys.ACTION, WebKeys.ACTION_MODIFY_DASHBOARD);
					url.setParameter(WebKeys.URL_BINDER_ID, binder.getId().toString());
					url.setParameter("_scope", "global");
					dashboardToolbar.addToolbarMenuItem("3_manageDashboard", "dashboard", NLT.get("dashboard.configure.global"), url);
				}
				//Check the access rights of the user
				if (bs.getBinderModule().testAccess(binder, BinderOperation.setProperty)) {
					url = response.createActionURL();
					url.setParameter(WebKeys.ACTION, WebKeys.ACTION_MODIFY_DASHBOARD);
					url.setParameter(WebKeys.URL_BINDER_ID, binder.getId().toString());
					url.setParameter("_scope", "binder");
					dashboardToolbar.addToolbarMenuItem("3_manageDashboard", "dashboard", NLT.get("dashboard.configure.binder"), url);
				}
	
				qualifiers = new HashMap();
				qualifiers.put("onClick", "ss_showHideAllDashboardComponents(this, '" + 
						response.getNamespace() + "_dashboardComponentCanvas', '" +
						binder.getId()+"');return false;");
				
				if (DashboardHelper.checkIfShowingAllComponents(binder)) {
					qualifiers.put("icon", "dashboard_hide.gif");
					dashboardToolbar.addToolbarMenu("4_showHideDashboard", NLT.get("toolbar.hideDashboard"), "#", qualifiers);
				} else {
					qualifiers.put("icon", "dashboard_show.gif");
					dashboardToolbar.addToolbarMenu("4_showHideDashboard", NLT.get("toolbar.showDashboard"), "#", qualifiers);
				}
			}
		}		
	}
	
	public static void setupWhatsNewBinderBeans(AllModulesInjected bs, Binder binder, Map model, String page) {	
		Integer pageSize = Integer.valueOf(SPropsUtil.getString("relevance.entriesPerBox"));
		setupWhatsNewBinderBeans(bs, binder, model, page, pageSize, "");
	}
	public static List<Long> setupWhatsNewBinderBeans(AllModulesInjected bs, Binder binder, Map model, String page, Integer pageSize,
			String type) {
		return setupWhatsNewBinderBeans(bs, binder, binder.getId(), model, page, pageSize, type);
	}
	public static List<Long> setupWhatsNewBinderBeans(AllModulesInjected bs, Binder binder, Long binderId, Map model, String page,
			Integer pageSize, String type) {		
        User user = RequestContextHolder.getRequestContext().getUser();
        //What's new is not available to the guest user
        if (user.isShared()) return new ArrayList<Long>() ;

        //Get the documents bean for the documents just created or modified
		Map options = new HashMap();
		if (page == null || page.equals("")) page = "0";
		Integer pageNumber = Integer.valueOf(page);
		if (pageNumber < 0) pageNumber = 0;
		model.put(WebKeys.PAGE_NUMBER, String.valueOf(pageNumber));
		int pageStart = pageNumber * pageSize;
		
		//Prepare for a standard search operation
		options.put(ObjectKeys.SEARCH_PAGE_ENTRIES_PER_PAGE, pageSize);
		
		Integer searchUserOffset = 0;
		Integer searchLuceneOffset = 0;
		options.put(ObjectKeys.SEARCH_OFFSET, searchLuceneOffset);
		options.put(ObjectKeys.SEARCH_USER_OFFSET, searchUserOffset);
		
		Integer maxHits = pageSize;
		options.put(ObjectKeys.SEARCH_USER_MAX_HITS, maxHits);
		
		Integer summaryWords = new Integer(20);
		options.put(WebKeys.SEARCH_FORM_SUMMARY_WORDS, summaryWords);
		
		Integer intInternalNumberOfRecordsToBeFetched = searchLuceneOffset + maxHits;
		if (searchUserOffset > 0) {
			intInternalNumberOfRecordsToBeFetched+=searchUserOffset;
		}
		options.put(ObjectKeys.SEARCH_MAX_HITS, intInternalNumberOfRecordsToBeFetched);

		options.put(ObjectKeys.SEARCH_OFFSET, Integer.valueOf(pageStart));
		int offset = ((Integer) options.get(ObjectKeys.SEARCH_OFFSET)).intValue();
		int maxResults = ((Integer) options.get(ObjectKeys.SEARCH_MAX_HITS)).intValue();
		
		List<String> trackedPlaces = new ArrayList<String>();
		List<String> trackedPeopleIds = new ArrayList<String>();
		if (type.equals(ObjectKeys.MOBILE_WHATS_NEW_VIEW_TRACKED)) {
			Long userWsId = user.getWorkspaceId();
			if (userWsId != null) {
				Binder userWs = bs.getBinderModule().getBinder(userWsId);
				trackedPlaces = SearchUtils.getTrackedPlacesIds(bs, userWs);
			}
			trackedPeopleIds = SearchUtils.getTrackedPeopleIds(bs, binder);
		} else if (type.equals(ObjectKeys.MOBILE_WHATS_NEW_VIEW_TEAMS)) {
			Collection myTeams = bs.getBinderModule().getTeamMemberships(user.getId(), org.kablink.teaming.module.shared.SearchUtils.fieldNamesList(Constants.DOCID_FIELD));
			Iterator itTeams = myTeams.iterator();
			while (itTeams.hasNext()) {
				Map team = (Map)itTeams.next();
				trackedPlaces.add((String)team.get(Constants.DOCID_FIELD));
			}
		} else if (type.equals(ObjectKeys.MOBILE_WHATS_NEW_VIEW_FAVORITES)) {
			Map userProperties = (Map) bs.getProfileModule().getUserProperties(user.getId()).getProperties();
			Object obj = userProperties.get(ObjectKeys.USER_PROPERTY_FAVORITES);
			Favorites f;
			if (obj != null && obj instanceof Document) {
				f = new Favorites((Document)obj);
				//fixup - have to store as string cause hibernate equals fails
				bs.getProfileModule().setUserProperty(null, ObjectKeys.USER_PROPERTY_FAVORITES, f.toString());
			} else {		
				f = new Favorites((String)obj);
			}
			List<Long> favIdList = f.getFavoritesBinderIdList();
			for (Long id : favIdList) {
				trackedPlaces.add(String.valueOf(id));
			}
		} else {
			if (binderId != null) {
				trackedPlaces.add(binderId.toString());
			}
		}
		List<Long> trackedBinderIds = new ArrayList<Long>();
		for (String s_id : trackedPlaces) trackedBinderIds.add(Long.valueOf(s_id));
		if (!trackedPlaces.isEmpty() || !trackedPeopleIds.isEmpty()) {
			Criteria crit = SearchUtils.entriesForTrackedPlacesAndPeople(bs, trackedPlaces, trackedPeopleIds);
			Map results = bs.getBinderModule().executeSearchQuery(crit, Constants.SEARCH_MODE_NORMAL, offset, maxResults, null);
			model.put(WebKeys.WHATS_NEW_BINDER, results.get(ObjectKeys.SEARCH_ENTRIES));
			model.put(WebKeys.SEARCH_TOTAL_HITS, results.get(ObjectKeys.SEARCH_COUNT_TOTAL));
			
			Map places = new HashMap();
	    	List items = (List) results.get(ObjectKeys.SEARCH_ENTRIES);
	    	if (items != null) {
		    	Iterator it = items.iterator();
		    	while (it.hasNext()) {
		    		Map entry = (Map)it.next();
					String id = (String)entry.get(Constants.BINDER_ID_FIELD);
					if (id != null) {
						Long bId = new Long(id);
						if (!places.containsKey(id)) {
							try {
								Binder place = bs.getBinderModule().getBinder(bId);
								places.put(id, place);
							} catch(Exception e) {}
						}
					}
		    	}
	    	}
	    	model.put(WebKeys.WHATS_NEW_BINDER_FOLDERS, places);

		} else {
			model.put(WebKeys.WHATS_NEW_BINDER, new ArrayList());
			model.put(WebKeys.SEARCH_TOTAL_HITS, 0);
		}
		
    	return trackedBinderIds;
	}
	public static void setupUnseenBinderBeans(AllModulesInjected bs, Binder binder, Map model, String page) {		
		//Get a list of unseen entries in this binder tree
        User user = RequestContextHolder.getRequestContext().getUser();
        //What's unread is not available to the guest user
        if (user.isShared()) return;

		Map options = new HashMap();
		if (page == null || page.equals("")) page = "0";
		Integer pageNumber = Integer.valueOf(page);
		if (pageNumber < 0) pageNumber = 0;
		model.put(WebKeys.PAGE_NUMBER, String.valueOf(pageNumber));
		int intEntriesPerPage = Integer.valueOf(SPropsUtil.getString("search.whatsNew.entriesPerPage"));
		int pageStart = pageNumber * intEntriesPerPage;
		
		//Prepare for a standard search operation
		String entriesPerPage = SPropsUtil.getString("search.unseen.maxEntries");
		options.put(ObjectKeys.SEARCH_PAGE_ENTRIES_PER_PAGE, new Integer(entriesPerPage));
		
		Integer searchUserOffset = 0;
		Integer searchLuceneOffset = 0;
		options.put(ObjectKeys.SEARCH_OFFSET, searchLuceneOffset);
		options.put(ObjectKeys.SEARCH_USER_OFFSET, searchUserOffset);
		
		Integer maxHits = new Integer(entriesPerPage);
		options.put(ObjectKeys.SEARCH_USER_MAX_HITS, maxHits);
		
		Integer summaryWords = new Integer(20);
		options.put(WebKeys.SEARCH_FORM_SUMMARY_WORDS, summaryWords);
		
		Integer intInternalNumberOfRecordsToBeFetched = searchLuceneOffset + maxHits;
		if (searchUserOffset > 0) {
			intInternalNumberOfRecordsToBeFetched+=searchUserOffset;
		}
		options.put(ObjectKeys.SEARCH_MAX_HITS, intInternalNumberOfRecordsToBeFetched);

		options.put(ObjectKeys.SEARCH_OFFSET, Integer.valueOf(0));
		int offset = ((Integer) options.get(ObjectKeys.SEARCH_OFFSET)).intValue();
		int maxResults = ((Integer) options.get(ObjectKeys.SEARCH_MAX_HITS)).intValue();
		
		List<String> trackedPlaces = new ArrayList<String>();
		trackedPlaces.add(binder.getId().toString());
	    //get entries created within last 30 days
		Date creationDate = new Date();
		creationDate.setTime(creationDate.getTime() - ObjectKeys.SEEN_TIMEOUT_DAYS*24*60*60*1000);
		String startDate = DateTools.dateToString(creationDate, DateTools.Resolution.SECOND);
		String now = DateTools.dateToString(new Date(), DateTools.Resolution.SECOND);
		Criteria crit = SearchUtils.entriesForTrackedPlaces(bs, trackedPlaces);
		crit.add(org.kablink.util.search.Restrictions.between(
				Constants.MODIFICATION_DATE_FIELD, startDate, now));
		Map results = bs.getBinderModule().executeSearchQuery(crit, Constants.SEARCH_MODE_NORMAL, offset, maxResults,
				org.kablink.teaming.module.shared.SearchUtils.fieldNamesList(Constants.DOCID_FIELD,Constants.LASTACTIVITY_FIELD,Constants.MODIFICATION_DATE_FIELD,Constants.BINDER_ID_FIELD));
		List<Map> entries = (List<Map>) results.get(ObjectKeys.SEARCH_ENTRIES);
		SeenMap seen = bs.getProfileModule().getUserSeenMap(null);
		List<Map> unseenEntries = new ArrayList();
		for (Map entry : entries) {
			//Only show the unseen entries
			if (!seen.checkIfSeen(entry)) unseenEntries.add(entry);
			if (unseenEntries.size() >= pageStart + intEntriesPerPage) break;
		}
		if (unseenEntries.size() > pageStart && unseenEntries.size() >= pageStart + intEntriesPerPage) {
			model.put(WebKeys.WHATS_NEW_BINDER, unseenEntries.subList(pageStart, pageStart + intEntriesPerPage));
		} else if (unseenEntries.size() > pageStart) {
			model.put(WebKeys.WHATS_NEW_BINDER, unseenEntries.subList(pageStart, unseenEntries.size()));
		}

		if (unseenEntries.size() > pageStart) {
			Map places = new HashMap();
	    	Iterator it = unseenEntries.iterator();
	    	while (it.hasNext()) {
	    		Map entry = (Map)it.next();
				String id = (String)entry.get(Constants.BINDER_ID_FIELD);
				if (id != null) {
					Long bId = new Long(id);
					if (!places.containsKey(id)) {
						Binder place = bs.getBinderModule().getBinder(bId);
						places.put(id, place);
					}
				}
	    	}
	    	model.put(WebKeys.WHATS_NEW_BINDER_FOLDERS, places);
		}
	    model.put(WebKeys.WHATS_UNSEEN_TYPE, true);
	}
	
	public static void updateUserStatus(Long folderId, User user) {
		// Default to updating the user's status using the most recent
		// entry in their MiniBlog folder.
		updateUserStatus(folderId, null, user);
	}
	
	public static void updateUserStatus(Long folderId, Long entryId, User user) {
		Folder	miniBlog;
		FolderModule  folderModule  = getFolderModule();
		BinderModule  binderModule  = getBinderModule();
		ProfileModule profileModule = getProfileModule();
		ReportModule  reportModule  = getReportModule();
		
		// If the user is referencing a mini blog folder that we can't
		// access or that has been deleted, we ignore it and use the
		// folder that we were given.
		Long miniBlogId = user.getMiniBlogId();
		boolean userHasMiniBlog = (null != miniBlogId); 
		if (userHasMiniBlog) {
			miniBlog = null;
        	try {
        		miniBlog = folderModule.getFolder(miniBlogId); 
        		if (miniBlog.isDeleted()) {
        			miniBlog = null;
        		}
        	} catch (Exception ex) {
				logger.debug("BinderHelper.updateUserStatus(Exception:  '" + MiscUtil.exToString(ex) + "'):  Ignoring miniBlog");
				miniBlog = null;
        	}
    		userHasMiniBlog = (null != miniBlog); 
		}
		
		// Does this user have a MiniBlog folder yet?
		if (!userHasMiniBlog) {
			try {
				Folder folder = folderModule.getFolder(folderId);
				// Is this folder in the user's workspace
				Long workspaceId = user.getWorkspaceId();
				if (workspaceId != null) {
					Binder parentBinder = folder.getParentBinder();
					while (parentBinder != null) {
						if (parentBinder.getId().longValue() == workspaceId.longValue()) {
							// Does this Folder have a default view defined?
					   		Definition defaultBinderView = folder.getDefaultViewDef();
					   		if (null != defaultBinderView) {
					   			// Yes!  Is the default view a MiniBlog Folder?
					   			if (defaultBinderView.getName().equals("_miniBlogFolder")) {
				   					// Yes!  Use it as this user's MiniBlog.
				   					miniBlogId = folder.getId();
				   				}
					   		}
					   		break;
						}
						parentBinder = parentBinder.getParentBinder();
					}
				}
			} catch(Exception e) {}
		}

		// Does the folderId refer to the user's MiniBlog? 
		if ((null != miniBlogId) && (miniBlogId.longValue() == folderId.longValue())) {
			try {
				// Yes!  Can we access the MiniBlog folder?
				miniBlog = (Folder) binderModule.getBinder(miniBlogId);
				if (miniBlog.isDeleted()) {
					//The miniblog folder doesn't exist anymore.
					miniBlog = null;
				}
			} catch(NoBinderByTheIdException e) {
				//The miniblog folder doesn't exist anymore,
				logger.debug("BinderHelper.updateUserStatus(NoBinderByTheIdException):  Ignoring miniBlog");
				miniBlog = null;
			}
			if (null != miniBlog) {
				// Yes!  Were we given the entryId for the MiniBlog
				// entry to update the user's status with?
				String	text = null;
				if (null == entryId) {
					// No!  Use the most recent entry.  If there are no
					// entries, we'll just set the status text to an
					// empty string so that the string displayed
					// gets removed.
					Criteria crit = SearchUtils.entriesForTrackedMiniBlogs(new Long[]{user.getId()});
					crit.add(Restrictions.eq(Constants.BINDER_ID_FIELD, folderId.toString()));
					Map results   = binderModule.executeSearchQuery(crit, Constants.SEARCH_MODE_NORMAL, 0, 1,
							org.kablink.teaming.module.shared.SearchUtils.fieldNamesList(Constants.DESC_FIELD));
			    	List<Map> items = (List) results.get(ObjectKeys.SEARCH_ENTRIES);
			    	boolean found = false;
			    	for (Map item: items) {
			    		text  = ((String) item.get(Constants.DESC_FIELD));
			    		found = (null != text);
			    		if (found) {
			    			text = text.trim();
			    			if (0 < text.length() && ('<' == text.charAt(0))) {
			    				text = text.replaceAll("\\<.*?\\>","");
			    			}
			    		}
			    		break;
			    	}
			    	if (!found) {
			    		text = "";
			    	}
				}
				else {
					// Yes, we were given the entryId for the MiniBlog
					// entry to update the user's status with!  Can we
					// access it?
					FolderEntry miniBlogEntry = folderModule.getEntry(miniBlogId, entryId);
					if (null != miniBlogEntry) {
						// Yes!  Read the description from it.
						text = miniBlogEntry.getDescription().getStrippedText();
					}
				}
				
				// Do we have the text to update the user's status with?
				if (null != text) {
					// Yes!  If we need to set the folder received as
					// the user's mini blog...
					if (!userHasMiniBlog) {
						// ...set it...
						profileModule.setUserMiniBlog(user, miniBlogId);
					}
					
					// ...and update the status text.
					profileModule.setStatus(text);
					profileModule.setStatusDate(new Date());
					reportModule.addStatusInfo(user);
				}
			}
		}
	}
	
	public static void sendMailOnEntryCreate(AllModulesInjected bs, ActionRequest request, 
			Long folderId, Long entryId) {
        User user = RequestContextHolder.getRequestContext().getUser();
		MapInputData inputData = new MapInputData(request.getParameterMap());
		Set<Long> idList       = LongIdUtil.getIdsAsLongSet(getInputValues(inputData,"_sendMail_toList"));
		Set<Long> idListGroups = LongIdUtil.getIdsAsLongSet(getInputValues(inputData,"_sendMail_toList_groups"));
		Set<Long> idListTeams  = LongIdUtil.getIdsAsLongSet(getInputValues(inputData,"_sendMail_toList_teams"));
		
		// If we're supposed to automatically notify attendees...
		String cBox = notifyAttendeeCBoxes(inputData);
		if (null != cBox) {
			// ...merge the attendee ID sets into the email ID sets
			// ...we just pulled from inputData.
			String setKey2 = cBox.substring(0, cBox.indexOf(WebKeys.AUTO_NOTIFY_TAIL));
			idList         = mergeIdSets(inputData, idList,        setKey2);
			idListGroups   = mergeIdSets(inputData, idListGroups, (setKey2 + WebKeys.AUTO_NOTIFY_GROUPS_TAIL));
			idListTeams    = mergeIdSets(inputData, idListTeams,  (setKey2 + WebKeys.AUTO_NOTIFY_TEAMS_TAIL));
		}
		
		String toTeam = PortletRequestUtils.getStringParameter(request, "_sendMail_toTeam", "");
		if ((0 < idList.size()) || (0 < idListGroups.size()) || (0 < idListTeams.size()) || !toTeam.equals("")) {
			FolderEntry entry = bs.getFolderModule().getEntry(folderId, entryId);
			ArrayList<Long> handledIds = new ArrayList<Long>();
			Set<Long> recipients = new HashSet<Long>();
			if (0 < idList.size())       handleEmailRecipients(handledIds, recipients, idList);
			if (0 < idListGroups.size()) handleEmailRecipients(handledIds, recipients, idListGroups);
			if (0 < idListTeams.size())  handleTeamRecipients(handledIds,  recipients, idListTeams, bs.getBinderModule());
			if (!toTeam.equals(""))      handleEmailRecipients(handledIds, recipients, bs.getBinderModule().getTeamMemberIds( entry.getParentFolder() ));
			
			if (!recipients.isEmpty()) {
				try {
					String title = PortletRequestUtils.getStringParameter(request, "title", "--no title--", false);
					String body = PortletRequestUtils.getStringParameter(request, "_sendMail_body", "", false);
					String subject = PortletRequestUtils.getStringParameter(request, "_sendMail_subject", "\"" + title + "\" entry notification", false);
					String includeAttachments = PortletRequestUtils.getStringParameter(request, "_sendMail_includeAttachments", "");
					boolean incAtt = (!includeAttachments.equals(""));
					Set emailAddress = new HashSet();
					//See if this user wants to be BCC'd on all mail sent out
					String bccEmailAddress = user.getBccEmailAddress();
					if (bccEmailAddress != null && !bccEmailAddress.equals("")) {
						if (!emailAddress.contains(bccEmailAddress.trim())) {
							//Add the user's chosen bcc email address
							emailAddress.add(bccEmailAddress.trim());
						}
					}
					bs.getAdminModule().sendMail(entry, recipients, null, emailAddress, null, null, subject, 
							new Description(body, Description.FORMAT_HTML), incAtt);
				} catch (Exception e) {
					logger.debug("BinderHelper.sendMailOnCreate(Exception:  '" + MiscUtil.exToString(e) + "'):  Ignored");
					//TODO Log that mail wasn't sent
				}
			}
		}
	}

	/*
	 * Returns a Set<Long> containing the unique members of set1 merged
	 * with set2.
	 */
	private static Set<Long> mergeIdSets(MapInputData inputData, Set<Long> set1, String setKey2) {
		Set<Long> mergedIdSet = new HashSet<Long>();

		// Track the items in list1...
		for (Long id: set1) {
			mergedIdSet.add(id);
		}

		// ...merge in the items from list2...
		String[] list2 = getInputValues(inputData, setKey2);
		Set<Long> set2 = LongIdUtil.getIdsAsLongSet(list2);
		for (Long id: set2) {
			if (!(mergedIdSet.contains(id))) {
				mergedIdSet.add(id);
			}
		}

		// ...and return the merged Set<Long>.
		return mergedIdSet;
	}

	/*
	 * Checks inputData for one of the known attendee check boxes being
	 * checked.  If one is checked, its ID is returned.  Otherwise,
	 * null is returned.
	 */
	private static String notifyAttendeeCBoxes(MapInputData inputData) {
		final String[] AUTO_NOTIFY_CBOX_IDS = {
			WebKeys.AUTO_NOTIFY_ATTENDEES,		// For Calendar  entries.
			WebKeys.AUTO_NOTIFY_ASSIGNEES,		// For Task      entries.
			WebKeys.AUTO_NOTIFY_RESPONSIBLES,	// For Milestone entries.
		};
		
		String cBox = null;
		for (int i = 0; i < AUTO_NOTIFY_CBOX_IDS.length; i += 1) {
			cBox = notifyAttendeeCBox(inputData, AUTO_NOTIFY_CBOX_IDS[i]);
			if (null != cBox) {
				break;
			}
		}

		return cBox;
	}

	/*
	 * Checks to see if the check box cBox has been marked as checked
	 * in inputData.  If it has, cBox is returned.  Otherwise, null is
	 * returned.
	 */
	private static String notifyAttendeeCBox(MapInputData inputData, String cBox) {
		String reply;
		String cBoxData = inputData.getSingleValue(cBox);
		if ((null != cBoxData) && (cBoxData.equalsIgnoreCase("true") || cBoxData.equalsIgnoreCase("on") || cBoxData.equalsIgnoreCase(cBox))) {
			reply = cBox;
		}
		else {
			reply = null;
		}

		return reply;
	}
	
	private static String[] getInputValues(InputDataAccessor inputData, String parameter) {
		String[] reply = inputData.getValues(parameter);
		if (null == reply) {
			reply = new String[0];
		}
		return reply;
	}
	
	private static void handleEmailRecipients(ArrayList<Long> handledIds, Set<Long> recipients, Set<Long> newRecipients) {
		// Scan the new recipients.
		for (Long id:newRecipients) {
			// Have we already handled this recipient?
			if ((-1) == handledIds.indexOf(id)) {
				// No!  Mark it has having been handled and add it to
				// the recipients list.
				handledIds.add(id);
				recipients.add(id);
			}
		}
	}
	
	private static void handleTeamRecipients(ArrayList<Long> handledIds, Set<Long> recipients, Set<Long> teamWSs, BinderModule bm) {
		// Scan the team workspaces.
		for (Long id:teamWSs) {
			// Have we already handled this team?
			if ((-1) == handledIds.lastIndexOf(id)) {
				// No!  Mark it has having been handled and handle
				// the team members.
				handledIds.add(id);
				Set<Long> teamMemberIds = bm.getTeamMemberIds(id, true);
				handleEmailRecipients(handledIds, recipients, teamMemberIds);
			}
		}
	}
	
	public static void subscribeToThisEntry(AllModulesInjected bs, ActionRequest request, 
			Long folderId, Long entryId) {
		String subscribeElementPresent = PortletRequestUtils.getStringParameter(request, "_subscribe_element_present", "");
		//test attachments first for higher precedence
		Map<Integer,String[]> styles = new HashMap();
		for (int i=2; i<6; ++i) {
			if (i == 4) continue;
			String[] address = PortletRequestUtils.getStringParameters(request, "_subscribe"+i, false);
			if (address == null || address.length ==0) continue;
			else styles.put(Integer.valueOf(i), address);
		}
		if (!styles.isEmpty()) {
			bs.getFolderModule().setSubscription(folderId, entryId, styles);
			
		} else if (Validator.isNotNull(subscribeElementPresent)) {
			//The user turned off the subscription
			bs.getFolderModule().setSubscription(folderId, entryId, null);
		}
	}

	public static HashMap getEntryAccessMap(AllModulesInjected bs, Map model, FolderEntry entry) {
		Map accessControlMap = (Map) model.get(WebKeys.ACCESS_CONTROL_MAP);
		HashMap entryAccessMap = new HashMap();
		if (accessControlMap.containsKey(entry.getId())) {
			entryAccessMap = (HashMap) accessControlMap.get(entry.getId());
		}
		return entryAccessMap;
	}
	
	public static void setAccessControlForAttachmentList(AllModulesInjected bs, 
			Map model, FolderEntry entry, User user) {

		Map accessControlEntryMap = getAccessControlEntityMapBean(model, entry);

		boolean reserveAccessCheck = false;
		boolean isUserBinderAdministrator = false;
		boolean isEntryReserved = false;
		boolean isLockedByAndLoginUserSame = false;

		if (bs.getFolderModule().testAccess(entry, FolderOperation.reserveEntry)) {
			reserveAccessCheck = true;
		}
		if (bs.getFolderModule().testAccess(entry, FolderOperation.overrideReserveEntry)) {
			isUserBinderAdministrator = true;
		}
		
		HistoryStamp historyStamp = entry.getReservation();
		if (historyStamp != null) isEntryReserved = true;

		if (isEntryReserved) {
			Principal lockedByUser = historyStamp.getPrincipal();
			if (lockedByUser.getId().equals(user.getId())) {
				isLockedByAndLoginUserSame = true;
			}
		}
		
		if (bs.getFolderModule().testAccess(entry, FolderOperation.addReply)) {
			accessControlEntryMap.put("addReply", new Boolean(true));
		}		
		
		if (bs.getFolderModule().testAccess(entry, FolderOperation.modifyEntry)) {
			if (reserveAccessCheck && isEntryReserved && !(isUserBinderAdministrator || isLockedByAndLoginUserSame) ) {
			} else {
				accessControlEntryMap.put("modifyEntry", new Boolean(true));
			}
		}
		
		if (bs.getFolderModule().testAccess(entry, FolderOperation.deleteEntry)) {
			if (reserveAccessCheck && isEntryReserved && !(isUserBinderAdministrator || isLockedByAndLoginUserSame) ) {
			} else {
				accessControlEntryMap.put("deleteEntry", new Boolean(true));
			}
		}		
	}

	public static Map prepareSearchResultPage(AllModulesInjected bs, RenderRequest request, Tabs tabs) throws Exception {
		Map model = new HashMap();

		Integer tabId = PortletRequestUtils.getIntParameter(request, WebKeys.URL_TAB_ID, -1);
		//new search
		Tabs.TabEntry tab = tabs.findTab(Tabs.SEARCH, tabId);
		if (tab == null) {
			prepareSearchResultData(bs, request, tabs, model, null);
			return model;
		}
		// get query and options from tab
		Document searchQuery = tab.getQueryDoc();
		Map options = getOptionsFromTab(tab);
		Integer pageNo = PortletRequestUtils.getIntParameter(request, WebKeys.URL_PAGE_NUMBER, -1);
		if (pageNo != -1) options.put(Tabs.PAGE, pageNo);				

		// get page no and actualize options
		// execute query
		// actualize tabs info
		actualizeOptions(options, request);
		Map results =  bs.getBinderModule().executeSearchQuery(searchQuery, Constants.SEARCH_MODE_NORMAL, options);
		prepareSearchResultPage(bs, results, model, searchQuery, options, tab);
		
		return model;
	}

	public static Map prepareSavedQueryResultData(AllModulesInjected bs, RenderRequest request, Tabs tabs, Map options) throws PortletRequestBindingException {
		Map model = new HashMap();

		String queryName = PortletRequestUtils.getStringParameter(request, WebKeys.URL_SEARCH_QUERY_NAME, "", false);
		User currentUser = RequestContextHolder.getRequestContext().getUser();
		
		// get query and options from tab		
		Document searchQuery = getSavedQuery(bs, queryName, bs.getProfileModule().getUserProperties(currentUser.getId()));
		if (searchQuery == null) {
			model.putAll(prepareSavedQueries(bs));
			return model;
		}
		
		// get page no and actualize options
		// execute query
		// actualize tabs info
		if(options != null)
			options.putAll(prepareSearchOptions(bs, request));
		else
			options = prepareSearchOptions(bs, request);
		
		actualizeOptions(options, request);
		Element preDeletedOnlyTerm = (Element)searchQuery.getRootElement().selectSingleNode("//filterTerms/filterTerm[@preDeletedOnly='true']");
		if (preDeletedOnlyTerm != null) {
			options.put(ObjectKeys.SEARCH_PRE_DELETED, Boolean.TRUE);
		}

		options.put(Tabs.TITLE, queryName);
		Map results =  bs.getBinderModule().executeSearchQuery(searchQuery, Constants.SEARCH_MODE_NORMAL, options);
		
		Tabs.TabEntry tab = tabs.addTab(searchQuery, options);
		
		prepareSearchResultPage(bs, results, model, searchQuery, options, tab);
		
		Element filterTerm = (Element)searchQuery.getRootElement().selectSingleNode("//filterTerms/filterTerm[@filterType='text' and @caseSensitive='true']");
		if (filterTerm != null) {
			model.put(WebKeys.SEARCH_FORM_CASE_SENSITIVE, true);
		}
		if (preDeletedOnlyTerm != null) {
			model.put(WebKeys.SEARCH_FORM_PREDELETED_ONLY, true);
		}
		
		return model;
	}
	
	public static void prepareSearchResultData(AllModulesInjected bs, RenderRequest request, Tabs tabs, Map model, Map options) 
			throws Exception {

		User user = RequestContextHolder.getRequestContext().getUser();
		if(options != null) {
			options.putAll(prepareSearchOptions(bs, request));
		}
		else {
			options = prepareSearchOptions(bs, request);			
		}
		model.put("quickSearch", options.get(WebKeys.SEARCH_FORM_QUICKSEARCH));
		
		SearchFilterRequestParser requestParser = new SearchFilterRequestParser(request, bs.getDefinitionModule());
		Document searchQuery = requestParser.getSearchQuery();
		//See if this is a request to search within the My Files collection
		if (ObjectKeys.SEARCH_SCOPE_MY_FILES.equals(options.get(ObjectKeys.SEARCH_SCOPE))) {
			//Build the ancilary criteria for searching within the My Files collection
			Criteria crit = SearchUtils.getMyFilesSearchCriteria(bs, user.getWorkspaceId(), true, false, false, false, true);
			// Perform the search for the binders to search...
			int maxResults = ((Integer) options.get(ObjectKeys.SEARCH_MAX_HITS)).intValue();
			Map searchResults = bs.getBinderModule().executeSearchQuery(
				crit,
				Constants.SEARCH_MODE_NORMAL,
				0,
				maxResults,
				org.kablink.teaming.module.shared.SearchUtils.fieldNamesList(Constants.DOCID_FIELD,Constants.DOC_TYPE_FIELD));
			
			// Get the binder hits
			List<Map> searchEntries = ((List<Map>) searchResults.get(ObjectKeys.SEARCH_ENTRIES));
			List binderIds = new ArrayList();
			for (Map entryMap:  searchEntries) {
				String docId = (String)entryMap.get(Constants.DOCID_FIELD);
				String docType = (String)entryMap.get(Constants.DOC_TYPE_FIELD);
				if (docId != null && Constants.DOC_TYPE_BINDER.equals(docType)) {
					binderIds.add(docId);
				}
			}
			//Now, using the binderIds, get the entries that match the search options
			if (binderIds.isEmpty()) {
				//Make sure there is some binderId to search for or the search returns everything
				binderIds.add("xxx");
			}
			crit = SearchUtils.entriesForTrackedPlacesEntriesAndPeople(bs, binderIds, null, null, false, 
					Constants.LASTACTIVITY_FIELD, true, true, true);
			options.put(ObjectKeys.SEARCH_CRITERIA_AND, crit);
		} else if (ObjectKeys.SEARCH_SCOPE_NET_FOLDERS.equals(options.get(ObjectKeys.SEARCH_SCOPE))) {
			//Search just the user's net folders
			//Build the ancilary criteria for searching within the Net Folders collection
			Criteria crit = SearchUtils.getNetFoldersSearchCriteria(bs);
			// Perform the search for the binders to search...
			int maxResults = ((Integer) options.get(ObjectKeys.SEARCH_MAX_HITS)).intValue();
			Binder nfBinder = SearchUtils.getNetFoldersRootBinder();
			Map searchResults = bs.getBinderModule().searchFolderOneLevelWithInferredAccess(
					crit,
					Constants.SEARCH_MODE_SELF_CONTAINED_ONLY,
					GwtUIHelper.getOptionInt(options, ObjectKeys.SEARCH_OFFSET,   0),
					GwtUIHelper.getOptionInt(options, ObjectKeys.SEARCH_MAX_HITS, ObjectKeys.SEARCH_MAX_HITS_SUB_BINDERS),
					nfBinder);
			//Now, remove any results where the current user does not have AllowNetFolderAccess rights
			SearchUtils.removeNetFoldersWithNoRootAccess(searchResults);
			
			// Get the binder hits
			List<Map> searchEntries = ((List<Map>) searchResults.get(ObjectKeys.SEARCH_ENTRIES));
			List binderIds = new ArrayList();
			for (Map entryMap:  searchEntries) {
				String docId = (String)entryMap.get(Constants.DOCID_FIELD);
				String docType = (String)entryMap.get(Constants.DOC_TYPE_FIELD);
				if (docId != null && Constants.DOC_TYPE_BINDER.equals(docType)) {
					binderIds.add(docId);
				}
			}
			//Now, using the binderIds, get the entries that match the search options
			if (binderIds.isEmpty()) {
				//Make sure there is some binderId to search for or the search returns everything
				binderIds.add("xxx");
			}
			crit = SearchUtils.entriesForTrackedPlacesEntriesAndPeople(bs, binderIds, null, null, false, Constants.LASTACTIVITY_FIELD, true, true, true);
			options.put(ObjectKeys.SEARCH_CRITERIA_AND, crit);
		} else if (ObjectKeys.SEARCH_SCOPE_SHARED_WITH_ME.equals(options.get(ObjectKeys.SEARCH_SCOPE))) {
			//Search the user's "shared with me" files
			//Build the ancilary criteria for searching within this collection
			Criteria crit = SearchUtils.getSharedWithMePrincipalsCriteria();
			
			// Perform the search for the binders to search...
			int maxResults = ((Integer) options.get(ObjectKeys.SEARCH_MAX_HITS)).intValue();
			Map searchResults = bs.getBinderModule().executeSearchQuery(
				crit,
				Constants.SEARCH_MODE_NORMAL,
				0,
				maxResults,
				org.kablink.teaming.module.shared.SearchUtils.fieldNamesList(Constants.DOCID_FIELD,Constants.DOC_TYPE_FIELD));
			
			// Get the binder hits
			List<Map> searchEntries = ((List<Map>) searchResults.get(ObjectKeys.SEARCH_ENTRIES));
			List<String> binderIds = new ArrayList();
			for (Map entryMap:  searchEntries) {
				String docId = (String)entryMap.get(Constants.DOCID_FIELD);
				String docType = (String)entryMap.get(Constants.DOC_TYPE_FIELD);
				if (docId != null && Constants.DOC_TYPE_BINDER.equals(docType)) {
					binderIds.add(docId);
				}
			}
			crit = SearchUtils.getSharedWithMeSearchCriteria(binderIds);
			options.put(ObjectKeys.SEARCH_CRITERIA_AND, crit);
		} else if (ObjectKeys.SEARCH_SCOPE_SHARED_BY_ME.equals(options.get(ObjectKeys.SEARCH_SCOPE))) {
			//Search the user's "shared by me" files
			//Build the ancillary criteria for searching within this collection
			//First, get the list of shared binder ids
			Criteria crit = SearchUtils.getSharedByMeFoldersSearchCriteria(bs, null);
			// Perform the search to get the binders to be searched...
			int maxResults = ((Integer) options.get(ObjectKeys.SEARCH_MAX_HITS)).intValue();
			Map searchResults = bs.getBinderModule().executeSearchQuery(
				crit,
				Constants.SEARCH_MODE_NORMAL,
				0,
				maxResults,
				org.kablink.teaming.module.shared.SearchUtils.fieldNamesList(Constants.DOCID_FIELD,Constants.DOC_TYPE_FIELD));
			
			// Get the binder hits
			List<Map> searchEntries = ((List<Map>) searchResults.get(ObjectKeys.SEARCH_ENTRIES));
			List<String> binderIds = new ArrayList();
			for (Map entryMap:  searchEntries) {
				String docId = (String)entryMap.get(Constants.DOCID_FIELD);
				String docType = (String)entryMap.get(Constants.DOC_TYPE_FIELD);
				if (docId != null && Constants.DOC_TYPE_BINDER.equals(docType)) {
					binderIds.add(docId);
				}
			}
			//Now get the final search criteria
			crit = SearchUtils.getSharedByMeSearchCriteria(bs, null, binderIds);
			options.put(ObjectKeys.SEARCH_CRITERIA_AND, crit);
		} else if (ObjectKeys.SEARCH_SCOPE_CURRENT.equals(options.get(ObjectKeys.SEARCH_SCOPE))) {
			//Search the current folder (if known)
			String searchContextBinderId = PortletRequestUtils.getStringParameter(request, ObjectKeys.SEARCH_CONTEXT_BINDER_ID, "");
			List binderIds = new ArrayList();
			if (!searchContextBinderId.equals("")) {
				binderIds.add(searchContextBinderId);
			}
			Boolean includeSubFolders = Boolean.TRUE;
			if (options.containsKey(ObjectKeys.SEARCH_INCLUDE_NESTED_BINDERS)) {
				includeSubFolders = (Boolean)options.get(ObjectKeys.SEARCH_INCLUDE_NESTED_BINDERS);
			}
			Criteria crit = SearchUtils.entriesForTrackedPlacesEntriesAndPeople(bs, binderIds, null, null, false, 
					Constants.LASTACTIVITY_FIELD, includeSubFolders, true, true);
			options.put(ObjectKeys.SEARCH_CRITERIA_AND, crit);
		}
		
		Map results =  bs.getBinderModule().executeSearchQuery(searchQuery, Constants.SEARCH_MODE_NORMAL, options);
		
		Tabs.TabEntry tab = tabs.addTab(searchQuery, options);
		
		prepareSearchResultPage(bs, results, model, searchQuery, options, tab);
	}
	
	public static void prepareSearchResultPage (AllModulesInjected bs, Map results, Map model, Document query, 
			Map options, Tabs.TabEntry tab) {
		
		model.put(WebKeys.URL_TAB_ID, tab.getTabId());
		//save tab options
		tab.setData(options);
		SearchFilterToMapConverter searchFilterConverter = 
			new SearchFilterToMapConverter(bs, query);
		model.putAll(searchFilterConverter.convertAndPrepareFormData());
		
		// SearchUtils.filterEntryAttachmentResults(results);
		prepareRatingsAndFolders(bs, model, (List) results.get(ObjectKeys.SEARCH_ENTRIES));
		model.putAll(prepareSavedQueries(bs));

		// this function puts also proper part of entries list into a model
		preparePagination(bs, model, results, options, tab);
		
		model.put(WebKeys.SEARCH_FORM_CASE_SENSITIVE, options.get(ObjectKeys.SEARCH_CASE_SENSITIVE));
		model.put(WebKeys.SEARCH_FORM_PREDELETED_ONLY, options.get(ObjectKeys.SEARCH_PRE_DELETED));
		model.put("resultsCount", options.get(ObjectKeys.SEARCH_USER_MAX_HITS));
		model.put("summaryWordCount", (Integer)options.get(WebKeys.SEARCH_FORM_SUMMARY_WORDS));
		model.put(WebKeys.SEARCH_SEARCH_SORT_BY, options.get( ObjectKeys.SEARCH_SORT_BY ) );
		model.put(WebKeys.SEARCH_SEARCH_SORT_BY_SECONDARY, options.get( ObjectKeys.SEARCH_SORT_BY_SECONDARY ) );
		model.put(WebKeys.SEARCH_SCOPE, options.get(ObjectKeys.SEARCH_SCOPE));
		model.put(WebKeys.SEARCH_INCLUDE_NESTED_BINDERS, options.get(ObjectKeys.SEARCH_INCLUDE_NESTED_BINDERS));

		model.put("quickSearch", options.get(WebKeys.SEARCH_FORM_QUICKSEARCH));
		
	}
	public static Map prepareSearchFormData(AllModulesInjected bs, RenderRequest request) throws PortletRequestBindingException {
		Map options = prepareSearchOptions(bs, request);
		Map model = new HashMap();
		model.put("resultsCount", options.get(ObjectKeys.SEARCH_USER_MAX_HITS));
		model.put(WebKeys.SEARCH_SEARCH_SORT_BY, options.get( ObjectKeys.SEARCH_SORT_BY ) );
		model.put(WebKeys.SEARCH_SEARCH_SORT_BY_SECONDARY, options.get( ObjectKeys.SEARCH_SORT_BY_SECONDARY ) );
		model.put("quickSearch", false);
		
		model.putAll(prepareSavedQueries(bs));
		
		try {
			Workspace ws = bs.getWorkspaceModule().getTopWorkspace();
			Document tree = bs.getBinderModule().getDomBinderTree(ws.getId(), new WsDomTreeBuilder(ws, true, bs),1);
			model.put(WebKeys.DOM_TREE, tree);
		} catch(AccessControlException e) {}
		
		return model;
	}
	
	public static Map prepareSearchOptions(AllModulesInjected bs, RenderRequest request) {
		
		Map options;
		
		options = getDefaultSortOrderForSearch( request );
		
		Boolean searchCaseSensitive = false;
		Boolean searchPreDeletedOnly = false;
		try {
			searchCaseSensitive = PortletRequestUtils.getBooleanParameter(request, WebKeys.SEARCH_FORM_CASE_SENSITIVE);
			searchPreDeletedOnly = PortletRequestUtils.getBooleanParameter(request, WebKeys.SEARCH_FORM_PREDELETED_ONLY);
		} catch(Exception e) {
			logger.debug("BinderHelper.prepareSearchOptions(Exception:  '" + MiscUtil.exToString(e) + "'):  Ignored");
		}
		if (searchCaseSensitive == null) searchCaseSensitive = false;
		options.put(ObjectKeys.SEARCH_CASE_SENSITIVE, searchCaseSensitive);
		if ((searchPreDeletedOnly != null) && searchPreDeletedOnly) { 
			options.put(ObjectKeys.SEARCH_PRE_DELETED, searchPreDeletedOnly);
		}
		//Get the scope
		String searchScope = PortletRequestUtils.getStringParameter(request, ObjectKeys.SEARCH_SCOPE, ObjectKeys.SEARCH_SCOPE_ALL);
		options.put(ObjectKeys.SEARCH_SCOPE, searchScope);
		Boolean includeSubfolders = PortletRequestUtils.getBooleanParameter(request, ObjectKeys.SEARCH_INCLUDE_NESTED_BINDERS, Boolean.FALSE);
		options.put(ObjectKeys.SEARCH_INCLUDE_NESTED_BINDERS, includeSubfolders);
		
		//If the entries per page is not present in the user properties, then it means the
		//number of records per page is obtained from the ssf properties file, so we do not have 
		//to worry about checking the old and new number or records per page.
		
		//Getting the entries per page from the user properties
		User user = RequestContextHolder.getRequestContext().getUser();
		UserProperties userProp = bs.getProfileModule().getUserProperties(user.getId());
		String entriesPerPage = (String) userProp.getProperty(ObjectKeys.PAGE_ENTRIES_PER_PAGE);
		if (entriesPerPage == null || "".equals(entriesPerPage)) {
			entriesPerPage = SPropsUtil.getString("search.records.listed");
		}
		options.put(ObjectKeys.SEARCH_PAGE_ENTRIES_PER_PAGE, new Integer(entriesPerPage));
		
		
		// it should be always 0, this method is(should be) used to only on first result page
		Integer searchUserOffset = PortletRequestUtils.getIntParameter(request, ObjectKeys.SEARCH_USER_OFFSET, 0);
			
		Integer searchLuceneOffset = 0;
		options.put(ObjectKeys.SEARCH_OFFSET, searchLuceneOffset);
		options.put(ObjectKeys.SEARCH_USER_OFFSET, searchUserOffset);
		
		Integer maxHits = PortletRequestUtils.getIntParameter(request, WebKeys.SEARCH_FORM_MAX_HITS, new Integer(entriesPerPage));
		if (maxHits > Utils.getSearchDefaultMaxHits()) maxHits = Utils.getSearchDefaultMaxHits();
		options.put(ObjectKeys.SEARCH_USER_MAX_HITS, maxHits);
		
		Integer summaryWords = PortletRequestUtils.getIntParameter(request, WebKeys.SEARCH_FORM_SUMMARY_WORDS, new Integer(20));
		options.put(WebKeys.SEARCH_FORM_SUMMARY_WORDS, summaryWords);
		
		Integer intInternalNumberOfRecordsToBeFetched = searchLuceneOffset + maxHits + ObjectKeys.SEARCH_RESULTS_TO_CREATE_STATISTICS;
		if (searchUserOffset > ObjectKeys.SEARCH_RESULTS_TO_CREATE_STATISTICS) {
			intInternalNumberOfRecordsToBeFetched+=searchUserOffset;
		}
		options.put(ObjectKeys.SEARCH_MAX_HITS, intInternalNumberOfRecordsToBeFetched);

		Integer pageNo = PortletRequestUtils.getIntParameter(request, WebKeys.URL_PAGE_NUMBER, 1);
		options.put(Tabs.PAGE, pageNo);				
		
		Boolean quickSearch = PortletRequestUtils.getBooleanParameter(request, WebKeys.SEARCH_FORM_QUICKSEARCH, Boolean.FALSE);
		options.put(WebKeys.SEARCH_FORM_QUICKSEARCH, quickSearch);
		DateFormat fmt = DateFormat.getTimeInstance(DateFormat.SHORT, user.getLocale());
		fmt.setTimeZone(user.getTimeZone());
		if (quickSearch) {
			options.put(Tabs.TITLE, NLT.get("searchForm.quicksearch.Title") + " " + fmt.format(new Date()));
		} else {
			options.put(Tabs.TITLE, NLT.get("searchForm.advanced.Title") + " " + fmt.format(new Date()));
		} 
	
		return options;
	}

	private static void actualizeOptions(Map options, RenderRequest request) {
		Integer pageNo = (Integer)options.get(Tabs.PAGE);
		if ((pageNo == null) || pageNo < 1) {
			pageNo = 1;
			
		}
		int defaultMaxOnPage = ObjectKeys.SEARCH_MAX_HITS_DEFAULT;
		if (options.get(ObjectKeys.SEARCH_USER_MAX_HITS) != null) defaultMaxOnPage = (Integer) options.get(ObjectKeys.SEARCH_USER_MAX_HITS);
		int[] maxOnPageArr = PortletRequestUtils.getIntParameters(request, WebKeys.SEARCH_FORM_MAX_HITS);
		int maxOnPage = defaultMaxOnPage;
		if (maxOnPageArr.length >0) maxOnPage = maxOnPageArr[0];
		if (maxOnPage > Utils.getSearchDefaultMaxHits()) maxOnPage = Utils.getSearchDefaultMaxHits();
		int userOffset = (pageNo - 1) * maxOnPage;
		int[] summaryWords = PortletRequestUtils.getIntParameters(request, WebKeys.SEARCH_FORM_SUMMARY_WORDS);
		int summaryWordsCount = 20;
		if (options.containsKey(WebKeys.SEARCH_FORM_SUMMARY_WORDS)) { summaryWordsCount = (Integer)options.get(WebKeys.SEARCH_FORM_SUMMARY_WORDS);}
		if (summaryWords.length > 0) {summaryWordsCount = summaryWords[0];}
		
		Integer searchLuceneOffset = 0;
		int maxPageToSee = (ObjectKeys.SEARCH_RESULTS_TO_CREATE_STATISTICS + maxOnPage) / maxOnPage; 
		if (pageNo > maxPageToSee) { // pageNo <= 21
			searchLuceneOffset += (pageNo - maxPageToSee) * maxOnPage;
			userOffset = ObjectKeys.SEARCH_RESULTS_TO_CREATE_STATISTICS;
		}
		options.put(ObjectKeys.SEARCH_OFFSET, searchLuceneOffset);
		options.put(ObjectKeys.SEARCH_USER_OFFSET, userOffset);
		
		
		options.put(ObjectKeys.SEARCH_USER_MAX_HITS, maxOnPage);
		options.put(WebKeys.URL_PAGE_NUMBER, pageNo);
		options.put(WebKeys.SEARCH_FORM_SUMMARY_WORDS, summaryWordsCount);
		
	}
	private static Map getOptionsFromTab(Tabs.TabEntry tab) {
		Map options = new HashMap();
		Map tabData = tab.getData();
		if (tabData.containsKey(ObjectKeys.SEARCH_PAGE_ENTRIES_PER_PAGE)) options.put(ObjectKeys.SEARCH_PAGE_ENTRIES_PER_PAGE, tabData.get(ObjectKeys.SEARCH_PAGE_ENTRIES_PER_PAGE));
		if (tabData.containsKey(ObjectKeys.SEARCH_OFFSET)) options.put(ObjectKeys.SEARCH_OFFSET, tabData.get(ObjectKeys.SEARCH_OFFSET));
		if (tabData.containsKey(ObjectKeys.SEARCH_USER_OFFSET)) options.put(ObjectKeys.SEARCH_USER_OFFSET, tabData.get(ObjectKeys.SEARCH_USER_OFFSET));
		if (tabData.containsKey(ObjectKeys.SEARCH_MAX_HITS)) options.put(ObjectKeys.SEARCH_MAX_HITS, tabData.get(ObjectKeys.SEARCH_MAX_HITS));
		if (tabData.containsKey(ObjectKeys.SEARCH_USER_MAX_HITS)) options.put(ObjectKeys.SEARCH_USER_MAX_HITS, tabData.get(ObjectKeys.SEARCH_USER_MAX_HITS));
		if (tabData.containsKey(Tabs.TITLE)) options.put(Tabs.TITLE, tabData.get(Tabs.TITLE));
		if (tabData.containsKey(WebKeys.SEARCH_FORM_SUMMARY_WORDS)) options.put(WebKeys.SEARCH_FORM_SUMMARY_WORDS, tabData.get(WebKeys.SEARCH_FORM_SUMMARY_WORDS));
		if (tabData.containsKey(WebKeys.SEARCH_FORM_QUICKSEARCH)) options.put(WebKeys.SEARCH_FORM_QUICKSEARCH, tabData.get(WebKeys.SEARCH_FORM_QUICKSEARCH));
		if (tabData.containsKey(ObjectKeys.SEARCH_CASE_SENSITIVE)) options.put(ObjectKeys.SEARCH_CASE_SENSITIVE, tabData.get(ObjectKeys.SEARCH_CASE_SENSITIVE));
		if (tabData.containsKey(ObjectKeys.SEARCH_PRE_DELETED)) options.put(ObjectKeys.SEARCH_PRE_DELETED, tabData.get(ObjectKeys.SEARCH_PRE_DELETED));
		if (tabData.containsKey(ObjectKeys.SEARCH_HIDDEN)) options.put(ObjectKeys.SEARCH_HIDDEN, tabData.get(ObjectKeys.SEARCH_HIDDEN));
		if (tabData.containsKey(ObjectKeys.SEARCH_FIND_USER_HIDDEN)) options.put(ObjectKeys.SEARCH_FIND_USER_HIDDEN, tabData.get(ObjectKeys.SEARCH_FIND_USER_HIDDEN));
		if (tabData.containsKey(Tabs.PAGE)) options.put(Tabs.PAGE, tabData.get(Tabs.PAGE));
		if (tabData.containsKey(ObjectKeys.SEARCH_SORT_BY)) options.put(ObjectKeys.SEARCH_SORT_BY, tabData.get(ObjectKeys.SEARCH_SORT_BY));
		if (tabData.containsKey(ObjectKeys.SEARCH_SORT_DESCEND)) options.put(ObjectKeys.SEARCH_SORT_DESCEND, tabData.get(ObjectKeys.SEARCH_SORT_DESCEND));
		if (tabData.containsKey(ObjectKeys.SEARCH_SORT_BY_SECONDARY)) options.put(ObjectKeys.SEARCH_SORT_BY_SECONDARY, tabData.get(ObjectKeys.SEARCH_SORT_BY_SECONDARY));
		if (tabData.containsKey(ObjectKeys.SEARCH_SORT_DESCEND_SECONDARY)) options.put(ObjectKeys.SEARCH_SORT_DESCEND_SECONDARY, tabData.get(ObjectKeys.SEARCH_SORT_DESCEND_SECONDARY));
		if (tabData.containsKey(ObjectKeys.SEARCH_SCOPE)) options.put(ObjectKeys.SEARCH_SCOPE, tabData.get(ObjectKeys.SEARCH_SCOPE));
		if (tabData.containsKey(ObjectKeys.SEARCH_INCLUDE_NESTED_BINDERS)) options.put(ObjectKeys.SEARCH_INCLUDE_NESTED_BINDERS, tabData.get(ObjectKeys.SEARCH_INCLUDE_NESTED_BINDERS));
		if (tabData.containsKey(ObjectKeys.SEARCH_CRITERIA_AND)) options.put(ObjectKeys.SEARCH_CRITERIA_AND, tabData.get(ObjectKeys.SEARCH_CRITERIA_AND));
		if (tabData.containsKey(ObjectKeys.SEARCH_CRITERIA_OR)) options.put(ObjectKeys.SEARCH_CRITERIA_OR, tabData.get(ObjectKeys.SEARCH_CRITERIA_OR));
		return options;
	}
	
	public static Document getSavedQuery(AllModulesInjected bs, String queryName, UserProperties userProperties) {
		
		Map properties = userProperties.getProperties();
		if (properties.containsKey(ObjectKeys.USER_PROPERTY_SAVED_SEARCH_QUERIES)) {
			Map queries = (Map)properties.get(ObjectKeys.USER_PROPERTY_SAVED_SEARCH_QUERIES);
			Object q = queries.get(queryName);
			if (q == null) return null;
			if (q instanceof String) {
				try {
					return XmlUtil.parseText((String)q);
				} catch (Exception ex) {
					logger.debug("BinderHelper.getSearchFilter(Exception:  '" + MiscUtil.exToString(ex) + "'):  Removed query");
					queries.remove(queryName);
					bs.getProfileModule().setUserProperty(null, ObjectKeys.USER_PROPERTY_SAVED_SEARCH_QUERIES, queries);
				};
			}
			//In v1 these are stored as documents; shouldn't be because the hibernate dirty check always fails causing updates
			if (q instanceof Document) {
				queries.put(queryName, ((Document)q).asXML());
				bs.getProfileModule().setUserProperty(null, ObjectKeys.USER_PROPERTY_SAVED_SEARCH_QUERIES, queries);
				return (Document)q;
			}
		
		}
		return null;
	}


	
	private static Map prepareSavedQueries(AllModulesInjected bs) {
		Map result = new HashMap();
		
		User currentUser = RequestContextHolder.getRequestContext().getUser();
		
		UserProperties userProperties = bs.getProfileModule().getUserProperties(currentUser.getId());
		if (userProperties != null) {
			Map properties = userProperties.getProperties();
			if (properties != null && properties.containsKey(ObjectKeys.USER_PROPERTY_SAVED_SEARCH_QUERIES)) {
				Map queries = (Map)properties.get(ObjectKeys.USER_PROPERTY_SAVED_SEARCH_QUERIES);
				result.put(WebKeys.SEARCH_SAVED_QUERIES, queries.keySet());
			}
		}
		return result;
	}
	//This method rates the people
	public static List ratePeople(List entries) {
		//The same logic and naming has been followed for both people and placess
		return ratePlaces(entries);
	}

	private static void prepareRatingsAndFolders(AllModulesInjected bs, Map model, List entries) {
		List peoplesWithCounters = sortPeopleInEntriesSearchResults(entries);
		List placesWithCounters = sortPlacesInEntriesSearchResults(bs.getBinderModule(), entries);
		
		List peoplesRating = ratePeople(peoplesWithCounters);
		model.put(WebKeys.FOLDER_ENTRYPEOPLE + "_all", peoplesRating);
		
		List peoplesRatingToShow = new ArrayList();
		if (peoplesRating.size() > 20) {
			peoplesRatingToShow.addAll(peoplesRating.subList(0,20));
		} else {
			peoplesRatingToShow.addAll(peoplesRating);
		}
		List placesRating = ratePlaces(placesWithCounters);
		if (placesRating.size() > 20) {
			placesRating = placesRating.subList(0,20);
		}
		model.put(WebKeys.FOLDER_ENTRYPEOPLE, peoplesRatingToShow);
		model.put(WebKeys.FOLDER_ENTRYPLACES, placesRating);

		Map folders = prepareFolderList(placesWithCounters, true);
		extendEntriesInfo(entries, folders);

		// TODO check and make it better, copied from SearchController
		List entryCommunityTags = sortCommunityTags(entries);
		List entryPersonalTags = sortPersonalTags(entries);
		int intMaxHitsForCommunityTags = getMaxHitsPerTag(entryCommunityTags);
		int intMaxHitsForPersonalTags = getMaxHitsPerTag(entryPersonalTags);
		int intMaxHits = intMaxHitsForCommunityTags;
		if (intMaxHitsForPersonalTags > intMaxHitsForCommunityTags) intMaxHits = intMaxHitsForPersonalTags;
		entryCommunityTags = rateCommunityTags(entryCommunityTags, intMaxHits);
		entryPersonalTags = ratePersonalTags(entryPersonalTags, intMaxHits);

		model.put(WebKeys.FOLDER_ENTRYTAGS, entryCommunityTags);
		model.put(WebKeys.FOLDER_ENTRYPERSONALTAGS, entryPersonalTags);
	}

	public static List sortPlacesInEntriesSearchResults(BinderModule binderModule, List entries) {
		HashMap placeMap = new HashMap();
		ArrayList placeList = new ArrayList();
		// first go thru the original search results and 
		// find all the unique places.  Keep a count to see
		// if any are more active than others.
		for (int i = 0; i < entries.size(); i++) {
			Map entry = (Map)entries.get(i);
			String id = (String)entry.get("_binderId");
			if (id == null) continue;
			Long bId = new Long(id);
			if (placeMap.get(bId) == null) {
				placeMap.put(bId, new Place(bId,1));
			} else {
				Place p = (Place)placeMap.remove(bId);
				p = new Place(p.getId(),p.getCount()+1);
				placeMap.put(bId,p);
			}
		}
		//sort the hits
		Collection collection = placeMap.values();
		Object[] array = collection.toArray();
		Arrays.sort(array);
		
		for (int j = 0; j < array.length; j++) {
			Binder binder=null;
			try {
				binder = binderModule.getBinder(((Place)array[j]).getId());
			} catch (Exception ex) {
				logger.debug("BinderHelper.sortPlacesInEntriesSearchResults(Exception:  '" + MiscUtil.exToString(ex) + "'):  No access or doesn't exist");
			}
			int count = ((Place)array[j]).getCount();
			Map place = new HashMap();
			place.put(WebKeys.BINDER, binder);
			place.put(WebKeys.SEARCH_RESULTS_COUNT, new Integer(count));
			placeList.add(place);
		}
		return placeList;

	}

	//This method rates the places
	public static List ratePlaces(List entries) {
		List ratedList = new ArrayList();
		int intMaxHitsPerFolder = 0;
		for (int i = 0; i < entries.size(); i++) {
			Map place = (Map) entries.get(i);
			Integer resultCount = (Integer) place.get(WebKeys.SEARCH_RESULTS_COUNT);
			if (i == 0) {
				place.put(WebKeys.SEARCH_RESULTS_RATING, new Integer(100));
				place.put(WebKeys.SEARCH_RESULTS_RATING_CSS, "ss_brightest");
				intMaxHitsPerFolder = resultCount;
			}
			else {
				int intResultCount = resultCount.intValue();
				Double DblRatingForFolder = ((double)intResultCount/intMaxHitsPerFolder) * 100;
				int intRatingForFolder = DblRatingForFolder.intValue();
				place.put(WebKeys.SEARCH_RESULTS_RATING, new Integer(DblRatingForFolder.intValue()));
				if (intRatingForFolder > 80 && intRatingForFolder <= 100) {
					place.put(WebKeys.SEARCH_RESULTS_RATING_CSS, "ss_brightest");
				}
				else if (intRatingForFolder > 50 && intRatingForFolder <= 80) {
					place.put(WebKeys.SEARCH_RESULTS_RATING_CSS, "ss_brighter");
				}
				else if (intRatingForFolder > 20 && intRatingForFolder <= 50) {
					place.put(WebKeys.SEARCH_RESULTS_RATING_CSS, "ss_bright");
				}
				else if (intRatingForFolder > 10 && intRatingForFolder <= 20) {
					place.put(WebKeys.SEARCH_RESULTS_RATING_CSS, "ss_dim");
				}
				else if (intRatingForFolder >= 0 && intRatingForFolder <= 10) {
					place.put(WebKeys.SEARCH_RESULTS_RATING_CSS, "ss_very_dim");
				}
			}
			ratedList.add(place);
		}
		return ratedList;
	}
	
	public static Map prepareFolderList(List placesWithCounters, Boolean includeParentBinderTitle) {
		Map folderMap = new HashMap();
		Iterator it = placesWithCounters.iterator();
		while (it.hasNext()) {
			Map place = (Map) it.next();
			Binder binder = (Binder)place.get(WebKeys.BINDER);
			if (binder == null) continue;
			Binder parentBinder = binder.getParentBinder();
			String parentBinderTitle = "";
			if (includeParentBinderTitle && parentBinder != null) 
				parentBinderTitle = parentBinder.getTitle() + " // ";
			folderMap.put(binder.getId(), parentBinderTitle + binder.getTitle());
			folderMap.put("pathname" + binder.getId().toString(), binder.getPathName());
		}
		return folderMap;
	}
	
	public static void extendEntriesInfo(List entries, Map folders) {
		Iterator it = entries.iterator();
		while (it.hasNext()) {
			Map entry = (Map) it.next();
			if (entry.get(WebKeys.SEARCH_BINDER_ID) != null) {
				String s_binderId = (String)entry.get(WebKeys.SEARCH_BINDER_ID);
				entry.put(WebKeys.BINDER_TITLE, folders.get(Long.parseLong(s_binderId)));
				entry.put(WebKeys.BINDER_PATH_NAME, folders.get("pathname"+s_binderId));
			}
		}
	}
	
	private static void preparePagination(AllModulesInjected bs, Map model, Map results, Map options, Tabs.TabEntry tab) {
		Map<Long,FolderEntry> topEntries = new HashMap<Long,FolderEntry>();
		int totalRecordsFound = (Integer) results.get(ObjectKeys.SEARCH_COUNT_TOTAL);
		boolean totalIsApproximate = (Boolean)results.get(ObjectKeys.SEARCH_COUNT_TOTAL_APPROXIMATE);
		boolean isMoreHits = (Boolean)results.get(ObjectKeys.SEARCH_THERE_IS_MORE);
		int pageInterval = ObjectKeys.SEARCH_MAX_HITS_DEFAULT;
		if (options != null && options.get(ObjectKeys.SEARCH_USER_MAX_HITS) != null) {
			pageInterval = (Integer) options.get(ObjectKeys.SEARCH_USER_MAX_HITS);
		}
		
		int pagesCount = (int)Math.ceil((double)totalRecordsFound / pageInterval);
		
		
		List allResultsList = (List) results.get(ObjectKeys.SEARCH_ENTRIES);  
		
		int userOffsetStart = 0;
		if (options != null && options.containsKey(ObjectKeys.SEARCH_USER_OFFSET)) {
			userOffsetStart = (Integer) options.get(ObjectKeys.SEARCH_USER_OFFSET);
		}
		if (userOffsetStart > allResultsList.size() || userOffsetStart < 0) {
			userOffsetStart = 0;
		}
		int userOffsetEnd = userOffsetStart + pageInterval;
		if ((allResultsList.size() - userOffsetStart) < pageInterval) {
			userOffsetEnd = userOffsetStart + (allResultsList.size() - userOffsetStart);
		}		
		
		List<Map> shownOnPage = allResultsList.subList(userOffsetStart, userOffsetEnd);
		
		int pageNo = 1;
		if (options != null && options.get(WebKeys.URL_PAGE_NUMBER) != null) {
			pageNo = (Integer) options.get(WebKeys.URL_PAGE_NUMBER);
		}
		int firstOnCurrentPage = (pageNo - 1) * pageInterval;;
		
		if (firstOnCurrentPage > totalRecordsFound || firstOnCurrentPage < 0) {
			firstOnCurrentPage = 0;
		}

		int currentPageNo = firstOnCurrentPage / pageInterval + 1;
		int lastOnCurrentPage = firstOnCurrentPage + pageInterval;
		if ((totalRecordsFound - firstOnCurrentPage) < pageInterval) {
			lastOnCurrentPage = firstOnCurrentPage + (totalRecordsFound - firstOnCurrentPage);
			if (firstOnCurrentPage < 0) firstOnCurrentPage = 0;
		}
		
		model.put(WebKeys.FOLDER_ENTRIES, shownOnPage);
		model.put(WebKeys.PAGE_NUMBER, currentPageNo);
		
		//Get the top entries of any item that is either an attachment of a reply
		for (Map item : shownOnPage) {
			String entryType = (String)item.get(Constants.ENTRY_TYPE_FIELD);
			String entityType = (String)item.get(Constants.ENTITY_FIELD);
			String docType = (String)item.get(Constants.DOC_TYPE_FIELD);
			String isLibrary = (String)item.get(Constants.IS_LIBRARY_FIELD);
			String entryId = (String)item.get(Constants.DOCID_FIELD);
			if (Constants.ENTRY_TYPE_REPLY.equals(entryType)) {
				//This is a reply. We need to get its top entry
				String topEntryId = (String)item.get(Constants.ENTRY_TOP_ENTRY_ID_FIELD);
				if (topEntryId != null) {
					try {
						FolderEntry topEntry = bs.getFolderModule().getEntry(null, Long.valueOf(topEntryId));
						topEntries.put(topEntry.getId(), topEntry);
					} catch(Exception e) {}
				}
				
			} else if (Constants.DOC_TYPE_ATTACHMENT.equals(docType)) {
				//This is an attachment. We need to get its owning entry
				if (entryId != null) {
					try {
						FolderEntry entry = bs.getFolderModule().getEntry(null, Long.valueOf(entryId));
						topEntries.put(entry.getId(), entry);
					} catch(Exception e) {}
				}
			}
		}
		model.put(WebKeys.FOLDER_ENTRIES_TOP_ENTRIES, topEntries);
		
		List pageNos = new ArrayList();
		int startFrom = 1;
		if (currentPageNo >= 7) {
			startFrom = currentPageNo - 3;
		}
		for (int i = startFrom; i <= currentPageNo; i++) {
			if (i > 0) {
				pageNos.add(i);
			}
		}
		
		//See if this is an approximate number of hits
		if (totalIsApproximate) {
			pagesCount = currentPageNo;
			if (isMoreHits) {
				pagesCount++;
			}
		}
		
		for (int i = currentPageNo+1; i <= currentPageNo+3; i++) {
			if (i <= pagesCount) {
				pageNos.add(i);
			}
		}
		
		model.put(WebKeys.PAGE_COUNT, pagesCount);
		model.put(WebKeys.PAGE_NUMBERS, pageNos);
		model.put(WebKeys.PAGE_TOTAL_RECORDS, totalRecordsFound);
		model.put(WebKeys.PAGE_START_INDEX, firstOnCurrentPage+1);
		model.put(WebKeys.PAGE_END_INDEX, lastOnCurrentPage);
		model.put(ObjectKeys.SEARCH_COUNT_TOTAL_APPROXIMATE, results.get(ObjectKeys.SEARCH_COUNT_TOTAL_APPROXIMATE));
		model.put(ObjectKeys.SEARCH_THERE_IS_MORE, results.get(ObjectKeys.SEARCH_THERE_IS_MORE));
	}
	
	
	// This method reads thru the results from a search, finds the principals, 
	// and places them into an array that is ordered by the number of times
	// they show up in the results list.
	public static List sortPeopleInEntriesSearchResults(List entries) {
		HashMap userMap = new HashMap();
		ArrayList userList = new ArrayList();
		// first go thru the original search results and 
		// find all the unique principals.  Keep a count to see
		// if any are more active than others.
		for (int i = 0; i < entries.size(); i++) {
			Map entry = (Map)entries.get(i);
			Principal user = (Principal)entry.get(WebKeys.PRINCIPAL);
			if (user == null) {
				continue;
			}
			if (userMap.get(user.getId()) == null) {
				userMap.put(user.getId(), new Person(user.getId(),user,1));
			} else {
				Person p = (Person)userMap.remove(user.getId());
				p.incrCount();
				userMap.put(user.getId(),p);
			}
		}
		//sort the hits
		Collection collection = userMap.values();
		Object[] array = collection.toArray();
		Arrays.sort(array);
		
		for (int j = 0; j < array.length; j++) {
			HashMap person = new HashMap();
			Principal user = (Principal) ((Person)array[j]).getUser();
			int intUserCount = ((Person)array[j]).getCount();
			person.put(WebKeys.USER_PRINCIPAL, user);
			person.put(WebKeys.SEARCH_RESULTS_COUNT, new Integer(intUserCount));
			userList.add(person);
		}
		return userList;
	}
	
	// This class is used by the following method as a way to sort
	// the values in a hashmap
	public static class Person implements Comparable {
		long id;
		int count;
		Principal user;

		public Person (long id, Principal p, int count) {
			this.id = id;
			this.user = p;
			this.count = count;
		}
		
		public int getCount() {
			return this.count;
		}

		public void incrCount() {
			this.count += 1;
		}
		
		public Principal getUser() {
			return this.user;
		}
		
		@Override
		public int compareTo(Object o) {
			Person p = (Person) o;
			int result = this.getCount() < p.getCount() ? 1 : 0;
			return result;
			}
	}
	// This class is used by the following method as a way to sort
	// the values in a hashmap
	public static class Place implements Comparable {
		long id;
		int count;

		public Place (long id, int count) {
			this.id = id;
			this.count = count;
		}
		
		public int getCount() {
			return this.count;
		}

		public void incrCount() {
			this.count += 1;
		}
		
		public long getId() {
			return this.id;
		}
		
		@Override
		public int compareTo(Object o) {
			Place p = (Place) o;
			int result = this.getCount() < p.getCount() ? 1 : 0;
			return result;
			}
	}
	
	public static void buildFolderActionsToolbar(AllModulesInjected bs, RenderRequest request, 
			RenderResponse response, Toolbar folderActionsToolbar, String forumId) {
        User user = RequestContextHolder.getRequestContext().getUser();
        String userDisplayStyle = user.getDisplayStyle();
        if (userDisplayStyle == null) userDisplayStyle = getDefaultViewDisplayStyle();
        
		Map qualifiers;
		PortletURL url;

        //Folder action menu
		boolean accessible_simple_ui = SPropsUtil.getBoolean("accessibility.simple_ui", false);
		if (!userDisplayStyle.equals(ObjectKeys.USER_DISPLAY_STYLE_ACCESSIBLE) || !accessible_simple_ui) {
			//Only show these options if not in accessible mode
			folderActionsToolbar.addToolbarMenu("4_display_styles", NLT.get("toolbar.folder_actions"));
			
			//iframe
			qualifiers = new HashMap();
			if (userDisplayStyle.equals(ObjectKeys.USER_DISPLAY_STYLE_IFRAME) || 
					userDisplayStyle.equals(ObjectKeys.USER_DISPLAY_STYLE_POPUP)) 
				qualifiers.put(WebKeys.TOOLBAR_MENU_SELECTED, true);
			url = response.createActionURL();
			url.setParameter(WebKeys.ACTION, WebKeys.ACTION_VIEW_FOLDER_LISTING);
			url.setParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_SET_DISPLAY_STYLE);
			url.setParameter(WebKeys.URL_BINDER_ID, forumId);
			url.setParameter(WebKeys.URL_VALUE, ObjectKeys.USER_DISPLAY_STYLE_IFRAME);
			folderActionsToolbar.addToolbarMenuItem("4_display_styles", "styles", 
					NLT.get("toolbar.menu.display_style_iframe"), url, qualifiers);
			//newpage
			qualifiers = new HashMap();
			if (userDisplayStyle.equals(ObjectKeys.USER_DISPLAY_STYLE_NEWPAGE)) 
				qualifiers.put(WebKeys.TOOLBAR_MENU_SELECTED, true);
			url = response.createActionURL();
			url.setParameter(WebKeys.ACTION, WebKeys.ACTION_VIEW_FOLDER_LISTING);
			url.setParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_SET_DISPLAY_STYLE);
			url.setParameter(WebKeys.URL_BINDER_ID, forumId);
			url.setParameter(WebKeys.URL_VALUE, ObjectKeys.USER_DISPLAY_STYLE_NEWPAGE);
			folderActionsToolbar.addToolbarMenuItem("4_display_styles", "styles", 
					NLT.get("toolbar.menu.display_style_newpage"), url, qualifiers);
		}
	}

	// type = "add", "deletePerson", or "delete"
	public static void trackThisBinder(AllModulesInjected bs, Long binderId, String type) {
		//The list of tracked binders and shared binders are kept in the user' user workspace user folder properties
		User user = RequestContextHolder.getRequestContext().getUser();
		boolean deletePerson = type.equals("deletePerson");
		Binder binder = (deletePerson ? null : bs.getBinderModule().getBinder(binderId));
		Long userWorkspaceId = user.getWorkspaceId();
		if ((deletePerson || (binder != null)) && userWorkspaceId != null) {
			UserProperties userForumProperties = bs.getProfileModule().getUserProperties(user.getId(), userWorkspaceId);
			Map relevanceMap = (Map)userForumProperties.getProperty(ObjectKeys.USER_PROPERTY_RELEVANCE_MAP);
			if (relevanceMap == null) relevanceMap = new HashMap();
			List trackedBinders = (List) relevanceMap.get(ObjectKeys.RELEVANCE_TRACKED_BINDERS);
			if (trackedBinders == null) {
				trackedBinders = new ArrayList();
				relevanceMap.put(ObjectKeys.RELEVANCE_TRACKED_BINDERS, trackedBinders);
			}
			List trackedPeople = (List) relevanceMap.get(ObjectKeys.RELEVANCE_TRACKED_PEOPLE);
			if (trackedPeople == null) {
				trackedPeople = new ArrayList();
				relevanceMap.put(ObjectKeys.RELEVANCE_TRACKED_PEOPLE, trackedPeople);
			}
			List trackedCalendars = (List) relevanceMap.get(ObjectKeys.RELEVANCE_TRACKED_CALENDARS);
			if (trackedCalendars == null) {
				trackedCalendars = new ArrayList();
				relevanceMap.put(ObjectKeys.RELEVANCE_TRACKED_CALENDARS, trackedCalendars);
			}
			if (type.equals("add")) {
				if (!trackedBinders.contains(binderId)) trackedBinders.add(binderId);
				if (binder.getEntityType().equals(EntityType.workspace) && 
						binder.getDefinitionType() != null &&
						(binder.getDefinitionType() == Definition.USER_WORKSPACE_VIEW ||
						 binder.getDefinitionType() == Definition.EXTERNAL_USER_WORKSPACE_VIEW)) {
					//This is a user workspace, so also track this user
					if (!trackedPeople.contains(binder.getOwnerId())) trackedPeople.add(binder.getOwnerId());
				}
				Definition binderDef = binder.getDefaultViewDef();
				Element familyProperty = (Element) binderDef.getDefinition().getRootElement()
						.selectSingleNode("//properties/property[@name='family']");
				if (familyProperty != null && familyProperty.attributeValue("value", "").equals("calendar")) {
					if (!trackedCalendars.contains(binderId)) trackedCalendars.add(binderId);
				}
			} else if (type.equals("delete")) {
				if (trackedBinders.contains(binderId)) trackedBinders.remove(binderId);
				if (trackedCalendars.contains(binderId)) trackedCalendars.remove(binderId);
			} else if (type.equals("deletePerson")) {
				//This is a user workspace, so also untrack this user
				if (trackedPeople.contains(binderId)) trackedPeople.remove(binderId);
			}
			//Save the updated list
			bs.getProfileModule().setUserProperty(user.getId(), userWorkspaceId, 
					ObjectKeys.USER_PROPERTY_RELEVANCE_MAP, relevanceMap);
		}
	}
	
	/**
	 * Returns true if a binder is the guest user workspace and false
	 * otherwise.
	 * 
	 * @param binderId
	 * 
	 * @return
	 */
	public static boolean isBinderGuestWorkspaceId(Long binderId) {
		boolean reply = false;
		if (null != binderId) {
			User guest = getProfileModule().getGuestUser();
			if (null != guest) {
				Long guestWSId = guest.getWorkspaceId();
				if (null != guestWSId) {
					reply = guestWSId.equals(binderId);
				}
			}
		}
		return reply;
	}
	
	public static boolean isBinderTracked(AllModulesInjected bs, Long binderId) {
		boolean reply = false;
		User user = RequestContextHolder.getRequestContext().getUser();
		Long userWorkspaceId = user.getWorkspaceId();
		UserProperties userForumProperties = bs.getProfileModule().getUserProperties(user.getId(), userWorkspaceId);
		if (null != userForumProperties) {
			Map relevanceMap = (Map)userForumProperties.getProperty(ObjectKeys.USER_PROPERTY_RELEVANCE_MAP);
			if (relevanceMap != null) {
				List trackedBinders = (List) relevanceMap.get(ObjectKeys.RELEVANCE_TRACKED_BINDERS);
				if (trackedBinders != null) {
					reply = trackedBinders.contains(binderId);
				}
			}
		}
		return reply;
	}

	/**
	 * Returns true if the current user is tracking the binder as a
	 * person and false otherwise.
	 * 
	 * @param bs
	 * @param binderId
	 * 
	 * @return
	 */
	public static boolean isPersonTracked(AllModulesInjected bs, Long binderId) {

		boolean reply = false;
		// Is the binder a user workspace?
		if (isBinderUserWorkspace(bs, binderId)) {
			// Yes!  Are we tracking it as a person?
			User user = RequestContextHolder.getRequestContext().getUser();
			Long userWorkspaceId = user.getWorkspaceId();
			UserProperties userForumProperties = bs.getProfileModule().getUserProperties(user.getId(), userWorkspaceId);
			if (null != userForumProperties) {
				Map relevanceMap = (Map)userForumProperties.getProperty(ObjectKeys.USER_PROPERTY_RELEVANCE_MAP);
				if (relevanceMap != null) {
					List trackedPeople = (List) relevanceMap.get(ObjectKeys.RELEVANCE_TRACKED_PEOPLE);
					if (trackedPeople != null) {
						Binder binder = bs.getBinderModule().getBinderWithoutAccessCheck(binderId);
						reply = trackedPeople.contains(binder.getOwnerId());
					}
				}
			}
		}
		
		// If we get here, reply is true if the user is tracking the
		// binder as a person and false otherwise.  Return it.
		return reply;
	}

	public static Map getSearchAndPagingModels(Map entries, Map options, boolean showTrash) {
		Map model = new HashMap();
		
		if (entries == null) {
			// there is no paging to set
			return model;
		}
		
		String sortBy = (String) options.get(ObjectKeys.SEARCH_SORT_BY);
		Boolean sortDescend = (Boolean) options.get(ObjectKeys.SEARCH_SORT_DESCEND);
		
		model.put(WebKeys.FOLDER_SORT_BY, sortBy);		
		model.put(WebKeys.FOLDER_SORT_DESCEND, sortDescend.toString());
		
		Integer totalSearchCount = (Integer) entries.get(ObjectKeys.TOTAL_SEARCH_COUNT);
		if (null == totalSearchCount) {
			totalSearchCount = (Integer) entries.get(ObjectKeys.SEARCH_COUNT_TOTAL);
			entries.put(ObjectKeys.TOTAL_SEARCH_COUNT, totalSearchCount);
		}
		int totalRecordsFound = totalSearchCount;
//		int totalRecordsReturned = (Integer) folderEntries.get(ObjectKeys.TOTAL_SEARCH_RECORDS_RETURNED);
		//Start Point of the Record
		int searchOffset = (Integer) options.get(ObjectKeys.SEARCH_OFFSET);
		int searchPageIncrement = (Integer) options.get(ObjectKeys.SEARCH_MAX_HITS);
		int goBackSoManyPages = 2;
		int goFrontSoManyPages = 3;
		
		HashMap pagingInfo = getPagingLinks(totalRecordsFound, searchOffset, searchPageIncrement, 
				goBackSoManyPages, goFrontSoManyPages);
		
		HashMap prevPage = (HashMap) pagingInfo.get(WebKeys.PAGE_PREVIOUS);
		ArrayList pageNumbers = (ArrayList) pagingInfo.get(WebKeys.PAGE_NUMBERS);
		HashMap nextPage = (HashMap) pagingInfo.get(WebKeys.PAGE_NEXT);
		String pageStartIndex = (String) pagingInfo.get(WebKeys.PAGE_START_INDEX);
		String pageEndIndex = (String) pagingInfo.get(WebKeys.PAGE_END_INDEX);

		model.put(WebKeys.PAGE_CURRENT, pagingInfo.get(WebKeys.PAGE_CURRENT));
		model.put(WebKeys.PAGE_PREVIOUS, prevPage);
		model.put(WebKeys.PAGE_NUMBERS, pageNumbers);
		model.put(WebKeys.PAGE_NEXT, nextPage);
		model.put(WebKeys.PAGE_START_INDEX, pageStartIndex);
		model.put(WebKeys.PAGE_END_INDEX, pageEndIndex);
		model.put(WebKeys.PAGE_TOTAL_RECORDS, ""+totalRecordsFound);
		
		double dblNoOfPages = Math.ceil((double)totalRecordsFound/searchPageIncrement);
		
		model.put(WebKeys.PAGE_COUNT, ""+dblNoOfPages);
		model.put(WebKeys.PAGE_LAST, String.valueOf(Math.round(dblNoOfPages)));
		model.put(WebKeys.PAGE_LAST_STARTING_INDEX, String.valueOf((Math.round(dblNoOfPages) -1) * searchPageIncrement));
		model.put(WebKeys.SEARCH_TOTAL_HITS, entries.get(ObjectKeys.SEARCH_COUNT_TOTAL));
		
		return model;
	}	
	
	//This method returns a HashMap with Keys referring to the Previous Page Keys,
	//Paging Number related Page Keys and the Next Page Keys.
	public static HashMap getPagingLinks(int intTotalRecordsFound, int intSearchOffset, 
			int intSearchPageIncrement, int intGoBackSoManyPages, int intGoFrontSoManyPages) {
		
		HashMap<String, Object> hmRet = new HashMap<String, Object>();
		ArrayList<HashMap> pagingInfo = new ArrayList<HashMap>(); 
		int currentDisplayValue = ( intSearchOffset + intSearchPageIncrement) / intSearchPageIncrement;
		hmRet.put(WebKeys.PAGE_CURRENT, String.valueOf(currentDisplayValue));

		//Adding Prev Page Link
		int prevInternalValue = intSearchOffset - intSearchPageIncrement;
		HashMap<String, Object> hmRetPrev = new HashMap<String, Object>();
		hmRetPrev.put(WebKeys.PAGE_DISPLAY_VALUE, "<<");
		hmRetPrev.put(WebKeys.PAGE_INTERNAL_VALUE, "" + prevInternalValue);
		if (intSearchOffset == 0) {
			hmRetPrev.put(WebKeys.PAGE_NO_LINK, "" + new Boolean(true));
		}
		hmRet.put(WebKeys.PAGE_PREVIOUS, hmRetPrev);

		//Adding Links before Current Display
		if (intSearchOffset != 0) {
			//Code for generating the Numeric Paging Information previous to offset			
			int startPrevDisplayFrom = currentDisplayValue - intGoBackSoManyPages;
			
			int wentBackSoManyPages = intGoBackSoManyPages + 1;
			for (int i = startPrevDisplayFrom; i < currentDisplayValue; i++) {
				wentBackSoManyPages--;
				if (i < 1) continue;
				prevInternalValue = (intSearchOffset - (intSearchPageIncrement * wentBackSoManyPages));
				HashMap<String, Object> hmPrev = new HashMap<String, Object>();
				hmPrev.put(WebKeys.PAGE_DISPLAY_VALUE, "" + i);
				hmPrev.put(WebKeys.PAGE_INTERNAL_VALUE, "" + prevInternalValue);
				pagingInfo.add(hmPrev);
			}
		}
		
		//Adding Links after Current Display
		for (int i = 0; i < intGoFrontSoManyPages; i++) {
			int nextInternalValue = intSearchOffset + (intSearchPageIncrement * i);
			int nextDisplayValue = (nextInternalValue + intSearchPageIncrement) / intSearchPageIncrement;  
			if ( !(nextInternalValue >= intTotalRecordsFound) ) {
				HashMap<String, Object> hmNext = new HashMap<String, Object>();
				hmNext.put(WebKeys.PAGE_DISPLAY_VALUE, "" + nextDisplayValue);
				hmNext.put(WebKeys.PAGE_INTERNAL_VALUE, "" + nextInternalValue);
				if (nextDisplayValue == currentDisplayValue) hmNext.put(WebKeys.PAGE_IS_CURRENT, new Boolean(true));
				pagingInfo.add(hmNext);
			}
			else break;
		}
		hmRet.put(WebKeys.PAGE_NUMBERS, pagingInfo);
		
		//Adding Next Page Link
		int nextInternalValue = intSearchOffset + intSearchPageIncrement;
		HashMap<String, Object> hmRetNext = new HashMap<String, Object>();
		hmRetNext.put(WebKeys.PAGE_DISPLAY_VALUE, ">>");
		hmRetNext.put(WebKeys.PAGE_INTERNAL_VALUE, "" + nextInternalValue);
		
		if ( (nextInternalValue >= intTotalRecordsFound) ) {
			hmRetNext.put(WebKeys.PAGE_NO_LINK, "" + new Boolean(true));
		}
		hmRet.put(WebKeys.PAGE_NEXT, hmRetNext);
		hmRet.put(WebKeys.PAGE_START_INDEX, "" + (intSearchOffset + 1));
		
		hmRet.put(WebKeys.PAGE_END_INDEX, "" + intTotalRecordsFound);
		
		return hmRet;
	}

	public static Long addMiniBlogEntry(AllModulesInjected bs, String text) {
		return addMiniBlogEntryDetailed(bs, text).getEntryId();
	}
	public static MiniBlogInfo addMiniBlogEntryDetailed(AllModulesInjected bs, String text) {
		if(text == null)
			return null;

		MiniBlogInfo reply = new MiniBlogInfo();
		Long entryId = null;

    	Pattern p = Pattern.compile("([\\s]*)$");
    	Matcher m = p.matcher(text);
    	if (m.find()) {
			//Trim any trailing whitespace
    		text = text.substring(0, m.start(0));
    	}
    	text = text.replaceAll("&quot;", "\"");
		
        User user = RequestContextHolder.getRequestContext().getUser();
		if (text.length() > ObjectKeys.USER_STATUS_DATABASE_FIELD_LENGTH) {
			text = text.substring(0, ObjectKeys.USER_STATUS_DATABASE_FIELD_LENGTH - 3) + "...";
		}
		bs.getProfileModule().setStatus(text);
		bs.getProfileModule().setStatusDate(new Date());
		bs.getReportModule().addStatusInfo(user);
		if (0 < text.length()) {
			//Add this to the user's mini blog folder
			Long miniBlogId = user.getMiniBlogId();
			Folder miniBlog = null;
			if (miniBlogId == null) {
				//The miniblog folder doesn't exist, so create it
				miniBlog = bs.getProfileModule().addUserMiniBlog(user);
				reply.setNewMiniBlogFolder(true);
				
			} else {
				try {
					miniBlog = (Folder) bs.getBinderModule().getBinder(miniBlogId);
					if (miniBlog.isDeleted()) {
						//The miniblog folder doesn't exist anymore, so try create it again
						miniBlog = bs.getProfileModule().addUserMiniBlog(user);
						reply.setNewMiniBlogFolder(true);
					}
				} catch(NoBinderByTheIdException e) {
					//The miniblog folder doesn't exist anymore, so try create it again
					logger.debug("BinderHelper.addMiniBlogEntry(NoBinderByTheIdException):  Calling bs.getProfileModule().addUserMiniBlog(...)");
					miniBlog = bs.getProfileModule().addUserMiniBlog(user);
					reply.setNewMiniBlogFolder(true);
				}
			}
			if (miniBlog != null) {
				//Found the mini blog folder, go add this new entry
				reply.setFolderId(miniBlog.getId());
		        DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, 
		        		DateFormat.SHORT, user.getLocale());
		        dateFormat.setTimeZone(user.getTimeZone());
				String mbTitle = dateFormat.format(new Date());
				Map data = new HashMap(); // Input data
				data.put(ObjectKeys.FIELD_ENTITY_TITLE, mbTitle);
				data.put(ObjectKeys.FIELD_ENTITY_DESCRIPTION, text);
				data.put(ObjectKeys.FIELD_ENTITY_DESCRIPTION_FORMAT, String.valueOf(Description.FORMAT_NONE));
				Definition def = miniBlog.getDefaultEntryDef();
				if (def == null) {
					try {
						def = bs.getDefinitionModule().getDefinitionByReservedId(ObjectKeys.DEFAULT_ENTRY_MINIBLOG_DEF);
					} catch (Exception ex) {
						logger.debug("BinderHelper.addMiniBlogEntry(Exception:  '" + MiscUtil.exToString(ex) + "'):  1:  Ignored");
					}
				}
				if (def != null) {
					FolderModule folderModule = bs.getFolderModule();
					
					miniBlogId = miniBlog.getId();
					try {
						entryId = folderModule.addEntry(miniBlogId, def.getId(), new MapInputData(data), null, null).getId();
					} catch (Exception ex) {
						logger.debug("BinderHelper.addMiniBlogEntry(Exception:  '" + MiscUtil.exToString(ex) + "'):  2:  Ignored");
					}
					if (null != entryId) {
						// Mark the entry as read
						bs.getProfileModule().setSeen(user.getId(),folderModule.getEntry(miniBlogId, entryId));
					}
				}
			}
		}

		reply.setEntryId(entryId);
		return reply;
	}
	
	public static void saveFolderColumnSettings(AllModulesInjected bs, ActionRequest request, 
			ActionResponse response, Long binderId) {
		boolean showTrash = PortletRequestUtils.getBooleanParameter(request, WebKeys.URL_SHOW_TRASH, false);
		Binder binder = bs.getBinderModule().getBinder(binderId);
		Map formData = request.getParameterMap();
		Map columns = new LinkedHashMap();
		Map columnsText = new LinkedHashMap();
		List<String> columnOrderList = null;
		String columnOrder = PortletRequestUtils.getStringParameter(request, "columns__order", "");
		//Convert the old style column sort order to a list
		if (columnOrder != null && !columnOrder.equals("")) {
			columnOrderList = new ArrayList<String>();
			String[] sortOrder = columnOrder.split("\\|");
			for (String columnName:  sortOrder) {
				if (MiscUtil.hasString(columnName)) {
					columnOrderList.add(columnName);
				}
			}
		}

		String[] columnNames;
		if (showTrash) {
			columnNames = TrashHelper.trashColumns;
		}
		else {
			columnNames = ListFolderHelper.folderColumns;
		}
		for (int i = 0; i < columnNames.length; i++) {
			columns.put(columnNames[i], PortletRequestUtils.getStringParameter(request, columnNames[i], ""));
			columnsText.put(columnNames[i], PortletRequestUtils.getStringParameter(request, "ss_col_text_"+columnNames[i], null));
		}
		Iterator itFormData = formData.entrySet().iterator();
		while (itFormData.hasNext()) {
			Map.Entry me = (Map.Entry) itFormData.next();
			if (me.getKey().toString().startsWith("customCol_", 0)) {
				String colName = me.getKey().toString().substring(10, me.getKey().toString().length());
				columns.put(colName, "on");
				columnsText.put(colName, PortletRequestUtils.getStringParameter(request, "ss_col_text_"+colName, null));
			}
		}
		
		//See if this request was to set the folder default
		boolean folderDefault = formData.containsKey("setFolderDefaultColumns");
		
		//Save the column settings
		saveFolderColumnSettings(bs, binderId, columns, columnsText, columnOrderList, folderDefault);
	}
	
	/**
	 * Saves the folder columns configuration on the specified binder.
	 * 
	 * @param bs
	 * @param binderId
	 * @param columns		// column name, "on" or "". If "on", then the columns is shown
	 * @param columnsText	// column name, column title or null. If null, the default title is shown
	 * @param columnsOrder	//String of column names separated by "|"
	 * @param isDefault		//true if this is the default for the binder
	 * 
	 * @return
	 */
	public static void saveFolderColumnSettings(AllModulesInjected bs, Long binderId, 
			Map columns, Map columnsText, List<String> columnOrder, boolean folderDefault) {
        User user = RequestContextHolder.getRequestContext().getUser();
		Binder binder = bs.getBinderModule().getBinder(binderId);
		
		//See if this request was to set the folder default
		if (folderDefault || binder instanceof TemplateBinder) {
			if (bs.getBinderModule().testAccess(binder, BinderOperation.modifyBinder)) {
				bs.getBinderModule().setProperty(binder.getId(), ObjectKeys.BINDER_PROPERTY_FOLDER_COLUMNS, columns);
				bs.getBinderModule().setProperty(binder.getId(), ObjectKeys.BINDER_PROPERTY_FOLDER_COLUMN_SORT_ORDER_LIST, columnOrder);
				bs.getBinderModule().setProperty(binder.getId(), ObjectKeys.BINDER_PROPERTY_FOLDER_COLUMN_SORT_ORDER, null);
				bs.getBinderModule().setProperty(binder.getId(), ObjectKeys.BINDER_PROPERTY_FOLDER_COLUMN_TITLES, columnsText);
			}
		}
		
		Map values = new HashMap();
		values.put(ObjectKeys.USER_PROPERTY_FOLDER_COLUMNS, columns);
		values.put(ObjectKeys.USER_PROPERTY_FOLDER_COLUMN_SORT_ORDER_LIST, columnOrder);
		values.put(ObjectKeys.USER_PROPERTY_FOLDER_COLUMN_SORT_ORDER, null);
		values.put(ObjectKeys.USER_PROPERTY_FOLDER_COLUMN_TITLES, columnsText);
		//Reset the column positions to the default
	   	values.put(WebKeys.FOLDER_COLUMN_POSITIONS, "");
	   	bs.getProfileModule().setUserProperties(user.getId(), binderId, values);

	}
	
	public static void buildWorkflowSupportBeans(AllModulesInjected bs, List entryList, Map model) {
		Map captionMap = new HashMap();
		Map threadMap = new HashMap();
		Map questionsMap = new HashMap();
		Map questionRespondersMap = new HashMap();
		Map transitionMap = new HashMap();
		Map descriptionMap = new HashMap();
		for (int i=0; i<entryList.size(); i++) {
			FolderEntry entry = (FolderEntry)entryList.get(i);
			Set states = entry.getWorkflowStates();
			for (Iterator iter=states.iterator(); iter.hasNext();) {
				WorkflowState ws = (WorkflowState)iter.next();
				//store the UI caption for each state
				captionMap.put(ws.getTokenId(), WorkflowUtils.getStateCaption(ws.getDefinition(), ws.getState()));
				descriptionMap.put(ws.getTokenId(), WorkflowUtils.getStateDescription(ws.getDefinition(), ws.getState()));
				//See if user can transition out of this state
				if (!entry.isPreDeleted()) {
					//get all manual transitions
					Map trans = bs.getFolderModule().getManualTransitions(entry, ws.getTokenId());
					transitionMap.put(ws.getTokenId(), trans);
				} 
				if (!threadMap.containsKey(ws.getTokenId()) && Validator.isNotNull(ws.getThreadName())) {
					String threadCaption = WorkflowUtils.getThreadCaption(ws.getDefinition(), ws.getThreadName());
					if (Validator.isNull(threadCaption)) threadCaption = ws.getThreadName();
					threadMap.put(ws.getTokenId(), threadCaption);
				}
					
				if (!entry.isPreDeleted()) {
					Map<String,Map> qMap = bs.getFolderModule().getWorkflowQuestions(entry, ws.getTokenId());
					//Get the responders for each question
					for (String q : qMap.keySet()) {
						Map<Long,User> qResponders = WorkflowProcessUtils.getQuestionResponderPrincipals(entry, ws, q);
						questionRespondersMap.put(q, qResponders);
					}
					questionsMap.put(ws.getTokenId(), qMap);
				}
			}
		}
		model.put(WebKeys.WORKFLOW_CAPTIONS, captionMap);
		model.put(WebKeys.WORKFLOW_THREAD_CAPTIONS, threadMap);
		model.put(WebKeys.WORKFLOW_QUESTIONS, questionsMap);
		model.put(WebKeys.WORKFLOW_QUESTION_RESPONDERS, questionRespondersMap);
		model.put(WebKeys.WORKFLOW_TRANSITIONS, transitionMap);
		model.put(WebKeys.WORKFLOW_DESCRIPTIONS, descriptionMap);
	}

    public static boolean checkIfWorkflowResponseAllowed(WorkflowSupport entry, WorkflowState ws, String questionName) {
     	User user = RequestContextHolder.getRequestContext().getUser();
     	boolean response = true;
 		//Check to make sure this is allowed
 		if (WorkflowProcessUtils.checkIfQuestionRespondersSpecified(entry, ws, questionName)) {
 			//Build a list of the people who can respond
 			Set<Long> responders = WorkflowProcessUtils.getQuestionResponders(entry, ws, questionName, true);
	   		//See if this includes All Users
	        Long allUsersId = Utils.getAllUsersGroupId();
	        Long allExtUsersId = Utils.getAllExtUsersGroupId();
	        if ((allUsersId != null && responders.contains(allUsersId) && user.getIdentityInfo().isInternal()) ||
	        		(allExtUsersId != null && responders.contains(allExtUsersId) && !user.getIdentityInfo().isInternal())) {
	        	//All Users can respond (leave the answer = true)
	        } else if (!responders.contains(user.getId())) {
 				//This user is not on the responder list. Just ignore the request
 				response = false;
 			}
 		}
 		return response;
     }
     

	public static List getAllApplications(AllModulesInjected bs) {
		Map options = new HashMap();
		options.put(ObjectKeys.SEARCH_SORT_BY, Constants.SORT_TITLE_FIELD);
		options.put(ObjectKeys.SEARCH_SORT_DESCEND, Boolean.FALSE);
		//get them all
		options.put(ObjectKeys.SEARCH_MAX_HITS, Integer.MAX_VALUE-1);

		Document searchFilter = DocumentHelper.createDocument();
		Element field = searchFilter.addElement(Constants.FIELD_ELEMENT);
    	field.addAttribute(Constants.FIELD_NAME_ATTRIBUTE,Constants.ENTRY_TYPE_FIELD);
    	Element child = field.addElement(Constants.FIELD_TERMS_ELEMENT);
    	child.setText(Constants.ENTRY_TYPE_APPLICATION);
    	options.put(ObjectKeys.SEARCH_FILTER_AND, searchFilter);
    	
		Map searchResults = bs.getProfileModule().getApplications(options);
		List applications = (List) searchResults.get(ObjectKeys.SEARCH_ENTRIES);
		return applications;
	}

	/**
	 * Returns the ID of the next/previous item in the given folder.
	 * 
	 * @param bs
	 * @param folder
	 * @param entryId
	 * @param next
	 * @param options
	 * 
	 * @return
	 */
	public static Long getNextPrevEntry(AllModulesInjected bs, Folder folder, Long entryId, boolean next, Map options) {
		// If we don't have a folder...
		if (folder == null) {
			// ...there can be no next.
			return null; 
		}

		// We need to provide the next/previous in the context of the
		// folder's current filter.  If we weren't given the filter as
		// part of the options Map, read it and add it there.  
		User user = RequestContextHolder.getRequestContext().getUser();
		UserProperties userFolderProperties = bs.getProfileModule().getUserProperties(user.getId(), folder.getId());
		Document searchFilter;
		if (null == options) {
			options = new HashMap();
			searchFilter = null;
		}
		else {
			searchFilter = ((Document) options.get(ObjectKeys.SEARCH_SEARCH_FILTER));
		}
		if (null == searchFilter) {
			addSearchFiltersToOptions(bs, folder, userFolderProperties, true, options);
		}
		
      	options.put(ObjectKeys.SEARCH_MAX_HITS, Integer.valueOf(ObjectKeys.SEARCH_MAX_HITS_FOLDER_ENTRIES));
      	options.put(ObjectKeys.SEARCH_OFFSET, Integer.valueOf(0));
      	initSortOrder(bs, userFolderProperties, options, getViewType(bs, folder.getId()));

      	Map searchResults = bs.getFolderModule().getEntries(folder.getId(), options);
		List folderEntries = (List) searchResults.get(ObjectKeys.SEARCH_ENTRIES);

		for (int i = 0; i < folderEntries.size(); i++) {
			Map entry = (Map) folderEntries.get(i);
			if (entry.containsKey("_docId") && ((String)entry.get("_docId")).equals(entryId.toString())) {
				//Found the current entry
				if (next) {
					i++;
				} else {
					i--;
				}
				if (i >= 0 && i < folderEntries.size()) {
					entry = (Map) folderEntries.get(i);
					String docId = (String) entry.get("_docId");
					if (docId != null) return Long.valueOf(docId);
				}
				return null;
			}
		}
		return null;
	}
	
	public static Long getNextPrevEntry(AllModulesInjected bs, Folder folder, Long entryId, boolean next) {
		return getNextPrevEntry(bs, folder, entryId, next, null);
	}

	/**
	 * Adds the user's search filters as a single Document to an
	 * options Map.
	 * 
	 * @param bs
	 * @param binder
	 * @param userFolderProperties
	 * @param unescapeName
	 * @param options
	 */
	public static void addSearchFiltersToOptions(AllModulesInjected bs, Binder binder, UserProperties userFolderProperties, boolean unescapeName, Map options) {
		// Does the user have any search filters defined on this binder?
		Document searchFilters = getBinderSearchFilter(bs, binder, userFolderProperties, unescapeName);
		if (null != searchFilters) {
			// Yes!  Stuff them into the options Map.
			options.put(ObjectKeys.SEARCH_SEARCH_FILTER, searchFilters);
		}
	}
	
	/**
	 * Returns the user's search filters as a single Document.
	 * 
	 * @param bs
	 * @param binder
	 * @param userFolderProperties
	 * @param unescapeName
	 */
	public static Document getBinderSearchFilter(AllModulesInjected bs, Binder binder, UserProperties userFolderProperties, boolean unescapeName) {
		// Convert any existing V1 filters.
		convertV1Filters(bs, userFolderProperties);

		// Does the user have any filters selected on this folder?
		List<String> currentFilters = getCurrentUserFilters(userFolderProperties);
		if (!(currentFilters.isEmpty())) {
			// Yes!  Get the personal and global filters from the
			// binder properties.
			Map personalFilters = ((Map) userFolderProperties.getProperty(ObjectKeys.USER_PROPERTY_SEARCH_FILTERS));
			Map globalFilters   = ((Map) binder.getProperty(              ObjectKeys.BINDER_PROPERTY_FILTERS)     );

			// Scan the user's filters...
			SearchFilter searchFilter = new SearchFilter(true);
			for (String filterSpec: currentFilters) {
				// ...extracting the name...
				String filterName  = getFilterNameFromSpec(filterSpec);
				if (unescapeName) {
					filterName = MiscUtil.replace(filterName, "+", " ");
				}
				
				// ...scope...
				String  filterScope = getFilterScopeFromSpec(filterSpec);
				boolean isGlobal    = filterScope.equals(ObjectKeys.USER_PROPERTY_USER_FILTER_GLOBAL);
				
				// ...and filter XML for each.
				String searchFilterXml;
				if (isGlobal)
				     searchFilterXml = ((String) globalFilters.get(  filterName));
				else searchFilterXml = ((String) personalFilters.get(filterName));

				// Do we have XML for this filter?
				if (MiscUtil.hasString(searchFilterXml)) {
					Document searchFilterDoc;
					try {
						// Yes!  Parse it and append it to the search
						// filter.
						searchFilterDoc = XmlUtil.parseText(searchFilterXml);
						searchFilter.appendFilter(searchFilterDoc);
					}
					
					catch (Exception ignore) {
						// Log the exception...
						logger.error("BinderHelper.addSearchFiltersToOptions(Exception:  '" + MiscUtil.exToString(ignore) + "')", ignore);
						
						// ...get rid of the bogus filter.
						if (isGlobal) {
							globalFilters.remove(  searchFilterXml);
							bs.getBinderModule().setProperty(
								binder.getId(),
								ObjectKeys.BINDER_PROPERTY_FILTERS,
								globalFilters);
						}
						
						else {
							personalFilters.remove(searchFilterXml);
							bs.getProfileModule().setUserProperty(
								userFolderProperties.getId().getPrincipalId(),
								userFolderProperties.getId().getBinderId(),
								ObjectKeys.USER_PROPERTY_SEARCH_FILTERS,
								personalFilters);
						}
					}
				}
			}

			// If we get here, searchFilter contains the combined
			// filters that the user has selected.  Stuff the Document
			// into the options Map.
			return searchFilter.getFilter();
		}
		
		else {
			return null;
		}
	}
	
	/*
	 * Given a filter specification, returns the name component.
	 */
	private static String getFilterNameFromSpec(String filterSpec) {
		String reply = null;
		if (null != filterSpec) {
			int cPos = filterSpec.indexOf(':');
			if (0 < cPos) {
				reply = filterSpec.substring(cPos + 1);
			}
		}
		return reply;
	}
	
	/*
	 * Given a filter specification, returns the scope component.
	 */
	private static String getFilterScopeFromSpec(String filterSpec) {
		String reply = null;
		if (null != filterSpec) {
			int cPos = filterSpec.indexOf(':');
			if (0 < cPos) {
				reply = filterSpec.substring(0, cPos);
			}
		}
		return reply;
	}
	
	/*
	 * Constructs a string used as a filter specification that combines
	 * for the filter's name and scope.
	 */
	private static String buildFilterSpec(String filterName, String filterScope) {
		return (filterScope + ":" + filterName);
	}
	
	/**
	 * Returns a List<String> of the user's current filters from their
	 * properties on a folder.
	 * 
	 * @param userFolderProperties
	 * 
	 * @return
	 */
	public static List<String> getCurrentUserFilters(UserProperties userFolderProperties, boolean unescapeName) {
		List<String> currentFilters = ((List<String>) userFolderProperties.getProperty(ObjectKeys.USER_PROPERTY_USER_FILTERS));
		if (null == currentFilters) {
			currentFilters = new ArrayList<String>();
		}
		String filterName = ((String) userFolderProperties.getProperty(ObjectKeys.USER_PROPERTY_USER_FILTER));
		if (MiscUtil.hasString(filterName)) {
			String filterScope = ((String) userFolderProperties.getProperty(ObjectKeys.USER_PROPERTY_USER_FILTER_SCOPE));
			if (!(MiscUtil.hasString(filterScope))) {
				filterScope = ObjectKeys.USER_PROPERTY_USER_FILTER_PERSONAL;
			}
			if (unescapeName) {
				filterName = MiscUtil.replace(filterName, "+", " ");
			}
			String filterSpec = buildFilterSpec(filterName, filterScope);
			if (!(currentFilters.contains(filterSpec))) {
				currentFilters.add(filterSpec);
			}
		}
		return currentFilters;
	}
	
	public static List<String> getCurrentUserFilters(UserProperties userFolderProperties) {
		// Always use the initial form of the method.
		return getCurrentUserFilters(userFolderProperties, false);
	}

	public static void initSortOrder(AllModulesInjected bs, 
			UserProperties userFolderProperties, Map options, String viewType) {
		//Start - Determine the Sort Order
		//since one one tab/folder, no use in saving info in tabs
		//Trying to get Sort Information first from the options and
		//then from the User Folder Properties
		String searchSortBy = (String) options.get(ObjectKeys.SEARCH_SORT_BY);
		if (!(MiscUtil.hasString(searchSortBy))) {
			searchSortBy = (String) userFolderProperties.getProperty(ObjectKeys.SEARCH_SORT_BY);
		}
		String searchSortDescend = (String) options.get(ObjectKeys.SEARCH_SORT_DESCEND);
		if (!(MiscUtil.hasString(searchSortDescend))) {
			searchSortDescend = (String) userFolderProperties.getProperty(ObjectKeys.SEARCH_SORT_DESCEND);
		}
		
		//Setting the Sort properties if it is available in the Tab or User Folder Properties Level. 
		//If not, go with the Default Sort Properties 
		if (Validator.isNotNull(searchSortBy)) {
			options.put(ObjectKeys.SEARCH_SORT_BY, searchSortBy);
			if (("true").equalsIgnoreCase(searchSortDescend)) {
				options.put(ObjectKeys.SEARCH_SORT_DESCEND, Boolean.TRUE);
			} else {
				options.put(ObjectKeys.SEARCH_SORT_DESCEND, Boolean.FALSE);
			}
		}
		if (!options.containsKey(ObjectKeys.SEARCH_SORT_BY)) { 
			options.put(ObjectKeys.SEARCH_SORT_BY, Constants.SORTNUMBER_FIELD);
			options.put(ObjectKeys.SEARCH_SORT_DESCEND, Boolean.TRUE);
		} else if (!options.containsKey(ObjectKeys.SEARCH_SORT_DESCEND)) {
			options.put(ObjectKeys.SEARCH_SORT_DESCEND, Boolean.TRUE);
		}
		//End - Determine the Sort Order
		
	}
	public static void initSortOrder(AllModulesInjected bs, 
			UserProperties userFolderProperties, Map options) {
		initSortOrder(bs, userFolderProperties, options, null);
	}
	
	public static boolean isBinderNameLegal(String name) {
		boolean result = true;
		Pattern pattern = Pattern.compile("[/\\*?\"<>:|]");
		Matcher m = pattern.matcher( name );
		if (m.find()) return false;
		return result;
	}
	
	public static FileAttachment getFileAttachmentById(AllModulesInjected bs, String fileId) {
		FileAttachment fa = null;
		try {
			fa = bs.getFileModule().getFileAttachmentById(fileId);
		} catch(AccessControlException e) {}
		return fa;
	}
	
	//Look for requests to create new files during the add or modify entry operations
	public static void processCreateFileRequests(AllModulesInjected bs, ActionRequest request, Map fileMap) {
		Map formData = request.getParameterMap();
		Iterator itFormData = formData.keySet().iterator();
		while (itFormData.hasNext()) {
			String key = (String)itFormData.next();
			if (key.startsWith(WebKeys.CREATE_FILE_NAME_FORM_ELEMENT)) {
				//Found a key, get the form element name (which is appended to the end)
				String eleName = key.substring(WebKeys.CREATE_FILE_NAME_FORM_ELEMENT.length(), key.length());
				String[] fileName = (String[])formData.get(WebKeys.CREATE_FILE_NAME_FORM_ELEMENT + eleName);
				String[] fileType = (String[])formData.get(WebKeys.CREATE_FILE_TYPE_FORM_ELEMENT + eleName);
				if (Validator.isNotNull(fileName[0]) && Validator.isNotNull(fileType[0])) {
					String fn = fileName[0];
					if(!fn.contains("."))
						fn += fileType[0];
					MultipartFile mf = new SimpleMultipartFile(fn, 
							getNewableFileSupport().getInitialFileContent(fileType[0]));
					fileMap.put(eleName, mf);
				}
			}
		}
	}

	public static void fixupFolderAndEntryDefinitions(PortletRequest request, AllModulesInjected bs, Long binderId,
			boolean folderFixups, boolean entryFixups, String entryDefinition) throws FixupFolderDefsException {
		// Do we already have a fixup thread running?
		FixupFolderDefsThread fixFolderDefsThread = FixupFolderDefsThread.getFixupFolderDefsThread(request);
		if ((fixFolderDefsThread != null) && fixFolderDefsThread.isFolderFixupInProgress()) {
			// Yes!  Then we can't start another.
			throw new FixupFolderDefsException(
				FixupFolderDefsException.FixupExceptionCause.EXCEPTION_PREVIOUS_THREAD_BUSY);
		}

		// If we can't create a new fixup thread...
		fixFolderDefsThread = FixupFolderDefsThread.createFixupFolderDefsThread(
			request,
			binderId,
			folderFixups,
			entryFixups,
			entryDefinition);
		
		if (null == fixFolderDefsThread) {
			// ...tell the user.
			throw new FixupFolderDefsException(
				FixupFolderDefsException.FixupExceptionCause.EXCEPTION_CANT_CREATE_THREAD);
		}
		
		// Note that we start the thread via a separate AJAX request so
		// that we can display a message to the user that it's running.
	}
	
	public static boolean isFolderFixupInProgress(PortletRequest request) {
		FixupFolderDefsThread fixFolderDefsThread = FixupFolderDefsThread.getFixupFolderDefsThread(request);
		return ((fixFolderDefsThread != null) && fixFolderDefsThread.isFolderFixupInProgress());
	}
	
	public static boolean isFolderFixupReady(PortletRequest request) {
		FixupFolderDefsThread fixFolderDefsThread = FixupFolderDefsThread.getFixupFolderDefsThread(request);
		return ((fixFolderDefsThread != null) && fixFolderDefsThread.isFolderFixupReady());
	}
	
	public static void indexEntity(DefinableEntity entity) {
		if (EntityType.folderEntry.equals(entity.getEntityType())) {
			getFolderModule().indexEntry((FolderEntry)entity, false);
		} else if (EntityType.folder.equals(entity.getEntityType()) || 
				EntityType.workspace.equals(entity.getEntityType()) || 
				EntityType.profiles.equals(entity.getEntityType())) {
			getBinderModule().indexBinder(entity.getId(), false);
		} else if (EntityType.user.equals(entity.getEntityType())) {
			getProfileModule().indexEntry((Principal)entity);
		}
	}

	/**
	 * Returns true if a title is already registered in a binder and
	 * false otherwise.
	 * 
	 * @param binderId
	 * @param title
	 * 
	 * @return
	 */
	public static boolean isTitleRegistered(Long binderId, String title) {
		CoreDao cd = ((CoreDao) SpringContextUtil.getBean("coreDao"));
		return (cd.isTitleRegistered(binderId, WebHelper.getNormalizedTitle(title)));
	}
	
	private static SimpleDateFormat getSimpleDateFormat() {
		// Teaming stores date value in string representation in the XML for the change log.
		// This string representation can contain date format symbols that may not be meaningful 
		// to some non-US locales. For example, the "month" values (eg. May) are not pertaining
		// to the Japanese locale. Consequently, an attempt to parse such string using OS-default
		// locale will fail when the default locale of the OS does not support those date format
		// symbols. Therefore, we MUST explicitly specify to use US locale for parsing of such
		// string.
		return new SimpleDateFormat("dd MMM yyyy HH:mm:ss z", Locale.US);
	}

	/**
	 * Returns the name of the family from the default view associated
	 * with a binder.
	 * 
	 * @param binder
	 * @return
	 */
	public static String getBinderDefaultFamilyName(Binder binder) {
		Definition def = ((null == binder) ? null : binder.getDefaultViewDef());
		return getFamilyNameFromDef(def);
	}
	
	/**
	 * Returns the name of the default view associated with a binder.
	 * 
	 * @param binder
	 * 
	 * @return
	 */
	public static String getBinderDefaultViewName(Binder binder) {
		Definition def        = ((null == binder)     ? null : binder.getDefaultViewDef());
		Document   defDoc     = ((null == def)        ? null : def.getDefinition());
		Element    defDocRoot = ((null == defDoc)     ? null : defDoc.getRootElement());
		String     reply      = ((null == defDocRoot) ? null : defDocRoot.attributeValue("name"));
		
		return reply;
	}	

	/**
	 * Returns the family name from a Definition.
	 * 
	 * @param def
	 * 
	 * @return
	 */
	public static String getFamilyNameFromDef(Definition def) {
		Document   defDoc     = ((null == def)        ? null : def.getDefinition());
		Element    defDocRoot = ((null == defDoc)     ? null : defDoc.getRootElement());
		Element    family     = ((null == defDocRoot) ? null : ((Element) defDocRoot.selectSingleNode("//properties/property[@name='family']")));
		String     reply      = ((null == family)     ? null : family.attributeValue("value", ""));
		
		return reply;
	}
		
	/**
	 * Returns the definition from a folder's view.
	 * 
	 * @param bs
	 * @param folderId
	 */
	public static Definition getFolderDefinitionFromView(AllModulesInjected bs, Long folderId) {
		//Since there is nothing important being revealed, get the binder without doing an access check for better performance
		Binder binder = bs.getBinderModule().getBinderWithoutAccessCheck(folderId);
		return getFolderDefinitionFromView(bs, binder);
	}
	public static Definition getFolderDefinitionFromView(AllModulesInjected bs, Binder binder) {
		// Does the user have a default definition selected for this
		// binder?
		UserProperties userFolderProperties = bs.getProfileModule().getUserProperties(RequestContextHolder.getRequestContext().getUser().getId(), binder.getId());
		String userSelectedDefinition = ((String) userFolderProperties.getProperty(ObjectKeys.USER_PROPERTY_DISPLAY_DEFINITION));

		// If we can find the default definition for this binder,
		// return it.
		HashMap model = new HashMap();
		DefinitionHelper.getDefinitions(binder, model, userSelectedDefinition);		
		return ((Definition) model.get(WebKeys.DEFAULT_FOLDER_DEFINITION));
	}
	
	/*
	 */
	private static BinderModule getBinderModule() {
		return ((BinderModule) SpringContextUtil.getBean("binderModule"));
	}
	
	/*
	 */
	private static FolderModule getFolderModule() {
		return ((FolderModule) SpringContextUtil.getBean("folderModule"));
	}
	
	/*
	 */
	private static NewableFileSupport getNewableFileSupport() {
		return ((NewableFileSupport) SpringContextUtil.getBean("newableFileSupport"));
	}
	
	/*
	 */
	private static ProfileModule getProfileModule() {
		return ((ProfileModule) SpringContextUtil.getBean("profileModule"));
	}
	
	/*
	 */
	private static ReportModule getReportModule() {
		return ((ReportModule) SpringContextUtil.getBean("reportModule"));
	}
	
	/*
	 */
	private static FolderDao getFolderDao() {
		return ((FolderDao) SpringContextUtil.getBean("folderDao"));
	}
	
	/*
	 */
	private static CoreDao getCoreDao() {
		return ((CoreDao) SpringContextUtil.getBean("coreDao"));
	}
	
	/*
	 */
	private static ResourceDriverManager getResourceDriverManager() {
		return ((ResourceDriverManager) SpringContextUtil.getBean("resourceDriverManager"));
	}
	
	/**
	 * Make sure binder title is unique.
	 * 
	 * If title is not unique in the parent binder, then change it to
	 * be unique by adding '(n)' to the name. However, if caller specified <code>requiredTitle</code>,
	 * then it must be possible to use this specified value as the title. Otherwise, it is an error.
	 * 
	 * @param title
	 * @param parent
	 * 
	 * @return
	 */
    public static String getUniqueBinderTitleInParent(String title, Binder parent, String requiredTitle) throws TitleException {
    	List<Binder> subBinders = parent.getBinders();
    	Set<String> binderTitles = new HashSet<String>();
    	for (Binder b : subBinders) {
    		binderTitles.add(b.getTitle().toLowerCase());
    	}
    	if(Validator.isNotNull(requiredTitle)) {
    		// required title specified
    		if(binderTitles.contains(requiredTitle.toLowerCase()))
    			throw new TitleException(requiredTitle); // the title is already used
    		else
    			return requiredTitle; // the title is available
    	}
    	else {
	    	int maxTries = 100;
	    	Pattern p = Pattern.compile("(^.*\\()([0-9]+)\\)$", Pattern.CASE_INSENSITIVE);
	    	while (binderTitles.contains(title.toLowerCase())) {
	    		//There is another binder with this title. Try the next title higher
	    		Matcher m = p.matcher(title);
	    		if (m.find()) {
	    			String t1 = m.group(1);
	    			String n = m.group(2);
	    			Integer n2 = Integer.valueOf(n) + 1;
	    			title = t1 + String.valueOf(n2) + ")";		//Rebuild the title with the (number) incremented
	    		} else {
	    			title = title + "(2)";
	    		}
	    		if (--maxTries <= 0) break;
	    	}
	    	return title;
    	}
    }
	
    /**
     * Returns the title from a user workspace's owner.  If the title
     * can't be determined, null is returned.
     * 
     * @param wsId
     * 
     * @return
     */
	public static String getUserWorkspaceOwnerTitle(Long wsId) {
		// Is the binder the guest user's workspace?
		String title = null;
		if (isBinderGuestWorkspaceId(wsId)) {
			// Yes!  Access the guest user.
			title = getProfileModule().getGuestUser().getTitle();
		}
		
		else {
			// No, it's not the guest user's workspace!  Access
			// the owner.
			Binder wsBinder = getBinderModule().getBinder(wsId);
			if ((null != wsBinder) && (wsBinder instanceof Workspace)) {
				Principal owner = wsBinder.getOwner();
				if (null != owner) {
					title = owner.getTitle();
				}
			}
		}
		return title;
	}
	
	/**
	 * Returns true if a binder is the current user's workspace and
	 * false otherwise.
	 * 
	 * @param binder
	 * 
	 * @return
	 */
	public static boolean isBinderCurrentUsersWS(Binder binder) {
		// Is the binder a workspace?
		boolean reply = false;
		if (binder.getEntityType().name().equals(EntityType.workspace.name())) {
			// Yes!  Is it the current user's workspace?
			Long binderId = binder.getId();
			User currentUser = RequestContextHolder.getRequestContext().getUser();
			Long currentUserWSId = currentUser.getWorkspaceId();
			reply = ((null != currentUserWSId) && currentUserWSId.equals(binderId));
		}
		
		// If we get here, reply is true if binder is the current
		// user's workspace and false otherwise.  Return it.
		return reply;
	}
	
	/**
	 * Returns true if the given binder is one of the many binders a
	 * user can't delete, even if they have rights to do so.
	 * 
	 * @param binder
	 * 
	 * @return
	 */
	public static boolean isBinderDeleteProtected(Binder binder) {
		return
			(isBinderSystemUserWS(    binder) ||	// Any system user (e.g., Email posting agent, ...)
			 isBinderCurrentUsersWS(  binder) ||	// The currently logged in user's workspace.
			 isBinderProfilesRootWS(  binder) ||	// The root workspace that contains all other workspaces.
			 isBinderNetFoldersRootWS(binder) ||	// The root workspace that contains all Net Folders.
			 isBinderHomeFolder(      binder) ||	// Any user's Home folder.
			 isBinderTopNetFolder(    binder) ||	// Any top level Net Folder.
			 isBinderMyFilesStorage(  binder));		// Any user's My Files Storage folder.
	}
	
	/**
	 * Returns true if a binder is a Home folder and false otherwise.
	 * 
	 * @param binder
	 * 
	 * @return
	 */
	public static boolean isBinderHomeFolder(Binder binder) {
		// Is the binder a folder?
		boolean reply = false;
		if (binder.getEntityType().name().equals(EntityType.folder.name())) {
			// Yes!  Is it a Home folder?
			reply = ((Folder) binder).isHomeDir();
		}
		
		// If we get here, reply is true if binder is a Home folder
		// and false otherwise.  Return it.
		return reply;
	}

	/**
	 * Returns true if the specified binder is recognized as a My Files
	 * Storage folder and false otherwise.
	 * 
	 * @param binder
	 * @param updateBinderFlags
	 * 
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public static boolean isBinderMyFilesStorage(Binder binder, boolean updateBinderFlags) {
		SimpleProfiler.start("BinderHelper.isBinderMyFilesStorage(()");
		try {
			// If we weren't given a binder or it's not a folder...
			if ((null == binder) || (!(binder instanceof Folder))) {
				// ...it's not a My Files Storage folder.
				return false;
			}
	
			// If the binder says it's a My Files Storage folder...
	    	boolean reply = binder.isMyFilesDir();
	    	if (reply) {
	    		// ...there's nothing more to do.  Return what it
	    		// ...said.
	    		return reply;
	    	}
	
	    	// If the binder has a My Files Storage marker...
	    	Boolean mfDir = ((Boolean) binder.getProperty(ObjectKeys.BINDER_PROPERTY_MYFILES_DIR_DEPRECATED));
	    	if (null != mfDir) {
	    		// ...use that value...
	    		reply = mfDir.booleanValue();
	
	    		// ...and if we're supposed to...
	    		if (updateBinderFlags) {
		    		// ...clean things up so all we'll use from here on is
		    		// ...what's on the binder.
		    		updateBinderMyFilesDirMarkers(binder);
	    		}
	    	}
	
	    	// If we get here, reply is true if the binder was recognized
	    	// as a My Files Storage folder and false otherwise.  Return
	    	// it.
	    	return reply;
		}
		
		finally {
			SimpleProfiler.stop("BinderHelper.isBinderMyFilesStorage(()");
		}
	}
	
	public static boolean isBinderMyFilesStorage(Binder binder) {
		// Always use the initial form of the method.
		return isBinderMyFilesStorage(binder, true);	// true -> If necessary, update the My Files Storage binder markers.
	}

	/**
	 * Corrects the My Files Storage markings on a binder so that it
	 * uses a database field instead of a binder property for that
	 * purpose.
	 * 
	 * @param binder
	 */
	@SuppressWarnings("deprecation")
	public static void updateBinderMyFilesDirMarkers(final Binder binder) {
		// If we weren't given a binder...
		if (null == binder) {
			// ...there's nothing to correct.
			return;
		}
		
		// Does the binder have a My Files Storage marker stored in its
		// properties?
    	final Boolean mfDir = ((Boolean) binder.getProperty(ObjectKeys.BINDER_PROPERTY_MYFILES_DIR_DEPRECATED));
    	if (null != mfDir) {
    		// Yes!  Does it differ from that stored on the Binder?
    		final boolean markersDiffer = (mfDir.booleanValue() != binder.isMyFilesDir());

    		// Create a callback we can use to update the My Files
    		// Storage markers as a particular user.
    		final Long    binderOwnerId       = binder.getOwnerId();
    		final boolean binderOwnerResolves = (null != ResolveIds.getResolvedUser(binderOwnerId, true));
			RunasCallback doUpdate = new RunasCallback() {
				@Override
				public Object doAs() {
					updateBinderMyFilesDirMarkersImpl(binder, binderOwnerResolves, mfDir, markersDiffer);
					return null;
				}
			};
			
    		// If the user that owns the binder can be resolved, we
			// correct the My Files Storage markers as that user.
			// Otherwise, we do it as admin.
    		String zoneName = RequestContextHolder.getRequestContext().getZoneName();
    		if (binderOwnerResolves)
    		     RunasTemplate.runas(     doUpdate, zoneName, binderOwnerId);
    		else RunasTemplate.runasAdmin(doUpdate, zoneName               );
    	}
	}

	/*
	 * Does the actual changes as required by 
	 * updateBinderMyFilesDirMarkers().
	 */
	@SuppressWarnings("deprecation")
	private static void updateBinderMyFilesDirMarkersImpl(Binder binder, boolean binderOwnerResolves, boolean mfDir, boolean updateBinderFlag) {
		// If necessary, update the binder's marker...
		BinderModule bm = getBinderModule();
		Long binderId = binder.getId();
		if (updateBinderFlag) {
			bm.setMyFilesDir(binderId, mfDir);
		}
		
		// ...remove the property...
		bm.setProperty(binderId, ObjectKeys.BINDER_PROPERTY_MYFILES_DIR_DEPRECATED, null);

		// ...and if the binder owner can be resolved...
		if (binderOwnerResolves) {
			// ...remove any My Files Storage marked stored in their
			// ...UserProperties. 
			removeUserPropertiesMyFilesDirMarkers(binder.getOwnerId());
		}
	}
	
	/**
	 * Removes any My Files Storage marker that may have been stored in
	 * a user's properties.  We now use only the marker stored on the
	 * binder for that purpose.
	 * 
	 * @param userId
	 */
	@SuppressWarnings("deprecation")
	public static void removeUserPropertiesMyFilesDirMarkers(Long userId) {
		// Does this user have a My Files Storage marker stored in
		// their UserProperties?
		ProfileModule  pm             = getProfileModule();
   		UserProperties userProperties = pm.getUserProperties(userId);
   		Long           mfId           = ((null == userProperties) ? null : ((Long) userProperties.getProperty(ObjectKeys.USER_PROPERTY_MYFILES_DIR_DEPRECATED)));
   		if (null != mfId) {
   			// Yes!  Remove it.
	   		pm.setUserProperty(userId, ObjectKeys.USER_PROPERTY_MYFILES_DIR_DEPRECATED, null);
   		}
	}
	
	/**
	 * Returns true if a binder is the root profiles workspace and
	 * false otherwise.
	 * 
	 * @param binder
	 * 
	 * @return
	 */
	public static boolean isBinderProfilesRootWS(Binder binder) {
		boolean reply = false;
		if (binder instanceof Workspace) {
			Workspace ws = ((Workspace) binder);
			if (ws.isReserved()) {
				reply = ws.getInternalId().equals(ObjectKeys.PROFILE_ROOT_INTERNALID);
			}
		}
		return reply;
	}

	/**
	 * Returns true if a binder is the root Net Folders workspace and
	 * false otherwise.
	 * 
	 * @param binder
	 * 
	 * @return
	 */
	public static boolean isBinderNetFoldersRootWS(Binder binder) {
		boolean reply = false;
		if (binder instanceof Workspace) {
			Workspace ws = ((Workspace) binder);
			if (ws.isReserved()) {
				reply = ws.getInternalId().equals(ObjectKeys.NET_FOLDERS_ROOT_INTERNALID);
			}
		}
		return reply;
	}
	
	/**
	 * Returns true if a binder is a top level Net Folder and false
	 * otherwise.
	 * 
	 * @param binder
	 * 
	 * @return
	 */
	public static boolean isBinderTopNetFolder(Binder binder) {
		// Is the binder a folder?
		boolean reply = false;
		if (binder.getEntityType().name().equals(EntityType.folder.name())) {
			// Yes!  Is it a Net Folder?
			if (binder.isAclExternallyControlled()) {
				// Yes!  Is it a top level Net Folder?
				reply = ((Folder) binder).isTop();
			}
		}
		
		// If we get here, reply is true if binder is a top level Net
		// Folder and false otherwise.  Return it.
		return reply;
	}

	/**
	 * Returns true if the specified binder is the user's currently
	 * active My Files Storage folder and false otherwise.
	 *
	 * @param bs
	 * @param user
	 * @param binder
	 * 
	 * @return
	 */
	public static boolean isBinderUsersActiveMyFilesStorage(AllModulesInjected bs, User user, Binder binder) {
		// If the binder is a My Files Storage folder...
		boolean reply = isBinderMyFilesStorage(binder);
		if (reply) {
			// ...that's contained in the user's workspace, it's
			// ...recognized as the user's active My Files Storage
			// ...folder.
			reply = binder.getParentBinder().getId().equals(user.getWorkspaceId());
		}
		
		// If we get here, reply is true if the binder was recognized
		// as the user's My Files Storage folder and false otherwise.
		// Return it.
		return reply;
	}
	
	public static boolean isBinderUsersActiveMyFilesStorage(AllModulesInjected bs, Binder binder) {
		// Always use the initial form of the method.
		return isBinderUsersActiveMyFilesStorage(bs, RequestContextHolder.getRequestContext().getUser(), binder);
	}
	
	/**
	 * Returns true if a binder is a system user's workspace and false
	 * otherwise.
	 * 
	 * @param binder
	 * 
	 * @return
	 */
	public static boolean isBinderSystemUserWS(Binder binder) {
		// Is the binder a workspace?
		boolean reply = false;
		if (binder.getEntityType().name().equals(EntityType.workspace.name())) {
			// Yes!  Is it the guest user's workspace?
			Long binderId = binder.getId();
			if (isBinderGuestWorkspaceId(binderId)) {
				// Yes!  Return true.
				reply = true;
			}
			
			else {
				// No, the binder isn't get guest user's workspace!  Is
				// the owner a system user?
				Principal owner = binder.getOwner();
				if (owner.isReserved()) {
					// Yes!  Is the binder that user's workspace?
					Long ownerWSId = owner.getWorkspaceId();
					reply = ((null != ownerWSId) && ownerWSId.equals(binderId));
				}
			}
		}
		
		// If we get here, reply is true if binder is a system user's
		// workspace and false otherwise.  Return it.
		return reply;
	}
	
    public static void moveEntryCheckMirrored(Binder binder, Entry entry, Binder destination) 
			throws NotSupportedException {
    	if(binder.isMirrored()) {
    		ResourceDriver sourceDriver = getResourceDriverManager().getDriver(binder.getResourceDriverName());
 			if(sourceDriver.isReadonly()) {
 				throw new NotSupportedException("errorcode.notsupported.moveEntry.mirroredSource.readonly",
 						new String[] {sourceDriver.getTitle(), binder.getPathName()});
 			}
 			else {
 				if(destination.isMirrored()) {
 					ResourceDriver destDriver = getResourceDriverManager().getDriver(destination.getResourceDriverName());
 					if(destDriver.isReadonly()) {
 						throw new NotSupportedException("errorcode.notsupported.moveEntry.mirroredDestination.readonly",
 								new String[] {destDriver.getTitle(), destination.getPathName()});
 					}
 				}
 				else {
 					throw new NotSupportedException("errorcode.notsupported.moveEntry.mirroredSource",
 							new String[] {binder.getPathName(), destination.getPathName()});
 				}
 			}
 	   }
 	   else {
 		   if(destination.isMirrored()) {
 				throw new NotSupportedException("errorcode.notsupported.moveEntry.mirroredDestination",
 						new String[] {binder.getPathName(), destination.getPathName()});			   
 		   }
 	   }
    }
    
    public static void copyEntryCheckMirrored(Binder binder, Entry entry, Binder destination) 
    		throws NotSupportedException {
    	if(binder.isMirrored()) {
			if(destination.isMirrored()) {
				ResourceDriver destDriver = getResourceDriverManager().getDriver(destination.getResourceDriverName());
				if(destDriver.isReadonly()) {
					throw new NotSupportedException("errorcode.notsupported.copyEntry.mirroredDestination.readonly",
							new String[] {destDriver.getTitle(), destination.getPathName()});
				}
			}
			else {
				//It is always OK to copy from mirrored to non-mirrored
			}
 	   }
 	   else {
 		   if (destination.isMirrored()) {
 			   //If the source is not mirrored and the destination is mirrored, then check that the entry has one and only one file
 			   Set<Attachment> atts = entry.getAttachments();
 			   if (atts.size() == 1 || (!entry.isTop() && atts.size() == 0)) {
 				   //This is either the top entry with one attached file or a reply with no attached files
 				   //Now check that all comments have no attached files
 				   List<FolderEntry>children = getFolderDao().loadEntryDescendants((FolderEntry)entry);
 				   for (FolderEntry child:children) {
 					   atts = child.getAttachments();
 					   if (!atts.isEmpty()) {
 			 			   throw new NotSupportedException("errorcode.notsupported.copyEntry.complexEntryToMirrored." + (destination.isAclExternallyControlled() ? "net" : "mirrored"),
 			 						new String[] {binder.getPathName(), destination.getPathName()});			   
 					   }
 				   }
 				   //This entry is OK to copy
 				   return;
 			   }
 			   throw new NotSupportedException("errorcode.notsupported.copyEntry.complexEntryToMirrored." + (destination.isAclExternallyControlled() ? "net" : "mirrored"),
 						new String[] {binder.getPathName(), destination.getPathName()});			   
 		   }
 	   }
    }
    
    public static void copyOrMoveEntryCheckUniqueFileNames(Binder destination, Entry entry)
    		throws TitleException {
        copyOrMoveEntryCheckUniqueFileNames(destination, entry, null);
    }

    public static void copyOrMoveEntryCheckUniqueFileNames(Binder destination, Entry entry, String [] toFileNames)
    		throws TitleException {
    	//get Entry Children
    	if (entry instanceof FolderEntry) {
    		List entries = getFolderDao().loadEntryDescendants((FolderEntry)entry);
        	entries.add(entry);
        	// Check file titles in the entries
       		for (Iterator iter=entries.iterator(); iter.hasNext();) {
       			FolderEntry e = (FolderEntry)iter.next();
       	    	Collection<FileAttachment> atts = entry.getFileAttachments();
                int i=0;
       	    	for (FileAttachment att : atts) {
       	    		String fileName = att.getFileItem().getName();
                    if (e.getId()==entry.getId() && toFileNames!=null) {
                        fileName = toFileNames[i];
                    }
	       			if (getCoreDao().isFileNameRegistered(destination.getId(), fileName)) {
	       				throw new TitleException(fileName);
	       			}
                    i++;
       	    	}
       		}
    	}

    }

    /**
     * Returns the default view display style currently in effect.
     * 
     * @return
     */
    public static String getDefaultViewDisplayStyle() {
    	return DEFAULT_VDS;
    }
    
    /**
     * Returns true if the given binder is a Vibe mirrored folder and
     * false otherwise.
     * 
     * @param binder
     * 
     * @return
     */
    public static boolean isVibeMirroredFolder(Binder binder) {
    	return (
    		(null != binder)           &&
    		(binder instanceof Folder) &&
    		binder.isMirrored()        &&
    		(!(binder.isAclExternallyControlled())));
    }
}
