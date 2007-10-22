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
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
<%
	String instanceCount = (String) request.getAttribute("instanceCount");
	String binderId = (String) request.getAttribute("binderId");
	String formElement = (String) request.getAttribute("formElement");
	String appendAll = ((Boolean) request.getAttribute("appendAll")).toString();
	String checkOnLoad = ((Boolean) request.getAttribute("checkOnLoad")).toString();
%>
<c:set var="iCount" value="<%= instanceCount %>"/>
<c:set var="binderId" value="<%= binderId %>"/>
<c:set var="formElement" value="<%= formElement %>"/>
<c:set var="appendAll" value="<%= appendAll %>"/>
<c:set var="checkOnLoad" value="<%= checkOnLoad %>"/>
<c:set var="prefix" value="${iCount}" />

<script type="text/javascript" src="<html:rootPath/>js/jsp/tag_jsps/team/team_members.js"></script>

<div class="ss_teamMembersPane">
	<span id="ss_teamMembersLoadLink_${prefix}" onclick="if (window.ss_loadTeamMembersList) ss_loadTeamMembersList('<ssf:url adapter="true" portletName="ss_forum" action="__ajax_request" actionUrl="true"><ssf:param name="operation" value="get_team_members" /><ssf:param name="binderId" value="${binderId}" /></ssf:url>', '${prefix}' <c:if test="${appendAll == 'true' || checkOnLoad == 'true'}">, true</c:if>);"
		onmouseover="this.style.cursor='pointer'; " onmouseout="this.style.cursor='default'; ">
		<img <ssf:alt tag="alt.expand"/> id="ss_teamIcon_${prefix}" src="<html:imagesPath/>pics/sym_s_expand.gif" />
		<span class="ss_bold"><ssf:nlt tag="sendMail.team" /></span>
	</span>

	<div id="ss_teamMembersList_${prefix}" class="ss_teamMembersList ss_style" style="display: block;"></div>

	<img src="<html:imagesPath/>pics/1pix.gif" <ssf:alt/>
	  onload="ss_setTeamMembersVariables('${prefix}', '${formElement}'); <c:if test="${appendAll}">$('ss_teamMembersLoadLink_${prefix}').onclick(); </c:if>" />

</div>