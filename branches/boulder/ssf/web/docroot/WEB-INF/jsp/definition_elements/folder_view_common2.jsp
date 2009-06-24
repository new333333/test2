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
<% // Folder listing %>
<jsp:useBean id="ssUser" type="org.kablink.teaming.domain.User" scope="request" />
<jsp:useBean id="ssSeenMap" type="org.kablink.teaming.domain.SeenMap" scope="request" />
<c:if test="${empty ss_entryViewStyle}">
  <c:set var="ss_entryViewStyle" value="" scope="request"/>
  <c:if test="${ssUser.displayStyle == 'newpage' && slidingTableStyle != 'fixed'}">
    <c:set var="ss_entryViewStyle" value="full" scope="request"/>
  </c:if>
</c:if>
<%
boolean useAdaptor = true;
List folderEntriesSeen = new ArrayList();
if (ObjectKeys.USER_DISPLAY_STYLE_ACCESSIBLE.equals(ssUser.getDisplayStyle()) &&
		!ObjectKeys.GUEST_USER_INTERNALID.equals(ssUser.getInternalId())) {
	useAdaptor = false;
}
String ssFolderTableHeight = "";
if (ssUserFolderProperties != null && ssUserFolderProperties.containsKey("folderEntryHeight")) {
	ssFolderTableHeight = (String) ssUserFolderProperties.get("folderEntryHeight");
}
if (ssFolderTableHeight == null || ssFolderTableHeight.equals("") || 
		ssFolderTableHeight.equals("0")) ssFolderTableHeight = "400";
%>
  <div align="left">
		<% // filter toolbar %>
	    <jsp:include page="/WEB-INF/jsp/forum/view_forum_user_filters.jsp" />
	    <div class="ss_folder_border">
		  <% // Add the toolbar with the navigation widgets, commands and filter %>
		  <ssf:toolbar style="ss_actions_bar5 ss_actions_bar">			
			<ssHelpSpot 
			  		helpId="workspaces_folders/menus_toolbars/folder_toolbar" offsetX="0" offsetY="0" 
			  		title="<ssf:nlt tag="helpSpot.folderControlAndFiltering"/>"></ssHelpSpot>
		    <% // Entry toolbar %>
		    <ssf:toolbar toolbar="${ssEntryToolbar}" style="ss_actions_bar5 ss_actions_bar" item="true" />			
		  </ssf:toolbar>
		  <div class="ss_clear"></div>
	    </div>
		<jsp:include page="/WEB-INF/jsp/forum/add_files_to_folder.jsp" />
	</div><!-- end of 2nd breadcrumb area -->
<jsp:include page="/WEB-INF/jsp/forum/view_forum_page_navigation.jsp" />

