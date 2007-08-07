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
<% //view a folder forum in accessible mode %>
<jsp:useBean id="ssSeenMap" type="com.sitescape.team.domain.SeenMap" scope="request" />

<%
//Get the folder type of this definition (folder, file, or event)
String folderViewStyle = "folder";
Element folderViewTypeEle = (Element)ssConfigElement.selectSingleNode("properties/property[@name='type']");
if (folderViewTypeEle != null) folderViewStyle = folderViewTypeEle.attributeValue("value", "folder");
%>
<c:set var="ss_folderViewStyle" value="<%= folderViewStyle %>" scope="request" />

<div id="ss_showfolder" class="ss_style ss_portlet ss_content_outer" style="display:block; margin:2px;">

 <%@ include file="/WEB-INF/jsp/common/presence_support.jsp" %>

 <% // Navigation bar %>
 <jsp:include page="/WEB-INF/jsp/definition_elements/navbar.jsp" />

 <jsp:include page="/WEB-INF/jsp/definition_elements/tabbar.jsp" />

 <%@ include file="/WEB-INF/jsp/sidebars/sidebar_dispatch.jsp" %>

 <ssf:sidebarPanel title="__definition_default_workspace" id="ss_workspace_sidebar"
    initOpen="true" sticky="true">
    Coming soon!
 </ssf:sidebarPanel>


  <% // Folder toolbar %>
 <div class="ss_content_inner">
  <%@ include file="/WEB-INF/jsp/definition_elements/folder_toolbar.jsp" %>
 </div>

 <div class="ss_content_inner">
 <% // Navigation links %>
 <%@ include file="/WEB-INF/jsp/definition_elements/navigation_links.jsp" %>

 <ssf:displayConfiguration configDefinition="${ssConfigDefinition}" 
     configElement="${ssConfigElement}" 
     configJspStyle="${ssConfigJspStyle}" />

 </div>

 <script type="text/javascript">
function ss_showForumEntryInIframe(url) {
    self.location.href = url;
    return false;
}
 </script>

</div>

<% // Footer toolbar %>
<jsp:include page="/WEB-INF/jsp/definition_elements/footer_toolbar.jsp" />
