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
package org.kablink.teaming.module.shared;

import java.io.InputStream;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.activation.FileTypeMap;

import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.DefinableEntity;
import org.kablink.teaming.domain.FolderEntry;
import org.kablink.teaming.domain.NoFileVersionByTheIdException;
import org.kablink.teaming.domain.Principal;
import org.kablink.teaming.domain.ReservedByAnotherUserException;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.domain.EntityIdentifier.EntityType;
import org.kablink.teaming.domain.FileAttachment;
import org.kablink.teaming.domain.VersionAttachment;
import org.kablink.teaming.module.admin.AdminModule;
import org.kablink.teaming.module.binder.BinderModule;
import org.kablink.teaming.module.binder.impl.WriteEntryDataException;
import org.kablink.teaming.module.file.FileModule;
import org.kablink.teaming.module.file.WriteFilesException;
import org.kablink.teaming.module.folder.FolderModule;
import org.kablink.teaming.module.profile.ProfileModule;
import org.kablink.teaming.security.AccessControlException;
import org.kablink.teaming.security.AccessControlManager;
import org.kablink.teaming.security.function.WorkArea;
import org.kablink.teaming.security.function.WorkAreaOperation;
import org.kablink.teaming.util.ExtendedMultipartFile;
import org.kablink.teaming.util.NoContentMultipartFile;
import org.kablink.teaming.util.SPropsUtil;
import org.kablink.teaming.util.SimpleMultipartFile;
import org.kablink.teaming.util.SpringContextUtil;
import org.kablink.teaming.util.Utils;
import org.kablink.util.Validator;

import org.springframework.web.multipart.MultipartFile;

/**
 * ?
 * 
 * @author ?
 */
@SuppressWarnings({"unchecked", "unused"})
public class FileUtils {
	public final static String	DEFAULT_MIME_CONTENT_TYPE	= "application/octet-stream";

	public static void setFileVersionAging(DefinableEntity entity) {
		Binder binder = entity.getParentBinder();
		if (!entity.getEntityType().equals(EntityType.folderEntry) && 
				!entity.getEntityType().equals(EntityType.user) &&
				!entity.getEntityType().equals(EntityType.group) &&
				!entity.getEntityType().equals(EntityType.application) &&
				!entity.getEntityType().equals(EntityType.applicationGroup)) {
			binder = (Binder)entity;
		}
		Boolean versionAgingEnabled = binder.getVersionAgingEnabled();
		if (versionAgingEnabled == null) {
			//If this was never set, then we assume it is enabled (this is the default out-of-the-box behavior)
			versionAgingEnabled = Boolean.TRUE;
		}
		Boolean zoneVersionAgingEnabled = Boolean.FALSE;
		Long zoneVersionAgingMaxDays =  getAdminModule().getFileVersionsMaxAge();
		if (zoneVersionAgingMaxDays != null && zoneVersionAgingMaxDays > 0) {
			zoneVersionAgingEnabled = Boolean.TRUE;
		}

		Long versionAgingDays = binder.getVersionAgingDays();
    	Collection<FileAttachment> atts = entity.getFileAttachments();
    	for (FileAttachment fa : atts) {
			Integer currentMajorVersion = -1;
    		Set<VersionAttachment> fileVersions = fa.getFileVersions();
    		for (VersionAttachment va : fileVersions) {
				//Is this version in the same major version category?
    			if (va.getMajorVersion() != currentMajorVersion) {
    				//This is a new major version category, reset the counters and clear aging flag
    				currentMajorVersion = va.getMajorVersion();
    				if (va.isAgingEnabled()) {
    					//Top level files in a major version category are not subject to aging
    					va.setAgingEnabled(Boolean.FALSE);
    				}
    			} else {
    				//This is a minor version that is not the highest in its major class. It is subject to aging
    				//Binder aging has both agingEnabled=true and agingDate != null
    				//Zone aging has agingEnabled=true and agingDate=null
    				//If the binder aging was explicitly turned off, then no aging should occur in this binder
    				if (versionAgingEnabled && (va.getAgingEnabled() == null || !va.isAgingEnabled())) {
    					//The current agingEnabled value was wrong, so make it correct.
    					if (!Utils.checkIfFilr()) {
    						// (Bug #888672) Don't do this if running under Filr license. Filr doesn't support
    						// versioning yet and this logic isn't needed. This to avoid the version record to
    						// be updated in the database unnecessary since it is going to be shortly deleted
    						// any way. This hack needs to be removed and correct solution has to be implemented
    						// - Probably the right thing to do is to combine the two transactions into one so
    						// that all update to the attachment meta data to be flushed out in a single transaction.
    						va.setAgingEnabled(Boolean.TRUE);
    					}
    				} else if (!versionAgingEnabled && va.getAgingEnabled() != null && va.getAgingEnabled()) {
    					//Aging for this folder was turned off. So, make the enabled flag false (if it isn't already)
    					va.setAgingEnabled(Boolean.FALSE);
    				}
    				//Calculate the binder aging date (if binder has a "agingDays" value and if the binder aging is enabled)
    				if (versionAgingEnabled && versionAgingDays != null) {
    					Date creationDate = va.getCreation().getDate();
    					Date agingDate = new Date(creationDate.getTime() + versionAgingDays*24*60*60*1000);
    					if (!agingDate.equals(va.getAgingDate())) {
    						//Only change it when the date has changed
    						va.setAgingDate(agingDate);
    					}
    				} else if (va.getAgingDate() != null) {
    					//Make sure the aging days is null when binder aging is off so it is subject to zone wide aging
    					va.setAgingDate(null);
    				}
    			}
			}
    	}
	}
    
