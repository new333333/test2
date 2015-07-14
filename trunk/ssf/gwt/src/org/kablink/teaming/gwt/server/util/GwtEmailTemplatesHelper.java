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

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.gwt.client.GwtTeamingException;
import org.kablink.teaming.gwt.client.binderviews.folderdata.FolderColumn;
import org.kablink.teaming.gwt.client.binderviews.folderdata.FolderRow;
import org.kablink.teaming.gwt.client.rpc.shared.DeleteCustomizedEmailTemplatesRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.FolderRowsRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.ManageEmailTemplatesInfoRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.ErrorListRpcResponseData.ErrorInfo;
import org.kablink.teaming.gwt.client.rpc.shared.FolderRowsRpcResponseData.TotalCountType;
import org.kablink.teaming.gwt.client.util.BinderInfo;
import org.kablink.teaming.gwt.client.util.EntityId;
import org.kablink.teaming.gwt.client.util.EntryTitleInfo;
import org.kablink.teaming.gwt.client.util.WorkspaceType;
import org.kablink.teaming.util.AllModulesInjected;
import org.kablink.teaming.util.EmailTemplatesHelper;
import org.kablink.teaming.util.NLT;
import org.kablink.teaming.web.WebKeys;
import org.kablink.teaming.web.util.MiscUtil;
import org.kablink.teaming.web.util.WebUrlUtil;

/**
 * Helper methods for the GWT UI server code in dealing with email
 * templates.
 *
 * @author drfoster@novell.com
 */
public class GwtEmailTemplatesHelper {
	protected static Log m_logger = LogFactory.getLog(GwtEmailTemplatesHelper.class);
	
	/*
	 * Inhibits this class from being instantiated. 
	 */
	private GwtEmailTemplatesHelper() {
		// Nothing to do.
	}
	
	/**
	 * Deletes the specified customized email templates.
	 *
	 * @param bs
	 * @param request
	 * @param entityIds
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException
	 */
	public static DeleteCustomizedEmailTemplatesRpcResponseData deleteCustomizedEmailTemplates(AllModulesInjected bs, HttpServletRequest request, List<EntityId> entityIds) throws GwtTeamingException {
		DeleteCustomizedEmailTemplatesRpcResponseData reply = new DeleteCustomizedEmailTemplatesRpcResponseData(new ArrayList<ErrorInfo>());
		deleteCustomizedEmailTemplatesImpl(bs, request, entityIds, reply);
		return reply;
	}

	@SuppressWarnings("unused")
	private static void deleteCustomizedEmailTemplatesImpl(AllModulesInjected bs, HttpServletRequest request, List<EntityId> entityIds, DeleteCustomizedEmailTemplatesRpcResponseData reply) throws GwtTeamingException {
		GwtServerProfiler gsp = GwtServerProfiler.start(m_logger, "GwtEmailTemplatesHelper.deleteCustomizedEmailTemplatesImpl()");
		try {
			// Were we given any proxy identities to delete?
			if (MiscUtil.hasItems(entityIds)) {
				// Yes!  Scan them.
				List<EntityId> successfulDeletes = new ArrayList<EntityId>();
				for (EntityId eid:  entityIds) {
//!					...this needs to be implemented...
				}
				reply.setSuccessfulDeletes(successfulDeletes);
			}
		}
		
		catch (Exception ex) {
			throw GwtLogHelper.getGwtClientException(
				m_logger,
				ex,
				"GwtEmailTemplatesHelper.deleteCustomizedEmailTemplatesImpl( SOURCE EXCEPTION ):  ");
		}
		
		finally {
			gsp.stop();
		}
	}

