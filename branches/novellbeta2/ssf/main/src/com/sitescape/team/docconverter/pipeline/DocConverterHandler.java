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
package com.sitescape.team.docconverter.pipeline;

import java.io.File;
import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sitescape.team.docconverter.TextConverter;
import com.sitescape.team.pipeline.DocSink;
import com.sitescape.team.pipeline.DocSource;
import com.sitescape.team.pipeline.PipelineInvocation;
import com.sitescape.team.pipeline.support.AbstractDocHandler;
import com.sitescape.team.util.FileHelper;
import com.sitescape.team.util.TempFileUtil;

/**
 * This is a DocHandler wrapper around DocConverter and allows document
 * conversion functionality to be plugged into the doc processing pipeline. 
 * 
 * @author jong
 *
 */
public class DocConverterHandler extends AbstractDocHandler {

 	protected final Log logger = LogFactory.getLog(getClass());

	private TextConverter textConverter;
	
	// Export process timeout in milliseconds.
	private long timeout = 10000; // default value

	protected TextConverter getTextConverter() {
		return textConverter;
	}

	public void setTextConverter(TextConverter textConverter) {
		this.textConverter = textConverter;
	}

	protected long getTimeout() {
		return timeout;
	}

	public void setTimeout(long timeout) {
		this.timeout = timeout;
	}

	public void doHandle(DocSource source, DocSink sink, PipelineInvocation invocation) throws Throwable {
		File inputTempFile = null;
		// Get source file, if any, handed over by the pipeline. 
		File inputFile = source.getFile();
		if(inputFile == null) {
			// Input data doesn't come in the form of a file. Since our doc converter
			// can only work with a file, we must create a temporary file and store
			// the input data in it before invoking the actual doc converter function.
			// Note: One might legitimately argue that DocSource should have offered
			// a convenient method like getDataAsFile. However, providing such an 
			// API makes it more complex to decide who is responsible for disposing
			// of the file returned from such method. 
			inputFile = inputTempFile = TempFileUtil.createTempFileWithContent
			("docconverterinput_", source.getDataAsInputStream());
		}
		
		try {
			// (rsordillo) The doHandle() method should not be getting called anymore. This method worked fine
			// before the need to implement Stellent an OpenOffice mechanisms.
			String text = "";
			//String text = textConverter.convertToText(inputFile, timeout);
			
			sink.setString(text, "UTF-8");
		}
		finally {
			if(inputTempFile != null) {
				// If we generated temporary input file, we must delete it. 
				try {
					FileHelper.delete(inputTempFile);
				}
				catch(IOException e) {
					logger.warn(e.getLocalizedMessage(), e);
				}				
			}
		}
		
		invocation.proceed(); // Proceed to the next handler in the chain
	}
}