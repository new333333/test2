package org.kablink.teaming.gwt.client.profile.widgets;

import org.kablink.teaming.gwt.client.EditCanceledHandler;
import org.kablink.teaming.gwt.client.EditSuccessfulHandler;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.profile.ProfileAttributeAttachment;
import org.kablink.teaming.gwt.client.profile.ProfileAttributeListElement;
import org.kablink.teaming.gwt.client.widgets.DlgBox;

import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Event.NativePreviewHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;

public class ModifyAvatarDlg extends DlgBox implements NativePreviewHandler  {

	ProfileAttributeListElement attrEle;
	
	public ModifyAvatarDlg(boolean autoHide, boolean modal, int pos, int pos2, ProfileAttributeListElement attrItem) {
		super(autoHide, modal, pos, pos2);
		
		this.attrEle = attrItem;
		
		createAllDlgContent("", null, null, null);
		
		// Register a preview-event handler.  We do this so we can see the mouse-down event
		// in and out side of the widget.
		Event.addNativePreviewHandler( this );
	}

	/**
	 * Create the header, content and footer for the dialog box.
	 */
	public void createAllDlgContent(String caption,
			EditSuccessfulHandler editSuccessfulHandler,// We will call this
														// handler when the user
														// presses the ok button
			EditCanceledHandler editCanceledHandler, // This gets called when
														// the user presses the
														// Cancel button
			Object properties) // Where properties used in the dialog are read
								// from and saved to.
	{
		FlowPanel panel;
		Panel content;
		Panel header;
		Panel footer;

		panel = new FlowPanel();
		panel.addStyleName("modifyAvatarDlg");

		// Add the header.
		header = createHeader(caption);
		panel.add(header);

		// Add the main content of the dialog box.
		content = createContent(properties);
		panel.add(content);

		// Create the footer.
		footer = createFooter();
		panel.add(footer);

		init(properties);

		// Initialize the handlers
		initHandlers(editSuccessfulHandler, editCanceledHandler);

		setWidget(panel);
	}// end createAllDlgContent()
	
	
	private void init(Object properties) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * Override the createHeader() method because we need to make it nicer.
	 */
	public Panel createHeader(String caption) {
		FlowPanel panel;

		panel = new FlowPanel();
		panel.addStyleName("teamingDlgBoxHeader");

		ProfileAttributeAttachment attach = (ProfileAttributeAttachment) attrEle.getValue();
		String stitle = attach.getName();
		if(stitle == null){
			stitle = ""; 
		}

		Label titleLabel = new Label(stitle, false);
		titleLabel.addStyleName("modifyAvatarTitle");
		panel.add(titleLabel);

		Anchor closeA = new Anchor();
		closeA.addStyleName("qViewClose");

		Image cancelImage = new Image(GwtTeaming.getMainMenuImageBundle()
				.closeCircle16());
		closeA.getElement().appendChild(cancelImage.getElement());

		// GwtTeaming.getMessages().cancel()
		closeA.setVisible(true);
		panel.add(closeA);

		closeA.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				hide();
			}
		});

		return panel;
	}// end createHeader()

	
	public Panel createContent(Object propertiesObj) {
		FlowPanel panel = new FlowPanel();
		panel.addStyleName("modifyAvatarDlgContent");
		
		FlowPanel photoPanel = new FlowPanel();
		panel.add(photoPanel);
		photoPanel.addStyleName("modifyAvatarDlgPhoto");
		
		Image img = new Image(attrEle.getValue().toString());
		photoPanel.add(img);
		
		FlowPanel content = new FlowPanel();
		panel.add(content);
		content.addStyleName("modifyAvatarDlgActions");
		
		Anchor setDefaultAvatar = new Anchor();
		content.add(setDefaultAvatar);
		setDefaultAvatar.addStyleName("qView-a");
		setDefaultAvatar.addStyleName("qView-action");
		setDefaultAvatar.setText("Set as default avatar");
		
		setDefaultAvatar.addClickHandler(new ClickHandler(){
			public void onClick(ClickEvent event) {
				
			}});
		
		
		Anchor removeAvatar = new Anchor();
		content.add(removeAvatar);
		removeAvatar.addStyleName("qView-a");
		removeAvatar.addStyleName("qView-action");
		removeAvatar.setText("Remove avatar");
		
		removeAvatar.addClickHandler(new ClickHandler(){
			public void onClick(ClickEvent event) {
				
			}});
		
		return panel;
	}
	
	/*
	 * Override the createFooter() method so we can control what buttons are in
	 * the footer.
	 */
	public Panel createFooter() {
		FlowPanel panel;

		panel = new FlowPanel();
		panel.addStyleName("qViewFooter");

		return panel;
	}// end createFooter()

	public Object getDataFromDlg() {
		return null;
	}

	public FocusWidget getFocusWidget() {
		return null;
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
		
		if ( isMouseOver(this, nativeEvent.getClientX(), nativeEvent.getClientY())) {
			return;
		} else {
			hide();
			return;
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
		left = widget.getAbsoluteLeft() - widget.getElement().getOwnerDocument().getScrollLeft();
		top = widget.getAbsoluteTop() - widget.getElement().getOwnerDocument().getScrollTop();
		height = widget.getOffsetHeight();
		width = widget.getOffsetWidth();
		
		// Is the mouse over this control?
		if ( mouseY >= top && mouseY <= (top + height) && mouseX >= left && (mouseX <= left + width) )
			return true;
		
		return false;
	}// end isMouseOver()
}
