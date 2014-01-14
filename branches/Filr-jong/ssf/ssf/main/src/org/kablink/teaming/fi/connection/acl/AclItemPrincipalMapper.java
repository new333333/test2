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
package org.kablink.teaming.fi.connection.acl;

import java.util.List;
import java.util.Map;

import org.kablink.teaming.domain.User;

/**
 * A concrete resource driver supporting ACL must implement this interface so that
 * Vibe can figure out which Vibe user or group each of the principal maps to.
 *
 * @author jong
 *
 */
public interface AclItemPrincipalMapper {

	/**
	 * Returns the ID of the Vibe principal object (which could be either user or
	 * group) that this particular principal ID maps to.
	 * <p>
	 * A concrete implementation of this method must be thread-safe, that is, safe for
	 * use by multiple concurrent threads on the same object instance.
	 * 
	 * @param aclItemPrincipalIdType ACL item principal ID type
	 * @param aclItemPrincipalId ACL item principal ID
	 * @return Vibe principal ID
	 * @throws AclItemPrincipalMappingException
	 * 
	 */
	public Long toVibePrincipalId(String aclItemPrincipalIdType, String aclItemPrincipalId) throws AclItemPrincipalMappingException;
	
	/**
	 * Returns the ID of the Vibe user object that this particular principal ID maps to.
	 * If the principal ID refers to a non-user object such as group, then it throws
	 * <code>AclItemPrincipalMappingException</code>.
	 * 
	 * @param aclItemPrincipalIdType
	 * @param aclItemPrincipalId
	 * @return
	 * @throws AclItemPrincipalMappingException
	 */
	public Long toVibeUserId(String aclItemPrincipalIdType, String aclItemPrincipalId) throws AclItemPrincipalMappingException;
	
	/**
	 * Returns the file system principal ID corresponding to the specified Vibe user object. 
	 * 
	 * @param vibeUser Vibe user object
	 * @return file system principal ID
	 * @throws AclItemPrincipalMappingException
	 */
	public Map<String,String> toFileSystemPrincipalId(User vibeUser) throws AclItemPrincipalMappingException;
	
	/**
	 * Returns the file system IDs of the groups that the specified Vibe user is a member of.
	 * 
	 * @param vibeUser
	 * @return
	 * @throws AclItemPrincipalMappingException
	 */
	public Map<String, List<String>> toFileSystemGroupIds(User vibeUser) throws AclItemPrincipalMappingException;
}
