/**
 * Copyright (c) 1998-2010 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2010 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2010 Novell, Inc. All Rights Reserved.
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
package org.kablink.teaming.relevance.util;


import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.kablink.teaming.domain.AnyOwner;
import org.kablink.teaming.domain.Attachment;
import org.kablink.teaming.domain.DefinableEntity;
import org.kablink.teaming.domain.FolderEntry;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.domain.UserPrincipal;
import org.kablink.teaming.domain.Workspace;
import org.kablink.teaming.domain.EntityIdentifier.EntityType;
import org.kablink.teaming.module.folder.FolderModule;
import org.kablink.teaming.relevance.Relevance;
import org.kablink.teaming.relevance.RelevanceManager;
import org.kablink.teaming.util.AllModulesInjected;
import org.kablink.teaming.util.SpringContextUtil;
import org.kablink.teaming.util.Utils;
import org.kablink.teaming.web.util.MiscUtil;


/**
 * This class contains a collection of miscellaneous utility methods
 * for working with relevance integration.
 * 
 * @author drfoster@novell.com
 */
public final class RelevanceUtils
{
	/*
	 * Class constructor that prevents this class from being
	 * instantiated.
	 */
	private RelevanceUtils()
	{
		// Nothing to do.
	}

	/*
	 * Adds an Attachment to a Set<Attachment> if that Attachment is
	 * not already in the Set.
	 */
	private static void addUniqueAttachmentToSet(Set<Attachment> attSet, Attachment att) {
		String attId = att.getId();
		for (Iterator<Attachment> attIT = attSet.iterator(); attIT.hasNext();) {
			if (attId.equals(attIT.next().getId())) {
				return;
			}
		}
		attSet.add(att);
	}

	/*
	 * Adds a User to a Set<User> if that User is not already in the
	 * Set.
	 */
	private static void addUniqueUserToSet(Set<User> userSet, User user) {
		long userId = user.getId();
		for (Iterator<User> userIT = userSet.iterator(); userIT.hasNext();) {
			if (userId == userIT.next().getId()) {
				return;
			}
		}
		userSet.add(user);
	}

	/*
	 * Adds a Workspace to a Set<Workspace> if that Workspace is not
	 * already in the Set.
	 */
	private static void addUniqueWorkspaceToSet(Set<Workspace> wsSet, Workspace ws) {
		long wsId = ws.getId();
		for (Iterator<Workspace> wsIT = wsSet.iterator(); wsIT.hasNext();) {
			if (wsId == wsIT.next().getId()) {
				return;
			}
		}
		wsSet.add(ws);
	}

	/**
	 * Returns true if a DefinableEntity contains any Attachment's that
	 * have a relevance UUID assigned to them.
	 * 
	 * @param entity The DefinableEntity to check for containing any
	 *    Attachment's that have a relevance UUID assigned to them.
	 * 
	 * @return
	 */
	public static boolean entityHasRelatedFiles(DefinableEntity entity) {
		// Is relevance integration enabled?
		if (isRelevanceEnabled()) {
			// Yes!  Scan this entity's Attachment's.
			Set<Attachment> eaSet = entity.getAttachments();
			for (Iterator<Attachment> eaIT = eaSet.iterator(); eaIT.hasNext();) {
				// Does this Attachment have a relevance UUID connected?
				Attachment ea = eaIT.next();
				if (MiscUtil.hasString(ea.getRelevanceUUID())) {
					// Yes!  Return true.
					return true;
				}
			}
		}
		
		// If we get here, either relevance integration is disabled or
		// this entity did not have any Attachment's with relevance
		// UUIDs.  Return false.
		return false;
	}
	public static boolean entityHasRelatedFiles(String entityIdS) {
		Long entityID;
		try {
			entityID = Long.valueOf(entityIdS);
		}
		catch (Exception e) {
			// Although it should never happen, this is here mainly to
			// handle numeric format exceptions.
			getRelevanceEngine().getRelevanceLogger().error("RelevanceUtils.entityHasRelatedFiles( EXCEPTION ):  ", e);
			entityID = null;
		}
		return ((null == entityID) ? false : entityHasRelatedFiles(entityID));
	}
	public static boolean entityHasRelatedFiles(Long entityId) {
		FolderModule fm = ((FolderModule) SpringContextUtil.getBean("folderModule"));
		FolderEntry fe = fm.getEntry(null, entityId);
		return ((null != fe) && entityHasRelatedFiles(fe));
	}
	
