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
package org.kablink.teaming.gwt.server.util;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.kablink.teaming.ApplicationExistsException;
import org.kablink.teaming.ApplicationGroupExistsException;
import org.kablink.teaming.GroupExistsException;
import org.kablink.teaming.PasswordMismatchException;
import org.kablink.teaming.UserExistsException;
import org.kablink.teaming.domain.MobileDevice;
import org.kablink.teaming.domain.NoBinderByTheIdException;
import org.kablink.teaming.domain.NoFolderEntryByTheIdException;
import org.kablink.teaming.domain.NoUserByTheIdException;
import org.kablink.teaming.fi.InvalidPasswordException;
import org.kablink.teaming.fi.InvalidUsernameException;
import org.kablink.teaming.fi.LogonFailureException;
import org.kablink.teaming.gwt.client.GwtTeamingException;
import org.kablink.teaming.gwt.client.GwtTeamingException.ExceptionType;
import org.kablink.teaming.gwt.client.GwtTeamingException.NotLoggedInSubtype;
import org.kablink.teaming.gwt.client.admin.ExtensionDefinitionInUseException;
import org.kablink.teaming.gwt.client.binderviews.folderdata.DescriptionHtml;
import org.kablink.teaming.gwt.client.binderviews.folderdata.FolderRow.PrincipalInfoId;
import org.kablink.teaming.gwt.client.binderviews.folderdata.GuestInfo;
import org.kablink.teaming.gwt.client.presence.GwtPresenceInfo;
import org.kablink.teaming.gwt.client.rpc.shared.CanAddEntitiesRpcResponseData;
import org.kablink.teaming.gwt.client.util.AssignmentInfo;
import org.kablink.teaming.gwt.client.util.CommentsInfo;
import org.kablink.teaming.gwt.client.util.EmailAddressInfo;
import org.kablink.teaming.gwt.client.util.EntityId;
import org.kablink.teaming.gwt.client.util.EntryEventInfo;
import org.kablink.teaming.gwt.client.util.EntryLinkInfo;
import org.kablink.teaming.gwt.client.util.EntryTitleInfo;
import org.kablink.teaming.gwt.client.util.MobileDevicesInfo;
import org.kablink.teaming.gwt.client.util.PrincipalAdminType;
import org.kablink.teaming.gwt.client.util.PrincipalInfo;
import org.kablink.teaming.gwt.client.util.TaskFolderInfo;
import org.kablink.teaming.gwt.client.util.TaskStats;
import org.kablink.teaming.gwt.client.util.ViewFileInfo;
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
	 * Returns a Boolean for dumping.
	 * 
	 * @param b
	 * 
	 * @return
	 */
	public static String dumpBoolean(Boolean b) {
		return ((null == b) ? "*null*" : String.valueOf(b.booleanValue()));
	}
	
	/**
	 * Returns a String containing the dump of the contents of an
	 * CanAddEntitiesRpcResponseData.
	 * 
	 * @param caeData
	 * 
	 * @return
	 */
	public static String dumpCAEAsString(CanAddEntitiesRpcResponseData caeData) {
		StringBuffer sb = new StringBuffer();
		if (null == caeData) {
			sb.append("*null*");
		}
		else {
			sb.append("Can add entries:  " + caeData.canAddEntries() + ", ");
			sb.append("Can add folders:  " + caeData.canAddFolders()       );
		}
		return sb.toString();
	}
	
	/**
	 * Returns a Date for dumping.
	 * 
	 * @param d
	 * 
	 * @return
	 */
	public static String dumpDate(Date d) {
		return ((null == d) ? "*null*" : GwtServerHelper.getDateTimeString(d));
	}
	
	/**
	 * Returns a String containing the dump of the contents of an
	 * EntityId.
	 * 
	 * @param eid
	 * 
	 * @return
	 */
	public static String dumpEIDAsString(EntityId eid) {
		StringBuffer sb = new StringBuffer();
		if (null == eid) {
			sb.append("*null*");
		}
		else {
			String s = eid.getEntityType();
			sb.append("Type:  " + ((null == s) ? "*null*" : s) + ", ");
			
			Long id = eid.getBinderId();       sb.append("Binder ID:  "        + GwtLogHelper.dumpLong(  id) + ", ");
			     id = eid.getEntityId();       sb.append("Entity ID:  "        + GwtLogHelper.dumpLong(  id) + ", ");
			     s  = eid.getMobileDeviceId(); sb.append("Mobile Device ID:  " + GwtLogHelper.dumpString(s )       );
		}
		return sb.toString();
	}
	
	/**
	 * Returns a String containing the dump of the contents of a
	 * List<Long>.
	 * 
	 * @param ll
	 * 
	 * @return
	 */
	public static String dumpLLAsString(List<Long> ll) {
		StringBuffer sb = new StringBuffer();
		if (null == ll) {
			sb.append("*null*");
		}
		else {
			int llSize = ll.size();
			sb.append(String.valueOf(llSize));
			if (0 < llSize) {
				sb.append(":  [");
				for (int i = 0; i < llSize; i += 1) {
					if (0 < i) {
						sb.append(", ");
					}
					sb.append(ll.get(i));
				}
				sb.append("]");
			}
		}
		return sb.toString();
	}
	
	/**
	 * Returns a Long for dumping.
	 * 
	 * @param l
	 * 
	 * @return
	 */
	public static String dumpLong(Long l) {
		return ((null == l) ? "*null*" : String.valueOf(l));
	}

	/**
	 * Dumps a Map<String, Boolean> to a logger's debug output.
	 * 
	 * @param logger
	 * @param start1
	 * @param start2
	 * @param map
	 */
	public static void dumpMapStringBooleanToDebug(Log logger, String start1, String start2, Map<String, Boolean> map) {
		if (null == map) {
			debug(logger, start1 + "  *null*");
		}
		
		else if (map.isEmpty()) {
			debug(logger, start1 + "  *empty*");
		}
		
		else {
			debug(logger, start1);
			Set<String> keys = map.keySet();
			for (String key:  keys) {
				debug(logger, start2 + "Key:  " + "\"" + key + "\", Boolean:  " + dumpBoolean(map.get(key)));
			}
		}
	}
	
	/**
	 * Dumps a Map<String, CommentsInfo> to a logger's debug output.
	 * 
	 * @param logger
	 * @param start1
	 * @param start2
	 * @param map
	 */
	public static void dumpMapStringCommentsInfoTooDebug(Log logger, String start1, String start2, Map<String, CommentsInfo> map) {
		if (null == map) {
			debug(logger, start1 + "  *null*");
		}
		
		else if (map.isEmpty()) {
			debug(logger, start1 + "  *empty*");
		}
		
		else {
			debug(logger, start1);
			Set<String> keys = map.keySet();
			for (String key:  keys) {
				debug(logger, start2 + "Key:  " + "\"" + key + "\"");
				
				CommentsInfo ci = map.get(key);
				if (null == ci) {
					debug(logger, start2 + "CommentsInfo:  *null*");
				}
				else {
					debug(logger, start2 + "CommentsInfo:");
					debug(logger, start2 + "...Comments disabled:  " + ci.isCommentsDisabled()          );
					debug(logger, start2 + "...Comment count:      " + ci.getCommentsCount()            );
					debug(logger, start2 + "...Entity ID:          " + dumpEIDAsString(ci.getEntityId()));
					debug(logger, start2 + "...Entity title:       " + dumpString(ci.getEntityTitle())  );
				}
			}
		}
	}
	
	/**
	 * Dumps a Map<String, DescriptionHtml> to a logger's debug output.
	 * 
	 * @param logger
	 * @param start1
	 * @param start2
	 * @param map
	 */
	public static void dumpMapStringDescriptionHtmlToDebug(Log logger, String start1, String start2, Map<String, DescriptionHtml> map) {
		if (null == map) {
			debug(logger, start1 + "  *null*");
		}
		
		else if (map.isEmpty()) {
			debug(logger, start1 + "  *empty*");
		}
		
		else {
			debug(logger, start1);
			Set<String> keys = map.keySet();
			for (String key:  keys) {
				debug(logger, start2 + "Key:  " + "\"" + key + "\"");
				
				DescriptionHtml dh = map.get(key);
				if (null == dh) {
					debug(logger, start2 + "DescriptionHtml:  *null*");
				}
				else {
					debug(logger, start2 + "DescriptionHtml:");
					debug(logger, start2 + "...Is HTML:      " + dh.isHtml()                    );
					debug(logger, start2 + "...Description:  " + dumpString(dh.getDescription()));
				}
			}
		}
	}
	
	/**
	 * Dumps a Map<String, EmailAddressInfo> to a logger's debug
	 * output.
	 * 
	 * @param logger
	 * @param start1
	 * @param start2
	 * @param map
	 */
	public static void dumpMapStringEmailAddressInfoToDebug(Log logger, String start1, String start2, Map<String, EmailAddressInfo> map) {
		if (null == map) {
			debug(logger, start1 + "  *null*");
		}
		
		else if (map.isEmpty()) {
			debug(logger, start1 + "  *empty*");
		}
		
		else {
			debug(logger, start1);
			Set<String> keys = map.keySet();
			for (String key:  keys) {
				debug(logger, start2 + "Key:  " + "\"" + key + "\"");
				
				EmailAddressInfo emai = map.get(key);
				if (null == emai) {
					debug(logger, start2 + "EmailAddressInfo:  *null*");
				}
				else {
					debug(logger, start2 + "EmailAddressInfo:");
					debug(logger, start2 + "...EMA:               " + dumpString(emai.getEmailAddress()));
					debug(logger, start2 + "...User has WS:       " + emai.isUserHasWS()                );
					debug(logger, start2 + "...User WS in trash:  " + emai.isUserWSInTrash()            );
				}
			}
		}
	}
	
	/**
	 * Dumps a Map<String, EntryEventInfo> to a logger's debug output.
	 * 
	 * @param logger
	 * @param start1
	 * @param start2
	 * @param map
	 */
	public static void dumpMapStringEntryEventInfoToDebug(Log logger, String start1, String start2, Map<String, EntryEventInfo> map) {
		if (null == map) {
			debug(logger, start1 + "  *null*");
		}
		
		else if (map.isEmpty()) {
			debug(logger, start1 + "  *empty*");
		}
		
		else {
			debug(logger, start1);
			Set<String> keys = map.keySet();
			for (String key:  keys) {
				debug(logger, start2 + "Key:  " + "\"" + key + "\"");
				
				EntryEventInfo eei = map.get(key);
				if (null == eei) {
					debug(logger, start2 + "EntryEventInfo:  *null*");
				}
				else {
					debug(logger, start2 + "EntryEventInfo:");
					debug(logger, start2 + "...All day event:       " + eei.getAllDayEvent()          );
					debug(logger, start2 + "...Duration days only:  " + eei.getDurationDaysOnly()     );
					debug(logger, start2 + "...Duration days:       " + eei.getDurationDays()         );
					debug(logger, start2 + "...End date:            " + dumpString(eei.getEndDate())  );
					debug(logger, start2 + "...Start date:          " + dumpString(eei.getStartDate()));
				}
			}
		}
	}
	
	/**
	 * Dumps a Map<String, EntryLinkInfo> to a logger's debug output.
	 * 
	 * @param logger
	 * @param start1
	 * @param start2
	 * @param map
	 */
	public static void dumpMapStringEntryLinkInfoToDebug(Log logger, String start1, String start2, Map<String, EntryLinkInfo> map) {
		if (null == map) {
			debug(logger, start1 + "  *null*");
		}
		
		else if (map.isEmpty()) {
			debug(logger, start1 + "  *empty*");
		}
		
		else {
			debug(logger, start1);
			Set<String> keys = map.keySet();
			for (String key:  keys) {
				debug(logger, start2 + "Key:  " + "\"" + key + "\"");
				
				EntryLinkInfo eli = map.get(key);
				if (null == eli) {
					debug(logger, start2 + "EntryLinkInfo:  *null*");
				}
				else {
					debug(logger, start2 + "EntryLinkInfo:");
					debug(logger, start2 + "...HREF:    " + dumpString(eli.getHref())  );
					debug(logger, start2 + "...Target:  " + dumpString(eli.getTarget()));
					debug(logger, start2 + "...Text:    " + dumpString(eli.getText())  );
				}
			}
		}
	}
	
	/**
	 * Dumps a Map<String, EntryTitleInfo> to a logger's debug output.
	 * 
	 * @param logger
	 * @param start1
	 * @param start2
	 * @param map
	 */
	public static void dumpMapStringEntryTitleInfoToDebug(Log logger, String start1, String start2, Map<String, EntryTitleInfo> map) {
		if (null == map) {
			debug(logger, start1 + "  *null*");
		}
		
		else if (map.isEmpty()) {
			debug(logger, start1 + "  *empty*");
		}
		
		else {
			debug(logger, start1);
			Set<String> keys = map.keySet();
			for (String key:  keys) {
				debug(logger, start2 + "Key:  " + "\"" + key + "\"");
				
				EntryTitleInfo eli = map.get(key);
				if (null == eli) {
					debug(logger, start2 + "EntryTitleInfo:  *null*");
				}
				else {
					debug(logger, start2 + "EntryTitleInfo:");
					debug(logger, start2 + "...Title:                  " + dumpString(eli.getTitle())                    );
					debug(logger, start2 + "...Description is HTML:    " + eli.isDescriptionHtml()                       );
					debug(logger, start2 + "...Description:            " + dumpString(eli.getDescription())              );
					debug(logger, start2 + "...Hidden:                 " + eli.isHidden()                                );
					debug(logger, start2 + "...Seen:                   " + eli.isSeen()                                  );
					debug(logger, start2 + "...Trash:                  " + eli.isTrash()                                 );
					debug(logger, start2 + "...EntityId:               " + dumpEIDAsString(eli.getEntityId())            );
					debug(logger, start2 + "...CanAddEntities:         " + dumpCAEAsString(eli.getCanAddFolderEntities()));
					debug(logger, start2 + "...File:                   " + eli.isFile()                                  );
					debug(logger, start2 + "...File download URL:      " + dumpString(eli.getFileDownloadUrl())          );
					debug(logger, start2 + "...File view as HTML URL:  " + dumpString(eli.getFileViewAsHtmlUrl())        );
					debug(logger, start2 + "...File icon:              " + dumpString(eli.getFileIcon())                 );
				}
			}
		}
	}
	
	/**
	 * Dumps a Map<String, GuestInfo> to a logger's debug output.
	 * 
	 * @param logger
	 * @param start1
	 * @param start2
	 * @param map
	 */
	public static void dumpMapStringGuestInfoToDebug(Log logger, String start1, String start2, Map<String, GuestInfo> map) {
		if (null == map) {
			debug(logger, start1 + "  *null*");
		}
		
		else if (map.isEmpty()) {
			debug(logger, start1 + "  *empty*");
		}
		
		else {
			debug(logger, start1);
			Set<String> keys = map.keySet();
			for (String key:  keys) {
				debug(logger, start2 + "Key:  " + "\"" + key + "\"");
				
				GuestInfo gi = map.get(key);
				if (null == gi) {
					debug(logger, start2 + "GuestInfo:  *null*");
				}
				else {
					debug(logger, start2 + "GuestInfo:");
					debug(logger, start2 + "...Title:        " + dumpString(gi.getTitle())             );
					debug(logger, start2 + "...User ID:      " + dumpLong(  gi.getUserId())            );
					debug(logger, start2 + "...Avatar URL:   " + dumpString(gi.getAvatarUrl())         );
					debug(logger, start2 + "...Profile URL:  " + dumpString(gi.getProfileUrl())        );
					debug(logger, start2 + "...Phone:        " + dumpString(gi.getPhone())             );
					debug(logger, start2 + "...EMA:          " + dumpString(gi.getEmailAddress())      );
					debug(logger, start2 + "...Mobile EMA:   " + dumpString(gi.getMobileEmailAddress()));
					debug(logger, start2 + "...Text EMA:     " + dumpString(gi.getTextEmailAddress())  );
				}
			}
		}
	}
	
	/**
	 * Dumps a Map<String, List<AssignmentInfo>> to a logger's debug
	 * output.
	 * 
	 * @param logger
	 * @param start1
	 * @param start2
	 * @param map
	 */
	public static void dumpMapStringListAssignmentInfoToDebug(Log logger, String start1, String start2, Map<String, List<AssignmentInfo>> map) {
		if (null == map) {
			debug(logger, start1 + "  *null*");
		}
		
		else if (map.isEmpty()) {
			debug(logger, start1 + "  *empty*");
		}
		
		else {
			debug(logger, start1);
			Set<String> keys = map.keySet();
			for (String key:  keys) {
				debug(logger, start2 + "Key:  " + "\"" + key + "\"");
				
				List<AssignmentInfo> aiList = map.get(key);
				if (null == aiList) {
					debug(logger, start2 + "List<AssignmentInfo>:  *null*");
				}
				else {
					
					debug(logger, start2 + "List<AssignmentInfo>:  " + aiList.size() + " items.");
					int aiIndex = 0;
					for (AssignmentInfo ai:  aiList) {
						debug(logger, start2 + "...Index:                      " + aiIndex                                );
						debug(logger, start2 + "......ID:                      " + dumpLong(ai.getId())                   );
						debug(logger, start2 + "......Title:                   " + dumpString(ai.getTitle())              );
						debug(logger, start2 + "......Assigee type:            " + ai.getAssigneeType().name()            );
						debug(logger, start2 + "......Person:                  " + ai.isUserPerson()                      );
						debug(logger, start2 + "......Disabled:                " + ai.isUserDisabled()                    );
						debug(logger, start2 + "......External:                " + ai.isUserExternal()                    );
						debug(logger, start2 + "......Has WS:                  " + ai.isUserHasWS()                       );
						debug(logger, start2 + "......WS in trash:             " + ai.isUserWSInTrash()                   );
						debug(logger, start2 + "......EMA:                     " + dumpString(ai.getEmailAddress())       );
						debug(logger, start2 + "......Presence info:           " + dumpPIAsString(ai.getPresence())       );
						debug(logger, start2 + "......Presence user WS ID:     " + dumpLong(ai.getPresenceUserWSId())     );
						debug(logger, start2 + "......Presence dude:           " + dumpString(ai.getPresenceDude())       );
						debug(logger, start2 + "......Members:                 " + ai.getMembers()                        );
						debug(logger, start2 + "......Hover:                   " + dumpString(ai.getHover())              );
						debug(logger, start2 + "......View profile entry URL:  " + dumpString(ai.getViewProfileEntryUrl()));
						debug(logger, start2 + "......Avatar URL:              " + dumpString(ai.getAvatarUrl())          );
						
						aiIndex += 1;
					}
				}
			}
		}
	}
	
	/**
	 * Dumps a Map<String, List<TaskFolderInfo>> to a logger's debug
	 * output.
	 * 
	 * @param logger
	 * @param start1
	 * @param start2
	 * @param map
	 */
	public static void dumpMapStringListTaskFolderInfoToDebug(Log logger, String start1, String start2, Map<String, List<TaskFolderInfo>> map) {
		if (null == map) {
			debug(logger, start1 + "  *null*");
		}
		
		else if (map.isEmpty()) {
			debug(logger, start1 + "  *empty*");
		}
		
		else {
			debug(logger, start1);
			Set<String> keys = map.keySet();
			for (String key:  keys) {
				debug(logger, start2 + "Key:  " + "\"" + key + "\"");
				
				List<TaskFolderInfo> tfiList = map.get(key);
				if (null == tfiList) {
					debug(logger, start2 + "List<TaskFolderInfo>:  *null*");
				}
				else {
					
					debug(logger, start2 + "List<TaskFolderInfo>:  " + tfiList.size() + " items.");
					int tfiIndex = 0;
					for (TaskFolderInfo tfi:  tfiList) {
						debug(logger, start2 + "...Index:                " + tfiIndex                               );
						debug(logger, start2 + "......Title:             " + dumpString(tfi.getTitle())             );
						debug(logger, start2 + "......Folder ID:         " + dumpLong(tfi.getFolderId())            );
						debug(logger, start2 + "......Folder permalink:  " + dumpString(tfi.getFolderPermalink())   );
						debug(logger, start2 + "......Task statistics:   " + dumpTSAsString(tfi.getTaskStatistics()));
						
						tfiIndex += 1;
					}
				}
			}
		}
	}
	
	/**
	 * Dumps a Map<String, MobileDevicesInfo> to a logger's debug
	 * output.
	 * 
	 * @param logger
	 * @param start1
	 * @param start2
	 * @param map
	 */
	public static void dumpMapStringMobileDevicesInfoToDebug(Log logger, String start1, String start2, Map<String, MobileDevicesInfo> map) {
		if (null == map) {
			debug(logger, start1 + "  *null*");
		}
		
		else if (map.isEmpty()) {
			debug(logger, start1 + "  *empty*");
		}
		
		else {
			debug(logger, start1);
			Set<String> keys = map.keySet();
			for (String key:  keys) {
				debug(logger, start2 + "Key:  " + "\"" + key + "\"");
				
				MobileDevicesInfo mdi = map.get(key);
				if (null == mdi) {
					debug(logger, start2 + "MobileDevicesInfo:  *null*");
				}
				else {
					debug(logger, start2 + "MobileDevicesInfo:");
					debug(logger, start2 + "...User ID:         " + dumpLong(mdi.getUserId())  );
					debug(logger, start2 + "...Mobile devices:  " + mdi.getMobileDevicesCount());
				}
			}
		}
	}
	
	/**
	 * Dumps a Map<String, PrincipalAdminType> to a logger's debug
	 * output.
	 * 
	 * @param logger
	 * @param start1
	 * @param start2
	 * @param map
	 */
	public static void dumpMapStringPrincipalAdminTypeToDebug(Log logger, String start1, String start2, Map<String, PrincipalAdminType> map) {
		if (null == map) {
			debug(logger, start1 + "  *null*");
		}
		
		else if (map.isEmpty()) {
			debug(logger, start1 + "  *empty*");
		}
		
		else {
			debug(logger, start1);
			Set<String> keys = map.keySet();
			for (String key:  keys) {
				debug(logger, start2 + "Key:  " + "\"" + key + "\"");
				
				PrincipalAdminType pat = map.get(key);
				if (null == pat) {
					debug(logger, start2 + "PrincipalAdminType:  *null*");
				}
				else {
					debug(logger, start2 + "PrincipalAdminType:");
					debug(logger, start2 + "...Admin:           " + pat.isAdmin()                );
					debug(logger, start2 + "...Principal type:  " + pat.getPrincipalType().name());
				}
			}
		}
	}
	
	/**
	 * Dumps a Map<String, PrincipalInfo> to a logger's debug output.
	 * 
	 * @param logger
	 * @param start1
	 * @param start2
	 * @param map
	 */
	public static void dumpMapStringPrincipalInfoToDebug(Log logger, String start1, String start2, Map<String, PrincipalInfo> map) {
		if (null == map) {
			debug(logger, start1 + "  *null*");
		}
		
		else if (map.isEmpty()) {
			debug(logger, start1 + "  *empty*");
		}
		
		else {
			debug(logger, start1);
			Set<String> keys = map.keySet();
			for (String key:  keys) {
				debug(logger, start2 + "Key:  " + "\"" + key + "\"");
				
				PrincipalInfo pi = map.get(key);
				if (null == pi) {
					debug(logger, start2 + "PrincipalInfo:  *null*");
				}
				else {
					debug(logger, start2 + "PrincipalInfo:");
					debug(logger, start2 + "...ID:                      " + dumpLong(pi.getId())                   );
					debug(logger, start2 + "...Title:                   " + dumpString(pi.getTitle())              );
					debug(logger, start2 + "...Person:                  " + pi.isUserPerson()                      );
					debug(logger, start2 + "...Disabled:                " + pi.isUserDisabled()                    );
					debug(logger, start2 + "...External:                " + pi.isUserExternal()                    );
					debug(logger, start2 + "...Has WS:                  " + pi.isUserHasWS()                       );
					debug(logger, start2 + "...WS in trash:             " + pi.isUserWSInTrash()                   );
					debug(logger, start2 + "...EMA:                     " + dumpString(pi.getEmailAddress())       );
					debug(logger, start2 + "...Presence info:           " + dumpPIAsString(pi.getPresence())       );
					debug(logger, start2 + "...Presence user WS ID:     " + dumpLong(pi.getPresenceUserWSId())     );
					debug(logger, start2 + "...Presence dude:           " + dumpString(pi.getPresenceDude())       );
					debug(logger, start2 + "...Members:                 " + pi.getMembers()                        );
					debug(logger, start2 + "...View profile entry URL:  " + dumpString(pi.getViewProfileEntryUrl()));
					debug(logger, start2 + "...Avatar URL:              " + dumpString(pi.getAvatarUrl())          );
				}
			}
		}
	}
	
	/**
	 * Dumps a Map<String, PrincipalInfoId> to a logger's debug output.
	 * 
	 * @param logger
	 * @param start1
	 * @param start2
	 * @param map
	 */
	public static void dumpMapStringPrincipalInfoIdToDebug(Log logger, String start1, String start2, Map<String, PrincipalInfoId> map) {
		if (null == map) {
			debug(logger, start1 + "  *null*");
		}
		
		else if (map.isEmpty()) {
			debug(logger, start1 + "  *empty*");
		}
		
		else {
			debug(logger, start1);
			Set<String> keys = map.keySet();
			for (String key:  keys) {
				debug(logger, start2 + "Key:  " + "\"" + key + "\"");
				
				PrincipalInfoId piId = map.get(key);
				if (null == piId) {
					debug(logger, start2 + "PrincipalInfoId:  *null*");
				}
				else {
					debug(logger, start2 + "PrincipalInfoId:");
					debug(logger, start2 + "...ID:  " + dumpLong(piId.getId()));
				}
			}
		}
	}
	
	/**
	 * Dumps a Map<String, ViewFileInfo> to a logger's debug output.
	 * 
	 * @param logger
	 * @param start1
	 * @param start2
	 * @param map
	 */
	public static void dumpMapStringViewFileInfoToDebug(Log logger, String start1, String start2, Map<String, ViewFileInfo> map) {
		if (null == map) {
			debug(logger, start1 + "  *null*");
		}
		
		else if (map.isEmpty()) {
			debug(logger, start1 + "  *empty*");
		}
		
		else {
			debug(logger, start1);
			Set<String> keys = map.keySet();
			for (String key:  keys) {
				debug(logger, start2 + "Key:  " + "\"" + key + "\"");
				
				ViewFileInfo vfi = map.get(key);
				if (null == vfi) {
					debug(logger, start2 + "ViewFileInfo:  *null*");
				}
				else {
					debug(logger, start2 + "ViewFileInfo:");
					debug(logger, start2 + "...EID:        " + dumpEIDAsString(vfi.getEntityId())   );
					debug(logger, start2 + "...File ID:    " + dumpString(     vfi.getFileId())     );
					debug(logger, start2 + "...File time:  " + dumpString(     vfi.getFileTime())   );
					debug(logger, start2 + "...View type:  " + dumpString(     vfi.getViewType())   );
					debug(logger, start2 + "...URL:        " + dumpString(     vfi.getViewFileUrl()));
				}
			}
		}
	}
	
	/**
	 * Dumps a Map<String, String> to a logger's debug output.
	 * 
	 * @param logger
	 * @param start1
	 * @param start2
	 * @param map
	 */
	public static void dumpMapStringStringToDebug(Log logger, String start1, String start2, Map<String, String> map) {
		if (null == map) {
			debug(logger, start1 + "  *null*");
		}
		
		else if (map.isEmpty()) {
			debug(logger, start1 + "  *empty*");
		}
		
		else {
			debug(logger, start1);
			Set<String> keys = map.keySet();
			for (String key:  keys) {
				debug(logger, start2 + "Key:  " + "\"" + key + "\", String:  " + dumpString(map.get(key)));
			}
		}
	}
	
	/**
	 * Dumps a MobileDevice object to a logger's debug output.
	 * 
	 * @param logger
	 * @param start1
	 * @param start2
	 * @param md
	 */
	public static void dumpMobileDeviceToDebug(Log logger, String start1, String start2, Object mdo) {
		if (null == mdo) {
			debug(logger, start1 + "  *null*");
		}
		
		else {
			MobileDevice md = ((MobileDevice) mdo);
			
			debug(logger, start1);
			debug(logger, start2 + "MobileDevice:");
			debug(logger, start2 + "...User ID:         " + dumpLong(md.getUserId())          );
			debug(logger, start2 + "...User title:      " + dumpString(md.getUserTitle())     );
			debug(logger, start2 + "...Device ID:       " + dumpString(md.getDeviceId())      );
			debug(logger, start2 + "...Last login:      " + dumpDate(md.getLastLogin())       );
			debug(logger, start2 + "...Wipe scheduled:  " + dumpBoolean(md.getWipeScheduled()));
			debug(logger, start2 + "...Last wipe:       " + dumpDate(md.getLastWipe())        );
			debug(logger, start2 + "...Description:     " + dumpString(md.getDescription())   );
		}
	}
	
	/**
	 * Returns a String containing the dump of the contents of a
	 * GwtPresenceInfo.
	 * 
	 * @param eid
	 * 
	 * @return
	 */
	public static String dumpPIAsString(GwtPresenceInfo eid) {
		StringBuffer sb = new StringBuffer();
		if (null == eid) {
			sb.append("*null*");
		}
		else {
			sb.append("Status:  " + eid.getStatus() + " (" + dumpString(eid.getStatusText()) + ")");
		}
		return sb.toString();
	}
	
	/**
	 * Returns a String for dumping.
	 * 
	 * @param s
	 * 
	 * @return
	 */
	public static String dumpString(String s) {
		return ((null == s) ? "*null*" : ("\"" + s + "\""));
	}
	
	/**
	 * Returns a String containing the dump of the contents of a
	 * TaskStats.
	 * 
	 * @param ts
	 * 
	 * @return
	 */
	public static String dumpTSAsString(TaskStats ts) {
		StringBuffer sb = new StringBuffer();
		if (null == ts) {
			sb.append("*null*");
		}
		else {
			sb.append("C0:"    + ts.getCompleted0()   + ", ");
			sb.append("C10:"   + ts.getCompleted10()  + ", ");
			sb.append("C20:"   + ts.getCompleted20()  + ", ");
			sb.append("C30:"   + ts.getCompleted30()  + ", ");
			sb.append("C40:"   + ts.getCompleted40()  + ", ");
			sb.append("C50:"   + ts.getCompleted50()  + ", ");
			sb.append("C60:"   + ts.getCompleted60()  + ", ");
			sb.append("C70:"   + ts.getCompleted70()  + ", ");
			sb.append("C80:"   + ts.getCompleted80()  + ", ");
			sb.append("C90:"   + ts.getCompleted90()  + ", ");
			sb.append("C1000:" + ts.getCompleted100() + ", ");
			
			sb.append("Critical:" + ts.getPriorityCritical() + ", ");
			sb.append("High:"     + ts.getPriorityHigh()     + ", ");
			sb.append("Least:"    + ts.getPriorityLeast()    + ", ");
			sb.append("Low:"      + ts.getPriorityLow()      + ", ");
			sb.append("Medium:"   + ts.getPriorityMedium()   + ", ");
			sb.append("None:"     + ts.getPriorityNone()     + ", ");
			
			sb.append("Canceled:"     + ts.getStatusCanceled()  + ", ");
			sb.append("Completed:"    + ts.getStatusCompleted() + ", ");
			sb.append("In process:"   + ts.getStatusInProcess() + ", ");
			sb.append("Needs action:" + ts.getStatusNeedsAction()     );
		}
		return sb.toString();
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
				ExceptionType      exType;
				NotLoggedInSubtype subType = null;
				
				if      (ex instanceof AccessControlException               )  exType = ExceptionType.ACCESS_CONTROL_EXCEPTION;
				else if (ex instanceof ApplicationExistsException           )  exType = ExceptionType.APPLICATION_EXISTS_EXCEPTION;
				else if (ex instanceof ApplicationGroupExistsException      )  exType = ExceptionType.APPLICATION_GROUP_EXISTS_EXCEPTION;
				else if (ex instanceof ExtensionDefinitionInUseException    )  exType = ExceptionType.EXTENSION_DEFINITION_IN_USE;
				else if (ex instanceof FavoritesLimitExceededException      )  exType = ExceptionType.FAVORITES_LIMIT_EXCEEDED;
				else if (ex instanceof InvalidPasswordException             ) {exType = ExceptionType.USER_NOT_LOGGED_IN; subType = NotLoggedInSubtype.INVALID_PASSWORD;}
				else if (ex instanceof InvalidUsernameException             ) {exType = ExceptionType.USER_NOT_LOGGED_IN; subType = NotLoggedInSubtype.INVALID_USERNAME;}
				else if (ex instanceof LogonFailureException                ) {exType = ExceptionType.USER_NOT_LOGGED_IN; subType = NotLoggedInSubtype.LOGON_FAILED;    }
				else if (ex instanceof NoBinderByTheIdException             )  exType = ExceptionType.NO_BINDER_BY_THE_ID_EXCEPTION;
				else if (ex instanceof NoFolderEntryByTheIdException        )  exType = ExceptionType.NO_FOLDER_ENTRY_BY_THE_ID_EXCEPTION;
				else if (ex instanceof NoUserByTheIdException               )  exType = ExceptionType.NO_BINDER_BY_THE_ID_EXCEPTION;
				else if (ex instanceof OperationAccessControlExceptionNoName)  exType = ExceptionType.ACCESS_CONTROL_EXCEPTION;
				else if (ex instanceof GroupExistsException                 )  exType = ExceptionType.GROUP_ALREADY_EXISTS;
				else if (ex instanceof RDException                          )  exType = ExceptionType.NET_FOLDER_ROOT_ALREADY_EXISTS;
				else if (ex instanceof PasswordMismatchException            )  exType = ExceptionType.CHANGE_PASSWORD_EXCEPTION;
				else if (ex instanceof UserExistsException                  )  exType = ExceptionType.USER_ALREADY_EXISTS;
				else                                                           exType = ExceptionType.UNKNOWN;
				
				reply.setExceptionType(     exType );
				reply.setNotLoggedInSubtype(subType);
				reply.setWrapped(           true   );
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
				if (null != ex) {
					if (ex instanceof AccessControlException)
				         error(traceLogger, clientLogMsg + ex);	// Ensures that something gets logged for these since their stack trace is otherwise masked out.
					else error(traceLogger, clientLogMsg,  ex);
				}
				else {
					error(traceLogger, clientLogMsg, reply);
				}
			}
			
			break;
			
		case DEBUG:
			// If logging is enabled...
			if (isDebugEnabled(logger))
			     traceLogger = logger;
			else traceLogger = m_logger;
			if (isDebugEnabled(traceLogger)) {
				// ...log the exception that got us here.
				if (null != ex) {
					if (ex instanceof AccessControlException)
				         debug(traceLogger, clientLogMsg + ex);	// Ensures that something gets logged for these since their stack trace is otherwise masked out.
					else debug(traceLogger, clientLogMsg,  ex);
				}
				else {
					debug(traceLogger, clientLogMsg, reply);
				}
			}
			
			break;
			
		case FATAL:
			// If logging is enabled...
			if (isFatalEnabled(logger))
			     traceLogger = logger;
			else traceLogger = m_logger;
			if (isFatalEnabled(traceLogger)) {
				// ...log the exception that got us here.
				if (null != ex) {
					if (ex instanceof AccessControlException)
				         fatal(traceLogger, clientLogMsg + ex);	// Ensures that something gets logged for these since their stack trace is otherwise masked out.
					else fatal(traceLogger, clientLogMsg,  ex);
				}
				else {
					fatal(traceLogger, clientLogMsg, reply);
				}
			}
			
			break;
			
		case INFO:
			// If logging is enabled...
			if (isInfoEnabled(logger))
			     traceLogger = logger;
			else traceLogger = m_logger;
			if (isInfoEnabled(traceLogger)) {
				// ...log the exception that got us here.
				if (null != ex) {
					if (ex instanceof AccessControlException)
				         info(traceLogger, clientLogMsg + ex);	// Ensures that something gets logged for these since their stack trace is otherwise masked out.
					else info(traceLogger, clientLogMsg,  ex);
				}
				else {
					info(traceLogger, clientLogMsg, reply);
				}
			}
			
			break;
			
		case TRACE:
			// If logging is enabled...
			if (isTraceEnabled(logger))
			     traceLogger = logger;
			else traceLogger = m_logger;
			if (isTraceEnabled(traceLogger)) {
				// ...log the exception that got us here.
				if (null != ex) {
					if (ex instanceof AccessControlException)
				         trace(traceLogger, clientLogMsg + ex);	// Ensures that something gets logged for these since their stack trace is otherwise masked out.
					else trace(traceLogger, clientLogMsg,  ex);
				}
				else {
					trace(traceLogger, clientLogMsg, reply);
				}
			}
			
			break;
			
		case WARN:
			// If logging is enabled...
			if (isWarnEnabled(logger))
			     traceLogger = logger;
			else traceLogger = m_logger;
			if (isWarnEnabled(traceLogger)) {
				// ...log the exception that got us here.
				if (null != ex) {
					if (ex instanceof AccessControlException)
				         warn(traceLogger, clientLogMsg + ex);	// Ensures that something gets logged for these since their stack trace is otherwise masked out.
					else warn(traceLogger, clientLogMsg,  ex);
				}
				else {
					warn(traceLogger, clientLogMsg, reply);
				}
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
