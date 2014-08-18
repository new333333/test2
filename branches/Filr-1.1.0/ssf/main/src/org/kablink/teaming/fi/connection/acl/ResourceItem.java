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
 * This class represents core metadata associated with a file or directory
 * as retrieved from the file system.
 */
public class ResourceItem {

	/* The following set of properties come from the file system and are used to convey metadata to Filr side */
	
	private String parentPath;
	private String name;
	private String handle;
	// This field is relevant only for files. The value is 0 for folders.
	private long lastModified;
	private boolean directory;
	// This field is relevant only for files. The value is 0 for folders.
	private long contentLength;
	
	// This optional field is relevant only for folders, and should be filled only when the client 
	// explicitly asks for it. The value is null for all files indicating that this information is unknown.
	private Boolean aclInherited = null;
	
	// These fields are optional, and should be filled only when the client explicitly asks for it.
	private String ownerId;
	private String ownerIdType;
	
	/* The following set of properties are computed or managed on the Filr side and used solely to help with sync process without having to resort to another data structure */
	
	private Long creatorFilrId;
	private Long ownerFilrId;
	
	public static ResourceItem file(String parentPath, String name, long lastModified, long contentLength) {
		return new ResourceItem(parentPath, name, lastModified, false, contentLength);
	}
	
	public static ResourceItem file(String parentPath, String name, long lastModified, long contentLength, Boolean aclInherited, String ownerId, String ownerIdType) {
		return new ResourceItem(parentPath, name, lastModified, false, contentLength, aclInherited, ownerId, ownerIdType);
	}
	
	public static ResourceItem file(String filePath, long lastModified, long contentLength) {
		return new ResourceItem(makeParentPath(filePath), makeName(filePath), lastModified, false, contentLength);
	}
	
	public static ResourceItem file(String filePath, long lastModified, long contentLength, Boolean aclInherited, String ownerId, String ownerIdType) {
		return new ResourceItem(makeParentPath(filePath), makeName(filePath), lastModified, false, contentLength, aclInherited, ownerId, ownerIdType);
	}
	
	public static ResourceItem directory(String parentPath, String name) {
		return new ResourceItem(parentPath, name, 0, true, 0);
	}
	
	public static ResourceItem directory(String parentPath, String name, Boolean aclInherited, String ownerId, String ownerIdType) {
		return new ResourceItem(parentPath, name, 0, true, 0, aclInherited, ownerId, ownerIdType);
	}
	
	public static ResourceItem directory(String folderPath) {
		return new ResourceItem(makeParentPath(folderPath), makeName(folderPath), 0, true, 0);
	}
	
	public static ResourceItem directory(String folderPath, Boolean aclInherited, String ownerId, String ownerIdType) {
		return new ResourceItem(makeParentPath(folderPath), makeName(folderPath), 0, true, 0, aclInherited, ownerId, ownerIdType);
	}
	
	private ResourceItem(String parentPath, String name, long lastModified, boolean directory, long contentLength, Boolean aclInherited, String ownerId, String ownerIdType) {
		this(parentPath, name, lastModified, directory, contentLength);
		this.aclInherited = aclInherited;
		this.ownerId = ownerId;
		this.ownerIdType = ownerIdType;
	}
	
	private ResourceItem(String parentPath, String name, long lastModified, boolean directory, long contentLength) {
		this.parentPath = parentPath;
		this.name = name;
		this.lastModified = lastModified;
		this.directory = directory;
		this.contentLength = contentLength;
	}
	
	/**
	 * Returns the full path of the parent.
	 * 
	 * @return
	 */
	public String getParentPath() {
		return parentPath;
	}
	public void setParentPath(String parentPath) {
		this.parentPath = parentPath;
	}

	/**
	 * Returns the name of the file or directory.
	 */
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public String getHandle() {
		return handle;
	}

	public void setHandle(String handle) {
		this.handle = handle;
	}

