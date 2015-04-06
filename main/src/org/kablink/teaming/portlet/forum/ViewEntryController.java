/**
 * Copyright (c) 1998-2014 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2014 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2014 Novell, Inc. All Rights Reserved.
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
package org.kablink.teaming.portlet.forum;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletRequest;
import javax.portlet.PortletURL;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.WindowState;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.dom4j.Document;
import org.dom4j.Element;
import org.kablink.teaming.web.WebKeys;
import org.kablink.teaming.web.util.BinderHelper;
import org.springframework.web.portlet.ModelAndView;

import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.domain.AuditType;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.Definition;
import org.kablink.teaming.domain.FileAttachment;
import org.kablink.teaming.domain.Folder;
import org.kablink.teaming.domain.FolderEntry;
import org.kablink.teaming.domain.HistoryStamp;
import org.kablink.teaming.domain.NoBinderByTheIdException;
import org.kablink.teaming.domain.NoDefinitionByTheIdException;
import org.kablink.teaming.domain.NoFolderEntryByTheIdException;
import org.kablink.teaming.domain.Principal;
import org.kablink.teaming.domain.SeenMap;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.domain.UserProperties;
import org.kablink.teaming.domain.WorkflowState;
import org.kablink.teaming.domain.EntityIdentifier.EntityType;
import org.kablink.teaming.ical.util.UrlUtil;
import org.kablink.teaming.module.binder.BinderModule.BinderOperation;
import org.kablink.teaming.module.definition.DefinitionUtils;
import org.kablink.teaming.module.folder.FolderModule.FolderOperation;
import org.kablink.teaming.module.shared.MapInputData;
import org.kablink.teaming.portletadapter.AdaptedPortletURL;
import org.kablink.teaming.portletadapter.portlet.PortletResponseImpl;
import org.kablink.teaming.portletadapter.support.PortletAdapterUtil;
import org.kablink.teaming.security.AccessControlException;
import org.kablink.teaming.security.function.OperationAccessControlExceptionNoName;
import org.kablink.teaming.ssfs.util.SsfsUtil;
import org.kablink.teaming.util.AllModulesInjected;
import org.kablink.teaming.util.NLT;
import org.kablink.teaming.util.SPropsUtil;
import org.kablink.teaming.util.TagUtil;
import org.kablink.teaming.web.portlet.SAbstractController;
import org.kablink.teaming.web.util.DefinitionHelper;
import org.kablink.teaming.web.util.ListFolderHelper;
import org.kablink.teaming.web.util.MiscUtil;
import org.kablink.teaming.web.util.PermaLinkUtil;
import org.kablink.teaming.web.util.PortletRequestUtils;
import org.kablink.teaming.web.util.Tabs;
import org.kablink.teaming.web.util.Toolbar;
import org.kablink.teaming.web.util.WebHelper;
import org.kablink.teaming.web.util.WebUrlUtil;
import org.kablink.util.BrowserSniffer;
import org.kablink.util.Validator;

/**
 * ?
 * 
 * @author ?
 */
@SuppressWarnings({"unchecked", "unused"})
public class ViewEntryController extends  SAbstractController {
	@Override
	public void handleActionRequestAfterValidation(ActionRequest request, ActionResponse response) throws Exception {
		response.setRenderParameters(request.getParameterMap());
		try {
			response.setWindowState(request.getWindowState());
		} catch(Exception e) {
			logger.debug("ViewEntryController.handleActionRequestAfterValidation(Exception:  '" + MiscUtil.exToString(e) + "'): 1:  Ignored");
		};
		Map formData = request.getParameterMap();
		Long folderId = new Long(PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_BINDER_ID));				
		Long entryId = PortletRequestUtils.getLongParameter(request, WebKeys.URL_ENTRY_ID);				
		String op = PortletRequestUtils.getStringParameter(request, WebKeys.URL_OPERATION, "");
		
