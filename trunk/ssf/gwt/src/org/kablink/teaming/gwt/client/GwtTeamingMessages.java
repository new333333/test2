/**
 * Copyright (c) 1998-2010 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2010 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2010 Novell, Inc. All Rights Reserved.
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
 * This interface is used to retrieve strings from the file GwtTeamingMessages*.properties
 * @author jwootton
 *
 */
public interface GwtTeamingMessages extends Messages
{
	// Strings used in the "find" name-completion control
	String nOfn( int value1, int value2, int value3 );
	String searching();

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
	String pleaseSelectAnEntry();
	
	// Strings used with the Folder widget in the landing page editor
	String currentFolder();
	String findFolderLabel();
	String folderLabel();
	String folderProperties();
	String numEntriesToShow();
	String pleaseSelectAFolder();
	String showEntriesOpened();
	String showFolderDesc();
	String showTitleBar();
	
	// Strings used with the Graphic widget in the landing page editor.
	String graphicLabel();
	String graphicProperties();
	String noFileAttachmentsHint();
	String selectGraphicLabel();
	
	// lpe stands for Landing Page Editor
	String lpeAltCustomJsp();
	String lpeAltEntry();
	String lpeAltFolder();
	String lpeAltGraphic();
	String lpeAltLinkEntry();
	String lpeAltLinkFolderWorkspace();
	String lpeAltLinkURL();
	String lpeAltList();
	String lpeAltTable();
	String lpeAltUtilityElement();
	String lpeCustomJSP();
	String lpeDeleteWidget();
	String lpeEntry();
	String lpeFolder();
	String lpeGraphic();
	String lpeHint();
	String lpeLinkEntry();
	String lpeLinkFolderWS();
	String lpeLinkURL();
	String lpeList();
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
	String invalidColumnWidth( int colNum );
	String invalidTotalTableWidth();
	String numColumns();
	String tableProperties();

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
	String beta();
	String cancel();
	String ok();
	String oneMomentPlease();
	String percent();
	String showBorder();
	String showTitle();
	String title();
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
	String rpcFailure_CanManagePublicBinderTags();
	String rpcFailure_EntryDoesNotExist();
	String rpcFailure_FolderDoesNotExist();
	String rpcFailure_GetAdminActions();
	String rpcFailure_GetBinderInfo();
	String rpcFailure_GetBinderPermalink();
	String rpcFailure_GetBinderTags();
	String rpcFailure_GetBranding();
	String rpcFailure_GetFolder();
	String rpcFailure_GetFolderDefinitionId();
	String rpcFailure_GetFolderEntry();
	String rpcFailure_GetGeneric();
	String rpcFailure_GwtGroups();
	String rpcFailure_GetImUrl();
	String rpcFailure_GetMyTeams();
	String rpcFailure_GetPersonalPreferences();
	String rpcFailure_GetProfileAvatars();
	String rpcFailure_GetProfileInfo();
	String rpcFailure_GetProfileStats();
	String rpcFailure_GetRecentPlaces();
	String rpcFailure_GetRootWorkspaceId();
	String rpcFailure_GetSavedSearches();
	String rpcFailure_GetSelfRegInfo();
	String rpcFailure_GetStatus();
	String rpcFailure_GetTeamManagement();
	String rpcFailure_GetTeams();
	String rpcFailure_GetToolbarItems();
	String rpcFailure_GetTopRanked();
	String rpcFailure_GetTree();
	String rpcFailure_GetUpgradeInfo();
	String rpcFailure_IsPersonTracked();
	String rpcFailure_QViewMicroBlog();
	String rpcFailure_RemoveBinderTag();
	String rpcFailure_RemoveSavedSearch();
	String rpcFailure_SavePersonalPreferences();
	String rpcFailure_SaveSearch();
	String rpcFailure_Search();
	String rpcFailure_SetStatus();
	String rpcFailure_TrackingBinder();
	String rpcFailure_TrackingPerson();
	String rpcFailure_UnknownCause();
	String rpcFailure_UnknownException();
	String rpcFailure_UntrackingBinder();
	String rpcFailure_UntrackingPerson();
	String rpcFailure_UpdateFavorites();

	// Strings used with the MastHead
	String administrationHint();
	String guest();
	String helpHint();
	String loginHint();
	String logoutHint();
	String myWorkspaceHint();
	String personalPreferencesHint();
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
	String mainMenuBarFolder();
	String mainMenuBarManageSavedSearches();
	String mainMenuBarMyFavorites();
	String mainMenuBarMyTeams();
	String mainMenuBarMyWorkspace();
	String mainMenuBarRecentPlaces();
	String mainMenuBarTopRanked();
	String mainMenuBarViews();
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
	String treeCloseBreadCrumbs();
	
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

	//Profile
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
	
	// Presence related strings
	String presenceAvailable();
	String presenceAway();
	String presenceBusy();
	String presenceIdle();
	String presenceOffline();
	String presenceUnknown();
	String IEUseCompatibilityMode();

	// The following strings are used in the Administration Information dialog
	String adminInfoDlgHeader();
	String adminInfoDlgLoginAsAdmin();
	String adminInfoDlgRelease();
	String adminInfoDlgUpgradeAccessControls();
	String adminInfoDlgUpgradeDefinitions();
	String adminInfoDlgUpgradeSearchIndex();
	String adminInfoDlgUpgradeTasksNotDone();
	String adminInfoDlgUpgradeTemplates();
	
}// end GwtTeamingMessages
