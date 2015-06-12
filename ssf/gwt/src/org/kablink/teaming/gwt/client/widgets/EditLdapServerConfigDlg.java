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
package org.kablink.teaming.gwt.client.widgets;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.kablink.teaming.gwt.client.EditSuccessfulHandler;
import org.kablink.teaming.gwt.client.GwtLdapConnectionConfig;
import org.kablink.teaming.gwt.client.GwtLdapSearchInfo;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingMessages;
import org.kablink.teaming.gwt.client.datatable.LdapSearchBaseDnCell;
import org.kablink.teaming.gwt.client.datatable.VibeCellTable;
import org.kablink.teaming.gwt.client.ldapbrowser.DirectoryServer;
import org.kablink.teaming.gwt.client.ldapbrowser.LdapObject;
import org.kablink.teaming.gwt.client.ldapbrowser.LdapSearchInfo;
import org.kablink.teaming.gwt.client.ldapbrowser.LdapServer.DirectoryType;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.widgets.DlgBox;
import org.kablink.teaming.gwt.client.widgets.EditLdapSearchDlg.EditLdapSearchDlgClient;
import org.kablink.teaming.gwt.client.widgets.LdapBrowserDlg.LdapBrowserDlgClient;

import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.view.client.DefaultSelectionEventManager;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.MultiSelectionModel;

/**
 * ?
 *  
 * @author jwootton
 */
public class EditLdapServerConfigDlg extends DlgBox
{
	private static String EDIR_NAME = "eDirectory";
	private static String AD_NAME = "Active Directory";
	private static String DIR_TYPE_AD = "objectGUID";
	private static String DIR_TYPE_EDIR = "GUID";
	private static String DIR_TYPE_OTHER = "other";
	private static String SAM_ACCOUNT_NAME_ATTRIB = "sAMAccountName";
	private static String CN_ATTRIB = "cn";
	private static String OTHER_ATTRIB = "other";
	private static String EDIR_GUID_NAME = "GUID";
	private static String AD_GUID_NAME = "objectGUID";
	private static String OTHER_GUID_NAME = "other-guid";
	
	private GwtLdapConnectionConfig m_serverConfig;
	private String m_defaultGroupFilter = null;
	private String m_defaultUserFilter = null;

	private TabPanel m_tabPanel;

	// Controls used in the Server Information tab
	private Button m_browseProxyDnBtn;	// LDAP browse button next to m_proxyDnTextBox.
	private TextBox m_serverUrlTextBox;
	private TextBox m_proxyDnTextBox;
	private PasswordTextBox m_proxyPwdTextBox;
	private TextArea m_userAttribMappingsTextArea;
	private FlexTable m_serverPanelTable;
	private ListBox m_dirTypeLB;
	private ListBox m_guidLB;
	private ListBox m_nameAttribLB;

	// Controls used in the Users tab
	private CellTable<GwtLdapSearchInfo> m_userSearchesTable;
    private MultiSelectionModel<GwtLdapSearchInfo> m_userSearchesSelectionModel;
	private ListDataProvider<GwtLdapSearchInfo> m_userSearchesDataProvider = null;
	private VibeSimplePager m_userSearchesPager;
	private List<GwtLdapSearchInfo> m_listOfUserSearches = null;
	
	// Controls used in the Groups tab
	private CellTable<GwtLdapSearchInfo> m_groupSearchesTable;
    private MultiSelectionModel<GwtLdapSearchInfo> m_groupSearchesSelectionModel;
	private ListDataProvider<GwtLdapSearchInfo> m_groupSearchesDataProvider = null;
	private VibeSimplePager m_groupSearchesPager;
	private List<GwtLdapSearchInfo> m_listOfGroupSearches = null;
	
	private EditLdapSearchDlg m_editLdapSearchDlg = null;
	private LdapBrowserDlg m_ldapBrowserDlg;
	
	/**
	 * Callback interface to interact with the "edit ldap server config" dialog
	 * asynchronously after it loads. 
	 */
	public interface EditLdapServerConfigDlgClient
	{
		void onSuccess( EditLdapServerConfigDlg elscDlg );
		void onUnavailable();
	}

	
	/**
	 * 
	 */
	private EditLdapServerConfigDlg(
		boolean autoHide,
		boolean modal,
		int xPos,
		int yPos,
		EditSuccessfulHandler editSuccessfulHandler )
	{
		super( autoHide, modal, xPos, yPos, DlgButtonMode.OkCancel );
		
		// Create the header, content and footer of this dialog box.
		createAllDlgContent(
						GwtTeaming.getMessages().editLdapServerConfigDlg_Header(),
						editSuccessfulHandler,
						null,
						null );
	}

	/**
	 * Add the given list of group search info to the dialog
	 */
	private void addGroupSearches( List<GwtLdapSearchInfo> listOfLdapSearches )
	{
		if ( m_listOfGroupSearches == null )
			m_listOfGroupSearches = new ArrayList<GwtLdapSearchInfo>();
		else
			m_listOfGroupSearches.clear();
		
		if ( listOfLdapSearches != null )
		{
			// Make a copy of the list
			for ( GwtLdapSearchInfo nextSearch : listOfLdapSearches )
			{
				m_listOfGroupSearches.add( nextSearch );
			}
		}
		
		if ( m_groupSearchesDataProvider == null )
		{
			m_groupSearchesDataProvider = new ListDataProvider<GwtLdapSearchInfo>( m_listOfGroupSearches );
			m_groupSearchesDataProvider.addDataDisplay( m_groupSearchesTable );
		}
		else
		{
			m_groupSearchesDataProvider.setList( m_listOfGroupSearches );
		}
		
		// Clear all selections.
		m_groupSearchesSelectionModel.clear();
		
		// Go to the first page
		m_groupSearchesPager.firstPage();
		
		// Tell the table how many group searches we have.
		m_groupSearchesTable.setRowCount( m_listOfGroupSearches.size(), true );
		m_groupSearchesDataProvider.refresh();
	}


	/**
	 * Add the given list of user search info to the dialog
	 */
	private void addUserSearches( List<GwtLdapSearchInfo> listOfLdapSearches )
	{
		if ( m_listOfUserSearches == null )
			m_listOfUserSearches = new ArrayList<GwtLdapSearchInfo>();
		else
			m_listOfUserSearches.clear();
		
		if ( listOfLdapSearches != null )
		{
			// Make a copy of the list
			for ( GwtLdapSearchInfo nextSearch : listOfLdapSearches )
			{
				m_listOfUserSearches.add( nextSearch );
			}
		}
		
		if ( m_userSearchesDataProvider == null )
		{
			m_userSearchesDataProvider = new ListDataProvider<GwtLdapSearchInfo>( m_listOfUserSearches );
			m_userSearchesDataProvider.addDataDisplay( m_userSearchesTable );
		}
		else
		{
			m_userSearchesDataProvider.setList( m_listOfUserSearches );
		}
		
		// Clear all selections.
		m_userSearchesSelectionModel.clear();
		
		// Go to the first page
		m_userSearchesPager.firstPage();
		
		// Tell the table how many user searches we have.
		m_userSearchesTable.setRowCount( m_listOfUserSearches.size(), true );
		m_userSearchesDataProvider.refresh();
	}


	/**
	 * Create all the controls that make up the dialog box.
	 */
	@Override
	public Panel createContent( Object props )
	{
		GwtTeamingMessages messages;
		FlowPanel mainPanel = null;
		
		messages = GwtTeaming.getMessages();
		
		mainPanel = new FlowPanel();
		mainPanel.setStyleName( "teamingDlgBoxContent" );

		m_tabPanel = new TabPanel();
		m_tabPanel.addStyleName( "vibe-tabPanel" );

		mainPanel.add( m_tabPanel );

		// Create a panel to hold the ldap server information
		{
			Panel serverPanel;
			
			serverPanel = createServerPanel( messages );
			m_tabPanel.add( serverPanel, messages.editLdapServerConfigDlg_ServerTab() );
		}
		
		// Create a panel to hold the user search criteria
		{
			Panel usersPanel;
			
			usersPanel = createUsersPanel( messages );
			m_tabPanel.add( usersPanel, messages.editLdapServerConfigDlg_UsersTab() );
		}
		
		// Create a panel to hold the group search criteria
		{
			Panel groupsPanel;
			
			groupsPanel = createGroupsPanel( messages );
			m_tabPanel.add( groupsPanel, messages.editLdapServerConfigDlg_GroupsTab() );
		}

		m_tabPanel.selectTab( 0 );
		
		return mainPanel;
	}

