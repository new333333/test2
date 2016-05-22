/**
 * Copyright (c) 1998-2015 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2015 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2015 Novell, Inc. All Rights Reserved.
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
 * @author drfoster@novell.com
 */
public interface GwtTeamingMessages extends Messages {
	// Base product names.  Non-translatable?
	String companyNovell();
	String productFilr();
	String productVibe();
	
	// Strings used in the 'find' name-completion control.
	String findCtrl_NoItemsFound();
	String nOfn_Approximate(int value1, int value2, int value3);
	String nOfn_Exact(      int value1, int value2, int value3);
	String searching();
	String searchEntireSiteLabel();
	String searchCurrentFolderWorkspaceLabel();

	// Strings used in the 'Preview landing page' dialog.
	String previewLandingPageDlgHeader();
	
	// Strings used with the Custom Jsp widget in the landing page
	// editor.
	String customJspAssocEntry();
	String customJspAssocFolder();
	String customJspLabel();
	String customJspName();
	String customJspProperties();
	
	// Strings used with the Entry widget in the landing page editor.
	String currentEntry();
	String entryLabel();
	String entryProperties();
	String findEntry();
	String noEntrySelected();
	String pleaseSelectAnEntry();
	
	// Strings used with the Folder widget in the landing page editor.
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
	
	// Strings used with the Google Gadget widget in the landing page
	// editor.
	String googleGadgetCodeLabel();
	String googleGadgetProperties();
	
	// Strings used with the Graphic widget in the landing page editor.
	String graphicLabel();
	String graphicProperties();
	String noFileAttachmentsHint();
	String selectGraphicLabel();
	
	// Strings used in the Enhanced Views widget in the landing page
	// editor.
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
	
	// lpe stands for Landing Page Editor.
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
	
	// Strings used with the 'Link to entry' widget.
	String linkToEntryLabel();
	String linkToEntryTitleLabel();
	String linkToEntryProperties();
	String openEntryInNewWnd();
	
	// Strings used with the 'Link to folder widget.
	String currentFolderWorkspace();
	String folderOrWorkspaceLabel();
	String linkToFolderLabel();
	String linkToFolderProperties();
	String linkToFolderTitleLabel();
	String openFolderInNewWnd();
	String pleaseSelectAFolderOrWorkspace();
	
	// Strings used with the 'Link to URL' widget.
	String linkToUrl();
	String linkToUrlLabel();
	String linkToUrlProperties();
	String linkToUrlUrl(String url);
	String openUrlInNewWnd();
	
	// Strings used in the 'List' widget properties dialog.
	String listProperties();
	
	// Strings used with the 'table' widget.
	String columnXWidth(      int colNum);
	String emptyColumnWidth(  int colNum);
	String invalidColumnWidth(int colNum);
	String invalidNumberOfRows();
	String invalidTotalTableWidth();
	String numColumns();
	String numRows();
	String tableProperties();
	
	// String used in the 'IFrame' widget properties dialog.
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

	// Miscellaneous strings.
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
	String apply();
	String betaWithProduct(String productName);
	String licenseExpired( String productName);
	String licenseInvalid( String productName);
	String cancel();
	String cantAccessEntry();
	String cantAccessFolder();
	String change();
	String confirmChangesWillBeLost();
	String edit();
	String find();
	String helpDlg();
	String login();
	String missingRequestInfo();
	String none();
	String ok();
	String oneMomentPlease();
	String overflowLabel();
	String overflowShortLabel(String prop);
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
	String tourDlg();
	String tourMissingStart();
	String tourMissingStop();
	String unknownFileUploadError(String erro);
	String vibeInsideLandingPage();
	String close();
	String yes();
	String no();
	
	// Strings used with the Utility Element widget in the landing page
	// editor.
	String utilityElementHint();
	String utilityElementLabel();
	String utilityElementProperties();
	String utilityElementLinkToAdminPage();
	String utilityElementLinkToMyWorkspace();
	String utilityElementLinkToShareFolderOrWorkspace();
	String utilityElementLinkToTrackFolderOrWorkspace();
	String utilityElementSignInForm();
	String utilityElementVideoTutorials();
	
	String testPanelState(String value);
	String testWaiting();
	
	// Strings used with extensions.
	String extensionsName();
	String extensionsDesc();
	String extensionsZone();
	String extensionsRemove();
	String extensionsRemoveFailed();
	String extensionsConfirmDelete();
	String extensionsWaiting();
	String extensionsRPCError();
	
	// Strings used with extensions dialog.
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
	String rpcFailure_AddNewProxyIdentity();
	String rpcFailure_CanAddFolder();
	String rpcFailure_CanManagePublicTags();
	String rpcFailure_CanModifyBinder();
	String rpcFailure_ChangeEntryTypes();
	String rpcFailure_ChangeFavoriteState();
	String rpcFailure_CheckForActivityStreamChanges();
	String rpcFailure_CheckNetFoldersStatus();
	String rpcFailure_CheckNetFolderRootsStatus();
	String rpcFailure_ClearUsersAdHocFolders();
	String rpcFailure_ClearUsersDownload();
	String rpcFailure_ClearUsersPublicCollection();
	String rpcFailure_ClearUsersWebAccess();
	String rpcFailure_CollapseSubtasks();
	String rpcFailure_CopyEntries();
	String rpcFailure_CreateApplicationAlreadyExists();
	String rpcFailure_CreateApplicationGroupAlreadyExists();
	String rpcFailure_CreateBlogPage();
	String rpcFailure_CreateChangeLogReport();
	String rpcFailure_CreateDummyMobileDevices();
	String rpcFailure_CreateEmailReport();
	String rpcFailure_CreateGroup();
	String rpcFailure_CreateGroupAlreadyExists();
	String rpcFailure_CreateUserAlreadyExists();
	String rpcFailure_CreateLicenseReport();
	String rpcFailure_CreateLoginReport();
	String rpcFailure_CreateUserAccessReport();
	String rpcFailure_CreateUserActivityReport();
	String rpcFailure_DeleteCustomizedEmailTemplates();
	String rpcFailure_DeleteMobileDevices();
	String rpcFailure_DeleteNetFolders();
	String rpcFailure_DeleteNetFolderServers();
	String rpcFailure_DeleteProxyIdentities();
	String rpcFailure_DeleteGroups();
	String rpcFailure_DeleteSelectedUsers();
	String rpcFailure_DeleteSelections();
	String rpcFailure_DeleteShares();
	String rpcFailure_DeleteTasks();
	String rpcFailure_DisableUsers();
	String rpcFailure_DisableUsersAdHocFolders();
	String rpcFailure_DisableUsersDownload();
	String rpcFailure_DisableUsersPublicCollection();
	String rpcFailure_DisableUsersWebAccess();
	String rpcFailure_EditEntry();
	String rpcFailure_EmailPublicLink();
	String rpcFailure_EnableUsers();
	String rpcFailure_EnableUsersAdHocFolders();
	String rpcFailure_EnableUsersDownload();
	String rpcFailure_EnableUsersPublicCollection();
	String rpcFailure_EnableUsersWebAccess();
	String rpcFailure_EntryDoesNotExist();
	String rpcFailure_executeCustomJsp();
	String rpcFailure_executeEnhancedViewJsp();
	String rpcFailure_ExpandBucket();
	String rpcFailure_ExpandSubtasks();
	String rpcFailure_FindUserByEmailAddress();
	String rpcFailure_FolderDoesNotExist();
	String rpcFailure_ForceFilesUnlock();
	String rpcFailure_ForceUsersToChangePassword();
	String rpcFailure_GetAccessoryStatus();
	String rpcFailure_GetActivityStreamParams();
	String rpcFailure_GetActivityStreamsTree();
	String rpcFailure_GetAddMeetingUrl();
	String rpcFailure_GetAdhocFolderSetting();
	String rpcFailure_GetAdminActions();
	String rpcFailure_GetAllNetFolders();
	String rpcFailure_GetAllNetFolderServers();
	String rpcFailure_GetAllGroups();
	String rpcFailure_GetAntiVirusSettings();
	String rpcFailure_GetBinderDescription();
	String rpcFailure_GetBinderFilters();
	String rpcFailure_GetBinderHasOtherComponents();
	String rpcFailure_GetBinderOwnerAvatarUrl();
	String rpcFailure_GetBinderSharingRightsInfo();
	String rpcFailure_GetBinderStats();
	String rpcFailure_GetBlogArchiveInfo();
	String rpcFailure_GetBlogPages();
	String rpcFailure_GetCalendarAppointments();
	String rpcFailure_GetCalendarDisplayData();
	String rpcFailure_GetCalendarNextPreviousPeriod();
	String rpcFailure_GetCanAddEntities();
	String rpcFailure_GetCanAddEntitiesToBinders();
	String rpcFailure_GetCanManageBinderTags();
	String rpcFailure_GetClipboardTeamUsers();
	String rpcFailure_GetClipboardUsers();
	String rpcFailure_GetClipboardUsersFromList();
	String rpcFailure_GetCollectionPointUrl();
	String rpcFailure_GetCommentCount();
	String rpcFailure_GetDateStr();
	String rpcFailure_GetDefaultUserSettings();
	String rpcFailure_GetDesktopAppDownloadInfo();
	String rpcFailure_GetDownloadFileUrl();
	String rpcFailure_GetDownloadFolderAsCSVFileUrl();
	String rpcFailure_GetEmailNotificationInfo();
	String rpcFailure_GetEntityActionToolbarItems();
	String rpcFailure_GetEntityPermalink();
	String rpcFailure_GetEntityRights();
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
	String rpcFailure_GetFolderEntryTypes();
	String rpcFailure_GetFolderFilters();
	String rpcFailure_GetFolderHasUserList();
	String rpcFailure_GetFolderRows();
	String rpcFailure_GetFolderSortSetting();
	String rpcFailure_GetFolderToolbarItems();
	String rpcFailure_GetFooterToolbarItems();
	String rpcFailure_GetGroupActionToolbarItems();
	String rpcFailure_GetHtml5Specs();
	String rpcFailure_GetKeyShieldConfig();
	String rpcFailure_GetNetFolderGlobalSettings();
	String rpcFailure_GetLandingPageData();
	String rpcFailure_GetLdapConfig();
	String rpcFailure_GetLdapServerData();
	String rpcFailure_GetLdapSupportsExternalUserImport();
	String rpcFailure_GetListOfAttachments();
	String rpcFailure_GetLocales();
	String rpcFailure_GetBinderAccessories();
	String rpcFailure_GetBinderAccessory();
	String rpcFailure_GetBinderInfo();
	String rpcFailure_GetBinderPermalink();
	String rpcFailure_GetBranding();
	String rpcFailure_GetChangeLogsHtml();
	String rpcFailure_GetClickOnTitleAction();
	String rpcFailure_GetCreditsHtml();
	String rpcFailure_GetDatabasePruneDlgConfiguration();
	String rpcFailure_GetDataQuotaExceededHtml();
	String rpcFailure_GetDataQuotaHighwaterExceededHtml();
	String rpcFailure_GetDefaultActivityStream();
	String rpcFailure_GetDefaultStorageId();
	String rpcFailure_GetDesktopBranding();
	String rpcFailure_GetDiskUsageHtml();
	String rpcFailure_GetDiskUsageInfo();
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
	String rpcFailure_GetHtmlElementInfo();
	String rpcFailure_GetImUrl();
	String rpcFailure_GetInheritedLandingPageProperties();
	String rpcFailure_GetIsDynamicGroupMembershipAllowed();
	String rpcFailure_GetJspHtml();
	String rpcFailure_GetLdapSyncResults();
	String rpcFailure_GetLimitedUserVisibilityDisplay();
	String rpcFailure_GetLimitUserVisibilityInfo();
	String rpcFailure_GetListOfChildBinders();
	String rpcFailure_GetLocale();
	String rpcFailure_GetMailToPublicLinks();
	String rpcFailure_GetMainPageInfo();
	String rpcFailure_GetManageAdministratorsInfo();
	String rpcFailure_GetManageEmailTemplatesInfo();
	String rpcFailure_GetManageMobileDevicesInfo();
	String rpcFailure_GetManageProxyIdentitiesInfo();
	String rpcFailure_GetManageTeamsInfo();
	String rpcFailure_GetManageTeamsState();
	String rpcFailure_GetManageUsersInfo();
	String rpcFailure_GetManageUsersState();
	String rpcFailure_GetMobileAppsConfiguration();
	String rpcFailure_GetMobileBranding();
	String rpcFailure_GetMyFilesContainerInfo();
	String rpcFailure_GetMyTeams();
	String rpcFailure_GetNameCompletionSettings();
	String rpcFailure_NumberOfMembers();
	String rpcFailure_GetNetFolder();
	String rpcFailure_GetNetFolderSyncStatistics();
	String rpcFailure_GetNextFolderEntryInfo();
	String rpcFailure_GetNextFolderEntryInfo_NoAccess();
	String rpcFailure_GetParentBinderPermalink();
	String rpcFailure_GetPasswordExpiration();
	String rpcFailure_GetPasswordPolicyConfig();
	String rpcFailure_GetPasswordPolicyInfo();
	String rpcFailure_GetPersonalPreferences();
	String rpcFailure_GetPersonalWorkspaceDisplayData();
	String rpcFailure_GetPhotoAlbumDisplayData();
	String rpcFailure_GetPreviousFolderEntryInfo();
	String rpcFailure_GetPreviousFolderEntryInfo_NoAccess();
	String rpcFailure_GetPresenceInfo();
	String rpcFailure_GetPrincipalInfo();
	String rpcFailure_GetProfileAvatars();
	String rpcFailure_GetProfileEntryInfo();
	String rpcFailure_GetProfileInfo();
	String rpcFailure_GetProfileStats();
	String rpcFailure_GetProjectInfo();
	String rpcFailure_GetPublicLinks();
	String rpcFailure_GetRecentPlaces();
	String rpcFailure_GetReportsInfo();
	String rpcFailure_GetRootWorkspaceId();
	String rpcFailure_GetSavedSearches();
	String rpcFailure_GetSelectedUsersDetails();
	String rpcFailure_GetSelectionDetails();
	String rpcFailure_GetSelfRegInfo();
	String rpcFailure_GetSendToFriendUrl();
	String rpcFailure_GetShareBinderPageUrl();
	String rpcFailure_GetSharedViewState();
	String rpcFailure_GetSharingInfo();
	String rpcFailure_GetShareLists();
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
	String rpcFailure_GetTelemetrySettings();
	String rpcFailure_GetTimeZones();
	String rpcFailure_GetToolbarItems();
	String rpcFailure_GetTopLevelEntryId();
	String rpcFailure_GetTopRanked();
	String rpcFailure_GetTrackedPeople();
	String rpcFailure_GetTrackedPlaces();
	String rpcFailure_GetTrashUrl();
	String rpcFailure_GetTree();
	String rpcFailure_GetUpdateLogsConfig();
	String rpcFailure_GetUpgradeInfo();
	String rpcFailure_GetUserAccessInfo();
	String rpcFailure_GetUserAvatar();
	String rpcFailure_GetUserFileSyncAppConfig();
	String rpcFailure_GetUserListInfo();
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
	String rpcFailure_GetWikiDisplayData();
	String rpcFailure_GetWorkspaceContributorIds();
	String rpcFailure_GetXssHtml();
	String rpcFailure_GetXsrfToken();
	String rpcFailure_GetZipDownloadUrl();
	String rpcFailure_GetZoneShareRights();
	String rpcFailure_GetZoneShareTerms();
	String rpcFailure_HideShares();
	String rpcFailure_ImportIcalByUrl();
	String rpcFailure_InvalidateSession();
	String rpcFailure_IsAllUsersGroup();
	String rpcFailure_IsPersonTracked();
	String rpcFailure_LdapGuidNotConfigured();
	String rpcFailure_LockEntries();
	String rpcFailure_MarkFolderContentsRead();
	String rpcFailure_MarkFolderContentsUnread();
	String rpcFailure_markupStringReplacement();
	String rpcFailure_ModifyGroup();
	String rpcFailure_ModifyGroupMembership();
	String rpcFailure_ModifyProxyIdentity();
	String rpcFailure_MoveEntries();
	String rpcFailure_PersistActivityStreamSelection();
	String rpcFailure_PersistExpansionState();
	String rpcFailure_PinEntry();
	String rpcFailure_PurgeTasks();
	String rpcFailure_QViewMicroBlog();
	String rpcFailure_RemoveAndroidMobileBranding();
	String rpcFailure_RemoveBinderTag();
	String rpcFailure_RemoveFavorite();
	String rpcFailure_RemoveIosMobileBranding();
	String rpcFailure_RemoveMacDesktopBranding();
	String rpcFailure_RemoveSavedSearch();
	String rpcFailure_RemoveWindowsDesktopBranding();
	String rpcFailure_RemoveWindowsMobileBranding();
	String rpcFailure_RenameEntity_File();
	String rpcFailure_RenameEntity_Folder();
	String rpcFailure_RenameEntity_Unknown();
	String rpcFailure_RenameEntity_Workspace();
	String rpcFailure_ReplyToEntry();
	String rpcFailure_ResetVelocityEngine();
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
	String rpcFailure_SaveFolderFilters();
	String rpcFailure_SaveFolderPinningState();
	String rpcFailure_SaveFolderSort();
	String rpcFailure_SaveHtmlElementStatus();
	String rpcFailure_SaveKeyShieldConfig();
	String rpcFailure_SaveLdapConfig();
	String rpcFailure_SaveManageUsersState();
	String rpcFailure_SaveNameCompletionSettings();
	String rpcFailure_SavePasswordPolicyConfig();
	String rpcFailure_SavePersonalPreferences();
	String rpcFailure_SaveSearch();
	String rpcFailure_SaveSharedFilesState();
	String rpcFailure_SaveShareExpirationValue();
	String rpcFailure_SaveShareLists();
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
	String rpcFailure_SaveUpdateLogsConfig();
	String rpcFailure_SaveUserAccessConfig();
	String rpcFailure_SaveUserListStatus();
	String rpcFailure_SaveWhatsNewShowSetting();
	String rpcFailure_SaveZoneShareRights();
	String rpcFailure_SaveZoneShareTerms();
	String rpcFailure_Search();
	String rpcFailure_SendForgottenPwdEmail();
	String rpcFailure_SendNotificationEmail();
	String rpcFailure_SetAntiVirusSettings();
	String rpcFailure_SetBinderSharingRightsInfo();
	String rpcFailure_SetDefaultUserSettings();
	String rpcFailure_SetDesktopAppDownloadVisibility();
	String rpcFailure_SetEntriesPinState();
	String rpcFailure_SetHasSeenOesWarning();
	String rpcFailure_SetMobileDevicesWipeScheduledState();
	String rpcFailure_SetPrincipalsAdminRights();
	String rpcFailure_SetPrincipalsLimitedUserVisibility();
	String rpcFailure_SetSeen();
	String rpcFailure_SetStatus();
	String rpcFailure_SetTelemetrySettings();
	String rpcFailure_SetTelemetryTier2Enabled();
	String rpcFailure_SetUnseen();
	String rpcFailure_ShareEntry();
	String rpcFailure_ShowShares();
	String rpcFailure_StartLdapSync();
	String rpcFailure_StartLdapSyncPreview();
	String rpcFailure_StopSyncNetFolders();
	String rpcFailure_SyncNetFolders();
	String rpcFailure_SyncNetFolderServer();
	String rpcFailure_SyncNetFolderRoots();
	String rpcFailure_TestAntiVirusSettings();
	String rpcFailure_TestGroupMembershipCriteria();
	String rpcFailure_ErrorTestingKeyShieldConnection();
	String rpcFailure_ErrorTestingNetFolderServerConnection();
	String rpcFailure_TrackingBinder();
	String rpcFailure_TrackingPerson();
	String rpcFailure_TrashPurgeAll();
	String rpcFailure_TrashPurgeSelectedEntities();
	String rpcFailure_TrashRestoreAll();
	String rpcFailure_TrashRestoreSelectedEntities();
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
	String rpcFailure_UserNotLoggedIn_InvalidPassword();
	String rpcFailure_UserNotLoggedIn_InvalidUsername();
	String rpcFailure_UserNotLoggedIn_LogonFailed();
	String rpcFailure_SetUserSharingRightsInfo();
	String rpcFailure_ValidateEmailAddress();
	String rpcFailure_ValidateEntryEvents();
	String rpcFailure_ValidateShareLists();
	String rpcFailure_ValidateUploads();
	String rpcFailure_XsrfTokenFailure();

