<%
/**
 * Copyright (c) 2006 SiteScape, Inc. All rights reserved.
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

<div class="ss_style ss_portlet">
<h3><ssf:nlt tag="binder.configure.access_control" text="Configure access control"/></h3>

<c:if test="${ssFunctionMembershipInherited}">
This folder is inheriting its access control settings from its parent folder.<br>
</c:if>

<c:if test="${!ssFunctionMembershipInherited}">
<c:forEach var="function" items="${ssFunctions}">
<jsp:useBean id="function" type="com.sitescape.ef.security.function.Function" />
<ssf:expandableArea title="<%= function.getName() %>">
<form class="ss_style" name="<portlet:namespace/>rolesForm" method="post" action="<portlet:actionURL>
			<portlet:param name="action" value="configure_access_control"/>
			<portlet:param name="binderId" value="${ssBinder.id}"/>
		</portlet:actionURL>">

	<input type="hidden" name="roleId" value="${function.id}">
	<input type="submit" name="modifyBtn"
	 value="<ssf:nlt tag="button.modify" text="Modify"/>">
</form>
<br/>
</ssf:expandableArea>

</c:forEach>
</c:if>
<br/>

<form class="ss_style" name="<portlet:namespace/>rolesForm" method="post" action="<portlet:actionURL>
			<portlet:param name="action" value="configure_access_control"/>
			<portlet:param name="binderId" value="${ssBinder.id}"/>
		</portlet:actionURL>">

	<input type="submit" name="closeBtn" value="<ssf:nlt tag="button.close" text="Close"/>">
</form>
</div>
