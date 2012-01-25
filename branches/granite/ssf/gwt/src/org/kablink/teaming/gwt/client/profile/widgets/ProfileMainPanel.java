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

package org.kablink.teaming.gwt.client.profile.widgets;

import java.util.List;

import org.kablink.teaming.gwt.client.EditSuccessfulHandler;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.event.TeamingEvents;
import org.kablink.teaming.gwt.client.event.TrackCurrentBinderEvent;
import org.kablink.teaming.gwt.client.event.UntrackCurrentBinderEvent;
import org.kablink.teaming.gwt.client.presence.PresenceControl;
import org.kablink.teaming.gwt.client.profile.DiskUsageInfo;
import org.kablink.teaming.gwt.client.profile.ProfileAttribute;
import org.kablink.teaming.gwt.client.profile.ProfileAttributeListElement;
import org.kablink.teaming.gwt.client.profile.ProfileCategory;
import org.kablink.teaming.gwt.client.profile.ProfileRequestInfo;
import org.kablink.teaming.gwt.client.profile.widgets.ProfileAttributeWidget.ProfileAttributeWidgetClient;
import org.kablink.teaming.gwt.client.rpc.shared.BooleanRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.GetDiskUsageInfoCmd;
import org.kablink.teaming.gwt.client.rpc.shared.GetProfileAvatarsCmd;
import org.kablink.teaming.gwt.client.rpc.shared.IsPersonTrackedCmd;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.InputElement;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteHandler;
import com.google.gwt.dom.client.NodeList;

@SuppressWarnings("unchecked")
public class ProfileMainPanel extends Composite implements SubmitCompleteHandler {

	ProfileRequestInfo profileRequestInfo;
	
	private FlexTable grid;
	private FlowPanel mainPanel;
	private FlowPanel titlePanel;
	private ProfileFollowingWidget followingAnchor;
	private boolean isEditable = false;
	private Anchor edit;
	private ProfileAvatarArea profileAvatarArea;
	private EditSuccessfulHandler editAvatarSuccessHandler;

	private FileUpload fileUpload;
	private Anchor uploadBtn;
	private Anchor delete;
	private FormPanel formPanel;
	private GwtProfilePage m_profilePage;

	private InlineLabel quotaMsgLabel;

	/**
	 * Constructor
	 * 
	 * @param profileRequestInfo
	 */
	public ProfileMainPanel(final ProfileRequestInfo profileRequestInfo, final GwtProfilePage profilePage) {
		this.profileRequestInfo = profileRequestInfo;
		this.m_profilePage = profilePage;
		
		// create the main panel
		mainPanel = new FlowPanel();
		mainPanel.setStyleName("profile-Content-c");

		// add user's title to the profile div
		createTitleArea();

		// add the actions area to the title div
		createActionsArea();

		// ...its content panel...
		createContentPanel();

		// All composites must call initWidget() in their constructors.
		initWidget(mainPanel);
	}

	/**
	 * Create the the main content panel that will hold all of the attributes
	 * name value pairs
	 */
	private void createContentPanel() {
		grid = new FlexTable();
		grid.setWidth("100%");
		grid.setCellSpacing(0);
		grid.setCellPadding(0);
		//grid.resizeColumns(3);
		grid.setStyleName("sectionTable");
		mainPanel.add(grid);
	}

	/**
	 * Create the Title Area that contains the user's Name
	 */
	private void createTitleArea() {

		// create a title div for the user title and actionable items
		titlePanel = new FlowPanel();
		titlePanel.addStyleName("profile-title-area");
		mainPanel.add(titlePanel);

		String userName = profileRequestInfo.getUserName();
		String url = profileRequestInfo.getAdaptedUrl();
		
		Anchor anchor = new Anchor(userName, url);
		anchor.setTitle(GwtTeaming.getMessages().qViewWorkspaceTitle());
		
		anchor.addStyleName("profile-title");
		titlePanel.add(anchor);

		PresenceControl presence = new PresenceControl(profileRequestInfo.getBinderId(), true, true, true);

		Anchor workspace = new Anchor( GwtTeaming.getMessages().qViewWorkspace(), url);
		workspace.setTitle(GwtTeaming.getMessages().qViewWorkspaceTitle());
		
		workspace.addStyleName("profile-workspace-link");

		titlePanel.add(workspace);
		titlePanel.add(presence);
	}

