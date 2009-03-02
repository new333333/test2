/**
 * The contents of this file are subject to the Common Public Attribution License Version 1.0 (the "CPAL");
 * you may not use this file except in compliance with the CPAL. You may obtain a copy of the CPAL at
 * http://www.opensource.org/licenses/cpal_1.0. The CPAL is based on the Mozilla Public License Version 1.1
 * but Sections 14 and 15 have been added to cover use of software over a computer network and provide for
 * limited attribution for the Original Developer. In addition, Exhibit A has been modified to be
 * consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT WARRANTY OF ANY KIND,
 * either express or implied. See the CPAL for the specific language governing rights and limitations
 * under the CPAL.
 * 
 * The Original Code is ICEcore. The Original Developer is SiteScape, Inc. All portions of the code
 * written by SiteScape, Inc. are Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * 
 * 
 * Attribution Information
 * Attribution Copyright Notice: Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by ICEcore]
 * Attribution URL: [www.icecore.com]
 * Graphic Image as provided in the Covered Code [web/docroot/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are defined in the CPAL as a
 * work which combines Covered Code or portions thereof with code not governed by the terms of the CPAL.
 * 
 * 
 * SITESCAPE and the SiteScape logo are registered trademarks and ICEcore and the ICEcore logos
 * are trademarks of SiteScape, Inc.
 */
package org.kablink.teaming.web.util;


import java.util.List;
import java.util.Map;

import javax.portlet.RenderRequest;

import org.kablink.teaming.domain.Definition;
import org.kablink.teaming.domain.ProfileBinder;
import org.kablink.teaming.module.profile.ProfileModule.ProfileOperation;
import org.kablink.teaming.portletadapter.AdaptedPortletURL;
import org.kablink.teaming.util.AllModulesInjected;
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
		List			defaultEntryDefinitions;
		ProfileBinder	profileBinder;

		profileBinder = bs.getProfileModule().getProfileBinder();

		// Build the url to invoke the "Add User" page.
		defaultEntryDefinitions = profileBinder.getEntryDefinitions();
		if ( !defaultEntryDefinitions.isEmpty() )
		{
			Definition			def;
			AdaptedPortletURL	adapterUrl;

			// There is only 1 entry definition for a Profile binder.  Get it.
			def = (Definition) defaultEntryDefinitions.get( 0 );
			
			// Create the url needed to invoke the "Add User" page.
			adapterUrl = new AdaptedPortletURL( request, "ss_forum", true );
			adapterUrl.setParameter( WebKeys.ACTION, WebKeys.ACTION_ADD_PROFILE_ENTRY );
			adapterUrl.setParameter( WebKeys.URL_BINDER_ID, profileBinder.getId().toString() );
			adapterUrl.setParameter( WebKeys.URL_ENTRY_TYPE, def.getId() );
			model.put( WebKeys.ADD_USER_URL, adapterUrl.toString() );

			// Add the flag that indicates the "Create new account" ui should be available.
			model.put( WebKeys.ADD_USER_ALLOWED, "true" );
		}
	}// end addCreateNewAccountDataToResponse()

	/**
	 * This class determines if the logged in user has rights to add a user.
	 */
	public static boolean canAddUser( AllModulesInjected bs )
	{
		boolean	canAdd	= false;
		
    	try
    	{
    		ProfileBinder	profileBinder;

    		profileBinder = bs.getProfileModule().getProfileBinder();
			if ( bs.getProfileModule().testAccess( profileBinder, ProfileOperation.addEntry ) )
				canAdd = true;
    	}
    	catch (Exception e)
    	{
    		// Nothing to do.  It just means that the Guest user doesn't not have rights to the Profile binder.
    	}
		
    	return canAdd;
	}// end canAddUser()
}// end MiscUtil
