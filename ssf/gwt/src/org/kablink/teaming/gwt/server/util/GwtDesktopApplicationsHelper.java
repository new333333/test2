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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.domain.FileAttachment;
import org.kablink.teaming.domain.FolderEntry;
import org.kablink.teaming.domain.ZoneConfig;
import org.kablink.teaming.gwt.client.GwtDesktopApplicationsLists;
import org.kablink.teaming.gwt.client.GwtDesktopApplicationsLists.GwtAppInfo;
import org.kablink.teaming.gwt.client.GwtDesktopApplicationsLists.GwtAppListMode;
import org.kablink.teaming.gwt.client.GwtDesktopApplicationsLists.GwtAppPlatform;
import org.kablink.teaming.gwt.client.GwtFileSyncAppConfiguration;
import org.kablink.teaming.gwt.client.GwtTeamingException;
import org.kablink.teaming.gwt.client.GwtTeamingException.ExceptionType;
import org.kablink.teaming.gwt.client.rpc.shared.DesktopAppDownloadInfoRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.DesktopAppDownloadInfoRpcResponseData.FileDownloadInfo;
import org.kablink.teaming.module.zone.ZoneModule;
import org.kablink.teaming.util.AllModulesInjected;
import org.kablink.teaming.util.DesktopApplicationsLists;
import org.kablink.teaming.util.DesktopApplicationsLists.AppInfo;
import org.kablink.teaming.util.DesktopApplicationsLists.AppListMode;
import org.kablink.teaming.util.DesktopApplicationsLists.AppPlatform;
import org.kablink.teaming.util.SPropsUtil;
import org.kablink.teaming.util.SpringContextUtil;
import org.kablink.teaming.util.Utils;
import org.kablink.teaming.web.WebKeys;
import org.kablink.teaming.web.util.MiscUtil;
import org.kablink.teaming.web.util.WebUrlUtil;

/**
 * Helper methods for the GWT Desktop Application administration
 * console page.
 *
 * @author drfoster@novell.com
 */
public class GwtDesktopApplicationsHelper {
	protected static Log m_logger = LogFactory.getLog(GwtDesktopApplicationsHelper.class);

	// The following are used as URL components when constructing the
	// URLs for accessing the desktop download application information.
	private static final String JSON_TAIL		= "version.json";
	private static final String JSON_XP_TAIL	= "version-winxp.json";
	private static final String MACOS_TAIL_FILR	= "novellfilr/osx/x64/";
	private static final String MACOS_TAIL_VIBE	= "novellvibedesktop/osx/x64/";
	private static final String WIN32_TAIL_FILR	= "novellfilr/windows/x86/";
	private static final String WIN32_TAIL_VIBE	= "novellvibedesktop/windows/x86/";
	private static final String WIN64_TAIL_FILR	= "novellfilr/windows/x64/";
	private static final String WIN64_TAIL_VIBE	= "novellvibedesktop/windows/x64/";
	private static final String WINXP_TAIL_FILR	= "novellfilr/windows/x86/";
	private static final String WINXP_TAIL_VIBE	= "novellvibedesktop/windows/x86/";
	
	// Relative path within the local file system where the desktop
	// applications can be found for downloading.
	private static final String LOCAL_DESKTOP_APPS_NODE = "desktopapp";
	private static final String LOCAL_DESKTOP_APPS_BASE = ("/../" + LOCAL_DESKTOP_APPS_NODE);
	
	// Default value for whether we ignore SSL certificates when
	// dealing with download the desktop application.  The value
	// will be used if an 'ignore.ssl.certs.on.desktop.app.download'
	// is not defined in the ssf*.properties file.
	private static final boolean IGNORE_SSL_CERTS_ON_DESKTOP_APP_DOWNLOAD_DEFAULT	= true;
	private static final String  IGNORE_SSL_CERTS_ON_DESKTOP_APP_DOWNLOAD_KEY		= "ignore.ssl.certs.on.desktop.app.download";
	
	// Default value for whether we allow the system to follow
	// redirects when connecting to a desktop application download URL.
	// The value will be used if a
	// 'follow.desktop.app.download.url.redirects' is not defined in
	// the ssf*.properties file.
	private static final boolean FOLLOW_DESKTOP_APP_DOWNLOAD_URL_REDIRECTS	= true;
	private static final String  FOLLOW_DESKTOP_APP_DOWNLOAD_URL_KEY		= "follow.desktop.app.download.url.redirects";
	
