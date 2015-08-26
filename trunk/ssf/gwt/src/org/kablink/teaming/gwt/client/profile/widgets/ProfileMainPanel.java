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

import java.util.List;

import org.kablink.teaming.gwt.client.EditSuccessfulHandler;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.event.TeamingEvents;
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

/**
 * ?
 * 
 * @author nbjensen@novell.com
 */
@SuppressWarnings("unchecked")
public class ProfileMainPanel extends Composite implements SubmitCompleteHandler {
	ProfileRequestInfo				profileRequestInfo;			//
	
	private Anchor					m_editBtn;					//
	private Anchor					m_uploadBtn;				//
	private boolean					m_isEditable;				//
	private EditSuccessfulHandler	m_editAvatarSuccessHandler;	//
	private FileUpload				m_fileUpload;				//
	private FlexTable				m_grid;						//
	private FlowPanel				m_mainPanel;				//
	private FlowPanel				m_titlePanel;				//
	private FormPanel				m_formPanel;				//
	private GwtProfilePage			m_profilePage;				//
	private InlineLabel				m_quotaMsgLabel;			//
	private ProfileAvatarArea		m_profileAvatarArea;		//
	private ProfileFollowingWidget	m_followingAnchor;			//

	private final static String[] FIXUP_AVATAR_URL_CHANGE_THESE = new String[] {
		// The follow define URL parts used to 'fixup' an avatar URL so
		// that we use a scaled image instead of a full image.
		"readFile",
		"readScaled",
	};
	private final static String FIXUP_AVATAR_URL_TO_THIS = "readThumbnail";	// Other option:  "readScaledFile"
	
	/**
	 * Constructor
	 * 
	 * @param profileRequestInfo
	 */
	public ProfileMainPanel(final ProfileRequestInfo profileRequestInfo, final GwtProfilePage profilePage) {
		this.profileRequestInfo = profileRequestInfo;
		m_profilePage = profilePage;
		
		// create the main panel
		m_mainPanel = new FlowPanel();
		m_mainPanel.setStyleName("profile-Content-c");

		// add user's title to the profile div
		createTitleArea();

		// add the actions area to the title div
		createActionsArea();

		// ...its content panel...
		createContentPanel();

		// All composites must call initWidget() in their constructors.
		initWidget(m_mainPanel);
	}

	/**
	 * Create the the main content panel that will hold all of the attributes
	 * name value pairs
	 */
	private void createContentPanel() {
		m_grid = new FlexTable();
		m_grid.setWidth("100%");
		m_grid.setCellSpacing(0);
		m_grid.setCellPadding(0);
		//m_grid.resizeColumns(3);
		m_grid.setStyleName("sectionTable");
		m_mainPanel.add(m_grid);
	}

	/**
	 * Create the Title Area that contains the user's Name
	 */
	private void createTitleArea() {

		// create a title div for the user title and actionable items
		m_titlePanel = new FlowPanel();
		m_titlePanel.addStyleName("profile-title-area");
		m_mainPanel.add(m_titlePanel);

		String userName = profileRequestInfo.getUserName();
		if (GwtClientHelper.isLicenseFilr()) {
			InlineLabel unLabel = new InlineLabel(userName);
			unLabel.addStyleName("profile-title");
			m_titlePanel.add(unLabel);
		}
		
		else {
			String url = profileRequestInfo.getAdaptedUrl();
			
			Anchor anchor = new Anchor(userName, url);
			anchor.setTitle(GwtTeaming.getMessages().qViewWorkspaceTitle());
			
			anchor.addStyleName("profile-title");
			m_titlePanel.add(anchor);
	
			Anchor workspace = new Anchor( GwtTeaming.getMessages().qViewWorkspace(), url);
			workspace.setTitle(GwtTeaming.getMessages().qViewWorkspaceTitle());
			
			workspace.addStyleName("profile-workspace-link");
	
			m_titlePanel.add(workspace);
		}
		
		PresenceControl presence = new PresenceControl(profileRequestInfo.getUserId(), profileRequestInfo.getBinderId(), true, true, true);
		m_titlePanel.add(presence);
	}

