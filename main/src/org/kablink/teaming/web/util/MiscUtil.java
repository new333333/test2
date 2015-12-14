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
package org.kablink.teaming.web.util;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.security.MessageDigest;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.portlet.RenderRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.antivirus.VirusDetectedError;
import org.kablink.teaming.comparator.StringComparator;
import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.dao.CoreDao;
import org.kablink.teaming.domain.AuthenticationConfig;
import org.kablink.teaming.domain.CustomAttribute;
import org.kablink.teaming.domain.DefinableEntity;
import org.kablink.teaming.domain.Definition;
import org.kablink.teaming.domain.FileAttachment;
import org.kablink.teaming.domain.FileItem;
import org.kablink.teaming.domain.FolderEntry;
import org.kablink.teaming.domain.HistoryStamp;
import org.kablink.teaming.domain.Principal;
import org.kablink.teaming.domain.Subscription;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.domain.UserPrincipal;
import org.kablink.teaming.domain.UserProperties;
import org.kablink.teaming.domain.ZoneInfo;
import org.kablink.teaming.module.admin.AdminModule;
import org.kablink.teaming.module.binder.BinderModule;
import org.kablink.teaming.module.folder.FolderModule;
import org.kablink.teaming.module.profile.ProfileModule;
import org.kablink.teaming.module.workspace.WorkspaceModule;
import org.kablink.teaming.module.zone.ZoneModule;
import org.kablink.teaming.portletadapter.AdaptedPortletURL;
import org.kablink.teaming.portletadapter.portlet.HttpServletRequestReachable;
import org.kablink.teaming.security.function.Function;
import org.kablink.teaming.util.AllModulesInjected;
import org.kablink.teaming.util.NLT;
import org.kablink.teaming.util.ReleaseInfo;
import org.kablink.teaming.util.ResolveIds;
import org.kablink.teaming.util.SPropsUtil;
import org.kablink.teaming.util.SimpleProfiler;
import org.kablink.teaming.util.SpringContextUtil;
import org.kablink.teaming.util.Utils;
import org.kablink.teaming.web.WebKeys;
import org.kablink.util.BrowserSniffer;
import org.kablink.util.HttpHeaders;

import org.springframework.web.multipart.MultipartFile;

/**
 * This class contains a collection of miscellaneous utility methods.
 * 
 * @author drfoster@novell.com
 */
public final class MiscUtil {
	protected static Log m_logger = LogFactory.getLog(MiscUtil.class);
	
	// Initialized by the first call to get...Module();
	private static BinderModule		m_binderModule;		//
	private static AdminModule		m_adminModule;		//
	private static CoreDao			m_coreDao;			//
	private static FolderModule		m_folderModule;		//
	private static ProfileModule	m_profileModule;	//
	private static WorkspaceModule	m_wsModule;			//
	private static ZoneModule		m_zoneModule;		//
	
	// The following are used as the return values for the various
	// comparators.
	public final static int COMPARE_EQUAL	=   0;
	public final static int COMPARE_GREATER	=   1;
	public final static int COMPARE_LESS	= (-1);
	
	// The following string is used to recognize Chrome's user agent string.
	private final static String CHROME_AGENT_MARKER		= "chrome/";
	private final static int	CHROME_VERSION_PARTS	= 4;
	
	// The following is use as the key into the session cache where we
	// store whether the user's browser supports NPAPI.
	private final static String	BROWSER_SUPPORTS_NPAPI	= "browserSupportsNPAPI";
	
	// The following defines the language strings that may need to be
	// patched into a documentation URL.
	private final static String[] DOC_LANGS = {
		"cs-cz",
		"da-dk",
		"de-de",
		"es-es",
		"fr-fr",
		"hu-hu",
		"it-it",
		"ja-jp",
		"nl-nl",
		"pl-pl",
		"pt-br",
		"ru-ru",
		"sv-se",
		"zh-cn",
		"zh-tw",		
	};
	
	// Names of the different help guides.
	private static final String USER_GUIDE = "user";
	private static final String ADV_USER_GUIDE = "adv_user";
	private static final String ADMIN_GUIDE = "admin";
	private static final String INSTALLATION_GUIDE = "install";
	
	/*
	 * Enumeration type to specific the platform the browser is running
	 * on.
	 */
	@SuppressWarnings("unused")
	private enum BrowserPlatform {
		LINUX,
		MAC,
		WINDOWS,
		
		UNKNOWN;
		
		boolean isLinux()   {return    this.equals(LINUX  );  }
		boolean isMac()     {return    this.equals(MAC    );  }
		boolean isWindows() {return    this.equals(WINDOWS);  }
		boolean isValid()   {return (!(this.equals(UNKNOWN)));}
	}

	/**
	 * Inner class...
	 */
	public static class IdTriple {
		public Long		m_binderId;		//
		public Long		m_entryId;		//
		public String	m_entityType;	//
		
		public IdTriple(Long binderId, Long entryId, String entityType) {
			super();
			
			m_binderId   = binderId;
			m_entryId    = entryId;
			m_entityType = entityType;
		}
	}

