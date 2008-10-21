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
package com.sitescape.team.web.util;

import java.util.Map;

import javax.portlet.PortletRequest;
import javax.servlet.http.HttpServletRequest;

import com.sitescape.team.domain.DefinableEntity;
import com.sitescape.team.domain.EntityIdentifier;
import com.sitescape.team.domain.FileAttachment;
import com.sitescape.team.domain.EntityIdentifier.EntityType;
import com.sitescape.team.module.profile.ProfileModule;
import com.sitescape.team.portletadapter.AdaptedPortletURL;
import com.sitescape.team.util.SpringContextUtil;
import com.sitescape.team.web.WebKeys;
import com.sitescape.util.search.Constants;

public class PermaLinkUtil {

	
	//userId may be placeholder
	public static String getUserPermalink(HttpServletRequest request, String userId) {
		AdaptedPortletURL adapterUrl = new AdaptedPortletURL(request, "ss_forum", true);
		adapterUrl.setParameter(WebKeys.ACTION, WebKeys.ACTION_VIEW_PERMALINK);
		adapterUrl.setParameter(WebKeys.URL_ENTRY_ID, userId);
		adapterUrl.setParameter(WebKeys.URL_ENTITY_TYPE, EntityIdentifier.EntityType.user.name());
		return adapterUrl.toString();
	}
	
	public static String getPermalink(HttpServletRequest request, DefinableEntity entity) {
		AdaptedPortletURL url = new AdaptedPortletURL(request, "ss_forum", true);
		getPermalinkURL(url, entity.getId(), entity.getEntityType());
		return url.toString();
	}
	public static String getPermalink(PortletRequest request, DefinableEntity entity) {
		AdaptedPortletURL url = new AdaptedPortletURL(request, "ss_forum", true);
		getPermalinkURL(url, entity.getId(), entity.getEntityType());
		return url.toString();
	}
	public static String getPermalink(DefinableEntity entity) {
		AdaptedPortletURL url = AdaptedPortletURL.createAdaptedPortletURLOutOfWebContext("ss_forum", true);
		getPermalinkURL(url, entity.getId(), entity.getEntityType());
		return url.toString();
	}
	public static String getPermalink(Long entityId, EntityIdentifier.EntityType entityType) {
		AdaptedPortletURL adapterUrl = AdaptedPortletURL.createAdaptedPortletURLOutOfWebContext("ss_forum", true);
		getPermalinkURL(adapterUrl, entityId, entityType);
		return adapterUrl.toString();
	}
	protected static void getPermalinkURL(AdaptedPortletURL adapterUrl, Long entityId, EntityIdentifier.EntityType entityType) {
		adapterUrl.setParameter(WebKeys.ACTION, WebKeys.ACTION_VIEW_PERMALINK);
		adapterUrl.setParameter(WebKeys.URL_ENTITY_TYPE, entityType.name());
		if (entityType.isBinder()) {
			adapterUrl.setParameter(WebKeys.URL_BINDER_ID, entityId.toString());
		} else {
			adapterUrl.setParameter(WebKeys.URL_ENTRY_ID, entityId.toString());
		} 
		
	}
	public static String getPermalink(HttpServletRequest request, Map searchResults) {
		AdaptedPortletURL url = new AdaptedPortletURL(request, "ss_forum", true);
		getPermalinkURL(url, searchResults);
		return url.toString();
	}
	public static String getPermalink(PortletRequest request, Map searchResults) {
		AdaptedPortletURL url = new AdaptedPortletURL(request, "ss_forum", true);
		getPermalinkURL(url, searchResults);
		return url.toString();
	}
	public static String getPermalink(Map searchResults) {
		AdaptedPortletURL url = AdaptedPortletURL.createAdaptedPortletURLOutOfWebContext("ss_forum", true);
		getPermalinkURL(url, searchResults);
		return url.toString();
	}
	protected static void getPermalinkURL(AdaptedPortletURL url, Map searchResults) {
		String id = (String)searchResults.get(Constants.DOCID_FIELD);
		String type = (String)searchResults.get(Constants.ENTITY_FIELD);
		url.setParameter(WebKeys.ACTION, WebKeys.ACTION_VIEW_PERMALINK);
		EntityIdentifier.EntityType entityType = EntityIdentifier.EntityType.valueOf(type);
		url.setParameter(WebKeys.URL_ENTITY_TYPE, entityType.name());
		if (entityType.isBinder()) {
			url.setParameter(WebKeys.URL_BINDER_ID, id);
		} else {
			url.setParameter(WebKeys.URL_ENTRY_ID, id);
		} 

	}
	public static String getFilePermalink(FileAttachment attachment) {
		return getFilePermalink(attachment.getOwner().getEntity(), attachment.getFileItem().getName());
		
	}
	public static String getFilePermalink(DefinableEntity entity, String fileName) {
		AdaptedPortletURL adapterUrl = AdaptedPortletURL.createAdaptedPortletURLOutOfWebContext("ss_forum", true);
		getPermalinkURL(adapterUrl, entity.getId(), entity.getEntityType());
		adapterUrl.setParameter(WebKeys.URL_FILE_NAME, fileName);
		return adapterUrl.toString();	
	}
	public static String getFilePermalink(Map searchResults, String fileName) {
		AdaptedPortletURL adapterUrl = AdaptedPortletURL.createAdaptedPortletURLOutOfWebContext("ss_forum", true);
		getPermalinkURL(adapterUrl, searchResults);
		adapterUrl.setParameter(WebKeys.URL_FILE_NAME, fileName);
		return adapterUrl.toString();
	}
	public static String getTitlePermalink(Long binderId, String normalizedTitle) {
		AdaptedPortletURL adapterUrl = AdaptedPortletURL.createAdaptedPortletURLOutOfWebContext("ss_forum", true);
		adapterUrl.setParameter(WebKeys.ACTION, WebKeys.ACTION_VIEW_PERMALINK);
		adapterUrl.setParameter(WebKeys.URL_BINDER_ID, binderId.toString());
		adapterUrl.setParameter(WebKeys.URL_ENTRY_TITLE, normalizedTitle);
		adapterUrl.setParameter(WebKeys.URL_ENTITY_TYPE, EntityType.folderEntry.name());
		return adapterUrl.toString();

	}
	private static ProfileModule getProfileModule() {
		return (ProfileModule) SpringContextUtil.getBean("profileModule");
	}
}
