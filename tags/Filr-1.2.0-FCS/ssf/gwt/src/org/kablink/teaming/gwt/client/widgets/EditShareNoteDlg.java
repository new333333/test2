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
import org.kablink.teaming.gwt.client.widgets.DlgBox;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TextArea;

/**
 * 
 * @author jwootton
 *
 */
public class EditShareNoteDlg extends DlgBox
	implements KeyPressHandler
{
	private TextArea m_noteTextArea;
	
	/**
	 * Callback interface to interact with the "Edit Share Note" dialog asynchronously after it loads. 
	 */
	public interface EditShareNoteDlgClient
	{
		void onSuccess( EditShareNoteDlg esnDlg );
		void onUnavailable();
	}


	/*
	 * Class constructor.
	 * 
	 * Note that the class constructor is private to facilitate code
	 * splitting.  All instantiations of this object must be done
	 * through its createAsync().
	 */
	private EditShareNoteDlg(
		boolean autoHide,
		boolean modal )
	{
		super( autoHide, modal );

		// Create the header, content and footer of this dialog box.
		createAllDlgContent(
						GwtTeaming.getMessages().editShareNoteDlg_caption(),
						null,
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
		FlexTable mainTable;
		int col;
		
		mainPanel = new VibeFlowPanel();
		mainPanel.setStyleName( "teamingDlgBoxContent" );
		mainPanel.addStyleName( "editShareNoteDlg_MainPanel" );

		mainTable = new FlexTable();
		mainTable.addStyleName( "editShareNoteDlg_table" );
		mainTable.addStyleName( "gwt-label" );
		mainTable.getRowFormatter().setVerticalAlign( 0, HasVerticalAlignment.ALIGN_TOP );
		mainPanel.add( mainTable );
		
		col = 0;
		
		mainTable.setText( 0, col, GwtTeaming.getMessages().editShareNoteDlg_noteLabel() );
		++col;
		
		m_noteTextArea = new TextArea();
		m_noteTextArea.addStyleName( "editShareNoteDlg_TextArea" );
		m_noteTextArea.addStyleName( "editShareNoteDlg_TextAreaBorder" );
		mainTable.setWidget( 0, col, m_noteTextArea );
		
		return mainPanel;
	}
	
	
	/**
	 * Get the text entered by the user.
	 */
	@Override
	public Object getDataFromDlg()
	{
		return m_noteTextArea.getValue();
	}
	
	/**
	 * Return the widget that should get the focus when the dialog is shown. 
	 */
	@Override
	public FocusWidget getFocusWidget()
	{
		return m_noteTextArea;
	}

	/**
	 * Initialize the controls in the dialog with the values from the properties
	 */
	public void init( String note, EditSuccessfulHandler editSuccessfulHandler )
	{
		m_noteTextArea.setValue( note );
		
		// Set the "edit successful handler" we should use.
		initHandlers( editSuccessfulHandler, null );
	}
	
	/**
	 * Limit the size of the note
	 */
	@Override
	public void onKeyPress( KeyPressEvent event )
	{
        int keyCode;
    	Object source;
    	
    	// Make sure we are dealing with a TextArea
    	source = event.getSource();
    	if ( source instanceof TextArea )
    	{
	        keyCode = event.getNativeEvent().getKeyCode();
	        if ( (keyCode != KeyCodes.KEY_TAB) && (keyCode != KeyCodes.KEY_BACKSPACE)
	             && (keyCode != KeyCodes.KEY_DELETE) && (keyCode != KeyCodes.KEY_ENTER) && (keyCode != KeyCodes.KEY_HOME)
	             && (keyCode != KeyCodes.KEY_END) && (keyCode != KeyCodes.KEY_LEFT) && (keyCode != KeyCodes.KEY_UP)
	             && (keyCode != KeyCodes.KEY_RIGHT) && (keyCode != KeyCodes.KEY_DOWN) )
	        {
				String text;
				
				text = m_noteTextArea.getText(); 
				if ( text != null && text.length() > 253 )
				{
	        		// Suppress the current keyboard event.
	        		m_noteTextArea.cancelKey();
				}
	        }
    	}
	}

	/**
	 * Loads the EditShareNoteDlg split point and returns an instance
	 * of it via the callback.
	 * 
	 */
	public static void createAsync(
							final boolean autoHide,
							final boolean modal,
							final EditShareNoteDlgClient esnDlgClient )
	{
		GWT.runAsync( EditShareNoteDlg.class, new RunAsyncCallback()
		{
			@Override
			public void onFailure(Throwable reason)
			{
				Window.alert( GwtTeaming.getMessages().codeSplitFailure_EditShareNoteDlg() );
				if ( esnDlgClient != null )
				{
					esnDlgClient.onUnavailable();
				}
			}

			@Override
			public void onSuccess()
			{
				EditShareNoteDlg esnDlg;
				
				esnDlg = new EditShareNoteDlg( autoHide, modal );
				esnDlgClient.onSuccess( esnDlg );
			}
		});
	}
}
