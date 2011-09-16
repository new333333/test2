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
package org.kablink.teaming.relevance.impl;


import java.io.IOException;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.kablink.teaming.domain.Attachment;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.DefinableEntity;
import org.kablink.teaming.util.AllModulesInjected;


/**
 * Implementation of class for relevance engine integration.
 * 
 * @author drfoster@novell.com
 * 
 */
public class RelevanceEngine implements org.kablink.teaming.relevance.Relevance
{
	private Log m_relevanceLogger = LogFactory.getLog(RelevanceEngine.class);
	
	/*
	 * Class constructor.
	 */
	public RelevanceEngine() {
	}

	/**
	 * Adds a Attachment to the relevance engine.
	 *
	 * @param binder
	 * @param entity
	 * @param att
	 * 
	 * @return
	 */
	public String addAttachment(Binder binder, DefinableEntity entity, Attachment att) {
		return null;
	}
	
	/**
	 * Returns a Set<Attachment> that are relevant to a file based on
	 * that file's UUID.
	 * 
	 * @param bs
	 * @param relevanceUUID
	 * 
	 * @return
	 */
	public Set<Attachment> getRelevantAttachments(AllModulesInjected bs, String relevanceUUID) throws IOException {
		return null;
	}
	
	/**
	 * Returns the Attachment matching the given UUID.
	 * 
	 * @param bs
	 * @param relevanceUUID
	 * 
	 * @return
	 */
	public Attachment getAttachment(AllModulesInjected bs, String relevanceUUID) {
		return null;
	}
	
	/**
	 * Returns the log4j logger object to log information about
	 * relevance integration.
	 * 
	 * @return
	 */
	public Log getRelevanceLogger() {
		return m_relevanceLogger;
	}

	/**
	 * Always returns false since no relevance engines are supported in
	 * Kablink Teaming. 
	 */
	public Boolean isRelevanceEnabled() {
		m_relevanceLogger.debug("RelevanceEngine.m_isRelevanceEnabled( 'Relevance not supported in Kablink Teaming.' ):  false");
		return Boolean.FALSE;
	}

	/**
	 * Removes the Brownstone file associated with a Teaming
	 * Attachment.
	 * 
	 * @param att
	 */
	public void removeAttachment(final Attachment att) {
	}
	
	/**
	 * Removes the a relevance file from Brownstone.
	 * 
	 * @param uuid
	 */
	public void removeAttachmentUUID(String uuid) {	
	}
}
