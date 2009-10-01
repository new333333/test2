/**
 * Copyright (c) 1998-2009 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2009 Novell, Inc. All Rights Reserved.
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipInputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.SortField;
import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipOutputStream;
import org.dom4j.Attribute;
import org.dom4j.Branch;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.hibernate.NonUniqueObjectException;
import org.kablink.teaming.ConfigurationException;
import org.kablink.teaming.InternalException;
import org.kablink.teaming.NoObjectByTheIdException;
import org.kablink.teaming.NotSupportedException;
import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.comparator.BinderComparator;
import org.kablink.teaming.comparator.PrincipalComparator;
import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.dao.CoreDao;
import org.kablink.teaming.dao.util.FilterControls;
import org.kablink.teaming.dao.util.ObjectControls;
import org.kablink.teaming.dao.util.SFQuery;
import org.kablink.teaming.domain.Attachment;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.ChangeLog;
import org.kablink.teaming.domain.CustomAttribute;
import org.kablink.teaming.domain.DefinableEntity;
import org.kablink.teaming.domain.Definition;
import org.kablink.teaming.domain.EntityIdentifier;
import org.kablink.teaming.domain.FileAttachment;
import org.kablink.teaming.domain.Folder;
import org.kablink.teaming.domain.FolderEntry;
import org.kablink.teaming.domain.HistoryStamp;
import org.kablink.teaming.domain.LibraryEntry;
import org.kablink.teaming.domain.NoBinderByTheIdException;
import org.kablink.teaming.domain.NoDefinitionByTheIdException;
import org.kablink.teaming.domain.NotificationDef;
import org.kablink.teaming.domain.PostingDef;
import org.kablink.teaming.domain.Principal;
import org.kablink.teaming.domain.SimpleName;
import org.kablink.teaming.domain.Subscription;
import org.kablink.teaming.domain.Tag;
import org.kablink.teaming.domain.TemplateBinder;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.domain.UserPrincipal;
import org.kablink.teaming.domain.VersionAttachment;
import org.kablink.teaming.domain.WorkflowState;
import org.kablink.teaming.domain.Workspace;
import org.kablink.teaming.domain.ZoneInfo;
import org.kablink.teaming.domain.EntityIdentifier.EntityType;
import org.kablink.teaming.lucene.Hits;
import org.kablink.teaming.lucene.TagObject;
import org.kablink.teaming.module.binder.BinderModule;
import org.kablink.teaming.module.binder.BinderModule.BinderOperation;
import org.kablink.teaming.module.binder.impl.WriteEntryDataException;
import org.kablink.teaming.module.binder.processor.BinderProcessor;
import org.kablink.teaming.module.definition.DefinitionModule;
import org.kablink.teaming.module.definition.DefinitionUtils;
import org.kablink.teaming.module.definition.export.ElementBuilder;
import org.kablink.teaming.module.definition.export.ElementBuilderUtil;
import org.kablink.teaming.module.file.FileModule;
import org.kablink.teaming.module.file.WriteFilesException;
import org.kablink.teaming.module.folder.FolderModule;
import org.kablink.teaming.module.ical.IcalModule;
import org.kablink.teaming.module.impl.CommonDependencyInjection;
import org.kablink.teaming.module.profile.ProfileModule;
import org.kablink.teaming.module.shared.EntityIndexUtils;
import org.kablink.teaming.module.shared.InputDataAccessor;
import org.kablink.teaming.module.shared.ObjectBuilder;
import org.kablink.teaming.module.shared.SearchUtils;
import org.kablink.teaming.module.workflow.WorkflowModule;
import org.kablink.teaming.module.workspace.WorkspaceModule;
import org.kablink.teaming.module.zone.ZoneModule;
import org.kablink.teaming.remoting.RemotingException;
import org.kablink.teaming.remoting.ws.util.DomInputData;
import org.kablink.teaming.search.IndexErrors;
import org.kablink.teaming.search.IndexSynchronizationManager;
import org.kablink.teaming.search.LuceneReadSession;
import org.kablink.teaming.search.LuceneWriteSession;
import org.kablink.teaming.search.QueryBuilder;
import org.kablink.teaming.search.SearchFieldResult;
import org.kablink.teaming.search.SearchObject;
import org.kablink.teaming.security.AccessControlException;
import org.kablink.teaming.security.function.WorkAreaOperation;
import org.kablink.teaming.util.LongIdUtil;
import org.kablink.teaming.util.NLT;
import org.kablink.teaming.util.ResolveIds;
import org.kablink.teaming.util.SPropsUtil;
import org.kablink.teaming.util.SpringContextUtil;
import org.kablink.teaming.util.StatusTicket;
import org.kablink.teaming.util.TagUtil;
import org.kablink.teaming.util.TempFileUtil;
import org.kablink.teaming.util.XmlFileUtil;
import org.kablink.teaming.util.ZipEntryStream;
import org.kablink.teaming.web.WebKeys;
import org.kablink.teaming.web.tree.DomTreeBuilder;
import org.kablink.teaming.web.util.DefinitionHelper;
import org.kablink.teaming.web.util.WebUrlUtil;
import org.kablink.util.FileUtil;
import org.kablink.util.Validator;
import org.kablink.util.search.Constants;
import org.kablink.util.search.Criteria;
import org.kablink.util.search.Order;
import org.springframework.orm.hibernate3.HibernateSystemException;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.FileCopyUtils;

import static org.kablink.util.search.Restrictions.between;
import static org.kablink.util.search.Restrictions.eq;
import static org.kablink.util.search.Restrictions.in;

/**
 * @author Janet McCann
 * 
 */

