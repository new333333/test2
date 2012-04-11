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

import com.google.gwt.i18n.client.Messages;


/**
 * This interface is used to retrieve strings from the file
 * GwtTeamingMessages*.properties.
 * 
 * @author jwootton
 */
public interface GwtTeamingMessages extends Messages
{
	// Strings used in the "find" name-completion control
	String nOfn( int value1, int value2, int value3 );
	String searching();
	String searchEntireSiteLabel();
	String searchCurrentFolderWorkspaceLabel();

	// Strings used with the Custom Jsp widget in the landing page editor.
	String customJspAssocEntry();
	String customJspAssocFolder();
	String customJspLabel();
	String customJspName();
	String customJspProperties();
	
	// Strings used with the Entry widget in the landing page editor
	String currentEntry();
	String entryLabel();
	String entryProperties();
	String findEntry();
	String noEntrySelected();
	String pleaseSelectAnEntry();
	
	// Strings used with the Folder widget in the landing page editor
	String currentFolder();
	String findFolderLabel();
	String folderLabel();
	String folderProperties();
	String noFolderSelected();
	String numEntriesToShow();
	String pleaseSelectAFolder();
	String showEntriesOpened();
	String showFolderDesc();
	String showTitleBar();
	
	// Strings used with the Googld Gadget widget in the landing page editor
	String googleGadgetCodeLabel();
	String googleGadgetProperties();
	
	// Strings used with the Graphic widget in the landing page editor.
	String graphicLabel();
	String graphicProperties();
	String noFileAttachmentsHint();
	String selectGraphicLabel();
	
	// Strings used in the Enhanced Views widget in the landing page editor
	String enhancedViewLabel();
	String enhancedViewNameLabel();
	String enhancedViewProperties();
	String enhancedViewDisplayEntry();
	String enhancedViewDisplayEntryDesc();
	String enhancedViewDisplayFullEntry();
	String enhancedViewDisplayFullEntryDesc();
	String enhancedViewDisplayRecentEntries();
	String enhancedViewDisplayRecentEntriesDesc();
	String enhancedViewDisplayRecentEntriesList();
	String enhancedViewDisplayRecentEntriesListDesc();
	String enhancedViewDisplayRecentEntriesListSorted();
	String enhancedViewDisplayRecentEntriesListSortedDesc();
	String enhancedViewDisplayFileListSorted();
	String enhancedViewDisplayFileListSortedDesc();
	String enhancedViewDisplayCalendarFolder();
	String enhancedViewDisplayCalendarFolderDesc();
	String enhancedViewDisplayTaskFolder();
	String enhancedViewDisplayTaskFolderDesc();
	String enhancedViewDisplaySurvey();
	String enhancedViewDisplaySurveyDesc();
	String enhancedViewDisplayMyTasks();
	String enhancedViewDisplayMyTasksDesc();
	String enhancedViewDisplayMyCalendarEvents();
	String enhancedViewDisplayMyCalendarEventsDesc();
	
	// lpe stands for Landing Page Editor
	String lpeAltCustomJsp();
	String lpeAltDeleteAll();
	String lpeAltDeleteElement();
	String lpeAltEditElementProperties();
	String lpeAltEditLPProperties();
	String lpeAltEntry();
	String lpeAltFolder();
	String lpeAltGoogleGadget();
	String lpeAltGraphic();
	String lpeAltHtml();
	String lpeAltIFrame();
	String lpeAltLinkEntry();
	String lpeAltLinkFolderWorkspace();
	String lpeAltLinkURL();
	String lpeAltList();
	String lpeAltMoveElement();
	String lpeAltEnhancedView();
	String lpeAltTable();
	String lpeAltUtilityElement();
	String lpeCustomJSP();
	String lpeDeleteAllWarning();
	String lpeDeleteWidget();
	String lpeEditHtml();
	String lpeEntry();
	String lpeFolder();
	String lpeGoogleGadget();
	String lpeGraphic();
	String lpeHint();
	String lpeHtml();
	String lpeIFrame();
	String lpeLinkEntry();
	String lpeLinkFolderWS();
	String lpeLinkURL();
	String lpeList();
	String lpeEnhancedView();
	String lpeTable();
	String lpeUtilityElement();
	
	// Strings used with the "Link to entry" widget
	String linkToEntryLabel();
	String linkToEntryTitleLabel();
	String linkToEntryProperties();
	String openEntryInNewWnd();
	
	// Strings used with the "Link to folder" widget
	String currentFolderWorkspace();
	String folderOrWorkspaceLabel();
	String linkToFolderLabel();
	String linkToFolderProperties();
	String linkToFolderTitleLabel();
	String openFolderInNewWnd();
	String pleaseSelectAFolderOrWorkspace();
	
	// Strings used with the "Link to url" widget
	String linkToUrl();
	String linkToUrlLabel();
	String linkToUrlProperties();
	String linkToUrlUrl( String url );
	String openUrlInNewWnd();
	
	// Strings used in the "List" widget properties dialog
	String listProperties();
	
