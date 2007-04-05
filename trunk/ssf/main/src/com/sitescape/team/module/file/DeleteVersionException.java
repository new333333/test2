/**
 * The contents of this file are governed by the terms of your license
 * with SiteScape, Inc., which includes disclaimers of warranties and
 * limitations on liability. You may not use this file except in accordance
 * with the terms of that license. See the license for the specific language
 * governing your rights and limitations under the license.
 *
 * Copyright (c) 2007 SiteScape, Inc.
 *
 */
package com.sitescape.team.module.file;

import com.sitescape.team.domain.VersionAttachment;
import com.sitescape.team.exception.UncheckedCodedException;

/**
 * Thrown to indicate that the request to delete a version is failed
 * because it is the only remaining version for the file.  
 * 
 * @author jong
 *
 */
public class DeleteVersionException extends UncheckedCodedException {

	private static final String DeleteVersionException_ErrorCode = "errorcode.delete.version";
	 
	public DeleteVersionException(VersionAttachment va) {
        super(DeleteVersionException_ErrorCode, new Object[]{va.getVersionNumber(), va.getFileItem().getName()});
    }
}
