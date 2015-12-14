/**
 * Copyright (c) 1998-2014 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2014 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2014 Novell, Inc. All Rights Reserved.
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingImageBundle;
import org.kablink.teaming.gwt.client.GwtTeamingMessages;
import org.kablink.teaming.gwt.client.mainmenu.ToolbarItem;
import org.kablink.teaming.gwt.client.rpc.shared.GetFooterToolbarItemsCmd;
import org.kablink.teaming.gwt.client.rpc.shared.GetToolbarItemsRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.util.BinderInfo;
import org.kablink.teaming.gwt.client.util.EntityId;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.widgets.VibeFlowPanel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
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
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.TextBox;

/**
 * Class used for the content of the footer panel in the views.  
 * 
 * @author drfoster@novell.com
 */
public class FooterPanel extends ToolPanelBase {
	private boolean					m_isFilr;		//
	private FooterPanelVisibility	m_fpVisibility;	//
	private GwtTeamingImageBundle	m_images;		//
	private GwtTeamingMessages		m_messages;		//
	private List<ToolbarItem>		m_toolbarIems;	//
	private ToolbarItem				m_footerTBI;	//
	private VibeFlowPanel			m_closerPanel;	// The panel holding the close button in the upper right corner of the footer's display.
	private VibeFlowPanel			m_fp;			// The panel holding the FooterPanel's contents.
	private VibeFlowPanel			m_dataPanel;	// The panel holding the display of the data, ..., once rendered.
	
	// The following manage the strings used by the footer.  The map is
	// loaded with the appropriate strings from the resource bundle for
	// the footer based on whether it's in Filr or Vibe mode each time
	// it is instantiated.  See initFooterStrings().
	private enum StringIds{
		CAPTION_ATOM,
		CAPTION_EMAIL_ADDRESSES,
		CAPTION_FILE_DOWNLOAD,
		CAPTION_ICAL,
		CAPTION_PERMALINK,
		CAPTION_RSS,
		CAPTION_WEBDAV,

		KEY_ATOM,
		KEY_EMAIL_ADDRESSES,
		KEY_FILE_DOWNLOAD,
		KEY_HEADER,
		KEY_FOOTER,
		KEY_ICAL,
		KEY_PERMALINK,
		KEY_RSS,
		KEY_WEBDAV_ENTRY,
		KEY_WEBDAV_FOLDER,
	}
	private Map<StringIds, String> m_strMap;
	
	/*
	 * Constructor method.
	 * 
	 * Note that the class constructor is private to facilitate code
	 * splitting.  All instantiations of this object must be done
	 * through its createAsync().
	 */
	private FooterPanel(RequiresResize containerResizer, BinderInfo binderInfo, EntityId entityId, ToolPanelReady toolPanelReady, FooterPanelVisibility	fpVisibility) {
		// Initialize the super class...
		super(containerResizer, binderInfo, entityId, toolPanelReady);
		
		// ...store the footer specific parameters...
		m_fpVisibility = fpVisibility;

		// ...initialize the data members...
		m_images   = GwtTeaming.getImageBundle();
		m_messages = GwtTeaming.getMessages();
		m_isFilr   = GwtClientHelper.isLicenseFilr();
		initFooterStrings();
		
		// ...and construct the panel.
		m_fp = new VibeFlowPanel();
		m_fp.addStyleName("vibe-binderViewTools vibe-footerPanel");
		initWidget(m_fp);
		loadPart1Async();
	}

	/**
	 * Returns the data panel containing the footer.
	 * 
	 * @return
	 */
	public Panel getDataPanel() {
		return m_dataPanel;
	}
	
	/**
	 * Hides the footer's closer panel.
	 */
	public void hideCloser() {
		// If we have a closer panel...
		if (null != m_closerPanel) {
			// ...hide it.
			m_closerPanel.setVisible(false);
		}
	}
	