	/*
	 * Class constructor that prevents this class from being
	 * instantiated.
	 */
	private GwtDesktopApplicationsHelper() {
		// Nothing to do.
	}
	
	/*
	 * Constructs a desktop application FileDownloadInfo object from a
	 * JSON object.
	 */
	private static FileDownloadInfo buildDesktopAppInfo_Common(String jsonData, String baseUrl, String platformTail) {
		String fName = GwtServerHelper.getSFromJSO(jsonData, "filename");
		String url;
		if (MiscUtil.hasString(fName))
		     url = (baseUrl + platformTail + fName);
		else url = null;
		String md5 = GwtServerHelper.getSFromJSO(jsonData, "md5");
		
		FileDownloadInfo reply;
		if (MiscUtil.hasString(url))
		     reply = new FileDownloadInfo(fName, url, md5);
		else reply = null;
		return reply;
	}
	
	/*
	 * Constructs a desktop application FileDownloadInfo object from
	 * local files.
	 */
	private static FileDownloadInfo buildDesktopAppInfo_Local(String baseFilePath, String baseUrl, String platformTail, String jsonTail) {
		String jsonData = doFileGet(baseFilePath + "/" + platformTail + jsonTail);
		return buildDesktopAppInfo_Common(jsonData, baseUrl, platformTail);
	}
	
	/*
	 * Constructs a desktop application FileDownloadInfo object from a
	 * remote URL.
	 */
	private static FileDownloadInfo buildDesktopAppInfo_Remote(String baseUrl, String platformTail, String jsonTail) {
		String jsonData = doHTTPGet((baseUrl + platformTail + jsonTail));
		return buildDesktopAppInfo_Common(jsonData, baseUrl, platformTail);
	}
	
	/*
	 * Connects to an HTTP URL.
	 */
	private static HttpURLConnection connectToHttpUrl(String url) throws IOException, KeyManagementException, NoSuchAlgorithmException {
		// Open the connection to URL...
		HttpURLConnection reply =
			((HttpURLConnection) openUrlConnection(
				url,
				SPropsUtil.getBoolean(
					IGNORE_SSL_CERTS_ON_DESKTOP_APP_DOWNLOAD_KEY,
					IGNORE_SSL_CERTS_ON_DESKTOP_APP_DOWNLOAD_DEFAULT)));

		// ...initialize it as we need to to access the desktop
		// ...application...
		reply.setRequestMethod("GET");
		reply.setInstanceFollowRedirects(
			SPropsUtil.getBoolean(
				FOLLOW_DESKTOP_APP_DOWNLOAD_URL_KEY,
				FOLLOW_DESKTOP_APP_DOWNLOAD_URL_REDIRECTS));
		reply.connect();
	
		// ...and return it.
		return reply;
	}
	
	/*
	 * Returns the contents of a file.
	 */
	private static String doFileGet(String filePath) {
		BufferedReader  reader = null;
		FileInputStream fis    = null;
		String          reply  = null;
		
		try {
			File file = new File(filePath);
			fis       = new FileInputStream(file);
			reader    = new BufferedReader(new InputStreamReader(fis));
			String       line;
			StringBuffer jsonDataBuf = new StringBuffer();
			while (null != (line = reader.readLine())) {
			    jsonDataBuf.append(line);
			}
			reply = jsonDataBuf.toString();
		}
		
		catch (Exception ex) {
			GwtLogHelper.debug(m_logger, "GwtDesktopApplicationsHelper.doFilrGet( '" + filePath + "' )", ex);
			reply = null;
		}
		
		finally {
			// If we have a reader...
			if (null != reader) {
				// ...make sure it gets closed...
				try {reader.close();}
				catch (Exception e) {}
				reader = null;
			}

			// If we have a file input stream...
			if (null != fis) {
				// ...make sure it gets closed...
				try {fis.close();}
				catch (Exception e) {}
				fis = null;
			}
			
		}
		
		// If we get here, reply is null or refers to the content of
		// the file.  Return it.
		return reply;
	}
	
