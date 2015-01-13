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
package org.kablink.teaming.tools.portlet;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import org.xml.sax.SAXException;

/**
 * ?
 * 
 * @author ?
 */
@SuppressWarnings("unchecked")
public class GeneratePortletXml {
	public static void main(String[] args) {
		if ((args.length == 0) || (args.length > 1)) {
			System.out.println("usage: java GeneratePortletXml <WEB-INF pathname>");
			return;
		}
		try {
			doMain(args[0]);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	private static void doMain(String pathname) throws Exception {
		String inFile = pathname + File.separator +"portlet.xml.tmpl";
		String outFile = pathname + File.separator + "portlet.xml";
		
		Document document = null;
        SAXReader reader = fixSAXReaderSecurity(new SAXReader());
        InputStreamReader fIn=null;
        try {
        	fIn = new InputStreamReader(new FileInputStream(inFile), "UTF-8");
        	document = reader.read(fIn);
 
        } catch (Exception ex) {
        	System.out.println("Cannot read XML template file " + inFile + ": error is: " + ex.getLocalizedMessage());
        	throw ex;
        } finally {
        	if (fIn != null) {
        		try {
        			fIn.close(); 
        		} catch (Exception ex) {}
        	}
        }
		FileOutputStream fOut = null;
		XMLWriter xOut=null;
		try {
			//explicitly set encoding so there is no mistake.
			//cannot guarantee default will be set to UTF-8
			fOut = new FileOutputStream(outFile);
			OutputFormat fmt = OutputFormat.createPrettyPrint();
			fmt.setEncoding("UTF-8");
    		xOut = new XMLWriter(fOut, fmt);
           	Element strRoot = document.getRootElement();
            
           	String []localeCodes = {"en", "da", "de", "es", "fr", "hu_HU", "it", "ja",	
           			"nl", "pl", "pt_BR", "ru_RU", "sv", "uk_UA", "zh_CN", "zh_TW"};

            String []localeCodes2 = {"en", "da_DK", "de_DE", "es_ES", "fr_FR", "hu_HU", "it_IT", "ja_JP",	
            		"nl_NL", "pl_PL", "pt_BR", "ru_RU", "sv_SV", "uk_UA", "zh_CN", "zh_TW"};

           	generateTags(strRoot, localeCodes, localeCodes2, pathname);
            xOut.write(strRoot);
    		xOut.flush();
	    } catch (Exception ex) {
	    	System.out.println("Can't write XML file " + outFile + ":error is: " + ex.getLocalizedMessage());
	    	throw(ex);
	    } finally {
	    	if (xOut != null) xOut.close();
	    	else if (fOut != null) fOut.close();
	    }
	}
				
	private static void generateTags(Element node, String []localeCodes, String []localeCodes2, String pathname)  throws Exception {
		List <Element> elements = node.elements("portlet");
		
		for(int i = 0; i < elements.size(); i++) {
			Element lastEle = elements.get(i).element("resource-bundle");
			elements.get(i).remove(lastEle);
			for (int j=0; j<localeCodes.length; j++) {
				Element temp = elements.get(i).addElement("supported-locale");
				temp.addText(localeCodes2[j]);
			}
			elements.get(i).add(lastEle);
		}
	}
	
	/*
	 * Implements a fix for bug#901787 on a newly constructed
	 * SAXReader.
	 */
	private static SAXReader fixSAXReaderSecurity(SAXReader saxReader) {
		try {
			saxReader.setFeature("http://xml.org/sax/features/external-general-entities",   false);
			saxReader.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
		}
		catch (SAXException e) {
//			m_logger.error("fixSAXReaderSecurity( SAXException ):  ", e);
			saxReader = null;
		}
		return saxReader;
	}
}
