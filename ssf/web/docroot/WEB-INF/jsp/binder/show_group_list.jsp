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
<%@ page import="org.kablink.teaming.ObjectKeys" %>
<%@ page import="org.kablink.teaming.util.NLT" %>
<ssf:ifadapter>
<body class="tundra">
</ssf:ifadapter>
<script type="text/javascript" src="<html:rootPath />js/jsp/tag_jsps/find/find.js"></script>
<div class="ss_style ss_portlet" style="padding:10px;">

<c:set var="formTitle" value="${ssGroup.title} (${ssGroup.name})"/>
<c:if test="${empty ssGroup && !empty ssTeamBinder}">
  <c:set var="formTitle" value="${ssTeamBinder.title}"/>
</c:if>
  
<ssf:form title="${formTitle}">

<c:if test="${!empty ssUsers}">
<br/>
<br/>
<span class="ss_bold"><ssf:nlt tag="access.users"/></span>
<br/>
  <c:forEach var="user" items="${ssUsers}" varStatus="status">
	<ssf:userTitle user="${user}"/> (<ssf:userName user="${user}"/>)<br/>
  </c:forEach>
</c:if>

<c:if test="${!empty ssGroups}">
<br/>
<br/>
<span class="ss_bold"><ssf:nlt tag="access.groups"/></span>
<br/>
  <c:forEach var="group" items="${ssGroups}" varStatus="status">
	<a href="<ssf:url
		adapter="true" 
		crawlable="true"
		portletName="ss_forum" 
		action="__ajax_request"
		actionUrl="false"><ssf:param 
		name="operation" value="get_group_list"/><ssf:param 
		name="groupId" value="${group.id}"/></ssf:url>"
    onClick="ss_openUrlInWindow(this, '_blank', 400, 600);return false;">${group.title}</a>
    <a href="<ssf:url
		adapter="true" 
		crawlable="true"
		portletName="ss_forum" 
		action="__ajax_request"
		actionUrl="false"><ssf:param 
		name="operation" value="get_group_list"/><ssf:param 
		name="groupId" value="${group.id}"/></ssf:url>"
    onClick="ss_openUrlInWindow(this, '_blank', 400, 600);return false;"> (${group.name})</a><br/>
  </c:forEach>
</c:if>

<c:if test="${empty ssGroups && !empty ssGroup}">
	<jsp:useBean id="ssGroup" type="org.kablink.teaming.domain.Group" scope="request"/>
    <% if (ObjectKeys.ALL_USERS_GROUP_INTERNALID.equals(ssGroup.getInternalId())) { %>
    	<div style="padding:20px;">
    		<span class="ss_italic">
    			<ssf:nlt tag="group.allusers.contains"/>
    		</span>
    	</div>
    <% } %>
    <% if (ObjectKeys.ALL_EXT_USERS_GROUP_INTERNALID.equals(ssGroup.getInternalId())) { %>
    	<div style="padding:20px;">
    		<span class="ss_italic">
    			<ssf:nlt tag="group.allextusers.contains"/>
    		</span>
    	</div>
    <% } %>
</c:if>

<div style="padding-top:30px;">
<input type="submit" class="ss_submit" value="<ssf:nlt tag="button.close"/>" 
  onClick="ss_cancelButtonCloseWindow();return false;">
</div>

</ssf:form>
</div>
<ssf:ifadapter>
</body>
</html>
</ssf:ifadapter>
