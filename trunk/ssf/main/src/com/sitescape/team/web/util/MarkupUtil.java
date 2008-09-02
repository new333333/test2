package com.sitescape.team.web.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.portlet.PortletURL;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.WindowState;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.multipart.MultipartFile;

import com.sitescape.team.domain.Binder;
import com.sitescape.team.domain.DefinableEntity;
import com.sitescape.team.domain.Description;
import com.sitescape.team.domain.EntityIdentifier;
import com.sitescape.team.domain.EntityIdentifier.EntityType;
import com.sitescape.team.domain.FileAttachment;
import com.sitescape.team.portletadapter.AdaptedPortletURL;
import com.sitescape.team.repository.RepositoryUtil;
import com.sitescape.team.search.SearchFieldResult;
import com.sitescape.team.util.FileUploadItem;
import com.sitescape.team.web.WebKeys;
import com.sitescape.util.Html;
import com.sitescape.util.Validator;
import com.sitescape.util.search.Constants;
import com.sitescape.team.web.util.WebHelper;
import com.sitescape.util.Http;

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
        		img = m3.replaceFirst("src=\"{{attachmentUrl: " + WebHelper.getFileName(fileHandle).replace("$", "\\$") + "}}\"");
        		description.setText(m.replaceFirst(img.replace("$", "\\$"))); //remove special chars from replacement string
        		m = uploadImagePattern.matcher(description.getText());
        	}
    	}
	}
	//called after file is actually saved, to fixup markup
