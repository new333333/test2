/**
 * Copyright (c) 1998-2013 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2013 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2013 Novell, Inc. All Rights Reserved.
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
package org.kablink.teaming.gwt.server.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.kablink.teaming.ApplicationExistsException;
import org.kablink.teaming.ApplicationGroupExistsException;
import org.kablink.teaming.GroupExistsException;
import org.kablink.teaming.PasswordMismatchException;
import org.kablink.teaming.UserExistsException;
import org.kablink.teaming.domain.NoBinderByTheIdException;
import org.kablink.teaming.domain.NoFolderEntryByTheIdException;
import org.kablink.teaming.domain.NoUserByTheIdException;
import org.kablink.teaming.gwt.client.GwtTeamingException;
import org.kablink.teaming.gwt.client.GwtTeamingException.ExceptionType;
import org.kablink.teaming.gwt.client.admin.ExtensionDefinitionInUseException;
import org.kablink.teaming.module.resourcedriver.RDException;
import org.kablink.teaming.security.AccessControlException;
import org.kablink.teaming.security.function.OperationAccessControlExceptionNoName;
import org.kablink.teaming.util.SPropsUtil;
import org.kablink.teaming.web.util.FavoritesLimitExceededException;
import org.kablink.teaming.web.util.MiscUtil;

/**
 * Helper methods for logging information (exceptions, messages, ...)
 * from the GWT code.
 *
 * @author drfoster@novell.com
 */
public class GwtLogHelper {
	// The logger to used if we aren't given one by the caller.
	private static Log m_logger = LogFactory.getLog(GwtLogHelper.class);

	// The following control whether access control exception messages
	// are logged for the various levels of output logging.
	//
	// The only cases we want to write information to the log for ACL
	// violations is for debug and fatal level messages.f
	private final static boolean DEBUG_LOG_ACL_EXCEPTIONS	= true;
	private final static boolean ERROR_LOG_ACL_EXCEPTIONS	= false;
	private final static boolean FATAL_LOG_ACL_EXCEPTIONS	= true;
	private final static boolean INFO_LOG_ACL_EXCEPTIONS	= false;
	private final static boolean TRACE_LOG_ACL_EXCEPTIONS	= false;
	private final static boolean WARN_LOG_ACL_EXCEPTIONS	= false;

	// Enumeration type that maps to the various logging levels
	// available via a Log object.
	private enum LogLevel {
		DEBUG,
		ERROR,
		FATAL,
		INFO,
		TRACE,
		WARN;

		/**
		 * Maps a log level name to its equivalent enumeration
		 * 
		 * @param logLevel
		 * @param logLevelDefault
		 * 
		 * @return
		 */
		public static LogLevel getEnum(String logLevel, LogLevel logLevelDefault) {
			for (LogLevel ll:  LogLevel.values()) {
				if (logLevel.equalsIgnoreCase(ll.name())) {
					return ll;
				}
			}
			return logLevelDefault;
		}
	}
	
	// The following are used to generate the ssf*.properties key used
	// to determine the proper client exception logging level.
	private final static String LOG_LEVEL_KEY_BASE			= "gwt.client.exception.log.level.";
	private final static String LOG_LEVEL_SYNTHESIZED_TAIL	= "synthesized";
	private final static String LOG_LEVEL_WRAPPED_TAIL		= "wrapped";
	
	/*
	 * Class constructor that prevents this class from being
	 * instantiated.
	 */
	private GwtLogHelper() {
		// Nothing to do.
	}
	
	/**
	 * Logs a debug message.
	 * 
	 * @param logger
	 * @param message
	 * @param t
	 */
	public static void debug(Log logger, Object message, Throwable t) {
		if (shouldLogMessage(DEBUG_LOG_ACL_EXCEPTIONS, t)) {
			if (null == logger) logger = m_logger;
			if (null == t)
			     logger.debug(message   );
			else logger.debug(message, t);
		}
	}
	
	/*
	 * Variations of this method.
	 */
	public static void debug(            Object message, Throwable t) {debug(null,   message, t   );}
	public static void debug(Log logger, Object message)              {debug(logger, message, null);}
	public static void debug(            Object message)              {debug(null,   message, null);}
	
	/**
	 * Returns true if debug logging is enabled on the given logger.
	 * 
	 * @param logger
	 * 
	 * @return
	 */
	public static boolean isDebugEnabled(Log logger) {
		return ((null == logger) ? m_logger.isDebugEnabled() : logger.isDebugEnabled());
	}
	
