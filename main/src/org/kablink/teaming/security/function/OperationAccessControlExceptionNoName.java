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
package org.kablink.teaming.security.function;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kablink.teaming.domain.Folder;
import org.kablink.teaming.domain.FolderEntry;
import org.kablink.teaming.security.AccessControlException;

public class OperationAccessControlExceptionNoName extends AccessControlException {

	private static final long serialVersionUID = 1L;

	private static final Log logger = LogFactory.getLog(OperationAccessControlExceptionNoName.class);
    
	private static final String OperationAccessControlException_ErrorCode = "errorcode.operation.denied.noName";
	
	public static OperationAccessControlExceptionNoName newInstance(String username, String operationName, WorkArea workArea) {
		OperationAccessControlExceptionNoName exc = new OperationAccessControlExceptionNoName(username, operationName);
		if(logger.isDebugEnabled()) {
			if(workArea instanceof FolderEntry) {
				FolderEntry folderEntry = (FolderEntry)workArea;
				Folder folder = folderEntry.getParentFolder();
				logger.debug("User [" + username + "] is not authorized to perform [" + operationName + "] on work area [" + workArea.getWorkAreaType() + ":" + workArea.getWorkAreaId() + 
						"] parent folder [" + ((folder==null)? "null":folder.getId()) + "]", exc);
			}
			else if (workArea instanceof Folder) {
				Folder folder = (Folder)workArea;
				Folder parentFolder = folder.getParentFolder();
				logger.debug("User [" + username + "] is not authorized to perform [" + operationName + "] on work area [" + workArea.getWorkAreaType() + ":" + workArea.getWorkAreaId() + 
						"] parent folder [" + ((parentFolder==null)? "null":parentFolder.getId()) + "]", exc);
			}
			else if(logger.isTraceEnabled()) {
				logger.trace("User [" + username + "] is not authorized to perform [" + operationName + "] on work area [" + workArea.getWorkAreaType() + ":" + workArea.getWorkAreaId() + "]", exc);
			}
		}
		return exc;
	}
	
	private OperationAccessControlExceptionNoName(String username, String operationName) {
		super(OperationAccessControlException_ErrorCode, new Object[] {username, operationName});
	}
}
