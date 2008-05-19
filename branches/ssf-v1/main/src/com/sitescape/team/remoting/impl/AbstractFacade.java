/**
 * The contents of this file are subject to the Common Public Attribution License Version 1.0 (the "CPAL");
 * you may not use this file except in compliance with the CPAL. You may obtain a copy of the CPAL at
 * http://www.opensource.org/licenses/cpal_1.0. The CPAL is based on the Mozilla Public License Version 1.1
 * but Sections 14 and 15 have been added to cover use of software over a computer network and provide for
 * limited attribution for the Original Developer. In addition, Exhibit A has been modified to be
 * consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT WARRANTY OF ANY KIND,
 * either express or implied. See the CPAL for the specific language governing rights and limitations
 * under the CPAL.
 * 
 * The Original Code is ICEcore. The Original Developer is SiteScape, Inc. All portions of the code
 * written by SiteScape, Inc. are Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * 
 * 
 * Attribution Information
 * Attribution Copyright Notice: Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by ICEcore]
 * Attribution URL: [www.icecore.com]
 * Graphic Image as provided in the Covered Code [web/docroot/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are defined in the CPAL as a
 * work which combines Covered Code or portions thereof with code not governed by the terms of the CPAL.
 * 
 * 
 * SITESCAPE and the SiteScape logo are registered trademarks and ICEcore and the ICEcore logos
 * are trademarks of SiteScape, Inc.
 */
package com.sitescape.team.remoting.impl;
import java.io.File;
import java.io.IOException;
import java.io.StringBufferInputStream;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;

import net.fortuna.ical4j.data.ParserException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Branch;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import com.sitescape.team.ObjectKeys;
import com.sitescape.team.context.request.RequestContextHolder;
import com.sitescape.team.domain.Binder;
import com.sitescape.team.domain.Definition;
import com.sitescape.team.domain.FileAttachment;
import com.sitescape.team.domain.Folder;
import com.sitescape.team.domain.FolderEntry;
import com.sitescape.team.domain.HKey;
import com.sitescape.team.domain.Principal;
import com.sitescape.team.domain.Subscription;
import com.sitescape.team.domain.User;
import com.sitescape.team.domain.Workspace;
import com.sitescape.team.module.definition.DefinitionModule;
import com.sitescape.team.module.definition.DefinitionUtils;
import com.sitescape.team.module.definition.ws.ElementBuilder;
import com.sitescape.team.module.definition.ws.ElementBuilderUtil;
import com.sitescape.team.module.file.WriteFilesException;
import com.sitescape.team.module.folder.index.IndexUtils;
import com.sitescape.team.module.profile.index.ProfileIndexUtils;
import com.sitescape.team.module.shared.EmptyInputData;
import com.sitescape.team.module.shared.EntityIndexUtils;
import com.sitescape.team.module.shared.InputDataAccessor;
import com.sitescape.team.module.shared.XmlUtils;
import com.sitescape.team.remoting.Facade;
import com.sitescape.team.search.BasicIndexUtils;
import com.sitescape.team.security.function.Function;
import com.sitescape.team.util.AbstractAllModulesInjected;
import com.sitescape.team.util.DatedMultipartFile;
import com.sitescape.team.util.LongIdUtil;
import com.sitescape.team.util.SPropsUtil;
import com.sitescape.team.util.SimpleProfiler;
import com.sitescape.team.util.stringcheck.StringCheckUtil;
import com.sitescape.team.web.tree.WebSvcTreeHelper;
import com.sitescape.team.web.tree.WsDomTreeBuilder;
import com.sitescape.team.web.util.WebUrlUtil;
import com.sitescape.util.Validator;

/**
 * POJO implementation of Facade interface.
 * 
 * Important: This class is NOT tied to any specific remoting protocol 
 * such as SOAP. Therefore don't ever put protocol or tool specific code 
 * (such as capability that utilizes Axis engine directly) into this class.  
 * 
 * @author jong
 *
 */
public abstract class AbstractFacade extends AbstractAllModulesInjected implements Facade, ElementBuilder.BuilderContext {

	protected final Log logger = LogFactory.getLog(getClass());

	public static class AttachmentHandler {
		public void handleAttachment(FileAttachment att, String webUrl) {}
	}

