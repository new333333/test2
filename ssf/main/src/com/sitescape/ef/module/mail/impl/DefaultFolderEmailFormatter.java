
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
import java.io.StringWriter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.DocumentSource;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.mail.Session;
import javax.mail.Flags;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.Templates;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import com.sitescape.ef.ConfigurationException;
import com.sitescape.ef.domain.FolderEntry;
import com.sitescape.ef.domain.Folder;
import com.sitescape.ef.domain.PostingDef;
import com.sitescape.ef.dao.util.OrderBy;
import com.sitescape.ef.module.mail.FolderEmailFormatter;
import com.sitescape.ef.security.AccessControlManager;
import com.sitescape.ef.security.acl.AclManager;
import com.sitescape.ef.util.NLT;
import com.sitescape.ef.util.XmlClassPathConfigFiles;
import com.sitescape.ef.portletadapter.AdaptedPortletURL;
import com.sitescape.ef.web.WebKeys;
import com.sitescape.ef.util.SpringContextUtil;
import com.sitescape.util.Validator;
import com.sitescape.ef.domain.Principal;
import com.sitescape.ef.domain.HistoryStamp;
import com.sitescape.util.GetterUtil;
import com.sitescape.ef.module.definition.DefinitionModule;
import com.sitescape.ef.module.definition.notify.Notify;
/**
 * @author Janet McCann
 *
 */
