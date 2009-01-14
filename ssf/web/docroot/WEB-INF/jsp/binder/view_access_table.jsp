<%
/**
 * The contents of this file are subject to the Common Public Attribution License Version 1.0 (the "CPAL");
 * you may not use this file except in compliance with the CPAL. You may obtain a copy of the CPAL at
 * http://www.opensource.org/licenses/cpal_1.0. The CPAL is based on the Mozilla Public License Version 1.1
 * but Sections 14 and 15 have been added to cover use of software over a computer network and provide for
 * limited attribution for the Original Developer. In addition, Exhibit A has been modified to be
 * consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT WARRANTY OF ANY KIND,
 * either express or implied. See the CPAL for the specific language governing rights and limitations
 * under the CPAL.
 * 
 * The Original Code is ICEcore. The Original Developer is SiteScape, Inc. All portions of the code
 * written by SiteScape, Inc. are Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * 
 * 
 * Attribution Information
 * Attribution Copyright Notice: Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by ICEcore]
 * Attribution URL: [www.icecore.com]
 * Graphic Image as provided in the Covered Code [web/docroot/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are defined in the CPAL as a
 * work which combines Covered Code or portions thereof with code not governed by the terms of the CPAL.
 * 
 * 
 * SITESCAPE and the SiteScape logo are registered trademarks and ICEcore and the ICEcore logos
 * are trademarks of SiteScape, Inc.
 */
%>

<div id="${ss_accessControlTableDivId}" class="ss_portlet ss_style ss_form">
<TABLE class="ss_table">
<THEAD>
<TR>
  <TH class="ss_table_paragraph_bld" noWrap="noWrap"><ssf:nlt tag="access.operation"/></TH>
  <TH class="ss_table_paragraph_bld" noWrap="noWrap"><ssf:nlt tag="access.users"/></TH>
  <TH class="ss_table_paragraph_bld" noWrap="noWrap"><ssf:nlt tag="access.groups"/></TH>
</TR>
</THEAD>
<TBODY>
<TR>
  <TD valign="top">
    <c:if test="${ssBinder.entityType == 'folder'}"><ssf:nlt tag="access.operation.viewFolder"/></c:if>
    <c:if test="${ssBinder.entityType == 'profiles'}"><ssf:nlt tag="access.operation.viewProfiles"/></c:if>
    <c:if test="${ssBinder.entityType != 'folder' && ssBinder.entityType != 'profiles'}"><ssf:nlt tag="access.operation.viewWorkspace"/></c:if>
  </TD>
  <TD valign="top" nowrap>
    <c:forEach var="user" items="${ss_accessSortedUsers}">
      <c:if test="${!empty ssOperationMap['readEntries'].ssUsers[user.id]}">
        <span>${user.title}</span> <span class="ss_smallprint ss_italic">(${user.name})</span><br/>
      </c:if>
    </c:forEach>
  </TD>
  <TD valign="top">
     <c:forEach var="group" items="${ss_accessSortedGroups}">
      <c:if test="${!empty ssOperationMap['readEntries'].ssGroups[group.id]}">
        <a href="<ssf:url
				adapter="true" 
				crawlable="true"
				portletName="ss_forum" 
				action="__ajax_request"
				actionUrl="false"><ssf:param 
				name="operation" value="get_group_list"/><ssf:param 
				name="groupId" value="${group.id}"/></ssf:url>"
		     onClick="ss_openUrlInWindow(this, '_blank', 400, 600);return false;">
		  <span>${group.title}</span> 
		  <span class="ss_smallprint ss_italic">(${group.name})</span>
		</a><br/>
      </c:if>
    </c:forEach>
  </TD>
</TR>

<c:if test="${ssBinder.entityType == 'folder'}">
<TR>
  <TD valign="top"><ssf:nlt tag="access.operation.create"/></TD>
  <TD valign="top" nowrap>
    <c:forEach var="user" items="${ss_accessSortedUsers}">
      <c:if test="${!empty ssOperationMap['createEntries'].ssUsers[user.id]}">
        <span>${user.title}</span> <span class="ss_smallprint ss_italic">(${user.name})</span><br/>
      </c:if>
    </c:forEach>
  </TD>
  <TD valign="top">
     <c:forEach var="group" items="${ss_accessSortedGroups}">
      <c:if test="${!empty ssOperationMap['createEntries'].ssGroups[group.id]}">
        <a href="<ssf:url
				adapter="true" 
				crawlable="true"
				portletName="ss_forum" 
				action="__ajax_request"
				actionUrl="false"><ssf:param 
				name="operation" value="get_group_list"/><ssf:param 
				name="groupId" value="${group.id}"/></ssf:url>"
		     onClick="ss_openUrlInWindow(this, '_blank', 400, 600);return false;">
		  <span>${group.title}</span> 
		  <span class="ss_smallprint ss_italic">(${group.name})</span>
		</a><br/>
      </c:if>
    </c:forEach>
  </TD>
</TR>
</c:if>

<TR>
  <TD valign="top">
    <c:if test="${ssBinder.entityType == 'folder'}"><ssf:nlt tag="access.operation.manageFolder"/></c:if>
    <c:if test="${ssBinder.entityType == 'profiles'}"><ssf:nlt tag="access.operation.manageProfiles"/></c:if>
    <c:if test="${ssBinder.entityType != 'folder' && ssBinder.entityType != 'profiles'}"><ssf:nlt tag="access.operation.manageWorkspace"/></c:if>
  </TD>
  <TD valign="top" nowrap>
    <c:forEach var="user" items="${ss_accessSortedUsers}">
      <c:if test="${!empty ssOperationMap['binderAdministration'].ssUsers[user.id]}">
        <span>${user.title}</span> <span class="ss_smallprint ss_italic">(${user.name})</span><br/>
      </c:if>
    </c:forEach>
  </TD>
  <TD valign="top">
     <c:forEach var="group" items="${ss_accessSortedGroups}">
      <c:if test="${!empty ssOperationMap['binderAdministration'].ssGroups[group.id]}">
        <a href="<ssf:url
				adapter="true" 
				crawlable="true"
				portletName="ss_forum" 
				action="__ajax_request"
				actionUrl="false"><ssf:param 
				name="operation" value="get_group_list"/><ssf:param 
				name="groupId" value="${group.id}"/></ssf:url>"
		     onClick="ss_openUrlInWindow(this, '_blank', 400, 600);return false;">
		  <span>${group.title}</span> 
		  <span class="ss_smallprint ss_italic">(${group.name})</span>
		</a><br/>
      </c:if>
    </c:forEach>
  </TD>
</TR>

</TBODY>
</TABLE>
</div>
