<%
/**
 * Copyright (c) 2007 SiteScape, Inc. All rights reserved.
 *
 * The information in this document is subject to change without notice 
 * and should not be construed as a commitment by SiteScape, Inc.  
 * SiteScape, Inc. assumes no responsibility for any errors that may appear 
 * in this document.
 *
 * Restricted Rights:  Use, duplication, or disclosure by the U.S. Government 
 * is subject to restrictions as set forth in subparagraph (c)(1)(ii) of the
 * Rights in Technical Data and Computer Software clause at DFARS 252.227-7013.
 *
 * SiteScape and SiteScape Forum are trademarks of SiteScape, Inc.
 */
%>
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
<%@ page import="com.sitescape.ef.util.NLT" %>
<ssf:ifadapter>
<body>
</ssf:ifadapter>
<script type="text/javascript">
	var width = ss_getWindowWidth()/2;
	if (width < 700) width=700;
	var height = ss_getWindowHeight();
	if (height < 600) height=600;
self.window.resizeTo(width, height);
</script>
<div class="ss_style ss_portlet">
<c:choose>
<c:when test="${!empty ssErrorList}">
<form class="ss_style ss_form" method="post">
<span class="ss_titlebold"><ssf:nlt tag="sendMail.status"/></span><br/>
<br/>
<ul>
<c:forEach var="item" items="${ssErrorList}">
	<li>${item}</li>
</c:forEach>
</ul>
<c:if test="${!empty ssEmailAddresses}">
<span class="ss_titlebold"><ssf:nlt tag="sendMail.distribution"/></span><br/>
<br/>
<ul>
<c:forEach var="item" items="${ssEmailAddresses}">
	<li>${item}</li>
</c:forEach>
</ul>
</c:if>
<div class="ss_buttonBarLeft">
<input type="submit" class="ss_submit" name="closeBtn" value="<ssf:nlt tag="button.close"/>" onClick="self.window.close();return false;">
</div>
</form>
</c:when>
<c:otherwise>
<script type="text/javascript" src="<html:rootPath/>js/tiny_mce/tiny_mce.js"></script>
<script type="text/javascript">tinyMCE.init(
 {mode: "specific_textareas", editor_selector: "mceEditable",
  language: "en", 
  content_css: "<html:rootPath/>css/editor.css", 
  relative_urls: false, accessibility_focus: false,
  remove_script_host: false,
  plugins: "table,advimage,preview,contextmenu,paste", 
  theme_advanced_toolbar_location: "top", theme_advanced_toolbar_align: "top", 
  theme_advanced_toolbar_align: "left", theme_advanced_statusbar_location: "bottom", 
  theme_advanced_resizing: true, 
  theme_advanced_buttons2_add: "pastetext,pasteword,advimage,spellchecker",
  theme_advanced_buttons3_add: "tablecontrols", 
  theme_advanced_resizing_use_cookie : false});</script>
  
<form class="ss_style ss_form" method="post" 
  onSubmit="return ss_onSubmit(this);" name="<portlet:namespace />fm">

<span class="ss_bold"><ssf:nlt tag="sendMail.title"/></span>
<table class="ss_style"  border="0" cellspacing="0" cellpadding="0" width="50%">
 <tr><td>
<fieldset class="ss_fieldset">
  <legend class="ss_legend"><ssf:nlt tag="sendMail.recipients" /></legend>
<table class="ss_style"  border ="0" cellspacing="0" cellpadding="0">
 <tr><td>
   <span class="ss_labelAbove ss_bold"><ssf:nlt tag="sendMail.addresses"/>:</span>
   <input class="ss_style" type="text" name="addresses" id="addresses" size="86" value="">
 </td></tr>
 <tr><td>
 <input class="ss_style" type="checkbox" name="self" id="self" >&nbsp;<span class="ss_labelRight">
  ${ssUser.title} (${ssUser.emailAddress})</span>
 </td></tr>
 <c:if test="${!empty ssTeamMembership}">
 <tr><td>
 <input type="checkbox" class="ss_style" name="teamMembers" id="teamMembers" >&nbsp;<span class="ss_labelRight">
  <ssf:nlt tag="sendMail.team"/></span>
 </td></tr>
 </c:if>
</table>

<table class="ss_style"  border ="0" cellspacing="0" cellpadding="3">
<tr>
<td class="ss_bold" valign="top"><ssf:nlt tag="general.users" text="Users"/></td>
<td valign="top">
  <ssf:find formName="${renderResponse.namespace}fm" formElement="users" 
    type="user" userList="${ssUsers}"/>
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
</fieldset>
</td></tr>
<tr><td>
<fieldset class="ss_fieldset">
  <legend class="ss_legend"><ssf:nlt tag="sendMail.message" /></legend>
 
<table class="ss_style"  border ="0" cellspacing="0" cellpadding="0">
 <tr><td>
   <span class="ss_labelAbove ss_bold"><ssf:nlt tag="sendMail.subject"/></span>
   <input class="ss_style" type="text" name="subject" id="subject" size="86" <c:if test="${!empty ssEntry}">value="${ssEntry.docNumber}. ${ssEntry.title}" </c:if>>
</td></tr>
<tr><td>
   <span class="ss_labelAbove ss_bold"><ssf:nlt tag="sendMail.message"/></span>
   <textarea class="ss_style mceEditable" rows="20" cols="80"
  name="body">
<c:if test="${!empty ssEntry}">
	  <ssf:displayConfiguration configDefinition="${ssConfigDefinition}" 
	    configElement="${ssConfigElement}" 
	    configJspStyle="${ssConfigJspStyle}"
	    processThisItem="true" 
	    entry="${ssEntry}" />
</c:if>
  </textarea>
</td></tr>
<c:if test="${!empty ssEntry}">
<tr><td>
 <input type="checkbox" name="attachments" id="attachments" class="ss_style" >&nbsp;<span class="ss_labelRight">
  <ssf:nlt tag="sendMail.includeAttachments"/></span></td>
</td></tr>
</c:if>
</table>
</fieldset>
</td></tr>
</table>
<br/>
<div class="ss_buttonBarLeft">
<input type="submit" class="ss_submit" name="okBtn" value="<ssf:nlt tag="button.ok"/>">
<input type="submit" class="ss_submit" name="closeBtn" value="<ssf:nlt tag="button.close"/>" onClick="self.window.close();return false;">
</div>
</form>

</c:otherwise>
</c:choose>
</div>
<ssf:ifadapter>
</body>
</html>
</ssf:ifadapter>
