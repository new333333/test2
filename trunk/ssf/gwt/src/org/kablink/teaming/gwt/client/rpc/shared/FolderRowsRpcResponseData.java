/**
 * Copyright (c) 1998-2013 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2013 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2013 Novell, Inc. All Rights Reserved.
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

import org.kablink.teaming.gwt.client.binderviews.folderdata.FolderRow;
import org.kablink.teaming.gwt.client.util.CloudFolderAuthentication;
import org.kablink.teaming.gwt.client.util.CloudFolderType;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * This class holds the response data for the 'get folder rows' RPC
 * command.
 * 
 * @author drfoster@novell.com
 */
public class FolderRowsRpcResponseData implements IsSerializable, VibeRpcResponseData {
	private boolean						m_totalIsApproximate;	// true -> m_totalRows is an approximate row count.  false -> It's the correct count.
	private CloudFolderAuthentication	m_cfAuthentication;		//
	private int							m_startOffset;			//
	private int							m_totalRows;			//
	private List<FolderRow> 			m_folderRows;			//
	private List<Long>					m_contributorIds;		//

	/**
	 * Constructor method.
	 * 
	 * No parameters as per GWT serialization requirements.
	 */
	public FolderRowsRpcResponseData() {
		// Initialize the super class.
		super();
	}
	
	/**
	 * Constructor method.
	 *
	 * @param folderRows
	 * @param startOffset
	 * @param totalRows
	 * @param totalIsApproximate
	 * @param contributorIds
	 */
	public FolderRowsRpcResponseData(List<FolderRow> folderRows, int startOffset, int totalRows, boolean totalIsApproximate, List<Long> contributorIds) {
		// Initialize this object...
		this();
		
		// ...and store the parameters.
		setFolderRows(        folderRows        );
		setStartOffset(       startOffset       );
		setTotalRows(         totalRows         );
		setTotalIsApproximate(totalIsApproximate);
		setContributorIds(    contributorIds    );
	}
	
	/**
	 * Constructor method.
	 * 
	 * @param cfAuthentication
	 */
	public FolderRowsRpcResponseData(CloudFolderAuthentication cfAuthentication) {
		// Initialize this object...
		this();
		
		// ...and store the parameter.
		setCloudFolderAuthentication(cfAuthentication);
	}
	
	/**
	 * Constructor method.
	 *
	 * @param cft
	 * @param authenticationUrl
	 * @param authenticationGuid
	 */
	public FolderRowsRpcResponseData(CloudFolderType cft, String authenticationUrl, String authenticationGuid) {
		// Initialize this object.
		this(
			new CloudFolderAuthentication(
				cft,
				authenticationUrl,
				authenticationGuid));
	}
	
	/**
	 * Get'er methods.
	 * 
	 * @return
	 */
	public boolean                   isTotalApproximate()           {return m_totalIsApproximate;}
	public CloudFolderAuthentication getCloudFolderAuthentication() {return m_cfAuthentication;  }
	public int                       getStartOffset()               {return m_startOffset;       }
	public int                       getTotalRows()                 {return m_totalRows;         }
	public List<FolderRow>           getFolderRows()                {return m_folderRows;        }
	public List<Long>                getContributorIds()            {return m_contributorIds;    }

	/**
	 * Set'er methods.
	 * 
	 * @param
	 */
	public void setTotalIsApproximate(       boolean                   totalIsApproximate) {m_totalIsApproximate = totalIsApproximate;}
	public void setCloudFolderAuthentication(CloudFolderAuthentication cfAuthentication)   {m_cfAuthentication   = cfAuthentication;  }
	public void setStartOffset(              int                       startOffset)        {m_startOffset        = startOffset;       }
	public void setTotalRows(                int                       totalRows)          {m_totalRows          = totalRows;         }
	public void setFolderRows(               List<FolderRow>           folderRows)         {m_folderRows         = folderRows;        }
	public void setContributorIds(           List<Long>                contributorIds)     {m_contributorIds     = contributorIds;    }

	/**
	 * Returns a count of the folder rows being tracked.
	 * 
	 * @return
	 */
	public int getFolderRowCount() {
		return ((null == m_folderRows) ? 0 : m_folderRows.size());
	}

	/**
	 * Returns true if the querying the folder's rows requires
	 * Cloud Folder authentication and false otherwise.
	 * 
	 * @return
	 */
	public boolean requiresCloudFolderAuthentication() {
		return (null != m_cfAuthentication);
	}
}
