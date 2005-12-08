package com.sitescape.ef.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author Jong Kim
 *
 */
public class FileHelper {
    
	private static final int BUFFER_SIZE = 4096;
	
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
    
	public static void copyContent(InputStream in, OutputStream out) throws IOException {
		int len;
		byte[] buffer = new byte[BUFFER_SIZE];
		while((len = in.read(buffer)) != -1) {
			out.write(buffer, 0, len);
		}
		out.flush();
	}

}
