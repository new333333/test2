<%@ page language="java" pageEncoding="ISO-8859-1"%>
<%@ include file="/WEB-INF/jsp/common/common.jsp" %>
<taconite-root xml:space="preserve">
    <taconite-replace-children contextNodeID="${ss_divId}" parseInBrowser="true">
		<ul style="padding-left: 25px; margin-top: 3px;">
			<c:forEach var="teamMember" items="${ssTeamMembers}">
				<li><input type="checkbox" name="${formElementName}" id="${prefix}_team_member_id_${teamMember.id}" value="${teamMember.id}" onChange="adjustTeamMembersCheck${prefix}();"/><label for="${prefix}_team_member_id_${teamMember.id}">${teamMember.title}</label></li>
			</c:forEach>
		</ul>
    </taconite-replace-children>
</taconite-root>
