/**
 * Copyright (c) 1998-2011 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2011 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2011 Novell, Inc. All Rights Reserved.
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

import org.kablink.teaming.gwt.client.EditCanceledHandler;
import org.kablink.teaming.gwt.client.EditSuccessfulHandler;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.RequestInfo;
import org.kablink.teaming.gwt.client.lpe.LPETinyMCEConfiguration;
import org.kablink.teaming.gwt.client.lpe.LandingPageEditor;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.widgets.DlgBox;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TextArea;


/**
 * 
 * @author jwootton
 *
 */
public class TinyMCEDlg extends DlgBox
{
	TinyMCE m_tinyMCE = null;
	TextArea m_textArea = null;	// This is used if we are running on a device that doesn't support the tinyMCE editor.
	AbstractTinyMCEConfiguration m_tinyMCEConfig = null;
	
	
	/*
	 * Note that the class constructor is private to facilitate code
	 * splitting.  All instantiations of this object must be done
	 * through its createAsync().
	 */
	private TinyMCEDlg(
		String dlgTitle,
		AbstractTinyMCEConfiguration tinyMCEConfig,
		EditSuccessfulHandler editSuccessfulHandler,	// We will call this handler when the user presses the ok button
		EditCanceledHandler editCanceledHandler, 		// This gets called when the user presses the Cancel button
		boolean autoHide,
		boolean modal,
		int xPos,
		int yPos,
		Object properties )
	{
		super( autoHide, modal, xPos, yPos );
	
		m_tinyMCEConfig = tinyMCEConfig;
		
		// Create the header, content and footer of this dialog box.
		createAllDlgContent( dlgTitle, editSuccessfulHandler, editCanceledHandler, properties ); 
	}// end TinyMCEDlg()
	

	/**
	 * Clear all the content from the dialog and start fresh.
	 */
	public void clearContent()
	{
	}// end clearContent()
	
	
	/**
	 * Create all the controls that make up the dialog box.
	 */
	public Panel createContent( Object props )
	{
		FlowPanel mainPanel = null;
		RequestInfo reqInfo;
		
		mainPanel = new FlowPanel();
		mainPanel.setStyleName( "teamingDlgBoxContent" );
	
		// Can the tinyMCE editor run on the device we are running on?
		reqInfo = GwtClientHelper.getRequestInfo();
		if ( reqInfo.isTinyMCECapable() )
		{
			// Yes
			m_tinyMCE = new TinyMCE( m_tinyMCEConfig, 300, 15 );
			mainPanel.add( m_tinyMCE );
		}
		else
		{
			// No, create a textarea instead of a tinyMCE editor
			m_textArea = new TextArea();
			m_textArea.setCharacterWidth( 300 );
			m_textArea.setVisibleLines( 15 );
	        DOM.setStyleAttribute( m_textArea.getElement(), "width", "100%" );
			
			mainPanel.add( m_textArea );
		}
		
		init( props );

		return mainPanel;
	}// end createContent()
	
	
	/**
	 * 
	 */
	public Object getDataFromDlg()
	{
		// Do we have a tinyMCE editor?
		if ( m_tinyMCE != null )
		{
			// Yes
			return m_tinyMCE.getText();
		}
		
		// If we get here we don't have a tinyMCE editor
		if ( m_textArea != null )
		{
			return m_textArea.getText();
		}
		
		// We should never get here.
		return "";
	}// end getDataFromDlg()
	
	
	/**
	 * We don't have a widget to give the focus to. 
	 */
	public FocusWidget getFocusWidget()
	{
		return null;
	}// end getFocusWidget()
	
	
	/**
	 * Unload the tinyMCE control.
	 */
	public void hide()
	{
		super.hide();
		
		if ( m_tinyMCE != null )
			m_tinyMCE.unload();
	}// end hide()
	
	
	/**
	 * Initialize the controls in the dialog with the values from the given object.
	 */
	public void init( Object props )
	{
		if ( props instanceof String )
			setText( (String) props );
		else
			setText( null );
	}// end init()
	
	
	/**
	 * Set the text in the tinyMCE editor.
	 */
	public void setText( String text )
	{
		// Do we have a tinyMCE editor?
		if ( m_tinyMCE != null )
		{
			// Yes
			if ( text != null )
				m_tinyMCE.setText( text );
			else
				m_tinyMCE.setText( "" );
		}
		else if ( m_textArea != null )
		{
			if ( text != null )
				m_textArea.setText( text );
			else
				m_textArea.setText( "" );
		}
	}// end setText()
	

	/**
	 * Show this dialog.
	 */
	public void show()
	{
		Scheduler.ScheduledCommand cmd;

        // Show this dialog.
		super.show();
		
		// Give the focus to the tinyMCE editor.
		// Use a deferred command so the dialog is completely visible before giving
		// the focus to the tinyMCE editor.
		cmd = new Scheduler.ScheduledCommand()
		{
			public void execute()
			{
				if ( m_tinyMCE != null )
					m_tinyMCE.setFocus();
				else if ( m_textArea != null )
					m_textArea.setFocus( true );
			}
		};
		Scheduler.get().scheduleDeferred( cmd );
	}
	