	/**
	 * 
	 */
	private Panel createGroupsPanel( GwtTeamingMessages messages )
	{
		VerticalPanel groupsPanel;
		CellTable.Resources cellTableResources;
		FlowPanel menuPanel;
		
		groupsPanel = new VerticalPanel();
		
		// Create a menu
		{
			InlineLabel label;
			
			menuPanel = new FlowPanel();
			menuPanel.addStyleName( "editLdapServerConfigDlg_MenuPanel" );
			
			// Add an "Add" button.
			label = new InlineLabel( messages.editLdapServerConfigDlg_AddSearchLabel() );
			label.addStyleName( "editLdapConfigDlg_Btn" );
			label.addClickHandler( new ClickHandler()
			{
				@Override
				public void onClick( ClickEvent event )
				{
					Scheduler.ScheduledCommand cmd;
					
					cmd = new Scheduler.ScheduledCommand()
					{
						@Override
						public void execute()
						{
							invokeAddGroupSearchDlg();
						}
					};
					Scheduler.get().scheduleDeferred( cmd );
				}
			} );
			menuPanel.add( label );
			
			// Add a "Delete" button.
			label = new InlineLabel( messages.editLdapServerConfigDlg_DeleteSearchLabel() );
			label.addStyleName( "editLdapConfigDlg_Btn" );
			label.addClickHandler( new ClickHandler()
			{
				@Override
				public void onClick( ClickEvent event )
				{
					Scheduler.ScheduledCommand cmd;
					
					cmd = new Scheduler.ScheduledCommand()
					{
						@Override
						public void execute()
						{
							deleteSelectedGroupSearches();
						}
					};
					Scheduler.get().scheduleDeferred( cmd );
				}
			} );
			menuPanel.add( label );
		}
		
		// Create the CellTable that will display the list of group searches.
		cellTableResources = GWT.create( VibeCellTable.VibeCellTableResources.class );
		m_groupSearchesTable = new CellTable<GwtLdapSearchInfo>( 20, cellTableResources );
		
		// Set the widget that will be displayed when there is no search criteria
		{
			FlowPanel flowPanel;
			InlineLabel noSearchesLabel;
			
			flowPanel = new FlowPanel();
			flowPanel.addStyleName( "noObjectsFound" );
			noSearchesLabel = new InlineLabel( messages.editLdapServerConfigDlg_NoSearchesLabel() );
			flowPanel.add( noSearchesLabel );
			
			m_groupSearchesTable.setEmptyTableWidget( flowPanel );
		}
		
	    // Add a selection model so we can select searches.
	    m_groupSearchesSelectionModel = new MultiSelectionModel<GwtLdapSearchInfo>();
	    m_groupSearchesTable.setSelectionModel(
	    									m_groupSearchesSelectionModel,
	    									DefaultSelectionEventManager.<GwtLdapSearchInfo> createCheckboxManager() );

		// Add a checkbox in the first column
		{
			Column<GwtLdapSearchInfo, Boolean> ckboxColumn;
			CheckboxCell ckboxCell;
			
            ckboxCell = new CheckboxCell( true, false );
		    ckboxColumn = new Column<GwtLdapSearchInfo, Boolean>( ckboxCell )
            {
            	@Override
		        public Boolean getValue( GwtLdapSearchInfo searchInfo )
		        {
            		// Get the value from the selection model.
		            return m_groupSearchesSelectionModel.isSelected( searchInfo );
		        }
		    };
	        m_groupSearchesTable.addColumn( ckboxColumn, SafeHtmlUtils.fromSafeConstant( "<br/>" ) );
		    m_groupSearchesTable.setColumnWidth( ckboxColumn, 20, Unit.PX );			
		}
		
		// Add the "Base DN" column.  The user can click on the text in this column
		// to edit the search info.
		{
			LdapSearchBaseDnCell cell;
			Column<GwtLdapSearchInfo,GwtLdapSearchInfo> baseDnCol;

			cell = new LdapSearchBaseDnCell();
			baseDnCol = new Column<GwtLdapSearchInfo, GwtLdapSearchInfo>( cell )
			{
				@Override
				public GwtLdapSearchInfo getValue( GwtLdapSearchInfo ldapSearch )
				{
					return ldapSearch;
				}
			};
		
			baseDnCol.setFieldUpdater( new FieldUpdater<GwtLdapSearchInfo, GwtLdapSearchInfo>()
			{
				@Override
				public void update( int index, final GwtLdapSearchInfo searchInfo, GwtLdapSearchInfo value )
				{
					Scheduler.ScheduledCommand cmd;
					
					cmd = new ScheduledCommand()
					{
						@Override
						public void execute()
						{
							invokeModifyExistingGroupSearchDlg( searchInfo );
						}
					};
					Scheduler.get().scheduleDeferred( cmd );
				}
			} );
			m_groupSearchesTable.addColumn( baseDnCol, messages.editLdapServerConfigDlg_BaseDnCol() );
		}

		// Add the "User Filter" column
		{
			TextColumn<GwtLdapSearchInfo> filterCol;

			filterCol = new TextColumn<GwtLdapSearchInfo>()
			{
				@Override
				public String getValue( GwtLdapSearchInfo searchInfo )
				{
					String filter;
					
					filter = searchInfo.getFilter();
					if ( filter == null )
						filter = "";
					
					// Only show the first 50 characters.
					if ( filter.length() > 50 )
					{
						filter = filter.substring( 0, 49 );
						filter += "...";
					}
					
					return filter;
				}
			};
			m_groupSearchesTable.addColumn( filterCol, messages.editLdapServerConfigDlg_FilterCol() );
		}

		// Create a pager
		{
			m_groupSearchesPager = new VibeSimplePager();
			m_groupSearchesPager.setDisplay( m_groupSearchesTable );
		}

		groupsPanel.add( menuPanel );
		groupsPanel.add( m_groupSearchesTable );
		groupsPanel.setHorizontalAlignment( HasHorizontalAlignment.ALIGN_CENTER );
		groupsPanel.add( m_groupSearchesPager );
		groupsPanel.setCellHeight( m_groupSearchesPager, "100%" );

		return groupsPanel;
	}
	
