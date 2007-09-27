/**
 * The contents of this file are subject to the Common Public Attribution License Version 1.0 (the "CPAL");
 * you may not use this file except in compliance with the CPAL. You may obtain a copy of the CPAL at
 * http://www.opensource.org/licenses/cpal_1.0. The CPAL is based on the Mozilla Public License Version 1.1
 * but Sections 14 and 15 have been added to cover use of software over a computer network and provide for
 * limited attribution for the Original Developer. In addition, Exhibit A has been modified to be
 * consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT WARRANTY OF ANY KIND,
 * either express or implied. See the CPAL for the specific language governing rights and limitations
 * under the CPAL.
 * 
 * The Original Code is ICEcore. The Original Developer is SiteScape, Inc. All portions of the code
 * written by SiteScape, Inc. are Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * 
 * 
 * Attribution Information
 * Attribution Copyright Notice: Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by ICEcore]
 * Attribution URL: [www.icecore.com]
 * Graphic Image as provided in the Covered Code [web/docroot/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are defined in the CPAL as a
 * work which combines Covered Code or portions thereof with code not governed by the terms of the CPAL.
 * 
 * 
 * SITESCAPE and the SiteScape logo are registered trademarks and ICEcore and the ICEcore logos
 * are trademarks of SiteScape, Inc.
 */
package com.sitescape.team.security.authentication.impl;

import org.easymock.MockControl;
import org.springframework.test.AbstractTransactionalDataSourceSpringContextTests;

import com.sitescape.team.dao.ProfileDao;
import com.sitescape.team.domain.NoUserByTheNameException;
import com.sitescape.team.domain.User;
import com.sitescape.team.security.authentication.UserDoesNotExistException;
import com.sitescape.team.security.authentication.impl.AuthenticationManagerImpl;


/**
 * Logic unit test for <code>AuthenticationManagerImpl</code>.
 * 
 * @author Jong Kim
 */
public class AuthenticationManagerImplTests extends AbstractTransactionalDataSourceSpringContextTests {
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
		user = new User();
//		user.setZoneName("testZone");
		user.setName("testUser");
		
		// Set up the actual object that we are testing.
		authMgr = new AuthenticationManagerImpl();
		authMgr.setProfileDao(profileDaoMock);
	}
	
	public void testAuthenticateOk() {
		// Define expected behavior of the mock object.
		profileDaoControl.reset();
		profileDaoMock.findUserByName("testUser", "testZone");
		profileDaoControl.setReturnValue(user);
		profileDaoControl.replay();
		
		user.setPassword("testPassword");

		// Execute the method being tested.
		User authenticatedUser = authMgr.authenticate("testZone", "testUser", "testPassword", false, false, null);
		assertEquals(user, authenticatedUser);
		
		// Verifies that all expectations have been met.
		profileDaoControl.verify();
	}
	
	public void testAuthenticateUserDoesNotExistException() {
		// Define expected behavior of the mock object. 
		profileDaoControl.reset();
		profileDaoMock.findUserByName("testUser", "testZone");
		profileDaoControl.setThrowable(new NoUserByTheNameException(""));
		profileDaoControl.replay();
		
		// Execute the method being tested.
		try {
			authMgr.authenticate("testZone", "testUser", "testPassword", false, false, "test");
			fail("Should throw UserDoesNotExistException");
		}
		catch(UserDoesNotExistException e) {
			assertTrue(true); // All is well
		}
		
		// Verifies that all expectations have been met.
		profileDaoControl.verify();
	}
}
