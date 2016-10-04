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
<% // Delete file version %>
<%@ page import="org.kablink.teaming.util.NLT" %>
<%@ include file="/WEB-INF/jsp/common/common.jsp" %>
<c:set var="ss_windowTitle" value='<%= NLT.get("file.deleteVersion") %>' scope="request"/>
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
<body class="tundra">
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>

<div class="ss_style ss_portlet diag_modal">

	<form name="form1" id="form1" class="ss_style ss_form" method="post" 
	  action="<ssf:url
				adapter="true" 
				portletName="ss_forum" 
				action="modify_file" 
				actionUrl="true" 
				><ssf:param name="entityId" value="${ss_entity.id}"/><ssf:param 
				name="entityType" value="${ss_entity.entityType}"/><ssf:param 
				name="fileId" value="${ss_fileAttachment.id}"/><ssf:param 
				name="operation" value="delete"/></ssf:url>"
	>
	<div>
		<h2><ssf:nlt tag="file.deleteVersion"/></h2>
		<div class="ss_bold"><ssf:nlt tag="file.deleteConfirm"/></div>
		<div><ssf:nlt tag="file.deleteConfirm2"/></div>


		<table cellpadding="3" class="margintop3">
			<tr>
				<td>
				  <span><ssf:nlt tag="file.name"/></span>
				</td>
				<td>
				  <span class="ss_bold">${ss_fileAttachment.fileItem.name}</span>
				</td>
			</tr>
		
			<tr>
				<td>
				  <span><ssf:nlt tag="file.version"/></span>
				</td>
				<td>
				  <span class="ss_bold">${ss_fileAttachment.fileVersion}</span>
				</td>
			</tr>
		
			<tr>
				<td>
				  <span><ssf:nlt tag="file.date"/></span>
				</td>
				<td>
		  			<span class="ss_bold"><fmt:formatDate timeZone="${ssUser.timeZone.ID}"
					value="${ss_fileAttachment.creation.date}" type="both" 
					timeStyle="short" dateStyle="medium" /></span>
				</td>
			</tr>
		</table>
	
	</div>

	<div class="teamingDlgBoxFooter">
		<input type="submit" class="ss_submit" name="okBtn" value="<ssf:nlt tag="button.ok" />"/>
		<input type="submit" class="ss_submit" name="closeBtn" value="<ssf:nlt tag="button.cancel" text="Cancel"/>"
			onClick="ss_cancelButtonCloseWindow();return false;"/>
	</div>		
	</form>
</div>
</body>
</html>
