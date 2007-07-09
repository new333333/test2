<%
/**
 * The contents of this file are governed by the terms of your license
 * with SiteScape, Inc., which includes disclaimers of warranties and
 * limitations on liability. You may not use this file except in accordance
 * with the terms of that license. See the license for the specific language
 * governing your rights and limitations under the license.
 *
 * Copyright (c) 2007 SiteScape, Inc.
 *
 */
%>
<% //View the listing part of a milestone folder %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>

<table class="ss_milestones_list">
	<tr>
		<th>
<c:if test="${ssConfigJspStyle != 'template'}">
			    <a href="<portlet:actionURL windowState="maximized" portletMode="view">
					<portlet:param name="action" value="${action}"/>
					<portlet:param name="operation" value="save_folder_sort_info"/>
					<portlet:param name="binderId" value="${ssFolder.id}"/>
					<portlet:param name="ssFolderSortBy" value="_sortTitle"/>
					<c:choose>
					  <c:when test="${ ssFolderSortBy == '_sortTitle' && ssFolderSortDescend == 'false'}">
					  	<portlet:param name="ssFolderSortDescend" value="true"/>
					  </c:when>
					  <c:otherwise>
					  	<portlet:param name="ssFolderSortDescend" value="false"/>
					  </c:otherwise>
					</c:choose>
					<portlet:param name="tabId" value="${tabId}"/>
				</portlet:actionURL>"
			
				<c:choose>
				  <c:when test="${ ssFolderSortBy == '_sortTitle' && ssFolderSortDescend == 'false'}">
				  	<ssf:title tag="title.sort.by.column.desc">
				  		<ssf:param name="value" value="<%= NLT.get("milestone.title") %>" />
				  	</ssf:title>
				  </c:when>
				  <c:otherwise>
				  	<ssf:title tag="title.sort.by.column.asc">
				  		<ssf:param name="value" value="<%= NLT.get("milestone.title") %>" />
				  	</ssf:title>
				  </c:otherwise>
				</c:choose>	
				 >
</c:if>	
			      <div class="ss_title_menu"><ssf:nlt tag="milestone.title"/> </div>
			    	<c:if test="${ ssFolderSortBy == '_sortTitle' && ssFolderSortDescend == 'true'}">
						<img <ssf:alt tag="title.sorted.by.column.desc"><ssf:param name="value" value="<%= NLT.get("folder.column.Title") %>" /></ssf:alt> border="0" src="<html:imagesPath/>pics/menudown.gif"/>
					</c:if>
					<c:if test="${ ssFolderSortBy == '_sortTitle' && ssFolderSortDescend == 'false'}">
						<img <ssf:alt tag="title.sorted.by.column.asc"><ssf:param name="value" value="<%= NLT.get("folder.column.Title") %>" /></ssf:alt> border="0" src="<html:imagesPath/>pics/menuup.gif"/>
					</c:if>
<c:if test="${ssConfigJspStyle != 'template'}">
			    </a>
</c:if>		
		</th>
		<th><ssf:nlt tag="milestone.responsible"/></th>
		<th><ssf:nlt tag="milestone.tasks"/></th>
		<th>
<c:if test="${ssConfigJspStyle != 'template'}">
				<a href="<portlet:actionURL windowState="maximized" portletMode="view">
					<portlet:param name="action" value="${action}"/>
					<portlet:param name="operation" value="save_folder_sort_info"/>
					<portlet:param name="binderId" value="${ssFolder.id}"/>
					<portlet:param name="ssFolderSortBy" value="status"/>
					<c:choose>
					  <c:when test="${ ssFolderSortBy == 'status' && ssFolderSortDescend == 'false'}">
					  	<portlet:param name="ssFolderSortDescend" value="true"/>
					  </c:when>
					  <c:otherwise>
					  	<portlet:param name="ssFolderSortDescend" value="false"/>
					  </c:otherwise>
					</c:choose>
					<portlet:param name="tabId" value="${tabId}"/>
				</portlet:actionURL>"
				
				<c:choose>
				  <c:when test="${ ssFolderSortBy == 'status' && ssFolderSortDescend == 'false'}">
				  	<ssf:title tag="title.sort.by.column.desc">
				  		<ssf:param name="value" value="<%= NLT.get("milestone.status") %>" />
				  	</ssf:title>
				  </c:when>
				  <c:otherwise>
				  	<ssf:title tag="title.sort.by.column.asc">
				  		<ssf:param name="value" value="<%= NLT.get("milestone.status") %>" />
				  	</ssf:title>
				  </c:otherwise>
				</c:choose>	
				 >