	protected AttachmentHandler attachmentHandler = new AttachmentHandler();
	
	public void handleAttachment(FileAttachment att, String webUrl)
	{
		attachmentHandler.handleAttachment(att, webUrl);
	}

	public String getDefinitionAsXML(String definitionId) {
		return getDefinitionModule().getDefinition(definitionId).getDefinition().getRootElement().asXML();
	}
	
	public String getDefinitionConfigAsXML() {
		return getDefinitionModule().getDefinitionConfig().getRootElement().asXML();
	}

	public String getDefinitionListAsXML() {
		List<Definition> defs = getDefinitionModule().getDefinitions();
		Document doc = DocumentHelper.createDocument();
		doc.addElement("definitions");
		Element root = doc.getRootElement();
		for (Definition def:defs) {
			Element defElement = root.addElement(ObjectKeys.XTAG_ELEMENT_TYPE_DEFINITION);
			defElement.addAttribute(ObjectKeys.XTAG_ATTRIBUTE_NAME, def.getName());
			defElement.addAttribute(ObjectKeys.XTAG_ATTRIBUTE_INTERNALID, def.getInternalId());
			defElement.addAttribute("id", def.getId().toString());
			defElement.addAttribute(ObjectKeys.XTAG_ATTRIBUTE_TYPE, Integer.toString(def.getType()));			
			XmlUtils.addAttributeCData(defElement, ObjectKeys.XTAG_ENTITY_TITLE, ObjectKeys.XTAG_TYPE_STRING, def.getTitle());
			
		}
		return root.asXML();
	}
	public void setDefinitions(long binderId, String[] definitionIds, String[] workflowAssociations) {
		HashMap wfs = new HashMap();
		for (int i=0; i<workflowAssociations.length; i++) {
			String [] vals = workflowAssociations[i].split(",");
			wfs.put(vals[0], vals[1]);
		}
		getBinderModule().setDefinitions(binderId, Arrays.asList(definitionIds), wfs);
	}
	public long addFolder(long parentBinderId, long binderConfigId, String title)
	{
		try {
			return getAdminModule().addBinderFromTemplate(binderConfigId, parentBinderId, title, null);
		} catch(WriteFilesException e) {
			throw new RemotingException(e);
		}
	}
		
	public String getFolderEntriesAsXML(long binderId) {
		com.sitescape.team.domain.Binder binder = getBinderModule().getBinder(new Long(binderId));

		Document doc = DocumentHelper.createDocument();
		Element folderElement = doc.addElement("folder");
		folderElement.addAttribute("title", binder.getTitle());
		folderElement.addAttribute("id", binder.getId().toString());

			if (binder instanceof Folder) {
			Map options = new HashMap();
			Map folderEntries = getFolderModule().getFullEntries(binder.getId(), options);
			List entrylist = (List)folderEntries.get(ObjectKeys.FULL_ENTRIES);
			Iterator entryIterator = entrylist.listIterator();
			while (entryIterator.hasNext()) {
				FolderEntry entry  = (FolderEntry) entryIterator.next();
				Element entryElem = folderElement.addElement("entry");
				addEntryAttributes(entryElem, entry);
			}
		}
		
		return doc.getRootElement().asXML();
	}

	public String getFolderEntryAsXML(long binderId, long entryId, boolean includeAttachments) {
		Long bId = new Long(binderId);
		Long eId = new Long(entryId);
		
		// Retrieve the raw entry.
		FolderEntry entry = 
			getFolderModule().getEntry(bId, eId);

		Document doc = DocumentHelper.createDocument();
		
		Element entryElem = doc.addElement("entry");

		// Handle structured fields of the entry known at compile time. 
		addEntryAttributes(entryElem, entry);

		// Handle custom fields driven by corresponding definition. 
		addCustomElements(entryElem, entry);
		
		String xml = doc.getRootElement().asXML();
		
		/*
		System.out.println("*** XML representation for entry " + entry.getId());
		System.out.println(xml);
		System.out.println("*** Pretty XML representation for entry " + entry.getId());
		prettyPrint(doc);
		*/
		
		return xml;
	}
	
