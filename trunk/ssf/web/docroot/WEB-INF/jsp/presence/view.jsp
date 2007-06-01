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
<c:if test="${!empty ssUsers || ss_windowState == 'maximized'}">
  <c:set var="ssNamespace" value="${renderResponse.namespace}"/>
  <c:if test="${!empty ssComponentId}">
  <c:set var="ssNamespace" value="${renderResponse.namespace}_${ssComponentId}"/>
  </c:if>
  <%@ include file="/WEB-INF/jsp/common/presence_support.jsp" %>
</c:if>

<c:set var="userIdList" value=""/>
<jsp:useBean id="userIdList" type="java.lang.String" />

<div class="ss_portlet_style ss_portlet">
<c:if test="${empty ss_dashboard_context && ss_windowState == 'maximized'}">
<% // Navigation bar %>
<jsp:include page="/WEB-INF/jsp/definition_elements/navbar.jsp" />
</c:if>
<div class="ss_style" style="padding:4px;">

<ssHelpSpot helpId="portlets/buddy_list_portlet" offsetX="0" offsetY="-10" 
			    title="<ssf:nlt tag="helpSpot.buddyListPortlet"/>"></ssHelpSpot>

<c:if test="${!empty ssUsers || ss_windowState == 'maximized'}">
  <div class="ss_portlet_style ss_portlet">
  <table cellpadding="3" style="width:100%;">
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
</c:if>

<c:if test="${!empty ssUsers || ss_windowState == 'maximized' || !empty ssDashboard}">
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
					<jsp:useBean id="u1" type="com.sitescape.team.domain.User" />
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
					  <a href="<portlet:renderURL windowState="maximized"><portlet:param 
					  	name="action" value="view_ws_listing"/><portlet:param 
					  	name="binderId" value="${u1.parentBinder.id}"/><portlet:param 
					  	name="entryId" value="${u1.id}"/></portlet:renderURL>"><c:out value="${u1.title}"/></a>
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
				<br>
			</td>
		</tr>
		</table>
	</td>
  </tr>
  </table>
</c:if>
<c:if test="${empty ss_dashboard_context}">
<div align="right">
  <a class="ss_linkButton" href="<portlet:renderURL 
      portletMode="edit" 
      windowState="maximized" />">
    <span><ssf:nlt tag="presence.addBuddy"/></span>
  </a>
</div>
</c:if>

</div>

<c:if test="${!empty ssUsers || ss_windowState == 'maximized'}">
<script type="text/javascript">
function ${ssNamespace}_getPresence (timeout) {
	ss_setupStatusMessageDiv();
	clearTimeout(${ssNamespace}_presenceTimer);
	var url = "<ssf:url 
    	adapter="true" 
    	portletName="ss_forum" 
    	action="__view_presence" 
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
</c:if>
</div>
