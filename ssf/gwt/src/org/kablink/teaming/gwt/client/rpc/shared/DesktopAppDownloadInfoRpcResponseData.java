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

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * This class holds the response data for the RPCs that return
 * information about downloading the desktop applications.
 * 
 * @author drfoster@novell.com
 */
public class DesktopAppDownloadInfoRpcResponseData implements IsSerializable, VibeRpcResponseData {
	private FileDownloadInfo	m_mac;		//
	private FileDownloadInfo	m_win32;	//
	private FileDownloadInfo	m_win64;	//
	private String 				m_macHelpUrl;
	private String 				m_winHelpUrl;

	/**
	 * Inner class used to track filename/URL pairs.
	 */
	public static class FileDownloadInfo implements IsSerializable {
		private String m_filename;	// The name of this file.
		private String m_md5;		// The MD5 checksum for the file, if known.
		private String m_url;		// The URL to download this file.

		/*
		 * Constructor method.
		 * 
		 * Zero parameter constructor required for GWT serialization.
		 */
		private FileDownloadInfo() {
			// Initialize the super class.
			super();
		}
		
		/**
		 * Constructor method.
		 * 
		 * @param filename
		 * @param url
		 * @param md5
		 */
		public FileDownloadInfo(String filename, String url, String md5) {
			// Initialize this object...
			this();
			
			// ...and store the parameters.
			setFilename(filename);
			setUrl(     url     );
			setMd5(     md5     );
		}
		/**
		 * Constructor method.
		 * 
		 * @param filename
		 * @param url
		 */
		public FileDownloadInfo(String filename, String url) {
			// Initialize this object.
			this(filename, url, null);
		}

		/**
		 * Get'er methods.
		 * 
		 * @return
		 */
		public String getFilename(){return m_filename; }
		public String getMd5()     {return m_md5;      }
		public String getUrl()     {return m_url;      }

		/**
		 * Set'er methods.
		 * 
		 * @param
		 */
		public void setFilename(String s) {m_filename = s;}
		public void setMd5(     String s) {m_md5      = s;}
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
	public FileDownloadInfo getMac()   {return m_mac;  }
	public FileDownloadInfo getWin32() {return m_win32;}
	public FileDownloadInfo getWin64() {return m_win64;}

	public String getMacHelpUrl() {
		return m_macHelpUrl;
	}

	public String getWinHelpUrl() {
		return m_winHelpUrl;
	}

	/**
	 * Set'er methods.
	 * 
	 * @param
	 */
	public void setMac(  FileDownloadInfo mac)   {m_mac   = mac;  }
	public void setWin32(FileDownloadInfo win32) {m_win32 = win32;}
	public void setWin64(FileDownloadInfo win64) {m_win64 = win64;}

	public void setMacHelpUrl(String macHelpUrl) {
		this.m_macHelpUrl = macHelpUrl;
	}

	public void setWinHelpUrl(String winHelpUrl) {
		this.m_winHelpUrl = winHelpUrl;
	}

}
