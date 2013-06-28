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
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ include file="/WEB-INF/jsp/common/common.jsp" %>

<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet" %>
<%@ taglib prefix="portletadapter" uri="http://www.sitescape.com/tags-portletadapter" %>

<portletadapter:defineObjects1/>
<ssf:ifadapter><portletadapter:defineObjects2/></ssf:ifadapter>
<ssf:ifnotadapter><portlet:defineObjects/></ssf:ifnotadapter>
<c:if test="${ssConfigJspStyle != 'mobile'}">
	<a href="javascript: //"
	onclick="ss_toggleShowDiv('ss_show_team_${ss_showTeamInstanceCount}'); return false;" 
	class="ss_smallprint ss_nowrap">
		<img style="margin: 1px 3px 0" border="0" src="<html:imagesPath/>pics/team_16.png" align="absmiddle" />
		<span class="${ss_showTeamTitleStyle}"><c:out value="${ss_showTeamTeam.title}" /></span>
		<ssf:nlt tag="showTeam.team.members"><ssf:param 
		name="value" value="${fn:length(ss_showTeamTeamMembers)}"/></ssf:nlt></a>

		<div id="ss_show_team_${ss_showTeamInstanceCount}" style="display: none;">
			<c:forEach var="member" items="${ss_showTeamTeamMembers}" >
			 <c:if test="${member.entityType == 'user'}">
		 	   <div class="marginleft3"><ssf:showUser user="${member}" showPresence="${ss_showTeamShowPresence}"/></div>
		 	 </c:if>
			 <c:if test="${member.entityType == 'group'}">
		 	   <div class="marginleft3">${member.title}</div>
		 	 </c:if>
			</c:forEach>
		</div>
</c:if>
<c:if test="${ssConfigJspStyle == 'mobile'}">
  <span class="${ss_showTeamTitleStyle}">${ss_showTeamTeam.title}</span>
	<c:forEach var="member" items="${ss_showTeamTeamMembers}" >
		<div style="margin-left:2em">
		  <c:if test="${member.entityType == 'user'}">
		    <ssf:showUser user="${member}" showPresence="${ss_showTeamShowPresence}"/>
		   </c:if>
		   <c:if test="${member.entityType == 'group'}">
		 	   ${member.title}
		   </c:if>
		</div>
	</c:forEach>
</c:if>