	// Strings used with the "table" widget
	String columnXWidth( int colNum );
	String emptyColumnWidth( int colNum );
	String invalidColumnWidth( int colNum );
	String invalidNumberOfRows();
	String invalidTotalTableWidth();
	String numColumns();
	String numRows();
	String tableProperties();
	
	// String used in the "IFrame" widget properties dialog.
	String borderLabel();
	String frameNameLabel();
	String heightLabel();
	String iframeProperties();
	String marginHeightLabel();
	String marginWidthLabel();
	String pxLabel();
	String showScrollbarsLabel();
	String showScrollbars_Always();
	String showScrollbars_Auto();
	String showScrollbars_Never();

	String urlLabel();
	String widthLabel();

	// Misc strings
	String _1();
	String _2();
	String _3();
	String _4();
	String _5();
	String _6();
	String _7();
	String _8();
	String _9();
	String _10();
	String betaWithProduct( String productName );
	String cancel();
	String cantAccessEntry();
	String cantAccessFolder();
	String change();
	String edit();
	String find();
	String missingRequestInfo();
	String ok();
	String oneMomentPlease();
	String overflowLabel();
	String percent();
	String send();
	String showBorder();
	String showTitle();
	String title();
	String unknownFileUploadError( String error);
	String vibeInsideLandingPage();
	String close();
	
	// Strings used with the Utility Element widget in the landing page editor.
	String utilityElementHint();
	String utilityElementLabel();
	String utilityElementProperties();
	String utilityElementLinkToAdminPage();
	String utilityElementLinkToMyWorkspace();
	String utilityElementLinkToShareFolderOrWorkspace();
	String utilityElementLinkToTrackFolderOrWorkspace();
	String utilityElementSignInForm();
	String utilityElementVideoTutorials();
	
	String testPanelState( String value );
	String testWaiting();
	
	// Strings used with extensions
	String extensionsName();
	String extensionsDesc();
	String extensionsZone();
	String extensionsRemove();
	String extensionsRemoveFailed();
	String extensionsConfirmDelete();
	String extensionsWaiting();
	String extensionsRPCError();
	
	// Strings used with extensions dlg
	String extensionsDlgDescription();
	String extensionsDlgDeployed();
	String extensionsDlgZoneName();
	String extensionsDlgId();
	String extensionsDlgAuthorName();
	String extensionsDlgAuthorSite();
	String extensionsDlgAuthorEmail();
	String extensionsDlgCreated();
	String extensionsDlgFilesTitle();
	String extensionsDlgFilesError();
	String extensionsDlgVersion();
	String extensionsDlgDateFormat();
	
