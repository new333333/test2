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
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
<body class="ss_style_body tundra">
<div class="ss_pseudoPortal">
<div class="ss_style ss_portlet">
<script type="text/javascript">
</script>


<spam class="ss_titlebold"><ssf:nlt tag="administration.configure_roles.configure" text="Configure SiteScape Forum Roles"/></span>
<c:if test="${!empty ssException}">
<font color="red">

<span class="ss_largerprint"><c:out value="${ssException}"/></span>
<br/>

<c:if test="${!empty ssRoleUsers}">
<span style="padding-left:20px;"><ssf:nlt tag="errorcode.role.inuse.by"/></span>
<br/>
</c:if>

<c:forEach var="user" items="${ssRoleUsers}">
<span style="padding-left:40px;">${user}</span>
<br/>
</c:forEach>

</font>
</c:if>

<ssf:expandableArea title='<%= NLT.get("administration.configure_roles.add") %>'>
<form class="ss_style ss_form" method="post" 
	action="<ssf:url action="configure_roles" actionUrl="true"/>">
		
	<span class="ss_bold"><ssf:nlt tag="administration.configure_roles.name" text="Name"/></span>
	<input type="text" class="ss_text" size="70" name="roleName" maxlength="64"><br>
		
	<c:forEach var="operation" items="${ssWorkAreaOperations}">
		<input type="checkbox" name="<c:out value="${operation.value}"/>">
		<c:out value="${operation.key}"/><br>
	</c:forEach>		

	<input type="submit" class="ss_submit" name="addBtn" value="<ssf:nlt tag="button.add" text="Add"/>">
</form>
</ssf:expandableArea>

<br>
<hr>
<br>
<h3><ssf:nlt tag="administration.configure_roles.existing" text="Currently defined roles"/></h3>

<c:forEach var="function" items="${ssFunctions}">
<jsp:useBean id="function" type="org.kablink.teaming.security.function.Function" />
<ssf:expandableArea title='<%= NLT.getDef(function.getName()) %>'>
<form class="ss_style ss_form" method="post" 
	action="<ssf:url action="configure_roles" actionUrl="true"/>">
	<span class="ss_bold"><ssf:nlt tag="administration.configure_roles.name" text="Name"/></span>
	<input type="text" class="ss_text" size="70" name="roleName" value="${function.name}"><br>
	<c:forEach var="operation" items="${ssWorkAreaOperations}">
		<c:set var="checked" value=""/>
		<c:forEach var="roleOperation" items="${function.operations}">
			<c:if test="${roleOperation.name == operation.value}">
				<c:set var="checked" value="checked"/>
			</c:if>
		</c:forEach>
		<input type="checkbox" name="<c:out value="${operation.value}"/>" <c:out value="${checked}"/>>
		<c:out value="${operation.key}"/><br>
	</c:forEach>		
	<input type="hidden" name="roleId" value="${function.id}">
<div class="ss_buttonBarLeft">
	<input type="submit" class="ss_submit" name="modifyBtn" value="<ssf:nlt tag="button.apply" text="Apply"/>">
	<input type="submit" class="ss_submit" name="deleteBtn" value="<ssf:nlt tag="button.delete" text="Delete"/>">

</div>
</form>
<br/>
</ssf:expandableArea>

</c:forEach>

<br/>

<form class="ss_style ss_form" name="${renderResponse.namespace}rolesForm" method="post" 
	action="<ssf:url action="site_administration" actionUrl="false"/>" >

	<input type="button" class="ss_submit" name="closeBtn" value="<ssf:nlt tag="button.close" text="Close"/>"
		  onClick="self.window.close();return false;"/>
</form>
</div>

</div>
</body>
</html>
