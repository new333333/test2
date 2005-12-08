package com.sitescape.ef.util;

import java.lang.reflect.Method;

import junit.framework.TestCase;

/**
 * Logic unit test for <code>ReflectHelper</code>.
 * 
 * @author Jong Kim
 */
public class ReflectHelperTests extends TestCase {
	
	public void testClassForNameOk() {
		try {
			Class c = ReflectHelper.classForName("TestClass");
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
