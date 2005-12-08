package com.sitescape.ef.security.authentication.impl;

import org.easymock.MockControl;

import com.sitescape.ef.dao.CoreDao;
import com.sitescape.ef.domain.User;

import junit.framework.TestCase;

public class AuthenticationManagerImplTests extends TestCase {

	public void testAuthenticateOk() {
		// Set up mock object and control
		MockControl coreDaoControl = MockControl.createControl(CoreDao.class);
		CoreDao coreDao = (CoreDao) coreDaoControl.getMock();
		User user = new User();
		
		// Set up the actual object that we are testing.
		AuthenticationManagerImpl authMgr = new AuthenticationManagerImpl();
		authMgr.setCoreDao(coreDao);
		
		// Define expected behavior of the mock object.
		coreDao.findUserByName("testUser", "testZone");
		coreDaoControl.setReturnValue(user);
		coreDaoControl.replay();
		
		// Execute the method being tested.
		User authenticatedUser = authMgr.authenticate("testZone", "testUser");
		assertEquals(user, authenticatedUser);
		
		// Verifies that all expectations have been met.
		coreDaoControl.verify();
	}
}
