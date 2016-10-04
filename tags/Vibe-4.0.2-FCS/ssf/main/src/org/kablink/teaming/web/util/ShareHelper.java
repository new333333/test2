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
package org.kablink.teaming.web.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kablink.teaming.domain.*;
import org.kablink.teaming.module.sharing.SharingModule;
import org.kablink.teaming.security.function.WorkAreaOperation;
import org.kablink.teaming.util.*;

import java.util.*;

/**
 * Helper class dealing with various administrative functions.
 * 
 * @author drfoster@novell.com
 */
public class ShareHelper {
	protected static Log m_logger = LogFactory.getLog(ShareHelper.class);

	public static SharingModule.EntityShareRights calculateHighestEntityShareRights(AllModulesInjected ami, EntityIdentifier entityId) {
		return ami.getSharingModule().calculateHighestEntityShareRights(entityId);
	}

	/**
	 * Get AccessRights that corresponds to the given RightSet
	 *
	 * @param rightSet
	 *
	 * @return
	 */
	public static ShareItem.Role getAccessRightsFromRightSet( WorkAreaOperation.RightSet rightSet )
	{
		ShareItem.Role accessRights;

		accessRights = ShareItem.Role.NONE;

		if ( rightSet != null )
		{
			WorkAreaOperation.RightSet viewerRightSet;
			WorkAreaOperation.RightSet editorRightSet;
			WorkAreaOperation.RightSet contributorRightSet;
			boolean shareInternal;
			boolean shareExternal;
			boolean sharePublic;
			boolean shareForward;
			boolean folderShareInternal;
			boolean folderShareExternal;
			boolean folderSharePublic;
			boolean folderShareForward;

			// areRightSetsEqual() compares "share internal", "share external", "share public" and "share forward".
			// That is why we are setting them to false.
			shareInternal = rightSet.isAllowSharing();
			shareExternal = rightSet.isAllowSharingExternal();
			sharePublic = rightSet.isAllowSharingPublic();
			shareForward = rightSet.isAllowSharingForward();
			folderShareInternal = rightSet.isAllowFolderSharingInternal();
			folderShareExternal = rightSet.isAllowFolderSharingExternal();
			folderSharePublic = rightSet.isAllowFolderSharingPublic();
			folderShareForward = rightSet.isAllowFolderSharingForward();
			rightSet.setAllowSharing( false );
			rightSet.setAllowSharingExternal( false );
			rightSet.setAllowSharingPublic( false );
			rightSet.setAllowSharingForward( false );
			rightSet.setAllowFolderSharingInternal( false );
			rightSet.setAllowFolderSharingExternal( false );
			rightSet.setAllowFolderSharingPublic( false );
			rightSet.setAllowFolderSharingForward( false );

			viewerRightSet = getViewerRightSet();
			editorRightSet = getEditorRightSet();
			contributorRightSet = getContributorRightSet();

			// Is the given RightSet equal to the "View" RightSet
			if ( areRightSetsGreaterOrEqual( rightSet, viewerRightSet ) )
			{
				// Yes
				accessRights = ShareItem.Role.VIEWER;
			}
			// Is the given RightSet equal to the "Editor" RightSet
			if ( areRightSetsGreaterOrEqual( rightSet, editorRightSet ) )
			{
				// Yes
				accessRights = ShareItem.Role.EDITOR;
			}
			// Is the given RightSet equal to the "Contributor" RightSet
			if ( areRightSetsGreaterOrEqual( rightSet, contributorRightSet ) )
			{
				// Yes
				accessRights = ShareItem.Role.CONTRIBUTOR;
			}

			// Restore the values we set to false.
			rightSet.setAllowSharing( shareInternal );
			rightSet.setAllowSharingExternal( shareExternal );
			rightSet.setAllowSharingPublic( sharePublic );
			rightSet.setAllowSharingForward( shareForward );
			rightSet.setAllowFolderSharingInternal( folderShareInternal );
			rightSet.setAllowFolderSharingExternal( folderShareExternal );
			rightSet.setAllowFolderSharingPublic( folderSharePublic );
			rightSet.setAllowFolderSharingForward( folderShareForward );
		}

		return accessRights;
	}

	/*
	 * Return the RightSet that corresponds to the "Contributor" rights
	 */
	public static WorkAreaOperation.RightSet getContributorRightSet()
	{
		WorkAreaOperation.RightSet rightSet;
		List<WorkAreaOperation> operations;

		operations = ShareItem.Role.CONTRIBUTOR.getRightSet().getRights();
		rightSet = new WorkAreaOperation.RightSet( operations.toArray( new WorkAreaOperation[ operations.size() ] ) );

		return rightSet;
	}

	/*
	 * Return the RightSet that corresponds to the "Editor" rights
	 */
	public static WorkAreaOperation.RightSet getEditorRightSet()
	{
		WorkAreaOperation.RightSet rightSet;
		List<WorkAreaOperation> operations;

		operations = ShareItem.Role.EDITOR.getRightSet().getRights();
		rightSet = new WorkAreaOperation.RightSet( operations.toArray( new WorkAreaOperation[ operations.size() ] ) );

		return rightSet;
	}

	/*
	 * Return the RightSet that corresponds to the "Viewer" rights
	 */
	public static WorkAreaOperation.RightSet getViewerRightSet()
	{
		WorkAreaOperation.RightSet rightSet;
		List<WorkAreaOperation> operations;

		operations = ShareItem.Role.VIEWER.getRightSet().getRights();
		rightSet = new WorkAreaOperation.RightSet( operations.toArray( new WorkAreaOperation[ operations.size() ] ) );

		return rightSet;
	}

	/*
	 * Compare the 2 RightSet objects to see if they have the same or
	 * greater rights.
	 */
	private static boolean areRightSetsGreaterOrEqual(WorkAreaOperation.RightSet rightSet1, WorkAreaOperation.RightSet rightSet2 )
	{
		if ( rightSet1 == null || rightSet2 == null )
		{
			m_logger.error( "In GwtShareHelper.areRightSetsGreaterOrEqual(), one of the RightSet parameters is null" );
			return false;
		}

		return rightSet1.greaterOrEqual( rightSet2 );
	}
}
