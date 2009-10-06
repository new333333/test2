<%
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
%>
<% /* Trash Listing. */ %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<jsp:useBean id="ssBinder"               type="org.kablink.teaming.domain.Binder" scope="request" />
<jsp:useBean id="ssUserFolderProperties" type="java.util.Map"                     scope="request" />
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

<nobr><center>
	<br />
	<c:if test="${trashMode == 'workspace'}" >Workspace (ID:  ${ssBinder.id}) trash:  ...this needs to be implemented...</c:if>
	<c:if test="${trashMode == 'folder'}"    >Folder (ID:  ${ssBinder.id}) trash:  ...this needs to be implemented...</c:if>
	<br />- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
</center></nobr>

<script type="text/javascript" src="<html:rootPath/>js/binder/ss_trash.js"></script>
<div id="ss_trashDiv${ss_namespace}" align="center">
	<div id="ss_folder_wrap">
		<div id="ss_folder_type_file" class="ss_style_color">
			<!-- Trash:  Title Bar -->
			<div id="ss_topic_box">
				<div id="ss_topic_box_h1">
					<ul class="ss_horizontal ss_nobullet">
						<li>
							<div class="ss_treeWidget">
								<a href="<ssf:url crawlable="true" adapter="true" portletName="ss_forum" binderId="${ssUser.workspaceId}" action="view_ws_listing"/>">
									<span>${ssUser.title} (${ssUser.name})</span>
			    		  		</a>
								&nbsp;&gt;&gt;&nbsp;&nbsp;
							</div>
						</li>
						<li><div class="ss_treeWidget">${ssBinder.title}</div></li>
					</ul>
					<div class="ss_clear"></div>
				</div>
			</div>

		
			<!-- Trash:  Menu Bar -->
			<div align="left">
			    <div class="ss_folder_border">
					<ssf:toolbar style="ss_actions_bar5 ss_actions_bar">			
						<ssHelpSpot 
							helpId="workspaces_folders/menus_toolbars/trash_view_toolbar" offsetX="0" offsetY="0" 
							title="<ssf:nlt tag="helpSpot.trashViewMenu"/>"></ssHelpSpot>
						<ssf:toolbar toolbar="${ssTrashViewToolbar}" style="ss_actions_bar5 ss_actions_bar" item="true" />			
					</ssf:toolbar>
				</div>
				<div class="ss_clear"></div>
			</div>

		
			<!-- Trash:  Navigation Bar -->
			<c:set var="ssForumPageNav_HideGoBox" value="true" scope="request"/>
			<jsp:include page="/WEB-INF/jsp/forum/view_forum_page_navigation.jsp" />


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
						<ssf:slidingTableColumn  style="${slidingTableColStyle}" width="4%">
							<div class="ss_title_menu"><input type="checkbox" id="trash_selectAllCB" style="margin: 0;" onChange="ss_trashSelectAll()" /></div>
						</ssf:slidingTableColumn>


						<!-- Trash Listing Header Column:  Title -->
						<c:if test="${!empty ssFolderColumns['title']}">
						    <ssf:slidingTableColumn  style="${slidingTableColStyle}" width="26%">
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
										<ssf:param name="ssFolderSortBy" value="_lastActivity"         />
										<c:choose>
											<c:when test="${ ssFolderSortBy == '_lastActivity' && ssFolderSortDescend == 'true'}">
												<ssf:param name="ssFolderSortDescend" value="false"/>
											</c:when>
											<c:otherwise>
												<ssf:param name="ssFolderSortDescend" value="true"/>
											</c:otherwise>
										</c:choose></ssf:url>"
									<c:choose>
										<c:when test="${ ssFolderSortBy == '_lastActivity' && ssFolderSortDescend == 'true'}">
											<ssf:title tag="title.sort.by.column.asc">
												<ssf:param name="value" value='<%= NLT.get("folder.column.LastActivity") %>' />
											</ssf:title>
										</c:when>
										<c:otherwise>
											<ssf:title tag="title.sort.by.column.desc">
												<ssf:param name="value" value='<%= NLT.get("folder.column.LastActivity") %>' />
											</ssf:title>
										</c:otherwise>
									</c:choose>
								>
									<ssf:nlt tag="folder.column.LastActivity"/>
									<c:if test="${ ssFolderSortBy == '_lastActivity' && ssFolderSortDescend == 'true'}">
										<img
											<ssf:alt tag="title.sorted.by.column.desc">
												<ssf:param name="value" value='<%= NLT.get("folder.column.LastActivity") %>' />
											</ssf:alt>
											border="0"
											src="<html:imagesPath/>pics/menudown.gif"/>
									</c:if>
									<c:if test="${ ssFolderSortBy == '_lastActivity' && ssFolderSortDescend == 'false'}">
										<img
											<ssf:alt tag="title.sorted.by.column.asc">
												<ssf:param name="value" value='<%= NLT.get("folder.column.LastActivity") %>' />
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
										<ssf:param name="ssFolderSortBy" value="_creatorTitle"         />
										<c:choose>
											<c:when test="${ ssFolderSortBy == '_creatorTitle' && ssFolderSortDescend == 'false'}">
												<ssf:param name="ssFolderSortDescend" value="true"/>
											</c:when>
											<c:otherwise>
												<ssf:param name="ssFolderSortDescend" value="false"/>
											</c:otherwise>
										</c:choose></ssf:url>"
									<c:choose>
										<c:when test="${ ssFolderSortBy == '_creatorTitle' && ssFolderSortDescend == 'false'}">
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
									<c:if test="${ ssFolderSortBy == '_creatorTitle' && ssFolderSortDescend == 'true'}">
										<img
											<ssf:alt tag="title.sorted.by.column.desc">
												<ssf:param name="value" value='<%= NLT.get("trash.column.Author") %>' />
											</ssf:alt>
											border="0"
											src="<html:imagesPath/>pics/menudown.gif"/>
									</c:if>
									<c:if test="${ ssFolderSortBy == '_creatorTitle' && ssFolderSortDescend == 'false'}">
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
										<ssf:param name="operation"      value="save_folder_sort_info"/>
										<ssf:param name="binderId"       value="${ssBinder.id}"/>
										<ssf:param name="ssFolderSortBy" value="_location"/>
										<c:choose>
											<c:when test="${ ssFolderSortBy == '_location' && ssFolderSortDescend == 'false'}">
												<ssf:param name="ssFolderSortDescend" value="true"/>
											</c:when>
											<c:otherwise>
												<ssf:param name="ssFolderSortDescend" value="false"/>
											</c:otherwise>
										</c:choose></ssf:url>"
									<c:choose>
										<c:when test="${ ssFolderSortBy == '_location' && ssFolderSortDescend == 'false'}">
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
									<c:if test="${ ssFolderSortBy == '_location' && ssFolderSortDescend == 'true'}">
										<img
											<ssf:alt tag="title.sorted.by.column.desc">
												<ssf:param name="value" value='<%= NLT.get("trash.column.Location") %>' />
											</ssf:alt>
											border="0"
											src="<html:imagesPath/>pics/menudown.gif"/>
									</c:if>
									<c:if test="${ ssFolderSortBy == '_location' && ssFolderSortDescend == 'false'}">
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
						<c:set var="folderLineId" value="folderLine_${entry1._docId}"/>;
						<jsp:useBean id="entry1" type="java.util.HashMap" />
						<%  
							if (!folderEntriesSeen.contains(entry1.get("_docId"))) {
								folderEntriesSeen.add(entry1.get("_docId"));
								String seenStyle = "";
								String seenStyleAuthor = "";
								String seenStyleFine = "class=\"ss_fineprint\"";
								String seenStyleTitle = seenStyle;
								String seenStyleTitle2 = "class=\"ss_noUnderlinePlus\"";
								boolean hasFile = false;
								boolean oneFile = false;
								if (entry1.containsKey("_fileID")) {
									String srFileID = entry1.get("_fileID").toString();
									hasFile = true;
									if (!srFileID.contains(",")) {
										oneFile = true;
									}
								}
						%>
						<c:set var="seenStyleburst" value=""/>
						<c:set var="hasFile2" value="<%= hasFile %>"/>
						<c:set var="oneFile2" value="<%= oneFile %>"/>
						<ssf:slidingTableRow
								style="${slidingTableRowStyle}" 
								oddStyle="${slidingTableRowOddStyle}"
								evenStyle="${slidingTableRowEvenStyle}"
								id="${folderLineId}">
							<!-- Trash List Data:  Row -->
<!-- ...row data goes here... -->
						</ssf:slidingTableRow>
						<%
							}
						%>
					</c:forEach>
				</ssf:slidingTable>
			</div>
		</div>
	</div>
</div>
