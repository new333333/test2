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
<c:set var="ssNamespace" value="${renderResponse.namespace}"/>
<c:if test="${!empty ssComponentId}">
<c:set var="ssNamespace" value="${renderResponse.namespace}_${ssComponentId}"/>
</c:if>
<%@ include file="/WEB-INF/jsp/common/presence_support.jsp" %>
<c:set var="userIdList" value=""/>
<jsp:useBean id="userIdList" type="java.lang.String" />

<div class="ss_portlet_style ss_portlet">

<div>

<div class="ss_portlet_style ss_portlet">
<table style="width:100%;">
<tr>
<td>
<a class="ss_linkButton ss_bold ss_smallprint" href=""
  onClick="if (${ssNamespace}_getPresence) {${ssNamespace}_getPresence('')};return false;"
><ssf:nlt tag="general.Refresh"/></a>
</td>
<td align="right">
<div id="${ssNamespace}_refreshDate">
<span class="ss_smallprint ss_light"><ssf:nlt 
tag="presence.last.refresh"/> <fmt:formatDate timeZone="${ssUser.timeZone.ID}" value="<%= new java.util.Date() %>" 
type="time" /></span>
</div>
</td>
</tr>
</table>
</div>

<c:if test="${!empty ssDashboard}">
	<c:set var="ssUsers" value="${ssDashboard.beans[ssComponentId].ssUsers}"/>
	<c:set var="ssGroups" value="${ssDashboard.beans[ssComponentId].ssGroups}"/>
</c:if>
<table border="0" cellpadding="4" cellspacing="0" width="100%">
<tr>
	<td>
		<table border="0" cellpadding="0" cellspacing="0">
		<tr>
			<td valign="top">
				<c:if test="${!empty ssUsers}">
					<table cellspacing="0" cellpadding="0">
					<c:forEach var="u1" items="${ssUsers}">
					<jsp:useBean id="u1" type="com.sitescape.ef.domain.User" />
					  <tr>
					  <td><span id="${ssNamespace}_user_<c:out value="${u1.id}"/>"
					  ><ssf:presenceInfo user="<%=u1%>" componentId="${ssNamespace}"/> </span></td>
					  <td>&nbsp;&nbsp;&nbsp;</td>
					  <td>
					  <ssf:ifadapter>
					  <a href="<ssf:url adapter="true" portletName="ss_forum" 
					    action="view_permalink"
					    binderId="${u1.parentBinder.id}"
					    entryId="${u1.id}">
					    <ssf:param name="entityType" value="workspace" />
						</ssf:url>"><c:out value="${u1.title}"/></a>
					  </ssf:ifadapter>
					  <ssf:ifnotadapter>
					  <a href="<portlet:renderURL windowState="maximized">
						<portlet:param name="action" value="view_ws_listing"/>
						<portlet:param name="binderId" value="${u1.parentBinder.id}"/>
						<portlet:param name="entryId" value="${u1.id}"/>
						</portlet:renderURL>"><c:out value="${u1.title}"/></a>
					  </ssf:ifnotadapter>
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
					  <td><span id="${ssNamespace}_user_<c:out value="${u2.id}"/>"
					  ><ssf:presenceInfo user="<%=(com.sitescape.ef.domain.User)u2%>"
					    componentId="${ssNamespace}"/> </span></td>
					  <td>&nbsp;&nbsp;&nbsp;</td>
					  <td>&nbsp;&nbsp;&nbsp;</td>
					  <td>
					  <ssf:ifadapter>
					  <a href="<ssf:url adapter="true" portletName="ss_forum" 
					    action="view_permalink"
						binderId="${u2.parentBinder.id}"
						entryId="${u2.id}">
						<ssf:param name="entityType" value="workspace" />
						</ssf:url>"><c:out value="${u2.title}"/></a>
					  </ssf:ifadapter>
					  <ssf:ifnotadapter>
					  <a href="<portlet:renderURL windowState="maximized">
						<portlet:param name="action" value="view_ws_listing"/>
						<portlet:param name="binderId" value="${u2.parentBinder.id}"/>
						<portlet:param name="entryId" value="${u2.id}"/>
						</portlet:renderURL>"><c:out value="${u2.title}"/></a>
					  </ssf:ifnotadapter>
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
function ${ssNamespace}_getPresence (timeout) {
	ss_setupStatusMessageDiv();
	clearTimeout(${ssNamespace}_presenceTimer);
	var url = "<ssf:url 
    	adapter="true" 
    	portletName="ss_forum" 
    	action="view_presence" 
    	actionUrl="false" >
    	</ssf:url>"
	var ajaxRequest = new ss_AjaxRequest(url); //Create AjaxRequest object
	ajaxRequest.addFormElements("${ssNamespace}_presenceForm");
	ajaxRequest.setData("timeout", timeout);
	//ajaxRequest.setEchoDebugInfo();
	ajaxRequest.setPostRequest(ss_postRequest);
	ajaxRequest.setUsePOST();
	ajaxRequest.sendRequest();  //Send the request
	${ssNamespace}_presenceTimer = setTimeout("${ssNamespace}_presenceTimout()", 300000);
}
var ${ssNamespace}_presenceTimer = null;
function ${ssNamespace}_presenceTimout() {
	${ssNamespace}_getPresence("timeout");
}	
${ssNamespace}_presenceTimer = setTimeout("${ssNamespace}_presenceTimout()", 300000);
</script>

<form class="ss_portlet_style ss_form" id="${ssNamespace}_presenceForm" 
  style="display:none;">
<input type="hidden" name="userList" value="<%= userIdList %>">
<input type="hidden" name="ssNamespace" value="${ssNamespace}">
<input type="hidden" name="ssComponentId" value="${ssComponentId}">
</form>

</div>

