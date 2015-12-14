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
<% // User filters %>
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
<%@ include file="/WEB-INF/jsp/forum/init.jsp" %>
<%@ page import="java.util.TreeMap" %>
<%@ page import="org.kablink.teaming.comparator.StringComparator" %>
<jsp:useBean id="ssUser" type="org.kablink.teaming.domain.User" scope="request" />

<c:set var="currentFilter" value="${ssUserFolderProperties.userFilter}"/>
<c:set var="currentFilterScope" value="${ssUserFolderProperties.userFilterScope}"/>
<div align="left" class="ssPageNavi">

<table width="98%">
 <tbody>
  <tr><td align="left" width="1%" nowrap>
     <div class="ss_link_2">
     	<span style="white-space:nowrap;"><ssf:nlt tag="filter.filter" text="Filter"/>:&nbsp;</span>
  	 </div>
	</td>
	<td align="left" width="93%">
	 <div class="ss_navbar_inline">
		<ul>
			<c:if test="${ssConfigJspStyle != 'template'}">
			<li>
				<a href="<ssf:url action="${action}" actionUrl="true"><ssf:param 
				name="binderId" value="${ssBinder.id}"/><ssf:param 
				name="operation" value="select_filter"/><ssf:param 
				name="select_filter" value=""/></ssf:url>"><span 
					<c:if test="${empty currentFilter}"> class="ss_navbar_current"</c:if>
					<c:if test="${!empty currentFilter}"> class="ss_navbar_not_current"</c:if>
					><ssf:nlt tag="None"/></span></a>
			</li>				
			<% 
				//Sort the filters
				TreeMap tm = new TreeMap(new StringComparator(ssUser.getLocale()));
			%>
			<c:set var="sortedFilters" value="<%= tm %>" />
			<c:forEach var="filter1" items="${ssUserFolderProperties.searchFilterMap}">
				<jsp:useBean id="filter1" type="java.util.Map.Entry" />
				<% tm.put(filter1.getKey(), "personal"); %>
			</c:forEach>
			<c:forEach var="filter2" items="${ssBinder.properties.binderFilters}">
				<jsp:useBean id="filter2" type="java.util.Map.Entry"  />
				<% tm.put(filter2.getKey(), "global"); %>
			</c:forEach>
						
			<c:forEach var="filter" items="${sortedFilters}">
			 <li><a href="<ssf:url action="${action}" actionUrl="true"><ssf:param 
				name="binderId" value="${ssBinder.id}"/><ssf:param 
				name="operation" value="select_filter"/><ssf:param 
				name="operation2" value="${filter.value}"/><ssf:param 
				name="select_filter" value="${filter.key}"/></ssf:url>"><span 
					<c:if test="${filter.key == currentFilter && currentFilterScope == filter.value}"> class="ss_navbar_current"</c:if>
					<c:if test="${filter.key != currentFilter || currentFilterScope != filter.value}"> class="ss_navbar_not_current"</c:if>
					><c:out value="${filter.key}"/></span></a>
			 </li>
			</c:forEach>
			</c:if>
			<c:if test="${ssConfigJspStyle == 'template'}">
			  <li>
			    <ssf:nlt tag="None"/></span>
			  </li>
			</c:if>
		</ul>
				
	 </div>
	</td>
	<td align="left" width="5%" nowrap>
		<div class="ss_link_2">
			<c:if test="${ssConfigJspStyle != 'template'}">
			  <a href="<ssf:url ><ssf:param 
			  name="action" value="build_filter"/><ssf:param 
			  name="binderId" value="${ssBinder.id}"/><ssf:param 
			  name="binderType" value="${ssBinder.entityType}"/></ssf:url>"
			  title="<ssf:nlt tag="sidebar.tags.filterHover"/>"
			  ><span style="white-space:nowrap;"><ssf:nlt tag="sidebar.tags.filter"/></span></a>
			</c:if>
			<c:if test="${ssConfigJspStyle == 'template'}">
			  <span style="white-space:nowrap;"><ssf:nlt tag="sidebar.tags.filter"/></span>
			</c:if>
		</div>
	</td>
  </tr>

 </tbody>
</table>
</div>
