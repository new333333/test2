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
<% //View the listing part of a milestone folder %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>

<table class="ss_milestones_list">
	<tr>
		<th>
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
				  		<ssf:param name="value" value='<%= NLT.get("milestone.title") %>' />
				  	</ssf:title>
				  </c:when>
				  <c:otherwise>
				  	<ssf:title tag="title.sort.by.column.asc">
				  		<ssf:param name="value" value='<%= NLT.get("milestone.title") %>' />
				  	</ssf:title>
				  </c:otherwise>
				</c:choose>	
				 >
</c:if>	
			      <div class="ss_title_menu"><ssf:nlt tag="milestone.title"/> </div>
			    	<c:if test="${ ssFolderSortBy == '_sortTitle' && ssFolderSortDescend == 'true'}">
						<img <ssf:alt tag="title.sorted.by.column.desc"><ssf:param name="value" 
						value='<%= NLT.get("folder.column.Title") %>' /></ssf:alt> border="0" src="<html:imagesPath/>pics/menudown.gif"/>
					</c:if>
					<c:if test="${ ssFolderSortBy == '_sortTitle' && ssFolderSortDescend == 'false'}">
						<img <ssf:alt tag="title.sorted.by.column.asc"><ssf:param name="value" 
						value='<%= NLT.get("folder.column.Title") %>' /></ssf:alt> border="0" src="<html:imagesPath/>pics/menuup.gif"/>
					</c:if>
<c:if test="${ssConfigJspStyle != 'template'}">
			    </a>
</c:if>		
		</th>
		<th>
<c:if test="${ssConfigJspStyle != 'template'}">
			    <a href="<ssf:url action="${action}" actionUrl="true"><ssf:param 
			    	name="operation" value="save_folder_sort_info"/><ssf:param 
			    	name="binderId" value="${ssFolder.id}"/><ssf:param 
			    	name="ssFolderSortBy" value="responsible"/><c:choose><c:when 
			    	test="${ ssFolderSortBy == 'responsible' && ssFolderSortDescend == 'false'}"><ssf:param 
			    	name="ssFolderSortDescend" value="true"/></c:when><c:otherwise><ssf:param 
			    	name="ssFolderSortDescend" value="false"/></c:otherwise></c:choose></ssf:url>"
			
				<c:choose>
				  <c:when test="${ ssFolderSortBy == 'responsible' && ssFolderSortDescend == 'false'}">
				  	<ssf:title tag="title.sort.by.column.desc">
				  		<ssf:param name="value" value='<%= NLT.get("milestone.responsible") %>' />
				  	</ssf:title>
				  </c:when>
				  <c:otherwise>
				  	<ssf:title tag="title.sort.by.column.asc">
				  		<ssf:param name="value" value='<%= NLT.get("milestone.responsible") %>' />
				  	</ssf:title>
				  </c:otherwise>
				</c:choose>	
				 >
</c:if>	
			      <div class="ss_title_menu"><ssf:nlt tag="milestone.responsible"/> </div>
			    	<c:if test="${ ssFolderSortBy == 'responsible' && ssFolderSortDescend == 'true'}">
						<img <ssf:alt tag="title.sorted.by.column.desc"><ssf:param name="value" 
						value='<%= NLT.get("folder.column.Title") %>' /></ssf:alt> border="0" src="<html:imagesPath/>pics/menudown.gif"/>
					</c:if>
					<c:if test="${ ssFolderSortBy == 'responsible' && ssFolderSortDescend == 'false'}">
						<img <ssf:alt tag="title.sorted.by.column.asc"><ssf:param name="value" 
						value='<%= NLT.get("folder.column.Title") %>' /></ssf:alt> border="0" src="<html:imagesPath/>pics/menuup.gif"/>
					</c:if>
<c:if test="${ssConfigJspStyle != 'template'}">
			    </a>
</c:if>				
		</th>
		<th>
