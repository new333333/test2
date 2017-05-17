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

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.kablink.teaming.domain.ShareItem.RecipientType;
import org.kablink.teaming.gwt.client.binderviews.folderdata.FolderColumn;
import org.kablink.teaming.gwt.client.util.CollectionType;
import org.kablink.teaming.gwt.client.util.ShareRights;
import org.kablink.teaming.gwt.client.util.ShareRights.AccessRights;
import org.kablink.teaming.util.AllModulesInjected;
import org.kablink.teaming.util.SimpleProfiler;
import org.kablink.teaming.web.util.MiscUtil;

/**
 * Used to track shares in a GwtSharedMeItem.
 *
 * @author drfoster@novell.com
 */
public class GwtPerShareInfo {
	private boolean			m_recipientPublic;	// true -> Shared with Public (m_recipientId and m_recipientType will be ignored.)  false -> Not shared with Public.
	private boolean			m_rightsExpired;	//
	private Date			m_rightsExpire;		//
	private Date			m_shareDate;		//
	private Long			m_recipientId;		//
	private Long			m_shareId;			//
	private Long			m_sharerId;			//
	private RecipientType	m_recipientType;	//
	private ShareRights		m_rights;			//
	private String			m_comment;			//
	private String			m_recipientTitle;	//
	private String			m_sharerTitle;		//
	
	/**
	 * Inner class used to compare two GwtPerShareInfo's.
	 */
	public static class PerShareInfoComparator implements Comparator<GwtPerShareInfo> {
		private boolean				m_sortDescend;	// true -> Sort the list descending.  false -> Sort it ascending.
		private CollectionType		m_ct;			// The collection type of the per share info's being sorted.
		private String				m_sortBy;		// The column being sorted by.
		
		/**
		 * Constructor method.
		 * 
		 * @param ct
		 * @param sortBy
		 * @param sortDescend
		 */
		public PerShareInfoComparator(CollectionType ct, String sortBy, boolean sortDescend) {
			// Initialize the super class...
			super();
			
			// ...store the parameters.
			m_ct          = ct;
			m_sortBy      = sortBy;
			m_sortDescend = sortDescend;
		}

		/**
		 * Compares two GwtPerShareInfo objects.
		 * 
		 * Implements the Comparator.compare() method.
		 * 
		 * @param psi1
		 * @param psi2
		 * 
		 * @return
		 */
		@Override
		public int compare(GwtPerShareInfo psi1, GwtPerShareInfo psi2) {
			int  reply;
			if      (FolderColumn.COLUMN_SHARE_DATE.equals(      m_sortBy)) reply = compareByShareDates(     psi1, psi2);
			else if (FolderColumn.COLUMN_SHARE_EXPIRATION.equals(m_sortBy)) reply = compareByExpirationDates(psi1, psi2);
			else if (FolderColumn.COLUMN_SHARE_ACCESS.equals(    m_sortBy)) reply = compareByRights(         psi1, psi2);
			else if (FolderColumn.COLUMN_SHARE_MESSAGE.equals(   m_sortBy)) reply = compareByMessage(        psi1, psi2);
			else                                                            reply = compareByRecipient(m_ct, psi1, psi2);
			
			// If we're doing a descending sort...
			if (m_sortDescend) {
				// ...invert the reply.
				reply = (-reply);
			}
			
			// If we get here, reply contains the appropriate value for
			// the compare.  Return it.
			return reply;
		}

		/*
		 * Compares two dates.
		 */
		private static int compareByDates(Date d1, Date d2) {
			int  reply;
			long l1 = ((null == d1) ? 0 : d1.getTime());
			long l2 = ((null == d2) ? 0 : d2.getTime());
			if (l1 < l2)
				 reply = MiscUtil.COMPARE_LESS;
			else reply = MiscUtil.COMPARE_GREATER;
			return reply;
		}
		
		/**
		 * Compares two GwtPerShareInfo objects by expiration dates.
		 * 
		 * @param psi1
		 * @param psi2
		 */
		public static int compareByExpirationDates(GwtPerShareInfo psi1, GwtPerShareInfo psi2) {
			return compareByDates(psi1.getRightsExpire(), psi2.getRightsExpire());
		}
		