		if (entryId != null) {
			//See if the user asked to change state
			if (formData.containsKey("changeStateBtn") && WebHelper.isMethodPost(request)) {
				//Change the state
				//Get the workflow process to change and the name of the new state
				Long replyId = new Long(PortletRequestUtils.getLongParameter(request, "replyId"));
				if (replyId == null) replyId = entryId;
		        Long tokenId = new Long(PortletRequestUtils.getRequiredLongParameter(request, "tokenId"));	
				String toState = PortletRequestUtils.getStringParameter(request, "toState", "");
				if (!toState.equals("")) {
					//Check if this user is allowed to do this manual transition
					if (getFolderModule().checkIfManualTransitionAllowed(folderId, replyId, tokenId, toState)) {
						getFolderModule().modifyWorkflowState(folderId, replyId, tokenId, toState);
					}
				}
				response.setRenderParameter(WebKeys.IS_REFRESH, "1");
			} else if (formData.containsKey("changeRatingBtn") && WebHelper.isMethodPost(request)) {
				Long replyId = new Long(PortletRequestUtils.getLongParameter(request, "replyId"));
				if (replyId == null) replyId = entryId;
				long rating = PortletRequestUtils.getRequiredLongParameter(request, "rating");
				getFolderModule().setUserRating(folderId, replyId, rating);
				response.setRenderParameter(WebKeys.IS_REFRESH, "1");
			} else if (formData.containsKey("changeTags") && WebHelper.isMethodPost(request)) {
				boolean community = true;
				Long replyId = new Long(PortletRequestUtils.getLongParameter(request, "replyId"));
				if (replyId == null) replyId = entryId;
				String tag = PortletRequestUtils.getRequiredStringParameter(request, "tag");
				String scope = PortletRequestUtils.getRequiredStringParameter(request,"scope");
				if (scope.equalsIgnoreCase("Personal")) community = false;
				getFolderModule().setTag(folderId, replyId, tag, community);
				response.setRenderParameter(WebKeys.IS_REFRESH, "1");
			} else if (formData.containsKey("respondBtn") && WebHelper.isMethodPost(request)) {
				Long replyId = new Long(PortletRequestUtils.getLongParameter(request, "replyId"));
				if (replyId == null) replyId = entryId;
		        Long tokenId = new Long(PortletRequestUtils.getRequiredLongParameter(request, "tokenId"));	
		        getFolderModule().setWorkflowResponse(folderId, replyId, tokenId, new MapInputData(formData));
		        //force reload of listing for state change
				response.setRenderParameter(WebKeys.IS_REFRESH, "1");
			} else if (op.equals(WebKeys.OPERATION_SET_WIKI_HOMEPAGE)) {
				Binder binder = getBinderModule().getBinder(folderId);
				//Check the access rights of the user
				if (getBinderModule().testAccess(binder, BinderOperation.setProperty)) {
					getBinderModule().setProperty(folderId, ObjectKeys.BINDER_PROPERTY_WIKI_HOMEPAGE, entryId.toString());
					response.setRenderParameter(WebKeys.URL_BINDER_ID, folderId.toString());		
					response.setRenderParameter(WebKeys.ACTION, WebKeys.ACTION_VIEW_FOLDER_LISTING);
					response.setRenderParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_RELOAD_LISTING);
				}
			} else if (op.equals(WebKeys.OPERATION_CLEAR_WIKI_HOMEPAGE)) {
				Binder binder = getBinderModule().getBinder(folderId);
				//Check the access rights of the user
				if (getBinderModule().testAccess(binder, BinderOperation.setProperty)) {
					getBinderModule().setProperty(folderId, ObjectKeys.BINDER_PROPERTY_WIKI_HOMEPAGE, "");
					response.setRenderParameter(WebKeys.URL_BINDER_ID, folderId.toString());		
					response.setRenderParameter(WebKeys.ACTION, WebKeys.ACTION_VIEW_FOLDER_LISTING);
					response.setRenderParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_RELOAD_LISTING);
				}
			} else if (op.equals(WebKeys.OPERATION_VOTE_SURVEY_REMOVE_ALL)) {
				getFolderModule().deleteAllVotes(folderId, entryId);
				
			} else if (op.equals(WebKeys.OPERATION_FORCE_UNLOCK_FILE)) {
				Binder binder = getBinderModule().getBinder(folderId);
				FolderEntry entry = getFolderModule().getEntry(folderId, entryId);
				String fileId = PortletRequestUtils.getRequiredStringParameter(request, WebKeys.URL_FILE_ID);
				FileAttachment fa = (FileAttachment)entry.getAttachment(fileId);
				if (getBinderModule().testAccess(binder, BinderOperation.deleteBinder)) {
					getFileModule().forceUnlock(binder, entry, fa);
				}
			} 
		} else if (op.equals(WebKeys.OPERATION_GO_TO_ENTRY)) {
			String entryNumber = PortletRequestUtils.getStringParameter(request, "ssGoToEntry", "");
			if (!entryNumber.equals("")) {
				try {
					FolderEntry entry = getFolderModule().getEntry(folderId, entryNumber);
					response.setRenderParameter(WebKeys.URL_BINDER_ID, folderId.toString());		
					response.setRenderParameter(WebKeys.URL_ENTRY_ID, entry.getId().toString());
				} catch(Exception e) {
					logger.debug("ViewEntryController.handleActionRequestAfterValidation(Exception:  '" + MiscUtil.exToString(e) + "'): 2:  Ignored");
					response.setRenderParameter(WebKeys.URL_BINDER_ID, folderId.toString());		
					response.setRenderParameter(WebKeys.ACTION, WebKeys.ACTION_VIEW_FOLDER_LISTING);
				}
			} else {
				response.setRenderParameter(WebKeys.URL_BINDER_ID, folderId.toString());		
				response.setRenderParameter(WebKeys.ACTION, WebKeys.ACTION_VIEW_FOLDER_LISTING);
			}
		}
	}
	@Override
	public ModelAndView handleRenderRequestAfterValidation(RenderRequest request, 
			RenderResponse response) throws Exception {
		User user = RequestContextHolder.getRequestContext().getUser();
		String displayStyle = user.getDisplayStyle();
		if (request.getWindowState().equals(WindowState.NORMAL)) 
			return prepBeans(request, BinderHelper.CommonPortletDispatch(this, request, response));
		
		Map<String,Object> model = new HashMap();
		
		Long folderId = new Long(PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_BINDER_ID));				
		String entryId = PortletRequestUtils.getStringParameter(request, WebKeys.URL_ENTRY_ID, "");
		String zoneUUID = PortletRequestUtils.getStringParameter(request, WebKeys.URL_ZONE_UUID, "");
		String caTab = PortletRequestUtils.getStringParameter(request, WebKeys.ENTRY_ATTACHMENT_TAB_TO_VIEW, "");
		model.put(WebKeys.ENTRY_ATTACHMENT_TAB_TO_VIEW, caTab);
		folderId = getBinderModule().getZoneBinderId(folderId, zoneUUID, EntityType.folder.name());
		if (folderId == null) {
			String refererUrl = (String)request.getAttribute(WebKeys.REFERER_URL);
			model.put(WebKeys.REFERER_URL, refererUrl);
			model.put(WebKeys.ERROR_MESSAGE, NLT.get("errorcode.entry.not.imported"));
			return prepBeans(request, new ModelAndView(WebKeys.VIEW_ERROR_RETURN, model));
		}
		if (!entryId.equals("")) {
			//See if this id needs to be corrected
			Long targetEntryId = getFolderModule().getZoneEntryId(Long.valueOf(entryId), zoneUUID);
			if (targetEntryId == null) {
				model.put(WebKeys.ERROR_MESSAGE, NLT.get("errorcode.entry.not.imported"));
				return prepBeans(request, new ModelAndView(WebKeys.VIEW_ERROR_RETURN, model));
			}
			entryId = targetEntryId.toString();
		}
		String entryViewType = PortletRequestUtils.getStringParameter(request, WebKeys.URL_ENTRY_VIEW_TYPE, "entryView");
		String entryViewStyle = PortletRequestUtils.getStringParameter(request, WebKeys.URL_ENTRY_VIEW_STYLE, "");
		String entryViewStyle2 = PortletRequestUtils.getStringParameter(request, WebKeys.URL_ENTRY_VIEW_STYLE2, "");
		String displayType = BinderHelper.getDisplayType(request);
		if (entryViewStyle.equals("")) {
			if (ObjectKeys.USER_DISPLAY_STYLE_NEWPAGE.equals(displayStyle) &&
					!ViewController.WIKI_PORTLET.equals(displayType)) entryViewStyle = WebKeys.URL_ENTRY_VIEW_STYLE_FULL;
		} else if (WebKeys.URL_ENTRY_VIEW_STYLE_FULL_CHECK.equals(entryViewStyle)) {
			if (!ViewController.WIKI_PORTLET.equals(displayType)) {
				entryViewStyle = WebKeys.URL_ENTRY_VIEW_STYLE_FULL;
			}
		}
		Map formData = request.getParameterMap();
		Map userProperties = getProfileModule().getUserProperties(null).getProperties();
		
		//Let the jsp know what style to show the entry in 
		//  (popup has no navbar header, inline has no navbar and no script tags, full has a navbar header)
		model.put(WebKeys.ENTRY_VIEW_STYLE, entryViewStyle);
		model.put(WebKeys.ENTRY_VIEW_STYLE2, entryViewStyle2);
		
		String operation = PortletRequestUtils.getStringParameter(request, WebKeys.URL_OPERATION, "");
		String operation2 = PortletRequestUtils.getStringParameter(request, WebKeys.URL_OPERATION2, "");
		if (!operation.equals("")) {
			model.put(WebKeys.URL_OPERATION, operation); 
		}

		try {
			Folder entryFolder = getFolderModule().getFolder(folderId);
			if (operation2.equals(WebKeys.OPERATION_VIEW_NEXT)) {
				Long nextEntryId = BinderHelper.getNextPrevEntry(this, entryFolder, Long.valueOf(entryId), true);
				if (nextEntryId != null) {
					entryId = nextEntryId.toString();
				} else {
					entryId = "";
					operation = WebKeys.OPERATION_SHOW_NO_MORE_ENTRIES;
				}
			}
			if (operation2.equals(WebKeys.OPERATION_VIEW_PREVIOUS)) {
				Long prevEntryId = BinderHelper.getNextPrevEntry(this, entryFolder, Long.valueOf(entryId), false);
				if (prevEntryId != null) {
					entryId = prevEntryId.toString();
				} else {
					entryId = "";
					operation = WebKeys.OPERATION_SHOW_NO_MORE_ENTRIES;
				}
			}
		} catch(Exception e) {
			logger.debug("ViewEntryController.handleRenderRequestAfterValidation(Exception:  '" + MiscUtil.exToString(e) + "'):  1:  Ignored");
		}

		//Set up the standard beans
		BinderHelper.setupStandardBeans(this, request, response, model, folderId);
		UserProperties userFolderProperties = (UserProperties)model.get(WebKeys.USER_FOLDER_PROPERTIES_OBJ);

		model.put(WebKeys.ACTION, WebKeys.ACTION_VIEW_FOLDER_ENTRY);
		
 		//BinderHelper.getViewType requires read access to the binder.  
 		//This causes access errors when have access to an entry but not the binder which happens
 		//when you are following an email permalink link
 		//setup default value for reload case = viewType shouldn't matter
 		String viewPath=WebKeys.VIEW_LISTING_IFRAME;
 		model.put(WebKeys.TABS, Tabs.getTabs(request));
 		FolderEntry fe = null;
		try {
			if (Validator.isNull(entryId)) {
				if (operation.equals(WebKeys.OPERATION_SHOW_NO_ENTRIES) || 
						operation.equals(WebKeys.OPERATION_SHOW_NO_MORE_ENTRIES)) {
					String  binderParam = PortletRequestUtils.getStringParameter(request, WebKeys.URL_BINDER_ENTRY_DEFS, "");
					if (!binderParam.equals("")) {
						model.put(WebKeys.URL_BINDER_ENTRY_DEFS, binderParam);
					}
					binderParam = PortletRequestUtils.getStringParameter(request, WebKeys.URL_BINDER_ENTRY_ADD, "");
					if (!binderParam.equals("")) {
						model.put(WebKeys.URL_BINDER_ENTRY_ADD, binderParam);
					}
					if (operation.equals(WebKeys.OPERATION_SHOW_NO_MORE_ENTRIES)) {
						return prepBeans(request, new ModelAndView(WebKeys.VIEW_NO_MORE_ENTRIES, model));
					}
					return prepBeans(request, new ModelAndView(WebKeys.VIEW_NO_ENTRIES, model));
				}
				
				entryId = PortletRequestUtils.getStringParameter(request, WebKeys.URL_ENTRY_TITLE, "");
				if (!entryId.equals("")) {
					model.put(WebKeys.ENTRY_TITLE, PortletRequestUtils.getStringParameter(request, WebKeys.URL_ENTRY_PAGE_TITLE, "", false));
					Set entries = getFolderModule().getFolderEntryByNormalizedTitle(folderId, entryId, zoneUUID);
					if (entries.size() == 1) {
						FolderEntry entry = (FolderEntry)entries.iterator().next();
						entryId = entry.getId().toString();
						fe = getShowEntry(entryId, formData, request, response, folderId, model, entryViewType);
					} else if (entries.size() == 0) {
						//There are no entries by this title
						Folder folder = null;
						try {
							folder = getFolderModule().getFolder(folderId);
						} catch(NoBinderByTheIdException e) {
							model.put(WebKeys.ERROR_MESSAGE, NLT.get("errorcode.no.folder.by.the.id", new String[] {folderId.toString()}));
							return prepBeans(request, new ModelAndView(WebKeys.VIEW_ERROR_RETURN, model));
						}
						buildNoEntryBeans(request, response, folder, entryId, model);
						return prepBeans(request, new ModelAndView(WebKeys.VIEW_NO_TITLE_ENTRY, model));
					} else {
						//There are multiple matches
						model.put(WebKeys.FOLDER_ENTRIES, entries);
						return prepBeans(request, new ModelAndView(WebKeys.VIEW_MULTIPLE_TITLE_ENTRIES, model));
					}
				} else {
					return prepBeans(request, new ModelAndView(WebKeys.VIEW_NO_MORE_ENTRIES, model));
				}
			} else {
				try {
					fe = getShowEntry(entryId, formData, request, response, folderId, model, entryViewType);
				} catch (NoFolderEntryByTheIdException nf) {
					Folder newFolder = getFolderModule().locateEntry(Long.valueOf(entryId));
					if (newFolder == null) {
						//entry doesn't exist; see if parent folder exists
						Binder folder = null;
						try {
							folder = getBinderModule().getBinder(folderId);
							BinderHelper.buildWorkspaceTreeBean(this, folder, model, null);
						} catch(Exception e) {
							logger.debug("ViewEntryController.handleRenderRequestAfterValidation(Exception:  '" + MiscUtil.exToString(e) + "'):  2:  Ignored");
						}
						throw nf;
					}
					model.put("entryMoved", newFolder);
					
					try {
						fe = getShowEntry(entryId, formData, request, response, newFolder.getId(), model, entryViewType);
						folderId = newFolder.getId();
					} catch(Exception e) {
						try {
							logger.debug("ViewEntryController.handleRenderRequestAfterValidation(Exception:  '" + MiscUtil.exToString(e) + "'):  3");
							Binder folder = getBinderModule().getBinder(folderId);
							BinderHelper.setupStandardBeans(this, request, response, model, folderId);
							DefinitionHelper.getDefinitions(folder, model, 
									(String)userFolderProperties.getProperty(ObjectKeys.USER_PROPERTY_DISPLAY_DEFINITION));
						} catch(Exception e2) {
							logger.debug("ViewEntryController.handleRenderRequestAfterValidation(Exception:  '" + MiscUtil.exToString(e2) + "'):  4:  Ignored");
						}
						throw nf;
					}
				} catch(OperationAccessControlExceptionNoName e) {
					//Access is not allowed
					if (WebHelper.isUserLoggedIn(request) && 
							!ObjectKeys.GUEST_USER_INTERNALID.equals(user.getInternalId())) {
						//Access is not allowed
						String refererUrl = (String)request.getAttribute(WebKeys.REFERER_URL);
						model.put(WebKeys.URL, refererUrl);
						return prepBeans(request, new ModelAndView(WebKeys.VIEW_ACCESS_DENIED, model));
					} else {
						//Please log in
						String refererUrl = (String)request.getAttribute(WebKeys.REFERER_URL);
						model.put(WebKeys.URL, refererUrl);
						return prepBeans(request, new ModelAndView(WebKeys.VIEW_LOGIN_PLEASE, model));
					}
				} catch(AccessControlException e) {
					//Access is not allowed
					if (WebHelper.isUserLoggedIn(request) && 
							!ObjectKeys.GUEST_USER_INTERNALID.equals(user.getInternalId())) {
						//Access is not allowed
						String refererUrl = (String)request.getAttribute(WebKeys.REFERER_URL);
						model.put(WebKeys.URL, refererUrl);
						return prepBeans(request, new ModelAndView(WebKeys.VIEW_ACCESS_DENIED, model));
					} else {
						//Please log in
						String refererUrl = (String)request.getAttribute(WebKeys.REFERER_URL);
						model.put(WebKeys.URL, refererUrl);
						return prepBeans(request, new ModelAndView(WebKeys.VIEW_LOGIN_PLEASE, model));
					}
				}
			}

			//Set up the rest of the standard beans
			//These have been documented, so don't delete any
			model.put(WebKeys.DEFINITION_ENTRY, fe);
			model.put(WebKeys.ENTRY, fe);
			if (fe != null) 
				BinderHelper.getBinderAccessibleUrl(this, fe.getParentBinder(), fe.getId(), request, response, model);

			//Build the reload url (after getting the entryId from the title if necessary)
			if (PortletAdapterUtil.isRunByAdapter((PortletRequest) request)) {
				AdaptedPortletURL adapterUrl = new AdaptedPortletURL(request, "ss_forum", false);
				adapterUrl.setParameter(WebKeys.ACTION, WebKeys.ACTION_VIEW_FOLDER_ENTRY);
				adapterUrl.setParameter(WebKeys.URL_BINDER_ID, folderId.toString());
				adapterUrl.setParameter(WebKeys.URL_ENTRY_ID, entryId);
				adapterUrl.setParameter(WebKeys.URL_RANDOM, WebKeys.URL_RANDOM_PLACEHOLDER);
				model.put(WebKeys.RELOAD_URL, adapterUrl.toString());
				if (formData.containsKey(WebKeys.RELOAD_URL_FORCED)) {
					model.clear();
					model.put(WebKeys.RELOAD_URL_FORCED, adapterUrl.toString());			
					return prepBeans(request, new ModelAndView(WebKeys.VIEW_LISTING_IFRAME, model));
				} 
			} else {
				PortletURL reloadUrl = response.createRenderURL();
				reloadUrl.setParameter(WebKeys.URL_BINDER_ID, folderId.toString());
				reloadUrl.setParameter(WebKeys.URL_ENTRY_ID, entryId);
				reloadUrl.setParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_VIEW_ENTRY);
				reloadUrl.setParameter(WebKeys.ACTION, WebKeys.ACTION_VIEW_FOLDER_LISTING);
				reloadUrl.setParameter(WebKeys.URL_RANDOM, WebKeys.URL_RANDOM_PLACEHOLDER);
				model.put(WebKeys.RELOAD_URL, reloadUrl.toString());
				if (formData.containsKey(WebKeys.RELOAD_URL_FORCED)) {
					model.clear();
					model.put(WebKeys.RELOAD_URL_FORCED, reloadUrl.toString());			
					return prepBeans(request, new ModelAndView(WebKeys.VIEW_LISTING_IFRAME, model));
				} 
			}

			String viewType = BinderHelper.getViewType(this, fe.getParentBinder());			
			viewPath = BinderHelper.getViewListingJsp(this, viewType);
			buildEntryToolbar(this, request, response, model, fe, viewType, userProperties);
			BinderHelper.setRepliesAccessControl(this, model, fe);

			//Build the mashup beans
			Document configDocument = (Document)model.get(WebKeys.CONFIG_DEFINITION);
			DefinitionHelper.buildMashupBeans(this, fe.getParentBinder(), configDocument, model, request );

			boolean accessible_simple_ui = SPropsUtil.getBoolean("accessibility.simple_ui", false);
			if (!displayType.equals(ObjectKeys.USER_DISPLAY_STYLE_ACCESSIBLE) || !accessible_simple_ui) {
				//Folder action menu
				//Build the standard toolbar
				Toolbar folderActionsToolbar = new Toolbar();
				BinderHelper.buildFolderActionsToolbar(this, request, response, folderActionsToolbar, folderId.toString());
				model.put(WebKeys.FOLDER_ACTIONS_TOOLBAR,  folderActionsToolbar.getToolbar());
			}
			
			//Build the navigation beans
			Folder folder = (Folder)fe.getParentBinder();
			BinderHelper.buildNavigationLinkBeans(this, folder, model);
			Binder workspaceBinder = null;
			if (folder.isTop()) {
				workspaceBinder = folder.getParentBinder();
			} else {
				workspaceBinder = folder.getTopFolder().getParentBinder();
			}
			BinderHelper.buildWorkspaceTreeBean(this, workspaceBinder, model, null);
			
			//Get access rights for the parent folder. Used to put forceUnlock link on page
			if (getBinderModule().testAccess(folder, BinderOperation.deleteBinder)) {
				model.put(WebKeys.CAN_FORCE_FILE_UNLOCK, true);
			}
				
			//only want to update visits when first enter.  Don't want cancels on modifies
			//to increment count
			if (!PortletRequestUtils.getStringParameter(request, WebKeys.IS_REFRESH, "0").equals("1")) { 
				//doesn't make sense on replies unless we update the visits when replies are show with the entry
				//that seems wasteful, so don't bother at all
				if (fe.isTop()) getFolderModule().setUserVisit(fe);  
		       	getReportModule().addAuditTrail(AuditType.view, fe);

			}
		} catch(NoFolderEntryByTheIdException e) {
			logger.debug("ViewEntryController.handleRenderRequestAfterValidation(NoFolderEntryByTheIdException):  Ignored");
		} catch(OperationAccessControlExceptionNoName e) {
			//Access is not allowed
			if (WebHelper.isUserLoggedIn(request) && 
					!ObjectKeys.GUEST_USER_INTERNALID.equals(user.getInternalId())) {
				//Access is not allowed
				String refererUrl = (String)request.getAttribute(WebKeys.REFERER_URL);
				model.put(WebKeys.URL, refererUrl);
				return prepBeans(request, new ModelAndView(WebKeys.VIEW_ACCESS_DENIED, model));
			} else {
				//Please log in
				String refererUrl = (String)request.getAttribute(WebKeys.REFERER_URL);
				model.put(WebKeys.URL, refererUrl);
				return prepBeans(request, new ModelAndView(WebKeys.VIEW_LOGIN_PLEASE, model));
			}
		}
		
		PortletURL nextPrevUrl = response.createRenderURL();
		nextPrevUrl.setParameter(WebKeys.URL_BINDER_ID, folderId.toString());
		nextPrevUrl.setParameter(WebKeys.URL_ENTRY_ID, entryId);
		nextPrevUrl.setParameter(WebKeys.ACTION, WebKeys.ACTION_VIEW_FOLDER_ENTRY);
		nextPrevUrl.setParameter(WebKeys.URL_RANDOM, WebKeys.URL_RANDOM_PLACEHOLDER);
		model.put(WebKeys.NEXT_PREV_URL, nextPrevUrl.toString());
		
		if(fe == null) {
			Document config = getDefinitionModule().getDefinitionConfig();
			model.put(WebKeys.CONFIG_DEFINITION, config);
			return prepBeans(request, new ModelAndView("entry/deleted_entry", model));		
		} else {
			if (operation.equals(WebKeys.OPERATION_VIEW_PHOTO)) {
				return prepBeans(request, new ModelAndView(WebKeys.VIEW_PHOTO, model));
			}
			if (entryViewType.equals("entryBlogView") && PortletAdapterUtil.isRunByAdapter(request)) {
				model.put(WebKeys.SNIPPET, true);
				viewPath = "entry/view_entry_snippet";
			}
			return prepBeans(request, new ModelAndView(viewPath, model));
		}
	} 

	protected Toolbar buildEntryToolbar(AllModulesInjected bs, RenderRequest request, RenderResponse response, 
			Map model, FolderEntry entry, String viewType, Map userProperties) {

		PortletURL url;
		
		User user = RequestContextHolder.getRequestContext().getUser();
		
		//Initialize the acl bean
		Map accessControlEntryMap = BinderHelper.getAccessControlEntityMapBean(model, entry);

		Map disabledQual = new HashMap();
		disabledQual.put("disabled", new Boolean(true));
		PortletURL nullPortletUrl = null;
		boolean reserveAccessCheck = false;
		boolean isUserBinderAdministrator = false;
		boolean isEntryReserved = false;
		boolean isLockedByAndLoginUserSame = false;

		if (getFolderModule().testAccess(entry, FolderOperation.reserveEntry)) {
			reserveAccessCheck = true;
		}
		if (getFolderModule().testAccess(entry, FolderOperation.overrideReserveEntry)) {
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
	    //The "Reply" menu
		//strings for urls
		String entryDefId=entry.getEntryDefId(); // cannot be null here
		String entryId = entry.getId().toString();
		String folderId = entry.getParentFolder().getId().toString();
		Document defDoc = entry.getEntryDefDoc();
		Element familyEle = (Element) defDoc.getRootElement().selectSingleNode("./properties/property[@name='family']");
		String replyText = NLT.get("toolbar.comment");
		if (familyEle != null && familyEle.attributeValue("value", "").equals("discussion")) {
			replyText = NLT.get("toolbar.reply");
			model.put(WebKeys.DEFINITION_FAMILY, familyEle.attributeValue("value", ""));
		}
				
	    //Build the toolbar array
		Folder folder = entry.getParentFolder();
		Toolbar toolbar = new Toolbar();
		boolean isPreDeleted = entry.isPreDeleted();
		if (!isPreDeleted) {
			if (viewType.equals(Definition.VIEW_STYLE_WIKI) && 
					(!(folder.isMirrored() && folder.getResourceDriverName() == null)) && 
					!folder.isMirroredAndReadOnly() && getFolderModule().testAccess(entry, FolderOperation.addEntry)) {
				List defaultEntryDefinitions = folder.getEntryDefinitions();
				int defaultEntryDefs = ((null == defaultEntryDefinitions) ? 0 : defaultEntryDefinitions.size());
				model.put(WebKeys.URL_BINDER_ENTRY_DEFS, String.valueOf( defaultEntryDefs ));
				if (defaultEntryDefs > 1) {
					SortedMap addEntryUrls = new TreeMap();
					model.put(WebKeys.URL_ADD_ENTRIES, addEntryUrls);
					int count = 1;
					int	defaultEntryDefIndex = ListFolderHelper.getDefaultFolderEntryDefinitionIndex(
						RequestContextHolder.getRequestContext().getUser().getId(),
						bs.getProfileModule(),
						folder,
						defaultEntryDefinitions);
					Map dropdownQualifiers = new HashMap();
					dropdownQualifiers.put("highlight", new Boolean(true));
					String	entryAdd = NLT.get("toolbar.new");
					toolbar.addToolbarMenu("1_add", entryAdd, "", dropdownQualifiers);
					model.put(WebKeys.URL_BINDER_ENTRY_ADD, entryAdd);
					Map qualifiers = new HashMap();
					qualifiers.put("popup", new Boolean(true));
					//String onClickPhrase = "if (self.ss_addEntry) {return(self.ss_addEntry(this))} else {return true;}";
					//qualifiers.put(ObjectKeys.TOOLBAR_QUALIFIER_ONCLICK, onClickPhrase);
					for (int i=0; i<defaultEntryDefinitions.size(); ++i) {
						Definition def = (Definition) defaultEntryDefinitions.get(i);
						AdaptedPortletURL adapterUrl = new AdaptedPortletURL(request, "ss_forum", true);
						adapterUrl.setParameter(WebKeys.ACTION, WebKeys.ACTION_ADD_FOLDER_ENTRY);
						adapterUrl.setParameter(WebKeys.URL_BINDER_ID, folder.getId().toString());
						adapterUrl.setParameter(WebKeys.URL_ENTRY_TYPE, def.getId());
						String title = NLT.getDef(def.getTitle());
						if (toolbar.checkToolbarMenuItem("1_add", "entries", title)) {
							title = title + " (" + String.valueOf(count++) + ")";
						}
						toolbar.addToolbarMenuItem("1_add", "entries", title, adapterUrl.toString(), qualifiers);
						if (i == defaultEntryDefIndex) {
							adapterUrl.setParameter(WebKeys.URL_NAMESPACE, response.getNamespace());
							adapterUrl.setParameter(WebKeys.URL_ADD_DEFAULT_ENTRY_FROM_INFRAME, "1");
							model.put(WebKeys.URL_ADD_DEFAULT_ENTRY, adapterUrl.toString());
						}
						addEntryUrls.put(title, adapterUrl.toString());
					}
				} else if (defaultEntryDefs != 0) {
					// Only one option
					Definition def = (Definition) defaultEntryDefinitions.get(0);
					AdaptedPortletURL adapterUrl = new AdaptedPortletURL(request, "ss_forum", true);
					adapterUrl.setParameter(WebKeys.ACTION, WebKeys.ACTION_ADD_FOLDER_ENTRY);
					adapterUrl.setParameter(WebKeys.URL_BINDER_ID, folder.getId().toString());
					adapterUrl.setParameter(WebKeys.URL_ENTRY_TYPE, def.getId());
					String[] nltArgs = new String[] {NLT.getDef(def.getTitle())};
					String title = NLT.get("toolbar.new");
					Map qualifiers = new HashMap();
					qualifiers.put("popup", new Boolean(true));
					qualifiers.put("highlight", new Boolean(true));
					toolbar.addToolbarMenu("1_add", title, adapterUrl.toString(), qualifiers);
					model.put(WebKeys.URL_BINDER_ENTRY_ADD, title);
					model.put(WebKeys.URL_ADD_ENTRY, adapterUrl.toString());
					
					adapterUrl.setParameter(WebKeys.URL_NAMESPACE, response.getNamespace());
					adapterUrl.setParameter(WebKeys.URL_ADD_DEFAULT_ENTRY_FROM_INFRAME, "1");
					model.put(WebKeys.URL_ADD_DEFAULT_ENTRY, adapterUrl.toString());
				}
			}

			if (getFolderModule().testAccess(entry, FolderOperation.addReply)) {
				accessControlEntryMap.put("addReply", new Boolean(true));
				List replyStyles = DefinitionUtils.getPropertyValueList(defDoc.getRootElement(), "replyStyle");
				model.put(WebKeys.ENTRY_REPLY_STYLES, replyStyles);
				if (!replyStyles.isEmpty()) {
					// Is there only one reply style?
					if (replyStyles.size() == 1) {
						// Yes!  So don't show it as a drop down menu.
						String replyStyleId = (String)replyStyles.get(0);
						if (Validator.isNotNull(replyStyleId)) {
							AdaptedPortletURL adapterUrl = new AdaptedPortletURL(request, "ss_forum", true);
							adapterUrl.setParameter(WebKeys.ACTION, WebKeys.ACTION_ADD_FOLDER_REPLY);
							adapterUrl.setParameter(WebKeys.URL_BINDER_ID, folderId);
							adapterUrl.setParameter(WebKeys.URL_ENTRY_TYPE, replyStyleId);
							adapterUrl.setParameter(WebKeys.URL_ENTRY_ID, entryId);
	
							Map qualifiers = new HashMap();
							qualifiers.put("nosort", true);
							qualifiers.put("popup", new Boolean(true));
							// Note that for the single case, we push
							// the add reply URL for the 'Add
							// Comment...' button on the comment tab.
							String addReplyUrl = adapterUrl.toString();
							model.put(WebKeys.URL_ADD_REPLY_URL, addReplyUrl);
							toolbar.addToolbarMenu("1_reply", replyText, 
									addReplyUrl, qualifiers);
						}
					} else {
						// No, there's more than one reply style!  Show
						// it as a drop down menu.
						toolbar.addToolbarMenu("1_reply", replyText);
						Map qualifiers = new HashMap();
						qualifiers.put("nosort", true);
						qualifiers.put("popup", new Boolean(true));
						for (int i = 0; i < replyStyles.size(); i++) {
							String replyStyleId = (String)replyStyles.get(i);
							try {
								Definition replyDef = getDefinitionModule().getDefinition(replyStyleId);
								AdaptedPortletURL adapterUrl = new AdaptedPortletURL(request, "ss_forum", true);
								adapterUrl.setParameter(WebKeys.ACTION, WebKeys.ACTION_ADD_FOLDER_REPLY);
								adapterUrl.setParameter(WebKeys.URL_BINDER_ID, folderId);
								adapterUrl.setParameter(WebKeys.URL_ENTRY_TYPE, replyStyleId);
								adapterUrl.setParameter(WebKeys.URL_ENTRY_ID, entryId);
								toolbar.addToolbarMenuItem("1_reply", "replies", NLT.getDef(replyDef.getTitle()), 
										adapterUrl.toString(), qualifiers);
								
								// Note that for the multiple case, we
								// don't push an add reply URL so no
								// 'Add Comment...' button will appear
								// on the comment tab.
							} catch (NoDefinitionByTheIdException e) {
								logger.debug("ViewEntryController.buildEntryToolbar(NoDefinitionByTheIdException):  Ignored");
								continue;
							}
						}
					}
				}
			}
		    
			if (getFolderModule().testAccess(entry, FolderOperation.modifyEntry) ||
					getFolderModule().testAccess(entry, FolderOperation.modifyEntryFields)) {
				if ((isEntryReserved && !isLockedByAndLoginUserSame) || entry.getParentFolder().isMirroredAndReadOnly()) {
					toolbar.addToolbarMenu("2_modify", NLT.get("toolbar.modify"), nullPortletUrl, disabledQual);
				}
				else {
					if (getFolderModule().testAccess(entry, FolderOperation.modifyEntry))
						accessControlEntryMap.put("modifyEntry", new Boolean(true));
					if (getFolderModule().testAccess(entry, FolderOperation.modifyEntryFields))
						accessControlEntryMap.put("modifyEntryFields", new Boolean(true));
					//The "Modify" menu
					Map qualifiers = new HashMap();
					qualifiers.put("nosort", true);
					qualifiers.put("popup", new Boolean(true));
					AdaptedPortletURL adapterUrl = new AdaptedPortletURL(request, "ss_forum", true);
					adapterUrl.setParameter(WebKeys.ACTION, WebKeys.ACTION_MODIFY_FOLDER_ENTRY);
					adapterUrl.setParameter(WebKeys.URL_BINDER_ID, folderId);
					adapterUrl.setParameter(WebKeys.URL_ENTRY_TYPE, entryDefId);
					adapterUrl.setParameter(WebKeys.URL_ENTRY_ID, entryId);
					toolbar.addToolbarMenu("2_modify", NLT.get("toolbar.modify"), adapterUrl.toString(), qualifiers);
				}
			}
				
			if (getFolderModule().testAccess(entry, FolderOperation.deleteEntry)) {
				//The "Delete" menu
				if ((isEntryReserved && !isLockedByAndLoginUserSame) || entry.getParentFolder().isMirroredAndReadOnly()) {
					toolbar.addToolbarMenu("5_delete", NLT.get("toolbar.delete"), nullPortletUrl, disabledQual);
				}
				else {
					accessControlEntryMap.put("deleteEntry", new Boolean(true));
					Map qualifiers = new HashMap();
					qualifiers.put("nosort", true);
					qualifiers.put("onClick", "return ss_confirmDeleteEntry(this, '" + folderId + "', '" + entryId + "');");
					url = response.createActionURL();
					url.setParameter(WebKeys.ACTION, WebKeys.ACTION_MODIFY_FOLDER_ENTRY);
					url.setParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_DELETE);
					url.setParameter(WebKeys.URL_BINDER_ID, folderId);
					url.setParameter(WebKeys.URL_ENTRY_TYPE, entryDefId);
					url.setParameter(WebKeys.URL_ENTRY_ID, entryId); 
					toolbar.addToolbarMenu("3_delete", NLT.get("toolbar.delete"), url, qualifiers);
				}
			}
				
			//The "Actions" menu
			toolbar.addToolbarMenu("4_actions", NLT.get("toolbar.actions"));

			if (getFolderModule().testAccess(entry, FolderOperation.readEntry) && entry.isTop()) {
				//The "Access Control" menu
				accessControlEntryMap.put("accessControl", new Boolean(true));
				Map qualifiers = new HashMap();
				qualifiers.put("nosort", true);
				qualifiers.put("popup", new Boolean(true));
				qualifiers.put(WebKeys.HELP_SPOT, "helpSpot.accessControlMenu");
				url = response.createActionURL();
				url.setParameter(WebKeys.ACTION, WebKeys.ACTION_ACCESS_CONTROL);
				url.setParameter(WebKeys.URL_WORKAREA_ID, entryId); 
				url.setParameter(WebKeys.URL_WORKAREA_TYPE, entry.getWorkAreaType()); 
				toolbar.addToolbarMenuItem("4_actions", "actions", NLT.get("toolbar.menu.accessControl"), url, qualifiers);
			}
			
			//Does the user have access to reserve the entry
			if (reserveAccessCheck) {
				//If no one has reserved the entry, it can be locked
				if (!isEntryReserved) {
					Map qualifiers = new HashMap();
					qualifiers.put("nosort", true);
					qualifiers.put("onClick", "ss_postToThisUrl(this.href);return false;");
					url = response.createActionURL();
					url.setParameter(WebKeys.ACTION, WebKeys.ACTION_LOCK_FOLDER_ENTRY);
					url.setParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_LOCK);
					url.setParameter(WebKeys.URL_BINDER_ID, folderId);
					url.setParameter(WebKeys.URL_ENTRY_ID, entryId);
					toolbar.addToolbarMenuItem("4_actions", "actions", NLT.get("toolbar.lock.one"), url, qualifiers);
				} else {
				    //If some one has reserved the entry	
					//If the person who has locked the entry and the logged in user are the same we allow access to unlock
					//If the person who has logged in is the binder administrator we allow access to unlock
					if (isLockedByAndLoginUserSame || isUserBinderAdministrator) {
						Map qualifiers = new HashMap();
						qualifiers.put("nosort", true);
						qualifiers.put("onClick", "ss_postToThisUrl(this.href);return false;");
			   			url = response.createActionURL();
						url.setParameter(WebKeys.ACTION, WebKeys.ACTION_LOCK_FOLDER_ENTRY);
						url.setParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_UNLOCK);
						url.setParameter(WebKeys.URL_BINDER_ID, folderId);
						url.setParameter(WebKeys.URL_ENTRY_ID, entryId);
						if(!isLockedByAndLoginUserSame) {
							qualifiers.put("onClick", "return ss_confirmUnlockEntry(this);");
							toolbar.addToolbarMenuItem("4_actions", "actions", NLT.get("toolbar.unlock.one"), url, qualifiers);
						} else {
							toolbar.addToolbarMenuItem("4_actions", "actions", NLT.get("toolbar.unlock.one"), url, qualifiers);
						}
					}
				}
			}
			
			if (viewType.equals(Definition.VIEW_STYLE_WIKI)) {
				if (getBinderModule().testAccess(entry.getParentBinder(), BinderOperation.setProperty)) {
					String wikiHomePageId = (String)entry.getParentBinder().getProperty(ObjectKeys.BINDER_PROPERTY_WIKI_HOMEPAGE);
					model.put(WebKeys.WIKI_HOMEPAGE_ENTRY_ID, wikiHomePageId);
					if (Validator.isNotNull(wikiHomePageId) && wikiHomePageId.equals(String.valueOf(entry.getId()))) {
						Map qualifiers = new HashMap();
						qualifiers.put("nosort", true);
						qualifiers.put("onClick", "return ss_confirmPost(ss_clearWikiHomePageConfirmation, '', this.href)");
						url = response.createActionURL();
						url.setParameter(WebKeys.ACTION, WebKeys.ACTION_VIEW_FOLDER_ENTRY);
						url.setParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_CLEAR_WIKI_HOMEPAGE);
						url.setParameter(WebKeys.URL_BINDER_ID, folderId);
						url.setParameter(WebKeys.URL_ENTRY_TYPE, entryDefId);
						url.setParameter(WebKeys.URL_ENTRY_ID, entryId); 
						toolbar.addToolbarMenuItem("4_actions", "actions", NLT.get("toolbar.clearWikiHomepage"), url, qualifiers);
					} else {
						Map qualifiers = new HashMap();
						qualifiers.put("nosort", true);
						qualifiers.put("onClick", "return ss_confirmPost(ss_setWikiHomePageConfirmation, '', this.href)");
						qualifiers.put(WebKeys.HELP_SPOT, "helpSpot.setWikiHomepage");
						url = response.createActionURL();
						url.setParameter(WebKeys.ACTION, WebKeys.ACTION_VIEW_FOLDER_ENTRY);
						url.setParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_SET_WIKI_HOMEPAGE);
						url.setParameter(WebKeys.URL_BINDER_ID, folderId);
						url.setParameter(WebKeys.URL_ENTRY_TYPE, entryDefId);
						url.setParameter(WebKeys.URL_ENTRY_ID, entryId); 
						toolbar.addToolbarMenuItem("4_actions", "actions", NLT.get("toolbar.setWikiHomepage"), url, qualifiers);
					}
				}
			}	
			
			if (!ObjectKeys.GUEST_USER_INTERNALID.equals(user.getInternalId()) &&
					entry.isTop()) {
				boolean canShare = getFolderModule().testAccess(entry, FolderOperation.allowSharing);
				if (!canShare) {
					canShare = user.getId().equals(entry.getCreation().getPrincipal().getId());
				}
				if (canShare) {
					Map qualifiers = new HashMap();
					qualifiers.put("nosort", true);
					qualifiers.put("onClick", "window.top.ss_invokeShareDlg('"+entryId+"');return false;");
					AdaptedPortletURL adapterUrl = new AdaptedPortletURL(request, "ss_forum", false);
					adapterUrl.setParameter(WebKeys.ACTION, WebKeys.ACTION_VIEW_FOLDER_ENTRY);
					adapterUrl.setParameter(WebKeys.URL_BINDER_ID, folderId);
					adapterUrl.setParameter(WebKeys.URL_ENTRY_ID, entryId); 
					toolbar.addToolbarMenuItem("4_actions", "actions", NLT.get("toolbar.shareThis"), adapterUrl.toString(), qualifiers);
				}
			}

			if ((!isEntryReserved || isLockedByAndLoginUserSame) && 
					entry.isTop() && getFolderModule().testAccess(entry, FolderOperation.moveEntry)) {
				//The "Move" menu item
				Map qualifiers = new HashMap();
				qualifiers.put("nosort", true);
				qualifiers.put("popup", new Boolean(true));
				url = response.createActionURL();
				url.setParameter(WebKeys.ACTION, WebKeys.ACTION_MODIFY_FOLDER_ENTRY);
				url.setParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_MOVE);
				url.setParameter(WebKeys.URL_BINDER_ID, folderId);
				url.setParameter(WebKeys.URL_ENTRY_ID, entryId);
				toolbar.addToolbarMenuItem("4_actions", "actions", NLT.get("toolbar.move"), url, qualifiers);
			}
			if (entry.isTop() && getFolderModule().testAccess(entry, FolderOperation.copyEntry)) {
				//The "Copy" menu
				Map qualifiers = new HashMap();
				qualifiers.put("nosort", true);
				qualifiers.put("popup", new Boolean(true));
				url = response.createActionURL();
				url.setParameter(WebKeys.ACTION, WebKeys.ACTION_MODIFY_FOLDER_ENTRY);
				url.setParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_COPY);
				url.setParameter(WebKeys.URL_BINDER_ID, folderId);
				url.setParameter(WebKeys.URL_ENTRY_ID, entryId);
				toolbar.addToolbarMenuItem("4_actions", "actions", NLT.get("toolbar.copy"), url, qualifiers);
			}
			if (getFolderModule().testAccess(entry, FolderOperation.changeEntryType)) {
				//The "Change entry type" menu
				Map qualifiers = new HashMap();
				qualifiers.put("nosort", true);
				qualifiers.put("popup", new Boolean(true));
				url = response.createActionURL();
				url.setParameter(WebKeys.ACTION, WebKeys.MANAGE_FOLDER_ENTRY_TYPES);
				url.setParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_CHANGE_ENTRY_TYPE_ENTRY);
				url.setParameter(WebKeys.URL_BINDER_ID, folderId);
				url.setParameter(WebKeys.URL_ENTRY_ID, entryId);
				toolbar.addToolbarMenuItem("4_actions", "actions", NLT.get("toolbar.changeEntryType"), url, qualifiers);
			}

			HttpServletRequest req = WebHelper.getHttpServletRequest(request);
			String userAgents = org.kablink.teaming.util.SPropsUtil.getString("mobile.userAgents", "");
			String tabletUserAgents = org.kablink.teaming.util.SPropsUtil.getString("tablet.userAgentRegexp", "");
			Boolean testForAndroid = org.kablink.teaming.util.SPropsUtil.getBoolean("tablet.useDefaultTestForAndroidTablets", false);
			if (BrowserSniffer.is_mobile(req, userAgents) && !BrowserSniffer.is_tablet(req, tabletUserAgents, testForAndroid)) {
				//The "Mobile UI" menu
				Map qualifiers = new HashMap();
				qualifiers.put("nosort", true);
				qualifiers.put("onClick", "window.open(this.href,'_top');return false;");
				url = response.createActionURL();
				url.setParameter(WebKeys.ACTION, WebKeys.ACTION_MOBILE_AJAX);
				url.setParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_MOBILE_SHOW_MOBILE_UI);
				toolbar.addToolbarMenuItem("4_actions", "actions", NLT.get("toolbar.showMobileUI"), url, qualifiers);
			}

				
			if (getFolderModule().testAccess(entry, FolderOperation.modifyEntry)) {
				Map<String,Definition> workflowAssociations = entry.getParentBinder().getWorkflowAssociations();
				List<Definition> configWorkflows = entry.getParentBinder().getWorkflowDefinitions();
				Set<WorkflowState>runningWorkflows = entry.getWorkflowStates();
				Set<Definition>runningWorkflowDefinitions = new HashSet<Definition>();
				for (WorkflowState ws : runningWorkflows) {
					runningWorkflowDefinitions.add(ws.getDefinition());
				}
				if (!configWorkflows.isEmpty() || !runningWorkflows.isEmpty() || !workflowAssociations.isEmpty()) {
					Map qualifiers = new HashMap();
					
					//The "Workflow" menu
					//See if there are workflows running
					Map runningWorkflowDefs = new HashMap();
					for (WorkflowState state:runningWorkflows) {
						Definition workflowDef = state.getDefinition();
						if (!runningWorkflowDefs.containsKey(workflowDef.getId()) &&
								(configWorkflows.contains(workflowDef) || !workflowAssociations.containsValue(workflowDef))) {
							String wfTitle = NLT.getDef(workflowDef.getTitle());
							String wfTitle1 = wfTitle.replaceAll("'", "\\\\'");
							qualifiers = new HashMap();
							qualifiers.put("nosort", true);
							qualifiers.put("onClick", "return ss_confirmPost('" + NLT.get("entry.confirmStopWorkflow") + "', '"+wfTitle1+"', this.href)");
							url = response.createActionURL();
							url.setParameter(WebKeys.ACTION, WebKeys.ACTION_STOP_WORKFLOW);
							url.setParameter(WebKeys.URL_BINDER_ID, folderId);
							url.setParameter(WebKeys.URL_ENTRY_ID, entryId); 
							url.setParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_STOP_WORKFLOW);
							url.setParameter(WebKeys.URL_WORKFLOW_TYPE, workflowDef.getId());
							toolbar.addToolbarMenuItem("4_actions", "workflow", 
									NLT.get("toolbar.menu.stopWorkflow", new String[] {wfTitle}), url, qualifiers);
							runningWorkflowDefs.put(workflowDef.getId(), "1");
						}
					}
					
					//Add the associated workflows to configWorkflows
					for (Definition associatedDef : workflowAssociations.values()) {
						//This makes it possible to start any associated workflow if it isn't running
						if (!configWorkflows.contains(associatedDef) && !runningWorkflowDefinitions.contains(associatedDef)) {
							configWorkflows.add(associatedDef);
						}
					}
					for (Definition workflowDef:configWorkflows) {
						if (!runningWorkflowDefs.containsKey(workflowDef.getId())) {
							String wfTitle = NLT.getDef(workflowDef.getTitle());
							String wfTitle1 = wfTitle.replaceAll("'", "\\\\'");
							qualifiers = new HashMap();
							qualifiers.put("nosort", true);
							qualifiers.put("onClick", "return ss_confirmPost('" + NLT.get("entry.confirmStartWorkflow") + "', '"+wfTitle1+"', this.href)");
							url = response.createActionURL();
							url.setParameter(WebKeys.ACTION, WebKeys.ACTION_START_WORKFLOW);
							url.setParameter(WebKeys.URL_BINDER_ID, folderId);
							url.setParameter(WebKeys.URL_ENTRY_ID, entryId); 
							url.setParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_START_WORKFLOW);
							url.setParameter(WebKeys.URL_WORKFLOW_TYPE, workflowDef.getId());
							toolbar.addToolbarMenuItem("4_actions", "workflow", 
									NLT.get("toolbar.menu.startWorkflow", new String[] {wfTitle}), url, qualifiers);
						}
					}
				}
			}
	
			if (getFolderModule().testAccess(entry, FolderOperation.report)) {
				accessControlEntryMap.put("report", new Boolean(true));
				Map qualifiers = new HashMap();
				qualifiers.put("nosort", true);
				toolbar.addToolbarMenu("6_reports", NLT.get("toolbar.reports"), "", qualifiers);
	
				String servletUrl = WebUrlUtil.getServletRootURL(request) + WebKeys.SERVLET_DOWNLOAD_REPORT + "?" +
				WebKeys.URL_BINDER_ID + "=" + folderId + "&" + WebKeys.URL_ENTRY_ID + "=" + entryId + "&" +
				WebKeys.URL_REPORT_TYPE + "=entry&forumOkBtn=OK"; 
				toolbar.addToolbarMenuItem("6_reports", "", NLT.get("toolbar.reports.activity"), servletUrl, qualifiers);
				
				qualifiers = new HashMap();
				qualifiers.put("nosort", true);
				qualifiers.put("popup", Boolean.TRUE);
				AdaptedPortletURL adapterUrl = new AdaptedPortletURL(request, "ss_forum", true);
				adapterUrl.setParameter(WebKeys.ACTION, WebKeys.ACTION_VIEW_WORKFLOW_HISTORY);
				adapterUrl.setParameter(WebKeys.URL_ENTITY_ID, entryId);
				adapterUrl.setParameter(WebKeys.URL_FOLDER_ID, folderId);
				toolbar.addToolbarMenuItem("6_reports", "", NLT.get("toolbar.reports.workflowHistory"), adapterUrl.toString(), qualifiers);
			}
		}
		
		//The "Footer" menu
		Toolbar footerToolbar = new Toolbar();
		if (!isPreDeleted) {
			Map qualifiers = new HashMap();
			String permaLink = PermaLinkUtil.getPermalink(request, entry);
			qualifiers.put("onClick", "ss_showPermalink(this);return false;");
			footerToolbar.addToolbarMenu("1_permalink", NLT.get("toolbar.menu.entryPermalink"), permaLink, qualifiers);
	
			model.put(WebKeys.PERMALINK, permaLink);
			model.put(WebKeys.MOBILE_URL, SsfsUtil.getMobileUrl(request));		
	
			if (entry.isTop() && !ObjectKeys.GUEST_USER_INTERNALID.equals(user.getInternalId())) {
				AdaptedPortletURL adapterSubscriptionUrl = new AdaptedPortletURL(request, "ss_forum", false);
				adapterSubscriptionUrl.setParameter(WebKeys.ACTION, WebKeys.ACTION_AJAX_REQUEST);
				adapterSubscriptionUrl.setParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_SUBSCRIBE);
				adapterSubscriptionUrl.setParameter(WebKeys.URL_BINDER_ID, folderId);
				adapterSubscriptionUrl.setParameter(WebKeys.URL_ENTRY_ID, entryId);
				adapterSubscriptionUrl.setParameter("rn", "ss_randomNumberPlaceholder");			
				
				qualifiers = new HashMap();		
				qualifiers.put("onClick", "ss_createPopupDiv(this, 'ss_subscription_entry"+entryId+"'); return false;");
				footerToolbar.addToolbarMenu("4_subscribe", NLT.get("toolbar.menu.subscribeToEntry"), adapterSubscriptionUrl.toString(), qualifiers);
			}
			
			String[] contributorIds = ListFolderHelper.collectContributorIds(entry);
			
			if (!user.getEmailAddresses().isEmpty() && 
					!ObjectKeys.GUEST_USER_INTERNALID.equals(user.getInternalId())) {
				AdaptedPortletURL adapterUrl = new AdaptedPortletURL(request, "ss_forum", true);
				adapterUrl.setParameter(WebKeys.ACTION, WebKeys.ACTION_SEND_ENTRY_EMAIL);
				adapterUrl.setParameter(WebKeys.URL_BINDER_ID, folderId);
				adapterUrl.setParameter(WebKeys.URL_ENTRY_ID, entryId);
				qualifiers = new HashMap();
				qualifiers.put("popup", Boolean.TRUE);
				qualifiers.put("post", Boolean.TRUE);
				qualifiers.put("postParams", Collections.singletonMap(WebKeys.USER_IDS_TO_ADD, contributorIds));			
				footerToolbar.addToolbarMenu("3_sendMail", NLT.get("toolbar.menu.sendMail"), adapterUrl.toString(), qualifiers);
			}

			// Bugzilla 715365:  Remove link to conferencing.
			/*
				if (getConferencingModule().isEnabled() && 
						!ObjectKeys.GUEST_USER_INTERNALID.equals(user.getInternalId())) {
					AdaptedPortletURL adapterUrl = new AdaptedPortletURL(request, "ss_forum", true);
					adapterUrl.setParameter(WebKeys.ACTION, WebKeys.ACTION_ADD_MEETING);
					adapterUrl.setParameter(WebKeys.URL_BINDER_ID, folderId);
					adapterUrl.setParameter(WebKeys.URL_ENTRY_ID, entryId);
					qualifiers = new HashMap();
					qualifiers.put("popup", Boolean.TRUE);
					qualifiers.put("post", Boolean.TRUE);
					qualifiers.put("postParams", Collections.singletonMap(WebKeys.USER_IDS_TO_ADD, contributorIds));
					footerToolbar.addToolbarMenu("6_addMeeting", NLT.get("toolbar.menu.addMeeting"), adapterUrl.toString(), qualifiers);
				}
			*/
			
			//   iCalendar
			if (entry.getEvents() != null && !entry.getEvents().isEmpty()) {
				qualifiers = new HashMap();
				qualifiers.put("onClick", "ss_showPermalink(this);return false;");
				footerToolbar.addToolbarMenu("2_iCalendar", NLT.get("toolbar.menu.iCalendar"), UrlUtil.getICalURL(request, folderId, entryId), qualifiers);
				model.put(WebKeys.TOOLBAR_URL_ICAL, UrlUtil.getICalURL(request, folderId, entryId));
			}
			
			//Export / Import
			if (entry.isTop() && !ObjectKeys.GUEST_USER_INTERNALID.equals(user.getInternalId())) {
				if (bs.getBinderModule().testAccess(entry.getParentBinder(), BinderOperation.export)) {
					qualifiers = new HashMap();
					url = response.createActionURL();
					url.setParameter(WebKeys.ACTION, WebKeys.ACTION_EXPORT_IMPORT);
					url.setParameter(WebKeys.OPERATION, WebKeys.OPERATION_EXPORT);
					url.setParameter(WebKeys.URL_BINDER_ID, folderId);
					url.setParameter(WebKeys.URL_ENTRY_ID, entryId); 
					url.setParameter(WebKeys.URL_SHOW_MENU, "false");
					footerToolbar.addToolbarMenu("5_export", NLT.get("toolbar.menu.exportEntry"), url, qualifiers);
				}
			}
		}


		model.put(WebKeys.FOLDER_ENTRY_TOOLBAR,  toolbar.getToolbar());
		model.put(WebKeys.FOOTER_TOOLBAR,  footerToolbar.getToolbar());

		return toolbar;
	}

	protected void buildNoEntryBeans(RenderRequest request, 
			RenderResponse response, Folder folder, String entryTitle, Map model) {
		//Initialize the acl bean
		Map accessControlFolderMap = BinderHelper.getAccessControlEntityMapBean(model, folder);

		if ( response instanceof PortletResponseImpl )
		{
			HttpServletResponse httpServletResponse;
			
			// Set the http response header to no-cache so this page won't get cached.
			httpServletResponse = ((PortletResponseImpl)response).getHttpServletResponse();
			httpServletResponse.setHeader( "Pragma", "no-cache" );
			httpServletResponse.setHeader( "Cache-Control", "no-cache" );
			httpServletResponse.setDateHeader( "Expires", 0 );
		}

		//Build the "add entry" beans
		List defaultEntryDefinitions = folder.getEntryDefinitions();
		Map urls = new HashMap();
		Map titles = new TreeMap();
		model.put(WebKeys.ADD_ENTRY_DEFINITIONS,  defaultEntryDefinitions);
		model.put(WebKeys.ADD_ENTRY_URLS,  urls);
		model.put(WebKeys.ADD_ENTRY_TITLES,  titles);
		model.put(WebKeys.ADD_ENTRY_TITLE,  entryTitle);
		if (getFolderModule().testAccess(folder, FolderOperation.addEntry)) {				
			accessControlFolderMap.put("addEntry", new Boolean(true));
			if (!defaultEntryDefinitions.isEmpty()) {
				int	defaultEntryDefIndex = ListFolderHelper.getDefaultFolderEntryDefinitionIndex(
					RequestContextHolder.getRequestContext().getUser().getId(),
					getProfileModule(),
					folder,
					defaultEntryDefinitions);
				for (int i=0; i<defaultEntryDefinitions.size(); ++i) {
					Definition def = (Definition) defaultEntryDefinitions.get(i);
					AdaptedPortletURL adapterUrl = new AdaptedPortletURL(request, "ss_forum", true);
					adapterUrl.setParameter(WebKeys.ACTION, WebKeys.ACTION_ADD_FOLDER_ENTRY);
					adapterUrl.setParameter(WebKeys.URL_BINDER_ID, folder.getId().toString());
					adapterUrl.setParameter(WebKeys.URL_ENTRY_TYPE, def.getId());
					urls.put(def.getId(), adapterUrl.toString());
					titles.put(NLT.getDef(def.getTitle()), def.getId());
					if (i == defaultEntryDefIndex) {
						adapterUrl.setParameter(WebKeys.URL_NAMESPACE, response.getNamespace());
						adapterUrl.setParameter(WebKeys.URL_ADD_DEFAULT_ENTRY_FROM_INFRAME, "1");
						model.put(WebKeys.URL_ADD_DEFAULT_ENTRY, adapterUrl.toString());
					}
				}
			}
		}
	}

	protected FolderEntry getShowEntry(String entryId, Map formData, RenderRequest req, RenderResponse response, 
			Long folderId, Map model, String viewType)  {
		Folder folder = null;
		FolderEntry entry = null;
		Map folderEntries = null;
		folderEntries  = getFolderModule().getEntryTree(folderId, Long.valueOf(entryId), true);
		entry = (FolderEntry)folderEntries.get(ObjectKeys.FOLDER_ENTRY);
		boolean isPreDeleted = entry.isPreDeleted();
		if (!isPreDeleted) {
			folderEntries  = getFolderModule().getEntryTree(folderId, Long.valueOf(entryId), false);
		}
		folder = entry.getParentFolder();
		model.put(WebKeys.FOLDER_ENTRY_PRE_DELETED, isPreDeleted);
		model.put(WebKeys.FOLDER_ENTRY_DESCENDANTS, folderEntries.get(ObjectKeys.FOLDER_ENTRY_DESCENDANTS));
		model.put(WebKeys.FOLDER_ENTRY_ANCESTORS, folderEntries.get(ObjectKeys.FOLDER_ENTRY_ANCESTORS));
		
		boolean isAppletSupported = SsfsUtil.supportApplets(req);
		
		String strEntryURL = DefinitionHelper.getWebDAVURL(req, folder, entry);
		//String strEntryURL = SsfsUtil.getEntryUrl(folder, entry, strRepositoryName);
		//String strWebDavURL = SsfsUtil.getLibraryBinderUrl(folder);
		
		SeenMap seen = getProfileModule().getUserSeenMap(null);
		Map seenEntries = new HashMap();
		
		model.put(WebKeys.IS_APPLET_SUPPORTED, isAppletSupported);
		model.put(WebKeys.SEEN_MAP, seen);
		model.put(WebKeys.SEEN_ENTRIES, seenEntries);
		model.put(WebKeys.ENTRY, entry);
		model.put(WebKeys.DEFINITION_ENTRY, entry);
		model.put(WebKeys.FOLDER, folder);
		model.put(WebKeys.BINDER, folder);
		model.put(WebKeys.BINDER_WEBDAV_URL, strEntryURL);
		//model.put(WebKeys.BINDER_WEBDAV_URL, strWebDavURL);
		Map tagResults = TagUtil.uniqueTags(getFolderModule().getTags(entry));
		model.put(WebKeys.COMMUNITY_TAGS, tagResults.get(ObjectKeys.COMMUNITY_ENTITY_TAGS));
		model.put(WebKeys.PERSONAL_TAGS, tagResults.get(ObjectKeys.PERSONAL_ENTITY_TAGS));
		model.put(WebKeys.CONFIG_JSP_STYLE, Definition.JSP_STYLE_VIEW);
		if (DefinitionHelper.getDefinition(entry.getEntryDefDoc(), model, "//item[@name='"+viewType+"']") == false) {
			DefinitionHelper.getDefaultEntryView(entry, model, "//item[@name='"+viewType+"']");
		}
		//Figure out which is the "primary file" for this entry (if any)
		DefinitionHelper.getPrimaryFile(entry, model);

		//only start transaction if necessary
		List<FolderEntry> replies = new ArrayList((List)model.get(WebKeys.FOLDER_ENTRY_DESCENDANTS));
		if (replies != null)  {
			for (FolderEntry reply : replies) {
				if (!seen.checkIfSeen(reply)) {  
					seenEntries.put(reply.getId(), false);
					getProfileModule().setSeen(null, reply);
				} else {
					seenEntries.put(reply.getId(), true);
				}
			}
			replies.add(entry);
		}
		
		//Now check if the top entry is unseen or not
		if (!seen.checkIfSeen(entry)) {  
			seenEntries.put(entry.getId(), true);		//always show the entry as "seen"
			getProfileModule().setSeen(null, entry);
		} else {
			seenEntries.put(entry.getId(), true);
		}
		BinderHelper.buildWorkflowSupportBeans(this, replies, model);
		
		Map fieldsData = getDefinitionModule().getEntryDefinitionElements(entry.getEntryDefId());
		model.put(WebKeys.ENTRY_DEFINTION_ELEMENT_DATA, fieldsData);
		
		return entry;
	}
	
	/*
	 * Ensures the beans in the ModelAndView are ready to go.
	 */
	private static ModelAndView prepBeans(RenderRequest request, ModelAndView mv) {
		// Nothing to do here.  Unlike the various 'binder'
		// controllers, we don't want or need to call
		// GwtUIHelper.cacheToolbarBeans(request, mv);
		return mv;
	}
}
