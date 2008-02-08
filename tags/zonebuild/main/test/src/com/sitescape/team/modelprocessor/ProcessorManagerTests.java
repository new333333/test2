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
