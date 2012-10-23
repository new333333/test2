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

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 * User: david
 * Date: 5/17/12
 * Time: 3:54 PM
 */
@XmlRootElement(name = "binder_brief")
public class BinderBrief extends DefinableEntityBrief {
   	private Boolean library;
   	private String path;
   	private Boolean mirrored;
   	private Boolean homeDir;

    public BinderBrief() {
        setDocType("binder");
    }

    protected BinderBrief(BinderBrief orig) {
        super(orig);
    }

    public Boolean getLibrary() {
        return library;
    }

    public void setLibrary(Boolean library) {
        this.library = library;
    }

    public Boolean getMirrored() {
        return mirrored;
    }

    public void setMirrored(Boolean mirrored) {
        this.mirrored = mirrored;
    }

    public Boolean getHomeDir() {
		return homeDir;
	}

	public void setHomeDir(Boolean homeDir) {
		this.homeDir = homeDir;
	}

	public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @XmlTransient
    public boolean isFolder() {
        return "folder".equals(getEntityType());
    }

    @XmlTransient
    public boolean isWorkspace() {
        return "workspace".equals(getEntityType());
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return new BinderBrief(this);
    }
}
