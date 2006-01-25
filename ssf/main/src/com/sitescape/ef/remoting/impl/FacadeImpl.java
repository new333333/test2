package com.sitescape.ef.remoting.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

import javax.activation.DataHandler;

import org.apache.axis.AxisFault;
import org.apache.axis.Message;
import org.apache.axis.MessageContext;
import org.apache.axis.attachments.AttachmentPart;
import org.apache.axis.attachments.Attachments;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.springframework.util.FileCopyUtils;

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

	public String getDefinitionAsXML(String definitionId) {
		return getDefinitionModule().getDefinition(definitionId).getDefinition().asXML();
	}
	public String getDefinitionConfigAsXML() {
		return getDefinitionModule().getDefinitionConfig().asXML();
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
		
		return getFolderModule().addEntry(new Long(binderId), definitionId, new DomInputData(doc), null).longValue();
	}
	
	public int uploadFile(long binderId, long entryId, String fileUploadDataItemName, String fileName) {
		// Just to make sure that everything is set up properly.
		User user = RequestContextHolder.getRequestContext().getUser();
		
		
		InputStream is =null;
		FileOutputStream os = null;

		try
		{
			// Get all the attachments
			AttachmentPart[] attachments = getMessageAttachments();

			//Extract the first attachment. (Since in this case we have only one attachment sent)
			DataHandler dh = attachments[0].getDataHandler();

			is = dh.getInputStream();
			File file = new File("C:/junk2", fileName);
			os = new FileOutputStream(file);
			FileCopyUtils.copy(is, os);
			is.close();
			os.close();
		}
		catch(Exception e)
		{
			String status="File Could Not Be Saved: "+e.getMessage();
			System.out.println("In Impl: "+e);
		}

		return 1;
		
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
	
	/**
	* Extract attachments from the current request
	* @return a list of attachmentparts or an empty array for no attachments 
	* support in this axis buid/runtime
	*/
	private AttachmentPart[] getMessageAttachments() throws AxisFault {
		MessageContext msgContext = MessageContext.getCurrentContext();
		Message reqMsg = msgContext.getRequestMessage();
		Attachments messageAttachments = reqMsg.getAttachmentsImpl();
		if (null == messageAttachments) {
			System.out.println("no attachment support");
			return new AttachmentPart[0];
		}
		int attachmentCount = messageAttachments.getAttachmentCount();
		AttachmentPart attachments[] = new AttachmentPart[attachmentCount];
		Iterator it = messageAttachments.getAttachments().iterator();
		int count = 0;
		while (it.hasNext()) {
			AttachmentPart part = (AttachmentPart) it.next();
			attachments[count++] = part;
		}
		return attachments;
	}
}