	public static boolean isDebugEnabled() {
		// Always use the initial form of the method.
		return isDebugEnabled(null);
	}
	
	/**
	 * Logs an error message.
	 * 
	 * @param logger
	 * @param message
	 * @param t
	 */
	public static void error(Log logger, Object message, Throwable t) {
		if (shouldLogMessage(ERROR_LOG_ACL_EXCEPTIONS, t)) {
			if (null == logger) logger = m_logger;
			if (null == t)
			     logger.error(message   );
			else logger.error(message, t);
		}
	}
	
	/*
	 * Variations of this method.
	 */
	public static void error(            Object message, Throwable t) {error(null,   message, t   );}
	public static void error(Log logger, Object message)              {error(logger, message, null);}
	public static void error(            Object message)              {error(null,   message, null);}
	
	/**
	 * Returns true if error logging is enabled on the given logger.
	 * 
	 * @param logger
	 * 
	 * @return
	 */
	public static boolean isErrorEnabled(Log logger) {
		return ((null == logger) ? m_logger.isErrorEnabled() : logger.isErrorEnabled());
	}
	
	public static boolean isErrorEnabled() {
		// Always use the initial form of the method.
		return isErrorEnabled(null);
	}
	
	/**
	 * Logs a fatal message.
	 * 
	 * @param logger
	 * @param message
	 * @param t
	 */
	public static void fatal(Log logger, Object message, Throwable t) {
		if (shouldLogMessage(FATAL_LOG_ACL_EXCEPTIONS, t)) {
			if (null == logger) logger = m_logger;
			if (null == t)
			     logger.fatal(message   );
			else logger.fatal(message, t);
		}
	}
	
	/*
	 * Variations of this method.
	 */
	public static void fatal(            Object message, Throwable t) {fatal(null,   message, t   );}
	public static void fatal(Log logger, Object message)              {fatal(logger, message, null);}
	public static void fatal(            Object message)              {fatal(null,   message, null);}
	
	/**
	 * Returns true if fatal logging is enabled on the given logger.
	 * 
	 * @param logger
	 * 
	 * @return
	 */
	public static boolean isFatalEnabled(Log logger) {
		return ((null == logger) ? m_logger.isFatalEnabled() : logger.isFatalEnabled());
	}
	
	public static boolean isFatalEnabled() {
		// Always use the initial form of the method.
		return isFatalEnabled(null);
	}
	
	/**
	 * Logs an informational message.
	 * 
	 * @param logger
	 * @param message
	 * @param t
	 */
	public static void info(Log logger, Object message, Throwable t) {
		if (shouldLogMessage(INFO_LOG_ACL_EXCEPTIONS, t)) {
			if (null == logger) logger = m_logger;
			if (null == t)
			     logger.info(message   );
			else logger.info(message, t);
		}
	}
	
	/*
	 * Variations of this method.
	 */
	public static void info(            Object message, Throwable t) {info(null,   message, t   );}
	public static void info(Log logger, Object message)              {info(logger, message, null);}
	public static void info(            Object message)              {info(null,   message, null);}
	
	/**
	 * Returns true if info logging is enabled on the given logger.
	 * 
	 * @param logger
	 * 
	 * @return
	 */
	public static boolean isInfoEnabled(Log logger) {
		return ((null == logger) ? m_logger.isInfoEnabled() : logger.isInfoEnabled());
	}
	
	public static boolean isInfoEnabled() {
		// Always use the initial form of the method.
		return isInfoEnabled(null);
	}
	
	/**
	 * Logs a trace message.
	 * 
	 * @param logger
	 * @param message
	 * @param t
	 */
	public static void trace(Log logger, Object message, Throwable t) {
		if (shouldLogMessage(TRACE_LOG_ACL_EXCEPTIONS, t)) {
			if (null == logger) logger = m_logger;
			if (null == t)
			     logger.trace(message   );
			else logger.trace(message, t);
		}
	}
	
	/*
	 * Variations of this method.
	 */
	public static void trace(            Object message, Throwable t) {trace(null,   message, t   );}
	public static void trace(Log logger, Object message)              {trace(logger, message, null);}
	public static void trace(            Object message)              {trace(null,   message, null);}
	
	/**
	 * Returns true if trace logging is enabled on the given logger.
	 * 
	 * @param logger
	 * 
	 * @return
	 */
	public static boolean isTraceEnabled(Log logger) {
		return ((null == logger) ? m_logger.isTraceEnabled() : logger.isTraceEnabled());
	}
	
	public static boolean isTraceEnabled() {
		// Always use the initial form of the method.
		return isTraceEnabled(null);
	}
	
