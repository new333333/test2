package com.sitescape.ef.web.crosscontext.ssfs;

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

	// User credential or authentication related key names.
	public static final String USER_NAME = "username";
	public static final String PASSWORD = "password";
	public static final String ZONE_NAME = "zonename";
	
	// Argument key for OPERATION_SET_RESOURCE operation.
	public static final String ARG_INPUTSTREAM = "inputStream";
	
	// Argument key for URI (whose value is a map). - This argument is used for
	// all operations.
	public static final String URI = "uri";
	
	// Key names for URI parts - These are keys for the entries in the URI map.
	public static final String URI_ZONENAME = "zonename"; // value = String
	public static final String URI_TYPE = "type";	// value = String
	public static final String URI_BINDER_ID = "binderId";	// value = Long
	public static final String URI_FILENAME = "filename";	// value = String
	public static final String URI_ENTITY_TYPED_ID = "entityTypedId";	// value = String
	public static final String URI_ITEM_TYPE = "itemType";	// value = String
	public static final String URI_ELEMNAME = "elemname";	// value = String
	 
	public static final String URI_IS_FOLDER = "isFolder"; // value = Boolean
	
	// URI type values (values for URI_TYPE key) - Do NOT abbreviate the 
	// string value since it is actually used as part of the WebDAV URI. 
	public static final String URI_TYPE_INTERNAL = "internal";;
	public static final String URI_TYPE_LIBRARY = "library";
	
	// URI item type values (values for URI_ITEM_TYPE) - Do NOT abbreviate 
	// the string value since it is actually used as part of the WebDAV URI.
	public static final String URI_ITEM_TYPE_PRIMARY = "primary";
	public static final String URI_ITEM_TYPE_FILE	 = "file";
	public static final String URI_ITEM_TYPE_GRAPHIC = "graphic";
	public static final String URI_ITEM_TYPE_ATTACH  = "attach";

	// Key name used for return value.
	public static final String RETURN = "return"; // value = Object
	
	// Key name used for returning error information.
	public static final String ERROR = "error";
	
	// Error values - possible values for ERROR key
	public static final String ERROR_NO_ACCESS = "noAccess";
	public static final String ERROR_NO_SUCH_OBJECT = "noSuchObject";
	public static final String ERROR_ALREADY_EXISTS = "alreadyExists"; 
}
