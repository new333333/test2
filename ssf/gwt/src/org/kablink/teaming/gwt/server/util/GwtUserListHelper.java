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
package org.kablink.teaming.gwt.server.util;

import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;

import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.domain.CustomAttribute;
import org.kablink.teaming.domain.Definition;
import org.kablink.teaming.domain.Folder;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.domain.UserProperties;
import org.kablink.teaming.gwt.client.GwtTeamingException;
import org.kablink.teaming.gwt.client.rpc.shared.UserListInfoRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.UserListInfoRpcResponseData.UserListInfo;
import org.kablink.teaming.gwt.client.util.BinderInfo;
import org.kablink.teaming.gwt.server.util.GwtViewHelper.UserWorkspacePair;
import org.kablink.teaming.util.AllModulesInjected;
import org.kablink.teaming.util.LongIdUtil;
import org.kablink.teaming.util.NLT;
import org.kablink.teaming.web.util.MiscUtil;

/**
 * Helper methods for User List panels embedded GWT views.
 *
 * @author drfoster@novell.com
 */
public class GwtUserListHelper {
	protected static Log m_logger = LogFactory.getLog(GwtUserListHelper.class);

	/*
	 * Class constructor that prevents this class from being
	 * instantiated.
	 */
	private GwtUserListHelper() {
		// Nothing to do.
	}
	
	/**
	 * Return true of the user list panel should be visible on the
	 * given binder and false otherwise.
	 * 
	 * @param bs
	 * @param request
	 * @param binderId
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException
	 */
	public static Boolean getUserListStatus(AllModulesInjected bs, HttpServletRequest request, Long binderId) throws GwtTeamingException {
		try {
			User			user                 = GwtServerHelper.getCurrentUser();
			UserProperties	userFolderProperties = bs.getProfileModule().getUserProperties(user.getId(), binderId);
			
			// Has the user saved the status of the user list panel
			// on this binder?
			Boolean userListStatus = ((Boolean) userFolderProperties.getProperty(ObjectKeys.USER_PROPERTY_BINDER_SHOW_USER_LIST));
			if (null == userListStatus) {
				// No!  Then we default to show it.  Save this status
				// in the user's properties for this binder.
				userListStatus = Boolean.TRUE;
				saveUserListStatus(bs, request, binderId, userListStatus);
			}
			
			// If we get here, userListStatus contains true if we
			// should show the user list panel and false otherwise.
			// Return it.
			return userListStatus;
		}
		
		catch (Exception e) {
			// Convert the exception to a GwtTeamingException and throw
			// that.
			throw
				GwtLogHelper.getGwtClientException(
					m_logger,
					e,
					"GwtUserListHelper.getUserListStatus( SOURCE EXCEPTION ):  ");
		}
	}
	
	/**
	 * Returns true if a folder's view definition has user_list
	 * <item>'s and false otherwise.
	 * 
	 * @param bs
	 * @param request
	 * @param folderInfo
	 * 
	 * @return
	 */
	public static boolean getFolderHasUserList(AllModulesInjected bs, HttpServletRequest request, BinderInfo folderInfo) {
		GwtServerProfiler gsp = GwtServerProfiler.start(m_logger, "GwtUserListHelper.getFolderHasUserList(1)");
		try {
			// Does the folder have any user_list <item>'s?
			Folder folder = bs.getFolderModule().getFolder(folderInfo.getBinderIdAsLong());
			return getFolderHasUserList(folder);
		}
		
		catch (Exception e) {
			// Log the error and assume there are no user_list's.
			GwtLogHelper.error(m_logger, "GwtUserListHelper.getFolderHasUserList( 1: SOURCE EXCEPTION ):  ", e);
			return false;
		}
		
		finally {
			gsp.stop();
		}
	}
	
	public static boolean getFolderHasUserList(Folder folder) {
		GwtServerProfiler gsp = GwtServerProfiler.start(m_logger, "GwtUserListHelper.getFolderHasUserList(2)");
		try {
			// Does the folder have any user_list <item>'s?
			List<Node> userListNodes = getFolderUserListNodes(folder);
			return MiscUtil.hasItems(userListNodes);
		}
		
		catch (Exception e) {
			// Log the error and assume there are no user_list's.
			GwtLogHelper.error(m_logger, "GwtUserListHelper.getFolderHasUserList( 2: SOURCE EXCEPTION ):  ", e);
			return false;
		}
		
		finally {
			gsp.stop();
		}
	}
	