<div id="ss_folder_table_parent" class="ss_folder">

	<c:set var="slidingTableTableStyle" value=""/>
	<c:if test="${slidingTableStyle == 'fixed'}">
  		<c:set var="slidingTableTableStyle" value="ss_fixed_table"/>
	</c:if>
	<c:set var="slidingTableRowStyle" value="ss_table_oddRow"/>
	<c:set var="slidingTableRowOddStyle" value="ss_table_oddRow"/>
	<c:set var="slidingTableRowEvenStyle" value="ss_table_evenRow"/>
	<c:set var="slidingTableColStyle" value=""/>
	<c:if test="${slidingTableStyle == 'fixed'}">
  		<c:set var="slidingTableRowStyle" value=""/>
  		<c:set var="slidingTableRowOddStyle" value="ss_fixed_odd_TR"/>
  		<c:set var="slidingTableRowEvenStyle" value="ss_fixed_even_TR"/>
  		<c:set var="slidingTableColStyle" value="ss_fixed_TD"/>
	</c:if>
	<ssf:ifaccessible>
  		<c:set var="slidingTableRowStyle" value=""/>
  		<c:set var="slidingTableRowOddStyle" value="ss_fixed_odd_TR"/>
  		<c:set var="slidingTableRowEvenStyle" value="ss_fixed_even_TR"/>
  		<c:set var="slidingTableColStyle" value="ss_fixed_TD"/>
	</ssf:ifaccessible>

	<ssf:slidingTable id="ss_folder_table" parentId="ss_folder_table_parent" type="${slidingTableStyle}" 
 	  height="<%= ssFolderTableHeight %>" folderId="${ssBinder.id}" tableStyle="${slidingTableTableStyle}">
	<ssf:slidingTableRow style="${slidingTableRowStyle}" headerRow="true">

	<c:if test="${slidingTableStyle == 'fixed'}">
      <ssf:slidingTableColumn  style="${slidingTableColStyle}" width="2%">
        <img src="<html:imagesPath/>pics/discussion/ss_pin_orange.png" width="17" height="18" 
          title="<%= NLT.get("discussion.pinned").replaceAll("\"", "&QUOT;") %>">
      </ssf:slidingTableColumn>
	</c:if>

  	<c:if test="${!empty ssFolderColumns['number']}">
    <ssf:slidingTableColumn  style="${slidingTableColStyle}" width="6%">

      <a href="<ssf:url binderId="${ssBinder.id}" action="${action}" actionUrl="true"><ssf:param 
    	name="operation" value="save_folder_sort_info"/><ssf:param 
    	name="ssFolderSortBy" value="_sortNum"/><c:choose><c:when 
    	test="${ ssFolderSortBy == '_sortNum' && ssFolderSortDescend == 'true'}"><ssf:param 
    	name="ssFolderSortDescend" value="false"/></c:when><c:otherwise><ssf:param 
    	name="ssFolderSortDescend" value="true"/></c:otherwise></c:choose></ssf:url>"

	<c:choose>
	  <c:when test="${ ssFolderSortBy == '_sortNum' && ssFolderSortDescend == 'true'}">
	  	<ssf:title tag="title.sort.by.column.asc">
	  		<ssf:param name="value" value='<%= NLT.get("folder.column.Number") %>' />
	  	</ssf:title>
	  </c:when>
	  <c:otherwise>
	  	<ssf:title tag="title.sort.by.column.desc">
	  		<ssf:param name="value" value='<%= NLT.get("folder.column.Number") %>' />
	  	</ssf:title>
	  </c:otherwise>
	</c:choose>
	>
    	<ssf:nlt tag="folder.column.Number"/>
	    <c:if test="${ ssFolderSortBy == '_sortNum' && ssFolderSortDescend == 'true'}">
			<img <ssf:alt tag="title.sorted.by.column.desc"><ssf:param name="value" 
			value='<%= NLT.get("folder.column.Number") %>' /></ssf:alt> border="0" src="<html:imagesPath/>pics/menudown.gif"/>
		</c:if>
	    <c:if test="${ ssFolderSortBy == '_sortNum' && ssFolderSortDescend == 'false'}">
			<img <ssf:alt tag="title.sorted.by.column.asc"><ssf:param name="value" 
			value='<%= NLT.get("folder.column.Number") %>' /></ssf:alt> border="0" src="<html:imagesPath/>pics/menuup.gif"/>
		</c:if>
    </a>
    </ssf:slidingTableColumn>
  </c:if>

  <c:if test="${!empty ssFolderColumns['title']}">
    <ssf:slidingTableColumn  style="${slidingTableColStyle}" width="28%">
    
    <a href="<ssf:url binderId="${ssBinder.id}" action="${action}" actionUrl="true"><ssf:param 
    	name="operation" value="save_folder_sort_info"/><ssf:param 
    	name="ssFolderSortBy" value="_sortTitle"/><c:choose><c:when 
    	test="${ ssFolderSortBy == '_sortTitle' && ssFolderSortDescend == 'false'}"><ssf:param 
    	name="ssFolderSortDescend" value="true"/></c:when><c:otherwise><ssf:param 
    	name="ssFolderSortDescend" value="false"/></c:otherwise></c:choose></ssf:url>"
	
	<c:choose>
	  <c:when test="${ ssFolderSortBy == '_sortTitle' && ssFolderSortDescend == 'false'}">
	  	<ssf:title tag="title.sort.by.column.desc">
	  		<ssf:param name="value" value='<%= NLT.get("folder.column.Title") %>' />
	  	</ssf:title>
	  </c:when>
	  <c:otherwise>
	  	<ssf:title tag="title.sort.by.column.asc">
	  		<ssf:param name="value" value='<%= NLT.get("folder.column.Title") %>' />
	  	</ssf:title>
	  </c:otherwise>
	</c:choose>	
	 >
      <div class="ss_title_menu"><ssf:nlt tag="folder.column.Title"/> </div>
    	<c:if test="${ ssFolderSortBy == '_sortTitle' && ssFolderSortDescend == 'true'}">
			<img <ssf:alt tag="title.sorted.by.column.desc"><ssf:param name="value" 
			value='<%= NLT.get("folder.column.Title") %>' /></ssf:alt> border="0" src="<html:imagesPath/>pics/menudown.gif"/>
		</c:if>
		<c:if test="${ ssFolderSortBy == '_sortTitle' && ssFolderSortDescend == 'false'}">
			<img <ssf:alt tag="title.sorted.by.column.asc"><ssf:param name="value" 
			value='<%= NLT.get("folder.column.Title") %>' /></ssf:alt> border="0" src="<html:imagesPath/>pics/menuup.gif"/>
		</c:if>
    </a>
      
    </ssf:slidingTableColumn>
  </c:if>
  
  <c:if test="${!empty ssFolderColumns['author']}">
    <ssf:slidingTableColumn  style="${slidingTableColStyle}" width="20%">

    <a href="<ssf:url action="${action}" actionUrl="true"><ssf:param 
    	name="operation" value="save_folder_sort_info"/><ssf:param 
    	name="binderId" value="${ssBinder.id}"/><ssf:param 
    	name="ssFolderSortBy" value="_creatorTitle"/><c:choose><c:when 
    	test="${ ssFolderSortBy == '_creatorTitle' && ssFolderSortDescend == 'false'}"><ssf:param 
    	name="ssFolderSortDescend" value="true"/></c:when><c:otherwise><ssf:param 
    	name="ssFolderSortDescend" value="false"/></c:otherwise></c:choose></ssf:url>"
	
	<c:choose>
	  <c:when test="${ ssFolderSortBy == '_creatorTitle' && ssFolderSortDescend == 'false'}">
	  	<ssf:title tag="title.sort.by.column.desc">
	  		<ssf:param name="value" value='<%= NLT.get("folder.column.Author") %>' />
	  	</ssf:title>
	  </c:when>
	  <c:otherwise>
	  	<ssf:title tag="title.sort.by.column.asc">
	  		<ssf:param name="value" value='<%= NLT.get("folder.column.Author") %>' />
	  	</ssf:title>
	  </c:otherwise>
	</c:choose>
	>
		<ssf:nlt tag="folder.column.Author"/>
	    <c:if test="${ ssFolderSortBy == '_creatorTitle' && ssFolderSortDescend == 'true'}">
			<img <ssf:alt tag="title.sorted.by.column.desc"><ssf:param name="value" 
			value='<%= NLT.get("folder.column.Author") %>' /></ssf:alt> border="0" src="<html:imagesPath/>pics/menudown.gif"/>
		</c:if>
		<c:if test="${ ssFolderSortBy == '_creatorTitle' && ssFolderSortDescend == 'false'}">
			<img <ssf:alt tag="title.sorted.by.column.asc"><ssf:param name="value" 
			value='<%= NLT.get("folder.column.Author") %>' /></ssf:alt> border="0" src="<html:imagesPath/>pics/menuup.gif"/>
		</c:if>
    </a>
    </ssf:slidingTableColumn>
  </c:if>

  <c:if test="${!empty ssFolderColumns['comments']}">
    <ssf:slidingTableColumn  style="${slidingTableColStyle}" width="12%">
      <div class="ss_title_menu">
        <c:if test="${ss_folderViewStyle == 'folder'}"><ssf:nlt tag="folder.column.Replies"/></c:if>
        <c:if test="${ss_folderViewStyle != 'folder'}"><ssf:nlt tag="folder.column.Comments"/></c:if>
      </div>
    </ssf:slidingTableColumn>
  </c:if>

  <c:if test="${!empty ssFolderColumns['size']}">
    <ssf:slidingTableColumn  style="${slidingTableColStyle}" width="8%">

    <a href="<ssf:url binderId="${ssBinder.id}" action="${action}" actionUrl="true"><ssf:param 
	    	name="operation" value="save_folder_sort_info"/><ssf:param 
	    	name="ssFolderSortBy" value="_fileSize"/><c:choose><c:when 
	    	test="${ ssFolderSortBy == '_fileSize' && ssFolderSortDescend == 'true'}"><ssf:param 
	    	name="ssFolderSortDescend" value="false"/></c:when><c:otherwise><ssf:param 
	    	name="ssFolderSortDescend" value="true"/></c:otherwise></c:choose></ssf:url>">
	    <ssf:nlt tag="folder.column.Size"/>
	    <c:if test="${ ssFolderSortBy == '_fileSize' && ssFolderSortDescend == 'true'}">
			<img <ssf:alt tag="alt.showMenu"/> border="0" src="<html:imagesPath/>pics/menudown.gif"/>
		</c:if>
	    <c:if test="${ ssFolderSortBy == '_fileSize' && ssFolderSortDescend == 'false' }">
	    
			<img <ssf:alt tag="alt.hideThisMenu"/> border="0" src="<html:imagesPath/>pics/menuup.gif"/>
		</c:if>
    </a>
    </ssf:slidingTableColumn>
  </c:if>

  <c:if test="${!empty ssFolderColumns['download']}">
    <ssf:slidingTableColumn  style="${slidingTableColStyle}" width="8%">
      <div class="ss_title_menu"><ssf:nlt tag="folder.column.Download"/> </div>
    </ssf:slidingTableColumn>
  </c:if>

  <c:if test="${!empty ssFolderColumns['html']}">
    <ssf:slidingTableColumn  style="${slidingTableColStyle}" width="10%">
      <div class="ss_title_menu"><ssf:nlt tag="folder.column.Html"/> </div>
    </ssf:slidingTableColumn>
  </c:if>

  <c:if test="${!empty ssFolderColumns['state']}">
    <ssf:slidingTableColumn  style="${slidingTableColStyle}" width="8%">

    <a href="<ssf:url action="${action}" actionUrl="true"><ssf:param 
    	name="operation" value="save_folder_sort_info"/><ssf:param 
    	name="binderId" value="${ssBinder.id}"/><ssf:param 
    	name="ssFolderSortBy" value="_workflowState"/><c:choose><c:when 
    	test="${ ssFolderSortBy == '_workflowState' && ssFolderSortDescend == 'false'}"><ssf:param 
    	name="ssFolderSortDescend" value="true"/></c:when><c:otherwise><ssf:param 
    	name="ssFolderSortDescend" value="false"/></c:otherwise></c:choose></ssf:url>"
	
	<c:choose>
	  <c:when test="${ ssFolderSortBy == '_workflowState' && ssFolderSortDescend == 'false'}">
	  	<ssf:title tag="title.sort.by.column.desc">
	  		<ssf:param name="value" value='<%= NLT.get("folder.column.State") %>' />
	  	</ssf:title>
	  </c:when>
	  <c:otherwise>
	  	<ssf:title tag="title.sort.by.column.asc">
	  		<ssf:param name="value" value='<%= NLT.get("folder.column.State") %>' />
	  	</ssf:title>
	  </c:otherwise>
	</c:choose>
	>
    	<ssf:nlt tag="folder.column.State"/>
	    <c:if test="${ ssFolderSortBy == '_workflowState' && ssFolderSortDescend == 'true'}">
			<img <ssf:alt tag="title.sorted.by.column.desc"><ssf:param name="value" 
			value='<%= NLT.get("folder.column.State") %>' /></ssf:alt> border="0" src="<html:imagesPath/>pics/menudown.gif"/>
		</c:if>
		<c:if test="${ ssFolderSortBy == '_workflowState' && ssFolderSortDescend == 'false'}">
			<img <ssf:alt tag="title.sorted.by.column.asc"><ssf:param name="value" 
			value='<%= NLT.get("folder.column.State") %>' /></ssf:alt> border="0" src="<html:imagesPath/>pics/menuup.gif"/>
		</c:if>
    </a>
    </ssf:slidingTableColumn>
  </c:if>

  <c:if test="${!empty ssFolderColumns['date']}">
    <ssf:slidingTableColumn  style="${slidingTableColStyle}" width="20%">
    
    <a href="<ssf:url action="${action}" actionUrl="true"><ssf:param 
    	name="operation" value="save_folder_sort_info"/><ssf:param 
    	name="binderId" value="${ssBinder.id}"/><ssf:param 
    	name="ssFolderSortBy" value="_lastActivity"/><c:choose><c:when 
    	test="${ ssFolderSortBy == '_lastActivity' && ssFolderSortDescend == 'true'}"><ssf:param 
    	name="ssFolderSortDescend" value="false"/></c:when><c:otherwise><ssf:param 
    	name="ssFolderSortDescend" value="true"/></c:otherwise></c:choose></ssf:url>"
	
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
			<img <ssf:alt tag="title.sorted.by.column.desc"><ssf:param name="value" 
			value='<%= NLT.get("folder.column.LastActivity") %>' /></ssf:alt> border="0" src="<html:imagesPath/>pics/menudown.gif"/>
		</c:if>
		<c:if test="${ ssFolderSortBy == '_lastActivity' && ssFolderSortDescend == 'false'}">
			<img <ssf:alt tag="title.sorted.by.column.asc"><ssf:param name="value" 
			value='<%= NLT.get("folder.column.LastActivity") %>' /></ssf:alt> border="0" src="<html:imagesPath/>pics/menuup.gif"/>
		</c:if>
    </a>
    </ssf:slidingTableColumn>
  </c:if>
  
  <c:forEach var="column" items="${ssFolderColumns}">
    <c:set var="colName" value="${column.key}"/>
    <c:set var="defId" value=""/>
    <c:set var="eleType" value=""/>
    <c:set var="eleName" value=""/>
    <c:set var="eleCaption" value=""/>
	<jsp:useBean id="colName" type="java.lang.String" scope="page"/>
	<jsp:useBean id="defId" type="java.lang.String" scope="page"/>
	<jsp:useBean id="eleType" type="java.lang.String" scope="page"/>
	<jsp:useBean id="eleName" type="java.lang.String" scope="page"/>
	<jsp:useBean id="eleCaption" type="java.lang.String" scope="page"/>
