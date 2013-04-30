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
package org.kablink.teaming.gwt.client.admin;

import java.util.ArrayList;

import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingException;
import org.kablink.teaming.gwt.client.GwtTeamingException.ExceptionType;
import org.kablink.teaming.gwt.client.rpc.shared.GetExtensionInfoCmd;
import org.kablink.teaming.gwt.client.rpc.shared.GetExtensionInfoRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.RemoveExtensionCmd;
import org.kablink.teaming.gwt.client.rpc.shared.RemoveExtensionRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteHandler;
import com.google.gwt.user.client.ui.FormPanel.SubmitEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitHandler;

/**
 * This widget is to configure extensions (plugins and widgets)
 * 
 * @author nbjensen
 */
public class ExtensionsConfig  extends Composite {
	
	private FlowPanel fPanel = new FlowPanel();
	private FlexTable extFlexTable = new FlexTable();
	private Label	extensionPanelStateText = new Label();
	private ArrayList <ExtensionInfoClient> extList = new ArrayList<ExtensionInfoClient>();

	/*
	 * Note that the class constructor is private to facilitate code
	 * splitting.  All instantiations of this object must be done
	 * through its createAsync().
	 */
	private ExtensionsConfig() {

		fPanel.setStyleName("ss_form");
		fPanel.add(new HTML("<br/>"));
		
		fPanel.add(extensionPanelStateText);
		
		extFlexTable.setCellPadding(2);
		extFlexTable.setStyleName("lpeTableDropZone");
		extFlexTable.getRowFormatter().addStyleName(0, "lpeTableDropZone-highlighted");
		
		extFlexTable.setText(0, 0, GwtTeaming.getMessages().extensionsName());
		extFlexTable.setText(0, 1, GwtTeaming.getMessages().extensionsDesc());
		extFlexTable.setText(0, 2, GwtTeaming.getMessages().extensionsRemove());
		
		extFlexTable.getCellFormatter().setStyleName(0, 0, "ss_bold");
		extFlexTable.getCellFormatter().setStyleName(0, 1, "ss_bold");
		extFlexTable.getCellFormatter().setStyleName(0, 2, "ss_bold");
		
		fPanel.add(extFlexTable);

		{
			Timer timer;
			timer = new Timer()
			{
				/**
				 * 
				 */
				@Override
				public void run()
				{
					refreshTable();
				}// end run()
			};
			
			timer.schedule( 250 );
		}
		
		// All composites must call initWidget() in their constructors.
		initWidget( fPanel );
	}
	
	@SuppressWarnings("unused")
	private FormPanel addFileUpload()
	{
		// Create a FormPanel and point it at a service.
	    final FormPanel form = new FormPanel("uploadFormTgt");
	    form.setStyleName("ss_form");
	    form.setAction("http://localhost:8080/ssf/a/do?p_name=ss_forum&p_action=1&action=manage_extensions");
	    
	    // Because we're going to add a FileUpload widget, we'll need to set the
	    // form to use the POST method, and multipart MIME encoding.
	    form.setEncoding(FormPanel.ENCODING_MULTIPART);
	    form.setMethod(FormPanel.METHOD_POST);

	    // Create a panel to hold all of the form widgets.
	    VerticalPanel panel = new VerticalPanel();
	    form.add(panel);

	    // Create a FileUpload widget.
	    final FileUpload upload = new FileUpload();
	    upload.setName("uploadFormElement");
	    panel.add(upload);
	    
	    final Button uploadButton = new Button("Submit");
		uploadButton.addClickHandler(new ClickHandler(){
			public void onClick(ClickEvent event) {
				String filename = upload.getFilename();
				if (filename.length() == 0)	{
					Window.alert("Error: No File");
				} else {
					form.submit();
				}
			}
			
		});
		panel.add(uploadButton);
		
	    // Add an event handler to the form.
	    form.addSubmitHandler(new SubmitHandler() {
		  public void onSubmit(SubmitEvent event) {
		        // This event is fired just before the form is submitted. We can take
		        // this opportunity to perform validation.
			    String filename = upload.getFilename();
			    if (filename.length() == 0) {
		          Window.alert("The text box must not be empty");
		          event.cancel();
		        }
			    
			    Window.alert("Submitted form");
		  }
	    });

	    form.addSubmitCompleteHandler(new SubmitCompleteHandler() {

			public void onSubmitComplete(SubmitCompleteEvent event) {
				// When the form submission is successfully completed, this event is
		        // fired. Assuming the service returned a response of type text/html,
		        // we can get the result text here (see the FormPanel documentation for
		        // further explanation).
		        Window.alert(event.getResults());
			}
	    	
	    }); 
	    
	    return form;
	}
	
	
	private void refreshTable() {
		GetExtensionInfoCmd cmd;
		
		// create an async callback to handle the result of the request to get the state:
		AsyncCallback<VibeRpcResponse> callback = new AsyncCallback<VibeRpcResponse>()
		{
			/**
			 * 
			 */
			public void onFailure(Throwable t)
			{
				// display error text if we can't get the tutorial panel state:
				String msg = GwtTeaming.getMessages().extensionsRPCError();
				GwtClientHelper.handleGwtRPCFailure( t, msg );
				extensionPanelStateText.setText( GwtTeaming.getMessages().extensionsRPCError() );
			}
	
			public void onSuccess( VibeRpcResponse response ) {
				ExtensionInfoClient[] info;
				GetExtensionInfoRpcResponseData responseData;
				
				responseData = (GetExtensionInfoRpcResponseData) response.getResponseData();
				info = responseData.getExtensionInfo();
				
				// display the tutorial panel state in the label:
				extensionPanelStateText.setText( "" );
				updateTable(info);
			}
		};
	
		extensionPanelStateText.setText( GwtTeaming.getMessages().extensionsWaiting() );
		cmd = new GetExtensionInfoCmd();
		GwtClientHelper.executeCommand( cmd, callback );
	}
	
