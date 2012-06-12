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
<c:set var="summaryWordCount" value="30"/>
<c:if test="${!empty ssDashboard.dashboard.components[componentId].data.summaryWordCount}">
	<c:set var="summaryWordCount" value="${ssDashboard.dashboard.components[componentId].data.summaryWordCount}"/>
</c:if>

<c:forEach var="fileEntry" items="${ssDashboard.beans[componentId].ssSearchFormData.searchResults}" >
  <c:set var="hitCount" value="${hitCount + 1}"/>
  
<div class="ss_blog_summary_title">
  <table cellspacing="0" cellpadding="0" width="100%">
  <tr>
  <td valign="top"><span class="ss_blog_summary_title_text">
    <a href="<ssf:permalink search="${fileEntry}"/>"
			onclick="return ss_gotoPermalink('${fileEntry._binderId}','${fileEntry._docId}', '${fileEntry._entityType}', '', 'yes');">

     <c:if test="${empty fileEntry.title}">
    <span class="ss_fineprint"><i>(<ssf:nlt tag="entry.noTitle" />)</i></span>
    </c:if>
    <span class="ss_bold ss_underline"><c:out value="${fileEntry.title}"/></span></a>
	</td>
	<td align="right" nowrap valign="top"><span class="ss_italic ss_smallprint">
	<c:if test="${ssDashboard.scope != 'portlet'}">
	    <c:if test="${fileEntry._entityType == 'folderEntry' || 
      		fileEntry._entityType == 'reply'}">
		<ssf:menu titleId="ss_folderName_${hitCount}_${componentId}_${ss_namespace}/>" 
		    menuClass="ss_actions_bar5 ss_actions_bar_submenu">
		  <ssf:param name="title" useBody="true">
		      <c:if test="${empty ssDashboard.beans[componentId].ssSearchFormData.ssBinderData[fileEntry._binderId].iconName}">
		        <img border="0" src="<html:imagesPath/>icons/folder.gif" <ssf:alt/> />
		      </c:if>
		      <c:if test="${!empty ssDashboard.beans[componentId].ssSearchFormData.ssBinderData[fileEntry._binderId].iconName}">
				<c:set var="binderIconName" value="${ssDashboard.beans[componentId].ssSearchFormData.ssBinderData[fileEntry._binderId].iconName}"/>
				<jsp:useBean id="binderIconName" type="java.lang.String" />
				<%
					if (binderIconName != null && binderIconName.startsWith("/")) {
						binderIconName = binderIconName.substring(1);
					}
				%>
		        <img border="0" <ssf:alt/>
		          src="<html:imagesPath/><%=  binderIconName %>" />
		      </c:if>
		  </ssf:param>

			<ssf:ifnotaccessible>
				<ul class="ss_actions_bar2 ss_actions_bar_submenu" style="width:250px;">
					<li><a href="<ssf:url adapter="true" portletName="ss_forum" 
						    action="view_permalink"
						    binderId="${fileEntry._binderId}">
						    <ssf:param name="entityType" value="folder" />
							</ssf:url>" 
							onclick="return ss_gotoPermalink('${fileEntry._binderId}', '${fileEntry._binderId}', 'folder', '', 'yes');">					
						  ${ssDashboard.beans[componentId].ssSearchFormData.ssBinderData[fileEntry._binderId].title}
						</a>
					</li>
				</ul>
			</ssf:ifnotaccessible>
			
			<ssf:ifaccessible>
				<a href="<ssf:url adapter="true" portletName="ss_forum" 
				    action="view_permalink"
				    binderId="${fileEntry._binderId}">
				    <ssf:param name="entityType" value="folder" />
					</ssf:url>" 
					onclick="return ss_gotoPermalink('${fileEntry._binderId}', '${fileEntry._binderId}', 'folder', '', 'yes');">					
				  ${ssDashboard.beans[componentId].ssSearchFormData.ssBinderData[fileEntry._binderId].title}
				</a>
			</ssf:ifaccessible>

		</ssf:menu>
     </c:if>
    &nbsp;&nbsp;
    </c:if>
    <c:out value="${fileEntry._principal.title}"/>,&nbsp;&nbsp;
	<fmt:formatDate timeZone="${ssUser.timeZone.ID}"
      value="${fileEntry._modificationDate}" type="both" 
	  timeStyle="short" dateStyle="short" /></span>&nbsp;&nbsp;
	</td>
	</tr>
	</table>
</div>

<div style="padding-bottom:10px;">

    <div class="ss_smallprint ss_indent_medium">  
 		<ssf:textFormat formatAction="limitedDescription" textMaxWords="${summaryWordCount}">
 	     <ssf:markup search="${fileEntry}" >${fileEntry._desc}</ssf:markup>
 		</ssf:textFormat>
 		<div class="ss_clear"></div>
    </div>
</div>
</c:forEach>

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
	  [<ssf:nlt tag="dashboard.noEntriesFound"/>]
	</span>
</c:if>
	</td>
	<c:if test="${ssDashboard.scope != 'portlet'}">
		<c:set var="binderId" value="${ssBinder.id}"/>
	</c:if>
	<c:if test="${ssDashboard.scope == 'portlet'}">
		<c:set var="binderId" value="${ssDashboardPortlet.id}"/>
	</c:if>
	
	<td align="right">
	  <c:if test="${ss_pageNumber > 0}">
	    <span>
	      <a onclick="ss_moreDashboardSearchResults('${binderId}', '${ss_pageNumber - 1}', '${ss_pageSize}', '${ss_namespace}', '${ss_divId}', '${componentId}', 'blog'); return false;"
	        href="javascript:;" >&lt;&lt;&lt;&nbsp;<ssf:nlt tag="general.previousPage"/></a>&nbsp;&nbsp;&nbsp;
	    </span>
	  </c:if>
	  <c:if test="${(ss_pageNumber * ss_pageSize + hitCount) < ssDashboard.beans[componentId].ssSearchFormData.ssEntrySearchCount}">
	    <span>&nbsp;&nbsp;
	      <a onclick="ss_moreDashboardSearchResults('${binderId}', '${ss_pageNumber + 1}', '${ss_pageSize}', '${ss_namespace}', '${ss_divId}', '${componentId}', 'blog'); return false;"
	        href="javascript:;" ><ssf:nlt tag="general.nextPage"/>&nbsp;&gt;&gt;&gt;</a>
	    </span>
	  </c:if>
    </td>
   </tr>
  </table>
</div>
