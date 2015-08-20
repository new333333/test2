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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.bind.DatatypeConverter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.kablink.teaming.BinderQuotaException;
import org.kablink.teaming.DataQuotaException;
import org.kablink.teaming.FileSizeLimitException;
import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.antivirus.VirusDetectedError;
import org.kablink.teaming.antivirus.VirusDetectedException;
import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.dao.CoreDao;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.Definition;
import org.kablink.teaming.domain.FileAttachment;
import org.kablink.teaming.domain.Folder;
import org.kablink.teaming.domain.FolderEntry;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.domain.ZoneConfig;
import org.kablink.teaming.domain.ZoneInfo;
import org.kablink.teaming.gwt.client.GwtTeamingException;
import org.kablink.teaming.gwt.client.binderviews.folderdata.FileBlob;
import org.kablink.teaming.gwt.client.binderviews.folderdata.FileBlob.ReadType;
import org.kablink.teaming.gwt.client.rpc.shared.BooleanRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.FileConflictsInfoRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.Html5SpecsRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.StringRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.ValidateUploadsRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.ErrorListRpcResponseData.ErrorInfo;
import org.kablink.teaming.gwt.client.rpc.shared.FileConflictsInfoRpcResponseData.DisplayInfo;
import org.kablink.teaming.gwt.client.util.BinderInfo;
import org.kablink.teaming.gwt.client.util.UploadInfo;
import org.kablink.teaming.module.admin.AdminModule;
import org.kablink.teaming.module.binder.BinderModule;
import org.kablink.teaming.module.file.WriteFilesException;
import org.kablink.teaming.module.folder.FolderModule;
import org.kablink.teaming.module.profile.ProfileModule;
import org.kablink.teaming.module.shared.FolderUtils;
import org.kablink.teaming.module.shared.MapInputData;
import org.kablink.teaming.security.AccessControlException;
import org.kablink.teaming.util.AllModulesInjected;
import org.kablink.teaming.util.ExtendedMultipartFile;
import org.kablink.teaming.util.FileIconsHelper;
import org.kablink.teaming.util.IconSize;
import org.kablink.teaming.util.NLT;
import org.kablink.teaming.util.SpringContextUtil;
import org.kablink.teaming.util.TempFileUtil;
import org.kablink.teaming.web.util.BinderHelper;
import org.kablink.teaming.web.util.DefinitionHelper;
import org.kablink.teaming.web.util.Html5Helper;
import org.kablink.teaming.web.util.MiscUtil;
import org.kablink.teaming.web.util.WebHelper;
import org.kablink.util.Validator;

import org.springframework.web.multipart.MultipartFile;

/**
 * Helper methods for the GWT UI server code that services requests
 * dealing with the HTML5 file uploader.
 *
 * @author drfoster@novell.com
 */
public class GwtHtml5Helper {
	protected static Log m_logger = LogFactory.getLog(GwtHtml5Helper.class);

	// Used in various file size calculations, ...
	private final static long MEGABYTES = (1024l * 1024l);
	
	/*
	 * Class constructor that prevents this class from being
	 * instantiated.
	 */
	private GwtHtml5Helper() {
		// Nothing to do.
	}
	
	/**
	 * Aborts any file upload in progress.
	 * 
	 * @param bs
	 * @param request
	 * @param folderInfo
	 * @param fileBlob
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException
	 */
	public static BooleanRpcResponseData abortFileUpload(AllModulesInjected bs, HttpServletRequest request, BinderInfo folderInfo, FileBlob fileBlob) throws GwtTeamingException {
		try {
			// Do we have an upload filename cached in the session?
			HttpSession session     = WebHelper.getRequiredSession(request);
			String      uploadFName = getUploadFileCacheKey(fileBlob);
			String      fileName    = ((String) session.getAttribute(uploadFName));
			if (MiscUtil.hasString(fileName)) {
				// Yes!  Remove it...
				session.removeAttribute(uploadFName);
				try {
					// ...and if we can access the temporary file for
					// ...it...
					File tempFile = TempFileUtil.getHtml5UploaderTempFileByName(fileName);
					if (null != tempFile) {
						// ...delete that.
						tempFile.delete();
					}
				}
				
				catch (Throwable t) {
					// Ignore.
				}
			}

			// Stop profiling this upload.
			stopUploadProfiler(request, "abortFileUpload", fileBlob);
			
			return new BooleanRpcResponseData(true);
		}
		
		catch (Exception e) {
			// Convert the exception to a GwtTeamingException and throw
			// that.
			throw
				GwtLogHelper.getGwtClientException(
					m_logger,
					e,
					"GwtHtml5Helper.abortFileUpload( SOURCE EXCEPTION ):  ");
		}
	}

