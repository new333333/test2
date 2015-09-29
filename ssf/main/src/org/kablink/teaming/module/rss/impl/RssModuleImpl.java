/**
 * Copyright (c) 1998-2015 Novell, Inc. and its licensors. All rights reserved.
 * 
 * This work is governed by the Common Public Attribution License Version 1.0 (the
 * "CPAL"); you may not use this file except in compliance with the CPAL. You may
 * obtain a copy of the CPAL at http://www.opensource.org/licenses/cpal_1.0. The
 * CPAL is based on the Mozilla Public License Version 1.1 but Sections 14 and 15
 * have been added to cover use of software over a computer network and provide
 * for limited attribution for the Original Developer. In addition, Exhibit A has
 * been modified to be consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT
 * WARRANTY OF ANY KIND, either express or implied. See the CPAL for the specific
 * language governing rights and limitations under the CPAL.
 * 
 * The Original Code is ICEcore, now called Kablink. The Original Developer is
 * Novell, Inc. All portions of the code written by Novell, Inc. are Copyright
 * (c) 1998-2015 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2015 Novell, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by Kablink]
 * Attribution URL: [www.kablink.org]
 * Graphic Image as provided in the Covered Code
 * [ssf/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are
 * defined in the CPAL as a work which combines Covered Code or portions thereof
 * with code not governed by the terms of the CPAL.
 * 
 * NOVELL and the Novell logo are registered trademarks and Kablink and the
 * Kablink logos are trademarks of Novell, Inc.
 */
package org.kablink.teaming.module.rss.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.activation.FileTypeMap;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.dao.util.OrderBy;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.DefinableEntity;
import org.kablink.teaming.domain.Description;
import org.kablink.teaming.domain.EntityIdentifier.EntityType;
import org.kablink.teaming.domain.Entry;
import org.kablink.teaming.domain.Folder;
import org.kablink.teaming.domain.FolderEntry;
import org.kablink.teaming.domain.Principal;
import org.kablink.teaming.lucene.analyzer.SsfIndexAnalyzer;
import org.kablink.teaming.lucene.analyzer.SsfQueryAnalyzer;
import org.kablink.teaming.module.binder.BinderModule;
import org.kablink.teaming.module.folder.FolderModule;
import org.kablink.teaming.module.impl.CommonDependencyInjection;
import org.kablink.teaming.module.profile.ProfileModule;
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
import org.kablink.teaming.util.SpringContextUtil;
import org.kablink.teaming.util.Utils;
import org.kablink.teaming.util.XmlFileUtil;
import org.kablink.teaming.web.WebKeys;
import org.kablink.teaming.web.util.MarkupUtil;
import org.kablink.teaming.web.util.PermaLinkUtil;
import org.kablink.teaming.web.util.WebUrlUtil;
import org.kablink.util.LockFile;
import org.kablink.util.PropertyNotFoundException;

import org.springframework.mail.javamail.ConfigurableMimeFileTypeMap;

import org.w3c.tidy.Tidy;
import org.w3c.tidy.TidyMessage;

/**
 * ?
 * 
 * @author ?
 */
@SuppressWarnings({"deprecation", "unused"})
public class RssModuleImpl extends CommonDependencyInjection implements RssModule, RssModuleImplMBean {
	private static QueryParser qp = new QueryParser(Version.LUCENE_34, "guid", new SsfQueryAnalyzer());

	private final String ALL_FIELD = "allField";
	private final String ALL = "all";
	private final String AGE = "age";

	private final long DAYMILLIS = 24L * 60L * 60L * 1000L;

	protected Log logger = LogFactory.getLog(getClass());
	protected ProfileModule profileModule;
	protected BinderModule binderModule;
	protected FolderModule folderModule;
	
	private String rssRootDir;

	int maxDays = 31;

	int maxInactiveDays = 7;
	SimpleDateFormat rssFmt = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z");
	SimpleDateFormat atomFmt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

