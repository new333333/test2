<%@ include file="/WEB-INF/jsp/common/include.jsp" %>

<%@ include file="/WEB-INF/jsp/search/search_css.jsp" %>
<%@ include file="/WEB-INF/jsp/search/search_js.jsp" %>
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

<script type="text/javascript">
// Init empty additional options
/*
ss_addOption('workflow');
ss_addOption('entry');
ss_addOption('tag');
ss_addOption('creation_date');
ss_addOption('modification_date');
ss_addOption('creator_by_id');
*/
</script>
	