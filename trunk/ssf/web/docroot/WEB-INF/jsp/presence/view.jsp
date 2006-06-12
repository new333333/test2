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
<%@ include file="/WEB-INF/jsp/common/presence_support.jsp" %>
<c:set var="userIdList" value=""/>
<jsp:useBean id="userIdList" type="java.lang.String" />

<div id="ss_showpresence" class="ss_portlet_style ss_portlet">

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
<div id="ss_presence_status_message" class="ss_portlet_style" style="visibility:hidden; display:none;"></div>

<table border="0" cellpadding="4" cellspacing="0" width="100%">
<tr>
	<td>
		<table border="0" cellpadding="0" cellspacing="0">
		<tr>
			<td valign="top">
				<c:if test="${empty ssUsers}">
				  <ssf:nlt tag="portlet.notConfigured" 
				   text="The portlet preferences are not set.  Choose the edit button to configure the portlet."/>
				 </c:if>
				<c:if test="${!empty ssUsers}">
					<table cellspacing="0" cellpadding="0">
					<c:forEach var="u1" items="${ssUsers}">
					<jsp:useBean id="u1" type="com.sitescape.ef.domain.User" />
					  <tr>
					  <td><span id="count_<c:out value="${u1.id}"/>"><ssf:presenceInfo user="<%=u1%>"/> </span></td>
					  <td>&nbsp;&nbsp;&nbsp;</td>
					  <td><c:out value="${u1.title}"/>
					  </td>							
					  </tr>
					  <%
					  	if (!userIdList.equals("")) userIdList += " ";
					  	userIdList += u1.getId().toString();
					  %>
					</c:forEach>
					</table>
				 </c:if>
				<c:if test="${!empty ssGroups}">
					<table cellspacing="0" cellpadding="0">
					<c:forEach var="group" items="${ssGroups}">
					<c:forEach var="u2" items="${group.members}">
					<jsp:useBean id="u2" type="com.sitescape.ef.domain.Principal" />
					<c:if test="<%= u2 instanceof com.sitescape.ef.domain.User %>">
					  <tr>
					  <td><span id="count_<c:out value="${u2.id}"/>"><ssf:presenceInfo user="<%=(com.sitescape.ef.domain.User)u2%>"/> </span></td>
					  <td>&nbsp;&nbsp;&nbsp;</td>
					  <td><c:out value="${u2.title}"/>
					  </td>							
					  </tr>
					</c:if>
					  <%
					  	if (!userIdList.equals("")) userIdList += " ";
					  	userIdList += u2.getId().toString();
					  %>
					</c:forEach>
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
function ss_getPresence() {
	var url = "<ssf:url 
    	adapter="true" 
    	portletName="ss_presence" 
    	action="view_presence" 
    	actionUrl="false" >
    	</ssf:url>"
	var ajaxRequest = new AjaxRequest(url); //Create AjaxRequest object
	ajaxRequest.addFormElements("presenceForm")
//	ajaxRequest.setEchoDebugInfo();
//	ajaxRequest.setPreRequest(ss_preRequest);
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
<form class="ss_portlet_style ss_form" id="presenceForm" style="display:none;">
<input type="hidden" name="userList" value="<%= userIdList %>">
</form>


