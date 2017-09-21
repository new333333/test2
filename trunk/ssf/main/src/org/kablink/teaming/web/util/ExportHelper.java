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
package org.kablink.teaming.web.util;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipInputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipOutputStream;

import org.dom4j.Branch;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.dao.CoreDao;
import org.kablink.teaming.dao.ProfileDao;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.BinderQuota;
import org.kablink.teaming.domain.CustomAttribute;
import org.kablink.teaming.domain.DefinableEntity;
import org.kablink.teaming.domain.Definition;
import org.kablink.teaming.domain.EntityIdentifier;
import org.kablink.teaming.domain.FileAttachment;
import org.kablink.teaming.domain.Folder;
import org.kablink.teaming.domain.FolderEntry;
import org.kablink.teaming.domain.HistoryStamp;
import org.kablink.teaming.domain.NoDefinitionByTheIdException;
import org.kablink.teaming.domain.NoFolderEntryByTheIdException;
import org.kablink.teaming.domain.Principal;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.domain.UserPrincipal;
import org.kablink.teaming.domain.VersionAttachment;
import org.kablink.teaming.domain.WorkflowResponse;
import org.kablink.teaming.domain.WorkflowState;
import org.kablink.teaming.domain.WorkflowSupport;
import org.kablink.teaming.domain.Workspace;
import org.kablink.teaming.domain.ZoneInfo;
import org.kablink.teaming.domain.EntityIdentifier.EntityType;
import org.kablink.teaming.lucene.util.SearchFieldResult;
import org.kablink.teaming.module.admin.AdminModule;
import org.kablink.teaming.module.binder.BinderModule;
import org.kablink.teaming.module.binder.BinderModule.BinderOperation;
import org.kablink.teaming.module.binder.impl.EntryDataErrors;
import org.kablink.teaming.module.binder.impl.WriteEntryDataException;
import org.kablink.teaming.module.definition.DefinitionModule;
import org.kablink.teaming.module.definition.DefinitionUtils;
import org.kablink.teaming.module.definition.export.ElementBuilder;
import org.kablink.teaming.module.definition.export.ElementBuilderUtil;
import org.kablink.teaming.module.file.FileModule;
import org.kablink.teaming.module.file.FilesErrors;
import org.kablink.teaming.module.file.WriteFilesException;
import org.kablink.teaming.module.folder.FolderModule;
import org.kablink.teaming.module.ical.IcalModule;
import org.kablink.teaming.module.profile.ProfileModule;
import org.kablink.teaming.module.shared.EntityIndexUtils;
import org.kablink.teaming.module.workflow.WorkflowModule;
import org.kablink.teaming.module.workspace.WorkspaceModule;
import org.kablink.teaming.module.zone.ZoneModule;
import org.kablink.teaming.remoting.ws.util.DomInputData;
import org.kablink.teaming.security.AccessControlException;
import org.kablink.teaming.security.function.Function;
import org.kablink.teaming.security.function.WorkArea;
import org.kablink.teaming.security.function.WorkAreaFunctionMembership;
import org.kablink.teaming.util.NLT;
import org.kablink.teaming.util.ResolveIds;
import org.kablink.teaming.util.SPropsUtil;
import org.kablink.teaming.util.SpringContextUtil;
import org.kablink.teaming.util.StatusTicket;
import org.kablink.teaming.util.TempFileUtil;
import org.kablink.teaming.util.Utils;
import org.kablink.teaming.util.XmlFileUtil;
import org.kablink.teaming.util.XmlUtil;
import org.kablink.teaming.util.ZipEntryStream;
import org.kablink.teaming.web.util.ServerTaskLinkage;
import org.kablink.teaming.web.util.ServerTaskLinkage.ServerTaskLink;
import org.kablink.teaming.web.util.WebUrlUtil;
import org.kablink.util.FileUtil;
import org.kablink.util.Validator;
import org.kablink.util.search.Constants;
import org.kablink.util.search.Criteria;
import org.kablink.util.search.Order;

import static org.kablink.util.search.Restrictions.in;

import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.FileCopyUtils;

/**
 * ?
 * 
 * @author Janet McCann
 */
@SuppressWarnings({"unchecked", "unused"})
public class ExportHelper {
	protected static final Log logger = LogFactory.getLog(ExportHelper.class);
	private static AdminModule adminModule = (AdminModule) SpringContextUtil.getBean("adminModule");
	private static BinderModule binderModule = (BinderModule) SpringContextUtil.getBean("binderModule");
	private static FolderModule folderModule = (FolderModule) SpringContextUtil.getBean("folderModule");
	private static FileModule fileModule = (FileModule) SpringContextUtil.getBean("fileModule");
	private static WorkspaceModule workspaceModule = (WorkspaceModule) SpringContextUtil.getBean("workspaceModule");
	private static DefinitionModule definitionModule = (DefinitionModule) SpringContextUtil.getBean("definitionModule");
	private static WorkflowModule workflowModule = (WorkflowModule) SpringContextUtil.getBean("workflowModule");
	private static CoreDao coreDao = (CoreDao) SpringContextUtil.getBean("coreDao");
	private static TransactionTemplate transactionTemplate = (TransactionTemplate) SpringContextUtil.getBean("transactionTemplate");
	private static IcalModule iCalModule = (IcalModule) SpringContextUtil.getBean("icalModule");
	private static ProfileModule profileModule = (ProfileModule) SpringContextUtil.getBean("profileModule");
	private static ZoneModule zoneModule = (ZoneModule) SpringContextUtil.getBean("zoneModule");
	private static ProfileDao profileDao = (ProfileDao)SpringContextUtil.getBean("profileDao");
	
	public static final String workspaces = "workspaces";
	public static final String folders = "folders";
	public static final String entries = "entries";
	public static final String files = "files";
	public static final String errors = "errors";
	public static final String errorList = "errorList";
	
	//Export version number. Used to distinguish between export file formats 
	public static final String exportVersion = "3";
	public static final String exportVersionV1 = "3";

	// used during export so that all id's are at least 8 digits long,
	// with leading zeroes
	private static NumberFormat nft = null;
	
	private static NumberFormat getNumberFormat() {
		if (nft == null) {
			int paddingSize = SPropsUtil.getInt("export.id.padding.size", 8);
			StringBuffer sb = new StringBuffer("#");
			for (int i = 0; i < paddingSize; i++)
				sb.append("0");
			nft = new DecimalFormat(sb.toString());
		}
		return nft;
	}
	
	// used during export. file name prefix used to identify xml files
	// for folders and workspaces
	private static String binderPrefix = "b_";

	public static void export(Long binderId, Long entityId, OutputStream out,
			Map options, Collection<Long> binderIds, Boolean noSubBinders, 
			StatusTicket statusTicket, Map reportMap) throws Exception {

		try {
			getNumberFormat();
	
			ZipOutputStream zipOut = new ZipOutputStream(out);
	
			// Standard zip encoding is cp437. (needed when chars are outside the
			// ASCII range)
			zipOut.setEncoding("cp437");
	
			// Binder and entry definitions that we will later export
			Set defListAlreadyAdded = new HashSet();
	
			if (entityId != null) {
				processEntry(zipOut, folderModule.getEntry(binderId, entityId), "",
						defListAlreadyAdded, reportMap);
			} else {
	
				EntityType entType = binderModule.getBinder(binderId)
						.getEntityType();
	
				// see if folder
				if (entType == EntityType.folder) {
					process(zipOut, folderModule.getFolder(binderId), false,
							options, binderIds, noSubBinders, defListAlreadyAdded, statusTicket, reportMap);
					// see if ws or profiles ws
				} else if ((entType == EntityType.workspace)
						|| (entType == EntityType.profiles)) {
					process(zipOut, workspaceModule.getWorkspace(binderId), true,
							options, binderIds, noSubBinders, defListAlreadyAdded, statusTicket, reportMap);
				} else {
					// something's wrong
				}
			}
	
			zipOut.finish();
		}
		catch(Exception e) {
			if(e instanceof ExportException)
				throw e;
			else
				throw new ExportException(e);
		}
	}

	private static void process(ZipOutputStream zipOut, Binder start,
			boolean isWorkspace, Map options, Collection<Long> binderIds, 
			Boolean noSubBinders, Set defListAlreadyAdded, StatusTicket statusTicket, Map reportMap) throws Exception {
		Map<Long,Boolean> binderIdsToExport = new HashMap<Long,Boolean>();
		binderIdsToExport.put(start.getId(), false);
		//Get sub-binder list including intermediate binders that may be inaccessible
		SortedSet<Binder> binders = binderModule.getBinders(binderIds, Boolean.FALSE);
		for (Binder binder : binders) {
			//Mark this binder as having everything exported
			binderIdsToExport.put(binder.getId(), true);
			if (!binder.getId().equals(start.getId())) {
				//Add in all of the parent binders up to start
				Binder parent = binder.getParentBinder();
				while (parent != null && !parent.getId().equals(start.getId())) {
					if (!binderIdsToExport.containsKey(parent.getId())) 
						binderIdsToExport.put(parent.getId(), false);
					parent = parent.getParentBinder();
				}
			}
		}
		//Nothing selected means do everything
		if (binders.isEmpty()) binderIdsToExport.put(start.getId(), true);
		
		// Export the definitions
		exportDefinitionList(zipOut, start.getDefinitions(), defListAlreadyAdded);

		
		//Get a complete list of all sub-binders
		List folderIds = new ArrayList();
		folderIds.add(start.getId().toString());
		Criteria crit = new Criteria();
		crit.add(in(Constants.DOC_TYPE_FIELD, new String[] {Constants.DOC_TYPE_BINDER}))
			.add(in(Constants.ENTRY_ANCESTRY, folderIds));
		crit.addOrder(Order.asc(Constants.BINDER_ID_FIELD));
		Map binderMap = binderModule.executeSearchQuery(crit, Constants.SEARCH_MODE_SELF_CONTAINED_ONLY, 0, ObjectKeys.SEARCH_MAX_HITS_SUB_BINDERS,
				org.kablink.teaming.module.shared.SearchUtils.fieldNamesList(Constants.DOCID_FIELD,Constants.ENTRY_ANCESTRY));

		List binderMapList = (List)binderMap.get(ObjectKeys.SEARCH_ENTRIES); 
		List binderIdList = new ArrayList();

      	for (Iterator iter=binderMapList.iterator(); iter.hasNext();) {
      		Map entryMap = (Map) iter.next();
      		Long docId = new Long((String)entryMap.get(Constants.DOCID_FIELD));
      		binderIdList.add(docId);
	        if (!noSubBinders && !binderIdsToExport.containsKey(docId)) {
	        	binderIdsToExport.put(docId, false);
	        	//All sub-binders should be exported; add these to the list
	        	ArrayList ancestors = ((SearchFieldResult)entryMap.get(Constants.ENTRY_ANCESTRY)).getValueArray();
	        	Iterator itAncestors = ancestors.iterator();
	        	while (itAncestors.hasNext()) {
	        		Long ancId = Long.valueOf((String)itAncestors.next());
	        		if (binderIdsToExport.containsKey(ancId) && binderIdsToExport.get(ancId)) {
	        			binderIdsToExport.put(docId, true);
	        			break;
	        		}
	        	}
	        }
      	}
		
        //Go through all of the binders and export them
      	Iterator<Long> itBinders = binderIdsToExport.keySet().iterator();
      	while (itBinders.hasNext()) {
      		Long binderId = (Long)itBinders.next();
      		Binder binder = null;
      		try {
				binder = binderModule.getBinder(binderId);
			} catch(Exception e) {
				Integer count = (Integer)reportMap.get(errors);
				reportMap.put(errors, ++count);
				((List)reportMap.get(errorList)).add(e.getLocalizedMessage());
				continue;
			}
			//Make sure the definitions used by this binder are exported
			exportDefinitionList(zipOut, binder.getDefinitions(), defListAlreadyAdded);
			if ( statusTicket != null )
				statusTicket.setStatus(NLT.get("administration.export_import.exporting", new String[] {binder.getPathName()}));
			String pathName = "";
			if (EntityType.workspace.equals(binder.getEntityType()) || EntityType.profiles.equals(binder.getEntityType())) {
				Binder parentBinder = binder;
				while (parentBinder != null && !parentBinder.equals(start)) {
					parentBinder = parentBinder.getParentBinder();
					if (parentBinder == null) break;
					if (EntityType.folder.equals(parentBinder.getEntityType())) {
						pathName = binderPrefix + "f" + nft.format(parentBinder.getId()) + "/" + pathName;
					} else {
						pathName = binderPrefix + "w" + nft.format(parentBinder.getId()) + "/" + pathName;
					}
				}
				// We have to use "/" instead of File.separator so the correct directory structure will be created in the zip file.
				// Retrieve the raw workspace.
				Workspace workspace = workspaceModule.getWorkspace(binderId);
				addBinderDefinitions(zipOut, workspace, defListAlreadyAdded, reportMap);

				zipOut.putNextEntry(new ZipEntry(pathName + binderPrefix + "w"
						+ nft.format(binderId) + "/" + "."
						+ binderPrefix + "w" + nft.format(binderId) + ".xml"));
			
				XmlFileUtil.writeFile(getWorkspaceAsDoc(null, binderId, false,
						binderPrefix + "w" + nft.format(binderId)),
						zipOut);
				zipOut.closeEntry();
				Integer count = (Integer)reportMap.get(workspaces);
				reportMap.put(workspaces, ++count);

			} else {
				Binder parentBinder = binder;
				while (!parentBinder.equals(start)) {
					parentBinder = parentBinder.getParentBinder();
					if (parentBinder == null) break;
					if (EntityType.folder.equals(parentBinder.getEntityType())) {
						pathName = binderPrefix + "f" + nft.format(parentBinder.getId()) + "/" + pathName;
					} else {
						pathName = binderPrefix + "w" + nft.format(parentBinder.getId()) + "/" + pathName;
					}
				}
				//Add any definitions used by this folder
				addBinderDefinitions(zipOut, binder, defListAlreadyAdded, reportMap);
				
				List<Long> entryIds;
				if (binderIdsToExport.get(binderId))
				     entryIds = getFolderEntryIds(binderId, options);
				else entryIds = null;
	
				// We have to use "/" instead of File.separator so the correct directory structure will be created in the zip file.
				zipOut.putNextEntry(new ZipEntry(pathName + binderPrefix + "f"
						+ nft.format(binderId) + "/" + "."
						+ binderPrefix + "f" + nft.format(binderId) + ".xml"));
				XmlFileUtil.writeFile(getFolderAsDoc(null, binderId, entryIds, false,
						pathName + binderPrefix + "f" + nft.format(binderId)),
						zipOut);
				zipOut.closeEntry();
				Integer count = (Integer)reportMap.get(folders);
				reportMap.put(folders, ++count);

	
				//Only output entries if folder is selected (or is a sub-folder being output)
				if (binderIdsToExport.get(binderId)) {
					for (Long entryId:  entryIds) {
						try {
							FolderEntry entry = folderModule.getEntry(binderId,
									entryId);
							processEntry(zipOut, entry, pathName + binderPrefix + "f"
									+ nft.format(binderId), defListAlreadyAdded, reportMap);
	
							count = (Integer)reportMap.get(entries);
							reportMap.put(entries, ++count);
						} catch(NoFolderEntryByTheIdException e) {
							Integer c = (Integer)reportMap.get(errors);
							reportMap.put(errors, ++c);
							((List)reportMap.get(errorList)).add(e.getLocalizedMessage());
						}
					}
				}
			}

			String entityLetter = "w";
			if (EntityType.folder.equals(binder.getEntityType())) entityLetter = "f";
			Set<FileAttachment> attachments = binder.getFileAttachments();
	
			for (FileAttachment attach : attachments) {
				processBinderAttachment(zipOut, binder, attach, pathName + binderPrefix
						+ entityLetter + nft.format(binderId), reportMap);
			}
      	}

		return;
	}