	/*
	 * Class constructor that prevents this class from being
	 * instantiated.
	 */
	private MiscUtil() {
		// Nothing to do.
	}
	
	
	/**
	 * Add all of the information needed to support the "Create new
	 * account" ui to the response.
	 * 
	 * @param bs
	 * @param request
	 * @param model
	 */
	@SuppressWarnings("unchecked")
	public static void addCreateNewAccountDataToResponse(AllModulesInjected	bs, RenderRequest request, Map<String,Object> model) {
		ProfileModule			profileModule;
		Map<String, Definition>	entryDefsMap;
		Definition				def;
		Collection<Definition>	entryDefsCollection;
		Iterator<Definition>	entryDefsIterator;

		profileModule = bs.getProfileModule();
		
		// There is only 1 entry definition for a Profile binder.  Get it.
		entryDefsMap = profileModule.getProfileBinderEntryDefsAsMap();
		entryDefsCollection = entryDefsMap.values();
		entryDefsIterator = entryDefsCollection.iterator();
		if (entryDefsIterator.hasNext()) {
			AdaptedPortletURL	adapterUrl;

			def = (Definition) entryDefsIterator.next();

			// Create the url needed to invoke the "Add User" page.
			adapterUrl = new AdaptedPortletURL(request, "ss_forum", true);
			adapterUrl.setParameter(WebKeys.ACTION, WebKeys.ACTION_ADD_PROFILE_ENTRY);
			adapterUrl.setParameter(WebKeys.URL_BINDER_ID, profileModule.getProfileBinderId().toString());
			adapterUrl.setParameter(WebKeys.URL_ENTRY_TYPE, def.getId());
			model.put(WebKeys.ADD_USER_URL, adapterUrl.toString());

			// Add the flag that indicates the "Create new account" ui should be available.
			model.put(WebKeys.ADD_USER_ALLOWED, "true");
		}
	}

	
	/**
	 * This method determines if self-registration is available.
	 * Self-registration is available if the logged in user has rights
	 * to add a user and we are not running the enterprise version
	 * of Vibe.
	 * 
	 * @param bs
	 * 
	 * @return
	 */
	public static boolean canDoSelfRegistration(AllModulesInjected bs) {
		boolean	canAdd	= false;
    	try {
    		// Can the logged in user add an entry to the profile binder?
			if (doesGuestUserHaveAddRightsToProfileBinder(bs)) {
				// Yes, is the "Allow people to create their own accounts" option turned on?
				AuthenticationConfig authConfig = bs.getAuthenticationModule().getAuthenticationConfig();
				if ((null != authConfig) && authConfig.isAllowSelfRegistration()) {
					// Yes, are we running the Enterprise version of Vibe?
					if (!(ReleaseInfo.isLicenseRequiredEdition())) {
						// No, self registration is available.
						canAdd = true;
					}
				}
			}
    	}
    	catch (Exception e) {
    		// Nothing to do.  It just means that the Guest user
    		// doesn't not have rights to the Profile binder.
    	}
    	return canAdd;
	}
	
	
	/**
	 * This method determines if the guest user has the rights needed
	 * to add an entry to the profile binder.
	 * 
	 * @param bs
	 * 
	 * @return
	 */
	public static boolean doesGuestUserHaveAddRightsToProfileBinder(AllModulesInjected bs) {
		return getProfileModule().doesGuestUserHaveAddRightsToProfileBinder();
	}

	
	/**
	 * Returns a string to display for an exception.
	 * 
	 * @param e
	 * 
	 * @return
	 */
	public static String exToString(Exception e) {
		String reply;
		if (null == e) {
			reply = "<null>";
		}
		else {
			reply = e.getLocalizedMessage();
			if ((null == reply) || reply.equalsIgnoreCase("null")) {
				reply = e.getMessage();
				if ((null == reply) || reply.equalsIgnoreCase("null")) {
					reply = e.toString();
					if (null == reply) {
						reply = "<message not available>";
					}
				}
			}
		}
		return reply;
	}
	
	
	/**
	 * Returns true if nativeMobileApp mode is set, based on the
	 * RenderRequest and false otherwise.
	 * 
	 * @param request
	 * 
	 * @return
	 */
	public static boolean isNativeMobileApp(RenderRequest request) {
		String param = PortletRequestUtils.getStringParameter(request, WebKeys.URL_NATIVE_MOBILE_APP, "");
		boolean reply;
		if (hasString(param)) {
			reply = Boolean.valueOf(param);
		}
		else {
	        HttpSession session = ((HttpServletRequestReachable) request).getHttpServletRequest().getSession();
	        Boolean captive = ((Boolean) session.getAttribute(WebKeys.URL_NATIVE_MOBILE_APP));
	        if (null == captive)
	        	 reply = false;
	        else reply = captive.booleanValue();
		}
		return reply;
	}
	
	
	/**
	 * Splits the String(s) in a RenderRequest property into their
	 * constituent Long values.
	 * 
	 * @param request
	 * @param requestKey
	 * @param model
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static List splitUserIds(RenderRequest request, String requestKey) {
		return splitUserIds(request, requestKey, null);
	}
	
	@SuppressWarnings("unchecked")
	public static List splitUserIds(RenderRequest request, String requestKey, Map model) {
		// Are there any values in the request parameter?
		ArrayList userIds = new ArrayList();
		String[] uids = PortletRequestUtils.getStringParameters(request, requestKey);
		int uidsCount = ((null == uids) ? 0 : uids.length);
		if (0 < uidsCount) {
			// Yes!  Is there only one that contains a ','?
			if ((1 == uidsCount) && (0 < uids[0].indexOf(','))) {
				// Yes!  Then it's a comma separated list of values.
				// Split them up.
				uids = uids[0].split(",");
				uidsCount = ((null == uids) ? 0 : uids.length);
			}
			
			// Scan the values...
			StringBuffer uidsBuf = new StringBuffer();
			for (int i = 0; i < uidsCount; i += 1) {
				// ...adding each to the StringBuffer...
				if (0 < i) {
					uidsBuf.append(",");
				}
				String uid = uids[i].trim();
				uidsBuf.append(uid);
				
				// ...and to the ArrayList.
				userIds.add(Long.valueOf(uid));
			}
			
			// If we were requested to put the string values into a
			// model...
			if (null != model) {
				// ...put them there.
				model.put(requestKey, uidsBuf.toString());
			}
		}
		
		// If we get here, userIds refers to an ArrayList of the Long
		// values from the request property.  Return it.
		return userIds;
	}

	/**
	 * Looks in formData for a title.  If one is found that's empty AND
	 * fileMap contains an uploaded file specification, the filename is
	 * used as the title.
	 * 
	 * @param fileMap
	 * @param formData
	 */
	@SuppressWarnings("unchecked")
	public static Map defaultTitleToFilename(Map fileMap, Map formData) {
		// Did we get the maps we need to work with?
		if ((null != fileMap) && (null != formData)) {
			// Yes!  Did we find an empty title in the form data?
			Object titleO = formData.get(WebKeys.URL_ENTRY_TITLE);
			String title = null;
			if ((null != titleO) && (titleO instanceof String[])) {
				String[] titleA = ((String[]) titleO);
				if (0 < titleA.length) {
					title = titleA[0];
				}
			}
			if ((null != title) && (0 == title.length())) {
				// Yes!  Do we have an uploaded file?
		    	MultipartFile myFile = ((MultipartFile) fileMap.get(WebKeys.URL_ENTRY_UPLOAD));
		    	String fileName = ((null == myFile) ? null : myFile.getOriginalFilename());
		    	if ((null != fileName) && (0 < fileName.length())) {
		    		// Yes!  Use the upload file's name as the title.
		    		HashMap formDataCopy = new HashMap();
		    		formDataCopy.putAll(formData);
		    		formData = formDataCopy;
					formData.put(WebKeys.URL_ENTRY_TITLE, new String[]{fileName});
		    	}
			}
		}
		return formData;
	}

	/**
	 * Returns the entriesPerPage to use based on the settings in the
	 * userProperites.  We're abstracting this here because there are no
	 * saved userProperties for the guest user.
	 * 
	 * @param userProperties
	 * @return
	 */
	public static String entriesPerPage(UserProperties userProperties) {
		return
			entriesPerPage(
				(null == userProperties) ?
					null                 :
					userProperties.getProperties());
	}
	
