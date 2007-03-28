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
<c:if test="${empty ss_portletInitialization}">
<c:set var="folderIdList" value=""/>
<jsp:useBean id="folderIdList" type="java.lang.String" />

<script type="text/javascript">
var count = 0
function <portlet:namespace/>_getUnseenCounts() {
	ss_setupStatusMessageDiv()
	<c:forEach var="folder" items="${ssFolderList}">
		document.getElementById("<portlet:namespace/>_count_<c:out value="${folder.id}"/>").style.color = "silver";
	</c:forEach>
	var url = "<ssf:url 
    	adapter="true" 
    	portletName="ss_forum" 
    	action="__ajax_request" 
    	actionUrl="true" >
		<ssf:param name="operation" value="unseen_counts" />
    	</ssf:url>"
	var ajaxRequest = new ss_AjaxRequest(url); //Create AjaxRequest object
	ajaxRequest.addFormElements("<portlet:namespace/>_unseenCountForm")
	//ajaxRequest.setEchoDebugInfo();
	//ajaxRequest.setPreRequest(ss_preRequest);
	ajaxRequest.setPostRequest(ss_postRequest);
	ajaxRequest.setUsePOST();
	ajaxRequest.sendRequest();  //Send the request
}


</script>

<div class="ss_portlet_style ss_portlet">

<c:if test="${ss_windowState == 'maximized'}">
<% // Navigation bar %>
<jsp:include page="/WEB-INF/jsp/definition_elements/navbar.jsp" />
</c:if>

<table width="100%">
<tr>
<td align="left">
<c:if test="${!empty ssFolderList}">
  <a class="ss_linkButton ss_bold ss_smallprint" 
    href="javascript: ;" onClick="<portlet:namespace/>_getUnseenCounts();return false;"
    ><ssf:nlt tag="portlet.showUnread"/></a>
</c:if>
</td>
<td align="right">
  <a class="ss_linkButton ss_smallprint" 
    href="<portlet:renderURL 
      portletMode="edit" 
      windowState="maximized" />"
    ><ssf:nlt tag="portlet.configure"/></a>
</td>
</tr>
</table>

<c:if test="${empty ssFolderList}">
<div style="padding:4px;">
  <ssf:nlt tag="portlet.notConfigured"/>
</div>
</c:if>
 
<c:if test="${!empty ssFolderList}">
<table cellspacing="0" cellpadding="0">
<c:forEach var="folder" items="${ssFolderList}">
<jsp:useBean id="folder" type="com.sitescape.team.domain.Folder" />
  <tr>
  <td><span id="<portlet:namespace/>_count_<c:out value="${folder.id}"/>"><font color="silver">-</font></span></td>
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

</div>
<form class="ss_portlet_style ss_form" id="<portlet:namespace/>_unseenCountForm" style="display:none;">
<input type="hidden" name="forumList" value="<%= folderIdList %>">
<input type="hidden" name="ssNamespace" value="<portlet:namespace/>">
</form>

</c:if>