	/*
	 * Does an HTTP get on the given URL and returns what's read.
	 */
	private static String doHTTPGet(String httpUrl) {
		BufferedReader		reader        = null;
		HttpURLConnection	urlConnection = null;
		String				reply         = null;
		
		try {
			// Make the HTTP connection...
			urlConnection = connectToHttpUrl(httpUrl);

			// ...and read the content from it.
			String			line;
			StringBuffer	result = new StringBuffer();
			reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
			while (null != (line = reader.readLine())) {
			    result.append(line);
			}
			reply = result.toString();
		}
		
		catch (Exception ex) {
			GwtLogHelper.error(m_logger, "GwtDesktopApplicationsHelper.doHTTPGet( '" + httpUrl + "' )", ex);
			reply = null;
		}
		
		finally {
			// If we have a reader...
			if (null != reader) {
				// ...make sure it gets closed...
				try {reader.close();}
				catch (Exception e) {}
				reader = null;
			}

			// ...and if we have an HTTP connection...
			if (null != urlConnection) {
				// ...make sure it gets disconnected.
				urlConnection.disconnect();
				urlConnection = null;
			}
		}

		// If we get here, reply is null or refers to the content of
		// the HTTP GET.  Return it.
		return reply;
	}
	
	/**
	 * Returns a DesktopAppDownloadInfoRpcResponseData object
	 * containing the information for downloading the desktop
	 * applications.
	 * 
	 * @param bs
	 * @param request
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException
	 */
	public static DesktopAppDownloadInfoRpcResponseData getDesktopAppDownloadInformation(AllModulesInjected bs, HttpServletRequest request) throws GwtTeamingException {
		try {
			// Construct the DesktopAppDownloadInfoRpcResponseData
			// object we'll fill in and return...
			DesktopAppDownloadInfoRpcResponseData reply = new DesktopAppDownloadInfoRpcResponseData();

			// ...and complete it from the local information or remote
			// ...URL as appropriate.
			ZoneConfig	zc = bs.getZoneModule().getZoneConfig(RequestContextHolder.getRequestContext().getZoneId());
			if (zc.getFsaDeployLocalApps())
				 getDesktopAppDownloadInformation_Local( bs, request, zc, reply);
			else getDesktopAppDownloadInformation_Remote(bs, request, zc, reply);
			
			// If we get here, reply refers to the
			// DesktopAppDownloadInfoRpcResponseData object
			// containing the information about downloading the desktop
			// application.  Return it.
			return reply;
		}
		catch (Exception ex) {
			throw GwtLogHelper.getGwtClientException(m_logger, ex);
		}		
	}

	/*
	 * Completes a DesktopAppDownloadInfoRpcResponseData object
	 * containing the information for downloading the desktop
	 * applications from the local server.
	 */
	private static void getDesktopAppDownloadInformation_Local(AllModulesInjected bs, HttpServletRequest request, ZoneConfig zc, DesktopAppDownloadInfoRpcResponseData appDownloadInfo) throws GwtTeamingException {
		try {
			// Get the base file path and URL for downloading the files
			// from the local file system...
			String baseFilePath = SpringContextUtil.getServletContext().getRealPath(LOCAL_DESKTOP_APPS_BASE      );
			String baseUrl      = (WebUrlUtil.getSimpleURLContextBaseURL(request) + LOCAL_DESKTOP_APPS_NODE + "/");
			
			// ...and construct and store the desktop
			// ...application information.
			boolean isFilr = Utils.checkIfFilr();
			appDownloadInfo.setMac(  buildDesktopAppInfo_Local(baseFilePath, baseUrl, (isFilr ? MACOS_TAIL_FILR : MACOS_TAIL_VIBE), JSON_TAIL)   );
			appDownloadInfo.setWin32(buildDesktopAppInfo_Local(baseFilePath, baseUrl, (isFilr ? WIN32_TAIL_FILR : WIN32_TAIL_VIBE), JSON_TAIL)   );
			appDownloadInfo.setWin64(buildDesktopAppInfo_Local(baseFilePath, baseUrl, (isFilr ? WIN64_TAIL_FILR : WIN64_TAIL_VIBE), JSON_TAIL)   );
			appDownloadInfo.setWinXP(buildDesktopAppInfo_Local(baseFilePath, baseUrl, (isFilr ? WINXP_TAIL_FILR : WINXP_TAIL_VIBE), JSON_XP_TAIL));
		}
		catch (Exception ex) {
			throw GwtLogHelper.getGwtClientException(m_logger, ex);
		}		
	}

