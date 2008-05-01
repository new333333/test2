/**
 * $Id: editor_plugin_src.js 163 2007-01-03 16:31:00Z spocke $
 *
 * @author Moxiecode
 * @copyright Copyright © 2004-2007, Moxiecode Systems AB, All rights reserved.
 */

/* Import plugin specific language pack */
tinyMCE.importPluginLanguagePack('ss_wikilink');

// Plucin static class
var TinyMCE_ssWikiLinkPlugin = {
	getInfo : function() {
		return {
			longname : 'ICEcore Wiki Link',
			author : 'SiteScape, Inc.',
			authorurl : 'http://www.sitescape.com',
			infourl : 'http://www.sitescape.com/icecore-open/plugin_wikilink.html',
			version : tinyMCE.majorVersion + "." + tinyMCE.minorVersion
		};
	},

	/**
	 * Returns the HTML contents of the wikilink control.
	 */
	getControlHTML : function(cn) {
		switch (cn) {
			case "ss_wikilink":
				return tinyMCE.getButtonHTML(cn, 'lang_wikilink_desc', '{$pluginurl}/images/wikilink.gif', 'mce_ssWikiLink');
		}

		return "";
	},

	/**
	 * Executes the mceEmotion command.
	 */
	execCommand : function(editor_id, element, command, user_interface, value) {
		// Handle commands
		switch (command) {
			case "mce_ssWikiLink":
				var template = new Array();

				template['file'] = ss_wikiLinkUrl;
				template['width'] = 550;
				template['height'] = 300;

				tinyMCE.openWindow(template, {editor_id : editor_id, inline : "yes"});

				return true;
		}

		// Pass to next handler in chain
		return false;
	},
	
	handleNodeChange : function(editor_id, node, undo_index, undo_levels, visual_aid, any_selection) {
		if (node == null)
			return;

		do {
			if (node.nodeName == "A" && tinyMCE.getAttrib(node, 'rel') != "") {
				tinyMCE.switchClass(editor_id + '_ss_wikilink', 'mceButtonSelected');
				return true;
			}
		} while ((node = node.parentNode));

		if (any_selection) {
			tinyMCE.switchClass(editor_id + '_ss_wikilink', 'mceButtonNormal');
			return true;
		}

		tinyMCE.switchClass(editor_id + '_ss_wikilink', 'mceButtonNormal');

		return true;
	}
	
	
};

// Register plugin
tinyMCE.addPlugin('ss_wikilink', TinyMCE_ssWikiLinkPlugin);
