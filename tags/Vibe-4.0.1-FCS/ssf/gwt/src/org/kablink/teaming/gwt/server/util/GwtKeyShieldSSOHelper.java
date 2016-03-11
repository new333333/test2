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

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.domain.KeyShieldConfig;
import org.kablink.teaming.gwt.client.GwtKeyShieldConfig;
import org.kablink.teaming.gwt.client.rpc.shared.SaveKeyShieldConfigRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.TestKeyShieldConnectionResponse;
import org.kablink.teaming.gwt.client.rpc.shared.TestKeyShieldConnectionResponse.GwtKeyShieldConnectionTestStatusCode;
import org.kablink.teaming.util.AllModulesInjected;

/**
 * Helper methods for GWT manipulating KeyShield SSO configurations.
 *
 * @author drfoster@novell.com
 */
public class GwtKeyShieldSSOHelper {
	protected static Log m_logger = LogFactory.getLog(GwtKeyShieldSSOHelper.class);

	/*
	 * Class constructor that prevents this class from being
	 * instantiated.
	 */
	private GwtKeyShieldSSOHelper() {
		// Nothing to do.
	}
	
	/*
	 * Converts a domain based KeyShieldConfig object to a GWT based
	 * GwtKeyShieldConfig object.
	 */
	private static GwtKeyShieldConfig getGwtKSCFromKSC(KeyShieldConfig ksc) {
		GwtKeyShieldConfig gwtKsc = GwtKeyShieldConfig.getGwtKeyShieldConfig();
		
		gwtKsc.setApiAuthKey(        ksc.getApiAuthKey()                );
		gwtKsc.setAuthConnectorNames(ksc.getAuthConnectorNamesAsSet()   );
		gwtKsc.setUsernameAttributeAlias(ksc.getUsernameAttributeAlias());
		
		Integer timeout = ksc.getHttpTimeout();
		if (null == timeout) gwtKsc.setHttpConnectionTimeout(250    );
		else                 gwtKsc.setHttpConnectionTimeout(timeout);
		
		gwtKsc.setIsEnabled(               ksc.getEnabled()                 );
		gwtKsc.setServerUrl(               ksc.getServerUrl()               );
		gwtKsc.setHardwareTokenRequired(   ksc.getHardwareTokenRequired()   );
		gwtKsc.setNonSsoAllowedForLdapUser(ksc.getNonSsoAllowedForLdapUser());
		gwtKsc.setSsoErrorMessageForWeb(   ksc.getSsoErrorMessageForWeb()   );
		gwtKsc.setSsoErrorMessageForWebdav(ksc.getSsoErrorMessageForWebdav());
		
		return gwtKsc;
	}
	
	/**
	 * Returns a GwtKeySHieldConfig object containing the current
	 * KeyShield SSO configuration.
	 * 
	 * @param bs
	 * 
	 * @return
	 */
	public static GwtKeyShieldConfig getKeyShieldConfig(AllModulesInjected bs) {
		KeyShieldConfig keyShieldConfig = null;

		GwtKeyShieldConfig config;
		try {
			keyShieldConfig = bs.getKeyShieldModule().getKeyShieldConfig(RequestContextHolder.getRequestContext().getZoneId());
		}
		catch (Exception ex) {
			ex.printStackTrace();
			config = null;
		}

		if (null == keyShieldConfig) config = null;	// We return null as an indication that there was no value for this zone.
		else                         config = getGwtKSCFromKSC( keyShieldConfig );
		
		return config;
	}
	
	/*
	 * Converts a GWT based GwtKeyShieldConfig object to a domain based
	 * KeyShieldConfig object.
	 */
	private static KeyShieldConfig getKSCFromGwtKSC(Long zoneId, GwtKeyShieldConfig gwtKsc) {
		KeyShieldConfig ksc = new KeyShieldConfig(zoneId);
		if (null != gwtKsc) {
			ksc.setApiAuthKey(               gwtKsc.getApiAuthKey()              );
			ksc.setAuthConnectorNamesFromSet(gwtKsc.getAuthConnectorNames()      );
			ksc.setUsernameAttributeAlias(   gwtKsc.getUsernameAttributeAlias()  );
			ksc.setEnabled(                  gwtKsc.isEnabled()                  );
			ksc.setHttpTimeout(              gwtKsc.getHttpConnectionTimeout()   );
			ksc.setServerUrl(                gwtKsc.getServerUrl()               );
			ksc.setHardwareTokenRequired(    gwtKsc.isHardwareTokenRequired()    );
			ksc.setNonSsoAllowedForLdapUser( gwtKsc.isNonSsoAllowedForLdapUser() );
			ksc.setSsoErrorMessageForWeb(    gwtKsc.getSsoErrorMessageForWeb()   );
			ksc.setSsoErrorMessageForWebdav( gwtKsc.getSsoErrorMessageForWebdav());
		}
		return ksc;
	}
	
	/**
	 * Saves a KeyShield SSO configuration.
	 * 
	 * @param bs
	 * @param config
	 * 
	 * @return
	 */
	public static SaveKeyShieldConfigRpcResponseData saveKeyShieldConfig(AllModulesInjected bs, GwtKeyShieldConfig config) {
		SaveKeyShieldConfigRpcResponseData responseData = new SaveKeyShieldConfigRpcResponseData();
		responseData.setSaveSuccessfull(false);

		try {
			Long zoneId = RequestContextHolder.getRequestContext().getZoneId();
			KeyShieldConfig keyShieldConfig = getKSCFromGwtKSC(zoneId, config);
			
			bs.getKeyShieldModule().saveKeyShieldConfig(zoneId, keyShieldConfig);
			responseData.setSaveSuccessfull(true);
		}
		
		catch (Exception ex) {
			ex.printStackTrace();
		}

		return responseData;
	}

	/**
	 * Test the connection for the given KeyShield SSO configuration.
	 * 
	 * @param bs
	 * @param gwtConfig
	 * 
	 * @return
	 */
	public static TestKeyShieldConnectionResponse testKeyShieldConnection(AllModulesInjected bs, GwtKeyShieldConfig gwtConfig) {
		TestKeyShieldConnectionResponse response = new TestKeyShieldConnectionResponse();
		
		try {
			KeyShieldConfig ksConfig = getKSCFromGwtKSC(
				RequestContextHolder.getRequestContext().getZoneId(),
				gwtConfig );
			
			bs.getKeyShieldModule().testConnection(ksConfig);
			response.setStatusCode(GwtKeyShieldConnectionTestStatusCode.NORMAL);
		}
		
		catch (Exception ex) {
			response.setStatusCode(GwtKeyShieldConnectionTestStatusCode.FAILED);
			
			// Capture a description of the exception.
			Throwable cause = ex.getCause();
			String desc;
			if (null != cause) desc = cause.getMessage();
			else               desc = ex.getMessage();
			response.setStatusDescription( desc );
			
			// Capture the stack trace.
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			PrintStream ps = new PrintStream( baos );
			ex.printStackTrace(ps);
			response.setStackTrace(baos.toString());
		}

		return response;
	}
}
