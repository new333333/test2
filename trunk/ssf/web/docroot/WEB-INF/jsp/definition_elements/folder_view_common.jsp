<% // Folder listing %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<jsp:useBean id="ssSeenMap" type="com.sitescape.ef.domain.SeenMap" scope="request" />
<jsp:useBean id="ssUser" type="com.sitescape.ef.domain.User" scope="request" />
<%
	String displayStyle = ssUser.getDisplayStyle();
	if (displayStyle == null) displayStyle = "";
	
	String slidingTableStyle = "sliding";
	if (ssUser.getDisplayStyle() != null && 
	        ssUser.getDisplayStyle().equals(ObjectKeys.USER_DISPLAY_STYLE_VERTICAL)) {
		slidingTableStyle = "sliding_scrolled";
	}
	boolean useAdaptor = true;
	if (ssUser.getDisplayStyle() != null && 
	        ssUser.getDisplayStyle().equals(ObjectKeys.USER_DISPLAY_STYLE_ACCESSIBLE)) {
		useAdaptor = false;
	}
	String ssFolderTableHeight = "";
	Map ssFolderPropertiesMap = ssUserFolderProperties.getProperties();
	if (ssFolderPropertiesMap != null && ssFolderPropertiesMap.containsKey("folderEntryHeight")) {
		ssFolderTableHeight = (String) ssFolderPropertiesMap.get("folderEntryHeight");
	}
	if (ssFolderTableHeight == null || ssFolderTableHeight.equals("") || 
			ssFolderTableHeight.equals("0")) ssFolderTableHeight = "400";
%>
<script type="text/javascript">
var ss_displayStyle = "<%= displayStyle %>";
var ss_saveFolderColumnsUrl = "<portlet:actionURL windowState="maximized">
		<portlet:param name="action" value="${action}"/>
		<portlet:param name="binderId" value="${ssFolder.id}"/>
		<portlet:param name="operation" value="save_folder_columns"/>
		</portlet:actionURL>";
var ss_saveSubscriptionUrl = "<portlet:actionURL windowState="maximized">
		<portlet:param name="action" value="${action}"/>
		<portlet:param name="binderId" value="${ssBinder.id}"/>
		<portlet:param name="operation" value="subscribe"/>
		</portlet:actionURL>";
var ss_placeholderEntryUrl = "<portlet:renderURL windowState="maximized">
		<portlet:param name="action" value="view_folder_entry"/>
		<portlet:param name="binderId" value="ssBinderIdPlaceHolder"/>
		<portlet:param name="entryId" value="ssEntryIdPlaceHolder"/>
		<portlet:param name="newTab" value="ssNewTabPlaceHolder"/>
		</portlet:renderURL>";
var ss_placeholderFileUrl = "<ssf:url 
    	webPath="viewFile"
    	folderId="ssBinderIdPlaceHolder"
    	entryId="ssEntryIdPlaceHolder" >
    	</ssf:url>";
var ss_confirmDeleteFolderText = "<ssf:nlt tag="folder.confirmDeleteFolder"/>";
</script>

<div id="ss_folder_table_parent" class="ss_folder">

<div style="margin:0px;">
<%
	if (ssUser.getDisplayStyle() != null && 
	        ssUser.getDisplayStyle().equals(ObjectKeys.USER_DISPLAY_STYLE_VERTICAL)) {
%>
<div align="right" style="margin:0px 4px 0px 0px;">
  <a href="<ssf:url
	adapter="true" 
	portletName="ss_forum" 
	action="__ajax_request" 
	actionUrl="true" >
	<ssf:param name="operation" value="configure_folder_columns" />
	<ssf:param name="binderId" value="${ssBinder.id}" />
	<ssf:param name="rn" value="ss_randomNumberPlaceholder" />
	</ssf:url>" onClick="ss_createPopupDiv(this, 'ss_folder_column_menu');return false;">
    <span class="ss_fineprint ss_light"><ssf:nlt tag="misc.configureColumns"/></span></a>
</div>
<%
	}