	/**
	 * 
	 */
	private Panel createServerPanel( GwtTeamingMessages messages )
	{
		FlowPanel serverPanel;
		FlexTable.FlexCellFormatter cellFormatter; 
		Label label;
		FlowPanel tmpPanel;
		int row = 0;
		
		serverPanel = new FlowPanel();
		
		m_serverPanelTable = new FlexTable();
		cellFormatter = m_serverPanelTable.getFlexCellFormatter();
		m_serverPanelTable.setCellSpacing( 4 );
		
		// Add the server url controls
		{
			// Add a hint for the server url
			tmpPanel = new FlowPanel();
			label = new Label( messages.editLdapServerConfigDlg_ServerUrlHint() );
			label.addStyleName( "editLdapServerConfigDlg_Hint" );
			tmpPanel.add( label );
			m_serverPanelTable.setHTML( row, 0, tmpPanel.getElement().getInnerHTML() );
			cellFormatter.setColSpan( row, 0, 2 );
			++row;
			
			tmpPanel = new FlowPanel();
			label = new Label( messages.editLdapServerConfigDlg_ServerUrlLabel() );
			tmpPanel.add( label );
			m_serverPanelTable.setHTML( row, 0, tmpPanel.getElement().getInnerHTML()  );
		
			m_serverUrlTextBox = new TextBox();
			m_serverUrlTextBox.setVisibleLength( 40 );
			m_serverPanelTable.setWidget( row, 1, m_serverUrlTextBox );
			++row;
		}
		
		// Add the proxy dn controls
		{
			tmpPanel = new FlowPanel();
			label = new Label( messages.editLdapServerConfigDlg_ProxyDNLabel() );
			tmpPanel.add( label );
			m_serverPanelTable.setHTML( row, 0, tmpPanel.getElement().getInnerHTML()  );

			tmpPanel = new FlowPanel();
			m_proxyDnTextBox = new TextBox();
			m_proxyDnTextBox.setVisibleLength( 40 );
			tmpPanel.add( m_proxyDnTextBox );
			
			Image btnImg = GwtClientHelper.buildImage( GwtTeaming.getImageBundle().browseLdap().getSafeUri().asString() );
			btnImg.setTitle( GwtTeaming.getMessages().editLdapServerConfigDlg_ProxyDn_Alt() );
			FlowPanel html = new FlowPanel();
			html.add( btnImg );
			m_browseProxyDnBtn = new Button( html.getElement().getInnerHTML() );
			m_browseProxyDnBtn.addStyleName( "editLdapServerConfigDlg_BrowseDN" );
			m_browseProxyDnBtn.addClickHandler( new ClickHandler()
			{
				@Override
				public void onClick( ClickEvent event )
				{
					GwtClientHelper.deferCommand( new ScheduledCommand()
					{
						@Override
						public void execute()
						{
							String ldapUrl = m_serverUrlTextBox.getValue();
							if (null == ldapUrl) ldapUrl = "";
							ldapUrl = ldapUrl.trim();
							if (GwtClientHelper.hasString( ldapUrl ))
							     browseLdapForProxyDn( ldapUrl);
							else GwtClientHelper.deferredAlert( GwtTeaming.getMessages().editLdapServerConfigDlg_NoServerURL() );
						}
					} );
				}
			} );
			// Because most customers do not have anonymous access turned on in their ldap
			// directory we are not going to add an ldap browse button.
			// tmpPanel.add( m_browseProxyDnBtn );
			
			m_serverPanelTable.setWidget( row, 1, tmpPanel );
			++row;
		}
		
		// Add the proxy password controls
		{
			tmpPanel = new FlowPanel();
			label = new Label( messages.editLdapServerConfigDlg_ProxyPasswordLabel() );
			tmpPanel.add( label );
			m_serverPanelTable.setHTML( row, 0, tmpPanel.getElement().getInnerHTML()  );
			
			m_proxyPwdTextBox = new PasswordTextBox();
			m_proxyPwdTextBox.setVisibleLength( 20 );
			m_serverPanelTable.setWidget( row, 1, m_proxyPwdTextBox );
			++row;
		}
		
		// Add a little space
		{
			tmpPanel = new FlowPanel();
			tmpPanel.getElement().getStyle().setMarginTop( 5, Unit.PX );
			m_serverPanelTable.setWidget( row, 0, tmpPanel );
			++row;
		}
		
		// Add the controls for entering ldap attribute names.
		{
			// Add a listbox that holds the different directory types
			{
				tmpPanel = new FlowPanel();
				label = new Label( messages.editLdapServerConfigDlg_DirTypeLabel() );
				tmpPanel.add( label );
				m_serverPanelTable.setHTML( row, 0, tmpPanel.getElement().getInnerHTML() );
				
				m_dirTypeLB = new ListBox();
				m_dirTypeLB.setMultipleSelect( false );
				m_dirTypeLB.setVisibleItemCount( 1 );
				m_dirTypeLB.addItem( EDIR_NAME, DIR_TYPE_EDIR );
				m_dirTypeLB.addItem( AD_NAME, DIR_TYPE_AD );
				
				// Filr only supports eDir and AD.  Add "Other" if we are not running Filr
				if ( GwtClientHelper.isLicenseFilr() == false )
					m_dirTypeLB.addItem( messages.editLdapServerConfigDlg_Other(), DIR_TYPE_OTHER );
				
				m_dirTypeLB.addChangeHandler( new ChangeHandler()
				{
					@Override
					public void onChange( ChangeEvent event )
					{
						Scheduler.ScheduledCommand cmd;
						
						cmd = new Scheduler.ScheduledCommand()
						{
							@Override
							public void execute()
							{
								handleDirTypeChanged();
							}
						};
						Scheduler.get().scheduleDeferred( cmd );
					}
				} );
				m_serverPanelTable.setWidget( row, 1, m_dirTypeLB );
				++row;
			}
			
			// Add a little space
			{
				tmpPanel = new FlowPanel();
				tmpPanel.getElement().getStyle().setMarginTop( 5, Unit.PX );
				m_serverPanelTable.setWidget( row, 0, tmpPanel );
				++row;
			}
			
			// Add controls for entering the name of the attribute that uniquely identifies a user.
			{
				// Add a hint for the guid attribute
				tmpPanel = new FlowPanel();
				label = new Label( messages.editLdapServerConfigDlg_GuidAttributeHint1() );
				label.addStyleName( "editLdapServerConfigDlg_Hint" );
				tmpPanel.add( label );
				m_serverPanelTable.setHTML( row, 0, tmpPanel.getElement().getInnerHTML() );
				cellFormatter.setColSpan( row, 0, 2 );
				++row;
				
				tmpPanel = new FlowPanel();
				label = new Label( messages.editLdapServerConfigDlg_GuidAttributeLabel() );
				tmpPanel.add( label );
				m_serverPanelTable.setHTML( row, 0, tmpPanel.getElement().getInnerHTML() );
				
				// Add a listbox that holds the names of the attributes used by eDir and AD
				m_guidLB = new ListBox();
				m_guidLB.setMultipleSelect( false );
				m_guidLB.setVisibleItemCount( 1 );
				m_guidLB.addItem( EDIR_GUID_NAME, EDIR_GUID_NAME );
				m_guidLB.addItem( AD_GUID_NAME, AD_GUID_NAME );
				m_guidLB.addItem( messages.editLdapServerConfigDlg_Other(), OTHER_GUID_NAME );
				
				m_guidLB.addChangeHandler( new ChangeHandler()
				{
					@Override
					public void onChange( ChangeEvent event )
					{
						Scheduler.ScheduledCommand cmd;
						
						cmd = new Scheduler.ScheduledCommand()
						{
							@Override
							public void execute()
							{
								handleGuidSelectionChanged();
							}
						};
						Scheduler.get().scheduleDeferred( cmd );
					}
				} );
				m_serverPanelTable.setWidget( row, 1, m_guidLB );
				++row;
			}
		}
		
		// Add a little space
		{
			tmpPanel = new FlowPanel();
			tmpPanel.getElement().getStyle().setMarginTop( 5, Unit.PX );
			m_serverPanelTable.setWidget( row, 0, tmpPanel );
			++row;
		}
		
		// Add the name attribute controls
		{
			String productName;
			
			productName = getProductName();
			
			// Add a hint for the name attribute
			tmpPanel = new FlowPanel();
			label = new Label( messages.editLdapServerConfigDlg_NameAttributeHint( productName ) );
			label.addStyleName( "editLdapServerConfigDlg_Hint" );
			tmpPanel.add( label );
			m_serverPanelTable.setHTML( row, 0, tmpPanel.getElement().getInnerHTML() );
			cellFormatter.setColSpan( row, 0, 2 );
			++row;
			
			tmpPanel = new FlowPanel();
			label = new Label( messages.editLdapServerConfigDlg_NameAttributeLabel( productName ) );
			tmpPanel.add( label );
			m_serverPanelTable.setHTML( row, 0, tmpPanel.getElement().getInnerHTML()  );

			// Add a listbox where the user can select the attribute that is used for the
			// user's account name.
			m_nameAttribLB = new ListBox();
			m_nameAttribLB.setMultipleSelect( false );
			m_nameAttribLB.setVisibleItemCount( 1 );
			m_nameAttribLB.addChangeHandler( new ChangeHandler()
			{
				@Override
				public void onChange( ChangeEvent event )
				{
					Scheduler.ScheduledCommand cmd;
					
					cmd = new Scheduler.ScheduledCommand()
					{
						@Override
						public void execute()
						{
							handleNameAttribChanged();
						}
					};
					Scheduler.get().scheduleDeferred( cmd );
				}
			} );
			m_serverPanelTable.setWidget( row, 1, m_nameAttribLB );
			++row;
		}
		
		// Add a little space
		{
			tmpPanel = new FlowPanel();
			tmpPanel.getElement().getStyle().setMarginTop( 5, Unit.PX );
			m_serverPanelTable.setWidget( row, 0, tmpPanel );
			++row;
		}
		
		// Add the user attribute mappings controls
		{
			// Add a hint.
			tmpPanel = new FlowPanel();
			label = new Label( messages.editLdapServerConfigDlg_UserAttributeMappingHint() );
			label.getElement().getStyle().setWidth( 600, Unit.PX );
			label.addStyleName( "editLdapServerConfigDlg_Hint" );
			tmpPanel.add( label );
			m_serverPanelTable.setHTML( row, 0, tmpPanel.getElement().getInnerHTML() );
			cellFormatter.setColSpan( row, 0, 2 );
			++row;
			
			m_userAttribMappingsTextArea = new TextArea();
			m_userAttribMappingsTextArea.setVisibleLines( 8 );
			m_userAttribMappingsTextArea.setWidth( "350px" );
			m_serverPanelTable.setWidget( row, 0, m_userAttribMappingsTextArea );
			cellFormatter.setColSpan( row, 0, 2 );
			++row;
		}
		
		serverPanel.add( m_serverPanelTable );
		
		return serverPanel;
	}

