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
