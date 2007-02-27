<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
<%
	String instanceCount = ((Integer) request.getAttribute("instanceCount")).toString();
	String binderId = (String) request.getAttribute("binderId");
	String clickRoutine = (String) request.getAttribute("clickRoutine");
%>
<c:set var="iCount" value="<%= instanceCount %>"/>
<c:set var="binderId" value="<%= binderId %>"/>
<c:set var="clickRoutine" value="<%= clickRoutine %>"/>
<c:set var="prefix" value="${iCount}" />

<script type="text/javascript" src="<html:rootPath/>js/jsp/tag_jsps/team/team_members.js"></script>

<div class="ss_teamMenu">
	<img id="ss_teamIcon_${prefix}" src="<html:imagesPath/>icons/group.gif" onmouseover="if (window.ss_displayTeamMembersMenu) ss_displayTeamMembersMenu('<ssf:url adapter="true" portletName="ss_forum" action="__ajax_request" actionUrl="true"><ssf:param name="operation" value="get_team_members_count" /><ssf:param name="binderId" value="${binderId}" /></ssf:url>', '${prefix}');" />
	<div id="ss_teamOptions_${prefix}" class="ss_teamOptionsPane ss_style" style="visibility: hidden; display: none; position: absolute;">
		<ul id="ss_teamOptionsListUL_${prefix}">
			<li class="ss_pasteAllUsers" onmouseover="this.style.backgroundColor='#333'; this.style.color='#FFF'; this.style.cursor='pointer';" onmouseout="this.style.backgroundColor='#FFF'; this.style.color='#333'; this.style.cursor='default';" onclick="if (window.ss_loadTeamMembers) ss_loadTeamMembers('${prefix}', ss_addAllUsersFromTeam);"><ssf:nlt tag="teamMembers.addAll"/> (<strong id="teamMembersAmount_${prefix}">${teamMembersCount}</strong>)</li>	
		</ul>
	</div>
	
	<img src="<html:imagesPath/>pics/1pix.gif" onload="ss_setTeamMembersVariables('${prefix}', '<ssf:url adapter="true" portletName="ss_forum" action="__ajax_request" actionUrl="true"><ssf:param name="operation" value="get_team_members" /><ssf:param name="binderId" value="${binderId}" /></ssf:url>', '${clickRoutine}');" />
    		
</div>