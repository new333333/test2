/**
 * Copyright (c) 1998-2015 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2015 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2015 Novell, Inc. All Rights Reserved.
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
package org.kablink.teaming.gwt.client.profile.widgets;

import java.util.Date;

import org.kablink.teaming.gwt.client.GwtTeamingException.ExceptionType;
import org.kablink.teaming.gwt.client.event.TeamingEvents;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingException;
import org.kablink.teaming.gwt.client.presence.PresenceControl;
import org.kablink.teaming.gwt.client.profile.UserStatus;
import org.kablink.teaming.gwt.client.rpc.shared.GetUserStatusCmd;
import org.kablink.teaming.gwt.client.rpc.shared.SaveUserStatusCmd;
import org.kablink.teaming.gwt.client.rpc.shared.SaveUserStatusRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Widget;

/**
 * ?
 * 
 * @author ?
 */
public class UserStatusControl extends Composite implements Event.NativePreviewHandler {
	private final static int LINES = 3;
	private final static int ONE_LINE = 1;
	private final static int MAX_CHARS = 255;
	
	private TextArea input;
	private HTML statusText;
	private Button shareButton;
	private InlineLabel timeLabel;
	private Anchor clear;
	private InlineLabel textAmount; 
	private int totalChar = 0;
	private InlineLabel limitExceeded;
	private boolean savingUserStatusInProgress = false;

