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
package org.kablink.teaming.gwt.client.binderviews;

import java.util.List;

import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingImageBundle;
import org.kablink.teaming.gwt.client.GwtTeamingMessages;
import org.kablink.teaming.gwt.client.mainmenu.ToolbarItem;
import org.kablink.teaming.gwt.client.rpc.shared.GetFooterToolbarItemsCmd;
import org.kablink.teaming.gwt.client.rpc.shared.GetToolbarItemsRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.util.BinderInfo;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.widgets.VibeFlowPanel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
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
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlexTable.FlexCellFormatter;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.TextBox;


/**
 * Class used for the content of the footer panel in the binder views.  
 * 
 * @author drfoster@novell.com
 */
public class FooterPanel extends ToolPanelBase {
	private GwtTeamingImageBundle	m_images;		//
	private GwtTeamingMessages		m_messages;		//
	private List<ToolbarItem>		m_toolbarIems;	//
	private ToolbarItem				m_footerTBI;	//
	private VibeFlowPanel			m_fp;			// The panel holding the FooterPanel's contents.
	private VibeFlowPanel			m_dataPanel;	// The panel holding the display of the data, ..., once rendered.
	
	/*
	 * Constructor method.
	 * 
	 * Note that the class constructor is private to facilitate code
	 * splitting.  All instantiations of this object must be done
	 * through its createAsync().
	 */
	private FooterPanel(RequiresResize containerResizer, BinderInfo binderInfo, ToolPanelReady toolPanelReady) {
		// Initialize the super class...
		super(containerResizer, binderInfo, toolPanelReady);

		// ...initialize the data members...
		m_images   = GwtTeaming.getImageBundle();
		m_messages = GwtTeaming.getMessages();
		
		// ...and construct the panel.
		m_fp = new VibeFlowPanel();
		m_fp.addStyleName("vibe-binderViewTools vibe-footerPanel");
		initWidget(m_fp);
		loadPart1Async();
	}

	/**
	 * Loads the FooterPanel split point and returns an instance
	 * of it via the callback.
	 * 
	 * @param containerResizer
	 * @param binderInfo
	 * @param toolPanelReady
	 * @param tpClient
	 */
	public static void createAsync(final ResizeComposite containerResizer, final BinderInfo binderInfo, final ToolPanelReady toolPanelReady, final ToolPanelClient tpClient) {
		GWT.runAsync(FooterPanel.class, new RunAsyncCallback()
		{			
			@Override
			public void onSuccess() {
				FooterPanel fp = new FooterPanel(containerResizer, binderInfo, toolPanelReady);
				tpClient.onSuccess(fp);
			}
			
			@Override
			public void onFailure(Throwable reason) {
				Window.alert(GwtTeaming.getMessages().codeSplitFailure_FooterPanel());
				tpClient.onUnavailable();
			}
		});
	}
	
	/*
	 * Asynchronously construct's the contents of the footer panel.
	 */
	private void loadPart1Async() {
		ScheduledCommand doLoad = new ScheduledCommand() {
			@Override
			public void execute() {
				loadPart1Now();
			}
		};
		Scheduler.get().scheduleDeferred(doLoad);
	}
	
	/*
	 * Synchronously construct's the contents of the footer panel.
	 */
	private void loadPart1Now() {
		final Long folderId = m_binderInfo.getBinderIdAsLong();
		GwtClientHelper.executeCommand(
				new GetFooterToolbarItemsCmd(folderId),
				new AsyncCallback<VibeRpcResponse>() {
			@Override
			public void onFailure(Throwable t) {
				GwtClientHelper.handleGwtRPCFailure(
					t,
					m_messages.rpcFailure_GetFooterToolbarItems(),
					folderId);
			}
			
			@Override
			public void onSuccess(VibeRpcResponse response) {
				// Store the toolbar items and continue loading.
				GetToolbarItemsRpcResponseData responseData = ((GetToolbarItemsRpcResponseData) response.getResponseData());
				m_toolbarIems = responseData.getToolbarItems();
				loadPart2Async();
			}
		});
	}
	
	/*
	 * Asynchronously construct's the contents of the footer panel.
	 */
	private void loadPart2Async() {
		ScheduledCommand doLoad = new ScheduledCommand() {
			@Override
			public void execute() {
				loadPart2Now();
			}
		};
		Scheduler.get().scheduleDeferred(doLoad);
	}
	
