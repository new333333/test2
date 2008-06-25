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
<c:if test="${ssDefinitionEntry.entityType == 'folder'}">

<ssf:sidebarPanel title="sidebar.tags.tools" id="ss_tooltags_sidebar" divClass="ss_place_tags" initOpen="false" sticky="true">

<em>Add a Filter, Show Entries, Folder View, and Folder Action to go here</em>
<hr>
<div>
	<ul style="padding-top: 2px; padding-left: 5px;">
	<li>
		<c:if test="${ssConfigJspStyle != 'template'}">
		<a href="<ssf:url ><ssf:param 
			name="action" value="build_filter"/><ssf:param 
			name="binderId" value="${ssBinder.id}"/><ssf:param 
			name="binderType" value="${ssBinder.entityType}"/></ssf:url>"
		><span class="ss_tabs_title"><ssf:nlt tag="sidebar.tags.filter" text="Add a Filter"/></span></a>
		</c:if>
		<c:if test="${ssConfigJspStyle == 'template'}">
		<span class="ss_tabs_title"><ssf:nlt tag="sidebar.tags.filter" text="Add a Filter"/></span>
		</c:if>
	</li>
	<li>

		<% // configure columns area %>
	 	<a href="<ssf:url
			adapter="true" 
			portletName="ss_forum" 
			action="__ajax_request" 
			actionUrl="true" >
			<ssf:param name="operation" value="configure_folder_columns" />
			<ssf:param name="binderId" value="${ssBinder.id}" />
			<ssf:param name="rn" value="ss_randomNumberPlaceholder" />
			</ssf:url>" onClick="ss_createPopupDiv(this, 'ss_folder_column_menu');return false;">
		    <span class="ss_tabs_title"><ssf:nlt tag="misc.configureColumns"/></span> </a> 

	</li>
	</ul>	
</div>
			
			<% // Show number of entries per page %>
			<form name="ss_recordsPerPage_${renderResponse.namespace}" id="ss_recordsPerPage_${renderResponse.namespace}" method="post" 
			    action="<ssf:url action="${action}" actionUrl="true"><ssf:param 
				name="binderId" value="${ssFolder.id}"/>
				<c:if test="${!empty cTag}"><ssf:param 
				name="cTag" value="${cTag}"/></c:if><c:if test="${!empty pTag}"><ssf:param 
				name="pTag" value="${pTag}"/></c:if><c:if test="${!empty yearMonth}"><ssf:param 
				name="yearMonth" value="${yearMonth}"/></c:if><c:if test="${!empty endDate}"><ssf:param 
				name="endDate" value="${endDate}"/></c:if><ssf:param 
				name="operation" value="change_entries_on_page"/></ssf:url>">
			    
			    <input type="hidden" name="ssEntriesPerPage" />
			
				<div class="ss_results_pro_page">
				  <span class="ss_tabs_title">
	
				  <ssf:menu title="${ssPageMenuControlTitle}" 
				    titleId="ss_selectEntriesTitle${renderResponse.namespace}" 
				    titleClass="ss_compact" menuClass="ss_actions_bar4 ss_actions_bar_submenu" menuImage="pics/menudown.gif">
				
				    <ssf:ifnotaccessible>
				
					<ul class="ss_actions_bar4 ss_actions_bar_submenu" style="width:150px;">
					<li>
						<a href="javascript: ;" onClick="ss_changePageEntriesCount_${renderResponse.namespace}('ss_recordsPerPage_${renderResponse.namespace}', '5');return false;">
							<ssf:nlt tag="folder.Page"><ssf:param name="value" value="5"/></ssf:nlt>
						</a>
					</li>
					<li>	
						<a href="javascript: ;" onClick="ss_changePageEntriesCount_${renderResponse.namespace}('ss_recordsPerPage_${renderResponse.namespace}', '10');return false;">
							<ssf:nlt tag="folder.Page"><ssf:param name="value" value="10"/></ssf:nlt>
						</a>
					</li>
					<li>
						<a href="javascript: ;" onClick="ss_changePageEntriesCount_${renderResponse.namespace}('ss_recordsPerPage_${renderResponse.namespace}', '25');return false;">
							<ssf:nlt tag="folder.Page"><ssf:param name="value" value="25"/></ssf:nlt>
						</a>
					</li>
					<li>
						<a href="javascript: ;" onClick="ss_changePageEntriesCount_${renderResponse.namespace}('ss_recordsPerPage_${renderResponse.namespace}', '50');return false;">
							<ssf:nlt tag="folder.Page"><ssf:param name="value" value="50"/></ssf:nlt>
						</a>
					</li>
					<li>
						<a href="javascript: ;" onClick="ss_changePageEntriesCount_${renderResponse.namespace}('ss_recordsPerPage_${renderResponse.namespace}', '100');return false;">
							<ssf:nlt tag="folder.Page"><ssf:param name="value" value="100"/></ssf:nlt>
						</a>
					</li>
					</ul>
					
				    </ssf:ifnotaccessible>	
				
				    <ssf:ifaccessible>

					<a href="javascript: ;" onClick="ss_changePageEntriesCount_${renderResponse.namespace}('ss_recordsPerPage_${renderResponse.namespace}', '5');return false;"
					title="<ssf:nlt tag="folder.Page"><ssf:param name="value" value="5"/></ssf:nlt>">
						<ssf:nlt tag="folder.Page"><ssf:param name="value" value="5"/></ssf:nlt>
					</a><br/>

					<a href="javascript: ;" onClick="ss_changePageEntriesCount_${renderResponse.namespace}('ss_recordsPerPage_${renderResponse.namespace}', '10');return false;"
					title="<ssf:nlt tag="folder.Page"><ssf:param name="value" value="10"/></ssf:nlt>">
						<ssf:nlt tag="folder.Page"><ssf:param name="value" value="10"/></ssf:nlt>
					</a><br/>

					<a href="javascript: ;" onClick="ss_changePageEntriesCount_${renderResponse.namespace}('ss_recordsPerPage_${renderResponse.namespace}', '25');return false;"
					title="<ssf:nlt tag="folder.Page"><ssf:param name="value" value="25"/></ssf:nlt>">
						<ssf:nlt tag="folder.Page"><ssf:param name="value" value="25"/></ssf:nlt>
					</a><br/>

					<a href="javascript: ;" onClick="ss_changePageEntriesCount_${renderResponse.namespace}('ss_recordsPerPage_${renderResponse.namespace}', '50');return false;"
					title="<ssf:nlt tag="folder.Page"><ssf:param name="value" value="50"/></ssf:nlt>">
						<ssf:nlt tag="folder.Page"><ssf:param name="value" value="50"/></ssf:nlt>
					</a><br/>

					<a href="javascript: ;" onClick="ss_changePageEntriesCount_${renderResponse.namespace}('ss_recordsPerPage_${renderResponse.namespace}', '100');return false;"
					title="<ssf:nlt tag="folder.Page"><ssf:param name="value" value="100"/></ssf:nlt>">
						<ssf:nlt tag="folder.Page"><ssf:param name="value" value="100"/></ssf:nlt>
					</a><br/>

				    </ssf:ifaccessible>
					
				  </ssf:menu>

			    </span>
			    </div>
			</form>
