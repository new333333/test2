package com.sitescape.ef.remoting.impl;

import java.io.IOException;
import java.text.DateFormat;
import java.util.List;
import java.util.Locale;

import org.apache.axis.AxisFault;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import com.sitescape.ef.context.request.RequestContextHolder;
import com.sitescape.ef.domain.Definition;
import com.sitescape.ef.domain.FolderEntry;
import com.sitescape.ef.domain.Principal;
import com.sitescape.ef.domain.User;
import com.sitescape.ef.module.binder.BinderModule;
import com.sitescape.ef.module.definition.DefinitionModule;
import com.sitescape.ef.module.definition.notify.Notify;
import com.sitescape.ef.module.file.WriteFilesException;
import com.sitescape.ef.module.folder.FolderModule;
import com.sitescape.ef.module.profile.ProfileModule;
import com.sitescape.ef.remoting.api.Binder;
import com.sitescape.ef.remoting.api.Facade;
import com.sitescape.ef.remoting.api.Folder;
import com.sitescape.ef.web.util.WebUrlUtil;

/**
 * POJO implementation of Facade interface.
 * 
 * @author jong
 *
 */
public abstract class AbstractFacade implements Facade {

	protected final Log logger = LogFactory.getLog(getClass());

	private FolderModule folderModule;
	private DefinitionModule definitionModule;
	private BinderModule binderModule;
	private ProfileModule profileModule;

	protected FolderModule getFolderModule() {
		return folderModule;
	}
	public void setFolderModule(FolderModule folderModule) {
		this.folderModule = folderModule;
	}
	protected DefinitionModule getDefinitionModule() {
		return definitionModule;
	}
	public void setDefinitionModule(DefinitionModule definitionModule) {
		this.definitionModule = definitionModule;
	}
	protected BinderModule getBinderModule() {
		return binderModule;
	}
	public void setBinderModule(BinderModule binderModule) {
		this.binderModule = binderModule;
	}
	protected ProfileModule getProfileModule() {
		return profileModule;
	}
	public void setProfileModule(ProfileModule profileModule) {
		this.profileModule = profileModule;
	}
	
	public String getDefinitionAsXML(String definitionId) {
		return getDefinitionModule().getDefinition(definitionId).getDefinition().asXML();
	}
	
	public String getDefinitionConfigAsXML() {
		return getDefinitionModule().getDefinitionConfig().asXML();
	}

	public Binder getBinder(long binderId) {
		com.sitescape.ef.domain.Binder dbinder = getBinderModule().getBinder(new Long(binderId));
		
		return DBinderToBinder(dbinder);
	}

	public String getFolderEntryAsXML(long binderId, long entryId) {
		Long bId = new Long(binderId);
		Long eId = new Long(entryId);
		
		// Retrieve the raw entry.
		FolderEntry entry = 
			getFolderModule().getEntry(bId, eId);

		Document doc = DocumentHelper.createDocument();
		
		Element entryElem = doc.addElement("entry");
		
		// Handle structured fields of the entry known at compile time. 
		entryElem.addAttribute("id", entry.getId().toString());
		entryElem.addAttribute("binderId", entry.getParentBinder().getId().toString());
		entryElem.addAttribute("definitionId", entry.getEntryDef().getId());
		entryElem.addAttribute("title", entry.getTitle());
		entryElem.addAttribute("docNumber", entry.getDocNumber());
		entryElem.addAttribute("docLevel", String.valueOf(entry.getDocLevel()));
		String entryUrl = WebUrlUtil.getEntryViewURL(entry);
		entryElem.addAttribute("href", entryUrl);
		
		// Handle custom fields driven by corresponding definition. 
		addCustomElements(entryElem, entry);
		
		String xml = doc.asXML();
		
		/*
		System.out.println("*** XML representation for entry " + entry.getId());
		System.out.println(xml);
		System.out.println("*** Pretty XML representation for entry " + entry.getId());
		prettyPrint(doc);
		*/
		
		return xml;
	}
	
	public long addFolderEntry(long binderId, String definitionId, String inputDataAsXML) {
		Document doc = getDocument(inputDataAsXML);
		
		try {
			return getFolderModule().addEntry(new Long(binderId), definitionId, 
				new DomInputData(doc), null).longValue();
		}
		catch(WriteFilesException e) {
			throw new RemotingException(e);
		}
	}
	
	public abstract void uploadFolderFile(long binderId, long entryId, 
			String fileUploadDataItemName, String fileName);
	
	public Folder getFolder(long binderId) {
		return DFolderToFolder(getFolderModule().getFolder(new Long(binderId)));
	}
	
	public void modifyFolderEntry(long binderId, long entryId, String inputDataAsXML) {
		Document doc = getDocument(inputDataAsXML);
		
		try {
			getFolderModule().modifyEntry(new Long(binderId), new Long(entryId), 
				new DomInputData(doc), null);
		}
		catch(WriteFilesException e) {
			throw new RemotingException(e);
		}			
	}
	
