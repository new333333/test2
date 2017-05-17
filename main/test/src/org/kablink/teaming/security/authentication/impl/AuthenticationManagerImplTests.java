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
package org.kablink.teaming.security.authentication.impl;

import org.easymock.MockControl;
import org.kablink.teaming.security.authentication.impl.AuthenticationManagerImpl;

import org.kablink.teaming.dao.ProfileDao;
import org.kablink.teaming.domain.IdentityInfo;
import org.kablink.teaming.domain.NoUserByTheNameException;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.security.authentication.UserDoesNotExistException;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;


/**
 * Logic unit test for <code>AuthenticationManagerImpl</code>.
 * 
 * @author Jong Kim
 */
public class AuthenticationManagerImplTests extends AbstractTransactionalJUnit4SpringContextTests {
	MockControl profileDaoControl;
	ProfileDao profileDaoMock;
	User user;
	AuthenticationManagerImpl authMgr;
	
	protected String[] getConfigLocations() {
		mockSetUp();
		//authentication manager now requires Session
		return new String[] {"/com/sitescape/team/security/authentication/impl/applicationContext-authenticationManager.xml"};
	}
	protected void mockSetUp() {
		// Set up mock object and control
		profileDaoControl = MockControl.createControl(ProfileDao.class);
		profileDaoMock = (ProfileDao) profileDaoControl.getMock();
		user = new User(new IdentityInfo());
//		user.setZoneName("testZone");
		user.setName("testUser");
		user.setForeignName("testUser");
		
		// Set up the actual object that we are testing.
		authMgr = new AuthenticationManagerImpl();
		authMgr.setProfileDao(profileDaoMock);
	}

}
