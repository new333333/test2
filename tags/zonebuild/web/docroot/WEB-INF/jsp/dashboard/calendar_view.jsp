<%
// The dashboard "search" component
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
  //this is used by penlets and portlets
 //Don't include "include.jsp" directly 
%>
<%@ include file="/WEB-INF/jsp/dashboard/common_setup.jsp" %>
<c:set var="prefix" value="${ssComponentId}${ss_namespace}" />

<c:set var="ss_pageNumber" value="0"/>
<c:if test="${ssConfigJspStyle != 'template'}">

	<c:if test="${!empty ssDashboard.beans[componentId].ssFolderList}">
		<table class="ss_style" cellspacing="0" cellpadding="0">
			<c:forEach var="folder" items="${ssDashboard.beans[componentId].ssFolderList}">
				<tr>
				  <td style="padding: 2px;">
				    <a href="javascript: ;"
						onClick="return ss_gotoPermalink('${folder.parentBinder.id}', '${folder.parentBinder.id}', '${folder.parentBinder.entityIdentifier.entityType}', '${ss_namespace}', 'yes');"
						>${folder.parentBinder.title}</a> // 
				    <a href="javascript: ;"
						onClick="return ss_gotoPermalink('${folder.id}', '${folder.id}', 'folder', '${ss_namespace}', 'yes');"
						id="ssDashboardFolderLink${prefix}${folder.id}"><span class="ss_bold">${folder.title}</span></a></td>
				</tr>
			</c:forEach>
		</table>
		<br/>
		
	</c:if>

	<div id="${ss_divId}" width="100%">
		<%@ include file="/WEB-INF/jsp/dashboard/calendar_view2.jsp" %>
	</div>
</c:if>

<c:if test="${ssConfigJspStyle == 'template'}">
	<c:if test="${!empty ssDashboard.beans[componentId].ssFolderList}">
		<table class="ss_style" cellspacing="0" cellpadding="0">
			<c:forEach var="folder" items="${ssDashboard.beans[componentId].ssFolderList}">
				<tr>
				  <td>
				    ${folder.parentBinder.title} // <span class="ss_bold">${folder.title}</span>
				   </td>
				</tr>
			</c:forEach>
		</table>
		<br/>
	</c:if>
</c:if>
