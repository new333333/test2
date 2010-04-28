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
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.util.TagInfo;
import org.kablink.teaming.gwt.client.util.TagType;
import org.kablink.teaming.gwt.client.widgets.DlgBox;

import com.google.gwt.dom.client.Text;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;


/**
 * Implements a dialog for managing folder and workspace tags.
 *  
 * @author drfoster@novell.com
 */
@SuppressWarnings("unused")
public class TagThisDlg extends DlgBox implements EditSuccessfulHandler, EditCanceledHandler {
	private final static String IDBASE				= "tagThis_";		// Base ID for rows in the tag this Grid.
	private final static String OPTION_HEADER_ID	= "optionHeader";	//
	private final static int	MAX_TAG_LENGTH		= 60;				// As per ObjectKeys.MAX_TAG_LENGTH.
	private final static int	VISIBLE_TAG_LENGTH	= 20;				// Any better guesses?

	private BinderInfo m_currentBinder;				// The currently selected binder.
	private boolean m_isPublicTagManager;			// true -> The user can manage public tags on the binder.  false -> They can't.
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
	 * @param binderTags
	 * @param isPublicTagManager
	 * @param dlgCaption
	 */
	public TagThisDlg(boolean autoHide, boolean modal, int left, int top, BinderInfo currentBinder, List<TagInfo> binderTags, boolean isPublicTagManager, String dlgCaption) {
		// Initialize the superclass...
		super(autoHide, modal, left, top, DlgButtonMode.Close);

		// ...initialize everything else...
		m_messages = GwtTeaming.getMessages();
		m_images = GwtTeaming.getMainMenuImageBundle();
		m_isPublicTagManager = isPublicTagManager;
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
		Element e = grid.getRowFormatter().getElement(row); 
		e.setId(OPTION_HEADER_ID);
		e.addClassName("tagThisDlg_SectionHeaderRow");
		e = grid.getCellFormatter().getElement(row, 0);
		e.setAttribute("colspan", "2");
		e.addClassName("tagThisDlg_SectionHeaderCell");
		grid.setWidget(row, 0, header);
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
		vp.add(m_tagThisGrid);
		
		// ...and return the panel.
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
		// Nothing to do.  Return true to close the dialog.
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
		renderTagLinkRows(m_tagThisGrid, m_tagThisGrid.getRowCount());
		
		// ...render the personal tags into the panel...
		addHeaderRow(m_tagThisGrid, m_tagThisGrid.getRowCount(), m_messages.mainMenuTagThisDlgPersonalTags());
		if (0 < m_personalTagsCount) {
			for (int i = 0; i < m_personalTagsCount; i += 1) {
				renderDefinedTagRow(m_tagThisGrid, m_tagThisGrid.getRowCount(), m_personalTags.get(i));
			}
		}
		else {
			renderNoTagsRow(m_tagThisGrid, m_tagThisGrid.getRowCount(), ("NoTags_" + TagType.PERSONAL));
		}
		renderAddTagRow(m_tagThisGrid, m_tagThisGrid.getRowCount(), TagType.PERSONAL);

		// ...and render the community tags into the panel.
		addHeaderRow(m_tagThisGrid, m_tagThisGrid.getRowCount(), m_messages.mainMenuTagThisDlgCommunityTags());
		if (0 < m_communityTagsCount) {
			for (int i = 0; i < m_communityTagsCount; i += 1) {
				renderDefinedTagRow(m_tagThisGrid, m_tagThisGrid.getRowCount(), m_communityTags.get(i));
			}
		}
		else {
			renderNoTagsRow(m_tagThisGrid, m_tagThisGrid.getRowCount(), ("NoTags_" + TagType.COMMUNITY));
		}
		renderAddTagRow(m_tagThisGrid, m_tagThisGrid.getRowCount(), TagType.COMMUNITY);
	}
	
