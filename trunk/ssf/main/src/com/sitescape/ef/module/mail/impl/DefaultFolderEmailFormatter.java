
package com.sitescape.ef.module.mail.impl;

import java.util.Collection;
import java.util.Iterator;
import java.util.HashMap;
import java.util.Map;
import java.util.Date;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;
import java.util.ArrayList;
import java.io.File;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.IOException;
import java.io.FileOutputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.DocumentSource;
import org.springframework.util.FileCopyUtils;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMultipart;
import javax.mail.BodyPart;
import javax.mail.Part;
import javax.mail.internet.MimeBodyPart;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.Flags;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.Templates;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import com.sitescape.ef.domain.FolderEntry;
import com.sitescape.ef.domain.Folder;
import com.sitescape.ef.domain.PostingDef;
import com.sitescape.ef.domain.Definition;
import com.sitescape.ef.dao.util.OrderBy;
import com.sitescape.ef.module.mail.FolderEmailFormatter;
import com.sitescape.ef.security.AccessControlManager;
import com.sitescape.ef.security.acl.AclManager;
import com.sitescape.ef.util.DirPath;
import com.sitescape.ef.util.NLT;
import com.sitescape.ef.portletadapter.AdaptedPortletURL;
import com.sitescape.ef.web.WebKeys;
import com.sitescape.ef.web.util.WebHelper;
import com.sitescape.util.Validator;
import com.sitescape.ef.domain.Principal;
import com.sitescape.ef.domain.HistoryStamp;
import com.sitescape.util.GetterUtil;
import com.sitescape.ef.module.definition.DefinitionModule;
import com.sitescape.ef.module.definition.notify.Notify;
import com.sitescape.ef.module.folder.FolderModule;
import com.sitescape.ef.module.mail.MailModule;
/**
 * @author Janet McCann
 *
 */
public class DefaultFolderEmailFormatter implements FolderEmailFormatter {
	private Log logger = LogFactory.getLog(getClass());
    protected AccessControlManager accessControlManager;
    protected AclManager aclManager;
    private FolderModule folderModule;
    protected DefinitionModule definitionModule;
    protected MailModule mailModule;
	private TransformerFactory transFactory = TransformerFactory.newInstance();

	protected Map transformers = new HashMap();
    public DefaultFolderEmailFormatter () {
	}
    public void setAccessControlManager(
            AccessControlManager accessControlManager) {
        this.accessControlManager = accessControlManager;
    }
    public void setAclManager(AclManager aclManager) {
        this.aclManager = aclManager;
    }
    public void setDefinitionModule(DefinitionModule definitionModule) {
        this.definitionModule = definitionModule;
    }
    public void setFolderModule(FolderModule folderModule) {
    	this.folderModule = folderModule;
    }
	public void setMailModule(MailModule mailModule) {
		this.mailModule = mailModule;
	}


	/**
	 * Only supports lookups from topFolder ie)per forum
	 */
	public OrderBy getLookupOrder(Folder folder) {
		return new OrderBy("HKey.sortKey");
	}
	private int checkDate(HistoryStamp dt1, Date dt2) {
		if (dt1 == null) return -1;
		Date date = dt1.getDate();
		if (date == null) return -1;
		return date.compareTo(dt2);
	}
	private int checkDate(HistoryStamp dt1, HistoryStamp dt2) {
		if (dt2 == null) return 1;
		return checkDate(dt1, dt2.getDate());
	}

	protected void doFolder(Element element, Folder folder) {
		element.addAttribute("name", folder.getId().toString());
		element.addAttribute("title", folder.getTitle());

	}

