/**
 * The contents of this file are governed by the terms of your license
 * with SiteScape, Inc., which includes disclaimers of warranties and
 * limitations on liability. You may not use this file except in accordance
 * with the terms of that license. See the license for the specific language
 * governing your rights and limitations under the license.
 *
 * Copyright (c) 2007 SiteScape, Inc.
 *
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
