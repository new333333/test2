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

import org.kablink.teaming.gwt.client.util.ActivityStreamData.SpecificBinderData;
import org.kablink.teaming.gwt.client.util.ActivityStreamDataType;
import org.kablink.teaming.gwt.client.util.ActivityStreamInfo;
import org.kablink.teaming.gwt.client.util.ActivityStreamParams;
import org.kablink.teaming.gwt.client.util.ActivityStreamData.PagingData;

/**
 * This class holds all of the information necessary to execute the
 * 'get activity stream data' command.
 * 
 * @author drfoster@novell.com
 */
public class GetActivityStreamDataCmd extends VibeRpcCmd {
	private ActivityStreamDataType	m_asDataType;			//
	private ActivityStreamInfo		m_asInfo;				//
	private ActivityStreamParams	m_asParams;				//
	private PagingData				m_pagingData;			//
	private SpecificBinderData		m_sbData;	//
	
	/**
	 * Class constructor.
	 * 
	 * For GWT serialization, must have a zero parameter
	 * constructor.
	 */
	public GetActivityStreamDataCmd() {
		// Initialize the super class.
		super();		
	}

	/**
	 * Class constructor.
	 * 
	 * @param asDataType
	 * @param asInfo
	 * @param asParams
	 * @param pagingData 
	 */
	public GetActivityStreamDataCmd(ActivityStreamDataType asDataType, ActivityStreamInfo asInfo, ActivityStreamParams asParams, PagingData pagingData) {
		// Initialize this object...
		this();

		// ...and store the parameters.
		setActivityStreamDataType(asDataType);
		setActivityStreamInfo(    asInfo    );
		setActivityStreamParams(  asParams  );
		setPagingData(            pagingData);
	}
	
	/**
	 * Class constructor.
	 * 
	 * @param asDataType
	 * @param asInfo
	 * @param asParams
	 * @param pagingData
	 * @param sbData 
	 */
	public GetActivityStreamDataCmd(ActivityStreamDataType asDataType, ActivityStreamInfo asInfo, ActivityStreamParams asParams, PagingData pagingData, SpecificBinderData sbData) {
		// Initialize this object...
		this(asDataType, asInfo, asParams, pagingData);
		
		// ...and store the remaining parameters.
		setSpecificBinderData(sbData);
	}
	
	/**
	 * Get'er methods.
	 * 
	 * @return
	 */
	public ActivityStreamDataType getActivityStreamDataType() {return m_asDataType;}
	public ActivityStreamInfo     getActivityStreamInfo()     {return m_asInfo;    }
	public ActivityStreamParams   getActivityStreamParams()   {return m_asParams;  }
	public PagingData             getPagingData()             {return m_pagingData;}
	public SpecificBinderData     getSpecificBinderData()     {return m_sbData;    }
	
	/**
	 * Set'er methods.
	 * 
	 * @param
	 */
	public void setActivityStreamDataType(ActivityStreamDataType asDataType) {m_asDataType = asDataType;}
	public void setActivityStreamInfo    (ActivityStreamInfo     asInfo)     {m_asInfo     = asInfo;    }
	public void setActivityStreamParams(  ActivityStreamParams   asParams)   {m_asParams   = asParams;  }
	public void setPagingData(            PagingData             pagingData) {m_pagingData = pagingData;}
	public void setSpecificBinderData(    SpecificBinderData     sbData)     {m_sbData     = sbData;    }
	
	/**
	 * Returns the command's enumeration value.
	 * 
	 * Implements VibeRpcCmd.getCmdType()
	 * 
	 * @return
	 */
	@Override
	public int getCmdType() {
		return VibeRpcCmdType.GET_ACTIVITY_STREAM_DATA.ordinal();
	}
}
