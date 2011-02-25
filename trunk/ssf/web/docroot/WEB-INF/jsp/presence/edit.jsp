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

<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
<script type="text/javascript" src="<html:rootPath />js/jsp/tag_jsps/find/find.js"></script>

<div class="ss_portlet">
<div class="ss_style ss_form" style="margin:6px;">
<div>
<div style="margin:6px; width:100%;">
<h3><ssf:nlt tag="portlet.presence.configure" text="Configure buddy list"/></h3>
<fieldset class="ss_fieldset">
  <legend class="ss_legend"><ssf:nlt tag="portlet.presence.buddies" 
    text="Buddies"/></legend>
 <form class="ss_style" name="${renderResponse.namespace}fm" method="post" 
  onSubmit="return ss_onSubmit(this);"
  action="<portlet:actionURL windowState="maximized"><portlet:param 
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
<table cellpadding="10px" width="100%" style="border-spacing: 10px;">

<tr>
<td class="ss_bold" valign="top"><ssf:nlt tag="general.users" 
 text="Users"/></td>
<td valign="top">
  <ssf:find formName="${renderResponse.namespace}fm" formElement="users" 
    type="user" userList="${ssUsers}" width="200px"/>
</td>
</tr>
<tr>
<td class="ss_bold" valign="top"><ssf:nlt tag="general.groups" 
 text="Groups"/></td>
<td valign="top">
  <ssf:find formName="${renderResponse.namespace}fm" formElement="groups" 
    type="group" userList="${ssGroups}" width="200px"/>
</td>
</tr>

<tr>
<td colspan="2">
	<ssf:clipboard type="user" formElement="users" />	
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
