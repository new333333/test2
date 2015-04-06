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
<%@ page import="org.kablink.teaming.domain.ShareItem" %>
<% boolean shared; %>

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
  <TD valign="top" width="15%">
    <c:if test="${ssBinder.entityType == 'folder'}"><ssf:nlt tag="access.operation.viewFolder"/></c:if>
    <c:if test="${ssBinder.entityType == 'profiles'}"><ssf:nlt tag="access.operation.viewProfiles"/></c:if>
    <c:if test="${ssBinder.entityType != 'folder' && ssBinder.entityType != 'profiles'}"><ssf:nlt tag="access.operation.viewWorkspace"/></c:if>
  </TD>
  <TD valign="top" width="50%">
    <ul style="margin:0px 0px 0px 12px; padding:0px;">
    <c:forEach var="user" items="${ss_accessSortedUsersAll}">
    	<c:set var="shared" value="false" />
		<c:forEach var="shareItem1" items="${ss_accessControlShareItems}">
		  <jsp:useBean id="shareItem1" type="org.kablink.teaming.domain.ShareItem" />
			<%
			if (shareItem1.isLatest()) {
				%>
				<c:if test="${ss_accessControlShareItemRecipients[shareItem1.id].id == user.id}">
					<c:set var="shared" value="true" />
				</c:if>
				<%
			}
			%>
		</c:forEach>
		<c:if test="${!empty ssOperationMap['readEntries'].ssUsers[user.id] || shared}">
		  <li style="list-style: square outside none;">
		    <span><ssf:userTitle user="${user}"/></span> <span class="ss_smallprint ss_italic">(<ssf:userName user="${user}"/>)</span>
		  </li>
		</c:if>
    </c:forEach>
    </ul>
    <c:if test="${empty ssOperationMap['readEntries'].ssUsers}">&nbsp;</c:if>
  </TD>
  <TD valign="top" width="35%">
     <ul style="margin:0px 0px 0px 12px; padding:0px;">
     <c:forEach var="group" items="${ss_accessSortedGroupsAll}">
    	<c:set var="shared" value="false" />
		<c:forEach var="shareItem2" items="${ss_accessControlShareItems}">
		  <jsp:useBean id="shareItem2" type="org.kablink.teaming.domain.ShareItem" />
			<%
			if (shareItem2.isLatest()) {
				%>
				<c:if test="${ss_accessControlShareItemRecipients[shareItem2.id].id == group.id}">
					<c:set var="shared" value="true" />
				</c:if>
				<%
			}
			%>
		</c:forEach>
        <c:if test="${!empty ssOperationMap['readEntries'].ssGroups[group.id] || shared}">
          <li style="list-style: square outside none;">
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
		    <c:if test="${ss_accessWorkareaIsPersonal && group.id == ss_accessAllUsersGroup}">
      		  <span class="ss_fineprint" style="vertical-align: super;">1</span>
      		  <c:set var="allUsersGroupSeen" value="true"/>
    	    </c:if>
		  </a>
          </li>
        </c:if>
    </c:forEach>
    </ul>
    <c:if test="${empty ssOperationMap['readEntries'].ssGroups}">&nbsp;</c:if>
  </TD>
</TR>

<c:if test="${ssBinder.entityType == 'folder'}">
<TR>
  <TD valign="top"><ssf:nlt tag="access.operation.create"/></TD>
  <TD valign="top">
    <ul style="margin:0px 0px 0px 12px; padding:0px;">
    <c:forEach var="user" items="${ss_accessSortedUsersAll}">
    	<c:set var="shared" value="false" />
		<c:forEach var="shareItem3" items="${ss_accessControlShareItems}">
		  <jsp:useBean id="shareItem3" type="org.kablink.teaming.domain.ShareItem" />
			<%
			if (shareItem3.isLatest()) {
				%>
				<c:if test="${ss_accessControlShareItemRecipients[shareItem3.id].id == user.id}">
				  <c:if test="${shareItem3.role == 'EDITOR' || shareItem3.role == 'CONTRIBUTOR'}">
					<c:set var="shared" value="true" />
				  </c:if>
				</c:if>
				<%
			}
			%>
		</c:forEach>
      <c:if test="${!empty ssOperationMap['createEntries'].ssUsers[user.id] || shared}">
        <li style="list-style: square outside none;"><span><ssf:userTitle user="${user}"/></span> 
          <span class="ss_smallprint ss_italic">(<ssf:userName user="${user}"/>)</span></li>
      </c:if>
    </c:forEach>
    </ul>
    <c:if test="${empty ssOperationMap['createEntries'].ssUsers}">&nbsp;</c:if>
  </TD>
  <TD valign="top">
     <ul style="margin:0px 0px 0px 12px; padding:0px;">
     <c:forEach var="group" items="${ss_accessSortedGroupsAll}">
    	<c:set var="shared" value="false" />
		<c:forEach var="shareItem4" items="${ss_accessControlShareItems}">
		  <jsp:useBean id="shareItem4" type="org.kablink.teaming.domain.ShareItem" />
			<%
			if (shareItem4.isLatest()) {
				%>
				<c:if test="${ss_accessControlShareItemRecipients[shareItem4.id].id == group.id}">
				  <c:if test="${shareItem4.role == 'EDITOR' || shareItem4.role == 'CONTRIBUTOR'}">
					<c:set var="shared" value="true" />
				  </c:if>
				</c:if>
				<%
			}
			%>
		</c:forEach>
      <c:if test="${!empty ssOperationMap['createEntries'].ssGroups[group.id] || shared}">
        <li style="list-style: square outside none;"><a href="<ssf:url
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
		  <c:if test="${ss_accessWorkareaIsPersonal && group.id == ss_accessAllUsersGroup}">
      		<span class="ss_fineprint" style="vertical-align: super;">1</span>
      		<c:set var="allUsersGroupSeen" value="true"/>
    	  </c:if>
		</a></li>
      </c:if>
    </c:forEach>
    </ul>
    <c:if test="${empty ssOperationMap['createEntries'].ssGroups}">&nbsp;</c:if>
  </TD>
