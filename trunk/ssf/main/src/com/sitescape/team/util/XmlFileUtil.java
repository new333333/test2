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
package com.sitescape.team.util;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ByteArrayInputStream;
import java.io.StringWriter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import com.sitescape.team.domain.SSClobString;

public class XmlFileUtil {
	public static String FILE_ENCODING="UTF-8";
	protected static Log logger = LogFactory.getLog(XmlFileUtil.class);
	
	public static void setFileEncoding(String encoding)
	{
		FILE_ENCODING = encoding;
	}
	
	public static String getEncoding()
	{
		return FILE_ENCODING;
	}
	
	public static Document readFile(String path) 
	throws Exception {
	Document document = null;
    SAXReader reader = new SAXReader();
    InputStreamReader fIn=null;
    try {
    	if (FILE_ENCODING.equals(""))
    		fIn = new InputStreamReader(new FileInputStream(path));
    	else
    		fIn = new InputStreamReader(new FileInputStream(path), FILE_ENCODING);
    	document = reader.read(fIn);
    } catch (Exception ex) {
    	logger.error("Cannot read XML file " + path + ":error is: " + ex.getLocalizedMessage());
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
	
	public static Document readStream(InputStream fIn) 
	throws Exception {
	Document document = null;
    SAXReader reader = new SAXReader();
    try {
    	document = reader.read(fIn);
    } catch (Exception ex) {
    	logger.error("Cannot read XML from stream. error is: " + ex.getLocalizedMessage());
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
			//explicity set encoding so their is no mistake.
			//cannot guarentee default will be set to UTF-8
			fOut = new FileOutputStream(path);
			OutputFormat fmt = OutputFormat.createPrettyPrint();
			if (!FILE_ENCODING.equals(""))
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
	public static String writeString(Document doc, OutputFormat format) 
		throws Exception {
     	StringWriter sOut = new StringWriter();
		XMLWriter xOut = new XMLWriter(sOut, format);
		try {
			xOut.write(doc);
			return sOut.toString();
     	} catch (Exception ex) {
	    	logger.error("Can't write XML document, error is: " + ex.getLocalizedMessage());
	    	throw ex;
     	} finally {
     		try {
     			xOut.close();
     			sOut.close();
     		} catch (IOException io) {};
     	}

	}
	public static Document generateSAXXMLFromString(String fileContent)
		throws Exception {

		Document generatedDocument = null;
		if (fileContent == null || fileContent.equals("")) return generatedDocument;
		
		ByteArrayInputStream byteArrayInputStream = null; 
		
		SAXReader saxReader = new SAXReader();
		
		try {
			//the fileContent must contain a valid tag at the start of the text
			byteArrayInputStream = new ByteArrayInputStream(fileContent.getBytes());
			generatedDocument = saxReader.read(byteArrayInputStream);
	    } catch (Exception ex) {
	    	logger.error("Can't write XML file content " + fileContent + ":error is: " + ex.getLocalizedMessage());
	    }
	    return generatedDocument;
	}    

	public static Document generateXMLFromString(String fileContent)
		throws Exception {
		
		Document generatedDocument = null;
		if (fileContent == null || fileContent.equals("")) return generatedDocument;
		try {
			//the fileContent must contain a valid tag at the start of the text
			generatedDocument = DocumentHelper.parseText(fileContent);
	    } catch (Exception ex) {
	    	logger.error("Can't create XML file " + fileContent + ":error is: " + ex.getLocalizedMessage());
	    	throw(ex);
	    }
	    return generatedDocument;
	}
	
}
