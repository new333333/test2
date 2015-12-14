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

import org.kablink.teaming.gwt.client.admin.AdminAction;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * This class holds the response data for the RPCs that return
 * information about running reports.
 * 
 * @author drfoster@novell.com
 */
public class ReportsInfoRpcResponseData implements IsSerializable, VibeRpcResponseData {
	private List<ReportInfo> m_reports;	//
	
	/**
	 * Inner class used to track an instance of a report.
	 */
	public static class ReportInfo implements IsSerializable {
		private AdminAction m_report;	//
		private String		m_title;	//
		
		/**
		 * Constructor method. 
		 * 
		 * For GWT serialization, must have a zero parameter constructor.
		 */
		public ReportInfo() {
			// Initialize the super class.
			super();
		}
		
		/**
		 * Constructor method. 
		 * 
		 * @param report
		 * @param title
		 */
		public ReportInfo(AdminAction report, String title) {
			// Initialize this object...
			this();

			// ...and store the parameters.
			setReport(report);
			setTitle( title );
		}
		
		/**
		 * Get'er methods.
		 * 
		 * @return
		 */
		public AdminAction getReport() {return m_report;}
		public String      getTitle()  {return m_title; }
		
		/**
		 * Set'er methods.
		 * 
		 * @param
		 */
		public void setReport(AdminAction report) {m_report = report;}
		public void setTitle( String      title)  {m_title  = title; }
	}
	
	/**
	 * Constructor method. 
	 * 
	 * For GWT serialization, must have a zero parameter constructor.
	 */
	public ReportsInfoRpcResponseData() {
		// Initialize the super class...
		super();
		
		// ...and initialize anything else that requires it.
		m_reports = new ArrayList<ReportInfo>();
	}
	
	/**
	 * Get'er methods.
	 * 
	 * @return
	 */
	public List<ReportInfo> getReports() {return m_reports;}
	
	/**
	 * Set'er methods.
	 * 
	 * @param
	 */
	public void setReports(List<ReportInfo> reports) {m_reports = reports;}

	/**
	 * Adds a ReportInfo to the List<ReportInfo> of them.
	 * 
	 * @param report
	 */
	public void addReport(ReportInfo report) {
		m_reports.add(report);
	}
}
