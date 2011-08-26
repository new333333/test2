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
<%@ include file="/WEB-INF/jsp/common/common.jsp" %>
<c:if test="${!empty ssWorkspaces}">
  <div class="folders">    
	<c:forEach var="workspace" items="${ssWorkspaces}" >
		<a href="<ssf:url adapter="true" portletName="ss_forum" 
				folderId="${workspace.id}" 
				action="__ajax_mobile" operation="mobile_show_workspace" actionUrl="false" />">
	      <div class="folder-item folder-item-ws">
		    <img class="margin5r" src="<html:rootPath/>images/mobile/workspace_top.png" align="absmiddle" />
		  <c:if test="${empty workspace.title}">
		    (<ssf:nlt tag="workspace.noTitle"/>)
		  </c:if>
		  <c:out value="${workspace.title}" escapeXml="true"/>
		  </div>
		</a>
	</c:forEach>
  </div>
</c:if>
	
<c:if test="${!empty ssFolders}">
  <div class="folders">
	<c:forEach var="folder" items="${ssFolders}" >
		<a href="<ssf:url adapter="true" portletName="ss_forum" 
				folderId="${folder.id}" 
				action="__ajax_mobile" operation="mobile_show_folder" actionUrl="false" />">
	      <div class="folder-item">
            <img class="margin5r" src="<html:rootPath/>images/mobile/folder.png" align="absmiddle" />
		  <c:if test="${empty folder.title}">
		    (<ssf:nlt tag="folder.noTitle"/>)
		  </c:if>
		  <c:out value="${folder.title}" escapeXml="true"/>
		  </div>
		</a>
	</c:forEach>
  </div>
</c:if>
<c:set var="ss_mobileBinderListShown" value="true" scope="request"/>
