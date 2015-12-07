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

import java.util.HashMap;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.gwt.client.GwtBrandingData;
import org.kablink.teaming.gwt.client.GwtTeamingException;
import org.kablink.teaming.gwt.client.rpc.shared.GwtDesktopBrandingRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.GwtMobileBrandingRpcResponseData;
import org.kablink.teaming.module.binder.BinderModule;
import org.kablink.teaming.module.shared.MapInputData;
import org.kablink.teaming.runas.RunasCallback;
import org.kablink.teaming.runas.RunasTemplate;
import org.kablink.teaming.util.AbstractAllModulesInjected;
import org.kablink.teaming.util.AllModulesInjected;
import org.kablink.teaming.web.util.MarkupUtil;
import org.kablink.teaming.web.util.WebHelper;

/**
 * Helper methods for GWT branding.
 *
 * @author drfoster@novell.com
 */
public class GwtBrandingHelper {
	protected static Log m_logger = LogFactory.getLog(GwtBrandingHelper.class);

	/*
	 * Class constructor that prevents this class from being
	 * instantiated.
	 */
	private GwtBrandingHelper() {
		// Nothing to do.
	}
	
	/**
	 * Return a GwtBrandingData object for the home workspace.
	 * 
	 * @param bs
	 * @param req
	 * @param ctx
	 */
	public static GwtBrandingData getSiteBrandingData(final AbstractAllModulesInjected bs, final HttpServletRequest req, final ServletContext ctx) throws GwtTeamingException {
		GwtBrandingData brandingData = ((GwtBrandingData) RunasTemplate.runasAdmin(
			new RunasCallback() {
				@Override
				public Object doAs() {
					GwtBrandingData siteBrandingData;

					try {
						// Get the top workspace.
						Binder topWorkspace = bs.getWorkspaceModule().getTopWorkspace();				
					
						// Get the branding data from the top workspace.
						String binderId = topWorkspace.getId().toString();
						siteBrandingData = GwtServerHelper.getBinderBrandingData(
							bs,
							binderId,
							false,
							req,
							ctx);
					}
					
					catch (Exception e) {
						siteBrandingData = new GwtBrandingData();
					}

					return siteBrandingData;
				}
			},
			WebHelper.getRequiredZoneName(req))); 
		brandingData.setIsSiteBranding(true);

		return brandingData;
	}
	
	/**
	 * Returns a GwtDesktopBrandingRpcResponseData object with the
	 * current settings.
	 * 
	 * @param bs
	 * @param request
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException
	 */
	public static GwtDesktopBrandingRpcResponseData getDesktopSiteBrandingData(AllModulesInjected bs, HttpServletRequest request) throws GwtTeamingException {
		GwtServerProfiler gsp = GwtServerProfiler.start(m_logger, "GwtBrandingHelper.getDesktopSiteBrandingData()");
		try {
			String macFileName;
			String windowsFileName;
			
//!			...this needs to be implemented...
			macFileName     =
			windowsFileName = null;
			
			return
				new GwtDesktopBrandingRpcResponseData(
					macFileName,
					windowsFileName);
		}
		
		catch (Exception ex) {
			throw GwtLogHelper.getGwtClientException(m_logger, ex);
		}
		
		finally {
			gsp.stop();
		}
	}
	
	/**
	 * Returns a GwtMobileBrandingRpcResponseData object with the
	 * current settings.
	 * 
	 * @param bs
	 * @param request
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException
	 */
	public static GwtMobileBrandingRpcResponseData getMobileSiteBrandingData(AllModulesInjected bs, HttpServletRequest request) throws GwtTeamingException {
		GwtServerProfiler gsp = GwtServerProfiler.start(m_logger, "GwtBrandingHelper.getMobileSiteBrandingData()");
		try {
			String androidFileName;
			String iosFileName;
			String windowsFileName;
			
//!			...this needs to be implemented...
			androidFileName =
			iosFileName     =
			windowsFileName = null;
			
			return
				new GwtMobileBrandingRpcResponseData(
					androidFileName,
					iosFileName,
					windowsFileName);
		}
		
		catch (Exception ex) {
			throw GwtLogHelper.getGwtClientException(m_logger, ex);
		}
		
		finally {
			gsp.stop();
		}
	}
	
	/**
	 * Save the given branding data to the given binder.
	 * 
	 * @param req
	 * @param binderId
	 * @param brandingData
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException
	 */
	public static Boolean saveBrandingData(AllModulesInjected bs, HttpServletRequest req, String binderId, GwtBrandingData brandingData) throws GwtTeamingException {
		try {
			// Get the binder object.
			BinderModule binderModule = bs.getBinderModule();
			Long binderIdL = new Long( binderId );
			if (binderIdL != null) {
				// Create a Map that holds the branding and extended branding.
				HashMap<String, Object> hashMap = new HashMap<String, Object>();
				
				// Add the old-style branding to the map.
				// JW:  Do we need to do something with the HTML found
				//      in the branding?
				String branding = brandingData.getBranding();
				if (branding == null) {
					branding = "";
				}
				
				// Remove mce_src as an attribute from all <img> tags.  See bug 766415.
				// There was a bug that caused the mce_src attribute to be included in the <img>
				// tag and written to the db.  We want to remove it.
				branding = MarkupUtil.removeMceSrc( branding );

				hashMap.put( "branding", branding );

				// Add the extended branding data to the map.
				branding = brandingData.getBrandingAsXmlString();
				if ( branding == null )
					branding = "";

				hashMap.put( "brandingExt", branding );
				
				// Update the binder with the new branding data.
				MapInputData dataMap = new MapInputData( hashMap );
				binderModule.modifyBinder( binderIdL, dataMap, null, null, null );
			}
		}
		
		catch (Exception e) {
			throw GwtLogHelper.getGwtClientException(e);
		}
		
		return Boolean.TRUE;
	}

	/**
	 * Saves the specified desktop application site branding data.
	 * 
	 * @param bs
	 * @param request
	 * @param desktopBrandingData
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException
	 */
	public static Boolean saveDesktopSiteBrandingData(AllModulesInjected bs, HttpServletRequest request, GwtDesktopBrandingRpcResponseData desktopBrandingData) throws GwtTeamingException {
//!		...this needs to be implemented...
		return Boolean.TRUE;
	}

	/**
	 * Saves the specified mobile application site branding data.
	 * 
	 * @param bs
	 * @param request
	 * @param mobileBrandingData
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException
	 */
	public static Boolean saveMobileSiteBrandingData(AllModulesInjected bs, HttpServletRequest request, GwtMobileBrandingRpcResponseData mobileBrandingData) throws GwtTeamingException {
//!		...this needs to be implemented...
		return Boolean.TRUE;
	}
}
