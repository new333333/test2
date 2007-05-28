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
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
<%@ include file="/WEB-INF/jsp/common/presence_support.jsp" %>
<div class="ss_style ss_portlet_style">
	

	<%@ include file="/WEB-INF/jsp/search/search_js.jsp" %>

	<script type="text/javascript">
	
	<c:if test="${!empty ssWorkflowDefinitionMap}">
		var ss_searchWorkflows = new Array();
		var ss_searchSteps = new Array();
		<c:forEach var="wf" items="${ssWorkflowDefinitionMap}">
			ss_searchWorkflows['${wf.id}'] = '${wf.title}';
			<c:forEach var="step" items="${wf.steps}">
				ss_searchSteps['${wf.id}-${step.name}'] = '${step.title}';
			</c:forEach>
		</c:forEach>
	</c:if>
	<c:if test="${!empty ssEntryDefinitionMap}">
		var ss_searchEntries = new Array();
		var ss_searchFields = new Array();
		var ss_searchFieldsTypes = new Array();
		<c:forEach var="entry" items="${ssEntryDefinitionMap}">
			ss_searchEntries['${entry.id}'] = '${entry.title}';
			<c:forEach var="field" items="${entry.fields}">
				ss_searchFields['${entry.id}-${field.name}'] = '${field.title}';
				ss_searchFieldsTypes['${entry.id}-${field.name}'] = '${field.type}';
			</c:forEach>
		</c:forEach>
	</c:if>
	</script>


	<% // Navigation bar %>
	<jsp:include page="/WEB-INF/jsp/definition_elements/navbar.jsp" />

	<% // Tabs %>
	<jsp:include page="/WEB-INF/jsp/definition_elements/tabbar.jsp" />
	<div class="ss_clear"></div>

	<div id="ss_tab_data_${ss_tabs.current_tab}">

		<div id="ss_tabs_container">
			<ul class="ss_actions_bar ss_actions_bar1"></ul>
			<% // Breadcrumbs %>
			<jsp:include page="/WEB-INF/jsp/definition_elements/navigation_links.jsp" />
		
			<div id="ss_tab_content">
				
				<table width="99%" cellpadding="0" cellspacing="0">
					<% // used table couse of IE problem with positioning of div with margin-left: -x% attribute placed in a table %>
					<tr>
						<td id="ss_rankings" >
							<div>
								<!-- Saved searches -->
								<%@ include file="/WEB-INF/jsp/search/save_search.jsp" %>
															
								<!-- Places rating - Moved to the new file -->
								<%@ include file="/WEB-INF/jsp/search/rating_places.jsp" %>
						
								<!-- People rating - Moved to the new file -->
								<%@ include file="/WEB-INF/jsp/search/rating_people.jsp" %>
						
								<!-- Tags -->
								<%@ include file="/WEB-INF/jsp/search/tags.jsp" %>
							</div>
						</td>
						
						<td id="ss_content_container">
				
							<div class="ss_searchContainer">
								<div id="ss_searchForm_spacer"></div>
						
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
						</td>
					</tr>
				</table>			
					
			</div>
			
		</div>
	</div>
</div>

<script type="text/javascript">
	<%@ include file="/WEB-INF/jsp/search/advanced_search_form_data_init.jsp" %>
</script>

