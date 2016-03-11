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

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.kablink.teaming.gwt.client.GwtTeamingException;
import org.kablink.teaming.gwt.client.rpc.shared.BooleanRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.DefaultUserSettingsInfoRpcResponseData;
import org.kablink.teaming.module.profile.ProfileModule;
import org.kablink.teaming.util.AllModulesInjected;
import org.kablink.teaming.util.LocaleUtils;

/**
 * Helper methods for default user settings.
 *
 * @author drfoster@novell.com
 */
public class GwtDefaultUserSettingsHelper {
	protected static Log m_logger = LogFactory.getLog(GwtDefaultUserSettingsHelper.class);

	/*
	 * Class constructor that prevents this class from being
	 * instantiated.
	 */
	private GwtDefaultUserSettingsHelper() {
		// Nothing to do.
	}

	/**
	 * Returns a DefaultUserSettingsInfoRpcResponseData containing
	 * information about the default user settings.
	 * 
	 * @param bs
	 * @param req
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException
	 */
	public static DefaultUserSettingsInfoRpcResponseData getDefaultUserSettingsInfo(AllModulesInjected bs, HttpServletRequest req) throws GwtTeamingException {
		GwtServerProfiler gsp = GwtServerProfiler.start(m_logger, "GwtDefaultUserSettingsHelper.getDefaultUserSettingsInfo()");
		try {
			String timeZone    = LocaleUtils.getDefaultTimeZoneId();
			String locale      = LocaleUtils.getDefaultLocale().toString();
			String timeZoneExt = LocaleUtils.getDefaultTimeZoneIdExt();
			String localeExt   = LocaleUtils.getDefaultLocaleExt().toString();
			
			return new DefaultUserSettingsInfoRpcResponseData(timeZone, locale, timeZoneExt, localeExt);
		}
		
		catch (Exception ex) {
			throw GwtLogHelper.getGwtClientException(m_logger, ex);
		}
		
		finally {
			gsp.stop();
		}
	}
	
	/**
	 * Saves the default user settings in the zone config object.
	 * 
	 * @param bs
	 * @param request
	 * @param timeZone
	 * @param locale
	 * @param timeZoneExt
	 * @param localeExt
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException
	 */
	public static BooleanRpcResponseData setDefaultUserSettings(AllModulesInjected bs, HttpServletRequest request, String timeZone, String locale, String timeZoneExt, String localeExt) throws GwtTeamingException {
		GwtServerProfiler gsp = GwtServerProfiler.start(m_logger, "GwtDefaultUserSettingsHelper.detDefaultUserSettings()");
		try {
			ProfileModule pm = bs.getProfileModule();
			pm.setDefaultUserSettings(timeZone, locale, timeZoneExt, localeExt);
			return new BooleanRpcResponseData(true);
		}
		
		catch (Exception ex) {
			throw GwtLogHelper.getGwtClientException(m_logger, ex);
		}
		
		finally {
			gsp.stop();
		}
	}
}