	/*
	 * Writes debug information about a file blob to the system log.
	 */
	private static void debugTraceBlob(FileBlob fileBlob, String methodName, String traceHead, String traceTail, boolean lastBlob) {
		if (GwtLogHelper.isDebugEnabled(m_logger)) {
			String blobHash = fileBlob.getBlobMD5Hash();
			if (null == blobHash) {
				blobHash = "***";
			}
			String	dump  = (traceHead + ":  '" + fileBlob.getFileName() + "' (fSize:" + fileBlob.getFileSize() + ", bStart:" + fileBlob.getBlobStart() + ", bSize:" + fileBlob.getBlobSize() + ", last:" + lastBlob + ", md5Hash:" + blobHash + ", uploadId:" + fileBlob.getUploadId() + ")");
			boolean hasTail = MiscUtil.hasString(traceTail);
			dump = ("GwtHtml5Helper." + methodName + "( " + dump + " )" + (hasTail ? ":  " + traceTail : ""));
			int dataLen;
			if (fileBlob.getReadType().isArrayBuffer()) {
				byte[] data = fileBlob.getBlobDataBytes(); 
				dataLen =  ((null == data )? 0 : data.length);
			}
			else {
				String data = fileBlob.getBlobDataString();
				dataLen = ((null == data) ? 0 : data.length());
			}
			dump += ("\n\nData Uploaded:  " + dataLen + (fileBlob.isBlobBase64() ? " base64 encoded" : "") + " bytes."); 
			GwtLogHelper.debug(m_logger, dump);
		}
	}
	
	private static void debugTraceBlob(FileBlob fileBlob, String methodName, String traceHead, boolean lastBlob) {
		// Always use the initial form of the method.
		debugTraceBlob(fileBlob, methodName, traceHead, null, lastBlob);
	}

	/*
	 * Deletes the temporary file used by the uploader, ignoring any
	 * errors.
	 */
	private static void deleteTempFile(File tempFile) {
		// If we have a temporary file to delete...
		if (null != tempFile) {
			// ...delete it.
			try {tempFile.delete();}
			catch (Throwable t) {/* Ignore. */}
		}
	}
	
	/*
	 * Use Spring to access a CoreDao object. 
	 */
	private static CoreDao getCoreDao() {
		return ((CoreDao) SpringContextUtil.getBean("coreDao"));
	}

