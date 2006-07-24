<c:if test="${empty ss_ratingSupportLoaded}">
<script type="text/javascript">
var ss_ratingImages = new Array();
var ss_ratingRedStar = "<html:imagesPath/>pics/star_red.gif";
var ss_ratings_info = new Array();
var ss_currentRatingInfoId = "";

ss_ratings_info[1] = "<ssf:nlt tag="popularity.rating.1star" />"
ss_ratings_info[2] = "<ssf:nlt tag="popularity.rating.2stars" />"
ss_ratings_info[3] = "<ssf:nlt tag="popularity.rating.3stars" />"
ss_ratings_info[4] = "<ssf:nlt tag="popularity.rating.4stars" />"
ss_ratings_info[5] = "<ssf:nlt tag="popularity.rating.5stars" />"
function ss_saveRating(rating, id) {
	ss_setupStatusMessageDiv()
	var ratingDiv = "ss_rating_div_" + id
	var url = "<ssf:url 
    	adapter="true" 
    	portletName="ss_forum" 
    	action="__ajax_request" 
    	actionUrl="true" >
		<ssf:param name="operation" value="save_rating" />
    	</ssf:url>"
	var ajaxRequest = new AjaxRequest(url); //Create AjaxRequest object
	ajaxRequest.addKeyValue("rating", rating)
	ajaxRequest.addKeyValue("ratingDivId", ratingDiv)
	ajaxRequest.addKeyValue("entryId", id)
	ajaxRequest.addKeyValue("binderId", "${ssBinder.id}")
	ajaxRequest.setData("entryId", id)
	//ajaxRequest.setEchoDebugInfo();
	ajaxRequest.setPostRequest(ss_postSaveRatingRequest);
	ajaxRequest.setUsePOST();
	ajaxRequest.sendRequest();  //Send the request
}
function ss_postSaveRatingRequest(obj) {
	//See if there was an error
	if (self.document.getElementById("ss_status_message").innerHTML == "error") {
		alert("<ssf:nlt tag="general.notLoggedIn" text="Your session has timed out. Please log in again."/>");
	}
	if (obj.getData('entryId') == ss_currentRatingInfoId) {
		var infoDiv = document.getElementById("ss_rating_info_div")
    	infoDiv.style.display = "none"
    	infoDiv.style.visibility = "hidden"
    	ss_currentRatingInfoId = "";
	}
}
function ss_showRating(rating, id) {
	var iRating = parseInt(rating);
	for (var i = 1; i <= iRating; i++) {
		var imgId = "ss_rating_img_" + id + "_" + i;
		var imgObj = document.getElementById(imgId)
		if (ss_ratingImages[imgId] == null) ss_ratingImages[imgId] = imgObj.src;
		imgObj.src = ss_ratingRedStar;
	}
	ss_moveDivToBody("ss_rating_info_div")
	var infoDiv = document.getElementById("ss_rating_info_div")
	var infoSpan = document.getElementById("ss_rating_info")
	infoSpan.innerHTML = ss_ratings_info[rating];
    infoDiv.style.left = parseInt(parseInt(ss_getImageLeft(imgId)) + 20) + "px";
    infoDiv.style.top = parseInt(parseInt(ss_getImageTop(imgId)) - 30) + "px";
    infoDiv.style.display = "block"
    infoDiv.style.visibility = "visible"
    ss_currentRatingInfoId = id;
}
function ss_clearRating(rating, id) {
	var iRating = parseInt(rating);
	for (var i = 1; i <= iRating; i++) {
		var imgId = "ss_rating_img_" + id + "_" + i;
		var imgObj = document.getElementById(imgId)
		if (ss_ratingImages[imgId] != null) imgObj.src = ss_ratingImages[imgId];
	}
	var infoDiv = document.getElementById("ss_rating_info_div")
    infoDiv.style.display = "none"
    infoDiv.style.visibility = "hidden"
    ss_currentRatingInfoId = "";
}
</script>
<div id="ss_rating_info_div" 
  style="position:absolute; display:none; visibility:hidden;
  border:1px solid black; padding:4px; background-color:#ffffff;">
<span><ssf:nlt tag="popularity.rating.register"/></span>:
<span id="ss_rating_info"></span>
</div>
<c:set var="ss_ratingSupportLoaded" value="1" scope="request"/>
</c:if>
<c:set var="ss_ratingDivId" value="ss_rating_div_${ssDefinitionEntry.id}" 
  scope="request"/>
<table cellspacing="0" cellpadding="0">
<tr>
<td valign="middle" nowrap>
<%@ include file="/WEB-INF/jsp/forum/rating.jsp" %>
</td>
<td>&nbsp;&nbsp;&nbsp;</td>
<td valign="middle" nowrap>
<span class="ss_bold ss_smallprint">
<ssf:nlt tag="popularity.visits" text="Visits"/>:</span>
<c:if test="${!empty ssDefinitionEntry.popularity}">
<span class="ss_smallprint"><c:out value="${ssDefinitionEntry.popularity}"/></span>
</c:if>
<c:if test="${empty ssDefinitionEntry.popularity}">
<span class="ss_smallprint"><ssf:nlt tag="popularity.visits.none" /></span>
</c:if>
</td>
</tr>
</table>
