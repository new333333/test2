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

import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponseData;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * This class is used to hold mobile application branding data.
 * 
 * @author drfoster@novell.com
 */
public class GwtMobileBrandingRpcResponseData implements IsSerializable, VibeRpcResponseData {
	private GwtBrandingFileInfo	m_androidFileInfo;	//
	private GwtBrandingFileInfo	m_iosFileInfo;		//
	private GwtBrandingFileInfo	m_windowsFileInfo;	//
	
	/*
	 * Constructor method.
	 * 
	 * Zero parameter constructor required for GWT serialization.
	 */
	private GwtMobileBrandingRpcResponseData() {
		// Initialize the super class.
		super();
	}
	
	/**
	 * Constructor method.
	 * 
	 * @param androidFileInfo
	 * @param iosFileInfo
	 * @param windowsFileInfo
	 */
	public GwtMobileBrandingRpcResponseData(GwtBrandingFileInfo androidFileInfo, GwtBrandingFileInfo iosFileInfo, GwtBrandingFileInfo windowsFileInfo) {
		// Initialize this object...
		this();
		
		// ...and store the parameters.
		setAndroidFileInfo(androidFileInfo);
		setIOSFileInfo(    iosFileInfo    );
		setWindowsFileInfo(windowsFileInfo);
	}
	
	/**
	 * Get'er methods.
	 * 
	 * @return
	 */
	public GwtBrandingFileInfo getAndroidFileInfo()     {return m_androidFileInfo;}
	public GwtBrandingFileInfo getIOSFileInfo()         {return m_iosFileInfo;    }
	public GwtBrandingFileInfo getWindowsFileInfo()     {return m_windowsFileInfo;}
	public String              getAndroidFileDateTime() {return ((null == m_androidFileInfo) ? null : m_androidFileInfo.getFileDateTime());}
	public String              getAndroidFileName()     {return ((null == m_androidFileInfo) ? null : m_androidFileInfo.getFileName());    }
	public String              getIOSFileDateTime()     {return ((null == m_iosFileInfo)     ? null : m_iosFileInfo.getFileDateTime());    }
	public String              getIOSFileName()         {return ((null == m_iosFileInfo)     ? null : m_iosFileInfo.getFileName());        }
	public String              getWindowsFileDateTime() {return ((null == m_windowsFileInfo) ? null : m_windowsFileInfo.getFileDateTime());}
	public String              getWindowsFileName()     {return ((null == m_windowsFileInfo) ? null : m_windowsFileInfo.getFileName());    }
	
	/**
	 * Set'er methods.
	 * 
	 * @param
	 */
	public void setAndroidFileInfo(GwtBrandingFileInfo androidFileInfo) {m_androidFileInfo = androidFileInfo;}
	public void setIOSFileInfo(    GwtBrandingFileInfo iosFileInfo)     {m_iosFileInfo     = iosFileInfo;    }
	public void setWindowsFileInfo(GwtBrandingFileInfo windowsFileInfo) {m_windowsFileInfo = windowsFileInfo;}
}
