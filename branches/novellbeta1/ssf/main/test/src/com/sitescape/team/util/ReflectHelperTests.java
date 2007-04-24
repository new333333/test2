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
package com.sitescape.team.util;

import java.lang.reflect.Method;

import com.sitescape.team.util.ReflectHelper;

import junit.framework.TestCase;

/**
 * Logic unit test for <code>ReflectHelper</code>.
 * 
 * @author Jong Kim
 */
public class ReflectHelperTests extends TestCase {
	
	public void testClassForNameOk() {
		try {
			Class c = ReflectHelper.classForName("com.sitescape.team.util.ReflectHelperTests$TestClass");
			assertEquals(c, TestClass.class);
		} catch (ClassNotFoundException e) {
			fail("ClassNotFoundException should not be raised");
		}
	}
	
	public void testClassForNameClassNotFoundException() {
		try {
			ReflectHelper.classForName("ExtremelyUnlikelyToExistClass");
			fail("ClassNotFoundException should have been raised");
		} catch (ClassNotFoundException e) {
			assertTrue(true);
		}		
	}
	
	public void testGetterMethod() {
		Method method = ReflectHelper.getterMethod(TestClass2.class, "foo", true);
		assertNotNull(method);
		
		method = ReflectHelper.getterMethod(TestClass2.class, "foo", false);
		assertNull(method);
		
		method = ReflectHelper.getterMethod(TestClass2.class, "bar", true);
		assertNotNull(method);
		
		method = ReflectHelper.getterMethod(TestClass2.class, "bar", false);
		assertNotNull(method);
	}
	
	private class TestClass {
		public String getFoo() {
			return null;
		}
	}
	
	private class TestClass2 extends TestClass {
		public String getBar() {
			return null;
		}
	}
}
