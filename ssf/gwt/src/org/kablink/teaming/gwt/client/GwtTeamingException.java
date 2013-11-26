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

package org.kablink.teaming.gwt.client;

import com.google.gwt.user.client.rpc.IsSerializable;
import com.google.gwt.user.client.rpc.SerializationException;

/**
 * This can be used for all GWT Teaming exceptions.
 * @author jwootton
 *
 */
public class GwtTeamingException extends SerializationException
	implements IsSerializable
{
	/**
	 * This class defines all the possible types of exceptions that GwtTeamingException knows about.
	 * @author jwootton
	 *
	 */
	public enum ExceptionType implements IsSerializable
	{
		ACCESS_CONTROL_EXCEPTION,
		CHANGE_PASSWORD_EXCEPTION,
		EXTENSION_DEFINITION_IN_USE,
		FAVORITES_LIMIT_EXCEEDED,
		GROUP_ALREADY_EXISTS,
		LDAP_GUID_NOT_CONFIGURED,
		NET_FOLDER_ALREADY_EXISTS,
		NET_FOLDER_ROOT_ALREADY_EXISTS,
		NO_BINDER_BY_THE_ID_EXCEPTION,
		NO_FOLDER_ENTRY_BY_THE_ID_EXCEPTION,
		NO_RPC_HANDLER,
		USER_NOT_LOGGED_IN,
		FOLDER_EXPECTED,
		INVALID_AUTO_UPDATE_URL,
		UNKNOWN;
	}// end ExceptionType
	

	private static final long serialVersionUID = -5972316795230937529L;
	private ExceptionType m_type = ExceptionType.UNKNOWN;
	private String m_additionalDetails;

	/**
	 * Constructor method.
	 * 
	 * @param type
	 * @param additionalDetails
	 */
	public GwtTeamingException( ExceptionType type, String additionalDetails ){
		// Initialize the super class...
		super();
		
		// ...and store the parameters.
		setExceptionType(     type              );
		setAdditionalDetails( additionalDetails );
	}// end GwtTeamingException()
	
	/**
	 * Constructor method.
	 * 
	 * Zero parameter constructor as per GWT serialization
	 * requirements.
	 */
	public GwtTeamingException()
	{
		// Always use the initial form of the constructor.
		this( ExceptionType.UNKNOWN, "" );
	}// end GwtTeamingException()
	
	/**
	 * Constructor method. 
	 * 
	 * @param type
	 */
	public GwtTeamingException( ExceptionType type )
	{
		// Always use the initial form of the constructor.
		this( type, "" );
	}// end GwtTeamingException()
	
	/**
	 * Constructor method. 
	 * 
	 * @param additionalDetails
	 */
	public GwtTeamingException( String additionalDetails )
	{
		// Always use the initial form of the constructor.
		this( ExceptionType.UNKNOWN, additionalDetails );
	}// end GwtTeamingException()
	
	/**
	 * Return the type of exception we are dealing with.
	 * 
	 * @return
	 */
	public ExceptionType getExceptionType()
	{
		return m_type;
	}// end getExceptionType()
	
	/**
	 * Returns the addition details string, if any.
	 * 
	 * @return
	 */
	public String getAdditionalDetails() {
		return m_additionalDetails;
	}

	
	/**
	 * Set the type of exception we are dealing with.
	 * 
	 * @param type
	 */
	public void setExceptionType( ExceptionType type )
	{
		m_type = type;
	}// end setExceptionType()

	/**
	 * Stores an additional details string.
	 * 
	 * @param additionalDetails
	 */
	public void setAdditionalDetails( String additionalDetails )
	{
		m_additionalDetails = additionalDetails;
	}// end setAdditionalDetails()
}// end GwtTeamingException