<%
	if (colName.contains(",")) {
		String[] temp = colName.split(",");
		if (temp.length == 4) {
			defId = temp[0];
			eleType = temp[1];
			eleName = temp[2];
			eleCaption = temp[3];
		}
	}
	if (!defId.equals("")) {
%>
	  <c:set var="eleName" value="<%= eleName %>"/>
	  <c:set var="eleCaption" value="<%= eleCaption %>"/>
	  <c:set var="eleType" value="<%= eleType %>"/>
	  <c:set var="eleSortName" value="${eleName}"/>
	  <c:if test="${eleType == 'selectbox' || eleType == 'radio'}"><c:set var="eleSortName" value="_caption_${eleName}"/></c:if>
	  <c:if test="${eleType == 'text' || eleType == 'hidden'}"><c:set var="eleSortName" value="_sort_${eleName}"/></c:if>
	  <ssf:slidingTableColumn  style="${slidingTableColStyle}" width="20%">
	    <c:if test="${eleType != 'selectbox' && eleType != 'radio' && eleType != 'text' && eleType != 'hidden' && eleType != 'checkbox' && eleType != 'date' && eleType != 'datetime'}">
	      <div class="ss_title_menu">${eleCaption}</div>
	    </c:if>
	    <c:if test="${eleType == 'selectbox' || eleType == 'radio' || eleType == 'text' || eleType == 'hidden' || eleType == 'checkbox' || eleType == 'date' || eleType == 'datetime'}">
		    <a href="<ssf:url action="${action}" actionUrl="true"><ssf:param 
		    	name="operation" value="save_folder_sort_info"/><ssf:param 
		    	name="binderId" value="${ssBinder.id}"/><ssf:param 
		    	name="ssFolderSortBy" value="${eleSortName}"/><c:choose><c:when 
		    	test="${ ssFolderSortBy == eleSortName && ssFolderSortDescend == 'false'}"><ssf:param 
		    	name="ssFolderSortDescend" value="true"/></c:when><c:otherwise><ssf:param 
		    	name="ssFolderSortDescend" value="false"/></c:otherwise></c:choose></ssf:url>"
			
			<c:choose>
			  <c:when test="${ ssFolderSortBy == eleSortName && ssFolderSortDescend == 'true'}">
			  	<ssf:title tag="title.sort.by.column.asc">
			  		<ssf:param name="value" value='${eleCaption}' />
			  	</ssf:title>
			  </c:when>
			  <c:otherwise>
			  	<ssf:title tag="title.sort.by.column.desc">
			  		<ssf:param name="value" value='${eleCaption}' />
			  	</ssf:title>
			  </c:otherwise>
			</c:choose>
			>
				${eleCaption}
			    <c:if test="${ ssFolderSortBy == eleSortName && ssFolderSortDescend == 'true'}">
					<img <ssf:alt tag="title.sorted.by.column.desc"><ssf:param name="value" 
					value="${eleCaption}" /></ssf:alt> border="0" src="<html:imagesPath/>pics/menudown.gif"/>
				</c:if>
				<c:if test="${ ssFolderSortBy == eleSortName && ssFolderSortDescend == 'false'}">
					<img <ssf:alt tag="title.sorted.by.column.asc"><ssf:param name="value" 
					value='${eleCaption}' /></ssf:alt> border="0" src="<html:imagesPath/>pics/menuup.gif"/>
				</c:if>
		    </a>
	    </c:if>
	  </ssf:slidingTableColumn>
<%
	}
