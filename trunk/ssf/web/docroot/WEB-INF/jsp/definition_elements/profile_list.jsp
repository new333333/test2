<% // Profile listing %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<jsp:useBean id="ssUserFolderProperties" type="com.sitescape.team.domain.UserProperties" scope="request" />
<jsp:useBean id="ssUser" type="com.sitescape.team.domain.User" scope="request" />

<script type="text/javascript" src="<html:rootPath/>js/forum/ss_folder.js"></script>

<%
	String slidingTableStyle = "sliding";
	if (ssUser.getDisplayStyle() != null && 
	        ssUser.getDisplayStyle().equals(ObjectKeys.USER_DISPLAY_STYLE_VERTICAL)) {
		slidingTableStyle = "sliding_scrolled";
	}
	String ssFolderTableHeight = "";
	Map ssFolderPropertiesMap = ssUserFolderProperties.getProperties();
	if (ssFolderPropertiesMap != null && ssFolderPropertiesMap.containsKey("folderEntryHeight")) {
		ssFolderTableHeight = (String) ssFolderPropertiesMap.get("folderEntryHeight");
	}
	if (ssFolderTableHeight == null || ssFolderTableHeight.equals("") || 
			ssFolderTableHeight.equals("0")) ssFolderTableHeight = "400";
%>
<%@ include file="/WEB-INF/jsp/common/presence_support.jsp" %>

<div class="ss_folder">
<br />

<div style="margin:0px;">

<div align="right" style="margin:0px 4px 0px 0px;">
    
<table width="99%" border="0" cellspacing="0px" cellpadding="0px">

	<tr>
		<td align="left" width="55%">
			<%@ include file="/WEB-INF/jsp/forum/view_forum_page_navigation.jsp" %>
		</td>

		<td align="right" width="20%">&nbsp;
		</td>
	</tr>
</table>

</div>

<div class="ss_folder_border" style="position:relative; top:2; margin:2px; 
  border-top:solid #666666 1px; 
  border-right:solid #666666 1px; 
  border-left:solid #666666 1px;">
  
<table cellspacing="0" cellpadding="0" width="95%">
<tr>
<td align="center">
<%@ include file="/WEB-INF/jsp/forum/view_forum_user_filters.jsp" %>
</td>
</tr>
</table>
</div>
</div>
<ssf:slidingTable id="ss_folder_table" type="<%= slidingTableStyle %>" 
 height="<%= ssFolderTableHeight %>" folderId="${ssBinder.id}">

<ssf:slidingTableRow headerRow="true">
  <ssf:slidingTableColumn width="30%">Title</ssf:slidingTableColumn>
  <ssf:slidingTableColumn width="50%">Email</ssf:slidingTableColumn>
  <ssf:slidingTableColumn width="20%">LoginName</ssf:slidingTableColumn>
</ssf:slidingTableRow>

<c:forEach var="entry" items="${ssEntries}" >
<jsp:useBean id="entry" type="java.util.HashMap" />
<%
	String folderLineId = "";
	String docId = "";
	if (entry != null && entry.get("_docId") != null) {
		docId = (String) entry.get("_docId");
		folderLineId = "folderLine_" + docId;
	}
%>

<ssf:slidingTableRow id="<%= folderLineId %>">

  <ssf:slidingTableColumn>
<%
	if (entry.containsKey("_zonName")) {
%>
	<ssf:presenceInfo zonName="<%=(String)entry.get("_zonName")%>"/> 
<%
	} else {
		//No zon name; output a white dude.
%>
	<ssf:presenceInfo zonName=""/> 
<%
	}
%>
    <a href="<ssf:url     
    adapter="true" 
    portletName="ss_forum" 
    folderId="${ssBinder.id}" 
    action="view_profile_entry" 
    entryId="<%= docId %>" actionUrl="false" />" 
    onClick="ss_loadEntry(this,'<c:out value="${entry._docId}"/>');return false;" >
    <c:if test="${empty entry.title}">
    <span class="ss_fineprint">--no title--</span>
    </c:if>
    <span><c:out value="${entry.title}"/></span></a>
  </ssf:slidingTableColumn>
  
  <ssf:slidingTableColumn>
    <c:if test="${!empty entry._email}">
	  <a href="mailto:${entry._email}">
      <span><c:out value="${entry._email}"/></span></a>
    </c:if>
  </ssf:slidingTableColumn>

  <ssf:slidingTableColumn>
    <span><c:out value="${entry._loginName}"/></span>
  </ssf:slidingTableColumn>
  
 </ssf:slidingTableRow>
</c:forEach>
</ssf:slidingTable>
</div>
