package com.sitescape.ef.pipeline;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

import org.springframework.core.io.Resource;
import org.springframework.util.FileCopyUtils;

import com.sitescape.ef.pipeline.support.AbstractDocHandler;
import com.sitescape.ef.util.TempFileUtil;

public class StringConcatenatingDocHandler extends AbstractDocHandler {

	private static final String CHARSET_NAME = "UTF-8";
	
	private String outputMethod;
	private File fileDir;
	
	public void setOutputMethod(String outputMethod) {
		this.outputMethod = outputMethod;
	}
	public void setFileDir(Resource fileDirResource) throws IOException {
		this.fileDir = fileDirResource.getFile();
	}

	public void doHandle(DocSource source, DocSink sink, PipelineInvocation invocation) throws Throwable {
		// Get input. I don't care what form it is. So just get it as a String.
		String inputStr = source.getDataAsString();
		
		if(inputStr.startsWith("THROW") && getName().equals("DH5")) {
			// This particular condition triggers exception throwing. 
			// It doesn't matter what kind of exception it throws. 
			throw new Exception(); 
		}
		
		// Append my name (I mean the name of this DocHandler) to the input
		// string with '.' as a delimiter.
		String outputStr = inputStr + "." + getName();
		
		// Depending on the configuration flag specified, we use different
		// method for passing output data into the sink. 
		if(outputMethod.equals("setByteArray")) {
			sink.setByteArray(outputStr.getBytes(CHARSET_NAME), true, CHARSET_NAME);
		}
		else if(outputMethod.equals("setString")) {
			sink.setString(outputStr, CHARSET_NAME);
		}
		else if(outputMethod.equals("setFile")) {
			File tempFile = TempFileUtil.createTempFile(getName(), fileDir);
			FileCopyUtils.copy(outputStr.getBytes(CHARSET_NAME), tempFile);
			sink.setFile(tempFile, true, true, CHARSET_NAME);
		}
		else if(outputMethod.equals("setInputStream")) {
			ByteArrayInputStream is = new ByteArrayInputStream(outputStr.getBytes(CHARSET_NAME));
			sink.setInputStream(is, true, CHARSET_NAME);
		}
		else if(outputMethod.equals("getBuiltinOutputStream")) {
			OutputStream os = sink.getBuiltinOutputStream(true, CHARSET_NAME);
			FileCopyUtils.copy(outputStr.getBytes(CHARSET_NAME), os);
		}
		else {
			throw new IllegalArgumentException(fileDir.getAbsolutePath());
		}
		
		invocation.proceed();
	}

}
