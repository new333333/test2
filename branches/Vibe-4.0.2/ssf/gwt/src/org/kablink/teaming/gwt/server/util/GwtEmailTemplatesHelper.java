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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
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
import org.kablink.teaming.gwt.client.rpc.shared.FileConflictsInfoRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.FolderRowsRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.ManageEmailTemplatesInfoRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.ValidateUploadsRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.ErrorListRpcResponseData.ErrorInfo;
import org.kablink.teaming.gwt.client.rpc.shared.FileConflictsInfoRpcResponseData.DisplayInfo;
import org.kablink.teaming.gwt.client.rpc.shared.FolderRowsRpcResponseData.TotalCountType;
import org.kablink.teaming.gwt.client.util.BinderInfo;
import org.kablink.teaming.gwt.client.util.EntityId;
import org.kablink.teaming.gwt.client.util.EntryTitleInfo;
import org.kablink.teaming.gwt.client.util.UploadInfo;
import org.kablink.teaming.gwt.client.util.WorkspaceType;
import org.kablink.teaming.module.admin.AdminModule.AdminOperation;
import org.kablink.teaming.util.AllModulesInjected;
import org.kablink.teaming.util.EmailTemplatesHelper;
import org.kablink.teaming.util.FileIconsHelper;
import org.kablink.teaming.util.IconSize;
import org.kablink.teaming.util.NLT;
import org.kablink.teaming.util.Utils;
import org.kablink.teaming.web.WebKeys;
import org.kablink.teaming.web.util.MiscUtil;
import org.kablink.teaming.web.util.WebUrlUtil;
import org.kablink.util.FileUtil;

/**
 * Helper methods for the GWT UI server code in dealing with email
 * templates.
 *
 * @author drfoster@novell.com
 */
public class GwtEmailTemplatesHelper {
	protected static Log m_logger = LogFactory.getLog(GwtEmailTemplatesHelper.class);

	// The following templates are excluded from the default email
	// template list when running as Filr.
	private final static String[] EXCLUDED_FILR_DEFAULT_TEMPLATES = {
		"attachments.vm",
		"attributes.vm",
		"box.vm",
		"checkbox.vm",
		"creationdate.vm",
		"creator.vm",
		"custom.vm",
		"dataElement.vm",
		"date_time.vm",
		"date.vm",
		"description.vm",
		"digestTitle.vm",
		"digestTOC.vm",
		"divider.vm",
		"entry.vm",
		"event.vm",
		"expandable_div.vm",
		"externalConfirmation.vm",
		"fieldset.vm",
		"file.vm",
		"folder.vm",
//		"footer.vm",
		"forgottenPasswordNotification.vm",
//		"header.vm",
		"html.vm",
//		"passwordChangedNotification.vm",
		"places.vm",
		"principallist.vm",
//		"publicLinkNotification.vm",
		"radio.vm",
		"selectbox.vm",
//		"selfRegistrationRequired.vm",
//		"sharedEntryInvite.vm",
//		"sharedEntryNotification.vm",
//		"sharedFolderInvite.vm",
//		"sharedFolderNotification.vm",
		"showAvatar.vm",
		"signature.vm",
//		"style.vm",
		"survey.vm",
		"table2.vm",
		"table3.vm",
//		"teaming.vm",
		"title.vm",
		"workflow_notification_footer.vm",
		"workflow_notification_header.vm",
		"workflow.vm",
	};
	
	// The following templates are excluded from the default email
	// template list when running as Filr.
	private final static String[] EXCLUDED_VIBE_DEFAULT_TEMPLATES = {
		// Currently, we don't exclude any of the templates in Vibe.
	};
	
	/*
	 * Inhibits this class from being instantiated. 
	 */
	private GwtEmailTemplatesHelper() {
		// Nothing to do.
	}

