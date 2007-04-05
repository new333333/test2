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

import com.sitescape.team.util.InvokeUtil;
import com.sitescape.team.util.ObjectPropertyNotFoundException;

import junit.framework.TestCase;

/**
 * Provides logic unit test for <code>InvokeUtil</code> class.
 * 
 * @author Jong Kim
 */
public class InvokeUtilTests extends TestCase {
	
	public void testInvokeGetterOk() {
		SimpleBean obj = new SimpleBean();
		String v1 = "test";
		obj.setFoo(v1);
		Object v2 = InvokeUtil.invokeGetter(obj, "foo");
		assertEquals(v1, v2);
	}
	
	public void testInvokeGetterObjectPropertyNotFoundException() {
		SimpleBean obj = new SimpleBean();
		try {
			InvokeUtil.invokeGetter(obj, "bar");
			fail("ObjectPropertyNotFoundException should be raised if the requested property does not exist");
		}
		catch(ObjectPropertyNotFoundException e) {
			assertTrue(true); // Ok
		}
	}
	
	public void testInvokeSetterOk() {
		SimpleBean obj = new SimpleBean();
		String v1 = "test";
		// Take the session factory out of the equation.
		InvokeUtil.setSessionFactoryImplementor(null);
		InvokeUtil.invokeSetter(obj, "foo", v1);
		String v2 = obj.getFoo();
		assertEquals(v1, v2);
	}
	
	public void testInvokeSetterObjectPropertyNotFoundException() {
		SimpleBean obj = new SimpleBean();
		// Take the session factory out of the equation.
		InvokeUtil.setSessionFactoryImplementor(null);
		try {
			InvokeUtil.invokeSetter(obj, "bar", "test");
			fail("ObjectPropertyNotFoundException should be raised if the requested property does not exist");
		}
		catch(ObjectPropertyNotFoundException e) {
			assertTrue(true); // Ok
		}
	}
	
	private class SimpleBean {
		String foo;
		
		public void setFoo(String foo) {
			this.foo = foo;
		}
		
		public String getFoo() {
			return foo;
		}
	}
}
