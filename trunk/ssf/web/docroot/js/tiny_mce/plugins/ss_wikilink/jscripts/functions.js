function init() {
	//tinyMCEPopup.resizeToInnerSize();
}

function ss_insertICElink(binderId, title, text, currentBinderId) {

	var link;
	
	if (binderId == currentBinderId) {
		link = '[[' + text + "]]";
	} else {
		link = '{{titleUrl: binderId=' + binderId + ' title=' + title + ' text=' + text + '}}';
	}

	tinyMCE.execCommand('mceInsertContent', false, link);
	tinyMCEPopup.close();
}
