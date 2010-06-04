/**
 * Copyright (c) 1998-2010 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2010 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2010 Novell, Inc. All Rights Reserved.
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

import java.util.HashMap;
import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.domain.DefinableEntity;
import org.kablink.teaming.domain.EntityIdentifier;
import org.kablink.teaming.domain.FileAttachment;
import org.kablink.teaming.domain.Folder;
import org.kablink.teaming.domain.FolderEntry;
import org.kablink.teaming.domain.NoBinderByTheIdException;
import org.kablink.teaming.domain.NoFolderEntryByTheIdException;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.domain.Workspace;
import org.kablink.teaming.module.shared.AccessUtils;
import org.kablink.teaming.portletadapter.AdaptedPortletURL;
import org.kablink.teaming.portletadapter.portlet.PortletResponseImpl;
import org.kablink.teaming.runas.RunasCallback;
import org.kablink.teaming.runas.RunasTemplate;
import org.kablink.teaming.security.AccessControlException;
import org.kablink.teaming.util.NLT;
import org.kablink.teaming.util.ReleaseInfo;
import org.kablink.teaming.util.SPropsUtil;
import org.kablink.teaming.web.WebKeys;
import org.kablink.teaming.web.portlet.SAbstractController;
import org.kablink.teaming.web.util.BinderHelper;
import org.kablink.teaming.web.util.GwtUIHelper;
import org.kablink.teaming.web.util.MiscUtil;
import org.kablink.teaming.web.util.PermaLinkUtil;
import org.kablink.teaming.web.util.PortletRequestUtils;
import org.kablink.teaming.web.util.WebHelper;
import org.kablink.teaming.web.util.WebUrlUtil;
import org.kablink.util.BrowserSniffer;
import org.kablink.util.Validator;
import org.springframework.web.portlet.ModelAndView;


/**
 * @author Peter Hurley
 *
 */