	private static void processBinderAttachment(ZipOutputStream zipOut, Binder binder,
			FileAttachment attachment, String pathName, Map reportMap) throws IOException {

		processAttachment(zipOut, binder, binder, attachment, pathName, reportMap);

		return;
	}

	private static String calcFullId(FolderEntry entry) {
		if (entry.getParentEntry() == null) {
			return nft.format(entry.getId());
		} else {
			return calcFullId(entry.getParentEntry()) + "_"
					+ nft.format(entry.getId());
		}
	}

	private static void processEntry(ZipOutputStream zipOut, FolderEntry entry,
			String pathName, Set defListAlreadyAdded, Map reportMap) throws Exception {
		String fullId = null;

		fullId = calcFullId(entry);

		//Add any definitions used by this folder
		addEntryDefinitions(zipOut, entry, defListAlreadyAdded, reportMap);

		if (!pathName.equals("")) {
			// We have to use "/" instead of File.separator so the correct directory structure will be created in the zip file.
			zipOut.putNextEntry(new ZipEntry(pathName + "/" + "e"
					+ fullId + ".xml"));
			XmlFileUtil.writeFile(getEntryAsDoc(zipOut, null, entry.getParentBinder()
					.getId(), entry.getId(), false, pathName + "/"
					+ "e" + fullId, defListAlreadyAdded), zipOut);
		} else {
			zipOut.putNextEntry(new ZipEntry("e" + fullId + ".xml"));
			XmlFileUtil.writeFile(getEntryAsDoc(zipOut, null, entry.getParentBinder()
					.getId(), entry.getId(), false, "e" + fullId, defListAlreadyAdded),
					zipOut);
		}
		zipOut.closeEntry();

		Set<FileAttachment> attachments = entry.getFileAttachments();
		for (FileAttachment attach : attachments) {
			processEntryAttachment(zipOut, entry, fullId, attach, pathName, reportMap);
		}

		List<FolderEntry> replies = entry.getReplies();
		for (FolderEntry reply : replies) {
			processEntryReply(zipOut, reply, fullId, pathName, defListAlreadyAdded, reportMap);
		}

		return;
	}

	private static void processEntryReply(ZipOutputStream zipOut, FolderEntry reply,
			String fullId, String pathName, Set defListAlreadyAdded, Map reportMap) throws Exception {
		String newFullId = fullId + "_" + nft.format(reply.getId());

		//Add any definitions used by this folder
		addEntryDefinitions(zipOut, reply, defListAlreadyAdded, reportMap);

		if (!pathName.equals("")) {
			// We have to use "/" instead of File.separator so the correct directory structure will be created in the zip file.
			zipOut.putNextEntry(new ZipEntry(pathName + "/" + "e"
					+ newFullId + ".xml"));
			XmlFileUtil.writeFile(getEntryAsDoc(zipOut, null, reply.getParentBinder()
					.getId(), reply.getId(), false, pathName + "/"
					+ "e" + newFullId, defListAlreadyAdded), zipOut);
		} else {
			zipOut.putNextEntry(new ZipEntry("e" + newFullId + ".xml"));
			XmlFileUtil.writeFile(getEntryAsDoc(zipOut, null, reply.getParentBinder()
					.getId(), reply.getId(), false, "e" + newFullId, defListAlreadyAdded),
					zipOut);
		}
		zipOut.closeEntry();

		Set<FileAttachment> attachments = reply.getFileAttachments();
		for (FileAttachment attach : attachments) {
			processEntryAttachment(zipOut, reply, newFullId, attach, pathName, reportMap);
		}

		List<FolderEntry> reps = reply.getReplies();
		for (FolderEntry rep : reps) {
			processEntryReply(zipOut, rep, newFullId, pathName, defListAlreadyAdded, reportMap);
		}

		return;
	}

	private static void processEntryAttachment(ZipOutputStream zipOut,
			FolderEntry entry, String fullId, FileAttachment attachment,
			String pathName, Map reportMap) throws IOException {

		processAttachment(zipOut, entry.getParentBinder(), entry, attachment,
				pathName + "/" + "e" + fullId, reportMap);

		return;
	}

	private static void processAttachment(ZipOutputStream zipOut, Binder binder,
			DefinableEntity entity, FileAttachment attachment, String pathName, Map reportMap)
			throws IOException {

		String fileName = filename8BitSingleByteOnly(attachment, SPropsUtil
				.getBoolean("export.filename.8bitsinglebyte.only", true));

		String fileExt = EntityIndexUtils.getFileExtension(attachment
				.getFileItem().getName());

		// latest version
		InputStream fileStream = null;

		try {
			fileStream = fileModule.readFile(binder, entity, attachment);

			// We have to use "/" instead of File.separator so the correct directory structure will be created in the zip file.
			zipOut.putNextEntry(new ZipEntry(pathName + "/" + fileName));
			FileUtil.copy(fileStream, zipOut);
			zipOut.closeEntry();

			Integer count = (Integer)reportMap.get(files);
			reportMap.put(files, ++count);
		} catch (Exception e) {
			if (fileStream == null) {
				//The file must not exist, so just skip it. This really isn't an error condition.
			} else {
				logger.error(e);
				String eMsg = NLT.get("export.error.attachment") + " - " + binder.getPathName().toString() + 
						", entryId=" + entity.getId().toString() + ", " + fileName;
				logger.error(eMsg);
				Integer c = (Integer)reportMap.get(errors);
				reportMap.put(errors, ++c);
				((List)reportMap.get(errorList)).add(eMsg);
	
				// We have to use "/" instead of File.separator so the correct directory structure will be created in the zip file.
				zipOut.putNextEntry(new ZipEntry(pathName + "/"
						+ fileName + ".error_message.txt"));
				zipOut.write(NLT.get("export.error.attachment",
						"Error processing this attachment").getBytes());
				zipOut.closeEntry();
			}
		}
		finally {
			try {
				if(fileStream != null)
					fileStream.close();
			}
			catch(IOException ignore) {}
		}

		// older versions, from highest to lowest
		Set fileVersions = attachment.getFileVersions();
		if (!fileVersions.isEmpty()) {
			Iterator<VersionAttachment> versionIter = fileVersions.iterator();
			if (versionIter.hasNext()) {
				VersionAttachment vAttach = versionIter.next();		//Skip the latest file - it was already output above
				for (int i = 1; i < fileVersions.size(); i++) {
					if (versionIter.hasNext()) {
						vAttach = versionIter.next();
			
						int versionNum = fileVersions.size() - i;
			
						fileStream = null;
						try {
							fileStream = fileModule.readFile(binder, entity,
									vAttach);
			
							// We have to use "/" instead of File.separator so the correct directory structure will be created in the zip file.
							zipOut.putNextEntry(new ZipEntry(pathName + "/"
									+ fileName + ".versions" + "/" + versionNum
									+ "." + fileExt));
							FileUtil.copy(fileStream, zipOut);
							zipOut.closeEntry();
			
							Integer count = (Integer)reportMap.get(files);
							reportMap.put(files, ++count);
						} catch (Exception e) {
							if (fileStream == null) {
								//The file must not exist, so just skip it. This really isn't an error condition.
							} else {
								logger.error(e);
								String eMsg = NLT.get("export.error.attachment") + " - " + binder.getPathName().toString() + 
										", entryId=" + entity.getId().toString() + ", " + fileName;
								logger.error(eMsg);
								Integer c = (Integer)reportMap.get(errors);
								reportMap.put(errors, ++c);
								((List)reportMap.get(errorList)).add(eMsg);
				
								// We have to use "/" instead of File.separator so the correct directory structure will be created in the zip file.
								zipOut.putNextEntry(new ZipEntry(pathName + "/"
										+ fileName + ".versions" + "/" + versionNum
										+ "." + fileExt + ".error_message.txt"));
								zipOut.write(NLT.get("export.error.attachment",
										"Error processing this attachment").getBytes());
								zipOut.closeEntry();
							}
						}
						finally {
							try {
								if(fileStream != null)
									fileStream.close();
							}
							catch(IOException ignore) {}
						}
					}
				}
			}
		}

	}

	private static Document getEntryAsDoc(ZipOutputStream zipOut, String accessToken, long binderId,
			long entryId, boolean includeAttachments, String pathName,
			Set defListAlreadyAdded) {
		Long bId = new Long(binderId);
		Long eId = new Long(entryId);

		// Retrieve the raw entry.
		FolderEntry entry = folderModule.getEntry(bId, eId);
		Document doc = DocumentHelper.createDocument();
		Element entryElem = doc.addElement("entry");

		// Handle structured fields of the entry known at compile time.
		addEntityAttributes(entryElem, entry);

		// Handle custom fields driven by corresponding definition.
		addCustomElements(entryElem, entry);

		// attachments
		addAttachmentFiles(doc, entry);

		// workflows
		addWorkflows(doc.getRootElement(), entry);

		//Access control settings
		WorkArea workArea = (WorkArea)entry;
		Element settingsEle = doc.getRootElement().addElement("settings");
		addAccessControls(workArea, settingsEle);

		return doc;
	}

	private static Document getFolderAsDoc(String accessToken, long folderId, List<Long> entryIds,
			boolean includeAttachments, String pathName) {
		Long fId = new Long(folderId);

		// Retrieve the raw folder.
		Folder folder = folderModule.getFolder(fId);

		Element team = binder_getTeamMembersAsElement(null, folder.getId());

		Document doc = DocumentHelper.createDocument();
		Element folderElem = doc.addElement("folder");

		// Handle structured fields of the folder known at compile time.
		addEntityAttributes(folderElem, folder);

		// Handle custom fields driven by corresponding definition.
		addCustomElements(folderElem, folder);

		// teams
		folderElem.add(team);

		// attachments
		addAttachmentFiles(doc, folder);

		// binder settings
		addSettingsList(doc.getRootElement(), folder);

		// workflows
		addWorkflows(doc.getRootElement(), folder);
		
		// task linkage
		addTaskLinkage(doc.getRootElement(), folder, entryIds);

		return doc;
	}

	private static Document getWorkspaceAsDoc(String accessToken, Long binderId,
			boolean includeAttachments, String pathName) {
		
		// Retrieve the raw workspace.
		Workspace workspace = workspaceModule.getWorkspace(binderId);

		Element team = binder_getTeamMembersAsElement(null, workspace.getId());

		Document doc = DocumentHelper.createDocument();
		Element workspaceElem = doc.addElement("workspace");

		// Handle structured fields of the workspace known at compile time.
		addEntityAttributes(workspaceElem, workspace);

		// Handle custom fields driven by corresponding definition.
		addCustomElements(workspaceElem, workspace);

		// teams
		workspaceElem.add(team);

		// attachments
		addAttachmentFiles(doc, workspace);

		// binder settings
		addSettingsList(doc.getRootElement(), workspace);

		// workflows
		addWorkflows(doc.getRootElement(), workspace);
		
		return doc;
	}

	private static void addEntityAttributes(Element entityElem, DefinableEntity entity) {
		entityElem.addAttribute("id", entity.getId().toString());
		Binder binder = entity.getParentBinder();
		if (binder != null) entityElem.addAttribute("binderId", binder.getId().toString());

		if (entity.getEntryDefId() != null) {
			Definition def = coreDao.loadDefinition(entity.getEntryDefId(), RequestContextHolder.getRequestContext().getZoneId());
			entityElem.addAttribute("definitionId", def.getId());
			entityElem.addAttribute("definitionName", def.getName());
		}

		entityElem.addAttribute("title", entity.getTitle());
		addExportVersion(entityElem, entity);
		addZoneId(entityElem, entity);

		// if a folder
		if (entity instanceof Binder) {
			entityElem.addAttribute("libraryFolder", String.valueOf(((Binder) entity).isLibrary()));
			entityElem.addAttribute("uniqueTitles", String.valueOf(((Binder) entity).isUniqueTitles()));
		}

		addEntitySignature(entityElem, entity);
		
		String entityUrl = "";

		// if a folder entry
		if (entity instanceof FolderEntry) {
			entityElem.addAttribute("docNumber", ((FolderEntry) entity)
					.getDocNumber());
			entityElem.addAttribute("docLevel", String
					.valueOf(((FolderEntry) entity).getDocLevel()));

			// add parent entry id if it exists
			FolderEntry parentEntry = ((FolderEntry) entity).getParentEntry();
			if (parentEntry != null) {
				entityElem.addAttribute("parentId", String.valueOf(parentEntry
						.getId()));
			}

			entityUrl = WebUrlUtil.getEntryViewURL((FolderEntry) entity);
		}

		entityElem.addAttribute("href", entityUrl);
		addRating(entityElem, entity);
	}

	private static void addEntitySignature(Element entityElem, DefinableEntity entity) {
		Element signature = entityElem.addElement("signature");
		Element creation = signature.addElement("creation");
		addHistoryStamp(creation, entity.getCreation());
		Element modification = signature.addElement("modification");
		addHistoryStamp(modification, entity.getModification());
	}
	private static void addHistoryStamp(Element entityElem, HistoryStamp stamp) {
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    	entityElem.addAttribute("date", sdf.format(stamp.getDate()));
		addPrincipalToDocument(entityElem, stamp.getPrincipal());
	}
	private static void addRating(Element element, DefinableEntity entity) {
		if (entity.getAverageRating() != null) {
			element.addAttribute("averageRating", entity.getAverageRating()
					.getAverage().toString());
			element.addAttribute("ratingCount", entity.getAverageRating()
					.getCount().toString());
		}
	}

	private static void addExportVersion(Element element, DefinableEntity entity) {
		element.addAttribute("exportVersion", exportVersion);
	}