%>
<div class="ss_folder_border" style="position:relative; top:2; margin:2px; padding:2px;
  border-top:solid #666666 1px; 
  border-right:solid #666666 1px; 
  border-left:solid #666666 1px;">

<% // Add the toolbar with the navigation widgets, commands and filter %>
<ssf:toolbar style="ss_actions_bar">

<ssf:toolbar style="ss_actions_bar" item="true">
<c:set var="ss_history_bar_table_class" value="ss_actions_bar_background ss_actions_bar_history_bar" scope="request"/>
<%@ include file="/WEB-INF/jsp/forum/view_forum_history_bar.jsp" %>
</ssf:toolbar>

<% // Entry toolbar %>
<c:if test="${!empty ssEntryToolbar}">
<ssf:toolbar toolbar="${ssEntryToolbar}" style="ss_actions_bar" item="true" />
</c:if>

<ssf:toolbar style="ss_actions_bar" item="true" >
<%@ include file="/WEB-INF/jsp/forum/view_forum_user_filters.jsp" %>
</ssf:toolbar>

</ssf:toolbar>

</div>
</div>
<ssf:slidingTable id="ss_folder_table" parentId="ss_folder_table_parent" type="<%= slidingTableStyle %>" 
 height="<%= ssFolderTableHeight %>" folderId="${ssFolder.id}">

