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
	String searchRPCFailed( String cause );

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
	String getFolderEntryRPCFailed( String cause );
	String pleaseSelectAnEntry();
	
	// Strings used with the Folder widget in the landing page editor
	String currentFolder();
	String findFolderLabel();
	String folderLabel();
	String folderProperties();
	String getFolderRPCFailed( String cause );
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
	String cancel();
	String ok();
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
	
	// Strings used for error messages
	String errorAccessToEntryDenied( String entryId );
	String errorAccessToFolderDenied( String folderId );
	String errorEntryDoesNotExist( String entryId );
	String errorFolderDoesNotExist( String folderId );
	String errorUnknownException();

	// Strings used with the MastHead
	String administrationHint();
	String getBrandingRPCFailed( String cause );
	String helpHint();
	String loginHint();
	String logoutHint();
	String myWorkspaceHint();
	
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
	String loginDlgKablinkHeader();
	String loginDlgLoginFailed();
	String loginDlgNovellHeader();
	String loginDlgPassword();
	String loginDlgReset();
	String loginDlgUserId();
	
	// Strings used in the main menu.
	String mainMenuAltBrowseHierarchy();
	String mainMenuAltGwtUI();
	String mainMenuAltLeftNavHideShow();
	String mainMenuAltMastHeadHideShow();
	String mainMenuBarActions();
	String mainMenuBarFolder();
	String mainMenuBarMyFavorites();
	String mainMenuBarMyTeams();
	String mainMenuBarMyWorkspace();
	String mainMenuBarRecentPlaces();
	String mainMenuBarWorkspace();
	String mainMenuFavoritesAdd();
	String mainMenuFavoritesDlgDelete();
	String mainMenuFavoritesDlgMoveDown();
	String mainMenuFavoritesDlgMoveUp();
	String mainMenuFavoritesEdit();
	String mainMenuFavoritesEditDlgHeader();
	String mainMenuFavoritesNoFavorites();
	String mainMenuFavoritesRemove();
	String mainMenuFolderOptionsDlgFolderViews();
	String mainMenuFolderOptionsDlgHeader();
	String mainMenuFolderOptionsDlgImportCalendar();
	String mainMenuFolderOptionsNoOptions();
	String mainMenuManageBrandFolder();
	String mainMenuManageBrandWorkspace();
	String mainMenuManageEditTeam();
	String mainMenuManageFolderOptions();
	String mainMenuManageSendTeamEmail();
	String mainMenuManageStartTeamConference();
	String mainMenuManageTagThisFolder();
	String mainMenuManageTagThisWorkspace();
	String mainMenuManageViewTeam();
	String mainMenuMyTeamsNoTeams();
	String mainMenuRecentPlacesNoPlaces();
	String mainMenuTagThisDlgCommunityTags();
	String mainMenuTagThisDlgAdd();
	String mainMenuTagThisDlgDelete();
	String mainMenuTagThisDlgHeaderFolder();
	String mainMenuTagThisDlgHeaderWorkspace();
	String mainMenuTagThisDlgPersonalTags();
	String mainMenuTagThisDlgTags();

	// Strings used in the workspace tree control.
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
	
}// end GwtTeamingMessages