	private static final String SCHEME_HOST_PORT_PATTERN = "<link>http.*/ssf/a/";
	private static final String AUTHOR_ID_PATTERN = "<authorId>([0-9]*)</authorId>\n";
	private static final String AUTHOR_PATTERN = "<author>(.*)</author>";
	private static final String AUTHOR_NAME_PATTERN = "<author><name>(.*)</name></author>";
	private static final String RSS_ATTACHMENT_URL_PATTERN = "(\\{\\{RSSattachmentUrl: ([^}]*)\\}\\})";
	private static final String RSS_ENTITY_ID_PATTERN = " entityId=([0-9]*)";
	private static final String RSS_ENTITY_TYPE_PATTERN = " entityType=([^\\s]*)";
	private static final String RSS_FILE_NAME_PATTERN = " fileName=([^\\}]*)";
	
	private Pattern pattern = Pattern.compile(SCHEME_HOST_PORT_PATTERN);
	private Pattern patternAuthorId = Pattern.compile(AUTHOR_ID_PATTERN);
	private Pattern patternAuthor = Pattern.compile(AUTHOR_PATTERN);
	private Pattern patternAuthorName = Pattern.compile(AUTHOR_NAME_PATTERN);
	private Pattern rssAttachmentUrlPattern = Pattern.compile(RSS_ATTACHMENT_URL_PATTERN, Pattern.CASE_INSENSITIVE );
	private Pattern rssEntityIdPattern = Pattern.compile(RSS_ENTITY_ID_PATTERN, Pattern.CASE_INSENSITIVE );
	private Pattern rssEntityTypePattern = Pattern.compile(RSS_ENTITY_TYPE_PATTERN, Pattern.CASE_INSENSITIVE );
	private Pattern rssFileNamePattern = Pattern.compile(RSS_FILE_NAME_PATTERN, Pattern.CASE_INSENSITIVE );
	
	private FileTypeMap mimeTypes = new ConfigurableMimeFileTypeMap();

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

	public ProfileModule getProfileModule() {
		return this.profileModule;
	}
	@Override
	public String getRssRootDir() {
		return rssRootDir;
	}
	
	/*
	 */
	private BinderModule getBinderModule() {
		return ((BinderModule) SpringContextUtil.getBean("binderModule"));
	}
	
