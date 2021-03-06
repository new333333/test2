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

import org.kablink.teaming.util.encrypt.EncryptUtil;

/**
 * Domain object that models a proxy identity for Net Folder Server
 * authentication.
 * 
 * @author drfoster@novell.com
 */
public class ProxyIdentity extends PersistentLongIdObject {
	private   String	plainPassword;	// Cached password in clear text in memory.
    protected String	password;		// Encrypted password value for storage/DB.
	protected String	proxyName;		//
	protected String	title;			//

	/**
	 * Constructor method.
	 * 
     * Used by Hibernate.
	 */
	protected ProxyIdentity() {
    }

	/**
	 * Constructor method.
	 * 
     * Used by the application.
     * 
     * @param password
     * @param proxyName
     * @param title
	 */
	public ProxyIdentity(String password, String proxyName, String title) {
		setPassword( password );
		setProxyName(proxyName);
		setTitle(    title    );
    }

	/**
	 * Constructor method (copy constructor.)
	 *
     * Used by the application.
     * 
	 * @param pi
	 */
	public void copy(ProxyIdentity pi) {
		// Don't copy ID and zone ID. Copy just the data.
		setPlainPassword(pi.getPlainPassword());
		setPassword(     pi.getPassword()     );
		setProxyName(    pi.getProxyName()    );
		setTitle(        pi.getTitle()        );
	}

	/*
	 * Returns the plain password.
	 */
	private String getPlainPassword() {
		return plainPassword;
	}

	/*
	 * Stores a plain password.
	 */
	private void setPlainPassword(String plainPassword) {
		this.plainPassword = plainPassword;
	}
	
	/**
	 * Returns the proxy's clear text password.
	 * 
	 * @return
	 */
	public String getPassword() {
		if (null == plainPassword) {
			if (null != password) {
				plainPassword = EncryptUtil.getStringEncryptor_second_gen().decrypt(password);
			}
		}
		return plainPassword;
	}
	
	/**
	 * Sets the proxy's password from clear text.
	 * 
	 * @param password
	 */
	public void setPassword(String password) {
		if (null == password) {
			this.password      =
			this.plainPassword = null;
		}
		else {
			this.password      = EncryptUtil.getStringEncryptor_second_gen().encrypt(password);			
			this.plainPassword = password;
		}
	}

	/**
	 * Returns the proxy's name.
	 * 
	 * @return
	 */
	public String getProxyName() {
		return this.proxyName;
	}
	
	/**
	 * Sets the proxy's name.
	 * 
	 * @param proxyName
	 */
	public void setProxyName(String proxyName) {
		this.proxyName = proxyName;
	}
	
	/**
	 * Returns the proxy's title.
	 * 
	 * @return
	 */
	public String getTitle() {
		return this.title;
	}

	/**
	 * Sets the proxy's title.
	 * 
	 * @param title
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * @return
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{")
		.append("id=")
		.append(id)
		.append(",proxyName=")
		.append(this.proxyName)
		.append(",title=")
		.append(this.title)
		.append("}");
		return sb.toString();
	}
}
