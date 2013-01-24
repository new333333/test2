/**
 * Copyright (c) 1998-2013 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2013 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2013 Novell, Inc. All Rights Reserved.
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
	// Base product names.  Non-translatable?
	String companyNovell();
	String productFilr();
	String productVibe();
	
	// Strings used in the "find" name-completion control
	String nOfn( int value1, int value2, int value3 );
	String searching();
	String searchEntireSiteLabel();
	String searchCurrentFolderWorkspaceLabel();

	// Strings used in the "Preview landing page" dialog.
	String previewLandingPageDlgHeader();
	
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
	String enhancedViewDisplayMyCalendarEvents();
	String enhancedViewDisplayMyCalendarEventsDesc();
	String enhancedViewDisplayMyTasks();
	String enhancedViewDisplayMyTasksDesc();
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
	String lpeAltPreviewLandingPage();
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
	String login();
	String missingRequestInfo();
	String none();
	String ok();
	String oneMomentPlease();
	String overflowLabel();
	String percent();
	String publicName();
	String send();
	String shareExternal();
	String shareInternal();
	String sharePublic();
	String showBorder();
	String showTitle();
	String signOut();
	String title();
	String unknownFileUploadError( String error);
	String vibeInsideLandingPage();
	String close();
	String yes();
	String no();
	
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
	String rpcFailure_AddNewFolder();
	String rpcFailure_CanAddFolder();
	String rpcFailure_CanManagePublicTags();
	String rpcFailure_CanModifyBinder();
	String rpcFailure_ChangeEntryTypes();
	String rpcFailure_ChangeFavoriteState();
	String rpcFailure_CheckForActivityStreamChanges();
	String rpcFailure_CheckNetFoldersStatus();
	String rpcFailure_CheckNetFolderRootsStatus();
	String rpcFailure_CollapseSubtasks();
	String rpcFailure_CopyEntries();
	String rpcFailure_CreateBlogPage();
	String rpcFailure_CreateEmailReport();
	String rpcFailure_CreateGroup();
	String rpcFailure_CreateGroupAlreadyExists();
	String rpcFailure_CreateLicenseReport();
	String rpcFailure_CreateLoginReport();
	String rpcFailure_CreateUserAccessReport();
	String rpcFailure_CreateUserActivityReport();
	String rpcFailure_DeleteNetFolders();
	String rpcFailure_DeleteNetFolderServers();
	String rpcFailure_DeleteFolderEntries();
	String rpcFailure_DeleteGroups();
	String rpcFailure_DeleteUserWorkspaces();
	String rpcFailure_DeleteTasks();
	String rpcFailure_DisableUsers();
	String rpcFailure_DisableUsersAdHocFolders();
	String rpcFailure_EnableUsers();
	String rpcFailure_EnableUsersAdHocFolders();
	String rpcFailure_EntryDoesNotExist();
	String rpcFailure_executeCustomJsp();
	String rpcFailure_executeEnhancedViewJsp();
	String rpcFailure_ExpandBucket();
	String rpcFailure_ExpandSubtasks();
	String rpcFailure_FindUserByEmailAddress();
	String rpcFailure_FolderDoesNotExist();
	String rpcFailure_GetAccessoryStatus();
	String rpcFailure_GetActivityStreamParams();
	String rpcFailure_GetActivityStreamsTree();
	String rpcFailure_GetAddMeetingUrl();
	String rpcFailure_GetAdhocFolderSetting();
	String rpcFailure_GetAdminActions();
	String rpcFailure_GetAllNetFolders();
	String rpcFailure_GetAllNetFolderServers();
	String rpcFailure_GetAllGroups();
	String rpcFailure_GetBinderDescription();
	String rpcFailure_GetBinderFilters();
	String rpcFailure_GetBinderOwnerAvatarUrl();
	String rpcFailure_GetBinderStats();
	String rpcFailure_GetBlogArchiveInfo();
	String rpcFailure_GetBlogPages();
	String rpcFailure_GetCalendarAppointments();
	String rpcFailure_GetCalendarDisplayData();
	String rpcFailure_GetCalendarNextPreviousPeriod();
	String rpcFailure_GetClipboardTeamUsers();
	String rpcFailure_GetClipboardUsers();
	String rpcFailure_GetClipboardUsersFromList();
	String rpcFailure_GetCollectionPointUrl();
	String rpcFailure_GetDesktopAppDownloadInfo();
	String rpcFailure_GetDownloadFileUrl();
	String rpcFailure_GetEmailNotificationInfo();
	String rpcFailure_GetEntityActionToolbarItems();
	String rpcFailure_GetEntityPermalink();
	String rpcFailure_GetEntryComments();
	String rpcFailure_GetEntryTypes();
	String rpcFailure_getExecuteJspUrl();
	String rpcFailure_GetFileConflictsInfo();
	String rpcFailure_GetFileFolderEntries();
	String rpcFailure_GetFileUrl();
	String rpcFailure_GetFolderColumns();
	String rpcFailure_GetFolderDisplayData();
	String rpcFailure_GetFolderEntries();
	String rpcFailure_GetFolderEntryDetails();
	String rpcFailure_GetFolderRows();
	String rpcFailure_GetFolderSortSetting();
	String rpcFailure_GetFolderToolbarItems();
	String rpcFailure_GetFooterToolbarItems();
	String rpcFailure_GetLandingPageData();
	String rpcFailure_GetListOfAttachments();
	String rpcFailure_GetBinderAccessories();
	String rpcFailure_GetBinderAccessory();
	String rpcFailure_GetBinderInfo();
	String rpcFailure_GetBinderPermalink();
	String rpcFailure_GetBranding();
	String rpcFailure_GetChangeLogsHtml();
	String rpcFailure_GetCreditsHtml();
	String rpcFailure_GetDataQuotaExceededHtml();
	String rpcFailure_GetDataQuotaHighwaterExceededHtml();
	String rpcFailure_GetDefaultActivityStream();
	String rpcFailure_GetDiskUsageHtml();
	String rpcFailure_GetFileSyncAppConfiguration();
	String rpcFailure_GetEntryPermalink();
	String rpcFailure_GetFavorites();
	String rpcFailure_GetFolder();
	String rpcFailure_GetFolderDefinitionId();
	String rpcFailure_GetFolderEntry();
	String rpcFailure_GetGeneric();
	String rpcFailure_GetGroupLdapQuery();
	String rpcFailure_GetGroupMembershipType();
	String rpcFailure_GetGroupMembership();
	String rpcFailure_GwtGroups();
	String rpcFailure_GetGwtUIInfo();
	String rpcFailure_GetHelpUrl();
	String rpcFailure_GetImUrl();
	String rpcFailure_GetInheritedLandingPageProperties();
	String rpcFailure_GetIsDynamicGroupMembershipAllowed();
	String rpcFailure_GetListOfChildBinders();
	String rpcFailure_GetLocale();
	String rpcFailure_GetMainPageInfo();
	String rpcFailure_GetManageUsersInfo();
	String rpcFailure_GetManageUsersState();
	String rpcFailure_GetMobileAppsConfiguration();
	String rpcFailure_GetMyFilesContainerInfo();
	String rpcFailure_GetMyTeams();
	String rpcFailure_NumberOfMembers();
	String rpcFailure_GetNetFolder();
	String rpcFailure_GetNextPreviousFolderEntryInfo();
	String rpcFailure_GetParentBinderPermalink();
	String rpcFailure_GetPersonalPreferences();
	String rpcFailure_GetPresenceInfo();
	String rpcFailure_GetProfileAvatars();
	String rpcFailure_GetProfileEntryInfo();
	String rpcFailure_GetProfileInfo();
	String rpcFailure_GetProfileStats();
	String rpcFailure_GetProjectInfo();
	String rpcFailure_GetRecentPlaces();
	String rpcFailure_GetReportsInfo();
	String rpcFailure_GetRootWorkspaceId();
	String rpcFailure_GetSavedSearches();
	String rpcFailure_GetSelfRegInfo();
	String rpcFailure_GetSendToFriendUrl();
	String rpcFailure_GetShareBinderPageUrl();
	String rpcFailure_GetSharedViewState();
	String rpcFailure_GetSharingInfo();
	String rpcFailure_GetSignGuestbookUrl();
	String rpcFailure_GetSiteAdminUrl();
	String rpcFailure_GetStatus();
	String rpcFailure_GetSubscriptionData();
	String rpcFailure_GetSystemErrorLogUrl();
	String rpcFailure_GetTags();
	String rpcFailure_GetTagRights();
	String rpcFailure_GetTagSortOrder();
	String rpcFailure_GetTaskDisplayData();
	String rpcFailure_GetTaskLinkage();
	String rpcFailure_GetTaskList();
	String rpcFailure_GetTeamManagement();
	String rpcFailure_GetTeamMembership();
	String rpcFailure_GetTeams();
	String rpcFailure_GetToolbarItems();
	String rpcFailure_GetTopRanked();
	String rpcFailure_GetTrackedPeople();
	String rpcFailure_GetTrackedPlaces();
	String rpcFailure_GetTrashUrl();
	String rpcFailure_GetTree();
	String rpcFailure_GetUpgradeInfo();
	String rpcFailure_GetUserAccessInfo();
	String rpcFailure_GetUserFileSyncAppConfig();
	String rpcFailure_GetUserMobileAppsConfiguration();
	String rpcFailure_GetUserPermalink();
	String rpcFailure_GetUserProperties();
	String rpcFailure_GetUserSharingRightsInfo();
	String rpcFailure_GetUserWorkspaceInfo();
	String rpcFailure_GetUserWorkspaceUrl();
	String rpcFailure_GetViewFileUrl();
	String rpcFailure_GetViewFolderEntryUrl();
	String rpcFailure_GetViewInfo();
	String rpcFailure_GetWhoHasAccess();
	String rpcFailure_GetWorkspaceContributorIds();
	String rpcFailure_GetXssHtml();
	String rpcFailure_GetZoneShareRights();
	String rpcFailure_HideShares();
	String rpcFailure_ImportIcalByUrl();
	String rpcFailure_IsAllUsersGroup();
	String rpcFailure_IsPersonTracked();
	String rpcFailure_LdapGuidNotConfigured();
	String rpcFailure_LockEntries();
	String rpcFailure_markupStringReplacement();
	String rpcFailure_ModifyGroup();
	String rpcFailure_MoveEntries();
	String rpcFailure_PersistActivityStreamSelection();
	String rpcFailure_PersistExpansionState();
	String rpcFailure_PinEntry();
	String rpcFailure_PurgeFolderEntries();
	String rpcFailure_PurgeTasks();
	String rpcFailure_PurgeUsers();
	String rpcFailure_PurgeUserWorkspaces();
	String rpcFailure_QViewMicroBlog();
	String rpcFailure_RemoveBinderTag();
	String rpcFailure_RemoveFavorite();
	String rpcFailure_RemoveSavedSearch();
	String rpcFailure_RenameEntity_File();
	String rpcFailure_RenameEntity_Folder();
	String rpcFailure_RenameEntity_Unknown();
	String rpcFailure_RenameEntity_Workspace();
	String rpcFailure_ReplyToEntry();
	String rpcFailure_SaveAccessoryStatus();
	String rpcFailure_SaveAdhocFolderSetting();
	String rpcFailure_SaveBinderRegionState();
	String rpcFailure_SaveCalendarDayView();
	String rpcFailure_SaveCalendarHours();
	String rpcFailure_SaveCalendarSettings();
	String rpcFailure_SaveCalendarShow();
	String rpcFailure_SaveClipboardUsers();
	String rpcFailure_SaveColumnWidths();
	String rpcFailure_SaveEmailNotificationInfo();
	String rpcFailure_SaveFileSyncAppConfiguration();
	String rpcFailure_SaveFolderColumns();
	String rpcFailure_SaveFolderEntryDlgPosition();
	String rpcFailure_SaveFolderPinningState();
	String rpcFailure_SaveFolderSort();
	String rpcFailure_SaveManageUsersState();
	String rpcFailure_SavePersonalPreferences();
	String rpcFailure_SaveSearch();
	String rpcFailure_SaveSharedFilesState();
	String rpcFailure_SaveSharedViewState();
	String rpcFailure_SaveSubscriptionData();
	String rpcFailure_SaveTags();
	String rpcFailure_SaveTagSortOrder();
	String rpcFailure_SaveTaskCompleted();
	String rpcFailure_SaveTaskDueDate();
	String rpcFailure_SaveTaskGraphState();
	String rpcFailure_SaveTaskLinkage();
	String rpcFailure_SaveTaskPriority();
	String rpcFailure_SaveTaskSort();
	String rpcFailure_SaveTaskStatus();
	String rpcFailure_SaveUserAccessConfig();
	String rpcFailure_SaveWhatsNewShowSetting();
	String rpcFailure_SaveZoneShareRights();
	String rpcFailure_Search();
	String rpcFailure_SetDesktopAppDownloadVisibility();
	String rpcFailure_SetEntriesPinState();
	String rpcFailure_SetSeen();
	String rpcFailure_SetStatus();
	String rpcFailure_SetUnseen();
	String rpcFailure_ShareEntry();
	String rpcFailure_ShowShares();
	String rpcFailure_SyncNetFolders();
	String rpcFailure_SyncNetFolderServer();
	String rpcFailure_SyncNetFolderRoots();
	String rpcFailure_TestGroupMembershipCriteria();
	String rpcFailure_ErrorTestingNetFolderServerConnection();
	String rpcFailure_TrackingBinder();
	String rpcFailure_TrackingPerson();
	String rpcFailure_TrashPurgeAll();
	String rpcFailure_TrashPurgeSelectedEntries();
	String rpcFailure_TrashRestoreAll();
	String rpcFailure_TrashRestoreSelectedEntries();
	String rpcFailure_UnknownCause();
	String rpcFailure_UnknownException();
	String rpcFailure_UnlockEntries();
	String rpcFailure_UnpinEntry();
	String rpcFailure_UntrackingBinder();
	String rpcFailure_UntrackingPerson();
	String rpcFailure_UpdateCalculatedDatesBinder();
	String rpcFailure_UpdateCalculatedDatesTask();
	String rpcFailure_UpdateCalendarEvent();
	String rpcFailure_UpdateCalendarEventAppointment();
	String rpcFailure_UpdateCalendarEventTask();
	String rpcFailure_UpdateFavorites();
	String rpcFailure_UploadFileBlob();
	String rpcFailure_SetUserSharingRightsInfo();
	String rpcFailure_ValidateEmailAddress();
	String rpcFailure_ValidateUploads();

	// Strings used to describe various split point load failures.
	String codeSplitFailure_AccessoriesPanel();
	String codeSplitFailure_ActivityStreamCtrl();
	String codeSplitFailure_AddFilesDlg();
	String codeSplitFailure_AddFilesHtml5Popup();
	String codeSplitFailure_AddNewFolderDlg();
	String codeSplitFailure_AdminControl();
	String codeSplitFailure_AdminInfoDlg();
	String codeSplitFailure_BinderOwnerAvatarPanel();
	String codeSplitFailure_BlogArchiveCtrl();
	String codeSplitFailure_BlogFolderView();
	String codeSplitFailure_BlogGlobalTagsCtrl();
	String codeSplitFailure_BlogPageCtrl();
	String codeSplitFailure_BreadCrumbPanel();
	String codeSplitFailure_CalendarFolderView();
	String codeSplitFailure_CalendarNavigationPanel();
	String codeSplitFailure_CalendarSettingsDlg();
	String codeSplitFailure_ChangeEntryTypesDlg();
	String codeSplitFailure_ChangePasswordDlg();
	String codeSplitFailure_ChildBindersWidget();
	String codeSplitFailure_ClipboardDlg();
	String codeSplitFailure_CollectionView();
	String codeSplitFailure_ConfigureAdhocFoldersDlg();
	String codeSplitFailure_ConfigureFileSyncAppDlg();
	String codeSplitFailure_ConfigureMobileAppsDlg();
	String codeSplitFailure_ConfigureUserAccessDlg();
	String codeSplitFailure_ConfigureUserFileSyncAppDlg();
	String codeSplitFailure_ConfigureUserMobileAppsDlg();
	String codeSplitFailure_ConfirmDlg();
	String codeSplitFailure_ContentControl();
	String codeSplitFailure_CreateBlogPageDlg();
	String codeSplitFailure_CopyMoveEntriesDlg();
	String codeSplitFailure_DescriptionPanel();
	String codeSplitFailure_DesktopAppDownloadControl();
	String codeSplitFailure_DesktopAppDownloadDlg();
	String codeSplitFailure_DiscussionFolderView();
	String codeSplitFailure_DiscussionWSView();
	String codeSplitFailure_EditBrandingDlg();
	String codeSplitFailure_EditNetFolderRightsDlg();
	String codeSplitFailure_EditShareNoteDlg();
	String codeSplitFailure_EditShareRightsDlg();
	String codeSplitFailure_EditUserZoneShareRightsDlg();
	String codeSplitFailure_EditZoneShareRightsDlg();
	String codeSplitFailure_EmailNotificationDlg();
	String codeSplitFailure_EntryMenuPanel();
	String codeSplitFailure_ExtensionsConfig();
	String codeSplitFailure_FilterPanel();
	String codeSplitFailure_FileConflictsDlg();
	String codeSplitFailure_FileFolderView();
	String codeSplitFailure_FindCtrl();
	String codeSplitFailure_FolderEntryComposite();
	String codeSplitFailure_FolderEntryDlg();
	String codeSplitFailure_FolderEntryView();
	String codeSplitFailure_FolderOptionsDlg();
	String codeSplitFailure_FolderColumnsDlg();
	String codeSplitFailure_FooterPanel();
	String codeSplitFailure_GenericWSView();
	String codeSplitFailure_GuestbookFolderView();
	String codeSplitFailure_HomeWSView();
	String codeSplitFailure_ImportIcalByFileDlg();
	String codeSplitFailure_ImportIcalByUrlDlg();
	String codeSplitFailure_ImportProfilesDlg();
	String codeSplitFailure_LandingPage();
	String codeSplitFailure_LandingPageEditor();
	String codeSplitFailure_LandingPageWidget();
	String codeSplitFailure_LoginDlg();
	String codeSplitFailure_MainMenuControl();
	String codeSplitFailure_MainPage();
	String codeSplitFailure_ManageCommentsComposite();
	String codeSplitFailure_ManageCommentsDlg();
	String codeSplitFailure_ManageGroupsDlg();
	String codeSplitFailure_ManageNetFoldersDlg();
	String codeSplitFailure_ManageNetFolderServersDlg();
	String codeSplitFailure_ManageMenuPopup();
	String codeSplitFailure_ManageSavedSearchesDlg();
	String codeSplitFailure_ManageUsersDlg();
	String codeSplitFailure_MicroBlogFolderView();
	String codeSplitFailure_MilestoneFolderView();
	String codeSplitFailure_MirroredFileFolderView();
	String codeSplitFailure_ModifyGroupDlg();
	String codeSplitFailure_ModifyNetFolderDlg();
	String codeSplitFailure_ModifyNetFolderServerDlg();
	String codeSplitFailure_MultiErrorAlertDlg();
	String codeSplitFailure_NetFolderSelectPrincipalsWidget();
	String codeSplitFailure_NetFoldersWSView();
	String codeSplitFailure_PersonalWorkspacesView();
	String codeSplitFailure_ProfileAttributeWidget();
	String codeSplitFailure_ProfileEntryDlg();
	String codeSplitFailure_ProfilePage();
	String codeSplitFailure_ProgressDlg();
	String codeSplitFailure_ProjectInfoWidget();
	String codeSplitFailure_ProjectStatsWidget();
	String codeSplitFailure_ProjectManagementWSView();
	String codeSplitFailure_QuickViewDlg();
	String codeSplitFailure_RenameEntityDlg();
	String codeSplitFailure_RunAReportDlg();
	String codeSplitFailure_SearchOptionsComposite();
	String codeSplitFailure_SelectPrincipalsWidget();
	String codeSplitFailure_SurveyFolderView();
	String codeSplitFailure_ShareExpirationDlg();
	String codeSplitFailure_ShareThisDlg();
	String codeSplitFailure_ShareWithPublicInfoDlg();
	String codeSplitFailure_ShareWithTeamsDlg();
	String codeSplitFailure_SizeColumnsDlg();
	String codeSplitFailure_TagThisDlg();
	String codeSplitFailure_TaskFolderView();
	String codeSplitFailure_TaskGraphsPanel();
	String codeSplitFailure_TaskListing();
	String codeSplitFailure_TeamWSView();
	String codeSplitFailure_TinyMCEDlg();
	String codeSplitFailure_TrashView();
	String codeSplitFailure_UserPropertiesDlg();
	String codeSplitFailure_UserShareRightsDlg();
	String codeSplitFailure_UserStatusControl();
	String codeSplitFailure_ViewsMenuPopup();
	String codeSplitFailure_WhoHasAccessDlg();
	String codeSplitFailure_WorkspaceTreeControl();
	String codeSplitFailure_ZoneShareRightsSelectPrincipalsWidget();
	
	// Strings used to describe various event handling errors.
	String eventHandling_NoActionMenuHandler(         String eventName                  );
	String eventHandling_NoContextMenuEventHandler(   String eventName                  );
	String eventHandling_NoEntryMenuHandler(          String eventName                  );
	String eventHandling_NoEventHandlerDefined(       String eventName, String className);
	String eventHandling_NonSimpleEvent(              String eventName, String className);
	String eventHandling_UnhandledEvent(              String eventName, String className);
	String eventHandling_UnknownEditInPlaceEditorType(String editorType                 );
	String eventHandling_Validation_NoHandler(        String eventName, String className);
	String eventHandling_Validation_NotListed(        String eventName, String className);
	String eventHandling_Validation_NoValidator(      String eventName                  );
	
	// Strings used with the MastHead
	String adminMenuItem();
	String administrationHint();
	String downloadFilrDesktopApp();
	String downloadVibeDesktopApp();
	String guest();
	String helpMenuItem();
	String helpHint();
	String invokeUserListHint();
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
	String novellFilr();
	String novellTeaming();
	String sampleText();
	String siteAndBinderBrandingLabel();
	String siteBrandingOnlyLabel();
	String stretchImg();
	String textColorLabel();
	String useBrandingImgLabel();
	String useAdvancedBrandingLabel();
	
	// Strings used in the login dialog.
	String loginDlg_AuthProviderAol();
	String loginDlg_AuthProviderGoogle();
	String loginDlg_AuthProviderMyOpenId();
	String loginDlg_AuthProviderVerisign();
	String loginDlg_AuthProviderYahoo();
	String loginDlg_AuthProviderUnknown();
	String loginDlg_AuthenticateUsingOpenID( String providerName );
	String loginDlg_ConfirmationText();
	String loginDlg_externalUserSelfRegFailed();
	String loginDlg_ExtUserRegistrationHint();
	String loginDlg_FirstNameLabel();
	String loginDlg_firstNameRequired();
	String loginDlg_LastNameLabel();
	String loginDlg_lastNameRequired();
	String loginDlg_OrLabel();
	String loginDlg_PwdLabel();
	String loginDlg_pwdRequired();
	String loginDlg_pwdDoNotMatch();
	String loginDlg_ReenterPwdLabel();
	String loginDlg_Register();
	String loginDlg_RegisterUsingSelfReg();
	String loginDlgAuthenticating();
	String loginDlgCreateNewAccount();
	String loginDlgKablinkHeader();
	String loginDlgLoginFailed();
	String loginDlgNovellFilrHeader();
	String loginDlgNovellHeader();
	String loginDlgOpenIDIdentity();
	String loginDlgPassword();
	String loginDlgReset();
	String loginDlgSelectAuthProviderLabel();
	String loginDlgUseOpenId();
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
	String mainMenuClipboardDlgAddPeople();
	String mainMenuClipboardDlgAddTeam();
	String mainMenuClipboardDlgClearAll();
	String mainMenuClipboardDlgDelete();
	String mainMenuClipboardDlgEmpty();
	String mainMenuClipboardDlgHeader();
	String mainMenuClipboardDlgReading();
	String mainMenuClipboardDlgSelectAll();
	String mainMenuEmailNotificationDlgAltHelpAll();
	String mainMenuEmailNotificationDlgAltHelpOverride();
	String mainMenuEmailNotificationDlgBanner();
	String mainMenuEmailNotificationDlgClearEntrySubscription();
	String mainMenuEmailNotificationDlgDigest();
	String mainMenuEmailNotificationDlgErrorSelection();
	String mainMenuEmailNotificationDlgHeader();
	String mainMenuEmailNotificationDlgIndividualMessages();
	String mainMenuEmailNotificationDlgIndividualMessagesNoAttachments();
	String mainMenuEmailNotificationDlgMakeSelection();
	String mainMenuEmailNotificationDlgNoChanges();
	String mainMenuEmailNotificationDlgOverride();
	String mainMenuEmailNotificationDlgReading();
	String mainMenuEmailNotificationDlgTextMessaging();
	String mainMenuErrorNoContributorsToEmail();
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
	String mainMenuFolderOptionsUnexpectedEvent(String eventName);
	String mainMenuImportIcalByFileDlgErrorBogusJSONData(String jsonData);
	String mainMenuImportIcalByFileDlgErrorFailed(String detail);
	String mainMenuImportIcalByFileDlgErrorNoFile();
	String mainMenuImportIcalByFileDlgErrorParse(String detail);
	String mainMenuImportIcalByFileDlgHeader(String importType);
	String mainMenuImportIcalByFileDlgHint();
	String mainMenuImportIcalByFileDlgSuccess(String added, String modified);
	String mainMenuImportIcalByUrlDlgErrorFailed(String detail);
	String mainMenuImportIcalByUrlDlgErrorNoUrl();
	String mainMenuImportIcalByUrlDlgErrorParse(String detail);
	String mainMenuImportIcalByUrlDlgErrorUnknown();
	String mainMenuImportIcalByUrlDlgErrorUrl(String detail);
	String mainMenuImportIcalByUrlDlgHeader(String importType);
	String mainMenuImportIcalByUrlDlgHintCalendar();
	String mainMenuImportIcalByUrlDlgHintTask();
	String mainMenuImportIcalByUrlDlgSuccess(String added, String modified);
	String mainMenuImportIcalTypeError();
	String mainMenuImportIcalTypeCalendar();
	String mainMenuImportIcalTypeTask();
	String mainMenuManageEditTeam();
	String mainMenuManageEmailTeam();
	String mainMenuManageFolderOptions();
	String mainMenuManageFolderColumns();
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
	String mainMenuRenameBinderDlgErrorBogusBinder(String binderType);
	String mainMenuSearchEmpty();
	String mainMenuSearchImageAlt();
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
	String mainMenuWhoHasAccessDlgGroupsWithAccess();
	String mainMenuWhoHasAccessDlgHeader();
	String mainMenuWhoHasAccessDlgNone();
	String mainMenuWhoHasAccessDlgReading();
	String mainMenuWhoHasAccessDlgUsersWithAccess();
	
	// Strings used by the 'Add New Folder' dialog.
	String addNewFolderDlgError_AddFailed();
	String addNewFolderDlgError_NoName();
	String addNewFolderDlgHeader();
	String addNewFolderDlgName();

	// Strings used by the 'Calendar Settings' dialog.
	String calendarSettingsDlg_Day_Sunday();
	String calendarSettingsDlg_Day_Monday();
	String calendarSettingsDlg_Day_Tuesday();
	String calendarSettingsDlg_Day_Wednesday();
	String calendarSettingsDlg_Day_Thursday();
	String calendarSettingsDlg_Day_Friday();
	String calendarSettingsDlg_Day_Saturday();
	String calendarSettingsDlg_Header();
	String calendarSettingsDlg_Label_WeekStartsOn();
	String calendarSettingsDlg_Label_WorkDayStartsAt();

	// Strings used in the workspace tree control.
	String treeAltConfigureFolder();
	String treeAltConfigureWorkspace();
	String treeAltEntry();
	String treeAltFolder();
	String treeAltWorkspace();
	String treeBucketHover( String firstPart, String lastPart );
	String treeCloseActivityStreams();
	String treeCloseActivityStreamsHint();
	String treeCloseBreadCrumbs();
	String treeErrorNoManageMenu();
	String treeIntentionallyLeftBlank();
	String treeInternalErrorNoCollection();
	String treeInternalErrorRefreshNotSidebar();
	String treeInternalErrorRerootNotSidebar();
	String treePreviousCollection();
	String treePreviousFolder();
	String treeSiteWide();
	String treeTrash();
	String treeWSAndFolders();
	
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
	String qViewErrorNoUserForQuickView();
	String qViewErrorCantTrack();
	String qViewErrorCantUntrack();

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
	String profileInsufficientViewProfileRights();
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
	String adminInfoDlgEnterProxyCredentials( String serverName );
	String adminInfoDlgFilrTasksToBeCompleted();
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
	String confirmDeleteEntry();
	String followedPeople();
	String followedPlaces();
	String hideComments();
	String hideDesc();
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
	String showAllComments();
	String showEntireDescHint();
	String showPartialDescHint();
	String showUnread();
	String showUnreadEntries();
	String whatsNew();
	String whatsNewWithName( String name );
	
	// The following strings are used in the activity stream "reply to entry" ui
	String addAComment();
	String defaultReplyTitle( String title );
	String noReplyText();
	String replyToEntryLabel();
	
	// The following strings are used in the activity stream "share this entry" ui.
	String addRecipient();
	String commentsLabel();
	String defaultShareTitle( String title );
	String noShareRecipients();
	String noShareRecipientsOrTeams();
	String removeShareHint();
	String shareAccess();
	String shareCaption();
	String shareDlg_addExternalUserTitle();
	String shareDlg_alreadySharedWithSelectedRecipient( String recipientName );
	String shareDlg_cantShareWithAllExternalUsersGroup();
	String shareDlg_cantShareWithAllInternalUsersGroup();
	String shareDlg_cantShareWithExternalUser();
	String shareDlg_cantShareWithPublic();
	String shareDlg_cantShareWithYourself();
	String shareDlg_clickToAddNote();
	String shareDlg_contributor();
	String shareDlg_editor();
	String shareDlg_emailAddressInvalidPrompt();
	String shareDlg_expiresAfter( String after );
	String shareDlg_expiresLabel();
	String shareDlg_expiresNever();
	String shareDlg_expiresOn( String on );
	String shareDlg_makePublic();
	String shareDlg_noNote();
	String shareDlg_noTeamsToShareWith();
	String shareDlg_noteLabel();
	String shareDlg_notifyLabel();
	String shareDlg_rightsLabel();
	String shareDlg_savingShareInfo();
	String shareDlg_shareLabel();
	String shareDlg_sharingLabel();
	String shareDlg_sharePublicTitle();
	String shareDlg_viewer();
	String shareEntityName();
	String shareErrors();
	String shareExpires();
	String shareHint();
	String shareName();
	String shareNote();
	String shareRecipientType();
	String shareRecipientTypeExternalUser();
	String shareRecipientTypeGroup();
	String shareRecipientTypePublic();
	String shareRecipientTypeTeam();
	String shareRecipientTypeUser();
	String shareTheseItems();
	String shareWithGroups();
	String shareWithTeams();
	String shareWithUsers();
	String sharingMultipleItems( int numItems );
	String unknownShareType();
	String usersWithoutRights();
	
	// The following strings are using in the "share expiration" dialog
	String shareExpirationDlg_caption();
	String shareExpirationDlg_days();
	String shareExpirationDlg_expiresAfter();
	String shareExpirationDlg_expiresLabel();
	String shareExpirationDlg_expiresNever();
	String shareExpirationDlg_expiresOn();
	String shareExpirationDlg_noDateEntered();
	String shareExpirationDlg_noDaysEntered();
	
	// The following strings are used in the "share with teams" dialog
	String shareWithTeamsDlg_caption();
	String shareWithTeamsDlg_Instructions();
	
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
	String deleteEntry();
	String markRead();
	String markUnread();
	String reply();
	String sendToFriend();
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
	String taskAltHierarchyDisabled();
	String taskAltLocationGotoThisFolder();
	String taskAltLocationIsThisFolder();
	String taskAltMoveDown();
	String taskAltMoveLeft();
	String taskAltMoveRight();
	String taskAltMoveUp();
	String taskAltPurge();
	String taskAltTaskActions();
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
	String taskGraphsAltHide();
	String taskGraphsAltShow();
	String taskGraphs();
	String taskGraphsPriority();
	String taskGraphsRefresh();
	String taskGraphsStatus();
	String taskGraphsStatusCanceled(String percent, String count);
	String taskGraphsStatusCompleted(String percent, String count);
	String taskGraphsStatusInProcess(String percent, String count);
	String taskGraphsStatusNeedsAction(String percent, String count);
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
	String borderColorLabel();
	String borderWidthLabel();
	String contentTextColorLabel();
	String headerBackgroundColorLabel();
	String headerTextColorLabel();
	String inheritPropertiesLabel();
	String invalidBorderColor( String color );
	String invalidContentTextColor( String color );
	String invalidHeaderBgColor( String color );
	String invalidHeaderTextColor( String color );

	// Strings used in the Novell Desktop App dialog (File Sync)
	String fileSyncAppAllowAccess( String productName );
	String fileSyncAppAllowCachePwd();
	String fileSyncAppAutoUpdateUrlLabel();
	String fileSyncAppAutoUpdateUrlRequiredPrompt();
	String fileSyncAppDlgHeader();
	String fileSyncAppEnableDeployLabel();
	String fileSyncAppHeader2();
	String fileSyncAppIntervalLabel();
	String fileSyncApp_InvalidAutoUpdateUrlText();
	String fileSyncAppMaxFileSizeLabel();
	String fileSyncAppMBLabel();
	String fileSyncAppMinutesLabel();
	String fileSyncApp_OnSaveUnknownException( String desc );
	String fileSyncAppUseGlobalSettings();
	String fileSyncAppUseUserSettings();
	
	// Strings used in the Configure User File Sync App dialog
	String configureUserFileSyncAppDlgErrorHeader();
	String configureUserFileSyncAppDlgHeader( String numUsers );
	String configureUserFileSyncAppDlgOnSaveUnknownException( String desc );
	String configureUserFileSyncDlgSaving( String completed, String total );

	// Strings used by the filter bar in the various binder views.
	String vibeBinderFilter_Alt_Filters();
	String vibeBinderFilter_Filter();
	String vibeBinderFilter_Filters();
	String vibeBinderFilter_None();
	
	// Strings used by the footer in the various binder views.
	String vibeBinderFooter_Filr_AtomUrl();
	String vibeBinderFooter_Filr_EmailAddresses();
	String vibeBinderFooter_Filr_EmailAddressesHint();
	String vibeBinderFooter_Filr_FileDownload();
	String vibeBinderFooter_Filr_FileDownloadHint();
	String vibeBinderFooter_Filr_iCalUrl();
	String vibeBinderFooter_Filr_iCalUrlHint();
	String vibeBinderFooter_Filr_KeyHeader();
	String vibeBinderFooter_Filr_KeyFooter();
	String vibeBinderFooter_Filr_Permalink();
	String vibeBinderFooter_Filr_PermalinkHint();
	String vibeBinderFooter_Filr_RSSUrl();
	String vibeBinderFooter_Filr_RSSUrlHint();
	String vibeBinderFooter_Filr_WebDAVUrl();
	String vibeBinderFooter_Filr_WebDAVUrlHintEntry();
	String vibeBinderFooter_Filr_WebDAVUrlHintFolder();
	String vibeBinderFooter_Vibe_AtomUrl();
	String vibeBinderFooter_Vibe_EmailAddresses();
	String vibeBinderFooter_Vibe_EmailAddressesHint();
	String vibeBinderFooter_Vibe_FileDownload();
	String vibeBinderFooter_Vibe_FileDownloadHint();
	String vibeBinderFooter_Vibe_iCalUrl();
	String vibeBinderFooter_Vibe_iCalUrlHint();
	String vibeBinderFooter_Vibe_KeyHeader();
	String vibeBinderFooter_Vibe_KeyFooter();
	String vibeBinderFooter_Vibe_Permalink();
	String vibeBinderFooter_Vibe_PermalinkHint();
	String vibeBinderFooter_Vibe_RSSUrl();
	String vibeBinderFooter_Vibe_RSSUrlHint();
	String vibeBinderFooter_Vibe_WebDAVUrl();
	String vibeBinderFooter_Vibe_WebDAVUrlHintEntry();
	String vibeBinderFooter_Vibe_WebDAVUrlHintFolder();
	
	// Strings used by the entry menu bar in the various binder views.
	String vibeEntryMenu_Alt_ClearFilter();
	String vibeEntryMenu_Alt_GlobalFilter();
	String vibeEntryMenu_Alt_FilterOptions();
	String vibeEntryMenu_Alt_ListOptions();
	String vibeEntryMenu_Alt_PersonalFilter();
	String vibeEntryMenu_Alt_Pin_ShowAll();
	String vibeEntryMenu_Alt_Pin_ShowPinned();
	String vibeEntryMenu_Alt_Shared_ShowAll();
	String vibeEntryMenu_Alt_Shared_ShowFiles();
	String vibeEntryMenu_ClearFilters();
	String vibeEntryMenu_GlobalizeFilter(String filterName);
	String vibeEntryMenu_ManageFilters();
	String vibeEntryMenu_ManageUsers_EnabledFilter();
	String vibeEntryMenu_ManageUsers_ExternalFilter();
	String vibeEntryMenu_ManageUsers_DisabledFilter();
	String vibeEntryMenu_ManageUsers_InternalFilter();
	String vibeEntryMenu_ManageUsers_Warning_NoUsers1();
	String vibeEntryMenu_ManageUsers_Warning_NoUsers2(); 
	String vibeEntryMenu_PersonalizeFilter(String filterName);
	String vibeEntryMenu_SharedView_HiddenFilter();
	String vibeEntryMenu_SharedView_NonHiddenFilter();
	String vibeEntryMenu_SharedView_Warning_NoShares();
	String vibeEntryMenu_Warning_FoldersIgnored();
	String vibeEntryMenu_Warning_OnlyFolders();
		
	// Strings used in the Folder Columns dialog
	String folderColumnsDlgColumn();
	String folderColumnsDlgCustomLabel();
	String folderColumnsDlgHeader();
	String folderColumnsDlgMoveDown();
	String folderColumnsDlgMoveUp();
	String folderColumnsDlgNoOptions();
	String folderColumnsDlgOrder();
	String folderColumnsDlgRestoreDefaults();
	String folderColumnsDlgSetAsDefault();
	String folderColumnsDlgShow();
	
	// Strings used by various widgets of the Vibe Data table.
	String vibeDataTable_Alt_CollapseDescription();
	String vibeDataTable_Alt_Comments();
	String vibeDataTable_Alt_EntryActions();
	String vibeDataTable_Alt_ExpandDescription();
	String vibeDataTable_Alt_ExternalUser_Guest();
	String vibeDataTable_Alt_ExternalUser_Others();
	String vibeDataTable_Alt_InternalUser_LDAP();
	String vibeDataTable_Alt_InternalUser_PersonAdmin();
	String vibeDataTable_Alt_InternalUser_PersonOthers();
	String vibeDataTable_Alt_InternalUser_System();
	String vibeDataTable_Alt_UnknownUser();
	String vibeDataTable_Alt_Unread();
	String vibeDataTable_Alt_PinEntry();
	String vibeDataTable_Alt_PinHeader();
	String vibeDataTable_Alt_PinHeader_PinAll();
	String vibeDataTable_Alt_PinHeader_UnpinAll();
	String vibeDataTable_Alt_StarGold();
	String vibeDataTable_Alt_StarGray();
	String vibeDataTable_Alt_UnpinEntry();
	String vibeDataTable_Alt_View();
	String vibeDataTable_ColumnResizer();
	String vibeDataTable_Confirm_CantShareSomeSelections();
	String vibeDataTable_Download();
	String vibeDataTable_Error_MirroredDriverNotConfigured();
	String vibeDataTable_Empty();
	String vibeDataTable_Empty_Pinning();
	String vibeDataTable_Event_AllDay();
	String vibeDataTable_Event_End();
	String vibeDataTable_Event_Duration(String days);
	String vibeDataTable_Event_Start();
	String vibeDataTable_GuestbookInternalErrorOverrideMissing();
	String vibeDataTable_InternalError_UnexpectedRowCount(int rowsRequested, int rowsRead);
	String vibeDataTable_InternalError_UnsupportedStructuredToolbar();
	String vibeDataTable_MemberCount(String count);
	String vibeDataTable_People();
	String vibeDataTable_Pin();
	String vibeDataTable_Select();
	String vibeDataTable_TrashConfirmPurge();
	String vibeDataTable_TrashConfirmPurgeAll();
	String vibeDataTable_TrashConfirmPurgeAllWithSelections();
	String vibeDataTable_TrashConfirmPurgeDeleteSourceOnMirroredSubFolders();
	String vibeDataTable_TrashConfirmRestoreAllWithSelections();
	String vibeDataTable_TrashInternalErrorOverrideMissing(String methodName);
	String vibeDataTable_View();
	String vibeDataTable_ViewTrash();
	String vibeDataTable_Warning_NoEntryActions();
	String vibeDataTable_Warning_ShareNoRights();
	String vibeDataTable_WhatsNew();
	
	// String used in the Landing Page
	String nowFollowingBinder();
	String utilityElementAdminPage();
	String utilityElementFollowWorkspace();
	String utilityElementLogIn();
	String utilityElementMyWorkspace();
	String utilityElementShareWorkspace();

	// Strings used in the TaskFolderWidget
	String taskFolderWidget_assignedTo();
	String taskFolderWidget_dueDate();
	String taskFolderWidget_percentDone();
	String taskFolderWidget_priority();
	String taskFolderWidget_status();

	// Strings used in the SizeColumnsDlg.
	String sizeColumnsDlgColColumn();
	String sizeColumnsDlgColDefault();
	String sizeColumnsDlgColResize();
	String sizeColumnsDlgColSize();
	String sizeColumnsDlgColUnit();
	String sizeColumnsDlgDefaultRB(String def);
	String sizeColumnsDlgFixedRB();
	String sizeColumnsDlgFlowRB();
	String sizeColumnsDlgHeader();
	String sizeColumnsDlgSliderHint();
	String sizeColumnsDlgUnitPercentRB();
	String sizeColumnsDlgUnitPixelRB();
	String sizeColumnsDlgWarnPercents();
	
	// Strings used in the AddFilesDlg.
	String addFilesDlgFrameTitle();
	String addFilesDlgHavingTrouble();
	String addFilesDlgHeader();
	
	// Strings used in the AddFilesHtml5Popup.
	String addFilesHtml5PopupAbort();
	String addFilesHtml5PopupAbortAlt();
	String addFilesHtml5PopupBrowse();
	String addFilesHtml5PopupBrowseAlt();
	String addFilesHtml5PopupBusy(String fileName, int thisOne, int total);
	String addFilesHtml5PopupFoldersSkipped(String folderNames);
	String addFilesHtml5PopupHint();
	String addFilesHtml5PopupProgressItem();
	String addFilesHtml5PopupProgressTotal();
	String addFilesHtml5PopupReadError(String fileName, String errorDesc);
	String addFilesHtml5PopupUploadValidationError();
	
	// Strings used in the "Child binders widget"
	String workspacesHeader();
	String unreadEntries( Long numUnread );

	// Strings used in the "Unread entries" dialog
	String unreadEntriesDlgHeader();
	
	// Strings used in the CopyMoveEntriesDlg in copy mode.
	String copyEntriesDlgCaption1_Entries();
	String copyEntriesDlgCaption1_Entry();
	String copyEntriesDlgCaption1_Folder();
	String copyEntriesDlgCaption1_Workspace();
	String copyEntriesDlgCaption2();
	String copyEntriesDlgCurrentDestination();
	String copyEntriesDlgCurrentDestinationNone();
	String copyEntriesDlgErrorInvalidSearchResult();
	String copyEntriesDlgErrorCopyFailures();
	String copyEntriesDlgErrorTargetInSourceAll();
	String copyEntriesDlgErrorTargetInSourceSome();
	String copyEntriesDlgHeader();
	String copyEntriesDlgProgress(int done, int total);
	String copyEntriesDlgSelectDestination();
	String copyEntriesDlgWarningNoSelection();

	// Strings used in the CopyMoveEntriesDlg in move mode.
	String moveEntriesDlgCaption1_Entries();
	String moveEntriesDlgCaption1_Entry();
	String moveEntriesDlgCaption1_Folder();
	String moveEntriesDlgCaption1_Workspace();
	String moveEntriesDlgCaption2();
	String moveEntriesDlgCurrentDestination();
	String moveEntriesDlgCurrentDestinationNone();
	String moveEntriesDlgErrorInvalidSearchResult();
	String moveEntriesDlgErrorMoveFailures();
	String moveEntriesDlgErrorTargetInSourceAll();
	String moveEntriesDlgErrorTargetInSourceSome();
	String moveEntriesDlgHeader();
	String moveEntriesDlgProgress(int done, int total);
	String moveEntriesDlgSelectDestination();
	String moveEntriesDlgWarningNoSelection();

	// Strings used by the operations against selected entries.
	String deleteFolderEntriesError();
	String deleteFolderEntryError();
	String deleteTasksError();
	String purgeFolderEntriesError();
	String purgeFolderEntryError();
	String purgeTasksError();
	
	// Strings used by the operations against selected users.
	String enableUsersError();
	String deleteUserWorkspacesError();
	String disableUsersError();
	String purgeUserWorkspacesError();
	String purgeUsersError();
	
	// Strings used by the lock selected entries facility.
	String lockEntriesError();

	// Strings used by the unlock selected entries facility.
	String unlockEntriesError();
	
	// Strings used by ChangeEntryTypesDlg.
	String changeEntryTypesDlgCurrent();
	String changeEntryTypesDlgErrorChangeFailures();
	String changeEntryTypesDlgErrorNoSelection();
	String changeEntryTypesDlgHeader();
	String changeEntryTypesDlgHeaderFor(String title);
	String changeEntryTypesDlgLocal();
	String changeEntryTypesDlgNew();
	String changeEntryTypesDlgNote();
	String changeEntryTypesDlgSelect();
	
	// Strings used by the ProfileEntryDlg.
	String profileEntryDlgDelete();
	String profileEntryDlgHeader();
	String profileEntryDlgLabelize(String label);
	String profileEntryDlgModify();
	String profileEntryDlgNote();
	
	// Strings used by the ConfirmDlg.
	String confirmDlgHeader();
	
	// Strings used by the MultiErrorAlertDlg.
	String multiErrorAlertDlgHeader();
	
	// Strings used in the Project Information widget
	String dueDateLabel();
	String managerLabel();
	String projectInformation();
	String projectStatusCancelled();
	String projectStatusClosed();
	String projectStatusOpen();
	String projectStatusUnknown();
	String statusLabel();

	// Strings used in the Project Statistics widget
	String projectStatistics();
	
	// String used by the task graphs widgets.
	String taskGraphs_PriorityCritical( String percent, String count);
	String taskGraphs_PriorityHigh(     String percent, String count);
	String taskGraphs_PriorityLeast(    String percent, String count);
	String taskGraphs_PriorityLow(      String percent, String count);
	String taskGraphs_PriorityMedium(   String percent, String count);
	String taskGraphs_PriorityNone(     String percent, String count);
	String taskGraphs_StatusCanceled(   String percent, String count);
	String taskGraphs_StatusCompleted(  String percent, String count);
	String taskGraphs_StatusInProcess(  String percent, String count);
	String taskGraphs_StatusNeedsAction(String percent, String count);
	
	// Strings used by the milestone graphs widgets.
	String milestoneGraphs_StatusCompleted( String percent, String count );
	String milestoneGraphs_StatusOpen( String percent, String count );
	String milestoneGraphs_StatusReopened( String percent, String count );
	
	// Strings used by the guest book folder.
	String guestBook_Error_CouldNotGetSigningURL();
	String guestBook_GotoProfile();
	
	// Strings used in the "My Tasks" widget
	String myTasksHeader();	
	
	// Strings used in the 'Binder View Helper' APIs.
	String binderViewsConfirmDeleteEntries();
	String binderViewsConfirmDeleteEntry();
	String binderViewsConfirmDeleteFolder();
	String binderViewsConfirmDeleteWorkspace();
	String binderViewsConfirmDeleteUserWS();
	String binderViewsConfirmPurgeDeleteSourceOnMirroredSubFolders();
	String binderViewsConfirmPurgeEntries();
	String binderViewsConfirmPurgeEntry();
	String binderViewsConfirmPurgeFolder();
	String binderViewsConfirmPurgeWorkspace();
	String binderViewsConfirmPurgeUsers();
	String binderViewsConfirmPurgeUserWS();
	String binderViewsDeleteFolderEntriesCaption();
	String binderViewsDeleteFolderEntriesProgress();
	String binderViewsDeleteTasksCaption();
	String binderViewsDeleteTasksProgress();
	String binderViewsDeleteUserWorkspacesCaption();
	String binderViewsDeleteUserWorkspacesProgress();
	String binderViewsPromptPurgeMirroredFolders();
	String binderViewsPurgeFolderEntriesCaption();
	String binderViewsPurgeFolderEntriesProgress();
	String binderViewsPurgeTasksCaption();
	String binderViewsPurgeTasksProgress();
	String binderViewsPurgeUsersCaption();
	String binderViewsPurgeUsersProgress();
	String binderViewsPurgeUserWorkspacesCaption();
	String binderViewsPurgeUserWorkspacesProgress();
	
	// Strings used in the calendar view.
	String calendarNav_Alt_GoTo();
	String calendarNav_Alt_GoToToday();
	String calendarNav_Alt_NextTimePeriod();
	String calendarNav_Alt_PreviousTimePeriod();
	String calendarNav_Alt_Settings();
	String calendarNav_Alt_View1();
	String calendarNav_Alt_View3();
	String calendarNav_Alt_ViewMonth();
	String calendarNav_Alt_ViewWeek();
	String calendarNav_Alt_View2Weeks();
	String calendarNav_Alt_ViewWorkWeek();
	String calendarNav_Hours_FullDay();
	String calendarNav_Hours_WorkDay();
	String calendarView_Confirm_DeleteEntry();
	String calendarView_Confirm_PurgeEntry();
	String calendarView_Error_CantAdd();
	String calendarView_Error_CantClickCreateWhenViewByDate();
	String calendarView_Error_CantModify();
	String calendarView_Error_CantPurge();
	String calendarView_Error_CantTrash();
	String calendarView_Error_CantUpdateRecurrence();
	String calendarView_Error_CantUpdateWhenViewByDate();
	String calendarView_Recurrence(int index, int total);

	// Strings used by the 'Progress' dialog.
	String progressDlgConfirmCancel();

	// Strings used by the 'Quick Filter' composite.
	String quickFilterAltOff();
	String quickFilterAltOn();
	String quickFilter_empty();

	// Strings used in the "Edit graphic properties" dialog
	String editGraphicPropertiesDlgSetImageSize();

	// Strings used in the Manage Comments composite.
	String manageCommentsCompositeSend();
	String manageCommentsCompositeWhoHasAccess();
	
	// Strings used in the Manage Comments dialog.
	String manageCommentsDlgComments(int cCount);
	
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
	String modifyStaticMembershipDlgExternalAllowedLabel();
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

	// Strings used in the Show dynamic membership dialog
	String showDynamicMembershipDlgGroupTab();
	String showDynamicMembershipDlgHeader();
	String showDynamicMembershipDlgUserTab();
	
	// String used in the blog archive control
	String blogArchiveTitle();
	
	// Strings used in the blog global tags control.
	String blogGlobalTagsTitle();
	
	// Strings used in the blog page control.
	String blogPageCtrl_newPageLabel();
	String blogPageCtrl_selectPageLabel();

	// Strings used in the "Add blog page" dialog
	String createBlogPageDlg_caption();
	String createBlogPageDlg_createFailed();
	String createBlogPageDlg_newPageNameLabel();
	String createBlogPageDlg_noNameSpecified();

	// Strings used with Filr
	String folders();
	String myFiles();
	String netFolders();
	String peopleList();
	String sharedByMe();
	String sharedWithMe();

	// Strings used with the "Share send-to" widget
	String shareSendToWidget_AllRecipients();
	String shareSendToWidget_OnlyModifiedRecipients();
	String shareSendToWidget_OnlyNewRecipients();
	String shareSendToWidget_SelectedRecipients();
	String shareSendToWidget_Unknown();
	
	// Strings used in the manage Net Folder Servers dialog
	String manageNetFolderServersDlg_AddNetFolderServerLabel();
	String manageNetFolderServersDlg_ConfirmDelete( String netFolderServerNames );
	String manageNetFolderServersDlg_CouldNotDeletePrompt( String netFolderServerNames );
	String manageNetFolderServersDlg_DeleteNetFolderServerLabel();
	String manageNetFolderServersDlg_Header();
	String manageNetFolderServersDlg_NameCol();
	String manageNetFolderServersDlg_NoNetFolderServersLabel();
	String manageNetFolderServersDlg_ServerPathCol();
	String manageNetFolderServersDlg_SelectServersToDelete();
	String manageNetFolderServersDlg_SelectServersToSync();
	String manageNetFolderServersDlg_SyncLabel();
	String manageNetFolderServersDlg_Syncing();
	String manageNetFolderServersDlg_SyncFailure();
	String manageNetFolderServersDlg_UnknownStatus();

	// Strings used in the modify Net Folder Server dialog
	String modifyNetFolderServerDlg_AddHeader();
	String modifyNetFolderServerDlg_AllowSelfSignedCertsLabel();
	String modifyNetFolderServerDlg_CreatingNetFolderServer();
	String modifyNetFolderServerDlg_EditHeader( String name );
	String modifyNetFolderServerDlg_EnableSyncScheduleLabel();
	String modifyNetFolderServerDlg_ErrorCreatingNetFolderServer( String err );
	String modifyNetFolderServerDlg_ErrorModifyingNetFolderServer( String err );
	String modifyNetFolderServerDlg_Group();
	String modifyNetFolderServerDlg_HostUrlLabel();
	String modifyNetFolderServerDlg_HostUrlRequired();
	String modifyNetFolderServerDlg_InsufficientRights();
	String modifyNetFolderServerDlg_IsSharePointServerLabel();
	String modifyNetFolderServerDlg_ModifyingNetFolderServer();
	String modifyNetFolderServerDlg_NameLabel();
	String modifyNetFolderServerDlg_NameRequired();
	String modifyNetFolderServerDlg_NotFullyConfigured();
	String modifyNetFolderServerDlg_PrivilegedPrincipalsHint();
	String modifyNetFolderServerDlg_ProxyNameLabel();
	String modifyNetFolderServerDlg_ProxyPwdLabel();
	String modifyNetFolderServerDlg_ServerAlreadyExists();
	String modifyNetFolderServerDlg_ServerPathLabel();
	String modifyNetFolderServerDlg_SyncAllNetFoldersPrompt();
	String modifyNetFolderServerDlg_SyncOfNetFolderServerStarted();
	String modifyNetFolderServerDlg_TestConnectionLabel();
	String modifyNetFolderServerDlg_TypeLabel();
	String modifyNetFolderServerDlg_Type_Famt();
	String modifyNetFolderServerDlg_Type_FileSystem();
	String modifyNetFolderServerDlg_Type_WebDav();
	String modifyNetFolderServerDlg_SyncScheduleCaption();
	String modifyNetFolderServerDlg_User();
	
	// Strings used in the Manage Net Folders dialog
	String manageNetFoldersDlg_AddNetFolderLabel();
	String manageNetFoldersDlg_ConfirmDelete( String netFolderName );
	String manageNetFoldersDlg_DeleteNetFolderLabel();
	String manageNetFoldersDlg_Header();
	String manageNetFoldersDlg_NameCol();
	String manageNetFoldersDlg_NoNetFoldersLabel();
	String manageNetFoldersDlg_PromptForSync();
	String manageNetFoldersDlg_RelativePathCol();
	String manageNetFoldersDlg_ServerCol();
	String manageNetFoldersDlg_SelectFoldersToDelete();
	String manageNetFoldersDlg_SelectFoldersToSync();
	String manageNetFoldersDlg_SyncLabel();
	String manageNetFoldersDlg_Syncing();
	String manageNetFoldersDlg_UnknownStatus();

	// Strings used in the Modify Net Folder dialog
	String modifyNetFolderDlg_AddHeader();
	String modifyNetFolderDlg_AllowDataSyncBy();
	String modifyNetFolderDlg_AllowDesktopAppToSyncLabel();
	String modifyNetFolderDlg_AllowMobileAppsToSyncLabel();
	String modifyNetFolderDlg_ConfigTab();
	String modifyNetFolderDlg_CreateNetFolderServerLabel();
	String modifyNetFolderDlg_CreatingNetFolder();
	String modifyNetFolderDlg_DataSyncTab();
	String modifyNetFolderDlg_EditHeader( String name );
	String modifyNetFolderDlg_EnableSyncScheduleLabel();
	String modifyNetFolderDlg_ErrorCreatingNetFolder( String err );
	String modifyNetFolderDlg_ErrorModifyingNetFolder( String err );
	String modifyNetFolderDlg_IndexContentLabel();
	String modifyNetFolderDlg_InsufficientRights();
	String modifyNetFolderDlg_ModifyingNetFolder();
	String modifyNetFolderDlg_NameLabel();
	String modifyNetFolderDlg_NameRequired();
	String modifyNetFolderDlg_NetFolderAlreadyExists();
	String modifyNetFolderDlg_NetFolderServerLabel();
	String modifyNetFolderDlg_NoNetFolderServersLabel();
	String modifyNetFolderDlg_NoNetFolderServersPrompt();
	String modifyNetFolderDlg_ParentBinderRequired();
	String modifyNetFolderDlg_ParentFolderLabel();
	String modifyNetFolderDlg_RelativePathLabel();
	String modifyNetFolderDlg_RightsTab();
	String modifyNetFolderDlg_SelectPrincipalsHint();
	String modifyNetFolderDlg_ScheduleTab();
	String modifyNetFolderDlg_SyncScheduleCaption();
	
	// Strings used in the Schedule widget
	String scheduleWidget_AtTimeLabel();
	String scheduleWidget_EveryDayLabel();
	String scheduleWidget_FridayLabel();
	String scheduleWidget_HoursLabel();
	String scheduleWidget_MondayLabel();
	String scheduleWidget_OnSelectedDaysLabel();
	String scheduleWidget_RepeatEveryLabel();
	String scheduleWidget_SaturdayLabel();
	String scheduleWidget_SundayLabel();
	String scheduleWidget_ThursdayLabel();
	String scheduleWidget_TuesdayLabel();
	String scheduleWidget_WednesdayLabel();

	// Strings used in the Edit Share Note dialog
	String editShareNoteDlg_caption();
	String editShareNoteDlg_noteLabel();

	// Strings used in the Edit Share Rights dialog
	String editShareRightsDlg_CanShareExternalLabel();
	String editShareRightsDlg_CanShareInternalLabel();
	String editShareRightsDlg_CanShareLabel();
	String editShareRightsDlg_CanSharePublicLabel();
	String editShareRightsDlg_caption();
	String editShareRightsDlg_ContributorLabel();
	String editShareRightsDlg_EditorLabel();
	String editShareRightsDlg_GrantRightsLabel();
	String editShareRightsDlg_ViewerLabel();

	// Strings used in the User Actions Popup
	String userActionsPanel_PersonalPreferences();
	String userActionsPanel_ViewProfile();
	String userActionsPanel_ViewSharedByMe();

	// Strings used when testing a net folder and net folder server connection
	String testConnection_InProgressLabel();
	String testConnection_NetworkError();
	String testConnection_Normal();
	String testConnection_ProxyCredentialsError();
	String testConnection_UnknownStatus();

	// Strings used in the Configure User Access dialog
	String configureUserAccessDlg_Header();
	String configureUserAccessDlg_AllowGuestAccessLabel();
	String configureUserAccessDlg_AllowGuestReadOnlyLabel();
	String configureUserAccessDlg_AllowSelfRegInternalUserAccountLabel();
	String configureUserAccessDlg_AllowExternalUserAccessLabel();
	String configureUserAccessDlg_AllowSelfRegExternalUserAccountLabel();
	
	// Strings used by the GWT based Folder Entry viewer.
	String folderEntry_Alt_Close();
	String folderEntry_Alt_Hide();
	String folderEntry_Alt_LockedBy(String user);
	String folderEntry_Alt_Next();
	String folderEntry_Alt_Previous();
	String folderEntry_Error_NoNext();
	String folderEntry_Error_NoPrevious();
	String folderEntry_Close();
	String folderEntry_Comments(int count);
	String folderEntry_Hide();
	String folderEntry_Locked();
	String folderEntry_Modified();
	String folderEntry_ShowDescription();
	
	// String used in the Configure Adhoc Folders dialog
	String configureAdhocFoldersDlg_AllowAdhocFoldersLabel();
	String configureAdhocFoldersDlg_Header();
	
	// String used in the desktop application download control.
	String desktopAppCtrl_Alt_HideForSession();
	String desktopAppCtrl_DontShowAgain();
	String desktopAppCtrl_Hint_Filr();
	String desktopAppCtrl_Hint_Vibe();
	
	// String used in the desktop application download dialog.
	String downloadAppDlgAlt_AndroidDownloads();
	String downloadAppDlgAlt_IOSDownloads();
	String downloadAppDlgAlt_MacDownloads();
	String downloadAppDlgAlt_WindowsDownloads();
	String downloadAppDlgBody_Downloads();
	String downloadAppDlgBody_Instructions();
	String downloadAppDlgBody_Product();
	String downloadAppDlgBody_Type();
	String downloadAppDlgDownloadAndroid(String company, String product);
	String downloadAppDlgDownloadFilenameUnknown();
	String downloadAppDlgDownloadIOS(String company, String product);
	String downloadAppDlgDownloadMac1(String product, String filename, String quickStart);
	String downloadAppDlgDownloadMac2(String company, String product);
	String downloadAppDlgDownloadWindows1(String product, String filename32, String filename64, String quickStart);
	String downloadAppDlgDownloadWindows2(String company, String product);
	String downloadAppDlgError_NoMacUrl();
	String downloadAppDlgError_NoUrls();
	String downloadAppDlgError_NoWin32Url();
	String downloadAppDlgError_NoWin64Url();
	String downloadAppDlgHeader(String product);
	String downloadAppDlgInstructAndroid1(String type);
	String downloadAppDlgInstructAndroid2();
	String downloadAppDlgInstructAndroid3(String company, String product, String type);
	String downloadAppDlgInstructAndroid4(String company, String product);
	String downloadAppDlgInstructIOS1(String company, String product, String iPhone, String iPad, String type);
	String downloadAppDlgInstructIOS2();
	String downloadAppDlgInstructIOS3();
	String downloadAppDlgInstructIOS4(String company, String product);
	String downloadAppDlgProductAndroid(String product);
	String downloadAppDlgProductIOS(String product);
	String downloadAppDlgProductMac(String product);
	String downloadAppDlgProductWindows(String product);
	String downloadAppDlgSubhead(String product);
	String downloadAppDlgUrlMac();
	String downloadAppDlgUrlWin32();
	String downloadAppDlgUrlWin64();

	// Strings used in the "Select Principals" widget
	String selectPrincipalsWidget_CantSelectExternalUserPrompt();
	String selectPrincipalsWidget_NameCol();
	String selectPrincipalsWidget_NoPrincipalsHint();
	String selectPrincipalsWidget_PrincipalAlreadyInListPrompt( String name );
	String selectPrincipalsWidget_RemovePrincipalHint();
	String selectPrincipalsWidget_RightsCol();
	String selectPrincipalsWidget_SelectPrincipalsLabel();
	String selectPrincipalsWidget_TypeCol();

	// Strings used by the 'Manage Users' dialog.
	String manageUsersDlgCaption();
	
	// Strings used by the 'Import Profiles' dialog.
	String importProfilesDlgErrorBogusJSONData( String msg );
	String importProfilesDlgErrorFailed( String msg );
	String importProfilesDlgErrorNoFile();
	String importProfilesDlgErrorParse( String msg );
	String importProfilesDlgHeader();
	String importProfilesDlgSuccess();
	String importProfilesDlgViewSample();
	
	// Strings used by the 'User Share Rights' dialog.
	String userShareRightsDlgError_NoWorkspace();
	String userShareRightsDlgError_SetFailures();
	String userShareRightsDlgHeader( int count );
	String userShareRightsDlgLabel_Allow();
	String userShareRightsDlgLabel_AllowForwarding();
	String userShareRightsDlgLabel_AllowSharingWith();
	String userShareRightsDlgLabel_Clear();
	String userShareRightsDlgLabel_ExternalUsers();
	String userShareRightsDlgLabel_InternalUsers();
	String userShareRightsDlgLabel_NoChange();
	String userShareRightsDlgLabel_NoZoneSettings();
	String userShareRightsDlgLabel_Public();
	String userShareRightsDlgProgress( int done, int total );

	// Strings used to represent the different rights
	String allowAccess();
	String externalRights();
	String forwardingRights();
	String internalRights();
	String noRights();
	String publicRights();
	String shareWithAllExternalRights();
	String shareWithAllInternalRights();

	// Strings used in the Edit Net Folder Rights dialog
	String editNetFolderRightsDlg_AllowAccessLabel();
	String editNetFolderRightsDlg_Caption();
	String editNetFolderRightsDlg_Instructions();
	String editNetFolderRightsDlg_ReShareLabel();
	String editNetFolderRightsDlg_ShareInternalLabel();
	String editNetFolderRightsDlg_ShareExternalLabel();
	String editNetFolderRightsDlg_SharePublicLabel();

	// Strings used for the empty collection view widget.
	String emptyCollection_Info_MyFiles( String product );
	String emptyCollection_Info_SharedByMe_1( String product );
	String emptyCollection_Info_SharedByMe_2();
	String emptyCollection_Info_SharedWithMe( String product );
	String emptyCollection_Info_NetFolders_1( String product );
	String emptyCollection_Info_NetFolders_2();
	String emptyCollection_Info_NetFolders_3();
	String emptyCollection_Info_NetFolders_4( String product );
	String emptyCollection_SubHead_MyFiles();
	String emptyCollection_SubHead_SharedByMe();
	String emptyCollection_SubHead_SharedWithMe();
	String emptyCollection_SubHead_NetFolders();
	
	// Strings used for the empty file folder view widget.
	String emptyFileFolder_Info_1();
	String emptyFileFolder_Info_2( String company, String product );
	String emptyFileFolder_Info_3( String product );
	String emptyFileFolder_SubHead();
	
	// Strings used for the empty people view widget.
	String emptyPeople_Info_1( String company, String product );
	String emptyPeople_Info_2( String company, String product );
	String emptyPeople_SubHead();
	
	// Strings used in the administration console home page
	String adminConsoleInfoWidget_AdminGuideLabel();
	String adminConsoleInfoWidget_BuildLabel();
	String adminConsoleInfoWidget_FilrApplianceLabel();
	String adminConsoleInfoWidget_FilrHeader();
	String adminConsoleInfoWidget_GeneralInfoFilr();
	String adminConsoleInfoWidget_GeneralInfoVibe();
	String adminConsoleInfoWidget_KablinkHeader();
	String adminConsoleInfoWidget_NovellHeader();
	String adminConsoleInfoWidget_ReleaseLabel();
	String adminConsoleInfoWidget_SeeAdminGuide();
	
	// Strings used by the 'Run a Report' dialog.
	String runAReportDlgCaption();
	String runAReportDlgChoose();
	String runAReportDlgInternalError_UnknownReport(String unknownReport);
	String runAReportDlgSelect();

	// String used by the 'Email Report' dialog.
	String emailReportAndSeparator();
	String emailReportCaption();
	String emailReportReportColumn_AttachedFiles();
	String emailReportReportColumn_Comment();
	String emailReportReportColumn_From();
	String emailReportReportColumn_LogStatus();
	String emailReportReportColumn_LogType();
	String emailReportReportColumn_SendDate();
	String emailReportReportColumn_Subject();
	String emailReportReportColumn_ToAddresses();
	String emailReportRunReport();
	String emailReportTypeErrors();
	String emailReportTypeReceived();
	String emailReportTypeSent();
	String emailReportWarning_NoData();
	
	// String used by the 'License Report' dialog.
	String licenseReportAndSeparator();
	String licenseReportCaption();
	String licenseReportReport_Activity();
	String licenseReportReport_AllowedExt();
	String licenseReportReport_AllowedExtNote();
	String licenseReportReport_AllowedReg();
	String licenseReportReport_AllowedRegNote();
	String licenseReportReport_CurrentActive(long count);
	String licenseReportReport_CurrentLicense();
	String licenseReportReport_Effective();
	String licenseReportReport_KeyIssued();
	String licenseReportReport_KeyUID();
	String licenseReportReport_License(String company, String product, String date);
	String licenseReportReport_ProductTitle();
	String licenseReportReport_StatsCol_Checksum();
	String licenseReportReport_StatsCol_Date();
	String licenseReportReport_StatsCol_Local();
	String licenseReportReport_StatsCol_LDAP();
	String licenseReportReport_StatsCol_OpenId();
	String licenseReportReport_StatsCol_365();
	String licenseReportRunReport();
	
	// String used by the 'Login Report' dialog.
	String loginReportAndSeparator();
	String loginReportCaption1();
	String loginReportCaption2();
	String loginReportPeople();
	String loginReportRemove();
	String loginReportRunReport();
	String loginReportSort();
	String loginReportSortAll_Date();
	String loginReportSortAll_User();
	String loginReportSortSummaries_Last();
	String loginReportSortSummaries_None();
	String loginReportSortSummaries_Number();
	String loginReportSortSummaries_User();
	String loginReportType_All();
	String loginReportType_Summaries();
	String loginReportWarning_UserAlreadySelected(String name);
	
	// String used by the 'System Error Log Report' composite.
	String systemErrorLogReportCaption();
	String systemErrorLogReportRunReport();

	// Strings used in the Configure Mobile Apps dialog
	String configureMobileAppsDlgAllowAccess( String productName );
	String configureMobileAppsDlgAllowCacheContent();
	String configureMobileAppsDlgAllowCachePwd();
	String configureMobileAppsDlgAllowPlayWithOtherApps();
	String configureMobileAppsDlgHeader();
	String configureMobileAppsDlgHeader2();
	String configureMobileAppsDlgOnSaveUnknownException( String err );
	String configureMobileAppsDlgUseGlobalSettings();
	String configureMobileAppsDlgUseUserSettings();
	String configureMobileAppsSyncIntervalLabel();
	String configureMobileAppsSyncMinutesLabel();
	
	// Strings used in the Configure User Mobile Apps dialog
	String configuerUserMobileAppsDlgErrorHeader();
	String configureUserMobileAppsDlgHeader( String numUsers );
	String configureUserMobileAppsDlgOnSaveUnknownException( String err );
	String configuerUserMobileAppsDlgSaving( String completed, String total );

	// String used by the 'User Access Report' composite.
	String userAccessReportCaption();
	String userAccessReportObjectsColName();
	String userAccessReportObjectsColType();
	String userAccessReportObjectsHint();
	String userAccessReportObjectType_Folder();
	String userAccessReportObjectType_Profiles();
	String userAccessReportObjectType_Unknown();
	String userAccessReportObjectType_Workspace();
	String userAccessReportUser();
	String userAccessReportUserHint();
	String userAccessReportWarning_NoData();
	
	// String used by the 'User Activity Report' composite.
	String userActivityReportAndSeparator();
	String userActivityReportCaption1();
	String userActivityReportCaption2();
	String userActivityReportPeople();
	String userActivityReportRemove();
	String userActivityReportRunReport();
	String userActivityReportType_All();
	String userActivityReportType_Summaries();
	String userActivityReportWarning_UserAlreadySelected(String name);

	// Strings used by the 'User Properties' dialog.
	String userPropertiesDlgEdit_NetFolders();
	String userPropertiesDlgEdit_PersonalStorage();
	String userPropertiesDlgEdit_Profile();
	String userPropertiesDlgEdit_Quotas();
	String userPropertiesDlgEdit_Sharing();
	String userPropertiesDlgHeader();
	String userPropertiesDlgLabelize(String label);
	String userPropertiesDlgLabel_eDirContainer();
	String userPropertiesDlgLabel_Home();
	String userPropertiesDlgLabel_HomePath();
	String userPropertiesDlgLabel_LastLogin();
	String userPropertiesDlgLabel_LdapDN();
	String userPropertiesDlgLabel_NetFolders();
	String userPropertiesDlgLabel_PersonalStorage();
	String userPropertiesDlgLabel_Quota();
	String userPropertiesDlgLabel_Sharing();
	String userPropertiesDlgLabel_Source();
	String userPropertiesDlgLabel_Type();
	String userPropertiesDlgLabel_UserId();
	String userPropertiesDlgNetFolders();
	String userPropertiesDlgNo();
	String userPropertiesDlgNoAboutMe();
	String userPropertiesDlgNoHome();
	String userPropertiesDlgNoNF();
	String userPropertiesDlgNoQuota();
	String userPropertiesDlgNoWS();
	String userPropertiesDlgPersonalStorage();
	String userPropertiesDlgPersonalStorage_Clear();
	String userPropertiesDlgPersonalStorage_Disable();
	String userPropertiesDlgPersonalStorage_Enable();
	String userPropertiesDlgPersonalStorage_NoGlobal();
	String userPropertiesDlgPersonalStorage_NoPerUser();
	String userPropertiesDlgPersonalStorage_YesGlobal();
	String userPropertiesDlgPersonalStorage_YesPerUser();
	String userPropertiesDlgProfile();
	String userPropertiesDlgQuota();
	String userPropertiesDlgQuota_Group(long quota);
	String userPropertiesDlgQuota_User( long quota);
	String userPropertiesDlgQuota_Zone( long quota);
	String userPropertiesDlgQuotasDisabled();
	String userPropertiesDlgSharing();
	String userPropertiesDlgSharing_External();
	String userPropertiesDlgSharing_Forwarding();
	String userPropertiesDlgSharing_Internal();
	String userPropertiesDlgSharing_NoRights();
	String userPropertiesDlgSharing_Public();
	String userPropertiesDlgSourceLDAP();
	String userPropertiesDlgSourceLocal();
	String userPropertiesDlgUnknown();
	String userPropertiesDlgYes();

	// Strings used in the "Edit Zone Share Rights" dialog.
	String editZoneShareRightsDlg_Header();
	String editZoneShareRightsDlg_ReadingRights();
	String editZoneShareRightsDlg_SavingRights();
	String editZoneShareRightsDlg_SelectPrincipalsHint();

	// Strings used in the "Edit Zone Share Rights" dialog
	String editUserZoneShareRightsDlg_Caption();
	String editUserZoneShareRightsDlg_Instructions();
	String editUserZoneShareRightsDlg_ReShareLabel();
	String editUserZoneShareRightsDlg_ShareExternalLabel();
	String editUserZoneShareRightsDlg_ShareInternalLabel();
	String editUserZoneShareRightsDlg_SharePublicLabel();
	String editUserZoneShareRightsDlg_ShareWithAllExternalUsersLabel();
	String editUserZoneShareRightsDlg_ShareWithAllInternalUsersLabel();
	
	// Strings used by the 'Rename an Entity' dialog.
	String renameEntityDlgError_BogusEntity(String entityType);
	String renameEntityDlgError_NoName_File();
	String renameEntityDlgError_NoName_Folder();
	String renameEntityDlgError_NoName_Unknown();
	String renameEntityDlgError_NoName_Workspace();
	String renameEntityDlgHeader_File();
	String renameEntityDlgHeader_Folder();
	String renameEntityDlgHeader_Unknown();
	String renameEntityDlgHeader_Workspace();
	String renameEntityDlgName();
	
	// Strings used by the 'File Conflicts' dialog.
	String fileConflictsDlgBtnCancel();
	String fileConflictsDlgBtnOverwrite();
	String fileConflictsDlgBtnVersion();
	String fileConflictsDlgConfirmOverwrite();
	String fileConflictsDlgConfirmVersion();
	String fileConflictsDlgConflictingFiles();
	String fileConflictsDlgHeader();

	// Strings used in the Share with public info dialog
	String shareWithPublicInfoDlg_Header();
	String shareWithPublicInfoDlg_Instructions( String itemName );
	String shareWithPublicInfoDlg_Instructions2( String itemName );

	// Strings used in the Change Password dialog
	String changePasswordDlg_ChangeDefaultPasswordHint();
	String changePasswordDlg_ChangingPassword();
	String changePasswordDlg_ConfirmPasswordLabel();
	String changePasswordDlg_CurrentPasswordLabel();
	String changePasswordDlg_EnterCurrentPwd();
	String changePasswordDlg_ErrorChangingPassword( String desc );
	String changePasswordDlg_ChangeDefaultPasswordHeader();
	String changePasswordDlg_NewPasswordLabel();
	String changePasswordDlg_PasswordCannotBeEmpty();
	String changePasswordDlg_PasswordsDoNotMatch();
	
	
}// end GwtTeamingMessages
