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
package org.kablink.teaming.fi;

import org.kablink.teaming.exception.UncheckedCodedException;
import org.kablink.util.api.ApiErrorCode;

public class FIException extends UncheckedCodedException {

	private static final long serialVersionUID = 1L;
	
	public static final String CANNOT_FIND_RESOURCE_DRIVER_BY_NAME = "fi.error.cannot.find.resource.driver.by.name";
	public static final String CANNOT_FIND_STATIC_RESOURCE_DRIVER_BY_NAME_HASH = "fi.error.cannot.find.static.resource.driver.by.name.hash";
	public static final String CANNOT_FIND_RESOURCE_DRIVER_BY_ID = "fi.error.cannot.find.resource.driver.by.id";
	public static final String FAILED_TO_DELETE = "fi.error.failed.to.delete";
	public static final String FAILED_TO_MOVE = "fi.error.failed.to.move";
	public static final String FAILED_TO_WRITE = "fi.error.failed.to.write";
	public static final String PARENT_BINDER_DOES_NOT_EXIST = "fi.error.parent.binder.doesnot.exist";
	public static final String PARENT_BINDER_IS_NOT_BINDER="fi.error.parent.binder.isnot.binder";
	public static final String PARENT_BINDER_IS_NOT_MIRRORED_FOLDER="fi.error.parent.binder.isnot.mirrored.folder";
	public static final String FILE_DOES_NOT_EXIST="fi.error.file.doesnot.exist";
	public static final String BINDER_DOES_NOT_EXIST="fi.error.binder.doesnot.exist";
	public static final String BINDER_IS_NOT_MIRRORED_FOLDER="fi.error.binder.isnot.mirrored.folder";
	public static final String FAILED_TO_SYNCHRONIZE_NOT_DIRECTORY="fi.error.failed.to.synchronize.not.directory";
	public static final String CANNOT_ADD_FOLDER_ANCESTER="fi.error.cannot.add.folder.ancester";
	public static final String CANNOT_MAP_TO_EXTERNAL_PRINCIPAL_ID="fi.error.cannot.map.to.external.principal.id";
	public static final String CREDENTIAL_UNAVAILABLE_FOR_USER="fi.error.credential.unavailable.for.user";
	
    public FIException(String errorCode, Object arg) {
    	super(errorCode, new Object[]{arg});
    }

    public FIException(String errorCode, Object[] args) {
    	super(errorCode, args);
    }
    
    public int getHttpStatusCode() {
    	return 500; // internal server error
    }


	/* (non-Javadoc)
	 * @see org.kablink.teaming.exception.ApiErrorCodeSupport#getApiErrorCode()
	 */
	@Override
	public ApiErrorCode getApiErrorCode() {
		return ApiErrorCode.MIRRORED_ERROR;
	}
}
