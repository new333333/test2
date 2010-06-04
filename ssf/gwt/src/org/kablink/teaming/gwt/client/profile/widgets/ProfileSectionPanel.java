package org.kablink.teaming.gwt.client.profile.widgets;

import java.util.Iterator;

import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingMessages;
import org.kablink.teaming.gwt.client.profile.ProfileRequestInfo;
import org.kablink.teaming.gwt.client.service.GwtRpcServiceAsync;
import org.kablink.teaming.gwt.client.util.ActionTrigger;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.WidgetCollection;

public abstract class ProfileSectionPanel extends FlowPanel {

	protected ProfileRequestInfo 		profileRequestInfo; // Initial values passed 
	protected GwtRpcServiceAsync	rpcService;			//
	protected GwtTeamingMessages	messages; 			// The menu's messages.
	protected ActionTrigger 		actionTrigger;
	protected Label 				headingLabel;
	protected String 				sectionTitle;
	protected boolean               selectedMore;
	private Image 					expandImage;
	private Image					collapseImage;
	private FlowPanel 				titlePanel;
	private SideBarAnchor 			sbA;
	
	public ProfileSectionPanel(ProfileRequestInfo requestInfo, String title, ActionTrigger trigger) {

		rpcService			= GwtTeaming.getRpcService();
		messages			= GwtTeaming.getMessages();
		profileRequestInfo 	= requestInfo;
		actionTrigger 		= trigger;
		sectionTitle        = title;
		
		createTitleAreaWidget();
	}
	
	public Label getHeadingLabel() {
		return headingLabel;
	}
	
	protected void addContentWidget(Widget w, boolean visible) {
		add(w);
		w.setVisible(visible);
	}

	protected void showExpandButton() {
		collapseImage.setVisible(false);
		expandImage.setVisible(true);
	}

	protected void showCollapseButton() {
		expandImage.setVisible(false);
		collapseImage.setVisible(true);
	}
	
	protected void createTitleAreaWidget(){
		//add a title to the section
		if(sectionTitle !=null) {
			titlePanel = new FlowPanel();
			titlePanel.addStyleName("profileSectionTitle");
			add(titlePanel);
			
			headingLabel = new Label(sectionTitle);
			headingLabel.addStyleName("sideBarTitle");
			titlePanel.add(headingLabel);
			
			expandImage = new Image(GwtTeaming.getImageBundle().expand16());
			expandImage.addStyleName("sideBarTitle");
			expandImage.setTitle("Expand");
			expandImage.setVisible(false);
			expandImage.addClickHandler(new SelectedMoreClickHandler());
			titlePanel.add(expandImage);

			collapseImage = new Image(GwtTeaming.getImageBundle().collapse16());
			collapseImage.addStyleName("sideBarTitle");
			collapseImage.setTitle("Collapse");
			collapseImage.setVisible(false);
			collapseImage.addClickHandler(new SelectedMoreClickHandler());
			titlePanel.add(collapseImage);
		}
	}
	
	/**
	 * Clear all of the widgets expect the title area widget
	 */
	protected void clearWidgets() {
		WidgetCollection widgets = this.getChildren();
		int count = (widgets != null) ? widgets.size() : 0;
		
		for(int index = 1; index < count; index++ ){
			this.remove(index);
		}
	}
	
	/*
	 * Inner class that handles clicks on the more link
	 */
	private class SelectedMoreClickHandler implements ClickHandler {
		/**
		 * Called when the user clicks on a team.
		 * @param event
		 */
		public void onClick(ClickEvent event) {
			if(selectedMore) {
				selectedMore = false;
				collapse();
			} else {
				selectedMore = true;
				expand();
			}
		}
	}
	
	protected void expand() {
		WidgetCollection widgets = getChildren();
		Iterator<Widget> wi = widgets.iterator();
		
		for(;wi.hasNext();){
			Widget w = wi.next();
			w.setVisible(true);
		}
		
		showCollapseButton();
	}
	protected void collapse() {
		WidgetCollection widgets = getChildren();
		Iterator<Widget> wi = widgets.iterator();
		
		int count = 0;
		for(;wi.hasNext();){
			Widget w = wi.next();
			if(count < 5){
				w.setVisible(true);
			} else {
				w.setVisible(false);
			}
			count++;
		}
		
		showExpandButton();
	}
}