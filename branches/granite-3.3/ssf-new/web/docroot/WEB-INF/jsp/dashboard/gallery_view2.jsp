<%
// The dashboard "gallery search" component
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
 //Don't include "include.jsp" directly 
%>
<%@ include file="/WEB-INF/jsp/dashboard/common_setup.jsp" %>

<table width="99%"><tr><td>
<c:choose>
<c:when test="${ssDashboard.dashboard.components[componentId].data.galleryImageSize == 'small'}">
<div class="ss_thumbnail_gallery ss_thumbnail_small"> 
</c:when>
<c:otherwise>
<div class="ss_thumbnail_gallery ss_thumbnail_big"> 
</c:otherwise>
</c:choose>
<c:set var="hitCount" value="0"/>
<c:set var="resultCount" value="0"/>
<c:forEach var="fileEntry" items="${ssDashboard.beans[componentId].ssSearchFormData.searchResults}" >
  <c:set var="resultCount" value="${resultCount + 1}"/>
  <c:if test="${not empty fileEntry._fileID}">

  <c:set var="hitCount" value="${hitCount + 1}"/>
    <div>
	  <a href="<ssf:fileUrl search="${fileEntry}"/>"
		onclick="return ss_openUrlInWindow(this, '_blank');">
    <img <ssf:alt text="${fileEntry.title}"/> border="0" src="<ssf:fileUrl webPath="readThumbnail" search="${fileEntry}"/>"/></a>
    <br/>
  	<c:choose>
  	<c:when test="${fileEntry._entityType == 'folderEntry'}">
	<ssf:titleLink 
			entryId="${fileEntry._docId}" binderId="${fileEntry._binderId}" 
				entityType="folderEntry" namespace="${ss_namespace}" 
				isDashboard="yes" dashboardType="${ssDashboard.scope}">				
					<ssf:param name="url" useBody="true">
						<ssf:url adapter="true" portletName="ss_forum" folderId="${fileEntry._binderId}" 
						action="view_folder_entry" entryId="${fileEntry._docId}" actionUrl="true" />
					</ssf:param>				
					<c:out value="${fileEntry.title}" escapeXml="false"/>
	</ssf:titleLink>

    </c:when>
    <c:when test="${fileEntry._entityType == 'user'}">
    
			<ssf:titleLink 
				entryId="${fileEntry._docId}" binderId="${fileEntry._binderId}" 
				entityType="user" namespace="${ss_namespace}" 
				useBinderFunction="permalink" isDashboard="yes" dashboardType="${ssDashboard.scope}">											
						<ssf:param name="url" useBody="true">
								<ssf:permalink search="${fileEntry}"/>
						</ssf:param>
					<c:out value="${fileEntry.title}" escapeXml="false"/>
			</ssf:titleLink>
    </c:when>
    <c:when test="${fileEntry._entityType == 'folder' || fileEntry._entityType == 'workspace' || fileEntry._entityType == 'profiles'}">
			<ssf:titleLink 
					entryId="${fileEntry._docId}" binderId="${fileEntry._docId}" 
					entityType="${fileEntry._entityType}"  
					namespace="${ss_namespace}"  
					useBinderFunction="permalink" isDashboard="yes" dashboardType="${ssDashboard.scope}">
											
						<ssf:param name="url" useBody="true">
								<ssf:permalink search="${fileEntry}"/>
						</ssf:param>
					<c:out value="${fileEntry.title}" escapeXml="false"/>
			</ssf:titleLink>
   </c:when>
 	</c:choose>
    </div>
 </c:if>

</c:forEach>
</div>
</table>

<div>
  <table width="100%">
   <tr>
    <td>
<c:if test="${hitCount > 0}">
      <span class="ss_light ss_fineprint">
	    [<ssf:nlt tag="folder.Results">
	    <ssf:param name="value" value="${ss_pageNumber * ss_pageSize + 1}"/>
	    <ssf:param name="value" value="${ss_pageNumber * ss_pageSize + hitCount}"/>
	    <ssf:param name="value" value="${ssDashboard.beans[componentId].ssSearchFormData.ssEntrySearchCount}"/>
	    </ssf:nlt>]
	  </span>
</c:if>
<c:if test="${hitCount == 0}">
    <span class="ss_light ss_fineprint">
	  [<ssf:nlt tag="dashboard.gallery.noneFound"/>]
	</span>
</c:if>
	</td>
	<td align="right">
	<c:if test="${ssDashboard.scope != 'portlet'}">
		<c:set var="binderId" value="${ssBinder.id}"/>
	</c:if>
	<c:if test="${ssDashboard.scope == 'portlet'}">
		<c:set var="binderId" value="${ssDashboardPortlet.id}"/>
	</c:if>
	  <c:if test="${ss_pageNumber > 0}">
	    <span>
	      <a onclick="ss_moreDashboardSearchResults('${binderId}', '${ss_pageNumber - 1}', '${ss_pageSize}', '${ss_namespace}', '${ss_divId}', '${componentId}', 'gallery'); return false;"
	        href="javascript:;" >&lt;&lt;&lt;&nbsp;<ssf:nlt tag="general.previousPage"/></a>&nbsp;&nbsp;&nbsp;
	    </span>
	  </c:if>
	  <c:if test="${(empty ss_pageNumber || ss_pageNumber == 0) && ssDashboard.beans[componentId].ssSearchFormData.ssEntrySearchCount > ss_pageSize}">
	    <span class="ss_light">&lt;&lt;&lt;&nbsp;<ssf:nlt tag="general.previousPage"/>&nbsp;&nbsp;&nbsp;</span>
	  </c:if>
	  <c:if test="${(ss_pageNumber * ss_pageSize + resultCount) < ssDashboard.beans[componentId].ssSearchFormData.ssEntrySearchCount}">
	    <span>&nbsp;&nbsp;
	      <a onclick="ss_moreDashboardSearchResults('${binderId}', '${ss_pageNumber + 1}', '${ss_pageSize}', '${ss_namespace}', '${ss_divId}', '${componentId}', 'gallery'); return false;"
	        href="javascript:;" ><ssf:nlt tag="general.nextPage"/>&nbsp;&gt;&gt;&gt;</a>
	    </span>
	  </c:if>
	  <c:if test="${hitCount > 0 && (ss_pageNumber * ss_pageSize + resultCount) >= ssDashboard.beans[componentId].ssSearchFormData.ssEntrySearchCount}">
	    <span class="ss_light">&nbsp;&nbsp;<ssf:nlt tag="general.nextPage"/>&nbsp;&gt;&gt;&gt;</span>
	  </c:if>
    </td>
   </tr>
  </table>
</div>
