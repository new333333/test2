/* Functions for the ss_addimage plugin popup */
/* The ss_addimage plugin was written by SiteScape. It is a modification of the standard advimage plugin. */

var preloadImg = null;
var orgImageWidth, orgImageHeight;
tinyMCEPopup.requireLangPack();

function preinit() {
	// Initialize
	//tinyMCE.setWindowArg('mce_windowresize', false);

	// Import external list url javascript
	if (url = tinyMCEPopup.getParam("external_image_list_url"))
		document.write('<script language="javascript" type="text/javascript" src="' + tinyMCE.editor.documentBaseURI.toAbsolute(url) + '"></script>');
}

function convertURL(url, node, on_save) {
	//alert("convert url: "+url)
	return url;
}

function getImageSrc(str) {
	//alert("Get src: "+str)
	var pos = -1;

	if (!str)
		return "";

	if ((pos = str.indexOf('this.src=')) != -1) {
		var src = str.substring(pos + 10);

		src = src.substring(0, src.indexOf('\''));

		if (tinyMCEPopup.getParam('convert_urls'))
			src = convertURL(src, null, true);

		//alert("src = "+src)
		return src;
	}

	return "";
}

function init() {
	tinyMCE.resizeToInnerSize();

	var formObj = document.forms[0];
	var inst = tinyMCEPopup.editor;
	var elm = inst.selection.getNode();
	var action = "insert";
	var html = "";

	// Src browser
	html = getBrowserHTML('srcbrowser','src','image','ss_addimage');
	document.getElementById("srcbrowsercontainer").innerHTML = html;

	// Longdesc browser
	html = getBrowserHTML('longdescbrowser','longdesc','file','ss_addimage');
	document.getElementById("longdesccontainer").innerHTML = html;

	// Resize some elements
	if (isVisible('srcbrowser'))
		document.getElementById('src').style.width = '260px';

	if (isVisible('longdescbrowser'))
		document.getElementById('longdesc').style.width = '180px';

	// Check action
	if (elm != null && elm.nodeName == "IMG")
		action = "update";

	formObj.insert.value = tinyMCE.getLang('lang_' + action, 'Insert', true); 

	if (action == "update") {
		var src = ss_getAttrib(elm, 'src');

		src = convertURL(src, elm, true);

		// Use mce_src if found
		var mceRealSrc = ss_getAttrib(elm, 'mce_src');
		if (mceRealSrc != "") {
			src = mceRealSrc;

			if (tinyMCEPopup.getParam('convert_urls'))
				src = convertURL(src, elm, true);
		}

		// Setup form data
		var style = dom.parseStyle(ss_getAttrib(elm, "style"));

		// Store away old size
		orgImageWidth = trimSize(getStyle(elm, 'width'))
		orgImageHeight = trimSize(getStyle(elm, 'height'));

		formObj.src.value    = src;
		formObj.alt.value    = ss_getAttrib(elm, 'alt');
		formObj.title.value  = ss_getAttrib(elm, 'title');
		formObj.border.value = trimSize(getStyle(elm, 'border', 'borderWidth'));
		formObj.vspace.value = ss_getAttrib(elm, 'vspace');
		formObj.hspace.value = ss_getAttrib(elm, 'hspace');
		formObj.width.value  = orgImageWidth;
		formObj.height.value = orgImageHeight;
		formObj.id.value  = ss_getAttrib(elm, 'id');
		formObj.dir.value  = ss_getAttrib(elm, 'dir');
		formObj.lang.value  = ss_getAttrib(elm, 'lang');
		formObj.longdesc.value  = ss_getAttrib(elm, 'longdesc');
		formObj.usemap.value  = ss_getAttrib(elm, 'usemap');
		formObj.style.value  = dom.serializeStyle(style);

		// Select by the values
		if (tinyMCE.isMSIE)
			selectByValue(formObj, 'align', getStyle(elm, 'align', 'styleFloat'));
		else
			selectByValue(formObj, 'align', getStyle(elm, 'align', 'cssFloat'));

		addClassesToList('classlist', 'ss_addimage');

		selectByValue(formObj, 'classlist', ss_getAttrib(elm, 'class'));
		selectByValue(formObj, 'imagelistsrc', src);

		updateStyle();
		showPreviewImage(src, true);
		changeAppearance();

		window.focus();
	} else
		addClassesToList('classlist', 'ss_addimage');

	// If option enabled default contrain proportions to checked
	if (tinyMCEPopup.getParam("ss_addimage_constrain_proportions", true))
		formObj.constrain.checked = true;

}