	/**
	 * Returns last modified time of the file, or <code>0</code> if directory.
	 * 
	 * @return
	 */
	public long getLastModified() {
		return lastModified;
	}
	public void setLastModified(long lastModified) {
		this.lastModified = lastModified;
	}
	
	/** 
	 * Return last modified time in milliseconds, but adjusted to second precision.
	 * This method is necessary because our database stores time values only up
	 * to second precision. If we don't consistently use this method, we can end
	 * up with a situation where endless attempts are made to update/synchronize 
	 * the same file unnecessarily even when there's been no changes since the
	 * last synchronization.
	 * 
	 * @return
	 */
	public long getLastModifiedSecondAdjusted() {
		return (lastModified / 1000) * 1000;
	}
	
	/**
	 * Returns <code>true</code> if directory, <code>false</code> if file.
	 * 
	 * @return
	 */
	public boolean isDirectory() {
		return directory;
	}
	public void setDirectory(boolean directory) {
		this.directory = directory;
	}
	
	/**
	 * Returns content length (in byte) of the file, or <code>0</code> if directory.
	 * 
	 * @return
	 */
	public long getContentLength() {
		return contentLength;
	}
	public void setContentLength(long contentLength) {
		this.contentLength = contentLength;
	}
	
	/**
	 * Returns whether this file or directory inherits its ACL from the parent directory or not.
	 * 
	 * @return
	 */
	public Boolean isAclInherited() {
		return aclInherited;
	}
	public Boolean getAclInherited() {
		return aclInherited;
	}
	public void setAclInherited(Boolean aclInherited) {
		this.aclInherited = aclInherited;
	}
	
	/**
	 * Returns the ID of the principal that owns the resource.
	 * 
	 * @return
	 */
	public String getOwnerId() {
		return ownerId;
	}
	public void setOwnerId(String ownerId) {
		this.ownerId = ownerId;
	}

	/**
	 * Returns the type of the owner id for the resource.
	 * 
	 * @return
	 */
	public String getOwnerIdType() {
		return ownerIdType;
	}

	public void setOwnerIdType(String ownerIdType) {
		this.ownerIdType = ownerIdType;
	}

	/**
	 * Returns the Filr principal ID that the original owner ID from the file system maps to. 
	 * 
	 * @return
	 */
	public Long getCreatorFilrId() {
		return creatorFilrId;
	}

	public void setCreatorFilrId(Long creatorFilrId) {
		this.creatorFilrId = creatorFilrId;
	}

	/**
	 * Returns optionally the Filr principal ID designated as the owner or <code>null</code>. 
	 * 
	 * @return
	 */
	public Long getOwnerFilrId() {
		return ownerFilrId;
	}

	public void setOwnerFilrId(Long ownerFilrId) {
		this.ownerFilrId = ownerFilrId;
	}

	/**
	 * Returns String representation of the object.
	 */
	@Override
	public String toString() {
		// Note: This is NOT a normalized path, but merely a string representation of the object useful
		// for display purpose only. So it must NOT be treated as a valid path to the resource.
		// A normalized path can only be obtained from the respective resource driver implementing class.		
		StringBuilder sb = new StringBuilder();
		sb.append("{")
		.append("parentPath=")
		.append(parentPath)
		.append(",name=")
		.append(name)
		.append(",handle=")
		.append(handle)
		.append(",lastModified=")
		.append(lastModified)
		.append(",directory=")
		.append(directory)
		.append(",contentLength=")
		.append(contentLength)
		.append(",aclInherited=")
		.append(aclInherited)
		.append(",ownerId=")
		.append(ownerId)
		.append(",ownerIdType=")
		.append(ownerIdType)
		.append("}");
		return sb.toString();

	}
	
	private static String makeParentPath(String path) {
		int index = path.lastIndexOf("/");
		if(index < 0)
			return "";
		else
			return path.substring(0, index);
	}
	
	private static String makeName(String path) {
		int index = path.lastIndexOf("/");
		if(index < 0)
			return path;
		else
			return path.substring(index+1);
	}
}
