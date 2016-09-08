/**
 * Copyright (c) 1998-2013 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2013 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2013 Novell, Inc. All Rights Reserved.
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

import java.util.ArrayList;
import java.util.List;

import org.kablink.teaming.gwt.client.binderviews.FolderEntryComposite.FolderEntryCompositeClient;
import org.kablink.teaming.gwt.client.event.EventHelper;
import org.kablink.teaming.gwt.client.event.TeamingEvents;
import org.kablink.teaming.gwt.client.EditCanceledHandler;
import org.kablink.teaming.gwt.client.EditSuccessfulHandler;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.rpc.shared.SaveFolderEntryDlgPositionCmd;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.util.ViewFolderEntryInfo;
import org.kablink.teaming.gwt.client.widgets.DlgBox;
import org.kablink.teaming.gwt.client.widgets.VibeFlowPanel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.Panel;
import com.google.web.bindery.event.shared.HandlerRegistration;

/**
 * Implements 'Folder Entry' dialog..
 *  
 * @author drfoster@novell.com
 */
public class FolderEntryDlg extends DlgBox {
	private List<HandlerRegistration>	m_registeredEventHandlers;	// Event handlers that are currently registered.
	private VibeFlowPanel				m_fp;						// The panel that holds the dialog's contents.
	private ViewFolderEntryInfo			m_vfei;						// The folder entry the dialog is being running against.
	private ViewReady					m_viewReady;				//

	// Default height and width for the dialog.
	private final static int	DEFAULT_HEIGHT	=  768;
	private final static int	DEFAULT_WIDTH	= 1024;
	
	// The following defines the TeamingEvents that are handled by
	// this class.  See EventHelper.registerEventHandlers() for how
	// this array is used.
	private TeamingEvents[] m_registeredEvents = new TeamingEvents[] {
	};

	/*
	 * Class constructor.
	 * 
	 * Note that the class constructor is private to facilitate code
	 * splitting.  All instantiations of this object must be done
	 * through its createAsync().
	 */
	private FolderEntryDlg() {
		// Initialize the superclass...
		super(false, true, DlgButtonMode.Close);

		// ...and create the dialog's content.
		addStyleName("vibe-feDlg-box");
		createAllDlgContent(
			"",										// Caption displayed by FolderEntryComposite, not the dialog.
			new EditSuccessfulHandler() {			// The dialog's EditSuccessfulHandler.
				@Override
				public boolean editSuccessful(Object callbackData) {
					savePositionAsync();
					return true;
				}
			},
			new EditCanceledHandler() {				// The dialog's EditCanceledHandler.
				@Override
				public boolean editCanceled() {
					savePositionAsync();
					return true;
				}
			},
			null);									// Create callback data.  Unused. 
	}

	/**
	 * Creates all the controls that make up the dialog.
	 * 
	 * Implements the DlgBox.createContent() abstract method.
	 * 
	 * @param callbackData (unused)
	 * 
	 * @return
	 */
	@Override
	public Panel createContent(Object callbackData) {
		// Create a panel to hold the dialog's content...
		m_fp = new VibeFlowPanel();
		m_fp.addStyleName("vibe-feDlg-panel");
		
		// ...and return it.
		return m_fp;
	}

	/**
	 * Unused.
	 * 
	 * Implements the DlgBox.getDataFromDlg() abstract method.
	 * 
	 * @return
	 */
	@Override
	public Object getDataFromDlg() {
		// Unused.
		return "";
	}

	/**
	 * Returns the Widget to give the focus to.
	 * 
	 * Implements the DlgBox.getFocusWidget() abstract method.
	 * 
	 * @return
	 */
	@Override
	public FocusWidget getFocusWidget() {
		return null;
	}

	/**
	 * Called when the data table is attached.
	 * 
	 * Overrides Widget.onAttach()
	 */
	@Override
	public void onAttach() {
		// Let the widget attach and then register our event handlers.
		super.onAttach();
		registerEvents();
	}
	
	/**
	 * Called when the data table is detached.
	 * 
	 * Overrides Widget.onDetach()
	 */
	@Override
	public void onDetach() {
		// Let the widget detach and then unregister our event
		// handlers.
		super.onDetach();
		unregisterEvents();
	}
	
