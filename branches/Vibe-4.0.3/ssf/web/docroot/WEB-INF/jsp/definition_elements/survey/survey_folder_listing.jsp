<%
/**
 * Copyright (c) 1998-2012 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2012 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2012 Novell, Inc. All Rights Reserved.
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
<% //View the listing part of a survey folder %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<%@ page import="java.util.Date" %>
<jsp:useBean id="ssSeenMap" type="org.kablink.teaming.domain.SeenMap" scope="request" />
<script type="text/javascript">
var ss_showingFolder = true;
</script>

<table class="ss_surveys_list">
	<tr class="ss_tableheader_style">
		<td></td>
		<td class="ss_nowrap">
<c:if test="${ssConfigJspStyle != 'template'}">
			    <a href="<ssf:url action="${action}" actionUrl="true"><ssf:param 
			    	name="operation" value="save_folder_sort_info"/><ssf:param 
			    	name="binderId" value="${ssFolder.id}"/><ssf:param 
			    	name="ssFolderSortBy" value="_sortTitle"/><c:choose><c:when 
			    	test="${ ssFolderSortBy == '_sortTitle' && ssFolderSortDescend == 'false'}"><ssf:param 
			    	name="ssFolderSortDescend" value="true"/></c:when><c:otherwise><ssf:param 
			    	name="ssFolderSortDescend" value="false"/></c:otherwise></c:choose></ssf:url>"
			
				<c:choose>
				  <c:when test="${ ssFolderSortBy == '_sortTitle' && ssFolderSortDescend == 'false'}">
				  	<ssf:title tag="title.sort.by.column.desc">
				  		<ssf:param name="value" value='<%= NLT.get("survey.title") %>' />
				  	</ssf:title>
				  </c:when>
				  <c:otherwise>
				  	<ssf:title tag="title.sort.by.column.asc">
				  		<ssf:param name="value" value='<%= NLT.get("survey.title") %>' />
				  	</ssf:title>
				  </c:otherwise>
				</c:choose>	
				 >
</c:if>	

			    	<c:if test="${ ssFolderSortBy != '_sortTitle'}">
			 		  <div class="ss_title_menu"><ssf:nlt tag="survey.title"/> </div>
					</c:if>
			    	<c:if test="${ ssFolderSortBy == '_sortTitle' && ssFolderSortDescend == 'true'}">
			 		  <div class="ss_title_menu_sorted"><ssf:nlt tag="survey.title"/> </div>
						<img <ssf:alt tag="title.sorted.by.column.desc"><ssf:param name="value" 
						value='<%= NLT.get("folder.column.Title") %>' /></ssf:alt> border="0" src="<html:imagesPath/>pics/menudown.gif"/>
					</c:if>
					<c:if test="${ ssFolderSortBy == '_sortTitle' && ssFolderSortDescend == 'false'}">
					  <div class="ss_title_menu_sorted"><ssf:nlt tag="survey.title"/> </div>
						<img <ssf:alt tag="title.sorted.by.column.asc"><ssf:param name="value" 
						value='<%= NLT.get("folder.column.Title") %>' /></ssf:alt> border="0" src="<html:imagesPath/>pics/menuup.gif"/>
					</c:if>

<c:if test="${ssConfigJspStyle != 'template'}">
			    </a>
</c:if>
		</td>
		<td>
<c:if test="${ssConfigJspStyle != 'template'}">
			    <a href="<ssf:url action="${action}" actionUrl="true"><ssf:param 
			    	name="operation" value="save_folder_sort_info"/><ssf:param 
			    	name="binderId" value="${ssFolder.id}"/><ssf:param 
			    	name="ssFolderSortBy" value="_principal"/><c:choose><c:when 
			    	test="${ ssFolderSortBy == '_principal' && ssFolderSortDescend == 'false'}"><ssf:param 
			    	name="ssFolderSortDescend" value="true"/></c:when><c:otherwise><ssf:param 
			    	name="ssFolderSortDescend" value="false"/></c:otherwise></c:choose></ssf:url>"
			
				<c:choose>
				  <c:when test="${ ssFolderSortBy == '_principal' && ssFolderSortDescend == 'false'}">
				  	<ssf:title tag="title.sort.by.column.desc">
				  		<ssf:param name="value" value='<%= NLT.get("survey.creator") %>' />
				  	</ssf:title>
				  </c:when>
				  <c:otherwise>
				  	<ssf:title tag="title.sort.by.column.asc">
				  		<ssf:param name="value" value='<%= NLT.get("survey.creator") %>' />
				  	</ssf:title>
				  </c:otherwise>
				</c:choose>	
				 >
</c:if>	

			    	<c:if test="${ ssFolderSortBy != '_principal'}">
			 		  <div class="ss_title_menu"><ssf:nlt tag="survey.creator"/> </div>
					</c:if>
			    	<c:if test="${ ssFolderSortBy == '_principal' && ssFolderSortDescend == 'true'}">
			 		  <div class="ss_title_menu_sorted"><ssf:nlt tag="survey.creator"/> </div>
						<img <ssf:alt tag="title.sorted.by.column.desc"><ssf:param name="value" 
						value='<%= NLT.get("folder.column.Title") %>' /></ssf:alt> border="0" src="<html:imagesPath/>pics/menudown.gif"/>
					</c:if>
					<c:if test="${ ssFolderSortBy == '_principal' && ssFolderSortDescend == 'false'}">
					  <div class="ss_title_menu_sorted"><ssf:nlt tag="survey.creator"/> </div>
						<img <ssf:alt tag="title.sorted.by.column.asc"><ssf:param name="value" 
						value='<%= NLT.get("folder.column.Title") %>' /></ssf:alt> border="0" src="<html:imagesPath/>pics/menuup.gif"/>
					</c:if>

<c:if test="${ssConfigJspStyle != 'template'}">
			    </a>
</c:if>		
		
		</td>
		<td colspan="2">
<c:if test="${ssConfigJspStyle != 'template'}">
			    <a href="<ssf:url action="${action}" actionUrl="true"><ssf:param 
			    	name="operation" value="save_folder_sort_info"/><ssf:param 
			    	name="binderId" value="${ssFolder.id}"/><ssf:param 
			    	name="ssFolderSortBy" value="due_date"/><c:choose><c:when 
			    	test="${ ssFolderSortBy == 'due_date' && ssFolderSortDescend == 'false'}"><ssf:param 
			    	name="ssFolderSortDescend" value="true"/></c:when><c:otherwise><ssf:param 
			    	name="ssFolderSortDescend" value="false"/></c:otherwise></c:choose></ssf:url>"
				
				<c:choose>
				  <c:when test="${ ssFolderSortBy == 'due_date' && ssFolderSortDescend == 'false'}">
				  	<ssf:title tag="title.sort.by.column.desc">
				  		<ssf:param name="value" value='<%= NLT.get("survey.dueDate") %>' />
				  	</ssf:title>
				  </c:when>
				  <c:otherwise>
				  	<ssf:title tag="title.sort.by.column.asc">
				  		<ssf:param name="value" value='<%= NLT.get("survey.dueDate") %>' />
				  	</ssf:title>
				  </c:otherwise>
				</c:choose>	
				 >
</c:if>

			    	<c:if test="${ ssFolderSortBy != 'due_date'}">
			 		  <div class="ss_title_menu"><ssf:nlt tag="survey.dueDate"/> </div>
					</c:if>
			    	<c:if test="${ ssFolderSortBy == 'due_date' && ssFolderSortDescend == 'true'}">
			 		  <div class="ss_title_menu_sorted"><ssf:nlt tag="survey.dueDate"/> </div>
						<img <ssf:alt tag="title.sorted.by.column.desc"><ssf:param name="value" 
						value='<%= NLT.get("folder.column.Title") %>' /></ssf:alt> border="0" src="<html:imagesPath/>pics/menudown.gif"/>
					</c:if>
					<c:if test="${ ssFolderSortBy == 'due_date' && ssFolderSortDescend == 'false'}">
					  <div class="ss_title_menu_sorted"><ssf:nlt tag="survey.dueDate"/> </div>
						<img <ssf:alt tag="title.sorted.by.column.asc"><ssf:param name="value" 
						value='<%= NLT.get("folder.column.Title") %>' /></ssf:alt> border="0" src="<html:imagesPath/>pics/menuup.gif"/>
					</c:if>

<c:if test="${ssConfigJspStyle != 'template'}">
			    </a>
</c:if>		
		</td>
	</tr>
	<c:if test="${empty ssFolderEntries}">
	<tr>
		<td colspan="3"><jsp:include page="/WEB-INF/jsp/forum/view_no_entries.jsp" /></td></tr>
	</c:if>
	<c:if test="${!empty ssFolderEntries}">
		<c:forEach var="entry" items="${ssFolderEntries}" >
			<jsp:useBean id="entry" type="java.util.HashMap" />
			<% boolean overdue = org.kablink.teaming.util.DateComparer.isOverdue((Date)entry.get("due_date")); %>
			<c:set var="overdue" value="<%= overdue %>"/>
			<c:if test="${overdue && entry.status != 'completed'}">
				<c:set var="tdClass" value="class='ss_overdue'" />
			</c:if>
				
			<tr>
				<td class="ss_fixed_TD_unread" valign="middle">
				
					<% if (!ssSeenMap.checkIfSeen(entry)) { %>
					
					  <a id="ss_sunburstDiv${entry._binderId}_${entry._docId}" href="javascript: ;" 
					  title="<ssf:nlt tag="sunburst.click"/>"
					  onClick="ss_hideSunburst('${entry._docId}', '${entry._binderId}');return false;"
					><span 
					  style="display:${ss_sunburstVisibilityHide};"
					  id="ss_sunburstShow${renderResponse.namespace}" 
					  class="ss_fineprint">
						<img src="<html:rootPath/>images/pics/discussion/sunburst.png" align="absmiddle" border="0" <ssf:alt tag="alt.new"/> />
					  </span>
					  </a>
						
					<% } %>
				
				</td>
				<td class="ss_fixed_TD ss_nowrap">

					<% if (!ssSeenMap.checkIfSeen(entry)) { %>
					<span class="ss_entryTitle ss_normalprint ss_bold">	
					<% } %>
										
							<ssf:titleLink action="view_folder_entry" entryId="${entry._docId}" 
							binderId="${entry._binderId}" entityType="${entry._entityType}" 
							namespace="${renderResponse.namespace}">
							
								<ssf:param name="url" useBody="true">
									<ssf:url adapter="true" portletName="ss_forum" folderId="${entry._binderId}" 
									action="view_folder_entry" entryId="${entry._docId}" actionUrl="true" />
								</ssf:param>
							
								<c:out value="${entry.title}" escapeXml="false"/>
							</ssf:titleLink>
					<% if (!ssSeenMap.checkIfSeen(entry)) { %>
					</span>
					<% } %>

				</td>
				<td class="ss_fixed_TD ss_nowrap">
					<ssf:showUser user="${entry._principal}" />
				</td>
				<td ${tdClass} style="white-space: nowrap; border-bottom: solid 1px #D2D5D1; font-size: 12px !important; padding: 4px 10px 2px 3px;">
					<fmt:formatDate timeZone="${ssUser.timeZone.ID}" value="${entry.due_date}" type="both" dateStyle="medium" timeStyle="short" />
					<c:if test="${overdue}">
						&nbsp;<ssf:nlt tag="survey.overdue"/>
					</c:if>		
				</td>
				<td class="ss_fixed_TD ss_nowrap" width="100%">&nbsp;</td>				
			</tr>
		</c:forEach>
	</c:if>
</table>

<c:set var="ss_useDefaultViewEntryPopup" value="1" scope="request"/>