public class ExportHelper {
	protected static final Log logger = LogFactory.getLog(Binder.class);
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
			Map options, Collection<Long> binderIds, Boolean noSubBinders, StatusTicket statusTicket) throws Exception {

		getNumberFormat();

		ZipOutputStream zipOut = new ZipOutputStream(out);

		// Standard zip encoding is cp437. (needed when chars are outside the
		// ASCII range)
		zipOut.setEncoding("cp437");

		// Binder and entry definitions that we will later export
		Set defList = new HashSet();

		if (entityId != null) {
			processEntry(zipOut, folderModule.getEntry(binderId, entityId), "",
					defList);
		} else {

			EntityType entType = binderModule.getBinder(binderId)
					.getEntityType();

			// see if folder
			if (entType == EntityType.folder) {
				process(zipOut, folderModule.getFolder(binderId), false,
						options, binderIds, noSubBinders, defList, statusTicket);
				// see if ws or profiles ws
			} else if ((entType == EntityType.workspace)
					|| (entType == EntityType.profiles)) {
				process(zipOut, workspaceModule.getWorkspace(binderId), true,
						options, binderIds, noSubBinders, defList, statusTicket);
			} else {
				// something's wrong
			}
		}

		// Export the binder and entry definitions
		exportDefinitionList(zipOut, defList);

		zipOut.finish();
	}

	private static void process(ZipOutputStream zipOut, Binder start,
			boolean isWorkspace, Map options, Collection<Long> binderIds, 
			Boolean noSubBinders, Set defList, StatusTicket statusTicket) throws Exception {
		Map<Long,Boolean> binderIdsToExport = new HashMap<Long,Boolean>();
		binderIdsToExport.put(start.getId(), false);
		SortedSet<Binder> binders = binderModule.getBinders(binderIds);
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
		
		addDefinitions(defList, start.getDefinitions());
		
		//Get a complete list of all sub-binders
		List folderIds = new ArrayList();
		folderIds.add(start.getId().toString());
		Criteria crit = new Criteria();
		crit.add(in(Constants.DOC_TYPE_FIELD, new String[] {Constants.DOC_TYPE_BINDER}))
			.add(in(Constants.ENTRY_ANCESTRY, folderIds));
		crit.addOrder(Order.asc(Constants.BINDER_ID_FIELD));
		Map binderMap = binderModule.executeSearchQuery(crit, 0, ObjectKeys.SEARCH_MAX_HITS_SUB_BINDERS);

		List binderMapList = (List)binderMap.get(ObjectKeys.SEARCH_ENTRIES); 
		List binderIdList = new ArrayList();

      	for (Iterator iter=binderMapList.iterator(); iter.hasNext();) {
      		Map entryMap = (Map) iter.next();
      		Long docId = new Long((String)entryMap.get("_docId"));
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
				continue;
			}
			statusTicket.setStatus(NLT.get("administration.export_import.exporting", new String[] {binder.getPathName()}));
			if (EntityType.workspace.equals(binder.getEntityType())) {
				zipOut.putNextEntry(new ZipEntry(binderPrefix + "w"
						+ nft.format(binderId) + File.separator + "."
						+ binderPrefix + "w" + nft.format(binderId) + ".xml"));
				XmlFileUtil.writeFile(getWorkspaceAsDoc(null, binderId, false,
						binderPrefix + "w" + nft.format(binderId), defList),
						zipOut);
				zipOut.closeEntry();

			} else {
				zipOut.putNextEntry(new ZipEntry(binderPrefix + "f"
						+ nft.format(binderId) + File.separator + "."
						+ binderPrefix + "f" + nft.format(binderId) + ".xml"));
				XmlFileUtil.writeFile(getFolderAsDoc(null, binderId, false,
						binderPrefix + "f" + nft.format(binderId), defList),
						zipOut);
				zipOut.closeEntry();
	
				//Only output entries if folder is selected (or is a sub-folder being output)
				if (binderIdsToExport.get(binderId)) {
					Map folderEntries = folderModule.getEntries(binderId, options);
					List searchEntries = (List) folderEntries.get("search_entries");
		
					for (int i = 0; i < searchEntries.size(); i++) {
						Map searchEntry = (Map) searchEntries.get(i);
						Long entryId = Long.valueOf(searchEntry.get("_docId")
								.toString());
						FolderEntry entry = folderModule.getEntry(binderId,
								entryId);
						processEntry(zipOut, entry, binderPrefix + "f"
								+ nft.format(binderId), defList);
					}
				}
			}

			String entityLetter = isWorkspace ? "w" : "f";
			Set<FileAttachment> attachments = binder.getFileAttachments();
	
			for (FileAttachment attach : attachments) {
				processBinderAttachment(zipOut, binder, attach, binderPrefix
						+ entityLetter + nft.format(binderId));
			}
      	}

		return;
	}

	private static void processBinderAttachment(ZipOutputStream zipOut, Binder binder,
			FileAttachment attachment, String pathName) throws IOException {

		processAttachment(zipOut, binder, binder, attachment, pathName);

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
			String pathName, Set defList) throws Exception {
		String fullId = null;

		fullId = calcFullId(entry);

		if (!pathName.equals("")) {
			zipOut.putNextEntry(new ZipEntry(pathName + File.separator + "e"
					+ fullId + ".xml"));
			XmlFileUtil.writeFile(getEntryAsDoc(null, entry.getParentBinder()
					.getId(), entry.getId(), false, pathName + File.separator
					+ "e" + fullId, defList), zipOut);
		} else {
			zipOut.putNextEntry(new ZipEntry("e" + fullId + ".xml"));
			XmlFileUtil.writeFile(getEntryAsDoc(null, entry.getParentBinder()
					.getId(), entry.getId(), false, "e" + fullId, defList),
					zipOut);
		}
		zipOut.closeEntry();

		defList.add(entry.getEntryDef());

		Set<FileAttachment> attachments = entry.getFileAttachments();
		for (FileAttachment attach : attachments) {
			processEntryAttachment(zipOut, entry, fullId, attach, pathName);
		}

		List<FolderEntry> replies = entry.getReplies();
		for (FolderEntry reply : replies) {
			processEntryReply(zipOut, reply, fullId, pathName, defList);
		}

		return;
	}

	private static void processEntryReply(ZipOutputStream zipOut, FolderEntry reply,
			String fullId, String pathName, Set defList) throws Exception {
		String newFullId = fullId + "_" + nft.format(reply.getId());

		if (!pathName.equals("")) {
			zipOut.putNextEntry(new ZipEntry(pathName + File.separator + "e"
					+ newFullId + ".xml"));
			XmlFileUtil.writeFile(getEntryAsDoc(null, reply.getParentBinder()
					.getId(), reply.getId(), false, pathName + File.separator
					+ "e" + newFullId, defList), zipOut);
		} else {
			zipOut.putNextEntry(new ZipEntry("e" + newFullId + ".xml"));
			XmlFileUtil.writeFile(getEntryAsDoc(null, reply.getParentBinder()
					.getId(), reply.getId(), false, "e" + newFullId, defList),
					zipOut);
		}
		zipOut.closeEntry();

		defList.add(reply.getEntryDef());

		Set<FileAttachment> attachments = reply.getFileAttachments();
		for (FileAttachment attach : attachments) {
			processEntryAttachment(zipOut, reply, newFullId, attach, pathName);
		}

		List<FolderEntry> reps = reply.getReplies();
		for (FolderEntry rep : reps) {
			processEntryReply(zipOut, rep, newFullId, pathName, defList);
		}

		return;
	}

	private static void processEntryAttachment(ZipOutputStream zipOut,
			FolderEntry entry, String fullId, FileAttachment attachment,
			String pathName) throws IOException {

		processAttachment(zipOut, entry.getParentBinder(), entry, attachment,
				pathName + File.separator + "e" + fullId);

		return;
	}

	private static void processAttachment(ZipOutputStream zipOut, Binder binder,
			DefinableEntity entity, FileAttachment attachment, String pathName)
			throws IOException {

		String fileName = filename8BitSingleByteOnly(attachment, SPropsUtil
				.getBoolean("export.filename.8bitsinglebyte.only", true));

		Set fileVersions = attachment.getFileVersions();
		Iterator<VersionAttachment> versionIter = fileVersions.iterator();

		String fileExt = EntityIndexUtils.getFileExtension(attachment
				.getFileItem().getName());

		// latest version

		VersionAttachment vAttach = versionIter.next();

		InputStream fileStream = null;
		try {
			fileStream = fileModule.readFile(binder, entity,
					vAttach);

			zipOut.putNextEntry(new ZipEntry(pathName + File.separator
					+ fileName));
			FileUtil.copy(fileStream, zipOut);
			zipOut.closeEntry();

			fileStream.close();
		} catch (Exception e) {
			logger.error(e);
			logger.error(NLT.get("export.error.attachment") + " - " + binder.getPathName().toString() + 
					", entryId=" + entity.getId().toString() + ", " + fileName);

			zipOut.putNextEntry(new ZipEntry(pathName + File.separator
					+ fileName + ".error_message.txt"));
			zipOut.write(NLT.get("export.error.attachment",
					"Error processing this attachment").getBytes());
			zipOut.closeEntry();
			if (fileStream != null) fileStream.close();
		}

		// older versions, from highest to lowest

		for (int i = 1; i < fileVersions.size(); i++) {
			vAttach = versionIter.next();

			int versionNum = fileVersions.size() - i;

			fileStream = null;
			try {
				fileStream = fileModule.readFile(binder, entity,
						vAttach);

				zipOut.putNextEntry(new ZipEntry(pathName + File.separator
						+ fileName + ".versions" + File.separator + versionNum
						+ "." + fileExt));
				FileUtil.copy(fileStream, zipOut);
				zipOut.closeEntry();

				fileStream.close();
			} catch (Exception e) {
				logger.error(e);
				logger.error(NLT.get("export.error.attachment") + " - " + binder.getPathName().toString() + 
						", entryId=" + entity.getId().toString() + ", " + fileName);

				zipOut.putNextEntry(new ZipEntry(pathName + File.separator
						+ fileName + ".versions" + File.separator + versionNum
						+ "." + fileExt + ".error_message.txt"));
				zipOut.write(NLT.get("export.error.attachment",
						"Error processing this attachment").getBytes());
				zipOut.closeEntry();
				if (fileStream != null) fileStream.close();
			}
		}

	}

	private static Document getEntryAsDoc(String accessToken, long binderId,
			long entryId, boolean includeAttachments, String pathName,
			Set defList) {
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
		adjustAttachmentUrls(doc, pathName);

		// workflows
		addWorkflows(doc.getRootElement(), entry, defList);

		return doc;
	}

	private static Document getFolderAsDoc(String accessToken, long folderId,
			boolean includeAttachments, String pathName, Set defList) {
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
		adjustAttachmentUrls(doc, pathName);

		// binder settings
		addSettingsList(doc.getRootElement(), folder, defList);

		// workflows
		addWorkflows(doc.getRootElement(), folder, defList);

		return doc;
	}

	private static Document getWorkspaceAsDoc(String accessToken, long workspaceId,
			boolean includeAttachments, String pathName, Set defList) {
		Long wId = new Long(workspaceId);

		// Retrieve the raw workspace.
		Workspace workspace = workspaceModule.getWorkspace(wId);

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
		adjustAttachmentUrls(doc, pathName);

		// binder settings
		addSettingsList(doc.getRootElement(), workspace, defList);

		// workflows
		addWorkflows(doc.getRootElement(), workspace, defList);

		return doc;
	}

	private static void addEntityAttributes(Element entityElem, DefinableEntity entity) {
		entityElem.addAttribute("id", entity.getId().toString());
		Binder binder = entity.getParentBinder();
		if (binder != null) entityElem.addAttribute("binderId", binder.getId().toString());

		if (entity.getEntryDef() != null) {
			entityElem.addAttribute("definitionId", entity.getEntryDef()
					.getId());
		}

		entityElem.addAttribute("title", entity.getTitle());
		addZoneId(entityElem, entity);
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
		DefinitionHelper.addPrincipalToDocument(entityElem, stamp.getPrincipal());
	}
	private static void addRating(Element element, DefinableEntity entity) {
		if (entity.getAverageRating() != null) {
			element.addAttribute("averageRating", entity.getAverageRating()
					.getAverage().toString());
			element.addAttribute("ratingCount", entity.getAverageRating()
					.getCount().toString());
		}
	}

	private static void addZoneId(Element element, DefinableEntity entity) {
		ZoneInfo zoneInfo = ExportHelper.getZoneInfo();
		String entityZoneUUID = zoneInfo.getId() + "." + entity.getId().toString();
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

			public String getFlagElementName() {
				return "export";
			}
		};

		definitionModule.walkDefinition(entity, visitor, null);

		// see if title and attachments have been handled
		Element root = entity.getEntryDef().getDefinition().getRootElement();
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
			DefinitionHelper.addPrincipalToDocument(team, p);
		}

		return team;
	}

	private static void adjustAttachmentUrls(Document entityDoc, String pathName) {
		String xPath = "//attribute[@type='attachFiles']//file//@href";

		List hrefs = entityDoc.selectNodes(xPath);

		for (Attribute attr : (List<Attribute>) hrefs) {
			File tempFile = new File(attr.getValue());
			attr.setValue(pathName + File.separator + tempFile.getName());
		}
	}

	private static void addWorkflows(Element element, DefinableEntity entity,
			Set defList) {

		Element workflowsEle = element.addElement("workflows");

		if (entity instanceof FolderEntry) {
			for (WorkflowState workflow : ((FolderEntry) entity)
					.getWorkflowStates()) {
				if (workflow != null) {
					if (workflowsEle != null) {
						defList.add(workflow.getDefinition());

						Element value = workflowsEle.addElement("process");
						value.addAttribute("definitionId", workflow
								.getDefinition().getId());
						value.addAttribute("name", workflow.getDefinition()
								.getName());
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
					value
							.addAttribute("name",
									((Definition) workflowDefinitions.get(i))
											.getName());
				}
			}

			Map workflowAssociations = ((Binder) entity)
					.getWorkflowAssociations();

			addDefinitions(defList,
					new ArrayList(workflowAssociations.values()));

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

	private static void addSettingsList(Element element, Binder binder, Set defList) {

		Element settingsEle = element.addElement("settings");

		// views

		List<Definition> viewDefinitions = binder.getViewDefinitions();

		Element views = settingsEle.addElement("views");

		for (int i = 0; i < viewDefinitions.size(); i++) {
			if (views != null) {
				Element value = views.addElement("view");
				value.addAttribute("definitionId", viewDefinitions.get(i)
						.getId());
				value.addAttribute("name", viewDefinitions.get(i).getName());
			}
		}

		// entries

		List<Definition> entryDefinitions = binder.getEntryDefinitions();

		Element entries = settingsEle.addElement("entries");

		for (int i = 0; i < entryDefinitions.size(); i++) {
			if (entries != null) {
				Element value = entries.addElement("entry");
				value.addAttribute("definitionId", entryDefinitions.get(i)
						.getId());
				value.addAttribute("name", entryDefinitions.get(i).getName());
			}
		}
	}

	private static void addDefinitions(Set defList, List defListToAdd) {
		for (Definition def : (List<Definition>) defListToAdd) {
			defList.add(def);
		}
	}

	private static void exportDefinitionList(ZipOutputStream zipOut, Set defList)
			throws Exception {
		for (Definition def : (Set<Definition>) defList) {
			exportDefinition(zipOut, def);
		}
	}

	private static void exportDefinition(ZipOutputStream zipOut, Definition def)
			throws Exception {
		String name = def.getName();

		if (Validator.isNull(name))
			name = def.getTitle();

		zipOut.putNextEntry(new ZipEntry("__definitions" + File.separator
				+ name + ".xml"));
		XmlFileUtil.writeFile(def.getDefinition(), zipOut);
		zipOut.closeEntry();
	}

	// Regex patterns used during import to check whether the xml file is for
	// folder, workspace, or entry
	private static Pattern entryPattern = Pattern.compile("e[0-9]{8}");
	private static Pattern workspacePattern = Pattern.compile("." + binderPrefix
			+ "w[0-9]{8}");
	private static Pattern folderPattern = Pattern.compile("." + binderPrefix
			+ "f[0-9]{8}");

	public static void importZip(Long binderId, InputStream fIn, StatusTicket statusTicket) throws IOException {
		Binder binder = binderModule.getBinder(binderId);
		ZipInputStream zIn = new ZipInputStream(fIn);

		// key-value pairs: old exported entry id - new entry id assigned during
		// import
		HashMap entryIdMap = new HashMap();

		// key-value pairs: old exported binder id - new binder id assigned
		// during import
		HashMap binderIdMap = new HashMap();

		String tempDir = deploy(zIn);

		try {
			File tempDirFile = new File(tempDir);
			importDir(tempDirFile, tempDir, binderId, entryIdMap, binderIdMap, statusTicket);
		} finally {
			FileUtil.deltree(tempDir);
		}
	}

	private static void importDir(File currentDir, String tempDir, Long topBinderId,
			Map entryIdMap, Map binderIdMap, StatusTicket statusTicket) throws IOException {

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
				importDir(child, tempDir, topBinderId, entryIdMap, binderIdMap, statusTicket);
			else {
				String fileExt = EntityIndexUtils.getFileExtension(child
						.getName().toLowerCase());

				if (child.getAbsolutePath().startsWith(
						tempDir + File.separator + "__definitions"
								+ File.separator)) {

					// adding definition to top binder locally if it does not
					// already exist

					Binder topBinder = loadBinder(topBinderId);

					String xmlStr = null;

					FileInputStream input = new FileInputStream(child);

					ByteArrayOutputStream output = new ByteArrayOutputStream();

					int data = 0;
					while ((data = input.read()) != -1) {
						output.write(data);
					}

					xmlStr = output.toString();
					output.close();
					input.close();

					Document tempDoc = getDocument(xmlStr);

					String defId = getDatabaseId(tempDoc);

					if (defId.equals(ObjectKeys.DEFAULT_MIRRORED_FILE_ENTRY_DEF)
							|| defId.equals(ObjectKeys.DEFAULT_MIRRORED_FILE_FOLDER_DEF)) {
						// don't add definitions if they are for mirrored file
						// entries
						// or mirrored file folders
					} else {
						definitionModule.addDefinition(tempDoc, topBinder, false);
					}

				} else if (fileExt.equals("xml")) {

					// need to check if this xml file is for a folder,
					// workspace,
					// entry, or just an attachment.
					// checking by regex-ing the filename.

					// entry?
					Matcher m = entryPattern.matcher(child.getName());
					boolean result = m.lookingAt();

					if (result) {
						String xmlStr = null;

						FileInputStream input = new FileInputStream(child);
						ByteArrayOutputStream output = new ByteArrayOutputStream();

						int data = 0;
						while ((data = input.read()) != -1) {
							output.write(data);
						}

						xmlStr = output.toString();
						output.close();
						input.close();

						Document tempDoc = getDocument(xmlStr);
						String defId = getDefinitionId(tempDoc);

						if (defId.equals(ObjectKeys.DEFAULT_MIRRORED_FILE_ENTRY_DEF)) {
							setDefinitionId(tempDoc,
									ObjectKeys.DEFAULT_LIBRARY_ENTRY_DEF);
							defId = getDefinitionId(tempDoc);
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
							if (parentId == null)
								folder_addEntryWithXML(null, newBinderId,
										defId, xmlStr, tempDir, entryIdMap,
										binderIdMap, entryId, statusTicket);
							else {
								Long newParentId = (Long) entryIdMap.get(parentId);
								folder_addReplyWithXML(null, newBinderId,
										newParentId, defId, xmlStr, tempDir,
										entryIdMap, binderIdMap, entryId);
							}
						}
					}

					// workspace?
					m = workspacePattern.matcher(child.getName());
					result = m.lookingAt();

					if (result) {
						String xmlStr = null;
						FileInputStream input = new FileInputStream(child);
						ByteArrayOutputStream output = new ByteArrayOutputStream();
						int data = 0;

						while ((data = input.read()) != -1) {
							output.write(data);
						}

						xmlStr = output.toString();
						output.close();
						input.close();

						Document tempDoc = getDocument(xmlStr);
						String defId = getDefinitionId(tempDoc);
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
							binder_addBinderWithXML(null, newParentId, defId,
									xmlStr, binderId, binderIdMap, statusTicket);
						}
					}

					// folder?
					m = folderPattern.matcher(child.getName());
					result = m.lookingAt();

					if (result) {
						String xmlStr = null;
						FileInputStream input = new FileInputStream(child);
						ByteArrayOutputStream output = new ByteArrayOutputStream();
						int data = 0;

						while ((data = input.read()) != -1) {
							output.write(data);
						}

						xmlStr = output.toString();
						output.close();
						input.close();

						Document tempDoc = getDocument(xmlStr);
						String defId = getDefinitionId(tempDoc);

						if (defId
								.equals(ObjectKeys.DEFAULT_MIRRORED_FILE_FOLDER_DEF)) {
							setDefinitionId(tempDoc,
									ObjectKeys.DEFAULT_LIBRARY_FOLDER_DEF);
							defId = getDefinitionId(tempDoc);
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
							binder_addBinderWithXML(null, newParentId, defId,
									xmlStr, binderId, binderIdMap, statusTicket);
						}
					}
				}
			}
		}
	}

	private static String getEntityType(Document entity) {
		return entity.getRootElement().getName();
	}

	private static String getDefinitionId(Document entity) {
		return entity.getRootElement().attributeValue("definitionId", "");
	}

	private static void setDefinitionId(Document entity, String defId) {
		entity.getRootElement().attribute("definitionId").setValue(defId);
	}

	private static String getDatabaseId(Document entity) {
		return entity.getRootElement().attributeValue("databaseId", "");
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

	private static long binder_addBinderWithXML(String accessToken, long parentId,
			String definitionId, String inputDataAsXML, long binderId,
			Map binderIdMap, StatusTicket statusTicket) {

		final Document doc = getDocument(inputDataAsXML);

		try {
			Long newBinderId = binderModule.addBinder(new Long(parentId),
					definitionId, new DomInputData(doc, iCalModule),
					new HashMap(), null).getId().longValue();
			binderIdMap.put(binderId, newBinderId);

			// team members
			addTeamMembers(newBinderId, doc);

			// workflows
			final Binder binder = loadBinder(newBinderId);
			statusTicket.setStatus(NLT.get("administration.export_import.importing", new String[] {binder.getPathName()}));

			transactionTemplate.execute(new TransactionCallback() {
				public Object doInTransaction(TransactionStatus status) {

					// binder settings
					importSettingsList(doc, binder);

					// workflows
					importWorkflows(doc, binder);
					return null;
				}
			});

			return newBinderId;
		} catch (WriteFilesException e) {
			throw new RemotingException(e);
		}
	}

	private static long folder_addEntryWithXML(String accessToken, long binderId,
			String definitionId, String inputDataAsXML, String tempDir,
			Map entryIdMap, Map binderIdMap, Long entryId, StatusTicket statusTicket) {
		return addFolderEntry(accessToken, binderId, definitionId,
				inputDataAsXML, tempDir, entryIdMap, binderIdMap, entryId, statusTicket);
	}

	private static long addFolderEntry(String accessToken, long binderId,
			String definitionId, String inputDataAsXML,
			String tempDir, Map entryIdMap, Map binderIdMap, Long entryId, StatusTicket statusTicket) {

		final Document doc = getDocument(inputDataAsXML);
		String[] fileNames = new String[0];
		Map options = new HashMap();
		//Set the entry creator and modifier fields
		setSignature(options, doc);

		try {
			// create new entry
			long newEntryId = folderModule.addEntry(new Long(binderId),
					definitionId, new DomInputData(doc, iCalModule), null,
					options).getId().longValue();

			// add file attachments
			addFileAttachments(binderId, newEntryId, doc, tempDir);
			
			// add entry id to entry id map
			entryIdMap.put(entryId, newEntryId);

			// workflows
			try {
				final FolderEntry entry = folderModule.getEntry(null, newEntryId);
				statusTicket.setStatus(NLT.get("administration.export_import.importingEntry", new String[] {entry.getTitle()}));
				importWorkflows(doc, entry);
			} catch(Exception e) {}

			return newEntryId;
		} catch (WriteFilesException e) {
			throw new RemotingException(e);
		} catch (WriteEntryDataException e) {
			throw new RemotingException(e);
		}
	}

	private static long folder_addReplyWithXML(String accessToken, long binderId,
			long parentId, String definitionId, String inputDataAsXML,
			String tempDir, Map entryIdMap, Map binderIdMap, Long entryId) {
		return addReply(accessToken, binderId, parentId, definitionId,
				inputDataAsXML, tempDir, entryIdMap, binderIdMap, entryId);
	}

	private static long addReply(String accessToken, long binderId, long parentId,
			String definitionId, String inputDataAsXML, String tempDir,
			Map entryIdMap, Map binderIdMap, Long entryId) {

		Document doc = getDocument(inputDataAsXML);

		Map options = new HashMap();
		//Set the entry creator and modifier fields
		setSignature(options, doc);
		
		try {
			// add new reply
			long newEntryId = folderModule.addReply(new Long(binderId),
					new Long(parentId), definitionId,
					new DomInputData(doc, iCalModule), null, options).getId()
					.longValue();

			// add file attachments
			addFileAttachments(binderId, newEntryId, doc, tempDir);

			// add entry reply id to entry id map
			entryIdMap.put(entryId, newEntryId);

			// workflows
			try {
				final FolderEntry entry = folderModule.getEntry(null, newEntryId);
				importWorkflows(doc, entry);
			} catch(Exception e) {}

			return newEntryId;
		} catch (WriteFilesException e) {
			throw new RemotingException(e);
		} catch (WriteEntryDataException e) {
			throw new RemotingException(e);
		}
	}

	private static Document getDocument(String xml) {
		// Parse XML string into a document tree.
		try {
			return DocumentHelper.parseText(xml);
		} catch (DocumentException e) {
			logger.error(e);
			throw new IllegalArgumentException(e.toString());
		}
	}

	private static void addFileAttachments(Long binderId, Long entryId,
			Document entityDoc, String tempDir) {

		String xPath = "//attribute[@type='attachFiles']//file";
		List attachFiles = entityDoc.selectNodes(xPath);

		for (Element fileEle : (List<Element>) attachFiles) {
			boolean handled = false;

			String href = tempDir + File.separator
					+ fileEle.attributeValue("href", "");
			
			String filename = fileEle.getText();
			
			int numVersions = Integer.valueOf(fileEle
					.attributeValue("numVersions", "0"));
			
			String versionsDir = null;

			if (numVersions > 1) {
				versionsDir = tempDir + File.separator
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
							numVersions, versionsDir);
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
								href, numVersions, versionsDir);
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
								href, numVersions, versionsDir);
						handled = true;
						break;
					}
				}
			}
		}
	}

	private static void addFileVersions(Long binderId, Long entryId,
			String fileDataItemName, String filename, String href,
			int numVersions, String versionsDir) {

		String fileExt = EntityIndexUtils.getFileExtension(filename);
		InputStream iStream = null;

		if (numVersions > 1) {
			for (int i = 1; i < numVersions; i++) {
				try {
					iStream = new FileInputStream(new File(versionsDir
							+ File.separator + i + "." + fileExt));
					folderModule.modifyEntry(binderId, entryId,
							fileDataItemName, filename, iStream);
					iStream.close();
				} catch (Exception e) {
					logger.error(e);
					return;
				}
			}
		}

		try {
			iStream = new FileInputStream(new File(href));
			folderModule.modifyEntry(binderId, entryId, fileDataItemName,
					filename, iStream);
			iStream.close();
		} catch (Exception e) {
			logger.error(e);
			return;
		}
	}

	private static void setSignature(Map options, Document entityDoc) {
		User user = RequestContextHolder.getRequestContext().getUser();
		Element signature = (Element) entityDoc.selectSingleNode("//signature");
		if (signature != null) {
			Element creation = (Element)signature.selectSingleNode("./creation");
			if (creation != null) {
				String sDate = creation.attributeValue("date", "");
				Element value = (Element) creation.selectSingleNode("./value");
				if (!sDate.equals("") && value != null) {
					String id = value.attributeValue("id", "");
					String name = value.attributeValue("name", "");
					if (!name.equals("")) {
						List names = new ArrayList();
						names.add(name);
						List<Principal> principals = ResolveIds.getPrincipalsByName(names, false);
						if (!principals.isEmpty()) {
							Principal p = principals.get(0);
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
							} catch(java.text.ParseException e) {
							}
						}
					}
				}
			}
		}
	}
	
	private static File getTemporaryDirectory() {
		return new File(TempFileUtil.getTempFileDir("import"), UUID
				.randomUUID().toString());
	}

	private static String deploy(ZipInputStream zipIn) throws IOException {
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
			}
		} finally {
			zipIn.close();
		}

		return tempDir.getAbsolutePath();
	}

	private static void binder_setTeamMembers(String accessToken, long binderId,
			String[] memberNames) {

		Set<Long> ids = new HashSet();
		if (memberNames.length > 0) {
			Collection<Principal> principals = profileModule
					.getPrincipalsByName(Arrays.asList(memberNames));
			for (Principal p : principals) {
				ids.add(p.getId());
			}
		}
		binderModule.setTeamMembers(binderId, ids);
	}

	private static void addTeamMembers(Long binderId, Document entityDoc) {
		String xPath = "//team";
		Element team = (Element) entityDoc.selectSingleNode(xPath);

		boolean teamInherited = Boolean.parseBoolean(team
				.attributeValue("inherited", "false"));

		Binder binder = loadBinder(binderId);

		if (teamInherited)
			binder.setTeamMembershipInherited(true);
		else {
			List<String> names = new ArrayList<String>();
			xPath = "//team//value";
			List principals = entityDoc.selectNodes(xPath);

			for (Element member : (List<Element>) principals) {
				names.add(member.attributeValue("name", ""));
			}

			String[] namesArray = new String[names.size()];
			names.toArray(namesArray);

			binder_setTeamMembers(null, binderId, namesArray);
		}
	}

	private static void importWorkflows(Document entityDoc, DefinableEntity entity) {

		if (entity instanceof FolderEntry) {

			// end all workflows that started upon entry creation

			Set defaultWorkflows = ((FolderEntry) entity).getWorkflowStates();
			Iterator iter = defaultWorkflows.iterator();

			while (iter.hasNext()) {
				((FolderEntry) entity).removeWorkflowState((WorkflowState) iter
						.next());
			}

			// start up the imported workflows

			String xPath = "//workflows//process";
			List<Element> workflows = entityDoc.selectNodes(xPath);

			for (Element process : workflows) {
				String defId = process.attributeValue("definitionId", "");
				String state = process.attributeValue("state", "");
				Definition def = definitionModule.getDefinition(defId);

				EntityIdentifier entityIdentifier = new EntityIdentifier(entity
						.getId(), EntityIdentifier.EntityType.folderEntry);

				Map options = new HashMap();
				options
						.put(ObjectKeys.INPUT_OPTION_FORCE_WORKFLOW_STATE,
								state);

				workflowModule.addEntryWorkflow((FolderEntry) entity,
						entityIdentifier, def, options);

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
				if (!defId.equals("")) newDefinitionList.add(defId);
			}

			// associated workflows

			xPath = "//workflows//associated//process";
			workflows = entityDoc.selectNodes(xPath);

			Map<String, String> workflowAssociations = new HashMap<String, String>();

			for (Element process : workflows) {
				String entryDefId = process.attributeValue("entryDefinitionId", "");
				String workflowDefId = process.attributeValue("workflowDefinitionId", "");
				if (!entryDefId.equals("")) workflowAssociations.put(entryDefId, workflowDefId);
			}

			binderModule.setDefinitions(entity.getId(), newDefinitionList,
					workflowAssociations);
		}
	}

	private static void importSettingsList(Document entityDoc, Binder binder) {

		// current binder definitions

		List<Definition> newDefinitionList = binder.getDefinitions();

		// views

		String xPath = "//settings//views//view";
		List<Element> views = entityDoc.selectNodes(xPath);

		for (Element view : views) {
			String defId = view.attributeValue("definitionId", "");

			// don't want to include mirrored file folder as an imported view
			// setting

			if (!defId.equals("") && !defId.equals(ObjectKeys.DEFAULT_MIRRORED_FILE_FOLDER_DEF)) {
				newDefinitionList.add(definitionModule.getDefinition(defId));
			}
		}

		// entries

		xPath = "//settings//entries//entry";
		List<Element> entries = entityDoc.selectNodes(xPath);

		for (Element entry : entries) {
			String defId = entry.attributeValue("definitionId", "");

			// don't want to include mirrored file entry as an imported allowed
			// entry setting

			if (!defId.equals("") && !defId.equals(ObjectKeys.DEFAULT_MIRRORED_FILE_ENTRY_DEF)) {
				newDefinitionList.add(definitionModule.getDefinition(defId));
			}
		}

		binder.setDefinitions(newDefinitionList);
	}

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

				if (c >= 0 && c < 256) {
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

	private Binder loadBinder(Long binderId, Long zoneId) {
		Binder binder = coreDao.loadBinder(binderId, zoneId);
		if (binder.isDeleted())
			throw new NoBinderByTheIdException(binderId);
		return binder;
	}
	
	public static ZoneInfo getZoneInfo() {
		return zoneModule.getZoneInfo(RequestContextHolder.getRequestContext().getZoneId());
	}

}