function setSwapImageDisabled(state) {
	var formObj = document.forms[0];

	formObj.onmousemovecheck.checked = !state;

	setBrowserDisabled('overbrowser', state);
	setBrowserDisabled('outbrowser', state);

}

function ss_getAttrib(ele, eleType) {
	return ele.getAttribute(eleType);
}


function setAttrib(elm, attrib, value) {
	var formObj = document.forms[0];
	var valueElm = formObj.elements[attrib];

	if (typeof(value) == "undefined" || value == null) {
		value = "";

		if (valueElm)
			value = valueElm.value;
	}

	if (value != "") {
		elm.setAttribute(attrib, value);

		if (attrib == "style")
			attrib = "style.cssText";

		if (attrib == "longdesc")
			attrib = "longDesc";

		if (attrib == "width") {
			attrib = "style.width";
			value = value + "px";
		}

		if (attrib == "height") {
			attrib = "style.height";
			value = value + "px";
		}

		if (attrib == "class")
			attrib = "className";

		eval('elm.' + attrib + "=value;");
	} else
		elm.removeAttribute(attrib);
}

function makeAttrib(attrib, value) {
	var formObj = document.forms[0];
	var valueElm = formObj.elements[attrib];

	if (typeof(value) == "undefined" || value == null) {
		value = "";

		if (valueElm)
			value = valueElm.value;
	}

	if (value == "")
		return "";

	// XML encode it
	value = value.replace(/&/g, '&amp;');
	value = value.replace(/\"/g, '&quot;');
	value = value.replace(/</g, '&lt;');
	value = value.replace(/>/g, '&gt;');

	return ' ' + attrib + '="' + value + '"';
}

function insertAction() {
	var inst = tinyMCEPopup.editor;
	var elm = inst.selection.getNode();
	var formObj = document.forms[0];
	var src = formObj.src.value;
	var srcUrl = "";
	srcUrl = formObj.srcUrl.value;
	//alert("Insert action: "+src)
	var imageClass = " ss_addimage "
	if (src == '') {
		src = srcUrl;
		imageClass = " ss_addimage_att "
	}
	
	var fileSelectObj = document.getElementById('srcUrl');
	var radioBtnObj = document.getElementById('typeSelFile');
	var prevObj = document.getElementById('prev');
	if (radioBtnObj.checked && prevObj.innerHTML == "" &&
			(elm == null || elm.nodeName.toUpperCase() != "IMG")) {
		alert(tinyMCEPopup.getLang('ss_addimage_dlg.missing_img', '', true))
		return
	} else if ((fileSelectObj.value == null || fileSelectObj.value == "") && prevObj.innerHTML == "") {
		alert(tinyMCEPopup.getLang('ss_addimage_dlg.missing_img', '', true))
		return
	}

	if (tinyMCEPopup.getParam("accessibility_warnings")) {
		if (formObj.alt.value == "") {
			var answer = confirm(tinyMCEPopup.getLang('ss_addimage_dlg.missing_alt', '', true));
			if (answer == true) {
				formObj.alt.value = " ";
			}
		} else {
			var answer = true;
		}

		if (!answer)
			return;
	}

	//alert("elm: "+elm+", "+elm.nodeName.toUpperCase())
	if (elm != null && elm.nodeName.toUpperCase() == "IMG") {
		//alert("IMG src: "+convertURL(src, tinyMCE.imgElement))
		setAttrib(elm, 'src', convertURL(src, tinyMCE.imgElement));
		setAttrib(elm, 'mce_src', src);
		setAttrib(elm, 'alt');
		setAttrib(elm, 'title');
		setAttrib(elm, 'border');
		setAttrib(elm, 'vspace');
		setAttrib(elm, 'hspace');
		setAttrib(elm, 'width');
		setAttrib(elm, 'height');
		setAttrib(elm, 'id');
		setAttrib(elm, 'dir');
		setAttrib(elm, 'lang');
		setAttrib(elm, 'longdesc');
		setAttrib(elm, 'usemap');
		setAttrib(elm, 'style');
		setAttrib(elm, 'class', getSelectValue(formObj, 'classlist') + imageClass);
		setAttrib(elm, 'align', getSelectValue(formObj, 'align'));

		//tinyMCE.execCommand("mceRepaint");

		// Repaint if dimensions changed
		if (formObj.width.value != orgImageWidth || formObj.height.value != orgImageHeight)
			//inst.repaint();

		// Refresh in old MSIE
		if (tinyMCE.isMSIE5)
			elm.outerHTML = elm.outerHTML;
	} else {
		var html = "<img";

		html += makeAttrib('src', src);
		html += makeAttrib('mce_src', src);
		html += makeAttrib('alt');
		html += makeAttrib('title');
		html += makeAttrib('border');
		html += makeAttrib('vspace');
		html += makeAttrib('hspace');
		html += makeAttrib('width');
		html += makeAttrib('height');
		html += makeAttrib('id');
		html += makeAttrib('dir');
		html += makeAttrib('lang');
		html += makeAttrib('longdesc');
		html += makeAttrib('usemap');
		html += makeAttrib('style');
		html += makeAttrib('class', getSelectValue(formObj, 'classlist') + imageClass);
		html += makeAttrib('align', getSelectValue(formObj, 'align'));
		html += " />";
		//alert("IMG HTML: "+html)
		tinyMCE.execCommand("mceInsertContent", false, html);
	}

	tinyMCEPopup.close();
}

function cancelAction() {
	tinyMCEPopup.close();
}

function changeAppearance() {
	var formObj = document.forms[0];
	var img = document.getElementById('alignSampleImg');

	if (img) {
		img.align = formObj.align.value;
		img.border = formObj.border.value;
		img.hspace = formObj.hspace.value;
		img.vspace = formObj.vspace.value;
	}
}

function changeMouseMove() {
	var formObj = document.forms[0];

	setSwapImageDisabled(!formObj.onmousemovecheck.checked);
}

function updateStyle() {
	var f = document.forms[0], nl = f.elements, ed = tinyMCEPopup.editor, dom = ed.dom, n = ed.selection.getNode();
	var formObj = document.forms[0];
	var st = dom.parseStyle(formObj.style.value);

	if (tinyMCEPopup.getParam('inline_styles', false)) {
		st['width'] = formObj.width.value == '' ? '' : formObj.width.value + "px";
		st['height'] = formObj.height.value == '' ? '' : formObj.height.value + "px";
		st['border-width'] = formObj.border.value == '' ? '' : formObj.border.value + "px";
		st['margin-top'] = formObj.vspace.value == '' ? '' : formObj.vspace.value + "px";
		st['margin-bottom'] = formObj.vspace.value == '' ? '' : formObj.vspace.value + "px";
		st['margin-left'] = formObj.hspace.value == '' ? '' : formObj.hspace.value + "px";
		st['margin-right'] = formObj.hspace.value == '' ? '' : formObj.hspace.value + "px";
	} else {
		st['width'] = st['height'] = st['border-width'] = null;

		if (st['margin-top'] == st['margin-bottom'])
			st['margin-top'] = st['margin-bottom'] = null;

		if (st['margin-left'] == st['margin-right'])
			st['margin-left'] = st['margin-right'] = null;
	}

	formObj.style.value = dom.serializeStyle(st);
}

function styleUpdated() {
	var formObj = document.forms[0];
	var st = dom.parseStyle(formObj.style.value);

	if (st['width'])
		formObj.width.value = st['width'].replace('px', '');

	if (st['height'])
		formObj.height.value = st['height'].replace('px', '');

	if (st['margin-top'] && st['margin-top'] == st['margin-bottom'])
		formObj.vspace.value = st['margin-top'].replace('px', '');

	if (st['margin-left'] && st['margin-left'] == st['margin-right'])
		formObj.hspace.value = st['margin-left'].replace('px', '');

	if (st['border-width'])
		formObj.border.value = st['border-width'].replace('px', '');
}

function changeHeight() {
	var formObj = document.forms[0];

	if (!formObj.constrain.checked || !preloadImg) {
		updateStyle();
		return;
	}

	if (formObj.width.value == "" || formObj.height.value == "")
		return;

	var temp = (formObj.width.value / preloadImg.width) * preloadImg.height;
	formObj.height.value = temp.toFixed(0);
	updateStyle();
}

function changeWidth() {
	var formObj = document.forms[0];

	if (!formObj.constrain.checked || !preloadImg) {
		updateStyle();
		return;
	}

	if (formObj.width.value == "" || formObj.height.value == "")
		return;

	var temp = (formObj.height.value / preloadImg.height) * preloadImg.width;
	formObj.width.value = temp.toFixed(0);
	updateStyle();
}

function onSelectMainImage(target_form_element, name, value) {
	var formObj = document.forms[0];

	formObj.alt.value = name;
	formObj.title.value = name;

	resetImageData();
	showPreviewImage(formObj.elements[target_form_element].value, false);
}

function showPreviewImage(src, start) {
	var formObj = document.forms[0];

	selectByValue(document.forms[0], 'imagelistsrc', src);

	var elm = document.getElementById('prev');

	if (!start && tinyMCEPopup.getParam("ss_addimage_update_dimensions_onchange", true))
		resetImageData();

	if (src == "")
		elm.innerHTML = "";
	else
		elm.innerHTML = '<img class="ss_addimage" src="' + src + '" border="0" />';

	getImageData(src);
}

function getImageData(src) {
	preloadImg = new Image();

	tinymce.dom.Event.add(preloadImg, "load", updateImageData);
	tinymce.dom.Event.add(preloadImg, "error", resetImageData);

	preloadImg.src = src;
}

function updateImageData() {
	var formObj = document.forms[0];

	if (formObj.width.value == "")
		formObj.width.value = preloadImg.width;

	if (formObj.height.value == "")
		formObj.height.value = preloadImg.height;

	updateStyle();
}

function resetImageData() {
	var formObj = document.forms[0];
	formObj.width.value = formObj.height.value = "";	
}

function getSelectValue(form_obj, field_name) {
	var elm = form_obj.elements[field_name];

	if (elm == null || elm.options == null)
		return "";

	return elm.options[elm.selectedIndex].value;
}

function getImageListHTML(elm_id, target_form_element, onchange_func) {
	if (typeof(tinyMCEImageList) == "undefined" || tinyMCEImageList.length == 0)
		return "";

	var html = "";

	html += '<select id="' + elm_id + '" name="' + elm_id + '"';
	html += ' class="mceImageList" onfocus="tinyMCE.addSelectAccessibility(event, this, window);" onchange="this.form.' + target_form_element + '.value=';
	html += 'this.options[this.selectedIndex].value;';

	if (typeof(onchange_func) != "undefined")
		html += onchange_func + '(\'' + target_form_element + '\',this.options[this.selectedIndex].text,this.options[this.selectedIndex].value);';

	html += '"><option value="">---</option>';

	for (var i=0; i<tinyMCEImageList.length; i++)
		html += '<option value="' + tinyMCEImageList[i][1] + '">' + tinyMCEImageList[i][0] + '</option>';

	html += '</select>';

	return html;

	// tinyMCE.debug('-- image list start --', html, '-- image list end --');
}

// While loading
preinit();
