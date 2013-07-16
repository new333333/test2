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
 * (c) 1998-2012 Novell, Inc. All Rights Reserved.
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
import org.kablink.teaming.domain.Definition;
import org.kablink.teaming.remoting.rest.v1.util.ResourceUtil;
import org.kablink.teaming.rest.v1.model.DefinitionBrief;
import org.kablink.teaming.rest.v1.model.SearchResultList;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * User: david
 * Date: 5/25/12
 * Time: 12:22 PM
 */
@Path("/definitions")
@Singleton
@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
public class DefinitionResource extends AbstractResource {
    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public SearchResultList<DefinitionBrief> getDefinitions() {
        List<Definition> defs = getDefinitionModule().getDefinitions(null, Boolean.TRUE);
        SearchResultList<DefinitionBrief> results = new SearchResultList<DefinitionBrief>();
        for (Definition def : defs) {
            results.append(ResourceUtil.buildDefinitionBrief(def));
        }
        return results;
    }

    @GET
    @Path("{id}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public DefinitionBrief getDefinitions(@PathParam("id") String id) {
        Definition def = getDefinitionModule().getDefinition(id);
        return ResourceUtil.buildDefinitionBrief(def);
    }
}
