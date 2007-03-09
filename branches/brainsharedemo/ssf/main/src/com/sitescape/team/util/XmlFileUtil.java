package com.sitescape.team.util;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.ByteArrayInputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

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