	private static void addZoneId(Element element, DefinableEntity entity) {
		ZoneInfo zoneInfo = ExportHelper.getZoneInfo();
		String zoneInfoId = zoneInfo.getId();
		if (Validator.isNull(zoneInfoId)) zoneInfoId = "";
		String entityZoneUUID = zoneInfoId + "." + entity.getId().toString();
		CustomAttribute zoneUUIDs = entity.getCustomAttribute(Constants.ZONE_UUID_FIELD);
		Set zoneUUIDvalues = new HashSet();
		if (zoneUUIDs != null) zoneUUIDvalues = zoneUUIDs.getValueSet();
		if (!zoneUUIDvalues.contains(entityZoneUUID)) zoneUUIDvalues.add(entityZoneUUID);
		Iterator itZoneUUIDs = zoneUUIDvalues.iterator();
		while (itZoneUUIDs.hasNext()) {
			String zoneUUID = (String) itZoneUUIDs.next();
			Element attributeEle = element.addElement("attribute");
			attributeEle.addAttribute("name", Constants.ZONE_UUID_FIELD);
			attributeEle.addAttribute("type", "text");
			attributeEle.setText(zoneUUID);
		}
	}

	private static void addCustomElements(final Element entityElem, final DefinableEntity entry) {
		addCustomAttributes(entityElem, entry);
	}

	private static void addCustomAttributes(final Element entityElem, final DefinableEntity entity) {
		final ElementBuilder.BuilderContext context = null;

		DefinitionModule.DefinitionVisitor visitor = new DefinitionModule.DefinitionVisitor() {
			@Override
			public void visit(Element entityElement, Element flagElement, Map args) {
				if (flagElement.attributeValue("apply", "").equals("true")) {
					String fieldBuilder = flagElement.attributeValue("elementBuilder");
					String typeValue = entityElement.attributeValue("name");
					String nameValue = DefinitionUtils.getPropertyValue(entityElement, "name");
					if (Validator.isNull(nameValue)) {
						nameValue = typeValue;
					}
					ElementBuilderUtil.buildElement(entityElem, entity,
							typeValue, nameValue, fieldBuilder, context);
				}
			}

			@Override
			public String getFlagElementName() {
				return "export";
			}
		};

		definitionModule.walkDefinition(entity, visitor, null);

		// see if title and attachments have been handled
		Element root = entity.getEntryDefDoc().getRootElement();
		if (root != null) {
			Element title = (Element) root.selectSingleNode("//item[@name='title']");
			if (title == null) {
				// Force processing of title. Not all forms will have a title element
				// but binders (and some other entities) have to have a title
				title = (Element) definitionModule.getDefinitionConfig()
						.getRootElement().selectSingleNode("//item[@name='title']");

				Element flagElem = (Element) title.selectSingleNode("export");

				ElementBuilderUtil.buildElement(entityElem, entity,
						"title", DefinitionUtils.getPropertyValue(
								title, "name"), flagElem
								.attributeValue("elementBuilder"), context);
			}
			
			Element attachments = (Element) root.selectSingleNode("//item[@name='attachFiles']");
			if (attachments == null) {
				// Force processing of attachments. Not all forms will have an
				// attachment element,
				// but this is the only code that actually sends the files, even
				// if they
				// are part of a graphic or file element. So force attachment
				// processing
				// to pick up all files
				attachments = (Element) definitionModule.getDefinitionConfig()
						.getRootElement().selectSingleNode(
								"//item[@name='attachFiles']");

				Element flagElem = (Element) attachments
						.selectSingleNode("export");

				ElementBuilderUtil.buildElement(entityElem, entity,
						"attachFiles", DefinitionUtils.getPropertyValue(
								attachments, "name"), flagElem
								.attributeValue("elementBuilder"), context);
			}
		}
	}

	private static Element binder_getTeamMembersAsElement(String accessToken,
			long binderId) {
		Binder binder = binderModule.getBinder(new Long(binderId));
		SortedSet<Principal> principals = binderModule.getTeamMembers(binder, true);
		Document doc = DocumentHelper.createDocument();
		Element team = doc.addElement("team");
		team.addAttribute("inherited",
				binder.isTeamMembershipInherited() ? "true" : "false");
		for (Principal p : principals) {
			addPrincipalToDocument(team, p);
		}

		return team;
	}

	private static void addAttachmentFiles(Document entityDoc, DefinableEntity entity) {
		String xPath = "//attribute[@name='ss_attachFile']";

		List attachFiles = entityDoc.selectNodes(xPath);
		if (!entity.getFileAttachments().isEmpty() && (attachFiles == null || attachFiles.isEmpty())) {
			Element element = entityDoc.getRootElement().addElement("attribute");
			element.addAttribute("name", "ss_attachFile");
			element.addAttribute("type", "attachFiles");
			
			//The attached files are already loaded, so do it now
			for (FileAttachment att:entity.getFileAttachments()) {
				if (att != null && att.getFileItem() != null) { 
					Element value = element.addElement("file");
					value.setText(att.getFileItem().getName());
					
					String fileName = binderModule.filename8BitSingleByteOnly(att, 
							SPropsUtil.getBoolean("export.filename.8bitsinglebyte.only", true));
					value.addAttribute("href", fileName);
					
					value.addAttribute("numVersions", String.valueOf(att.getFileVersions().size()));
				}
			}
		}
	}

	private static void addWorkflows(Element element, DefinableEntity entity) {

		Element workflowsEle = element.addElement("workflows");

		if (entity instanceof FolderEntry) {
			for (WorkflowResponse response : ((FolderEntry) entity)
					.getWorkflowResponses()) {
				if (response != null) {
					if (workflowsEle != null) {
						Element value = workflowsEle.addElement("response");
						String defId = response.getDefinitionId();
						Definition def = definitionModule.getDefinition(defId);
						value.addAttribute("definitionId", defId);
						value.addAttribute("definitionName", def.getName());
						value.addAttribute("responseName", response.getName());
						Date date = response.getResponseDate();
						SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
						value.addAttribute("responseDate", formatter.format(date.getTime()));
						value.addAttribute("responderId", response.getResponderId().toString());
						value.addAttribute("responseValue", response.getResponse());
						User responder = profileDao.loadUser(response.getResponderId(), 
								RequestContextHolder.getRequestContext().getZoneId());
						addPrincipalToDocument(value, responder);
					}
				}
			}
			for (WorkflowState workflow : ((FolderEntry) entity)
					.getWorkflowStates()) {
				if (workflow != null) {
					if (workflowsEle != null) {
						Element value = workflowsEle.addElement("process");
						value.addAttribute("definitionId", workflow.getDefinition().getId());
						value.addAttribute("definitionName", workflow.getDefinition().getName());
						value.addAttribute("state", workflow.getState());
					}
				}
			}
		} else if (entity instanceof Binder) {

			List workflowDefinitions = ((Binder) entity)
					.getWorkflowDefinitions();

			Element allowed = workflowsEle.addElement("allowed");

			for (int i = 0; i < workflowDefinitions.size(); i++) {
				if (allowed != null) {
					Element value = allowed.addElement("process");
					value.addAttribute("definitionId",
							((Definition) workflowDefinitions.get(i)).getId());
					value.addAttribute("definitionName", 
							((Definition) workflowDefinitions.get(i)).getName());
				}
			}

			Map workflowAssociations = ((Binder) entity)
					.getWorkflowAssociations();

			Element associated = workflowsEle.addElement("associated");

			Iterator keyIter = workflowAssociations.keySet().iterator();

			while (keyIter.hasNext()) {
				if (associated != null) {
					Element value = associated.addElement("process");

					String key = (String) keyIter.next();

					value.addAttribute("entryDefinitionId", key);

					Definition entryDef = definitionModule.getDefinition(key);

					value.addAttribute("entryDefinitionName", entryDef
							.getName());

					Definition workflowDef = ((Definition) workflowAssociations
							.get(key));

					value.addAttribute("workflowDefinitionId", workflowDef
							.getId());
					value.addAttribute("workflowDefinitionName", workflowDef
							.getName());
				}
			}
		}
	}

	private static void addSettingsList(Element element, Binder binder) {

		Element settingsEle = element.addElement("settings");

		//Branding
		String branding = binder.getBranding();
		if (branding != null) {
			Element brandingEle = settingsEle.addElement("branding");
			brandingEle.setText(branding);
		}
		String brandingExt = binder.getBrandingExt();
		if (brandingExt != null) {
			Element brandingExtEle = settingsEle.addElement("brandingExt");
			brandingExtEle.setText(brandingExt);
		}
		
		// views
		List<Definition> viewDefinitions = binder.getViewDefinitions();
		Element views = settingsEle.addElement("views");
		for (int i = 0; i < viewDefinitions.size(); i++) {
			if (views != null) {
				Element value = views.addElement("view");
				value.addAttribute("definitionId", viewDefinitions.get(i)
						.getId());
				value.addAttribute("definitionName", viewDefinitions.get(i).getName());
			}
		}
		
		//Access control settings
		WorkArea workArea = (WorkArea)binder;
		addAccessControls(workArea, settingsEle);

		// entries
		List<Definition> entryDefinitions = binder.getEntryDefinitions();
		Element entries = settingsEle.addElement("entries");
		for (int i = 0; i < entryDefinitions.size(); i++) {
			if (entries != null) {
				Element value = entries.addElement("entry");
				value.addAttribute("definitionId", entryDefinitions.get(i)
						.getId());
				value.addAttribute("definitionName", entryDefinitions.get(i).getName());
			}
		}
		
		// version controls
		Element versionControls = settingsEle.addElement("versionControls");
		Boolean versionsEnabled = binder.getVersionsEnabled();
		if (versionsEnabled != null) {
			versionControls.addAttribute("versionsEnabled", versionsEnabled.toString());
		}
		Long versionsToKeep = binder.getVersionsToKeep();
		if (versionsToKeep != null) {
			versionControls.addAttribute("versionsToKeep", versionsToKeep.toString());
		}
		Boolean versionAgingEnabled = binder.getVersionAgingEnabled();
		if (versionAgingEnabled != null) {
			versionControls.addAttribute("versionAgingEnabled", versionAgingEnabled.toString());
		}
		Long versionAgingDays = binder.getVersionAgingDays();
		if (versionAgingDays != null) {
			versionControls.addAttribute("versionAgingDays", versionAgingDays.toString());
		}
		Long versionsMaxFileSize = binder.getMaxFileSize();
		if (versionsMaxFileSize != null) {
			versionControls.addAttribute("versionsMaxFileSize", versionsMaxFileSize.toString());
		}
		Boolean fileEncryptionEnabled = binderModule.isBinderFileEncryptionEnabled(binder);
		if (fileEncryptionEnabled != null) {
			versionControls.addAttribute("fileEncryptionEnabled", fileEncryptionEnabled.toString());
		}
		
		// binder quota
		Element binderQuotaEle = settingsEle.addElement("binderQuota");
		BinderQuota binderQuota = adminModule.getBinderQuota(binder);
		if (binderQuota.getDiskQuota() != null) {
			binderQuotaEle.addAttribute("binderQuota", binderQuota.getDiskQuota().toString());
		}
		
		// filters
		Map<String,String> searchFilters = (Map<String,String>)binder.getProperty(ObjectKeys.BINDER_PROPERTY_FILTERS);
		if (searchFilters != null) {
			Element filtersEle = settingsEle.addElement("filters");
			for (Entry<String, String> me : searchFilters.entrySet()) {
				Element filterEle = filtersEle.addElement("filter");
				filterEle.addAttribute("name", me.getKey());
				filterEle.addAttribute("value", me.getValue());
			}
		}
		
		// binder properties
		String wikiHomePageId = (String)binder.getProperty(ObjectKeys.BINDER_PROPERTY_WIKI_HOMEPAGE);
		if (wikiHomePageId != null && !wikiHomePageId.equals("")) {
			Element wikiHomePageEle = settingsEle.addElement("wikiHomePageId");
			wikiHomePageEle.setText(wikiHomePageId);
		}

		try {
			String properties = binder.getSerializedProperties();
			if (properties!=null) {
                Element propElem = settingsEle.addElement("properties");
                propElem.setText(properties);
            }
		} catch (IOException e) {
			logger.warn("Failed to export properties for binder: " + binder.getPathName() + ".  Some settings such as column layouts will not be included when the binder is imported.", e);
		}
	}

	private static void addAccessControls(WorkArea workArea, Element settingsElement) {
		Long zoneId = RequestContextHolder.getRequestContext().getZoneId();
		Element accessControls = settingsElement.addElement("accessControls");
		if (!workArea.isFunctionMembershipInherited()) {
			List<WorkAreaFunctionMembership> membership = adminModule.getWorkAreaFunctionMemberships(workArea);
			for (WorkAreaFunctionMembership wfm : membership) {
				Element functionEle = accessControls.addElement("function");
				//Get the function (aka role)
				Function f = adminModule.getFunction(wfm.getFunctionId());
				functionEle.addAttribute("id", f.getId().toString());
				functionEle.addAttribute("name", f.getName());
				Element membersEle = functionEle.addElement("members");
	    		Set<Long> ids = wfm.getMemberIds();
	    		for (Long id : ids) {
	    			if (id < 0) {
	    				//This is a special id, add it as such
	    				Element memberEle = membersEle.addElement("specialId");
	    				memberEle.addAttribute("id", id.toString());
	    			}
	    		}
	    		List<UserPrincipal> members = profileDao.loadUserPrincipals(ids, zoneId, true);
				for (UserPrincipal p : members) {
					addPrincipalToDocument(membersEle, p);
				}
			}
			//See if this is an entry
			if (workArea instanceof FolderEntry) {
				if (((FolderEntry) workArea).hasEntryAcl()) {
					accessControls.addAttribute("hasEntryAcl", "true");
					if (((FolderEntry) workArea).isIncludeFolderAcl()) {
						accessControls.addAttribute("includeFolderAcl", "true");
					}
				}
			}
		}
	}
	
	private static void addBinderDefinitions(ZipOutputStream zipOut, Binder binder, 
			Set defListAlreadyAdded, Map reportMap) {
		List<Definition> defListToAdd = new ArrayList<Definition>();
		defListToAdd.add(binder.getEntryDef());

		//Get a list of all of the definitions in use by this binder
		defListToAdd.addAll(binder.getViewDefinitions());
		defListToAdd.addAll(binder.getEntryDefinitions());
		defListToAdd.addAll(binder.getWorkflowDefinitions());

		Map workflowAssociations = binder.getWorkflowAssociations();
		Iterator keyIter = workflowAssociations.keySet().iterator();
		while (keyIter.hasNext()) {
			String key = (String) keyIter.next();
			Definition entryDef = definitionModule.getDefinition(key);
			if (entryDef != null) defListToAdd.add(entryDef);
			Definition workflowDef = (Definition) workflowAssociations.get(key);
			if (workflowDef != null) defListToAdd.add(workflowDef);
		}

		try {
			exportDefinitionList(zipOut, defListToAdd, defListAlreadyAdded);
		} catch(Exception e) {
			logger.error(e);
			Integer c = (Integer)reportMap.get(errors);
			reportMap.put(errors, ++c);
			((List)reportMap.get(errorList)).add(e.getLocalizedMessage());
		}
	}
	
