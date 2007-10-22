<%
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
%>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<%@ page import="com.sitescape.util.BrowserSniffer" %>
<%
boolean isIE = BrowserSniffer.is_ie(request);
%>
<ssf:sidebarPanel id="ss_folder_sidebar" divClass="ss_blog_sidebar"
                  title="__definition_default_folder_blog" initOpen="true" sticky="true">
	  <ssHelpSpot helpId="workspaces_folders/misc_tools/blog_controls" offsetX="0" 
         <c:if test="<%= isIE %>">
           offsetY="25"
         </c:if>
	    title="<ssf:nlt tag="helpSpot.blogControls"/>"></ssHelpSpot>

		<div class="ss_blog_sidebar_hole">
			<ssf:ifnotaccessible>
				<div id="ss_blog_sidebar_date_popup"></div>
		 		<form name="ss_blog_sidebar_date_form" style="display:inline;">
					<ssf:datepicker id="ss_blog_sidebar_date" 
		            	calendarDivId="ss_blog_sidebar_date_popup"
		            	formName="ss_blog_sidebar_date_form"
		            	immediateMode="true" initDate="${ssFolderEndDate}"
						callbackRoutine="ss_blog_sidebar_date_callback" />
		      	</form>
			</ssf:ifnotaccessible>
        </div>
         
		<div class="ss_blog_sidebar_subhead"><ssf:nlt tag="blog.archives"/></div>
        <div class="ss_blog_sidebar_box">		
		<table>
		<c:forEach var="monthYear" items="${ssBlogMonthHits}">
		  <tr>
		  <td><a href="${ssBlogMonthUrls[monthYear.key]}" 
		  class="<c:if test="${!empty selectedYearMonth && selectedYearMonth == ssBlogMonthTitles[monthYear.key]}">ss_bold</c:if>"
		  <ssf:title tag="title.entries.archived.on">
		  	<ssf:param name="value" value="${ssBlogMonthTitles[monthYear.key]}" />
		  	<ssf:param name="value" value="${ssBinder.title}" />
		  </ssf:title> >
		  <c:out value="${ssBlogMonthTitles[monthYear.key]}"/></a></td>
		  <td align="right">(<c:out value="${monthYear.value}"/>)</td>
		  </tr>
		</c:forEach>
		</table>
		</div>
        <c:if test="${!empty ssFolderEntryCommunityTags}"> 	
		<div class="ss_blog_sidebar_subhead"><ssf:nlt tag="tags.community"/></div>
        <div class="ss_blog_sidebar_box">				
		   <c:forEach var="tag" items="${ssFolderEntryCommunityTags}">
			   	<a href="<portlet:actionURL windowState="maximized" portletMode="view"><portlet:param 
					name="action" value="view_folder_listing"/><portlet:param 
					name="binderId" value="${ssBinder.id}"/><portlet:param 
					name="cTag" value="${tag.ssTag}"/></portlet:actionURL>" 
					class="ss_displaytag ${tag.searchResultsRatingCSS} <c:if test="${!empty cTag && cTag == tag.ssTag}">ss_bold</c:if>"
					  <ssf:title tag="title.search.entries.in.folder.for.community.tag">
					  	<ssf:param name="value" value="${tag.ssTag}" />
					  </ssf:title>
					>${tag.ssTag}</a>&nbsp;&nbsp;
		   </c:forEach>
		</div>
		</c:if>
        <c:if test="${!empty ssFolderEntryPersonalTags}"> 
		<div class="ss_blog_sidebar_subhead"><ssf:nlt tag="tags.personal"/></div>
        <div class="ss_blog_sidebar_box">		
		   <c:forEach var="tag" items="${ssFolderEntryPersonalTags}">
		
		   	<a href="<portlet:actionURL windowState="maximized" portletMode="view"><portlet:param 
				name="action" value="view_folder_listing"/><portlet:param 
				name="binderId" value="${ssBinder.id}"/><portlet:param 
				name="pTag" value="${tag.ssTag}"/></portlet:actionURL>" 
				class="ss_displaytag ${tag.searchResultsRatingCSS} <c:if test="${!empty pTag && pTag == tag.ssTag}">ss_bold</c:if>"
				  <ssf:title tag="title.search.entries.in.folder.for.personal.tag">
				  	<ssf:param name="value" value="${tag.ssTag}" />
				  </ssf:title>
				>${tag.ssTag}</a>&nbsp;&nbsp;
		   </c:forEach>
		</div>
		</c:if>
</ssf:sidebarPanel>
<%@ include file="/WEB-INF/jsp/sidebars/folder_tags.jsp" %>
	  