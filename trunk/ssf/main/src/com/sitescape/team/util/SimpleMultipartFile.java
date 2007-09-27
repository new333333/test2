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
package com.sitescape.team.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

/**
 * This class implements only a subset of the methods defined in 
 * <code>MultipartFile</code> interface that are actually needed
 * by Aspen. Furthermore, it adds a couple of additional methods - 
 * <code>getFile</code> and <code>close</code>.  
 * 
 * @author jong
 *
 */
public class SimpleMultipartFile implements MultipartFile {

	protected String fileName;
	
	// Only one of the following two is set per instance.
	protected InputStream content;
	
	protected File file;
	protected boolean deleteOnClose = false;

	public SimpleMultipartFile(String fileName, InputStream content) {
		this.fileName = fileName;
		this.content = content;
	}
	
	public SimpleMultipartFile(String fileName, File file, boolean deleteOnClose) {
		this.fileName = fileName;
		this.file = file;
		this.deleteOnClose = deleteOnClose;
	}
	
	public String getName() {
		throw new UnsupportedOperationException();
	}

	public boolean isEmpty() {
		throw new UnsupportedOperationException();
	}

	public String getOriginalFilename() {
		return fileName;
	}

	public String getContentType() {
		return null;
	}

	/**
	 * Return the size of the file in bytes.
	 * <p>
	 * Note: This method overrides/extends the semantics of the interface 
	 * it is implementing. The interface does not specify what to return
	 * when the size is unknown. In other word, it assumes that the size
	 * is always computable. This particular implementation extends the
	 * semantics by allowing for return value of -1 (or any negative 
	 * long value) to indicate to the caller that the size is unknown.
	 * In purely OO sense, this is a violation of the contract defined
	 * by the interface, hence very bad programming practice that should be
	 * avoided whenever possible. However, the cost of avoiding this
	 * hack is too high for our project. So I think it justifies this
	 * small wrinkle (we are not living in an ideal world). 
	 */
	public long getSize() {
		if(file != null)
			return file.length();
		else
			return -1;
	}

	public byte[] getBytes() throws IOException {
		if(file != null)
			return FileCopyUtils.copyToByteArray(file);
		else
			return FileCopyUtils.copyToByteArray(content);
	}

	public InputStream getInputStream() throws IOException {
		if(file != null)
			return new BufferedInputStream(new FileInputStream(file));
		else 
			return content;
	}

	public void transferTo(File dest) throws IOException, IllegalStateException {
		if (dest.exists() && !dest.delete()) {
			throw new IOException(
					"Destination file [" + dest.getAbsolutePath() + "] already exists and could not be deleted");
		}
		
		if(file != null) {
			FileCopyUtils.copy(file, dest);
		}
		else {
			FileCopyUtils.copy(content, new BufferedOutputStream(new FileOutputStream(dest)));
		}
	}
	
	/**
	 * Returns a file if the data is already in a file. 
	 * Otherwise returns <code>null</code>.
	 * 
	 * @return file or <code>null</code>
	 */
	public File getFile() {
		return file;
	}
	
	/**
	 * Releases resources associated with this object.
	 */
	public void close() {
		if(content != null) {
			try {
				// The content may have already been closed. 
				// But closing it multiple times shouldn't cause a trouble.
				content.close();
			}
			catch(IOException ignore) {}
		}
		
		if(file != null && deleteOnClose) {
			file.delete();
		}
	}
}
