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


/**
 * This class holds all of the information necessary to execute the
 * 'save calendar settings' command.
 * 
 * @author drfoster@novell.com
 */
public class SaveCalendarSettingsCmd extends VibeRpcCmd {
	private int		m_weekStart;	// The week starting day      to save.  (-1) -> Make no changes.
	private int		m_workDayStart;	// The work day starting hour to save.  (-1) -> Make no changes.
	private Long	m_folderId;		// The ID of the calendar folder whose settings are being saved.
	
	/**
	 * Class constructor.
	 * 
	 * For GWT serialization, must have a zero parameter
	 * constructor.
	 */
	public SaveCalendarSettingsCmd() {
		// Initialize the super class.
		super();		
	}

	/**
	 * Class constructor.
	 * 
	 * @param folderId
	 * @param weekStart
	 * @param workDayStart
	 */
	public SaveCalendarSettingsCmd(Long folderId, int weekStart, int workDayStart) {
		// Initialize this object...
		this();		
		
		// ...and store the parameters.
		setFolderId(    folderId    );
		setWeekStart(   weekStart   );
		setWorkDayStart(workDayStart);
	}
	
	/**
	 * Get'er methods.
	 * 
	 * @return
	 */
	public int  getWeekStart()    {return m_weekStart;   }
	public int  getWorkDayStart() {return m_workDayStart;}
	public Long getFolderId()     {return m_folderId;    }
	
	/**
	 * Set'er methods.
	 * 
	 * @param
	 */
	public void setWeekStart(   int  weekStart)    {m_weekStart    = weekStart;   }
	public void setWorkDayStart(int  workDayStart) {m_workDayStart = workDayStart;}
	public void setFolderId(    Long folderId)     {m_folderId     = folderId;    }
	
	/**
	 * Returns the command's enumeration value.
	 * 
	 * Implements VibeRpcCmd.getCmdType()
	 * 
	 * @return
	 */
	@Override
	public int getCmdType() {
		return VibeRpcCmdType.SAVE_CALENDAR_SETTINGS.ordinal();
	}
}