<ssf:slidingTableRow headerRow="true">
  <c:if test="${!empty ssFolderColumns['number']}">
    <ssf:slidingTableColumn width="12%">

    <a href="<portlet:actionURL windowState="maximized" portletMode="view">
		<portlet:param name="action" value="${action}"/>
		<portlet:param name="operation" value="save_folder_sort_info"/>
		<portlet:param name="binderId" value="${ssFolder.id}"/>
		<portlet:param name="ssFolderSortBy" value="_sortNum"/>
		
		<c:choose>
		  <c:when test="${ ssFolderSortBy == '_sortNum' && ssFolderSortDescend == 'true'}">
		  	<portlet:param name="ssFolderSortDescend" value="false"/>
		  </c:when>
		  <c:otherwise>
		  	<portlet:param name="ssFolderSortDescend" value="true"/>
		  </c:otherwise>
		</c:choose>
	</portlet:actionURL>">
    	<ssf:nlt tag="folder.column.Number"/>
    <a/>

    <c:if test="${ ssFolderSortBy == '_sortNum' && ssFolderSortDescend == 'true'}">
		<img src="<html:imagesPath/>pics/sym_s_down.gif"/>
	</c:if>
    <c:if test="${ ssFolderSortBy == '_sortNum' && ssFolderSortDescend == 'false' }">
		<img src="<html:imagesPath/>pics/sym_s_up.gif"/>
	</c:if>

    </ssf:slidingTableColumn>
  </c:if>
  <c:if test="${!empty ssFolderColumns['title']}">
    <ssf:slidingTableColumn width="28%">
    
    <a href="<portlet:actionURL windowState="maximized" portletMode="view">
		<portlet:param name="action" value="${action}"/>
		<portlet:param name="operation" value="save_folder_sort_info"/>
		<portlet:param name="binderId" value="${ssFolder.id}"/>
		<portlet:param name="ssFolderSortBy" value="_title1"/>
		<c:choose>
		  <c:when test="${ ssFolderSortBy == '_title1' && ssFolderSortDescend == 'false'}">
		  	<portlet:param name="ssFolderSortDescend" value="true"/>
		  </c:when>
		  <c:otherwise>
		  	<portlet:param name="ssFolderSortDescend" value="false"/>
		  </c:otherwise>
		</c:choose>
	</portlet:actionURL>">
      <div class="ss_title_menu"><ssf:nlt tag="folder.column.Title"/> </div>
    <a/>

    <c:if test="${ ssFolderSortBy == '_title1' && ssFolderSortDescend == 'true'}">
		<img src="<html:imagesPath/>pics/sym_s_down.gif"/>
	</c:if>
	<c:if test="${ ssFolderSortBy == '_title1' && ssFolderSortDescend == 'false'}">
		<img src="<html:imagesPath/>pics/sym_s_up.gif"/>
	</c:if>
      
    </ssf:slidingTableColumn>
  </c:if>
  <c:if test="${!empty ssFolderColumns['state']}">
    <ssf:slidingTableColumn width="20%">

    <a href="<portlet:actionURL windowState="maximized" portletMode="view">
		<portlet:param name="action" value="${action}"/>
		<portlet:param name="operation" value="save_folder_sort_info"/>
		<portlet:param name="binderId" value="${ssFolder.id}"/>
		<portlet:param name="ssFolderSortBy" value="_workflowState"/>
		<c:choose>
		  <c:when test="${ ssFolderSortBy == '_workflowState' && ssFolderSortDescend == 'false'}">
		  	<portlet:param name="ssFolderSortDescend" value="true"/>
		  </c:when>
		  <c:otherwise>
		  	<portlet:param name="ssFolderSortDescend" value="false"/>
		  </c:otherwise>
		</c:choose>		
	</portlet:actionURL>">
    	<ssf:nlt tag="folder.column.State"/>
    <a/>

    <c:if test="${ ssFolderSortBy == '_workflowState' && ssFolderSortDescend == 'true'}">
		<img src="<html:imagesPath/>pics/sym_s_down.gif"/>
	</c:if>
	<c:if test="${ ssFolderSortBy == '_workflowState' && ssFolderSortDescend == 'false'}">
		<img src="<html:imagesPath/>pics/sym_s_up.gif"/>
	</c:if>

    </ssf:slidingTableColumn>
  </c:if>
  <c:if test="${!empty ssFolderColumns['author']}">
    <ssf:slidingTableColumn width="20%">

    <a href="<portlet:actionURL windowState="maximized" portletMode="view">
		<portlet:param name="action" value="${action}"/>
		<portlet:param name="operation" value="save_folder_sort_info"/>
		<portlet:param name="binderId" value="${ssFolder.id}"/>
		<portlet:param name="ssFolderSortBy" value="_creatorTitle"/>
		<c:choose>
		  <c:when test="${ ssFolderSortBy == '_creatorTitle' && ssFolderSortDescend == 'false'}">
		  	<portlet:param name="ssFolderSortDescend" value="true"/>
		  </c:when>
		  <c:otherwise>
		  	<portlet:param name="ssFolderSortDescend" value="false"/>
		  </c:otherwise>
		</c:choose>		
	</portlet:actionURL>">
		<ssf:nlt tag="folder.column.Author"/>
    <a/>

    <c:if test="${ ssFolderSortBy == '_creatorTitle' && ssFolderSortDescend == 'true'}">
		<img src="<html:imagesPath/>pics/sym_s_down.gif"/>
	</c:if>
	<c:if test="${ ssFolderSortBy == '_creatorTitle' && ssFolderSortDescend == 'false'}">
		<img src="<html:imagesPath/>pics/sym_s_up.gif"/>
	</c:if>
    
    </ssf:slidingTableColumn>
  </c:if>
  <c:if test="${!empty ssFolderColumns['date']}">
    <ssf:slidingTableColumn width="20%">
    
    <a href="<portlet:actionURL windowState="maximized" portletMode="view">
		<portlet:param name="action" value="${action}"/>
		<portlet:param name="operation" value="save_folder_sort_info"/>
		<portlet:param name="binderId" value="${ssFolder.id}"/>
		<portlet:param name="ssFolderSortBy" value="_modificationDate"/>
		<c:choose>
		  <c:when test="${ ssFolderSortBy == '_modificationDate' && ssFolderSortDescend == 'true'}">
		  	<portlet:param name="ssFolderSortDescend" value="false"/>
		  </c:when>
		  <c:otherwise>
		  	<portlet:param name="ssFolderSortDescend" value="true"/>
		  </c:otherwise>
		</c:choose>		
	</portlet:actionURL>">
		<ssf:nlt tag="folder.column.Date"/>
    <a/>

    <c:if test="${ ssFolderSortBy == '_modificationDate' && ssFolderSortDescend == 'true'}">
		<img src="<html:imagesPath/>pics/sym_s_down.gif"/>
	</c:if>
	<c:if test="${ ssFolderSortBy == '_modificationDate' && ssFolderSortDescend == 'false'}">
		<img src="<html:imagesPath/>pics/sym_s_up.gif"/>
	</c:if>
    
    </ssf:slidingTableColumn>
  </c:if>
