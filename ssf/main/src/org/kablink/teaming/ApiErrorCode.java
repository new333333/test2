/**
 * Copyright (c) 1998-2012 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2012 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2012 Novell, Inc. All Rights Reserved.
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

package org.kablink.teaming;

/**
 * @author jong
 *
 */
public enum ApiErrorCode {

	ACCESS_DENIED, // Access is denied
	SERVER_CONFIG_ERROR, // Server configuration is bad
	INVALID_DEFINITION, // Definition is invalid
	DEFINITION_IN_USE, // Can not delete definition because it is in use
	ONLY_FILE_VERSION, // Can not delete file version because it is the only remaining version of the file
	FILE_LOCKED, // File is locked by another user
	CONTENT_FILTERING_FAILED, // File content filtering failed
	INVALID_FOLDER_HIERARCHY, // The hierarchy of the folder is invalid
	ROLE_EXISTS, // Role already exists
	LOCK_ID_MISMATCH, // The supplied lock token id does not match
	ZONE_MISMATCH, // Found zone does not match expected zone
	USER_EXISTS, // User already exists with this name.
	MISSING_MULTIPART_FORM_DATA, // Missing multipart form data
	BAD_MULTIPART_FORM_DATA, // Bad multipart form data
	NOT_USER, // The entity does not represent a user
	NOT_GROUP, // The entity does not represent a group
	INVALID_ENTITY_TYPE, // Entity type is unknown or not supported by this method
	FILE_VERSION_CONFLICT, // Specified version number does not reflect the current state of the file
	UNSUPPORTED_OPERATION, // Unsupported operation
	OPTIMISTIC_LOCKING_FAILURE, // Hibernate optimistic locking failed
	ILLEGAL_ARGUMENT, // Illegal argument
	GENERAL_ERROR, // General error occurred
	INVALID_HTML, // The input HTML is invalid
	FILE_FILTERING_ERROR,
	FILE_WRITE_FAILED,
	FILE_DELETE_FAILED,
	FILE_LOCK_CANCELLATION_FAILED,
	FILE_EXISTS, // This folder requires that all uploaded files have unique filenames. A file with this name already exists.
	FILE_ARCHIVE_FAILED,
	MIRRORED_FILE_IN_REGULAR_FOLDER,
	MIRRORED_FILE_MULTIPLE,
	REGULAR_FILE_IN_MIRRORED_FOLDER,
	MIRRORED_FILE_READONLY_DRIVER,
	FILE_ENCRYPTION_FAILED,
	FILE_NOT_FOUND,
	UNSUPPORTED_MEDIA_TYPE,
	IO_ERROR,
	UNAUTHENTICATED,
	NAME_EXISTS,
	TEXT_VERIFICATION_FAILED,
	INVALID_DATA,
	ENTITY_RESERVED,
	PASSWORD_MISMATCH,
	NOT_SUPPORTED,
	NOT_FOUND,
	ILLEGAL_CHARACTER,
	LUCENE_INDEX_ERROR,
	USER_QUOTA_EXCEEDED,
	BINDER_QUOTA_EXCEEDED,
	FILE_SIZE_LIMIT_EXCEEDED,
	FILE_VERSION_NOT_FOUND,
	USER_NAME_MISSING,
	ZONE_NAME_MISSING,
	ZONE_VIRTUAL_HOST_MISSING,
	DEFINITION_EXISTS,
	APPLICATION_EXISTS,
	APPLICATION_GROUP_EXISTS,
	GROUP_EXISTS,
	ZONE_NAME_EXISTS,
	ZONE_VIRTUAL_HOST_EXISTS,
	MIRRORED_FOLDER_ERROR,
}
