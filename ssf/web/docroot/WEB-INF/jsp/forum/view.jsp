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
<jsp:useBean id="ssFolderList" type="java.util.List" scope="request" />
<c:set var="folderIdList" value=""/>
<jsp:useBean id="folderIdList" type="java.lang.String" />

<% // Toolbar %>
<c:set var="toolbar" value="${ssForumToolbar}" scope="request" />
<%@ include file="/WEB-INF/jsp/definition_elements/toolbar_view.jsp" %>

<table border="0" cellpadding="4" cellspacing="0" width="100%">
<tr>
	<td>
		<table border="0" cellpadding="0" cellspacing="0">
		<tr>
			<td valign="top">
				<c:if test="${empty ssFolderList}">
				  <ssf:nlt tag="forum.notConfigured" 
				   text="The portlet preferences are not set.  Choose the edit button to configure the portlet."/>
				 </c:if>
				<c:if test="${!empty ssFolderList}">
					<table cellspacing="0" cellpadding="0">
					<c:forEach var="folder" items="<%= ssFolderList %>">
					<jsp:useBean id="folder" type="com.sitescape.ef.domain.Folder" />
					  <tr><td>
						<a href="<portlet:renderURL windowState="maximized">
								<portlet:param name="action" value="view_forum"/>
								<portlet:param name="forumId" value="${folder.id}"/>
							</portlet:renderURL>"><c:out value="${folder.title}"/></a>
					  </td>
					  <td>&nbsp;&nbsp;&nbsp;</td>
					  <td><span id="count_<c:out value="${folder.id}"/>"></span></td>
					  </tr>
					  <%
					  	if (!folderIdList.equals("")) folderIdList += " ";
					  	folderIdList += folder.getId().toString();
					  %>
					</c:forEach>
					</table>
				 </c:if>
				<br>
			</td>
		</tr>
		</table>
	</td>
</tr>
</table>

<script language="JavaScript" src="<html:rootPath/>js/common/taconite-client.js"></script>
<script language="JavaScript" src="<html:rootPath/>js/common/taconite-parser.js"></script>
<script language="javascript">
var count = 0
function getUnseenCounts() {
	<c:forEach var="folder" items="<%= ssFolderList %>">
		document.getElementById("count_<c:out value="${folder.id}"/>").style.color = "silver";
	</c:forEach>
	var url = "<ssf:servletrooturl/>listUnseen?operation=unseen_counts"
	var ajaxRequest = new AjaxRequest(url); //Create AjaxRequest object
	ajaxRequest.addFormElements("unseenCountForm")
	ajaxRequest.setEchoDebugInfo();
	ajaxRequest.sendRequest();  //Send the request
}
</script>
<form id="unseenCountForm" onSubmit="getUnseenCounts();return false;">
<input type="hidden" name="forumList" value="<%= folderIdList %>">
<input type="submit" name="showCounts" value="Show unseen counts">
</form>
<div id="unseenCounts">
</div>

