//Routines to support showing an entry

function ss_saveRating(rating, id) {
	ss_setupStatusMessageDiv()
	var ratingDiv = "ss_rating_div_" + id
	var url = ss_saveRatingUrl;
	var ajaxRequest = new AjaxRequest(url); //Create AjaxRequest object
	ajaxRequest.addKeyValue("rating", rating)
	ajaxRequest.addKeyValue("ratingDivId", ratingDiv)
	ajaxRequest.addKeyValue("entryId", id)
	ajaxRequest.addKeyValue("binderId", ss_binderId)
	ajaxRequest.setData("entryId", id)
	//ajaxRequest.setEchoDebugInfo();
	ajaxRequest.setPostRequest(ss_postSaveRatingRequest);
	ajaxRequest.setUsePOST();
	ajaxRequest.sendRequest();  //Send the request
}
function ss_postSaveRatingRequest(obj) {
	//See if there was an error
	if (self.document.getElementById("ss_status_message").innerHTML == "error") {
		alert(ss_not_logged_in);
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