	/*
	 * Renders an add tag row in a Grid.
	 */
	private void renderAddTagRow(Grid grid, int row, final TagType tagType) {
		// Is the user allowed to add this type of tag to the binder?
		boolean allowAdd = ((TagType.PERSONAL == tagType) || m_isPublicTagManager);
		if (allowAdd) {
			// Yes!  Create a row for the add...
			grid.insertRow(row);
			String rowId = (IDBASE + "Add_" + tagType);
			grid.getRowFormatter().getElement(row).setId(rowId);
			Element e = grid.getCellFormatter().getElement(row, 0);
			e.setAttribute("colspan", "2");

			// Create a panel for the add widgets...
			FlowPanel addPanel = new FlowPanel();
			addPanel.addStyleName("tagThisDlg_Add");
			
			// ...create the tag name <INPUT> widget...
			final TextBox addName = new TextBox();
			addName.addStyleName("tagThisDlg_AddInput");
			addPanel.add(addName);
			addName.setMaxLength(MAX_TAG_LENGTH);
			addName.setVisibleLength(VISIBLE_TAG_LENGTH);
			
			// ...and create the add push button.
			Button addButton = new Button(m_messages.mainMenuTagThisDlgAdd(), new ClickHandler() {
				public void onClick(ClickEvent event) {
					// Is the name of the tag to add valid?
					String tagName = addName.getValue();
					tagName = validateTagName(tagName, tagType);
					if (!(GwtClientHelper.hasString(tagName))) {
						// No!  validateTagName() will have told the user
						// about any problems.  Simply bail.
						return;
					}
					
					// Generate a TagInfo for the new tag.
					TagInfo addTag = new TagInfo();
					addTag.setTagName(tagName);
					addTag.setTagType(tagType);
					
					// Can we and add it to the binder?
					GwtTeaming.getRpcService().addBinderTag(m_currentBinder.getBinderId(), addTag, new AsyncCallback<TagInfo>() {
						public void onFailure(Throwable t) {
							Window.alert(t.toString());
						}
						public void onSuccess(TagInfo addedTag) {
							// Perhaps.  Did it really get added?
							if (null != addedTag) {
								// Yes!  Add it to the appropriate list...
								if (addedTag.isCommunityTag()) {m_communityTags.add(addedTag); m_communityTagsCount += 1;}
								else                           {m_personalTags.add(addedTag);  m_personalTagsCount  += 1;}
								
								// ...and regenerate the dialog's contents.
								reRenderDlg();
							}
						}
					});
				}
			});
			addButton.addStyleName("tagThisDlg_AddButton teamingButton");
			addPanel.add(addButton);
			
			// Finally, add the add widget's panel to the Grid.
			grid.setWidget(row, 0, addPanel);
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

		// ...and if the user can delete this tag...
		boolean allowDelete = (ti.isPersonalTag() || m_isPublicTagManager);
		if (allowDelete) {
			// ...create a delete button anchor...
			Image deleteImg = new Image(m_images.tagDelete());
			deleteImg.setTitle(m_messages.mainMenuTagThisDlgDelete());
			deleteImg.addStyleName("tagThisDlg_DeleteImg");
			Anchor deleteAnchor = new Anchor();
			deleteAnchor.addStyleName("tagThisDlg_DeleteAnchor");
			deleteAnchor.getElement().appendChild(deleteImg.getElement());
			deleteAnchor.addClickHandler(new ClickHandler() {
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
							reRenderDlg();
						}
					});
				}
			});
			grid.setWidget(row, 0, deleteAnchor);
		}

		// ...and create the row's label.
		grid.setWidget(row, 1, new DlgLabel(ti.getTagName()));
		grid.getCellFormatter().setWidth(row, 1, "100%");
	}

	/*
	 * Renders a no tags row in a Grid.
	 */
	private void renderNoTagsRow(Grid grid, int row, String idExtension) {
		grid.insertRow(row);
		String rowId = (IDBASE + idExtension);
		grid.getRowFormatter().getElement(row).setId(rowId);
		grid.setWidget(row, 1, new DlgLabel(m_messages.mainMenuTagThisDlgNoTags()));
	}
	
	/*
	 * Renders rows for the links to personal and community tags in a
	 * Grid.
	 */
	private void renderTagLinkRows(Grid grid, int row) {
		boolean tagLinksDisplayed = false;
		FlowPanel tagLinksPanel;
		Iterator<TagInfo> tagsIT;
		
		// If we have any personal tags...
		if (0 < m_personalTagsCount) {
			// ...render their links in a row.
			tagLinksDisplayed = true;
			grid.insertRow(row);
			String rowId = (IDBASE + "TagLinks_" + TagType.PERSONAL);
			grid.getRowFormatter().getElement(row).setId(rowId);
			tagLinksPanel = new FlowPanel();
			for (tagsIT = m_personalTags.iterator(); tagsIT.hasNext(); ) {
				renderTagLink(tagLinksPanel, tagsIT.next());
			}
			grid.setWidget(row, 1, tagLinksPanel);
		}
		
		// If we have any community tags...
		if (0 < m_communityTagsCount) {
			// ...render their links in a row...
			tagLinksDisplayed = true;
			grid.insertRow(row);
			String rowId = (IDBASE + "TagLinks_" + TagType.COMMUNITY);
			grid.getRowFormatter().getElement(row).setId(rowId);
			tagLinksPanel = new FlowPanel();
			for (tagsIT = m_communityTags.iterator(); tagsIT.hasNext(); ) {
				renderTagLink(tagLinksPanel, tagsIT.next());
			}
			grid.setWidget(row, 1, tagLinksPanel);
		}

		// If we didn't render any tag links...
		if (!tagLinksDisplayed) {
			// ...render a row saying that.
			renderNoTagsRow(m_tagThisGrid, m_tagThisGrid.getRowCount(), "TagLinks_Empty");
		}
	}
	
	/*
	 * Renders the link for a tag into a FlowPanel.
	 */
	private void renderTagLink(FlowPanel tagLinksPanel, final TagInfo tag) {
//!		...this needs to be implemented...
		Element e = tagLinksPanel.getElement();
		String innerHTML = e.getInnerHTML();
		if (GwtClientHelper.hasString(innerHTML)) {
			innerHTML += "";
		}
		else {
			innerHTML = "";
		}
		innerHTML += ((tag.isCommunityTag() ? "C:" : "P:") + tag.getTagName());
		e.setInnerHTML(innerHTML);
	}
	
	/*
	 * Re-renders the content of the dialog.
	 */
	private void reRenderDlg() {
		// Remove any existing content from the dialog...
		for (int i = (m_tagThisGrid.getRowCount() - 1); i >= 0; i -= 1) {
			m_tagThisGrid.removeRow(i);
		}
		
		// ...and regenerate it.
		populateTagThisGrid();
	}
	
	/*
	 * Does what's needed to validate tagName.  The user is informed
	 * if any errors are detected.
	 * 
	 * If the string needs to be modified to be validated, the modified
	 * string is returned.
	 */
	private String validateTagName(String tagName, TagType tagType) {
		// Do we have a tag name to validate?
		String reply = tagName;
		if (GwtClientHelper.hasString(reply)) {
			// Yes!
			
			List<TagInfo> tagList = ((TagType.COMMUNITY == tagType) ? m_communityTags : m_personalTags);
			
//!			...this needs to be implemented...
		}
		return reply;
	}
}
