package com.sitescape.ef.rss;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.ParseException;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.document.DateTools;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;

import com.sitescape.ef.dao.ProfileDao;
import com.sitescape.ef.domain.Binder;
import com.sitescape.ef.domain.Entry;
import com.sitescape.ef.domain.Folder;
import com.sitescape.ef.domain.FolderEntry;
import com.sitescape.ef.domain.User;
import com.sitescape.ef.module.impl.CommonDependencyInjection;
import com.sitescape.team.util.ConfigPropertyNotFoundException;
import com.sitescape.team.util.Constants;
import com.sitescape.team.util.NLT;
import com.sitescape.team.util.SPropsUtil;
import com.sitescape.team.util.XmlFileUtil;
import com.sitescape.team.web.util.WebUrlUtil;

public class RssGenerator extends CommonDependencyInjection {

	//TODO MAXITEMS should be set in properties or via that application context file.
	private final int MAXITEMS = 20;
	private final long DAYMILLIS = 24L * 60L * 60L * 1000L;
	
	protected Log logger = LogFactory.getLog(getClass());
	protected ProfileDao profileDao;
	
	private String rssRootDir;
	private long monthAgoTime = 31L * DAYMILLIS;
	
	public RssGenerator() {
		int maxDays = 31;

		try {
			maxDays = SPropsUtil.getInt("rss.max.elapseddays");
		} catch (ConfigPropertyNotFoundException e) {
		}

		monthAgoTime = new Date().getTime() - (maxDays * DAYMILLIS);
	}
	
	public String getRssRootDir() {
		return rssRootDir;
	}

	public void setRssRootDir(String rssRootDir) {
		if(rssRootDir.endsWith(Constants.SLASH))
			this.rssRootDir = rssRootDir;
		else
			this.rssRootDir = rssRootDir + Constants.SLASH;
	}

	public void setProfileDao(ProfileDao profileDao) {
		this.profileDao = profileDao;
	}
	protected ProfileDao getProfileDao() {
		return profileDao;
	}
	public void generateRssFeed(Binder binder) {
		
		// See if the feed already exists
		String rssFileName = getRssFileName(binder);
		File rf = new File(rssFileName);
		if (rf.exists()) return;

		// Make sure the rss directory exists
		File rssdir = new File(rssRootDir);
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
		Long id = binder.getId();
		if (binder instanceof Folder) {
			Folder f = (Folder)binder;
			if (!f.isTop()) id = f.getTopFolder().getId();
		}
		String rssFileName = rssRootDir + id + ".xml";
		return rssFileName;
	}
	
	public void writeRssFile(Binder binder, Document doc)
	{
		String rssFileName = getRssFileName(binder);
		try {
			XmlFileUtil.writeFile(doc, rssFileName);
		} catch (Exception ioe) {logger.error("Can't write RSS file for binder:" + binder.getName() + "error is: " + ioe.toString());}
		
	}
	
	public void deleteRssFile(Binder binder) {
		
		// See if the feed exists
		String rssFileName = getRssFileName(binder);
		File rf = new File(rssFileName);
		if (rf.exists()) rf.delete();
	}
	
	
	public void trimItems(Element rssRoot) {

		// trim based on elapsed time since the entry has been modified.

		// Get the list of nodes with ages set
		List ageNodes = rssRoot.selectNodes("/rss/channel/item/age");

		// Walk thru the nodes, see if any of the items are older
		// than the age requirement
		for (Iterator i = ageNodes.iterator(); i.hasNext();) {
			Node thisAge = (Node) i.next();
			long itemAge = Long.parseLong(thisAge.getText());
			if (itemAge < monthAgoTime) {
				thisAge.getParent().detach();
			}

		}
	}
	
	public void updateRssFeed(Entry entry, Set ids) {
		// See if the feed already exists
		String rssFileName = getRssFileName(entry.getParentBinder());
		File rf = new File(rssFileName);
		if (!rf.exists())
			this.generateRssFeed(entry.getParentBinder());
	    
		Document doc = this.parseFile(this.getRssFileName(entry.getParentBinder()));
		Element rssRoot = doc.getRootElement();
		Node channelPubDate = (Node)rssRoot.selectSingleNode("/rss/channel/pubDate");
		channelPubDate.setText(entry.getModification().getDate().toString());
		trimItems(rssRoot);
		Element channelNode = (Element)rssRoot.selectSingleNode("/rss/channel");
		
		// see if the current entry is already in the channel, if it is, update it.
		Node entryNode = channelNode.selectSingleNode("item/guid[.='" + entry.getId() + "']");
		if (entryNode != null)
			entryNode.detach();
		
		channelNode.add(this.createElementFromEntry(entry, ids));
		
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
		Set userAclSet = getProfileDao().getPrincipalIds(user);
		// Walk thru the nodes with ACL's and find the ones this
		// user has read access to, and delete the rest.
		for (Iterator i = aclNodes.iterator(); i.hasNext();) {
			access = false;
			Node thisAcl = (Node)i.next();
 	       	String[] acls = thisAcl.getStringValue().split(" ");
 	       	for (int j = 0; j < acls.length; j++) {
 	       		if (userAclSet.contains(new Long(acls[j]))) {
 	       			access = true;
 	       			break;
 	       		}
 	       	}
 	       	
 	       	if (!access) {
 	       		thisAcl.getParent().detach();
 	       	} else {
 	       		// need to delete the acl before we send it to the client
 	       		thisAcl.detach();
 	       	}
        }
		
		//detach the age before sending the xml to the user
		List ageNodes = rssRoot.selectNodes("/rss/channel/item/age");
		for (Iterator i = ageNodes.iterator(); i.hasNext();) {
			Node thisAge = (Node)i.next();
			thisAge.detach();
		}

		// return the doc
		return doc.asXML();
	}

    public Document parseFile(String rssFileName) {
    	Document document = null;
        try {
        	document = XmlFileUtil.readFile(rssFileName);
        } catch (DocumentException de) {logger.error("RSS Error: Can't read RSS file" + rssFileName);
        } catch (FileNotFoundException fn) {logger.error("RSS Error: File not found" + rssFileName);
        } catch (Exception e) {}; //already reported}
        return document;
    }
    
    public Element createElementFromEntry(Entry entry, Set ids) 
    {
    	Element entryElement = DocumentHelper.createElement("item");
    	//Title needs to change for some readers to display it
    	entryElement.addElement("title")
    		.addText(entry.getTitle());// + NLT.get("rss.modified.date", new Object[]{entry.getModification().getDate()}));
    	entryElement.addElement("link")
    		.addText(WebUrlUtil.getEntryViewURL((FolderEntry)entry));//ROY
    	String description = entry.getDescription() == null ? "" : entry.getDescription().getText();
    	Date eDate = entry.getModification().getDate();
    	
    	entryElement.addElement("description")
    		.addText(description);
    	entryElement.addElement("author")
    		.addText(entry.getCreation().getPrincipal().getName());
    	entryElement.addElement("pubDate")
			.addText(eDate.toString());
    	entryElement.addElement("age")
    		.addText(new Long(eDate.getTime()).toString());
    	entryElement.addElement("guid")
    		.addAttribute("isPermaLink", "false")
    		.addText(entry.getId().toString());
    	if (ids != null) {
    		//enumerate the acls
    		StringBuffer pIds = new StringBuffer();
       		for (Iterator i = ids.iterator(); i.hasNext();) {
        		pIds.append(i.next()).append(" ");
        	}
    		entryElement.addElement("sitescapeAcl")
    			.addText(pIds.toString());
    	}
    	return entryElement;
    	
    }
}