</c:if>
			      <div class="ss_title_menu"><ssf:nlt tag="milestone.status"/> </div>
			    	<c:if test="${ ssFolderSortBy == 'status' && ssFolderSortDescend == 'true'}">
						<img <ssf:alt tag="title.sorted.by.column.desc"><ssf:param name="value" value="<%= NLT.get("folder.column.Title") %>" /></ssf:alt> border="0" src="<html:imagesPath/>pics/menudown.gif"/>
					</c:if>
					<c:if test="${ ssFolderSortBy == 'status' && ssFolderSortDescend == 'false'}">
						<img <ssf:alt tag="title.sorted.by.column.asc"><ssf:param name="value" value="<%= NLT.get("folder.column.Title") %>" /></ssf:alt> border="0" src="<html:imagesPath/>pics/menuup.gif"/>
					</c:if>
<c:if test="${ssConfigJspStyle != 'template'}">
			    </a>
</c:if>		
		</th>
		<th>
<c:if test="${ssConfigJspStyle != 'template'}">
			    <a href="<portlet:actionURL windowState="maximized" portletMode="view">
					<portlet:param name="action" value="${action}"/>
					<portlet:param name="operation" value="save_folder_sort_info"/>
					<portlet:param name="binderId" value="${ssFolder.id}"/>
					<portlet:param name="ssFolderSortBy" value="start_end#EndDate"/>
					<c:choose>
					  <c:when test="${ ssFolderSortBy == 'start_end#EndDate' && ssFolderSortDescend == 'false'}">
					  	<portlet:param name="ssFolderSortDescend" value="true"/>
					  </c:when>
					  <c:otherwise>
					  	<portlet:param name="ssFolderSortDescend" value="false"/>
					  </c:otherwise>
					</c:choose>
					<portlet:param name="tabId" value="${tabId}"/>
				</portlet:actionURL>"
				
				<c:choose>
				  <c:when test="${ ssFolderSortBy == 'start_end#EndDate' && ssFolderSortDescend == 'false'}">
				  	<ssf:title tag="title.sort.by.column.desc">
				  		<ssf:param name="value" value="<%= NLT.get("milestone.dueDate") %>" />
				  	</ssf:title>
				  </c:when>
				  <c:otherwise>
				  	<ssf:title tag="title.sort.by.column.asc">
				  		<ssf:param name="value" value="<%= NLT.get("milestone.dueDate") %>" />
				  	</ssf:title>
				  </c:otherwise>
				</c:choose>	
				 >
</c:if>
			      <div class="ss_title_menu"><ssf:nlt tag="milestone.dueDate"/> </div>
			    	<c:if test="${ ssFolderSortBy == 'start_end#EndDate' && ssFolderSortDescend == 'true'}">
						<img <ssf:alt tag="title.sorted.by.column.desc"><ssf:param name="value" value="<%= NLT.get("folder.column.Title") %>" /></ssf:alt> border="0" src="<html:imagesPath/>pics/menudown.gif"/>
					</c:if>
					<c:if test="${ ssFolderSortBy == 'start_end#EndDate' && ssFolderSortDescend == 'false'}">
						<img <ssf:alt tag="title.sorted.by.column.asc"><ssf:param name="value" value="<%= NLT.get("folder.column.Title") %>" /></ssf:alt> border="0" src="<html:imagesPath/>pics/menuup.gif"/>
					</c:if>
<c:if test="${ssConfigJspStyle != 'template'}">
			    </a>
