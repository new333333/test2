<%
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
%>
<% // htmlarea editor %>
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
<%@ page import="java.lang.String" %>
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
--%></c:if><%--
--%><script type="text/javascript" src="<html:rootPath/>js/tiny_mce/tiny_mce.js"></script>
<script type="text/javascript">
tinyMCE.init(
 {mode: "specific_textareas", editor_selector: "mceEditable",
  language: "${language}", 
  content_css: "<ssf:url webPath="viewCss"><ssf:param name="sheet" value="editor"/></ssf:url>",
  relative_urls: true, 
  width: "100%",
<ssf:ifnotaccessible>  accessibility_focus: false,</ssf:ifnotaccessible>
<ssf:ifaccessible>  accessibility_focus: true,</ssf:ifaccessible>
  remove_script_host: false,
  entities:  "39,#39,160,nbsp,161,iexcl,162,cent,163,pound,164,curren,165,yen,166,brvbar,167,sect,168,uml,169,copy,170,ordf,171,laquo,172,not,173,shy,174,reg,175,macr,176,deg,177,plusmn,178,sup2,179,sup3,180,acute,181,micro,182,para,183,middot,184,cedil,185,sup1,186,ordm,187,raquo,188,frac14,189,frac12,190,frac34,191,iquest,402,fnof,8226,bull,8230,hellip,8242,prime,8243,Prime,8254,oline,8260,frasl,8472,weierp,8465,image,8476,real,8482,trade,8501,alefsym,8592,larr,8593,uarr,8594,rarr,8595,darr,8596,harr,8629,crarr,8656,lArr,8657,uArr,8658,rArr,8659,dArr,8660,hArr,8704,forall,8706,part,8707,exist,8709,empty,8711,nabla,8712,isin,8713,notin,8715,ni,8719,prod,8721,sum,8722,minus,8727,lowast,8730,radic,8733,prop,8734,infin,8736,ang,8743,and,8744,or,8745,cap,8746,cup,8747,int,8756,there4,8764,sim,8773,cong,8776,asymp,8800,ne,8801,equiv,8804,le,8805,ge,8834,sub,8835,sup,8836,nsub,8838,sube,8839,supe,8853,oplus,8855,otimes,8869,perp,8901,sdot,8968,lceil,8969,rceil,8970,lfloor,8971,rfloor,9001,lang,9002,rang,9674,loz,9824,spades,9827,clubs,9829,hearts,9830,diams,34,quot,38,amp,60,lt,62,gt,338,OElig,339,oelig,352,Scaron,353,scaron,376,Yuml,710,circ,732,tilde,8194,ensp,8195,emsp,8201,thinsp,8204,zwnj,8205,zwj,8206,lrm,8207,rlm,8211,ndash,8212,mdash,8216,lsquo,8217,rsquo,8218,sbquo,8220,ldquo,8221,rdquo,8222,bdquo,8224,dagger,8225,Dagger,8240,permil,8249,lsaquo,8250,rsaquo,8364,euro",
  gecko_spellcheck : true,
  plugins: "table,<%--
  --%><c:if test="${empty ssInlineNoImage}">ss_addimage,</c:if><%--
  --%>preview,paste,ss_wikilink", 
  theme_advanced_toolbar_location: "top", theme_advanced_toolbar_align: "top", 
  theme_advanced_toolbar_align: "left", theme_advanced_statusbar_location: "bottom", 
  theme_advanced_resizing: true, 
  convert_fonts_to_spans: true,
  theme_advanced_styles: "8px=ss_size_8px;9px=ss_size_9px;10px=ss_size_10px;11px=ss_size_11px;12px=ss_size_12px;13px=ss_size_13px;14px=ss_size_14px;15px=ss_size_15px;16px=ss_size_16px",
  theme_advanced_buttons1_add: "forecolor,backcolor",
  theme_advanced_buttons2_add: "pastetext,pasteword<%--
  --%><c:if test="${empty ssInlineNoImage}">,ss_addimage</c:if><%--
  --%><c:if test="${!empty wikiLinkBinderId}">,ss_wikilink</c:if>",
  theme_advanced_path: false,
  theme_advanced_buttons3_add: "tablecontrols", 
  theme_advanced_resizing_use_cookie : false});

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
</script>
<div align="left" style="<c:if test="${!empty element_color}">background-color:${element_color};
</c:if>">
<textarea class="ss_style mceEditable"
  style="<c:if test="${!empty element_height}">height:${element_height}px;</c:if> <c:if 
  test="${!empty element_color}">background-color:${element_color}; </c:if>"
  <c:if test="${!empty element_id}">
    id="${element_id}" 
  </c:if>
  <c:if test="${!empty element_name}">
    name="${element_name}" 
  </c:if>
><c:out value="${init_text}"/>