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
package com.sitescape.team.security.dao;

import java.util.List;
import java.util.Set;

import com.sitescape.team.NoObjectByTheIdException;
import com.sitescape.team.security.accesstoken.impl.TokenInfoRequest;
import com.sitescape.team.security.accesstoken.impl.TokenInfoSession;
import com.sitescape.team.security.function.Function;
import com.sitescape.team.security.function.WorkAreaFunctionMembership;

/**
 *
 * @author Jong Kim
 */
public interface SecurityDao {
    
    public void save(Object obj);
    
    public void update(Object obj);
    
    public void delete(Object obj);
    public void deleteWorkAreaFunctionMemberships(Long zoneId, Long workAreaId, String workAreaType);
    
    public Function loadFunction(Long zoneId, Long id)  throws NoObjectByTheIdException;
    	    	     
    public List findFunctions(Long zoneId);

    /**
     * Returns specified <code>WorkAreaFunctionMembership</code>.
     * Returns <code>null</code> if not exists.
     * 
     * @param zoneId
     * @param workAreaId
     * @param workAreaType
     * @param functionId
     * @return
     */
    public WorkAreaFunctionMembership getWorkAreaFunctionMembership(Long zoneId, 
    		Long workAreaId, String workAreaType, Long functionId);

    public List findWorkAreaFunctionMemberships(Long zoneId, Long functionId);
	public List findWorkAreaFunctionMemberships(Long zoneId, Long functionId, String workAreaType);

    /**
     * 
     * @param zoneId
     * @param workAreaId
     * @param workAreaType
     * @param workAreaOperationName
     * @param membersToLookup	a set of Long
     * @return
     */
    public boolean checkWorkAreaFunctionMembership(Long zoneId, Long workAreaId, 
            String workAreaType, String workAreaOperationName, Set membersToLookup);
    public List findWorkAreaFunctionMembershipsByOperation(Long zoneId,
            Long workAreaId, String workAreaType, 
            String workAreaOperationName);
    public List findWorkAreaByOperation( Long zoneId,
             String workAreaOperationName,  Set membersToLookup);

    public TokenInfoSession loadTokenInfoSession(Long zoneId, String infoId);

    public void deleteUserTokenInfoSession(Long userId);
    
    public TokenInfoRequest loadTokenInfoRequest(Long zoneId, String infoId);

    public void deleteAll(Class clazz);
    
}
