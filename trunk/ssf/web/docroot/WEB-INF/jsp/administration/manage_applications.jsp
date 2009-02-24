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
<%@ page import="org.kablink.teaming.util.NLT" %>
<%@ include file="/WEB-INF/jsp/common/common.jsp" %>
<c:set var="ss_windowTitle" value='<%= NLT.get("administration.manage.applications") %>' scope="request"/>
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
<body class="ss_style_body tundra">
<div class="ss_pseudoPortal">

<script type="text/javascript">

function ${renderResponse.namespace}_onsub(obj) {
	if (obj.name.value == '') {
		alert('<ssf:nlt tag="general.required.name"/>');
		return false;
	}
	return true;
}
</script>
<div class="ss_style ss_portlet">
<ssf:form titleTag="administration.manage.applications">

<div style="padding:10px;" id="ss_manageApplications">
<span class="ss_titlebold"><ssf:nlt tag="administration.manage.applications" /></span>
<br>
<br>

<c:if test="${!empty ssException}">
<font color="red">

<span class="ss_largerprint"><c:out value="${ssException}"/></span>
<br/>

</font>
</c:if>
<ssf:expandableArea title='<%= NLT.get("administration.add.application") %>'>
<form class="ss_style ss_form" method="post" 
	action="<ssf:url action="manage_applications" actionUrl="true">
		<ssf:param name="binderId" value="${ssBinder.id}"/>
		</ssf:url>" onSubmit="return(${renderResponse.namespace}_onsub(this))">
		
	<label for="title"><span class="ss_bold"><ssf:nlt tag="administration.add.applicationTitle"/></span><br/></label>
	<input type="text" class="ss_text" size="70" name="title" id="title"><br/><br/>
		
	<label for="name"><span class="ss_bold"><ssf:nlt tag="administration.add.applicationName"/></span><ssf:inlineHelp 
	  tag="ihelp.designers.data_name"/><br/></label>
	<input type="text" class="ss_text" size="70" name="name" id="name"><br/><br/>
		
	<label for="description"><span class="ss_bold"><ssf:nlt tag="administration.add.applicationDescription"/></span><br/></label>
	<textarea name="description" id="description" wrap="virtual" rows="4" cols="70"></textarea><br/><br/>
	
	<input type="checkbox" name="trusted" id="trusted" value="true"/> <label for="trusted"><span class="ss_bold"><ssf:nlt tag="administration.add.applicationTrusted"/></span><br/><br/></label>
	
	<label for="postUrl"><span class="ss_bold"><ssf:nlt tag="administration.add.applicationPostUrl"/></span><br/></label>
	<input type="text" class="ss_text" size="70" name="postUrl" id="postUrl"><br/><br/>
		
	<label for="timeout"><span class="ss_bold"><ssf:nlt tag="administration.add.applicationTimeout"/></span><br/></label>
	<input type="text" class="ss_text" size="15" name="timeout" id="timeout" value='<%=org.kablink.teaming.util.SPropsUtil.getString("remoteapp.timeout","60")%>'><br/><br/>
		
	<label for="maxIdleTime"><span class="ss_bold"><ssf:nlt tag="administration.add.applicationMaxIdleTime"/></span><br/></label>
	<input type="text" class="ss_text" size="15" name="maxIdleTime" id="maxIdleTime" value='<%=org.kablink.teaming.util.SPropsUtil.getString("remoteapp.maxIdleTime","3600")%>'><br/><br/>
		
	<input type="checkbox" name="sameHostPolicy" id="sameHostPolicy" value="true" checked/> <label for="sameHostPolicy"><span class="ss_bold"><ssf:nlt tag="administration.add.applicationSameHostPolicy"/></span><br/><br/><br/></label>
	
	<input type="submit" class="ss_submit" name="addBtn" value="<ssf:nlt tag="button.add" text="Add"/>">
</form>
</ssf:expandableArea>
<br/>
<br/>

<table>
<tr>
<td valign="top">
<span class="ss_bold"><ssf:nlt tag="administration.selectApplicationToManage"/></span>
<br/>
<div class="ss_indent_medium" id="ss_modifyApplications">
  <c:forEach var="application" items="${ss_applicationList}">
  	<a href="<ssf:url action="manage_applications" actionUrl="true"><ssf:param 
		name="binderId" value="${ssBinder.id}"/><ssf:param 
		name="entryId" value="${application._docId}"/></ssf:url>"
	><span>${application.title}</span> <span class="ss_smallprint">(${application._applicationName})</span></a><br/>
  </c:forEach>
</div>
</td>

<td valign="top">
<c:if test="${!empty ssApplication}">
<div class="ss_style ss_portlet" style="margin-left:20px; padding:8px; border:solid 1px black;">
<span class="ss_bold ss_largerprint">${ssApplication.title}</span> <span class="ss_smallprint">(${ssApplication.name})</span>
<br/>
<br/>
<form name="ss_applicationForm" id="ss_applicationForm" method="post"
  action="<ssf:url action="manage_applications" actionUrl="true"><ssf:param 
	name="binderId" value="${ssBinder.id}"/><ssf:param 
	name="entryId" value="${ssApplication.id}"/></ssf:url>"
  onSubmit="return ss_onSubmit(this);">
		
<ssf:expandableArea title='<%= NLT.get("administration.modify.application") %>'>
	<label for="title"><span class="ss_bold"><ssf:nlt tag="administration.add.applicationTitle"/></span><br/></label>
	<input type="text" class="ss_text" size="50" name="title" id="title" value="${ssApplication.title}"><br/><br/>
		
	<label for="postUrl"><span class="ss_bold"><ssf:nlt tag="administration.add.applicationPostUrl"/></span><br/></label>
	<input type="text" class="ss_text" size="50" name="postUrl" id="postUrl" value="${ssApplication.postUrl}"><br/><br/>		
		
	<label for="description"><span class="ss_bold"><ssf:nlt tag="administration.add.applicationDescription"/></span><br/></label>
	<textarea name="description" id="description" wrap="virtual" rows="4" cols="50">${ssApplication.description}</textarea><br/><br/>
	
	<label for="timeout"><span class="ss_bold"><ssf:nlt tag="administration.add.applicationTimeout"/></span><br/></label>
	<input type="text" class="ss_text" size="15" name="timeout" id="timeout" value="${ssApplication.timeout}"><br/><br/>		
		
	<input type="checkbox" name="trusted" id="trusted" value="true" <c:if test="${ssApplication.trusted}">checked</c:if>/> <label for="trusted"><span class="ss_bold"><ssf:nlt tag="administration.add.applicationTrusted"/></span><br/><br/></label>
</ssf:expandableArea>

<br/>
<input type="submit" class="ss_submit" name="okBtn" value="<ssf:nlt tag="button.apply"/>">
<input type="submit" class="ss_submit" name="deleteBtn" value="<ssf:nlt tag="button.delete"/>">
 
</form>
</div>

</c:if>
</td>

</tr>
</table>

<br/>

<div class="ss_formBreak"/>

<form class="ss_style ss_form" method="post"
		  action="<ssf:url action="manage_applications" actionUrl="true"><ssf:param 
		  name="binderId" value="${ssBinder.id}"/></ssf:url>" 
		  name="${renderResponse.namespace}fm">
<div class="ss_buttonBarLeft">

<input type="button" class="ss_submit" name="closeBtn" value="<ssf:nlt tag="button.close" text="Close"/>"
		  onClick="self.window.close();return false;"/>
</div>
</form>
</div>

</div>
</ssf:form>
</div>

</div>
</body>
</html>