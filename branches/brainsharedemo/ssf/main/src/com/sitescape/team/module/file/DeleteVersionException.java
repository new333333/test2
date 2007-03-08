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
