/**
 * Copyright (c) 1998-2012 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2012 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2012 Novell, Inc. All Rights Reserved.
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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.portlet.RenderRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.domain.AuthenticationConfig;
import org.kablink.teaming.domain.Definition;
import org.kablink.teaming.domain.FolderEntry;
import org.kablink.teaming.domain.HistoryStamp;
import org.kablink.teaming.domain.UserPrincipal;
import org.kablink.teaming.domain.UserProperties;
import org.kablink.teaming.domain.ZoneInfo;
import org.kablink.teaming.module.folder.FolderModule;
import org.kablink.teaming.module.profile.ProfileModule;
import org.kablink.teaming.module.zone.ZoneModule;
import org.kablink.teaming.portletadapter.AdaptedPortletURL;
import org.kablink.teaming.portletadapter.portlet.HttpServletRequestReachable;
import org.kablink.teaming.util.AllModulesInjected;
import org.kablink.teaming.util.NLT;
import org.kablink.teaming.util.ReleaseInfo;
import org.kablink.teaming.util.SPropsUtil;
import org.kablink.teaming.util.SpringContextUtil;
import org.kablink.teaming.util.Utils;
import org.kablink.teaming.web.WebKeys;
import org.springframework.web.multipart.MultipartFile;

/**
 * This class contains a collection of miscellaneous utility methods.
 * 
 * @author jwootton
 */
public final class MiscUtil
{
	protected static Log m_logger = LogFactory.getLog(MiscUtil.class);
	
	// The following are used as the return values for the various
	// comparators.
	public final static int COMPARE_EQUAL	=   0;
	public final static int COMPARE_GREATER	=   1;
	public final static int COMPARE_LESS	= (-1);
	
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