public class DefaultFolderEmailFormatter implements FolderEmailFormatter {
	private Log logger = LogFactory.getLog(getClass());
    protected AccessControlManager accessControlManager;
    protected AclManager aclManager;
    protected DefinitionModule definitionModule;
    protected XmlClassPathConfigFiles configDocs;
	private Map zoneProps = new HashMap();
	private TransformerFactory transFactory;
	public static final String NOTIFY_TEMPLATE_TEXT="notify.mailText";
	public static final String NOTIFY_TEMPLATE_HTML="notify.mailHtml";
	public static final String NOTIFY_TEMPLATE_CACHE_DISABLED="notify.templateCacheDisabled";
	public static final String NOTIFY_FROM="notify.from";
	public static final String NOTIFY_SUBJECT="notify.subject";
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
	public void setConfigDocs(XmlClassPathConfigFiles configDocs) {
		this.configDocs = configDocs;
	}
	public XmlClassPathConfigFiles getConfigDocs() {
		return configDocs;
	}
	//called after bean properties are initialized
	public void init() {
		Document doc = configDocs.getAsDom4jDocument(0);
		Element root = doc.getRootElement();
		Element z,p;
		//load mail style sheets
		transFactory = TransformerFactory.newInstance();
		//setup default zone config.
		HashMap defaultProps = new HashMap();			
		defaultProps.put("name", "");
		defaultProps.put(NOTIFY_TEMPLATE_TEXT, "mailText.xslt");
		defaultProps.put(NOTIFY_TEMPLATE_HTML, "mailHTML.xslt");
		defaultProps.put(NOTIFY_TEMPLATE_CACHE_DISABLED, "true");
		zoneProps.put("", defaultProps);
		//load zone properties.  A null zone name is the default for all zones.
		for (Iterator i=root.elements("zone").iterator(); i.hasNext();) {
			z = (Element)i.next();
			String zoneName = z.attributeValue("name");
			HashMap mProps = (HashMap)zoneProps.get(zoneName);
			if (mProps == null) {
				mProps = new HashMap();
				zoneProps.put(zoneName, mProps);
			}
			
			for (Iterator j=z.elements("property").iterator(); j.hasNext();) {
				p = (Element)j.next();
				mProps.put(p.attributeValue("name"),p.getText());
			};
		}
		
	
	}	
	/**
	 * Load property for zone.  If not specified, return default	 
	 */ 
	protected Object getProperty(String zoneName, String name) {
		HashMap mProps = (HashMap)zoneProps.get(zoneName);
		Object obj=null;
		//first look for zone specific entries
		if (mProps != null)
			obj  = mProps.get(name);
		//pick up default if not found
		if (obj == null) {
			mProps = (HashMap)zoneProps.get("");
			if (mProps != null)
				obj  = mProps.get(name);
		}
		return obj;	
		
	}
	/**
	 * Set a property.  Will overwrite value if already exists
	 * @param zoneName
	 * @param name
	 * @param value
	 */
	protected void setProperty(String zoneName, String name, Object value) {
		HashMap zProps = (HashMap)zoneProps.get(zoneName);
		if (zProps == null) {
			zProps = new HashMap();
			zoneProps.put(zoneName, zProps);
		}
		zProps.put(name, value);
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

	protected void doEntry(Element element, FolderEntry entry, Date notifyDate, Notify notifyDef, boolean hasChanges) {
		HistoryStamp stamp;
		if (hasChanges) {
			//style sheet will translate these tags
			element.addAttribute("hasChanges", "true");
			if (checkDate(entry.getCreation(), notifyDate) > 0) {
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
			url.setParameter(WebKeys.FORUM_URL_FORUM_ID, entry.getTopFolder().getId().toString());
			url.setParameter(WebKeys.FORUM_URL_ENTRY_ID, entry.getId().toString());
			entryUrl = url.toString();
		} catch (Exception e) {
			
		}
		element.addAttribute("href", entryUrl);
		definitionModule.addNotifyElementForEntry(element, notifyDef, entry);		
	}
	// get cached template.  If not cached yet,load it
	protected Transformer getTransformer(String zoneName, String type) throws TransformerConfigurationException {
		//convert mail templates into cached transformer temlates
		Object obj = getProperty(zoneName, type);
		Templates trans;
		if (obj == null)
			throw new ConfigurationException("Missing mail ("+ type + ") stylesheet");
		if (obj instanceof String) {
			String templateName = (String)obj;
			Source xsltSource = new StreamSource(new File(SpringContextUtil.getWebRootName(),templateName));
			trans = transFactory.newTemplates(xsltSource);
			//replace name with actual template
			if (GetterUtil.getBoolean((String)getProperty(zoneName, NOTIFY_TEMPLATE_CACHE_DISABLED), false) == false)
				setProperty(zoneName, type, trans);
		} else {
			trans = (Templates)obj;
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
	    Date notifyDate = folder.getNotificationDef().getLastNotification();
	    if (notifyDate == null) return result;
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
				doEntry(element, parent, notifyDate, notify, false);
				seenIds.add(parent.getId());
			}
					
			seenIds.add(entry.getId());
			element = fElement.addElement("folderEntry");
			doEntry(element, entry, notifyDate, notify, true);
		}
		
		
		result.put(FolderEmailFormatter.PLAIN, doTransform(mailDigest, folder.getZoneName(), NOTIFY_TEMPLATE_TEXT, notify.getLocale()));
		result.put(FolderEmailFormatter.HTML, doTransform(mailDigest, folder.getZoneName(), NOTIFY_TEMPLATE_HTML, notify.getLocale()));
		
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
			subject = (String)getProperty(folder.getZoneName(), NOTIFY_SUBJECT);
		//if not specified, us a localized default
		if (Validator.isNull(subject))
			return NLT.get("notify.subject", notify.getLocale()) + " " + folder.toString();
		return subject;
	}
	
	public String getFrom(Folder folder, Notify notify) {
		String from = folder.getNotificationDef().getFromAddress();
		if (Validator.isNull(from))
			from = (String)getProperty(folder.getZoneName(), NOTIFY_FROM);
		return from;
	}
	public void postMessages(Folder folder, Message[] msgs, Session session) {
		PostingDef pDef = folder.getPostingDef();
		String subject,from;
		for (int i=0; i<msgs.length; ++i) {
			try {
				subject = msgs[i].getSubject();
				from = msgs[i].getFrom().toString();
				msgs[i].setFlag(Flags.Flag.DELETED, true); // set the DELETED flag

				
			} catch (MessagingException me) {
				
				
			}
		}
	}
}
