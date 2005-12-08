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
<h1 class="titleblue">Configure Email Notification Schedule</h1>
<c:choose>
<c:when test="${!empty ssWsDomTree}">
<jsp:useBean id="ssWsDomTree" type="org.dom4j.Document" scope="request" />
	<table border="0" cellpadding="0" cellspacing="0" width="95%">
	<tr align="left"><td>Choose a folder</td></tr>
	<tr>
		<td>
			<div>
				<ssf:tree treeName="ssWsDomTree" treeDocument="<%= ssWsDomTree %>" rootOpen="true" />
			</div>
		</td>
	</tr>
	</table>
	<br/>
</c:when>
<c:otherwise>

<form name="<portlet:namespace/>fm" method="post" action="<portlet:actionURL>
			<portlet:param name="action" value="configure_notify"/>
			<portlet:param name="forumId" value="${ssFolder.id}"/>
		</portlet:actionURL>">
<script language="javascript" type="text/javascript">
function <portlet:namespace/>setEnable() {
	if (document.<portlet:namespace/>fm.disabled.checked) {
		document.<portlet:namespace/>fm.enabled.value = "false";
	} else {
		document.<portlet:namespace/>fm.enabled.value = "true";
	}
}
</script>
<input type="hidden" id="enabled" name="enabled" value="${ssNotification.enabled}"/>

<div align="left">
Folder: ${ssFolder.title}
<br>
<input type="checkbox" class="content" id="disabled" name="disabled" onClick="<portlet:namespace/>setEnable();" <c:if test="${!ssNotification.enabled}">checked</c:if>>
<span class="content">Disable e-mail notification</span></input><br/>

</div>

<div align="left">
<hr noshade=noshade size=1/>
<span class=contentbold>Set the e-mail notification schedule</span>
Notification e-mail will be sent at the specified times on each specified day.
<br>
<c:set var="schedule" value="${ssNotification.schedule}"/>
<%@ include file="/WEB-INF/jsp/administration/schedule.jsp" %>
<hr noshade=noshade size=1/>
<span class=contentbold>Set the default distribution list</span>
<br><br>
<span class="content"><label for="sendToGroups">Send to a group (or groups)</label></span>
<br /><select class="content" name="sendToGroups" id="sendToGroups" multiple size="6">
<option class="content" value="" <c:if test="${empty ssSelectedGroups}">selected</c:if>>--none--</option>
<c:forEach var="group" items="${ssGroups}">
<c:set var="id" value="${group.id}"/>
<option class="content" value="${group.id}" <c:if test="${ssSelectedGroups[id] == true}">selected</c:if>>${group.title} (${group.name})</option>
</c:forEach>
</select>
<br><br>
<c:choose>
<c:when test="${empty ssUsers}" >
<input type="submit" name="listUsers" value="Select from user list..."/>
<input type="hidden" name="showUsers" id="showUsers" value="0"/>
</c:when>
<c:otherwise>
<input type="hidden" name="showUsers" id="showUsers" value="1">
<span class="contentbold"><label for="sendToUsers">Send to selected users</label></span><br />
<select class="content" name="sendToUsers" id="sendToUsers" multiple size="11">
<option class="content" value="" <c:if test="${empty ssSelectedUsers}">selected</c:if>>--none--</option>
<c:forEach var="user" items="${ssUsers}">
<c:set var="id" value="${user.id}"/>
<option class="content" value="${user.id}" <c:if test="${ssSelectedUsers[id] == true}">selected</c:if>>${user.title} (${user.name})</option>
</c:forEach>
</select>
</c:otherwise>
</c:choose>
<br><br>
<span class=contentbold>

Send to a list of e-mail addresses. (Enter one e-mail address per line)
</span><br />

<textarea name="emailAddress" rows=4 cols=50 >
<c:forEach var="addr" items="${ssNotification.emailAddress}"><c:out value="${addr}"/>
</c:forEach>
</textarea>
<br><br>
<hr shade=noshade size=1>
	<br>
	<input type="submit" name="okBtn" value="Ok">
	<input type="submit" name="cancelBtn" value="Cancel">
</div>
</form>
</c:otherwise>
</c:choose>
