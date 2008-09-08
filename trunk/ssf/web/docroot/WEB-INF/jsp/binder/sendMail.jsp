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
<%@ page import="com.sitescape.team.util.NLT" %>
<ssf:ifadapter>
<body class="tundra">
</ssf:ifadapter>
<script type="text/javascript">
	var width = ss_getWindowWidth()/2;
	if (width < 700) width=700;
	var height = ss_getWindowHeight();
	if (height < 600) height=600;
self.window.resizeTo(width, height);
</script>
<div class="ss_style ss_portlet" style="padding:10px;">
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
  
<form class="ss_style ss_form" method="post" 
  onSubmit="return ss_onSubmit(this);" name="${renderResponse.namespace}fm">

<span class="ss_bold"><ssf:nlt tag="sendMail.title"/></span>
<table class="ss_style"  border="0" cellspacing="0" cellpadding="0" width="95%">
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
  <label for="self">${ssUser.title} (${ssUser.emailAddress})</label></span>
 </td></tr>
</table>

<table class="ss_style"  border ="0" cellspacing="0" cellpadding="3" width="95%">
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
<tr><td colspan="2">
	<ssf:clipboard type="user" formElement="users" />
	<c:if test="${!empty ssBinder}">
		<ssf:teamMembers binderId="${ssBinder.id}" formElement="users" appendAll="${appendTeamMembers}"/>
	</c:if>
</td></tr>
</table>
</fieldset>
</td></tr>
<tr><td>
<fieldset class="ss_fieldset">
  <legend class="ss_legend"><ssf:nlt tag="sendMail.message" /></legend>
 
<table class="ss_style"  border ="0" cellspacing="0" cellpadding="0" width="95%">
 <tr><td>
   <span class="ss_labelAbove ss_bold"><ssf:nlt tag="sendMail.subject"/></span>
   <!-- just use title, not docNumber so calendar entries contain summary -->
   <input class="ss_style" type="text" name="subject" id="subject" size="86" <c:if test="${!empty ssEntry}">value="${ssEntry.title}" </c:if>>
</td></tr>
<tr><td>
   <span class="ss_labelAbove ss_bold"><c:if test="${!empty ssEntry}"><ssf:nlt tag="entry.sendMail.body"/></c:if><c:if test="${empty ssEntry}"><ssf:nlt tag="sendMail.message"/></c:if></span>
    <div align="left">
    <ssf:htmleditor name="mailBody" height="200">
	<br/>

<c:if test="${empty ssEntry}">
  <a href="<ssf:permalink entity="${ssBinder}"/>">${ssBinder.title}</a><br/>
</c:if>
  </ssf:htmleditor>
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
</td></tr>
</table>
<br/>
<div class="ss_buttonBarLeft">
<input type="submit" class="ss_submit" name="okBtn" value="<ssf:nlt tag="button.ok"/>">
<input type="submit" class="ss_submit" name="closeBtn" value="<ssf:nlt tag="button.close"/>" onClick="window.close();">
</div>
</form>

</c:otherwise>
</c:choose>
</div>
<ssf:ifadapter>
</body>
</html>
</ssf:ifadapter>
