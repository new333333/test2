/**
 * Copyright (c) 1998-2015 Novell, Inc. and its licensors. All rights reserved.
 * 
 * This work is governed by the Common Public Attribution License Version 1.0 (the
 * "CPAL"); you may not use this file except in compliance with the CPAL. You may
 * obtain a copy of the CPAL at http://www.opensource.org/licenses/cpal_1.0. The
 * CPAL is based on the Mozilla Public License Version 1.1 but Sections 14 and 15
 * have been added to cover use of software over a computer network and provide
 * for limited attribution for the Original Developer. In addition, Exhibit A has
 * been modified to be consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT
 * WARRANTY OF ANY KIND, either express or implied. See the CPAL for the specific
 * language governing rights and limitations under the CPAL.
 * 
 * The Original Code is ICEcore, now called Kablink. The Original Developer is
 * Novell, Inc. All portions of the code written by Novell, Inc. are Copyright
 * (c) 1998-2015 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2015 Novell, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by Kablink]
 * Attribution URL: [www.kablink.org]
 * Graphic Image as provided in the Covered Code
 * [ssf/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are
 * defined in the CPAL as a work which combines Covered Code or portions thereof
 * with code not governed by the terms of the CPAL.
 * 
 * NOVELL and the Novell logo are registered trademarks and Kablink and the
 * Kablink logos are trademarks of Novell, Inc.
 */
package org.kablink.teaming.util;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringWriter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import org.kablink.teaming.util.XmlUtil;

/**
 * ?
 * 
 * @author ?
 */
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
    SAXReader reader = XmlUtil.getSAXReader();
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
    SAXReader reader = XmlUtil.getSAXReader();
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
	public static void writeFile(Document doc, OutputStream out) 
	throws Exception {
		XMLWriter xOut=null;
		//explicity set encoding so their is no mistake.
		//cannot guarentee default will be set to UTF-8
		OutputFormat fmt = OutputFormat.createPrettyPrint();
		if (!FILE_ENCODING.equals(""))
			fmt.setEncoding(FILE_ENCODING);
   		xOut = new XMLWriter(out, fmt);
   		xOut.write(doc);
   		xOut.flush();
				
	}
	public static void writeFile(Document doc, String path)
		throws Exception {
		FileOutputStream fOut = null;
		try {
			fOut = new FileOutputStream(path);
			writeFile(doc, fOut);
	    } catch (Exception ex) {
	    	logger.error("Can't write XML file " + path + ":error is: " + ex.getLocalizedMessage());
	    	throw(ex);
	    } finally {
	    	if (fOut != null) fOut.close();
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
		
		SAXReader saxReader = XmlUtil.getSAXReader();
		
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
			generatedDocument = XmlUtil.parseText(fileContent);
	    } catch (Exception ex) {
	    	logger.error("Can't create XML file " + fileContent + ":error is: " + ex.getLocalizedMessage());
	    	throw(ex);
	    }
	    return generatedDocument;
	}
	
}
