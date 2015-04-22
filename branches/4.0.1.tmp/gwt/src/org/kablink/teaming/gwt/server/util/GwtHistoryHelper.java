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
package org.kablink.teaming.gwt.server.util;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kablink.teaming.gwt.client.GwtTeamingException;
import org.kablink.teaming.gwt.client.admin.GwtAdminAction;
import org.kablink.teaming.gwt.client.rpc.shared.BooleanRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.StringRpcResponseData;
import org.kablink.teaming.gwt.client.util.HistoryInfo;
import org.kablink.teaming.gwt.client.util.HistoryInfo.HistoryActivityStreamInfo;
import org.kablink.teaming.gwt.client.util.HistoryInfo.HistoryAdminActionInfo;
import org.kablink.teaming.gwt.client.util.HistoryInfo.HistoryUrlInfo;
import org.kablink.teaming.util.SPropsUtil;
import org.kablink.teaming.web.util.GwtUISessionData;

/**
 * Helper methods for the GWT UI server code that deals with browser
 * history.
 *
 * @author drfoster@novell.com
 */
public class GwtHistoryHelper {
	protected static Log m_logger = LogFactory.getLog(GwtHistoryHelper.class);
	
	private final static int	MAX_SERVER_HISTORY_CACHE	= SPropsUtil.getInt("max.non.html5.user.history.items", 50);	// Maximum number of history items we track for users running on browsers without HTML5 storage support.
	private final static String	CACHED_HISTORY				= "historyMap";													// Key into the session where we store a user's history.
	
	/*
	 * Inhibits this class from being instantiated. 
	 */
	private GwtHistoryHelper() {
		// Nothing to do.
	}

	/**
	 * Clears the contents of a user's history map.
	 * 
	 * @param request
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException
	 */
	public static BooleanRpcResponseData clearHistory(HttpServletRequest request) throws GwtTeamingException {
		try {
			// Simply remove the user's history map from the session.
			// It will get recreated if something new gets pushed.
			HttpSession session = GwtServerHelper.getCurrentHttpSession();
			session.removeAttribute(CACHED_HISTORY);
			dumpHistoryInfo("server:clear", "all", null);
			return new BooleanRpcResponseData(true);
		}
		
		catch (Exception ex) {
			throw GwtLogHelper.getGwtClientException(
				m_logger,
				ex,
				"GwtHistoryHelper.clearHistory( SOURCE EXCEPTION ):  ");
		}		
	}

	/*
	 * Dumps the contents of a HistoryInfo object.
	 */
	private static void dumpHistoryInfo(String method, String historyToken, HistoryInfo hi) {
		// If debug tracing isn't enabled...
		if (!(GwtLogHelper.isDebugEnabled(m_logger))) {
			// ...bail.
			return;
		}
		
		// If we weren't given a HistoryInfo to dump...
		Long userId = GwtServerHelper.getCurrentUserId();
		if (null == hi) {
			// ...trace that fact and bail.
			GwtLogHelper.debug(m_logger, "dumpHistoryInfo( " + method + ":T=" + historyToken + ":U=" + userId + " ):  No HistoryInfo supplied.");
			return;
		}
		
		// Dump the contents of the HistoryInfo.
		GwtLogHelper.debug(m_logger, "dumpHistoryInfo( " + method + ":T=" + historyToken + ":U=" + userId + " ):");
		GwtLogHelper.debug(m_logger, "...Masthead Collection:  "   + hi.getSelectedMastheadCollection().name());
		GwtLogHelper.debug(m_logger, "...Type:  "                  + hi.getItemType().name());
		switch (hi.getItemType()) {
		case ACTIVITY_STREAM:
			HistoryActivityStreamInfo asInfo = hi.getActivityStreamInfo();
			GwtLogHelper.debug(m_logger, "......Show Setting:  "   + ((null == asInfo.getShowSetting()) ? "*null*" : asInfo.getShowSetting().name()));
			GwtLogHelper.debug(m_logger, "......ActivityStream:  " + asInfo.getActivityStreamInfo().getStreamName());
			GwtLogHelper.debug(m_logger, "......Title:  "          + asInfo.getActivityStreamInfo().getTitle());
			break;

		case ADMIN_ACTION:
			HistoryAdminActionInfo aaInfo = hi.getAdminActionInfo();
			GwtAdminAction adminAction = aaInfo.getAdminAction();
			if (null == adminAction) {
				GwtLogHelper.debug(m_logger, "......Action:  *null*");
			}
			else {
				GwtLogHelper.debug(m_logger, "......Action:  "         +                                                       adminAction.getActionType().name());
				GwtLogHelper.debug(m_logger, "......Localized Name:  " + ((null == adminAction.getLocalizedName() ? "*null*" : adminAction.getLocalizedName()  )));
				GwtLogHelper.debug(m_logger, "......URL:  "            + ((null == adminAction.getUrl()           ? "*null*" : adminAction.getUrl()            )));
			}
			break;
			
		case URL:
			HistoryUrlInfo urlInfo = hi.getUrlInfo();
			GwtLogHelper.debug(m_logger, "......Instigator:  "     + urlInfo.getInstigator().name());
			GwtLogHelper.debug(m_logger, "......URL:  "            + urlInfo.getUrl());
			break;
		
		default:
			GwtLogHelper.debug(m_logger, "......*Internal Error*:  No history item type handler.");
			break;
		}
	}
	
