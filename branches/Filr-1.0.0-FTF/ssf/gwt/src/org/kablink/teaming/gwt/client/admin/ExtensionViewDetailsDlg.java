/**
 * Copyright (c) 1998-2010 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2010 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2010 Novell, Inc. All Rights Reserved.
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
import java.util.Date;

import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.rpc.shared.GetExtensionFilesCmd;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * 
 * @author nbjensen
 *
 */
class ExtensionViewDetailsDlg extends PopupPanel implements ClickHandler {

    private FocusWidget m_okBtn;
    private Label	extensionPanelStateText = new Label();
	private FlexTable fileTable;
	private Panel sPanel;

	public ExtensionViewDetailsDlg(ExtensionInfoClient info, boolean autoHide, boolean modal, int xPos, int yPos) {

		super( autoHide, modal );
		
		// Override the style used for PopupPanel
		setStyleName( "teamingDlgBox" );
		
		setAnimationEnabled( true );
		
		setPopupPosition( xPos, yPos );
		
		// Create the header, content and footer of this dialog box.
		createAllDlgContent( info.getTitle(), info ); 
    }

	public Panel createContent(ExtensionInfoClient info) {
		VerticalPanel	mainPanel;
		FlexTable		table;

		mainPanel = new VerticalPanel();
		mainPanel.setStyleName( "teamingDlgBoxContent" );

		String sDateDeployed = info.getDateDeployed();
		
		DateTimeFormat df = DateTimeFormat.getLongDateFormat();
		Date dateDeployed = new Date();
		if(dateDeployed != null)
		{
			sDateDeployed = df.format(dateDeployed);
		}
		
		table = new FlexTable();
		table.setCellSpacing( 2 );
		int row = 0;
		table.setWidget( row, 0, new Label( GwtTeaming.getMessages().extensionsDlgDescription() ) );
		table.setWidget( row, 1, new Label( info.getDescription() ) );
		row++;
		table.setWidget( row, 0, new Label( GwtTeaming.getMessages().extensionsDlgVersion() ) );
		table.setWidget( row, 1, new Label( info.getVersion() ) );
		row++;
		
		table.setWidget( row, 0, new Label( GwtTeaming.getMessages().extensionsDlgDeployed() ) );
		table.setWidget( row, 1, new Label( sDateDeployed ) );
		row++;
		table.setWidget( row, 0, new Label( GwtTeaming.getMessages().extensionsDlgZoneName() ) );
		table.setWidget( row, 1, new Label( info.getZoneName() ) );
		row++;
		table.setWidget( row, 0, new Label( GwtTeaming.getMessages().extensionsDlgId() ) );
		table.setWidget( row, 1, new Label( info.getId() ) );
		table.getWidget(row, 0).setStyleName("marginBottomPoint25em");
		table.getWidget(row, 1).setStyleName("marginBottomPoint25em");
		row++;
		table.setWidget( row, 0, new Label( GwtTeaming.getMessages().extensionsDlgAuthorName() ) );
		table.setWidget( row, 1, new Label( info.getAuthor() ) );
		row++;
		table.setWidget( row, 0, new Label( GwtTeaming.getMessages().extensionsDlgAuthorEmail() ) );
		table.setWidget( row, 1, new Label( info.getAuthorEmail() ) );
		row++;
		table.setWidget( row, 0, new Label( GwtTeaming.getMessages().extensionsDlgAuthorSite() ) );

		String authorSite = info.getAuthorSite();
		if(authorSite != null){
			if(!authorSite.contains("http://")){
				authorSite = "http://"+info.getAuthorSite();
			} 
		}
		
		
		String sDateCreated = info.getDateCreated();
		
		
		Anchor link = new Anchor(info.getAuthorSite(), authorSite);
		table.setWidget( row, 1, link );
		row++;
		table.setWidget( row, 0, new Label( GwtTeaming.getMessages().extensionsDlgCreated() ) );
		table.setWidget( row, 1, new Label( sDateCreated ) );

		mainPanel.add( table );
		mainPanel.add(extensionPanelStateText);
		mainPanel.add(new HTML("<br/>"));
		
		fileTable = new FlexTable();
		fileTable.setCellPadding(2);
		fileTable.getRowFormatter().addStyleName(0, "lpeTableDropZone-highlighted");
		fileTable.setText(0, 0, GwtTeaming.getMessages().extensionsDlgFilesTitle());
		fileTable.getCellFormatter().setStyleName(0, 0, "ss_bold");

		sPanel = new ScrollPanel();
		sPanel.setSize("800px", "240px");
		sPanel.add(fileTable);
		mainPanel.add( sPanel );
		
		return mainPanel;
	}

