package com.sitescape.ef.remoting.impl;

import java.io.IOException;
import java.text.DateFormat;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import com.sitescape.ef.context.request.RequestContextHolder;
import com.sitescape.ef.domain.User;
import com.sitescape.ef.module.definition.DefinitionModule;
import com.sitescape.ef.module.definition.notify.Notify;
import com.sitescape.ef.module.folder.FolderModule;
import com.sitescape.ef.remoting.api.Entry;
import com.sitescape.ef.remoting.api.Facade;
import com.sitescape.ef.web.util.WebUrlUtil;

/**
 * POJO implementation of Facade interface.
 * 
 * @author jong
 *
 */
public class FacadeImpl implements Facade {

	protected final Log logger = LogFactory.getLog(getClass());

	private FolderModule folderModule;
	private DefinitionModule definitionModule;

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

	public Entry getEntry(long binderId, long entryId) {
		// TODO fake for now
		/*
		Entry entry = new Entry();
		entry.setBinderId(new Long(binderId));
		entry.setId(new Long(entryId));
		entry.setTitle("Hey, what do you expect?");
		*/
		
		Long bId = new Long(binderId);
		Long eId = new Long(entryId);
		
		// Fetch a domain entry - either read transaction or no transaction.
		com.sitescape.ef.domain.FolderEntry domainEntry = 
			getFolderModule().getEntry(bId, eId);
		String title = domainEntry.getTitle();
		
		// Populate the remote entry with the data from domain entry.
		Entry entry = new Entry();
		entry.setBinderId(bId);
		entry.setId(eId);
		entry.setTitle(title);
		
		// TODO The following code tests lazy loading - to be removed
		Map attrs = domainEntry.getCustomAttributes();
		for(Iterator i = attrs.entrySet().iterator(); i.hasNext();) {
			Map.Entry me = (Map.Entry) i.next();
			Object key = me.getKey();
			Object val = me.getValue();
			System.out.println(key.toString());
		}
		
		// Try updating a field on the entry - done outside of any transaction.
		domainEntry.setTitle(title + ".a");
		
		// Invoking the following method causes an update transaction,
		// which will make Hibernate to flush out the change we made to the
		// domain object above (dirty object) at the transaction commit.
		// This technique works as long as the dirty object is part of the
		// same session.
		//getFolderModule().setFake();
		
		return entry;
	}

	public String getEntryAsXML(long binderId, long entryId) {
		Long bId = new Long(binderId);
		Long eId = new Long(entryId);
		
		// Retrieve the raw entry.
		com.sitescape.ef.domain.FolderEntry entry = 
			getFolderModule().getEntry(bId, eId);

		Document doc = DocumentHelper.createDocument();
		
		Element entryElem = doc.addElement("entry");
		
		// Handle structured fields of the entry known at compile time. 
		entryElem.addAttribute("id", entry.getId().toString());
		entryElem.addAttribute("title", entry.getTitle());
		entryElem.addAttribute("docNumber", entry.getDocNumber());
		entryElem.addAttribute("docLevel", String.valueOf(entry.getDocLevel()));
		String entryUrl = WebUrlUtil.getEntryViewURL(entry);
		entryElem.addAttribute("href", entryUrl);
		
		// Handle custom fields driven by corresponding definition. 
		addCustomElements(entryElem, entry);
		
		String xml = doc.asXML();
		
		System.out.println("*** XML representation for entry " + entry.getId());
		System.out.println(xml);
		System.out.println("*** Pretty XML representation for entry " + entry.getId());
		prettyPrint(doc);
		
		return xml;
	}
	
	public long addEntry(long binderId, String definitionId, String inputDataAsXML) {

		// Parse XML string into a document tree.
		Document doc = null;
		try {
			doc = DocumentHelper.parseText(inputDataAsXML);
		} catch (DocumentException e) {
			logger.error(e);
			throw new IllegalArgumentException(e.toString());
		}
		
		//return getFolderModule().addEntry(binderId, definitionId, doc, null).longValue();
		return 0;// TODO 
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
		
		getDefinitionModule().addNotifyElementForEntry(entryElem, notify, entry, 
				new String[] {"entryData", "commonEntryData"});
		
	}
}