/**	public static void scanDescriptionForFileRename(DefinableEntity entity) {
		String entityType = entity.getEntityType().name();
		String binderId = "";
		String entryId = "";
		if (entity.getEntityType().isBinder()) { 
			binderId = entity.getId().toString();
			entryId = entity.getId().toString();
		} else {
			binderId = entity.getParentBinder().getId().toString();
			entryId = entity.getId().toString();
		}
		Pattern p1 = Pattern.compile("(\\{\\{attachmentUrl: ([^}]*)\\}\\})");
    	Matcher m1 = p1.matcher(description.getText());
    	int loopDetector = 0;
    	boolean changes = false;
    	while (m1.find()) {
    		if (loopDetector > 2000) {
	        	logger.error("Error processing markup: " + description.getText());
    			return;
    		}
    		loopDetector++;
    		String url = m1.group(2);
			if (entity != null) {
	    		//Look for the attachment
	    		FileAttachment fa = entity.getFileAttachment(url.trim());
	    		if (fa != null) {
    		    	//Now, replace the url with special markup version
    	        		String newText = new String("{{attachmentFileId: fileId=" + fa.getId() 
    	        				+ specialAmp + "binderId=" + binderId + specialAmp + "entryId=" + entryId 
    	        				+ specialAmp + "entityType=" + entityType + "}}");
    	        		description.setText(m1.replaceFirst(newText.replace("$", "\\$")));
    	        		m1 = p1.matcher(description.getText());
    	        		changes = true;
	    		}
			}
    	}
    	//don't want to break "==" compare if not necesary, faster for long text
    	if (changes) description.setText(description.getText().replaceAll(specialAmp, "&"));

	}
	**/

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
    		String[] args = url.split(com.sitescape.team.util.Constants.SLASH);
 
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
	}
	public static String markupStringReplacement(final RenderRequest req, final RenderResponse res, 
			final HttpServletRequest httpReq, final HttpServletResponse httpRes,
			final Map searchResults, String inputString, String type) {
		UrlBuilder builder = new UrlBuilder() {
			public String getFileUrlByName(String fileName) {				
				return WebUrlUtil.getFileUrl(WebUrlUtil.getServletRootURL(httpReq), WebKeys.ACTION_READ_FILE, searchResults, fileName, true);
			}
			public String getFileUrlById(String fileId) {
				Object fileNameResult = searchResults.get(com.sitescape.util.search.Constants.FILENAME_FIELD);
				Object fileIdResult = searchResults.get(com.sitescape.util.search.Constants.FILE_ID_FIELD);
				//looking for a specific file
				String fileName=null;
				if (fileIdResult instanceof SearchFieldResult) {
					List values = ((SearchFieldResult)fileIdResult).getValueArray();
					for (int i=0; i<values.size(); ++i) {
						if (fileId.equals(values.get(i))) {
							try {
								fileName = ((SearchFieldResult)fileNameResult).getValueArray().get(i).toString();
								break;
							} catch (Exception ignoreMisMatch) {};
						}
					}
				} else {
					if (fileId.equals(fileIdResult.toString())) {
						fileName = fileNameResult.toString();
					}
				}
				if (Validator.isNull(fileName)) return "";
				return getFileUrlByName(fileName);

			}
			public String getRelativeTitleUrl(String normalizedTitle) {
				return getTitleUrl((String)searchResults.get(Constants.BINDER_ID_FIELD), normalizedTitle);
			}
			public String getTitleUrl(String binderId, String normalizedTitle) {
    			String action = WebKeys.ACTION_VIEW_FOLDER_ENTRY;
    			Map params = new HashMap();
    			params.put(WebKeys.URL_BINDER_ID, binderId);
    			params.put(WebKeys.URL_NORMALIZED_TITLE, normalizedTitle);
    			if (req == null && res == null && httpReq == null && httpRes == null) {
    				action = WebKeys.ACTION_VIEW_PERMALINK;
    				params.put(WebKeys.URL_ENTRY_TITLE, normalizedTitle);
    				params.put(WebKeys.URL_ENTITY_TYPE, EntityType.folderEntry.name());
    			}
    			return getPortletUrl(req, res, httpReq, httpRes, action, true, params);

			}
		};
		return markupStringReplacement(req, res, httpReq, httpRes, builder,
				(String)searchResults.get(Constants.DOCID_FIELD), (String)searchResults.get(Constants.ENTITY_FIELD), inputString, type);
	}
	
	public static String markupStringReplacement(final RenderRequest req, final RenderResponse res, 
			final HttpServletRequest httpReq, final HttpServletResponse httpRes,
			final DefinableEntity entity, String inputString, String type) {
		UrlBuilder builder = new UrlBuilder() {
			public String getFileUrlByName(String fileName) {
				return WebUrlUtil.getFileUrl(WebUrlUtil.getServletRootURL(httpReq), WebKeys.ACTION_READ_FILE, entity, fileName, true);
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
    			String action = WebKeys.ACTION_VIEW_FOLDER_ENTRY;
    			Map params = new HashMap();
    			params.put(WebKeys.URL_BINDER_ID, binderId);
    			params.put(WebKeys.URL_NORMALIZED_TITLE, normalizedTitle);
    			if (req == null && res == null && httpReq == null && httpRes == null) {
    				action = WebKeys.ACTION_VIEW_PERMALINK;
    				params.put(WebKeys.URL_ENTRY_TITLE, normalizedTitle);
    				params.put(WebKeys.URL_ENTITY_TYPE, EntityType.folderEntry.name());
    			}
    			return getPortletUrl(req, res, httpReq, httpRes, action, true, params);

			}
		};
		return markupStringReplacement(req, res, httpReq, httpRes, builder,
				entity.getId().toString(), entity.getEntityType().name(), inputString, type);
	}
	private static String markupStringReplacement(RenderRequest req, RenderResponse res, 
			HttpServletRequest httpReq, HttpServletResponse httpRes, UrlBuilder builder, 
			String entityId, String entityType, String inputString, String type) {
		if (Validator.isNull(inputString)) return inputString;  //don't waste time
		String outputString = new String(inputString);
		//this happens on form view, the data is escaped on input from the jsp??
		outputString = outputString.replaceAll("%20", " ");
		outputString = outputString.replaceAll("%7B", "{");
		outputString = outputString.replaceAll("%7D", "}");
		int loopDetector;
		try {
	    	//Replace the markup urls with real urls {{attachmentUrl: tempFileHandle}}
			Matcher matcher = attachmentUrlPattern.matcher(outputString);
			loopDetector = 0;
			while (matcher.find()) {
				if (loopDetector++ > 2000) {
					logger.error("Error processing markup [3]: " + inputString);
					return outputString;
				}
				if (matcher.groupCount() >= 2) {
					String fileName = matcher.group(2);
					String webUrl = builder.getFileUrlByName(fileName);
					//the filename is already escaped as html, so need to remove the encoding that building the URL did
					outputString = matcher.replaceFirst(webUrl.replace("$", "\\$"));
					matcher = attachmentUrlPattern.matcher(outputString);
				}
	    	}
	    	
	    	//Replace the markup v1 attachmentFileIds {{attachmentFileId: binderId=xxx entryId=xxx fileId=xxx entityType=xxx}}
			//with v2 urls 
			//  from the fileId, we can get the fileName and use the new URLS.
			matcher = v1AttachmentFileIdPattern.matcher(outputString);
			loopDetector = 0;
			while (matcher.find()) {
				if (loopDetector++ > 2000) {
					logger.error("Error processing markup [4]: " + inputString);
					return outputString;
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
						outputString = matcher.replaceFirst(webUrl.replace("$", "\\$"));
			    		matcher = v1AttachmentFileIdPattern.matcher(outputString);
			    	}
				}
			}
		
	    	//Replace the markup {{titleUrl}} with real urls {{titleUrl: binderId=xxx title=xxx}}
			matcher = titleUrlPattern.matcher(outputString);
			loopDetector = 0;
			while (matcher.find()) {
				if (loopDetector++ > 2000) {
					logger.error("Error processing markup [5]: " + inputString);
					return outputString;
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
		    	} else {
		    		String webUrl = builder.getTitleUrl(s_binderId, WebHelper.getNormalizedTitle(normalizedTitle));
		    		titleLink.append("<a href=\"").append(webUrl);
		    		titleLink.append("\" onClick=\"if (self.ss_openTitleUrl) return self.ss_openTitleUrl(this);\">");
		    		titleLink.append("<span class=\"ss_title_link\">").append(title).append("</span></a>");
		    	}
    			outputString = matcher.replaceFirst(titleLink.toString().replace("$", "\\$"));
    			matcher = titleUrlPattern.matcher(outputString);   		
	    	}
	    	
	    	//When viewing the string, replace the markup title links with real links    [[page title]]
			if (entityType.equals(EntityType.folderEntry.name()) && type.equals(WebKeys.MARKUP_VIEW)) {
		    	matcher  = pageTitleUrlTextPattern.matcher(outputString);
		    	loopDetector = 0;
		    	while (matcher.find()) {
		    		if (loopDetector++ > 2000) {
			        	logger.error("Error processing markup [6]: " + inputString);
		    			return outputString;
		    		}
		    		//Get the title
		    		String title = matcher.group(2).trim();
		    		String normalizedTitle = WebHelper.getNormalizedTitle(title);
		    		if (Validator.isNotNull(normalizedTitle)) {
		    			//Build the url to that entry
			    		StringBuffer titleLink = new StringBuffer();				
		    			String webUrl = builder.getRelativeTitleUrl(normalizedTitle);
			    		titleLink.append("<a href=\"").append(webUrl);
			    		titleLink.append("\" onClick=\"if (self.ss_openTitleUrl) return self.ss_openTitleUrl(this);\">");
			    		titleLink.append("<span class=\"ss_title_link\">").append(title).append("</span></a>");
				    	//use substring so don't have to parse $ out of replacement string
		    			outputString = matcher.replaceFirst(titleLink.toString().replace("$", "\\$"));
		    			matcher = pageTitleUrlTextPattern.matcher(outputString);
		    		}
				}
			}
		} catch(Exception e) {
			logger.error("Error processing markup [7]: " + inputString, e);
			return inputString;
		}
     	return outputString;
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
    	Pattern p0 = Pattern.compile("(==[=]*)([^=]*)(==[=]*)");
    	Matcher m0 = p0.matcher(body);
    	if (m0.find()) {
			Map part = new HashMap();
			part.put("prefix", body.substring(0, m0.start(0)));
			bodyParts.add(part);
			body = body.substring(m0.start(0), body.length());
    	}
    	
    	int sectionNumber = 0;
    	Pattern p1 = Pattern.compile("(==[=]*)([^=]*)(==[=]*)");
    	Matcher m1 = p1.matcher(body);
    	while (m1.find()) {
    		if (loopDetector > 2000) {
	        	logger.error("Error processing markup [6]: " + body);
    			return body;
    		}
    		loopDetector++;
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
	    	Pattern p2 = Pattern.compile("(==[=]*)([^=]*)(==[=]*)");
	    	Matcher m2 = p2.matcher(body);
	    	if (m2.find()) {
				part.put("sectionBody", body.substring(0, m2.start(0)));
				body = body.substring(m2.start(0), body.length());
	    	} else {
	    		part.put("sectionBody", body);
	    	}
	    	part.put("sectionText", m1.group(1) + m1.group(2) + m1.group(3) + part.get("sectionBody"));
			bodyParts.add(part);
			m1 = p1.matcher(body);
    		
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
	
	public static String getPortletUrl(RenderRequest req, RenderResponse res, 
			HttpServletRequest httpReq, HttpServletResponse httpRes,
			String action, boolean actionUrl, Map params) {
		String portletName="ss_forum";
		if (req == null || res == null) {
			//This call must have come from a servlet (e.g., rss)
			//  Build a permalink url
			if (!Validator.isNull(action)) {
				params.put("action", action);
			}
			AdaptedPortletURL adapterUrl = new AdaptedPortletURL(httpReq, portletName, actionUrl);
			Iterator it = params.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry me = (Map.Entry) it.next();
				adapterUrl.setParameter((String) me.getKey(), (String)me.getValue());
			}
			return adapterUrl.toString();

		} else {
			PortletURL portletURL = null;
			if (actionUrl) {
				portletURL = res.createActionURL();
			} else {
				portletURL = res.createRenderURL();
			}
			try {
				portletURL.setWindowState(new WindowState(WindowState.MAXIMIZED.toString()));
			} catch(Exception e) {}
			
			Iterator it = params.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry me = (Map.Entry) it.next();
				portletURL.setParameter((String) me.getKey(), (String)me.getValue());
			}
			if (!Validator.isNull(action)) {
				portletURL.setParameter("action", new String[] {action});
			}
	
			return portletURL.toString();
		}
	}
	
	
}
