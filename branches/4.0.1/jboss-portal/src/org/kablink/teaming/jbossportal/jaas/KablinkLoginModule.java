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
package org.kablink.teaming.jbossportal.jaas;

import java.security.acl.Group;
import java.util.Map;


import org.jboss.security.SimpleGroup;
import org.jboss.security.auth.spi.UsernamePasswordLoginModule;
import org.kablink.teaming.asmodule.bridge.BridgeClient;

import javax.security.auth.Subject;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.login.LoginException;

/**
 * A SiteScape-supplied login module used to synchronize authenticated user
 * into SiteScape user database.
 */
public class KablinkLoginModule extends UsernamePasswordLoginModule {
	
	private static final org.jboss.logging.Logger log = 
		org.jboss.logging.Logger.getLogger(KablinkLoginModule.class);

	private static final String SERVICE_CLASS_NAME = "org.kablink.teaming.bridge.AuthenticationBridge";
	
	private static final String SERVICE_METHOD_NAME = "authenticateEasy";
	
	private static final Class[] SERVICE_METHOD_ARG_TYPES = 
		new Class[] {String.class, String.class, String.class, Map.class, String.class};
	
	private String additionalRole;
	private String synchronizeIdentity;
	
	public void initialize(Subject subject, CallbackHandler callbackHandler, Map sharedState, Map options) {
	      super.initialize(subject, callbackHandler, sharedState, options);
	      
	      additionalRole = (String)options.get("additionalRole");
	      synchronizeIdentity = (String)options.get("synchronizeIdentity");
	}

	public boolean commit() throws LoginException {
		if(isSynchronizeIdentity()) {
			String username = getUsername();
			String password = getPassword();
						
			try {
				// Since this same login module is used for logins by both browser-based
				// client (ssf) and WebDAV client (ssfs) we can't really distinguish between them... 
				BridgeClient.invoke(null, null, SERVICE_CLASS_NAME, 
						SERVICE_METHOD_NAME, SERVICE_METHOD_ARG_TYPES,
						new Object[] {null, username, password, null, "jbossportal"});
			}
			catch(Exception e) {
				log.warn("Failed to synchronize identity of user: " + getUsername(), e);
				// It's unclear whether we should abort the user login or let
				// the user continue in this case. For now, we will abort it.
				throw new LoginException(e.toString());				
			}
		}
		
		return super.commit();
	}

	/**
	 * Simply return empty string. 
	 * It is ok because we override <code>validatePassword</code>.
	 */
	protected String getUsersPassword() throws LoginException {
		return "";
	}

	@Override
	protected Group[] getRoleSets() throws LoginException {
	      Group group = new SimpleGroup("Roles");
	      if (additionalRole != null)
	      {
	         try
	         {
	            group.addMember(createIdentity(additionalRole));
	         }
	         catch (Exception e)
	         {
	            //just a try
	            log.error("Error when adding additional role: ", e);
	         }
	      }
	      return new Group[]{group};
	}

   /**
	 * This always returns true so that this login module always passes
	 * regardless of the password returned from <code>getUsersPassword</code>.
	 * 
	 * @param inputPassword
	 * @param expectedPassword
	 * @return
	 */
	protected boolean validatePassword(String inputPassword,
			String expectedPassword) {
		return true;
	}
	
	protected boolean isSynchronizeIdentity() {
		// default is true
		if (synchronizeIdentity != null
				&& synchronizeIdentity.equalsIgnoreCase("false")) {
			return Boolean.FALSE.booleanValue();
		}
		return Boolean.TRUE.booleanValue();
	}

	protected String getPassword() {
		// Since this login module is invoked if and only if the regular portal
		// login has been successful, we do know that the user-supplied password
		// is the right one. 
		return new String((char[]) getCredentials());
	}
}
