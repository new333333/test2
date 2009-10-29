/**
 * Copyright (c) 1998-2009 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2009 Novell, Inc. All Rights Reserved.
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
import org.kablink.teaming.gwt.client.service.GwtRpcService;
import org.kablink.teaming.gwt.client.service.GwtRpcServiceAsync;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.AnchorElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DialogBox;
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
	 * 
	 */
	public ExtensionsConfig() {

		fPanel.setStyleName("ss_form");
		
		// Create a hint
		//		Label hintLabel = new Label( GwtTeaming.getMessages().lpeHint() );
		//		hintLabel.addStyleName( "lpeHint" );
		//		fPanel.add( hintLabel );

		
		fPanel.add(new HTML("<br/>"));
		
//		FlowPanel expandPanel = new FlowPanel();
//		expandPanel.setStyleName("ss_expandable_area_title");
//		
//		VerticalPanel vPanel = new VerticalPanel();
//		vPanel.setStyleName("ss_style");
//		vPanel.add(addFileUpload());
//
//		expandPanel.add(vPanel);
//		fPanel.add(expandPanel);
//		
//		fPanel.add(new HTML("<br/>"));
//		
//		fPanel.add(extensionPanelStateText);

		extFlexTable.setCellPadding(2);
		extFlexTable.setStyleName("lpeTableDropZone");
		extFlexTable.getRowFormatter().addStyleName(0, "lpeTableDropZone-highlighted");
		
		extFlexTable.setText(0, 0, GwtTeaming.getMessages().extensionsName());
		extFlexTable.setText(0, 1, GwtTeaming.getMessages().extensionsDesc());
		extFlexTable.setText(0, 2, GwtTeaming.getMessages().extensionsZone());
		extFlexTable.setText(0, 3, GwtTeaming.getMessages().extensionsRemove());
		
		extFlexTable.getCellFormatter().setStyleName(0, 0, "ss_bold");
		extFlexTable.getCellFormatter().setStyleName(0, 1, "ss_bold");
		extFlexTable.getCellFormatter().setStyleName(0, 2, "ss_bold");
		extFlexTable.getCellFormatter().setStyleName(0, 3, "ss_bold");
		
		fPanel.add(extFlexTable);

//		NamedFrame iframe = new NamedFrame("uploadFormTgt");
//		iframe.setVisible(false);
//		fPanel.add(iframe);
//
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
			
			timer.schedule( 500 );
		}
		
		// All composites must call initWidget() in their constructors.
		initWidget( fPanel );
	}
	
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
		
		GwtRpcServiceAsync	gwtRpcService;
		
		// create an async callback to handle the result of the request to get the state:
		AsyncCallback<ExtensionInfoClient[]> callback = new AsyncCallback<ExtensionInfoClient[]>()
		{
			/**
			 * 
			 */
			public void onFailure(Throwable t)
			{
				// display error text if we can't get the tutorial panel state:
				extensionPanelStateText.setText( "Failed to get the AdminModule" );
			}
	
			public void onSuccess(ExtensionInfoClient[] info) {
				// display the tutorial panel state in the label:
				extensionPanelStateText.setText( "" );
				updateTable(info);
			}
		};
	
		extensionPanelStateText.setText( "Waiting" );
		gwtRpcService = (GwtRpcServiceAsync) GWT.create( GwtRpcService.class );
		gwtRpcService.getExtensionInfo(callback);
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
	
	private void addRow(ExtensionInfoClient info, final int row)
	{
		if(extList.contains(info))
			return;
		
		extList.add(info);
		
		final Anchor achorLabel = new Anchor(info.getName());
		
		//final Label nameLabel = new Label(info.getName());
		achorLabel.setStyleName("ss_style");
		
		achorLabel.addClickHandler(new ClickHandler(){
			public void onClick(ClickEvent event) {
				DialogBox box = new DialogBox();
				box.show();
			}});
		
		extFlexTable.setWidget(row, 0, achorLabel );
		extFlexTable.setText(row, 1, info.getDescription());
		extFlexTable.setText(row, 2, info.getZoneId().toString());
		
		Button removeButton = new Button("x");
		removeButton.addStyleDependentName("remove");
		removeButton.addClickHandler(new ClickHandler(){
			public void onClick(ClickEvent event) {
				removeExtension(row);
			}});
		
		extFlexTable.setWidget(row, 3, removeButton);
	}
	
	private void removeExtension(final int row) {
			
			ExtensionInfoClient extInfo = extList.get(row-1);

		
			GwtRpcServiceAsync	gwtRpcService;
			
			// create an async callback to handle the result of the request to get the state:
			AsyncCallback<ExtensionInfoClient[]> callback = new AsyncCallback<ExtensionInfoClient[]>()
			{
				/**
				 * 
				 */
				public void onFailure(Throwable t)
				{
					// display error text if we can't get the tutorial panel state:
					extensionPanelStateText.setText( "Failed to get the AdminModule" );
				}
		
				public void onSuccess(ExtensionInfoClient[] info) {
					// display the tutorial panel state in the label:
					extensionPanelStateText.setText( "" );
					extFlexTable.removeRow(row);
					extList.remove(row-1);
					updateTable(info);
				}
			};
		
			extensionPanelStateText.setText( "Waiting" );
			gwtRpcService = (GwtRpcServiceAsync) GWT.create( GwtRpcService.class );
			gwtRpcService.removeExtension(extInfo.getId(), callback);
		}
	}