<c:if test="${ssConfigJspStyle != 'template'}">
			    <a href="<ssf:url action="${action}"><ssf:param 
			    	name="operation" value="save_folder_sort_info"/><ssf:param 
			    	name="binderId" value="${ssFolder.id}"/><ssf:param 
			    	name="ssFolderSortBy" value="tasks"/><c:choose><c:when 
			    	test="${ ssFolderSortBy == 'tasks' && ssFolderSortDescend == 'false'}"><ssf:param 
			    	name="ssFolderSortDescend" value="true"/></c:when><c:otherwise><ssf:param 
			    	name="ssFolderSortDescend" value="false"/></c:otherwise></c:choose></ssf:url>"
			
				<c:choose>
				  <c:when test="${ ssFolderSortBy == 'tasks' && ssFolderSortDescend == 'false'}">
				  	<ssf:title tag="title.sort.by.column.desc">
				  		<ssf:param name="value" value='<%= NLT.get("milestone.tasks") %>' />
				  	</ssf:title>
				  </c:when>
				  <c:otherwise>
				  	<ssf:title tag="title.sort.by.column.asc">
				  		<ssf:param name="value" value='<%= NLT.get("milestone.tasks") %>' />
				  	</ssf:title>
				  </c:otherwise>
				</c:choose>	
				 >
</c:if>	
			      <div class="ss_title_menu"><ssf:nlt tag="milestone.tasks"/> </div>
			    	<c:if test="${ ssFolderSortBy == 'tasks' && ssFolderSortDescend == 'true'}">
						<img <ssf:alt tag="title.sorted.by.column.desc"><ssf:param name="value" 
						value='<%= NLT.get("folder.column.Title") %>' /></ssf:alt> border="0" src="<html:imagesPath/>pics/menudown.gif"/>
					</c:if>
					<c:if test="${ ssFolderSortBy == 'tasks' && ssFolderSortDescend == 'false'}">
						<img <ssf:alt tag="title.sorted.by.column.asc"><ssf:param name="value" 
						value='<%= NLT.get("folder.column.Title") %>' /></ssf:alt> border="0" src="<html:imagesPath/>pics/menuup.gif"/>
					</c:if>
<c:if test="${ssConfigJspStyle != 'template'}">
			    </a>
</c:if>			
		</th>
		<th>
<c:if test="${ssConfigJspStyle != 'template'}">
				<a href="<ssf:url action="${action}" actionUrl="true"><ssf:param 
					name="operation" value="save_folder_sort_info"/><ssf:param 
					name="binderId" value="${ssFolder.id}"/><ssf:param 
					name="ssFolderSortBy" value="status"/><c:choose><c:when 
					test="${ ssFolderSortBy == 'status' && ssFolderSortDescend == 'false'}"><ssf:param 
					name="ssFolderSortDescend" value="true"/></c:when><c:otherwise><ssf:param 
					name="ssFolderSortDescend" value="false"/></c:otherwise></c:choose></ssf:url>"
				
				<c:choose>
				  <c:when test="${ ssFolderSortBy == 'status' && ssFolderSortDescend == 'false'}">
				  	<ssf:title tag="title.sort.by.column.desc">
				  		<ssf:param name="value" value='<%= NLT.get("milestone.status") %>' />
				  	</ssf:title>
				  </c:when>
				  <c:otherwise>
				  	<ssf:title tag="title.sort.by.column.asc">
				  		<ssf:param name="value" value='<%= NLT.get("milestone.status") %>' />
				  	</ssf:title>
				  </c:otherwise>
				</c:choose>	
				 >
</c:if>
			      <div class="ss_title_menu"><ssf:nlt tag="milestone.status"/> </div>
			    	<c:if test="${ ssFolderSortBy == 'status' && ssFolderSortDescend == 'true'}">
						<img <ssf:alt tag="title.sorted.by.column.desc"><ssf:param name="value" 
						value='<%= NLT.get("folder.column.Title") %>' /></ssf:alt> border="0" src="<html:imagesPath/>pics/menudown.gif"/>
					</c:if>
					<c:if test="${ ssFolderSortBy == 'status' && ssFolderSortDescend == 'false'}">
						<img <ssf:alt tag="title.sorted.by.column.asc"><ssf:param name="value" 
						value='<%= NLT.get("folder.column.Title") %>' /></ssf:alt> border="0" src="<html:imagesPath/>pics/menuup.gif"/>
					</c:if>
