<%
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
%>
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
<%@ page import="org.kablink.teaming.util.NLT" %>
<ssf:ifadapter>
<body class="tundra">
</ssf:ifadapter>
<script type="text/javascript" src="<html:rootPath />js/jsp/tag_jsps/find/find.js"></script>
<script type="text/javascript">
	var width = ss_getWindowWidth()/2;
	if (width < 700) width=700;
	var height = ss_getWindowHeight();
	if (height < 600) height=600;
self.window.resizeTo(width, height);
</script>
<div class="ss_style ss_portlet" style="padding:10px;">
  
<ssf:form title='<%= NLT.get("sendMail.title") %>'>

<form class="ss_style ss_form" method="post" 
  onSubmit="return ss_onSubmit(this);" name="${renderResponse.namespace}fm">

<fieldset class="ss_fieldset">
  <legend class="ss_legend"><ssf:nlt tag="sendMail.recipients" /></legend>
<table class="ss_style" border ="0" cellspacing="4" cellpadding="4">
 <tr><td>
   <span class="ss_labelAbove ss_bold"><ssf:nlt tag="sendMail.addresses"/>:</span>
   <input class="ss_style" type="text" name="addresses" id="addresses" size="86" value="">
 </td></tr>
 <tr><td>
 <input class="ss_style" type="checkbox" name="self" id="self" >&nbsp;<span class="ss_labelRight">
  <label for="self">${ssUser.title} (${ssUser.emailAddress})</label></span>
 </td></tr>
</table>
<br/>

<table class="ss_style" border ="0" cellspacing="4" cellpadding="4">
<tr>
<td class="ss_bold" valign="top"><ssf:nlt tag="general.users" text="Users"/></td>
<td valign="top">
  <ssf:find formName="${renderResponse.namespace}fm" formElement="users" 
    type="user" userList="${ssUsers}" binderId="${ssBinder.id}"/>
</td>
</tr>
<tr>
<td class="ss_bold" valign="top"><ssf:nlt tag="general.groups" text="Groups"/></td>
<td valign="top">
  <ssf:find formName="${renderResponse.namespace}fm" formElement="groups" 
    type="group" userList="${ssGroups}"/>
</td>
</tr>
</table>
<br/>

<table class="ss_style" border ="0" cellspacing="4" cellpadding="4" width="95%">
<tr><td>
	<ssf:clipboard type="user" formElement="users" />
	<c:if test="${!empty ssBinder}">
		<ssf:teamMembers binderId="${ssBinder.id}" formElement="users" appendAll="${appendTeamMembers}"/>
	</c:if>
</td></tr>
</table>

<ssf:expandableArea title="CC">
<table class="ss_style" border="0" cellspacing="4" cellpadding="4">
<tr>
<td class="ss_bold" valign="top"><ssf:nlt tag="general.users" text="Users"/></td>
<td valign="top">
  <ssf:find formName="${renderResponse.namespace}fm" formElement="ccusers" 
    type="user" />
</td>
</tr>
<tr>
<td class="ss_bold" valign="top"><ssf:nlt tag="general.groups" text="Groups"/></td>
<td valign="top">
  <ssf:find formName="${renderResponse.namespace}fm" formElement="ccgroups" 
    type="group"/>
</td>
</tr>
</table>
</ssf:expandableArea>

<ssf:expandableArea title="BCC">
<table class="ss_style" border="0" cellspacing="4" cellpadding="4">
<tr>
<td class="ss_bold" valign="top"><ssf:nlt tag="general.users" text="Users"/></td>
<td valign="top">
  <ssf:find formName="${renderResponse.namespace}fm" formElement="bccusers" 
    type="user" />
</td>
</tr>
<tr>
<td class="ss_bold" valign="top"><ssf:nlt tag="general.groups" text="Groups"/></td>
<td valign="top">
  <ssf:find formName="${renderResponse.namespace}fm" formElement="bccgroups" 
    type="group"/>
</td>
</tr>
</table>
</ssf:expandableArea>

</fieldset>

<fieldset class="ss_fieldset">
  <legend class="ss_legend"><ssf:nlt tag="sendMail.message" /></legend>
 
<table class="ss_style"  border ="0" cellspacing="4" cellpadding="4" width="95%">
 <tr><td>
   <span class="ss_labelAbove ss_bold"><ssf:nlt tag="sendMail.subject"/></span>
   <!-- just use title, not docNumber so calendar entries contain summary -->
   <input class="ss_style" type="text" name="subject" id="subject" size="86" <c:if test="${!empty ssEntry}">value="${ssEntry.title}" </c:if>>
