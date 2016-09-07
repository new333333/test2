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
package org.kablink.teaming.web.util;

import java.io.IOException;
import java.net.URI;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.portlet.PortletURL;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.document.DateTools;
import org.dom4j.Document;
import org.dom4j.Element;
import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.dao.ProfileDao;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.CustomAttribute;
import org.kablink.teaming.domain.DefinableEntity;
import org.kablink.teaming.domain.Definition;
import org.kablink.teaming.domain.Description;
import org.kablink.teaming.domain.EntityIdentifier;
import org.kablink.teaming.domain.Event;
import org.kablink.teaming.domain.FileAttachment;
import org.kablink.teaming.domain.Principal;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.domain.ZoneInfo;
import org.kablink.teaming.domain.EntityIdentifier.EntityType;
import org.kablink.teaming.module.binder.BinderModule;
import org.kablink.teaming.module.definition.DefinitionModule;
import org.kablink.teaming.module.folder.FolderModule;
import org.kablink.teaming.module.shared.MapInputData;
import org.kablink.teaming.portletadapter.AdaptedPortletURL;
import org.kablink.teaming.repository.RepositoryUtil;
import org.kablink.teaming.search.BasicIndexUtils;
import org.kablink.teaming.util.AbstractAllModulesInjected;
import org.kablink.teaming.util.FileUploadItem;
import org.kablink.teaming.util.NLT;
import org.kablink.teaming.util.SPropsUtil;
import org.kablink.teaming.util.SpringContextUtil;
import org.kablink.teaming.web.WebKeys;
import org.kablink.util.BrowserSniffer;
import org.kablink.util.Html;
import org.kablink.util.Http;
import org.kablink.util.Validator;
import org.kablink.util.search.Constants;
import org.springframework.web.multipart.MultipartFile;


/**
 * ?
 * 
 * @author phurley@novell.com
 */
@SuppressWarnings({"unchecked","unused"})
public class MarkupUtil {
	protected static Log logger = LogFactory.getLog(MarkupUtil.class);
	protected final static Pattern mceSrcPattern = Pattern.compile( "(mce_src=\")([^\"]*)(\")", Pattern.CASE_INSENSITIVE );
	//From Doc: All of the state involved in performing a match resides in the matcher, so many matchers can share the same pattern. 
	// Fix for bug 727558, uploadImagePattern was changed to be case insensitive.  In IE, the tinyMCE editor is adding <IMG instead of the normal <img
	// the Pattern.CASE_INSENSITIVE parameter was added to all the Patter.compile() calls.  Some of these calls
	// already had this parameter.
	protected final static Pattern uploadImagePattern = Pattern.compile("(<img[^>]*\\ssrc\\s*=\\s*\"[^{}\"]*viewType=ss_viewUploadFile[^>]*>)", Pattern.CASE_INSENSITIVE);
	protected final static Pattern urlSrcPattern = Pattern.compile("\\ssrc\\s*=\\s*\"([^{}\"]*)\"", Pattern.CASE_INSENSITIVE );
	protected final static String uploadImageViewTypePattern = "viewType=ss_viewUploadFile(&amp%3b)?";
	
	protected final static Pattern fileIdPattern = Pattern.compile("fileId=([^\\&\"]*)", Pattern.CASE_INSENSITIVE );
	protected final static Pattern binderIdPattern = Pattern.compile("binderId=([^\\&\"]*)", Pattern.CASE_INSENSITIVE );
	protected final static Pattern entryIdPattern = Pattern.compile("entryId=([^\\&\"]*)", Pattern.CASE_INSENSITIVE );
	protected final static Pattern entityTypePattern = Pattern.compile("entityType=([^\\&\"]*)", Pattern.CASE_INSENSITIVE );
	
	protected final static Pattern v1AttachmentUrlPattern = Pattern.compile("(<img [^>]*src\\s*=\\s*\"[^>]*viewType=ss_viewAttachmentFile[^>]*>)", Pattern.CASE_INSENSITIVE );
	protected final static Pattern readFileImagePattern = Pattern.compile("(<img [^>]*src\\s*=\\s*\"[^>]*/readFile/[^>]*>)", Pattern.CASE_INSENSITIVE );
	protected final static Pattern readFilePathPattern = Pattern.compile("/readFile/[^\"]*", Pattern.CASE_INSENSITIVE );
	protected final static Pattern attachedImagePattern = Pattern.compile("(<img [^>]*class\\s*=\\s*\"\\s*ss_addimage_att\\s*\"[^>]*>)", Pattern.CASE_INSENSITIVE );
	
	protected final static Pattern iceCoreLinkPattern = Pattern.compile("(<a [^>]*class\\s*=\\s*\"*\\s*ss_icecore_link\\s*\"*[^>]*>)(.*)", Pattern.CASE_INSENSITIVE|Pattern.DOTALL);
	protected final static Pattern iceCoreLinkRelPattern = Pattern.compile("rel\\s*=\\s*\"([^\"]*)", Pattern.CASE_INSENSITIVE );
	protected final static Pattern iceCoreLinkAPattern = Pattern.compile("</a>", Pattern.CASE_INSENSITIVE|Pattern.DOTALL);
	
	protected final static Pattern youtubeLinkPattern = Pattern.compile("(<a [^>]*class\\s*=\\s*\"\\s*ss_youtube_link\\s*\"[^>]*>)([^<]*)</a>", Pattern.CASE_INSENSITIVE|Pattern.DOTALL);
	protected final static Pattern youtubeLinkRelPattern = Pattern.compile("rel=\\s*\"([^\"]*)", Pattern.CASE_INSENSITIVE );
	
	protected final static Pattern permaLinkUrlPattern = Pattern.compile("(href\\s*=\\s*[\"']https?://[^\\s\"'>]*/ssf/a/c/[^\\s\"'>]*/action/view_permalink/[^\\s\"'>]*)", Pattern.CASE_INSENSITIVE );
	protected final static Pattern permaLinkHrefPattern = Pattern.compile("(href\\s*=\\s*[\"'])https?://[^\\s]*/ssf/a/c/[^\\s]*/action/view_permalink/[^\\s\"']*", Pattern.CASE_INSENSITIVE );
	protected final static Pattern permaLinkEntityTypePattern = Pattern.compile("/entityType/([^\\s\"'/]*)", Pattern.CASE_INSENSITIVE );
	protected final static Pattern permaLinkEntryIdPattern = Pattern.compile("/entryId/([0-9]*)", Pattern.CASE_INSENSITIVE );
	protected final static Pattern permaLinkEntryTitlePattern = Pattern.compile("/title/([^\\s/]*)", Pattern.CASE_INSENSITIVE );
	protected final static Pattern permaLinkBinderIdPattern = Pattern.compile("/binderId/([0-9]*)", Pattern.CASE_INSENSITIVE );
	protected final static Pattern permaLinkZoneUUIDPattern = Pattern.compile("/zoneUUID/([^\\s\"'/]*)", Pattern.CASE_INSENSITIVE );

	protected final static Pattern m_imgAttachmentUrlPattern = Pattern.compile( "((<img[^>]*)(src=\"\\{\\{attachmentUrl: ([^}]*)\\}\\})([^>]*>))", Pattern.CASE_INSENSITIVE );
	protected final static Pattern m_dataMceSrcPattern = Pattern.compile( "((data-mce-src=\")([^\"]*))", Pattern.CASE_INSENSITIVE );

	protected final static Pattern attachmentUrlPattern = Pattern.compile("(cid:\\{\\{attachmentUrl: ([^}]*)\\}\\})", Pattern.CASE_INSENSITIVE );
	protected final static Pattern attachmentUrlPattern2 = Pattern.compile("(cid:%7b%7battachmentUrl:%20(.*?)%7d%7d)", Pattern.CASE_INSENSITIVE );
	protected final static Pattern v2AttachmentUrlPattern = Pattern.compile("(?<!cid:)(\\{\\{attachmentUrl: ([^}]*)\\}\\})", Pattern.CASE_INSENSITIVE );
	protected final static Pattern v1AttachmentFileIdPattern = Pattern.compile("(\\{\\{attachmentFileId: ([^}]*)\\}\\})", Pattern.CASE_INSENSITIVE );
	protected final static Pattern titleUrlPattern = Pattern.compile("(cid:\\{\\{titleUrl: ([^\\}]*)\\}\\})", Pattern.CASE_INSENSITIVE );
	protected final static Pattern v2TitleUrlPattern = Pattern.compile("(?<!cid:)(\\{\\{titleUrl: ([^\\}]*)\\}\\})", Pattern.CASE_INSENSITIVE );
	protected final static Pattern titleUrlBinderPattern = Pattern.compile("binderId=([^\\s]*)", Pattern.CASE_INSENSITIVE );
	protected final static Pattern titleUrlBinderPattern2 = Pattern.compile("binderId%3d([^\\s]*)", Pattern.CASE_INSENSITIVE );
	protected final static Pattern titleUrlZoneUUIDPattern = Pattern.compile("zoneUUID=([^\\s]*)", Pattern.CASE_INSENSITIVE );
	protected final static Pattern titleUrlZoneUUIDPattern2 = Pattern.compile("zoneUUID%3d([^\\s]*)", Pattern.CASE_INSENSITIVE );
	protected final static Pattern titleUrlTitlePattern = Pattern.compile("title=([^\\s]*)", Pattern.CASE_INSENSITIVE );
	protected final static Pattern titleUrlTitlePattern2 = Pattern.compile("title%3d([^\\s]*)", Pattern.CASE_INSENSITIVE );
	protected final static Pattern titleUrlTextPattern = Pattern.compile("text=(.*)$", Pattern.CASE_INSENSITIVE );
	protected final static Pattern titleUrlTextPattern2 = Pattern.compile("text%3d(.*)$", Pattern.CASE_INSENSITIVE );
	protected final static Pattern youtubeUrlPattern = Pattern.compile("(\\{\\{youtubeUrl: ([^\\}]*)\\}\\})", Pattern.CASE_INSENSITIVE );
	protected final static Pattern youtubeUrlUrlPattern = Pattern.compile("url(?:=|%3d)([^\\s]*)", Pattern.CASE_INSENSITIVE );
	protected final static Pattern youtubeUrlWidthPattern = Pattern.compile("width(?:=|%3d)([^\\s]*)", Pattern.CASE_INSENSITIVE );
	protected final static Pattern youtubeUrlHeightPattern = Pattern.compile("height(?:=|%3d)([^\\s]*)", Pattern.CASE_INSENSITIVE );
	protected final static Pattern hrefPattern = Pattern.compile("((<a[\\s]href[=\\s]\")([^\":]*)\")", Pattern.CASE_INSENSITIVE );
	protected final static Pattern pageTitleUrlTextPattern = Pattern.compile("(\\[\\[([^\\]]*)\\]\\])");
	protected final static Pattern sectionPattern =Pattern.compile("(<p>)?(==[=]*)([^=]+)(==[=]*)(</p>)?", Pattern.CASE_INSENSITIVE );	// See comments below regarding Bugzilla 692804.
	protected final static Pattern httpPattern =Pattern.compile("^https*://[^/]*(/[^/]*)/s/readFile/(.*)$", Pattern.CASE_INSENSITIVE );
	protected static Integer youtubeDivId = 0;

	protected final static Pattern vibeFunctionPattern = Pattern.compile("(cid:\\{\\{vibe:([^\\}]*)\\}\\})", Pattern.CASE_INSENSITIVE );
	protected final static Pattern v2VibeFunctionPattern = Pattern.compile("(?<!cid:)(\\{\\{vibe:([^\\}]*)\\}\\})", Pattern.CASE_INSENSITIVE );

	private static BinderModule binderModule;
	private static FolderModule folderModule;
	private static DefinitionModule definitionModule;

	private static void initializeBeans() {
		binderModule = (BinderModule) SpringContextUtil.getBean("binderModule");
		folderModule = (FolderModule) SpringContextUtil.getBean("folderModule");
		definitionModule = (DefinitionModule) SpringContextUtil.getBean("definitionModule");
	}

