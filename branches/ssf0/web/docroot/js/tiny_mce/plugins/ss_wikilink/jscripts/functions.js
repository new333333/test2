/* The ss_wikilink plugin was written by SiteScape using the TinyMCE plugin development documentation as a guide. */
function init() {
	//tinyMCEPopup.resizeToInnerSize();
}

function ss_editingICElink() {
	var inst = tinyMCE.getInstanceById(tinyMCE.getWindowArg('editor_id'));
	var elm = inst.getFocusElement();
	elm = tinyMCE.getParentElement(elm, "a");
	if (elm == null) { return false; }
	return true;
}

function ss_insertICElink(binderId, title, currentBinderId) {
	var link = "";
	var inst = tinyMCE.getInstanceById(tinyMCE.getWindowArg('editor_id'));
	var elm = inst.getFocusElement();
    var linkText = inst.selection.getSelectedText();
    var pad = "";
    if (linkText.charAt(linkText.length - 1) == " ") { pad = " "; }
    linkText = linkText.trim();

	elm = tinyMCE.getParentElement(elm, "a");

	tinyMCEPopup.execCommand("mceBeginUndoLevel");

	// Create new anchor elements
	if (elm == null) {
		if (title == "" && (binderId == "" || binderId == currentBinderId)) {
		    if (linkText != "") {
				link = '[[' + linkText + ']]';
			}
		} else if ((linkText == "") && (title != "") && (binderId == "" || binderId == currentBinderId)) {
			link = '[[' + title + ']]';
		} else {
		    if (binderId == "") { binderId == currentBinderId };
		    if (linkText == "") { linkText = title; pad = " "; }
		    if (title == "") { title = linkText; }
			link = '<a class="ss_icecore_link" rel="binderId=' + binderId + ' title=' + ss_prenormalizeText(title) + '">' + linkText + '</a>' + pad;
		}
		if (link != "") {
			tinyMCE.execCommand('mceInsertContent', false, link);
		}
	} else {
		setAttrib(elm, "rel", 'binderId=' + binderId + ' title=' + title);
		setAttrib(elm, "class", "ss_icecore_link");
	}

	tinyMCE._setEventsEnabled(inst.getBody(), false);
	tinyMCEPopup.execCommand("mceEndUndoLevel");
	tinyMCEPopup.close();
}


function ss_insertICElinkFromForm(currentBinderId) {
	var binderId = dojo.byId("binderId").value;
	var pageName = dojo.byId("pageName").value;
	ss_insertICElink(binderId, pageName, currentBinderId);
}

function ss_cancelICElinkEdit() {
	tinyMCEPopup.close();
}

function ss_popup_folder() {
    dojo.html.toggleDisplay("folder_popup");
    dojo.html.hide("page_popup");
}
function ss_popup_page() {
    dojo.html.toggleDisplay("page_popup");
    dojo.html.hide("folder_popup");
}


function ss_loadLinkBinderId(binderId, type, obj) {
	dojo.byId("binderId").value = binderId;
    ss_findEntriesBinderIdss_findLinkEntryForm_searchTitle = binderId;
	dojo.byId("linkToFolderName").innerHTML = dojo.dom.textContent(obj);
	dojo.html.toggleDisplay('folder_popup');
	dojo.lfx.html.highlight("linkToFolderName", "#FFFF66", 1000).play();
}

function ss_loadLinkEntryId(entryId, obj) {
	dojo.byId("pageName").value = dojo.dom.textContent(obj);
	dojo.html.toggleDisplay("page_popup");
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

function ss_prenormalizeText(t) {
	t = t.trim();
	t = t.replace(/\s+/g, '_');
	return t;
}

//trim function
String.prototype.trim = function() {
	// skip leading and trailing whitespace
	// and return everything in between
	var x=this;
	x=x.replace(/^\s*(.*)/, "$1");
	x=x.replace(/(.*?)\s*$/, "$1");
	return x;
}