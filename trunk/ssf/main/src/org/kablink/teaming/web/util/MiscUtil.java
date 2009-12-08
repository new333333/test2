/**
 * Copyright (c) 1998-2009 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2009 Novell, Inc. All Rights Reserved.
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


import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.portlet.RenderRequest;

import org.kablink.teaming.domain.AuthenticationConfig;
import org.kablink.teaming.domain.Definition;
import org.kablink.teaming.module.profile.ProfileModule;
import org.kablink.teaming.portletadapter.AdaptedPortletURL;
import org.kablink.teaming.util.AllModulesInjected;
import org.kablink.teaming.util.ReleaseInfo;
import org.kablink.teaming.web.WebKeys;
import org.springframework.web.multipart.MultipartFile;


/**
 * @author jwootton
 *
 * This class contains a collection of miscellaneous utility methods.
 * 
 */
public final class MiscUtil
{
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
	 * This method will return true if the given name is the name of a system user account.
	 * Currently there are 5 system user accounts: "admin", "guest", "_postingAgent", "_jobProcessingAgent" and "_synchronizationAgent".
	 */
	public static boolean isSystemUserAccount( String name )
	{
		if ( name == null)
			return false;
		
		if ( name.equalsIgnoreCase( "admin" ) || name.equalsIgnoreCase( "guest" ) ||
			  name.equalsIgnoreCase( "_postingAgent" ) || name.equalsIgnoreCase( "_jobProcessingAgent" ) ||
			  name.equalsIgnoreCase("_synchronizationAgent"))
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
				uidsBuf.append(uids[i]);
				
				// ...and to the ArrayList.
				userIds.add(Long.valueOf(uids[i]));
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
}// end MiscUtil
