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

import org.kablink.teaming.gwt.client.rpc.shared.ErrorListRpcResponseData.ErrorInfo;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * This class holds the response data for the 'get download folder as
 * CSV file url' command.
 * 
 * @author drfoster@novell.com
 */
public class DownloadFolderAsCSVFileUrlRpcResponseData implements IsSerializable, VibeRpcResponseData {
	private ErrorListRpcResponseData	m_errors;	//
	private String						m_url;		//
	
	/**
	 * Constructor method.
	 * 
	 * Zero parameter constructor as per GWT serialization
	 * requirements.
	 */
	public DownloadFolderAsCSVFileUrlRpcResponseData() {
		// Initialize the super class...
		super();
		
		// ...and initialize anything else that requires it.
		m_errors = new ErrorListRpcResponseData();
	}
	
	/**
	 * Constructor method.
	 * 
	 * @param url
	 */
	public DownloadFolderAsCSVFileUrlRpcResponseData(String url) {
		// Initialize this object...
		this();
		
		// ...and store the parameter.
		setUrl(url);
	}
	
	/**
	 * Get'er methods.
	 * 
	 * @return
	 */
	public boolean                  hasErrors()            {return m_errors.hasErrors();           }
	public ErrorListRpcResponseData getErrors()            {return m_errors;                       }
	public int                      getErrorCount()        {return m_errors.getErrorCount();       }
	public int                      getTotalMessageCount() {return m_errors.getTotalMessageCount();}
	public int                      getWarningCount()      {return m_errors.getWarningCount();     }
	public List<ErrorInfo>          getErrorList()         {return m_errors.getErrorList();        }
	public String                   getUrl()               {return m_url;                          }
	
	/**
	 * Set'er methods.
	 * 
	 * @param
	 */
	public void setUrl(String value) {m_url = value;}
	
	/**
	 * Add'er methods.
	 * 
	 * @param
	 */
	public void addError(  String error)   {m_errors.addError(  error  );}
	public void addWarning(String warning) {m_errors.addWarning(warning);}
}
