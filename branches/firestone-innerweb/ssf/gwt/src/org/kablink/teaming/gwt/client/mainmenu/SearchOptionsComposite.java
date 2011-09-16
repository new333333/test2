/**
 * Copyright (c) 1998-2011 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2011 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2011 Novell, Inc. All Rights Reserved.
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

import java.util.Iterator;
import java.util.List;

import org.kablink.teaming.gwt.client.event.EventHelper;
import org.kablink.teaming.gwt.client.event.GotoPermalinkUrlEvent;
import org.kablink.teaming.gwt.client.event.SearchAdvancedEvent;
import org.kablink.teaming.gwt.client.event.SearchFindResultsEvent;
import org.kablink.teaming.gwt.client.event.SearchSavedEvent;
import org.kablink.teaming.gwt.client.event.SearchTagEvent;
import org.kablink.teaming.gwt.client.event.TeamingEvents;
import org.kablink.teaming.gwt.client.GwtFolder;
import org.kablink.teaming.gwt.client.GwtSearchCriteria;
import org.kablink.teaming.gwt.client.GwtTag;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingItem;
import org.kablink.teaming.gwt.client.GwtTeamingMainMenuImageBundle;
import org.kablink.teaming.gwt.client.GwtTeamingMessages;
import org.kablink.teaming.gwt.client.GwtUser;
import org.kablink.teaming.gwt.client.event.ChangeContextEvent;
import org.kablink.teaming.gwt.client.rpc.shared.GetBinderPermalinkCmd;
import org.kablink.teaming.gwt.client.rpc.shared.GetSavedSearchesCmd;
import org.kablink.teaming.gwt.client.rpc.shared.GetSavedSearchesRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.StringRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.util.OnSelectBinderInfo;
import org.kablink.teaming.gwt.client.util.OnSelectBinderInfo.Instigator;
import org.kablink.teaming.gwt.client.widgets.FindCtrl;
import org.kablink.teaming.gwt.client.widgets.FindCtrl.FindCtrlClient;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.Widget;


/**
 * Class used for the content of the additional search options.  
 * 
 * @author drfoster@novell.com
 */
