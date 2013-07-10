/**
 * Copyright (c) 1998-2011 Novell, Inc. and its licensors. All rights reserved.
 * 
 * This work is governed by the Common Public Attribution License Version 1.0 (the
 * "CPAL"); you may not use this file except in compliance with the CPAL. You may
 * obtain a copy of the CPAL at http://www.opensource.org/licenses/cpal_1.0. The
 * CPAL is based on the Mozilla Public License Version 1.1 but Sections 14 and 15
 * have been added to cover use of software over a computer network and provide
 * for limited attribution for the Original Developer. In addition, Exhibit A has
 * been modified to be consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT
 * WARRANTY OF ANY KIND, either express or implied. See the CPAL for the specific
 * language governing rights and limitations under the CPAL.
 * 
 * The Original Code is ICEcore, now called Kablink. The Original Developer is
 * Novell, Inc. All portions of the code written by Novell, Inc. are Copyright
 * (c) 1998-2011 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2011 Novell, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by Kablink]
 * Attribution URL: [www.kablink.org]
 * Graphic Image as provided in the Covered Code
 * [ssf/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are
 * defined in the CPAL as a work which combines Covered Code or portions thereof
 * with code not governed by the terms of the CPAL.
 * 
 * NOVELL and the Novell logo are registered trademarks and Kablink and the
 * Kablink logos are trademarks of Novell, Inc.
 */

package org.kablink.teaming.gwt.client;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.ImageResource.ImageOptions;
import com.google.gwt.resources.client.ImageResource.RepeatStyle;


/**
 * Images used by GWT Teaming.
 * 
 * @author jwootton
 */
