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
import java.util.Set;

import org.kablink.teaming.gwt.client.EditSuccessfulHandler;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingMessages;
import org.kablink.teaming.gwt.client.rpc.shared.GetFolderEntryTypeCmd;
import org.kablink.teaming.gwt.client.rpc.shared.GetFolderEntryTypeRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponseData;
import org.kablink.teaming.gwt.client.util.EntityId;
import org.kablink.teaming.gwt.client.util.GwtFolderEntryType;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.util.GwtRecipientType;
import org.kablink.teaming.gwt.client.util.GwtShareItem;
import org.kablink.teaming.gwt.client.util.ShareExpirationValue;
import org.kablink.teaming.gwt.client.util.ShareRights;
import org.kablink.teaming.gwt.client.util.ShareRights.AccessRights;
import org.kablink.teaming.gwt.client.widgets.AccessRightsInfoDlg.AccessRightsInfoDlgClient;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * ?
 *  
 * @author jwootton
 */
public class EditShareWidget extends Composite
	implements EditSuccessfulHandler
{
	private FlowPanel m_rightsPanel;
	private FlowPanel m_rightsPanelForMultiEdit;
	private FlowPanel m_rightsPanelForSingleEdit;
	
	private Label m_caption;
	private FlowPanel m_headerPanel;
	private FlowPanel m_contentPanel;
	private FlowPanel m_footerPanel;
	
	// Data members used with access rights
	private ListBox m_accessRightsListbox;
	private RadioButton m_viewerRb;
	private RadioButton m_editorRb;
	private RadioButton m_contributorRb;

	// Data members used with reshare
	private VerticalPanel m_resharePanelForMultiEdit;
	private ListBox m_canReshareInternalListbox;
	private ListBox m_canReshareExternalListbox;
	private ListBox m_canResharePublicListbox;
	private ListBox m_canResharePublicLinkListbox;
	private Label m_canShareLabel;
	private CheckBox m_canReshareExternalCkbox;
	private CheckBox m_canReshareInternalCkbox;
	private CheckBox m_canResharePublicCkbox;
	private CheckBox m_canResharePublicLinkCkbox;
	private Label m_canReshareInternalLabel;
	private Label m_canReshareExternalLabel;
	private Label m_canResharePublicLabel;
	private Label m_canResharePublicLinkLabel;
	private EditSuccessfulHandler m_editSuccessfulHandler;
	private ArrayList<GwtShareItem> m_listOfShareItems;
	
	// Data members used with share expiration
	private ShareExpirationWidget m_expirationWidget;
	
	// Data members used with share note
	private TextAreaWithMax m_noteTextArea;
	
	private Label m_descLabel;
	private AccessRightsInfoDlg m_accessRightsInfoDlg;
	
	private static String VIEWER = "viewer";
	private static String EDITOR = "editor";
	private static String CONTRIBUTOR = "contributor";
	private static String LEAVE_UNCHANGED = "leave-unchanged";
	private static String RESHARE_YES = "Reshare-Yes";
	private static String RESHARE_NO = "Reshare-No";
	


	/*
	 * Class constructor.
	 * 
	 */
	public EditShareWidget()
	{
		FlowPanel mainPanel;
		
		mainPanel = new FlowPanel();
		mainPanel.addStyleName( "editShareWidget_mainPanel" );
		
		m_headerPanel = createHeader();
		mainPanel.add( m_headerPanel );
		
		m_contentPanel = createContent();
		mainPanel.add( m_contentPanel );
		
		m_footerPanel = createFooter();
		mainPanel.add( m_footerPanel );
		
		initWidget( mainPanel );
	}

	/**
	 * Create all the controls that make up the body of the widget.
	 */
	public FlowPanel createContent()
	{
		FlowPanel mainPanel;
		
		mainPanel = new FlowPanel();
		mainPanel.addStyleName( "editShareWidget_contentPanel" );
		
		createRightsContent( mainPanel );
		createExpirationContent( mainPanel );
		createNoteContent( mainPanel );
		
		// Add a label that will be used to display a description of a Filr Link and a Public Link
		{
			m_descLabel = new Label();
			m_descLabel.addStyleName( "editShareWidget_descLabel" );
			m_descLabel.setVisible( false );
			mainPanel.add( m_descLabel );
		}
		
		return mainPanel;
	}
	
	/**
	 * 
	 */
	private void createExpirationContent( FlowPanel panel )
	{
		FlowPanel mainPanel;

		mainPanel = new FlowPanel();
		mainPanel.addStyleName( "editShareDlg_expirationPanel" );

		m_expirationWidget = new ShareExpirationWidget();
		mainPanel.add( m_expirationWidget );

		panel.add( mainPanel );
	}
	
	/**
	 * 
	 */
	private FlowPanel createFooter()
	{
		FlowPanel panel;
		Button applyBtn;
		
		panel = new FlowPanel();
		panel.addStyleName( "editShareWidget_footerPanel" );
		
		applyBtn = new Button( GwtTeaming.getMessages().apply() );
		applyBtn.addStyleName( "teamingSmallButton" );
		applyBtn.addClickHandler( new ClickHandler()
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
						handleApplyButton();
					}
				};
				Scheduler.get().scheduleDeferred( cmd );
			}
		} );
		
		// We are removing the Apply button for now.  We'll see what the beta customers say.
		// The Share dialog will call our saveSettings() method.
