<%
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
%>
<% // htmlarea editor %>
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
<%@ page import="org.kablink.teaming.util.SPropsUtil" %>
<%@ page import="java.lang.String" %>
<%@ page import="org.kablink.teaming.util.NLT" %>
<c:set var="ss_quotaMessage" value="" />
<c:if test="${ss_diskQuotaHighWaterMarkExceeded && !ss_diskQuotaExceeded}">
<c:set var="ss_quotaMessage" ><ssf:nlt tag="quota.nearLimit"><ssf:param name="value" useBody="true"
	    ><fmt:formatNumber value="${(ssUser.diskQuota*1048576 - ssUser.diskSpaceUsed)/1048576}" 
	    maxFractionDigits="2"/></ssf:param></ssf:nlt></c:set>
</c:if>
<c:if test="${ss_diskQuotaExceeded}">
<c:set var="ss_quotaMessage" ><ssf:nlt tag="quota.diskQuotaExceeded"/></c:set>
</c:if>
<c:set var="isShowYouTube" value='<%= SPropsUtil.getBoolean("youTube.showInEditor") %>'/>
<c:if test="${empty ss_tinyMCE_hasBeenLoaded}">
<c:set var="wikiLinkBinderId" value="" scope="request"/><%--
--%><c:if test="${!empty ssDefinitionEntry}"><%--
    --%><c:choose><%--
        --%><c:when test="${ssDefinitionEntry.entityType == 'folderEntry'}"><%--
            --%><c:set var="wikiLinkBinderId" value="${ssDefinitionEntry.parentFolder.id}" scope="request" /><%--
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
--%><script type="text/javascript" src="<html:rootPath/>js/tiny_mce/tiny_mce.js"></script>
<script type="text/javascript">
<c:if test="${!empty ss_diskQuotaExceeded}">
var ss_diskQuotaExceeded = ${ss_diskQuotaExceeded};
</c:if>
<c:if test="${empty ss_diskQuotaExceeded}">
var ss_diskQuotaExceeded = false;
</c:if>
var ss_imageSelections${element_id} = "<select name='srcUrl' id='srcUrl'><%--
--%><c:forEach var="selection" items="${ssDefinitionEntry.fileAttachments}" ><%--
--%><option value='<ssf:escapeJavaScript value="${selection.fileItem.name}"/>'>${selection.fileItem.name}</option><%--
--%></c:forEach></select>";
tinyMCE.init(
 {mode: "specific_textareas", editor_selector: "mceEditable",
  theme : "advanced", 
<c:choose><%--
    --%><c:when test="${ssUser.locale.language == 'da'}">language: 'da',</c:when><%--
    --%><c:when test="${ssUser.locale.language == 'de'}">language: 'de',</c:when><%--
    --%><c:when test="${ssUser.locale.language == 'es'}">language: 'es',</c:when><%--
    --%><c:when test="${ssUser.locale.language == 'fr'}">language: 'fr',</c:when><%--
    --%><c:when test="${ssUser.locale.language == 'hu'}">language: 'hu',</c:when><%--
    --%><c:when test="${ssUser.locale.language == 'it'}">language: 'it',</c:when><%--
    --%><c:when test="${ssUser.locale.language == 'ja'}">language: 'ja',</c:when><%--
    --%><c:when test="${ssUser.locale.language == 'nl'}">language: 'nl',</c:when><%--
    --%><c:when test="${ssUser.locale.language == 'pl'}">language: 'pl',</c:when><%--
    --%><c:when test="${ssUser.locale.language == 'pt'}">language: 'pt',</c:when><%--
    --%><c:when test="${ssUser.locale.language == 'ru'}">language: 'ru',</c:when><%--
    --%><c:when test="${ssUser.locale.language == 'uk'}">language: 'uk',</c:when><%--
    --%><c:when test="${ssUser.locale.language == 'sv'}">language: 'sv',</c:when><%--
    --%><c:when test="${ssUser.locale.language == 'sv'}">language: 'sv',</c:when><%--
    --%><c:when test="${ssUser.locale.language == 'zh'}">language: 'zh',</c:when><%--
    --%><c:otherwise>language: 'en',</c:otherwise><%--
--%></c:choose>
  content_css: "<ssf:url webPath="viewCss"><ssf:param name="sheet" value="editor"/></ssf:url>",
  relative_urls: false, 
  remove_script_host : false,
  document_base_url : "<ssf:fileUrl entity="${ssDefinitionEntry}" baseUrl="true"/>",
  width: "100%",
  accessibility_warnings: true,
  accessibility_focus: true,
  entities:  "39,#39,34,quot,38,amp,60,lt,62,gt",
  gecko_spellcheck : true,
  plugins: "compat2x,table,<%--
  --%><c:if test="${empty ssInlineNoImage && !ssBinder.mirrored}">ss_addimage,</c:if><%--
  --%>preview,paste,ss_wikilink<c:if test="${isShowYouTube}">,ss_youtube</c:if>",
  theme_advanced_buttons3_add : "pastetext,pasteword,selectall",
  theme_advanced_toolbar_location: "top", theme_advanced_toolbar_align: "top", 
  theme_advanced_toolbar_align: "left", theme_advanced_statusbar_location: "bottom", 
  theme_advanced_resizing: true, 
  convert_fonts_to_spans: true,
  theme_advanced_styles: "8px=ss_size_8px;9px=ss_size_9px;10px=ss_size_10px;11px=ss_size_11px;12px=ss_size_12px;13px=ss_size_13px;14px=ss_size_14px;15px=ss_size_15px;16px=ss_size_16px;18px=ss_size_18px;20px=ss_size_20px;24px=ss_size_24px;28px=ss_size_28px;32px=ss_size_32px",
  theme_advanced_buttons1_add: "forecolor,backcolor",
  theme_advanced_buttons2_add: "pastetext,pasteword<%--
  --%><c:if test="${empty ssInlineNoImage}">,ss_addimage</c:if><%--
  --%><c:if test="${!empty wikiLinkBinderId}">,ss_wikilink</c:if><c:if test="${isShowYouTube}">,ss_youtube</c:if>",
  theme_advanced_path: false,
  theme_advanced_buttons3_add: "tablecontrols", 
  theme_advanced_disable : "image,advimage",
  theme_advanced_resizing_use_cookie : false});

tinyMCE.addI18n('${ssUser.locale.language}.ss_addimage_dlg',{
overQuota : "${ss_quotaMessage} ",
srcFile : "<ssf:nlt tag="editor.addimage.srcFile"/>",
addFile : "<ssf:nlt tag="editor.addimage.addFile"/>",
addUrl : "<ssf:nlt tag="editor.addimage.addUrl"/>",
imageName : "<ssf:nlt tag="editor.addimage.imageName"/>",
imageSelectBox : ss_imageSelections${element_id},
missing_img : "<ssf:nlt tag="editor.addimage.noImage"/>"
});

tinyMCE.addI18n('${ssUser.locale.language}.ss_addimage',{
desc_no : "<ssf:nlt tag="editor.addimage.overQuota"/>"
});
tinyMCE.addI18n('${ssUser.locale.language}.ss_wikilink',{
desc : "<ssf:nlt tag="editor.wikilink.title"/>"
});
tinyMCE.addI18n('${ssUser.locale.language}.ss_youtube',{
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

  <c:if test="${!empty element_id}">
    <label for="${element_id}">&nbsp;</label>
  </c:if>
<textarea class="ss_style mceEditable"
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