	private void updateTable(ExtensionInfoClient[] extensions){
		
		extList.clear();
		extFlexTable.clear();
		
		int length = (extensions != null ? extensions.length : 0);
		for(int i=0; i < length; i++){
			ExtensionInfoClient extInfo = extensions[i];
			addRow(extInfo, i+1);  //add 1 to row to account for the header
		}
	}
	
	private void addRow(final ExtensionInfoClient info, final int row)
	{
		if(extList.contains(info))
			return;
		
		extList.add(info);
		
		String name = info.getTitle();
		if(name == null)
		{
			name = info.getName();
		}
		final Anchor achorLabel = new Anchor(name);
		
		//final Label nameLabel = new Label(info.getName());
		achorLabel.setStyleName("ss_style");
		
		achorLabel.addClickHandler(new ClickHandler(){
			public void onClick(ClickEvent event) {
				int xPos = achorLabel.getAbsoluteLeft();
				int yPos = achorLabel.getAbsoluteTop();
				ExtensionViewDetailsDlg box = new ExtensionViewDetailsDlg(info, false, true, xPos, yPos);
				box.show();
			}});
		
		extFlexTable.setWidget(row, 0, achorLabel );
		extFlexTable.setText(row, 1, info.getDescription());
		
		Button removeButton = new Button("x");
		removeButton.addStyleDependentName("remove");
		removeButton.addClickHandler(new ClickHandler(){
			public void onClick(ClickEvent event) {
				if(Window.confirm(GwtTeaming.getMessages().extensionsConfirmDelete())){
					removeExtension(row);
				}
			}});
		
		extFlexTable.setWidget(row, 2, removeButton);
	}
	
	private void removeExtension(final int row) {
			RemoveExtensionCmd cmd;
			ExtensionInfoClient extInfo = extList.get(row-1);
			
			// create an async callback to handle the result of the request to get the state:
			AsyncCallback<VibeRpcResponse> callback = new AsyncCallback<VibeRpcResponse>()
			{
				/**
				 * 
				 */
				public void onFailure(Throwable t)
				{
					String msg = GwtTeaming.getMessages().extensionsRPCError();
					GwtClientHelper.handleGwtRPCFailure( t, msg );
					
					try {
						throw t;
					} catch (ExtensionDefinitionInUseException exEx) {
						Window.alert( GwtTeaming.getMessages().extensionsRemoveFailed() );
						//extensionPanelStateText.setText( exEx.getMessage() );
					} catch (GwtTeamingException gtEx) {
						if ( gtEx.getExceptionType() == ExceptionType.EXTENSION_DEFINITION_IN_USE )
							Window.alert( GwtTeaming.getMessages().extensionsRemoveFailed() );
					} catch (Throwable e) {
						// display error text if we can't get the tutorial panel state:
						extensionPanelStateText.setText( msg );
					}
				}
		
				public void onSuccess( VibeRpcResponse response ) {
					ExtensionInfoClient[] info;
					RemoveExtensionRpcResponseData responseData;
					
					responseData = (RemoveExtensionRpcResponseData) response.getResponseData();
					info = responseData.getExtensionInfo();
					
					// display the tutorial panel state in the label:
					extensionPanelStateText.setText( "" );
					extFlexTable.removeRow(row);
					extList.remove(row-1);
					updateTable(info);
				}
			};
		
			extensionPanelStateText.setText( GwtTeaming.getMessages().extensionsWaiting() );
			cmd = new RemoveExtensionCmd( extInfo.getId() );
			GwtClientHelper.executeCommand( cmd, callback );
		}
	
		/**
		 * Callback interface to interact with the extensions
		 * configuration utility asynchronously after it loads. 
		 */
		public interface ExtensionsConfigClient {
			void onSuccess(ExtensionsConfig ec);
			void onUnavailable();
		}
	
		/**
		 * Loads the ExtensionsConfig split point and returns an
		 * instance of it via the callback.
		 * 
		 * @param ecClient
		 */
		public static void createAsync(final ExtensionsConfigClient ecClient) {
			GWT.runAsync(ExtensionsConfig.class, new RunAsyncCallback() {			
				@Override
				public void onSuccess() {
					ExtensionsConfig ec = new ExtensionsConfig();
					ecClient.onSuccess(ec);
				}
				
				@Override
				public void onFailure(Throwable reason) {
					Window.alert(GwtTeaming.getMessages().codeSplitFailure_ExtensionsConfig());
					ecClient.onUnavailable();
				}
			});
		}
	}