	/*
	 * Runs the LDAP browser for the user DN.
	 */
	private void browseLdapForProxyDn( final String ldapUrl )
	{
		// Have we instantiated an LDAP browser yet?
		if ( null == m_ldapBrowserDlg )
		{
			// No!  Create one now...
			LdapBrowserDlg.createAsync( new LdapBrowserDlgClient()
			{
				@Override
				public void onUnavailable()
				{
					// Nothing to do.  Error handled in
					// asynchronous provider.
				}
				
				@Override
				public void onSuccess( LdapBrowserDlg ldapDlg )
				{
					// ...save it away...
					m_ldapBrowserDlg = ldapDlg;
					GwtClientHelper.deferCommand(new ScheduledCommand()
					{
						@Override
						public void execute()
						{
							// ...and run it.
							browseLdapForProxyDnImpl( ldapUrl );
						}
					} );
				}
			} );
		}
		
		else
		{
			// Yes, we've already instantiated an LDAP browser!  Simply
			// run it.
			browseLdapForProxyDnImpl( ldapUrl );
		}
	}
	
	private void browseLdapForProxyDnImpl( final String ldapUrl )
	{
		DirectoryServer server = new DirectoryServer();
		server.setAddress(       ldapUrl );
		server.setSyncUser(      null    );	// null -> Use an...
		server.setSyncPassword(  null    );	// ...anonymous...
		server.setGuidAttribute( null    ); // ...connection.

		LdapSearchInfo si = new LdapSearchInfo();
		si.setSearchObjectClass( LdapSearchInfo.RETURN_USERS );
		si.setSearchSubTree( false );
		
		LdapBrowserDlg.initAndShow(
			m_ldapBrowserDlg,
			new LdapBrowserCallback()
			{
				@Override
				public void closed()
				{
					// Ignored.  We don't care if the user closes
					// the browser.
				}

				@Override
				public void selectionChanged( LdapObject selection, DirectoryType dt )
				{
					// Since we're browsing for user DN, it will ONLY
					// be a leaf node.  Ignore non-leaf selections.
					if ( selection.isLeaf() )
					{
						m_proxyDnTextBox.setValue( selection.getDn() );
						m_ldapBrowserDlg.hide();
					}
				}
			},
			server,
			si,
			m_browseProxyDnBtn );
	}
	
	/**
	 * 
	 */
	private Panel createUsersPanel( GwtTeamingMessages messages )
	{
		VerticalPanel usersPanel;
		CellTable.Resources cellTableResources;
		FlowPanel menuPanel;
		
		usersPanel = new VerticalPanel();
		
		// Create a menu
		{
			InlineLabel label;
			
			menuPanel = new FlowPanel();
			menuPanel.addStyleName( "editLdapServerConfigDlg_MenuPanel" );
			
			// Add an "Add" button.
			label = new InlineLabel( messages.editLdapServerConfigDlg_AddSearchLabel() );
			label.addStyleName( "editLdapConfigDlg_Btn" );
			label.addClickHandler( new ClickHandler()
			{
				@Override
				public void onClick( ClickEvent event )
				{
					Scheduler.ScheduledCommand cmd;
					
					cmd = new Scheduler.ScheduledCommand()
					{
						@Override
						public void execute()
						{
							invokeAddUserSearchDlg();
						}
					};
					Scheduler.get().scheduleDeferred( cmd );
				}
			} );
			menuPanel.add( label );
			
			// Add a "Delete" button.
			label = new InlineLabel( messages.editLdapServerConfigDlg_DeleteSearchLabel() );
			label.addStyleName( "editLdapConfigDlg_Btn" );
			label.addClickHandler( new ClickHandler()
			{
				@Override
				public void onClick( ClickEvent event )
				{
					Scheduler.ScheduledCommand cmd;
					
					cmd = new Scheduler.ScheduledCommand()
					{
						@Override
						public void execute()
						{
							deleteSelectedUserSearches();
						}
					};
					Scheduler.get().scheduleDeferred( cmd );
				}
			} );
			menuPanel.add( label );
		}
		
		// Create the CellTable that will display the list of ldap servers.
		cellTableResources = GWT.create( VibeCellTable.VibeCellTableResources.class );
		m_userSearchesTable = new CellTable<GwtLdapSearchInfo>( 20, cellTableResources );
		
		// Set the widget that will be displayed when there is no search criteria
		{
			FlowPanel flowPanel;
			InlineLabel noSearchesLabel;
			
			flowPanel = new FlowPanel();
			flowPanel.addStyleName( "noObjectsFound" );
			noSearchesLabel = new InlineLabel( messages.editLdapServerConfigDlg_NoSearchesLabel() );
			flowPanel.add( noSearchesLabel );
			
			m_userSearchesTable.setEmptyTableWidget( flowPanel );
		}
		
	    // Add a selection model so we can select searches.
	    m_userSearchesSelectionModel = new MultiSelectionModel<GwtLdapSearchInfo>();
	    m_userSearchesTable.setSelectionModel(
	    									m_userSearchesSelectionModel,
	    									DefaultSelectionEventManager.<GwtLdapSearchInfo> createCheckboxManager() );

		// Add a checkbox in the first column
		{
			Column<GwtLdapSearchInfo, Boolean> ckboxColumn;
			CheckboxCell ckboxCell;
			
            ckboxCell = new CheckboxCell( true, false );
		    ckboxColumn = new Column<GwtLdapSearchInfo, Boolean>( ckboxCell )
            {
            	@Override
		        public Boolean getValue( GwtLdapSearchInfo searchInfo )
		        {
            		// Get the value from the selection model.
		            return m_userSearchesSelectionModel.isSelected( searchInfo );
		        }
		    };
	        m_userSearchesTable.addColumn( ckboxColumn, SafeHtmlUtils.fromSafeConstant( "<br/>" ) );
		    m_userSearchesTable.setColumnWidth( ckboxColumn, 20, Unit.PX );			
		}
		
		// Add the "Base DN" column.  The user can click on the text in this column
		// to edit the search info.
		{
			LdapSearchBaseDnCell cell;
			Column<GwtLdapSearchInfo,GwtLdapSearchInfo> baseDnCol;

			cell = new LdapSearchBaseDnCell();
			baseDnCol = new Column<GwtLdapSearchInfo, GwtLdapSearchInfo>( cell )
			{
				@Override
				public GwtLdapSearchInfo getValue( GwtLdapSearchInfo ldapSearch )
				{
					return ldapSearch;
				}
			};
		
			baseDnCol.setFieldUpdater( new FieldUpdater<GwtLdapSearchInfo, GwtLdapSearchInfo>()
			{
				@Override
				public void update( int index, final GwtLdapSearchInfo searchInfo, GwtLdapSearchInfo value )
				{
					Scheduler.ScheduledCommand cmd;
					
					cmd = new ScheduledCommand()
					{
						@Override
						public void execute()
						{
							invokeModifyExistingUserSearchDlg( searchInfo );
						}
					};
					Scheduler.get().scheduleDeferred( cmd );
				}
			} );
			m_userSearchesTable.addColumn( baseDnCol, messages.editLdapServerConfigDlg_BaseDnCol() );
		}

		// Add the "Filter" column
		{
			TextColumn<GwtLdapSearchInfo> filterCol;

			filterCol = new TextColumn<GwtLdapSearchInfo>()
			{
				@Override
				public String getValue( GwtLdapSearchInfo searchInfo )
				{
					String filter;
					
					filter = searchInfo.getFilter();
					if ( filter == null )
						filter = "";
					
					// Only show the first 50 characters.
					if ( filter.length() > 50 )
					{
						filter = filter.substring( 0, 49 );
						filter += "...";
					}
					
					return filter;
				}
			};
			m_userSearchesTable.addColumn( filterCol, messages.editLdapServerConfigDlg_FilterCol() );
		}

		// Create a pager
		{
			m_userSearchesPager = new VibeSimplePager();
			m_userSearchesPager.setDisplay( m_userSearchesTable );
		}

		usersPanel.add( menuPanel );
		usersPanel.add( m_userSearchesTable );
		usersPanel.setHorizontalAlignment( HasHorizontalAlignment.ALIGN_CENTER );
		usersPanel.add( m_userSearchesPager );
		usersPanel.setCellHeight( m_userSearchesPager, "100%" );

		return usersPanel;
	}
	
