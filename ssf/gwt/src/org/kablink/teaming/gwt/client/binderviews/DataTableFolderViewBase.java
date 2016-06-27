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
package org.kablink.teaming.gwt.client.binderviews;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.kablink.teaming.gwt.client.GwtProxyIdentity;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingDataTableImageBundle;
import org.kablink.teaming.gwt.client.GwtTeamingFilrImageBundle;
import org.kablink.teaming.gwt.client.binderviews.EntryMenuPanel;
import org.kablink.teaming.gwt.client.binderviews.folderdata.ColumnWidth;
import org.kablink.teaming.gwt.client.binderviews.folderdata.DescriptionHtml;
import org.kablink.teaming.gwt.client.binderviews.folderdata.FolderColumn;
import org.kablink.teaming.gwt.client.binderviews.folderdata.FolderRow;
import org.kablink.teaming.gwt.client.binderviews.folderdata.GuestInfo;
import org.kablink.teaming.gwt.client.binderviews.util.BinderViewsHelper;
import org.kablink.teaming.gwt.client.binderviews.util.DeleteEntitiesHelper.DeleteEntitiesCallback;
import org.kablink.teaming.gwt.client.binderviews.util.DeleteUsersHelper.DeleteUsersCallback;
import org.kablink.teaming.gwt.client.binderviews.FooterPanel;
import org.kablink.teaming.gwt.client.binderviews.ViewReady;
import org.kablink.teaming.gwt.client.datatable.ActionMenuCell;
import org.kablink.teaming.gwt.client.datatable.ActionMenuColumn;
import org.kablink.teaming.gwt.client.datatable.ApplyColumnWidths;
import org.kablink.teaming.gwt.client.datatable.AssignmentColumn;
import org.kablink.teaming.gwt.client.datatable.CloudFolderAuthenticationDlg;
import org.kablink.teaming.gwt.client.datatable.CloudFolderAuthenticationDlg.CloudFolderAuthenticationCallback;
import org.kablink.teaming.gwt.client.datatable.CloudFolderAuthenticationDlg.CloudFolderAuthenticationDlgClient;
import org.kablink.teaming.gwt.client.datatable.CommentsColumn;
import org.kablink.teaming.gwt.client.datatable.CustomColumn;
import org.kablink.teaming.gwt.client.datatable.DescriptionHtmlColumn;
import org.kablink.teaming.gwt.client.datatable.DownloadColumn;
import org.kablink.teaming.gwt.client.datatable.EmailAddressColumn;
import org.kablink.teaming.gwt.client.datatable.EmailTemplateNameColumn;
import org.kablink.teaming.gwt.client.datatable.EntryPinColumn;
import org.kablink.teaming.gwt.client.datatable.EntryTitleColumn;
import org.kablink.teaming.gwt.client.datatable.GuestColumn;
import org.kablink.teaming.gwt.client.datatable.LimitedUserVisibilityColumn;
import org.kablink.teaming.gwt.client.datatable.MobileDeviceWipeScheduleInfo;
import org.kablink.teaming.gwt.client.datatable.MobileDevicesColumn;
import org.kablink.teaming.gwt.client.datatable.MobileDeviceWipeScheduledColumn;
import org.kablink.teaming.gwt.client.datatable.PresenceCell.PresenceClickAction;
import org.kablink.teaming.gwt.client.datatable.PresenceColumn;
import org.kablink.teaming.gwt.client.datatable.PrincipalAdminTypeColumn;
import org.kablink.teaming.gwt.client.datatable.ProxyIdentityTitleColumn;
import org.kablink.teaming.gwt.client.datatable.RatingColumn;
import org.kablink.teaming.gwt.client.datatable.ShareStringValueColumn;
import org.kablink.teaming.gwt.client.datatable.SizeColumnsDlg;
import org.kablink.teaming.gwt.client.datatable.SizeColumnsDlg.SizeColumnsDlgClient;
import org.kablink.teaming.gwt.client.datatable.StringColumn;
import org.kablink.teaming.gwt.client.datatable.TaskFolderColumn;
import org.kablink.teaming.gwt.client.datatable.VibeCheckboxCell;
import org.kablink.teaming.gwt.client.datatable.VibeCheckboxCell.VibeCheckboxData;
import org.kablink.teaming.gwt.client.datatable.VibeDataGrid;
import org.kablink.teaming.gwt.client.datatable.VibeColumn;
import org.kablink.teaming.gwt.client.datatable.VibeDataTableConstants;
import org.kablink.teaming.gwt.client.datatable.ViewColumn;
import org.kablink.teaming.gwt.client.event.ActivityStreamCommentDeletedEvent;
import org.kablink.teaming.gwt.client.event.ChangeEntryTypeSelectedEntitiesEvent;
import org.kablink.teaming.gwt.client.event.ClearSelectedUsersAdHocFoldersEvent;
import org.kablink.teaming.gwt.client.event.ClearSelectedUsersDownloadEvent;
import org.kablink.teaming.gwt.client.event.ClearSelectedUsersWebAccessEvent;
import org.kablink.teaming.gwt.client.event.ContentChangedEvent;
import org.kablink.teaming.gwt.client.event.ContentChangedEvent.Change;
import org.kablink.teaming.gwt.client.event.ContributorIdsReplyEvent;
import org.kablink.teaming.gwt.client.event.ContributorIdsRequestEvent;
import org.kablink.teaming.gwt.client.event.CopyPublicLinkSelectedEntitiesEvent;
import org.kablink.teaming.gwt.client.event.CopySelectedEntitiesEvent;
import org.kablink.teaming.gwt.client.event.DeleteSelectedEntitiesEvent;
import org.kablink.teaming.gwt.client.event.DeleteSelectedUsersEvent;
import org.kablink.teaming.gwt.client.event.DisableSelectedUsersEvent;
import org.kablink.teaming.gwt.client.event.DisableSelectedUsersAdHocFoldersEvent;
import org.kablink.teaming.gwt.client.event.DisableSelectedUsersDownloadEvent;
import org.kablink.teaming.gwt.client.event.DisableSelectedUsersWebAccessEvent;
import org.kablink.teaming.gwt.client.event.DownloadFolderAsCSVFileEvent;
import org.kablink.teaming.gwt.client.event.EditPublicLinkSelectedEntitiesEvent;
import org.kablink.teaming.gwt.client.event.EmailPublicLinkSelectedEntitiesEvent;
import org.kablink.teaming.gwt.client.event.EnableSelectedUsersEvent;
import org.kablink.teaming.gwt.client.event.EnableSelectedUsersAdHocFoldersEvent;
import org.kablink.teaming.gwt.client.event.EnableSelectedUsersDownloadEvent;
import org.kablink.teaming.gwt.client.event.EnableSelectedUsersWebAccessEvent;
import org.kablink.teaming.gwt.client.event.EventHelper;
import org.kablink.teaming.gwt.client.event.ForceSelectedUsersToChangePasswordEvent;
import org.kablink.teaming.gwt.client.event.FullUIReloadEvent;
import org.kablink.teaming.gwt.client.event.HideSelectedSharesEvent;
import org.kablink.teaming.gwt.client.event.InvokeBinderShareRightsDlgEvent;
import org.kablink.teaming.gwt.client.event.InvokeColumnResizerEvent;
import org.kablink.teaming.gwt.client.event.InvokeCopyFiltersDlgEvent;
import org.kablink.teaming.gwt.client.event.InvokeDropBoxEvent;
import org.kablink.teaming.gwt.client.event.InvokeEditInPlaceEvent;
import org.kablink.teaming.gwt.client.event.InvokeSignGuestbookEvent;
import org.kablink.teaming.gwt.client.event.LockSelectedEntitiesEvent;
import org.kablink.teaming.gwt.client.event.MailToPublicLinkEntityEvent;
import org.kablink.teaming.gwt.client.event.ManageSharesSelectedEntitiesEvent;
import org.kablink.teaming.gwt.client.event.MarkFolderContentsReadEvent;
import org.kablink.teaming.gwt.client.event.MarkFolderContentsUnreadEvent;
import org.kablink.teaming.gwt.client.event.MarkReadSelectedEntitiesEvent;
import org.kablink.teaming.gwt.client.event.MarkUnreadSelectedEntitiesEvent;
import org.kablink.teaming.gwt.client.event.MobileDeviceWipeScheduleStateChangedEvent;
import org.kablink.teaming.gwt.client.event.MoveSelectedEntitiesEvent;
import org.kablink.teaming.gwt.client.event.QuickFilterEvent;
import org.kablink.teaming.gwt.client.event.SetSelectedBinderShareRightsEvent;
import org.kablink.teaming.gwt.client.event.SetSelectedPrincipalsAdminRightsEvent;
import org.kablink.teaming.gwt.client.event.SetSelectedPrincipalsLimitedUserVisibilityEvent;
import org.kablink.teaming.gwt.client.event.SharedViewFilterEvent;
import org.kablink.teaming.gwt.client.event.ShareSelectedEntitiesEvent;
import org.kablink.teaming.gwt.client.event.ShowSelectedSharesEvent;
import org.kablink.teaming.gwt.client.event.SubscribeSelectedEntitiesEvent;
import org.kablink.teaming.gwt.client.event.TeamingEvents;
import org.kablink.teaming.gwt.client.event.ToggleSharedViewEvent;
import org.kablink.teaming.gwt.client.event.TrashPurgeAllEvent;
import org.kablink.teaming.gwt.client.event.TrashPurgeSelectedEntitiesEvent;
import org.kablink.teaming.gwt.client.event.TrashRestoreAllEvent;
import org.kablink.teaming.gwt.client.event.TrashRestoreSelectedEntitiesEvent;
import org.kablink.teaming.gwt.client.event.UnlockSelectedEntitiesEvent;
import org.kablink.teaming.gwt.client.event.ViewPinnedEntriesEvent;
import org.kablink.teaming.gwt.client.event.ViewSelectedEntryEvent;
import org.kablink.teaming.gwt.client.event.ViewWhoHasAccessEvent;
import org.kablink.teaming.gwt.client.event.ZipAndDownloadFolderEvent;
import org.kablink.teaming.gwt.client.event.ZipAndDownloadSelectedFilesEvent;
import org.kablink.teaming.gwt.client.rpc.shared.CanAddEntitiesRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.CanAddEntitiesToBindersRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.EntityRightsRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.ErrorListRpcResponseData.ErrorInfo;
import org.kablink.teaming.gwt.client.rpc.shared.FolderColumnsRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.FolderRowsRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.FolderRowsRpcResponseData.TotalCountType;
import org.kablink.teaming.gwt.client.rpc.shared.ErrorListRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.ForceUsersToChangePasswordCmd;
import org.kablink.teaming.gwt.client.rpc.shared.GetCanAddEntitiesToBindersCmd;
import org.kablink.teaming.gwt.client.rpc.shared.GetCommentCountCmd;
import org.kablink.teaming.gwt.client.rpc.shared.GetEntityRightsCmd;
import org.kablink.teaming.gwt.client.rpc.shared.GetFolderColumnsCmd;
import org.kablink.teaming.gwt.client.rpc.shared.GetFolderRowsCmd;
import org.kablink.teaming.gwt.client.rpc.shared.GetMyFilesContainerInfoCmd;
import org.kablink.teaming.gwt.client.rpc.shared.IntegerRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.SaveFolderPinningStateCmd;
import org.kablink.teaming.gwt.client.rpc.shared.SaveFolderSortCmd;
import org.kablink.teaming.gwt.client.rpc.shared.SaveSharedFilesStateCmd;
import org.kablink.teaming.gwt.client.rpc.shared.SaveSharedViewStateCmd;
import org.kablink.teaming.gwt.client.rpc.shared.SetEntriesPinStateCmd;
import org.kablink.teaming.gwt.client.rpc.shared.SetLimitedUserVisibilityRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.SetPrincipalsAdminRightsCmd;
import org.kablink.teaming.gwt.client.rpc.shared.SetPrincipalsAdminRightsRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.SetPrincipalsAdminRightsRpcResponseData.AdminRights;
import org.kablink.teaming.gwt.client.rpc.shared.SetUserVisibilityCmd;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.util.AssignmentInfo;
import org.kablink.teaming.gwt.client.util.BinderIconSize;
import org.kablink.teaming.gwt.client.util.BinderInfo;
import org.kablink.teaming.gwt.client.util.CloudFolderAuthentication;
import org.kablink.teaming.gwt.client.util.CollectionType;
import org.kablink.teaming.gwt.client.util.CommentsInfo;
import org.kablink.teaming.gwt.client.util.EmailAddressInfo;
import org.kablink.teaming.gwt.client.util.EntityId;
import org.kablink.teaming.gwt.client.util.EntityRights;
import org.kablink.teaming.gwt.client.util.EntryPinInfo;
import org.kablink.teaming.gwt.client.util.EntryTitleInfo;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.util.LimitedUserVisibilityInfo;
import org.kablink.teaming.gwt.client.util.MobileDevicesInfo;
import org.kablink.teaming.gwt.client.util.PrincipalAdminType;
import org.kablink.teaming.gwt.client.util.PrincipalInfo;
import org.kablink.teaming.gwt.client.util.ShareStringValue;
import org.kablink.teaming.gwt.client.util.SharedViewState;
import org.kablink.teaming.gwt.client.util.TaskFolderInfo;
import org.kablink.teaming.gwt.client.util.ViewFileInfo;
import org.kablink.teaming.gwt.client.util.WorkspaceType;
import org.kablink.teaming.gwt.client.widgets.ConfirmCallback;
import org.kablink.teaming.gwt.client.widgets.ConfirmDlg;
import org.kablink.teaming.gwt.client.widgets.BinderShareRightsDlg;
import org.kablink.teaming.gwt.client.widgets.BinderShareRightsDlg.BinderShareRightsDlgClient;
import org.kablink.teaming.gwt.client.widgets.ConfirmDlg.ConfirmDlgClient;
import org.kablink.teaming.gwt.client.widgets.VibeFlowPanel;
import org.kablink.teaming.gwt.client.widgets.VibeSelectAllHeader;
import org.kablink.teaming.gwt.client.widgets.VibeSimplePager;
import org.kablink.teaming.gwt.client.widgets.VibeVerticalPanel;

import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.view.client.AsyncDataProvider;
import com.google.gwt.view.client.DefaultSelectionEventManager;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.MultiSelectionModel;
import com.google.gwt.view.client.Range;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.cellview.client.AbstractCellTable;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent;
import com.google.gwt.user.cellview.client.ColumnSortList;
import com.google.gwt.user.cellview.client.ColumnSortList.ColumnSortInfo;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.cellview.client.Header;
import com.google.gwt.user.cellview.client.RowStyles;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.UIObject;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ProvidesKey;
import com.google.web.bindery.event.shared.HandlerRegistration;

/**
 * Base object of 'data table' based folder views.
 * 
 * @author drfoster@novell.com
 */