public class SearchOptionsComposite extends Composite
	implements
	// Event handlers implemented by this class.
		SearchFindResultsEvent.Handler
{
	private FindCtrl m_finderControl;
	private FlowPanel m_mainPanel;
	private GwtTeamingMainMenuImageBundle m_images;
	private GwtTeamingMessages m_messages;
	private PopupPanel m_searchOptionsPopup;

	// The following are used as the ID's on the radio buttons.
	private final static String RB_PERSON = "personRadio";
	private final static String RB_PLACE  = "placeRadio";
	private final static String RB_TAG    = "tagRadio";

	// The following defines the TeamingEvents that are handled by
	// this class.  See EventHelper.registerEventHandlers() for how
	// this array is used.
	private TeamingEvents[] m_registeredEvents = new TeamingEvents[] {
		// Search events.
		TeamingEvents.SEARCH_FIND_RESULTS,
	};
	
	/*
	 * Inner class used to wrap the finder radio buttons.
	 */
	private class FinderRB extends RadioButton {
		/*
		 * Class constructor.
		 */
		FinderRB(String id, String label, final GwtSearchCriteria.SearchType searchType, boolean checked) {
			super("finders", label);
			addClickHandler(new ClickHandler() {
				public void onClick(ClickEvent event) {
					m_finderControl.hideSearchResults();
					m_finderControl.setInitialSearchString("");
					m_finderControl.setSearchType(searchType);
					setFocusOnSearch();
				}
			});
			getElement().setId(id);
			addStyleName("searchOptionsDlg_FindersRadio mediumtext");
			setValue(checked);
		}
		
		FinderRB(String id, String label, final GwtSearchCriteria.SearchType searchType) {
			this(id, label, searchType, false);
		}
	}

	/*
	 * Inner class used to process selections in the saved searches
	 * list box.
	 */
	private class SavedSearchSelected implements ChangeHandler {
		public void onChange(ChangeEvent event) {
			// Is other than the select a search item selected?
			ListBox ssiList = ((ListBox) event.getSource());
			int ssi = ssiList.getSelectedIndex();
			if (0 < ssi) {
				// Yes!  Hide the search options popup...
				m_searchOptionsPopup.hide();
				
				// ...and perform the search.
				String searchName = ssiList.getItemText(ssi);
				GwtTeaming.fireEvent(new SearchSavedEvent(searchName));
			}
		}
	}
	
	/*
	 * Class constructor.
	 * 
	 * Note that the class constructor is private to facilitate code
	 * splitting.  All instantiations of this object must be done
	 * through its createAsync().
	 */
	private SearchOptionsComposite(PopupPanel searchOptionsPopup) {
		// Store the parameter...
		m_searchOptionsPopup = searchOptionsPopup;

		// ...register the events to be handled by this class...
		EventHelper.registerEventHandlers(
			GwtTeaming.getEventBus(),
			m_registeredEvents,
			this);
		
		// ...and initialize everything else.
		m_images = GwtTeaming.getMainMenuImageBundle();
		m_messages = GwtTeaming.getMessages();

		// Create the composite's content.
		m_mainPanel = new FlowPanel();
		m_mainPanel.addStyleName("searchOptionsDlg_Content");
		addHeader();
		addContent();
		
		// All composites must call initWidget() in their constructors.
		initWidget(m_mainPanel);
	}

	/*
	 * Adds the advanced search push button to the main content.
	 */
	private void addAdvancedSearch() {
		// Create the button label...
		InlineLabel asLabel = new InlineLabel(m_messages.mainMenuSearchOptionsAdvancedSearch());
		asLabel.addStyleName("searchOptionsDlg_AdvancedSearchLabel");

		// ...create the button Anchor...
		Anchor asAnchor = new Anchor();
		asAnchor.addStyleName("searchOptionsDlg_AdvancedSearchA");
		asAnchor.getElement().appendChild(asLabel.getElement());
		asAnchor.setTitle(m_messages.mainMenuSearchOptionsAdvancedSearch());
		asAnchor.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				// Hide the search options popup and run the advanced
				// search dialog.
				m_searchOptionsPopup.hide();
				SearchAdvancedEvent.fireOne();
			}
		});

		// ..and tie everything together.
		FlowPanel asPanel = new FlowPanel();
		asPanel.addStyleName("searchOptionsDlg_AdvancedSearchPanel margintop3");
		asPanel.add(asAnchor);
		m_mainPanel.add(asPanel);
	}	
	
	/*
	 * Adds the content to the main panel.
	 */
	private void addContent() {
		addFinders();
		
		// After asynchronously loading and adding the FindCtrl,
		// addFinders() will add the saved searches and advanced
		// search widgets.
	}

	/*
	 * Adds the finder widgets to the main content.
	 */
	private void addFinders() {
		// Create a panel to hold the finder radio buttons...
		FlowPanel rbPanel = new FlowPanel();
		rbPanel.addStyleName("searchOptionsDlg_FindersRadioPanel");

		// create the radio buttons themselves...
		FinderRB rb;
		rb = new FinderRB(RB_PERSON, m_messages.mainMenuSearchOptionsPeople(), GwtSearchCriteria.SearchType.PERSON, true); rbPanel.add(rb);
		rb = new FinderRB(RB_PLACE,  m_messages.mainMenuSearchOptionsPlaces(), GwtSearchCriteria.SearchType.PLACES);       rbPanel.add(rb);
		rb = new FinderRB(RB_TAG,    m_messages.mainMenuSearchOptionsTags(),   GwtSearchCriteria.SearchType.TAG);          rbPanel.add(rb);

		// ...add the radio button panel to the main content...
		m_mainPanel.add(rbPanel);

		// ...and add a finder widget for it.
		FindCtrl.createAsync(this, GwtSearchCriteria.SearchType.PERSON, 30, new FindCtrlClient() {			
			@Override
			public void onUnavailable() {
				// Nothing to do.  Error handled in
				// asynchronous provider.
			}
			
			@Override
			public void onSuccess(FindCtrl findCtrl) {
				m_finderControl = findCtrl;
				m_finderControl.addStyleName("searchOptionsDlg_FinderWidget margintop2 marginbottom2");
				m_mainPanel.add(m_finderControl);
				
				addSavedSearches();
				addAdvancedSearch();
				
				setFocusOnSearch();
			}
		});
	}
	
	
	/*
	 * Adds the header to the main panel.
	 */
	private void addHeader() {
		// Create a panel for the close push button...
		FlowPanel closePBPanel = new FlowPanel();
		
		// ...create the Image for it...
		Image closePBImg = new Image(m_images.closeX());
		closePBImg.addStyleName("searchOptionsDlg_CloseImg");
		closePBImg.setTitle(m_messages.mainMenuSearchOptionsCloseAlt());
		
		// ...create the Anchor for it...
		Anchor closePBAnchor = new Anchor();
		closePBAnchor.addStyleName("searchOptionsDlg_CloseA");
		closePBAnchor.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				m_searchOptionsPopup.hide();
			}
		});
		
		// ...tie close push button together...
		closePBAnchor.getElement().appendChild(closePBImg.getElement());
		closePBPanel.add(closePBAnchor);
		m_mainPanel.add(closePBPanel);
		
		// ...and add the header text. 
		FlowPanel headerPanel = new FlowPanel();
		headerPanel.addStyleName("searchOptionsDlg_Header");
		headerPanel.add(new InlineLabel(m_messages.mainMenuSearchOptionsHeader()));
		m_mainPanel.add(headerPanel);
	}
	
	/*
	 * Adds the saved search widgets to the main content.
	 */
	private void addSavedSearches() {
		// Create a label for the save search widgets...
		FlowPanel ssLabelPanel = new FlowPanel();
		ssLabelPanel.addStyleName("searchOptionsDlg_SavedSearchesLabelPanel margintop3");
		InlineLabel ssLabel = new InlineLabel(m_messages.mainMenuSearchOptionsSavedSearches());
		ssLabel.addStyleName("searchOptionsDlg_SavedSearchesLabel");
		ssLabelPanel.add(ssLabel);
		m_mainPanel.add(ssLabelPanel);

		// ...create the saved searches list box...
		FlowPanel ssListPanel = new FlowPanel();
		ssListPanel.addStyleName("searchOptionsDlg_SavedSearchesSelectPanel margintop1");
		ListBox ssList = new ListBox();
		ssListPanel.add(ssList);
		m_mainPanel.add(ssListPanel);

		// ...and populate it.
		populateSavedSearchList(ssList);
	}

	/*
	 * Loads a binder into the context pane.
	 */
	private void loadBinder(final String binderId) {
		GetBinderPermalinkCmd cmd;
		
		cmd = new GetBinderPermalinkCmd( binderId );
		GwtClientHelper.executeCommand( cmd, new AsyncCallback<VibeRpcResponse>() {
			public void onFailure(Throwable t) {
				GwtClientHelper.handleGwtRPCFailure(
					t,
					GwtTeaming.getMessages().rpcFailure_GetBinderPermalink(),
					binderId);
			}
			
			public void onSuccess(final VibeRpcResponse response ) {
				String binderPermalink;
				StringRpcResponseData responseData;
				
				responseData = (StringRpcResponseData) response.getResponseData();
				binderPermalink = responseData.getStringValue();
				
				OnSelectBinderInfo osbInfo = new OnSelectBinderInfo(binderId, binderPermalink, false, Instigator.SEARCH_SELECT);
				GwtTeaming.fireEvent(new ChangeContextEvent(osbInfo));
			}// end onSuccess()
		});
	}

	/**
	 * Handles SearchFindResultsEvent's received by this class.
	 * 
	 * Implements the SearchFindResultsEvent.Handler.onSearchFindResults() method.
	 * 
	 * @param event
	 */
	@Override
	public void onSearchFindResults(SearchFindResultsEvent event) {
		// If the find results aren't for the search options
		// composite...
		if (!(((Widget) event.getSource()).equals(this))) {
			// ...ignore the event.
			return;
		}
		
		// Yes!  Hide the search results list.
		m_finderControl.hideSearchResults();

		// Is the search result a GwtFolder?
		GwtTeamingItem obj = event.getSearchResults();
		if (obj instanceof GwtFolder) {
			// Yes!  Hide the search options popup and switch to
			// that folder.
			m_searchOptionsPopup.hide();
			loadBinder(((GwtFolder) obj).getFolderId());
			return;
		}
		
		// No, it's not a GwtFolder!  Is it a GwtUser?
		else if (obj instanceof GwtUser) {
			// Yes!  Hide the search options popup.
			m_searchOptionsPopup.hide();
			
			// Does the user have a workspace we can access?
			GwtUser user = ((GwtUser) obj);
			String userWSId = user.getWorkspaceId();
			if (GwtClientHelper.hasString(userWSId)) {
				// Yes!  Switch to it.
				loadBinder(userWSId);
			}
			else {
				// No, the user doesn't have a workspace we can
				// access!  Activate their permalink instead.
				GwtTeaming.fireEvent(
					new GotoPermalinkUrlEvent(
						user.getViewWorkspaceUrl()));
			}
			return;
		}
		
		// No, it's not a GwtUser either!  Is it a GwtTag?
		else if (obj instanceof GwtTag) {
			// Yes!  Hide the search options popup and perform a
			// search for the tag.
			m_searchOptionsPopup.hide();
			GwtTeaming.fireEvent(new SearchTagEvent(((GwtTag) obj).getTagName()));
		}
		
		else
		{
			// No, it's not a GwtTag either!  Whatever it is, we're
			// not built to handle it.
			Window.alert(m_messages.mainMenuSearchOptionsInvalidResult());
		}
	}
	
	/*
	 * Called to use GWT RPC to populate the saved searches list box.
	 */
	private void populateSavedSearchList(final ListBox ssList) {
		GetSavedSearchesCmd cmd;
		
		// Add a no saved searches item and disable the widget.  If we
		// find some from the RPC call, we'll remove this and re-enable
		// it.
		ssList.addItem(m_messages.mainMenuSearchOptionsNoSavedSearches(),"noSavedSearches");
		ssList.setEnabled(false);
		
		// Does the user have any saved searches defined?
		cmd = new GetSavedSearchesCmd();
		GwtClientHelper.executeCommand( cmd, new AsyncCallback<VibeRpcResponse>() {
			public void onFailure(Throwable t) {
				GwtClientHelper.handleGwtRPCFailure(
					t,
					m_messages.rpcFailure_GetSavedSearches());
			}
			public void onSuccess(VibeRpcResponse response)  {
				List<SavedSearchInfo> ssiList;
				GetSavedSearchesRpcResponseData responseData;
				
				responseData = (GetSavedSearchesRpcResponseData) response.getResponseData();
				ssiList = responseData.getSavedSearches();
				
				int count = ((null == ssiList) ? 0 : ssiList.size());
				if (0 < count) {
					// Yes!  Remove the no saved searches item and
					// re-enable the list box...
					ssList.removeItem(0);
					ssList.setEnabled(true);
					ssList.addItem(m_messages.mainMenuSearchOptionsSelectASearch(), "selectASearch");
					
					// ...scan the saved searches...
					for (Iterator<SavedSearchInfo> ssiIT = ssiList.iterator(); ssiIT.hasNext(); ) {
						// ...adding an item for each to the list
						// ...box...
						String searchName = ssiIT.next().getName();
						ssList.addItem(searchName, searchName);
					}
					
					// ...and add a change handler to handle the user
					// ...selecting one.
					ssList.addChangeHandler(new SavedSearchSelected());
				}
			}
		});
	}
	
	/*
	 * Moves the input focus to the people, places and tags INPUT
	 * widget.
	 */
	private void setFocusOnSearch() {
		ScheduledCommand cmd = new ScheduledCommand() {
			public void execute() {
    			m_finderControl.getFocusWidget().setFocus(true);
			}
		};
		Scheduler.get().scheduleDeferred(cmd);
	}
	
	/**
	 * Callback interface to interact with the search options composite
	 * asynchronously after it loads. 
	 */
	public interface SearchOptionsCompositeClient {
		void onSuccess(SearchOptionsComposite soc);
		void onUnavailable();
	}

	/**
	 * Loads the SearchOptionsComposite split point and returns an
	 * instance of it via the callback.
	 * 
	 * @param searchOptionsPopup
	 * @param actonTrigger
	 * @param socClient
	 */
	public static void createAsync(final PopupPanel searchOptionsPopup, final SearchOptionsCompositeClient socClient) {
		// The SearchOptionsComposite is dependent on the FindCtrl.
		// Make sure it has been fetched before trying to use it.
		FindCtrl.prefetch(new FindCtrl.FindCtrlClient() {			
			@Override
			public void onUnavailable() {
				// Nothing to do.  Error handled in
				// asynchronous provider.
				socClient.onUnavailable();
			}
			
			@Override
			public void onSuccess(FindCtrl findCtrl) {
				GWT.runAsync(SearchOptionsComposite.class, new RunAsyncCallback()
				{			
					@Override
					public void onSuccess() {
						SearchOptionsComposite soc = new SearchOptionsComposite(searchOptionsPopup);
						socClient.onSuccess(soc);
					}
					
					@Override
					public void onFailure(Throwable reason) {
						Window.alert( GwtTeaming.getMessages().codeSplitFailure_SearchOptionsComposite() );
						socClient.onUnavailable();
					}
				});
			}
		});
	}
}
