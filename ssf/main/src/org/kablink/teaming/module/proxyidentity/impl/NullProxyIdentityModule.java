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
package org.kablink.teaming.module.proxyidentity.impl;

import java.util.List;
import java.util.Map;

import org.kablink.teaming.domain.ProxyIdentity;
import org.kablink.teaming.module.impl.CommonDependencyInjection;
import org.kablink.teaming.module.proxyidentity.ProxyIdentityModule;

/**
 * Stub module for managing proxy identities.
 * 
 * @author drfoster@novell.com
 */
@SuppressWarnings("unchecked")
public class NullProxyIdentityModule extends CommonDependencyInjection implements ProxyIdentityModule {
    /**
     * Creates a new ProxyIdentity.
     * 
     * @param proxyIdentity
     */
	@Override
    public void addProxyIdentity(ProxyIdentity proxyIdentity) {
		// Nothing to do.
	}
    
    /**
     * Deletes an existing ProxyIdentity.
     * 
     * @param id
     */
	@Override
    public void deleteProxyIdentity(Long id) {
		// Nothing to do.
	}
	
	@Override
    public void deleteProxyIdentity(ProxyIdentity proxyIdentity) {
    	// Nothing to do.
    }
    
    /**
     * Returns a specific ProxyIdentity, if its defined.
     * 
     * @param id
     * 
     * @return
     */
	@Override
    public ProxyIdentity getProxyIdentity(Long id) {
    	return null;
    }
    
    /**
     * Returns a list of ProxyIdentity's.
     * 
     * @return
     */
	@Override
    public List<ProxyIdentity> getProxyIdentityList() {
		return null;
	}
    
    /**
     * Returns a list of ProxyIdentity's.
     * 
	 * Returns a Map containing:
	 * 		Key:  ObjectKeys.SEARCH_ENTRIES:      List<ProxyIdentity> of the ProxyIdentity's.
	 *		Key:  ObjectKeys.SEARCH_COUNT_TOTAL:  Long of the total entries available that satisfy the selection specifications.
	 * 
     * @param options
     * 
     * @return
     */
	@Override
	public Map getProxyIdentities(Map options) {
		return null;
	}
    
    /**
     * Returns a List<ProxyIdentity>, of the ProxyIdentities that match
     * the given title.
     * 
     * @param title
     * 
     * @return
     */
	@Override
    public List<ProxyIdentity> getProxyIdentitiesByTitle(String title) {
    	return null;
    }
    
    /**
     * Modifies a ProxyIdentity.
     * 
     * @param proxyIdentity
     */
	@Override
    public void modifyProxyIdentity(ProxyIdentity proxyIdentity) {
    	// Nothing to do.
    }
}