	// Strings used to describe various split point load failures.
	String codeSplitFailure_AccessoriesPanel();
	String codeSplitFailure_ActivityStreamCtrl();
	String codeSplitFailure_AddFilesDlg();
	String codeSplitFailure_AddFilesHtml5Popup();
	String codeSplitFailure_AddNewFolderDlg();
	String codeSplitFailure_AdminControl();
	String codeSplitFailure_AdminInfoDlg();
	String codeSplitFailure_AdministratorsView();
	String codeSplitFailure_AlertDlg();
	String codeSplitFailure_BinderOwnerAvatarPanel();
	String codeSplitFailure_BinderShareRightsDlg();
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
	String codeSplitFailure_CloudFolderAuthenticationDlg();
	String codeSplitFailure_CollectionView();
	String codeSplitFailure_ConfigureAdhocFoldersDlg();
	String codeSplitFailure_ConfigureAntiVirusDlg();
	String codeSplitFailure_ConfigureFileSyncAppDlg();
	String codeSplitFailure_ConfigureMobileAppsDlg();
	String codeSplitFailure_ConfigurePasswordPolicyDlg();
	String codeSplitFailure_ConfigureTelemetryDlg();
	String codeSplitFailure_ConfigureUserAccessDlg();
	String codeSplitFailure_ConfigureUpdateLogsDlg();
	String codeSplitFailure_ConfigureUserFileSyncAppDlg();
	String codeSplitFailure_ConfigureUserMobileAppsDlg();
	String codeSplitFailure_ConfirmDlg();
	String codeSplitFailure_ContentControl();
	String codeSplitFailure_CopyFiltersDlg();
	String codeSplitFailure_CreateBlogPageDlg();
	String codeSplitFailure_CopyMoveEntriesDlg();
	String codeSplitFailure_CopyPublicLinkDlg();
	String codeSplitFailure_CustomBinderView();
	String codeSplitFailure_DefaultUserSettingsDlg();
	String codeSplitFailure_DeleteSelectedUsersDlg();
	String codeSplitFailure_DeleteSelectionsDlg();
	String codeSplitFailure_DescriptionPanel();
	String codeSplitFailure_DesktopAppDownloadControl();
	String codeSplitFailure_DesktopAppDownloadDlg();
	String codeSplitFailure_DiscussionFolderView();
	String codeSplitFailure_DiscussionWSView();
	String codeSplitFailure_DownloadPanel();
	String codeSplitFailure_EditBrandingDlg();
	String codeSplitFailure_EditDesktopBrandingDlg();
	String codeSplitFailure_EditKeyShieldConfigDlg();
	String codeSplitFailure_EditLdapConfigDlg();
	String codeSplitFailure_EditLdapSearchDlg();
	String codeSplitFailure_EditLdapServerConfigDlg();
	String codeSplitFailure_EditMobileBrandingDlg();
	String codeSplitFailure_EditNetFolderRightsDlg();
	String codeSplitFailure_EditPasswordPolicyComposite();
	String codeSplitFailure_EditPublicLinkDlg();
	String codeSplitFailure_EditShareDlg();
	String codeSplitFailure_EditShareNoteDlg();
	String codeSplitFailure_EditShareRightsDlg();
	String codeSplitFailure_EditUserZoneShareRightsDlg();
	String codeSplitFailure_EditZoneShareSettingsDlg();
	String codeSplitFailure_EmailNotificationDlg();
	String codeSplitFailure_EmailPublicLinkDlg();
	String codeSplitFailure_EmailTemplatesView();
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
	String codeSplitFailure_ForgottenPwdDlg();
	String codeSplitFailure_GenericWSView();
	String codeSplitFailure_GlobalWorkspacesView();
	String codeSplitFailure_GuestbookFolderView();
	String codeSplitFailure_HomeWSView();
	String codeSplitFailure_HtmlElementPanel();
	String codeSplitFailure_Html5UploadHelper();
	String codeSplitFailure_ImportIcalByFileDlg();
	String codeSplitFailure_ImportIcalByUrlDlg();
	String codeSplitFailure_ImportProfilesDlg();
	String codeSplitFailure_NetFolderGlobalSettingsDlg();
	String codeSplitFailure_LandingPage();
	String codeSplitFailure_LandingPageEditor();
	String codeSplitFailure_LandingPageWidget();
	String codeSplitFailure_LdapBrowserDlg();
	String codeSplitFailure_LdapSyncResultsDlg();
	String codeSplitFailure_LimitUserVisibilityDlg();
	String codeSplitFailure_LimitUserVisibilityView();
	String codeSplitFailure_LoginDlg();
	String codeSplitFailure_MailToMultiplePublicLinksSelectDlg();
	String codeSplitFailure_MailToPanel();
	String codeSplitFailure_MainMenuControl();
	String codeSplitFailure_MainPage();
	String codeSplitFailure_ManageAdministratorsDlg();
	String codeSplitFailure_ManageCommentsComposite();
	String codeSplitFailure_ManageCommentsDlg();
	String codeSplitFailure_ManageDatabasePruneDlg();
	String codeSplitFailure_ManageEmailTemplatesDlg();
	String codeSplitFailure_ManageGroupsDlg();
	String codeSplitFailure_ManageMobileDevicesDlg();
	String codeSplitFailure_ManageNetFoldersDlg();
	String codeSplitFailure_ManageNetFolderServersDlg();
	String codeSplitFailure_ManageMenuPopup();
	String codeSplitFailure_ManageProxyIdentitiesDlg();
	String codeSplitFailure_ManageSavedSearchesDlg();
	String codeSplitFailure_ManageTeamsDlg();
	String codeSplitFailure_ManageUsersDlg();
	String codeSplitFailure_MicroBlogFolderView();
	String codeSplitFailure_MilestoneFolderView();
	String codeSplitFailure_MirroredFileFolderView();
	String codeSplitFailure_MobileDevicesView();
	String codeSplitFailure_ModifyGroupDlg();
	String codeSplitFailure_ModifyLimitedUserVisibilityDlg();
	String codeSplitFailure_ModifyNetFolderDlg();
	String codeSplitFailure_ModifyNetFolderServerDlg();
	String codeSplitFailure_MultiErrorAlertDlg();
	String codeSplitFailure_MultiPromptDlg();
	String codeSplitFailure_NetFolderSelectPrincipalsWidget();
	String codeSplitFailure_NetFoldersWSView();
	String codeSplitFailure_NetFolderSyncStatisticsDlg();
	String codeSplitFailure_PersonalWorkspacesView();
	String codeSplitFailure_PersonalWorkspaceView();
	String codeSplitFailure_PhotoAlbumFolderView();
	String codeSplitFailure_ProfileAttributeWidget();
	String codeSplitFailure_ProfileEntryDlg();
	String codeSplitFailure_ProfilePage();
	String codeSplitFailure_ProgressDlg();
	String codeSplitFailure_ProjectInfoWidget();
	String codeSplitFailure_ProjectStatsWidget();
	String codeSplitFailure_ProjectManagementWSView();
	String codeSplitFailure_ProxyIdentitiesView();
	String codeSplitFailure_ProxyIdentityDlg();
	String codeSplitFailure_PromptDlg();
	String codeSplitFailure_QuickViewDlg();
	String codeSplitFailure_RenameEntityDlg();
	String codeSplitFailure_RunAReportDlg();
	String codeSplitFailure_SearchOptionsComposite();
	String codeSplitFailure_SelectCSVDelimiterDlg();
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
	String codeSplitFailure_TeamWorkspacesView();
	String codeSplitFailure_TeamWSView();
	String codeSplitFailure_TelemetryTier2Dlg();
	String codeSplitFailure_TinyMCEDlg();
	String codeSplitFailure_TrashView();
	String codeSplitFailure_UserPropertiesDlg();
	String codeSplitFailure_UserListPanel();
	String codeSplitFailure_UserShareRightsDlg();
	String codeSplitFailure_UserStatusControl();
	String codeSplitFailure_ViewsMenuPopup();
	String codeSplitFailure_WhoHasAccessDlg();
	String codeSplitFailure_WikiFolderView();
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
	
	// Strings used with the MastHead.
	String adminMenuItem();
	String administrationHint();
	String downloadFilrDesktopApp();
	String downloadVibeDesktopApp();
	String guest();
	String helpMenuItem();
	String helpHint();
	String ideasPortalMenuItem();
	String ideasPortalMenuItemItem();	
	String invokeUserListHint();
	String loginHint();
	String logoutHint();
	String masthead_BrowseFilr();
	String myWorkspaceHint();
	String newsFeedMenuItem();
	String personalPrefsMenuItem();
	String personalPreferencesHint();
	String resourceLibMenuItem();
	String resourceLibraryHint();
	String teamingFeedHint();
	String trashInformation();
	
	// Strings used in the edit branding dialog.
	String addImage();
	String advancedBtn();
	String backgroundColorLabel();
	String binderOverridesBrandingLabel();
	String brandingDlgHeader();
	String brandingDlgSiteBrandingHeader();
	String brandingRulesLabel();
	String backgroundImgLabel();
	String cantEditBranding();
	String clearBrandingLabel();
	String colorDescription();
	String colorHint();
	String displayColorPicker();
	String editAdvancedBranding();
	String editBrandingDlg_CurrentImage();
	String editBrandingDlg_CustomLoginDlgImg();
	String editBrandingDlg_LoginDialogCaption();
	String editBrandingDlg_LoginDialogImgHint();
	String editBrandingDlg_UploadLabel();
	String editBrandingDlg_Tour_advancedBranding();
	String editBrandingDlg_Tour_backgroundColor();
	String editBrandingDlg_Tour_backgroundImage();
	String editBrandingDlg_Tour_brandingArea(String product);
	String editBrandingDlg_Tour_brandingImage();
	String editBrandingDlg_Tour_finish(       String product);
	String editBrandingDlg_Tour_loginDlgImage(String product);
	String editBrandingDlg_Tour_start(        String product);
	String editBrandingDlg_Tour_textColor();
	String imgNone();
	String invalidBackgroundColor(String color);
	String invalidTextColor(      String color);
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
	String loginDlg_AuthenticateUsingOpenID(String providerName);
	String loginDlg_ConfirmationText();
	String loginDlg_EnterAsGuest();
	String loginDlg_externalUserSelfRegFailed();
	String loginDlg_ExtUserPwdResetHint();
	String loginDlg_ExtUserRegistrationHint();
	String loginDlg_FirstNameLabel();
	String loginDlg_ForgottenPwd();
	String loginDlg_firstNameRequired();
	String loginDlg_InvalidCaptcha();
	String loginDlg_LastNameLabel();
	String loginDlg_lastNameRequired();
	String loginDlg_OrLabel();
	String loginDlg_PwdLabel();
	String loginDlg_pwdRequired();
	String loginDlg_PasswordExpiredHint();
	String loginDlg_PasswordResetComplete();
	String loginDlg_PasswordResetRequested();
	String loginDlg_PwdResetFailed();
	String loginDlg_pwdDoNotMatch();
	String loginDlg_ReenterPwdLabel();
	String loginDlg_TermsLabel();
	String loginDlg_TermsPopupBlockMessage();
	String loginDlg_Register();
	String loginDlg_RegisterUsingSelfReg();
	String loginDlg_ResetPwd();
	String loginDlgAuthenticating();
	String loginDlgCreateNewAccount();
	String loginDlgKablinkHeader();
	String loginDlgLoginFailed();
	String loginDlgLoginWebAccessRestricted();
	String loginDlgNovellFilrHeader();
	String loginDlgNovellHeader();
	String loginDlgOpenIDIdentity();
	String loginDlgPassword();
	String loginDlgReset();
	String loginDlgSelectAuthProviderLabel();
	String loginDlgUseOpenId();
	String loginDlgUserId();
	String loginDlgDownloadingFile1();
	String loginDlgDownloadingFile2();
	
