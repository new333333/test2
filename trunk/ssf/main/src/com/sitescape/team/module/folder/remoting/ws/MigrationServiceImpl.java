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
package com.sitescape.team.module.folder.remoting.ws;

import com.sitescape.team.context.request.RequestContextHolder;
import com.sitescape.team.domain.HistoryStamp;
import com.sitescape.team.domain.NoUserByTheNameException;
import com.sitescape.team.domain.Principal;
import com.sitescape.team.domain.User;
import com.sitescape.team.module.file.WriteFilesException;
import com.sitescape.team.remoting.RemotingException;

public class MigrationServiceImpl extends FolderServiceImpl implements
		MigrationService {

	public long addFolder(long parentId, String definitionId,
			String inputDataAsXML, Timestamps timestamps) {
		return super.addFolder(parentId, definitionId, inputDataAsXML);
	}

	public long addFolderEntry(long binderId, String definitionId,
			String inputDataAsXML, String attachedFileName,
			Timestamps timestamps) {
		long entryId = super.addFolderEntry(binderId, definitionId, inputDataAsXML, attachedFileName);
		alterTimestamps(binderId, entryId, timestamps);
		return entryId;
	}

	public long addReply(long binderId, long parentId, String definitionId,
			String inputDataAsXML, Timestamps timestamps) {
		long entryId = super.addReply(binderId, parentId, definitionId, inputDataAsXML);
		alterTimestamps(binderId, entryId, timestamps);
		return entryId;
	}

	public void uploadFolderFile(long binderId, long entryId,
			String fileUploadDataItemName, String fileName,
			Timestamps timestamps) {
		super.uploadFolderFile(binderId, entryId, fileUploadDataItemName, fileName);
	}
	
	protected void alterTimestamps(long binderId, long entryId, Timestamps timestamps)
	{
		User user = RequestContextHolder.getRequestContext().getUser();
		Principal creator = null;
		try {
			creator = getProfileModule().findUserByName(timestamps.getCreator());
		} catch(NoUserByTheNameException e) {
			creator = user;
		}
		HistoryStamp creation = new HistoryStamp(creator, timestamps.getCreationDate().getTime());
		Principal modifier = user;
		try {
			modifier = getProfileModule().findUserByName(timestamps.getModifier());
		} catch(NoUserByTheNameException e) {
			modifier = user;
		}
		HistoryStamp modification = new HistoryStamp(modifier, timestamps.getModificationDate().getTime());
		try {
			getFolderModule().changeEntryTimstamps(binderId, entryId, creation, modification);
		} catch(WriteFilesException e) {
			throw new RemotingException(e);
		}
	}
}
