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
package org.kablink.teaming.module.shared;

import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.domain.FolderEntry;
import org.kablink.teaming.domain.Principal;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.module.folder.FolderModule.FolderOperation;
import org.kablink.teaming.security.function.WorkAreaOperation;
import org.kablink.teaming.util.SPropsUtil;

/**
 * Utility methods for checking comment access.
 * 
 * @author drfoster@novell.com
 */
public class CommentAccessUtils  {
	private static Boolean m_useACLsForComments;	// Set true/false first time it's need based on ssf*.properties settings.
	
	/**
	 * Enumeration used to tell the caller of a method how to proceed.
	 */
	public enum CommentAccess {
		ALLOWED,
		REJECTED,
		PROCESS_ACLS;

		/**
		 * Get'er methods.
		 * 
		 * @return
		 */
		public boolean isAllowed()     {return this.equals(ALLOWED     );}
		public boolean isRejected()    {return this.equals(REJECTED    );}
		public boolean isProcessAcls() {return this.equals(PROCESS_ACLS);}
	}
	
	/*
	 * Inhibits this class from being instantiated. 
	 */
	private CommentAccessUtils() {
		// Nothing to do.
	}

	/**
	 * Given a FolderEntry, WorkAreaOperation and User, returns a
	 * CommentAccess that tells the caller how to proceed with access
	 * checking on the entry.
	 * 
	 * @param fe
	 * @param wao
	 * @param user
	 * 
	 * @return
	 */
	public static CommentAccess checkCommentAccess(FolderEntry fe, WorkAreaOperation wao, User user) {
		// If we don't have an operation or we're supposed to do ACL
		// checks for comments or this isn't a comment...
		if ((null == wao) || useACLsForComments() || fe.isTop()) {
			// ...simply tell the call to process the ACLs.
			return CommentAccess.PROCESS_ACLS;
		}
		
		// Is this the built-in admin?
		CommentAccess reply;
		if (user.isAdmin()) {
			// Yes!  They're always allowed to do everything.
			reply = CommentAccess.ALLOWED;
		}
		
		// No, this isn't the built-in admin!  Is it  Guest?
		else if (user.isShared()) {
			// Yes!  They're not allowed to do anything.
			reply =
				(wao.equals(WorkAreaOperation.READ_ENTRIES) ?
					CommentAccess.PROCESS_ACLS              :
					CommentAccess.REJECTED);
		}
			
		else {
			// No, this isn't the Guest either!  Is it an operation we
			// care about?
			reply = CommentAccess.PROCESS_ACLS;
			boolean delete =                            wao.equals(WorkAreaOperation.DELETE_ENTRIES);
			boolean modify = ((!delete) &&              wao.equals(WorkAreaOperation.MODIFY_ENTRIES));
			boolean rename = ((!delete) && (!modify) && wao.equals(WorkAreaOperation.RENAME_ENTRIES));
			if (delete || modify || rename) {
				// Yes!  Does the user own the comment?
				Long      userId = user.getId();
				Principal owner  = fe.getOwner();
				if (owner.getId().equals(userId)) {
					// Yes!  The operation is always allowed.
					reply = CommentAccess.ALLOWED;
				}
				
				else {
					// No, the user doesn't own the comment!  Do
					// they own any of the comment's parentage the
					// comment is attached to?
					FolderEntry parentEntry = fe.getParentEntry();
					while (null != parentEntry) {
						if (parentEntry.getOwner().getId().equals(userId)) {
							// Yes!  They're allowed to delete somebody
							// else's comment but not to edit or rename
							// it.
							if (delete) reply = CommentAccess.ALLOWED;
							else        reply = CommentAccess.REJECTED;
							break;
						}
						parentEntry = parentEntry.getParentEntry();
					}
					
					// If the user doesn't own any of the comment's
					// parentage...
					if (null == parentEntry) {
						// ...reject the request.
						reply = CommentAccess.REJECTED;
					}
				}
			}
		}
		
		return reply;
	}
	
	public static CommentAccess checkCommentAccess(FolderEntry fe, WorkAreaOperation wao) {
		// Always use the initial form of the method.
		return checkCommentAccess(fe, wao, RequestContextHolder.getRequestContext().getUser());
	}
	
	public static CommentAccess checkCommentAccess(FolderEntry fe, FolderOperation fo, User user) {
		// Always use the initial form of the method.
		return checkCommentAccess(fe, mapFOToWAO(fo), user);
	}
	
	public static CommentAccess checkCommentAccess(FolderEntry fe, FolderOperation fo) {
		// Always use the initial form of the method.
		return checkCommentAccess(fe, mapFOToWAO(fo), RequestContextHolder.getRequestContext().getUser());
	}

	/*
	 * Returns the WorkAreaOperation equivalent of the
	 * FolderOperation's we care about against comments.
	 */
	private static WorkAreaOperation mapFOToWAO(FolderOperation fo) {
		WorkAreaOperation waoReply;
		switch (fo) {
		case deleteEntry:
		case preDeleteEntry:  waoReply = WorkAreaOperation.DELETE_ENTRIES; break;
		case modifyEntry:     waoReply = WorkAreaOperation.MODIFY_ENTRIES; break;
		case renameEntry:     waoReply = WorkAreaOperation.RENAME_ENTRIES; break;
		default:              waoReply = null;                             break;
		}
		return waoReply;
	}

	/*
	 * Returns true if we supposed to perform ACL checks against
	 * comments or false if we special case handle them as per changes
	 * with Filr Axion/Vibe 4.0.1.
	 */
	private static boolean useACLsForComments() {
		// If we haven't read the ssf*.properties flag yet...
		if (null == m_useACLsForComments) {
			// ...read it now...
			m_useACLsForComments = SPropsUtil.getBoolean("use.acls.for.comments", false);
		}
		
		// ...and return its value.
		return m_useACLsForComments;
	}
}