	/**
	 * Class constructor that prevents this class from being instantiated.
	 */
	private MiscUtil()
	{
		// Nothing to do.
	}// end MiscUtil()
	
	
	/**
	 * Add all of the information needed to support the "Create new account" ui to the response.
	 */
	@SuppressWarnings("unchecked")
	public static void addCreateNewAccountDataToResponse(
		AllModulesInjected	bs,
		RenderRequest		request,
		Map<String,Object>	model )
	{
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
		if ( entryDefsIterator.hasNext() )
		{
			AdaptedPortletURL	adapterUrl;

			def = (Definition) entryDefsIterator.next();

			// Create the url needed to invoke the "Add User" page.
			adapterUrl = new AdaptedPortletURL( request, "ss_forum", true );
			adapterUrl.setParameter( WebKeys.ACTION, WebKeys.ACTION_ADD_PROFILE_ENTRY );
			adapterUrl.setParameter( WebKeys.URL_BINDER_ID, profileModule.getProfileBinderId().toString() );
			adapterUrl.setParameter( WebKeys.URL_ENTRY_TYPE, def.getId() );
			model.put( WebKeys.ADD_USER_URL, adapterUrl.toString() );

			// Add the flag that indicates the "Create new account" ui should be available.
			model.put( WebKeys.ADD_USER_ALLOWED, "true" );
		}
	}// end addCreateNewAccountDataToResponse()

	
	/**
	 * This method determines if self-registration is available.  Self-registration is available
	 * if the logged in user has rights to add a user and we are not running the enterprise version
	 * of Teaming.
	 */
	public static boolean canDoSelfRegistration( AllModulesInjected bs )
	{
		boolean	canAdd	= false;
		
    	try
    	{
    		// Can the logged in user add an entry to the profile binder?
			if ( doesGuestUserHaveAddRightsToProfileBinder( bs ) )
			{
				AuthenticationConfig	authConfig;
				
				// Yes, is the "Allow people to create their own accounts" option turned on?
				authConfig = bs.getAuthenticationModule().getAuthenticationConfig();
				if ( authConfig != null && authConfig.isAllowSelfRegistration() )
				{
					// Yes, are we running the Enterprise version of Teaming?
					if ( ReleaseInfo.isLicenseRequiredEdition() == false )
					{
						// No, self registration is available.
						canAdd = true;
					}
				}
			}
    	}
    	catch (Exception e)
    	{
    		// Nothing to do.  It just means that the Guest user doesn't not have rights to the Profile binder.
    	}
		
    	return canAdd;
	}// end canDoSelfRegistration()
	
	
	/**
	 * This method determines if the guest user has the rights needed to add an entry to
	 * the profile binder.
	 */
	public static boolean doesGuestUserHaveAddRightsToProfileBinder( AllModulesInjected bs )
	{
		ProfileModule	profileModule;

		profileModule = bs.getProfileModule();
		return profileModule.doesGuestUserHaveAddRightsToProfileBinder();
	}// end doesGuestUserHaveAddRightsToProfileBinder()

	
	/**
	 * Returns a string to display for an exception.
	 */
	public static String exToString( Exception e )
	{
		String reply;
		if ( null == e )
		{
			reply = "<null>";
		}
		else
		{
			reply = e.getLocalizedMessage();
			if (( null == reply ) || reply.equalsIgnoreCase( "null" ))
			{
				reply = e.getMessage();
				if (( null == reply ) || reply.equalsIgnoreCase( "null" ))
				{
					reply = e.toString();
					if (null == reply)
					{
						reply = "<message not available>";
					}
				}
			}
		}
		return reply;
	}// end exToString
	
	
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
	 * This method will return true if the given name is the name of a system user account.
	 * Currently there are 5 system user accounts: "admin", "guest", "_postingAgent", "_jobProcessingAgent", "_synchronizationAgent", and "_fileSyncAgent.
	 */
	public static boolean isSystemUserAccount( String name )
	{
		if ( name == null)
			return false;
		
		if ( name.equalsIgnoreCase( "admin" ) || name.equalsIgnoreCase( "guest" ) ||
			  name.equalsIgnoreCase( "_postingAgent" ) || name.equalsIgnoreCase( "_jobProcessingAgent" ) ||
			  name.equalsIgnoreCase("_synchronizationAgent") ||
			  name.equalsIgnoreCase("_fileSyncAgent"))
		{
			return true;
		}
		
		// If we get here the name is not a system user account.
		return false;
	}// end isSystemUserAccount()

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
	}//end splitUserIds

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
	}// end defaultTitleToFilename()

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
		return ObjectKeys.STATIC_DIR + "/" + SPropsUtil.getString(ObjectKeys.STATIC_DIR_PROPERTY, "xxx") + "/";
	}
	
	/**
	 * Returns the full path to static files
	 * 
	 * @return
	 */
	public static String getFullStaticPath(HttpServletRequest request) {
		String contextPath = request.getContextPath();
		if (contextPath.endsWith("/")) contextPath = contextPath.substring(0,contextPath.length()-1);
		return request.getContextPath() + "/" + ObjectKeys.STATIC_DIR + "/" + 
				SPropsUtil.getString(ObjectKeys.STATIC_DIR_PROPERTY, "xxx") + "/";
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
	public static boolean isEmailAddressValid( String usedAs, String ema )
	{
		return( null != validateEmailAddress( usedAs, ema ));
	}// end isEmailAddressValid()
	
	/**
	 * Performs a collated compare on two strings without generating any
	 * exceptions.
	 * 
	 * @param s1
	 * @param s2
	 * 
	 * @return
	 */
	public static int safeSColatedCompare(String s1, String s2) {
		Collator collator = Collator.getInstance();
		collator.setStrength(Collator.IDENTICAL);
		return
			collator.compare(
				((null == s1) ? "" : s1),
				((null == s2) ? "" : s2) );
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
	public static ZoneInfo getCurrentZone()
	{
		ZoneModule zm = ((ZoneModule) SpringContextUtil.getBean( "zoneModule" ));
		return zm.getZoneInfo( RequestContextHolder.getRequestContext().getZoneId() );
	}//end getCurrentZone()
	
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
	public static String getFromOverride()
	{
		// Can we access the current zone name?
		ZoneInfo zoneInfo = getCurrentZone();
		String reply = zoneInfo.getZoneName();
		if ( hasString( reply ) )
		{
			// Yes!  Check for a zone specific setting.
			reply = SPropsUtil.getString( (SPropsUtil.FROM_EMAIL_GLOBAL_OVERRIDE + "." + reply), "" ); 
		}

		// Do we have a zone specific setting?
		if ( ! ( hasString( reply ) ) )
		{
			// No!  Check for a global setting.
			reply = SPropsUtil.getString( SPropsUtil.FROM_EMAIL_GLOBAL_OVERRIDE, "" );
		}

		// Do we have a global setting?
		if ( ! ( hasString( reply ) ) )
		{
			// No!  Then ensure we return null.
			reply = null;
		}

		// If we get here, reply refers to the from override or is
		// null if there wasn't one.  Return it.
		return reply;
	}// end getFromOverride()
	
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
	public static Collection<InternetAddress> validateInternetAddressCollection( String usedAs, Collection<InternetAddress> addrs )
	{
		// Do we have any addresses to validate?
		Collection<InternetAddress> reply = new ArrayList<InternetAddress>();
		if ( null != addrs )
		{
			// Yes!  Scan them.
			for (Iterator<InternetAddress> iaIT = addrs.iterator(); iaIT.hasNext(); )
			{
				// Was this InternetAddress valid?
				InternetAddress ia = validateIA( usedAs, iaIT.next() );
				if ( null != ia )
				{
					// Yes!  Add it to the validated collection.
					reply.add( ia );
				}
			}
		}

		// If we get here, reply refers to a
		// Collection<InternetAddress> of the valid InternetAddress's
		// from the Collection<InternetAddress> passed in.  Return it.
		return reply;
	}//end validateInternetAddressCollection()
	
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
	public static Collection<String> validateEmailAddressCollection( String usedAs, Collection<String> addrs )
	{
		// Do we have any email addresses to validate?
		Collection<String> reply = new ArrayList<String>();
		if ( null != addrs )
		{
			// Yes!  Scan them.
			for ( Iterator<String> itEA = addrs.iterator(); itEA.hasNext(); )
			{
				// Is this email address valid?
				String ea = itEA.next();
				InternetAddress ia = new InternetAddress();
				try {ia.setAddress( ea );} catch (Exception e) {};
				if ( null != validateIA( usedAs, ia ) )
				{
					// Yes!  Add it to the validated collection.
					reply.add( ea );
				}
			}
		}
		
		// If we get here, reply refers to a
		// Collection<String> of the valid email addresses from the
		// Collection<String> passed in.  Return it.
		return reply;
	}// end validateEmailAddressCollection()

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
	public static InternetAddress validateEmailAddress( String usedAs, String ema )
	{
		// If we we don't have an email address to validate...
		ema = (( null == ema ) ? "" : ema.trim()); 
		if ( 0 == ema.length() )
		{
			// ...return null.
			return null;
		}

		// Otherwise, construct an InternetAddress from it...
		InternetAddress ia = new InternetAddress();
		try { ia.setAddress( ema ); } catch ( Exception e ) {};
		
		// ...and validate that.
		return validateIA( usedAs, ia );
	}// end validateEmailAddress()
	
	/*
	 * Validates an InternetAddress as containing a valid email
	 * address.
	 * 
	 * If it does, it is returned.  If it doesn't, an error is logged
	 * and null is returned.
	 */
	private static InternetAddress validateIA( String usedAs, InternetAddress ia )
	{
		try
		{
			// Is this InternetAddress valid?
			ia.validate();
		}
		catch ( Exception ex )
		{
			// No!  Log the error...
			StringBuffer error = new StringBuffer( usedAs + ":  invalid InternetAddress dropped" );
			error.append( getIAPartString( "address",  ia.getAddress()  ) );
			error.append( getIAPartString( "personal", ia.getPersonal() ) );
			error.append( getIAPartString( "type",     ia.getType()     ) );
			error.append( getIAPartString( "toString", ia.toString()    ) );
			m_logger.error( error.toString() );
						
			// ...and forget about it.
			ia = null;

			// Was the exception we caught other than an
			// AddressException?
			if ( ! ( ex instanceof AddressException ) )
			{
				// Yes!  Then something deeper than just a bogus email
				// address is happening.  Lets log the exception too.
				m_logger.error( "MiscUtil.validateIA( EXCEPTION ):  ", ex );
			}
		}

		// If we get here, ia refers to the validated InternetAddress
		// or is null.  Return it.
		return ia;
	}// end validateIA()
	
	/*
	 * Returns a String for constructing an error message about the
	 * invalid email address.
	 */
	private static String getIAPartString( String name, String value )
	{
		return ( ", " + name + ":  '" + ( ( null == value ) ? "" : value ) + "'" );
	}// end getIAPartString()
	
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
			Long entryId  = Long.parseLong(entryIdS);
			FolderModule fm = ((FolderModule) SpringContextUtil.getBean("folderModule"));
			FolderEntry fe = fm.getEntry(binderId, entryId);
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

	/**
	 * Return the url that points to the appropriate help documentation.
	 */
	public static String getHelpUrl( String guideName, String pageId, String sectionId )
	{
		String url;
		String lang;
		String guideComponent = null;
		String product;
		
		// Get the base help url from ssf-ext.properties.
		url = SPropsUtil.getString( "help.hostName", "http://www.novell.com" );
		
		// Do we have a language code to put on the url?
		lang = getHelpLangCode();
		if ( lang != null && lang.length() > 0 )
		{
			// Yes
			url +=  "/" + lang;
		}
		
		url += "/documentation";
		
		product = "/vibe33";
		
		// Are we running Filr?
		if ( Utils.checkIfFilr() )
		{
			// Yes
			url += "/novell-filr1";
			product = "/filr1";
		}
		// Are we running Novell Teaming?
		else if ( ReleaseInfo.isLicenseRequiredEdition())
		{
			// Yes
			url += "/vibe33";
		}
		else
			url += "/kablinkvibe33";
		
		if ( guideName != null && guideName.length() > 0 )
		{
			if ( guideName.equalsIgnoreCase( USER_GUIDE ) )
			{
				// Get the url to the user guide.
				guideComponent = product + "_user/data/";
			}
			else if ( guideName.equalsIgnoreCase( ADV_USER_GUIDE ) )
			{
				// Get the url to the advanced user guide.
				guideComponent = product + "_useradv/data/";
			}
			else if ( guideName.equalsIgnoreCase( ADMIN_GUIDE ) )
			{
				// Get the url to the administration guide.
				guideComponent = product + "_admin/data/";
			}
			else
				guideComponent = null;
			
			// Did we recognize the name of the guide?
			if ( guideComponent != null )
			{
				// Yes, add the guide component to the url.
				url += guideComponent;
				
				// Do we have a specific page to go to in the documentation?
				if ( pageId != null )
				{
					// Yes, each page has its own html file.
					url += pageId + ".html";
					
					// Do we have a specific section within the page to go to?
					if ( sectionId != null )
					{
						// Yes
						url += "#" + sectionId;
					}
				}
				else
				{
					// No, take the user to the start of the guide.
					url += "bookinfo.html";
				}
			}
		}

		return url;
	}

	/**
	 * Return the language code that should be put on the help url.
	 */
	private static String getHelpLangCode()
	{
		String lang;
		String originalLang;
		int i;
		
		// Get the language the user is running in
		lang = NLT.get( "Teaming.Lang", "" );
		originalLang = lang;
		
		// Do we know the language? 
		if ( lang == null || lang.length() == 0 )
		{
			// No
			return null;
		}
		
		// Is the language English?
		if ( lang.indexOf( "en" ) == 0 )
		{
			// Yes, we don't need to put a language code on the url.
			return null;
		}

		// We only need the first two characters of the language to
		// localize the documentation URLs.
		if ( lang.length() > 2 )
		{
			lang = lang.substring( 0, 2 ); 
		}

		// Is the language Chinese?
		if ( lang.equalsIgnoreCase( "zh" ) )
		{
			// Yes, use the full language string of zh-tw or zh-cn
			lang = originalLang.toLowerCase();
			lang = lang.replace( '_', '-' );
		}

		// Look for the appropriate language code.
		for (i = 0; i < DOC_LANGS.length; ++i)
		{
			if ( DOC_LANGS[i].indexOf( lang ) == 0 )
			{
				break;
			}
		}		

		// Do we have a language code for this language?
		if ( i == DOC_LANGS.length )
		{
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
		return SPropsUtil.getBoolean("html.standards.mode", false);
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
		if ((s == null) || (oldSub == null) || (newSub == null)) {
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
	public static List<IdTriple> getIdTriplesFromMultipleEntryIds( String multipleEntityIds )
	{
		List<IdTriple> reply = new ArrayList<IdTriple>();
		if ( MiscUtil.hasString( multipleEntityIds ) )
		{
			String[] meIds = multipleEntityIds.split( "," );
			for ( String meId:  meIds )
			{
				String[] eId = meId.split( ":" );
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
	public static Collection<Long> validateCL(Collection<Long> lC)
	{
		return ( ( null == lC ) ? new HashSet<Long>() : lC );
	}// end validateCL()
	
	/**
	 * Validates that a Collection<String> is non-null.
	 * 
	 * @param lS
	 * 
	 * @return
	 */
	public static Collection<String> validateCS(Collection<String> sC)
	{
		return ( ( null == sC ) ? new HashSet<String>() : sC );
	}// end validateCS()
	
	/**
	 * Validates that a List<InternetAddress> is non-null.
	 * 
	 * @param iaList
	 * 
	 * @return
	 */
	public static List<InternetAddress> validateIAL( List<InternetAddress> iaList )
	{
		return ( ( null == iaList ) ? new ArrayList<InternetAddress>() : iaList );
	}// end validateIAL()
	
	/**
	 * Validates that a List<Locale> is non-null.
	 * 
	 * @param lList
	 * 
	 * @return
	 */
	public static List<Locale> validateLL( List<Locale> lList )
	{
		return ( ( null == lList ) ? new ArrayList<Locale>() : lList );
	}// end validateIAL()
	
	/*
	 * Converts a byte[] to a string of hex characters.
	 */
	private static String baToHS( final byte[] b )
	{
		final StringBuffer sb = new StringBuffer( b.length * 2 );
		final int baLen = b.length;
		for ( int i = 0; i < baLen; i += 1 )
		{
			int v = ( b[i] & 0xff );
			if ( v < 16 )
			{
				sb.append( '0' );
			}
			sb.append( Integer.toHexString( v ) );
		}
		return sb.toString();
	}// end baToHS()
	
	/**
	 * Returns the MD5 hash from a byte[].
	 * 
	 * @param dataBytes
	 * 
	 * @return
	 * 
	 * @throws Exception
	 */
	public static String getMD5Hash( byte[] dataBytes ) throws Exception
	{
		// Read the data through the MD5 digest...
	    InputStream		input     =  new ByteArrayInputStream( dataBytes );
	    byte[]			buffer    = new byte[1024];
	    MessageDigest	md5Digest = MessageDigest.getInstance( "MD5" );
	    int				read;
	    do {
	        read = input.read( buffer );
	        if ( read > 0 )
	        {
	            md5Digest.update( buffer, 0, read );
	        }
	    } while ( read != (-1) );
	    input.close();

	    // ...and return the hash as a string.
	    return baToHS( md5Digest.digest() );
	}// end getMD5Hash()
	
	public static String getMD5Hash( String data ) throws Exception
	{
		// Always use the initial form of the method.
		return getMD5Hash( data.getBytes() );
	}// end getMD5Hash()
}// end MiscUtil
