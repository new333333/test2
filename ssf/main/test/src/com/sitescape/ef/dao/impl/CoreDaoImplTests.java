package com.sitescape.ef.dao.impl;

import java.util.Iterator;
import java.util.Map;

import org.hibernate.LazyInitializationException;
import org.springframework.test.AbstractTransactionalDataSourceSpringContextTests;

import com.sitescape.ef.dao.util.FilterControls;
import com.sitescape.ef.domain.Group;
import com.sitescape.ef.domain.NoUserByTheNameException;
import com.sitescape.ef.domain.User;

public class CoreDaoImplTests extends AbstractTransactionalDataSourceSpringContextTests {

	protected CoreDaoImpl cdi;
	
	protected String[] getConfigLocations() {
		return new String[] {"/com/sitescape/ef/dao/impl/applicationContext-coredao.xml"};
	}
	
	/*
	 * This method is provided to set the CoreDaoImpl instance being tested
	 * by the Dependency Injection, which is done automatically by the
	 * superclass.
	 */
	public void setCoreDaoImpl(CoreDaoImpl cdi) {
		this.cdi = cdi;
	}
	
	public void testFindUserByName() {
		User user = cdi.findUserByName("liferay.com.1", "liferay.com");
		assertNotNull(user);
	}
	
	public void testFindUserByNameNoUserByTheNameException() {
		// Test three slightly different cases: It throws the same exception
		// for all cases. Throwing different exception for each case will make
		// the database lookup more expensive, so it's not worth it. 
		
		// Test the situation where zone exists but username does not. 
		try {
			cdi.findUserByName("nonExistingUser", "liferay.com");			
			fail("Should throw NoUserByTheNameException");
		}
		catch(NoUserByTheNameException e) {
			assertTrue(true); // Ok
		}
		
		// Test the situation where username exists but zone doesn't.
		try {
			cdi.findUserByName("liferay.com.1", "nonExistingZone");			
			fail("Should throw NoUserByTheNameException");
		}
		catch(NoUserByTheNameException e) {
			assertTrue(true); // Ok
		}
		
		// Test the situation where username exists but zone doesn't.
		try {
			cdi.findUserByName("nonExistingUser", "nonExistingZone");			
			fail("Should throw NoUserByTheNameException");
		}
		catch(NoUserByTheNameException e) {
			assertTrue(true); // Ok
		}
	}
	
	public void testLoadUserAndLazyLoading() {
		// phase1: Load it. 
		User user = cdi.loadUser(new Long(68), "liferay.com");
		assertNotNull(user);
		
		// phase2: Test lazy loading, by ending the transation (it rolls back).
		// Here we expect LazyInitializationException from Hibernate because
		// the session is already closed. If we had open-session-in-view
		// setup, lazy loading would have worked. But that is not the case here.
		endTransaction();
		try {
			Map customAttrs = user.getCustomAttributes();
			for(Iterator i = customAttrs.entrySet().iterator(); i.hasNext();) {
				Map.Entry ent = (Map.Entry) i.next();
				Object key = ent.getKey();
				Object val = ent.getValue();
			}
			// If you're still here, something's wrong.
			fail("Should throw LazyInitializationException");
		}
		catch(LazyInitializationException e) {
			assertTrue(true); // As expected
		}
	}
	
	public void testAddGroup() {
		FilterControls filter = new FilterControls();
		int count = cdi.countGroups(filter);
		
		Group newGroup = new Group();
		newGroup.setName("brandNewGroup");
		newGroup.setZoneName("liferay.com");
		
		cdi.save(newGroup);
		
		int newCount = cdi.countGroups(filter);
		
		assertEquals(count + 1, newCount);
	}
}
