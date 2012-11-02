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
package org.kablink.teaming.gwt.client.rpc.shared;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * This class holds the response data for the RPCs that return
 * information about downloading the desktop applications.
 * 
 * @author drfoster@novell.com
 */
public class DesktopAppDownloadInfoRpcResponseData implements IsSerializable, VibeRpcResponseData {
	private FilenameUrlPair	m_mac;		//
	private FilenameUrlPair	m_win32;	//
	private FilenameUrlPair	m_win64;	//
	
	/**
	 * Inner class used to track filename/URL pairs.
	 */
	public static class FilenameUrlPair implements IsSerializable {
		private String m_filename;	// The filename for this filename/URL pair.
		private String m_url;		// The URL      for this filename/URL pair.

		/**
		 * Constructor method.
		 * 
		 * Zero parameter constructor required for GWT serialization.
		 */
		public FilenameUrlPair() {
			// Initialize the super class.
			super();
		}
		
		/**
		 * Constructor method.
		 * 
		 * @param filename
		 * @param url
		 */
		public FilenameUrlPair(String filename, String url) {
			// Initialize this object...
			this();
			
			// ...and store the parameters.
			setFilename(filename);
			setUrl(     url     );
		}

		/**
		 * Get'er methods.
		 * 
		 * @return
		 */
		public String getFilename(){return m_filename; }
		public String getUrl()     {return m_url;      }

		/**
		 * Set'er methods.
		 * 
		 * @param
		 */
		public void setFilename(String s) {m_filename = s;}
		public void setUrl(     String s) {m_url      = s;}
	}
	
	/**
	 * Constructor method. 
	 * 
	 * For GWT serialization, must have a zero parameter constructor.
	 */
	public DesktopAppDownloadInfoRpcResponseData() {
		// Initialize the super class.
		super();
	}
	
	/**
	 * Get'er methods.
	 * 
	 * @return
	 */
	public FilenameUrlPair getMac() {return m_mac;  }
	public FilenameUrlPair getWin32() {return m_win32;}
	public FilenameUrlPair getWin64() {return m_win64;}
	
	/**
	 * Set'er methods.
	 * 
	 * @param
	 */
	public void setMac(  FilenameUrlPair mac)   {m_mac   = mac;  }
	public void setWin32(FilenameUrlPair win32) {m_win32 = win32;}
	public void setWin64(FilenameUrlPair win64) {m_win64 = win64;}
}
