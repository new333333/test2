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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;

import javax.portlet.PortletSession;
import javax.portlet.PortletURL;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.document.DateTools;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;

import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.Definition;
import org.kablink.teaming.domain.EntityIdentifier;
import org.kablink.teaming.domain.Group;
import org.kablink.teaming.domain.NoBinderByTheIdException;
import org.kablink.teaming.domain.Principal;
import org.kablink.teaming.domain.ProfileBinder;
import org.kablink.teaming.domain.SeenMap;
import org.kablink.teaming.domain.TemplateBinder;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.domain.UserProperties;
import org.kablink.teaming.domain.Workspace;
import org.kablink.teaming.domain.AuditType;
import org.kablink.teaming.domain.EntityIdentifier.EntityType;
import org.kablink.teaming.lucene.util.SearchFieldResult;
import org.kablink.teaming.module.admin.AdminModule.AdminOperation;
import org.kablink.teaming.module.binder.BinderModule.BinderOperation;
import org.kablink.teaming.module.definition.DefinitionUtils;
import org.kablink.teaming.module.profile.ProfileModule.ProfileOperation;
import org.kablink.teaming.module.profile.impl.GuestProperties;
import org.kablink.teaming.portletadapter.AdaptedPortletURL;
import org.kablink.teaming.portletadapter.support.PortletAdapterUtil;
import org.kablink.teaming.search.SearchUtils;
import org.kablink.teaming.security.AccessControlException;
import org.kablink.teaming.security.function.OperationAccessControlExceptionNoName;
import org.kablink.teaming.ssfs.util.SsfsUtil;
import org.kablink.teaming.util.AllModulesInjected;
import org.kablink.teaming.util.NLT;
import org.kablink.teaming.util.TagUtil;
import org.kablink.teaming.util.Utils;
import org.kablink.teaming.web.WebKeys;
import org.kablink.teaming.web.tree.WsDomTreeBuilder;
import org.kablink.util.BrowserSniffer;
import org.kablink.util.Validator;
import org.kablink.util.search.Constants;
import org.kablink.util.search.Criteria;

import static org.kablink.util.search.Constants.BINDERS_PARENT_ID_FIELD;
import static org.kablink.util.search.Constants.DOCID_FIELD;
import static org.kablink.util.search.Constants.ENTITY_FIELD;
import static org.kablink.util.search.Constants.ENTRY_ANCESTRY;
import static org.kablink.util.search.Constants.MODIFICATION_DATE_FIELD;

import org.springframework.web.portlet.ModelAndView;
import org.springframework.web.portlet.bind.PortletRequestBindingException;

/**
 * ?
 * 
 * @author ?
 */
@SuppressWarnings("unchecked")
public class WorkspaceTreeHelper {
	protected static final Log logger = LogFactory.getLog(Workspace.class);
	
