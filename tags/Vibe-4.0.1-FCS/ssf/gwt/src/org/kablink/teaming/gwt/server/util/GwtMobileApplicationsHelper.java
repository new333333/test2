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

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.domain.MobileAppsConfig;
import org.kablink.teaming.domain.MobileOpenInWhiteLists;
import org.kablink.teaming.domain.Principal;
import org.kablink.teaming.domain.ZoneConfig;
import org.kablink.teaming.domain.MobileAppsConfig.MobileOpenInSetting;
import org.kablink.teaming.gwt.client.GwtPrincipalMobileAppsConfig;
import org.kablink.teaming.gwt.client.GwtTeamingException;
import org.kablink.teaming.gwt.client.GwtZoneMobileAppsConfig;
import org.kablink.teaming.gwt.client.rpc.shared.SavePrincipalMobileAppsConfigRpcResponseData;
import org.kablink.teaming.gwt.client.util.GwtMobileOpenInSetting;
import org.kablink.teaming.module.profile.ProfileModule;
import org.kablink.teaming.module.zone.ZoneModule;
import org.kablink.teaming.util.AllModulesInjected;
import org.kablink.teaming.util.NLT;
import org.kablink.teaming.util.PrincipalMobileAppsConfig;
import org.kablink.teaming.web.util.MiscUtil;

/**
 * Helper methods for the GWT UI server code in dealing with mobile
 * applications.
 *
 * @author drfoster@novell.com
 */
public class GwtMobileApplicationsHelper {
	protected static Log m_logger = LogFactory.getLog(GwtMobileApplicationsHelper.class);
	
	/*
	 * Inhibits this class from being instantiated. 
	 */
	private GwtMobileApplicationsHelper() {
		// Nothing to do.
	}
	
	/**
	 * Return a GwtMobileAppsConfiguration object that holds the mobile
	 * application configuration data.
	 * 
	 * @param bs
	 * @param request
	 * 
	 * @return
	 */
	public static GwtZoneMobileAppsConfig getMobileAppsConfiguration(AllModulesInjected bs, HttpServletRequest request) {
		ZoneModule zm = bs.getZoneModule();
		ZoneConfig zc = zm.getZoneConfig(RequestContextHolder.getRequestContext().getZoneId());
		MobileAppsConfig mobileAppsConfig = zc.getMobileAppsConfig();
		
		// Get the whether mobile applications are enabled.
		GwtZoneMobileAppsConfig gwtMobileAppsConfig = new GwtZoneMobileAppsConfig();
		gwtMobileAppsConfig.setMobileAppsEnabled(mobileAppsConfig.getMobileAppsEnabled());
		
		// Get the setting that determines whether the mobile
		// applications can remember the password.
		gwtMobileAppsConfig.setAllowCachePwd(mobileAppsConfig.getMobileAppsAllowCachePwd());
		
		// Get the setting that determines if mobile applications can
		// cache content.
		gwtMobileAppsConfig.setAllowCacheContent(mobileAppsConfig.getMobileAppsAllowCacheContent());

		// Get the setting that determines if the mobile applications
		// can play with other applications.
		gwtMobileAppsConfig.setAllowPlayWithOtherApps(mobileAppsConfig.getMobileAppsAllowPlayWithOtherApps());
		
		// Get the setting that determines if the mobile applications
		// should force the user to enter their PIN code.
		gwtMobileAppsConfig.setForcePinCode(mobileAppsConfig.getMobileAppsForcePinCode());

		// Get the Mobile applications sync interval.
		gwtMobileAppsConfig.setSyncInterval(mobileAppsConfig.getMobileAppsSyncInterval());
		
		// Get the Mobile Application Management (MAM) settings.
		gwtMobileAppsConfig.setMobileCutCopyEnabled(                    mobileAppsConfig.getMobileCutCopyEnabled()                    );
		gwtMobileAppsConfig.setMobileAndroidScreenCaptureEnabled(       mobileAppsConfig.getMobileAndroidScreenCaptureEnabled()       );
		gwtMobileAppsConfig.setMobileDisableOnRootedOrJailBrokenDevices(mobileAppsConfig.getMobileDisableOnRootedOrJailBrokenDevices());
		GwtMobileOpenInSetting gwtMoi;
		MobileOpenInSetting moi = mobileAppsConfig.getMobileOpenInEnum();
		if (null == moi) {
			gwtMoi = GwtMobileOpenInSetting.ALL_APPLICATIONS;
		}
		
		else {
			switch (moi) {
			default:
			case ALL_APPLICATIONS:  gwtMoi = GwtMobileOpenInSetting.ALL_APPLICATIONS; break;
			case DISABLED:          gwtMoi = GwtMobileOpenInSetting.DISABLED;         break;
			case WHITE_LIST:        gwtMoi = GwtMobileOpenInSetting.WHITE_LIST;       break;
			}
		}
		gwtMobileAppsConfig.setMobileOpenIn(gwtMoi);
		
		List<String> androidApplications;
		List<String> iosApplications;
		MobileOpenInWhiteLists mwl = mobileAppsConfig.getMobileOpenInWhiteLists();
		if (null == mwl) {
			androidApplications =
			iosApplications     = null;
		}
		else {
			androidApplications = mwl.getAndroidApplications();
			iosApplications     = mwl.getIosApplications();
		}
		if (null == androidApplications) androidApplications = new ArrayList<String>();
		if (null == iosApplications    ) iosApplications     = new ArrayList<String>();
		gwtMobileAppsConfig.setAndroidApplications(MiscUtil.sortStringList(androidApplications));
		gwtMobileAppsConfig.setIosApplications(    MiscUtil.sortStringList(iosApplications    ));
		
		return gwtMobileAppsConfig;
	}
	