	/**
	 * Create the Actions Area, this is an area that should hold the additional
	 * actions any user can perform like follow or unfollow a user.
	 * 
	 */
	private void createActionsArea() {
		
		FlowPanel actionsPanel = new FlowPanel();
		actionsPanel.addStyleName("profile-actions");
		titlePanel.add(actionsPanel);
		
		//create the following action
		followingAnchor = createFollowingAction(actionsPanel);
		
		//create the edit action
		edit = createEditAction(actionsPanel);

		// Add a mouse-over handler
		MouseOverHandler mouseOverHandler = new MouseOverHandler() {
			public void onMouseOver(MouseOverEvent event) {
				Widget widget;

				widget = (Widget) event.getSource();
				widget.removeStyleName("subhead-control-bg1");
				widget.addStyleName("subhead-control-bg2");
			}// end onMouseOver()
		};
		edit.addMouseOverHandler(mouseOverHandler);
		

		// Add a mouse-out handler
		MouseOutHandler mouseOutHandler = new MouseOutHandler() {
			public void onMouseOut(MouseOutEvent event) {
				Widget widget;

				// Remove the background color we added to the anchor when the
				// user moved the mouse over the anchor.
				widget = (Widget) event.getSource();
				removeMouseOverStyles(widget);
			}// end onMouseOut()
		};
		edit.addMouseOutHandler(mouseOutHandler);
		
		delete = createDeleteAction(actionsPanel);
		
		// Add a mouse-over handler
		delete.addMouseOverHandler( new MouseOverHandler() {
			public void onMouseOver(MouseOverEvent event) {
				Widget widget;

				widget = (Widget) event.getSource();
				widget.removeStyleName("subhead-control-bg1");
				widget.addStyleName("subhead-control-bg2");
			}// end onMouseOver()
		});

		// Add a mouse-out handler
		delete.addMouseOutHandler( new MouseOutHandler() {
			public void onMouseOut(MouseOutEvent event) {
				Widget widget;

				// Remove the background color we added to the anchor when the
				// user moved the mouse over the anchor.
				widget = (Widget) event.getSource();
				removeMouseOverStyles(widget);
			}// end onMouseOut()
		});
		
		updateFollowingStatus();
	}


	private ProfileFollowingWidget createFollowingAction(FlowPanel panel){

		FlowPanel followPanel = new FlowPanel();
		followPanel.addStyleName("profile-action");
		panel.add(followPanel);
		
		ProfileFollowingWidget fAnchor = new ProfileFollowingWidget(GwtTeaming.getMessages().qViewFollowing(),
																	GwtTeaming.getMessages().qViewFollowingTitle());
		fAnchor.setVisible(showFollowButton());
		followPanel.add(fAnchor);
		
		fAnchor.addStyleName("editBrandingLink");
		fAnchor.addStyleName("editBrandingAdvancedLink");
		fAnchor.addStyleName("roundcornerSM");
		fAnchor.addStyleName("subhead-control-bg1");
		
		fAnchor.addClickHandler(new ActionClickHandler("FollowId"));;
		
		return fAnchor;
	}
	
	private Anchor createEditAction(FlowPanel panel) {
		
		FlowPanel actions = new FlowPanel();
		actions.addStyleName("profile-action");
		panel.add(actions);

		Anchor eAnchor = new Anchor(GwtTeaming.getMessages().profileEdit());
		eAnchor.setTitle(GwtTeaming.getMessages().profileEditTitle());
		
		eAnchor.addStyleName("editBrandingLink");
		eAnchor.addStyleName("editBrandingAdvancedLink");
		eAnchor.addStyleName("roundcornerSM");
		eAnchor.addStyleName("subhead-control-bg1");
		eAnchor.setVisible(isProfileModifable());

		actions.add(eAnchor);
		eAnchor.addClickHandler(new ActionClickHandler("EditId"));

		return eAnchor;
	}
	
	private Anchor createDeleteAction(FlowPanel panel) {
		
		FlowPanel actions = new FlowPanel();
		actions.addStyleName("profile-action");
		panel.add(actions);

		Anchor eAnchor = new Anchor(GwtTeaming.getMessages().profileDelete());
		eAnchor.setTitle(GwtTeaming.getMessages().profileDelete());
		
		eAnchor.addStyleName("editBrandingLink");
		eAnchor.addStyleName("editBrandingAdvancedLink");
		eAnchor.addStyleName("roundcornerSM");
		eAnchor.addStyleName("subhead-control-bg1");
		eAnchor.setVisible(profileRequestInfo.isBinderAdmin() && !profileRequestInfo.isOwner());

		actions.add(eAnchor);
		eAnchor.addClickHandler(new ActionClickHandler("DeleteId"));

		return eAnchor;
	}
	