	public static void modifyFolderEntryWithFile(FolderEntry entry, String dataName, String filename, InputStream is, Date modDate, String expectedMd5)
			throws AccessControlException, ReservedByAnotherUserException, WriteFilesException, WriteEntryDataException 
			 {
		if (Validator.isNull(dataName) && entry.getParentFolder().isLibrary()) {
			// The file is being created within a library folder and the client hasn't specified a data item name explicitly.
			// This will attach the file to the most appropriate definition element (data item) of the entry type (which is by default "upload").
			FolderUtils.modifyLibraryEntry(entry, filename, null, is, null, modDate, expectedMd5, true, null, null);
		}
		else {
			if (Validator.isNull(dataName) || "ss_attachFile".equals(dataName)) 
				dataName="ss_attachFile1";
			Map options = null;
			MultipartFile mf;
			if(modDate != null || expectedMd5 != null) {
                if (modDate != null) {
				    options = new HashMap();
				    options.put(ObjectKeys.INPUT_OPTION_NO_MODIFICATION_DATE, Boolean.TRUE);
                }
                mf = new ExtendedMultipartFile(filename, is, modDate, expectedMd5);
			}
			else {
				mf = new SimpleMultipartFile(filename, is); 					
			}
			Map fileItems = new HashMap(); // Map of names to file items	
			fileItems.put(dataName, mf); // single file item
			getFolderModule().modifyEntry(null, entry.getId(), 
					new EmptyInputData(), fileItems, null, null, options);
		}
	}

	public static void validateModifyFolderEntryWithFile(FolderEntry entry, String filename, long fileSize) 
			throws AccessControlException, ReservedByAnotherUserException, WriteFilesException, WriteEntryDataException 
	{
		String dataName="ss_attachFile1";
		Map options = null;
		options = new HashMap();
		options.put(ObjectKeys.INPUT_OPTION_VALIDATION_ONLY, Boolean.TRUE);
		MultipartFile mf = new NoContentMultipartFile(filename, fileSize); 					

		Map fileItems = new HashMap(); // Map of names to file items	
		fileItems.put(dataName, mf); // single file item
		getFolderModule().modifyEntry(null, entry.getId(), 
				new EmptyInputData(), fileItems, null, null, options);
	}

	public static void modifyPrincipalWithFile(Principal principal, String dataName,
			String filename, InputStream is, Date modDate, String expectedMd5)
			throws AccessControlException, ReservedByAnotherUserException,
			WriteFilesException, WriteEntryDataException {
		if (Validator.isNull(dataName) || "ss_attachFile".equals(dataName))
			dataName = "ss_attachFile1";
		Map options = null;
		MultipartFile mf;
        if(modDate != null || expectedMd5 != null) {
             if (modDate != null) {
                options = new HashMap();
                options.put(ObjectKeys.INPUT_OPTION_NO_MODIFICATION_DATE, Boolean.TRUE);
             }
             mf = new ExtendedMultipartFile(filename, is, modDate, expectedMd5);
        }
        else {
            mf = new SimpleMultipartFile(filename, is);
        }
		Map fileItems = new HashMap();
		fileItems.put(dataName, mf);
		getProfileModule().modifyEntry(principal.getId(), new EmptyInputData(), fileItems, null, null, options);
	}

