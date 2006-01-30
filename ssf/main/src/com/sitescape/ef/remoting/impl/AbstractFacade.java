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
import com.sitescape.ef.domain.User;
import com.sitescape.ef.module.binder.BinderModule;
import com.sitescape.ef.module.definition.DefinitionModule;
import com.sitescape.ef.module.definition.notify.Notify;
import com.sitescape.ef.module.folder.FolderModule;
import com.sitescape.ef.remoting.api.Binder;
import com.sitescape.ef.remoting.api.Facade;
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
		com.sitescape.ef.domain.FolderEntry entry = 
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

		// Parse XML string into a document tree.
		Document doc = null;
		try {
			doc = DocumentHelper.parseText(inputDataAsXML);
		} catch (DocumentException e) {
			logger.error(e);
			throw new IllegalArgumentException(e.toString());
		}
		
		return getFolderModule().addEntry(new Long(binderId), definitionId, 
				new DomInputData(doc), null).longValue();
	}
	
	public abstract void uploadFolderFile(long binderId, long entryId, 
			String fileUploadDataItemName, String fileName);
	
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
		
		getDefinitionModule().addNotifyElementForEntry(entryElem, notify, entry, 
				new String[] {"entryData", "commonEntryData"});
		
	}
	
	private Binder DBinderToBinder(com.sitescape.ef.domain.Binder dbinder) {
		Binder binder = new Binder();
		
		binder.setId(dbinder.getId().longValue());
		binder.setName(dbinder.getName());
		binder.setZoneName(dbinder.getZoneName());
		binder.setType(dbinder.getType());
		binder.setTitle(dbinder.getTitle());
		binder.setOwningWorkspaceId(dbinder.getOwningWorkspace().getId().longValue());
		
		List entryDefs = dbinder.getEntryDefs();
		String[] entryDefinitionIds = new String[entryDefs.size()];
		for(int i = 0; i < entryDefinitionIds.length; i++) {
			entryDefinitionIds[i] = ((Definition) entryDefs.get(i)).getId();
		}
		binder.setEntryDefinitionIds(entryDefinitionIds);
		
		return binder;	
	}
}
