/**
 * The contents of this file are governed by the terms of your license
 * with SiteScape, Inc., which includes disclaimers of warranties and
 * limitations on liability. You may not use this file except in accordance
 * with the terms of that license. See the license for the specific language
 * governing your rights and limitations under the license.
 *
 * Copyright (c) 2007 SiteScape, Inc.
 *
 */
package com.sitescape.team.remoting.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringBufferInputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.naming.OperationNotSupportedException;

import net.fortuna.ical4j.data.CalendarOutputter;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.ValidationException;

import org.apache.axis.Message;
import org.apache.axis.MessageContext;
import org.apache.axis.attachments.AttachmentPart;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import com.sitescape.team.ObjectKeys;
import com.sitescape.team.domain.Folder;
import com.sitescape.team.domain.FolderEntry;
import com.sitescape.team.domain.Principal;
import com.sitescape.team.domain.Workspace;
import com.sitescape.team.module.definition.DefinitionModule;
import com.sitescape.team.module.definition.DefinitionUtils;
import com.sitescape.team.module.definition.ws.ElementBuilderUtil;
import com.sitescape.team.module.file.WriteFilesException;
import com.sitescape.team.module.mail.MailModule;
import com.sitescape.team.remoting.Facade;
import com.sitescape.team.util.AbstractAllModulesInjected;
import com.sitescape.team.util.AllModulesInjected;
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
public abstract class AbstractFacade extends AbstractAllModulesInjected implements Facade {

	protected final Log logger = LogFactory.getLog(getClass());
	
	public String getDefinitionAsXML(String definitionId) {
		return getDefinitionModule().getDefinition(definitionId).getDefinition().getRootElement().asXML();
	}
	
	public String getDefinitionConfigAsXML() {
		return getDefinitionModule().getDefinitionConfig().getRootElement().asXML();
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

	private static class CalendarDataSource implements DataSource
	{
		String data = "";
		
		public CalendarDataSource(Calendar cal)
		{
			StringWriter writer = new StringWriter();
			CalendarOutputter out = new CalendarOutputter();
			try {
				out.output(cal, writer);
				data = writer.toString();
			} catch(IOException e) {
			} catch(ValidationException e) {
			}
		}
		
		public String getName() { return "com.sitescape.team.CalendarDataSource"; }
		public String getContentType() { return "text/calendar"; }
		
		public InputStream getInputStream() throws IOException
		{
			return new StringBufferInputStream(data);
		}
		
		public OutputStream getOutputStream() throws IOException
		{
			throw new IOException("Output not supported to this DataSource");
		}
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
		
		if(!entry.getEvents().isEmpty()) {
			Calendar eventCalendar = getIcalConverter().generate(entry, entry.getEvents(), MailModule.DEFAULT_TIMEZONE);
			DataHandler dh = new DataHandler(new CalendarDataSource(eventCalendar));
			MessageContext messageContext = MessageContext.getCurrentContext();
			Message responseMessage = messageContext.getResponseMessage();
			responseMessage.addAttachmentPart(new AttachmentPart(dh));
		}

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
	
	public void modifyFolderEntry(long binderId, long entryId, String inputDataAsXML) {
		Document doc = getDocument(inputDataAsXML);
		
		try {
			getFolderModule().modifyEntry(new Long(binderId), new Long(entryId), 
				new DomInputData(doc), new HashMap(), null, null);
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
		
		String xml = doc.getRootElement().asXML();
		
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
			getProfileModule().modifyEntry(new Long(binderId), new Long(principalId), new DomInputData(doc));
		}
		catch(WriteFilesException e) {
			throw new RemotingException(e);
		}

	}
	
	public void deletePrincipal(long binderId, long principalId) {
		try {
			getProfileModule().deleteEntry(new Long(binderId), new Long(principalId), false);
		}
		catch(WriteFilesException e) {
			throw new RemotingException(e);
		}

	}
	
	public String getWorkspaceTreeAsXML(long binderId, int levels) {
		com.sitescape.team.domain.Binder binder = null;
		
		if(binderId == -1) {
			binder = getWorkspaceModule().getTopWorkspace();
		} else {
			binder = getBinderModule().getBinder(new Long(binderId));
		}

		Document tree;
		String treeKey = "";
		if (binder instanceof Workspace) {
			tree = getWorkspaceModule().getDomWorkspaceTree(binder.getId(), new WsDomTreeBuilder(binder, true, this, treeKey), levels);
		} 
		else {
			//com.sitescape.team.domain.Folder topFolder = ((com.sitescape.team.domain.Folder)binder).getTopFolder();
			tree = getFolderModule().getDomFolderTree(binder.getId(), new WsDomTreeBuilder(binder, false, this, treeKey), levels);
			
			//if (topFolder == null) topFolder = (com.sitescape.team.domain.Folder)binder;
			//tree = getFolderModule().getDomFolderTree(topFolder.getId(), new WsDomTreeBuilder(topFolder, false, this, treeKey));
		}

		String xml = tree.getRootElement().asXML();
		//System.out.println(xml);

		return xml;
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
		final AllModulesInjected moduleSource = this;
		DefinitionModule.DefinitionVisitor visitor = new DefinitionModule.DefinitionVisitor() {
			public void visit(Element entryElement, Element flagElement, Map args)
			{
                if (flagElement.attributeValue("apply").equals("true")) {
                	String fieldBuilder = flagElement.attributeValue("elementBuilder");
					String nameValue = DefinitionUtils.getPropertyValue(entryElement, "name");									
					if (Validator.isNull(nameValue)) {nameValue = entryElement.attributeValue("name");}
                	ElementBuilderUtil.buildElement(entryElem, entry, nameValue, fieldBuilder, moduleSource);
                }
			}
			public String getFlagElementName() { return "webService"; }
		};
		
		getDefinitionModule().walkDefinition(entry, visitor);
		
	}
	
}
