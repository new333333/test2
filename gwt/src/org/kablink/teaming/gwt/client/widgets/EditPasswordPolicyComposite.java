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
package org.kablink.teaming.gwt.client.widgets;

import java.util.ArrayList;
import java.util.List;

import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingMessages;
import org.kablink.teaming.gwt.client.event.EventHelper;
import org.kablink.teaming.gwt.client.event.TeamingEvents;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.xml.client.Document;
import com.google.web.bindery.event.shared.HandlerRegistration;

/**
 * Composite that contains a password policy editor.
 * 
 * @author drfoster@novell.com
 */
@SuppressWarnings("unused")
public class EditPasswordPolicyComposite extends ResizeComposite {
	private Document					m_policyXml;				// The XML containing the password policy being edited.
	private GwtTeamingMessages			m_messages;					// Access to Vibe's messages.
	private List<HandlerRegistration>	m_registeredEventHandlers;	// Event handlers that are currently registered.
	private VibeFlowPanel				m_rootContent;				// The main panel containing the composite's content.
	
	// The following defines the TeamingEvents that are handled by
	// this class.  See EventHelper.registerEventHandlers() for how
	// this array is used.
	private static final TeamingEvents[] REGISTERED_EVENTS = new TeamingEvents[] {
//!		...this needs to be implemented...
	};

	/*
	 * Constructor method.
	 * 
	 * Note that the class constructor is private to facilitate code
	 * splitting.  All instantiations of this object must be done
	 * through its createAsync().
	 */
	private EditPasswordPolicyComposite() {
		// Initialize the super class...
		super();
	
		// ...initialize everything else...
		m_messages = GwtTeaming.getMessages();
		
		// ...and create the composite's content.
		createContent();
		initWidget(m_rootContent);
	}

	/**
	 * Creates the content for the composite.
	 */
	public void createContent() {
		// Create the main content panel for the composite. 
		m_rootContent = new VibeFlowPanel();
		m_rootContent.addStyleName("vibe-editPasswordPolicyComposite-rootPanel");
	}

	/**
	 * Returns the policy XML as it currently stands.
	 * 
	 * @return
	 */
	public Document getPolicyXml() {
		return m_policyXml;
	}
	
	/**
	 * Called when the composite is attached to the document.
	 * 
	 * Overrides the Widget.onAttach() method.
	 */
	@Override
	public void onAttach() {
		// Attach the widget and register the event handlers.
		super.onAttach();
		registerEvents();
	}
	
	/**
	 * Called when the composite is detached from the document.
	 * 
	 * Overrides the Widget.onDetach() method.
	 */
	@Override
	public void onDetach() {
		// Let the widget detach and then unregister our event
		// handlers.
		super.onDetach();
		unregisterEvents();
	}

	/*
	 * Asynchronously populates the contents of the composite.
	 */
	private void populateCompositeAsync() {
		GwtClientHelper.deferCommand(
			new ScheduledCommand() {
				@Override
				public void execute() {
					populateCompositeNow();
				}
			});
	}
	
	/*
	 * Synchronously populates the contents of the composite.
	 */
	private void populateCompositeNow() {
		// Clear anything currently in the composite.
		m_rootContent.clear();
		
//!		...this needs to be implemented...
		m_rootContent.add(new InlineLabel("EditPasswordPolicyComposite:  ...this needs to be implemented..."));
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

		// Does the extender want any events registered?
		if ((null != REGISTERED_EVENTS) && (0 < REGISTERED_EVENTS.length)) {
			// Yes!  If the list of registered events is empty...
			if (m_registeredEventHandlers.isEmpty()) {
				// ...register the events.
				EventHelper.registerEventHandlers(
					GwtTeaming.getEventBus(),
					REGISTERED_EVENTS,
					this,
					m_registeredEventHandlers);
			}
		}
	}

	/*
	 * Asynchronously runs the given instance of the edit password
	 * policy composite.
	 */
	private static void runCompositeAsync(final EditPasswordPolicyComposite eppComposite, final Document policyXml) {
		GwtClientHelper.deferCommand(
			new ScheduledCommand() {
				@Override
				public void execute() {
					eppComposite.runCompositeNow(policyXml);
				}
			});
	}
	
	/*
	 * Synchronously runs the given instance of the edit password
	 * policy composite.
	 */
	private void runCompositeNow(final Document policyXml) {
		// Store the parameters...
		m_policyXml = policyXml;
		
		// ...and start populating the composite.
		populateCompositeAsync();
	}

