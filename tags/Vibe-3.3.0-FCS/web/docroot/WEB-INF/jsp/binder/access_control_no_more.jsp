<%
/**
 * Copyright (c) 1998-2010 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2010 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2010 Novell, Inc. All Rights Reserved.
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
<c:set var="ss_windowTitle" value='<%= NLT.get("access.noMore") %>' scope="request"/>
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
<body class="ss_style_body tundra">
<div class="ss_portlet diag_modal2">
	<ssf:form titleTag="access.configure">
		<div class="ss_style ss_form" style="margin:0px; padding:10px 16px 10px 10px;">
			<div>
			  <c:if test="${ss_accessControlException}">
			    <span class="ss_bold ss_errorLabel"><ssf:nlt tag="access.noLonger"/></span>
			    <br/>
			    <c:if test="${ssDefinitionEntry.entityType != 'folderEntry'}">
			      <br/>
			      <input type="button" class="ss_submit" 
			        onClick="self.window.close();"
					value="<ssf:nlt tag="button.close"/>"/>
				</c:if>
			  </c:if>
			  <c:if test="${!ss_accessControlException}">
			    <span class="ss_bold ss_errorLabel"><ssf:nlt tag="access.noLongerManage"/></span>
			    <br/>
			    <br/>
			    <c:if test="${ssDefinitionEntry.entityType == 'folderEntry'}">
			      <input type="button" class="ss_submit" 
			        onClick="self.location.href='<ssf:url adapter="true" portletName="ss_forum" 
					  folderId="${ssDefinitionEntry.parentFolder.id}" 
					  action="view_folder_entry" 
					  entryId="${ssDefinitionEntry.id}"/>';return false;"
					value="<ssf:nlt tag="button.close"/>"/>
				</c:if>
			    <c:if test="${ssDefinitionEntry.entityType != 'folderEntry'}">
			      <input type="button" class="ss_submit" 
			        onClick="self.window.close();"
					value="<ssf:nlt tag="button.close"/>"/>
				</c:if>
			  </c:if>
			</div>
		</div>
	</ssf:form>
</div>

</body>
</html>
