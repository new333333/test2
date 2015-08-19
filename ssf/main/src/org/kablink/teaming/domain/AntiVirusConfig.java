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
 * (c) 1998-2013 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2013 Novell, Inc. All Rights Reserved.
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

/**
 * @author Jong
 *
 */
public class AntiVirusConfig extends ZonedObject implements LastUpdateTimeAware {

	public enum Type {
		gwava((short)1);
		
		short value;
		
		Type(short value) {
			this.value = value;
		}
		
		public short getValue() {
			return value;
		}
		
		public static Type valueOf(short value) {
			switch(value) {
			case 1: return Type.gwava;
			default: throw new IllegalArgumentException("Invalid db value " + value + " for enum AntiVirusConfig.Type");
			}
		}
	}

	private Short type;
	private boolean enabled = false;
	private String serviceUrl;
	private String interfaceId;
	private String username;
	private String password;
	
	private Long lastUpdateTime;
	
	/*
	 * Constructor for Hibernate
	 */
	AntiVirusConfig() {}
	
	/*
	 * Constructor for application
	 */
	public AntiVirusConfig(Long zoneId) {
		this.zoneId = zoneId;
	}
	
	public void copy(AntiVirusConfig config) {
		this.zoneId = config.zoneId;
		this.type = config.type;
		this.enabled = config.enabled;
		this.serviceUrl = config.serviceUrl;
		this.interfaceId = config.interfaceId;
		this.username = config.username;
		this.password = config.password;

		// Do not copy lastUpdateTime since it is automatically updated
		// by the framework whenever change is made to the database.
	}
	
	public String getServiceUrl() {
		return serviceUrl;
	}

	public void setServiceUrl(String serviceUrl) {
		this.serviceUrl = serviceUrl;
	}

	public String getInterfaceId() {
		return interfaceId;
	}

	public void setInterfaceId(String interfaceId) {
		this.interfaceId = interfaceId;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Type getType() {
		if(type != null)
			return Type.valueOf(type.shortValue());
		else
			return null;
	}
	
	public void setType(Type type) {
		if(type == null)
			throw new IllegalArgumentException("Type must be specified");
		this.type = type.getValue();
	}
	
	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	@Override
	public Long getLastUpdateTime() {
		return lastUpdateTime;
	}
	
	/**
	 * @return
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{")
		.append("zoneId=")
		.append(zoneId)
		.append(",type=")
		.append(type)
		.append(",enabled=")
		.append(enabled)
		.append(",serviceUrl=")
		.append(serviceUrl)
		.append(",interfaceId=")
		.append(interfaceId)
		.append(",username=")
		.append(username)
		.append("}");
		return sb.toString();
	}
}
