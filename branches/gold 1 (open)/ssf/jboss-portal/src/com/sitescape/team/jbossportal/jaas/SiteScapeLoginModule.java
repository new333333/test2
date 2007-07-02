/**
 * The contents of this file are governed by the terms of your license
 * with SiteScape, Inc., which includes disclaimers of warranties and
 * limitations on liability. You may not use this file except in accordance
 * with the terms of that license. See the license for the specific language
 * governing your rights and limitations under the license.
 *
 * Copyright (c) 2007 SiteScape, Inc.
 *
 */
package com.sitescape.team.jbossportal.jaas;

import java.lang.reflect.Method;
import java.security.acl.Group;
import java.util.Map;

import com.sitescape.team.asmodule.bridge.SiteScapeBridgeUtil;

import org.jboss.security.SimpleGroup;
import org.jboss.security.auth.spi.UsernamePasswordLoginModule;

import javax.security.auth.Subject;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.login.LoginException;

/**
 * A SiteScape-supplied login module used to synchronize authenticated user
 * into SiteScape user database.
 */
public class SiteScapeLoginModule extends UsernamePasswordLoginModule {
	
	private static final org.jboss.logging.Logger log = 
		org.jboss.logging.Logger.getLogger(SiteScapeLoginModule.class);

	private static final String CLASS_NAME =
		"com.sitescape.team.util.SynchUser";
	
	private Object synchUser;
	private Method synchMethod;

	private String additionalRole;
	private String synchronizeIdentity;

	public SiteScapeLoginModule() {
		try { 
			// Instantiate a SynchUser. Assign it to a variable of Object
			// rather than of SynchUser type to prevent current classloader 
			// from attempting to load SynchUser class.
			synchUser = SiteScapeBridgeUtil.newInstance(CLASS_NAME);
			
			// We use reflection to invoke the method later on.
			synchMethod = SiteScapeBridgeUtil.getMethod
			(CLASS_NAME, "synch", String.class, String.class, String.class);
		}
		catch (Exception e) {
			log.error("Error instantiating SiteScapeLoginModule: ", e);
		}	
	}
	
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
				SiteScapeBridgeUtil.invoke(synchMethod, synchUser, null, username, password);
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
