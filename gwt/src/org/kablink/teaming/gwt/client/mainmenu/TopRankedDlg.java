/**
 * Copyright (c) 1998-2014 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2014 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2014 Novell, Inc. All Rights Reserved.
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
import java.util.List;

import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingMainMenuImageBundle;
import org.kablink.teaming.gwt.client.GwtTeamingMessages;
import org.kablink.teaming.gwt.client.event.GotoPermalinkUrlEvent;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.util.TopRankedInfo;
import org.kablink.teaming.gwt.client.widgets.DlgBox;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.HTMLTable.CellFormatter;

/**
 * Implements a dialog for selecting a top ranked item.
 *  
 * @author drfoster@novell.com
 */
public class TopRankedDlg extends DlgBox {
	private Grid							m_triGrid;			// Once displayed, the table of top ranked items.
	private GwtTeamingMainMenuImageBundle	m_images;			// Access to the GWT UI menu images.
	private GwtTeamingMessages				m_messages;			// Access to the GWT UI messages.
	private int								m_triListCount;		// Count of items in m_triList.
	private int								m_triPeopleCount;	// Count of items in m_triPeople.
	private int								m_triPlacesCount;	// Count of items in m_triPlaces.
	private List<TopRankedInfo> 			m_triList;			// Full List<TopRankedInfo>.
	private List<TopRankedInfo> 			m_triPeople;		// List<TopRankedInfo> of top ranked people.
	private List<TopRankedInfo> 			m_triPlaces;		// List<TopRankedInfo> of top ranked places.

	/*
	 * Inner class that wraps labels displayed in the dialog's content.
	 */
	private class DlgLabel extends Label {
		public DlgLabel(String label) {
			super(label);
			addStyleName("topRankedDlg_Label");
		}
	}

	/**
	 * Class constructor.
	 * 
	 * @param autoHide
	 * @param modal
	 * @param left
	 * @param top
	 * @param triList
	 */
	public TopRankedDlg(boolean autoHide, boolean modal, int left, int top, List<TopRankedInfo> triList) {
		// Initialize the superclass...
		super(autoHide, modal, left, top, DlgButtonMode.Close);

		// ...initialize everything else...
		m_images   = GwtTeaming.getMainMenuImageBundle();
		m_messages = GwtTeaming.getMessages();
		initializeLists(triList);
	
		// ...and create the dialog's content.
		createAllDlgContent(
			m_messages.mainMenuTopRankedDlgHeader(),
			getSimpleSuccessfulHandler(),	// The dialog's EditSuccessfulHandler.
			getSimpleCanceledHandler(),		// The dialog's EditCanceledHandler.
			null);							// Data passed via global data members. 
	}
	
	/*
	 * Adds a section banner row to the top ranked grid.
	 */
	private void addSectionBanner(Grid grid, int row, String bannerText) {
		DlgLabel banner = new DlgLabel(bannerText);
		banner.addStyleName("topRankedDlg_SectionBanner");

		grid.insertRow(row);
		Element e = grid.getRowFormatter().getElement(row); 
		e.addClassName("topRankedDlg_SectionBannerRow");
		e = grid.getCellFormatter().getElement(row, 0);
		e.addClassName("topRankedDlg_SectionBannerCell");
		grid.setWidget(row, 0, banner);
		GwtClientHelper.setGridColSpan(grid, row, 0, 2);
	}

