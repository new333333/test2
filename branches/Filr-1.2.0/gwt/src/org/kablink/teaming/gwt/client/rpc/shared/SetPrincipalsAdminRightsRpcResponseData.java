/**
 * Copyright (c) 1998-2014 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2014 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2014 Novell, Inc. All Rights Reserved.
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

import java.util.List;
import java.util.Map;

import org.kablink.teaming.gwt.client.rpc.shared.ErrorListRpcResponseData.ErrorInfo;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * This class holds the response data for the 'set selected principals
 * admin rights' RPC command
 * 
 * @author drfoster@novell.com
 */
public class SetPrincipalsAdminRightsRpcResponseData implements IsSerializable, VibeRpcResponseData {
	private ErrorListRpcResponseData	m_errorList;			//
	private Map<Long, AdminRights>		m_adminRightsChangeMap;	//
	
	/**
	 * Inner class used to encapsulate an admin rights display string
	 * with a boolean indicating whether the entity has admin rights.
	 */
	public static class AdminRights implements IsSerializable {
		private boolean	m_admin;	    //
		private String	m_adminRights;	//

		/*
		 * Constructor method.
		 * 
		 * Zero parameter constructor required for GWT serialization.
		 */
		private AdminRights() {
			super();
		}
		
		/**
		 * Constructor method.
		 * 
		 * @param adminRights
		 * @param admin
		 */
		public AdminRights(String adminRights, boolean admin) {
			// Initialize this object...
			this();
			
			// ...and store the parameters.
			setAdminRights(adminRights);
			setAdmin(      admin      );
		}
		
		/**
		 * Get'er methods.
		 * 
		 * @return
		 */
		public boolean isAdmin()        {return m_admin;      }
		public String  getAdminRights() {return m_adminRights;}

		/**
		 * Set'er methods.
		 * 
		 * @param
		 */
		public void setAdmin(      boolean admin)       {m_admin       = admin;      }
		public void setAdminRights(String  adminRights) {m_adminRights = adminRights;}
	}
	
	/**
	 * Constructor method.
	 * 
	 * For GWT serialization, must have a zero parameter constructor.
	 */
	public SetPrincipalsAdminRightsRpcResponseData() {
		// Initialize the super class.
		super();
	}

	/**
	 * Constructor method.
	 * 
	 * @param errorList
	 */
	public SetPrincipalsAdminRightsRpcResponseData(List<ErrorInfo> errorList) {
		// Initialize this object...
		this();
		
		// ...and store the parameter.
		setErrorList(errorList);
	}
	
	/**
	 * Get'er methods.
	 * 
	 * @return
	 */
	public Map<Long, AdminRights> getAdminRightsChangeMap() {return m_adminRightsChangeMap;}
	
	/**
	 * Set'er methods.
	 * 
	 * @param
	 */
	public void setAdminRightsChangeMap(Map<Long, AdminRights> adminRightsChangeMap) {m_adminRightsChangeMap = adminRightsChangeMap;}
	
	/**
	 * Adds an error to the list.
	 * 
	 * @param error
	 */
	public void addError(String error) {
		// If we weren't given an error...
		if ((null == error) || (0 == error.length())) {
			// ...bail.
			return;
		}

		// If we don't have an error list yet...
		if (null == m_errorList) {
			// ...create one.
			m_errorList = new ErrorListRpcResponseData();
		}

		// Add the error to the list.
		m_errorList.addError(error);
	}

	/**
	 * Returns the List<ErrorInfo> from the encapsulated
	 * ErrorListRpcResponseData.
	 * 
	 * @return
	 */
	public List<ErrorInfo> getErrorList() {
		if (null != m_errorList) {
			return m_errorList.getErrorList();
		}
		return null;
	}
	
	/**
	 * Returns true if the encapsulated ErrorListRpcResponseData
	 * contains any errors and false otherwise.
	 * 
	 * @return
	 */
	public boolean hasErrors() {
		return ((null != m_errorList) && m_errorList.hasErrors());
	}
	
	/**
	 * Stores a new error list in the encapsulated
	 * ErrorListRpcResponseData.
	 * 
	 * @param
	 */
	public void setErrorList(List<ErrorInfo> errorList) {
		if (null == m_errorList) {
			m_errorList = new ErrorListRpcResponseData();
		}
		m_errorList.setErrorList(errorList);
	}
}
