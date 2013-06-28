<%
/**
 * Copyright (c) 1998-2010 Novell, Inc. and its licensors. All rights reserved.
 * 
 * This work is governed by the Common Public Attribution License Version 1.0 (the
 * "CPAL"); you may not use this file except in compliance with the CPAL. You may
 * obtain a copy of the CPAL at http://www.opensource.org/licenses/cpal_1.0. The
 * CPAL is based on the Mozilla Public License Version 1.1 but Sections 14 and 15
 * have been added to cover use of software over a computer network and provide
 * for limited attribution for the Original Developer. In addition, Exhibit A has
 * been modified to be consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT
 * WARRANTY OF ANY KIND, either express or implied. See the CPAL for the specific
 * language governing rights and limitations under the CPAL.
 * 
 * The Original Code is ICEcore, now called Kablink. The Original Developer is
 * Novell, Inc. All portions of the code written by Novell, Inc. are Copyright
 * (c) 1998-2010 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2010 Novell, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by Kablink]
 * Attribution URL: [www.kablink.org]
 * Graphic Image as provided in the Covered Code
 * [ssf/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are
 * defined in the CPAL as a work which combines Covered Code or portions thereof
 * with code not governed by the terms of the CPAL.
 * 
 * NOVELL and the Novell logo are registered trademarks and Kablink and the
 * Kablink logos are trademarks of Novell, Inc.
 */
%>
<%@ page import="org.kablink.teaming.util.NLT" %>
<%@ include file="/WEB-INF/jsp/common/servlet.include.jsp" %>
<body class="tundra">
<script type="text/javascript" src="<html:rootPath />js/jsp/tag_jsps/find/find.js"></script>
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
		
<ssf:expandableArea title='<%= NLT.get("administration.modify.group") %>'>
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
    type="user" userList="${ssUsers}" binderId="${ssBinderId}" width="150px"/>
</td>
</tr>
<tr>
<td class="ss_bold" valign="top"><ssf:nlt tag="general.groups" text="Groups"/></td>
<td valign="top">
  <ssf:find formName="ss_groupForm" formElement="groups" 
    type="group" userList="${ssGroups}" width="150px"/>
</td>
</tr>
<tr><td colspan="2">
	<ssf:clipboard type="user" formElement="users" />
</td></tr>
</table>
<br/>
<input type="submit" class="ss_submit" name="okBtn" value="<ssf:nlt tag="button.apply"/>">
<input type="submit" class="ss_submit" name="deleteBtn" value="<ssf:nlt tag="button.delete"/>">
<input type="submit" style="margin-left:15px;" class="ss_submit" name="closeBtn" 
  value="<ssf:nlt tag="button.close" />"
  onClick="if (parent.ss_hideDivNone) parent.ss_hideDivNone('ss_groupsDiv${ss_namespace}'); return false;" />
  
</form>
</div>


</body>
</html>

