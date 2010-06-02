package org.kablink.teaming.gwt.client.profile;

import java.util.List;

import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.mainmenu.SavedSearchInfo;
import org.kablink.teaming.gwt.client.util.ActionTrigger;
import org.kablink.teaming.gwt.client.util.TeamingAction;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Label;

public class ProfileSearchesSectionPanel extends ProfileSectionPanel {

	private final String IDBASE = "mySearches_";
	private List<SavedSearchInfo> ssList;
	
	/**
	 * Create the saved searches
	 * 
	 * @param profileRequestInfo
	 * @param title
	 * @param trigger
	 */
	public ProfileSearchesSectionPanel(ProfileRequestInfo profileRequestInfo, String title, ActionTrigger trigger) {
		super(profileRequestInfo, title, trigger);
		setStyleName("tracking-subhead");
		//populate the saved searches list
		populateSavedSearchList();
	}
	
	/*
	 * Called to use GWT RPC to populate the saved searches list box.
	 */
	private void populateSavedSearchList() {

		//ssList.addItem(GwtTeaming.getMessages().mainMenuSearchOptionsNoSavedSearches(),"noSavedSearches");
		//ssList.setEnabled(false);
		
		// Does the user have any saved searches defined?
		GwtTeaming.getRpcService().getSavedSearches(new AsyncCallback<List<SavedSearchInfo>>() {
			public void onFailure(Throwable t) {
				Window.alert(t.toString());
			}
			public void onSuccess(List<SavedSearchInfo> ssiList)  {

				ssList = ssiList;
				buildSavedSearchLinks();
			}
		});
	}

	
	private void buildSavedSearchLinks(){
		
		if(selectedMore) {
			clearWidgets();
		}
		
		SideBarAnchor sbA;

		int count = 0;
		// ...scan the saved searches...
		for (SavedSearchInfo savedSearch: ssList ) {

			// ...creating an item structure for each.
			String id = (IDBASE + savedSearch.getName());
			sbA = new SideBarAnchor(id, savedSearch.getName(), savedSearch.getName(), new SavedSearchesClickHandler(savedSearch));
			
			boolean visible = true;
			if(count > 3){
				visible = false;
				showExpandButton();
			}
			
			addContentWidget(sbA, visible);
			count++;
		}
		
		// If there weren't any teams...
		if (0 == count) {
			// ...put something in the menu that tells the user
			// ...that.
			Label content = new Label(messages.mainMenuSearchOptionsNoSavedSearches());
			//content.addStyle():
			addContentWidget(content, true);
			
			return;
		}
	}

	/*
	 * Inner class that handles clicks on individual teams.
	 */
	private class SavedSearchesClickHandler implements ClickHandler {
		private SavedSearchInfo savedSearch;	// The team clicked on.
	
		/**
		 * Class constructor.
		 * 
		 * @param myTeam
		 */
		SavedSearchesClickHandler(SavedSearchInfo s) {
			// Simply store the parameter.
			savedSearch = s;
		}
	
		/**
		 * Called when the user clicks on a team.
		 * 
		 * @param event
		 */
		public void onClick(ClickEvent event) {
	
			if(savedSearch.getName() != "") {
				String name = savedSearch.getName();
				actionTrigger.triggerAction(TeamingAction.SAVED_SEARCH, name);
			} 
		}
	}
}