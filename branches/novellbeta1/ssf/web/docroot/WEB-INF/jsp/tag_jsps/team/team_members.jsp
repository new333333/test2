<%
/**
 * The contents of this file are governed by the terms of your license
 * with SiteScape, Inc., which includes disclaimers of warranties and
 * limitations on liability. You may not use this file except in accordance
 * with the terms of that license. See the license for the specific language
 * governing your rights and limitations under the license.
 *
 * Copyright (c) 2007 SiteScape, Inc.
 *
 */
%>
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
<%
	String instanceCount = ((Integer) request.getAttribute("instanceCount")).toString();
	String binderId = (String) request.getAttribute("binderId");
	String formElement = (String) request.getAttribute("formElement");
	String appendAll = ((Boolean) request.getAttribute("appendAll")).toString();
%>
<c:set var="iCount" value="<%= instanceCount %>"/>
<c:set var="binderId" value="<%= binderId %>"/>
<c:set var="formElement" value="<%= formElement %>"/>
<c:set var="appendAll" value="<%= appendAll %>"/>
<c:set var="prefix" value="${iCount}" />

<script type="text/javascript" src="<html:rootPath/>js/jsp/tag_jsps/team/team_members.js"></script>

<div class="ss_teamMembersPane">
	<span id="ss_teamMembersLoadLink_${prefix}" onclick="if (window.ss_loadTeamMembersList) ss_loadTeamMembersList('<ssf:url adapter="true" portletName="ss_forum" action="__ajax_request" actionUrl="true"><ssf:param name="operation" value="get_team_members" /><ssf:param name="binderId" value="${binderId}" /></ssf:url>', ${prefix}<c:if test="${appendAll == 'true'}">, ${appendAll}</c:if>);"
		onmouseover="this.style.cursor='pointer'; " onmouseout="this.style.cursor='default'; ">
		<img id="ss_teamIcon_${prefix}" src="<html:imagesPath/>pics/sym_s_expand.gif" />
		<span class="ss_bold"><ssf:nlt tag="sendMail.team" /></span>
	</span>

	<div id="ss_teamMembersList_${prefix}" class="ss_teamMembersList ss_style" style="display: block;"></div>

	<img src="<html:imagesPath/>pics/1pix.gif" onload="ss_setTeamMembersVariables('${prefix}', '${formElement}'); <c:if test="${appendAll}">$('ss_teamMembersLoadLink_${prefix}').onclick(); </c:if>" />

</div>