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
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 * Information about a binder's children.  Will contain either a list of children error information, but not both.
 */
@XmlRootElement
public class BinderChildren {
    private Long binderId;
    private ErrorInfo error;
    private SearchResultList<SearchableObject> children;

    public BinderChildren() {
    }

    public BinderChildren(Long binderId, SearchResultList<SearchableObject> children, ErrorInfo error) {
        this.binderId = binderId;
        this.children = children;
        this.error = error;
    }

    /**
     * The ID of the parent binder.
     */
    @XmlElement(name = "binder_id")
    public Long getBinderId() {
        return binderId;
    }

    public void setBinderId(Long binderId) {
        this.binderId = binderId;
    }

    /**
     * The binder's children.  A list of BinderBrief and FileProperties objects.  Only applies if no occurred retrieving the children.
     */
    @XmlElement(name = "children")
    public SearchResultList<SearchableObject> getChildren() {
        return children;
    }

    public void setChildren(SearchResultList<SearchableObject> children) {
        this.children = children;
    }

    /**
     * An error that occurred retrieving the children.
     */
    public ErrorInfo getError() {
        return error;
    }

    public void setError(ErrorInfo error) {
        this.error = error;
    }

    @XmlTransient
    public int getChildrenCount() {
        return children==null ? 0 : children.getCount();
    }
}