	public static void modifyBinderWithFile(Binder binder, String dataName,
			String filename, InputStream is)
			throws AccessControlException, ReservedByAnotherUserException,
			WriteFilesException, WriteEntryDataException {
		if (Validator.isNull(dataName) || "ss_attachFile".equals(dataName))
			dataName = "ss_attachFile1";
		MultipartFile mf = new SimpleMultipartFile(filename, is);
		Map fileItems = new HashMap();
		fileItems.put(dataName, mf);
		getBinderModule().modifyBinder(binder.getId(), new EmptyInputData(), fileItems, null, null);
	}
	
	public static VersionAttachment findVersionAttachment(String fileVersionId) 
	throws NoFileVersionByTheIdException {
		FileAttachment fa = getFileModule().getFileAttachmentById(fileVersionId);
		if(fa == null)
			throw new NoFileVersionByTheIdException(fileVersionId);
		else if(!(fa instanceof VersionAttachment))
			throw new NoFileVersionByTheIdException(fileVersionId, "The specified file version ID represents a file rather than a file version");
		else
			return (VersionAttachment) fa;
	}

	public static void deleteFileVersion(VersionAttachment va) {
		DefinableEntity entity = va.getOwner().getEntity();
		// Due to some odd design by another developer, I have to pass in top-level
		// attachment object (as opposed to the top-most version attachment) to the
		// lower level, if the specified version happens to be the top-most one.
		FileAttachment fa = va;
		if(FileUtils.isTopMostVersion(va))
			fa = va.getParentAttachment();
		getBinderModule().deleteFileVersion((entity instanceof Binder)? (Binder)entity : entity.getParentBinder(), entity, fa);
	}
	
	public static void promoteFileVersionCurrent(VersionAttachment va) 
	throws UnsupportedOperationException {
		DefinableEntity entity = va.getOwner().getEntity();
		if(entity instanceof FolderEntry) {
			FolderEntry entry = (FolderEntry) entity;
			if(entry.getParentBinder().isMirrored())
				throw new UnsupportedOperationException("Mirrored file does not support version promotion");
		}
		if(FileUtils.isTopMostVersion(va))
			throw new UnsupportedOperationException("Cannot promote a version that is already current");
		getBinderModule().promoteFileVersionCurrent(entity, va);
	}

	public static void setFileVersionNote(VersionAttachment va, String note) {
		// Due to some odd design by another developer, I have to pass in top-level
		// attachment object (as opposed to the top-most version attachment) to the
		// lower level, if the specified version happens to be the top-most one.
		FileAttachment fa = va;
		if(FileUtils.isTopMostVersion(va))
			fa = va.getParentAttachment();
		getBinderModule().setFileVersionNote(va.getOwner().getEntity(), fa, note);
	}

	public static void setFileVersionStatus(VersionAttachment va, int status) {
		// Due to some odd design by another developer, I have to pass in top-level
		// attachment object (as opposed to the top-most version attachment) to the
		// lower level, if the specified version happens to be the top-most one.
		FileAttachment fa = va;
		if(FileUtils.isTopMostVersion(va))
			fa = va.getParentAttachment();
		getBinderModule().setFileVersionStatus(va.getOwner().getEntity(), fa, status);
	}

	public static boolean isTopMostVersion(VersionAttachment va) {
		return (va.getParentAttachment().getHighestVersionNumber() == va.getVersionNumber());
	}