	// Strings used in the personal preferences dialog.
	String accessibleMode();
	String editorOverridesLabel();
	String entryDisplayStyleLabel();
	String numEntriesPerPageCannotBeBlank();
	String numEntriesPerPageInvalidNum();
	String numEntriesPerPageLabel();
	String fileLinkActionLabel();
	String fileLinkActionOption_Download();
	String fileLinkActionOption_ViewDetails();
	String fileLinkActionOption_ViewHtmlElseDetails();
	String fileLinkActionOption_ViewHtmlElseDownload();
	String hidePublicCollectionLabel();
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
	String mainMenuEmailNotificationDlgAltListCollapse();
	String mainMenuEmailNotificationDlgAltListExpand();
	String mainMenuEmailNotificationDlgBannerFilr();
	String mainMenuEmailNotificationDlgBannerVibe();
	String mainMenuEmailNotificationDlgClearEntrySubscription();
	String mainMenuEmailNotificationDlgDigest();
	String mainMenuEmailNotificationDlgHeader();
	String mainMenuEmailNotificationDlgIndividualMessages();
	String mainMenuEmailNotificationDlgIndividualMessagesNoAttachments();
	String mainMenuEmailNotificationDlgMessageType();
	String mainMenuEmailNotificationDlgMultipleItems(int count);
	String mainMenuEmailNotificationDlgNoChanges();
	String mainMenuEmailNotificationDlgNoTitle();
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
	String addNewFolderDlgError_NameTooLong(int max);
	String addNewFolderDlgError_NoName();
	String addNewFolderDlgHeader();
	String addNewFolderDlgName();
	String addNewFolderDlgType();
	String addNewFolderDlg_Type_BoxDotNet();
	String addNewFolderDlg_Type_DropBox();
	String addNewFolderDlg_Type_GoogleDrive();
	String addNewFolderDlg_Type_PersonalStorage();
	String addNewFolderDlg_Type_SkyDrive();

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
	String treeBucketHover(String firstPart, String lastPart);
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
	String treeStopFollowing();
	String treeTrash();
	String treeWSAndFolders();
	
	// Strings used in the 'Add File Attachment' dialog.
	String addFileAttachmentDlgHeader();
	String addFileLabel();
	String noFilesSelectedMsg();
	
	// Strings used in the 'Color picker' dialog
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
	
	// The following strings are used with a tinyMCE editor.
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
	
	// The following strings are for the User Status or Micro blog
	// Control.
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
	
	// The following string are used with the Quick View popup.
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
	String qViewErrorDeletedWorkspace();
	String qViewErrorNoProfile();
	String qViewErrorNoRights();
	String qViewErrorCantTrack();
	String qViewErrorCantUntrack();

	// Profile.
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
	String profileDataPasswordExpires();
	String profileDataQuota();
	String profileQuotaMegaBytes(String size);
	String profileQuotaUsed();
	String profileSetDefaultAvatar();
	String profileRemoveAvatar();
	String profileUpload();
	String profileUploadSelect();
	
	// Presence related strings.
	String presenceAvailable();
	String presenceAway();
	String presenceBusy();
	String presenceIdle();
	String presenceOffline();
	String presenceUnknown();

	// The following strings are used in the Administration Information
	// dialog.
	String adminInfoDlgDocumentationLink();
	String adminInfoDlgEnterProxyCredentials(String serverName);
	String adminInfoDlgExpiredLicense(String productName);
	String adminInfoDlgFilrTasksToBeCompleted();
	String adminInfoDlgHeader();
	String adminInfoDlgInstallGuide();
	String adminInfoDlgLoginAsAdmin();
	String adminInfoDlgRelease();
	String adminInfoDlgSelectNetFolderServerType(String serverName);
	String adminInfoDlgUpgradeDefinitions();
	String adminInfoDlgUpgradeImportTypelessDN();
	String adminInfoDlgUpgradeSearchIndex();
	String adminInfoDlgUpgradeTasksDocumentationLink();
	String adminInfoDlgUpgradeTasksNotDone();
	String adminInfoDlgUpgradeTemplates();

	// The following strings are used in the activity stream control.
	String actionsLabel();
	String autoRefreshIsPaused();
	String followedPeople();
	String followedPlaces();
	String hideComments();
	String hideDesc();
	String markEntryAsReadHint();
	String myFavorites();
	String myTeams();
	String multipleComments(int numComments);
	String nextRefresh(String time);
	String noEntriesFound();
	String noFavorites();
	String noPeopleFollowed();
	String noPlacesFollowed();
	String noTeams();
	String noTitle();
	String oneComment();
	String pauseActivityStream(int refreshRate);
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
	String whatsNewWithName(String name);
	
	// The following strings are used in the activity stream 'reply to
	// entry' UI.
	String addAComment();
	String defaultReplyTitle(String title);
	String noReplyText();
	
	// The following strings are used in the activity stream 'share
	// this entry' UI.
	String addRecipient();
	String commentsLabel();
	String copyPublicLinkTheseItems();
	String defaultShareTitle(String title);
	String editPublicLinkTheseItems();
	String emailPublicLinkTheseItems();
	String manageShares();
	String noShareRecipients();
	String noShareRecipientsOrTeams();
	String removeShareHint();
	String shareAccess();
	String shareCaption();
	String shareDlg_accessLabel();
	String shareDlg_addExternalUserTitle();
	String shareDlg_canShareWith(String shareWith);
	String shareDlg_alreadySharedWithSelectedRecipient(String recipientName);
	String shareDlg_cantShareWithAllExternalUsersGroup();
	String shareDlg_cantShareWithAllInternalUsersGroup();
	String shareDlg_cantShareWithDisabledUser(String name);
	String shareDlg_cantShareWithExternalUser_Param(String emailAddr);
	String shareDlg_cantShareWithPublic();
	String shareDlg_cantShareWithYourself();
	String shareDlg_clickToAddNote();
	String shareDlg_contributor();
	String shareDlg_deleteButton();
	String shareDlg_editButton();
	String shareDlg_editor();
	String shareDlg_emailAddressInvalidPrompt_Param(   String emailAddr);
	String shareDlg_emailAddressInvalid_blDomain_Param(String emailAddr);
	String shareDlg_emailAddressInvalid_blEMA_Param(   String emailAddr);
	String shareDlg_emailAddressInvalid_wl_Param(      String emailAddr);
	String shareDlg_expiresAfter(String after);
	String shareDlg_expiresLabel();
	String shareDlg_expiresNever();
	String shareDlg_expiresOn(String on);
	String shareDlg_fileLabel();
	String shareDlg_findByFileLabel();
	String shareDlg_findByFolderLabel();
	String shareDlg_findByUserLabel();
	String shareDlg_findAllShares();
	String shareDlg_findShareItemsBy();
	String shareDlg_findSharesByFile();
	String shareDlg_findSharesByFolder();
	String shareDlg_findSharesByHint();
	String shareDlg_findSharesByUser();
	String shareDlg_folderLabel();
	String shareDlg_groupMembershipLabel();
	String shareDlg_makePublic();
	String shareDlg_manageShares();
	String shareDlg_manageMultipleItems(int numItems);
	String shareDlg_noNote();
	String shareDlg_noShareItemsFoundHint();
	String shareDlg_noShareItemsHint();
	String shareDlg_noShareItemsToManageHint();
	String shareDlg_noTeamsToShareWith();
	String shareDlg_noteLabel();
	String shareDlg_notifyLabel();
	String shareDlg_publicLinkTitle();
	String shareDlg_publicUrlLabel();
	String shareDlg_readingShareInfo();
	String shareDlg_reshareExternal();
	String shareDlg_reshareInternal();
	String shareDlg_reshareLabel();
	String shareDlg_reshareNo();
	String shareDlg_resharePublic();
	String shareDlg_resharePublicLink();
	String shareDlg_rightsLabel();
	String shareDlg_savingShareInfo();
	String shareDlg_selectMethodToFindShares();
	String shareDlg_selectSharesToDelete();
	String shareDlg_selectSharesToEdit();
	String shareDlg_sendingNotificationEmail();
	String shareDlg_sharedByLabel();
	String shareDlg_sharedWithCol();
	String shareDlg_shareLabel();
	String shareDlg_sharingLabel();
	String shareDlg_sharePublicTitle();
	String shareDlg_shareWithHint();
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
	String shareSharedBy();
	String shareSharedWith();
	String shareTheseItems();
	String shareWithGroups();
	String shareWithTeams();
	String shareWithUsers();
	String sharingMultipleItems(int numItems);
	String unknownShareType();
	String usersWithoutRights();
	
	// The following strings are using in the 'share expiration'
	// dialog.
	String shareExpirationDlg_cantEnterPriorDate();
	String shareExpirationDlg_caption();
	String shareExpirationDlg_days();
	String shareExpirationDlg_expiresAfter();
	String shareExpirationDlg_expiresLabel();
	String shareExpirationDlg_expiresNever();
	String shareExpirationDlg_expiresOn();
	String shareExpirationDlg_noDateEntered();
	String shareExpirationDlg_noDaysEntered();
	
	// The following strings are used in the 'share with teams' dialog.
	String shareWithTeamsDlg_caption();
	String shareWithTeamsDlg_Instructions();
	
	// The following strings are used in the Group Membership popup.
	String allExtUsersGroupDesc();
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

	// The following strings are used in the 'Actions' popup menu.
	String deleteEntry();
	String editEntry();
	String markRead();
	String markUnread();
	String reply();
	String sendToFriend();
	String share();
	String subscribe();
	String tag();
	String viewDetails();
	
	// The following strings are used in the 'Subscribe to Entry'
	// dialog.
	String cantSubscribeNoEmailAddresses();
	String sendEmailTo();
	String sendEmailWithoutAttachmentsTo();
	String sendTextTo();
	String subscribeToEntryDlgHeader();
	String subscribeToEntryHeader();

	// The following strings are used with the 'Tag This' dialog.
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
	String promptSaveBeforeTagSearch(String tagName);
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
	String taskAltParentWithDurationError();
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
	
	// Strings used in the Landing Page Properties dialog.
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
	String hideFooter();
	String hideMasthead();
	String hideMenu();
	String hideSidebar();
	String inheritPropertiesLabel();
	String invalidBorderColor(     String color);
	String invalidContentTextColor(String color);
	String invalidHeaderBgColor(   String color);
	String invalidHeaderTextColor( String color);
	String landingPagePropertiesDlg_BackgroundTab();
	String landingPagePropertiesDlg_BorderTab();
	String landingPagePropertiesDlg_HeaderTab();
	String landingPagePropertiesDlg_MiscTab();
	String landingPagePropertiesDlg_PageStyle();
	String landingPagePropertiesDlg_PageStyleDark();
	String landingPagePropertiesDlg_PageStyleLight();

	// Strings used in the Novell Desktop Application (File Sync.)
	String fileSyncAppAllowAccess(String productName);
	String fileSyncAppAllowCachePwd();
	String fileSyncAppAutoUpdateUrlLabel();
	String fileSyncAppAutoUpdateUrlLabel_UseLocal();
	String fileSyncAppAutoUpdateUrlLabel_UseRemote();
	String fileSyncAppAutoUpdateUrlRequiredPrompt();
	String fileSyncAppDlgHeader();
	String fileSyncAppEnableDeployLabel();
	String fileSyncAppHeader2();
	String fileSyncAppHeader3();
	String fileSyncAppHeader4();
	String fileSyncAppIntervalLabel();
	String fileSyncAppListHint();
	String fileSyncAppMaxFileSizeLabel();
	String fileSyncAppMBLabel();
	String fileSyncAppMinutesLabel();
	String fileSyncAppCleanUpLabel();
	String fileSyncApppDaysLabel();
	String fileSyncApp_Add();
	String fileSyncApp_Delete();
	String fileSyncApp_Description();
	String fileSyncApp_Error_NoProcessName();
	String fileSyncApp_InvalidAutoUpdateUrlText();
	String fileSyncDlg_LabelBlacklist();
	String fileSyncDlg_LabelWhitelist();
	String fileSyncApp_MacApps();
	String fileSyncApp_MacApps_AddPrompt();
	String fileSyncApp_Mode();
	String fileSyncApp_ModeBlacklist();
	String fileSyncApp_ModeBoth();
	String fileSyncApp_ModeDisabled();
	String fileSyncApp_ModeWhitelist();
	String fileSyncApp_PromptHeader();
	String fileSyncApp_RestoreDefaults();
	String fileSyncApp_RestoreDefaults_Alt();
	String fileSyncApp_RestoreDefaults_Confirm();
	String fileSyncApp_OnSaveUnknownException(String desc);
	String fileSyncApp_WindowsApps();
	String fileSyncApp_WindowsApps_AddPrompt();
	String fileSyncAppUseGlobalSettings();
	String fileSyncAppUseGroupSettings();
	String fileSyncAppUseUserSettings();
	
	// Strings used in the Manage Database Logs dialog.
	String databasePruneDlgHeader_Filr();
	String databasePruneDlgHeader_Vibe();
	String databasePruneDlgHeader1();
	String databasePruneDlgHeader2_Filr();
	String databasePruneDlgHeader2_Vibe();
	String databasePruneDlgHeader3();
	String databasePruneDlgRemoveAuditTrailEntries();
	String databasePruneDlgRemoveChangeLogEntries();
	String databasePruneDlgAgeUnits();
	String databasePruneDlgEnableAuditTrail();
	String databasePruneDlgEnableChangeLog();
	String databasePruneDlgEnableFileArchiving();
	String databasePruneDlgCautionAuditTrail();
	String databasePruneDlgCautionChangeLog();
	String databasePruneDlgCautionIrrevocable();
	String databasePruneDlg_OnSaveUnknownException(String desc);
	String databasePruneDlgCautionFileArchiving_Both();
	String databasePruneDlgCautionFileArchiving_Filr();
	String databasePruneDlgCautionFileArchiving_Vibe();
	String databasePruneDlg_Error_AuditLogInvalid();
	String databasePruneDlg_Error_AuditLogTooSmall( int pruneAge);
	String databasePruneDlg_Error_ChangeLogInvalid();
	String databasePruneDlg_Error_ChangeLogTooSmall(int pruneAge);
	
	// Strings used in the Configure User File Sync Application dialog.
	String configureUserFileSyncAppDlgErrorHeader();
	String configureUserFileSyncAppDlgHeaderGroups(String numGroups);
	String configureUserFileSyncAppDlgHeaderUsers( String numUsers );
	String configureUserFileSyncAppDlgOnSaveUnknownException(String desc);
	String configureUserFileSyncDlgSaving(String completed, String total);

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
	String vibeEntryMenu_CopyFilters();
	String vibeEntryMenu_GlobalizeFilter(String filterName);
	String vibeEntryMenu_ManageFilters();
	String vibeEntryMenu_ManageUsers_EnabledFilter_Hide();
	String vibeEntryMenu_ManageUsers_EnabledFilter_Show();
	String vibeEntryMenu_ManageUsers_ExternalFilter_Hide();
	String vibeEntryMenu_ManageUsers_ExternalFilter_Show();
	String vibeEntryMenu_ManageUsers_DisabledFilter_Hide();
	String vibeEntryMenu_ManageUsers_DisabledFilter_Show();
	String vibeEntryMenu_ManageUsers_InternalFilter_Hide();
	String vibeEntryMenu_ManageUsers_InternalFilter_Show();
	String vibeEntryMenu_ManageUsers_SiteAdminsFilter_Hide();
	String vibeEntryMenu_ManageUsers_SiteAdminsFilter_Show();
	String vibeEntryMenu_ManageUsers_NonSiteAdminsFilter_Hide();
	String vibeEntryMenu_ManageUsers_NonSiteAdminsFilter_Show();
	String vibeEntryMenu_ManageUsers_Warning_NoUsers1();
	String vibeEntryMenu_ManageUsers_Warning_NoUsers2(); 
	String vibeEntryMenu_ManageUsers_Warning_NoUsers3(); 
	String vibeEntryMenu_PersonalizeFilter(String filterName);
	String vibeEntryMenu_SharedView_HiddenFilter();
	String vibeEntryMenu_SharedView_NonHiddenFilter();
	String vibeEntryMenu_SharedView_Warning_NoShares();
	String vibeEntryMenu_Warning_FoldersIgnored();
	String vibeEntryMenu_Warning_OnlyFolders_Entries();
	String vibeEntryMenu_Warning_OnlyFolders_Files();
		
	// Strings used in the Folder Columns dialog.
	String folderColumnsDlgColumn();
	String folderColumnsDlgCustomLabel();
	String folderColumnsDlgHeader();
	String folderColumnsDlgMoveBottom();
	String folderColumnsDlgMoveDown();
	String folderColumnsDlgMoveTop();
	String folderColumnsDlgMoveUp();
	String folderColumnsDlgNoOptions();
	String folderColumnsDlgOrder();
	String folderColumnsDlgRestoreDefaults();
	String folderColumnsDlgSetAsDefault();
	String folderColumnsDlgShow();
	
