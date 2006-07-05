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

<div id="ss_showfolder" class="ss_portlet_style ss_portlet">

<% // Toolbar %>
<c:if test="${!empty ssForumToolbar}">
<c:set var="ss_toolbar" value="${ssForumToolbar}" scope="request" />
<%@ include file="/WEB-INF/jsp/definition_elements/toolbar_view.jsp" %>
</c:if>
<script type="text/javascript">
function ss_showNotLoggedInMsg() {
	alert("<ssf:nlt tag="general.notLoggedIn" text="Your session has timed out. Please log in again."/>");
}
</script>

<table border="0" cellpadding="4" cellspacing="0" width="100%">
<tr>
	<td>
		<table border="0" cellpadding="0" cellspacing="0">
		<tr>
			<td valign="top">
				<c:if test="${empty ssFolderList}">
				  <ssf:nlt tag="portlet.notConfigured" 
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
								<portlet:param name="action" value="view_folder_listing"/>
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
<script type="text/javascript">
var count = 0
function ss_getUnseenCounts() {
	ss_setupStatusMessageDiv()
	<c:forEach var="folder" items="${ssFolderList}">
		document.getElementById("count_<c:out value="${folder.id}"/>").style.color = "silver";
	</c:forEach>
	var url = "<ssf:url 
    	adapter="true" 
    	portletName="ss_forum" 
    	action="__ajax_request" 
    	actionUrl="true" >
		<ssf:param name="operation" value="unseen_counts" />
    	</ssf:url>"
	var ajaxRequest = new AjaxRequest(url); //Create AjaxRequest object
	ajaxRequest.addFormElements("unseenCountForm")
	//ajaxRequest.setEchoDebugInfo();
	//ajaxRequest.setPreRequest(ss_preRequest);
	ajaxRequest.setPostRequest(ss_postRequest);
	ajaxRequest.setUsePOST();
	ajaxRequest.sendRequest();  //Send the request
}
function ss_preRequest(obj) {
	alert('preRequest: ' + obj.getQueryString());
}
function ss_postRequest(obj) {
	//alert('postRequest: ' + obj.getXMLHttpRequestObject().responseText);
	//See if there was an error
	if (self.document.getElementById("ss_status_message").innerHTML == "error") {
		if (self.ss_showNotLoggedInMsg) self.ss_showNotLoggedInMsg();
	}
}
</script>
<form class="ss_portlet_style ss_form" id="unseenCountForm" style="display:none;">
<input type="hidden" name="forumList" value="<%= folderIdList %>">
</form>