	private void addEntryAttributes(Element entryElem, FolderEntry entry)
	{
		entryElem.addAttribute("id", entry.getId().toString());
		entryElem.addAttribute("binderId", entry.getParentBinder().getId().toString());
		entryElem.addAttribute("definitionId", entry.getEntryDef().getId());
		entryElem.addAttribute("title", entry.getTitle());
		entryElem.addAttribute("docNumber", entry.getDocNumber());
		entryElem.addAttribute("docLevel", String.valueOf(entry.getDocLevel()));
		String entryUrl = WebUrlUtil.getEntryViewURL(entry);
		entryElem.addAttribute("href", entryUrl);
	}
	private void addEntryAttributes(Element entryElem, Map entry, boolean isPrincipal)
	{
		entryElem.addAttribute("id", (String) entry.get(EntityIndexUtils.DOCID_FIELD));
		entryElem.addAttribute("binderId", (String) entry.get(EntityIndexUtils.BINDER_ID_FIELD));
		entryElem.addAttribute("definitionId", (String) entry.get(EntityIndexUtils.COMMAND_DEFINITION_FIELD));
		entryElem.addAttribute("title", (String) entry.get(EntityIndexUtils.TITLE_FIELD));
		if(isPrincipal) {
			entryElem.addAttribute("type", (String) entry.get(EntityIndexUtils.ENTRY_TYPE_FIELD));
			entryElem.addAttribute("reserved", Boolean.toString(entry.get(ProfileIndexUtils.RESERVEDID_FIELD)!=null));
		} else {
			entryElem.addAttribute("docNumber", (String) entry.get(IndexUtils.DOCNUMBER_FIELD));
			entryElem.addAttribute("docLevel", "" + (new HKey((String) entry.get(IndexUtils.SORTNUMBER_FIELD))).getLevel());

			String entryUrl = WebUrlUtil.getEntryViewURL((String) entry.get(EntityIndexUtils.BINDER_ID_FIELD),
					(String) entry.get(EntityIndexUtils.DOCID_FIELD));
			entryElem.addAttribute("href", entryUrl);
		}
	}

	static int count = 0;
	static SimpleProfiler profiler = null;
	
	public long migrateFolderEntry(long binderId, String definitionId,
			String inputDataAsXML, 
			String creator, Calendar creationDate, String modifier, Calendar modificationDate,
			boolean subscribe) {
		Map options = new HashMap();
		options.put(ObjectKeys.INPUT_OPTION_NO_INDEX, Boolean.TRUE);
    	options.put(ObjectKeys.INPUT_OPTION_NO_WORKFLOW, Boolean.TRUE);
		getMigrationOptions(options, creator, creationDate, modifier, modificationDate);
		inputDataAsXML = StringCheckUtil.check(inputDataAsXML);
		
		Document doc = getDocument(inputDataAsXML);
		if(profiler == null) {
			profiler = new SimpleProfiler("webServices");
			count = 0;
		}
		SimpleProfiler.setProfiler(profiler);
		try {
			Long entryId = getFolderModule().addEntry(new Long(binderId), definitionId, 
				new DomInputData(doc, getIcalModule()), null , options).longValue();
			if(subscribe) {
				getFolderModule().addSubscription(new Long(binderId), entryId,
						Subscription.MESSAGE_STYLE_NO_ATTACHMENTS_EMAIL_NOTIFICATION, creator);
			}
			return entryId;
		}
		catch(WriteFilesException e) {
			throw new RemotingException(e);
		} finally {
			if(++count == 10000) {
				logger.info(SimpleProfiler.toStr());
				profiler = null;
				SimpleProfiler.clearProfiler();
			}
		}
	}
	public long addFolderEntry(long binderId, String definitionId, String inputDataAsXML, String attachedFileName) {
		inputDataAsXML = StringCheckUtil.check(inputDataAsXML);
		
		Document doc = getDocument(inputDataAsXML);
		if(profiler == null) {
			profiler = new SimpleProfiler("webServices");
			count = 0;
		}
		SimpleProfiler.setProfiler(profiler);
		try {
			return getFolderModule().addEntry(new Long(binderId), definitionId, 
				new DomInputData(doc, getIcalModule()), getFileAttachments("ss_attachFile", new String[]{attachedFileName} ), null).longValue();
		}
		catch(WriteFilesException e) {
			throw new RemotingException(e);
		} finally {
			if(++count == 10000) {
				logger.info(SimpleProfiler.toStr());
				profiler = null;
				SimpleProfiler.clearProfiler();
			}
		}
	}
	public void migrateEntryWorkflow(long binderId, long entryId, String definitionId, String startState, String modifier, Calendar modificationDate) {
		Map options = new HashMap();
		options.put(ObjectKeys.INPUT_OPTION_NO_INDEX, Boolean.TRUE);
		options.put(ObjectKeys.INPUT_OPTION_FORCE_WORKFLOW_STATE, startState);
		getMigrationOptions(options, null, null, modifier, modificationDate);
		getFolderModule().addEntryWorkflow(binderId, entryId, definitionId, options);
	}
	public Map getFileAttachments(String fileUploadDataItemName, String[] fileNames)
	{
		return new HashMap();
	}
	