	/**
	 * Returns a FileConflictsInfoRpcResponseData object containing
	 * information for rendering conflicts information in a dialog.
	 * 
	 * @param bs
	 * @param request
	 * @param folderInfo
	 * @param fileConflicts
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException
	 */
	public static FileConflictsInfoRpcResponseData getFileConflictsInfo(AllModulesInjected bs, HttpServletRequest request, BinderInfo folderInfo, List<UploadInfo> fileConflicts) throws GwtTeamingException {
		try {
			// Allocate a FileConflictsInfoRpcResponseData to return
			// the information in.
			FileConflictsInfoRpcResponseData reply = new FileConflictsInfoRpcResponseData();
			
			if (folderInfo.isBinderEmailTemplates()) {
				GwtEmailTemplatesHelper.getFileConflictsInfo(bs, request, folderInfo, fileConflicts,  reply);
			}
			
			else {
				// Add a DisplayInfo for the folder to the reply.
				Folder folder = bs.getFolderModule().getFolder(folderInfo.getBinderIdAsLong());
				String name = folder.getTitle();
				DisplayInfo di = new DisplayInfo(
					(MiscUtil.hasString(name) ? name : ("--" + NLT.get("entry.noTitle") + "--")),
					folder.getPathName(),
					folder.getIconName(IconSize.MEDIUM));
				reply.setFolderDisplay(di);
	
				// If we have some file conflicts...
				if (MiscUtil.hasItems(fileConflicts)) {
					// ...scan them...
					for (UploadInfo fileConflict:  fileConflicts) {
						// ...for for those that represent a file...
						if (fileConflict.isFile()) {
							// ...add a DisplayInfo for them to the reply.
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
			}
			
			// If we get here, reply refers to the
			// FileConflictsInfoRpcResponseData with the information
			// for running the conflicts dialog.  Return it.
			return reply;
		}
		
		catch (Exception e) {
			// Convert the exception to a GwtTeamingException and throw
			// that.
			throw
				GwtLogHelper.getGwtClientException(
					m_logger,
					e,
					"GwtHtml5Helper.getFileConflictsInfo( SOURCE EXCEPTION ):  ");
		}
	}

	/**
	 * Returns an Html5SpecsRpcResponseData object built using the
	 * parameters from the ssf*.properties.
	 * 
	 * @param bs
	 * @param request
	 * 
	 * @return
	 */
	public static Html5SpecsRpcResponseData getHtml5UploadSpecs(AllModulesInjected bs, HttpServletRequest request) {
		boolean encode          = Html5Helper.isHtml5UploadEncode();
		boolean md5HashValidate = Html5Helper.isHtml5UploadMd5HashValidate();
		
		Html5SpecsRpcResponseData reply;
		if (Html5Helper.isHtml5UploadVariableBlobs()) {
			reply = new Html5SpecsRpcResponseData(
				encode,
				md5HashValidate,
				Html5Helper.getHtml5VariableBlobsPerFile(),
				Html5Helper.getHtml5VariableBlobsMinBlobSize(),
				Html5Helper.getHtml5VariableBlobsMaxBlobSize());
		}
		
		else {
			reply = new Html5SpecsRpcResponseData(
				encode,
				md5HashValidate,
				Html5Helper.getHtml5FixedBlobSize());
		}
		
		return reply;
	}
	
	/*
	 * Returns the key to use for storing file upload information
	 * in the session cache.
	 */
	private static String getUploadFileCacheKey(FileBlob fileBlob) {
		return (Html5Helper.UPLOAD_FILE_PREFIX + String.valueOf(GwtServerHelper.getCurrentUserId()) + "." + String.valueOf(fileBlob.getUploadId()) + ".");
	}
	
	/*
	 * Returns the key to use for profiling a file upload.
	 */
	private static String getUploadProfilerKey(FileBlob fileBlob) {
		return (fileBlob.getFileName() + ":" + String.valueOf(fileBlob.getUploadId()));
	}

	/*
	 * Starts profiling a file upload.
	 */
	private static void startUploadProfiler(HttpServletRequest request, FileBlob fileBlob) {
		// If profiling is enabled...
		if (m_logger.isInfoEnabled()) {
			// ...write the start time/date stamp to the session cache.
			HttpSession session = WebHelper.getRequiredSession(request);
			session.setAttribute(getUploadProfilerKey(fileBlob), new Date());
		}
	}
	
	/*
	 * Stops profiling a file upload.
	 */
	private static void stopUploadProfiler(HttpServletRequest request, String stoppingMethod, FileBlob fileBlob) {
		// If profiling is enabled...
		if (m_logger.isInfoEnabled()) {
			// ...remove and setting from the session cache...
			HttpSession session    = WebHelper.getRequiredSession(request);
			String      uploadPKey = getUploadProfilerKey(fileBlob);
			Date uploadStart = ((Date) session.getAttribute(uploadPKey));
			session.removeAttribute(uploadPKey);
			if (null != uploadStart) {
				// ...and log the information.
				m_logger.info("GwtHtml5Helper." + stoppingMethod + "( '" + uploadPKey + "' ):  Time:  " + (new Date().getTime() - uploadStart.getTime()) + "ms");
			}
		}
	}
	
	/**
	 * Uploads a file blob.
	 * 
	 * If the blob is the last one for the file, the file entry is
	 * created.  Otherwise, the blob is cached while we await
	 * additional blobs for it.
	 *
	 * @param bs
	 * @param request
	 * @param folderInfo
	 * @param fileBlob
	 * @param lastBlob
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException
	 */
	@SuppressWarnings("unchecked")
	public static StringRpcResponseData uploadFileBlob(AllModulesInjected bs, HttpServletRequest request, BinderInfo folderInfo, FileBlob fileBlob, boolean lastBlob) throws GwtTeamingException {
		try {
			// Trace what we read to the log.
			debugTraceBlob(fileBlob, "uploadFileBlob", "Uploaded", lastBlob);

			// Is this the first blob of a file?
			StringRpcResponseData reply = null;
			HttpSession session = WebHelper.getRequiredSession(request);
			boolean firstBlob = (0l == fileBlob.getBlobStart());
			File tempFile;
			String uploadFName = getUploadFileCacheKey(fileBlob);
			if (firstBlob) {
				// Yes!  Create a new temporary file for it and store
				// the file handle in the session cache.  The format of
				// the prefix used is:  'uploadFile.<userId>.<timestamp>.'
				tempFile = TempFileUtil.createHtml5UploaderTempFile(uploadFName);
				if (!lastBlob) {
					session.setAttribute(uploadFName, tempFile.getName());
				}

				// Start profiling this upload.
				startUploadProfiler(request, fileBlob);
			}
			
			else {
				// No, this isn't the first blob of a file!  Can we
				// access the temporary file from the handle stored
				// in the session cache.
				String tempFName = ((String) session.getAttribute(uploadFName));
				boolean hasTempFName = MiscUtil.hasString(tempFName);
				tempFile = (hasTempFName ? TempFileUtil.getHtml5UploaderTempFileByName(tempFName) : null);
				if (null == tempFile) {
					// No!  Generate an error to that affect.
					reply = new StringRpcResponseData();
					reply.setStringValue(NLT.get("binder.add.files.html5.upload.noTempAccess", new String[]{fileBlob.getFileName()}));
					lastBlob = true;	// Set true so things get properly cleaned up.
				}
				if (lastBlob && hasTempFName) {
					session.removeAttribute(uploadFName);
				}
			}

			// Have we generated an error yet?
			if (null == reply) {
				// No!  Get the data for the blob as a byte[].
				String blobDataString;
				byte[] blobDataBytes;
				ReadType blobReadType = fileBlob.getReadType();
				if (blobReadType.isArrayBuffer()) {
					blobDataString = null;
					blobDataBytes  = fileBlob.getBlobDataBytes();
				}
				else {
					blobDataString = fileBlob.getBlobDataString();
					if      (null == blobDataString)   blobDataString = "";
					else if (blobReadType.isDataUrl()) blobDataString = FileBlob.fixDataUrlString(blobDataString);
					blobDataBytes = blobDataString.getBytes();
				}
				
				// Does the MD5 hash calculated on the blob we just
				// received match the MD5 hash that came with it? 
				String blobHashReceived = fileBlob.getBlobMD5Hash();
				boolean blobHashValid = (null == blobHashReceived);
				if (!blobHashValid) {
					String blobHashCalculated = MiscUtil.getMD5Hash(blobDataBytes);
					blobHashValid = blobHashCalculated.equals(blobHashReceived);
				}
				if (!blobHashValid) {
					// No!  Then the data is corrupt.  Return the error
					// to the user.
					reply = new StringRpcResponseData();
					reply.setStringValue(NLT.get("binder.add.files.html5.upload.corrupt"));
					deleteTempFile(tempFile);
				}
				else {
					FileOutputStream fo = null;
					try {
						// Yes!  The MD5 hashes match!  If the data is
						// base64 encoded...
						if (fileBlob.isBlobBase64()) {
							// ...decode it...
							if (null == blobDataString) {
								blobDataString = new String(blobDataBytes);
							}
							blobDataBytes = DatatypeConverter.parseBase64Binary(blobDataString);
						}
						
						// ...and write it to the file.
						fo = new FileOutputStream(tempFile, (!firstBlob));
						fo.write(blobDataBytes);
					}
					
					catch (Exception e) {
						// Return the error to the user...
						reply = new StringRpcResponseData();
						reply.setStringValue(NLT.get("binder.add.files.html5.upload.error", new String[]{e.getLocalizedMessage()}));
						
						// ...close the stream and delete the file...
						if (null != fo) {
							try {fo.close();}
							catch (Throwable t) {/* Ignored. */}
							fo = null;
						}
						deleteTempFile(tempFile);
						
						// ...and log the error.
						GwtLogHelper.error(m_logger, "GwtHtml5Helper.uploadFileBlob( File name:  '" + fileBlob.getFileName() + "', EXCEPTION:1 ):  ", e);
					}
					
					finally {
						// Ensure we've closed the stream.
						if (null != fo) {
							try {
								fo.close();
							}
							catch (IOException ioe) {
								// We may get an IOException here if we
								// run out of disk space when we close
								// it.  We need to account for that so
								// so the user sees something
								// meaningful.
								if (null == reply) {
									reply = new StringRpcResponseData();
									reply.setStringValue(NLT.get("binder.add.files.html5.upload.error", new String[]{ioe.getLocalizedMessage()}));
								}
								deleteTempFile(tempFile);
							}
							fo = null;
						}
					}
				}
			}

			// Did we just successfully write the last blob of the file
			// to the temporary file we use to cache it while we stream
			// it to the server?
			if ((null == reply) && lastBlob) {
				// Yes!  Are we uploading a customized email template?
				if (folderInfo.isBinderEmailTemplates()) {
					// Yes!  Copy the temporary file to the appropriate
					// place.
					String          fileName = fileBlob.getFileName();
			    	FileInputStream fi       = new FileInputStream(tempFile);
					try {
						GwtEmailTemplatesHelper.copyCustomizedEmailTemplate(fi, fileName);
					}
					
	    	    	catch (Exception ex) {
	    				reply = new StringRpcResponseData();
	    				reply.setStringValue(NLT.get("entry.uploadError.emailTemplate", new String[]{fileName, ex.getLocalizedMessage()}));
	    				
	    				// Log the error.
						GwtLogHelper.error(m_logger, "GwtHtml5Helper.uploadFileBlob( File name:  '" + fileName + "', EXCEPTION:2 ):  ", ex);
	    	    	}
					
					finally {
						// Ensure we've closed the stream.
	    	    		if (null != fi) {
	    	    			fi.close();
	    	    			fi = null;
	    	    		}
	    	    		
	    	    		// ...and delete the temporary file.
						deleteTempFile(tempFile);
					}
				}
				
				else {
					// No, we aren't uploading a customized email
					// template!  We need to create the entry for the
					// file in the target folder.
					FolderModule	fm       = bs.getFolderModule();
					ProfileModule	pm       = bs.getProfileModule();
					Long			folderId = folderInfo.getBinderIdAsLong();
			    	Folder			folder   = fm.getFolder(folderId);
			    	FileInputStream fi       = new FileInputStream(tempFile);
			    	try {
			    		// What do we know about the file?
						String	fileName  = fileBlob.getFileName();
		    	    	Date	modDate;
		    	    	Long	fileUTCMS = fileBlob.getFileUTCMS();
		    	    	if (null == fileUTCMS)
		    	    	     modDate = null;
		    	    	else modDate = new Date(fileUTCMS);
		
		    	    	// Are we creating an entry in a library
		    	    	// folder?
		    	    	if (folder.isLibrary()) {
		        	    	// Yes!  If there's an existing entry...
		        	    	FolderEntry existingEntry = fm.getLibraryFolderEntryByFileName(folder, fileName);
		    	    		if (null != existingEntry) {
		    	    			// ...we modify it...
		        	    		FolderUtils.modifyLibraryEntry(existingEntry, fileName, null, fi, null, modDate, null, true, null, null);
		        				pm.setSeen(null, existingEntry);
		        	    	}
		    	    		
		    	    		else {
		    	    			// ...otherwise, we create a new one.
		        	    		FolderEntry fe = FolderUtils.createLibraryEntry(folder, fileName, fi, modDate, null, true);
		        				pm.setSeen(null, fe);
		        	    	}
		    	    	}
		    	    	else {
		        	    	// No, we aren't creating an entry in a
		    	    		// library folder!  Setup an input data map
		    	    		// using the file's name as the title...
							Map entryNameOnly = new HashMap();
		        	    	entryNameOnly.put(ObjectKeys.FIELD_ENTITY_TITLE, fileName);
		        	    	MapInputData inputData = new MapInputData(entryNameOnly);
		
		        	    	// ...if there's an existing entry, we'll
		        	    	// ...modify it, otherwise, we'll create a
		        	    	// ...new one...
		    				Long		zoneId   = RequestContextHolder.getRequestContext().getZoneId();
		    				ZoneInfo	zi       = bs.getZoneModule().getZoneInfo(zoneId);
		    				String		zoneUUID = zi.getId();
							Set<FolderEntry> feSet = fm.getFolderEntryByNormalizedTitle(folderId, WebHelper.getNormalizedTitle(fileName), zoneUUID);
							FolderEntry existingEntry;
							if (MiscUtil.hasItems(feSet))
							     existingEntry = feSet.iterator().next();
							else existingEntry = null;
							boolean useExisting = (null != existingEntry);
							
		        	    	// ...get the definition to use for the
							// ...entry and file...
		    	        	Definition fileDef;
							if (useExisting)
							     fileDef = DefinitionHelper.getDefinition(existingEntry.getEntryDefId());
							else fileDef = folder.getDefaultFileEntryDef();
		    	        	String fileDefId = ((fileDef != null) ? fileDef.getId() : null);
		    	        	
		        			// ...wrap the input stream in a data
		    	        	// ...structure suitable for the business
		    	        	// ...module...
		        			MultipartFile mf          = new ExtendedMultipartFile(fileName, fi, modDate);
		        			Map           oneFileMap  = new HashMap();
		        			String        elementName = FolderUtils.getDefinitionElementNameForNonMirroredFile(fileDef);
		        			oneFileMap.put(elementName, mf);
		
		        			// ...modify or create the entry...
							if (useExisting)
							                     fm.modifyEntry(folderId, existingEntry.getId(), inputData, oneFileMap, null, null, null);
							else existingEntry = fm.addEntry(   folderId, fileDefId,             inputData, oneFileMap,             null);
		
							// ...and mark it read.
		    				pm.setSeen(null, existingEntry);
		    	    	}
	    	    	}
	    	    	
	    	    	catch (Exception ex) {
	    	    		String errMsg;
		    	    	if      (ex instanceof AccessControlException) errMsg = NLT.get("entry.duplicateFileInLibrary2"           );
		    	    	else if (ex instanceof BinderQuotaException)   errMsg = NLT.get("entry.uploadError.binderQuotaException"  );
		    	    	else if (ex instanceof DataQuotaException)     errMsg = NLT.get("entry.uploadError.dataQuotaException"    );
		    	    	else if (ex instanceof FileSizeLimitException) errMsg = NLT.get("entry.uploadError.fileSizeLimitException");
						else if (ex instanceof WriteFilesException)    errMsg = NLT.get("entry.uploadError.writeFilesException", new String[]{ex.getLocalizedMessage()});
		    	    	
		    	    	else {
		    	    		errMsg = NLT.get("entry.uploadError.unknownError", new String[]{ex.getLocalizedMessage()});
							if (ex instanceof VirusDetectedException) {
								List<VirusDetectedError> errors = ((VirusDetectedException) ex).getErrors();
								if (MiscUtil.hasItems(errors)) {
									// Since we process one file at a time
									// here, there can be at most one error.
									errMsg = MiscUtil.getLocalizedVirusDetectedErrorString(errors.get(0));
								}
							}
		    	    	}
		    	    	
	    				reply = new StringRpcResponseData();
	    				reply.setStringValue(errMsg);
	    				
	    				// Log the error.
						GwtLogHelper.error(m_logger, "GwtHtml5Helper.uploadFileBlob( File name:  '" + fileBlob.getFileName() + "', EXCEPTION:3 ):  ", ex);
	    	    	}
	    	    	
	    	    	finally {
						// Ensure we've closed the stream.
	    	    		if (null != fi) {
	    	    			fi.close();
	    	    			fi = null;
	    	    		}
	    	    		
	    	    		// ...and delete the temporary file.
						deleteTempFile(tempFile);
	    	    	}
				}
			}

			// If this is the last upload blob...
			if (lastBlob) {
				// ...stop profiling this upload.
				stopUploadProfiler(request, "uploadFileBlob", fileBlob);
			}

			// Return an empty string response or the one we
			// constructed containing any error we encountered during
			// the upload.
			return ((null == reply) ? new StringRpcResponseData() : reply);
		}
		
		catch (Exception e) {
			// Convert the exception to a GwtTeamingException and throw
			// that.
			throw
				GwtLogHelper.getGwtClientException(
					m_logger,
					e,
					"GwtHtml5Helper.uploadFileBlob( SOURCE EXCEPTION ):  ");
		}
	}
	
	/**
	 * Validates that the user can upload the files/folders in
	 * List<UploadInfo> of things pending upload.
	 * 
	 * @param bs
	 * @param request
	 * @param folderInfo
	 * @param uploads
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException
	 */
	public static ValidateUploadsRpcResponseData validateUploads(AllModulesInjected bs, HttpServletRequest request, BinderInfo folderInfo, List<UploadInfo> uploads) throws GwtTeamingException {
		try {
			// Allocate validation response we can return.
			ValidateUploadsRpcResponseData reply = new ValidateUploadsRpcResponseData(new ArrayList<ErrorInfo>());
			
			// We're we given anything to validate?
			if (MiscUtil.hasItems(uploads)) {
				// Yes!  Are we validating uploads for email templates?
				if (folderInfo.isBinderEmailTemplates()) {
					// Yes!  Let the email templates helper do the
					// validation.
					GwtEmailTemplatesHelper.validateUploads(bs, request, folderInfo, uploads, reply);
				}
				
				else {
					// No, we aren't validating uploads for email
					// templates!  Access the objects we need to
					// perform the analysis.
					AdminModule		am                    = bs.getAdminModule();
					BinderModule	bm                    = bs.getBinderModule();
					FolderModule	fm                    = bs.getFolderModule();
					Long			folderId			  = folderInfo.getBinderIdAsLong();
					Folder			folder                = fm.getFolder(folderId);
					Long			binderFileSizeLimit   = bm.getBinderMaxFileSize(folder);
					Long			binderFileSizeLimitMB = null;
					Long			userFileSizeLimit     = am.getUserFileSizeLimit();
					Long			userFileSizeLimitMB   = null;
					Long			zoneId                = RequestContextHolder.getRequestContext().getZoneId();
					ZoneInfo		zi                    = bs.getZoneModule().getZoneInfo(zoneId);
					String			zoneUUID              = zi.getId();
					
					// What do we need to check?
					boolean	enforceQuotas            = ((!(folder.isMirrored())) && (!(folder.isAclExternallyControlled())));
					boolean	checkBinderQuotas        = (enforceQuotas && bm.isBinderDiskQuotaEnabled());
					boolean	checkUserQuotas          = (enforceQuotas && am.isQuotaEnabled());
					boolean	checkBinderFileSizeLimit = ((null != binderFileSizeLimit) && (0 < binderFileSizeLimit));
					boolean	checkUserFileSizeLimit   = ((null != userFileSizeLimit) && (0 < userFileSizeLimit));
					if (checkBinderFileSizeLimit) {
						binderFileSizeLimitMB = (binderFileSizeLimit * MEGABYTES);
					}
					if (checkUserFileSizeLimit) {
						userFileSizeLimitMB = (userFileSizeLimit * MEGABYTES);
					}
					
					// Do we need to worry about quotas?
					if (checkBinderQuotas || checkUserQuotas || checkBinderFileSizeLimit || checkUserFileSizeLimit) {
						// Yes!  Scan the UploadInfo's.
						long totalSize = 0l;
						for (UploadInfo upload:  uploads) {
							// Is this upload a file?
							if (upload.isFile()) {
								// Yes!  Does its size exceed the
								// binder's file size limit?
								long size = upload.getSize();
								if (checkBinderFileSizeLimit && (size > binderFileSizeLimitMB)) {
									// Yes!  Add an appropriate error the
									// reply.
									reply.addError(NLT.get("validateUploadError.quotaExceeded.binder", new String[]{upload.getName(), String.valueOf(binderFileSizeLimit)}));
								}
								
								// Does its size exceed the user's file
								// size limit?
								if (checkUserFileSizeLimit && (size > userFileSizeLimitMB)) {
									// Yes!  Add an appropriate error the
									// reply.
									reply.addError(NLT.get("validateUploadError.quotaExceeded.file", new String[]{upload.getName(), String.valueOf(userFileSizeLimit)}));
								}
								
								// Add this file's size to the running
								// total.
								totalSize += size;
							}
						}
	
						// Will the total size of all the files being
						// uploaded exceed this binder's remaining
						// quota?
						if (checkBinderQuotas && (!(bm.isBinderDiskQuotaOk(folder, totalSize)))) {
							// Yes!  Add an appropriate error the
							// reply.
							reply.addError(NLT.get("validateUploadError.quotaExceeded.folder", new String[]{String.valueOf(bm.getMinBinderQuotaLeft(folder) / MEGABYTES)}));
						}
		
						// Do we need to check quotas assigned to this
						// user or their groups?
						if (checkUserQuotas) {
							// Yes!  Do we need to check the total
							// upload size against this user's quota?
							User user = GwtServerHelper.getCurrentUser();
							long userQuota = user.getDiskQuota();
							if (0 == userQuota) {
								userQuota = user.getMaxGroupsQuota();
								if (0 == userQuota) {
									ZoneConfig zc = getCoreDao().loadZoneConfig(RequestContextHolder.getRequestContext().getZoneId());
									userQuota = zc.getDiskQuotaUserDefault();
								}
							}
							long userQuotaMB       = (userQuota * MEGABYTES);
							Long userDiskSpaceUsed = user.getDiskSpaceUsed();
							if ((0l < userQuota) && (null != userDiskSpaceUsed)) {
								// Yes!  Does it exceed the user's
								// quota?
								if ((totalSize + userDiskSpaceUsed) > userQuotaMB) {
									// Yes!  Add an appropriate error
									// the reply.
									reply.addError(NLT.get("validateUploadError.quotaExceeded.user", new String[]{String.valueOf(userQuota)}));
								}
							}
						}
	
						// If we detected any quota errors...
						if (reply.hasErrors()) {
							// ...we'll stop with the analysis and
							// ...return what we've got.
							return reply;
						}
					}
	
					// Scan the UploadInfo's again.
					for (UploadInfo upload:  uploads) {
						// Is this a file upload?
						String name = upload.getName();
						if (upload.isFile()) {
							// Yes!  Does it contain a valid name?
							if (Validator.containsPathCharacters(name)) {
								reply.addError(NLT.get("validateUploadError.invalidName.file", new String[]{name}));
							}
						}
						
						else {
							// No, it must be a folder upload!  Does it
							// contain a valid name?
							if (!(BinderHelper.isBinderNameLegal(name))) {
								reply.addError(NLT.get("validateUploadError.invalidName.folder", new String[]{name}));
							}
						}
					}
					
					// Scan the UploadInfo's again.
					for (UploadInfo upload:  uploads) {
						// Is this upload a file?
						if (!(upload.isFile())) {
							// No!  Skip it.
							continue;
						}
						
						// Does the folder contain an entry with this
						// name?
						String uploadFName = upload.getName();
						
						// Do we care about unique file names?
						if (folder.isLibrary()) {
							// Yes!  Does an entry with this file name
							// already exist?
							FolderEntry fe = fm.getLibraryFolderEntryByFileName(folder, uploadFName);
							if (null != fe) {
								// Yes!  Track it as a duplicate.
								reply.addDuplicate(upload);
								continue;
							}
							
							// Does a folder with this name exist?
							Binder b = bm.getBinderByParentAndTitle(folderId, uploadFName);
							if (null != b) {
								// Yes!  Thats an error and the file
								// can't be uploaded.
								reply.addError(NLT.get("validateUploadError.nameExistsAsFolder", new String[]{uploadFName}));
								continue;
							}
						}
						
						// Do we care about unique titles?
						if (folder.isUniqueTitles()) {
							// Yes!  Does an entry with this title
							// already exist?
							Set<FolderEntry> feSet = fm.getFolderEntryByNormalizedTitle(folderId, WebHelper.getNormalizedTitle(uploadFName), zoneUUID);
							if (MiscUtil.hasItems(feSet)) {
								// Yes!  Track it as a duplicate.
								reply.addDuplicate(upload);
								continue;
							}
						}
						
						// Scan the files attached to the folder.
						for (FileAttachment fa:  folder.getFileAttachments()) {
							// Does this attachment's file exist and is
							// it a match?
							if (fa.getFileExists() && fa.getFileItem().getName().equals(uploadFName)) {
								// Yes!  Track it as a duplicate.
								reply.addDuplicate(upload);
								break;
							}
						}
					}
				}
			}
			
			// If we get here, reply refers to a
			// ValidateUploadsRpcResponseData containing any errors and
			// information about any duplicates that we encountered.
			// Return it.
			return reply;
		}
		
		catch (Exception e) {
			// Convert the exception to a GwtTeamingException and throw
			// that.
			throw
				GwtLogHelper.getGwtClientException(
					m_logger,
					e,
					"GwtHtml5Helper.validateUploads( SOURCE EXCEPTION ):  ");
		}
	}
}
