package org.kablink.teaming.gwt.client.profile;

import java.util.Date;

import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingException;
import org.kablink.teaming.gwt.client.GwtTeamingMessages;
import org.kablink.teaming.gwt.client.GwtTeamingException.ExceptionType;
import org.kablink.teaming.gwt.client.presence.PresenceControl;
import org.kablink.teaming.gwt.client.service.GwtRpcServiceAsync;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;

import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Widget;

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

	public UserStatusControl () {
		
		String userStatus = "";

		FlowPanel mainPanel = new FlowPanel();
		mainPanel.setStyleName("user_status");
		
		PresenceControl presence = new PresenceControl(getBinderId(), true, true, true);
		mainPanel.add(presence);

		statusText = new HTML();
		statusText.setText(userStatus);
		statusText.setWordWrap(true);
		statusText.setStyleName("user_status_text");
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

			public void onClick(ClickEvent event) {
				if(!input.getText().equals("") && !input.getText().equals(GwtTeaming.getMessages().statusMessage())){
						
					final String status = input.getText();

					//update the status to the given user
					setUserStatus(status);
				}
			}});
		
		//add a key listener
		input.addKeyUpHandler( new KeyUpHandler() {
			public void onKeyUp(KeyUpEvent event) {
				
				textAmount.setVisible(true);
				totalChar = input.getValue().length();
				if(totalChar == MAX_CHARS){
	        		textAmount.setVisible(true);
					textAmount.setText("0");

					limitExceeded.setVisible(false);
	        		limitExceeded.setText("");;
				} else if(totalChar > MAX_CHARS) {
	        		limitExceeded.setVisible(true);
	        		limitExceeded.setText(""+(MAX_CHARS - totalChar));

	        		textAmount.setVisible(false);
					textAmount.setText("");
	        	} else {
	        		textAmount.setVisible(true);
					textAmount.setText(""+(MAX_CHARS - totalChar));
	        		
	        		limitExceeded.setVisible(false);
	        		limitExceeded.setText("");;
	        	}
			}});
		
		// Register a preview-event handler.  We do this so we can see the mouse-down event
		// in and out side of the widget.
		Event.addNativePreviewHandler( this );
		
		// check if the user is owns the current workspace
		showInputsControls();
		
		//get the status of the current user
		getUserStatus();
		
		initWidget(mainPanel);
	}

	private void setUserStatus(final String status) {
		
		AsyncCallback<Boolean> rpcCallback = new AsyncCallback<Boolean>(){

			public void onFailure(Throwable t) {
				String errMsg;
				String cause = "";
				GwtTeamingMessages messages;
				
				messages = GwtTeaming.getMessages();
				
				if ( t instanceof GwtTeamingException )
				{
					ExceptionType type;
				
					// Determine what kind of exception happened.
					type = ((GwtTeamingException)t).getExceptionType();
					if ( type == ExceptionType.ACCESS_CONTROL_EXCEPTION )
						cause = messages.errorAccessToFolderDenied( getBinderId() );
					else if ( type == ExceptionType.NO_BINDER_BY_THE_ID_EXCEPTION )
						cause = messages.errorFolderDoesNotExist( getBinderId() );
					else
						cause = messages.errorUnknownException();
				}
				else
				{
					cause = t.getLocalizedMessage();
					if ( cause == null )
						cause = t.toString();
				}
				
				errMsg = messages.getBrandingRPCFailed( cause );
				Window.alert( errMsg );
			}

			public void onSuccess(Boolean result) {

				//Get the text from the input widget and set the status text field
				statusText.setText(status);
				Date date = new Date();
				setTime(date);
				
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
			}
			
		};
		
		
		if(status.length() > MAX_CHARS) {
			Window.alert(GwtTeaming.getMessages().exceededError());
			return;
		}
		
		// Issue an ajax request to save the branding data.
		{
			GwtRpcServiceAsync rpcService;
			rpcService = GwtTeaming.getRpcService();
			
			// Issue an ajax request to save the user status to the db.  rpcCallback will
			// be called when we get the response back.
			rpcService.saveUserStatus(status, rpcCallback );
		}
	}
	
	
	private void getUserStatus() {
		
		AsyncCallback<UserStatus> rpcCallback = new AsyncCallback<UserStatus>(){

			public void onFailure(Throwable t) {
				String errMsg;
				String cause = "";
				GwtTeamingMessages messages;
				
				messages = GwtTeaming.getMessages();
				
				if ( t instanceof GwtTeamingException )
				{
					ExceptionType type;
				
					// Determine what kind of exception happened.
					type = ((GwtTeamingException)t).getExceptionType();
					if ( type == ExceptionType.ACCESS_CONTROL_EXCEPTION )
						cause = messages.errorAccessToFolderDenied( getBinderId() );
					else if ( type == ExceptionType.NO_BINDER_BY_THE_ID_EXCEPTION )
						cause = messages.errorFolderDoesNotExist( getBinderId() );
					else
						cause = messages.errorUnknownException();
				}
				else
				{
					cause = t.getLocalizedMessage();
					if ( cause == null )
						cause = t.toString();
				}
				
				errMsg = messages.getBrandingRPCFailed( cause );
				Window.alert( errMsg );
			}

			public void onSuccess(UserStatus result) {
				if(result != null) {
					
					String description = result.getStatus();
					if(description != null && !description.equals("")){
						statusText.setText(description);
						//show time
						setTime(result.getModifyDate());
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
			GwtRpcServiceAsync rpcService;
			
			rpcService = GwtTeaming.getRpcService();
			
			// Issue an ajax request to save the user status to the db.  rpcCallback will
			// be called when we get the response back.
			rpcService.getUserStatus(getBinderId(), rpcCallback );
		}

		
	}

	/**
	 * Using this onPreviewNativeEvent to check if the mouse click is in the input widget 
	 */
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
     */
    private void setTime(Date modifyDate) {
    	
    	long sec = 1000;
    	long min = 60 * sec;
    	long hour = 60 * min;
    	long day = 24 * hour;
    	
		Date currentDate = new Date();
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
}