	@SuppressWarnings("unchecked")
	public static String entriesPerPage(Map propMap) {
		return
			getPropertyFromMap(
				propMap,
				ObjectKeys.PAGE_ENTRIES_PER_PAGE,
				SPropsUtil.getString(
					"folder.records.listed",
					"25"));
	}

	/**
	 * Validates and returns a String that's safe for inclusion in a
	 * logging call.
	 * 
	 * @param s
	 * 
	 * @return
	 */
	public static String getSafeLogString(String s) {
		String reply;
		if      (null == s)          reply = "*null*";
		else if (0    == s.length()) reply = "*empty*";
		else                         reply = s;
		return reply;
	}
	
	@SuppressWarnings("unchecked")
	public static String getUserProperty(UserProperties userProperties, String property, String defValue) {
		Map	propMap;
		if (null == userProperties) propMap = null;
		else                        propMap = userProperties.getProperties();
		return getPropertyFromMap(propMap, property, defValue);
	}
	
	@SuppressWarnings("unchecked")
	private static String getPropertyFromMap(Map propMap, String property, String defValue) {
		String reply;
		if (null == propMap) reply = defValue;
		else                 reply = ((String) propMap.get(property));
		return reply;
	}

	/**
	 * Returns true if a Collection has anything in it and false
	 * otherwise.
	 * 
	 * @param c
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static boolean hasItems(Collection c) {
		return ((null != c) && (!(c.isEmpty())));
	}
	
	/**
	 * Returns true if a Map has anything in it and false otherwise.
	 * 
	 * @param m
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static boolean hasItems(Map m) {
		return ((null != m) && (!(m.isEmpty())));
	}
	
	/**
	 * Returns true is s refers to a non null, non 0 length String and
	 * false otherwise.
	 * 
	 * @param s
	 * 
	 * @return
	 */
	public static boolean hasString(String s) {
		return ((null != s) && (0 < s.length()));
	}

	/**
	 * Returns the path to static files
	 * 
	 * @return
	 */
	public static String getStaticPath() {
		return (ObjectKeys.STATIC_DIR + "/" + SPropsUtil.getString(ObjectKeys.STATIC_DIR_PROPERTY, "repair") + "/");
	}
	
	/**
	 * Returns the full path to static files
	 * 
	 * @return
	 */
	public static String getFullStaticPath(HttpServletRequest request) {
		String contextPath = request.getContextPath();
		if (contextPath.endsWith("/")) {
			contextPath = contextPath.substring(0,contextPath.length()-1);
		}
		return
			(request.getContextPath() + "/" +
			 ObjectKeys.STATIC_DIR    + "/" + 
			 SPropsUtil.getString(ObjectKeys.STATIC_DIR_PROPERTY, "repair") + "/");
	}
	
	/**
	 * Returns true if we're in captive mode, based on the
	 * RenderRequest and false otherwise.
	 * 
	 * @param request
	 * 
	 * @return
	 */
	public static boolean isCaptive(RenderRequest request) {
		String captiveParam = PortletRequestUtils.getStringParameter(request, WebKeys.URL_CAPTIVE, "");
		boolean reply;
		if (hasString(captiveParam)) {
			reply = Boolean.valueOf(captiveParam);
		}
		else {
	        HttpSession session = ((HttpServletRequestReachable) request).getHttpServletRequest().getSession();
	        Boolean captive = ((Boolean) session.getAttribute(WebKeys.CAPTIVE));
	        if (null == captive)
	        	 reply = false;
	        else reply = captive.booleanValue();
		}
		return reply;
	}

	/**
	 * Returns true if an email address is valid and false otherwise.
	 * 
	 * @param ema
	 * 
	 * @return
	 */
	public static boolean isEmailAddressValid(String usedAs, String ema) {
		return(null != validateEmailAddress(usedAs, ema));
	}
	
	/**
	 * Performs a collated compare on two strings without generating any
	 * exceptions.
	 * 
	 * @param s1
	 * @param s2
	 * @param collator
	 * 
	 * @return
	 */
	public static int safeSColatedCompare(String s1, String s2, Collator collator) {
		return
			collator.compare(
				((null == s1) ? "" : s1),
				((null == s2) ? "" : s2));
   }

	/**
	 * Performs a collated compare on two strings without generating
	 * any exceptions.
	 * 
	 * @param s1
	 * @param s2
	 * 
	 * @return
	 */
	public static int safeSColatedCompare(String s1, String s2) {
		Collator collator = Collator.getInstance(RequestContextHolder.getRequestContext().getUser().getLocale());
		collator.setStrength(Collator.IDENTICAL);
		return safeSColatedCompare(s1, s2, collator);
   }

	/**
	 * Converts a String to an int protected against any exceptions.
	 * 
	 * @param s
	 * @param def
	 * 
	 * @return
	 */
	public static int safeSToInt(String s, int def) {
		int reply = def;
		if (hasString(s)) {
			try                  {reply = Integer.parseInt(s);}
			catch (Exception ex) {reply = def;                }
		}
		return reply;
	}
	
	/**
	 * Returns the ZoneInfo for the zone we're currently running under.
	 * 
	 * @return
	 */
	public static ZoneInfo getCurrentZone() {
		return getZoneModule().getZoneInfo(RequestContextHolder.getRequestContext().getZoneId());
	}
	
	/**
	 * Called to determine if there is an override specified for the
	 * from email address for eMails.  If there is, it is returned.
	 * If there isn't, null is returned.
	 * 
	 * For implementation details an logic, see the comments describing
	 * the 'ssf.outgoing.from.address' setting in the ssf.properties
	 * file.
	 * 
	 * @return
	 */
	public static String getFromOverride() {
		// Can we access the current zone name?
		ZoneInfo zoneInfo = getCurrentZone();
		String reply = zoneInfo.getZoneName();
		if (hasString(reply)) {
			// Yes!  Check for a zone specific setting.
			reply = SPropsUtil.getString((SPropsUtil.FROM_EMAIL_GLOBAL_OVERRIDE + "." + reply), ""); 
		}

		// Do we have a zone specific setting?
		if (!(hasString(reply))) {
			// No!  Check for a global setting.
			reply = SPropsUtil.getString(SPropsUtil.FROM_EMAIL_GLOBAL_OVERRIDE, "");
		}

		// Do we have a global setting?
		if (!(hasString(reply))) {
			// No!  Then ensure we return null.
			reply = null;
		}

		// If we get here, reply refers to the from override or is
		// null if there wasn't one.  Return it.
		return reply;
	}
	