	public static boolean matchesTopMostVersion(FileAttachment fa, Integer lastVersionNumber, Integer lastMajorVersionNumber, Integer lastMinorVersionNumber) {
		boolean result;
		if((lastVersionNumber != null && lastVersionNumber.intValue() >= 0) || 
				(lastMajorVersionNumber != null && lastMajorVersionNumber.intValue() >= 0) || 
				(lastMinorVersionNumber != null && lastMinorVersionNumber.intValue() >= 0)) {
			result = false;
            Integer actualVersionNumber;
            Integer actualMajorNumber;
            Integer actualMinorNumber;
			VersionAttachment va = fa.getHighestVersion();
			if(va != null) {
                actualVersionNumber = va.getVersionNumber();
                actualMajorNumber = va.getMajorVersion();
                actualMinorNumber = va.getMinorVersion();
            } else {
                actualVersionNumber = fa.getLastVersion();
                actualMajorNumber = fa.getMajorVersion();
                actualMinorNumber = fa.getMinorVersion();
            }
            if(lastVersionNumber != null && lastVersionNumber.intValue() >= 0) {
                if(lastVersionNumber.intValue() == actualVersionNumber)
                    result = true;
            }
            else if(lastMajorVersionNumber != null && lastMajorVersionNumber.intValue() >= 0 && lastMinorVersionNumber != null && lastMinorVersionNumber.intValue() >= 0) {
                if(lastMajorVersionNumber.intValue() == actualMajorNumber &&
                    lastMinorVersionNumber.intValue() == actualMinorNumber)
                    result = true;
            }
		}
		else {
			result = true;
		}
		return result;
	}
	
	public static boolean shouldAccessFileSystemWithExternalAclInUserMode(DefinableEntity entity, WorkAreaOperation workAreaOperation) {
		User user = RequestContextHolder.getRequestContext().getUser();
		
		if(ObjectKeys.SUPER_USER_INTERNALID.equals(user.getInternalId())) {
			// The file operation is being requested by admin. We treat admin like Linux root
			// acccount and grant all accesses to all files.
			return false; // proxy mode
		}
		else if(ObjectKeys.FILE_SYNC_AGENT_INTERNALID.equals(user.getInternalId())) {
			// The file operation is being requested by file sync agent, which ALWAYS accesses
			// file system in proxy mode regardless of sharing. 
			return false; // proxy mode
		}
		else if(ObjectKeys.JOB_PROCESSOR_INTERNALID.equals(user.getInternalId())) {
			// The file operation is being requested by background job such as re-indexing
			// triggered by ACL changes. We need to allow it to access file system in 
			// proxy mode, which doesn't introduce security risk because even background
			// jobs are subject to normal ACL checking.
			return false; // proxy mode
		}
		else {
			boolean shareGrantedAccess = false;
			
			if(entity instanceof WorkArea)
				shareGrantedAccess = getAccessControlManager().testRightGrantedBySharing(user, (WorkArea) entity, workAreaOperation);
						
			if(shareGrantedAccess)
				return false; // proxy mode
			else
				return true; // user mode
		}
	}

	private static FolderModule getFolderModule() {
		return (FolderModule) SpringContextUtil.getBean("folderModule");
	}

	private static ProfileModule getProfileModule() {
		return (ProfileModule) SpringContextUtil.getBean("profileModule");
	}

	private static BinderModule getBinderModule() {
		return (BinderModule) SpringContextUtil.getBean("binderModule");
	}

	private static AdminModule getAdminModule() {
		return (AdminModule) SpringContextUtil.getBean("adminModule");
	}

	private static FileModule getFileModule() {
		return (FileModule) SpringContextUtil.getBean("fileModule");
	}
	
	private static AccessControlManager getAccessControlManager() {
		return (AccessControlManager) SpringContextUtil.getBean("accessControlManager");
	}
	
	//Routine to get the contentType for a file being downloaded to a browser
	public static String validateDownloadContentType(String contentType) {
		//Protect against XSS attacks if this is an HTML file
		if (contentType.toLowerCase().contains("text/html")) {
			if (SPropsUtil.getBoolean("xss.forceDownloadedHtmlFilesToBeSavedToDisk", true)) {
				contentType = DEFAULT_MIME_CONTENT_TYPE;
			}
		}
		return contentType;
	}

	/**
	 * Returns the mime content type to use for a file name allowing
	 * for mixed case extensions.
	 * 
	 * @param ftm
	 * @param shortFileName
	 * 
	 * @return
	 */
	public static String getMimeContentType(FileTypeMap ftm, String shortFileName) {
		String reply = ftm.getContentType(shortFileName);
		if ((null != reply) && reply.equalsIgnoreCase(DEFAULT_MIME_CONTENT_TYPE)) {
			reply = ftm.getContentType(shortFileName.toLowerCase());
		}
		return reply;
	}
}
