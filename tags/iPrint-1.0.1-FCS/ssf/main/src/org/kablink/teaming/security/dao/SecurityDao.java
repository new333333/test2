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
package org.kablink.teaming.security.dao;

import java.util.Date;
import java.util.List;
import java.util.Set;

import org.kablink.teaming.NoObjectByTheIdException;
import org.kablink.teaming.security.accesstoken.impl.TokenInfoApplication;
import org.kablink.teaming.security.accesstoken.impl.TokenInfoRequest;
import org.kablink.teaming.security.accesstoken.impl.TokenInfoSession;
import org.kablink.teaming.security.function.Condition;
import org.kablink.teaming.security.function.Function;
import org.kablink.teaming.security.function.WorkAreaFunctionMembership;


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
    	    	     
    public List<Function> findFunctions(Long zoneId);
    
    public Condition loadFunctionCondition(Long zoneId, Long functionConditionId) throws NoObjectByTheIdException;
    
    public List<Condition> findFunctionConditions(Long zoneId);

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

    public List<WorkAreaFunctionMembership> findWorkAreaFunctionMemberships(Long zoneId, Long functionId);
	public List<WorkAreaFunctionMembership> findWorkAreaFunctionMemberships(Long zoneId, Long workAreaId, String workAreaType);

    /**
     * 
     * @param zoneId
     * @param workAreaId
     * @param workAreaType
     * @param workAreaOperationName
     * @param membersToLookup	a set of Long
     * @return
     */
    public List<Long> checkWorkAreaFunctionMembership(Long zoneId, Long workAreaId, 
            String workAreaType, String workAreaOperationName, Set<Long> membersToLookup);
    public List<WorkAreaFunctionMembership> findWorkAreaFunctionMembershipsByOperation(Long zoneId,
            Long workAreaId, String workAreaType, 
            String workAreaOperationName);
    public List<WorkAreaFunctionMembership> findWorkAreaByOperation( Long zoneId,
             String workAreaOperationName,  Set<Long> membersToLookup);

    public TokenInfoSession loadTokenInfoSession(Long zoneId, String infoId);

    public void deleteUserTokenInfoSession(Long userId);
    
    public TokenInfoRequest loadTokenInfoRequest(Long zoneId, String infoId);

    public TokenInfoApplication loadTokenInfoApplication(Long zoneId, String infoId);

    public void deleteAll(Class clazz);
 
    public void deleteTokenInfoOlderThan(Date thisDate);
}
