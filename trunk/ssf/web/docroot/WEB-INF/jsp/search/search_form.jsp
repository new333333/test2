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

<%@ include file="/WEB-INF/jsp/search/search_js.jsp" %>

<div id="ss_portlet_content" class="ss_style ss_portlet_style ss_content_outer">
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
	