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
package org.kablink.teaming.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.dom4j.Document;
import org.dom4j.Element;

import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.asmodule.zonecontext.ZoneContextHolder;
import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.CustomAttribute;
import org.kablink.teaming.domain.HomePageConfig;
import org.kablink.teaming.domain.Workspace;
import org.kablink.teaming.domain.EntityIdentifier.EntityType;
import org.kablink.teaming.module.admin.AdminModule;
import org.kablink.teaming.module.binder.BinderModule;
import org.kablink.teaming.module.license.LicenseChecker;
import org.kablink.teaming.module.zone.ZoneModule;
import org.kablink.teaming.runas.RunasCallback;
import org.kablink.teaming.runas.RunasTemplate;
import org.kablink.teaming.search.IndexSynchronizationManager;
import org.kablink.teaming.security.AccessControlException;
import org.kablink.teaming.util.cache.DefinitionCache;
import org.kablink.teaming.web.WebKeys;
import org.kablink.teaming.web.util.DefinitionHelper;
import org.kablink.teaming.web.util.ExportException;
import org.kablink.teaming.web.util.ExportHelper;
import org.kablink.teaming.web.util.MiscUtil;
import org.kablink.teaming.web.util.PermaLinkUtil;

import org.springframework.transaction.support.TransactionTemplate;

/**
 * Helper methods for working with landing pages.
 * 
 * @author drfoster@novell.com
 */
public class LandingPageHelper {
	protected static Log m_logger = LogFactory.getLog(LandingPageHelper.class);
	
	// Relative path within the local file system where the default
	// landing page file can be found for installing.
	private static final String LOCAL_LANDING_PAGE_NODE = "vibelandingpage";
	private static final String LOCAL_LANDING_PAGE_BASE = ("/" + LOCAL_LANDING_PAGE_NODE);
	
	/*
	 * Inhibits this class from being instantiated. 
	 */
	private LandingPageHelper() {
		// Nothing to do.
	}

	/*
	 * Appends a 'novl_landing' flag to a URL.
	 */
	private static String addLandingPageFlagToUrl(String url) {
		if (null == url) {
			return url;
		}
		
		String reply;
		boolean crawler = url.contains(Constants.SLASH + WebKeys.URL_NOVL_URL_FLAG + Constants.SLASH);
		if (crawler)
		     reply = (url + Constants.SLASH     + WebKeys.URL_NOVL_LANDING_PAGE_FLAG + Constants.SLASH + "1");
		else reply = (url + Constants.AMPERSAND + WebKeys.URL_NOVL_LANDING_PAGE_FLAG + Constants.EQUAL + "1");
		return reply;
	}
	
	/*
	 * Returns true if the given landing page exists in the Global
	 * Workspaces binder in the current zone.  Otherwise, returns
	 * false.
	 */
	private static boolean doesDefaultLandingPageExist(Binder globalWS, String lpFileName) {
		return (null != getDefaultLandingPage(globalWS, lpFileName));
	}

	/*
	 * If found, returns the given landing page in the Global
	 * Workspaces binder in the current zone.  Otherwise, returns null.
	 */
	private static Binder getDefaultLandingPage(Binder globalWS, String lpFileName) {
		// If we don't have a Global Workspaces or landing page
		// filename...
		if ((null == globalWS) || (!(MiscUtil.hasString(lpFileName)))) {
			// ...bail.
			return null;
		}
		
		// Determine the base name for the default landing page.
		int pIndex = lpFileName.indexOf('.');
		if (0 < pIndex) {
			lpFileName = lpFileName.substring(0, pIndex);
		}
		
		// If it exists, can we access the default landing page?
		Binder reply;
		try {
			reply = MiscUtil.getBinderModule().getBinderByParentAndTitle(globalWS.getId(), lpFileName);
		}
		
		catch (AccessControlException ace) {
			// No!  Log the error.
			m_logger.error("getDefaultlandingPage():  Could not access the binder '" + lpFileName + "'", ace);
			reply = null;
		}

		// If we get here, reply refers to the given landing page.
		// Return it.
		return reply;
		
	}
	
	/*
	 * Returns the Vibe default landing page import filename as read
	 * from the ssf*.properties.
	 * 
	 * If there is no default Vibe landing page import filename
	 * specified, null is returned.
	 */
	private static String getDefaultLandingPageFileName() {
		return SPropsUtil.getString("vibe.default.landing.page", null);
	}

