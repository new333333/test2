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
<% //view a folder forum with folder on the left and the entry on the right in an iframe %>
<%@ include file="/WEB-INF/jsp/common/common.jsp" %>
<jsp:useBean id="ssSeenMap" type="com.sitescape.team.domain.SeenMap" scope="request" />
<%@ page import="com.sitescape.team.module.definition.DefinitionUtils" %>
<jsp:useBean id="ssConfigDefinition" type="org.dom4j.Document" scope="request" />
<%
//Get the folder type of this definition (folder, file, or event)
String folderViewStyle = DefinitionUtils.getViewType(ssConfigDefinition);
if (folderViewStyle == null || folderViewStyle.equals("")) folderViewStyle = "folder";
%>
<c:set var="ss_folderViewStyle" value="<%= folderViewStyle %>" scope="request" />
<c:set var="ss_sidebarVisibility" value="${ssUserProperties.sidebarVisibility}" scope="request"/>
<c:if test="${empty ss_sidebarVisibility}"><c:set var="ss_sidebarVisibility" value="block" scope="request"/></c:if>
<c:if test="${ss_sidebarVisibility == 'none'}">
  <c:set var="ss_sidebarVisibilityShow" value="block"/>
  <c:set var="ss_sidebarVisibilityHide" value="none"/>
  <c:set var="ss_sidebarTdStyle" value=""/>
</c:if>
<c:if test="${ss_sidebarVisibility != 'none'}">
  <c:set var="ss_sidebarVisibilityShow" value="none"/>
  <c:set var="ss_sidebarVisibilityHide" value="block"/>
  <c:set var="ss_sidebarTdStyle" value="ss_view_sidebar"/>
</c:if>

<div id="ss_showfolder${renderResponse.namespace}" class="ss_style ss_portlet ss_content_outer">
<jsp:include page="/WEB-INF/jsp/common/presence_support.jsp" />
<jsp:include page="/WEB-INF/jsp/definition_elements/popular_view_init.jsp" />
<jsp:include page="/WEB-INF/jsp/forum/view_workarea_navbar.jsp" />
<div class="ss_actions_bar1_pane ss_sidebarImage">
<table cellspacing="0" cellpadding="0">
<tr>
<ssf:ifnotaccessible>
<c:if test="${!ss_mashupHideSidebar}">
<td valign="middle">
<a href="javascript: ;" 
  onClick="ss_showHideSidebar('${renderResponse.namespace}');return false;"
><span style="padding-left:12px; display:${ss_sidebarVisibilityShow};"
  id="ss_sidebarHide${renderResponse.namespace}" 
  class="ss_fineprint ss_sidebarSlidesm ss_sidebarSlidetext"><ssf:nlt tag="toolbar.sidebar.show"/></span><span 
  style="padding-left:12px; display:${ss_sidebarVisibilityHide};"
  id="ss_sidebarShow${renderResponse.namespace}" 
  class="ss_fineprint ss_sidebarSlide ss_sidebarSlidetext"><ssf:nlt tag="toolbar.sidebar.hide"/></span></a>
</td>
</c:if>
</ssf:ifnotaccessible>
<td valign="middle">
<jsp:include page="/WEB-INF/jsp/definition_elements/folder_toolbar.jsp" />
</td></tr>
</table>
</div>
<div width="100%">
    <ssf:ifnotaccessible>
    <table cellpadding="0" cellspacing="0" border="0" width="100%">
    <tbody>
    <tr>
    <c:if test="${!ss_mashupHideSidebar}">
    <td valign="top" class="${ss_sidebarTdStyle}" id="ss_sidebarTd${renderResponse.namespace}">
		<jsp:include page="/WEB-INF/jsp/sidebars/sidebar.jsp" />
	</td>
	</c:if>
	<c:if test="${!ss_mashupHideToolbar}">
	<td valign="top" class="ss_view_info">
	</ssf:ifnotaccessible>
	  <jsp:include page="/WEB-INF/jsp/definition_elements/folder_entry_toolbar.jsp" />
	  <div align="center">
	    <div id="ss_folder_wrap">
		  <div id="ss_whatsNewDiv${ss_namespace}">
		  <c:if test="${!empty ss_whatsNewBinder || ss_pageNumber > '0'}">
		  <%@ include file="/WEB-INF/jsp/forum/whats_new_page.jsp" %>
		  </c:if>
		  </div>
	      <div id="ss_folder_type_${ssDefinitionFamily}" class="ss_style_color" >
			<ssf:displayConfiguration configDefinition="${ssConfigDefinition}" 
					  configElement="${ssConfigElement}" 
					  configJspStyle="${ssConfigJspStyle}" 
					  entry="${ssBinder}" />
		  </div>
		  <% // Footer toolbar %>
		  <c:if test="${!ss_mashupHideFooter}">
		    <jsp:include page="/WEB-INF/jsp/definition_elements/footer_toolbar.jsp" />
		  </c:if>
		  
		  <c:if test="${ss_mashupShowAlternateToolbar}">
		    <jsp:include page="/WEB-INF/jsp/definition_elements/folder_toolbar.jsp" />
		  </c:if>
		</div>
	  </div>
	<ssf:ifnotaccessible>
	</td>
	</c:if>
	</tr>
	</tbody>
	</table>
	</ssf:ifnotaccessible>
</div>
<ssf:ifaccessible>
  <div>
	<jsp:include page="/WEB-INF/jsp/sidebars/sidebar.jsp" />
  </div>
</ssf:ifaccessible>
</div>
<script type="text/javascript">
ss_createOnLoadObj('ss_initShowFolderDiv${renderResponse.namespace}', ss_initShowFolderDiv('${renderResponse.namespace}'));
</script>

<c:if test="${!empty ssEntryIdToBeShown && !empty ss_useDefaultViewEntryPopup}">
	<script type="text/javascript">
		function ss_showEntryToBeShown${renderResponse.namespace}() {
		    var url = "<ssf:url     
				adapter="true" 
				portletName="ss_forum" 
				folderId="${ssBinder.id}" 
				action="view_folder_entry" 
				entryId="${ssEntryIdToBeShown}" 
				actionUrl="true" />" 
			ss_showForumEntryInIframe(url);
		}
		ss_createOnLoadObj('ss_showEntryToBeShown${renderResponse.namespace}', ss_showEntryToBeShown${renderResponse.namespace});
	</script>
</c:if>

