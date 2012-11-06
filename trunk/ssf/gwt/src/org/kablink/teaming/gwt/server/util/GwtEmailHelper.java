/**
 * Copyright (c) 1998-2012 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2012 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2012 Novell, Inc. All Rights Reserved.
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.DefinableEntity;
import org.kablink.teaming.domain.EmailAddress;
import org.kablink.teaming.domain.FolderEntry;
import org.kablink.teaming.domain.GroupPrincipal;
import org.kablink.teaming.domain.Principal;
import org.kablink.teaming.domain.ShareItem;
import org.kablink.teaming.domain.Subscription;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.gwt.client.GwtTeamingException;
import org.kablink.teaming.gwt.client.rpc.shared.BooleanRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.EmailNotificationInfoRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.EmailNotificationInfoRpcResponseData.EmailAddressInfo;
import org.kablink.teaming.gwt.client.util.EntityId;
import org.kablink.teaming.module.binder.BinderModule;
import org.kablink.teaming.module.folder.FolderModule;
import org.kablink.teaming.util.AllModulesInjected;
import org.kablink.teaming.util.ResolveIds;
import org.kablink.teaming.util.SPropsUtil;
import org.kablink.teaming.web.util.MiscUtil;

/**
 * Helper methods for the GWT email handling.
 *
 * @author drfoster@novell.com
 */
public class GwtEmailHelper {
	protected static Log m_logger = LogFactory.getLog(GwtEmailHelper.class);

	// The style index used for various email address types.
	private static final int DIGEST_STYLE		= 1;
	private static final int MSG_STYLE			= 2;
	private static final int MSG_NOATT_STYLE	= 3;
	private static final int OVERRIDE_STYLE		= 4;
	private static final int TEXT_STYLE			= 5;
	private static final int STYLES				= 6;	// Why 6?  Who knows!  It wasn't documented in subscribe_return.jsp.
	
	/*
	 * Class constructor that prevents this class from being
	 * instantiated.
	 */
	private GwtEmailHelper() {
		// Nothing to do.
	}

	/**
	 * Returns an EmailNotificationInfoRpcResponseData object
	 * containing the email notification information for the current
	 * user on the specified binder.
	 * 
	 * Note:  The logic used by this method was reverse engineered from
	 * that used by subscribe_return.jsp.
	 * 
	 * @param bs
	 * @param request
	 * @param binderId
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException
	 */
	public static EmailNotificationInfoRpcResponseData getEmailNotificationInfo(AllModulesInjected bs, HttpServletRequest request, Long binderId) throws GwtTeamingException {
		try {
			// Construct an EmailNotificationInfoRpcResponseData
			// object to return.
			EmailNotificationInfoRpcResponseData reply = new EmailNotificationInfoRpcResponseData();
			reply.setBannerHelpUrl(  MiscUtil.getHelpUrl("user", "informed_notifications", null)                             );
			reply.setOverrideHelpUrl(MiscUtil.getHelpUrl("user", "informed_notifications", "informed_notifications_override"));

			// Does the user have any email addresses defined?
			User user = GwtServerHelper.getCurrentUser();
			Map<String, EmailAddress> emailAddresses = user.getEmailAddresses();
			if ((null != emailAddresses) && (!(emailAddresses.isEmpty()))) {
				// Yes!  Scan them...
				for (String type: emailAddresses.keySet()) {
					// ...adding an EmailAddressInfo for each to the
					// ...response.
					reply.addEmailAddress(type, emailAddresses.get(type).getAddress());
				}
			}

			// We're we given a binder ID?
			if (null != binderId) {
				// Yes!  Does the user have any subscriptions set on
				// this binder?
				BinderModule bm = bs.getBinderModule();
				Binder binder = bm.getBinder(binderId);
				Subscription sub = bm.getSubscription(binder);
				if (null != sub) {
					// Yes!  Analyze them.
					Map<Integer, String[]> subStyles     = sub.getStyles();
					Map<String,  String>   currentStyles = new HashMap<String, String>();
					for (int i = 1; i < STYLES; i += 1) {
						String[] types = subStyles.get(Integer.valueOf(i));
						if (null == types) {
							continue;
						}
						
						if (0 == types.length) {
							currentStyles.put(String.valueOf(i), "x");
						}
						
						else {
							for (int j = 0; j < types.length; j += 1) {
								currentStyles.put(i + types[j], "x");
							}
						}
					}
					
					// Apply the user's subscription settings to the
					// response data that we're going to return.
					List<EmailAddressInfo> eaiList = reply.getEmailAddresses();
					for (EmailAddressInfo eai:  eaiList) {
						String ea = eai.getAddress();
						String t = eai.getType();
						if (MiscUtil.hasString(currentStyles.get(DIGEST_STYLE    + t))) reply.addDigestAddress(  ea);
						if (MiscUtil.hasString(currentStyles.get(MSG_STYLE       + t))) reply.addMsgAddress(     ea);
						if (MiscUtil.hasString(currentStyles.get(MSG_NOATT_STYLE + t))) reply.addMsgNoAttAddress(ea);
						if (MiscUtil.hasString(currentStyles.get(TEXT_STYLE      + t))) reply.addTextAddress(    ea);
					}
					reply.setOverridePresets(MiscUtil.hasString(currentStyles.get(String.valueOf(OVERRIDE_STYLE))));
				}
			}
			
			
			// If we get here, reply refers to an
			// EmailNotificationInfoRpcResponseData object with the
			// user's subscription information to this binder.
			// Return it.
			return reply;
		}
		
		catch (Exception ex) {
			throw GwtServerHelper.getGwtTeamingException(ex);
		}
	}

