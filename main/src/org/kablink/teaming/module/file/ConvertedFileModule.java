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
package org.kablink.teaming.module.file;

import java.io.OutputStream;

import org.kablink.teaming.UncheckedIOException;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.DefinableEntity;
import org.kablink.teaming.domain.FileAttachment;
import org.kablink.teaming.repository.RepositoryServiceException;

public interface ConvertedFileModule {

    /**
     * Reads the specified scaled file into the output stream.
     * 
     * @param fa
     * @param binder
     * @param entity
     * @param out
	 * @throws UncheckedIOException
	 * @throws RepositoryServiceException
     */
	public void readScaledFile(Binder binder, DefinableEntity entity, 
			FileAttachment fa, OutputStream out) throws  
			UncheckedIOException, RepositoryServiceException;
	    
    /**
     * Reads the specified scaled file into the output stream.
     * If the thumbnail was originally stored as "directly accessible" file
     * in a directory visible to the web client without requiring any access
     * control or assistance from the server-side service, the caller must not 
     * use this method (In other words, it is the caller's responsibility to 
     * keep track of whether a thumbnail file is directly accessible or not. 
     * The file module does not maintain that information.).
     * 
     * @param fa
     * @param binder
     * @param entity
     * @param out
     */
	public void readThumbnailFile(
			Binder binder, DefinableEntity entity, FileAttachment fa, OutputStream out) 
		throws UncheckedIOException, RepositoryServiceException;
	
	public void readCacheHtmlFile(
			String url, Binder binder, DefinableEntity entity, FileAttachment fa, OutputStream out) 
		throws UncheckedIOException, RepositoryServiceException;
	
	public void readCacheImageReferenceFile(
			Binder binder, DefinableEntity entity, FileAttachment fa, OutputStream out, String imageFileName) 
		throws UncheckedIOException, RepositoryServiceException;
	
	public void readCacheUrlReferenceFile(
			Binder binder, DefinableEntity entity, FileAttachment fa, OutputStream out, String urlFileName) 
		throws UncheckedIOException, RepositoryServiceException;

}
