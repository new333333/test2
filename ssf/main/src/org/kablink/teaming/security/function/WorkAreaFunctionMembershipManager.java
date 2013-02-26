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
package org.kablink.teaming.security.function;

import java.util.List;
import java.util.Set;

/**
 *
 * @author Jong Kim
 */
public interface WorkAreaFunctionMembershipManager {
    
    public void addWorkAreaFunctionMembership(WorkAreaFunctionMembership functionMembership);
    public void copyWorkAreaFunctionMemberships(Long zoneId, WorkArea source, WorkArea destination);
    public void copyWorkAreaFunctionMemberships(Long zoneId, WorkArea source, WorkArea extSource, WorkArea destination,
    		boolean justThisScope, String scope);
    public void deleteWorkAreaFunctionMemberships(Long zoneId, WorkArea workArea);
    public void deleteWorkAreaFunctionMembership(WorkAreaFunctionMembership functionMembership);
    
    public void updateWorkAreaFunctionMembership(WorkAreaFunctionMembership functionMembership);
    
    /**
     * Returns <code>WorkAreaFunctionMembership</code> for
     * @param zoneId
     * @param workArea
     * @param functionId
     * @return
     */
    public WorkAreaFunctionMembership getWorkAreaFunctionMembership(Long zoneId, WorkArea workArea, Long functionId);
    
    /**
     * Returns a list of <code>WorkAreaFunctionMembership</code>
     * @param zoneId
     * @param workArea
     * @return
     */
    public List<WorkAreaFunctionMembership> findWorkAreaFunctionMemberships(Long zoneId, WorkArea workArea);
    public List<WorkAreaFunctionMembership> findWorkAreaFunctionMembershipsByOperation(Long zoneId, WorkArea workArea, WorkAreaOperation workAreaOperation);
    public List<WorkAreaFunctionMembership> findWorkAreaFunctionMembershipsByOperation(Long zoneId, WorkAreaOperation workAreaOperation, Set<Long> membersToLookup);
    public List<WorkAreaFunctionMembership> findWorkAreaFunctionMemberships(Long zoneId, WorkArea workArea, String functionScope);
    /**
     * 
     * @param zoneId
     * @param workArea
     * @param workAreaOperation
     * @param membersToLookup a set of <code>Long</code>
     * @return
     */
    public boolean checkWorkAreaFunctionMembership(Long zoneId, WorkArea workArea, 
            WorkAreaOperation workAreaOperation, Set membersToLookup);   
    
    public Function getFunction(Long zoneId, Long functionId);
}