	/*
	 * Scans the IDs in the given collection for any that resolve to an
	 * all users group and returns a collection without them.
	 */
	@SuppressWarnings("unchecked")
	private static Collection<Long> removeAllUserGroups(Collection<Long> principalIds) {
		// Are there any IDs in the collection we were given?
		if (MiscUtil.hasItems(principalIds)) {
			// Yes!  Are there any that resolve?
			List<Principal> principalList = ResolveIds.getPrincipals(principalIds);
			if (MiscUtil.hasItems(principalList)) {
				// Yes!  Scan them.
				List<Long> allUsersList = new ArrayList<Long>();
				for (Principal p:  principalList) {
					// Is this Principal a group?
					if (p instanceof GroupPrincipal) {
						// Yes!  Is it an all users group?
						String internalId = p.getInternalId();
						if ((null != internalId) &&
								(internalId.equalsIgnoreCase(ObjectKeys.ALL_USERS_GROUP_INTERNALID) ||
								 internalId.equalsIgnoreCase(ObjectKeys.ALL_EXT_USERS_GROUP_INTERNALID))) {
							// Yes!  Add its ID to the list of those
							// we're tracking.
							allUsersList.add(p.getId());
						}
					}
				}

				// Are we tracking any all user groups that are being
				// sent to?
				if (!(allUsersList.isEmpty())) {
					// Yes!  Scan the original principal IDs...
					List<Long> nonAllUserIds = new ArrayList<Long>();
					for (Long pId:  principalIds) {
						// ...tracking those that aren't all user
						// ...groups...
						if (!(allUsersList.contains(pId))) {
							nonAllUserIds.add(pId);
						}
					}
					
					// ...and use the new collection.  We do this to
					// ...avoid any side affects related to changing
					// ...the initial collection we were given.
					principalIds = nonAllUserIds;
				}
			}
		}
		
		// If we get here, principalIds now refers to a collection
		// without any all user groups.  Return it.
		return principalIds;
	}
	
	/**
	 * Save the email notification information for the current user
	 * on the specified binder.
	 * 
	 * @param bs
	 * @param request
	 * @param binderId
	 * @param overridePresets
	 * @param digestAddressTypes
	 * @param msgAddressTypes
	 * @param msgNoAttAddressTypes
	 * @param textAddressTypes
	 * 
	 * Note:  The logic used by this method was based on the used by
	 * AjaxController.ajaxDoSubscription().
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException
	 */
	public static BooleanRpcResponseData saveEmailNotificationInfo(
		AllModulesInjected	bs,						//
		HttpServletRequest	request,				//
		Long				binderId,				// null -> Entity subscription mode.
		List<EntityId>		entityIds,				// null -> Binder email notification mode.
		boolean				overridePresets,		//
		List<String>		digestAddressTypes,		//
		List<String>		msgAddressTypes,		//
		List<String>		msgNoAttAddressTypes,	//
		List<String>		textAddressTypes)		//
			throws GwtTeamingException
	{
		try {
			// Allocate a map to hold the new subscription information.
			Map<Integer, String[]> saveStyles = new HashMap<Integer, String[]>();
			
			// If the override checkbox was checked...
			if (overridePresets) {
				// ...add that to the styles map.
				saveStyles.put(Subscription.DISABLE_ALL_NOTIFICATIONS, null);
			}
			
			// Scan the possible styles.
			for (int i = 1; i < STYLES; i += 1) {
				// Are there any email addresses defined for this
				// style?
				List<String> emaTypeList;
				switch (i) {
				case DIGEST_STYLE:     emaTypeList = digestAddressTypes;   break;
				case MSG_STYLE:        emaTypeList = msgAddressTypes;      break;
				case MSG_NOATT_STYLE:  emaTypeList = msgNoAttAddressTypes; break;
				case TEXT_STYLE:       emaTypeList = textAddressTypes;     break;
				default:               continue;
				}
				String[] address = emaTypeList.toArray(new String[0]);
				if ((null == address) || (0 == address.length)) {
					// No!  Skip it.
					continue;
				}
				
				// Yes, add them to the styles map.
				saveStyles.put(Integer.valueOf(i), address);
			}

			// Are we saving email notification settings on a binder?
			if (null != binderId) {
				// Yes!  Save the new styles map as the user's
				// subscription information on that binder.
				bs.getBinderModule().setSubscription(binderId, saveStyles);
			}
			
			else {
				// No, we aren't saving email notification settings on
				// a binder!  We must be saving them for entries.  Scan
				// the entries.
				BinderModule bm = bs.getBinderModule();
				FolderModule fm = bs.getFolderModule();
				for (EntityId entityId:  entityIds) {
					// Access the entity and subscriptions we're
					// working on.
					boolean			isBinder = entityId.isBinder();
					Long			bId      = entityId.getBinderId();
					Long			eId      = entityId.getEntityId();
					Binder			binder;
					FolderEntry		fe;
					Subscription	sub;
					if (entityId.isBinder()) {
						binder = bm.getBinder(      eId   );
						sub    = bm.getSubscription(binder);
					}
					else {
						fe  = fm.getEntry(bId,   eId);
						sub = fm.getSubscription(fe );
					}
					
					// Scan the email addresses we need to save for
					// this entity.
					Map<Integer, String[]> entityStyles = new HashMap<Integer, String[]>();
					for (Integer key:  saveStyles.keySet()) {
						// Is this email address saying don't change
						// what's currently there for this user on this
						// entity?
						String[] emas = saveStyles.get(key);
						if ((null != emas) && (1 == emas.length) && ("*no-change*".equals(emas[0]))) {
							// Yes!  If the user has anything there,
							// use it, otherwise, don't use anything.
							if ((null != sub) && sub.hasStyle(key))
								 entityStyles.put(key, sub.getEmailTypes(key));
							else entityStyles.put(key, new String[0]);
						}
						else {
							// No, this isn't an email address saying
							// don't change what's currently there!
							// Use what we were given.
							entityStyles.put(key, emas);
						}
							
					}
					
					// When we get here, entityStyles contains the
					// appropriate settings for this entity for the
					// user.  Save it.
					if (isBinder)
					     bm.setSubscription(     eId, entityStyles);
					else fm.setSubscription(bId, eId, entityStyles);
				}
			}
			
			// Regardless of what happened, if we get here, we always
			// return true.
			return new BooleanRpcResponseData(Boolean.TRUE);
		}
		
		catch (Exception ex) {
			// Convert the exception to a GwtTeamingException and throw
			// that.
			if ((!(GwtServerHelper.m_logger.isDebugEnabled())) && m_logger.isDebugEnabled()) {
			     m_logger.debug("GwtEmailHelper.saveEmailNotificationInfo( SOURCE EXCEPTION ):  ", ex);
			}
			throw GwtServerHelper.getGwtTeamingException(ex);
		}
	}

