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
package org.kablink.teaming.web.util;

import java.util.Date;
import java.util.Map;

import javax.portlet.PortletRequest;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.kablink.teaming.asmodule.zonecontext.ZoneContextHolder;
import org.kablink.teaming.domain.DefinableEntity;
import org.kablink.teaming.domain.EntityIdentifier;
import org.kablink.teaming.domain.FileAttachment;
import org.kablink.teaming.domain.EntityIdentifier.EntityType;
import org.kablink.teaming.module.profile.ProfileModule;
import org.kablink.teaming.portletadapter.AdaptedPortletURL;
import org.kablink.teaming.util.SpringContextUtil;
import org.kablink.teaming.util.SPropsUtil;
import org.kablink.teaming.web.WebKeys;
import org.kablink.util.Http;
import org.kablink.util.Validator;
import org.kablink.util.search.Constants;

/**
 * ?
 * 
 * @author ?
 */
public class PermaLinkUtil {
	public static final int COLLECTION_USER_DEFAULT = (-1);
    public static final int COLLECTION_MY_FILES = 0;
    public static final int COLLECTION_NET_FOLDERS = 1;
    public static final int COLLECTION_SHARED_BY_ME = 2;
    public static final int COLLECTION_SHARED_WITH_ME = 3;
    public static final int COLLECTION_SHARED_PUBLIC = 4;

	private static final Log m_logger = LogFactory.getLog(PermaLinkUtil.class);

	public static String getUserPermalink(HttpServletRequest request, String userId, Integer collection) {
		AdaptedPortletURL adapterUrl = new AdaptedPortletURL(request, "ss_forum", true, true);
		adapterUrl.setParameter(WebKeys.ACTION, WebKeys.ACTION_VIEW_PERMALINK);
		adapterUrl.setParameter(WebKeys.URL_ENTRY_ID, userId);
		adapterUrl.setParameter(WebKeys.URL_ENTITY_TYPE, EntityIdentifier.EntityType.user.name());
        adapterUrl.setParameter(WebKeys.URL_SHOW_COLLECTION, collection.toString());
		return adapterUrl.toString();
	}

	public static String getUserWhatsNewPermalink(HttpServletRequest request, String userId) {
		AdaptedPortletURL adapterUrl = new AdaptedPortletURL(request, "ss_forum", true, true);
		adapterUrl.setParameter(WebKeys.ACTION, WebKeys.ACTION_VIEW_PERMALINK);
		adapterUrl.setParameter(WebKeys.URL_ENTRY_ID, userId);
		adapterUrl.setParameter(WebKeys.URL_ENTITY_TYPE, EntityIdentifier.EntityType.user.name());
        adapterUrl.setParameter(WebKeys.URL_ACTIVITY_STREAMS_SHOW_SITE_WIDE, "1");
        adapterUrl.setParameter(WebKeys.URL_ACTIVITY_STREAMS_SHOW_SPECIFIC, "10");
		return adapterUrl.toString();
	}

	//userId may be placeholder
	public static String getUserPermalink(HttpServletRequest request, String userId, boolean startWithActivityStreams, boolean startWithDefaultCollection) {
		AdaptedPortletURL adapterUrl = new AdaptedPortletURL(request, "ss_forum", true, true);
		adapterUrl.setParameter(WebKeys.ACTION, WebKeys.ACTION_VIEW_PERMALINK);
		adapterUrl.setParameter(WebKeys.URL_ENTRY_ID, userId);
		adapterUrl.setParameter(WebKeys.URL_ENTITY_TYPE, EntityIdentifier.EntityType.user.name());
		if (startWithActivityStreams) {
			adapterUrl.setParameter(WebKeys.URL_ACTIVITY_STREAMS_SHOW_SITE_WIDE, "1");
		}
		else if (startWithDefaultCollection) {
			adapterUrl.setParameter(WebKeys.URL_SHOW_COLLECTION, String.valueOf(COLLECTION_USER_DEFAULT));
		}
		return adapterUrl.toString();
	}

	public static String getUserPermalink(HttpServletRequest request, String userId) {
		return getUserPermalink(request, userId, false, false);
	}
	
	public static String getPermalink(HttpServletRequest request, DefinableEntity entity) {
		AdaptedPortletURL url = new AdaptedPortletURL(request, "ss_forum", true, true);
		getPermalinkURL(url, entity.getId(), entity.getEntityType());
		return url.toString();
	}
	
