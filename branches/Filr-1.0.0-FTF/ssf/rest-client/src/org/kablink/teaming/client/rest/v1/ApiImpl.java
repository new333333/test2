/**
 * Copyright (c) 1998-2011 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2009 Novell, Inc. All Rights Reserved.
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

package org.kablink.teaming.client.rest.v1;

import java.io.File;
import java.io.InputStream;
import java.net.URI;
import java.util.Date;

import javax.activation.MimetypesFileTypeMap;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;

import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;
import org.kablink.teaming.rest.v1.model.FileProperties;
import org.kablink.teaming.rest.v1.model.FileVersionProperties;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import org.kablink.teaming.rest.v1.model.SearchResultList;

/**
 * @author jong
 *
 */
public class ApiImpl implements Api {
	
	private final String FILE_TEMPLATE_BY_NAME = "file/name/{entityType}/{entityId}/{filename}";
	private final String FILE_TEMPLATE_BY_ID = "file/id/{fileid}";
	
	private ApiClient conn;
	
	ApiImpl(ApiClient conn) {
		this.conn = conn;
	}
	
	public FileProperties writeFile(String entityType, long entityId, String filename, File inFile) {
		return writeFileContent(entityType, entityId, filename, inFile, null, null, null, null, null);
	}
	
	public FileProperties writeFile(String entityType, long entityId, String filename, InputStream file) {
		return writeFileContent(entityType, entityId, filename, file, null, null, null, null, null);
	}
	
	public FileProperties writeFile(String fileId, File inFile) {
		String mt = new MimetypesFileTypeMap().getContentType(inFile.getName());
		return writeFileContent(fileId, inFile, mt, null, null, null, null, null);
	}
	
	public FileProperties writeFile(String fileId, InputStream file, String mimeType) {
		return writeFileContent(fileId, file, mimeType, null, null, null, null, null);
	}
	
	public FileProperties writeFile(String entityType, long entityId,
			String filename, File inFile, String dataName, Date modDate,
			Integer lastVersionNumber, Integer lastMajorVersionNumber,
			Integer lastMinorVersionNumber) {
		return writeFileContent(entityType, entityId, filename, inFile, dataName, modDate, lastVersionNumber, lastMajorVersionNumber, lastMinorVersionNumber);
	}

	public FileProperties writeFile(String entityType, long entityId,
			String filename, InputStream file, String dataName, Date modDate,
			Integer lastVersionNumber, Integer lastMajorVersionNumber,
			Integer lastMinorVersionNumber) {
		return writeFileContent(entityType, entityId, filename, file, dataName, modDate, lastVersionNumber, lastMajorVersionNumber, lastMinorVersionNumber);
	}

	public FileProperties writeFile(String fileId, File inFile, String dataName,
			Date modDate, Integer lastVersionNumber,
			Integer lastMajorVersionNumber, Integer lastMinorVersionNumber) {
		String mt = new MimetypesFileTypeMap().getContentType(inFile.getName());
		return writeFileContent(fileId, inFile, mt, dataName, modDate, lastVersionNumber, lastMajorVersionNumber, lastMinorVersionNumber);
	}

	public FileProperties writeFile(String fileId, InputStream file,
			String mimeType, String dataName, Date modDate,
			Integer lastVersionNumber, Integer lastMajorVersionNumber,
			Integer lastMinorVersionNumber) {
		return writeFileContent(fileId, file, mimeType, dataName, modDate, lastVersionNumber, lastMajorVersionNumber, lastMinorVersionNumber);
	}

	public File readFileAsFile(String entityType, long entityId, String filename) {
		UriBuilder ub = UriBuilder.fromUri(conn.getBaseUrl()).path(FILE_TEMPLATE_BY_NAME);
		URI resourceUri = ub.build(entityType, entityId, filename);
		Client c = conn.getClient();		
		WebResource r = c.resource(resourceUri);
		return r.accept(MediaType.WILDCARD).get(File.class);
	}
	
	public InputStream readFile(String entityType, long entityId, String filename) {
		UriBuilder ub = UriBuilder.fromUri(conn.getBaseUrl()).path(FILE_TEMPLATE_BY_NAME);
		URI resourceUri = ub.build(entityType, entityId, filename);
		Client c = conn.getClient();		
		WebResource r = c.resource(resourceUri);
		return r.accept(MediaType.WILDCARD).get(InputStream.class);
	}
	
	public File readFileAsFile(String fileId) {
		UriBuilder ub = UriBuilder.fromUri(conn.getBaseUrl()).path(FILE_TEMPLATE_BY_ID);
		URI resourceUri = ub.build(fileId);
		Client c = conn.getClient();		
		WebResource r = c.resource(resourceUri);
		return r.accept(MediaType.WILDCARD).get(File.class);
	}
	
	public InputStream readFile(String fileId) {
		UriBuilder ub = UriBuilder.fromUri(conn.getBaseUrl()).path(FILE_TEMPLATE_BY_ID);
		URI resourceUri = ub.build(fileId);
		Client c = conn.getClient();		
		WebResource r = c.resource(resourceUri);
		return r.accept(MediaType.WILDCARD).get(InputStream.class);
	}
	
	public FileProperties readFileProperties(String entityType, long entityId, String filename) {
		Client c = conn.getClient();		
		URI resourceUri = UriBuilder.fromUri(conn.getBaseUrl()).path(FILE_TEMPLATE_BY_NAME).path("properties").build(entityType, entityId, filename);
		WebResource r = c.resource(resourceUri);
		return r.accept(conn.getAcceptableMediaTypes()).get(FileProperties.class);
	}
	
