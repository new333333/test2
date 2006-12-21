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
<%@ page import="com.sitescape.ef.util.NLT" %>

<div class="ss_style ss_portlet">
<form class="ss_style ss_form" name="${renderResponse.namespace}fm" method="post" 
  onSubmit="return ss_onSubmit(this);"

    action="<portlet:actionURL>
			<portlet:param name="action" value="config_email"/>
			<portlet:param name="binderId" value="${ssBinder.id}"/>
		</portlet:actionURL>">

<span class="ss_bold"><ssf:nlt tag="notify.forum.label"/>&nbsp;${ssBinder.title}</span>

<br/><br/>
<table class="ss_style" border ="0" cellspacing="0" cellpadding="3" width="100%">
<tr><td> 
<span class="ss_labelLeft"><ssf:nlt tag="incoming.select"/></span>

<select name="alias" id="alias">
<option value=""><ssf:nlt tag="selected.none"/></option>
<c:forEach var="opt" items="${ssPostings}">
<c:if test="${empty opt.binder}">
		<option value="${opt.id}">${opt.emailAddress}</option>
</c:if>
<c:if test="${ssBinder.posting.id == opt.id}">
<option value="${opt.id}" selected="selected">${opt.emailAddress}</option>
</c:if>
</c:forEach>
</select>
</td>
<td class="ss_buttonBarRight">
<input type="submit" class="ss_submit" name="okBtn" value="<ssf:nlt tag="button.apply"/>">
<input type="submit" class="ss_submit" name="closeBtn" value="<ssf:nlt tag="button.close" text="Close"/>">
</td></tr></table>

<c:if test="${!empty ssScheduleInfo}">

<br/>

<table class="ss_style"  border="1" cellspacing="0" cellpadding="3" width="100%">
<tr>
<th><ssf:nlt tag="notify.schedule"/></th>
<th><ssf:nlt tag="notify.distribution.list"/></th>
</tr>
<tr>
<td valign="top">
<input type="checkbox" id="enabled" name="enabled" <c:if test="${ssScheduleInfo.enabled}">checked</c:if> />
<span class="ss_labelLeft"><ssf:nlt tag="notify.schedule.enable"/></span>
<br/>

<c:set var="schedule" value="${ssScheduleInfo.schedule}"/>
<%@ include file="/WEB-INF/jsp/administration/schedule.jsp" %>
</td>
<td>
<table class="ss_style"  border ="0" cellspacing="0" cellpadding="3">
<tr>
<td class="ss_bold" valign="top"><ssf:nlt tag="notify.teams"/></td>
<td valign="top">
<input type="checkbox" id="teamOn" name="teamOn" <c:if test="${ssBinder.notificationDef.teamOn}">checked</c:if>/>
</td>
</tr>
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
<tr>
<td colspan="2">
<span class="ss_labelAbove ss_bold"><ssf:nlt tag="notify.addresses.instructions"/></span>
	  	<c:set var="mappings" value="${ssBinder.notificationDef.emailAddress}"/>
<jsp:useBean id="mappings" type="String[]" scope="page" />
	  	<%
			StringBuffer buf = new StringBuffer();
	  		for (int i=0; i<mappings.length; ++i) {
				buf.append(mappings[i] + "\n");
	  		}
	  	%>
	  	<textarea name="emailAddress" style="height: 100px; width: 500px; overflow:auto;" wrap="hard" disabled><%=buf.toString()%></textarea>
</td></tr>
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

</div>
