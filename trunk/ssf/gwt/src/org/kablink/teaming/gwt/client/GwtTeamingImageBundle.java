/**
 * Copyright (c) 1998-2014 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2014 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2014 Novell, Inc. All Rights Reserved.
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
 * @author jwootton@novell.com
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
	@Source("org/kablink/teaming/gwt/public/images/browse_ldap.png")
	public ImageResource browseLdap();

	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/cal_menu.png")
	public ImageResource calDatePicker();

	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/cal_menu_over.png")
	public ImageResource calDatePickerMouseOver();

	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/CalView/calView_1day_btn.png")
	public ImageResource calView1Day();

	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/CalView/calView_1day_over_btn.png")
	public ImageResource calView1DayMouseOver();

	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/CalView/calView_1day_sel_btn.png")
	public ImageResource calView1DaySelected();

	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/CalView/calView_3day_btn.png")
	public ImageResource calView3Day();

	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/CalView/calView_3day_over_btn.png")
	public ImageResource calView3DayMouseOver();

	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/CalView/calView_3day_sel_btn.png")
	public ImageResource calView3DaySelected();

	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/CalView/calView_5day_btn.png")
	public ImageResource calView5Day();

	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/CalView/calView_5day_over_btn.png")
	public ImageResource calView5DayMouseOver();

	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/CalView/calView_5day_sel_btn.png")
	public ImageResource calView5DaySelected();

	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/CalView/calView_2week_btn.png")
	public ImageResource calView2Week();

	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/CalView/calView_2week_over_btn.png")
	public ImageResource calView2WeekMouseOver();

	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/CalView/calView_2week_sel_btn.png")
	public ImageResource calView2WeekSelected();

	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/CalView/calView_month_btn.png")
	public ImageResource calViewMonth();

	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/CalView/calView_month_over_btn.png")
	public ImageResource calViewMonthMouseOver();

	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/CalView/calView_month_sel_btn.png")
	public ImageResource calViewMonthSelected();

	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/CalView/calView_today_btn.png")
	public ImageResource calViewToday();

	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/CalView/calView_today_over_btn.png")
	public ImageResource calViewTodayMouseOver();

	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/CalView/calView_week_btn.png")
	public ImageResource calViewWeek();

	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/CalView/calView_week_over_btn.png")
	public ImageResource calViewWeekMouseOver();

	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/CalView/calView_week_sel_btn.png")
	public ImageResource calViewWeekSelected();

	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/close_20.png")
	public ImageResource closeBorder();

	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/close_20b.png")
	public ImageResource close20();

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
	@Source("org/kablink/teaming/gwt/public/images/comments_22.png")
	public ImageResource comments22();

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
	@Source("org/kablink/teaming/gwt/public/images/config_options_btn.png")
	public ImageResource configOptions();

	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/config_options_over_btn.png")
	public ImageResource configOptionsMouseOver();

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
	@Source("org/kablink/teaming/gwt/public/images/cog_20.png")
	public ImageResource cog20();

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
	@Source("org/kablink/teaming/gwt/public/images/entry_file.png")
	public ImageResource entry_file();
	
	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/entry_task.png")
	public ImageResource entry_task();

	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/expired_license_icon16.png")
	public ImageResource expiredLicenseIcon16();
	
	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/pin_gray.png")
	public ImageResource grayPin();
	
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
	@Source("org/kablink/teaming/gwt/public/images/Masthead/Filr_hierarchy_25.png")
	public ImageResource mastheadBrowseFilr();

	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/mast_head_filr_graphic.png")
	public ImageResource mastHeadFilrGraphic();

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
	@Source("org/kablink/teaming/gwt/public/images/menu_gray.png")
	public ImageResource menu();

	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/menu_but.png")
	public ImageResource menuButton();

	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/move10.png")
	public ImageResource move10();

	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/my_workspace1.png")
	public ImageResource myWorkspace1();

	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/NetFolders/sync_canceled.png")
	public ImageResource netFolderSyncStatusCanceled();

	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/NetFolders/sync_completed.png")
	public ImageResource netFolderSyncStatusCompleted();

	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/NetFolders/sync_in_progress.gif")
	public ImageResource netFolderSyncStatusInProgress();

	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/NetFolders/sync_never_run.png")
	public ImageResource netFolderSyncStatusNeverRun();

	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/NetFolders/sync_stopped.png")
	public ImageResource netFolderSyncStatusStopped();

	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/NetFolders/sync_status_unknown.png")
	public ImageResource netFolderSyncStatusUnknown();

	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/NetFolders/waiting_to_be_syncd.png")
	public ImageResource netFolderSyncStatusWaitingToBeSyncd();

	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/my_workspace2.png")
	public ImageResource myWorkspace2();

	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/Masthead/news_feed_2.png")
	public ImageResource newsFeedMenuImg();

	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/arrow_right.png")
	public ImageResource next16();

	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/arrow_right_disabled.png")
	public ImageResource nextDisabled16();
	
	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/arrow_right_over.png")
	public ImageResource nextMouseOver16();
	
	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/pin_orange.png")
	public ImageResource orangePin();
	
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
	@Source("org/kablink/teaming/gwt/public/images/preview20.png")
	public ImageResource preview20();

	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/arrow_left.png")
	public ImageResource previous16();

	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/arrow_left_disabled.png")
	public ImageResource previousDisabled16();

	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/arrow_left_over.png")
	public ImageResource previousMouseOver16();

	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/public16.png")
	public ImageResource public16();

	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/public25.png")
	public ImageResource public25();

	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/public36.png")
	public ImageResource public36();

	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/public48.png")
	public ImageResource public48();

	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/public50.png")
	public ImageResource public50();

	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/public72.png")
	public ImageResource public72();

	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/public96.png")
	public ImageResource public96();

	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/publicLink16.png")
	public ImageResource publicLink16();

	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/publicLink25.png")
	public ImageResource publicLink25();

	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/publicLink36.png")
	public ImageResource publicLink36();

	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/publicLink48.png")
	public ImageResource publicLink48();

	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/publicLink72.png")
	public ImageResource publicLink72();

	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/publicLink48.png")
	public ImageResource publicLink96();

	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/refresh.png")
	public ImageResource refresh();

	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/report_16b.gif")
	public ImageResource report16();
	
	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/Masthead/resource_library_2.png")
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
	@Source("org/kablink/teaming/gwt/public/images/sharedAll.png")
	public ImageResource sharedAll();
	
	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/sharedFiles.png")
	public ImageResource sharedFiles();
	
	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/Widgets/sizingArrows.png")
	public ImageResource sizingArrows();
	
	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/Widgets/sizingArrowsGray.gif")
	public ImageResource sizingArrowsGray();
	
	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/spacer_1px.png")
	public ImageResource spacer1px();

	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/spinner16x16.gif")
	public ImageResource spinner16();

	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/spinner25x25.gif")
	public ImageResource spinner25();

	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/spinner32x32.gif")
	public ImageResource spinner32();

	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/spinner36x36.gif")
	public ImageResource spinner36();

	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/spinner48x48.gif")
	public ImageResource spinner48();

	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/spinner72x72.gif")
	public ImageResource spinner72();

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
	@Source("org/kablink/teaming/gwt/public/images/tour_but_teal.png")
	public ImageResource tour3();

	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/arrow_up.png")
	public ImageResource up16();

	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/arrow_up_disabled.png")
	public ImageResource upDisabled16();

	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/arrow_up_over.png")
	public ImageResource upMouseOver16();

	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/warning_36.png")
	public ImageResource warning32();
	
	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/warning_25.png")
	public ImageResource warningIcon16();
	
	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/workspaceLarge.png")
	public ImageResource workspaceImgLarge();
	
	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/WorkspaceTree/folder_blog.png")
	public ImageResource blogFolderLarge();
	
	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/WorkspaceTree/folder_calendar.png")
	public ImageResource calendarFolderLarge();
	
	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/WorkspaceTree/folder_discussion.png")
	public ImageResource discussionFolderLarge();
	
	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/WorkspaceTree/folder_file.png")
	public ImageResource fileFolderLarge();
	
	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images//WorkspaceTree/folder_guestbook.png")
	public ImageResource guestbookFolderLarge();
	
	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/WorkspaceTree/folder_milestone.png")
	public ImageResource milestoneFolderLarge();
	
	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/WorkspaceTree/folder_miniblog.png")
	public ImageResource miniblogFolderLarge();
	
	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/WorkspaceTree/folder_mirrored.png")
	public ImageResource mirroredFileFolderLarge();

	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/OpenIDAuthProviders/aol.png")
	public ImageResource openIdAuthProvider_aol();
	
	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/OpenIDAuthProviders/google.png")
	public ImageResource openIdAuthProvider_google();
	
	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/OpenIDAuthProviders/myopenid.png")
	public ImageResource openIdAuthProvider_myopenid();
	
	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/OpenIDAuthProviders/verisign.png")
	public ImageResource openIdAuthProvider_verisign();
	
	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/OpenIDAuthProviders/yahoo.png")
	public ImageResource openIdAuthProvider_yahoo();
	
	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/OpenIDAuthProviders/unknown.png")
	public ImageResource openIdAuthProvider_unknown();

	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/WorkspaceTree/folder_photo.png")
	public ImageResource photoAlbumFolderLarge();
	
	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/WorkspaceTree/folder_survey.png")
	public ImageResource surveyFolderLarge();
	
	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/WorkspaceTree/folder_task.png")
	public ImageResource taskFolderLarge();
	
	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/trash_button.png")
	public ImageResource trashButton();
	
	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/folder_trash_large.png")
	public ImageResource trashFolderLarge();
	
	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/triangle_w.png")
	public ImageResource triangle();
	
	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/Masthead/admin_console_2.png")
	public ImageResource userActionsPanel_Admin();

	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/Masthead/change_password.png")
	public ImageResource userActionsPanel_ChangePassword();

	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/Masthead/icon_download_Vibe_header_b.png")
	public ImageResource userActionsPanel_DownloadDesktopApp();

	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/Masthead/help_2.png")
	public ImageResource userActionsPanel_Help();
	
	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/Masthead/Filr_Ideas_Portal_Icon.png")
	public ImageResource userActionsPanel_IdeasPortal();

	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/Masthead/personal_prefs_2.png")
	public ImageResource userActionsPanel_PersonalPreferences();

	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/Masthead/view_profile.png")
	public ImageResource userActionsPanel_ViewProfile();

	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/Masthead/view_shared_by_me.png")
	public ImageResource userActionsPanel_ViewSharedByMe();

	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/Masthead/Filr_userlist_25.png")
	public ImageResource userList();

	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/Masthead/Filr_whatsnew_25.png")
	public ImageResource masthead_WhatsNew();

	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/WorkspaceTree/folder_wiki.png")
	public ImageResource wikiFolderLarge();
	
	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/WorkspaceTree/folder_generic.png")
	public ImageResource genericFolderLarge();
	
	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/emailConfirmation.png")
	public ImageResource emailConfirmation();

	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/admin_72.png")
	public ImageResource adminConsoleHomePage();

	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/admin_36.png")
	public ImageResource adminConsole36();
	
	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/admin_system_36.png")
	public ImageResource adminSystem36();

	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/admin_system_72.png")
	public ImageResource adminSystem72();
	
	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/UserPhoto.png")
	public ImageResource userAvatar();
	
	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("org/kablink/teaming/gwt/public/images/list-item-arrow.gif")
	public ImageResource listItemArrow();
}// end GwtTeamingImageBundle
