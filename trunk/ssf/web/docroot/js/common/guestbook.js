
var ss_signGuestbookIframeOffset = 50;

function ss_showSignGuestbookIframe(namespace, obj) {
	var targetDiv = document.getElementById(namespace + '_add_entry_from_iframe');
	var iframeDiv = document.getElementById(namespace + '_new_guestbook_entry_iframe');
	if (window.frames[namespace + '_new_guestbook_entry_iframe'] != null) {
		eval("var iframeHeight = parseInt(window." + namespace + "_new_guestbook_entry_iframe.document.body.scrollHeight);");
		if (iframeHeight > 0) {
			iframeDiv.style.height = iframeHeight + ss_signGuestbookIframeOffset + "px"
		}
	}
}
function ss_signGuestbook(namespace, obj) {
	var targetDiv = document.getElementById(namespace + '_add_entry_from_iframe');
	if (targetDiv != null) {
		if (targetDiv.style.visibility == 'visible') {
			targetDiv.style.visibility = 'hidden';
			targetDiv.style.display = 'none';
			return;
		}
	}
	targetDiv.style.visibility = 'visible';
	targetDiv.style.display = 'block';
	var iframeDiv = document.getElementById(namespace + '_new_guestbook_entry_iframe');
	iframeDiv.src = obj.href;
}

function ss_hideAddEntryIframe(namespace) {
	var targetDiv = document.getElementById(namespace + '_add_entry_from_iframe');
	if (targetDiv != null) {
		targetDiv.style.visibility = 'hidden'
		targetDiv.style.display = 'none'
	}
}
