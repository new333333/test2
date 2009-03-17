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
package org.kablink.teaming.remoting.ws.util.attachments;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import javax.activation.DataHandler;

import org.kablink.teaming.util.FileHelper;
import org.kablink.teaming.util.FileModDateSupport;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;


/**
 * <code>MultipartFile</code> implementation for Apache Axis attachment support.
 * <p>
 * To be precise, this implemmentation doesn't actually represent an uploaded
 * file received in a multipart request. Instead, it acts as an adaptor so
 * as to provide application with the convenience of accessing uploaded files
 * through an uniform interface no matter how the files were uploaded.
 * Only a small subset of the methods in the interface are actually implemented
 * and the rest will throw an <code>UnsupportedOperationException</code>
 * exception if invoked. 
 * 
 * @author jong
 *
 */
public class AxisMultipartFile implements MultipartFile, FileModDateSupport {

	private String fileName;
	private DataHandler dataHandler;
	private File file;
	private long size;
	private Date modDate;
	private String modifier=null;

	public AxisMultipartFile(String fileName, DataHandler dataHandler) {
		this.fileName = fileName;
		this.dataHandler = dataHandler;
		this.file = new File(dataHandler.getName());
		this.size = file.length();
	}
	
	public AxisMultipartFile(String fileName, DataHandler dataHandler, String modifier, Date modDate) {
		this.fileName = fileName;
		this.dataHandler = dataHandler;
		this.file = new File(dataHandler.getName());
		this.size = file.length();
		this.modifier = modifier;
		this.modDate = modDate;
	}

	public String getName() {
		throw new UnsupportedOperationException();
	}

	public boolean isEmpty() {
		return (this.size == 0);
	}

	public String getOriginalFilename() {
		return fileName;
	}

	public String getContentType() {
		return null;
	}

	public long getSize() {
		return this.size;
	}

	// Only one of the following three methods can be invoked and at most once.
	
	public byte[] getBytes() throws IOException {
		return FileCopyUtils.copyToByteArray(getInputStream());
	}

	public InputStream getInputStream() throws IOException {
		return dataHandler.getInputStream();
	}

	public void transferTo(File dest) throws IOException, IllegalStateException {
		FileHelper.move(this.file, dest);
	}
	public Date getModDate() {
		return modDate;
	}
	public void setModDate(Date modDate) {
		this.modDate = modDate;
	}
	public String getModifier() {
		return modifier;
	}
	public void setModifier(String modifier) {
		this.modifier = modifier;
	}
}
