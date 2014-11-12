/**
 * Copyright (c) 1998-2014 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2014 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2014 Novell, Inc. All Rights Reserved.
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
package org.kablink.teaming.gwt.client.rpc.shared;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * This class holds the response data for RPC commands that return a
 * List<String> of error messages.
 * 
 * @author drfoster@novell.com
 */
public class ErrorListRpcResponseData implements IsSerializable, VibeRpcResponseData {
	private List<ErrorInfo> m_errorList;	//

	/**
	 * Inner class used use to represent an instance of an error
	 * message.
	 */
	public static class ErrorInfo implements IsSerializable {
		private ErrorLevel	m_level;	//
		private String		m_message;	//
		
		/**
		 * Constructor method.
		 * 
		 * GWT serialization requires a zero length constructor.
		 */
		public ErrorInfo() {
			// Initialize the super class...
			super();

			// ...and initialize anything else requiring
			// ...initialization.
			setLevel(ErrorLevel.ERROR);
		}

		/**
		 * Constructor method.
		 * 
		 * @param message
		 * @param level
		 */
		public ErrorInfo(String message, ErrorLevel level) {
			// Initialize this object...
			this();
			
			// ...and store the parameters.
			setMessage(message);
			setLevel(  level  );
		}

		/**
		 * Get'er methods.
		 * 
		 * @return
		 */
		public ErrorLevel getLevel()   {return m_level;  }
		public String     getMessage() {return m_message;}
		
		/**
		 * Set'er methods.
		 * 
		 * @param
		 */
		public void setLevel(  ErrorLevel level)   {m_level   = level;  }
		public void setMessage(String     message) {m_message = message;}
		
		/**
		 * Creator methods.
		 * 
		 * @param message
		 * 
		 * @return
		 */
		public static ErrorInfo createError(  String message) {return new ErrorInfo(message, ErrorLevel.ERROR);  }
		public static ErrorInfo createWarning(String message) {return new ErrorInfo(message, ErrorLevel.WARNING);}
	}

	/**
	 * Enumeration to specify the level of an error.
	 */
	public enum ErrorLevel implements IsSerializable {
		ERROR,
		WARNING,
	}
	
	/**
	 * Constructor method.
	 * 
	 * For GWT serialization, must have a zero parameter
	 * constructor.
	 */
	public ErrorListRpcResponseData() {
		// Initialize the super class.
		super();
	}

	/**
	 * Constructor method.
	 * 
	 * @param errorList
	 */
	public ErrorListRpcResponseData(List<ErrorInfo> errorList) {
		// Initialize this object...
		this();
		
		// ...and store the parameter.
		setErrorList(errorList);
	}
	
	/**
	 * Add'er methods.
	 * 
	 * @param
	 */
	public void addError(   String       error)    {addMessage( error,    ErrorLevel.ERROR);  }
	public void addErrors(  List<String> errors)   {addMessages(errors,   ErrorLevel.ERROR);  }
	public void addWarning( String       warning)  {addMessage( warning,  ErrorLevel.WARNING);}
	public void addWarnings(List<String> warnings) {addMessages(warnings, ErrorLevel.WARNING);}
	
	/**
	 * Get'er methods.
	 * 
	 * @return
	 */
	public boolean         hasErrors()    {return ((null != m_errorList) && (!(m_errorList.isEmpty())));}
	public List<ErrorInfo> getErrorList() {return m_errorList;                                          }
	
	/**
	 * Set'er methods.
	 * 
	 * @param
	 */
	public void setErrorList(List<ErrorInfo> errorList) {m_errorList = errorList;}

	/**
	 * Returns the count of errors in the List<ErrorInfo>.
	 * 
	 * @param errors
	 * 
	 * @return
	 */
	public int getErrorCount() {
		return getLevelCount(ErrorLevel.ERROR);
	}
	
	/*
	 * Returns the count of a specific error level in the
	 * List<ErrorInfo>.
	 */
	private int getLevelCount(ErrorLevel level) {
		int reply = 0;
		if ((null != m_errorList) && (!(m_errorList.isEmpty()))) {
			for (ErrorInfo error:  m_errorList) {
				if (level.equals(error.getLevel())) {
					reply += 1;
				}
			}
		}
		return reply;
	}
	
	/**
	 * Returns the total message count in the List<ErrorInfo>.
	 * 
	 * @param errors
	 * 
	 * @return
	 */
	public int getTotalMessageCount() {
		return ((null == m_errorList) ? 0 : m_errorList.size());
	}
	
	/**
	 * Returns the count of Warnings in the List<ErrorInfo>.
	 * 
	 * @param errors
	 * 
	 * @return
	 */
	public int getWarningCount() {
		return getLevelCount(ErrorLevel.WARNING);
	}
	
	/*
	 * Adds a message to the list.
	 */
	private void addMessage(String message, ErrorLevel level) {
		// If we weren't given a message...
		if ((null == message) || (0 == message.length())) {
			// ...bail.
			return;
		}

		// If we don't have an error list yet...
		if (null == m_errorList) {
			// ...allocate one.
			m_errorList = new ArrayList<ErrorInfo>();
		}

		// Add the message to the list.
		m_errorList.add(new ErrorInfo(message, level));
	}
	
	/*
	 * Adds a list of messages to the list.
	 */
	private void addMessages(List<String> messages, ErrorLevel level) {
		if (null != messages) {
			for (String message: messages) {
				addMessage(message, level);
			}
		}
	}
}
