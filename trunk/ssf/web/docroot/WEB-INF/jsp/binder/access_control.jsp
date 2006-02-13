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

<c:if test="${ssBinder.functionMembershipInherited}">
This folder is inheriting its access control settings from its parent folder.<br>
</c:if>

<c:if test="${!ssBinder.functionMembershipInherited}">
<fieldset class="ss_fieldset">
  <legend class="ss_legend"><ssf:nlt tag="accessControl.currentMembershipSettings" text="Current membership settings"/></legend>
<c:forEach var="function" items="${ssFunctionMap}">
  <c:if test="${!empty function.value.ssUsers || !empty function.value.ssGroups}">
	<span calss="ss_bold"><c:out value="${function.key.name}"/></span>
	<br>
	<span><ssf:nlt tag="accessControl.users" text="Users"/></span>
	<br>
	<ul>
	<c:forEach var="user" items="${function.value.ssUsers}">
		<li><c:out value="${user.title}"/></li>
	</c:forEach>
	</ul>
	<br>
	<span><ssf:nlt tag="accessControl.users" text="Users"/></span>
	<br>
	<ul>
	<c:forEach var="user" items="${function.value.ssGroups}">
		<li><c:out value="${user.title}"/></li>
	</c:forEach>
	</ul>
  </c:if>
</c:forEach>
</fieldset>

<c:forEach var="function" items="${ssFunctionMap}">
<ssf:expandableArea title="${function.key.name}">
<form class="ss_style" name="<portlet:namespace/>rolesForm" method="post" action="<portlet:actionURL>
			<portlet:param name="action" value="configure_access_control"/>
			<portlet:param name="binderId" value="${ssBinder.id}"/>
		</portlet:actionURL>">
	<ul>
	<c:forEach var="user" items="${function.value.ssUsers}">
		<li><c:out value="${user.title}"/></li>
	</c:forEach>
	</ul>
	<ul>
	<c:forEach var="user" items="${function.value.ssGroups}">
		<li><c:out value="${user.title}"/></li>
	</c:forEach>
	</ul>
	<input type="hidden" name="roleId" value="${function.key.id}">
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
