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
<c:set var="folderIdList" value=""/>
<jsp:useBean id="folderIdList" type="java.lang.String" />

<div id="ss_showfolder" class="ss_portlet">

<% // Toolbar %>
<c:if test="${!empty ssForumToolbar}">
<c:set var="toolbar" value="${ssForumToolbar}" scope="request" />
<%@ include file="/WEB-INF/jsp/definition_elements/toolbar_view.jsp" %>
</c:if>
<script language="javascript">
function showNotLoggedInMsg() {
	alert("<ssf:nlt tag="forum.unseenCounts.notLoggedIn" text="Your session has timed out. Please log in again."/>");
}
</script>
<div id="status_message"></div>

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
					<c:forEach var="folder" items="${ssFolderList}">
					<jsp:useBean id="folder" type="com.sitescape.ef.domain.Folder" />
					  <tr>
					  <td><span id="count_<c:out value="${folder.id}"/>"><font color="silver">-</font></span></td>
					  <td>&nbsp;&nbsp;&nbsp;</td>
					  <td>
						<a href="<portlet:renderURL windowState="maximized">
								<portlet:param name="action" value="view_listing"/>
								<portlet:param name="binderId" value="${folder.id}"/>
							</portlet:renderURL>"><c:out value="${folder.title}"/></a>
					  </td>
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

</div>
<script language="JavaScript" src="<html:rootPath/>js/common/taconite-client.js"></script>
<script language="JavaScript" src="<html:rootPath/>js/common/taconite-parser.js"></script>
<script language="javascript">
var count = 0
function ss_getUnseenCounts() {
	<c:forEach var="folder" items="${ssFolderList}">
		document.getElementById("count_<c:out value="${folder.id}"/>").style.color = "silver";
	</c:forEach>
	var url = "<ssf:url 
    	adapter="true" 
    	portletName="ss_forum" 
    	action="__view_unseen" 
    	actionUrl="true" >
		<ssf:param name="operation" value="unseen_counts" />
    	</ssf:url>"
	var ajaxRequest = new AjaxRequest(url); //Create AjaxRequest object
	ajaxRequest.addFormElements("unseenCountForm")
	//ajaxRequest.setEchoDebugInfo();
	//ajaxRequest.setPreRequest(ss_preRequest);
	//ajaxRequest.setPostRequest(ss_postRequest);
	ajaxRequest.setUsePOST();
	ajaxRequest.sendRequest();  //Send the request
}
function ss_preRequest(obj) {
	alert('preRequest: ' + obj.getQueryString());
}
function ss_postRequest(obj) {
	alert('postRequest: ' + obj.getXMLHttpRequestObject().responseText);
}
</script>
<form id="unseenCountForm" >
<input type="hidden" name="forumList" value="<%= folderIdList %>">
</form>
<div id="unseenCounts">
</div>

