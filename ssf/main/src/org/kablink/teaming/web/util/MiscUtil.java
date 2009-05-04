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


import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.portlet.RenderRequest;

import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.dao.ProfileDao;
import org.kablink.teaming.domain.AuthenticationConfig;
import org.kablink.teaming.domain.Definition;
import org.kablink.teaming.domain.ProfileBinder;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.module.profile.ProfileModule;
import org.kablink.teaming.module.profile.ProfileModule.ProfileOperation;
import org.kablink.teaming.module.profile.impl.ProfileModuleImpl;
import org.kablink.teaming.portletadapter.AdaptedPortletURL;
import org.kablink.teaming.util.AllModulesInjected;
import org.kablink.teaming.util.ReleaseInfo;
import org.kablink.teaming.web.WebKeys;


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
	
}// end MiscUtil
