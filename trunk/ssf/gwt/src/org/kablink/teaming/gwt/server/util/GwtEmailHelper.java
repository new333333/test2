/**
 * Copyright (c) 1998-2011 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2011 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2011 Novell, Inc. All Rights Reserved.
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
import org.kablink.teaming.domain.Subscription;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.gwt.client.GwtTeamingException;
import org.kablink.teaming.gwt.client.rpc.shared.EmailNotificationInfoRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.EmailNotificationInfoRpcResponseData.EmailAddressInfo;
import org.kablink.teaming.module.binder.BinderModule;
import org.kablink.teaming.util.AllModulesInjected;
import org.kablink.teaming.web.util.MiscUtil;


/**
 * Helper methods for the GWT email handling.
 *
 * @author drfoster@novell.com
 */
public class GwtEmailHelper {
	protected static Log m_logger = LogFactory.getLog(GwtEmailHelper.class);

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

			// Does the user have any subscriptions set on this binder?
			BinderModule bm = bs.getBinderModule();
			Binder binder = bm.getBinder(binderId);
			Subscription sub = bm.getSubscription(binder);
			if (null != sub) {
				// Yes!  Analyze them.
				Map<Integer, String[]> subStyles     = sub.getStyles();
				Map<String,  String>   currentStyles = new HashMap<String, String>();
				for (int i = 1; i < 6; i += 1) {
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
				
				// Apply the user's subscription settings to the response
				// data that we're going to return.
				List<EmailAddressInfo> eaiList = reply.getEmailAddresses();
				for (EmailAddressInfo eai:  eaiList) {
					String ea = eai.getAddress();
					String t = eai.getType();
					if (MiscUtil.hasString(currentStyles.get(1 + t))) reply.setDigestAddress(  ea);
					if (MiscUtil.hasString(currentStyles.get(2 + t))) reply.setMsgAddress(     ea);
					if (MiscUtil.hasString(currentStyles.get(3 + t))) reply.setMsgNoAttAddress(ea);
					if (MiscUtil.hasString(currentStyles.get(5 + t))) reply.setTextAddress(    ea);
				}
				reply.setOverridePresets(MiscUtil.hasString(currentStyles.get("4")));
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
}