	/**
	 * Use to determine if should show the follow button
	 * @return
	 */
	private boolean showFollowButton() {
		return !profileRequestInfo.isOwner();
	}
	
	

	/**
	 * Create the Profile Main Content Section, this creates the grid that
	 * should hold all of the user attribute name value pairs
	 * 
	 * @param cat
	 * @param grid
	 * @param rowCount
	 * @return
	 */
	public void createProfileInfoSection(final ProfileCategory cat, final FlexTable grid) {

		int row = grid.getRowCount();
		
		Label sectionHeader = new Label(cat.getTitle());
		sectionHeader.setStyleName("sectionHeading");

		grid.setWidget(row, 0, sectionHeader);
		grid.getFlexCellFormatter().setColSpan(row, 0, 2);

		// remove the bottom border from the section heading titles
		grid.getFlexCellFormatter().setStyleName(row, 0, "sectionHeadingRBB");
		
		if(cat.getName() != null && cat.getName().equals("profileHeadingPhotosAndImagesView")) {
			if(isProfileModifable()){
				buildUploadImage(grid);
			}
			
			editAvatarSuccessHandler = createAvatarSuccessHandler();
		}
		
		for (ProfileAttribute attr : cat.getAttributes()) {
			
			if(attr == null) {
				continue;
			}
			
			String dataName = attr.getDataName();
			if(dataName != null && !dataName.equals("picture")) {
				String stitle = (GwtClientHelper.hasString(attr.getTitle())? attr.getTitle() : "");
				
				Label title = new Label(stitle + ":");
				title.setStyleName("attrLabel");
				row = grid.getRowCount();
				grid.setWidget(row, 0, title);

				ProfileAttributeWidget.createAsync(attr, isEditable, row, new ProfileAttributeWidgetClient() {					
					@Override
					public void onUnavailable() {
						// Nothing to do.  Error handled in
						// asynchronous provider.
					}
					
					@Override
					public void onSuccess(ProfileAttributeWidget paw, int row) {
						Widget value = paw.getWidget();
						grid.setWidget(row, 1, value);
						grid.getFlexCellFormatter().setWidth(row, 1, "70%");
						grid.getFlexCellFormatter().setHorizontalAlignment(row, 1,
								HasHorizontalAlignment.ALIGN_LEFT);
					}
				});
			} else {
				//This must be a picture attribute
				profileAvatarArea = new ProfileAvatarArea(attr, isEditable, getRequestInfo(), editAvatarSuccessHandler);
				row = grid.getRowCount();
				grid.setWidget(row, 0, profileAvatarArea);
				grid.getFlexCellFormatter().setColSpan(row, 0, 2);
				grid.getFlexCellFormatter().setHorizontalAlignment(row, 0,
						HasHorizontalAlignment.ALIGN_LEFT);
			}
		}
	}