	/*
	 * Completes a DesktopAppDownloadInfoRpcResponseData object
	 * containing the information for downloading the desktop
	 * applications from a remote URL.
	 */
	private static void getDesktopAppDownloadInformation_Remote(AllModulesInjected bs, HttpServletRequest request, ZoneConfig zc, DesktopAppDownloadInfoRpcResponseData appDownloadInfo) throws GwtTeamingException {
		try {
			// Extract the base desktop application update URL and
			// validate it for any redirects, ...
			String baseUrl = getFinalHttpUrl(zc.getFsaAutoUpdateUrl(), "From getDesktopAppDownloadInformation()");
			if (MiscUtil.hasString(baseUrl)) {
				// ...and construct and store the desktop
				// ...application information.
				boolean isFilr = Utils.checkIfFilr();
				appDownloadInfo.setMac(  buildDesktopAppInfo_Remote(baseUrl, (isFilr ? MACOS_TAIL_FILR : MACOS_TAIL_VIBE), JSON_TAIL)   );
				appDownloadInfo.setWin32(buildDesktopAppInfo_Remote(baseUrl, (isFilr ? WIN32_TAIL_FILR : WIN32_TAIL_VIBE), JSON_TAIL)   );
				appDownloadInfo.setWin64(buildDesktopAppInfo_Remote(baseUrl, (isFilr ? WIN64_TAIL_FILR : WIN64_TAIL_VIBE), JSON_TAIL)   );
				appDownloadInfo.setWinXP(buildDesktopAppInfo_Remote(baseUrl, (isFilr ? WINXP_TAIL_FILR : WINXP_TAIL_VIBE), JSON_XP_TAIL));
			}
		}
		catch (Exception ex) {
			throw GwtLogHelper.getGwtClientException(m_logger, ex);
		}		
	}

	/*
	 * Converts a GwtAppListMode value to a domain AppListMode value.
	 */
	private static AppListMode getDomainALMFromGwtALM(GwtAppListMode mode) {
		return AppListMode.valueOf(mode.name());
	}
	
	/*
	 * Converts a GwtAppInfo object to a domain AppInfo object.
	 */
	private static AppInfo getDomainAIFromGwtAI(GwtAppInfo app) {
		return new AppInfo(app.getDescription(), app.getProcessName());
	}
	
	/*
	 * Converts a GwtAppPlatform value to a domain AppPlatform value.
	 */
	private static AppPlatform getDomainAPFromGwtAP(GwtAppPlatform platform) {
		return AppPlatform.valueOf(platform.name());
	}
	
	/*
	 * Converts a GwtDesktopApplicationsList object to a domain
	 * DesktopApplicationsLists object.
	 */
	private static DesktopApplicationsLists getDomainDALFromGwtDAL(GwtDesktopApplicationsLists daLists) {
		DesktopApplicationsLists reply = new DesktopApplicationsLists();
		if (null != daLists) {
			reply.setAppListMode(getDomainALMFromGwtALM(daLists.getAppListMode()));
			for (GwtAppPlatform gwtPlatform:  GwtAppPlatform.values()) {
				AppPlatform domainPlatform = getDomainAPFromGwtAP(gwtPlatform);
				List<AppInfo>		domainAppList = reply.getApplications(  domainPlatform);
				List<GwtAppInfo>	gwtAppList    = daLists.getApplications(gwtPlatform   );
				for (GwtAppInfo gwtApp:  gwtAppList) {
					domainAppList.add(getDomainAIFromGwtAI(gwtApp));
				}
			}
		}
		return reply;
	}
	
	/**
	 * Return a download file URL that can be used to download an
	 * entry's file.
	 * 
	 * @param request
	 * @param bs
	 * @param binderId
	 * @param entryId
	 * @param asPermalink
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException
	 */
	public static String getDownloadFileUrl(HttpServletRequest request, AllModulesInjected bs, Long binderId, Long entryId, boolean asPermalink) throws GwtTeamingException {
		try {
			// Look for the entry's primary file attachment?
			FolderEntry		entry  = bs.getFolderModule().getEntry(null, entryId);
			FileAttachment	dlAttr = MiscUtil.getPrimaryFileAttachment(entry);

			// If we the primary file attachment, generate a URL to
			// download it.  Otherwise, return null.
			String reply;
			if (null != dlAttr)
				 reply = WebUrlUtil.getFileUrl(request, WebKeys.ACTION_READ_FILE, dlAttr, false, true);
			else reply = null;
			return reply;
		}
		
		catch (Exception ex) {
			throw GwtLogHelper.getGwtClientException(m_logger, ex);
		}		
	}
	