	// Strings used by various widgets of the Vibe Data table.
	String vibeDataTable_Administrators();
	String vibeDataTable_Alt_CancelWipe();
	String vibeDataTable_Alt_CollapseDescription();
	String vibeDataTable_Alt_Comments();
	String vibeDataTable_Alt_EntryActions();
	String vibeDataTable_Alt_ExpandDescription();
	String vibeDataTable_Alt_ExternalUser_Guest();
	String vibeDataTable_Alt_ExternalUser_LDAP();
	String vibeDataTable_Alt_ExternalUser_Others();
	String vibeDataTable_Alt_InternalUser_LDAP();
	String vibeDataTable_Alt_InternalUser_LDAPAdmin();
	String vibeDataTable_Alt_InternalUser_PersonAdmin();
	String vibeDataTable_Alt_InternalUser_PersonOthers();
	String vibeDataTable_Alt_InternalUser_PersonOthersAdmin();
	String vibeDataTable_Alt_InternalUser_System();
	String vibeDataTable_Alt_InternalUser_SystemAdmin();
	String vibeDataTable_Alt_Ldap_ExternalGroup();
	String vibeDataTable_Alt_Ldap_Group();
	String vibeDataTable_Alt_Ldap_GroupAdmin();
	String vibeDataTable_Alt_Local_ExternalGroup();
	String vibeDataTable_Alt_Local_Group();
	String vibeDataTable_Alt_Local_GroupAdmin();
	String vibeDataTable_Alt_MobileDevices();
	String vibeDataTable_Alt_MobileDevices_None();
	String vibeDataTable_Alt_UnknownGroupType();
	String vibeDataTable_Alt_UnknownUser();
	String vibeDataTable_Alt_Unread();
	String vibeDataTable_Alt_PinEntry();
	String vibeDataTable_Alt_PinHeader();
	String vibeDataTable_Alt_PinHeader_PinAll();
	String vibeDataTable_Alt_PinHeader_UnpinAll();
	String vibeDataTable_Alt_ScheduleWipe();
	String vibeDataTable_Alt_StarGold();
	String vibeDataTable_Alt_StarGray();
	String vibeDataTable_Alt_System_Group();
	String vibeDataTable_Alt_System_GroupAdmin();
	String vibeDataTable_Alt_UnpinEntry();
	String vibeDataTable_Alt_View();
	String vibeDataTable_ColumnResizer();
	String vibeDataTable_Confirm_CantCopyPublicLink_1a();
	String vibeDataTable_Confirm_CantCopyPublicLink_1b();
	String vibeDataTable_Confirm_CantCopyPublicLink_1c();
	String vibeDataTable_Confirm_CantCopyPublicLink_2a();
	String vibeDataTable_Confirm_CantCopyPublicLink_2b();
	String vibeDataTable_Confirm_CantCopyPublicLink_2c();
	String vibeDataTable_Confirm_CantCopyPublicLink_3();
	String vibeDataTable_Confirm_CantEditPublicLink_1a();
	String vibeDataTable_Confirm_CantEditPublicLink_1b();
	String vibeDataTable_Confirm_CantEditPublicLink_1c();
	String vibeDataTable_Confirm_CantEditPublicLink_2a();
	String vibeDataTable_Confirm_CantEditPublicLink_2b();
	String vibeDataTable_Confirm_CantEditPublicLink_2c();
	String vibeDataTable_Confirm_CantEditPublicLink_3();
	String vibeDataTable_Confirm_CantEmailPublicLink_1a();
	String vibeDataTable_Confirm_CantEmailPublicLink_1b();
	String vibeDataTable_Confirm_CantEmailPublicLink_1c();
	String vibeDataTable_Confirm_CantEmailPublicLink_2a();
	String vibeDataTable_Confirm_CantEmailPublicLink_2b();
	String vibeDataTable_Confirm_CantEmailPublicLink_2c();
	String vibeDataTable_Confirm_CantEmailPublicLink_3();
	String vibeDataTable_Confirm_CantShareNetFolders();
	String vibeDataTable_Confirm_CantShareNoRights();
	String vibeDataTable_Confirm_CantShareNoRightsAndNetFolders();
	String vibeDataTable_Confirm_CantSubscribe();
	String vibeDataTable_Download();
	String vibeDataTable_EmailTemplates();
	String vibeDataTable_Error_ForcingPasswordChange();
	String vibeDataTable_Error_GetFolderRows();
	String vibeDataTable_Error_MirroredDriverNotConfigured();
	String vibeDataTable_Error_SavingAdminRights();
	String vibeDataTable_Error_SavingLimitedUserVisibility();
	String vibeDataTable_Empty();
	String vibeDataTable_Empty_Pinning();
	String vibeDataTable_Event_AllDay();
	String vibeDataTable_Event_End();
	String vibeDataTable_Event_Duration(String days);
	String vibeDataTable_Event_Start();
	String vibeDataTable_Globals();
	String vibeDataTable_GuestbookInternalErrorOverrideMissing();
	String vibeDataTable_InternalError_NestedCloudFolderAuthentication();
	String vibeDataTable_InternalError_UnexpectedRowCount(int rowsRequested, int rowsRead);
	String vibeDataTable_InternalError_UnsupportedStructuredToolbar();
	String vibeDataTable_LimitedUserVisibility();
	String vibeDataTable_LimitedUserVisibility_Alt();
	String vibeDataTable_MemberCount(String count);
	String vibeDataTable_MobileDevices();
	String vibeDataTable_People();
	String vibeDataTable_Pin();
	String vibeDataTable_ProxyIdentities();
	String vibeDataTable_ProxyIdentity_Alt();
	String vibeDataTable_Select();
	String vibeDataTable_Teams();
	String vibeDataTable_TrashConfirmPurge();
	String vibeDataTable_TrashConfirmPurgeAll();
	String vibeDataTable_TrashConfirmPurgeAllWithSelections();
	String vibeDataTable_TrashConfirmRestoreAllWithSelections();
	String vibeDataTable_TrashInternalErrorOverrideMissing(String methodName);
	String vibeDataTable_View();
	String vibeDataTable_ViewTrash();
	String vibeDataTable_Warning_CantCopyPublicLink_1a();
	String vibeDataTable_Warning_CantCopyPublicLink_1b();
	String vibeDataTable_Warning_CantCopyPublicLink_1c();
	String vibeDataTable_Warning_CantCopyPublicLink_2a();
	String vibeDataTable_Warning_CantCopyPublicLink_2b();
	String vibeDataTable_Warning_CantCopyPublicLink_2c();
	String vibeDataTable_Warning_CantCopyPublicLink_3();
	String vibeDataTable_Warning_CantEditPublicLink_1a();
	String vibeDataTable_Warning_CantEditPublicLink_1b();
	String vibeDataTable_Warning_CantEditPublicLink_1c();
	String vibeDataTable_Warning_CantEditPublicLink_2a();
	String vibeDataTable_Warning_CantEditPublicLink_2b();
	String vibeDataTable_Warning_CantEditPublicLink_2c();
	String vibeDataTable_Warning_CantEditPublicLink_3();
	String vibeDataTable_Warning_CantEmailPublicLink_1a();
	String vibeDataTable_Warning_CantEmailPublicLink_1b();
	String vibeDataTable_Warning_CantEmailPublicLink_1c();
	String vibeDataTable_Warning_CantEmailPublicLink_2a();
	String vibeDataTable_Warning_CantEmailPublicLink_2b();
	String vibeDataTable_Warning_CantEmailPublicLink_2c();
	String vibeDataTable_Warning_CantEmailPublicLink_3();
	String vibeDataTable_Warning_CantSubscribe();
	String vibeDataTable_Warning_NoEntryActions();
	String vibeDataTable_Warning_ShareNetFolders();
	String vibeDataTable_Warning_ShareNoRights();
	String vibeDataTable_Warning_ShareNoRightsAndNetFolders();
	String vibeDataTable_WhatsNew();
	
	// Strings used by the Vibe simple pager widget.
	String vibeSimplePager_Of(             String start, String end, String size);
	String vibeSimplePager_OfApproximately(String start, String end, String size);
	String vibeSimplePager_OfAtLeast(      String start, String end, String size);
	String vibeSimplePager_OfOver(         String start, String end, String size);
	
	// String used in the Landing Page.
	String nowFollowingBinder();
	String utilityElementAdminPage();
	String utilityElementFollowWorkspace();
	String utilityElementLogIn();
	String utilityElementMyWorkspace();
	String utilityElementShareWorkspace();

	// Strings used in the TaskFolderWidget.
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
	String addFilesHtml5PopupBusyPre();
	String addFilesHtml5PopupBusyPost(int thisOne, int total);
	String addFilesHtml5PopupClose();
	String addFilesHtml5PopupCloseAlt();
	String addFilesHtml5PopupDnDHintProd(String product);
	String addFilesHtml5PopupFoldersSkipped(String folderNames);
	String addFilesHtml5PopupHint();
	String addFilesHtml5PopupProgressItem();
	String addFilesHtml5PopupProgressTotal();
	String addFilesHtml5PopupReadError(String fileName, String errorDesc);
	String addFilesHtml5PopupUploadValidationError();
	String addFilesHtml5PopupValidating();

	// Strings used by Html5FileUploadClientHelper.java.
	String html5Uploader_InternalError_NoBrowserSupport();
	String html5Uploader_InternalError_UploaderAlreadyActive();
	String html5Uploader_Warning_NoFiles();
	String html5Uploader_Warning_NoFilesIE();
	
	// Strings used in the 'Child binders widget'.
	String workspacesHeader();
	String unreadEntries(Long numUnread);

	// Strings used in the 'Unread entries' dialog.
	String unreadEntriesDlgHeader();
	
	// Strings used in the CloudFolderAuthenticationDlg.
	String cloudFolderAuthenticationDlgHeader();
	String cloudFolderAuthenticationDlgMessageAboveLogo();
	String cloudFolderAuthenticationDlgMessageBelowLogo();
	
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
	
	// Strings used in the CopyMoveEntriesDlg in all modes.
	String cmeDlg_Alt_Browse();

	// Strings used by the operations against selected entries.
	String deleteFolderEntriesError();
	String deleteTasksError();
	String purgeTasksError();
	
	// Strings used by the operations against selected users.
	String enableUsersError();
	String deleteSelectedUsersError();
	String disableUsersError();
	
	// Strings used by the lock selected entries facility.
	String lockEntriesError();

	// Strings used by the unlock selected entries facility.
	String unlockEntriesError();

	// Strings used by the zip and download selected files facility.
	String zipDownloadUrlError();
	
	// Strings used by the download a folder as a CSV file facility.
	String downloadFolderAsCSVFileUrlError();
	
	// String used within BinderViewsHelper.
	String binderViewsHelper_download();
	String binderViewsHelper_failureMailToPublicLink(String cause);
	String binderViewsHelper_failureSettingAdHocFolders();
	String binderViewsHelper_failureSettingDownload();
	String binderViewsHelper_failureSettingPublicCollection();
	String binderViewsHelper_failureSettingWebAccess();
	String binderViewsHelper_internalErrorEmailTemplatesWithoutHtml5();
	String binderViewsHelper_view();
	
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
	String multiErrorAlertDlgHeaderConfirm();
	String multiErrorAlertDlgHeaderError();
	
	// Strings used in the Project Information widget.
	String dueDateLabel();
	String managerLabel();
	String projectInformation();
	String projectStatusCancelled();
	String projectStatusClosed();
	String projectStatusOpen();
	String projectStatusUnknown();
	String statusLabel();

	// Strings used in the Project Statistics widget.
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
	String milestoneGraphs_StatusCompleted(String percent, String count);
	String milestoneGraphs_StatusOpen(     String percent, String count);
	String milestoneGraphs_StatusReopened( String percent, String count);
	
	// Strings used by the guest book folder.
	String guestBook_Error_CouldNotGetSigningURL();
	String guestBook_GotoProfile();
	
	// Strings used in the 'My Tasks' widget.
	String myTasksHeader();	
	
	// Strings used in the 'Binder View Helper' APIs.
	String binderViewsConfirmDeleteEntries();
	String binderViewsConfirmDeleteEntry();
	String binderViewsConfirmDeleteFolder();
	String binderViewsConfirmDeleteWorkspace();
	String binderViewsConfirmDeleteUserWS();
	String binderViewsDeleteSelectedUsersCaption();
	String binderViewsDeleteSelectedUsersProgress();
	String binderViewsDeleteSelectionsCaption();
	String binderViewsDeleteSelectionsProgress();
	String binderViewsDeleteTasksCaption();
	String binderViewsDeleteTasksProgress();
	String binderViewsPurgeTasksCaption();
	String binderViewsPurgeTasksProgress();
	
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
	String calendarView_Hint_PhysicalByActivity();
	String calendarView_Hint_PhysicalByCreation();
	String calendarView_Hint_PhysicalEvents();
	String calendarView_Hint_Virtual();
	String calendarView_Error_CantAdd();
	String calendarView_Error_CantClickCreateWhenViewByDate();
	String calendarView_Error_CantModify();
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

	// Strings used in the 'Edit graphic properties' dialog.
	String editGraphicPropertiesDlgSetImageSize();

	// Strings used in the Manage Comments composite.
	String manageCommentsCompositeSend();
	String manageCommentsCompositeWhoHasAccess();
	
	// Strings used in the Manage Comments dialog.
	String manageCommentsDlgComments(int cCount);
	
	// Strings used in the Manage Groups dialog.
	String manageGroupsDlgAdminCol();
	String manageGroupsDlgAdminRightsClear();
	String manageGroupsDlgAdminRightsSet();
	String manageGroupsDlgConfirmDelete(String groupNames);
	String manageGroupsDlgCreatingGroup();
	String manageGroupsDlgDeleteGroupLabel();
	String manageGroupsDlgDeletingGroup();
	String manageGroupsDlgDesktopAppSettings();
	String manageGroupsDlgDownload_Clear();
	String manageGroupsDlgDownload_Disable();
	String manageGroupsDlgDownload_Enable();
	String manageGroupsDlg_Error_SavingAdminRights();
	String manageGroupsDlgEditGroupLabel();
	String manageGroupsDlgHeader();
	String manageGroupsDlgMobileAppSettings();
	String manageGroupsDlgModifyingGroup();
	String manageGroupsDlgMoreLabel();
	String manageGroupsDlgNameCol();
	String manageGroupsDlgNewGroupLabel();
	String manageGroupsDlgNoGroupsLabel();
	String manageGroupsDlgPersonalStorage_Clear();
	String manageGroupsDlgPersonalStorage_Disable();
	String manageGroupsDlgPersonalStorage_Enable();
	String manageGroupsDlgSelectGroupToDelete();
	String manageGroupsDlgSelectGroupsToModify();
	String manageGroupsDlgSelect1GroupToEdit();
	String manageGroupsDlgTitleCol();
	String manageGroupsDlgTypeCol();
	String manageGroupsDlgUnknownStatus();
	String manageGroupsDlgUpdatingMembership();
	String manageGroupsDlgWebAccess_Clear();
	String manageGroupsDlgWebAccess_Disable();
	String manageGroupsDlgWebAccess_Enable();
	String manageGroupsDlgGroup();
	
	// Strings used in the Modify Group dialog.
	String addGroupDlgHeader();
	String modifyGroupDlgCreatingGroup();
	String modifyGroupDlgEditGroupMembershipLabel();
	String modifyGroupDlgErrorCreatingGroup(String desc);
	String modifyGroupDlgDescriptionLabel();
	String modifyGroupDlgDynamicLabel();
	String modifyGroupDlgDynamicGroupMembershipNotAllowed();
	String modifyGroupDlgGroupAlreadyExists();
	String modifyGroupDlgHeader(String groupTitle);
	String modifyGroupDlgNameLabel();
	String modifyGroupDlgNameRequired();
	String modifyGroupDlgNameTooLong();
	String modifyGroupDlgStaticLabel();
	String modifyGroupDlgTitleLabel();
	String modifyGroupDlgUserAlreadyExists();
	String modifyGroupDlgViewGroupMembershipLabel();

	// Strings used in the Modify static membership dialog.
	String modifyStaticMembershipDlgDeleteLabel();
	String modifyStaticMembershipDlgExternalAllowedLabel();
	String modifyStaticMembershipDlgGroupLabel();
	String modifyStaticMembershipDlgGroupTab();
	String modifyStaticMembershipDlgHeader(String groupTitle);
	String modifyStaticMembershipDlgNameCol();
	String modifyStaticMembershipDlgNoGroupsLabel();
	String modifyStaticMembershipDlgNoUsersLabel();
	String modifyStaticMembershipDlgSelectGroupToRemove();
	String modifyStaticMembershipDlgSelectUserToRemove();
	String modifyStaticMembershipDlgUserLabel();
	String modifyStaticMembershipDlgUserTab();

