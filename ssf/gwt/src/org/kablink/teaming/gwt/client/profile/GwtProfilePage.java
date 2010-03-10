package org.kablink.teaming.gwt.client.profile;

import org.kablink.teaming.gwt.client.TeamingAction;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;

public class GwtProfilePage extends Composite
{
	private ProfileRequestInfo profileRequestInfo = null;
	
	public GwtProfilePage() {
		FlowPanel profilePanel;
		
		profilePanel = new FlowPanel();
		//profilePanel.addStyleName("mainProfilePanel");
		
		// Get information about the request we are dealing with.
		profileRequestInfo = getProfileRequestInfo();

		Label label = new Label("This is the GWT Profile Page: ");
		profilePanel.add(label);
		
		String url = profileRequestInfo.getAdaptedUrl();
		Label urlLabel = new Label("This is the URL: "+url);
		profilePanel.add(urlLabel);
		
		String binderId = profileRequestInfo.getBinderId();
		Label binderLabel = new Label("This is the BinderId: "+binderId);
		profilePanel.add(binderLabel);
		
		// All composites must call initWidget() in their constructors.
		initWidget( profilePanel );
	}

	/**
	 * Use JSNI to grab the JavaScript object that holds the information about the request dealing with.
	 */
	private native ProfileRequestInfo getProfileRequestInfo() /*-{
		// Return a reference to the JavaScript variable called, m_requestInfo.
		return $wnd.profileRequestInfo;
	}-*/;
}
