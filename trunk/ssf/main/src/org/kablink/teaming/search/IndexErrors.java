/**
 * The contents of this file are subject to the Common Public Attribution License Version 1.0 (the "CPAL");
 * you may not use this file except in compliance with the CPAL. You may obtain a copy of the CPAL at
 * http://www.opensource.org/licenses/cpal_1.0. The CPAL is based on the Mozilla Public License Version 1.1
 * but Sections 14 and 15 have been added to cover use of software over a computer network and provide for
 * limited attribution for the Original Developer. In addition, Exhibit A has been modified to be
 * consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT WARRANTY OF ANY KIND,
 * either express or implied. See the CPAL for the specific language governing rights and limitations
 * under the CPAL.
 * 
 * The Original Code is ICEcore. The Original Developer is SiteScape, Inc. All portions of the code
 * written by SiteScape, Inc. are Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * 
 * 
 * Attribution Information
 * Attribution Copyright Notice: Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by ICEcore]
 * Attribution URL: [www.icecore.com]
 * Graphic Image as provided in the Covered Code [web/docroot/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are defined in the CPAL as a
 * work which combines Covered Code or portions thereof with code not governed by the terms of the CPAL.
 * 
 * 
 * SITESCAPE and the SiteScape logo are registered trademarks and ICEcore and the ICEcore logos
 * are trademarks of SiteScape, Inc.
 */
package org.kablink.teaming.search;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.Entry;
import org.kablink.teaming.domain.FolderEntry;
import org.kablink.teaming.domain.Principal;
import org.kablink.teaming.util.NLT;


public class IndexErrors implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private List<Binder> binders;
	private List<Entry> entries;
	private Integer errorCount;
	
	public IndexErrors() {
		this.binders = new ArrayList();
		this.entries = new ArrayList();
		this.errorCount = new Integer(0);
	}
	
	public void addError(Binder binder) {
		binders.add(binder);
		errorCount++;
	}
	
	public void addError(Entry entry) {
		entries.add(entry);
		errorCount++;
	}
	
	public List<Binder> getBinders() {
		return binders;
	}
	
	public List<Entry> getEntries() {
		return entries;
	}
	
	public Integer getErrorCount() {
		return errorCount;
	}
	
	public boolean checkIfErrors() {
		if (errorCount == 0) return false;
		else return true;
	}
	
	public void add(Integer count) {
		errorCount += count;
	}
	
	public void add(IndexErrors ie) {
		binders.addAll(ie.getBinders());
		entries.addAll(ie.getEntries());
		add(ie.getErrorCount());
	}
}