</TR>
</c:if>

<TR>
  <TD valign="top">
    <c:if test="${ssBinder.entityType == 'folder'}"><ssf:nlt tag="access.operation.manageFolder"/></c:if>
    <c:if test="${ssBinder.entityType == 'profiles'}"><ssf:nlt tag="access.operation.manageProfiles"/></c:if>
    <c:if test="${ssBinder.entityType != 'folder' && ssBinder.entityType != 'profiles'}"><ssf:nlt tag="access.operation.manageWorkspace"/></c:if>
  </TD>
  <TD valign="top">
    <ul style="margin:0px 0px 0px 12px; padding:0px;">
    <c:forEach var="user" items="${ss_accessSortedUsersAll}">
    	<c:set var="shared" value="false" />
		<c:forEach var="shareItem5" items="${ss_accessControlShareItems}">
		  <jsp:useBean id="shareItem5" type="org.kablink.teaming.domain.ShareItem" />
			<%
			if (shareItem5.isLatest()) {
				%>
				<c:if test="${ss_accessControlShareItemRecipients[shareItem5.id].id == user.id}">
				  <c:if test="${shareItem5.role == 'CONTRIBUTOR'}">
					<c:set var="shared" value="true" />
				  </c:if>
				</c:if>
				<%
			}
			%>
		</c:forEach>
      <c:if test="${!empty ssOperationMap['binderAdministration'].ssUsers[user.id] || shared}">
        <li style="list-style: square outside none;"><span><ssf:userTitle user="${user}"/></span> 
          <span class="ss_smallprint ss_italic">(<ssf:userName user="${user}"/>)</span></li>
      </c:if>
    </c:forEach>
    </ul>
    <c:if test="${empty ssOperationMap['binderAdministration'].ssUsers}">&nbsp;</c:if>
  </TD>
  <TD valign="top">
     <ul style="margin:0px 0px 0px 12px; padding:0px;">
     <c:forEach var="group" items="${ss_accessSortedGroupsAll}">
    	<c:set var="shared" value="false" />
		<c:forEach var="shareItem6" items="${ss_accessControlShareItems}">
		  <jsp:useBean id="shareItem6" type="org.kablink.teaming.domain.ShareItem" />
			<%
			if (shareItem6.isLatest()) {
				%>
				<c:if test="${ss_accessControlShareItemRecipients[shareItem6.id].id == group.id}">
				  <c:if test="${shareItem6.role == 'CONTRIBUTOR'}">
					<c:set var="shared" value="true" />
				  </c:if>
				</c:if>
				<%
			}
			%>
		</c:forEach>
      <c:if test="${!empty ssOperationMap['binderAdministration'].ssGroups[group.id] || shared}">
        <li style="list-style: square outside none;"><a href="<ssf:url
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
		  <c:if test="${ss_accessWorkareaIsPersonal && group.id == ss_accessAllUsersGroup}">
      		<span class="ss_fineprint" style="vertical-align: super;">1</span>
      		<c:set var="allUsersGroupSeen" value="true"/>
    	  </c:if>
		</a></li>
      </c:if>
    </c:forEach>
    </ul>
    <c:if test="${empty ssOperationMap['binderAdministration'].ssGroups}">&nbsp;</c:if>
  </TD>
</TR>

</TBODY>
</TABLE>
<c:if test="${allUsersGroupSeen}">
<br>
<div>
<span class="ss_smallprint" style="vertical-align: super;">1</span> 
<span><ssf:nlt tag="access.allUsersNote"/></span>
</div>
</c:if>
</div>
