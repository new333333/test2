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

import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingDataTableImageBundle;
import org.kablink.teaming.gwt.client.GwtTeamingMessages;
import org.kablink.teaming.gwt.client.rpc.shared.SaveBinderRegionStateCmd;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.util.BinderInfo;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.widgets.VibeFlowPanel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.AttachEvent;
import com.google.gwt.event.logical.shared.AttachEvent.Handler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.RequiresResize;


/**
 * Class used for the content of the description in the binder views.  
 * 
 * @author drfoster@novell.com
 */
public class DescriptionPanel extends ToolPanelBase {
	private Anchor							m_expanderAnchor;		// The Anchor containing the widget that allows the description to be expanded or collapsed.
	private boolean							m_descriptionExpanded;	// true -> The description should be expanded.  false -> The description should be collapsed.
	private boolean							m_descriptionIsHTML;	// true -> The content of m_description is HTML.  false -> It's plain text.
	private boolean							m_panelReady;			// true -> The panel is fully constructed and running.  false -> The panel is in the process of being constructed.
	private GwtTeamingDataTableImageBundle	m_images;				// Access to Vibe's data table image bundle.
	private GwtTeamingMessages				m_messages;				// Access to Vibe's localized message resources.
	private Image							m_expanderImg;			// The Image contain the expand/collapse image in m_expanderAnchor.
	private String							m_description;			// The binder's description.
	private VibeFlowPanel					m_contentPanel;			// The panel holding the description HTML widgets.
	private VibeFlowPanel					m_expanderPanel;		// The panel holding the description panel's expander widgets.
	private VibeFlowPanel					m_fp;					// The panel holding the DescriptionPanel's contents.

	// The following defines the height a description can be before we
	// allow the user to expand/collapse it.
	private final static int EXPANDABLE_THRESHOLD = 225;
	
	/*
	 * Constructor method.
	 * 
	 * Note that the class constructor is private to facilitate code
	 * splitting.  All instantiations of this object must be done
	 * through its createAsync().
	 */
	private DescriptionPanel(RequiresResize containerResizer, BinderInfo binderInfo, ToolPanelReady toolPanelReady) {
		// Initialize the super class...
		super(containerResizer, binderInfo, toolPanelReady);
		
		// ...initialize the data members...
		m_images              = GwtTeaming.getDataTableImageBundle();
		m_messages            = GwtTeaming.getMessages();
		m_description         = binderInfo.getBinderDesc();
		m_descriptionIsHTML   = binderInfo.isBinderDescHTML();
		m_descriptionExpanded = binderInfo.isBinderDescExpanded();
		
		// ...and construct the panel.
		m_fp = new VibeFlowPanel();
		initWidget(m_fp);
		loadPart1Async();
	}

	/**
	 * Loads the DescriptionPanel split point and returns an instance
	 * of it via the callback.
	 * 
	 * @param containerResizer
	 * @param binderInfo
	 * @param toolPanelReady
	 * @param tpClient
	 */
	public static void createAsync(final RequiresResize containerResizer, final BinderInfo binderInfo, final ToolPanelReady toolPanelReady, final ToolPanelClient tpClient) {
		GWT.runAsync(DescriptionPanel.class, new RunAsyncCallback()
		{			
			@Override
			public void onSuccess() {
				DescriptionPanel fp = new DescriptionPanel(containerResizer, binderInfo, toolPanelReady);
				tpClient.onSuccess(fp);
			}
			
			@Override
			public void onFailure(Throwable reason) {
				Window.alert(GwtTeaming.getMessages().codeSplitFailure_DescriptionPanel());
				tpClient.onUnavailable();
			}
		});
	}

	/*
	 * Collapses the description panel to only display a portion of the description.
	 */
	private void doCollapse() {
		m_expanderAnchor.setTitle(m_messages.vibeDataTable_Alt_ExpandDescription());
		m_expanderImg.setResource(m_images.expandDescription());
		m_expanderAnchor.getElement().setAttribute("n-state", "expand");
		m_contentPanel.addStyleName("vibe-descriptionContentClipped");
		persistStateAsync("collapsed");
		panelResized();
	}
	
	/*
	 * Expands the description panel to it's full height.
	 */
	private void doExpand() {
		m_expanderAnchor.setTitle(m_messages.vibeDataTable_Alt_CollapseDescription());
		m_expanderImg.setResource(m_images.collapseDescription());
		m_expanderAnchor.getElement().setAttribute("n-state", "collapse");
		m_contentPanel.removeStyleName("vibe-descriptionContentClipped");
		persistStateAsync("expanded");
		panelResized();
	}
	