	// Strings used in the Modify dynamic membership dialog.
	String modifyDynamicMembershipDlgBaseDnAlt();
	String modifyDynamicMembershipDlgBaseDnLabel();
	String modifyDynamicMembershipDlgCurrentMembershipLabel(int count);
	String modifyDynamicMembershipDlgCurrentMembershipCalculatingLabel();
	String modifyDynamicMembershipDlgHeader();
	String modifyDynamicMembershipDlgLdapFilterLabel();
	String modifyDynamicMembershipDlgSearchSubtreeLabel();
	String modifyDynamicMembershipDlgTestQueryInProgressLabel();
	String modifyDynamicMembershipDlgTestQueryLabel();
	String modifyDynamicMembershipDlgTestQueryResults(int count);
	String modifyDynamicMembershipDlgUpdateLabel();

	// Strings used in the Show dynamic membership dialog.
	String showDynamicMembershipDlgGroupTab();
	String showDynamicMembershipDlgHeader();
	String showDynamicMembershipDlgUserTab();
	
	// String used in the blog archive control.
	String blogArchiveTitle();
	
	// Strings used in the blog global tags control.
	String blogGlobalTagsTitle();
	
	// Strings used in the blog page control.
	String blogPageCtrl_newPageLabel();
	String blogPageCtrl_selectPageLabel();

	// Strings used in the 'Add blog page' dialog.
	String createBlogPageDlg_caption();
	String createBlogPageDlg_createFailed();
	String createBlogPageDlg_newPageNameLabel();
	String createBlogPageDlg_noNameSpecified();

	// Strings used with Filr.
	String administratorsList();
	String folders();
	String globalsList();
	String limitUserVisibilityList();
	String mobileDevicesList();
	String myFiles();
	String myFilesStorage();
	String netFolders();
	String peopleList();
	String proxyIdentitiesList();
	String sharedByMe();
	String sharedWithMe();
	String sharedPublic();
	String teamsList();

	// Strings used with the 'Share send-to' widget.
	String shareSendToWidget_AllRecipients();
	String shareSendToWidget_NoOne();
	String shareSendToWidget_OnlyModifiedRecipients();
	String shareSendToWidget_OnlyNewRecipients();
	String shareSendToWidget_SelectedRecipients();
	String shareSendToWidget_Unknown();
	
	// Strings used in the manage Net Folder Servers dialog.
	String manageNetFolderServersDlg_AddNetFolderServerLabel();
	String manageNetFolderServersDlg_ConfirmDelete(       String netFolderServerNames);
	String manageNetFolderServersDlg_CouldNotDeletePrompt(String netFolderServerNames);
	String manageNetFolderServersDlg_DeleteNetFolderServerLabel();
	String manageNetFolderServersDlg_Header();
	String manageNetFolderServersDlg_NameCol();
	String manageNetFolderServersDlg_NoNetFolderServersLabel();
	String manageNetFolderServersDlg_OesWarning();
	String manageNetFolderServersDlg_ServerPathCol();
	String manageNetFolderServersDlg_SelectServersToDelete();
	String manageNetFolderServersDlg_SelectServersToSync();
	String manageNetFolderServersDlg_SyncLabel();
	String manageNetFolderServersDlg_Syncing();
	String manageNetFolderServersDlg_SyncFailure();
	String manageNetFolderServersDlg_UnknownStatus();

	// Strings used in the modify Net Folder Server dialog.
	String modifyNetFolderServerDlg_AddHeader();
	String modifyNetFolderServerDlg_AllowDesktopAppToTriggerSync();
	String modifyNetFolderServerDlg_AllowSelfSignedCertsLabel();
	String modifyNetFolderServerDlg_AuthenticationTab();
	String modifyNetFolderServerDlg_AuthTypeLabel();
	String modifyNetFolderServerDlg_AuthType_Kerberos();
	String modifyNetFolderServerDlg_AuthType_KerberosThenNtlm();
	String modifyNetFolderServerDlg_AuthType_NMAS();
	String modifyNetFolderServerDlg_AuthType_Ntlm();
	String modifyNetFolderServerDlg_ConfigTab();
	String modifyNetFolderServerDlg_CreatingNetFolderServer();
	String modifyNetFolderServerDlg_EditHeader(String name);
	String modifyNetFolderServerDlg_EnableSyncScheduleLabel();
	String modifyNetFolderServerDlg_EnterProxyNamePrompt();
	String modifyNetFolderServerDlg_EnterProxyPwdPrompt();
	String modifyNetFolderServerDlg_ErrorCreatingNetFolderServer( String err);
	String modifyNetFolderServerDlg_ErrorInvalidSearchResult();
	String modifyNetFolderServerDlg_ErrorModifyingNetFolderServer(String err);
	String modifyNetFolderServerDlg_Group();
	String modifyNetFolderServerDlg_HostUrlLabel();
	String modifyNetFolderServerDlg_HostUrlRequired();
	String modifyNetFolderServerDlg_IndexContentCB();
	String modifyNetFolderServerDlg_InsufficientRights();
	String modifyNetFolderServerDlg_IsSharePointServerLabel();
	String modifyNetFolderServerDlg_Minutes();
	String modifyNetFolderServerDlg_ModifyingNetFolderServer();
	String modifyNetFolderServerDlg_NameLabel();
	String modifyNetFolderServerDlg_NameRequired();
	String modifyNetFolderServerDlg_NoLdapServers();
	String modifyNetFolderServerDlg_NotFullyConfigured();
	String modifyNetFolderServerDlg_PrivilegedPrincipalsHint();
	String modifyNetFolderServerDlg_ProxyIdentityLabel();
	String modifyNetFolderServerDlg_ProxyName_Alt();
	String modifyNetFolderServerDlg_ProxyNameHint1();
	String modifyNetFolderServerDlg_ProxyNameHint2();
	String modifyNetFolderServerDlg_ProxyNameHint3();
	String modifyNetFolderServerDlg_ProxyNameLabel();
	String modifyNetFolderServerDlg_ProxyPwdLabel();
	String modifyNetFolderServerDlg_ProxyTypeIdentity();
	String modifyNetFolderServerDlg_ProxyTypeManual();
	String modifyNetFolderServerDlg_RefreshRightsLabel();
	String modifyNetFolderServerDlg_ScheduleTab();
	String modifyNetFolderServerDlg_SelectServerTypePrompt();
	String modifyNetFolderServerDlg_SelectProxyIdentityPrompt();
	String modifyNetFolderServerDlg_ServerAlreadyExists();
	String modifyNetFolderServerDlg_ServerPathCleaned(String page);
	String modifyNetFolderServerDlg_ServerPathHint1();
	String modifyNetFolderServerDlg_ServerPathOESHint();
	String modifyNetFolderServerDlg_ServerPathWindowsHint();
	String modifyNetFolderServerDlg_ServerPathLabel();
	String modifyNetFolderServerDlg_SharePointPathHint();
	String modifyNetFolderServerDlg_SyncAllNetFoldersPrompt();
	String modifyNetFolderServerDlg_SyncOfNetFolderServerStarted();
	String modifyNetFolderServerDlg_SyncOnlyDirStructureCB();
	String modifyNetFolderServerDlg_SyncScheduleCaption();
	String modifyNetFolderServerDlg_SyncTab();
	String modifyNetFolderServerDlg_TestConnectionLabel();
	String modifyNetFolderServerDlg_TypeLabel();
	String modifyNetFolderServerDlg_Type_Famt();
	String modifyNetFolderServerDlg_Type_FileSystem();
	String modifyNetFolderServerDlg_Type_Netware();
	String modifyNetFolderServerDlg_Type_OES();
	String modifyNetFolderServerDlg_Type_OES2015();
	String modifyNetFolderServerDlg_Type_SharePoint2010();
	String modifyNetFolderServerDlg_Type_SharePoint2013();
	String modifyNetFolderServerDlg_Type_Undefined();
	String modifyNetFolderServerDlg_Type_WebDav();
	String modifyNetFolderServerDlg_Type_Windows();
	String modifyNetFolderServerDlg_UseDirectoryRightsCB();
	String modifyNetFolderServerDlg_User();
	
	// Strings used in the Manage Net Folders dialog.
	String manageNetFoldersDlg_AddNetFolderLabel();
	String manageNetFoldersDlg_CancelSyncRequested();
	String manageNetFoldersDlg_ConfirmDelete(String netFolderName);
	String manageNetFoldersDlg_DeleteFailed();
	String manageNetFoldersDlg_DeleteInProgress();
	String manageNetFoldersDlg_DeleteNetFolderErrorMsg(String netFolderName, String errorMsg);
	String manageNetFoldersDlg_DeleteNetFolderLabel();
	String manageNetFoldersDlg_FilterOptionsAlt();
	String manageNetFoldersDlg_Header();
	String manageNetFoldersDlg_nNetFoldersToDelete(int count);
	String manageNetFoldersDlg_NameCol();
	String manageNetFoldersDlg_NoNetFoldersLabel();
	String manageNetFoldersDlg_PromptForSync();
	String manageNetFoldersDlg_RelativePathCol();
	String manageNetFoldersDlg_SearchingForNetFolders();
	String manageNetFoldersDlg_ServerCol();
	String manageNetFoldersDlg_SelectFoldersToDelete();
	String manageNetFoldersDlg_SelectFoldersToStopSync();
	String manageNetFoldersDlg_SelectFoldersToSync();
	String manageNetFoldersDlg_ShowHomeDirsLabel();
	String manageNetFoldersDlg_StopSyncLabel();
	String manageNetFoldersDlg_SyncLabel();
	String manageNetFoldersDlg_Syncing();
	String manageNetFoldersDlg_SyncStatusCol();
	String manageNetFoldersDlg_UnknownStatus();
	
	// Net Folder sync status.
	String netFolderSyncStatusCanceled();
	String netFolderSyncStatusCompleted();
	String netFolderSyncStatusInProgress();
	String netFolderSyncStatusNeverRun();
	String netFolderSyncStatusStopped();
	String netFolderSyncStatusUnknown();
	String netFolderSyncStatusWaitingToBeSyncd();

	// Strings used in the Modify Net Folder dialog.
	String modifyNetFolderDlg_AddHeader();
	String modifyNetFolderDlg_AllowDataSyncBy();
	String modifyNetFolderDlg_AllowDesktopAppToSyncLabel();
	String modifyNetFolderDlg_AllowDesktopAppTriggerSync();
	String modifyNetFolderDlg_AllowMobileAppsToSyncLabel();
	String modifyNetFolderDlg_ConfigTab();
	String modifyNetFolderDlg_CreateNetFolderServerLabel();
	String modifyNetFolderDlg_CreatingNetFolder();
	String modifyNetFolderDlg_DataSyncTab();
	String modifyNetFolderDlg_EditHeader(String name);
	String modifyNetFolderDlg_EnableJitsLabel();
	String modifyNetFolderDlg_EnableSyncScheduleLabel();
	String modifyNetFolderDlg_ErrorCreatingNetFolder( String err);
	String modifyNetFolderDlg_ErrorModifyingNetFolder(String err);
	String modifyNetFolderDlg_ForwardSlashNotPermittedInRelativePath();
	String modifyNetFolderDlg_FullSyncDirOnlyCB();
	String modifyNetFolderDlg_IndexContentLabel();
	String modifyNetFolderDlg_InsufficientRights();
	String modifyNetFolderDlg_JitsAclMaxAgeLabel();
	String modifyNetFolderDlg_JitsResultsMaxAgeLabel();
	String modifyNetFolderDlg_ModifyingNetFolder();
	String modifyNetFolderDlg_NameLabel();
	String modifyNetFolderDlg_NameRequired();
	String modifyNetFolderDlg_NetFolderAlreadyExists();
	String modifyNetFolderDlg_NetFolderServerLabel();
	String modifyNetFolderDlg_NoNetFolderServersLabel();
	String modifyNetFolderDlg_NoNetFolderServersPrompt();
	String modifyNetFolderDlg_ParentBinderRequired();
	String modifyNetFolderDlg_ParentFolderLabel();
	String modifyNetFolderDlg_PleaseEnterRelativePath();
	String modifyNetFolderDlg_PleaseSelectNetFolderServer();
	String modifyNetFolderDlg_RelativePathLabel();
	String modifyNetFolderDlg_RightsTab();
	String modifyNetFolderDlg_SelectPrincipalsHint();
	String modifyNetFolderDlg_ScheduleTab();
	String modifyNetFolderDlg_SyncScheduleCaption();
	String modifyNetFolderDlg_UseJistsSettingsFromNetFolderRbLabel();
	String modifyNetFolderDlg_UseJitsSettingsFromNetFolderServerRbLabel();
	String modifyNetFolderDlg_UseNetFolderDesktopAppTriggerSync();
	String modifyNetFolderDlg_UseNetFolderIndexContentOptionRbLabel();
	String modifyNetFolderDlg_UseNetFolderScheduleRbLabel();
	String modifyNetFolderDlg_UseNetFolderServerDesktopAppTriggerSync();
	String modifyNetFolderDlg_UseNetFolderServerIndexContentOptionRbLabel();
	String modifyNetFolderDlg_UseNetFolderServerScheduleRbLabel();
	String modifyNetFolderDlg_UseNetFolderServerSyncOptionRbLabel();
	String modifyNetFolderDlg_UseNetFolderSyncOptionRbLabel();
	
	// Strings used in the Schedule widget.
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

	// Strings used in the Edit Share Note dialog.
	String editShareNoteDlg_caption();
	String editShareNoteDlg_noteLabel();

	// Strings used in the Edit Share Rights dialog.
	String editShareRightsDlg_CanShareExternalLabel();
	String editShareRightsDlg_CanShareInternalLabel();
	String editShareRightsDlg_CanShareLabel();
	String editShareRightsDlg_CanSharePublicLabel();
	String editShareRightsDlg_CanSharePublicLinkLabel();
	String editShareRightsDlg_caption();
	String editShareRightsDlg_ContributorLabel();
	String editShareRightsDlg_EditorLabel();
	String editShareRightsDlg_GrantRightsLabel();
	String editShareRightsDlg_ViewerLabel();
	String editShareRightsDlg_UnavailableMessage();
	String editShareRightsDlg_UnavailableTextMessage();

	// Strings used in the Edit Share dialog.
	String editShareDlg_accessRightsLabel();
	String editShareDlg_captionEdit1(String recipientName);
	String editShareDlg_captionEditMultiple(int numShares);
	String editShareDlg_canReshareExternalLabel();
	String editShareDlg_canReshareInternalLabel();
	String editShareDlg_canResharePublicLabel();
	String editShareDlg_canResharePublicLinkLabel();
	String editShareDlg_filrLinkDesc();
	String editShareDlg_leaveUnchanged();
	String editShareDlg_no();
	String editShareDlg_publicLinkDesc();
	String editShareDlg_undefinedNote();
	String editShareDlg_yes();

	// Strings used in the User Actions Popup.
	String userActionsPanel_ChangePassword();
	String userActionsPanel_PersonalPreferences();
	String userActionsPanel_ViewProfile();
	String userActionsPanel_ViewSharedByMe();

	// Strings used when testing a net folder and net folder server
	// connection.
	String testConnection_FailedError();
	String testConnection_InProgressLabel();
	String testConnection_NetworkError();
	String testConnection_Normal();
	String testConnection_ProxyCredentialsError();
	String testConnection_UnknownStatus();

	// Strings used in the Configure User Access dialog.
	String configureUserAccessDlg_Header();
	String configureUserAccessDlg_AllowGuestAccessLabel();
	String configureUserAccessDlg_AllowGuestReadOnlyLabel();
	String configureUserAccessDlg_AllowSelfRegInternalUserAccountLabel();
	String configureUserAccessDlg_AllowExternalUserAccessLabel();
	String configureUserAccessDlg_AllowSelfRegExternalUserAccountLabel();
	String configureUserAccessDlg_DisableDownloadLabel();
	String configureUserAccessDlg_DisableWebAccessLabel();
	
	// Strings used in the Configure Password Policy dialog.
	String configurePasswordPolicyDlg_ConfirmForcePasswordChanges();
	String configurePasswordPolicyDlg_Header();
	String configurePasswordPolicyDlg_Hint();
	String configurePasswordPolicyDlg_Hint_AtLeast3();
	String configurePasswordPolicyDlg_Hint_Expiration(int expirationDays);
	String configurePasswordPolicyDlg_Hint_Lower();
	String configurePasswordPolicyDlg_Hint_MinimumLength(int minimumLength);
	String configurePasswordPolicyDlg_Hint_NoName();
	String configurePasswordPolicyDlg_Hint_Number();
	String configurePasswordPolicyDlg_Hint_Symbol(String symbols);
	String configurePasswordPolicyDlg_Hint_Upper();
	String configurePasswordPolicyDlg_EnablePasswordComplexityChecking();
	
