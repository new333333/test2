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

import java.util.List;

import org.kablink.teaming.gwt.client.EditCanceledHandler;
import org.kablink.teaming.gwt.client.EditSuccessfulHandler;
import org.kablink.teaming.gwt.client.GwtDynamicGroupMembershipCriteria;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingMessages;
import org.kablink.teaming.gwt.client.ldapbrowser.LdapObject;
import org.kablink.teaming.gwt.client.ldapbrowser.LdapSearchInfo;
import org.kablink.teaming.gwt.client.ldapbrowser.LdapServer.DirectoryType;
import org.kablink.teaming.gwt.client.rpc.shared.IntegerRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.TestGroupMembershipCriteriaCmd;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.widgets.DlgBox;
import org.kablink.teaming.gwt.client.widgets.LdapBrowserDlg.LdapBrowseListCallback;
import org.kablink.teaming.gwt.client.widgets.LdapBrowserDlg.LdapBrowserDlgClient;

import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.FlexTable.FlexCellFormatter;

/**
 * This dialog can be used to modify the dynamic membership of a group.
 * 
 * @author jwootton
 */
public class ModifyDynamicMembershipDlg extends DlgBox
{
	private TextBox m_baseDnTxtBox;
	private TextArea m_filterTextArea;
	private CheckBox m_searchSubtreeCB;
	private CheckBox m_updateCB;
	private Button m_currentMembershipBtn;
	private FlowPanel m_inProgressPanel;
	private ShowDynamicMembershipDlg m_dynamicMembershipDlg;
	private Long m_groupId;
	private Button m_browseBaseDnBtn;				// LDAP browse button next to m_baseDnTxtBox.
	private LdapBrowserDlg m_ldapBrowserDlg;		// An instance of an LdapBrowserDlg.
	private List<LdapBrowseSpec> m_ldapServerList;	// List of LDAP servers obtained the first time m_browseBaseDnBtn is clicked.
	
	/**
	 * 
	 */
	public ModifyDynamicMembershipDlg(
		boolean autoHide,
		boolean modal,
		EditSuccessfulHandler editSuccessfulHandler,	// We will call this handler when the user presses the ok button
		EditCanceledHandler editCanceledHandler, 		// This gets called when the user presses the Cancel button
		int xPos,
		int yPos )
	{
		super( autoHide, modal, xPos, yPos );

		// Create the header, content and footer of this dialog box.
		createAllDlgContent( GwtTeaming.getMessages().modifyDynamicMembershipDlgHeader(), editSuccessfulHandler, editCanceledHandler, null ); 
	}
	
