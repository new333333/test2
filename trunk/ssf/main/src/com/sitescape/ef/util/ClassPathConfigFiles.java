package com.sitescape.ef.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

/**
 *
 * @author Jong Kim
 */
public class ClassPathConfigFiles {
    
    protected String[] configFiles;
    protected Resource[] resources;
    
    public void setConfigFiles(String[] configFiles) {
        this.configFiles = configFiles;
        this.resources = new Resource[configFiles.length];
        for(int i = 0; i < configFiles.length; i++)
            this.resources[i] = toResource(configFiles[i]);
    }
    
    /**
     * Returns the number of config files. 
     * 
     * @return
     */
    public int size() {
        return configFiles.length;
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
        return resources[index].getFile();
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
        return resources[index].getInputStream();
    }
    
    private Resource toResource(String filePath) {
        return new ClassPathResource(filePath);
    }
}
