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
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
<jsp:include page="/WEB-INF/jsp/common/presence_support.jsp" />
<jsp:include page="/WEB-INF/jsp/definition_elements/popular_view_init.jsp" />
<div class="ss_style ss_portlet_style">
	

	<jsp:include page="/WEB-INF/jsp/search/search_js.jsp" />


<jsp:include page="/WEB-INF/jsp/forum/view_workarea_navbar.jsp" />
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
<div class="ss_actions_bar1_pane ss_sidebarImage" width="100%">
<table cellspacing="0" cellpadding="0" width="100%">
<tr><td valign="middle">
<a href="javascript: ;" 
  onClick="ss_showHideSidebar('${renderResponse.namespace}');return false;"
><span style="padding-left:9px; display:${ss_sidebarVisibilityShow};"
  id="ss_sidebarHide${renderResponse.namespace}" 
  class="ss_fineprint ss_sidebarSlidesm"><ssf:nlt tag="toolbar.sidebar.show"/></span><span 
  style="padding-left:9px; display:${ss_sidebarVisibilityHide};"
  id="ss_sidebarShow${renderResponse.namespace}" 
  class="ss_fineprint ss_sidebarSlide"><ssf:nlt tag="toolbar.sidebar.hide"/></span></a>
</td><td valign="top">
<jsp:include page="/WEB-INF/jsp/definition_elements/folder_toolbar.jsp" />
</td></tr>
</table>
</div>
    <table cellpadding="0" cellspacing="0" border="0" width="100%">
    <tbody>
    <tr>
    <td valign="top" class="${ss_sidebarTdStyle}" id="ss_sidebarTd${renderResponse.namespace}">
		<c:set var="ss_searchResultsPage" value="true" scope="request" />
		<jsp:include page="/WEB-INF/jsp/sidebars/sidebar.jsp" />
	</td>
	<td class="ss_view_info" valign="top">

	<div>

		<div id="ss_tabs_container">

			<div id="ss_tab_content">
				
						<div id="ss_content_container">
				
							<div class="ss_searchContainer">
								<div id="ss_content">
									<c:if test="${quickSearch}">
										<!-- Quick search form -->
										<%@ include file="/WEB-INF/jsp/search/quick_search_form.jsp" %>
									</c:if>
									<c:if test="${!quickSearch}">
										<!-- Advanced search form -->
										<%@ include file="/WEB-INF/jsp/search/advanced_search_form.jsp" %>
									</c:if>		
							
									<!-- Search results navigation -->
									<div class="ss_searchResult_header_top ssPageNavi">
										<%@ include file="/WEB-INF/jsp/search/results_navigation.jsp" %>
									</div>
																
									<!-- Search result list -->
									<%@ include file="/WEB-INF/jsp/search/result_list.jsp" %>

									<c:if test="${ss_pageNumber != 1 || ssPageEndIndex != ssTotalRecords}">
										<!-- Search results navigation -->
										<div class="ss_searchResult_header_bottom ssPageNavi">
											<%@ include file="/WEB-INF/jsp/search/results_navigation.jsp" %>
										</div>
									</c:if>
								</div>
							</div>
						</div>
			</div>
		</div>
		
		<% // Footer toolbar %>
		<jsp:include page="/WEB-INF/jsp/definition_elements/footer_toolbar.jsp" />
			
	</div>
	</td>
	</tr>
	</tbody>
	</table>
</div>

<script type="text/javascript">
	<jsp:include page="/WEB-INF/jsp/search/advanced_search_form_data_init.jsp" />
</script>