	public static String getPermalink(PortletRequest request, DefinableEntity entity) {
		AdaptedPortletURL url = new AdaptedPortletURL(request, "ss_forum", true, true);
		getPermalinkURL(url, entity.getId(), entity.getEntityType());
		return url.toString();
	}
	
	public static String getPermalinkForEmail(DefinableEntity entity, boolean crawler) {
		// For the permalink to be built using the static information
		// from the properties files.
		Boolean oldUseRTContext = ZoneContextHolder.getUseRuntimeContext();
		ZoneContextHolder.setUseRuntimeContext(Boolean.FALSE);
		String reply = getPermalink(entity, crawler);
		ZoneContextHolder.setUseRuntimeContext(oldUseRTContext);
		
		if (forceSecureLinksInEmail()) {
			reply = forceHTTPSInUrl(reply);
		}
		return reply;
	}
	
	public static String getPermalinkForEmail(DefinableEntity entity) {
		return getPermalinkForEmail(entity, false);
	}
	
	public static String getPermalink(DefinableEntity entity) {
		AdaptedPortletURL url = AdaptedPortletURL.createAdaptedPortletURLOutOfWebContext("ss_forum", true);
		getPermalinkURL(url, entity.getId(), entity.getEntityType());
		return url.toString();
	}

	public static String getPermalink(DefinableEntity entity, boolean crawler) {
		AdaptedPortletURL url = AdaptedPortletURL.createAdaptedPortletURLOutOfWebContext("ss_forum", true);
		url.setCrawler(crawler);
		getPermalinkURL(url, entity.getId(), entity.getEntityType());
		return url.toString();
	}
	
	/**
	 * 
	 */
	public static String getPermalink( HttpServletRequest request, Long entityId, EntityIdentifier.EntityType entityType )
	{
		AdaptedPortletURL adapterUrl = new AdaptedPortletURL( request, "ss_forum", true, false );
		getPermalinkURL(adapterUrl, entityId, entityType);
		return adapterUrl.toString();
	}

	public static String getPermalink(Long entityId, EntityIdentifier.EntityType entityType) {
		AdaptedPortletURL adapterUrl = AdaptedPortletURL.createAdaptedPortletURLOutOfWebContext("ss_forum", true);
		getPermalinkURL(adapterUrl, entityId, entityType);
		return adapterUrl.toString();
	}
	public static String getPermalinkLoginUrl(Long entityId, EntityIdentifier.EntityType entityType) {
		AdaptedPortletURL adapterUrl = AdaptedPortletURL.createAdaptedPortletURLOutOfWebContext("ss_forum", true);
		getPermalinkURL(adapterUrl, entityId, entityType);
		adapterUrl.setParameter(WebKeys.URL_LOGIN_URL, String.valueOf(true));
		return adapterUrl.toString();
	}
	public static String getPermalink(Long entityId, EntityIdentifier.EntityType entityType, Boolean captive) {
		AdaptedPortletURL adapterUrl = AdaptedPortletURL.createAdaptedPortletURLOutOfWebContext("ss_forum", true);
		getPermalinkURL(adapterUrl, entityId, entityType);
		if(captive != null)
			adapterUrl.setParameter(WebKeys.URL_CAPTIVE, captive.toString());
		return adapterUrl.toString();
	}
	public static String getSubscribePermalink(Long entityId, EntityIdentifier.EntityType entityType, Boolean captive) {
		AdaptedPortletURL adapterUrl = AdaptedPortletURL.createAdaptedPortletURLOutOfWebContext("ss_forum", true);
		getPermalinkURL(adapterUrl, entityId, entityType);
        adapterUrl.setParameter(WebKeys.URL_INVOKE_SUBSCRIBE, "1");
		if(captive != null)
			adapterUrl.setParameter(WebKeys.URL_CAPTIVE, captive.toString());
		return adapterUrl.toString();
	}

	public static String getSharePermalink(Long entityId, EntityIdentifier.EntityType entityType, Boolean captive) {
		AdaptedPortletURL adapterUrl = AdaptedPortletURL.createAdaptedPortletURLOutOfWebContext("ss_forum", true);
		getPermalinkURL(adapterUrl, entityId, entityType);
        adapterUrl.setParameter(WebKeys.URL_INVOKE_SHARE, "1");
		if(captive != null)
			adapterUrl.setParameter(WebKeys.URL_CAPTIVE, captive.toString());
		return adapterUrl.toString();
	}

