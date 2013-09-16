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

import org.kablink.teaming.gwt.client.EditCanceledHandler;
import org.kablink.teaming.gwt.client.EditSuccessfulHandler;
import org.kablink.teaming.gwt.client.GwtTeaming;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;


/**
 * This control allows the user to specify a color by picking a color or entering the name of a color 
 * @author jwootton
 *
 */
public class ColorCtrl extends VibeWidget
	implements ClickHandler, KeyUpHandler
{
	private TextBox m_colorTextbox = null;
	private InlineLabel m_sampleColor = null;
	private ColorPickerDlg m_colorPickerDlg = null;
	
	
	/**
	 * 
	 */
	public ColorCtrl()
	{
		VibeFlowPanel mainPanel;
		FlexTable table;
		int nextRow;
		
		mainPanel = new VibeFlowPanel();
		mainPanel.addStyleName( "colorCtrlMainPanel" );
		
		// Create a table where the color control and hint will live.
		nextRow = 0;
		table = new FlexTable();
		table.addStyleName( "colorControl" );
		table.getRowFormatter().setVerticalAlign( nextRow, HasVerticalAlignment.ALIGN_MIDDLE );
		mainPanel.add( table );
		
		// Add a text box where the user can enter the color
		{
			m_colorTextbox = new TextBox();
			m_colorTextbox.setVisibleLength( 20 );
			m_colorTextbox.addKeyUpHandler( this );
			table.setWidget( nextRow, 0, m_colorTextbox );
		}
		
		// Add a link next to the color textbox the user can click on to invoke a color picker.
		{
			Anchor colorLink;

			colorLink = new Anchor();
			colorLink.setTitle( GwtTeaming.getMessages().displayColorPicker() );
			colorLink.addStyleName( "colorPickerLink" );
			colorLink.addStyleName( "landingPagePropertiesLink" );
			colorLink.addStyleName( "subhead-control-bg1" );
			colorLink.addStyleName( "roundcornerSM" );
			table.setWidget( nextRow, 1, colorLink );

			// Add the browse image to the link.
			{
				Image colorPickerImg;
				Element linkElement;
				Element imgElement;
				
				colorPickerImg = new Image( GwtTeaming.getImageBundle().colorPicker() );
				linkElement = colorLink.getElement();
				imgElement = colorPickerImg.getElement();
				imgElement.getStyle().setMarginTop( 2, Style.Unit.PX );
				linkElement.appendChild( imgElement );
			}

			// Add a clickhandler to the color hint.  When the user clicks on the hint we
			// will invoke the color picker.
			colorLink.addClickHandler( this );
		}
		
		// Add a "sample text" field that will display the selected color.
		{
			Style style;
			
			m_sampleColor = new InlineLabel( "" );
			m_sampleColor.getElement().setInnerHTML( "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" );
			m_sampleColor.addStyleName( "editBrandingSampleText" );
			style = m_sampleColor.getElement().getStyle();
			style.setMarginLeft( 8, Unit.PX );
			table.setWidget( nextRow, 2, m_sampleColor );
		}

		// Add a hint below the color text box.
		{
			Label colorHint;

			++nextRow;
			colorHint = new Label( GwtTeaming.getMessages().colorHint() );
			table.setWidget( nextRow, 0, colorHint );
		}
		
		initWidget( mainPanel );
	}
	
	/**
	 * Return the selected color
	 */
	public String getColor()
	{
		return m_colorTextbox.getText();
	}
	
	/**
	 * 
	 */
	public void init( String color )
	{
		if ( color == null )
			color = "";
		
		m_colorTextbox.setText( color );
		
		// Update the sample text with the given color.
		updateSampleTextBgColor();
	}

	/**
	 * Invoke the "Color Picker" dialog.
	 */
	public void invokeColorPickerDlg( final int x, final int y )
	{
		PopupPanel.PositionCallback posCallback;
		
		// Have we already created a "Color Picker" dialog?
		if ( m_colorPickerDlg != null )
		{
			// Yes
			m_colorPickerDlg.setPopupPosition( x, y );
		}
		else
		{
			EditSuccessfulHandler editSuccessfulHandler;
			EditCanceledHandler editCanceledHandler;
			
			editSuccessfulHandler = new EditSuccessfulHandler()
			{
				/**
				 * This method gets called when user user presses ok in the "Color Picker" dialog.
				 */
				public boolean editSuccessful( Object obj )
				{
					m_colorPickerDlg.hide();

					if ( obj instanceof ColorPickerDlg.Color )
					{
						ColorPickerDlg.Color color;
						
						color = (ColorPickerDlg.Color) obj;
						
						// Put the selected color in the appropriate textbox.
						m_colorTextbox.setText( color.getName() );

						// Update the sample text with the selected color.
						updateSampleTextBgColor();
					}
					
					return true;
				}
			};
				
			editCanceledHandler = new EditCanceledHandler()
			{
				/**
				 * This method gets called when the user presses cancel in the "Color Picker" dialog.
				 */
				public boolean editCanceled()
				{
					m_colorPickerDlg.hide();
					
					return true;
				}
			};

			// No, create a "color picker" dialog.
			m_colorPickerDlg = new ColorPickerDlg(
												editSuccessfulHandler,
												editCanceledHandler,
												false,
												true,
												x,
												y,
												null );
		}

		posCallback = new PopupPanel.PositionCallback()
		{
			/**
			 * 
			 */
			public void setPosition( int offsetWidth, int offsetHeight )
			{
				m_colorPickerDlg.setPopupPosition( x - offsetWidth + 50, y );
			}
		};
		m_colorPickerDlg.setPopupPositionAndShow( posCallback );
	}


	/**
	 * Validate that the given color is valid.
	 */
	public boolean isColorValid( String color )
	{
		Element element;
		Style style;
		String origColor;
		boolean valid;
		
		if ( color == null )
			return false;
		
		element = m_sampleColor.getElement();
		style = element.getStyle();
		origColor = style.getBackgroundColor();
		
		try
		{
			String tmpColor;
			
			valid = true;
			
			// On IE, setBackgroundColor() will throw an exception if the color is not valid.
			// On FF, setBackgroundColor() will not set the color if is an invalid color.
			style.setBackgroundColor( color );
			
			// Get the background color.  If it is empty then we were passed an invalid color.
			tmpColor = style.getBackgroundColor();
			if ( tmpColor == null || tmpColor.length() == 0 )
				valid = false;
		}
		catch( Exception ex )
		{
			valid = false;
		}
		
		style.setBackgroundColor( origColor );
		
		return valid;
	}
	
	
	/**
	 * Invoke the color picker.
	 */
	public void onClick( ClickEvent event )
	{
		Widget anchor;
		
		// Get the anchor the user clicked on.
		anchor = (Widget) event.getSource();
		
		// Invoke the "Color Picker" dialog.
		invokeColorPickerDlg( anchor.getAbsoluteLeft(), anchor.getAbsoluteTop() );
	}

	/**
	 * Update the background color of the sample text.
	 */
	public void onKeyUp( KeyUpEvent event )
	{
		updateSampleTextBgColor();
	}

	/**
	 * Update the background color of the sample text with the color that is found in the textbox.
	 */
	private void updateSampleTextBgColor()
	{
		Element element;
		Style style;
		String color;
		
		element = m_sampleColor.getElement();
		style = element.getStyle();
		
		// Get the background color
		color = m_colorTextbox.getText();
		style.clearBackgroundColor();
		if ( color != null && color.length() > 0 )
		{
			try
			{
				style.setBackgroundColor( color );
			}
			catch (Exception ex)
			{
				// Nothing to do
			}
		}
	}
}