<c:if test="${ssConfigJspStyle != 'template'}">
			    </a>
</c:if>		
		</th>
		<th>
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
				  		<ssf:param name="value" value='<%= NLT.get("milestone.dueDate") %>' />
				  	</ssf:title>
				  </c:when>
				  <c:otherwise>
				  	<ssf:title tag="title.sort.by.column.asc">
				  		<ssf:param name="value" value='<%= NLT.get("milestone.dueDate") %>' />
				  	</ssf:title>
				  </c:otherwise>
				</c:choose>	
				 >
</c:if>
			      <div class="ss_title_menu"><ssf:nlt tag="milestone.dueDate"/> </div>
			    	<c:if test="${ ssFolderSortBy == 'due_date' && ssFolderSortDescend == 'true'}">
						<img <ssf:alt tag="title.sorted.by.column.desc"><ssf:param name="value" 
						value='<%= NLT.get("folder.column.Title") %>' /></ssf:alt> border="0" src="<html:imagesPath/>pics/menudown.gif"/>
					</c:if>
					<c:if test="${ ssFolderSortBy == 'due_date' && ssFolderSortDescend == 'false'}">
						<img <ssf:alt tag="title.sorted.by.column.asc"><ssf:param name="value" 
						value='<%= NLT.get("folder.column.Title") %>' /></ssf:alt> border="0" src="<html:imagesPath/>pics/menuup.gif"/>
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
				
   					<% if (!ssSeenMap.checkIfSeen(entry)) { %>
				    
					  <a id="ss_sunburstDiv${entry._binderId}_${entry._docId}" href="javascript: ;" 
					  title="<ssf:nlt tag="sunburst.click"/>"
					  onClick="ss_hideSunburst('${entry._docId}', '${entry._binderId}');return false;"
					><span 
					  style="display:${ss_sunburstVisibilityHide};"
					  id="ss_sunburstShow${renderResponse.namespace}" 
					  class="ss_fineprint">
					  	<img src="<html:rootPath/>images/pics/discussion/sunburst.png" align="text-bottom" border="0" <ssf:alt tag="alt.new"/> />&nbsp;
					  </span>
					  </a>
					    
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
				</span>
			</td>
			<td>
				<ul class="ss_nobullet">
				<c:forEach var="principal" items='<%= com.sitescape.team.util.ResolveIds.getPrincipals(entry.get("responsible")) %>' >
					<li><ssf:showUser user="${principal}" /></li>
				</c:forEach>
				</ul>
			</td>
			<td>
				<ul class="ss_nobullet">
					<c:forEach var="selection" items='<%= com.sitescape.team.util.ResolveIds.getBinderTitlesAndIcons(entry.get("tasks")) %>' varStatus="status">
						<li><a href="<ssf:url crawlable="true" adapter="true" portletName="ss_forum"
			  				folderId="${selection.key}" 
			  				action="view_folder_listing">
			  				<ssf:param name="binderId" value="${selection.key}"/>
			  				<ssf:param name="newTab" value="1"/>
			  				</ssf:url>"><c:out value="${selection.value.title}" escapeXml="false"/></a>
			  				
			  				<c:choose>
				  				<c:when test="${selection.value.deleted}">
				  					<span class="ss_fineprint ss_light"><ssf:nlt tag="milestone.folder.deleted"/></span>
				  				</c:when>
				  				<c:otherwise>
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
					  			</c:otherwise>
					  		</c:choose>
			  			</li>
					</c:forEach>
				</ul>
			</td>
			<td>
				<%
					java.util.Map statusCaptions = com.sitescape.team.web.util.DefinitionHelper.findSelectboxSelectionsAsMap("status", (String)entry.get("_commandDef"));
					String caption = (String)statusCaptions.get(entry.get("status"));
				%>
				<c:if test="${!empty caption}">
					<ssf:nlt tag="<%= caption %>"/>
				</c:if>
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

<c:set var="ss_useDefaultViewEntryPopup" value="1" scope="request"/>