</ssf:slidingTableRow>

<c:forEach var="entry1" items="${ssFolderEntries}" >
<jsp:useBean id="entry1" type="java.util.HashMap" />
<%
	String folderLineId = "folderLine_" + (String) entry1.get("_docId");
	String seenStyle = "";
	String seenStyleFine = "class=\"ss_finePrint\"";
	if (!ssSeenMap.checkIfSeen(entry1)) {
		seenStyle = "class=\"ss_bold\"";
		seenStyleFine = "class=\"ss_bold ss_fineprint\"";
	}
%>
<ssf:slidingTableRow id="<%= folderLineId %>">

 <c:if test="${!empty ssFolderColumns['number']}">
  <ssf:slidingTableColumn>
    <a href="<ssf:url     
    adapter="<%= useAdaptor %>" 
    portletName="ss_forum" 
    folderId="${ssFolder.id}" 
    action="view_folder_entry" 
    entryId="<%= entry1.get("_docId").toString() %>" actionUrl="true" />" 
    onClick="ss_loadEntry(this,'<c:out value="${entry1._docId}"/>');return false;" 
    ><span <%= seenStyle %>><c:out value="${entry1._docNum}"/>.</span></a>&nbsp;&nbsp;&nbsp;
  </ssf:slidingTableColumn>
 </c:if>
  
 <c:if test="${!empty ssFolderColumns['title']}">
  <ssf:slidingTableColumn>
    <a 
    class="ss_title_menu" 
    href="<ssf:url     
    adapter="<%= useAdaptor %>" 
    portletName="ss_forum" 
    folderId="${ssFolder.id}" 
    action="view_folder_entry" 
    entryId="<%= entry1.get("_docId").toString() %>" actionUrl="true" />" 
    onClick="ss_loadEntry(this, '${entry1._docId}');return false;" 
    onMouseOver="ss_linkMenu.showButton(this);"
    onMouseOut="ss_linkMenu.hideButton(this);"
    ><img class="ss_title_menu"
    onClick="ss_linkMenu.showMenu(this, '${entry1._docId}', '${ssBinder.id}', '${entry1._definitionType}');"
    src="<html:imagesPath/>pics/downarrow_off.gif"/><c:if test="${empty entry1.title}"
    ><span <%= seenStyleFine %>>--<ssf:nlt tag="entry.noTitle"/>--</span
    ></c:if><span <%= seenStyle %>><c:out value="${entry1.title}"/></span></a>
  </ssf:slidingTableColumn>
 </c:if>
  
 <c:if test="${!empty ssFolderColumns['state']}">
  <ssf:slidingTableColumn>
    <c:if test="${!empty entry1._workflowStateCaption}">
    <a href="<ssf:url     
    adapter="<%= useAdaptor %>" 
    portletName="ss_forum" 
    folderId="${ssFolder.id}" 
    action="view_folder_entry" 
    entryId="<%= entry1.get("_docId").toString() %>" actionUrl="true" />" 
    onClick="ss_loadEntry(this,'<c:out value="${entry1._docId}"/>');return false;" 
    ><span <%= seenStyle %>><c:out value="${entry1._workflowStateCaption}"/></span></a>
    </c:if>
  </ssf:slidingTableColumn>
 </c:if>
  
 <c:if test="${!empty ssFolderColumns['author']}">
  <ssf:slidingTableColumn>
	<ssf:presenceInfo user="<%=(User)entry1.get("_principal")%>"/> 
	<span <%= seenStyle %>><c:out value="${entry1._principal.title}"/></span>
  </ssf:slidingTableColumn>
 </c:if>
 <c:if test="${!empty ssFolderColumns['date']}">
  <ssf:slidingTableColumn>
    <span <%= seenStyle %>><fmt:formatDate timeZone="${ssUser.timeZone.ID}"
     value="${entry1._modificationDate}" type="both" 
	 timeStyle="short" dateStyle="short" /></span>
  </ssf:slidingTableColumn>
 </c:if>
