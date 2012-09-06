/**
 * Copyright (c) 1998-2012 Novell, Inc. and its licensors. All rights reserved.
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
 * Attribution Copyright Notice: Copyright (c) 1998-2012 Novell, Inc. All Rights Reserved.
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
package org.kablink.teaming.remoting.rest.v1.resource;

import com.sun.jersey.spi.resource.Singleton;
import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.domain.*;
import org.kablink.teaming.remoting.rest.v1.util.LinkUriUtil;
import org.kablink.teaming.remoting.rest.v1.util.ResourceUtil;
import org.kablink.teaming.rest.v1.model.BinderBrief;
import org.kablink.teaming.rest.v1.model.SearchResultList;
import org.kablink.teaming.rest.v1.model.TeamBrief;
import org.kablink.teaming.rest.v1.model.User;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * User: david
 * Date: 5/16/12
 * Time: 4:04 PM
 */
@Path("/self")
@Singleton
@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
public class SelfResource extends AbstractResource {

    /**
     * Gets the User object representing the authenticated user.
     * @param includeAttachments    Configures whether attachments should be included in the returned User object.
     * @return  Returns the authenticated User object
     */
    @GET
   	@Produces( { MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public User getSelf(@QueryParam("include_attachments") @DefaultValue("true") boolean includeAttachments,
                        @QueryParam("text_descriptions") @DefaultValue("false") boolean textDescriptions) {
        Long userId = getLoggedInUserId();
        // Retrieve the raw entry.
        Principal entry = getProfileModule().getEntry(userId);

        if(!(entry instanceof org.kablink.teaming.domain.User))
            throw new IllegalArgumentException(userId + " does not represent an user. It is " + entry.getClass().getSimpleName());

        User user = ResourceUtil.buildUser((org.kablink.teaming.domain.User) entry, includeAttachments, textDescriptions);
        user.setLink("/self");
        user.addAdditionalLink("roots", "/self/roots");
        if (user.getWorkspace()!=null) {
            user.addAdditionalLink("my_file_folders", user.getWorkspace().getLink() + "/library_folders");
        }
        user.addAdditionalLink("net_folders", "/self/net_folders");
        user.addAdditionalLink("shared_with_me", "/self/shared_with_me");
        user.addAdditionalLink("shared_by_me", "/self/shared_by_me");
        return user;
    }

    /**
     * Returns the authenticated user's favorite binders
     * @return Returns a list of BinderBrief objects.
     */
    @GET
    @Path("/favorites")
   	@Produces( { MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public SearchResultList<BinderBrief> getFavorites() {
        Long userId = getLoggedInUserId();
        List<Binder> binders = getProfileModule().getUserFavorites(userId);
        SearchResultList<BinderBrief> results = new SearchResultList<BinderBrief>();
        for (Binder binder : binders) {
            results.append(ResourceUtil.buildBinderBrief(binder));
        }
        return results;
    }

    /**
     * Returns the teams that the authenticated user is a member of.
     * @return Returns a list of BinderBrief objects.
     */
    @GET
    @Path("/teams")
   	@Produces( { MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public SearchResultList<TeamBrief> getTeams() {
        Long userId = getLoggedInUserId();
        List<TeamInfo> binders = getProfileModule().getUserTeams(userId);
        SearchResultList<TeamBrief> results = new SearchResultList<TeamBrief>();
        for (TeamInfo binder : binders) {
            results.append(ResourceUtil.buildTeamBrief(binder));
        }
        return results;
    }

    /**
     * Returns a list of virtual workspace roots for the authenticated user.  This is useful for displaying
     * starting points for browsing different parts of the workspace hierarchy.
     * @deprecated  This operation is temporary and is very likely to change.
     * @return Returns a list of BinderBrief objects.
     */
    @GET
    @Path("/roots")
   	@Produces( { MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public SearchResultList<BinderBrief> getRoots() {
        SearchResultList<BinderBrief> results = new SearchResultList<BinderBrief>();
        results.appendAll(new BinderBrief[] {
                getFakeMyWorkspace(), getFakeMyTeams(), getFakeMyFavorites(),
                ResourceUtil.buildBinderBrief(getBinderModule().getBinder(getWorkspaceModule().getTopWorkspaceId()))
        });
        return results;
    }

    @GET
    @Path("/net_folders")
   	@Produces( { MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public BinderBrief getNetFolders() {
        return getFakeNetFolders();
    }

    @GET
    @Path("/shared_with_me")
   	@Produces( { MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public BinderBrief getSharedWithMe() {
        return getFakeSharedWithMe();
    }

    @GET
    @Path("/shared_by_me")
   	@Produces( { MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public BinderBrief getSharedByMe() {
        return getFakeSharedByMe();
    }

    private BinderBrief getFakeMyWorkspace() {
        org.kablink.teaming.domain.User loggedInUser = RequestContextHolder.getRequestContext().getUser();
        Binder myWorkspace = getBinderModule().getBinder(loggedInUser.getWorkspaceId());
        BinderBrief binder = ResourceUtil.buildBinderBrief(myWorkspace);
        //TODO: localize
        binder.setTitle("My Workspace");
        return binder;
    }

    private BinderBrief getFakeMyTeams() {
        BinderBrief binder = new BinderBrief();
        //TODO: localize
        binder.setTitle("My Teams");
        binder.setIcon(LinkUriUtil.buildIconLinkUri("/icons/workspace_team.png"));
        binder.addAdditionalLink("child_binders", "/self/teams");
        return binder;
    }

    private BinderBrief getFakeMyFavorites() {
        BinderBrief binder = new BinderBrief();
        //TODO: localize
        binder.setTitle("My Favorites");
        binder.setIcon(LinkUriUtil.buildIconLinkUri("/icons/workspace_star.png"));
        binder.addAdditionalLink("child_binders", "/self/favorites");
        return binder;
    }

    private BinderBrief getFakeSharedWithMe() {
        BinderBrief binder = new BinderBrief();
        //TODO: localize
        binder.setTitle("Shared with Me");
        binder.setIcon(LinkUriUtil.buildIconLinkUri("/icons/workspace.png"));
        Long userId = getLoggedInUserId();
        String baseUri = "/shares/with_user/" + userId;
        binder.addAdditionalLink("child_binders", baseUri + "/binders");
        binder.addAdditionalLink("child_binder_tree", baseUri + "/binder_tree");
        binder.addAdditionalLink("child_entries", baseUri + "/entries");
        binder.addAdditionalLink("child_files", baseUri + "/files");
        binder.addAdditionalLink("child_library_entities", baseUri + "/library_entities");
        binder.addAdditionalLink("child_library_files", baseUri + "/library_files");
        binder.addAdditionalLink("child_library_folders", baseUri + "/library_folders");
        binder.addAdditionalLink("child_library_tree", baseUri + "/library_tree");
        return binder;
    }

    private BinderBrief getFakeSharedByMe() {
        BinderBrief binder = new BinderBrief();
        //TODO: localize
        binder.setTitle("Shared by Me");
        binder.setIcon(LinkUriUtil.buildIconLinkUri("/icons/workspace.png"));
        Long userId = getLoggedInUserId();
        String baseUri = "/shares/by_user/" + userId;
        binder.addAdditionalLink("child_binders", baseUri + "/binders");
//        binder.addAdditionalLink("child_binder_tree", baseUri + "/binder_tree");
        binder.addAdditionalLink("child_entries", baseUri + "/entries");
        binder.addAdditionalLink("child_files", baseUri + "/files");
        binder.addAdditionalLink("child_library_files", baseUri + "/library_files");
        binder.addAdditionalLink("child_library_folders", baseUri + "/library_folders");
//        binder.addAdditionalLink("child_library_tree", baseUri + "/library_tree");
        return binder;
    }

    private BinderBrief getFakeNetFolders() {
        BinderBrief binder = new BinderBrief();
        //TODO: localize
        binder.setTitle("Net Folders");
        binder.setIcon(LinkUriUtil.buildIconLinkUri("/icons/workspace.png"));
        Long userId = getLoggedInUserId();
        String baseUri = "/net_folders";
        binder.addAdditionalLink("child_binders", baseUri);
        //binder.addAdditionalLink("child_binder_tree", baseUri + "/binder_tree");
        //binder.addAdditionalLink("child_files", baseUri + "/files");
        binder.addAdditionalLink("child_library_entities", baseUri + "/library_entities");
        //binder.addAdditionalLink("child_library_files", baseUri + "/library_files");
        binder.addAdditionalLink("child_library_folders", baseUri);
        //binder.addAdditionalLink("child_library_tree", baseUri + "/library_tree");
        return binder;
    }
}
