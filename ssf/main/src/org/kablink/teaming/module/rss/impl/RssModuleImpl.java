/**
 * The contents of this file are subject to the Common Public Attribution License Version 1.0 (the "CPAL");
 * you may not use this file except in compliance with the CPAL. You may obtain a copy of the CPAL at
 * http://www.opensource.org/licenses/cpal_1.0. The CPAL is based on the Mozilla Public License Version 1.1
 * but Sections 14 and 15 have been added to cover use of software over a computer network and provide for
 * limited attribution for the Original Developer. In addition, Exhibit A has been modified to be
 * consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT WARRANTY OF ANY KIND,
 * either express or implied. See the CPAL for the specific language governing rights and limitations
 * under the CPAL.
 * 
 * The Original Code is ICEcore. The Original Developer is SiteScape, Inc. All portions of the code
 * written by SiteScape, Inc. are Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * 
 * 
 * Attribution Information
 * Attribution Copyright Notice: Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by ICEcore]
 * Attribution URL: [www.icecore.com]
 * Graphic Image as provided in the Covered Code [web/docroot/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are defined in the CPAL as a
 * work which combines Covered Code or portions thereof with code not governed by the terms of the CPAL.
 * 
 * 
 * SITESCAPE and the SiteScape logo are registered trademarks and ICEcore and the ICEcore logos
 * are trademarks of SiteScape, Inc.
 */
package org.kablink.teaming.module.rss.impl;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Collection;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.kablink.teaming.dao.util.OrderBy;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.Entry;
import org.kablink.teaming.domain.Folder;
import org.kablink.teaming.domain.FolderEntry;
import org.kablink.teaming.lucene.SsfIndexAnalyzer;
import org.kablink.teaming.lucene.SsfQueryAnalyzer;
import org.kablink.teaming.module.impl.CommonDependencyInjection;
import org.kablink.teaming.module.rss.RssModule;
import org.kablink.teaming.module.shared.EntityIndexUtils;
import org.kablink.teaming.search.QueryBuilder;
import org.kablink.teaming.search.SearchObject;
import org.kablink.teaming.util.Constants;
import org.kablink.teaming.util.FileHelper;
import org.kablink.teaming.util.FilePathUtil;
import org.kablink.teaming.util.NLT;
import org.kablink.teaming.util.SPropsUtil;
import org.kablink.teaming.util.SimpleProfiler;
import org.kablink.teaming.web.WebKeys;
import org.kablink.teaming.web.util.MarkupUtil;
import org.kablink.teaming.web.util.PermaLinkUtil;
import org.kablink.teaming.web.util.WebUrlUtil;
import org.kablink.util.PropertyNotFoundException;
import org.kablink.util.LockFile;

