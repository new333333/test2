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
import org.kablink.teaming.util.Utils;
import org.kablink.teaming.web.util.MiscUtil;

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
	private static GwtKeyShieldConfig getGwtKeyShieldConfigFromKeyShieldConfig(KeyShieldConfig keyShieldConfig) {
		GwtKeyShieldConfig config = GwtKeyShieldConfig.getGwtKeyShieldConfig();
		
		config.setApiAuthKey(        keyShieldConfig.getApiAuthKey()             );
		config.setAuthConnectorNames(keyShieldConfig.getAuthConnectorNamesAsSet());
		
		String unaa = keyShieldConfig.getUsernameAttributeAlias();
		if (!(MiscUtil.hasString(unaa))) {
			unaa = (Utils.checkIfFilr() ? "x-filr" : "x-vibe");
		}
		config.setUsernameAttributeAlias(unaa);
		
		Integer timeout = keyShieldConfig.getHttpTimeout();
		if (null == timeout) config.setHttpConnectionTimeout(250    );
		else                 config.setHttpConnectionTimeout(timeout);
		
		config.setIsEnabled(keyShieldConfig.getEnabled()  );
		config.setServerUrl(keyShieldConfig.getServerUrl());
		
		return config;
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

		if (null == keyShieldConfig) config = GwtKeyShieldConfig.getGwtKeyShieldConfig();
		else                         config = getGwtKeyShieldConfigFromKeyShieldConfig( keyShieldConfig );
		
		return config;
	}
	
	/*
	 * Converts a GWT based GwtKeyShieldConfig object to a domain based
	 * KeyShieldConfig object.
	 */
	private static KeyShieldConfig getKeyShieldConfigFromGwtKeyShieldConfig(Long zoneId, GwtKeyShieldConfig config) {
		KeyShieldConfig keyShieldConfig = new KeyShieldConfig(zoneId);
		if (null != config) {
			keyShieldConfig.setApiAuthKey(               config.getApiAuthKey()            );
			keyShieldConfig.setAuthConnectorNamesFromSet(config.getAuthConnectorNames()    );
			keyShieldConfig.setUsernameAttributeAlias(   config.getUsernameAttributeAlias());
			keyShieldConfig.setEnabled(                  config.isEnabled()                );
			keyShieldConfig.setHttpTimeout(              config.getHttpConnectionTimeout() );
			keyShieldConfig.setServerUrl(                config.getServerUrl()             );
		}
		return keyShieldConfig;
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
			KeyShieldConfig keyShieldConfig = getKeyShieldConfigFromGwtKeyShieldConfig(zoneId, config);
			
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
			KeyShieldConfig ksConfig = getKeyShieldConfigFromGwtKeyShieldConfig(
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