	/**
	 * 
	 */
	private void danceDlg()
	{
		String dirType;
		
		// Get the selected directory type
		dirType = getSelectedDirType();
		
		danceDlg( dirType );
	}
	
	/**
	 * 
	 */
	private void danceDlg( String dirType )
	{
		// Add or remove attribute names from the "account name attribute" listbox
		// depending on the selected dir type
		{
			int index;
			
			// Is sAMAccountName in the listbox?
			index = GwtClientHelper.doesListboxContainValue( m_nameAttribLB, SAM_ACCOUNT_NAME_ATTRIB );

			if ( dirType.equalsIgnoreCase( DIR_TYPE_AD ) || dirType.equalsIgnoreCase( DIR_TYPE_OTHER ) )
			{
				// Is sAMAccountName in the listbox?
				if ( index == -1 )
				{
					// No, add it.
					m_nameAttribLB.insertItem( SAM_ACCOUNT_NAME_ATTRIB, SAM_ACCOUNT_NAME_ATTRIB, 0 );
				}
			}
			else if ( dirType.equalsIgnoreCase( DIR_TYPE_EDIR ) )
			{
				// Is sAMAccountName in the listbox?
				if ( index != -1 )
				{
					// Yes, remove it.
					m_nameAttribLB.removeItem( index );
				}
			}
			
			// Is an attribute selected?
			index = m_nameAttribLB.getSelectedIndex();
			if ( index == -1 )
			{
				// No, select the first attrib.
				m_nameAttribLB.setItemSelected( 0, true );
			}
		}
		
		// Add or remove attribute names from the "Guid attribute" listbox depending on
		// the selected dir type.
		{
			int index;
			
			index = GwtClientHelper.doesListboxContainValue( m_guidLB, AD_GUID_NAME );
			
			// Is AD selected?
			if ( dirType.equalsIgnoreCase( DIR_TYPE_AD ) || dirType.equalsIgnoreCase( DIR_TYPE_OTHER ) )
			{
				// Yes
				// Is "objectGUID" in the listbox?
				if ( index == -1 )
				{
					int guidIndex;
					
					// No, add it
					m_guidLB.insertItem( AD_GUID_NAME, AD_GUID_NAME, 0 );

					// Remove "guid" as an option.
					guidIndex = GwtClientHelper.doesListboxContainValue( m_guidLB, EDIR_GUID_NAME );
					if ( guidIndex != -1 )
					{
						m_guidLB.removeItem( guidIndex );
					}
				}
			}
			else if ( dirType.equalsIgnoreCase( DIR_TYPE_EDIR ) )
			{
				int guidIndex;
				
				// Is "objectGUID" in the list box?
				if ( index != -1 )
				{
					// Yes
					// Remove it.
					m_guidLB.removeItem( index );
				}
				
				// Is "guid" in the list box?
				guidIndex = GwtClientHelper.doesListboxContainValue( m_guidLB, EDIR_GUID_NAME );
				if ( guidIndex == -1 )
				{
					// No
					// Add it
					m_guidLB.insertItem( EDIR_GUID_NAME, EDIR_GUID_NAME, 0 );
				}
			}
		}
	}
	
	/**
	 * 
	 */
	private void deleteSelectedGroupSearches()
	{
		Set<GwtLdapSearchInfo> selectedSearchInfo;
		
		selectedSearchInfo = getSelectedGroupSearches();
		
		// Do we have any searches to delete?
		if ( selectedSearchInfo != null && selectedSearchInfo.isEmpty() == false )
		{
			if ( m_serverConfig != null )
				m_serverConfig.setIsDirty( true );
			
			// Remove the selected group searches from our list.
			for ( GwtLdapSearchInfo nextSearchInfo : selectedSearchInfo )
			{
				m_listOfGroupSearches.remove( nextSearchInfo );
			}
			
			// Unselect the selected searches
			m_groupSearchesSelectionModel.clear();
			
			// Update the table to reflect the fact that we deleted a search.
			m_groupSearchesDataProvider.refresh();

			// Tell the table how many searches we have left.
			m_groupSearchesTable.setRowCount( m_listOfGroupSearches.size(), true );
		}
		else
		{
			Window.alert( GwtTeaming.getMessages().editLdapServerConfigDlg_SelectSearchesToDelete() );
		}
	}

	/**
	 * 
	 */
	private void deleteSelectedUserSearches()
	{
		Set<GwtLdapSearchInfo> selectedSearchInfo;
		
		selectedSearchInfo = getSelectedUserSearches();
		
		// Do we have any searches to delete?
		if ( selectedSearchInfo != null && selectedSearchInfo.isEmpty() == false )
		{
			if ( m_serverConfig != null )
				m_serverConfig.setIsDirty( true );
			
			// Remove the selected user searches from our list.
			for ( GwtLdapSearchInfo nextSearchInfo : selectedSearchInfo )
			{
				m_listOfUserSearches.remove( nextSearchInfo );
			}
			
			// Unselect the selected searches
			m_userSearchesSelectionModel.clear();
			
			// Update the table to reflect the fact that we deleted a search.
			m_userSearchesDataProvider.refresh();

			// Tell the table how many searches we have left.
			m_userSearchesTable.setRowCount( m_listOfUserSearches.size(), true );
		}
		else
		{
			Window.alert( GwtTeaming.getMessages().editLdapServerConfigDlg_SelectSearchesToDelete() );
		}
	}

