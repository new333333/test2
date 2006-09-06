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

<div class="ss_portlet_style ss_portlet">

<% // Toolbar %>
<c:if test="${!empty ssForumToolbar}">
<ssf:toolbar toolbar="${ssForumToolbar}" style="ss_actions_bar" item="true" />
</c:if>

<table border="0" cellpadding="4" cellspacing="0" width="100%">
<tr>
	<td>
		<table border="0" cellpadding="0" cellspacing="0">
		<tr>
			<td valign="top">
				<c:if test="${empty ssFolderList}">
				  <ssf:nlt tag="portlet.notConfigured" 
				   text="The portlet preferences are not set.  Choose the edit button to configure the portlet."/>
				  <a class="ss_linkButton ss_smallprint" 
				    href="<portlet:renderURL 
				      portletMode="edit" 
				      windowState="maximized" />"
				    ><ssf:nlt tag="portlet.setPreferences"/></a>
				 </c:if>
				<c:if test="${!empty ssFolderList}">
					<table cellspacing="0" cellpadding="0">
					<c:forEach var="folder" items="${ssFolderList}">
					<jsp:useBean id="folder" type="com.sitescape.ef.domain.Folder" />
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
	var ajaxRequest = new AjaxRequest(url); //Create AjaxRequest object
	ajaxRequest.addFormElements("<portlet:namespace/>_unseenCountForm")
	//ajaxRequest.setEchoDebugInfo();
	//ajaxRequest.setPreRequest(ss_preRequest);
	ajaxRequest.setPostRequest(ss_postRequest);
	ajaxRequest.setUsePOST();
	ajaxRequest.sendRequest();  //Send the request
}


</script>
<form class="ss_portlet_style ss_form" id="<portlet:namespace/>_unseenCountForm" style="display:none;">
<input type="hidden" name="forumList" value="<%= folderIdList %>">
<input type="hidden" name="ssNamespace" value="<portlet:namespace/>">
</form>


