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
package org.kablink.teaming.ssfs;

/**
 * IMPORTANT: Do NOT make this class dependent upon any other class in the
 * system. In other word, do NOT import any class other than java or
 * javax classes.
 * 
 * @author jong
 *
 */
public abstract class CrossContextConstants {
	
	// The key names can be something concise because there is no 
	// possibility for name collision. We do not share the same
	// request object with 3rd party web app.
	
	// Operation key name
	public static final String OPERATION = "operation";
	
	// Operation values - value of OPERATION key
	// Note: For better efficiency in comparison expressions, we use Integer
	// rather than usual String as the values of these symbols. 
	public static final Integer OPERATION_AUTHENTICATE = new Integer(1);
	public static final Integer OPERATION_CREATE_FOLDER = new Integer(2);
	public static final Integer OPERATION_CREATE_RESOURCE = new Integer(3);
	public static final Integer OPERATION_SET_RESOURCE = new Integer(4);
	public static final Integer OPERATION_GET_RESOURCE = new Integer(5);
	public static final Integer OPERATION_REMOVE_OBJECT = new Integer(6);
	public static final Integer OPERATION_GET_CHILDREN_NAMES = new Integer(7);
	public static final Integer OPERATION_GET_PROPERTIES = new Integer(8);
	public static final Integer OPERATION_CREATE_SET_RESOURCE = new Integer(9);
	public static final Integer OPERATION_LOCK_RESOURCE = new Integer(10);
	public static final Integer OPERATION_UNLOCK_RESOURCE = new Integer(11);
	public static final Integer OPERATION_COPY_OBJECT = new Integer(12);
	public static final Integer OPERATION_MOVE_OBJECT = new Integer(13);
	/*
	public static final String OPERATION_AUTHENTICATE = "authenticate";
	public static final String OPERATION_OBJECT_EXISTS = "objectExists";
	public static final String OPERATION_CREATE_RESOURCE = "createResource";
	public static final String OPERATION_SET_RESOURCE = "setResource";
	public static final String OPERATION_GET_RESOURCE = "getResource";
	public static final String OPERATION_GET_RESOURCE_LENGTH = "getResourceLength";
	public static final String OPERATION_REMOVE_RESOURCE = "removeResource";
	public static final String OPERATION_GET_LAST_MODIFIED = "getLastModified";
	public static final String OPERATION_GET_CREATION_DATE = "getCreationDate";
	public static final String OPERATION_GET_CHILDREN_NAMES = "getChildrenNames";
	*/

	// User credential or authentication related key names.
	public static final String USER_NAME = "username";
	public static final String USER_ID = "userid";
	public static final String PASSWORD = "password";
	public static final String SERVER_NAME = "serverName";
	public static final String IGNORE_PASSWORD = "ignorePassword";
	
	// Argument key for OPERATION_SET_RESOURCE operation.
	public static final String INPUT_STREAM = "inputStream";
	
	// Argument keys for OPERATION_COPY_OBJECT and OPERATION_MOVE_OBJECT
	public static final String OVERWRITE = "overwrite";
	public static final String RECURSIVE = "recursive";
	
	// Argument key for URI (whose value is a map). - This argument is used for
	// all operations that take single uri. 
	public static final String URI = "uri";
	
	// Argument keys for URIs - Used for operations that take two URIs.
	public static final String SOURCE_URI = "suri";
	public static final String TARGET_URI = "turi";
	
	// Key names for URI parts - These are keys for the entries in the URI map.
	
	// The following keys are used for both internal and library URLs.
	public static final String URI_ORIGINAL = "original"; // value = String
	public static final String URI_TYPE = "type";	// value = String
	public static final String URI_ZONENAME = "zonename"; // value = String
	
	// The following keys are used for internal URLs only.
	public static final String URI_BINDER_ID = "binderId";	// value = Long
	public static final String URI_FILEPATH = "filepath";	// value = String
	public static final String URI_ENTRY_ID = "entryId";	// value = Long
	public static final String URI_ITEM_TYPE = "itemType";	// value = String
	public static final String URI_ELEMNAME = "elemname";	// value = String
	public static final String URI_REPOS_NAME = "reposname"; // value = String
	 
	// The following keys are used for library URLs only.
	public static final String URI_LIBPATH = "libpath"; 	// value = String
	
	// URI type values (values for URI_TYPE key) - Do NOT abbreviate the 
	// string value since it is actually used as part of the WebDAV URI. 
	public static final String URI_TYPE_INTERNAL = "internal";;
	public static final String URI_TYPE_LIBRARY = "library";
	
	// URI item type values (values for URI_ITEM_TYPE) - Do NOT abbreviate 
	// or alter the string value since it is actually used as part of the 
	// WebDAV URI.
	public static final String URI_ITEM_TYPE_LIBRARY = "library";
	public static final String URI_ITEM_TYPE_FILE	 = "file";
	public static final String URI_ITEM_TYPE_GRAPHIC = "graphic";
	public static final String URI_ITEM_TYPE_ATTACH  = "attach";

	// Basic information about the object that tells whether the object
	// exists or not. If it exists whether it is a folder or a file.
	
	// Key name used for returning quick object info
	public static final String OBJECT_INFO = "objInfo";
	
	// Possible object info values - value of OBJECT_INFO key
	
	// The object exists and it is a folder
	public static final String OBJECT_INFO_DIRECTORY 			= "di";
	// The object exists and it is a file (non-folder)
	public static final String OBJECT_INFO_FILE					= "fi";
	// The object does not exist
	public static final String OBJECT_INFO_NON_EXISTING 		= "ne";
	// The object refers to virtual help file
	public static final String OBJECT_INFO_VIRTUAL_HELP_FILE 	= "vh";
	
	// Key name used for return value.
	public static final String RETURN = "return"; // value = Object
	
	// Key name used for returning error status code.
	public static final String ERROR = "error";
	
	// Error status codes - for ERROR key for WebDAV authentication
	public static final String ERROR_AUTHENTICATION_FAILURE = "authFailure";
	
	// Error status codes - for ERROR key for WebDAV operations 
	public static final String ERROR_NO_ACCESS = "noAccess";
	public static final String ERROR_NO_SUCH_OBJECT = "noSuchObject";
	public static final String ERROR_ALREADY_EXISTS = "alreadyExists"; 
	public static final String ERROR_LOCK = "lock";
	public static final String ERROR_TYPE_MISMATCH = "typeMismatch";
	public static final String ERROR_GENERAL = "generalError";
	public static final String WARNING_GENERAL = "generalWarning";
	
	// Key name used for returning error message. 
	public static final String ERROR_MESSAGE = "errorMessage";
	
	// DAV Properties
	public static final String DAV_PROPERTIES_NAMESPACE = "DAV:";
	public static final String DAV_PROPERTIES_CREATION_DATE = "DAV::creationdate";
	public static final String DAV_PROPERTIES_GET_CONTENT_LENGTH = "DAV::getcontentlength";
	public static final String DAV_PROPERTIES_GET_CONTENT_TYPE = "DAV::getcontenttype";
	public static final String DAV_PROPERTIES_GET_LAST_MODIFIED = "DAV::getlastmodified";
	
	// LOCK Properties
	public static final String LOCK_PROPERTIES_ID = "lockId"; 
	public static final String LOCK_PROPERTIES_SUBJECT = "lockSubject";
	public static final String LOCK_PROPERTIES_EXPIRATION_DATE = "lockExpirationDate";
	public static final String LOCK_PROPERTIES_OWNER_INFO = "lockOwner";
}