	/*
	 * Synchronously construct's the contents of the footer panel.
	 */
	private void loadPart2Now() {
		try {
			// If we don't have a footer toolbar item...
			m_footerTBI = ToolbarItem.getNestedToolbarItem(m_toolbarIems, "ssFooterToolbar");
			if (null == m_footerTBI) {
				// ...we don't display anything in the footer.
				return;
			}
			
			// If we the footer toolbar item doesn't contain a
			// permalink item...
			ToolbarItem permalinkTBI = m_footerTBI.getNestedToolbarItem("permalink");
			if (null == permalinkTBI) {
				// ...we don't display anything in the footer.
				return;
			}
	
			// Create an Anchor for the foot link...
			VibeFlowPanel ap = new VibeFlowPanel();
			ap.addStyleName("vibe-footerAnchorPanel");
			final Anchor a = new Anchor();
			a.addStyleName("vibe-footerAnchor");
			ToolbarItem iCalTBI = m_footerTBI.getNestedToolbarItem("iCalendar");
			String ilString = ((null == iCalTBI) ? "" : (iCalTBI.getTitle() + ", "));
			ilString += permalinkTBI.getTitle();
			InlineLabel il = new InlineLabel(ilString);
			il.addStyleName("vibe-footerAnchorLabel");
			a.getElement().appendChild(il.getElement());
			ap.add(a);
			m_fp.add(ap);
	
			// ...and add the handlers to it.
			a.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					// Have we already rendered the data?
					if (null != m_dataPanel) {
						// Yes!  Toggle its visibility and force the
						// container to resize to reflect the new
						// visibility state of the footer.
						m_dataPanel.setVisible(!m_dataPanel.isVisible());
						panelResized();
					}
					
					else {
						// No, we have yet to render the data!  Render
						// it now.
						renderPermalinksAsync();
					}
				}
			});
			a.addMouseOutHandler(new MouseOutHandler() {
				@Override
				public void onMouseOut(MouseOutEvent event) {
					a.removeStyleName("vibe-footerAnchor-hover");
				}
			});
			a.addMouseOverHandler(new MouseOverHandler() {
				@Override
				public void onMouseOver(MouseOverEvent event) {
					a.addStyleName("vibe-footerAnchor-hover");
				}
			});
		}
		
		finally {
			// Tell who's using this tool panel that it's ready to go.
			toolPanelReady();
		}
	}

	/*
	 * Adds a row with a label and hint to the hint grid.
	 */
	private void renderHintGridRow(FlexTable hintGrid, String label, String hint) {
		int row = hintGrid.getRowCount();
		hintGrid.setWidget(row, 0, new InlineLabel(label));
		hintGrid.setWidget(row, 1, new InlineLabel(hint ));
	}
	
	/*
	 * Asynchronously creates and renders the permalink information.
	 */
	private void renderPermalinksAsync() {
		ScheduledCommand renderPermalinks = new ScheduledCommand() {
			@Override
			public void execute() {
				renderPermalinksNow();
			}
		};
		Scheduler.get().scheduleDeferred(renderPermalinks);
	}

	/*
	 * Synchronously creates and renders the permalink information.
	 */
	private void renderPermalinksNow() {
		// Create a panel to hold the data...
		m_dataPanel = new VibeFlowPanel();
		m_dataPanel.addStyleName("vibe-footerDataPanel");
		m_fp.add(m_dataPanel);

		// ...create a link to close it...
		VibeFlowPanel closerPanel = new VibeFlowPanel();
		closerPanel.addStyleName("vibe-footerDataCloser");
		m_dataPanel.add(closerPanel);
		Anchor closerA = new Anchor();
		closerA.addStyleName("vibe-footerDataCloserAnchor");
		final Image closerImg = new Image(m_images.closeX());
		closerA.getElement().appendChild(closerImg.getElement());
		closerPanel.add(closerA);
		closerA.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				// Hide the data panel and force the container to
				// resize to reflect the new visibility state of the
				// footer.
				m_dataPanel.setVisible(false);
				panelResized();
			}
		});
		closerA.addMouseOutHandler(new MouseOutHandler() {
			@Override
			public void onMouseOut(MouseOutEvent event) {
				closerImg.setResource(m_images.closeX());
			}
		});
		closerA.addMouseOverHandler(new MouseOverHandler() {
			@Override
			public void onMouseOver(MouseOverEvent event) {
				closerImg.setResource(m_images.closeXMouseOver());
			}
		});

		// ...create a panel and grid to hold the links data...
		VibeFlowPanel linksPanel = new VibeFlowPanel();
		linksPanel.addStyleName("vibe-footerDataLinksPanel");
		m_dataPanel.add(linksPanel);
		FlexTable linksGrid = new FlexTable();
		linksGrid.setCellPadding(6);
		linksGrid.setCellSpacing(2);
		linksGrid.setBorderWidth(1);
		linksGrid.addStyleName("vibe-footerDataLinksGrid");
		linksPanel.add(linksGrid);
		FlexCellFormatter cf = linksGrid.getFlexCellFormatter();

		// ...add a row for the permalink...
		ToolbarItem permalinkTBI = m_footerTBI.getNestedToolbarItem("permalink");
		VibeFlowPanel rowDataPanel = renderRow(linksGrid, cf, m_messages.vibeBinderFooter_Permalink());
		renderRowLink(rowDataPanel, permalinkTBI.getUrl());

		// ...if there are simple names defined on the binder...
		ToolbarItem simpleNamesTBI = m_footerTBI.getNestedToolbarItem("simpleNames");
		if (null != simpleNamesTBI) {
			// ...scan them...
			int c = Integer.parseInt(simpleNamesTBI.getQualifierValue("simple.count"));
			String simplePrefix = simpleNamesTBI.getQualifierValue("simple.prefix");
			for (int i = 0; i < c; i += 1) {
				// ...adding a link for each to the table...
				String simpleName = simpleNamesTBI.getQualifierValue("simple." + i + ".name");
				renderRowLink(rowDataPanel, (simplePrefix + simpleName));
			}

			// ...scan them again...
			String hostName = simpleNamesTBI.getQualifierValue("simple.host");
			rowDataPanel = renderRow(linksGrid, cf, m_messages.vibeBinderFooter_EmailAddresses());
			for (int i = 0; i < c; i += 1) {
				// ...adding an email for each to the table...
				String emailAddress = simpleNamesTBI.getQualifierValue("simple." + i + ".email");
				renderRowText(rowDataPanel, (emailAddress + "@" + hostName));
			}
		}

		// ...if there's an RSS URL defined...
		ToolbarItem rssTBI = m_footerTBI.getNestedToolbarItem("subscribeRSS");
		if (null != rssTBI) {
			// ...add a link opener for it...
			rowDataPanel = renderRow(linksGrid, cf, m_messages.vibeBinderFooter_RSSUrl());
			renderRowLinkOpener(rowDataPanel, rssTBI.getUrl());
		}

		// ...if there's an atom URL defined...
		ToolbarItem atomTBI = m_footerTBI.getNestedToolbarItem("subscribeAtom");
		if (null != atomTBI) {
			// ...add a link opener for it...
			rowDataPanel = renderRow(linksGrid, cf, m_messages.vibeBinderFooter_AtomUrl());
			renderRowLinkOpener(rowDataPanel, atomTBI.getUrl());
		}

		// ...if there's a WebDAV URL defined...
		ToolbarItem webDavTBI = m_footerTBI.getNestedToolbarItem("webdavUrl");
		if (null != webDavTBI) {
			// ...add a link for it...
			rowDataPanel = renderRow(linksGrid, cf, m_messages.vibeBinderFooter_WebDAVUrl());
			renderRowLink(rowDataPanel, webDavTBI.getUrl());
		}

		// ...and if there's an iCal URL defined...
		ToolbarItem iCalTBI = m_footerTBI.getNestedToolbarItem("iCalendar");
		if (null != iCalTBI) {
			// ...add a link for it.
			rowDataPanel = renderRow(linksGrid, cf, m_messages.vibeBinderFooter_iCalUrl());
			renderRowLink(rowDataPanel, iCalTBI.getUrl());
		}
		
		// Create a grid to hold the keys (i.e., definitions) of what's
		// in the above table...
		linksPanel.add(new HTML("<br /><br />"));
		FlexTable hintGrid = new FlexTable();
		hintGrid.setCellPadding(6);
		hintGrid.setCellSpacing(6);
		hintGrid.setBorderWidth(0);
		hintGrid.addStyleName("vibe-footerDataHintGrid");
		linksPanel.add(hintGrid);
		cf = hintGrid.getFlexCellFormatter();

		// ...add a header string to the key grid...
		InlineLabel hintHeader = new InlineLabel(m_messages.vibeBinderFooter_KeyHeader());
		hintHeader.addStyleName("vibe-footerDataHintGridHeader");
		hintGrid.setWidget(0, 0, hintHeader);
		cf.setColSpan(    0, 0, 2 );

		// ...add rows for each item type...
		renderHintGridRow(hintGrid, m_messages.vibeBinderFooter_Permalink(),      m_messages.vibeBinderFooter_PermalinkHint()     );
		renderHintGridRow(hintGrid, m_messages.vibeBinderFooter_EmailAddresses(), m_messages.vibeBinderFooter_EmailAddressesHint());
		renderHintGridRow(hintGrid, m_messages.vibeBinderFooter_WebDAVUrl(),      m_messages.vibeBinderFooter_WebDAVUrlHint()     );
		renderHintGridRow(hintGrid, m_messages.vibeBinderFooter_iCalUrl(),        m_messages.vibeBinderFooter_iCalUrlHint()       );
		renderHintGridRow(hintGrid, m_messages.vibeBinderFooter_RSSUrl(),         m_messages.vibeBinderFooter_RSSUrlHint()        );

		// ...and add a footer with further explanations.
		VibeFlowPanel keyFooterPanel = new VibeFlowPanel();
		keyFooterPanel.addStyleName("vibe-footerDataHintGridFooter");
		linksPanel.add(keyFooterPanel);
		keyFooterPanel.add(new InlineLabel(m_messages.vibeBinderFooter_KeyFooter()));

		// Finally, force the container to resize to reflect the
		// expanded footer.
		panelResized();
	}

	/*
	 * Renders row in the grid and returns it data panel.
	 */
	private VibeFlowPanel renderRow(FlexTable grid, FlexCellFormatter cf, String label) {
		// Get the row's index...
		int row = grid.getRowCount();

		// ...add the cell for the row's label...
		grid.setWidget(         row, 0, new InlineLabel(label)        );
		cf.setWidth(            row, 0, "10%"                         );
		cf.setWordWrap(         row, 0, false                         );
		cf.setVerticalAlignment(row, 0, HasVerticalAlignment.ALIGN_TOP);

		// ...add the cell for the row's data...
		VibeFlowPanel rowDataPanel = new VibeFlowPanel();
		grid.setWidget(         row, 1, rowDataPanel                  );
		cf.setWidth(            row, 1, "90%"                         );
		cf.setWordWrap(         row, 1, false                         );
		cf.setVerticalAlignment(row, 1, HasVerticalAlignment.ALIGN_TOP);

		// ...and return the panel that's to contain the row's data.
		return rowDataPanel;
	}
	
	/*
	 * Renders a link in the row data panel.
	 */
	private void renderRowLink(VibeFlowPanel rowDataPanel, final String url) {
		// Define a panel with an INPUT that contains the link.  We use
		// and INPUT for this to facilitate the user being able to copy
		// the link to the clipboard.
		VibeFlowPanel linkPanel = new VibeFlowPanel();
		rowDataPanel.add(linkPanel);
		final TextBox linkInput = new TextBox();
		linkInput.addStyleName("vibe-footerDataLinksInput");
		linkPanel.add(linkInput);
		linkInput.setValue(url);
		linkInput.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				linkInput.selectAll();
			}
		});
		linkInput.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				// If the user happens to change the link (since it's
				// an INPUT, they CAN edit it), we simply restore the
				// INPUT back to its initial value.
				linkInput.setValue(url);
				linkInput.selectAll();
			}
		});
	}
	
	/*
	 * Renders a link to open a URL in a new window in the row data panel.
	 */
	private void renderRowLinkOpener(VibeFlowPanel rowDataPanel, final String url) {
		// Define a panel with a link to launch the URL in a new
		// window.
		VibeFlowPanel linkPanel = new VibeFlowPanel();
		rowDataPanel.add(linkPanel);
		final Anchor linkA = new Anchor();
		linkA.addStyleName("vibe-footerDataLinksOpenerAnchor");
		linkPanel.add(linkA);
		InlineLabel linkLabel = new InlineLabel(url);
		linkLabel.addStyleName("vibe-footerDataLinksOpenerLabel");
		linkA.getElement().appendChild(linkLabel.getElement());
		linkA.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				Window.open(
					url,
					"teamingSubscribe",
					("directories=no,location=no,menubar=yes,resizable=yes,scrollbars=yes,status=no,toolbar=no,width=" + 800 + ",height=" + 600));
			}
		});
		linkA.addMouseOutHandler(new MouseOutHandler() {
			@Override
			public void onMouseOut(MouseOutEvent event) {
				linkA.removeStyleName("vibe-footerDataLinksOpenerAnchor-hover");
			}
		});
		linkA.addMouseOverHandler(new MouseOverHandler() {
			@Override
			public void onMouseOver(MouseOverEvent event) {
				linkA.addStyleName("vibe-footerDataLinksOpenerAnchor-hover");
			}
		});
	}
	
	/*
	 * Renders some text in the row data panel.
	 */
	private void renderRowText(VibeFlowPanel rowDataPanel, String text) {
		// Define a panel with the text in a SPAN.
		VibeFlowPanel textPanel = new VibeFlowPanel();
		rowDataPanel.add(textPanel);
		InlineLabel textLabel = new InlineLabel(text);
		textLabel.addStyleName("vibe-footerDataLinksText");
		textPanel.add(textLabel);
	}
	
	/**
	 * Called from the binder view to allow the panel to do any
	 * work required to reset itself.
	 * 
	 * Implements ToolPanelBase.resetPanel()
	 */
	@Override
	public void resetPanel() {
		// Reset the widgets and reload the footer.
		m_fp.clear();
		loadPart1Async();
	}
}
