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

    private TransformerFactory transFactory = TransformerFactory.newInstance();
    
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
		File inputTempFile = null;
		// Get source file, if any, handed over by the pipeline. 
		File inputFile = source.getFile();
		// Input data doesn't come in the form of a file. Since our doc converter
		// can only work with a file, we must create a temporary file and store
		// the input data in it before invoking the actual doc converter function. 
		if(inputFile == null) {
			inputFile = inputTempFile = TempFileUtil.createTempFile("dcinput", SPropsUtil.getFile("temp.dir"));
			FileCopyUtils.copy(source.getDataAsInputStream(), new BufferedOutputStream(new FileOutputStream(inputFile)));
		}
		
		try {
			// Create an empty file to be used as output file for the converter.
			// The output file will contain text data in xml format. 
			File outputTextFile = TempFileUtil.createTempFile("docconverter", SPropsUtil.getFile("temp.dir"));
			
			try {
				// Invoke the actual converter function giving it timeout value.
				docConverter.convert(inputFile, outputTextFile, timeout);
				
				if(outputTextFile.length() > 0) {
					// Create a dom object from the output file containing xml text.
					org.dom4j.Document document = getDomDocument(outputTextFile);
					
					if(document != null) {
						// Run the stylesheet to extract text from the xml. 
						String text = getTextFromXML(document, docConverter.getNullTransformFile());
						// Note: Roy, for some reason, the text coming out of the transformer
						// always contain <?xml version="1.0" encoding="UTF-8"?> prefix??
						
						if(text != null) {
							// Put the text data (result of conversion) into the sink 
							// so that it can be consumed by the next guy in the chain.
							sink.setString(text, "UTF-8");
						}
					}
				}
			}
			finally {
				try {
					// It is important to delete the output text file, since
					// it is owned by this handler not by the framework. 
					FileHelper.delete(outputTextFile);
				}
				catch(IOException e) {
					logger.warn(e.getMessage(), e);
				}
			}
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
	
    private String getTextFromXML(org.dom4j.Document tempfile, File transformFile) {
    	
    	Locale l = Locale.getDefault();
		Templates trans;
		Transformer tranny = null;
        
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