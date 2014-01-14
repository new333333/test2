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
package org.kablink.teaming.module.conferencing.impl;

import org.kablink.teaming.module.conferencing.ConferencingException;
import org.kablink.teaming.module.conferencing.ConferencingModule;
import org.kablink.teaming.module.conferencing.MeetingInfo;
import org.kablink.teaming.module.impl.CommonDependencyInjection;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

public class ConferencingModuleImpl extends CommonDependencyInjection
		implements ConferencingModule, ConferencingModuleImplMBean, InitializingBean, DisposableBean {

	private String m_url;
	private boolean m_enabled;

	public void setConferencingURL(String url) {
		m_url = url;
	}

	public String getConferencingURL() {
		return m_url;
	}

	public void setEnabled(boolean enabled) {
		m_enabled = enabled;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
	}

	@Override
	public void destroy() throws Exception {
	}

	/* (non-Javadoc)
	 * @see org.kablink.teaming.module.conferencing.ConferencingModule#isEnabled()
	 */
	@Override
	public boolean isEnabled() {
		return m_enabled;
	}

	/* (non-Javadoc)
	 * @see org.kablink.teaming.module.conferencing.ConferencingModule#login(java.lang.String, java.lang.String)
	 */
	@Override
	public void login(String user, String pwd) throws ConferencingException {
		throw new ConferencingException(ConferencingException.FUNCTION_NOT_IMPLEMENTED, "Function is not implemented.");
	}

	/* (non-Javadoc)
	 * @see org.kablink.teaming.module.conferencing.ConferencingModule#logout(java.lang.String)
	 */
	@Override
	public void logout(String user) throws ConferencingException {
		throw new ConferencingException(ConferencingException.FUNCTION_NOT_IMPLEMENTED, "Function is not implemented.");
	}

	/* (non-Javadoc)
	 * @see org.kablink.teaming.module.conferencing.ConferencingModule#scheduleMeeting(java.lang.String, org.kablink.teaming.module.conferencing.MeetingInfo)
	 */
	@Override
	public MeetingInfo scheduleMeeting(String user, MeetingInfo meetingInfo)
			throws ConferencingException {
		throw new ConferencingException(ConferencingException.FUNCTION_NOT_IMPLEMENTED, "Function is not implemented.");
	}

	/* (non-Javadoc)
	 * @see org.kablink.teaming.module.conferencing.ConferencingModule#startMeeting(java.lang.String, org.kablink.teaming.module.conferencing.MeetingInfo)
	 */
	@Override
	public String startMeeting(String user, MeetingInfo meetingInfo)
			throws ConferencingException {
		throw new ConferencingException(ConferencingException.FUNCTION_NOT_IMPLEMENTED, "Function is not implemented.");
	}

	/* (non-Javadoc)
	 * @see org.kablink.teaming.module.conferencing.ConferencingModule#isMeetingRunning(java.lang.String)
	 */
	@Override
	public boolean isMeetingRunning(String user) throws ConferencingException {
		throw new ConferencingException(ConferencingException.FUNCTION_NOT_IMPLEMENTED, "Function is not implemented.");
	}

	/* (non-Javadoc)
	 * @see org.kablink.teaming.module.conferencing.ConferencingModule#endMeeting(java.lang.String)
	 */
	@Override
	public void endMeeting(String user) throws ConferencingException {
		throw new ConferencingException(ConferencingException.FUNCTION_NOT_IMPLEMENTED, "Function is not implemented.");
	}
}
