/**
 * Copyright (c) 1998-2009 Novell, Inc. and its licensors. All rights reserved.
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
 * Attribution Copyright Notice: Copyright (c) 1998-2009 Novell, Inc. All Rights Reserved.
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
package org.kablink.teaming.remoting.ws.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
public class FolderEntry extends Entry implements Serializable {

	private String docNumber;
	private int docLevel;
	private String href;
	private List<Workflow> workflows = new ArrayList();
	protected Boolean preDeleted;
	protected Long preDeletedWhen;
	protected Long preDeletedBy;
	protected Long reservedBy;
	public int getDocLevel() {
		return docLevel;
	}

	public void setDocLevel(int docLevel) {
		this.docLevel = docLevel;
	}

	public String getDocNumber() {
		return docNumber;
	}

	public void setDocNumber(String docNumber) {
		this.docNumber = docNumber;
	}

	public String getHref() {
		return href;
	}

	public void setHref(String href) {
		this.href = href;
	}
	public Workflow[] getWorkflows() {
		Workflow[] array = new Workflow[workflows.size()];
		return workflows.toArray(array);		
	}
	public void setWorkflows(Workflow[] workflows) {
		if (workflows != null)
			this.workflows = Arrays.asList(workflows); 
		else
			this.workflows = new ArrayList<Workflow>();
	}

    public Boolean isPreDeleted() {
    	return ((null != preDeleted) && preDeleted);
    }
    public void setPreDeleted(Boolean preDeleted) {
    	this.preDeleted = preDeleted;
    }
    
	public Long getPreDeletedWhen() {
		return preDeletedWhen;
	}
	public void setPreDeletedWhen(Long preDeletedWhen) {
		this.preDeletedWhen = preDeletedWhen;
	}
	
	public Long getPreDeletedBy() {
		return preDeletedBy;
	}
	public void setPreDeletedBy(Long preDeletedBy) {
		this.preDeletedBy = preDeletedBy;
	}

	public Long getReservedBy() {
		return reservedBy;
	}

	public void setReservedBy(Long reservedBy) {
		this.reservedBy = reservedBy;
	}
}