</td></tr>
<tr><td>
   <span class="ss_labelAbove ss_bold"><c:if test="${!empty ssEntry}"><ssf:nlt tag="entry.sendMail.body"/></c:if><c:if test="${empty ssEntry}"><ssf:nlt tag="sendMail.message"/></c:if></span>
    <div align="left">
 
 <script type="text/javascript" src="<html:rootPath/>js/tiny_mce/tiny_mce.js"></script>
<script type="text/javascript">
//need to shut off relative links and adding pictures for mail.
tinyMCE.init(
 {mode: "specific_textareas", editor_selector: "mceEditable",
<c:choose>
<c:when test="${language == 'da_DK'}">language: 'da',</c:when>
<c:when test="${language == 'de_DE'}">language: 'de',</c:when>
<c:when test="${language == 'es_ES'}">language: 'es',</c:when>
<c:when test="${language == 'fr_FR'}">language: 'fr',</c:when>
<c:when test="${language == 'hu_HU'}">language: 'hu',</c:when>
<c:when test="${language == 'it_IT'}">language: 'it',</c:when>
<c:when test="${language == 'ja_JP'}">language: 'ja_utf-8',</c:when>
<c:when test="${language == 'nl_NL'}">language: 'nl',</c:when>
<c:when test="${language == 'pl_PL'}">language: 'pl',</c:when>
<c:when test="${language == 'pt_BR'}">language: 'pt_br',</c:when>
<c:when test="${language == 'ru_RU'}">language: 'ru',</c:when>
<c:when test="${language == 'uk_UA'}">language: 'uk',</c:when>
<c:when test="${language == 'sv_SE'}">language: 'sv',</c:when>
<c:when test="${language == 'sv_SV'}">language: 'sv',</c:when>
<c:when test="${language == 'zh_CN'}">language: 'zh_cn_utf8',</c:when>
<c:when test="${language == 'zh_TW'}">language: 'zh_tw_utf8',</c:when>
<c:otherwise>locale: 'en',</c:otherwise>
</c:choose>
  content_css: "<ssf:url webPath="viewCss"><ssf:param name="sheet" value="editor"/></ssf:url>",
  relative_urls: false, 
  width: "100%",
<ssf:ifnotaccessible>  accessibility_focus: false,</ssf:ifnotaccessible>
<ssf:ifaccessible>  accessibility_focus: true,</ssf:ifaccessible>
  remove_script_host: false,
  entities:  "39,#39,34,quot,38,amp,60,lt,62,gt",
  gecko_spellcheck : true,
  plugins: "table,preview,paste", 
  theme_advanced_toolbar_location: "top", theme_advanced_toolbar_align: "top", 
  theme_advanced_toolbar_align: "left", theme_advanced_statusbar_location: "bottom", 
  theme_advanced_resizing: true, 
  convert_fonts_to_spans: true,
  theme_advanced_styles: "8px=ss_size_8px;9px=ss_size_9px;10px=ss_size_10px;11px=ss_size_11px;12px=ss_size_12px;13px=ss_size_13px;14px=ss_size_14px;15px=ss_size_15px;16px=ss_size_16px;18px=ss_size_18px;20px=ss_size_20px;24px=ss_size_24px;28px=ss_size_28px;32px=ss_size_32px",
  theme_advanced_buttons1_add: "forecolor,backcolor",
  theme_advanced_buttons2_add: "pastetext,pasteword",
  theme_advanced_path: false,
  theme_advanced_buttons3_add: "tablecontrols", 
  theme_advanced_resizing_use_cookie : false});

</script>
<div align="left">
<textarea class="ss_style mceEditable" name="mailBody" style="height:200px">
  <% //need to escape cause html is going into textarea %>
 <c:out value="${body}" escapeXml="true"/>
  </textarea>
  </div>
</td></tr>
<c:if test="${!empty ssEntry}">

<tr><td>
 <input type="checkbox" name="attachments" id="attachments" class="ss_style" >&nbsp;<span class="ss_labelRight">
  <ssf:nlt tag="sendMail.includeAttachments"/></span></td>
</td></tr>
</c:if>
</table>
</fieldset>

<br/>
<div class="ss_buttonBarLeft">
<input type="submit" class="ss_submit" name="okBtn" value="<ssf:nlt tag="button.ok"/>">
<input type="submit" class="ss_submit" name="closeBtn" value="<ssf:nlt tag="button.close"/>" onClick="window.close();">
</div>
</form>
</ssf:form>

</div>
<ssf:ifadapter>
</body>
</html>
</ssf:ifadapter>