	// Strings used to describe various RPC failure conditions.
	// Notes:
	// 1. The implementation of GwtClientHelper.handleGwtRPCFailure()
	//    will take care of all parameter substitutions; and
	// 2. Unlike normal messages, the replacement parameters in the
	//    rcpFailure_* messages MUST use '[n]' instead of '{n}' to keep
	//    the GWT compiler happy.
	String rpcFailure_AccessToEntryDenied();
	String rpcFailure_AccessToFolderDenied();
	String rpcFailure_AddBinderTag();
	String rpcFailure_AddFavorite();
	String rpcFailure_AddFavoriteLimitExceeded();
	String rpcFailure_CanManagePublicTags();
	String rpcFailure_CanModifyBinder();
	String rpcFailure_CheckForActivityStreamChanges();
	String rpcFailure_CollapseSubtasks();
	String rpcFailure_CreateGroup();
	String rpcFailure_CreateGroupAlreadyExists();
	String rpcFailure_DeleteGroups();
	String rpcFailure_DeleteTasks();
	String rpcFailure_EntryDoesNotExist();
	String rpcFailure_ExpandBucket();
	String rpcFailure_ExpandSubtasks();
	String rpcFailure_FolderDoesNotExist();
	String rpcFailure_GetActivityStreamParams();
	String rpcFailure_GetActivityStreamsTree();
	String rpcFailure_GetAddMeetingUrl();
	String rpcFailure_GetAdminActions();
	String rpcFailure_GetAllGroups();
	String rpcFailure_GetLandingPageData();
	String rpcFailure_GetListOfAttachments();
	String rpcFailure_GetBinderInfo();
	String rpcFailure_GetBinderPermalink();
	String rpcFailure_GetBranding();
	String rpcFailure_GetDefaultActivityStream();
	String rpcFailure_GetFileSyncAppConfiguration();
	String rpcFailure_GetEntryPermalink();
	String rpcFailure_GetFavorites();
	String rpcFailure_GetFolder();
	String rpcFailure_GetFolderDefinitionId();
	String rpcFailure_GetFolderEntry();
	String rpcFailure_GetGeneric();
	String rpcFailure_GetGroupLdapQuery();
	String rpcFailure_GetGroupMembership();
	String rpcFailure_GetGroupMembershipType();
	String rpcFailure_GwtGroups();
	String rpcFailure_GetGwtUIInfo();
	String rpcFailure_GetImUrl();
	String rpcFailure_GetIsDynamicGroupMembershipAllowed();
	String rpcFailure_GetLocale();
	String rpcFailure_GetMyTeams();
	String rpcFailure_NumberOfMembers();
	String rpcFailure_GetPersonalPreferences();
	String rpcFailure_GetPresenceInfo();
	String rpcFailure_GetProfileAvatars();
	String rpcFailure_GetProfileInfo();
	String rpcFailure_GetProfileStats();
	String rpcFailure_GetRecentPlaces();
	String rpcFailure_GetRootWorkspaceId();
	String rpcFailure_GetSavedSearches();
	String rpcFailure_GetSelfRegInfo();
	String rpcFailure_GetSiteAdminUrl();
	String rpcFailure_GetStatus();
	String rpcFailure_GetSubscriptionData();
	String rpcFailure_GetTags();
	String rpcFailure_GetTagRights();
	String rpcFailure_GetTagSortOrder();
	String rpcFailure_GetTaskLinkage();
	String rpcFailure_GetTaskList();
	String rpcFailure_GetTeamManagement();
	String rpcFailure_GetTeamMembership();
	String rpcFailure_GetTeams();
	String rpcFailure_GetToolbarItems();
	String rpcFailure_GetTopRanked();
	String rpcFailure_GetTrackedPeople();
	String rpcFailure_GetTrackedPlaces();
	String rpcFailure_GetTree();
	String rpcFailure_GetUpgradeInfo();
	String rpcFailure_GetUserPermalink();
	String rpcFailure_GetUserWorkspaceUrl();
	String rpcFailure_GetViewFolderEntryUrl();
	String rpcFailure_IsAllUsersGroup();
	String rpcFailure_IsPersonTracked();
	String rpcFailure_LdapGuidNotConfigured();
	String rpcFailure_markupStringReplacement();
	String rpcFailure_ModifyGroup();
	String rpcFailure_PersistActivityStreamSelection();
	String rpcFailure_PersistExpansionState();
	String rpcFailure_PurgeTasks();
	String rpcFailure_QViewMicroBlog();
	String rpcFailure_RemoveBinderTag();
	String rpcFailure_RemoveFavorite();
	String rpcFailure_RemoveSavedSearch();
	String rpcFailure_ReplyToEntry();
	String rpcFailure_SaveFileSyncAppConfiguration();
	String rpcFailure_SavePersonalPreferences();
	String rpcFailure_SaveSearch();
	String rpcFailure_SaveSubscriptionData();
	String rpcFailure_SaveTags();
	String rpcFailure_SaveTagSortOrder();
	String rpcFailure_SaveTaskCompleted();
	String rpcFailure_SaveTaskDueDate();
	String rpcFailure_SaveTaskLinkage();
	String rpcFailure_SaveTaskPriority();
	String rpcFailure_SaveTaskSort();
	String rpcFailure_SaveTaskStatus();
	String rpcFailure_SaveWhatsNewShowSetting();
	String rpcFailure_Search();
	String rpcFailure_SetSeen();
	String rpcFailure_SetStatus();
	String rpcFailure_SetUnseen();
	String rpcFailure_ShareEntry();
	String rpcFailure_TestGroupMembershipCriteria();
	String rpcFailure_TrackingBinder();
	String rpcFailure_TrackingPerson();
	String rpcFailure_UnknownCause();
	String rpcFailure_UnknownException();
	String rpcFailure_UntrackingBinder();
	String rpcFailure_UntrackingPerson();
	String rpcFailure_UpdateCalculatedDatesBinder();
	String rpcFailure_UpdateCalculatedDatesTask();
	String rpcFailure_UpdateFavorites();

	// Strings used to describe various split point load failures.
	String codeSplitFailure_ActivityStreamCtrl();
	String codeSplitFailure_AdminControl();
	String codeSplitFailure_AdminInfoDlg();
	String codeSplitFailure_ContentControl();
	String codeSplitFailure_EditBrandingDlg();
	String codeSplitFailure_ExtensionsConfig();
	String codeSplitFailure_FindCtrl();
	String codeSplitFailure_FolderOptionsDlg();
	String codeSplitFailure_LandingPage();
	String codeSplitFailure_LandingPageEditor();
	String codeSplitFailure_LoginDlg();
	String codeSplitFailure_MainMenuControl();
	String codeSplitFailure_MainPage();
	String codeSplitFailure_ManageMenuPopup();
	String codeSplitFailure_ManageSavedSearchesDlg();
	String codeSplitFailure_ProfileAttributeWidget();
	String codeSplitFailure_ProfilePage();
	String codeSplitFailure_QuickViewDlg();
	String codeSplitFailure_SearchOptionsComposite();
	String codeSplitFailure_TagThisDlg();
	String codeSplitFailure_TaskListing();
	String codeSplitFailure_TinyMCEDlg();
	String codeSplitFailure_UserStatusControl();
	String codeSplitFailure_ViewsMenuPopup();
	String codeSplitFailure_WorkspaceTreeControl();
	
