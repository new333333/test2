/**
 * Copyright (c) 1998-2009 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2009 Novell, Inc. All Rights Reserved.
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
package org.kablink.teaming.bridge;

import java.util.HashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.dao.ProfileDao;
import org.kablink.teaming.domain.NoUserByTheNameException;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.module.binder.impl.WriteEntryDataException;
import org.kablink.teaming.module.file.WriteFilesException;
import org.kablink.teaming.module.profile.ProfileModule;
import org.kablink.teaming.module.shared.MapInputData;
import org.kablink.teaming.security.AccessControlException;
import org.kablink.teaming.util.SPropsUtil;
import org.kablink.teaming.util.SpringContextUtil;


public class ProfileBridge {
	
	private static Log logger = LogFactory.getLog(ProfileBridge.class);

	private static final String PORTAL_PROFILE_DELETE_USER_WORKSPACE = "portal.profile.deleteUserWorkspace";
	private static final boolean PORTAL_PROFILE_DELETE_USER_WORKSPACE_DEFAULT_VALUE = false;
	
	public static void modifyScreenName(String oldScreenName,
			String newScreenName) throws AccessControlException,
			WriteFilesException {
		String zoneName = RequestContextHolder.getRequestContext().getZoneName();

		HashMap map = new HashMap();
		map.put("name", newScreenName);
		map.put("foreignName", newScreenName);

		try {
			User user = getProfileDao().findUserByName(oldScreenName, zoneName);

			getProfileModule().modifyEntry(user.getId(), new MapInputData(map));
		} catch (NoUserByTheNameException e) {
			// The user doesn't exist on the Teaming side.
			// This is possible, so don't throw an error.
			logger.warn(e.toString());
		} catch (WriteEntryDataException e) {
			// The user doesn't exist on the Teaming side.
			// This is possible, so don't throw an error.
			logger.warn(e.toString());
		}
	}
	
	public static void deleteUserByName(String userName) {
		boolean deleteWS = 
			SPropsUtil.getBoolean(PORTAL_PROFILE_DELETE_USER_WORKSPACE, 
				PORTAL_PROFILE_DELETE_USER_WORKSPACE_DEFAULT_VALUE);
		HashMap options = new HashMap();
		options.put(ObjectKeys.INPUT_OPTION_DELETE_USER_WORKSPACE, Boolean.valueOf(deleteWS));
		getProfileModule().deleteUserByName(userName, options);
	}
	
	private static ProfileModule getProfileModule() {
		return (ProfileModule) SpringContextUtil.getBean("profileModule");
	}
	
	private static ProfileDao getProfileDao() {
		return (ProfileDao) SpringContextUtil.getBean("profileDao");
	}
}