	/**
	 * Logs a warning message.
	 * 
	 * @param logger
	 * @param message
	 * @param t
	 */
	public static void warn(Log logger, Object message, Throwable t) {
		if (shouldLogMessage(WARN_LOG_ACL_EXCEPTIONS, t)) {
			if (null == logger) logger = m_logger;
			if (null == t)
			     logger.warn(message   );
			else logger.warn(message, t);
		}
	}
	
	/*
	 * Variations of this method.
	 */
	public static void warn(            Object message, Throwable t) {warn(null,   message, t   );}
	public static void warn(Log logger, Object message)              {warn(logger, message, null);}
	public static void warn(            Object message)              {warn(null,   message, null);}
	
	/**
	 * Returns true if warn logging is enabled on the given logger.
	 * 
	 * @param logger
	 * 
	 * @return
	 */
	public static boolean isWarnEnabled(Log logger) {
		return ((null == logger) ? m_logger.isWarnEnabled() : logger.isWarnEnabled());
	}
	
	public static boolean isWarnEnabled() {
		// Always use the initial form of the method.
		return isWarnEnabled(null);
	}
	
	/**
	 * Returns a GwtTeamingException from a generic Exception that can
	 * be serialized to the client.  The message and/or exception is
	 * logged, as appropriate.
	 *
	 * @param logger
	 * @param ex
	 * @param clientLogMmg
	 * 
	 * @return
	 */
	public static GwtTeamingException getGwtClientException(Log logger, Exception ex, String clientLogMsg) {
		// If we were given a GwtTeamingException...
		GwtTeamingException reply;
		if ((null != ex) && (ex instanceof GwtTeamingException)) {
			// ...simply return it.
			reply = ((GwtTeamingException) ex);
		}
		
		else {
			// Otherwise, construct an appropriate GwtTeamingException.
			reply = new GwtTeamingException();
			if (null != ex) {
				ExceptionType exType;
				
				if      (ex instanceof AccessControlException               ) exType = ExceptionType.ACCESS_CONTROL_EXCEPTION;
				else if (ex instanceof ApplicationExistsException           ) exType = ExceptionType.APPLICATION_EXISTS_EXCEPTION;
				else if (ex instanceof ApplicationGroupExistsException      ) exType = ExceptionType.APPLICATION_GROUP_EXISTS_EXCEPTION;
				else if (ex instanceof ExtensionDefinitionInUseException    ) exType = ExceptionType.EXTENSION_DEFINITION_IN_USE;
				else if (ex instanceof FavoritesLimitExceededException      ) exType = ExceptionType.FAVORITES_LIMIT_EXCEEDED;
				else if (ex instanceof NoBinderByTheIdException             ) exType = ExceptionType.NO_BINDER_BY_THE_ID_EXCEPTION;
				else if (ex instanceof NoFolderEntryByTheIdException        ) exType = ExceptionType.NO_FOLDER_ENTRY_BY_THE_ID_EXCEPTION;
				else if (ex instanceof NoUserByTheIdException               ) exType = ExceptionType.NO_BINDER_BY_THE_ID_EXCEPTION;
				else if (ex instanceof OperationAccessControlExceptionNoName) exType = ExceptionType.ACCESS_CONTROL_EXCEPTION;
				else if (ex instanceof GroupExistsException                 ) exType = ExceptionType.GROUP_ALREADY_EXISTS;
				else if (ex instanceof RDException                          ) exType = ExceptionType.NET_FOLDER_ROOT_ALREADY_EXISTS;
				else if (ex instanceof PasswordMismatchException            ) exType = ExceptionType.CHANGE_PASSWORD_EXCEPTION;
				else if (ex instanceof UserExistsException                  ) exType = ExceptionType.USER_ALREADY_EXISTS;
				else                                                          exType = ExceptionType.UNKNOWN;
				
				reply.setExceptionType(exType);
				reply.setWrapped(      true  );
			}
		}

		// What message are we supposed to log for the exception?
		if (!(MiscUtil.hasString(clientLogMsg))) {
			if (null != ex)
			     clientLogMsg = "GwtLogHelper.getGwtClientException( SOURCE EXCEPTION ):  ";
			else clientLogMsg = "GwtLogHelper.getGwtClientException( GWT EXCEPTION ):  ";
		}

		// If either the logger we were given or GwtLogHelper's logger
		// are enabled at the requested level, we'll log the message. 
		Log traceLogger;
		switch (getClientExceptionLogLevel(reply.isWrapped())) {
		default:
		case ERROR:
			// If logging is enabled...
			if (isErrorEnabled(logger))
			     traceLogger = logger;
			else traceLogger = m_logger;
			if (isErrorEnabled(traceLogger)) {
				// ...log the exception that got us here.
				if (null != ex)
				     error(traceLogger, clientLogMsg, ex   );
				else error(traceLogger, clientLogMsg, reply);
			}
			
			break;
			
		case DEBUG:
			// If logging is enabled...
			if (isDebugEnabled(logger))
			     traceLogger = logger;
			else traceLogger = m_logger;
			if (isDebugEnabled(traceLogger)) {
				// ...log the exception that got us here.
				if (null != ex)
				     debug(traceLogger, clientLogMsg, ex   );
				else debug(traceLogger, clientLogMsg, reply);
			}
			
			break;
			
		case FATAL:
			// If logging is enabled...
			if (isFatalEnabled(logger))
			     traceLogger = logger;
			else traceLogger = m_logger;
			if (isFatalEnabled(traceLogger)) {
				// ...log the exception that got us here.
				if (null != ex)
				     fatal(traceLogger, clientLogMsg, ex   );
				else fatal(traceLogger, clientLogMsg, reply);
			}
			
			break;
			
		case INFO:
			// If logging is enabled...
			if (isInfoEnabled(logger))
			     traceLogger = logger;
			else traceLogger = m_logger;
			if (isInfoEnabled(traceLogger)) {
				// ...log the exception that got us here.
				if (null != ex)
				     info(traceLogger, clientLogMsg, ex   );
				else info(traceLogger, clientLogMsg, reply);
			}
			
			break;
			
		case TRACE:
			// If logging is enabled...
			if (isTraceEnabled(logger))
			     traceLogger = logger;
			else traceLogger = m_logger;
			if (isTraceEnabled(traceLogger)) {
				// ...log the exception that got us here.
				if (null != ex)
				     trace(traceLogger, clientLogMsg, ex   );
				else trace(traceLogger, clientLogMsg, reply);
			}
			
			break;
			
		case WARN:
			// If logging is enabled...
			if (isWarnEnabled(logger))
			     traceLogger = logger;
			else traceLogger = m_logger;
			if (isWarnEnabled(traceLogger)) {
				// ...log the exception that got us here.
				if (null != ex)
				     warn(traceLogger, clientLogMsg, ex   );
				else warn(traceLogger, clientLogMsg, reply);
			}
			
			break;
		}

		// If we get here, reply refers to the GwtTeamingException that
		// was requested.  Return it.
		return reply;
	}

