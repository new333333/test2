/*
 * Created on Jun 24, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.sitescape.ef.docconverter.impl;

import com.sitescape.ef.docconverter.TextConverter;
import com.sitescape.ef.util.FileHelper;
import com.sitescape.ef.util.TempFileUtil;
import com.stellent.scd.*;
import java.io.*;
import java.util.*;

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
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;


/**
 * The <code>Converter</code> class uses the {@link Export Export} 
 * technology according to the properties provided in a given 
 * configuration file.  The configuration file is assumed to be 
 * correctly formatted.
 *
 * @author klein
 * @version 1.00
 * @see Export Export
 */

public class DocConverter implements TextConverter, InitializingBean, DisposableBean {

	private static final String INPUTPATHKEY = "inputpath";
	private static final String OUTPUTPATHKEY = "outputpath";
	private static final String OUTPUTIDKEY = "outputid";
	private String configFileName;
	private String nullTransform;
	
	Properties configProps = new Properties();
	protected final Log logger = LogFactory.getLog(getClass());

	private TransformerFactory transFactory = TransformerFactory.newInstance();
	    
	public void afterPropertiesSet() throws Exception {
		try {
			setConverterConfiguration(getConfigFileName());
		} catch (Exception e) {}
	}	
	
	public void destroy() throws Exception {
		
		// Close the socket connection that you established in afterPropertiesSet.
		// Do any other cleanup stuff as necessary. 
	}
	
	/**
	 * @return Returns the configFileName.
	 */
	public String getConfigFileName() {
		return configFileName;
	}

	/**
	 * @param configFileName The configFileName to set.
	 */
	public void setConfigFileName(String configFileName) {
		this.configFileName = configFileName;
	}


	/**
	 * @return Returns the nullTransform.
	 */
	public String getNullTransform() {
		return nullTransform;
	}

	/**
	 * @param nullTransform The nullTransform to set.
	 */
	public void setNullTransform(String nullTransform) {
		this.nullTransform = nullTransform;
	}

	/**
	 * @return Returns the nullTransform file.
	 */
	public File getNullTransformFile() {
		try {
			//load singleton with our config file
			return new ClassPathResource(nullTransform).getFile();
		}
        catch (Exception e) {
        	Log logger = LogFactory.getLog(getClass());
        	logger.error("DocConverter, transform file error: " + e.getLocalizedMessage());
        }
		return null;
	}
	
	/**
	 * This is going to be a bean, so, we need a bean style setter.
	 * @param configFileName
	 */
	
	public void setConverterConfiguration(String configFileName) {	
		Properties p = new Properties();
		
		Resource resource;
		
		resource = toResource(configFileName);
		
		try {
	
			if (resource != null) {
	            InputStream is = resource.getInputStream();
				configProps.load(is);
				is.close();
				logger.info("DOCCONVERTER: Loading " + configFileName);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Given a path to an properties file, return a Resource object for that file.
	 * 
	 * @param filePath
	 * @return Resource
	 */
	private Resource toResource(String filePath) {
        return new ClassPathResource(filePath);
    }

	public boolean convert(File ifp, File ofp, long timeout)
	{
		return convert(ifp.getAbsolutePath(), ofp.getAbsolutePath(),timeout);
	}
	
	/**
	 *  Run the conversion using the given input path, output path.
	 *
	 *  @param ifp     Input path.
	 *  @param ofp     Output path.
	 *  @param timeout Export process timeout in milliseconds.
	 *  @return <code>true</code> if successful, <code>false</code> otherwise
	 */
	public boolean convert(String ifp, String ofp, long timeout)
	{
		String oid = configProps.getProperty(OUTPUTIDKEY);

		//  Remove extra control properties.
		configProps.remove(INPUTPATHKEY);
		configProps.remove(OUTPUTPATHKEY);
		
		//  Process the conversion.
		Export e = new Export(configProps);

		ExportStatusCode result = e.convert(ifp, ofp, oid, timeout);
        if (result.getCode() == ExportStatusCode.SCCERR_OK.getCode())
        {
			   logger.info("Conversion Successful!" + ifp + ":" + ofp);
			   return true;
        }
        else {
           logger.warn("Conversion Error: " + result);
           return false; 
        }
	}
	
	/**
	 *  Run the conversion using the given input path, output path.
	 *  Default the timeout to 0.
	 *
	 *  @param ifp     Input path.
	 *  @param ofp     Output path.
	 */

	public boolean convert(String ifp, String ofp)
	{
		// default the timeout value to 0
		return convert(ifp,ofp,0);
	}

	public String convertToText(File inputFile, long timeout) throws Exception {
		// Create an empty file to be used as output file for the converter.
		// The output file will contain text data in xml format. 
		File outputTextFile = TempFileUtil.createTempFile("docconverteroutput_");
		
		try {
			// Invoke the actual converter function giving it timeout value.
			boolean result = convert(inputFile, outputTextFile, timeout);
			
			String text = null;
			
			if(result) {
				if(outputTextFile.length() > 0) {
					// Create a dom object from the output file containing xml text.
					org.dom4j.Document document = getDomDocument(outputTextFile);
					
					if(document != null) {
						// Run the stylesheet to extract text from the xml. 
						text = getTextFromXML(document, getNullTransformFile());
						// Note: Roy, for some reason, the text coming out of the transformer
						// always contain <?xml version="1.0" encoding="UTF-8"?> prefix??
					}
				}
			}
			
			// Put the text data (result of conversion) into the sink 
			// so that it can be consumed by the next guy in the chain.
			if(text == null)
				text = "";
			
			return text;
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