	protected void doEntry(Element element, FolderEntry entry, Notify notifyDef, boolean hasChanges) {
		HistoryStamp stamp;
		if (hasChanges) {
			//style sheet will translate these tags
			element.addAttribute("hasChanges", "true");
			if (checkDate(entry.getCreation(), notifyDef.getStartDate()) > 0) {
				element.addAttribute("notifyType", "newEntry");
				stamp = entry.getCreation();
			} else if (checkDate(entry.getWorkflowChange(), entry.getModification()) > 0) {
				stamp = entry.getWorkflowChange();
				element.addAttribute("notifyType", "workflowEntry");
			} else {
				element.addAttribute("notifyType", "modifiedEntry");
				stamp = entry.getModification();
			} 
		} else {
			stamp = entry.getModification();				
			element.addAttribute("hasChanges", "false");
		}
		if (stamp == null) stamp = new HistoryStamp();
		Principal p = stamp.getPrincipal();
		String title = null;
		if (p != null) title = p.getTitle();
		if (Validator.isNull(title)) element.addAttribute("notifyBy",NLT.get("entry.noTitle", notifyDef.getLocale()));
		else element.addAttribute("notifyBy", title);
		
		Date date = stamp.getDate();
		if (date == null) element.addAttribute("notifyDate", "");
		else element.addAttribute("notifyDate", notifyDef.getDateFormat().format(date));

		element.addAttribute("name", entry.getId().toString());
		element.addAttribute("title", entry.getTitle());			    
		element.addAttribute("docNumber", entry.getDocNumber());			    
		element.addAttribute("docLevel", String.valueOf(entry.getDocLevel()));
		String entryUrl="";
		try {
			AdaptedPortletURL url = new AdaptedPortletURL("ss_forum", false);
			url.setParameter("action", "view_entry");
			url.setParameter(WebKeys.URL_BINDER_ID, entry.getTopFolder().getId().toString());
			url.setParameter(WebKeys.URL_ENTRY_ID, entry.getId().toString());
			entryUrl = url.toString();
		} catch (Exception e) {
			
		}
		element.addAttribute("href", entryUrl);
		definitionModule.addNotifyElementForEntry(element, notifyDef, entry);		
	}
	// get cached template.  If not cached yet,load it
	protected Transformer getTransformer(String zoneName, String type) throws TransformerConfigurationException {
		//convert mail templates into cached transformer temlates
		Templates trans;
		trans = (Templates)transformers.get(zoneName + ":" + type);
		if (trans == null) {
			String templateName = mailModule.getMailProperty(zoneName, type);
			Source xsltSource = new StreamSource(new File(DirPath.getXsltDirPath(),templateName));
			trans = transFactory.newTemplates(xsltSource);
			//replace name with actual template
			if (GetterUtil.getBoolean(mailModule.getMailProperty(zoneName, MailModule.NOTIFY_TEMPLATE_CACHE_DISABLED), false) == false)
				transformers.put(zoneName + ":" + type, trans);
		} 
		return trans.newTransformer();
	}

	protected String doTransform(Document document, String zoneName, String type, Locale locale) {
		StreamResult result = new StreamResult(new StringWriter());
		try {
			Transformer trans = getTransformer(zoneName, type);
			trans.setParameter("Lang", locale.toString());
			trans.transform(new DocumentSource(document), result);
		} catch (Exception ex) {
			return ex.getMessage();
		}
		return result.getWriter().toString();
	}

