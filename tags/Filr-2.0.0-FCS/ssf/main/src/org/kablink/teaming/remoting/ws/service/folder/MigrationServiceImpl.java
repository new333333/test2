/**
 * Copyright (c) 1998-2009 Novell, Inc. and its licensors. All rights reserved.
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
package org.kablink.teaming.remoting.ws.service.folder;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import org.dom4j.Document;
import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.Folder;
import org.kablink.teaming.domain.Principal;
import org.kablink.teaming.domain.Subscription;
import org.kablink.teaming.module.binder.impl.WriteEntryDataException;
import org.kablink.teaming.module.file.WriteFilesException;
import org.kablink.teaming.remoting.ws.RemotingException;
import org.kablink.teaming.remoting.ws.model.DefinableEntity;
import org.kablink.teaming.remoting.ws.model.FolderEntry;
import org.kablink.teaming.remoting.ws.model.Timestamp;
import org.kablink.teaming.remoting.ws.util.DomInputData;
import org.kablink.teaming.remoting.ws.util.ModelInputData;
import org.kablink.teaming.security.AccessControlException;


public class MigrationServiceImpl extends FolderServiceImpl implements
		MigrationService {



	public long migration_addFolderEntryWithXML(String accessToken, long binderId, String definitionId,
			String inputDataAsXML, String creator, Calendar creationDate, String modifier, 
			Calendar modificationDate, boolean subscribe) {
		HashMap options = new HashMap();
		options.put(ObjectKeys.INPUT_OPTION_NO_INDEX, Boolean.TRUE);
    	options.put(ObjectKeys.INPUT_OPTION_NO_WORKFLOW, Boolean.TRUE);
		getTimestamps(options, creator, creationDate, modifier, modificationDate);
		long entryId = addFolderEntry(accessToken, binderId, definitionId, inputDataAsXML, null, options);
		if (subscribe) {
			Map<Integer,String[]> styles = new HashMap();
			styles.put(Subscription.MESSAGE_STYLE_NO_ATTACHMENTS_EMAIL_NOTIFICATION, new String[]{Principal.PRIMARY_EMAIL});
			getFolderModule().setSubscription(binderId, entryId, styles);
		}
		return entryId;
	}

	public long migration_addReplyWithXML(String accessToken, long binderId, long parentId, String definitionId,
			String inputDataAsXML, String creator, Calendar creationDate, String modifier, Calendar modificationDate) {
		Map options = new HashMap();
		options.put(ObjectKeys.INPUT_OPTION_NO_INDEX, Boolean.TRUE);
    	options.put(ObjectKeys.INPUT_OPTION_NO_WORKFLOW, Boolean.TRUE);
		getTimestamps(options, creator, creationDate, modifier, modificationDate);
		return addReply(accessToken, binderId, parentId, definitionId, inputDataAsXML, null, options);
	}

	public void migration_uploadFolderFile(String accessToken, long binderId, long entryId,
			String fileUploadDataItemName, String fileName,
			String modifier, Calendar modificationDate) {
		throw new UnsupportedOperationException();
	}
	
	public void migration_uploadFolderFileStaged(String accessToken, long binderId, long entryId, 
			String fileUploadDataItemName, String fileName, String stagedFileRelativePath, String modifier, Calendar modificationDate){
			
		Map options = new HashMap();
		options.put(ObjectKeys.INPUT_OPTION_NO_INDEX, Boolean.TRUE);
		options.put(ObjectKeys.INPUT_OPTION_NO_WORKFLOW, Boolean.TRUE);
    	options.put(ObjectKeys.INPUT_OPTION_NO_MODIFICATION_DATE, Boolean.TRUE);
		uploadFolderFileStaged(accessToken, binderId, entryId, fileUploadDataItemName, fileName, 
				stagedFileRelativePath, modifier, modificationDate, options);
	}
	public void migration_addEntryWorkflow(String accessToken, long binderId, long entryId, String definitionId, String startState, String modifier, Calendar modificationDate) {
		Map options = new HashMap();
		options.put(ObjectKeys.INPUT_OPTION_NO_INDEX, Boolean.TRUE);
		options.put(ObjectKeys.INPUT_OPTION_FORCE_WORKFLOW_STATE, startState);
		getTimestamps(options, null, null, modifier, modificationDate);
		getFolderModule().addEntryWorkflow(binderId, entryId, definitionId, options);
	}
	public long migration_addBinderWithXML(String accessToken, long parentId, String definitionId,
			String inputDataAsXML, String creator, Calendar creationDate, String modifier, Calendar modificationDate) {
		try {
			Map options = new HashMap();
			//let binder be indexed, so it can be found
			getTimestamps(options, creator, creationDate, modifier, modificationDate);
			Document doc = getDocument(inputDataAsXML);
			DomInputData inputData = new DomInputData(doc, getIcalModule());
			Binder binder = getBinderModule().addBinder(parentId, definitionId, inputData, null, options);
			//sub-folders should inherit
			if (binder instanceof Folder && binder.getParentBinder() instanceof Folder)
				getBinderModule().setDefinitionsInherited(binder.getId(), true);
			return binder.getId().longValue();
		} catch(WriteFilesException e) {
			throw new RemotingException(e);
		} catch (AccessControlException e) {
			throw new RemotingException(e);
		} catch (WriteEntryDataException e) {
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

	public long migration_addBinder(String accessToken, org.kablink.teaming.remoting.ws.model.Binder modelBinder) {
		try {
			Map options = new HashMap();
			//let binder be indexed, so it can be found
			getTimestamps(options, modelBinder);
			Binder binder = getBinderModule().addBinder(modelBinder.getParentBinderId(), 
					modelBinder.getDefinitionId(), new ModelInputData(modelBinder), null, options);
			//sub-folders should inherit
			if (binder instanceof Folder && binder.getParentBinder() instanceof Folder)
				getBinderModule().setDefinitionsInherited(binder.getId(), true);
			return binder.getId().longValue();
		} catch(WriteFilesException e) {
			throw new RemotingException(e);
		} catch (AccessControlException e) {
			throw new RemotingException(e);
		} catch (WriteEntryDataException e) {
			throw new RemotingException(e);
		}
	}

	public long migration_addFolderEntry(String accessToken, FolderEntry entry, boolean subscribe) {
		HashMap options = new HashMap();
		options.put(ObjectKeys.INPUT_OPTION_NO_INDEX, Boolean.TRUE);
    	options.put(ObjectKeys.INPUT_OPTION_NO_WORKFLOW, Boolean.TRUE);
 		getTimestamps(options, entry);
		Long entryId = addFolderEntry(accessToken, entry, null, options);
		if (subscribe) {
			Map<Integer,String[]> styles = new HashMap();
			styles.put(Subscription.MESSAGE_STYLE_NO_ATTACHMENTS_EMAIL_NOTIFICATION, new String[]{Principal.PRIMARY_EMAIL});
			getFolderModule().setSubscription(entry.getParentBinderId(), entryId, styles);
		}
		return entryId;
	}

	public long migration_addReply(String accessToken, long parentEntryId, FolderEntry reply) {
		Map options = new HashMap();
		options.put(ObjectKeys.INPUT_OPTION_NO_INDEX, Boolean.TRUE);
    	options.put(ObjectKeys.INPUT_OPTION_NO_WORKFLOW, Boolean.TRUE);
		getTimestamps(options, reply);
		return addReply(accessToken, parentEntryId, reply, null, options);
	}

}
