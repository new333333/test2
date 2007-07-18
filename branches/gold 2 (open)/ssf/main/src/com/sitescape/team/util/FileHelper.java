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
package com.sitescape.team.util;

import java.io.File;
import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.FileCopyUtils;

/**
 * @author Jong Kim
 *
 */
public class FileHelper {
	
	private static Log logger = LogFactory.getLog(FileHelper.class);

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

	public static void delete(File file) throws IOException {
		if(!file.exists())
			return; // noop
		
		int count = 1;
		
		while(count <= 3) {
			if(file.delete())
				return;
		
			try {
				Thread.sleep(10);
			} 
			catch (InterruptedException e) {}
			
			count++;
		}
		
        throw new IOException("Could not delete file [" + file.getAbsolutePath() + "]");
	}
	
	public static void move(File source, File dest) throws IOException {
		delete(dest);
		
		mkdirsIfNecessary(dest.getParentFile());
		
		// First, try to rename it.
		if(source.renameTo(dest))
			return;
		
		// Simple renaming didn't do. Let's try copying.
		destructiveCopyRecursively(source, dest, true);
		
		// If you're still here, it means that the copy above was successful. 
		// It is safe to delete the source now. 
		deleteRecursively(source);
	}

	/**
	 * This copies the source (which may be either directory or file) to the 
	 * destination (NOT into the destination). As the method name indicates,
	 * unlike regular copy function, this performs a destructive copying
	 * meaning that the original content of the destination (whether directory
	 * or file) will be destroyed.  
	 * 
	 * @param source
	 * @param dest
	 * @throws IOException
	 */
	public static void destructiveCopyRecursively(File source, File dest, 
			boolean preserveLastModified) throws IOException {
		delete(dest);
		
		mkdirsIfNecessary(dest.getParentFile());
		
		if(source.isDirectory()) {
			mkdirs(dest);
			File[] sourceChildren = source.listFiles();
			if(sourceChildren != null) {
				for(int i = 0; i < sourceChildren.length; i++) {
					File sourceChild = sourceChildren[i];
					File destChild = new File(dest, sourceChild.getName());
					destructiveCopyRecursively(sourceChild, destChild, preserveLastModified);
				}
			}
		}
		else {
			FileCopyUtils.copy(source, dest);
		}
		if(preserveLastModified) {
			dest.setLastModified(source.lastModified());
		}
	}
	
    public static void deleteRecursively(File file) {
    	if(!file.exists())
    		return;
    	
        if (file.isDirectory()) {
            String[] children = file.list();
            for (int i=0; i<children.length; i++) {
                deleteRecursively(new File(file, children[i]));
            }
        }
    
        // We can now safely delete it.
        try {
        	delete(file);
        }
        catch(IOException e) {
        	logger.error(e);
        }
    }		
}
