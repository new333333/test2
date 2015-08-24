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
package org.kablink.teaming.servlet.forum;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.activation.FileTypeMap;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.compress.archivers.zip.Zip64Mode;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.compress.archivers.zip.X5455_ExtendedTimestamp;

import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.UncheckedIOException;
import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.domain.Attachment;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.DefinableEntity;
import org.kablink.teaming.domain.Entry;
import org.kablink.teaming.domain.FileAttachment;
import org.kablink.teaming.domain.Folder;
import org.kablink.teaming.domain.FolderEntry;
import org.kablink.teaming.domain.AuditType;
import org.kablink.teaming.domain.EntityIdentifier.EntityType;
import org.kablink.teaming.domain.ShareItem;
import org.kablink.teaming.domain.ShareItem.RecipientType;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.module.admin.AdminModule.AdminOperation;
import org.kablink.teaming.module.binder.BinderModule;
import org.kablink.teaming.module.binder.BinderModule.BinderOperation;
import org.kablink.teaming.module.file.FileModule;
import org.kablink.teaming.module.folder.FolderModule;
import org.kablink.teaming.module.folder.FolderModule.FolderOperation;
import org.kablink.teaming.module.shared.EntityIndexUtils;
import org.kablink.teaming.module.shared.FileUtils;
import org.kablink.teaming.module.workspace.WorkspaceModule;
import org.kablink.teaming.runas.RunasCallback;
import org.kablink.teaming.runas.RunasTemplate;
import org.kablink.teaming.security.AccessControlException;
import org.kablink.teaming.telemetry.TelemetryService;
import org.kablink.teaming.util.Constants;
import org.kablink.teaming.util.EmailTemplatesHelper;
import org.kablink.teaming.util.FileHelper;
import org.kablink.teaming.util.NLT;
import org.kablink.teaming.util.SPropsUtil;
import org.kablink.teaming.util.SpringContextUtil;
import org.kablink.teaming.web.WebKeys;
import org.kablink.teaming.web.util.AdminHelper;
import org.kablink.teaming.web.util.EntryCsvHelper;
import org.kablink.teaming.web.util.MiscUtil;
import org.kablink.teaming.web.util.WebUrlUtil;
import org.kablink.util.FileUtil;
import org.kablink.util.Http;
import org.kablink.util.search.Criteria;
import org.kablink.util.search.Order;

import static org.kablink.util.search.Restrictions.in;

import org.springframework.mail.javamail.ConfigurableMimeFileTypeMap;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;

/**
 * ?
 * 
 * @author ?
 */
@SuppressWarnings("unused")
public class ReadFileController extends AbstractReadFileController {
	private final static boolean ZIP_ENCODED_FILENAMES	= true;
	private final static boolean UTF8_ENCODE_ZIPS		= true;
	
	private FileTypeMap mimeTypes = new ConfigurableMimeFileTypeMap();
	
	// Default filename used when multiple files from a list are to be
	// downloaded in a .zip file.  Note that this matches the English
	// translation of the file.zipListDownload.fileName string in the
	// messages.properties file.
	private final static String ZIPLIST_DEFAULT_FILENAME = "files.zip";
	
	// Default filename used when the files from a folder are to be
	// downloaded in a .zip file.  Note that this matches the English
	// translation of the file.zipFolderDownload.fileName string in the
	// messages.properties file.
	private final static String ZIPFOLDER_DEFAULT_FILENAME = "folder.zip";
	
	/*
	 * Inner class used to count files and folders added to a zip.
	 */
	private class ZipCounter {
		private int	m_fileCount;	//
		private int	m_folderCount;	//

		/*
		 * Constructor method.
		 */
		private ZipCounter() {
			super();
			
			m_fileCount   =
			m_folderCount = 0;
		}
		
		/*
		 * Get'er methods.
		 */
		private int getFileCount()   {return m_fileCount;  }
		private int getFolderCount() {return m_folderCount;}
		
		/*
		 * Set'er method.
		 */
		private void incrFiles(  int increment) {m_fileCount   += increment;}
		private void incrFolders(int increment) {m_folderCount += increment;}
	}
	