	public Map buildNotificationMessage(Folder folder, Collection entries,  Notify notify) {
	    Map result = new HashMap();
	    if (notify.getStartDate() == null) return result;
		Set seenIds = new TreeSet();
		Document mailDigest = DocumentHelper.createDocument();
		
    	Element rootElement = mailDigest.addElement("mail");
       	rootElement.addAttribute("summary", String.valueOf(notify.isSummary()));
		Element element;
		Folder lastFolder=null;
		Element fElement=null;
		ArrayList parentChain = new ArrayList();
		element = rootElement.addElement("topFolder");
		element.addAttribute("changeCount", String.valueOf(entries.size()));
      	element.addAttribute("title", folder.getTitle());
 		
		for (Iterator i=entries.iterator();i.hasNext();) {
			parentChain.clear();
			FolderEntry entry = (FolderEntry)i.next();	
			if (!entry.getParentFolder().equals(lastFolder)) {
				fElement = rootElement.addElement("folder");
				doFolder(fElement, entry.getParentFolder());
			}
			//make sure change of entries exist from topentry down to changed entry
			//since entries are sorted by sortKey, we should have processed an changed parents
			//already
			FolderEntry parent = entry.getParentEntry();
			while ((parent != null) && (!seenIds.contains(parent.getId()))) {
				parentChain.add(parent);
				parent=parent.getParentEntry();
			}
			for (int pos=parentChain.size()-1; pos>=0; --pos) {
				element = fElement.addElement("folderEntry");
				parent = (FolderEntry)parentChain.get(pos);
				doEntry(element, parent, notify, false);
				seenIds.add(parent.getId());
			}
					
			seenIds.add(entry.getId());
			element = fElement.addElement("folderEntry");
			doEntry(element, entry, notify, true);
		}
		
		
		result.put(FolderEmailFormatter.PLAIN, doTransform(mailDigest, folder.getZoneName(), MailModule.NOTIFY_TEMPLATE_TEXT, notify.getLocale()));
		result.put(FolderEmailFormatter.HTML, doTransform(mailDigest, folder.getZoneName(), MailModule.NOTIFY_TEMPLATE_HTML, notify.getLocale()));
		
		return result;
	}
	public Object[] validateIdList(Collection entries, Collection userIds) {
	   	Object[] result = new Object[1];
    	Object[] row = new Object[2];
    	result[0] = row;
    	row[0] = entries;
    	row[1] = userIds;
    	return result;
	}
	public String getSubject(Folder folder, Notify notify) {
		String subject = folder.getNotificationDef().getSubject();
		if (Validator.isNull(subject))
			subject = mailModule.getMailProperty(folder.getZoneName(), MailModule.NOTIFY_SUBJECT);
		//if not specified, us a localized default
		if (Validator.isNull(subject))
			return NLT.get("notify.subject", notify.getLocale()) + " " + folder.toString();
		return subject;
	}
	
	public String getFrom(Folder folder, Notify notify) {
		String from = folder.getNotificationDef().getFromAddress();
		if (Validator.isNull(from))
			from = mailModule.getMailProperty(folder.getZoneName(), MailModule.NOTIFY_FROM);
		return from;
	}
	public void postMessages(Folder folder, PostingDef pDef, Message[] msgs, Session session) {
		String type;
		Object content;
		Map fileItems = new HashMap();
		Map inputData = new HashMap();
		Definition definition = pDef.getDefinition();
		if (definition == null) definition = folder.getDefaultPostingDef();
		String [] from = new String[1];
		String [] title = new String[1];
		for (int i=0; i<msgs.length; ++i) {
			try {
				
				title[0] = msgs[i].getSubject();
				from[0] = msgs[i].getFrom()[0].toString();
				inputData.put("from", from); 
				inputData.put("title", title);
				type=msgs[i].getContentType().trim();
				content = msgs[i].getContent();
				if (type.startsWith("text/plain")) {
					processText(content, inputData);
				} else if (type.startsWith("text/html")) {
					processHTML(content, inputData);
				} else if (content instanceof MimeMultipart) {
					processMime((MimeMultipart)content, inputData, fileItems);
				}
				//msgs[i].setFlag(Flags.Flag.DELETED, true); // set the DELETED flag
				folderModule.addEntry(folder.getId(), definition.getId(), inputData, fileItems);
				fileItems.clear();
				inputData.clear();
			} catch (MessagingException me) {			
			} catch (IOException io) {}
		}
	}
	private void processText(Object content, Map inputData) {
		if (inputData.containsKey("description")) return;
		String[] val = new String[1];
		val[0] = (String)content;
		inputData.put("description", val);			
	}
	private void processHTML(Object content, Map inputData) {
		String[] val = new String[1];
		val[0] = (String)content;
		inputData.put("description", val);			
	}	
	private void processMime(MimeMultipart content, Map inputData, Map fileItems) throws MessagingException, IOException {
		int count = content.getCount();
		String cType = content.getContentType();
		for (int i=0; i<count; ++i ) {
			BodyPart part = content.getBodyPart(i);
			String type = part.getContentType();
			String disposition = part.getDisposition();
			if ((disposition != null) && (disposition.compareToIgnoreCase(Part.ATTACHMENT) == 0))
				fileItems.put("ss_attachFile" + Integer.toString(fileItems.size() + 1), new FileHandler(part));
			else if (part.isMimeType("text/html"))
				processHTML(part.getContent(), inputData);
			else if (part.isMimeType("text/plain"))
				processText(part.getContent(), inputData);
			else if (part instanceof MimeBodyPart) {
				Object bContent = ((MimeBodyPart)part).getContent();
				if (bContent instanceof MimeMultipart) processMime((MimeMultipart)bContent, inputData, fileItems);
			}
			
		}
	}
	public class FileHandler implements org.springframework.web.multipart.MultipartFile {
		BodyPart part;
		String fileName;
		String type;
		int size;
		