	/**
	 * Send a share notification mail message to a collection of users
	 * and/or explicit email addresses.
	 * 
	 * @param bs				- Access to modules.
	 * @param share				- Share item.
	 * @param sharedEntity		- Entity (folder or folder entry) being shared.
	 * @param principalIds		- toList,  users and groups
	 * @param teamIds			- toList,  teams.
	 * @param emailAddresses	- toList,  stand alone email address.
	 * @param ccIds				- ccList,  users and groups
	 * @param bccIds			- bccList, users and groups
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException
	 */
	public static Map<String, Object> sendShareNotification(
		AllModulesInjected	bs,				//
		ShareItem			share,			//
		DefinableEntity		sharedEntity,	//
		Collection<Long>	principalIds,	//
		Collection<Long>	teamIds,		//
		Collection<String>	emailAddresses,	//
		Collection<Long>	ccIds, 			//
		Collection<Long>	bccIds)			//
			throws GwtTeamingException
	{
		try {
			// Is sending email to an all user group allowed?
			if (!(SPropsUtil.getBoolean("mail.allowSendToAllUsers", false))) {
				// No!  Remove any that we're being asked to send to.
				principalIds = removeAllUserGroups(principalIds);
				ccIds        = removeAllUserGroups(ccIds      );
				bccIds       = removeAllUserGroups(bccIds     );
			}

			// Are there any actual targets for the email notification?
			boolean hasTargets = (
				MiscUtil.hasItems(principalIds)   ||
				MiscUtil.hasItems(teamIds)        ||
				MiscUtil.hasItems(emailAddresses) ||
				MiscUtil.hasItems(ccIds)          ||
				MiscUtil.hasItems(bccIds));

			Map<String, Object> reply;
			if (hasTargets) {
				// Yes!  Send it.
				reply = bs.getAdminModule().sendMail(
					share,
					sharedEntity,
					principalIds,
					teamIds,
					emailAddresses,
					ccIds,
					bccIds);
			}
			else {
				// No, there aren't any targets!  Return an empty
				// reply.
				reply = new HashMap<String, Object>();
			}
			
			// If we get here, reply contains a map of the results of
			// the email notification.  Return it.
			return reply;
		}
		
		catch (Exception ex) {
			// Convert the exception to a GwtTeamingException and throw
			// that.
			if ((!(GwtServerHelper.m_logger.isDebugEnabled())) && m_logger.isDebugEnabled()) {
			     m_logger.debug("GwtEmailHelper.sendShareNotification( SOURCE EXCEPTION ):  ", ex);
			}
			throw GwtServerHelper.getGwtTeamingException(ex);
		}
	}
}
