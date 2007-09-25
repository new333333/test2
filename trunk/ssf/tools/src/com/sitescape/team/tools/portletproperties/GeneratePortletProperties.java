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
package com.sitescape.team.tools.portletproperties;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.Locale;

import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.core.io.FileSystemResourceLoader;

public class GeneratePortletProperties {
	public static void main(String[] args) {
		if ((args.length == 0) || (args.length > 1)) {
			System.out.println("usage: java GeneratePortletProperties <WEB-INF pathname>");
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
		String outPath = pathname + "\\classes\\content";
		
        String []propNames = {"administration", "blog", "exception", "forum", 
        		"gallery", "guestbook", "presence", "search", "task", 
        		"toolbar", "welcome", "widgettest",	"wiki",	"workspacetree"};
           	
        String []localeCodes = {"en", "da", "de", "es", "fr", "it", "ja",	
        		"nl", "pl", "pt_BR", "sv", "zh_CN", "zh_TW"};
        
        try {
        generatePropFiles(pathname, outPath, propNames, localeCodes);
            
	    } catch (Exception ex) {
	    	System.out.println("Can't write properties files in " + outPath + ": error is: " + ex.getLocalizedMessage());
	    	throw(ex);
	    } 
	}
				
	private static void generatePropFiles(String pathname, String outPath, String []propNames, String []localeCodes)  throws Exception {
		ReloadableResourceBundleMessageSource rrbms = new ReloadableResourceBundleMessageSource();
		
		rrbms.setBasename(pathname + "\\messages\\messages");
		rrbms.setDefaultEncoding("UTF-8");
		
		FileSystemResourceLoader fsrl = new FileSystemResourceLoader();
		
		rrbms.setResourceLoader(fsrl);
		
		for(int i = 0; i < propNames.length; i++) {
			for(int j = 0; j < localeCodes.length; j++) {
				File file=null;
				OutputStreamWriter writer=null;
				String outFile;
				
				if (localeCodes[j].equals("en"))
					outFile = outPath + "\\" + propNames[i] + ".properties";
				else
					outFile = outPath + "\\" + propNames[i] + "_" 
						+ localeCodes[j] + ".native";
				
				file = new File(outFile);
				
				try {
					//explicitly set encoding so there is no mistake.
					//cannot guarantee default will be set to UTF-8
		    		
					FileOutputStream fout = new FileOutputStream(outFile);
					BufferedOutputStream bout = new BufferedOutputStream(fout);
					writer = new OutputStreamWriter(bout,"UTF-8");
		           	
		           	String begincode = "javax.portlet."; 
		           	String middlecode = propNames[i] + ".";   
		           	String code, value;
		           	String endcode="";
		           	
		           	writer.write("##\n## Resource bundle file for ss_"
		           			+ propNames[i] + " portlet\n##\n## Note: Do not "
		           			+ "add any other messages here.\n##\n\n");
		           	
		           	for(int k = 0; k < 3; k++) {
		           		if(k==0) endcode = "title";
		           		if(k==1) endcode = "short-title";
		           		if(k==2) endcode = "keywords";
		           		
		           		code = begincode + middlecode + endcode;
		           		value = rrbms.getMessage(code, null , "", new Locale(localeCodes[j]));
		           		writer.write(begincode + endcode + "=" + value);
		           		
		           		if(k==0 || k==1)
		           			writer.write("\n");
		           	}

		           	writer.flush();
			    } catch (Exception ex) {
			    	throw(ex);
			    } finally {
			    	if (writer != null) writer.close();
			    	else if (file != null) file = null;
			    }
			}
		}
	}
}
