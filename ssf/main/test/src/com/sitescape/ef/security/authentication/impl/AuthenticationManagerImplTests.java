package com.sitescape.ef.security.authentication.impl;

import org.easymock.MockControl;

import com.sitescape.ef.dao.ProfileDao;
import com.sitescape.ef.domain.NoUserByTheNameException;
import com.sitescape.ef.domain.User;
import com.sitescape.ef.security.authentication.UserDoesNotExistException;

import junit.framework.TestCase;

/**
 * Logic unit test for <code>AuthenticationManagerImpl</code>.
 * 
 * @author Jong Kim
 */
public class AuthenticationManagerImplTests extends TestCase {

	MockControl profileDaoControl;
	ProfileDao profileDao;
	User user;
	AuthenticationManagerImpl authMgr;
	
	// This method is called once per test method execution. That is, the
	// purpose of this method is solely to avoid code duplication. 
	// If you want a setup that gets called only once for a set of tests,
	// you should use TestSetup in conjunction with TestSuite. 
	protected void setUp() {
		// Set up mock object and control
		profileDaoControl = MockControl.createControl(ProfileDao.class);
		profileDao = (ProfileDao) profileDaoControl.getMock();
		user = new User();
		user.setZoneName("testZone");
		user.setName("testUser");
		
		// Set up the actual object that we are testing.
		authMgr = new AuthenticationManagerImpl();
		authMgr.setProfileDao(profileDao);
	}
	
	public void testAuthenticateOk() {
		// Define expected behavior of the mock object.
		profileDaoControl.reset();
		profileDao.findUserByNameOnlyIfEnabled("testUser", "testZone");
		profileDaoControl.setReturnValue(user);
		profileDaoControl.replay();
		
		user.setPassword("testPassword");

		// Execute the method being tested.
		User authenticatedUser = authMgr.authenticate("testZone", "testUser", "testPassword", false);
		assertEquals(user, authenticatedUser);
		
		// Verifies that all expectations have been met.
		profileDaoControl.verify();
	}
	
	public void testAuthenticateUserDoesNotExistException() {
		// Define expected behavior of the mock object. 
		profileDaoControl.reset();
		profileDao.findUserByNameOnlyIfEnabled("testUser", "testZone");
		profileDaoControl.setThrowable(new NoUserByTheNameException(""));
		profileDaoControl.replay();
		
		// Execute the method being tested.
		try {
			authMgr.authenticate("testZone", "testUser", "testPassword", false);
			fail("Should throw UserDoesNotExistException");
		}
		catch(UserDoesNotExistException e) {
			assertTrue(true); // All is well
		}
		
		// Verifies that all expectations have been met.
		profileDaoControl.verify();
	}
}
