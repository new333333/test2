/**
 * Copyright (c) 1998-2009 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2009 Novell, Inc. All Rights Reserved.
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
package org.kablink.teaming.tools.portletproperties;

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
		String outPath = pathname + File.separator + "classes" + File.separator + "content";
		
        String []propNames = {"relevance_dashboard", "administration", "blog", "exception", "forum", 
        		"gallery", "guestbook", "mobile", "presence", "search", "task", 
        		"toolbar", "welcome", "widgettest",	"wiki",	"workspacetree", 
        		"workarea", "workarea_accessories", "workarea_context", "workarea_navigation"};
           	
        String []localeCodes = {"en", "da", "de", "es", "fr", "hu_HU", "it", "ja",	
        		"nl", "pl", "pt_BR", "ru_RU", "sv", "zh_CN", "zh_TW", "uk_UA"};

        String []localeCodes2 = {"en", "da_DK", "de_DE", "es_ES", "fr_FR", "hu_HU", "it_IT", "ja_JP",	
        		"nl_NL", "pl_PL", "pt_BR", "ru_RU", "sv_SV", "zh_CN", "zh_TW", "uk_UA"};
        
        try {
        generatePropFiles(pathname, outPath, propNames, localeCodes, localeCodes2);
            
	    } catch (Exception ex) {
	    	System.out.println("Can't write properties files in " + outPath + ": error is: " + ex.getLocalizedMessage());
	    	throw(ex);
	    } 
	}
				
	private static void generatePropFiles(String pathname, String outPath, String []propNames, String []localeCodes, String []localeCodes2)  throws Exception {
		ReloadableResourceBundleMessageSource rrbms = new ReloadableResourceBundleMessageSource();
		
		rrbms.setBasename(pathname + File.separator + "messages" + File.separator + "messages");
		rrbms.setDefaultEncoding("UTF-8");
		
		FileSystemResourceLoader fsrl = new FileSystemResourceLoader();
		
		rrbms.setResourceLoader(fsrl);
		
		for(int i = 0; i < propNames.length; i++) {
			for(int j = 0; j < localeCodes.length; j++) {
				File file=null;
				OutputStreamWriter writer=null;
				String outFile;
				
				if (localeCodes[j].equals("en"))
					outFile = outPath + File.separator + propNames[i] + ".properties";
				else
					outFile = outPath + File.separator + propNames[i] + "_" 
						+ localeCodes2[j] + ".native";
				
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
