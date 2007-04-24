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
<%@ page import="com.sitescape.team.util.NLT" %>
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>

<script type="text/javascript">
</script>

<div class="ss_style ss_portlet">
<h3><ssf:nlt tag="administration.configure_roles.configure" text="Configure SiteScape Forum Roles"/></h3>
<c:if test="${!empty ssException}">
<p><c:out value="${ssException}"/></p>
</c:if>
<ssf:expandableArea title="<%= NLT.get("administration.configure_roles.add") %>">
<form class="ss_style ss_form" method="post" 
	action="<portlet:actionURL><portlet:param 
	name="action" value="configure_roles"/></portlet:actionURL>">
		
	<span class="ss_bold"><ssf:nlt tag="administration.configure_roles.name" text="Name"/></span>
	<input type="text" class="ss_text" size="70" name="roleName"><br>
		
	<c:forEach var="operation" items="${ssWorkAreaOperations}">
		<input type="checkbox" name="<c:out value="${operation.key}"/>">
		<c:out value="${operation.value}"/><br>
	</c:forEach>		

	<input type="submit" class="ss_submit" name="addBtn" value="<ssf:nlt tag="button.add" text="Add"/>">
</form>
</ssf:expandableArea>

<br>
<hr>
<br>
<h3><ssf:nlt tag="administration.configure_roles.existing" text="Currently defined roles"/></h3>

<c:forEach var="function" items="${ssFunctions}">
<jsp:useBean id="function" type="com.sitescape.team.security.function.Function" />
<ssf:expandableArea title="<%= function.getName() %>">
<form class="ss_style ss_form" method="post" 
	action="<portlet:actionURL><portlet:param 
		name="action" value="configure_roles"/></portlet:actionURL>">

	<c:forEach var="operation" items="${ssWorkAreaOperations}">
		<c:set var="checked" value=""/>
		<c:forEach var="roleOperation" items="${function.operations}">
			<c:if test="${roleOperation.name == operation.key}">
				<c:set var="checked" value="checked"/>
			</c:if>
		</c:forEach>
		<input type="checkbox" name="<c:out value="${operation.key}"/>" <c:out value="${checked}"/>>
		<c:out value="${operation.value}"/><br>
	</c:forEach>		
	<input type="hidden" name="roleId" value="${function.id}">
<div class="ss_buttonBarLeft">
	<input type="submit" class="ss_submit" name="modifyBtn" value="<ssf:nlt tag="button.modify" text="Modify"/>">
<c:if test="${!function.reserved}">
	<input type="submit" class="ss_submit" name="deleteBtn" value="<ssf:nlt tag="button.delete" text="Delete"/>">
</c:if>
</div>
</form>
<br/>
</ssf:expandableArea>

</c:forEach>

<br/>

<form class="ss_style ss_form" name="<portlet:namespace/>rolesForm" method="post" 
	action="<portlet:renderURL windowState="normal" portletMode="view"></portlet:renderURL>">

	<input type="submit" class="ss_submit" name="closeBtn" value="<ssf:nlt tag="button.close" text="Close"/>">
</form>
</div>
