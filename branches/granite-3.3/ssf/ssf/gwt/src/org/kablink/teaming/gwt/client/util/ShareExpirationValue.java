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
package org.kablink.teaming.gwt.client.util;

import java.util.Date;

import org.kablink.teaming.gwt.client.GwtTeaming;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.shared.DateTimeFormat.PredefinedFormat;
import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * This class represents a share expiration value
 */
public class ShareExpirationValue
	implements IsSerializable
{
	private ShareExpirationType m_expirationType;
	private Long m_value;
	
	/**
	 * This class represents the different share expiration values
	 */
	public enum ShareExpirationType implements IsSerializable
	{
		AFTER_DAYS,
		NEVER,
		ON_DATE,
		
		UNKNOWN
	}

	/**
	 * 
	 */
	public ShareExpirationValue()
	{
		m_expirationType = ShareExpirationType.NEVER;
		m_value = null;
	}
	
	/**
	 * 
	 */
	public ShareExpirationValue( ShareExpirationValue value )
	{
		set( value );
	}
	
	/**
	 * 
	 */
	public ShareExpirationType getExpirationType()
	{
		return m_expirationType;
	}
	
	/**
	 * 
	 */
	public Long getValue()
	{
		return m_value;
	}
	
	/**
	 * 
	 */
	public String getValueAsString()
	{
		if ( m_expirationType == ShareExpirationType.NEVER )
			return GwtTeaming.getMessages().shareDlg_expiresNever();
		
		if ( m_expirationType == ShareExpirationType.ON_DATE )
		{
			String on;
			
			on = "";
			if ( m_value != null )
			{
				int offsetMinutes;
				Date date;
				com.google.gwt.i18n.shared.DateTimeFormat dtFormat;
				com.google.gwt.i18n.shared.TimeZone timeZone;
				
				date = new Date( m_value );
				dtFormat = DateTimeFormat.getFormat( PredefinedFormat.DATE_SHORT );
				offsetMinutes = GwtTeaming.m_requestInfo.getTimeZoneOffsetHour() * 60 * -1;
			    timeZone = com.google.gwt.i18n.client.TimeZone.createTimeZone( offsetMinutes );

				on = dtFormat.format( date, timeZone );
			}
			
			return GwtTeaming.getMessages().shareDlg_expiresOn( on );
		}
		
		if ( m_expirationType == ShareExpirationType.AFTER_DAYS )
		{
			String after;
			
			after = "";
			if ( m_value != null )
			{
				if ( m_value < 0 )
					m_value = Long.valueOf( 0 );
				
				after = m_value.toString();
			}
			
			return GwtTeaming.getMessages().shareDlg_expiresAfter( after );
		}
		
		return "Unknown expiration type";
	}
	
	/**
	 * 
	 */
	public void set( ShareExpirationValue value )
	{
		if ( value != null )
		{
			m_expirationType = value.getExpirationType();
			m_value = value.getValue();
		}
	}
	
	/**
	 * 
	 */
	public void setType( ShareExpirationType type )
	{
		m_expirationType = type;
	}
	
	/**
	 * 
	 */
	public void setValue( Long value )
	{
		m_value = value;
	}
}