	/*
	 * Variations of this method.
	 */
	public static GwtTeamingException getGwtClientException(Log logger, Exception ex)                 {return getGwtClientException(logger, ex,   null   );}
	public static GwtTeamingException getGwtClientException(            Exception ex, String baseMsg) {return getGwtClientException(null,   ex,   baseMsg);}
	public static GwtTeamingException getGwtClientException(            Exception ex)                 {return getGwtClientException(null,   ex,   null   );}
	public static GwtTeamingException getGwtClientException(Log logger,               String baseMsg) {return getGwtClientException(logger, null, baseMsg);}
	public static GwtTeamingException getGwtClientException(Log logger)                               {return getGwtClientException(logger, null, null   );}
	public static GwtTeamingException getGwtClientException(                          String baseMsg) {return getGwtClientException(null,   null, baseMsg);}
	public static GwtTeamingException getGwtClientException()                                         {return getGwtClientException(null,   null, null   );}

	/*
	 * Maps a GWT client exception log level setting from the
	 * ssf*.properties and returns its enumeration equivalent.
	 */
	private static LogLevel getClientExceptionLogLevel(boolean wrapped) {
		LogLevel logLevelDefault;
		String   logLevelKey = LOG_LEVEL_KEY_BASE;
		if (wrapped)
		      {logLevelKey += LOG_LEVEL_WRAPPED_TAIL;     logLevelDefault = LogLevel.ERROR;}
		else  {logLevelKey += LOG_LEVEL_SYNTHESIZED_TAIL; logLevelDefault = LogLevel.DEBUG;}
		String logLevel     = SPropsUtil.getString(logLevelKey, logLevelDefault.name());
		return LogLevel.getEnum(logLevel, logLevelDefault);
	}
	
	/*
	 * Returns true if a message should be logged for the given
	 * exception (may be null) and false otherwise.
	 */
	private static boolean shouldLogMessage(boolean logAclExceptions, Throwable t) {
		return ((logAclExceptions || (null == t) || (!(t instanceof AccessControlException))));
	}
}