	/**
	 * Called to determine whether a from override should be applied to
	 * all out bound email or only those from the system.
	 * 
	 * @return
	 */
	public static boolean isFromOverrideForAll() {
		// Can we access the current zone name?
		ZoneInfo zoneInfo = getCurrentZone();
		String value = zoneInfo.getZoneName();
		if (hasString(value)) {
			// Yes!  Check for a zone specific setting.
			value = SPropsUtil.getString((SPropsUtil.FROM_EMAIL_GLOBAL_OVERRIDE_ALL + "." + value), ""); 
		}

		// Do we have a zone specific setting?
		if (!(hasString(value))) {
			// No!  Check for a global setting.
			value = SPropsUtil.getString(SPropsUtil.FROM_EMAIL_GLOBAL_OVERRIDE_ALL, "");
		}

		// Do we have a global setting?
		boolean reply;
		if (!(hasString(value))) {
			// No!  Then ensure we return false.
			reply = false;
		}
		else {
			// Otherwise, return true if the setting is true.
			reply = value.equalsIgnoreCase("true");
		}

		// If we get here, reply is true of the from override should
		// apply to all outgoing email and false otherwise.  Return it.
		return reply;
	}
	
	/**
	 * Validates a Collection<InternetAddress> as containing valid
	 * InternetAddress's.
	 * 
	 * A separate Collection<InternetAddress> is returned containing
	 * only those InternetAddress's from the original that are valid.
	 * Invalid InternetAddress's are logged and dropped.
	 * 
	 * @param usedAs
	 * @param addrs
	 * 
	 * @return
	 */
	public static Collection<InternetAddress> validateInternetAddressCollection(String usedAs, Collection<InternetAddress> addrs) {
		// Do we have any addresses to validate?
		Collection<InternetAddress> reply = new ArrayList<InternetAddress>();
		if (null != addrs) {
			// Yes!  Scan them.
			for (Iterator<InternetAddress> iaIT = addrs.iterator(); iaIT.hasNext(); ) {
				// Was this InternetAddress valid?
				InternetAddress ia = validateIA(usedAs, iaIT.next());
				if (null != ia) {
					// Yes!  Add it to the validated collection.
					reply.add(ia);
				}
			}
		}

		// If we get here, reply refers to a
		// Collection<InternetAddress> of the valid InternetAddress's
		// from the Collection<InternetAddress> passed in.  Return it.
		return reply;
	}
	
	/**
	 * Validates a Collection<String> as containing valid email
	 * addresses.
	 * 
	 * A separate Collection<String> is returned containing only those
	 * email addresses from the original that are valid.  Invalid email
	 * addresses are logged and dropped.
	 * 
	 * @param usedAs
	 * @param addrs
	 * 
	 * @return
	 */
	public static Collection<String> validateEmailAddressCollection(String usedAs, Collection<String> addrs) {
		// Do we have any email addresses to validate?
		Collection<String> reply = new ArrayList<String>();
		if (null != addrs) {
			// Yes!  Scan them.
			for (Iterator<String> itEA = addrs.iterator(); itEA.hasNext();) {
				// Is this email address valid?
				String ea = itEA.next();
				InternetAddress ia = new InternetAddress();
				try {ia.setAddress(ea);} catch (Exception e) {};
				if (null != validateIA(usedAs, ia)) {
					// Yes!  Add it to the validated collection.
					reply.add(ea);
				}
			}
		}
		
		// If we get here, reply refers to a
		// Collection<String> of the valid email addresses from the
		// Collection<String> passed in.  Return it.
		return reply;
	}

	/**
	 * Given an email address in string form, returns the corresponding
	 * InternetAddress if the email address is valid and false
	 * otherwise.
	 * 
	 * @param usedAs
	 * @param ema
	 * 
	 * @return
	 */
	public static InternetAddress validateEmailAddress(String usedAs, String ema) {
		// If we we don't have an email address to validate...
		ema = ((null == ema) ? "" : ema.trim()); 
		if (0 == ema.length()) {
			// ...return null.
			return null;
		}

		// Otherwise, construct an InternetAddress from it...
		InternetAddress ia = new InternetAddress();
		try {ia.setAddress(ema);} catch (Exception e) {};
		
		// ...and validate that.
		return validateIA(usedAs, ia);
	}
	
	/*
	 * Validates an InternetAddress as containing a valid email
	 * address.
	 * 
	 * If it does, it is returned.  If it doesn't, an error is logged
	 * and null is returned.
	 */
	private static InternetAddress validateIA(String usedAs, InternetAddress ia) {
		try {
			// Is this InternetAddress valid?
			ia.validate();
		}
		catch (Exception ex) {
			// No!  Log the error...
			StringBuffer error = new StringBuffer(usedAs + ":  invalid InternetAddress dropped");
			error.append(getIAPartString("address",  ia.getAddress() ));
			error.append(getIAPartString("personal", ia.getPersonal()));
			error.append(getIAPartString("type",     ia.getType()    ));
			error.append(getIAPartString("toString", ia.toString()   ));
			m_logger.error(error.toString());
						
			// ...and forget about it.
			ia = null;

			// Was the exception we caught other than an
			// AddressException?
			if (!(ex instanceof AddressException)) {
				// Yes!  Then something deeper than just a bogus email
				// address is happening.  Lets log the exception too.
				m_logger.error("validateIA( EXCEPTION ):  ", ex);
			}
		}

		// If we get here, ia refers to the validated InternetAddress
		// or is null.  Return it.
		return ia;
	}
	
	/*
	 * Returns a String for constructing an error message about the
	 * invalid email address.
	 */
	private static String getIAPartString(String name, String value) {
		return (", " + name + ":  '" + ((null == value) ? "" : value) + "'");
	}
	
	/**
	 * Returns true if the FolderEntry is reserved and false otherwise.
	 * 
	 * Be careful when using this call from a jsp. It may do a database operation.
	 * Don't call this from places where the entry isn't already fetched (such as from a folder listing jsp).
	 * 
	 * @param binderId
	 * @param entryId
	 * 
	 * @return
	 */
	public static boolean isEntryReserved(String binderIdS, String entryIdS) {
		boolean reply = false;
		try {
			Long binderId = Long.parseLong(binderIdS);
			Long entryId  = Long.parseLong(entryIdS );
			FolderEntry fe = getFolderModule().getEntry(binderId, entryId);
			if (null != fe) {
				HistoryStamp reservation = fe.getReservation();
				if (null != reservation) {
					UserPrincipal principal = reservation.getPrincipal(); 
					reply = (null != principal);
				}
			}
		}
		catch (Exception e) {
			// Ignore.
		}
		return reply;
	}