	/*
	 */
	private FolderModule getFolderModule() {
		return ((FolderModule) SpringContextUtil.getBean("folderModule"));
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
	private Directory getRssIndexDirectory(Binder binder) throws IOException {
		return FSDirectory.open(getRssIndexPath(binder));
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
	
	@Override
	public synchronized void deleteRssFeed(Binder binder) {
		File indexPath = getRssIndexPath(binder);		
		if (!indexPath.exists()) return;
		LockFile rfl = new LockFile(getRssIndexLockFile(binder));
		try {
			if (!rfl.getLock()) {
				logger.info("Couldn't get the RssFeedLock");
			}
			deleteFeed(binder, rfl);
		} finally {
			rfl.releaseLock();
		}
		
	}
	public synchronized void deleteFeed(Binder binder, LockFile rfl) {

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
		try {
			if (rf.exists())
				rfl.releaseLock();
				FileHelper.deleteRecursively(rf);
		} catch (Exception e) {}; //just ignore the error, the feed is gone
	}

	@Override
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
			IndexReader indexReader = IndexReader.open(getRssIndexDirectory(binder), false);
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
	private void generateRssFeed(Binder binder) throws IOException {

		// See if the feed already exists
		File rssdir = getRssIndexPath(binder);
		if (IndexReader.indexExists(getRssIndexDirectory(binder)))
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
					.getRssIndexDirectory(binder), new SsfIndexAnalyzer(), true, IndexWriter.MaxFieldLength.UNLIMITED);

			long startDate = new Date().getTime() - (maxDays * DAYMILLIS);
			Date start = new Date(startDate);

			List<FolderEntry> entries = getFolderDao().loadFolderTreeUpdates(
					(Folder) binder, start, new Date(),
					new OrderBy("HKey.sortKey"), 100);
			for (int i = 0; i < entries.size(); i++) {
				FolderEntry entry = entries.get(i);
				if (!(entry.isPreDeleted())) {
					org.apache.lucene.document.Document doc = createDocumentFromEntry(entry);
					indexWriter.addDocument(doc);
				}
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
	private boolean rssFeedInactive(Binder binder, LockFile rfl) {
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
				deleteFeed(binder, rfl);
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
	@Override
	public synchronized void updateRssFeed(Entry entry) {

		SimpleProfiler.start("RssModule.updateRssFeed");
		File indexPath = getRssIndexPath(entry.getParentBinder());

		// See if the feed already exists
		if (!indexPath.exists()) {
			SimpleProfiler.stop("RssModule.updateRssFeed");
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
			if (rssFeedInactive(entry.getParentBinder(),rfl))
				return;

			// see if the rss capability has been disabled, if so, delete the feed.
			boolean rssEnabled = SPropsUtil.getBoolean("rss.enable", true);
			if (!rssEnabled) {
				deleteFeed(entry.getParentBinder(), rfl);
				return;
			}
			
			long endDate = new Date().getTime() - (maxDays * DAYMILLIS);
			String dateRange = "0 TO " + endDate;

			List<Integer> delDocIds = new ArrayList<Integer>();

			IndexSearcher indexSearcher = new IndexSearcher(getRssIndexDirectory(entry.getParentBinder()));
			// see if the current entry is already in the index, if it is,
			// delete it. 
			String qString = "guid:" + entry.getId().toString();
			Query q = qp.parse(qString);
			TopDocs topDocs = indexSearcher.search(q, Integer.MAX_VALUE);
			ScoreDoc[] scoreDocs = topDocs.scoreDocs;

			for (int i = 0; i < topDocs.totalHits; i++) {
				delDocIds.add(scoreDocs[i].doc);
			}
			// trim the rss file based on number of entries, and/or elapsed time
			qString = "age:[" + dateRange + "]";
			topDocs = indexSearcher.search(qp.parse(qString), Integer.MAX_VALUE);
			scoreDocs = topDocs.scoreDocs;

			for (int i = 0; i < topDocs.totalHits; i++) {
				delDocIds.add(scoreDocs[i].doc);
			}

			Directory directory = getRssIndexDirectory(entry.getParentBinder());
			indexSearcher.close();
			if (delDocIds.size() > 0) {
				IndexReader ir = IndexReader.open(directory, false);
				for (int i = 0; i < delDocIds.size(); i++) {
					ir.deleteDocument((int) delDocIds.get(i));
				}
				ir.close();
			}

			// now add the entry
			IndexWriter indexWriter = new IndexWriter(directory,
					new SsfIndexAnalyzer(), false, IndexWriter.MaxFieldLength.UNLIMITED);
			indexWriter.addDocument(this.createDocumentFromEntry(entry));
			indexWriter.close();
		} catch (Exception e) {
			logger.info("Rss module error: " + e.toString());
		} finally {
			rfl.releaseLockIfValid();
		}
		SimpleProfiler.stop("RssModule.updateRssFeed");
	}

	private String getRssDisabledString(HttpServletRequest request) {
		String rss = addRssHeader(request, null, "RSS has been disabled for this feed");
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
	@Override
	public synchronized void filterRss(HttpServletRequest request,
			HttpServletResponse response, Binder binder) {

		ServletOutputStream out;
		try {
			out = response.getOutputStream();
		} catch (IOException e1) {
			logger.info("filterRss: " + e1.toString());
			return;
		}
		boolean rssEnabled = SPropsUtil.getBoolean("rss.enable", true);
		if (!rssEnabled) {
			try {
				out.print(this.getRssDisabledString(request));
			} catch (IOException e) {
				logger.info("filterRss: " + e.toString());
			}
			return;
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

			IndexSearcher indexSearcher = new IndexSearcher(getRssIndexDirectory(binder));

			// this will add acls to the query
			SearchObject so = buildRssQuery();

			// get the matching entries
			TopDocs topDocs = indexSearcher.search(so.getLuceneQuery(), null, Integer.MAX_VALUE, so.getSortBy());

			// create the return string, add the channel info
			String rss = addRssHeader(request, binder, binder.getTitle());
			
			String adapterRoot = WebUrlUtil.getAdapterRootURL(request, null);
			
			// step thru the hits and add them to the rss return string
			int count = 0;
			String item;
			ScoreDoc[] scoreDocs = topDocs.scoreDocs;
			while (count < topDocs.totalHits) {
				org.apache.lucene.document.Document doc = indexSearcher.doc(scoreDocs[count].doc);
				item = doc.getField("rssItem").stringValue();
				item = fixupSchemeHostPort(item, adapterRoot);
				item = fixupAuthor(item, adapterRoot);
				rss += item;
				count++;
			}
			indexSearcher.close();
			rss += addRssFooter();

			outputRssAttachmentFiles(rss, binder, out);
			return;

		} catch (Exception e) {
			logger.info("filterRss: " + e.toString());
			return;
		} finally {
			rfl.releaseLock();
		}
	}
	
	private void outputRssAttachmentFiles(String text, Binder binder, ServletOutputStream out) {
		long zoneId = RequestContextHolder.getRequestContext().getZoneId();
		try {
	    	Matcher m = rssAttachmentUrlPattern.matcher(text);
	    	while (m.find()) {
	    		String urlInfo = m.group(0);
	    		
		    	//Now, replace the RSS URL Pattern with either a permalink or a base64 version of an image file
				String s_id = "";
				Matcher fieldMatcher = rssEntityIdPattern.matcher(urlInfo);
				if (fieldMatcher.find() && fieldMatcher.groupCount() >= 1) s_id = fieldMatcher.group(1);
				String s_entityType = "";
				fieldMatcher = rssEntityTypePattern.matcher(urlInfo);
				if (fieldMatcher.find() && fieldMatcher.groupCount() >= 1) s_entityType = fieldMatcher.group(1);
				String s_fileName = "";
				fieldMatcher = rssFileNamePattern.matcher(urlInfo);
				if (fieldMatcher.find() && fieldMatcher.groupCount() >= 1) s_fileName = fieldMatcher.group(1);
				String startText = text.substring(0, m.start());
				out.print(startText);
				
				//See if this is an image file
				String mimeType = mimeTypes.getContentType(s_fileName);
				String url = PermaLinkUtil.getFilePermalink(Long.valueOf(s_id), s_entityType, s_fileName);
				if (mimeType != null && mimeType.startsWith("image/")) {
					DefinableEntity entity;
					if (s_entityType.equals(EntityType.folderEntry.name())) {
						entity = getFolderModule().getEntry(null, Long.valueOf(s_id));
						
					} else {
						entity = getBinderModule().getBinder(Long.valueOf(s_id));
					}
					if (!Utils.outputImageAsDataUrl(entity, s_fileName, mimeType, out)) {
						//Sending out the file failed, so just output the permalink
						out.print(url);
					}
				} else {
					out.print(url);
				}
				text = text.substring(m.end());
				m = rssAttachmentUrlPattern.matcher(text);
	
	    	}
	    	//output the remainder of the text
	    	out.write(text.getBytes(XmlFileUtil.FILE_ENCODING));	// Bugzilla 938060:  Handle all UTF-8 characters. 
	    	
		} catch (IOException e) {
			logger.info("filterRss: " + e.toString());
		} catch (Exception e) {
			logger.info("filterRss: " + e.toString());
			try {
				//Try to output the remaining text
				out.print(text);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				logger.info("filterRss: " + e.toString());
			}
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
	@Override
	public synchronized void filterAtom(HttpServletRequest request,
			HttpServletResponse response, Binder binder) {
		
		ServletOutputStream out;
		try {
			out = response.getOutputStream();
		} catch (IOException e1) {
			logger.info("filterAtom: " + e1.toString());
			return;
		}
		boolean rssEnabled = SPropsUtil.getBoolean("rss.enable", true);
		if (!rssEnabled) {
			try {
				out.print(this.getRssDisabledString(request));
			} catch (IOException e) {
				logger.info("filterAtom: " + e.toString());
			}
			return;
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

			IndexSearcher indexSearcher = new IndexSearcher(getRssIndexDirectory(binder));

			// this will add acls to the query
			SearchObject so = buildRssQuery();

			// get the matching entries
			TopDocs topDocs = indexSearcher.search(so.getLuceneQuery(), null, Integer.MAX_VALUE, so.getSortBy());

			// create the return string, add the channel info
			String atom = addAtomHeader(binder.getTitle());
			
			String adapterRoot = WebUrlUtil.getAdapterRootURL(request, null);
			
			// step thru the hits and add them to the rss return string
			int count = 0;
			String item;
			ScoreDoc[] scoreDocs = topDocs.scoreDocs;
			while (count < topDocs.totalHits) {
				org.apache.lucene.document.Document doc = indexSearcher.doc(scoreDocs[count].doc);
				item = doc.getField("atomItem").stringValue();
				item = fixupSchemeHostPort(item, adapterRoot);
				item = fixupAuthorName(item, adapterRoot);
				atom += item;
				count++;
			}
			indexSearcher.close();
			atom += addAtomFooter();

			outputRssAttachmentFiles(atom, binder, out);
			return;

		} catch (Exception e) {
			logger.info("filterAtom: " + e.toString());
			return;
		} finally {
			rfl.releaseLock();
		}
	}

	protected String fixupSchemeHostPort(String item, String adapterRoot) {
		Matcher matcher = pattern.matcher(item);
		return matcher.replaceFirst("<link>" + adapterRoot);
	}
	
	protected String fixupAuthor(String item, String adapterRoot) {
		//See if this user is allowed to see the author of this entry
		String id = "";
		Matcher matcher = patternAuthorId.matcher(item);
		if (matcher.find()) {
			id = matcher.group(1);
			item = matcher.replaceFirst("");
		}
		if (Utils.canUserOnlySeeCommonGroupMembers()) {
			if (!id.equals("")) {
				Principal p = profileDao.loadPrincipal(Long.valueOf(id), RequestContextHolder.getRequestContext().getZoneId(), true);
				if (p != null) {
					p = Utils.fixProxy(p);
					matcher = patternAuthor.matcher(item);
					if (matcher.find()) {
						item = matcher.replaceFirst("<author>" + p.getTitle() + "</author>");
					}
				}
			}
		}
		return item;
	}
	
	protected String fixupAuthorName(String item, String adapterRoot) {
		//See if this user is allowed to see the author of this entry
		String id = "";
		Matcher matcher = patternAuthorId.matcher(item);
		if (matcher.find()) {
			id = matcher.group(1);
			item = matcher.replaceFirst("");
		}
		if (Utils.canUserOnlySeeCommonGroupMembers()) {
			if (!id.equals("")) {
				Principal p = profileDao.loadPrincipal(Long.valueOf(id), RequestContextHolder.getRequestContext().getZoneId(), true);
				if (p != null) {
					p = Utils.fixProxy(p);
					matcher = patternAuthorName.matcher(item);
					if (matcher.find()) {
						item = matcher.replaceFirst("<author><name>" + p.getTitle() + "</name></author>");
					}
				}
			}
		}
		return item;
	}
	
	/**
	 * return an Authentication error to the reader
	 * 
	 *  @param request
	 *  @param response
	 */
	@Override
	public String AuthError(HttpServletRequest request,
			HttpServletResponse response) {
		Document doc = createEmptyRssDoc(NLT.get("rss.auth.failure"));
		return doc.asXML();
	}
	
	@Override
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
				Field.Store.YES, Field.Index.NOT_ANALYZED);
		doc.add(ageField);
		Field guidField = new Field("guid", entry.getId().toString(),
				Field.Store.YES, Field.Index.ANALYZED);
		doc.add(guidField);
		Field allField = new Field(ALL_FIELD, ALL, Field.Store.NO,
				Field.Index.NOT_ANALYZED);
		doc.add(allField);
		Field rssItemField = new Field("rssItem", createRssItem(entry),
				Field.Store.YES, Field.Index.NOT_ANALYZED);
		doc.add(rssItemField);
		Field atomItemField = new Field("atomItem", createAtomItem(entry),
				Field.Store.YES, Field.Index.NOT_ANALYZED);
		doc.add(atomItemField);
		// add same acls(folder and entry) as search engine uses
		EntityIndexUtils.addReadAccess(doc, entry.getParentBinder(), entry, true, true);
		EntityIndexUtils.addBinderAcls(doc, entry.getParentBinder());

		return doc;

	}

	private SearchObject buildRssQuery() {
		SearchObject so = new SearchObject();
		SortField[] sortFields = new SortField[1];
		//setup for sorting by age
		boolean descend = true;
		String sortBy = AGE;
		int sortType = SortField.STRING;
		
		org.dom4j.Document qTree = DocumentHelper.createDocument();
		Element qTreeRootElement = qTree.addElement(org.kablink.util.search.Constants.QUERY_ELEMENT);
		Element qTreeAndElement = qTreeRootElement
				.addElement(org.kablink.util.search.Constants.AND_ELEMENT);
		Element field = qTreeAndElement.addElement(org.kablink.util.search.Constants.FIELD_ELEMENT);
		field.addAttribute(org.kablink.util.search.Constants.FIELD_NAME_ATTRIBUTE, ALL_FIELD);
		Element child = field.addElement(org.kablink.util.search.Constants.FIELD_TERMS_ELEMENT);
		child.setText(ALL);
		//create the query
		QueryBuilder qb = new QueryBuilder(true, false);
		so = qb.buildQuery(qTree);
		// add the sort field (by descending age)
		sortFields[0] = new SortField(sortBy, sortType, descend);
		so.setSortBy(sortFields);
		combineAclClausesWithQuery(so);
		return so;

	}

	private String createRssItem(Entry entry) {
		String ret = "<item>\n";
		String title = "<![CDATA[ " + entry.getTitle() + "]]>";
		ret += "<title>" + title + "</title>\n";
		ret += "<link>"
				+ PermaLinkUtil.getPermalink(entry).replaceAll(
						"&", "&amp;") + "</link>\n";

		String description = entry.getDescription() == null ? "" : tidyGetXHTML(entry
				.getDescription());
		description = MarkupUtil.markupStringReplacement(null, null, null, null,
				entry, description, WebKeys.MARKUP_RSS);
		description = "<![CDATA[ " + description + "]]>";
		ret += "<description>" + description + "</description>\n";

		Principal p = entry.getCreation().getPrincipal();
		ret += "<authorId>" + p.getId().toString() + "</authorId>\n";
		ret += "<author>"+ p.getTitle() + "</author>\n";

		Date eDate = entry.getModification().getDate();
		
		ret += "<pubDate>" + rssFmt.format(eDate) + "</pubDate>\n";

		ret += "<age>" + new Long(eDate.getTime()).toString() + "</age>\n";
		ret += "<guid>" + PermaLinkUtil.getPermalink(entry).replaceAll(
				"&", "&amp;") + "</guid>\n";
		ret += "</item>\n";
		return ret;
	}

	private String addRssHeader(HttpServletRequest request, Binder binder, String title) {
		String ret = "";
		ret += "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
		ret += "<rss version=\"2.0\">\n";
		ret += "<channel>\n";
		ret += "<title><![CDATA[ " +  title + "]]></title>\n";
		if (binder == null) {
			ret += "<link/>";
		} else {
			ret += "<link>"
				+ PermaLinkUtil.getPermalink(binder).replaceAll(
						"&", "&amp;") + "</link>\n";
			ret += "<guid>"
				+ PermaLinkUtil.getPermalink(binder).replaceAll(
						"&", "&amp;") + "</guid>\n";
		}
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
		ret += "<link href=\""
				+ PermaLinkUtil.getPermalink(entry).replaceAll(
						"&", "&amp;") + "\"/>\n";

		String subtitle = entry.getDescription() == null ? "" : tidyGetXHTML(entry
				.getDescription());
		subtitle = MarkupUtil.markupStringReplacement(null, null, null, null,
				entry, subtitle, WebKeys.MARKUP_RSS);
		//subtitle = "<![CDATA[ " + subtitle + "]]>";
		ret += "<subtitle>" + subtitle + "</subtitle>\n";

		Principal p = entry.getCreation().getPrincipal();
		ret += "<authorId>" + p.getId().toString() + "</authorId>\n";
		ret += "<author><name>"+ p.getTitle() + "</name></author>\n";

		Date eDate = entry.getModification().getDate();
		
		ret += "<published>" + atomFmt.format(eDate) + "</published>\n";

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
	
    private String tidyGetXHTML(Description description) {
		String text = description.getText();
		if (description.getFormat() == Description.FORMAT_HTML) {
			ByteArrayInputStream sr = new ByteArrayInputStream(text.getBytes());
			ByteArrayOutputStream sw = new ByteArrayOutputStream();
			TidyMessageListener tml = new TidyMessageListener();
			Tidy tidy = new Tidy();
			tidy.setQuiet(true);
			tidy.setShowWarnings(false);
			tidy.setMessageListener(tml);
			tidy.setPrintBodyOnly(true);
			tidy.setFixUri(false);
			tidy.setFixComments(false);
			tidy.setAsciiChars(false);
			tidy.setBreakBeforeBR(false);
			tidy.setBurstSlides(false);
			tidy.setDropEmptyParas(false);
			tidy.setDropFontTags(false);
			tidy.setDropProprietaryAttributes(false);
			tidy.setEncloseBlockText(false);
			tidy.setEncloseText(false);
			tidy.setIndentAttributes(false);
			tidy.setIndentCdata(false);
			tidy.setIndentContent(false);
			tidy.setLiteralAttribs(true);
			tidy.setLogicalEmphasis(false);
			tidy.setLowerLiterals(false);
			tidy.setMakeClean(false);
			tidy.setMakeBare(false);
			tidy.setInputEncoding("UTF8");
			tidy.setOutputEncoding("UTF8");
			tidy.setRawOut(true);
			tidy.setSmartIndent(false);
			tidy.setTidyMark(false);
			tidy.setWord2000(true);	// Allows <o:p> constructs as per MS Outlook, MS Word, ...
			tidy.setWrapAsp(false);
			tidy.setWrapAttVals(false);
			tidy.setWrapJste(false);
			tidy.setWrapPhp(false);
			tidy.setWrapScriptlets(false);
			tidy.setWrapSection(false);
			tidy.setWraplen(1000000);
			tidy.setXHTML(true);
			org.w3c.dom.Document doc = tidy.parseDOM(sr, sw);
			if (tml.isErrors() || tidy.getParseErrors() > 0) {
				description.setText("");
			} else {
				if (!text.equals("")) {
					//If the original value was not empty, then store the corrected html
					text = sw.toString().trim();
				}
			}
		}
		return text;
    }

	private class TidyMessageListener implements org.w3c.tidy.TidyMessageListener {
		private int errorCount = 0;
		@Override
		public void messageReceived(TidyMessage message) {
			message.toString();
			errorCount++;
		}
		public boolean isErrors() {
			if (errorCount > 0) return true;
			return false;
		}
	}

	private void combineAclClausesWithQuery(SearchObject so) {
		// Note: This directly modifies in place the query object associated with the SearchObject.
		String acls = so.getAclQueryStr();
		if (acls != null && acls.length() != 0) {
			Query top = so.getLuceneQuery();
			if(!(top instanceof BooleanQuery)) {
				BooleanQuery bq = new BooleanQuery();
				bq.add(top, BooleanClause.Occur.MUST);
				top = bq;
			}
			((BooleanQuery) top).add(so.parseQueryStringWSA(acls), BooleanClause.Occur.MUST);
			so.setLuceneQuery(top);
		}
	}
}