%>
  </c:forEach>
  
  <c:if test="${!empty ssFolderColumns['rating']}">
    <ssf:slidingTableColumn  style="${slidingTableColStyle}" width="10%">
    <a href="<ssf:url action="${action}" actionUrl="true"><ssf:param 
    	name="operation" value="save_folder_sort_info"/><ssf:param 
    	name="binderId" value="${ssBinder.id}"/><ssf:param 
    	name="ssFolderSortBy" value="_rating"/><c:choose><c:when 
    	test="${ ssFolderSortBy == '_rating' && ssFolderSortDescend == 'true'}"><ssf:param 
    	name="ssFolderSortDescend" value="false"/></c:when><c:otherwise><ssf:param 
    	name="ssFolderSortDescend" value="true"/></c:otherwise></c:choose></ssf:url>"
	
	<c:choose>
	  <c:when test="${ ssFolderSortBy == '_rating' && ssFolderSortDescend == 'false'}">
	  	<ssf:title tag="title.sort.by.column.desc">
	  		<ssf:param name="value" value='<%= NLT.get("folder.column.Rating") %>' />
	  	</ssf:title>
	  </c:when>
	  <c:otherwise>
	  	<ssf:title tag="title.sort.by.column.asc">
	  		<ssf:param name="value" value='<%= NLT.get("folder.column.Rating") %>' />
	  	</ssf:title>
	  </c:otherwise>
	</c:choose>
	><ssf:nlt tag="folder.column.Rating"/>
	    <c:if test="${ ssFolderSortBy == '_rating' && ssFolderSortDescend == 'true'}">
			<img <ssf:alt tag="title.sorted.by.column.desc"><ssf:param name="value" 
			value='<%= NLT.get("folder.column.Rating") %>' /></ssf:alt> border="0" src="<html:imagesPath/>pics/menudown.gif"/>
		</c:if>
		<c:if test="${ ssFolderSortBy == '_rating' && ssFolderSortDescend == 'false'}">
			<img <ssf:alt tag="title.sorted.by.column.asc"><ssf:param name="value" 
			value='<%= NLT.get("folder.column.Rating") %>' /></ssf:alt> border="0" src="<html:imagesPath/>pics/menuup.gif"/>
		</c:if>
    </a>
    </ssf:slidingTableColumn>
  </c:if>