	/**
	 * Get the data from the controls in the dialog box.
	 */
	@Override
	public Object getDataFromDlg()
	{
		if ( m_serverConfig != null )
		{
			String serverUrl;
			String proxyDn;
			String pwd;
			String guidAttrib;
			String userIdAttrib;
			String userAttribMappings;

			// Validate what the admin entered
			{
				GwtTeamingMessages messages;
				
				messages = GwtTeaming.getMessages();
				
				serverUrl = m_serverUrlTextBox.getValue();
				proxyDn = m_proxyDnTextBox.getValue();
				pwd = m_proxyPwdTextBox.getValue();
				guidAttrib = getSelectedGuidAttrib();
				userIdAttrib = getSelectedNameAttrib();
				userAttribMappings = m_userAttribMappingsTextArea.getValue();

				if ( isFieldValid( serverUrl, m_serverUrlTextBox, messages.editLdapServerConfigDlg_ErrorNoServerUrl() ) == false )
					return null;

				if ( isFieldValid( proxyDn, m_proxyDnTextBox, messages.editLdapServerConfigDlg_ErrorNoProxyDn() ) == false )
					return null;

				if ( isFieldValid( pwd, m_proxyPwdTextBox, messages.editLdapServerConfigDlg_ErrorNoPwd() ) == false )
					return null;
				
				if ( guidAttrib.equalsIgnoreCase( OTHER_GUID_NAME ) )
				{
					Window.alert( messages.editLdapServerConfigDlg_ErrorNoGuidAttrib() );
					return null;
				}
				
				if ( userIdAttrib.equalsIgnoreCase( OTHER_ATTRIB ) )
				{
					Window.alert( messages.editLdapServerConfigDlg_ErrorNoUserIdAttrib( getProductName() ) );
					return null;
				}

				if ( isFieldValid( userAttribMappings, m_userAttribMappingsTextArea, messages.editLdapServerConfigDlg_ErrorNoUserAttribMappings() ) == false )
					return null;
				
				// Did the admin define any user or group base dns?
				if ( (m_listOfUserSearches == null || m_listOfUserSearches.size() == 0) &&
					 (m_listOfGroupSearches == null || m_listOfGroupSearches.size() == 0) )
				{
					Scheduler.ScheduledCommand cmd;
					
					// No
					Window.alert( messages.editLdapServerConfigDlg_ErrorNoBaseDn() );
					
					cmd = new ScheduledCommand()
					{
						@Override
						public void execute()
						{
							m_tabPanel.selectTab( 1 );
						}
					};
					Scheduler.get().scheduleDeferred( cmd );
					
					return null;
				}
			}
			
			// Did anything change?
			{
				boolean isDirty;
				
				isDirty = m_serverConfig.isDirty();
				
				if ( GwtClientHelper.areStringsEqual( serverUrl, m_serverConfig.getServerUrl() ) == false )
					isDirty = true;
				else if ( GwtClientHelper.areStringsEqual( proxyDn, m_serverConfig.getProxyDn() ) == false )
					isDirty = true;
				else if ( GwtClientHelper.areStringsEqual( pwd, m_serverConfig.getProxyPwd() ) == false )
					isDirty = true;
				else if ( GwtClientHelper.areStringsEqual( guidAttrib, m_serverConfig.getLdapGuidAttribute() ) == false )
					isDirty = true;
				else if ( GwtClientHelper.areStringsEqual( userIdAttrib, m_serverConfig.getUserIdAttribute() ) == false )
					isDirty = true;
				else if ( GwtClientHelper.areStringsEqual( userAttribMappings, m_serverConfig.getUserAttributeMappingsAsString() ) == false )
					isDirty = true;
				
				m_serverConfig.setIsDirty( isDirty );
			}
			
			m_serverConfig.setServerUrl( serverUrl );
			m_serverConfig.setProxyDn( proxyDn );
			m_serverConfig.setProxyPwd( pwd );
			m_serverConfig.setLdapGuidAttribute( guidAttrib );
			m_serverConfig.setUserIdAttribute( userIdAttrib );
			
			// Get the user attribute mappings
			{
				Map<String,String> mappings;
				
				mappings = new HashMap<String,String>();
				
				if ( userAttribMappings != null && userAttribMappings.length() > 0 )
				{
					String[] lines;
					
					// Get each line of the mappsings.
					lines = GwtClientHelper.split( userAttribMappings, "\n" );
					
					if ( lines != null && lines.length > 0 )
					{
						for ( int i=0; i < lines.length; ++i )
						{
							String line;

							line = lines[i];
							if ( line != null && line.length() > 0 )
							{
								String[] values;

								values = GwtClientHelper.split( line, "=" );
								if ( values.length == 2 && values[0] != null && values[1] != null )
									mappings.put( values[1].trim(), values[0].trim() );
							}
						}
					}
				}
				
				m_serverConfig.setUserAttributeMappings( mappings );
			}
			
			// Update the list of user searches
			{
				m_serverConfig.emptyListOfUserSearchCriteria();
				if ( m_listOfUserSearches != null )
				{
					for ( GwtLdapSearchInfo nextSearch : m_listOfUserSearches )
					{
						m_serverConfig.addUserSearchCriteria( nextSearch );
						if ( nextSearch.isDirty() )
							m_serverConfig.setIsDirty( true );
					}
				}
			}
			
			// Update the list of group searches
			{
				m_serverConfig.emptyListOfGroupSearchCriteria();
				if ( m_listOfGroupSearches != null )
				{
					for ( GwtLdapSearchInfo nextSearch : m_listOfGroupSearches )
					{
						m_serverConfig.addGroupSearchCriteria( nextSearch );
						if ( nextSearch.isDirty() )
							m_serverConfig.setIsDirty( true );
					}
				}
			}
		}
		
		return m_serverConfig;
	}
	
	
	/**
	 * Return the widget that should get the focus when the dialog is shown. 
	 */
	@Override
	public FocusWidget getFocusWidget()
	{
		return null;
	}
	
	/**
	 * Return the product name
	 */
	private String getProductName()
	{
		return GwtClientHelper.getProductName();
	}
	
	/**
	 * Return the selected directory type
	 */
	private String getSelectedDirType()
	{
		String dirType;
		int index;
		
		dirType = DIR_TYPE_EDIR;
		index = m_dirTypeLB.getSelectedIndex();
		if ( index >= 0 )
			dirType = m_dirTypeLB.getValue( index );
		
		return dirType;
	}
	
	/**
	 * Return a list of selected group searches.
	 */
	public Set<GwtLdapSearchInfo> getSelectedGroupSearches()
	{
		return m_groupSearchesSelectionModel.getSelectedSet();
	}
	
	/**
	 * Return the name of the attribute used for the guid
	 */
	private String getSelectedGuidAttrib()
	{
		String guid;
		int index;
		
		guid = "GUID";
		index = m_guidLB.getSelectedIndex();
		if ( index >= 0 )
			guid = m_guidLB.getValue( index );
		
		return guid;
	}
	
	/**
	 * Return the name of the ldap attribute that is used for the account name
	 */
	private String getSelectedNameAttrib()
	{
		int index;
		String name;
		
		index = m_nameAttribLB.getSelectedIndex();
		if ( index == -1 )
			index = 0;

		name = m_nameAttribLB.getValue( index );
		
		return name;
	}
	
	/**
	 * Return a list of selected user searches.
	 */
	public Set<GwtLdapSearchInfo> getSelectedUserSearches()
	{
		return m_userSearchesSelectionModel.getSelectedSet();
	}

	/**
	 * Return the directory type as found in the ldap configuration 
	 */
	private String getDirTypeFromConfig( GwtLdapConnectionConfig config )
	{
		String dirType;
		String guidValue;

		guidValue = config.getLdapGuidAttribute();
		if ( guidValue == null || guidValue.length() == 0 )
			dirType = DIR_TYPE_EDIR;
		else
		{
			dirType = guidValue;
			if ( dirType.equalsIgnoreCase( DIR_TYPE_AD ) == false && dirType.equalsIgnoreCase( DIR_TYPE_EDIR ) == false )
				dirType = DIR_TYPE_OTHER;
		}
		
		return dirType;
	}
	
