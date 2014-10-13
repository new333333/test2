/**
 * Copyright (c) 1998-2012 Novell, Inc. and its licensors. All rights reserved.
 * 
 * This work is governed by the Common Public Attribution Login Version 1.0 (the
 * "CPAL"); you may not use this file except in compliance with the CPAL. You may
 * obtain a copy of the CPAL at http://www.opensource.org/licenses/cpal_1.0. The
 * CPAL is based on the Mozilla Public Login Version 1.1 but Sections 14 and 15
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

import java.util.Date;
import java.util.List;

/**
 * This class holds all of the information necessary to execute the
 * 'create login report' command.
 * 
 * @author drfoster@novell.com
 */
public class CreateLoginReportCmd extends VibeRpcCmd {
	private Date		m_begin;		//
	private Date		m_end;			//
	private List<Long>	m_userIds;		//
	private String		m_longSortBy;	//
	private String		m_reportType;	//
	private String		m_shortSortBy;	//
	
	/**
	 * Constructor method.
	 * 
	 * For GWT serialization, must have a zero parameter constructor.
	 */
	public CreateLoginReportCmd() {
		// Initialize the super class.
		super();
	}
	
	/**
	 * Constructor method.
	 * 
	 * @param begin
	 * @param end
	 * @param userIds
	 * @param reportType
	 * @param longSortBy
	 * @param shortSortBy
	 */
	public CreateLoginReportCmd(Date begin, Date end, List<Long> userIds, String reportType, String longSortBy, String shortSortBy) {
		// Initialize this object...
		super();
		
		// ...and store the parameters.
		setBegin(      begin      );
		setEnd(        end        );
		setUserIds(    userIds    );
		setReportType( reportType );
		setLongSortBy( longSortBy );
		setShortSortBy(shortSortBy);
	}
	
	/**
	 * Get'er methods.
	 * 
	 * @return
	 */
	public Date       getBegin()       {return m_begin;      }
	public Date       getEnd()         {return m_end;        }
	public List<Long> getUserIds()     {return m_userIds;    }
	public String     getLongSortBy()  {return m_longSortBy; }
	public String     getReportType()  {return m_reportType; }
	public String     getShortSortBy() {return m_shortSortBy;}
	
	/**
	 * Set'er methods.
	 * 
	 * @param
	 */
	public void setBegin(      Date       begin)       {m_begin       = begin;      }
	public void setEnd(        Date       end)         {m_end         = end;        }
	public void setUserIds(    List<Long> userIds)     {m_userIds     = userIds;    }
	public void setLongSortBy( String     longSortBy)  {m_longSortBy  = longSortBy; }
	public void setReportType( String     reportType)  {m_reportType  = reportType; }
	public void setShortSortBy(String     shortSortBy) {m_shortSortBy = shortSortBy;}
	
	/**
	 * Returns the command's enumeration value.
	 * 
	 * Implements VibeRpcCmd.getCmdType()
	 * 
	 * @return
	 */
	@Override
	public int getCmdType() {
		return VibeRpcCmdType.CREATE_LOGIN_REPORT.ordinal();
	}
}
