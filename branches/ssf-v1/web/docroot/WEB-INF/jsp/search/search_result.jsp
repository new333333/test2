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
<%@ include file="/WEB-INF/jsp/common/presence_support.jsp" %>
<div class="ss_style ss_portlet_style">
	

	<%@ include file="/WEB-INF/jsp/search/search_js.jsp" %>

	<script type="text/javascript">
	
	<c:if test="${!empty ssWorkflowDefinitionMap}">
		var ss_searchWorkflows = new Array();
		var ss_searchSteps = new Array();
		<c:forEach var="wf" items="${ssWorkflowDefinitionMap}">
			ss_searchWorkflows['${wf.id}'] = '<ssf:escapeJavaScript value="${wf.title}"/>';
			<c:forEach var="step" items="${wf.steps}">
				ss_searchSteps['${wf.id}-${step.name}'] = '<ssf:escapeJavaScript value="${step.title}"/>';
			</c:forEach>
		</c:forEach>
	</c:if>
	<c:if test="${!empty ssEntryDefinitionMap}">
		var ss_searchEntries = new Array();
		var ss_searchFields = new Array();
		var ss_searchFieldsTypes = new Array();
		<c:forEach var="entry" items="${ssEntryDefinitionMap}">
			ss_searchEntries['${entry.id}'] = '<ssf:escapeJavaScript value="${entry.title}"/>';
			<c:forEach var="field" items="${entry.fields}">
				ss_searchFields['${entry.id}-<ssf:escapeJavaScript value="${field.name}"/>'] = '<ssf:escapeJavaScript value="${field.title}"/>';
				ss_searchFieldsTypes['${entry.id}-<ssf:escapeJavaScript value="${field.name}"/>'] = '<ssf:escapeJavaScript value="${field.type}"/>';
			</c:forEach>
		</c:forEach>
	</c:if>
	</script>

    <table cellpadding="0" cellspacing="0" border="0" width="100%">
    <tbody>
    <tr>
    <td valign="top" style="width: 200px">


	<% // Navigation bar %>
	<jsp:include page="/WEB-INF/jsp/definition_elements/navbar.jsp" />

	<% // Tabs %>
	<jsp:include page="/WEB-INF/jsp/definition_elements/tabbar.jsp" />
	<div class="ss_clear"></div>

	<!-- Saved searches -->
	<%@ include file="/WEB-INF/jsp/search/save_search.jsp" %>
								
	<!-- Places rating - Moved to the new file -->
	<%@ include file="/WEB-INF/jsp/search/rating_places.jsp" %>

	<!-- People rating - Moved to the new file -->
	<%@ include file="/WEB-INF/jsp/search/rating_people.jsp" %>

	<!-- Tags -->
	<%@ include file="/WEB-INF/jsp/search/tags.jsp" %>

	</td>
	<td class="ss_view_info" valign="top">

	<div id="ss_tab_data_${ss_tabs.current_tab}">

		<div id="ss_tabs_container">
			<jsp:include page="/WEB-INF/jsp/definition_elements/folder_toolbar.jsp" />
			<% // Breadcrumbs %>
			<jsp:include page="/WEB-INF/jsp/definition_elements/navigation_links.jsp" />
		
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
							
									<!-- Search result header -->
									<%@ include file="/WEB-INF/jsp/search/result_header.jsp" %>
								
									<!-- Search result list -->
									<%@ include file="/WEB-INF/jsp/search/result_list.jsp" %>
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
	<%@ include file="/WEB-INF/jsp/search/advanced_search_form_data_init.jsp" %>
</script>

