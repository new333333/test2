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

<div class="ss_portlet">

<div class="forms">
<form method="post" action="" style="display:inline;">

<div class="buttonBarRight">
<input type="submit" name="okBtn" class="submit" value="  OK  "> <input type="submit" name="cancelBtn" class="submit" value="Cancel">
</div>

<fieldset class="fieldset" title="Fieldset one">
 <legend class="legend">Default forum view</legend>

<c:forEach var="item" items="${public_folder_definitions}">
	<c:choose>
	<c:when test="${default_folder_definition_id == item.value.id}">
	<input type="radio" name="defaultFolderDefinition" value="<c:out value="${item.value.id}"/>" checked><c:out value="${item.value.name}"/><br/>
	</c:when>
	<c:otherwise>
	<input type="radio" name="defaultFolderDefinition" value="<c:out value="${item.value.id}"/>"><c:out value="${item.value.name}"/><br/>
	</c:otherwise>
	</c:choose>
</c:forEach>

</fieldset>

<fieldset class="fieldset" title="Fieldset one">
 <legend class="legend">Default entry types</legend>

<c:forEach var="item" items="${public_entry_definitions}">
	<c:choose>
	<c:when test="${empty entry_definition_map[item.key]}">
	<input type="checkbox" name="defaultEntryDefinition" value="<c:out value="${item.value.id}"/>"><c:out value="${item.value.name}"/><br/>
	</c:when>
	<c:otherwise>
	<input type="checkbox" name="defaultEntryDefinition" value="<c:out value="${item.value.id}"/>" checked><c:out value="${item.value.name}"/><br/>
	</c:otherwise>
	</c:choose>
</c:forEach>

</fieldset>

<div class="formBreak"/>

<div class="buttonBarLeft">
<input type="submit" name="okBtn" class="submit" value="  OK  "> <input type="submit" name="cancelBtn" class="submit" value="Cancel">
</div>

</form>
</div>

</div>

</liferay:box>