public class ViewPermalinkController  extends SAbstractController {
	public void handleActionRequestAfterValidation(final ActionRequest request, final ActionResponse response) throws Exception {
		User user = null;
		String sUrl = null;
		AdaptedPortletURL adaptedPortletUrl = null;
		response.setRenderParameters(request.getParameterMap());
		
		try {
			if (!WebHelper.isUserLoggedIn(request) || RequestContextHolder.getRequestContext() == null) {
				Long zoneId = WebHelper.getZoneIdByVirtualHost(request);
				user = AccessUtils.getZoneGuestUser(zoneId);
				if (user == null) {
				//	User must log in to see this
					response.setRenderParameters(request.getParameterMap());
					return;
				}
				adaptedPortletUrl = (AdaptedPortletURL)RunasTemplate.runas(new RunasCallback() {
					public Object doAs() {
						return processRequest(request, response);
					}
				}, user);
				
				if (adaptedPortletUrl != null) {
					sUrl = adaptedPortletUrl.toString();
			    	if(logger.isDebugEnabled()) {
			    		logger.debug("Permalink followed: " + sUrl);
			    	}
				}
				
			} else {
				user = RequestContextHolder.getRequestContext().getUser();
				adaptedPortletUrl = processRequest(request, response);
				if (adaptedPortletUrl != null) {
					sUrl = adaptedPortletUrl.toString();
			    	if(logger.isDebugEnabled())
			    	{
			    		logger.debug("Permalink followed: " + sUrl);
			    	}
				}
			}

			boolean durangoUI = GwtUIHelper.isGwtUIActive(request);
			if (durangoUI && adaptedPortletUrl != null)
			{
				String param;
				
				// Has this permalink already been seen by GWT?
				param = PortletRequestUtils.getStringParameter( request, "seen_by_gwt", "");
				if ( param == null || !param.equalsIgnoreCase( "1" ) )
				{
					String binderId = "";
					String urlStr = "";

					// No, don't redirect the url.

					// Get the url that we would have redirected to.
					if ( adaptedPortletUrl != null )
					{
						String action;
						
						// Are we dealing with an action of "view_ws_listing"?
						action = adaptedPortletUrl.getParameterSingleValue( WebKeys.URL_ACTION );
						if ( action != null && action.equalsIgnoreCase( WebKeys.ACTION_VIEW_WS_LISTING ) )
						{
							String entryId;
							
							// Yes, does the "entityId" parameter equal "ss_user_id_place_holder"?
							entryId = adaptedPortletUrl.getParameterSingleValue( WebKeys.URL_ENTRY_ID );
							if ( entryId != null && entryId.equalsIgnoreCase( WebKeys.URL_USER_ID_PLACE_HOLDER ) )
							{
								Long userId;
								
								// Yes, get the binder id of the user's workspace.
								userId = WebHelper.getRequiredUserId( request );
					 			binderId  = getProfileModule().getEntryWorkspaceId( userId ).toString();
								if ( binderId != null && binderId.length() > 0 )
								{
									// Create a new url that just has the "action" and "binderId" parameters.
									adaptedPortletUrl = new AdaptedPortletURL( request, "ss_forum", true );

									adaptedPortletUrl.setParameter( WebKeys.URL_ACTION, WebKeys.ACTION_VIEW_WS_LISTING  );
						 			adaptedPortletUrl.setParameter( WebKeys.URL_BINDER_ID, binderId );
								}
							}
						}
						
						binderId = adaptedPortletUrl.getParameterSingleValue( WebKeys.URL_BINDER_ID );
						if ( binderId == null )
						   binderId = "";
						   
						urlStr = adaptedPortletUrl.toString();
					}
					
					// Add the id of the binder we are working with.
					response.setRenderParameter( WebKeys.URL_BINDER_ID, binderId );
					
					// Add the adapted portlet url.
					response.setRenderParameter( "adaptedUrl", urlStr );
					
					return;
				}
				
				String operation = PortletRequestUtils.getStringParameter( request, "operation", "");
				if (MiscUtil.hasString(operation)) {
					sUrl += ("&operation=" + operation);
				}
			}
			
			if (sUrl != null) {
				response.sendRedirect(sUrl);
			} else {
				//There is no mapping to this entity, so fall through the render handler
			}
		} catch  (AccessControlException ac) {
			//User must log in to see this
			response.setRenderParameters(request.getParameterMap());
			response.setRenderParameter("accessException", "true");
			return;
		} catch (NoBinderByTheIdException nb) {
			logger.debug("ViewPermalinkController.handleActionRequestAfterValidation(NoBinderByTheIdException):  Exception rendered to response.");
			response.setRenderParameters(request.getParameterMap());
			response.setRenderParameter("noBinderByIdException", "true");
		}
	}

