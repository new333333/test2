

function ssEventEditor(prefix) {
	
	var prefix = prefix;
	
	this.toggleAllDay = function (checkboxObj) {
		if (!checkboxObj.checked) {
			ss_show(prefix + "eventStartTime");
			ss_show(prefix + "eventEndTime");
		} else {
			ss_hide(prefix + "eventStartTime");
			ss_hide(prefix + "eventEndTime");			
		}
	}
	
	function ss_show(objId){
		var obj = document.getElementById(objId);
		if (obj && obj.style) {
			obj.style.visibility="visible";
			obj.style.display="block";
		}
	}
	
	function ss_hide(objId){
		var obj = document.getElementById(objId);
		if (obj && obj.style) {
			obj.style.visibility="hidden";
			obj.style.display="none";
		}
	}	
	
}