	/*
	 * Initialize the Map of the strings used by the footer based
	 * whether we're in Filr or Vibe mode.
	 */
	private void initFooterStrings() {
		// Start with an empty map...
		if (null == m_strMap)
		     m_strMap = new HashMap<StringIds, String>();
		else m_strMap.clear();

		// ...and if we're in Filr mode...
		if (m_isFilr) {
			// ...load the Filr specific strings...
			m_strMap.put(StringIds.CAPTION_ATOM,            m_messages.vibeBinderFooter_Filr_AtomUrl());
			m_strMap.put(StringIds.CAPTION_EMAIL_ADDRESSES, m_messages.vibeBinderFooter_Filr_EmailAddresses());
			m_strMap.put(StringIds.CAPTION_FILE_DOWNLOAD,   m_messages.vibeBinderFooter_Filr_FileDownload());
			m_strMap.put(StringIds.CAPTION_ICAL,            m_messages.vibeBinderFooter_Filr_iCalUrl());
			m_strMap.put(StringIds.CAPTION_PERMALINK,       m_messages.vibeBinderFooter_Filr_Permalink());
			m_strMap.put(StringIds.CAPTION_RSS,             m_messages.vibeBinderFooter_Filr_RSSUrl());
			m_strMap.put(StringIds.CAPTION_WEBDAV,          m_messages.vibeBinderFooter_Filr_WebDAVUrl());

			m_strMap.put(StringIds.KEY_ATOM,                m_messages.vibeBinderFooter_Filr_AtomUrl());
			m_strMap.put(StringIds.KEY_EMAIL_ADDRESSES,     m_messages.vibeBinderFooter_Filr_EmailAddressesHint());
			m_strMap.put(StringIds.KEY_FILE_DOWNLOAD,       m_messages.vibeBinderFooter_Filr_FileDownloadHint());
			m_strMap.put(StringIds.KEY_HEADER,              m_messages.vibeBinderFooter_Filr_KeyHeader());
			m_strMap.put(StringIds.KEY_FOOTER,              m_messages.vibeBinderFooter_Filr_KeyFooter());
			m_strMap.put(StringIds.KEY_ICAL,                m_messages.vibeBinderFooter_Filr_iCalUrlHint());
			m_strMap.put(StringIds.KEY_PERMALINK,           m_messages.vibeBinderFooter_Filr_PermalinkHint());
			m_strMap.put(StringIds.KEY_RSS,                 m_messages.vibeBinderFooter_Filr_RSSUrlHint());
			m_strMap.put(StringIds.KEY_WEBDAV_ENTRY,        m_messages.vibeBinderFooter_Filr_WebDAVUrlHintEntry());
			m_strMap.put(StringIds.KEY_WEBDAV_FOLDER,       m_messages.vibeBinderFooter_Filr_WebDAVUrlHintFolder());
		}
		
		else {
			// ...otherwise, load the Vibe specific strings...
			m_strMap.put(StringIds.CAPTION_ATOM,            m_messages.vibeBinderFooter_Vibe_AtomUrl());
			m_strMap.put(StringIds.CAPTION_EMAIL_ADDRESSES, m_messages.vibeBinderFooter_Vibe_EmailAddresses());
			m_strMap.put(StringIds.CAPTION_FILE_DOWNLOAD,   m_messages.vibeBinderFooter_Vibe_FileDownload());
			m_strMap.put(StringIds.CAPTION_ICAL,            m_messages.vibeBinderFooter_Vibe_iCalUrl());
			m_strMap.put(StringIds.CAPTION_PERMALINK,       m_messages.vibeBinderFooter_Vibe_Permalink());
			m_strMap.put(StringIds.CAPTION_RSS,             m_messages.vibeBinderFooter_Vibe_RSSUrl());
			m_strMap.put(StringIds.CAPTION_WEBDAV,          m_messages.vibeBinderFooter_Vibe_WebDAVUrl());

			m_strMap.put(StringIds.KEY_ATOM,                m_messages.vibeBinderFooter_Vibe_AtomUrl());
			m_strMap.put(StringIds.KEY_EMAIL_ADDRESSES,     m_messages.vibeBinderFooter_Vibe_EmailAddressesHint());
			m_strMap.put(StringIds.KEY_FILE_DOWNLOAD,       m_messages.vibeBinderFooter_Vibe_FileDownloadHint());
			m_strMap.put(StringIds.KEY_HEADER,              m_messages.vibeBinderFooter_Vibe_KeyHeader());
			m_strMap.put(StringIds.KEY_FOOTER,              m_messages.vibeBinderFooter_Vibe_KeyFooter());
			m_strMap.put(StringIds.KEY_ICAL,                m_messages.vibeBinderFooter_Vibe_iCalUrlHint());
			m_strMap.put(StringIds.KEY_PERMALINK,           m_messages.vibeBinderFooter_Vibe_PermalinkHint());
			m_strMap.put(StringIds.KEY_RSS,                 m_messages.vibeBinderFooter_Vibe_RSSUrlHint());
			m_strMap.put(StringIds.KEY_WEBDAV_ENTRY,        m_messages.vibeBinderFooter_Vibe_WebDAVUrlHintEntry());
			m_strMap.put(StringIds.KEY_WEBDAV_FOLDER,       m_messages.vibeBinderFooter_Vibe_WebDAVUrlHintFolder());
		}
	}