	/**
	 * Copies the FileInputStream to the customized email templates
	 * directory as the given filename.
	 * 
	 * @param fis
	 * @param fileName
	 * 
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static void copyCustomizedEmailTemplate(AllModulesInjected bs, FileInputStream fis, String fileName) throws FileNotFoundException, IOException {
		// (bug 981245, 981383) This checking is necessary to ensure that the user
		// has the right to upload custom email template.
		bs.getAdminModule().checkAccess(AdminOperation.manageFunction);
		
		String filePath = (EmailTemplatesHelper.getEmailTemplatesCustomizedPath(true) + fileName);
		File fo = new File(filePath);
		FileOutputStream fos = null;
		try {
			// Copy the file...
			fos = new FileOutputStream(fo, false);
			FileUtil.copy(fis, fos);
			
			// ...and force the VelocityEngine for this zone to be
			// ...recreated.  This is to ensure it's resource file
			// ...cache is flushed and this file change gets picked
			// ...up.
			EmailTemplatesHelper.resetVelocityEngine();
		}
		
		finally {
			// Ensure the output stream we write the file to has been
			// closed.
			if (null != fos) {
				try                 {fos.close(); }
				catch (Exception e) {/* Ignore. */}
				fos = null;
			}
		}
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

	private static void deleteCustomizedEmailTemplatesImpl(AllModulesInjected bs, HttpServletRequest request, List<EntityId> entityIds, DeleteCustomizedEmailTemplatesRpcResponseData reply) throws GwtTeamingException {
		GwtServerProfiler gsp = GwtServerProfiler.start(m_logger, "GwtEmailTemplatesHelper.deleteCustomizedEmailTemplatesImpl()");
		try {
			// (bug 986430, 986431) This checking is necessary to ensure that the user
			// has the right to delete custom email template.
			bs.getAdminModule().checkAccess(AdminOperation.manageFunction);

			// Were we given any proxy identities to delete?
			if (MiscUtil.hasItems(entityIds)) {
				// Yes!  Scan them.
				String customPath = EmailTemplatesHelper.getEmailTemplatesCustomizedPath(true);
				List<EntityId> successfulDeletes = new ArrayList<EntityId>();
				for (EntityId eid:  entityIds) {
					// Is this entity an email template?
					if (eid.isEmailTemplate()) {
						// Yes!  Does it exist in the customized email
						// templates directory?
						String name = eid.getEmailTemplateName();
						
						// (bug 986430, 986431) We must validate the specified file name to guard against potential attack.
						if(name != null && (name.contains("/") || name.contains("\\"))) {
							// Don't allow file name to contain path delimiter.
							throw new IllegalArgumentException("Illegal file name '" + name + "'");
						}

						String fullPath = (customPath + name);
						File f = new File(fullPath);
						if (!(f.exists())) {
							// No!  Return an error for it.
							reply.addError(NLT.get("emailTemplates.delete.doesntExist", new String[]{name}));
							continue;
						}
						
						// Is it a file?
						if (!(f.isFile())) {
							// No!  Return an error for it.
							reply.addError(NLT.get("emailTemplates.delete.notAFile", new String[]{name}));
							continue;
						}
						
						try {
							// Can we delete that file?
							if (f.delete())
							     successfulDeletes.add(eid);														// Yes!  Track it.
							else reply.addError(NLT.get("emailTemplates.delete.deleteFailed", new String[]{name}));	// No!   Return an error for it.
							
						}
						catch (Exception ex) {
							// No!  Return an error for it.
							reply.addError(NLT.get("emailTemplates.delete.deleteFailed.exception", new String[]{name, ex.toString()}));
							m_logger.error("deleteCustomizedEmailTemplatesImpl():  Could not delete '" + fullPath + "'",  ex);
						}
					}
					
					else {
						// No, this entity is not an email template!
						// Return an error for it.
						reply.addError(NLT.get("emailTemplates.delete.invalidEID", new String[]{eid.getEntityType()}));
					}
				}
				reply.setSuccessfulDeletes(successfulDeletes);

				// If we deleted any of the custom email templates...
				if (!(successfulDeletes.isEmpty())) {
					// ...the velocity engine will have to be reset.
					EmailTemplatesHelper.resetVelocityEngine();
				}
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
	private static void enumerateEmailTemplateRows(HttpServletRequest request, final String[] exclusions, List<FolderRow> rows, File emailTemplatesDir, List<FolderColumn> folderColumns, boolean defaultEmailTemplates) {
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
					// Yes!  Is this file supposed to be excluded?
					String fName = f.getName();
					if (!(sInSA(fName, exclusions))) {
						// No!  Is it an email template file?
						if (MiscUtil.hasString(fName) && fName.endsWith(EmailTemplatesHelper.TEMPLATE_EXTENSION)) {
							// Yes!  Return true so that we include it.
							return true;
						}
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
					row.setSelectionDisabled(defaultEmailTemplates);
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
			final String[] exclusions = (Utils.checkIfFilr() ? EXCLUDED_FILR_DEFAULT_TEMPLATES : EXCLUDED_VIBE_DEFAULT_TEMPLATES);
			File emailTemplatesDir = EmailTemplatesHelper.getEmailTemplatesDefault();
			enumerateEmailTemplateRows(request, exclusions, rows, emailTemplatesDir, folderColumns, true);	// true -> These are the default email templates.
			
			// Add rows for the customized email templates.
			emailTemplatesDir = EmailTemplatesHelper.getEmailTemplatesCustomized();
			enumerateEmailTemplateRows(request, null, rows, emailTemplatesDir, folderColumns, false);	// false -> These are the customized email templates.

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
	 * Returns a FileConflictsInfoRpcResponseData object containing
	 * information for rendering conflicts information in a dialog.
	 * 
	 * @param bs
	 * @param request
	 * @param folderInfo
	 * @param fileConflicts
	 * @param reply
	 * 
	 * @throws GwtTeamingException
	 */
	public static void getFileConflictsInfo(AllModulesInjected bs, HttpServletRequest request, BinderInfo folderInfo, List<UploadInfo> fileConflicts, FileConflictsInfoRpcResponseData reply) throws GwtTeamingException {
		try {
			// Add the DisplayInfo for the customized email templates
			// area.
			Binder binder = bs.getBinderModule().getBinder(folderInfo.getBinderIdAsLong());
			DisplayInfo di = new DisplayInfo(
				NLT.get("emailTemplates.binderTitle.customized"),
				EmailTemplatesHelper.getEmailTemplatesCustomizedPath(true),
				binder.getIconName(IconSize.MEDIUM));
			reply.setFolderDisplay(di);
			
			// If we have some file conflicts...
			if (MiscUtil.hasItems(fileConflicts)) {
				// ...scan them...
				for (UploadInfo fileConflict:  fileConflicts) {
					di = new DisplayInfo(
						fileConflict.getName(),
						"",	// Don't need a path for files.
						FileIconsHelper.getFileIconFromFileName(
							fileConflict.getName(),
							IconSize.SMALL));
					reply.addFileConflictDisplay(di);
				}
			}
		}
		
		catch (Exception e) {
			// Convert the exception to a GwtTeamingException and throw
			// that.
			throw
				GwtLogHelper.getGwtClientException(
					m_logger,
					e,
					"GwtEmailTemplatesHelper.getFileConflictsInfo( SOURCE EXCEPTION ):  ");
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
	
	/*
	 * Returns true if a String is in a String[] and false otherwise.
	 */
	private static boolean sInSA(String s, String[] sa) {
		if ((MiscUtil.hasString(s) && (null != sa) && (0 < sa.length))) {
			for (String saString:  sa) {
				if (s.equalsIgnoreCase(saString)) {
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * Validates that the user can upload the files/folders in
	 * List<UploadInfo> of things pending upload.
	 * 
	 * @param bs
	 * @param request
	 * @param folderInfo
	 * @param uploads
	 * @param reply
	 * 
	 * @throws GwtTeamingException
	 */
	public static void validateUploads(AllModulesInjected bs, HttpServletRequest request, BinderInfo folderInfo, List<UploadInfo> uploads, ValidateUploadsRpcResponseData reply) throws GwtTeamingException {
		try {
			// If there aren't any upload items...
			if (!(MiscUtil.hasItems(uploads))) {
				// ...bail.
				return;
			}

			// What the base path to the customized email templates?
			String basePath = EmailTemplatesHelper.getEmailTemplatesCustomizedPath(true);
			
			// Scan the files to be uploaded.
			for (UploadInfo upload:  uploads) {
				// If this isn't a file...
				String name = upload.getName();
				if (!upload.isFile()) {
					// ...it can't be uploaded.
					reply.addError(NLT.get("validateUploadError.emailTemplate.notAFile", new String[]{name}));
					continue;
				}

				// If this isn't an email template...
				if (!(name.endsWith(EmailTemplatesHelper.TEMPLATE_EXTENSION))) {
					// ...it can't be uploaded.
					reply.addError(NLT.get("validateUploadError.emailTemplate.notAnEmailTemplate", new String[]{name, EmailTemplatesHelper.TEMPLATE_EXTENSION}));
					continue;
				}

				// Does this file already exist as a customized template?
				File uploadFile = new File(basePath + name);
				if (uploadFile.exists()) {
					// Yes!  Track it as a duplicate.
					reply.addDuplicate(upload);
					continue;
				}
			}
		}
		
		catch (Exception e) {
			// Convert the exception to a GwtTeamingException and throw
			// that.
			throw
				GwtLogHelper.getGwtClientException(
					m_logger,
					e,
					"GwtEmailTemplatesHelper.validateUploads( SOURCE EXCEPTION ):  ");
		}
	}
}
