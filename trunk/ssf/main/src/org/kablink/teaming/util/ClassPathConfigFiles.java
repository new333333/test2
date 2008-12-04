/**
 * The contents of this file are subject to the Common Public Attribution License Version 1.0 (the "CPAL");
 * you may not use this file except in compliance with the CPAL. You may obtain a copy of the CPAL at
 * http://www.opensource.org/licenses/cpal_1.0. The CPAL is based on the Mozilla Public License Version 1.1
 * but Sections 14 and 15 have been added to cover use of software over a computer network and provide for
 * limited attribution for the Original Developer. In addition, Exhibit A has been modified to be
 * consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT WARRANTY OF ANY KIND,
 * either express or implied. See the CPAL for the specific language governing rights and limitations
 * under the CPAL.
 * 
 * The Original Code is ICEcore. The Original Developer is SiteScape, Inc. All portions of the code
 * written by SiteScape, Inc. are Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * 
 * 
 * Attribution Information
 * Attribution Copyright Notice: Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by ICEcore]
 * Attribution URL: [www.icecore.com]
 * Graphic Image as provided in the Covered Code [web/docroot/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are defined in the CPAL as a
 * work which combines Covered Code or portions thereof with code not governed by the terms of the CPAL.
 * 
 * 
 * SITESCAPE and the SiteScape logo are registered trademarks and ICEcore and the ICEcore logos
 * are trademarks of SiteScape, Inc.
 */
package org.kablink.teaming.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

/**
 *
 * @author Jong Kim
 */
public class ClassPathConfigFiles {
    
    protected List<String> configFiles;
    protected List<Resource> resources;
    
    public void setConfigFiles(String[] cFiles) {
    	configFiles = new ArrayList<String>();
    	resources = new ArrayList<Resource>();
    	
    	for(int i = 0; i < cFiles.length; i++) {
    		String cFile = cFiles[i];
    		Resource resource = null;
    		if(cFile.startsWith("optional:")) {
    			cFile = cFile.substring(9);
    			resource = toResource(cFile);
    			if(!resource.exists()) {
    				// The optional resource does not exist. Proceed.
    				continue;
    			}
    		}
    		else {
    			resource = toResource(cFile);
    		}
			configFiles.add(cFile);
			resources.add(resource);    			
    	}
    }
    
    /**
     * Returns the number of config files. 
     * 
     * @return
     */
    public int size() {
        return configFiles.size();
    }
    
    /**
     * Returns the first file as <code>File</code>. 
     * 
     * @return
     * @throws IOException
     */
    public File getAsFile() throws IOException {
        return getAsFile(0);
    }
    
    /**
     * Returns the specified file as <code>File</code>.
     *  
     * @param index
     * @return
     * @throws IOException
     */
    public File getAsFile(int index) throws IOException {
    	return resources.get(index).getFile();
    }
    
    /**
     * Returns the first file as <code>InputStream</code>.
     * 
     * @return
     * @throws IOException
     */
    public InputStream getAsInputStream() throws IOException {
        return getAsInputStream(0);
    }
    
    /**
     * Returns the specified file as <code>InputStream</code>.
     * 
     * @param index
     * @return
     * @throws IOException
     */
    public InputStream getAsInputStream(int index) throws IOException {
    	return resources.get(index).getInputStream();
    }
    
    /**
     * Returns the resources.
     * @return
     */
    public Resource[] getResources() {
    	return resources.toArray(new Resource[resources.size()]);
    }
    
    private Resource toResource(String filePath) {
        return new ClassPathResource(filePath);
    }
}
