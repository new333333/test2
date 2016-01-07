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
package org.kablink.teaming.rest.v1.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import java.util.ArrayList;
import java.util.List;

/**
 * User: david
 * Date: 5/16/12
 * Time: 12:43 PM
 */
public abstract class BaseRestObject {
    private String link;
    @XmlElementWrapper(name="links")
    @XmlElement(name="link")
    private List<Link> additionalLinks;

    public BaseRestObject() {
    }

    public BaseRestObject(String link) {
        this.link = link;
    }

    protected BaseRestObject(BaseRestObject orig) {
        this.link = orig.link;
        this.additionalLinks = orig.additionalLinks;
    }

    public String getLink() {
        return link;
    }

    @XmlElement(name="href")
    public void setLink(String link) {
        this.link = link;
    }

    public List<Link> getAdditionalLinks() {
        return additionalLinks;
    }

    public void addAdditionalLink(String relation, String uri) {
        addAdditionalLink(new Link(relation, uri));
    }

    public void addAdditionalLink(String uri) {
        addAdditionalLink(new Link(null, uri));
    }

    public void addAdditionalLink(Link link) {
        if (additionalLinks ==null) {
            additionalLinks = new ArrayList<Link>();
        }
        additionalLinks.add(link);
    }

    public String findRelatedLink(String relation) {
        if (additionalLinks!=null) {
            for (Link link : additionalLinks) {
                if (link.getRel().equals(relation)) {
                    return link.getHref();
                }
            }
        }
        return null;
    }
}