	/*
	 * Returns a DirectoryServer object the LDAP browser can use to
	 * authenticate to the tree.
	 */
	private DirectoryServer getDirectoryServer()
	{
		DirectoryServer server = new DirectoryServer();
		server.setAddress( m_serverUrlTextBox.getValue() );
		server.setSyncUser( m_proxyDnTextBox.getValue() );
		server.setSyncPassword( m_proxyPwdTextBox.getValue() );
		server.setGuidAttribute( getSelectedGuidAttrib() );
		return server;
	}

	/**
	 * This method gets called when the dir type changes
	 */
	private void handleDirTypeChanged()
	{
		String dirType;
		
		danceDlg();
		
		dirType = getSelectedDirType();

		if ( DIR_TYPE_AD.equalsIgnoreCase( dirType ) )
		{
			// User selected Active Directory.
			// Select "objectGUID" as the guid attribute
			GwtClientHelper.selectListboxItemByValue( m_guidLB, AD_GUID_NAME );
			
			// Select sAMAccountName as the name attribute.
			GwtClientHelper.selectListboxItemByValue( m_nameAttribLB, SAM_ACCOUNT_NAME_ATTRIB );
		}
		else if ( DIR_TYPE_EDIR.equalsIgnoreCase( dirType ) )
		{
			// User selected eDirectory
			// Select "GUID" as the guid attribute
			GwtClientHelper.selectListboxItemByValue( m_guidLB, EDIR_GUID_NAME );
			
			// Select cn as the name attribute
			GwtClientHelper.selectListboxItemByValue( m_nameAttribLB, CN_ATTRIB );
		}
	}
	
	/**
	 * This method gets called when the guid selection changes
	 */
	private void handleGuidSelectionChanged()
	{
		String guidName;
		
		// Did the user select "Other"? 
		guidName = getSelectedGuidAttrib();
		if ( guidName != null && guidName.equalsIgnoreCase( OTHER_GUID_NAME ) )
		{
			String attribName;
			String msg;
			
			// Yes, prompt the user for the name of the guid attribute.
			msg = GwtTeaming.getMessages().editLdapServerConfigDlg_NameAttributePrompt( getProductName() );
			attribName = Window.prompt( msg, "" );
			
			// Did the user enter the name of an ldap attribute?
			if ( attribName != null && attribName.length() > 0 )
			{
				int index;
				
				// Yes
				// Does the name already exist in our listbox?
				index = GwtClientHelper.doesListboxContainValue( m_guidLB, attribName );
				if ( index == -1 )
				{
					// No, add it before "other"
					index = GwtClientHelper.doesListboxContainValue( m_guidLB, OTHER_ATTRIB );
					m_guidLB.insertItem( attribName, attribName, index );
				}
				
				// Select the name that was entered
				GwtClientHelper.selectListboxItemByValue( m_guidLB, attribName );
			}
		}
	}
	
	/**
	 * This method gets called when the name attribute changes
	 */
	private void handleNameAttribChanged()
	{
		String attrib;
		
		// Did the user select "Other"?
		attrib = getSelectedNameAttrib();
		if ( attrib != null && attrib.equalsIgnoreCase( OTHER_ATTRIB ) )
		{
			String attribName;
			String msg;
			
			// Yes, prompt the user for the name of the attribute.
			msg = GwtTeaming.getMessages().editLdapServerConfigDlg_NameAttributePrompt( getProductName() );
			attribName = Window.prompt( msg, "" );
			
			// Did the user enter the name of an ldap attribute?
			if ( attribName != null && attribName.length() > 0 )
			{
				int index;
				
				// Yes
				// Does the name already exist in our listbox?
				index = GwtClientHelper.doesListboxContainValue( m_nameAttribLB, attribName );
				if ( index == -1 )
				{
					// No, add it before "other"
					index = GwtClientHelper.doesListboxContainValue( m_nameAttribLB, OTHER_ATTRIB );
					m_nameAttribLB.insertItem( attribName, attribName, index );
				}
				
				// Select the name that was entered
				GwtClientHelper.selectListboxItemByValue( m_nameAttribLB, attribName );
			}
		}
	}
	
	/**
	 * See if the user provided a String value for the given field.
	 */
	private boolean isFieldValid( String value, final FocusWidget inputWidget, String errMsg )
	{
		Scheduler.ScheduledCommand cmd;
		
		if ( value != null && value.length() > 0 )
			return true;
		
		Window.alert( errMsg );

		cmd = new ScheduledCommand()
		{
			@Override
			public void execute()
			{
				inputWidget.setFocus( true );
			}
		};
		Scheduler.get().scheduleDeferred( cmd );
		
		return false;
	}
	
	/**
	 * 
	 */
	public void init( GwtLdapConnectionConfig config, String defaultUserFilter, String defaultGroupFilter )
	{
		String dirType;
		
		// Add all the possible attribute names to the listbox
		{
			GwtTeamingMessages messages;
			
			messages = GwtTeaming.getMessages();
			
			m_nameAttribLB.clear();
			m_nameAttribLB.addItem( SAM_ACCOUNT_NAME_ATTRIB, SAM_ACCOUNT_NAME_ATTRIB );
			m_nameAttribLB.addItem( CN_ATTRIB, CN_ATTRIB );
			m_nameAttribLB.addItem( messages.editLdapServerConfigDlg_Other(), OTHER_ATTRIB );
		}
		
		m_serverConfig = config;
		m_defaultUserFilter = defaultUserFilter;
		m_defaultGroupFilter = defaultGroupFilter;
		
		m_serverUrlTextBox.setValue( "" );
		m_proxyDnTextBox.setValue( "" );
		m_proxyPwdTextBox.setValue( "" );
		m_userAttribMappingsTextArea.setValue( "" );
		
		if ( config == null )
			return;
		
		dirType = getDirTypeFromConfig( config );
		danceDlg( dirType );

		m_serverUrlTextBox.setValue( config.getServerUrl() );
		m_proxyDnTextBox.setValue( config.getProxyDn() );
		m_proxyPwdTextBox.setValue( config.getProxyPwd() );
		setDirType( config );
		setGuidValue( config.getLdapGuidAttribute() );
		setNameAttribValue( config.getUserIdAttribute() );
		m_userAttribMappingsTextArea.setValue( config.getUserAttributeMappingsAsString() );
		
		addUserSearches( config.getListOfUserSearchCriteria() );
		
		addGroupSearches( config.getListOfGroupSearchCriteria() );

		if ( m_serverConfig.isDirty() == false )
			m_serverConfig.setIsDirtySearchInfo( false );
	}

	/**
	 * 
	 */
	private void invokeAddGroupSearchDlg()
	{
		GwtLdapSearchInfo searchInfo;
		EditSuccessfulHandler editSuccessfulHandler;
		
		searchInfo = new GwtLdapSearchInfo();
		searchInfo.setFilter( m_defaultGroupFilter );
		
		editSuccessfulHandler = new EditSuccessfulHandler()
		{
			@Override
			public boolean editSuccessful( Object obj )
			{
				if ( obj instanceof GwtLdapSearchInfo )
				{
					if ( m_serverConfig != null )
						m_serverConfig.setIsDirty( true );
					
					// Add the new search to our list
					m_listOfGroupSearches.add( (GwtLdapSearchInfo) obj );
					
					// Tell the table how many searches we have.
					m_groupSearchesTable.setRowCount( m_listOfGroupSearches.size(), true );
					
					m_groupSearchesDataProvider.refresh();
				}
				
				return true;
			}
		};
		
		invokeModifyLdapSearchDlg( searchInfo, false, editSuccessfulHandler );
	}
	