	private static void addEntryDefinitions(ZipOutputStream zipOut, FolderEntry entry, 
			Set defListAlreadyAdded, Map reportMap) {
		List<Definition> defListToAdd = new ArrayList<Definition>();
		Definition def = null;
		if(entry.getEntryDefId() != null)
			def = definitionModule.getDefinition(entry.getEntryDefId());
		if(def != null)
			defListToAdd.add(def);

		for (WorkflowState workflow : entry.getWorkflowStates()) {
			if (workflow != null) {
				defListToAdd.add(workflow.getDefinition());
			}
		}
		try {
			exportDefinitionList(zipOut, defListToAdd, defListAlreadyAdded);
		} catch(Exception e) {
			logger.error(e);
			Integer c = (Integer)reportMap.get(errors);
			reportMap.put(errors, ++c);
			((List)reportMap.get(errorList)).add(e.getLocalizedMessage());
		}
	}
	
	private static void exportDefinitionList(ZipOutputStream zipOut, List<Definition> defListToAdd, Set defListAlreadyAdded)
			throws Exception {
		for (Definition def : defListToAdd) {
			if (def != null && !defListAlreadyAdded.contains(def)) {
				exportDefinition(zipOut, def);
				defListAlreadyAdded.add(def);
			}
		}
	}

	private static void exportDefinition(ZipOutputStream zipOut, Definition def)
			throws Exception {
		String name = def.getName();

		if (Validator.isNull(name))
			name = def.getTitle();

		// We have to use "/" instead of File.separator so the correct directory structure will be created in the zip file.
		zipOut.putNextEntry(new ZipEntry("__definitions" + "/"
				+ name + ".xml"));
		Document defXmlDoc = definitionModule.getDefinitionAsXml(def);
		XmlFileUtil.writeFile(defXmlDoc, zipOut);
		zipOut.closeEntry();
	}

	// Regex patterns used during import to check whether the xml file is for
	// folder, workspace, or entry
	private static Pattern entryPattern = Pattern.compile("e[0-9]{8}");
	private static Pattern workspacePattern = Pattern.compile("." + binderPrefix
			+ "w[0-9]{8}");
	private static Pattern folderPattern = Pattern.compile("." + binderPrefix
			+ "f[0-9]{8}");

	public static Long importZip(Long binderId, InputStream fIn, StatusTicket statusTicket,
			Map reportMap) throws IOException, ExportException {
		Binder binder = binderModule.getBinder(binderId);
		binderModule.checkAccess(binder, BinderOperation.export);
		
		getNumberFormat();
		
		if ( statusTicket != null )
			statusTicket.setStatus(NLT.get("loading.files") + "...");
		
		Map<String, Principal> nameCache = new HashMap();
		ZipInputStream zIn = new ZipInputStream(fIn);

		// key-value pairs: old exported entry id - new entry id assigned during
		// import
		HashMap<Long, Long> entryIdMap = new HashMap<Long, Long>();

		// key-value pairs: old exported binder id - new binder id assigned
		// during import
		HashMap<Long, Long> binderIdMap = new HashMap<Long, Long>();

		// binder properties to be set
		// after imports
		Map<Long, Map<String, Object>> binderPropertyMap = new HashMap<Long, Map<String, Object>>();

		// key-value pairs:  new exported binder id - associated
		// ServerTaskLinkage.
		HashMap<Long, ServerTaskLinkage> taskLinkageMap = new HashMap<Long, ServerTaskLinkage>();
		
		// key-value pairs: old exported definition id - new definition id assigned
		// during import
		HashMap<String, String> definitionIdMap = new HashMap<String, String>();

		//List of new definitions added during this import
		List<String> newDefIds = new ArrayList<String>();

		logger.debug("Unzipping to disk temporarily...");
		String tempDir = deploy(zIn, reportMap);
		logger.debug("Unzipping completed");

		try {
			File tempDirFile = new File(tempDir);
			importDir(tempDirFile, tempDir, binderId, entryIdMap, binderIdMap, binderPropertyMap, taskLinkageMap, definitionIdMap, newDefIds,
					statusTicket, reportMap, nameCache);
		} catch(Exception e) {
				if (e instanceof ExportException)
					throw (ExportException) e;
				else
					throw new ExportException(e);
		} finally {
			FileUtil.deltree(tempDir);
		}
		
		//After importing binders and entries, perform a final fix-up of mappings (landing pages and markup)
		fixUpLinks(binderIdMap, binderPropertyMap, taskLinkageMap, entryIdMap);
		//Make sure the principal mappings and internal defIds are handled
		for (String defId : newDefIds) {
			definitionModule.updateDefinitionReferences(defId);
		}
		
		//Try to return the first binder created
		if ( binderIdMap.size() > 0 )
		{
			Iterator<Long> iter;
			Long firstId = null;
			
			Collection<Long> valueSet = binderIdMap.values();
			iter = valueSet.iterator();
			while ( iter.hasNext() ) {
				Long nextId = iter.next();
				if (firstId == null || firstId > nextId) {
					firstId = nextId;
				}
			}
			return firstId;
		}
		
		return null;
	}

	private static void importDir(File currentDir, String tempDir, Long topBinderId,
			Map<Long,Long> entryIdMap, Map<Long,Long> binderIdMap, Map<Long, Map<String, Object>> binderPropertyMap, Map<Long, ServerTaskLinkage> taskLinkageMap, 
			Map<String, String> definitionIdMap, List<String> newDefIds, 
			StatusTicket statusTicket, Map reportMap, Map<String, Principal> nameCache) throws IOException {

		Binder topBinder = loadBinder(topBinderId);

		SortedMap sortMap = new TreeMap(String.CASE_INSENSITIVE_ORDER);
		File[] tempChildren = currentDir.listFiles();

		for (File tempChild : tempChildren) {
			sortMap.put(tempChild.getAbsolutePath(), tempChild);
		}

		Set keys = sortMap.keySet();
		Iterator keyIter = keys.iterator();

		while (keyIter.hasNext()) {
			File child = (File) sortMap.get(keyIter.next());

			if (child.isDirectory())
				importDir(child, tempDir, topBinderId, entryIdMap, binderIdMap, binderPropertyMap, taskLinkageMap, definitionIdMap, newDefIds,
						statusTicket, reportMap, nameCache);
			else {
				String fileExt = EntityIndexUtils.getFileExtension(child
						.getName().toLowerCase());

				if (child.getAbsolutePath().startsWith(
						tempDir + File.separator + "__definitions"
								+ File.separator )) {

					// adding definition to top binder locally if it does not
					// already exist

					String xmlStr = null;

					BufferedInputStream input = new BufferedInputStream(new FileInputStream(child));

					ByteArrayOutputStream output = new ByteArrayOutputStream(12288);

					int data = 0;
					while ((data = input.read()) != -1) {
						output.write(data);
					}

					xmlStr = output.toString();
					output.close();
					input.close();

					Document tempDoc = getDocument(xmlStr, nameCache);
					String defId = getDefinitionDatabaseId(tempDoc);
					String internalId = getDefinitionInternalId(tempDoc);
					String defName = getDefinitionName(tempDoc);
					//See if this definition already exists
					Definition def = null;
					if (!definitionIdMap.containsKey(defId)) {
						if (!internalId.equals("")) {
							//This is a default definition, get its id
							try {
								def = definitionModule.getDefinitionByReservedId(internalId);
							} catch(NoDefinitionByTheIdException e) {}
							if (def != null) {
								definitionIdMap.put(defId, def.getId());
							} else {
								try {
									def = definitionModule.getDefinitionByName(null, false, defName);
								} catch(NoDefinitionByTheIdException e) {}
								if (def != null) definitionIdMap.put(defId, def.getId());
							}
						} else {
							try {
								def = definitionModule.getDefinitionByName(null, false, defName);
							} catch(NoDefinitionByTheIdException e) {}
							if (def == null) {
								try {
									def = definitionModule.getDefinitionByName(topBinder, false, defName);
								} catch(NoDefinitionByTheIdException e) {}
							}
							if (def != null) definitionIdMap.put(defId, def.getId());
						}
					}

					if (ObjectKeys.DEFAULT_MIRRORED_FILE_ENTRY_DEF.equals(internalId)
							|| ObjectKeys.DEFAULT_MIRRORED_FILE_FOLDER_DEF.equals(internalId)) {
						// don't add definitions if they are for mirrored file
						// entries
						// or mirrored file folders
					} else {
						if (def == null) def = definitionModule.addDefinition(tempDoc, topBinder, false);
						if (def != null) {
							definitionIdMap.put(defId, def.getId());
							//Remember the new def so we can update its principal ids later
							newDefIds.add(def.getId());
						}
					}

				} else if (fileExt.equals("xml")) {
					String fileName;
					
					fileName = child.getName();
					
					// need to check if this xml file is for a folder,
					// workspace,
					// entry, or just an attachment.
					// checking by regex-ing the filename.

					// entry?
					Matcher m = entryPattern.matcher( fileName );
					boolean result = m.lookingAt();

					if (result) {
						String xmlStr = null;

						BufferedInputStream input = new BufferedInputStream(new FileInputStream(child));
						ByteArrayOutputStream output = new ByteArrayOutputStream(12288);

						int data = 0;
						while ((data = input.read()) != -1) {
							output.write(data);
						}

						xmlStr = output.toString();
						output.close();
						input.close();

						Document tempDoc = getDocument(xmlStr, nameCache);
						String defId = getEntityDefinitionId(tempDoc);
						String defName = getEntityDefinitionName(tempDoc);
						Definition def = getTargetDefinition(defId, defName, definitionIdMap, reportMap, topBinder);

						if (def != null && ObjectKeys.DEFAULT_MIRRORED_FILE_ENTRY_DEF.equals(def.getId())) {
							Definition newDef = definitionModule.getDefinitionByReservedId(ObjectKeys.DEFAULT_LIBRARY_ENTRY_DEF);
							if (newDef != null) {
								setEntityDefinitionId(tempDoc, newDef.getId());
								defId = newDef.getId();
								def = newDef;
							}
						}

						String entType = getEntityType(tempDoc);
						Long entryId = Long.valueOf(getId(tempDoc));
						Long binderId = Long.valueOf(getBinderId(tempDoc));

						Long newBinderId = (Long) binderIdMap.get(binderId);

						if (newBinderId == null) {
							newBinderId = topBinderId;
						}

						String parentIdStr = getParentId(tempDoc);
						Long parentId = null;

						if (!Validator.isNull(parentIdStr))
							parentId = Long.valueOf(parentIdStr);

						// check actual entity type of the data in the xml file
						if (entType.equals("entry")) {
							if (parentId == null) {
								try {
									folder_addEntryWithXML(null, newBinderId, topBinderId,
										def, tempDoc.asXML(), tempDir, entryIdMap, binderIdMap, 
										definitionIdMap, entryId, statusTicket, reportMap, nameCache);
							
									Integer count = (Integer)reportMap.get(entries);
									reportMap.put(entries, ++count);
								} catch(Exception e) {
									Integer c = (Integer)reportMap.get(errors);
									reportMap.put(errors, ++c);
									((List)reportMap.get(errorList)).add(e.getLocalizedMessage());
									logger.error(e);
								}
							} else {
								Long newParentId = (Long) entryIdMap.get(parentId);
								try {
									folder_addReplyWithXML(null, newBinderId, topBinderId,
											newParentId, def, tempDoc.asXML(), tempDir, entryIdMap, binderIdMap, 
											definitionIdMap, entryId, reportMap, nameCache);
								} catch(Exception e) {
									Integer c = (Integer)reportMap.get(errors);
									reportMap.put(errors, ++c);
									((List)reportMap.get(errorList)).add(e.getLocalizedMessage());
									logger.error(e);
								}
							}
						}
					}

					// workspace?
					m = workspacePattern.matcher( fileName );
					result = m.lookingAt();

					if (result) {
						String xmlStr = null;
						BufferedInputStream input = new BufferedInputStream(new FileInputStream(child));
						ByteArrayOutputStream output = new ByteArrayOutputStream(12288);
						int data = 0;

						while ((data = input.read()) != -1) {
							output.write(data);
						}

						xmlStr = output.toString();
						output.close();
						input.close();

						Document tempDoc = getDocument(xmlStr, nameCache);
						String defId = getEntityDefinitionId(tempDoc);
						Definition def = null;
						if (definitionIdMap.containsKey(defId)) 
							def = coreDao.loadDefinition(definitionIdMap.get(defId), null);

						String entType = getEntityType(tempDoc);
						Long parentId = null;
						Long newParentId = null;
						Long binderId = Long.valueOf(getId(tempDoc));
						if (!Validator.isNull(getBinderId(tempDoc))) 
							parentId = Long.valueOf(getBinderId(tempDoc));
						newParentId = (Long) binderIdMap.get(parentId);

						if (newParentId == null) {
							newParentId = topBinderId;
						}

						// check actual entity type of the data in the xml file
						if (entType.equals("workspace")) {
							try {
								binder_addBinderWithXML(null, newParentId, def,
										xmlStr, binderId, topBinderId, binderIdMap, binderPropertyMap, taskLinkageMap, definitionIdMap, tempDir, reportMap,
										statusTicket, nameCache);
								Integer count = (Integer)reportMap.get(workspaces);
								reportMap.put(workspaces, ++count);
							} catch(Exception e) {
								Integer c = (Integer)reportMap.get(errors);
								reportMap.put(errors, ++c);
								((List)reportMap.get(errorList)).add(e.getLocalizedMessage());
								logger.error(e);
							}
						}
					}

					// folder?
					m = folderPattern.matcher( fileName );
					result = m.lookingAt();

					if (result) {
						String xmlStr = null;
						BufferedInputStream input = new BufferedInputStream(new FileInputStream(child));
						ByteArrayOutputStream output = new ByteArrayOutputStream(12288);
						int data = 0;

						while ((data = input.read()) != -1) {
							output.write(data);
						}

						xmlStr = output.toString();
						output.close();
						input.close();

						Document tempDoc = getDocument(xmlStr, nameCache);
						String defId = getEntityDefinitionId(tempDoc);
						Definition def = null;
						if (definitionIdMap.containsKey(defId)) 
							def = coreDao.loadDefinition(definitionIdMap.get(defId), null);

						if (def != null && ObjectKeys.DEFAULT_MIRRORED_FILE_FOLDER_DEF.equals(def.getId())) {
							def = definitionModule.getDefinitionByReservedId(ObjectKeys.DEFAULT_LIBRARY_FOLDER_DEF);
							if (def != null) {
								setEntityDefinitionId(tempDoc, def.getId());
								defId = def.getId();
							}
						}

						String entType = getEntityType(tempDoc);
						Long parentId = Long.valueOf(getBinderId(tempDoc));
						Long binderId = Long.valueOf(getId(tempDoc));

						Long newParentId = (Long) binderIdMap.get(parentId);

						if (newParentId == null) {
							newParentId = topBinderId;
						}

						// check actual entity type of the data in the xml file
						if (entType.equals("folder")) {
							try {
								binder_addBinderWithXML(null, newParentId, def,
										tempDoc.asXML(), binderId, topBinderId, binderIdMap, binderPropertyMap, taskLinkageMap, definitionIdMap, tempDir, reportMap, 
										statusTicket, nameCache);
								Integer count = (Integer)reportMap.get(folders);
								reportMap.put(folders, ++count);
							} catch(Exception e) {
								Integer c = (Integer)reportMap.get(errors);
								reportMap.put(errors, ++c);
								((List)reportMap.get(errorList)).add(e.getLocalizedMessage());
								logger.error(e);
							}
						}
					}
				}
			}
		}
	}
	