	/**
	 * Create all the controls that make up the dialog box.
	 */
	@Override
	public Panel createContent( Object props )
	{
		GwtTeamingMessages messages;
		Panel mainPanel;
		FlexTable table;
		FlexCellFormatter cellFormatter;
		int nextRow;
		
		messages = GwtTeaming.getMessages();
		
		mainPanel = new FlowPanel();
		mainPanel.setStyleName( "teamingDlgBoxContent" );
		
		// Create a table to hold the controls.
		table = new FlexTable();
		table.setCellSpacing( 4 );
		table.addStyleName( "dlgContent" );
		
		cellFormatter = table.getFlexCellFormatter();
		
		nextRow = 0;
		
		// Add a label that will be used to display the current membership information.
		{
			ClickHandler clickHandler;
			
			m_currentMembershipBtn = new Button( "" );
			m_currentMembershipBtn.getElement().getStyle().setMarginBottom( 8, Unit.PX );
			
			clickHandler = new ClickHandler()
			{
				/**
				 * 
				 */
				@Override
				public void onClick( ClickEvent event )
				{
					GwtClientHelper.deferCommand( new ScheduledCommand()
					{
						/**
						 * 
						 */
						@Override
						public void execute()
						{
							// Invoke the Show Dynamic Membership dialog.
							invokeShowDynamicMembershipDlg();
						}
					} );
				}
				
			};
			m_currentMembershipBtn.addClickHandler( clickHandler );

			table.setWidget( nextRow, 0, m_currentMembershipBtn );
			cellFormatter.setColSpan( nextRow, 0, 2 );
			++nextRow;
		}
		
		// Create the controls for "Base DN"
		{
			InlineLabel label;
			
			label = new InlineLabel( messages.modifyDynamicMembershipDlgBaseDnLabel() );
			table.setWidget( nextRow, 0, label );
			
			FlowPanel tmpPanel = new FlowPanel();
			m_baseDnTxtBox = new TextBox();
			m_baseDnTxtBox.setVisibleLength( 40 );
			tmpPanel.add( m_baseDnTxtBox );
			
			FlowPanel html = new FlowPanel();
			Image btnImg = GwtClientHelper.buildImage( GwtTeaming.getImageBundle().browseLdap().getSafeUri().asString() );
			btnImg.setTitle( GwtTeaming.getMessages().modifyDynamicMembershipDlgBaseDnAlt() );
			html.add( btnImg );
			m_browseBaseDnBtn = new Button( html.getElement().getInnerHTML() );
			m_browseBaseDnBtn.addStyleName( "dynamicGroupMembership_BrowseDN" );
			m_browseBaseDnBtn.addClickHandler( new ClickHandler()
			{
				@Override
				public void onClick( ClickEvent event )
				{
					GwtClientHelper.deferCommand( new ScheduledCommand()
					{
						@Override
						public void execute()
						{
							browseLdapForBaseDNAsync();
						}
					} );
				}
			} );
			tmpPanel.add( m_browseBaseDnBtn );
			table.setWidget( nextRow, 1, tmpPanel );
			++nextRow;
		}
		
		// Create the controls for "ldap filter"
		{
			table.setText( nextRow, 0, messages.modifyDynamicMembershipDlgLdapFilterLabel() );
			++nextRow;
			
			m_filterTextArea = new TextArea();
			m_filterTextArea.setCharacterWidth( 56 );
			m_filterTextArea.setVisibleLines( 15 );
			table.setWidget( nextRow, 0, m_filterTextArea );
			cellFormatter.setColSpan( nextRow, 0, 2 );
			
			++nextRow;
		}
		
		// Add the "Search subtree" checkbox
		{
			m_searchSubtreeCB = new CheckBox( messages.modifyDynamicMembershipDlgSearchSubtreeLabel() );
			table.setWidget( nextRow, 0, m_searchSubtreeCB );
			cellFormatter.setColSpan( nextRow, 0, 2 );
			
			++nextRow;
		}
		
		// Add the "Update group membership during scheduled ldap synchronization" checkbox
		{
			m_updateCB = new CheckBox( messages.modifyDynamicMembershipDlgUpdateLabel() );
			table.setWidget( nextRow, 0, m_updateCB );
			cellFormatter.setColSpan( nextRow, 0, 2 );
			
			++nextRow;
		}
		
		// Add a spacer
		{
			Label spacer;
			
			spacer = new Label();
			spacer.getElement().getStyle().setMarginBottom( 6, Unit.PX );
			table.setWidget( nextRow, 0, spacer );
			++nextRow;
		}
		
		// Add a "Test ldap query" button
		{
			Button btn;
			ClickHandler clickHandler;
			
			btn = new Button( messages.modifyDynamicMembershipDlgTestQueryLabel() );
			btn.addStyleName( "teamingButton" );
			table.setWidget( nextRow, 0, btn );
			//!!!cellFormatter.setColSpan( nextRow, 0, 2 );
			
			clickHandler = new ClickHandler()
			{
				/**
				 * 
				 */
				@Override
				public void onClick( ClickEvent event )
				{
					GwtClientHelper.deferCommand( new ScheduledCommand()
					{
						/**
						 * 
						 */
						@Override
						public void execute()
						{
							// Invoke the "Execute LDAP Query" dialog.  This dialog will
							// execute the ldap query and tell the user how many users/grops
							// were found.
							invokeExecuteLdapQueryDlg();
						}
					} );
				}
				
			};
			btn.addClickHandler( clickHandler );
			
			// Add a panel that will display a "Executing ldap query..." message
			{
				ImageResource imgResource;
				Image img;
				InlineLabel label;
				
				m_inProgressPanel = new FlowPanel();
				m_inProgressPanel.addStyleName( "testLdapQueryInProgress" );
				m_inProgressPanel.setVisible( false );

				imgResource = GwtTeaming.getImageBundle().spinner16();
				img = new Image( imgResource );
				img.getElement().setAttribute( "align", "absmiddle" );
				m_inProgressPanel.add( img );

				label = new InlineLabel( GwtTeaming.getMessages().modifyDynamicMembershipDlgTestQueryInProgressLabel() );
				m_inProgressPanel.add( label );
				
				table.setWidget( nextRow, 1, m_inProgressPanel );
			}

			++nextRow;
		}
		mainPanel.add( table );

		return mainPanel;
	}