	/**
	 * 
	 */
	private void invokeAddUserSearchDlg()
	{
		GwtLdapSearchInfo searchInfo;
		EditSuccessfulHandler editSuccessfulHandler;
		
		searchInfo = new GwtLdapSearchInfo();
		searchInfo.setFilter( m_defaultUserFilter );
		
		editSuccessfulHandler = new EditSuccessfulHandler()
		{
			@Override
			public boolean editSuccessful( Object obj )
			{
				if ( obj instanceof GwtLdapSearchInfo )
				{
					if ( m_serverConfig != null )
						m_serverConfig.setIsDirty( true );
					
					// Add the new search to our list
					m_listOfUserSearches.add( (GwtLdapSearchInfo) obj );
					
					// Tell the table how many searches we have.
					m_userSearchesTable.setRowCount( m_listOfUserSearches.size(), true );
					
					m_userSearchesDataProvider.refresh();
				}
				
				return true;
			}
		};
		
		invokeModifyLdapSearchDlg( searchInfo, true, editSuccessfulHandler );
	}
	
	/**
	 * 
	 */
	private void invokeModifyExistingGroupSearchDlg( GwtLdapSearchInfo searchInfo )
	{
		EditSuccessfulHandler editSuccessfulHandler;
		
		editSuccessfulHandler = new EditSuccessfulHandler()
		{
			@Override
			public boolean editSuccessful( Object obj )
			{
				if ( obj instanceof GwtLdapSearchInfo )
				{
					int index;
					
					index = m_listOfGroupSearches.indexOf( obj );
					if ( index != -1 )
					{
						m_groupSearchesDataProvider.getList().set( index, (GwtLdapSearchInfo) obj );
					}
				}
				
				return true;
			}
		};
		
		invokeModifyLdapSearchDlg( searchInfo, false, editSuccessfulHandler );
	}
	
	/**
	 * 
	 */
	private void invokeModifyExistingUserSearchDlg( GwtLdapSearchInfo searchInfo )
	{
		EditSuccessfulHandler editSuccessfulHandler;
		
		editSuccessfulHandler = new EditSuccessfulHandler()
		{
			@Override
			public boolean editSuccessful( Object obj )
			{
				if ( obj instanceof GwtLdapSearchInfo )
				{
					int index;
					
					index = m_listOfUserSearches.indexOf( obj );
					if ( index != -1 )
					{
						m_userSearchesDataProvider.getList().set( index, (GwtLdapSearchInfo) obj );
					}
				}
				
				return true;
			}
		};
		
		invokeModifyLdapSearchDlg( searchInfo, true, editSuccessfulHandler );
	}
	
	/**
	 * 
	 */
	private void invokeModifyLdapSearchDlg(
		final GwtLdapSearchInfo searchInfo,
		final boolean isUserSearch,
		final EditSuccessfulHandler editSuccessfulHandler )
	{
		if ( m_editLdapSearchDlg == null )
		{
			int x;
			int y;
			
			if ( isUserSearch )
			{
				x = m_userSearchesTable.getAbsoluteLeft();
				y = m_userSearchesTable.getAbsoluteTop();
			}
			else
			{
				x = m_groupSearchesTable.getAbsoluteLeft();
				y = m_groupSearchesTable.getAbsoluteTop();
			}
			
			// Create the dialog.
			EditLdapSearchDlg.createDlg(
									false,
									true,
									x,
									y,
									editSuccessfulHandler,
									new EditLdapSearchDlgClient()
			{			
				@Override
				public void onUnavailable()
				{
					// Nothing to do.  Error handled in asynchronous provider.
				}
				
				@Override
				public void onSuccess( EditLdapSearchDlg elsDlg )
				{
					ScheduledCommand cmd;
					
					m_editLdapSearchDlg = elsDlg;

					cmd = new ScheduledCommand()
					{
						@Override
						public void execute() 
						{
							invokeModifyLdapSearchDlg( searchInfo, isUserSearch, editSuccessfulHandler );
						}
					};
					Scheduler.get().scheduleDeferred( cmd );
				}
			} );
		}
		else
		{
			// Show the dialog.
			EditLdapSearchDlg.initAndShow(
										m_editLdapSearchDlg,
										getDirectoryServer(),
										searchInfo,
										isUserSearch,
										editSuccessfulHandler,
										null );
		}
	}

	/**
	 * 
	 */
	private void setDirType( GwtLdapConnectionConfig config )
	{
		String dirType;
		
		if ( config == null )
			return;

		dirType = getDirTypeFromConfig( config );

		GwtClientHelper.selectListboxItemByValue( m_dirTypeLB, dirType );
	}
	
	/**
	 * 
	 */
	private void setGuidValue( String guidValue )
	{
		if ( guidValue == null || guidValue.length() == 0 )
			guidValue = EDIR_GUID_NAME;

		// Does the given attribute exist in our listbox?
		if ( GwtClientHelper.doesListboxContainValue( m_guidLB, guidValue ) == -1 )
		{
			int index;
			
			// No, add it to the list box before other.
			index = GwtClientHelper.doesListboxContainValue( m_guidLB, OTHER_ATTRIB );
			m_guidLB.insertItem( guidValue, guidValue, index );
		}
		
		GwtClientHelper.selectListboxItemByValue( m_guidLB, guidValue );
	}
	
	/**
	 * 
	 */
	private void setNameAttribValue( String attrib )
	{
		if ( attrib == null || attrib.length() == 0 )
			attrib = CN_ATTRIB;
		
		// Does the given attribute exist in our listbox?
		if ( GwtClientHelper.doesListboxContainValue( m_nameAttribLB, attrib ) == -1 )
		{
			int index;
			
			// No, add it to the list box before other.
			index = GwtClientHelper.doesListboxContainValue( m_nameAttribLB, OTHER_ATTRIB );
			m_nameAttribLB.insertItem( attrib, attrib, index );
		}
		
		GwtClientHelper.selectListboxItemByValue( m_nameAttribLB, attrib );
	}
	
	/**
	 * Executes code through the GWT.runAsync() method to ensure that all of the
	 * executing code is in this split point.
	 */
	public static void createDlg(
		final boolean autoHide,
		final boolean modal,
		final int left,
		final int top,
		final EditSuccessfulHandler editSuccessfulHandler,
		final EditLdapServerConfigDlgClient elscDlgClient )
	{
		GWT.runAsync( EditLdapServerConfigDlg.class, new RunAsyncCallback()
		{
			@Override
			public void onFailure( Throwable reason )
			{
				Window.alert( GwtTeaming.getMessages().codeSplitFailure_EditLdapServerConfigDlg() );
				if ( elscDlgClient != null )
				{
					elscDlgClient.onUnavailable();
				}
			}

			@Override
			public void onSuccess()
			{
				EditLdapServerConfigDlg elscDlg;
				
				elscDlg = new EditLdapServerConfigDlg(
												autoHide,
												modal,
												left,
												top,
												editSuccessfulHandler );
				
				if ( elscDlgClient != null )
					elscDlgClient.onSuccess( elscDlg );
			}
		} );
	}

	/**
	 * Executes code through the GWT.runAsync() method to ensure that all of the
	 * executing code is in this split point.
	 */
	public static void initAndShow(
		final EditLdapServerConfigDlg dlg,
		final GwtLdapConnectionConfig config,
		final String defaultUserFilter,
		final String defaultGroupFilter,
		final EditSuccessfulHandler editSuccessfulHandler,
		final EditLdapServerConfigDlgClient elscDlgClient )
	{
		GWT.runAsync( EditLdapServerConfigDlg.class, new RunAsyncCallback()
		{
			@Override
			public void onFailure( Throwable reason )
			{
				Window.alert( GwtTeaming.getMessages().codeSplitFailure_EditLdapServerConfigDlg() );
				if ( elscDlgClient != null )
				{
					elscDlgClient.onUnavailable();
				}
			}

			@Override
			public void onSuccess()
			{
				dlg.init(
						config,
						defaultUserFilter,
						defaultGroupFilter );
						
				dlg.initHandlers( editSuccessfulHandler, null );
				dlg.show();
			}
		} );
	}
}
