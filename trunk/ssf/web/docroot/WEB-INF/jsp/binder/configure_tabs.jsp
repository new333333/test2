<%
/**
 * Copyright (c) 1998-2014 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2014 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2014 Novell, Inc. All Rights Reserved.
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
<%@ page import="org.kablink.teaming.web.util.BinderHelper" %>
<jsp:useBean id="ssBinder" type="org.kablink.teaming.domain.Binder" scope="request" />
<%
	boolean isMyFilesStorage = BinderHelper.isBinderMyFilesStorage(ssBinder);
%>
<%@ include file="/WEB-INF/jsp/common/common.jsp" %>
		<div style="text-align: left; margin: 0px 15px 0px 0px; border: 0pt none;" 
		  class="wg-tabs margintop3 marginbottom2">
		  <table>
		    <tr>
		      <% if (!isMyFilesStorage) { %>
			  <td>
				  <div class="wg-tab roundcornerSM ${ss_tab_definitions}">
					  <a href="<ssf:url action="configure_definitions" actionUrl="true"><ssf:param 
						name="binderId" value="${ssBinder.id}"/><ssf:param 
						name="binderType" value="${ssBinder.entityType}"/></ssf:url>"
					  >
					  <c:if test="${ssBinder.entityType != 'folder'}">
					    <span><ssf:nlt tag="binder.configure.definitions.workspace"/></span>
					  </c:if>
					  <c:if test="${ssBinder.entityType == 'folder'}">
					    <span><ssf:nlt tag="binder.configure.definitions.folder"/></span>
					  </c:if>
					  </a>
				  </div>
			  </td>
			  <% } %>
			  <% if (!(ssBinder instanceof org.kablink.teaming.domain.TemplateBinder)) { %>
			  <td>
				  <div class="wg-tab roundcornerSM ${ss_tab_simpleUrls}">
					  <a href="<ssf:url action="configure_definitions" actionUrl="true"><ssf:param 
						name="binderId" value="${ssBinder.id}"/><ssf:param 
						name="binderType" value="${ssBinder.entityType}"/><ssf:param 
						name="operation" value="simpleUrls"/></ssf:url>"
					  ><ssf:nlt tag="binder.configure.definitions.simpleUrls"/></a>
				  </div>
			  </td>
			  <% } %>
		      <% if (!isMyFilesStorage) { %>
			  <c:if test="${ssBinder.entityType == 'folder'}">
			  <% if (!(ssBinder instanceof org.kablink.teaming.domain.TemplateBinder)) { %>
			  <td>
				  <div class="wg-tab roundcornerSM ${ss_tab_changeEntryTypes}">
					  <a href="<ssf:url action="manage_folder_entry_types" actionUrl="true"><ssf:param 
						name="binderId" value="${ssBinder.id}"/></ssf:url>"
					  ><ssf:nlt tag="binder.configure.folderEntryTypes"/></a>
				  </div>
			  </td>
			  <% } %>
			  </c:if>
			  <td>
				  <div class="wg-tab roundcornerSM ${ss_tab_quota}">
					  <a href="<ssf:url action="manage_binder_quota" actionUrl="true"><ssf:param 
						name="binderId" value="${ssBinder.id}"/><ssf:param 
						name="showMenu" value="true"/></ssf:url>"
					  ><ssf:nlt tag="quota.manageQuota"/></a>
				  </div>
			  </td>
			  <td>
				  <div class="wg-tab roundcornerSM ${ss_tab_versionControls}" >
					  <a href="<ssf:url action="manage_version_controls" actionUrl="true"><ssf:param 
						name="binderId" value="${ssBinder.id}"/></ssf:url>"
					  ><ssf:nlt tag="folder.manageFolderVersionControls"/></a>
				  </div>
			  </td>
			  <% } %>
		    </tr>
		  </table>
		</div>
		<div class="ss_clear"></div>

