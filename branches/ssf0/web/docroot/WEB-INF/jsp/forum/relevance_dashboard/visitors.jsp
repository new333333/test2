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
<%@ page import="com.sitescape.team.util.NLT" %>
<%@ include file="/WEB-INF/jsp/common/common.jsp" %>
<div id="ss_today">
<div id="ss_nextPage" align="right">
<c:if test="${ss_visitorsPage > '0'}">
<a href="javascript: ;" 
  onClick="ss_showDashboardPage('${ssBinder.id}', 'visitors', '${ss_visitorsPage}', 'previous', 'ss_dashboardVisitors${renderResponse.namespace}');return false;">
<img src="<html:imagesPath/>pics/sym_arrow_left_.gif" 
  title="<ssf:nlt tag="general.previousPage"/>"/>
</a>
</c:if>
<c:if test="${empty ss_visitorsPage || ss_visitorsPage <= '0'}">
<img src="<html:imagesPath/>pics/sym_arrow_left_g.gif"/>
</c:if>
<c:if test="${!empty ss_visitors}">
<a href="javascript: ;" 
  onClick="ss_showDashboardPage('${ssBinder.id}', 'visitors', '${ss_visitorsPage}', 'next', 'ss_dashboardVisitors${renderResponse.namespace}');return false;">
<img src="<html:imagesPath/>pics/sym_arrow_right_.gif"
  title="<ssf:nlt tag="general.nextPage"/>"/>
</a>
</c:if>
<c:if test="${empty ss_visitors}">
<img src="<html:imagesPath/>pics/sym_arrow_right_g.gif"/>
</c:if>
</div>

  <c:forEach var="user" items="${ss_visitors}">
  <table cellpadding="0" cellspacing="0"><tbody>
    <tr><td valign="top">
		    <span><ssf:showUser user="${user}" titleStyle="ss_link_1" /></span>
		    <c:if test="${!empty user.status}">
		      <div id="ss_im_status"><em>${user.status}</em></div>
		    </c:if>
    </td></tr>
  </tbody></table>
  </c:forEach>
</div>
