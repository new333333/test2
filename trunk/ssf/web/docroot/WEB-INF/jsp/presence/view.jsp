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

<script language="JavaScript">
var ss_presenceTimer = null;
function ss_presenceTimeout() {
	ss_getPresence("timeout");
	ss_presenceTimer = setTimeout("ss_presenceTimeout()", 300000);
}	
</script>
<div id="ss_showpresence" class="ss_portlet_style ss_portlet">

<% // Toolbar %>
<c:if test="${empty ssForumToolbar && !empty ssForumToolbar}">
<c:set var="ss_toolbar" value="${ssForumToolbar}" scope="request" />
<%@ include file="/WEB-INF/jsp/definition_elements/toolbar_view.jsp" %>
</c:if>
<script type="text/javascript">
function ss_showNotLoggedInMsg() {
	alert("<ssf:nlt tag="general.notLoggedIn" text="Your session has timed out. Please log in again."/>");
}
</script>
<div class="ss_toolbar">
<table cellspacing="0" cellpadding="2" style="width:100%;">
<tr>
<td><a class="ss_linkButton ss_bold ss_smallprint" href=""
  onClick="if (ss_getPresence) {ss_getPresence('')};return false;"
><ssf:nlt tag="general.Refresh"/></a></td>
<td align="right"><div id="ss_refreshDate">
<span class="ss_smallprint ss_gray"><ssf:nlt 
tag="presence.last.refresh"/> <fmt:formatDate value="<%= new java.util.Date() %>" 
type="time" /></span>
</div></td>
</tr>
</table>
</div>
<table><tr><td>
<table border="0" cellpadding="4" cellspacing="0" width="100%">
<tr>
	<td>
		<table border="0" cellpadding="0" cellspacing="0">
		<tr>
			<td valign="top">
				<c:if test="${empty ssUsers and empty ssGroups}">
				  <ssf:nlt tag="portlet.notConfigured" 
				   text="The portlet preferences are not set.  Choose the edit button to configure the portlet."/>
				 </c:if>
				<c:if test="${!empty ssUsers}">
					<table cellspacing="0" cellpadding="0">
					<c:forEach var="u1" items="${ssUsers}">
					<jsp:useBean id="u1" type="com.sitescape.ef.domain.User" />
					  <tr>
					  <td><span id="count_<c:out value="${u1.id}"/>"><ssf:presenceInfo 
					    user="<%=u1%>"/> </span></td>
					  <td>&nbsp;&nbsp;&nbsp;</td>
					  <td><a href="" 
					  onClick="if (ss_getPresenceEntry) {return ss_getPresenceEntry(<c:out value="${u1.parentBinder.id}"/>,<c:out value="${u1.id}"/>)};return false;">
		    		   <c:out value="${u1.title}"/></a>
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
					  <td><span id="count_<c:out value="${u2.id}"/>"><ssf:presenceInfo 
					    user="<%=(com.sitescape.ef.domain.User)u2%>"/> </span></td>
					  <td>&nbsp;&nbsp;&nbsp;</td>
					  <td><a href="" 
					  onClick="if (ss_getPresenceEntry) {ss_getPresenceEntry(<c:out value="${u2.parentBinder.id}"/>,<c:out value="${u2.id}"/>)};">
		    		   <c:out value="${u2.title}"/></a>
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
</td><td>
<div id="ss_presence_view_entry"></div>

</td></tr></table>
<div id="ss_presence_sizer_div"></div>
</div>
<script type="text/javascript">
var count = 0
function ss_getPresence(timeout) {
	ss_setupStatusMessageDiv()
	clearTimeout(ss_presenceTimer);
	var url = "<ssf:url 
    	adapter="true" 
    	portletName="ss_presence" 
    	action="view_presence" 
    	actionUrl="false" >
    	</ssf:url>"
	var ajaxRequest = new AjaxRequest(url); //Create AjaxRequest object
	ajaxRequest.addFormElements("presenceForm")
	ajaxRequest.setData("timeout", timeout)
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
		if (obj.getData('timeout') != "timeout") {
			//This call wasn't made from a timeout. So, give error message
			if (self.ss_showNotLoggedInMsg) self.ss_showNotLoggedInMsg();
		}
	}
}
</script>
<form class="ss_portlet_style ss_form" id="presenceForm" style="display:none;">
<input type="hidden" name="userList" value="<%= userIdList %>">
</form>

<script type="text/javascript">
ss_presenceTimer = setTimeout("ss_presenceTimeout()", 300000);

var <portlet:namespace/>presenceState = "<c:out value="${renderRequest.windowState}"/>"
function ss_getPresenceEntry(binderId, entryId) {
		var url = "<portlet:renderURL windowState="maximized">
				  	<portlet:param name="action" value="view_profile_entry" />
				  	<portlet:param name="operation" value="buddy" />		
				  	<portlet:param name="binderId" value="ssBinderIdPlaceHolder" />
					<portlet:param name="entryId" value="ssEntryIdPlaceHolder"/>
		    		</portlet:renderURL>"
		url = ss_replaceSubStr(url, "ssBinderIdPlaceHolder",  binderId);
		url = ss_replaceSubStr(url, "ssEntryIdPlaceHolder", entryId);
		self.location.href = url;
		return false;
//This doesn't work, cause the viewEntry code pulls in to much stuff 
 	if (<portlet:namespace/>presenceState == "maximized") {
		var url = "<ssf:url 
   		adapter="true" 
	    	portletName="ss_profile" 
	    	action="view_profile_entry"
	    	operation="buddy"
	    	folderId="ssBinderIdPlaceHolder"
	    	entryId="ssEntryIdPlaceHolder"
	    	actionUrl="false" >
	    	</ssf:url>"
		url = ss_replaceSubStr(url, "ssBinderIdPlaceHolder",  binderId);
		url = ss_replaceSubStr(url, "ssEntryIdPlaceHolder", entryId);
		var ajaxRequest = new AjaxRequest(url);
		//ajaxRequest.setEchoDebugInfo();
		ajaxRequest.setPreRequest(ss_preRequest);
		ajaxRequest.setPostRequest(ss_postRequest);
		ajaxRequest.setUsePOST();
		ajaxRequest.sendRequest();  //Send the request
		return false;
	} else {
		var url = "<portlet:renderURL windowState="maximized">
				  	<portlet:param name="binderId" value="ssBinderIdPlaceHolder" />
					<portlet:param name="entryId" value="ssEntryIdPlaceHolder"/>
		    		</portlet:renderURL>"
		url = ss_replaceSubStr(url, "ssBinderIdPlaceHolder",  binderId);
		url = ss_replaceSubStr(url, "ssEntryIdPlaceHolder", entryId);
		self.location.href = url;
		return false;
 	}	    				    	
}
<c:if test="${!empty binderId and !empty entryId}">
//ss_getPresenceEntry(<c:out value="${binderId}"/>,<c:out value="${entryId}"/>);
</c:if>
</script>