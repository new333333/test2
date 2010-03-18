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
package org.kablink.teaming.gwt.client.util;


/**
 * Class used to communicate information about a selected binder
 * between the WorkspaceTreeControl and its registered ActionHandler's.
 * 
 * @author drfoster@novell.com
 */
public class OnSelectBinderInfo {
	private boolean m_isPermalinkUrl;
	private boolean m_isTrash;
	private Instigator m_instigator = Instigator.OTHER;
	private Long m_binderId;
	private String m_binderUrl;

	// Various marker strings used to recognize the format of a URL.
	private final static String AMPERSAND_FORMAT_MARKER = "a/do?";
	private final static String CAPTIVE_MARKER = "captive";
	private final static String GWT_MARKER = "seen_by_gwt";
	private final static String PERMALINK_MARKER = "view_permalink";

	// Used to identifies the instigator of the Binder selection, if
	// known.
	public enum Instigator {
		BREAD_CRUMB_TREE,
		SIDEBAR_TREE,
		
		OTHER,
	}

	/**
	 * Constructor method.  (1 of 4)
	 * 
	 * @param ti
	 */
	public OnSelectBinderInfo(TreeInfo ti, Instigator instigator) {
		// Always use the final form of the constructor.
		this(Long.parseLong(ti.getBinderId()), ti.getBinderPermalink(), ti.isBinderTrash(), instigator);
	}

	/**
	 * Constructor method.  (2 of 4)
	 * 
	 * @param binderId
	 * @param binderUrl
	 * @param isTrash
	 */
	public OnSelectBinderInfo(String binderId, String binderUrl, boolean isTrash) {
		// Always use the final form of the constructor.
		this(Long.parseLong(binderId), binderUrl, isTrash, Instigator.OTHER);
	}
	
	/**
	 * Constructor method.  (3 of 4)
	 * 
	 * @param binderId
	 * @param binderUrl
	 * @param isTrash
	 */
	public OnSelectBinderInfo(Long binderId, String binderUrl, boolean isTrash) {
		// Always use the final form of the constructor.
		this(binderId, binderUrl, isTrash, Instigator.OTHER);
	}

	/*
	 * Constructor method.  (4 of 4)
	 */
	private OnSelectBinderInfo(Long binderId, String binderUrl, boolean isTrash, Instigator instigator) {
		// Simply store the parameters.
		setBinderId(binderId);
		setBinderUrl(binderUrl);
		m_isTrash = isTrash;
		m_instigator = instigator;
	}

	/*
	 * Performs any URL fixing that's required based on the format of
	 * the URL.
	 */
	private void fixupUrl() {
		// Add a captive marker...
		String marker;
		boolean useAmpersand = (0 < m_binderUrl.indexOf(AMPERSAND_FORMAT_MARKER));
		if (useAmpersand)
			 marker = ("&" + CAPTIVE_MARKER + "=true");
		else marker = ("/" + CAPTIVE_MARKER + "/true");
		if (0 > m_binderUrl.indexOf(marker)) {
			m_binderUrl += marker;
		}
		
		// ...and if the URL is a permalink...
		m_isPermalinkUrl = (0 < m_binderUrl.indexOf(PERMALINK_MARKER));
		if (m_isPermalinkUrl) {
			// ...and it doesn't have a GWT marker...
			if (useAmpersand)
				 marker = ("&" + GWT_MARKER + "=1");
			else marker = ("/" + GWT_MARKER + "/1");
			if (0 > m_binderUrl.indexOf(marker)) {
				// ...add one.
				m_binderUrl += marker;
			}
		}
	}
	
	/**
	 * Returns the Binder ID from this OnSelectBinderInfo object.
	 * 
	 * @return
	 */
	public Long getBinderId() {
		return m_binderId;
	}
	
	/**
	 * Returns the Binder URL from this OnSelectBinderInfo object.
	 * 
	 * @return
	 */
	public String getBinderUrl() {
		return m_binderUrl;
	}

	/**
	 * Returns the instigator of the Binder selection, if known.
	 * 
	 * @return
	 */
	public Instigator getInstigator() {
		return m_instigator;
	}
	/**
	 * Returns true if the Binder URL is a permalink and false
	 * otherwise.
	 * 
	 * @return
	 */
	public boolean isPermalinkUrl() {
		return m_isPermalinkUrl;
	}
	
	/**
	 * Returns true if for a Binder's trash and false otherwise.
	 * 
	 * @return
	 */
	public boolean isTrash() {
		return m_isTrash;
	}
	
	/*
	 * Stores the Binder ID into this OnSelectBinderInfo object.
	 */
	private void setBinderId(Long binderId) {
		m_binderId = binderId;
	}
	
	/**
	 * Stores a Binder URL into this OnSelectBinderInfo object.
	 * 
	 * @param binderUrl
	 */
	public void setBinderUrl(String binderUrl) {
		m_binderUrl = binderUrl;
		fixupUrl();
	}

	/**
	 * Store an instigator for the binder selection.
	 * 
	 * @param instigator
	 */
	public void setInstigator(Instigator instigator) {
		m_instigator = instigator;
	}
}
