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
package org.kablink.teaming.gwt.client.util;

import org.kablink.teaming.gwt.client.binderviews.PersonalWorkspaceView;
import org.kablink.teaming.gwt.client.binderviews.PhotoAlbumFolderView;
import org.kablink.teaming.gwt.client.binderviews.ViewReady;
import org.kablink.teaming.gwt.client.binderviews.WikiFolderView;
import org.kablink.teaming.gwt.client.event.*;
import org.kablink.teaming.gwt.client.widgets.VibeEntityViewPanel;

/**
 * Created by david on 4/19/16.
 */
public class GwtClientFolderViewHelper {

    public static BinderViewLayoutData buildBinderLayoutData(ViewInfo vi) {
        return new BinderViewLayoutData(vi, buildGwtBinderLayoutEvent(vi.getBinderInfo()));
    }

    public static ShowBinderEvent buildGwtBinderLayoutEvent(BinderInfo bi) {
        ShowBinderEvent event = null;
        // What type of binder is it?
        BinderType bt = bi.getBinderType();
        switch ( bt )
        {
            case COLLECTION:
                event = new ShowCollectionViewEvent(bi);
                break;
            case FOLDER:
                // What type of folder is it?
                FolderType ft = bi.getFolderType();
                switch ( ft )
                {
                    case CALENDAR:
                        event = new ShowCalendarFolderEvent(bi);
                        break;
                    case BLOG:
                        event = new ShowBlogFolderEvent(bi);
                        break;
                    case DISCUSSION:
                        event = new ShowDiscussionFolderEvent(bi);
                        break;
                    case FILE:
                        event = new ShowFileFolderEvent(bi);
                        break;
                    case GUESTBOOK:
                        event = new ShowGuestbookFolderEvent(bi);
                        break;
                    case MILESTONE:
                        event = new ShowMilestoneFolderEvent(bi);
                        break;
                    case MINIBLOG:
                        event = new ShowMicroBlogFolderEvent(bi);
                        break;
                    case MIRROREDFILE:
                        event = new ShowMirroredFileFolderEvent(bi);
                        break;
                    case SURVEY:
                        event = new ShowSurveyFolderEvent(bi);
                        break;
                    case TASK:
                        event = new ShowTaskFolderEvent(bi);
                        break;
                    case TRASH:
                        event = new ShowTrashEvent(bi);
                        break;
                    case PHOTOALBUM:
                        boolean showGwtPA = PhotoAlbumFolderView.SHOW_GWT_PHOTO_ALBUM;	//! DRF (20150318)
                        if (showGwtPA) {
                            event = new ShowPhotoAlbumFolderEvent(bi);
                        }
                        break;
                    case WIKI:
                        boolean showGwtWiki = WikiFolderView.SHOW_GWT_WIKI;	//! DRF (20150326)
                        if (showGwtWiki) {
                            event = new ShowWikiFolderEvent(bi);
                        }
                        break;

                    default:
                        // Something we don't know how to handle!
                        GwtClientHelper.debugAlert( "ContentControl.setViewNow( Unhandled FolderType:  " + ft.name() + " )" );
                        break;
                }
                break;

            case WORKSPACE:
                // What type of workspace is it?
                WorkspaceType wt = bi.getWorkspaceType();
                switch ( wt )
                {
                    case LANDING_PAGE:
                        // Fire the event that will display the landing page.
                        event = new ShowLandingPageEvent(bi);
                        break;
                    case DISCUSSIONS:
                        // Fire the event that will display the Discussion workspace.
                        event = new ShowDiscussionWSEvent(bi);
                        break;
                    case TEAM:
                        // Fire the event that will display the Team workspace.
                        event = new ShowTeamWSEvent(bi);
                        break;
                    case WORKSPACE:
                        // Fire the event that will display the generic workspace.
                        event = new ShowGenericWSEvent(bi);
                        break;
                    case TRASH:
                        event = new ShowTrashEvent(bi);
                        break;
                    case GLOBAL_ROOT:
                        // Fire the event that will display the Global workspace.
                        event = new ShowGlobalWSEvent(bi);
                        break;
                    case TEAM_ROOT:
                        // Fire the event that will display the Team root workspace.
                        event = new ShowTeamRootWSEvent(bi);
                        break;
                    case TOP:
                        // Fire the event that will display the home (top) workspace.
                        event = new ShowHomeWSEvent(bi);
                        break;
                    case PROJECT_MANAGEMENT:
                        // Fire the event that will display the project management workspace.
                        event = new ShowProjectManagementWSEvent(bi);
                        break;
                    case PROFILE_ROOT:
                    case PROFILE_ROOT_MANAGEMENT:
                        // Fire the event that will display the profile root workspace.
                        event = new ShowPersonalWorkspacesEvent(bi);
                        break;
                    case NET_FOLDERS_ROOT:
                        // Fire the event that will display the
                        // root Net Folders workspace.
                        event = new ShowNetFoldersWSEvent(bi);
                        break;
                    case USER:
                        boolean showGwtPWS = PersonalWorkspaceView.SHOW_GWT_PERSONAL_WORKSPACE;	//! DRF (20150318)
                        if (showGwtPWS) {
                            // Fire the event that will display the
                            // Personal Workspace view.
                            event = new ShowPersonalWorkspaceEvent(bi);
                        }
                        break;
                    default:
                        // Something we don't know how to handle!
                        GwtClientHelper.debugAlert( "ContentControl.setViewNow( Unhandled WorkspaceType:  " + wt.name() + " )" );
                        break;
                }
                break;
        }
        return event;
    }


}