</ssf:slidingTableRow>

  <% // Beginning of Rows %>
  
<c:forEach var="entry1" items="${ssFolderEntries}" >
<c:set var="folderLineId" value="folderLine_${entry1._docId}"/>;
<jsp:useBean id="entry1" type="java.util.HashMap" />
<%  
	if (!folderEntriesSeen.contains(entry1.get("_docId"))) {
		folderEntriesSeen.add(entry1.get("_docId"));
		String seenStyle = "";
		String seenStyleAuthor = "";
		String seenStyleFine = "class=\"ss_fineprint\"";
		if (!ssSeenMap.checkIfSeen(entry1)) {
			seenStyle = "class=\"ss_unseen\"";
			seenStyleAuthor="ss_unseen";
			seenStyleFine = "class=\"ss_unseen ss_fineprint\"";
		}
		String seenStyleTitle = seenStyle;
		String seenStyleTitle2 = "class=\"ss_noUnderlinePlus\"";
%>
<c:if test="${slidingTableStyle == 'fixed'}">
<%
		seenStyleTitle = "class=\"normal\"";
		seenStyleTitle2 = "class=\"normal\"";
%>
</c:if>
<%
		boolean hasFile = false;
		boolean oneFile = false;
		if (entry1.containsKey("_fileID")) {
			String srFileID = entry1.get("_fileID").toString();
			hasFile = true;
			if (!srFileID.contains(",")) oneFile = true;
		}
%>
<c:set var="seenStyleburst" value=""/>

<%
		if (!ssSeenMap.checkIfSeen(entry1)) {
			%><c:set var="seenStyleburst" value="1"/><%
		}
%>

<c:set var="hasFile2" value="<%= hasFile %>"/>
<c:set var="oneFile2" value="<%= oneFile %>"/>
<ssf:slidingTableRow style="${slidingTableRowStyle}" 
  oddStyle="${slidingTableRowOddStyle}" evenStyle="${slidingTableRowEvenStyle}" id="${folderLineId}" >

 <c:if test="${slidingTableStyle == 'fixed'}">
  <ssf:slidingTableColumn  style="${slidingTableColStyle}" width="2%">
     <a href="javascript: ;" onClick="ss_pinEntry(this,'${entry1._binderId}','${entry1._docId}');return false;">
      <img 
      <c:if test="${!empty ssPinnedEntries[entry1._docId]}">
        src="<html:imagesPath/>pics/discussion/ss_pin_orange.png" width="17" height="18"
        title="<%= NLT.get("discussion.unpin").replaceAll("\"", "&QUOT;") %>"
      </c:if>
      <c:if test="${empty entry1._pinned}">
        src="<html:imagesPath/>pics/discussion/ss_pin_grey.png" width="17" height="18"
        title="<%= NLT.get("discussion.pin").replaceAll("\"", "&QUOT;") %>"
      </c:if>
      ></a>
  </ssf:slidingTableColumn>
 </c:if>

 <c:if test="${!empty ssFolderColumns['number']}">
  <ssf:slidingTableColumn  style="${slidingTableColStyle}">
    <a href="<ssf:url     
    adapter="<%= useAdaptor %>" 
    portletName="ss_forum" 
    binderId="${ssBinder.id}" 
    action="view_folder_entry" 
    entryId="${entry1._docId}" actionUrl="true">
    <ssf:param name="entryViewStyle" value="${ss_entryViewStyle}"/>
    <ssf:param name="entryViewStyle2" value="${ss_entryViewStyle2}"/></ssf:url>" 
