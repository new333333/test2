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

import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingMainMenuImageBundle;
import org.kablink.teaming.gwt.client.GwtTeamingMessages;
import org.kablink.teaming.gwt.client.MenuIds;
import org.kablink.teaming.gwt.client.mainmenu.SearchOptionsComposite.SearchOptionsCompositeClient;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;

import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TeamingPopupPanel;

/**
 * Class used to encapsulate the search widgets at the right of Vibe's
 * main menu.  
 * 
 * @author drfoster@novell.com
 */
public class GlobalSearchComposite extends Composite {
	private FlowPanel						m_mainPanel;	//
	private GwtTeamingMainMenuImageBundle	m_images;		//
	private GwtTeamingMessages 				m_messages;		//
	private MenuBarButton					m_soButton;		//
	private SearchMenuPanel					m_searchPanel;	//
	private TeamingPopupPanel               m_soPopup;		//

	/**
	 * Constructor method.
	 * 
	 * @param includeSearchOptions
	 */
	public GlobalSearchComposite(boolean includeSearchOptions) {
		// Initialize the super class...
		super();
		
		// ...initialize the global data members...
		m_images   = GwtTeaming.getMainMenuImageBundle();
		m_messages = GwtTeaming.getMessages();

		// ...initialize the GlobalSearchComposite's contents...
		initSearchContents(includeSearchOptions);

		// ...and initialize the Composite itself.
		initWidget(m_mainPanel);
	}
	
	/**
	 * Constructor method.
	 */
	public GlobalSearchComposite() {
		// Always use the initial form of the constructor.
		this(true);
	}

	/**
	 * Get'er methods.
	 * 
	 * @return
	 */
	public MenuBarButton     getSearchOptionsButton() {return m_soButton;   }
	public SearchMenuPanel   getSearchMenuPanel()     {return m_searchPanel;}
	public TeamingPopupPanel getSearchOptionsPopup()  {return m_soPopup;    }

	/*
	 * Initializes the contents of the GlobalSearchComposite.
	 */
	private void initSearchContents(boolean includeSearchOptions) {
		m_mainPanel = new FlowPanel();
		m_mainPanel.addStyleName("vibe-globalSearch");
		
		m_searchPanel = new SearchMenuPanel();
		m_mainPanel.add(m_searchPanel);
		
		if (includeSearchOptions) {
			m_soButton = new MenuBarButton(MenuIds.MAIN_GLOBAL_SEARCH_OPTIONS, m_images.searchOptions(), m_messages.mainMenuAltSearchOptions(), new Command() {
				@Override
				public void execute() {
					m_soButton.removeStyleName("subhead-control-bg2");
					m_soPopup = new TeamingPopupPanel(true, false);
					GwtClientHelper.rollDownPopup(m_soPopup);
					m_soPopup.addStyleName("searchOptions_Browser roundcornerSM-bottom");
					SearchOptionsComposite.createAsync(
							m_soPopup,
							new SearchOptionsCompositeClient() {					
						@Override
						public void onUnavailable() {
							// Nothing to do.  Error handled in
							// asynchronous provider.
						}
						
						@Override
						public void onSuccess(SearchOptionsComposite soc) {
							// Connect things together...
							soc.addStyleName("searchOptions");
							m_soPopup.setWidget(soc);
							m_soPopup.setGlassEnabled(true);
							m_soPopup.setGlassStyleName("vibe-mainMenuPopup_Glass");
							
							// ...and show the search options popup.  We do
							// ...this as a scheduled command so that the
							// ...asynchronous processing related to the
							// ...creation of the SearchOptionsComposite
							// ...has a chance to complete.
							GwtClientHelper.deferCommand(new ScheduledCommand() {
								@Override
								public void execute() {
									// Position and show the popup as per
									// the position of the search panel on
									// the menu.
									m_soPopup.setPopupPositionAndShow(new PopupPanel.PositionCallback() {
										@Override
										public void setPosition(int offsetWidth, int offsetHeight) {
											int soPopupLeft = ((m_soButton.getAbsoluteLeft() + m_soButton.getOffsetWidth()) - offsetWidth);
											int soPopupTop  = m_soButton.getElement().getAbsoluteBottom();
											
/*
	GwtClientHelper.deferredAlert(
		  "w:"   + offsetWidth                                 +
		"\nh:"   + offsetHeight                                +
		"\nb:l:" + m_soButton.getAbsoluteLeft()                +
		"\nb:w:" + m_soButton.getOffsetWidth()                 +
		"\nb:b:" + m_soButton.getElement().getAbsoluteBottom() +
		"\nl:"   + soPopupLeft                                 +
		"\nt:"   + soPopupTop);
*/
											
											m_soPopup.setPopupPosition(soPopupLeft, soPopupTop);
										}
									});
								}
							});
						}
					});
				}});
			m_soButton.getElement().setId(MenuIds.MAIN_GLOBAL_SEARCH_BUTTON);
			m_soButton.addStyleName("vibe-mainMenuButton subhead-control-bg1 roundcornerSM");
			
			MenuBar soBar = new MenuBar();
			soBar.getElement().setId(MenuIds.MAIN_GLOBAL_SEARCH_BAR);
			soBar.addStyleName("vibe-mainMenuSearchOptions_Button");
			soBar.addItem(m_soButton);
			m_mainPanel.add(soBar);
		}
	}
	
	/**
	 * Hides or shows this GlobalSearchComposite.
	 * 
	 * Overrides the UIObject.setVisible() method.
	 * 
	 * @param visible
	 */
	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		
		GwtClientHelper.setVisibile(m_searchPanel, true);
		GwtClientHelper.setVisibile(m_soButton,    true);
	}
}