	/**
	 * Return a GwtPrincipalMobileAppsConfig object that holds the
	 * mobile application configuration data for the given user or
	 * group.
	 * 
	 * @param bs
	 * @param request
	 * @param principalId
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException
	 */
	public static GwtPrincipalMobileAppsConfig getPrincipalMobileAppsConfig(AllModulesInjected bs, HttpServletRequest request, Long principalId) throws GwtTeamingException {
		try {
			PrincipalMobileAppsConfig pConfig = bs.getProfileModule().getPrincipalMobileAppsConfig(principalId);
			
			GwtPrincipalMobileAppsConfig reply = new GwtPrincipalMobileAppsConfig();
			boolean useDefault = pConfig.getUseDefaultSettings();
			reply.setUseGlobalSettings(useDefault);
			if (!useDefault) {
				reply.setMobileAppsEnabled(                          pConfig.getMobileAppsEnabled()                       );
				reply.setAllowCachePwd(                              pConfig.getAllowCachePwd()                           );
				reply.setAllowCacheContent(                          pConfig.getAllowCacheContent()                       );
				reply.setAllowPlayWithOtherApps(                     pConfig.getAllowPlayWithOtherApps()                  );
				reply.setForcePinCode(                               pConfig.getForcePinCode()                            );
				reply.setMobileCutCopyEnabled(                       pConfig.getMobileCutCopyEnabled()                    );
				reply.setMobileAndroidScreenCaptureEnabled(          pConfig.getMobileAndroidScreenCaptureEnabled()       );
				reply.setMobileDisableOnRootedOrJailBrokenDevices(   pConfig.getMobileDisableOnRootedOrJailBrokenDevices());
				reply.setMobileOpenIn(GwtMobileOpenInSetting.valueOf(pConfig.getMobileOpenIn().ordinal() )                );
				reply.setAndroidApplications(                        pConfig.getAndroidApplications()                     );
				reply.setIosApplications(                            pConfig.getIosApplications()                         );
			}
			
			return reply;
		}
		
		catch (Exception ex) {
			// Convert the exception to a GwtTeamingException and throw
			// that.
			throw
				GwtLogHelper.getGwtClientException(
					m_logger,
					ex,
					"GwtMobileApplicationsHelper.getPrincipalMobileAppsConfig( SOURCE EXCEPTION ):  ");
		}
	}
	
	/**
	 * Save the given Mobile Applications Configuration.
	 * 
	 * @param bs
	 * @param request
	 * @param gwtMobileAppsConfig
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException
	 */
	public static Boolean saveMobileAppsConfiguration(AllModulesInjected bs, HttpServletRequest request, GwtZoneMobileAppsConfig gwtMobileAppsConfig) throws GwtTeamingException {
		try {
			MobileAppsConfig mobileAppsConfig = new MobileAppsConfig();
			mobileAppsConfig.setMobileAppsAllowCacheContent(       gwtMobileAppsConfig.getAllowCacheContent()     );
			mobileAppsConfig.setMobileAppsAllowCachePwd(           gwtMobileAppsConfig.getAllowCachePwd()         );
			mobileAppsConfig.setMobileAppsAllowPlayWithOtherApps(  gwtMobileAppsConfig.getAllowPlayWithOtherApps());
			mobileAppsConfig.setMobileAppsForcePinCode(            gwtMobileAppsConfig.getForcePinCode()          );
			mobileAppsConfig.setMobileAppsEnabled(                 gwtMobileAppsConfig.getMobileAppsEnabled()     );
			mobileAppsConfig.setMobileAppsSyncInterval(new Integer(gwtMobileAppsConfig.getSyncInterval())         );

			// Save the various Mobile Application Management (MAM)
			// settings.
			mobileAppsConfig.setMobileCutCopyEnabled(                    gwtMobileAppsConfig.getMobileCutCopyEnabled()                    );
			mobileAppsConfig.setMobileAndroidScreenCaptureEnabled(       gwtMobileAppsConfig.getMobileAndroidScreenCaptureEnabled()       );
			mobileAppsConfig.setMobileDisableOnRootedOrJailBrokenDevices(gwtMobileAppsConfig.getMobileDisableOnRootedOrJailBrokenDevices());
			GwtMobileOpenInSetting gwtMoi = gwtMobileAppsConfig.getMobileOpenIn();
			MobileOpenInSetting moi;
			if (null == gwtMoi) {
				moi = MobileOpenInSetting.ALL_APPLICATIONS;
			}
			else {
				switch (gwtMoi) {
				default:
				case ALL_APPLICATIONS:  moi = MobileOpenInSetting.ALL_APPLICATIONS; break;
				case DISABLED:          moi = MobileOpenInSetting.DISABLED;         break;
				case WHITE_LIST:        moi = MobileOpenInSetting.WHITE_LIST;       break;
				}
			}
			mobileAppsConfig.setMobileOpenInEnum(moi);
			
			MobileOpenInWhiteLists mwl = mobileAppsConfig.getMobileOpenInWhiteLists();
			if (null == mwl) {
				mwl = new MobileOpenInWhiteLists();
				mobileAppsConfig.setMobileOpenInWhiteLists(mwl);
			}
			mwl.setMobileOpenInWhiteLists(
				MiscUtil.sortStringList(gwtMobileAppsConfig.getAndroidApplications()),
				MiscUtil.sortStringList(gwtMobileAppsConfig.getIosApplications()   ));
			
			bs.getAdminModule().setMobileAppsConfig(mobileAppsConfig);
			return Boolean.TRUE;
		}
		
		catch (Exception e) {
			// Convert the exception to a GwtTeamingException and throw
			// that.
			throw
				GwtLogHelper.getGwtClientException(
					m_logger,
					e,
					"GwtMobileApplicationsHelper.saveMobileAppsConfiguration( SOURCE EXCEPTION ):  ");
		}
	}
	
