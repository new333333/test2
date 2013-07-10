/**
 * Copyright (c) 1998-2009 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2009 Novell, Inc. All Rights Reserved.
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
package org.kablink.teaming.gwt.client;


import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponseData;

import com.google.gwt.user.client.rpc.IsSerializable;


/**
 * This class is used to hold the Database Prune Age Configuration data.
 * @author phurley
 *
 */
public class GwtDatabasePruneConfiguration
	implements IsSerializable, VibeRpcResponseData
{
	private boolean m_fileArchivingEnabled = true;
	private boolean m_auditTrailEnabled = true;
	private boolean m_changeLogEnabled = true;
	private int m_auditTrailPruneAgeDays = 0;
	private int m_changeLogPruneAgeDays = 0;
	
	/**
	 * 
	 */
	public GwtDatabasePruneConfiguration()
	{
	}

	public boolean getFileArchivingEnabled() {
		return m_fileArchivingEnabled;
	}
	public void setFileArchivingEnabled(boolean fileArchivingEnabled) {
		m_fileArchivingEnabled = fileArchivingEnabled;
	}
	

	/**
	 * The auditTrail prune age is in months.
	 */
	public int getAuditTrailPruneAge()
	{
		return m_auditTrailPruneAgeDays / 30;
	}
	public int getAuditTrailPruneAgeDays()
	{
		return m_auditTrailPruneAgeDays;
	}
	public boolean getAuditTrailEnabled() {
		return m_auditTrailEnabled;
	}
	
	/**
	 * The changeLog prune age is in minutes.
	 */
	public int getChangeLogPruneAge()
	{
		return m_changeLogPruneAgeDays / 30;
	}
	public int getChangeLogPruneAgeDays()
	{
		return m_changeLogPruneAgeDays;
	}
	public boolean getChangeLogEnabled() {
		return m_changeLogEnabled;
	}
	
	/**
	 * 
	 */
	public void setAuditTrailPruneAge( int intervalInMonths )
	{
		m_auditTrailPruneAgeDays = intervalInMonths * 30;
	}
	public void setAuditTrailPruneAgeDays( int intervalInDays )
	{
		m_auditTrailPruneAgeDays = intervalInDays;
	}
	public void setAuditTrailEnabled(boolean auditTrailEnabled) {
		m_auditTrailEnabled = auditTrailEnabled;
	}
	
	/**
	 * 
	 */
	public void setChangeLogPruneAge( int intervalInMonths )
	{
		m_changeLogPruneAgeDays = intervalInMonths * 30;
	}
	public void setChangeLogPruneAgeDays( int intervalInDays )
	{
		m_changeLogPruneAgeDays = intervalInDays;
	}
	public void setChangeLogEnabled(boolean changeLogEnabled) {
		m_changeLogEnabled = changeLogEnabled;
	}
}
