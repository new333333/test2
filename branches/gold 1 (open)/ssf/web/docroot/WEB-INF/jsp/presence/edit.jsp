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

<div class="ss_portlet">
<div class="ss_style ss_form" style="margin:6px;">
<div class="ss_rounded">
<div style="margin:6px; width:100%;">
<h3><ssf:nlt tag="portlet.presence.configure" text="Configure buddy list"/></h3>
<fieldset class="ss_fieldset">
  <legend class="ss_legend"><ssf:nlt tag="portlet.presence.buddies" 
    text="Buddies"/></legend>
 <form class="ss_style" name="${renderResponse.namespace}fm" method="post" 
  onSubmit="return ss_onSubmit(this);"
  action="<portlet:actionURL><portlet:param 
  	name="action" value="configure"/></portlet:actionURL>">

<%
/* Liferay handles this already
<table>
<tr><td><span class="ss_labelLeft"><ssf:nlt tag="portlet.title"/></span>
</td><td><input class="ss_text" name="title" size="20" value="${portletTitle}"/>
</td></tr>
</table>
*/
%>
<table cellspacing="10px" cellpadding="10px" width="100%">

<tr>
<td class="ss_bold" valign="top"><ssf:nlt tag="general.users" 
 text="Users"/></td>
<td valign="top">
  <ssf:find formName="${renderResponse.namespace}fm" formElement="users" 
    type="user" userList="${ssUsers}"/>
</td>
</tr>
<tr>
<td class="ss_bold" valign="top"><ssf:nlt tag="general.groups" 
 text="Groups"/></td>
<td valign="top">
  <ssf:find formName="${renderResponse.namespace}fm" formElement="groups" 
    type="group" userList="${ssGroups}"/>
</td>
</tr>

<tr>
<td colspan="2">
<input type="submit" class="ss_submit" name="applyBtn"
 value="<ssf:nlt tag="button.apply"/>"
/>
<input style="margin-left:15px;" type="submit" class="ss_submit" name="closeBtn"
 value="<ssf:nlt tag="button.close"/>"
 onClick="self.location.href='<portlet:renderURL windowState="normal" portletMode="view"/>';return false;"
/>
</td>
</tr>
</table>

</form>
</fieldset>


</div>
</div>
</div>
</div>