</ssf:sidebarPanel>
<ssf:sidebarPanel title="sidebar.tags.folder" id="ss_placetags_sidebar" divClass="ss_place_tags" initOpen="false" sticky="true">

  <c:set var="ss_tagObject" value="${ssDefinitionEntry}" scope="request"/>
  <%@ include file="/WEB-INF/jsp/definition_elements/tag_view.jsp" %>
</ssf:sidebarPanel>
</c:if>
<c:if test="${ssDefinitionEntry.entityType == 'workspace'}">
<ssf:sidebarPanel title="relevance.userStatus" id="ss_status_sidebar" divClass="ss_place_tags" initOpen="false" sticky="true">
<ssf:ifLoggedIn>
<script type="text/javascript">
ss_statusCurrent = "${ssUser.status}";
</script>

<input type="text" size="42" style="font-size:9px; background-color:#e6e6e6;" value="${ssUser.status}"
  onFocus="ss_setStatusBackground(this, 'focus');"
  onKeyPress="ss_updateStatusSoon(this, event);"
  onChange="ss_updateStatusNow(this);"
  onBlur="ss_updateStatusNow(this);ss_setStatusBackground(this, 'blur')"
  onMouseover="ss_setStatusBackground(this, 'mouseOver');"
  onMouseout="ss_setStatusBackgroundCheck(this);"
  />

</ssf:ifLoggedIn> 
</ssf:sidebarPanel>
<ssf:sidebarPanel title="sidebar.tags.workspace" id="ss_workspace_sidebar" divClass="ss_place_tags" initOpen="false" sticky="true">
  <c:set var="ss_tagObject" value="${ssDefinitionEntry}" scope="request"/>
  <%@ include file="/WEB-INF/jsp/definition_elements/tag_view.jsp" %>
</ssf:sidebarPanel>
</c:if>