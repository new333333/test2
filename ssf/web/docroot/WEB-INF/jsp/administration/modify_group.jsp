<%
/**
 * Copyright (c) 2007 SiteScape, Inc. All rights reserved.
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
<%@ page import="com.sitescape.team.util.NLT" %>
<%@ include file="/WEB-INF/jsp/common/servlet.include.jsp" %>
<body>
<script type="text/javascript" src="<html:rootPath/>js/jsp/tag_jsps/find/single_user.js"></script>
<script type="text/javascript" src="<html:rootPath/>js/jsp/tag_jsps/find/user_list.js"></script>
<script type="text/javascript">
function ss_saveResults() {
	if (parent.ss_size_group_iframe) parent.ss_size_group_iframe();
}
</script>

<div class="ss_style ss_portlet">
<span class="ss_bold ss_largerprint">${ssGroup.title}</span> <span class="ss_smallprint">(${ssGroup.name})</span>
<br/>
<br/>
<form name="ss_groupForm" id="ss_groupForm" method="post"
  action="<ssf:url adapter="true" portletName="ss_forum" 
		    action="__ajax_request"
		    actionUrl="true"
		    binderId="${ssBinderId}"
		    entryId="${ssGroup.id}">
			<ssf:param name="operation" value="modify_group"/>
			<ssf:param name="namespace" value="${ss_namespace}"/>
			</ssf:url>"
  onSubmit="return ss_onSubmit(this);">
		
<ssf:expandableArea title="<%= NLT.get("administration.modify.group") %>">
	<span class="ss_bold"><ssf:nlt tag="administration.add.groupTitle"/></span><br/>
	<input type="text" class="ss_text" size="40" name="title" value="${ssGroup.title}"><br/><br/>
		
	<span class="ss_bold"><ssf:nlt tag="administration.add.groupDescription"/></span><br/>
	<textarea name="description" wrap="virtual" rows="4" cols="40">${ssGroup.description}</textarea><br/><br/>
		
</ssf:expandableArea>
<br/>
<br/>
<span class="ss_bold"><ssf:nlt tag="administration.modifyGroupMembership" /></span>
<br/>
<table class="ss_style" border="0" cellspacing="0" cellpadding="3" width="95%">
<tr>
<td class="ss_bold" valign="top"><ssf:nlt tag="general.users" text="Users"/></td>
<td valign="top">
  <ssf:find formName="ss_groupForm" formElement="users" 
    type="user" userList="${ssUsers}" binderId="${ssBinderId}"/>
</td>
</tr>
<tr>
<td class="ss_bold" valign="top"><ssf:nlt tag="general.groups" text="Groups"/></td>
<td valign="top">
  <ssf:find formName="ss_groupForm" formElement="groups" 
    type="group" userList="${ssGroups}"/>
</td>
</tr>
<tr><td colspan="2">
	<ssf:clipboard type="user" formElement="users" />
</td></tr>
</table>
<br/>
<input type="submit" class="ss_submit" name="okBtn" value="<ssf:nlt tag="button.apply"/>">
<input type="submit" style="margin-left:15px;" class="ss_submit" name="closeBtn" 
  value="<ssf:nlt tag="button.close" />"
  onClick="if (parent.ss_hideDivNone) parent.ss_hideDivNone('ss_groupsDiv${ss_namespace}'); return false;" />
  
</form>
</div>


</body>
</html>

