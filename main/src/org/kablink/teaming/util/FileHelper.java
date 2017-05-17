/**
 * Copyright (c) 1998-2015 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2015 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2015 Novell, Inc. All Rights Reserved.
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
package org.kablink.teaming.util;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import javax.mail.internet.MimeUtility;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.DefinableEntity;
import org.kablink.teaming.domain.FileAttachment;
import org.kablink.teaming.repository.RepositoryUtil;
import org.kablink.util.BrowserSniffer;

import org.springframework.util.FileCopyUtils;

/**
 * ?
 * 
 * @author Jong Kim
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
	
	public static long getLength(Binder binder, DefinableEntity entity, FileAttachment fa) {
		if(ObjectKeys.FI_ADAPTER.equals(fa.getRepositoryName())) {
			return RepositoryUtil.getContentLengthUnversioned(fa.getRepositoryName(), binder, entity, fa.getFileItem().getName());
		}
		else {
			return fa.getFileItem().getLength();
		}
	}
	
	public static String encodeFileName(HttpServletRequest request, String fileName) throws UnsupportedEncodingException {
		if(BrowserSniffer.is_ie(request) || BrowserSniffer.is_edge(request)) {
			String file = URLEncoder.encode(fileName, "UTF8");
			file = StringUtils.replace(file, "+", "%20");
			file = StringUtils.replace(file, "%2B", "+");
			return file;
		}
		else if(BrowserSniffer.is_mozilla(request)) {
			String file = MimeUtility.encodeText(fileName, "UTF8", "Q");
			file = StringUtils.replace(file, "+", "%20");
			file = StringUtils.replace(file, "%2B", "+");
			return file;
		}
		else {
			return fileName;
		}
	}
	
	public static boolean checkIfAttachment(String contentType) {
		boolean result = true;
		String[] types = SPropsUtil.getStringArray("view.file.directly", ",");
		for (int i=0; i < types.length; i++) {
			if (contentType.startsWith(types[i].trim())) return false;
		}
		return result;
	}
	
	/*
	 * Read the entire content of the file as a single string.
	 */
	public static String readString(String path, Charset charset) throws IOException {
		byte[] bytes = Files.readAllBytes(Paths.get(path));
		return new String(bytes, charset);
	}
	
	/*
	 * Read all lines from the file
	 */
	public static List<String> readLines(String path, Charset charset) throws IOException {
		return Files.readAllLines(Paths.get(path), charset);
	}
	
	public static String readFirstLine(String path, Charset charset) throws IOException {
		List<String> lines = readLines(path, charset);
		if(lines != null && lines.size() > 0)
			return lines.get(0);
		else
			return null;
	}
	
	public static void writeString(String path, Charset charset, String data) throws IOException  {
		try(FileOutputStream fos = new FileOutputStream(path);
				BufferedOutputStream bos = new BufferedOutputStream(fos);
				OutputStreamWriter osw = new OutputStreamWriter(bos, "UTF-8")) {
			osw.write(data);
		}
	}

	/**
	 * Returns the appropriate string to use for the
	 * Content-Disposition in an HTTP response header based on the
	 * given filename.
	 * 
	 * @param request
	 * @param defaultFileName
	 * @param actualFileName
	 * 
	 * @return
	 * 
	 * @throws UnsupportedEncodingException
	 */
	public static String getHttpContentDispositionForFilename(HttpServletRequest request, String defaultFileName, String actualFileName, String attachment) throws UnsupportedEncodingException {
		String reply = (attachment + "filename=\"" + defaultFileName + "\"; filename*=" + utf8EncodeFileNameForHttpContentDisposition(request, actualFileName));
		return reply;
	}
	
	public static String getHttpContentDispositionForFilename(HttpServletRequest request, String defaultFileName, String actualFileName) throws UnsupportedEncodingException {
		return getHttpContentDispositionForFilename(request, defaultFileName, actualFileName, "attachment; ");
	}
	
	private static String utf8EncodeFileNameForHttpContentDisposition(HttpServletRequest request, String fileName) throws UnsupportedEncodingException {
		String file = URLEncoder.encode(fileName, "UTF8");
		file = StringUtils.replace(file, "+", "%20");
		file = StringUtils.replace(file, "%2B", "+");
		return ("UTF-8''" + file);
	}
}
