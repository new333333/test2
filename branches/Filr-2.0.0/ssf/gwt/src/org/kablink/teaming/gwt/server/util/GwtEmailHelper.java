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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.EmailAddress;
import org.kablink.teaming.domain.FileAttachment;
import org.kablink.teaming.domain.FolderEntry;
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
import org.kablink.teaming.util.FileIconsHelper;
import org.kablink.teaming.util.IconSize;
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
	 * @param entityId
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException
	 */
	public static EmailNotificationInfoRpcResponseData getEmailNotificationInfo(AllModulesInjected bs, HttpServletRequest request, EntityId entityId) throws GwtTeamingException {
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

			// We're we given an entity ID?
			String singleEntityIconUrl = null;
			String singleEntityPath    = null;
			String singleEntityTitle   = null;
			if (null != entityId) {
				// Yes!  Is it for a binder?
				Subscription sub;
				if (entityId.isBinder()) {
					// Yes!  Does the user have any subscriptions set
					// on this binder?
					BinderModule bm = bs.getBinderModule();
					Binder binder = bm.getBinder(entityId.getEntityId());
					sub = bm.getSubscription(binder);
					
					singleEntityIconUrl = binder.getIconName(IconSize.MEDIUM);
					singleEntityPath    = binder.getPathName();
					singleEntityTitle   = binder.getTitle();
				}
				
				else {
					// No, it isn't for a folder!  It must be for a
					// folder entry.  Does the user have any
					// subscriptions set on that entry?
					FolderModule fm = bs.getFolderModule();
					FolderEntry fe = fm.getEntry(entityId.getBinderId(), entityId.getEntityId());
					sub = fm.getSubscription(fe);
					
					FileAttachment fa = GwtServerHelper.getFileEntrysFileAttachment(bs, fe, true);
					if (null != fa) {
						String fName = fa.getFileItem().getName();
						singleEntityIconUrl = FileIconsHelper.getFileIconFromFileName(fName, IconSize.MEDIUM);
					}
					singleEntityPath  = fe.getParentBinder().getPathName();
					singleEntityTitle = fe.getTitle();
				}

				// Did we get any subscriptions?
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
					if (entityId.isBinder()) {
						reply.setOverridePresets(MiscUtil.hasString(currentStyles.get(String.valueOf(OVERRIDE_STYLE))));
					}
				}
			}

			// Store any single entity information we found.
			reply.setSingleEntityIconUrl(singleEntityIconUrl);
			reply.setSingleEntityPath(   singleEntityPath   );
			reply.setSingleEntityTitle(  singleEntityTitle  );
			
			// If we get here, reply refers to an
			// EmailNotificationInfoRpcResponseData object with the
			// user's subscription information to the entity.
			// Return it.
			return reply;
		}
		
		catch (Exception ex) {
			throw GwtLogHelper.getGwtClientException(ex);
		}
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
			throw
				GwtLogHelper.getGwtClientException(
					m_logger,
					ex,
					"GwtEmailHelper.saveEmailNotificationInfo( SOURCE EXCEPTION ):  ");
		}
	}
	
	/**
	 * Returns true if the user has any email addresses defined and
	 * false otherwise.
	 * 
	 * @param user
	 * 
	 * @return
	 */
	public static boolean userHasEmailAddress(User user) {
		Map<String, EmailAddress> emailAddresses = user.getEmailAddresses();
		return ((null != emailAddresses) && (!(emailAddresses.isEmpty())));
	}
}
