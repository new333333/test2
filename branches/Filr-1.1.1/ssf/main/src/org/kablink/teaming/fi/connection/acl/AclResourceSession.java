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

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;

import org.kablink.teaming.UncheckedIOException;
import org.kablink.teaming.fi.FIException;
import org.kablink.teaming.fi.connection.ResourceSession;

/**
 * A resource session interface that supports access control list (ACL).
 * 
 * @author jong
 *
 */
public interface AclResourceSession extends ResourceSession {

	/**
	 * Reads the access control list. 
	 * <p>
	 * If there is no ACL item, it must return an empty set, not <code>null</code>.
	 * 
	 * @return a set of items representing the ACL. 
	 */
	public Set<AclItem> getAcl();

	/**
	 * Return the ACL resource driver that allocated this session. 
	 * Convenience method.
	 * 
	 * @return
	 */
	public AclResourceDriver getAclResourceDriver();

	/**
	 * Returns the ID of the principal that owns the resource, or <code>null</code>
	 * if no such information is available.
	 * 
	 * @return
	 */
	public String getOwnerPrincipalId();
	
	/**
	 * Returns the representation type of the principal ID for the owner of the resource.
	 * 
	 * @return
	 */
	public String getOwnerPrincipalIdType();

	/**
	 * Returns whether the resource inherits its ACL from the parent folder or not.
	 * 
	 * @return
	 */
	public boolean isAclInherited();
	
	/**
	 * Returns the children of the directory as a list of <code>ResourceItem</code> objects.
	 * This differs from <code>listNames</code> method in that all requested core meta data associated
	 * with each child is returned in a single invocation of the method for improved efficiency.
	 * The file content and the actual ACL of individual child must be retrieved separately only 
	 * when it is actually needed.
	 * <p>
	 * The method returns the following set of information for each child.<br>
	 * 		parent path (required, for both file and folder)<br>
	 * 		name of the child (required, for both file and folder)<br>
	 * 		last modified time (required for file only, irrelevant for folder)<br>
	 * 		whether the child is file or folder (required)<br>
	 * 		content length (required for file only, irrelevant for folder)<br>
	 * 		ACL inherited/equivalence flag (optional for folder only, irrelevant for file) - This should be filled in only when asked explicitly by the caller<br>
	 * 		owner ID and type (optional for file and folder) - This should be filled in only when asked explicitly by the caller<br>
	 * <p>
	 * Returns an empty list if the directory is empty.
	 *
	 * @param directoryOnly 
	 * If <code>true</code>, only returns information about folders but not files.
	 * If <code>false</code> returns information about all children including folders and files.
	 * This should help save bandwidth when the parent directory contains large number of files but few
	 * sub-folders and the caller doesn't need information about files.
	 * 
	 * @param includeAclInfoForFolder 
	 * If <code>true</code>, the result should include ACL inheritance/equivalence flag for each folder.
	 * If <code>false</code>, ACL information must not be obtained for any folder. It is important to
	 * avoid the processing cost associated with obtaining such information, unless the caller explicitly
	 * requests for it.
	 * 
	 * @param includeAclInfoForFile
	 * If <code>true</code>, the result should include ACL inheritance/equivalence flag for each file.
	 * If <code>false</code>, ACL information must not be obtained for any file. It is important to
	 * avoid the processing cost associated with obtaining such information, unless the caller explicitly
	 * requests for it.
	 * 
	 * @param includeOwnerInfo
	 * If<code>true</code>, the result should include owner ID and type information for each child.
	 * This applies to both folders and files.
	 * If<code>false</code>, owner information must not be obtained for any child.
	 * 
	 * @return
	 * @throws FIException
	 * @throws IllegalStateException If the path is not set, etc.
	 */
	public List<ResourceItem> getChildren(boolean directoryOnly, boolean includeAclInfoForFolder, boolean includeAclInfoForFile, boolean includeOwnerInfo) throws FIException, IllegalStateException;
		
	/**
	 * Return the name of the highest permission that the context owner of this session has on the file
	 * or folder pointed to by the current path. If the user has no permission on that file or folder, 
	 * it should return <code>null</code>.
	 * <p>
	 * The permission name is specific to the resource driver implementing this interface and is defined
	 * by the accompanying driver helper class implementing <code>AclItemPermissionMapper</code> interface. 
	 * 
	 * @param groupIds 
	 * @return highest permission name or <code>null</code>
	 * @throws FIException
	 * @throws UncheckedIOException
	 */
	public String getPermissionName(Map<String, List<String>> groupIds) throws FIException, UncheckedIOException;

