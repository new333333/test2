/**
 * Copyright (c) 1998-2009 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2009 Novell, Inc. All Rights Reserved.
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
package org.kablink.teaming.web.util;

import java.io.IOException;
import java.net.URI;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.portlet.PortletURL;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Element;
import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.domain.CustomAttribute;
import org.kablink.teaming.domain.DefinableEntity;
import org.kablink.teaming.domain.Definition;
import org.kablink.teaming.domain.Description;
import org.kablink.teaming.domain.FileAttachment;
import org.kablink.teaming.domain.ZoneInfo;
import org.kablink.teaming.domain.EntityIdentifier.EntityType;
import org.kablink.teaming.module.binder.BinderModule;
import org.kablink.teaming.module.definition.DefinitionModule;
import org.kablink.teaming.module.folder.FolderModule;
import org.kablink.teaming.module.shared.MapInputData;
import org.kablink.teaming.repository.RepositoryUtil;
import org.kablink.teaming.util.FileUploadItem;
import org.kablink.teaming.util.SPropsUtil;
import org.kablink.teaming.util.SpringContextUtil;
import org.kablink.teaming.web.WebKeys;
import org.kablink.util.BrowserSniffer;
import org.kablink.util.Html;
import org.kablink.util.Http;
import org.kablink.util.Validator;
import org.kablink.util.search.Constants;
import org.springframework.web.multipart.MultipartFile;

import com.google.gwt.dom.client.Document;



public class MarkupUtil {
	protected static Log logger = LogFactory.getLog(MarkupUtil.class);
	//From Doc: All of the state involved in performing a match resides in the matcher, so many matchers can share the same pattern. 
	protected final static Pattern uploadImagePattern = Pattern.compile("(<img [^>]*src=\"[^\"]*viewType=ss_viewUploadFile[^>]*>)");
	protected final static Pattern urlSrcPattern = Pattern.compile("src *= *\"([^\"]*)\"");
	
	protected final static Pattern fileIdPattern = Pattern.compile("fileId=([^\\&\"]*)");
	protected final static Pattern binderIdPattern = Pattern.compile("binderId=([^\\&\"]*)");
	protected final static Pattern entryIdPattern = Pattern.compile("entryId=([^\\&\"]*)");
	protected final static Pattern entityTypePattern = Pattern.compile("entityType=([^\\&\"]*)");
	
	protected final static Pattern v1AttachmentUrlPattern = Pattern.compile("(<img [^>]*src=\"[^>]*viewType=ss_viewAttachmentFile[^>]*>)");
	protected final static Pattern readFileImagePattern = Pattern.compile("(<img [^>]*src=\"[^>]*/readFile/[^>]*>)");
	protected final static Pattern readFilePathPattern = Pattern.compile("/readFile/[^\"]*");
	protected final static Pattern attachedImagePattern = Pattern.compile("(<img [^>]*class=\"\\s*ss_addimage_att\\s*\"[^>]*>)");
	
	protected final static Pattern iceCoreLinkPattern = Pattern.compile("(<a [^>]*class=\"\\s*ss_icecore_link\\s*\"[^>]*>)(.*)");
	protected final static Pattern iceCoreLinkRelPattern = Pattern.compile("rel=\"([^\"]*)");
	protected final static Pattern iceCoreLinkAPattern = Pattern.compile("</a>");
	
	protected final static Pattern youtubeLinkPattern = Pattern.compile("(<a [^>]*class=\"\\s*ss_youtube_link\\s*\"[^>]*>)([^<]*)</a>");
	protected final static Pattern youtubeLinkRelPattern = Pattern.compile("rel=\"([^\"]*)");
	
	protected final static Pattern attachmentUrlPattern = Pattern.compile("(\\{\\{attachmentUrl: ([^}]*)\\}\\})");
	protected final static Pattern v1AttachmentFileIdPattern = Pattern.compile("(\\{\\{attachmentFileId: ([^}]*)\\}\\})");
	protected final static Pattern titleUrlPattern = Pattern.compile("(\\{\\{titleUrl: ([^\\}]*)\\}\\})");
	protected final static Pattern titleUrlBinderPattern = Pattern.compile("binderId=([^ ]*)");
	protected final static Pattern titleUrlZoneUUIDPattern = Pattern.compile("zoneUUID=([^ ]*)");
	protected final static Pattern titleUrlTitlePattern = Pattern.compile("title=([^ ]*)");
	protected final static Pattern titleUrlTextPattern = Pattern.compile("text=(.*)$");
	protected final static Pattern youtubeUrlPattern = Pattern.compile("(\\{\\{youtubeUrl: ([^\\}]*)\\}\\})");
	protected final static Pattern youtubeUrlUrlPattern = Pattern.compile("url=([^ ]*)");
	protected final static Pattern youtubeUrlWidthPattern = Pattern.compile("width=([^ ]*)");
	protected final static Pattern youtubeUrlHeightPattern = Pattern.compile("height=([^ ]*)");
	protected final static Pattern hrefPattern = Pattern.compile("((<a[\\s]href[=\\s]\")([^\":]*)\")");
	protected final static Pattern pageTitleUrlTextPattern = Pattern.compile("(\\[\\[([^\\]]*)\\]\\])");
	protected final static Pattern sectionPattern =Pattern.compile("(==[=]*)([^=]*)(==[=]*)");
	protected final static Pattern httpPattern =Pattern.compile("^https*://[^/]*(/[^/]*)/s/readFile/(.*)$");
	protected static Integer youtubeDivId = 0;

	private static BinderModule binderModule = (BinderModule) SpringContextUtil.getBean("binderModule");
	private static FolderModule folderModule = (FolderModule) SpringContextUtil.getBean("folderModule");
	private static DefinitionModule definitionModule = (DefinitionModule) SpringContextUtil.getBean("definitionModule");

	/**
	 * Parse a description looking for uploaded file references
	 * 
	 * @param Description
	 * @param File
	 * @return 
	 */
	public static void scanDescriptionForUploadFiles(Description description, String fieldName, List fileData) {
		if (Validator.isNull(description.getText())) return;
     	Matcher m = uploadImagePattern.matcher(description.getText());
    	int loopDetector = 0;
    	while (m.find()) {
    		if (loopDetector++ > 2000) {
	        	logger.error("Error processing markup [1]: " + description.getText());
    			break;
    		}
    		String fileHandle = "";
    		String img = m.group();
         	Matcher m2 = fileIdPattern.matcher(img);
        	if (m2.find() && m2.groupCount() >= 1) fileHandle = Http.decodeURL(m2.group(1));

	    	if (Validator.isNotNull(fileHandle)) {
	    		MultipartFile myFile = null;
		    	try {
		    		myFile = WebHelper.wrapFileHandleInMultipartFile(fileHandle);
		    	} catch(IOException e) {
		    		return;
		    	}
		    	if (myFile != null) {
		    		String fileName = myFile.getOriginalFilename();
			    	if (Validator.isNull(fileName)) continue;
			    	// Different repository can be specified for each file uploaded.
			    	// If not specified, use the statically selected one.  
			    	String repositoryName = RepositoryUtil.getDefaultRepositoryName();
			    	FileUploadItem fui = new FileUploadItem(FileUploadItem.TYPE_ATTACHMENT, null, myFile, repositoryName);
			    	//flag as used in markup, for further processing after files are saved
			    	fui.setMarkup(true);
			    	fui.setMarkupFieldName(fieldName);
			    	fileData.add(fui);
		    	}
	    	}
	    	//Now, replace the url with special markup version
	    	Matcher m3 = urlSrcPattern.matcher(img);
        	if (m3.find()) {
        		String fileName = WebHelper.getFileName(fileHandle);
        		try {
        			//re-encode the file name
        			URI uri = new URI(null, null, fileName, null); //encode as editor does after a modify, so always looks the same
        			fileName = uri.getRawPath();
        		} catch (Exception ex) {};
        		img = m3.replaceFirst("src=\"{{attachmentUrl: " + fileName.replace("$", "\\$") + "}}\"");
        		description.setText(m.replaceFirst(img.replace("$", "\\$"))); //remove special chars from replacement string
        		m = uploadImagePattern.matcher(description.getText());
        	}
    	}
	}

	//converts back to markup.  Would happen after modify
	public static void scanDescriptionForAttachmentFileUrls(Description description) {
		if (Validator.isNull(description.getText())) return;
    	Matcher m = v1AttachmentUrlPattern.matcher(description.getText());
    	int loopDetector = 0;
     	while (m.find()) {
    		if (loopDetector++ > 2000) {
	        	logger.error("Error processing markup [2]: " + description.getText());
    			break;
    		}
    		String fileId = "";
    		String img = m.group();
        	Matcher fieldMatcher = fileIdPattern.matcher(img);
        	if (fieldMatcher.find() && fieldMatcher.groupCount() >= 1) fileId = fieldMatcher.group(1).trim();
    		//the old url had binderId, entryId and entityType, but you could only point to a file in the same entry
        	//so these are not needed - this needs to be replaced with readFile url.
        	//This code is here to capture old urls that are lingering
	    	if (Validator.isNotNull(fileId)) {
		    	//Now, replace the url with special markup version
		    	Matcher m1 = urlSrcPattern.matcher(img);
	        	if (m1.find()) {
	        		//not sure what this specialAmp is about, but leave as is from v1
	        		img = m1.replaceFirst("src=\"{{attachmentFileId: fileId=" + fileId + "}}\"");
	        		description.setText(m.replaceFirst(img.replace("$", "\\$"))); //remove regex special char
	        		m = v1AttachmentUrlPattern.matcher(description.getText());
	        	}
	    	}
    	}

    	m = readFileImagePattern.matcher(description.getText());
    	loopDetector = 0;
    	while (m.find()) {
    		if (loopDetector++ > 2000) {
	        	logger.error("Error processing markup [2]: " + description.getText());
    			break;
    		}
    		String url = "";
    		String img = m.group(0);
        	Matcher m2 = readFilePathPattern.matcher(img);
        	
        	if (m2.find()) url = m2.group().trim();
    		String[] args = url.split(org.kablink.teaming.util.Constants.SLASH);
 
	    	if (args.length == 8) {
		    	//Now, replace the url with special markup version
		    	Matcher m1 = urlSrcPattern.matcher(img);
	        	if (m1.find()) {
	        		String fileName = args[WebUrlUtil.FILE_URL_NAME];
	 
	        		img = m1.replaceFirst("src=\"{{attachmentUrl: " + fileName.replace("$", "\\$") + "}}\"");
	        		description.setText(m.replaceFirst(img.replace("$", "\\$")));  //remove regex special char
	        		m = readFileImagePattern.matcher(description.getText());
	        	}
	    	}
    	}

    	m = attachedImagePattern.matcher(description.getText());
    	loopDetector = 0;
    	while (m.find()) {
    		if (loopDetector++ > 2000) {
	        	logger.error("Error processing markup [2]: " + description.getText());
    			break;
    		}
    		String url = "";
    		String img = m.group(0);

	    	//See if this has already been fixed up
    		Matcher m2 = attachmentUrlPattern.matcher(img);
        	if (m2.find()) continue;

	    	//Now, replace the url with special markup version
	    	Matcher m1 = urlSrcPattern.matcher(img);
        	if (m1.find()) {
        		String fileName = m1.group(1);
        		
        		//See if this is a full file spec that needs to be trimmed back to just the file name
        		String correctedFileName = fileName;
    			String ctx = SPropsUtil.getString(SPropsUtil.SSF_CTX, "/ssf");
        		Matcher m3 = httpPattern.matcher(correctedFileName);
        		if (m3.find() && ctx.equals(m3.group(1))) correctedFileName = m3.group(2);
        		String[] urlArgs = correctedFileName.split("/");
        		if (urlArgs.length >= WebUrlUtil.FILE_URL_ARG_LENGTH - 3) {
        			fileName = "";
        			for (int i = 5; i < urlArgs.length; i++) fileName += urlArgs[i];
        		}
 
        		img = m1.replaceFirst("src=\"{{attachmentUrl: " + fileName.replace("$", "\\$") + "}}\"");
        		description.setText(m.replaceFirst(img.replace("$", "\\$")));  //remove regex special char
        		m = attachedImagePattern.matcher(description.getText());
        	}
    	}
	}

	
	public static void scanDescriptionForICLinks(Description description) {
		if (Validator.isNull(description.getText())) return;
    	Matcher m = iceCoreLinkPattern.matcher(description.getText());
    	int loopDetector = 0;
    	while (m.find()) {
    		if (loopDetector++ > 2000) {
	        	logger.error("Error processing markup [2a]: " + description.getText());
    			break;
    		}
    		String linkArgs = "";
    		String link = m.group();
        	Matcher m2 = iceCoreLinkRelPattern.matcher(link);
        	if (m2.find() && m2.groupCount() >= 1) linkArgs = m2.group(1).trim().replace("$", "\\$");
    		
        	String linkText = "" ;
        	if (m.groupCount() >= 2) { linkText = m.group(2).trim().replace("$", "\\$"); }
        	int i = linkText.indexOf("</a>");
        	if (i < 0) break;
        	String titleText = linkText.substring(0, i);
        	String remainderText = linkText.substring(i+4, linkText.length());

        	if (Validator.isNotNull(linkArgs)) {
        		description.setText(m.replaceFirst("{{titleUrl: " + linkArgs.replaceAll("%2B", "+") + " text=" + Html.stripHtml(titleText) + "}}" + remainderText));
        		m = iceCoreLinkPattern.matcher(description.getText());
	    	}
    	}
	}

	public static void scanDescriptionForYouTubeLinks(Description description) {
		if (Validator.isNull(description.getText())) return;
    	Matcher m = youtubeLinkPattern.matcher(description.getText());
    	int loopDetector = 0;
    	while (m.find()) {
    		if (loopDetector++ > 2000) {
	        	logger.error("Error processing markup [2a]: " + description.getText());
    			break;
    		}
    		String linkArgs = "";
    		String link = m.group();
        	Matcher m2 = youtubeLinkRelPattern.matcher(link);
        	if (m2.find() && m2.groupCount() >= 1) linkArgs = m2.group(1).trim().replace("$", "\\$");
    		
        	String linkText = "" ;
        	if (m.groupCount() >= 2) { linkText = m.group(2).trim().replace("$", "\\$"); }

        	if (Validator.isNotNull(linkArgs)) {
        		description.setText(m.replaceFirst("{{youtubeUrl: " + linkArgs.replaceAll("%2B", "+") + "}}"));
        		m = youtubeLinkPattern.matcher(description.getText());
	    	}
    	}
	}

	public static void scanDescriptionForExportTitleUrls(Description description) {
		if (Validator.isNull(description.getText())) return;
    	//Scan the text for {{titleUrl: binderId=xxx zoneUUID=xxx title=xxx}}
		//  Remove the zoneUUID if it is the same as the current zone
		ZoneInfo zoneInfo = ExportHelper.getZoneInfo();
		StringBuffer outputBuf = new StringBuffer(description.getText());
		Matcher matcher = titleUrlPattern.matcher(outputBuf.toString());
		int loopDetector;
		matcher = titleUrlPattern.matcher(outputBuf.toString());
		if (matcher.find()) {
			loopDetector = 0;
			outputBuf = new StringBuffer();
			do {
				if (loopDetector++ > 2000) {
					logger.error("Error processing markup [5]: " + description.getText());
					return;
				}
				if (matcher.groupCount() < 2) continue;
	    		String link = matcher.group();
		    		
				String s_zoneUUID = "";
				Matcher fieldMatcher = titleUrlZoneUUIDPattern.matcher(link);
				if (fieldMatcher.find() && fieldMatcher.groupCount() >= 1) s_zoneUUID = fieldMatcher.group(1).trim();
		    	if (!s_zoneUUID.equals("") && s_zoneUUID.equals(String.valueOf(zoneInfo.getId()))) {
		    		link = link.replaceFirst("zoneUUID=" + String.valueOf(zoneInfo.getId()), "");
		    	}
    			matcher.appendReplacement(outputBuf, link.toString().replace("$", "\\$"));
	    	} while (matcher.find());
			matcher.appendTail(outputBuf);
			if (!outputBuf.toString().equals(description.getText())) description.setText(outputBuf.toString());
		}
	}

	protected interface UrlBuilder {
		public String getFileUrlByName(String fileName);
		public String getFileUrlById(String fileId);
		public String getRelativeTitleUrl(String normalizedTitle, String title);
		public String getTitleUrl(String binderId, String zoneUUID, String normalizedTitle, String title);
		public String getRootUrl();
		public String getImagesRootUrl();
		public String getRootServletUrl();
	}
	public static String markupStringReplacement(final RenderRequest req, final RenderResponse res, 
			final HttpServletRequest httpReq, final HttpServletResponse httpRes,
			final Map searchResults, String inputString, final String type) {
		UrlBuilder builder = new UrlBuilder() {
			public String getRootUrl() {
				if (httpReq != null) return WebUrlUtil.getAdapterRootURL(httpReq, httpReq.isSecure());
				if (req != null) return WebUrlUtil.getAdapterRootURL(req, req.isSecure());
				return WebUrlUtil.getAdapterRootUrl();
			}
			public String getImagesRootUrl() {
				return WebUrlUtil.getRelativeSSFContextRootURL() + "images/";
			}
			public String getRootServletUrl() {
				if (httpReq != null) return WebUrlUtil.getServletRootURL(httpReq, httpReq.isSecure());
				if (req != null) return WebUrlUtil.getServletRootURL(req, req.isSecure());
				return WebUrlUtil.getServletRootURL();
			}
			public String getFileUrlByName(String fileName) {
				if (!WebKeys.MARKUP_EXPORT.equals(type)) return WebUrlUtil.getFileUrl(WebUrlUtil.getServletRootURL(httpReq), WebKeys.ACTION_READ_FILE, searchResults, fileName);
				//need permalink
				return PermaLinkUtil.getFilePermalink(searchResults, fileName);
			}
			public String getFileUrlById(String fileId) {
				Object fileName = searchResults.get(WebUrlUtil.getFileInfoById((String)org.kablink.util.search.Constants.FILENAME_AND_ID_FIELD,fileId));
				if (fileName == null) return "";
				return getFileUrlByName(fileName.toString());

			}
			public String getRelativeTitleUrl(String normalizedTitle, String title) {
				String zoneUUID = "";
				return getTitleUrl((String)searchResults.get(org.kablink.util.search.Constants.BINDER_ID_FIELD), 
						zoneUUID, normalizedTitle, title);
			}
			public String getTitleUrl(String binderId, String zoneUUID, String normalizedTitle, String title) {
				if (WebKeys.MARKUP_EXPORT.equals(type) || res == null) {
					return PermaLinkUtil.getTitlePermalink(Long.valueOf(binderId), zoneUUID, normalizedTitle);
				}
				PortletURL portletURL = portletURL = res.createActionURL();
				portletURL.setParameter(WebKeys.URL_BINDER_ID, binderId);
				if (normalizedTitle != null && !normalizedTitle.equals("")) {
					portletURL.setParameter(WebKeys.URL_NORMALIZED_TITLE, normalizedTitle);
					portletURL.setParameter(WebKeys.ACTION, WebKeys.ACTION_VIEW_FOLDER_ENTRY);
					portletURL.setParameter(WebKeys.URL_ENTRY_PAGE_TITLE, title);
				} else {
					portletURL.setParameter(WebKeys.ACTION, WebKeys.ACTION_VIEW_FOLDER_LISTING);
				}
				return portletURL.toString();
			}
		};
		return markupStringReplacement(req, res, httpReq, httpRes, builder,
				(String)searchResults.get(org.kablink.util.search.Constants.DOCID_FIELD), (String)searchResults.get(org.kablink.util.search.Constants.ENTITY_FIELD), inputString, type);
	}
	
	public static String markupStringReplacement(final RenderRequest req, final RenderResponse res, 
			final HttpServletRequest httpReq, final HttpServletResponse httpRes,
			final DefinableEntity entity, String inputString, final String type) {
		UrlBuilder builder = new UrlBuilder() {
			public String getRootUrl() {
				if (httpReq != null) return WebUrlUtil.getAdapterRootURL(httpReq, httpReq.isSecure());
				if (req != null) return WebUrlUtil.getAdapterRootURL(req, req.isSecure());
				return WebUrlUtil.getAdapterRootUrl();
			}
			public String getImagesRootUrl() {
				return WebUrlUtil.getRelativeSSFContextRootURL() + "images/";
			}
			public String getRootServletUrl() {
				if (httpReq != null) return WebUrlUtil.getServletRootURL(httpReq, httpReq.isSecure());
				if (req != null) return WebUrlUtil.getServletRootURL(req, req.isSecure());
				return WebUrlUtil.getServletRootURL();
			}
			public String getFileUrlByName(String fileName) {
				if (!WebKeys.MARKUP_EXPORT.equals(type)) 
					return WebUrlUtil.getFileUrl(WebUrlUtil.getServletRootURL(httpReq), WebKeys.ACTION_READ_FILE, entity, fileName);
				//need permalink
				return PermaLinkUtil.getFilePermalink(entity, fileName);
			}
			public String getFileUrlById(String fileId) {
				try {
					FileAttachment fa = (FileAttachment)entity.getAttachment(fileId);
					return getFileUrlByName(fa.getFileItem().getName());
				} catch (Exception ex) {return "";}
			}
			public String getRelativeTitleUrl(String normalizedTitle, String title) {
				CustomAttribute zoneUUIDattr = entity.getCustomAttribute(Constants.ZONE_UUID_FIELD);
				String zoneUUID = "";
				Long binderId = entity.getId();
				if (EntityType.folderEntry.equals(entity.getEntityType())) binderId = entity.getParentBinder().getId();
				return getTitleUrl(binderId.toString(), 
						zoneUUID, normalizedTitle, title);
			}
			public String getTitleUrl(String binderId, String zoneUUID, String normalizedTitle, String title) {
				if (WebKeys.MARKUP_EXPORT.equals(type) || res == null) {
					return PermaLinkUtil.getTitlePermalink(Long.valueOf(binderId), normalizedTitle);
				}
				PortletURL portletURL = portletURL = res.createActionURL();
				portletURL.setParameter(WebKeys.URL_BINDER_ID, binderId);
				if (Validator.isNotNull(zoneUUID)) portletURL.setParameter(WebKeys.URL_ZONE_UUID, zoneUUID);
				if (normalizedTitle != null && !normalizedTitle.equals("")) {
					portletURL.setParameter(WebKeys.URL_NORMALIZED_TITLE, normalizedTitle);
					portletURL.setParameter(WebKeys.ACTION, WebKeys.ACTION_VIEW_FOLDER_ENTRY);
					portletURL.setParameter(WebKeys.URL_ENTRY_PAGE_TITLE, title);
				} else {
					portletURL.setParameter(WebKeys.ACTION, WebKeys.ACTION_VIEW_FOLDER_LISTING);
				}
				return portletURL.toString();
			}
		};
		return markupStringReplacement(req, res, httpReq, httpRes, builder,
				entity.getId().toString(), entity.getEntityType().name(), inputString, type);
	}
	private static String markupStringReplacement(RenderRequest req, RenderResponse res, 
			HttpServletRequest httpReq, HttpServletResponse httpRes, UrlBuilder builder, 
			String entityId, String entityType, String inputString, String type) {
		if (Validator.isNull(inputString)) return inputString;  //don't waste time
		StringBuffer outputBuf = new StringBuffer(inputString);
		
//why?		outputString = outputString.replaceAll("%20", " ");
//		outputString = outputString.replaceAll("%7B", "{");
//		outputString = outputString.replaceAll("%7D", "}");
		int loopDetector;
		try {
			Matcher matcher; //Pattern.compile("((<a[\\s]href[=\\s]\")([^\":]*)\")");
			//do first, before add hrefs
			if (type.equals(WebKeys.MARKUP_EXPORT)) {
				//tinymce stores relative urls.  If this isn't going to be used by tinymce, need to change the urls
				//This isn't true anymore, but doesn't hurt
				matcher = hrefPattern.matcher(outputBuf);
				if (matcher.find()) {
					loopDetector = 0;
					outputBuf = new StringBuffer();
					do {						
						if (loopDetector++ > 2000) {
							logger.error("Error processing markup [6]: " + inputString);
							return outputBuf.toString();
						}
						int count = matcher.groupCount();
						String link = matcher.group(3);
						String root = builder.getRootUrl();
						if (link.startsWith("../")) {
							root = root.substring(0, root.length()-1); //strip last /
							do {
								link = link.substring(3, link.length());
								root = root.substring(0, root.lastIndexOf("/"));
							} while (link.startsWith("../"));
							link = root + "/" + link; 
						} else {
							link = root + link;
						}
						matcher.appendReplacement(outputBuf, "$2" + link.replace("$", "\\$") + "\"");
						//outputString = matcher.replaceFirst("$2" + link.replace("$", "\\$") + "\"");
						//matcher = hrefPattern.matcher(outputString);
					} while (matcher.find());
					matcher.appendTail(outputBuf);
				}
			}
			
			//Replace the markup urls with real urls {{attachmentUrl: tempFileHandle}}
			matcher = attachmentUrlPattern.matcher(outputBuf);
			if (matcher.find()) {
				loopDetector = 0;
				outputBuf = new StringBuffer();
				do {
					if (loopDetector++ > 2000) {
						logger.error("Error processing markup [3]: " + inputString);
						return outputBuf.toString();
					}
					if (matcher.groupCount() >= 2) {
						String fileName = matcher.group(2);
						//remove escaping that timyMce for html escaping - get here if someone typed {{att.. }}themselves
						fileName = StringEscapeUtils.unescapeHtml(fileName);
		           		try {
							//remove escaping for urls which are left in the text after modify or add file
		        			URI uri = new URI(fileName);
		        			fileName = uri.getPath();
		        		} catch (Exception ex) {};
	
						String webUrl = builder.getFileUrlByName(fileName);
						matcher.appendReplacement(outputBuf, webUrl.replace("$", "\\$"));
					}
				} while (matcher.find());
				matcher.appendTail(outputBuf);
	    	}
	    	
	    	//Replace the markup v1 attachmentFileIds {{attachmentFileId: binderId=xxx entryId=xxx fileId=xxx entityType=xxx}}
			//with v2 urls 
			//  from the fileId, we can get the fileName and use the new URLS.
			matcher = v1AttachmentFileIdPattern.matcher(outputBuf);
			if (matcher.find()) {
				loopDetector = 0;
				outputBuf = new StringBuffer();
				do {
					if (loopDetector++ > 2000) {
						logger.error("Error processing markup [4]: " + inputString);
						return outputBuf.toString();
			    	}
					if (matcher.groupCount() >= 2) {
						String fileIds = matcher.group(2).trim();
				   		String fileId = "";
			        	Matcher fieldMatcher = fileIdPattern.matcher(fileIds);
			        	if (fieldMatcher.find() && fieldMatcher.groupCount() >= 1) fileId = fieldMatcher.group(1).trim();
			    		//the old url had binderId, entryId and entityType, but you could only point to a file in the same entry
			        	//so these are not needed - this needs to be replaced with readFile url.
			        	//This code is here to capture old urls that are lingering
				    	if (Validator.isNotNull(fileId)) {
				    		String webUrl = builder.getFileUrlById(fileId);
				    		if (webUrl == null || webUrl.equals("")) {
				    			//Not found, just build a url using just the file id
				    			webUrl = builder.getRootServletUrl() + WebKeys.SERVLET_VIEW_FILE + "?" +
				    				WebKeys.URL_FILE_VIEW_TYPE + "=" + WebKeys.FILE_VIEW_TYPE_ATTACHMENT_FILE + 
				    				"&" + WebKeys.URL_FILE_ID + "=" + fileId; 
				    		}
				    		matcher.appendReplacement(outputBuf, webUrl.replace("$", "\\$"));
				    	}
					}
				} while (matcher.find());
				matcher.appendTail(outputBuf);
			}
	    	//Replace the markup {{titleUrl}} with real urls {{titleUrl: binderId=xxx title=xxx}}
			matcher = titleUrlPattern.matcher(outputBuf.toString());
			if (matcher.find()) {
				loopDetector = 0;
				outputBuf = new StringBuffer();
				do {
					if (loopDetector++ > 2000) {
						logger.error("Error processing markup [5]: " + inputString);
						return outputBuf.toString();
					}
					if (matcher.groupCount() < 2) continue;
					String urlParts = matcher.group(2).trim();
					String s_binderId = "";
					Matcher fieldMatcher = titleUrlBinderPattern.matcher(urlParts);
					if (fieldMatcher.find() && fieldMatcher.groupCount() >= 1) s_binderId = fieldMatcher.group(1).trim();
			    		
					String s_zoneUUID = "";
					fieldMatcher = titleUrlZoneUUIDPattern.matcher(urlParts);
					if (fieldMatcher.find() && fieldMatcher.groupCount() >= 1) s_zoneUUID = fieldMatcher.group(1).trim();
			    		
					String normalizedTitle = "";
					fieldMatcher = titleUrlTitlePattern.matcher(urlParts);
					if (fieldMatcher.find() && fieldMatcher.groupCount() >= 1) normalizedTitle = fieldMatcher.group(1).trim();
			        	
					String title = "";
					fieldMatcher = titleUrlTextPattern.matcher(urlParts); //html stripped on input
					if (fieldMatcher.find() && fieldMatcher.groupCount() >= 1) title = fieldMatcher.group(1).trim();
					if (normalizedTitle.equals("")) normalizedTitle = title;
					if (title.equals("")) title = normalizedTitle;
			        	
					//build the link
		    		StringBuffer titleLink = new StringBuffer();
			    	if (type.equals(WebKeys.MARKUP_FORM)) {
			        	titleLink.append("<a class=\"ss_icecore_link\" rel=\"binderId=");
			        	titleLink.append(s_binderId);
			        	if (!s_zoneUUID.equals("")) titleLink.append(" zoneUUID=" + s_zoneUUID);
			        	titleLink.append(" title=");
			        	titleLink.append(Html.stripHtml(normalizedTitle)).append("\">");
			        	titleLink.append(title).append("</a>");
			    	} else if (type.equals(WebKeys.MARKUP_VIEW)){
			    		String webUrl = builder.getTitleUrl(s_binderId, s_zoneUUID, 
			    				WebHelper.getNormalizedTitle(normalizedTitle), title);
			    		String showInParent = "false";
			    		if (normalizedTitle == null || normalizedTitle.equals("")) showInParent = "true";
			    		titleLink.append("<a href=\"").append(webUrl);
			    		titleLink.append("\" onClick=\"if (self.ss_openTitleUrl) return self.ss_openTitleUrl(this, "+showInParent+");\">");
			    		titleLink.append("<span class=\"ss_title_link\">").append(title).append("</span></a>");
			    	} else {
			    		String webUrl = builder.getTitleUrl(s_binderId, s_zoneUUID,
			    				WebHelper.getNormalizedTitle(normalizedTitle), title);
			    		titleLink.append("<a href=\"").append(webUrl).append("\">").append(title).append("</a>");
			    		
			    	}
	    			matcher.appendReplacement(outputBuf, titleLink.toString().replace("$", "\\$"));
		    	} while (matcher.find());
				matcher.appendTail(outputBuf);
			}
		    	
	    	//Replace the markup {{youTubeUrl}} with real urls {{youTubeUrl: url=xxx width=www height=hhh}}
			matcher = youtubeUrlPattern.matcher(outputBuf.toString());
			if (matcher.find()) {
				loopDetector = 0;
				outputBuf = new StringBuffer();
				do {
					if (loopDetector++ > 2000) {
						logger.error("Error processing markup [5]: " + inputString);
						return outputBuf.toString();
					}
					if (matcher.groupCount() < 2) continue;
					String urlParts = matcher.group(2).trim();
					String s_url = "";
					Matcher fieldMatcher = youtubeUrlUrlPattern.matcher(urlParts);
					if (fieldMatcher.find() && fieldMatcher.groupCount() >= 1) s_url = fieldMatcher.group(1).trim();
			    		
					String s_width = "";
					fieldMatcher = youtubeUrlWidthPattern.matcher(urlParts);
					if (fieldMatcher.find() && fieldMatcher.groupCount() >= 1) s_width = fieldMatcher.group(1).trim();
			    		
					String s_height = "";
					fieldMatcher = youtubeUrlHeightPattern.matcher(urlParts);
					if (fieldMatcher.find() && fieldMatcher.groupCount() >= 1) s_height = fieldMatcher.group(1).trim();
			    		
					//build the link
		    		StringBuffer titleLink = new StringBuffer();
			    	if (type.equals(WebKeys.MARKUP_FORM)) {
			        	titleLink.append("<a class=\"ss_youtube_link\" rel=\"url=");
			        	titleLink.append(s_url);
			        	if (!s_width.equals("")) titleLink.append(" width=" + s_width);
			        	if (!s_height.equals("")) titleLink.append(" height=" + s_height);
			        	titleLink.append("\" style=\"padding:12px 12px; background:url(");
			        	titleLink.append(builder.getImagesRootUrl()).append("pics/media.gif) no-repeat center;\">");
			        	titleLink.append("&nbsp;</a>");
			        	titleLink.append("</a>");
			    	} else if (s_url.startsWith("http://www.youtube.com/")) {
			    		if (checkIfMobile(req, httpReq)) {
				    		titleLink.append("<div>\n");
				    		titleLink.append("<a href=\"");
				    		titleLink.append(s_url.replaceFirst("www.youtube.com", "m.youtube.com"));
				    		titleLink.append("\"><img width=\"60\" height=\"38\" src=\"");
				    		titleLink.append(builder.getImagesRootUrl());
				    		titleLink.append("pics/yt_powered_by_black.png").append("\"/></a>\n");
				    		titleLink.append("</div>\n");
			    			
			    		} else {
				    		/*
								<div id="ytapiplayer">
	    							You need Flash player 8+ and JavaScript enabled to view this video.
	  							</div>
	                            <script type="text/javascript">
								    var params = { allowScriptAccess: "always" };
								    var atts = { id: "myytplayer" };
								    swfobject.embedSWF("http://www.youtube.com/v/VIDEO_ID?enablejsapi=1&playerapiid=ytplayer", 
								                       "ytapiplayer", "425", "356", "8", null, null, params, atts);
								</script>
				    		 */
				    		Integer id = ++youtubeDivId;
				    		if (youtubeDivId > 1000000) youtubeDivId = 0;
				    		titleLink.append("<div id=\"ss_videoDiv"+id.toString()+"\">\n");
				    		titleLink.append("<div id=\"ytapiplayer"+id.toString()+"\">\n");
				    		titleLink.append("");
				    		titleLink.append("</div>\n");
				    		titleLink.append("<div>\n");
				    		titleLink.append("<a href=\"" + s_url + "\"><img width=\"60\" height=\"38\" src=\"");
				    		titleLink.append(builder.getImagesRootUrl());
				    		titleLink.append("pics/yt_powered_by_black.png").append("\"/></a>\n");
				    		titleLink.append("</div>\n");
				    		titleLink.append("</div>\n");
				    		titleLink.append("<script type=\"text/javascript\">\n");
				    		titleLink.append("var params = { allowScriptAccess: \"always\" };\n");
				    		titleLink.append("var atts = { id: \"myytplayer\" };\n");
				    		titleLink.append("swfobject.embedSWF(\"").append(s_url.replace("?v=", "/v/"));
				    		titleLink.append("?enablejsapi=1&playerapiid=ytplayer\",");
				    		titleLink.append(" \"ytapiplayer"+id.toString()+"\", \"").append(s_width).append("\", ");
				    		titleLink.append("\"").append(s_height).append("\", \"8\", null, null, params, atts);\n");
				    		titleLink.append("ss_createSpannedAreaObj(\"ss_videoDiv"+id.toString()+"\");\n");
				    		titleLink.append("</script>\n");
			    		}
			    	} else {
			        	titleLink.append("<a target=\"_blank\" src=\"");
			        	titleLink.append(s_url);
			        	titleLink.append("\">");
			        	titleLink.append(s_url).append("</a>");
			    	}
	    			matcher.appendReplacement(outputBuf, titleLink.toString().replace("$", "\\$"));
		    	} while (matcher.find());
				matcher.appendTail(outputBuf);
			}
		    	
	    	//When viewing the string, replace the markup title links with real links    [[page title]]
			if ((entityType.equals(EntityType.folderEntry.name()) || entityType.equals(EntityType.folder.name())) && 
					(type.equals(WebKeys.MARKUP_VIEW) || type.equals(WebKeys.MARKUP_EXPORT))) {
		    	matcher  = pageTitleUrlTextPattern.matcher(outputBuf);
				if (matcher.find()) {
					loopDetector = 0;
					outputBuf = new StringBuffer();
			    	do {
			    		if (loopDetector++ > 2000) {
				        	logger.error("Error processing markup [6]: " + inputString);
			    			return outputBuf.toString();
			    		}
			    		//Get the title
			    		String title = matcher.group(2).trim();
			    		String normalizedTitle = WebHelper.getNormalizedTitle(title);
			    		if (Validator.isNotNull(normalizedTitle)) {
			    			//Build the url to that entry
				    		StringBuffer titleLink = new StringBuffer();				
			    			String webUrl = builder.getRelativeTitleUrl(normalizedTitle, title);
			    			if (type.equals(WebKeys.MARKUP_VIEW)) {
					    		String showInParent = "false";
					    		if (normalizedTitle == null || normalizedTitle.equals("")) showInParent = "true";
			    				titleLink.append("<a href=\"").append(webUrl);
			    				titleLink.append("\" onClick=\"if (self.ss_openTitleUrl) return self.ss_openTitleUrl(this, "+showInParent+");\">");
			    				titleLink.append("<span class=\"ss_title_link\">").append(title).append("</span></a>");
			    			} else {
			    				titleLink.append("<a href=\"").append(webUrl).append("\">").append(title).append("</a>");
			    				
			    			}
					    	//use substring so don't have to parse $ out of replacement string
			    			matcher.appendReplacement(outputBuf, titleLink.toString().replace("$", "\\$"));
			    		}
					} while (matcher.find());
					matcher.appendTail(outputBuf);
				}
			}
		} catch(Exception e) {
			logger.error("Error processing markup [7]: " + inputString, e);
			return inputString;
		}
     	return outputBuf.toString();
	}
	
	//Routine to fix up descriptios before exporting them
	public static String markupStringReplacementForExport(String inputString) {
    	//Fixup the markup {{titleUrl}} with zoneUUID {{titleUrl: binderId=xxx zoneUUID=xxx title=xxx text=xxx}}
		StringBuffer outputBuf = new StringBuffer(inputString);
		Matcher matcher = titleUrlPattern.matcher(outputBuf.toString());
		int loopDetector;
		if (matcher.find()) {
			loopDetector = 0;
			outputBuf = new StringBuffer();
			do {
				if (loopDetector++ > 2000) {
					logger.error("Error processing markup [5]: " + inputString);
					return outputBuf.toString();
				}
				if (matcher.groupCount() < 2) continue;
				String urlParts = matcher.group(2).trim();
				String s_binderId = "";
				Matcher fieldMatcher = titleUrlBinderPattern.matcher(urlParts);
				if (fieldMatcher.find() && fieldMatcher.groupCount() >= 1) s_binderId = fieldMatcher.group(1).trim();
		    		
				String s_zoneUUID = "";
				fieldMatcher = titleUrlZoneUUIDPattern.matcher(urlParts);
				if (fieldMatcher.find() && fieldMatcher.groupCount() >= 1) s_zoneUUID = fieldMatcher.group(1).trim();
				if (s_zoneUUID.equals("")) {
					ZoneInfo zoneInfo = ExportHelper.getZoneInfo();
					s_zoneUUID = String.valueOf(zoneInfo.getId());
				}
		    		
				String normalizedTitle = "";
				fieldMatcher = titleUrlTitlePattern.matcher(urlParts);
				if (fieldMatcher.find() && fieldMatcher.groupCount() >= 1) normalizedTitle = fieldMatcher.group(1).trim();
		        	
				String title = "";
				fieldMatcher = titleUrlTextPattern.matcher(urlParts); //html stripped on input
				if (fieldMatcher.find() && fieldMatcher.groupCount() >= 1) title = fieldMatcher.group(1).trim();
		        	
				//rebuild the link
	    		String titleLink = "{{titleUrl: binderId=" + s_binderId + " zoneUUID=" + s_zoneUUID + 
	    			" title=" + normalizedTitle + " text=" + Html.stripHtml(title) + "}}";
    			matcher.appendReplacement(outputBuf, titleLink.replace("$", "\\$"));
	    	} while (matcher.find());
			matcher.appendTail(outputBuf);
		}
     	return outputBuf.toString();
	}
	
	//Routine to fix up descriptios before exporting them
	public static String markupStringReplacementForMashupCanvasExport(String inputString) {
		return DefinitionHelper.fixupMashupCanvasForExport(inputString);
	}

	//Routine to split a body of text into sections
	public static List markupSplitBySection(String body) {
		List bodyParts = new ArrayList();
    	Matcher m0 = sectionPattern.matcher(body);
    	if (m0.find()) {
			Map part = new HashMap();
			part.put("prefix", body.substring(0, m0.start(0)));
			bodyParts.add(part);
			body = body.substring(m0.start(0), body.length());
    	}
    	
    	int sectionNumber = 0;
    	Matcher m1 = sectionPattern.matcher(body);
    	int loopDetector = 0;
    	while (m1.find()) {
    		if (loopDetector++ > 2000) {
	        	logger.error("Error processing markup [6]: " + body);
    			return bodyParts;
    		}
 			Map part = new HashMap();
    		//Get the section title
    		String title = m1.group(2).trim();
    		if (title == null) title = "";
			
    		part.put("sectionTitle", title);
    		part.put("sectionNumber", String.valueOf(sectionNumber));
    		
			String equalSigns = m1.group(1).trim();
			int sectionDepth = Integer.valueOf(equalSigns.length());
			if (sectionDepth > 4) sectionDepth = 4;
			sectionDepth--;
			part.put("sectionTitleClass", "ss_sectionHeader" + String.valueOf(sectionDepth));
			
			body = body.substring(m1.end(), body.length());
	    	Matcher m2 = sectionPattern.matcher(body);
	    	if (m2.find()) {
				part.put("sectionBody", body.substring(0, m2.start(0)));
				body = body.substring(m2.start(0), body.length());
	    	} else {
	    		part.put("sectionBody", body);
	    	}
	    	part.put("sectionText", m1.group(1) + m1.group(2) + m1.group(3) + part.get("sectionBody"));
			bodyParts.add(part);
			m1 = sectionPattern.matcher(body);
    		
			sectionNumber++;
		}
		return bodyParts;
	}
	//Routine to split a body of text into sections
	public static String markupSectionsReplacement(String body) {
		List<Map> bodyParts = new ArrayList();
    	int loopDetector = 0;
    	Matcher m0 = sectionPattern.matcher(body);
    	if (m0.find()) {
			Map part = new HashMap();
			part.put("prefix", body.substring(0, m0.start(0)));
			bodyParts.add(part);
			body = body.substring(m0.start(0), body.length());
    	}
    	
    	int sectionNumber = 0;
    	Matcher m1 = sectionPattern.matcher(body);
    	while (m1.find()) {
    		if (loopDetector++ > 2000) {
	        	logger.error("Error processing markup [6]: " + body);
    			return body;
    		}
 			Map part = new HashMap();
    		//Get the section title
    		String title = m1.group(2).trim();
    		if (title == null) title = "";
			
    		part.put("sectionTitle", title);
    		part.put("sectionNumber", String.valueOf(sectionNumber));
    		
			String equalSigns = m1.group(1).trim();
			int sectionDepth = Integer.valueOf(equalSigns.length());
			if (sectionDepth > 4) sectionDepth = 4;
			sectionDepth--;
			part.put("sectionTitleClass", "ss_sectionHeader" + String.valueOf(sectionDepth));
			
			body = body.substring(m1.end(), body.length());
	    	Matcher m2 = sectionPattern.matcher(body);
	    	if (m2.find()) {
				part.put("sectionBody", body.substring(0, m2.start(0)));
				body = body.substring(m2.start(0), body.length());
	    	} else {
	    		part.put("sectionBody", body);
	    	}
	    	part.put("sectionText", m1.group(1) + m1.group(2) + m1.group(3) + part.get("sectionBody"));
			bodyParts.add(part);
			m1 = sectionPattern.matcher(body);
    		
			sectionNumber++;
		}
    	String result = "";
    	if (bodyParts.isEmpty()) return body;
    	for (Map bodyPart : bodyParts) {
    		result += "<div>";
    		if (bodyPart.containsKey("prefix")) result += bodyPart.get("prefix");
    		if (bodyPart.containsKey("sectionTitle")) {
	    		result += "<div><span ";
	    		if (bodyPart.containsKey("sectionTitleClass")) 
	    			result += "class=\"" + bodyPart.get("sectionTitleClass") + "\"";
	    		result += ">";
	    		result += bodyPart.get("sectionTitle");
	    		result += "</span></div>";
    		}
    		if (bodyPart.containsKey("sectionBody")) result += bodyPart.get("sectionBody");
    		result += "</div>\n";
    	}
		return result;
	}
	
	public static boolean checkIfMobile(final RenderRequest req, final HttpServletRequest httpReq) {
		if (req != null) {
			HttpServletRequest hr = WebHelper.getHttpServletRequest(req);
			if (BrowserSniffer.is_iphone(hr) || 
					BrowserSniffer.is_blackberry(hr) || 
					BrowserSniffer.is_wml(hr)) return true;
			else return false;
		} else if (httpReq != null) {
			if (BrowserSniffer.is_iphone(httpReq) || 
					BrowserSniffer.is_blackberry(httpReq) || 
					BrowserSniffer.is_wml(httpReq)) return true;
			else return false;
		} else {
			return false;
		}
	}
	
	public static void fixupImportedLinks(final DefinableEntity entity, final Long originalEntityId,
			final Map<Long, Long> binderIdMap, final Map<Long, Long> entryIdMap) {
		final Map<String,Object> data = new HashMap<String,Object>(); // Changed data
			
		DefinitionModule.DefinitionVisitor visitor = new DefinitionModule.DefinitionVisitor() {
			public void visit(Element entityElement, Element flagElement, Map args) {
				//Get the type of this element
				String type = entityElement.attributeValue("name", "");
				Element nameProperty = (Element)entityElement.selectSingleNode("./properties/property[@name='name']");
				if (nameProperty == null) return;
				String attrName = nameProperty.attributeValue("value", "");
				if (attrName.equals("")) return;
				//Scan description and htmlEditorTextarea elements for "titleUrl" links to fix 
				Description description = null;
				if (type.equals("description")) {
					description = entity.getDescription();
				} else if (type.equals("htmlEditorTextarea")) {
					description = (Description)entity.getCustomAttribute(attrName).getValue();
				}
				
				boolean dataChanged = false;
				if (description != null && !Validator.isNull(description.getText())) {
			    	//Scan the text for {{titleUrl: binderId=xxx zoneUUID=xxx title=xxx}}
					//  Remove the zoneUUID if it is the same as the current zone
					StringBuffer outputBuf = new StringBuffer(description.getText());
					Matcher matcher = titleUrlPattern.matcher(outputBuf.toString());
					int loopDetector;
					matcher = titleUrlPattern.matcher(outputBuf.toString());
					if (matcher.find()) {
						loopDetector = 0;
						outputBuf = new StringBuffer();
						do {
							if (loopDetector++ > 2000) {
								logger.error("Error processing markup [5]: " + description.getText());
								break;
							}
							if (matcher.groupCount() < 2) continue;
				    		String link = matcher.group();
					    		
							String s_binderId = "";
							Matcher fieldMatcher = titleUrlBinderPattern.matcher(link);
							if (fieldMatcher.find() && fieldMatcher.groupCount() >= 1) s_binderId = fieldMatcher.group(1).trim();
					    	if (!s_binderId.equals("")) {
					    		Long sourceBinderId = Long.valueOf(s_binderId);
					    		if (binderIdMap.containsKey(sourceBinderId) && 
					    				!binderIdMap.get(sourceBinderId).toString().equals(s_binderId)) {
					    			link = link.replaceFirst("binderId=" + s_binderId + " ", 
					    					"binderId=" + binderIdMap.get(sourceBinderId).toString() + " ");
					    		}
					    	}
							String s_zoneUUID = "";
							fieldMatcher = titleUrlZoneUUIDPattern.matcher(link);
							if (fieldMatcher.find() && fieldMatcher.groupCount() >= 1) 
								s_zoneUUID = fieldMatcher.group(1).trim();
					    	if (!s_zoneUUID.equals("")) {
					    		link = link.replaceFirst("zoneUUID=" + s_zoneUUID, "");
					    	}
			    			matcher.appendReplacement(outputBuf, link.toString().replace("$", "\\$"));
				    	} while (matcher.find());
						matcher.appendTail(outputBuf);
						if (!outputBuf.toString().equals(description.getText())) {
							description.setText(outputBuf.toString());
							dataChanged = true;
						}
					}
					
					//Scan for permalinks to fix up
					//TODO add code here
					
				}
				
				//Save any changes to the description
				if (dataChanged) {
					data.put(attrName, description.getText()); 
				}
				
				//Scan for landing pages
				if (type.equals("mashupCanvas") && entity.getCustomAttributes().containsKey(attrName)) {
					String mashup = (String)entity.getCustomAttribute(attrName).getValue();
					if (mashup != null && !mashup.equals("")) {
						String newMashup = DefinitionHelper.fixupMashupCanvasForImport(mashup, binderIdMap, entryIdMap);
						if (!mashup.equals(newMashup)) {
							data.put(attrName, newMashup); 
						}
					}
				}
			}
			
			public String getFlagElementName() {
				return "export";
			}
		};
		definitionModule.walkDefinition(entity, visitor, null);
		if (!data.isEmpty()) {
			//Save any changes
			if (EntityType.folderEntry.equals(entity.getEntityType())) {
				try {
					folderModule.modifyEntry(entity.getParentBinder().getId(), entity.getId(), 
						new MapInputData(data), null, null, null, null);
				} catch(Exception e) {
					logger.error(e.getLocalizedMessage());
				}
			} else if (EntityType.workspace.equals(entity.getEntityType()) ||
					EntityType.folder.equals(entity.getEntityType())) {
				try {
					binderModule.modifyBinder(entity.getId(), new MapInputData(data), null, null, null);
				} catch(Exception e) {
					logger.error(e.getLocalizedMessage());
				}
			}
		}
	}
}