	private void buildUploadImage(FlexTable grid) {
		
		FlowPanel panel = new FlowPanel();
		panel.addStyleName("profileUploadPanel");
		panel.setWidth("100%");
		
		//create a form element in order to upload a new photo
		formPanel = new FormPanel();
		panel.add(formPanel);
		
		formPanel.setEncoding( FormPanel.ENCODING_MULTIPART );
		formPanel.setMethod( FormPanel.METHOD_POST );
		formPanel.addSubmitCompleteHandler( this );
		
		fileUpload = new FileUpload();
		fileUpload.addStyleName("profileUpload");
		String name = "picture";
		fileUpload.setName( name );
		InputElement input = fileUpload.getElement().cast();
		input.setSize(40);
		input.setId(name);
		
		formPanel.add(fileUpload);
		formPanel.getElement().setId("form1");
		formPanel.setAction( profileRequestInfo.getModifyUrl() + "&okBtn=1" + "&profile=1" );
	
		uploadBtn = new Anchor(GwtTeaming.getMessages().profileUpload());
		uploadBtn.setTitle(GwtTeaming.getMessages().profileUploadSelect());
		uploadBtn.addClickHandler( new ClickHandler(){
				public void onClick(ClickEvent event){
					formPanel.submit();
				}
			});
		
		uploadBtn.setVisible(false);
		uploadBtn.addStyleName("profile-anchor");
		uploadBtn.addStyleName("profileUploadButton");
		//add the upload button to the div
		panel.add(uploadBtn);
		
		//if the file upload changes, add the upload button
		fileUpload.addChangeHandler(new ChangeHandler(){
			public void onChange(ChangeEvent event) {
				uploadBtn.setVisible(true);
			}
		});
		
		//add message tags if necessary
		FlowPanel qDiskMsgDiv = createMessageDiv();
		if(qDiskMsgDiv != null){
			panel.add(qDiskMsgDiv);
		}
		
		//panel.add(upload);
		int row = grid.getRowCount();
		grid.setWidget(row, 0, panel );
		grid.getFlexCellFormatter().setColSpan(row, 0 , 2);
		// remove the bottom border from the section heading titles
		grid.getFlexCellFormatter().setStyleName(row, 0, "sectionHeadingRBB");
		
		formPanel.addSubmitCompleteHandler(new SubmitCompleteHandler() {
			public void onSubmitComplete(SubmitCompleteEvent event) {
				// When the form submission is successfully completed, this event is
		        // fired. Assuming the service returned a response of type text/html,
		        // we can get the result text here (see the FormPanel documentation for
		        // further explanation).
				if(event != null) {
					String result = event.getResults();
					if(GwtClientHelper.hasString(result)) {
						if(result.contains("ss_error_msg")) {
							int beginIndex = result.indexOf("ss_error_msg");
							int endIndex = result.indexOf("ss_error_code");

							String part1 = result.substring(beginIndex, endIndex);
							char quoteChar = '\'';
							beginIndex = part1.indexOf(quoteChar);
							if (0 > beginIndex) {
								quoteChar = '"';
								beginIndex = part1.indexOf(quoteChar);
							}
							String part2 = part1.substring(beginIndex+1);
							endIndex = part2.indexOf(quoteChar);
							String msg = part2.substring(0, endIndex);
							Window.alert(msg);
						}
					}
				}

			}
	    }); 
	}
	
	private FlowPanel createMessageDiv() {
		FlowPanel msgDiv = null; 
		msgDiv = new FlowPanel();
		quotaMsgLabel = new InlineLabel();
		msgDiv.addStyleName("stats_error_msg");
		msgDiv.add(quotaMsgLabel);

		String quota = profileRequestInfo.getQuotaMessage();
		if(GwtClientHelper.hasString(quota)) {
			quotaMsgLabel.setText(quota);
		}
		
		return msgDiv;
	}
	
	/**
	 * Set the Profile Category which contains the heading label and all of the
	 * child Profile Attributes
	 * 
	 * @param cat
	 */
	public void setCategory(ProfileCategory cat) {
		createProfileInfoSection(cat, grid);
	}

	/**
	 * Remove the styles that were added to the given widget when the user moved
	 * the mouse over the widget.
	 */
	private void removeMouseOverStyles(Widget widget) {
		widget.removeStyleName("subhead-control-bg2");
		widget.addStyleName("subhead-control-bg1");
	}// end removeMouseOverStyles()

	/*
	 * This method will be called to track the current binder.
	 */
	private void followPerson() {
		GwtClientHelper.jsFireVibeEventOnMainEventBus(TeamingEvents.TRACK_CURRENT_BINDER);
	}
	
	/*
	 * This method will be called to remove the tracking on the person
	 * whose workspace is the current binder.
	 */
	private void unfollowPerson() {
		GwtClientHelper.jsFireVibeEventOnMainEventBus(TeamingEvents.UNTRACK_CURRENT_BINDER);
		GwtClientHelper.jsFireVibeEventOnMainEventBus(TeamingEvents.UNTRACK_CURRENT_PERSON);
	}
	
	/**
	 * Checks to see if the current User is following this person
	 * @return
	 */
	private void updateFollowingStatus() {
		IsPersonTrackedCmd cmd;
		
		cmd = new IsPersonTrackedCmd( profileRequestInfo.getBinderId() );
		GwtClientHelper.executeCommand( cmd, new AsyncCallback<VibeRpcResponse>()
				{
					public void onFailure( Throwable t )
					{
						GwtClientHelper.handleGwtRPCFailure(
							t,
							GwtTeaming.getMessages().rpcFailure_IsPersonTracked(),
							profileRequestInfo.getBinderId());
					}//end onFailure()
					
					public void onSuccess( VibeRpcResponse response )
					{
						Boolean success;
						BooleanRpcResponseData responseData;
						
						responseData = (BooleanRpcResponseData) response.getResponseData();
						success = responseData.getBooleanValue();
						
						if(success.booleanValue()) {
							updateFollowingButton(true);
						} else {
							updateFollowingButton(false);
						}
					}// end onSuccess()
				});
	}


