/**
 * Copyright (c) 1998-2011 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2011 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2011 Novell, Inc. All Rights Reserved.
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

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * This class holds the response data for any command that expects a
 * List<ClipboardUser>.
 * 
 * @author drfoster@novell.com
 */
public class ClipboardUsersRpcResponseData implements IsSerializable, VibeRpcResponseData {
	private List<ClipboardUser>	m_clipboardUsers;	// The list of clipboard users being returned.
	
	
	/**
	 * Inner class used to represent a clipboard user.
	 */
	public static class ClipboardUser implements IsSerializable {
		private Long	m_userId;	// The user's ID.
		private String	m_title;	// The user's title.
		
		/**
		 * Constructor method.
		 * 
		 * For GWT serialization, must have a zero parameter
		 * constructor.
		 */
		public ClipboardUser() {
			// Initialize the super class.
			super();
		}
		
		/**
		 * Constructor methods.
		 * 
		 * @param userId
		 * @param title
		 */
		public ClipboardUser(Long userId, String title) {
			// Initialize the class...
			this();
			
			// ..and store the parameters.
			setUserId(userId);
			setTitle( title );
		}

		/**
		 * Get'er methods.
		 * 
		 * @return
		 */
		public Long   getUserId() {return m_userId;}
		public String getTitle()  {return m_title; }
		
		/**
		 * Set'er methods.
		 * 
		 * @param
		 */
		public void setUserId(Long   userId) {m_userId = userId;}
		public void setTitle( String title ) {m_title = title;  }
	}
	
	/**
	 * Constructor method.
	 * 
	 * @param clipboardUsers
	 */
	public ClipboardUsersRpcResponseData(List<ClipboardUser> clipboardUsers) {
		// Initialize the superclass..
		super();
		
		// ...and store the parameters.
		setClipboardUsers(clipboardUsers);
	}
	
	/**
	 * Constructor method.
	 * 
	 * For GWT serialization, must have a zero parameter constructor.
	 */
	public ClipboardUsersRpcResponseData() {
		// Always use the initial form of the constructor.
		this(new ArrayList<ClipboardUser>());
	}
	
	/**
	 * Get'er methods.
	 * 
	 * @return
	 */
	public List<ClipboardUser> getClipboardUsers() {return m_clipboardUsers;}
	
	/**
	 * Set'er methods.
	 * 
	 * @param
	 */
	public void setClipboardUsers(List<ClipboardUser> clipboardUsers) {m_clipboardUsers = clipboardUsers;}

	/**
	 * Adds a user to the clipboard users list.
	 * 
	 * @param user
	 */
	public void addUser(ClipboardUser user) {
		m_clipboardUsers.add(user);
	}
	
	/**
	 * Adds a user to the clipboard users list.
	 * 
	 * @param userId
	 * @param title
	 */
	public void addUser(Long userId, String title) {
		addUser(new ClipboardUser(userId, title));
	}
}
