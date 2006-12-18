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
<span class="ss_titlebold"><ssf:nlt tag="notify.title"/></span><br/>
<br/>
<c:choose>
<c:when test="${!empty ssWsDomTree}">
<jsp:useBean id="ssWsDomTree" type="org.dom4j.Document" scope="request" />
	<table class="ss_style" border="0" cellpadding="0" cellspacing="0" width="95%">
	<tr align="left"><td><ssf:nlt tag="notify.choose"/></td></tr>
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

<form class="ss_style ss_form" name="${renderResponse.namespace}fm" method="post" 
  onSubmit="return ss_onSubmit(this);"

    action="<portlet:actionURL><portlet:param 
    	name="action" value="configure_notify"/><portlet:param 
    	name="binderId" value="${ssBinder.id}"/></portlet:actionURL>">

<span class="ss_bold"><ssf:nlt tag="notify.forum.label"/>${ssBinder.title}</span>

<div class="ss_buttonBarRight">
<br/>
<input type="submit" class="ss_submit" name="okBtn" value="<ssf:nlt tag="button.apply"/>">
	<input type="submit" class="ss_submit" name="closeBtn" value="<ssf:nlt tag="button.close" text="Close"/>">
</div>
<table class="ss_style" border ="0" cellspacing="0" cellpadding="3">
<tr><td> 
<input type="checkbox" id="enabled" name="enabled" <c:if test="${ssScheduleInfo.enabled}">checked</c:if>/>
<span class="ss_labelRight ss_bold"><ssf:nlt tag="notify.schedule.enable"/></span><br/>
</td></tr></table>

<br/>
<ssf:expandableArea title="<%= NLT.get("notify.schedule") %>">
<c:set var="schedule" value="${ssScheduleInfo.schedule}"/>
<%@ include file="/WEB-INF/jsp/administration/schedule.jsp" %>
<div class="ss_divider"></div>
</ssf:expandableArea>


<fieldset class="ss_fieldset">
  <legend class="ss_legend"><ssf:nlt tag="notify.distribution.list" /></legend>

<table class="ss_style"  border ="0" cellspacing="0" cellpadding="3">
<tr>
<td class="ss_bold" valign="top"><ssf:nlt tag="notify.teams"/></td>
<td valign="top">
<input type="checkbox" id="teamOn" name="teamOn" <c:if test="${ssBinder.notificationDef.teamOn}">checked</c:if>/>
</td></tr>
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
	  	<textarea name="emailAddress" style="height: 100px; width: 500px; overflow:auto;" wrap="hard"><%=buf.toString()%></textarea>
</td></tr>
</table>
</fieldset>
<br/>
<div class="ss_buttonBarLeft">
<input type="submit" class="ss_submit" name="okBtn" value="<ssf:nlt tag="button.apply"/>">
	<input type="submit" class="ss_submit" name="closeBtn" value="<ssf:nlt tag="button.close" text="Close"/>">
</div>
</form>

</c:otherwise>
</c:choose>
</div>
