package com.sitescape.ef.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

public class XmlFileUtil {
	public static final String FILE_ENCODING="UTF-8";
	protected static Log logger = LogFactory.getLog(XmlFileUtil.class);
	
	public static Document readFile(String path) 
		throws Exception {
		Document document = null;
        SAXReader reader = new SAXReader();
        InputStreamReader fIn=null;
        try {
        	fIn = new InputStreamReader(new FileInputStream(path), FILE_ENCODING);
        	document = reader.read(fIn);
        } catch (Exception ex) {
        	logger.error("Cannot read XML fiel " + path + ":error is: " + ex.getLocalizedMessage());
        	throw ex;
        } finally {
        	if (fIn != null) {
        		try {
        			fIn.close(); 
        		} catch (Exception ex) {}
        	}
        }
        return document;
    }
	public static void writeFile(Document doc, String path)
		throws Exception {
		FileOutputStream fOut = null;
		XMLWriter xOut=null;
		try {
			// explicity set encoding so their is not mistake.
			//cannot guarentee default will be set to UTF-8
			fOut = new FileOutputStream(path);
			OutputFormat fmt = OutputFormat.createCompactFormat();
			fmt.setEncoding(FILE_ENCODING);
    		xOut = new XMLWriter(fOut, fmt);
    		xOut.write(doc);
    		xOut.flush();
	    } catch (Exception ex) {
	    	logger.error("Can't write XML file " + path + ":error is: " + ex.getLocalizedMessage());
	    	throw(ex);
	    } finally {
	    	if (xOut != null) xOut.close();
	    	else if (fOut != null) fOut.close();
	    }
		
	}    
}