	private static void fixUpLinks(Map<Long, Long> binderIdMap, Map<Long, Map<String, Object>> binderPropertyMap, 
			Map<Long, ServerTaskLinkage> taskLinkageMap, Map<Long, Long> entryIdMap) {
		Long zoneId = RequestContextHolder.getRequestContext().getZoneId();
		Iterator itBinders = binderIdMap.entrySet().iterator();
		while (itBinders.hasNext()) {
			Map.Entry me = (Map.Entry) itBinders.next();
			Binder binder = binderModule.getBinder((Long) me.getValue());
			try {
				MarkupUtil.fixupImportedLinks(binder, (Long)me.getKey(), binderIdMap, entryIdMap);
			} catch(Exception e) {
				//Something couldn't be fixed up, log an error and leave the link as is
				logger.error(e);
			}
			Map<String, Object> binderProperties = binderPropertyMap.get(binder.getId());
			if (binderProperties != null) {
				String wikiHomePageId = (String) binderProperties.get(ObjectKeys.BINDER_PROPERTY_WIKI_HOMEPAGE);
				if (wikiHomePageId != null && !wikiHomePageId.equals("")) {
					Long newWikiHomePageId = entryIdMap.get(Long.valueOf(wikiHomePageId));
					if (newWikiHomePageId != null) {
						binder.setProperty(ObjectKeys.BINDER_PROPERTY_WIKI_HOMEPAGE, String.valueOf(newWikiHomePageId));
					}
				}
			}
		}
		Iterator itEntries = entryIdMap.entrySet().iterator();
		while (itEntries.hasNext()) {
			Map.Entry me = (Map.Entry) itEntries.next();
			FolderEntry entry = folderModule.getEntry(null, (Long) me.getValue());
			try {
				MarkupUtil.fixupImportedLinks(entry, (Long)me.getKey(), binderIdMap, entryIdMap);
			} catch(Exception e) {
				//Something couldn't be fixed up, log an error and leave the link as is
				logger.error(e);
			}
		}
		ServerTaskLinkage.fixupAndStoreTaskLinkages(
			binderModule,
			taskLinkageMap,
			entryIdMap);
	}

	private static String getEntityType(Document entity) {
		return entity.getRootElement().getName();
	}

	private static String getEntityDefinitionId(Document entity) {
		return entity.getRootElement().attributeValue("definitionId", "");
	}

	private static String getEntityDefinitionName(Document entity) {
		return entity.getRootElement().attributeValue("definitionName", "");
	}

	private static void setEntityDefinitionId(Document entity, String defId) {
		entity.getRootElement().attribute("definitionId").setValue(defId);
	}

	private static String getDefinitionDatabaseId(Document entity) {
		return entity.getRootElement().attributeValue("databaseId", "");
	}

	private static String getDefinitionInternalId(Document entity) {
		return entity.getRootElement().attributeValue("internalId", "");
	}

	private static String getDefinitionName(Document entity) {
		return entity.getRootElement().attributeValue("name", "");
	}

	private static String getId(Document entity) {
		return entity.getRootElement().attributeValue("id", "");
	}

	private static String getBinderId(Document entity) {
		return entity.getRootElement().attributeValue("binderId", "");
	}

	private static String getParentId(Document entity) {
		return entity.getRootElement().attributeValue("parentId", "");
	}
	
	private static Definition getTargetDefinition(String defId, String defName, 
			Map<String, String> definitionIdMap, Map reportMap, Binder topBinder) {
		Definition def = null;
		if (definitionIdMap.containsKey(defId)) {
			def = coreDao.loadDefinition(definitionIdMap.get(defId), null);
		} else if (!defId.equals("") && !defName.equals("")) {
			try {
				def = definitionModule.getDefinitionByName(null, false, defName);
				if (def != null) definitionIdMap.put(defId, def.getId());
			} catch(Exception e) {
				try {
					def = definitionModule.getDefinitionByName(topBinder, false, defName);
					if (def != null) definitionIdMap.put(defId, def.getId());
				} catch(Exception e2) {
					Integer c = (Integer)reportMap.get(errors);
					reportMap.put(errors, ++c);
					((List)reportMap.get(errorList)).add(e.getLocalizedMessage());
				}
			}
		}
		return def;
	}

	private static long binder_addBinderWithXML(String accessToken, long parentId, Definition def,
			String inputDataAsXML, long binderId, Long topBinderId, Map<Long,Long> binderIdMap, Map<Long, Map<String, Object>> binderPropertyMap, 
			Map<Long, ServerTaskLinkage> taskLinkageMap, 
			Map<String, String> definitionIdMap, String tempDir, Map reportMap, 
			StatusTicket statusTicket, Map<String, Principal> nameCache) {

		final Binder topBinder = loadBinder(topBinderId);
		final Binder parentBinder = loadBinder(parentId);

		final Document doc = getDocument(inputDataAsXML, nameCache);
		final Map<String, String> fDefIdMap = new HashMap<String, String>(definitionIdMap);

		try {
			if(logger.isDebugEnabled())
				logger.debug("Adding binder to parent " + parentId + " with definition " + def.getId());
			//Make sure there is no other binder with the same name in the destination binder.
			String title = doc.getRootElement().attributeValue("title", "");
			if (!title.equals("")) {
		     	String newTitle = BinderHelper.getUniqueBinderTitleInParent(title, parentBinder, null);
		    	if (!newTitle.equals(title)) {
		    		//There was a conflict, so update the title to the unique one
		    		doc.getRootElement().addAttribute("title", newTitle);
		    		Element titleAttrEle = (Element)doc.getRootElement().selectSingleNode("./attribute[@name='title']");
		    		if (titleAttrEle != null) {
		    			titleAttrEle.setText(newTitle);
		    		}
		    	}
			}

			Binder newBinder = binderModule.addBinder(new Long(parentId),
					def.getId(), new DomInputData(doc, iCalModule),
					new HashMap(), null);
			Long newBinderId = newBinder.getId().longValue();
			binderIdMap.put(binderId, newBinderId);
			final Binder binder = loadBinder(newBinderId);
			transactionTemplate.execute(new TransactionCallback() {
				@Override
				public Object doInTransaction(TransactionStatus status) {
					String libraryFolder = doc.getRootElement().attributeValue("libraryFolder", "");
					if (libraryFolder.equalsIgnoreCase("true")) {
						binder.setLibrary(true);
					} else {
						binder.setLibrary(false);
					}
					String uniqueTitles = doc.getRootElement().attributeValue("uniqueTitles", "");
					if (uniqueTitles.equalsIgnoreCase("true")) {
						binder.setUniqueTitles(true);
					} else {
						binder.setUniqueTitles(false);
					}
					return null;
				}
			});

			// add file attachments
			addBinderFileAttachments(newBinderId, topBinderId, binderIdMap, doc, tempDir, reportMap);

			// team members
			if(logger.isDebugEnabled())
				logger.debug("Adding team members to binder " + newBinderId);
			addTeamMembers(newBinderId, doc, nameCache);

			final Map rMap = reportMap;
			final Map fNameCache = nameCache;
			
			// binder settings
			importSettingsList(doc, binder, fDefIdMap, rMap, topBinder, fNameCache, binderPropertyMap);

			// workflows
			if ( statusTicket != null )
				statusTicket.setStatus(NLT.get("administration.export_import.importing", new String[] {binder.getPathName()}));

			if(logger.isDebugEnabled())
				logger.debug("Importing workflows for the binder " + newBinderId);
			// workflows
			importWorkflows(doc, binder, fDefIdMap, rMap, fNameCache, topBinder, true);
			
			// task linkage
			importTaskLinkage(doc, newBinderId, taskLinkageMap);
			
			// 05/30/2015 JK (bug #932689) Simply evicting the newly added binder alone is not enough. We need to clear 
			// the entire session so that the next iteration can start over with a fresh new empty session. Otherwise, 
			// we tend to run into this infamous "a different object with the same identifier value was already associated
			// with the session" Hibernate error because the application ends up operating with duplicate copies of the 
			// same object where one is associated with the session while the other is detached. Of course, it is NOT 
			// sufficient to  just clear the session in order to avoid that situation. More importantly, we need to 
			// ensure that the application code doesn't hang on to and reuse loaded (and subsequently detached) objects
			// across iterations. Also critically important is to NOT misuse transaction boundaries.
			// To that regard, unless other precautions are taken appropriately, simply (and blindly?) clearing 
			// session cannot do any magic. But when other things are in good order, doing this at the right place at
			// the right time should help with resource management, so it's not unimportant.
			coreDao.clear();
			
			return newBinderId;
		} catch (WriteFilesException e) {
			Integer c = (Integer)reportMap.get(errors);
			reportMap.put(errors, ++c);
			((List)reportMap.get(errorList)).add(e.getLocalizedMessage());
			throw new RuntimeException(e);
		} catch (AccessControlException e) {
			throw new RuntimeException(e);
		} catch (WriteEntryDataException e) {
			Integer c = (Integer)reportMap.get(errors);
			EntryDataErrors errors = e.getErrors();
			List problems = errors.getProblems();
			for (int i = 0; i < problems.size(); i++) {
				((List)reportMap.get(errorList)).add(problems.get(i).toString());
				c++;
			}
			reportMap.put(errors, c);
			throw new RuntimeException(e);
		}
	}

	private static long folder_addEntryWithXML(String accessToken, long binderId, Long topBinderId,
			Definition def, String inputDataAsXML, String tempDir,
			Map entryIdMap, Map binderIdMap, Map<String, String> definitionIdMap, 
			Long entryId, StatusTicket statusTicket,
			Map reportMap, Map<String, Principal> nameCache) {
		return addFolderEntry(accessToken, binderId, topBinderId, def,
				inputDataAsXML, tempDir, entryIdMap, binderIdMap, definitionIdMap, entryId, statusTicket, 
				reportMap, nameCache);
	}

	private static long addFolderEntry(String accessToken, long binderId, Long topBinderId,
			Definition def, String inputDataAsXML, String tempDir, Map entryIdMap, 
			Map binderIdMap, Map<String, String> definitionIdMap, 
			Long entryId, StatusTicket statusTicket, Map reportMap,
			Map<String, Principal> nameCache) {

		final Binder topBinder = loadBinder(topBinderId);
		final Document doc = getDocument(inputDataAsXML, nameCache);
		Map options = new HashMap();
		//Set the entry creator and modifier fields
		setSignature(options, doc, nameCache);
		options.put(ObjectKeys.INPUT_OPTION_NO_INDEX, Boolean.TRUE);

		FolderEntry entry = null;
		try {
			// create new entry
			if(logger.isDebugEnabled())
				logger.debug("Adding entry in binder " + binderId + " with definition " + def.getId());
			entry = folderModule.addEntry(new Long(binderId),
					def.getId(), new DomInputData(doc, iCalModule), null,
					options);
			long newEntryId = entry.getId().longValue();

			// add entry id to entry id map
			entryIdMap.put(entryId, newEntryId);
			
			// add file attachments
			try {
				addFileAttachments(binderId, newEntryId, topBinderId, doc, tempDir, reportMap, binderIdMap, entryIdMap);
			} catch(Exception e) {
				Integer c = (Integer)reportMap.get(errors);
				reportMap.put(errors, ++c);
				((List)reportMap.get(errorList)).add(e.getLocalizedMessage() + " (" + entry.getTitle() + ")");
				if (entry != null) {
					entryIdMap.remove(entryId);
					folderModule.deleteEntry(entry.getParentBinder().getId(), entry.getId());
				}
				throw new RuntimeException(e);
			}
			// workflows
			if(logger.isDebugEnabled())
				logger.debug("Importing workflows for the entry " + newEntryId);
			try {
				final Map<String,String> fDefinitionIdMap = definitionIdMap;
				final Map rMap = reportMap;
				final Map fNameCache = nameCache;
				final FolderEntry fEntry = entry;
				if ( statusTicket != null )
					statusTicket.setStatus(NLT.get("administration.export_import.importingEntry", 
						new String[] {"[" + String.valueOf(reportMap.get("entries")) + "] " + entry.getTitle()}));
				importWorkflows(doc, fEntry, fDefinitionIdMap, rMap, fNameCache, topBinder, false);
			} catch(Exception e) {}
			
			//Access controls
			WorkArea workArea = (WorkArea)entry;
			addAccessControls(workArea, doc, reportMap, nameCache);

			// Index the entry only once
			if(logger.isDebugEnabled())
				logger.debug("Indexing the entry " + newEntryId);
			folderModule.indexEntry(entry, false);
			
			// We're done with the entry. Kick it out of cache.
			// Do NOT clear the entire session here though, since it would cause a lot of trouble
			// from the plenty of detached entities that are still floating around within VM
			// and that will come into play in subsequent iterations of this whole action...
			if(logger.isDebugEnabled())
				logger.debug("Clearing the session of the entry");
			
			return newEntryId;
		} catch (WriteFilesException e) {
			Integer c = (Integer)reportMap.get(errors);
			reportMap.put(errors, ++c);
			((List)reportMap.get(errorList)).add(e.getLocalizedMessage());
			if (entry != null) {
				entryIdMap.remove(entryId);
				folderModule.deleteEntry(entry.getParentBinder().getId(), entry.getId());
			}
			throw new RuntimeException(e);
		} catch (WriteEntryDataException e) {
			Integer c = (Integer)reportMap.get(errors);
			reportMap.put(errors, ++c);
			((List)reportMap.get(errorList)).add(e.getLocalizedMessage());
			if (entry != null) {
				entryIdMap.remove(entryId);
				folderModule.deleteEntry(entry.getParentBinder().getId(), entry.getId());
			}
			throw new RuntimeException(e);
		}
	}