	// Strings used to describe various event handling errors.
	String eventHandling_NoContextMenuEventHandler(String eventName                  );
	String eventHandling_NoEventHandlerDefined(    String eventName, String className);
	String eventHandling_NonSimpleEvent(           String eventName, String className);
	String eventHandling_UnhandledEvent(           String eventName, String className);
	String eventHandling_Validation_NoHandler(     String eventName, String className);
	String eventHandling_Validation_NotListed(     String eventName, String className);
	String eventHandling_Validation_NoValidator(   String eventName                  );
	
	// Strings used with the MastHead
	String adminMenuItem();
	String administrationHint();
	String guest();
	String helpMenuItem();
	String helpHint();
	String loginHint();
	String logoutHint();
	String myWorkspaceHint();
	String newsFeedMenuItem();
	String personalPrefsMenuItem();
	String personalPreferencesHint();
	String resourceLibMenuItem();
	String resourceLibraryHint();
	String teamingFeedHint();
	
	// Strings used in the edit branding dialog.
	String addImage();
	String advancedBtn();
	String backgroundColorLabel();
	String binderOverridesBrandingLabel();
	String brandingDlgHeader();
	String brandingRulesLabel();
	String backgroundImgLabel();
	String cantEditBranding();
	String clearBrandingLabel();
	String colorHint();
	String displayColorPicker();
	String editAdvancedBranding();
	String imgNone();
	String invalidBackgroundColor( String color );
	String invalidTextColor( String color );
	String kablinkTeaming();
	String noImagesAvailable();
	String novellTeaming();
	String sampleText();
	String siteAndBinderBrandingLabel();
	String siteBrandingOnlyLabel();
	String stretchImg();
	String textColorLabel();
	String useBrandingImgLabel();
	String useAdvancedBrandingLabel();
	
	// Strings used in the login dialog.
	String loginDlgAuthenticating();
	String loginDlgCreateNewAccount();
	String loginDlgKablinkHeader();
	String loginDlgLoginFailed();
	String loginDlgNovellHeader();
	String loginDlgPassword();
	String loginDlgReset();
	String loginDlgUserId();
	
	// Strings used in the personal preferences dialog.
	String accessibleMode();
	String editorOverridesLabel();
	String entryDisplayStyleLabel();
	String numEntriesPerPageCannotBeBlank();
	String numEntriesPerPageInvalidNum();
	String numEntriesPerPageLabel();
	String personalPreferencesDlgHeader();
	String showEntriesAsAnOverlay();
	String showEntriesInNewPage();
	String showEntriesInPopupWnd();
	String showToolTips();
	String showTutorialPanel();
	
	// Strings used in the main menu.
	String mainMenuAltBrowseHierarchy();
	String mainMenuAltGwtUI();
	String mainMenuAltLeftNavHideShow();
	String mainMenuAltMastHeadHideShow();
	String mainMenuAltSearchOptions();
	String mainMenuBarActivityStreams();
	String mainMenuBarFolder();
	String mainMenuBarManageSavedSearches();
	String mainMenuBarMyFavorites();
	String mainMenuBarMyTeams();
	String mainMenuBarMyWorkspace();
	String mainMenuBarRecentPlaces();
	String mainMenuBarTopRanked();
	String mainMenuBarViews();
	String mainMenuBarWhatsNew();
	String mainMenuBarWorkspace();
	String mainMenuFavoritesAdd();
	String mainMenuFavoritesDlgDelete();
	String mainMenuFavoritesDlgMoveDown();
	String mainMenuFavoritesDlgMoveUp();
	String mainMenuFavoritesEdit();
	String mainMenuFavoritesEditDlgHeader();
	String mainMenuFavoritesNoFavorites();
	String mainMenuFavoritesRemove();
	String mainMenuFolderOptionsDlgConfigure();
	String mainMenuFolderOptionsDlgFolderViews();
	String mainMenuFolderOptionsDlgHeader();
	String mainMenuFolderOptionsDlgImportCalendar();
	String mainMenuFolderOptionsDlgImportTask();
	String mainMenuFolderOptionsNoOptions();
	String mainMenuManageEditTeam();
	String mainMenuManageEmailTeam();
	String mainMenuManageFolderOptions();
	String mainMenuManageSavedSearchesDlgDeleteSearch();
	String mainMenuManageSavedSearchesDlgErrorSearchDuplicate();
	String mainMenuManageSavedSearchesDlgErrorSearchHasInvalidData();
	String mainMenuManageSavedSearchesDlgHeader();
	String mainMenuManageSavedSearchesDlgLinks();
	String mainMenuManageSavedSearchesDlgNoItems();
	String mainMenuManageSavedSearchesDlgSave();
	String mainMenuManageSavedSearchesDlgSavedSearches();
	String mainMenuManageSavedSearchesDlgSaveSearch();
	String mainMenuManageSavedSearchesDlgWarningNameTruncated();
	String mainMenuManageStartTeamConference();
	String mainMenuManageTagThisFolder();
	String mainMenuManageTagThisWorkspace();
	String mainMenuManageViewTeam();
	String mainMenuMyTeamsNoTeams();
	String mainMenuMyGroupsNoGroups();
	String mainMenuRecentPlacesNoPlaces();
	String mainMenuSearchButtonAlt();
	String mainMenuSearchOptionsAdvancedSearch();
	String mainMenuSearchOptionsCloseAlt();
	String mainMenuSearchOptionsHeader();
	String mainMenuSearchOptionsInvalidResult();
	String mainMenuSearchOptionsNoSavedSearches();
	String mainMenuSearchOptionsPeople();
	String mainMenuSearchOptionsPlaces();
	String mainMenuSearchOptionsSavedSearches();
	String mainMenuSearchOptionsSelectASearch();
	String mainMenuSearchOptionsTags();
	String mainMenuTagThisDlgAdd();
	String mainMenuTagThisDlgAddCommunityAlt();
	String mainMenuTagThisDlgAddPersonalAlt();
	String mainMenuTagThisDlgCommunityTags();
	String mainMenuTagThisDlgDelete();
	String mainMenuTagThisDlgErrorDuplicateTag();
	String mainMenuTagThisDlgErrorTagHasPunctuation();
	String mainMenuTagThisDlgErrorTagHasSpaces();
	String mainMenuTagThisDlgErrorTagHasUnderscores();
	String mainMenuTagThisDlgHeaderFolder();
	String mainMenuTagThisDlgHeaderWorkspace();
	String mainMenuTagThisDlgNoTags();
	String mainMenuTagThisDlgPersonalTags();
	String mainMenuTagThisDlgTags();
	String mainMenuTagThisDlgWarningTagTruncated();
	String mainMenuTopRankedDlgHeader();
	String mainMenuTopRankedDlgNoItems();
	String mainMenuTopRankedDlgPeople();
	String mainMenuTopRankedDlgPlaces();
	String mainMenuTopRankedDlgRating();
	String mainMenuTopRankedDlgTopRankedPlaces();
	String mainMenuTopRankedDlgTopRankedPeople();
	String mainMenuViewsWhatsNewInFolder();
	String mainMenuViewsWhatsNewInWorkspace();
	String mainMenuViewsWhatsUnreadInFolder();
	String mainMenuViewsWhatsUnreadInWorkspace();

