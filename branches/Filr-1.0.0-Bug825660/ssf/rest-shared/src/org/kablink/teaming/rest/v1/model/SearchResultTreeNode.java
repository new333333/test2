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
package org.kablink.teaming.rest.v1.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

/**
 * User: david
 * Date: 5/24/12
 * Time: 11:06 AM
 */
@XmlRootElement(name = "node")
public class SearchResultTreeNode<T> {
    private T item;
    private List<SearchResultTreeNode<T>> children;

    public SearchResultTreeNode() {
    }

    public SearchResultTreeNode(T item) {
        this.item = item;
    }

    protected SearchResultTreeNode(SearchResultTreeNode<T> node) {
        copyItems(node);
    }

    @XmlElementWrapper(name = "children")
    @XmlElement(name = "node")
    public List<SearchResultTreeNode<T>> getChildren() {
        return children;
    }

    public void setChildren(List<SearchResultTreeNode<T>> children) {
        this.children = children;
    }

    public SearchResultTreeNode<T> addChild(T child) {
        return addChild(new SearchResultTreeNode<T>(child));
    }

    public SearchResultTreeNode<T> addChild(SearchResultTreeNode<T> child) {
        if (children==null) {
            children = new ArrayList<SearchResultTreeNode<T>>();
        }
        children.add(child);
        return child;
    }

    @XmlElement(name="item")
    public T getItem() {
        return item;
    }

    public void setItem(T item) {
        this.item = item;
    }

    protected void copyItems(SearchResultTreeNode<T> orig) {
        if (orig.item instanceof SearchableObject) {
            try {
                this.item = (T) ((SearchableObject)orig.item).clone();
            } catch (CloneNotSupportedException e) {
                //Ignore
            }
        }
        List<SearchResultTreeNode<T>> copiedChildren = null;
        if (orig.children!=null) {
            copiedChildren = new ArrayList<SearchResultTreeNode<T>>(orig.children.size());
            for (SearchResultTreeNode<T> child : orig.children) {
                copiedChildren.add(new SearchResultTreeNode<T>(child));
            }
        }
        this.children = copiedChildren;
    }
}