	/*
	 * Asynchronously construct's the contents of the description panel.
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
	 * Synchronously construct's the contents of the description panel.
	 */
	private void loadPart1Now() {
		// Do we have a description?
		if (GwtClientHelper.hasString(m_description)) {
			// Yes!  We need to render it.  Add the initial panel
			// styles.
			m_fp.addStyleName("vibe-binderViewTools vibe-descriptionPanel");

			// Create the widgets that allow the user to
			// expand/collapse the description.  These are initially
			// hidden, but will be shown if the display of the
			// description is large enough to warrant it.
			m_expanderPanel = new VibeFlowPanel();
			m_expanderPanel.addStyleName("vibe-descriptionExpanderBar");
			m_expanderAnchor = new Anchor();
			final Element expandAE = m_expanderAnchor.getElement();
			ImageResource image;
			String title;
			String state;
			if (m_descriptionExpanded) {
				image = m_images.collapseDescription();
				title = m_messages.vibeDataTable_Alt_CollapseDescription();
				state = "collapse";
			}
			else {
				image = m_images.expandDescription();
				title = m_messages.vibeDataTable_Alt_ExpandDescription();
				state = "expand";
			}
			m_expanderAnchor.addStyleName("vibe-descriptionExpanderAnchor");
			m_expanderAnchor.setTitle(title);
			expandAE.setAttribute("n-state", state);
			m_expanderImg = new Image(image); 
			m_expanderImg.addStyleName("vibe-descriptionExpanderImg");
			expandAE.appendChild(m_expanderImg.getElement());
			m_expanderAnchor.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					if (expandAE.getAttribute("n-state").equals("expand"))
					     doExpand();
					else doCollapse();
				}
			});
			m_expanderPanel.add(m_expanderAnchor);
			m_expanderPanel.setVisible(false);	// The expander is hidden until we decide whether we need to show it or not.  See sizeDescriptionNow() below.
			m_fp.add(m_expanderPanel);

			// Create the widgets that display the description (in HTML
			// or plain text) itself.
			m_contentPanel = new VibeFlowPanel();
			m_contentPanel.addStyleName("vibe-descriptionContent");
			Element cpE = m_contentPanel.getElement();
			if (m_descriptionIsHTML) {
				m_contentPanel.addStyleName("vibe-descriptionHTML");
				cpE.setInnerHTML(m_description);
			}
			else {
				m_contentPanel.addStyleName("vibe-descriptionText");
				cpE.setInnerText(m_description);
			}
			m_contentPanel.addAttachHandler(new Handler() {
				@Override
				public void onAttachOrDetach(AttachEvent event) {
					// Is this an attach event? 
					if (event.isAttached()) {
						// Yes!  Analyze the size of the rendered
						// description.  We have to do this post
						// rendering because we only show the expander
						// it displays larger than our threshold.
						sizeDescriptionAsync();
					}
				}
			});
			m_fp.add(m_contentPanel);
		}
		
		else {
			// No, we don't have a description!  Simply tell who's
			// using this tool panel that it's ready to go.
			toolPanelReady();
		}
	}

	/*
	 * Asynchronously stores the expansion state of a description.
	 */
	private void persistStateAsync(final String state) {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				persistStateNow(state);
			}
		});
	}
	
	/*
	 * Synchronously stores the expansion state of a description.
	 */
	private void persistStateNow(final String state) {
		final Long binderId = m_binderInfo.getBinderIdAsLong();
		GwtClientHelper.executeCommand(
				new SaveBinderRegionStateCmd(binderId, "descriptionRegion", state),
				new AsyncCallback<VibeRpcResponse>() {
			@Override
			public void onFailure(Throwable t) {
				GwtClientHelper.handleGwtRPCFailure(
					t,
					m_messages.rpcFailure_SaveBinderRegionState(),
					binderId);
			}
			
			@Override
			public void onSuccess(VibeRpcResponse response) {
				// Nothing to do.
			}
		});
	}
	
	/**
	 * Called from the binder view to allow the panel to do any
	 * work required to reset itself.
	 * 
	 * Implements ToolPanelBase.resetPanel()
	 */
	@Override
	public void resetPanel() {
		// Reset the widgets and reload the description.
		m_fp.clear();
		m_fp.removeStyleName("vibe-binderViewTools vibe-DescriptionPanel");
		m_panelReady = true;
		loadPart1Async();
	}

	/*
	 * Asynchronously manages the size of the description.
	 */
	private void sizeDescriptionAsync() {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				sizeDescriptionNow();
			}
		});
	}
	
	/*
	 * Synchronously manages the size of the description.
	 */
	private void sizeDescriptionNow() {
		// If the description rendered larger than our threshold...
		int descHeight = m_contentPanel.getOffsetHeight();
		if (EXPANDABLE_THRESHOLD < descHeight) {
			// ...enable its expansion and collapsing.
			m_expanderPanel.setVisible(true);
			if (!m_descriptionExpanded) {
				m_contentPanel.addStyleName("vibe-descriptionContentClipped");
			}
		}

		// If this tool panel is just now becoming ready...
		if (!m_panelReady) {
			// ...tell who's using it that it's ready to go.
			toolPanelReady();
			m_panelReady = true;
		}
	}
}