	public void uploadCalendarEntries(long folderId, String iCalDataAsXML)
	{
		iCalDataAsXML = StringCheckUtil.check(iCalDataAsXML);
		
		Document doc = getDocument(iCalDataAsXML);
		List<Node> entryNodes = (List<Node>) doc.selectNodes("//entry");
		for(Node entryNode : entryNodes) {
			String iCal = entryNode.getText();
			try {
				getIcalModule().parseToEntries(folderId, new StringBufferInputStream(iCal));
			} catch(IOException e) {
				throw new RemotingException(e);
			} catch(ParserException e) {
				throw new RemotingException(e);
			}
		}
	}
	
	public String search(String query, int offset, int maxResults)
	{
		query = StringCheckUtil.check(query);
		
		Document queryDoc = getDocument(query);
		
		Document doc = DocumentHelper.createDocument();
		Element folderElement = doc.addElement("searchResults");

		Map folderEntries = getBinderModule().executeSearchQuery(queryDoc, offset, maxResults);
		List entrylist = (List)folderEntries.get(ObjectKeys.SEARCH_ENTRIES);
		Iterator entryIterator = entrylist.listIterator();
		while (entryIterator.hasNext()) {
			Map result = (Map) entryIterator.next();
			String docType = (String) result.get(BasicIndexUtils.DOC_TYPE_FIELD);
			Element resultElem = null;
			if(BasicIndexUtils.DOC_TYPE_ATTACHMENT.equals(docType)) {
				String attachmentType = "file";
				resultElem = folderElement.addElement(attachmentType);
			} else if(BasicIndexUtils.DOC_TYPE_BINDER.equals(docType)) {
				String binderType = (String) result.get(EntityIndexUtils.ENTITY_FIELD);
				resultElem = folderElement.addElement(docType);
				resultElem.addAttribute("type", binderType);
				resultElem.addAttribute("id", (String) result.get(EntityIndexUtils.DOCID_FIELD));
				resultElem.addAttribute("title", (String) result.get(EntityIndexUtils.TITLE_FIELD));
			} else if(BasicIndexUtils.DOC_TYPE_ENTRY.equals(docType)) {
				String entryType = (String) result.get(EntityIndexUtils.ENTRY_TYPE_FIELD);
				String elementName = null;
				boolean isPrincipal = true;
				if(EntityIndexUtils.ENTRY_TYPE_ENTRY.equalsIgnoreCase(entryType)) {
					elementName="entry";
					isPrincipal = false;
				} else if(EntityIndexUtils.ENTRY_TYPE_REPLY.equalsIgnoreCase(entryType)) {
					elementName="entry";
					isPrincipal = false;					
				} else if(EntityIndexUtils.ENTRY_TYPE_USER.equalsIgnoreCase(entryType)) {
					elementName="principal";
				} else if(EntityIndexUtils.ENTRY_TYPE_GROUP.equalsIgnoreCase(entryType)) {
					elementName="principal";
				}
				resultElem = folderElement.addElement(elementName);
				addEntryAttributes(resultElem, result, isPrincipal);
			}
		}
		
		return doc.getRootElement().asXML();
	}

	public void modifyFolderEntry(long binderId, long entryId, String inputDataAsXML) {
		inputDataAsXML = StringCheckUtil.check(inputDataAsXML);
		
		Document doc = getDocument(inputDataAsXML);
		
		try {
			getFolderModule().modifyEntry(new Long(binderId), new Long(entryId), 
				new DomInputData(doc, getIcalModule()), new HashMap(), null, null, null);
		}
		catch(WriteFilesException e) {
			throw new RemotingException(e);
		}			
	}

