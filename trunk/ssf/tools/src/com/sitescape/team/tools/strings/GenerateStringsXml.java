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
package com.sitescape.team.tools.strings;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.Locale;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.core.io.FileSystemResourceLoader;

public class GenerateStringsXml {
	public static void main(String[] args) {
		if ((args.length == 0) || (args.length > 1)) {
			System.out.println("usage: java GenerateStringsXml <WEB-INF pathname>");
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
		
		String inFile = pathname + "\\xslt\\strings.xml.tmpl";
		String outFile = pathname + "\\xslt\\strings.xml";
		
		Document document = null;
        SAXReader reader = new SAXReader();
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
            
           	
           	String []localeCodes = {"en", "da", "de", "es", "fr", "it", "ja",	
           			"nl", "pl", "pt_BR", "sv", "zh_CN", "zh_TW"};
           	
           	generateTags(strRoot, localeCodes, pathname);
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
				
	private static void generateTags(Element node, String []localeCodes, String pathname)  throws Exception {
		List <Element> elements = node.elements();
		
		ReloadableResourceBundleMessageSource rrbms = new ReloadableResourceBundleMessageSource();
		
		rrbms.setBasename(pathname + "\\messages\\messages");
		rrbms.setDefaultEncoding("UTF-8");
		
		FileSystemResourceLoader fsrl = new FileSystemResourceLoader();
		
		rrbms.setResourceLoader(fsrl);
		for (Element element:elements) {
			String name = element.attributeValue("name");
			for (int j=0; j<localeCodes.length; j++) {
				Element temp = element.addElement("lang");
				temp.addAttribute("name", localeCodes[j]);
				String text= rrbms.getMessage("strings.xml." + name, null , "", new Locale(localeCodes[j]));
				temp.addText(text);
			}
		}
	}
}