	/**
	 * Returns a UserListInfoRpcResponseData corresponding to the
	 * user_list <item>'s from a folder view definition.
	 * 
	 * @param bs
	 * @param request
	 * @param folderInfo
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException
	 */
	public static UserListInfoRpcResponseData getFolderUserListInfo(AllModulesInjected bs, HttpServletRequest request, BinderInfo folderInfo) throws GwtTeamingException {
		GwtServerProfiler gsp = GwtServerProfiler.start(m_logger, "GwtUserListHelper.getFolderUserListInfo()");
		try {
			// Allocate a UserListInfoRpcResponseData we can fill in
			// and return.
			UserListInfoRpcResponseData reply = new UserListInfoRpcResponseData();

			// Does the folder have any user_list <item>'s?
			Folder folder = bs.getFolderModule().getFolder(folderInfo.getBinderIdAsLong());
			List<Node> userListNodes = getFolderUserListNodes(folder);
			int userListCount = ((null == userListNodes) ? 0 : userListNodes.size());
			if (0 < userListCount) {
				// Yes!  Scan them.
				for (Node userListNode:  userListNodes) {
					// Determine this <item>'s caption.
					Node captionNode = userListNode.selectSingleNode("properties/property[@name='caption']");
					String caption;
					if (null == captionNode) {
						caption = NLT.get("__user_list");
						int count = reply.getUserListInfoListCount();
						if (0 < count) {
							caption += ("_" + (count + 1));
						}
					}
					else {
						caption = ((Element) captionNode).attributeValue("value");
					}

					// Does this <item> have a data name?
					Node dataNameNode = userListNode.selectSingleNode("properties/property[@name='name']");
					if (null != dataNameNode) {
						// Yes!  Create a UserListInfo for it...
						String dataName = ((Element) dataNameNode).attributeValue("value");
						UserListInfo userListInfo = new UserListInfo(caption, dataName);
						reply.addUserListInfo(userListInfo);
						
						// ...look for this <item>'s user IDs...
						CustomAttribute ca = folder.getCustomAttribute(dataName);
						Set<Long> userIds = ((null == ca) ? null : LongIdUtil.getIdsAsLongSet(ca.getValue().toString(), ","));
						
						// ...and if we find any...
						if (MiscUtil.hasItems(userIds)) {
							// ...add PrincipalInfo's for them to the
							// ...UserListInfo.
							List<UserWorkspacePair> uwsPairs = GwtViewHelper.getUserWorkspacePairs(userIds, null, false);
							GwtViewHelper.getUserInfoFromPIds(bs, request, userListInfo.getUsers(), null ,uwsPairs);
						}
					}
				}
			}
			
			return reply;
		}
		
		catch (Exception e) {
			// Convert the exception to a GwtTeamingException and throw
			// that.
			throw
				GwtLogHelper.getGwtClientException(
					m_logger,
					e,
					"GwtUserListHelper.getFolderUserListInfo( SOURCE EXCEPTION ):  ");
		}
		
		finally {
			gsp.stop();
		}
	}
	
	/*
	 * Returns a List<Node> of the user_list <item>'s from a folder's
	 * view definition.
	 */
	@SuppressWarnings("unchecked")
	private static List<Node> getFolderUserListNodes(Folder folder) {
		// Do we have a Folder?
		List<Node> reply = null;
		if (null != folder) {
			// Yes!  Can we access it's view definition document?
			Definition viewDef    = folder.getDefaultViewDef();
			Document   viewDefDoc = ((null == viewDef) ? null : viewDef.getDefinition());
			if (null != viewDefDoc) {
				// Yes!  Does it contain any user_list <item>'s?
		  		reply = viewDefDoc.selectNodes("//item[@type='form']//item[@name='user_list']");
			}
		}
		
		// If we get here, reply refers to a List<Node> of the folder's
		// user_list <item>'s or is null.  Return it.
		return reply;
	}
	
	/**
	 * Saves whether the user list panel should be visible on the
	 * given binder.
	 * 
	 * @param bs
	 * @param request
	 * @param binderId
	 * @param showUserListPanel
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException
	 */
	public static Boolean saveUserListStatus(AllModulesInjected bs, HttpServletRequest request, Long binderId, boolean showUserListPanel) throws GwtTeamingException {
		try {
			// Save the user list status...
			bs.getProfileModule().setUserProperty(
				GwtServerHelper.getCurrentUserId(),
				binderId,
				ObjectKeys.USER_PROPERTY_BINDER_SHOW_USER_LIST,
				new Boolean(showUserListPanel));
			
			// ...and return true.
			return Boolean.TRUE;
		}
		
		catch (Exception e) {
			// Convert the exception to a GwtTeamingException and throw
			// that.
			throw
				GwtLogHelper.getGwtClientException(
					m_logger,
					e,
					"GwtUserListHelper.saveUserListStatus( SOURCE EXCEPTION ):  ");
		}
	}
}
