package org.kablink.teaming.gwt.client.profile;

import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingException;
import org.kablink.teaming.gwt.client.GwtTeamingMessages;
import org.kablink.teaming.gwt.client.GwtTeamingException.ExceptionType;
import org.kablink.teaming.gwt.client.service.GwtRpcServiceAsync;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;

import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
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
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Widget;

public class UserStatusControl extends Composite implements Event.NativePreviewHandler {
	
	private final static int LINES = 3;
	private final static int ONE_LINE = 1;
	
	private TextArea input;
	private HTML statusText;
	private Button shareButton;
	private InlineLabel timeLabel;
	private Anchor clear; 

	public UserStatusControl () {
		
		String userStatus = "";

		FlowPanel mainPanel = new FlowPanel();
		mainPanel.setStyleName("user_status");
		
		statusText = new HTML();
		statusText.setText(userStatus);
		statusText.setWordWrap(true);
		statusText.setStyleName("user_status_text");
		mainPanel.add(statusText);
		
		timeLabel = new InlineLabel("1 hour ago");
		timeLabel.setStyleName("user_status_time");
		timeLabel.addStyleName("marginleft1");
		mainPanel.add(timeLabel);
		
		clear = new Anchor("clear");
		clear.setStyleName("clear_status");
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
		input.setText("What are you working on?");
		input.setStyleName("user_status_input");
		updatePanel.add(input);
		
		//Share your status button
		shareButton = new Button("Share");
		shareButton.addStyleName("alignBottom");
		
		updatePanel.add(shareButton);
		
		shareButton.addClickHandler(new ClickHandler(){

			public void onClick(ClickEvent event) {
				if(!input.getText().equals("") && !input.getText().equals("What are you working on?")){
						
						final String status = input.getText();

						//update the status to the given user
						setUserStatus(status);
				}
			}});
		
		mainPanel.add(updatePanel);
		
		//text info
		//Label textAmount = new Label("255");
		//mainPanel.add(textAmount);
				
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
				
				//clear the input field once the statusText has been populated...
				input.setText("");

				if(!status.equals("")) {
					//Set the visibility of the status text, time and clear link
					showStatus(true);
				} else {
					showStatus(false);
				}

			}
			
		};
		
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
				input.setVisibleLines(ONE_LINE);
				
				if(!GwtClientHelper.hasString(input.getText())) {
					input.setText("What are you working on?");
				}
				return;
			}
		} else  { 
			if(input.getVisibleLines() != LINES) {
				input.setVisibleLines(LINES);
				if(input.getText().equals("What are you working on?")) {
					input.setText("");
				}
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
		shareButton.setVisible(visible);
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
    
}
