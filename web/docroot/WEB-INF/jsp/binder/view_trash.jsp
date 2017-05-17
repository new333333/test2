<%
/**
 * Copyright (c) 1998-2010 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2010 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2010 Novell, Inc. All Rights Reserved.
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
<% /* Trash Listing.                                             */ %>
<% /* -------------                                              */ %>
<% /* Note:  The implementation of this JSP was heavily based on */ %>
<% /*        folder_view_common2.jsp.                            */ %>

<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<jsp:useBean id="ssBinder"               type="org.kablink.teaming.domain.Binder" scope="request" />
<jsp:useBean id="ssUserFolderProperties" type="java.util.Map"                     scope="request" />
<c:if test="${empty ss_breadcrumbsShowIdRoutine}">
  <c:set var="ss_breadcrumbsShowIdRoutine" value="ss_treeShowIdNoWS" scope="request" />
</c:if>
<%
	List folderEntriesSeen = new ArrayList();
	Map ssFolderColumns = ((Map) ssUserFolderProperties.get("userFolderColumns"));
	if (null == ssFolderColumns) {
		ssFolderColumns = ((Map) ssBinder.getProperty("folderColumns"));
	}
	if (null == ssFolderColumns) {
		ssFolderColumns = new java.util.HashMap();
		ssFolderColumns.put("title",    "title");
		ssFolderColumns.put("date",     "date");
		ssFolderColumns.put("author",   "author");
		ssFolderColumns.put("location", "location");
	}
	
	
	String ssFolderTableHeight = "";
	if ((null != ssUserFolderProperties) && ssUserFolderProperties.containsKey("folderEntryHeight")) {
		ssFolderTableHeight = ((String) ssUserFolderProperties.get("folderEntryHeight"));
	}
	if ((null == ssFolderTableHeight) || ssFolderTableHeight.equals("") || ssFolderTableHeight.equals("0")) {
		ssFolderTableHeight = "400";
	}
%>
<c:set var="ssFolderColumns" value="<%= ssFolderColumns %>" scope="request"/>

<script type="text/javascript" src="<html:rootPath/>js/binder/ss_trash.js"></script>
<script type="text/javascript">
	<% /* Store the namespace and binder ID for use in */ %>
	<% /* ss_trash.js.                                 */ %> 
	var g_namespace = "${ss_namespace}";
	var	g_binderId  = "${ssBinder.id}";
	var g_pageCount	= Number(<c:out value="${ssPageCount}" />);

	
	<% /* Store information about the trashed entries that we're */ %>
	<% /* dealing with on this page.                             */ %> 
	var	g_trashEntries = new Array();
	var	trashEntry;
	<c:forEach var="entry3" items="${ssFolderEntries}">
		trashEntry = new SSTrashEntry();
		trashEntry.m_docId = Number(${entry3._docId});
		trashEntry.m_docType = "${entry3._docType}";
		trashEntry.m_entityType = "${entry3._entityType}";
		trashEntry.m_title = "<ssf:escapeJavaScript>${entry3.title}</ssf:escapeJavaScript>";
		<c:if test="${entry3._docType == 'entry'}">
			trashEntry.m_locationBinderId = Number(${entry3._binderId});
		</c:if>
		<c:if test="${entry3._docType != 'entry'}">
			trashEntry.m_locationBinderId = Number(${entry3._binderParentId});
		</c:if>
		g_trashEntries[g_trashEntries.length] = trashEntry;
	</c:forEach>
	var g_trashEntriesCount = g_trashEntries.length;


	<% /* Store the localized error messages that might need to */ %>
	<% /* display while running this page.                      */ %>
	var g_trashStrings = new Array();
	g_trashStrings["trash.error.NoItemsSelected"]							= "<ssf:escapeJavaScript><ssf:nlt tag='trash.error.NoItemsSelected'                          /></ssf:escapeJavaScript>";
	g_trashStrings["trash.confirm.Purge"]									= "<ssf:escapeJavaScript><ssf:nlt tag='trash.confirm.Purge'                                  /></ssf:escapeJavaScript>";
	g_trashStrings["trash.confirm.Purge.DeleteSourceOnMirroredSubFolders"]	= "<ssf:escapeJavaScript><ssf:nlt tag='trash.confirm.Purge.DeleteSourceOnMirroredSubFolders' /></ssf:escapeJavaScript>";
	g_trashStrings["trash.confirm.PurgeAll"]								= "<ssf:escapeJavaScript><ssf:nlt tag='trash.confirm.PurgeAll'                               /></ssf:escapeJavaScript>";
	g_trashStrings["trash.confirm.PurgeAll.WithSelections"]					= "<ssf:escapeJavaScript><ssf:nlt tag='trash.confirm.PurgeAll.WithSelections'                /></ssf:escapeJavaScript>";
	g_trashStrings["trash.confirm.RestoreAll.WithSelections"]				= "<ssf:escapeJavaScript><ssf:nlt tag='trash.confirm.RestoreAll.WithSelections'              /></ssf:escapeJavaScript>";
