
function ss_buddyPhotoLoadError (imgObj, innerHTML) {
	var parentObj = imgObj.parentNode;
	parentObj.removeChild(imgObj);
	parentObj.innerHTML = innerHTML;
	parentObj.parentNode.className = parentObj.parentNode.className + " noImg";
}