	public void deleteFolderEntry(long binderId, long entryId) {
		getFolderModule().deleteEntry(new Long(binderId), new Long(entryId));
	}
	
	public long addReply(long binderId, long parentId, String definitionId, String inputDataAsXML) {
		Document doc = getDocument(inputDataAsXML);
		
		try {
			return getFolderModule().addReply(new Long(binderId), new Long(parentId), 
				definitionId, new DomInputData(doc), null).longValue();
		}
		catch(WriteFilesException e) {
			throw new RemotingException(e);
		}
	}
	
	public String getPrincipalAsXML(long binderId, long principalId) {
		Long bId = new Long(binderId);
		Long pId = new Long(principalId);
		
		// Retrieve the raw entry.
		Principal entry = 
			getProfileModule().getEntry(bId, pId);

		Document doc = DocumentHelper.createDocument();
		
		Element entryElem = doc.addElement("entry");
		
		// Handle structured fields of the entry known at compile time. 
		entryElem.addAttribute("id", entry.getId().toString());
		entryElem.addAttribute("binderId", entry.getParentBinder().getId().toString());
		entryElem.addAttribute("definitionId", entry.getEntryDef().getId());
		entryElem.addAttribute("title", entry.getTitle());
		entryElem.addAttribute("disabled", Boolean.toString(entry.isDisabled()));
		entryElem.addAttribute("reserved", Boolean.toString(entry.isReserved()));
		
		// Handle custom fields driven by corresponding definition. 
		addCustomElements(entryElem, entry);
		
		String xml = doc.asXML();
		
		return xml;
	}
	
	public long addUser(long binderId, String definitionId, String inputDataAsXML) {
		Document doc = getDocument(inputDataAsXML);
		
		try {
			return getProfileModule().addUser(new Long(binderId), definitionId, new DomInputData(doc), null).longValue();
		}
		catch(WriteFilesException e) {
			throw new RemotingException(e);
		}

	}
	
	public long addGroup(long binderId, String definitionId, String inputDataAsXML) {
		Document doc = getDocument(inputDataAsXML);
		
		try {
			return getProfileModule().addGroup(new Long(binderId), definitionId, new DomInputData(doc), null).longValue();
		}
		catch(WriteFilesException e) {
			throw new RemotingException(e);
		}

	}
	
	public void modifyPrincipal(long binderId, long principalId, String inputDataAsXML) {
		Document doc = getDocument(inputDataAsXML);
		
		try {
			getProfileModule().modifyEntry(new Long(binderId), new Long(principalId), new DomInputData(doc), null);
		}
		catch(WriteFilesException e) {
			throw new RemotingException(e);
		}

	}
	
	public void deletePrincipal(long binderId, long principalId) {
		try {
			getProfileModule().deleteEntry(new Long(binderId), new Long(principalId));
		}
		catch(WriteFilesException e) {
			throw new RemotingException(e);
		}

	}
	
	private Binder DBinderToBinder(com.sitescape.ef.domain.Binder dbinder) {
		Binder binder = new Binder();
		
		binder.setId(dbinder.getId().longValue());
		binder.setName(dbinder.getName());
		binder.setZoneName(dbinder.getZoneName());
		binder.setType(dbinder.getType());
		binder.setTitle(dbinder.getTitle());
		binder.setParentBinderId(dbinder.getParentBinder().getId().longValue());
		
		List entryDefs = dbinder.getEntryDefs();
		String[] entryDefinitionIds = new String[entryDefs.size()];
		for(int i = 0; i < entryDefinitionIds.length; i++) {
			entryDefinitionIds[i] = ((Definition) entryDefs.get(i)).getId();
		}
		binder.setEntryDefinitionIds(entryDefinitionIds);
		
		return binder;	
	}
	
	private Folder DFolderToFolder(com.sitescape.ef.domain.Folder dfolder) {
		Folder folder = new Folder();
		
		if(dfolder.getParentFolder() != null)		
			folder.setParentFolderId(dfolder.getParentFolder().getId());
		
		if(dfolder.getTopFolder() != null)
			folder.setTopFolderId(dfolder.getTopFolder().getId());
		
		return folder;
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
	
	private void addCustomElements(Element entryElem, com.sitescape.ef.domain.Entry entry) {
		// This is a kludge (hack) that uses the implementation of the existing 
		// email notification mechanism to quickly demonstrate the concept only.
		// TODO To be rewritten
		
		Notify notify = new Notify();
		notify.setFull(true);
		User user = RequestContextHolder.getRequestContext().getUser();
		Locale locale = user.getLocale();
		notify.setLocale(locale);
		notify.setDateFormat(DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.FULL, locale));
		
		getDefinitionModule().addNotifyElementForEntity(entryElem, notify, entry, 
				new String[] {"entryData", "commonEntryData"});
		
	}
	
}
