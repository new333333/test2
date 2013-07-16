<%
/**
 * Copyright (c) 1998-2009 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2009 Novell, Inc. All Rights Reserved.
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
<%@ page import="org.kablink.teaming.security.function.Condition" %>
<%@ page import="org.kablink.teaming.security.function.ConditionalClause" %>
<%@ page import="org.kablink.teaming.security.function.Function" %>
<%@ page import="java.util.Set" %>
<%@ page import="java.util.HashSet" %>
<%@ page import="java.util.List" %>
<%@ page import="org.kablink.teaming.domain.Principal"     %>
<%@ page import="org.kablink.teaming.util.ResolveIds" %>
<%@ include file="/WEB-INF/jsp/common/common.jsp" %>
<c:set var="ss_windowTitle" value='<%= NLT.get("binder.configure.access_control.sharing.manageShares") %>' scope="request"/>
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
<body class="ss_style_body tundra">

<script type="text/javascript" >
var doitText = "<ssf:escapeQuotes><ssf:nlt tag="binder.configure.access_control.sharing.revokeWarning"/></ssf:escapeQuotes>";
function setItemToDelete(id) {
	var formObj = document.getElementById("form1");
	if (formObj != null) {
		if (confirm(doitText)) {
			formObj['shareItemId'].value = id;
		} else {
			formObj['shareItemId'].value = '';
		}
	}
}
</script>

<div class="ss_portlet">
<c:set var="title_tag" value="binder.configure.access_control.sharing.manageShares"/>
<ssf:form titleTag="${title_tag}">
<div class="ss_style ss_form" style="margin:0px; padding:10px 16px 10px 10px;">
<div style="margin:6px; width:100%;">
<table cellpadding="0" cellspacing="0" width="100%">
<tr>
<td valign="top">
<c:choose>
<c:when test="${ssWorkArea.workAreaType == 'folder'}">
  <span><ssf:nlt tag="access.currentFolder"/></span>
<span class="ss_bold ss_largestprint"><ssf:nlt tag="${ssWorkArea.title}" checkIfTag="true"/></span>
</c:when>
<c:when test="${ssWorkArea.workAreaType == 'folderEntry'}">
  <span><ssf:nlt tag="access.currentEntry"/></span>
<span class="ss_bold ss_largestprint"><ssf:nlt tag="${ssWorkArea.title}" checkIfTag="true"/></span>
</c:when>
<c:otherwise>
  <span><ssf:nlt tag="access.currentWorkspace"/></span>
	<% //need to check tags for templates %>
	<span class="ss_bold ss_largestprint"><ssf:nlt tag="${ssWorkArea.title}" checkIfTag="true"/></span>
</c:otherwise>
</c:choose>
<br/>

<form name="form1" id="form1" class="ss_style ss_form" method="post" 
	action="<ssf:url><ssf:param 
	  		name="action" value="configure_access_control"/><ssf:param 
	  		name="actionUrl" value="true"/><ssf:param 
	  		name="workAreaId" value="${ssWorkArea.workAreaId}"/><ssf:param 
	  		name="workAreaType" value="${ssWorkArea.workAreaType}"/><ssf:param
	  		name="operation" value="manage_sharing"/></ssf:url>"
	onSubmit="ss_checkForAllUsersGroup();return true;"
>
	
	<div align="right">
	  <input type="submit" class="ss_submit" name="closeBtn" value="<ssf:nlt tag="button.close"/>"/>
	</div>
		
<ssf:box style="rounded">

  <div style="padding:4px 8px;">
	<table class="ss_table">
	 <thead>
	 <tr>
	  <th><ssf:nlt tag="binder.configure.access_control.sharing.sharer"/></th>
	  <th><ssf:nlt tag="binder.configure.access_control.sharing.recipient"/></th>
	  <th><ssf:nlt tag="binder.configure.access_control.sharing.right"/></th>
	  <th><ssf:nlt tag="binder.configure.access_control.sharing.expiration"/></th>
	  <th><ssf:nlt tag="binder.configure.access_control.sharing.revokeHeader"/></th>
	 </tr>
	 </thead>
	 
      <c:forEach var="shareItem" items="${ss_accessControlShareItems}">
      	<jsp:useBean id="shareItem" type="org.kablink.teaming.domain.ShareItem" />
      	<%
      	if (shareItem.isLatest()) {
      		List sharers = ResolveIds.getPrincipals(String.valueOf(shareItem.getSharerId().longValue()));
      		if (!sharers.isEmpty()) {
      			Principal sharer = (Principal)sharers.get(0);
      	%>
        <c:set var="recipient" value="${ss_accessControlShareItemRecipients[shareItem.id]}"/>
        <tr>
          <td class="ss_table_paragraph">
            <%= sharer.getTitle() %>
            <span class="ss_small">&nbsp;(<%= sharer.getName() %>)</span>
          </td>
          <td class="ss_table_paragraph">
			<img src="<html:imagesPath/>icons/${shareItem.recipientType.icon}"/> 
			<c:if test="${recipient.entityType == 'user'}">
			  ${recipient.title} <span class="ss_small">&nbsp;(${recipient.name})</span>
			</c:if>
			<c:if test="${recipient.entityType == 'group'}">
			  <a href="<ssf:url
					adapter="true" 
					crawlable="true"
					portletName="ss_forum" 
					action="__ajax_request"
					actionUrl="false"><ssf:param 
					name="operation" value="get_group_list"/><ssf:param 
					name="groupId" value="${recipient.id}"/></ssf:url>"
			    onClick="ss_openUrlInWindow(this, '_blank', 400, 600);return false;">${recipient.title}</a>
			</c:if>
			<c:if test="${recipient.entityType == 'folder' || recipient.entityType == 'workspace'}">
			  <a href="<ssf:url
					adapter="true" 
					crawlable="true"
					portletName="ss_forum" 
					action="__ajax_request"
					actionUrl="false"><ssf:param 
					name="operation" value="get_group_list"/><ssf:param 
					name="teamId" value="${recipient.id}"/></ssf:url>"
			    onClick="ss_openUrlInWindow(this, '_blank', 400, 600);return false;">${recipient.title}</a>
			</c:if>
            <c:if test="${recipient.entityType == 'user' || recipient.entityType == 'group'}">
              <span class="ss_small">&nbsp;(${recipient.name})</span>
            </c:if>
          </td>
          <td class="ss_table_paragraph">${shareItem.role.title}</td>
          <td class="ss_table_paragraph">
            <c:if test="${!empty shareItem.endDate}">
              <fmt:formatDate timeZone="${ssUser.timeZone.ID}"
			     value="${shareItem.endDate}" type="date" dateStyle="medium" />
              <c:if test="${shareItem.expired}">
                <span class="ss_smallprint ss_errorLabel">
                  &nbsp;(<ssf:nlt tag="binder.configure.access_control.sharing.exipred"/>)
                </span>
              </c:if>
            </c:if>
          </td>
          <td class="ss_table_paragraph">
            <c:if test="${ss_accessControlShareItemDeleteRights[shareItem.id]}">
              <input type="submit" name="revokeBtn" 
                value="<ssf:nlt tag='button.delete'/>"
                onClick="setItemToDelete('${shareItem.id}');"
              >
            </c:if>
          </td>
        </tr>
        <% 
        	} 
        }
        %>
      </c:forEach>
    </table>

  </div>
</fieldset>
<input type="hidden" name="shareItemId"/>

</ssf:box>

<div style="padding-top:10px;">
  <input type="submit" class="ss_submit" name="closeBtn" value="<ssf:nlt tag="button.close"/>"/>
</div>

</form>
</ssf:form>
</div>

</body>
</html>