	// Strings used in the workspace tree control.
	String treeBucketHover( String firstPart, String lastPart );
	String treeCloseActivityStreams();
	String treeCloseActivityStreamsHint();
	String treeCloseBreadCrumbs();
	String treeSiteWide();
	
	// Strings used in the "Add File Attachment" dialog.
	String addFileAttachmentDlgHeader();
	String addFileLabel();
	String noFilesSelectedMsg();
	
	// Strings used in the "Color picker" dialog
	String aliceBlue();
	String antiqueWhite();
	String aqua(); 
	String aquaMarine();
	String azure();
	String beige();
	String bisque();
	String black();
	String blanchedAlmond();
	String blue();
	String blueViolet();
	String brown();
	String burlyWood();
	String cadetBlue();
	String chartreuse();
	String chocolate();
	String coral();
	String cornflowerBlue();
	String cornSilk();
	String crimson();
	String cyan();
	String darkBlue();
	String darkCyan();
	String darkGoldenRod();
	String darkGray();
	String darkGreen();
	String darkKhaki();
	String darkMagenta();
	String darkOliveGreen();
	String darkOrange();
	String darkOrchid();
	String darkRed();
	String darkSalmon();
	String darkSeaGreen();
	String darkSlateBlue();
	String darkSlateGray();
	String darkTurquoise();
	String darkViolet();
	String deepPink();
	String deepSkyBlue();
	String dimGray();
	String dodgerBlue();
	String fireBrick();
	String floralWhite();
	String forestGreen();
	String fuchsia();
	String gainsboro();
	String ghostWhite();
	String gold();
	String goldenRod();
	String gray();
	String green();
	String greenYellow();
	String honeyDew();
	String hotPink();
	String indianRed();
	String indigo();
	String ivory();
	String khaki();
	String lavender();
	String lavenderBlush();
	String lawnGreen();
	String lemonChiffon();
	String lightBlue();
	String lightCoral();
	String lightCyan();
	String lightGoldenRodYellow();
	String lightGrey();
	String lightGreen();
	String lightPink();
	String lightSalmon();
	String lightSeaGreen();
	String lightSkyBlue();
	String lightSlateGray();
	String lightSteelBlue();
	String lightYellow();
	String lime();
	String limeGreen();
	String linen();
	String magenta();
	String maroon();
	String mediumAquaMarine();
	String mediumBlue();
	String mediumOrchid();
	String mediumPurple();
	String mediumSeaGreen();
	String mediumSlateBlue();
	String mediumSpringGreen();
	String mediumTurquoise();
	String mediumVioletRed();
	String midnightBlue();
	String mintCream();
	String mistyRose();
	String moccasin();
	String navajoWhite();
	String navy();
	String oldLace();
	String olive();
	String oliveDrab();
	String orange();
	String orangeRed();
	String orchid();
	String paleGoldenRod();
	String paleGreen();
	String paleTurquoise();
	String paleVioletRed();
	String papayaWhip();
	String peachPuff();
	String peru();
	String pink();
	String plum();
	String powderBlue();
	String purple();
	String red();
	String rosyBrown();
	String royalBlue();
	String saddleBrown();
	String salmon();
	String sandyBrown();
	String seaGreen();
	String seaShell();
	String sienna();
	String silver();
	String skyBlue();
	String slateBlue();
	String slateGray();
	String snow();
	String springGreen();
	String steelBlue();
	String tan();
	String teal();
	String thistle();
	String tomato();
	String turquoise();
	String violet();
	String wheat();
	String white();
	String whiteSmoke();
	String yellow();
	String yellowGreen();
	String colorName();
	String colorPickerDlgHeader();
	String hexValue();
	