	/*
	public void deleteFolderEntry(long binderId, long entryId) {
		getFolderModule().deleteEntry(new Long(binderId), new Long(entryId), WebStatusTicket.nullTicket);
	}
	*/
	
	public long migrateReply(long binderId, long parentId, String definitionId,
			String inputDataAsXML,  String creator, Calendar creationDate, String modifier, Calendar modificationDate) {

		Map options = new HashMap();
		options.put(ObjectKeys.INPUT_OPTION_NO_INDEX, Boolean.TRUE);
    	options.put(ObjectKeys.INPUT_OPTION_NO_WORKFLOW, Boolean.TRUE);
		getMigrationOptions(options, creator, creationDate, modifier, modificationDate);
		inputDataAsXML = StringCheckUtil.check(inputDataAsXML);

		Document doc = getDocument(inputDataAsXML);
		
		try {
			return getFolderModule().addReply(new Long(binderId), new Long(parentId), 
				definitionId, new DomInputData(doc, getIcalModule()), null, options).longValue();
		}
		catch(WriteFilesException e) {
			throw new RemotingException(e);
		}
	}
	public long addReply(long binderId, long parentId, String definitionId, String inputDataAsXML, String attachedFileName) {
		inputDataAsXML = StringCheckUtil.check(inputDataAsXML);

		Document doc = getDocument(inputDataAsXML);
		
		try {
			return getFolderModule().addReply(new Long(binderId), new Long(parentId), 
				definitionId, new DomInputData(doc, getIcalModule()), getFileAttachments("ss_attachFile", new String[]{attachedFileName} ), null).longValue();
		}
		catch(WriteFilesException e) {
			throw new RemotingException(e);
		}
	}
	
	private Element addPrincipalToDocument(Branch doc, Principal entry)
	{
		Element entryElem = doc.addElement("principal");
		
		// Handle structured fields of the entry known at compile time. 
		entryElem.addAttribute("id", entry.getId().toString());
		entryElem.addAttribute("binderId", entry.getParentBinder().getId().toString());
		entryElem.addAttribute("definitionId", entry.getEntryDef().getId());
		entryElem.addAttribute("title", entry.getTitle());
		entryElem.addAttribute("emailAddress", entry.getEmailAddress());
		entryElem.addAttribute("type", entry.getEntityType().toString());
		entryElem.addAttribute("disabled", Boolean.toString(entry.isDisabled()));
		entryElem.addAttribute("reserved", Boolean.toString(entry.isReserved()));
		entryElem.addAttribute("name", entry.getName());
		
		return entryElem;
	}
	
	private Element addPrincipalToDocument(Branch doc, Map user)
	{
		Element entryElem = doc.addElement("principal");
		
		entryElem.addAttribute("id", (String) user.get(EntityIndexUtils.DOCID_FIELD));
		entryElem.addAttribute("binderId", (String) user.get(EntityIndexUtils.BINDER_ID_FIELD));
		entryElem.addAttribute("definitionId", (String) user.get(EntityIndexUtils.COMMAND_DEFINITION_FIELD));
		entryElem.addAttribute("title", (String) user.get(EntityIndexUtils.TITLE_FIELD));
		entryElem.addAttribute("emailAddress", (String) user.get(ProfileIndexUtils.EMAIL_FIELD));
		entryElem.addAttribute("type", (String) user.get(EntityIndexUtils.ENTRY_TYPE_FIELD));
		entryElem.addAttribute("reserved", Boolean.toString(user.get(ProfileIndexUtils.RESERVEDID_FIELD)!=null));
		if (EntityIndexUtils.ENTRY_TYPE_USER.equals(user.get(EntityIndexUtils.ENTRY_TYPE_FIELD))) {
			entryElem.addAttribute("name", (String)user.get(ProfileIndexUtils.LOGINNAME_FIELD));
		} else {
			entryElem.addAttribute("name", (String)user.get(ProfileIndexUtils.GROUPNAME_FIELD));			
		}
		
/*
 * I don't know how to get this from the map
		entryElem.addAttribute("disabled", Boolean.toString(entry.isDisabled()));
*/

		return entryElem;
	}
	