	/*
	 * Iterates through the contents of an email templates directory,
	 * adding a FolderRow to the List<FolderRow> for each email
	 * template found.
	 */
	private static void enumerateEmailTemplateRows(HttpServletRequest request, List<FolderRow> rows, File emailTemplatesDir, List<FolderColumn> folderColumns, boolean defaultEmailTemplates) {
		// If we don't have a directory to enumerate through...
		if ((null == emailTemplatesDir) || (!(emailTemplatesDir.exists())) || (!(emailTemplatesDir.isDirectory()))) {
			// ...bail.
			return;
		}

		// List the email template files from the directory.
		File[] emailTemplates = emailTemplatesDir.listFiles(new FileFilter() {
			@Override
			public boolean accept(File f) {
				// Is this a file that really exists? 
				if (f.exists() && f.isFile()) {
					// Yes!  Is it an email template file?
					String fName = f.getName();
					if (MiscUtil.hasString(fName) && fName.endsWith(EmailTemplatesHelper.TEMPLATE_EXTENSION)) {
						// Yes!  Return true so that we include it.
						return true;
					}
				}

				// If we get here, this isn't a file that we care
				// about.  Return false so that we skip it.
				return false;
			}
		});
		
		// If we didn't find any email templates in that directory...
		if ((null == emailTemplates) || (0 == emailTemplates.length)) {
			// ...bail.
			return;
		}

		// Find need the email template name FolderColumn for use
		// within the row generation loop.
		FolderColumn fcName = null;
		for (FolderColumn fc:  folderColumns) {
			if (FolderColumn.isColumnEmailTemplateName(FolderColumn.COLUMN_EMAIL_TEMPLATE_NAME)) {
				fcName = fc;
				break;
			}
		}
		
		// What's the type of these email templates.
		String templateType = (defaultEmailTemplates ? "emailTemplates.type.default" : "emailTemplates.type.custom");
		templateType = NLT.get(templateType);

		// Scan the email template files.  At this point, we know that
		// these are all email template files that actually exist.
		for (File emailTemplate:  emailTemplates) {
			// Find/create the FolderRow for this email template.
			FolderRow row;
			String    fileName = emailTemplate.getName();
			if (defaultEmailTemplates || (null == fcName))
			     row = null;
			else row = findRowByFileName(rows, fcName, fileName);
			if (null == row) {
				row = new FolderRow();
				row.setEntityId(new EntityId(EntityId.EMAIL_TEMPLATE, fileName));
				rows.add(row);
			}
			
			// ...and populate it.
			for (FolderColumn fc:  folderColumns) {
				String cn = fc.getColumnName();
				if (FolderColumn.isColumnEmailTemplateName(cn)) {
					EntryTitleInfo etInfo = new EntryTitleInfo();
					etInfo.setTitle(fileName);
					etInfo.setFileDownloadUrl(WebUrlUtil.getFileEmailTemplateUrl(request, WebKeys.ACTION_READ_FILE, fileName, defaultEmailTemplates));
					row.setColumnValue(fc, etInfo);
				}
				
				else if (FolderColumn.isColumnEmailTemplateType(cn)) {
					row.setColumnValue(fc, templateType);
				}
			}
		}
	}

	/**
	 * Returns the FolderRow from a List<FolderRow> whose filename is
	 * fileName.
	 * 
	 * @param rows
	 * @param fcName
	 * @param fileName
	 * 
	 * @return
	 */
	private static FolderRow findRowByFileName(List<FolderRow> rows, FolderColumn fcName, String fileName) {
		// If we don't have any rows...
		if (!(MiscUtil.hasItems(rows))) {
			// ...it can't be found.
			return null;
		}

		// Scan the rows.
		for (FolderRow row:  rows) {
			// Is this the requested row? 
			EntryTitleInfo etInfo = row.getColumnValueAsEntryTitle(fcName);
			if (etInfo.getTitle().equals(fileName)) {
				// Yes!  Return it.
				return row;
			}
		}

		// If we get here, we couldn't find a row with the requested
		// filename.  Return null.
		return null;
	}
	