		/**
		 * Compares two GwtPerShareInfo objects by their share message.
		 * 
		 * @param psi1
		 * @param psi2
		 */
		public static int compareByMessage(GwtPerShareInfo psi1, GwtPerShareInfo psi2) {
			return compareByString(psi1.getComment(), psi2.getComment());
		}
		
		/**
		 * Compares two GwtPerShareInfo objects by their sharer or
		 * share recipient.
		 *
		 * @param ct
		 * @param psi1
		 * @param psi2
		 */
		public static int compareByRecipient(CollectionType ct, GwtPerShareInfo psi1, GwtPerShareInfo psi2) {
			// Extract the recipient titles to compare...
			String s1;
			String s2;
			if (CollectionType.SHARED_BY_ME.equals(ct)) {
				s1 = psi1.getRecipientTitle();
				s2 = psi2.getRecipientTitle();
			}
			else {
				s1 = psi1.getSharerTitle();
				s2 = psi2.getSharerTitle();
			}

			// ...and compare them.
			return compareByString(s1, s2);
		}
		
		/**
		 * Compares two GwtPerShareInfo objects by share rights.
		 * 
		 * @param psi1
		 * @param psi2
		 */
		public static int compareByRights(GwtPerShareInfo psi1, GwtPerShareInfo psi2) {
			int reply = MiscUtil.COMPARE_EQUAL;
			AccessRights ar1 = psi1.getRights().getAccessRights();
			AccessRights ar2 = psi2.getRights().getAccessRights();
			switch (ar1) {
			case VIEWER:
				switch (ar2) {
				case VIEWER:
					reply = compareBySharable(psi1, psi2);
					break;
					
				case EDITOR:
				case CONTRIBUTOR:
					reply = MiscUtil.COMPARE_LESS;
					break;
				}
				break;
				
			case EDITOR:
				switch (ar2) {
				case VIEWER:
					reply = MiscUtil.COMPARE_GREATER;
					break;
					
				case EDITOR:
					reply = compareBySharable(psi1, psi2);
					break;
					
				case CONTRIBUTOR:
					reply = MiscUtil.COMPARE_LESS;
					break;
				}
				break;
			
			case CONTRIBUTOR:
				switch (ar2) {
				case VIEWER:
				case EDITOR:
					reply = MiscUtil.COMPARE_GREATER;
					break;
					
				case CONTRIBUTOR:
					reply = compareBySharable(psi1, psi2);
					break;
				}
				break;
			}
			
			return reply;
		}
		
		/*
		 * Compares two GwtPerShareInfo objects by whether they're
		 * sharable.
		 */
		private static int compareBySharable(GwtPerShareInfo psi1, GwtPerShareInfo psi2) {
			int reply;
			boolean isSharable1 = psi1.getRights().getCanShareForward();
			boolean isSharable2 = psi2.getRights().getCanShareForward();
			if      (isSharable1 == isSharable2) reply = MiscUtil.COMPARE_EQUAL;
			else if (isSharable1)                reply = MiscUtil.COMPARE_GREATER;
			else                                 reply = MiscUtil.COMPARE_LESS;
			return reply;
		}

		/**
		 * Compares two GwtPerShareInfo objects by share dates.
		 * 
		 * @param psi1
		 * @param psi2
		 */
		public static int compareByShareDates(GwtPerShareInfo psi1, GwtPerShareInfo psi2) {
			return compareByDates(psi1.getShareDate(), psi2.getShareDate());
		}
		
		/*
		 * Compares two strings.
		 */
		private static int compareByString(String s1, String s2) {
			if (null == s1) s1 = "";
			if (null == s2) s2 = "";
			return MiscUtil.safeSColatedCompare(s1, s2);
		}
		
		/**
		 * Sorts the List<GwtPerShareInfo> attached to the
		 * GwtSharedMeItem's in a List<GwtSharedMeItem>.
		 * 
		 * @param bs
		 * @param ct
		 * @param shareItems
		 * @param sortBy
		 * @param sortDescend
		 */
		public static void sortPerShareInfoLists(AllModulesInjected bs, CollectionType ct, List<GwtSharedMeItem> shareItems, String sortBy, boolean sortDescend) {
			SimpleProfiler.start("GwtViewHelper.sortPerShareInfoLists()");
			try {
				// Scan the List<GwtSharedMeItem>.
				Comparator<GwtPerShareInfo> psiComparator = new PerShareInfoComparator(ct, sortBy, sortDescend);
				for (GwtSharedMeItem meItem:  shareItems) {
					// If this GwtSharedMeItem has any
					// GwtPerShareInfo's...
					List<GwtPerShareInfo> psiList = meItem.getPerShareInfos();
					if (MiscUtil.hasItems(psiList)) {
						// ...sort them.
						Collections.sort(psiList, psiComparator);
					}
				}
			}
			
			finally {
				SimpleProfiler.stop("GwtViewHelper.sortPerShareInfoLists()");
			}
		}
	}

