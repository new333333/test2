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

import java.util.Date;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * This class holds all of the information necessary to execute the
 * 'create email report' command.
 * 
 * @author drfoster@novell.com
 */
public class CreateEmailReportCmd extends VibeRpcCmd {
	private Date		m_begin;		//
	private Date		m_end;			//
	private EmailType	m_emailType;	//
	
	/**
	 * Enumeration value used to represent the class of emails to be
	 * reported on.
	 */
	public enum EmailType implements IsSerializable {
		ERROR,
		RECEIVED,
		SENT,
	}
	
	/**
	 * Constructor method.
	 * 
	 * For GWT serialization, must have a zero parameter constructor.
	 */
	public CreateEmailReportCmd() {
		// Initialize the super class.
		super();
	}
	
	/**
	 * Constructor method.
	 * 
	 * @param begin
	 * @param end
	 * @param emailType
	 */
	public CreateEmailReportCmd(Date begin, Date end, EmailType emailType) {
		// Initialize this object...
		super();
		
		// ...and store the parameters.
		setBegin(    begin    );
		setEnd(      end      );
		setEmailType(emailType);
	}
	
	/**
	 * Get'er methods.
	 * 
	 * @return
	 */
	public Date      getBegin()     {return m_begin;    }
	public Date      getEnd()       {return m_end;      }
	public EmailType getEmailType() {return m_emailType;}
	
	/**
	 * Set'er methods.
	 * 
	 * @param
	 */
	public void setBegin(    Date      begin)     {m_begin     = begin;    }
	public void setEnd(      Date      end)       {m_end       = end;      }
	public void setEmailType(EmailType emailType) {m_emailType = emailType;}
	
	/**
	 * Returns the command's enumeration value.
	 * 
	 * Implements VibeRpcCmd.getCmdType()
	 * 
	 * @return
	 */
	@Override
	public int getCmdType() {
		return VibeRpcCmdType.CREATE_EMAIL_REPORT.ordinal();
	}
}
