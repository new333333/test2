/**
 * Copyright (c) 1998-2009 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2009 Novell, Inc. All Rights Reserved.
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

import org.kablink.teaming.gwt.client.EditSuccessfulHandler;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingMessages;
import org.kablink.teaming.gwt.client.util.GwtShareItem;
import org.kablink.teaming.gwt.client.util.ShareRights;
import org.kablink.teaming.gwt.client.util.ShareRights.AccessRights;
import org.kablink.teaming.gwt.client.widgets.DlgBox;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RadioButton;

/**
 * 
 * @author jwootton
 *
 */
public class EditShareRightsDlg extends DlgBox
	implements EditSuccessfulHandler
{
	private RadioButton m_viewerRb;
	private RadioButton m_editorRb;
	private RadioButton m_contributorRb;
	private Label m_canShareLabel;
	private CheckBox m_canShareExternalCkbox;
	private CheckBox m_canShareInternalCkbox;
	private CheckBox m_canSharePublicCkbox;
	private EditSuccessfulHandler m_editSuccessfulHandler;
	private ArrayList<GwtShareItem> m_listOfShareItems;

	/**
	 * Callback interface to interact with the "Edit Share Rights" dialog asynchronously after it loads. 
	 */
	public interface EditShareRightsDlgClient
	{
		void onSuccess( EditShareRightsDlg esrDlg );
		void onUnavailable();
	}


	/*
	 * Class constructor.
	 * 
	 * Note that the class constructor is private to facilitate code
	 * splitting.  All instantiations of this object must be done
	 * through its createAsync().
	 */
	private EditShareRightsDlg(
		boolean autoHide,
		boolean modal )
	{
		super( autoHide, modal );

		// Create the header, content and footer of this dialog box.
		createAllDlgContent(
						GwtTeaming.getMessages().editShareRightsDlg_caption(),
						this,
						null,
						null ); 
	}

	/**
	 * Create all the controls that make up the dialog box.
	 */
	@Override
	public Panel createContent( Object props )
	{
		GwtTeamingMessages messages;
		VibeFlowPanel mainPanel;
		VibeFlowPanel rbPanel;
		VibeFlowPanel tmpPanel;
		
		messages = GwtTeaming.getMessages();
		
		mainPanel = new VibeFlowPanel();
		mainPanel.setStyleName( "teamingDlgBoxContent" );
		mainPanel.addStyleName( "editShareRightsDlg_MainPanel" );
		
		// Create a panel for the radio buttons to live in.
		rbPanel = new VibeFlowPanel();
		rbPanel.addStyleName( "editShareRightsDlg_RbPanel" );
		
		m_viewerRb = new RadioButton( "shareRights", messages.editShareRightsDlg_ViewerLabel() );
		tmpPanel = new VibeFlowPanel();
		tmpPanel.add( m_viewerRb );
		rbPanel.add( tmpPanel );

		m_editorRb = new RadioButton( "shareRights", messages.editShareRightsDlg_EditorLabel() );
		tmpPanel = new VibeFlowPanel();
		tmpPanel.add( m_editorRb );
		rbPanel.add( tmpPanel );
		
		m_contributorRb = new RadioButton( "shareRights", messages.editShareRightsDlg_ContributorLabel() );
		tmpPanel = new VibeFlowPanel();
		tmpPanel.add( m_contributorRb );
		rbPanel.add( tmpPanel );
		
		m_canShareLabel = new Label( messages.editShareRightsDlg_CanShareLabel() );
		m_canShareLabel.addStyleName( "margintop2" );
		rbPanel.add( m_canShareLabel );
		
		// Add the "allow share internal checkbox.
		m_canShareInternalCkbox = new CheckBox( messages.editShareRightsDlg_CanShareInternalLabel() );
		tmpPanel = new VibeFlowPanel();
		tmpPanel.addStyleName( "marginleft1" );
		tmpPanel.add( m_canShareInternalCkbox );
		rbPanel.add( tmpPanel );
		
		// Add the "allow share external" checkbox.
		m_canShareExternalCkbox = new CheckBox( messages.editShareRightsDlg_CanShareExternalLabel() );
		tmpPanel = new VibeFlowPanel();
		tmpPanel.addStyleName( "marginleft1" );
		tmpPanel.add( m_canShareExternalCkbox );
		rbPanel.add( tmpPanel );
		
		// Add the "allow share public" checkbox.
		m_canSharePublicCkbox = new CheckBox( messages.editShareRightsDlg_CanSharePublicLabel() );
		tmpPanel = new VibeFlowPanel();
		tmpPanel.addStyleName( "marginleft1" );
		tmpPanel.add( m_canSharePublicCkbox );
		rbPanel.add( tmpPanel );
		
		mainPanel.add( rbPanel );
		
		return mainPanel;
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
			AccessRights accessRights;
			ShareRights shareRights;
			
			// Yes
			for ( GwtShareItem nextShareItem : m_listOfShareItems )
			{
				boolean canShareForward;
				
				shareRights = nextShareItem.getShareRights();
				accessRights = ShareRights.AccessRights.NONE;
				
				if ( m_viewerRb.isVisible() && m_viewerRb.getValue() == true )
					accessRights = ShareRights.AccessRights.VIEWER;
				else if ( m_editorRb.isVisible() && m_editorRb.getValue() == true )
					accessRights = ShareRights.AccessRights.EDITOR;
				else if ( m_contributorRb.isVisible() && m_contributorRb.getValue() == true )
					accessRights = ShareRights.AccessRights.CONTRIBUTOR;
				
				shareRights.setAccessRights( accessRights );
	
				canShareForward = false;
				
				if ( m_canShareInternalCkbox.isVisible() && m_canShareInternalCkbox.getValue() == true )
				{
					canShareForward = true;
					shareRights.setCanShareWithInternalUsers( true );
				}
				else
					shareRights.setCanShareWithInternalUsers( false );
	
				if ( m_canShareExternalCkbox.isVisible() && m_canShareExternalCkbox.getValue() == true )
				{
					canShareForward = true;
					shareRights.setCanShareWithExternalUsers( true );
				}
				else
					shareRights.setCanShareWithExternalUsers( false );
	
				if ( m_canSharePublicCkbox.isVisible() && m_canSharePublicCkbox.getValue() == true )
				{
					canShareForward = true;
					shareRights.setCanShareWithPublic( true );
				}
				else
					shareRights.setCanShareWithPublic( false );
	
				shareRights.setCanShareForward( canShareForward );
				
				nextShareItem.setIsDirty( true );
			}
			
			// Do we have a handler we should call?
			if ( m_editSuccessfulHandler != null )
				m_editSuccessfulHandler.editSuccessful( Boolean.TRUE );
		}

		return true;
	}

	/**
	 * Get the text entered by the user.
	 */
	@Override
	public Object getDataFromDlg()
	{
		return Boolean.TRUE;
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
	 * Return the list of GwtShareItems we are working with.
	 */
	public ArrayList<GwtShareItem> getListOfShareItems()
	{
		return m_listOfShareItems;
	}
	
	/**
	 * Initialize the controls in the dialog with the values from the properties
	 */
	public void init(
		ArrayList<GwtShareItem> listOfShareItems,
		ShareRights highestRightsPossible,
		EditSuccessfulHandler editSuccessfulHandler )
	{
		ShareRights shareRights;
		boolean entityIsBinder;
		boolean canShareForward;

		m_listOfShareItems = listOfShareItems;
		m_editSuccessfulHandler = editSuccessfulHandler;
		
		if ( highestRightsPossible == null )
			highestRightsPossible = new ShareRights();
		
		m_viewerRb.setVisible( false );
		m_editorRb.setVisible( false );
		m_contributorRb.setVisible( false );
		
		m_viewerRb.setValue( false );
		m_editorRb.setValue( false );
		m_contributorRb.setValue( false );
		
		// Are we only dealing with 1 share item?
		if ( listOfShareItems.size() == 1 )
		{
			GwtShareItem shareItem;
			
			// Get the share rights from the one share item we are working with.
			shareItem = listOfShareItems.get( 0 );
			shareRights = shareItem.getShareRights();
			entityIsBinder = shareItem.getEntityId().isBinder();
		}
		else
		{
			// We are working with multiple share items.  Default to Viewer.
			shareRights = new ShareRights();
			shareRights.setAccessRights( AccessRights.VIEWER );
			
			entityIsBinder = true;
			
			// See if every entity is a binder
			for ( GwtShareItem nextShareItem : listOfShareItems )
			{
				entityIsBinder = nextShareItem.getEntityId().isBinder();
				if ( entityIsBinder == false )
					break;
			}
		}
		
		switch ( shareRights.getAccessRights() )
		{
		case CONTRIBUTOR:
			m_contributorRb.setValue( true );
			break;
		
		case EDITOR:
			m_editorRb.setValue( true );
			break;
			
		case VIEWER:
			m_viewerRb.setValue( true );
			break;
		}
		
		// Hide/show the controls for the rights the user can/cannot give
		switch ( highestRightsPossible.getAccessRights() )
		{
		case CONTRIBUTOR:
			m_viewerRb.setVisible( true );
			m_editorRb.setVisible( true );
			
			// Show the "contributor" radio button only if we are dealing with a binder.
			m_contributorRb.setVisible( entityIsBinder );
			break;
			
		case EDITOR:
			m_viewerRb.setVisible( true );
			m_editorRb.setVisible( true );
			m_contributorRb.setVisible( false );
			break;
			
		case VIEWER:
			m_viewerRb.setVisible( true );
			m_editorRb.setVisible( false );
			m_contributorRb.setVisible( false );
			break;
		}
		
		canShareForward = highestRightsPossible.getCanShareForward();
		
		m_canShareLabel.setVisible( canShareForward );
		
		// Show/hide the "share internal" checkbox depending on whether the user has "share internal" rights.
		m_canShareInternalCkbox.setVisible( canShareForward && highestRightsPossible.getCanShareWithInternalUsers() );
		m_canShareInternalCkbox.setValue( shareRights.getCanShareWithInternalUsers() );
		
		// Show/hide the "share external" checkbox depending on whether the user has "share external" rights.
		m_canShareExternalCkbox.setVisible( canShareForward && highestRightsPossible.getCanShareWithExternalUsers() );
		m_canShareExternalCkbox.setValue( shareRights.getCanShareWithExternalUsers() );
		
		// Show/hide the "share public" checkbox depending on whether the user has "share public" rights.
		m_canSharePublicCkbox.setVisible( canShareForward && highestRightsPossible.getCanShareWithPublic() );
		m_canSharePublicCkbox.setValue( shareRights.getCanShareWithPublic() );
	}
	
	/**
	 * Loads the EditShareRightsDlg split point and returns an instance
	 * of it via the callback.
	 * 
	 */
	public static void createAsync(
							final boolean autoHide,
							final boolean modal,
							final EditShareRightsDlgClient esrDlgClient )
	{
		GWT.runAsync( EditShareRightsDlg.class, new RunAsyncCallback()
		{
			@Override
			public void onFailure(Throwable reason)
			{
				Window.alert( GwtTeaming.getMessages().codeSplitFailure_EditShareRightsDlg() );
				if ( esrDlgClient != null )
				{
					esrDlgClient.onUnavailable();
				}
			}

			@Override
			public void onSuccess()
			{
				EditShareRightsDlg esrDlg;
				
				esrDlg = new EditShareRightsDlg( autoHide, modal );
				esrDlgClient.onSuccess( esrDlg );
			}
		});
	}
}
