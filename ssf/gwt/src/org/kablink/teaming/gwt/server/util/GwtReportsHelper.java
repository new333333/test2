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
package org.kablink.teaming.gwt.server.util;

import java.text.Collator;
import java.util.Collections;
import java.util.Comparator;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kablink.teaming.gwt.client.GwtTeamingException;
import org.kablink.teaming.gwt.client.admin.AdminAction;
import org.kablink.teaming.gwt.client.rpc.shared.ReportsInfoRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.ReportsInfoRpcResponseData.ReportInfo;
import org.kablink.teaming.module.admin.AdminModule;
import org.kablink.teaming.module.admin.AdminModule.AdminOperation;
import org.kablink.teaming.util.AllModulesInjected;
import org.kablink.teaming.util.NLT;
import org.kablink.teaming.util.Utils;

/**
 * Helper methods for the GWT reports handling.
 *
 * @author drfoster@novell.com
 */
public class GwtReportsHelper {
	protected static Log m_logger = LogFactory.getLog(GwtReportsHelper.class);

	/*
	 * Inner class used compare two ReportInfo objects.
	 */
	private static class ReportInfoComparator implements Comparator<ReportInfo> {
		private Collator	m_collator;	//
		
		/**
		 * Class constructor.
		 */
		public ReportInfoComparator() {
			m_collator = Collator.getInstance();
			m_collator.setStrength(Collator.IDENTICAL);
		}

	      
		/**
		 * Implements the Comparator.compare() method on two ReportInfo objects.
		 *
		 * Returns:
		 *    -1 if reportInfo1 <  reportInfo2;
		 *     0 if reportInfo1 == reportInfo2; and
		 *     1 if reportInfo1 >  reportInfo2.
		 */
		@Override
		public int compare(ReportInfo reportInfo1, ReportInfo reportInfo2) {
			String s1 = reportInfo1.getTitle();
			if (null == s1) {
				s1 = "";
			}

			String s2 = reportInfo2.getTitle();
			if (null == s2) {
				s2 = "";
			}

			return 	m_collator.compare(s1, s2);
		}
	}

	/*
	 * Class constructor that prevents this class from being
	 * instantiated.
	 */
	private GwtReportsHelper() {
		// Nothing to do.
	}
	
	/**
	 * Returns a ReportsInfoRpcResponseData object containing the
	 * information for running reports.
	 * 
	 * @param bs
	 * @param request
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException
	 */
	public static ReportsInfoRpcResponseData getReportsInfo(AllModulesInjected bs, HttpServletRequest request) throws GwtTeamingException {
		try {
			// Construct the ReportsInfoRpcResponseData object we'll
			// fill in and return.
			ReportsInfoRpcResponseData reply = new ReportsInfoRpcResponseData();
			
			// Add the reports that all administrators have access to.
			reply.addReport(new ReportInfo(AdminAction.REPORT_ACTIVITY_BY_USER,              NLT.get("administration.report.title.activityByUser"))     );
			reply.addReport(new ReportInfo(AdminAction.REPORT_DATA_QUOTA_EXCEEDED,           NLT.get("administration.report.title.disk_quota_exceeded")));
			reply.addReport(new ReportInfo(AdminAction.REPORT_DATA_QUOTA_HIGHWATER_EXCEEDED, NLT.get("administration.report.title.highwater_exceeded")) );
			reply.addReport(new ReportInfo(AdminAction.REPORT_DISK_USAGE,                    NLT.get("administration.report.title.quota"))              );
			reply.addReport(new ReportInfo(AdminAction.REPORT_EMAIL,                         NLT.get("administration.report.title.email"))              );
			reply.addReport(new ReportInfo(AdminAction.REPORT_LICENSE,                       NLT.get("administration.report.title.license"))            );
			reply.addReport(new ReportInfo(AdminAction.REPORT_LOGIN,                         NLT.get("administration.report.title.login"))              );			
			reply.addReport(new ReportInfo(AdminAction.REPORT_USER_ACCESS,                   NLT.get("administration.report.title.user_access"))        );
			reply.addReport(new ReportInfo(AdminAction.REPORT_VIEW_CREDITS,                  NLT.get("administration.credits"))                         );
			reply.addReport(new ReportInfo(AdminAction.REPORT_XSS,                           NLT.get("administration.report.title.xss", "XSS Report"))  );
			
			// Does the user have rights to run 'Content Modification
			// Log Report'?
			AdminModule	am     = bs.getAdminModule();
			boolean		isFilr = Utils.checkIfFilr();
			if ((!isFilr) && am.testAccess( AdminOperation.manageFunction)) {
				// Yes!  Add that.
				reply.addReport(
					new ReportInfo(
						AdminAction.REPORT_VIEW_CHANGELOG,
						NLT.get("administration.view_change_log")));
			}
			
			// Does the user have rights to run the 'System error logs'
			// report?
			if (am.testAccess( AdminOperation.manageErrorLogs)) {
				// Yes!  Add that.
				reply.addReport(
					new ReportInfo(
						AdminAction.REPORT_VIEW_SYSTEM_ERROR_LOG,
						NLT.get("administration.system_error_logs")));
			}

			// Sort the reports we've got by title.
			ReportInfoComparator ric = new ReportInfoComparator();
			Collections.sort(reply.getReports(), ric);
			
			// If we get here, reply refers to the 
			// ReportsInfoRpcResponseData object containing the
			// information about the reports.  Return it.
			return reply;
		}
		catch (Exception ex) {
			// Convert the exception to a GwtTeamingException and throw
			// that.
			if ((!(GwtServerHelper.m_logger.isDebugEnabled())) && m_logger.isDebugEnabled()) {
			     m_logger.debug("GwtReportsHelper.getReportsInfo( SOURCE EXCEPTION ):  ", ex);
			}
			throw GwtServerHelper.getGwtTeamingException(ex);
		}		
	}
}
