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
import java.util.Date;

import org.kablink.teaming.rest.v1.model.FileProperties;
import org.kablink.teaming.rest.v1.model.FileVersionProperties;
import org.kablink.teaming.rest.v1.model.SearchResultList;

/**
 * @author jong
 *
 */
public interface Api {
	public FileProperties writeFile(String entityType, long entityId, String filename, File inFile);
	
	public FileProperties writeFile(String entityType, long entityId, String filename, InputStream file);
	
	public FileProperties writeFile(String fileId, File inFile);
	
	public FileProperties writeFile(String fileId, InputStream file, String mimeType);
		
	public FileProperties writeFile(String entityType, long entityId, String filename, File inFile, String dataName, Date modDate, Integer lastVersionNumber, Integer lastMajorVersionNumber, Integer lastMinorVersionNumber);
	
	public FileProperties writeFile(String entityType, long entityId, String filename, InputStream file, String dataName, Date modDate, Integer lastVersionNumber, Integer lastMajorVersionNumber, Integer lastMinorVersionNumber);
	
	public FileProperties writeFile(String fileId, File inFile, String dataName, Date modDate, Integer lastVersionNumber, Integer lastMajorVersionNumber, Integer lastMinorVersionNumber);
	
	public FileProperties writeFile(String fileId, InputStream file, String mimeType, String dataName, Date modDate, Integer lastVersionNumber, Integer lastMajorVersionNumber, Integer lastMinorVersionNumber);
	
	public InputStream readFile(String entityType, long entityId, String filename);
	
	public InputStream readFile(String fileId);
	
	public FileProperties readFileProperties(String entityType, long entityId, String filename);
	
	public FileProperties readFileProperties(String fileId);
	
	public FileProperties updateFileProperties(String entityType, long entityId, String filename, FileProperties fileProperties);
	
	public FileProperties updateFileProperties(String fileId, FileProperties fileProperties);
	
	public void deleteFile(String entityType, long entityId, String filename);
	
	public void deleteFile(String fileId);
	
	public SearchResultList<FileVersionProperties> getFileVersions(String entityType, long entityId, String filename);
	
	public SearchResultList<FileVersionProperties> getFileVersions(String fileId);

}
