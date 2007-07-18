

function ssEventEditor(prefix) {
	
	var prefix = prefix;
	
	this.toggleAllDay = function (checkboxObj, timeFormObjIds) {
		if (!checkboxObj.checked) {
			ss_show(prefix + "eventStartTime");
			ss_show(prefix + "eventEndTime");
			
			for (var i = 0; i < timeFormObjIds.length; i++) {
				var timeFormObj = document.getElementById(timeFormObjIds[i]);
				if (timeFormObj) {
					timeFormObj.value = "false";
				}
			}
			
		} else {
			ss_hide(prefix + "eventStartTime");
			ss_hide(prefix + "eventEndTime");	
			
			for (var i = 0; i < timeFormObjIds.length; i++) {
				var timeFormObj = document.getElementById(timeFormObjIds[i]);
				if (timeFormObj) {
					timeFormObj.value = "true";
				}
			}
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