	/*
	 * Unregisters any global event handlers that may be registered.
	 */
	private void unregisterEvents() {
		// If we have a non-empty list of registered events...
		if (GwtClientHelper.hasItems(m_registeredEventHandlers)) {
			// ...unregister them.  (Note that this will also empty the
			// ...list.)
			EventHelper.unregisterEventHandlers(m_registeredEventHandlers);
		}
	}

	/*
	 * Asynchronously validates the password policy being edited.
	 */
	private static void validatePasswordPolicyAsync(final EditPasswordPolicyComposite eppComposite, final ValidatePasswordPolicyCallback vppCallback) {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				eppComposite.validatePasswordPolicyNow(vppCallback);
			}
		});
	}
	
	/*
	 * Synchronously validates the password policy being edited.
	 */
	private void validatePasswordPolicyNow(final ValidatePasswordPolicyCallback vppCallback) {
//!		...this needs to be implemented...
		vppCallback.onValid();
	}
	
	
	/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */
	/* The following code is used to load the split point containing */
	/* the edit password policy composite and perform some operation */
	/* on it.                                                        */
	/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */
	
	/**
	 * Callback interface to interact with the edit password policy
	 * composite asynchronously after it loads. 
	 */
	public interface EditPasswordPolicyCompositeClient {
		void onSuccess(EditPasswordPolicyComposite eppComposite);
		void onUnavailable();
	}
	
	/**
	 * Callback interface used to validate the password policy as it
	 * currently stands.
	 */
	public interface ValidatePasswordPolicyCallback {
		void onValid();
		void onInvalid();
	}

	/*
	 * Asynchronously loads the EditPasswordPolicyComposite and performs some
	 * operation against the code.
	 */
	private static void doAsyncOperation(
			// Parameters used to create a composite.
			final EditPasswordPolicyCompositeClient	eppCompositeClient,
			
			// Parameters used to run a composite.
			final EditPasswordPolicyComposite	runComposite,
			final Document						policyXml,
		
			// Parameters used to validate the password policy in a
			// composite.
			final EditPasswordPolicyComposite		validateComposite,
			final ValidatePasswordPolicyCallback	vppCallback) {
		GWT.runAsync(EditPasswordPolicyComposite.class, new RunAsyncCallback() {
			@Override
			public void onFailure(Throwable reason) {
				Window.alert(GwtTeaming.getMessages().codeSplitFailure_EditPasswordPolicyComposite());
				if (null != eppCompositeClient) {
					eppCompositeClient.onUnavailable();
				}
			}

			@Override
			public void onSuccess() {
				// Is this a request to create a composite?
				if (null != eppCompositeClient) {
					// Yes!  Create the composite and tell the caller
					// that it has been created.
					EditPasswordPolicyComposite composite = new EditPasswordPolicyComposite();
					eppCompositeClient.onSuccess(composite);
				}
				
				// No, it's not a request to create a composite!
				// Is it a request to run one?
				else if (null != runComposite) {
					// Yes!  Run it.
					runCompositeAsync(runComposite, policyXml);
				}
				
				// No, it's not a request to run a composite either!
				// Is it a request to validate the password policy
				// contained in one?
				else if (null != validateComposite) {
					// Yes!  Validate it.
					validatePasswordPolicyAsync(validateComposite, vppCallback);
				}
			}
		});
	}
	
	/**
	 * Loads the EditPasswordPolicyComposite split point and returns an instance of it
	 * via the callback.
	 * 
	 * @param eppCompositeClient
	 */
	public static void createAsync(EditPasswordPolicyCompositeClient eppCompositeClient) {
		doAsyncOperation(eppCompositeClient, null, null, null, null);
	}
	
	/**
	 * Initializes and shows the edit password policy composite.
	 * 
	 * @param eppComposite
	 * @param policyXml
	 */
	public static void initAndShow(EditPasswordPolicyComposite eppComposite, Document policyXml) {
		doAsyncOperation(null, eppComposite, policyXml, null, null);
	}
	
	/**
	 * Validates the password policy contained in the composite.
	 * 
	 * @param eppComposite
	 * @param vppCallback
	 */
	public static void validatePasswordPolicy(EditPasswordPolicyComposite eppComposite, ValidatePasswordPolicyCallback vppCallback) {
		doAsyncOperation(null, null, null, eppComposite, vppCallback);
	}
}