	public String getAllPrincipalsAsXML(int firstRecord, int maxRecords) {
		Document doc = DocumentHelper.createDocument();
    	Map options = new HashMap();
    	options.put(ObjectKeys.SEARCH_OFFSET, new Integer(firstRecord));
    	options.put(ObjectKeys.SEARCH_MAX_HITS, new Integer(maxRecords));
		Map results = getProfileModule().getPrincipals(getProfileModule().getProfileBinder().getId(), options);
		List users = (List) results.get(ObjectKeys.SEARCH_ENTRIES);
		Element rootElement = doc.addElement("principals");
		rootElement.addAttribute("first", ""+firstRecord);
		rootElement.addAttribute("count", ((Integer)results.get(ObjectKeys.TOTAL_SEARCH_RECORDS_RETURNED)).toString());
		rootElement.addAttribute("total", ((Integer)results.get(ObjectKeys.SEARCH_COUNT_TOTAL)).toString());
		for(Object searchEntry : users) {
			Map user = (Map) searchEntry;
			addPrincipalToDocument(rootElement, user);
		}
		String xml = rootElement.asXML();
		
		return xml;
	}
	public String getPrincipalAsXML(long binderId, long principalId) {
		Long bId = new Long(binderId);
		Long pId = new Long(principalId);
		
		// Retrieve the raw entry.
		Principal entry = 
			getProfileModule().getEntry(bId, pId);

		Document doc = DocumentHelper.createDocument();
		
		Element entryElem = addPrincipalToDocument(doc, entry);
		
		// Handle custom fields driven by corresponding definition. 
		addCustomElements(entryElem, entry);
		
		String xml = doc.getRootElement().asXML();
		
		return xml;
	}
	
	public long addUserWorkspace(long userId) {
		User user = (User)getProfileModule().getEntry(getProfileModule().getProfileBinder().getId(), userId);
		return getProfileModule().addUserWorkspace(user).getId();
	}
	
	public String getWorkspaceTreeAsXML(long binderId, int levels, String page) {
		com.sitescape.team.domain.Binder binder = null;
		
		if(binderId == -1) {
			binder = getWorkspaceModule().getTopWorkspace();
		} else {
			binder = getBinderModule().getBinder(new Long(binderId));
		}

		Document tree;
		if (binder instanceof Workspace) {
			tree = getWorkspaceModule().getDomWorkspaceTree(binder.getId(), new WsDomTreeBuilder(binder, true, this, new WebSvcTreeHelper(), page), levels);
		} 
		else {
			//com.sitescape.team.domain.Folder topFolder = ((com.sitescape.team.domain.Folder)binder).getTopFolder();
			tree = getBinderModule().getDomBinderTree(binder.getId(), 
					new WsDomTreeBuilder(binder, false, this, new WebSvcTreeHelper(), page), levels);
			
			//if (topFolder == null) topFolder = (com.sitescape.team.domain.Folder)binder;
			//tree = getFolderModule().getDomFolderTree(topFolder.getId(), new WsDomTreeBuilder(topFolder, false, this, treeKey));
		}

		String xml = tree.getRootElement().asXML();
		//System.out.println(xml);

		return xml;
	}
	
	public String getTeamMembersAsXML(long binderId)
	{
		Binder binder = getBinderModule().getBinder(new Long(binderId));
		SortedSet<Principal> principals = getBinderModule().getTeamMembers(binder, true);
		Document doc = DocumentHelper.createDocument();
		Element team = doc.addElement("team");
		team.addAttribute("inherited", binder.isTeamMembershipInherited()?"true":"false");
		for(Principal p : principals) {
			addPrincipalToDocument(team, p);
		}
		
		return doc.getRootElement().asXML();
	}
	
	public void setTeamMembers(long binderId, String[] memberNames) {
		Collection<Principal> principals = getProfileModule().getPrincipalsByName(Arrays.asList(memberNames));
		Set<Long>ids = new HashSet();
		for (Principal p:principals) {
			ids.add(p.getId());
		}
		
		getBinderModule().setTeamMembers(binderId, ids);
	}
	public String getTeamsAsXML()
	{
		User user = RequestContextHolder.getRequestContext().getUser();
		List<Map> myTeams = getBinderModule().getTeamMemberships(user.getId());
		Document doc = DocumentHelper.createDocument();
		Element teams = doc.addElement("teams");
		teams.addAttribute("principalId", user.getId().toString());
		for(Map binder : myTeams) {
			Element team = teams.addElement("team");
			team.addAttribute("title", (String) binder.get(EntityIndexUtils.TITLE_FIELD));
			team.addAttribute("binderId", (String) binder.get(EntityIndexUtils.DOCID_FIELD));
		}
		return doc.getRootElement().asXML();
	}
	
