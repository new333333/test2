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
package com.sitescape.team.module.rss.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;

import com.sitescape.team.domain.Binder;
import com.sitescape.team.domain.Entry;
import com.sitescape.team.domain.Folder;
import com.sitescape.team.domain.FolderEntry;
import com.sitescape.team.domain.User;
import com.sitescape.team.module.binder.AccessUtils;
import com.sitescape.team.module.impl.CommonDependencyInjection;
import com.sitescape.team.module.rss.RssModule;
import com.sitescape.team.module.rss.impl.RssModuleImplMBean;
import com.sitescape.team.module.shared.EntityIndexUtils;
import com.sitescape.team.util.ConfigPropertyNotFoundException;
import com.sitescape.team.util.Constants;
import com.sitescape.team.util.SPropsUtil;
import com.sitescape.team.util.SimpleProfiler;
import com.sitescape.team.util.XmlFileUtil;
import com.sitescape.team.web.WebKeys;
import com.sitescape.team.web.util.WebHelper;
import com.sitescape.team.web.util.WebUrlUtil;

public class RssModuleImpl extends CommonDependencyInjection implements RssModule, RssModuleImplMBean {

	//TODO MAXITEMS should be set in properties or via that application context file.
	private final int MAXITEMS = 20;
	private final long DAYMILLIS = 24L * 60L * 60L * 1000L;
	
	protected Log logger = LogFactory.getLog(getClass());
	
	private String rssRootDir;
	private long monthAgoTime = 31L * DAYMILLIS;
	
	private static final String emptyRssFileContent =
		"<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
		"<rss version=\"2.0\">" +
		"<channel>" +
	    "<title>Security Community of Practice</title>" +
	    "<link/>" +
	    "<description>Updates to the Security Community of Practice forum</description>" +
	    "<pubDate>" +
		new Date().toString() +
		"</pubDate>" +
	    "<ttl>60</ttl>" +
	    "<generator feedVersion=\"1.0\">SiteScape Forum</generator>" +
	    "</channel>" +
	    "</rss>";
	
	public RssModuleImpl() {
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
	
	public void generateRssFeed(Binder binder) {
		
		// See if the feed already exists
		String rssFileName = getRssFileName(binder);
		File rf = new File(rssFileName);
		if (rf.exists()) return;

		// Make sure the rss directory exists
		File rssdir = new File(rssRootDir);
		if (!rssdir.exists()) rssdir.mkdir();	
		
		Document doc = createEmptyRssDoc("Updates to the " + binder.getTitle() + " forum");
		
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
	
	public void updateRssFeed(Entry entry) {
		SimpleProfiler.startProfiler("RssModule.updateRssFeed");
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
		
		channelNode.add(this.createElementFromEntry(entry));
		
		writeRssFile(entry.getParentBinder(), doc);
		SimpleProfiler.stopProfiler("RssModule.updateRssFeed");
	}

	public String filterRss(HttpServletRequest request, HttpServletResponse response, Binder binder, User user) {
		if(user != null) { // Normal situation
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
			Set<Long> userAclSet = getProfileDao().getPrincipalIds(user);
			Set userStringSet = new HashSet();
			for (Long id:userAclSet) {
				userStringSet.add(id.toString());
			}
			// Walk thru the nodes with ACL's and find the ones this
			// user has read access to, and delete the rest.
			for (Iterator i = aclNodes.iterator(); i.hasNext();) {
				Element thisAcl = (Element)i.next();
				if (user.isSuper()) {
					// need to delete the acl before we send it to the client
					thisAcl.detach();
				} else {
					access = AccessUtils.checkAccess(thisAcl, userStringSet); 	       	
					if (!access) {
	 	       			thisAcl.getParent().detach();
	 	       		} else {
	 	       			// need to delete the acl before we send it to the client
	 	       			thisAcl.detach();
	 	       		}
				}
	        }
			
			//detach the age before sending the xml to the user
			List ageNodes = rssRoot.selectNodes("/rss/channel/item/age");
			for (Iterator i = ageNodes.iterator(); i.hasNext();) {
				Node thisAge = (Node)i.next();
				thisAge.detach();
			}
	
			// return the doc
			String results = doc.asXML();
			results = WebHelper.markupStringReplacement(null, null, 
					request, response, null, results, WebKeys.MARKUP_VIEW);
			return results;
		}
		else {
			// This request is being made without appropriate user authentication.
			// Do NOT use binder in this case, since it may be null in this situation.
			Document doc = createEmptyRssDoc("Updates to the Unknown forum");
			return doc.asXML();
		}
	}

	public String AuthError(HttpServletRequest request, HttpServletResponse response) {
		Document doc = createEmptyRssDoc("Authentication failure, please get a new URL for this feed.");
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
    
    public Element createElementFromEntry(Entry entry) 
    {
    	Element entryElement = DocumentHelper.createElement("item");
    	//Title needs to change for some readers to display it
    	entryElement.addElement("title")
    		.addText(entry.getTitle());// + NLT.get("rss.modified.date", new Object[]{entry.getModification().getDate()}));
    	entryElement.addElement("link")
    		.addText(WebUrlUtil.getEntryViewURL((FolderEntry)entry));//ROY
    	String description = entry.getDescription() == null ? "" : entry.getDescription().getText();
    	description = WebHelper.markupStringReplacement(null, null, 
				null, null, entry, description, WebKeys.MARKUP_FILE);
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

    	Element acl = entryElement.addElement("sitescapeAcl");
    	//add same acls as search engine uses
    	EntityIndexUtils.addReadAccess(acl, entry.getParentBinder(), entry);
    	return entryElement;
    	
    }
    
	private Document createEmptyRssDoc(String title) {
		// First create our top-level document
		Document doc = DocumentHelper.createDocument();
		Element root = doc.addElement("rss");

		// Set RSS version number
		root.addAttribute("version", "2.0");
		
	    Element channel = root.addElement("channel");
	    
	    channel.addElement("title")
	    	.addText(title);
	    channel.addElement("link")
	    	.addText("" /*this.getRssLink(binder)*/);
	    channel.addElement("description")
	    	.addText(title);
	    channel.addElement("pubDate")
	    	.addText(new Date().toString());
	    channel.addElement("ttl")
	    	.addText("60");
	    channel.addElement("generator")
	    	.addAttribute("feedVersion", "1.0")
	    	.addText("SiteScape Forum");
	    
	    return doc;
	}
	
}
