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
	
	// Operation values
	public static final String OPERATION_AUTHENTICATE = "authenticate";

	// User credential or authentication related key names. 
	public static final String USER_NAME = "username";
	public static final String PASSWORD = "password";
	public static final String ZONE_NAME = "zonename";

	// Key names for URI parts
	public static final String URI_ZONENAME = "uri.zonename";
	public static final String URI_TYPE = "uri.type";
	public static final String URI_BINDER_ID = "uri.binderId";
	public static final String URI_FILENAME = "uri.filename";
	public static final String URI_ENTITY_TYPED_ID = "uri.entityTypedId";
	public static final String URI_ITEM_TYPE = "uri.itemType";
	public static final String URI_ELEMNAME = "uri.elemname";
	
	// The following key can have either Boolean.TRUE or Boolean.FALSE
	// as its value. 
	public static final String URI_IS_FOLDER = "uri.isFolder"; 
	
	// URI type values
	public static final String URI_TYPE_INTERNAL = "internal";;
	public static final String URI_TYPE_LIBRARY = "library";
	
	// URI item type values
	public static final String URI_ITEM_TYPE_PRIMARY = "primary";
	public static final String URI_ITEM_TYPE_FILE	 = "file";
	public static final String URI_ITEM_TYPE_GRAPHIC = "graphic";
	public static final String URI_ITEM_TYPE_ATTACH  = "attach";

	// Key name used for returning status information.
	public static final String STATUS = "status";
	
	// Status values
	public static final String STATUS_NO_ACCESS = "noAccess";
	public static final String STATUS_NO_SUCH_OBJECT = "noSuchObject";
	public static final String STATUS_ALREADY_EXISTS = "alreadyExists"; 
}