	/**
	 * Callback interface to interact with the dialog asynchronously
	 * after it loads. 
	 */
	public interface TinyMCEDlgClient
	{
		void onSuccess( TinyMCEDlg dlg );
		void onSuccess( AbstractTinyMCEConfiguration config );
		void onUnavailable();
	}

	/*
	 * Asynchronously loads the TagThisDialog and performs some
	 * operation against the code.
	 */
	private static void doAsyncOperation(
		// Prefetch parameters.  true -> Prefetch only.  false -> Something else.
		final TinyMCEDlgClient dlgClient,
		final boolean prefetch,
		
		// Creation parameters.
		final String dlgTitle,
		final AbstractTinyMCEConfiguration tinyMCEConfig,
		final EditSuccessfulHandler editSuccessfulHandler,	// We will call this handler when the user presses the ok button
		final EditCanceledHandler editCanceledHandler, 		// This gets called when the user presses the Cancel button
		final boolean autoHide,
		final boolean modal,
		final int xPos,
		final int yPos,
		final Object properties,

		// Branding tinyMCE configuration parameters.
		final String brandingBinderId,
		
		// Landing page tinyMCE configuration parameters.
		final LandingPageEditor lpe,
		final String lpeBinderId )
	{
		loadControl1(
			// Prefetch parameters.
			dlgClient,
			prefetch,
				
			// Creation parameters.
			dlgTitle,
			tinyMCEConfig,
			editSuccessfulHandler,	// We will call this handler when the user presses the ok button
			editCanceledHandler, 	// This gets called when the user presses the Cancel button
			autoHide,
			modal,
			xPos,
			yPos,
			properties,
				
			// Branding tinyMCE configuration parameters.
			brandingBinderId,
				
			// Landing page tinyMCE configuration parameters.
			lpe,
			lpeBinderId );
	}// end doAsyncOperation()
	
	/*
	 * Various control loaders used to load the split points containing
	 * the code for the controls by the TinyMCEDlg object.
	 * 
	 * Loads the split point for the TinyMCEDlg.
	 */
	private static void loadControl1(
		// Prefetch parameters.  true -> Prefetch only.  false -> Something else.
		final TinyMCEDlgClient dlgClient,
		final boolean prefetch,
		
		// Creation parameters.
		final String dlgTitle,
		final AbstractTinyMCEConfiguration tinyMCEConfig,
		final EditSuccessfulHandler editSuccessfulHandler,	// We will call this handler when the user presses the ok button
		final EditCanceledHandler editCanceledHandler, 		// This gets called when the user presses the Cancel button
		final boolean autoHide,
		final boolean modal,
		final int xPos,
		final int yPos,
		final Object properties,

		// Branding tinyMCE configuration parameters.
		final String brandingBinderId,
		
		// Landing page tinyMCE configuration parameters.
		final LandingPageEditor lpe,
		final String lpeBinderId )
	{
		GWT.runAsync( TinyMCEDlg.class, new RunAsyncCallback()
		{			
			@Override
			public void onSuccess()
			{
				initTinyMCE_Finish(
					// Prefetch parameters.
					dlgClient,
					prefetch,
						
					// Creation parameters.
					dlgTitle,
					tinyMCEConfig,
					editSuccessfulHandler,	// We will call this handler when the user presses the ok button
					editCanceledHandler, 	// This gets called when the user presses the Cancel button
					autoHide,
					modal,
					xPos,
					yPos,
					properties,
						
					// Branding tinyMCE configuration parameters.
					brandingBinderId,
						
					// Landing page tinyMCE configuration parameters.
					lpe,
					lpeBinderId );
			}// onSuccess()
			
			@Override
			public void onFailure( Throwable reason )
			{
				Window.alert( GwtTeaming.getMessages().codeSplitFailure_TinyMCEDlg() );
				dlgClient.onUnavailable();
			}// end onFailure()
		} );
	}// end loadControl1()
	
