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
<%@ include file="/WEB-INF/jsp/common/common.jsp" %>
<c:if test="${empty ss_portletInitialization}">
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
</c:if>

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
<div class="ss_style" style="padding:4px;">

<c:if test="${empty ssFolderList}">
<div align="right">
  <a class="ss_linkButton" 
    href="<portlet:renderURL 
      portletMode="edit" 
      windowState="maximized" />"
    ><ssf:nlt tag="portlet.configure"/></a>
</div>
<div>
  <ssf:nlt tag="portlet.notConfigured"/>
</div>
</c:if>
 
<c:if test="${!empty ssFolderList}">
<div style="padding:5px 0px;">
  <a class="ss_linkButton ss_bold ss_smallprint" 
    href="javascript: ;" onClick="<portlet:namespace/>_getUnseenCounts();return false;"
    ><ssf:nlt tag="portlet.showUnread"/></a>
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
		</portlet:renderURL>"><span>${folder.title}</span></a>
	<c:if test="${folder.parentBinder.entityIdentifier.entityType == 'folder'}">
	  <a style="padding-left:20px;" href="<portlet:renderURL windowState="maximized"><portlet:param 
			name="action" value="view_folder_listing"/><portlet:param 
			name="binderId" value="${folder.parentBinder.id}"/></portlet:renderURL>">
			<span class="ss_smallprint ss_light">(${folder.parentBinder.title})</span></a>
	</c:if>
	<c:if test="${folder.parentBinder.entityIdentifier.entityType != 'folder'}">
	  <a style="padding-left:20px;" href="<portlet:renderURL windowState="maximized">
			<portlet:param name="action" value="view_ws_listing"/>
			<portlet:param name="binderId" value="${folder.parentBinder.id}"/></portlet:renderURL>">
			<span  class="ss_smallprint ss_light">(${folder.parentBinder.title})</span></a>
	</c:if>
  </td>
  </tr>
  <%
  	if (!folderIdList.equals("")) folderIdList += " ";
  	folderIdList += folder.getId().toString();
  %>
</c:forEach>
</table>
<div align="right">
  <a class="ss_linkButton" 
    href="<portlet:renderURL 
      portletMode="edit" 
      windowState="maximized" />"
    ><ssf:nlt tag="portlet.configure"/></a>
</div>
</c:if>

</div>
</div>
<form class="ss_portlet_style ss_form" id="<portlet:namespace/>_unseenCountForm" style="display:none;">
<input type="hidden" name="forumList" value="<%= folderIdList %>">
<input type="hidden" name="ssNamespace" value="<portlet:namespace/>">
</form>