	/*
	 * Returns the full path to the Vibe default landing page import
	 * file.
	 * 
	 * If there is no default Vibe landing page import filename
	 * specified, null is returned.
	 */
	private static String getDefaultLandingPageFilePath() {
		String lpName = getDefaultLandingPageFileName();
		if (!(MiscUtil.hasString(lpName))) {
			return null;
		}
		if (0 > lpName.indexOf('.')) {
			lpName += ".zip";
		}
		return (SpringContextUtil.getServletContext().getRealPath(LOCAL_LANDING_PAGE_BASE) + File.separator + lpName);
	}

	/**
	 * Returns the URL for the Default Home Page or null if one isn't
	 * accessible or specified.
	 * 
	 * @param req
	 * 
	 * @return
	 */
	public static String getDefaultLandingPageUrl(HttpServletRequest req) {
		// Is there a Default Home Page defined?
		String reply = null;
		ZoneModule zm = MiscUtil.getZoneModule();
		Long zoneId = zm.getZoneIdByVirtualHost(ZoneContextHolder.getServerName());
		HomePageConfig homePageConfig = zm.getZoneConfig(zoneId).getHomePageConfig();
		Long homeId = ((null == homePageConfig) ? null : homePageConfig.getDefaultHomePageId());
		if (null != homeId) {
			try {
				// Yes!  Can we access it?
				reply = getLandingPageUrlFromId(req, homeId);
			}
			catch (Exception ex) {
				// May cause an exception depending on who's logged in
				// and whether they have access to it.  That's not an
				// error for the purposes of this method.
				reply = null;
			}
		}
		
		// If we get here, reply refers to the Default Home Page URL if
		// one is defined and accessible or is null.  Return it.
		return reply;
	}
	
	/**
	 * Returns the URL for the Default Guest Home Page or null if one
	 * isn't accessible or specified.
	 * 
	 * @param req
	 * 
	 * @return
	 */
	public static String getDefaultGuestLandingPageUrl(HttpServletRequest req) {
		// Is there a Default Guest Home Page defined?
		String reply = null;
		ZoneModule zm = MiscUtil.getZoneModule();
		Long zoneId = zm.getZoneIdByVirtualHost(ZoneContextHolder.getServerName());
		HomePageConfig homePageConfig = zm.getZoneConfig(zoneId).getHomePageConfig();
		Long guestHomeId = ((null == homePageConfig) ? null : homePageConfig.getDefaultGuestHomePageId());
		if (null != guestHomeId) {
			try {
				// Yes!  Can we access it?
				reply = getLandingPageUrlFromId(req, guestHomeId);
			}
			catch (Exception ex) {
				// May cause an exception depending on who's logged in
				// and whether they have access to it.  That's not an
				// error for the purposes of this method.
				reply = null;
			}
		}
		
		// If we get here, reply refers to the Default Guest Home Page
		// URL if one is defined and accessible or is null.  Return it.
		return reply;
	}
	
	/*
	 * Returns the Global Workspaces binder for the current zone.
	 */
	@SuppressWarnings("unchecked")
	private static Binder getGlobalWorkspaces() {
		Binder reply = null;
		List<Binder> childBinders = MiscUtil.getWorkspaceModule().getTopWorkspace().getBinders();
		if ((null != childBinders) && (!(childBinders.isEmpty()))) {
			for (Binder nextBinder:  childBinders) {
				String internalId = nextBinder.getInternalId();
				if (internalId.equalsIgnoreCase(ObjectKeys.GLOBAL_ROOT_INTERNALID)) {
					reply = nextBinder;
					break;
				}
			}
		}
		return reply;
	}