	/*
	 * Asynchronously construct's the contents of the footer panel.
	 */
	private void loadPart1Async() {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				loadPart1Now();
			}
		});
	}
	
	/*
	 * Synchronously construct's the contents of the footer panel.
	 */
	private void loadPart1Now() {
		// Get an EntityId for what the footer is displaying
		// information for.
		EntityId entityId;
		if (null == m_binderInfo)
		     entityId = m_entityId;
		else entityId = m_binderInfo.buildEntityId();
		
		// Can we get the footer toolbar items for that EntityId?
		final Long itemId = entityId.getEntityId();
		GwtClientHelper.executeCommand(
				new GetFooterToolbarItemsCmd(entityId),
				new AsyncCallback<VibeRpcResponse>() {
			@Override
			public void onFailure(Throwable t) {
				GwtClientHelper.handleGwtRPCFailure(
					t,
					m_messages.rpcFailure_GetFooterToolbarItems(),
					itemId);
			}
			
			@Override
			public void onSuccess(VibeRpcResponse response) {
				// Yes!  Store the toolbar items and continue loading.
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
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				loadPart2Now();
			}
		});
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
	
			// Create an Anchor for the footer link...
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
					setFooterVisibleAsync((null == m_dataPanel) || (!(m_dataPanel.isVisible())) || (null != m_fpVisibility));
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
		if (GwtClientHelper.hasString(label) && GwtClientHelper.hasString(hint)) {
			int row = hintGrid.getRowCount();
			hintGrid.setWidget(row, 0, new InlineLabel(label));
			hintGrid.setWidget(row, 1, new InlineLabel(hint ));
		}
	}
	
	/*
	 * Asynchronously creates and renders the permalink information.
	 */
	private void renderPermalinksAsync() {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				renderPermalinksNow();
			}
		});
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
		m_closerPanel = new VibeFlowPanel();
		m_closerPanel.addStyleName("vibe-footerDataCloser");
		m_dataPanel.add(m_closerPanel);
		Anchor closerA = new Anchor();
		closerA.addStyleName("vibe-footerDataCloserAnchor");
		final Image closerImg = new Image(m_images.closeX());
		closerA.getElement().appendChild(closerImg.getElement());
		m_closerPanel.add(closerA);
		closerA.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				// Hide the data panel and force the container to
				// resize to reflect the new visibility state of the
				// footer.
				m_dataPanel.setVisible(false);
				if (null == m_fpVisibility)
				     panelResized();
				else m_fpVisibility.footerPanelVisible(false);
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
		linksGrid.setCellPadding(5);
		linksGrid.setCellSpacing(0);
		linksGrid.setBorderWidth(0);
		linksGrid.addStyleName("vibe-footerDataLinksGrid");
		linksPanel.add(linksGrid);
		FlexCellFormatter cf = linksGrid.getFlexCellFormatter();

		// ...add a row for the permalink...
		ToolbarItem permalinkTBI = m_footerTBI.getNestedToolbarItem("permalink");
		VibeFlowPanel rowDataPanel = renderRow(linksGrid, cf, m_strMap.get(StringIds.CAPTION_PERMALINK));
		renderRowLink(rowDataPanel, permalinkTBI.getUrl());

		// ...if we're not in Filr mode...
		if (!m_isFilr) {
			// ...and if there are simple names defined on a binder...
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
				rowDataPanel = renderRow(linksGrid, cf, m_strMap.get(StringIds.CAPTION_EMAIL_ADDRESSES));
				for (int i = 0; i < c; i += 1) {
					// ...adding an email for each to the table...
					String emailAddress = simpleNamesTBI.getQualifierValue("simple." + i + ".email");
					renderRowText(rowDataPanel, (emailAddress + "@" + hostName));
				}
			}
		}

		// ...if there's a file download URL defined...
		ToolbarItem fileDownloadTBI    = m_footerTBI.getNestedToolbarItem("fileDownload");
		boolean		hasFileDownloadUrl = (null != fileDownloadTBI);
		if (hasFileDownloadUrl) {
			// ...add a link for it...
			rowDataPanel = renderRow(linksGrid, cf, m_strMap.get(StringIds.CAPTION_FILE_DOWNLOAD));
			renderRowLink(rowDataPanel, fileDownloadTBI.getUrl());
		}

		// ...if we not in Filr mode...
		if (!m_isFilr) {
			// ...if there's an RSS URL defined...
			ToolbarItem rssTBI = m_footerTBI.getNestedToolbarItem("subscribeRSS");
			if (null != rssTBI) {
				// ...add a link opener for it...
				rowDataPanel = renderRow(linksGrid, cf, m_strMap.get(StringIds.CAPTION_RSS));
				renderRowLinkOpener(rowDataPanel, rssTBI.getUrl());
			}
	
			// ...if there's an atom URL defined...
			ToolbarItem atomTBI = m_footerTBI.getNestedToolbarItem("subscribeAtom");
			if (null != atomTBI) {
				// ...add a link opener for it...
				rowDataPanel = renderRow(linksGrid, cf, m_strMap.get(StringIds.CAPTION_ATOM));
				renderRowLinkOpener(rowDataPanel, atomTBI.getUrl());
			}
		}

		// ...if there's a WebDAV URL defined...
		ToolbarItem webDavTBI    = m_footerTBI.getNestedToolbarItem("webdavUrl");
		boolean		hasWebDAVUrl = (null != webDavTBI);
		if (hasWebDAVUrl) {
			// ...add a link for it...
			rowDataPanel = renderRow(linksGrid, cf, m_strMap.get(StringIds.CAPTION_WEBDAV));
			renderRowLink(rowDataPanel, webDavTBI.getUrl());
		}

		// ...and if there's an iCal URL defined...
		ToolbarItem iCalTBI = m_footerTBI.getNestedToolbarItem("iCalendar");
		if (null != iCalTBI) {
			// ...add a link for it.
			rowDataPanel = renderRow(linksGrid, cf, m_strMap.get(StringIds.CAPTION_ICAL));
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
		InlineLabel hintHeader = new InlineLabel(m_strMap.get(StringIds.KEY_HEADER));
		hintHeader.addStyleName("vibe-footerDataHintGridHeader");
		hintGrid.setWidget(0, 0, hintHeader);
		cf.setColSpan(    0, 0, 2 );

		// ...add rows for each item type...
		renderHintGridRow(    hintGrid, m_strMap.get(StringIds.CAPTION_PERMALINK),     m_strMap.get(StringIds.KEY_PERMALINK)      );
		if (hasFileDownloadUrl) {
			renderHintGridRow(hintGrid, m_strMap.get(StringIds.CAPTION_FILE_DOWNLOAD), m_strMap.get(StringIds.KEY_FILE_DOWNLOAD)  );
		}
		renderHintGridRow(    hintGrid, m_strMap.get(StringIds.CAPTION_ICAL),          m_strMap.get(StringIds.KEY_EMAIL_ADDRESSES));
		if (hasWebDAVUrl) {
			StringIds webDavId = ((null != m_binderInfo) ? StringIds.KEY_WEBDAV_FOLDER : StringIds.KEY_WEBDAV_ENTRY);
			renderHintGridRow(hintGrid, m_strMap.get(StringIds.CAPTION_WEBDAV),        m_strMap.get(webDavId)                     );
		}
		renderHintGridRow(    hintGrid, m_strMap.get(StringIds.CAPTION_ICAL),          m_strMap.get(StringIds.KEY_ICAL)           );
		if (!m_isFilr) {
			renderHintGridRow(hintGrid, m_strMap.get(StringIds.CAPTION_RSS),           m_strMap.get(StringIds.KEY_RSS)            );
		}

		// ...and add a footer with further explanations if necessary.
		String footerHint = m_strMap.get(StringIds.KEY_FOOTER);
		if (GwtClientHelper.hasString(footerHint)) {
			VibeFlowPanel keyFooterPanel = new VibeFlowPanel();
			keyFooterPanel.addStyleName("vibe-footerDataHintGridFooter");
			linksPanel.add(keyFooterPanel);
			keyFooterPanel.add(new InlineLabel(footerHint));
		}

		// Finally, force the container to resize to reflect the
		// expanded footer.
		if (null == m_fpVisibility)
		     panelResized();
		else m_fpVisibility.footerPanelVisible(true);
	}

	/*
	 * Renders row in the grid and returns it data panel.
	 */
	private VibeFlowPanel renderRow(FlexTable grid, FlexCellFormatter cf, String label) {
		if (GwtClientHelper.hasString(label)) {
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
		return null;
	}
	
	/*
	 * Renders a link in the row data panel.
	 */
	private void renderRowLink(VibeFlowPanel rowDataPanel, final String url) {
		if (null != rowDataPanel) {
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
	}
	
	/*
	 * Renders a link to open a URL in a new window in the row data panel.
	 */
	private void renderRowLinkOpener(VibeFlowPanel rowDataPanel, final String url) {
		if (null != rowDataPanel) {
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
	}
	
	/*
	 * Renders some text in the row data panel.
	 */
	private void renderRowText(VibeFlowPanel rowDataPanel, String text) {
		if (null != rowDataPanel) {
			// Define a panel with the text in a SPAN.
			VibeFlowPanel textPanel = new VibeFlowPanel();
			rowDataPanel.add(textPanel);
			InlineLabel textLabel = new InlineLabel(text);
			textLabel.addStyleName("vibe-footerDataLinksText");
			textPanel.add(textLabel);
		}
	}
	
	/**
	 * Called from the views to allow the panel to do any work required
	 * to reset itself.
	 * 
	 * Implements ToolPanelBase.resetPanel()
	 */
	@Override
	public void resetPanel() {
		// Reset the widgets and reload the footer.
		m_fp.clear();
		loadPart1Async();
	}

	/*
	 * Asynchronously expands the footer.
	 */
	private void setFooterVisibleAsync(final boolean show) {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				setFooterVisibleNow(show);
			}
		});
	}
	
	/*
	 * Synchronously expands the footer.
	 */
	private void setFooterVisibleNow(final boolean show) {
		// Have we already rendered the data?
		if (null != m_dataPanel) {
			// Yes!  Toggle its visibility and force the
			// container to resize to reflect the new
			// visibility state of the footer.
			m_dataPanel.setVisible(show);
			if (null == m_fpVisibility)
			     panelResized();
			else m_fpVisibility.footerPanelVisible(show);
		}
		
		else {
			// No, we have yet to render the data!  Render
			// it now.
			renderPermalinksAsync();
		}
	}
	
	
	/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */
	/* The following code is used to load the split point containing */
	/* the footer panel and perform some operation on it.            */
	/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */

	/*
	 * Loads the FooterPanel split point for a binder and returns an
	 * instance of it via the callback.
	 */
	private static void doAsyncOperation(
			// createAsync() parameters.
			final ResizeComposite		containerResizer,
			final BinderInfo			binderInfo,
			final EntityId				entityId,
			final ToolPanelReady		toolPanelReady,
			final ToolPanelClient		tpClient,
			final FooterPanelVisibility	fpVisibility,
			
			// expandFooter parameters.
			final FooterPanel expandFP) {
		GWT.runAsync(FooterPanel.class, new RunAsyncCallback()
		{			
			@Override
			public void onSuccess() {
				if (null == expandFP) {
					FooterPanel fp = new FooterPanel(containerResizer, binderInfo, entityId, toolPanelReady, fpVisibility);
					tpClient.onSuccess(fp);
				}
				
				else {
					expandFP.setFooterVisibleAsync(true);
				}
			}
			
			@Override
			public void onFailure(Throwable reason) {
				Window.alert(GwtTeaming.getMessages().codeSplitFailure_FooterPanel());
				tpClient.onUnavailable();
			}
		});
	}
	
	/**
	 * Loads the FooterPanel split point for a binder and returns an
	 * instance of it via the callback.
	 * 
	 * @param containerResizer
	 * @param binderInfo
	 * @param toolPanelReady
	 * @param tpClient
	 * @param fpVisibility
	 */
	public static void createAsync(final ResizeComposite containerResizer, final BinderInfo binderInfo, final ToolPanelReady toolPanelReady, final ToolPanelClient tpClient, FooterPanelVisibility fpVisibility) {
		doAsyncOperation(containerResizer, binderInfo, null, toolPanelReady, tpClient, fpVisibility, null);
	}
	
	/**
	 * Loads the FooterPanel split point for a binder and returns an
	 * instance of it via the callback.
	 * 
	 * @param containerResizer
	 * @param binderInfo
	 * @param toolPanelReady
	 * @param tpClient
	 */
	public static void createAsync(final ResizeComposite containerResizer, final BinderInfo binderInfo, final ToolPanelReady toolPanelReady, final ToolPanelClient tpClient) {
		// Always use the previous form of the method.
		createAsync(containerResizer, binderInfo, toolPanelReady, tpClient, null);
	}
	
	/**
	 * Loads the FooterPanel split point for an entity and returns an
	 * instance of it via the callback.
	 * 
	 * @param containerResizer
	 * @param entityId
	 * @param toolPanelReady
	 * @param tpClient
	 * @param fpVisibility
	 */
	public static void createAsync(final ResizeComposite containerResizer, final EntityId entityId, final ToolPanelReady toolPanelReady, final ToolPanelClient tpClient, FooterPanelVisibility fpVisibility) {
		doAsyncOperation(containerResizer, null, entityId, toolPanelReady, tpClient, fpVisibility, null);
	}
	
	/**
	 * Loads the FooterPanel split point for an entity and returns an
	 * instance of it via the callback.
	 * 
	 * @param containerResizer
	 * @param entityId
	 * @param toolPanelReady
	 * @param tpClient
	 */
	public static void createAsync(final ResizeComposite containerResizer, final EntityId entityId, final ToolPanelReady toolPanelReady, final ToolPanelClient tpClient) {
		// Always use the previous form of the method.
		createAsync(containerResizer, entityId, toolPanelReady, tpClient, null);
	}
	
	/**
	 * Expands the footer if it is not already expanded.
	 * 
	 * @param fp
	 */
	public static void expandFooter(FooterPanel fp) {
		doAsyncOperation(null, null, null, null, null, null, fp);
	}
}