public abstract class DataTableFolderViewBase extends FolderViewBase
	implements ApplyColumnWidths,
		// Event handlers implemented by this class.
		ActivityStreamCommentDeletedEvent.Handler,
		ChangeEntryTypeSelectedEntitiesEvent.Handler,
		ClearSelectedUsersAdHocFoldersEvent.Handler,
		ClearSelectedUsersDownloadEvent.Handler,
		ClearSelectedUsersWebAccessEvent.Handler,
		ContentChangedEvent.Handler,
		ContributorIdsRequestEvent.Handler,
		CopyPublicLinkSelectedEntitiesEvent.Handler,
		CopySelectedEntitiesEvent.Handler,
		DeleteSelectedEntitiesEvent.Handler,
		DeleteSelectedUsersEvent.Handler,
		DisableSelectedUsersEvent.Handler,
		DisableSelectedUsersAdHocFoldersEvent.Handler,
		DisableSelectedUsersDownloadEvent.Handler,
		DisableSelectedUsersWebAccessEvent.Handler,
		DownloadFolderAsCSVFileEvent.Handler,
		EditPublicLinkSelectedEntitiesEvent.Handler,
		EmailPublicLinkSelectedEntitiesEvent.Handler,
		EnableSelectedUsersEvent.Handler,
		EnableSelectedUsersAdHocFoldersEvent.Handler,
		EnableSelectedUsersDownloadEvent.Handler,
		EnableSelectedUsersWebAccessEvent.Handler,
		ForceSelectedUsersToChangePasswordEvent.Handler,
		HideSelectedSharesEvent.Handler,
		InvokeBinderShareRightsDlgEvent.Handler,
		InvokeColumnResizerEvent.Handler,
		InvokeCopyFiltersDlgEvent.Handler,
		InvokeDropBoxEvent.Handler,
		InvokeEditInPlaceEvent.Handler,
		InvokeSignGuestbookEvent.Handler,
		LockSelectedEntitiesEvent.Handler,
		MailToPublicLinkEntityEvent.Handler,
		ManageSharesSelectedEntitiesEvent.Handler,
		MarkFolderContentsReadEvent.Handler,
		MarkFolderContentsUnreadEvent.Handler,
		MarkReadSelectedEntitiesEvent.Handler,
		MarkUnreadSelectedEntitiesEvent.Handler,
		MobileDeviceWipeScheduleStateChangedEvent.Handler,
		MoveSelectedEntitiesEvent.Handler,
		QuickFilterEvent.Handler,
		SetSelectedBinderShareRightsEvent.Handler,
		SetSelectedPrincipalsAdminRightsEvent.Handler,
		SetSelectedPrincipalsLimitedUserVisibilityEvent.Handler,
		SharedViewFilterEvent.Handler,
		ShareSelectedEntitiesEvent.Handler,
		ShowSelectedSharesEvent.Handler,
		SubscribeSelectedEntitiesEvent.Handler,
		ToggleSharedViewEvent.Handler,
		TrashPurgeAllEvent.Handler,
		TrashPurgeSelectedEntitiesEvent.Handler,
		TrashRestoreAllEvent.Handler,
		TrashRestoreSelectedEntitiesEvent.Handler,
		UnlockSelectedEntitiesEvent.Handler,
		ViewPinnedEntriesEvent.Handler,
		ViewSelectedEntryEvent.Handler,
		ViewWhoHasAccessEvent.Handler,
		ZipAndDownloadFolderEvent.Handler,
		ZipAndDownloadSelectedFilesEvent.Handler
{
	private BinderShareRightsDlg				m_binderShareRightsDlg;				// A BinderShareRightsDlg, once one is created.
	private boolean								m_fixedLayout;						//
	private CloudFolderAuthenticationDlg		m_cfaDlg;							//
	private Column<FolderRow, VibeCheckboxData>	m_selectColumn;						//
	private ColumnWidth							m_actionMenuColumnWidth;			//
	private ColumnWidth							m_100PctColumnWidth;				//
	private ColumnWidth							m_defaultColumnWidth;				//
	private List<FolderColumn>					m_folderColumnsList;				// The List<FolderColumn>' of the columns to be displayed.
	private List<HandlerRegistration>			m_dtfvb_registeredEventHandlers;	// Event handlers that are currently registered.
	private List<Long>							m_contributorIds;					//
	private Map<String, ColumnWidth>			m_defaultColumnWidths;				// Map of column names -> Default ColumnWidth objects.
	private Map<String, ColumnWidth>			m_columnWidths;						// Map of column names -> Current ColumnWidth objects.
	private SizeColumnsDlg						m_sizeColumnsDlg;					//
	private String								m_folderStyles;						// Specific style(s) for the for the folders that extend this.
	private String								m_quickFilter;						// Any quick filter that's active.
	private VibeDataGrid<FolderRow>				m_dataTable;						// The actual data table holding the view's information.
	private VibeSimplePager 					m_dataTablePager;					// Pager widgets at the bottom of the data table.
	
	protected GwtTeamingDataTableImageBundle	m_images;		//
	protected GwtTeamingFilrImageBundle			m_filrImages;	//

	// The following controls whether the display data read from the
	// server is dumped as part of the content of the view.
	private final static boolean DUMP_DISPLAY_DATA	= false;

	// The following is used to control how the data table is filled
	// when all the default column sizes are in pixels.  If true, a
	// separate padding column at 100% width is added.  If false, the
	// last column in the data table is adjusted to be 100% in width.
	private final static boolean ADD_PAD_COLUMN_FOR_ALL_PIXEL_WIDTHS	= false;	//
	
	// The following is used as the ID on the <IMG> containing the
	// pin header.
	private final static String PIN_HEADER_IMG_ID	= "pinHeaderImg";
	
	// The following are used to construct the style names applied
	// to the columns and rows of the data table.
	private final static String STYLE_COL_BASE		= "vibe-dataTableFolderColumn";
	private final static String STYLE_COL_SELECT	= "select";
	private final static String STYLE_ROW_BASE		= "vibe-dataTableFolderRow";
	private final static String STYLE_ROW_EVEN		= "even";
	private final static String STYLE_ROW_ODD		= "odd";

	// Defines how long we delay, in MS, after loading the rows for the
	// view before we send the request for the user's rights for adding
	// to any nested folder's.
	private final static int NESTED_FOLDER_RIGHTS_QUERY_DELAY	= 0;	// 0 -> No delay, simply asynchronous.
	
	// The following defines the TeamingEvents that are handled by
	// this class.  See EventHelper.registerEventHandlers() for how
	// this array is used.
	private static final TeamingEvents[] dtfvb_REGISTERED_EVENTS = new TeamingEvents[] {
		TeamingEvents.ACTIVITY_STREAM_COMMENT_DELETED,
		TeamingEvents.CHANGE_ENTRY_TYPE_SELECTED_ENTITIES,
		TeamingEvents.CLEAR_SELECTED_USERS_ADHOC_FOLDERS,
		TeamingEvents.CLEAR_SELECTED_USERS_DOWNLOAD,
		TeamingEvents.CLEAR_SELECTED_USERS_WEBACCESS,
		TeamingEvents.CONTENT_CHANGED,
		TeamingEvents.CONTRIBUTOR_IDS_REQUEST,
		TeamingEvents.COPY_PUBLIC_LINK_SELECTED_ENTITIES,
		TeamingEvents.COPY_SELECTED_ENTITIES,
		TeamingEvents.DELETE_SELECTED_ENTITIES,
		TeamingEvents.DELETE_SELECTED_USERS,
		TeamingEvents.DISABLE_SELECTED_USERS,
		TeamingEvents.DISABLE_SELECTED_USERS_ADHOC_FOLDERS,
		TeamingEvents.DISABLE_SELECTED_USERS_DOWNLOAD,
		TeamingEvents.DISABLE_SELECTED_USERS_WEBACCESS,
		TeamingEvents.DOWNLOAD_FOLDER_AS_CSV_FILE,
		TeamingEvents.EDIT_PUBLIC_LINK_SELECTED_ENTITIES,
		TeamingEvents.EMAIL_PUBLIC_LINK_SELECTED_ENTITIES,
		TeamingEvents.ENABLE_SELECTED_USERS,
		TeamingEvents.ENABLE_SELECTED_USERS_ADHOC_FOLDERS,
		TeamingEvents.ENABLE_SELECTED_USERS_DOWNLOAD,
		TeamingEvents.ENABLE_SELECTED_USERS_WEBACCESS,
		TeamingEvents.FORCE_SELECTED_USERS_TO_CHANGE_PASSWORD,
		TeamingEvents.HIDE_SELECTED_SHARES,
		TeamingEvents.INVOKE_BINDER_SHARE_RIGHTS_DLG,
		TeamingEvents.INVOKE_COLUMN_RESIZER,
		TeamingEvents.INVOKE_COPY_FILTERS_DLG,
		TeamingEvents.INVOKE_DROPBOX,
		TeamingEvents.INVOKE_EDIT_IN_PLACE,
		TeamingEvents.INVOKE_SIGN_GUESTBOOK,
		TeamingEvents.LOCK_SELECTED_ENTITIES,
		TeamingEvents.MAILTO_PUBLIC_LINK_ENTITY,
		TeamingEvents.MANAGE_SHARES_SELECTED_ENTITIES,
		TeamingEvents.MARK_FOLDER_CONTENTS_READ,
		TeamingEvents.MARK_FOLDER_CONTENTS_UNREAD,
		TeamingEvents.MARK_READ_SELECTED_ENTITIES,
		TeamingEvents.MARK_UNREAD_SELECTED_ENTITIES,
		TeamingEvents.MOBILE_DEVICE_WIPE_SCHEDULE_CHANGED,
		TeamingEvents.MOVE_SELECTED_ENTITIES,
		TeamingEvents.QUICK_FILTER,
		TeamingEvents.SET_SELECTED_BINDER_SHARE_RIGHTS,
		TeamingEvents.SET_SELECTED_PRINCIPALS_ADMIN_RIGHTS,
		TeamingEvents.SET_SELECTED_PRINCIPALS_LIMIT_USER_VISIBILITY,
		TeamingEvents.SHARED_VIEW_FILTER,
		TeamingEvents.SHARE_SELECTED_ENTITIES,
		TeamingEvents.SHOW_SELECTED_SHARES,
		TeamingEvents.SUBSCRIBE_SELECTED_ENTITIES,
		TeamingEvents.TOGGLE_SHARED_VIEW,
		TeamingEvents.TRASH_PURGE_ALL,
		TeamingEvents.TRASH_PURGE_SELECTED_ENTITIES,
		TeamingEvents.TRASH_RESTORE_ALL,
		TeamingEvents.TRASH_RESTORE_SELECTED_ENTITIES,
		TeamingEvents.UNLOCK_SELECTED_ENTITIES,
		TeamingEvents.VIEW_PINNED_ENTRIES,
		TeamingEvents.VIEW_SELECTED_ENTRY,
		TeamingEvents.VIEW_WHO_HAS_ACCESS,
		TeamingEvents.ZIP_AND_DOWNLOAD_FOLDER,
		TeamingEvents.ZIP_AND_DOWNLOAD_SELECTED_FILES,
	};
	
	/*
	 * Inner class used to provide list of FolderRow's.
	 */
	private class FolderRowAsyncProvider extends AsyncDataProvider<FolderRow> {
		private AbstractCellTable<FolderRow> m_vdt;	// The data table we're providing data for.
		
		/**
		 * Constructor method.
		 *
		 * @param vdt
		 * @param keyProvider
		 */
		public FolderRowAsyncProvider(AbstractCellTable<FolderRow> vdt, ProvidesKey<FolderRow> keyProvider) {
			// Initialize the super class and keep track of the data
			// table.
			super(keyProvider);
			m_vdt = vdt;
		}

		/**
		 * Called to asynchronously page through the data.
		 * 
		 * @param display
		 * 
		 * Overrides the AsyncDataProvider.onRowChanged() method.
		 */
		@Override
		protected void onRangeChanged(HasData<FolderRow> display) {
			onRangeChangedImpl(m_vdt, getFolderId(), display.getVisibleRange(), null);
		}
	}

	/*
	 * Inner class to provide a key to a FolderRow.
	 */
	private class FolderRowKeyProvider implements ProvidesKey<FolderRow> {
		/**
		 * Returns the key used to identity a FolderRow.
		 * 
		 * @param fr
		 * 
		 * Implements the ProvidesKey.getKey() method.
		 */
		@Override
		public Object getKey(FolderRow fr) {
			// The key to a row is its entityId.
			return fr.getEntityId();
		}
	}

	/*
	 * Inner class used to provide row selection for FolderRow's.
	 */
	protected class FolderRowSelectionModel extends MultiSelectionModel<FolderRow> {
		/**
		 * Constructor method.
		 * 
		 * @param keyProvider
		 */
		public FolderRowSelectionModel(FolderRowKeyProvider keyProvider) {
			// Simply initialize the super class.
			super(keyProvider);
		}
	}

	/*
	 * Inner class used to provide column sort handling for
	 * FolderRow's.
	 */
	private class FolderRowSortHandler implements ColumnSortEvent.Handler {
		/**
		 * Called when the user clicks a column to change the sort
		 * order.
		 * 
		 * Implements the ColumnSortEvent.Handler.onColumnSort()
		 * method.
		 * 
		 * @param event
		 */
		@Override
		public void onColumnSort(ColumnSortEvent event) {
			final Column<?, ?> column = event.getColumn();
			if (column instanceof VibeColumn) {
				@SuppressWarnings("unchecked")
				final FolderColumn fc = ((VibeColumn) column).getFolderColumn();
				final String  folderSortBy        = fc.getColumnSortKey();
				final boolean folderSortByChanged = (!(folderSortBy.equalsIgnoreCase(getFolderSortBy())));
				final boolean folderSortDescend   = (folderSortByChanged ? getFolderSortDescend() : (!getFolderSortDescend()));
				final SaveFolderSortCmd cmd = new SaveFolderSortCmd(getFolderInfo(), folderSortBy, (!folderSortDescend));
				GwtClientHelper.executeCommand(cmd, new AsyncCallback<VibeRpcResponse>() {
					@Override
					public void onFailure(Throwable caught) {
						GwtClientHelper.handleGwtRPCFailure(
							caught,
							GwtTeaming.getMessages().rpcFailure_SaveFolderSort());
					}

					@Override
					public void onSuccess(VibeRpcResponse result) {
						// Store the sort change...
						setFolderSortBy(     folderSortBy     );
						setFolderSortDescend(folderSortDescend);
						
						// ...and reset the view to redisplay things
						// ...with it.
						resetViewAsync();
					}
				});
			}
			
			else if (column instanceof EntryPinColumn) {
				// Toggle the state of the pin on the header.
				final boolean pinning = (!(isPinning()));
				setPinning(pinning);
				String imgTitle;
				String imgUrl;
				if (pinning)
				     {imgUrl = m_images.orangePin().getSafeUri().asString(); imgTitle = m_messages.vibeDataTable_Alt_PinHeader_UnpinAll();}
				else {imgUrl = m_images.grayPin().getSafeUri().asString();   imgTitle = m_messages.vibeDataTable_Alt_PinHeader_PinAll();  }
				Element img = DOM.getElementById(PIN_HEADER_IMG_ID);
				img.setAttribute("src",   imgUrl  );
				img.setAttribute("title", imgTitle);

				// Are there any entries (i.e., not binders) in the
				// data table?
				final List<EntityId> pinnedEntityIds = getAllPinnedEntityIds(!pinning);
				if (GwtClientHelper.hasItems(pinnedEntityIds)) {
					// Yes!  Pin/unpin them.
				    showBusySpinner();
					final SetEntriesPinStateCmd cmd = new SetEntriesPinStateCmd(pinnedEntityIds, pinning);
					GwtClientHelper.executeCommand(cmd, new AsyncCallback<VibeRpcResponse>() {
						@Override
						public void onFailure(Throwable caught) {
							hideBusySpinner();
							GwtClientHelper.handleGwtRPCFailure(
								caught,
								GwtTeaming.getMessages().rpcFailure_SetEntriesPinState());
						}

						@Override
						public void onSuccess(VibeRpcResponse result) {
							// Store the pin changes in the rows...
							for (EntityId entryId:  pinnedEntityIds) {
								getRowByEntityId(entryId).setPinned(pinning);
							}
							
							// ...and reset the view to redisplay things
							// ...with changes.
							hideBusySpinner();
							resetViewAsync();
						}
					});
				}
			}
		}
	}

	/**
	 * Constructor method.
	 * 
	 * @param folderInfo
	 * @param viewReady
	 * @param folderStyles
	 */
	public DataTableFolderViewBase(BinderInfo folderInfo, ViewReady viewReady, String folderStyles) {
		// Initialize the super class...
		super(folderInfo, viewReady, "vibe-dataTableFolder", true);

		// ...and initialize any other data members.
		initDataMembers(folderStyles);
	}
	
	/*
	 * Get'er methods.
	 */
	protected AbstractCellTable<FolderRow> getDataTable()          {return m_dataTable;                                   }
	private   boolean                      getFolderSortDescend()  {return getFolderDisplayData().getFolderSortDescend(); }
	private   int                          getFolderPageSize()     {return getFolderDisplayData().getFolderPageSize();    }
	private   Map<String, String>          getFolderColumnWidths() {return getFolderDisplayData().getFolderColumnWidths();}
	private   String                       getFolderSortBy()       {return getFolderDisplayData().getFolderSortBy();      }
	
	/*
	 * Set'er methods.
	 */
	private void setFolderSortBy(     String  folderSortBy)      {getFolderDisplayData().setFolderSortBy(folderSortBy);          }
	private void setFolderSortDescend(boolean folderSortDescend) {getFolderDisplayData().setFolderSortDescend(folderSortDescend);}

	/*
	 * Adds a column to manage pinning entries.
	 */
	private void addPinColumn(final FolderRowSelectionModel selectionModel, int colIndex, double pctTotal) {
		// Define the pin header...
		String imgTitle;
		String imgUrl;
		if (isPinning())
	         {imgUrl = m_images.orangePin().getSafeUri().asString(); imgTitle = m_messages.vibeDataTable_Alt_PinHeader_UnpinAll();}
		else {imgUrl = m_images.grayPin().getSafeUri().asString();   imgTitle = m_messages.vibeDataTable_Alt_PinHeader_PinAll();  }
		Image i = new Image();
		i.setUrl(  imgUrl  );
		i.setTitle(imgTitle);
		i.getElement().setId(PIN_HEADER_IMG_ID);
		VibeFlowPanel fp = new VibeFlowPanel();
		fp.add(i);
		SafeHtml pinHtml = SafeHtmlUtils.fromTrustedString(fp.getElement().getInnerHTML());

		// ...define a column for it...
		EntryPinColumn<FolderRow> column = new EntryPinColumn<FolderRow>() {
			@Override
			public EntryPinInfo getValue(FolderRow fr) {
				EntryPinInfo reply;
				if (fr.isBinder()) {
					reply = null;
				}
				else {
					reply = ((EntryPinInfo) fr.getClientEntryPinInfo());
					if (null == reply) {
						reply = new EntryPinInfo(
							fr.isPinned(),
							getFolderId(),
							fr.getEntityId().getEntityId());
						fr.setClientEntryPinInfo(reply);
					}
				}
				return reply;
			}
		};
		
		// ...and connect it all together.
		column.setSortable(   true                                             );	// Not really sorted, but enters/exits 'pin' mode. 
	    m_dataTable.addColumn(column, pinHtml                                  );
	    setColumnStyles(      column, FolderColumn.COLUMN_PIN, colIndex        );
	    setColumnWidth(               FolderColumn.COLUMN_PIN, column, pctTotal);
	}
	
	/*
	 * Adds a select column to the data table including a select all
	 * checkbox in the header.
	 */
	private void addSelectColumn(final FolderRowSelectionModel selectionModel, int colIndex, double pctTotal) {
		// Define the select all checkbox in the header...
		CheckboxCell cbSelectAllCell = new CheckboxCell();
		final VibeSelectAllHeader saHeader = new VibeSelectAllHeader(cbSelectAllCell);
		saHeader.setUpdater(new ValueUpdater<Boolean>() {
			@Override
			public void update(Boolean checked) {
				List<FolderRow> rows = m_dataTable.getVisibleItems();
				if (null != rows) {
					for (FolderRow row : rows) {
						if (!(row.isSelectionDisabled())) {
							selectionModel.setSelected(row, checked);
						}
					}
				}
				
				// If we have an entry menu...
				EntryMenuPanel emp = getEntryMenuPanel();
				if (null != emp) {
					// ...tell it to update the state of its items that
					// ...require a selection.
					checked = (0 < getSelectedEntityCount());
					EntryMenuPanel.setEntriesSelected(emp,  checked                                   );
					EntryMenuPanel.setEntrySelected(  emp, (checked && (1 == getSelectedEntryCount())));
				}
			}
		});

		// ...define a column for it...
		VibeCheckboxCell cbRowCell = new VibeCheckboxCell();
		m_selectColumn = new Column<FolderRow, VibeCheckboxData>(cbRowCell) {
			@Override
			public VibeCheckboxData getValue(FolderRow row) {
				return
					new VibeCheckboxData(
						selectionModel.isSelected(row),
						row.isSelectionDisabled());
			}
		};

		// ...connect updating the contents of the table when the
		// ...check box is checked or unchecked...
		m_selectColumn.setFieldUpdater(new FieldUpdater<FolderRow, VibeCheckboxData>() {
			@Override
			public void update(int index, FolderRow row, VibeCheckboxData data) {
				Boolean checked = data.getValue();
				selectionModel.setSelected(row, checked);
				if (!checked) {
					saHeader.setValue(checked);
					checked = areEntriesSelected();
				}

				// If we have an entry menu...
				EntryMenuPanel emp = getEntryMenuPanel();
				if (null != emp) {
					// ...tell it to update the state of its items that
					// ...require a selection.
					checked = (0 < getSelectedEntityCount());
					EntryMenuPanel.setEntriesSelected(emp,  checked                                   );
					EntryMenuPanel.setEntrySelected(  emp, (checked && (1 == getSelectedEntryCount())));
				}
			};
		});

		// ...and connect it all together.
		m_selectColumn.setSortable(false); 
	    m_dataTable.addColumn(m_selectColumn, saHeader);
	    setColumnStyles(m_selectColumn, FolderColumn.COLUMN_SELECT, colIndex        );
	    setColumnWidth(         FolderColumn.COLUMN_SELECT, m_selectColumn, pctTotal);
	}

	/**
	 * Allows the view's that extend this do what ever they need to
	 * to these widths for their own purposes.
	 * 
	 * @param columnWidths
	 */
	protected void adjustFixedColumnWidths(Map<String, ColumnWidth> columnWidths) {
		// By default, there are no adjustments.
	}
	
	protected void adjustFloatColumnWidths(Map<String, ColumnWidth> columnWidths) {
		// By default, there are no adjustments.
	}

	/**
	 * Applies the column widths in a column widths Map.
	 *  
	 * Implements the ApplyColumnWidths.applyColumnWidths() method.
	 * 
	 * @param folderColumns
	 * @param columnWidths
	 */
	@Override
	public void applyColumnWidths(List<FolderColumn> folderColumns, Map<String, ColumnWidth> columnWidths, ColumnWidth defaultColumnWidth) {
		// If all the columns being applied use pixel widths, force the
		// last one to a width of 100% when necessary.
		fixupPixelColumns(columnWidths, defaultColumnWidth);
		
		double pctTotal = ColumnWidth.sumPCTWidths(folderColumns, columnWidths, defaultColumnWidth);
		for (FolderColumn fc:  folderColumns) {
			String      cName = fc.getColumnName();
			ColumnWidth cw    = columnWidths.get(cName);
			setColumnWidth(
				((null == cw) ? defaultColumnWidth : cw),
				m_dataTable.getColumn(getColumnIndex(folderColumns, cName)),
				pctTotal);
		}
	}

	/**
	 * Returns true if there are binders in the data table and false
	 * otherwise.
	 * 
	 * @return
	 */
	final public boolean areBindersInDataTable() {
		// If we've got more than 1 page of entries, we'll assume there
		// will be a binder somewhere.  Do we have more than 1 page of
		// entries?
		int totalRows = m_dataTable.getRowCount();
	    int pageSize  = m_dataTablePager.getDisplay().getVisibleRange().getLength();
	    boolean reply = (totalRows > pageSize);
	    if (!reply) {
	    	// No!  We need to scan the rows in the table to see if
	    	// there any binders.
			List<FolderRow> rows = m_dataTable.getVisibleItems();
			if (null != rows) {
				for (FolderRow row:  rows) {
					if (row.isBinder()) {
						reply = true;
						break;
					}
				}
			}
	    }
	    
	    // If we get here, reply is true if there are binders in the
	    // data table and false otherwise.  Return it.
	    return reply;
	}

	/**
	 * Returns true if there are binders selected in the data table and
	 * false otherwise.
	 * 
	 * @return
	 */
	final public boolean areBindersSelectedInDataTable() {
		boolean reply = false;
		List<FolderRow> rows = m_dataTable.getVisibleItems();
		if (null != rows) {
			FolderRowSelectionModel fsm = ((FolderRowSelectionModel) m_dataTable.getSelectionModel());
			for (FolderRow row:  rows) {
				if (fsm.isSelected(row) && row.isBinder()) {
					reply = true;
					break;
				}
			}
		}
		
	    // If we get here, reply is true if there are binders selected
		// in the data table and false otherwise.  Return it.
		return reply;
	}
	
	/**
	 * Returns true if any rows are selected and false otherwise.
	 * 
	 * @return
	 */
	final public boolean areEntriesSelected() {
		// Are there any visible rows in the table?
		List<FolderRow> rows  = m_dataTable.getVisibleItems();
		if (null != rows) {
			// Yes!  Scan them
			FolderRowSelectionModel fsm = ((FolderRowSelectionModel) m_dataTable.getSelectionModel());
			for (FolderRow row : rows) {
				// Is this row selected?
				if (fsm.isSelected(row)) {
					// Yes!  Return true.  No need to look any further.
					return true;
				}
			}
		}
		
		// If we get here, no rows were selected.  Return false.
		return false;
	}

	/*
	 * Constructs a SafeHtml object containing the HTML for the comment
	 * column's header.
	 */
	private SafeHtml buildCommentHeaderHtml(FolderColumn fc) {
		VibeFlowPanel commentBubble = new VibeFlowPanel();
		commentBubble.addStyleName("vibe-dataTableComments-headerBubble");
		commentBubble.setTitle(fc.getColumnTitle());
		commentBubble.getElement().setInnerHTML("&nbsp;&nbsp;");
		VibeFlowPanel html = new VibeFlowPanel();
		html.add(commentBubble);
		return SafeHtmlUtils.fromTrustedString(html.getElement().getInnerHTML());
	}
	
	/*
	 * Constructs a SafeHtml object containing the HTML for the mobile
	 * devices column's header.
	 */
	private SafeHtml buildMobileDevicesHeaderHtml(FolderColumn fc) {
		VibeFlowPanel deviceBubble = new VibeFlowPanel();
		deviceBubble.addStyleName("vibe-dataTableMobileDevices-headerBubble");
		deviceBubble.setTitle(fc.getColumnTitle());
		deviceBubble.getElement().setInnerHTML("&nbsp;&nbsp;");
		VibeFlowPanel html = new VibeFlowPanel();
		html.add(deviceBubble);
		return SafeHtmlUtils.fromTrustedString(html.getElement().getInnerHTML());
	}
	
	/*
	 * Returns true if entries can be pinned in the current view and
	 * false otherwise.
	 */
	private boolean canPinEntries() {
		return getFolderDisplayData().getFolderSupportsPinning();
	}
	
	/*
	 * Returns true if entries can be selected in the current view and
	 * false otherwise.
	 */
	private boolean canSelectEntries() {
		return true;
	}

	/*
	 * Asynchronously runs the copy public link dialog on the selected
	 * entities.
	 */
	private void copySelectedEntitiesPublicLinkAsync(final List<EntityId> selectedEntities) {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				copySelectedEntitiesPublicLinkNow(selectedEntities);
			}
		});
	}
	
	/*
	 * Synchronously runs the copy public link dialog on the selected
	 * entities.
	 */
	private void copySelectedEntitiesPublicLinkNow(List<EntityId> selectedEntities) {
		BinderViewsHelper.copyEntitiesPublicLink(selectedEntities);
	}

	/*
	 * Returns the number of entry rows in a List<FolderRow> that are
	 * not file entries.
	 */
	private static int countNonFileEntryRows(List<FolderRow> rows) {
		int reply = 0;
		if (GwtClientHelper.hasItems(rows)) {
			boolean isFilr = GwtClientHelper.isLicenseFilr();
			for (FolderRow row:  rows) {
				if (row.getEntityId().isEntry() && (!(row.isRowFile(isFilr)))) {
					reply += 1;
				}
			}
		}
		return reply;
	}
	
	/*
	 * Removes the selection from the rows in a List<FolderRows>.
	 */
	private void deselectRows(List<FolderRow> rows) {
		// Do we have any rows to remove the selection from?
		if (GwtClientHelper.hasItems(rows)) {
			// Yes!  Scan them...
			FolderRowSelectionModel fsm = ((FolderRowSelectionModel) m_dataTable.getSelectionModel());
			for (FolderRow row : rows) {
				// ...and remove the selection from each.
				fsm.setSelected(row, false);
			}
		}
	}

	/*
	 * Initializes the data table as being empty.
	 */
	private void displayEmptyDataTable(final AbstractCellTable<FolderRow> vdt) {
		List<FolderRow> folderRows = new ArrayList<FolderRow>();
		vdt.setRowData( 0, folderRows);
		vdt.setRowCount(0            );
		postProcessRowDataAsync(folderRows);
	}
	
	/*
	 * Asynchronously sets the size of the data table based on its
	 * position in the view.
	 */
	private void doResizeAsync(int delay) {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				doResizeNow();
			}
		},
		delay);
	}

	/*
	 * Asynchronously sets the size of the data table based on its
	 * position in the view.
	 */
	private void doResizeAsync() {
		doResizeAsync(INITIAL_RESIZE_DELAY);
	}
	
	/*
	 * Synchronously sets the size of the data table based on its
	 * position in the view.
	 */
	private void doResizeNow() {
		onResize();
	}
	
	/*
	 * Asynchronously runs the edit public link dialog on the selected
	 * entities.
	 */
	private void editSelectedEntitiesPublicLinkAsync(final List<EntityId> selectedEntities) {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				editSelectedEntitiesPublicLinkNow(selectedEntities);
			}
		});
	}
	
	/*
	 * Synchronously runs the edit public link dialog on the selected
	 * entities.
	 */
	private void editSelectedEntitiesPublicLinkNow(List<EntityId> selectedEntities) {
		BinderViewsHelper.editEntitiesPublicLink(selectedEntities);
	}

	/*
	 * Asynchronously runs the email public link dialog on the selected
	 * entities.
	 */
	private void emailSelectedEntitiesPublicLinkAsync(final List<EntityId> selectedEntities) {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				emailSelectedEntitiesPublicLinkNow(selectedEntities);
			}
		});
	}
	
	/*
	 * Synchronously runs the email public link dialog on the selected
	 * entities.
	 */
	private void emailSelectedEntitiesPublicLinkNow(List<EntityId> selectedEntities) {
		BinderViewsHelper.emailEntitiesPublicLink(selectedEntities);
	}
	
	/*
	 * If all the column use pixel widths, forces the last one to a
	 * width of 100% when necessary.
	 */
	private void fixupPixelColumns(Map<String, ColumnWidth> columnWidths, ColumnWidth defaultColumnWidth) {
		// Are we adding a pad column if all the column widths are in
		// pixels?
		boolean addPadColumn = ADD_PAD_COLUMN_FOR_ALL_PIXEL_WIDTHS;
		if (!addPadColumn) {
			// No!  If all the columns being displayed are using pixel
			// widths...
			List<FolderColumn> sizingColumns = getColumnsForSizing();
			if (GwtClientHelper.hasItems(sizingColumns) && (0 == ColumnWidth.pctColumns(sizingColumns, columnWidths, defaultColumnWidth))) {
				// ...and the total pixel width is less than the width
				// ...of the view...
				int totalPXWidth = ColumnWidth.sumPXWidths(sizingColumns, columnWidths, defaultColumnWidth);
				int viewWidth    = getOffsetWidth();
//				GwtClientHelper.debugAlert("fixupPixelColumns( totalPXWidth=" + totalPXWidth + ", viewWidth=" + viewWidth + " )");	// Debug assist!
				if (totalPXWidth < viewWidth) {
					// ...force the last column to 100%.
					FolderColumn lastCol = sizingColumns.get(sizingColumns.size() - 1);
					columnWidths.put(lastCol.getColumnName(), m_100PctColumnWidth);
				}
			}
		}
	}
	
	/*
	 * Returns a List<EntityIds> of the EntityId's of all the entries
	 * (excluding binders) from the data table whose pinned state
	 * matches the given boolean.
	 */
	private List<EntityId> getAllPinnedEntityIds(boolean pinned) {
		// Are there any rows in the table?
		List<EntityId>  reply      = new ArrayList<EntityId>();
		List<FolderRow> folderRows = m_dataTable.getVisibleItems();
		if (null != folderRows) {
			// Yes!  Scan them
			for (FolderRow fr : folderRows) {
				// Is this row an entry that matches the requested
				// pinned state?
				EntityId rowEID = fr.getEntityId();
				if (rowEID.isEntry() && (fr.isPinned() == pinned)) {
					// Yes!  Add its entity ID to the List<EntityId>.
					reply.add(rowEID);
				}
			}
		}
		
		// If we get here, reply refers to List<EntityId> of the
		// EntityId's of the entries from the data table that match the
		// given pinned state.  Return it.
		return reply;
	}
	
	/*
	 * Returns the FolderColumn of a named FolderColumn from a
	 * List<FolderColumn>.
	 */
	private static FolderColumn getColumnByName(List<FolderColumn> folderColumns, String cName) {
		// Scan the List<FolderColumn>...
		for (FolderColumn fc:  folderColumns) {
			// ...is this the column in question?
			if (fc.getColumnName().equals(cName)) {
				// Yes!  Return its display index.
				return fc;
			}
		}

		// If we get here, we couldn't find the column in question. 
		// Return null.
		return null;
	}
	
	/*
	 * Returns the index of a named FolderColumn from a
	 * List<FolderColumn>.
	 */
	private static int getColumnIndex(List<FolderColumn> folderColumns, String cName) {
		// Scan the List<FolderColumn>...
		for (FolderColumn fc:  folderColumns) {
			// ...is this the column in question?
			if (fc.getColumnName().equals(cName)) {
				// Yes!  Return its display index.
				return fc.getDisplayIndex();
			}
		}

		// If we get here, we couldn't find the column in question. 
		// Return -1.
		return (-1);
	}
	
	/*
	 * Returns a List<FolderColumn> we can use to run the column sizing
	 * dialog.
	 */
	private List<FolderColumn> getColumnsForSizing() {
		// Allocate a List<FolderColumn> we can return...
		List<FolderColumn> reply = new ArrayList<FolderColumn>();

		// ...if this view supports entry selections...
		FolderColumn fc;
		if (canSelectEntries() && (null != m_selectColumn)) {
			// ...add a column for the checkbox selector...
			fc = new FolderColumn();
			fc.setColumnName(FolderColumn.COLUMN_SELECT);
			fc.setColumnTitle(m_messages.vibeDataTable_Select());
			reply.add(fc);
		}
		
		// ...if this view supports entry pinning...
		if (canPinEntries()) {
			// ...add a column for the pin selector...
			fc = new FolderColumn();
			fc.setColumnName(FolderColumn.COLUMN_PIN);
			fc.setColumnTitle(m_messages.vibeDataTable_Pin());
			reply.add(fc);
		}

		// ...copy all the defined columns...
		for (FolderColumn fcScan:  m_folderColumnsList) {
			reply.add(fcScan);
		}
		
		// ...and return the List<FolderColumn>.
		return reply;
	}

	/**
	 * Returns the widget to use for displaying the table empty message.
	 * 
	 * Provided as a convenience method.  Classes that extend this may
	 * override to provide whatever they want displayed.
	 * 
	 * @return
	 */
	protected Widget getEmptyTableWidget() {
		return
			new Label(
				isPinning()                                  ?
					m_messages.vibeDataTable_Empty_Pinning() :
					m_messages.vibeDataTable_Empty());
	}

	/*
	 * Asynchronously process the nested folders in the rows so that
	 * we can determine whether to make them drop targets.
	 */
	private void getNestedFolderRightsAsync(final List<FolderRow> folderRows) {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				getNestedFolderRightsNow(folderRows);
			}
		},
		NESTED_FOLDER_RIGHTS_QUERY_DELAY);
	}
	
	/*
	 * Synchronously process the nested folders in the rows so that
	 * we can determine whether to make them drop targets.
	 */
	private void getNestedFolderRightsNow(final List<FolderRow> folderRows) {
		// Scan the rows.
		final Map<Long, EntryTitleInfo> etiMap = new HashMap<Long, EntryTitleInfo>();
		final List<Long>                fIds   = new ArrayList<Long>();
		for (FolderRow fr:  folderRows) {
			// Is this row a nested folder?
			EntityId eid = fr.getEntityId();
			if (eid.isFolder()) {
				// Yes!  Does it contain an EntryTitleInfo without the
				// user's add rights?
				EntryTitleInfo eti = fr.getRowEntryTitlesMap().get(FolderColumn.COLUMN_TITLE);
				if ((null != eti) && (null == eti.getCanAddFolderEntities())) {
					// Yes!  Track the folder's ID and its
					// EntryTitleInfo.
					Long fid = eid.getEntityId(); 
					fIds.add(  fid     );
					etiMap.put(fid, eti);
				}
			}
		}

		// Did we find any nested folders we need to get the user's add
		// rights for?
		if (!(fIds.isEmpty())) {
			// Yes!  Get the user's add rights to them from the server.
			final GetCanAddEntitiesToBindersCmd cmd = new GetCanAddEntitiesToBindersCmd(fIds);
			GwtClientHelper.executeCommand(cmd, new AsyncCallback<VibeRpcResponse>() {
				@Override
				public void onFailure(Throwable caught) {
					GwtClientHelper.handleGwtRPCFailure(
						caught,
						GwtTeaming.getMessages().rpcFailure_GetCanAddEntitiesToBinders());
				}

				@Override
				public void onSuccess(VibeRpcResponse result) {
					// Extract the user's add rights from the
					// response...
					CanAddEntitiesToBindersRpcResponseData   response = ((CanAddEntitiesToBindersRpcResponseData) result.getResponseData());
					Map<Long, CanAddEntitiesRpcResponseData> addMap   = response.getCanAddEntitiesMap();
					
					// ...scan the folder IDs we got the user's add
					// ...rights for...
					Set<Long> fIds = addMap.keySet();
					for (Long fId:  fIds) {
						// ...and add the rights to the folder's
						// ...EntryTitleInfo.
						etiMap.get(fId).setCanAddFolderEntities(addMap.get(fId));
					}
				}
			});
		}
	}

	/*
	 * Returns the URL to use for a row's image.
	 */
	private String getRowImageUrl(FolderRow fr, EntryTitleInfo eti) {
		// Is the row a binder?
		BinderIconSize	bis = BinderIconSize.getListViewIconSize();
		ImageResource	imgRes;
		String			reply;
		if (fr.isBinder()) {
			// Yes!  Is this the user's home folder?
			if (fr.isHomeDir()) {
				// Yes!
				switch (bis) {
				default:
				case SMALL:   imgRes = m_filrImages.folderHome();        break; 
				case MEDIUM:  imgRes = m_filrImages.folderHome_medium(); break;
				case LARGE:   imgRes = m_filrImages.folderHome_large();  break;
				}
				reply  = imgRes.getSafeUri().asString();
			}
			
			else {
				// No, this isn't the user's home folder!  Do we have a
				// specific image for it?
				String binderIcon = fr.getBinderIcon(bis);
				if (GwtClientHelper.hasString(binderIcon)) {
					// Yes!  Use it to construct the URL.
					String imagesPath = GwtClientHelper.getRequestInfo().getImagesPath();
					if (binderIcon.startsWith("/"))
					     reply = (imagesPath + binderIcon.substring(1));
					else reply = (imagesPath + binderIcon);
				}
				
				else {
					// No, we don't have a specific image for it!  Use
					// the generic folder image.
					switch (bis) {
					default:
					case SMALL:   imgRes = m_filrImages.folder();        break;
					case MEDIUM:  imgRes = m_filrImages.folder_medium(); break;
					case LARGE:   imgRes = m_filrImages.folder_large();  break;
					}
					reply = imgRes.getSafeUri().asString();
				}
			}
		}
		
		else {
			// No, the row isn't a binder!
			reply = null;
			if ((null != eti) && eti.isFile()) {
				String fileIcon = eti.getFileIcon();
				if (GwtClientHelper.hasString(fileIcon)) {
					reply = (GwtClientHelper.getRequestInfo().getImagesPath() + fileIcon);
				}
			}

			// Do we have a specific icon from an entry?
			if (null == reply) {
				// No!  Use the generic entry image.
				switch (bis) {
				default:
				case SMALL:   imgRes = m_filrImages.entry();        break;
				case MEDIUM:  imgRes = m_filrImages.entry_medium(); break;
				case LARGE:   imgRes = m_filrImages.entry_large();  break;
				}
				reply = imgRes.getSafeUri().asString();
			}
		}
		
		// If we get here, reply refers to the URL for the row's image.
		// Return it.
		return reply;
	}
	
	/**
	 * Returns the FolderRow for the given entity ID.
	 * 
	 * @param entityId
	 */
	final protected FolderRow getRowByEntityId(EntityId entityId) {
		// Are there any rows in the table?
		List<FolderRow> rows  = m_dataTable.getVisibleItems();
		if (null != rows) {
			// Yes!  Scan them
			for (FolderRow row : rows) {
				// Is this row an entry?
				EntityId rowEID = row.getEntityId();
				if (rowEID.equalsEntityId(entityId)) {
					return row;
				}
			}
		}
		
		// If we get here, we couldn't find the row in question.
		// Return null.
		return null;
	}
	
	/**
	 * Returns a List<EntityIds> of the entity IDs of the selected rows
	 * from the table.
	 * 
	 * @return
	 */
	public List<EntityId> getSelectedEntityIds() {
		// Are there any selected rows in the table?
		List<EntityId>  reply = new ArrayList<EntityId>();
		List<FolderRow> rows  = m_dataTable.getVisibleItems();
		if (null != rows) {
			// Yes!  Scan them
			FolderRowSelectionModel fsm = ((FolderRowSelectionModel) m_dataTable.getSelectionModel());
			for (FolderRow row : rows) {
				// Is this row selected?
				if (fsm.isSelected(row)) {
					// Yes!  Add its entity ID to the List<EntityId>.
					reply.add(row.getEntityId());
				}
			}
		}
		
		// If we get here, reply refers to List<EntityId> of the entity
		// IDs of the selected rows from the data table.  Return it.
		return reply;
	}

	/*
	 * Returns a count of the selected entries (i.e., folder entries,
	 * or binders, ...)
	 */
	private int getSelectedEntityCount() {
		List<EntityId> selectedEntities = getSelectedEntityIds();
		return ((null == selectedEntities) ? 0 : selectedEntities.size());
	}

	/*
	 * Returns a count of the selected entries (i.e., folder entries,
	 * not binders, ...)
	 */
	private int getSelectedEntryCount() {
		int reply = 0;
		List<EntityId> selectedEntities = getSelectedEntityIds();
		if (GwtClientHelper.hasItems(selectedEntities)) {
			for (EntityId selectedEntity:  selectedEntities) {
				if (selectedEntity.isEntry()) {
					reply += 1;
				}
			}
		}
		return reply;
	}

	/*
	 * Scans the current columns looking for the comments column.  If
	 * it is found, it's returned.  Otherwise, null is returned.
	 */
	private FolderColumn getCommentsColumn() {
		for (FolderColumn fc:  m_folderColumnsList) {
			if (FolderColumn.isColumnComments(fc.getColumnName())) {
				return fc;
			}
		}
		return null;
	}
	
	/*
	 * Scans the current columns looking for the full name column.  If
	 * it is found, it's returned.  Otherwise, null is returned.
	 */
	private FolderColumn getFullNameColumn() {
		for (FolderColumn fc:  m_folderColumnsList) {
			if (FolderColumn.isColumnFullName(fc.getColumnName())) {
				return fc;
			}
		}
		return null;
	}
	
	/*
	 * Scans the current columns looking for the device wipe schedule
	 * column.  If it is found, it's returned.  Otherwise, null is
	 * returned.
	 */
	private FolderColumn getDeviceWipeScheduleColumn() {
		for (FolderColumn fc:  m_folderColumnsList) {
			if (FolderColumn.isColumnDeviceWipeScheduled(fc.getColumnName())) {
				return fc;
			}
		}
		return null;
	}
	
	/*
	 * Scans the current columns looking for the title column.  If it
	 * is found, it's returned.  Otherwise, null is returned.
	 */
	private FolderColumn getTitleColumn() {
		for (FolderColumn fc:  m_folderColumnsList) {
			if (FolderColumn.isColumnTitle(fc.getColumnName())) {
				return fc;
			}
		}
		return null;
	}
	
	/*
	 * Initializes various data members for the class.
	 */
	private void initDataMembers(String folderStyles) {
		// Store the parameters...
		m_folderStyles = folderStyles;

		// Initialize a map of the ColumnWidth's used in the data
		// table...
		m_columnWidths = new HashMap<String, ColumnWidth>();

		// ...initialize the remaining data members...
		m_filrImages  = GwtTeaming.getFilrImageBundle();
		m_images      = GwtTeaming.getDataTableImageBundle();
		m_fixedLayout = isFixedLayoutImpl(m_dataTable);
		if (m_fixedLayout)
		     initDataMembersFixed();
		else initDataMembersFloat();
		m_actionMenuColumnWidth = new ColumnWidth(VibeDataTableConstants.ACTION_MENU_WIDTH_PX, Unit.PX );
		m_100PctColumnWidth     = new ColumnWidth(100,                                         Unit.PCT);
		
		// ...and store the initial columns widths as the defaults.
		m_defaultColumnWidths = ColumnWidth.copyColumnWidths(m_columnWidths);
	}

	/*
	 * Initializes any additional data members required when using a
	 * fixed table layout.
	 * 
	 * Note:  Except for the select and pin columns, the values for the
	 *    column width were extracted from the implementation of
	 *    folder_view_common2.jsp or view_trash.jsp.
	 */
	private void initDataMembersFixed() {
		// The following defines the default width that will be used for
		// columns that don't have one specified.
		m_defaultColumnWidth = new ColumnWidth(20);

		// Add the widths for predefined column names...
		m_columnWidths.put(FolderColumn.COLUMN_AUTHOR,    new ColumnWidth( 24         ));	// Unless otherwise specified...
		m_columnWidths.put(FolderColumn.COLUMN_COMMENTS,  new ColumnWidth( 70, Unit.PX));	// ...the widths default to...
		m_columnWidths.put(FolderColumn.COLUMN_DATE,      new ColumnWidth(160, Unit.PX));	// ...be a percentage value.
		m_columnWidths.put(FolderColumn.COLUMN_DOCNUMBER, new ColumnWidth( 60, Unit.PX));
		m_columnWidths.put(FolderColumn.COLUMN_DOWNLOAD,  new ColumnWidth(  8         ));	
		m_columnWidths.put(FolderColumn.COLUMN_HTML,      new ColumnWidth( 10         ));
		m_columnWidths.put(FolderColumn.COLUMN_LOCATION,  new ColumnWidth( 30         ));
		m_columnWidths.put(FolderColumn.COLUMN_RATING,    new ColumnWidth( 10         ));
		m_columnWidths.put(FolderColumn.COLUMN_SIZE,      new ColumnWidth( 80, Unit.PX));
		m_columnWidths.put(FolderColumn.COLUMN_STATE,     new ColumnWidth(  8         ));
	    m_columnWidths.put(FolderColumn.COLUMN_TITLE,     new ColumnWidth(240, Unit.PX));

		// ...and then add the widths for everything else.
		m_columnWidths.put(FolderColumn.COLUMN_SELECT,   new ColumnWidth( 33, Unit.PX));
		m_columnWidths.put(FolderColumn.COLUMN_PIN,      new ColumnWidth( 33, Unit.PX));

		// Finally, let the view's that extend this do what ever they
		// need to these widths for their own purposes.
		adjustFixedColumnWidths(m_columnWidths);
	}
	
	/*
	 * Initializes any additional data members required when using a
	 * floating table layout.
	 */
	private void initDataMembersFloat() {
		// The following defines the default width that will be used for
		// columns that don't have one specified.
		m_defaultColumnWidth = null;	// null -> Flow (no specific width.)

		// For a floating table layout, the only column whose width we
		// explicitly set is the title.
		m_columnWidths.put(FolderColumn.COLUMN_TITLE, new ColumnWidth(100, Unit.PCT));
		
		// Finally, let the view's that extend this do what ever they
		// need to these widths for their own purposes.
		adjustFloatColumnWidths(m_columnWidths);
	}
	
	/*
	 * Initializes the columns in the data table.
	 */
	private void initTableColumns(final FolderRowSelectionModel selectionModel) {
		// Clear the data table's column sort list.
		ColumnSortList csl = m_dataTable.getColumnSortList();
		csl.clear();

		// If this folder supports entry selections...
		double pctTotal = ColumnWidth.sumPCTWidths(m_folderColumnsList, m_columnWidths, m_defaultColumnWidth);
		int colIndex = 0;
		if (canSelectEntries()) {
			// ...add a column for a checkbox selector.
			addSelectColumn(selectionModel, colIndex++, pctTotal);
		}

		// If this folder supports entry pinning...
		if (canPinEntries()) {
			// ...add a column to manage pinning the entry.
			addPinColumn(selectionModel, colIndex++, pctTotal);
		}

		// If all the columns being displayed use pixel widths, force
		// the last one to a width of 100% when necessary.
		fixupPixelColumns(m_columnWidths, m_defaultColumnWidth);
		
	    // Scan the columns defined in this folder.
		for (final FolderColumn fc:  m_folderColumnsList) {
			// For some columns (e.g., entry titles), we define a 2nd,
			// support column for it.  These variables are used to
			// define that.
			ColumnWidth					supportColumnWidth  = null;
			String						supportColumnTitle  = null;
			String						supportColumnStyles = null;
			VibeColumn<FolderRow, ?>	supportColumn       = null;
			SafeHtml					columnHeaderHtml    = null;
			String						columnHeaderStyle   = null;
			
			// We need to define a VibeColumn<FolderRow, ?> of some
			// sort for each one.  Is this a column that should show
			// a download link for?
			VibeColumn<FolderRow, ?> column;
			final String cName = fc.getColumnEleName();
			if (FolderColumn.isColumnDownload(cName)) {
				// Yes!  Create a DownloadColumn for it.
				column = new DownloadColumn<FolderRow>(fc) {
					@Override
					public Long getValue(FolderRow fr) {
						String value = fr.getColumnValueAsString(fc);
						Long reply;
						if (GwtClientHelper.hasString(value))
						     reply = fr.getEntityId().getEntityId();
						else reply = null;
						return reply;
					}
				};
			}
			
			// No, this column doesn't show a download link!  Does it
			// show presence?
			else if (FolderColumn.isColumnPresence(cName) || FolderColumn.isColumnFullName(cName) || FolderColumn.isColumnDeviceUser(cName)) {
				// Yes!  Create a PresenceColumn for it.
				boolean userManagementCell = (FolderColumn.isColumnFullName(cName) && getFolderInfo().isBinderProfilesRootWSManagement());
				PresenceClickAction clickAction;
				if (userManagementCell) clickAction = PresenceClickAction.SHOW_USER_PROPERTIES;
				else                    clickAction = PresenceClickAction.SHOW_SIMPLE_PROFILE;
				column = new PresenceColumn<FolderRow>(fc, clickAction) {
					@Override
					public PrincipalInfo getValue(FolderRow fr) {
						return fr.getColumnValueAsPrincipalInfo(fc);
					}
				};

				// Is this the full name column for the personal
				// workspaces view contained in the Manage Users
				// dialog? 
				if (userManagementCell) {
					// Yes!  Create an ActionMenuColumn for it.
					supportColumn = new ActionMenuColumn<FolderRow>(fc, getFolderInfo()) {
						@Override
						public EntryTitleInfo getValue(FolderRow fr) {
							// The ActionMenuColumn requires an
							// EntryTitleInfo.  For the full name
							// column in the manage users dialog, we
							// don't have one.  Construct a dummy
							// one with enough information for the
							// ActionMenuColumn to work.
							EntryTitleInfo dummyETI = new EntryTitleInfo();
							dummyETI.setEntityId(fr.getEntityId());
							return dummyETI;
						}
					};
					supportColumn.setSortable(false);
					supportColumnWidth  = m_actionMenuColumnWidth;
					supportColumnStyles = (STYLE_COL_BASE + " vibe-dataTableActions-column");
				}
			}

			// No, this column doesn't show presence either!  Does it
			// show a rating?
			else if (FolderColumn.isColumnRating(cName)) {
				// Yes!  Create a RatingColumn for it.
				column = new RatingColumn<FolderRow>(fc) {
					@Override
					public Integer getValue(FolderRow fr) {
						String value = fr.getColumnValueAsString(fc);
						if (null != value) value = value.trim();
						Integer reply;
						if (GwtClientHelper.hasString(value)) {
							reply = Math.round(Float.valueOf(value));
						}
						else reply = null;
						return reply;
					}
				};
			}
			
			// No, this column doesn't show a rating either!  Does it
			// show an entry title?
			else if (FolderColumn.isColumnTitle(cName)) {
				// Yes!  Create a EntryTitleColumn for it.
				column = new EntryTitleColumn<FolderRow>(fc, getFolderDisplayData().getFileLinkAction(), getFolderInfo(), this) {
					@Override
					public EntryTitleInfo getValue(FolderRow fr) {
						EntryTitleInfo reply = fr.getColumnValueAsEntryTitle(fc);
						if ((null != reply) && showEntryTitleIcon()) {
							// Create the rows's Image widget...
							Image rowImg = new Image();
							rowImg.getElement().setAttribute("align", "absmiddle");
							rowImg.setUrl(getRowImageUrl(fr, reply));
							
							// ...apply any scaling to the Image...
							int width  = BinderIconSize.getListViewIconSize().getBinderIconWidth();  if ((-1) != width)  rowImg.setWidth( width  + "px");
							int height = BinderIconSize.getListViewIconSize().getBinderIconHeight(); if ((-1) != height) rowImg.setHeight(height + "px");

							// ...and store the Image in the reply.
							reply.setClientItemImage(rowImg);
						}
						return reply;
					}
				};

				// Is this entry title for other than an item in a
				// trash folder? 
				if (!(isTrash())) {
					// Yes!  Create an ActionMenuColumn for it.
					supportColumn = new ActionMenuColumn<FolderRow>(fc, getFolderInfo()) {
						@Override
						public EntryTitleInfo getValue(FolderRow fr) {
							return fr.getColumnValueAsEntryTitle(fc);
						}
					};
					supportColumn.setSortable(false);
					supportColumnWidth  = m_actionMenuColumnWidth;
					supportColumnStyles = (STYLE_COL_BASE + " vibe-dataTableActions-column");
				}
			}
			
			// No, this column doesn't show an entry title either!
			// Does it show an email template name?
			else if (FolderColumn.isColumnEmailTemplateName(cName)) {
				// Yes!  Create a EmailTemplateNameColumn for it.
				column = new EmailTemplateNameColumn<FolderRow>(fc) {
					@Override
					public EntryTitleInfo getValue(FolderRow fr) {
						return fr.getColumnValueAsEntryTitle(fc);
					}
				};
			}
			
			// No, this column doesn't show an email template name
			// either!  Does it show a view link?
			else if (FolderColumn.isColumnView(cName)) {
				// Yes!  Create a ViewColumn for it.
				column = new ViewColumn<FolderRow>(fc) {
					@Override
					public ViewFileInfo getValue(FolderRow fr) {
						return fr.getColumnValueAsViewFile(fc);
					}
				};
			}
			
			// No, this column doesn't show a view link either!  Is it
			// a custom column?
			else if (FolderColumn.isColumnCustom(fc)) {
				// Yes!  Create a CustomColumn for it.
				column = new CustomColumn<FolderRow>(fc) {
					@Override
					public Object getValue(FolderRow fr) {
						Object             reply = fr.getColumnValueAsEntryEvent(fc);
						if (null == reply) reply = fr.getColumnValueAsEntryLink(fc);
						if (null == reply) reply = fr.getColumnValueAsString(fc);
						return reply;
					}
				};
			}
			
			// No, this column doesn't show a custom column either!
			// Does it display assignment information of some sort?
			else if (AssignmentInfo.isColumnAssigneeInfo(cName) ||
					 FolderColumn.isColumnTeamMembers(   cName) ||
			         FolderColumn.isColumnSharedBy(      cName) ||
			         FolderColumn.isColumnSharedWith(    cName)) {
				// Yes!  Create an AssignmentColumn for it.
				column = new AssignmentColumn<FolderRow>(fc, m_columnWidths.get(cName)) {
					@Override
					public List<AssignmentInfo> getValue(FolderRow fr) {
						return fr.getColumnValueAsAssignmentInfos(fc);
					}
				};
			}
			
			// No, this column doesn't show an assignment either!  Is
			// it a collection of task folders?
			else if (FolderColumn.isColumnTaskFolders(cName)) {
				// Yes!  Create an TaskFolderColumn for it.
				column = new TaskFolderColumn<FolderRow>(fc) {
					@Override
					public List<TaskFolderInfo> getValue(FolderRow fr) {
						return fr.getColumnValueAsTaskFolderInfos(fc);
					}
				};
			}

			// No, this column isn't a collection of task folders
			// either!  Is it an HTML description column?
			else if (FolderColumn.isColumnDescriptionHtml(cName) || FolderColumn.isColumnDeviceDescription(cName)) {
				// Yes!  Create a DescriptionHtmlColumn for it.
				column = new DescriptionHtmlColumn<FolderRow>(fc) {
					@Override
					public DescriptionHtml getValue(FolderRow fr) {
						return fr.getColumnValueAsDescriptionHtml(fc);
					}
				};

				// Is this description for a mobile device? 
				if (FolderColumn.isColumnDeviceDescription(cName)) {
					// Yes!  Create an ActionMenuColumn for it.
					supportColumn = new ActionMenuColumn<FolderRow>(fc, getFolderInfo()) {
						@Override
						public EntryTitleInfo getValue(FolderRow fr) {
							// The ActionMenuColumn requires an
							// EntryTitleInfo.  For the device
							// description column in the manage mobile
							// devices dialog, we don't have one.
							// Construct a dummy one with enough
							// information for the ActionMenuColumn to
							// work.
							EntryTitleInfo dummyETI = new EntryTitleInfo();
							dummyETI.setEntityId(fr.getEntityId());
							return dummyETI;
						}
					};
					supportColumn.setSortable(false);
					supportColumnWidth  = m_actionMenuColumnWidth;
					supportColumnStyles = (STYLE_COL_BASE + " vibe-dataTableActions-column");
				}
			}
			
			// No, this column isn't an HTML description column either!
			// Is it the signer of a guest book?
			else if (FolderColumn.isColumnGuest(cName)) {
				// Yes!  Create a GuestColumn for it.
				column = new GuestColumn<FolderRow>(fc) {
					@Override
					public GuestInfo getValue(FolderRow fr) {
						return fr.getColumnValueAsGuestInfo(fc);
					}
				};
			}
			
			// No, this column isn't signer of a guest book either!  Is
			// it an email address?
			else if (FolderColumn.isColumnEmailAddress(cName)) {
				// Yes!  Create a EmailAddressColumn for it.
				column = new EmailAddressColumn<FolderRow>(fc) {
					@Override
					public EmailAddressInfo getValue(FolderRow fr) {
						return fr.getColumnValueAsEmailAddress(fc);
					}
				};
			}
			
			// No, this column isn't an email address either!  Is it a
			// share string value?
			else if (FolderColumn.isColumnShareStringValue(cName)) {
				// Yes!  Create a ShareStringValueColumn for it.
				column = new ShareStringValueColumn<FolderRow>(fc) {
					@Override
					public List<ShareStringValue> getValue(FolderRow fr) {
						List<ShareStringValue> reply;
						if      (cName.equals(FolderColumn.COLUMN_SHARE_ACCESS))     reply = fr.getColumnValueAsShareAccessInfos(    fc);
						else if (cName.equals(FolderColumn.COLUMN_SHARE_DATE))       reply = fr.getColumnValueAsShareDateInfos(      fc);
						else if (cName.equals(FolderColumn.COLUMN_SHARE_EXPIRATION)) reply = fr.getColumnValueAsShareExpirationInfos(fc);
						else if (cName.equals(FolderColumn.COLUMN_SHARE_MESSAGE))    reply = fr.getColumnValueAsShareMessageInfos(   fc);
						else                                                         reply = null;
						return reply;
					}
				};
			}
			
			// No, this column isn't a shared value either!  Is it a
			// comments count?
			else if (FolderColumn.isColumnComments(cName)) {
				// Yes!  Create a CommentsColumn for it.
				column = new CommentsColumn<FolderRow>(fc) {
					@Override
					public CommentsInfo getValue(FolderRow fr) {
						FolderColumn	titleFC = getTitleColumn();
						EntryTitleInfo	eti     = ((null == titleFC) ? null : fr.getColumnValueAsEntryTitle(titleFC));
						CommentsInfo	reply   = fr.getColumnValueAsComments(fc);
						if (null != reply) {
							// Create an Image widget for the manage
							// comments dialog...
							Image rowImg = new Image();
							rowImg.addStyleName("vibe-manageCommentsDlg-captionImg");
							rowImg.getElement().setAttribute("align", "absmiddle");
							rowImg.setUrl(getRowImageUrl(fr, eti));
							
							// ...apply any scaling to the Image...
							int width  = BinderIconSize.getDialogCaptionIconSize().getBinderIconWidth();  if ((-1) != width)  rowImg.setWidth( width  + "px");
							int height = BinderIconSize.getDialogCaptionIconSize().getBinderIconHeight(); if ((-1) != height) rowImg.setHeight(height + "px");

							// ...and store the Image in the reply.
							reply.setClientItemImage(rowImg);
						}
						return reply;
					}
				};
				column.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
				columnHeaderHtml = buildCommentHeaderHtml(fc);
				columnHeaderStyle = "vibe-dataTableFolderColumn-headerCenter";
			}
			
			// No, this column isn't a comments count either!  Is it a
			// mobile devices count?
			else if (FolderColumn.isColumnMobileDevices(cName)) {
				// Yes!  Create a MobileDevicesColumn for it.
				column = new MobileDevicesColumn<FolderRow>(fc) {
					@Override
					public MobileDevicesInfo getValue(FolderRow fr) {
						FolderColumn		fullNameFC = getFullNameColumn();
						PrincipalInfo		pi         = ((null == fullNameFC) ? null : fr.getColumnValueAsPrincipalInfo(fullNameFC));
						MobileDevicesInfo	reply      = fr.getColumnValueAsMobileDevices(fc);
						if (null != reply) {
							// Create an Image widget for the manage
							// mobile devices dialog...
							String userUrl = pi.getAvatarUrl();
							if (!(GwtClientHelper.hasString(userUrl))) {
								userUrl = m_images.userPhoto().getSafeUri().asString();
							}
							Image rowImg = GwtClientHelper.buildImage(userUrl);
							rowImg.addStyleName("vibe-dataTableFolderColumn-mobileDevices");

							// ...and store the Image and user's title
							// ...in the reply.
							reply.setClientItemImage(rowImg);
							reply.setClientItemTitle(pi.getTitle());
						}
						return reply;
					}
				};
				column.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
				columnHeaderHtml = buildMobileDevicesHeaderHtml(fc);
				columnHeaderStyle = "vibe-dataTableFolderColumn-headerCenter";
			}
			
			// No, this column isn't a mobile devices count either!  Is
			// it a mobile devices wipe scheduled?
			else if (FolderColumn.isColumnDeviceWipeScheduled(cName)) {
				// Yes!  Create a MobileDeviceWipeScheduledColumn for
				// it.
				column = new MobileDeviceWipeScheduledColumn<FolderRow>(fc) {
					@Override
					public MobileDeviceWipeScheduleInfo getValue(FolderRow fr) {
						return
							new MobileDeviceWipeScheduleInfo(
								fr.getEntityId(),
								fr.getColumnWipeScheduled(fc),
								fr.getColumnValueAsString(fc));
					}
				};
			}
			
			// No, this column isn't a mobile device wipe scheduled
			// either!  Is it a principal type?
			else if (FolderColumn.isColumnPrincipalType(cName)) {
				// Yes!  Create a PrincipalTypeColumn for it.
				column = new PrincipalAdminTypeColumn<FolderRow>(fc) {
					@Override
					public PrincipalAdminType getValue(FolderRow fr) {
						return fr.getColumnValueAsPrincipalAdminType(fc);
					}
				};
			}
			
			// No, this column isn't a principal type either!  Is it a
			// can only see member?
			else if (FolderColumn.isColumnCanOnlySeeMembers(cName)) {
				// Yes!  Create a LimitedUserVisibilityColumn for it.
				column = new LimitedUserVisibilityColumn<FolderRow>(fc) {
					@Override
					public LimitedUserVisibilityInfo getValue(FolderRow fr) {
						return fr.getColumnValueAsLimitedUserVisibility(fc);
					}
				};
			}
			
			// No, this column isn't a can only see member column
			// either!  Is it a proxy title column??
			else if (FolderColumn.isColumnProxyTitle(cName)) {
				// Yes!  Create a ProxyIdentityTitleColumn for it.
				column = new ProxyIdentityTitleColumn<FolderRow>(fc) {
					@Override
					public GwtProxyIdentity getValue(FolderRow fr) {
						return fr.getColumnValueAsProxyIdentity(fc);
					}
				};
			}
			
			else {
				// No, this column isn't a proxy title either!  Define
				// a StringColumn for it.
				column = new StringColumn<FolderRow>(fc) {
					@Override
					public String getValue(FolderRow fr) {
						return fr.getColumnValueAsString(fc);
					}
				};
			}

			// Complete the initialization of the column.
			fc.setDisplayIndex(colIndex                                 );
			column.setSortable(fc.isColumnSortable()                    );
			if (null == columnHeaderHtml)
			     m_dataTable.addColumn(column, fc.getColumnTitle()      );
			else m_dataTable.addColumn(column, columnHeaderHtml         );
		    setColumnStyles(column, cName, colIndex++, columnHeaderStyle);
		    setColumnWidth( fc.getColumnName(), column, pctTotal        );

		    // Do we have a support column for the column we just
		    // added?
		    if (null != supportColumn) {
		    	// Yes!  Add it too.
		    	if (GwtClientHelper.hasString(supportColumnTitle))
			         m_dataTable.addColumn(supportColumn, supportColumnTitle                       );
		    	else m_dataTable.addColumn(supportColumn, SafeHtmlUtils.fromTrustedString("&nbsp;"));
		    	if (GwtClientHelper.hasString(supportColumnStyles)) {
		    		m_dataTable.addColumnStyleName(colIndex, supportColumnStyles);
		    	}
		    	colIndex += 1;
		    	if (null != supportColumnWidth) {
		    		m_dataTable.setColumnWidth(supportColumn, ColumnWidth.getWidthStyle(supportColumnWidth));
		    	}
		    }

		    // Is this the column we're sorted on?
		    if (fc.getColumnSortKey().equalsIgnoreCase(getFolderSortBy())) {
		    	// Yes!  Tell the data table about it.
				csl.push(
					new ColumnSortInfo(
						column,
						(!(getFolderSortDescend()))));
		    }
		}

		// Are all the columns being displayed using pixel widths?
		boolean addPadColumn = ADD_PAD_COLUMN_FOR_ALL_PIXEL_WIDTHS;
		if (addPadColumn) {
			List<FolderColumn> sizingColumns = getColumnsForSizing();
			if (GwtClientHelper.hasItems(sizingColumns) && (0 == ColumnWidth.pctColumns(sizingColumns, m_columnWidths, m_defaultColumnWidth))) {
				// Yes!  Then we need to add a padding column of 100% to
				// ensure the table gets filled to the end of the row and
				// doesn't unexpectedly stretch any of the pixel width
				// based columns.
				FolderColumn padFC = new FolderColumn();
				padFC.setColumnName(FolderColumn.COLUMN_PAD);
				padFC.setColumnTitle("");
				VibeColumn<FolderRow, ?> padColumn = new StringColumn<FolderRow>(padFC) {
					@Override
					public String getValue(FolderRow fr) {
						return "";
					}
				};
				padColumn.setSortable(false);
				m_dataTable.addColumn(padColumn, SafeHtmlUtils.fromTrustedString("&nbsp;"));
			    setColumnStyles(padColumn, FolderColumn.COLUMN_PAD, colIndex++);
				m_dataTable.setColumnWidth(padColumn, ColumnWidth.getWidthStyle(m_100PctColumnWidth));
			}
		}
	}

	/*
	 * Invoke the share dialog in administrative mode on the selected
	 * entities.
	 */
	private void invokeManageSharesDlgSelectedEntities(List<EntityId> selectedEntities) {
		BinderViewsHelper.invokeManageSharesDlg(selectedEntities);
	}

	/*
	 * Return true if the data table should used a fixed layout and
	 * false other wise. 
	 */
	@SuppressWarnings("unused")
	private boolean isFixedLayoutImpl(CellTable<FolderRow> ct) {
		// A CellTable can optionally use a fixed table layout.
		return false;
	}
	
	private boolean isFixedLayoutImpl(DataGrid<FolderRow> dg) {
		// A DataGrid must always use a fixed table layout.
		return true;
	}
	
	/*
	 * Asynchronously loads the column information for the folder.
	 */
	private void loadFolderColumnsAsync() {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				loadFolderColumnsNow();
			}
		});
	}

	/*
	 * Synchronously loads the column information for the folder.
	 */
	private void loadFolderColumnsNow() {
		GwtClientHelper.executeCommand(
				new GetFolderColumnsCmd(getFolderInfo()),
				new AsyncCallback<VibeRpcResponse>() {
			@Override
			public void onFailure(Throwable t) {
				GwtClientHelper.handleGwtRPCFailure(
					t,
					m_messages.rpcFailure_GetFolderColumns(),
					getFolderId());
			}
			
			@Override
			public void onSuccess(VibeRpcResponse response) {
				// Store the folder columns and complete the population of the view.
				FolderColumnsRpcResponseData responseData = ((FolderColumnsRpcResponseData) response.getResponseData());
				m_folderColumnsList = responseData.getFolderColumns();
				populateViewAsync();
			}
		});
	}
	
	/*
	 * Asynchronously mails the public link of the entity using a
	 * 'mailto://...' URL.
	 */
	private void mailToPublicLinkAsync(final EntityId entityId) {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				mailToPublicLinkNow(entityId);
			}
		});
	}
	
	/*
	 * Synchronously mails the public link of the entity using a
	 * 'mailto://...' URL.
	 */
	private void mailToPublicLinkNow(EntityId entityId) {
		BinderViewsHelper.mailToPublicLink(entityId);
	}

	/**
	 * Called when the data table is attached.
	 * 
	 * Overrides the Widget.onAttach() method.
	 */
	@Override
	public void onAttach() {
		// Let the widget attach and then register our event handlers.
		super.onAttach();
		registerEvents();
	}
	
	/**
	 * Handles ActivityStreamCommentDeletedEvent's received by this class.
	 * 
	 * Implements the ActivityStreamCommentDeletedEvent.Handler.onActivityStreamUIEntryDeleted() method.
	 * 
	 * @param event
	 */
	@Override
	public void onActivityStreamCommentDeleted(ActivityStreamCommentDeletedEvent event) {
		// If this view isn't displaying a comments column...
		FolderColumn commentsColumn = getCommentsColumn();
		if (null == commentsColumn) {
			// ...we don't need to do anything with this event.
			return;
		}
		// Do we have a top level EntityId that's targeted to the
		// binder this view is running against?
		EntityId topLevelEID = event.getTopLevelEntityId();
		if ((null != topLevelEID) && (getFolderInfo().isBinderCollection() || topLevelEID.getBinderId().equals(getFolderInfo().getBinderIdAsLong()))) {
			// Yes!  Then we need to update the comment comment count
			// in this entry's row, if it exists in the view.
			List<FolderRow> rows = m_dataTable.getVisibleItems();
			if (null != rows) {
				int rowIndex = 0;
				for (FolderRow row : rows) {
					if (row.getEntityId().equalsEntityId(topLevelEID)) {
						updateCommentCountAsync(commentsColumn, row, rowIndex);
						break;
					}
					rowIndex += 1;
				}
			}
		}
	}
	
	/**
	 * Handles ChangeEntryTypeSelectedEntitiesEvent's received by this class.
	 * 
	 * Implements the ChangeEntryTypeSelectedEntitiesEvent.Handler.onChangeEntryTypeSelectedEntities() method.
	 * 
	 * @param event
	 */
	@Override
	public void onChangeEntryTypeSelectedEntities(ChangeEntryTypeSelectedEntitiesEvent event) {
		// Is the event targeted to this folder?
		Long eventFolderId = event.getFolderId();
		if (eventFolderId.equals(getFolderId())) {
			// Yes!  Invoke the change.
			List<EntityId> selectedEntityIds = event.getSelectedEntities();
			if (!(GwtClientHelper.hasItems(selectedEntityIds))) {
				selectedEntityIds = getSelectedEntityIds();
			}
			BinderViewsHelper.changeEntryTypes(selectedEntityIds);
		}
	}
	
	/**
	 * Handles ClearSelectedUsersAdHocFoldersEvent's received by this class.
	 * 
	 * Implements the ClearSelectedUsersAdHocFoldersEvent.Handler.onClearSelectedUsersAdHocFolders() method.
	 * 
	 * @param event
	 */
	@Override
	public void onClearSelectedUsersAdHocFolders(ClearSelectedUsersAdHocFoldersEvent event) {
		// Is the event targeted to this folder?
		Long eventWorkspaceId = event.getWorkspaceId();
		if (eventWorkspaceId.equals(getFolderId())) {
			// Yes!  Invoke the clear.
			List<EntityId> selectedEntityIds = event.getSelectedEntities();
			if (!(GwtClientHelper.hasItems(selectedEntityIds))) {
				selectedEntityIds = getSelectedEntityIds();
			}
			BinderViewsHelper.clearUsersAdHocFolders(
				EntityId.getLongsFromEntityIds(selectedEntityIds),
				new FullUIReloadEvent());
		}
	}
	
	/**
	 * Handles ClearSelectedUsersDownloadEvent's received by this class.
	 * 
	 * Implements the ClearSelectedUsersDownloadEvent.Handler.onClearSelectedUsersDownload() method.
	 * 
	 * @param event
	 */
	@Override
	public void onClearSelectedUsersDownload(ClearSelectedUsersDownloadEvent event) {
		// Is the event targeted to this folder?
		Long eventWorkspaceId = event.getWorkspaceId();
		if (eventWorkspaceId.equals(getFolderId())) {
			// Yes!  Invoke the clear.
			List<EntityId> selectedEntityIds = event.getSelectedEntities();
			if (!(GwtClientHelper.hasItems(selectedEntityIds))) {
				selectedEntityIds = getSelectedEntityIds();
			}
			BinderViewsHelper.clearUsersDownload(
				EntityId.getLongsFromEntityIds(selectedEntityIds),
				new FullUIReloadEvent());
		}
	}
	
	/**
	 * Handles ClearSelectedUsersWebAccessEvent's received by this class.
	 * 
	 * Implements the ClearSelectedUsersWebAccessEvent.Handler.onClearSelectedUsersWebAccess() method.
	 * 
	 * @param event
	 */
	@Override
	public void onClearSelectedUsersWebAccess(ClearSelectedUsersWebAccessEvent event) {
		// Is the event targeted to this folder?
		Long eventWorkspaceId = event.getWorkspaceId();
		if (eventWorkspaceId.equals(getFolderId())) {
			// Yes!  Invoke the clear.
			List<EntityId> selectedEntityIds = event.getSelectedEntities();
			if (!(GwtClientHelper.hasItems(selectedEntityIds))) {
				selectedEntityIds = getSelectedEntityIds();
			}
			BinderViewsHelper.clearUsersWebAccess(
				EntityId.getLongsFromEntityIds(selectedEntityIds),
				new FullUIReloadEvent());
		}
	}
	
	/**
	 * Handles ContentChangedEvent's received by this class.
	 * 
	 * Implements the ContentChangedEvent.Handler.onContentChanged() method.
	 * 
	 * @param event
	 */
	@Override
	public void onContentChanged(final ContentChangedEvent event) {
		// If a share changed...
		if (Change.SHARING.equals(event.getChange())) {
			// ...in the 'Shared by Me' or 'Public' collection views...
			if (getFolderInfo().getCollectionType().isSharedByMe() ||
				getFolderInfo().getCollectionType().isSharedPublic()) {
				// ...force the UI to refresh.
				FullUIReloadEvent.fireOneAsync();
			}
			
			else {
				// ...otherwise, scan the columns in the table...
				int c = m_dataTable.getColumnCount();
				for (int i = 0; i < c; i += 1) {
					// ...and for all the ActionMenuColumn's...
					Column<?, ?> col = m_dataTable.getColumn(i);
					if (col instanceof ActionMenuColumn) {
						// ...tell their cells to clear their menu
						// ...maps thereby force the action menus to be
						// ...regenerated.
						((ActionMenuCell) col.getCell()).clearMenuMap();
					}
				}
			}
		}
	}
	
	/**
	 * Handles ContributorIdsRequestEvent's received by this class.
	 * 
	 * Implements the ContributorIdsRequestEvent.Handler.onContributorIdsRequest() method.
	 * 
	 * @param event
	 */
	@Override
	public void onContributorIdsRequest(ContributorIdsRequestEvent event) {
		// Is the event targeted to this folder?
		final Long eventBinderId = event.getBinderId();
		if (eventBinderId.equals(getFolderId())) {
			// Yes!  Asynchronously fire the corresponding reply event
			// with the contributor IDs.
			GwtClientHelper.deferCommand(new ScheduledCommand() {
				@Override
				public void execute() {
					GwtTeaming.fireEvent(
						new ContributorIdsReplyEvent(
							eventBinderId,
							m_contributorIds));
				}
			});
		}
	}
	
	/**
	 * Handles CopyPublicLinkSelectedEntitiesEvent's received by this class.
	 * 
	 * Implements the CopyPublicLinkSelectedEntitiesEvent.Handler.onCopyPublicLinkSelectedEntities() method.
	 * 
	 * @param event
	 */
	@Override
	public void onCopyPublicLinkSelectedEntities(CopyPublicLinkSelectedEntitiesEvent event) {
		// Is the event targeted to this folder?
		Long eventFolderId = event.getFolderId();
		if (eventFolderId.equals(getFolderId())) {
			// Yes!  Does the user have rights to share everything
			// they've selected?
			List<EntityId> seList = event.getSelectedEntities();
			final boolean validateSelectedRows = (!(GwtClientHelper.hasItems(seList)));
			if (validateSelectedRows) {
				seList = getSelectedEntityIds();
			}
			
			final List<EntityId> selectedEntities = seList;
			GwtClientHelper.executeCommand(
					new GetEntityRightsCmd(selectedEntities),
					new AsyncCallback<VibeRpcResponse>() {
				@Override
				public void onFailure(Throwable caught) {
					GwtClientHelper.handleGwtRPCFailure(
						caught,
						GwtTeaming.getMessages().rpcFailure_GetEntityRights());
				}

				@Override
				public void onSuccess(VibeRpcResponse response) {
					EntityRightsRpcResponseData responseData = ((EntityRightsRpcResponseData) response.getResponseData());
					onCopyPublicLinkSelectedEntitiesAsync(selectedEntities, responseData.getEntityRightsMap(), validateSelectedRows);
				}
			});
		}
	}

	/*
	 * Asynchronously processes the share request on the selected
	 * entries, given the current user's rights to them.
	 */
	private void onCopyPublicLinkSelectedEntitiesAsync(final List<EntityId> selectedEntities, final Map<String, EntityRights> entityRightsMap, final boolean validateSelectedRows) {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				onCopyPublicLinkSelectedEntitiesNow(selectedEntities, entityRightsMap, validateSelectedRows);
			}
		});
	}
	
	/*
	 * Synchronously processes the share request on the selected
	 * entries, given the current user's rights to them.
	 */
	private void onCopyPublicLinkSelectedEntitiesNow(final List<EntityId> selectedEntities, final Map<String, EntityRights> entityRightsMap, boolean validateSelectedRows) {
		// Are there any invalid rows?
		final List<FolderRow> invalidRows = (validateSelectedRows ? validateSelectedRows_PublicLink(entityRightsMap) : null);
		if (!(GwtClientHelper.hasItems(invalidRows))) {
			// No!  Invoke the share.
			copySelectedEntitiesPublicLinkAsync(selectedEntities);
		}
		
		else {
			// No, they don't have rights to share everything!  What
			// type of share failures are we dealing with?
			int totalPLFailures    = invalidRows.size();
			int plFolderFailures   = BinderViewsHelper.getFolderPublicLinkFailureCount(selectedEntities);
			int plNonFileFailures  = countNonFileEntryRows(invalidRows);
			int otherShareFailures = (totalPLFailures - (plFolderFailures + plNonFileFailures));
			if (0 > otherShareFailures) {
				otherShareFailures = 0;
			}
			boolean hasPLFolderFailures  = (0 < plFolderFailures  );
			boolean hasPLNonFileFailures = (0 < plNonFileFailures );
			boolean hasOtherPLFailures   = (0 < otherShareFailures);
			
			// Can they share any of them?
			if (selectedEntities.size() == totalPLFailures) {
				// No!  Tell them about the problem and bail.
				String shareAlert;
				if      (hasPLFolderFailures && hasPLNonFileFailures && hasOtherPLFailures) shareAlert = m_messages.vibeDataTable_Warning_CantCopyPublicLink_3();
				else if (hasPLFolderFailures && hasPLNonFileFailures)                       shareAlert = m_messages.vibeDataTable_Warning_CantCopyPublicLink_2a();
				else if (                       hasPLNonFileFailures && hasOtherPLFailures) shareAlert = m_messages.vibeDataTable_Warning_CantCopyPublicLink_2b();
				else if (hasPLFolderFailures &&                         hasOtherPLFailures) shareAlert = m_messages.vibeDataTable_Warning_CantCopyPublicLink_2c();
 				else if (hasPLFolderFailures)                                               shareAlert = m_messages.vibeDataTable_Warning_CantCopyPublicLink_1a();
				else if (                       hasPLNonFileFailures)                       shareAlert = m_messages.vibeDataTable_Warning_CantCopyPublicLink_1b();
				else                                                                        shareAlert = m_messages.vibeDataTable_Warning_CantCopyPublicLink_1c();
				GwtClientHelper.deferredAlert(shareAlert);
				return;
			}
			
			// Is the user sure they want to share the selections
			// they have rights to share?
			final String confirmPrompt;
			if      (hasPLFolderFailures && hasPLNonFileFailures && hasOtherPLFailures) confirmPrompt = m_messages.vibeDataTable_Confirm_CantCopyPublicLink_3();
			else if (hasPLFolderFailures && hasPLNonFileFailures)                       confirmPrompt = m_messages.vibeDataTable_Confirm_CantCopyPublicLink_2a();
			else if (                       hasPLNonFileFailures && hasOtherPLFailures) confirmPrompt = m_messages.vibeDataTable_Confirm_CantCopyPublicLink_2b();
			else if (hasPLFolderFailures &&                         hasOtherPLFailures) confirmPrompt = m_messages.vibeDataTable_Confirm_CantCopyPublicLink_2c();
			else if (hasPLFolderFailures)                                               confirmPrompt = m_messages.vibeDataTable_Confirm_CantCopyPublicLink_1a();
			else if (                       hasPLNonFileFailures)                       confirmPrompt = m_messages.vibeDataTable_Confirm_CantCopyPublicLink_1b();
			else                                                                        confirmPrompt = m_messages.vibeDataTable_Confirm_CantCopyPublicLink_1c();
			ConfirmDlg.createAsync(new ConfirmDlgClient() {
				@Override
				public void onUnavailable() {
					// Nothing to do.  Error handled in
					// asynchronous provider.
				}
				
				@Override
				public void onSuccess(ConfirmDlg cDlg) {
					ConfirmDlg.initAndShow(
						cDlg,
						new ConfirmCallback() {
							@Override
							public void dialogReady() {
								// Ignored.  We don't really care when the
								// dialog is ready.
							}

							@Override
							public void accepted() {
								// Yes, they're sure!  Remove the
								// selection from the entries they
								// don't have rights to share and
								// perform the copy public link on the
								// rest.
								removeRowEntities(                  selectedEntities, invalidRows);
								deselectRows(                                         invalidRows);
								copySelectedEntitiesPublicLinkAsync(selectedEntities             );
							}

							@Override
							public void rejected() {
								// No, they're not sure!
							}
						},
						confirmPrompt);
				}
			});
		}
	}
	
	/**
	 * Handles CopySelectedEntitiesEvent's received by this class.
	 * 
	 * Implements the CopySelectedEntitiesEvent.Handler.onCopySelectedEntities() method.
	 * 
	 * @param event
	 */
	@Override
	public void onCopySelectedEntities(CopySelectedEntitiesEvent event) {
		// Is the event targeted to this folder?
		Long eventFolderId = event.getFolderId();
		if (eventFolderId.equals(getFolderId())) {
			// Yes!  If the event doesn't contain any entities...
			List<EntityId> selectedEntityIds = event.getSelectedEntities();
			if (!(GwtClientHelper.hasItems(selectedEntityIds))) {
				// ...invoke the copy on those selected in the data
				// ...table.
				selectedEntityIds = getSelectedEntityIds();
				BinderViewsHelper.copyEntries(selectedEntityIds);
			}
		}
	}
	
	/**
	 * Handles DeleteSelectedEntitiesEvent's received by this class.
	 * 
	 * Implements the DeleteSelectedEntitiesEvent.Handler.onDeleteSelectedEntities() method.
	 * 
	 * @param event
	 */
	@Override
	public void onDeleteSelectedEntities(DeleteSelectedEntitiesEvent event) {
		// Is the event targeted to this folder?
		Long eventFolderId = event.getFolderId();
		if (eventFolderId.equals(getFolderId())) {
			// Yes!  Does the event contain any entities?
			List<EntityId> selectedEntityIds = event.getSelectedEntities();
			if (!(GwtClientHelper.hasItems(selectedEntityIds))) {
				// No!  Delete the entities selected in the data table
				// and reset the view to redisplay things with the
				// entities deleted.
				selectedEntityIds = getSelectedEntityIds();
				final boolean deletingBinders = EntityId.areBindersInEntityIds(selectedEntityIds);
				BinderViewsHelper.deleteSelections(
						selectedEntityIds,
						new DeleteEntitiesCallback() {
					@Override
					public void operationCanceled() {
						if (deletingBinders) {
							GwtClientHelper.getRequestInfo().setRefreshSidebarTree();
							FullUIReloadEvent.fireOne();
						}
						else {
							resetViewAsync();
						}
					}
	
					@Override
					public void operationComplete() {
						if (deletingBinders) {
							GwtClientHelper.getRequestInfo().setRefreshSidebarTree();
							FullUIReloadEvent.fireOne();
						}
						else {
							resetViewAsync();
						}
					}
					
					@Override
					public void operationFailed() {
						// Nothing to do.  The delete call will have told
						// the user about the failure.
					}
				});
			}
		}
	}
	
	/**
	 * Handles DeleteSelectedUsersEvent's received by this class.
	 * 
	 * Implements the DeleteSelectedUsersEvent.Handler.onDeleteSelectedUsers() method.
	 * 
	 * @param event
	 */
	@Override
	public void onDeleteSelectedUsers(DeleteSelectedUsersEvent event) {
		// Is the event targeted to this folder?
		Long eventWorkspaceId = event.getWorkspaceId();
		if (eventWorkspaceId.equals(getFolderId())) {
			// Yes!  Delete the users selected in the data table and
			// reset the view to redisplay things with the users
			// deleted.
			List<Long> selectedUserIds = EntityId.getLongsFromEntityIds(getSelectedEntityIds());
			BinderViewsHelper.deleteSelectedUsers(
					selectedUserIds,
					new DeleteUsersCallback() {
				@Override
				public void operationCanceled() {
					GwtClientHelper.getRequestInfo().setRefreshSidebarTree();
					FullUIReloadEvent.fireOne();
				}

				@Override
				public void operationComplete() {
					GwtClientHelper.getRequestInfo().setRefreshSidebarTree();
					FullUIReloadEvent.fireOne();
				}
				
				@Override
				public void operationFailed() {
					// Nothing to do.  The delete call will have told
					// the user about the failure.
				}
			});
		}
	}
	
	/**
	 * Called when the data table is detached.
	 * 
	 * Overrides the Widget.onDetach() method.
	 */
	@Override
	public void onDetach() {
		// Let the widget detach and then unregister our event
		// handlers.
		super.onDetach();
		unregisterEvents();
	}
	
	/**
	 * Handles DisableSelectedUsersEvent's received by this class.
	 * 
	 * Implements the DisableSelectedUsersEvent.Handler.onDisableSelectedUsers() method.
	 * 
	 * @param event
	 */
	@Override
	public void onDisableSelectedUsers(DisableSelectedUsersEvent event) {
		// Is the event targeted to this folder?
		Long eventWorkspaceId = event.getWorkspaceId();
		if (eventWorkspaceId.equals(getFolderId())) {
			// Yes!  Invoke the disable.
			BinderViewsHelper.disableUsers(
				EntityId.getLongsFromEntityIds(
					getSelectedEntityIds()));
		}
	}
	
	/**
	 * Handles DisableSelectedUsersAdHocFoldersEvent's received by this class.
	 * 
	 * Implements the DisableSelectedUsersAdHocFoldersEvent.Handler.onDisableSelectedUsersAdHocFolders() method.
	 * 
	 * @param event
	 */
	@Override
	public void onDisableSelectedUsersAdHocFolders(DisableSelectedUsersAdHocFoldersEvent event) {
		// Is the event targeted to this folder?
		Long eventWorkspaceId = event.getWorkspaceId();
		if (eventWorkspaceId.equals(getFolderId())) {
			// Yes!  Invoke the disable.
			List<EntityId> selectedEntityIds = event.getSelectedEntities();
			if (!(GwtClientHelper.hasItems(selectedEntityIds))) {
				selectedEntityIds = getSelectedEntityIds();
			}
			BinderViewsHelper.disableUsersAdHocFolders(
				EntityId.getLongsFromEntityIds(selectedEntityIds),
				new FullUIReloadEvent());
		}
	}
	
	/**
	 * Handles DisableSelectedUsersDownloadEvent's received by this class.
	 * 
	 * Implements the DisableSelectedUsersDownloadEvent.Handler.onDisableSelectedUsersDownload() method.
	 * 
	 * @param event
	 */
	@Override
	public void onDisableSelectedUsersDownload(DisableSelectedUsersDownloadEvent event) {
		// Is the event targeted to this folder?
		Long eventWorkspaceId = event.getWorkspaceId();
		if (eventWorkspaceId.equals(getFolderId())) {
			// Yes!  Invoke the disable.
			List<EntityId> selectedEntityIds = event.getSelectedEntities();
			if (!(GwtClientHelper.hasItems(selectedEntityIds))) {
				selectedEntityIds = getSelectedEntityIds();
			}
			BinderViewsHelper.disableUsersDownload(
				EntityId.getLongsFromEntityIds(selectedEntityIds),
				new FullUIReloadEvent());
		}
	}
	
	/**
	 * Handles DisableSelectedUsersWebAccessEvent's received by this class.
	 * 
	 * Implements the DisableSelectedUsersWebAccessEvent.Handler.onDisableSelectedUsersWebAccess() method.
	 * 
	 * @param event
	 */
	@Override
	public void onDisableSelectedUsersWebAccess(DisableSelectedUsersWebAccessEvent event) {
		// Is the event targeted to this folder?
		Long eventWorkspaceId = event.getWorkspaceId();
		if (eventWorkspaceId.equals(getFolderId())) {
			// Yes!  Invoke the disable.
			List<EntityId> selectedEntityIds = event.getSelectedEntities();
			if (!(GwtClientHelper.hasItems(selectedEntityIds))) {
				selectedEntityIds = getSelectedEntityIds();
			}
			BinderViewsHelper.disableUsersWebAccess(
				EntityId.getLongsFromEntityIds(selectedEntityIds),
				new FullUIReloadEvent());
		}
	}
	
	/**
	 * Handles DownloadFolderAsCSVFileEvent's received by this class.
	 * 
	 * Implements the DownloadFolderAsCSVFileEvent.Handler.onDownloadFolderAsCSVFile() method.
	 * 
	 * @param event
	 */
	@Override
	public void onDownloadFolderAsCSVFile(DownloadFolderAsCSVFileEvent event) {
		// Is the event targeted to this folder?
		Long dlFolderId    = event.getFolderId();
		Long eventFolderId = event.getHandleByFolderId();
		if (null == eventFolderId) {
			eventFolderId = dlFolderId;
		}
		if (eventFolderId.equals(getFolderId())) {
			// Yes!  Invoke the download.
			BinderViewsHelper.downloadFolderAsCSVFile(
				getDownloadPanel().getForm(),
				dlFolderId);
		}
	}
	
	/**
	 * Handles EditPublicLinkSelectedEntitiesEvent's received by this class.
	 * 
	 * Implements the EditPublicLinkSelectedEntitiesEvent.Handler.onEditPublicLinkSelectedEntities() method.
	 * 
	 * @param event
	 */
	@Override
	public void onEditPublicLinkSelectedEntities(EditPublicLinkSelectedEntitiesEvent event) {
		// Is the event targeted to this folder?
		Long eventFolderId = event.getFolderId();
		if (eventFolderId.equals(getFolderId())) {
			// Yes!  Does the user have rights to share everything
			// they've selected?
			List<EntityId> seList = event.getSelectedEntities();
			final boolean validateSelectedRows = (!(GwtClientHelper.hasItems(seList)));
			if (validateSelectedRows) {
				seList = getSelectedEntityIds();
			}
			
			final List<EntityId> selectedEntities = seList;
			GwtClientHelper.executeCommand(
					new GetEntityRightsCmd(selectedEntities),
					new AsyncCallback<VibeRpcResponse>() {
				@Override
				public void onFailure(Throwable caught) {
					GwtClientHelper.handleGwtRPCFailure(
						caught,
						GwtTeaming.getMessages().rpcFailure_GetEntityRights());
				}

				@Override
				public void onSuccess(VibeRpcResponse response) {
					EntityRightsRpcResponseData responseData = ((EntityRightsRpcResponseData) response.getResponseData());
					onEditPublicLinkSelectedEntitiesAsync(selectedEntities, responseData.getEntityRightsMap(), validateSelectedRows);
				}
			});
		}
	}

	/*
	 * Asynchronously processes the share request on the selected
	 * entries, given the current user's rights to them.
	 */
	private void onEditPublicLinkSelectedEntitiesAsync(final List<EntityId> selectedEntities, final Map<String, EntityRights> entityRightsMap, final boolean validateSelectedRows) {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				onEditPublicLinkSelectedEntitiesNow(selectedEntities, entityRightsMap, validateSelectedRows);
			}
		});
	}
	
	/*
	 * Synchronously processes the share request on the selected
	 * entries, given the current user's rights to them.
	 */
	private void onEditPublicLinkSelectedEntitiesNow(final List<EntityId> selectedEntities, final Map<String, EntityRights> entityRightsMap, boolean validateSelectedRows) {
		// Are there any invalid rows?
		final List<FolderRow> invalidRows = (validateSelectedRows ? validateSelectedRows_PublicLink(entityRightsMap) : null);
		if (!(GwtClientHelper.hasItems(invalidRows))) {
			// No!  Invoke the share.
			editSelectedEntitiesPublicLinkAsync(selectedEntities);
		}
		
		else {
			// No, they don't have rights to share everything!  What
			// type of share failures are we dealing with?
			int totalPLFailures    = invalidRows.size();
			int plFolderFailures   = BinderViewsHelper.getFolderPublicLinkFailureCount(selectedEntities);
			int plNonFileFailures  = countNonFileEntryRows(invalidRows);
			int otherShareFailures = (totalPLFailures - (plFolderFailures + plNonFileFailures));
			if (0 > otherShareFailures) {
				otherShareFailures = 0;
			}
			boolean hasPLFolderFailures  = (0 < plFolderFailures  );
			boolean hasPLNonFileFailures = (0 < plNonFileFailures );
			boolean hasOtherPLFailures   = (0 < otherShareFailures);
			
			// Can they share any of them?
			if (selectedEntities.size() == totalPLFailures) {
				// No!  Tell them about the problem and bail.
				String shareAlert;
				if      (hasPLFolderFailures && hasPLNonFileFailures && hasOtherPLFailures) shareAlert = m_messages.vibeDataTable_Warning_CantEditPublicLink_3();
				else if (hasPLFolderFailures && hasPLNonFileFailures)                       shareAlert = m_messages.vibeDataTable_Warning_CantEditPublicLink_2a();
				else if (                       hasPLNonFileFailures && hasOtherPLFailures) shareAlert = m_messages.vibeDataTable_Warning_CantEditPublicLink_2b();
				else if (hasPLFolderFailures &&                         hasOtherPLFailures) shareAlert = m_messages.vibeDataTable_Warning_CantEditPublicLink_2c();
 				else if (hasPLFolderFailures)                                               shareAlert = m_messages.vibeDataTable_Warning_CantEditPublicLink_1a();
				else if (                       hasPLNonFileFailures)                       shareAlert = m_messages.vibeDataTable_Warning_CantEditPublicLink_1b();
				else                                                                        shareAlert = m_messages.vibeDataTable_Warning_CantEditPublicLink_1c();
				GwtClientHelper.deferredAlert(shareAlert);
				return;
			}
			
			// Is the user sure they want to share the selections
			// they have rights to share?
			final String confirmPrompt;
			if      (hasPLFolderFailures && hasPLNonFileFailures && hasOtherPLFailures) confirmPrompt = m_messages.vibeDataTable_Confirm_CantEditPublicLink_3();
			else if (hasPLFolderFailures && hasPLNonFileFailures)                       confirmPrompt = m_messages.vibeDataTable_Confirm_CantEditPublicLink_2a();
			else if (                       hasPLNonFileFailures && hasOtherPLFailures) confirmPrompt = m_messages.vibeDataTable_Confirm_CantEditPublicLink_2b();
			else if (hasPLFolderFailures &&                         hasOtherPLFailures) confirmPrompt = m_messages.vibeDataTable_Confirm_CantEditPublicLink_2c();
			else if (hasPLFolderFailures)                                               confirmPrompt = m_messages.vibeDataTable_Confirm_CantEditPublicLink_1a();
			else if (                       hasPLNonFileFailures)                       confirmPrompt = m_messages.vibeDataTable_Confirm_CantEditPublicLink_1b();
			else                                                                        confirmPrompt = m_messages.vibeDataTable_Confirm_CantEditPublicLink_1c();
			ConfirmDlg.createAsync(new ConfirmDlgClient() {
				@Override
				public void onUnavailable() {
					// Nothing to do.  Error handled in
					// asynchronous provider.
				}
				
				@Override
				public void onSuccess(ConfirmDlg cDlg) {
					ConfirmDlg.initAndShow(
						cDlg,
						new ConfirmCallback() {
							@Override
							public void dialogReady() {
								// Ignored.  We don't really care when the
								// dialog is ready.
							}

							@Override
							public void accepted() {
								// Yes, they're sure!  Remove the
								// selection from the entries they
								// don't have rights to share and
								// perform the edit public link on the
								// rest.
								removeRowEntities(                  selectedEntities, invalidRows);
								deselectRows(                                         invalidRows);
								editSelectedEntitiesPublicLinkAsync(selectedEntities             );
							}

							@Override
							public void rejected() {
								// No, they're not sure!
							}
						},
						confirmPrompt);
				}
			});
		}
	}
	
	/**
	 * Handles EmailPublicLinkSelectedEntitiesEvent's received by this class.
	 * 
	 * Implements the EmailPublicLinkSelectedEntitiesEvent.Handler.onEmailPublicLinkSelectedEntities() method.
	 * 
	 * @param event
	 */
	@Override
	public void onEmailPublicLinkSelectedEntities(EmailPublicLinkSelectedEntitiesEvent event) {
		// Is the event targeted to this folder?
		Long eventFolderId = event.getFolderId();
		if (eventFolderId.equals(getFolderId())) {
			// Yes!  Does the user have rights to share everything
			// they've selected?
			List<EntityId> seList = event.getSelectedEntities();
			final boolean validateSelectedRows = (!(GwtClientHelper.hasItems(seList)));
			if (validateSelectedRows) {
				seList = getSelectedEntityIds();
			}
			
			final List<EntityId> selectedEntities = seList;
			GwtClientHelper.executeCommand(
					new GetEntityRightsCmd(selectedEntities),
					new AsyncCallback<VibeRpcResponse>() {
				@Override
				public void onFailure(Throwable caught) {
					GwtClientHelper.handleGwtRPCFailure(
						caught,
						GwtTeaming.getMessages().rpcFailure_GetEntityRights());
				}

				@Override
				public void onSuccess(VibeRpcResponse response) {
					EntityRightsRpcResponseData responseData = ((EntityRightsRpcResponseData) response.getResponseData());
					onEmailPublicLinkSelectedEntitiesAsync(selectedEntities, responseData.getEntityRightsMap(), validateSelectedRows);
				}
			});
		}
	}

	/*
	 * Asynchronously processes the share request on the selected
	 * entries, given the current user's rights to them.
	 */
	private void onEmailPublicLinkSelectedEntitiesAsync(final List<EntityId> selectedEntities, final Map<String, EntityRights> entityRightsMap, final boolean validateSelectedRows) {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				onEmailPublicLinkSelectedEntitiesNow(selectedEntities, entityRightsMap, validateSelectedRows);
			}
		});
	}
	
	/*
	 * Synchronously processes the share request on the selected
	 * entries, given the current user's rights to them.
	 */
	private void onEmailPublicLinkSelectedEntitiesNow(final List<EntityId> selectedEntities, final Map<String, EntityRights> entityRightsMap, boolean validateSelectedRows) {
		// Are there any invalid rows?
		final List<FolderRow> invalidRows = (validateSelectedRows ? validateSelectedRows_PublicLink(entityRightsMap) : null);
		if (!(GwtClientHelper.hasItems(invalidRows))) {
			// No!  Invoke the share.
			emailSelectedEntitiesPublicLinkAsync(selectedEntities);
		}
		
		else {
			// No, they don't have rights to share everything!  What
			// type of share failures are we dealing with?
			int totalPLFailures    = invalidRows.size();
			int plFolderFailures   = BinderViewsHelper.getFolderPublicLinkFailureCount(selectedEntities);
			int plNonFileFailures  = countNonFileEntryRows(invalidRows);
			int otherShareFailures = (totalPLFailures - (plFolderFailures + plNonFileFailures));
			if (0 > otherShareFailures) {
				otherShareFailures = 0;
			}
			boolean hasPLFolderFailures  = (0 < plFolderFailures  );
			boolean hasPLNonFileFailures = (0 < plNonFileFailures );
			boolean hasOtherPLFailures   = (0 < otherShareFailures);
			
			// Can they share any of them?
			if (selectedEntities.size() == totalPLFailures) {
				// No!  Tell them about the problem and bail.
				String shareAlert;
				if      (hasPLFolderFailures && hasPLNonFileFailures && hasOtherPLFailures) shareAlert = m_messages.vibeDataTable_Warning_CantEmailPublicLink_3();
				else if (hasPLFolderFailures && hasPLNonFileFailures)                       shareAlert = m_messages.vibeDataTable_Warning_CantEmailPublicLink_2a();
				else if (                       hasPLNonFileFailures && hasOtherPLFailures) shareAlert = m_messages.vibeDataTable_Warning_CantEmailPublicLink_2b();
				else if (hasPLFolderFailures &&                         hasOtherPLFailures) shareAlert = m_messages.vibeDataTable_Warning_CantEmailPublicLink_2c();
				else if (hasPLFolderFailures)                                               shareAlert = m_messages.vibeDataTable_Warning_CantEmailPublicLink_1a();
				else if (                       hasPLNonFileFailures)                       shareAlert = m_messages.vibeDataTable_Warning_CantEmailPublicLink_1b();
				else                                                                        shareAlert = m_messages.vibeDataTable_Warning_CantEmailPublicLink_1c();
				GwtClientHelper.deferredAlert(shareAlert);
				return;
			}
			
			// Is the user sure they want to share the selections
			// they have rights to share?
			final String confirmPrompt;
			if      (hasPLFolderFailures && hasPLNonFileFailures && hasOtherPLFailures) confirmPrompt = m_messages.vibeDataTable_Confirm_CantEmailPublicLink_3();
			else if (hasPLFolderFailures && hasPLNonFileFailures)                       confirmPrompt = m_messages.vibeDataTable_Confirm_CantEmailPublicLink_2a();
			else if (                       hasPLNonFileFailures && hasOtherPLFailures) confirmPrompt = m_messages.vibeDataTable_Confirm_CantEmailPublicLink_2b();
			else if (hasPLFolderFailures &&                         hasOtherPLFailures) confirmPrompt = m_messages.vibeDataTable_Confirm_CantEmailPublicLink_2c();
			else if (hasPLFolderFailures)                                               confirmPrompt = m_messages.vibeDataTable_Confirm_CantEmailPublicLink_1a();
			else if (                       hasPLNonFileFailures)                       confirmPrompt = m_messages.vibeDataTable_Confirm_CantEmailPublicLink_1b();
			else                                                                        confirmPrompt = m_messages.vibeDataTable_Confirm_CantEmailPublicLink_1c();
			ConfirmDlg.createAsync(new ConfirmDlgClient() {
				@Override
				public void onUnavailable() {
					// Nothing to do.  Error handled in
					// asynchronous provider.
				}
				
				@Override
				public void onSuccess(ConfirmDlg cDlg) {
					ConfirmDlg.initAndShow(
						cDlg,
						new ConfirmCallback() {
							@Override
							public void dialogReady() {
								// Ignored.  We don't really care when the
								// dialog is ready.
							}

							@Override
							public void accepted() {
								// Yes, they're sure!  Remove the
								// selection from the entries they
								// don't have rights to share and
								// perform the email public link on the
								// rest.
								removeRowEntities(                   selectedEntities, invalidRows);
								deselectRows(                                          invalidRows);
								emailSelectedEntitiesPublicLinkAsync(selectedEntities             );
							}

							@Override
							public void rejected() {
								// No, they're not sure!
							}
						},
						confirmPrompt);
				}
			});
		}
	}
	
	/**
	 * Handles EnableSelectedUsersEvent's received by this class.
	 * 
	 * Implements the EnableSelectedUsersEvent.Handler.onEnableSelectedUsers() method.
	 * 
	 * @param event
	 */
	@Override
	public void onEnableSelectedUsers(EnableSelectedUsersEvent event) {
		// Is the event targeted to this folder?
		Long eventWorkspaceId = event.getWorkspaceId();
		if (eventWorkspaceId.equals(getFolderId())) {
			// Yes!  Invoke the enable.
			BinderViewsHelper.enableUsers(
				EntityId.getLongsFromEntityIds(
					getSelectedEntityIds()));
		}
	}
	
	/**
	 * Handles EnableSelectedUsersAdHocFoldersEvent's received by this class.
	 * 
	 * Implements the EnableSelectedUsersAdHocFoldersEvent.Handler.onEnableSelectedUsersAdHocFolders() method.
	 * 
	 * @param event
	 */
	@Override
	public void onEnableSelectedUsersAdHocFolders(EnableSelectedUsersAdHocFoldersEvent event) {
		// Is the event targeted to this folder?
		Long eventWorkspaceId = event.getWorkspaceId();
		if (eventWorkspaceId.equals(getFolderId())) {
			// Yes!  Invoke the enable.
			List<EntityId> selectedEntityIds = event.getSelectedEntities();
			if (!(GwtClientHelper.hasItems(selectedEntityIds))) {
				selectedEntityIds = getSelectedEntityIds();
			}
			BinderViewsHelper.enableUsersAdHocFolders(
				EntityId.getLongsFromEntityIds(selectedEntityIds),
				new FullUIReloadEvent());
		}
	}
	
	/**
	 * Handles EnableSelectedUsersDownloadEvent's received by this class.
	 * 
	 * Implements the EnableSelectedUsersDownloadEvent.Handler.onEnableSelectedUsersDownload() method.
	 * 
	 * @param event
	 */
	@Override
	public void onEnableSelectedUsersDownload(EnableSelectedUsersDownloadEvent event) {
		// Is the event targeted to this folder?
		Long eventWorkspaceId = event.getWorkspaceId();
		if (eventWorkspaceId.equals(getFolderId())) {
			// Yes!  Invoke the enable.
			List<EntityId> selectedEntityIds = event.getSelectedEntities();
			if (!(GwtClientHelper.hasItems(selectedEntityIds))) {
				selectedEntityIds = getSelectedEntityIds();
			}
			BinderViewsHelper.enableUsersDownload(
				EntityId.getLongsFromEntityIds(selectedEntityIds),
				new FullUIReloadEvent());
		}
	}
	
	/**
	 * Handles EnableSelectedUsersWebAccessEvent's received by this class.
	 * 
	 * Implements the EnableSelectedUsersWebAccessEvent.Handler.onEnableSelectedUsersWebAccess() method.
	 * 
	 * @param event
	 */
	@Override
	public void onEnableSelectedUsersWebAccess(EnableSelectedUsersWebAccessEvent event) {
		// Is the event targeted to this folder?
		Long eventWorkspaceId = event.getWorkspaceId();
		if (eventWorkspaceId.equals(getFolderId())) {
			// Yes!  Invoke the enable.
			List<EntityId> selectedEntityIds = event.getSelectedEntities();
			if (!(GwtClientHelper.hasItems(selectedEntityIds))) {
				selectedEntityIds = getSelectedEntityIds();
			}
			BinderViewsHelper.enableUsersWebAccess(
				EntityId.getLongsFromEntityIds(selectedEntityIds),
				new FullUIReloadEvent());
		}
	}
	
	/**
	 * Handles ForceSelectedUsersToChangePasswordEvent's received by this class.
	 * 
	 * Implements the ForceSelectedUsersToChangePasswordEvent.Handler.onForceSelectedUsersToChangePassword() method.
	 * 
	 * @param event
	 */
	@Override
	public void onForceSelectedUsersToChangePassword(ForceSelectedUsersToChangePasswordEvent event) {
		// We only support forcing users to change their password from
		// the root personal workspace in management mode.  Is it
		// supported?
		WorkspaceType wt = getFolderInfo().getWorkspaceType();
		if (wt.isProfileRootManagement()) {
			// Yes!  Is the event targeted to this folder?
			Long eventBinderId = event.getBinderId();
			if (eventBinderId.equals(getFolderInfo().getBinderIdAsLong())) {
				// Yes!  Get the selected EntityId's...
				List<EntityId> selectedEntityIds = event.getSelectedEntities();
				if (!(GwtClientHelper.hasItems(selectedEntityIds))) {
					selectedEntityIds = getSelectedEntityIds();
				}
				
				// ...extract the selected user ID's from that...
				final List<Long> selectedPrincipalsList = new ArrayList<Long>();
				for (EntityId eid:  selectedEntityIds) {
					selectedPrincipalsList.add(eid.getEntityId());
				}
	
				// ...and force the users to change their password.
				onForceSelectedUsersToChangePasswordAsync(selectedPrincipalsList);
			}
		}
	}

	/*
	 * Asynchronously forces the selected users to change their
	 * password after their next successful login.
	 */
	private void onForceSelectedUsersToChangePasswordAsync(final List<Long> selectedPrincipalsList) {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				onForceSelectedUsersToChangePasswordNow(selectedPrincipalsList);
			}
		});
	}
	
	/*
	 * Synchronously forces the selected users to change their
	 * password after their next successful login.
	 */
	private void onForceSelectedUsersToChangePasswordNow(final List<Long> selectedPrincipalsList) {
	    showBusySpinner();
		ForceUsersToChangePasswordCmd cmd = new ForceUsersToChangePasswordCmd(selectedPrincipalsList);
		GwtClientHelper.executeCommand(cmd, new AsyncCallback<VibeRpcResponse>() {
			@Override
			public void onFailure(Throwable caught) {
			    hideBusySpinner();
				GwtClientHelper.handleGwtRPCFailure(
					caught,
					m_messages.rpcFailure_ForceUsersToChangePassword());
			}

			@Override
			public void onSuccess(VibeRpcResponse response) {
				// We're done.  If we had any errors...
			    hideBusySpinner();
			    ErrorListRpcResponseData responseData = ((ErrorListRpcResponseData) response.getResponseData()); 
				List<ErrorInfo> erList = responseData.getErrorList();
				if (GwtClientHelper.hasItems(erList)) {
					// ...display them...
					GwtClientHelper.displayMultipleErrors(m_messages.vibeDataTable_Error_ForcingPasswordChange(), erList);
				}
			}
		});
	}

	/**
	 * Handles HideSelectedSharesEvent's received by this class.
	 * 
	 * Implements the HideSelectedSharesEvent.Handler.onHideSelectedShares() method.
	 * 
	 * @param event
	 */
	@Override
	public void onHideSelectedShares(HideSelectedSharesEvent event) {
		// Is the event targeted to this folder?
		Long eventFolderId = event.getFolderId();
		if (eventFolderId.equals(getFolderId())) {
			// Yes!  Invoke the show selected shares.
			List<EntityId> selectedEntityIds = event.getSelectedEntities();
			if (!(GwtClientHelper.hasItems(selectedEntityIds))) {
				selectedEntityIds = getSelectedEntityIds();
			}
			BinderViewsHelper.hideSelectedShares(
				getFolderInfo().getCollectionType(),
				selectedEntityIds);
		}
	}
	
	/**
	 * Handles InvokeBinderShareRightsDlgEvent's received by this class.
	 * 
	 * Implements the InvokeBinderShareRightsDlgEvent.Handler.onInvokeBinderShareRightsDlg() method.
	 * 
	 * @param event
	 */
	@Override
	public void onInvokeBinderShareRightsDlg(final InvokeBinderShareRightsDlgEvent event) {
		// We only support setting binder rights from the root global
		// or team workspace workspace in management mode.  Is it
		// supported?
		WorkspaceType wt = getFolderInfo().getWorkspaceType();
		if (wt.isGlobalRoot() || wt.isTeamRoot()) {
			// Yes!  Is the event targeted to this folder?
			Long eventFolderId = event.getFolderId();
			if (eventFolderId.equals(getFolderInfo().getBinderIdAsLong())) {
				// Yes!  Have we create a binder share rights dialog
				// yet?
				if (null == m_binderShareRightsDlg) {
					// No!  Can we create one now?
					BinderShareRightsDlg.createAsync(new BinderShareRightsDlgClient() {
						@Override
						public void onUnavailable() {
							// Nothing to do.  Error handled in 
							// asynchronous provider.
						}
						
						@Override
						public void onSuccess(BinderShareRightsDlg usrDlg) {
							// Yes, we created the binder share rights
							// dialog!  Show it.
							m_binderShareRightsDlg = usrDlg;
							showBinderShareRightsDlgAsync(event.getBinderIds(), event.getShowRelativeTo());
						}
					});
				}
				
				else {
					// Yes, we have a binder share rights dialog!  Show
					// it.
					showBinderShareRightsDlgAsync(event.getBinderIds(), event.getShowRelativeTo());
				}
			}
		}
	}
	
	/**
	 * Handles InvokeColumnResizerEvent's received by this class.
	 * 
	 * Implements the InvokeColumnResizerEvent.Handler.onInvokeColumnResizer() method.
	 * 
	 * @param event
	 */
	@Override
	public void onInvokeColumnResizer(InvokeColumnResizerEvent event) {
		// Is the event targeted to this folder?
		BinderInfo eventBinderInfo = event.getBinderInfo();
		if (eventBinderInfo.isEqual(getFolderInfo())) {
			// Yes!  Invoke the column sizing dialog on the folder.
			// Have we instantiated a size columns dialog yet?
			if (null == m_sizeColumnsDlg) {
				// No!  Instantiate one now.
				SizeColumnsDlg.createAsync(new SizeColumnsDlgClient() {			
					@Override
					public void onUnavailable() {
						// Nothing to do.  Error handled in
						// asynchronous provider.
					}
					
					@Override
					public void onSuccess(final SizeColumnsDlg scDlg) {
						// ...and show it.
						m_sizeColumnsDlg = scDlg;
						GwtClientHelper.deferCommand(new ScheduledCommand() {
							@Override
							public void execute() {
								showSizeColumnsDlgNow();
							}
						});
					}
				});
			}
			
			else {
				// Yes, we've instantiated a size columns dialog
				// already!  Simply show it.
				showSizeColumnsDlgNow();
			}
		}
	}
	
	/**
	 * Handles InvokeCopyFiltersDlgEvent's received by this class.
	 * 
	 * Implements the InvokeCopyFiltersDlgEvent.Handler.onInvokeCopyFiltersDlg() method.
	 * 
	 * @param event
	 */
	@Override
	public void onInvokeCopyFiltersDlg(InvokeCopyFiltersDlgEvent event) {
		// Is the event targeted to this folder?
		BinderInfo eventFolderInfo = event.getFolderInfo();
		if (eventFolderInfo.isEqual(getFolderInfo())) {
			// Yes!  Invoke the copy filters dialog on the folder.
			onInvokeCopyFiltersDlgAsync(eventFolderInfo);
		}
	}

	/*
	 * Asynchronously invokes the copy filters dialog.
	 */
	private void onInvokeCopyFiltersDlgAsync(final BinderInfo folderInfo) {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				onInvokeCopyFiltersDlgNow(folderInfo);
			}
		} );
	}
	
	/*
	 * Synchronously invokes the copy filters dialog.
	 */
	private void onInvokeCopyFiltersDlgNow(final BinderInfo folderInfo) {
		BinderViewsHelper.invokeCopyFiltersDlg(folderInfo);
	}
		
	/**
	 * Handles InvokeDropBoxEvent's received by this class.
	 * 
	 * Implements the InvokeDropBoxEvent.Handler.onInvokeDropBox() method.
	 * 
	 * @param event
	 */
	@Override
	public void onInvokeDropBox(InvokeDropBoxEvent event) {
		// Is the event targeted to this folder?
		Long eventFolderId = event.getFolderId();
		if (eventFolderId.equals(getFolderId())) {
			// Yes!  Invoke the files drop box on the folder.
			BinderInfo fi = getFolderInfo();
			if (fi.isBinderCollection() && (CollectionType.MY_FILES.equals(fi.getCollectionType()))) {
				final GetMyFilesContainerInfoCmd cmd = new GetMyFilesContainerInfoCmd();
				GwtClientHelper.executeCommand(cmd, new AsyncCallback<VibeRpcResponse>() {
					@Override
					public void onFailure(Throwable caught) {
						GwtClientHelper.handleGwtRPCFailure(
							caught,
							GwtTeaming.getMessages().rpcFailure_GetMyFilesContainerInfo());
					}

					@Override
					public void onSuccess(VibeRpcResponse result) {
						onInvokeDropBoxAsync((BinderInfo) result.getResponseData());
					}
				});
			}
			else {
				onInvokeDropBoxAsync(fi);
			}
		}
	}

	/*
	 * Asynchronously invokes the drop box on the given folder.
	 */
	private void onInvokeDropBoxAsync(final BinderInfo dropTarget) {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				onInvokeDropBoxNow(dropTarget);
			}
		});
	}
	
	/*
	 * Synchronously invokes the drop box on the given folder.
	 */
	private void onInvokeDropBoxNow(BinderInfo dropTarget) {
		BinderViewsHelper.invokeDropBox(
			dropTarget,
			getEntryMenuPanel().getAddFilesMenuItem());
	}
	
	/**
	 * Handles InvokeEditInPlaceEvent's received by this class.
	 * 
	 * Implements the InvokeEditInPlaceEvent.Handler.onInvokeEditInPlace() method.
	 * 
	 * @param event
	 */
	@Override
	public void onInvokeEditInPlace(InvokeEditInPlaceEvent event) {
		// Is the event targeted to this folder?
		Long eventFolderId = event.getFolderId();
		if ((null != eventFolderId) && eventFolderId.equals(getFolderId())) {
			// Yes!  Run the edit on it.
			BinderViewsHelper.invokeEditInPlace(event);
		}
	}
	
	/**
	 * Handles InvokeSignGuestbookEvent's received by this class.
	 * 
	 * Implements the InvokeSignGuestbookEvent.Handler.onInvokeSignGuestbook() method.
	 * 
	 * @param event
	 */
	@Override
	public void onInvokeSignGuestbook(InvokeSignGuestbookEvent event) {
		// Is the event targeted to this folder?
		Long eventFolderId = event.getFolderId();
		if (eventFolderId.equals(getFolderId())) {
			// Yes!  Asynchronously invoke the guest book signing UI.
			GwtClientHelper.deferCommand(new ScheduledCommand() {
				@Override
				public void execute() {
					signGuestbook();
				}
			});
		}
	}
	
	/**
	 * Handles LockSelectedEntitiesEvent's received by this class.
	 * 
	 * Implements the LockSelectedEntitiesEvent.Handler.onLockSelectedEntities() method.
	 * 
	 * @param event
	 */
	@Override
	public void onLockSelectedEntities(LockSelectedEntitiesEvent event) {
		// Is the event targeted to this folder?
		Long eventFolderId = event.getFolderId();
		if (eventFolderId.equals(getFolderId())) {
			// Yes!  Invoke the lock.
			List<EntityId> selectedEntityIds = event.getSelectedEntities();
			if (!(GwtClientHelper.hasItems(selectedEntityIds))) {
				selectedEntityIds = getSelectedEntityIds();
			}
			BinderViewsHelper.lockEntries(selectedEntityIds);
		}
	}
	
	/**
	 * Handles MailToPublicLinkEntityEvent's received by this class.
	 * 
	 * Implements the MailToPublicLinkEntityEvent.Handler.onMailToPublicLinkEntity() method.
	 * 
	 * @param event
	 */
	@Override
	public void onMailToPublicLinkEntity(MailToPublicLinkEntityEvent event) {
		// Is the event targeted to this folder?
		Long eventFolderId = event.getFolderId();
		if (eventFolderId.equals(getFolderId())) {
			// Yes!  Mail the public link.
			mailToPublicLinkAsync(event.getEntityId());
		}
	}

	/**
	 * Handles ManageSharesSelectedEntitiesEvent's received by this class.
	 * 
	 * Implements the ManageSharesSelectedEntitiesEvent.Handler.onManageSharesSelectedEntities() method.
	 * 
	 * @param event
	 */
	@Override
	public void onManageSharesSelectedEntities(ManageSharesSelectedEntitiesEvent event) {
		// Is the event targeted to this folder?
		Long eventFolderId = event.getFolderId();
		if (eventFolderId.equals( getFolderId())) {
			// Yes!
			List<EntityId> seList = event.getSelectedEntities();
			if (!(GwtClientHelper.hasItems(seList))) {
				seList = getSelectedEntityIds();
			}
			final List<EntityId> selectedEntities = seList;

			// Invoke the Manage Shares dialog
			GwtClientHelper.deferCommand(new ScheduledCommand() {
				@Override
				public void execute() {
					invokeManageSharesDlgSelectedEntities(selectedEntities);
				}
			});
		}
	}
	
	/**
	 * Handles MarkFolderContentsReadEvent's received by this class.
	 * 
	 * Implements the MarkFolderContentsReadEvent.Handler.onMarkFolderContentsRead() method.
	 * 
	 * @param event
	 */
	@Override
	public void onMarkFolderContentsRead(MarkFolderContentsReadEvent event) {
		// Is the event targeted to this folder?
		Long folderId    = event.getFolderId();
		Long eventFolderId = event.getHandleByFolderId();
		if (null == eventFolderId) {
			eventFolderId = folderId;
		}
		if (eventFolderId.equals(getFolderId())) {
			// Yes!  Mark the folder contents as having been read.
			BinderViewsHelper.markFolderContentsRead(folderId);
		}
	}
	
	/**
	 * Handles MarkFolderContentsUnreadEvent's received by this class.
	 * 
	 * Implements the MarkFolderContentsUnreadEvent.Handler.onMarkFolderContentsUnread() method.
	 * 
	 * @param event
	 */
	@Override
	public void onMarkFolderContentsUnread(MarkFolderContentsUnreadEvent event) {
		// Is the event targeted to this folder?
		Long folderId    = event.getFolderId();
		Long eventFolderId = event.getHandleByFolderId();
		if (null == eventFolderId) {
			eventFolderId = folderId;
		}
		if (eventFolderId.equals(getFolderId())) {
			// Yes!  Mark the folder contents as having been unread.
			BinderViewsHelper.markFolderContentsUnread(folderId);
		}
	}
	
	/**
	 * Handles MarkReadSelectedEntitiesEvent's received by this class.
	 * 
	 * Implements the MarkReadSelectedEntitiesEvent.Handler.onMarkReadSelectedEntities() method.
	 * 
	 * @param event
	 */
	@Override
	public void onMarkReadSelectedEntities(MarkReadSelectedEntitiesEvent event) {
		// Is the event targeted to this folder?
		Long eventFolderId = event.getFolderId();
		if (eventFolderId.equals(getFolderId())) {
			// Yes!  Invoke the mark entries read.
			List<EntityId> selectedEntityIds = event.getSelectedEntities();
			if (!(GwtClientHelper.hasItems(selectedEntityIds))) {
				selectedEntityIds = getSelectedEntityIds();
			}
			BinderViewsHelper.markEntriesRead(selectedEntityIds);
		}
	}
	
	/**
	 * Handles MarkUnreadSelectedEntitiesEvent's received by this class.
	 * 
	 * Implements the MarkUnreadSelectedEntitiesEvent.Handler.onMarkUnreadSelectedEntities() method.
	 * 
	 * @param event
	 */
	@Override
	public void onMarkUnreadSelectedEntities(MarkUnreadSelectedEntitiesEvent event) {
		// Is the event targeted to this folder?
		Long eventFolderId = event.getFolderId();
		if (eventFolderId.equals(getFolderId())) {
			// Yes!  Invoke the mark entries read.
			List<EntityId> selectedEntityIds = event.getSelectedEntities();
			if (!(GwtClientHelper.hasItems(selectedEntityIds))) {
				selectedEntityIds = getSelectedEntityIds();
			}
			BinderViewsHelper.markEntriesUnread(selectedEntityIds);
		}
	}
	
	/**
	 * Handles MobileDeviceWipeScheduleStateChangedEvent's received by this class.
	 * 
	 * Implements the MobileDeviceWipeScheduleStateChangedEvent.Handler.onMobileDeviceWipeScheduleStateChanged() method.
	 * 
	 * @param event
	 */
	@Override
	public void onMobileDeviceWipeScheduleStateChanged(MobileDeviceWipeScheduleStateChangedEvent event) {
		// If the event doesn't have a wipe schedule... 
		MobileDeviceWipeScheduleInfo wipeSchedule = event.getWipeSchedule();
		if (null == wipeSchedule) {
			// ...ignore it.
			return;
		}
		
		// Is the event targeted to this folder?
		Long eventFolderId = wipeSchedule.getEntityId().getBinderId();
		if (eventFolderId.equals(getFolderId())) {
			// Yes!  Can we find the associated row and column?
			FolderRow    fr = getRowByEntityId(wipeSchedule.getEntityId());
			FolderColumn fc = getDeviceWipeScheduleColumn();
			if ((null != fr) && (null != fc)) {
				// Yes!  Update the column values in the row based on
				// wipe schedule from the event.
				fr.setColumnWipeScheduled(fc, wipeSchedule.isWipeScheduled());
				fr.setColumnValue(        fc, wipeSchedule.getDisplay()     );
			}
		}
	}
	
	/**
	 * Handles MoveSelectedEntitiesEvent's received by this class.
	 * 
	 * Implements the MoveSelectedEntitiesEvent.Handler.onMoveSelectedEntities() method.
	 * 
	 * @param event
	 */
	@Override
	public void onMoveSelectedEntities(MoveSelectedEntitiesEvent event) {
		// Is the event targeted to this folder?
		Long eventFolderId = event.getFolderId();
		if (eventFolderId.equals(getFolderId())) {
			// Yes!  If the event doesn't contain any entities...
			List<EntityId> selectedEntityIds = event.getSelectedEntities();
			if (!(GwtClientHelper.hasItems(selectedEntityIds))) {
				// ...invoke the move on what's selected in the data
				// ...table.
				selectedEntityIds = getSelectedEntityIds();
				BinderViewsHelper.moveEntries(selectedEntityIds);
			}
		}
	}
	
	/**
	 * Synchronously sets the size of the data table based on its
	 * position in the view.
	 * 
	 * Overrides the ViewBase.onResize() method.
	 */
	@Override
	public void onResize() {
		// Pass the resize on to the super class...
		super.onResize();
		if (m_dataTable!=null) {
			// ...and do what we need to do locally.
			onResizeAsync(m_dataTable);
		}
	}

	/*
	 * Asynchronously performs the local resizing necessary.
	 */
	@SuppressWarnings("unused")
	private void onResizeAsync(final CellTable<FolderRow> ct) {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				onResizeNow(ct);
			}
		});
	}
	
	/*
	 * Asynchronously performs the local resizing necessary.
	 */
	private void onResizeAsync(final DataGrid<FolderRow> dg) {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				onResizeNow(dg);
			}
		});
	}

	/*
	 * Synchronously performs the local resizing necessary.
	 */
	private void onResizeNow(final CellTable<FolderRow> ct) {
		// Nothing to do.
	}
	
	/*
	 * Synchronously performs the local resizing necessary.
	 */
	private void onResizeNow(final DataGrid<FolderRow> dg) {
		FooterPanel fp = getFooterPanel();
		
		int viewHeight		= getOffsetHeight();							// Height of the view.
		int viewTop			= getAbsoluteTop();								// Absolute top of the view.		
		int dtTop			= (dg.getAbsoluteTop() - viewTop);				// Top of the data table relative to the top of the view.		
		int dtPagerHeight	= m_dataTablePager.getOffsetHeight();			// Height of the data table's pager.
		int fpHeight		= ((null == fp) ? 0 : fp.getOffsetHeight());	// Height of the view's footer panel.
		int totalBelow		= (dtPagerHeight + fpHeight);					// Total space on the page below the data table.

		// What's the optimum height for the data table so we don't get
		// a vertical scroll bar?
		int dataTableHeight = (((viewHeight - dtTop) - totalBelow) - getNoVScrollAdjustment());
		int minHeight       = getMinimumContentHeight();
		if (minHeight > dataTableHeight) {
			// Too small!  Use the minimum even though this will turn
			// on the vertical scroll bar.
			dataTableHeight = minHeight;
		}
		
		// Set the height of the data table.
		dg.setHeight(dataTableHeight + "px");
	}

	
	/**
	 * Handles QuickFilterEvent's received by this class.
	 * 
	 * Implements the QuickFilterEvent.Handler.onQuickFilter() method.
	 * 
	 * @param event
	 */
	@Override
	public void onQuickFilter(QuickFilterEvent event) {
		// Is the event is targeted to the folder we're viewing?
		if (event.getFolderId().equals(getFolderInfo().getBinderIdAsLong())) {
			// Yes!  Track the current quick filter and force the data
			// table to refresh with it.
			m_quickFilter = event.getQuickFilter();
			resetDataTableAsync();
		}
	}

	/*
	 * Called to read and process a block of rows.
	 */
	private void onRangeChangedImpl(final AbstractCellTable<FolderRow> vdt, final Long folderId, final Range range, final String authenticationGuid) {
		final int     rowsRequested         = range.getLength();
		final boolean hasAuthenticationGuid = GwtClientHelper.hasString(authenticationGuid);
		GwtClientHelper.executeCommand(
				new GetFolderRowsCmd(
					getFolderInfo(),
					getFolderDisplayData(),
					m_folderColumnsList,
					range.getStart(),
					rowsRequested,
					m_quickFilter,
					authenticationGuid),
				new AsyncCallback<VibeRpcResponse>() {
			@Override
			public void onFailure(Throwable t) {
				// If we have a Cloud Folder authentication dialog
				// visible...
				if (hasAuthenticationGuid) {
					// ...hide it.
					m_cfaDlg.hide();
				}
				
				GwtClientHelper.handleGwtRPCFailure(
					t,
					m_messages.rpcFailure_GetFolderRows(),
					folderId);
				
				// If we have an entry menu...
				EntryMenuPanel emp = getEntryMenuPanel();
				if (null != emp) {
					// ...tell it to update the state of its items that
					// ...require entries be available.
					EntryMenuPanel.setEntriesAvailable(emp, false);
				}

				// ...and display a now items message.
				displayEmptyDataTable(vdt);
			}
			
			@Override
			public void onSuccess(VibeRpcResponse response) {
				// If we have a Cloud Folder authentication dialog
				// visible...
				if (hasAuthenticationGuid) {
					// ...hide it.
					m_cfaDlg.hide();
				}
				
				// Did querying the folder's rows generate an errors?
				FolderRowsRpcResponseData responseData = ((FolderRowsRpcResponseData) response.getResponseData());
				if (responseData.hasErrors()) {
					// Yes!  Display them.
					GwtClientHelper.displayMultipleErrors(m_messages.vibeDataTable_Error_GetFolderRows(), responseData.getErrorList());
				}
				
				// Does querying the folder's rows require the user
				// to authenticate?
				if (responseData.requiresCloudFolderAuthentication()) {
					// Yes!  Are we currently processing an authentication request?
					if (hasAuthenticationGuid) {
						// Yes!  That should never happen.  Not sure
						// how we should handle this.  Tell the user
						// about the problem...
						GwtClientHelper.deferredAlert(
							m_messages.vibeDataTable_InternalError_NestedCloudFolderAuthentication());
						
						// ...and display an empty data table.
						displayEmptyDataTable(vdt);
						return;
					}
					
					// Run the authentication dialog.
					runCloudFolderAuthenticationDlgAsync(
						responseData.getCloudFolderAuthentication(),
						vdt,
						folderId,
						range);
				}
				
				else {
					// Did we read more rows than we asked for?
					m_contributorIds = responseData.getContributorIds();
					List<FolderRow> folderRows = responseData.getFolderRows();
					int rowsRead = folderRows.size();
					if (rowsRead > rowsRequested) {
						// Yes!  This should only happen with pinned
						// entries.  Assert that's the case...
						GwtClientHelper.debugAssert(
							isPinning(),
							m_messages.vibeDataTable_InternalError_UnexpectedRowCount(
								rowsRequested,
								rowsRead));
						
						// ...and use the entries read as the new page
						// ...size.
						vdt.setPageSize(rowsRead);
					}
					
					// Apply the rows we read.
					TotalCountType tct = responseData.getTotalCountType();
					m_dataTablePager.setTotalCountType(tct);
					vdt.setRowData( responseData.getStartOffset(), folderRows   );
					vdt.setRowCount(responseData.getTotalRows(),   tct.isExact());
					
					// If we have an entry menu...
					EntryMenuPanel emp = getEntryMenuPanel();
					if (null != emp) {
						// ...tell it to update the state of its items that
						// ...require entries be available.
						EntryMenuPanel.setEntriesAvailable(emp, (0 < rowsRead));
					}

					// Does the browser support uploads using HTML5?
					if (GwtClientHelper.jsBrowserSupportsHtml5FileAPIs()) {
						// Yes!  Then we need to process the nested
						// folders in the rows so that we can determine
						// whether to make them drop targets.
						getNestedFolderRightsAsync(folderRows);
					}

					// Allow the view's that extend this do what ever
					// they need to do once a collection of rows has
					// been rendered.
					postProcessRowDataAsync(folderRows);
				}
			}
		});
	}
	
	/**
	 * Handles SetSelectedBinderShareRightsEvent's received by this class.
	 * 
	 * Implements the SetSelectedBinderShareRightsEvent.Handler.onSetSelectedBinderShareRights() method.
	 * 
	 * @param event
	 */
	@Override
	public void onSetSelectedBinderShareRights(SetSelectedBinderShareRightsEvent event) {
		// We only support setting binder rights from the root global
		// or team workspace workspace in management mode.  Is it
		// supported?
		WorkspaceType wt = getFolderInfo().getWorkspaceType();
		if (wt.isGlobalRoot() || wt.isTeamRoot()) {
			// Yes!  Is the event targeted to this folder?
			Long eventFolderId = event.getFolderId();
			if (eventFolderId.equals(getFolderInfo().getBinderIdAsLong())) {
				// Yes!  Get the selected EntityId's...
				List<EntityId> selectedEntityIds = event.getSelectedEntities();
				if (!(GwtClientHelper.hasItems(selectedEntityIds))) {
					selectedEntityIds = getSelectedEntityIds();
				}
				
				// ...extract the selected user ID's from that...
				final List<Long> selectedBinderList = new ArrayList<Long>();
				for (EntityId eid:  selectedEntityIds) {
					selectedBinderList.add(eid.getEntityId());
				}
	
				// ...and invoke the binder share rights dialog.
				GwtTeaming.fireEventAsync(
					new InvokeBinderShareRightsDlgEvent(
						eventFolderId,
						selectedBinderList));
			}
		}
	}

	/**
	 * Handles SetSelectedPrincipalsAdminRightsEvent's received by this class.
	 * 
	 * Implements the SetSelectedPrincipalsAdminRightsEvent.Handler.onSetSelectedPrincipalsAdminRights() method.
	 * 
	 * @param event
	 */
	@Override
	public void onSetSelectedPrincipalsAdminRights(SetSelectedPrincipalsAdminRightsEvent event) {
		// We only support setting binder rights from the root personal
		// workspace in management mode or manage administrators.  Is
		// it supported?
		WorkspaceType wt = getFolderInfo().getWorkspaceType();
		if (wt.isAdministratorManagement() || wt.isProfileRootManagement()) {
			// Yes!  Is the event targeted to this folder?
			Long eventBinderId = event.getBinderId();
			if (eventBinderId.equals(getFolderInfo().getBinderIdAsLong())) {
				// Yes!  Get the selected EntityId's...
				List<EntityId> selectedEntityIds = event.getSelectedEntities();
				if (!(GwtClientHelper.hasItems(selectedEntityIds))) {
					selectedEntityIds = getSelectedEntityIds();
				}
				
				// ...extract the selected user ID's from that...
				final List<Long> selectedPrincipalsList = new ArrayList<Long>();
				for (EntityId eid:  selectedEntityIds) {
					selectedPrincipalsList.add(eid.getEntityId());
				}
	
				// ...and perform the rights set.
				onSetSelectedPrincipalsAdminRightsAsync(selectedPrincipalsList, event.isSetRights());
			}
		}
	}

	/*
	 * Asynchronously sets or clears the admin rights on the selected
	 * principals.
	 */
	private void onSetSelectedPrincipalsAdminRightsAsync(final List<Long> selectedPrincipalsList, final boolean setRights) {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				onSetSelectedPrincipalsAdminRightsNow(selectedPrincipalsList, setRights);
			}
		});
	}
	
	/*
	 * Synchronously sets or clears the admin rights on the selected
	 * principals.
	 */
	private void onSetSelectedPrincipalsAdminRightsNow(final List<Long> selectedPrincipalsList, final boolean setRights) {
	    showBusySpinner();
		SetPrincipalsAdminRightsCmd cmd = new SetPrincipalsAdminRightsCmd(selectedPrincipalsList, setRights);
		GwtClientHelper.executeCommand(cmd, new AsyncCallback<VibeRpcResponse>() {
			@Override
			public void onFailure(Throwable caught) {
			    hideBusySpinner();
				GwtClientHelper.handleGwtRPCFailure(
					caught,
					m_messages.rpcFailure_SetPrincipalsAdminRights());
			}

			@Override
			public void onSuccess(VibeRpcResponse response) {
				// We're done.  If we had any errors...
			    hideBusySpinner();
			    SetPrincipalsAdminRightsRpcResponseData responseData = ((SetPrincipalsAdminRightsRpcResponseData) response.getResponseData()); 
				List<ErrorInfo> erList = responseData.getErrorList();
				if (GwtClientHelper.hasItems(erList)) {
					// ...display them...
					GwtClientHelper.displayMultipleErrors(m_messages.vibeDataTable_Error_SavingAdminRights(), erList);
				}

				// ...and if we changed anything...
				final Map<Long, AdminRights> adminRightsChangeMap = responseData.getAdminRightsChangeMap(); 
				if (GwtClientHelper.hasItems(adminRightsChangeMap)) {
					// ...force the rows that changed to refresh to
					// ...reflect the change.
					GwtClientHelper.deferCommand(new ScheduledCommand() {
						@Override
						public void execute() {
							// If we're removing rights in the manage
							// administrators view...
							if ((!setRights) && getFolderInfo().isBinderAdministratorManagement()) {
								// ...force the full UI to refresh...
								FullUIReloadEvent.fireOneAsync();
							}
							else {
								// ...otherwise, update the rows that
								// ...were changed.
								List<FolderRow> rows     = m_dataTable.getVisibleItems();
								FolderColumn	adminCol = getColumnByName(m_folderColumnsList, FolderColumn.COLUMN_ADMIN_RIGHTS  );
								FolderColumn	ptCol    = getColumnByName(m_folderColumnsList, FolderColumn.COLUMN_PRINCIPAL_TYPE);
								Set<Long>		keySet   = adminRightsChangeMap.keySet();
								for (Long key:  keySet) {
									int rowIndex = 0;
									for (FolderRow row : rows) {
										EntityId rowEID = row.getEntityId();
										if (rowEID.getEntityId().equals(key)) {
											AdminRights ar = adminRightsChangeMap.get(key);
											row.setColumnValue(                    adminCol,       ar.getAdminRights());
											row.getColumnValueAsPrincipalAdminType(ptCol).setAdmin(ar.isAdmin()       );
											m_dataTable.redrawRow(rowIndex);
											break;
										}
										rowIndex += 1;
									}
								}
							}
						}
					});
				}
			}
		});
	}

	/**
	 * Handles SetSelectedPrincipalsLimitedUserVisibilityEvent's received by this class.
	 * 
	 * Implements the SetSelectedPrincipalsLimitedUserVisibilityEvent.Handler.onSetSelectedPrincipalsLimitedUserVisibility() method.
	 * 
	 * @param event
	 */
	@Override
	public void onSetSelectedPrincipalsLimitedUserVisibility(SetSelectedPrincipalsLimitedUserVisibilityEvent event) {
		// We only support setting limited user visibility from the
		// limited user visibility view.  Is it supported?
		WorkspaceType wt = getFolderInfo().getWorkspaceType();
		if (wt.isLimitUserVisibility()) {
			// Yes!  Get the selected EntityId's...
			List<Long> selectedPrincipalsList;
			if (event.isSelectPrincipal()) {
				selectedPrincipalsList = null;
			}
			else {
				List<EntityId> selectedEntityIds = event.getSelectedEntities();
				if (!(GwtClientHelper.hasItems(selectedEntityIds))) {
					selectedEntityIds = getSelectedEntityIds();
				}
				
				// ...extract the selected user ID's from that...
				selectedPrincipalsList = new ArrayList<Long>();
				for (EntityId eid:  selectedEntityIds) {
					selectedPrincipalsList.add(eid.getEntityId());
				}
			}
			final List<Long> finalPrincipalsList = selectedPrincipalsList;

			// ...and perform the rights set.
			onSetSelectedPrincipalsLimitedUserVisibilityAsync(finalPrincipalsList, event.getLimited(), event.getOverride(), event.isSelectPrincipal());
		}
	}

	/*
	 * Asynchronously sets or clears the limited user visibility
	 * settings on the selected principals.
	 */
	private void onSetSelectedPrincipalsLimitedUserVisibilityAsync(final List<Long> selectedPrincipalsList, final Boolean limited, final Boolean override, final boolean selectPrincipal) {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				// Note that the 'select principal' variation of this
				// event is handled in LimitUserVisibilityDlg.java.
				if (!selectPrincipal) {
					onSetSelectedPrincipalsLimitedUserVisibilityNow(selectedPrincipalsList, limited, override);
				}
			}
		});
	}
	
	/*
	 * Synchronously sets or clears the limited user visibility
	 * settings on the selected principals.
	 */
	private void onSetSelectedPrincipalsLimitedUserVisibilityNow(final List<Long> selectedPrincipalsList, final Boolean limited, final Boolean override) {
	    showBusySpinner();
		SetUserVisibilityCmd cmd = new SetUserVisibilityCmd(selectedPrincipalsList, limited, override);
		GwtClientHelper.executeCommand(cmd, new AsyncCallback<VibeRpcResponse>() {
			@Override
			public void onFailure(Throwable caught) {
			    hideBusySpinner();
				GwtClientHelper.handleGwtRPCFailure(
					caught,
					m_messages.rpcFailure_SetPrincipalsLimitedUserVisibility());
			}

			@Override
			public void onSuccess(VibeRpcResponse response) {
				// We're done.  If we had any errors...
			    hideBusySpinner();
			    SetLimitedUserVisibilityRpcResponseData responseData = ((SetLimitedUserVisibilityRpcResponseData) response.getResponseData()); 
				List<ErrorInfo> erList = responseData.getErrorList();
				if (GwtClientHelper.hasItems(erList)) {
					// ...display them...
					GwtClientHelper.displayMultipleErrors(m_messages.vibeDataTable_Error_SavingLimitedUserVisibility(), erList);
				}

				// ...and if we changed anything...
				final Map<Long, LimitedUserVisibilityInfo> luvChangeMap = responseData.getLimitedUserVisibilityChangeMap(); 
				if (GwtClientHelper.hasItems(luvChangeMap)) {
					// ...force the rows that changed to refresh to
					// ...reflect the change.
					GwtClientHelper.deferCommand(new ScheduledCommand() {
						@Override
						public void execute() {
							// If we're removing settings in the limit
							// user visibility view...
							if ((((null != limited) && (!limited)) || ((null != override) && (!override))) && getFolderInfo().isBinderLimitUserVisibility()) {
								// ...force the full UI to refresh...
								FullUIReloadEvent.fireOneAsync();
							}
							else {
								// ...otherwise, update the rows that
								// ...were changed.
								List<FolderRow> rows          = m_dataTable.getVisibleItems();
								FolderColumn	limitationCol = getColumnByName(m_folderColumnsList, FolderColumn.COLUMN_CAN_ONLY_SEE_MEMBERS);
								Set<Long>		keySet        = luvChangeMap.keySet();
								for (Long key:  keySet) {
									int rowIndex = 0;
									for (FolderRow row:  rows) {
										EntityId rowEID = row.getEntityId();
										if (rowEID.getEntityId().equals(key)) {
											row.setColumnValue(limitationCol, luvChangeMap.get(key));
											m_dataTable.redrawRow(rowIndex);
											break;
										}
										rowIndex += 1;
									}
								}
							}
						}
					});
				}
			}
		});
	}

	/**
	 * Handles SharedViewFilterEvent's received by this class.
	 * 
	 * Implements the SharedViewFilterEvent.Handler.onSharedViewFilter() method.
	 * 
	 * @param event
	 */
	@Override
	public void onSharedViewFilter(SharedViewFilterEvent event) {
		// Toggle the appropriate state...
		EntryMenuPanel	emp = getEntryMenuPanel();
		SharedViewState	svs = emp.getSharedViewState().createCopy();
		switch (event.getSharedViewFilter()) {
		case SHOW_HIDDEN:     svs.setShowHidden(   !(svs.isShowHidden()   ));  break;
		case SHOW_NON_HIDDEN: svs.setShowNonHidden(!(svs.isShowNonHidden()));  break;
		}

		// ...save it...
		GwtClientHelper.executeCommand(
				new SaveSharedViewStateCmd(getFolderInfo().getCollectionType(), svs),
				new AsyncCallback<VibeRpcResponse>() {
			@Override
			public void onFailure(Throwable t) {
				// No!  Tell the user about the problem...
				GwtClientHelper.handleGwtRPCFailure(
					t,
					m_messages.rpcFailure_SaveSharedViewState());
			}

			@Override
			public void onSuccess(VibeRpcResponse result) {
				// ...and force a UI refresh.
				FullUIReloadEvent.fireOneAsync();
			}
		});
	}

	/**
	 * Handles ShareSelectedEntitiesEvent's received by this class.
	 * 
	 * Implements the ShareSelectedEntitiesEvent.Handler.onShareSelectedEntities() method.
	 * 
	 * @param event
	 */
	@Override
	public void onShareSelectedEntities(ShareSelectedEntitiesEvent event) {
		// Is the event targeted to this folder?
		Long eventFolderId = event.getFolderId();
		if (eventFolderId.equals(getFolderId())) {
			// Yes!  Does the user have rights to share everything
			// they've selected?
			List<EntityId> seList = event.getSelectedEntities();
			final boolean validateSelectedRows = (!(GwtClientHelper.hasItems(seList)));
			if (validateSelectedRows) {
				seList = getSelectedEntityIds();
			}
			
			final List<EntityId> selectedEntities = seList;
			GwtClientHelper.executeCommand(
					new GetEntityRightsCmd(selectedEntities),
					new AsyncCallback<VibeRpcResponse>() {
				@Override
				public void onFailure(Throwable caught) {
					GwtClientHelper.handleGwtRPCFailure(
						caught,
						GwtTeaming.getMessages().rpcFailure_GetEntityRights());
				}

				@Override
				public void onSuccess(VibeRpcResponse response) {
					EntityRightsRpcResponseData responseData = ((EntityRightsRpcResponseData) response.getResponseData());
					onShareSelectedEntitiesAsync(selectedEntities, responseData.getEntityRightsMap(), validateSelectedRows);
				}
			});
		}
	}

	/*
	 * Asynchronously processes the share request on the selected
	 * entries, given the current user's rights to them.
	 */
	private void onShareSelectedEntitiesAsync(final List<EntityId> selectedEntities, final Map<String, EntityRights> entityRightsMap, final boolean validateSelectedRows) {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				onShareSelectedEntitiesNow(selectedEntities, entityRightsMap, validateSelectedRows);
			}
		});
	}
	
	/*
	 * Synchronously processes the share request on the selected
	 * entries, given the current user's rights to them.
	 */
	private void onShareSelectedEntitiesNow(final List<EntityId> selectedEntities, final Map<String, EntityRights> entityRightsMap, boolean validateSelectedRows) {
		// Are there any invalid rows?
		final List<FolderRow> invalidRows = (validateSelectedRows ? validateSelectedRows_Sharing(entityRightsMap) : null);
		if (!(GwtClientHelper.hasItems(invalidRows))) {
			// No!  Invoke the share.
			shareSelectedEntitiesAsync(selectedEntities);
		}
		
		else {
			// No, they don't have rights to share everything!  What
			// type of share failures are we dealing with?
			int totalShareFailures = invalidRows.size();
			int nfShareFailures    = BinderViewsHelper.getNetFolderShareFailureCount(selectedEntities, entityRightsMap);
			int otherShareFailures = (totalShareFailures - nfShareFailures);
			if (0 > otherShareFailures) {
				otherShareFailures = 0;
			}
			boolean hasNFShareFailures    = (0 < nfShareFailures   );
			boolean hasOtherShareFailures = (0 < otherShareFailures);
			
			// Can they share any of them?
			if (selectedEntities.size() == totalShareFailures) {
				// No!  Tell them about the problem and bail.
				String shareAlert;
				if      (hasNFShareFailures && hasOtherShareFailures) shareAlert = m_messages.vibeDataTable_Warning_ShareNoRightsAndNetFolders();
				else if (hasNFShareFailures)                          shareAlert = m_messages.vibeDataTable_Warning_ShareNetFolders();
				else                                                  shareAlert = m_messages.vibeDataTable_Warning_ShareNoRights();
				GwtClientHelper.deferredAlert(shareAlert);
				return;
			}
			
			// Is the user sure they want to share the selections
			// they have rights to share?
			final String confirmPrompt;
			if      (hasNFShareFailures && hasOtherShareFailures) confirmPrompt = m_messages.vibeDataTable_Confirm_CantShareNoRightsAndNetFolders();
			else if (hasNFShareFailures)                          confirmPrompt = m_messages.vibeDataTable_Confirm_CantShareNetFolders();
			else                                                  confirmPrompt = m_messages.vibeDataTable_Confirm_CantShareNoRights();
			ConfirmDlg.createAsync(new ConfirmDlgClient() {
				@Override
				public void onUnavailable() {
					// Nothing to do.  Error handled in
					// asynchronous provider.
				}
				
				@Override
				public void onSuccess(ConfirmDlg cDlg) {
					ConfirmDlg.initAndShow(
						cDlg,
						new ConfirmCallback() {
							@Override
							public void dialogReady() {
								// Ignored.  We don't really care when the
								// dialog is ready.
							}

							@Override
							public void accepted() {
								// Yes, they're sure!  Remove the
								// selection from the entries they
								// don't have rights to share and
								// perform the share on the rest.
								removeRowEntities(         selectedEntities, invalidRows);
								deselectRows(                                invalidRows);
								shareSelectedEntitiesAsync(selectedEntities             );
							}

							@Override
							public void rejected() {
								// No, they're not sure!
							}
						},
						confirmPrompt);
				}
			});
		}
	}
	
	/**
	 * Handles ShowSelectedSharesEvent's received by this class.
	 * 
	 * Implements the ShowSelectedSharesEvent.Handler.onShowSelectedShares() method.
	 * 
	 * @param event
	 */
	@Override
	public void onShowSelectedShares(ShowSelectedSharesEvent event) {
		// Is the event targeted to this folder?
		Long eventFolderId = event.getFolderId();
		if (eventFolderId.equals(getFolderId())) {
			// Yes!  Invoke the hide selected shares.
			List<EntityId> selectedEntityIds = event.getSelectedEntities();
			if (!(GwtClientHelper.hasItems(selectedEntityIds))) {
				selectedEntityIds = getSelectedEntityIds();
			}
			BinderViewsHelper.showSelectedShares(
				getFolderInfo().getCollectionType(),
				selectedEntityIds);
		}
	}
	
	/**
	 * Handles SubscribeSelectedEntitiesEvent's received by this class.
	 * 
	 * Implements the SubscribeSelectedEntitiesEvent.Handler.onSubscribeSelectedEntities() method.
	 * 
	 * @param event
	 */
	@Override
	public void onSubscribeSelectedEntities(SubscribeSelectedEntitiesEvent event) {
		// Is the event targeted to this folder?
		Long eventFolderId = event.getFolderId();
		if (eventFolderId.equals(getFolderId())) {
			// Yes!  Get the user's rights to the selected entities...
			List<EntityId> seList = event.getSelectedEntities();
			final boolean validateSelectedRows = (!(GwtClientHelper.hasItems(seList)));
			if (validateSelectedRows) {
				seList = getSelectedEntityIds();
			}
			
			final List<EntityId> selectedEntities = seList;
			GwtClientHelper.executeCommand(
					new GetEntityRightsCmd(selectedEntities),
					new AsyncCallback<VibeRpcResponse>() {
				@Override
				public void onFailure(Throwable caught) {
					GwtClientHelper.handleGwtRPCFailure(
						caught,
						GwtTeaming.getMessages().rpcFailure_GetEntityRights());
				}

				@Override
				public void onSuccess(VibeRpcResponse response) {
					// ...and invoke the subscribe to on them.
					EntityRightsRpcResponseData responseData = ((EntityRightsRpcResponseData) response.getResponseData());
					onSubscribeSelectedEntitiesAsync(selectedEntities, responseData.getEntityRightsMap(), validateSelectedRows);
				}
			});
		}
	}
	
	/*
	 * Asynchronously processes the subscribe request on the selected
	 * entries, given the current user's rights to them.
	 */
	private void onSubscribeSelectedEntitiesAsync(final List<EntityId> selectedEntities, final Map<String, EntityRights> entityRightsMap, final boolean validateSelectedRows) {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				onSubscribeSelectedEntitiesNow(selectedEntities, entityRightsMap, validateSelectedRows);
			}
		});
	}
	
	/*
	 * Synchronously processes the subscribe request on the selected
	 * entries, given the current user's rights to them.
	 */
	private void onSubscribeSelectedEntitiesNow(final List<EntityId> selectedEntities, final Map<String, EntityRights> entityRightsMap, boolean validateSelectedRows) {
		// Are there any invalid rows?
		final List<FolderRow> invalidRows = (validateSelectedRows ? validateSelectedRows_Subscribe(entityRightsMap) : null);
		if (!(GwtClientHelper.hasItems(invalidRows))) {
			// No!  Invoke the subscribe.
			subscribeToSelectedEntitiesAsync(selectedEntities);
		}
		
		else {
			// No, they don't have rights to subscribe to everything!
			// Can they subscribe to any of them?
			int totalSubFailures = invalidRows.size();
			if (selectedEntities.size() == totalSubFailures) {
				// No!  Tell them about the problem and bail.
				GwtClientHelper.deferredAlert(m_messages.vibeDataTable_Warning_CantSubscribe());
				return;
			}
			
			// Is the user sure they want to subscribe to the
			// selections they have rights to subscribe to?
			ConfirmDlg.createAsync(new ConfirmDlgClient() {
				@Override
				public void onUnavailable() {
					// Nothing to do.  Error handled in
					// asynchronous provider.
				}
				
				@Override
				public void onSuccess(ConfirmDlg cDlg) {
					ConfirmDlg.initAndShow(
						cDlg,
						new ConfirmCallback() {
							@Override
							public void dialogReady() {
								// Ignored.  We don't really care when the
								// dialog is ready.
							}

							@Override
							public void accepted() {
								// Yes, they're sure!  Remove the
								// selection from the entries they
								// don't have rights to subscribe to
								// and perform the subscribe on the
								// rest.
								removeRowEntities(               selectedEntities, invalidRows);
								deselectRows(                                      invalidRows);
								subscribeToSelectedEntitiesAsync(selectedEntities);
							}

							@Override
							public void rejected() {
								// No, they're not sure!
							}
						},
						m_messages.vibeDataTable_Confirm_CantSubscribe());
				}
			});
		}
	}
	
	/**
	 * Handles ToggleSharedViewEvent's received by this class.
	 * 
	 * Implements the ToggleSharedViewEvent.Handler.onToggleSharedView() method.
	 * 
	 * @param event
	 */
	@Override
	public void onToggleSharedView(ToggleSharedViewEvent event) {
		// Is the event targeted to this view's collection?
		CollectionType eventCollectionType = event.getCollectionType();
		if (eventCollectionType.equals(getFolderInfo().getCollectionType())) {
			// Yes!  Save the toggled shared view state for the
			// collection.
			final SaveSharedFilesStateCmd cmd = new SaveSharedFilesStateCmd(getFolderInfo().getCollectionType(), (!(isSharedFiles())));
			GwtClientHelper.executeCommand(cmd, new AsyncCallback<VibeRpcResponse>() {
				@Override
				public void onFailure(Throwable caught) {
					GwtClientHelper.handleGwtRPCFailure(
						caught,
						GwtTeaming.getMessages().rpcFailure_SaveSharedFilesState());
				}

				@Override
				public void onSuccess(VibeRpcResponse result) {
					// ...and reload the view to redisplay things.
					FullUIReloadEvent.fireOne();
				}
			});
		}
	}
	
	/**
	 * Handles TrashPurgeAllEvent's received by this class.
	 * 
	 * Implements the TrashPurgeAllEvent.Handler.onTrashPurgeAll() method.
	 * 
	 * @param event
	 */
	@Override
	public void onTrashPurgeAll(TrashPurgeAllEvent event) {
		// Is the event targeted to this folder?
		Long eventFolderId = event.getBinderId();
		if (eventFolderId.equals(getFolderId())) {
			// Yes!  Asynchronously purge all the entries from the
			// trash.
			GwtClientHelper.deferCommand(new ScheduledCommand() {
				@Override
				public void execute() {
					trashPurgeAll();
				}
			});
		}
	}

	/**
	 * Handles TrashPurgeSelectedEntitiesEvent's received by this class.
	 * 
	 * Implements the TrashPurgeSelectedEntitiesEvent.Handler.onTrashPurgeSelectedEntities() method.
	 * 
	 * @param event
	 */
	@Override
	public void onTrashPurgeSelectedEntities(TrashPurgeSelectedEntitiesEvent event) {
		// Is the event targeted to this folder?
		Long eventFolderId = event.getBinderId();
		if (eventFolderId.equals(getFolderId())) {
			// Yes!  Asynchronously purge the selected entries from the
			// trash.
			GwtClientHelper.deferCommand(new ScheduledCommand() {
				@Override
				public void execute() {
					trashPurgeSelectedEntities();
				}
			});
		}
	}

	/**
	 * Handles TrashRestoreAllEvent's received by this class.
	 * 
	 * Implements the TrashRestoreAllEvent.Handler.onTrashRestoreAll() method.
	 * 
	 * @param event
	 */
	@Override
	public void onTrashRestoreAll(TrashRestoreAllEvent event) {
		// Is the event targeted to this folder?
		Long eventFolderId = event.getBinderId();
		if (eventFolderId.equals(getFolderId())) {
			// Yes!  Asynchronously restore all the entries in the
			// trash.
			GwtClientHelper.deferCommand(new ScheduledCommand() {
				@Override
				public void execute() {
					trashRestoreAll();
				}
			});
		}
	}

	/**
	 * Handles TrashRestoreSelectedEntitiesEvent's received by this class.
	 * 
	 * Implements the TrashRestoreSelectedEntitiesEvent.Handler.onTrashRestoreSelectedEntities() method.
	 * 
	 * @param event
	 */
	@Override
	public void onTrashRestoreSelectedEntities(TrashRestoreSelectedEntitiesEvent event) {
		// Is the event targeted to this folder?
		Long eventFolderId = event.getBinderId();
		if (eventFolderId.equals(getFolderId())) {
			// Yes!  Asynchronously restore the selected entries in the
			// trash.
			GwtClientHelper.deferCommand(new ScheduledCommand() {
				@Override
				public void execute() {
					trashRestoreSelectedEntities();
				}
			});
		}
	}
	
	/**
	 * Handles UnlockSelectedEntitiesEvent's received by this class.
	 * 
	 * Implements the UnlockSelectedEntitiesEvent.Handler.onUnlockSelectedEntities() method.
	 * 
	 * @param event
	 */
	@Override
	public void onUnlockSelectedEntities(UnlockSelectedEntitiesEvent event) {
		// Is the event targeted to this folder?
		Long eventFolderId = event.getFolderId();
		if (eventFolderId.equals(getFolderId())) {
			// Yes!  Invoke the unlock.
			List<EntityId> selectedEntityIds = event.getSelectedEntities();
			if (!(GwtClientHelper.hasItems(selectedEntityIds))) {
				selectedEntityIds = getSelectedEntityIds();
			}
			BinderViewsHelper.unlockEntries(selectedEntityIds);
		}
	}
	
	/**
	 * Handles ViewPinnedEntriesEvent's received by this class.
	 * 
	 * Implements the ViewPinnedEntriesEvent.Handler.onViewPinnedEntries() method.
	 * 
	 * @param event
	 */
	@Override
	public void onViewPinnedEntries(ViewPinnedEntriesEvent event) {
		// Is the event targeted to this folder?
		Long eventFolderId = event.getFolderId();
		if (eventFolderId.equals(getFolderId())) {
			// Yes!  Save the toggled pinning state for the folder.
			final SaveFolderPinningStateCmd cmd = new SaveFolderPinningStateCmd(getFolderId(), (!(isPinning())));
			GwtClientHelper.executeCommand(cmd, new AsyncCallback<VibeRpcResponse>() {
				@Override
				public void onFailure(Throwable caught) {
					GwtClientHelper.handleGwtRPCFailure(
						caught,
						GwtTeaming.getMessages().rpcFailure_SaveFolderPinningState());
				}

				@Override
				public void onSuccess(VibeRpcResponse result) {
					// ...and reload the view to redisplay things.
					FullUIReloadEvent.fireOne();
				}
			});
		}
	}
	
	/**
	 * Handles ViewSelectedEntryEvent's received by this class.
	 * 
	 * Implements the ViewSelectedEntryEvent.Handler.onViewSelectedEntry() method.
	 * 
	 * @param event
	 */
	@Override
	public void onViewSelectedEntry(ViewSelectedEntryEvent event) {
		// Is the event targeted to this folder?
		Long eventFolderId = event.getFolderId();
		if (eventFolderId.equals(getFolderId())) {
			// Yes!  Invoke the view.
			List<EntityId> eids = event.getSelectedEntities();
			if (!(GwtClientHelper.hasItems(eids))) {
				eids = getSelectedEntityIds();
			}
			if (GwtClientHelper.hasItems(eids)) {
				for (EntityId eid:  eids) {
					if (eid.isEntry()) {
						BinderViewsHelper.viewEntry(eid);
						return;
					}
				}
			}
		}
	}
	
	/**
	 * Handles ViewWhoHasAccessEvent's received by this class.
	 * 
	 * Implements the ViewWhoHasAccessEvent.Handler.onViewWhoHasAccess() method.
	 * 
	 * @param event
	 */
	@Override
	public void onViewWhoHasAccess(ViewWhoHasAccessEvent event) {
		// Is the event targeted to this folder?
		Long eventFolderId = event.getFolderId();
		if (eventFolderId.equals(getFolderId())) {
			// Yes!  Invoke the view.
			List<EntityId> eids = event.getSelectedEntities();
			if (!(GwtClientHelper.hasItems(eids))) {
				eids = getSelectedEntityIds();
			}
			if (GwtClientHelper.hasItems(eids)) {
				for (EntityId eid:  eids) {
					BinderViewsHelper.viewWhoHasAccess(eid);
					return;
				}
			}
		}
	}
	
	/**
	 * Handles ZipAndDownloadSelectedFilesEvent's received by this class.
	 * 
	 * Implements the ZipAndDownloadSelectedFilesEvent.Handler.onZipAndDownloadSelectedFiles() method.
	 * 
	 * @param event
	 */
	@Override
	public void onZipAndDownloadSelectedFiles(ZipAndDownloadSelectedFilesEvent event) {
		// Is the event targeted to this folder?
		Long eventFolderId = event.getFolderId();
		if (eventFolderId.equals(getFolderId())) {
			// Yes!  Invoke the zip and download.
			List<EntityId> selectedEntityIds = event.getSelectedEntities();
			if (!(GwtClientHelper.hasItems(selectedEntityIds))) {
				selectedEntityIds = getSelectedEntityIds();
			}
			BinderViewsHelper.zipAndDownloadFiles(
				getDownloadPanel().getForm(),
				selectedEntityIds,
				event.isRecursive());
		}
	}
	
	/**
	 * Handles ZipAndDownloadFolderEvent's received by this class.
	 * 
	 * Implements the ZipAndDownloadFolderEvent.Handler.onZipAndDownloadFolder() method.
	 * 
	 * @param event
	 */
	@Override
	public void onZipAndDownloadFolder(ZipAndDownloadFolderEvent event) {
		// Is the event targeted to this folder?
		Long dlFolderId    = event.getFolderId();
		Long eventFolderId = event.getHandleByFolderId();
		if (null == eventFolderId) {
			eventFolderId = dlFolderId;
		}
		if (eventFolderId.equals(getFolderId())) {
			// Yes!  Invoke the zip and download.
			BinderViewsHelper.zipAndDownloadFolder(
				getDownloadPanel().getForm(),
				dlFolderId,
				event.isRecursive());
		}
	}
	
	/**
	 * Completes populating the data table view specific content.
	 */
	final public void populateContent() {
		// Did we get any column width overrides?
		Map<String, String> widths = getFolderColumnWidths();
		if (GwtClientHelper.hasItems(widths)) {
			// Yes!  Scan them...
			for (String cName:  widths.keySet()) {
				try {
					// ...parsing the width string...
					String cwS = widths.get(cName);
					m_columnWidths.put(cName, ColumnWidth.parseWidthStyle(cwS));
				}
				catch (Exception e) {
					// On any exception, simply ignore the override.
				}
			}
		}
		
		// Asynchronously load the remaining data table specific
		// components of a folder view.
		loadFolderColumnsAsync();
	}

	/*
	 * Asynchronously populates the data table view given the available
	 * data.
	 */
	private void populateViewAsync() {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				populateView();
			}
		});
	}
	
	/*
	 * Synchronously populates the data table view given the available
	 * data.
	 */
	private void populateView() {
		// If we're supposed to dump the display data we're building this on...
		VibeVerticalPanel vp;
		if (DUMP_DISPLAY_DATA) {
			// ...dump it.
			vp = new VibeVerticalPanel();
			vp.add(new HTML("<br/>- - - - - Start:  Folder Display Data - - - - -<br/><br/>"));
			vp.add(new InlineLabel("Sort by:  "         + getFolderSortBy())     );
			vp.add(new InlineLabel("Sort descending:  " + getFolderSortDescend()));
			vp.add(new InlineLabel("Page size:  "       + getFolderPageSize())   );
			vp.add(new HTML("<br/>"));
			for (FolderColumn fc:  m_folderColumnsList) {
				vp.add(new InlineLabel(fc.getColumnEleName() + "='" + fc.getColumnTitle() + "'"));
			}
			vp.add(new HTML("<br/>- - - - - End:  Folder Display Data - - - - -<br/>"));
			getFlowPanel().add(vp);
		}
		
		// Create a key provider that will provide a unique key for
		// each row.
		FolderRowKeyProvider keyProvider = new FolderRowKeyProvider();
		
		// Create the table.
		m_dataTable = new VibeDataGrid<FolderRow>(getFolderPageSize(), keyProvider);
		setDataTableWidthImpl(m_dataTable);
		m_dataTable.addStyleName("vibe-dataTableFolderDataTableBase");
		if (GwtClientHelper.hasString(m_folderStyles)) {
			m_dataTable.addStyleName(m_folderStyles);
		}
		m_dataTable.setRowStyles(new RowStyles<FolderRow>() {
			@Override
			public String getStyleNames(FolderRow row, int rowIndex) {
				StringBuffer reply = new StringBuffer(STYLE_ROW_BASE);
				reply.append(" ");
				reply.append(STYLE_ROW_BASE);
				reply.append("-");
				reply.append((0 == (rowIndex % 2)) ? STYLE_ROW_EVEN : STYLE_ROW_ODD);
				return reply.toString();
			}
		});
		
		// Set a message to display when the table is empty.
		m_dataTable.setEmptyTableWidget(getEmptyTableWidget());

		// Attach a sort handler to sort the list.
	    FolderRowSortHandler sortHandler = new FolderRowSortHandler();
	    m_dataTable.addColumnSortHandler(sortHandler);
		
		// Create a pager that lets the user page through the table.
	    m_dataTablePager = new VibeSimplePager();
	    m_dataTablePager.setDisplay(m_dataTable);

	    // Add a selection model so the user can select cells.
	    FolderRowSelectionModel selectionModel = new FolderRowSelectionModel(keyProvider);
	    m_dataTable.setSelectionModel(selectionModel, DefaultSelectionEventManager.<FolderRow> createCheckboxManager());

	    // Initialize the table's columns.
	    initTableColumns(selectionModel);
	    
	    // Add the provider that supplies FolderRow's for the table.
	    FolderRowAsyncProvider folderRowProvider = new FolderRowAsyncProvider(m_dataTable, keyProvider);
	    folderRowProvider.addDataDisplay(m_dataTable);

	    // Add the table and pager to the view.
		vp = new VibeVerticalPanel("100%", null);
	    vp.add(m_dataTable     );
	    vp.add(m_dataTablePager);
	    vp.setCellHorizontalAlignment(m_dataTablePager, HasHorizontalAlignment.ALIGN_CENTER);
		getFlowPanel().add(vp);
		
		// Finally, ensure the table gets sized correctly.
		doResizeAsync();
	}

	/*
	 * Asynchronously allows the view's that extend this do what ever
	 * they need to do once a collection of rows have been rendered.
	 */
	private void postProcessRowDataAsync(final List<FolderRow> folderRows) {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				postProcessRowData(folderRows, m_folderColumnsList);
			}
		});
	}
	
	/**
	 * Allows the view's that extend this do what ever they need to
	 * do once a collection of rows have been rendered.
	 * 
	 * @param columnWidths
	 */
	protected void postProcessRowData(final List<FolderRow> folderRows, final List<FolderColumn> folderColumns) {
		// By default, there is no post processing required.
	}

	/*
	 * Registers any global event handlers that need to be registered.
	 */
	private void registerEvents() {
		// If we having allocated a list to track events we've
		// registered yet...
		if (null == m_dtfvb_registeredEventHandlers) {
			// ...allocate one now.
			m_dtfvb_registeredEventHandlers = new ArrayList<HandlerRegistration>();
		}

		// If the list of registered events is empty...
		if (m_dtfvb_registeredEventHandlers.isEmpty()) {
			// ...register the events.
			EventHelper.registerEventHandlers(
				GwtTeaming.getEventBus(),
				dtfvb_REGISTERED_EVENTS,
				this,
				m_dtfvb_registeredEventHandlers);
		}
	}

	/*
	 * Removes the EntityId's from a List<EntityId> corresponding to
	 * the FolderRow's in a List<FolderRow>.
	 */
	private void removeRowEntities(List<EntityId> entities, List<FolderRow> rows) {
		// Do we have list to process?
		if (GwtClientHelper.hasItems(entities) && GwtClientHelper.hasItems(rows)) {
			// Yes!  Scan the rows.
			for (FolderRow row:  rows) {
				// Scan the entities.
				for (EntityId entity:  entities) {
					// Is this the entity for the row?
					if (entity.equalsEntityId(row.getEntityId())) {
						// Yes!  Remove it from the List<EntityId> and
						// skip to the next row.
						entities.remove(entity);
						break;
					}
				}
			}
		}
	}

	/**
	 * Removes the select column in the data table.
	 * 
	 * A view will call this, for example, if there's nothing in its
	 * entry menu that can act upon selections.
	 */
	public void removeSelectColumn() {
		// If we have a select column defined...
		if (null != m_selectColumn) {
			// ...remove it from the data table...
			m_dataTable.removeColumn(m_selectColumn);
			
			// ...forget about it...
			m_selectColumn = null;

			// ...and account for it in the other column's indexes.
			for (FolderColumn fc:  m_folderColumnsList) {
				fc.setDisplayIndex(fc.getDisplayIndex() - 1);
			}
		}
	}
	
	/*
	 * Asynchronously resets the the content of the data table.
	 * 
	 * This is different from resetViewAsync() in that only the data
	 * table is reset and not the tool panels, ...
	 */
	private void resetDataTableAsync() {
		getFlowPanel().clear();
		populateViewAsync();
	}
	
	/*
	 * Asynchronously resets the view.
	 */
	private void resetViewAsync() {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				resetView();
			}
		});
	}

	/*
	 * Asynchronously runs the Cloud Folder authentication dialog.
	 */
	private void runCloudFolderAuthenticationDlgAsync(final CloudFolderAuthentication cfAuthentication, final AbstractCellTable<FolderRow> vdt, final Long folderId, final Range range) {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				runCloudFolderAuthenticationDlgNow(
					cfAuthentication,
					vdt,
					folderId,
					range);
			}
		});
	}
	
	/*
	 * Synchronously runs the Cloud Folder authentication dialog.
	 */
	private void runCloudFolderAuthenticationDlgNow(final CloudFolderAuthentication cfAuthentication, final AbstractCellTable<FolderRow> vdt, final Long folderId, final Range range) {
		// Have we instantiated a Cloud Folder authentication dialog
		// yet?
		if (null == m_cfaDlg) {
			// No!  Instantiate one now.
			CloudFolderAuthenticationDlg.createAsync(new CloudFolderAuthenticationDlgClient() {			
				@Override
				public void onUnavailable() {
					// Nothing to do.  Error handled in
					// asynchronous provider.
				}
				
				@Override
				public void onSuccess(final CloudFolderAuthenticationDlg cfaDlg) {
					// ...and show it.
					m_cfaDlg = cfaDlg;
					GwtClientHelper.deferCommand(new ScheduledCommand() {
						@Override
						public void execute() {
							showCloudFolderAuthenticationDlgNow(
								cfAuthentication,
								vdt,
								folderId,
								range);
						}
					});
				}
			});
		}
		
		else {
			// Yes, we've instantiated a Cloud Folder authentication
			// dialog already!  Simply show it.
			showCloudFolderAuthenticationDlgNow(
				cfAuthentication,
				vdt,
				folderId,
				range);
		}
	}

	/*
	 * Sets the style on a column in the data table based on the
	 * column's name.
	 */
	private void setColumnStyles(Column<FolderRow, ?> column, String columnName, int colIndex, String addedHeaderStyles) {
		if (!(FolderColumn.isColumnTitle(columnName))) {
			column.setCellStyleNames("gwtUI_nowrap");
		}
	    StringBuffer styles = new StringBuffer(STYLE_COL_BASE);
	    styles.append(" ");
	    styles.append(STYLE_COL_BASE);
	    styles.append("-");
	    styles.append(columnName.equals(FolderColumn.COLUMN_SELECT) ? STYLE_COL_SELECT : columnName);
	    m_dataTable.addColumnStyleName(colIndex, styles.toString());
	    
	    if (GwtClientHelper.hasString(addedHeaderStyles)) {
	    	Header<?> h = m_dataTable.getHeader(colIndex);
	    	String baseStyles = h.getHeaderStyleNames();
	    	if (GwtClientHelper.hasString(baseStyles))
	    	     baseStyles += " ";
	    	else baseStyles = "";
	    	h.setHeaderStyleNames(baseStyles + addedHeaderStyles);
	    }
	}
	
	private void setColumnStyles(Column<FolderRow, ?> column, String columnName, int colIndex) {
		// Always use the initial form of the method.
		setColumnStyles(column, columnName, colIndex, null);
	}

	/*
	 * Sets the width of a column in the data table based on the
	 * column's name.
	 */
	private void setColumnWidth(String cName, Column<FolderRow, ?> column, double pctTotal) {
		// Set the width for the column.
		ColumnWidth cw = m_columnWidths.get(cName);
		setColumnWidth(
			((null == cw) ? m_defaultColumnWidth : cw),
			column,
			pctTotal);
	}
	
	/*
	 * Sets the width of a column in the data table based on a
	 * ColumnWidth object.
	 */
	private void setColumnWidth(ColumnWidth cw, Column<FolderRow, ?> column, double pctTotal) {
		// Do we have a column width to set?
		if (null == cw) {
			// No!  Remove any setting that's already there.
			m_dataTable.clearColumnWidth(column);
		}
		
		else {
			// Yes, we have a column width to set!  Is it a percent we
			// need to adjust?
			double  currentWidth  = cw.getWidth();
			boolean adjustedWidth = (cw.isPCT() && (pctTotal != ((double) 100)));
			if (adjustedWidth) {
				// Yes!  Scale the width used so that the actual values sum
				// to 100%.
				cw.setWidth(
					ColumnWidth.scalePCTWidth(
						currentWidth,
						pctTotal));
			}
			
			// Put the width into affect.
			m_dataTable.setColumnWidth(column, ColumnWidth.getWidthStyle(cw));
			if (adjustedWidth) {
				cw.setWidth(currentWidth);
			}
		}
	}
	
	/*
	 * Sets the table width.
	 */
	@SuppressWarnings("unused")
	private void setDataTableWidthImpl(CellTable<FolderRow> ct) {
		ct.setWidth("100%", m_fixedLayout);
	}

	private void setDataTableWidthImpl(DataGrid<FolderRow> dg) {
		dg.setWidth("100%");
	}

	/*
	 * Asynchronously runs the share dialog on the selected entities.
	 */
	private void shareSelectedEntitiesAsync(final List<EntityId> selectedEntities) {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				shareSelectedEntitiesNow(selectedEntities);
			}
		});
	}
	
	/*
	 * Synchronously runs the share dialog on the selected entities.
	 */
	private void shareSelectedEntitiesNow(List<EntityId> selectedEntities) {
		BinderViewsHelper.shareEntities(selectedEntities);
	}
	
	/*
	 * Asynchronously shows the binder share rights dialog.
	 */
	private void showBinderShareRightsDlgAsync(final List<Long> selectedBinderList, final UIObject showRelativeTo) {
		GwtClientHelper.deferCommand(
			new ScheduledCommand() {
				@Override
				public void execute() {
					showBinderShareRightsDlgNow(selectedBinderList, showRelativeTo);
				}
			});
	}

	/*
	 * Synchronously shows the binder share rights dialog.
	 */
	private void showBinderShareRightsDlgNow(final List<Long> selectedBinderList, final UIObject showRelativeTo) {
		int binderCount = ((null == selectedBinderList) ? 0 : selectedBinderList.size());
		boolean setTeamMemberRights = getFolderInfo().isBinderTeamsRootWS();
		String caption;
		if (setTeamMemberRights)
		     caption = m_messages.shareTeamRightsDlgHeader(     binderCount);
		else caption = m_messages.shareWorkspaceRightsDlgHeader(binderCount);
		BinderShareRightsDlg.initAndShow(
			m_binderShareRightsDlg,
			caption,
			selectedBinderList,
			(!setTeamMemberRights),	// true -> Set all users   rights.  false -> Don't.
			setTeamMemberRights,	// true -> Set team member rights.  false -> Don't.
			showRelativeTo);
	}

	/*
	 * Synchronously shows the Cloud Folder authentication dialog.
	 */
	private void showCloudFolderAuthenticationDlgNow(final CloudFolderAuthentication cfAuthentication, final AbstractCellTable<FolderRow> vdt, final Long folderId, final Range range) {
		CloudFolderAuthenticationDlg.initAndShow(
			m_cfaDlg,
			cfAuthentication,
			new CloudFolderAuthenticationCallback() {
				@Override
				public void dialogReady() {
					// With the authentication dialog running, we now
					// need to reissue the request for the folder's
					// rows, this time with the GUID from the
					// authentication request. 
					GwtClientHelper.deferCommand(new ScheduledCommand() {
						@Override
						public void execute() {
							onRangeChangedImpl(
								vdt,
								folderId,
								range,
								cfAuthentication.getAuthenticationGuid());
						}
					});
				}
			});
	}

	/**
	 * Allows the view's that extend this to decide whether or not they
	 * want an entry icon on their title cell.
	 * 
	 * @return
	 */
	protected boolean showEntryTitleIcon() {
		// By default, we show entry title icons.
		return true;
	}
	
	/*
	 * Synchronously shows the column sizing dialog.
	 */
	private void showSizeColumnsDlgNow() {
		SizeColumnsDlg.initAndShow(
			m_sizeColumnsDlg,
			getFolderInfo(),
			getColumnsForSizing(),
			m_columnWidths,
			m_defaultColumnWidth,
			m_defaultColumnWidths,
			this,
			m_dataTable.getAbsoluteTop(),
			m_fixedLayout);
	}

	/**
	 * Invokes the sign the guest book UI. 
	 * 
	 * Stub provided as a convenience method.  Must be overridden by
	 * those classes that extend this that provide guest book services.
	 */
	public void signGuestbook() {
		GwtClientHelper.deferredAlert(m_messages.vibeDataTable_GuestbookInternalErrorOverrideMissing());
	}

	/*
	 * Asynchronously runs the subscribe to dialog on the selected
	 * entities.
	 */
	private final static void subscribeToSelectedEntitiesAsync(final List<EntityId> selectedEntities) {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				subscribeToSelectedEntitiesNow(selectedEntities);
			}
		});
	}
	
	/*
	 * Synchronously runs the subscribe to dialog on the selected
	 * entities.
	 */
	private final static void subscribeToSelectedEntitiesNow(final List<EntityId> selectedEntities) {
		BinderViewsHelper.subscribeToEntries(selectedEntities);
	}
	
	/**
	 * Purges all the entries from the trash.
	 * 
	 * Stub provided as a convenience method.  Must be overridden by
	 * those classes that extend this that provide trash handling.
	 */
	public void trashPurgeAll() {
		GwtClientHelper.deferredAlert(m_messages.vibeDataTable_TrashInternalErrorOverrideMissing("trashPurgeAll()"));
	}
	
	/**
	 * Purges the selected entries from the trash.
	 * 
	 * Stub provided as a convenience method.  Must be overridden by
	 * those classes that extend this that provide trash handling.
	 */
	public void trashPurgeSelectedEntities() {
		GwtClientHelper.deferredAlert(m_messages.vibeDataTable_TrashInternalErrorOverrideMissing("trashPurgeSelectedEntities()"));
	}
	
	/**
	 * Restores all the entries in the trash. 
	 * 
	 * Stub provided as a convenience method.  Must be overridden by
	 * those classes that extend this that provide trash handling.
	 */
	public void trashRestoreAll() {
		GwtClientHelper.deferredAlert(m_messages.vibeDataTable_TrashInternalErrorOverrideMissing("trashRestoreAll()"));
	}
	
	/**
	 * Restores the selected entries in the trash.
	 * 
	 * Stub provided as a convenience method.  Must be overridden by
	 * those classes that extend this that provide trash handling.
	 */
	public void trashRestoreSelectedEntities() {
		GwtClientHelper.deferredAlert(m_messages.vibeDataTable_TrashInternalErrorOverrideMissing("trashRestoreSelectedEntities()"));
	}
	
	/*
	 * Asynchronously updates the comment count in the given row.
	 */
	private void updateCommentCountAsync(final FolderColumn commentsColumn, final FolderRow row, final int rowIndex) {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				updateCommentCountNow(commentsColumn, row, rowIndex);
			}
		});
	}
	
	/*
	 * Synchronously updates the comment count in the given row.
	 */
	private void updateCommentCountNow(final FolderColumn commentsColumn, final FolderRow row, final int rowIndex) {
		GetCommentCountCmd cmd = new GetCommentCountCmd(row.getEntityId());
		GwtClientHelper.executeCommand(cmd, new AsyncCallback<VibeRpcResponse>() {
			@Override
			public void onFailure(Throwable caught) {
				GwtClientHelper.handleGwtRPCFailure(
					caught,
					GwtTeaming.getMessages().rpcFailure_GetCommentCount());
			}

			@Override
			public void onSuccess(VibeRpcResponse result) {
				int count = ((IntegerRpcResponseData) result.getResponseData()).getIntegerValue();
				if (0 <= count) {
					CommentsInfo ci = row.getColumnValueAsComments(commentsColumn);
					ci.setCommentsCount(count);
					m_dataTable.redrawRow(rowIndex);
				}
			}
		});
	}
	/*
	 * Returns a List<FolderRow> of the selected rows that the user
	 * can't share the public link from.
	 */
	private List<FolderRow> validateSelectedRows_PublicLink(final Map<String, EntityRights> entityRightsMap) {
		// Are there any selected rows in the table?
		List<FolderRow> reply = new ArrayList<FolderRow>();
		List<FolderRow> rows  = m_dataTable.getVisibleItems();
		if (GwtClientHelper.hasItems(rows)) {
			// Yes!  Scan them
			FolderRowSelectionModel fsm = ((FolderRowSelectionModel) m_dataTable.getSelectionModel());
			for (FolderRow row : rows) {
				// Is this row selected?
				if (fsm.isSelected(row)) {
					// Yes!  Can it be public linked?
					EntityRights er = entityRightsMap.get(EntityRights.getEntityRightsKey(row.getEntityId()));
					if ((null == er) || (!(er.isCanPublicLink()))) {
						// No!  Track it as invalid.
						reply.add(row);
					}
					
					// Yes, it can be public linked!  Is it an entry?
					else if (row.getEntityId().isBinder()) {
						// No!  Track it as invalid.
						reply.add(row);
					}
					
					// Yes, it's an entry!  Is it a file entry?
					else if (!(row.isRowFile(GwtClientHelper.isLicenseFilr()))) {
						// No!  Track it as invalid.
						reply.add(row);
					}
				}
			}
		}
		
		// If we get here, reply refers to List<FolderRow> of the rows
		// the user doesn't have rights to share.  Return it.
		return reply;
	}

	/*
	 * Returns a List<FolderRow> of the selected rows that the user
	 * can't share.
	 */
	private List<FolderRow> validateSelectedRows_Sharing(final Map<String, EntityRights> entityRightsMap) {
		// Are there any selected rows in the table?
		List<FolderRow> reply = new ArrayList<FolderRow>();
		List<FolderRow> rows  = m_dataTable.getVisibleItems();
		if (GwtClientHelper.hasItems(rows)) {
			// Yes!  Scan them
			FolderRowSelectionModel fsm = ((FolderRowSelectionModel) m_dataTable.getSelectionModel());
			for (FolderRow row : rows) {
				// Is this row selected?
				if (fsm.isSelected(row)) {
					// Yes!  Is it sharable?
					EntityRights er = entityRightsMap.get(EntityRights.getEntityRightsKey(row.getEntityId()));
					if ((null == er) || (!(er.isCanShare()))) {
						// No!  Track it as invalid.
						reply.add(row);
					}
				}
			}
		}
		
		// If we get here, reply refers to List<FolderRow> of the rows
		// the user doesn't have rights to share.  Return it.
		return reply;
	}

	/*
	 * Returns a List<FolderRow> of the selected rows that the user
	 * can't subscribe to.
	 */
	private List<FolderRow> validateSelectedRows_Subscribe(final Map<String, EntityRights> entityRightsMap) {
		// Are there any selected rows in the table?
		List<FolderRow> reply = new ArrayList<FolderRow>();
		List<FolderRow> rows  = m_dataTable.getVisibleItems();
		if (GwtClientHelper.hasItems(rows)) {
			// Yes!  Scan them
			FolderRowSelectionModel fsm = ((FolderRowSelectionModel) m_dataTable.getSelectionModel());
			for (FolderRow row : rows) {
				// Is this row selected?
				if (fsm.isSelected(row)) {
					// Yes!  Can it be subscribed to?
					EntityRights er = entityRightsMap.get(EntityRights.getEntityRightsKey(row.getEntityId()));
					if ((null == er) || (!(er.isCanSubscribe()))) {
						// No!  Track it as invalid.
						reply.add(row);
					}
				}
			}
		}
		
		// If we get here, reply refers to List<FolderRow> of the rows
		// the user doesn't have rights to share.  Return it.
		return reply;
	}

	/*
	 * Unregisters any global event handlers that may be registered.
	 */
	private void unregisterEvents() {
		// If we have a non-empty list of registered events...
		if (GwtClientHelper.hasItems(m_dtfvb_registeredEventHandlers)) {
			// ...unregister them.  (Note that this will also empty the
			// ...list.)
			EventHelper.unregisterEventHandlers(m_dtfvb_registeredEventHandlers);
		}
	}
}
