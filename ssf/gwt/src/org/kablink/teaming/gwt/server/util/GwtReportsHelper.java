/**
 * Copyright (c) 1998-2014 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2014 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2014 Novell, Inc. All Rights Reserved.
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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.FilterOutputStream;
import java.text.Collator;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.Node;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.kablink.teaming.calendar.TimeZoneHelper;
import org.kablink.teaming.domain.ChangeLog;
import org.kablink.teaming.domain.EntityIdentifier;
import org.kablink.teaming.domain.EntityIdentifier.EntityType;
import org.kablink.teaming.domain.LicenseStats;
import org.kablink.teaming.gwt.client.GwtTeamingException;
import org.kablink.teaming.gwt.client.admin.AdminAction;
import org.kablink.teaming.gwt.client.rpc.shared.ChangeLogReportRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.CreateEmailReportCmd.EmailType;
import org.kablink.teaming.gwt.client.rpc.shared.EmailReportRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.EmailReportRpcResponseData.EmailItem;
import org.kablink.teaming.gwt.client.rpc.shared.LicenseReportRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.LicenseReportRpcResponseData.LicenseItem;
import org.kablink.teaming.gwt.client.rpc.shared.LicenseReportRpcResponseData.LicenseReleaseInfo;
import org.kablink.teaming.gwt.client.rpc.shared.LicenseReportRpcResponseData.LicenseStatsItem;
import org.kablink.teaming.gwt.client.rpc.shared.ReportsInfoRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.ReportsInfoRpcResponseData.ReportInfo;
import org.kablink.teaming.gwt.client.rpc.shared.StringRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.UserAccessReportRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.UserAccessReportRpcResponseData.UserAccessItem;
import org.kablink.teaming.module.admin.AdminModule;
import org.kablink.teaming.module.admin.AdminModule.AdminOperation;
import org.kablink.teaming.module.license.LicenseModule;
import org.kablink.teaming.module.report.ReportModule;
import org.kablink.teaming.portletadapter.AdaptedPortletURL;
import org.kablink.teaming.util.AllModulesInjected;
import org.kablink.teaming.util.NLT;
import org.kablink.teaming.util.ReleaseInfo;
import org.kablink.teaming.util.SpringContextUtil;
import org.kablink.teaming.util.TempFileUtil;
import org.kablink.teaming.util.Utils;
import org.kablink.teaming.web.WebKeys;
import org.kablink.teaming.web.util.MiscUtil;
import org.kablink.teaming.web.util.WebUrlUtil;
import org.springframework.util.FileCopyUtils;

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
			m_collator = Collator.getInstance(GwtServerHelper.getCurrentUser().getLocale());
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
	 * Creates a change log report and returns the data via a
	 * ChangeLogReportRpcResponseData object.
	 * 
	 * @param bs
	 * @param request
	 * @param binderId
	 * @param entityId
	 * @param entityType
	 * @param operation
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException
	 */
	public static ChangeLogReportRpcResponseData createChangeLogReport(AllModulesInjected bs, HttpServletRequest request, Long binderId, Long entityId, String entityType, String operation) throws GwtTeamingException {
		try {
			// Allocate a ChangeLogReportRpcResponseData we can collect
			// change logs in.
			ChangeLogReportRpcResponseData reply = new ChangeLogReportRpcResponseData();

			// What changes are being asked for?
			AdminModule am = bs.getAdminModule();
			List<ChangeLog>	changes = null;
			if ((null != binderId) && (null == entityId)) {
				// Changes for a binder!
				changes = am.getChanges(binderId, operation);
			}
			
			else if (null != entityId) {
				// Changes for an entity!
				if (!(MiscUtil.hasString(entityType))) {
					entityType = EntityType.folderEntry.name();
				}
				EntityIdentifier entityIdentifier = new EntityIdentifier(entityId, EntityType.valueOf(entityType));
				if (null != entityIdentifier) {
					changes = am.getChanges(entityIdentifier, operation);
				}
			}

			// Do we have any changes to return?
			if (MiscUtil.hasItems(changes)) {
				// Yes!  Scan them...
				for (ChangeLog change:  changes) {
					// ...adding their XML to the change log response.
					reply.addChangeLog(change.getXmlNoHeader());
				}
			}
			
			// If we get here, reply refers to the
			// ChangeLogReportRpcResponseData object containing the
			// results of the report.  Return it.
			return reply;
		}
		
		catch (Exception ex) {
			// Convert the exception to a GwtTeamingException and throw
			// that.
			if ((!(GwtLogHelper.isDebugEnabled())) && GwtLogHelper.isDebugEnabled(m_logger)) {
			     GwtLogHelper.debug(m_logger, "GwtReportsHelper.createChangeLogReport( SOURCE EXCEPTION ):  ", ex);
			}
			throw GwtLogHelper.getGwtClientException(ex);
		}		
	}
	
	/**
	 * Creates an email report and returns the results via an
	 * EmailResportRpcResponseData object.
	 * 
	 * @param bs
	 * @param request
	 * @param begin
	 * @param end
	 * @param emailType
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException
	 */
	public static EmailReportRpcResponseData createEmailReport(AllModulesInjected bs, HttpServletRequest request, Date begin, Date end, EmailType emailType) throws GwtTeamingException {
		try {
			// Construct the EmailReportRpcResponseData object we'll
			// fill in and return.
			EmailReportRpcResponseData reply = new EmailReportRpcResponseData();

			// Read the report information.
			String reportType;
			switch (emailType) {
			default:
			case SENT:      reportType = ReportModule.EMAIL_REPORT_TYPE_SEND;    break;
			case ERROR:     reportType = ReportModule.EMAIL_REPORT_TYPE_ERRORS;  break;
			case RECEIVED:  reportType = ReportModule.EMAIL_REPORT_TYPE_RECEIVE; break;
			}
			if (null != end) {
				// Add 1 day to the end date.
				GregorianCalendar cal = new GregorianCalendar();
				cal.setTime(end);
				cal.add(Calendar.DATE, 1);
				end = cal.getTime();
			}

			List<Map<String, Object>> reportList = bs.getReportModule().generateEmailReport(begin, end, reportType);

			// Did we get any items back?
			if (MiscUtil.hasItems(reportList)) {
				// Yes!  Scan them...
				for (Map<String, Object> nextItem:  reportList) {
					// ...creating an EmailItem for each...
					EmailItem ei = new EmailItem();
					ei.setAttachedFiles(getStringFromEmailReportMap(nextItem, ReportModule.EMAIL_LOG_ATTACHED_FILES));
					ei.setFrom(         getStringFromEmailReportMap(nextItem, ReportModule.EMAIL_LOG_FROM_ADDRESS  ));
					ei.setComment(      getStringFromEmailReportMap(nextItem, ReportModule.EMAIL_LOG_COMMENT       ));
					ei.setLogStatus(    getStringFromEmailReportMap(nextItem, ReportModule.EMAIL_LOG_STATUS        ));
					ei.setLogType(      getStringFromEmailReportMap(nextItem, ReportModule.EMAIL_LOG_TYPE          ));
					ei.setSendDate(     getStringFromEmailReportMap(nextItem, ReportModule.EMAIL_LOG_SEND_DATE     ));
					ei.setSubject(      getStringFromEmailReportMap(nextItem, ReportModule.EMAIL_LOG_SUBJECT       ));
					ei.setToAddresses(  getStringFromEmailReportMap(nextItem, ReportModule.EMAIL_LOG_TO_ADDRESSES  ));
					
					// ...that gets added to the response data.
					reply.addEmailItem(ei);
				}
			}
			
			// If we get here, reply refers to the 
			// EmailReportRpcResponseData object containing the results
			// of the report.  Return it.
			return reply;
		}
		
		catch (Exception ex) {
			// Convert the exception to a GwtTeamingException and throw
			// that.
			if ((!(GwtLogHelper.isDebugEnabled())) && GwtLogHelper.isDebugEnabled(m_logger)) {
			     GwtLogHelper.debug(m_logger, "GwtReportsHelper.createEmailReport( SOURCE EXCEPTION ):  ", ex);
			}
			throw GwtLogHelper.getGwtClientException(ex);
		}		
	}
	
	/**
	 * Creates a license report and returns the results via a
	 * LicenseReportRpcResponseData object.
	 * 
	 * The logic for this method was copied from
	 * LicenseReportController.populateModel().
	 * 
	 * @param bs
	 * @param request
	 * @param begin
	 * @param end
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException
	 */
	public static LicenseReportRpcResponseData createLicenseReport(AllModulesInjected bs, HttpServletRequest request, Date begin, Date end) throws GwtTeamingException {
		try {
			// Construct the LicenseReportRpcResponseData object we'll
			// fill in and return.
			Date currentDate = new Date();
			LicenseReportRpcResponseData reply = new LicenseReportRpcResponseData();
			reply.setBeginDate(      GwtServerHelper.getDateString(    begin)                                           );
			reply.setEndDate(        GwtServerHelper.getDateString(    end)                                             );
			reply.setReportDate(     GwtServerHelper.getDateTimeString(currentDate, DateFormat.MEDIUM, DateFormat.SHORT));

			Date originalEndDate = null;
			if (null != end) {
				GregorianCalendar cal = new GregorianCalendar();
				cal.setTime(end);
				
				// Remember the original end date supplied by the user.
				originalEndDate = cal.getTime();
				
				// Add 1 day to the end date.
				cal.add(Calendar.DATE, 1);
				end = cal.getTime();
			}
			
			// There is a job that runs once a day that counts the
			// number of users in Filr/Vibe and writes this information
			// into the SS_LicenseStats table.  If the end date of the
			// report equals today, do a count right now and write the
			// count into the SS_LicenseStats table so the information
			// returned to the user is up-to-date.
			//
			// Is the end date of the report equal to today?
			LicenseModule lm = bs.getLicenseModule();
			if ((null != originalEndDate) && (null != currentDate)) {
				Calendar			todayCal = GregorianCalendar.getInstance();
				GregorianCalendar	endCal   = new GregorianCalendar();
				endCal.setTime(originalEndDate);
				if (endCal.get(Calendar.MONTH) == todayCal.get(Calendar.MONTH)) {
					if (endCal.get(Calendar.DAY_OF_MONTH) == todayCal.get(Calendar.DAY_OF_MONTH)) {
						if (endCal.get(Calendar.YEAR) == todayCal.get(Calendar.YEAR)) {
							// If we get here then the end date of the
							// report is today.  Count the users in
							// Filr/Vibe so the data we return is up-to
							// date.
							lm.recordCurrentUsage();
						}
					}
				}
			}

			// Are there any license statistics to report on?
			ReportModule 		rm           = bs.getReportModule();
			List<LicenseStats>	licenseStats = rm.generateLicenseReport(begin, end);
			if (MiscUtil.hasItems(licenseStats)) {
				// Yes!  Scan them...
				for (LicenseStats ls:  licenseStats) {
					// ...adding a LicenseStatsItem to the reply for
					// ...each.
					LicenseStatsItem lsi = new LicenseStatsItem();
					
					lsi.setId(               ls.getId()                                         );
				    lsi.setSnapshotDate(     GwtServerHelper.getDateString(ls.getSnapshotDate()));
					lsi.setInternalUserCount(ls.getInternalUserCount()                          );	//Local internal
					lsi.setExternalUserCount(ls.getExternalUserCount()                          );	//LDAP synced
					lsi.setOpenIdUserCount(  ls.getOpenIdUserCount()                            );	//OpenId users
					lsi.setOtherExtUserCount(  ls.getOtherExtUserCount()              			);	//Self-registered users
					lsi.setGuestAccessEnabled(  ls.getGuestAccessEnabled()              		);	//Guest Access Enabled
					lsi.setActiveUserCount(  ls.getActiveUserCount()                            );
					lsi.setCheckSum(         ls.getChecksum()                                   );
					
					reply.addLicenseStats(lsi);
				}
			}

			// Scan the installed licenses...
			StringBuffer		uids   = new StringBuffer();
			TimeZone			gmt    = TimeZoneHelper.getTimeZone("GMT");
			DateTimeFormatter	dtf    = DateTimeFormat.forPattern("yyyy-MM-dd");
			dtf = dtf.withOffsetParsed();
			for(Document doc:  lm.getLicenses()) {
				// ...adding a LicenseItem to the reply for each.
				LicenseItem li = new LicenseItem();

				// Track this license's UID.
				uids.append(getLicenseValue(doc, "//KeyInfo/@uid") + " ");
				
				// Get the date issued from the license XML and convert
				// it to a Date object.
				Date	tmpIssuedDate    = null;
				Date	tmpEffectiveDate    = null;
				Date	tmpExpirationDate   = null;
				String	tmpIssuedDateStr = getLicenseValue(doc, "//KeyInfo/@issued");
				String	tmpEffectiveDateStr = getLicenseValue(doc, "//KeyInfo/@issued");
				try {
					tmpIssuedDate = dtf.parseDateTime(tmpIssuedDateStr).toDate();
				}
				catch (Exception ex) {
					// This should never happen.
				}
				if (null == tmpIssuedDate) {
					// This should never happen.
					tmpIssuedDate = new Date();
				}
				li.setIssued(GwtServerHelper.getDateString(tmpIssuedDate, DateFormat.MEDIUM, gmt));
				
				// Get the license start date.
				dtf     = DateTimeFormat.forPattern("MM/dd/yyyy");
				dtf     = dtf.withOffsetParsed();
				tmpEffectiveDate = null;
				tmpEffectiveDateStr = getLicenseValue(doc, "//Dates/@effective");
				try {
					if (tmpEffectiveDateStr.equalsIgnoreCase("trial")) {
						@SuppressWarnings("unused")
						String days = getLicenseValue(doc, "//Dates/@expiration");
						int daysSinceInstallation = GwtServerHelper.getDaysSinceInstallation();
						Calendar c = Calendar.getInstance();
						c.add(Calendar.DAY_OF_MONTH, -daysSinceInstallation);
						tmpEffectiveDate = c.getTime();
					} else {
						tmpEffectiveDate = dtf.parseDateTime(tmpEffectiveDateStr).toDate();
					}
				}
				catch (Exception ex) {
					// This should never happen.
				}
				if (null == tmpEffectiveDate) {
					// This should never happen.
					tmpEffectiveDate = new Date();
				}
				li.setEffectiveStart(GwtServerHelper.getDateString(tmpEffectiveDate, DateFormat.MEDIUM, gmt));

				// Get the license end date.
				String tmpExpirationDateStr = getLicenseValue(doc, "//Dates/@expiration");
				try {
					if (tmpEffectiveDateStr.equalsIgnoreCase("trial")) {
						//For trial licenses, the expiration value is a number of days
						Calendar c = Calendar.getInstance();
						c.setTime(tmpEffectiveDate);
						c.add(Calendar.DAY_OF_MONTH, Integer.valueOf(tmpExpirationDateStr));
						tmpExpirationDate = c.getTime();
					} else {
						tmpExpirationDate = dtf.parseDateTime(tmpExpirationDateStr).toDate();
					}
				}
				catch (Exception ex) {
					// This should never happen.
				}
				if (null == tmpExpirationDate) {
					// This should never happen.
					tmpExpirationDate = new Date();
				}
				li.setEffectiveEnd(GwtServerHelper.getDateString(tmpExpirationDate, DateFormat.MEDIUM, gmt));

				li.setContact(getLicenseValue(       doc, "//AuditPolicy/ReportContact"));
				li.setProductTitle(getLicenseValue(  doc, "//Product/@title")           );
				li.setProductVersion(getLicenseValue(doc, "//Product/@version")         );
				
				reply.addLicense(li);
			}

			// Store the remaining data in the reply.
			LicenseReleaseInfo lri = new LicenseReleaseInfo();
			lri.setName(       ReleaseInfo.getName()       );
			lri.setReleaseInfo(ReleaseInfo.getReleaseInfo());
			lri.setVersion(    ReleaseInfo.getVersion()    );
			
			reply.setReleaseInfo(    lri                    );
			reply.setLicenseKey(     uids.toString()        );
			reply.setRegisteredUsers(lm.getRegisteredUsers());
			reply.setExternalUsers(  lm.getExternalUsers()  );			
			
			// If we get here, reply refers to the 
			// LicenseReportRpcResponseData object containing the results
			// of the report.  Return it.
			return reply;
		}
		
		catch (Exception ex) {
			// Convert the exception to a GwtTeamingException and throw
			// that.
			if ((!(GwtLogHelper.isDebugEnabled())) && GwtLogHelper.isDebugEnabled(m_logger)) {
			     GwtLogHelper.debug(m_logger, "GwtReportsHelper.createLicenseReport( SOURCE EXCEPTION ):  ", ex);
			}
			throw GwtLogHelper.getGwtClientException(ex);
		}		
	}
	
	/**
	 * Creates a login report and returns a URL to the results via a
	 * StringRpcResponseData object.
	 * 
	 * @param bs
	 * @param request
	 * @param begin
	 * @param end
	 * @param userIds
	 * @param reportType
	 * @param longSortBy
	 * @param shortSortBy
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException
	 */
	public static StringRpcResponseData createLoginReport(AllModulesInjected bs, HttpServletRequest request, Date begin, Date end, List<Long> userIds, String reportType, String longSortBy, String shortSortBy) throws GwtTeamingException {
		try {
			// Convert the List<Long> of user IDs to string form for the
			// URL...
			String users;
			if (MiscUtil.hasItems(userIds)) {
				StringBuffer ub = new StringBuffer("");
				for (Long uid:  userIds) {
					ub.append(" " + String.valueOf(uid) + " ");
				}
				users = ub.toString();
			}
			else {
				users = "";
			}
			
			// ...construct the URL...
			SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
			String url = (
				WebUrlUtil.getServletRootURL(request)                                     +
				WebKeys.SERVLET_DOWNLOAD_REPORT                                           + "?" +
				WebKeys.URL_REPORT_TYPE         + "=login"                                + "&" +
				WebKeys.URL_REPORT_OPTION_TYPE  + "=" + reportType                        + "&" +
				WebKeys.URL_REPORT_SORT_TYPE    + "=" + shortSortBy                       + "&" +
				WebKeys.URL_REPORT_SORT_TYPE_2  + "=" + longSortBy                        + "&" +
				WebKeys.URL_START_DATE_YYYYMMDD + "=" + formatter.format(begin.getTime()) + "&" +
				WebKeys.URL_END_DATE_YYYYMMDD   + "=" + formatter.format(end.getTime())   + "&" +
				"users=" + users);
			
			// ...and construct a StringRpcResponseData object
			// ...containing the URL.
			StringRpcResponseData reply = new StringRpcResponseData(url);
			
			// If we get here, reply refers to the
			// StringRpcResponseData object containing the results of
			// the report.  Return it.
			return reply;
		}
		
		catch (Exception ex) {
			// Convert the exception to a GwtTeamingException and throw
			// that.
			if ((!(GwtLogHelper.isDebugEnabled())) && GwtLogHelper.isDebugEnabled(m_logger)) {
			     GwtLogHelper.debug(m_logger, "GwtReportsHelper.createLoginReport( SOURCE EXCEPTION ):  ", ex);
			}
			throw GwtLogHelper.getGwtClientException(ex);
		}		
	}
	
	/**
	 * Creates a user access report and returns the data via a
	 * UserAccessReportRpcResponseData object.
	 * 
	 * @param bs
	 * @param request
	 * @param userId
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException
	 */
	public static UserAccessReportRpcResponseData createUserAccessReport(AllModulesInjected bs, HttpServletRequest request, Long userId) throws GwtTeamingException {
		try {
			// Create the base URL for modifying an entity's ACLs...
			AdaptedPortletURL modifyACLsUrl = new AdaptedPortletURL(request, "ss_forum", true);
			modifyACLsUrl.setParameter(WebKeys.ACTION, WebKeys.ACTION_ACCESS_CONTROL);
			
			// ...and use it to construct a
			// ...UserAccessReportRpcResponseData object to return.
			UserAccessReportRpcResponseData reply = new UserAccessReportRpcResponseData(modifyACLsUrl.toString());
			
			// Does the given user have access to any items?
			List<Map<String, Object>> report = bs.getReportModule().generateAccessReportByUser(userId, null, null, "summary");
			if (MiscUtil.hasItems(report)) {
				// Yes!  Scan them...
				for (Map<String, Object> item:  report) {
					// ...and add a UserAccessItem to the reply object
					// ...for each.
					reply.addUserAccessItem(new UserAccessItem(
						((String) item.get(ReportModule.ENTITY_PATH)),
						((String) item.get(ReportModule.ENTITY_TYPE)),
						((Long)   item.get(ReportModule.BINDER_ID))));
				}
			}
	        
			// If we get here, reply refers to the
			// UserAccessReportRpcResponseData object containing the
			// results of the report.  Return it.
			return reply;
		}
		
		catch (Exception ex) {
			// Convert the exception to a GwtTeamingException and throw
			// that.
			if ((!(GwtLogHelper.isDebugEnabled())) && GwtLogHelper.isDebugEnabled(m_logger)) {
			     GwtLogHelper.debug(m_logger, "GwtReportsHelper.createUserAccessReport( SOURCE EXCEPTION ):  ", ex);
			}
			throw GwtLogHelper.getGwtClientException(ex);
		}		
	}
	
	/**
	 * Creates a user activity report and returns a URL to the results
	 * via a StringRpcResponseData object.
	 * 
	 * @param bs
	 * @param request
	 * @param begin
	 * @param end
	 * @param userIds
	 * @param reportType
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException
	 */
	public static StringRpcResponseData createUserActivityReport(AllModulesInjected bs, HttpServletRequest request, Date begin, Date end, List<Long> userIds, String reportType) throws GwtTeamingException {
		try {
			// Convert the List<Long> of user IDs to string form for the
			// URL...
			String users;
			if (MiscUtil.hasItems(userIds)) {
				StringBuffer ub = new StringBuffer("");
				for (Long uid:  userIds) {
					ub.append(" " + String.valueOf(uid) + " ");
				}
				users = ub.toString();
			}
			else {
				users = "";
			}
			
			// ...construct the URL...
			SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
			String url = (
				WebUrlUtil.getServletRootURL(request)                                     +
				WebKeys.SERVLET_DOWNLOAD_REPORT                                           + "?" +
				WebKeys.URL_REPORT_TYPE         + "=activityByUser"                       + "&" +
				WebKeys.URL_REPORT_FLAVOR       + "=" + reportType                        + "&" +
				WebKeys.URL_START_DATE_YYYYMMDD + "=" + formatter.format(begin.getTime()) + "&" +
				WebKeys.URL_END_DATE_YYYYMMDD   + "=" + formatter.format(end.getTime())   + "&" +
				"users=" + users);
			
			// ...and construct a StringRpcResponseData object
			// ...containing the URL.
			StringRpcResponseData reply = new StringRpcResponseData(url);
			
			// If we get here, reply refers to the
			// StringRpcResponseData object containing the results of
			// the report.  Return it.
			return reply;
		}
		
		catch (Exception ex) {
			// Convert the exception to a GwtTeamingException and throw
			// that.
			if ((!(GwtLogHelper.isDebugEnabled())) && GwtLogHelper.isDebugEnabled(m_logger)) {
			     GwtLogHelper.debug(m_logger, "GwtReportsHelper.createUserActivityReport( SOURCE EXCEPTION ):  ", ex);
			}
			throw GwtLogHelper.getGwtClientException(ex);
		}		
	}
	
	/*
	 * Copied from LicenseReportController.getValue().
	 */
	private static String getLicenseValue(Document doc, String xpath) {
		Node node = null;
		return ((doc != null && (node = doc.selectSingleNode(xpath)) != null) ? node.getText() : "");
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
			if ((!(GwtLogHelper.isDebugEnabled())) && GwtLogHelper.isDebugEnabled(m_logger)) {
			     GwtLogHelper.debug(m_logger, "GwtReportsHelper.getReportsInfo( SOURCE EXCEPTION ):  ", ex);
			}
			throw GwtLogHelper.getGwtClientException(ex);
		}		
	}

	/*
	 * Extracts a non-null string from an email report map.
	 */
	private static String getStringFromEmailReportMap(Map<String, Object> reportMap, String key) {
		String reply = ((String) reportMap.get(key));
		if (reply == null) {
			reply = "";
		}
		return reply;
	}
	
	/**
	 * Returns a URL to download the system error log.
	 * 
	 * @param bs
	 * @param request
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException
	 */
	public static String getSystemErrorLogUrl(AllModulesInjected bs, HttpServletRequest request) throws GwtTeamingException {
		FileOutputStream	fo      = null;
		ZipOutputStream		zipOut  = null;
		FilterOutputStream	wrapper = null;
		
		try {
			// Generate a ZIP file containing the system error logs...
			File tempFile = TempFileUtil.createTempFile("logfiles");
			fo       = new FileOutputStream(tempFile);
			zipOut   = new ZipOutputStream(fo);
			wrapper  = new FilterOutputStream(zipOut) {
				@Override
				public void close() {}  // FileCopyUtils will try to close this too soon
			};
			File logDirectory = new File(SpringContextUtil.getServletContext().getRealPath("/../../logs"));
			for (String logFile:  logDirectory.list(
					new FilenameFilter() {
						@Override
						public boolean accept(File file, String filename) {
							return filename.startsWith("appserver.log");
						}
					})) {
				zipOut.putNextEntry(new ZipEntry(logFile));
				FileCopyUtils.copy(new FileInputStream(new File(logDirectory, logFile)), wrapper);
			}
			zipOut.finish();

			// ...and return a link to it.  
			return (
				WebUrlUtil.getServletRootURL(request)          +
				WebKeys.SERVLET_VIEW_FILE                      + "?" +
				"viewType=zipped&fileId=" + tempFile.getName() + "&" +
				WebKeys.URL_FILE_TITLE    + "=logfiles.zip");
		}
		
		catch (Exception ex) {
			// Convert the exception to a GwtTeamingException and throw
			// that.
			if ((!(GwtLogHelper.isDebugEnabled())) && GwtLogHelper.isDebugEnabled(m_logger)) {
			     GwtLogHelper.debug(m_logger, "GwtReportsHelper.getSystemErrorLogUrl( SOURCE EXCEPTION ):  ", ex);
			}
			throw GwtLogHelper.getGwtClientException(ex);
		}
		
		finally {
			// Ensure we've closed the streams.
			if (null != wrapper) {
				try {wrapper.close();}
				catch (Exception e) {}
				wrapper = null;
			}
			if (null != zipOut) {
				try {zipOut.close();}
				catch (Exception e) {}
				zipOut = null;
			}
			if (null != fo) {
				try {fo.close();}
				catch (Exception e) {}
				fo = null;
			}
		}
	}
}