	/*
	 * Asynchronously populates the contents of the dialog.
	 */
	private void populateDlgAsync() {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				populateDlgNow();
			}
		});
	}
	
	/*
	 * Synchronously populates the contents of the dialog.
	 */
	private void populateDlgNow() {
		FolderEntryComposite.createAsync(
			new FolderEntryCompositeClient() {
				@Override
				public void onUnavailable() {
					// Nothing to do.  Error handled in asynchronous
					// provider.
				}
	
				@Override
				public void onSuccess(FolderEntryComposite fec) {
					// Calculate the size and position for the dialog.
					final int x = m_vfei.getX(); int cx = m_vfei.getCX();
					final int y = m_vfei.getY(); int cy = m_vfei.getCY();
					if (((-1) == cx) || ((-1) == cy)) {
						cx = DEFAULT_WIDTH;
						cy = DEFAULT_HEIGHT;
					}

					// Clear the dialog's content panel, resize it and
					// add the composite.
					m_fp.clear();
					m_fp.setHeight(cy + "px");
					m_fp.setWidth( cx + "px");
					m_fp.add(fec);

					// Finally, show it.
					if (((-1) == x) || ((-1) == y)) {
						center();
					}
					
					else {
						setPopupPositionAndShow(new PositionCallback() {
							@Override
							public void setPosition(int offsetWidth, int offsetHeight) {
								setPopupPosition(x, y);
							}
						});
					}
				}
			},
			this,
			m_vfei,
			m_viewReady);
	}
	
	/*
	 * Registers any global event handlers that need to be registered.
	 */
	private void registerEvents() {
		// If we having allocated a list to track events we've
		// registered yet...
		if (null == m_registeredEventHandlers) {
			// ...allocate one now.
			m_registeredEventHandlers = new ArrayList<HandlerRegistration>();
		}

		// If the list of registered events is empty...
		if (m_registeredEventHandlers.isEmpty()) {
			// ...register the events.
			EventHelper.registerEventHandlers(
				GwtTeaming.getEventBus(),
				m_registeredEvents,
				this,
				m_registeredEventHandlers);
		}
	}

	/*
	 * Asynchronously runs the given instance of the folder entry
	 * dialog.
	 */
	private static void runDlgAsync(final FolderEntryDlg feDlg, final ViewFolderEntryInfo vfei, final ViewReady viewReady) {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				feDlg.runDlgNow(vfei, viewReady);
			}
		});
	}
	
	/*
	 * Synchronously runs the given instance of the folder entry
	 * dialog.
	 */
	private void runDlgNow(ViewFolderEntryInfo vfei, ViewReady viewReady) {
		// Store the parameters...
		m_vfei      = vfei;
		m_viewReady = viewReady;
		
		// ...and start populating the dialog.
		populateDlgAsync();
	}

	/*
	 * Asynchronously saves the position of the view dialog in the
	 * user's preferences.
	 */
	private void savePositionAsync() {
		// Get the dialog's current position...
		final int x  = getAbsoluteLeft();
		final int y  = getAbsoluteTop();
		final int cx = m_fp.getOffsetWidth();
		final int cy = m_fp.getOffsetHeight();
		
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				// ...and save it.
				savePositionNow(x, y, cx, cy);
			}
		});
	}
	
	/*
	 * Synchronously saves the position of the view dialog in the
	 * user's preferences.
	 */
	private void savePositionNow(int x, int y, int cx, int cy) {
		SaveFolderEntryDlgPositionCmd cmd = new SaveFolderEntryDlgPositionCmd(x, y, cx, cy);
		GwtClientHelper.executeCommand(cmd, new AsyncCallback<VibeRpcResponse>() {
			@Override
			public void onFailure(Throwable caught) {
				GwtClientHelper.handleGwtRPCFailure(
					caught,
					GwtTeaming.getMessages().rpcFailure_SaveFolderEntryDlgPosition());
			}

			@Override
			public void onSuccess(VibeRpcResponse response) {
				// Nothing to do.
			}
		});
	}

	/*
	 * Unregisters any global event handlers that may be registered.
	 */
	private void unregisterEvents() {
		// If we have a non-empty list of registered events...
		if ((null != m_registeredEventHandlers) && (!(m_registeredEventHandlers.isEmpty()))) {
			// ...unregister them.  (Note that this will also empty the
			// ...list.)
			EventHelper.unregisterEventHandlers(m_registeredEventHandlers);
		}
	}

	
	/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */
	/* The following code is used to load the split point containing */
	/* the folder entry dialog and perform some operation on it.     */
	/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */
	
	/**
	 * Callback interface to interact with the folder entry dialog
	 * asynchronously after it loads. 
	 */
	public interface FolderEntryDlgClient {
		void onSuccess(FolderEntryDlg feDlg);
		void onUnavailable();
	}

	/*
	 * Asynchronously loads the FolderEntryDlg and performs some
	 * operation against the code.
	 */
	private static void doAsyncOperation(
			// Required creation parameters.
			final FolderEntryDlgClient feDlgClient,
			
			// initAndShow parameters,
			final FolderEntryDlg		feDlg,
			final ViewFolderEntryInfo	vfei,
			final ViewReady         	viewReady) {
		GWT.runAsync(FolderEntryDlg.class, new RunAsyncCallback() {
			@Override
			public void onFailure(Throwable reason) {
				Window.alert(GwtTeaming.getMessages().codeSplitFailure_FolderEntryDlg());
				if (null != feDlgClient) {
					feDlgClient.onUnavailable();
				}
			}

			@Override
			public void onSuccess() {
				// Is this a request to create a dialog?
				if (null != feDlgClient) {
					// Yes!  Create it and return it via the callback.
					FolderEntryDlg feDlg = new FolderEntryDlg();
					feDlgClient.onSuccess(feDlg);
				}
				
				else {
					// No, it's not a request to create a dialog!  It
					// must be a request to run an existing one.  Run
					// it.
					runDlgAsync(feDlg, vfei, viewReady);
				}
			}
		});
	}
	
	/**
	 * Loads the FolderEntryDlg split point and returns an instance of it
	 * via the callback.
	 * 
	 * @param feDlgClient
	 */
	public static void createAsync(FolderEntryDlgClient feDlgClient) {
		doAsyncOperation(feDlgClient, null, null, null);
	}
	
	/**
	 * Initializes and shows the folder entry dialog.
	 * 
	 * @param feDlg
	 * @param vfei
	 */
	public static void initAndShow(FolderEntryDlg feDlg, ViewFolderEntryInfo vfei, ViewReady viewReady) {
		doAsyncOperation(null, feDlg, vfei, viewReady);
	}
}
