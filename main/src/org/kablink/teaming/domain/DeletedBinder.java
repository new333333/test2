/**
 * Copyright (c) 1998-2014 Novell, Inc. and its licensors. All rights reserved.
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
package org.kablink.teaming.domain;

import java.io.Serializable;
import java.util.Date;

import org.kablink.teaming.util.SPropsUtil;

/**
 * @author Jong
 *
 */
public class DeletedBinder extends ZonedObject implements Serializable {

	private static final long serialVersionUID = 1L;

	private static final int DELETED_BINDER_PATH_MAX_SIZE_DEFAULT = 512;

	private long binderId; // ID of the binder deleted
	private short binderType; // Type of the binder deleted
	private String binderPath; // Path of the binder deleted
	private Date deletedDate; // Date of the deletion
	
	// For Hibernate
	protected DeletedBinder() {
	}
	
	// For application
	public DeletedBinder(EntityIdentifier.EntityType binderType, Long binderId,
			Date deletedDate, String binderPath, Long zoneId) {
		this(binderType, binderId, zoneId);
		this.deletedDate = deletedDate;
		setBinderPath(binderPath);
	}
	
	public DeletedBinder(Binder binder) {
		this(binder.getEntityType(), binder.getId(),
				computeDeletedDate(binder),
				binder.getPathName(),
				binder.getZoneId());
	}
	
	private DeletedBinder(EntityIdentifier.EntityType binderType, Long binderId, Long zoneId) {
		if(binderType == null)
			throw new IllegalArgumentException("Binder type must be specified");
		if(binderId == null)
			throw new IllegalArgumentException("Binder ID must be specified");
		this.binderType = (short) binderType.getValue();
		this.binderId = binderId;
		this.zoneId = zoneId;
	}
	
	private static Date computeDeletedDate(Binder binder) {
		// When we delete a binder, it first creates a history. Use the same timestamp value.
		// (see deleteBinder_preDelete() method in AbstractBinderProcessor.
		if(binder.getModificationDate() != null)
			return binder.getModificationDate();
		else
			return new Date();
	}
	
	public EntityIdentifier.EntityType getBinderType() {
		return EntityIdentifier.EntityType.valueOf(binderType);
	}
	
	public Long getBinderId() {
		return binderId;
	}
	
	public Date getDeletedDate() {
		return deletedDate;
	}
	
	public String getBinderPath() {
		return binderPath;
	}
	
	private void setBinderPath(String binderPath) {
		int max = SPropsUtil.getInt("deleted.binder.path.max.size", DELETED_BINDER_PATH_MAX_SIZE_DEFAULT);
		if(binderPath != null && binderPath.length() > max) {
			binderPath = "..." + binderPath.substring(binderPath.length()-max+3);
		}		
		this.binderPath = binderPath;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (obj == this) return true;
		if (obj instanceof DeletedBinder) {
			DeletedBinder pk = (DeletedBinder) obj;
			// Binder ID is the primary key
			if(pk.binderId == this.binderId)
				return true;
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		// Binder ID is the primary key
		return Long.valueOf(binderId).hashCode();
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{")
		.append("binderId=")
		.append(binderId)
		.append(", binderType=")
		.append(binderType)
		.append(", binderPath=")
		.append(binderPath)
		.append(", deletedDate=")
		.append(deletedDate)
		.append("}");
		return sb.toString();
	}
	
}