//~JW:	panel.add( applyBtn );

		return panel;
	}
	
	/**
	 * 
	 */
	private FlowPanel createHeader()
	{
		FlowPanel panel;
		
		panel = new FlowPanel();
		panel.addStyleName( "editShareWidget_headerPanel" );

		m_caption = new Label();
		m_caption.setStyleName( "teamingDlgBoxHeader-captionLabel" );
		panel.add( m_caption );

		return panel;
	}
	
	/**
	 * Create the controls needed for the share note.
	 */
	public void createNoteContent( FlowPanel mainPanel )
	{
		FlexTable mainTable;
		int row;
		
		mainTable = new FlexTable();
		mainTable.addStyleName( "editShareNoteDlg_table" );
		mainTable.getRowFormatter().setVerticalAlign( 0, HasVerticalAlignment.ALIGN_TOP );
		mainPanel.add( mainTable );
		
		row = 0;
		
		mainTable.setText( row, 0, GwtTeaming.getMessages().editShareNoteDlg_noteLabel() );
		mainTable.getRowFormatter().addStyleName(row,"gwt-label");
		++row;
		
		m_noteTextArea = new TextAreaWithMax();
		m_noteTextArea.setMaxLength( 255 );
		m_noteTextArea.addStyleName( "editShareNoteDlg_TextArea" );
		m_noteTextArea.addStyleName( "editShareNoteDlg_TextAreaBorder" );
		mainTable.setWidget( row, 0, m_noteTextArea );
	}
	
	/**
	 * Create the ui controls needed to edit rights for multiple shares
	 */
	private FlowPanel createRightsContentForMultiEdit()
	{
		GwtTeamingMessages messages;
		HorizontalPanel hPanel;
		Label label;
		FlowPanel panel;
		
		messages = GwtTeaming.getMessages();
		
		panel = new FlowPanel();
		
		{
			hPanel = new HorizontalPanel();
			hPanel.setVerticalAlignment( HasVerticalAlignment.ALIGN_MIDDLE );
			hPanel.setSpacing( 4 );
			
			// Create a panel for the "Access Rights:" label and the info image to live in.
			{
				FlowPanel tmpPanel;
				InlineLabel inlineLabel;
				
				tmpPanel = new FlowPanel();
				hPanel.add( tmpPanel );

				inlineLabel = new InlineLabel( messages.editShareDlg_accessRightsLabel() );
				inlineLabel.addStyleName( "gwt-label" );
				tmpPanel.add( inlineLabel );

				// Add an info image for the user to click on to get a description of what
				// Viewer, Editor and Contributor means.
				{
					ImageResource imageResource;
					final Image img;
					
					imageResource = GwtTeaming.getImageBundle().info2();
					img = new Image( imageResource );
					img.addStyleName( "editShareRightsDlg_AccessRightsInfoImg" );
					img.getElement().setAttribute( "title", "" );
					
					img.addClickHandler( new ClickHandler()
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
									invokeAccessRightsInfoDlg( img.getAbsoluteLeft(), img.getAbsoluteTop() );
								}
							};
							Scheduler.get().scheduleDeferred( cmd );
						}
					} );
					
					tmpPanel.add( img );
				}
			}

			m_accessRightsListbox = new ListBox();
			m_accessRightsListbox.setMultipleSelect( false );
			m_accessRightsListbox.setVisibleItemCount( 1 );
			hPanel.add( m_accessRightsListbox );
			panel.add( hPanel );
		}
		
		// Add the controls needed to define re-share rights
		{
			m_resharePanelForMultiEdit = new VerticalPanel();
			m_resharePanelForMultiEdit.addStyleName( "margintop2" );
			panel.add( m_resharePanelForMultiEdit );
			
			// Add a "Allow re-share with:" label
			hPanel = new HorizontalPanel();
			hPanel.setVerticalAlignment( HasVerticalAlignment.ALIGN_MIDDLE );
			hPanel.setSpacing( 4 );
			label = new Label( messages.editShareRightsDlg_CanShareLabel() );
			label.addStyleName( "gwt-label" );
			hPanel.add( label );
			m_resharePanelForMultiEdit.add( hPanel );
			
			// Add the "Reshare with internal users" listbox
			hPanel = new HorizontalPanel();
			hPanel.setVerticalAlignment( HasVerticalAlignment.ALIGN_MIDDLE );
			hPanel.setSpacing( 4 );
			hPanel.addStyleName( "marginleft1" );
			m_canReshareInternalLabel = new Label( messages.editShareDlg_canReshareInternalLabel() );
			hPanel.add( m_canReshareInternalLabel );
			m_canReshareInternalListbox = new ListBox();
			m_canReshareInternalListbox.setMultipleSelect( false );
			m_canReshareInternalListbox.setVisibleItemCount( 1 );
			m_canReshareInternalListbox.addItem( messages.editShareDlg_yes(), RESHARE_YES );
			m_canReshareInternalListbox.addItem( messages.editShareDlg_no(), RESHARE_NO );
			m_canReshareInternalListbox.addItem( messages.editShareDlg_leaveUnchanged(), LEAVE_UNCHANGED );
			hPanel.add( m_canReshareInternalListbox );
			m_resharePanelForMultiEdit.add( hPanel );
			
			// Add the "Reshare with external users" listbox
			hPanel = new HorizontalPanel();
			hPanel.setVerticalAlignment( HasVerticalAlignment.ALIGN_MIDDLE );
			hPanel.setSpacing( 4 );
			hPanel.addStyleName( "marginleft1" );
			m_canReshareExternalLabel = new Label( messages.editShareDlg_canReshareExternalLabel() );
			hPanel.add( m_canReshareExternalLabel );
			m_canReshareExternalListbox = new ListBox();
			m_canReshareExternalListbox.setMultipleSelect( false );
			m_canReshareExternalListbox.setVisibleItemCount( 1 );
			m_canReshareExternalListbox.addItem( messages.editShareDlg_yes(), RESHARE_YES );
			m_canReshareExternalListbox.addItem( messages.editShareDlg_no(), RESHARE_NO );
			m_canReshareExternalListbox.addItem( messages.editShareDlg_leaveUnchanged(), LEAVE_UNCHANGED );
			hPanel.add( m_canReshareExternalListbox );
			m_resharePanelForMultiEdit.add( hPanel );
			
			// Add the "Reshare with public" listbox
			hPanel = new HorizontalPanel();
			hPanel.setVerticalAlignment( HasVerticalAlignment.ALIGN_MIDDLE );
			hPanel.setSpacing( 4 );
			hPanel.addStyleName( "marginleft1" );
			m_canResharePublicLabel = new Label( messages.editShareDlg_canResharePublicLabel() );
			hPanel.add( m_canResharePublicLabel );
			m_canResharePublicListbox = new ListBox();
			m_canResharePublicListbox.setMultipleSelect( false );
			m_canResharePublicListbox.setVisibleItemCount( 1 );
			m_canResharePublicListbox.addItem( messages.editShareDlg_yes(), RESHARE_YES );
			m_canResharePublicListbox.addItem( messages.editShareDlg_no(), RESHARE_NO );
			m_canResharePublicListbox.addItem( messages.editShareDlg_leaveUnchanged(), LEAVE_UNCHANGED );
			m_canResharePublicListbox.addChangeHandler( new ChangeHandler()
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
							int selectedIndex;
							
							selectedIndex = m_canResharePublicListbox.getSelectedIndex();
							if ( selectedIndex >= 0 )
							{
								String value;
								
								// Did the user select Yes in the public listbox?
								value = m_canResharePublicListbox.getValue( selectedIndex );
								if ( value != null && value.equalsIgnoreCase( RESHARE_YES ) == true )
								{
									// Yes
									// Select Yes in the "Internal users" and "External users" listboxes
									// because if you are sharing with the public you also need to
									// give rights to share with internal and external users.
									GwtClientHelper.selectListboxItemByValue( m_canReshareInternalListbox, RESHARE_YES );
									GwtClientHelper.selectListboxItemByValue( m_canReshareExternalListbox, RESHARE_YES );
								}
							}
							
							danceDlg();
						}
					};
					
					Scheduler.get().scheduleDeferred( cmd );
				}
			} );
			hPanel.add( m_canResharePublicListbox );
			m_resharePanelForMultiEdit.add( hPanel );

			// Add the "Filr Link" listbox
			hPanel = new HorizontalPanel();
			hPanel.setVerticalAlignment( HasVerticalAlignment.ALIGN_MIDDLE );
			hPanel.setSpacing( 4 );
			hPanel.addStyleName( "marginleft1" );
			m_canResharePublicLinkLabel = new Label( messages.editShareDlg_canResharePublicLinkLabel() );
			hPanel.add( m_canResharePublicLinkLabel );
			m_canResharePublicLinkListbox = new ListBox();
			m_canResharePublicLinkListbox.setMultipleSelect( false );
			m_canResharePublicLinkListbox.setVisibleItemCount( 1 );
			m_canResharePublicLinkListbox.addItem( messages.editShareDlg_yes(), RESHARE_YES );
			m_canResharePublicLinkListbox.addItem( messages.editShareDlg_no(), RESHARE_NO );
			m_canResharePublicLinkListbox.addItem( messages.editShareDlg_leaveUnchanged(), LEAVE_UNCHANGED );
			hPanel.add( m_canResharePublicLinkListbox );
			m_resharePanelForMultiEdit.add( hPanel );
		}
		
		return panel;
	}
	
	/**
	 * Create the ui controls needed to edit rights for a single share
	 */
	private FlowPanel createRightsContentForSingleEdit()
	{
		GwtTeamingMessages messages;
		FlowPanel mainPanel;
		FlowPanel rbPanel;
		FlowPanel tmpPanel;
		InlineLabel inlineLabel;
		
		messages = GwtTeaming.getMessages();
		
		mainPanel = new FlowPanel();
		
		// Add an "Access Rights" heading
		inlineLabel = new InlineLabel( messages.editShareDlg_accessRightsLabel() );
		inlineLabel.addStyleName("editShareRightsDlg_accessLabel");
		mainPanel.add( inlineLabel );
		
		// Add an info image for the user to click on to get a description of what
		// Viewer, Editor and Contributor means.
		{
			ImageResource imageResource;
			final Image img;
			
			imageResource = GwtTeaming.getImageBundle().info2();
			img = new Image( imageResource );
			img.addStyleName( "editShareRightsDlg_AccessRightsInfoImg" );
			img.addClickHandler( new ClickHandler()
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
							invokeAccessRightsInfoDlg( img.getAbsoluteLeft(), img.getAbsoluteTop() );
						}
					};
					Scheduler.get().scheduleDeferred( cmd );
				}
			} );
			
			img.getElement().setAttribute( "title", "" );
			
			mainPanel.add( img );
		}
		
		// Create a panel for the radio buttons to live in.
		rbPanel = new FlowPanel();
		rbPanel.addStyleName( "editShareRightsDlg_RbPanel" );
		
		m_viewerRb = new RadioButton( "shareRights", messages.editShareRightsDlg_ViewerLabel() );
		tmpPanel = new FlowPanel();
		tmpPanel.add( m_viewerRb );
		rbPanel.add( tmpPanel );

		m_editorRb = new RadioButton( "shareRights", messages.editShareRightsDlg_EditorLabel() );
		tmpPanel = new FlowPanel();
		tmpPanel.add( m_editorRb );
		rbPanel.add( tmpPanel );
		
		m_contributorRb = new RadioButton( "shareRights", messages.editShareRightsDlg_ContributorLabel() );
		tmpPanel = new FlowPanel();
		tmpPanel.add( m_contributorRb );
		rbPanel.add( tmpPanel );
		
		mainPanel.add( rbPanel );
		
		rbPanel = new FlowPanel();
		rbPanel.addStyleName( "editShareRightsDlg_RbPanel" );
		
		// Add the "Allow the recipient to re-share this item with:"
		m_canShareLabel = new Label( messages.editShareRightsDlg_CanShareLabel() );
		m_canShareLabel.addStyleName( "editShareRightsDlg_reshareLabel" );
		mainPanel.add( m_canShareLabel );
		
		// Add the "allow share internal checkbox.
		m_canReshareInternalCkbox = new CheckBox( messages.editShareRightsDlg_CanShareInternalLabel() );
		tmpPanel = new FlowPanel();
		tmpPanel.add( m_canReshareInternalCkbox );
		rbPanel.add( tmpPanel );
		
		// Add the "allow share external" checkbox.
		m_canReshareExternalCkbox = new CheckBox( messages.editShareRightsDlg_CanShareExternalLabel() );
		tmpPanel = new FlowPanel();
		tmpPanel.add( m_canReshareExternalCkbox );
		rbPanel.add( tmpPanel );
		
		// Add the "allow share public" checkbox.
		m_canResharePublicCkbox = new CheckBox( messages.editShareRightsDlg_CanSharePublicLabel() );
		m_canResharePublicCkbox.addClickHandler( new ClickHandler()
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
						if ( m_canResharePublicCkbox.getValue() == true )
						{
							m_canReshareInternalCkbox.setValue( Boolean.TRUE );
							m_canReshareExternalCkbox.setValue( Boolean.TRUE );
						}
						
						danceDlg();
					}
				};
				Scheduler.get().scheduleDeferred( cmd );
			}
		} );
		tmpPanel = new FlowPanel();
		tmpPanel.add( m_canResharePublicCkbox );
		rbPanel.add( tmpPanel );
		
		// Add the "allow share public link checkbox.
		m_canResharePublicLinkCkbox = new CheckBox( messages.editShareRightsDlg_CanSharePublicLinkLabel() );
		tmpPanel = new FlowPanel();
		tmpPanel.add( m_canResharePublicLinkCkbox );
		rbPanel.add( tmpPanel );
		
		mainPanel.add( rbPanel );
		
		return mainPanel;
	}
	
	/**
	 * 
	 */
	public void createRightsContent( FlowPanel panel )
	{
		m_rightsPanel = new FlowPanel();
		
		// We will add the appropriate panel to the widget in the init() method.
		m_rightsPanelForMultiEdit = createRightsContentForMultiEdit();
		m_rightsPanelForSingleEdit = createRightsContentForSingleEdit();

		panel.add( m_rightsPanel );
	}
	
	/**
	 * 
	 */
	private void danceDlg()
	{
		if ( m_listOfShareItems != null )
		{
			if ( m_listOfShareItems.size() == 1 )
			{
				// If the public checkbox is checked then the internal and external checkboxes
				// must be checked and disabled.
				if ( m_canResharePublicCkbox.getValue() == true )
				{
					m_canReshareInternalCkbox.setEnabled( false );
					m_canReshareExternalCkbox.setEnabled( false );
				}
				else
				{
					m_canReshareInternalCkbox.setEnabled( true );
					m_canReshareExternalCkbox.setEnabled( true );
				}
			}
			else
			{
				int selectedIndex;
				
				if ( m_canResharePublicListbox.isVisible() == true )
				{
					selectedIndex = m_canResharePublicListbox.getSelectedIndex();
					if ( selectedIndex >= 0 )
					{
						String value;
						
						// Did the user select Yes in the public listbox?
						value = m_canResharePublicListbox.getValue( selectedIndex );
						if ( value != null && value.equalsIgnoreCase( RESHARE_YES ) == true )
						{
							// Yes
							// Disable the "Internal users" and "External Users" listboxes.
							m_canReshareInternalListbox.setEnabled( false );
							m_canReshareExternalListbox.setEnabled( false );
						}
						else
						{
							m_canReshareInternalListbox.setEnabled( true );
							m_canReshareExternalListbox.setEnabled( true );
						}
					}
					else
					{
						m_canReshareInternalListbox.setEnabled( true );
						m_canReshareExternalListbox.setEnabled( true );
					}
				}
				else
				{
					m_canReshareInternalListbox.setEnabled( true );
					m_canReshareExternalListbox.setEnabled( true );
				}
			}
		}
	}
	
	/**
	 * 
	 */
	@Override
	public boolean editSuccessful( Object obj )
	{
		// Do we have a share item we are working with?
		if ( m_listOfShareItems != null )
		{
			// Yes
			boolean anyDirty = false;
			for ( GwtShareItem nextShareItem : m_listOfShareItems )
			{
				saveShareRights( nextShareItem );
				saveExpirationValue( nextShareItem );
				saveNote( nextShareItem );
				anyDirty |= nextShareItem.isDirty();
			}
			
			boolean validateExpiration = (anyDirty && ((null == obj) ? true : ((Boolean) obj)));
			if (validateExpiration && ( ! ( m_expirationWidget.validateExpirationValue() ) ) )
				return false;
			
			// Do we have a handler we should call?
			if ( m_editSuccessfulHandler != null )
				m_editSuccessfulHandler.editSuccessful( Boolean.TRUE );
		}

		return true;
	}

	/**
	 * Return the list of GwtShareItems we are working with.
	 */
	public ArrayList<GwtShareItem> getListOfShareItems()
	{
		return m_listOfShareItems;
	}

	/**
	 * 
	 */
	private void handleApplyButton()
	{
		editSuccessful( Boolean.TRUE );
	}

	/**
	 * Go through the list of GwtShareItems and if there are any that are folder entries, issue an rpc
	 * request to get the folder entry type. 
	 */
	public void initCheckForRequiredData(
		final ArrayList<GwtShareItem> listOfShareItems,
		final ShareRights highestRightsPossible,
		final EditSuccessfulHandler editSuccessfulHandler )
	{
		ArrayList<Long> listOfIds;
		
		listOfIds = new ArrayList<Long>();
		
		if ( listOfShareItems != null && listOfShareItems.size() > 0 )
		{
			for ( GwtShareItem nextShareItem : listOfShareItems )
			{
				EntityId entityId;
				
				entityId = nextShareItem.getEntityId();
				if ( entityId != null && entityId.isEntry() && nextShareItem.getFolderEntryType() == null )
				{
					listOfIds.add( entityId.getEntityId() );
				}
			}
		}
		
		// Do we have any items being shared that are folder entries?
		if ( listOfIds.size() > 0 )
		{
			AsyncCallback<VibeRpcResponse> getEntryTypeCallback;

			// Yes
			getEntryTypeCallback = new AsyncCallback<VibeRpcResponse>()
			{
				@Override
				public void onFailure( Throwable caught )
				{
					GwtClientHelper.handleGwtRPCFailure(
													caught,
													GwtTeaming.getMessages().rpcFailure_GetFolderEntryTypes() );
				}

				@Override
				public void onSuccess( final VibeRpcResponse vibeResult )
				{
					Scheduler.ScheduledCommand cmd;
					
					cmd = new Scheduler.ScheduledCommand()
					{
						@Override
						public void execute()
						{
							VibeRpcResponseData responseData;
							
							responseData = vibeResult.getResponseData();
							if ( responseData != null && responseData instanceof GetFolderEntryTypeRpcResponseData )
							{
								GetFolderEntryTypeRpcResponseData entryTypeResponseData;
								HashMap<Long,GwtFolderEntryType> listOfEntryTypes;
								
								entryTypeResponseData = (GetFolderEntryTypeRpcResponseData) responseData;

								listOfEntryTypes = entryTypeResponseData.getListOfTypes();
								
								if ( listOfEntryTypes != null && listOfEntryTypes.size() > 0 )
								{
									Set<Long> keySet;
									
									keySet = listOfEntryTypes.keySet();
									
									for ( Long nextEntryId : keySet )
									{
										// Find this entry id list our list of GwtShareItems
										for ( GwtShareItem nextShareItem : listOfShareItems )
										{
											if ( nextEntryId.equals( nextShareItem.getEntityId().getEntityId() ) )
											{
												GwtFolderEntryType entryType;
												
												entryType = listOfEntryTypes.get( nextEntryId );
												nextShareItem.setFolderEntryType( entryType );
											}
										}
									}
								}

								initFinish( listOfShareItems, highestRightsPossible, editSuccessfulHandler );
							}
						}
					};
					Scheduler.get().scheduleDeferred( cmd );
				}				
			};

			// Issue an ajax request to get the folder entry types
			GetFolderEntryTypeCmd cmd = new GetFolderEntryTypeCmd( listOfIds );
			GwtClientHelper.executeCommand( cmd, getEntryTypeCallback );
		}
		else
			initFinish( listOfShareItems, highestRightsPossible, editSuccessfulHandler );
	}
	
	/**
	 * Initialize the controls in the dialog with the values from the properties
	 */
	public void init(
		ArrayList<GwtShareItem> listOfShareItems,
		ShareRights highestRightsPossible,
		EditSuccessfulHandler editSuccessfulHandler )
	{
		// Go through the list of share items and if there are any that are folder entries
		// get the folder entry type.  When initCheckForRequired() data is finished it will call
		// initFinish().
		initCheckForRequiredData( listOfShareItems, highestRightsPossible, editSuccessfulHandler );
	}
	
	/**
	 * Initialize the controls in the dialog with the values from the properties
	 */
	private void initFinish(
		ArrayList<GwtShareItem> listOfShareItems,
		ShareRights highestRightsPossible,
		EditSuccessfulHandler editSuccessfulHandler )
	{
		// Update the caption
		{
			// Are we editing more than 1 share?
			if ( listOfShareItems.size() == 1 )
			{
				GwtShareItem shareItem;
				
				// No
				shareItem = listOfShareItems.get( 0 );
				setCaption( GwtTeaming.getMessages().editShareDlg_captionEdit1( shareItem.getRecipientName() ) );
			}
			else
			{
				setCaption( GwtTeaming.getMessages().editShareDlg_captionEditMultiple( listOfShareItems.size() ) );
			}
		}
		
		m_listOfShareItems = listOfShareItems;
		m_editSuccessfulHandler = editSuccessfulHandler;

		initRightsControls( listOfShareItems, highestRightsPossible );
		initExpirationControls( listOfShareItems );
		initNoteControls( listOfShareItems );
		
		// Initialize the description
		{
			m_descLabel.setVisible( false );
			if ( m_listOfShareItems != null && m_listOfShareItems.size() == 1 )
			{
				GwtShareItem shareItem;
				GwtRecipientType recipientType;
				String desc = null;
				
				shareItem = m_listOfShareItems.get( 0 );
				recipientType = shareItem.getRecipientType();
				if ( recipientType == GwtRecipientType.PUBLIC_LINK )
				{
					desc = GwtTeaming.getMessages().editShareDlg_filrLinkDesc();
				}
				else if ( recipientType == GwtRecipientType.PUBLIC_TYPE )
				{
					desc = GwtTeaming.getMessages().editShareDlg_publicLinkDesc();
				}
				
				if ( desc != null )
				{
					m_descLabel.setText( desc );
					m_descLabel.setVisible( true );
				}
			}
		}
		
		danceDlg();
	}
	
	/**
	 * Initialize the controls dealing with expiration
	 */
	private void initExpirationControls( ArrayList<GwtShareItem> listOfShares )
	{
		if ( listOfShares == null || listOfShares.size() == 0 )
			return;
		
		m_expirationWidget.addStandardExpirationTypes();

		if ( listOfShares.size() > 1 )
		{
			m_expirationWidget.addDoNotModify();
			m_expirationWidget.init( null );
		}
		else
		{
			ShareExpirationValue expirationValue;

			expirationValue = listOfShares.get( 0 ).getShareExpirationValue();
			m_expirationWidget.init( expirationValue );
		}
	}
	
	/**
	 * Initialize the controls dealing with rights
	 */
	public void initRightsControls(
		ArrayList<GwtShareItem> listOfShareItems,
		ShareRights highestRightsPossible )
	{
		if ( highestRightsPossible == null )
			highestRightsPossible = new ShareRights();
		
		m_rightsPanel.clear();
		
		// Are we only dealing with 1 share item?
		if ( listOfShareItems.size() == 1 )
		{
			m_rightsPanel.add( m_rightsPanelForSingleEdit );
			initRightsControlsForSingleEdit( listOfShareItems.get( 0 ), highestRightsPossible );
		}
		else
		{
			m_rightsPanel.add( m_rightsPanelForMultiEdit );
			initRightsControlsForMultiEdit( listOfShareItems, highestRightsPossible );
		}
	}
	
	/**
	 * Initialize the controls dealing with rights for multiple shares
	 */
	private void initRightsControlsForMultiEdit(
		ArrayList<GwtShareItem> listOfShareItems,
		ShareRights highestRightsPossible )
	{
		GwtTeamingMessages messages;
		boolean entityIsBinder;
		boolean entityIsFileEntry;

		messages = GwtTeaming.getMessages();
		
		if ( highestRightsPossible == null )
			highestRightsPossible = new ShareRights();
		
		entityIsBinder = true;
		entityIsFileEntry = true;
		
		// See if every entity is a binder
		for ( GwtShareItem nextShareItem : listOfShareItems )
		{
			EntityId entityId;
			
			entityId = nextShareItem.getEntityId();
			
			if ( entityId.isBinder() == false )
			{
				if ( nextShareItem.getFolderEntryType() != GwtFolderEntryType.FILE )
					entityIsFileEntry = false;
				
				entityIsBinder = false;
			}
		}

		// Add the appropriate options to the "access rights" listbox.
		{
			
			m_accessRightsListbox.clear();
			
			switch ( highestRightsPossible.getAccessRights() )
			{
			case CONTRIBUTOR:
				m_accessRightsListbox.addItem( messages.editShareRightsDlg_ViewerLabel(), VIEWER );
				m_accessRightsListbox.addItem( messages.editShareRightsDlg_EditorLabel(), EDITOR );
				
				// Add "contributor" only if we are dealing with a binder.
				if ( entityIsBinder )
					m_accessRightsListbox.addItem( messages.editShareRightsDlg_ContributorLabel(), CONTRIBUTOR );
	
				break;
				
			case EDITOR:
				m_accessRightsListbox.addItem( messages.editShareRightsDlg_ViewerLabel(), VIEWER );
				m_accessRightsListbox.addItem( messages.editShareRightsDlg_EditorLabel(), EDITOR );
				break;
				
			case VIEWER:
				m_accessRightsListbox.addItem( messages.editShareRightsDlg_ViewerLabel(), VIEWER );
				break;
				
			default:
				break;
			}
	
			// Add an "Leave unchanged" option.
			m_accessRightsListbox.addItem( messages.editShareDlg_leaveUnchanged(), LEAVE_UNCHANGED );
			GwtClientHelper.selectListboxItemByValue( m_accessRightsListbox, LEAVE_UNCHANGED );
		}
		
		// Update the controls dealing with re-share
		{
			boolean canShareForward;

			canShareForward = highestRightsPossible.getCanShareForward();
			
			m_resharePanelForMultiEdit.setVisible( canShareForward );
			
			if ( canShareForward )
			{
				boolean canShare;
				
				// Show/hide the "share internal" listbox depending on whether the user has "share internal" rights.
				canShare = highestRightsPossible.getCanShareWithInternalUsers();
				m_canReshareInternalLabel.setVisible( canShare );
				m_canReshareInternalListbox.setVisible( canShare );
				if ( canShare )
					GwtClientHelper.selectListboxItemByValue( m_canReshareInternalListbox, LEAVE_UNCHANGED );

				// Show/hide the "share external" listbox depending on whether the user has "share external" rights.
				canShare = highestRightsPossible.getCanShareWithExternalUsers();
				m_canReshareExternalLabel.setVisible( canShare );
				m_canReshareExternalListbox.setVisible( canShare );
				if ( canShare )
					GwtClientHelper.selectListboxItemByValue( m_canReshareExternalListbox, LEAVE_UNCHANGED );

				// Show/hide the "share public" listbox depending on whether the user has "share public" rights.
				canShare = highestRightsPossible.getCanShareWithPublic();
				m_canResharePublicLabel.setVisible( canShare );
				m_canResharePublicListbox.setVisible( canShare );
				if ( canShare )
					GwtClientHelper.selectListboxItemByValue( m_canResharePublicListbox, LEAVE_UNCHANGED );

				// Show/hide the "share public link" listbox depending on whether the user has "share public link" rights.
				{
					if ( entityIsBinder == true || entityIsFileEntry == false )
						canShare = false;
					else
						canShare = highestRightsPossible.getCanSharePublicLink();
					
					m_canResharePublicLinkLabel.setVisible( canShare );
					m_canResharePublicLinkListbox.setVisible( canShare );
					if ( canShare )
						GwtClientHelper.selectListboxItemByValue( m_canResharePublicLinkListbox, LEAVE_UNCHANGED );
				}
			}
		}
	}
	
	/**
	 * Initialize the controls dealing with rights for a single share
	 */
	private void initRightsControlsForSingleEdit(
		GwtShareItem shareItem,
		ShareRights highestRightsPossible )
	{
		ShareRights shareRights;
		boolean entityIsBinder;
		boolean entityIsFileEntry = false;
		boolean canShareForward;

		if ( highestRightsPossible == null )
			highestRightsPossible = new ShareRights();
		
		// Get the share rights from the one share item we are working with.
		shareRights = shareItem.getShareRights();

		entityIsBinder = shareItem.getEntityId().isBinder();

		if ( shareItem.getEntityId().isEntry() && shareItem.getFolderEntryType() == GwtFolderEntryType.FILE )
			entityIsFileEntry = true;

		m_viewerRb.setEnabled( false );
		m_editorRb.setEnabled( false );
		m_contributorRb.setEnabled( false );
		
		m_viewerRb.setValue( false );
		m_editorRb.setValue( false );
		m_contributorRb.setValue( false );
		
		switch ( shareRights.getAccessRights() )
		{
		case CONTRIBUTOR:
			if(highestRightsPossible.getUnAlteredAccessRights() == AccessRights.CONTRIBUTOR)
				m_contributorRb.setValue( true );
			break;
		
		case EDITOR:
			if(highestRightsPossible.getUnAlteredAccessRights() == AccessRights.CONTRIBUTOR || 
					highestRightsPossible.getUnAlteredAccessRights() == AccessRights.EDITOR)
				m_editorRb.setValue( true );
			break;
			
		case VIEWER:
			if(highestRightsPossible.getUnAlteredAccessRights() == AccessRights.CONTRIBUTOR || 
					highestRightsPossible.getUnAlteredAccessRights() == AccessRights.EDITOR ||
					highestRightsPossible.getUnAlteredAccessRights() == AccessRights.VIEWER)
				m_viewerRb.setValue( true );
			break;
			
		default:
			break;
		}
		
		m_contributorRb.getElement().getStyle().clearColor();
		m_editorRb.getElement().getStyle().clearColor();
		
		// Hide/show the controls for the rights the user can/cannot give
		switch ( highestRightsPossible.getAccessRights() )
		{
		case CONTRIBUTOR:
			m_viewerRb.setEnabled( true );
			m_editorRb.setEnabled( true );
			
			// Show the "contributor" radio button only if we are dealing with a binder.
			m_contributorRb.setEnabled( entityIsBinder );
			break;
			
		case EDITOR:
			if(highestRightsPossible.getUnAlteredAccessRights() == AccessRights.CONTRIBUTOR)
				m_contributorRb.getElement().getStyle().setColor("red");			
			m_viewerRb.setEnabled( true );
			m_editorRb.setEnabled( true );
			m_contributorRb.setEnabled( false );
			break;
			
		case VIEWER:
			if(highestRightsPossible.getUnAlteredAccessRights() == AccessRights.EDITOR || highestRightsPossible.getUnAlteredAccessRights() == AccessRights.CONTRIBUTOR)
				m_editorRb.getElement().getStyle().setColor("red");
			if(highestRightsPossible.getUnAlteredAccessRights() == AccessRights.CONTRIBUTOR)
				m_contributorRb.getElement().getStyle().setColor("red");		
			if(highestRightsPossible.getUnAlteredAccessRights() == AccessRights.NONE){
				m_viewerRb.getElement().getStyle().setColor("red");
				m_viewerRb.setEnabled( false );
			}
			else
				m_viewerRb.setEnabled( true );
			m_editorRb.setEnabled( false );			
			m_contributorRb.setEnabled( false );
			break;
			
		default:
			break;
		}
		
		canShareForward = highestRightsPossible.getCanShareForward();
		
		m_canShareLabel.setVisible( canShareForward );
		
		// Show/hide the "share internal" checkbox depending on whether the user has "share internal" rights.
		m_canReshareInternalCkbox.setVisible( canShareForward && highestRightsPossible.getCanShareWithInternalUsers() );
		m_canReshareInternalCkbox.setValue( shareRights.getCanShareWithInternalUsers() );
		
		// Show/hide the "share external" checkbox depending on whether the user has "share external" rights.
		m_canReshareExternalCkbox.setVisible( canShareForward && highestRightsPossible.getCanShareWithExternalUsers() );
		m_canReshareExternalCkbox.setValue( shareRights.getCanShareWithExternalUsers() );
		
		// Show/hide the "share public" checkbox depending on whether the user has "share public" rights.
		m_canResharePublicCkbox.setVisible( canShareForward && highestRightsPossible.getCanShareWithPublic() );
		m_canResharePublicCkbox.setValue( shareRights.getCanShareWithPublic() );
		
		// Show/hide the "share public link" checkbox depending on whether the user has "share public link" rights.
		if ( canShareForward && highestRightsPossible.getCanSharePublicLink() && (entityIsBinder == true || entityIsFileEntry == true) )
			m_canResharePublicLinkCkbox.setVisible( true );
		else
			m_canResharePublicLinkCkbox.setVisible( false );
			
		m_canResharePublicLinkCkbox.setValue( shareRights.getCanSharePublicLink() );
	}
	
	/**
	 * Initialize the controls that deal with the share note.
	 */
	private void initNoteControls( ArrayList<GwtShareItem> listOfShareItems )
	{
		String note;
		
		// Are we only dealing with 1 share item?
		if ( listOfShareItems.size() == 1 )
		{
			GwtShareItem shareItem;
			
			// Get the share rights from the one share item we are working with.
			shareItem = listOfShareItems.get( 0 );
			note = shareItem.getComments();
		}
		else
		{
			note = GwtTeaming.getMessages().editShareDlg_undefinedNote();
		}

		m_noteTextArea.setValue( note );
	}
	
	/**
	 * 
	 */
	private void invokeAccessRightsInfoDlg( final int x, final int y )
	{
		if ( m_accessRightsInfoDlg == null )
		{
			// Run an async cmd to create the dialog.
			AccessRightsInfoDlg.createDlg(
										false,
										true,
										new Integer( x ),
										new Integer( y ),
										null,
										new AccessRightsInfoDlgClient()
			{			
				@Override
				public void onUnavailable()
				{
					// Nothing to do.  Error handled in asynchronous provider.
				}
				
				@Override
				public void onSuccess( final AccessRightsInfoDlg dlg )
				{
					m_accessRightsInfoDlg = dlg;
					
					GwtClientHelper.deferCommand( new ScheduledCommand()
					{
						@Override
						public void execute() 
						{
							invokeAccessRightsInfoDlg( x, y );
						}
					} );
				}
			} );
		}
		else
		{
			AccessRightsInfoDlgClient client = null;
			
			// Run an async cmd to show the dialog.
			AccessRightsInfoDlg.initAndShow(
											m_accessRightsInfoDlg,
											new Integer( x ),
											new Integer( y ),
											client );
		}
		
	}

	/**
	 * Update the GwtShareItem with the expiration value from the dialog
	 */
	private void saveExpirationValue( GwtShareItem shareItem )
	{
		ShareExpirationValue origValue;
		ShareExpirationValue expirationValue;
		boolean usedNewValue = false;
		boolean dirty = false;
		
		origValue = shareItem.getShareExpirationValue();
		
		expirationValue = m_expirationWidget.getExpirationValue();
		if ( expirationValue != null )
		{
			shareItem.setShareExpirationValue( expirationValue );
			usedNewValue = true;
		}
		
		if ( usedNewValue )
		{
			if ( origValue == null && expirationValue != null )
				dirty = true;
			else if ( origValue != null && expirationValue == null )
				dirty = true;
			else if ( expirationValue != null && expirationValue.equalsValue( origValue ) == false )
				dirty = true;
		}
		
		if ( dirty )
			shareItem.setIsDirty( true );
	}
		
	/**
	 * 
	 */
	public boolean saveSettings( boolean validateExpiration )
	{
		return editSuccessful( new Boolean( validateExpiration ) );
	}
	
	/**
	 * Update the GwtShareItem with the share rights from the dialog
	 */
	private void saveShareRights( GwtShareItem shareItem )
	{
		// Are the multi-edit controls visible?
		if ( m_rightsPanelForMultiEdit.isAttached() )
		{
			// Yes
			saveShareRightsForMultiEdit( shareItem );
		}
		else
		{
			// No
			saveShareRightsForSingleEdit( shareItem );
		}
	}
	
	/**
	 * Update the GwtShareItem with the share rights from multi-edit controls
	 */
	private void saveShareRightsForMultiEdit( GwtShareItem shareItem )
	{
		ShareRights shareRights;
		ShareRights origShareRights;
		
		shareRights = shareItem.getShareRights();
		
		origShareRights = new ShareRights();
		origShareRights.copy( shareRights );
		
		// Save the access rights.
		{
			int selectedIndex;

			selectedIndex = m_accessRightsListbox.getSelectedIndex();
			if ( selectedIndex >= 0 )
			{
				String value;
				
				value = m_accessRightsListbox.getValue( selectedIndex );
				if ( value != null && value.equalsIgnoreCase( LEAVE_UNCHANGED ) == false )
				{
					AccessRights accessRights;

					accessRights = ShareRights.AccessRights.NONE;

					if ( value.equalsIgnoreCase( VIEWER ) )
						accessRights = ShareRights.AccessRights.VIEWER;
					else if ( value.equalsIgnoreCase( EDITOR ) )
						accessRights = ShareRights.AccessRights.EDITOR;
					else if ( value.equalsIgnoreCase( CONTRIBUTOR ) )
						accessRights = ShareRights.AccessRights.CONTRIBUTOR;

					shareRights.setAccessRights( accessRights );
				}
			}
		}

		// Save the re-share rights
		if ( m_resharePanelForMultiEdit.isVisible() )
		{
			int selectedIndex;
			boolean canShareForward;
			boolean setCanShareForward;
			
			setCanShareForward = false;
			canShareForward = false;
			
			if ( m_canReshareInternalListbox.isVisible() )
			{
				selectedIndex = m_canReshareInternalListbox.getSelectedIndex();
				if ( selectedIndex >= 0 )
				{
					String value;
					
					value = m_canReshareInternalListbox.getValue( selectedIndex );
					if ( value != null && value.equalsIgnoreCase( LEAVE_UNCHANGED ) == false )
					{
						setCanShareForward = true;

						if ( value.equalsIgnoreCase( RESHARE_YES ) )
						{
							canShareForward = true;
							shareRights.setCanShareWithInternalUsers( true );
						}
						else
							shareRights.setCanShareWithInternalUsers( false );
					}
				}
			}
	
			if ( m_canReshareExternalListbox.isVisible() )
			{
				selectedIndex = m_canReshareExternalListbox.getSelectedIndex();
				if ( selectedIndex >= 0 )
				{
					String value;
					
					value = m_canReshareExternalListbox.getValue( selectedIndex );
					if ( value != null && value.equalsIgnoreCase( LEAVE_UNCHANGED ) == false )
					{
						setCanShareForward = true;

						if ( value.equalsIgnoreCase( RESHARE_YES ) )
						{
							canShareForward = true;
							shareRights.setCanShareWithExternalUsers( true );
						}
						else
							shareRights.setCanShareWithExternalUsers( false );
					}
				}
			}
	
			if ( m_canResharePublicListbox.isVisible() )
			{
				selectedIndex = m_canResharePublicListbox.getSelectedIndex();
				if ( selectedIndex >= 0 )
				{
					String value;
					
					value = m_canResharePublicListbox.getValue( selectedIndex );
					if ( value != null && value.equalsIgnoreCase( LEAVE_UNCHANGED ) == false )
					{
						setCanShareForward = true;

						if ( value.equalsIgnoreCase( RESHARE_YES ) )
						{
							canShareForward = true;
							shareRights.setCanShareWithPublic( true );
						}
						else
							shareRights.setCanShareWithPublic( false );
					}
				}
			}
	
			
			if ( m_canResharePublicLinkListbox.isVisible() )
			{
				selectedIndex = m_canResharePublicLinkListbox.getSelectedIndex();
				if ( selectedIndex >= 0 )
				{
					String value;
					
					value = m_canResharePublicLinkListbox.getValue( selectedIndex );
					if ( value != null && value.equalsIgnoreCase( LEAVE_UNCHANGED ) == false )
					{
						setCanShareForward = true;

						if ( value.equalsIgnoreCase( RESHARE_YES ) )
						{
							canShareForward = true;
							shareRights.setCanSharePublicLink( true );
						}
						else
							shareRights.setCanSharePublicLink( false );
					}
				}
			}
	
			if ( setCanShareForward )
				shareRights.setCanShareForward( canShareForward );
		}
		
		if ( origShareRights.equalsRights( shareRights ) == false )
			shareItem.setIsDirty( true );
	}
	
	/**
	 * Update the GwtShareItem with the share rights from single-edit controls
	 */
	private void saveShareRightsForSingleEdit( GwtShareItem shareItem )
	{
		AccessRights accessRights;
		ShareRights origShareRights;
		ShareRights shareRights;
		boolean canShareForward;
		
		shareRights = shareItem.getShareRights();
		
		origShareRights = new ShareRights();
		origShareRights.copy( shareRights );
		
		accessRights = ShareRights.AccessRights.NONE;
		
		if ( m_viewerRb.isVisible() && m_viewerRb.getValue() == true )
			accessRights = ShareRights.AccessRights.VIEWER;
		else if ( m_editorRb.isVisible() && m_editorRb.getValue() == true )
			accessRights = ShareRights.AccessRights.EDITOR;
		else if ( m_contributorRb.isVisible() && m_contributorRb.getValue() == true )
			accessRights = ShareRights.AccessRights.CONTRIBUTOR;
		
		shareRights.setAccessRights( accessRights );
		
		canShareForward = false;
		
		if ( m_canReshareInternalCkbox.isVisible() && m_canReshareInternalCkbox.getValue() == true )
		{
			canShareForward = true;
			shareRights.setCanShareWithInternalUsers( true );
		}
		else
			shareRights.setCanShareWithInternalUsers( false );

		if ( m_canReshareExternalCkbox.isVisible() && m_canReshareExternalCkbox.getValue() == true )
		{
			canShareForward = true;
			shareRights.setCanShareWithExternalUsers( true );
		}
		else
			shareRights.setCanShareWithExternalUsers( false );

		if ( m_canResharePublicCkbox.isVisible() && m_canResharePublicCkbox.getValue() == true )
		{
			canShareForward = true;
			shareRights.setCanShareWithPublic( true );
		}
		else
			shareRights.setCanShareWithPublic( false );

		if ( m_canResharePublicLinkCkbox.isVisible() && m_canResharePublicLinkCkbox.getValue() == true )
		{
			canShareForward = true;
			shareRights.setCanSharePublicLink( true );
		}
		else
			shareRights.setCanSharePublicLink( false );

		shareRights.setCanShareForward( canShareForward );

		if ( origShareRights.equalsRights( shareRights ) == false )
			shareItem.setIsDirty( true );
	}
	
	/**
	 * Update the GwtShareItem with the note from the dialog
	 */
	private void saveNote( GwtShareItem shareItem )
	{
		String origNote;
		String newNote;
		boolean usedNewNote = false;
		boolean dirty = false;
		
		origNote = shareItem.getComments();
		newNote = m_noteTextArea.getValue();
		
		// Are we dealing with more than 1 share item?
		if ( m_listOfShareItems != null && m_listOfShareItems.size() > 1 )
		{
			// Yes
			// Is the note equal to "Do not modify"?
			if ( newNote != null && newNote.equalsIgnoreCase( GwtTeaming.getMessages().editShareDlg_undefinedNote() ) == false )
			{
				// No, save the note
				shareItem.setComments( newNote );
				usedNewNote = true;
			}
		}
		else
		{
			// No
			if ( newNote != null )
			{
				shareItem.setComments( newNote );
				usedNewNote = true;
			}
		}
		
		if ( usedNewNote )
		{
			if ( origNote == null && newNote != null )
				dirty = true;
			else if ( origNote != null && newNote == null )
				dirty = true;
			else if ( newNote != null && newNote.equals( origNote ) == false )
				dirty = true;
		}
		
		if ( dirty )
			shareItem.setIsDirty( true );
	}
	
	/**
	 * 
	 */
	private void setCaption( String caption )
	{
		m_caption.setText( caption );
	}
	
	/**
	 * 
	 */
	public void setWidgetHeight( final int height )
	{
		Scheduler.ScheduledCommand cmd;
		
		cmd = new Scheduler.ScheduledCommand()
		{
			@Override
			public void execute()
			{
				int contentHeight;
				
				contentHeight = height -m_headerPanel.getOffsetHeight() - m_footerPanel.getOffsetHeight();  
				m_contentPanel.getElement().getStyle().setHeight( contentHeight, Unit.PX );
			}
		};
		Scheduler.get().scheduleDeferred( cmd );
	}
}
