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


import org.kablink.teaming.gwt.client.EditSuccessfulHandler;
import org.kablink.teaming.gwt.client.GwtTeaming;
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
	private CheckBox m_canShareCkbox;
	private EditSuccessfulHandler m_editSuccessfulHandler;
	private GwtShareItem m_shareItem;
	
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
		VibeFlowPanel mainPanel;
		VibeFlowPanel rbPanel;
		VibeFlowPanel tmpPanel;
		Label label;
		
		mainPanel = new VibeFlowPanel();
		mainPanel.setStyleName( "teamingDlgBoxContent" );
		mainPanel.addStyleName( "editShareRightsDlg_MainPanel" );

		label = new Label( GwtTeaming.getMessages().editShareRightsDlg_GrantRightsLabel() );
		mainPanel.add( label );
		
		// Create a panel for the radio buttons to live in.
		rbPanel = new VibeFlowPanel();
		rbPanel.addStyleName( "editShareRightsDlg_RbPanel" );
		
		m_viewerRb = new RadioButton( "shareRights", GwtTeaming.getMessages().editShareRightsDlg_ViewerLabel() );
		tmpPanel = new VibeFlowPanel();
		tmpPanel.add( m_viewerRb );
		rbPanel.add( tmpPanel );

		m_editorRb = new RadioButton( "shareRights", GwtTeaming.getMessages().editShareRightsDlg_EditorLabel() );
		tmpPanel = new VibeFlowPanel();
		tmpPanel.add( m_editorRb );
		rbPanel.add( tmpPanel );
		
		m_contributorRb = new RadioButton( "shareRights", GwtTeaming.getMessages().editShareRightsDlg_ContributorLabel() );
		tmpPanel = new VibeFlowPanel();
		tmpPanel.add( m_contributorRb );
		rbPanel.add( tmpPanel );
		
		m_canShareCkbox = new CheckBox( GwtTeaming.getMessages().editShareRightsDlg_CanShareLabel() );
		m_canShareCkbox.addStyleName( "editShareRightsDlg_CanShareCkbox" );
		tmpPanel = new VibeFlowPanel();
		tmpPanel.addStyleName( "margintop3" );
		tmpPanel.add( m_canShareCkbox );
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
		if ( m_shareItem != null )
		{
			AccessRights accessRights;
			
			// Yes
			accessRights = ShareRights.AccessRights.UNKNOWN;
			
			if ( m_viewerRb.isVisible() && m_viewerRb.getValue() == true )
				accessRights = ShareRights.AccessRights.VIEWER;
			else if ( m_editorRb.isVisible() && m_editorRb.getValue() == true )
				accessRights = ShareRights.AccessRights.EDITOR;
			else if ( m_contributorRb.isVisible() && m_contributorRb.getValue() == true )
				accessRights = ShareRights.AccessRights.CONTRIBUTOR;
			
			m_shareItem.setShareAccessRights( accessRights );

			if ( m_canShareCkbox.isVisible() && m_canShareCkbox.getValue() == true )
				m_shareItem.setShareCanShareWithOthers( true );
			else
				m_shareItem.setShareCanShareWithOthers( false );

			m_shareItem.setIsDirty( true );
			
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
	 * Initialize the controls in the dialog with the values from the properties
	 */
	public void init(
		GwtShareItem shareItem,
		AccessRights highestRightsPossible,
		EditSuccessfulHandler editSuccessfulHandler )
	{
		ShareRights shareRights;

		m_shareItem = shareItem;
		m_editSuccessfulHandler = editSuccessfulHandler;
		
		m_viewerRb.setVisible( false );
		m_editorRb.setVisible( false );
		m_contributorRb.setVisible( false );
		
		m_viewerRb.setValue( false );
		m_editorRb.setValue( false );
		m_contributorRb.setValue( false );
		
		shareRights = shareItem.getShareRights();
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
		
		// Initialize the "can share with others" checkbox"
		m_canShareCkbox.setValue( shareRights.getCanShareWithOthers() );
		
		// Hide/show the controls for the rights the user can/cannot give
		switch ( highestRightsPossible )
		{
		case CONTRIBUTOR:
			m_viewerRb.setVisible( true );
			m_editorRb.setVisible( true );
			
			// Show the "contributor" radio button only if we are dealing with a binder.
			m_contributorRb.setVisible( shareItem.getEntityId().isBinder() );
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