	/**
	 * Return the url that repairs to the appropriate help
	 * documentation.
	 * 
	 * @param guideName
	 * @param pageId
	 * @param sectionId
	 * 
	 * @return
	 */
	public static String getHelpUrl(String guideName, String pageId, String sectionId) {
		// Get the base help url from ssf-ext.properties.
		String url = SPropsUtil.getString("help.hostName", "http://www.novell.com");
		
		// Do we have a language code to put on the url?
		String lang = getHelpLangCode();
		if (hasString(lang)) {
			// Yes
			url +=  "/" + lang;
		}
		url += "/documentation";
		
		String product = ("/" + SPropsUtil.getString("release.product", "vibe"));
		
		// Are we running Filr?
		boolean isFilr = Utils.checkIfFilr();
		if (isFilr) {
			// Yes
			url    += ("/" + SPropsUtil.getString("filr.release.product.novell", "novell-filr"));
			product = ("/" + SPropsUtil.getString("filr.release.product",        "filr"       ));
		}
		// Are we running Novell Vibe?
		else if (ReleaseInfo.isLicenseRequiredEdition())
		     url += ("/" + SPropsUtil.getString("release.product",         "vibe"       ));
		else url += ("/" + SPropsUtil.getString("release.product.kablink", "kablinkvibe"));
		
		String guideSeparator = (isFilr ? "-" : "_");
		String guideComponent = null;
		if (hasString(guideName)) {
			if (guideName.equalsIgnoreCase(USER_GUIDE)) {
				// Get the url to the user guide.
				guideComponent = product + guideSeparator + "user/data/";
			}
			else if (guideName.equalsIgnoreCase(ADV_USER_GUIDE)) {
				// Get the url to the advanced user guide.
				guideComponent = product + guideSeparator + "useradv/data/";
			}
			else if (guideName.equalsIgnoreCase(ADMIN_GUIDE)) {
				// Get the url to the administration guide.
				guideComponent = product + guideSeparator + "admin/data/";
			}
			else if (guideName.equalsIgnoreCase(INSTALLATION_GUIDE)) {
				// Get the url to the installation guide.
				guideComponent = product + guideSeparator + "inst/data/";
			}
			else {
				guideComponent = null;
			}
			
			// Did we recognize the name of the guide?
			if (null != guideComponent) {
				// Yes, add the guide component to the url.
				url += guideComponent;
				
				// Do we have a specific page to go to in the documentation?
				if (null != pageId) {
					// Yes, each page has its own HTML file.
					url += pageId + ".html";
					
					// Do we have a specific section within the page to go to?
					if (null != sectionId) {
						// Yes
						url += "#" + sectionId;
					}
				}
				else {
					// No, take the user to the start of the guide.
					url += "bookinfo.html";
					if (isFilr) {
						url = replace(url, "user/data", "user-web/data");
					}
				}
			}
		}

		return url;
	}

	/*
	 * Return the language code that should be put on the help url.
	 */
	private static String getHelpLangCode() {
		// Get the language the user is running in
		String lang = NLT.get("Teaming.Lang", "");
		String originalLang = lang;
		
		// Do we know the language? 
		if (!(hasString(lang))) {
			// No
			return null;
		}
		
		// Is the language English?
		if (0 == lang.indexOf("en")) {
			// Yes, we don't need to put a language code on the url.
			return null;
		}

		// We only need the first two characters of the language to
		// localize the documentation URLs.
		if (lang.length() > 2) {
			lang = lang.substring(0, 2); 
		}

		// Is the language Chinese?
		if (lang.equalsIgnoreCase("zh")) {
			// Yes, use the full language string of zh-tw or zh-cn
			lang = originalLang.toLowerCase();
			lang = lang.replace('_', '-');
		}

		// Look for the appropriate language code.
		int i;
		for (i = 0; i < DOC_LANGS.length; i += 1) {
			if (0 == DOC_LANGS[i].indexOf(lang)) {
				break;
			}
		}		

		// Do we have a language code for this language?
		if (i == DOC_LANGS.length) {
			// No
			return null;
		}

		return DOC_LANGS[i];
	}
	
	/**
	 * Returns true if we're to run in HTML standards mode and false
	 * otherwise.
	 * 
	 * @return
	 */
	public static boolean isHtmlStandardsMode() {
		return SPropsUtil.getBoolean("html.standards.mode", true);
	}
	
	public static boolean isHtmlQuirksMode() {
		return (!(isHtmlStandardsMode()));
	}
	
	/**
	 * Replaces all occurrences of oldSub with newSub in s.
	 * 
	 * The implementation was copied from StringUtil.replace().
	 * 
	 * @param s
	 * @param oldSub
	 * @param newSub
	 * 
	 * @return
	 */
	public static String replace(String s, String oldSub, String newSub) {
		if ((null == s) || (null == oldSub) || (null == newSub)) {
			return null;
		}

		int y = s.indexOf(oldSub);
		if (y >= 0) {
			StringBuffer sb = new StringBuffer();
			int length = oldSub.length();
			int x = 0;

			while (x <= y) {
				sb.append(s.substring(x, y));
				sb.append(newSub);
				x = y + length;
				y = s.indexOf(oldSub, x);
			}

			sb.append(s.substring(x));
			return sb.toString();
		}
		
		else {
			return s;
		}
	}
	
	/**
	 * Returns a List<IdTriple> of the binder ID/entry ID/entity type
	 * stored in a multiple entity ID string.
	 * 
	 * @param multipleEntityIds
	 * 
	 * @return
	 */
	public static List<IdTriple> getIdTriplesFromMultipleEntryIds(String multipleEntityIds) {
		List<IdTriple> reply = new ArrayList<IdTriple>();
		if (hasString(multipleEntityIds)) {
			String[] meIds = multipleEntityIds.split(",");
			for (String meId:  meIds) {
				String[] eId = meId.split(":");
				reply.add(new IdTriple(Long.parseLong(eId[0]), Long.parseLong(eId[1]), eId[2]));
			}
		}
		return reply;
	}
	
	/**
	 * Validates that a Collection<Long> is non-null.
	 * 
	 * @param lC
	 * 
	 * @return
	 */
	public static Collection<Long> validateCL(Collection<Long> lC) {
		return ((null == lC) ? new HashSet<Long>() : lC);
	}
	
	/**
	 * Validates that a Collection<String> is non-null.
	 * 
	 * @param lS
	 * 
	 * @return
	 */
	public static Collection<String> validateCS(Collection<String> sC) {
		return ((null == sC) ? new HashSet<String>() : sC);
	}
	
	/**
	 * Validates that a List<InternetAddress> is non-null.
	 * 
	 * @param iaList
	 * 
	 * @return
	 */
	public static List<InternetAddress> validateIAL(List<InternetAddress> iaList) {
		return ((null == iaList) ? new ArrayList<InternetAddress>() : iaList);
	}

	/**
	 * ?
	 *  
	 * @param localeId
	 * 
	 * @return
	 */
    public static Locale findLocale(String localeId) {
        for (Locale locale : NLT.getLocales()) {
            if (locale.toString().equals(localeId)) {
                return locale;
            }
        }
        return null;
    }
    