	public void setEditable(boolean edit) {
		isEditable  = edit;
	}
	
	public class ActionClickHandler implements ClickHandler {

		String handlerId = null;
		public ActionClickHandler(String id) {
			handlerId = id;
		}

		public void onClick(ClickEvent event) {
			if (handlerId.equals("EditId")) {
				String url = profileRequestInfo.getModifyUrl();
				if(GwtClientHelper.hasString(url)){
					GwtClientHelper.jsLaunchUrlInWindow(url, "_blank", 800, 800);
				}
			} else if(handlerId.equals("FollowId")) {
				if(followingAnchor.isChecked()) {
					unfollowPerson();
				} else {
					followPerson();
				}
			} else if (handlerId.equals("DeleteId")) {
				String url = profileRequestInfo.getDeleteUserUrl();
				if(GwtClientHelper.hasString(url)){
					GwtClientHelper.jsLaunchUrlInWindow(url, "_blank", 400, 400);
				}
			}
		}
	}

	/**
	 * Use to determine if should show the edit button
	 * 
	 * @return true if owns this profile or is binderAdmin
	 */
	private boolean isProfileModifable() {
		return profileRequestInfo.isBinderAdmin()
				|| profileRequestInfo.isModifyAllowed();
	}

	/**
	 * Update the follow button 
	 * @param isFollowing
	 */
	private void updateFollowingButton(boolean isFollowing) {
		if(isFollowing) {
			followingAnchor.setText(GwtTeaming.getMessages().qViewFollowing());
			followingAnchor.setTitle(GwtTeaming.getMessages().qViewFollowingTitle());
			followingAnchor.setChecked(true);
		} else {
			followingAnchor.setText(GwtTeaming.getMessages().qViewFollow());
			followingAnchor.setTitle(GwtTeaming.getMessages().qViewFollowTitle());
			followingAnchor.setChecked(false);
		}
	}
	
	private class ProfileFollowingWidget extends Anchor {

		private Label label;
		private Image img;
		private boolean isChecked;
		private FlowPanel panel;

		public ProfileFollowingWidget(String text, String title) {
			super();

			panel = new FlowPanel();

			img = new Image(GwtTeaming.getImageBundle().check12());
			img.addStyleName("qView-follow-img");
			img.setVisible(false);
			panel.add(img);

			label = new Label(text);
			label.addStyleName("qView-follow-label");
			label.setTitle(title);
			panel.add(label);
			
			getElement().appendChild(panel.getElement());
			
			
			// Add a mouse-over handler
			addMouseOverHandler( new MouseOverHandler() {
				public void onMouseOver(MouseOverEvent event) {
					if(!isChecked()){
						removeStyleName("subhead-control-bg1");
						addStyleName("subhead-control-bg2");
					} else {
						removeStyleName("profile-selected-bg");
						addStyleName("subhead-control-bg2");
					}
				}// end onMouseOver()
			});

			// Add a mouse-out handler
			addMouseOutHandler( new MouseOutHandler() {
				public void onMouseOut(MouseOutEvent event) {
					if(!isChecked()){
						removeStyleName("subhead-control-bg2");
						addStyleName("subhead-control-bg1");
					} else {
						removeStyleName("subhead-control-bg2");
						addStyleName("profile-selected-bg");
					}
				}// end onMouseOut()
			});
		}

		public boolean isChecked() {
			return isChecked;
		}

		public void setChecked(boolean checked) {
			if(checked) {
				isChecked = true;
				if(img != null){
					removeStyleName("subhead-control-bg1");
					addStyleName("profile-selected-bg");
					img.setVisible(true);
				}
			} else {
				isChecked = false;
				if(img != null){
					removeStyleName("profile-selected-bg");
					addStyleName("subhead-control-bg1");
					img.setVisible(false);
				}
			}
		}
		
		public void setText(String text) {
			label.setText(text);
		}
		
		public void setTitle(String text) {
			label.setTitle(text);
		}
	}

	public void onSubmitComplete(SubmitCompleteEvent event) {
		// Do we have an editSuccessfulHandler?
		if ( editAvatarSuccessHandler != null )
		{
			// Yes, call it.
			editAvatarSuccessHandler.editSuccessful( null );
		}
	}
	