	// The following strings are used with a tinyMCE editor
	String addFile();
	String addUrl();
	String imageName();
	String insertLinkToTeamingPage();
	String missingImage();
	String overQuota();
	String showHideToolbars();
	String srcFile();
	String youTubeDimensions();
	String youTubeTitle();
	String youTubeUrl();
	
	// The following strings are for the User Status or Micro blog Control
	String statusMessage();
	String clearStatus();
	String shareStatus();
	String oneSecondAgo();
	String secondsAgo(long seconds);
	String oneMinuteAgo();
	String minutesAgo(long value);
	String oneHourAgo();
	String hoursAgo(long value);
	String oneDayAgo();
	String daysAgo(long value);
	String now();
	String charactersTyped(int value);
	String exceededMax(int value);
	String exceededError();
	String clearCurrentStatus();

	
	// The following strings are used with the Administration page.
	String administrationHeader();
	String managementCategory();
	String reportCategory();
	String systemCategory();
	
	// The following string are used with the Quick View popup
	String qViewProfile();
	String qViewProfileTitle();
	String qViewWorkspace();
	String qViewWorkspaceTitle();
	String qViewConference();
	String qViewConferenceTitle();
	String qViewFollow();
	String qViewFollowTitle();
	String qViewFollowing();
	String qViewFollowingTitle();
	String qViewMicroBlog();
	String qViewMicroBlogTitle();
	String qViewInstantMessage();
	String qViewInstantMessageTitle();
	String qViewErrorWorkspaceDoesNotExist();
	String qViewErrorDeletedWorkspace();
	String qViewErrorNoProfile();

	//Profile
	String profileCallMe();
	String profileEdit();
	String profileDelete();
	String profileEditTitle();
	String profileNotFollowing();
	String profileNoSavedSearches();
	String profileSavedSearches();
	String profileTeams();
	String profileGroups();
	String profileFollowing();
	String profileAboutMe();
	String profileDataQuota();
	String profileQuotaUsed();
	String profileSetDefaultAvatar();
	String profileRemoveAvatar();
	String profileUpload();
	String profileUploadSelect();
	
	// Presence related strings
	String presenceAvailable();
	String presenceAway();
	String presenceBusy();
	String presenceIdle();
	String presenceOffline();
	String presenceUnknown();

	// The following strings are used in the Administration Information dialog
	String adminInfoDlgHeader();
	String adminInfoDlgLoginAsAdmin();
	String adminInfoDlgRelease();
	String adminInfoDlgUpgradeDefinitions();
	String adminInfoDlgUpgradeSearchIndex();
	String adminInfoDlgUpgradeTasksNotDone();
	String adminInfoDlgUpgradeTemplates();

	// The following strings are used in the activity stream control.
	String actionsLabel();
	String autoRefreshIsPaused();
	String followedPeople();
	String followedPlaces();
	String markEntryAsReadHint();
	String myFavorites();
	String myTeams();
	String multipleComments( int numComments );
	String nextRefresh( String time );
	String noEntriesFound();
	String noFavorites();
	String noPeopleFollowed();
	String noPlacesFollowed();
	String noTeams();
	String noTitle();
	String oneComment();
	String pauseActivityStream( int refreshRate );
	String refresh();
	String resumeActivityStream();
	String selectEntryDisplayStyle();
	String siteWide();
	String showAll();
	String showAllEntries();
	String showUnread();
	String showUnreadEntries();
	String whatsNew();
	String whatsNewWithName( String name );
	
	// The following strings are used in the activity stream "reply to entry" ui
	String defaultReplyTitle( String title );
	String noReplyText();
	String replyToEntryLabel();
	
	// The following strings are used in the activity stream "share this entry" ui.
	String addRecipient();
	String commentsLabel();
	String defaultShareTitle( String title );
	String noShareRecipients();
	String noShareRecipientsOrTeams();
	String removeRecipientHint();
	String shareCaption();
	String shareErrors();
	String shareHint();
	String shareName();
	String shareType();
	String shareTypeGroup();
	String shareTypeUser();
	String shareWithGroups();
	String shareWithTeams();
	String shareWithUsers();
	String unknownShareType();
	String usersWithoutRights();
	
