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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.kablink.teaming.gwt.client.EditCanceledHandler;
import org.kablink.teaming.gwt.client.EditSuccessfulHandler;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingMainMenuImageBundle;
import org.kablink.teaming.gwt.client.GwtTeamingMessages;
import org.kablink.teaming.gwt.client.util.BinderInfo;
import org.kablink.teaming.gwt.client.util.TagInfo;
import org.kablink.teaming.gwt.client.widgets.DlgBox;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.VerticalPanel;


/**
 * Implements a dialog for managing folder and workspace tags.
 *  
 * @author drfoster@novell.com
 */
@SuppressWarnings("unused")
public class TagThisDlg extends DlgBox implements EditSuccessfulHandler, EditCanceledHandler {
	private final static String IDBASE				= "tagThis_";	// Base ID for rows in the tag this Grid.
	private final static String OPTION_HEADER_ID	= "optionHeader";

	private BinderInfo m_currentBinder;				// The currently selected binder.
	private Grid m_tagThisGrid;						// Once displayed, the table with the dialog's contents.
	private GwtTeamingMainMenuImageBundle m_images;	// Access to the GWT main menu images.
	private GwtTeamingMessages m_messages;			// Access to the GWT UI messages.
	private int m_communityTagsCount;				// Count of TagInfo's in m_communityTags.
	private int m_personalTagsCount;				// Count of TagInfo's in m_personalTags.
	private List<TagInfo> m_communityTags;			// The community tags defined on the binder.
	private List<TagInfo> m_personalTags;			// The personal  tags defined on the binder.

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
	 * @param currentBinder
	 * @param dlgCaption
	 */
	public TagThisDlg(boolean autoHide, boolean modal, int left, int top, BinderInfo currentBinder, List<TagInfo> binderTags, String dlgCaption) {
		// Initialize the superclass...
		super(autoHide, modal, left, top, DlgButtonMode.Close);

		// ...initialize everything else...
		m_messages = GwtTeaming.getMessages();
		m_images = GwtTeaming.getMainMenuImageBundle();
		m_currentBinder = currentBinder;
		m_communityTags = new ArrayList<TagInfo>();
		m_personalTags = new ArrayList<TagInfo>();
		for (Iterator<TagInfo> tagsIT = binderTags.iterator(); tagsIT.hasNext(); ) {
			TagInfo ti = tagsIT.next();
			if (ti.isCommunityTag()) m_communityTags.add(ti);
			else                     m_personalTags.add(ti);
		}
		m_communityTagsCount = m_communityTags.size();
		m_personalTagsCount  = m_personalTags.size();
	
		// ...and create the dialog's content.
		createAllDlgContent(
			dlgCaption,
			this,	// The dialog's EditSuccessfulHandler.
			this,	// The dialog's EditCanceledHandler.
			null);	// Data passed via global data members. 
	}
	
	/*
	 * Adds a section header row to the tag this grid.
	 */
	private void addHeaderRow(Grid grid, int row, String headerText) {
		DlgLabel header = new DlgLabel(headerText);
		header.addStyleName("tagThisDlg_SectionHeader");

		grid.insertRow(row);
		grid.getRowFormatter().getElement(row).setId(OPTION_HEADER_ID);
		grid.setWidget(row, 1, header);
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
		// Create a panel and Grid to hold the dialog's content...
		VerticalPanel vp = new VerticalPanel();
		m_tagThisGrid = new Grid(0, 2);
		m_tagThisGrid.addStyleName("tagThisDlg_Grid");
		m_tagThisGrid.setCellPadding(0);
		m_tagThisGrid.setCellSpacing(0);
		populateTagThisGrid();
//!		vp.add(m_tagThisGrid);
		
//!		...this needs to be implemented...
		vp.add(new DlgLabel("...this needs to be implemented..."));
		
		// ...and return the panel the with the dialog's contents.
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
		// Return something so that the editSuccessful() method gets
		// called.  Doesn't matter what since we're passing our data
		// around via global data members.
		return Boolean.TRUE;
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

	/*
	 * Populate the tag this Grid with the defined tags.
	 */
	private void populateTagThisGrid() {
		// Render the tag links into the panel...
		addHeaderRow(m_tagThisGrid, m_tagThisGrid.getRowCount(), m_messages.mainMenuTagThisDlgTags());
		if (0 == (m_communityTagsCount + m_personalTagsCount)) {
		}
		else {
		}
		
		// ...render the personal tags into the panel...
		addHeaderRow(m_tagThisGrid, m_tagThisGrid.getRowCount(), m_messages.mainMenuTagThisDlgPersonalTags());
		if (0 < m_personalTagsCount) {
			for (int i = 0; i < m_personalTagsCount; i += 1) {
				renderDefinedTagRow(m_tagThisGrid, m_tagThisGrid.getRowCount(), m_personalTags.get(i));
			}
		}

		// ...and render the community tags into the panel.
		addHeaderRow(m_tagThisGrid, m_tagThisGrid.getRowCount(), m_messages.mainMenuTagThisDlgCommunityTags());
		if (0 < m_communityTagsCount) {
			for (int i = 0; i < m_communityTagsCount; i += 1) {
				renderDefinedTagRow(m_tagThisGrid, m_tagThisGrid.getRowCount(), m_communityTags.get(i));
			}
		}
	}
	
	/*
	 * Renders a TagInfo as a row in a Grid.
	 */
	private void renderDefinedTagRow(Grid grid, int row, final TagInfo ti) {
		// Create the row...
		grid.insertRow(row);
		String rowId = (IDBASE + ti.getTagName());
		grid.getRowFormatter().getElement(row).setId(rowId);
		
		// ...create a delete button anchor...
		Image img = new Image(m_images.tagDelete());
		img.setTitle(m_messages.mainMenuTagThisDlgDelete());
		img.addStyleName("tagThisDlg_DeleteImg");
		Anchor a = new Anchor();
		a.addStyleName("tagThisDlg_DeleteAnchor");
		a.getElement().appendChild(img.getElement());
		a.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				GwtTeaming.getRpcService().removeBinderTag(m_currentBinder.getBinderId(), ti, new AsyncCallback<Boolean>() {
					public void onFailure(Throwable t) {
						Window.alert(t.toString());
					}
					public void onSuccess(Boolean success) {
						// Remove the deleted TagInfo from its list...
						if (ti.isCommunityTag()) {m_communityTags.remove(ti); m_communityTagsCount -= 1;}
						else                     {m_personalTags.remove(ti);  m_personalTagsCount  -= 1;}

						// ...and regenerate the dialog's contents.
						for (int i = (m_tagThisGrid.getRowCount() - 1); i >= 0; i -= 1) {
							m_tagThisGrid.removeRow(i);
						}
						populateTagThisGrid();
					}
				});
			}
		});

		// ...and create the row's label.
		grid.setWidget(row, 0, a);
		grid.setWidget(row, 1, new DlgLabel(ti.getTagName()));
	}
}