	/*
	 * Runs the LDAP browser for the base DN.
	 */
	private void browseLdapForBaseDNAsync()
	{
		GwtClientHelper.deferCommand( new ScheduledCommand()
		{
			@Override
			public void execute()
			{
				browseLdapForBaseDNNow();
			}
		} );
	}
	
	private void browseLdapForBaseDNNow()
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
					// ...save it away and run it.
					m_ldapBrowserDlg = ldapDlg;
					getLdapServersAndRunLdapBrowserAsync();
				}
			} );
		}
		
		else
		{
			// Yes, we've already instantiated an LDAP browser!  Simply
			// run it.
			getLdapServersAndRunLdapBrowserNow();
		}
	}

	/*
	 * Gets the list of LDAP servers and runs the browser on them.
	 */
	private void getLdapServersAndRunLdapBrowserAsync()
	{
		GwtClientHelper.deferCommand(new ScheduledCommand()
		{
			@Override
			public void execute()
			{
				// ...and run it.
				getLdapServersAndRunLdapBrowserNow();
			}
		} );
	}
	
	private void getLdapServersAndRunLdapBrowserNow()
	{
		// Have we obtained the list of LDAP servers yet?
		if ( null == m_ldapServerList )
		{
			// No!  Read them now...
			LdapSearchInfo si = new LdapSearchInfo();
			si.setSearchObjectClass(LdapSearchInfo.RETURN_CONTAINERS_ONLY);
			si.setSearchSubTree(false);
			LdapBrowserDlg.getLdapServerList( si, new LdapBrowseListCallback()
			{
				@Override
				public void onFailure()
				{
					// Nothing to do.  Error handled in
					// asynchronous provider.
				}
				
				@Override
				public void onSuccess( List<LdapBrowseSpec> serverList )
				{
					// ...save them away and run the dialog.
					m_ldapServerList = serverList;
					runLdapBrowserAsync();
				}
			} );
		}
		
		else
		{
			// Yes, we've already obtained the list of LDAP servers!
			// Simply run the dialog.
			runLdapBrowserNow();
		}
	}

	/*
	 * Runs the LDAP browser.
	 */
	private void runLdapBrowserAsync()
	{
		GwtClientHelper.deferCommand(new ScheduledCommand()
		{
			@Override
			public void execute()
			{
				runLdapBrowserNow();
			}
		} );
	}
	
	private void runLdapBrowserNow()
	{
		// Do we have any LDAP servers to browse?
		int c = ( ( null == m_ldapServerList ) ? 0 : m_ldapServerList.size() );
		if ( 0 == c )
		{
			// No!  Tell the user about the problem and bail.
			GwtClientHelper.deferredAlert( GwtTeaming.getMessages().modifyNetFolderServerDlg_NoLdapServers() );
			return;
		}
		
		// Run the LDAP browser using the list of LDAP servers.
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
					m_baseDnTxtBox.setValue( selection.getDn() );
					m_ldapBrowserDlg.hide();
				}
			},
			m_ldapServerList,		// List of LDAP servers that can be browsed.
			m_browseBaseDnBtn );	// The dialog is positioned relative to this.
	}
	
	/**
	 * Return the criteria for group membership
	 */
	@Override
	public Object getDataFromDlg()
	{
		GwtDynamicGroupMembershipCriteria membershipCriteria;
		
		membershipCriteria = new GwtDynamicGroupMembershipCriteria();
		
		// Get the base dn
		membershipCriteria.setBaseDn( m_baseDnTxtBox.getText() );
		
		// Get the ldap filter.
		membershipCriteria.setLdapFilter( m_filterTextArea.getText() );
		
		// Get the "search subtree" value.
		membershipCriteria.setSearchSubtree( m_searchSubtreeCB.getValue() );
		
		// Get the "Update group membership during scheduled ldap synchronization" value.
		membershipCriteria.setUpdateDuringLdapSync( m_updateCB.getValue() );
		
		return membershipCriteria;
	}
	
	
	/**
	 * Return the widget that should get the focus when the dialog is shown. 
	 */
	@Override
	public FocusWidget getFocusWidget()
	{
		return m_baseDnTxtBox;
	}
	
	/**
	 * 
	 */
	public void init( GwtDynamicGroupMembershipCriteria membershipCriteria, Integer currentMembershipCnt, Long groupId )
	{
		m_groupId = groupId;
		
		// Forget about any list of LDAP servers.  The list may have
		// changed since this dialog was last run and setting this to
		// null will cause it to be reloaded when needed.
		m_ldapServerList = null;
		
		if ( membershipCriteria != null )
		{
			// Initialeze the base dn text box.
			m_baseDnTxtBox.setValue( membershipCriteria.getBaseDn() );
			
			// Initialize the ldap filter text area
			m_filterTextArea.setText( membershipCriteria.getLdapFilter() );
			
			// Update the "search subtree" checkbox.
			m_searchSubtreeCB.setValue( membershipCriteria.getSearchSubtree() );
			
			// Update the "Update group membership during scheduled ldap synchronization" checkbox.
			m_updateCB.setValue( membershipCriteria.getUpdateDuringLdapSync() );
			
			// Update the "current membership: xxx user/groups"
			setCurrentMembershipCount( currentMembershipCnt );
			
			// If we are dealing with an existing group, enable the "Current membership" button.
			// Otherwise, disable it.
			if ( m_groupId != null )
				m_currentMembershipBtn.setEnabled( true );
			else
				m_currentMembershipBtn.setEnabled( false );
		}
	}

	/**
	 * Invoke the "Execute ldap query" dialog.  This dialog will execute the ldap query
	 * entered by the user and will tell the user how many users/groups were found
	 */
	private void invokeExecuteLdapQueryDlg()
	{
		GwtDynamicGroupMembershipCriteria membershipCriteria;
		
		// Get the membership criteria entered by the user.
		membershipCriteria = (GwtDynamicGroupMembershipCriteria) getDataFromDlg();
		
		// Is there an outstanding rpc request to test the group membership criteria?
		if ( m_inProgressPanel.isVisible() == false )
		{
			TestGroupMembershipCriteriaCmd cmd;
			AsyncCallback<VibeRpcResponse> rpcCallback;
			
			// No
			// Make the "Executing ldap query..." visible
			m_inProgressPanel.setVisible( true );
			
			rpcCallback = new AsyncCallback<VibeRpcResponse>()
			{
				@Override
				public void onFailure( Throwable caught )
				{
					// Is this dialog still visible?
					if ( isVisible() )
					{
						// Yes, tell the user about the error
						GwtClientHelper.handleGwtRPCFailure(
							caught,
							GwtTeaming.getMessages().rpcFailure_TestGroupMembershipCriteria() );

						// Hide the "Executing ldap query..." visible
						m_inProgressPanel.setVisible( false );
					}
				}
	
				@Override
				public void onSuccess( VibeRpcResponse result )
				{
					// Is this dialog still visible?
					if ( isVisible() )
					{
						IntegerRpcResponseData responseData;
						Integer count;
						
						// Yes, tell the user the results.
						responseData = (IntegerRpcResponseData) result.getResponseData();
						count = responseData.getIntegerValue();
						Window.alert( GwtTeaming.getMessages().modifyDynamicMembershipDlgTestQueryResults( count ) );

						// Hide the "Executing ldap query..." visible
						m_inProgressPanel.setVisible( false );
					}
				}						
			};
			
			// Issue an rpc request to test the membership criteria
			cmd = new TestGroupMembershipCriteriaCmd( membershipCriteria );
			GwtClientHelper.executeCommand( cmd, rpcCallback );
		}
	}
	
	/**
	 * Invoke the Show Dynamic Membership dialog.  This dialog will display a list of all the
	 * dynamic members of a group.
	 */
	private void invokeShowDynamicMembershipDlg()
	{
		int x;
		int y;
		
		// Get the position of this dialog.
		x = getAbsoluteLeft() + 50;
		y = getAbsoluteTop() + 50;
		
		if ( m_dynamicMembershipDlg == null )
		{
			m_dynamicMembershipDlg = new ShowDynamicMembershipDlg( false, true, x, y );
		}

		m_dynamicMembershipDlg.init( "", m_groupId );
		m_dynamicMembershipDlg.setPopupPosition( x, y );
		m_dynamicMembershipDlg.show();
	}

	/**
	 * 
	 */
	public void setCurrentMembershipCount( Integer count )
	{
		// Do we have a count?
		if ( count != null )
		{
			// Yes
			m_currentMembershipBtn.setText( GwtTeaming.getMessages().modifyDynamicMembershipDlgCurrentMembershipLabel( count ) );
		}
		else
		{
			// No, update the label to say "Calculating..."
			m_currentMembershipBtn.setText( GwtTeaming.getMessages().modifyDynamicMembershipDlgCurrentMembershipCalculatingLabel() );
		}
	}
}
