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
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
<%@ page import="com.sitescape.team.util.NLT" %>
<%
String wsTreeName = "email_" + renderResponse.getNamespace();
%>
<script type="text/javascript">
function <%= wsTreeName %>_showId(id, obj, action) {
	//Build a url to go to
	var url = "<portlet:renderURL windowState="maximized"><portlet:param 
			name="action" value="ssActionPlaceHolder"/><portlet:param 
			name="binderId" value="ssBinderIdPlaceHolder"/></portlet:renderURL>"
	url = ss_replaceSubStr(url, "ssBinderIdPlaceHolder", id);
	url = ss_replaceSubStr(url, "ssActionPlaceHolder", action);
	self.location.href = url;
	return false;
}
</script>
<div class="ss_style ss_portlet">
<c:choose>
<c:when test="${!empty ssWsDomTree}">

<jsp:useBean id="ssWsDomTree" type="org.dom4j.Document" scope="request" />
<form class="ss_style ss_form" name="<ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>fm" 
    id="<ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>fm" method="post" 
    action="<portlet:actionURL windowState="maximized"><portlet:param 
    	name="action" value="config_email"/></portlet:actionURL>">
<div class="ss_buttonBarRight">
<input type="submit" class="ss_submit" name="closeBtn" value="<ssf:nlt tag="button.close" text="Close"/>">
</div>
	<table class="ss_style" border="0" cellpadding="0" cellspacing="0" width="95%">
	<tr align="left"><td><ssf:nlt tag="tree.choose_folder"/></td></tr>
	<tr>
		<td align="left">
			<div>
			<ssf:tree treeName="<%= wsTreeName %>"  treeDocument="${ssWsDomTree}" 
			  topId="${ssWsDomTreeBinderId}" 
			  rootOpen="true" showImages="false" />
			</div>
		</td>
	</tr>
	</table>
	<br/>
<div class="ss_buttonBarLeft">
<input type="submit" class="ss_submit" name="closeBtn" value="<ssf:nlt tag="button.close" text="Close"/>">
</div>
</form>
</c:when>
<c:otherwise>
<span class="ss_bold"><ssf:nlt tag="notify.forum.label"/>&nbsp;${ssBinder.title}</span><br/>
<c:if test="${!empty ssException}">
<span class="ss_largerprint"><ssf:nlt tag="administration.errors"/> (<c:out value="${ssException}"/>)</span></br>
</c:if>

<c:set var="ss_breadcrumbsShowIdRoutine" value="<%= wsTreeName + "_showId"%>" scope="request" />
<c:set var="ss_breadcrumbsTreeName" value="<%= wsTreeName %>" scope="request" />

<%@ include file="/WEB-INF/jsp/definition_elements/navigation_links.jsp" %>
<form class="ss_style ss_form" name="${renderResponse.namespace}fm" method="post" 
  onSubmit="return ss_onSubmit(this);"

    action="<portlet:actionURL windowState="maximized"><portlet:param 
    	name="action" value="config_email"/><portlet:param 
    	name="binderId" value="${ssBinder.id}"/></portlet:actionURL>">

<br/>
<table class="ss_style" border ="0" cellspacing="0" cellpadding="3" width="100%">
<tr><td> 
<span class="ss_labelLeft"><ssf:nlt tag="incoming.select"/></span> <ssf:inlineHelp jsp="workspaces_folders/misc_tools/email_alias" />

<input type="text" name="alias" value="${ssBinder.posting.emailAddress}" size="30"> 
<c:if test="${!ssScheduleInfo2.enabled}"><br/><ssf:nlt tag="incoming.disabled"/></c:if>

</td>
<td class="ss_buttonBarRight">
<input type="submit" class="ss_submit" name="okBtn" value="<ssf:nlt tag="button.apply"/>">
<input type="submit" class="ss_submit" name="closeBtn" value="<ssf:nlt tag="button.close" text="Close"/>">
</td></tr></table>

<c:if test="${!empty ssScheduleInfo}">

<br/>

<table class="ss_style"  border="0" cellspacing="0" cellpadding="3" width="100%">
<tr>
<td align="center"><span class="ss_bold"><ssf:nlt tag="notify.header"/></span></td>
</tr>
</table>

<table class="ss_style"  border="1" cellspacing="0" cellpadding="3" width="100%">
<tr>
<th width="30%"><ssf:nlt tag="notify.schedule"/></th>
<th width="70%"><ssf:nlt tag="notify.distribution.list"/> <ssf:inlineHelp tag="ihelp.email.notification_list"/></th>
</tr>
<tr>
<td valign="top">
<input type="checkbox" class="ss_style" id="enabled" name="enabled" <c:if test="${ssScheduleInfo.enabled}">checked</c:if> />
<span class="ss_labelLeft"><ssf:nlt tag="notify.schedule.enable"/> <ssf:inlineHelp tag="ihelp.email.enableCheckBox"/></span>
<br/>

<c:set var="schedule" value="${ssScheduleInfo.schedule}"/>
<%@ include file="/WEB-INF/jsp/administration/schedule.jsp" %>
</td>
<td>

<table class="ss_style"  border ="0" cellspacing="0" cellpadding="3">
 <tr><td>
   <span class="ss_labelAbove ss_bold"><ssf:nlt tag="sendMail.addresses"/></span>
   <input type="text" class="ss_style" name="addresses" id="addresses" size="86" value="${ssBinder.notificationDef.emailAddress}">
 </td></tr>
<tr><td>
<input type="checkbox" class="ss_style" id="teamMembers" name="teamMembers" <c:if test="${ssBinder.notificationDef.teamOn}">checked</c:if>/>
<span class="ss_labelRight"><ssf:nlt tag="sendMail.team"/></span>
</td></tr>
</table>
<table class="ss_style"  border ="0" cellspacing="0" cellpadding="3">
<tr>
<td class="ss_bold" valign="top"><ssf:nlt tag="general.users" text="Users"/></td>
<td valign="top">
  <ssf:find formName="${renderResponse.namespace}fm" formElement="users" 
    type="user" userList="${ssUsers}"/>
</td>
</tr>
<tr>
<td class="ss_bold" valign="top"><ssf:nlt tag="general.groups" text="Groups"/></td>
<td valign="top">
  <ssf:find formName="${renderResponse.namespace}fm" formElement="groups" 
    type="group" userList="${ssGroups}"/>
</td>
</tr>
</table>
</td>
</tr>
</table>

<br/>

<div class="ss_buttonBarLeft">
<input type="submit" class="ss_submit" name="okBtn" value="<ssf:nlt tag="button.apply"/>">
	<input type="submit" class="ss_submit" name="closeBtn" value="<ssf:nlt tag="button.close" text="Close"/>">
</div>
</c:if>
</form>

</c:otherwise>
</c:choose>
</div>