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
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ include file="/WEB-INF/jsp/common/common.jsp" %>

<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet" %>
<%@ taglib prefix="portletadapter" uri="http://www.sitescape.com/tags-portletadapter" %>

<portletadapter:defineObjects1/>
<ssf:ifadapter><portletadapter:defineObjects2/></ssf:ifadapter>
<ssf:ifnotadapter><portlet:defineObjects/></ssf:ifnotadapter>
<c:if test="${ssConfigJspStyle != 'mobile'}">
	<img border="0" src="<html:imagesPath/>pics/group_icon_small.gif" />
		<span class="${ss_showGroupTitleStyle}"><c:out value="${ss_showGroupGroup.title}" /></span>
	<a href="javascript: //"
	onclick="ss_toggleShowDiv('ss_show_group_${ss_showGroupInstanceCount}'); return false;" class="ss_fineprint"><ssf:nlt tag="showGroup.group.members"><ssf:param name="value" value="${fn:length(ss_showGroupGroupMembers)}"/></ssf:nlt></a>
	<div id="ss_show_group_${ss_showGroupInstanceCount}" style="display: none;">
  		<ul>
			<c:forEach var="member" items="${ss_showGroupGroupMembers}" >
		 	 <li><ssf:showUser user="${member}" showPresence="${ss_showGroupShowPresence}"/></li>
			</c:forEach>
  		</ul>
	</div>
</c:if>
<c:if test="${ssConfigJspStyle == 'mobile'}">
  <span class="${ss_showGroupTitleStyle}">${ss_showGroupGroup.title}</span>
	<ul>
		<c:forEach var="member" items="${ss_showGroupGroupMembers}" >
			<li><ssf:showUser user="${member}" showPresence="${ss_showGroupShowPresence}"/></li>
		</c:forEach>
	</ul>
</c:if>
