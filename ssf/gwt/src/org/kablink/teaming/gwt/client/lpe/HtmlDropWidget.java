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

import java.util.ArrayList;

import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.widgets.DlgBox;
import org.kablink.teaming.gwt.client.widgets.PropertiesObj;
import org.kablink.teaming.gwt.client.widgets.TinyMCEDlg;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.FlowPanel;

/**
 * 
 * @author jwootton
 *
 */
public class HtmlDropWidget extends DropWidget
{
	private static TinyMCEDlg m_tinyMCEDlg = null;		// For efficiency sake, we only create one dialog box.
	private HtmlProperties	m_properties = null;
	private FlowPanel m_htmlPanel;
	

	/**
	 * 
	 */
	public HtmlDropWidget( LandingPageEditor lpe, HtmlConfig configData )
	{
		HtmlProperties properties;
		
		properties = null;
		if ( configData != null )
			properties = configData.getProperties();
		
		init( lpe, properties );
	}
	
	
	/**
	 * 
	 */
	public HtmlDropWidget( LandingPageEditor lpe, HtmlProperties properties )
	{
		init( lpe, properties );
	}
	

	/**
	 * Create a configuration string that represents this widget and that can be stored in the db.
	 */
	public String createConfigString()
	{
		return m_properties.createConfigString();
	}
	
	
	/**
	 * Return the drag proxy object that should be displayed when the user drags this item.
	 */
	public DragProxy getDragProxy()
	{
		if ( m_dragProxy == null )
		{
			// Create a drag proxy that will be displayed when the user drags this item.
			m_dragProxy = new DragProxy( GwtTeaming.getImageBundle().landingPageEditorHtml(), GwtTeaming.getMessages().lpeHtml() );
		}
		
		return m_dragProxy;
	}
	

	/**
	 * Return the dialog box used to edit the properties of this widget.
	 */
	public DlgBox getPropertiesDlgBox( int x, int y )
	{
		// Have we already created a dialog box?
		if ( m_tinyMCEDlg == null )
		{
			LPETinyMCEConfiguration tinyMCEConfig;
			FileAttachments fileAttachments;
			ArrayList<String> listOfFileNames = null;
			
			// No, create one.
			tinyMCEConfig = new LPETinyMCEConfiguration( m_lpe, m_lpe.getBinderId() );
			
			// Get the file attachments for the landing page.
			listOfFileNames = new ArrayList<String>();
			fileAttachments = m_lpe.getFileAttachments();
			if ( fileAttachments != null )
			{
				int i;
				
				for (i = 0; i < fileAttachments.getNumAttachments(); ++i)
				{
					String fileName;
					
					fileName = fileAttachments.getFileName( i );
					listOfFileNames.add( fileName );
				}
			}
			tinyMCEConfig.setListOfFileAttachments( listOfFileNames );

			// No, create a "Edit Advanced Branding" dialog.
			m_tinyMCEDlg = new TinyMCEDlg(
										GwtTeaming.getMessages().lpeEditHtml(),
										tinyMCEConfig,
										this,
										this,
										false,
										true,
										x,
										y,
										null );
		}
		else
		{
			// Yes, update the controls in the dialog with the values from the properties.
			m_tinyMCEDlg.init( m_properties.getHtml() );
			m_tinyMCEDlg.initHandlers( this, this );
		}
		
		return m_tinyMCEDlg;
	}
	
	
	/**
	 * 
	 */
	private void init( LandingPageEditor lpe, HtmlProperties properties )
	{
		FlowPanel wrapperPanel;
		
		m_lpe = lpe;
		
		wrapperPanel = new FlowPanel();
		wrapperPanel.addStyleName( "dropWidgetWrapperPanel" );
		
		// Create an Edit/Delete control and position it at the top/right of this widget.
		// This control allows the user to edit the properties of this widget and to delete this widget.
		{
			ActionsControl ctrl;
			FlowPanel panel;
			
			ctrl = new ActionsControl( this, this, this );
			ctrl.addStyleName( "upperRight" );
			
			// Wrap the edit/delete control in a panel.  We position the edit/delete control on the right
			// side of the wrapper panel.
			panel = new FlowPanel();
			panel.addStyleName( "editDeleteWrapperPanel" );
			
			panel.add( ctrl );
			wrapperPanel.add( panel );
		}
		
		// Create the controls that will hold the html
		{
			m_htmlPanel = new FlowPanel();
			m_htmlPanel.addStyleName( "lpeDropWidget" );
			m_htmlPanel.addStyleName( "lpeHtmlWidget" );
			
			wrapperPanel.add( m_htmlPanel );
		}
		
		// Create an object to hold all of the properties that define an "HTML" widget.
		m_properties = new HtmlProperties();
		
		// If we were passed some properties, make a copy of them.
		if ( properties != null )
			m_properties.copy( properties );
		
		// All composites must call initWidget() in their constructors.
		initWidget( wrapperPanel );

		// Update the dynamic parts of this widget
		updateWidget( m_properties );
	}
	
	
	/**
	 * Create the appropriate ui based on the given properties.
	 */
	public void updateWidget( Object props )
	{
		// Save the properties that were passed to us.
		if ( props instanceof PropertiesObj )
			m_properties.copy( (PropertiesObj) props );
		else if ( props instanceof String )
		{
			m_properties.setHtml( (String) props );
		}
		
		// Update this widget with the given html.
		if ( m_htmlPanel != null )
			m_htmlPanel.getElement().setInnerHTML( m_properties.getHtml() );
	}
}
