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
package org.kablink.teaming.gwt.client.rpc.shared;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * This class holds the response data for the RPCs that return
 * information about user default settings.
 * 
 * @author drfoster@novell.com
 */
public class DefaultUserSettingsInfoRpcResponseData implements IsSerializable, VibeRpcResponseData {
	private String	m_locale;		//
	private String	m_localeExt;	//
	private String	m_timeZone;		//
	private String	m_timeZoneExt;	//
	
	/*
	 * Constructor method. 
	 * 
	 * For GWT serialization, must have a zero parameter constructor.
	 */
	private DefaultUserSettingsInfoRpcResponseData() {
		// Initialize the super class.
		super();
	}
	
	/**
	 * Constructor method. 
	 * 
	 * @param timeZone
	 * @param locale
	 * @param timeZoneExt
	 * @param localeExt
	 */
	public DefaultUserSettingsInfoRpcResponseData(String timeZone, String locale, String timeZoneExt, String localeExt) {
		// Initialize this object...
		this();
		
		// ...and store the parameters.
		setTimeZone(   timeZone   );
		setLocale(     locale     );
		setTimeZoneExt(timeZoneExt);
		setLocaleExt(  localeExt  );
	}
	
	/**
	 * Get'er methods.
	 * 
	 * @return
	 */
	public String getLocale()      {return m_locale;     }
	public String getLocaleExt()   {return m_localeExt;  }
	public String getTimeZone()    {return m_timeZone;   }
	public String getTimeZoneExt() {return m_timeZoneExt;}
	
	/**
	 * Set'er methods.
	 * 
	 * @param
	 */
	public void setLocale(     String locale)      {m_locale      = locale;     }
	public void setLocaleExt(  String localeExt)   {m_localeExt   = localeExt;  }
	public void setTimeZone(   String timeZone)    {m_timeZone    = timeZone;   }
	public void setTimeZoneExt(String timeZoneExt) {m_timeZoneExt = timeZoneExt;}
}