	/**
	 * Validates that a List<Locale> is non-null.
	 * 
	 * @param lList
	 * 
	 * @return
	 */
	public static List<Locale> validateLL(List<Locale> lList) {
		return ((null == lList) ? new ArrayList<Locale>() : lList);
	}
	
	/*
	 * Converts a byte[] to a string of hex characters.
	 */
	private static String baToHS(final byte[] b) {
		final StringBuffer sb = new StringBuffer(b.length * 2);
		final int baLen = b.length;
		for (int i = 0; i < baLen; i += 1) {
			int v = (b[i] & 0xff);
			if (v < 16) {
				sb.append('0');
			}
			sb.append(Integer.toHexString(v));
		}
		return sb.toString();
	}
	
	/**
	 * Returns the MD5 hash from a byte[].
	 * 
	 * @param dataBytes
	 * 
	 * @return
	 * 
	 * @throws Exception
	 */
	public static String getMD5Hash(byte[] dataBytes) throws Exception {
		// Read the data through the MD5 digest...
	    InputStream		input     = new ByteArrayInputStream(dataBytes);
	    byte[]			buffer    = new byte[1024];
	    MessageDigest	md5Digest = MessageDigest.getInstance("MD5");
	    int				read;
	    do {
	        read = input.read(buffer);
	        if (read > 0) {
	            md5Digest.update(buffer, 0, read);
	        }
	    } while (read != (-1));
	    input.close();

	    // ...and return the hash as a string.
	    return baToHS(md5Digest.digest());
	}
	
	public static String getMD5Hash(String data) throws Exception {
		// Always use the initial form of the method.
		return getMD5Hash(data.getBytes());
	}

	/**
	 * Given a DefinableEntity that's a FolderEntry, returns the
	 * FileAttachment to use as the entity's primary file attachment,
	 * if available.  Otherwise, returns null.
	 * 
	 * @param de
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static FileAttachment getPrimaryFileAttachment(DefinableEntity de) {
		SimpleProfiler.start("MiscUtil.getPrimaryFileAttachment()");
		try {
			// Do we have a DefinableEntity that's a FolderEntry?
			FileAttachment reply = null;
			if ((null != de) && (de instanceof FolderEntry)) {
				// Yes!  Does that entry have a primary file attribute?
				FolderEntry fe = ((FolderEntry) de);
				Map model  = new HashMap();
				DefinitionHelper.getPrimaryFile(fe, model);
				String attrName = ((String) model.get(WebKeys.PRIMARY_FILE_ATTRIBUTE));
				if (hasString(attrName)) {
					// Yes!  Can we access the custom attribute values for
					// that attribute?
					CustomAttribute ca = fe.getCustomAttribute(attrName);
					if (null != ca) {
						// Yes!  Does it contain any FileAttachment's?
						Collection values = ca.getValueSet();
						if (hasItems(values)) {
							// Yes!  Return the first one.
							reply = ((FileAttachment) values.iterator().next());
						}
					}
				}
		
				// Do we have the FileAttachment for the entry yet?
				if (null == reply) {
					// No!  Does it have any attachments?
					Collection<FileAttachment> atts = fe.getFileAttachments();
					if (hasItems(atts)) {
						// Yes!  Return the first one.
						reply = ((FileAttachment) atts.iterator().next());
					}
				}
			}
	
			// If we get here, reply refers to the DefinableEntity's
			//primary file attachment or is null if one can't be
			//determined.  Return it.
			return reply;
		}
		
		finally {
			SimpleProfiler.stop("MiscUtil.getPrimaryFileAttachment()");
		}
	}

    public static String getPrimaryFileName(DefinableEntity de) {
        String fName = null;
        FileAttachment fa = getPrimaryFileAttachment(de);
        FileItem fi = fa.getFileItem();
        if (null != fi) {
            fName = fi.getName();
        }
        return fName;
    }

	/**
	 * Returns the display string for the product name.
	 * 
	 * @param user
	 * 
	 * @return
	 */
	public static String getProductName(User user) {
		String keyTail;
		if (Utils.checkIfFilr())
		     keyTail = "filr";
		else keyTail = "vibe";
		return NLT.get(("productName." + keyTail), user.getLocale());
	}
	
	public static String getProductName() {
		// Always use the initial form of the method.
		return getProductName(RequestContextHolder.getRequestContext().getUser());
	}

	/**
	 * If there is more than one entry in a List<String>, it is sorted.
	 * Simply returns the list it was given, sorted or otherwise. 
	 *  
	 * @param ls
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static List<String> sortStringList(List<String> ls) {
		if ((null != ls) && (1 < ls.size())) {
			StringComparator sc = new StringComparator(RequestContextHolder.getRequestContext().getUser().getLocale());
			Collections.sort(ls, sc);
		}
		return ls;
	}

	/**
	 * ?
	 * 
	 * @param filename
	 * 
	 * @return
	 */
    public static boolean isPdf(String filename) {
        boolean isPdf = false;
        if (hasString(filename)) {
            int pPos = filename.lastIndexOf('.');
            if (0 < pPos) {
                isPdf = filename.substring(pPos).toLowerCase().equals(".pdf");
            }
        }
        return isPdf;
    }

	/*
	 * Returns true if the NPAPIs are supported in the current browser
	 * and false otherwise.  Without NPAPI, there can be no Java
	 * applets.
	 */
	private static boolean isNPAPISupportedImpl(HttpServletRequest req) {
		// Are we running on Chrome?
		if (BrowserSniffer.is_chrome(req)) {
			// Yes!  Can we find the Chrome agent marker so we can
			// split out its version and platform?
			String userAgent = req.getHeader(HttpHeaders.USER_AGENT).toLowerCase();
			int chromePos = userAgent.indexOf(CHROME_AGENT_MARKER);
			if (0 < chromePos) {
				// Yes!  Is there a space following the Chrome agent
				// marker?  
				String chromePart = userAgent.substring(chromePos + CHROME_AGENT_MARKER.length());
				int spacePos = chromePart.indexOf(' ');
				if (0 < spacePos) {
					// Yes!  Can we parse it as a Chrome version
					// string?
					int[] vsn = parseChromeVersion(chromePart.substring(0, spacePos));
					if (null != vsn) {
						// Yes!  Can we find the platform information
						// in the user agent string?
						int prenPos = userAgent.indexOf('(');
						int nerpPos = userAgent.indexOf(')');
						int semiPos = userAgent.indexOf(';');
						if (    (prenPos < chromePos) && (0       < prenPos) &&		// If there's a '(' before the 'Chrome/' and before the first ')'...
								(nerpPos < chromePos) && (nerpPos > prenPos) &&		// ...and the ')' is before the 'Chrome/' and after the first '('...
								(semiPos > prenPos)   && (semiPos < nerpPos)) {		// ...and there's a ';' between the '(' and ')'...
							// Yes!  Can we determine the version NPAPI				// ...it should be the osString.
							// support was dropped from Chrome on that
							// platform?
							String platformS = userAgent.substring((prenPos + 1), nerpPos);
							BrowserPlatform platform;
							if      (platformS.contains("linux"    )) platform = BrowserPlatform.LINUX;
							else if (platformS.contains("macintosh")) platform = BrowserPlatform.MAC;
							else if (platformS.contains("windows"  )) platform = BrowserPlatform.WINDOWS;
							else                                      platform = BrowserPlatform.UNKNOWN;
							int[] npapiDropped = getChromeNPAPIDroppedVersion(platform);
							if (null != npapiDropped) {
								// Yes!  Given Chrome's version and the
								// version NPAPI was support was
								// dropped on the platform, does this
								// Chrome version support NPAPI?
								return isNPAPISupportedInChrome(vsn, npapiDropped);
							}
						}
					}
				}
			}
		}
		
		else if (BrowserSniffer.is_edge(req)) {
			// NPAPI is not supported using the Edge browser on Windows
			// 10.
			return false;
		}
		
		// If we get here, we assume the NPAPIs are supported.  Return
		// true.
		return true;
	}
	
