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

package org.kablink.teaming.module.authentication;

/**
 * Indicates authentication service provider against which authentication takes place.
 * It is extremely important to note that in Vibe authentication service provider isn't 
 * necessarily the same as identity source. For example, Vibe caches LDAP user profile 
 * information in Vibe's database, and when LDAP user attempts to log in, if the LDAP 
 * server is unreachable for whatever reason, Vibe allows the user to authenticate against 
 * the user credential information cached in Vibe database. In this case, the user's 
 * identity source is still LDAP because that's the identity information is originated
 * from. However, the authentication service provider is local, even if it's temporary.
 * Also, for all external users, Vibe uses a single identity source type called "external"
 * to refer to all of the identity sources, whereas for authentication service provider it 
 * refer to individual authentication mechanisms that are actually used.
 * 
 * @author jong
 *
 */
public enum AuthenticationServiceProvider {

	UNKNOWN,
	LOCAL,
	LDAP,
	OPENID,
	PRE;

}
