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

import org.kablink.teaming.gwt.client.util.CloudFolderType;

/**
 * This class holds all of the information necessary to execute the
 * 'add new folder' command.
 * 
 * @author drfoster@novell.com
 */
public class AddNewFolderCmd extends VibeRpcCmd {
	private CloudFolderType	m_cft;				// null -> Not a cloud folder.
	private Long			m_binderId;			//
	private Long			m_folderTemplateId;	//
	private String			m_folderName;		//
	
	/**
	 * Constructor method.
	 * 
	 * For GWT serialization requirements, must have a zero parameter
	 * constructor.
	 */
	public AddNewFolderCmd() {
		// Initialize the super class.
		super();
	}
	
	/**
	 * Constructor method.
	 * 
	 * @param binderId
	 * @param folderTemplateId
	 * @param folderName
	 * @param cft
	 */
	public AddNewFolderCmd(Long binderId, Long folderTemplateId, String folderName, CloudFolderType cft) {
		// Initialize this object...
		this();
		
		// ...and store the parameters.
		setBinderId(        binderId        );
		setFolderTemplateId(folderTemplateId);
		setFolderName(      folderName      );
		setCloudFolderType( cft             );
	}
	
	/**
	 * Constructor method.
	 * 
	 * @param binderId
	 * @param folderTemplateId
	 * @param folderName
	 */
	public AddNewFolderCmd(Long binderId, Long folderTemplateId, String folderName) {
		// Initialize this object...
		this(binderId, folderTemplateId, folderName, null);
	}
	
	/**
	 * Get'er methods.
	 * 
	 * @return
	 */
	public CloudFolderType getCloudFolderType()  {return m_cft;             }
	public Long            getBinderId()         {return m_binderId;        }
	public Long            getFolderTemplateId() {return m_folderTemplateId;}
	public String          getFolderName()       {return m_folderName;      }
	
	/**
	 * Set'er methods.
	 * 
	 * @param
	 */
	public void setCloudFolderType( CloudFolderType cft)              {m_cft              = cft;             }
	public void setBinderId(        Long            binderId)         {m_binderId         = binderId;        }
	public void setFolderTemplateId(Long            folderTemplateId) {m_folderTemplateId = folderTemplateId;}
	public void setFolderName(      String          folderName)       {m_folderName       = folderName;      }
	
	/**
	 * Returns the command's enumeration value.
	 * 
	 * Implements VibeRpcCmd.getCmdType()
	 * 
	 * @return
	 */
	@Override
	public int getCmdType() {
		return VibeRpcCmdType.ADD_NEW_FOLDER.ordinal();
	}
}
