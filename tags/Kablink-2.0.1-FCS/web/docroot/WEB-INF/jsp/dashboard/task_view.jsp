<%
// The dashboard "search" component
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
  //this is used by penlets and portlets
 //Don't include "include.jsp" directly 
%>
<%@ include file="/WEB-INF/jsp/dashboard/common_setup.jsp" %>
<c:set var="prefix" value="${ssComponentId}${ss_namespace}" />
<c:set var="hitCount" value="${ssDashboard.beans[componentId].ssSearchFormData.ssEntrySearchRecordReturned}"/>

<c:set var="ss_pageNumber" value="0"/>
<c:if test="${ssConfigJspStyle != 'template'}">

	<c:if test="${!empty ssDashboard.beans[componentId].ssFolderList}">
		<table>
			<tr>
				<td><img border="0" src="<html:imagesPath/><c:choose><c:when test="${hitCount > 0}">pics/flip_up16H.gif</c:when><c:otherwise>pics/flip_down16H.gif</c:otherwise></c:choose>" onclick="ss_showHideTaskList('${prefix}_ss_dashboard_folder_list', this, true, 'ss_navigator_box');" <ssf:alt/> /></td><td></td>
			</tr>
			<tr><td></td>
				<td><table class="ss_style" cellspacing="0" cellpadding="0" id="${prefix}_ss_dashboard_folder_list" style="<c:choose><c:when test="${hitCount > 0}">display: none; visibility: hidden; </c:when><c:otherwise>display: table; visibility: visible; </c:otherwise></c:choose>">
						<c:forEach var="folder" items="${ssDashboard.beans[componentId].ssFolderList}">
							<tr>
							  <td>
							    <a href="javascript: ;"
									onclick="return ss_gotoPermalink('${folder.parentBinder.id}', '${folder.parentBinder.id}', '${folder.parentBinder.entityIdentifier.entityType}', '${ss_namespace}', 'yes');"
									>${folder.parentBinder.title}</a> // 
							    <a href="javascript: ;"
									onclick="return ss_gotoPermalink('${folder.id}', '${folder.id}', 'folder', '${ss_namespace}', 'yes');"><span class="ss_bold">${folder.title}</span></a></td>
							</tr>
						</c:forEach>
					</table></td>
			</tr>
		</table>
	
	</c:if>
	
	<div id="${ss_divId}" width="100%">
	<%@ include file="/WEB-INF/jsp/dashboard/task_view2.jsp" %>
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
	</c:if>
</c:if>