</ssf:slidingTableRow>
</c:forEach>
</ssf:slidingTable>
<%
	if (ssUser.getDisplayStyle() == null || 
	        !ssUser.getDisplayStyle().equals(ObjectKeys.USER_DISPLAY_STYLE_VERTICAL)) {
%>
<div align="right">
  <a href="<ssf:url
	adapter="true" 
	portletName="ss_forum" 
	action="__ajax_request" 
	actionUrl="true" >
	<ssf:param name="operation" value="configure_folder_columns" />
	<ssf:param name="binderId" value="${ssBinder.id}" />
	<ssf:param name="rn" value="ss_randomNumberPlaceholder" />
	</ssf:url>" onClick="ss_createPopupDiv(this, 'ss_folder_column_menu');return false;">
    <span class="ss_fineprint ss_light"><ssf:nlt tag="misc.configureColumns"/></span></a>
</div>
<%
	}
%>
</div>
<div id="ss_emd" class="ss_link_menu">
<ul id="ss_folderMenuShowFileLink" class="ss_title_menu"><li><a href="#" 
  onClick="ss_linkMenu.showFile(); return false;"><ssf:nlt 
  tag="linkMenu.showFile"/></a></li></ul>
<ul id="ss_folderMenuShowEntryLink" class="ss_title_menu"><li><a href="#" 
  onClick="ss_linkMenu.showEntry(); return false;"><ssf:nlt 
  tag="linkMenu.showEntry"/></a></li></ul>
<ul class="ss_title_menu"><li><a href="#" 
  onClick="ss_linkMenu.currentTab(); return false;"><ssf:nlt tag="linkMenu.currentTab"/></a></li></ul>
<ul class="ss_title_menu"><li><a href="#" 
  onClick="ss_linkMenu.newTab(); return false;"><ssf:nlt tag="linkMenu.newTab"/></a></li></ul>
<ul class="ss_title_menu"><li><a href="#" 
  onClick="ss_linkMenu.newWindow(); return false;"><ssf:nlt tag="linkMenu.newWindow"/></a></li></ul>
</div>
<script type="text/javascript">
function ss_initLinkMenu() {
	ss_linkMenu.menuDiv = "ss_emd";
	ss_linkMenu.binderId = "${ssBinder.id}";
	ss_linkMenu.entityType = "folderEntry";
	ss_linkMenu.binderDefinitionType = "${ssBinder.definitionType}";
	ss_linkMenu.entryUrl = ss_placeholderEntryUrl;
	ss_linkMenu.fileUrl = ss_placeholderFileUrl;
	ss_linkMenu.menuLinkShowEntry = 'ss_folderMenuShowEntryLink';
	ss_linkMenu.menuLinkShowFile = 'ss_folderMenuShowFileLink';
}
ss_createOnLoadObj('ss_initLinkMenu', ss_initLinkMenu);
</script>



