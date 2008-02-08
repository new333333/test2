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
	 * Returns the HTML contents of the emotions control.
	 */
	getControlHTML : function(cn) {
		switch (cn) {
			case "ss_wikiLink":
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
				template['width'] = 250;
				template['height'] = 160;

				tinyMCE.openWindow(template, {editor_id : editor_id, inline : "yes"});

				return true;
		}

		// Pass to next handler in chain
		return false;
	}
};

// Register plugin
tinyMCE.addPlugin('ss_wikilink', TinyMCE_ssWikiLinkPlugin);