public class RssModuleImpl extends CommonDependencyInjection implements
		RssModule, RssModuleImplMBean {

	private static QueryParser qp = new QueryParser("guid",
			new SsfQueryAnalyzer());

	private final String ALL_FIELD = "allField";

	private final long DAYMILLIS = 24L * 60L * 60L * 1000L;

	protected Log logger = LogFactory.getLog(getClass());

	private String rssRootDir;

	int maxDays = 31;

	int maxInactiveDays = 7;
	SimpleDateFormat rssFmt = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z");
	SimpleDateFormat atomFmt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

	private static final String SCHEME_HOST_PORT_PATTERN = "<link>http.*/ssf/a/";

	private Pattern pattern = Pattern.compile(SCHEME_HOST_PORT_PATTERN);

	public RssModuleImpl() {

		try {
			maxDays = SPropsUtil.getInt("rss.max.elapseddays");
		} catch (PropertyNotFoundException e) {
		}
		try {
			maxInactiveDays = SPropsUtil.getInt("rss.max.inactivedays");
		} catch (PropertyNotFoundException e) {
		}

	}

	public String getRssRootDir() {
		return rssRootDir;
	}

	public void setRssRootDir(String rssRootDir) {
		if (rssRootDir.endsWith(Constants.SLASH))
			this.rssRootDir = rssRootDir;
		else
			this.rssRootDir = rssRootDir + Constants.SLASH;
	}
	//directory path for this binder
	private String getRssPath(Binder binder) {
		if (binder instanceof Folder) {
			Folder f = (Folder) binder;
			if (!f.isTop())
				binder = f.getTopFolder();
		}
		return rssRootDir + FilePathUtil.getBinderDirPath(binder);
		
	}
	//path of actual lucene index
	private File getRssIndexPath(Binder binder) {
		return new File(getRssPath(binder) + "index" + File.separatorChar);
	}
	//index file is 1 level up from lucene
	private File getRssIndexLockFile(Binder binder) {
		return new File(getRssPath(binder), "lockfile");
	}
	private File getRssIndexActivityFile(Binder binder) {
		return new File(getRssPath(binder), "timestampfile");
	}
	
	public synchronized void deleteRssFeed(Binder binder) {
		LockFile rfl = new LockFile(getRssIndexLockFile(binder));
		try {
			if (!rfl.getLock()) {
				logger.info("Couldn't get the RssFeedLock");
			}
			deleteFeed(binder);
		} finally {
			rfl.releaseLock();
		}
		
	}
	public synchronized void deleteFeed(Binder binder) {

		// See if the feed exists
		File rf = new File(getRssPath(binder));
		
		File indexPath = getRssIndexPath(binder);
		
		if (!indexPath.exists()) return;
		
		try {
			if (indexPath.exists())
				FileHelper.deleteRecursively(indexPath);
		} catch (Exception e) {
			logger.info("Rss module error: " + e.toString());
		} 
		if (rf.exists())
			FileHelper.deleteRecursively(rf);
	}

	public synchronized void deleteRssFeed(Binder binder, Collection<Entry> entries) {
		File indexPath = getRssIndexPath(binder);

		// See if the feed already exists
		if (!indexPath.exists())
			return; // if it doesn't already exist, then don't update it.

		LockFile rfl = new LockFile(getRssIndexLockFile(binder));
		try {
			if (!rfl.getLock()) {
				logger.info("Couldn't get the RssFeedLock");
			}
			IndexReader indexReader = IndexReader.open(indexPath);
			// see if the current entry is already in the index, if it is,
			// delete it. 
			for (Entry e: entries) {
				indexReader.deleteDocuments(new Term("guid", e.getId().toString()));
			}
			indexReader.close();
		} catch (Exception e) {
			logger.info("Rss module error: " + e.toString());
		} finally {
			rfl.releaseLock();
		}
	}
	private void generateRssFeed(Binder binder) {

		// See if the feed already exists
		File rssdir = getRssIndexPath(binder);
		if (IndexReader.indexExists(rssdir.getPath()))
			return;

		// Make sure the rss directory exists
		if (!rssdir.exists())
			rssdir.mkdirs();

		generateRssIndex(binder);
	}
	private synchronized void generateRssIndex(Binder binder) {
		// create a new index, then populate it
		LockFile rfl = new LockFile(getRssIndexLockFile(binder));

		try {
			if (!rfl.getLock()) {
				logger.info("Couldn't get the RssFeedLock");
			}
			IndexWriter indexWriter = new IndexWriter(this
					.getRssIndexPath(binder), new SsfIndexAnalyzer(), true);

			long startDate = new Date().getTime() - (maxDays * DAYMILLIS);
			Date start = new Date(startDate);

			// TODO figure out sort key
			List<FolderEntry> entries = getFolderDao().loadFolderTreeUpdates(
					(Folder) binder, start, new Date(),
					new OrderBy("HKey.sortKey"), 100);
			for (int i = 0; i < entries.size(); i++) {
				Entry entry = entries.get(i);
				org.apache.lucene.document.Document doc = createDocumentFromEntry(entry);
				indexWriter.addDocument(doc);
			}
			indexWriter.close();
		} catch (Exception e) {
			logger.info("generateRssIndex: " + e.toString());
		} finally {
			rfl.releaseLock();
		}
	}

	/**
	 * See if the rss feed has been inactive for more than a month, if so, delete it.
	 * 
	 * @param entry
	 * @param indexPath
	 * @return
	 */
	private boolean rssFeedInactive(Binder binder) {
		// set the pathname to the rss last read timestamp file
		try {
			File tf = getRssIndexActivityFile(binder);
			// if it doesn't exist, then create it now
			if (!tf.exists()) {
				// create it
				tf.createNewFile();
				return false;
			}
			long lastModified = tf.lastModified();
			// see if it's been a month
			long currentTime = System.currentTimeMillis();
			long maxInactive = maxInactiveDays * DAYMILLIS;
			if ((currentTime - lastModified) > maxInactive) {
				deleteFeed(binder);
				return true;
			}
		} catch (Exception e) {
		}
		return false;
	}

	/**
	 * 
	 * @param indexPath
	 */
	private void updateTimestamp(Binder binder) {
		File tf = getRssIndexActivityFile(binder);
		// if it doesn't exist, then create it now
		if (!tf.exists()) {
			// create it
			try {
				tf.createNewFile();
			} catch (Exception e) {
			}
			return;
		} else {
			tf.setLastModified(System.currentTimeMillis());
		}
	}

	/**
	 * Update the rss feed whenever a new entry is added, or when a current entry
	 * is modified.  If the RSS index for this folder does not exist, or, hasn't been
	 * read in a time period then return immediately.  
	 * 
	 * The feed will be pruned, both by removing duplicate entries (for modify ops) and
	 * by the age of the entries.
	 * 
	 * @param entry
	 */
	public synchronized void updateRssFeed(Entry entry) {

		SimpleProfiler.startProfiler("RssModule.updateRssFeed");
		File indexPath = getRssIndexPath(entry.getParentBinder());

		// See if the feed already exists
		if (!indexPath.exists()) {
			SimpleProfiler.stopProfiler("RssModule.updateRssFeed");
			return; // if it doesn't already exist, then don't update it.
		}

		LockFile rfl = new LockFile(getRssIndexLockFile(entry.getParentBinder()));
		try {
			if (!rfl.getLock()) {
				logger.info("Couldn't get the RssFeedLock");
			}
			// check to see if this feed has been read from in a month, if yes, 
			// change the last modified timestamp, otherwise, delete it and 
			// let the next reader recreate it.
			if (rssFeedInactive(entry.getParentBinder()))
				return;

			// see if the rss capability has been disabled, if so, delete the feed.
			boolean rssEnabled = SPropsUtil.getBoolean("rss.enable", true);
			if (!rssEnabled) {
				deleteFeed(entry.getParentBinder());
				return;
			}
			
			long endDate = new Date().getTime() - (maxDays * DAYMILLIS);
			String dateRange = "0 TO " + endDate;

			List<Integer> delDocIds = new ArrayList<Integer>();

			IndexSearcher indexSearcher = new IndexSearcher(indexPath.getPath());
			// see if the current entry is already in the index, if it is,
			// delete it. 
			String qString = "guid:" + entry.getId().toString();
			Query q = qp.parse(qString);
			Hits hits = indexSearcher.search(q);

			for (int i = 0; i < hits.length(); i++) {
				delDocIds.add(hits.id(i));
			}
			// trim the rss file based on number of entries, and/or elapsed time
			qString = "age:[" + dateRange + "]";
			hits = indexSearcher.search(qp.parse(qString));

			for (int i = 0; i < hits.length(); i++) {
				delDocIds.add(hits.id(i));
			}

			indexSearcher.close();
			if (delDocIds.size() > 0) {
				IndexReader ir = IndexReader.open(indexPath);
				for (int i = 0; i < delDocIds.size(); i++) {
					ir.deleteDocument((int) delDocIds.get(i));
				}
				ir.close();
			}

			// now add the entry
			IndexWriter indexWriter = new IndexWriter(indexPath,
					new SsfIndexAnalyzer(), false);
			indexWriter.addDocument(this.createDocumentFromEntry(entry));
			indexWriter.close();
		} catch (Exception e) {
			logger.info("Rss module error: " + e.toString());
		} finally {
			rfl.releaseLock();
		}
		SimpleProfiler.stopProfiler("RssModule.updateRssFeed");
	}

	private String getRssDisabledString() {
		String rss = addRssHeader("RSS has been disabled for this feed");
		rss += addRssFooter();
		return rss;
	}
	/**
	 * Find the rss feed index. If it doesn't exist, then create it.
	 * Filter results by the requestor's acls.
	 * 
	 * @param request
	 * @param response
	 * @param binder
	 * @param user
	 */
	public synchronized String filterRss(HttpServletRequest request,
			HttpServletResponse response, Binder binder) {

		boolean rssEnabled = SPropsUtil.getBoolean("rss.enable", true);
		if (!rssEnabled) {
			return this.getRssDisabledString();
		}
		
		File indexPath = getRssIndexPath(binder);
		LockFile rfl = new LockFile(getRssIndexLockFile(binder));

		try {
			// See if the feed already exists

			if (!indexPath.exists())
				this.generateRssFeed(binder);

			if (!rfl.getLock()) {
				logger.info("Couldn't get the RssFeedLock");
			}

			updateTimestamp(binder);

			IndexSearcher indexSearcher = new IndexSearcher(indexPath.getPath());

			// this will add acls to the query
			Query q = buildRssQuery();

			// get the matching entries
			Hits hits = indexSearcher.search(q);

			// create the return string, add the channel info
			String rss = addRssHeader(binder.getTitle());
			
			String adapterRoot = WebUrlUtil.getAdapterRootURL(request, null);
			
			// step thru the hits and add them to the rss return string
			int count = 0;
			String item;
			while (count < hits.length()) {
				org.apache.lucene.document.Document doc = hits.doc(count);
				item = doc.getField("rssItem").stringValue();
				item = fixupSchemeHostPort(item, adapterRoot);
				rss += item;
				count++;
			}
			indexSearcher.close();
			rss += addRssFooter();

			return rss;

		} catch (Exception e) {
			logger.info("filterRss: " + e.toString());
			return "";
		} finally {
			rfl.releaseLock();
		}
	}

	/**
	 * Find the atom feed index. If it doesn't exist, then create it.
	 * Filter results by the requestor's acls.
	 * 
	 * @param request
	 * @param response
	 * @param binder
	 * @param user
	 */
	public synchronized String filterAtom(HttpServletRequest request,
			HttpServletResponse response, Binder binder) {

		boolean rssEnabled = SPropsUtil.getBoolean("rss.enable", true);
		if (!rssEnabled) {
			return this.getRssDisabledString();
		}
		
		File indexPath = getRssIndexPath(binder);
		LockFile rfl = new LockFile(getRssIndexLockFile(binder));

		try {
			// See if the feed already exists

			if (!indexPath.exists())
				this.generateRssFeed(binder);

			if (!rfl.getLock()) {
				logger.info("Couldn't get the RssFeedLock");
			}

			updateTimestamp(binder);

			IndexSearcher indexSearcher = new IndexSearcher(indexPath.getPath());

			// this will add acls to the query
			Query q = buildRssQuery();

			// get the matching entries
			Hits hits = indexSearcher.search(q);

			// create the return string, add the channel info
			String atom = addAtomHeader(binder.getTitle());
			
			String adapterRoot = WebUrlUtil.getAdapterRootURL(request, null);
			
			// step thru the hits and add them to the rss return string
			int count = 0;
			String item;
			while (count < hits.length()) {
				org.apache.lucene.document.Document doc = hits.doc(count);
				item = doc.getField("atomItem").stringValue();
				item = fixupSchemeHostPort(item, adapterRoot);
				atom += item;
				count++;
			}
			indexSearcher.close();
			atom += addAtomFooter();

			return atom;

		} catch (Exception e) {
			logger.info("filterAtom: " + e.toString());
			return "";
		} finally {
			rfl.releaseLock();
		}
	}

	protected String fixupSchemeHostPort(String item, String adapterRoot) {
		Matcher matcher = pattern.matcher(item);
		return matcher.replaceFirst("<link>" + adapterRoot);
	}
	
	/**
	 * return an Authentication error to the reader
	 * 
	 *  @param request
	 *  @param response
	 */
	public String AuthError(HttpServletRequest request,
			HttpServletResponse response) {
		Document doc = createEmptyRssDoc(NLT.get("rss.auth.failure"));
		return doc.asXML();
	}
	
	public String BinderExistenceError(HttpServletRequest request,
			HttpServletResponse response) {
		Document doc = createEmptyRssDoc(NLT.get("binder.deleted"));
		return doc.asXML();
	}
	
	private org.apache.lucene.document.Document createDocumentFromEntry(
			Entry entry) {
		org.apache.lucene.document.Document doc = new org.apache.lucene.document.Document();

		Date eDate = entry.getModification().getDate();

		Field ageField = new Field("age", new Long(eDate.getTime()).toString(),
				Field.Store.YES, Field.Index.UN_TOKENIZED);
		doc.add(ageField);
		Field guidField = new Field("guid", entry.getId().toString(),
				Field.Store.YES, Field.Index.TOKENIZED);
		doc.add(guidField);
		Field allField = new Field(ALL_FIELD, "all", Field.Store.NO,
				Field.Index.UN_TOKENIZED);
		doc.add(allField);
		Field rssItemField = new Field("rssItem", createRssItem(entry),
				Field.Store.YES, Field.Index.UN_TOKENIZED);
		doc.add(rssItemField);
		Field atomItemField = new Field("atomItem", createAtomItem(entry),
				Field.Store.YES, Field.Index.UN_TOKENIZED);
		doc.add(atomItemField);
		// add same acls(folder and entry) as search engine uses
		EntityIndexUtils.addReadAccess(doc, entry.getParentBinder(), entry, true);

		return doc;

	}

	private Query buildRssQuery() {
		SearchObject so = new SearchObject();
		org.dom4j.Document qTree = DocumentHelper.createDocument();
		Element qTreeRootElement = qTree.addElement(org.kablink.util.search.Constants.QUERY_ELEMENT);
		Element qTreeAndElement = qTreeRootElement
				.addElement(org.kablink.util.search.Constants.AND_ELEMENT);
		Element field = qTreeAndElement.addElement(org.kablink.util.search.Constants.FIELD_ELEMENT);
		field.addAttribute(org.kablink.util.search.Constants.FIELD_NAME_ATTRIBUTE, ALL_FIELD);
		Element child = field.addElement(org.kablink.util.search.Constants.FIELD_TERMS_ELEMENT);
		child.setText("all");
		QueryBuilder qb = new QueryBuilder(true);
		so = qb.buildQuery(qTree);

		return so.getQuery();

	}

	private String createRssItem(Entry entry) {
		String ret = "<item>\n";
		String title = "<![CDATA[ " + entry.getTitle() + "]]>";
		ret += "<title>" + title + "</title>\n";
		ret += "<link>"
				+ PermaLinkUtil.getPermalink(entry).replaceAll(
						"&", "&amp;") + "</link>\n";

		String description = entry.getDescription() == null ? "" : entry
				.getDescription().getText();
		description = MarkupUtil.markupStringReplacement(null, null, null, null,
				entry, description, WebKeys.MARKUP_EXPORT);
		description = "<![CDATA[ " + description + "]]>";
		ret += "<description>" + description + "</description>\n";

		ret += "<author>" + entry.getCreation().getPrincipal().getName()
				+ "</author>\n";

		Date eDate = entry.getModification().getDate();
		
		ret += "<pubDate>" + rssFmt.format(eDate) + "</pubDate>\n";

		ret += "<age>" + new Long(eDate.getTime()).toString() + "</age>\n";
		ret += "<guid>" + PermaLinkUtil.getPermalink(entry).replaceAll(
				"&", "&amp;") + "</guid>\n";
		ret += "</item>\n";
		return ret;
	}

	private String addRssHeader(String title) {
		String ret = "";
		ret += "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
		ret += "<rss version=\"2.0\">\n";
		ret += "<channel>\n";
		ret += "<title><![CDATA[ " +  title + "]]></title>\n";
		ret += "<link/>";
		ret += "<description><![CDATA[ " + title + "]]></description>\n";
		ret += "<pubDate>" + rssFmt.format(new Date()) + "</pubDate>\n";
		ret += "<ttl>60</ttl>\n";
		ret += "<generator feedVersion=\"1.0\">kablink</generator>\n";
		return ret;
	}

	private String addRssFooter() {
		String ret = "";
		ret += "</channel>\n";
		ret += "</rss>\n";
		return ret;
	}
	
	private Document createEmptyRssDoc(String title) {
		// First create our top-level document
		Document doc = DocumentHelper.createDocument();
		Element root = doc.addElement("rss");

		// Set RSS version number
		root.addAttribute("version", "2.0");

		Element channel = root.addElement("channel");

		channel.addElement("title").addText(title);
		channel.addElement("link").addText("");
		channel.addElement("description").addText(title);
		channel.addElement("pubDate").addText(rssFmt.format(new Date()));
		channel.addElement("ttl").addText("60");
		channel.addElement("generator").addAttribute("feedVersion", "1.0")
				.addText("kablink");

		return doc;
	}
	private String addAtomHeader(String title) {
		String ret = "";
		ret += "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
		ret += "<feed xmlns=\"http://www.w3.org/2005/Atom\">\n";
		//ret += "<title><![CDATA[ " +  title + "]]></title>\n";
		ret += "<title> " +  title + "</title>\n";
		ret += "<link/>";
		//ret += "<subtitle><![CDATA[ " + title + "]]></subtitle>\n";
		ret += "<subtitle> " + title + " </subtitle>\n";
		ret += "<updated>" + atomFmt.format(new Date()) + "</updated>\n";
		ret += "<generator feedVersion=\"1.0\">kablink</generator>\n";
		return ret;
	}

	private String addAtomFooter() {
		String ret = "";
		ret += "</feed>\n";
		return ret;
	}
	
	private String createAtomItem(Entry entry) {
		String ret = "<entry>\n";
		//String title = "<![CDATA[ " + entry.getTitle() + "]]>";
		String title = entry.getTitle();
		ret += "<title>" + title + "</title>\n";
		ret += "<link>"
				+ PermaLinkUtil.getPermalink(entry).replaceAll(
						"&", "&amp;") + "</link>\n";

		String subtitle = entry.getDescription() == null ? "" : entry
				.getDescription().getText();
		subtitle = MarkupUtil.markupStringReplacement(null, null, null, null,
				entry, subtitle, WebKeys.MARKUP_EXPORT);
		//subtitle = "<![CDATA[ " + subtitle + "]]>";
		ret += "<subtitle>" + subtitle + "</subtitle>\n";

		ret += "<author>" + entry.getCreation().getPrincipal().getName()
				+ "</author>\n";

		Date eDate = entry.getModification().getDate();
		
		ret += "<published>" + atomFmt.format(eDate) + "</published>\n";

		ret += "<age>" + new Long(eDate.getTime()).toString() + "</age>\n";
		ret += "<id>" + PermaLinkUtil.getPermalink(entry).replaceAll(
				"&", "&amp;") + "</id>\n";
		ret += "</entry>\n";
		return ret;
	}
	
	private Document createEmptyAtomDoc(String title) {
		// First create our top-level document
		Document doc = DocumentHelper.createDocument();
		Element root = doc.addElement("feed");

		// Set Atom namespace
		root.addAttribute("xmlns","http://www.w3.org/2005/Atom");

		root.addElement("title").addText(title);
		root.addElement("link").addText("");
		root.addElement("subtitle").addText(title);
		root.addElement("updated").addText(atomFmt.format(new Date()));
		root.addElement("generator").addAttribute("feedVersion", "1.0")
				.addText("kablink");

		return doc;
	}
}