	// Strings used in the Configure Update Logs dialog.
	String configureUpdateLogsDlg_AutoUpdateLogs();
	String configureUpdateLogsDlg_Header();
	String configureUpdateLogsDlg_Hint();
	
	// Strings used by the GWT based Folder Entry viewer.
	String folderEntry_Alt_Close();
	String folderEntry_Alt_EntryLockedBy(String user);
	String folderEntry_Alt_FileLockedBy(String user);
	String folderEntry_Alt_Hide();
	String folderEntry_Alt_MarkRead();
	String folderEntry_Alt_Next();
	String folderEntry_Alt_Previous();
	String folderEntry_Close();
	String folderEntry_Comments(int count);
	String folderEntry_Confirm_ForceFileUnlock();
	String folderEntry_Entry();
	String folderEntry_EntryLocked();
	String folderEntry_Error_NoNext();
	String folderEntry_Error_NoPrevious();
	String folderEntry_File();
	String folderEntry_FileLocked();
	String folderEntry_FileSize(String size);
	String folderEntry_Hide();
	String folderEntry_Locked();
	String folderEntry_Modified();
	String folderEntry_NoShares(String what);
	String folderEntry_Permalinks();
	String folderEntry_SharedBy();
	String folderEntry_SharedOn(String date);
	String folderEntry_SharedWith();
	String folderEntry_ShareExpires(String date);
	String folderEntry_ShareInfo();
	String folderEntry_ShareReshare_External();
	String folderEntry_ShareReshare_Internal();
	String folderEntry_ShareReshare_Public();
	String folderEntry_ShareReshares(String reshare);
	String folderEntry_ShareRight_Contributor();
	String folderEntry_ShareRight_Editor();
	String folderEntry_ShareRight_Viewer();
	String folderEntry_ShareRights(String right);
	String folderEntry_ShowDescription();
	String folderEntry_Trashed();
	
	// String used in the Configure adhoc Folders dialog.
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
	String downloadAppDlgAlt_MobileDownloads();
	String downloadAppDlgAlt_WindowsDownloads();
	String downloadAppDlgAppStore_Amazon();
	String downloadAppDlgAppStore_Apple();
	String downloadAppDlgAppStore_Blackberry();
	String downloadAppDlgAppStore_Google();
	String downloadAppDlgAppStore_Samsung();
	String downloadAppDlgAppStore_SamsungKnox();
	String downloadAppDlgAppStore_Windows();
	String downloadAppDlgBody_Downloads();
	String downloadAppDlgBody_Instructions();
	String downloadAppDlgBody_Product();
	String downloadAppDlgBody_Type();
	String downloadAppDlgDownloadAndroid(String company, String product);
	String downloadAppDlgDownloadFilenameUnknown();
	String downloadAppDlgDownloadIOS(String company, String product);
	String downloadAppDlgDownloadMac1(String product, String filename, String quickStart);
	String downloadAppDlgDownloadMac2(String company, String product);
	String downloadAppDlgDownloadMd5(String md5);
	String downloadAppDlgDownloadMobile(String company, String product);
	String downloadAppDlgDownloadWindows2(String company, String product);
	String downloadAppDlgDownloadWindows3(String product, String filename32, String filename64, String filenameXP, String quickStart);
	String downloadAppDlgDownloadWindows4(String product, String filename32, String filename64, String quickStart);
	String downloadAppDlgError_NoMacUrl();
	String downloadAppDlgError_NoUrls();
	String downloadAppDlgError_NoWin32Url();
	String downloadAppDlgError_NoWin64Url();
	String downloadAppDlgError_NoWinXPUrl();
	String downloadAppDlgHeader(String product);
	String downloadAppDlgInstructAndroid1(String type);
	String downloadAppDlgInstructAndroid2();
	String downloadAppDlgInstructAndroid3(String company, String product, String type);
	String downloadAppDlgInstructIOS1(String company, String product, String iPhone, String iPad, String type);
	String downloadAppDlgInstructIOS2();
	String downloadAppDlgInstructIOS3();
	String downloadAppDlgInstructMobile(String product, String companyLC, String productLC, String company);
	String downloadAppDlgMinimumAndroid(String product);
	String downloadAppDlgMinimumIOS(String product);
	String downloadAppDlgMinimumMac(String product);
	String downloadAppDlgMinimumWindows32(String product);
	String downloadAppDlgMinimumWindows64(String product);
	String downloadAppDlgMinimumWindowsXP(String product);
	String downloadAppDlgProductAndroid(String product);
	String downloadAppDlgProductIOS(String product);
	String downloadAppDlgProductMac(String product);
	String downloadAppDlgProductMobile(String product);
	String downloadAppDlgProductWindows(String product);
	String downloadAppDlgSubhead(String product);
	String downloadAppDlgUrlMac();
	String downloadAppDlgUrlWin32();
	String downloadAppDlgUrlWin64();
	String downloadAppDlgUrlWinXP();

	// Strings used in the 'Select Principals' widget.
	String selectPrincipalsWidget_CantSelectExternalUserPrompt();
	String selectPrincipalsWidget_NameCol();
	String selectPrincipalsWidget_NoPrincipalsHint();
	String selectPrincipalsWidget_PrincipalAlreadyInListPrompt(String name);
	String selectPrincipalsWidget_RemovePrincipalHint();
	String selectPrincipalsWidget_RightsCol();
	String selectPrincipalsWidget_SelectPrincipalsLabel();
	String selectPrincipalsWidget_TypeCol();

	// Strings used by the 'Manage Administrators' dialog.
	String manageAdministratorsDlgCaption();
	String manageAdministratorsDlgErrorInvalidSearchResult();
	
	// Strings used by the 'Manage Teams' dialog.
	String manageTeamsDlgCaption();
	
	// Strings used by the 'Share Binder Rights' dialog.
	String shareTeamRightsDlgHeader(     int count);
	String shareWorkspaceRightsDlgHeader(int count);
	
	// Strings used by the 'Manage Users' dialog.
	String manageUsersDlgCaption();
	
	// Strings used by the 'Import Profiles' dialog.
	String importProfilesDlgErrorBogusJSONData(String msg);
	String importProfilesDlgErrorFailed(       String msg);
	String importProfilesDlgErrorNoFile();
	String importProfilesDlgErrorParse(String msg);
	String importProfilesDlgHeader();
	String importProfilesDlgSuccess();
	String importProfilesDlgViewSample();
	
	// Strings used by the 'User Share Rights' dialog.
	String userShareRightsDlgError_NoWorkspace();
	String userShareRightsDlgError_SetFailures();
	String userShareRightsDlgHeader(int count);
	String userShareRightsDlgLabel_Allow();
	String userShareRightsDlgLabel_AllowForwarding();
	String userShareRightsDlgLabel_AllowSharingWith();
	String userShareRightsDlgLabel_Clear();
	String userShareRightsDlgLabel_ExternalUsers();
	String userShareRightsDlgLabel_InternalUsers();
	String userShareRightsDlgLabel_NoChange();
	String userShareRightsDlgLabel_NoZoneSettings();
	String userShareRightsDlgLabel_Public();
	String userShareRightsDlgLabel_PublicLinks();
	String userShareRightsDlgProgress(int done, int total);

	// Strings used by the 'Binder Share Rights' dialog.
	String binderShareRightsDlgError_SetFailures();
	String binderShareRightsDlgHint_AllUsers();
	String binderShareRightsDlgHint_Owner();
	String binderShareRightsDlgHint_TeamMembers();
	String binderShareRightsDlgLabel_Allow();
	String binderShareRightsDlgLabel_AllowForwarding();
	String binderShareRightsDlgLabel_AllowSharingWith();
	String binderShareRightsDlgLabel_Clear();
	String binderShareRightsDlgLabel_ExternalUsers();
	String binderShareRightsDlgLabel_InternalUsers();
	String binderShareRightsDlgLabel_NoChange();
	String binderShareRightsDlgLabel_NoZoneSettings();
	String binderShareRightsDlgLabel_Public();
	String binderShareRightsDlgLabel_PublicLinks();
	String binderShareRightsDlgProgress(int done, int total);

	// Strings used to represent the different rights.
	String allowAccess();
	String externalRights();
	String folderExternalRights();
	String forwardingRights();
	String folderForwardingRights();
	String internalRights();
	String folderInternalRights();
	String noRights();
	String publicRights();
	String folderPublicRights();
	String shareLinkRights();
	String shareWithAllExternalRights();
	String shareWithAllInternalRights();

	// Strings used in the Edit Net Folder Rights dialog.
	String editNetFolderRightsDlg_AllowAccessLabel();
	String editNetFolderRightsDlg_CanShareLabel();
	String editNetFolderRightsDlg_CanShareFolderLabel();
	String editNetFolderRightsDlg_Caption();
	String editNetFolderRightsDlg_ErrorRetrievingZoneShareRights(String error);
	String editNetFolderRightsDlg_Hint();
	String editNetFolderRightsDlg_Instructions();
	String editNetFolderRightsDlg_ReShareLabel();
	String editNetFolderRightsDlg_ReshareFolderLabel();
	String editNetFolderRightsDlg_ShareInternalLabel();
	String editNetFolderRightsDlg_ShareExternalLabel();
	String editNetFolderRightsDlg_ShareLinkLabel();
	String editNetFolderRightsDlg_SharePublicLabel();

	// Strings used for the empty administrators view widget.
	String emptyAdministrators_Info_1(String company, String product);
	String emptyAdministrators_Info_2(String company, String product);
	String emptyAdministrators_SubHead();
	
	// Strings used for the empty collection view widget.
	String emptyCollection_Info_MyFiles(     String product);
	String emptyCollection_Info_SharedByMe_1(String product);
	String emptyCollection_Info_SharedByMe_2();
	String emptyCollection_Info_SharedWithMe(String product);
	String emptyCollection_Info_SharedPublic(String product);
	String emptyCollection_Info_NetFolders_1(String product);
	String emptyCollection_Info_NetFolders_2();
	String emptyCollection_Info_NetFolders_3();
	String emptyCollection_Info_NetFolders_4(String product);
	String emptyCollection_SubHead_MyFiles();
	String emptyCollection_SubHead_SharedByMe();
	String emptyCollection_SubHead_SharedWithMe();
	String emptyCollection_SubHead_SharedPublic();
	String emptyCollection_SubHead_NetFolders();
	
	// Strings used for the empty file folder view widget.
	String emptyFileFolder_Info_1();
	String emptyFileFolder_Info_2(String company, String product);
	String emptyFileFolder_Info_3(                String product);
	String emptyFileFolder_SubHead();
	
	// Strings used for the empty limit user visibility view widget.
	String emptyLimitUserVisibility_Info_1(String company, String product);
	String emptyLimitUserVisibility_Info_2(String company, String product);
	String emptyLimitUserVisibility_SubHead();
	
	// Strings used for the empty mobile devices view widget.
	String emptyMobileDevices_Info_1_System(String company, String product);
	String emptyMobileDevices_Info_1_User(  String company, String product);
	String emptyMobileDevices_Info_2(       String company, String product);
	String emptyMobileDevices_SubHead();
	
	// Strings used for the empty proxy identities view widget.
	String emptyProxyIdentities_Info_1(String company, String product);
	String emptyProxyIdentities_Info_2(String company, String product);
	String emptyProxyIdentities_SubHead();
	
	// Strings used for the empty my files storage view widget.
	String emptyMyFilesStorage_Info_1();
	String emptyMyFilesStorage_Info_2();
	String emptyMyFilesStorage_Info_3(String company, String product);
	String emptyMyFilesStorage_Info_4(                String product);
	String emptyMyFilesStorage_SubHead();
	
	// Strings used for the empty people view widget.
	String emptyPeople_Info_1(String company, String product);
	String emptyPeople_Info_2(String company, String product);
	String emptyPeople_SubHead();
	
	// Strings used for the empty teams view widget.
	String emptyTeams_Info_1(String company, String product);
	String emptyTeams_Info_2(String company, String product);
	String emptyTeams_SubHead();
	
	// Strings used for the empty globals view widget.
	String emptyGlobals_Info_1(String company, String product);
	String emptyGlobals_Info_2(String company, String product);
	String emptyGlobals_SubHead();
	
	// Strings used in the administration console home page.
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
	String runAReportDlgMaxSize();
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
	String licenseReportReport_StatsCol_OtherExt();
	String licenseReportReport_StatsCol_GuestAccessEnabled();
	String licenseReportReport_StatsCol_365();
	String licenseReportRunReport();
	String licenseReportGuestAccessEnabledYes();
	String licenseReportGuestAccessEnabledNo();

	
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

	// Strings used in the Configure Mobile Applications dialog.
	String configureMobileAppsDlgAddAndroid();
	String configureMobileAppsDlgAddIos();
	String configureMobileAppsDlgAllowAccess(String productName);
	String configureMobileAppsDlgAllowCacheContent();
	String configureMobileAppsDlgAllowCachePwd();
	String configureMobileAppsDlgButton_Add();
	String configureMobileAppsDlgButton_Delete();
	String configureMobileAppsDlgDisableApplicationsOnRootedOrJailBrokenDevices();
	String configureMobileAppsDlgForcePinCode();
	String configureMobileAppsDlgHeader();
	String configureMobileAppsDlgHeader2();
	String configureMobileAppsDlgHeader3();
	String configureMobileAppsDlgCutCopy();
	String configureMobileAppsDlgOnSaveUnknownException(String err);
	String configureMobileAppsDlgOpenIn();
	String configureMobileAppsDlgOpenIn_AllApps();
	String configureMobileAppsDlgOpenIn_Disabled();
	String configureMobileAppsDlgOpenIn_WhiteList();
	String configureMobileAppsDlgScreenCaptureAndroid();
	String configureMobileAppsDlgUseGlobalSettings();
	String configureMobileAppsDlgUseGroupSettings();
	String configureMobileAppsDlgUseUserSettings();
	String configureMobileAppsDlgWhiteListAndroid();
	String configureMobileAppsDlgWhiteListIos();
	String configureMobileAppsSyncIntervalLabel();
	String configureMobileAppsSyncMinutesLabel();
	
	// Strings used in the Configure User Mobile Applications dialog.
	String configuerUserMobileAppsDlgErrorHeader();
	String configureUserMobileAppsDlgHeaderGroups(String numGroup);
	String configureUserMobileAppsDlgHeaderUsers( String numUsers);
	String configureUserMobileAppsDlgOnSaveUnknownException(  String err  );
	String configuerUserMobileAppsDlgSaving(String completed, String total);

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
	
	// String used by the 'External User Report'
	String externalUserReportCaption1();
	String externalUserReportCaption2();
	String externalUserReportPeople();
	String externalUserReportRemove();
	String externalUserReportRunReport();

	// Strings used by the 'User Properties' dialog.
	String userPropertiesDlgEdit_HomeFolder();
	String userPropertiesDlgEdit_NetFolders();
	String userPropertiesDlgEdit_PersonalStorage();
	String userPropertiesDlgEdit_Profile();
	String userPropertiesDlgEdit_Quotas();
	String userPropertiesDlgEdit_Sharing();
	String userPropertiesDlgHeader();
	String userPropertiesDlgLabelize(String label);
	String userPropertiesDlgLabel_Home();
	String userPropertiesDlgLabel_HomePath();
	String userPropertiesDlgLabel_LastLogin();
	String userPropertiesDlgLabel_TermsAndConditionsAcceptDate();
	String userPropertiesDlgLabel_LdapContainer();
	String userPropertiesDlgLabel_LdapDN();
	String userPropertiesDlgLabel_NetFolders();
	String userPropertiesDlgLabel_NeverLoggedIn();
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
	String userPropertiesDlgPersonalStorage_NoExternal();
	String userPropertiesDlgPersonalStorage_NoGlobal();
	String userPropertiesDlgPersonalStorage_NoGuest();
	String userPropertiesDlgPersonalStorage_NoPerUser();
	String userPropertiesDlgPersonalStorage_YesGlobal();
	String userPropertiesDlgPersonalStorage_YesLocal();
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
	String userPropertiesDlgSharing_PublicLinks();
	String userPropertiesDlgSourceLDAP();
	String userPropertiesDlgSourceLocal();
	String userPropertiesDlgUnknown();
	String userPropertiesDlgYes();

	// Strings used in the 'Edit Zone Share Settings' dialog.
	String editZoneShareSettingsDlg_Header();
	String editZoneShareSettingsDlg_Lists();
	String editZoneShareSettingsDlg_Rights();
	String editZoneShareSettingsDlg_Terms();
	
