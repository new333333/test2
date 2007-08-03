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

    <table cellpadding="0" cellspacing="0" border="0" width="100%">
    <tbody>
    <tr>
    <td valign="top" class="ss_view_sidebar">

	<% // Navigation bar %>
	<jsp:include page="/WEB-INF/jsp/definition_elements/navbar.jsp" />

	<% // Tabs %>
	<jsp:include page="/WEB-INF/jsp/definition_elements/tabbar.jsp" />

	<%@ include file="/WEB-INF/jsp/search/save_search.jsp" %>


	</td>
	<td valign="top" class="ss_view_info">
			
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

		</div>
	</td>
    </tr>
    </tbody>
    </table>
</div>
<script type="text/javascript">
	<%@ include file="/WEB-INF/jsp/search/advanced_search_form_data_init.jsp" %>
</script>
	