</c:if>		
		</th>
	</tr>
	<c:forEach var="entry" items="${ssFolderEntries}" >
		<jsp:useBean id="entry" type="java.util.HashMap" />
		
		<c:set var="tdClass" value="" />
		<c:if test="${entry.status == 'completed'}">
			<c:set var="tdClass" value="class='ss_completed'" />
		</c:if>
		<%
			boolean overdue = com.sitescape.team.util.DateComparer.isOverdue((Date)entry.get("due_date"));
		%>
		<c:set var="overdue" value="<%= overdue %>"/>
		<c:if test="${overdue && entry.status != 'completed'}">
			<c:set var="tdClass" value="class='ss_overdue'" />
		</c:if>
		
		<tr>
			<td>
				<span class="ss_entryTitle ss_normalprint">
					<ssf:menuLink displayDiv="false" action="view_folder_entry" adapter="true" entryId="${entry._docId}" 
					binderId="${entry._binderId}" entityType="${entry._entityType}" 
					imageId='menuimg_${entry._docId}_${renderResponse.namespace}' 
				    menuDivId="ss_emd_${renderResponse.namespace}"
					linkMenuObjIdx="${renderResponse.namespace}" 
					namespace="${renderResponse.namespace}"
					entryCallbackRoutine="${showEntryCallbackRoutine}">
					
						<ssf:param name="url" useBody="true">
							<ssf:url adapter="true" portletName="ss_forum" folderId="${entry._binderId}" 
							action="view_folder_entry" entryId="${entry._docId}" actionUrl="true" />
						</ssf:param>
					
						<c:out value="${entry.title}" escapeXml="false"/>
					</ssf:menuLink>
				</span>
			</td>
			<td>
				<ul class="ss_nobullet">
				<c:forEach var="principal" items="<%= com.sitescape.team.util.ResolveIds.getPrincipals(entry.get("responsible")) %>" >
					<li><ssf:showUser user="${principal}" /></li>
				</c:forEach>
				</ul>
			</td>
			<td>
				<ul class="ss_nobullet">
					<c:forEach var="selection" items="<%= com.sitescape.team.util.ResolveIds.getBinderTitlesAndIcons(entry.get("tasks")) %>" varStatus="status">
						<li><a href="<ssf:url 
			  				folderId="${selection.key}" 
			  				action="view_folder_listing">
			  				<ssf:param name="binderId" value="${selection.key}"/>
			  				<ssf:param name="newTab" value="1"/>
			  				</ssf:url>"><c:out value="${selection.value.title}" escapeXml="false"/></a>
			  				
			  				<c:if test="${!empty selection.key &&
			  								!empty ssFolders &&
			  								!empty ssFolders[selection.key] &&
			  								!empty ssFolders[selection.key].customAttributes['statistics'] &&
			  								!empty ssFolders[selection.key].customAttributes['statistics'].value &&
			  								!empty ssFolders[selection.key].customAttributes['statistics'].value.value}">		
				  				<c:forEach var="definition" items="${ssFolders[selection.key].customAttributes['statistics'].value.value}">
				  					<c:if test="${!empty definition.value}">
					  					<c:forEach var="attribute" items="${definition.value}">
					  						<c:if test="${!empty attribute.key && !empty attribute.value}">
						  						<c:if test="${attribute.key == 'status'}">
						  							<ssf:drawStatistic statistic="${attribute.value}" style="shortColoredBar ss_statusBar" showLabel="false" showLegend="false"/>
						  						</c:if>
					  						</c:if>
					  					</c:forEach>
				  					</c:if>
				  				</c:forEach>
			  				</c:if>
			  			</li>
					</c:forEach>
				</ul>
			</td>
			<td ${tdClass}>
				<%
					java.util.Map statusCaptions = com.sitescape.team.web.util.DefinitionHelper.findSelectboxSelectionsAsMap("status", (String)entry.get("_commandDef"));
					String caption = (String)statusCaptions.get(entry.get("status"));
				%>
				<ssf:nlt tag="<%= caption %>"/>
			</td>
			<td ${tdClass}>
				<fmt:formatDate timeZone="${ssUser.timeZone.ID}" value="${entry.due_date}" type="both" dateStyle="medium" timeStyle="short" />
				<c:if test="${overdue && entry.status != 'completed'}">
					<ssf:nlt tag="milestone.overdue"/>
				</c:if>
			</td>
		</tr>
	</c:forEach>
</table>

<ssf:menuLink displayDiv="true" menuDivId="ss_emd_${renderResponse.namespace}" linkMenuObjIdx="${renderResponse.namespace}" 
	namespace="${renderResponse.namespace}">
</ssf:menuLink>