	/**
	 * Save the given GwtPrincipalMobileAppsConfig settings for the
	 * given users or groups.
	 *
	 * @param bs
	 * @param request
	 * @param config
	 * @param principalIds
	 * @param principalsAreUsers
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException
	 */
	public static SavePrincipalMobileAppsConfigRpcResponseData savePrincipalMobileAppsConfig(AllModulesInjected bs, HttpServletRequest request, GwtPrincipalMobileAppsConfig config, List<Long> principalIds, boolean principalsAreUsers) throws GwtTeamingException {
		try {
			SavePrincipalMobileAppsConfigRpcResponseData responseData = new SavePrincipalMobileAppsConfigRpcResponseData();
			if ((null == config) || (!(MiscUtil.hasItems(principalIds)))) {
				responseData.addError("Invalid parameters passed to savePrincipalMobileAppsConfig()");
				return responseData;
			}

			// Map the GWT based configuration to a non-GWT one.
			PrincipalMobileAppsConfig pConfig = new PrincipalMobileAppsConfig();
			boolean useDefault = config.getUseGlobalSettings();
			pConfig.setUseDefaultSettings(useDefault);
			if (!useDefault) {
				pConfig.setMobileAppsEnabled(     config.getMobileAppsEnabled()     );
				pConfig.setAllowCachePwd(         config.getAllowCachePwd()         );
				pConfig.setAllowCacheContent(     config.getAllowCacheContent()     );
				pConfig.setAllowPlayWithOtherApps(config.getAllowPlayWithOtherApps());
				pConfig.setForcePinCode(          config.getForcePinCode()          );
				
				// Mobile Application Management (MAM) settings.
				pConfig.setMobileCutCopyEnabled(                    config.getMobileCutCopyEnabled()                    );
				pConfig.setMobileAndroidScreenCaptureEnabled(       config.getMobileAndroidScreenCaptureEnabled()       );
				pConfig.setMobileDisableOnRootedOrJailBrokenDevices(config.getMobileDisableOnRootedOrJailBrokenDevices());
				pConfig.setMobileOpenIn(MobileOpenInSetting.valueOf(config.getMobileOpenIn().ordinal())                 );
				pConfig.setAndroidApplications(                     config.getAndroidApplications()                     );
				pConfig.setIosApplications(                         config.getIosApplications()                         );
			}

			ProfileModule pm = bs.getProfileModule();
			for (Long pId:  principalIds) {
				try {
					// We write them individually so that we can capture
					// errors individually.
					pm.savePrincipalMobileAppsConfig(pId, principalsAreUsers, pConfig);
				}
				
				catch (Exception ex) {
					// Save the error in the response...
					Principal p = pm.getEntry(pId);
					String cause;
					if (p.isDisabled()) {
						String key;
						if (principalsAreUsers)
						     key = "save.user.mobile.app.config.error.disabled.user";
						else key = "save.user.mobile.app.config.error.disabled.group";
						cause = NLT.get(key);
					}
					else {
						cause = ex.getLocalizedMessage();
					}
					String[] errorArgs = new String[] {p.getTitle(), cause};
					String errMsg = NLT.get("save.user.mobile.app.config.error", errorArgs);
					responseData.addError( errMsg );

					// ...and log it.
					GwtLogHelper.error(
						m_logger,
						"GwtMobileApplicationsHelper.savePrincipalMobileAppConfig( EXCEPTION ):  ",
						ex);
				}
			}
			
			return responseData;
		}
		
		catch (Exception ex) {
			// Convert the exception to a GwtTeamingException and throw
			// that.
			throw
				GwtLogHelper.getGwtClientException(
					m_logger,
					ex,
					"GwtMobileApplicationsHelper.savePrincipalMobileAppsConfig( SOURCE EXCEPTION ):  ");
		}
	}
}