	@Override
	@SuppressWarnings("unchecked")
	protected ModelAndView handleRequestAfterValidation(final HttpServletRequest request,
            final HttpServletResponse response) throws Exception {
		Long zoneId = RequestContextHolder.getRequestContext().getZoneId();
		Boolean singleByte = SPropsUtil.getBoolean("export.filename.8bitsinglebyte.only", true);
		
		// Assuming that the full request URL was http://localhost:8080/ssf/s/readFile/entityType/entryId/fileTime/fileVersion/filename.ext
		// the following call returns "/readFile/entityType/entryId/fileId/fileTime/fileVersion/filename.ext" portion of the URL.
		String pathInfo = request.getPathInfo();
		
		String[] args = pathInfo.split(Constants.SLASH);
		//We expect the url to be formatted as /readFile/entityType/entryId/fileTime/fileVersion/filename.ext
		//  or /readFile/entityType/entryId/zip
		//To support sitescape forum, where folder structures were allowed on an entry, the url may contain more pathinfo.
		//fileVersion=last, read latest
		//fileTime is present for browser caching
		//filename is present for browser handling of relative files
		if (args.length == WebUrlUtil.FILE_URL_ZIP_ARG_LENGTH && 
				String.valueOf(args[WebUrlUtil.FILE_URL_FILE_ID]).equals(WebUrlUtil.FILE_URL_TYPE_ZIP)) {
			//The user wants a zip file of all attachments
			try {
				DefinableEntity entity = getEntity(args[WebUrlUtil.FILE_URL_ENTITY_TYPE], Long.valueOf(args[WebUrlUtil.FILE_URL_ENTITY_ID]));
				Set<Attachment> attachments = entity.getAttachments();
				String fileName = getBinderModule().filename8BitSingleByteOnly(entity.getTitle() + ".zip", "files.zip", singleByte);
				ZipArchiveOutputStream zipOut = buildZipAndSetupResponse(response, fileName);
			
				Integer fileCounter = 1;
				for (Attachment attachment : attachments) {
					if (attachment instanceof FileAttachment) {
						String attExt = EntityIndexUtils.getFileExtension(((FileAttachment) attachment).getFileItem().getName());
						String attName = ((FileAttachment) attachment).getFileItem().getName();	//Note: do not translate this name
						if (!ZIP_ENCODED_FILENAMES) {
							attName = getBinderModule().filename8BitSingleByteOnly(attName, 
									"__file"+fileCounter.toString(), singleByte);	//Note: do not translate this name
						}
						fileCounter++;
						InputStream fileStream = null;
						try {
							if (entity.getEntityType().equals(EntityType.folderEntry)) {
								fileStream = getFileModule().readFile(entity.getParentBinder(), entity, (FileAttachment)attachment);
								// Mark it in the audit trail.
								getReportModule().addFileInfo(AuditType.download, (FileAttachment)attachment);
							} else if (entity.getEntityType().equals(EntityType.folder) || 
									entity.getEntityType().equals(EntityType.workspace)) {
								fileStream = getFileModule().readFile((Binder)entity, entity, (FileAttachment)attachment);
								// Mark it in the audit trail.
								getReportModule().addFileInfo(AuditType.download, (FileAttachment)attachment);
							} else {
								zipOut.finish();
								return null;
							}
							ZipArchiveEntry zae = new ZipArchiveEntry(attName);
							zipOut.putArchiveEntry(zae);
							FileUtil.copy(fileStream, zipOut);
							setDateTimeOnZipArchiveEntry(zae, attachment);
							zipOut.closeArchiveEntry();
						} catch (Exception e) {
							logger.error("Error reading file", e);
						}
						finally {
							try {
								if(fileStream != null) {
									fileStream.close();
									fileStream = null;
								}
							}
							catch(IOException ignore) {}
						}
					}
				}
				zipOut.finish();
			
				return null;
			} catch(Exception e) {
				//Bad format of url; just return null
				logger.error("Error reading file", e);
				response.sendError(HttpServletResponse.SC_BAD_REQUEST, NLT.get("file.error.unknownFile"));
			}
			return null;
		
		} else if (args.length == WebUrlUtil.FILE_URL_ZIP_SINGLE_ARG_LENGTH && 
				String.valueOf(args[WebUrlUtil.FILE_URL_FILE_ID]).equals(WebUrlUtil.FILE_URL_TYPE_ZIP)) {
			//The user wants a zip file of a single attachment
			String faId = args[WebUrlUtil.FILE_URL_ZIP_SINGLE_FILE_ID];
			try {
				DefinableEntity entity = getEntity(args[WebUrlUtil.FILE_URL_ENTITY_TYPE], Long.valueOf(args[WebUrlUtil.FILE_URL_ENTITY_ID]));
				Set<Attachment> attachments = entity.getAttachments();
				FileAttachment fileAtt = null;
				for (Attachment attachment : attachments) {
					if (attachment instanceof FileAttachment) {
						if (attachment.getId().equals(faId)) {
							fileAtt = (FileAttachment)attachment;
							break;
						}
						fileAtt = ((FileAttachment)attachment).findFileVersionById(faId);
						if (fileAtt != null) break;
					}
				}
				if (fileAtt != null) {
					String fileName = EntityIndexUtils.getFileNameWithoutExtension(fileAtt.getFileItem().getName());
					fileName = getBinderModule().filename8BitSingleByteOnly(fileName + ".zip", "download.zip", singleByte);
					ZipArchiveOutputStream zipOut = buildZipAndSetupResponse(response, fileName);		
					
					String attExt = EntityIndexUtils.getFileExtension(fileAtt.getFileItem().getName());
					String attName = fileAtt.getFileItem().getName();	//Note: do not translate this name
					if (!ZIP_ENCODED_FILENAMES) {
						attName = getBinderModule().filename8BitSingleByteOnly(attName, 
								"file", singleByte);	//Note: do not translate this name
					}
					
					InputStream fileStream = null;
					try {
						if (entity.getEntityType().equals(EntityType.folderEntry)) {
							fileStream = getFileModule().readFile(entity.getParentBinder(), entity, fileAtt);
							// Mark it in the audit trail.
							getReportModule().addFileInfo(AuditType.download, fileAtt);
						} else if (entity.getEntityType().equals(EntityType.folder) || 
								entity.getEntityType().equals(EntityType.workspace)) {
							fileStream = getFileModule().readFile((Binder)entity, entity, fileAtt);
							// Mark it in the audit trail.
							getReportModule().addFileInfo(AuditType.download, fileAtt);
						} else {
							zipOut.finish();
							return null;
						}
						ZipArchiveEntry zae = new ZipArchiveEntry(attName);
						zipOut.putArchiveEntry(zae);
						String loggerText = "Copying an unencrypted file of length ";
						if (fileAtt.isEncrypted()) {
							loggerText = "Copying an encrypted file of length ";
						}
						long startTime = System.nanoTime();
						FileUtil.copy(fileStream, zipOut);
						/*
						 * This was used to measure the time degradation for encrypted files
						logger.info(loggerText + fileAtt.getFileItem().getLengthKB() + 
								"KB took " + (System.nanoTime()-startTime)/1000000.0 + " ms");
						*/
						setDateTimeOnZipArchiveEntry(zae, fileAtt);
						zipOut.closeArchiveEntry();
					} catch (Exception e) {
						logger.error("Error reading file", e);
					}
					finally {
						try {
							if(fileStream != null) {
								fileStream.close();
								fileStream = null;
							}
						}
						catch(IOException ignore) {}
					}
					zipOut.finish();
				}
			
				return null;
			} catch(Exception e) {
				//Bad format of url; just return null
				logger.error("Error reading file", e);
				response.sendError(HttpServletResponse.SC_BAD_REQUEST, NLT.get("file.error.unknownFile"));
			}
			return null;
			
		} else if ((WebUrlUtil.FILE_URL_ZIPLIST_ARG_LENGTH == args.length) && 
					String.valueOf(args[WebUrlUtil.FILE_URL_ZIPLIST_ZIP               ]).equals("zip"                           ) &&
					String.valueOf(args[WebUrlUtil.FILE_URL_ZIPLIST_OPERATION         ]).equals(WebKeys.OPERATION_READ_FILE_LIST) &&
					String.valueOf(args[WebUrlUtil.FILE_URL_ZIPLIST_FILE_IDS_OPERAND  ]).equals(WebKeys.URL_FOLDER_ENTRY_LIST   ) &&
					String.valueOf(args[WebUrlUtil.FILE_URL_ZIPLIST_FOLDER_IDS_OPERAND]).equals(WebKeys.URL_FOLDER_LIST        )) {
			// Zip and download the primary files from a specific list
			// of entries!
			try {
				// Access the entries whose primary files are to be
				// zipped.
				String feIdsPacked = String.valueOf(args[WebUrlUtil.FILE_URL_ZIPLIST_FILE_IDS]);
				Collection<FolderEntry> feSet;
				if (feIdsPacked.equals("-")) {
					feSet = null;
				}
				else {
					String[]   feIdsList   = feIdsPacked.split(":");
					List<Long> feIds       = new ArrayList<Long>();
					for (String id:  feIdsList) {
						feIds.add(Long.parseLong(id));
					}
					feSet = getFolderModule().getEntries(feIds);
				}
				
				// Access the folders whose primary files are to be
				// zipped.
				String folderIdsPacked = String.valueOf(args[WebUrlUtil.FILE_URL_ZIPLIST_FOLDER_IDS]);
				Collection<Folder> folderSet;
				if (folderIdsPacked.equals("-")) {
					folderSet = null;
				}
				else {
					String[]   folderIdsList   = folderIdsPacked.split(":");
					List<Long> folderIds       = new ArrayList<Long>();
					for (String id:  folderIdsList) {
						folderIds.add(Long.parseLong(id));
					}
					folderSet = getFolderModule().getFolders(folderIds);
				}
				
				// Create the zip file for downloading...
				String fileName = NLT.get("file.zipListDownload.fileName");
				fileName = getBinderModule().filename8BitSingleByteOnly(fileName, ZIPLIST_DEFAULT_FILENAME, singleByte);
				ZipArchiveOutputStream zipOut = buildZipAndSetupResponse(response, fileName);

				// ...if we have any entries...
				if (MiscUtil.hasItems(feSet)) {
					// ...add their primary files to the zip...
					addCollectionFilesToZip(zipOut, null, feSet, singleByte);
				}
				
				// ...if we have any folders...
				if (MiscUtil.hasItems(folderSet)) {
					// ...scan them...
					boolean recursive = Boolean.parseBoolean(String.valueOf(args[WebUrlUtil.FILE_URL_ZIPLIST_RECURSIVE]));
					int topFolderCount = 1;
					ZipCounter runningZipCount = new ZipCounter();
					for (Folder folder:  folderSet) {
						// ...generate a name for the zip for each.
	    				String folderName = folder.getTitle();	// Note: do not translate this name.
	    				if (MiscUtil.hasString(folderName)) {
		    				if (!ZIP_ENCODED_FILENAMES) {
		    					folderName = getBinderModule().filename8BitSingleByteOnly(
		    						folderName,
		    						("__folder" + String.valueOf(topFolderCount)),	// For folder names that are invalid for a zip.
		    						singleByte);									// Note: do not translate this name.	
		    				}
	    				}
	    				else {
	    					folderName = ("__folder" + String.valueOf(topFolderCount));
	    				}
	    				
	    				// ...count this folder...
	    				topFolderCount += 1;
	    				runningZipCount.incrFolders(1);
	    				
						// ...and add them and their contents to the
						// ...zip...
						addFolderContentsToZip(
							response,
							zipOut,
							folderName,
							folder.getId(),
							runningZipCount,
							recursive,
							singleByte);
					}
				}
				
				// ...and finish it. 
				zipOut.finish();
			}
			
			catch(Exception e) {
				// Bad format of url; just return null.
				logger.error("ReadFileController.handleRequestAfterValidation( ZipList Downlaod ):  EXCEPTION:  ", e);
				response.sendError(HttpServletResponse.SC_BAD_REQUEST, NLT.get("file.error.unknownFileList"));
			}
			
			return null;
		}
		
		else if ((WebUrlUtil.FILE_URL_ZIPFOLDER_ARG_LENGTH == args.length) && 
				String.valueOf(args[WebUrlUtil.FILE_URL_ZIPFOLDER_ZIP              ]).equals("zip"                        ) &&
				String.valueOf(args[WebUrlUtil.FILE_URL_ZIPFOLDER_OPERATION        ]).equals(WebKeys.OPERATION_READ_FOLDER) &&
				String.valueOf(args[WebUrlUtil.FILE_URL_ZIPFOLDER_RECURSIVE_OPERAND]).equals(WebKeys.URL_RECURSIVE       )) {
			// Zip and download the primary files from a folder,
			// optionally recursively descending any sub-folders!
			try {
				// Do we zip things recursively?
				boolean recursive = Boolean.parseBoolean(String.valueOf(args[WebUrlUtil.FILE_URL_ZIPFOLDER_RECURSIVE]));
				
				// What folder are we zipping?
				Long   folderId = Long.parseLong(String.valueOf(args[WebUrlUtil.FILE_URL_ZIPFOLDER_FOLDER_ID]));
				Folder folder   = getFolderModule().getFolder(folderId);
				
				// Create the zip file for downloading...
				String fileName = folder.getTitle();
				if (MiscUtil.hasString(fileName))
				     fileName += ".zip";
				else fileName = NLT.get("file.zipFolderDownload.fileName");
				fileName = getBinderModule().filename8BitSingleByteOnly(fileName, ZIPFOLDER_DEFAULT_FILENAME, singleByte);
				ZipArchiveOutputStream zipOut = buildZipAndSetupResponse(response, fileName);

				// ...add the folder's contents to it...
				ZipCounter runningZipCount = new ZipCounter();
				runningZipCount.incrFolders(1);	// Count this folder.
				addFolderContentsToZip(
					response,
					zipOut,
					null,
					folderId,
					runningZipCount,
					recursive,
					singleByte);

				// ...and finish it. 
				zipOut.finish();
			}
			
			catch(Exception e) {
				// Bad format of url; just return null.
				logger.error("ReadFileController.handleRequestAfterValidation( ZipFolder Downlaod ):  EXCEPTION:  ", e);
				response.sendError(HttpServletResponse.SC_BAD_REQUEST, NLT.get("file.error.unknownFolder"));
			}
			
			return null;
		}

		else if ((WebUrlUtil.FILE_URL_CSVFOLDER_ARG_LENGTH == args.length) && 
				args[WebUrlUtil.FILE_URL_CSVFOLDER_FOLDER_CSV      ].equals(WebKeys.URL_FOLDER_CSV       ) &&
				args[WebUrlUtil.FILE_URL_CSVFOLDER_FOLDER_CSV_DELIM].equals(WebKeys.URL_FOLDER_CSV_DELIM ) &&
				args[WebUrlUtil.FILE_URL_CSVFOLDER_OPERATION       ].equals(WebKeys.OPERATION_READ_FOLDER)) {
			try {
				// Store the CSV delimiter to use in an options Map.
				String csvDelim = args[WebUrlUtil.FILE_URL_CSVFOLDER_FOLDER_CSV_DELIM + 1];
				if (!(MiscUtil.hasString(csvDelim))) {
					csvDelim = EntryCsvHelper.DEFAULT_CSV_DELIMITER;
				}
				HashMap csvOptions = new HashMap();
				csvOptions.put(ObjectKeys.CSV_DELIMITER, csvDelim);
				
				// What folder are we outputting as CSV?
				Long folderId = Long.parseLong(String.valueOf(args[WebUrlUtil.FILE_URL_CSVFOLDER_FOLDER_ID]));
				Folder folder = getFolderModule().getFolder(folderId);
				if (getFolderModule().testAccess(folder, FolderOperation.downloadFolderAsCsv)) {
					if (folder != null && getFolderModule().testAccess(folder, FolderOperation.downloadFolderAsCsv)) {
						String shortFileName = folder.getNormalTitle() + ".csv";
						String contentType = FileUtils.getMimeContentType(getFileTypeMap(), shortFileName);
						contentType = FileUtils.validateDownloadContentType(contentType);
						if (!(contentType.toLowerCase().contains("charset"))) {
							String encoding = SPropsUtil.getString("web.char.encoding", "UTF-8");
							if (MiscUtil.hasString(encoding)) {
								contentType += ("; charset=" + encoding);
							}
						}
						response.setContentType(contentType);
						boolean isHttps = request.getScheme().equalsIgnoreCase("https");
						String cacheControl = "private, max-age=86400";
						if (isHttps) {
							response.setHeader("Pragma", "public");
							cacheControl += ", proxy-revalidate, s-maxage=0";
						}
						response.setHeader("Cache-Control", cacheControl);
						response.setHeader("Content-Disposition",
							("attachment; filename=\"" + FileHelper.encodeFileName(request, shortFileName) + "\""));
						//Write out the BOM so Excel knows how to handle double byte characters properly.
						OutputStream outputStream = response.getOutputStream();
						outputStream.write(0xEF);   // 1st byte of BOM
						outputStream.write(0xBB);
						outputStream.write(0xBF);   // last byte of BOM
					} else {
						// Bad format of url; just return null.
						response.sendError(HttpServletResponse.SC_BAD_REQUEST, NLT.get("file.error.unknownFolder"));
					}
				} else {
					// No rights to do this operation
					response.sendError(HttpServletResponse.SC_BAD_REQUEST, NLT.get("error.noRightToDownloadAsCsv"));
				}

				OutputStream out = response.getOutputStream();
				EntryCsvHelper.folderToCsv(this, folder, csvOptions, out);
				response.getOutputStream().flush();
			} catch(AccessControlException e) {
				// No access to the folder.
				response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());				
			} catch(Exception e) {
				// Something else failed.
				logger.error("Error reading file", e);
				response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());				
			}
			
		}

		else if ((WebUrlUtil.FILE_URL_SHARED_PUBLIC_FILE_ARG_LENGTH == args.length) && 
				String.valueOf(args[WebUrlUtil.FILE_URL_ENTITY_TYPE]).equals(WebUrlUtil.FILE_URL_TYPE_SHARE)) {
			// Shared File Link
			try {
				Long shareItemId = Long.parseLong(String.valueOf(args[WebUrlUtil.FILE_URL_SHARED_PUBLIC_FILE_SHARE_ID]));
				String passKey = args[WebUrlUtil.FILE_URL_SHARED_PUBLIC_FILE_PASSKEY];
				final String operation = args[WebUrlUtil.FILE_URL_SHARED_PUBLIC_FILE_OPERATION];
				if (operation.equals(WebKeys.URL_SHARE_PUBLIC_LINK) || operation.equals(WebKeys.URL_SHARE_PUBLIC_LINK_HTML)) {
					final ShareItem shareItem = getSharingModule().getShareItem(shareItemId);
					if (shareItem != null && shareItem.isLatest() && !shareItem.isExpired() && !shareItem.isDeleted()) {
						if (shareItem.getRecipientType().equals(RecipientType.publicLink) && 
								shareItem.getPassKey().equals(passKey) && !passKey.equals("")) {
							User sharer = getProfileModule().getUserDeadOrAlive(shareItem.getSharerId());
							final User recipient = RequestContextHolder.getRequestContext().getUser();
							if (sharer != null & sharer.isActive()) {
								//OK, run this request under the account of the sharer to see if the access to the item is still allowed
								final String fn = args[WebUrlUtil.FILE_URL_SHARED_PUBLIC_FILE_NAME];
					            Exception result = (Exception) RunasTemplate.runas(new RunasCallback() {
									@Override
									public Object doAs() {
										try {
											DefinableEntity sharedEntity = getSharingModule().getSharedEntity(shareItem);
											FileAttachment fa = null;
											if (sharedEntity != null) {
												fa = getAttachment(sharedEntity, fn, WebKeys.READ_FILE_LAST, null);
											}
											if (fa != null && operation.equals(WebKeys.URL_SHARE_PUBLIC_LINK)) {
												DefinableEntity entity = fa.getOwner().getEntity();
												if ((entity instanceof FolderEntry) && ((FolderEntry) entity).isPreDeleted()) {
													logger.error("Error:  Cannot download a file that's in the trash.");
													response.sendError(HttpServletResponse.SC_BAD_REQUEST, NLT.get("file.error.inTrash"));
												}
												
												else {
													String shortFileName = FileUtil.getShortFileName(fa.getFileItem().getName());	
													String contentType = FileUtils.getMimeContentType(getFileTypeMap(), shortFileName);
													WebUrlUtil.getSharedPublicFileUrl(request, shareItem.getId(), shareItem.getPassKey(), WebKeys.URL_SHARE_PUBLIC_LINK, shortFileName);
													//Protect against XSS attacks if this is an HTML file
													contentType = FileUtils.validateDownloadContentType(contentType);
					
													if (!(contentType.toLowerCase().contains("charset"))) {
														String encoding = SPropsUtil.getString("web.char.encoding", "UTF-8");
														if (MiscUtil.hasString(encoding)) {
															contentType += ("; charset=" + encoding);
														}
													}
													response.setContentType(contentType);
													boolean isHttps = request.getScheme().equalsIgnoreCase("https");
													String cacheControl = "private, max-age=86400";
													if (isHttps) {
														response.setHeader("Pragma", "public");
														cacheControl += ", proxy-revalidate, s-maxage=0";
													}
													response.setHeader("Cache-Control", cacheControl);
													String attachment = "";
													if (FileHelper.checkIfAttachment(contentType)) attachment = "attachment; ";
													response.setHeader("Content-Disposition",
															attachment + "filename=\"" + FileHelper.encodeFileName(request, shortFileName) + "\"");
													response.setHeader("Last-Modified", formatDate(fa.getModification().getDate()));	
													try {
														Binder parent = getBinder(entity);
														if (!fa.isEncrypted()) {
															//The file length cannot be guaranteed if the file is encrypted. It is better to leave this field off in that case.
															response.setHeader("Content-Length", 
																String.valueOf(FileHelper.getLength(parent, entity, fa)));
														}
														getFileModule().readFile(parent, entity, fa, response.getOutputStream());
														//Report the file download in the audit trail
														getReportModule().addFileInfo(AuditType.view, fa, recipient);
													}
													catch(Exception e) {
														logger.error("Error reading file", e);
														response.sendError(HttpServletResponse.SC_BAD_REQUEST, NLT.get("file.error") + ": " + e.getMessage());
													}
												}
												
											} else if (fa != null && operation.equals(WebKeys.URL_SHARE_PUBLIC_LINK_HTML)) {
												String viewType = ServletRequestUtils.getStringParameter(request, WebKeys.URL_FILE_VIEW_TYPE, ""); 
												String fileId = ServletRequestUtils.getStringParameter(request, WebKeys.URL_FILE_ID, ""); 
												String fileTitle = ServletRequestUtils.getStringParameter(request, WebKeys.URL_FILE_TITLE, ""); 
												if (!fileTitle.equals("")) fileTitle = Http.decodeURL(fileTitle);
												String fileTime = ServletRequestUtils.getStringParameter(request, WebKeys.URL_FILE_TIME, ""); 
												if (viewType.equals("")) {
													/**
													 * Convert specified file (XLS, PDF, DOC, etc) to HTML format and display to browser. Part of "View as HTML" functionality.
													 */
													DefinableEntity entity = fa.getOwner().getEntity();
													try {
														Binder parent = null;
														if (entity instanceof Binder) parent = (Binder)entity;
														if (entity instanceof FolderEntry) parent = ((FolderEntry)entity).getParentBinder();
														response.setContentType("text/html");
														response.setHeader("Cache-Control", "private");
														if (entity != null && parent != null) {
															getConvertedFileModule().readCacheHtmlFile(request.getRequestURI(), shareItem, parent, entity, fa, response.getOutputStream());
															getReportModule().addFileInfo(AuditType.view, fa, recipient);
														}
														return null;
													}
													catch(Exception e) {
														logger.error("Error reading file", e);
														String url = WebUrlUtil.getServletRootURL(request);
														url += "errorHandler";
														String eMsg = e.getLocalizedMessage();
														if (eMsg == null) eMsg = e.toString();
														eMsg = eMsg.replaceAll("\"", "'");
														String output = "<html><head><script language='javascript'>function submitForm(){ document.errorform.submit(); }</script></head><body onload='javascript:submitForm()'><form name='errorform' action='" + url + "'><input type='hidden' name='ssf-error' value=\"" + eMsg + "\"></input></form></body></html>";
														
														response.setContentType("text/html; charset=UTF-8");
														response.getOutputStream().print(output);
														response.getOutputStream().flush();
													}
													
												} else if (viewType.equals("image") || viewType.equals("url")) {
													/**
													 * There is a <IMG> or <A> in an HTML file that points to a file within the SS file repository
													 * We must fetch that file from disk an stream into the browser. The file location could be anywhere
													 * on the server machine. Part of "View as HTML" functionality.
													 */
													try {
														DefinableEntity entity = fa.getOwner().getEntity();
														Binder parent = getBinder(entity);
														String fileName = ServletRequestUtils.getStringParameter(request, "filename", ""); 
														if (viewType.equals("url")) {
															response.setContentType("text/html");
															response.setHeader("Cache-Control", "private");
															getConvertedFileModule().readCacheUrlReferenceFile(shareItem, parent, entity, fa, response.getOutputStream(), fileName);
														} else {
															response.setContentType("image/jpeg");
															getConvertedFileModule().readCacheImageReferenceFile(shareItem, parent, entity, fa, response.getOutputStream(), fileName);
														}
													}
													catch(Exception e) {
														logger.error("Error reading file", e);
														response.getOutputStream().print(NLT.get("file.error") + ": " + e.getLocalizedMessage());
													}
													
													try {
														response.getOutputStream().flush();
													}
													catch(Exception ignore) {}
												}
	
											} else {
												response.sendError(HttpServletResponse.SC_BAD_REQUEST, NLT.get("file.error.unknownFile"));
											}
										} catch(Exception e) {
											// Bad format of url; just return null.
											logger.error("ReadFileController.handleRequestAfterValidation( Share File Downlaod ):  EXCEPTION:  ", e);
											//res.sendError(HttpServletResponse.SC_BAD_REQUEST, NLT.get("file.error.unknownFile"));
											return e;
										}
										return null;
									}
								}, zoneId, sharer.getId());
					            if (result != null) {
					            	response.sendError(HttpServletResponse.SC_BAD_REQUEST, NLT.get("file.error.unknownFile"));
					            }
							}
						}
					}
				}
			} catch(Exception e) {
				// Bad format of url; just return null.
				logger.error("ReadFileController.handleRequestAfterValidation( Share File Downlaod ):  EXCEPTION:  ", e);
				response.sendError(HttpServletResponse.SC_BAD_REQUEST, NLT.get("file.error.unknownFile"));
			}
		}
		
		else if ((args.length == WebUrlUtil.FILE_URL_EMAIL_TEMPLATE_ARG_LENGTH) &&
				(args[WebUrlUtil.FILE_URL_EMAIL_TEMPLATE_EMAIL_TEMPLATE].equals(WebUrlUtil.FILE_URL_EMAIL_TEMPLATE))) {
			// Email template!
			//
			// Do we have both a type and filename?
			String type     = args[WebUrlUtil.FILE_URL_EMAIL_TEMPLATE_TYPE];
			String fileName = args[WebUrlUtil.FILE_URL_EMAIL_TEMPLATE_FILENAME];
			if (MiscUtil.hasString(type) && MiscUtil.hasString(fileName)) {
				// Yes!  Construct a full path to the file.
				boolean defaultEmailTemplate = (type.equals(WebUrlUtil.FILE_URL_EMAIL_TEMPLATE_TYPE_DEFAULT));
				String filePath =
					(defaultEmailTemplate                                       ?
						EmailTemplatesHelper.getEmailTemplatesDefaultPath(true) :
						EmailTemplatesHelper.getEmailTemplatesCustomizedPath(true));
				filePath += fileName;

				// Setup the appropriate content information in the
				// response.
				String contentType = FileUtils.getMimeContentType(getFileTypeMap(), fileName);
				contentType = FileUtils.validateDownloadContentType(contentType);
				if (!(contentType.toLowerCase().contains("charset"))) {
					String encoding = SPropsUtil.getString("web.char.encoding", "UTF-8");
					if (MiscUtil.hasString(encoding)) {
						contentType += ("; charset=" + encoding);
					}
				}
				response.setContentType(contentType);
				boolean isHttps = request.getScheme().equalsIgnoreCase("https");
				String cacheControl = "private, max-age=86400";
				if (isHttps) {
					response.setHeader("Pragma", "public");
					cacheControl += ", proxy-revalidate, s-maxage=0";
				}
				response.setHeader("Cache-Control", cacheControl);
				response.setHeader("Content-Disposition", ("attachment; filename=\"" + FileHelper.encodeFileName(request, fileName) + "\""));
				
				// Copy the file to the response.
				InputStream is = null;
				try {
					File emailTemplate = new File(filePath);
					is = new FileInputStream(emailTemplate);
					FileCopyUtils.copy(is, response.getOutputStream());
				}
				
				catch(Exception e) {
					// If there's any exception, return an error in the
					// response.
					logger.error("Error reading '" + type + "' email template file '" + filePath + "'", e);
					response.sendError(HttpServletResponse.SC_BAD_REQUEST, NLT.get("file.error.cantReadEmailTemplate", new String[]{type, filePath}));
				}
				
				finally {
					// Finally, ensure we don't leave a dangling input
					// stream.
					if (null != is) {
						try                    {is.close();}
						catch (IOException io) {/* Ignored. */}
						is = null;
					}
				}		
			}
		}
		
		else if ((args.length == WebUrlUtil.FILE_URL_TELEMETRY_DATA_ARG_LENGTH) &&
				(args[WebUrlUtil.FILE_URL_TELEMETRY_DATA_TELEMETRY_DATA].equals(WebUrlUtil.FILE_URL_TELEMETRY_DATA))) {
			// Telemetry data!  Does the user have rights to download
			// it?
			if (getAdminModule().testAccess(AdminOperation.manageFunction)) {
				// Yes!  Setup the appropriate content information in
				// the response.
				String contentType = FileUtils.getMimeContentType(getFileTypeMap(), WebUrlUtil.FILE_URL_TELEMETRY_DATA_FILENAME_DEFAULT);
				contentType = FileUtils.validateDownloadContentType(contentType);
				if (!(contentType.toLowerCase().contains("charset"))) {
					String encoding = SPropsUtil.getString("web.char.encoding", "UTF-8");
					if (MiscUtil.hasString(encoding)) {
						contentType += ("; charset=" + encoding);
					}
				}
				response.setContentType(contentType);
				boolean isHttps = request.getScheme().equalsIgnoreCase("https");
				String cacheControl = "private, max-age=0";
				if (isHttps) {
					response.setHeader("Pragma", "public");
					cacheControl += ", proxy-revalidate, s-maxage=0";
				}
				response.setHeader("Cache-Control", cacheControl);
				response.setHeader("Content-Disposition", ("attachment; filename=\"" + FileHelper.encodeFileName(request, WebUrlUtil.FILE_URL_TELEMETRY_DATA_FILENAME_DEFAULT) + "\""));
				
				// Copy the data to the response.
				InputStream is = null;
				try {
					TelemetryService ts = ((TelemetryService) SpringContextUtil.getBean("telemetryService"));
					byte[] telemetryData = ts.getLatestTelemetryData(true);
					is = new ByteArrayInputStream(telemetryData);
					FileCopyUtils.copy(is, response.getOutputStream());
				}
				
				catch(Exception e) {
					// If there's any exception, return an error in the
					// response.
					logger.error("Error reading telemetry data:  ", e);
					response.sendError(HttpServletResponse.SC_BAD_REQUEST, NLT.get("file.error.cantTelemetryData"));
				}
				
				finally {
					// Finally, ensure we don't leave a dangling input
					// stream.
					if (null != is) {
						try                    {is.close();}
						catch (IOException io) {/* Ignored. */}
						is = null;
					}
				}
			}
			
			else {
				// No rights to do this operation.
				response.sendError(HttpServletResponse.SC_BAD_REQUEST, NLT.get("error.noRightToDownloadTelemetryData"));
			}
		}
		
		else if (args.length < WebUrlUtil.FILE_URL_ARG_LENGTH) {
			return null;
		}
		
		else {
			try {
				DefinableEntity entity = getEntity(args[WebUrlUtil.FILE_URL_ENTITY_TYPE], Long.valueOf(args[WebUrlUtil.FILE_URL_ENTITY_ID]));
				//Set up the beans needed by the jsps
				FileAttachment fa = null;
				if (args.length > WebUrlUtil.FILE_URL_ARG_LENGTH && entity instanceof FolderEntry) {
					fa = getAttachment((FolderEntry)entity, Arrays.asList(args).subList(WebUrlUtil.FILE_URL_NAME, args.length).toArray());
					//entity may have changed
					if (fa != null) {
						entity = fa.getOwner().getEntity();
					}
				} else {
					fa = getAttachment(entity, args[WebUrlUtil.FILE_URL_NAME], args[WebUrlUtil.FILE_URL_VERSION], args[WebUrlUtil.FILE_URL_FILE_ID]);
				}
				
				// If the entity is an Entry...
				if ((null != entity) && (entity instanceof Entry)) {
					// ...mark it as having been seen.
					getProfileModule().setSeen(null, ((Entry) entity));
				}
	
				if ((entity instanceof FolderEntry) && (((FolderEntry) entity).isPreDeleted())) {
					response.sendError(HttpServletResponse.SC_BAD_REQUEST, NLT.get("file.error.inTrash"));
				}
				
				else if (fa != null) {
					// Can the user download files?
					boolean canDownload = AdminHelper.getEffectiveDownloadSetting(this, RequestContextHolder.getRequestContext().getUser());
					if (canDownload) {
						// Yes!
						String shortFileName = FileUtil.getShortFileName(fa.getFileItem().getName());	
						String contentType = FileUtils.getMimeContentType(getFileTypeMap(), shortFileName);
						
						// Protect against XSS attacks if this is an
						// HTML file.
						contentType = FileUtils.validateDownloadContentType(contentType);
						if (!(contentType.toLowerCase().contains("charset"))) {
							String encoding = SPropsUtil.getString("web.char.encoding", "UTF-8");
							if (MiscUtil.hasString(encoding)) {
								contentType += ("; charset=" + encoding);
							}
						}
						response.setContentType(contentType);
						boolean isHttps = request.getScheme().equalsIgnoreCase("https");
						String cacheControl = "private, max-age=86400";
						if (isHttps) {
							response.setHeader("Pragma", "public");
							cacheControl += ", proxy-revalidate, s-maxage=0";
						}
						response.setHeader("Cache-Control", cacheControl);
						String attachment = "";
						if (FileHelper.checkIfAttachment(contentType)) attachment = "attachment; ";
						response.setHeader("Content-Disposition",
								attachment + "filename=\"" + FileHelper.encodeFileName(request, shortFileName) + "\"");
						response.setHeader("Last-Modified", formatDate(fa.getModification().getDate()));	
						try {
							Binder parent = getBinder(entity);
							if (!fa.isEncrypted()) {
								// The file length cannot be guaranteed
								// if the file is encrypted.  It is
								// better to leave this field off in
								// that case.
								response.setHeader("Content-Length", 
									String.valueOf(FileHelper.getLength(parent, entity, fa)));
							}
							getFileModule().readFile(parent, entity, fa, response.getOutputStream());
							// Mark it in the audit trail.
							getReportModule().addFileInfo(AuditType.view, fa);
						}
						catch(Exception e) {
							logger.error("Error reading file", e);
							response.sendError(HttpServletResponse.SC_BAD_REQUEST, NLT.get("file.error") + ": " + e.getMessage());
						}
					}
					else {
						response.sendError(HttpServletResponse.SC_BAD_REQUEST, NLT.get("file.error.cantDownload"));
					}
				} else {
					response.sendError(HttpServletResponse.SC_BAD_REQUEST, NLT.get("file.error.unknownFile"));
				}
				try {
					response.getOutputStream().flush();
				}
				catch(Exception ignore) {}
	
			} catch(Exception e) {
				//Bad format of url; Tell user that
				logger.error("Error reading file", e);
				response.sendError(HttpServletResponse.SC_BAD_REQUEST, NLT.get("file.error.unknownFile"));

				// Don't cache the response to this request
				{
					Date now;
					
					// Yes
					now = new Date();
					
					response.setDateHeader( "Date", now.getTime() );

					// Set the expiration date to yesterday.
					response.setDateHeader( "Expires", now.getTime() - 86400000L );
					
					// Tell the browser to never cache this file.
					response.setHeader( "Cache-control", "must-revalidate" );
				}
			}
		}
		
		return null;
	}

	/*
	 * Stores the date/time stamp from a FileAttachment on a
	 * ZipArchiveEntry.
	 */
	private static void setDateTimeOnZipArchiveEntry(ZipArchiveEntry zae, Attachment fa) {
		// Extract the date/time stamps for the ZipArchiveEntry from
		// the Attachment.
		Date createDate = ((null == fa.getCreation())     ? null : fa.getCreation().getDate());
		Date modDate    = ((null == fa.getModification()) ? null : fa.getModification().getDate());
		Date accessDate = modDate;
	
		// If we have one...
		if (null != modDate) {
			// ...set the modification time on the ZipArchiveEntry...
//!			zae.setTime(modDate.getTime());
		}

		// ...and set the various extended time stamps, as necessary.
		X5455_ExtendedTimestamp ef = new X5455_ExtendedTimestamp();
		byte efFlags = 0;
		if (null != createDate) {ef.setCreateJavaTime(createDate); efFlags |= X5455_ExtendedTimestamp.CREATE_TIME_BIT;}
		if (null != modDate)    {ef.setModifyJavaTime(modDate   ); efFlags |= X5455_ExtendedTimestamp.MODIFY_TIME_BIT;}
		if (null != accessDate) {ef.setAccessJavaTime(accessDate); efFlags |= X5455_ExtendedTimestamp.ACCESS_TIME_BIT;}
		if (0 != efFlags)       {ef.setFlags(efFlags);                                                                }
		zae.addExtraField(ef);
	}
	
	/*
	 * Adds the primary files from a Collection<FolderEntry> to a zip.
	 */
	private void addCollectionFilesToZip(ZipArchiveOutputStream zipOut, String filePath, Collection<FolderEntry> feCollection, boolean singleByte) {
		// Get the modules we need to do the work.
		BinderModule bm = getBinderModule();
		FileModule   fm = getFileModule();
		
		// Scan the entries whose primary files are to be added to the
		// zip.
		int fileCounter = 1;
		for (FolderEntry fe:  feCollection) {
			// Can we get this entry's primary file attachment?
			FileAttachment fileAtt = MiscUtil.getPrimaryFileAttachment(fe);
			if (null != fileAtt) {
				// Yes!  How do we name the file in the zip?
				String fileName = fileAtt.getFileItem().getName();	// Note: do not translate this name.
				if (!ZIP_ENCODED_FILENAMES) {
					fileName = bm.filename8BitSingleByteOnly(
						fileName,
						("__file" + String.valueOf(fileCounter)),	// For file names that are invalid for a zip.
						singleByte);								// Note: do not translate this name.	
				}
				fileCounter += 1;
				
				// Copy the file into the zip.
				InputStream fileStream = null;
				try {
					fileStream = fm.readFile(fe.getParentBinder(), fe, fileAtt);
					String subFilePath;
					if (MiscUtil.hasString(filePath))
					     subFilePath = (filePath + "/" + fileName);
					else subFilePath = fileName;
					ZipArchiveEntry zae = new ZipArchiveEntry(subFilePath);
					zipOut.putArchiveEntry(zae);
					long startTime = System.nanoTime();
					FileUtil.copy(fileStream, zipOut);
					/*
					 * This was used to measure the time degradation for encrypted files
							String loggerText = "Copying an unencrypted file of length ";
							if (fileAtt.isEncrypted()) {
								loggerText = "Copying an encrypted file of length ";
							}
							logger.info(loggerText + fileAtt.getFileItem().getLengthKB() + 
									"KB took " + (System.nanoTime()-startTime)/1000000.0 + " ms");
					*/
					setDateTimeOnZipArchiveEntry(zae, fileAtt);
					zipOut.closeArchiveEntry();
					// Mark it in the audit trail.
					getReportModule().addFileInfo(AuditType.download, fileAtt);
				} catch (Exception e) {
					logger.error("Error reading file", e);
				}
				finally {
					try {
						if(fileStream != null) {
							fileStream.close();
						}
					}
					catch(IOException ignore) {}
				}
			}
		}
	}
	
	/*
	 * Traverses the contents of a folder and adds it to a zip file.
	 */
	@SuppressWarnings("unchecked")
	private boolean addFolderContentsToZip(HttpServletResponse response, ZipArchiveOutputStream zipOut, String folderPath, Long folderId, ZipCounter runningZipCount, boolean recursive, boolean singleByte) throws IOException {
		// What are the maximum number of files we can zip?
		//
		// Note:  We don't impose a maximum here as it's checked
		//    already in the UI.  I decided to not do it here to so
		//    that if a few files are added between the UI check and
		//    this call, the download will still work.
		int maxFiles = (Integer.MAX_VALUE - 2);
//		int maxFiles = SPropsUtil.getInt("folder.zip.max.files", ObjectKeys.SEARCH_MAX_ZIP_FOLDER_FILES);	// Default is 1000.
		
		// Get the modules we need to do the work.
		BinderModule bm = getBinderModule();
		FolderModule fm = getFolderModule();
		
		// Get the entries from the folder...
		int remainingFiles = (maxFiles - runningZipCount.getFileCount());	// Number of files left before exceeding the maximum.
		List<Long> feIds = getEntryIdsFromFolder(fm, folderId, remainingFiles);
		if (null == feIds) {
			logger.error("ReadFileController.addFolderContentsToZip( Too many files in folder to zip )");
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, NLT.get("zipDownloadUrlError.TooManyFilesInFolderToZip"));
			return false;
		}
		runningZipCount.incrFiles(feIds.size());
		Set<FolderEntry> feSet = fm.getEntries(feIds);

		// ...and add their primary files to the zip.
		addCollectionFilesToZip(zipOut, folderPath, feSet, singleByte);
		
		// Are we exporting folders recursively?
		if (recursive) {
			// Yes!  Get the the IDs of the sub-folders.
			List<String> folderIds = new ArrayList<String>();
			folderIds.add(String.valueOf(folderId));
			Criteria crit = new Criteria();
			crit.add(in(org.kablink.util.search.Constants.DOC_TYPE_FIELD, new String[] {org.kablink.util.search.Constants.DOC_TYPE_BINDER}))
				.add(in(org.kablink.util.search.Constants.BINDERS_PARENT_ID_FIELD, folderIds));
			crit.addOrder(Order.asc(org.kablink.util.search.Constants.BINDER_ID_FIELD));
			Map sfMap = bm.executeSearchQuery(
				crit,
				org.kablink.util.search.Constants.SEARCH_MODE_SELF_CONTAINED_ONLY,
				0,
				(Integer.MAX_VALUE - 1),	// We process all the sub-folders regardless of how many.
				org.kablink.teaming.module.shared.SearchUtils.fieldNamesList(org.kablink.util.search.Constants.DOCID_FIELD));
			List       sfMaps = ((List) sfMap.get(ObjectKeys.SEARCH_ENTRIES)); 
			List<Long> sfIds  = new ArrayList<Long>();
	      	for (Iterator iter = sfMaps.iterator(); iter.hasNext();) {
	      		Map nextSFMap = ((Map) iter.next());
      			sfIds.add(Long.parseLong((String) nextSFMap.get(org.kablink.util.search.Constants.DOCID_FIELD)));
	      	}
			runningZipCount.incrFolders(sfIds.size());

	      	// Are there any sub-folders?
	      	if (!(sfIds.isEmpty())) {
	      		// Yes!  Scan them.
	      		Collection<Folder> subFolders = fm.getFolders(sfIds);
	      		int folderCounter = 1;
	      		for (Folder subFolder:  subFolders) {
	      			// Does the user has rights to read entries from
	      			// this folder?
	      			if (bm.testAccess(subFolder, BinderOperation.readEntries)) {
	    				// Yes!  How do we name the folder in the zip?
	    				String subFolderName = subFolder.getTitle();	// Note: do not translate this name.
	    				if (MiscUtil.hasString(subFolderName)) {
		    				if (!ZIP_ENCODED_FILENAMES) {
		    					subFolderName = bm.filename8BitSingleByteOnly(
		    						subFolderName,
		    						("__folder" + String.valueOf(folderCounter)),	// For folder names that are invalid for a zip.
		    						singleByte);									// Note: do not translate this name.	
		    				}
	    				}
	    				else {
	    					subFolderName = ("__folder" + String.valueOf(folderCounter));
	    				}
	    				folderCounter += 1;
	    				
	    				// Add the sub-folder's contents to the zip.
	    				String subFolderPath;
	    				if (MiscUtil.hasString(folderPath))
	    					 subFolderPath = (folderPath + "/" + subFolderName);
	    				else subFolderPath = subFolderName;
	    				if (!(addFolderContentsToZip(response, zipOut, subFolderPath, subFolder.getId(), runningZipCount, recursive, singleByte))) {
	    					// The recursive call will have logged the
	    					// error and stored it in the response.
	    					// Simply return to indicate the failure.
	    					return false;
	    				}
	      			}
	      		}
	      	}
		}
		
		return true;
	}

	/*
	 * Returns the name of a folder to use for logging an error.
	 */
	private String getFolderNameForError(FolderModule fm, Long folderId) {
		Folder folder;
		try {folder = fm.getFolder(folderId);}
		catch (Exception e) {folder = null;}
		String fName = ((null == folder) ? String.valueOf(folderId) : folder.getTitle());
		return fName;
	}
	
	/*
	 * Constructs a ZipArchiveOutputStream from an HttpServletResponse
	 * and fileName.
	 */
	private ZipArchiveOutputStream buildZipAndSetupResponse(HttpServletResponse response, String fileName) throws IOException {
		response.setContentType(FileUtils.getMimeContentType(mimeTypes, fileName));
		response.setHeader("Cache-Control", "private, max-age=0");
		response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
		OutputStream stream = response.getOutputStream();
		ZipArchiveOutputStream reply = new ZipArchiveOutputStream(stream);
		reply.setUseZip64(Zip64Mode.Always);

		if (UTF8_ENCODE_ZIPS) {
			reply.setEncoding("UTF-8");
		}
		else {
			// Standard zip encoding is cp437.  (Needed when characters
			// are outside the ASCII range.)
			reply.setEncoding(               "cp437");
			reply.setFallbackToUTF8(         true   );
			reply.setUseLanguageEncodingFlag(true   );
		}
		reply.setCreateUnicodeExtraFields(ZipArchiveOutputStream.UnicodeExtraFieldPolicy.ALWAYS);
		
		return reply;
	}

	/*
	 * Returns a List<Long> of the IDs of the entries contained in a
	 * folder.
	 * 
	 * If the number of remaining entries exceeds maxEntries, null is
	 * returned.
	 */
	@SuppressWarnings("unchecked")
	private List<Long> getEntryIdsFromFolder(FolderModule fm, Long folderId, int maxEntries) {
		Map options = new HashMap();
		options.put(ObjectKeys.SEARCH_OFFSET,   new Integer(0)           );
		options.put(ObjectKeys.SEARCH_MAX_HITS, new Integer(maxEntries + 1));				// We ask for 1 more than requested...
		Map        folderEntries = fm.getEntries(folderId, options);						// ...
		Integer    total  = ((Integer) folderEntries.get(ObjectKeys.SEARCH_COUNT_TOTAL));	// ...
		if (total > maxEntries) {															// ...so we can detect when there are too many.
			return null;
		}
		List       searchEntries = ((List) folderEntries.get(ObjectKeys.SEARCH_ENTRIES));
		List<Long> entryIds      = new ArrayList<Long>();
		int c = searchEntries.size();
		for (int i = 0; i < c; i += 1) {
			Map  searchEntry = ((Map) searchEntries.get(i));
			Long entryId     = Long.valueOf(searchEntry.get(org.kablink.util.search.Constants.DOCID_FIELD).toString());
			entryIds.add(entryId);
		}
		return entryIds;
	}
}
