/**
 * $Id: editor_plugin_src.js 164 2007-01-04 17:17:58Z spocke $
 *
 * @author Moxiecode
 * @copyright Copyright � 2004-2007, Moxiecode Systems AB, All rights reserved.
 */

tinyMCE.importPluginLanguagePack('searchreplace');

var TinyMCE_SearchReplacePlugin = {
	getInfo : function() {
		return {
			longname : 'Search/Replace',
			author : 'Moxiecode Systems AB',
			authorurl : 'http://tinymce.moxiecode.com',
			infourl : 'http://tinymce.moxiecode.com/tinymce/docs/plugin_searchreplace.html',
			version : tinyMCE.majorVersion + "." + tinyMCE.minorVersion
		};
	},

	initInstance : function (inst) {
		inst.addShortcut('ctrl', 'f', 'lang_searchreplace_search_desc', 'mceSearch', true);
		// No CTRL+R for "replace" because browsers will reload page instead of executing plugin
	},

	getControlHTML : function (cn) {
		switch (cn) {
			case "search" :
				return tinyMCE.getButtonHTML(cn, 'lang_searchreplace_search_desc', '{$pluginurl}/images/search.gif','mceSearch', true);

			case "replace" :
				return tinyMCE.getButtonHTML(cn, 'lang_searchreplace_replace_desc', '{$pluginurl}/images/replace.gif', 'mceSearchReplace', true);
		}

		return "";
	},

	execCommand : function (editor_id, element, command, user_interface, value) {
		var inst = tinyMCE.getInstanceById(editor_id), selectedText = inst.selection.getSelectedText(), rng;

		function defValue(key, default_value) {
			value[key] = typeof(value[key]) == "undefined" ? default_value : value[key];
		}

		function replaceSel(search_str, str, back) {
			if (!inst.selection.isCollapsed()) {
				if (tinyMCE.isRealIE)
					inst.selection.getRng().duplicate().pasteHTML(str); // Needs to be duplicated due to selection bug in IE
				else
					inst.execCommand('mceInsertContent', false, str);
			}
		}

		if (!value)
			value = [];

		defValue("editor_id", editor_id);
		defValue("searchstring", selectedText);
		defValue("replacestring", null);
		defValue("replacemode", "none");
		defValue("casesensitive", false);
		defValue("backwards", false);
		defValue("wrap", false);
		defValue("wholeword", false);
		defValue("inline", "yes");
		defValue("resizable", "no");

		switch (command) {
			case "mceSearch" :
				if (user_interface) {
					var template = new Array();

					template['file'] = '../../plugins/searchreplace/searchreplace.htm';
					template['width'] = 380;
					template['height'] = 155 + (tinyMCE.isNS7 ? 20 : 0) + (tinyMCE.isMSIE ? 15 : 0);
					template['width'] += tinyMCE.getLang('lang_searchreplace_delta_width', 0);
					template['height'] += tinyMCE.getLang('lang_searchreplace_delta_height', 0);

					inst.selection.collapse(true);

					tinyMCE.openWindow(template, value);
				} else {
					var win = tinyMCE.getInstanceById(editor_id).contentWindow;
					var doc = tinyMCE.getInstanceById(editor_id).contentWindow.document;
					var body = tinyMCE.getInstanceById(editor_id).contentWindow.document.body;
					var awin = value.win, found;

					if (body.innerHTML == "") {
						awin.alert(tinyMCE.getLang('lang_searchreplace_notfound'));
						return true;
					}

					if (value['replacemode'] == "current") {
						replaceSel(value['string'], value['replacestring'], value['backwards']);
						value['replacemode'] = "none";
						//tinyMCE.execInstanceCommand(editor_id, 'mceSearch', user_interface, value);
						//return true;
					}

					inst.selection.collapse(value['backwards']);

					if (tinyMCE.isMSIE) {
						var rng = inst.selection.getRng();
						var flags = 0;
						if (value['wholeword'])
							flags = flags | 2;

						if (value['casesensitive'])
							flags = flags | 4;

						if (!rng.findText) {
							awin.alert('This operation is currently not supported by this browser.');
							return true;
						}

						if (value['replacemode'] == "all") {
							found = false;

							while (rng.findText(value['string'], value['backwards'] ? -1 : 1, flags)) {
								found = true;
								rng.scrollIntoView();
								rng.select();
								replaceSel(value['string'], value['replacestring'], value['backwards']);
							}

							if (found)
								awin.alert(tinyMCE.getLang('lang_searchreplace_allreplaced'));
							else
								awin.alert(tinyMCE.getLang('lang_searchreplace_notfound'));

							return true;
						}

						if (rng.findText(value['string'], value['backwards'] ? -1 : 1, flags)) {
							rng.scrollIntoView();
							rng.select();
						} else
							awin.alert(tinyMCE.getLang('lang_searchreplace_notfound'));
					} else {
						if (value['replacemode'] == "all") {
							found = false;

							while (win.find(value['string'], value['casesensitive'], value['backwards'], value['wrap'], value['wholeword'], false, false)) {
								found = true;
								replaceSel(value['string'], value['replacestring'], value['backwards']);
							}

							if (found)
								awin.alert(tinyMCE.getLang('lang_searchreplace_allreplaced'));
							else
								awin.alert(tinyMCE.getLang('lang_searchreplace_notfound'));

							return true;
						}

						if (!win.find(value['string'], value['casesensitive'], value['backwards'], value['wrap'], value['wholeword'], false, false))
							awin.alert(tinyMCE.getLang('lang_searchreplace_notfound'));
					}
				}

				return true;

			case "mceSearchReplace" :
				value['replacestring'] = "";
				tinyMCE.execInstanceCommand(editor_id, 'mceSearch', user_interface, value, false);
				return true;
		}

		return false;
	}
};

tinyMCE.addPlugin("searchreplace", TinyMCE_SearchReplacePlugin);