    public static String getSharedPublicFileDownloadPermalink(Long shareItemId, String passKey, String fileName) {
        return WebUrlUtil.getSharedPublicFileUrl((HttpServletRequest)null, shareItemId, passKey, WebKeys.URL_SHARE_PUBLIC_LINK, fileName);
    }

    public static String getSharedPublicFileViewPermalink(Long shareItemId, String passKey, String fileName) {
        return WebUrlUtil.getSharedPublicFileUrl((HttpServletRequest)null, shareItemId, passKey, WebKeys.URL_SHARE_PUBLIC_LINK_HTML, fileName);
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
	@SuppressWarnings("unchecked")
	public static String getPermalink(HttpServletRequest request, Map searchResults) {
		AdaptedPortletURL url = new AdaptedPortletURL(request, "ss_forum", true, true);
		getPermalinkURL(url, searchResults);
		return url.toString();
	}
	@SuppressWarnings("unchecked")
	public static String getPermalink(PortletRequest request, Map searchResults) {
		AdaptedPortletURL url = new AdaptedPortletURL(request, "ss_forum", true, true);
		getPermalinkURL(url, searchResults);
		return url.toString();
	}
	@SuppressWarnings("unchecked")
	public static String getPermalink(Map searchResults) {
		AdaptedPortletURL url = AdaptedPortletURL.createAdaptedPortletURLOutOfWebContext("ss_forum", true);
		getPermalinkURL(url, searchResults);
		return url.toString();
	}
	@SuppressWarnings("unchecked")
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
	public static String getFilePermalinkForEmail(FileAttachment attachment) {
		String reply = getFilePermalink(attachment);
		if (forceSecureLinksInEmail()) {
			reply = forceHTTPSInUrl(reply);
		}
		return reply;
	}
	public static String getFilePermalink(FileAttachment attachment) {
		return getFilePermalink(attachment.getOwner().getEntity(), attachment.getFileItem().getName());
		
	}
	public static String getFilePermalink(DefinableEntity entity, String fileName) {
		AdaptedPortletURL adapterUrl = AdaptedPortletURL.createAdaptedPortletURLOutOfWebContext("ss_forum", true);
		getPermalinkURL(adapterUrl, entity.getId(), entity.getEntityType());
		adapterUrl.setParameter(WebKeys.URL_FILE_NAME, WebUrlUtil.urlEncodeFilename(fileName));
		return adapterUrl.toString();	
	}
	public static String getFilePermalink(Long entityId, String entityType, String fileName) {
		EntityType type = null;
		if (EntityType.folder.name().equals(entityType)) type = EntityType.folder;
		if (EntityType.workspace.name().equals(entityType)) type = EntityType.workspace;
		if (EntityType.folderEntry.name().equals(entityType)) type = EntityType.folderEntry;
		AdaptedPortletURL adapterUrl = AdaptedPortletURL.createAdaptedPortletURLOutOfWebContext("ss_forum", true);
		getPermalinkURL(adapterUrl, entityId, type);
		adapterUrl.setParameter(WebKeys.URL_FILE_NAME, WebUrlUtil.urlEncodeFilename(fileName));
		return adapterUrl.toString();	
	}
	@SuppressWarnings("unchecked")
	public static String getFilePermalink(Map searchResults, String fileName) {
		AdaptedPortletURL adapterUrl = AdaptedPortletURL.createAdaptedPortletURLOutOfWebContext("ss_forum", true);
		getPermalinkURL(adapterUrl, searchResults);
		adapterUrl.setParameter(WebKeys.URL_FILE_NAME, WebUrlUtil.urlEncodeFilename(fileName));
		return adapterUrl.toString();
	}

    public static String getFileDownloadPermalink(String fileId, String fileName, Date modDate, Long owningEntityId, EntityIdentifier.EntityType owningEntityType) {
        return WebUrlUtil.getFileUrl(WebUrlUtil.getServletRootURL((HttpServletRequest)null, null), WebKeys.ACTION_READ_FILE, owningEntityId.toString(), owningEntityType.name(),
                fileId, String.valueOf(modDate.getTime()), null,
                fileName, true);
    }

	public static String getTitlePermalink(Long binderId, String normalizedTitle) {
		AdaptedPortletURL adapterUrl = AdaptedPortletURL.createAdaptedPortletURLOutOfWebContext("ss_forum", true);
		adapterUrl.setParameter(WebKeys.ACTION, WebKeys.ACTION_VIEW_PERMALINK);
		adapterUrl.setParameter(WebKeys.URL_BINDER_ID, binderId.toString());
		if (normalizedTitle != null && !normalizedTitle.equals("")) {
			adapterUrl.setParameter(WebKeys.URL_ENTRY_TITLE, normalizedTitle);
			adapterUrl.setParameter(WebKeys.URL_ENTITY_TYPE, EntityType.folderEntry.name());
		} else {
			adapterUrl.setParameter(WebKeys.URL_ENTITY_TYPE, EntityType.folder.name());
		}
		return adapterUrl.toString();
	}
	public static String getTitlePermalink(Long binderId, String zoneUUID, String normalizedTitle) {
		AdaptedPortletURL adapterUrl = AdaptedPortletURL.createAdaptedPortletURLOutOfWebContext("ss_forum", true);
		adapterUrl.setParameter(WebKeys.ACTION, WebKeys.ACTION_VIEW_PERMALINK);
		adapterUrl.setParameter(WebKeys.URL_BINDER_ID, binderId.toString());
		if (Validator.isNotNull(zoneUUID)) adapterUrl.setParameter(WebKeys.URL_ZONE_UUID, zoneUUID);
		if (normalizedTitle != null && !normalizedTitle.equals("")) {
			adapterUrl.setParameter(WebKeys.URL_ENTRY_TITLE, normalizedTitle);
			adapterUrl.setParameter(WebKeys.URL_ENTITY_TYPE, EntityType.folderEntry.name());
		} else {
			adapterUrl.setParameter(WebKeys.URL_ENTITY_TYPE, EntityType.folder.name());
		}
		return adapterUrl.toString();
	}
	@SuppressWarnings("unused")
	private static ProfileModule getProfileModule() {
		return (ProfileModule) SpringContextUtil.getBean("profileModule");
	}

	/**
	 * Returns true if we're to force links in outgoing email messages
	 * to be HTTPS and false if we just use Vibe's default.
	 * 
	 * @return
	 */
	public static boolean forceSecureLinksInEmail() {
		return SPropsUtil.getBoolean("ssf.secure.links.in.email", false);
	}

	/**
	 * Forces the URL passed as a parameter to always be HTTPS using
	 * the configured ssf*.properties values.
	 * 
	 * @param url
	 * 
	 * @return
	 */
	public static String forceHTTPSInUrl(String url) {
		// If we weren't give a URL to fix...
		if (null == url) {
			// ...bail.
			return url;
		}
		url = url.trim();
		if (0 == url.length()) {
			// ...bail.
			return url;
		}

		// If the URL we were given is already HTTPS...
		String lcURL = url.toLowerCase();
		if (lcURL.startsWith("https")) {
			// ...bail.
			return url;
		}

		// To fixup the URL we we're given, what do we need to fix?
		int    ssfHTTPPort   = SPropsUtil.getInt(SPropsUtil.SSF_PORT, Http.HTTP_PORT);
		String ssfHost       = SPropsUtil.getDefaultHost();
		String fixThisNoPort = ("http://" + ssfHost.toLowerCase());
		String fixThisPorted = (fixThisNoPort + ":" + ssfHTTPPort);
		String fixThis;
		if (lcURL.startsWith(fixThisPorted)) {
			fixThis = fixThisPorted;
		}
		else if (lcURL.startsWith(fixThisNoPort)) {
			fixThis = fixThisNoPort;
		}
		else {
			fixThis = null;
			m_logger.warn("PermaLinkUtil.forceHTTPSInUrl( '" + url + "' ):  Fixup failed.");
			return url;
		}

		// Fixup the URL...
		int    ssfHTTPSPort = SPropsUtil.getInt(SPropsUtil.SSF_SECURE_PORT, Http.HTTPS_PORT);
		String fixedUrl     = "https://" + ssfHost + ":" + ssfHTTPSPort + url.substring(fixThis.length());
		
		if (m_logger.isDebugEnabled()) {
			// ..and trace what we did.
			m_logger.debug("PermaLinkUtil.forceHTTPSInUrl():");
			m_logger.debug("...Received:  '" + url      + "'");
			m_logger.debug("...Fixed:  '"    + fixedUrl + "'");
		}

		// If we get here, fixedUrl refers to the final, HTTPS version
		// of the URL we were passed.  Return it.
		return fixedUrl;
	}
}
