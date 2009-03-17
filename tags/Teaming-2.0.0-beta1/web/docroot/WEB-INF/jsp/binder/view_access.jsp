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
<c:set var="ss_windowTitle" value='<%= NLT.get("toolbar.whoHasAccess") %>' scope="request"/>
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
<body class="ss_style_body tundra">

<div class="ss_portlet">
<c:set var="title_tag" value="access.whoHasAccess.workspace"/>
<c:if test="${ssBinder.entityType == 'folder'}">
  <c:set var="title_tag" value="access.whoHasAccess.folder"/>
</c:if>
<c:if test="${ssBinder.entityType == 'profiles'}">
  <c:set var="title_tag" value="access.whoHasAccess.profiles"/>
</c:if>
<ssf:form titleTag="${title_tag}">
<div class="ss_style ss_form" style="margin:0px; padding:10px 16px 10px 10px;">
<div style="margin:6px; width:100%;">
<table cellpadding="0" cellspacing="0" width="100%">
<tr>
<td valign="top">
<c:choose>
<c:when test="${ssWorkArea.workAreaType == 'folder'}">
  <span><ssf:nlt tag="access.currentFolder"/></span>
<span class="ss_bold"><ssf:nlt tag="${ssWorkArea.title}" checkIfTag="true"/></span>
</c:when>
<c:otherwise>
  <span><ssf:nlt tag="access.currentWorkspace"/></span>
	<% //need to check tags for templates %>
	<span class="ss_bold"><ssf:nlt tag="${ssWorkArea.title}" checkIfTag="true"/></span>
</c:otherwise>
</c:choose>
<br/>
<c:if test="${ssWorkArea.workAreaType == 'folder'}">
  <span><ssf:nlt tag="access.folderOwner"/></span>
</c:if>
<c:if test="${ssWorkArea.workAreaType != 'folder'}">
  <span><ssf:nlt tag="access.workspaceOwner"/></span>
</c:if>
<span id="ss_accessControlOwner${renderResponse.namespace}"
  class="ss_bold">${ssWorkArea.owner.title} 
  <span class="ss_normal ss_smallprint ss_italic">(${ssWorkArea.owner.name})</span></span>
</td>
<td align="right" valign="top">
<form class="ss_form" method="post" style="display:inline;" 
	action="<ssf:url ><ssf:param 
	name="action" value="configure_access_control"/><ssf:param 
	name="actionUrl" value="true"/><ssf:param 
	name="workAreaId" value="${ssWorkArea.workAreaId}"/><ssf:param 
	name="workAreaType" value="${ssWorkArea.workAreaType}"/></ssf:url>">
  <input type="submit" class="ss_submit" name="closeBtn" 
    value="<ssf:nlt tag="button.close" text="Close"/>">
</form>
</td>
</tr>

<c:if test="${ssWorkArea.functionMembershipInheritanceSupported}">
  <tr>
  <td colspan="2">
  <br/>
  <c:if test="${ssWorkArea.functionMembershipInherited}">
	<c:if test="${ssWorkArea.workAreaType == 'folder'}">
      <span class="ss_bold"><ssf:nlt tag="binder.configure.access_control.inheriting"/></span>
    </c:if>
	<c:if test="${ssWorkArea.workAreaType != 'folder'}">
      <span class="ss_bold"><ssf:nlt tag="binder.configure.access_control.inheritingWorkspace"/></span>
    </c:if>
  </c:if>
  <c:if test="${!ssWorkArea.functionMembershipInherited}">
	<c:if test="${ssWorkArea.workAreaType == 'folder'}">
      <span class="ss_bold"><ssf:nlt tag="binder.configure.access_control.notInheriting" /></span>
    </c:if>
	<c:if test="${ssWorkArea.workAreaType != 'folder'}">
      <span class="ss_bold"><ssf:nlt tag="binder.configure.access_control.notInheritingWorkspace" /></span>
    </c:if>
  </c:if>
  </td>
  </tr>
</c:if>
</table>



<ssf:box style="rounded">
<div style="padding:4px 8px;">
<c:if test="${ss_accessFunctionsCount <= 0}">
<span class="ss_bold ss_italic"><ssf:nlt tag="access.noRoles"/></span><br/>
</c:if>
<c:if test="${ss_accessFunctionsCount > 0}">

<c:set var="ss_namespace" value="${renderResponse.namespace}" scope="request"/>

<c:set var="ss_accessControlTableDivId" value="ss_accessControlDiv${renderResponse.namespace}" scope="request"/>
<%@ include file="/WEB-INF/jsp/binder/view_access_table.jsp" %>

<br/>

</c:if>
<br/>
<br/>
<span class="ss_italic ss_small">[<ssf:nlt tag="access.superUser">
  <ssf:param name="value" value="${ss_superUser.title}"/>
  <ssf:param name="value" value="${ss_superUser.name}"/>
  </ssf:nlt>]</span><br/>
</div>
</ssf:box>


<br/>
<br/>

<form class="ss_form" method="post" style="display:inline;" 
	action="<ssf:url ><ssf:param 
	name="action" value="configure_access_control"/><ssf:param 
	name="actionUrl" value="true"/><ssf:param 
	name="workAreaId" value="${ssWorkArea.workAreaId}"/><ssf:param 
	name="workAreaType" value="${ssWorkArea.workAreaType}"/></ssf:url>">
  <input type="submit" class="ss_submit" name="closeBtn" 
    value="<ssf:nlt tag="button.close" text="Close"/>">
</form>
</div>
</div>

<c:forEach var="function" items="${ssFunctions}">
<jsp:useBean id="function" type="org.kablink.teaming.security.function.Function" />
<div id="${renderResponse.namespace}ss_operations${function.id}" class="ss_style ss_portlet"
  style="position:absolute; display:none; width:300px; border:1px solid #000000; 
  margin-bottom:10px; padding:4px; background-color:#ffffff;">
  <div align="right">
    <a href="javascript:;" onClick="ss_hideDiv('${renderResponse.namespace}ss_operations${function.id}');return false;">
      <img border="0" src="<html:imagesPath/>icons/close_off.gif" <ssf:alt tag="alt.hideThisMenu"/>/>
    </a>
  </div>
  <div>
	<span class="ss_bold"><%= NLT.getDef(function.getName()) %></span><br/>
	<c:forEach var="operation" items="${ssWorkAreaOperations}">
		<c:set var="checked" value=""/>
		<c:forEach var="roleOperation" items="${function.operations}">
			<c:if test="${roleOperation.name == operation.value}">
				<c:out value="${operation.key}"/><br>
			</c:if>
		</c:forEach>
	</c:forEach>	
  </div>	
</div>
</c:forEach>

</ssf:form>
</div>

</body>
</html>