	/**
	 * Returns the CustomAttribute to use as the mashup for a landing
	 * page.
	 *  
	 * @param bs
	 * @param binder
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static CustomAttribute getLandingPageMashupAttribute(Binder binder) {
		// Determine the name of the mashup custom attribute.
		String  customAttrName     = null;
		boolean customAttrIsMashup = false;
		Map<String,Object> tmpModel = new HashMap<String,Object>();
		DefinitionHelper.getDefinitions(binder, tmpModel);
		Document configDocument = ((Document) tmpModel.get(WebKeys.CONFIG_DEFINITION));
		if (null != configDocument) {
			List<Element> propertyElements = configDocument.selectNodes("//item[@type='form']//item[@type='data' and @name='mashupCanvas']/properties/property[@name='name']");
	    	if ((null != propertyElements) && (!(propertyElements.isEmpty()))) {
        		customAttrName     = propertyElements.get(0).attributeValue("value");
        		customAttrIsMashup = ((null != customAttrName) && customAttrName.equals("mashup"));
	        }
		}

		// Can we get the custom attribute using the name we determined
		// above?
		CustomAttribute reply;
		if (MiscUtil.hasString(customAttrName))
		     reply = binder.getCustomAttribute(customAttrName);
		else reply = null;
		if ((null == reply) && (!customAttrIsMashup)) {
			// No!  Try using the default name for the mashup custom
			// attribute.
			reply = binder.getCustomAttribute("mashup");
		}
		return reply;
	}
	
	public static CustomAttribute getLandingPageMashupAttribute(AllModulesInjected bs, Long binderId) {
		// Always use the initial version of the method.
		Binder binder = bs.getBinderModule().getBinder(binderId);
		return getLandingPageMashupAttribute(binder);
	}
	
	public static CustomAttribute getLandingPageMashupAttribute(AllModulesInjected bs, String binderId) {
		// Always use the previous version of the method.
		return getLandingPageMashupAttribute(bs, Long.parseLong(binderId));
	}
	
	/**
	 * Returns a landing URL given the binderId of the landing page.
	 * 
	 * @param req
	 * @param binderId
	 * 
	 * @return
	 */
	public static String getLandingPageUrlFromId(HttpServletRequest req, Long binderId) {
		String reply = PermaLinkUtil.getPermalink(req, binderId, EntityType.folder);
		reply = addLandingPageFlagToUrl(reply);
		return reply;
	}
	
	/**
	 * Imports the Vibe default landing page into the given zones as
	 * necessary.
	 * 
	 * @param zones
	 * @param transactionTemplate 
	 */
	public static void importVibeDefaultLandingPages(final List<Workspace> zones, final TransactionTemplate transactionTemplate) {
		// Are we running as Vibe?
		if (!(LicenseChecker.showVibeFeatures())) {
			// No!  Bail.
			return;
		}

		// Do we have a path for the landing page import file?
		final String lpFilePath = getDefaultLandingPageFilePath();
		if (!(MiscUtil.hasString(lpFilePath))) {
			// No!  Bail.
			return;
		}
		
		FileInputStream lpFileStream = null;
 		try {
 			// Open a FileInputStream on the file.
 			lpFileStream = new FileInputStream(lpFilePath);
			final FileInputStream finalLPFileStream = lpFileStream;
	
	 		// Do we have any zones to process?
			if ((null != zones) && (!(zones.isEmpty()))) {
				// Yes!  Scan them.
				final AdminModule am = MiscUtil.getAdminModule();
	    		for (Workspace zone:  zones) {
	    			final String zoneName = zone.getName();
	    			try {
	    				// Within each zone, check for the landing page
	    				// and import it if necessary.
		                RunasTemplate.runasAdmin(
	                		new RunasCallback() {
	                            @Override
	                            public Object doAs() {
	                            	// Can we get the Global
	                            	// Workspaces binder for
	                            	// this zone?
	                        		Binder globalWS = getGlobalWorkspaces();
	                        		if (null == globalWS) {
	                        			// No!  Then we're done 
	                        			// with it.
	                        			return null;
	                        		}
	                        		
	                            	// Does this zone already
	                            	// have a default landing
	                            	// page defined?
	                 			    HomePageConfig curLP = am.getHomePageConfig();
	                 			    boolean hasCurLP = (null != curLP);
	                 			    if (hasCurLP && (null != curLP.getDefaultHomePageId())) {
	                 			    	// Yes!  Then we're
	                 			    	// done with it.
	                 			    	return null;
	                 			    }
	                 			    
	                        		// Does this default
	                        		// landing page already
	                        		// exist in this zone?
	                        		String lpFileName = getDefaultLandingPageFileName();
	                        		if (doesDefaultLandingPageExist(globalWS, lpFileName)) {
	                        			// No!  Then we're 
	                        			// done with it.
	                        			return null;
	                        		}
	                        		
	                        		m_logger.debug("importVibeDefaultLandingPageImpl():  About to import file '" + lpFileName + "'...");
	    							importVibeDefaultLandingPageImpl(globalWS, lpFileName, finalLPFileStream, (hasCurLP ? curLP.getDefaultGuestHomePageId() : null));

	    							
	    							// At this point we must flush out any indexing changes that might have occurred
	    							// before clearing the context.
	    							IndexSynchronizationManager.applyChanges();
	    					 		RequestContextHolder.clear();	    					 		
	    					 		DefinitionCache.clear();
	    					 		
	    					 		return null;
	                            }
	                        },
	                        zoneName
		                );
	    			}
	    			catch (Exception e) {
	    				m_logger.error("importVibeDefaultLandingPages():  Failed to import Vibe default landing page for zone " + zone.getZoneId(), e);
	    			}       			
	    		}        		
			}
 		}
 		
 		catch (FileNotFoundException fnfEx) {
 			// The import file doesn't exist!  Log the error and bail.
 			m_logger.error("importVibeDefaultLandingPages():  Could not find the landing page import file '" + lpFilePath + "' !!!");
 			lpFileStream = null;
 			return;
 		}
 		
 		finally {
 			// If we have a FileInputStream...
 			if (null != lpFileStream) {
 				// ...make sure it got closed.
 				try {lpFileStream.close();}
 				catch (IOException e) {/* Ignore. */}
 				lpFileStream = null;
 			}
 		}
	}
	
