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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.kablink.teaming.gwt.client.EditCanceledHandler;
import org.kablink.teaming.gwt.client.EditSuccessfulHandler;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.widgets.DlgBox;
import org.kablink.teaming.gwt.client.widgets.PropertiesObj;

import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * 
 * @author jwootton
 *
 */
public class UtilityElementWidgetDlgBox extends DlgBox
{
	private HashMap<RadioButton, UtilityElement> m_radioBtns;
	
	/**
	 * 
	 */
	public UtilityElementWidgetDlgBox(
		EditSuccessfulHandler editSuccessfulHandler,	// We will call this handler when the user presses the ok button
		EditCanceledHandler editCanceledHandler, 		// This gets called when the user presses the Cancel button
		boolean autoHide,
		boolean modal,
		int xPos,
		int yPos,
		UtilityElementProperties properties ) // Where properties used in the dialog are read from and saved to.
	{
		super( autoHide, modal, xPos, yPos );

		m_radioBtns = new HashMap<RadioButton, UtilityElement>();
		
		// Create the header, content and footer of this dialog box.
		createAllDlgContent( GwtTeaming.getMessages().utilityElementProperties(), editSuccessfulHandler, editCanceledHandler, properties ); 
	}// end UtilityElementWidgetDlgBox()
	

	/**
	 * Create all the controls that make up the dialog box.
	 */
	public Panel createContent( Object props )
	{
		UtilityElementProperties properties;
		VerticalPanel mainPanel;
		RadioButton radioBtn;
		
		properties = (UtilityElementProperties) props;

		mainPanel = new VerticalPanel();
		mainPanel.setStyleName( "teamingDlgBoxContent" );

		// Create a radio button for each of the possible utility elements.
		for (UtilityElement utilityElement : UtilityElement.values())
		{
			radioBtn = new RadioButton( "utilityElements", utilityElement.getLocalizedText() );
			radioBtn.addStyleName( "marginBottom15em" );
			mainPanel.add( radioBtn );

			// Keep a list of the radio buttons and the utility element they are associated with.
			m_radioBtns.put( radioBtn, utilityElement );
		}
		
		init( properties );
		
		return mainPanel;
	}// end createContent()
	
	
	/**
	 * Get the data from the controls in the dialog box and store the data in the properties obj.
	 */
	public PropertiesObj getDataFromDlg()
	{
		UtilityElementProperties	properties;
		
		properties = new UtilityElementProperties();
		
		// Save away the selected utility element.
		properties.setType( getSelectedUtilityElement() );

		return properties;
	}// end getDataFromDlg()
	
	
	/**
	 * Return the widget that should get the focus when the dialog is shown. 
	 */
	public FocusWidget getFocusWidget()
	{
		return null;
	}// end getFocusWidget()
	
	
	/**
	 * Return the selected utility element.
	 */
	public UtilityElement getSelectedUtilityElement()
	{
		Set<Map.Entry<RadioButton, UtilityElement>> set;
		Iterator<Map.Entry<RadioButton, UtilityElement>> iterator;
		
		set = m_radioBtns.entrySet();
		iterator = set.iterator();
		
		// Spin through the list of radio buttons and see which one is selected.
		while ( iterator.hasNext() )
		{
			RadioButton radioBtn;
			Map.Entry<RadioButton, UtilityElement> nextEntry;
			
			// Is this radio button selected?
			nextEntry = iterator.next();
			radioBtn = nextEntry.getKey();
			if ( radioBtn.getValue() )
			{
				// Yes, return the utility element associated with the radio button.
				return nextEntry.getValue();
			}
		}
		
		// We should never get here but if we do, return "link to my workspace" as the selected utility element.
		return UtilityElement.LINK_TO_MYWORKSPACE; 
	}// end getSelectedUtilityElement()
	
	
	/**
	 * Initialize the controls on the page with the values from the properties.
	 */
	public void init(
		PropertiesObj props )
	{
		UtilityElementProperties properties;
		RadioButton firstRadioBtn = null;
		RadioButton radioBtn;
		Map.Entry<RadioButton, UtilityElement> nextEntry;
		boolean selectedRadioBtn = false;
		UtilityElement selectedUtilityElement;
		Set<Map.Entry<RadioButton, UtilityElement>> set;
		Iterator<Map.Entry<RadioButton, UtilityElement>> iterator;
		
		properties = (UtilityElementProperties) props;
		selectedUtilityElement = properties.getType();

		set = m_radioBtns.entrySet();
		iterator = set.iterator();
		
		// Unselect every radio button.
		while ( iterator.hasNext() )
		{
			nextEntry = iterator.next();
			radioBtn = nextEntry.getKey();
			radioBtn.setValue( false );
		}

		// Spin through the list of radio buttons and select the appropriate one.
		iterator = set.iterator();
		while ( iterator.hasNext() && !selectedRadioBtn )
		{
			UtilityElement nextUtilityElement;
			
			// Is this radio button selected?
			nextEntry = iterator.next();
			radioBtn = nextEntry.getKey();
			nextUtilityElement = nextEntry.getValue();
			
			if ( firstRadioBtn == null )
				firstRadioBtn = radioBtn;

			// Is this radio button associated with the selected utility element?
			if ( selectedUtilityElement != null && selectedUtilityElement.ordinal() == nextUtilityElement.ordinal() )
			{
				radioBtn.setValue( true );
				selectedRadioBtn = true;
			}
		}

		// Did we select a radio button?
		if ( !selectedRadioBtn )
		{
			// No, select the first one in the list.
			firstRadioBtn.setValue( true );
		}
		
	}// end init()

}// end UtilityElementWidgetDlgBox
