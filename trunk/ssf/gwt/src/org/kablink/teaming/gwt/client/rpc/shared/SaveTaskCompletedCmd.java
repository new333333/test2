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

import java.util.ArrayList;
import java.util.List;

import org.kablink.teaming.gwt.client.util.EntryId;


/**
 * This class holds all of the information necessary to execute the
 * 'save task completed' command.
 * 
 * @author drfoster@novell.com
 */
public class SaveTaskCompletedCmd extends VibeRpcCmd {
	private List<EntryId>	m_taskIds;		//
	private String			m_completed;	//
	
	/**
	 * Class constructor.
	 * 
	 * For GWT serialization, must have a zero parameter
	 * constructor.
	 */
	public SaveTaskCompletedCmd() {
		super();		
	}

	/**
	 * Class constructor.
	 * 
	 * @param taskIds
	 */
	public SaveTaskCompletedCmd(List<EntryId> taskIds, String completed) {
		this();		
		m_taskIds   = taskIds;
		m_completed = completed;
	}
	
	/**
	 * Class constructor.
	 * 
	 * @param binderId
	 * @param entryId
	 */
	public SaveTaskCompletedCmd(Long binderId, Long entryId, String completed) {
		this();
		
		m_completed = completed;
		m_taskIds   = new ArrayList<EntryId>();
		m_taskIds.add(new EntryId(binderId, entryId));
	}
	
	/**
	 * Get'er methods.
	 * 
	 * @return
	 */
	public List<EntryId> getTaskIds()   {return m_taskIds;  }	
	public String        getCompleted() {return m_completed;}	
	
	/**
	 * Returns the command's enumeration value.
	 * 
	 * Implements VibeRpcCmd.getCmdType()
	 * 
	 * @return
	 */
	@Override
	public int getCmdType() {
		return VibeRpcCmdType.SAVE_TASK_COMPLETED.ordinal();
	}
}