	private static long folder_addReplyWithXML(String accessToken, long binderId, Long topBinderId,
			long parentId, Definition def, String inputDataAsXML,
			String tempDir, Map entryIdMap, Map binderIdMap, Map<String, String> definitionIdMap, 
			Long entryId, Map reportMap, Map<String, Principal> nameCache) {
		return addReply(accessToken, binderId, topBinderId, parentId, def, inputDataAsXML, 
				tempDir, entryIdMap, binderIdMap, definitionIdMap, entryId, reportMap, nameCache);
	}

	private static long addReply(String accessToken, long binderId, Long topBinderId, long parentId,
			Definition def, String inputDataAsXML, String tempDir,
			Map entryIdMap, Map binderIdMap, Map<String, String> definitionIdMap, 
			Long entryId, Map reportMap, Map<String, Principal> nameCache) {

		final Binder topBinder = loadBinder(topBinderId);
		final Document doc = getDocument(inputDataAsXML, nameCache);

		Map options = new HashMap();
		//Set the entry creator and modifier fields
		setSignature(options, doc, nameCache);
		options.put(ObjectKeys.INPUT_OPTION_NO_INDEX, Boolean.TRUE);
		FolderEntry entry = null;
		try {
			// add new reply
			if(logger.isDebugEnabled())
				logger.debug("Adding reply to entry " + parentId + " + in binder " + binderId);
			entry = folderModule.addReply(new Long(binderId),
					new Long(parentId), def.getId(),
					new DomInputData(doc, iCalModule), null, options);
			long newEntryId = entry.getId().longValue();

			// add entry reply id to entry id map
			entryIdMap.put(entryId, newEntryId);

			// add file attachments
			try {
				addFileAttachments(binderId, newEntryId, topBinderId, doc, tempDir, reportMap, binderIdMap, entryIdMap);
			} catch(Exception e) {
				Integer c = (Integer)reportMap.get(errors);
				reportMap.put(errors, ++c);
				((List)reportMap.get(errorList)).add(e.getLocalizedMessage() + " (" + entry.getTitle() + ")");
				if (entry != null) {
					entryIdMap.remove(entryId);
					folderModule.deleteEntry(entry.getParentBinder().getId(), entry.getId());
				}
				throw new RuntimeException(e);
			}

			// workflows
			if(logger.isDebugEnabled())
				logger.debug("Importing workflows for the reply " + newEntryId);			
			try {
				final Map<String,String> fDefinitionIdMap = definitionIdMap;
				final Map rMap = reportMap;
				final Map fNameCache = nameCache;
				final FolderEntry fEntry = entry;
				importWorkflows(doc, fEntry, fDefinitionIdMap, rMap, fNameCache, topBinder, false);
			} catch(Exception e) {
				Integer c = (Integer)reportMap.get(errors);
				reportMap.put(errors, ++c);
				((List)reportMap.get(errorList)).add(e.getLocalizedMessage());
			}
			// Index the reply only once (and it's top entry)
			if(logger.isDebugEnabled())
				logger.debug("Indexing reply " + newEntryId);
			folderModule.indexEntry(entry, false);
			folderModule.indexEntry(entry.getTopEntry(), false);

			return newEntryId;
		} catch (WriteFilesException e) {
			Integer c = (Integer)reportMap.get(errors);
			reportMap.put(errors, ++c);
			((List)reportMap.get(errorList)).add(e.getLocalizedMessage());
			if (entry != null) {
				entryIdMap.remove(entryId);
				folderModule.deleteEntry(entry.getParentBinder().getId(), entry.getId());
			}
			throw new RuntimeException(e);
		} catch (WriteEntryDataException e) {
			Integer c = (Integer)reportMap.get(errors);
			reportMap.put(errors, ++c);
			((List)reportMap.get(errorList)).add(e.getLocalizedMessage());
			if (entry != null) {
				entryIdMap.remove(entryId);
				folderModule.deleteEntry(entry.getParentBinder().getId(), entry.getId());
			}
			throw new RuntimeException(e);
		}
	}

	private static Document getDocument(String xml, Map nameCache) {
		// Parse XML string into a document tree.
		try {
			Document doc = XmlUtil.parseText(xml);
			buildNameCache(doc, nameCache);
			//Check to see if this file format is legal
			Element rootEle = doc.getRootElement();
			String version = rootEle.attributeValue("exportVersion", exportVersion);
			if (Integer.valueOf(version) < Integer.valueOf(exportVersionV1))
				throw new ExportException(
						new Exception("Export file format is out of date; please re-create this export file."));
			return doc;
		} catch (DocumentException e) {
			logger.error(e);
			throw new IllegalArgumentException(e.toString());
		}
	}
	
	//Access control settings
	private static void addAccessControls(WorkArea workArea, Document entityDoc, 
			Map reportMap, Map<String, Principal> nameCache) {
		String zoneUUID = entityDoc.getRootElement().attributeValue("zoneUUID", "");
		Element accessControls = (Element)entityDoc.getRootElement().selectSingleNode("//settings//accessControls");
		List<Element> functions = entityDoc.selectNodes("//settings//accessControls//function");
		if (!functions.isEmpty()) {
			//There is an access control setting for this binder or entry. Go set it.
			List<Function> zoneFunctions;
			if (workArea instanceof FolderEntry) {
				zoneFunctions = adminModule.getFunctions(ObjectKeys.ROLE_TYPE_ENTRY);
			} else {
				zoneFunctions = adminModule.getFunctions(ObjectKeys.ROLE_TYPE_BINDER);
			}
			adminModule.setWorkAreaFunctionMembershipInherited(workArea, false);
			Map functionMemberships = new HashMap();
			
			for (Element functionEle : functions) {
				String fId = functionEle.attributeValue("id", "");
				String fName = functionEle.attributeValue("name", "");
				//Try to match the functions on this system with those from the export system
				Function f = null;
				for (Function zoneF : zoneFunctions) {
					if (zoneF.getName().equals(fName)) {
						f = zoneF;
						break;
					}
				}
				if (f != null) {
					if (!functionMemberships.containsKey(f.getId())) {
						functionMemberships.put(f.getId(), new HashSet());
					}
					//Get the membership (specialIds and principals)
					Set members = (Set)functionMemberships.get(f.getId());
					List<Element> specialIds = functionEle.selectNodes("members//specialId");
					for (Element specialIdEle : specialIds) {
						String specialId = specialIdEle.attributeValue("id", "");
						if (!specialId.equals("")) members.add(Long.valueOf(specialId));
					}
					List<Element> principalEles = functionEle.selectNodes("members//principal");
					for (Element principalEle : principalEles) {
						String name = principalEle.attributeValue("name", "");
						String emailAdr = principalEle.attributeValue("emailAddress", "");
						Principal p = matchPrincipal(name, emailAdr, zoneUUID, nameCache);
						if (p != null) {
							members.add(p.getId());
						} else {
							//This user or group wasn't found, let people know
							Integer c = (Integer)reportMap.get(errors);
							reportMap.put(errors, ++c);
							String[] eArgs = new String[2];
							eArgs[0] = name;
							eArgs[1] = emailAdr;
							String errStr = NLT.get("export.error.settingAccessControl.principalNotFound", eArgs);
							((List)reportMap.get(errorList)).add(errStr);
						}
					}
				} else {
					//Could not match the function with any on this system. Report this
					Integer c = (Integer)reportMap.get(errors);
					reportMap.put(errors, ++c);
					String[] eArgs = new String[1];
					eArgs[0] = fName;
					String errStr = NLT.get("export.error.settingAccessControl.functionNotFound", eArgs);
					((List)reportMap.get(errorList)).add(errStr);
				}
			}
			//See if this is an entry
			String hasEntryAcl = accessControls.attributeValue("hasEntryAcl", "");
			String includeFolderAcl = accessControls.attributeValue("includeFolderAcl", "");
			if (workArea instanceof FolderEntry && "true".equals(hasEntryAcl)) {
				if (workArea instanceof FolderEntry && "true".equals(includeFolderAcl)) {
					adminModule.setEntryHasAcl(workArea, Boolean.TRUE, Boolean.TRUE);
				} else {
					adminModule.setEntryHasAcl(workArea, Boolean.TRUE, Boolean.FALSE);
				}
			}
			//Now, the functions are set up with all the memberships, set it as the ACL
			adminModule.setWorkAreaFunctionMemberships(workArea, functionMemberships);
		}
	}

	private static void addFileAttachments(Long binderId, Long entryId, Long topBinderId, 
			Document entityDoc, String tempDir, Map reportMap, Map binderIdMap, Map entryIdMap) throws Exception {

		String hrefPath = getHrefPath(binderId, entryId, topBinderId, binderIdMap, entryIdMap);
		String xPath = "//attribute[@type='attachFiles']//file";
		List attachFiles = entityDoc.selectNodes(xPath);

		for (Element fileEle : (List<Element>) attachFiles) {
			boolean handled = false;

			String href = tempDir + File.separator + hrefPath
					+ fileEle.attributeValue("href", "");
			
			String filename = fileEle.getText();
			
			int numVersions = Integer.valueOf(fileEle
					.attributeValue("numVersions", "0"));
			
			String versionsDir = null;

			if (numVersions > 1) {
				versionsDir = tempDir + File.separator + hrefPath
						+ fileEle.attributeValue("href", "") + ".versions";
			}

			// see if there's a matching attachment of type 'file'
			String fileXPath = "//attribute[@type='file']//value";
			List fileList = entityDoc.selectNodes(fileXPath);

			for (Element fileListEle : (List<Element>) fileList) {
				if (fileListEle.getText().equals(filename)) {
					String name = fileListEle.getParent()
							.attributeValue("name", "");
					addFileVersions(binderId, entryId, name, filename, href,
							numVersions, versionsDir, reportMap);
					handled = true;
					break;
				}
			}

			// if not yet found, see if there's a matching attachment of type
			// 'graphic'

			if (!handled) {
				String graphicXPath = "//attribute[@type='graphic']//value";
				List graphicList = entityDoc.selectNodes(graphicXPath);

				for (Element graphicListEle : (List<Element>) graphicList) {
					if (graphicListEle.getText().equals(filename)) {
						String name = graphicListEle.getParent()
								.attributeValue("name", "");
						addFileVersions(binderId, entryId, name, filename,
								href, numVersions, versionsDir, reportMap);
						handled = true;
						break;
					}
				}
			}

			// if not yet found, see if there's a match in the rest of the
			// attachments

			if (!handled) {
				String attachsXPath = "//attribute[@type='attachFiles']//file";
				List attachsList = entityDoc.selectNodes(attachsXPath);

				for (Element attachsListEle : (List<Element>) attachsList) {
					if (attachsListEle.getText().equals(filename)) {
						String name = attachsListEle.getParent()
								.attributeValue("name", "")
								+ "1";
						addFileVersions(binderId, entryId, name, filename,
								href, numVersions, versionsDir, reportMap);
						handled = true;
						break;
					}
				}
			}
		}
	}

	private static void addFileVersions(Long binderId, Long entryId,
			String fileDataItemName, String filename, String href,
			int numVersions, String versionsDir, Map reportMap) throws Exception {
		Map options = new HashMap();
		options.put(ObjectKeys.INPUT_OPTION_NO_INDEX, Boolean.TRUE);

		String fileExt = EntityIndexUtils.getFileExtension(filename);
		InputStream iStream = null;

		if (numVersions > 1) {
			for (int i = 1; i < numVersions; i++) {
				try {
					iStream = new FileInputStream(new File(versionsDir
							+ File.separator + i + "." + fileExt));
					if(logger.isDebugEnabled())
						logger.debug("Adding file " + filename + " to entry " + entryId + " in binder " + binderId);
					folderModule.modifyEntry(binderId, entryId,
							fileDataItemName, filename, iStream, options);
					iStream.close();
					Integer count = (Integer)reportMap.get(files);
					reportMap.put(files, ++count);
				} catch (Exception e) {
					Integer c = (Integer)reportMap.get(errors);
					reportMap.put(errors, ++c);
					((List)reportMap.get(errorList)).add(NLT.get("export.error.noVersionFile", new String[] {filename}));
				}
			}
		}

		try {
			iStream = new FileInputStream(new File(href));
			if (iStream != null) {
				if(logger.isDebugEnabled())
					logger.debug("Adding file " + filename + " to entry " + entryId + " in binder " + binderId);
				folderModule.modifyEntry(binderId, entryId, fileDataItemName,
						filename, iStream, options);
				iStream.close();
				Integer count = (Integer)reportMap.get(files);
				reportMap.put(files, ++count);
			} else {
				Integer c = (Integer)reportMap.get(errors);
				reportMap.put(errors, ++c);
				((List)reportMap.get(errorList)).add(NLT.get("export.error.noAttachedFile", new String[] {filename}));
			}
		} catch (FileNotFoundException fnf) {
			Integer c = (Integer)reportMap.get(errors);
			reportMap.put(errors, ++c);
			((List)reportMap.get(errorList)).add(NLT.get("export.error.noAttachedFile", new String[] {filename}));
		} catch (Exception e) {
			logger.error(e);
			Integer c = (Integer)reportMap.get(errors);
			reportMap.put(errors, ++c);
			((List)reportMap.get(errorList)).add(e.getLocalizedMessage() + " (" + filename + ")");
			throw e;
		}
	}

	private static void addBinderFileAttachments(Long binderId, Long topBinderId,
			Map binderIdMap, Document entityDoc, String tempDir, Map reportMap) {

		String hrefPath = getHrefPath(binderId, topBinderId, binderIdMap);
		String xPath = "//attribute[@type='attachFiles']//file";
		List attachFiles = entityDoc.selectNodes(xPath);

		for (Element fileEle : (List<Element>) attachFiles) {
			boolean handled = false;

			String href = tempDir + File.separator + hrefPath
					+ fileEle.attributeValue("href", "");
			
			String filename = fileEle.getText();
			
			int numVersions = Integer.valueOf(fileEle
					.attributeValue("numVersions", "0"));
			
			String versionsDir = null;

			if (numVersions > 1) {
				versionsDir = tempDir + File.separator + hrefPath
						+ fileEle.attributeValue("href", "") + ".versions";
			}

			// see if there's a matching attachment of type 'file'
			String fileXPath = "//attribute[@type='file']//value";
			List fileList = entityDoc.selectNodes(fileXPath);

			for (Element fileListEle : (List<Element>) fileList) {
				if (fileListEle.getText().equals(filename)) {
					String name = fileListEle.getParent()
							.attributeValue("name", "");
					addBinderFileVersions(binderId, name, filename, href,
							numVersions, versionsDir, reportMap);
					handled = true;
					break;
				}
			}

			// if not yet found, see if there's a matching attachment of type
			// 'graphic'

			if (!handled) {
				String graphicXPath = "//attribute[@type='graphic']//value";
				List graphicList = entityDoc.selectNodes(graphicXPath);

				for (Element graphicListEle : (List<Element>) graphicList) {
					if (graphicListEle.getText().equals(filename)) {
						String name = graphicListEle.getParent()
								.attributeValue("name", "");
						addBinderFileVersions(binderId, name, filename,
								href, numVersions, versionsDir, reportMap);
						handled = true;
						break;
					}
				}
			}

			// if not yet found, see if there's a match in the rest of the
			// attachments

			if (!handled) {
				String attachsXPath = "//attribute[@type='attachFiles']//file";
				List attachsList = entityDoc.selectNodes(attachsXPath);

				for (Element attachsListEle : (List<Element>) attachsList) {
					if (attachsListEle.getText().equals(filename)) {
						String name = attachsListEle.getParent()
								.attributeValue("name", "")
								+ "1";
						addBinderFileVersions(binderId, name, filename,
								href, numVersions, versionsDir, reportMap);
						handled = true;
						break;
					}
				}
			}
		}
	}

