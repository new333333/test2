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

import com.sitescape.ef.docconverter.DocConverter;
import com.sitescape.ef.docconverter.DocConverterException;
import com.sitescape.ef.pipeline.impl.AbstractDocHandler;
import com.sitescape.ef.pipeline.util.TempFileUtil;
import com.sitescape.ef.pipeline.DocSink;
import com.sitescape.ef.pipeline.DocSource;
import com.sitescape.ef.pipeline.PipelineInvocation;
import com.sitescape.ef.util.FileHelper;
import com.sitescape.ef.util.SPropsUtil;

public class DocConverterHandler extends AbstractDocHandler {

    private static final String NULLXSL = "<?xml version='1.0' ?> \n    <xsl:stylesheet xmlns:xsl='http://www.w3.org/1999/XSL/Transform' version='1.0' />"; 

    private static TransformerFactory transFactory = TransformerFactory.newInstance();
    
	protected final Log logger = LogFactory.getLog(getClass());

	private DocConverter docConverter;
	// Export process timeout in milliseconds.
	private long timeout = 10000; // default value

	protected DocConverter getDocConverter() {
		return docConverter;
	}

	public void setDocConverter(DocConverter docConverter) {
		this.docConverter = docConverter;
	}

	protected long getTimeout() {
		return timeout;
	}

	public void setTimeout(long timeout) {
		this.timeout = timeout;
	}

	public void doHandle(DocSource source, DocSink sink, PipelineInvocation invocation) throws Throwable {
		// Get source file handed over by the pipeline. 
		File inputFile = source.getFile();
		File inputTempFile = null;
		if(inputFile == null) {
			inputFile = inputTempFile = TempFileUtil.createTempFile("dcinput", SPropsUtil.getFile("temp.dir"));
			FileCopyUtils.copy(source.getDataAsInputStream(), new BufferedOutputStream(new FileOutputStream(inputFile)));
		}
		
		try {
			File outputTextFile = TempFileUtil.createTempFile("docconverter", SPropsUtil.getFile("temp.dir"));
			
			try {
				docConverter.convert(inputFile, outputTextFile, timeout);
				
				if(outputTextFile.length() > 0) {
					org.dom4j.Document document = getDomDocument(outputTextFile);
					
					if(document != null) {
						String text = getTextFromXML(document, docConverter.getNullTransformFile());
						
						if(text != null) {
							sink.setString(text, "UTF-8");
						}
					}
				}
			}
			finally {
				try {
					FileHelper.delete(outputTextFile);
				}
				catch(IOException e) {
					logger.warn(e.getMessage(), e);
				}
			}
		}
		finally {
			if(inputTempFile != null) {
				try {
					FileHelper.delete(inputTempFile);
				}
				catch(IOException e) {
					logger.warn(e.getMessage(), e);
				}				
			}
		}
	}
	
	private org.dom4j.Document getDomDocument(File textFile) {
    	// open the file with an xml reader
		SAXReader reader = new SAXReader();
		try {
			return reader.read(textFile);
		} catch (DocumentException e) {
			logger.error(e.getMessage(), e);
			return null;
		}	
	}
	
    private static String getTextFromXML(org.dom4j.Document tempfile, File transformFile) {
    	
    	Locale l = Locale.getDefault();
		Templates trans;
		Transformer tranny = null;
		Source xsltSource = new StreamSource(NULLXSL);
        
        try {
			Source s = new StreamSource(transformFile);
			trans = transFactory.newTemplates(s);
			tranny =  trans.newTransformer();
		} catch (TransformerConfigurationException tce) {}
		
		StreamResult result = new StreamResult(new StringWriter());
		try {
			tranny.setParameter("Lang", l);
			tranny.transform(new DocumentSource(tempfile), result);
		} catch (Exception ex) {
			return ex.getMessage();
		}
		return result.getWriter().toString();
	}
}