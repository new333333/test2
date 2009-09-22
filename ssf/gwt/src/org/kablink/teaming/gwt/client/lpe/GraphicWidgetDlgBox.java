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
package org.kablink.teaming.gwt.client.lpe;

import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.widgets.DlgBox;
import org.kablink.teaming.gwt.client.widgets.EditCanceledHandler;
import org.kablink.teaming.gwt.client.widgets.EditSuccessfulHandler;
import org.kablink.teaming.gwt.client.widgets.PropertiesObj;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * 
 * @author jwootton
 *
 */
public class GraphicWidgetDlgBox extends DlgBox
{
	/**
	 * This class wraps a JavaScript object that holds the list of file attachments for this landing page.
	 * @author jwootton
	 *
	 */
	public static class FileAttachments extends JavaScriptObject
	{
		/**
		 * Overlay types always have a protected, zero-arg constructors.
		 */
		protected FileAttachments()
		{
		}// end FileAttachments()

		
		/**
		 * Return the file id for the given file attachment
		 */
		public final native String getFileId( int index )/*-{ return this[index].fileId; }-*/;
		
		
		/**
		 * Return the file name for the given file attachment
		 */
		public final native String getFileName( int index )/*-{ return this[index].fileName; }-*/;
		
		/**
		 * Return the number of file attachments.
		 */
		public final native int getNumAttachments()/*-{ return this.length; }-*/;
	}// end FileAttachments

	private CheckBox m_showBorderCkBox = null;
	private ListBox m_graphicListBox = null;
	
	/**
	 * 
	 */
	public GraphicWidgetDlgBox(
		EditSuccessfulHandler editSuccessfulHandler,	// We will call this handler when the user presses the ok button
		EditCanceledHandler editCanceledHandler, 		// This gets called when the user presses the Cancel button
		boolean autoHide,
		boolean modal,
		int xPos,
		int yPos,
		GraphicProperties properties ) // Where properties used in the dialog are read from and saved to.
	{
		super( autoHide, modal, xPos, yPos );
		
		// Create the header, content and footer of this dialog box.
		createAllDlgContent( GwtTeaming.getMessages().graphicProperties(), editSuccessfulHandler, editCanceledHandler, properties ); 
	}// end GraphicWidgetDlgBox()
	

	/**
	 * Create all the controls that make up the dialog box.
	 */
	public Panel createContent( PropertiesObj props )
	{
		GraphicProperties properties;
		Label			label;
		VerticalPanel	mainPanel;
		FlexTable		table;
		FileAttachments fileAttachments;
		int numAttachments;
		int i;
		
		properties = (GraphicProperties) props;

		mainPanel = new VerticalPanel();
		mainPanel.setStyleName( "teamingDlgBoxContent" );

		table = new FlexTable();
		table.setCellSpacing( 2 );

		// Get the number of file attachments.
		fileAttachments = getFileAttachments();
		numAttachments = fileAttachments.getNumAttachments();
		
		// Do we have any file attachments?
		if ( numAttachments > 0 )
		{
			// Yes
			// Add the "Graphic" label.
			label = new Label( GwtTeaming.getMessages().selectGraphicLabel() );
			table.setWidget( 0, 0, label );

			// Create a list box to hold the list of graphics.
			m_graphicListBox = new ListBox( false );
			m_graphicListBox.setVisibleItemCount( 1 );
			table.setWidget( 1, 0, m_graphicListBox );
			
			// Fill the list box with the names of the file attachments.
			for (i = 0; i < numAttachments; ++i)
			{
				String fileName;
				String fileId;
				
				fileName = fileAttachments.getFileName( i );
				fileId = fileAttachments.getFileId( i );
				m_graphicListBox.addItem( fileName, fileId );
			}
		}
		else
		{
			Label noAttachmentsLabel;
			
			// Add some text that informs the user there are no attachments.
			noAttachmentsLabel = new Label( GwtTeaming.getMessages().noFileAttachmentsHint() );
			table.setWidget( 1, 0, noAttachmentsLabel );
		}
		
		mainPanel.add( table );
		
		// Add a checkbox for "Show border"
		table = new FlexTable();
		table.setCellSpacing( 4 );
		m_showBorderCkBox = new CheckBox( GwtTeaming.getMessages().showBorder() );
		table.setWidget( 0, 0, m_showBorderCkBox );
		mainPanel.add( table );

		// Initialize the controls in the dialog with the values from the properties.
		init( properties );
		
		return mainPanel;
	}// end createContent()
	
	
	/**
	 * Get the data from the controls in the dialog box and store the data in the properties obj.
	 */
	public PropertiesObj getDataFromDlg()
	{
		GraphicProperties	properties;
		
		properties = new GraphicProperties();
		
		// Save away the "show border" value.
		properties.setShowBorder( getShowBorderValue() );
		
		// Save away the information about the selected graphic.
		properties.setGraphicId( getGraphicId() );
		properties.setGraphicName( getGraphicName() );
		
		return properties;
	}// end getDataFromDlg()

	
	/**
	 * Use JSNI to grab the JavaScript object that holds the list of file attachments.
	 */
	private native FileAttachments getFileAttachments() /*-{
		// Return a reference to the JavaScript variable called, m_fileAttachments.
		return $wnd.m_fileAttachments;
	}-*/;
	
	
	/**
	 * Return the id of the selected graphic.
	 */
	private String getGraphicId()
	{
		int index;
		
		// Do we have any file attachments?
		if ( m_graphicListBox == null )
		{
			// No
			return null;
		}
		
		// Get the index of the selected graphic.
		index = m_graphicListBox.getSelectedIndex();
		if ( index >= 0 )
			return m_graphicListBox.getValue( index );

		// If we get here, a graphic has not been selected.
		return null;
	}// end getGraphicId()
	
	
	/**
	 * Return the name of the selected graphic.
	 */
	private String getGraphicName()
	{
		int index;
		
		// Do we have any file attachments?
		if ( m_graphicListBox == null )
		{
			// No
			return null;
		}
		
		// Get the index of the selected graphic.
		index = m_graphicListBox.getSelectedIndex();
		if ( index >= 0 )
			return m_graphicListBox.getItemText( index );

		// If we get here, a graphic has not been selected.
		return null;
	}
	
	/**
	 * Return the widget that should get the focus when the dialog is shown. 
	 */
	public FocusWidget getFocusWidget()
	{
		return m_graphicListBox;
	}// end getFocusWidget()
	
	
	/**
	 * Return true if the "show border" checkbox is checked.
	 */
	public boolean getShowBorderValue()
	{
		return m_showBorderCkBox.getValue().booleanValue();
	}// end getShowBorderValue()
	
	
	/**
	 * Initialize the controls on the page with the values from the properties.
	 */
	public void init(
		PropertiesObj props )
	{
		GraphicProperties properties;
		String fileId;
		int selectedIndex = 0;
		int i;
		
		properties = (GraphicProperties) props;

		// Select the graphic in the ListBox.
		for (i = 0; i < m_graphicListBox.getItemCount(); ++i)
		{
			fileId = m_graphicListBox.getValue( i );
			
			// Is this graphic the currently selected graphic?
			if ( fileId.equalsIgnoreCase( properties.getGraphicId() ) )
			{
				selectedIndex = i;
				break;
			}
		}
		
		// Select the appropriate graphic.
		m_graphicListBox.setSelectedIndex( selectedIndex );
		
		m_showBorderCkBox.setValue( properties.getShowBorderValue() );
	}// end init()
}// end GraphicWidgetDlgBox