	private Document getDocument(String xml) {
		// Parse XML string into a document tree.
		try {
			return DocumentHelper.parseText(xml);
		} catch (DocumentException e) {
			logger.error(e);
			throw new IllegalArgumentException(e.toString());
		}
	}
	
	private void prettyPrint(Document doc) {
		OutputFormat format = OutputFormat.createPrettyPrint();
		try {
			XMLWriter writer = new XMLWriter(System.out, format);
			writer.write(doc);
		}
		catch(IOException e) {}
	}
	
	private void addCustomElements(final Element entryElem, final com.sitescape.team.domain.Entry entry) {
		final ElementBuilder.BuilderContext context = this;
		DefinitionModule.DefinitionVisitor visitor = new DefinitionModule.DefinitionVisitor() {
			public void visit(Element entryElement, Element flagElement, Map args)
			{
                if (flagElement.attributeValue("apply").equals("true")) {
                	String fieldBuilder = flagElement.attributeValue("elementBuilder");
					String nameValue = DefinitionUtils.getPropertyValue(entryElement, "name");									
					if (Validator.isNull(nameValue)) {nameValue = entryElement.attributeValue("name");}
                	ElementBuilderUtil.buildElement(entryElem, entry, nameValue, fieldBuilder, context);
                }
			}
			public String getFlagElementName() { return "webService"; }
		};
		
		getDefinitionModule().walkDefinition(entry, visitor, null);
		
	}
	public void setFunctionMembership(long binderId, String inputDataAsXml) {
		Binder binder = getBinderModule().getBinder(binderId);
		List<Function> functions = getAdminModule().getFunctions();
		Document doc = getDocument(inputDataAsXml);
		Map wfms = new HashMap();
		List<Element> wfmElements = doc.getRootElement().selectNodes("./" + ObjectKeys.XTAG_ELEMENT_TYPE_FUNCTION_MEMBERSHIP);
		
		for (Element wfmElement:wfmElements) {
			 String functionName = XmlUtils.getProperty(wfmElement, ObjectKeys.XTAG_WA_FUNCTION_NAME);
			 Function func = null;
			 for (Function f:functions) {
				 if (f.getName().equals(functionName)) {
					 func = f;
					 break;
				 }
			 }
			 if (func == null) continue;
			 List<Element> nameElements = wfmElement.selectNodes("./" + ObjectKeys.XTAG_ELEMENT_TYPE_PROPERTY + "[@name='" + ObjectKeys.XTAG_WA_MEMBER_NAME + "']");
			 Set<String> names = new HashSet();
			 for (Element e:nameElements) {
				 names.add(e.getTextTrim());				 
			 }
			 Set<Long>ids = new HashSet();
			 if (!names.isEmpty()) {
				 Collection<Principal> principals = getProfileModule().getPrincipalsByName(names);
				 for (Principal p:principals) {
					 ids.add(p.getId());
				 }
			 }
			 ids.addAll(LongIdUtil.getIdsAsLongSet(XmlUtils.getProperty(wfmElement, ObjectKeys.XTAG_WA_MEMBERS), ","));

			 if (ids.isEmpty()) continue;
			 wfms.put(func.getId(), ids);
		}
		if (wfms.isEmpty()) return;
		getAdminModule().setWorkAreaFunctionMembershipInherited(binder, false); 
		getAdminModule().setWorkAreaFunctionMemberships(binder, wfms);
	}
	public void setFunctionMembershipInherited(long binderId, boolean inherit) {
		Binder binder = getBinderModule().getBinder(binderId);
		getAdminModule().setWorkAreaFunctionMembershipInherited(binder, inherit); 		
	}
	public void setOwner(long binderId, long userId) {
		Binder binder = getBinderModule().getBinder(binderId);
		getAdminModule().setWorkAreaOwner(binder, userId, false); 		
	}
	public long migrateBinder(long parentId, String definitionId,
			String inputDataAsXML, String creator, Calendar creationDate, String modifier, Calendar modificationDate) {
		inputDataAsXML = StringCheckUtil.check(inputDataAsXML);
		
		try {
			Map options = new HashMap();
			//let binder be indexed, so it can be found
			getMigrationOptions(options, creator, creationDate, modifier, modificationDate);
			Document doc = getDocument(inputDataAsXML);
			Definition def = getDefinitionModule().getDefinition(definitionId);
			Binder binder = getBinderModule().getBinder(parentId);
			InputDataAccessor inputData = new DomInputData(doc, getIcalModule());
			Long binderId;
			if (def.getType() == Definition.WORKSPACE_VIEW) {
				binderId = getWorkspaceModule().addWorkspace(binder.getId(), def.getId(), inputData, null, options);
		   } else {
			   if (binder instanceof Workspace)
				   binderId =  getWorkspaceModule().addFolder(binder.getId(), def.getId(), inputData, null, options);
			   else {
				   binderId = getFolderModule().addFolder(binder.getId(), def.getId(), inputData, null, options);
				   //inherit by default
				   getBinderModule().setDefinitions(binderId, Boolean.TRUE);
			   }
		   }
			return binderId;
		} catch(WriteFilesException e) {
			throw new RemotingException(e);
		}
	}


