package com.sitescape.ef.util;

import java.io.File;

/**
 * @author Jong Kim
 *
 */
public class FileHelper {
    
    /**
     * Creates directory.
     * 
     * @param dir
     * @throws FileCreationException Thrown if failed to create.  
     */
    public static void mkdirs(File dir) {
        if(!dir.mkdirs())
            throw new FileCreationException("Could not create directory [" + dir.getAbsolutePath() + "]");
    }
    
    /**
     * Creates directory only if the directory doesn't already exist. 
     * 
     * @param dir
     * @throws FileCreationException Thrown if the directory doesn't already exist and it failed to create it
     */
    public static void mkdirsIfNecessary(File dir) {
        if(!dir.exists()) {
            mkdirs(dir);
        }
    }
    
    public static void mkdirsIfNecessary(String dirPath) {
        mkdirsIfNecessary(new File(dirPath));
    }
}