</script>

<div id="ss_trashDiv${ss_namespace}" align="center">
	<div id="ss_folder_wrap">
		<div id="ss_folder_type_file" class="ss_style_color">
			<!-- Trash:  Title Bar -->
			<div id="ss_topic_box">
				<div id="ss_topic_box_h1">
					<ul class="ss_horizontal ss_nobullet">
						<li><div class="ss_treeWidget"><ssf:nlt tag="trash.titlebar.Caption"/>:&nbsp;&nbsp;</div></li>
						<c:if test="${trashMode == 'workspace'}" >
							<li>
								<div class="ss_treeWidget">
									<a href="<ssf:url crawlable="true" adapter="true" portletName="ss_forum" binderId="${ssBinder.id}" action="view_ws_listing"/>">
										<span>${ssBinder.title}</span>
				    		  		</a>
								</div>
							</li>
						</c:if>

						<c:if test="${trashMode == 'folder'}" >
							<c:set var="parentBinder" value="${ssBinder}"/>
							<c:set var="action" value="view_ws_listing"/>
							<jsp:useBean id="parentBinder" type="org.kablink.teaming.domain.Binder" />
							<%
								Stack parentTree = new Stack();
								while (parentBinder != null) {
									parentTree.push(parentBinder);
									if (parentBinder.getEntityType().equals(org.kablink.teaming.domain.EntityIdentifier.EntityType.workspace)) {
										break;
									}
									parentBinder = parentBinder.getParentBinder();
								}
								while (!parentTree.empty()) {
									Binder nextBinder = ((Binder) parentTree.pop());
							%>
								<c:set var="nextBinder" value="<%= nextBinder %>"/>
								<li>
								<c:if test="${nextBinder.entityType == 'folder' && !empty ssNavigationLinkTree[nextBinder.id]}">
									<table cellpadding="0" cellspacing="0">
										<tr>
											<td valign="top">
												<ssf:tree
													treeName="ss_binderTitle${nextBinder.id}${renderResponse.namespace}" 
													treeDocument="${ssNavigationLinkTree[nextBinder.id]}" 
													topId="${nextBinder.id}"
													rootOpen="false" 
													showImages="false"
													showIdRoutine="${ss_breadcrumbsShowIdRoutine}" 
													namespace="${renderResponse.namespace}"
													highlightNode="${nextBinder.id}"
													titleClass="" />
											</td>
											<%
												if (!parentTree.empty()) {
											%>
													<td valign="top">
														<div class="ss_topic_box_h1 ss_treeWidget">&nbsp;&gt;&gt;&nbsp;&nbsp;</div>
													</td>
											<%
												}
											%>
										</tr>
									</table>
								</c:if>
								<c:if test="${nextBinder.entityType != 'folder' || empty ssNavigationLinkTree[nextBinder.id]}">
									<div class="ss_treeWidget">
										<a href="<ssf:url
												crawlable="true"
												adapter="true"
												portletName="ss_forum"
												folderId="${nextBinder.id}" 
												action="${action}"/>">
											<c:if test="${empty nextBinder.title}">
												<span class="ss_light">--<ssf:nlt tag="entry.noTitle" />--</span>
											</c:if>
											<span>${nextBinder.title}</span>
										</a>
										<%  if (!parentTree.empty()) {  %>&nbsp;&gt;&gt;&nbsp;&nbsp;<%  }  %>
									</div>
								</c:if>
								</li>
								<c:set var="action" value="view_folder_listing"/>
							<%
								}
							%>
						</c:if>
					</ul>
					<div class="ss_clear"></div>
				</div>
			</div>

		
			<!-- Trash:  Menu Bar -->
			<div align="left">
			    <div class="ss_folder_border">
					<ssf:toolbar style="ss_actions_bar5 ss_actions_bar">			
						<ssf:toolbar toolbar="${ssTrashViewToolbar}" style="ss_actions_bar5 ss_actions_bar" item="true" />			
					</ssf:toolbar>
				</div>
				<div class="ss_clear"></div>
			</div>

		
			<!-- Trash:  Navigation Bar -->
			<c:set var="action"                   value="view_folder_listing" scope="request" />
			<c:set var="ssForumPageNav_HideGoBox" value="true"                scope="request" />
			<c:set var="ssForumPageNav_ShowTrash" value="true"                scope="request" />
			<jsp:include page="/WEB-INF/jsp/forum/view_forum_page_navigation_init.jsp" />
			<jsp:include page="/WEB-INF/jsp/forum/view_forum_page_navigation.jsp"      />


			<!-- Trash:  Listing Table -->
			<div id="ss_folder_table_parent" class="ss_folder">
				<c:set var="slidingTableStyle"        value="sliding"          />
				<c:set var="slidingTableTableStyle"   value=""                 />
				<c:set var="slidingTableRowStyle"     value="ss_table_oddRow"  />
				<c:set var="slidingTableRowOddStyle"  value="ss_table_oddRow"  />
				<c:set var="slidingTableRowEvenStyle" value="ss_table_evenRow" />
				<c:set var="slidingTableColStyle"     value=""                 />

				<ssf:slidingTable
						id="ss_folder_table"
						parentId="ss_folder_table_parent"
						type="${slidingTableStyle}" 
	 	  				height="<%= ssFolderTableHeight %>"
	 	  				folderId="${ssBinder.id}"
	 	  				tableStyle="${slidingTableTableStyle}">
					<!-- Trash Listing:  Header -->
					<ssf:slidingTableRow style="${slidingTableRowStyle}" headerRow="true">
						<!-- Trash Listing Header Column:  Select All -->
						<ssf:slidingTableColumn  style="${slidingTableColStyle}" width="3%">
							<div class="ss_title_menu" id="trash_selectAllCB_DIV"><input type="checkbox" id="trash_selectAllCB" class="ss_sliding_table_checkbox" onClick="ss_trashSelectAll(this); return(true);" onMouseOver="return(true);" onMouseOut="return(true);"/></div>
						</ssf:slidingTableColumn>


						<!-- Trash Listing Header Column:  Title -->
						<c:set var="action" value="view_folder_listing"/>
						<c:if test="${!empty ssFolderColumns['title']}">
						    <ssf:slidingTableColumn  style="${slidingTableColStyle}" width="27%">
							    <a
									href="<ssf:url action="${action}" actionUrl="true">
										<ssf:param name="showTrash"      value="true"                  />
										<ssf:param name="operation"      value="save_folder_sort_info" />
										<ssf:param name="binderId"       value="${ssBinder.id}"        />
										<ssf:param name="ssFolderSortBy" value="_sortTitle"            />
										<c:choose>
											<c:when test="${ ssFolderSortBy == '_sortTitle' && ssFolderSortDescend == 'false'}">
												<ssf:param name="ssFolderSortDescend" value="true"/>
											</c:when>
											<c:otherwise>
												<ssf:param name="ssFolderSortDescend" value="false"/>
											</c:otherwise>
										</c:choose></ssf:url>"
									<c:choose>
										<c:when test="${ ssFolderSortBy == '_sortTitle' && ssFolderSortDescend == 'false'}">
											<ssf:title tag="title.sort.by.column.desc">
												<ssf:param name="value" value='<%= NLT.get("trash.column.Title") %>' />
											</ssf:title>
										</c:when>
										<c:otherwise>
											<ssf:title tag="title.sort.by.column.asc">
												<ssf:param name="value" value='<%= NLT.get("trash.column.Title") %>' />
											</ssf:title>
										</c:otherwise>
									</c:choose>	
								>
									<div class="ss_title_menu"><ssf:nlt tag="trash.column.Title"/> </div>
									<c:if test="${ ssFolderSortBy == '_sortTitle' && ssFolderSortDescend == 'true'}">
										<img
											<ssf:alt tag="title.sorted.by.column.desc">
												<ssf:param name="value" value='<%= NLT.get("trash.column.Title") %>' />
											</ssf:alt>
											border="0"
											src="<html:imagesPath/>pics/menudown.gif"/>
									</c:if>
									<c:if test="${ ssFolderSortBy == '_sortTitle' && ssFolderSortDescend == 'false'}">
										<img
											<ssf:alt tag="title.sorted.by.column.asc">
												<ssf:param name="value" value='<%= NLT.get("trash.column.Title") %>' />
											</ssf:alt>
											border="0"
											src="<html:imagesPath/>pics/menuup.gif"/>
									</c:if>
							    </a>
						    </ssf:slidingTableColumn>
						</c:if>


						<!-- Trash Listing Header Column:  Date -->
						<c:if test="${!empty ssFolderColumns['date']}">
							<ssf:slidingTableColumn  style="${slidingTableColStyle}" width="20%">
								<a
									href="<ssf:url action="${action}" actionUrl="true">
										<ssf:param name="showTrash"      value="true"                  />
										<ssf:param name="operation"      value="save_folder_sort_info" />
										<ssf:param name="binderId"       value="${ssBinder.id}"        />
										<ssf:param name="ssFolderSortBy" value="_preDeletedWhen"       />
										<c:choose>
											<c:when test="${ ssFolderSortBy == '_preDeletedWhen' && ssFolderSortDescend == 'true'}">
												<ssf:param name="ssFolderSortDescend" value="false"/>
											</c:when>
											<c:otherwise>
												<ssf:param name="ssFolderSortDescend" value="true"/>
											</c:otherwise>
										</c:choose></ssf:url>"
									<c:choose>
										<c:when test="${ ssFolderSortBy == '_preDeletedWhen' && ssFolderSortDescend == 'true'}">
											<ssf:title tag="title.sort.by.column.asc">
												<ssf:param name="value" value='<%= NLT.get("trash.column.LastActivity") %>' />
											</ssf:title>
										</c:when>
										<c:otherwise>
											<ssf:title tag="title.sort.by.column.desc">
												<ssf:param name="value" value='<%= NLT.get("trash.column.LastActivity") %>' />
											</ssf:title>
										</c:otherwise>
									</c:choose>
								>
									<ssf:nlt tag="trash.column.LastActivity"/>
									<c:if test="${ ssFolderSortBy == '_preDeletedWhen' && ssFolderSortDescend == 'true'}">
										<img
											<ssf:alt tag="title.sorted.by.column.desc">
												<ssf:param name="value" value='<%= NLT.get("trash.column.LastActivity") %>' />
											</ssf:alt>
											border="0"
											src="<html:imagesPath/>pics/menudown.gif"/>
									</c:if>
									<c:if test="${ ssFolderSortBy == '_preDeletedWhen' && ssFolderSortDescend == 'false'}">
										<img
											<ssf:alt tag="title.sorted.by.column.asc">
												<ssf:param name="value" value='<%= NLT.get("trash.column.LastActivity") %>' />
											</ssf:alt> border="0" src="<html:imagesPath/>pics/menuup.gif"/>
									</c:if>
								</a>
							</ssf:slidingTableColumn>
						</c:if>


						<!-- Trash Listing Header Column:  Author -->
						<c:if test="${!empty ssFolderColumns['author']}">
							<ssf:slidingTableColumn  style="${slidingTableColStyle}" width="20%">
								<a
									href="<ssf:url action="${action}" actionUrl="true">
										<ssf:param name="showTrash"      value="true"                  />
										<ssf:param name="operation"      value="save_folder_sort_info" />
										<ssf:param name="binderId"       value="${ssBinder.id}"        />
										<ssf:param name="ssFolderSortBy" value="_preDeletedByTitle"    />
										<c:choose>
											<c:when test="${ ssFolderSortBy == '_preDeletedByTitle' && ssFolderSortDescend == 'false'}">
												<ssf:param name="ssFolderSortDescend" value="true"/>
											</c:when>
											<c:otherwise>
												<ssf:param name="ssFolderSortDescend" value="false"/>
											</c:otherwise>
										</c:choose></ssf:url>"
									<c:choose>
										<c:when test="${ ssFolderSortBy == '_preDeletedByTitle' && ssFolderSortDescend == 'false'}">
											<ssf:title tag="title.sort.by.column.desc">
												<ssf:param name="value" value='<%= NLT.get("trash.column.Author") %>' />
											</ssf:title>
										</c:when>
										<c:otherwise>
											<ssf:title tag="title.sort.by.column.asc">
												<ssf:param name="value" value='<%= NLT.get("trash.column.Author") %>' />
											</ssf:title>
										</c:otherwise>
									</c:choose>
								>
									<ssf:nlt tag="trash.column.Author"/>
									<c:if test="${ ssFolderSortBy == '_preDeletedByTitle' && ssFolderSortDescend == 'true'}">
										<img
											<ssf:alt tag="title.sorted.by.column.desc">
												<ssf:param name="value" value='<%= NLT.get("trash.column.Author") %>' />
											</ssf:alt>
											border="0"
											src="<html:imagesPath/>pics/menudown.gif"/>
									</c:if>
									<c:if test="${ ssFolderSortBy == '_preDeletedByTitle' && ssFolderSortDescend == 'false'}">
										<img
											<ssf:alt tag="title.sorted.by.column.asc">
												<ssf:param name="value" value='<%= NLT.get("trash.column.Author") %>' />
											</ssf:alt>
											border="0"
											src="<html:imagesPath/>pics/menuup.gif"/>
									</c:if>
								</a>
							</ssf:slidingTableColumn>
						</c:if>


						<!-- Trash Listing Header Column:  Location -->
						<c:if test="${!empty ssFolderColumns['location']}">
							<ssf:slidingTableColumn  style="${slidingTableColStyle}" width="30%">
								<a
									href="<ssf:url action="${action}" actionUrl="true">
										<ssf:param name="showTrash"      value="true"                  />
										<ssf:param name="operation"      value="save_folder_sort_info" />
										<ssf:param name="binderId"       value="${ssBinder.id}"        />
										<ssf:param name="ssFolderSortBy" value="_preDeletedFrom"       />
										<c:choose>
											<c:when test="${ ssFolderSortBy == '_preDeletedFrom' && ssFolderSortDescend == 'false'}">
												<ssf:param name="ssFolderSortDescend" value="true"/>
											</c:when>
											<c:otherwise>
												<ssf:param name="ssFolderSortDescend" value="false"/>
											</c:otherwise>
										</c:choose></ssf:url>"
									<c:choose>
										<c:when test="${ ssFolderSortBy == '_preDeletedFrom' && ssFolderSortDescend == 'false'}">
											<ssf:title tag="title.sort.by.column.desc">
												<ssf:param name="value" value='<%= NLT.get("trash.column.Location") %>' />
											</ssf:title>
										</c:when>
										<c:otherwise>
											<ssf:title tag="title.sort.by.column.asc">
												<ssf:param name="value" value='<%= NLT.get("trash.column.Location") %>' />
											</ssf:title>
										</c:otherwise>
									</c:choose>
								>
									<ssf:nlt tag="trash.column.Location"/>
									<c:if test="${ ssFolderSortBy == '_preDeletedFrom' && ssFolderSortDescend == 'true'}">
										<img
											<ssf:alt tag="title.sorted.by.column.desc">
												<ssf:param name="value" value='<%= NLT.get("trash.column.Location") %>' />
											</ssf:alt>
											border="0"
											src="<html:imagesPath/>pics/menudown.gif"/>
									</c:if>
									<c:if test="${ ssFolderSortBy == '_preDeletedFrom' && ssFolderSortDescend == 'false'}">
										<img
											<ssf:alt tag="title.sorted.by.column.asc">
												<ssf:param name="value" value='<%= NLT.get("trash.column.Location") %>' />
											</ssf:alt>
											border="0"
											src="<html:imagesPath/>pics/menuup.gif"/>
									</c:if>
								</a>
							</ssf:slidingTableColumn>
						</c:if>
					</ssf:slidingTableRow>


					<!-- Trash Listing:  Data Rows-->
					<c:forEach var="entry1" items="${ssFolderEntries}">
						<!-- Setup Title -->
						<c:if test="${empty entry1.title}"> 
							<c:set var="entry1_title">--<ssf:nlt tag="entry.noTitle" />--</c:set>
						</c:if>
						<c:if test="${!empty entry1.title}">
							<c:set var="entry1_title" value="${entry1.title}"/>
						</c:if>

						<!-- Setup preDeletedWhen -->
						<jsp:useBean id="entry1" type="java.util.HashMap" />
						<c:set var="entry1_deletedWhen_raw" value="${entry1._preDeletedWhen}" />
						<c:if test="${empty entry1_deletedWhen_raw}">
							<c:set var="entry1_deletedWhen" value="" />
						</c:if>
						<c:if test="${!empty entry1_deletedWhen_raw}">
							<jsp:useBean id="utilDate" class="java.util.Date"/>
							<c:set target="${utilDate}" property="time" value="${entry1_deletedWhen_raw}"/>
							<fmt:formatDate timeZone="${ssUser.timeZone.ID}" value="${utilDate}" type="both" timeStyle="short" dateStyle="short" var="entry1_deletedWhen" />
						</c:if>

						<!-- Setup preDeletedBy -->							
						<%
							Object entry1_deletedById_raw = entry1.get("_preDeletedById");
							org.kablink.teaming.domain.User entry1_deletedByUser;
							if (null == entry1_deletedById_raw) {
								entry1_deletedByUser = null;
							}
							else {
								String entry1_deletedById = entry1_deletedById_raw.toString();
								List entry1_deletedByUsers = org.kablink.teaming.util.ResolveIds.getPrincipals(entry1_deletedById, false);
								entry1_deletedByUser = ((org.kablink.teaming.domain.User) entry1_deletedByUsers.get(0));
							}
						%>

						<!-- Setup preDeletedFrom -->
						<c:set var="entry1_deletedFrom" value="${entry1._preDeletedFrom}" />
						<c:if test="${empty entry1_deletedFrom}">
							<c:set var="entry1_deletedFrom" value="" />
						</c:if>

						<!-- Setup locationBinderId -->
						<c:if test="${entry1._docType == 'entry'}">
							<c:set var="entry1_locationBinderId" value="${entry1._binderId}"     />
						</c:if>
						<c:if test="${entry1._docType != 'entry'}">
							<c:set var="entry1_locationBinderId" value="${entry1._binderParentId}" />
						</c:if>


						<!-- Trash Listing Data:  Row -->
						<c:set var="folderLineId" value="folderLine_${entry1._docId}"/>
						<ssf:slidingTableRow
								style="${slidingTableRowStyle}" 
								oddStyle="${slidingTableRowOddStyle}"
								evenStyle="${slidingTableRowEvenStyle}"
								id="${folderLineId}">
							<!-- Trash Listing Data Column:  Select Row -->
							<ssf:slidingTableColumn  style="${slidingTableColStyle}">
								<div class="ss_title_menu" id="trash_selectOneCB_${entry1._docType}_${entry1._docId}_DIV"><input type="checkbox" id="trash_selectOneCB_${entry1._docType}_${entry1._docId}" class="ss_sliding_table_checkbox" onClick="ss_trashSelectOne(this); return(true);" onMouseOver="return(true);" onMouseOut="return(true);" /></div>
							</ssf:slidingTableColumn>


							<!-- Trash Listing Data Column:  Title -->
							<c:if test="${!empty ssFolderColumns['title']}">
								<ssf:slidingTableColumn style="${slidingTableColStyle}">
									<c:if test="${entry1._docType == 'entry'}">
										<img
											alt="<ssf:nlt tag="trash.alt.entryIcon" />"
											title="<ssf:nlt tag="trash.alt.entryIcon" />"
											border="0"
											height="12"
											width="12"
											class="ss_sliding_table_column_image"
											src="<html:imagesPath/>pics/entry16.png"/>
									</c:if>
									<c:if test="${entry1._docType != 'entry'}">
										<c:if test="${entry1._entityType == 'workspace'}">
											<img
												alt="<ssf:nlt tag="trash.alt.workspaceIcon" />"
												title="<ssf:nlt tag="trash.alt.workspaceIcon" />"
												border="0"
												height="12"
												width="12"
												class="ss_sliding_table_column_image"
												src="<html:imagesPath/>icons/workspace.gif"/>
										</c:if>
										<c:if test="${entry1._entityType != 'workspace'}">
											<img
												alt="<ssf:nlt tag="trash.alt.folderIcon" />"
												title="<ssf:nlt tag="trash.alt.folderIcon" />"
												border="0"
												height="12"
												width="12"
												class="ss_sliding_table_column_image"
												src="<html:imagesPath/>icons/folder.png"/>
										</c:if>
									</c:if>

									<c:if test="${entry1._docType == 'entry'}">
										<a
											class="ss_new_thread"
											href="<ssf:url
													crawlable="true"
													adapter="true"
													portletName="ss_forum"
													binderId="${entry1._binderId}"
													action="view_folder_entry"
													entryId="${entry1._docId}"
													actionUrl="true"> 
												<ssf:param name="entryViewStyle"  value="${ss_entryViewStyle}"  />
												<ssf:param name="entryViewStyle2" value="${ss_entryViewStyle2}" />
											</ssf:url>"	    
											onClick="ss_loadEntry(this,'${entry1._docId}', '${ssBinder.id}', '${entry1._entityType}', '${renderResponse.namespace}', 'no');return false;" 
										>
											<span><c:out value="${entry1_title}"/></span>
										</a>
									</c:if>
									<c:if test="${entry1._docType != 'entry'}">
										<span><c:out value="${entry1_title}"/></span>
									</c:if>
								</ssf:slidingTableColumn>
 							</c:if>


							<!-- Trash Listing Data Column:  Date -->
							<c:if test="${!empty ssFolderColumns['date']}">
								<ssf:slidingTableColumn  style="${slidingTableColStyle}">
									<span>${entry1_deletedWhen}</span>
								</ssf:slidingTableColumn>
							</c:if>


							<!-- Trash Listing Data Column:  Author -->
							<c:if test="${!empty ssFolderColumns['author']}">
								<ssf:slidingTableColumn  style="${slidingTableColStyle}">
									<% if (null != entry1_deletedByUser) { %>
										<ssf:showUser user='<%= entry1_deletedByUser %>' />
									<% } %> 
								</ssf:slidingTableColumn>
							</c:if>


							<!-- Trash Listing Data Column:  Location -->
							<c:if test="${!empty ssFolderColumns['location']}">
								<ssf:slidingTableColumn  style="${slidingTableColStyle}">
									<% if (false) { %>
										<% /* I bagged making this a link to go to the location */ %>
										<% /* folder because of the hassels of making sure that */ %>
										<% /* it has not been predeleted.                       */ %>
							    		<a href="javascript: ;"
											onclick="return ss_gotoPermalink('${entry1_locationBinderId}', '${entry1_locationBinderId}', 'folder', '${ss_namespace}', 'yes');"
											title="${ssBinder}"
											><span>${entry1_deletedFrom}</span></a>
									<% } else { %>
										<span>${entry1_deletedFrom}</span>
									<% } %>
								</ssf:slidingTableColumn>
							</c:if>
						</ssf:slidingTableRow>
					</c:forEach>
				</ssf:slidingTable>
			</div>

			<!-- Hover hints for each entry. -->
			<c:if test="${!empty ssFolderEntries}">
				<c:forEach var="entry2" items="${ssFolderEntries}" >
					<div id="ss_folderEntryTitle_${entry2._docId}" class="ss_hover_over" style="visibility:hidden; display:none;">
						<span class="ss_style" >
							<ssf:textFormat formatAction="limitedDescription" textMaxWords="folder.preview.wordCount">
								<ssf:markup search="${entry2}">${entry2._desc}</ssf:markup>
							</ssf:textFormat>
						</span>
						<div class="ss_clear"></div>
					</div>
				</c:forEach>
			</c:if>
		</div>
		<c:if test="${empty ssFolderEntries && !(ssBinder.mirrored && empty ssBinder.resourceDriverName)}">
			<c:set var="ssEmptyTrash" value="true" scope="request" />
			<jsp:include page="/WEB-INF/jsp/forum/view_no_entries.jsp" />
		</c:if>
	</div>
</div>
