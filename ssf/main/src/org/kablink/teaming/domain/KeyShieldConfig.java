/**
 * Copyright (c) 1998-2015 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2015 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2015 Novell, Inc. All Rights Reserved.
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
package org.kablink.teaming.domain;

import java.util.Iterator;
import java.util.TreeSet;

/**
 * ?
 *  
 * @author jwootton@novell.com
 */
public class KeyShieldConfig extends ZonedObject implements LastUpdateTimeAware
{
	private boolean enabled = false; // access="field"
	private String serverUrl;
	private Integer httpTimeout;
	private String apiAuthKey;
	private String authConnectorNames;	// Names are separated by a ','
	private Long lastUpdateTime;
	private String usernameAttributeAlias;
	private Boolean hardwareTokenRequired;
	private Boolean nonSsoAllowedForLdapUser;
	private String ssoErrorMessageForWeb;
	private String ssoErrorMessageForWebdav;

	/**
	 * 
	 */
	public KeyShieldConfig()
	{
	}
	
	/**
	 * 
	 * @param zoneId
	 */
	public KeyShieldConfig( Long zoneId )
	{
		this.zoneId = zoneId;
	}
	
	/**
	 * 
	 */
	public void copy( KeyShieldConfig config )
	{
		if ( config == null )
			return;
		
		setApiAuthKey( config.getApiAuthKey() );
		setAuthConnectorNames( config.getAuthConnectorNames() );
		setUsernameAttributeAlias( config.getUsernameAttributeAlias() );
		setEnabled( config.getEnabled() );
		setHttpTimeout( config.getHttpTimeout() );
		setServerUrl( config.getServerUrl() );
		setHardwareTokenRequired( config.getHardwareTokenRequired() );
		setNonSsoAllowedForLdapUser( config.getNonSsoAllowedForLdapUser() );
		setSsoErrorMessageForWeb( config.getSsoErrorMessageForWeb() );
		setSsoErrorMessageForWebdav( config.getSsoErrorMessageForWebdav() );
	}
	
	/**
	 * 
	 */
	public String getApiAuthKey()
	{
		return apiAuthKey;
	}
	
	/**
	 * 
	 */
	public String getAuthConnectorNames()
	{
		return authConnectorNames;
	}
	
	/**
	 * 
	 */
	public TreeSet<String> getAuthConnectorNamesAsSet()
	{
		TreeSet<String> returnValue;
		
		returnValue = new TreeSet<String>();
		
		if ( authConnectorNames != null && authConnectorNames.length() > 0 )
		{
			String[] names;
			
			names = authConnectorNames.split( "," );
			if ( names != null )
			{
				for ( String nextName: names )
				{
					returnValue.add( nextName );
				}
			}
			else
				returnValue.add( authConnectorNames );
		}
		
		return returnValue;
	}
	
	/**
	 * 
	 */
	public boolean getEnabled()
	{
		return enabled;
	}
	
	/**
	 * 
	 */
	public Integer getHttpTimeout()
	{
		return httpTimeout;
	}
	
	/**
	 * 
	 */
	public String getServerUrl()
	{
		return serverUrl;
	}
	
	/**
	 * 
	 */
	public void setApiAuthKey( String key )
	{
		this.apiAuthKey = key;
	}
	
	/**
	 * 
	 */
	public void setAuthConnectorNames( String names )
	{
		this.authConnectorNames = names;
	}
	
	/**
	 * 
	 */
	public void setAuthConnectorNamesFromSet( TreeSet<String> setOfNames )
	{
		String value = null;
		
		if ( setOfNames != null )
		{
			StringBuffer strBuff;
			Iterator<String> iter;

			strBuff = new StringBuffer();
			
			iter = setOfNames.iterator();
			while ( iter.hasNext() )
			{
				String nextName;
				
				nextName = iter.next();
				if ( strBuff.length() > 0 )
					strBuff.append( ',' );
				
				strBuff.append( nextName );
			}
			
			value = strBuff.toString().toLowerCase();
		}
		
		setAuthConnectorNames( value );
	}
	
	/**
	 * 
	 */
	public void setEnabled( boolean enabled )
	{
		this.enabled = enabled;
	}
	
	/**
	 * 
	 */
	public void setHttpTimeout( Integer timeout )
	{
		this.httpTimeout = timeout;
	}
	
	/**
	 * 
	 */
	public void setServerUrl( String url )
	{
		this.serverUrl = url;
	}
	
	public String getUsernameAttributeAlias() {
		return usernameAttributeAlias;
	}

	public void setUsernameAttributeAlias(String usernameAttributeAlias) {
		this.usernameAttributeAlias = usernameAttributeAlias;
	}

	/**
	 * 
	 * @param zoneId
	 */
	public void setZoneId( Long zoneId )
	{
		this.zoneId = zoneId;
	}

	@Override
	public Long getLastUpdateTime() {
		return lastUpdateTime;
	}

	public boolean getHardwareTokenRequired() {
		if(hardwareTokenRequired == null)
			return false; // Default is not-required.
		else
			return hardwareTokenRequired.booleanValue();
	}

	public void setHardwareTokenRequired(boolean hardwareTokenRequired) {
		this.hardwareTokenRequired = Boolean.valueOf(hardwareTokenRequired);
	}

	public boolean getNonSsoAllowedForLdapUser() {
		if(nonSsoAllowedForLdapUser == null)
			return true; // Default is true, primarily for backward compatibility
		else
			return nonSsoAllowedForLdapUser.booleanValue();
	}

	public void setNonSsoAllowedForLdapUser(
			boolean nonSsoAllowedForLdapUser) {
		this.nonSsoAllowedForLdapUser = Boolean.valueOf(nonSsoAllowedForLdapUser);
	}

	public String getSsoErrorMessageForWeb() {
		return ssoErrorMessageForWeb;
	}

	public void setSsoErrorMessageForWeb(String ssoErrorMessageForWeb) {
		this.ssoErrorMessageForWeb = ssoErrorMessageForWeb;
	}

	public String getSsoErrorMessageForWebdav() {
		return ssoErrorMessageForWebdav;
	}

	public void setSsoErrorMessageForWebdav(String ssoErrorMessageForWebdav) {
		this.ssoErrorMessageForWebdav = ssoErrorMessageForWebdav;
	}
	
	
}