	// Strings used in the 'Edit Zone Share Rights' tab.
	String editZoneShareRightsTab_AllowShareWithLdapGroups_NoExternal();
	String editZoneShareRightsTab_AllowShareWithLdapGroups_WithExternal();
	String editZoneShareRightsTab_ReadingRights();
	String editZoneShareRightsTab_SavingRights();
	String editZoneShareRightsTab_SelectPrincipalsHint();
	String editZoneShareTermsTab_LicensingTerms();
	String editZoneShareTermsTab_ShowTermsAndConditions();
	String editZoneShareTermsTab_ReadingLicensingTerms();
	
	// Strings used in the 'Edit Zone Share Lists' tab.
	String editZoneShareListsTab_Add();
	String editZoneShareListsTab_Cleanup();
	String editZoneShareListsTab_Confirm_DeleteShares();
	String editZoneShareListsTab_Delete();
	String editZoneShareListsTab_Domains();
	String editZoneShareListsTab_Domains_AddPrompt();
	String editZoneShareListsTab_EMAs();
	String editZoneShareListsTab_EMA_AddPrompt();
	String editZoneShareListsTab_Error_DeleteSharesFailed();
	String editZoneShareListsTab_Error_InvalidDomain();
	String editZoneShareListsTab_Error_InvalidEMA();
	String editZoneShareListsTab_Header();
	String editZoneShareListsTab_Mode();
	String editZoneShareListsTab_ModeBlacklist();
	String editZoneShareListsTab_ModeDisabled();
	String editZoneShareListsTab_ModeWhitelist();

	// Strings used in the 'Edit Zone Share Rights' dialog.
	String editUserZoneShareRightsDlg_Caption();
	String editUserZoneShareRightsDlg_Instructions();
	String editUserZoneShareRightsDlg_ReShareLabel();
	String editUserZoneShareRightsDlg_ShareExternalLabel();
	String editUserZoneShareRightsDlg_ShareInternalLabel();
	String editUserZoneShareRightsDlg_ShareLinkLabel();
	String editUserZoneShareRightsDlg_SharePublicLabel();
	String editUserZoneShareRightsDlg_ShareWithAllExternalUsersLabel();
	String editUserZoneShareRightsDlg_ShareWithAllInternalUsersLabel();
	
	// Strings used by the 'Rename an Entity' dialog.
	String renameEntityDlgError_BogusEntity(String entityType);
	String renameEntityDlgError_NameTooLong_File(     int max);
	String renameEntityDlgError_NameTooLong_Folder(   int max);
	String renameEntityDlgError_NameTooLong_Unknown(  int max);
	String renameEntityDlgError_NameTooLong_Workspace(int max);
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
	String fileConflictsDlgConfirmEmailTemplatesOverwrite1();
	String fileConflictsDlgConfirmEmailTemplatesOverwrite2();
	String fileConflictsDlgConfirmOverwrite1();
	String fileConflictsDlgConfirmOverwrite2();
	String fileConflictsDlgConfirmVersion1();
	String fileConflictsDlgConfirmVersion2();
	String fileConflictsDlgConflictingFiles();
	String fileConflictsDlgHeader();

	// Strings used in the Share with public info dialog.
	String shareWithPublicInfoDlg_Header();
	String shareWithPublicInfoDlg_InstructionsEntry();
	String shareWithPublicInfoDlg_InstructionsFolder();
	String shareWithPublicInfoDlg_Instructions3();

	// Strings used in the Change Password dialog.
	String changePasswordDlg_ChangeDefaultPasswordHint();
	String changePasswordDlg_ChangingPassword();
	String changePasswordDlg_ConfirmPasswordLabel();
	String changePasswordDlg_CurrentPasswordLabel();
	String changePasswordDlg_EnterCurrentPwd();
	String changePasswordDlg_ErrorChangingPassword(String desc);
	String changePasswordDlg_ChangeDefaultPasswordHeader();
	String changePasswordDlg_NewPasswordLabel();
	String changePasswordDlg_PasswordCannotBeEmpty();
	String changePasswordDlg_PasswordsDoNotMatch();
	
	// Strings used by the find control browser.
	String findControlBrowser_Error_NotSupporter(String searchType);

	// String used in the Forgotten Password dialog.
	String forgottenPwdDlg_Caption();
	String forgottenPwdDlg_EmailAddress();
	String forgottenPwdDlg_EnterEmailAddress();
	String forgottenPwdDlg_Hint2(String product);
	String forgottenPwdDlg_HintNote();
	String forgottenPwdDlg_Instructions();
	String forgottenPwdDlg_InvalidEmailAddress2(String product);
	String forgottenPwdDlg_ForgottenPwdEmailSent();
	String forgottenPwdDlg_OnlyForExternalUsers();
	String forgottenPwdDlg_SelfRegistrationEmailSent();
	String forgottenPwdDlg_sendingEmail();
	
	// String used in the main content control.
	String contentControl_Warning_ShareNoRights();

	// Strings used in the Net Folder Sync Statistics dialog.
	String netFolderSyncStatisticsDlg_CountFailure();
	String netFolderSyncStatisticsDlg_DirEnum();
	String netFolderSyncStatisticsDlg_DirOnly();
	String netFolderSyncStatisticsDlg_EndDate();
	String netFolderSyncStatisticsDlg_EntriesExpunged();
	String netFolderSyncStatisticsDlg_FileCount();
	String netFolderSyncStatisticsDlg_FilesAdded();
	String netFolderSyncStatisticsDlg_FilesExpunged();
	String netFolderSyncStatisticsDlg_FilesHeading();
	String netFolderSyncStatisticsDlg_FilesModified();
	String netFolderSyncStatisticsDlg_FilesSetAcl();
	String netFolderSyncStatisticsDlg_FilesSetOwnership();
	String netFolderSyncStatisticsDlg_FolderCount();
	String netFolderSyncStatisticsDlg_FoldersAdded();
	String netFolderSyncStatisticsDlg_FoldersExpunged();
	String netFolderSyncStatisticsDlg_FoldersHeading();
	String netFolderSyncStatisticsDlg_FoldersProcessed();
	String netFolderSyncStatisticsDlg_FoldersSetAcl();
	String netFolderSyncStatisticsDlg_FoldersSetOwnership();
	String netFolderSyncStatisticsDlg_Header(String netFolderName);
	String netFolderSyncStatisticsDlg_MiscHeading();
	String netFolderSyncStatisticsDlg_NodeIpAddress();
	String netFolderSyncStatisticsDlg_NoValue();
	String netFolderSyncStatisticsDlg_StartDate();
	
	// Strings used in the Delete Selections dialog.
	String deleteSelectionsDlgConfirm();
	String deleteSelectionsDlgHeader();
	String deleteSelectionsDlgLabel_Trash();
	String deleteSelectionsDlgLabel_TrashAdHoc();
	String deleteSelectionsDlgLabel_Purge();
	String deleteSelectionsDlgLabel_PurgeAll();
	String deleteSelectionsDlgLabel_PurgeOnly();
	String deleteSelectionsDlgWarning_CantUndo();
	
	// Strings used in the Delete Selected Users dialog.
	String deleteSelectedUsersDlgConfirmWS();
	String deleteSelectedUsersDlgConfirmWSAndUsers();
	String deleteSelectedUsersDlgHeader();
	String deleteSelectedUsersDlgLabel_Trash();
	String deleteSelectedUsersDlgLabel_TrashAdHoc();
	String deleteSelectedUsersDlgLabel_TrashDisabled();
	String deleteSelectedUsersDlgLabel_Purge();
	String deleteSelectedUsersDlgLabel_PurgeAll();
	String deleteSelectedUsersDlgLabel_PurgeOnly();
	String deleteSelectedUsersDlgLabel_PurgeUsers1();
	String deleteSelectedUsersDlgLabel_PurgeUsers2();
	String deleteSelectedUsersDlgLabel_PurgeUsers3();
	String deleteSelectedUsersDlgLabel_PurgeUsers4();
	String deleteSelectedUsersDlgWarning_CantUndo();

	// Strings used in the Edit Ldap Configuration dialog.
	String editLdapConfigDlg_AddLdapServerLabel();
	String editLdapConfigDlg_AllowLocalLoginLabel();
	String editLdapConfigDlg_ConfirmDelete(String msg);
	String editLdapConfigDlg_CreatingUsersLabel();
	String editLdapConfigDlg_DefaultLocaleLabel();
	String editLdapConfigDlg_DefaultTimeZoneLabel();	
	String editLdapConfigDlg_DeleteGroupsLabel();
	String editLdapConfigDlg_DeleteLdapServerLabel();
	String editLdapConfigDlg_DeleteUserLabel();
	String editLdapConfigDlg_DeleteUsersWarning();
	String editLdapConfigDlg_DeleteWorkspaceLabel();
	String editLdapConfigDlg_DisableUserLabel();
	String editLdapConfigDlg_DisableUserLabel2();
	String editLdapConfigDlg_EnableSyncScheduleLabel();
	String editLdapConfigDlg_GroupsTab();
	String editLdapConfigDlg_Header();
	String editLdapConfigDlg_LdapConfigMustBeSaved();
	String editLdapConfigDlg_LdapConfigMustBeSavedBeforePreviewCanBeStarted();
	String editLdapConfigDlg_LdapGuidAttribChanged();
	String editLdapConfigDlg_LdapServersTab();
	String editLdapConfigDlg_LdapSyncInProgressCantStartAnother();
	String editLdapConfigDlg_LocalUserAccountsTab();
	String editLdapConfigDlg_NoLdapServersLabel();
	String editLdapConfigDlg_NoLdapServersToSync();
	String editLdapConfigDlg_PreviewLdapSyncLabel();
	String editLdapConfigDlg_ReadingLdapConfig();
	String editLdapConfigDlg_RegisterGroupProfilesAutomatically();
	String editLdapConfigDlg_RegisterUserProfilesAutomatically();
	String editLdapConfigDlg_SavingLdapConfig();
	String editLdapConfigDlg_ScheduleTab();
	String editLdapConfigDlg_SelectLdapServersToDelete();
	String editLdapConfigDlg_SelectLdapServersToSync();
	String editLdapConfigDlg_ServerUrlCol();
	String editLdapConfigDlg_ShowSyncResultsLabel();
	String editLdapConfigDlg_SyncGroupMembership();
	String editLdapConfigDlg_SyncGroupProfiles();
	String editLdapConfigDlg_Syncing();
	String editLdapConfigDlg_SyncLdapServerLabel();
	String editLdapConfigDlg_SyncUserProfiles();
	String editLdapConfigDlg_UserDNCol();
	String editLdapConfigDlg_UsersTab();
	String editLdapConfigDlg_UserTypeCol();

	// Strings used in the Edit ldap server configuration dialog.
	String editLdapServerConfigDlg_AddSearchLabel();
	String editLdapServerConfigDlg_BaseDnCol();
	String editLdapServerConfigDlg_DeleteSearchLabel();
	String editLdapServerConfigDlg_DirTypeLabel();
	String editLdapServerConfigDlg_ErrorNoBaseDn();
	String editLdapServerConfigDlg_ErrorNoGuidAttrib();
	String editLdapServerConfigDlg_ErrorNoPwd();
	String editLdapServerConfigDlg_ErrorNoProxyDn();
	String editLdapServerConfigDlg_ErrorNoServerUrl();
	String editLdapServerConfigDlg_ErrorNoUserAttribMappings();
	String editLdapServerConfigDlg_ErrorNoUserIdAttrib(String productName);
	String editLdapServerConfigDlg_FilterCol();
	String editLdapServerConfigDlg_GroupsTab();
	String editLdapServerConfigDlg_GuidAttributeHint1();
	String editLdapServerConfigDlg_GuidAttributeLabel();
	String editLdapServerConfigDlg_Header();
	String editLdapServerConfigDlg_NameAttributeHint(  String productName);
	String editLdapServerConfigDlg_NameAttributeLabel( String productName);
	String editLdapServerConfigDlg_NameAttributePrompt(String productName);
	String editLdapServerConfigDlg_NoSearchesLabel();
	String editLdapServerConfigDlg_NoServerURL();
	String editLdapServerConfigDlg_Other();
	String editLdapServerConfigDlg_ProxyDn_Alt();
	String editLdapServerConfigDlg_ProxyDNLabel();
	String editLdapServerConfigDlg_ProxyPasswordLabel();
	String editLdapServerConfigDlg_SelectSearchesToDelete();
	String editLdapServerConfigDlg_ServerTab();
	String editLdapServerConfigDlg_ServerUrlHint();
	String editLdapServerConfigDlg_ServerUrlLabel();
	String editLdapServerConfigDlg_UserAttributeMappingHint();
	String editLdapServerConfigDlg_UsersTab();
	String editLdapServerConfigDlg_UserTypeExternal();
	String editLdapServerConfigDlg_UserTypeHint();
	String editLdapServerConfigDlg_UserTypeInternal();
	String editLdapServerConfigDlg_UserTypeLabel();

	// Strings used in the Edit Ldap Search dialog.
	String editLdapSearchDlg_AttributeNameLabel();
	String editLdapSearchDlg_BaseDn_Alt();
	String editLdapSearchDlg_BaseDnLabel();
	String editLdapSearchDlg_CustomCriteriaRB();
	String editLdapSearchDlg_DontCreateNetFolderRB();
	String editLdapSearchDlg_ErrorNoAttributeName();
	String editLdapSearchDlg_ErrorNoBaseDn();
	String editLdapSearchDlg_ErrorNoFilter();
	String editLdapSearchDlg_ErrorNoNetFolderPath();
	String editLdapSearchDlg_ErrorNoNetFolderServer();
	String editLdapSearchDlg_FilterLabel();
	String editLdapSearchDlg_Header();
	String editLdapSearchDlg_HomeDirAttribRB();
	String editLdapSearchDlg_HomeDirNetFolderHeader();
	String editLdapSearchDlg_HomeDirNetFolderHint();
	String editLdapSearchDlg_NetFolderPathLabel();
	String editLdapSearchDlg_NetFolderServerLabel();
	String editLdapSearchDlg_NoServerURL();
	String editLdapSearchDlg_SearchSubtreeLabel();
	String editLdapSearchDlg_SpecifiedAttribRB();

	// Strings used in the Ldap Sync Results dialog.
	String ldapSyncResultsDlg_ActionCol();
	String ldapSyncResultsDlg_AddedAction();
	String ldapSyncResultsDlg_AddedGroupsLabel();
	String ldapSyncResultsDlg_AddedUsersLabel();
	String ldapSyncResultsDlg_DeletedAction();
	String ldapSyncResultsDlg_DeletedGroupsLabel();
	String ldapSyncResultsDlg_DeletedUsersLabel();
	String ldapSyncResultsDlg_DisabledAction();
	String ldapSyncResultsDlg_DisabledUsersLabel();
	String ldapSyncResultsDlg_FilterOptionsAlt();
	String ldapSyncResultsDlg_GroupType();
	String ldapSyncResultsDlg_Header();
	String ldapSyncResultsDlg_HeaderPreview();
	String ldapSyncResultsDlg_ModifiedAction();
	String ldapSyncResultsDlg_ModifiedGroupsLabel();
	String ldapSyncResultsDlg_ModifiedUsersLabel();
	String ldapSyncResultsDlg_NameCol();
	String ldapSyncResultsDlg_NoLdapSyncResults();
	String ldapSyncResultsDlg_PreviewHint();
	String ldapSyncResultsDlg_RequestingLdapSyncResults();
	String ldapSyncResultsDlg_ServerLabel();
	String ldapSyncResultsDlg_ShowAddedGroupsCB();
	String ldapSyncResultsDlg_ShowAddedUsersCB();
	String ldapSyncResultsDlg_ShowDeletedGroupsCB();
	String ldapSyncResultsDlg_ShowDeletedUsersCB();
	String ldapSyncResultsDlg_ShowDisabledUsersCB();
	String ldapSyncResultsDlg_ShowModifiedGroupsCB();
	String ldapSyncResultsDlg_ShowModifiedUsersCB();
	String ldapSyncResultsDlg_SyncStatusLabel();
	String ldapSyncResultsDlg_SyncStatus_Completed();
	String ldapSyncResultsDlg_SyncStatus_Error();
	String ldapSyncResultsDlg_SyncStatus_InProgress();
	String ldapSyncResultsDlg_SyncStatus_NotCollectingResults();
	String ldapSyncResultsDlg_SyncStatus_SyncAlreadyInProgress();
	String ldapSyncResultsDlg_TypeCol();
	String ldapSyncResultsDlg_UserType();

	// Strings used by AlertDlg.
	String alertDlgHeader(String product);