	public static String getDownloadFileUrl(HttpServletRequest request, AllModulesInjected bs, Long binderId, Long entryId) throws GwtTeamingException {
		// Always use the initial form of the method.
		return getDownloadFileUrl(request, bs, binderId, entryId, false);
	}
	
	@SuppressWarnings("unchecked")
	public static String getDownloadFileUrl(HttpServletRequest request, Map entryMap) throws GwtTeamingException {
		// Simply build a readFile URL using the information from the
		// map.
		return WebUrlUtil.getFileUrl(request, WebKeys.ACTION_READ_FILE, entryMap);
	}
	
	/**
	 * Return a GwtFileSyncAppConfiguration object that holds the File
	 * Sync Application configuration data.
	 * 
	 * @param bs
	 * 
	 * @return
	 */
	public static GwtFileSyncAppConfiguration getFileSyncAppConfiguration(AllModulesInjected bs, boolean requireAppLists) {
		ZoneModule zoneModule = bs.getZoneModule();
		ZoneConfig zoneConfig = zoneModule.getZoneConfig(RequestContextHolder.getRequestContext().getZoneId());
		
		GwtFileSyncAppConfiguration fileSyncAppConfiguration = new GwtFileSyncAppConfiguration();
		
		// Get whether desktop applications can be deployed locally or
		// not.
		File appsDirectory = new File(SpringContextUtil.getServletContext().getRealPath(LOCAL_DESKTOP_APPS_BASE));
		boolean localAppsExist = appsDirectory.exists();
		fileSyncAppConfiguration.setLocalAppsExist(localAppsExist);
		
		// Get the whether the File Sync Application is enabled.
		fileSyncAppConfiguration.setIsFileSyncAppEnabled(zoneConfig.getFsaEnabled());
		
		// Get the setting that determines whether the desktop
		// application can remember the password.
		fileSyncAppConfiguration.setAllowCachePwd(zoneConfig.getFsaAllowCachePwd());
		
		// Get the max file size the desktop application can download.
		fileSyncAppConfiguration.setMaxFileSize(zoneConfig.getFsaMaxFileSize());
		
		// Get the File Sync Application sync interval.
		fileSyncAppConfiguration.setSyncInterval(zoneConfig.getFsaSynchInterval());
		
		// Get the auto-update URL.
		fileSyncAppConfiguration.setAutoUpdateUrl(zoneConfig.getFsaAutoUpdateUrl());
		
		// Get whether deployment of the file sync application is
		// enabled.
		fileSyncAppConfiguration.setIsDeploymentEnabled(zoneConfig.getFsaDeployEnabled());
		
		// Get whether deployment is done from local or remote
		// applications.
		boolean deployLocalApps = (localAppsExist && zoneConfig.getFsaDeployLocalApps());
		fileSyncAppConfiguration.setUseLocalApps(   deployLocalApps );
		fileSyncAppConfiguration.setUseRemoteApps((!deployLocalApps));

		// If we're running Filr...
		if (Utils.checkIfFilr() && requireAppLists) {
			// ...we need to set the GwtDesktopApplicationsLists from
			// ...the domain DesktopApplicationsLists.
			fileSyncAppConfiguration.setGwtDesktopApplicationsLists(
				getGwtDALFromDomainDAL(
					zoneConfig.getDesktopApplicationsLists()));
		}
		
		return fileSyncAppConfiguration;
	}
	
	public static GwtFileSyncAppConfiguration getFileSyncAppConfiguration(AllModulesInjected bs) {
		return getFileSyncAppConfiguration(bs, true);
	}