	// The following strings are used in the Group Membership popup
	String allUsersGroupDesc();
	String noGroupMembers();
	String unknownGroupMemberType();
	
	// The following strings are used as the hover text over various
	// binder type images in the sidebar workspace tree control.
	String hoverBucket();
	String hoverFolder();
	String hoverFolderBlog();
	String hoverFolderCalendar();
	String hoverFolderDiscussion();
	String hoverFolderFile();
	String hoverFolderGuestbook();
	String hoverFolderMilestones();
	String hoverFolderMiniBlog();
	String hoverFolderMirroredFiles();
	String hoverFolderPhotoAlbum();
	String hoverFolderSurvey();
	String hoverFolderTask();
	String hoverFolderTrash();
	String hoverFolderWiki();
	String hoverWorkspace();
	String hoverWorkspaceDiscussions();
	String hoverWorkspaceGlobalRoot();
	String hoverWorkspaceLandingPage();
	String hoverWorkspacePersonal();
	String hoverWorkspaceProfileRoot();
	String hoverWorkspaceProjectManagement();
	String hoverWorkspaceTeam();
	String hoverWorkspaceTeamRoot();
	String hoverWorkspaceTop();
	String hoverWorkspaceTrash();

	// The following strings are used in the "Actions" popup menu.
	String markRead();
	String markUnread();
	String reply();
	String share();
	String subscribe();
	String tag();
	
	// The following strings are used in the "Subscribe to Entry" dialog.
	String cantSubscribeNoEmailAddresses();
	String sendEmailTo();
	String sendEmailWithoutAttachmentsTo();
	String sendTextTo();
	String subscribeToEntryDlgHeader();
	String subscribeToEntryHeader();

	// The following strings are used with the "Tag This" dialog.
	String addTag();
	String community();
	String deleteTagHint();
	String noTagRights();
	String noTagsForEntry();
	String noTagsForFolder();
	String noTagsForWorkspace();
	String listOfEntryTagsLabel();
	String listOfFolderTagsLabel(); 
	String listOfWorkspaceTagsLabel();
	String personal();
	String promptSaveBeforeTagSearch( String tagName );
	String tagHeader();
	String tagName();
	String tagThisEntry();
	String tagType();
	String unknownTagType();
	
	// The following are used for task disposition dialog.
	String taskDispositionDlgHeader();
	String taskDispositionDlgHint(String selectedTaskName);
	String taskDispositionDlgInsertAfter();
	String taskDispositionDlgInsertAppend();
	String taskDispositionDlgInsertAsSubtask();
	String taskDispositionDlgInsertBefore();
	
	// The following are used for task due date editing dialog.
	String taskDueDateDlgConfirm_DefaultTo1Day();
	String taskDueDateDlgError_DurationInvalidCombination();
	String taskDueDateDlgError_NoEnd();
	String taskDueDateDlgError_NoStartNoEnd();
	String taskDueDateDlgError_NoStart();
	String taskDueDateDlgHeader();
	String taskDueDateDlgLabelAllDay();
	String taskDueDateDlgLabelClearAll();
	String taskDueDateDlgLabelDays();
	String taskDueDateDlgLabelDuration();
	String taskDueDateDlgLabelStart();
	String taskDueDateDlgLabelEnd();

	// The following are used for task folder listing.
	String taskAltDateCalculated();
	String taskAltDelete();
	String taskAltFilterOff();
	String taskAltFilterOn();
	String taskAltHierarchyDisabled();
	String taskAltMoveDown();
	String taskAltMoveLeft();
	String taskAltMoveRight();
	String taskAltMoveUp();
	String taskAltPurge();
	String taskAltTaskClosed();
	String taskAltTaskUnread();
	String taskCantMove_Filter();
	String taskCantMove_NoMoveableTasksSelected();
	String taskCantMove_Order();
	String taskCantMove_Rights();
	String taskCantMove_Virtual();
	String taskCantMove_Zero();
	String taskColumn_assignedTo();
	String taskColumn_closedPercentDone();
	String taskColumn_dueDate();
	String taskColumn_location();
	String taskColumn_name();
	String taskColumn_order();
	String taskColumn_priority();
	String taskColumn_status();
	String taskCompleted_c0();
	String taskCompleted_c10();
	String taskCompleted_c20();
	String taskCompleted_c30();
	String taskCompleted_c40();
	String taskCompleted_c50();
	String taskCompleted_c60();
	String taskCompleted_c70();
	String taskCompleted_c80();
	String taskCompleted_c90();
	String taskCompleted_c100();
	String taskConfirmDelete();
	String taskConfirmPurge();
	String taskDebug_times(String taskCount, String readTime, String showTime, String totalTime);
	String taskFilter_empty();
	String taskHierarchyDisabled();
	String taskHierarchyDisabled_Filter();
	String taskHierarchyDisabled_Rights();
	String taskHierarchyDisabled_Sort();
	String taskHierarchyDisabled_Virtual();
	String taskHierarchyDisabledDlgBanner();
	String taskHierarchyDisabledDlgHeader();
	String taskInternalError_FilteredOrVirtual(String operation);
	String taskInternalError_UnexpectedEvent(String event);
	String taskInternalError_UnexpectedViewOption(String viewOption);
	String taskLabelDelete();
	String taskLabelOrder();
	String taskLabelPurge();
	String taskLabelSubtask();
	String taskMemberCount(String count);
	String taskNewAbove();
	String taskNewBelow();
	String taskNewSubtask();
	String taskNoDueDate();
	String taskNoTasks();
	String taskPleaseWait_Loading();
	String taskPleaseWait_Rendering();
	String taskPriority_p1();
	String taskPriority_p2();
	String taskPriority_p3();
	String taskPriority_p4();
	String taskPriority_p5();
	String taskProcess_move();
	String taskProcess_resize();
	String taskProcess_selectAll();
	String taskProcess_unSelectAll();
	String taskProcess_updatingDates();
	String taskShowMore();
	String taskStatus_cancelled();
	String taskStatus_completed();
	String taskStatus_inProcess();
	String taskStatus_needsAction();
	String taskUnread();
	String taskView();
	String taskViewAllEntries();
	String taskViewCompleted();
	String taskViewToday();
	String taskViewWeek();
	String taskViewMonth();
	String taskViewAllActive();
	String taskViewAssignedTasks();
	String taskViewFromFolder();
	
