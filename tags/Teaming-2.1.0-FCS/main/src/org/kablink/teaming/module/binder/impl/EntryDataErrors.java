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
package org.kablink.teaming.module.binder.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.kablink.teaming.util.NLT;


public class EntryDataErrors implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private List problems;
	
	public EntryDataErrors() {
		this.problems = new ArrayList();
	}
	
	public void addProblem(Problem problem) {
		problems.add(problem);
	}
	
	public List getProblems() {
		return problems;
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		for(int i = 0; i < problems.size(); i++) {
			if(i > 0)
				sb.append("\n");
			sb.append(((Problem) problems.get(i)).toString());
		}
		return sb.toString();
	}
	
	public static class Problem implements Serializable {
		private static final long serialVersionUID = 1L;
		
		// Problem types
		public static int GENERAL_PROBLEM								= 0;
		
		// Message codes corresponding to each problem type.
		public static String[] typeCodes = {
			"general.error.anErrorOccurred"
		};
		
		private int type; // required - one of the constants defined above
		// at most one of the following two is set.
		private Exception exception; // may be null
		
		public Problem(int type, Exception e) {
			this.type = type;
			this.exception = e;
			//logger.warn("Entry data error: " + e.getMessage());
		}

		public Exception getException() {
			return exception; // may be null
		}
		
		public int getType() {
			return type;
		}
		
		public String getTypeCode() {
			return typeCodes[type];
		}
		
		public String toString() {
			StringBuffer sb = new StringBuffer();
			String typeCodeError = NLT.get(getTypeCode()).trim();
			sb.append(typeCodeError);
			return sb.toString();
		}
	}

}