	/**
	 * Returns true if the NPAPIs are supported in the current browser
	 * and false otherwise.  Without NPAPI, there can be no Java
	 * applets.
	 * 
	 * @param req
	 * 
	 * @return
	 */
	public static boolean isNPAPISupported(HttpServletRequest req) {
		// Do have whether NPAPI's are supported cached in the session?
		HttpSession session = req.getSession();
		Boolean npapiSupported = ((Boolean) session.getAttribute(BROWSER_SUPPORTS_NPAPI));
		if (null == npapiSupported) {
			// No!  Determine whether they are...
			npapiSupported = Boolean.valueOf(isNPAPISupportedImpl(req));
			
			// ...and cache that value.
			session.setAttribute(BROWSER_SUPPORTS_NPAPI, npapiSupported);
		}
		
		// If we get here, npapiSupported is true if the NPAPI's are
		// supported and false otherwise.  Return it.
		return npapiSupported.booleanValue();
	}

	/*
	 * Parses and returns the constituent parts of a Chrome version
	 * string.
	 */
	private static int[] parseChromeVersion(String version) {
		String[] versionParts = version.split(Pattern.quote("."));
		if ((null != versionParts) && (CHROME_VERSION_PARTS == versionParts.length)) {
			int[] reply = new int[CHROME_VERSION_PARTS];
			for (int i = 0; i < CHROME_VERSION_PARTS; i += 1) {
				try {reply[i] = Integer.parseInt(versionParts[i]);}
				catch (Exception e) {return null;}
			}
			return reply;
		}
		return null;
	}
	
	/*
	 * Given the current version of Chrome and the version NPAPI
	 * support was dropped in Chrome, returns true if NPAPIs are
	 * supported and false otherwise. 
	 */
	private static boolean isNPAPISupportedInChrome(int[] currentVersion, int[] npapiDroppedVersion) {
		int currentMajor  = currentVersion[0];
		int currentMinor  = currentVersion[1];
		int currentPoint  = currentVersion[2];
		int currentRepair = currentVersion[3];
		
		int droppedMajor  = npapiDroppedVersion[0];
		int droppedMinor  = npapiDroppedVersion[1];
		int droppedPoint  = npapiDroppedVersion[2];
		int droppedRepair = npapiDroppedVersion[3];
		
		if (currentMajor  > droppedMajor)  return false;	// Current major > dropped:  No NPAPI.   
		if (currentMajor  < droppedMajor)  return true;		// Current major < dropped:  NPAPI should be there.
		if (currentMinor  > droppedMinor)  return false;	// Current major = dropped, current minor > dropped:  No NPAPI.
		if (currentMinor  < droppedMinor)  return true;		// Current major = dropped, current minor < dropped:  NPAPI should be there.
		if (currentPoint  > droppedPoint)  return false;	// Current major = dropped, current minor = dropped, current point > dropped:  No NPAPI
		if (currentPoint  < droppedPoint)  return false;	// Current major = dropped, current minor = dropped, current point < dropped:  NPAPI should be there.
		if (currentRepair > droppedRepair) return false;	// Current major = dropped, current minor = dropped, current point = dropped, current repair > dropped:  No NPAPI
		if (currentRepair < droppedRepair) return false;	// Current major = dropped, current minor = dropped, current point = dropped, current repair < dropped:  NPAPI should be there.

		// Current major = dropped, current minor  = dropped,
		// current point = dropped, current repair = dropped:  No NPAPI. 
		return false;
	}

	/*
	 * Returns the appropriate version of Chrome in which NPAPI support
	 * was dropped for the given platform.
	 */
	private static int[] getChromeNPAPIDroppedVersion(BrowserPlatform platform) {
		String key = "chrome.version.npapi.dropped.";
		switch (platform) {
		case LINUX:    key += "linux";   break;
		case MAC:      key += "mac";     break;
		case WINDOWS:  key += "windows"; break;
		default:       return null;
		}
		return parseChromeVersion(SPropsUtil.getString(key, ""));
	}
	
	/**
	 * Returns true if the entity by the given ID is in the trash and
	 * false otherwise.
	 * 
	 * @param entryId
	 * 
	 * @return
	 */
	public static boolean isEntryPreDeleted(Long entryId) {
		boolean reply = false; 
		try {
			FolderEntry fe = getFolderModule().getEntry(null, entryId);
			reply = fe.isPreDeleted();
		}
		catch (Exception e) {/* Ignore. */}
		return reply;
	}
	
	/**
	 * Returns the ID of the 'Can Only See Members of Group I Am In'
	 * role.
	 * 
	 * @return
	 */
	public static Long getCanOnlySeeMembersOfGroupsIAmInRoleId() {
		List<Function> fs  = getAdminModule().getFunctions();
		Long reply = null;
		for (Function f:  fs) {
			String fId = f.getInternalId();
			if (hasString(fId)) {
				if (fId.equalsIgnoreCase(ObjectKeys.FUNCTION_ONLY_SEE_GROUP_MEMBERS_INTERNALID)) {
					reply = f.getId();
					break;
				}
			}
		}
		return reply;
	}

	/**
	 * Returns the ID of the 'Override - Can Only See Members of Group
	 * I Am In' role.
	 * 
	 * @return
	 */
	public static Long getOverrideCanOnlySeeMembersOfGroupsIAmInRoleId() {
		List<Function> fs  = getAdminModule().getFunctions();
		Long reply = null;
		for (Function f:  fs) {
			String fId = f.getInternalId();
			if (hasString(fId)) {
				if (fId.equalsIgnoreCase(ObjectKeys.FUNCTION_OVERRIDE_ONLY_SEE_GROUP_MEMBERS_INTERNALID)) {
					reply = f.getId();
					break;
				}
			}
		}
		return reply;
	}