	public FileProperties readFileProperties(String fileId) {
		Client c = conn.getClient();		
		URI resourceUri = UriBuilder.fromUri(conn.getBaseUrl()).path(FILE_TEMPLATE_BY_ID).path("properties").build(fileId);
		WebResource r = c.resource(resourceUri);
		return r.accept(conn.getAcceptableMediaTypes()).get(FileProperties.class);
	}
	
	public FileProperties updateFileProperties(String entityType, long entityId, String filename, FileProperties fileProperties) {
		Client c = conn.getClient();		
		URI resourceUri = UriBuilder.fromUri(conn.getBaseUrl()).path(FILE_TEMPLATE_BY_NAME).path("properties").build(entityType, entityId, filename);
		WebResource r = c.resource(resourceUri);
		return r.accept(conn.getAcceptableMediaTypes()).post(FileProperties.class, fileProperties);
	}
	
	public FileProperties updateFileProperties(String fileId, FileProperties fileProperties) {
		Client c = conn.getClient();		
		URI resourceUri = UriBuilder.fromUri(conn.getBaseUrl()).path(FILE_TEMPLATE_BY_ID).path("properties").build(fileId);
		WebResource r = c.resource(resourceUri);
		return r.accept(conn.getAcceptableMediaTypes()).post(FileProperties.class, fileProperties);
	}
	
	public void deleteFile(String entityType, long entityId, String filename) {
		Client c = conn.getClient();		
		URI resourceUri = UriBuilder.fromUri(conn.getBaseUrl()).path(FILE_TEMPLATE_BY_NAME).build(entityType, entityId, filename);
		WebResource r = c.resource(resourceUri);
		r.accept(conn.getAcceptableMediaTypes()).delete();
	}
	
	public void deleteFile(String fileId) {
		Client c = conn.getClient();		
		URI resourceUri = UriBuilder.fromUri(conn.getBaseUrl()).path(FILE_TEMPLATE_BY_ID).build(fileId);
		WebResource r = c.resource(resourceUri);
		r.accept(conn.getAcceptableMediaTypes()).delete();
	}
	
	public SearchResultList<FileVersionProperties> getFileVersions(String entityType, long entityId, String filename) {
		Client c = conn.getClient();		
		URI resourceUri = UriBuilder.fromUri(conn.getBaseUrl()).path(FILE_TEMPLATE_BY_NAME).path("versions").build(entityType, entityId, filename);
		WebResource r = c.resource(resourceUri);
		return r.accept(conn.getAcceptableMediaTypes()).get(SearchResultList.class);
	}
	
	public SearchResultList<FileVersionProperties> getFileVersions(String fileId) {
		Client c = conn.getClient();		
		URI resourceUri = UriBuilder.fromUri(conn.getBaseUrl()).path(FILE_TEMPLATE_BY_ID).path("versions").build(fileId);
		WebResource r = c.resource(resourceUri);
		return r.accept(conn.getAcceptableMediaTypes()).get(SearchResultList.class);
	}

	private String ISO8601FromDate(Date date) {
		String dateStr = null;
		if(date != null) {
			DateTime dateTime = new DateTime(date);
			dateStr = ISODateTimeFormat.dateTime().print(dateTime);
		}
		return dateStr;
	}
	
	private FileProperties writeFileContent(String entityType, long entityId, String filename, Object file, String dataName, Date modDate, Integer lastVersionNumber, Integer lastMajorVersionNumber, Integer lastMinorVersionNumber) {
		UriBuilder ub = getFileUriBuilder(FILE_TEMPLATE_BY_NAME, dataName, modDate, lastVersionNumber, lastMajorVersionNumber, lastMinorVersionNumber);
		URI resourceUri = ub.build(entityType, entityId, filename);
		Client c = conn.getClient();		
		WebResource r = c.resource(resourceUri);
		String mt = new MimetypesFileTypeMap().getContentType(filename);
		return r.accept(conn.getAcceptableMediaTypes()).entity(file, mt).post(FileProperties.class);
	}

	private FileProperties writeFileContent(String fileId, Object file, String mimeType, String dataName, Date modDate, Integer lastVersionNumber, Integer lastMajorVersionNumber, Integer lastMinorVersionNumber) {
		UriBuilder ub = getFileUriBuilder(FILE_TEMPLATE_BY_ID, dataName, modDate, lastVersionNumber, lastMajorVersionNumber, lastMinorVersionNumber);
		URI resourceUri = ub.build(fileId);
		Client c = conn.getClient();		
		WebResource r = c.resource(resourceUri);
		return r.accept(conn.getAcceptableMediaTypes()).entity(file, mimeType).post(FileProperties.class);
	}
	
	private UriBuilder getFileUriBuilder(String fileResourceTemplate, String dataName, Date modDate, Integer lastVersionNumber, Integer lastMajorVersionNumber, Integer lastMinorVersionNumber) {
		String modDateStr = ISO8601FromDate(modDate);
		UriBuilder ub = UriBuilder.fromUri(conn.getBaseUrl()).path(FILE_TEMPLATE_BY_NAME);
		if(dataName != null)
			ub.queryParam("dataName", dataName);
		if(modDateStr != null)
			ub.queryParam("modDate", modDateStr);
		if(lastVersionNumber != null)
			ub.queryParam("lastVersionNumber", lastVersionNumber);
		if(lastMajorVersionNumber != null)
			ub.queryParam("lastMajorVersionNumber", lastMajorVersionNumber);
		if(lastMinorVersionNumber != null)
			ub.queryParam("lastMinorVersionNumber", lastMinorVersionNumber);
		return ub;
	}
}
