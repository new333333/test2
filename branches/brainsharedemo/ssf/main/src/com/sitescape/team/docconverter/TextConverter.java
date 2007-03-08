package com.sitescape.team.docconverter;

import java.io.File;
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
import org.springframework.core.io.ClassPathResource;

public abstract class TextConverter 
{
	protected final Log logger = LogFactory.getLog(getClass());
	protected String _nullTransform = "";
	
	public abstract String convert(String ifp, String ofp, long timeout)
		throws Exception;
	
	protected org.dom4j.Document getDomDocument(File textFile) {
    	// open the file with an xml reader
		SAXReader reader = new SAXReader();
		try {
			return reader.read(textFile);
		} catch (DocumentException e) {
			logger.error(e.getMessage(), e);
			return null;
		}	
	}
	
	protected String getTextFromXML(org.dom4j.Document tempfile, File transformFile)
    {	
    	Locale l = Locale.getDefault();
		Templates trans;
		Transformer tranny = null;
        
        try {
        	
        	TransformerFactory transFactory = TransformerFactory.newInstance();
        	
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
	
	/**
	 * @return Returns the nullTransform.
	 */
	public String getNullTransform() {
		return _nullTransform;
	}

	/**
	 * @param nullTransform The nullTransform to set.
	 */
	public void setNullTransform(String nullTransform) {
		_nullTransform = nullTransform;
	}

	/**
	 * @return Returns the nullTransform file.
	 */
	protected File getNullTransformFile() {
		try {
			//load singleton with our config file
			return new ClassPathResource(_nullTransform).getFile();
		}
        catch (Exception e) {
        	Log logger = LogFactory.getLog(getClass());
        	logger.error("DocConverter, transform file error: " + e.getLocalizedMessage());
        }
		return null;
	}
}
