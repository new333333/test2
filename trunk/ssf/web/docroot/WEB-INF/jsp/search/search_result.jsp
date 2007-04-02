<%@ include file="/WEB-INF/jsp/common/include.jsp" %>


<script type="text/javascript">
// TODO find the same method somewhere in common....
function ss_showHide(objId){
	var obj = document.getElementById(objId);
	if (obj && obj.style) {
		if (obj.style.visibility == "visible") {
			obj.style.visibility="hidden";
			obj.style.display="none";
		} else {
			obj.style.visibility="visible";
			obj.style.display="block";
		}
	}
}
function ss_showHideDetails(ind){
	ss_showHide("summary_"+ind);
	ss_showHide("details_"+ind);
}
var ss_opendBoxTooglerSrc = "<html:imagesPath/>pics/flip_down16H.gif";
var ss_closedBoxTooglerSrc = "<html:imagesPath/>pics/flip_up16H.gif";
function ss_showHideRatingBox(id, imgObj) {
	ss_showHide(id);
	if (imgObj.src.indexOf("flip_down16H.gif") > -1) {
		imgObj.src=ss_closedBoxTooglerSrc;
	} else {
		imgObj.src=ss_opendBoxTooglerSrc;
	}
}

function goToPage(ind) {
	url="<portlet:actionURL windowState="maximized" portletMode="view">
				<portlet:param name="action" value="advanced_search"/>
				<portlet:param name="tabId" value="${tabId}"/>
				<portlet:param name="operation" value="viewPage"/>
		</portlet:actionURL>";
	url = url + "&pageNumber=" + ind;
	window.location.assign(url);
}

</script>

<style type="text/css">
#ss_rankings { float: left; width: 215px;}
#ss_content_container {
	background: transparent url(<html:imagesPath/>pics/top_left.gif) no-repeat top left;
    float: right;
    width: 520px;
    padding:0px;
    margin:0px;
}
#ss_content { 
	border-left:1px solid #afc8e3; 
	border-right:1px solid #afc8e3;
    border-bottom:1px solid #afc8e3; 
	margin:0px; padding:0px;
}
#ss_searchForm_container {margin:0px; padding:0px;}
#ss_searchForm_spacer {
	background: #e8eff7 url(<html:imagesPath/>pics/top_border.gif) repeat-x top left;
	margin:0px 0px 0px 5px;
	padding:0px;
	height:5px;
	line-height:1px; 
	font-size:0px;
	border-right:1px solid #afc8e3;
}
#ss_searchForm {
	background: #e8eff7 url(<html:imagesPath/>pics/left_border.gif) repeat-y top left;
}
#ss_searchResult li {
	border-bottom: 1px solid #cccccc;
	display:block;
	margin:12px 12px 12px 12px;
}
#ss_searchResult {
	margin:12px 0px 0px 0px;
	padding:0px;
}
#ss_searchResult_header {
	border-bottom: 1px solid #afc8e3;	
	margin-bottom:24px;
	padding:6px 24px 6px 12px;
}
#ss_searchResult_numbers {float:left;}
#ss_paginator {float:right;}

div.ss_thumbnail {float: left; width:62px; text-align:center;vertical-align:left;}
div.ss_thumbnail img {width:50px;height:50px;padding:0px margin:0px;}
div.ss_entry {float: left; width:430px;}
.ss_entryTitle {float:left;margin:0px;}
div.ss_more {float: right; width:72px; text-align:right;}
div.ss_entryDetails {  padding:6px;}
.ss_label {font-weight:bold;}

.ss_rating_box {
	margin:0px 0px 24px 0px;
	padding:0px;
	width: 215px;
}
.ss_rating_box_title {
	background: transparent url(<html:imagesPath/>pics/sidemenuhead.gif) no-repeat top left;
	margin:0px;
	padding:0px 5px 0px 5px;
	height: 16px;
}
.ss_rating_box_content {
	margin:0px;
	padding: 6px;
	border-left: 1px solid #cccccc;
	border-right: 1px solid #cccccc;
	border-bottom: 1px solid #cccccc;
	background-color: #dbe6f2;
}
img.ss_toogler {float:right;}
div.ss_rating_box_title h4 {float:left;margin:0px;}
div.ss_rating_box_content table {width:200px; border-collapse: collapse; border-spacing: 0;}
div.ss_rating_box_content th {border-bottom: 1px solid #afc8e3;text-align:center;}
div.ss_rating_box_content td {text-align:center;}
span.ss_pageNumber{margin:0px 24px 0px 24px;}
</style>

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
	<div id="ss_content_container">
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
