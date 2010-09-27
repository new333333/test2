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
package org.kablink.teaming.remoting.ws.model;

import java.io.Serializable;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kablink.teaming.dao.CoreDao;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.Folder;
import org.kablink.teaming.domain.FolderEntry;
import org.kablink.teaming.domain.Principal;
import org.kablink.teaming.domain.Workspace;
import org.kablink.teaming.domain.HistoryStamp;
import org.kablink.teaming.domain.EntityIdentifier.EntityType;
import org.kablink.teaming.util.AllModulesInjected;
import org.kablink.teaming.web.util.PermaLinkUtil;
import org.kablink.teaming.web.util.TrashHelper;
import org.kablink.teaming.web.util.WebUrlUtil;
import org.kablink.teaming.web.util.TrashHelper.TrashEntry;

public class TrashBrief implements Serializable {
	protected static Log logger = LogFactory.getLog(TrashHelper.class);

	private BinderBrief binderBrief;
	private FolderEntryBrief folderEntryBrief;
	
	/**
	 * Constructor methods for TrashBrief objects.
	 * 
	 * @param bs
	 * @param cd
	 * @param trashEntry
	 */
	public TrashBrief() {}
	public TrashBrief(AllModulesInjected bs, CoreDao cd, TrashEntry trashEntry) {
		binderBrief = null;
		folderEntryBrief = null;
		
		// If the TrashEntry is a FolderEntry...
		if (trashEntry.isEntry()) {
			// ...construct a FolderEntryBrief object from it.
			folderEntryBrief = folderEntryBriefFromTrashEntry(bs, trashEntry);
		}
		
		// Otherwise, if the TrashEntry is a Binder...
		else if (trashEntry.isBinder()) {
			// ...construct a BinderBrief object from it.
			binderBrief = binderBriefFromTrashEntry(bs, cd, trashEntry);
		}
	}

	/**
	 * Returns this TrashBrief's BinderBrief, if it refers to a Binder
	 * and null otherwise.
	 * 
	 * @return
	 */
	public BinderBrief getBinderBrief() {
		return binderBrief;
	}

	/**
	 * Returns true if this TrashBrief references a Binder and false
	 * otherwise.
	 * 
	 * @return
	 */
	public boolean isBinder() {
		return (null != binderBrief);
	}

	/**
	 * Returns this TrashBrief's FolderEntryBrief, if it refers to a
	 * FolderEntry and null otherwise.
	 * 
	 * @return
	 */
	public FolderEntryBrief getFolderEntryBrief() {
		return folderEntryBrief;
	}
	
	/**
	 * Returns true if this TrashBrief references a FolderEntry and
	 * false otherwise.
	 * 
	 * @return
	 */
	public boolean isFolderEntry() {
		return (null != folderEntryBrief);
	}
	
	/*
	 * Creates a BinderBrief from a TrashEntry referencing a Binder.
	 */
	private static BinderBrief binderBriefFromTrashEntry(AllModulesInjected bs, CoreDao cd, TrashEntry te) {
		// If the TrashEntry isn't a Binder...
		if ((null == te) || (!(te.isBinder()))) {
			// ...we can't construct a BinderBrief for it.
			return null;
		}
		
		// Access what we need from the Binder...
		String entityType = EntityType.folder.name();
		Long id = te.m_docId;
		Binder binder = null;
		try {
			binder = (Binder) cd.load(Binder.class, id);
		}
		catch(Exception e) {
			logger.warn("TrashBrief.binderBriefFromTrashEntry():  " + e.toString(), e);
			return null;
		}
		if (null == binder) {
			return null;
		}
		String title = binder.getTitle();
		Long preDeletedWhen;
		Long preDeletedBy;
		if (binder instanceof Workspace) {
			Workspace ws = ((Workspace) binder);
			preDeletedWhen = ws.getPreDeletedWhen();
			preDeletedBy = ws.getPreDeletedBy();
			
		}
		else if (binder instanceof Folder) {
			Folder f = ((Folder) binder);
			preDeletedWhen = f.getPreDeletedWhen();
			preDeletedBy = f.getPreDeletedBy();
		}
		else {
			preDeletedWhen = Long.valueOf(0);
			preDeletedBy = null;
		}
		Timestamp creation;
		Timestamp modification;
		if (null != preDeletedBy) {
			Principal p = bs.getProfileModule().getEntry(preDeletedBy);
			creation = new Timestamp(p.getTitle(), new Date(preDeletedWhen));
			modification = new Timestamp(p.getTitle(), new Date(preDeletedWhen));
		}
		else {
			creation = new Timestamp("", new Date(0));
			modification = new Timestamp("", new Date(0));
		}
		Integer definitionType = binder.getDefinitionType();
		String permaLink = PermaLinkUtil.getPermalink(binder);
		
		// ...and construct and return a BinderBrief.
		return
			new BinderBrief(id,
				title,
				entityType,
				definitionType,
				creation,
				modification,
				permaLink);
	}
	
	/*
	 * Creates a FolderEntryBrief from a TrashEntry referencing a
	 * FolderEntry.
	 */
	private static FolderEntryBrief folderEntryBriefFromTrashEntry(AllModulesInjected bs, TrashEntry te) {
		// If the TrashEntry isn't referencing a FolderEntry...
		if ((null == te) || (!(te.isEntry()))) {
			// ...we can't construct a FolderEntryBrief for it.
			return null;
		}
		
		// Access what we need from the FolderEntry and construct
		// a FolderEntryBrief from it.
		FolderEntry fe = bs.getFolderModule().getEntry(te.m_locationBinderId, te.m_docId);
		FolderEntryBrief feb = new FolderEntryBrief();
		feb.setId(fe.getId());
		feb.setBinderId(fe.getParentBinder().getId());
		if(null != fe.getEntryDefId()) {
			feb.setDefinitionId(fe.getEntryDefId());
		}
		feb.setTitle(fe.getTitle());
		feb.setDocNumber(fe.getDocNumber());
		feb.setDocLevel(fe.getDocLevel());
		feb.setHref(WebUrlUtil.getEntryViewURL(fe));
		if(null != fe.getCreation()) {
			feb.setCreation(getTimestampFromHistory(fe.getCreation()));
		}
		if(null != fe.getModification()) {
			feb.setModification(getTimestampFromHistory(fe.getModification()));
		}
		return feb;
	}

	/*
	 * Constructs a Timestamp from a HistoryStamp.
	 */
	private static Timestamp getTimestampFromHistory(HistoryStamp hs) {
		return new Timestamp(hs.getPrincipal().getName(), hs.getDate());
	}
}
