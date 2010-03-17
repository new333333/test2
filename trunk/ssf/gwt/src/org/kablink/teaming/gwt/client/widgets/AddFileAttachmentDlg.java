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

import org.kablink.teaming.gwt.client.EditCanceledHandler;
import org.kablink.teaming.gwt.client.EditSuccessfulHandler;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.widgets.DlgBox;

import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.Panel;


/**
 * 
 * @author jwootton
 *
 */
public class AddFileAttachmentDlg extends DlgBox
{
	FlexTable m_table = null;
	ArrayList<FileUpload> m_fileUploadControls = null;
	
	/**
	 * 
	 */
	public AddFileAttachmentDlg(
		EditSuccessfulHandler editSuccessfulHandler,	// We will call this handler when the user presses the ok button
		EditCanceledHandler editCanceledHandler, 		// This gets called when the user presses the Cancel button
		boolean autoHide,
		boolean modal,
		int xPos,
		int yPos,
		Object properties )
	{
		super( autoHide, modal, xPos, yPos );
	
		// Create the header, content and footer of this dialog box.
		createAllDlgContent( GwtTeaming.getMessages().addFileAttachmentDlgHeader(), editSuccessfulHandler, editCanceledHandler, properties ); 
	}// end AddFileAttachmentDlg()
	

	/**
	 * Add a FileUpload control to the dialog.
	 */
	private FileUpload addFileUploadControl()
	{
		FileUpload fileUpload;
		int count;
		
		// If we haven't already created one, create an ArrayList that will hold all
		// the FileUpload controls we have in the dialog.
		if ( m_fileUploadControls == null )
			m_fileUploadControls = new ArrayList<FileUpload>();
		
		// Add the FileUpload control after any existing FileUpload controls.
		fileUpload = new FileUpload();
		count = m_table.getRowCount();
		m_table.setWidget( count, 0, fileUpload );

		// Keep a list of all the FileUpload controls we have created.
		m_fileUploadControls.add( fileUpload );
		
		return fileUpload;
	}// end addFileUploadControl()
	

	/**
	 * Clear all the content from the dialog and start fresh.
	 */
	public void clearContent()
	{
		// Remove all the rows that contain FileUpload controls.
		m_table.removeAllRows();
		m_fileUploadControls.clear();
		
		// Add one FileUpload control to the dialog.
		addFileUploadControl();
	}// end clearContent()
	
	
	/**
	 * Create all the controls that make up the dialog box.
	 */
	public Panel createContent( Object props )
	{
		FlowPanel mainPanel = null;
		
		mainPanel = new FlowPanel();
		mainPanel.setStyleName( "teamingDlgBoxContent" );

		m_table = new FlexTable();
		m_table.setCellSpacing( 4 );
		m_table.addStyleName( "dlgContent" );
		
		// Add a FileUpload control to the dialog.
		addFileUploadControl();
		
		mainPanel.add( m_table );
		
		init( props );

		return mainPanel;
	}// end createContent()
	
	
	/**
	 * Return a list of the file names that were selected by the user.
	 */
	public Object getDataFromDlg()
	{
		ArrayList<String> listOfFileNames;
		int i;
		
		listOfFileNames = new ArrayList<String>();
		for (i = 0; i < m_fileUploadControls.size(); ++i)
		{
			FileUpload fileUpload;
			
			// Get the next FileUpload control in the list.
			fileUpload = m_fileUploadControls.get( i );
			listOfFileNames.add( fileUpload.getFilename() );
		}
		
		return listOfFileNames;
	}// end getDataFromDlg()
	
	
	/**
	 * We don't have a widget to give the focus to. 
	 */
	public FocusWidget getFocusWidget()
	{
		return null;
	}// end getFocusWidget()
	
	
	/**
	 * Initialize the controls in the dialog with the values from the given object.
	 * Currently there is nothing to initialize.
	 */
	public void init( Object props )
	{
	}// end init()
	
}// end AddFileAttachmentDlg
