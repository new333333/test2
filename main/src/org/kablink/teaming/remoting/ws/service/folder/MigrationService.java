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

import org.kablink.teaming.remoting.ws.model.Binder;
import org.kablink.teaming.remoting.ws.model.FolderEntry;


public interface MigrationService {

	public long migration_addBinderWithXML(String accessToken, long parentId, String definitionId, String inputDataAsXML,
			String creator, Calendar creationDate, String modifier, Calendar modificationDate);
	
	public long migration_addFolderEntryWithXML(String accessToken, long binderId, String definitionId,
							   String inputDataAsXML, 
							   String creator, Calendar creationDate, String modifier, Calendar modificationDate,
							   boolean subscribe);
		
	public long migration_addReplyWithXML(String accessToken, long binderId, long parentId, String definitionId,
					     String inputDataAsXML, String creator, Calendar creationDate, String modifier, Calendar modificationDate);

	public void migration_uploadFolderFile(String accessToken, long binderId, long entryId, String fileUploadDataItemName,
								 String fileName, String modifier, Calendar modificationDate);
	
	public void migration_uploadFolderFileStaged(String accessToken, long binderId, long entryId, 
			String fileUploadDataItemName, String fileName, String stagedFileRelativePath, String modifier, Calendar modificationDate);
	public void migration_addEntryWorkflow(String accessToken, long binderId, long entryId, String definitionId, String startState, String modifier, Calendar modificationDate);

	public long migration_addBinder(String accessToken, Binder binder);
	
	public long migration_addFolderEntry(String accessToken, FolderEntry entry, boolean subscribe);
		
	public long migration_addReply(String accessToken, long parentEntryId, FolderEntry reply);


	public static class Timestamps implements java.io.Serializable
	{
		String creator;
		Calendar creationDate;
		String modifier;
		Calendar modificationDate;
		
		public Timestamps() {}
		
		public Timestamps(String creator, Calendar creationDate,
						  String modifier, Calendar modificationDate)
		{
			this.creator = creator;
			this.creationDate = creationDate;
			this.modifier = modifier;
			this.modificationDate = modificationDate;
		}
		
		public String getCreator() { return creator; }
		public void setCreator(String creator) { this.creator = creator; }
		public Calendar getCreationDate() { return creationDate; }
		public void setCreationDate(Calendar creationDate) { this.creationDate = creationDate; }
		public String getModifier() { return modifier; }
		public void setModifier(String modifier) { this.modifier = modifier; }
		public Calendar getModificationDate() { return modificationDate; }
		public void setModificationDate(Calendar modificationDate) { this.modificationDate = modificationDate; }
	}
}
