/**
 * Copyright (c) 1998-2015 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2015 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2015 Novell, Inc. All Rights Reserved.
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

import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.kablink.teaming.comparator.StringComparator;
import org.kablink.teaming.domain.MobileDevice;
import org.kablink.teaming.gwt.client.binderviews.folderdata.DescriptionHtml;
import org.kablink.teaming.gwt.client.binderviews.folderdata.FolderColumn;
import org.kablink.teaming.gwt.client.binderviews.folderdata.FolderRow;
import org.kablink.teaming.gwt.client.binderviews.folderdata.GuestInfo;
import org.kablink.teaming.gwt.client.util.AssignmentInfo;
import org.kablink.teaming.gwt.client.util.EmailAddressInfo;
import org.kablink.teaming.gwt.client.util.EntryEventInfo;
import org.kablink.teaming.gwt.client.util.EntryLinkInfo;
import org.kablink.teaming.gwt.client.util.EntryTitleInfo;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.util.LimitedUserVisibilityInfo;
import org.kablink.teaming.gwt.client.util.PrincipalAdminType;
import org.kablink.teaming.gwt.client.util.PrincipalInfo;
import org.kablink.teaming.gwt.client.util.TaskFolderInfo;
import org.kablink.teaming.gwt.client.util.ViewFileInfo;
import org.kablink.teaming.util.NLT;

/**
 * Class used to compare two FolderRow's using a given sort key.
 *
 * @author drfoster@novell.com
 */
public class FolderRowComparator implements Comparator<FolderRow> {
	private boolean 			m_sortDescending;	//
	private FolderColumn		m_sortColumn;		// The column being sorted.
	private List<FolderColumn>	m_folderColumns;	//
	private String				m_sortBy;			//
	private StringComparator	m_sc;				//
	
	// The default sort key to use of the given one doesn't map to a
	// defined column.
	public final static String DEFAULT_SORT_BY	= "_sortTitle";

	/**
	 * Class constructor.
	 * 
	 * @param sortBy
	 * @param sortDescending
	 * @param folderColumns
	 * @param defaultSortBy
	 */
	public FolderRowComparator(String sortBy, boolean sortDescending, List<FolderColumn> folderColumns, String defaultSortBy) {
		// Initialize the super class...
		super();

		// ...store the parameters...
		m_sortBy         = sortBy;
		m_sortDescending = sortDescending;
		m_folderColumns  = folderColumns;

		// ...and initialize the other data members.
		m_sc = new StringComparator(GwtServerHelper.getCurrentUser().getLocale());
		
		// Can we find the FolderColumn for the sort key were were
		// given?
		m_sortColumn = getColumn(m_sortBy);
		if ((null == m_sortColumn) && (!(defaultSortBy.equalsIgnoreCase(m_sortBy)))) {
			// No!  Can we find it for the default sort key?
			m_sortColumn = getColumn(defaultSortBy);
			if (null != m_sortColumn) {
				// Yes!  Use that as the sort key instead of the key we
				// were given.
				m_sortBy = defaultSortBy;
			}
		}
	}
	
	/**
	 * Class constructor.
	 * 
	 * @param sortBy
	 * @param sortDescending
	 * @param folderColumns
	 */
	public FolderRowComparator(String sortBy, boolean sortDescending, List<FolderColumn> folderColumns) {
		// Always use the initial form of the constructor.
		this(sortBy, sortDescending, folderColumns, DEFAULT_SORT_BY);
	}

	/**
	 * Compares two FolderRow's by their sort key.
	 * 
	 * Implements the Comparator.compare() method.
	 * 
	 * @param fr1
	 * @param fr2
	 * 
	 * @return
	 */
	@Override
	public int compare(FolderRow fr1, FolderRow fr2) {
		// Do we have the FolderColumn we're sorting on?
		if (null == m_sortColumn) {
			// No!  Then we can't perform a comparison.  Return EQUAL.
			return 0;
		}

		// Extract appropriate string values from the rows for the sort
		// column...
		String s1 = getColumnValueFromRow(fr1, m_sortColumn);
		String s2 = getColumnValueFromRow(fr2, m_sortColumn);
		
		// ...and compare those.
		int reply = m_sc.compare(s1, s2);
		if (m_sortDescending) {
			reply = (-reply);
		}
		
		// If we get here, reply contains the appropriate value for
		// the compare.  Return it.
		return reply;
	}