	public static ModelAndView setupWorkspaceBeans(AllModulesInjected bs, Long binderId, RenderRequest request, 
			RenderResponse response, boolean showTrash) throws Exception {
 		Map<String,Object> model = new HashMap<String,Object>();
 		String view = setupWorkspaceBeans(bs, binderId, request, response, model, showTrash);
 		return new ModelAndView(view, model);
	}
	@SuppressWarnings("unused")
	public static String setupWorkspaceBeans(AllModulesInjected bs, Long binderId, RenderRequest request, 
			RenderResponse response, Map model, boolean showTrash) throws Exception {
		model.put(WebKeys.URL_SHOW_TRASH, new Boolean(showTrash));
		model.put(WebKeys.WORKSPACE_BEANS_SETUP, true);
        User user = RequestContextHolder.getRequestContext().getUser();
        String targetJSP = WebKeys.VIEW_WORKSPACE;

		BinderHelper.setBinderPermaLink(bs, request, response);
		try {
			//won't work on adapter
			response.setProperty(RenderResponse.EXPIRATION_CACHE,"0");
		} catch (UnsupportedOperationException us) {
			// Commented out as this exception occurs way too often to
			// log every one.
			// logger.debug("WorkspaceTreeHelper.setBinderPermalink(UnsupportedOperationException):  Ignored");
		}

		String operation = PortletRequestUtils.getStringParameter(request, WebKeys.URL_OPERATION, "");
		if (operation.equals(WebKeys.OPERATION_RELOAD_LISTING)) {
			//An action is asking us to build the url to reload the parent page
			PortletURL reloadUrl = response.createRenderURL();
			reloadUrl.setParameter(WebKeys.URL_BINDER_ID, binderId.toString());
			reloadUrl.setParameter(WebKeys.ACTION, WebKeys.ACTION_VIEW_WS_LISTING);
			String random = String.valueOf(new Random().nextInt(999999));
			reloadUrl.setParameter(WebKeys.URL_RANDOM, random);
			reloadUrl.setParameter(WebKeys.URL_OPERATION, "noop");
			request.setAttribute(WebKeys.RELOAD_URL_FORCED, reloadUrl.toString());
			return WebKeys.VIEW_WORKSPACE;
		}

		String errorMsg = PortletRequestUtils.getStringParameter(request, WebKeys.ENTRY_DATA_PROCESSING_ERRORS, "");
		String errorMsg2 = PortletRequestUtils.getStringParameter(request, WebKeys.FILE_PROCESSING_ERRORS, "");
		if (errorMsg.equals("")) {
			errorMsg = errorMsg2;
		} else if (!errorMsg.equals("") && !errorMsg2.equals("")) {
			errorMsg += "<br/>" + errorMsg2;
		}
		model.put(WebKeys.FILE_PROCESSING_ERRORS, errorMsg);
		if (!errorMsg.equals("")) {
			model.put(WebKeys.ERROR_MESSAGE, errorMsg);
			return "forum/reload_opener";
		}

		Binder binder = null;
		String entryIdString =  PortletRequestUtils.getStringParameter(request, WebKeys.URL_ENTRY_ID, "");
		Long entryId = null;
		if (Validator.isNotNull(entryIdString) && entryIdString.equals(WebKeys.URL_USER_ID_PLACE_HOLDER)) {
			entryId= user.getId();
		} else if (Validator.isNotNull(entryIdString) && !entryIdString.equals(WebKeys.URL_ENTRY_ID_PLACE_HOLDER)) {
			entryId= PortletRequestUtils.getLongParameter(request, WebKeys.URL_ENTRY_ID);
		}
		//see if it is a user workspace - can also get directly to user ws by a binderId
		//so don't assume anything here.  This just allows us to handle users without a workspace.
		if (entryId != null) {
			Long workspaceId;
			try                 {workspaceId = bs.getProfileModule().getEntryWorkspaceId(entryId);}
			catch (Exception e) {workspaceId = null;                                              }
			if (workspaceId == null && user.getId().equals(entryId)) {
				//This is the user trying to access his or her own workspace; try to create it
				binder = bs.getProfileModule().addUserWorkspace(user, null);
				if (binder == null) {
					// Redirect to profile list
					return redirectToProfileListing(response, String.valueOf(binderId), entryIdString, model);
				}
				workspaceId = binder.getId(); 
			} else if (workspaceId != null) {
				try {
					binder = bs.getBinderModule().getBinder(workspaceId);
				} catch (NoBinderByTheIdException nb) {
					//User workspace does not yet exist
					User entry = null;
					entry = (User)bs.getProfileModule().getEntry(entryId);
					model.put(WebKeys.USER_OBJECT, entry);
					return WebKeys.VIEW_NO_USER_WORKSPACE;
				} catch(AccessControlException e) {
					BinderHelper.setupStandardBeans(bs, request, response, model, binderId);
					if (WebHelper.isUserLoggedIn(request) && 
							!ObjectKeys.GUEST_USER_INTERNALID.equals(user.getInternalId())) {
						// Access is not allowed to the workspace.
						// Redirect to the profile list.
						return redirectToProfileListing(response, String.valueOf(binderId), entryIdString, model);
					} else {
						//Please log in
						String refererUrl = (String)request.getAttribute(WebKeys.REFERER_URL);
						model.put(WebKeys.URL, refererUrl);

				    	// Is self registration permitted?
				    	if ( MiscUtil.canDoSelfRegistration( bs ) )
				    	{
				    		// Yes.
				    		// Add the information needed to support the "Create new account" ui to the response.
				    		MiscUtil.addCreateNewAccountDataToResponse( bs, request, model );
				    	}

						return WebKeys.VIEW_LOGIN_PLEASE;
					}
				}
			} else {
				try {
					User entry = (User)bs.getProfileModule().getEntry(entryId);
					model.put(WebKeys.USER_OBJECT, entry);
				}
				catch(Exception e) {
				}
				// Redirect to viewing the profile entry
				PortletURL reloadUrl = response.createRenderURL();
				reloadUrl.setParameter(WebKeys.URL_BINDER_ID, bs.getProfileModule().getProfileBinderId().toString());
				reloadUrl.setParameter(WebKeys.ACTION, WebKeys.ACTION_VIEW_PROFILE_ENTRY);
				reloadUrl.setParameter(WebKeys.URL_ENTRY_ID, entryId.toString());
				reloadUrl.setParameter(WebKeys.URL_ENTRY_VIEW_STYLE, WebKeys.URL_ENTRY_VIEW_STYLE_FULL);
				model.put(WebKeys.RELOAD_URL_FORCED, reloadUrl.toString());
				return WebKeys.VIEW_NO_USER_WORKSPACE;
			}
			binderId = workspaceId;
			entryId = null;
		}
		try {
			BinderHelper.setupStandardBeans(bs, request, response, model, binderId);
		} catch(NoBinderByTheIdException e) {
			model.put(WebKeys.ERROR_MESSAGE, NLT.get("errorcode.no.folder.by.the.id", new String[] {binderId.toString()}));
			return WebKeys.VIEW_ERROR_RETURN;
		}
		UserProperties userProperties = (UserProperties)model.get(WebKeys.USER_PROPERTIES_OBJ);
		UserProperties userFolderProperties = (UserProperties)model.get(WebKeys.USER_FOLDER_PROPERTIES_OBJ);

 		//Remember the last binder viewed
		String namespace = response.getNamespace();
        if (PortletAdapterUtil.isRunByAdapter(request)) {
        	namespace = PortletRequestUtils.getStringParameter(request, WebKeys.URL_NAMESPACE, "");
        }
		PortletSession portletSession = WebHelper.getRequiredPortletSession(request);
		portletSession.setAttribute(WebKeys.LAST_BINDER_VIEWED + namespace, binderId, PortletSession.APPLICATION_SCOPE);
		portletSession.setAttribute(WebKeys.LAST_BINDER_ENTITY_TYPE + namespace, EntityType.workspace.name(), PortletSession.APPLICATION_SCOPE);
		
		Map formData = request.getParameterMap();
		try {
			if (binder == null) binder = bs.getBinderModule().getBinder(binderId, false);
			bs.getReportModule().addAuditTrail(AuditType.view, binder);
			BinderHelper.getBinderAccessibleUrl(bs, binder, entryId, request, response, model);

	 		//Check special options in the URL
			String[] debug = (String[])formData.get(WebKeys.URL_DEBUG);
			if (debug != null && (debug[0].equals(WebKeys.DEBUG_ON) || debug[0].equals(WebKeys.DEBUG_OFF))) {
				//The user is requesting debug mode to be turned on or off
				if (debug[0].equals(WebKeys.DEBUG_ON)) {
					bs.getProfileModule().setUserProperty(user.getId(), 
							ObjectKeys.USER_PROPERTY_DEBUG, new Boolean(true));
				} else if (debug[0].equals(WebKeys.DEBUG_OFF)) {
					bs.getProfileModule().setUserProperty(user.getId(), 
							ObjectKeys.USER_PROPERTY_DEBUG, new Boolean(false));
				}
			}
			//Build the navigation beans
			BinderHelper.buildNavigationLinkBeans(bs, binder, model);
			BinderHelper.buildWorkspaceTreeBean(bs, binder, model, null);
			
			//See if this is a user workspace
			boolean showProfile = false;
			if ((binder.getDefinitionType() != null) && 
					((binder.getDefinitionType().intValue() == Definition.USER_WORKSPACE_VIEW) ||
						(binder.getDefinitionType().intValue() == Definition.EXTERNAL_USER_WORKSPACE_VIEW))) {
				Principal owner = binder.getCreation().getPrincipal(); //creator is user
				
				boolean isBinderAdmin = false;
				try {
					ProfileBinder pbinder = bs.getProfileModule().getProfileBinder();
					if (bs.getProfileModule().testAccess(pbinder, ProfileOperation.manageEntries)) {
						isBinderAdmin = true;
					}
				} catch(AccessControlException ex) {}
				model.put(WebKeys.IS_BINDER_ADMIN, isBinderAdmin);
				
				if (owner != null) {
					//	turn owner into real object = not hibernate proxy
					owner = Utils.fixProxy(owner);
					try {
						User u = user;
						if (!user.getId().equals(owner.getId())) {
							u = (User)bs.getProfileModule().getEntry(owner.getId());
							u = (User)Utils.fixProxy(u);
						}
						model.put(WebKeys.PROFILE_CONFIG_ENTRY, u);							
						Document profileDef = u.getEntryDefDoc();
						model.put(WebKeys.PROFILE_CONFIG_DEFINITION, profileDef);
						
						//Get the profileEntry view type and determine which display type to choose business card or profile view
						Node typeNode = profileDef.getRootElement().selectSingleNode("//definition/properties/property[@name='type']");
						if(typeNode != null) {
							String value = ((Element)typeNode).attributeValue("value");
							if(value != null && value.equals("profileGWT")){
								model.put("ss_gwtProfile", true);
								model.put(WebKeys.PROFILE_CONFIG_ELEMENT, 
										profileDef.getRootElement().selectSingleNode("//item[@name='profileEntryStandardView']"));
							} else {
								model.put(WebKeys.PROFILE_CONFIG_ELEMENT, 
										profileDef.getRootElement().selectSingleNode("//item[@name='profileEntryBusinessCard']"));
							}
						} else {
							model.put(WebKeys.PROFILE_CONFIG_ELEMENT, 
									profileDef.getRootElement().selectSingleNode("//item[@name='profileEntryBusinessCard']"));
						}
						
						model.put(WebKeys.PROFILE_CONFIG_JSP_STYLE, Definition.JSP_STYLE_VIEW);
						model.put(WebKeys.USER_WORKSPACE, true);

						//Get the dashboard initial tab if one was passed in
						String type    = PortletRequestUtils.getStringParameter(request, WebKeys.URL_TYPE, "");
						String profile = (Utils.checkIfFilr() ? "1" : PortletRequestUtils.getStringParameter(request,WebKeys.URL_PROFILE, ""));
						
						//if we don't find a Url Type look to see if there is a profile value						
						if(type.equals("")) {
							if(profile.equals("")){
								//check for the Operation first, this would be a direct action to request the 
								//profile view or the workspace								
								//then if the profile value is not there, check this binder to the user's workspace
                                //and redirect all searches to the profile view, but if the user is themself just  
								//goto the workspace
								if(operation != null && operation.equals(WebKeys.ACTION_SHOW_PROFILE)){
									showProfile = true;
								} else if(operation != null && operation.equals(WebKeys.ACTION_SHOW_WORKSPACE)){
									showProfile = false;
								} else if(!user.getWorkspaceId().equals( binder.getId()) ) {
									showProfile = true;
								}
							} else if(profile.equals("1")){
								showProfile = true;
							}
							model.put(WebKeys.ACTION_SHOW_PROFILE, showProfile);
						}
						
				        //Modify profile is not available to the guest user
				        if (showProfile && 
				        		!ObjectKeys.GUEST_USER_INTERNALID.equals(user.getInternalId())) {

				        	GwtUIHelper.setCommonRequestInfoData(request, bs, model);
				        	boolean showModifyProfile = false;
							if (owner.isActive() && bs.getProfileModule().testAccess(owner, ProfileOperation.modifyEntry)) {
								showModifyProfile = true;
							}

				        	if (showModifyProfile) {
								AdaptedPortletURL adapterUrl = new AdaptedPortletURL(request, "ss_forum", true);
								adapterUrl.setParameter(WebKeys.ACTION, WebKeys.ACTION_MODIFY_PROFILE_ENTRY);
								adapterUrl.setParameter(WebKeys.URL_BINDER_ID, owner.getParentBinder().getId().toString());
								adapterUrl.setParameter(WebKeys.URL_ENTRY_ID, owner.getId().toString());
								model.put(WebKeys.MODIFY_ENTRY_ALLOWED, true);
								model.put(WebKeys.MODIFY_ENTRY_ADAPTER,  adapterUrl.toString());
							} else {
								model.put(WebKeys.MODIFY_ENTRY_ALLOWED, false);
							}
				        	
				        	if (bs.getProfileModule().testAccess(owner, ProfileOperation.deleteEntry)) {
				    			Map qualifiers = new HashMap();
				    			qualifiers.put("popup", new Boolean(true));
				    			PortletURL url = response.createActionURL();
				    			url.setParameter(WebKeys.ACTION, WebKeys.ACTION_MODIFY_PROFILE_ENTRY);
				    			url.setParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_DELETE);
				    			url.setParameter(WebKeys.URL_BINDER_ID, owner.getParentBinder().getId().toString());
				    			url.setParameter(WebKeys.URL_ENTRY_ID, owner.getId().toString());
				    			model.put(WebKeys.DELETE_ENTRY_ADAPTER, url.toString());
				        	} else {
				    			model.put(WebKeys.DELETE_ENTRY_ADAPTER, "");
				        	}
				        }
				        
				        else {
							model.put(WebKeys.MODIFY_ENTRY_ALLOWED, false);
				        }
						
				        RelevanceDashboardHelper.setupRelevanceDashboardBeans(bs, request, response, 
				        		binder.getId(), type, model);
					} catch (Exception ex) {
        				logger.debug("WorkspaceTreeHelper.setupWorkspaceBeans(Exception:  '" + MiscUtil.exToString(ex) + "'):  User may have been deleted, but ws left around:  Ignored");
					}
				}
			}
			
			//Set up more standard beans
			//See if this is a user workspace
			if ((binder.getDefinitionType() != null) && 
					((binder.getDefinitionType().intValue() == Definition.USER_WORKSPACE_VIEW) ||
						(binder.getDefinitionType().intValue() == Definition.EXTERNAL_USER_WORKSPACE_VIEW))) {
				if (!showProfile && model.containsKey("ssRDCurrentTab")) {
					if ( ObjectKeys.RELEVANCE_DASHBOARD_OVERVIEW.equalsIgnoreCase( (String)model.get("ssRDCurrentTab") ) ) {
						//This user workspace is showing the accessories tab, so set up those beans
						DashboardHelper.getDashboardMap(binder, userProperties.getProperties(), model);
					}
				}
			} else {
				DashboardHelper.getDashboardMap(binder, userProperties.getProperties(), model);
			}
			if (!model.containsKey(WebKeys.SEEN_MAP)) 
				model.put(WebKeys.SEEN_MAP,bs.getProfileModule().getUserSeenMap(user.getId()));
			//See if the user has selected a specific view to use
			String userDefaultDef = (String)userFolderProperties.getProperty(ObjectKeys.USER_PROPERTY_DISPLAY_DEFINITION);
			DefinitionHelper.getDefinitions(binder, model, userDefaultDef);
			
			
			if (operation.equals(WebKeys.OPERATION_SHOW_TEAM_MEMBERS)) {
				model.put(WebKeys.SHOW_TEAM_MEMBERS, true);
				getTeamMembers(bs, formData, request, response, (Workspace)binder, model);
			} else {
				Document searchFilter = BinderHelper.getSearchFilter(bs, binder, userFolderProperties);
				Document configDocument = (Document)model.get(WebKeys.CONFIG_DEFINITION);
				String viewType = null;
				if (!showTrash) {
					viewType = DefinitionUtils.getViewType(configDocument);
				}
				if (viewType == null) viewType = "";
				if (viewType.equals(Definition.VIEW_STYLE_DISCUSSION_WORKSPACE)) {
					getShowDiscussionWorkspace(bs, formData, request, response, (Workspace)binder, searchFilter, model);					
				} else if (viewType.equals(Definition.VIEW_STYLE_PROJECT_WORKSPACE)) {
					getShowWorkspace(bs, formData, request, response, (Workspace)binder, searchFilter, model, showTrash);
					getShowProjectWorkspace(bs, formData, request, response, (Workspace)binder, searchFilter, model);
				} else {
					getShowWorkspace(bs, formData, request, response, (Workspace)binder, searchFilter, model, showTrash);
				}
			}
			Map tagResults = TagUtil.uniqueTags(bs.getBinderModule().getTags(binder));
			model.put(WebKeys.COMMUNITY_TAGS, tagResults.get(ObjectKeys.COMMUNITY_ENTITY_TAGS));
			model.put(WebKeys.PERSONAL_TAGS, tagResults.get(ObjectKeys.PERSONAL_ENTITY_TAGS));
			
			//Build the mashup beans
			Document configDocument = (Document)model.get(WebKeys.CONFIG_DEFINITION);
			DefinitionHelper.buildMashupBeans(bs, binder, configDocument, model, request );
			
			String type = PortletRequestUtils.getStringParameter(request, WebKeys.URL_TYPE, "");
			model.put(WebKeys.TYPE, type);
			String page = PortletRequestUtils.getStringParameter(request, WebKeys.URL_PAGE, "0");
			model.put(WebKeys.PAGE_NUMBER, page);
			if (type.equals(WebKeys.URL_WHATS_NEW)) 
				BinderHelper.setupWhatsNewBinderBeans(bs, binder, model, page);
			if (type.equals(WebKeys.URL_UNSEEN)) 
				BinderHelper.setupUnseenBinderBeans(bs, binder, model, page);
			
			// If the user is not the Guest user and the user is looking at their own workspace,
			// then get the user's tutorial panel state (closed, expanded or collapsed).
			{
				String		tutorialPanelState	= null;
				PortletURL	url;
				
				// Are we dealing with the Guest user?
				if ( !(userProperties instanceof GuestProperties) )
				{
					Integer binderDefType;

					// No
					// Are we dealing with a user workspace?
					binderDefType = binder.getDefinitionType();
					if ( binderDefType != null && 
							(binderDefType.intValue() == Definition.USER_WORKSPACE_VIEW ||
							 binderDefType.intValue() == Definition.EXTERNAL_USER_WORKSPACE_VIEW))
					{
						Long workspaceId;
						
						// Yes
						// Is the user looking at their own workspace?
						workspaceId = user.getWorkspaceId();
						if ( workspaceId != null && binderId.intValue() == workspaceId.intValue() )
						{
							// Yes, get the user's tutorial panel state.
							tutorialPanelState = (String) userProperties.getProperty( ObjectKeys.USER_PROPERTY_TUTORIAL_PANEL_STATE );
						}
					}
				}
				
				// Do we have a tutorial panel state?
				if ( tutorialPanelState == null || tutorialPanelState.length() == 0 )
				{
					// No, default to expanded.
					tutorialPanelState = "2";
				}
				
				// Add the tutorial panel state to the response.
				model.put( WebKeys.TUTORIAL_PANEL_STATE, tutorialPanelState );
				
				// Construct the url needed to invoke the "Play tutorial" page and add the url to the response.
				url = response.createActionURL();
				url.setParameter( WebKeys.ACTION, WebKeys.ACTION_PLAY_TUTORIAL );
				model.put( WebKeys.PLAY_TUTORIAL_BASE_URL, url.toString() );
			}
			
		} catch(NoBinderByTheIdException e) {
			model.put(WebKeys.ERROR_MESSAGE, NLT.get("errorcode.no.folder.by.the.id", new String[] {binderId.toString()}));
			return WebKeys.VIEW_ERROR_RETURN;
		} catch(OperationAccessControlExceptionNoName e) {
			//Access is not allowed
			if (WebHelper.isUserLoggedIn(request) && 
					!ObjectKeys.GUEST_USER_INTERNALID.equals(user.getInternalId())) {
				//Access is not allowed
				String refererUrl = (String)request.getAttribute(WebKeys.REFERER_URL);
				model.put(WebKeys.URL, refererUrl);
				return WebKeys.VIEW_ACCESS_DENIED;
			} else {
				//Please log in
				String refererUrl = (String)request.getAttribute(WebKeys.REFERER_URL);
				model.put(WebKeys.URL, refererUrl);
				return WebKeys.VIEW_LOGIN_PLEASE;
			}
		} catch(AccessControlException e) {
			//Access is not allowed
			if (WebHelper.isUserLoggedIn(request) && 
					!ObjectKeys.GUEST_USER_INTERNALID.equals(user.getInternalId())) {
				//Access is not allowed
				String refererUrl = (String)request.getAttribute(WebKeys.REFERER_URL);
				model.put(WebKeys.URL, refererUrl);
				return WebKeys.VIEW_ACCESS_DENIED;
			} else {
				//Please log in
				String refererUrl = (String)request.getAttribute(WebKeys.REFERER_URL);
				model.put(WebKeys.URL, refererUrl);
				return WebKeys.VIEW_LOGIN_PLEASE;
			}
		}
		