public interface GwtTeamingImageBundle extends ClientBundle
{
	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/activityStreamActions1.png")
	public ImageResource activityStreamActions1();

	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/activityStreamActions2.png")
	public ImageResource activityStreamActions2();

	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/add_btn.png")
	public ImageResource add_btn();

	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/Masthead/admin_console.png")
	public ImageResource adminMenuImg();

	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/administration1.png")
	public ImageResource administration1();

	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/administration2.png")
	public ImageResource administration2();

	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/trans50_black_arrowleft.png")
	public ImageResource arrowTrans50Left();
	
	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/breadspace.gif")
	public ImageResource breadSpace();
	
	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/browse_hierarchy.png")
	public ImageResource browseHierarchy();

	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/close_x.png")
	public ImageResource closeX();

	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/close_x_over.png")
	public ImageResource closeXMouseOver();

	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/check_12.png")
	public ImageResource check12();
	
	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/collapse_16.png")
	public ImageResource collapse16();

	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/collapser.png")
	public ImageResource collapser();
	
	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/colorPicker.png")
	public ImageResource colorPicker();
	
	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/delete.png")
	public ImageResource delete();

	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/delete_10.png")
	public ImageResource delete10();

	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/delete_16.gif")
	public ImageResource delete16();

	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/edit_10.png")
	public ImageResource edit10();

	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/edit_16.gif")
	public ImageResource edit16();

	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/expand_16.png")
	public ImageResource expand16();

	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/expander.png")
	public ImageResource expander();
	
	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/entry_blog.png")
	public ImageResource entry_blog();
	
	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/entry_file.gif")
	public ImageResource entry_file();
	
	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/entry_task.png")
	public ImageResource entry_task();

	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/Masthead/help.png")
	public ImageResource helpMenuImg();

	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/help1.png")
	public ImageResource help1();

	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/help2.png")
	public ImageResource help2();

	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/help_but_teal.png")
	public ImageResource help3();

	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/info2.png")
	public ImageResource info2();

	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source( "org/kablink/teaming/gwt/public/images/lpe_accessory.gif" )
	public ImageResource landingPageEditorAccessory();
	
	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/lpe_custom_jsp.png")
	public ImageResource landingPageEditorCustomJsp();

	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/lpe_entry16.png")
	public ImageResource landingPageEditorEntry();

	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/lpe_folder.png")
	public ImageResource landingPageEditorFolder();

	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source( "org/kablink/teaming/gwt/public/images/lpe_google_gadget.gif" )
	public ImageResource landingPageEditorGoogleGadget();
	
	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/lpe_graphic16.png")
	public ImageResource landingPageEditorGraphic();

	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source( "org/kablink/teaming/gwt/public/images/lpe_html.gif" )
	public ImageResource landingPageEditorHtml();
	
	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source( "org/kablink/teaming/gwt/public/images/lpe_iframe.gif" )
	public ImageResource landingPageEditorIFrame();
	
	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/lpe_link_entry.png")
	public ImageResource landingPageEditorLinkEntry();

	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/lpe_link_folder.png")
	public ImageResource landingPageEditorLinkFolder();

	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/lpe_link_url.png")
	public ImageResource landingPageEditorLinkUrl();

	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/lpe_list16b.png")
	public ImageResource landingPageEditorList();

	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/lpe_enhanced_view.gif")
	public ImageResource landingPageEditorEnhancedView();

	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/lpe_table_16.png")
	public ImageResource landingPageEditorTable();

	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/lpe_utility_element.png")
	public ImageResource landingPageEditorUtilityElement();
	
	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/login1.png")
	public ImageResource login1();

	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/login2.png")
	public ImageResource login2();

	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/logout1.png")
	public ImageResource logout1();

	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/logout2.png")
	public ImageResource logout2();

	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/management_16.png")
	public ImageResource management16();

	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/Masthead/masthead_actions.png")
	public ImageResource mastheadActions();

	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/Masthead/actions.png")
	public ImageResource mastheadActions2();

	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/mast_head_kablink_graphic.png")
	public ImageResource mastHeadKablinkGraphic();

	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/mast_head_novell_graphic.png")
	public ImageResource mastHeadNovellGraphic();

	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/mast_head_novell_logo.png")
	public ImageResource mastHeadNovellLogo();

	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/menu.png")
	public ImageResource menu();

	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/move10.png")
	public ImageResource move10();

	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/my_workspace1.png")
	public ImageResource myWorkspace1();

	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/my_workspace2.png")
	public ImageResource myWorkspace2();

	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/Masthead/news_feed.png")
	public ImageResource newsFeedMenuImg();

	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/arrow_right.png")
	public ImageResource next16();

	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/arrow_right_disabled.png")
	public ImageResource nextDisabled16();
	
	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/pause.png")
	public ImageResource pauseActivityStream();
	
	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/Masthead/personal_prefs.png")
	public ImageResource personalPrefsMenuImg();

	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/personal_prefs1.png")
	public ImageResource personalPrefs1();
	
	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/personal_prefs2.png")
	public ImageResource personalPrefs2();

	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/presence_available14.png")
	public ImageResource presenceAvailable16();

	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/presence_away14.png")
	public ImageResource presenceAway16();

	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/presence_busy14.png")
	public ImageResource presenceBusy16();

	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/presence_offline14.png")
	public ImageResource presenceOffline16();

	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/presence_unknown14.png")
	public ImageResource presenceUnknown16();
	
	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/arrow_left.png")
	public ImageResource previous16();

	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/arrow_left_disabled.png")
	public ImageResource previousDisabled16();

	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/refresh.png")
	public ImageResource refresh();

	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/report_16b.gif")
	public ImageResource report16();
	
	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/Masthead/resource_library.png")
	public ImageResource resourceLibMenuImg();

	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/resourceLib1.png")
	public ImageResource resourceLib1();

	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/resourceLib2.png")
	public ImageResource resourceLib2();

	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/resume.png")
	public ImageResource resumeActivityStream();

	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/spacer_1px.png")
	public ImageResource spacer1px();

	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/spinner16x16.gif")
	public ImageResource spinner16();

	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/sunburst.png")
	public ImageResource sunburst();

	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/system_config_16.png")
	public ImageResource system16();

	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/teaming_feed1.png")
	public ImageResource teamingFeed1();

	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/teaming_feed2.png")
	public ImageResource teamingFeed2();

	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/warn_icon16.gif")
	public ImageResource warningIcon16();	
}// end GwtTeamingImageBundle