	/*
	 * Imports a default Vibe landing page into the current zone.
	 */
	@SuppressWarnings("unchecked")
	private static void importVibeDefaultLandingPageImpl(Binder globalWS, String lpFileName, FileInputStream lpZipFile, Long guestHomePageId) {
		// Create a report Map for the import.
		Map reportMap = new HashMap();
 		reportMap.put(ExportHelper.workspaces, new Integer(0) );
 		reportMap.put(ExportHelper.folders,    new Integer(0) );
 		reportMap.put(ExportHelper.entries,    new Integer(0) );
 		reportMap.put(ExportHelper.files,      new Integer(0) );
 		reportMap.put(ExportHelper.errors,     new Integer(0) );
 		reportMap.put(ExportHelper.errorList,  new ArrayList());
 		
 		try {
			// Can we import the given zip?
			m_logger.debug("...about to call ExportHelper.importZip() on '" + lpFileName + "'...");
			Long globalWSId  = globalWS.getId();
			Long newBinderId = ExportHelper.importZip(globalWSId, lpZipFile, null, reportMap);
 			if (null != newBinderId) {
 				// Yes!  At least one binder was created.  Find the top
 				// binder created...
 	 			BinderModule bm = MiscUtil.getBinderModule();
 				Binder newBinder = bm.getBinder(newBinderId);
 				while ((null != newBinder.getParentBinder()) && (globalWSId != newBinder.getParentBinder().getId())) {
 					newBinder = newBinder.getParentBinder();
 				}
 				if ((null != newBinder.getParentBinder()) && (globalWSId == newBinder.getParentBinder().getId())) {
 					// ...and index it.
 					bm.indexBinder(newBinder.getId(), true);
 				}

 				// Can we now find the default landing page?
 				Binder lpDefault = getDefaultLandingPage(globalWS, lpFileName);
 				if (null != lpDefault) {
 					// Yes!  Set it as the default landing page.
	 	 			HomePageConfig homePageConfig = new HomePageConfig();
	 				homePageConfig.setDefaultHomePageId(lpDefault.getId());
	 				homePageConfig.setDefaultGuestHomePageId(guestHomePageId);
	 				MiscUtil.getAdminModule().setHomePageConfig(homePageConfig);
 				}
 			}
 		}
 		
 		catch (IOException     ioEx) {m_logger.error("importVibeDefaultLandingPages():  ExportHelper.importZip() threw an IOException.",      ioEx);}
		catch (ExportException ex)   {m_logger.error("importVibeDefaultLandingPages():  ExportHelper.importZip() threw an ExportException.",  ex  );}
		catch (Exception       e)    {m_logger.error("importVibeDefaultLandingPages():  ExportHelper.importZip() threw a generic Exception.", e   );}
	}
}