	/**
	 * Create the header, content and footer for the dialog box.
	 */
	public void createAllDlgContent(
		String	caption,
		final ExtensionInfoClient info ) 					// Where properties used in the dialog are read from and saved to.
	{
		FlowPanel	panel;
		Panel		content;
		Panel		header;
		Panel		footer;
		
		panel = new FlowPanel();

		// Add the header.
		header = createHeader( caption );
		panel.add( header );
		
		// Add the main content of the dialog box.
		content = createContent( info );
		panel.add( content );
		
		// Create the footer.
		footer = createFooter();
		panel.add( footer );
		
		setWidget( panel );
		
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
					buildFileInfo(info.getId(), info.getZoneName());
				}// end run()
			};
			
			timer.schedule( 250 );
		}
		
	}// end createAllDlgContent()

	/*
	 * Create the footer panel for this dialog box.
	 */
	public Panel createFooter()
	{
		FlowPanel panel;
		panel = new FlowPanel();
		
		// Associate this panel with its stylesheet.
		panel.setStyleName( "teamingDlgBoxFooter" );
		
		m_okBtn = new Button( GwtTeaming.getMessages().close() );
		m_okBtn.addClickHandler( this );
		m_okBtn.addStyleName( "teamingButton" );
		panel.add( m_okBtn );
		
		return panel;
	}// end createFooter()
	
	
	/**
	 * Get the Panel that holds the dialog box's header.
	 */
	public Panel createHeader( String caption )
	{
		FlowPanel	flowPanel;
		Label		label;
		
		flowPanel = new FlowPanel();
		flowPanel.setStyleName( "teamingDlgBoxHeader" );

		label = new Label( caption );
		label.addStyleName( "ss_bold" );
		flowPanel.add( label );
		
		return flowPanel;
	}// end createHeader()
	
	
	/*
	 * This method gets called when the user clicks on the ok or cancel button.
	 */
	public void onClick( ClickEvent event )
	{
		Object	source;
		
		// Get the object that was clicked on.
		source = event.getSource();
		
		// Did the user click on ok?
		if ( source == m_okBtn )
		{
			super.hide();
		}
		
	}// end onClick()
	
	
	/**
	 * Show this dialog.
	 */
	public void show()
	{
		// Show this dialog.
		super.show();
		
	}// end show()

	private void buildFileInfo(String id, String zoneName) {
		
		GetExtensionFilesCmd cmd;
		
		// create an async callback to handle the result of the request to get the state:
		AsyncCallback<VibeRpcResponse> callback = new AsyncCallback<VibeRpcResponse>()
		{
			/**
			 * 
			 */
			public void onFailure(Throwable t)
			{
				// display error text if we can't get the tutorial panel state:
				String msg = GwtTeaming.getMessages().extensionsDlgFilesError();
				GwtClientHelper.handleGwtRPCFailure( t, msg );
				extensionPanelStateText.setText( msg );
			}
	
			public void onSuccess( VibeRpcResponse response ) {
				ExtensionFiles paths;
				
				paths = (ExtensionFiles) response.getResponseData();
				
				// display the tutorial panel state in the label:
				extensionPanelStateText.setText( "" );
				updatePaths(paths);
			}
		};
	
		extensionPanelStateText.setText( GwtTeaming.getMessages().extensionsWaiting() );
		cmd = new GetExtensionFilesCmd( id, zoneName );
		GwtClientHelper.executeCommand( cmd, callback );
	}

	protected void updatePaths(ExtensionFiles extFiles) {
		fileTable.clear();
		
		ArrayList<String>paths = extFiles.getResults();
		int cnt = 0;
		for(String path: paths){
			addFileRow(path, cnt+1);  //add 1 to row to account for the header
			cnt++;
		}
	}

	private void addFileRow(String path, int row) {
		fileTable.setText(row, 0, path);
	}
}