
package com.sitescape.ef.module.mail.impl;

import java.util.Collection;
import java.util.Iterator;
import java.util.HashMap;
import java.util.Map;
import java.util.Date;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;

import org.dom4j.Document;
import org.dom4j.Element;

import com.sitescape.ef.domain.FolderEntry;
import com.sitescape.ef.domain.Folder;
import com.sitescape.ef.dao.util.OrderBy;
import com.sitescape.ef.module.mail.FolderEmailFormatter;
import com.sitescape.ef.security.AccessControlManager;
import com.sitescape.ef.security.acl.AclManager;
import com.sitescape.ef.util.NLT;
import com.sitescape.ef.util.XmlClassPathConfigFiles;
import com.sitescape.util.Validator;
/**
 * @author Janet McCann
 *
 */
public class DefaultFolderEmailFormatter implements FolderEmailFormatter {
	private String startSpan="<span style=\"font-family: arial, helvetica, sans-serif; font-size: 13px;\">";
	private String endSpan="</span>";
    protected AccessControlManager accessControlManager;
    protected AclManager aclManager;
	protected XmlClassPathConfigFiles configDocs;
	private Map zoneProps = new HashMap();

    public DefaultFolderEmailFormatter () {
	}
    public void setAccessControlManager(
            AccessControlManager accessControlManager) {
        this.accessControlManager = accessControlManager;
    }
    public void setAclManager(AclManager aclManager) {
        this.aclManager = aclManager;
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
		for (Iterator i=root.elements("zone").iterator(); i.hasNext();) {
			z = (Element)i.next();
			HashMap mProps = new HashMap();			
			zoneProps.put(z.attributeValue("name"), mProps);
			for (Iterator j=z.elements("property").iterator(); j.hasNext();) {
				p = (Element)j.next();
				mProps.put(p.attributeValue("name"),p.getText());
			};
		}
		
	
	}	
	/**
	 * Only supports lookups from topFolder ie)per forum
	 */
	public OrderBy getLookupOrder(Folder folder) {
		return new OrderBy("HKey.sortKey");
	}
	private int checkDate(Date dt1, Date dt2) {
		if (dt1 == null) return -1;
		return dt1.compareTo(dt2);
	}

	protected String doTocEntry(FolderEntry entry, Locale locale) {
		String title = entry.getTitle();
	    if (Validator.isNull(title))
    		title = NLT.get("entry.noTitle", locale);
		
		return "<a href=\"#id" + entry.getId() + "\">" + startSpan + 
			    		entry.getDocNumber() + " " + title + endSpan + "</a>";
	}
	protected void doFolder(Folder folder, Locale locale, StringBuffer plain, StringBuffer html) {
		Folder forum = folder.getTopFolder();
		if (forum == null) forum = folder;
		String label = NLT.get("notify.forumLabel", locale);
		String title = forum.getTitle();
	    if (Validator.isNull(title)) title = forum.toString();
		
		plain.append(label + " " + title + "\n");
		html.append(startSpan + "<b>" + label + "&nbsp;" + title + "</b><br/>");
				
		if (!folder.equals(forum)) {
			label = NLT.get("notify.folderLabel", locale);
			title = folder.getTitle();
			if (Validator.isNull(title)) title = folder.toString();
			plain.append(" " + label + " " + title + "\n");
			html.append("&nbsp;" + startSpan + "<b>" + label + "&nbsp;" + title + "</b><br/>");
			
		}

	}
	protected void doEntry(FolderEntry entry, Locale locale, StringBuffer plain, StringBuffer html, String typeTag) {
		String title = entry.getTitle();
		String label = NLT.get("notify.entryLabel", locale);
		if (Validator.isNull(entry.getTitle())) title = NLT.get("entry.noTitle", locale);
		plain.append(label + " " +  entry.getDocNumber() + " " + title + typeTag + "\n");
		html.append(label + "&nbsp;<b>" + entry.getDocNumber() + "&nbsp;" + title + "</b>&nbsp;" + typeTag + "<br/>");

	}
	public Map buildNotificationMessage(Folder folder, Collection entries,  Locale locale) {
	    Map result = new HashMap();
	    Date notifyDate = folder.getNotificationDef().getLastNotification();
	    if (notifyDate == null) return result;
	    StringBuffer plain = new StringBuffer();
		StringBuffer html = new StringBuffer();
		StringBuffer toc = new StringBuffer();
		String typeTag;
		Set seenIds = new TreeSet();
		Folder lastFolder=null;
		for (Iterator i=entries.iterator();i.hasNext();) {
			FolderEntry entry = (FolderEntry)i.next();	
			FolderEntry parent = entry.getTopEntry();
			//TODO: add workflow check
			if (checkDate(entry.getCreation().getDate(), notifyDate) > 0) {
				typeTag = NLT.get("notify.newEntry", locale);
			} else {
				typeTag = NLT.get("notify.modifiedEntry", locale);
			}
			if (parent != null) {
				Long id = parent.getId();
				if (!seenIds.contains(id)) {
					toc.append(doTocEntry(parent, locale));
					toc.append("<br/>");
				    seenIds.add(id);
				}
				toc.append("&nbsp;&nbsp;&nbsp;");
			}		
			toc.append(doTocEntry(entry, locale));
			toc.append("&nbsp;" + typeTag + "<br/>");
			seenIds.add(entry.getId());
			plain.append("___________________________________________________________\n");
			html.append("<hr size=\"1\" color=\"black\" noshade>");
			if (!entry.getParentFolder().equals(lastFolder)) {
				doFolder(entry.getParentFolder(), locale, plain, html);
			}
			doEntry(entry, locale, plain, html, typeTag);
		}
		result.put(FolderEmailFormatter.PLAIN, plain.toString());
		result.put(FolderEmailFormatter.HTML, toc.toString() + html.toString());
		
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
	public String getSubject(Folder folder, Locale locale) {
		return NLT.get("notify.subject", locale) + folder.toString();
	}
	
	public String getFrom(Folder folder) {
		String zoneName = folder.getZoneName();
		Map zoneP = (Map)zoneProps.get(zoneName);
		if (zoneP != null) {
			String from = (String)zoneP.get("from");
			if (from != null) return from;
		}
		zoneP = (Map)zoneProps.get("");
		if (zoneP != null) {
			String from = (String)zoneP.get("from");
			if (from != null) return from;
		}
		return null;
	}
}