	private static void addBinderFileVersions(Long binderId,
			String fileDataItemName, String filename, String href,
			int numVersions, String versionsDir, Map reportMap) {

		String fileExt = EntityIndexUtils.getFileExtension(filename);
		InputStream iStream = null;

		if (numVersions > 1) {
			for (int i = 1; i < numVersions; i++) {
				try {
					iStream = new FileInputStream(new File(versionsDir
							+ File.separator + i + "." + fileExt));
					if(logger.isDebugEnabled())
						logger.debug("Adding file " + filename + " to binder " + binderId);
					binderModule.modifyBinder(binderId,
							fileDataItemName, filename, iStream);
					iStream.close();
					Integer count = (Integer)reportMap.get(files);
					reportMap.put(files, ++count);
				} catch (Exception e) {
					logger.error(e);
					Integer c = (Integer)reportMap.get(errors);
					reportMap.put(errors, ++c);
					((List)reportMap.get(errorList)).add(e.getLocalizedMessage());
					return;
				}
			}
		}

		try {
			iStream = new FileInputStream(new File(href));
			if(logger.isDebugEnabled())
				logger.debug("Adding file " + filename + " to binder " + binderId);
			binderModule.modifyBinder(binderId, fileDataItemName,
					filename, iStream);
			iStream.close();
			Integer count = (Integer)reportMap.get(files);
			reportMap.put(files, ++count);
		} catch (Exception e) {
			logger.error(e);
			Integer c = (Integer)reportMap.get(errors);
			reportMap.put(errors, ++c);
			((List)reportMap.get(errorList)).add(e.getLocalizedMessage());
			return;
		}
	}

	private static String getHrefPath(Long binderId, Long topBinderId, Map binderIdMap) {
		return getHrefPath(binderId, null, topBinderId, binderIdMap, null);
	}
	private static String getHrefPath(Long binderId, Long entryId, Long topBinderId, Map binderIdMap, Map entryIdMap) {
		Map reverseBinderIdMap = new HashMap();
		for (Object id : binderIdMap.keySet()) reverseBinderIdMap.put(binderIdMap.get(id), id);
		Binder topBinder = loadBinder(topBinderId);
		Binder binder = loadBinder(binderId);
		String pathName = "";
		while (binder != null) {
			if (reverseBinderIdMap.containsKey(binder.getId())) {
				Long originalBinderId = (Long)reverseBinderIdMap.get(binder.getId());
				if (EntityType.folder.equals(binder.getEntityType())) {
					pathName = binderPrefix + "f" + nft.format(originalBinderId) + "/" + pathName;
				} else {
					pathName = binderPrefix + "w" + nft.format(originalBinderId) + "/" + pathName;
				}
			}
			binder = binder.getParentBinder();
			if (binder == null || binder.equals(topBinder)) break;
		}
		if (entryId != null) {
			Map reverseEntryIdMap = new HashMap();
			for (Object id : entryIdMap.keySet()) reverseEntryIdMap.put(entryIdMap.get(id), id);
			
			Long originalEntryId = (Long)reverseEntryIdMap.get(entryId);

			FolderEntry entry = folderModule.getEntry(binderId, entryId);
			String entryPath = nft.format(originalEntryId);
			if (entry != null && !entry.isTop()) {
				FolderEntry parentEntry = entry;
				while (!parentEntry.isTop()) {
					parentEntry = parentEntry.getParentEntry();
					Long originalParentEntryId = (Long)reverseEntryIdMap.get(parentEntry.getId());
					if (originalParentEntryId != null) 
						entryPath = nft.format(originalParentEntryId) + "_" + entryPath;
				}
			}
			if (originalEntryId != null) {
				pathName = pathName + "e" + entryPath + "/";
			}
		}
		return pathName;
	}
	private static void setSignature(Map options, Document entityDoc, Map<String, Principal> nameCache) {
		User user = RequestContextHolder.getRequestContext().getUser();
		String zoneUUID = entityDoc.getRootElement().attributeValue("zoneUUID", "");
		Element signature = (Element) entityDoc.selectSingleNode("//signature");
		if (signature != null) {
			Element creation = (Element)signature.selectSingleNode("./creation");
			if (creation != null) {
				String sDate = creation.attributeValue("date", "");
				Element value = (Element) creation.selectSingleNode("./principal");
				if (!sDate.equals("") && value != null) {
					String name = value.attributeValue("name", "");
					String emailAdr = value.attributeValue("emailAddress", "");
					List names = new ArrayList();
					names.add(emailAdr);
					Principal p = matchPrincipal(name, emailAdr, zoneUUID, nameCache);
			    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
			    	Calendar date = Calendar.getInstance();
			    	try {
						if (p != null && p instanceof UserPrincipal) {
							date.setTime(sdf.parse(sDate));
							//Set the creator of this entity to the original author and
							// make the current user be the modifier of the entity
							options.put(ObjectKeys.INPUT_OPTION_CREATION_NAME, p.getName());
							options.put(ObjectKeys.INPUT_OPTION_CREATION_DATE, date);
					    	Calendar now = Calendar.getInstance();
					    	now.setTime(new Date());
							options.put(ObjectKeys.INPUT_OPTION_MODIFICATION_NAME, user.getName());
							options.put(ObjectKeys.INPUT_OPTION_MODIFICATION_DATE, now);
						}
					} catch(java.text.ParseException e) {}
				}
			}
		}
	}
	
	private static Document buildNameCache(Document doc, Map nameCache) {
		String zoneUUID = doc.getRootElement().attributeValue("zoneUUID", "");
		List<Element> principalElements = doc.selectNodes("//principal");
		for (Element pEle : principalElements) {
			String name = pEle.attributeValue("name", "");
			String emailAdr = pEle.attributeValue("emailAddress", "");
			Principal p = matchPrincipal(name, emailAdr, zoneUUID, nameCache);
			fixUpPrincipal(pEle, p);
		}
		return doc;
	}
	
	private static void fixUpPrincipal(Element pEle, Principal p) {
		if (p == null) return;
		if (!p.getName().equals(pEle.getText())) pEle.setText(p.getName());
		
		Element parent = pEle.getParent();
		if (parent.getName().equals("attribute")) {
			List<Element> values = parent.selectNodes("./value");
			for (Element value : values) {
				if (pEle.getText().equals(value.getText())) {
					value.setText(p.getName());
					break;
				}
			}
		}
	}
	
	private static Principal matchPrincipal(String name, String emailAdr, String zoneUUID, 
			Map<String, Principal> nameCache) {
		if (nameCache != null && nameCache.containsKey(name)) return nameCache.get(name);
		Principal result = null;
		if (Validator.isNull(zoneUUID) || getZoneInfo().getId().equals(zoneUUID)) {
			//Importing into the same zone, just use this name as is
			List names = new ArrayList();
			names.add(name);
			List<Principal> principals = ResolveIds.getPrincipalsByName(names, false);
			if (!principals.isEmpty()) {
				result = principals.get(0);
			}
		} else {
			if (!Validator.isNull(emailAdr)) {
				List names = new ArrayList();
				names.add(emailAdr);
				List<Principal> principals = ResolveIds.findPrincipalByEmailAdr(names, false);
				if (!principals.isEmpty()) result = principals.get(0);
				for (Principal p : principals) {
					if (p.getName().equals(name)) {
						result = p;
						break;
					}
				}
			} else {
				//There is no email address, try looking for this name directly
				List names = new ArrayList();
				names.add(name);
				List<Principal> principals = ResolveIds.getPrincipalsByName(names, false);
				if (!principals.isEmpty()) {
					result = principals.get(0);
				}
			}
		}
		//Cache the result for faster lookup later
		if (result != null && nameCache != null) nameCache.put(name, result);
		return result;
	}
	private static File getTemporaryDirectory() {
		return new File(TempFileUtil.getTempFileDir("import"), UUID
				.randomUUID().toString());
	}

	private static String deploy(ZipInputStream zipIn, Map reportMap) throws IOException {
		File tempDir = getTemporaryDirectory();

		FileUtil.deltree(tempDir);

		tempDir.mkdirs();
		java.util.zip.ZipEntry entry = null;

		try {
			// load all the files
			while ((entry = zipIn.getNextEntry()) != null) {
				// extract file to proper temporary directory
				File inflated;
				String name = entry.getName();
				try {
					inflated = new File(tempDir, name);
					if (entry.isDirectory()) {
						inflated.mkdirs();
						zipIn.closeEntry();
						continue;
					} else {
						inflated.getParentFile().mkdirs();
						FileOutputStream entryOut = new FileOutputStream(inflated);
						FileCopyUtils.copy(new ZipEntryStream(zipIn), entryOut);
						entryOut.close();
					}
					zipIn.closeEntry();
				} catch(Exception e) {
					Integer c = (Integer)reportMap.get(errors);
					reportMap.put(errors, ++c);
					((List)reportMap.get(errorList)).add(e.getMessage());

					logger.error(e);
				}
			}
		} catch(Exception e) {
			Integer c = (Integer)reportMap.get(errors);
			reportMap.put(errors, ++c);
			((List)reportMap.get(errorList)).add(e.getMessage());
			logger.error(e);
		} finally {
			zipIn.close();
		}

		return tempDir.getAbsolutePath();
	}

	private static void addTeamMembers(Long binderId, Document entityDoc, Map<String, Principal> nameCache) {
		String zoneUUID = entityDoc.getRootElement().attributeValue("zoneUUID", "");
		String xPath = "//team";
		Element team = (Element) entityDoc.selectSingleNode(xPath);

		boolean teamInherited = Boolean.parseBoolean(team
				.attributeValue("inherited", "false"));

		Binder binder = loadBinder(binderId);

		if (teamInherited) {
			binderModule.setTeamMembershipInherited(binderId, true);
		} else {
			xPath = "//team//principal";
			List principals = entityDoc.selectNodes(xPath);
			Set<Long> ids = new HashSet();

			for (Element member : (List<Element>) principals) {
				String name = member.attributeValue("name", "");
				String emailAdr = member.attributeValue("emailAddress", "");
				Principal p = matchPrincipal(name, emailAdr, zoneUUID, nameCache);
				if (p != null) ids.add(p.getId());
			}

			binderModule.setTeamMembers(binderId, ids);
		}
	}

	public static Element addPrincipalToDocument(Branch doc, Principal entry) {
		Element entryElem = doc.addElement("principal");

		// Handle structured fields of the entry known at compile time.
		entryElem.addAttribute("id", entry.getId().toString());
		entryElem.addAttribute("name", entry.getName());
		entryElem.addAttribute("title", Utils.getUserTitle(entry));
		entryElem.addAttribute("emailAddress", entry.getEmailAddress());
		entryElem.setText(entry.getName());

		return entryElem;
	}

	private static void importWorkflows(final Document entityDoc, final DefinableEntity entity, 
			final Map<String, String> definitionIdMap, final Map reportMap, 
			final Map<String, Principal> nameCache, final Binder topBinder, boolean doIndex) {
		final User user = RequestContextHolder.getRequestContext().getUser();
		final String zoneUUID = entityDoc.getRootElement().attributeValue("zoneUUID", "");
		
		if (entity instanceof FolderEntry) {
			boolean needsToBeIndexed = transactionTemplate.execute(new TransactionCallback<Boolean>() {
				@Override
				public Boolean doInTransaction(TransactionStatus status) {
					boolean needsToBeIndexed = false;
					
					// end all workflows that started upon entry creation (none should have been started)
					Set defaultWorkflows = ((FolderEntry) entity).getWorkflowStates();
					Iterator iter = defaultWorkflows.iterator();

					while (iter.hasNext()) {
						((FolderEntry) entity).removeWorkflowState((WorkflowState) iter
								.next());
						needsToBeIndexed = true;
					}

					// add workflow responses
					List<Element> repsonses = entityDoc.selectNodes("//workflows//response");

					for (Element response : repsonses) {
						String defId = response.attributeValue("definitionId", "");
						String defName = response.attributeValue("definitionName", "");
						Definition def = getTargetDefinition(defId, defName, definitionIdMap, reportMap, topBinder);
						String question = response.attributeValue("responseName", "");
						String responseValue = response.attributeValue("responseValue", "");
						String responseDate = response.attributeValue("responseDate", "");

						Element responderEle = (Element)response.selectSingleNode("./principal");
						String responderName = responderEle.attributeValue("name", "");
						String responderEmailAdr = responderEle.attributeValue("emailAddress", "");
						Principal responder = matchPrincipal(responderName, responderEmailAdr, zoneUUID, nameCache);
						if (responder == null) responder = user;

				    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
				    	Date date = new Date();
				    	try {
							date.setTime(sdf.parse(responseDate).getTime());
				    	} catch(Exception e) {}

						try {
							WorkflowResponse wr = new WorkflowResponse();
							wr.setResponderId(responder.getId());
							wr.setResponseDate(date);
							wr.setDefinitionId(def.getId());
							wr.setName(question);
							wr.setResponse(responseValue);
							wr.setOwner(entity);
							coreDao.save(wr);
							((WorkflowSupport)entity).addWorkflowResponse(wr);

							//processor.processChangeLog(entity, ChangeLog.ADDWORKFLOWRESPONSE);
						
						} catch(Exception e) {
							logger.error(e);
						}
					}
					
					return needsToBeIndexed;
				}
			});

			// start up the imported workflows
			List<Element> workflows = entityDoc.selectNodes("//workflows//process");

			for (Element process : workflows) {
				String defId = process.attributeValue("definitionId", "");
				String defName = process.attributeValue("definitionName", "");
				String state = process.attributeValue("state", "");
				Definition def = getTargetDefinition(defId, defName, definitionIdMap, reportMap, topBinder);

				EntityIdentifier entityIdentifier = new EntityIdentifier(entity
						.getId(), EntityIdentifier.EntityType.folderEntry);

				Map options = new HashMap();
				options.put(ObjectKeys.INPUT_OPTION_FORCE_WORKFLOW_STATE, state);
				
				//Workflow State needs a modification timestamp
				//SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		    	Calendar current = Calendar.getInstance();
		    	current.setTime(new Date());
				
				options.put(ObjectKeys.INPUT_OPTION_MODIFICATION_NAME, user.getName());
				options.put(ObjectKeys.INPUT_OPTION_MODIFICATION_DATE, current);				
				
				try {
					workflowModule.addEntryWorkflow((FolderEntry) entity,
						entityIdentifier, def, options);
					
				} catch(Exception e) {
					logger.error(e);
				}
				needsToBeIndexed = true;

			}
			if (needsToBeIndexed && doIndex) {
				//Re-index this entry
				folderModule.indexEntry((FolderEntry)entity, false);
			}
		} else if (entity instanceof Binder) {

			List<String> newDefinitionList = new ArrayList<String>();

			// current binder definitions

			List<Definition> defs = ((Binder) entity).getDefinitions();

			for (int i = 0; i < defs.size(); i++) {
				newDefinitionList.add(defs.get(i).getId());
			}

			// allowed workflows

			String xPath = "//workflows//allowed//process";
			List<Element> workflows = entityDoc.selectNodes(xPath);

			for (Element process : workflows) {
				String defId = process.attributeValue("definitionId", "");
				String defName = process.attributeValue("definitionName", "");
				if (!defId.equals("")) {
					Definition def = getTargetDefinition(defId, defName, definitionIdMap, reportMap, topBinder);
					if (def != null) {
						if(!newDefinitionList.contains(def.getId()))
							newDefinitionList.add(def.getId());
					}
				}
			}

			// associated workflows

			xPath = "//workflows//associated//process";
			workflows = entityDoc.selectNodes(xPath);

			Map<String, String> workflowAssociations = new HashMap<String, String>();

			for (Element process : workflows) {
				String entryDefId = process.attributeValue("entryDefinitionId", "");
				String entryDefName = process.attributeValue("entryDefinitionName", "");
				String workflowDefId = process.attributeValue("workflowDefinitionId", "");
				String workflowDefName = process.attributeValue("workflowDefinitionName", "");
				if (!entryDefId.equals("")) {
					Definition entryDef = getTargetDefinition(entryDefId, entryDefName, definitionIdMap, reportMap, topBinder);
					Definition workflowDef = getTargetDefinition(workflowDefId, workflowDefName, definitionIdMap, reportMap, topBinder);
					if (entryDef != null && workflowDef != null) 
						workflowAssociations.put(entryDef.getId(), workflowDef.getId());
				}
			}
			
			try {
				binderModule.setDefinitions(entity.getId(), newDefinitionList,
					workflowAssociations);
			} catch(Exception e) {
				String[] args = new String[] {entity.getTitle(), e.getMessage()};
				logger.error("importWorkflows():  Exception from BinderModule.setDefinitions():  ", e);
				throw new ExportException(
						new Exception(NLT.get("export.error.settingWorkflows", args)));
			}
		}
	}