	public void migrateFolderFileStaged(long binderId, long entryId, 
			String fileUploadDataItemName, String fileName, String stagedFileRelativePath, String modifier, Calendar modificationDate){
		boolean enable = SPropsUtil.getBoolean("staging.upload.files.enable", false);
		if(enable) {
			fileUploadDataItemName = StringCheckUtil.check(fileUploadDataItemName);
			stagedFileRelativePath = StringCheckUtil.check(stagedFileRelativePath);
			fileName = StringCheckUtil.check(fileName);
			
			// Get the staged file
			String rootPath = SPropsUtil.getString("staging.upload.files.rootpath", "").trim();
			File file = new File(rootPath, stagedFileRelativePath);
			
			// Wrap it in a datastructure expected by our app.
			DatedMultipartFile mf = new DatedMultipartFile(fileName, file, false, modifier, modificationDate.getTime());
						
			// Create a map of file item names to items 
			Map fileItems = new HashMap();
			fileItems.put(fileUploadDataItemName, mf);
			
			Map options = new HashMap();
			options.put(ObjectKeys.INPUT_OPTION_NO_INDEX, Boolean.TRUE);
			options.put(ObjectKeys.INPUT_OPTION_NO_WORKFLOW, Boolean.TRUE);
	    	options.put(ObjectKeys.INPUT_OPTION_NO_MODIFICATION_DATE, Boolean.TRUE);
			try {
				// Finally invoke the business method. 
				getFolderModule().modifyEntry(new Long(binderId), new Long(entryId), 
					new EmptyInputData(), fileItems, null, null, options);
				// If you're here, the transaction was successful. 
				// Check if we need to delete the staged file or not.
				if(SPropsUtil.getBoolean("staging.upload.files.delete.after", false)) {
					file.delete();
				}
			}
			catch(WriteFilesException e) {
				throw new RemotingException(e);
			}
		}
		else {
			throw new UnsupportedOperationException("Staged file upload is disabled: " + binderId + ", " + 
					entryId + ", " + fileUploadDataItemName + ", " + fileName + ", " + stagedFileRelativePath);			
		}
	}
	public void indexFolder(long folderId) {
		getBinderModule().indexBinder(folderId, true);
	}
	protected void getMigrationOptions(Map options, String creator, Calendar creationDate,
			  String modifier, Calendar modificationDate)
	{
		if (creator != null) options.put(ObjectKeys.INPUT_OPTION_CREATION_NAME, creator);
		if (creationDate != null) options.put(ObjectKeys.INPUT_OPTION_CREATION_DATE, creationDate);
		if (modifier != null) options.put(ObjectKeys.INPUT_OPTION_MODIFICATION_NAME, modifier);
		if (modificationDate != null) options.put(ObjectKeys.INPUT_OPTION_MODIFICATION_DATE, modificationDate);
	}

}
