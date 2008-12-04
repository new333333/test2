package org.kablink.teaming.web.util;

import java.io.IOException;
import java.net.URI;
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
import org.kablink.teaming.domain.DefinableEntity;
import org.kablink.teaming.domain.Description;
import org.kablink.teaming.domain.FileAttachment;
import org.kablink.teaming.domain.EntityIdentifier.EntityType;
import org.kablink.teaming.repository.RepositoryUtil;
import org.kablink.teaming.util.FileUploadItem;
import org.kablink.teaming.web.WebKeys;
import org.kablink.util.Html;
import org.kablink.util.Http;
import org.kablink.util.Validator;
import org.springframework.web.multipart.MultipartFile;



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
	
	protected final static Pattern iceCoreLinkPattern = Pattern.compile("(<a [^>]*class=\"ss_icecore_link\"[^>]*>)([^<]*)</a>");
	protected final static Pattern iceCoreLinkRelPattern = Pattern.compile("rel=\"([^\"]*)");
	
	protected final static Pattern attachmentUrlPattern = Pattern.compile("(\\{\\{attachmentUrl: ([^}]*)\\}\\})");
	protected final static Pattern v1AttachmentFileIdPattern = Pattern.compile("(\\{\\{attachmentFileId: ([^}]*)\\}\\})");
	protected final static Pattern titleUrlPattern = Pattern.compile("(\\{\\{titleUrl: ([^\\}]*)\\}\\})");
	protected final static Pattern titleUrlBinderPattern = Pattern.compile("binderId=([^ ]*)");
	protected final static Pattern titleUrlTitlePattern = Pattern.compile("title=([^ ]*)");
	protected final static Pattern titleUrlTextPattern = Pattern.compile("text=(.*)$");
	protected final static Pattern hrefPattern = Pattern.compile("((<a[\\s]href[=\\s]\")([^\":]*)\")");
	protected final static Pattern pageTitleUrlTextPattern = Pattern.compile("(\\[\\[([^\\]]*)\\]\\])");
	protected final static Pattern sectionPattern =Pattern.compile("(==[=]*)([^=]*)(==[=]*)");
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
        			URI uri = new URI(null, null, fileName, null); //encode as editor does 
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
 
	    	if (args.length == 7) {
		    	//Now, replace the url with special markup version
		    	Matcher m1 = urlSrcPattern.matcher(img);
	        	if (m1.find()) {
	        		String fileName = args[6];
	 
	        		img = m1.replaceFirst("src=\"{{attachmentUrl: " + fileName.replace("$", "\\$") + "}}\"");
	        		description.setText(m.replaceFirst(img.replace("$", "\\$")));  //remove regex special char
	        		m = readFileImagePattern.matcher(description.getText());
	        	}
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

        	if (Validator.isNotNull(linkArgs)) {
        		description.setText(m.replaceFirst("{{titleUrl: " + linkArgs + " text=" + Html.stripHtml(linkText) + "}}"));
        		m = iceCoreLinkPattern.matcher(description.getText());
	    	}
    	}
	}

	protected interface UrlBuilder {
		public String getFileUrlByName(String fileName);
		public String getFileUrlById(String fileId);
		public String getRelativeTitleUrl(String normalizedTitle);
		public String getTitleUrl(String binderId, String normalizedTitle);
		public String getRootUrl();
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
			public String getFileUrlByName(String fileName) {
				if (!WebKeys.MARKUP_EXPORT.equals(type)) return WebUrlUtil.getFileUrl(WebUrlUtil.getServletRootURL(httpReq), WebKeys.ACTION_READ_FILE, searchResults, fileName);
				//need permalink
				return PermaLinkUtil.getFilePermalink(searchResults, fileName);
			}
			public String getFileUrlById(String fileId) {
				Object fileName = searchResults.get(org.kablink.util.search.Constants.FILENAME_FIELD+fileId);
				if (fileName == null) return "";
				return getFileUrlByName(fileName.toString());

			}
			public String getRelativeTitleUrl(String normalizedTitle) {
				return getTitleUrl((String)searchResults.get(org.kablink.util.search.Constants.BINDER_ID_FIELD), normalizedTitle);
			}
			public String getTitleUrl(String binderId, String normalizedTitle) {
				if (WebKeys.MARKUP_EXPORT.equals(type) || res == null) {
					return PermaLinkUtil.getTitlePermalink(Long.valueOf(binderId), normalizedTitle);
				}
				PortletURL portletURL = portletURL = res.createActionURL();
				portletURL.setParameter(WebKeys.URL_BINDER_ID, binderId);
				portletURL.setParameter(WebKeys.URL_NORMALIZED_TITLE, normalizedTitle);
				portletURL.setParameter(WebKeys.ACTION, WebKeys.ACTION_VIEW_FOLDER_ENTRY);
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
			public String getFileUrlByName(String fileName) {
				if (!WebKeys.MARKUP_EXPORT.equals(type)) return WebUrlUtil.getFileUrl(WebUrlUtil.getServletRootURL(httpReq), WebKeys.ACTION_READ_FILE, entity, fileName);
				//need permalink
				return PermaLinkUtil.getFilePermalink(entity, fileName);
			}
			public String getFileUrlById(String fileId) {
				try {
					FileAttachment fa = (FileAttachment)entity.getAttachment(fileId);
					return getFileUrlByName(fa.getFileItem().getName());
				} catch (Exception ex) {return "";}
			}
			public String getRelativeTitleUrl(String normalizedTitle) {
				return getTitleUrl(entity.getParentBinder().getId().toString(), normalizedTitle);
			}
			public String getTitleUrl(String binderId, String normalizedTitle) {
				if (WebKeys.MARKUP_EXPORT.equals(type) || res == null) {
					return PermaLinkUtil.getTitlePermalink(Long.valueOf(binderId), normalizedTitle);
				}
				PortletURL portletURL = portletURL = res.createActionURL();
				portletURL.setParameter(WebKeys.URL_BINDER_ID, binderId);
				portletURL.setParameter(WebKeys.URL_NORMALIZED_TITLE, normalizedTitle);
				portletURL.setParameter(WebKeys.ACTION, WebKeys.ACTION_VIEW_FOLDER_ENTRY);
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
						//remove escaping that timyMce adds
						fileName = StringEscapeUtils.unescapeHtml(fileName);
		           		try {
							//remove escaping for urls
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
			    		
					String normalizedTitle = "";
					fieldMatcher = titleUrlTitlePattern.matcher(urlParts);
					if (fieldMatcher.find() && fieldMatcher.groupCount() >= 1) normalizedTitle = fieldMatcher.group(1).trim();
			        	
					String title = "";
					fieldMatcher = titleUrlTextPattern.matcher(urlParts); //html stripped on input
					if (fieldMatcher.find() && fieldMatcher.groupCount() >= 1) title = fieldMatcher.group(1).trim();
			        	
					//build the link
		    		StringBuffer titleLink = new StringBuffer();				
			    	if (type.equals(WebKeys.MARKUP_FORM)) {
			        	titleLink.append("<a class=\"ss_icecore_link\" rel=\"binderId=");
			        	titleLink.append(s_binderId).append(" title=").append(Html.stripHtml(normalizedTitle)).append("\">");
			        	titleLink.append(title).append("</a>");
			    	} else if (type.equals(WebKeys.MARKUP_VIEW)){
			    		String webUrl = builder.getTitleUrl(s_binderId, WebHelper.getNormalizedTitle(normalizedTitle));
			    		titleLink.append("<a href=\"").append(webUrl);
			    		titleLink.append("\" onClick=\"if (self.ss_openTitleUrl) return self.ss_openTitleUrl(this);\">");
			    		titleLink.append("<span class=\"ss_title_link\">").append(title).append("</span></a>");
			    	} else {
			    		String webUrl = builder.getTitleUrl(s_binderId, WebHelper.getNormalizedTitle(normalizedTitle));
			    		titleLink.append("<a href=\"").append(webUrl).append("\">").append(title).append("</a>");
			    		
			    	}
	    			matcher.appendReplacement(outputBuf, titleLink.toString().replace("$", "\\$"));
		    	} while (matcher.find());
				matcher.appendTail(outputBuf);
			}
		    	
	    	//When viewing the string, replace the markup title links with real links    [[page title]]
			if (entityType.equals(EntityType.folderEntry.name()) && (type.equals(WebKeys.MARKUP_VIEW) || type.equals(WebKeys.MARKUP_EXPORT))) {
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
			    			String webUrl = builder.getRelativeTitleUrl(normalizedTitle);
			    			if (type.equals(WebKeys.MARKUP_VIEW)) {
			    				titleLink.append("<a href=\"").append(webUrl);
			    				titleLink.append("\" onClick=\"if (self.ss_openTitleUrl) return self.ss_openTitleUrl(this);\">");
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
	
}
