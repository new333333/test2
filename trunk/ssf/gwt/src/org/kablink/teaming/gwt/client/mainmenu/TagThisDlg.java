/**
 * Copyright (c) 1998-2010 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2010 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2010 Novell, Inc. All Rights Reserved.
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
package org.kablink.teaming.gwt.client.mainmenu;

import org.kablink.teaming.gwt.client.EditCanceledHandler;
import org.kablink.teaming.gwt.client.EditSuccessfulHandler;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingMessages;
import org.kablink.teaming.gwt.client.util.BinderInfo;
import org.kablink.teaming.gwt.client.widgets.DlgBox;

import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.VerticalPanel;


/**
 * Implements a dialog for managing folder and workspace tags.
 *  
 * @author drfoster@novell.com
 */
@SuppressWarnings("unused")
public class TagThisDlg extends DlgBox implements EditSuccessfulHandler, EditCanceledHandler {
	private final static String IDBASE = "tagThis_";	// Base ID for rows in the tag this Grid.

	private Grid m_tagThisGrid;				// Once displayed, the table with the dialog's contents.
	private GwtTeamingMessages m_messages;	// Access to the GWT UI messages.

	/*
	 * Inner class that wraps labels displayed in the dialog's content.
	 */
	private class DlgLabel extends Label {
		public DlgLabel(String label) {
			super(label);
			addStyleName("tagThisDlg_Label");
		}
	}

	/**
	 * Class constructor.
	 * 
	 * @param autoHide
	 * @param modal
	 * @param left
	 * @param top
	 * @param binderInfo
	 * @param dlgCaption
	 */
	public TagThisDlg(boolean autoHide, boolean modal, int left, int top, BinderInfo binderInfo, String dlgCaption) {
		// Initialize the superclass...
		super(autoHide, modal, left, top);

		// ...initialize everything else...
		m_messages           = GwtTeaming.getMessages();
	
		// ...and create the dialog's content.
		createAllDlgContent(
			dlgCaption,
			this,		// The dialog's EditSuccessfulHandler.
			this,		// The dialog's EditCanceledHandler.
			binderInfo);	// Data passed via global data members. 
	}
	
	/**
	 * Creates all the controls that make up the dialog.
	 * 
	 * Implements the DlgBox.createContent() abstract method.
	 * 
	 * @param ignored
	 * 
	 * @return
	 */
	@Override
	public Panel createContent(Object callbackData) {
		BinderInfo binderInfo = ((BinderInfo) callbackData);
		
		// Create a panel to hold the dialog's content...
		VerticalPanel vp = new VerticalPanel();

//!		...this needs to be implemented...
		vp.add(new DlgLabel("...this needs to be implemented..."));
		
		// Finally, return the panel the with the dialog's contents.
		return vp;
	}
	
	
	/**
	 * This method gets called when user user presses the Cancel push
	 * button.
	 * 
	 * Implements the EditCanceledHandler.editCanceled() interface
	 * method.
	 * 
	 * @return
	 */
	public boolean editCanceled() {
		// Simply return true to allow the dialog to close.
		return true;
	}

	
	/**
	 * This method gets called when user user presses the OK push
	 * button.
	 * 
	 * Implements the EditSuccessfulHandler.editSuccessful() interface
	 * method.
	 * 
	 * @param callbackData
	 * 
	 * @return
	 */
	public boolean editSuccessful(Object callbackData) {
//!		...this needs to be implemented...
		
		// Return true to close the dialog.
		return true;
	}

	
	/**
	 * Returns the edited List<ToolbarItem>.
	 * 
	 * Implements the DlgBox.getDataFromDlg() abstract method.
	 * 
	 * @return
	 */
	@Override
	public Object getDataFromDlg() {
//!		...this needs to be implemented...
		return "";
	}

	/**
	 * Returns the Widget to give the focus to.
	 * 
	 * Implements the DlgBox.getFocusWidget() abstract method.
	 * 
	 * @return
	 */
	@Override
	public FocusWidget getFocusWidget() {
		return null;
	}
}
