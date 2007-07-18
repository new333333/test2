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
package com.sitescape.team.modelprocessor;

import org.springframework.test.AbstractDependencyInjectionSpringContextTests;

import com.sitescape.team.modelprocessor.ProcessorManager;

/**
 * This integration unit test uses the Spring container.
 * 
 * @author Jong Kim
 */
public class ProcessorManagerTests extends AbstractDependencyInjectionSpringContextTests {

	protected ProcessorManager procMgr;
	
	protected String[] getConfigLocations() {
		return new String[] {"/com/sitescape/team/modelprocessor/applicationContext-processor.xml"};
	}
	
	public void setProcessorManager(ProcessorManager procMgr) {
		this.procMgr = procMgr;
	}

	public void testGetProcessorForClass() {
		TestModel testModel = new TestModel();
		
		TestProcessor1 processor1 = (TestProcessor1) procMgr.getProcessor(testModel, TestProcessor1.PROCESSOR_KEY);
		assertNotNull(processor1);
		assertEquals(processor1.getClass(), MyTestProcessor1.class);
		
		Object internalBean = applicationContext.getBean("myTestProcessor1");
		assertNotNull(internalBean);
		
		assertEquals(processor1, internalBean);
		
		TestProcessor2 processor2 = (TestProcessor2) procMgr.getProcessor(testModel, TestProcessor2.PROCESSOR_KEY);
		assertNotNull(processor2);
		assertEquals(processor2.getClass(), MyTestProcessor2.class);
		assertEquals(processor2.getGreeting(), "Hello");
		
		TestProcessor3 processor3 = (TestProcessor3) procMgr.getProcessor(testModel, TestProcessor3.PROCESSOR_KEY);
		assertNotNull(processor3);
		assertEquals(processor3.getClass(), MyTestProcessor3.class);
	}
	
	public class TestModel {}
	
	public interface TestProcessor1 {
	    public static final String PROCESSOR_KEY = "processorKey_testProcessor1";
	}
	public interface TestProcessor2 {
	    public static final String PROCESSOR_KEY = "processorKey_testProcessor2";
	    public String getGreeting();
	}
	public interface TestProcessor3 {
	    public static final String PROCESSOR_KEY = "processorKey_testProcessor3";
	}

	public static class MyTestProcessor1 implements TestProcessor1 {}
	public static class MyTestProcessor2 implements TestProcessor2 {
		
		private String greeting;
		public String getGreeting() {
			return greeting;
		}
		public void setGreeting(String greeting) {
			this.greeting = greeting;
		}
	}
	public static class MyTestProcessor3 implements TestProcessor3 {}
}