	private static BinderModule getBinderModule() {
		if (binderModule==null) {
			initializeBeans();
		}
		return binderModule;
	}

	private static DefinitionModule getDefinitionModule() {
		if (definitionModule==null) {
			initializeBeans();
		}
		return definitionModule;
	}

	private static FolderModule getFolderModule() {
		if (folderModule==null) {
			initializeBeans();
		}
		return folderModule;
	}

	/**
	 * Parse a description looking for uploaded file references
	 * 
	 * @param Description
	 * @param File
	 * @return 
	 */
	public static void scanDescriptionForUploadFiles(Description description, String fieldName, List fileData) {
		try {
			description.setText(replaceAttachmentUrlMacroForUploadFiles(description.getText(), fieldName, fileData));
		} catch (IOException e) {
		}
	}

	public static String replaceAttachmentUrlMacroForUploadFiles(String text, String fieldName, List fileData) throws IOException {
		if (Validator.isNotNull(text)) {
			Matcher m = uploadImagePattern.matcher(text);
			int loopDetector = 0;
			while (m.find()) {
				if (loopDetector++ > 2000) {
					logger.error("Error processing markup [1]: " + text);
					break;
				}
				String fileHandle = "";
				String img = m.group();
				Matcher m2 = fileIdPattern.matcher(img);
				if (m2.find() && m2.groupCount() >= 1) fileHandle = Http.decodeURL(m2.group(1));

				if (Validator.isNotNull(fileHandle)) {
					MultipartFile myFile = WebHelper.wrapFileHandleInMultipartFile(fileHandle);
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
					} catch (Exception ex) {
					}
					;
					img = m3.replaceFirst(" src=\"cid:{{attachmentUrl: " + fileName.replace("$", "\\$") + "}}\"");

					img = img.replaceAll(uploadImageViewTypePattern, "");

					text = m.replaceFirst(img.replace("$", "\\$")); //remove special chars from replacement string
					m = uploadImagePattern.matcher(text);
				}
			}
    	}
		return text;
	}

	//converts back to markup.  Would happen after modify
	public static void scanDescriptionForAttachmentFileUrls(Description description) {
		description.setText(replaceAllAttachmentFileUrls(description.getText()));
	}

	static String replaceAllAttachmentFileUrls(String text) {
		if (Validator.isNotNull(text)) {
			text = replaceV1AttachmentFileUrls(text);
			text = replaceReadFileAttachmentFileUrls(text);
			text = replaceAttachedImageAttachmentFileUrls(text);
		}
		return text;
	}

	static String replaceAttachedImageAttachmentFileUrls(String text) {
		if (Validator.isNotNull(text)) {
			Matcher m = attachedImagePattern.matcher(text);
			int loopDetector = 0;
			while (m.find()) {
				if (loopDetector++ > 2000) {
					logger.error("Error processing markup [2.2]: " + text);
					break;
				}
				String url = "";
				String img = m.group(0);
				String origImg;

				origImg = img;

				//See if this has already been fixed up
				Matcher m2 = attachmentUrlPattern.matcher(img);
				if (m2.find()) continue;

				//Now, replace the url with special markup version
				Matcher m1 = urlSrcPattern.matcher(img);
				if (m1.find()) {
					String fileName = m1.group(1);
					String markedUpImg;
					String desc;
					int start;
					int end;

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

					// Change the text from <img class="ss_addimage_att src="some file name" alt=" " />
					// to <img class="ss_addimage_att src="{{attachmentUrl: some file name"}}" alt=" " />
					img = m1.replaceFirst(" src=\"cid:{{attachmentUrl: " + fileName.replace("$", "\\$") + "}}\"");
					markedUpImg = img.replace("$", "\\$");    // remove regex special char
					text = text.replaceFirst(origImg, markedUpImg);

					// Start searching after the <img class="ss_addimage_att" src="{{attachmentUrl: some name}}" alt=" ">
					// we just added.
					start = text.lastIndexOf(markedUpImg) + markedUpImg.length();
					end = text.length();

					m = attachedImagePattern.matcher(text);
					m.region(start, end);
				}
			}
		}
		return text;
	}

	static String replaceReadFileAttachmentFileUrls(String text) {
		// Replace all instances of:
		// <img class=" ss_addimage_att " src="some-file-name" alt=" " data-mce-src="http://jwootton3.provo.novell.com:8080/ssf/s/readFile/workspace/1/-/1407854576062/last/some-file-name">
		// with:
		// <img class=" ss_addimage_att " src="{{attachmentUrl: some-file-name}}" alt=" " data-mce-src="http://jwootton3.provo.novell.com:8080/ssf/s/readFile/workspace/1/-/1407854576062/last/some-file-name">
		StringBuffer outputBuff = new StringBuffer(text);
		Matcher m = readFileImagePattern.matcher(outputBuff.toString());
		int loopDetector = 0;
		if (m.find()) {
            outputBuff = new StringBuffer();

            do {
                if (loopDetector++ > 2000) {
                    logger.error("Error processing markup [2.1]: " + text);
                    break;
                }
                String url = "";
                String img = m.group(0);
                Matcher m2 = readFilePathPattern.matcher(img);

                if (m2.find())
                    url = m2.group().trim();

                String[] args = url.split(org.kablink.teaming.util.Constants.SLASH);

                if (args.length == 8) {
                    //Now, replace the url with special markup version
                    Matcher m1 = urlSrcPattern.matcher(img);
                    if (m1.find()) {
                        String imgReplacement;
                        String fileName = args[WebUrlUtil.FILE_URL_NAME];

                        img = m1.replaceFirst(" src=\"cid:{{attachmentUrl: " + fileName.replace("$", "\\$") + "}}\"");
                        imgReplacement = img.replace("$", "\\$");
                        m.appendReplacement(outputBuff, imgReplacement);
                    }
                }
            }
            while (m.find());

            m.appendTail(outputBuff);
        }

		text = outputBuff.toString();
		return text;
	}

	static String replaceV1AttachmentFileUrls(String text) {
		if (Validator.isNotNull(text)) {
			Matcher m = v1AttachmentUrlPattern.matcher(text);
			int loopDetector = 0;
			while (m.find()) {
				if (loopDetector++ > 2000) {
					logger.error("Error processing markup [2]: " + text);
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
						img = m1.replaceFirst(" src=\"cid:{{attachmentFileId: fileId=" + fileId + "}}\"");
						text = m.replaceFirst(img.replace("$", "\\$")); //remove regex special char
						m = v1AttachmentUrlPattern.matcher(text);
					}
				}
			}
		}
		return text;
	}


	public static void scanDescriptionForICLinks(Description description) {
		if (Validator.isNull(description.getText())) return;
    	Matcher m = iceCoreLinkPattern.matcher(description.getText());
    	int loopDetector = 0;
    	while (m.find()) {
    		if (loopDetector++ > 2000) {
	        	logger.error("Error processing markup [2.3]: " + description.getText());
    			break;
    		}
    		String linkArgs = "";
    		String link = m.group();
        	Matcher m2 = iceCoreLinkRelPattern.matcher(link);
        	if (m2.find() && m2.groupCount() >= 1) linkArgs = m2.group(1).trim().replace("$", "\\$");
    		
        	String linkText = "" ;
        	if (m.groupCount() >= 2) { linkText = m.group(2).trim().replace("$", "\\$"); }
        	int i = linkText.toLowerCase().indexOf("</a>");
        	if (i < 0) break;
        	String titleText = linkText.substring(0, i);
        	String remainderText = linkText.substring(i+4, linkText.length());

        	if (Validator.isNotNull(linkArgs)) {
        		linkArgs = linkArgs.replaceFirst(titleUrlTextPattern.toString(), "");
        		description.setText(m.replaceFirst("cid:{{titleUrl: " + linkArgs.replaceAll("%2B", "+") + " text=" + Html.stripHtml(titleText) + "}}" + remainderText));
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
	        	logger.error("Error processing markup [2.4]: " + description.getText());
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
					logger.error("Error processing markup [3]: " + description.getText());
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
    			matcher.appendReplacement(outputBuf, Matcher.quoteReplacement(link.toString()));
	    	} while (matcher.find());
			matcher.appendTail(outputBuf);
			if (!outputBuf.toString().equals(description.getText())) description.setText(outputBuf.toString());
		}
	}

	protected interface UrlBuilder {
		public String getFileUrlByName(String fileName);
		public String getFileUrlById(String fileId);
		public String getRelativeTitleUrl(String normalizedTitle, String title, Boolean isMobile);
		public String getTitleUrl(String binderId, String zoneUUID, String normalizedTitle, String title, Boolean isMobile);
		public String getRootUrl();
		public String getImagesRootUrl();
		public String getRootServletUrl();
		public String getVibeFunctionResult(String functionText);
	}
	public static String markupStringReplacement(final RenderRequest req, final RenderResponse res, 
			final HttpServletRequest httpReq, final HttpServletResponse httpRes,
			final Map searchResults, String inputString, final String type) {
		return markupStringReplacement(req, res, httpReq, httpRes, searchResults, inputString, type, false);
	}
	public static String markupStringReplacement(final RenderRequest req, final RenderResponse res, 
			final HttpServletRequest httpReq, final HttpServletResponse httpRes,
			final Map searchResults, String inputString, final String type, final Boolean isMobile) {
		UrlBuilder builder = new UrlBuilder() {
			public String getRootUrl() {
				if (httpReq != null) return WebUrlUtil.getAdapterRootURL(httpReq, httpReq.isSecure());
				if (req != null) return WebUrlUtil.getAdapterRootURL(req, req.isSecure());
				return WebUrlUtil.getAdapterRootUrl();
			}
			public String getImagesRootUrl() {
				return WebUrlUtil.getStaticFilesSSFContextRootURL() + "images/";
			}
			public String getRootServletUrl() {
				if (httpReq != null) return WebUrlUtil.getServletRootURL(httpReq, httpReq.isSecure());
				if (req != null) return WebUrlUtil.getServletRootURL(req, req.isSecure());
				return WebUrlUtil.getServletRootURL();
			}
			public String getFileUrlByName(String fileName) {
				if (WebKeys.MARKUP_EXPORT.equals(type)) {
					//need permalink
					return PermaLinkUtil.getFilePermalink(searchResults, fileName);
				} else if (WebKeys.MARKUP_RSS.equals(type)) {
					String entityType = (String)searchResults.get(org.kablink.util.search.Constants.ENTITY_FIELD);
					String entityId = (String)searchResults.get(org.kablink.util.search.Constants.DOCID_FIELD);
					return "{{RSSattachmentUrl: entityId=" + String.valueOf(entityId) +
							" entityType=" +entityType + " fileName=" + fileName + "}}";
				} else {
					return WebUrlUtil.getFileUrl(WebUrlUtil.getServletRootURL(httpReq), WebKeys.ACTION_READ_FILE, searchResults, fileName);
				}
			}
			public String getFileUrlById(String fileId) {
				Object fileName = searchResults.get(WebUrlUtil.getFileInfoById((String)org.kablink.util.search.Constants.FILENAME_AND_ID_FIELD,fileId));
				if (fileName == null) return "";
				return getFileUrlByName(fileName.toString());

			}
			public String getRelativeTitleUrl(String normalizedTitle, String title, Boolean isMobile) {
				String zoneUUID = "";
				return getTitleUrl((String)searchResults.get(org.kablink.util.search.Constants.BINDER_ID_FIELD), 
						zoneUUID, normalizedTitle, title, isMobile);
			}
			public String getTitleUrl(String binderId, String zoneUUID, String normalizedTitle, String title, Boolean isMobile) {
				if (WebKeys.MARKUP_EXPORT.equals(type) || WebKeys.MARKUP_EMAIL.equals(type) || res == null) {
					return PermaLinkUtil.getTitlePermalink(Long.valueOf(binderId), zoneUUID, normalizedTitle);
				}
				String url = "";
				PortletURL portletURL = res.createActionURL();
				portletURL.setParameter(WebKeys.URL_BINDER_ID, binderId);
				if (Validator.isNotNull(zoneUUID)) portletURL.setParameter(WebKeys.URL_ZONE_UUID, zoneUUID);
				if (normalizedTitle != null && !normalizedTitle.equals("")) {
					portletURL.setParameter(WebKeys.URL_NORMALIZED_TITLE, normalizedTitle);
					portletURL.setParameter(WebKeys.ACTION, WebKeys.ACTION_VIEW_FOLDER_ENTRY);
					portletURL.setParameter(WebKeys.URL_ENTRY_PAGE_TITLE, title);
					url = portletURL.toString();
					if (isMobile) {
						AdaptedPortletURL adapterUrl = new AdaptedPortletURL(req, "ss_mobile", true);
						adapterUrl.setParameter(WebKeys.ACTION, WebKeys.ACTION_MOBILE_AJAX);
						adapterUrl.setParameter(WebKeys.URL_BINDER_ID, binderId.toString());
						adapterUrl.setParameter(WebKeys.URL_ENTRY_TITLE, normalizedTitle);
						adapterUrl.setParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_MOBILE_SHOW_ENTRY);
						if (Validator.isNotNull(zoneUUID)) adapterUrl.setParameter(WebKeys.URL_ZONE_UUID, zoneUUID);
						url = adapterUrl.toString();
					}
				} else {
					if (isMobile) {
						AdaptedPortletURL adapterUrl = new AdaptedPortletURL(req, "ss_mobile", true);
						adapterUrl.setParameter(WebKeys.ACTION, WebKeys.ACTION_MOBILE_AJAX);
						adapterUrl.setParameter(WebKeys.URL_BINDER_ID, binderId.toString());
						adapterUrl.setParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_MOBILE_SHOW_FOLDER);
						if (Validator.isNotNull(zoneUUID)) adapterUrl.setParameter(WebKeys.URL_ZONE_UUID, zoneUUID);
						url = adapterUrl.toString();
					} else {
						portletURL.setParameter(WebKeys.ACTION, WebKeys.ACTION_VIEW_FOLDER_LISTING);
						url = portletURL.toString();
					}
				}
				return url;
			}
			
			//vibe functions (for searchResults)
			public String getVibeFunctionResult(String functionText) {
				String defId = (String)searchResults.get(Constants.COMMAND_DEFINITION_FIELD);
				Definition def = null;
				if (defId != null) def = getDefinitionModule().getDefinition(defId);
				User user = RequestContextHolder.getRequestContext().getUser();
				Long zoneId = RequestContextHolder.getRequestContext().getZoneId();
				String result = "";
				String[] functionArgs = functionText.trim().split("\\|");
				if (functionArgs.length >= 1) {
					String vibeFunction = functionArgs[0].trim();
					if (vibeFunction.equals("title")) {
						result = (String)searchResults.get(Constants.TITLE_FIELD);
					} else if (vibeFunction.equals("user")) {
						if (functionArgs.length >= 2 && functionArgs[1].equals("name")) {
							result = user.getName();
						} else {
							result = user.getTitle();
						}
					} else if (vibeFunction.equals("createdBy")) {
						if (searchResults.get(Constants.CREATORID_FIELD) != null) {
							Long creatorId = Long.valueOf((String) searchResults.get(Constants.CREATORID_FIELD));
							ProfileDao profileDao = (ProfileDao) SpringContextUtil.getBean("profileDao");
							Principal creator = profileDao.loadPrincipal(creatorId, RequestContextHolder.getRequestContext().getZoneId(), false);
							if (creator != null) {
								if (functionArgs.length >= 2 && functionArgs[1].equals("name")) {
									result = creator.getName();
								} else {
									result = creator.getTitle();
								}
							}
						}
					} else if (vibeFunction.equals("createdOn")) {
						if (searchResults.get(Constants.CREATION_DATE_FIELD) != null) {
							Date createdOnDate = ((Date) searchResults.get(Constants.CREATION_DATE_FIELD));
					    	DateFormat df = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT, user.getLocale());
					    	df.setTimeZone(user.getTimeZone());
					    	result = df.format(createdOnDate);
						}
					} else if (vibeFunction.equals("modifiedBy")) {
						if (searchResults.get(Constants.MODIFICATIONID_FIELD) != null) {
							Long modifierId = Long.valueOf((String) searchResults.get(Constants.MODIFICATIONID_FIELD));
							ProfileDao profileDao = (ProfileDao) SpringContextUtil.getBean("profileDao");
							Principal modifier = profileDao.loadPrincipal(modifierId, RequestContextHolder.getRequestContext().getZoneId(), false);
							if (modifier != null) {
								if (functionArgs.length >= 2 && functionArgs[1].equals("name")) {
									result = modifier.getName();
								} else {
									result = modifier.getTitle();
								}
							}
						}
					} else if (vibeFunction.equals("modifiedOn")) {
						if (searchResults.get(Constants.MODIFICATION_DATE_FIELD) != null) {
							Date modifiedOnDate = ((Date) searchResults.get(Constants.MODIFICATION_DATE_FIELD));
					    	DateFormat df = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT, user.getLocale());
					    	df.setTimeZone(user.getTimeZone());
					    	result = df.format(modifiedOnDate);
						}
					} else if (vibeFunction.equals("data")) {
						if (functionArgs.length >= 2) {
							//Get the data item
							String dataName = functionArgs[1].trim();
							if (def != null) {
								String dataType = DefinitionHelper.findAttributeType(dataName, def.getDefinition());
								if ("date".equals(dataType) || "date_time".equals(dataType)) {
									Object dateObj = searchResults.get(dataName);
									Date date;
									if(dateObj instanceof Date) {
										date = (Date) dateObj;
									}
									else if(dateObj instanceof String) {
										try {
											date = DateTools.stringToDate((String)dateObj);
											DateFormat df;
											if ("date".equals(dataType)) {
												df = DateFormat.getDateInstance(DateFormat.MEDIUM, user.getLocale());
											} else {
												df = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT, user.getLocale());
											}
											df.setTimeZone(user.getTimeZone());
									    	result = df.format(date);
										} catch (ParseException e) {}
									}
								} else if ("event".equals(dataType)) {
									Date startDate = (Date)searchResults.get(dataName + 
											BasicIndexUtils.DELIMITER + Constants.EVENT_FIELD_LOGICAL_START_DATE);
									Date endDate = (Date)searchResults.get(dataName + 
											BasicIndexUtils.DELIMITER + Constants.EVENT_FIELD_LOGICAL_END_DATE);
									String timeZoneSensitive = (String)searchResults.get(dataName + 
											BasicIndexUtils.DELIMITER + Constants.EVENT_FIELD_TIME_ZONE_SENSITIVE);
									boolean allDayEvent = false;
									if (!"true".equals(timeZoneSensitive)) {
										allDayEvent = true;
									}
									if (startDate != null && endDate != null) {
										Calendar sd = Calendar.getInstance();
										sd.setTime(startDate);
										Calendar ed = Calendar.getInstance();
										ed.setTime(endDate);
										result = eventToString(sd, ed, allDayEvent);
									}
								} else if ("user_list".equals(dataType)) {
									Object vObj = searchResults.get(dataName);
									StringBuffer sb = new StringBuffer();
									if (vObj != null) {
										String[] values = vObj.toString().split(",");
										List<Long> userIdList = new ArrayList<Long>();
										for (int i = 0; i < values.length; i++) {
											String id = values[i].trim();
											if (!id.equals("")) {
												try {
													userIdList.add(Long.valueOf(Long.valueOf(id)));
												} catch(Exception e) {}
											}
										}
										ProfileDao profileDao = (ProfileDao) SpringContextUtil.getBean("profileDao");
										List<Principal> users = profileDao.loadPrincipals(userIdList, zoneId, false);
										for (Principal p : users) {
											if (sb.length() > 0) sb.append(", ");
											sb.append(p.getTitle());
										}
									}
									result = sb.toString();
								} else {
									result = (String)searchResults.get(dataName);
								}
							}
						}
					} else if (vibeFunction.equals("permalink")) {
						Long entryId = Long.valueOf((String)searchResults.get(Constants.DOCID_FIELD));
						String entryEntityType = (String)searchResults.get(Constants.ENTITY_FIELD);
						EntityIdentifier.EntityType entityType = EntityIdentifier.EntityType.valueOf(entryEntityType);
						String webUrl = PermaLinkUtil.getPermalink(entryId, entityType);
						if (type.equals(WebKeys.MARKUP_VIEW_TEXT)) {
							result = webUrl;
						} else {
							if (functionArgs.length >= 2) {
								result = "<a href=\"" + webUrl + "\">" + functionArgs[1] + "</a>";
							} else {
								result = "<a href=\"" + webUrl + "\">" + webUrl + "</a>";
							}
						}
					} else if (vibeFunction.equals("image")) {
						if (functionArgs.length >= 2) {
							String fileName =  new String(functionArgs[1].replaceAll("^[\\s\\u00A0]*", "").replaceAll("[\\s\\u00A0]*$", ""));
							String url = getFileUrlByName(fileName);
							result = getImageHTML(functionArgs, url, type);
						}
					} else if (vibeFunction.equals("file")) {
						if (functionArgs.length >= 2) {
							String fileName =  new String(functionArgs[1].replaceAll("^[\\s\\u00A0]*", "").replaceAll("[\\s\\u00A0]*$", ""));
							String url = getFileUrlByName(fileName);
							result = getFileHTML(functionArgs, url, type);
						}
					} else if (vibeFunction.equals("translate")) {
						if (functionArgs.length >= 2) {
							String tag =  functionArgs[1].trim();
							result = NLT.getDef(tag);
						}
					}
				}
				if (result == null) result = "";
				return result;
			}
		};
		return markupStringReplacement(req, res, httpReq, httpRes, builder,
				(String)searchResults.get(org.kablink.util.search.Constants.DOCID_FIELD), 
				(String)searchResults.get(org.kablink.util.search.Constants.ENTITY_FIELD), 
				inputString, type, isMobile);
	}
	
	public static String eventToString(Calendar startDate, Calendar endDate, boolean allDayEvent) {
		User user = RequestContextHolder.getRequestContext().getUser();
		String result = "";
		if (startDate != null) {
			DateFormat df1 = DateFormat.getDateInstance(DateFormat.MEDIUM, user.getLocale());
			df1.setTimeZone(user.getTimeZone());
			DateFormat df1GMT = DateFormat.getDateInstance(DateFormat.MEDIUM, user.getLocale());
			df1GMT.setTimeZone(TimeZone.getTimeZone("GMT"));
			DateFormat df2 = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT, user.getLocale());
			df2.setTimeZone(user.getTimeZone());
			DateFormat df3 = DateFormat.getTimeInstance(DateFormat.SHORT, user.getLocale());
			df3.setTimeZone(user.getTimeZone());
			if (allDayEvent) {
				//This is an all day event, just show the start date
				if (endDate == null || df1GMT.format(startDate.getTime()).equals(df1GMT.format(endDate.getTime()))) {
					result = df1GMT.format(startDate.getTime()) + " (" + NLT.get("event.allDay") + ")";
				} else {
					result = NLT.get("event.fromTo", new String[] {df1GMT.format(startDate.getTime()), df1GMT.format(endDate.getTime())}) + " (" + NLT.get("event.allDay") + ")";
				}
			} else if (startDate.equals(endDate)) {
				//This is just a date and time
				result = df2.format(startDate.getTime());
			} else if (endDate != null && !startDate.equals(endDate) &&
					startDate.get(Calendar.YEAR) == endDate.get(Calendar.YEAR) && 
					startDate.get(Calendar.DAY_OF_YEAR) == endDate.get(Calendar.DAY_OF_YEAR)) {
				//This is a meeting date, show the date and then a time range
				String[] args = new String[] {df1.format(startDate.getTime()), df3.format(startDate.getTime()), df3.format(endDate.getTime())};
				result = NLT.get("event.meeting", args);
			} else if (endDate != null && !startDate.equals(endDate) &&
					(startDate.get(Calendar.YEAR) != endDate.get(Calendar.YEAR) || 
					startDate.get(Calendar.DAY_OF_YEAR) != endDate.get(Calendar.DAY_OF_YEAR))) {
				//This is a date range across days. Show the range
				String[] args = new String[] {df1.format(startDate.getTime()), df1.format(endDate.getTime())};
				result = NLT.get("event.fromTo", args);
			}
		}
		return result;
	}
	
	//{{vibe: image | imageName | alt=text | height=y | width=x}}
	public static String getImageHTML(String[] functionArgs, String url, String type) {
		String result = "";
		String fileName =  new String(functionArgs[1].trim());
		fileName = fileName.replaceFirst("^[^\\w]*", "");
		if (functionArgs.length >= 2) {
			String qualifiers = "";
			for (int i = 2; i < functionArgs.length; i++) {
				String[] fArg = functionArgs[i].trim().split("=");
				if (fArg.length >= 2 && "alt".equals(fArg[0].trim().toLowerCase())) {
					qualifiers += " ALT=\"" + fArg[1].trim() + "\"";
					qualifiers += " TITLE=\"" + fArg[1].trim() + "\"";
				} else if (fArg.length >= 2 && "height".equals(fArg[0].trim().toLowerCase())) {
					qualifiers += " HEIGHT=\"" + fArg[1].trim() + "\"";
				} else if (fArg.length >= 2 && "width".equals(fArg[0].trim().toLowerCase())) {
					qualifiers += " WIDTH=\"" + fArg[1].trim() + "\"";
				} else if (fArg.length >= 2 && "border".equals(fArg[0].trim().toLowerCase())) {
					qualifiers += " BORDER=\"" + fArg[1].trim() + "\"";
				}
			}
			if (url != null) {
				if (type.equals(WebKeys.MARKUP_VIEW_TEXT)) {
					result = "[" + fileName + "]";
				} else {
					result = "<img src=\"" + url + "\" " + qualifiers + "/>";
				}
			}
		}
		return result;
	}
	
	//{{vibe: file | fileName | link text}}
	public static String getFileHTML(String[] functionArgs, String url, String type) {
		String result = "";
		String fileName =  new String(functionArgs[1].trim());
		fileName = fileName.replaceFirst("^[^\\w]*", "");
		if (functionArgs.length >= 2) {
			String linkText = fileName;
			if (functionArgs.length >= 3) {
				linkText = functionArgs[2].trim();
			}
			if (url != null) {
				if (type.equals(WebKeys.MARKUP_VIEW_TEXT)) {
					result = "[" + fileName + "]";
				} else {
					result = "<a target=\"_blank\" href=\"" + url + "\" title=\"" + fileName + "\">" + linkText + "</a>";
				}
			}
		}
		return result;
	}
	
	public static String markupStringReplacement(final RenderRequest req, final RenderResponse res, 
			final HttpServletRequest httpReq, final HttpServletResponse httpRes,
			final DefinableEntity entity, String inputString, final String type) {
		return markupStringReplacement(req, res, httpReq, httpRes, entity, inputString, type, false);
	}
	public static String markupStringReplacement(final RenderRequest req, final RenderResponse res, 
			final HttpServletRequest httpReq, final HttpServletResponse httpRes,
			final DefinableEntity entity, String inputString, final String type, final Boolean isMobile) {
		UrlBuilder builder = new UrlBuilder() {
			public String getRootUrl() {
				if (httpReq != null) return WebUrlUtil.getAdapterRootURL(httpReq, httpReq.isSecure());
				if (req != null) return WebUrlUtil.getAdapterRootURL(req, req.isSecure());
				return WebUrlUtil.getAdapterRootUrl();
			}
			public String getImagesRootUrl() {
				return WebUrlUtil.getStaticFilesSSFContextRootURL() + "images/";
			}
			public String getRootServletUrl() {
				if (httpReq != null) return WebUrlUtil.getServletRootURL(httpReq, httpReq.isSecure());
				if (req != null) return WebUrlUtil.getServletRootURL(req, req.isSecure());
				return WebUrlUtil.getServletRootURL();
			}
			public String getFileUrlByName(String fileName) {
				if (WebKeys.MARKUP_EXPORT.equals(type)) {
					//need permalink
					return PermaLinkUtil.getFilePermalink(entity, fileName);
				} else if (WebKeys.MARKUP_RSS.equals(type)) {
					return "{{RSSattachmentUrl: entityId=" + String.valueOf(entity.getId()) +
							" entityType=" +entity.getEntityType().name() + " fileName=" + fileName + "}}";
				} else {
					return WebUrlUtil.getFileUrl(WebUrlUtil.getServletRootURL(httpReq), WebKeys.ACTION_READ_FILE, entity, fileName);
				}
			}
			public String getFileUrlById(String fileId) {
				try {
					FileAttachment fa = (FileAttachment)entity.getAttachment(fileId);
					return getFileUrlByName(fa.getFileItem().getName());
				} catch (Exception ex) {return "";}
			}
			public String getRelativeTitleUrl(String normalizedTitle, String title, Boolean isMobile) {
				CustomAttribute zoneUUIDattr = entity.getCustomAttribute(Constants.ZONE_UUID_FIELD);
				String zoneUUID = "";
				Long binderId = entity.getId();
				if (EntityType.folderEntry.equals(entity.getEntityType())) binderId = entity.getParentBinder().getId();
				return getTitleUrl(binderId.toString(), 
						zoneUUID, normalizedTitle, title, isMobile);
			}
			public String getTitleUrl(String binderId, String zoneUUID, String normalizedTitle, String title, Boolean isMobile) {
				if (WebKeys.MARKUP_EXPORT.equals(type) || WebKeys.MARKUP_EMAIL.equals(type) || res == null) {
					return PermaLinkUtil.getTitlePermalink(Long.valueOf(binderId), normalizedTitle);
				}
				String url = "";
				
				PortletURL portletURL = res.createActionURL();
				portletURL.setParameter(WebKeys.URL_BINDER_ID, binderId);
				if (Validator.isNotNull(zoneUUID)) portletURL.setParameter(WebKeys.URL_ZONE_UUID, zoneUUID);
				if (normalizedTitle != null && !normalizedTitle.equals("")) {
					portletURL.setParameter(WebKeys.URL_NORMALIZED_TITLE, normalizedTitle);
					portletURL.setParameter(WebKeys.ACTION, WebKeys.ACTION_VIEW_FOLDER_ENTRY);
					portletURL.setParameter(WebKeys.URL_ENTRY_PAGE_TITLE, title);
					url = portletURL.toString();
					if (isMobile) {
						AdaptedPortletURL adapterUrl = new AdaptedPortletURL(req, "ss_mobile", true);
						adapterUrl.setParameter(WebKeys.ACTION, WebKeys.ACTION_MOBILE_AJAX);
						adapterUrl.setParameter(WebKeys.URL_BINDER_ID, binderId.toString());
						adapterUrl.setParameter(WebKeys.URL_ENTRY_TITLE, normalizedTitle);
						adapterUrl.setParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_MOBILE_SHOW_ENTRY);
						if (Validator.isNotNull(zoneUUID)) adapterUrl.setParameter(WebKeys.URL_ZONE_UUID, zoneUUID);
						url = adapterUrl.toString();
					}
				} else {
					if (isMobile) {
						AdaptedPortletURL adapterUrl = new AdaptedPortletURL(req, "ss_mobile", true);
						adapterUrl.setParameter(WebKeys.ACTION, WebKeys.ACTION_MOBILE_AJAX);
						adapterUrl.setParameter(WebKeys.URL_BINDER_ID, binderId.toString());
						adapterUrl.setParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_MOBILE_SHOW_FOLDER);
						if (Validator.isNotNull(zoneUUID)) adapterUrl.setParameter(WebKeys.URL_ZONE_UUID, zoneUUID);
						url = adapterUrl.toString();
					} else {
						if (Validator.isNotNull(zoneUUID)) portletURL.setParameter(WebKeys.URL_ZONE_UUID, zoneUUID);
						portletURL.setParameter(WebKeys.ACTION, WebKeys.ACTION_VIEW_FOLDER_LISTING);
						url = portletURL.toString();
					}
				}
				return url;
			}

			//vibe functions
			public String getVibeFunctionResult(String functionText) {
				Definition def = getDefinitionModule().getDefinition(entity.getEntryDefId());
				User user = RequestContextHolder.getRequestContext().getUser();
				Long zoneId = RequestContextHolder.getRequestContext().getZoneId();
				String result = "";
				String[] functionArgs = functionText.trim().split("\\|");
				if (functionArgs.length >= 1) {
					String vibeFunction = functionArgs[0].trim();
					if (vibeFunction.equals("title")) {
						result = entity.getTitle();
					} else if (vibeFunction.equals("user")) {
						if (functionArgs.length >= 2 && functionArgs[1].equals("name")) {
							result = user.getName();
						} else {
							result = user.getTitle();
						}
					} else if (vibeFunction.equals("createdBy")) {
						if (functionArgs.length >= 2 && functionArgs[1].equals("name")) {
							result = entity.getCreation().getPrincipal().getName();
						} else {
							result = entity.getCreation().getPrincipal().getTitle();
						}
					} else if (vibeFunction.equals("createdOn")) {
						Date createdOnDate = entity.getCreation().getDate();
					    DateFormat df = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT, user.getLocale());
					    df.setTimeZone(user.getTimeZone());
					    result = df.format(createdOnDate);
					} else if (vibeFunction.equals("modifiedBy")) {
						if (functionArgs.length >= 2 && functionArgs[1].equals("name")) {
							result = entity.getModification().getPrincipal().getName();
						} else {
							result = entity.getModification().getPrincipal().getTitle();
						}
					} else if (vibeFunction.equals("modifiedOn")) {
						Date modifiedOnDate = entity.getModification().getDate();
					    DateFormat df = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT, user.getLocale());
					    df.setTimeZone(user.getTimeZone());
					    result = df.format(modifiedOnDate);
					} else if (vibeFunction.equals("data")) {
						if (functionArgs.length >= 2) {
							//Get the data item
							CustomAttribute dataItem = entity.getCustomAttribute(functionArgs[1].trim());
							String dataName = functionArgs[1].trim();
							String dataType = DefinitionHelper.findAttributeType(dataName, def.getDefinition());
							if (dataItem != null) {
								if ("date".equals(dataType) || "date_time".equals(dataType)) {
									Date date = (Date) dataItem.getValue();
									if (date != null) {
										DateFormat df;
										if ("date".equals(dataType)) {
											df = DateFormat.getDateInstance(DateFormat.MEDIUM, user.getLocale());
										} else {
											df = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT, user.getLocale());
										}
										df.setTimeZone(user.getTimeZone());
									    result = df.format(date);
									}
								} else if ("event".equals(dataType)) {
									Event e = (Event) dataItem.getValue();
									if (e != null) {
										result = eventToString(e.getLogicalStart(), e.getLogicalEnd(), e.isAllDayEvent());
									}
								} else if ("radio".equals(dataType)) {
									String v = dataItem.getValue().toString();
									result = DefinitionHelper.getCaptionsFromValues(def, dataName, v);
								} else if ("selectbox".equals(dataType)) {
									Set<String> valueSet = dataItem.getValueSet();
									result = "";
									for (String v : valueSet) {
										if (!result.equals("") && !v.equals("")) {
											result = result + ", ";
										}
										result = result + DefinitionHelper.getCaptionsFromValues(def, dataName, v);
									}
								} else if ("user_list".equals(dataType)) {
									Set<String> userIdStrings = dataItem.getValueSet();
									List<Long> userIdList = new ArrayList<Long>();
									for (String uId : userIdStrings) {
										userIdList.add(Long.valueOf(uId));
									}
									ProfileDao profileDao = (ProfileDao) SpringContextUtil.getBean("profileDao");
									List<Principal> users = profileDao.loadPrincipals(userIdList, zoneId, false);
									StringBuffer sb = new StringBuffer();
									for (Principal p : users) {
										if (sb.length() > 0) sb.append(", ");
										sb.append(p.getTitle());
									}
									result = sb.toString();
								} else {
									result = dataItem.getValue().toString();
								}
							}
						}
					} else if (vibeFunction.equals("permalink")) {
						String webUrl = PermaLinkUtil.getPermalink(entity.getId(), entity.getEntityType());
						if (type.equals(WebKeys.MARKUP_VIEW_TEXT)) {
							result = webUrl;
						} else {
							if (functionArgs.length >= 2) {
								result = "<a href=\"" + webUrl + "\">" + functionArgs[1] + "</a>";
							} else {
								result = "<a href=\"" + webUrl + "\">" + webUrl + "</a>";
							}
						}
					} else if (vibeFunction.equals("image")) {
						if (functionArgs.length >= 2) {
							String fileName =  new String(functionArgs[1].replaceAll("^[\\s\\u00A0]*", "").replaceAll("[\\s\\u00A0]*$", ""));
							String url = getFileUrlByName(fileName);
							result = getImageHTML(functionArgs, url, type);
						}
					} else if (vibeFunction.equals("file")) {
						if (functionArgs.length >= 2) {
							String fileName =  new String(functionArgs[1].replaceAll("^[\\s\\u00A0]*", "").replaceAll("[\\s\\u00A0]*$", ""));
							String url = getFileUrlByName(fileName);
							result = getFileHTML(functionArgs, url, type);
						}
					} else if (vibeFunction.equals("translate")) {
						if (functionArgs.length >= 2) {
							String tag =  functionArgs[1].trim();
							result = NLT.getDef(tag);
						}
					}
				}
				if (result == null) result = "";
				return result;
			}
		};
		return markupStringReplacement(req, res, httpReq, httpRes, builder,
				entity.getId().toString(), entity.getEntityType().name(), inputString, type, isMobile);
	}
	private static String markupStringReplacement(RenderRequest req, RenderResponse res, 
			HttpServletRequest httpReq, HttpServletResponse httpRes, UrlBuilder builder, 
			String entityId, String entityType, String inputString, String type, Boolean isMobile) {
		if (Validator.isNull(inputString)) return inputString;  //don't waste time
		StringBuffer outputBuf = new StringBuffer(StringEscapeUtils.unescapeHtml(inputString));

//why?		outputString = outputString.replaceAll("%20", " ");
//		outputString = outputString.replaceAll("%7B", "{");
//		outputString = outputString.replaceAll("%7D", "}");
		int loopDetector;
		try {
			Matcher matcher; //Pattern.compile("((<a[\\s]href[=\\s]\")([^\":]*)\")");
			//do first, before add hrefs
			if (type.equals(WebKeys.MARKUP_EXPORT) || type.equals(WebKeys.MARKUP_EMAIL)) {
				//tinymce stores relative urls.  If this isn't going to be used by tinymce, need to change the urls
				//This isn't true anymore, but doesn't hurt
				matcher = hrefPattern.matcher(outputBuf);
				if (matcher.find()) {
					loopDetector = 0;
					outputBuf = new StringBuffer();
					do {						
						if (loopDetector++ > 2000) {
							logger.error("Error processing markup [4]: " + inputString);
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
						matcher.appendReplacement(outputBuf, "$2" + Matcher.quoteReplacement(link) + "\"");
						//outputString = matcher.replaceFirst("$2" + link.replace("$", "\\$") + "\"");
						//matcher = hrefPattern.matcher(outputString);
					} while (matcher.find());
					matcher.appendTail(outputBuf);
				}
			}

			//Replace the markup urls with real urls "cid:{{attachmentUrl: tempFileHandle}}"
			outputBuf = markupReplaceAttachmentReference(outputBuf, attachmentUrlPattern2, builder);
			//Replace the markup urls with real urls "{{attachmentUrl: tempFileHandle}}"
			outputBuf = markupReplaceAttachmentReference(outputBuf, v2AttachmentUrlPattern, builder);


	    	//Replace the markup v1 attachmentFileIds {{attachmentFileId: binderId=xxx entryId=xxx fileId=xxx entityType=xxx}}
			//with v2 urls 
			//  from the fileId, we can get the fileName and use the new URLS.
			outputBuf = markupReplaceFileIdReference(outputBuf, v1AttachmentFileIdPattern, builder);

	    	//Replace the markup {{titleUrl}} with real urls {{titleUrl: binderId=xxx title=xxx text=yyy}}
			//   In the "titleUrl": xxx is the normalized title of the entry. If null, it is a link to a folder.
			//       And yyy is the link text (e.g., <a ...>yyy</a>
			outputBuf = markupReplaceTitleUrlReference(outputBuf, titleUrlPattern, builder, type, isMobile);
			outputBuf = markupReplaceTitleUrlReference(outputBuf, v2TitleUrlPattern, builder, type, isMobile);

	    	//Replace the markup {{youTubeUrl}} with real urls {{youTubeUrl: url=xxx width=www height=hhh}}
			outputBuf = markupReplaceYouTubeUrlReference(outputBuf, youtubeUrlPattern, builder, type, checkIfMobile(req, httpReq));

	    	//Replace vibe parser functions markup {{vibe: xxx | yyy| zzz}} with the desired text
			if (type.equals(WebKeys.MARKUP_VIEW) || type.equals(WebKeys.MARKUP_VIEW_TEXT) || type.equals(WebKeys.MARKUP_EMAIL)) {
				//Only do this when viewing the entry. Leave the markup in for forms and export.
				outputBuf = markupReplaceVibeFunctionReference(outputBuf, vibeFunctionPattern, builder);
				outputBuf = markupReplaceVibeFunctionReference(outputBuf, v2VibeFunctionPattern, builder);
			}
		    	
	    	//When viewing the string, replace the markup title links with real links    [[page title]]
			if ((entityType.equals(EntityType.folderEntry.name()) || entityType.equals(EntityType.folder.name())) && 
					(type.equals(WebKeys.MARKUP_VIEW) || type.equals(WebKeys.MARKUP_EXPORT) || type.equals(WebKeys.MARKUP_EMAIL))) {
		    	matcher  = pageTitleUrlTextPattern.matcher(outputBuf);
				if (matcher.find()) {
					loopDetector = 0;
					outputBuf = new StringBuffer();
			    	do {
			    		if (loopDetector++ > 2000) {
				        	logger.error("Error processing markup [6.1]: " + inputString);
			    			return outputBuf.toString();
			    		}
			    		//Get the title
			    		String title = matcher.group(2).trim();
			    		String normalizedTitle = WebHelper.getNormalizedTitle(title);
			    		if (Validator.isNotNull(normalizedTitle)) {
			    			//Build the url to that entry
				    		StringBuffer titleLink = new StringBuffer();				
			    			String webUrl = builder.getRelativeTitleUrl(normalizedTitle, title, isMobile);
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
			    			matcher.appendReplacement(outputBuf, Matcher.quoteReplacement(titleLink.toString()));
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

	static StringBuffer markupReplaceAttachmentReference(StringBuffer inputBuf, Pattern pattern, UrlBuilder builder) {
		//Replace the markup urls with real urls "cid:{{attachmentUrl: tempFileHandle}}"
		StringBuffer outputBuf = inputBuf;
		Matcher matcher = pattern.matcher(inputBuf);
		if (matcher.find()) {
			int loopDetector = 0;
			outputBuf = new StringBuffer();
			do {
				if (loopDetector++ > 2000) {
					logger.error("Error processing markup [4.1]: " + inputBuf);
					return outputBuf;
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
					matcher.appendReplacement(outputBuf, Matcher.quoteReplacement(webUrl));
				}
			} while (matcher.find());
			matcher.appendTail(outputBuf);
		}
		return outputBuf;
	}

	static StringBuffer markupReplaceFileIdReference(StringBuffer inputBuf, Pattern pattern, UrlBuilder builder) {
		//Replace the markup v1 attachmentFileIds {{attachmentFileId: binderId=xxx entryId=xxx fileId=xxx entityType=xxx}}
		//with v2 urls
		//  from the fileId, we can get the fileName and use the new URLS.
		StringBuffer outputBuf = inputBuf;
		Matcher matcher = v1AttachmentFileIdPattern.matcher(inputBuf);
		if (matcher.find()) {
			int loopDetector = 0;
			outputBuf = new StringBuffer();
			do {
				if (loopDetector++ > 2000) {
					logger.error("Error processing markup [4.2]: " + inputBuf);
					return outputBuf;
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
						matcher.appendReplacement(outputBuf, Matcher.quoteReplacement(webUrl));
					}
				}
			} while (matcher.find());
			matcher.appendTail(outputBuf);
		}
		return outputBuf;
	}

	static StringBuffer markupReplaceTitleUrlReference(StringBuffer inputBuf, Pattern pattern, UrlBuilder builder, String type, Boolean isMobile) {
		//Replace the markup {{titleUrl}} with real urls {{titleUrl: binderId=xxx title=xxx text=yyy}}
		//   In the "titleUrl": xxx is the normalized title of the entry. If null, it is a link to a folder.
		//       And yyy is the link text (e.g., <a ...>yyy</a>
		StringBuffer outputBuf = inputBuf;
		Matcher matcher = pattern.matcher(inputBuf.toString());
		if (matcher.find()) {
			int loopDetector = 0;
			outputBuf = new StringBuffer();
			do {
				if (loopDetector++ > 2000) {
					logger.error("Error processing markup [5]: " + inputBuf);
					return outputBuf;
				}
				String s_binderId = "";
				String s_zoneUUID = "";
				String normalizedTitle = "";
				String title = "";

				if (matcher.groupCount() < 2) continue;
				String urlParts = matcher.group(2).trim();
				TitleMacroParameters params = TitleMacroParameters.find(urlParts);

				if (params!=null) {
					s_binderId = params.binderId;
					s_zoneUUID = params.zoneUUID;
					normalizedTitle = params.normalizedTitle;
					title = params.title;
				}

				//build the link
				StringBuffer titleLink = new StringBuffer();
				if (type.equals(WebKeys.MARKUP_FORM)) {
					titleLink.append("<a href=\"#\" class=\"ss_icecore_link\" rel=\"binderId=");
					titleLink.append(s_binderId);
					if (!s_zoneUUID.equals("")) titleLink.append(" zoneUUID=" + s_zoneUUID);
					titleLink.append(" title=");
					titleLink.append(Html.stripHtml(normalizedTitle));
					titleLink.append(" text=");
					titleLink.append(Html.stripHtml(title));
					titleLink.append("\">");
					titleLink.append(title).append("</a>");
				} else if (type.equals(WebKeys.MARKUP_VIEW)){
					String webUrl = builder.getTitleUrl(s_binderId, s_zoneUUID,
							WebHelper.getNormalizedTitle(normalizedTitle), title, isMobile);
					String showInParent = "false";
					if (normalizedTitle == null || normalizedTitle.equals("")) showInParent = "true";
					titleLink.append("<a href=\"").append(webUrl);
					titleLink.append("\" onClick=\"if (self.ss_openTitleUrl) return self.ss_openTitleUrl(this, "+showInParent+");\">");
					titleLink.append("<span class=\"ss_title_link\">").append(title).append("</span></a>");
				} else {
					String webUrl = builder.getTitleUrl(s_binderId, s_zoneUUID,
							WebHelper.getNormalizedTitle(normalizedTitle), title, isMobile);
					titleLink.append("<a href=\"").append(webUrl).append("\">").append(title).append("</a>");

				}
				matcher.appendReplacement(outputBuf, Matcher.quoteReplacement(titleLink.toString()));
			} while (matcher.find());
			matcher.appendTail(outputBuf);
		}
		return outputBuf;
	}

	static StringBuffer markupReplaceYouTubeUrlReference(StringBuffer inputBuf, Pattern pattern, UrlBuilder builder, String type, boolean isMobile) {
		//Replace the markup {{youTubeUrl}} with real urls {{youTubeUrl: url=xxx width=www height=hhh}}
		StringBuffer outputBuf = inputBuf;
		Matcher matcher = pattern.matcher(inputBuf.toString());
		if (matcher.find()) {
			int loopDetector = 0;
			outputBuf = new StringBuffer();
			do {
				if (loopDetector++ > 2000) {
					logger.error("Error processing markup [5.1]: " + inputBuf);
					return outputBuf;
				}
				String s_url = "";
				String s_width = "";
				String s_height = "";
				if (matcher.groupCount() < 2) continue;
				String urlParts = matcher.group(2).trim();
				YoutubeMacroParameters uwh = YoutubeMacroParameters.find(urlParts);
				if (uwh!=null) {
					s_url = uwh.url;
					s_width = uwh.width;
					s_height = uwh.height;
				}

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
				} else if (s_url.startsWith("http://www.youtube.com/") || s_url.startsWith("https://www.youtube.com/")) {
					if (isMobile) {
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
						String ytUrl = s_url.replaceFirst("youtube.com/watch\\?v=", "youtube.com/v/");
						ytUrl = ytUrl.replaceFirst("youtube.com\\?v=", "youtube.com/v/");
						if (youtubeDivId > 1000000) youtubeDivId = 0;
						titleLink.append("<div id=\"ss_videoDiv"+id.toString()+"\" class=\"ss_videoDiv\">\n");
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
						titleLink.append("var params = { allowScriptAccess: \"always\", wmode: \"opaque\" };\n");
						titleLink.append("var atts = { id: \"myytplayer\" };\n");
						titleLink.append("swfobject.embedSWF(\"").append(ytUrl);
						titleLink.append("?enablejsapi=1&playerapiid=ytplayer\",");
						titleLink.append(" \"ytapiplayer"+id.toString()+"\", \"").append(s_width).append("\", ");
						titleLink.append("\"").append(s_height).append("\", \"8\", null, null, params, atts);\n");
						titleLink.append("//ss_createSpannedAreaObj(\"ss_videoDiv"+id.toString()+"\");\n");
						titleLink.append("</script>\n");
					}
				} else if (s_url.startsWith("http://youtu.be/") || s_url.startsWith("https://youtu.be/")) {
					Integer id = ++youtubeDivId;
					//Make this into an embed URL
					if (s_url.startsWith("http://youtu.be/")) {
						s_url = s_url.replaceFirst("http://youtu.be/", "http://www.youtube.com/embed/");
					} else {
						s_url = s_url.replaceFirst("https://youtu.be/", "https://www.youtube.com/embed/");
					}
					titleLink.append("<div id=\"youTubeIFrame" + id.toString() + "\"></div>");
					//We have to set the iframe src after the page loads to avoid youtube wiping out following content
					titleLink.append("\n<script type=\"text/javascript\">\n");
					titleLink.append("ss_createOnLoadObj(\"youTubeFixup" + id.toString() + "\", function() {\n");
					titleLink.append("var divObj = document.getElementById(\"youTubeIFrame" + id.toString() + "\");\n");
					titleLink.append("divObj.innerHTML = '");
					titleLink.append("<iframe src=\"" + s_url + "\" width=\"" + s_width + "\" height=\"" + s_height + "\" ");
					titleLink.append(" frameborder=\"0\" allowfullscreen</iframe>");
					titleLink.append("';\n");
					titleLink.append("});\n");
					titleLink.append("</script>\n");
				} else {
					titleLink.append("<a target=\"_blank\" src=\"");
					titleLink.append(s_url);
					titleLink.append("\">");
					titleLink.append(s_url).append("</a>");
				}
				matcher.appendReplacement(outputBuf, Matcher.quoteReplacement(titleLink.toString()));
			} while (matcher.find());
			matcher.appendTail(outputBuf);
		}
		return outputBuf;
	}

	static StringBuffer markupReplaceVibeFunctionReference(StringBuffer inputBuf, Pattern pattern, UrlBuilder builder) {
		StringBuffer outputBuf = inputBuf;
		Matcher matcher = pattern.matcher(inputBuf.toString());
		if (matcher.find()) {
			int loopDetector = 0;
			outputBuf = new StringBuffer();
			do {
				if (loopDetector++ > 2000) {
					logger.error("Error processing markup [6]: " + inputBuf);
					return outputBuf;
				}
				if (matcher.groupCount() < 2) continue;
				String functionString = matcher.group(2).trim();
				//Parse and execute the vibe function
				String replacementText = builder.getVibeFunctionResult(functionString);

				matcher.appendReplacement(outputBuf, Matcher.quoteReplacement(replacementText));
			} while (matcher.find());
			matcher.appendTail(outputBuf);
		}
		return outputBuf;
	}

	//Routine to fix up descriptions before exporting them
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
					logger.error("Error processing markup [8]: " + inputString);
					return outputBuf.toString();
				}
				if (matcher.groupCount() < 2) continue;
				String urlParts = matcher.group(2).trim();
				String s_binderId = "";
				String s_binderIdEquals = "=";
				Matcher fieldMatcher = titleUrlBinderPattern.matcher(urlParts);
				if (fieldMatcher.find() && fieldMatcher.groupCount() >= 1) s_binderId = fieldMatcher.group(1).trim();
				fieldMatcher = titleUrlBinderPattern2.matcher(urlParts);
				if (fieldMatcher.find() && fieldMatcher.groupCount() >= 1) {
					s_binderId = fieldMatcher.group(1).trim();
					s_binderIdEquals = "%3d";
				}
		    		
				String s_zoneUUID = "";
				String s_zoneUUIDEquals = "=";
				fieldMatcher = titleUrlZoneUUIDPattern.matcher(urlParts);
				if (fieldMatcher.find() && fieldMatcher.groupCount() >= 1) s_zoneUUID = fieldMatcher.group(1).trim();
				fieldMatcher = titleUrlZoneUUIDPattern2.matcher(urlParts);
				if (fieldMatcher.find() && fieldMatcher.groupCount() >= 1) {
					s_zoneUUID = fieldMatcher.group(1).trim();
					s_zoneUUIDEquals = "%3d";
				}
				if (s_zoneUUID.equals("")) {
					ZoneInfo zoneInfo = ExportHelper.getZoneInfo();
					s_zoneUUID = String.valueOf(zoneInfo.getId());
					s_zoneUUIDEquals = s_binderIdEquals;
				}
		    		
				String normalizedTitle = "";
				String normalizedTitleEquals = "=";
				fieldMatcher = titleUrlTitlePattern.matcher(urlParts);
				if (fieldMatcher.find() && fieldMatcher.groupCount() >= 1) normalizedTitle = fieldMatcher.group(1).trim();
				fieldMatcher = titleUrlTitlePattern2.matcher(urlParts);
				if (fieldMatcher.find() && fieldMatcher.groupCount() >= 1) {
					normalizedTitle = fieldMatcher.group(1).trim();
					normalizedTitleEquals = "%3d";
				}
		        	
				String title = "";
				String titleEquals = "=";
				fieldMatcher = titleUrlTextPattern.matcher(urlParts); //html stripped on input
				if (fieldMatcher.find() && fieldMatcher.groupCount() >= 1) title = fieldMatcher.group(1).trim();
				fieldMatcher = titleUrlTextPattern2.matcher(urlParts); //html stripped on input
				if (fieldMatcher.find() && fieldMatcher.groupCount() >= 1) {
					title = fieldMatcher.group(1).trim();
					titleEquals = "%3d";
				}
		        	
				//rebuild the link
	    		String titleLink = "cid:{{titleUrl: binderId" + s_binderIdEquals + s_binderId + " zoneUUID" + s_zoneUUIDEquals + s_zoneUUID +
	    			" title" + normalizedTitleEquals + normalizedTitle + " text" + titleEquals + Html.stripHtml(title) + "}}";
    			matcher.appendReplacement(outputBuf, Matcher.quoteReplacement(titleLink));
	    	} while (matcher.find());
			matcher.appendTail(outputBuf);
		}
		//Fixup permalinks
		matcher = permaLinkUrlPattern.matcher(outputBuf.toString());
		if (matcher.find()) {
			loopDetector = 0;
			outputBuf = new StringBuffer();
			do {
				if (loopDetector++ > 2000) {
					logger.error("Error processing markup [8.1]: " + inputString);
					return outputBuf.toString();
				}
				if (matcher.groupCount() < 1) continue;
				String s_url = matcher.group(1);
				String s_zoneUUID = "";
				Matcher fieldMatcher = permaLinkZoneUUIDPattern.matcher(s_url);
				if (fieldMatcher.find() && fieldMatcher.groupCount() >= 1) s_zoneUUID = fieldMatcher.group(1);
				if (s_zoneUUID.equals("")) {
					ZoneInfo zoneInfo = ExportHelper.getZoneInfo();
					s_url = s_url.replaceFirst("/action/view_permalink/", "/action/view_permalink/zoneUUID/" + String.valueOf(zoneInfo.getId()) + "/");
				}
		    		
    			matcher.appendReplacement(outputBuf, Matcher.quoteReplacement(s_url));
	    	} while (matcher.find());
			matcher.appendTail(outputBuf);
		}
     	return outputBuf.toString();
	}
	
	//Routine to fix up descriptions before exporting them
	public static String markupStringReplacementForMashupCanvasExport(String inputString) {
		return DefinitionHelper.fixupMashupCanvasForExport(inputString);
	}

	//Routine to split a body of text into sections
	public static List markupSplitBySection(String body) {
		List bodyParts = new ArrayList();
    	Matcher m0 = sectionPattern.matcher(body);
    	if (m0.find()) {
    		// The 'prefix' is everything up to the first section title
    		// where a title is:
    		//      '==title text==' or
    		//      '<p>==titleText==</p>'
    		//
    		// Bugzilla 692804:
    		//    Before fixing this bug, the sectionPattern regular
    		//    expression was recognizing strings such as:
    		//         '...[ERROR] =============== URL...'
    		//    as a section title because it was checking for 0 or
    		//    more non '=' characters (using '*' in the pattern)
    		//    between the '=='s.  The fix for this bug was to
    		//    change the regular expression to look for 1 or more
    		//    non '=' characters (by using '+' in the pattern
    		//    instead of '*'.)
			Map part = new HashMap();
			if (m0.start(1) >= 0) {
				part.put("prefix", body.substring(0, m0.start(1)));
				bodyParts.add(part);
				body = body.substring(m0.start(1), body.length());
			} else {
				part.put("prefix", body.substring(0, m0.start(2)));
				bodyParts.add(part);
				body = body.substring(m0.start(2), body.length());
			}
    	}
    	
    	int sectionNumber = 0;
    	int lastSectionDepth = 0;
    	int maxDepthFound = 1;
    	Map<Integer,Integer> sectionNumbering = new HashMap<Integer,Integer>();
    	sectionNumbering.put(1, 0);
    	Matcher m1 = sectionPattern.matcher(body);
    	int loopDetector = 0;
    	while (m1.find()) {
    		if (loopDetector++ > 2000) {
	        	logger.error("Error processing markup [9]: " + body);
    			return bodyParts;
    		}
 			Map part = new HashMap();
    		//Get the section title
    		String title = m1.group(3).trim();
    		if (title == null) title = "";
			
    		part.put("sectionTitle", title);
    		part.put("sectionNumber", String.valueOf(sectionNumber));
    		
			String equalSigns = m1.group(2).trim();
			int sectionDepth = Integer.valueOf(equalSigns.length());
			if (sectionDepth > maxDepthFound) maxDepthFound = sectionDepth;
			sectionDepth--;		//depth is one less than the number of equal signs
			part.put("sectionTitleClass", "ss_sectionHeader" + String.valueOf(sectionDepth));
			part.put("sectionDepth", sectionDepth);
			
			body = body.substring(m1.end(), body.length());
	    	Matcher m2 = sectionPattern.matcher(body);
	    	if (m2.find()) {
	    		if (m2.groupCount() >= 2 && m2.start(0) >= 0) {
					part.put("sectionBody", body.substring(0, m2.start(0)));
					body = body.substring(m2.start(0), body.length());
	    		} else {
	    			part.put("sectionBody", body);
	    		}
	    	} else {
	    		part.put("sectionBody", body);
	    	}
	    	if (m1.group(1) != null && m1.group(5) != null) {
	    		part.put("sectionText", m1.group(1) + m1.group(2) + m1.group(3) + m1.group(4) + m1.group(5) + part.get("sectionBody"));
	    	} else {
	    		part.put("sectionText", m1.group(2) + m1.group(3) + m1.group(4) + part.get("sectionBody"));
	    	}
			bodyParts.add(part);
			m1 = sectionPattern.matcher(body);

			//Calculate the number text for this section
			if (sectionDepth > lastSectionDepth) {
				//Starting a new section in a deeper level; Start it (and all between) at 1 
				for (int i = lastSectionDepth + 1; i <= maxDepthFound; i++) {
					sectionNumbering.put(Integer.valueOf(i), 1);
				}
				sectionNumbering.put(Integer.valueOf(sectionDepth), 1);
				//Reset the levels below this one to 0 to indicate there is nothing there yet
				for (int i = sectionDepth + 1; i <= maxDepthFound; i++) {
					sectionNumbering.put(Integer.valueOf(i), 0);
				}
			} else if (sectionDepth < lastSectionDepth) {
				//We are going back up a level or more; increment the new level and reset the lower levels
				sectionNumbering.put(Integer.valueOf(sectionDepth), sectionNumbering.get(Integer.valueOf(sectionDepth)) + 1);
				for (int i = sectionDepth + 1; i <= maxDepthFound; i++) {
					sectionNumbering.put(Integer.valueOf(i), 0);
				}
			} else {
				//We are at the same level. Increment this level number
				sectionNumbering.put(Integer.valueOf(sectionDepth), sectionNumbering.get(Integer.valueOf(sectionDepth)) + 1);
				for (int i = sectionDepth + 1; i <= maxDepthFound; i++) {
					sectionNumbering.put(Integer.valueOf(i), 0);
				}
			}
			lastSectionDepth = sectionDepth;
			
			//Build the number text for this section
			String numberText = "";
			for (int i = 1; i <= maxDepthFound; i++) {
				Integer number = sectionNumbering.get(Integer.valueOf(i));
				if (number > 0) {
					if (!numberText.equals("")) numberText = numberText + ".";
					numberText = numberText + String.valueOf(number);
				}
			}
			part.put("sectionNumberText", numberText);
			
			sectionNumber++;
		}
		return bodyParts;
	}

	//Routine to split a body of text into sections
	public static String markupSectionsReplacement(String body) {
		String unescapedBody = StringEscapeUtils.unescapeHtml(body);
		return _markupSectionsReplacement(unescapedBody);
	}

	private static String _markupSectionsReplacement(String body) {
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
	        	logger.error("Error processing markup [9.1]: " + body);
    			return body;
    		}
 			Map part = new HashMap();
    		//Get the section title
    		String title = m1.group(3).trim();
    		if (title == null) title = "";
			
    		part.put("sectionTitle", title);
    		part.put("sectionNumber", String.valueOf(sectionNumber));
    		
			String equalSigns = m1.group(2).trim();
			int sectionDepth = Integer.valueOf(equalSigns.length());
			sectionDepth--;
			part.put("sectionDepth", sectionDepth);
			part.put("sectionTitleClass", "ss_sectionHeader" + String.valueOf(sectionDepth));
			
			body = body.substring(m1.end(), body.length());
	    	Matcher m2 = sectionPattern.matcher(body);
	    	if (m2.find()) {
				part.put("sectionBody", body.substring(0, m2.start(0)));
				body = body.substring(m2.start(0), body.length());
	    	} else {
	    		part.put("sectionBody", body);
	    	}
	    	if (m1.group(1) != null && m1.group(5) != null) {
	    		part.put("sectionText", m1.group(1) + m1.group(2) + m1.group(3) + m1.group(4) + m1.group(5) + part.get("sectionBody"));
	    	} else {
	    		part.put("sectionText", m1.group(2) + m1.group(3) + m1.group(4) + part.get("sectionBody"));
	    	}
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
		CustomAttribute ca_zoneUUID = entity.getCustomAttribute("_zoneUUID");
		String s_zoneUUID = "";
		if (ca_zoneUUID != null && ca_zoneUUID.getValueType() == CustomAttribute.STRING) {
			s_zoneUUID = (String)ca_zoneUUID.getValue();
		} else if (ca_zoneUUID != null && ca_zoneUUID.getValueType() == CustomAttribute.SET) {
			Object[] UUIDs = ca_zoneUUID.getValueSet().toArray();
			s_zoneUUID = UUIDs[0].toString();
		}
		if (s_zoneUUID != null && s_zoneUUID.contains(".")) {
			s_zoneUUID = s_zoneUUID.substring(0, s_zoneUUID.indexOf("."));
		}
		final String entity_zoneUUID = s_zoneUUID;
			
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
					CustomAttribute ca = entity.getCustomAttribute(attrName);
					if (ca != null) description = (Description)ca.getValue();
				}
				
				boolean dataChanged = false;
				if (description != null && !Validator.isNull(description.getText())) {
			    	//Scan the text for {{titleUrl: binderId=xxx zoneUUID=xxx title=xxx}}
					//  Remove the zoneUUID if it is the same as the current zone
					StringBuffer outputBuf = new StringBuffer(description.getText());
					outputBuf = fixupV2Urls(outputBuf, v2AttachmentUrlPattern);
					outputBuf = fixupV2Urls(outputBuf, v2TitleUrlPattern);
					outputBuf = fixupV2Urls(outputBuf, v2VibeFunctionPattern);

					outputBuf = fixupTitleUrls(outputBuf, titleUrlPattern, binderIdMap);
					outputBuf = fixupPermalinkUrls(outputBuf, permaLinkUrlPattern, entity_zoneUUID, binderIdMap, entryIdMap);
					dataChanged = !outputBuf.toString().equals(description.getText());
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
							//Add in the other mashup attributes so they get set correctly when the binder is modified
							Map ca = entity.getCustomAttributes();
							if (ca.containsKey(attrName + DefinitionModule.MASHUP_SHOW_BRANDING))
								data.put(attrName + DefinitionModule.MASHUP_SHOW_BRANDING, 
										entity.getCustomAttribute(attrName + DefinitionModule.MASHUP_SHOW_BRANDING).getValue().toString());
							if (ca.containsKey(attrName + DefinitionModule.MASHUP_SHOW_FAVORITES_AND_TEAMS))
								data.put(attrName + DefinitionModule.MASHUP_SHOW_FAVORITES_AND_TEAMS, 
										entity.getCustomAttribute(attrName + DefinitionModule.MASHUP_SHOW_FAVORITES_AND_TEAMS).getValue().toString());
							if (ca.containsKey(attrName + DefinitionModule.MASHUP_SHOW_NAVIGATION))
								data.put(attrName + DefinitionModule.MASHUP_SHOW_NAVIGATION, 
										entity.getCustomAttribute(attrName + DefinitionModule.MASHUP_SHOW_NAVIGATION).getValue().toString());
							if (ca.containsKey(attrName + DefinitionModule.MASHUP_HIDE_MASTHEAD))
								data.put(attrName + DefinitionModule.MASHUP_HIDE_MASTHEAD, 
										entity.getCustomAttribute(attrName + DefinitionModule.MASHUP_HIDE_MASTHEAD).getValue().toString());
							if (ca.containsKey(attrName + DefinitionModule.MASHUP_HIDE_SIDEBAR))
								data.put(attrName + DefinitionModule.MASHUP_HIDE_SIDEBAR, 
										entity.getCustomAttribute(attrName + DefinitionModule.MASHUP_HIDE_SIDEBAR).getValue().toString());
							if (ca.containsKey(attrName + DefinitionModule.MASHUP_HIDE_TOOLBAR))
								data.put(attrName + DefinitionModule.MASHUP_HIDE_TOOLBAR, 
										entity.getCustomAttribute(attrName + DefinitionModule.MASHUP_HIDE_TOOLBAR).getValue().toString());
							if (ca.containsKey(attrName + DefinitionModule.MASHUP_HIDE_FOOTER))
								data.put(attrName + DefinitionModule.MASHUP_HIDE_FOOTER, 
										entity.getCustomAttribute(attrName + DefinitionModule.MASHUP_HIDE_FOOTER).getValue().toString());
							if (ca.containsKey(attrName + DefinitionModule.MASHUP_STYLE))
								data.put(attrName + DefinitionModule.MASHUP_STYLE, 
										entity.getCustomAttribute(attrName + DefinitionModule.MASHUP_STYLE).getValue().toString());

							if ( ca.containsKey( attrName + DefinitionModule.MASHUP_PROPERTIES ) )
							{
								Object value;
								
								value = entity.getCustomAttribute( attrName + DefinitionModule.MASHUP_PROPERTIES ).getValue();
								if ( value != null && value instanceof Document )
								{
									Document doc;
									
									doc = (Document) value; 
									if ( doc != null )
									{
										data.put( attrName + DefinitionModule.MASHUP_PROPERTIES, 
												  doc.asXML() );
									}
								}

							}
						}
					}
				}
			}
			
			public String getFlagElementName() {
				return "export";
			}
		};
		getDefinitionModule().walkDefinition(entity, visitor, null);
		if (!data.isEmpty()) {
			//Save any changes
			if (EntityType.folderEntry.equals(entity.getEntityType())) {
				try {
					getFolderModule().modifyEntry(entity.getParentBinder().getId(), entity.getId(),
						new MapInputData(data), null, null, null, null);
				} catch(Exception e) {
					logger.error(e.getLocalizedMessage());
				}
			} else if (EntityType.workspace.equals(entity.getEntityType()) ||
					EntityType.folder.equals(entity.getEntityType())) {
				try {
					getBinderModule().modifyBinder(entity.getId(), new MapInputData(data), null, null, null);
				} catch(Exception e) {
					logger.error(e.getLocalizedMessage());
				}
			}
		}
	}

	private static StringBuffer fixupPermalinkUrls(StringBuffer inputBuf, Pattern permaLinkUrlPattern, String entity_zoneUUID,
													 Map<Long, Long> binderIdMap, Map<Long, Long> entryIdMap) {
		//Scan for permalinks to fix up
		StringBuffer outputBuf = inputBuf;
		Matcher matcher = permaLinkUrlPattern.matcher(outputBuf.toString());
		if (matcher.find()) {
            int loopDetector = 0;
            outputBuf = new StringBuffer();
            do {
                if (loopDetector++ > 2000) {
                    logger.error("Error processing markup [10.1]: " + inputBuf.toString());
                    break;
                }
                if (matcher.groupCount() < 1) continue;
                String s_url = matcher.group(1);
                String s_entryId = "";
                Matcher fieldMatcher = permaLinkEntryIdPattern.matcher(s_url);
                if (fieldMatcher.find() && fieldMatcher.groupCount() >= 1) s_entryId = fieldMatcher.group(1);
                String s_entryTitle = "";
                fieldMatcher = permaLinkEntryTitlePattern.matcher(s_url);
                if (fieldMatcher.find() && fieldMatcher.groupCount() >= 1) s_entryTitle = fieldMatcher.group(1);
                String s_binderId = "";
                fieldMatcher = permaLinkBinderIdPattern.matcher(s_url);
                if (fieldMatcher.find() && fieldMatcher.groupCount() >= 1) s_binderId = fieldMatcher.group(1);
                String s_entityType = "";
                fieldMatcher = permaLinkEntityTypePattern.matcher(s_url);
                if (fieldMatcher.find() && fieldMatcher.groupCount() >= 1) s_entityType = fieldMatcher.group(1);
                String s_zoneUUID = entity_zoneUUID;
                fieldMatcher = permaLinkZoneUUIDPattern.matcher(s_url);
                if (fieldMatcher.find() && fieldMatcher.groupCount() >= 1) s_zoneUUID = fieldMatcher.group(1);
                String href = "";
                fieldMatcher = permaLinkHrefPattern.matcher(s_url);
                if (fieldMatcher.find() && fieldMatcher.groupCount() >= 1) href = fieldMatcher.group(1);
                if (s_zoneUUID != null && !s_zoneUUID.equals("")) {
                    //This permalink was exported from another system. See if it can be recast to this system
                    String url = "";
                    if (!s_binderId.equals("") && binderIdMap.containsKey(Long.valueOf(s_binderId)) &&
                            ((EntityType.folder.name().equals(s_entityType)) ||
                            EntityType.workspace.name().equals(s_entityType))) {
                        s_binderId = String.valueOf(binderIdMap.get(Long.valueOf(s_binderId)));
                        url = PermaLinkUtil.getPermalink(Long.valueOf(s_binderId), EntityType.valueOf(s_entityType));
                    }
                    if (!s_entryId.equals("") && entryIdMap.containsKey(Long.valueOf(s_entryId)) &&
                            EntityType.folderEntry.name().equals(s_entityType)) {
                        s_entryId = String.valueOf(entryIdMap.get(Long.valueOf(s_entryId)));
                        url = PermaLinkUtil.getPermalink(Long.valueOf(s_entryId), EntityType.valueOf(s_entityType));
                    }
                    if (!url.equals("")) {
                        s_url = href + url;

                    } else {
                        //Recast the url to this system without doing the id translation
                        if (!s_binderId.equals("") &&  (EntityType.folder.name().equals(s_entityType)) ||
                                EntityType.workspace.name().equals(s_entityType)) {
                            url = PermaLinkUtil.getPermalink(Long.valueOf(s_binderId), EntityType.valueOf(s_entityType));
                        }
                        if (!s_entryId.equals("") && EntityType.folderEntry.name().equals(s_entityType)) {
                            url = PermaLinkUtil.getPermalink(Long.valueOf(s_entryId), EntityType.valueOf(s_entityType));
                        }
                        if (!url.equals("")) {
                            url += "&zoneUUID=" + s_zoneUUID;
                            s_url = href + url;
                        }
                    }
                }

                matcher.appendReplacement(outputBuf, Matcher.quoteReplacement(s_url));
            } while (matcher.find());
            matcher.appendTail(outputBuf);
        }
		return outputBuf;
	}

	private static StringBuffer fixupTitleUrls(StringBuffer inputBuf, Pattern titleUrlPattern,
											   Map<Long, Long> binderIdMap) {
		StringBuffer outputBuf = inputBuf;
		Matcher matcher = titleUrlPattern.matcher(inputBuf.toString());
		int loopDetector;
		if (matcher.find()) {
            loopDetector = 0;
            outputBuf = new StringBuffer();
            do {
                if (loopDetector++ > 2000) {
                    logger.error("Error processing markup [10]: " + inputBuf.toString());
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
                if (!s_zoneUUID.equals("") && s_zoneUUID.equals(ExportHelper.getZoneInfo().getId().toString())) {
                    link = link.replaceFirst("zoneUUID=" + s_zoneUUID, "");
                }
                matcher.appendReplacement(outputBuf, Matcher.quoteReplacement(link.toString()));
            } while (matcher.find());
            matcher.appendTail(outputBuf);
        }
		return outputBuf;
	}

	public static String fixupAllV2Urls(String input) {
		StringBuffer output = new StringBuffer(input);
		output = fixupV2Urls(output, v2AttachmentUrlPattern);
		output = fixupV2Urls(output, v2TitleUrlPattern);
		output = fixupV2Urls(output, v2VibeFunctionPattern);
		return output.toString();
	}

	public static StringBuffer fixupV2Urls(StringBuffer inputBuf, Pattern pattern) {
		StringBuffer outputBuf = inputBuf;
		Matcher matcher = pattern.matcher(inputBuf);
		if (matcher.find()) {
			outputBuf = new StringBuffer();
			do {
				if (matcher.groupCount() < 2) continue;

				String expr = "cid:" + matcher.group();
				matcher.appendReplacement(outputBuf, expr);
			} while (matcher.find());
			matcher.appendTail(outputBuf);
		}
		return outputBuf;
	}

	/**
	 * There was a bug where the mce_src attribute was being included as part of the <img> tag
	 * in the branding html.  This method will remove the mc_src attribute.
	 * The html looked like the following:
	 * 	<img 
			width="77" 
			height="100" 
			class=" ss_addimage " 
			style="width: 77px; height: 100px;" 
			alt=" " 
			mce_src="http://jwootton4.provo.novell.com:8080/ssf/s/viewFile?&amp;fileId=7-two.png_1814782083072314018.tmp" 
			src="http://jwootton4.provo.novell.com:8080/ssf/s/readFile/workspace/2401/ff8080823800299b013800aa55c6004b/1340040959000/last/two.png"
			 _moz_resizing="true">

	 */
	public static String removeMceSrc( String html )
	{
		String retValue = null;
		
		if ( html != null )
		{
			Matcher matcher;
			
			//retValue = html.replaceAll( "(mce_src=\")([^\"]*)(\")", "" );
			matcher = mceSrcPattern.matcher( html );
			if ( matcher != null )
			{
				//logger.info( "html: " + html );
				retValue = matcher.replaceAll( "" );
				//logger.info( "retValue: " + retValue );
			}
		}
		
		return retValue;
	}
	
	
	/**
	 * Parse the branding and replace all <img src="{{attachmentUrl: image-name}}" with a url
	 * to the image that is visible to all users (including the guest user).
	 * For example, <img src="http://somehost/ssf/branding/binder/binderId/imgName" />
	 */
	public static String fixupImgUrls(
		AbstractAllModulesInjected allModules,
		HttpServletRequest httpReq,
		ServletContext servletContext,
		Binder brandingSourceBinder,
		String branding )
	{
		StringBuffer outputBuf;
		Matcher matcher;

		if ( branding == null || branding.length() == 0 )
			return null;
		
		outputBuf = new StringBuffer( branding );
		matcher = m_imgAttachmentUrlPattern.matcher( outputBuf.toString() );

		// Replace the markup {{attachmentUrl: imageName}} inside an <img> tag with the url to the image.
		if ( matcher.find() )
		{
	    	int loopDetector = 0;

	    	loopDetector = 0;
			outputBuf = new StringBuffer();
			do
			{
				if ( loopDetector++ > 2000 )
				{
					logger.error( "Error processing markup in fixupImgUrls(): " + branding );
					return branding;
				}
				
				if ( matcher.groupCount() >= 5 )
				{
					String fileName;
					
					// Get the file name of the image.
					fileName = matcher.group( 4 );

					// Get the url to this image
					if ( fileName != null && fileName.length() > 0 )
					{
						String imgUrl;
						
						// Remove escaping that timyMce for html escaping - get here if someone typed {{att.. }}themselves
						fileName = StringEscapeUtils.unescapeHtml(fileName);
						
						imgUrl = BrandingUtil.getUrlToBinderBrandingImg(
																	allModules,
																	httpReq,
																	servletContext,
																	brandingSourceBinder,
																	fileName );
						
						if ( imgUrl != null && imgUrl.length() > 0 )
						{
							String replacement;
							String remainder;

							// Replace src="{{attachmentUrl: imgName}} with src="some url"
							imgUrl = Matcher.quoteReplacement( imgUrl );
							
							// Get the rest of the string after "src={{attachmentUrl: imgName}}"
							remainder = matcher.group( 5 );
							remainder = MarkupUtil.fixupDataMceSrc(
																httpReq,
																brandingSourceBinder,
																fileName,
																remainder );
							
							replacement = "$2 src=\"" + imgUrl + remainder;
							matcher.appendReplacement(outputBuf, replacement );
						}
					}
				}
				
			} while ( matcher.find() );
			
			matcher.appendTail( outputBuf );
    	}
    	
		return outputBuf.toString();
	}
	
	/**
	 * Replace the url found in the data-mce-src attribute with a "readFile" url.
	 */
	private static String fixupDataMceSrc(
		HttpServletRequest httpReq,
		Binder brandingSourceBinder,
		String fileName,
		String txt )
	{
		String readFileUrl = null;
		StringBuffer outputBuf;

		if ( txt == null || txt.length() == 0 )
			return txt;
		
		outputBuf = new StringBuffer( txt );

		// Get the "readFile" url to the image
		{
			String webPath;

			webPath = WebUrlUtil.getServletRootURL( httpReq );

			// Get a "readFile" url to the image
			readFileUrl = WebUrlUtil.getFileUrl(
											webPath,
											WebKeys.ACTION_READ_FILE,
											brandingSourceBinder,
											fileName );
		}
		
		if ( readFileUrl != null && readFileUrl.length() > 0 )
		{
			Matcher dataMceSrcMatcher;
			
			dataMceSrcMatcher = m_dataMceSrcPattern.matcher( txt );
		
			// Replace the url found in the data-mce-src url with the
			// "readFile" url.
			if ( dataMceSrcMatcher.find() )
			{
				String replacement;
				String remainder;

				outputBuf = new StringBuffer();
			
				replacement = "data-mce-src=\"" + readFileUrl;
				dataMceSrcMatcher.appendReplacement( outputBuf, replacement );
				dataMceSrcMatcher.appendTail( outputBuf );
			}
		}
		
		return outputBuf.toString();
	}

	static class YoutubeMacroParameters {
		private String url;
		private String width;
		private String height;

		public YoutubeMacroParameters(String url, String width, String height) {
			this.url = url;
			this.width = width;
			this.height = height;
		}

		static YoutubeMacroParameters find(String macroWithParams) {
			String decoded = StringEscapeUtils.unescapeHtml(macroWithParams);
			YoutubeMacroParameters uwh = _find(decoded);
			if (uwh==null) {
				uwh = _find(macroWithParams);
			}
			return uwh;
		}

		static YoutubeMacroParameters _find(String macroWithParams) {
			String s_url = null;
			Matcher fieldMatcher = youtubeUrlUrlPattern.matcher(macroWithParams);
			if (fieldMatcher.find() && fieldMatcher.groupCount() >= 1) s_url = fieldMatcher.group(1).trim();

			String s_width = null;
			fieldMatcher = youtubeUrlWidthPattern.matcher(macroWithParams);
			if (fieldMatcher.find() && fieldMatcher.groupCount() >= 1) s_width = fieldMatcher.group(1).trim();

			String s_height = null;
			fieldMatcher = youtubeUrlHeightPattern.matcher(macroWithParams);
			if (fieldMatcher.find() && fieldMatcher.groupCount() >= 1) s_height = fieldMatcher.group(1).trim();

			if (s_url!=null) {
				return new YoutubeMacroParameters(s_url, s_width, s_height);
			}
			return null;
		}
	}

	static class TitleMacroParameters {
		private String binderId;
		private String zoneUUID;
		private String normalizedTitle;
		private String title;

		public TitleMacroParameters(String binderId, String zoneUUID, String normalizedTitle, String title) {
			this.binderId = binderId;
			this.zoneUUID = zoneUUID;
			this.normalizedTitle = normalizedTitle;
			this.title = title;
		}

		static TitleMacroParameters find(String macroWithParams) {
			String decoded = StringEscapeUtils.unescapeHtml(macroWithParams);
			TitleMacroParameters uwh = _find(decoded);
			if (uwh==null) {
				uwh = _find(macroWithParams);
			}
			return uwh;
		}

		static TitleMacroParameters _find(String macroWithParams) {
			String s_binderId = "";
			Matcher fieldMatcher = titleUrlBinderPattern.matcher(macroWithParams);
			if (fieldMatcher.find() && fieldMatcher.groupCount() >= 1) s_binderId = fieldMatcher.group(1).trim();

			String s_zoneUUID = "";
			fieldMatcher = titleUrlZoneUUIDPattern.matcher(macroWithParams);
			if (fieldMatcher.find() && fieldMatcher.groupCount() >= 1) s_zoneUUID = fieldMatcher.group(1).trim();

			String normalizedTitle = "";
			fieldMatcher = titleUrlTitlePattern.matcher(macroWithParams);
			if (fieldMatcher.find() && fieldMatcher.groupCount() >= 1) normalizedTitle = fieldMatcher.group(1).trim();

			String title = "";
			fieldMatcher = titleUrlTextPattern.matcher(macroWithParams); //html stripped on input
			if (fieldMatcher.find() && fieldMatcher.groupCount() >= 1) title = fieldMatcher.group(1).trim();
			if (title.equals("")) title = normalizedTitle;

			if (!"".equals(s_binderId)) {
				return new TitleMacroParameters(s_binderId, s_zoneUUID, normalizedTitle, title);
			}
			return null;
		}
	}

}
