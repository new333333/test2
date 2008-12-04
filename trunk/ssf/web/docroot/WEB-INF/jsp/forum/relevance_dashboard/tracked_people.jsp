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
<%@ page import="org.kablink.teaming.util.NLT" %>
<%@ include file="/WEB-INF/jsp/common/common.jsp" %>
<div id="ss_para" "ss_link_5">
<div id="ss_hints"><em><ssf:nlt tag="relevance.peopleBeingTracked"/></em></div>
<c:forEach var="user" items="${ss_trackedPeople}">

    <div id="ss_col3_para" > 
    <ssf:showUser user="${user}" titleStyle="ss_link_1" />
    <c:if test="${ss_show_tracked_item_delete_button == 'true'}">
    <img style="padding:4px 0px 0px 2px;" align="texttop"
      src="<html:rootPath/>images/pics/delete.gif"
      onClick="ss_trackedPeopleDelete(this, '${user.id}');"/>
    </c:if>
    <c:if test="${!empty user.status}">
	    <div>
			<span class="ss_smallprint"><fmt:formatDate timeZone="${ssUser.timeZone.ID}"
			  value="${user.statusDate}" type="both" 
			  timeStyle="short" dateStyle="short" /></span>
	    </div>
	    <div id="ss_im_status"><em>${user.status}</em></div>
    </c:if>
    </div><!-- end of para -->
    
</c:forEach>
	</div> <!-- end of ss_para -->

<div class="ss_clear_float"></div>
