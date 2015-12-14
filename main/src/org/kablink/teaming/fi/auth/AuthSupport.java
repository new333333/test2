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
package org.kablink.teaming.fi.auth;

import org.kablink.teaming.UncheckedIOException;
import org.kablink.teaming.fi.FIException;
import org.kablink.teaming.fi.connection.acl.AclResourceSession;

/**
 * @author jong
 *
 */
public interface AuthSupport {

	/**
	 * Returns authentication information resulted from the authentication handshake 
	 * identified by the given uuid.
	 * <p>
	 * It will throw an exception if the operation times out or encounters an error.
	 * 
	 * @param uuid unique temporary identifier that identifies the user and the corresponding authentication handshake
	 * @return authentication information
	 * @throws FIException
	 * @throws UncheckedIOException
	 */
	public AuthInfo retrieveAuthInfo(String uuid) throws FIException, UncheckedIOException;
	
	/**
	 * Validates the authentication information if specified, and take appropriate action based on the state.
	 * Specifically, it will do the following:
	 * <p>
	 * 1. If the specified <code>authInfo</code> is <code>null</code>, it will throw <code>AuthException</code>
	 * with appropriate information needed to begin a new authentication handshake.
	 * <br>
	 * 2. If the specified <code>authInfo</code> is valid and current, it will create a new <code>AuthInfo</code>
	 * object, copy the content from the input, and return it.
	 * <br>
	 * 3. If the specified <code>authInfo</code> has expired and it is possible to refresh the token without
	 * starting a new authentication handshake, it will do that and return a new <code>AuthInfo</code> object 
	 * containing updated information.
	 * <br>
	 * 4. If the specified <code>authInfo</code> is invalid and it is not possible to refresh the token, it will throw
	 * <code>AuthException</code> with appropriate information needed to begin a new authentication handshake.
	 * 
	 * @param authInfo
	 * @return
	 * @throws AuthException
	 * @throws UncheckedIOException
	 */
	//public AuthInfo validateAuthInfo(AuthInfo authInfo) throws AuthException, UncheckedIOException;
	
	/**
	 * Opens a session using the auth info. Optionally it performs management task on the auth info.
	 * Here's the details of how this method behaves:
	 * <p>
	 * 1. If the specified <code>authInfo</code> is <code>null</code>, it will throw <code>AuthException</code>
	 * with appropriate information needed to begin a new authentication handshake.
	 * <br>
	 * 2. If the specified <code>authInfo</code> is valid and current, it will return a new session.
	 * <br>
	 * 3. If the specified <code>authInfo</code> has expired and it is possible to refresh the token without
	 * starting a new authentication handshake, it will refresh the token and update the <code>authInfo</code>
	 * object with the new information, and return a new session. 
	 * <br>
	 * 4. If the specified <code>authInfo</code> is invalid and it is not possible to refresh the token, it will
	 * throw <code>AuthException</code> with appropriate information needed to begin a new authentication handshake.
	 * 
	 * @param authInfo
	 * @return
	 * @throws AuthException
	 * @throws FIException
	 * @throws UncheckedIOException
	 */
	public AclResourceSession openSessionWithAuth(AuthInfo authInfo) throws AuthException, FIException, UncheckedIOException;

}