<c:if test="${slidingTableStyle != 'fixed' || ssUser.displayStyle != 'iframe'}">
    onClick="ss_loadEntry(this,'${entry1._docId}', '${ssBinder.id}', '${entry1._entityType}', '${renderResponse.namespace}', 'no');return false;" 
</c:if>
<c:if test="${slidingTableStyle == 'fixed' && ssUser.displayStyle == 'iframe'}">
    onClick="ss_loadEntryInPlace(this,'${entry1._docId}', '${ssBinder.id}', '${entry1._entityType}', '${renderResponse.namespace}', '${ss_entryViewStyle2}', 'no');return false;" 
</c:if>
    ><span <%= seenStyle %>><c:out value="${entry1._docNum}"/>.</span></a>&nbsp;
  </ssf:slidingTableColumn>
 </c:if>
  
 <c:if test="${!empty ssFolderColumns['title']}">
  <ssf:slidingTableColumn style="${slidingTableColStyle}">
  <!-- to keep sunburst in line -->
    <c:if test="${!empty seenStyleburst}">
    
  <a id="ss_sunburstDiv${ssBinder.id}_${entry1._docId}" href="javascript: ;" 
  title="<ssf:nlt tag="sunburst.click"/>"
  onClick="ss_hideSunburst('${entry1._docId}', '${ssBinder.id}');return false;"
>
  	<img src="<html:rootPath/>images/pics/discussion/sunburst.png" 
  	align="text-bottom" border="0" <ssf:alt tag="sunburst.click"/> />&nbsp;
  </a>
    
	</c:if>
	
  	<a  class="ss_new_thread"
  	href="<ssf:url crawlable="true"
    adapter="true" 
    portletName="ss_forum" 
    binderId="${ssBinder.id}" 
    action="view_folder_entry" 
    entryId="${entry1._docId}" actionUrl="true" 
    ><ssf:param name="entryViewStyle" value="${ss_entryViewStyle}"/>
    <ssf:param name="entryViewStyle2" value="${ss_entryViewStyle2}"/></ssf:url>" 
    
	<c:if test="${slidingTableStyle != 'fixed' || ssUser.displayStyle != 'iframe'}">
    	onClick="ss_loadEntry(this,'${entry1._docId}', '${ssBinder.id}', '${entry1._entityType}', '${renderResponse.namespace}', 'no');return false;" 
	</c:if>
	
	<c:if test="${slidingTableStyle == 'fixed' && ssUser.displayStyle == 'iframe'}">
    	onClick="ss_loadEntryInPlace(this, '${entry1._docId}', '${ssBinder.id}', '${entry1._entityType}', '${renderResponse.namespace}', '${ss_entryViewStyle2}', 'no', 'ss_folderEntryTitle_${entry1._docId}');return false;" 
    </c:if>
    <c:if test="${slidingTableStyle == 'fixed'}">
    	<c:if test="${!empty entry1._desc}">
    	  onMouseOver="ss_showHoverOver(this, 'ss_folderEntryTitle_${entry1._docId}', event, 20, 12);"
    	  onMouseOut="ss_hideHoverOver('ss_folderEntryTitle_${entry1._docId}');"
    	</c:if>
	</c:if>
    >
    
    <c:if test="${!empty seenStyleburst}">
  			<span id="folderLineSeen_${entry1._docId}" <%= seenStyle %> ><c:if test="${empty entry1.title}" 
    		>--<ssf:nlt tag="entry.noTitle" />--</c:if><c:out value="${entry1.title}"/></span></a>
	</c:if>

	<c:if test="${empty seenStyleburst}">
		<c:if test="${slidingTableStyle == 'fixed'}">
    		<span <%= seenStyle %>><c:if test="${empty entry1.title}" 
    		>--<ssf:nlt tag="entry.noTitle" />--</c:if><c:out value="${entry1.title}"/></span></a> 
		</c:if>
	  	<c:if test="${slidingTableStyle != 'fixed'}">
    		<span <%= seenStyle %>><c:if test="${empty entry1.title}" 
    		>--<ssf:nlt tag="entry.noTitle" />--</c:if><c:out value="${entry1.title}"/></span></a> 
		</c:if> 		
	</c:if>  	
  	
  </ssf:slidingTableColumn>
 </c:if>
  
  <c:if test="${!empty ssFolderColumns['author']}">
  <ssf:slidingTableColumn  style="${slidingTableColStyle}">
	<ssf:showUser user='<%=(User)entry1.get("_principal")%>' titleStyle="<%= seenStyleAuthor %>"/> 
  </ssf:slidingTableColumn>
 </c:if>
 
 <c:if test="${!empty ssFolderColumns['comments']}">
  <ssf:slidingTableColumn  style="${slidingTableColStyle}">
      <span <%= seenStyle %>>${entry1._totalReplyCount}</span>
  </ssf:slidingTableColumn>
 </c:if>
  
 <c:if test="${!empty ssFolderColumns['size']}">
  <ssf:slidingTableColumn  style="${slidingTableColStyle}">
    <c:if test="${hasFile2 && oneFile2 && !empty entry1._fileSize}">
      <span <%= seenStyle %>><fmt:setLocale value="${ssUser.locale}"/><fmt:formatNumber value="${entry1._fileSize}" />KB</span>
    </c:if>
  </ssf:slidingTableColumn>
 </c:if>
  
 <c:if test="${!empty ssFolderColumns['download']}">
  <ssf:slidingTableColumn  style="${slidingTableColStyle}">
    <c:if test="${hasFile2 && oneFile2}">
