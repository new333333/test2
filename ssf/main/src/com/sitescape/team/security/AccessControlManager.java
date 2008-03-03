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
package com.sitescape.team.security;

import java.util.Set;

import com.sitescape.team.domain.User;
import com.sitescape.team.security.acl.AccessType;
import com.sitescape.team.security.acl.AclAccessControlException;
import com.sitescape.team.security.acl.AclContainer;
import com.sitescape.team.security.acl.AclControlled;
import com.sitescape.team.security.function.WorkArea;
import com.sitescape.team.security.function.WorkAreaOperation;

/**
 *
 * @author Jong Kim
 */
public interface AccessControlManager {

	/**
	 * Return a set of principalIds that have the 
     * privilege to run the operation against the work area. 
	 * @param workArea
	 * @param workAreaOperation
	 * @return
	 */
	public Set getWorkAreaAccessControl(WorkArea workArea,
			WorkAreaOperation workAreaOperation); 
	
    /**
     * Same as {@link #checkOperation(WorkArea, WorkAreaOperation)} except 
     * that this returns <code>boolean</code> flag rather than throwing an 
     * exception.
     * 
     * @param workArea
     * @param workAreaOperation
     * @return
     */
    public boolean testOperation(WorkArea workArea,
            WorkAreaOperation workAreaOperation);
        
    /**
     * Same as {@link #checkOperation(User, WorkArea, WorkAreaOperation)} except
     * that this returns <code>boolean</code> flag rather than throwing an 
     * exception. 
     * 
     * @param user
     * @param workArea
     * @param workAreaOperation
     * @return
     */
    public boolean testOperation(User user, WorkArea workArea,
            WorkAreaOperation workAreaOperation);

    /**
     * Check if the user associated with the current request context has the 
     * privilege to run the operation against the work area. 
     * 
     * @param workArea
     * @param workAreaOperation
     * @throws AccessControlException
     */
    public void checkOperation(WorkArea workArea,
            WorkAreaOperation workAreaOperation) throws AccessControlException;
        
    /**
     * Check if the specified user has the privilege to run the operation
     * against the work area. 
     * <p>
     * Use this method if one of the following conditions is true.
     * <p>
     * 1) There is no <code>RequestContext</code> set up for the calling thread.<br>
     * 2) The user associated with the current request context is not the same
     * as the user against which this access check is being performed. 
     * 
     * @param user
     * @param workArea
     * @param workAreaOperation
     * @throws AccessControlException
     */
    public void checkOperation(User user, WorkArea workArea,
            WorkAreaOperation workAreaOperation) throws AccessControlException; 
}
