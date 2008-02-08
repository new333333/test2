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
    <a href="<ssf:url adapter="true" portletName="ss_forum" 
		    action="view_permalink"
		    binderId="${fileEntry._binderId}"
		    entryId="${fileEntry._docId}">
		    <ssf:param name="entityType" value="${fileEntry._entityType}" />
    	    <ssf:param name="newTab" value="1"/>
			</ssf:url>"
			onClick="return ss_gotoPermalink('${fileEntry._binderId}','${fileEntry._docId}', '${fileEntry._entityType}', '', 'yes');">

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
		    menuClass="ss_actions_bar2 ss_actions_bar_submenu">
		  <ssf:param name="title" useBody="true">
		      <c:if test="${empty ssDashboard.beans[componentId].ssSearchFormData.ssBinderData[fileEntry._binderId].iconName}">
		        <img border="0" src="<html:imagesPath/>icons/folder.gif" <ssf:alt/> />
		      </c:if>
		      <c:if test="${!empty ssDashboard.beans[componentId].ssSearchFormData.ssBinderData[fileEntry._binderId].iconName}">
		        <img border="0" <ssf:alt/>
		          src="<html:imagesPath/>${ssDashboard.beans[componentId].ssSearchFormData.ssBinderData[fileEntry._binderId].iconName}" />
		      </c:if>
		  </ssf:param>

			<ssf:ifnotaccessible>
				<ul class="ss_actions_bar2 ss_actions_bar_submenu" style="width:250px;">
					<li><a href="<ssf:url adapter="true" portletName="ss_forum" 
						    action="view_permalink"
						    binderId="${fileEntry._binderId}">
						    <ssf:param name="entityType" value="folder" />
				    	    <ssf:param name="newTab" value="1"/>
							</ssf:url>" 
							onClick="return ss_gotoPermalink('${fileEntry._binderId}', '${fileEntry._binderId}', 'folder', '', 'yes');">					
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
		    	    <ssf:param name="newTab" value="1"/>
					</ssf:url>" 
					onClick="return ss_gotoPermalink('${fileEntry._binderId}', '${fileEntry._binderId}', 'folder', '', 'yes');">					
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
      <ssf:markup binderId="${fileEntry._binderId}" entryId="${fileEntry._docId}">
 		<ssf:textFormat formatAction="limitedDescription" textMaxWords="${summaryWordCount}">
					${fileEntry._desc}
		</ssf:textFormat>
      </ssf:markup>
    </div>
<%
	}
%>  
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
	      <a onClick="ss_moreDashboardSearchResults('${binderId}', '${ss_pageNumber - 1}', '${ss_pageSize}', '${ss_divId}', '${componentId}', 'blog'); return false;"
	        href="javascript:;" >&lt;&lt;&lt;&nbsp;<ssf:nlt tag="general.previousPage"/></a>&nbsp;&nbsp;&nbsp;
	    </span>
	  </c:if>
	  <c:if test="${(ss_pageNumber * ss_pageSize + hitCount) < ssDashboard.beans[componentId].ssSearchFormData.ssEntrySearchCount}">
	    <span>&nbsp;&nbsp;
	      <a onClick="ss_moreDashboardSearchResults('${binderId}', '${ss_pageNumber + 1}', '${ss_pageSize}', '${ss_divId}', '${componentId}', 'blog'); return false;"
	        href="javascript:;" ><ssf:nlt tag="general.nextPage"/>&nbsp;&gt;&gt;&gt;</a>
	    </span>
	  </c:if>
    </td>
   </tr>
  </table>
</div>