	private static void importSettingsList(Document entityDoc, final Binder binder, 
			Map<String, String>definitionIdMap, Map reportMap, Binder topBinder, 
			Map<String, Principal> nameCache, Map<Long, Map<String, Object>> binderPropertyMap) {
		String zoneUUID = entityDoc.getRootElement().attributeValue("zoneUUID", "");
		FilesErrors filesErrors = new FilesErrors();

		// current binder definitions

		final List<Definition> newDefinitionList = binder.getDefinitions();
		
		//Branding
		Element brandingEle = (Element)entityDoc.selectSingleNode("//settings//branding");
		if (brandingEle != null) {
			binderModule.setBinderBranding(binder.getId(), brandingEle.getText());
		}
		Element brandingExtEle = (Element)entityDoc.selectSingleNode("//settings//brandingExt");
		if (brandingExtEle != null) {
			binderModule.setBinderBrandingExt(binder.getId(), brandingExtEle.getText());
		}

		// views
		String xPath = "//settings//views//view";
		List<Element> views = entityDoc.selectNodes(xPath);
		for (Element view : views) {
			String defId = view.attributeValue("definitionId", "");
			String defName = view.attributeValue("definitionName", "");
			Definition def = getTargetDefinition(defId, defName, definitionIdMap, reportMap, topBinder);
			if (def != null) {
				// don't want to include mirrored file folder as an imported view setting
				if (!def.getId().equals(ObjectKeys.DEFAULT_MIRRORED_FILE_FOLDER_DEF)) {
					if(!newDefinitionList.contains(def))
						newDefinitionList.add(def);
				}
			}
		}

		//Access control settings
		WorkArea workArea = (WorkArea)binder;
		addAccessControls(workArea, entityDoc, reportMap, nameCache);

		// entries
		xPath = "//settings//entries//entry";
		List<Element> entries = entityDoc.selectNodes(xPath);
		for (Element entry : entries) {
			String defId = entry.attributeValue("definitionId", "");
			String defName = entry.attributeValue("definitionName", "");
			Definition def = getTargetDefinition(defId, defName, definitionIdMap, reportMap, topBinder);
			if (def != null) {
				// don't want to include mirrored file entry as an imported allowed entry setting
				if (!def.getId().equals(ObjectKeys.DEFAULT_MIRRORED_FILE_ENTRY_DEF)) {
					if(!newDefinitionList.contains(def))
						newDefinitionList.add(def);
				}
			}
		}
		
		transactionTemplate.execute(new TransactionCallback() {
			@Override
			public Object doInTransaction(TransactionStatus status) {
				binder.setDefinitions(newDefinitionList);
				return null;
			}
		});
		
		// version controls
		xPath = "//settings//versionControls";
		Element versionControls = (Element)entityDoc.selectSingleNode(xPath);
		if (versionControls != null) {
			String versionsEnabled = versionControls.attributeValue("versionsEnabled", null);
			if (versionsEnabled != null) {
				binderModule.setBinderVersionsEnabled(binder.getId(), Boolean.valueOf(versionsEnabled));
			}
			String versionsToKeep = versionControls.attributeValue("versionsToKeep", null);
			if (versionsToKeep != null) {
				binderModule.setBinderVersionsToKeep(binder.getId(), Long.valueOf(versionsToKeep));
			}
			String versionAgingEnabled = versionControls.attributeValue("versionAgingEnabled", null);
			if (versionAgingEnabled != null) {
				binderModule.setBinderVersionAgingEnabled(binder.getId(), Boolean.valueOf(versionAgingEnabled));
			}
			String versionAgingDays = versionControls.attributeValue("versionAgingDays", null);
			if (versionAgingDays != null) {
				binderModule.setBinderVersionAgingDays(binder.getId(), Long.valueOf(versionAgingDays));
			}
			String versionsMaxFileSize = versionControls.attributeValue("versionsMaxFileSize", null);
			if (versionsMaxFileSize != null) {
				binderModule.setBinderMaxFileSize(binder.getId(), Long.valueOf(versionsMaxFileSize));
			}
			String fileEncryptionEnabled = versionControls.attributeValue("fileEncryptionEnabled", null);
			if (fileEncryptionEnabled != null && Boolean.valueOf(fileEncryptionEnabled)) {
				binderModule.setBinderFileEncryptionEnabled(binder.getId(), Boolean.valueOf(fileEncryptionEnabled), filesErrors);
			}
		}

		// binder quota
		xPath = "//settings//binderQuota";
		Element binderQuotaEle = (Element)entityDoc.selectSingleNode(xPath);
		if (binderQuotaEle != null) {
			String binderQuota = binderQuotaEle.attributeValue("binderQuota", null);
			if (binderQuota != null) {
				BinderQuota bq = adminModule.getBinderQuota(binder);
				bq.setDiskQuota(Long.valueOf(binderQuota));
				adminModule.setBinderQuota(binder, bq);
			}
		}

		xPath = "//settings//properties";
		Element propEle = (Element)entityDoc.selectSingleNode(xPath);
		if (propEle != null) {
			String serializedProps = propEle.getText();
			if (serializedProps != null) {
				try {
					binder.setSerializedProperties(serializedProps);
				} catch (Exception e) {
					logger.warn("Failed to import properties for binder: " + binder.getPathName() + ".  Some settings such as column layouts will not be included.", e);
				}
			}
		}
		
		// filters
		xPath = "//settings//filters/filter";
		List<Element> filters = entityDoc.selectNodes(xPath);
		if (!filters.isEmpty()) {
			Map searchFiltersG = new HashMap();
			for (Element filter : filters) {
				String name = filter.attributeValue("name");
				String value = filter.attributeValue("value");
				if (name != null && value != null) {
					searchFiltersG.put(name, value);
				}
			}
			binderModule.setProperty(binder.getId(), ObjectKeys.BINDER_PROPERTY_FILTERS, searchFiltersG);
		}
		
		// binder properties
		xPath = "//settings//wikiHomePageId";
		Element wikiHomePageEle = (Element)entityDoc.selectSingleNode(xPath);
		if (wikiHomePageEle != null) {
			Long zoneId = RequestContextHolder.getRequestContext().getZoneId();
			String wikiHomePageId = wikiHomePageEle.getText();
			if (wikiHomePageId != null && !wikiHomePageId.equals("")) {
				if (!binderPropertyMap.containsKey(binder.getId())) {
					binderPropertyMap.put(binder.getId(), new HashMap<String, Object>());
				}
				Map<String, Object> binderProperties = binderPropertyMap.get(binder.getId());
				binderProperties.put(ObjectKeys.BINDER_PROPERTY_WIKI_HOMEPAGE, wikiHomePageId);
			}
		}

	}

	//Keep this in sync with the same routine in BinderModuleImpl
	public static String filename8BitSingleByteOnly(FileAttachment attachment,
			boolean _8BitSingleByteOnly) {
		String fileName = attachment.getFileItem().getName();

		String fileExt = EntityIndexUtils.getFileExtension(attachment
				.getFileItem().getName());

		if (!_8BitSingleByteOnly) {
			return fileName;
		} else {
			for (int i = 0; i < fileName.length(); i++) {
				int c = (int) fileName.charAt(i);

				//Allow only Basic Latin characters
				if (c >= 0x20 && c < 0x7F) {
					// it's ok
				} else
					return attachment.getId() + "." + fileExt;
			}

			return fileName;
		}
	}

	private static Binder loadBinder(Long binderId) {
		return coreDao.loadBinder(binderId, RequestContextHolder.getRequestContext()
				.getZoneId());
	}

	public static ZoneInfo getZoneInfo() {
		return zoneModule.getZoneInfo(RequestContextHolder.getRequestContext().getZoneId());
	}

	/*
	 * Exports any task linkage attached to the folder.
	 */
	private static void addTaskLinkage(Element element, DefinableEntity entity, List<Long> entryIds) {
		// If the folder doesn't contain any entries...
		if ((null == entryIds) || entryIds.isEmpty()) {
			// ...we don't care about the linkage.
			return;
		}
		
		// If the folder doesn't have any task linkage...
		Folder folder = ((Folder) entity);
		Map serializationMap = ((Map) folder.getProperty(ObjectKeys.BINDER_PROPERTY_TASK_LINKAGE));
		if ((null == serializationMap) || serializationMap.isEmpty()) {
			// ...there's nothing to export.
			return;
		}

		// Parse the serialized task linkage...
		ServerTaskLinkage tl = ServerTaskLinkage.loadSerializationMap(serializationMap);
		
		// ...strip out any entry IDs that we don't have entries for...
		tl.validateTaskLinkage(entryIds);

		// ...and export the linkage.
		Element tlElement = element.addElement("task-linkage");
		addTaskLinkList(tlElement, tl.getTaskOrder());
	}

	/*
	 * Recursively exports the ServerTaskLink's in a
	 * List<ServerTaskLink> to an Element in the export file.
	 */
	private static void addTaskLinkList(Element e, List<ServerTaskLink> tlList) {
		for (ServerTaskLink link:  tlList) {
			Element linkElement = e.addElement("task-link");
			linkElement.addAttribute("entryId", String.valueOf(link.getEntryId()));
			addTaskLinkList(linkElement, link.getSubtasks());
		}
	}

	/*
	 * Returns a List<Long> of the IDs of the entries contained in a
	 * binder.
	 */
	private static List<Long> getFolderEntryIds(Long binderId, Map options) {
		Map folderEntries = folderModule.getEntries(binderId, options);
		List searchEntries = ((List) folderEntries.get("search_entries"));
		List<Long> entryIds = new ArrayList<Long>();
		int c = searchEntries.size();
		for (int i = 0; i < c; i += 1) {
			Map searchEntry = ((Map) searchEntries.get(i));
			Long entryId = Long.valueOf(searchEntry.get(Constants.DOCID_FIELD).toString());
			entryIds.add(entryId);
		}
		return entryIds;
	}
	
	/*
	 * Extracts the <task-linkage> information from a <folder>.  If
	 * found, creates a ServerTaskLinkage for it and adds it to the map
	 * tracking ServerTaskLinkage's for imported folders.
	 */
	private static void importTaskLinkage(Document doc, Long newBinderId, Map<Long, ServerTaskLinkage> taskLinkageMap) {
		// If the Document doesn't contain a <task-linkage>...
		Element tlElement = ((Element) doc.selectSingleNode("//task-linkage"));
		if (null == tlElement) {
			// ...there's nothing to import.
			return;
		}

		// Otherwise, create a ServerTaskLinkage...
		ServerTaskLinkage tl = new ServerTaskLinkage();
		
		// ...populate it with the data from the <task-linkage>...
		tl.setTaskOrder(importTaskLinkList(tlElement));
		
		// ...and map the new binder ID to that ServerTaskLinkage.
		taskLinkageMap.put(newBinderId, tl);
	}

	/*
	 * Extracts the <task-link> information from a <task-linkage> or
	 * other <task-link>.  Returns a List<ServerTaskLink> for
	 * <task-link>'s processed. 
	 */
	private static List<ServerTaskLink> importTaskLinkList(Element e) {
		// Allocate a List<ServerTaskLink> to return.
		List<ServerTaskLink> reply = new ArrayList<ServerTaskLink>();

		// Scan the <task-link>'s in the Element we were given...
		List<Element> linkElements = e.selectNodes("./task-link");
		for (Element linkElement: linkElements) {
			// ...adding a ServerTaskLink for each to the
			// ...List<ServerTaskLink> we're going to return...
			ServerTaskLink link = new ServerTaskLink();
			String entryId = linkElement.attributeValue("entryId");
			link.setEntryId(Long.parseLong(entryId));
			reply.add(link);

			// ...and process and nested <task-link>'s it contains.
			link.setSubtasks(importTaskLinkList(linkElement));
		}

		// If we get here, reply refers to the List<ServerTaskLink> of
		// the <task-link>'s we processed.  Return it.
		return reply;
	}
}