	/*
	 * Establishes the connection to an HTTP URL and follows any
	 * redirect, returning the final URL resolved to.
	 */
	private static String getFinalHttpUrl(String httpUrl, String usageForLog) {
		// Do we have a URL to finalize?
		httpUrl = ((null == httpUrl) ? "" : httpUrl.trim());
		if (0 == httpUrl.length()) {
			// No!  Return null.
			return null;
		}
		
		// Ensure the URL ends with a '/'.
		if ('/' != httpUrl.charAt(httpUrl.length() - 1)) {
			httpUrl += "/";
		}
		
		HttpURLConnection	urlConnection = null;
		String				reply         = null;
		
		try {
			// Make the HTTP connection.
			urlConnection = connectToHttpUrl(httpUrl);

			// Is the connection being redirected?
			int status = urlConnection.getResponseCode();
			switch (status) {
			case HttpURLConnection.HTTP_MOVED_PERM:
			case HttpURLConnection.HTTP_MOVED_TEMP:
				// Yes!  Get the redirected URL..
				reply = urlConnection.getHeaderField("Location");
				if (MiscUtil.hasString(reply)) {
					if ('/' != reply.charAt(reply.length() - 1)) {
						reply += "/";
					}
				}
				
				// ...and log the fact that it's being redirected.
				GwtLogHelper.debug(m_logger, "GwtDesktopApplicationsHelper.getFinalHttpUrl( '" + httpUrl + "' ):  " + usageForLog + "...");
				GwtLogHelper.debug(m_logger, "...is being redirected to:  '" + reply + "'");
				
				break;
				
			case HttpURLConnection.HTTP_OK:
            case HttpURLConnection.HTTP_NOT_FOUND:
				// No, the connection isn't being redirected!
            	// Everything is fine.  Return it.
            	//
                // Note that a 404 (Not Found) response getting the
            	//    base URL is acceptable depending on how the
            	//    auto-update server is set up.  The base URL can
            	//    return a 404 while the full URL to the desktop
            	//    application files still succeeds.
				reply = httpUrl;
				break;
				
			default:
				// Log any other connection status as an error and
				// return null.
				GwtLogHelper.error(m_logger, "GwtDesktopApplicationsHelper.getFinalHttpUrl( '" + httpUrl + "' ):  Connection status:  " + status);
				reply = null;
				break;
			}
		}
		
		catch (Exception ex) {
			// Log any exceptions as an error and return null.
			GwtLogHelper.error(m_logger, "GwtDesktopApplicationsHelper.getFinalHttpUrl( '" + httpUrl + "' )", ex);
			reply = null;
		}
		
		finally {
			// If we have an HTTP connection...
			if (null != urlConnection) {
				// ...make sure it gets disconnected.
				urlConnection.disconnect();
			}
		}

		// If we get here, reply is null or refers to the final URL
		// after following all redirects, ...  Return it.
		return reply;
	}

	/*
	 * Converts a domain AppListMode value to a GwtAppListMode value.
	 */
	private static GwtAppListMode getGwtALMFromDomainALM(AppListMode mode) {
		return GwtAppListMode.valueOf(mode.name());
	}
	
	/*
	 * Converts a domain AppInfo object to a GwtAppInfo object.
	 */
	private static GwtAppInfo getGwtAIFromDomainAI(AppInfo app) {
		return new GwtAppInfo(app.getDescription(), app.getProcessName());
	}
	
	/*
	 * Converts a domain AppPlatform value to a GwtAppPlatform value.
	 */
	private static GwtAppPlatform getGwtAPFromDomainAP(AppPlatform platform) {
		return GwtAppPlatform.valueOf(platform.name());
	}
	
	/*
	 * Converts a domain DesktopApplicationsList object to a
	 * GwtDesktopApplicationsLists object.
	 */
	private static GwtDesktopApplicationsLists getGwtDALFromDomainDAL(DesktopApplicationsLists daLists) {
		GwtDesktopApplicationsLists reply = new GwtDesktopApplicationsLists();
		if (null != daLists) {
			reply.setAppListMode(getGwtALMFromDomainALM(daLists.getAppListMode()));
			for (AppPlatform domainPlatform:  AppPlatform.values()) {
				GwtAppPlatform gwtPlatform = getGwtAPFromDomainAP(domainPlatform);
				List<GwtAppInfo>	gwtAppList    = reply.getApplications(  gwtPlatform   );
				List<AppInfo>		domainAppList = daLists.getApplications(domainPlatform);
				for (AppInfo domainApp:  domainAppList) {
					gwtAppList.add(getGwtAIFromDomainAI(domainApp));
				}
			}
		}
		return reply;
	}
	