<%
	String fn = "";
	if (entry1.containsKey("_fileName")) fn = (String) entry1.get("_fileName");
	String ext = "";
	if (fn.lastIndexOf(".") >= 0) ext = fn.substring(fn.lastIndexOf("."));
	
// - - - - -
	// In order to fix Bugzilla bug 488283, I removed the check disallowing
	// downloads of .ppt files from IE.  If there was a problem, it was
	// only with Powerpoint invoked from IE.  Removing the check now lets
	// this work if users use OpenOffice or some other application besides
	// Powerpoint to open these files.  If we get support questions about
	// why this fails in some installations with Powerpoint, we should
	// point them to Microsoft for a solution.
	//
	// drfoster@novell.com
// - - - - -
//	if (!isIECheck || !ext.equals(".ppt"))
// - - - - -
	{
		//Don't show ppt file urls for IE. Powerpoint 2007 doesn't work with these urls
%>
      <a href="<ssf:fileUrl search="${entry1}"/>" class="ss_download_link"
		onClick="return ss_openUrlInWindow(this, '_blank');"
	  ><span><ssf:nlt tag="entry.download"/></span></a>
<%
	}
%>
    </c:if>&nbsp;
  </ssf:slidingTableColumn>
 </c:if>
  
 <c:if test="${!empty ssFolderColumns['html']}">
  <ssf:slidingTableColumn  style="${slidingTableColStyle}">
    <c:if test="${hasFile2 && oneFile2}">
		<ssf:ifSupportsViewAsHtml relativeFilePath="${entry1._fileName}" browserType="<%=strBrowserType%>">
			<a target="_blank" style="text-decoration: none;" href="<ssf:url 
				webPath="viewFile"
			    folderId="${entry1._binderId}"
			    entryId="${entry1._docId}" >
				<ssf:param name="entityType" value="${entry1._entityType}"/>
			    <ssf:param name="fileId" value="${entry1._fileID}"/>
			    <ssf:param name="fileTime" value="${entry1._fileTime}"/>
			    <ssf:param name="viewType" value="html"/>
			    </ssf:url>" title="<ssf:nlt tag="title.open.file.in.html.format" />" 
			><span <%= seenStyle %>>[<ssf:nlt tag="entry.HTML" />]</span></a>
		</ssf:ifSupportsViewAsHtml>
    </c:if>&nbsp;
  </ssf:slidingTableColumn>
 </c:if>
  
<c:if test="${!empty ssFolderColumns['state']}">
  <ssf:slidingTableColumn  style="${slidingTableColStyle}">
    <c:if test="${!empty entry1._workflowStateCaption}">
    <a href="<ssf:url     
    adapter="<%= useAdaptor %>" 
    portletName="ss_forum" 
    binderId="${ssBinder.id}" 
    action="view_folder_entry" 
    entryId='<%= entry1.get("_docId").toString() %>' actionUrl="true" >
    <ssf:param name="entryViewStyle" value="${ss_entryViewStyle}"/>
    <ssf:param name="entryViewStyle2" value="${ss_entryViewStyle2}"/></ssf:url>" 
<c:if test="${slidingTableStyle != 'fixed'}">
    onClick="ss_loadEntry(this,'<c:out value="${entry1._docId}"/>', '${ssBinder.id}', '${entry1._entityType}', '${renderResponse.namespace}', 'no');return false;" 
</c:if>
<c:if test="${slidingTableStyle == 'fixed'}">
    onClick="ss_loadEntryInPlace(this,'<c:out value="${entry1._docId}"/>', '${ssBinder.id}', '${entry1._entityType}', '${renderResponse.namespace}', '${ss_entryViewStyle2}', 'no');return false;" 
</c:if>
    ><span <%= seenStyle %>><ssf:nlt tag="${entry1._workflowStateCaption}" checkIfTag="true"/></span></a>
    </c:if>&nbsp;
  </ssf:slidingTableColumn>
 </c:if>
  

 <c:if test="${!empty ssFolderColumns['date']}">
  <ssf:slidingTableColumn  style="${slidingTableColStyle}">
    <span <%= seenStyle %>><fmt:formatDate timeZone="${ssUser.timeZone.ID}"
     value="${entry1._lastActivity}" type="both" 
	 timeStyle="short" dateStyle="short" /></span>
  </ssf:slidingTableColumn>
 </c:if>
 
<c:set var="colName2" value=""/>
<c:set var="defId2" value=""/>
<c:set var="eleType2" value=""/>
<c:set var="eleName2" value=""/>
<c:set var="eleCaption2" value=""/>
<jsp:useBean id="colName2" type="java.lang.String"/>
<jsp:useBean id="defId2" type="java.lang.String"/>
<jsp:useBean id="eleType2" type="java.lang.String"/>
<jsp:useBean id="eleName2" type="java.lang.String"/>
<jsp:useBean id="eleCaption2" type="java.lang.String"/>
  <c:forEach var="column" items="${ssFolderColumns}">
	<jsp:useBean id="column" type="java.util.Map.Entry"/>
