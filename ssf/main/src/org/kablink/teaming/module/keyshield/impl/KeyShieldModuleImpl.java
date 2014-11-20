/**
 * Copyright (c) 1998-2014 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2014 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2014 Novell, Inc. All Rights Reserved.
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
package org.kablink.teaming.module.keyshield.impl;

import org.kablink.teaming.domain.KeyShieldConfig;
import org.kablink.teaming.module.impl.CommonDependencyInjection;
import org.kablink.teaming.module.keyshield.KeyShieldModule;
import org.kablink.teaming.module.zone.ZoneException;

/**
 * 
 * @author jwootton@novell.com
 */
public class KeyShieldModuleImpl extends CommonDependencyInjection
	implements KeyShieldModule
{
	/**
	 * Delete the KeyShieldConfig associated with the given zone id
	 */
	@Override
	public void deleteKeyShieldConfig( Long zoneId )
	{
		KeyShieldConfig config;
		
		if ( zoneId == null )
			return;
		
		config = getCoreDao().loadKeyShieldConfig( zoneId );
		if ( config != null )
		{
			try
			{
				getCoreDao().delete( config );
			}
			catch ( Exception ex )
			{
				logger.warn( "Error deleting KeyShieldConfig for zone: " + zoneId, ex );
			}
		}
	}
	
 	/**
 	 * 
 	 * @param zoneId
 	 * @return
 	 * @throws ZoneException
 	 */
	@Override
	public KeyShieldConfig getKeyShieldConfig( Long zoneId ) throws ZoneException
	{
		KeyShieldConfig config;
		
		config = getCoreDao().loadKeyShieldConfig( zoneId );

		if ( config == null )
			config = new KeyShieldConfig( zoneId );

		return config;
	}
	
	/**
	 * Update the db with the given KeyShieldConfig data
	 */
	@Override
	public void saveKeyShieldConfig( Long zoneId, KeyShieldConfig config )
	{
		KeyShieldConfig currentConfig;
		
		// Create a KeyShieldConfig object if one doesn't already exist in the db for the given zone.
		currentConfig = getCoreDao().loadKeyShieldConfig( zoneId );
		if ( currentConfig == null )
			currentConfig = new KeyShieldConfig( zoneId );
		
		// Make a copy of the config data that was passed to us.
		currentConfig.copy( config );
		
		getCoreDao().save( currentConfig );
	}
}
