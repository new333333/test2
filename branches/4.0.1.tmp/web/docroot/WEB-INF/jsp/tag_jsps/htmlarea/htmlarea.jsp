<%
/**
 * Copyright (c) 1998-2010 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2010 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2010 Novell, Inc. All Rights Reserved.
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
%>
<% // htmlarea editor %>
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
<%@ page import="org.kablink.teaming.util.SPropsUtil" %>
<%@ page import="java.lang.String" %>
<%@ page import="org.kablink.util.BrowserSniffer" %>
<%@ page import="org.kablink.teaming.util.NLT" %>
<%@ page import="java.util.Locale" %>
<c:set var="ss_quotaMessage" value="" />
<c:if test="${ss_diskQuotaHighWaterMarkExceeded && !ss_diskQuotaExceeded && !ss_isBinderMirroredFolder}">
<c:set var="ss_quotaMessage" ><ssf:nlt tag="quota.nearLimit"><ssf:param name="value" useBody="true"
	    ><fmt:formatNumber value="${(ss_diskQuotaUserMaximum - ssUser.diskSpaceUsed)/1048576}" 
	    maxFractionDigits="2"/></ssf:param></ssf:nlt></c:set>
</c:if>
<c:if test="${ss_diskQuotaExceeded && !ss_isBinderMirroredFolder}">
<c:set var="ss_quotaMessage" ><ssf:nlt tag="quota.diskQuotaExceeded"/></c:set>
</c:if>
<c:set var="isShowYouTube" value='<%= SPropsUtil.getBoolean("youTube.showInEditor") %>'/>
<%
	if (BrowserSniffer.is_TinyMCECapable(request, SPropsUtil.getString("TinyMCE.notSupportedUserAgents", ""))) {
%>
<c:if test="${empty ss_tinyMCE_hasBeenLoaded}">
<c:set var="wikiLinkBinderId" value="" scope="request"/><%--
--%><c:if test="${!empty ssDefinitionEntry}"><%--
    --%><c:choose><%--
        --%><c:when test="${ssDefinitionEntry.entityType == 'folderEntry'}"><%--
            --%><c:set var="wikiLinkBinderId" value="${ssDefinitionEntry.parentFolder.id}" scope="request" /><%--
        --%></c:when><%--
        --%><c:when test="${ssDefinitionEntry.entityType == 'user'}"><%--
            --%><c:set var="wikiLinkBinderId" value="${ssDefinitionEntry.workspaceId}" scope="request" /><%--
        --%></c:when><%--
        --%><c:otherwise><%--
            --%><c:set var="wikiLinkBinderId" value="${ssDefinitionEntry.id}" scope="request" /><%--
        --%></c:otherwise><%--
    --%></c:choose><%--
--%></c:if><%--
--%><c:if test="${empty ssDefinitionEntry}"><%--
    --%><c:if test="${!empty ssFolder}"><%--
            --%><c:set var="wikiLinkBinderId" value="${ssFolder.id}" scope="request" /><%--
    --%></c:if><%--
    --%><c:if test="${empty ssFolder && !empty ssBinderId}"><%--
            --%><c:set var="wikiLinkBinderId" value="${ssBinderId}" scope="request" /><%--
    --%></c:if><%--
--%></c:if><%--
--%><script type="text/javascript" src="<html:tinyMcePath/>tiny_mce.js"></script>
<%@ include file="/WEB-INF/jsp/common/tinymce_translations.jsp" %>
<script type="text/javascript">
<c:if test="${!empty ss_diskQuotaExceeded && !ss_isBinderMirroredFolder}">
var ss_diskQuotaExceeded = ${ss_diskQuotaExceeded};
</c:if>
<c:if test="${empty ss_diskQuotaExceeded || ss_isBinderMirroredFolder}">
var ss_diskQuotaExceeded = false;
</c:if>
var ss_imageSelections${element_id} = "<select name='srcUrl' id='srcUrl'><%--
--%><c:forEach var="selection" items="${ssDefinitionEntry.fileAttachments}" ><%--
--%><option value='<ssf:escapeJavaScript value="${selection.fileItem.name}"/>'>${selection.fileItem.name}</option><%--
--%></c:forEach></select>";

<c:choose>
	<c:when test="${ssUser.locale.language == 'cs'}"><c:set var="ss_user_lang" value="cs" /></c:when>
	<c:when test="${ssUser.locale.language == 'da'}"><c:set var="ss_user_lang" value="da" /></c:when>
	<c:when test="${ssUser.locale.language == 'de'}"><c:set var="ss_user_lang" value="de" /></c:when>
	<c:when test="${ssUser.locale.language == 'es'}"><c:set var="ss_user_lang" value="es" /></c:when>
	<c:when test="${ssUser.locale.language == 'fi'}"><c:set var="ss_user_lang" value="fi" /></c:when>
	<c:when test="${ssUser.locale.language == 'fr'}"><c:set var="ss_user_lang" value="fr" /></c:when>
	<c:when test="${ssUser.locale.language == 'hu'}"><c:set var="ss_user_lang" value="hu" /></c:when>
	<c:when test="${ssUser.locale.language == 'it'}"><c:set var="ss_user_lang" value="it" /></c:when>
	<c:when test="${ssUser.locale.language == 'ja'}"><c:set var="ss_user_lang" value="ja" /></c:when>
	<c:when test="${ssUser.locale.language == 'nl'}"><c:set var="ss_user_lang" value="nl" /></c:when>
	<c:when test="${ssUser.locale.language == 'pl'}"><c:set var="ss_user_lang" value="pl" /></c:when>
	<c:when test="${ssUser.locale.language == 'pt'}"><c:set var="ss_user_lang" value="pt" /></c:when>
	<c:when test="${ssUser.locale.language == 'tr'}"><c:set var="ss_user_lang" value="tr" /></c:when>
	<c:when test="${ssUser.locale.language == 'ru'}"><c:set var="ss_user_lang" value="ru" /></c:when>
	<c:when test="${ssUser.locale.language == 'uk'}"><c:set var="ss_user_lang" value="uk" /></c:when>
	<c:when test="${ssUser.locale.language == 'sv'}"><c:set var="ss_user_lang" value="sv" /></c:when>
	<c:when test="${ssUser.locale.country == 'TW'}"><c:set var="ss_user_lang" value="tw" /></c:when>
	<c:when test="${ssUser.locale.country == 'CN'}"><c:set var="ss_user_lang" value="zh" /></c:when>
	<c:when test="${ssUser.locale.language == 'zh'}"><c:set var="ss_user_lang" value="zh" /></c:when>
	<c:otherwise><c:set var="ss_user_lang" value="en" /></c:otherwise>
</c:choose>

tinyMCE.init({
  paste_postprocess: function(pi,o){o.node.innerHTML=TinyMCEWebKitPasteFixup("paste_postprocess",o.node.innerHTML);},
  mode: "specific_textareas", editor_selector: "mceEditable_standard",
  theme : "advanced",
  onpageload : "ss_addLanguageChanges",
  language: "${ss_user_lang}",
  content_css: "<html:rootPath/>css/view_css_tinymce_editor.css",
  relative_urls: false, 
  remove_script_host : false,
  document_base_url : "<ssf:fileUrl entity="${ssDefinitionEntry}" baseUrl="true"/>",
  width: "100%",
  accessibility_warnings: true,
  accessibility_focus: true,
  entities:  "39,#39,34,quot,38,amp,60,lt,62,gt",
  gecko_spellcheck : true,
  plugins: "pdw,table,preelementfix,<%--
  --%><c:if test="${empty ssInlineNoImage && !ssBinder.mirrored}">ss_addimage,</c:if><%--
  --%>preview,paste,ss_wikilink<c:if test="${isShowYouTube}">,ss_youtube</c:if>",
  theme_advanced_toolbar_location: "top", theme_advanced_toolbar_align: "top", 
  theme_advanced_toolbar_align: "left", theme_advanced_statusbar_location: "bottom", 
  theme_advanced_resizing: true, 
  convert_fonts_to_spans: true,
  theme_advanced_styles: "8px=ss_size_8px;9px=ss_size_9px;10px=ss_size_10px;11px=ss_size_11px;12px=ss_size_12px;13px=ss_size_13px;14px=ss_size_14px;15px=ss_size_15px;16px=ss_size_16px;18px=ss_size_18px;20px=ss_size_20px;24px=ss_size_24px;28px=ss_size_28px;32px=ss_size_32px",
  theme_advanced_buttons1_add: "pdw_toggle",
  theme_advanced_buttons2_add: "|<%--
  --%><c:if test="${empty ssInlineNoImage && !ssBinder.mirrored}">,ss_addimage</c:if><%--
  --%><c:if test="${!empty wikiLinkBinderId}">,ss_wikilink</c:if><c:if test="${isShowYouTube}">,ss_youtube</c:if>",
  theme_advanced_path: false,
  pdw_toggle_on : 1,
  pdw_toggle_toolbars : "2",
  pdw_element_id : "ss_htmleditor_${element_name}",
  theme_advanced_resizing_min_width : 100,
  theme_advanced_resizing_min_height : 100,
  theme_advanced_resizing_use_cookie : true});

tinyMCE.addI18n('${ss_user_lang}.ss_addimage_dlg',{
overQuota : "${ss_quotaMessage} ",
srcFile : "<ssf:nlt tag="editor.addimage.srcFile"/>",
addFile : "<ssf:nlt tag="editor.addimage.addFile"/>",
addUrl : "<ssf:nlt tag="editor.addimage.addUrl"/>",
imageName : "<ssf:nlt tag="editor.addimage.imageName"/>",
imageSelectBox : ss_imageSelections${element_id},
missing_img : "<ssf:nlt tag="editor.addimage.noImage"/>"
});

tinyMCE.addI18n('${ss_user_lang}.pdw',{
	description : "<ssf:nlt tag="editor.pdw.desc"/>"
	});
tinyMCE.addI18n('${ss_user_lang}.ss_addimage',{
desc_no : "<ssf:nlt tag="editor.addimage.overQuota"/>"
});
tinyMCE.addI18n('${ss_user_lang}.ss_wikilink',{
desc : "<ssf:nlt tag="editor.wikilink.title"/>"
});
tinyMCE.addI18n('${ss_user_lang}.ss_youtube',{
desc : "<ssf:nlt tag="editor.youtube.title"/>",
youTubeUrl : "<ssf:nlt tag="__youTubeUrl"/>",
dimensions : "<ssf:nlt tag="__youTubeDimensions"/>"
});

tinyMCE.init({
	  paste_postprocess: function(pi,o){o.node.innerHTML=TinyMCEWebKitPasteFixup("paste_postprocess",o.node.innerHTML);},
	  mode: "specific_textareas", editor_selector: "mceEditable_mirrored_file",
	  theme : "advanced",
	  onpageload : "ss_addLanguageChanges",
	  language: "${ss_user_lang}",
	  content_css: "<html:rootPath/>css/view_css_tinymce_editor.css",
	  relative_urls: false, 
	  remove_script_host : false,
	  document_base_url : "<ssf:fileUrl entity="${ssDefinitionEntry}" baseUrl="true"/>",
	  width: "100%",
	  accessibility_warnings: true,
	  accessibility_focus: true,
	  entities:  "39,#39,34,quot,38,amp,60,lt,62,gt",
	  gecko_spellcheck : true,
	  plugins: "pdw,table,preelementfix,<%--
	  --%>preview,paste,ss_wikilink<c:if test="${isShowYouTube}">,ss_youtube</c:if>",
	  theme_advanced_toolbar_location: "top", theme_advanced_toolbar_align: "top", 
	  theme_advanced_toolbar_align: "left", theme_advanced_statusbar_location: "bottom", 
	  theme_advanced_resizing: true, 
	  convert_fonts_to_spans: true,
	  theme_advanced_styles: "8px=ss_size_8px;9px=ss_size_9px;10px=ss_size_10px;11px=ss_size_11px;12px=ss_size_12px;13px=ss_size_13px;14px=ss_size_14px;15px=ss_size_15px;16px=ss_size_16px;18px=ss_size_18px;20px=ss_size_20px;24px=ss_size_24px;28px=ss_size_28px;32px=ss_size_32px",
	  theme_advanced_buttons1_add: "pdw_toggle",
	  theme_advanced_buttons2_add: "|<%--
	  --%><c:if test="${empty ssInlineNoImage && !ssBinder.mirrored}">,ss_addimage</c:if><%--
	  --%><c:if test="${!empty wikiLinkBinderId}">,ss_wikilink</c:if><c:if test="${isShowYouTube}">,ss_youtube</c:if>",
	  theme_advanced_path: false,
	  pdw_toggle_on : 1,
	  pdw_toggle_toolbars : "2",
	  pdw_element_id : "ss_htmleditor_${element_name}",
	  theme_advanced_resizing_min_width : 100,
	  theme_advanced_resizing_min_height : 100,
	  theme_advanced_resizing_use_cookie : true});

	tinyMCE.addI18n('${ss_user_lang}.pdw',{
		description : "<ssf:nlt tag="editor.pdw.desc"/>"
		});
	tinyMCE.addI18n('${ss_user_lang}.ss_wikilink',{
	desc : "<ssf:nlt tag="editor.wikilink.title"/>"
	});
	tinyMCE.addI18n('${ss_user_lang}.ss_youtube',{
	desc : "<ssf:nlt tag="editor.youtube.title"/>",
	youTubeUrl : "<ssf:nlt tag="__youTubeUrl"/>",
	dimensions : "<ssf:nlt tag="__youTubeDimensions"/>"
	});

tinyMCE.init({
	paste_postprocess: function(pi,o){o.node.innerHTML=TinyMCEWebKitPasteFixup("paste_postprocess",o.node.innerHTML);},
	mode: "specific_textareas", editor_selector: "mceEditable_minimal",
	theme : "simple",
	onpageload : "ss_addLanguageChanges",
	language: "${ss_user_lang}",
	  content_css: "<html:rootPath/>css/view_css_tinymce_editor.css",
	  relative_urls: false, 
	  remove_script_host : false,
	  document_base_url : "<ssf:fileUrl entity="${ssDefinitionEntry}" baseUrl="true"/>",
	  width: "100%",
	  accessibility_warnings: true,
	  accessibility_focus: true,
	  entities:  "39,#39,34,quot,38,amp,60,lt,62,gt",
	  gecko_spellcheck : true,
	  plugins: "table,preelementfix,<%--
	  --%><c:if test="${empty ssInlineNoImage && !ssBinder.mirrored}">ss_addimage,</c:if><%--
	  --%>preview,paste,ss_wikilink<c:if test="${isShowYouTube}">,ss_youtube</c:if>",
	  theme_advanced_buttons3_add : "pastetext,pasteword,selectall",
	  theme_advanced_toolbar_location: "top", theme_advanced_toolbar_align: "top", 
	  theme_advanced_toolbar_align: "left", theme_advanced_statusbar_location: "bottom", 
	  theme_advanced_resizing: true, 
	  convert_fonts_to_spans: true,
	  theme_advanced_styles: "8px=ss_size_8px;9px=ss_size_9px;10px=ss_size_10px;11px=ss_size_11px;12px=ss_size_12px;13px=ss_size_13px;14px=ss_size_14px;15px=ss_size_15px;16px=ss_size_16px;18px=ss_size_18px;20px=ss_size_20px;24px=ss_size_24px;28px=ss_size_28px;32px=ss_size_32px",
	  theme_advanced_buttons2_add: "|<%--
	  --%><c:if test="${empty ssInlineNoImage && !ssBinder.mirrored}">,ss_addimage</c:if><%--
	  --%><c:if test="${!empty wikiLinkBinderId}">,ss_wikilink</c:if><c:if test="${isShowYouTube}">,ss_youtube</c:if>",
	  theme_advanced_path: false,
	  theme_advanced_disable : "image,advimage",
	  theme_advanced_resizing_min_width : 100,
	  theme_advanced_resizing_min_height : 100,
	  theme_advanced_resizing_use_cookie : true});

	tinyMCE.addI18n('${ss_user_lang}.ss_addimage_dlg',{
	overQuota : "${ss_quotaMessage} ",
	srcFile : "<ssf:nlt tag="editor.addimage.srcFile"/>",
	addFile : "<ssf:nlt tag="editor.addimage.addFile"/>",
	addUrl : "<ssf:nlt tag="editor.addimage.addUrl"/>",
	imageName : "<ssf:nlt tag="editor.addimage.imageName"/>",
	imageSelectBox : ss_imageSelections${element_id},
	missing_img : "<ssf:nlt tag="editor.addimage.noImage"/>"
	});

	tinyMCE.addI18n('${ss_user_lang}.pdw',{
		description : "<ssf:nlt tag="editor.pdw.desc"/>"
		});
	tinyMCE.addI18n('${ss_user_lang}.ss_addimage',{
		desc_no : "<ssf:nlt tag="editor.addimage.overQuota"/>"
		});
	tinyMCE.addI18n('${ss_user_lang}.ss_wikilink',{
	desc : "<ssf:nlt tag="editor.wikilink.title"/>"
	});
	tinyMCE.addI18n('${ss_user_lang}.ss_youtube',{
	desc : "<ssf:nlt tag="editor.youtube.title"/>",
	youTubeUrl : "<ssf:nlt tag="__youTubeUrl"/>",
	dimensions : "<ssf:nlt tag="__youTubeDimensions"/>"
	});

var ss_imageUploadError1 = "<ssf:nlt tag="imageUpload.badFile"/>"
var ss_imageUploadUrl = "<ssf:url 
    adapter="true" 
    actionUrl="true"
    portletName="ss_forum" 
    action="__ajax_request">
	  <ssf:param name="operation" value="upload_image_file" />
    </ssf:url>";
var ss_wikiLinkUrl = "<ssf:url 
    adapter="true" 
    actionUrl="true"
    portletName="ss_forum" 
    action="__ajax_request">
	  <ssf:param name="operation" value="wikilink_form" />
	  <ssf:param name="binderId" value="${wikiLinkBinderId}" />
	  <ssf:param name="originalBinderId" value="${wikiLinkBinderId}" />
    </ssf:url>";
var ss_youTubeUrl = "<ssf:url 
    adapter="true" 
    actionUrl="true"
    portletName="ss_forum" 
    action="__ajax_request">
	  <ssf:param name="operation" value="youtube_form" />
    </ssf:url>";
var ss_invalidYouTubeUrl = "<%= NLT.get("__youTubeInvalidUrl").replaceAll("\"", "\\\\\"") %>";
</script>
</c:if>
<c:set var="ss_tinyMCE_hasBeenLoaded" value="1" scope="request"/>

<div align="left" style="<c:if test="${!empty element_color}">background-color:${element_color};
</c:if>">

<textarea class="ss_style mceEditable_${editor_toolbar}"
  style="<c:if test="${!empty element_height}">height:${element_height}px;</c:if> <c:if 
  test="${!empty element_color}">background-color:${element_color}; </c:if>"
  <c:if test="${!empty element_id}">
    id="${element_id}" 
  </c:if>
  <c:if test="${!empty element_name}">
    name="${element_name}" 
  </c:if>

>
  <% //need to escape cause html is going into textarea %>
 <c:out value="${init_text}" escapeXml="true"/>
<c:out value="${body}" escapeXml="true"/>
<%
	} else {
%>
<div align="left" style="<c:if test="${!empty element_color}">background-color:${element_color};
</c:if>">

<textarea 
  style="width:100%; <c:if test="${!empty element_height}">height:${element_height}px;</c:if> <c:if 
  test="${!empty element_color}">background-color:${element_color}; </c:if>"
  <c:if test="${!empty element_id}">
    id="${element_id}" 
  </c:if>
  <c:if test="${!empty element_name}">
    name="${element_name}" 
  </c:if>

><%--
	//Be very careful never to add any whitespace characters or cr-lf characters between 
	//  the opening <textarea> and the closing </textarea> in htmlarea_bottom.jsp
	//  Otherwise, they will get added to the form result
--%><c:out value="${init_text}" escapeXml="true"/><%--
--%><c:out value="${body}" escapeXml="true"/><%--
--%><%
	}
%>