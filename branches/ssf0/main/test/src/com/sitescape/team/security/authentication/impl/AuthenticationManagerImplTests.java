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

import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;
import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.sitescape.team.context.request.RequestContext;
import com.sitescape.team.context.request.RequestContextHolder;
import com.sitescape.team.dao.ProfileDao;
import com.sitescape.team.domain.NoUserByTheNameException;
import com.sitescape.team.domain.User;
import com.sitescape.team.security.authentication.UserDoesNotExistException;
import com.sitescape.team.support.AbstractTestBase;


/**
 * Logic unit test for <code>AuthenticationManagerImpl</code>.
 * 
 * @author Jong Kim
 */
public class AuthenticationManagerImplTests extends AbstractTestBase {
	private ProfileDao mProfileDao;
	private User user;
	@Autowired(required = true)
	protected AuthenticationManagerImpl authenticationManager;
	
	@Before
	public void mockSetUp() {
		// Set up mock object and control
		mProfileDao = createMock(ProfileDao.class);
		user = new User();
		user.setName("testUser");
		
		authenticationManager.setProfileDao(mProfileDao);
	}
	
	@Test
	public void authenticateOk() {
		String zone = "testZone";
		expect(mProfileDao.findUserByName("testUser", zone)).andReturn(user);
		replay(mProfileDao);
		
		user.setPassword("testPassword");
		RequestContext mRequestContext = createMock(RequestContext.class);
		expect(mRequestContext.getZoneName()).andReturn(zone);
		replay(mRequestContext);
		RequestContextHolder.setRequestContext(mRequestContext);

		try {
		User authenticatedUser = authenticationManager.authenticate(zone, "testUser", "testPassword", false, false, null);
		assertEquals(user, authenticatedUser);
		} finally {
			RequestContextHolder.clear();
		}
		verify(mProfileDao);
	}
	
	@Test(expected=UserDoesNotExistException.class)
	public void authenticateUserDoesNotExistException() {
		String zone = "testZone";
		expect(mProfileDao.findUserByName("testUser", zone)).andThrow(
				new NoUserByTheNameException("mock expection"));
		replay(mProfileDao);
		RequestContext mRequestContext = createMock(RequestContext.class);
		expect(mRequestContext.getZoneName()).andReturn(zone);
		replay(mRequestContext);
		RequestContextHolder.setRequestContext(mRequestContext);
		
		authenticationManager.authenticate(zone, "testUser", "testPassword", false, false, "test");
	}
}