	/**
	 * Constructor method.
	 *
	 * @param shareId			Share  ID.
	 * @param sharerId			Sharer ID.
	 * @param sharerTitle		Sharer title.
	 * @param recipientId		Recipient ID.
	 * @param recipientType		Recipient type (user, group or team.)
	 * @param recipientPublic	true -> Shared with Public (recipientId and recipientType will be ignored.)  false -> Not a public share.
	 * @param recipientTitle	Recipient title.
	 * @param shareDate			Time/date stamp the share was created.
	 * @param rights			The rights granted by the share.
	 * @param rightsExpire		Time/date stamp the share expires.
	 * @param rightsExpired		true -> The share is expired.  false -> It's not.
	 * @param comment			Comment associated with the share.
	 */
	public GwtPerShareInfo(Long shareId, Long sharerId, String sharerTitle, Long recipientId, RecipientType recipientType, boolean recipientPublic, String recipientTitle, Date shareDate, ShareRights rights, Date rightsExpire, boolean rightsExpired, String comment) {
		// Initialize the super class...
		super();
		
		// ...and store the parameters.
		setShareId(        shareId        );
		setSharerId(       sharerId       );
		setSharerTitle(    sharerTitle    );
		setRecipientId(    recipientId    );
		setRecipientType(  recipientType  );
		setRecipientPublic(recipientPublic);
		setRecipientTitle( recipientTitle );
		setShareDate(      shareDate      );
		setRights(         rights         );
		setRightsExpire(   rightsExpire   );
		setRightsExpired(  rightsExpired  );
		setComment(        comment        );
	}
	
	/**
	 * Get'er methods.
	 * 
	 * @return
	 */
	public boolean       isRecipientPublic() {return m_recipientPublic;}
	public boolean       isRightsExpired()   {return m_rightsExpired;  }
	public Date          getRightsExpire()   {return m_rightsExpire;   }
	public Date          getShareDate()      {return m_shareDate;      }
	public Long          getRecipientId()    {return m_recipientId;    }
	public Long          getShareId()        {return m_shareId;        }
	public Long          getSharerId()       {return m_sharerId;       }
	public RecipientType getRecipientType()  {return m_recipientType;  }
	public ShareRights   getRights()         {return m_rights;         }
	public String        getComment()        {return m_comment;        }
	public String        getSharerTitle()    {return m_sharerTitle;    }
	public String        getRecipientTitle() {return m_recipientTitle; }
	
	/**
	 * Set'er methods.
	 * 
	 * @param
	 */
	public void setRecipientPublic(boolean       recipientPublic) {m_recipientPublic = recipientPublic;}
	public void setRightsExpired(  boolean       rightsExpired)   {m_rightsExpired   = rightsExpired;  }
	public void setRightsExpire(   Date          rightsExpire)    {m_rightsExpire    = rightsExpire;   }
	public void setShareDate(      Date          shareDate)       {m_shareDate       = shareDate;      }
	public void setRecipientId(    Long          recipientId)     {m_recipientId     = recipientId;    }
	public void setShareId(        Long          shareId)         {m_shareId         = shareId;        }
	public void setSharerId(       Long          sharerId)        {m_sharerId        = sharerId;       }
	public void setRecipientType(  RecipientType recipientType)   {m_recipientType   = recipientType;  }
	public void setRights(         ShareRights   rights)          {m_rights          = rights;         }
	public void setComment(        String        comment)         {m_comment         = comment;        }
	public void setSharerTitle(    String        sharerTitle)     {m_sharerTitle     = sharerTitle;    }
	public void setRecipientTitle( String        recipientTitle)  {m_recipientTitle  = recipientTitle; }
}