		public FileHandler(BodyPart part) throws MessagingException {
			this.part = part;
			fileName = part.getFileName();
			type = part.getContentType();
			size = part.getSize();
		}
		/**
		 * Return the name of the parameter in the multipart form.
		 * @return the name of the parameter
		 */
		public String getName() {return "attachment";}

		/**
		 * Return whether the uploaded file is empty in the sense that
		 * no file has been chosen in the multipart form.
		 * @return whether the uploaded file is empty
		 */
		public boolean isEmpty() {return false;}
		
		/**
		 * Return the original filename in the client's filesystem.
		 * This may contain path information depending on the browser used,
		 * but it typically will not with any other than Opera.
		 * @return the original filename, or null if empty
		 */
		public String getOriginalFilename() {return fileName;}
		
		
		/**
		 * Return the content type of the file.
		 * @return the content type, or null if empty or not defined
		 */
		public String getContentType() {return type;}

		/**
		 * Return the size of the file in bytes.
		 * @return the size of the file, or 0 if empty
		 */
		public long getSize() {return size;}
		
		/**
		 * Return the contents of the file as an array of bytes.
		 * @return the contents of the file as bytes,
		 * or an empty byte array if empty
		 * @throws IOException in case of access errors
		 * (if the temporary store fails)
		 */
		public byte[] getBytes() throws IOException {
			byte [] results = new byte[size];
			try {
				part.getInputStream().read(results);
			} catch (MessagingException me) {
				throw new IOException(me.getLocalizedMessage());
			}
			return results;
		}

		/**
		 * Return an InputStream to read the contents of the file from.
		 * The user is responsible for closing the stream.
		 * @return the contents of the file as stream,
		 * or an empty stream if empty
		 * @throws IOException in case of access errors
		 * (if the temporary store fails)
		 */
		public InputStream getInputStream() throws IOException {
			try {
				return part.getInputStream();
			} catch (MessagingException me) {
				throw new IOException(me.getLocalizedMessage());
			}
		}
		
		/**
		 * Transfer the received file to the given destination file.
		 * <p>This may either move the file in the filesystem, copy the file in the
		 * filesystem, or save memory-held contents to the destination file.
		 * If the destination file already exists, it will be deleted first.
		 * <p>If the file has been moved in the filesystem, this operation cannot
		 * be invoked again. Therefore, call this method just once to be able to
		 * work with any storage mechanism.
		 * @param dest the destination file
		 * @throws IOException in case of reading or writing errors
		 * @throws java.lang.IllegalStateException if the file has already been moved
		 * in the filesystem as is not available anymore for another transfer
		*/
		public void transferTo(File dest) throws IOException, IllegalStateException {
			//copied from org.springframework.web.multipart.commons.CommonsMultiPart
//			if (!isAvailable()) {
//				throw new IllegalStateException("File has already been moved - cannot be transferred again");
//			}

			if (dest.exists() && !dest.delete()) {
				throw new IOException(
						"Destination file [" + dest.getAbsolutePath() + "] already exists and could not be deleted");
			}

			try {
				
				FileOutputStream out = new FileOutputStream(dest);
				InputStream in = getInputStream();
				FileCopyUtils.copy(in, out);
/*				dest.this.fileItem.write(dest);
				if (logger.isDebugEnabled()) {
					String action = "transferred";
					if (!this.fileItem.isInMemory()) {
						action = isAvailable() ? "copied" : "moved";
					}
					logger.debug("Multipart file '" + getName() + "' with original filename [" +
							getOriginalFilename() + "], stored " + getStorageDescription() + ": " +
							action + " to [" + dest.getAbsolutePath() + "]");
				}
*/
				}
			catch (IOException ex) {
				throw ex;
			}
			catch (Exception ex) {
				logger.error("Could not transfer to file", ex);
				throw new IOException("Could not transfer to file: " + ex.getMessage());
			}
		}
	}
}
