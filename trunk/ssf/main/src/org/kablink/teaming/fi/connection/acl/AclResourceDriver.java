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

import org.kablink.teaming.UncheckedIOException;
import org.kablink.teaming.fi.FIException;
import org.kablink.teaming.fi.connection.ResourceDriver;
import org.kablink.teaming.security.function.WorkAreaOperation;

/**
 * A resource driver interface that supports access control list (ACL).
 * 
 * @author jong
 *
 */
public interface AclResourceDriver extends ResourceDriver {

	/**
	 * Returns an implementation of <code>AclItemPermissionMapper</code> interface.
	 * <p>
	 * For efficiency reason, resource driver implementation should re-use the same
	 * mapper instance across all invocations of this method.
	 * 
	 * @return
	 */
	public AclItemPermissionMapper getAclItemPermissionMapper();
	
	/**
	 * Returns an implementation of <code>AclItemPrincipalMapper</code> interface.
	 * <p>
	 * For efficiency reason, resource driver implementation should re-use the same
	 * mapper instance across all invocations of this method.
	 * 
	 * @return
	 */
	public AclItemPrincipalMapper getAclItemPrincipalMapper();

	/**
	 * Returns an array of <code>WorkAreaOperation</code> objects indicating which Vibe
	 * rights should be controlled by the external ACLs.
	 * <p>
	 * The caller must not modify the array returned from this method.
	 * 
	 * @return an array of <code>WorkAreaOperation</code> objects
	 */
	public WorkAreaOperation[] getExternallyControlledlRights();

	/**
	 * Opens a session in proxy mode.
	 * 
	 * @param proxyUsername username of the proxy account
	 * @param proxyPassword password of the proxy account
	 * @return ACL resource session
	 * @throws FIException
	 * @throws UncheckedIOException
	 */
	public AclResourceSession openSessionProxyMode(String proxyUsername, String proxyPassword) throws FIException, UncheckedIOException;

	/**
	 * Opens a session in user mode.
	 * 
	 * @param aclItemPrincipalId ID of the storage system principal
	 * @param aclItemPrincipalPassword password of the storage system principal
	 * @return ACL resource session
	 * @throws FIException
	 * @throws UncheckedIOException
	 */
	public AclResourceSession openSessionUserMode(String aclItemPrincipalId, String aclItemPrincipalPassword) throws FIException, UncheckedIOException;

}
