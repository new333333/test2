package com.sitescape.ef.modelprocessor;

import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.test.AbstractDependencyInjectionSpringContextTests;

/**
 * This integration unit test uses the Spring container.
 * 
 * @author Jong Kim
 */
public class ProcessorManagerTests extends AbstractDependencyInjectionSpringContextTests {

	protected String[] getConfigLocations() {
		return new String[] {"/com/sitescape/ef/modelprocessor/applicationContext-processor.xml"};
	}

	public void testGetProcessorForClass() {
		ProcessorManager procMgr = (ProcessorManager) applicationContext.getBean("processorManager");
		assertNotNull(procMgr);
		
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
