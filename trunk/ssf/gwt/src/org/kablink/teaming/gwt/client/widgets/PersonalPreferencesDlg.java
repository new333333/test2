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
import org.kablink.teaming.gwt.client.GwtMainPage;
import org.kablink.teaming.gwt.client.GwtPersonalPreferences;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingException;
import org.kablink.teaming.gwt.client.GwtTeamingMessages;
import org.kablink.teaming.gwt.client.GwtTeamingException.ExceptionType;
import org.kablink.teaming.gwt.client.service.GwtRpcServiceAsync;
import org.kablink.teaming.gwt.client.widgets.DlgBox;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.HTMLTable;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;


/**
 * 
 * @author jwootton
 *
 */
public class PersonalPreferencesDlg extends DlgBox
{
	private RadioButton m_useBrandingImgRb;
	private RadioButton m_useAdvancedBrandingRb;
	private ListBox m_brandingImgListbox;
	private ListBox m_backgroundImgListbox;
	private AsyncCallback<ArrayList<String>> m_rpcReadCallback = null;
	

	/**
	 * 
	 */
	public PersonalPreferencesDlg(
		EditSuccessfulHandler editSuccessfulHandler,	// We will call this handler when the user presses the ok button
		EditCanceledHandler editCanceledHandler, 		// This gets called when the user presses the Cancel button
		boolean autoHide,
		boolean modal,
		int xPos,
		int yPos )
	{
		super( autoHide, modal, xPos, yPos );
		
		// Create the header, content and footer of this dialog box.
		createAllDlgContent( GwtTeaming.getMessages().personalPreferencesDlgHeader(), editSuccessfulHandler, editCanceledHandler, null ); 
	}// end PersonalPreferencesDlg()
	

	/**
	 * Create all the controls that make up the dialog box.
	 */
	public Panel createContent( Object props )
	{
		FlowPanel mainPanel = null;

		mainPanel = new FlowPanel();
		mainPanel.setStyleName( "teamingDlgBoxContent" );

		mainPanel.add( new Label( "This is the Personal Preferences dialog" ) );
		
		return mainPanel;
	}// end createContent()
	
	
	/**
	 * Get the data from the controls in the dialog box and store the data in a GwtPersonalPreferences obj.
	 */
	public Object getDataFromDlg()
	{
		return new GwtPersonalPreferences();
	}// end getDataFromDlg()
	
	
	/**
	 * Return the widget that should get the focus when the dialog is shown. 
	 */
	public FocusWidget getFocusWidget()
	{
		return null;
	}// end getFocusWidget()
	
	
	/**
	 * Initialize the controls in the dialog with the values from the given personal preferences.
	 */
	public void init( GwtPersonalPreferences personalPrefs )
	{
	}// end init()


	/**
	 * For the given image name, select the appropriate file name in the given listbox.
	 */
	private void selectImageInListbox( ListBox listbox, String imgName )
	{
		boolean foundImgName = false;
		int index;
		
		// Do we have an image name.
		if ( imgName != null && imgName.length() > 0 )
		{
			// Yes, try to select the image name in the given listbox.
			index = selectListboxItemByValue( listbox, imgName );
			if ( index != -1 )
				foundImgName = true;
		}
		else
		{
			// No, try to select the default Teaming image option.
			index = selectListboxItemByValue( listbox, BrandingPanel.DEFAULT_TEAMING_IMAGE );
			if ( index != -1 )
				foundImgName = true;
		}
		
		// Did we find the image name in the listbox?
		if ( foundImgName == false )
		{
			String value;
			
			// No
			// Are there files to select from in the listbox?
			value = listbox.getValue( 0 );
			if ( value != null && value.equalsIgnoreCase( "no images" ) )
			{
				// No, select the "no images available" option.
				listbox.setSelectedIndex( 0 );
			}
			else
			{
				// Yes
				// Try to select the default Teaming image option in the listbox.
				index = selectListboxItemByValue( listbox, BrandingPanel.DEFAULT_TEAMING_IMAGE );
				
				// Did we select the default teaming image?
				if ( index == -1 )
				{
					// No, select "no image"
					selectListboxItemByValue( listbox, BrandingPanel.NO_IMAGE );
				}
			}
		}
	}// end selectImageInListbox()
	
	
	/**
	 * For the given listbox, select the item in the listbox that has the given value.
	 */
	private int selectListboxItemByValue( ListBox listbox, String value )
	{
		int i;
		
		for (i = 0; i < listbox.getItemCount(); ++i)
		{
			String tmp;
			
			tmp = listbox.getValue( i );
			if ( tmp != null && tmp.equalsIgnoreCase( value ) )
			{
				listbox.setSelectedIndex( i );
				return i;
			}
		}
		
		// If we get here it means we did not find an item in the listbox with the given value.
		return -1;
	}// end selectListboxItemByValue()
}// end PersonalPreferencesDlg