	/**
	 * 
	 * @param request
	 * @return
	 */
	protected AdaptedPortletURL processRequest(ActionRequest request, ActionResponse response) {
		HttpServletRequest httpReq = WebHelper.getHttpServletRequest(request);
		boolean isMobile = false;
		String userAgents = org.kablink.teaming.util.SPropsUtil.getString("mobile.userAgents", "");
		if (httpReq != null) isMobile = BrowserSniffer.is_mobile(httpReq, userAgents);
		//binderId is not longer required on all entries
		String zoneUUID= PortletRequestUtils.getStringParameter(request, WebKeys.URL_ZONE_UUID, "");
		String binderId= PortletRequestUtils.getStringParameter(request, WebKeys.URL_BINDER_ID, "");
		String entryId= PortletRequestUtils.getStringParameter(request, WebKeys.URL_ENTRY_ID, "");
		String fileId= PortletRequestUtils.getStringParameter(request, WebKeys.URL_FILE_ID, "");
		String fileName= PortletRequestUtils.getStringParameter(request, WebKeys.URL_FILE_NAME, "");
		String entryTitle = PortletRequestUtils.getStringParameter(request, WebKeys.URL_ENTRY_TITLE, "");
		String captive = PortletRequestUtils.getStringParameter(request, WebKeys.URL_CAPTIVE, null);
		String showTrash = PortletRequestUtils.getStringParameter(request, WebKeys.URL_SHOW_TRASH, "");
		EntityIdentifier.EntityType entityType = EntityIdentifier.EntityType.none;
		DefinableEntity entity = null;
		try {
			entityType = EntityIdentifier.EntityType.valueOf(PortletRequestUtils.getStringParameter(request, WebKeys.URL_ENTITY_TYPE, ""));
		} catch(Exception ignore) {
			logger.debug("ViewPermalinkController.ProcessRequest(Exception:  '" + MiscUtil.exToString(ignore) + "'):  Ignored");
		};
		
		AdaptedPortletURL url = new AdaptedPortletURL(request, "ss_forum", true);
 		
 		if (Validator.isNotNull(fileId))
 		{
 		   String urlStr;
 		   
 		   urlStr = getFileUrlById(request, entityType, binderId, entryId, fileId);
 		   url = new AdaptedPortletURL( urlStr );
 		   return url;
        }
      
 		if (Validator.isNotNull(fileName))
 		{
 		   String urlStr;
 		   
 		   urlStr =getFileUrlByName(request, entityType, binderId, entryId, fileName);
 		   url = new AdaptedPortletURL( urlStr );
 		   return url;
        }

		boolean binderPreDeleted;
		if (entityType.equals(EntityIdentifier.EntityType.folderEntry)) { //folderEntry
			if (isMobile && Validator.isNotNull(entryId)) {
				entity = getFolderModule().getEntry(null, Long.valueOf(entryId));
				url.setParameter(WebKeys.URL_BINDER_ID, entity.getParentBinder().getId().toString());
				url.setParameter(WebKeys.URL_ENTRY_ID, entryId);
				if (!zoneUUID.equals("")) url.setParameter(WebKeys.URL_ZONE_UUID, zoneUUID);
				url.setParameter(WebKeys.URL_ACTION, WebKeys.ACTION_MOBILE_AJAX);
				url.setParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_MOBILE_SHOW_ENTRY);
			} else {
				//entries move so the binderId may not be valid
				if (Validator.isNotNull(entryId)) {
					Long targetEntryId = getFolderModule().getZoneEntryId(Long.valueOf(entryId), zoneUUID);
					if (targetEntryId == null) {
						response.setRenderParameter("notImportedException", "true");
						return null;
					} else {
						entryId = targetEntryId.toString();
						entity = getFolderModule().getEntry(null, Long.valueOf(entryId));
						url.setParameter(WebKeys.URL_BINDER_ID, entity.getParentBinder().getId().toString());
						url.setParameter(WebKeys.URL_ENTRY_ID, entryId);
					}
				} else {
					Long targetBinderId = getBinderModule().getZoneBinderId(Long.valueOf(binderId), zoneUUID, entityType.name());
					if (targetBinderId == null) {
						response.setRenderParameter("notImportedException", "true");
						return null;
					} else {
						binderId = String.valueOf(targetBinderId);
						entity = getBinderModule().getBinder(Long.valueOf(binderId));
						url.setParameter(WebKeys.URL_BINDER_ID, binderId);
						url.setParameter(WebKeys.URL_ENTRY_TITLE, entryTitle);
					}
				}
				
				boolean accessible_simple_ui = SPropsUtil.getBoolean("accessibility.simple_ui", false);
				User user = RequestContextHolder.getRequestContext().getUser();
				String displayStyle = user.getDisplayStyle();
				if (ObjectKeys.USER_DISPLAY_STYLE_NEWPAGE.equals(displayStyle) || 
						(ObjectKeys.USER_DISPLAY_STYLE_ACCESSIBLE.equals(displayStyle) && accessible_simple_ui)) {
					url.setParameter(WebKeys.URL_ACTION, "view_folder_entry");
				} else {
					url.setParameter(WebKeys.URL_ACTION, "view_folder_listing");
				}
				url.setParameter(WebKeys.URL_ENTRY_VIEW_STYLE, WebKeys.URL_ENTRY_VIEW_STYLE_FULL);
			}
			
			if (null != entity) {
				if (entity instanceof FolderEntry) {
					if (((FolderEntry) entity).isPreDeleted()) {
						throw new NoFolderEntryByTheIdException(entity.getId());
					}
				}
				else {
					if      (entity instanceof Workspace) binderPreDeleted = ((Workspace) entity).isPreDeleted();
					else if (entity instanceof Folder)    binderPreDeleted = ((Folder)    entity).isPreDeleted();
					else                                  binderPreDeleted = false;
					if (binderPreDeleted) {
						throw new NoBinderByTheIdException(entity.getId());
					}
				}
			}
		} else	if (entityType.isBinder() || entityType.equals(EntityIdentifier.EntityType.none)) {
			Long targetBinderId = getBinderModule().getZoneBinderId(Long.valueOf(binderId), zoneUUID, entityType.name());
			if (targetBinderId != null) {
				binderId = String.valueOf(targetBinderId);
				entity = getBinderModule().getBinder(Long.valueOf(binderId));
			}
			if (null != entity) {
				if      (entity instanceof Workspace) binderPreDeleted = ((Workspace) entity).isPreDeleted();
				else if (entity instanceof Folder)    binderPreDeleted = ((Folder)    entity).isPreDeleted();
				else binderPreDeleted = false;
				if (binderPreDeleted) {
					throw new NoBinderByTheIdException(Long.valueOf(binderId));
				}
			}
			url.setParameter(WebKeys.URL_BINDER_ID, binderId);
			entityType = entity.getEntityType();
			if (isMobile) {
				url.setParameter(WebKeys.URL_ACTION, WebKeys.ACTION_MOBILE_AJAX);
				if (entityType.equals(EntityIdentifier.EntityType.workspace)) {
					url.setParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_MOBILE_SHOW_WORKSPACE);
				} else if (entityType.equals(EntityIdentifier.EntityType.profiles)) {
					url.setParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_MOBILE_SHOW_WORKSPACE);
				} else {
					url.setParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_MOBILE_SHOW_FOLDER);				
				}
				if (!zoneUUID.equals("") && targetBinderId == null) {
					//Go to the mobile ui to show the error message
					url.setParameter(WebKeys.URL_BINDER_ID, "");
				}
			} else {
				if (entityType.equals(EntityIdentifier.EntityType.workspace)) {
					url.setParameter(WebKeys.URL_ACTION, "view_ws_listing");
				} else if (entityType.equals(EntityIdentifier.EntityType.profiles)) {
					url.setParameter(WebKeys.URL_ACTION, "view_profile_listing");
				} else {
					url.setParameter(WebKeys.URL_ACTION, "view_folder_listing");				
				}
			}
			
		} else if (entityType.isPrincipal()) {
			//permalinks are meant for the use workspace
	 		if (entryId.equals(WebKeys.URL_ENTRY_ID_PLACE_HOLDER) || entryId.equals(WebKeys.URL_USER_ID_PLACE_HOLDER)) {  
	 			try {
	 				entity = getProfileModule().getProfileBinder();
	 			} catch  (AccessControlException ac) {
					Long zoneId = WebHelper.getZoneIdByVirtualHost(request);
					User user = AccessUtils.getZoneSuperUser(zoneId);

	 				entity = (DefinableEntity)RunasTemplate.runas(new RunasCallback () {
	 					public Object doAs() {
	 						return getProfileModule().getProfileBinder();
	 					}
	 				}, user);
	 			}
				if (isMobile) {
					url.setParameter(WebKeys.URL_ACTION, WebKeys.ACTION_MOBILE_AJAX);
					url.setParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_MOBILE_SHOW_WORKSPACE);
		 			url.setParameter(WebKeys.URL_BINDER_ID, entity.getId().toString());
		 			url.setParameter(WebKeys.URL_ENTRY_ID, WebKeys.URL_USER_ID_PLACE_HOLDER);
				} else {
		 			url.setParameter(WebKeys.URL_ACTION, "view_ws_listing");
		 			url.setParameter(WebKeys.URL_BINDER_ID, entity.getId().toString());
		 			url.setParameter(WebKeys.URL_ENTRY_ID, WebKeys.URL_USER_ID_PLACE_HOLDER);
				}
	 		} else {
	 			Long workspaceId  = getProfileModule().getEntryWorkspaceId(Long.valueOf(entryId));
	 			if (workspaceId == null) {
					if (isMobile) {
						url.setParameter(WebKeys.URL_ACTION, WebKeys.ACTION_MOBILE_AJAX);
						url.setParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_MOBILE_SHOW_USER);
			 			url.setParameter(WebKeys.URL_BINDER_ID, getProfileModule().getProfileBinder().getId().toString());
			 			url.setParameter(WebKeys.URL_ENTRY_ID, entryId);
					} else {
			 			url.setParameter(WebKeys.URL_ACTION, "view_profile_entry");
			 			url.setParameter(WebKeys.URL_BINDER_ID, getProfileModule().getProfileBinder().getId().toString());
			 			url.setParameter(WebKeys.URL_ENTRY_ID, entryId);
			 			url.setParameter(WebKeys.URL_ENTRY_VIEW_STYLE, "full");
					}
	 			} else {
					if (isMobile) {
			 			entity = getBinderModule().getBinder(workspaceId);
						url.setParameter(WebKeys.URL_ACTION, WebKeys.ACTION_MOBILE_AJAX);
						url.setParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_MOBILE_SHOW_WORKSPACE);
			 			url.setParameter(WebKeys.URL_BINDER_ID, entity.getId().toString());
					} else {
			 			entity = getBinderModule().getBinder(workspaceId);
			 			url.setParameter(WebKeys.URL_ACTION, "view_ws_listing");
			 			url.setParameter(WebKeys.URL_BINDER_ID, entity.getId().toString());
					}
	 			}
	 		}
		} 
		
		if("true".equals(captive))
			url.setParameter(WebKeys.URL_CAPTIVE, "true");
		else if("false".equals(captive))
			url.setParameter(WebKeys.URL_CAPTIVE, "false");
		
		if (MiscUtil.hasString(showTrash)) {
			url.setParameter(WebKeys.URL_SHOW_TRASH, showTrash);
		}
				
    	return url;
	}
	
	protected String getFileUrlById(ActionRequest request, EntityIdentifier.EntityType entityType, String binderId, String entryId, String fileId) {
		DefinableEntity entity;
		if (entityType.equals(EntityIdentifier.EntityType.folderEntry)) { //folderEntry
			//entries move so the binderId may not be valid
			entity = getFolderModule().getEntry(null, Long.valueOf(entryId));
		} else if (entityType.isPrincipal()) {
 			entity = getProfileModule().getEntry(Long.valueOf(entryId));
		} else	{
			entity = getBinderModule().getBinder(Long.valueOf(binderId));
 		} 
		FileAttachment attachment = (FileAttachment)entity.getAttachment(fileId);
		if (attachment != null) {
			return WebUrlUtil.getFileUrl(request, WebKeys.ACTION_READ_FILE, attachment);
		} else {
			//use old v1 style
			Long entityId=null;
			if (entity.getEntityType().isBinder()) {
				binderId = entity.getId().toString();
			} else {
				binderId = entity.getParentBinder().getId().toString() ;
				entityId = entity.getId();
			}
			StringBuffer sUrl = new StringBuffer(WebUrlUtil.getServletRootURL(request)).append(WebKeys.SERVLET_VIEW_FILE).append("?");
			sUrl.append(WebKeys.URL_BINDER_ID).append("=").append(binderId);
			sUrl.append("&").append(WebKeys.URL_ENTITY_TYPE).append("=").append(entity.getEntityType().name());
			if (entityId!=null) sUrl.append("&").append(WebKeys.URL_ENTRY_ID).append("=").append(entityId);
			sUrl.append("&").append(WebKeys.URL_FILE_ID).append("=").append(fileId);
			return sUrl.toString();
		}
	
	}
	protected String getFileUrlByName(ActionRequest request, EntityIdentifier.EntityType entityType, String binderId, String entryId, String fileName) {
		DefinableEntity entity;
		if (entityType.equals(EntityIdentifier.EntityType.folderEntry)) { //folderEntry
			//entries move so the binderId may not be valid
			entity = getFolderModule().getEntry(null, Long.valueOf(entryId));
		} else if (entityType.isPrincipal()) {
 			entity = getProfileModule().getEntry(Long.valueOf(entryId));
		} else	{
			entity = getBinderModule().getBinder(Long.valueOf(binderId));
 		} 
		return WebUrlUtil.getFileUrl(WebUrlUtil.getServletRootURL(request), WebKeys.ACTION_READ_FILE, entity, fileName);
	}

	public ModelAndView handleRenderRequestAfterValidation(RenderRequest request, 
			RenderResponse response) throws Exception {
		Map<String,Object> model = new HashMap<String,Object>();
		
		if ( response instanceof PortletResponseImpl )
		{
			HttpServletResponse httpServletResponse;
			
			// Set the http response header to no-cache so this page won't get cached.
			httpServletResponse = ((PortletResponseImpl)response).getHttpServletResponse();
			httpServletResponse.setHeader( "Pragma", "no-cache" );
			httpServletResponse.setHeader( "Cache-Control", "no-cache" );
			httpServletResponse.setDateHeader( "Expires", 0 );
		}

		String binderId= PortletRequestUtils.getStringParameter(request, WebKeys.URL_BINDER_ID, "");
		String entryId= PortletRequestUtils.getStringParameter(request, WebKeys.URL_ENTRY_ID, "");
		String entityType= PortletRequestUtils.getStringParameter(request, WebKeys.URL_ENTITY_TYPE, "");
		String fileId= PortletRequestUtils.getStringParameter(request, WebKeys.URL_FILE_ID, "");
		String entryTitle = PortletRequestUtils.getStringParameter(request, WebKeys.URL_ENTRY_TITLE, "");
		String fileName= PortletRequestUtils.getStringParameter(request, WebKeys.URL_FILE_NAME, "");
		String captive = PortletRequestUtils.getStringParameter(request, WebKeys.URL_CAPTIVE, null);

		AdaptedPortletURL url = new AdaptedPortletURL(request, "ss_forum", true);
		url.setParameter(WebKeys.ACTION, WebKeys.ACTION_VIEW_PERMALINK);
		if (Validator.isNotNull(binderId)) url.setParameter(WebKeys.URL_BINDER_ID, binderId);
		if (Validator.isNotNull(entryId)) url.setParameter(WebKeys.URL_ENTRY_ID, entryId);
		if (Validator.isNotNull(entryTitle)) url.setParameter(WebKeys.URL_ENTRY_TITLE, entryTitle);
		if (Validator.isNotNull(entityType)) url.setParameter(WebKeys.URL_ENTITY_TYPE, entityType);
		if (Validator.isNotNull(fileId)) url.setParameter(WebKeys.URL_FILE_ID, fileId);
		if (Validator.isNotNull(fileName)) url.setParameter(WebKeys.URL_FILE_NAME, fileName);
		if("true".equals(captive))
			url.setParameter(WebKeys.URL_CAPTIVE, "true");
		else if("false".equals(captive))
			url.setParameter(WebKeys.URL_CAPTIVE, "false");
		model.put(WebKeys.URL, url.toString());

		boolean durangoUI = GwtUIHelper.isGwtUIActive(request);
		if (durangoUI)
		{
			String param;
			
			// If this permalink has already been handled by the GWT page there will be
			// a "seen_by_gwt" parameter on the url.
			
			// Has this permalink already been seen by GWT?
			param = PortletRequestUtils.getStringParameter( request, "seen_by_gwt", "");
			if ( param == null || !param.equalsIgnoreCase( "1" ) )
			{
				String urlStr;
			   	String userName;
			   	String isNovellTeaming;
			   	String accessException;
			   	String myWSUrl = "";
			   	User user;
			   
				// No, let the gwt page handle this permalink

				user = RequestContextHolder.getRequestContext().getUser();

				// Add the binder id to the response.
				model.put( WebKeys.URL_BINDER_ID, binderId );

				// Add the adapted portlet url to the reponse.
				urlStr = PortletRequestUtils.getStringParameter( request, "adaptedUrl", "" );
				model.put( "adaptedUrl", urlStr );

				// Add the "my workspace" url to the response.
				{
					myWSUrl = PermaLinkUtil.getPermalink( user );
					model.put( "myWorkspaceUrl", myWSUrl );
				}
				
				// Add the flag that tells us if we are running Novell or Kablink Teaming to the response.
				isNovellTeaming = Boolean.toString( ReleaseInfo.isLicenseRequiredEdition() );
				model.put( "isNovellTeaming", isNovellTeaming );
				
				// Get the flag that tells us if the user has rights to this permalink.
				accessException = PortletRequestUtils.getStringParameter( request, "accessException" );

				// Is a user logged in?
				if ( WebHelper.isGuestLoggedIn( request ) )
				{
					// No
					// Add a flag that tells that indicates a user is not logged in.
					model.put( "isUserLoggedIn", "false" );

					// Add the name "Guest" to the response
					userName = NLT.get( "administration.initial.guestTitle" );
					model.put( "userFullName", userName );

					// Does Guest have rights to view the permalink?
					if ( accessException != null && accessException.equalsIgnoreCase( "true" ) )
					{
						// No, add a flag that tells us to prompt for login.
						model.put( "promptForLogin", "true" );
					}
					else
					{
						// Add a flag that tells us we don't need to prompt for login.
						model.put( "promptForLogin", "false" );
					}
				}
				else
				{
					// Yes
					// Add a flag that tells that indicates a user is logged in.
					model.put( "isUserLoggedIn", "true" );
					model.put( "promptForLogin", "false" );

					// Add the user's name to the response.
					{
						String firstName;
						String lastName;
						String fullName;
						
						// Add the user's name.
						firstName = user.getFirstName();
						lastName = user.getLastName();
						if ( firstName != null )
						{
							fullName = firstName;
							if ( lastName != null )
								fullName += " " + lastName;
						}
						else if ( lastName != null )
						{
							fullName = lastName;
						}
						else
							fullName = user.getName();
							
						fullName += " (" + user.getName() + ")";
						
						model.put( "userFullName", fullName );
					}

					// Does this user have rights to view the permalink?
					if ( accessException != null && accessException.equalsIgnoreCase( "true" ) )
					{
						String errMsg;
						
						// No
						// Get the error message that will be displayed to the user.
						errMsg = NLT.get( "permalink.access.denied" );
						model.put( "errMsg", errMsg );
						
						model.put( "adaptedUrl", "" );
						model.put( WebKeys.URL_BINDER_ID, "" );
					}
				}
				
				//Set up the standard beans
				BinderHelper.setupStandardBeans(this, request, response, model);
				return new ModelAndView( "forum/GwtMainPage", model );
			}
		}
		
		if (!"true".equals(PortletRequestUtils.getStringParameter(request, "accessException")) &&
				!"true".equals(PortletRequestUtils.getStringParameter(request, "noBinderByIdException")) &&
				!"true".equals(PortletRequestUtils.getStringParameter(request, "notImportedException"))) {
			return new ModelAndView(WebKeys.VIEW_LOGIN_RETURN, model);
		}
		
		User user = null;
		if (!WebHelper.isUserLoggedIn(request) || RequestContextHolder.getRequestContext() == null) {
			//User must log in to see this
			BinderHelper.setupStandardBeans(this, request, response, model);
			return new ModelAndView(WebKeys.VIEW_LOGIN_PLEASE, model);
		} else {
	        user = RequestContextHolder.getRequestContext().getUser();
	 		
			//Set up the standard beans
			BinderHelper.setupStandardBeans(this, request, response, model);
			if (WebHelper.isUserLoggedIn(request) && 
						!ObjectKeys.GUEST_USER_INTERNALID.equals(user.getInternalId())) {
					//Access is not allowed
				if ("true".equals(PortletRequestUtils.getStringParameter(request, "noBinderByIdException"))) {
					model.put(WebKeys.ERROR_MESSAGE, NLT.get("errorcode.no.folder.by.the.id", new String[] {binderId}));
					return new ModelAndView(WebKeys.VIEW_ERROR_RETURN, model);
				}
				if ("true".equals(PortletRequestUtils.getStringParameter(request, "notImportedException"))) {
					model.put(WebKeys.ERROR_MESSAGE, NLT.get("errorcode.entry.not.imported", new String[] {binderId}));
					return new ModelAndView(WebKeys.VIEW_ERROR_RETURN, model);
				}
				return new ModelAndView(WebKeys.VIEW_ACCESS_DENIED, model);
			} else {
				//Please log in
				return new ModelAndView(WebKeys.VIEW_LOGIN_PLEASE, model);
			}
		}
		
	}
}