	/**
	 * Returns the ID of the site admin role.
	 * 
	 * @return
	 */
	public static Long getSiteAdminRoleId() {
		List<Function> fs  = getAdminModule().getFunctions();
		Long reply = null;
		for (Function f:  fs) {
			String fId = f.getInternalId();
			if (hasString(fId)) {
				if (fId.equalsIgnoreCase(ObjectKeys.FUNCTION_SITE_ADMIN_INTERNALID)) {
					reply = f.getId();
					break;
				}
			}
		}
		return reply;
	}

	/**
	 * Returns the DefinableEntity from a List<DefinableEntity> that
	 * corresponds to the given ID.
	 * 
	 * If one is not found, null is returned.
	 * 
	 * @param deList
	 * @param deid
	 * 
	 * @return
	 */
	public static DefinableEntity findDEInDEListById(List<DefinableEntity> deList, Long deid) {
		if (null != deList) {
			for (DefinableEntity de:  deList) {
				if (de.getId().equals(deid)) {
					return de;
				}
			}
		}
		return null;
	}
	
	/**
	 * Returns the User from a List<Principal> that corresponds to the
	 * given user ID.
	 * 
	 * If one is not found, null is returned.
	 * 
	 * @param pList
	 * @param uid
	 * 
	 * @return
	 */
	public static User findUserInPListById(List<Principal> pList, Long uid) {
		if (null != pList) {
			for (Principal p:  pList) {
				if (p.getId().equals(uid)) {
					return ((User) p);
				}
			}
		}
		return null;
	}
	
	/**
	 * Returns a List<Principal> of the Principal's associated with the
	 * Subscription's in a List<Subscription>.
	 * 
	 * @param subs
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static List<Principal> resolveSubscriptionPrincipals(List<Subscription> subs) {
		SimpleProfiler.start("MiscUtil.resolveSubscriptionPrincipals()");
    	try {
			List<Principal> reply;
			if (null != subs) {
				List<Long> uids = new ArrayList<Long>();
				for (Subscription sub:  subs) {
					ListUtil.addLongToListLongIfUnique(uids, sub.getId().getPrincipalId());
				}
				reply = ResolveIds.getPrincipals(uids);
			}
			else {
				reply = null;
			}
			return reply;
    	}
    	
    	finally {
    		SimpleProfiler.stop("MiscUtil.resolveSubscriptionPrincipals()");
    	}
	}

	/**
	 * Returns a localized String built from a VirusDetectedError for
	 * displaying the error to the user.
	 *  
	 * @param error
	 * 
	 * @return
	 */
	public static String getLocalizedVirusDetectedErrorString(VirusDetectedError error) {
		String   messageKey;
		switch (error.getType()) {
		default:
		case Other:                       messageKey = "virus.error.other";             break;
		case PolicyRestrictionViolation:  messageKey = "virus.error.policyRestriction"; break;
		case Virus:                       messageKey = "virus.error.virus";             break;
		}
		
		String message  = error.getMessage();
		List<String> patches = new ArrayList<String>();
		patches.add(error.getFileName());
		boolean hasMessage = MiscUtil.hasString(message);
		if (hasMessage) {
			patches.add(message);
			messageKey += ".message";
		}
		return NLT.get(messageKey, patches.toArray(new String[0]));
	}
	
	/**
	 * Returns a list of localized String's built from a list of
	 * VirusDetectedError's for displaying the error to the user.
	 *  
	 * @param errors
	 * 
	 * @return
	 */
	public static List<String> getLocalizedVirusDetectedErrorStrings(List<VirusDetectedError> errors) {
		List<String> reply = new ArrayList<String>();
		if (hasItems(errors)) {
			for (VirusDetectedError error:  errors) {
				reply.add(getLocalizedVirusDetectedErrorString(error));
			}
		}
		return reply;
	}
	
	/**
	 * Returns a separated list of Strings from a list of them.
	 * 
	 * @param errors
	 * @param separator
	 * 
	 * @return
	 */
	public static String getSeparatedErrorList(List<String> errors, String separator) {
		StringBuffer errorBuf = new StringBuffer();
		boolean first = true;
		for (String error:  errors) {
			if (!first) {
				errorBuf.append(separator);
			}
			errorBuf.append(error);
			first = false;
		}
		return errorBuf.toString();
	}
	
	/**
	 * Returns an instance of an AdminModule.
	 * 
	 * @return
	 */
	public static AdminModule getAdminModule() {
		if (null == m_adminModule) {
			m_adminModule = ((AdminModule) SpringContextUtil.getBean("adminModule"));
		}
		return m_adminModule;
	}

	/**
	 * Returns an instance of a BinderModule.
	 * 
	 * @return
	 */
	public static BinderModule getBinderModule() {
		if (null == m_binderModule) {
			m_binderModule = ((BinderModule) SpringContextUtil.getBean("binderModule"));
		}
		return m_binderModule;
	}

	/**
	 * Returns an instance of a CoreDao.
	 * 
	 * @return
	 */
	public static CoreDao getCoreDao() {
		if (null == m_coreDao) {
			m_coreDao = ((CoreDao) SpringContextUtil.getBean("coreDao"));
		}
		return m_coreDao;
	}
	
	/**
	 * Returns an instance of a FolderModule.
	 * 
	 * @return
	 */
	public static FolderModule getFolderModule() {
		if (null == m_folderModule) {
			m_folderModule = ((FolderModule) SpringContextUtil.getBean("folderModule"));
		}
		return m_folderModule;
	}

	/**
	 * Returns an instance of a ProfileModule.
	 * 
	 * @return
	 */
	public static ProfileModule getProfileModule() {
		if (null == m_profileModule) {
			m_profileModule = ((ProfileModule) SpringContextUtil.getBean("profileModule"));
		}
		return m_profileModule;
	}

	/**
	 * Returns an instance of a WorkspaceModule.
	 * 
	 * @return
	 */
	public static WorkspaceModule getWorkspaceModule() {
		if (null == m_wsModule) {
			m_wsModule = ((WorkspaceModule) SpringContextUtil.getBean("workspaceModule"));
		}
		return m_wsModule;
	}

	/**
	 * Returns an instance of a ZoneModule.
	 * 
	 * @return
	 */
	public static ZoneModule getZoneModule() {
		if (null == m_zoneModule) {
			m_zoneModule = ((ZoneModule) SpringContextUtil.getBean("zoneModule"));
		}
		return m_zoneModule;
	}
}
