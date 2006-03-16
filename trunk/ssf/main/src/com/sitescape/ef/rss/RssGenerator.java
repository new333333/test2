package com.sitescape.ef.rss;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

import com.sitescape.ef.domain.Binder;
import com.sitescape.ef.domain.Entry;
import com.sitescape.ef.domain.FolderEntry;
import com.sitescape.ef.domain.User;
import com.sitescape.ef.domain.WorkflowControlledEntry;
import com.sitescape.ef.module.impl.CommonDependencyInjection;
import com.sitescape.ef.security.acl.AccessType;
import com.sitescape.ef.security.acl.AclSet;
import com.sitescape.ef.util.DirPath;
import com.sitescape.ef.web.util.WebUrlUtil;


public class RssGenerator extends CommonDependencyInjection {

	protected Log logger = LogFactory.getLog(getClass());
	
	public void generateRssFeed(Binder binder) {
		
		// See if the feed already exists
		String rssFileName = getRssFileName(binder);
		File rf = new File(rssFileName);
		if (rf.exists()) return;

		// Make sure the rss directory exists
		File rssdir = new File(DirPath.getRssDirPath());
		if (!rssdir.exists()) rssdir.mkdir();	
		
		// First create our top-level document
		Document doc = DocumentHelper.createDocument();
		Element root = doc.addElement("rss");

		// Set RSS version number
		root.addAttribute("version", "2.0");
		
		String binderTitle = binder.getTitle();
		
	    Element channel = root.addElement("channel");
	    
	    channel.addElement("title")
	    	.addText(binderTitle);
	    channel.addElement("link")
	    	.addText("" /*this.getRssLink(binder)*/);
	    channel.addElement("description")
	    	.addText("Updates to the " + binderTitle + " forum");
	    channel.addElement("pubDate")
	    	.addText(new Date().toString());
	    channel.addElement("ttl")
	    	.addText("60");
	    channel.addElement("generator")
	    	.addAttribute("feedVersion", "1.0")
	    	.addText("SiteScape Forum");
	    
	    writeRssFile(binder, doc);
	}
	
	public String getRssFileName(Binder binder) 
	{
		String path = DirPath.getRssDirPath();
		String rssFileName = path + File.separator + binder.getId() + ".xml";
		return rssFileName;
	}

	public void writeRssFile(Binder binder, Document doc)
	{
		String rssFileName = getRssFileName(binder);
		try {
	    	FileWriter out = new FileWriter(rssFileName);
	    	doc.write( out );
	    	out.close();
	    } catch (IOException ioe) {logger.error("Can't write RSS file for binder:" + binder.getName() + "error is: " + ioe.toString());}
		
	}
	
	public void deleteRssFile(Binder binder) {
		
		// See if the feed exists
		String rssFileName = getRssFileName(binder);
		File rf = new File(rssFileName);
		if (rf.exists()) rf.delete();
	}
	
	public void updateRssFeed(WorkflowControlledEntry entry) {
		// See if the feed already exists
		String rssFileName = getRssFileName(entry.getParentBinder());
		File rf = new File(rssFileName);
		if (!rf.exists())
			this.generateRssFeed(entry.getParentBinder());
	    
		Document doc = this.parseFile(this.getRssFileName(entry.getParentBinder()));
		Element rssRoot = doc.getRootElement();
		Node channelPubDate = (Node)rssRoot.selectSingleNode("/rss/channel/pubDate");
		channelPubDate.setText(new Date().toString());
		
		Element channelNode = (Element)rssRoot.selectSingleNode("/rss/channel");
		
		// see if the current entry is already in the channel, if it is, update it.
		Node entryNode = channelNode.selectSingleNode("item/guid[.='" + entry.getId() + "']");
		if (entryNode != null)
			entryNode.detach();
		
		channelNode.add(this.createElementFromEntry(entry));
		
		writeRssFile(entry.getParentBinder(), doc);
	}

	public String filterRss(Binder binder, User user) {
		// See if the feed already exists
		boolean access = false;
		String rssFileName = getRssFileName(binder);
		File rf = new File(rssFileName);
		if (!rf.exists())
			this.generateRssFeed(binder);
	    
		Document doc = this.parseFile(this.getRssFileName(binder));
		Element rssRoot = doc.getRootElement();
		// Get the list of nodes with acls set
		List aclNodes = rssRoot.selectNodes("/rss/channel/item/sitescapeAcl");

		// get the current users acl set
		Set userAclSet = user.getAclSet().getMemberIds(AccessType.READ);
        
		// Walk thru the nodes with ACL's and find the ones this
		// user has read access to, and delete the rest.
		for (Iterator i = aclNodes.iterator(); i.hasNext();) {
			access = false;
			Node thisAcl = (Node)i.next();
 	       	String[] acls = thisAcl.getStringValue().split(" ");
 	       	for (int j = 0; j < acls.length; j++) {
 	       		if (userAclSet.contains(acls[j])) access = true;
 	       	}
 	       	
 	       	if (!access) {
 	       		thisAcl.getParent().detach();
 	       	} else {
 	       		// need to delete the acl before we send it to the client
 	       		thisAcl.detach();
 	       	}
        }

		// return the doc
		return doc.asXML();
	}

    public Document parseFile(String rssFileName) {
    	Document document = null;
        SAXReader reader = new SAXReader();
        try {
        	document = reader.read(rssFileName);
        } catch (DocumentException de) {logger.error("RSS Error: Can't read RSS file" + rssFileName);}
        return document;
    }
    
    public Element createElementFromEntry(WorkflowControlledEntry entry) 
    {
    	Element entryElement = DocumentHelper.createElement("item");
    	entryElement.addElement("title")
    		.addText(entry.getTitle());
    	entryElement.addElement("link")
    		.addText(WebUrlUtil.getEntryViewURL((FolderEntry)entry));//ROY
    	String description = entry.getDescription() == null ? "" : entry.getDescription().getText();
    	
    	entryElement.addElement("description")
    		.addText(description);
    	entryElement.addElement("author")
    		.addText(entry.getCreation().getPrincipal().getName());
    	entryElement.addElement("pubDate")
    		.addText(new Date().toString());
    	entryElement.addElement("guid")
    		.addAttribute("isPermaLink", "false")
    		.addText(entry.getId().toString());
    	if (entry.hasAclSet()) {
    		Set ids = new HashSet();
    		ids.addAll(entry.getAclSet().getMemberIds(AccessType.READ));
    		//enumerate the acls
    		StringBuffer pIds = new StringBuffer();
       		for (Iterator i = ids.iterator(); i.hasNext();) {
        		pIds.append(i.next()).append(" ");
        	}
    		entryElement.addElement("sitesscapeAcl")
    			.addText(pIds.toString());
    	}
    	return entryElement;
    	
    }
}
