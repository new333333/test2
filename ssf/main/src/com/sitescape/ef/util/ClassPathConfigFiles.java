package com.sitescape.ef.util;

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
    			// The following method call is used to test whether or not
    			// the specified file exists. 
    			try {
    				resource.getURL();
    			}
    			catch(Exception e) {
    				continue; // cannot find it. proceed.
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
    
    private Resource toResource(String filePath) {
        return new ClassPathResource(filePath);
    }
}
