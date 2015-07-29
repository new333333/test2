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

	public enum ConnectionTestStatusCode {
		/**
		 * Indicates a success
		 */
		NORMAL,
		/**
		 * Invalid proxy credentials
		 */
		PROXY_CREDENTIALS_ERROR,
		/**
		 * Network connection failure
		 */
		NETWORK_ERROR
	}
	
	public class ConnectionTestStatus {
		// connection test status code
		private ConnectionTestStatusCode code;
		// (optional) detailed error message 
		private String message;
		// (optional) exception object raised from test connection
		private Exception exc;
		public ConnectionTestStatus(ConnectionTestStatusCode code, String message, Exception exc) {
			this.code = code;
			this.message = message;
			this.exc = exc;
		}
		public ConnectionTestStatusCode getCode() {
			return code;
		}
		public String getMessage() {
			return message;
		}
		public Exception getException() {
			return exc;
		}
		public String toString() {
			return "[" + code.name() + "]" + ((message != null) ? message : "");
		}
	}
	
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
	 * Returns an immutable list of <code>WorkAreaOperation</code> objects indicating which
	 * Vibe/Filr rights should be controlled by the external ACLs.
	 * <p>
	 * The caller must not and can not modify the array returned from this method.
	 * 
	 * @return an array of <code>WorkAreaOperation</code> objects
	 */
	public List<WorkAreaOperation> getExternallyControlledlRights();
	
	/**
	 * Returns the name of the role type (aka scope) registered with the Vibe system.
	 * Role type is used to identify the set of Vibe roles that the permissions from
	 * the external system can map to via <code>AclItemPermissionMapper</code> mapper.
	 * Therefore it is required and expected that <code>AclItemPermissionMapper</code>
	 * mapper maps external system permissions only to those Vibe roles that belong
	 * in the role type returned from this method. If this condition/requirement is
	 * not met, the system behavior is unpredictable.
	 * <p>
	 * In the current version of Vibe, this part of the integration is not entirely
	 * pluggable in the sense that adding and registering a new role type requires 
	 * changes to the Vibe software. However, it's possible for new driver implementation
	 * to re-use an existing role type defined by another driver implementation
	 * if the access control requirement happens to be identical. In such case,
	 * no change is necessary to Vibe software.
	 * 
	 * @return
	 */
	public String getRegisteredRoleTypeName();

	/**
	 * Opens a session in proxy mode.
	 * 
	 * @param proxyUsername username of the proxy account
	 * @param proxyPassword password of the proxy account
	 * @param properties A map of optional properties or <code>null</code>. The actual semantics of the properties
	 * are specific to each driver implementation.
	 * @return ACL resource session
	 * @throws FIException
	 * @throws UncheckedIOException
	 */
	public AclResourceSession openSessionProxyMode(String proxyUsername, String proxyPassword, Map<String,Object> properties) throws FIException, UncheckedIOException;

	/**
	 * Opens a session in user mode.
	 * 
	 * @param userId
	 * @param password
	 * @return
	 * @throws FIException
	 * @throws UncheckedIOException
	 */
	public AclResourceSession openSessionUserMode(Map<String,String> userId, String password) throws FIException, UncheckedIOException;
	
	/**
	 * Tests a connection.
	 * 
	 * @param proxyUsername Username of the proxy account
	 * @param proxyPassword Password of the proxy account
	 * @param subPath (optional) Sub-element path under the root (e.g. "a\b"), or <code>null</code> if the root is to be tested
	 * @return
	 */
	public ConnectionTestStatus testConnection(String proxyUsername, String proxyPassword, String subPath);
	
}
