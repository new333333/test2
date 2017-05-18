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
package org.kablink.teaming.gwt.client.util;

import com.google.gwt.user.client.rpc.GwtTransient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by david on 4/13/16.
 */
public class BinderViewHtmlContainer extends BinderViewContainer implements BinderViewHtml {
    private String tag;
    private Map<String, String> attributes;
    private String text;
    @GwtTransient
    private BinderViewHtmlContainer childInsertionPoint;
    @GwtTransient
    private String htmlTop;
    @GwtTransient
    private String htmlBottom;
    @GwtTransient
    private BinderViewHtmlContainer parent;

    public BinderViewHtmlContainer() {
    }

    public Map<String, String> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, String> attributes) {
        this.attributes = attributes;
    }

    @Override
    public void setAttribute(String key, String value) {
        if (attributes==null) {
            attributes = new HashMap<String, String>();
        }
        attributes.put(key, value);
    }

    public String getTag() {
        return tag;
    }

    @Override
    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public void addChild(BinderViewDefBase child) {
        if (childInsertionPoint!=null && childInsertionPoint!=this) {
            childInsertionPoint.addChild(child);
        } else {
            super.addChild(child);
        }
    }

    @Override
    public void setChildren(List<BinderViewDefBase> children) {
        if (childInsertionPoint!=null && childInsertionPoint!=this) {
            childInsertionPoint.setChildren(children);
        } else {
            super.setChildren(children);
        }
    }

    public BinderViewHtmlContainer getChildInsertionPoint() {
        return childInsertionPoint;
    }

    public void setChildInsertionPoint(BinderViewHtmlContainer childInsertionPoint) {
        this.childInsertionPoint = childInsertionPoint;
    }

    public String getHtmlTop() {
        if (childInsertionPoint!=null && childInsertionPoint!=this) {
            return childInsertionPoint.getHtmlTop();
        } else {
            return htmlTop;
        }
    }

    public void setHtmlTop(String htmlTop) {
        if (childInsertionPoint!=null && childInsertionPoint!=this) {
            childInsertionPoint.setHtmlTop(htmlTop);
        } else {
            this.htmlTop = htmlTop;
        }
    }

    public String getHtmlBottom() {
        if (childInsertionPoint!=null && childInsertionPoint!=this) {
            return childInsertionPoint.getHtmlBottom();
        } else {
            return htmlBottom;
        }
    }

    public void setHtmlBottom(String htmlBottom) {
        if (childInsertionPoint!=null && childInsertionPoint!=this) {
            childInsertionPoint.setHtmlBottom(htmlBottom);
        } else {
            this.htmlBottom = htmlBottom;
        }
    }

    public void setParent(BinderViewHtmlContainer parent) {
        this.parent = parent;
    }
}
