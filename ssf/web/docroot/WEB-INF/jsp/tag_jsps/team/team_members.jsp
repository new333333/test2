<%
/**
 * Copyright (c) 1998-2013 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2013 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2013 Novell, Inc. All Rights Reserved.
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
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
<%@ page import="org.kablink.teaming.util.Utils" %>

<c:set var="prefix" value="${instanceCount}" />
<script type="text/javascript">
// Setup text strings for team_members.js
// Team memmbers text
ss_noTeamMembersText = "<ssf:nlt tag='teamMembers.noUsers'/>";
</script>
<script type="text/javascript" src="<html:rootPath/>js/jsp/tag_jsps/team/team_members.js"></script>
<script type="text/javascript">
function ss_loadTeamMembers_${prefix}() {
	ss_setTeamMembersVariables('${prefix}', '${formElement}');
	<c:if test="${appendAll}">
	var obj = document.getElementById("ss_teamMembersLoadLink_${prefix}");
	obj.onclick();
	</c:if>
}
ss_createOnLoadObj("ss_loadTeamMembers_${prefix}", ss_loadTeamMembers_${prefix});
</script>

<% if (!(Utils.checkIfFilr())) { %>
<div class="ss_teamMembersPane">
	<span id="ss_teamMembersLoadLink_${prefix}" 
		onclick="if (window.ss_loadTeamMembersList) ss_loadTeamMembersList('${binderId}', '${prefix}' <c:if test="${appendAll == 'true' || checkOnLoad == 'true'}">, true</c:if>);"
		onmouseover="this.style.cursor='pointer'; " 
		onmouseout="this.style.cursor='default'; ">
		<img <ssf:alt tag="alt.expand"/> id="ss_teamIcon_${prefix}" src="<html:imagesPath/>pics/sym_s_expand.gif" />
		<span class="ss_bold"><ssf:nlt tag="sendMail.team" /></span>
	</span>

	<div id="ss_teamMembersList_${prefix}" class="ss_teamMembersList ss_style" style="display: block;"></div>

</div>
<% } %>
