package com.sitescape.ef.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
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
     * @throws IOException Thrown if failed to create.  
     */
    public static void mkdirs(File dir) throws IOException {
        if(!dir.mkdirs())
            throw new IOException("Could not create directory [" + dir.getAbsolutePath() + "]");
    }
    
    /**
     * Creates directory only if the directory doesn't already exist. 
     * 
     * @param dir
     * @throws IOException Thrown if the directory doesn't already exist and it failed to create it
     */
    public static void mkdirsIfNecessary(File dir) throws IOException {
        if(!dir.exists()) {
            mkdirs(dir);
        }
    }
    
    public static void mkdirsIfNecessary(String dirPath) throws IOException {
        mkdirsIfNecessary(new File(dirPath));
    }
    
    public static void copyContent(File in, File out) throws IOException {
    	FileInputStream fis = new FileInputStream(in);
    	
    	try {
    		FileOutputStream fos = new FileOutputStream(out);
    		
    		try {
    			copyContent(fis, fos);
    		}
    		finally {
    			fos.close();
    		}
    	}
    	finally {
    		fis.close();
    	}
    }
    
	public static void copyContent(InputStream in, OutputStream out) throws IOException {
		int len;
		byte[] buffer = new byte[BUFFER_SIZE];
		while((len = in.read(buffer)) != -1) {
			out.write(buffer, 0, len);
		}
		out.flush();
	}

	public static void delete(File file) throws IOException {
		int count = 1;
		
		while(count <= 3) {
			if(file.delete())
				return;
		
			try {
				Thread.currentThread().sleep(1);
			} 
			catch (InterruptedException e) {}
			
			count++;
		}
		
        throw new IOException("Could not delete file [" + file.getAbsolutePath() + "]");
	}
	
	public static void move(File source, File dest) throws IOException {
		if(dest.exists() && !dest.delete())
			throw new IOException("Destination file [" + dest.getAbsolutePath() + "] already exists and could not be deleted");
		
		// First, try to rename it.
		if(source.renameTo(dest))
			return;
		
		// Simple renaming didn't do the trick. We will have to copy the content. 
		copyContent(source, dest);
		
		// Delete the source.
		delete(source);
	}
}