	/**
	 * Returns the rows for the email templates view.
	 * 
	 * @param bs
	 * @param request
	 * @param binder
	 * @param quickFilter
	 * @param options
	 * @param folderColumns
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static FolderRowsRpcResponseData getEmailTemplatesRows(AllModulesInjected bs, HttpServletRequest request, Binder binder, String quickFilter, Map options, BinderInfo bi, List<FolderColumn> folderColumns) {
		GwtServerProfiler gsp = GwtServerProfiler.start(m_logger, "GwtEmailTemplatesHelper.getProxyIdentityRows()");
		try {
			// Allocate a List<FolderRow> to hold the rows of email
			// templates we'll return.
			List<FolderRow> rows = new ArrayList<FolderRow>();
			
			// Add rows for the default email templates.
			File emailTemplatesDir = EmailTemplatesHelper.getEmailTemplatesDefault();
			enumerateEmailTemplateRows(request, rows, emailTemplatesDir, folderColumns, true);	// true -> These are the default email templates.
			
			// Add rows for the customized email templates.
			emailTemplatesDir = EmailTemplatesHelper.getEmailTemplatesCustomized();
			enumerateEmailTemplateRows(request, rows, emailTemplatesDir, folderColumns, false);	// false -> These are the customized email templates.

			// Is there more than one row?
			int totalRecords = rows.size();
			if (1 < totalRecords) {
				// Yes!  Then we need to sort them using the user's
				// current sort criteria.
				String sortBy       = ((String)  options.get(ObjectKeys.SEARCH_SORT_BY)     );
				boolean sortDescend = ((boolean) options.get(ObjectKeys.SEARCH_SORT_DESCEND));
				Comparator<FolderRow> comparator =
					new FolderRowComparator(
						sortBy,
						sortDescend,
						folderColumns);
				
				Collections.sort(rows, comparator);
			}
			
			// Finally, return the List<FolderRow> wrapped in a
			// FolderRowsRpcResponseData.
			FolderRowsRpcResponseData reply =
				new FolderRowsRpcResponseData(
					rows,
					0,
					totalRecords,
					TotalCountType.EXACT,
					new ArrayList<Long>());
			
			// If we get here, reply refers to a
			// FolderRowsRpcResponseData containing the rows from the
			// email templates.  Return it.
			if (GwtLogHelper.isDebugEnabled(m_logger)) {
				GwtViewHelper.dumpFolderRowsRpcResponseData(m_logger, binder, reply);
			}
			
			return reply;
		}
		
		finally {
			gsp.stop();
		}
	}

	/**
	 * Returns a ManageEmailTemplatesInfoRpcResponseData object
	 * containing the information for managing email templates.
	 * 
	 * @param bs
	 * @param request
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException
	 */
	public static ManageEmailTemplatesInfoRpcResponseData getManageEmailTemplatesInfo(AllModulesInjected bs, HttpServletRequest request) throws GwtTeamingException {
		try {
			// Construct the ManageDevicesInfoRpcResponseData
			// object we'll fill in and return.
			BinderInfo bi = GwtServerHelper.getBinderInfo(bs, request, bs.getWorkspaceModule().getTopWorkspaceId());
			if ((!(bi.getWorkspaceType().isTopWS())) && (!(bi.getWorkspaceType().isLandingPage()))) {
				GwtLogHelper.error(m_logger, "GwtEmailTemplatesHelper.getManageEmailTemplatesInformation():  The workspace type of the top workspace was incorrect.  Found:  " + bi.getWorkspaceType().name() + ", Expected:  " + WorkspaceType.TOP.name());
			}
			bi.setWorkspaceType(WorkspaceType.EMAIL_TEMPLATES);
			ManageEmailTemplatesInfoRpcResponseData reply = new ManageEmailTemplatesInfoRpcResponseData(bi);

			// If we get here, reply refers to the
			// ManageEmailTemplatesInfoRpcResponseData object
			// containing the information about managing proxy
			// identities.  Return it.
			return reply;
		}
		catch (Exception ex) {
			throw GwtLogHelper.getGwtClientException(
				m_logger,
				ex,
				"GwtEmailTemplatesHelper.getManageEmailTemplatesInfo( SOURCE EXCEPTION ):  ");
		}		
	}
}