	// Strings used in the Landing Page Properties Dialog
	String landingPagePropertiesDlgHeader();
	String backgroundRepeatLabel();
	String backgroundRepeat();
	String backgroundRepeatX();
	String backgroundRepeatY();
	String backgroundNoRepeat();

	// Strings used in the Novell Desktop App dialog (File Sync)
	String fileSyncAppAutoUpdateUrlLabel();
	String fileSyncAppDlgHeader();
	String fileSyncAppIntervalLabel();
	String fileSyncAppMinutesLabel();
	String fileSyncAppOff();
	String fileSyncAppOn();
	String fileSyncAppOnOffLabel();
	
	// Strings used in the Manage Groups dialog
	String manageGroupsDlgAddGroupLabel();
	String manageGroupsDlgConfirmDelete( String groupNames );
	String manageGroupsDlgCreatingGroup();
	String manageGroupsDlgDeleteGroupLabel();
	String manageGroupsDlgDeletingGroup();
	String manageGroupsDlgEditGroupLabel();
	String manageGroupsDlgHeader();
	String manageGroupsDlgModifyingGroup();
	String manageGroupsDlgNameCol();
	String manageGroupsDlgNoGroupsLabel();
	String manageGroupsDlgSelectGroupToDelete();
	String manageGroupsDlgSelect1GroupToEdit();
	String manageGroupsDlgTitleCol();
	String manageGroupsDlgUnknownStatus();
	
	// Strings used in the Modify Group dialog
	String addGroupDlgHeader();
	String modifyGroupDlgEditGroupMembershipLabel();
	String modifyGroupDlgDescriptionLabel();
	String modifyGroupDlgDynamicLabel();
	String modifyGroupDlgDynamicGroupMembershipNotAllowed();
	String modifyGroupDlgHeader( String groupTitle );
	String modifyGroupDlgNameLabel();
	String modifyGroupDlgNameRequired();
	String modifyGroupDlgNameTooLong();
	String modifyGroupDlgStaticLabel();
	String modifyGroupDlgTitleLabel();

	// Strings used in the Modify static membership dialog
	String modifyStaticMembershipDlgDeleteLabel();
	String modifyStaticMembershipDlgGroupLabel();
	String modifyStaticMembershipDlgGroupTab();
	String modifyStaticMembershipDlgHeader( String groupTitle );
	String modifyStaticMembershipDlgNameCol();
	String modifyStaticMembershipDlgNoGroupsLabel();
	String modifyStaticMembershipDlgNoUsersLabel();
	String modifyStaticMembershipDlgSelectGroupToRemove();
	String modifyStaticMembershipDlgSelectUserToRemove();
	String modifyStaticMembershipDlgUserLabel();
	String modifyStaticMembershipDlgUserTab();

	// Strings used in the Modify dynamic membership dialog
	String modifyDynamicMembershipDlgBaseDnLabel();
	String modifyDynamicMembershipDlgCurrentMembershipLabel( int count );
	String modifyDynamicMembershipDlgCurrentMembershipCalculatingLabel();
	String modifyDynamicMembershipDlgHeader();
	String modifyDynamicMembershipDlgLdapFilterLabel();
	String modifyDynamicMembershipDlgSearchSubtreeLabel();
	String modifyDynamicMembershipDlgTestQueryInProgressLabel();
	String modifyDynamicMembershipDlgTestQueryLabel();
	String modifyDynamicMembershipDlgTestQueryResults( int count );
	String modifyDynamicMembershipDlgUpdateLabel();

	// Strings used in the "Edit graphic properties" dialog
	String editGraphicPropertiesDlgSetImageSize();
	
}// end GwtTeamingMessages