		//Set up the standard beans
		model.put(WebKeys.BINDER, binder);
		model.put(WebKeys.FOLDER, binder);
		model.put(WebKeys.DEFINITION_ENTRY, binder);
		model.put(WebKeys.ENTRY, binder);
		
		Tabs.TabEntry tab;
		if (showTrash) {
			tab = TrashHelper.buildTrashTabs(request, binder, model);
		}
		else {
			tab = BinderHelper.initTabs(request, binder);
			model.put(WebKeys.TABS, tab.getTabs());
		}

		//Build a reload url
		PortletURL reloadUrl = response.createRenderURL();
		reloadUrl.setParameter(WebKeys.URL_BINDER_ID, binderId.toString());
		reloadUrl.setParameter(WebKeys.ACTION, WebKeys.ACTION_VIEW_WS_LISTING);
		reloadUrl.setParameter(WebKeys.URL_RANDOM, WebKeys.URL_RANDOM_PLACEHOLDER);
		model.put(WebKeys.RELOAD_URL, reloadUrl.toString());
		
		if(binder == null) {
			return "binder/deleted_binder";
		}
		
		Object obj = model.get(WebKeys.CONFIG_ELEMENT);
		if ((obj == null) || (obj.equals(""))) {
			buildWorkspaceToolbar(bs, request, response, model, (Workspace)binder, binder.getId().toString());
			return WebKeys.VIEW_NO_DEFINITION;
		}
		obj = model.get(WebKeys.CONFIG_DEFINITION);
		if ((obj == null) || (obj.equals(""))) {
			buildWorkspaceToolbar(bs, request, response, model, (Workspace)binder, binder.getId().toString());
			return WebKeys.VIEW_NO_DEFINITION;
		}
		