	/*
	 * Opens a URL connection, optionally ignoring any SSL
	 * certificates.
	 * 
	 * Note:  The algorithm used to ignore SSL certificates was
	 * obtained from https://code.google.com/p/misc-utils/wiki/JavaHttpsUrl
	 */
	private static URLConnection openUrlConnection(String url, boolean ignoreSSLCerts) throws IOException, KeyManagementException, NoSuchAlgorithmException {
		// Create the URLConnection object.
		final URLConnection reply = new URL(url).openConnection();
		
		if (ignoreSSLCerts && (reply instanceof HttpsURLConnection)) {
			// Create a trust manager that does not validate certificate chains
			final TrustManager[] trustAllCerts = new TrustManager[] {new X509TrustManager() {
		        @Override
		        public void checkClientTrusted(final X509Certificate[] chain, final String authType) {
		        }
		        @Override
		        public void checkServerTrusted(final X509Certificate[] chain, final String authType) {
		        }
		        @Override
		        public X509Certificate[] getAcceptedIssuers() {
		            return null;
		        }
		    }};
		    
			// Install the all-trusting trust manager.
		    final SSLContext sslContext = SSLContext.getInstance("SSL");
		    sslContext.init( null, trustAllCerts, new java.security.SecureRandom() );
		    
		    // Create an SSL socket factory with our all-trusting
		    // manager and use it for the socket factory which bypasses
		    // security checks.
		    final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
			((HttpsURLConnection) reply).setSSLSocketFactory(sslSocketFactory);
			((HttpsURLConnection) reply).setHostnameVerifier(new HostnameVerifier() {
				@Override
				public boolean verify(String hostname, SSLSession session) {
					return true;
				}
			});
		}
	    
	    // Finally, return the URLConnection.
		return reply;
	}

	/**
	 * Save the given File Sync Application configuration.
	 * 
	 * @param bs
	 * @param fsaConfiguration
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException
	 */
	public static Boolean saveFileSyncAppConfiguration(AllModulesInjected bs, GwtFileSyncAppConfiguration fsaConfiguration) throws GwtTeamingException {
		Boolean	allowCachePwd = new Boolean(fsaConfiguration.getAllowCachePwd()       );
		Boolean	deployEnabled = new Boolean(fsaConfiguration.getIsDeploymentEnabled() );
		Boolean	enabled       = new Boolean(fsaConfiguration.getIsFileSyncAppEnabled());
		Integer	interval      = new Integer(fsaConfiguration.getSyncInterval()        );
		Integer	maxFileSize   = new Integer(fsaConfiguration.getMaxFileSize()         );
		
		// Does the user entered auto update url need to be validated?
		Boolean	useRemoteApps = fsaConfiguration.getUseRemoteApps();
		String	autoUpdateUrl = fsaConfiguration.getAutoUpdateUrl();
		if (useRemoteApps && MiscUtil.hasString(autoUpdateUrl)) {
			// Yes, is it valid?
			if (!(validateDesktopAppDownloadUrl(autoUpdateUrl))) {
				// No
				GwtTeamingException gtEx = GwtLogHelper.getGwtClientException(m_logger);
				gtEx.setExceptionType(ExceptionType.INVALID_AUTO_UPDATE_URL);
				throw gtEx;				
			}
		}
		
		DesktopApplicationsLists domainDALists;
		if (Utils.checkIfFilr()) {
			GwtDesktopApplicationsLists gwtDALists = fsaConfiguration.getGwtDesktopApplicationsLists();
			domainDALists = ((null == gwtDALists)? null : getDomainDALFromGwtDAL(gwtDALists));
		}
		else {
			domainDALists = null;
		}
		
		bs.getAdminModule().setFileSynchAppSettings(
			enabled,
			interval,
			autoUpdateUrl,
			deployEnabled,
			fsaConfiguration.getUseLocalApps(),
			allowCachePwd,
			maxFileSize,
			domainDALists);

		return Boolean.TRUE;
	}
	
	/**
	 * Returns true if baseUrl is a valid desktop application download
	 * URL and false otherwise.
	 * 
	 * @param baseUrl
	 * 
	 * @return
	 */
	public static boolean validateDesktopAppDownloadUrl(String baseUrl) {
		// Validate the URL for any redirects, ...
		baseUrl = getFinalHttpUrl(baseUrl, "From validateDesktopAppDownloadUrl()");
		if (!(MiscUtil.hasString(baseUrl))) {
			return false;
		}
		
		// ...and test it.
		String platformTail = (Utils.checkIfFilr() ? WIN32_TAIL_FILR : WIN32_TAIL_VIBE);
		String jsonData = doHTTPGet((baseUrl + platformTail + JSON_TAIL));
		return (null != GwtServerHelper.getJSOFromS(jsonData));
	}
}