	/*
	 * Finishes the initialization of the TinyMCE object.
	 */
	private static void initTinyMCE_Finish(
		// Prefetch parameters.  true -> Prefetch only.  false -> Something else.
		final TinyMCEDlgClient dlgClient,
		final boolean prefetch,
		
		// Creation parameters.
		final String dlgTitle,
		final AbstractTinyMCEConfiguration tinyMCEConfig,
		final EditSuccessfulHandler editSuccessfulHandler,	// We will call this handler when the user presses the ok button
		final EditCanceledHandler editCanceledHandler, 		// This gets called when the user presses the Cancel button
		final boolean autoHide,
		final boolean modal,
		final int xPos,
		final int yPos,
		final Object properties,
		
		// Branding tinyMCE configuration parameters.
		final String brandingBinderId,
		
		// Landing page tinyMCE configuration parameters.
		final LandingPageEditor lpe,
		final String lpeBinderId )
	{
		if ( prefetch ) {
			dlgClient.onSuccess( (TinyMCEDlg)                   null );
			dlgClient.onSuccess( (AbstractTinyMCEConfiguration) null );
		}
		
		else if ( ( null == brandingBinderId ) && ( null == lpe ) ) {
			TinyMCEDlg dlg = new TinyMCEDlg(
				dlgTitle,
				tinyMCEConfig,
				editSuccessfulHandler,
				editCanceledHandler,
				autoHide,
				modal,
				xPos,
				yPos,
				properties );
			
			dlgClient.onSuccess( dlg );
		}
		
		else if ( null != brandingBinderId ) {
			BrandingTinyMCEConfiguration config = new BrandingTinyMCEConfiguration( brandingBinderId );
			dlgClient.onSuccess( config );
		}
		
		else if ( null != lpe ) {
			LPETinyMCEConfiguration config = new LPETinyMCEConfiguration( lpe, lpeBinderId );
			dlgClient.onSuccess( config );
		}
	}// end initTinyMCE_Finish()
		
	/**
	 * Loads the TinyMCEDlg split point and returns an instance of it
	 * via the callback.
	 * 
	 * @param dlgTitle
	 * @param tinyMCEConfig
	 * @param editSuccessfulHandler
	 * @param editCanceledHandler
	 * @param autoHide
	 * @param modal
	 * @param xPos
	 * @param yPos
	 * @param properties
	 * @param dlgClient
	 */
	public static void createAsync(
		// Creation parameters.
		final String dlgTitle,
		final AbstractTinyMCEConfiguration tinyMCEConfig,
		final EditSuccessfulHandler editSuccessfulHandler,	// We will call this handler when the user presses the ok button
		final EditCanceledHandler editCanceledHandler, 		// This gets called when the user presses the Cancel button
		final boolean autoHide,
		final boolean modal,
		final int xPos,
		final int yPos,
		final Object properties,
		final TinyMCEDlgClient dlgClient )
	{
		doAsyncOperation(
			// Prefetch parameters.  false -> Not a prefetch.
			dlgClient,
			false,
			
			// Required creation parameters.
			dlgTitle,
			tinyMCEConfig,
			editSuccessfulHandler,
			editCanceledHandler,
			autoHide,
			modal,
			xPos,
			yPos,
			properties,
			
			// Branding tinyMCE configuration parameters ignored.
			null,
			
			// Landing page tinyMCE configuration parameters ignored.
			null,
			null );
	}// end createAsync()
	
	public static void createBrandingTinyMCEConfiguration(
		final String brandingBinderId,
		final TinyMCEDlgClient dlgClient )
	{
		doAsyncOperation(
			// Prefetch parameters.  false -> Not a prefetch.
			dlgClient,
			false,
			
			// Creation parameters ignored.
			null,
			null,
			null,
			null,
			false,
			false,
			-1,
			-1,
			null,
			
			// Required branding tinyMCE configuration parameters.
			brandingBinderId,
			
			// Landing page tinyMCE configuration parameters ignored.
			null,
			null );
	}// end createBrandingTinyMCEConfiguration()
		
	public static void createLPETinyMCEConfiguration(
		final LandingPageEditor lpe,
		final String lpeBinderId,
		final TinyMCEDlgClient dlgClient )
	{		
		doAsyncOperation(
			// Prefetch parameters.  false -> Not a prefetch.
			dlgClient,
			false,
			
			// Creation parameters ignored.
			null,
			null,
			null,
			null,
			false,
			false,
			-1,
			-1,
			null,
			
			// Branding tinyMCE configuration parameters ignored.
			null,
			
			// Required landing page tinyMCE configuration parameters.
			lpe,
			lpeBinderId );
	}// end createLPETinyMCEConfiguration()
	
	/**
	 * Causes the split point for the TinyMCEDlg to be fetched.
	 * 
	 * @param dlgClient
	 */
	public static void prefetch( TinyMCEDlgClient dlgClient )
	{
		// If we weren't given a TinyMCEDlgClient...
		if (null == dlgClient) {
			// ...create one we can use.
			dlgClient = new TinyMCEDlgClient() {			
				@Override
				public void onUnavailable()
				{
					// Unused.
				}// end onUnavailable()
				
				@Override
				public void onSuccess(TinyMCEDlg findCtrl)
				{
					// Unused.
				}// end onSuccess()
				
				@Override
				public void onSuccess(AbstractTinyMCEConfiguration config)
				{
					// Unused.
				}// end onSuccess()
			};
		}
		
		doAsyncOperation(
			// Prefetch parameters.  true -> Prefetch only.
			dlgClient,
			true,

			// Creation parameters are ignored.
			null,
			null,
			null,
			null,
			false,
			false,
			-1,
			-1,
			null,
			
			// Branding tinyMCE configuration parameters ignored.
			null,
			
			// Landing page tinyMCE configuration parameters ignored.
			null,
			null );
	}// end prefetch()
	
	public static void prefetch()
	{
		prefetch( null );
	}// end prefetch()
}
