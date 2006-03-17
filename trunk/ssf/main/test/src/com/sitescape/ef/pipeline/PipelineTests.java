package com.sitescape.ef.pipeline;

import java.io.File;

import org.springframework.test.AbstractDependencyInjectionSpringContextTests;

import com.sitescape.ef.pipeline.impl.RAMConduit;
import com.sitescape.ef.util.FileHelper;

/**
 * Integration unit tests for data access layer. 
 * 
 * @author Jong Kim
 */
public class PipelineTests extends AbstractDependencyInjectionSpringContextTests {

	protected Pipeline pipeline;
	
	protected String[] getConfigLocations() {
		return new String[] {"/com/sitescape/ef/pipeline/applicationContext-pipeline.xml"};
	}
	
	protected void onSetUp() throws Exception {
		FileHelper.deleteRecursively(new File("C:/temp2"));
	}
	
	public void setPipeline(Pipeline pipeline) {
		this.pipeline = pipeline;
	}
	
	public void testInvocationOk() {
		String inputStr = "NORMAL";
		
		// Conduit is not used for multiple invocations of the pipeline. 
		// So there's no point in setting them up in a setUp method.
		Conduit firstConduit = new RAMConduit();
		firstConduit.getSink().setString(inputStr, "UTF-8");
		Conduit lastConduit = new RAMConduit();
		
		pipeline.invoke(firstConduit.getSource(), lastConduit.getSink());
		
		String outputStr = lastConduit.getSource().getDataAsString();
		
		// Check if final output strem came out as expected.
		assertEquals(outputStr, "NORMAL.DH1.DH2.DH3.DH4.DH5");
		
		// Make sure that the temp directory is empty. Otherwise, the
		// pipeline didn't properly clear resources after use. 
		if(new File("C:/temp2").list().length > 0)
			fail("C:/temp2 directory still contains files");
	}
	
	public void testInvocationPipelineException() {
		String inputStr = "THROW";
		
		// Conduit is not used for multiple invocations of the pipeline. 
		// So there's no point in setting them up in a setUp method.
		Conduit firstConduit = new RAMConduit();
		firstConduit.getSink().setString(inputStr, "UTF-8");
		Conduit lastConduit = new RAMConduit();

		try {
			pipeline.invoke(firstConduit.getSource(), lastConduit.getSink());
			fail("PipelineException should have been thrown");
		}
		catch(PipelineException e) {
			assertTrue(true); // Ok
		}

		// Make sure that the temp directory is empty. Otherwise, the
		// pipeline didn't properly clear resources after use. 
		if(new File("C:/temp2").list().length > 0)
			fail("C:/temp2 directory still contains files");
		
	}
}
