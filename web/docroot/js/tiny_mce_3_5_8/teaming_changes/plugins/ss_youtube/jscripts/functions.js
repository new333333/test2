/* The ss_youtube plugin was written by Novell using the TinyMCE plugin development documentation as a guide. */
var orgImageWidth, orgImageHeight;

function ss_youtube_init() {
	//tinyMCE.resizeToInnerSize();

	var formObj = document.forms[0];
	var inst = tinyMCEPopup.editor;
	var elm = inst.selection.getNode();
	var action = "insert";
	var html = "";

	// Check action
	if (elm != null && elm.nodeName.toUpperCase() == "P") {
		var children = elm.getElementsByTagName("a");
		if (children.length > 0) {
			elm = children.item(0);
		}
	}
	if (elm != null && elm.nodeName.toUpperCase() == "A") action = "update";

	//formObj.insert.value = tinyMCE.getLang('lang_' + action, 'Insert', true); 

	if (action == "update") {
		// Setup form data
		var rel = ss_getAttrib(elm, 'rel');
		
		formObj.youTubeUrl.value = getRelAttr(rel, "url");
		formObj.width.value  = getRelAttr(rel, "width");
		formObj.height.value = getRelAttr(rel, "height");

		window.focus();
	} 
}


function ss_editingYouTube() {
	var inst = tinyMCEPopup.editor;
	var elm = inst.selection.getNode();
	elm = ss_getParentElement(elm, "a");
	if (elm == null) { return false; }
	return true;
}

function ss_getParentElement(ele, eleType) {
	if (ele.parentElement == null) return null;
	if (ele.parentElement.nodeName == eleType) {
		return ele.parentElement;
	} else {
		return ss_getParentElement(ele.parentElement, eleType);
	}
}

function ss_getAttrib(ele, eleType) {
	return ele.getAttribute(eleType);
}


function ss_insertYouTube() {
	var inst = tinyMCEPopup.editor;
	if (inst.youtubeNode != null) inst.youtubeNode.parentNode.removeChild(inst.youtubeNode);
	elm = inst.selection.getNode();
    var url = document.getElementById('youTubeUrl').value;
    var width = document.getElementById('width').value;
    var height = document.getElementById('height').value;

	tinyMCEPopup.execCommand("mceBeginUndoLevel");

	if (elm != null && elm.nodeName.toUpperCase() == "A") {
		setAttrib(elm, "rel", 'url=' + url + ' width=' + width + ' height=' + height);
		
	} else {
		var rootPath = self.opener.ss_rootPath;
		var html = '<a class="ss_youtube_link" rel="url=' + url;
		html += ' width=' + width + ' height=' + height + '"';
		html += ' style="padding:12px 12px; background:url('+rootPath+'images/pics/media.gif) no-repeat center;">&nbsp;</a>';
		tinyMCE.execCommand("mceInsertContent", false, html);
	}

	tinyMCEPopup.execCommand("mceEndUndoLevel");
	tinyMCEPopup.close();
}


function cancelAction() {
	tinyMCEPopup.close();
}

function getRelAttr(rel, attr) {
	var i = rel.indexOf(attr + "=");
	if (i < 0) return "";
	var s = rel.substring(i + attr.length + 1, rel.length);
	i = s.indexOf(" ");
	if (i < 0) return s;
	s = s.substring(0, i);
	return s;
}

function setAttrib(elm, attrib, value) {

	if (typeof(value) == "undefined" || value == null) {
		value = "";
	}

	if (value != "") {
		elm.setAttribute(attrib.toLowerCase(), value);

		if (attrib == "style")
			attrib = "style.cssText";

		if (attrib.substring(0, 2) == 'on')
			value = 'return true;' + value;

		if (attrib == "class")
			attrib = "className";

		eval('elm.' + attrib + "=value;");
	} else
		elm.removeAttribute(attrib);
}

function replaceSubStrAll(str, subStr, newSubStrVal) {
    if (typeof str == 'undefined') return str;
    var newStr = str;
    var i = -1
    //Prevent a possible loop by only doing 1000 passes through this loop
    while (1000) {
        i = newStr.indexOf(subStr, i);
        var lenS = newStr.length;
        var lenSS = subStr.length;
        if (i >= 0) {
            newStr = newStr.substring(0, i) + newSubStrVal + newStr.substring(i+lenSS, lenS);
            i += newSubStrVal.length
        } else {
            break;
        }
    }
    return newStr;
}

function changeHeight() {
}

function changeWidth() {
}


tinyMCEPopup.requireLangPack();
tinyMCEPopup.onInit.add(ss_youtube_init, ss_youtube_init);