	// Strings used by PromptDlg.
	String promptDlgHeader();

	// Strings used in the Email Public Link dialog.
	String emailPublicLinkDlg_EmailHint();
	String emailPublicLinkDlg_Hint1();
	String emailPublicLinkDlg_MessageHint();
	String emailPublicLinkDlg_NoEmailAddresses();
	String emailPublicLinkDlg_SendBtn();
	String emailPublicLinkDlg_SendingEmails();

	// Strings used in the Copy Public Link dialog.
	String copyPublicLink();
	String copyPublicLink_DownloadFileLink();
	String copyPublicLink_Button();
	String copyPublicLink_Error_ReadErrors();
	String copyPublicLink_HintSingle();
	String copyPublicLink_HintMultiple();
	String copyPublicLink_InternalError_NoEntries();
	String copyPublicLink_InternalError_NotAnEntry();
	String copyPublicLink_MultipleItems(int numItems);
	String copyPublicLink_Reading();
	String copyPublicLink_ViewFileLink();

	// Strings used in the Edit Public Link dialog.
	String editPublicLink_ConfirmDelete();
	String editPublicLink_DeleteLink();
	String editPublicLink_DownloadFileLink();
	String editPublicLink_InternalError_NoEntries();
	String editPublicLink_InternalError_NotAnEntry();
	String editPublicLink_InternalError_MoreThanOneEntry();
	String editPublicLink_ViewFileLink();

	// LDAP browser dialog strings.
	String ldapBrowser_Caption();
	String ldapBrowser_InternalError_CantFindTree();
	String ldapBrowser_InternalError_NoTrees();
	String ldapBrowser_Label_Empty();
	String ldapBrowser_Label_SelectOne();
	String ldapBrowser_Label_SelectTree();
	String ldapBrowser_Label_Tree();
	String ldapBrowser_Warning_Partial();

	// Strings used in the name completion settings dialog.
	String nameCompletionSettingsDlg_Desc();
	String nameCompletionSettingsDlg_FQDN();
	String nameCompletionSettingsDlg_Header();
	String nameCompletionSettingsDlg_Hint();
	String nameCompletionSettingsDlg_Name();
	String nameCompletionSettingsDlg_PrimaryDisplayLabel();
	String nameCompletionSettingsDlg_SavingSettings();
	String nameCompletionSettingsDlg_SecondaryDisplayLabel();
	String nameCompletionSettingsDlg_Title();
	
	// Strings used in the Manage Devices dialog.
	String manageMobileDevicesDlgCaptionSystem();
	String manageMobileDevicesDlgDevices(int dCount);
	String manageMobileDevicesDlgSystem();
	String manageMobileDevicesDlgUser(String title);
	String manageMobileDevicesDlg_confirmDelete();
	String manageMobileDevicesDlg_failureDeletingMobileDevices();

	// Strings used in the Mail To Multiple Public Links Select dialog.
	String mailToMultiplePublicLinksSelect();
	String mailToMultiplePublicLinksSelect_Caption(int count);
	String mailToMultiplePublicLinksSelect_Column_Expires();
	String mailToMultiplePublicLinksSelect_Column_Note();
	String mailToMultiplePublicLinksSelect_Column_SharedOn();
	String mailToMultiplePublicLinksSelect_HeaderTail();
	String mailToMultiplePublicLinksSelect_InternalError_NoLinks();
	String mailToMultiplePublicLinksSelect_Never();
	String mailToMultiplePublicLinksSelect_NoNote();

	// Strings for the tour facility.
	String tourCloseAlt();
	String tourDone();
	String tourNext();
	String tourPrev();
	String tourSkip();

	// Strings used in the Net Folder Global Settings dialog.
	String netFolderGlobalSettingsDlg_EnableJits();
	String netFolderGlobalSettingsDlg_Header();
	String netFolderGlobalSettingsDlg_MaxWaitLabel();
	String netFolderGlobalSettingsDlg_OnSaveUnknownException(String errMsg);
	String netFolderGlobalSettingsDlg_SavingConfig();
	String netFolderGlobalSettingsDlg_SecondsLabel();
	String netFolderGlobalSettingsDlg_UseDirRightsHint();

	// Strings used in the 'Prompt for external users email address'
	// dialog.
	String PromptForExternalUsersEmailAddressDlg_Header();
	String PromptForExternalUsersEmailAddressDlg_Hint();
	String PromptForExternalUsersEmailAddressDlg_NoEmailAddressesEntered();

	// String used by the 'Change Log Report' composite.
	String changeLogBinderId();
	String changeLogDescription();
	String changeLogEntityId();
	String changeLogEntityType();
	String changeLogEntityType_folder();
	String changeLogEntityType_folderEntry();
	String changeLogEntityType_group();
	String changeLogEntityType_profiles();
	String changeLogEntityType_user();
	String changeLogEntityType_workspace();
	String changeLogError_NoIds();
	String changeLogFindBinder();
	String changeLogFindEntity();
	String changeLogOperation();
	String changeLogOperation_addBinder();
	String changeLogOperation_addEntry();
	String changeLogOperation_addFile();
	String changeLogOperation_addWorkflowResponse();
	String changeLogOperation_deleteAccess();
	String changeLogOperation_deleteBinder();
	String changeLogOperation_deleteEntry();
	String changeLogOperation_deleteFile();
	String changeLogOperation_deleteVersion();
	String changeLogOperation_modifyAccess();
	String changeLogOperation_modifyBinder();
	String changeLogOperation_modifyEntry();
	String changeLogOperation_modifyFile();
	String changeLogOperation_modifyWorkflowState();
	String changeLogOperation_moveBinder();
	String changeLogOperation_moveEntry();
	String changeLogOperation_renameFile();
	String changeLogOperation_showAll();
	String changeLogOperation_startWorkflow();
	String changeLogRunReport();
	String changeLogWarning_NoChanges();
	
	// String used by the 'Copy Filters' dialog.
	String copyFiltersDlg_Alt_Browse();
	String copyFiltersDlg_Error_CantCopyFromSelf();
	String copyFiltersDlg_Error_InvalidSearchResult();
	String copyFiltersDlg_Error_NoFilters();
	String copyFiltersDlg_Error_NothingSelected();
	String copyFiltersDlg_Error_SaveErrors();
	String copyFiltersDlg_Hint();
	String copyFiltersDlgCaption();
	String copyFiltersDlgCaptionGlobal();
	String copyFiltersDlgCaptionPersonal();
	String copyFiltersDlgSelectSource();
	
	// Strings used by the KeyShield SSO Configuration dialog.
	String editKeyShieldConfigDlg_ApiAuthKeyLabel();
	String editKeyShieldConfigDlg_AuthConnectorNamesHint();
	String editKeyShieldConfigDlg_ConnectorNamesLabel();
	String editKeyShieldConfigDlg_EnableKeyShieldLabel();
	String editKeyShieldConfigDlg_Error_DefaultWeb();
	String editKeyShieldConfigDlg_Error_DefaultWebDAV();
	String editKeyShieldConfigDlg_Error_SavingConfig();
	String editKeyShieldConfigDlg_Error_SsoErrorMessageForWeb();
	String editKeyShieldConfigDlg_Error_SsoErrorMessageForWebdav();
	String editKeyShieldConfigDlg_HardwareTokenRequiredLabel();
	String editKeyShieldConfigDlg_Header();
	String editKeyShieldConfigDlg_HttpConnectionTimeoutLabel();
	String editKeyShieldConfigDlg_MilliSecondsLabel();
	String editKeyShieldConfigDlg_NonSsoAllowedForLdapUserLabel();
	String editKeyShieldConfigDlg_ReadingConfig();
	String editKeyShieldConfigDlg_SavingConfig();
	String editKeyShieldConfigDlg_ServerUrlLabel();
	String editKeyShieldConfigDlg_StackTraceLabel();
	String editKeyShieldConfigDlg_TestConnectionLabel();
	String editKeyShieldConfigDlg_TwoFactorAuthHeader();
	String editKeyShieldConfigDlg_UsernameAttributeAliasHint();
	String editKeyShieldConfigDlg_UsernameAttributeAliasLabel();
	
	// Strings used by the Access Rights Info dialog.
	String AccessRightsInfoDlg_Contributor();
	String AccessRightsInfoDlg_ContributorDesc_Filr();
	String AccessRightsInfoDlg_ContributorDesc_Vibe();
	String AccessRightsInfoDlg_Editor();
	String AccessRightsInfoDlg_EditorDesc_Filr();
	String AccessRightsInfoDlg_EditorDesc_Vibe();
	String AccessRightsInfoDlg_Header();
	String AccessRightsInfoDlg_Viewer();
	String AccessRightsInfoDlg_ViewerDesc_Filr();
	String AccessRightsInfoDlg_ViewerDesc_Vibe();
	
	// Strings used by the 'Select CSV Delimiter' dialog.
	String selectCSVDelimiterDlgCustom();
	String selectCSVDelimiterDlgErr_Blank(int max);
	String selectCSVDelimiterDlgErr_Backslash();
	String selectCSVDelimiterDlgErr_Slash();
	String selectCSVDelimiterDlgHeader();
	String selectCSVDelimiterDlgHint1();
	String selectCSVDelimiterDlgHint2(int max);
	
	// Strings used by the 'Limit User Visibility' dialog.
	String limitUserVisibilityDlg_Header();
	String limitUserVisibilityDlgErrorInvalidSearchResult();
	
	// Strings used by the 'Modify Limit User Visibility' dialog.
	String modifyLimitedUserVisibilityDlgHeader();
	String modifyLimitedUserVisibilityDlgHint_Group(String name);
	String modifyLimitedUserVisibilityDlgHint_User( String name);
	String modifyLimitedUserVisibilityDlgRB_Clear();
	String modifyLimitedUserVisibilityDlgRB_Limited();
	String modifyLimitedUserVisibilityDlgRB_Override();
	
	// Strings used in the Manage Proxy Identities dialog.
	String manageProxyIdentitiesDlg();
	String manageProxyIdentitiesDlgCaption();
	String manageProxyIdentitiesDlg_confirmDelete();
	String manageProxyIdentitiesDlg_failureDeletingProxyIdentities();
	
	// Strings used by the 'Proxy Identity' dialog.
	String proxyIdentityDlgError_AddFailed();
	String proxyIdentityDlgError_ModifyFailed();
	String proxyIdentityDlgError_NameTooLong(int max);
	String proxyIdentityDlgError_NoName();
	String proxyIdentityDlgError_NoPassword();
	String proxyIdentityDlgError_NoPasswordVerify();
	String proxyIdentityDlgError_NoTitle();
	String proxyIdentityDlgError_PasswordsDontMatch();
	String proxyIdentityDlgError_PasswordTooLong(      int max);
	String proxyIdentityDlgError_PasswordVerifyTooLong(int max);
	String proxyIdentityDlgError_TitleTooLong(         int max);
	String proxyIdentityDlgHeader_Add();
	String proxyIdentityDlgHeader_Modify();
	String proxyIdentityDlgName();
	String proxyIdentityDlgName_Alt();
	String proxyIdentityDlgPassword();
	String proxyIdentityDlgPasswordVerify();
	String proxyIdentityDlgTitle();
	
	// Strings used in the Manage Email Templates dialog.
	String manageEmailTemplatesDlg();
	String manageEmailTemplatesDlgCaption();
	String manageEmailTemplatesDlg_confirmDelete1();
	String manageEmailTemplatesDlg_confirmDelete2();
	String manageEmailTemplatesDlg_failureDeletingEmailTemplates11();
	String manageEmailTemplatesDlg_failureDeletingEmailTemplates12();
	String manageEmailTemplatesDlg_failureDeletingEmailTemplates21();
	String manageEmailTemplatesDlg_failureDeletingEmailTemplates22();
	
	// Strings used in the Telemetry Tier 2 dialog.
	String telemetryTier2Dlg_InternalError_CantCancel();
	String telemetryTier2DlgHeader();
	String telemetryTier2DlgHint1(String product);
	String telemetryTier2DlgHint2();
	String telemetryTier2DlgHint3();
	String telemetryTier2DlgHint4a(String product);
	String telemetryTier2DlgHint4b();
	
	// Strings used in the Configure Telemetry (admin console) dialog.
	String configureTelemetryDlg_Header();
	String configureTelemetryDlgDownloadLabel();
	String configureTelemetryDlgTier1EnabledCheckBoxLabel();
	String configureTelemetryDlgTier1EnabledHint1(String company);
	String configureTelemetryDlgTier1EnabledHint2();
	String configureTelemetryDlgTier2EnabledCheckBoxLabel();
	
	// Strings used in the Configure Anti Virus (admin console) dialog.
	String configureAntiVirusDlg_Header();
	String configureAntiVirusDlgConnectTimeoutLabel();
	String configureAntiVirusDlgEnabledCheckBoxLabel();
	String configureAntiVirusDlgErrorNoInterfaceId();
	String configureAntiVirusDlgErrorNoPassword();
	String configureAntiVirusDlgErrorNoServiceUrl();
	String configureAntiVirusDlgErrorNoUserName();
	String configureAntiVirusDlgInterfaceIDLabel();
	String configureAntiVirusDlgMillisecondsLabel();
	String configureAntiVirusDlgPasswordLabel();
	String configureAntiVirusDlgServiceURLLabel();
	String configureAntiVirusDlgTestErrorWithDetails(String details);
	String configureAntiVirusDlgTestErrorWithoutDetails();
	String configureAntiVirusDlgTestLabel();
	String configureAntiVirusDlgTestSuccess();
	String configureAntiVirusDlgUsernameLabel();
	
	// Strings used by the 'Default User Settings' dialog.
	String defaultUserSettingsDlg_ExternalUsers_Filr();
	String defaultUserSettingsDlg_ExternalUsers_Vibe();
	String defaultUserSettingsDlg_Header();
	String defaultUserSettingsDlg_InternalUsers();
	String defaultUserSettingsDlg_Locale();
	String defaultUserSettingsDlg_TimeZone();
	
	// Strings used by the 'Edit Desktop Branding' dialog.
	String editDesktopBrandingDlgCaption();
	String editDesktopBrandingDlg_Alt_MacRemove();
	String editDesktopBrandingDlg_Alt_MacUpload();
	String editDesktopBrandingDlg_Alt_WindowsRemove();
	String editDesktopBrandingDlg_Alt_WindowsUpload();
	String editDesktopBrandingDlg_Comfirm_MacOverwrite(    String fName);
	String editDesktopBrandingDlg_Comfirm_MacRemove(       String fName);
	String editDesktopBrandingDlg_Comfirm_WindowsOverwrite(String fName);
	String editDesktopBrandingDlg_Comfirm_WindowsRemove(   String fName);
	String editDesktopBrandingDlg_Hint_Overwrite(String fName, String dateTime);
	String editDesktopBrandingDlg_MacCaption();
	String editDesktopBrandingDlg_WindowsCaption();
	String editDesktopBrandingDlg_Error_NoMacFile();
	String editDesktopBrandingDlg_Error_NoWindowsFile();
	
	// Strings used by the 'Edit Mobile Branding' dialog.
	String editMobileBrandingDlgCaption();
	String editMobileBrandingDlg_Alt_AndroidRemove();
	String editMobileBrandingDlg_Alt_AndroidUpload();
	String editMobileBrandingDlg_Alt_IosRemove();
	String editMobileBrandingDlg_Alt_IosUpload();
	String editMobileBrandingDlg_Alt_WindowsRemove();
	String editMobileBrandingDlg_Alt_WindowsUpload();
	String editMobileBrandingDlg_Comfirm_AndroidOverwrite(String fName);
	String editMobileBrandingDlg_Comfirm_AndroidRemove(   String fName);
	String editMobileBrandingDlg_Comfirm_IosOverwrite(    String fName);
	String editMobileBrandingDlg_Comfirm_IosRemove(       String fName);
	String editMobileBrandingDlg_Comfirm_WindowsOverwrite(String fName);
	String editMobileBrandingDlg_Comfirm_WindowsRemove(   String fName);
	String editMobileBrandingDlg_AndroidCaption();
	String editMobileBrandingDlg_Hint_Overwrite(String fName, String dateTime);
	String editMobileBrandingDlg_IosCaption();
	String editMobileBrandingDlg_WindowsCaption();
	String editMobileBrandingDlg_Error_NoAndroidFile();
	String editMobileBrandingDlg_Error_NoIosFile();
	String editMobileBrandingDlg_Error_NoWindowsFile();
	
	// Strings used by the 'Upload Site Branding' composite.
	String uploadSiteBranding_Remove();
	String uploadSiteBranding_Upload();
}
