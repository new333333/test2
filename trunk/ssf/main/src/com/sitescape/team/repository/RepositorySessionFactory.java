/**
 * The contents of this file are subject to the Common Public Attribution License Version 1.0 (the "CPAL");
 * you may not use this file except in compliance with the CPAL. You may obtain a copy of the CPAL at
 * http://www.opensource.org/licenses/cpal_1.0. The CPAL is based on the Mozilla Public License Version 1.1
 * but Sections 14 and 15 have been added to cover use of software over a computer network and provide for
 * limited attribution for the Original Developer. In addition, Exhibit A has been modified to be
 * consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT WARRANTY OF ANY KIND,
 * either express or implied. See the CPAL for the specific language governing rights and limitations
 * under the CPAL.
 * 
 * The Original Code is ICEcore. The Original Developer is SiteScape, Inc. All portions of the code
 * written by SiteScape, Inc. are Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * 
 * 
 * Attribution Information
 * Attribution Copyright Notice: Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by ICEcore]
 * Attribution URL: [www.icecore.com]
 * Graphic Image as provided in the Covered Code [web/docroot/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are defined in the CPAL as a
 * work which combines Covered Code or portions thereof with code not governed by the terms of the CPAL.
 * 
 * 
 * SITESCAPE and the SiteScape logo are registered trademarks and ICEcore and the ICEcore logos
 * are trademarks of SiteScape, Inc.
 */
package com.sitescape.team.repository;

import com.sitescape.team.UncheckedIOException;
import com.sitescape.team.repository.archive.ArchiveStore;

public interface RepositorySessionFactory {

	public void initialize() throws RepositoryServiceException, UncheckedIOException;
	
	/**
	 * Shuts down the factory. 
	 * <p>
	 * Possible errors are logged rather than thrown as exceptions as there is
	 * little that a client application could do in such a case. 
	 *
	 */
	public void shutdown();
	
	/**
	 * Returns whether or not the repository supports versioning.
	 * 
	 * @return
	 */
	public boolean supportVersioning();
	
	/**
	 * Returns whether the repository allows users to delete individual
	 * versions of a resource without deleting the entire resource. In other
	 * words, for repository system that does not support this, the only way
	 * to remove a particular resource is to delete it in its entirety which
	 * deletes all of its versions as well. For repository that does not
	 * support versioning, this method return <code>false</code>.
	 * 
	 * @return
	 */
	public boolean isVersionDeletionAllowed();
	
	/**
	 * Returns whether the repository supports smart checkin or not.
	 * Smart checkin means that, when checkin() is invoked, it will create
	 * a new version only if there has been any modification to the file
	 * since the last checkout. If the file has not been checked out or
	 * it has but there has been no modifications to it, then it will simply
	 * return the name of the latest existing version. 
	 * If smart checkin is not supported, the repository is not capable of
	 * keeping track of such state. Consequently, there is a danger that
	 * each invocation of checkin() may create a new version even when it
	 * is not necessary or desirable. In such case, it becomes the client's
	 * responsibility/burden to keep track of such state information and 
	 * decide whether it must call checkin() or not based on that information. 
	 * @return
	 */
	public boolean supportSmartCheckin();
	
	public ArchiveStore getArchiveStore();
}
