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
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>

<script language="javascript" type="text/javascript">
</script>

<h3><ssf:nlt tag="admin.roles.configure" text="Configure SiteScape Forum Roles"/></h3>
<form name="<portlet:namespace/>rolesForm" method="post" action="<portlet:actionURL>
			<portlet:param name="action" value="configure_roles"/>
		</portlet:actionURL>">
		
	<span><b><ssf:nlt tag="admin.roles.add" text="Add a new role"/></b></span>
	<span><b><ssf:nlt tag="admin.roles.name" text="Name"/></b></span>
	<input type="text" size="100" name="roleName"><br>
		
	<c:forEach var="operation" items="${ssWorkAreaOperations}">
		<input type="checkbox" name="<c:out value="${operation.key}"/>">
		<c:out value="${operation.value}"/><br>
	</c:forEach>		

	<input type="submit" name="addBtn" value="<ssf:nlt tag="common.add" text="Add"/>">
<form>

<br>
<hr>
<br>

<form name="<portlet:namespace/>rolesForm" method="post" action="<portlet:actionURL>
			<portlet:param name="action" value="configure_roles"/>
		</portlet:actionURL>">

		<c:forEach var="function" items="${ssFunctions}">
		<c:out value="${function.value.id}"/><br>
		</c:forEach>
	<input type="submit" name="modifyBtn"
	 value="<ssf:nlt tag="common.modify" text="Modify"/>">
<form>

<br>
<hr>
<br>

<form name="<portlet:namespace/>rolesForm" method="post" action="<portlet:actionURL>
			<portlet:param name="action" value="configure_roles"/>
		</portlet:actionURL>">

	<input type="submit" name="cancelBtn" value="<ssf:nlt tag="common.cancel" text="Cancel"/>">
<form>

