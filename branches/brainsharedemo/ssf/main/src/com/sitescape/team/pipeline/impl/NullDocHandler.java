package com.sitescape.team.pipeline.impl;

import com.sitescape.team.pipeline.DocSink;
import com.sitescape.team.pipeline.DocSource;
import com.sitescape.team.pipeline.PipelineInvocation;
import com.sitescape.team.pipeline.support.AbstractDocHandler;

/**
 * Simply copy data from source to sink as it is.
 * 
 * @author jong
 *
 */
public class NullDocHandler extends AbstractDocHandler {

	private static final int BUFFER_SIZE = 4096;
	
	private long dataSize; 
	
	public void doHandle(DocSource source, DocSink sink, PipelineInvocation invocation) throws Throwable {
		System.out.println(getName() + " is being executed");
		byte[] buffer = new byte[BUFFER_SIZE];
		int count;
		while((count = source.getDataAsInputStream().read(buffer)) > -1) {
			sink.getBuiltinOutputStream(false, null).write(buffer, 0, count);
			dataSize += count;
		}
		System.out.println(getName() + " processed " + dataSize + " number of bytes");
		
		invocation.proceed();
	}

}