	public ProfileRequestInfo getRequestInfo() {
		return profileRequestInfo;
	}
	
	private EditSuccessfulHandler createAvatarSuccessHandler() {
		
		if(editAvatarSuccessHandler == null) {
			
			editAvatarSuccessHandler = new EditSuccessfulHandler() {
				public boolean editSuccessful(Object obj) {
					
					GetProfileAvatarsCmd cmd;
					
					if(profileAvatarArea != null) {
						profileAvatarArea.clear();
					}
					
					if(formPanel != null) {
						formPanel.reset();
					}
					
					if(uploadBtn != null & uploadBtn.isVisible()){
						uploadBtn.setVisible(false);
					}
					
					//rebuild the avatar area

					// create an async callback to handle the result of the request to get
					// the state:
					AsyncCallback<VibeRpcResponse> callback = new AsyncCallback<VibeRpcResponse>() {
						public void onFailure(Throwable t) {
							// display error
							GwtClientHelper.handleGwtRPCFailure(
								t,
								GwtTeaming.getMessages().rpcFailure_GetProfileAvatars(),
								profileRequestInfo.getBinderId());
						}

						public void onSuccess( VibeRpcResponse response ) {
							ProfileAttribute attr;
							
							attr = (ProfileAttribute) response.getResponseData();
							
							try {
								profileAvatarArea.createWidget(attr);

								//replace the image on sidebar with the current image
								List<ProfileAttributeListElement> value = (List<ProfileAttributeListElement>)attr.getValue();
								if(value != null && value.size() > 0){
									ProfileAttributeListElement valItem = value.get(0);
									String sval = valItem.getValue().toString();
											
									Image img = new Image(sval);
			
									// Find the element that this RootPanel will wrap.
									Element elem = Document.get().getElementById("profilePhoto");
									Element oldChild = null;
									Element anchor = null;
									
									NodeList alist = (NodeList) elem.getElementsByTagName("a");
									if(alist!=null && alist.getLength() > 0){
										anchor = (Element) alist.getItem(0);
									}
									NodeList imgList = (NodeList) elem.getElementsByTagName("img");
									if(imgList!=null && imgList.getLength() > 0){
										oldChild = (Element) imgList.getItem(0);
									}
									
									if(anchor != null) {
										anchor.removeChild(oldChild);
										if(img.getElement() != null){
											img.getElement().removeAttribute("width");
											img.getElement().removeAttribute("height");
											anchor.appendChild(img.getElement());
										}
									}
									
									updateQuota();
								}	
							} catch (Exception e) {
								Window.alert("Error modifying avatar" + e.getMessage());
							}
						}
					};

					cmd = new GetProfileAvatarsCmd( profileRequestInfo.getBinderId() );
					GwtClientHelper.executeCommand( cmd, callback );
					
					return true;
				}
			};
		}
		
		return editAvatarSuccessHandler;
	}
	
	private void updateQuota() {
		
		GetDiskUsageInfoCmd cmd;
		
		AsyncCallback<VibeRpcResponse> callback = new AsyncCallback<VibeRpcResponse>() {
			public void onFailure(Throwable t) {
				// display error
				GwtClientHelper.handleGwtRPCFailure(
					t,
					GwtTeaming.getMessages().rpcFailure_GetProfileAvatars(),
					profileRequestInfo.getBinderId());
			}
			public void onSuccess( VibeRpcResponse response) {
				DiskUsageInfo info = null;
				
				if ( response.getResponseData() != null )
					info = (DiskUsageInfo) response.getResponseData();
				
				if(info != null) {
					if(info.getQuotaMessage() != null){
						quotaMsgLabel.setText(info.getQuotaMessage());
					} else {
						quotaMsgLabel.setText("");
					}
					
					if(info.getUsedQuota() != null) {
						m_profilePage.updateQuota(info.getUsedQuota());
					}
				}
			}
		};
		
		cmd = new GetDiskUsageInfoCmd( profileRequestInfo.getBinderId() );
		GwtClientHelper.executeCommand( cmd, callback );
	}
	
	public final native boolean isFileError() /*-{  if( wnd.profileEmptyFrame.ss_error_code != null ) {
															return true; 
													} else {
													 		return false;
													}}-*/;
	public final native String getFileError() /*-{ return window.profileEmptyFrame.ss_error_msg; }-*/;
}