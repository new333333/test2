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

<c:if test="${!empty ssDashboard.beans[componentId].ssBinder}">
<c:set var="folder" value="${ssDashboard.beans[componentId].ssBinder}"/>

<table class="ss_style" cellspacing="0" cellpadding="0">
<tr>
  <td>
<c:if test="${ssConfigJspStyle != 'template'}">
    <a href="javascript: ;"
		onclick="return ss_gotoPermalink('${folder.parentBinder.id}', '${folder.parentBinder.id}', '${folder.parentBinder.entityIdentifier.entityType}', '${ss_namespace}', 'yes');"
		>${folder.parentBinder.title}</a> // 
    <a href="javascript: ;"
		onclick="return ss_gotoPermalink('${folder.id}', '${folder.id}', 'folder', '${ss_namespace}', 'yes');"
		><span class="ss_bold">${folder.title}</span></a>
</c:if>
<c:if test="${ssConfigJspStyle == 'template'}">
    ${folder.parentBinder.title} // <span class="ss_bold">${folder.title}</span>
</c:if>
</td></tr>
</table>
<br/>

</c:if>


<div id="${ss_divId}">

<c:set var="wikiEntry" value="${ssDashboard.beans[componentId].wikiHomepageEntry}" />
<c:if test="${empty wikiEntry}">
  <span class="ss_light">--<ssf:nlt tag="entry.noWikiHomepageSet"/>--</span>
</c:if>

<c:if test="${!empty wikiEntry}">
<div>
	<ssf:titleLink 
			entryId="${wikiEntry.id}" binderId="${wikiEntry.parentFolder.id}" 
				entityType="${wikiEntry.entityType}" 
				namespace="${ss_namespace}" seenStyle="class=\"ss_entryTitle ss_normalprint\""
				isDashboard="yes" dashboardType="${ssDashboard.scope}">
				
					<ssf:param name="url" useBody="true">
						<ssf:url adapter="true" portletName="ss_forum" folderId="${wikiEntry.parentFolder.id}" 
						action="view_folder_entry" entryId="${wikiEntry.id}" actionUrl="true" />
					</ssf:param>
				
					<c:out value="${wikiEntry.title}" escapeXml="false"/>
	</ssf:titleLink>

</div>

<c:if test="${!empty wikiEntry.description}">
<div class="ss_entryContent ss_entryDescription">
 <span><ssf:markup entity="${wikiEntry}">${wikiEntry.description.text}</ssf:markup></span>
 <div class="ss_clear"></div>
</div>
</c:if> 

</c:if> 
</div>
