package com.sitescape.ef.docconverter.pipeline;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Locale;

import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.DocumentException;
import org.dom4j.io.DocumentSource;
import org.dom4j.io.SAXReader;
import org.springframework.util.FileCopyUtils;

import com.sitescape.ef.docconverter.TextConverter;
import com.sitescape.ef.docconverter.impl.DocConverter;
import com.sitescape.ef.pipeline.support.AbstractDocHandler;
import com.sitescape.ef.pipeline.DocSink;
import com.sitescape.ef.pipeline.DocSource;
import com.sitescape.ef.pipeline.PipelineInvocation;
import com.sitescape.ef.util.FileHelper;
import com.sitescape.ef.util.SPropsUtil;
import com.sitescape.ef.util.TempFileUtil;

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
			String text = textConverter.convertToText(inputFile, timeout);
			
			sink.setString(text, "UTF-8");
		}
		finally {
			if(inputTempFile != null) {
				// If we generated temporary input file, we must delete it. 
				try {
					FileHelper.delete(inputTempFile);
				}
				catch(IOException e) {
					logger.warn(e.getMessage(), e);
				}				
			}
		}
		
		invocation.proceed(); // Proceed to the next handler in the chain
	}
}