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
package com.sitescape.team.remoting.ws.service.folder;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import org.dom4j.Document;

import com.sitescape.team.ObjectKeys;
import com.sitescape.team.module.file.WriteFilesException;
import com.sitescape.team.remoting.RemotingException;
import com.sitescape.team.remoting.ws.util.DomInputData;
import com.sitescape.team.util.stringcheck.StringCheckUtil;

public class MigrationServiceImpl extends FolderServiceImpl implements
		MigrationService {



	public long addFolderEntry(String accessToken, long binderId, String definitionId,
			String inputDataAsXML, String creator, Calendar creationDate, String modifier, Calendar modificationDate) {
		HashMap options = new HashMap();
		options.put(ObjectKeys.INPUT_OPTION_NO_INDEX, Boolean.TRUE);
    	options.put(ObjectKeys.INPUT_OPTION_NO_WORKFLOW, Boolean.TRUE);
		getTimestamps(options, creator, creationDate, modifier, modificationDate);
		return addFolderEntry(accessToken, binderId, definitionId, inputDataAsXML, null, options);
	}

	public long addReply(String accessToken, long binderId, long parentId, String definitionId,
			String inputDataAsXML, String creator, Calendar creationDate, String modifier, Calendar modificationDate) {
		Map options = new HashMap();
		options.put(ObjectKeys.INPUT_OPTION_NO_INDEX, Boolean.TRUE);
    	options.put(ObjectKeys.INPUT_OPTION_NO_WORKFLOW, Boolean.TRUE);
		getTimestamps(options, creator, creationDate, modifier, modificationDate);
		return addReply(accessToken, binderId, parentId, definitionId, inputDataAsXML, null, options);
	}

	public void uploadFolderFile(String accessToken, long binderId, long entryId,
			String fileUploadDataItemName, String fileName,
			String modifier, Calendar modificationDate) {
		throw new UnsupportedOperationException();
	}
	
	public void uploadFolderFileStaged(String accessToken, long binderId, long entryId, 
			String fileUploadDataItemName, String fileName, String stagedFileRelativePath, String modifier, Calendar modificationDate){
			
		Map options = new HashMap();
		options.put(ObjectKeys.INPUT_OPTION_NO_INDEX, Boolean.TRUE);
		options.put(ObjectKeys.INPUT_OPTION_NO_WORKFLOW, Boolean.TRUE);
    	options.put(ObjectKeys.INPUT_OPTION_NO_MODIFICATION_DATE, Boolean.TRUE);
		uploadFolderFileStaged(accessToken, binderId, entryId, fileUploadDataItemName, fileName, 
				stagedFileRelativePath, modifier, modificationDate, options);
	}
	public void addEntryWorkflow(String accessToken, long binderId, long entryId, String definitionId, String startState, String modifier, Calendar modificationDate) {
		Map options = new HashMap();
		options.put(ObjectKeys.INPUT_OPTION_NO_INDEX, Boolean.TRUE);
		options.put(ObjectKeys.INPUT_OPTION_FORCE_WORKFLOW_STATE, startState);
		getTimestamps(options, null, null, modifier, modificationDate);
		addEntryWorkflow(accessToken, binderId, entryId, definitionId, options);
	}
	public long addBinder(String accessToken, long parentId, String definitionId,
			String inputDataAsXML, String creator, Calendar creationDate, String modifier, Calendar modificationDate) {
		inputDataAsXML = StringCheckUtil.check(inputDataAsXML);
		
		try {
			Map options = new HashMap();
			//let binder be indexed, so it can be found
			getTimestamps(options, creator, creationDate, modifier, modificationDate);
			Document doc = getDocument(inputDataAsXML);
			DomInputData inputData = new DomInputData(doc, getIcalModule());
			return getBinderModule().addBinder(parentId, definitionId, inputData, null, options).longValue();
		} catch(WriteFilesException e) {
			throw new RemotingException(e);
		}
	}

	protected void getTimestamps(Map options, String creator, Calendar creationDate,
			  String modifier, Calendar modificationDate)
	{
		if (creator != null) options.put(ObjectKeys.INPUT_OPTION_CREATION_NAME, creator);
		if (creationDate != null) options.put(ObjectKeys.INPUT_OPTION_CREATION_DATE, creationDate);
		if (modifier != null) options.put(ObjectKeys.INPUT_OPTION_MODIFICATION_NAME, modifier);
		if (modificationDate != null) options.put(ObjectKeys.INPUT_OPTION_MODIFICATION_DATE, modificationDate);
	}
}
