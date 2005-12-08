package com.sitescape.ef.util;

public interface Constants {

	// Commands used in Action classes

	public static final String CMD = "cmd";

	public static final String ADD = "add";

	public static final String APPROVE = "approve";

	public static final String CANCEL = "cancel";

	public static final String DELETE = "delete";

	public static final String EDIT = "edit";

	public static final String REJECT = "reject";

	public static final String SAVE = "save";

	public static final String SEARCH = "search";

	public static final String SEND = "send";

	public static final String UPDATE = "update";

	public static final String VIEW = "view";

	// Rreturn values used in Action classes

	public static final String COMMON_ERROR = "/common/error.jsp";

	public static final String COMMON_FORWARD = "/common/forward_js.jsp";

	public static final String COMMON_FORWARD_JSP = "/common/forward_jsp.jsp";

	public static final String COMMON_NULL = "/common/null.jsp";

	public static final String COMMON_REFERER = "/common/referer_js.jsp";

	public static final String COMMON_REFERER_JSP = "/common/referer_jsp.jsp";

	public static final String PORTAL_ERROR = "/portal/error";

	// Content types

	public static final String TEXT_HTML = "text/html";

	public static final String TEXT_PLAIN = "text/plain";

	public static final String TEXT_WML = "text/wml";

	public static final String TEXT_XML = "text/xml";

	// Content directories

	public static final String TEXT_HTML_DIR = "/html";

	public static final String TEXT_WML_DIR = "/wml";

	// Data source

	public static final String DATA_SOURCE = "jdbc/SiteScapePool";

	// JAAS

	public static final String REALM_NAME = "PortalRealm";

	public static final String JBOSS_LOGIN_MODULE = "client-login";

}
