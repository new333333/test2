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
package org.kablink.teaming.gwt.client.util;


import java.util.ArrayList;

import com.google.gwt.user.client.rpc.IsSerializable;


/**
 * This class is used to hold subscription data
 * @author jwootton
 *
 */
public class SubscriptionData
	implements IsSerializable
{
	public static final int SEND_TO_NONE					= 0;
	public static final int SEND_TO_PRIMARY_EMAIL_ADDRESS	= 1;
	public static final int SEND_TO_MOBILE_EMAIL_ADDRESS	= 2;
	public static final int SEND_TO_TEXT_ADDRESS			= 4;
	
	private int m_sendEmailTo;
	private int m_sendEmailToWithoutAttachment;
	private int m_sendTextTo;
	
	private String m_primaryEmailAddress;
	private String m_mobileEmailAddress;
	private String m_textAddress;
	
	/**
	 * 
	 */
	public SubscriptionData()
	{
		m_sendEmailTo = SEND_TO_NONE;
		m_sendEmailToWithoutAttachment = SEND_TO_NONE;
		m_sendTextTo = SEND_TO_NONE;
		
		m_primaryEmailAddress = null;
		m_mobileEmailAddress = null;
		m_textAddress = null;
	}

	/**
	 * Return the mobile email address
	 */
	public String getMobileEmailAddress()
	{
		return m_mobileEmailAddress;
	}
	
	/**
	 * Return the primary email address
	 */
	public String getPrimaryEmailAddress()
	{
		return m_primaryEmailAddress;
	}
	
	/**
	 * For the given value return who the recipients are as a string.
	 */
	private String[] getRecipients( int sendTo )
	{
		ArrayList<String> setting;
		String[] returnValue;
		
		if ( sendTo == SEND_TO_NONE )
			return null;
		
		setting = new ArrayList<String>();
		
		if ( (sendTo & SubscriptionData.SEND_TO_PRIMARY_EMAIL_ADDRESS) > 0 )
			setting.add( "_primary" );
		
		if ( (sendTo & SubscriptionData.SEND_TO_MOBILE_EMAIL_ADDRESS) > 0 )
			setting.add( "_mobile" );
		
		if ( (sendTo & SubscriptionData.SEND_TO_TEXT_ADDRESS) > 0 )
			setting.add( "_text" );
		
		if ( setting.size() > 0 )
			returnValue = (String[]) setting.toArray( new String[setting.size()] );
		else
			returnValue = null;
		
		return returnValue;
	}
	
	
	/**
	 * Return the value of which addresses should be sent an email.
	 */
	public int getSendEmailTo()
	{
		return m_sendEmailTo;
	}
	
	/**
	 * Return the value of which addresses should be sent an email.
	 */
	public String[] getSendEmailToAsString()
	{
		return getRecipients( m_sendEmailTo );
	}
	
	/**
	 * Return the value of which addresses should be sent an email without attachments.
	 */
	public int getSendEmailToWithoutAttachment()
	{
		return m_sendEmailToWithoutAttachment;
	}
	
	/**
	 * Return the value of which addresses should be sent an email without attachments.
	 */
	public String[] getSendEmailToWithoutAttachmentAsString()
	{
		return getRecipients( m_sendEmailToWithoutAttachment );
	}
	
	/**
	 * Return the value of which addresses should be sent a text.
	 */
	public int getSendTextTo()
	{
		return m_sendTextTo;
	}
	
	/**
	 * Return the value of which addresses should be sent a text.
	 */
	public String[] getSendTextToAsString()
	{
		return getRecipients( m_sendTextTo );
	}
	
	/**
	 * Return the text messaging address
	 */
	public String getTextMessagingAddress()
	{
		return m_textAddress;
	}
	
	/**
	 * Parse the "send to" values found in the String[] that came from the db and convert them into an integer.
	 */
	private int parseSendToValues( String[] values )
	{
		int i;
		int sendTo = SEND_TO_NONE;
		
		if ( values == null )
			return SEND_TO_NONE;
		
		for (i = 0; i < values.length; ++i)
		{
			String value;
			
			value = values[i];
			if ( value != null )
			{
				if ( value.equalsIgnoreCase( "_primary" ) )
					sendTo |= SEND_TO_PRIMARY_EMAIL_ADDRESS;
				else if ( value.equalsIgnoreCase( "_mobile" ) )
					sendTo |= SEND_TO_MOBILE_EMAIL_ADDRESS;
				else if ( value.equalsIgnoreCase( "_text" ) )
					sendTo |= SEND_TO_TEXT_ADDRESS;
			}
		}
		
		return sendTo;
	}

	/**
	 * Set the mobile email address
	 */
	public void setMobileEmailAddress( String address )
	{
		m_mobileEmailAddress = address;
	}
	
	/**
	 * Set the primary email address
	 */
	public void setPrimaryEmailAddress( String address )
	{
		m_primaryEmailAddress = address;
	}
	
	/**
	 * Set the value of which addresses should be sent an email.
	 */
	public void setSendEmailTo( String[] values )
	{
		// Parse the values that come from the db and convert them into an integer.
		m_sendEmailTo = parseSendToValues( values );
	}
	
	/**
	 * Set the value of which addresses should be sent an email.
	 */
	public void setSendEmailTo( int value )
	{
		m_sendEmailTo = value;
	}
	
	/**
	 * Set the value of which addresses should be sent an email without attachments.
	 */
	public void setSendEmailToWithoutAttachment( String[] values )
	{
		// Parse the values that come from the db and convert them into an integer.
		m_sendEmailToWithoutAttachment = parseSendToValues( values );
	}
	
	/**
	 * Set the value of which addresses should be sent an email without attachments.
	 */
	public void setSendEmailToWithoutAttachment( int value )
	{
		m_sendEmailToWithoutAttachment = value;
	}
	
	/**
	 * Set the value of which addresses should be sent a text.
	 */
	public void setSendTextTo( String[] values )
	{
		// Parse the values that come from the db and convert them into an integer.
		m_sendTextTo = parseSendToValues( values );
	}
	
	/**
	 * Set the value of which addresses should be sent a text.
	 */
	public void setSendTextTo( int value )
	{
		m_sendTextTo = value;
	}
	
	/**
	 * Set the text messaging address
	 */
	public void setTextMessagingAddress( String address )
	{
		m_textAddress = address;
	}
}
