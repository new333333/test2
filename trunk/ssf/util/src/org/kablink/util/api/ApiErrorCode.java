/**
 * Copyright (c) 1998-2015 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2015 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2015 Novell, Inc. All Rights Reserved.
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
package org.kablink.util.api;

/**
 * ?
 * 
 * @author jong
 */
public enum ApiErrorCode {
	// Authentication errors
	USERNAME_NOT_FOUND, // Authentication service cannot locate a user by this username.
	USERACCOUNT_NOT_ACTIVE, // This account has been disabled or deleted.
	BAD_CREDENTIALS, // Bad credentials
	USERACCOUNT_WEBACCESS_BLOCKED,	// This account has been block from using the web access client.
	PASSWORD_EXPIRED,	// This account's password has expired.

	// Authorization errors
	ACCESS_DENIED, // Access denied
	ACCESS_TOKEN_EXPIRED, // SOAP API access token expired
	ACCESS_TOKEN_INVALID, // SOAP API access token invalid
	UNAUTHENTICATED_ACCESS, // Unauthenticated access
	
	// Binder errors
	BINDER_NOT_FOUND, // No binder found with this name or id
	BINDER_SIMPLE_NAME_EXISTS, //A simple name already exists
	
	// Definition errors
	INVALID_DEFINITION, // Invalid definition
	DEFINITION_IN_USE, // Can not delete definition because it is in use
	DEFINITION_EXISTS, // A definition already exists with this name
	DEFINITION_NOT_FOUND, // No definition found with this name or id

	// Document conversion error
	DOC_CONVERSION_ERROR, // Error during document conversion
	
	// File errors
	FILE_ONLY_VERSION, // Can not delete version of file because it is the only remaining version of the file
	FILE_LOCKED, // File is locked by another user
	FILE_FILTER_ERROR, // Failed to filter content of file
	FILE_LOCK_ID_MISMATCH, // Specified file lock id does not match
	FILE_VERSION_CONFLICT, // Specified version number does not reflect the current state of the file
	FILE_WRITE_FAILED, // Writing file failed
	FILE_DELETE_FAILED, // Deleting file failed
	FILE_ARCHIVE_FAILED, // Archiving file failed
	FILE_ENCRYPTION_FAILED, // Encrypting file failed
	FILE_CHECKSUM_FAILED, // Encrypting file failed
	FILE_LOCK_CANCELLATION_FAILED, // File lock cancellation failed
	FILE_EXISTS, // This folder requires that all uploaded files have unique filenames. A file with this name already exists.
	FILE_NOT_FOUND, // No file found with this name or id
	FILE_VERSION_NOT_FOUND, // No file version found with this id
	FILE_PATH_TOO_LONG, // File path too long

	// Folder errors
	FOLDER_NOT_FOUND, // No folder found with this name or id
	INVALID_FOLDER_HIERARCHY, // The hierarchy of the folder is invalid
	
	// Folder entry errors
	ENTRY_RESERVED, // Entry reserved by another user
	ENTRY_NOT_FOUND, // No entry found with this name or id

	// General client errors
	MISSING_MULTIPART_FORM_DATA, // Missing multipart form data
	BAD_MULTIPART_FORM_DATA, // Bad multipart form data
	UNSUPPORTED_MEDIA_TYPE, // Unsupported media type
	INVALID_ENTITY_TYPE, // Entity type is unknown or not supported by this method
	INVALID_HTML, // Input HTML is invalid
	INVALID_CAPTCHA_RESPONSE, // Captcha response invalid
	ILLEGAL_CHARACTER, // Illegal character in the input
	TITLE_EXISTS, // The title already exists
	BAD_INPUT, // Bad input (catch-all category)
	NOT_SUPPORTED, // Not supported (catch-all category)
	ILLEGAL_STATE, // A request has been made at an illegal or inappropriate time 
	
	// General server errors
	SERVER_CONFIG_ERROR, // Server configuration error
	OPTIMISTIC_LOCKING_FAILURE, // Hibernate optimistic locking failure
	IO_ERROR, // I/O error
	SERVER_ERROR, // Server error (catch-all category)
	
	// Indexing/search errors
	LUCENE_ERROR, // Error during search or indexing of Lucene index
	INDEX_MGT_ERROR, // Error during index management operation
	INDEX_NODE_NOT_FOUND, // No index node object found with this name or id
	
	// Mirrored folder/file errors
	MIRRORED_FILE_IN_REGULAR_FOLDER, // Cannot write mirrored file in non-mirrored folder
	REGULAR_FILE_IN_MIRRORED_FOLDER, // Cannot write regular file in mirrored folder
	MIRRORED_MULTIPLE, // An entry cannot mirror more than one file
	MIRRORED_READONLY_DRIVER, // Cannot create/update/delete mirrored file through read-only driver
	MIRRORED_ACCESS_DENIED, // Access to mirrored file/folder denied
	MIRRORED_ERROR, // Error with mirrored folder/file
	MIRRORED_SERVER_DOWN, // The back-end file server is down or unreachable

