<%@ include file="/WEB-INF/jsp/common/include.jsp" %>

<%@ include file="/WEB-INF/jsp/search/search_css.jsp" %>
<%@ include file="/WEB-INF/jsp/search/search_js.jsp" %>
<!-- div class='ss_style' -->
	<div id="ss_rankings">
		<!-- Places rating - Moved to the new file -->
		<%@ include file="/WEB-INF/jsp/search/rating_places.jsp" %>

		<!-- People rating - Moved to the new file -->
		<%@ include file="/WEB-INF/jsp/search/rating_people.jsp" %>

		<!-- Tags -->
		<%@ include file="/WEB-INF/jsp/search/tags.jsp" %>
		
		<!-- Saved searches -->
		<%@ include file="/WEB-INF/jsp/search/save_search.jsp" %>
		
	</div>
	<div id="ss_content_container" class="ss_searchContainer">		<div id="ss_searchForm_spacer"></div>

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
	<div class="ss_clear"></div>
<!-- /div -->





<script type="text/javascript">

// TODO find the same method somewhere in common....
function fillMask(id, value) { 
	if (document.getElementById(id)) document.getElementById(id).value = value;
}

function createWorkflowContainer() {
	alert("Create workflow container");
}


<% /* fill the search mask form*/ %>
fillMask("searchText", "<ssf:escapeJavaScript value="${filterMap.searchText}"/>");
fillMask("searchAuthors", "<ssf:escapeJavaScript value="${filterMap.searchAuthors}"/>");
fillMask("searchTags", "<ssf:escapeJavaScript value="${filterMap.searchTags}"/>");
// filterMap.searchJoinerAnd ${filterMap.searchJoinerAnd}
<c:if test="${!empty filterMap.searchJoinerAnd && filterMap.searchJoinerAnd}">
	if (document.getElementById("searchJoinerAnd")) document.getElementById("searchJoinerAnd").checked="true";
</c:if>
<c:if test="${empty filterMap.searchJoinerAnd || !filterMap.searchJoinerAnd}">
	if (document.getElementById("searchJoinerOr")) document.getElementById("searchJoinerOr").checked="true";
</c:if>

function init() {
<c:if test="${!empty filterMap.additionalFilters.workflow}">
	createWorkflowContainer();
	var wfWidget = null;
	<c:forEach var="block" items="${filterMap.additionalFilters.workflow}" varStatus="status">
		wfWidget = ss_addWorkflow("1", "${block.searchWorkflow}", "${block.filterWorkflowStateName}");
	</c:forEach>
</c:if>
}
</script>