	/*
	 * Note that the class constructor is private to facilitate code
	 * splitting.  All instantiations of this object must be done
	 * through its createAsync().
	 */
	private UserStatusControl () {

		String userStatus = "";
		
		FlowPanel mainPanel = new FlowPanel();
		mainPanel.setStyleName("user_status");
		
		PresenceControl presence = new PresenceControl(null, getBinderId(), true, true, true);
		mainPanel.add(presence);

		statusText = new HTML();
		statusText.setText(userStatus);
		statusText.setWordWrap(true);
		statusText.setStyleName("user_status_text");
		if(GwtClientHelper.jsIsIE()){
			statusText.addStyleName("user_status_textIE");
		}
		mainPanel.add(statusText);
		
		timeLabel = new InlineLabel();
		timeLabel.setStyleName("user_status_time");
		timeLabel.addStyleName("marginleft1");
		mainPanel.add(timeLabel);
		
		clear = new Anchor(GwtTeaming.getMessages().clearStatus());
		clear.setTitle(GwtTeaming.getMessages().clearCurrentStatus());
		clear.setStyleName("clearStatus");
		mainPanel.add(clear);

		//listen for mouse clicks to determine if need to clear the statusText
		clear.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {

				//update the status to the given user
				setUserStatus("");
			}});
		
		FlowPanel updatePanel = new FlowPanel();
		updatePanel.setStyleName("user_status_update");

		//Create the input area for the status
		input = new TextArea();
		input.setVisibleLines(ONE_LINE);
		input.setText(GwtTeaming.getMessages().statusMessage());
		input.setStyleName("user_status_input");
		if(GwtClientHelper.jsIsIE()){
			input.addStyleName("user_status_inputIE");
		}
		
		updatePanel.add(input);
		
		mainPanel.add(updatePanel);
		
		FlowPanel infoPanel = new FlowPanel();
		infoPanel.addStyleName("user_status_info");
		
		textAmount = new InlineLabel();
		textAmount.setStyleName("user_status_textAmount");
		infoPanel.add(textAmount);
		
		limitExceeded = new InlineLabel();
		limitExceeded.setStyleName("user_status_limitExceeded");
		infoPanel.add(limitExceeded);
		
		//Share your status button
		shareButton = new Button(GwtTeaming.getMessages().shareStatus());
		shareButton.setTitle(GwtTeaming.getMessages().shareStatus());
		shareButton.addStyleName("shareButton");
		shareButton.setVisible(false);
		
		infoPanel.add(shareButton);
		
		mainPanel.add(infoPanel);
		
		//Add Mouse click listener to share button
		shareButton.addClickHandler(new ClickHandler(){

			@Override
			public void onClick(ClickEvent event) {
				if(!input.getText().equals("") && !input.getText().equals(GwtTeaming.getMessages().statusMessage())){
						
					final String status = input.getText();

					//update the status to the given user
					setUserStatus(status);
				}
			}});
		
		//add a key listener
		input.addKeyUpHandler( new KeyUpHandler() {
			@Override
			public void onKeyUp(KeyUpEvent event) {
				
				textAmount.setVisible(true);
				totalChar = input.getValue().length();
				if(totalChar == MAX_CHARS){
	        		textAmount.setVisible(true);
					textAmount.setText("0");

					limitExceeded.setVisible(false);
	        		limitExceeded.setText("");
				} else if(totalChar > MAX_CHARS) {
	        		limitExceeded.setVisible(true);
	        		limitExceeded.setText(""+(MAX_CHARS - totalChar));

	        		textAmount.setVisible(false);
					textAmount.setText("");
	        	} else {
	        		textAmount.setVisible(true);
					textAmount.setText(""+(MAX_CHARS - totalChar));
	        		
	        		limitExceeded.setVisible(false);
	        		limitExceeded.setText("");
	        	}
			}});
		
		// Register a preview-event handler.  We do this so we can see the mouse-down event
		// in and out side of the widget.
		Event.addNativePreviewHandler( this );
		
		// check if the user is owns the current workspace
		showInputsControls();
		
		//get the status of the current user
		getUserStatus();

		//register
		initialize();
		
		initWidget(mainPanel);
	}
	
	private void initialize() {
		// Nothing to do.
	}

	private void setUserStatus(final String status) {
		
		if( savingUserStatusInProgress )
			return;
		
		AsyncCallback<VibeRpcResponse> rpcCallback = new AsyncCallback<VibeRpcResponse>(){

			@Override
			public void onFailure(Throwable t) {
				GwtClientHelper.handleGwtRPCFailure(
					t,
					GwtTeaming.getMessages().rpcFailure_SetStatus(),
					getBinderId());
				
				savingUserStatusInProgress = false;
			}

			@Override
			public void onSuccess( VibeRpcResponse response ) {				
				//Get the text from the input widget and set the status text field
				statusText.setText(status);
				setTime(new Date(), new Date());
								
				//clear the input field once the statusText has been populated...
				input.setText("");
				//reset the char count
				totalChar = 0;
				//set visibility to false
				textAmount.setVisible(false);
				
				if(!status.equals("")) {
					//Set the visibility of the status text, time and clear link
					showStatus(true);
				} else {
					showStatus(false);
				}

				// In saving the status, did we create a new mini blog
				// folder that we need to refresh things for?
				SaveUserStatusRpcResponseData responseData = ((SaveUserStatusRpcResponseData) response.getResponseData());
				if (responseData.isNewMiniBlogFolder()) {
					// Yes!  Force the sidebar to refresh.
					GwtClientHelper.jsFireVibeEventOnMainEventBus(TeamingEvents.REFRESH_SIDEBAR_TREE);
				}
				
				savingUserStatusInProgress = false;
			}
			
		};
		
		
		if(status.length() > MAX_CHARS) {
			Window.alert(GwtTeaming.getMessages().exceededError());
			return;
		}
		
		// Issue an ajax request to save the user status.
		{
			SaveUserStatusCmd cmd;
			
			savingUserStatusInProgress = true;
			
			// Issue an ajax request to save the user status to the db.  rpcCallback will
			// be called when we get the response back.
			cmd = new SaveUserStatusCmd( status );
			GwtClientHelper.executeCommand( cmd, rpcCallback );
		}
	}
	
	
	private void getUserStatus() {
		AsyncCallback<VibeRpcResponse> rpcCallback = new AsyncCallback<VibeRpcResponse>(){
			@Override
			public void onFailure(Throwable t) {
				if ((t instanceof GwtTeamingException) && ExceptionType.ACCESS_CONTROL_EXCEPTION.equals(((GwtTeamingException) t).getExceptionType()))
				     GwtClientHelper.deferredAlert(         GwtTeaming.getMessages().qViewErrorNoRights()                 );
				else GwtClientHelper.handleGwtRPCFailure(t, GwtTeaming.getMessages().rpcFailure_GetStatus(), getBinderId());
			}

			@Override
			public void onSuccess( VibeRpcResponse response ) {
				UserStatus result = null;
				
				if ( response.getResponseData() != null )
					result = (UserStatus) response.getResponseData();
				
				if(result != null) {
					
					String description = result.getStatus();
					if(description != null && !description.equals("")){
						statusText.setText(description);
						//show time
						setTime(result.getModifyDate(), result.getCurrentDate());
						showStatus(true);
					} else {
						//Set the visibility of the status text, time and clear link
						showStatus(false);
					}
					
				}
			}

			
		};
		
		// Issue an ajax request to save the branding data.
		{
			GetUserStatusCmd cmd;
			
			// Issue an ajax request to save the user status to the db.  rpcCallback will
			// be called when we get the response back.
			cmd = new GetUserStatusCmd( null, getBinderId() );
			GwtClientHelper.executeCommand( cmd, rpcCallback );
		}

		
	}

	/**
	 * Using this onPreviewNativeEvent to check if the mouse click is in the input widget 
	 */
	@Override
	public void onPreviewNativeEvent(NativePreviewEvent previewEvent) {


		int eventType = previewEvent.getTypeInt();
		
		// We are only interested in mouse-down events.
		if ( eventType != Event.ONMOUSEDOWN )
			return;
		
		NativeEvent nativeEvent = previewEvent.getNativeEvent();
		//EventTarget target = event.getEventTarget();
		
		if ( isMouseOver(shareButton, nativeEvent.getClientX(), nativeEvent.getClientY())) {
			return;
		}
		
		if ( !isMouseOver(input, nativeEvent.getClientX(), nativeEvent.getClientY() ) ) {
			if(input.getVisibleLines() == LINES) {

				if(!GwtClientHelper.hasString(input.getText())) {
					input.setVisibleLines(ONE_LINE);
					input.setText(GwtTeaming.getMessages().statusMessage());
					//set visibility to false
					textAmount.setVisible(false);
					shareButton.setVisible(false);
				}
				
				return;
			}
		} else  { 
			if(input.getVisibleLines() != LINES) {
				input.setVisibleLines(LINES);
				if(input.getText().equals(GwtTeaming.getMessages().statusMessage())) {
					input.setText("");
					totalChar = 0;
				}
				shareButton.setVisible(true);
			} 
		}
	}
	
	/**
	 * Determine if the given coordinates are over this control.
	 */
	public boolean isMouseOver( Widget widget, int mouseX, int mouseY )
	{
		int left;
		int top;
		int width;
		int height;
		
		// Get the position and dimensions of this control.
		left = widget.getAbsoluteLeft();
		top = widget.getAbsoluteTop();
		height = widget.getOffsetHeight();
		width = widget.getOffsetWidth();
		
		// Factor scrolling into the mouse position.
		mouseY += Window.getScrollTop();
		mouseX += Window.getScrollLeft();
		
		// Is the mouse over this control?
		if ( mouseY >= top && mouseY <= (top + height) && mouseX >= left && (mouseX <= left + width) )
			return true;
		
		return false;
	}// end isMouseOver()
	
	/**
	 * Set the visibility of the status text, time and clear link
	 * @param visible
	 */
	private void showStatus(boolean visible) {

		statusText.setVisible(visible);
		timeLabel.setVisible(visible);
		
		//Only show if they are the owner of this status message
		if(isOwner()) {
			clear.setVisible(visible);
		}
	}
	
	/**
	 * Set the visibility of the input and clear buttons if the currentUser owns the workspace they are looking at.
	 * @param visible
	 */
	private void showInputsControls() {
		
		boolean visible = isOwner();

		clear.setVisible(visible);
		input.setVisible(visible);
		textAmount.setVisible(visible);
	}
	
	/**
	 * Is the workspace being referenced owned by the current user
	 * @return
	 */
	private boolean isOwner() {
		boolean isOwner = false;
		
		if(getCurrentUserWorkspaceId() == getBinderId()) {
			isOwner = true;
		} 

		return isOwner;
	}
	
    /**
     * This is the binderId of binder being referenced, may not be the loggedIn user
     * @return
     */
    protected static native String getBinderId() /*-{
		return $wnd.binderId;
    }-*/;
    
    /**
     * This is the workspace id of the current user
     */
    protected static native String getCurrentUserWorkspaceId() /*-{
		return $wnd.currentUserWorkspaceId;
	}-*/;
    
    
    /**
     * Set the time
     * @param modifyDate
     * @param date 
     */
    @SuppressWarnings("deprecation")
	private void setTime(Date modifyDate, Date currentDate) {
    	
    	long sec = 1000;
    	long min = 60 * sec;
    	long hour = 60 * min;
    	long day = 24 * hour;
    	
		long currentTime = currentDate.getTime();
		long modifyTime = modifyDate.getTime();
		long diffTime = currentTime - modifyTime;
		
		if( diffTime == 0 ) {
			this.timeLabel.setText(GwtTeaming.getMessages().now());
		} 		//if less then a min, then is now
		else if( diffTime < min ) {
			long seconds = diffTime / sec;
			if(seconds == 1) {
				this.timeLabel.setText(GwtTeaming.getMessages().oneSecondAgo());
			} else {
				this.timeLabel.setText(GwtTeaming.getMessages().secondsAgo(seconds));
			}
		} //if less than an hour, then how many minutes ago
		else if ( diffTime < hour ) {
			long minutes = diffTime / min;
			if(minutes == 1) {
				this.timeLabel.setText(GwtTeaming.getMessages().oneMinuteAgo());
			} else {
				this.timeLabel.setText(GwtTeaming.getMessages().minutesAgo(minutes));
			}
		} // if less than a day, then how many hours ago 
		else if ( diffTime < day ) {
			long hours = diffTime / hour;
			if(hours == 1) {
				this.timeLabel.setText(GwtTeaming.getMessages().oneHourAgo());
			} else {
				this.timeLabel.setText(GwtTeaming.getMessages().hoursAgo(hours));
			}
		} // if greater than a day, how many days ago 
		else if ( diffTime >= day ) {
			long days = diffTime / day;
			
			//if less than 30 days show the days otherwise show the actual date
			if(days < 30) {
				if(days == 1) {
					this.timeLabel.setText(GwtTeaming.getMessages().oneDayAgo());
					
				} else {
					this.timeLabel.setText(GwtTeaming.getMessages().daysAgo(days));
				}
			} else {
				String dateString = DateTimeFormat.getMediumDateTimeFormat().format(modifyDate);
				this.timeLabel.setText(dateString);
			}
		}
	}
	
	/**
	 * Callback interface to interact with the user status control
	 * asynchronously after it loads. 
	 */
	public interface UserStatusControlClient {
		void onSuccess(UserStatusControl usc);
		void onUnavailable();
	}

	/**
	 * Loads the UserStatusControl split point and returns an instance
	 * of it via the callback.
	 * 
	 * @param uscClient
	 */
	public static void createAsync(final UserStatusControlClient uscClient) {
		GWT.runAsync(UserStatusControl.class, new RunAsyncCallback() {			
			@Override
			public void onSuccess() {
				UserStatusControl usc = new UserStatusControl();
				uscClient.onSuccess(usc);
			}
			
			@Override
			public void onFailure(Throwable reason) {
				Window.alert(GwtTeaming.getMessages().codeSplitFailure_UserStatusControl());
				uscClient.onUnavailable();
			}
		});
	}
}