		return targetJSP;
	}
	
	protected static void getShowWorkspace(AllModulesInjected bs, Map formData, 
			RenderRequest req, RenderResponse response, Workspace ws, 
			Document searchFilter, Map<String,Object>model, boolean showTrash) throws Exception {
		Document wsTree;
		Long wsId = ws.getId();

		Long top = PortletRequestUtils.getLongParameter(req, WebKeys.URL_OPERATION2);
		if ((top != null) && (!ws.isRoot())) {
			wsTree = bs.getBinderModule().getDomBinderTree(top, wsId, new WsDomTreeBuilder(ws, true, bs));
		} else {
			wsTree = bs.getBinderModule().getDomBinderTree(wsId, new WsDomTreeBuilder(ws, true, bs),1);
		}
		model.put(WebKeys.WORKSPACE_DOM_TREE, wsTree);
		
		//Get the info for the "add a team" button
		buildAddTeamButton(bs, req, response, ws, model);
		buildWorkspaceToolbar(bs, req, response, model, ws, wsId.toString());
		
		if (showTrash) {
			TrashHelper.buildTrashViewToolbar(model);
			Map options = TrashHelper.buildTrashBeans(bs, req, response, wsId, model);
			Map trashEntries = TrashHelper.getTrashEntities(bs, model, ws, options);
			model.putAll(ListFolderHelper.getSearchAndPagingModels(trashEntries, options, showTrash));
			if (trashEntries != null) {
				List trashEntriesList = (List) trashEntries.get(ObjectKeys.SEARCH_ENTRIES);
				model.put(WebKeys.FOLDER_ENTRIES, trashEntriesList);
			}
		}
	}
	
	protected static void getShowDiscussionWorkspace(AllModulesInjected bs, Map formData, 
			RenderRequest req, RenderResponse response, Workspace ws, 
			Document searchFilter, Map<String,Object>model) throws PortletRequestBindingException {
		
    	Map<String, Counter> unseenCounts = new HashMap();

    	//Get the sorted list of child binders
		Map options = new HashMap();
		options.put(ObjectKeys.SEARCH_SORT_BY, org.kablink.util.search.Constants.SORT_TITLE_FIELD);
		options.put(ObjectKeys.SEARCH_SORT_DESCEND, new Boolean(false));
		options.put(ObjectKeys.SEARCH_MAX_HITS, ObjectKeys.MAX_BINDER_ENTRIES_RESULTS);
		Map searchResults = bs.getBinderModule().getBinders(ws, options);
		List<Map> binders = (List)searchResults.get(ObjectKeys.SEARCH_ENTRIES);
		model.put(WebKeys.BINDERS, binders); 
		
		//Now get the next level of binders below the workspaces in "binders"
		List binderIdList = new ArrayList();
		for (Map binder:binders) {
			String binderIdString = (String) binder.get(DOCID_FIELD);
			String binderEntityType = (String) binder.get(ENTITY_FIELD);
			if (binderIdString != null) {
				if (binderEntityType != null && (binderEntityType.equals(EntityIdentifier.EntityType.workspace.name()) ||
						binderEntityType.equals(EntityIdentifier.EntityType.profiles.name()))) {
					binderIdList.add(binderIdString);
				}
				unseenCounts.put(binderIdString, new WorkspaceTreeHelper.Counter());
			}
		}
		if (!binderIdList.isEmpty()) {
			//Now search for the next level of binders
			options = new HashMap();
			options.put(ObjectKeys.SEARCH_SORT_BY, org.kablink.util.search.Constants.SORT_TITLE_FIELD);
			options.put(ObjectKeys.SEARCH_SORT_DESCEND, new Boolean(false));
			options.put(ObjectKeys.SEARCH_MAX_HITS, ObjectKeys.MAX_BINDER_ENTRIES_RESULTS);
			Map searchResults2 = bs.getBinderModule().getBinders(ws, binderIdList, options);
			List<Map> binders2 = (List)searchResults2.get(ObjectKeys.SEARCH_ENTRIES);
			Map subBinders = new HashMap();
			for (Map binder : binders2) {
				String binderId = (String) binder.get(BINDERS_PARENT_ID_FIELD);
				if (binderId != null) {
					if (!subBinders.containsKey(binderId)) subBinders.put(binderId, new ArrayList());
					List binderList = (List) subBinders.get(binderId);
					binderList.add(binder);
					unseenCounts.put((String) binder.get(DOCID_FIELD), 
							new WorkspaceTreeHelper.Counter());
				}
			}
			model.put(WebKeys.BINDERS_SUB_BINDERS, subBinders);
		}

		//Get the recent entries anywhere in this workspace
		options = new HashMap();
		List binderIds = new ArrayList();
		binderIds.add(ws.getId().toString());
	    
		//get entries created within last 30 days
		Date creationDate = new Date();
		creationDate.setTime(creationDate.getTime() - ObjectKeys.SEEN_TIMEOUT_DAYS*24*60*60*1000);
		String startDate = DateTools.dateToString(creationDate, DateTools.Resolution.SECOND);
		String now = DateTools.dateToString(new Date(), DateTools.Resolution.SECOND);
		Criteria crit = SearchUtils.newEntriesDescendants(binderIds);
		crit.add(org.kablink.util.search.Restrictions.between(
				MODIFICATION_DATE_FIELD, startDate, now));
		Map results = bs.getBinderModule().executeSearchQuery(crit, Constants.SEARCH_MODE_NORMAL, 0, ObjectKeys.MAX_BINDER_ENTRIES_RESULTS,
				org.kablink.teaming.module.shared.SearchUtils.fieldNamesList(Constants.ENTRY_ANCESTRY,Constants.DOCID_FIELD,Constants.LASTACTIVITY_FIELD,Constants.MODIFICATION_DATE_FIELD));
    	List<Map> entries = (List) results.get(ObjectKeys.SEARCH_ENTRIES);

		//Get the count of unseen entries
		SeenMap seen = bs.getProfileModule().getUserSeenMap(null);
    	for (Map entry:entries) {
    		SearchFieldResult entryAncestors = (SearchFieldResult) entry.get(ENTRY_ANCESTRY);
			if (entryAncestors == null) continue;
			String entryIdString = (String) entry.get(DOCID_FIELD);
			if (entryIdString == null || (seen.checkIfSeen(entry))) continue;
			
			//Count up the unseen counts for all ancestor binders
			Iterator itAncestors = entryAncestors.getValueSet().iterator();
			while (itAncestors.hasNext()) {
				String binderIdString = (String)itAncestors.next();
				if (binderIdString.equals("")) continue;
				Counter cnt = unseenCounts.get(binderIdString);
				if (cnt == null) {
					cnt = new WorkspaceTreeHelper.Counter();
					unseenCounts.put(binderIdString, cnt);
				}
				cnt.increment();
			}
    	}
    	model.put(WebKeys.BINDER_UNSEEN_COUNTS, unseenCounts);
		
      	//Get the info for the "add a team" button
		buildAddTeamButton(bs, req, response, ws, model);
		buildWorkspaceToolbar(bs, req, response, model, ws, ws.getId().toString());
	}
	
	protected static void getShowProjectWorkspace(AllModulesInjected bs, Map formData, 
			RenderRequest req, RenderResponse response, Workspace ws, 
			Document searchFilter, Map<String,Object>model) throws PortletRequestBindingException {
		
    	//Get the sorted list of child binders
		Map options = new HashMap();
		options.put(ObjectKeys.SEARCH_SORT_BY, org.kablink.util.search.Constants.SORT_TITLE_FIELD);
		options.put(ObjectKeys.SEARCH_SORT_DESCEND, new Boolean(false));
		options.put(ObjectKeys.SEARCH_MAX_HITS, ObjectKeys.MAX_BINDER_ENTRIES_RESULTS);
		Map searchResults = bs.getBinderModule().getBinders(ws, options);
		List<Map> binders = (List)searchResults.get(ObjectKeys.SEARCH_ENTRIES);
		model.put(WebKeys.BINDERS, binders); 
		
		//Get the next level of binders below the workspaces in "binders"
		List binderIdList = new ArrayList();
		for (Map binder:binders) {
			String binderIdString = (String) binder.get(DOCID_FIELD);
			if (binderIdString != null) {
				binderIdList.add(Long.valueOf(binderIdString));
			}
		}
		if (!binderIdList.isEmpty()) {
			//Get sub-binder list including intermediate binders that may be inaccessible
			SortedSet<Binder> subBinders = bs.getBinderModule().getBinders(binderIdList, Boolean.FALSE);
			model.put(WebKeys.BINDER_SUB_BINDERS, subBinders);
		}

	}
	
	protected static void buildAddTeamButton(AllModulesInjected bs, 
			RenderRequest req, RenderResponse response, Workspace ws, Map<String,Object>model) {
		//Get the info for the "add a team" button
		if (!ws.isRoot() && bs.getBinderModule().testAccess(ws, BinderOperation.addWorkspace)) {
			Long cfgType = null;
			List result = bs.getTemplateModule().getTemplates(Definition.WORKSPACE_VIEW);
			if (result.isEmpty()) {
				result.add(bs.getTemplateModule().addDefaultTemplate(Definition.WORKSPACE_VIEW));	
			}
			for (int i = 0; i < result.size(); i++) {
				TemplateBinder tb = (TemplateBinder) result.get(i);
				if (tb.getInternalId() != null && tb.getInternalId().toString().equals(ObjectKeys.DEFAULT_TEAM_WORKSPACE_CONFIG)) {
					//We have found the team workspace template, get its config id
					cfgType = tb.getId();
					break;
				}
			}
			if (cfgType != null) {
				PortletURL url = response.createActionURL();
				url.setParameter(WebKeys.ACTION, WebKeys.ACTION_ADD_BINDER);
				url.setParameter(WebKeys.URL_BINDER_ID, ws.getId().toString());
				url.setParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_ADD_TEAM_WORKSPACE);
				url.setParameter(WebKeys.URL_BINDER_CONFIG_ID, cfgType.toString());
				model.put(WebKeys.ADD_TEAM_WORKSPACE_URL, url);
			}
		}
	}
	protected static void getTeamMembers(AllModulesInjected bs, Map formData, 
			RenderRequest req, RenderResponse response, Workspace ws, 
			Map<String,Object>model) throws PortletRequestBindingException {
		try {
			bs.getProfileModule().getProfileBinder(); //Check access to user list
			Collection<Principal> usersAndGroups = bs.getBinderModule().getTeamMembers(ws, false);
			SortedMap<String, User> teamUsers = new TreeMap();
			SortedMap<String, Group> teamGroups = new TreeMap();
			for (Principal p : usersAndGroups) {
				if (p instanceof User) {
					teamUsers.put(Utils.getUserTitle(p), (User)p);
				} else if (p instanceof Group) {
					teamGroups.put(p.getTitle(), (Group)p);
				}
			}
			model.put(WebKeys.TEAM_MEMBERS, teamUsers);
			model.put(WebKeys.TEAM_MEMBERS_COUNT, teamUsers.size());
			model.put(WebKeys.TEAM_MEMBER_GROUPS, teamGroups);
		} catch (AccessControlException ac) {
			logger.debug("WorkspaceTreeHelper.getTeamMembers(AccessControlException):  Ignored");
		}
		
		buildWorkspaceToolbar(bs, req, response, model, ws, ws.getId().toString());
	}
	
	protected static void buildWorkspaceToolbar(AllModulesInjected bs, RenderRequest request, 
			RenderResponse response, Map model, Workspace workspace, 
			String forumId) {
        User user = RequestContextHolder.getRequestContext().getUser();
		//Build the toolbar array
		Toolbar toolbar = new Toolbar();
		Toolbar dashboardToolbar = new Toolbar();
		Toolbar folderActionsToolbar = new Toolbar();
		Toolbar whatsNewToolbar = new Toolbar();
		Toolbar trashToolbar = new Toolbar();
		Toolbar gwtMiscToolbar = new Toolbar();
		Toolbar gwtUIToolbar = new Toolbar();
		Map qualifiers;
		AdaptedPortletURL adapterUrl;

		
		//The "Administration" menu
		boolean adminMenuCreated=false;
		qualifiers = new HashMap();
		qualifiers.put(WebKeys.HELP_SPOT, "helpSpot.manageWorkspaceMenu");
		toolbar.addToolbarMenu("1_administration", NLT.get("toolbar.manageThisWorkspace"), new HashMap(), qualifiers);

		//	The "Add" menu
		PortletURL url;
		//Add Workspace except to top or a user workspace
		if (!workspace.isRoot() && bs.getBinderModule().testAccess(workspace, BinderOperation.addWorkspace)) {
			adminMenuCreated=true;
			qualifiers = new HashMap();
			qualifiers.put("popup", new Boolean(true));
			url = response.createActionURL();
			url.setParameter(WebKeys.ACTION, WebKeys.ACTION_ADD_BINDER);
			url.setParameter(WebKeys.URL_BINDER_ID, forumId);
			url.setParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_ADD_WORKSPACE);
			toolbar.addToolbarMenuItem("1_administration", "addBinder", 
					NLT.get("toolbar.menu.addWorkspace"), url, qualifiers);
		}
		//Add Folder except to top
		if (!workspace.isRoot() && bs.getBinderModule().testAccess(workspace, BinderOperation.addFolder)) {
			adminMenuCreated=true;
			qualifiers = new HashMap();
			qualifiers.put("popup", new Boolean(true));
			url = response.createActionURL();
			url.setParameter(WebKeys.ACTION, WebKeys.ACTION_ADD_BINDER);
			url.setParameter(WebKeys.URL_BINDER_ID, forumId);
			url.setParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_ADD_FOLDER);
			toolbar.addToolbarMenuItem("1_administration", "addBinder", 
					NLT.get("toolbar.menu.addFolder"), url, qualifiers);
		}
	
		//Configuration
		if (bs.getBinderModule().testAccess(workspace, BinderOperation.modifyBinder)) {
			adminMenuCreated=true;
			qualifiers = new HashMap();
			qualifiers.put("popup", new Boolean(true));
			url = response.createRenderURL();
			url.setParameter(WebKeys.ACTION, WebKeys.ACTION_CONFIGURE_DEFINITIONS);
			url.setParameter(WebKeys.URL_BINDER_ID, forumId);
			url.setParameter(WebKeys.URL_BINDER_TYPE, workspace.getEntityType().name());
			toolbar.addToolbarMenuItem("1_administration", "configuration", 
					NLT.get("toolbar.menu.configuration"), url, qualifiers);
			
			//Modify
			
			adminMenuCreated=true;
			qualifiers = new HashMap();
			qualifiers.put("popup", new Boolean(true));
			adapterUrl = new AdaptedPortletURL(request, "ss_forum", true);
			adapterUrl.setParameter(WebKeys.ACTION, WebKeys.ACTION_MODIFY_BINDER);
			adapterUrl.setParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_MODIFY);
			adapterUrl.setParameter(WebKeys.URL_BINDER_ID, forumId);
			adapterUrl.setParameter(WebKeys.URL_BINDER_TYPE, workspace.getEntityType().name());
			toolbar.addToolbarMenuItem("1_administration", "configuration", NLT.get("toolbar.menu.modify_workspace"), 
					adapterUrl.toString(), qualifiers);
		}
		
		if (bs.getBinderModule().testAccess(workspace, BinderOperation.manageConfiguration)) {
			adminMenuCreated=true;
			qualifiers = new HashMap();
			qualifiers.put("popup", new Boolean(true));
			url = response.createRenderURL();
			url.setParameter(WebKeys.ACTION, WebKeys.ACTION_MANAGE_DEFINITIONS);
			url.setParameter(WebKeys.URL_BINDER_ID, forumId);
			toolbar.addToolbarMenuItem("1_administration", "configuration", NLT.get("administration.definition_builder_designers"), url, qualifiers);
		}
		if (bs.getBinderModule().testAccess(workspace, BinderOperation.manageConfiguration)) {
			adminMenuCreated=true;
			qualifiers = new HashMap();
			qualifiers.put("popup", new Boolean(true));
			url = response.createRenderURL();
			url.setParameter(WebKeys.ACTION, WebKeys.ACTION_MANAGE_TEMPLATES);
			url.setParameter(WebKeys.URL_BINDER_PARENT_ID, forumId);
			toolbar.addToolbarMenuItem("1_administration", "configuration", NLT.get("administration.template_builder_local"), url, qualifiers);
		}
		
		//Delete
		if (!workspace.isReserved()) {
			if (bs.getBinderModule().testAccess(workspace, BinderOperation.deleteBinder)) {
				adminMenuCreated=true;
				qualifiers = new HashMap();
				qualifiers.put("popup", new Boolean(true));
				url = response.createActionURL();
				url.setParameter(WebKeys.ACTION, WebKeys.ACTION_MODIFY_BINDER);
				url.setParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_DELETE);
				url.setParameter(WebKeys.URL_BINDER_ID, forumId);
				url.setParameter(WebKeys.URL_BINDER_TYPE, workspace.getEntityType().name());
				toolbar.addToolbarMenuItem("1_administration", "configuration", 
						NLT.get("toolbar.menu.delete_workspace"), url, qualifiers);
			}
		}
		
		//Move
		if (!workspace.isReserved() && (workspace.getDefinitionType() == null || 
				(workspace.getDefinitionType().intValue() != Definition.USER_WORKSPACE_VIEW) &&
				 workspace.getDefinitionType().intValue() != Definition.EXTERNAL_USER_WORKSPACE_VIEW)) {
			if (bs.getBinderModule().testAccess(workspace, BinderOperation.moveBinder)) {
				adminMenuCreated=true;
				qualifiers = new HashMap();
				qualifiers.put("popup", new Boolean(true));
				url = response.createActionURL();
				url.setParameter(WebKeys.ACTION, WebKeys.ACTION_MODIFY_BINDER);
				url.setParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_MOVE);
				url.setParameter(WebKeys.URL_BINDER_ID, forumId);
				url.setParameter(WebKeys.URL_BINDER_TYPE, workspace.getEntityType().name());
				toolbar.addToolbarMenuItem("1_administration", "configuration", 
						NLT.get("toolbar.menu.move_workspace"), url, qualifiers);
			}
			if (bs.getBinderModule().testAccess(workspace, BinderOperation.copyBinder)) {
				adminMenuCreated=true;
				qualifiers = new HashMap();
				qualifiers.put("popup", new Boolean(true));
				url = response.createActionURL();
				url.setParameter(WebKeys.ACTION, WebKeys.ACTION_MODIFY_BINDER);
				url.setParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_COPY);
				url.setParameter(WebKeys.URL_BINDER_ID, forumId);
				url.setParameter(WebKeys.URL_BINDER_TYPE, workspace.getEntityType().name());
				toolbar.addToolbarMenuItem("1_administration", "configuration", NLT.get("toolbar.menu.copy_workspace"), url, qualifiers);
			}

		}
		//Reporting
		if (bs.getBinderModule().testAccess(workspace, BinderOperation.report)) {
			adminMenuCreated=true;
			qualifiers = new HashMap();
			qualifiers.put("popup", new Boolean(true));
			url = response.createRenderURL();
			url.setParameter(WebKeys.ACTION, WebKeys.ACTION_ACTIVITY_REPORT);
			url.setParameter(WebKeys.URL_BINDER_ID, forumId);
			url.setParameter(WebKeys.URL_BINDER_TYPE, workspace.getEntityType().name());
			toolbar.addToolbarMenuItem("1_administration", "reports", 
					NLT.get("toolbar.menu.report"), url, qualifiers);
		}
		
		//Site administration
		if (bs.getAdminModule().testAccess(AdminOperation.manageFunction)) {
			adminMenuCreated=true;
			url = response.createActionURL();
			url.setParameter(WebKeys.ACTION, WebKeys.ACTION_SITE_ADMINISTRATION);
			url.setParameter(WebKeys.URL_BINDER_ID, forumId);
			toolbar.addToolbarMenuItem("1_administration", "siteAdmin", 
					NLT.get("toolbar.menu.siteAdministration"), url);
		}
		
		//Export / Import
		if (bs.getBinderModule().testAccess(workspace, BinderOperation.export)) {
			qualifiers = new HashMap();
			qualifiers.put("popup", new Boolean(true));
			url = response.createRenderURL();
			url.setParameter(WebKeys.ACTION, WebKeys.ACTION_EXPORT_IMPORT);
			url.setParameter(WebKeys.URL_BINDER_ID, forumId);
			url.setParameter(WebKeys.URL_SHOW_MENU, "true");
			toolbar.addToolbarMenuItem("1_administration", "configuration", 
					NLT.get("toolbar.menu.export_import_workspace"), url, qualifiers);
			//adminMenuCreated=true;	
		}
		
		//if no menu items were added, remove the empty menu
		if (!adminMenuCreated) toolbar.deleteToolbarMenu("1_administration");
		
		//Access control
		if (bs.getAdminModule().testAccess(workspace, AdminOperation.manageFunctionMembership)) {
			qualifiers = new HashMap();
			qualifiers.put("popup", new Boolean(true));
			qualifiers.put(WebKeys.HELP_SPOT, "helpSpot.accessControlMenu");
			url = response.createRenderURL();
			url.setParameter(WebKeys.ACTION, WebKeys.ACTION_ACCESS_CONTROL);
			url.setParameter(WebKeys.URL_WORKAREA_ID, workspace.getWorkAreaId().toString());
			url.setParameter(WebKeys.URL_WORKAREA_TYPE, workspace.getWorkAreaType());
			toolbar.addToolbarMenuItem("1_administration", "configuration", 
					NLT.get("toolbar.menu.accessControl"), url, qualifiers);
		}

		//The "Who has access" menu
		if (!ObjectKeys.GUEST_USER_INTERNALID.equals(user.getInternalId())) {
			qualifiers = new HashMap();
			qualifiers.put("title", NLT.get("toolbar.menu.title.whoHasAccessWorkspace"));
			qualifiers.put("popup", Boolean.TRUE);
			qualifiers.put("popupWidth", "600");
			qualifiers.put("popupHeight", "700");
			adminMenuCreated = true;
			adapterUrl = new AdaptedPortletURL(request, "ss_forum", true);
			adapterUrl.setParameter(WebKeys.ACTION, WebKeys.ACTION_ACCESS_CONTROL);
			adapterUrl.setParameter(WebKeys.URL_WORKAREA_ID, workspace.getWorkAreaId().toString());
			adapterUrl.setParameter(WebKeys.URL_WORKAREA_TYPE, workspace.getWorkAreaType());
			adapterUrl.setParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_VIEW_ACCESS);
			toolbar.addToolbarMenu("2_whoHasAccess", 
					NLT.get("toolbar.whoHasAccess"), adapterUrl.toString(), qualifiers);
		}
		
		//If this is a user workspace, add the "Manage this profile" menu
		if ((workspace.getDefinitionType() != null) && 
				((workspace.getDefinitionType().intValue() == Definition.USER_WORKSPACE_VIEW) ||
				 (workspace.getDefinitionType().intValue() == Definition.EXTERNAL_USER_WORKSPACE_VIEW))) {
			Principal owner = workspace.getCreation().getPrincipal(); //creator is user
		
			boolean showModifyProfileMenu = false;
			boolean showDeleteProfileMenu = false;
			boolean showDisableProfileMenu = false;
			boolean showDropdownMenu = false;
			if (owner.isActive() && bs.getProfileModule().testAccess(owner, ProfileOperation.modifyEntry)) {
				showModifyProfileMenu = true;
			}
		
			if ((owner.isActive() || 
					(((owner instanceof User) && 
							((User)owner).getIdentityInfo().isInternal() && ((User)owner).getIdentityInfo().isFromLocal()) 
							&& owner.isDisabled())) && 
					bs.getProfileModule().testAccess(owner, ProfileOperation.deleteEntry)) {
				//Don't let a user delete his or her own account
				if (!owner.getId().equals(user.getId())) {
					showDeleteProfileMenu = true;
					if ((owner instanceof User) && 
							((User)owner).getIdentityInfo().isInternal() && ((User)owner).getIdentityInfo().isFromLocal()) { 
						//showDisableProfileMenu = true;
					}
				}
			}
			if ((showModifyProfileMenu && (showDeleteProfileMenu || showDisableProfileMenu)) || 
					(showDeleteProfileMenu && (showModifyProfileMenu || showDisableProfileMenu)) ||
					(showDisableProfileMenu && (showModifyProfileMenu || showDeleteProfileMenu))) {
				showDropdownMenu = true;
			}
			
	        //Modify profile is not available to the guest user
	        if (!ObjectKeys.GUEST_USER_INTERNALID.equals(user.getInternalId())) {
				if (showDropdownMenu) {
					qualifiers = new HashMap();
					qualifiers.put(WebKeys.HELP_SPOT, "helpSpot.modifyProfileButton");
					toolbar.addToolbarMenu("4_manageProfile", NLT.get("toolbar.manageThisProfile"), new HashMap(), qualifiers);
					//	The "Modify" menu item
					if (showModifyProfileMenu) {
						qualifiers = new HashMap();
						qualifiers.put("onClick", "ss_openUrlInWindow(this, '_blank');return false;");
						adapterUrl = new AdaptedPortletURL(request, "ss_forum", true);
						adapterUrl.setParameter(WebKeys.ACTION, WebKeys.ACTION_MODIFY_PROFILE_ENTRY);
						adapterUrl.setParameter(WebKeys.URL_BINDER_ID, owner.getParentBinder().getId().toString());
						adapterUrl.setParameter(WebKeys.URL_ENTRY_ID, owner.getId().toString());
						toolbar.addToolbarMenuItem("4_manageProfile", "", NLT.get("toolbar.modify"), adapterUrl.toString(), qualifiers);
					}
					//	The "Disable" menu item
					if (showDisableProfileMenu) {
						qualifiers = new HashMap();
						qualifiers.put("onClick", "ss_openUrlInWindow(this, '_blank');return false;");
						url = response.createActionURL();
						url.setParameter(WebKeys.ACTION, WebKeys.ACTION_MODIFY_PROFILE_ENTRY);
						String menuText;
						if (owner.isDisabled()) {
							url.setParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_ENABLE);
							menuText = NLT.get("toolbar.enable");
						} else {
							url.setParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_DISABLE);
							menuText = NLT.get("toolbar.disable");
						}
						url.setParameter(WebKeys.URL_BINDER_ID, owner.getParentBinder().getId().toString());
						url.setParameter(WebKeys.URL_ENTRY_ID, owner.getId().toString());
						toolbar.addToolbarMenuItem("4_manageProfile", "", menuText, url, qualifiers);
					}
					//	The "Delete" menu item
					if (showDeleteProfileMenu) {
						qualifiers = new HashMap();
						qualifiers.put("onClick", "ss_openUrlInWindow(this, '_blank');return false;");
						url = response.createActionURL();
						url.setParameter(WebKeys.ACTION, WebKeys.ACTION_MODIFY_PROFILE_ENTRY);
						url.setParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_DELETE);
						url.setParameter(WebKeys.URL_BINDER_ID, owner.getParentBinder().getId().toString());
						url.setParameter(WebKeys.URL_ENTRY_ID, owner.getId().toString());
						toolbar.addToolbarMenuItem("4_manageProfile", "", NLT.get("toolbar.delete"), url, qualifiers);
					}
				} else {
					//	The "Modify" menu item
					if (showModifyProfileMenu) {
						qualifiers = new HashMap();
						qualifiers.put(WebKeys.HELP_SPOT, "helpSpot.modifyProfileButton");
						qualifiers.put("onClick", "ss_openUrlInWindow(this, '_blank');return false;");
						adapterUrl = new AdaptedPortletURL(request, "ss_forum", true);
						adapterUrl.setParameter(WebKeys.ACTION, WebKeys.ACTION_MODIFY_PROFILE_ENTRY);
						adapterUrl.setParameter(WebKeys.URL_BINDER_ID, owner.getParentBinder().getId().toString());
						adapterUrl.setParameter(WebKeys.URL_ENTRY_ID, owner.getId().toString());
						toolbar.addToolbarMenu("4_manageProfile", NLT.get("toolbar.menu.modify_profile"), adapterUrl.toString(), qualifiers);
					}
					//	The "disable" menu item
					if (showDisableProfileMenu) {
						qualifiers = new HashMap();
						qualifiers.put(WebKeys.HELP_SPOT, "helpSpot.modifyProfileButton");
						qualifiers.put("onClick", "ss_openUrlInWindow(this, '_blank');return false;");
						url = response.createActionURL();
						url.setParameter(WebKeys.ACTION, WebKeys.ACTION_MODIFY_PROFILE_ENTRY);
						if (owner.isDisabled()) {
							url.setParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_ENABLE);
						} else {
							url.setParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_DISABLE);
						}
						url.setParameter(WebKeys.URL_BINDER_ID, owner.getParentBinder().getId().toString());
						url.setParameter(WebKeys.URL_ENTRY_ID, owner.getId().toString());
						toolbar.addToolbarMenu("4_manageProfile", NLT.get("toolbar.disable"), url, qualifiers);
					}
					//	The "delete" menu item
					if (showDeleteProfileMenu) {
						qualifiers = new HashMap();
						qualifiers.put(WebKeys.HELP_SPOT, "helpSpot.modifyProfileButton");
						qualifiers.put("onClick", "ss_openUrlInWindow(this, '_blank');return false;");
						url = response.createActionURL();
						url.setParameter(WebKeys.ACTION, WebKeys.ACTION_MODIFY_PROFILE_ENTRY);
						url.setParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_DELETE);
						url.setParameter(WebKeys.URL_BINDER_ID, owner.getParentBinder().getId().toString());
						url.setParameter(WebKeys.URL_ENTRY_ID, owner.getId().toString());
						toolbar.addToolbarMenu("4_manageProfile", NLT.get("toolbar.delete"), url, qualifiers);
					}
				}
	        }
		}
		
		// list team members
		qualifiers = new HashMap();
					
		// The "Teams" menu
		//toolbar.addToolbarMenu("5_team", NLT.get("toolbar.teams"));
			
		//Add
		if (bs.getBinderModule().testAccess(workspace, BinderOperation.manageTeamMembers)) {
			adapterUrl = new AdaptedPortletURL(request, "ss_forum", true);
			adapterUrl.setParameter(WebKeys.ACTION, WebKeys.ACTION_ADD_TEAM_MEMBER);
			adapterUrl.setParameter(WebKeys.URL_BINDER_ID, forumId);
			adapterUrl.setParameter(WebKeys.URL_BINDER_TYPE, workspace.getEntityType().name());
			qualifiers = new HashMap();
			qualifiers.put("popup", Boolean.TRUE);
			qualifiers.put("popupWidth", "500");
			qualifiers.put("popupHeight", "600");
			//toolbar.addToolbarMenuItem("5_team", "", NLT.get("toolbar.teams.addMember"), adapterUrl.toString(), qualifiers);
			model.put(WebKeys.TOOLBAR_TEAM_ADD_URL, adapterUrl.toString());
		}
		// View
		if (!ObjectKeys.GUEST_USER_INTERNALID.equals(user.getInternalId())) {
			url = response.createRenderURL();
			url.setParameter(WebKeys.ACTION, WebKeys.ACTION_VIEW_WS_LISTING);
			url.setParameter(WebKeys.URL_BINDER_ID, forumId);
			url.setParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_SHOW_TEAM_MEMBERS);
			url.setParameter(WebKeys.URL_BINDER_TYPE, workspace.getEntityType().name());
			//toolbar.addToolbarMenuItem("5_team", "", NLT.get("toolbar.teams.view"), url);
			model.put(WebKeys.TOOLBAR_TEAM_VIEW_URL, url.toString());
		}
			
		// Sendmail
		if (Validator.isNotNull(user.getEmailAddress()) && 
				!ObjectKeys.GUEST_USER_INTERNALID.equals(user.getInternalId())) {
			adapterUrl = new AdaptedPortletURL(request, "ss_forum", true);
			adapterUrl.setParameter(WebKeys.ACTION, WebKeys.ACTION_SEND_EMAIL);
			adapterUrl.setParameter(WebKeys.URL_BINDER_ID, forumId);
			adapterUrl.setParameter(WebKeys.URL_APPEND_TEAM_MEMBERS, Boolean.TRUE.toString());
			qualifiers = new HashMap();
			qualifiers.put("popup", Boolean.TRUE);
			//toolbar.addToolbarMenuItem("5_team", "", NLT.get("toolbar.teams.sendmail"), adapterUrl.toString(), qualifiers);
			model.put(WebKeys.TOOLBAR_TEAM_SENDMAIL_URL, adapterUrl.toString());
		}
		
		// Meet
		if (bs.getConferencingModule().isEnabled() && !ObjectKeys.GUEST_USER_INTERNALID.equals(user.getInternalId())) {
			adapterUrl = new AdaptedPortletURL(request, "ss_forum", true);
			adapterUrl.setParameter(WebKeys.ACTION, WebKeys.ACTION_ADD_MEETING);
			adapterUrl.setParameter(WebKeys.URL_BINDER_ID, forumId);
			adapterUrl.setParameter(WebKeys.URL_APPEND_TEAM_MEMBERS, Boolean.TRUE.toString());
			qualifiers = new HashMap();
			qualifiers.put("popup", Boolean.TRUE);
			//toolbar.addToolbarMenuItem("5_team", "", NLT.get("toolbar.teams.meet"), adapterUrl.toString(), qualifiers);
			model.put(WebKeys.TOOLBAR_TEAM_MEET_URL, adapterUrl.toString());
		}

		//	The "Manage dashboard" menu
		BinderHelper.buildDashboardToolbar(request, response, bs, workspace, dashboardToolbar, model);

		//The "Footer" menu
		//RSS link 
		Toolbar footerToolbar = new Toolbar();
		String[] contributorIds = collectContributorIds(workspace);
		
		// permalink
		String permaLink = PermaLinkUtil.getPermalink(request, workspace);
		qualifiers = new HashMap();
		qualifiers.put("onClick", "ss_showPermalink(this);return false;");
		footerToolbar.addToolbarMenu("permalink", NLT.get("toolbar.menu.workspacePermalink"), 
				permaLink, qualifiers);
		
		model.put(WebKeys.PERMALINK, permaLink);
		model.put(WebKeys.MOBILE_URL, SsfsUtil.getMobileUrl(request));		
		
		//  Build the simple URL beans
		BinderHelper.buildSimpleUrlBeans(bs,  request, workspace, model);

		// clipboard
		if (!ObjectKeys.GUEST_USER_INTERNALID.equals(user.getInternalId())) {
			qualifiers = new HashMap();
			String contributorIdsAsJSString = "";
			for (int i = 0; i < contributorIds.length; i++) {
				contributorIdsAsJSString += contributorIds[i];
				if (i < (contributorIds.length -1)) {
					contributorIdsAsJSString += ", ";	
				}
			}
			qualifiers.put("onClick", "ss_muster.showForm('" + Clipboard.USERS + "', [" + contributorIdsAsJSString + "], '" + forumId + "');return false;");
			//footerToolbar.addToolbarMenu("clipboard", NLT.get("toolbar.menu.clipboard"), "", qualifiers);
			model.put(WebKeys.TOOLBAR_CLIPBOARD_IDS, contributorIds);
			model.put(WebKeys.TOOLBAR_CLIPBOARD_IDS_AS_JS_STRING, contributorIdsAsJSString);
			model.put(WebKeys.TOOLBAR_CLIPBOARD_SHOW, Boolean.TRUE);
		}
		
		// send mail
		if (user.getEmailAddress() != null && !user.getEmailAddress().equals("") && 
				!ObjectKeys.GUEST_USER_INTERNALID.equals(user.getInternalId())) {
			adapterUrl = new AdaptedPortletURL(request, "ss_forum", true);
			adapterUrl.setParameter(WebKeys.ACTION, WebKeys.ACTION_SEND_EMAIL);
			adapterUrl.setParameter(WebKeys.URL_BINDER_ID, forumId);
			adapterUrl.setParameter(WebKeys.URL_APPEND_TEAM_MEMBERS, Boolean.TRUE.toString());
			qualifiers = new HashMap();
			qualifiers.put("popup", Boolean.TRUE);
			//footerToolbar.addToolbarMenu("sendMail", NLT.get("toolbar.menu.sendMail"), adapterUrl.toString(), qualifiers);
			model.put(WebKeys.TOOLBAR_SENDMAIL_URL, adapterUrl.toString());
		}

		// trash
		TrashHelper.buildTrashToolbar(user, workspace, model, qualifiers, trashToolbar);

		// start meeting
		if (bs.getConferencingModule().isEnabled() && !ObjectKeys.GUEST_USER_INTERNALID.equals(user.getInternalId())) {
			adapterUrl = new AdaptedPortletURL(request, "ss_forum", true);
			adapterUrl.setParameter(WebKeys.ACTION, WebKeys.ACTION_ADD_MEETING);
			adapterUrl.setParameter(WebKeys.URL_BINDER_ID, forumId);
			adapterUrl.setParameter(WebKeys.URL_APPEND_TEAM_MEMBERS, Boolean.TRUE.toString());
			qualifiers = new HashMap();
			qualifiers.put("popup", Boolean.TRUE);
			//footerToolbar.addToolbarMenu("addMeeting", NLT.get("toolbar.menu.addMeeting"), adapterUrl.toString(), qualifiers);
			model.put(WebKeys.TOOLBAR_MEETING_URL, adapterUrl.toString());
		}

		//Mobile UI
		HttpServletRequest req = WebHelper.getHttpServletRequest(request);
		String userAgents = org.kablink.teaming.util.SPropsUtil.getString("mobile.userAgents", "");
		String tabletUserAgents = org.kablink.teaming.util.SPropsUtil.getString("tablet.userAgentRegexp", "");
		Boolean testForAndroid = org.kablink.teaming.util.SPropsUtil.getBoolean("tablet.useDefaultTestForAndroidTablets", false);
		if (BrowserSniffer.is_mobile(req, userAgents) && !BrowserSniffer.is_tablet(req, tabletUserAgents, testForAndroid)) {
			//The "Mobile UI" menu
			qualifiers = new HashMap();
			qualifiers.put("nosort", true);
			qualifiers.put("onClick", "window.open(this.href,'_top');return false;");
			url = response.createActionURL();
			url.setParameter(WebKeys.ACTION, WebKeys.ACTION_MOBILE_AJAX);
			url.setParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_MOBILE_SHOW_MOBILE_UI);
			//toolbar.addToolbarMenuItem("4_actions", "actions", NLT.get("toolbar.showMobileUI"), url, qualifiers);
			model.put(WebKeys.TOOLBAR_MOBILE_UI_URL, url.toString());
		}

		//Set up the whatsNewToolbar links
		//What's new
        //What's new is not available to the guest user
        if (!ObjectKeys.GUEST_USER_INTERNALID.equals(user.getInternalId())) {
			adapterUrl = new AdaptedPortletURL(request, "ss_forum", true);
			adapterUrl.setParameter(WebKeys.ACTION, WebKeys.ACTION_VIEW_WS_LISTING);
			adapterUrl.setParameter(WebKeys.URL_BINDER_ID, forumId);
			adapterUrl.setParameter(WebKeys.URL_TYPE, "whatsNew");
			adapterUrl.setParameter(WebKeys.URL_PAGE, "0");
			adapterUrl.setParameter(WebKeys.URL_NAMESPACE, response.getNamespace());
			qualifiers = new HashMap();
			qualifiers.put("title", NLT.get("toolbar.menu.title.whatsNewInWorkspace"));
			qualifiers.put("onClick", "ss_showWhatsNewPage(this, '"+forumId+"', 'whatsNew', '0', '', 'ss_whatsNewDiv', '"+response.getNamespace()+"');return false;");
			whatsNewToolbar.addToolbarMenu("whatsnew", NLT.get("toolbar.menu.whatsNew"), 
					adapterUrl.toString(), qualifiers);
			
			// What's unseen
			adapterUrl = new AdaptedPortletURL(request, "ss_forum", true);
			adapterUrl.setParameter(WebKeys.ACTION, WebKeys.ACTION_VIEW_WS_LISTING);
			adapterUrl.setParameter(WebKeys.URL_BINDER_ID, forumId);
			adapterUrl.setParameter(WebKeys.URL_TYPE, "unseen");
			adapterUrl.setParameter(WebKeys.URL_PAGE, "0");
			adapterUrl.setParameter(WebKeys.URL_NAMESPACE, response.getNamespace());
			qualifiers = new HashMap();
			qualifiers.put("title", NLT.get("toolbar.menu.title.whatsUnreadInWorkspace"));
			qualifiers.put("onClick", "ss_showWhatsNewPage(this, '"+forumId+"', 'unseen', '0', '', 'ss_whatsNewDiv', '"+response.getNamespace()+"');return false;");
			whatsNewToolbar.addToolbarMenu("unseen", NLT.get("toolbar.menu.whatsUnseen"), 
					adapterUrl.toString(), qualifiers);
        }

		//Build the folder actions toolbar
		BinderHelper.buildFolderActionsToolbar(bs, request, response, folderActionsToolbar, forumId);

		// GWT UI.  Note that these need to be last in the toolbar
		// building sequence because they access things in the
		// model to construct toolbars specific to the GWT UI.
		GwtUIHelper.buildGwtMiscToolbar(bs, request, workspace, model, gwtMiscToolbar);

		model.put(WebKeys.FOOTER_TOOLBAR,  footerToolbar.getToolbar());
		model.put(WebKeys.FOLDER_TOOLBAR, toolbar.getToolbar());
		model.put(WebKeys.DASHBOARD_TOOLBAR, dashboardToolbar.getToolbar());
		model.put(WebKeys.WHATS_NEW_TOOLBAR,  whatsNewToolbar.getToolbar());
		model.put(WebKeys.FOLDER_ACTIONS_TOOLBAR,  folderActionsToolbar.getToolbar());
		model.put(WebKeys.TRASH_TOOLBAR,  trashToolbar.getToolbar());
		model.put(WebKeys.GWT_MISC_TOOLBAR,  gwtMiscToolbar.getToolbar());
		model.put(WebKeys.GWT_UI_TOOLBAR,  gwtUIToolbar.getToolbar());
	}
	
	public static String[] collectContributorIds(Workspace workspace) {
		Set principals = new HashSet();
		principals.add(workspace.getCreation().getPrincipal().getId().toString());
		principals.add(workspace.getOwner().getId().toString());
		principals.add(workspace.getModification().getPrincipal().getId().toString());
		String[] as = new String[principals.size()];
		principals.toArray(as);
		return as;
	}
	
	private static String redirectToProfileListing(RenderResponse response, String binderIdString, String entryIdString, Map model) {
		PortletURL reloadUrl = response.createRenderURL();
		reloadUrl.setParameter(WebKeys.URL_BINDER_ID, binderIdString);
		reloadUrl.setParameter(WebKeys.ACTION, WebKeys.ACTION_VIEW_PROFILE_LISTING);
		reloadUrl.setParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_VIEW_ENTRY);
		reloadUrl.setParameter(WebKeys.URL_ENTRY_ID, entryIdString);
		model.put(WebKeys.RELOAD_URL_FORCED, reloadUrl.toString());
		return WebKeys.VIEW_WORKSPACE;
	}
	
	protected static void getShowModifyProfileAdapter(AllModulesInjected bs, RenderRequest request, 
			RenderResponse response, Map model, Workspace workspace){
		@SuppressWarnings("unused")
		User user = RequestContextHolder.getRequestContext().getUser();
	}

    /**
     * Helper class to return folder unseen counts as an objects
     * 
     * @author Janet McCann
     */
     public static class Counter {
    	private long count=0;
    	public Counter() {	
    	}
    	public void increment() {
    		++count;
    	}
    	public Long getCount() {
    		return count;
    	}    	
    }
}
