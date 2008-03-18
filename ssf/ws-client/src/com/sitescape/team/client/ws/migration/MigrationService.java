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
package com.sitescape.team.client.ws.migration;

import javax.activation.DataSource;

import com.sitescape.team.client.ws.WebServiceClient;
import com.sitescape.team.client.ws.migration.model.Timestamps;

public class MigrationService extends WebServiceClient implements MigrationServiceSoap {

	public MigrationService()
	{
		super("MigrationService");
	}
	
	public long addFolder(String accessToken, long parentId, String definitionId, String inputDataAsXML, Timestamps timestamps)
	{
		Long folderId = (Long)
			fetch("addFolder", new Object[] {accessToken, new Long(parentId), definitionId, inputDataAsXML, timestamps});
		return folderId.longValue();
	}

	public long addFolderEntry(String accessToken, long binderId, String definitionId,
			String inputDataAsXML, String attachedFileName, Timestamps timestamps)
	{
		Long entryId = (Long)
			fetch("addFolderEntry", new Object[] {accessToken, new Long(binderId), definitionId, inputDataAsXML, attachedFileName, timestamps}, attachedFileName);
		return entryId.longValue();
	}

	public long addReply(String accessToken, long binderId, long parentId, String definitionId,
			String inputDataAsXML, Timestamps timestamps)
	{
		Long entryId = (Long)
			fetch("addReply", new Object[] {accessToken, new Long(binderId), new Long(parentId), definitionId, inputDataAsXML, timestamps});
		return entryId.longValue();
	}

	public void uploadFolderFile(String accessToken, long binderId, long entryId,
			String fileUploadDataItemName, String fileName, Timestamps timestamps)
	{
		throw new UnsupportedOperationException("You must use the extended version, and pass in a DataSource");
	}
	
	public void uploadFolderFile(String accessToken, long binderId, long entryId,
			String fileUploadDataItemName, DataSource source, Timestamps timestamps)
	{
		fetch("uploadFolderFile", new Object[] {accessToken, new Long(binderId), new Long(entryId), fileUploadDataItemName, source.getName(), timestamps}, source);
	}
}
