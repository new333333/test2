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
package org.kablink.teaming.search;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.Entry;

public class IndexErrors implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private List<Binder> binders;
	private List<Entry> entries;
	private List<String> generalErrors;
	private int errorCount;
	
	public IndexErrors() {
		this.binders = new ArrayList<Binder>();
		this.entries = new ArrayList<Entry>();
		this.generalErrors = new ArrayList<String>();
		this.errorCount = 0;
	}
	
	public synchronized void addError(Binder binder) {
		binders.add(binder);
		errorCount++;
	}
	
	public synchronized void addError(Entry entry) {
		entries.add(entry);
		errorCount++;
	}
	
	public synchronized void addError(String msg) {
		generalErrors.add(msg);
		errorCount++;
	}
	
	public synchronized List<Binder> getBinders() {
		return binders;
	}
	
	public synchronized List<Entry> getEntries() {
		return entries;
	}
	
	public synchronized List<String> getGeneralErrors() {
		return generalErrors;
	}
	
	public synchronized int getErrorCount() {
		return errorCount;
	}
	
	public synchronized boolean checkIfErrors() {
		if (errorCount == 0) return false;
		else return true;
	}
	
	public synchronized void add(int count) {
		errorCount += count;
	}
	
	public synchronized void add(IndexErrors ie) {
		binders.addAll(ie.getBinders());
		entries.addAll(ie.getEntries());
		generalErrors.addAll(ie.getGeneralErrors());
		add(ie.getErrorCount());
	}
}