	/**
	 * Dumps a HistoryInfo object to the system log.
	 * 
	 * @param request
	 * @param method
	 * @param historyToken
	 * @param historyInfo
	 * 
	 * @throws GwtTeamingException
	 */
	public static void dumpHistoryInfo(HttpServletRequest request, String method, String historyToken, HistoryInfo historyInfo) throws GwtTeamingException {
		try {
			dumpHistoryInfo(method, historyToken, historyInfo);
		}
		
		catch (Exception ex) {
			throw GwtLogHelper.getGwtClientException(
				m_logger,
				ex,
				"GwtHistoryHelper.dumpHistoryUrl( SOURCE EXCEPTION ):  ");
		}		
	}
	
	/*
	 * Returns the history map from the session, if one exists.
	 * Returns null otherwise.
	 */
	@SuppressWarnings("unchecked")
	private static Map<String, HistoryInfo> getHistoryMap(HttpSession session) {
		Map<String, HistoryInfo> reply;
		GwtUISessionData sessionData = ((GwtUISessionData) session.getAttribute(CACHED_HISTORY));
		if (null == sessionData)
		     reply = null;
		else reply = ((Map<String, HistoryInfo>) sessionData.getData());
		return reply;
	}
	
	/**
	 * Returns a specific HistoryInfo, based on a history token, from
	 * the current user's history map.  If no such HistoryInfo can be
	 * found, null is returned.
	 * 
	 * @param request
	 * @param historyToken
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException
	 */
	public static HistoryInfo getHistoryInfo(HttpServletRequest request, String historyToken) throws GwtTeamingException {
		try {
			HistoryInfo reply;
			HttpSession session = GwtServerHelper.getCurrentHttpSession();
			Map<String, HistoryInfo> historyMap = getHistoryMap(session);
			if (null == historyMap)
			     reply = null;
			else reply = historyMap.get(historyToken);
			
			dumpHistoryInfo("server:get", historyToken, reply);
			return reply;
		}
		
		catch (Exception ex) {
			throw GwtLogHelper.getGwtClientException(
				m_logger,
				ex,
				"GwtHistoryHelper.getHistoryUrl( SOURCE EXCEPTION ):  ");
		}		
	}
	
	/**
	 * Pushes a HistoryInfo into the current user's history map.
	 * 
	 * @param request
	 * @param historyInfo
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException
	 */
	@SuppressWarnings("unchecked")
	public static StringRpcResponseData pushHistoryInfo(HttpServletRequest request, HistoryInfo historyInfo) throws GwtTeamingException {
		try {
			// Does this user have a history map defined?
			HttpSession session = GwtServerHelper.getCurrentHttpSession();
			Map<String, HistoryInfo> historyMap = getHistoryMap(session);
			if (null == historyMap) {
				// No!  Define one now...
				historyMap = new LinkedHashMap<String, HistoryInfo>(
						(MAX_SERVER_HISTORY_CACHE + 1),	// Initial capacity.
						(.75F)) {						// Load factor.
					@Override
				    protected boolean removeEldestEntry(Map.Entry eldest) {
						// This enforces that the map will never exceed
						// its maximum size.
						return (size() > MAX_SERVER_HISTORY_CACHE);
					}
				};
				
				// ...and store it in their session cache.
				session.setAttribute(CACHED_HISTORY, new GwtUISessionData(historyMap));
			}

			// Store the HistoryInfo in the history map.
			String historyToken = String.valueOf(new Date().getTime());
			historyMap.put(historyToken, historyInfo);
			StringRpcResponseData reply = new StringRpcResponseData(historyToken);
			dumpHistoryInfo("server:push", historyToken, historyInfo);
			return reply;
		}
		
		catch (Exception ex) {
			throw GwtLogHelper.getGwtClientException(
				m_logger,
				ex,
				"GwtHistoryHelper.pushHistoryUrl( SOURCE EXCEPTION ):  ");
		}		
	}
}
