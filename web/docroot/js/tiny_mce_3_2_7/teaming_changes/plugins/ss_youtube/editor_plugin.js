/**
 * $Id: editor_plugin_src.js 163 2007-01-03 16:31:00Z spocke $
 *
 * @author Moxiecode
 * @copyright Copyright © 2004-2007, Moxiecode Systems AB, All rights reserved.
 * 
 * Edited by Peter Hurley
 */

(function() {
	// Load plugin specific language pack
	tinymce.PluginManager.requireLangPack('ss_youtube');

	tinymce.create('tinymce.plugins.ssYouTubePlugin', {
		/**
		 * Initializes the plugin, this will be executed after the plugin has been created.
		 * This call is done before the editor instance has finished it's initialization so use the onInit event
		 * of the editor instance to intercept that event.
		 *
		 * @param {tinymce.Editor} ed Editor instance that the plugin is initialized in.
		 * @param {string} url Absolute URL to where the plugin is located.
		 */
		init : function(ed, url) {
			// Register the command so that it can be invoked by using tinyMCE.activeEditor.execCommand('mce_ssYouTube');
			ed.addCommand('mce_ssYouTube', function() {
				ed.windowManager.open({
					file : url + '/youtube.htm',
					width : 550 + ed.getLang('ss_youtube.delta_width', 0),
					height : 300 + ed.getLang('ss_youtube.delta_height', 0),
					inline : 1
				}, {
					plugin_url : url
				});
			});

			// Register ss_youtube button
			ed.addButton('ss_youtube', {
				title : 'ss_youtube.desc',
				cmd : 'mce_ssYouTube',
				image : url + '/images/media.gif'
			});

			// Add a node change handler, selects the button in the UI when a image is selected
			ed.onNodeChange.add(function(ed, cm, n) {
				cm.setActive('ss_youtube', n.className == 'ss_youtube_link');
				if (n.className == 'ss_youtube_link') {
					ed.youtubeNode = n;
				} else {
					ed.youtubeNode = null;
				}
			});
		},

		/**
		 * Creates control instances based in the incomming name. This method is normally not
		 * needed since the addButton method of the tinymce.Editor class is a more easy way of adding buttons
		 * but you sometimes need to create more complex controls like listboxes, split buttons etc then this
		 * method can be used to create those.
		 *
		 * @param {String} n Name of the control to create.
		 * @param {tinymce.ControlManager} cm Control manager to use inorder to create new control.
		 * @return {tinymce.ui.Control} New control instance or null if no control was created.
		 */
		createControl : function(n, cm) {
			return null;
		},

		/**
		 * Returns information about the plugin as a name/value array.
		 * The current keys are longname, author, authorurl, infourl and version.
		 *
		 * @return {Object} Name/value array containing information about the plugin.
		 */
		getInfo : function() {
			return {
				longname : 'Teaming YouTube Link',
				author : 'Peter Hurley - Novell',
				authorurl : 'http://kablink.org',
				infourl : 'http://kablink.org',
				version : "1.0"
			};
		}
	});

	// Register plugin
	tinymce.PluginManager.add('ss_youtube', tinymce.plugins.ssYouTubePlugin);
})();