	/*
	 * Adds a section header row to the top ranked grid.
	 */
	private void addSectionHeader(Grid grid, int row, String headerText) {
		grid.insertRow(row);
		grid.getRowFormatter().addStyleName(row, "topRankedDlg_SectionHeaderRow");

		CellFormatter gridCF = grid.getCellFormatter();
		grid.setWidget(row, 0, new InlineLabel(m_messages.mainMenuTopRankedDlgRating()));
		gridCF.getElement(row, 0).addClassName("topRankedDlg_SectionHeaderCell topRankedDlg_SectionHeaderCellRating");
		
		grid.setWidget(row, 1, new InlineLabel(headerText));
		gridCF.getElement(row, 1).addClassName("topRankedDlg_SectionHeaderCell topRankedDlg_SectionHeaderCellTitle");
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
	public Panel createContent(Object ignored) {
		// Create a panel to hold the dialog's content...
		VerticalPanel vp = new VerticalPanel();
		
		// Are there any top ranked items to display in the dialog?
		if (0 < m_triListCount) {
			// Yes!  Create a grid to contain them... 
			m_triGrid = new Grid(0, 2);
			m_triGrid.addStyleName("topRankedDlg_Grid");
			m_triGrid.setCellPadding(0);
			m_triGrid.setCellSpacing(0);
			
			// ...render the top ranked places  into the panel...
			if (0 < m_triPlacesCount) {
				addSectionBanner(m_triGrid, m_triGrid.getRowCount(), m_messages.mainMenuTopRankedDlgTopRankedPlaces());
				addSectionHeader(m_triGrid, m_triGrid.getRowCount(), m_messages.mainMenuTopRankedDlgPlaces());
				for (int i = 0; i < m_triPlacesCount; i += 1) {
					renderRow(m_triGrid, m_triGrid.getRowCount(), m_triPlaces.get(i));
				}
			}
			
			// ...render the top ranked people into the panel...
			if (0 < m_triPeopleCount) {
				addSectionBanner(m_triGrid, m_triGrid.getRowCount(), m_messages.mainMenuTopRankedDlgTopRankedPeople());
				addSectionHeader(m_triGrid, m_triGrid.getRowCount(), m_messages.mainMenuTopRankedDlgPeople());
				for (int i = 0; i < m_triPeopleCount; i += 1) {
					renderRow(m_triGrid, m_triGrid.getRowCount(), m_triPeople.get(i));
				}
			}
			
			// ...and connect everything together.
			vp.add(m_triGrid);
		}
		
		else {
			// No, there weren't any top ranked items to display in the
			// dialog!  Put a simple no available items message.
			DlgLabel noItemsLabel = new DlgLabel(m_messages.mainMenuTopRankedDlgNoItems());
			noItemsLabel.addStyleName("topRankedDlg_Empty");
			vp.add(noItemsLabel);
		}
		
		// Finally, return the panel the with the dialog's contents.
		return vp;
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
		// Nothing to do.  Return something so that the dialog can
		// close.
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
	 * Renders a TopRankedInfo as a row in a Grid.
	 */
	private void renderRow(Grid grid, int row, final TopRankedInfo tri) {
		// Insert a row into the Grid...
		grid.insertRow(row);

		// ...add the Image for the rating...
		Image starImg = new Image(m_images.ratingStar());
		starImg.addStyleName("topRankedDlg_ItemStar");
		String trStyle = tri.getTopRankedCSS();
		if (GwtClientHelper.hasString(trStyle)) {
			trStyle = ("gwtUI_" + trStyle);
			starImg.addStyleName(trStyle);
			if (GwtClientHelper.jsIsIE()) {
				starImg.addStyleName(trStyle + "IE");
			}
		}
		grid.setWidget(row, 0, starImg);

		// ...and add the Anchor for the link
		Anchor triAnchor = new Anchor(tri.getTopRankedName());
		triAnchor.addStyleName("topRankedDlg_ItemAnchor");
		String hover = tri.getTopRankedHoverText();
		if (GwtClientHelper.hasString(hover)) {
			triAnchor.setTitle(hover);
		}
		triAnchor.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				// Hide the dialog and go to the top ranked item's
				// permalink.
				hide();
				GwtTeaming.fireEvent(
					new GotoPermalinkUrlEvent(
						tri.getTopRankedPermalinkUrl()));
			}
		});
		grid.setWidget(row, 1, triAnchor);
		grid.getCellFormatter().setWidth(row, 1, "100%");
	}
	
	/*
	 * Initialize the top ranked item lists and counts.
	 */
	private void initializeLists(List<TopRankedInfo> triList) {
		m_triPeople = new ArrayList<TopRankedInfo>();
		m_triPlaces = new ArrayList<TopRankedInfo>();
		
		m_triList = ((null == triList) ? new ArrayList<TopRankedInfo>() : triList);
		m_triListCount = ((null == m_triList) ? 0 : m_triList.size());
		for (int i = (m_triListCount - 1); i >= 0; i -= 1) {
			TopRankedInfo tri = m_triList.get(i);
			switch (tri.getTopRankedType()) {
			case PERSON:  m_triPeople.add(0, tri); m_triPeopleCount += 1; break;
			case PLACE:   m_triPlaces.add(0, tri); m_triPlacesCount += 1; break;
			default:      m_triList.remove(i);     m_triListCount   -= 1; break;
			}
		}
	}
}