	/*
	 * Searches the global folder columns for the one using the sort
	 * key in question.
	 */
	private FolderColumn getColumn(String sortBy) {
		// Do we have a List<FolderColumn> to search?
		if (null != m_folderColumns) {
			// Yes!  Scan the FolderColumn's in the list.
			for (FolderColumn fc:  m_folderColumns) {
				// Does this column use the sort key in question?
				if (sortBy.equals(fc.getColumnSortKey())) {
					// Yes!  Return it.
					return fc;
				}
			}
		}
		
		// If we get here, we couldn't find the sort key in question.
		// Return null.
		return null;
	}
	
	public static String getColumnValueFromRow(FolderRow fr, FolderColumn fc) {
		String reply = null;
		
		// Is this a column that should show a download link for?
		String cName = fc.getColumnEleName();
		if (FolderColumn.isColumnDownload(cName) || FolderColumn.isColumnAdminRights(cName)) {
			// Yes!  Use its string value as the reply.
			reply = fr.getColumnValueAsString(fc);
		}
		
		// No, this column doesn't show a download link!  Does it
		// show presence?
		else if (FolderColumn.isColumnPresence(cName) || FolderColumn.isColumnFullName(cName) || FolderColumn.isColumnDeviceUser(cName)) {
			// Yes!  Return the PresenceInfo's title.
			PrincipalInfo pi = fr.getColumnValueAsPrincipalInfo(fc);
			if (null != pi) {
				reply = pi.getTitle();
			}
		}

		// No, this column doesn't show presence either!  Does it
		// show a rating?
		else if (FolderColumn.isColumnRating(cName)) {
			// Yes!  Return the rating value.
			String value = fr.getColumnValueAsString(fc);
			if (null != value) value = value.trim();
			Integer iValue;
			if (GwtClientHelper.hasString(value)) {
				iValue = Math.round(Float.valueOf(value));
			}
			else iValue = null;
			if (null != iValue) {
				reply = String.valueOf(iValue);
			}
		}
		
		// No, this column doesn't show a rating either!  Does it
		// show an entry title?
		else if (FolderColumn.isColumnTitle(cName) || FolderColumn.isColumnEmailTemplateName(cName)) {
			// Yes!  Return the EntyTitleInfo's title.
			EntryTitleInfo eti = fr.getColumnValueAsEntryTitle(fc);
			if (null != eti) {
				reply = eti.getTitle();
			}
		}
		
		// No, this column doesn't show an entry title either!
		// Does it show a view link?
		else if (FolderColumn.isColumnView(cName)) {
			// Yes!  If it contains a value, return a string (doesn't
			// matter what since all this column ever displays is
			// '[VIEW]'.
			ViewFileInfo vfi = fr.getColumnValueAsViewFile(fc);
			if (null != vfi) {
				reply = "view";
			}
		}
		
		// No, this column doesn't show a view link either!  Is it
		// a custom column?
		else if (FolderColumn.isColumnCustom(fc)) {
			// Yes!  Return an appropriate value for whatever it contains.
			EntryEventInfo eei = fr.getColumnValueAsEntryEvent(fc);
			if (null != eei) {
				reply = eei.getEndDate();
			}
			else {
				EntryLinkInfo eli = fr.getColumnValueAsEntryLink(fc);
				if (null != eli) {
					reply = eli.getText();
				}
				else {
					reply = fr.getColumnValueAsString(fc);
				}
			}
		}
		
		// No, this column doesn't show a custom column either!  Is
		// it an assignment of some sort?
		else if (AssignmentInfo.isColumnAssigneeInfo(cName)){
			// Yes!  Return the title from the first AssignmentInfo in
			// the value.
			List<AssignmentInfo> aiList = fr.getColumnValueAsAssignmentInfos(fc);
			if ((null != aiList) && (!(aiList.isEmpty()))) {
				reply = aiList.get(0).getTitle();
			}
		}
		
		// No, this column doesn't show an assignment either!  Is
		// it a collection of task folders?
		else if (FolderColumn.isColumnTaskFolders(cName)) {
			// Yes!  Return the title from the first TaskFolderInfo in
			// the value.
			List<TaskFolderInfo> tfList = fr.getColumnValueAsTaskFolderInfos(fc);
			if ((null != tfList) && (!(tfList.isEmpty()))) {
				reply = tfList.get(0).getTitle();
			}
		}

		// No, this column isn't a collection of task folders
		// either!  Is it an HTML description column?
		else if (FolderColumn.isColumnDescriptionHtml(cName)) {
			// Yes!  Return the description.
			DescriptionHtml dh = fr.getColumnValueAsDescriptionHtml(fc);
			if (null != dh) {
				reply = dh.getDescription();
			}
		}
		
		// No, this column isn't an HTML description column either!
		// Is it the signer of a guest book?
		else if (FolderColumn.isColumnGuest(cName)) {
			// Yes!  Return the title from the guest info.
			GuestInfo gi = fr.getColumnValueAsGuestInfo(fc);
			if (null != gi) {
				return gi.getTitle();
			}
		}
		
		// No, this column isn't signer of a guest book either!  Is
		// it an email address?
		else if (FolderColumn.isColumnEmailAddress(cName)) {
			// Yes!  Return the email address.
			EmailAddressInfo emi = fr.getColumnValueAsEmailAddress(fc);
			if (null != emi) {
				reply = emi.getEmailAddress();
			}
		}

		// No, this column isn't an email address either!  Is the
		// last login of a mobile device?
		else if (FolderColumn.isColumnDeviceLastLogin(cName)) {
			// Yes!  Do we have the row's MobileDevice?
			MobileDevice md = ((MobileDevice) fr.getServerMobileDevice());
			if (null != md) {
				// Yes!  Does it have a last login date?
				Date lastLogin = md.getLastLogin();
				if (null != lastLogin) {
					// Yes!  Return its MS count as a string.
					reply = String.valueOf(lastLogin.getTime());
				}
			}
		}

		// No, this column isn't a mobile device's last login either!
		// Is it the last wipe date of a mobile device?
		else if (FolderColumn.isColumnDeviceWipeDate(cName)) {
			// Yes!  Do we have the row's MobileDevice?
			MobileDevice md = ((MobileDevice) fr.getServerMobileDevice());
			if (null != md) {
				// Yes!  Does it have a last wipe date?
				Date wipeDate = md.getLastWipe();
				if (null != wipeDate) {
					// Yes!  Return its MS count as a string.
					reply = String.valueOf(wipeDate.getTime());
				}
			}
		}
		
		// No, this column isn't a mobile device's last wipe date of a
		// mobile device either!  Is it the wipe scheduled status of a
		// mobile device?
		else if (FolderColumn.isColumnDeviceWipeScheduled(cName)) {
			// Yes!  Do we have the row's MobileDevice?
			MobileDevice md = ((MobileDevice) fr.getServerMobileDevice());
			if (null != md) {
				// Yes!  Does it have a wipe scheduled flag?
				Boolean wipeScheduled = md.getWipeScheduled();
				if (null != wipeScheduled) {
					// Yes!  Return its appropriate display string.
					String key;
					if (wipeScheduled)
					     key = "general.yes";
					else key = "general.no";
					reply = NLT.get(key);
				}
			}
		}
		
		// No, this column isn't the wipe scheduled status of a
		// mobile device either!  Is it a principal type column?
		else if (FolderColumn.isColumnPrincipalType(cName)) {
			PrincipalAdminType pat = fr.getColumnValueAsPrincipalAdminType(fc);
			reply = ((null == pat) ? "" : pat.getPrincipalType().name());
		}
		
		// No, this column isn't principal type either!  Is it a
		// user visibility limitation column?
		else if (FolderColumn.isColumnCanOnlySeeMembers(cName)) {
			LimitedUserVisibilityInfo luvi = fr.getColumnValueAsLimitedUserVisibility(fc);
			reply = ((null == luvi) ? "" : luvi.getDisplay());
		}
		
		else {
			// No, this column isn't a user visibility limitation
			// column either!  Simply return its string value.
			reply = fr.getColumnValueAsString(fc);
		}

		// If we get here, reply is null of refers to the string value
		// to compare.  Return it.
		return ((null == reply) ? "" : reply);
	}
}