	// Miscellaneous errors
	LIBRARY_ENTRY_NOT_FOUND, // No library entry object found with this name or id
	BINDER_QUOTA_NOT_FOUND, // No binder quota object found with this name or id
	POSTING_NOT_FOUND, // No posting object found with this name or id
	ACCESSORY_NOT_FOUND, // No dashboard object found with this name or id
	
	// Principal (user/group/application/application group) errors
	PRINCIPAL_NOT_FOUND, // No principal found with this name or id
	USER_EXISTS, // User already exists with this name
	USER_NOT_FOUND, // No user found with this name or id
	PROXY_IDENTITY_EXISTS, // Proxy identity already exists with this title.
	PROXY_IDENTITY_NOT_FOUND, // No proxy identity found with this name or ID.
	NOT_USER, // The entity does not represent a user
	USER_NAME_MISSING, // User name is missing
	GROUP_EXISTS, // Group already exists with this name
	GROUP_NOT_FOUND, // No group found with this name or id
	NOT_GROUP, // The entity does not represent a group
	APPLICATION_EXISTS, // Application already exists with this name
	APPLICATION_NOT_FOUND, // No application found with this name or id
	APPLICATION_GROUP_EXISTS, // Application group already exists with this name

	// Quota errors
	USER_QUOTA_EXCEEDED, // User quota exceeded
	BINDER_QUOTA_EXCEEDED, // Binder quota exceeded
	FILE_SIZE_LIMIT_EXCEEDED, // File size limit exceeded
	
	// Remote application errors
	REMOTE_APP_ERROR, // Error executing remote application

	// Repository service errors
	REPOSITORY_ERROR, // Error during repository operation
	
	// Role management errors
	ROLE_EXISTS, // Role with this name already exists
	ROLE_NOT_FOUND, // No role object found with this name or id
	ROLE_CONDITION_NOT_FOUND, // No role condition object found with this name or id
	
	// Search errors
	SEARCH_INVALID_WILD_CARD, // Invalid use of a wild card in a search request
	
	// Tag errors
	TAG_NOT_FOUND, // No tag object found with this name or id
	
	// WebDAV errors
	WEBDAV_RESOURCE_EXISTS, // A resource with the same name already exists
	
	// Workflow errors
	WORKFLOW_ERROR, // Error during workflow operation
	
	// Workspace errors
	WORKSPACE_NOT_FOUND, // No workspace found with this name or id
	
	// Zone errors
	ZONE_NOT_FOUND, // No zone found with this name or id
	ZONE_MISMATCH, // Found zone does not match expected zone
	ZONE_NAME_MISSING, // Zone name is missing
	ZONE_VIRTUAL_HOST_MISSING, // Zone virtual host is missing
	ZONE_NAME_EXISTS, // Zone with this name already exists
	ZONE_VIRTUAL_HOST_EXISTS, // Zone with this virtual host already exists
	ZONE_ERROR, // Error during zone operation

	// OpenID errors
	OPENID_PROVIDER_NOT_FOUND, // No OpenID provider object found with this name or id

	// OpenID errors
    LDAP_CONFIG_NOT_FOUND, // No ldap config object found with this name or id

    LDAP_SYNC_ERROR,
    LDAP_READ_ERROR,
    HOME_FOLDER_CREATE_ERROR,
    HOME_FOLDER_DELETE_ERROR,
    NET_FOLDER_NOT_FOUND, // No net folder found with this name or id
    NET_FOLDER_SERVER_IN_USE, // No net folder found with this name or id

	// ShareItem errors
	SHAREITEM_NOT_FOUND, // No share found with this name or id
    INVALID_EMAIL_ADDRESS, // Invalid definition

    DEVICE_EXISTS, // A device with the ID already exists
    DEVICE_NOT_FOUND, // No device found with this id

    ACL_CHANGED, // No device found with this id
    CHANGES_PURGED, // No device found with this id
    CHANGES_WINDOW_PASSED, // The date is outside of the supported binder changes windows
    ADHOC_SETTING_CHANGED, // No device found with this id

    NOT_FOUND, // Object/item not found (generic)
    
    NET_FOLDER_CONFIG_NOT_FOUND, // No net folder configuration found with this name or id
    NET_FOLDER_SERVER_NOT_FOUND, // No net folder server found with this name or id
    
    // Anti-virus scanning errors
    VIRUS_DETECTED, // Anti-virus scanning detected infected file
}