	/**
	 * Return whether or not the resource referred to by the current path is visible to the user owning this session.
	 * A resource may be file or folder, and defined to be visible if the user should be able to see the
	 * existence of the resource, either by explicit rights granted on that resource or, in the case of folder, 
	 * by inferred access given by some other resource below that folder.
	 * 
	 * @return
	 * @throws FIException
	 * @throws UncheckedIOException
	 */
	public boolean isVisible(Map<String, List<String>> groupIds) throws FIException, UncheckedIOException;
	
	/**
	 * Return whether or not the individual resource in the specified array is visible to the user owning this session.
	 * A resource may be file or folder, and defined to be visible if the user should be able to see the
	 * existence of the resource, either by explicit rights granted on that resource or, in the case of folder, 
	 * by inferred access given by some other resource below that folder.
	 * <p>
	 * This method is functionally equivalent to calling <code>setPath</code> and <code>isVisible</code> methods
	 * in a loop specifying one resource at a time. This method should ignore the current path associated with
	 * the session.
	 * 
	 * @param resourcePaths A map of a resource path to an indication of whether the resource refers to a directory or not
	 * @return A map of resource path to an indication of whether the resource is visible or not
	 * @throws FIException
	 * @throws UncheckedIOException
	 */
	public Map<String,Boolean> areVisible(Map<String, Boolean> resourcePaths, Map<String, List<String>> groupIds) throws FIException, UncheckedIOException;
	
	/**
	 * Returns <code>ResourceItem</code> representing the resource at the current path, or <code>null</code>
	 * if no such resource is available.
	 * 
	 * IMPLEMENTATION NOTE: For best efficiency, whenever possible, the implementation of this method should
	 * obtain all pertaining pieces of information about the resource in a single trip to the data source.
	 * 
	 * @param includeAclInfo
	 * If <code>true</code>, the resource item should include ACL inheritance/equivalence flag for the resource.
	 * If <code>false</code>, ACL information must not be obtained for the resource. It is important to
	 * avoid the processing cost associated with obtaining such information, unless the caller explicitly
	 * requests for it.
	 * 
	 * @param includeOwnerInfo
	 * If<code>true</code>, the resource item should include owner ID and type information for the resource.
	 * If<code>false</code>, owner information must not be obtained for the resource.
	 * 
	 * @return
	 * @throws FIException
	 * @throws UncheckedIOException
	 */
	public ResourceItem getResource(boolean includeAclInfo, boolean includeOwnerInfo) throws FIException, UncheckedIOException;
	
	/**
	 * Returns a set of <code>ResourceChange</code> objects representing the changes occurred on the file
	 * system since the specified time at or below the folder denoted by the current path. 
	 * The result is sorted in ascending order of timestamp. If there's no change logs meeting the criteria, 
	 * this method returns an empty set. If the resource driver does not support "change since" capability,
	 * this method should return a <code>null</code>.
	 * <p>
	 * When Filr calls this method with a certain timestamp value, it also serves as acknowledging that
	 * the changes occurred BEFORE the timestamp value have been processed successfully by the Filr side.
	 * At that point, the implementation of this method, if it chooses to do so, can purge or remove
	 * the corresponding change logs occurred prior to that timestamp. The implementation must NEVER 
	 * purge those change logs corresponding to the result of the latest invocation of this method
	 * until the next call is made.
	 * 
	 * @param timestamp information about the changes occurred since this time (inclusive) should be returned
	 * @param maxResults maximum number of <code>ResourceChange</code> objects allowed in the result.
	 * IMPORTANT: If the number of changes found on the file system is greater than the specified
	 * <code>maxResults</code>, it is crucially important that the implementation must cut off the
	 * list at the specified size in a way that honors the timestamp ordering of the elements across
	 * all changes, both those that made into the list and those that did not. This is so that the
	 * next round of invocation to this method will not inadvertently miss any changes occurred
	 * on the file system.
	 * 
	 * @return
	 * @throws FIException
	 * @throws UncheckedIOException
	 */
	public SortedSet<ResourceChange> getChangesSince(long timestamp, int maxResults) throws FIException, UncheckedIOException;
}