	/**
	 * Create the Actions Area, this is an area that should hold the additional
	 * actions any user can perform like follow or unfollow a user.
	 * 
	 */
	private void createActionsArea() {
		
		FlowPanel actionsPanel = new FlowPanel();
		actionsPanel.addStyleName("profile-actions");
		m_titlePanel.add(actionsPanel);
		
		if (!(GwtClientHelper.isLicenseFilr())) {
			//create the following action
			m_followingAnchor = createFollowingAction(actionsPanel);
		}
		
		//create the m_editBtn action
		m_editBtn = createEditAction(actionsPanel);

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
		
		fAnchor.addClickHandler(new ActionClickHandler("FollowId"));;
		
		return fAnchor;
	}
	
	private Anchor createEditAction(FlowPanel panel) {
		
		FlowPanel actions = new FlowPanel();
		actions.addStyleName("profile-action");
		panel.add(actions);

		Anchor eAnchor = new Anchor(GwtTeaming.getMessages().profileEdit());
		eAnchor.setTitle(GwtTeaming.getMessages().profileEditTitle());
		
		eAnchor.setVisible(isProfileModifable());

		actions.add(eAnchor);
		eAnchor.addClickHandler(new ActionClickHandler("EditId"));

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
	 * Create the Profile Main Content Section, this creates the m_grid that
	 * should hold all of the user attribute name value pairs
	 * 
	 * @param cat
	 * @param m_grid
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
			
			m_editAvatarSuccessHandler = createAvatarSuccessHandler();
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

				ProfileAttributeWidget.createAsync(attr, m_isEditable, row, new ProfileAttributeWidgetClient() {					
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
				m_profileAvatarArea = new ProfileAvatarArea(attr, m_isEditable, getRequestInfo(), m_editAvatarSuccessHandler);
				row = grid.getRowCount();
				grid.setWidget(row, 0, m_profileAvatarArea);
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
		m_formPanel = new FormPanel();
		panel.add(m_formPanel);
		
		m_formPanel.setEncoding( FormPanel.ENCODING_MULTIPART );
		m_formPanel.setMethod( FormPanel.METHOD_POST );
		m_formPanel.addSubmitCompleteHandler( this );
		
		m_fileUpload = new FileUpload();
		m_fileUpload.addStyleName("profileUpload");
		String name = "picture";
		m_fileUpload.setName( name );
		InputElement input = m_fileUpload.getElement().cast();
		input.setSize(40);
		input.setId(name);
		
		m_formPanel.add(m_fileUpload);
		m_formPanel.getElement().setId("form1");
		m_formPanel.setAction( profileRequestInfo.getModifyUrl() + "&okBtn=1" + "&profile=1" );
	
		m_uploadBtn = new Anchor(GwtTeaming.getMessages().profileUpload());
		m_uploadBtn.setTitle(GwtTeaming.getMessages().profileUploadSelect());
		m_uploadBtn.addClickHandler( new ClickHandler(){
				@Override
				public void onClick(ClickEvent event){
					m_formPanel.submit();
				}
			});
		
		m_uploadBtn.setVisible(false);
		m_uploadBtn.addStyleName("profile-anchor");
		m_uploadBtn.addStyleName("profileUploadButton");
		//add the upload button to the div
		panel.add(m_uploadBtn);
		
		//if the file upload changes, add the upload button
		m_fileUpload.addChangeHandler(new ChangeHandler(){
			@Override
			public void onChange(ChangeEvent event) {
				m_uploadBtn.setVisible(true);
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
		
		m_formPanel.addSubmitCompleteHandler(new SubmitCompleteHandler() {
			@Override
			public void onSubmitComplete(SubmitCompleteEvent event) {
				// When the form submission is successfully completed, this event is
		        // fired. Assuming the service returned a response of type text/html,
		        // we can get the result text here (see the FormPanel documentation for
		        // further explanation).
				if(event != null) {
					String result = event.getResults();
					if(GwtClientHelper.hasString(result)) {
						if(result.contains("ss_error_msg")) {
							// The html that we received should look like the following:
							/*
								<script type="text/javascript">
									var ss_error_msg = "Some error message." ;
									var ss_error_code = 1;
								</script>
								
								<div class="ss_style ss_portlet">
									<h1>Error</h1>
								
									<p>Some error message.<br></p>
								
									<br>
									<input value="Back" class="ss_submit" onclick="setTimeout('self.window.history.back();', 2000);" type="button">
								</div>
							 */
							// See defCodedError.jsp.  Note that
							// exceptions must be mapped to this in
							// applicationContext.xml for everything
							// to work properly.
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
		m_quotaMsgLabel = new InlineLabel();
		msgDiv.addStyleName("stats_error_msg");
		msgDiv.add(m_quotaMsgLabel);

		String quota = profileRequestInfo.getQuotaMessage();
		if(GwtClientHelper.hasString(quota)) {
			m_quotaMsgLabel.setText(quota);
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
		createProfileInfoSection(cat, m_grid);
	}

	/**
	 * Remove the styles that were added to the given widget when the user moved
	 * the mouse over the widget.
	 */
	private void removeMouseOverStyles(Widget widget) {
		widget.removeStyleName("subhead-control-bg2");
		widget.addStyleName("subhead-control-bg1");
	}

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
					@Override
					public void onFailure( Throwable t )
					{
						GwtClientHelper.handleGwtRPCFailure(
							t,
							GwtTeaming.getMessages().rpcFailure_IsPersonTracked(),
							profileRequestInfo.getBinderId());
					}
					
					@Override
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
					}
				});
	}


	public void setEditable(boolean edit) {
		m_isEditable  = edit;
	}
	
	public class ActionClickHandler implements ClickHandler {

		String handlerId = null;
		public ActionClickHandler(String id) {
			handlerId = id;
		}

		@Override
		public void onClick(ClickEvent event) {
			if (handlerId.equals("EditId")) {
				String url = profileRequestInfo.getModifyUrl();
				if(GwtClientHelper.hasString(url)){
					GwtClientHelper.jsLaunchUrlInWindow(url, "_blank", 800, 800);
				}
			} else if(handlerId.equals("FollowId")) {
				if (null != m_followingAnchor) {
					if(m_followingAnchor.isChecked()) {
						unfollowPerson();
					} else {
						followPerson();
					}
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
	 * Use to determine if should show the m_editBtn button
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
		if (null != m_followingAnchor) {
			if(isFollowing) {
				m_followingAnchor.setText(GwtTeaming.getMessages().qViewFollowing());
				m_followingAnchor.setTitle(GwtTeaming.getMessages().qViewFollowingTitle());
				m_followingAnchor.setChecked(true);
			} else {
				m_followingAnchor.setText(GwtTeaming.getMessages().qViewFollow());
				m_followingAnchor.setTitle(GwtTeaming.getMessages().qViewFollowTitle());
				m_followingAnchor.setChecked(false);
			}
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
				@Override
				public void onMouseOver(MouseOverEvent event) {
					if(!isChecked()){
						removeStyleName("subhead-control-bg1");
						addStyleName("subhead-control-bg2");
					} else {
						removeStyleName("profile-selected-bg");
						addStyleName("subhead-control-bg2");
					}
				}
			});

			// Add a mouse-out handler
			addMouseOutHandler( new MouseOutHandler() {
				@Override
				public void onMouseOut(MouseOutEvent event) {
					if(!isChecked()){
						removeStyleName("subhead-control-bg2");
						addStyleName("subhead-control-bg1");
					} else {
						removeStyleName("subhead-control-bg2");
						addStyleName("profile-selected-bg");
					}
				}
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
		
		@Override
		public void setText(String text) {
			label.setText(text);
		}
		
		@Override
		public void setTitle(String text) {
			label.setTitle(text);
		}
	}

	@Override
	public void onSubmitComplete(SubmitCompleteEvent event) {
		// Do we have an editSuccessfulHandler?
		if ( m_editAvatarSuccessHandler != null )
		{
			// Yes, call it.
			m_editAvatarSuccessHandler.editSuccessful( null );
		}
	}
	
	public ProfileRequestInfo getRequestInfo() {
		return profileRequestInfo;
	}
	
	private EditSuccessfulHandler createAvatarSuccessHandler() {
		
		if(m_editAvatarSuccessHandler == null) {
			
			m_editAvatarSuccessHandler = new EditSuccessfulHandler() {
				@Override
				public boolean editSuccessful(Object obj) {
					
					GetProfileAvatarsCmd cmd;
					
					if(m_profileAvatarArea != null) {
						m_profileAvatarArea.clear();
					}
					
					if(m_formPanel != null) {
						m_formPanel.reset();
					}
					
					if(m_uploadBtn != null & m_uploadBtn.isVisible()){
						m_uploadBtn.setVisible(false);
					}
					
					//rebuild the avatar area

					// create an async callback to handle the result of the request to get
					// the state:
					AsyncCallback<VibeRpcResponse> callback = new AsyncCallback<VibeRpcResponse>() {
						@Override
						public void onFailure(Throwable t) {
							// display error
							GwtClientHelper.handleGwtRPCFailure(
								t,
								GwtTeaming.getMessages().rpcFailure_GetProfileAvatars(),
								profileRequestInfo.getBinderId());
						}

						@Override
						public void onSuccess( VibeRpcResponse response ) {
							ProfileAttribute attr;
							
							attr = (ProfileAttribute) response.getResponseData();
							String globalAvatarUrl = "";
							
							try {
								m_profileAvatarArea.createWidget(attr);

								//replace the image on sidebar with the current image
								List<ProfileAttributeListElement> value = (List<ProfileAttributeListElement>)attr.getValue();
								String avatarUrl;
								if(value != null && value.size() > 0){
									ProfileAttributeListElement valItem = value.get(0);
									avatarUrl       = 
									globalAvatarUrl = valItem.getValue().toString();
								}
								
								else {
									avatarUrl       = GwtTeaming.getImageBundle().userAvatar().getSafeUri().asString();
									globalAvatarUrl = "";
								}
											
								Image img = new Image(avatarUrl);
		
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
							} catch (Exception e) {
								Window.alert("Error modifying avatar" + e.getMessage());
							}
							
							// Tell the rest of the system that the
							// user's avatar has been modified.
							profileRequestInfo.setUserAvatarUrl(globalAvatarUrl);
							if (profileRequestInfo.getBinderId().equals(profileRequestInfo.getCurrentUserWorkspaceId())) {
								patchTopAvatarUrl(fixupAvatarUrl(globalAvatarUrl));
							}
						}
					};

					cmd = new GetProfileAvatarsCmd( profileRequestInfo.getBinderId() );
					GwtClientHelper.executeCommand( cmd, callback );
					
					return true;
				}
			};
		}
		
		return m_editAvatarSuccessHandler;
	}
	
	/*
	 * Updates the user's avatar URL in the top level request info.
	 */
	private native String patchTopAvatarUrl(String avatarUrl) /*-{
		$wnd.top.m_requestInfo.userAvatarUrl = avatarUrl;
	}-*/;
	
	/*
	 * Given an avatar URL from a user's profile, patches it so
	 * that it renders from a thumbnail instead of the full image.
	 */
	private static String fixupAvatarUrl(String url) {
		// Do we have a URL to fixup?
		if (GwtClientHelper.hasString(url)) {
			// Yes!  Change it so that it renders from a thumbnail
			// instead of the full image.							
			for (int i = 0; i < FIXUP_AVATAR_URL_CHANGE_THESE.length; i += 1) {
				if (url.contains(FIXUP_AVATAR_URL_CHANGE_THESE[i])) {
					url = GwtClientHelper.replace(url, FIXUP_AVATAR_URL_CHANGE_THESE[i], FIXUP_AVATAR_URL_TO_THIS);
					break;
				}
			}
		}
		
		// If we get here, URL refers to the fixed up avatar URL.
		// Return it.
		return url;
	}
	
	private void updateQuota() {
		
		GetDiskUsageInfoCmd cmd;
		
		AsyncCallback<VibeRpcResponse> callback = new AsyncCallback<VibeRpcResponse>() {
			@Override
			public void onFailure(Throwable t) {
				// display error
				GwtClientHelper.handleGwtRPCFailure(
					t,
					GwtTeaming.getMessages().rpcFailure_GetProfileAvatars(),
					profileRequestInfo.getBinderId());
			}
			@Override
			public void onSuccess( VibeRpcResponse response) {
				DiskUsageInfo info = null;
				
				if ( response.getResponseData() != null )
					info = (DiskUsageInfo) response.getResponseData();
				
				if(info != null) {
					if(info.getQuotaMessage() != null){
						m_quotaMsgLabel.setText(info.getQuotaMessage());
					} else {
						m_quotaMsgLabel.setText("");
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
	
	/**
	 * ?
	 * 
	 * @return
	 */
	public final native boolean isFileError() /*-{
		if(null != wnd.profileEmptyFrame.ss_error_code) {
			return true; 
		}
		
		else {
			return false;
		}
	}-*/;
	
	/**
	 * ?
	 * 
	 * @return
	 */
	public final native String getFileError() /*-{
		return window.profileEmptyFrame.ss_error_msg;
	}-*/;
}
