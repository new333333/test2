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
<%@ include file="/WEB-INF/jsp/dashboard/common_setup.jsp" %>

  <c:if test="${!empty ssDashboard}">
	<c:set var="ssUsers" value="${ssDashboard.beans[componentId].ssUsers}"/>
  </c:if>
<c:if test="${!empty ssUsers || ss_windowState == 'maximized'}">
  <div class="ss_portlet_style ss_portlet">
  <table cellpadding="4">
  <tr>
  <td style="padding-bottom:10px;">
  <a class="ss_linkButton ss_bold ss_smallprint" href=""
    onclick="if (${ss_namespace}_${componentId}_presence) {${ss_namespace}_${componentId}_presence.getPresence()};return false;"
  ><ssf:nlt tag="general.Refresh"/></a>
  </td>
  <td align="right" style="padding-left:20px;">
  <span class="ss_smallprint ss_light"><ssf:nlt 
  tag="presence.last.refresh"/> <fmt:formatDate timeZone="${ssUser.timeZone.ID}" value="<%= new java.util.Date() %>" 
  type="time" /></span>
  </td>
  </tr>
  </table>


  <table border="0" cellpadding="4" cellspacing="0">
  <tr>
	<td>
		<table border="0" cellpadding="0" cellspacing="0">
		<tr>
			<td valign="top">
				<c:if test="${!empty ssUsers}">
					<table cellspacing="0" cellpadding="0">
					<c:set var="uCount" value="0"/>
					<c:forEach var="u1" items="${ssUsers}">
					<c:if test="${uCount < 100}">
					<c:set var="uCount" value="${uCount + 1}"/>
					<jsp:useBean id="u1" type="org.kablink.teaming.domain.User" />
					  <tr>
					  <td><span id="${ss_namespace}_${componentId}_user_<c:out value="${u1.id}"/>"
					  ><ssf:presenceInfo user="<%=u1%>" componentId="${ss_namespace}_${componentId}"/> </span></td>
					  <td style="padding-left:10px;">
					  <ssf:ifadapter>
					  <a href="<ssf:permalink entity="${u1}"/>"
					  	onclick="ss_openUrlInWorkarea(this.href, '${u1.workspaceId}', 'view_ws_listing');return false;"
					  ><c:out value="${u1.title}"/></a>
					  </ssf:ifadapter>
					  <ssf:ifnotadapter>

				<c:if test="${ssConfigJspStyle == 'template'}">
					<c:out value="${u1.title}"/>
				</c:if>
				<c:if test="${ssConfigJspStyle != 'template'}">
					  <a href="<ssf:url action="view_ws_listing"><ssf:param 
					  	name="binderId" value="${u1.workspaceId}"/></ssf:url>"
					  	onclick="ss_openUrlInWorkarea(this.href, '${u1.workspaceId}', 'view_ws_listing');return false;"
					  ><c:out value="${u1.title}"/></a>
				</c:if>
					  </ssf:ifnotadapter>
					  </td>							
					  </tr>
					</c:if>
					</c:forEach>
					</table>
					<c:if test="${uCount >= 100}">
					  <div align="right">
					    <span class="ss_fineprint ss_italic">[<ssf:nlt tag="presence.userListTruncated"/>]</span>
					  </div>
					</c:if>
				 </c:if>
				<br>
			</td>
		</tr>
		</table>
	</td>
  </tr>
  </table>
</c:if>
  </div>
