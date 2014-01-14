/* The ss_wikilink plugin was written by SiteScape using the TinyMCE plugin development documentation as a guide. */
function init() {
	//tinyMCEPopup.resizeToInnerSize();
}

function ss_editingICElink() {
	var inst = tinyMCEPopup.editor;
	var elm = inst.selection.getNode();
	elm = ss_getParentElement(elm, "a");
	if (elm == null) { return false; }
	return true;
}

function ss_insertICElink(originalBinderId, title, currentBinderId) {
	//currentBinderId = 'xxx'
	var link = "";
	var inst = tinyMCEPopup.editor;
	//if (inst.wikilinkNode != null) inst.wikilinkNode.parentNode.removeChild(inst.wikilinkNode);
	elm = inst.selection.getNode();
    var linkText = inst.selection.getContent();
    if (linkText == null || linkText == "") {
    	var linkTextObj = document.getElementById('searchTitle');
    	if (linkTextObj != null) linkText = linkTextObj.value;
    	if (linkText == null || linkText == '') {
    		linkTextObj = document.getElementById('pageName');
    		if (linkTextObj != null) linkText = linkTextObj.value;
    	}
    	if (linkText == null || linkText == '') {
    		linkTextObj = document.getElementById('searchTitleFolder');
    		if (linkTextObj != null) linkText = linkTextObj.value;
    	}
    }
    var pad = "";
    if (linkText.charAt(linkText.length - 1) == " ") { pad = " "; }
    linkText = ss_wikilink_trim(linkText);

	elm = ss_getParentElement(elm, "a");

	tinyMCEPopup.execCommand("mceBeginUndoLevel");

	// Create new anchor elements
	if (elm == null || elm == '') {
	    if (originalBinderId == "") { originalBinderId == currentBinderId };
	    if (linkText == "") { linkText = title; pad = " "; }
	    if (title != "") { linkText = title; }
		link = '<a href="#" class="ss_icecore_link" rel="binderId=' + currentBinderId + ' title=' + ss_prenormalizeText(title) + '">' + linkText + '</a>' + pad;
		tinyMCE.execCommand('mceInsertContent', false, link);
	} else {
		setAttrib(elm, "rel", 'binderId=' + currentBinderId + ' title=' + ss_prenormalizeText(title));
		setAttrib(elm, "class", "ss_icecore_link");
		if (inst.wikilinkNode != null) inst.wikilinkNode.innerHTML = linkText;
	}

	tinyMCEPopup.execCommand("mceEndUndoLevel");
	tinyMCEPopup.close();
}

function ss_getParentElement(ele, eleType) {
	if (ele.parentElement == null) return null;
	if (ele.parentElement.nodeName == eleType) {
		return ele.parentElement;
	} else {
		return ss_getParentElement(ele.parentElement, eleType);
	}
}


function ss_insertICElinkFromForm(currentBinderId) {
	var originalBinderId = dojo.byId("originalBinderId").value;
	var pageName = dojo.byId("pageName").value;
	ss_insertICElink(originalBinderId, pageName, currentBinderId);
}

function ss_cancelICElinkEdit() {
	tinyMCEPopup.close();
}

function ss_popup_folder() {
    dojo.style("folder_popup", "display", "block");
    dojo.style("page_popup", "display", "none");
}
function ss_popup_page() {
    dojo.style("page_popup", "display", "block");
    dojo.style("folder_popup", "display", "none");
}

function ss_close_popup_page() {
    dojo.style("page_popup", "display", "none");
}

function ss_close_popup_folder() {
    dojo.style("folder_popup", "display", "none");
}

function ss_loadLinkBinderId(binderId, listObj, name) {
	dojo.byId("binderId").value = binderId;
	var originalBinderId = dojo.byId("originalBinderId").value;
	if (originalBinderId == '' || originalBinderId == 'undefined') originalBinderId = binderId;
	dojo.byId("searchTitleFolder").value = name;
	var url = ss_wikiLinkUrl;
	url = ss_replaceSubStr(url, "ssBinderIdPlaceHolder", binderId);
	url = ss_replaceSubStr(url, "ssOriginalBinderIdPlaceHolder", originalBinderId);
	self.location.href = url;
	return false;
	
    ss_findEntriesBinderIdss_findLinkEntryForm_searchTitle = binderId;
	dojo.byId("linkToFolderName").innerHTML = name;
	ss_close_popup_folder();
	// May need to probe bgcolor...
	dojo.animateProperty({node: "linkToFolderName", duration: 1000,
	       properties: { backgroundColor: {start: "#FFFFFF", end: "#FFFF66"} }}).play();
}

function ss_loadLinkEntryId(entryId, listObj, name) {
	dojo.byId("pageName").value = name;
	dojo.byId("searchTitle").value = name;
	ss_close_popup_page();
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

function setElmText(elm, value) {
	if (value != "") {
		eval("elm.value = value;");
	}
}

function ss_prenormalizeText(t) {
	t = ss_wikilink_trim(t);
	while (t.indexOf("+") >= 0) t = t.replace("\+", "%2B");
	t = t.replace(/\s+/g, '_');
	return t;
}

//trim function
function ss_wikilink_trim(str) {
	// skip leading and trailing whitespace
	// and return everything in between
	var x=str;
	x=x.replace(/^\s*(.*)/, "$1");
	x=x.replace(/(.*?)\s*$/, "$1");
	return x;
}