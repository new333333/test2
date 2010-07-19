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
<%@ include file="/WEB-INF/jsp/common/common.jsp" %>
<div id="ss_today">
<div align="right">
<c:if test="${ss_visitorsPage > '0'}">
<a href="javascript: ;" 
  onclick="ss_showDashboardPage('${ssBinder.id}', '${ssRDCurrentTab}', 'visitors', '${ss_visitorsPage}', 'previous', 'ss_dashboardVisitors', '${ss_relevanceDashboardNamespace}');return false;">
<img src="<html:imagesPath/>pics/sym_arrow_left_.png" 
  title="<ssf:nlt tag="general.previousPage"/>" <ssf:alt/>/>
</a>
</c:if>
<c:if test="${empty ss_visitorsPage || ss_visitorsPage <= '0'}">
<img src="<html:imagesPath/>pics/sym_arrow_left_g.png" <ssf:alt/>/>
</c:if>
<c:if test="${!empty ss_visitors}">
<a href="javascript: ;" 
  onclick="ss_showDashboardPage('${ssBinder.id}', '${ssRDCurrentTab}', 'visitors', '${ss_visitorsPage}', 'next', 'ss_dashboardVisitors', '${ss_relevanceDashboardNamespace}');return false;">
<img src="<html:imagesPath/>pics/sym_arrow_right_.png"
  title="<ssf:nlt tag="general.nextPage"/>" <ssf:alt/>/>
</a>
</c:if>
<c:if test="${empty ss_visitors}">
<img src="<html:imagesPath/>pics/sym_arrow_right_g.png" <ssf:alt/>/>
</c:if>
</div>

  <c:forEach var="user" items="${ss_visitors}">
    <div id="ss_col3_para" >
	  <span><ssf:showUser user="${user}" titleStyle="ss_link_1" /></span>
	  <c:if test="${!empty user.status}">
		<div class="list-indent"><span class="ss_smallprint"><fmt:formatDate timeZone="${ssUser.timeZone.ID}"
        value="${user.statusDate}" type="both" 
	    timeStyle="short" dateStyle="short" /></span></div>
		<div id="ss_im_status">${user.status}</div>
	  </c:if>
    </div><!-- end of para -->
  </c:forEach>
  <c:if test="${empty ss_visitors && ss_pageNumber > '0'}">
    <span class="ss_italic"><ssf:nlt tag="whatsnew.noMoreEntriesFound"/></span>
  </c:if>
</div><!-- end of today -->
