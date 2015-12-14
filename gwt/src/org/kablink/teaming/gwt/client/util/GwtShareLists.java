/**
 * Copyright (c) 1998-2013 Novell, Inc. and its licensors. All rights reserved.
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
package org.kablink.teaming.gwt.client.util;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * Class for manipulating the Sharing Blacklist/Whitelist information
 * stored in a blob in the SS_ZoneConfig table.
 * 
 * @author drfoster@novell.com
 */
public class GwtShareLists implements IsSerializable {
	private List<String> 	m_domains;	// The domains in the share list.
	private List<String> 	m_emas;		// The email addresses in the share list.
	private ShareListMode	m_mode;		// The mode of the share list.
	
	/**
	 * Enumeration value that represents the mode of a sharing list.
	 */
	public enum ShareListMode implements IsSerializable {
		BLACKLIST,
		DISABLED,
		WHITELIST;
		
		/**
		 * Get'er methods.
		 * 
		 * @return
		 */
		public boolean isBlacklist() {return BLACKLIST.equals(this);}
		public boolean isDisable()   {return DISABLED.equals( this);}
		public boolean isWhitelist() {return WHITELIST.equals(this);}
	}
	
	/**
	 * Constructor method.
	 */
	public GwtShareLists() {
		// Initialize the super class...
		super();
		
		// ...and initialize anything else that requires initialization.
		setShareListMode(ShareListMode.DISABLED);
	}
	
	/**
	 * Get'er methods.
	 * 
	 * @return
	 */
	public boolean       isBlacklist()       {return m_mode.isBlacklist();}
	public boolean       isDisable()         {return m_mode.isDisable();  }
	public boolean       isWhitelist()       {return m_mode.isWhitelist();}
	public List<String>  getDomains()        {return m_domains;           }
	public List<String>  getEmailAddresses() {return m_emas;              }
	public ShareListMode getShareListMode()  {return m_mode;              }
	
	/**
	 * Set'er methods.
	 * 
	 * @param
	 */
	public void setDomains(       List<String>  domains) {m_domains = domains;}
	public void setEmailAddresses(List<String>  emas)    {m_emas    = emas;   }
	public void setShareListMode( ShareListMode mode)    {m_mode    = mode;   }

	/**
	 * Adds a domain to the share list.
	 * 
	 * @param domain
	 */
	public void addDomain(String domain) {
		if (null == domain) {
			return;
		}
		domain = domain.trim();
		if ((0 < domain.length()) && ('@' == domain.charAt(0))) {
			domain = domain.substring(1);
		}
		if (0 == domain.length()) {
			return;
		}
		
		validateDomains();
		if (!(m_domains.contains(domain))) {
			m_domains.add(domain);
		}
	}
	
	/**
	 * Adds an email address to the share list.
	 * 
	 * @param ema
	 */
	public void addEmailAddress(String ema) {
		if (null == ema) {
			return;
		}
		ema = ema.trim();
		if (0 == ema.length()) {
			return;
		}
		
		validateEmailAddresses();
		if (!(m_emas.contains(ema))) {
			m_emas.add(ema);
		}
	}

	/*
	 * Validates that a domain list has been allocated.
	 */
	private void validateDomains() {
		if (null == m_domains) {
			m_domains = new ArrayList<String>();
		}
	}

	/*
	 * Validates that an email address list has been allocated.
	 */
	private void validateEmailAddresses() {
		if (null == m_emas) {
			m_emas = new ArrayList<String>();
		}
	}
}
