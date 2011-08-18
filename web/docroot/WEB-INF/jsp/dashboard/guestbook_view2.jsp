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

<c:set var="hitCount" value="0"/>

<div class="ss_blog">
<c:set var="summaryWordCount" value="20"/>
<c:if test="${!empty ssDashboard.dashboard.components[componentId].data.summaryWordCount}">
	<c:set var="summaryWordCount" value="${ssDashboard.dashboard.components[componentId].data.summaryWordCount}"/>
</c:if>

<c:forEach var="fileEntry" items="${ssDashboard.beans[componentId].ssSearchFormData.searchResults}" >

  <c:set var="hitCount" value="${hitCount + 1}"/>


  <table class="ss_guestbook" cellspacing="0" cellpadding="0" width="100%">
	  <tr> 	
			<td class="ss_miniBusinessCard" style="padding-bottom: 5px;" valign="top">
				<ssf:miniBusinessCard user="${fileEntry._principal}"/> 
			</td>		 	
			<td class="ss_guestbookContainer" valign="top">
			
		<ssf:titleLink 
			entryId="${fileEntry._docId}" binderId="${fileEntry._binderId}" 
				entityType="folderEntry" namespace="${ss_namespace}" seenStyle="class=\"ss_entryTitle ss_normalprint\""
				isDashboard="yes" dashboardType="${ssDashboard.scope}">				
					<ssf:param name="url" useBody="true">
						<ssf:url adapter="true" portletName="ss_forum" folderId="${fileEntry._binderId}" 
						action="view_folder_entry" entryId="${fileEntry._docId}" actionUrl="true" />
					</ssf:param>				
					<c:if test="${empty fileEntry.title}">
				    	${fileEntry._principal.title} <ssf:nlt tag="guestbook.author.wrote"/>: 
				    </c:if>
					<c:out value="${fileEntry.title}" escapeXml="false"/>
		</ssf:titleLink>
		
				<span class="ss_att_meta"><fmt:formatDate timeZone="${ssUser.timeZone.ID}"
				      value="${fileEntry._modificationDate}" type="both" 
					  timeStyle="short" dateStyle="short" /></span>
				
				<c:if test="${!empty fileEntry._desc}">
				<div class="ss_blockquote_watermark"></div>
				<div class="ss_blockquote_watermark_content">
					<span>
						<ssf:textFormat formatAction="limitedDescription" textMaxWords="${summaryWordCount}">
								<ssf:markup search="${fileEntry}">${fileEntry._desc}</ssf:markup>
						</ssf:textFormat>
					  </span>
					  <div class="ss_clear"></div>
				</div>
				</c:if>
			</td>					 	
		</tr>
	</table>

</c:forEach>
</div>

<div>
  <table width="100%">
   <tr>
    <td valign="top">
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
	  [<ssf:nlt tag="dashboard.noEntriesFound"/>]
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
	      <a onclick="ss_moreDashboardSearchResults('${binderId}', '${ss_pageNumber - 1}', '${ss_pageSize}', '${ss_namespace}', '${ss_divId}', '${componentId}', 'guestbook'); return false;"
	        href="javascript:;" >&lt;&lt;&lt;&nbsp;<ssf:nlt tag="general.previousPage"/></a>&nbsp;&nbsp;&nbsp;
	    </span>
	  </c:if>
	  <c:if test="${(ss_pageNumber * ss_pageSize + hitCount) < ssDashboard.beans[componentId].ssSearchFormData.ssEntrySearchCount}">
	    <span>&nbsp;&nbsp;
	      <a onclick="ss_moreDashboardSearchResults('${binderId}', '${ss_pageNumber + 1}', '${ss_pageSize}', '${ss_namespace}', '${ss_divId}', '${componentId}', 'guestbook'); return false;"
	        href="javascript:;" ><ssf:nlt tag="general.nextPage"/>&nbsp;&gt;&gt;&gt;</a>
	    </span>
	  </c:if>
    </td>
   </tr>
  </table>
</div>
