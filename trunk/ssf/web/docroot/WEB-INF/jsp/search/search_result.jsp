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
<%@ page import="org.kablink.teaming.web.util.GwtUIHelper" %>
<%@ page import="org.kablink.teaming.util.NLT" %>

<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
<jsp:include page="/WEB-INF/jsp/common/presence_support.jsp" />
<jsp:include page="/WEB-INF/jsp/definition_elements/popular_view_init.jsp" />
<div class="ss_style ss_portlet_style">
	
<script type="text/javascript" src="<html:rootPath/>js/forum/ss_folder.js"></script>
<jsp:include page="/WEB-INF/jsp/search/search_js.jsp" />

<script type="text/javascript">
function ss_getSearchTabId() {return ss_currentTabId;}
var ss_entriesSeen = new Array();
var ss_entryList = new Array();
var ss_entryCount = 0;
<c:forEach var="entry" items="${ssFolderEntries}" >
  <c:if test="${entry._entityType != 'folder' && entry._entityType != 'workspace' && entry._entityType != 'group'}">
    if (typeof ss_entriesSeen['docId${entry._docId}'] == "undefined") {
    	ss_entryList[ss_entryCount++] = { 
    		index : '${entry._docId}',
    		entryId : '${entry._docId}',
    		binderId : '${entry._binderId}',
    		entityType : '${entry._entityType}'
		};
    	ss_entriesSeen['docId${entry._docId}'] = 1;
    }
  </c:if>
</c:forEach>
</script>

<ssf:skipLink tag='<%= NLT.get("skip.header.toContent") %>' id="headerToContent_${renderResponse.namespace}"
  linkOnly="true"/>
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
<% if (!(GwtUIHelper.isGwtUIActive(request))) { %>
<div class="ss_actions_bar1_pane ss_sidebarImage" width="100%">
<table cellspacing="0" cellpadding="0" width="100%">
<tr><td valign="middle">
<a href="javascript: ;" 
  onClick="ss_showHideSidebar('${renderResponse.namespace}');return false;"
><span style="padding-left:12px; display:${ss_sidebarVisibilityShow};"
  id="ss_sidebarHide${renderResponse.namespace}" 
  class="ss_fineprint ss_sidebarSlidesm ss_sidebarSlidetext"><ssf:nlt tag="toolbar.sidebar.show"/></span><span 
  style="padding-left:12px; display:${ss_sidebarVisibilityHide};"
  id="ss_sidebarShow${renderResponse.namespace}" 
  class="ss_fineprint ss_sidebarSlide ss_sidebarSlidetext"><ssf:nlt tag="toolbar.sidebar.hide"/></span></a>
</td><td valign="middle">
<jsp:include page="/WEB-INF/jsp/definition_elements/folder_toolbar.jsp" />
</td></tr>
</table>
</div>
<% } %>
    <table cellpadding="0" cellspacing="0" border="0" width="100%">
    <tbody>
    <tr>
	<td>&nbsp;</td>
	<td class="ss_view_info" valign="top">

	<div id="ss_whatsNewDiv${ss_namespace}">
	<c:if test="${ss_type == 'whatsNew' || ss_type == 'unseen'}">
	<jsp:include page="/WEB-INF/jsp/forum/whats_new_page.jsp" />
	</c:if>
	</div>
	<div>

		<div id="ss_tabs_container">

			<div id="ss_tab_content">
				
						<div id="ss_content_container">
				
							<div class="ss_searchContainer">
								<div id="ss_content">
									<c:if test="${quickSearch}">\
										<!-- Quick search form -->
										<%@ include file="/WEB-INF/jsp/search/quick_search_form.jsp" %>
									</c:if>
									<c:if test="${!quickSearch}">
										<!-- Advanced search form -->
										<%@ include file="/WEB-INF/jsp/search/advanced_search_form.jsp" %>
									</c:if>		
							
									<!-- Search results navigation -->
									<div class="ss_searchResult_header_top ssPageNavi">
										<c:set var="ss_results_navigation_count" value="1"/>
										<%@ include file="/WEB-INF/jsp/search/results_navigation.jsp" %>
									</div>
																
<ssf:skipLink tag='<%= NLT.get("skip.header.toContent") %>' id="headerToContent_${renderResponse.namespace}"
  anchorOnly="true"/>
									<!-- Search result list -->
									<c:set var="ssFolderEntriesResults" value="${ssFolderEntries}" />
									<%@ include file="/WEB-INF/jsp/search/result_list.jsp" %>

									<c:if test="${ss_pageNumber != 1 || ssPageEndIndex != ssTotalRecords}">
										<!-- Search results navigation -->
										<div class="ss_searchResult_header_bottom ssPageNavi">
											<c:set var="ss_results_navigation_count" value="2"/>
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