	/**
	 * Given a Set<Attachment>, returns a Set<User> that maps
	 * each Attachment to the User that last modified it.  Note that
	 * any given User is only included once in the resultant Set.
	 * 
	 * @param attSet The Set<Attachment> whose User's are being
	 *    queried.
	 * 
	 * @return
	 */
	public static Set<User> getAttachmentUsers(Set<Attachment> attSet) {
		HashSet<User> reply = new HashSet<User>();
		for (Iterator<Attachment> attIT = attSet.iterator(); attIT.hasNext();) {
			Attachment att = attIT.next();
			UserPrincipal modifierPrincipal = att.getModification().getPrincipal();
			if (modifierPrincipal instanceof User) {
				addUniqueUserToSet(reply, ((User) modifierPrincipal));
			}
			else {
				getRelevanceEngine().getRelevanceLogger().debug("RelevanceUtils.getAttachmentUsers( 'Non-User UserPrincipal ignored.' ):  Title:  '" + Utils.getUserTitle(modifierPrincipal) + "'");
			}
		}
		
		return reply;
	}

	/*
	 * Returns the nearest containing Workspace of an Attachment.
	 */
	private static Workspace getAttachmentWorkspace(Attachment att) {
		AnyOwner attOwner = att.getOwner();
		DefinableEntity entity = attOwner.getEntity();
		EntityType eType = entity.getEntityType();
		while (eType != EntityType.workspace) {
			entity = entity.getParentBinder();
			if (null == entity) {
				break;
			}
			eType = entity.getEntityType();
		}
		
		return ((Workspace) entity);
	}
	
	/**
	 * Given a Set<Attachment>, returns a Set<Workspace> that
	 * maps each Attachment to its containing Workspace.  Note that any
	 * given Workspace is only included once in the resultant Set.
	 * 
	 * @param attSet The Set<Attachment> whose Workspace's are being
	 *    queried.
	 *                
	 * @return
	 */
	public static Set<Workspace> getAttachmentWorkspaces(Set<Attachment> attSet) {
		HashSet<Workspace> reply = new HashSet<Workspace>();
		for (Iterator<Attachment> attIT = attSet.iterator(); attIT.hasNext();) {
			Attachment att = attIT.next();
			addUniqueWorkspaceToSet(reply, getAttachmentWorkspace(att));
		}
		
		return reply;
	}

	/**
	 * Returns a Set<Attachment> of all the Attachment's that are
	 * related to the Attachment's on a FolderEntry.  Note that any
	 * given Attachment is only included once in the resultant Set.
	 * 
	 * @param bs The AllModulesInjected that we're running under.
	 * @param fe The FolderEntry whose related Attachment's are being
	 *    queried.
	 *    
	 * @return
	 * 
	 * @throws IOException
	 */
	public static Set<Attachment> getRelatedAttachments(AllModulesInjected bs, FolderEntry fe) throws IOException {
		HashSet<Attachment> reply = new HashSet<Attachment>();
		
		// Is relevance integration enabled?
		Relevance re = RelevanceUtils.getRelevanceEngine();
		if (re.isRelevanceEnabled()) {
			Set<Attachment> feAttSet = fe.getAttachments();
			for (Iterator<Attachment> feAttIT = feAttSet.iterator(); feAttIT.hasNext();) {
				// Does this Attachment have a relevance UUID?
				Attachment feAtt = feAttIT.next();
				String feAttUUID = feAtt.getRelevanceUUID();
				if (MiscUtil.hasString(feAttUUID)) {
					// Yes!  Scan its related attachments.
					Set<Attachment> rAttSet = re.getRelevantAttachments(bs, feAttUUID);
					for (Iterator<Attachment> rAttIT = rAttSet.iterator(); rAttIT.hasNext();) {
						// Are we already tracking this Attachment?
						Attachment rAtt = rAttIT.next();
						addUniqueAttachmentToSet(reply, rAtt);
					}
				}
			}
		}
		
		return reply;
	}
	
	/**
	 * Returns the relevance engine that we're currently running under.
	 * 
	 * @return
	 */
	public static Relevance getRelevanceEngine() {
		RelevanceManager relevanceManager = ((RelevanceManager) SpringContextUtil.getBean("relevanceManager"));
		return ((null == relevanceManager) ? null : relevanceManager.getRelevanceEngine());
	}

	/**
	 * Returns a UUID String in a format that's consistently searchable
	 * by the Lucene search engine.
	 * 
	 * @param uuid The UUID whose searchable version is being queried.
	 * 
	 * @return
	 */
	public static String getSearchableUUID(String uuid) {
		if (null != uuid) {
			uuid = uuid.trim();
			if (0 < uuid.length()) {
				String[] uuidParts = uuid.split("-");
				StringBuffer uuidBuf = new StringBuffer("uuid");
				for (int i = 0; i < uuidParts.length; i += 1) {
					uuidBuf.append(uuidParts[i]);
				}
				uuid = uuidBuf.toString().toLowerCase();
			}
		}
		return uuid;
	}
	
	/**
	 * Return true if relevance integration is currently enabled and
	 * false otherwise.
	 * 
	 * @return
	 */
	public static boolean isRelevanceEnabled() {
		Relevance relevanceEngine = getRelevanceEngine();
		return ((null != relevanceEngine) && relevanceEngine.isRelevanceEnabled());
	}
}
