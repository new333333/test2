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

import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.domain.AntiVirusConfig;
import org.kablink.teaming.gwt.client.GwtTeamingException;
import org.kablink.teaming.gwt.client.rpc.shared.AntiVirusSettingsRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.TestAntiVirusSettingsRpcResponseData;
import org.kablink.teaming.gwt.client.util.GwtAntiVirusConfig;
import org.kablink.teaming.gwt.client.util.GwtAntiVirusConfig.GwtAntiVirusType;
import org.kablink.teaming.module.antivirus.AntiVirusModule;
import org.kablink.teaming.util.AllModulesInjected;
import org.kablink.teaming.util.SpringContextUtil;
import org.kablink.teaming.web.util.MiscUtil;

/**
 * Helper methods for GWT manipulating anti virus configurations.
 *
 * @author drfoster@novell.com
 */
public class GwtAntiVirusHelper {
	protected static Log m_logger = LogFactory.getLog(GwtAntiVirusHelper.class);
	
	private static AntiVirusModule m_avModule;	//

	/*
	 * Class constructor that prevents this class from being
	 * instantiated.
	 */
	private GwtAntiVirusHelper() {
		// Nothing to do.
	}

	/**
	 * Returns an instance of an AdminModule.
	 * 
	 * @return
	 */
	public static AntiVirusModule getAntiVirusModule() {
		if (null == m_avModule) {
			m_avModule = ((AntiVirusModule) SpringContextUtil.getBean("antiVirusModule"));
		}
		return m_avModule;
	}

	/**
	 * Returns a AntiVirusSettingsRpcResponseData object with the
	 * current anti virus settings.
	 *  
	 * @param bs
	 * @param request
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException
	 */
	public static AntiVirusSettingsRpcResponseData getAntiVirusSettings(AllModulesInjected bs, HttpServletRequest request) throws GwtTeamingException {
		try {
			AntiVirusConfig avConfig = getAntiVirusModule().getAntiVirusConfig();
			if (null == avConfig) {
				avConfig = newDomainAntiVirusConfig();
			}
			return new AntiVirusSettingsRpcResponseData(getGwtAVCFromDomainAVC(avConfig));
		}
		
		catch(Exception ex) {
			GwtLogHelper.error(m_logger, "GwtAntiVirusHelper.getAntiVirusSettings( SOURCE EXCEPTION ):  ", ex);
			throw GwtLogHelper.getGwtClientException(ex);				
		}
	}
	
	/*
	 * Converts a GWT AntiVirusConfig to a domain AntiVirusConfig.
	 */
	private static AntiVirusConfig getDomainAVCFromGwtAVC(GwtAntiVirusConfig gwtAVC) {
		AntiVirusConfig reply = new AntiVirusConfig(RequestContextHolder.getRequestContext().getZoneId());
		
		reply.setEnabled(  gwtAVC.isEnabled()   );
		reply.setPassword( gwtAVC.getPassword() );
		reply.setServerUrl(gwtAVC.getServerUrl());
		reply.setUsername( gwtAVC.getUsername() );
		switch (           gwtAVC.getType()     ) {
		default:
		case gwava:  reply.setType(AntiVirusConfig.Type.gwava); break;
		}
		
		return reply;
	}
	
	/*
	 * Converts a domain AntiVirusConfig to a GwtAntiVirusConfig.
	 */
	private static GwtAntiVirusConfig getGwtAVCFromDomainAVC(AntiVirusConfig domainAVC) {
		GwtAntiVirusConfig reply = new GwtAntiVirusConfig();
		
		AntiVirusConfig.Type avt = domainAVC.getType();
		GwtAntiVirusType gavt;
		if (null == avt) {
			gavt = GwtAntiVirusType.gwava;
		}
		else {
			switch (avt) {
			default:
			case gwava:  gavt = GwtAntiVirusType.gwava; break;
			}
		}
		reply.setType(gavt);
		
		reply.setEnabled(  domainAVC.isEnabled()   );
		reply.setServerUrl(domainAVC.getServerUrl());
		reply.setUsername( domainAVC.getUsername() );
		reply.setPassword( domainAVC.getPassword() );
		
		return reply;
	}
	
	/*
	 * Constructs an new domain AntiVirusConfig object.
	 */
	private static AntiVirusConfig newDomainAntiVirusConfig() {
		AntiVirusConfig reply = new AntiVirusConfig(RequestContextHolder.getRequestContext().getZoneId());
		reply.setType(     AntiVirusConfig.Type.gwava);
		reply.setEnabled(  false                     );
		reply.setServerUrl(""                        );
		reply.setUsername( ""                        );
		reply.setPassword( ""                        );
		return reply;
	}

	/**
	 * Stores the requested anti virus settings.
	 *  
	 * @param bs
	 * @param request
	 * @param avConfig
	 * 
	 * @throws GwtTeamingException
	 */
	public static void setAntiVirusSettings(AllModulesInjected bs, HttpServletRequest request, GwtAntiVirusConfig avConfig) throws GwtTeamingException {
		try {
			getAntiVirusModule().saveAntiVirusConfig(getDomainAVCFromGwtAVC(avConfig));
		}
		
		catch(Exception ex) {
			GwtLogHelper.error(m_logger, "GwtAntiVirusHelper.setAntiVirusSettings( SOURCE EXCEPTION ):  Error saving anti virus settings.", ex);
			throw GwtLogHelper.getGwtClientException(ex);				
		}
	}
	
	/**
	 * Test the requested anti virus settings.
	 *  
	 * @param bs
	 * @param request
	 * @param avConfig
	 * 
	 * @throws GwtTeamingException
	 */
	public static TestAntiVirusSettingsRpcResponseData testAntiVirusSettings(AllModulesInjected bs, HttpServletRequest request, GwtAntiVirusConfig avConfig) throws GwtTeamingException {
		TestAntiVirusSettingsRpcResponseData reply = new TestAntiVirusSettingsRpcResponseData(true);
		try {
			getAntiVirusModule().testConnection(getDomainAVCFromGwtAVC(avConfig));
		}
		
		catch(Exception ex) {
			GwtLogHelper.error(m_logger, "GwtAntiVirusHelper.testAntiVirusSettings( SOURCE EXCEPTION ):  Error testing anti virus settings.", ex);
			reply.setValid(  false                  );
			reply.setDetails(MiscUtil.exToString(ex));
		}
		
		return reply;
	}
}
