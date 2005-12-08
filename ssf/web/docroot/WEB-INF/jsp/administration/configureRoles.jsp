<%
/**
 * Copyright (c) 2005 SiteScape, Inc. All rights reserved.
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
<%@ page import="com.sitescape.ef.util.NLT" %>
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>

<script language="javascript" type="text/javascript">
</script>

<div class="ss_portlet">
<h3><ssf:nlt tag="administration.configure_roles.configure" text="Configure SiteScape Forum Roles"/></h3>
<ssf:expandableArea title="<%= NLT.get("administration.configure_roles.add") %>">
<form name="<portlet:namespace/>rolesForm" method="post" action="<portlet:actionURL>
			<portlet:param name="action" value="configure_roles"/>
		</portlet:actionURL>">
		
	<span><b><ssf:nlt tag="administration.configure_roles.name" text="Name"/></b></span>
	<input type="text" size="70" name="roleName"><br>
		
	<c:forEach var="operation" items="${ssWorkAreaOperations}">
		<input type="checkbox" name="<c:out value="${operation.key}"/>">
		<c:out value="${operation.value}"/><br>
	</c:forEach>		

	<input type="submit" name="addBtn" value="<ssf:nlt tag="button.add" text="Add"/>">
</form>
</ssf:expandableArea>

<br>
<hr>
<br>
<h3><ssf:nlt tag="administration.configure_roles.existing" text="Currently defined roles"/></h3>

<c:forEach var="function" items="${ssFunctions}">
<jsp:useBean id="function" type="com.sitescape.ef.security.function.Function" />
<ssf:expandableArea title="<%= function.getName() %>">
<form name="<portlet:namespace/>rolesForm" method="post" action="<portlet:actionURL>
			<portlet:param name="action" value="configure_roles"/>
		</portlet:actionURL>">

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
	<input type="submit" name="modifyBtn"
	 value="<ssf:nlt tag="button.modify" text="Modify"/>">
</form>
<br/>
</ssf:expandableArea>

</c:forEach>

<br/>

<form name="<portlet:namespace/>rolesForm" method="post" action="<portlet:actionURL>
			<portlet:param name="action" value="configure_roles"/>
		</portlet:actionURL>">

	<input type="submit" name="closeBtn" value="<ssf:nlt tag="button.close" text="Close"/>">
</form>
</div>