<%
	String[] temp = new String[] {};
	colName2 = column.getKey().toString();
	if (colName2.contains(",")) {
		temp = colName2.split(",");
		if (temp != null && temp.length == 4) {
			defId2 = temp[0];
			eleType2 = temp[1];
			eleName2 = temp[2];
			eleCaption2 = temp[3];
		} else {
			defId2 = "";
			eleType2 = "";
			eleName2 = "";
			eleCaption2 = "";
		}
	}
	if (defId2 != null && !defId2.equals("")) {
%>
	  <c:set var="defId2" value="<%= defId2 %>"/>
	  <c:set var="eleType2" value="<%= eleType2 %>"/>
	  <c:set var="eleName2" value="<%= eleName2 %>"/>
	  <c:set var="eleCaption2" value="<%= eleCaption2 %>"/>
	  <c:set var="entryDef" value="${ssEntryDefinitionMap[defId2]}"/>
	  <c:if test="${!empty entryDef}">
	  <jsp:useBean id="entryDef" type="org.kablink.teaming.domain.Definition"/>
	  <ssf:slidingTableColumn  style="${slidingTableColStyle}">
         <span <%= seenStyle %>>
         <c:if test="${!empty eleName2 && !empty entry1[eleName2]}">
	       <c:if test="${eleType2 == 'selectbox' || eleType2 == 'radio' || eleType2 == 'checkbox' || eleType2 == 'text' || eleType2 == 'entryAttributes'}">
	         <%
	         	String eleValues = org.kablink.teaming.web.util.DefinitionHelper.getCaptionsFromValues(entryDef, eleName2, entry1.get(eleName2).toString());
	         %>
	         <%= eleValues %>
	       </c:if>
	       <c:if test="${eleType2 == 'date'}">
	       	 <c:if test="${!empty entry1[eleName2]}">
<%
	try {
		java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat("yyyy-MM-dd");
	    String year = ((String)entry1.get(eleName2)).substring(0,4);
		String month = ((String)entry1.get(eleName2)).substring(4,6);
		String day = ((String)entry1.get(eleName2)).substring(6,8);
		java.util.Date date = formatter.parse(year + "-" + month + "-" + day);
%>
	<fmt:formatDate value="<%= date %>" type="date" dateStyle="short" />
<%
	} catch(Exception e) {}
%>
	       	 </c:if>
	       </c:if>
	       <c:if test="${eleType2 == 'user_list' || 
	       				 eleType2 == 'userListSelectbox'}">
          	<c:set var="separator" value=""/>
<%
	try {
		String sr = entry1.get(eleName2).toString();
		java.util.Set ids = org.kablink.teaming.util.LongIdUtil.getIdsAsLongSet(sr, ",");
%>
          	<c:forEach var="user" 
          	  items="<%= org.kablink.teaming.util.ResolveIds.getPrincipals(ids, false) %>"
          	>${separator}${user.title}<c:set var="separator" value=", "/>
          	</c:forEach>
<%
	} catch(Exception e) {}
%>
          </c:if>
         </c:if>
         </span>
	   </ssf:slidingTableColumn>
	   </c:if>
<%
	}
%>
  </c:forEach>

 <c:if test="${!empty ssFolderColumns['rating']}">
   <ssf:slidingTableColumn  style="${slidingTableColStyle}">
     <c:if test="${!empty entry1._rating}">
		<span class = "ss_nowrap">
			<%
				String iRating = String.valueOf(java.lang.Math.round(Float.valueOf(entry1.get("_rating").toString())));
			%>
			<c:set var="sRating" value="<%= iRating %>"/>
			<c:if test="${sRating > 0}">
				<c:forEach var="i" begin="0" end="${sRating - 1}" step="1">
			
				  <img border="0" 
				    <ssf:alt tag="alt.goldStar"/>
				    src="<html:imagesPath/>pics/star_gold.gif"/>
				 
			
				</c:forEach>
			</c:if>
			
			<c:if test="${sRating < 5}">
				<c:forEach var="i" begin="${sRating}" end="4" step="1">
				  <img <ssf:alt tag="alt.grayStar"/> border="0" 
					    src="<html:imagesPath/>pics/star_gray.gif" />
				  
				</c:forEach>
			</c:if>
		</span>
     </c:if>&nbsp;
   </ssf:slidingTableColumn>
 </c:if>
  
</ssf:slidingTableRow>
<%
	}
%>
</c:forEach>
</ssf:slidingTable>
</div>

<c:if test="${ssBinder.mirrored && empty ssBinder.resourceDriverName}">
	<div class="ss_style ss_portlet"><span class="ss_errorLabel"><ssf:nlt tag="binder.mirrored.incomplete"/></span></div>
</c:if>
<c:if test="${empty ssFolderEntries && !(ssBinder.mirrored && empty ssBinder.resourceDriverName)}">
	<jsp:include page="/WEB-INF/jsp/forum/view_no_entries.jsp" />
</c:if>
<c:if test="${!empty ssFolderEntries}">
	<c:forEach var="entry2" items="${ssFolderEntries}" >
	  <div id="ss_folderEntryTitle_${entry2._docId}" class="ss_hover_over" 
	    style="visibility:hidden; display:none;">
	      <span class="ss_style" >
			  <ssf:textFormat formatAction="limitedDescription" textMaxWords="folder.preview.wordCount">
			    <ssf:markup search="${entry2}">${entry2._desc}</ssf:markup>
			    </ssf:textFormat>
	      </span>
	      <div class="ss_clear"></div>
	  </div>
	</c:forEach>
</c:if>
