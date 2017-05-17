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
package org.kablink.teaming.fi.connection.acl;

/**
 * Immutable class representing a change to a resource on the file system.
 * 
 * @author jong
 *
 */
public class ResourceChange implements Comparable<ResourceChange> {
	
	public enum ResourceChangeType {
		
		/*** Folder changes ***/
		
		/**
		 * A new folder created
		 * Since 1.1
		 */
		folder_create,
		/**
		 * A folder moved or renamed
		 */
		folder_move,
		/**
		 * A folder deleted
		 * Since 1.1
		 */
		folder_delete,
		/**
		 * ACL changed on a folder
		 * Since 1.1
		 */
		folder_acl,
		/**
		 * Owner changed on a folder
		 * Since 1.1
		 */
		folder_owner,
		
		/*** File changes ***/
		
		/**
		 * A new file created
		 * Since 1.1
		 */
		file_create,
		/**
		 * A file updated
		 * Since 1.1
		 */
		file_update,
		/**
		 * A file moved or renamed
		 */
		file_move,
		/**
		 * A file deleted
		 * Since 1.1
		 */
		file_delete,
		/**
		 * ACL changed on a file
		 */
		file_acl,
		/**
		 * Owner changed on a file
		 * Since 1.1
		 */
		file_owner,
		/**
		 * A file or folder deleted
		 * Since 1.1
		 */
		file_or_folder_delete
	}
	
	private long timestamp; // the time at which this change is detected/recorded
	
	private ResourceChangeType type; // resource change type
	
	private String path; // normalized path of the resource being affected; required field
	
	// TODO NOT IMPLEMENTED
	private String handle; // optional unique handle
	
	private String targetPath; // target path of the resource after move or rename; required only for move
	
	public ResourceChange(String path, ResourceChangeType type, long timestamp) {
		this(path, null, type, timestamp);
	}
	
	public ResourceChange(String path, String targetPath, ResourceChangeType type, long timestamp) {
		this.path = path;
		this.targetPath = targetPath;
		this.type = type;
		this.timestamp = timestamp;
	}
	
	public String getPath() {
		return path;
	}

	public String getTargetPath() {
		return targetPath;
	}

	public ResourceChangeType getType() {
		return type;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public String getHandle() {
		return handle;
	}
	
	@Override
	public int compareTo(ResourceChange anotherResourceChange) {
		if(anotherResourceChange == null)
			throw new IllegalArgumentException("Null arg is not accepted");
		// First, ascending order of timestamp
		if(timestamp < anotherResourceChange.timestamp) {
			return -1;
		}
		else if(timestamp > anotherResourceChange.timestamp) {
			return 1;
		}
		else {
			// Next, put all folder changes before file changes
			int c = type.compareTo(anotherResourceChange.type);
			if(c != 0) {
				return c;
			}
			else {
				// If the same type of operation occurs on the same resource at the same time, it must be identical event.
				return path.compareTo(anotherResourceChange.path);
			}
		}
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{timestamp=")
		.append(timestamp)
		.append(",type=")
		.append(type.name())
		.append(",path=")
		.append(path)
		.append("}");
		return sb.toString();
	}
}
