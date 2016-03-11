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
package org.kablink.teaming.webdav;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kablink.teaming.domain.FileAttachment;
import org.kablink.teaming.webdav.util.WebdavUtils;

import com.bradmcevoy.http.Auth;
import com.bradmcevoy.http.GetableResource;
import com.bradmcevoy.http.LockInfo;
import com.bradmcevoy.http.LockResult;
import com.bradmcevoy.http.LockTimeout;
import com.bradmcevoy.http.LockToken;
import com.bradmcevoy.http.LockableResource;
import com.bradmcevoy.http.PropFindableResource;
import com.bradmcevoy.http.Range;
import com.bradmcevoy.http.exceptions.BadRequestException;
import com.bradmcevoy.http.exceptions.LockedException;
import com.bradmcevoy.http.exceptions.NotAuthorizedException;
import com.bradmcevoy.http.exceptions.NotFoundException;
import com.bradmcevoy.http.exceptions.PreConditionFailedException;

/**
 * @author jong
 *
 */
public class EipFileNameResource extends WebdavResource implements FileAttachmentResource, PropFindableResource, GetableResource, LockableResource {

	private static final Log logger = LogFactory.getLog(EipFileNameResource.class);
	
	private FileAttachment fa;
	
	public EipFileNameResource(WebdavResourceFactory factory, FileAttachment fa) {
		super(factory, EipResource.WEBDAV_PATH + "/" + fa.getId() + "/" + fa.getFileItem().getName(), fa.getFileItem().getName());
		this.fa = fa;
	}

	/* (non-Javadoc)
	 * @see com.bradmcevoy.http.Resource#getUniqueId()
	 */
	@Override
	public String getUniqueId() {
		return "efn:" + fa.getId();
	}

	/* (non-Javadoc)
	 * @see com.bradmcevoy.http.Resource#getModifiedDate()
	 */
	@Override
	public Date getModifiedDate() {
		return getMiltonSafeDate(fa.getModification().getDate());
	}

	/* (non-Javadoc)
	 * @see com.bradmcevoy.http.LockableResource#lock(com.bradmcevoy.http.LockTimeout, com.bradmcevoy.http.LockInfo)
	 */
	@Override
	public LockResult lock(LockTimeout timeout, LockInfo lockInfo)
			throws NotAuthorizedException, PreConditionFailedException,
			LockedException {
		return factory.getLockManager().lock(timeout, lockInfo, this);
	}

	/* (non-Javadoc)
	 * @see com.bradmcevoy.http.LockableResource#refreshLock(java.lang.String)
	 */
	@Override
	public LockResult refreshLock(String token) throws NotAuthorizedException,
			PreConditionFailedException {
		return factory.getLockManager().refresh(token, this);
	}

	/* (non-Javadoc)
	 * @see com.bradmcevoy.http.LockableResource#unlock(java.lang.String)
	 */
	@Override
	public void unlock(String tokenId) throws NotAuthorizedException,
			PreConditionFailedException {
		factory.getLockManager().unlock(tokenId, this);
	}

	/* (non-Javadoc)
	 * @see com.bradmcevoy.http.LockableResource#getCurrentLock()
	 */
	@Override
	public LockToken getCurrentLock() {
		return factory.getLockManager().getCurrentToken(this);
	}

	/* (non-Javadoc)
	 * @see com.bradmcevoy.http.GetableResource#sendContent(java.io.OutputStream, com.bradmcevoy.http.Range, java.util.Map, java.lang.String)
	 */
	@Override
	public void sendContent(OutputStream out, Range range,
			Map<String, String> params, String contentType) throws IOException,
			NotAuthorizedException, BadRequestException, NotFoundException {
		WebdavUtils.sendFileContent(out, range, fa, logger);
	}

	/* (non-Javadoc)
	 * @see com.bradmcevoy.http.GetableResource#getMaxAgeSeconds(com.bradmcevoy.http.Auth)
	 */
	@Override
	public Long getMaxAgeSeconds(Auth auth) {
		return factory.getMaxAgeSecondsEipFile();
	}

	/* (non-Javadoc)
	 * @see com.bradmcevoy.http.GetableResource#getContentType(java.lang.String)
	 */
	@Override
	public String getContentType(String accepts) {
		return WebdavUtils.getFileContentType(accepts, fa.getFileItem().getName(), logger);
	}

	/* (non-Javadoc)
	 * @see com.bradmcevoy.http.GetableResource#getContentLength()
	 */
	@Override
	public Long getContentLength() {
		// Return null to play safe. This way, we let WebDAV interaction to compute
		// the file length based on the content being transmitted as opposed to 
		// relying on the meta data we provide. This is to avoid the unlikely
		// (but possible) situation where the length information is out-of-sync with
		// the content for whatever reason (e.g. Lucene index is out-of-sync, etc.).
		return null;
	}

	/* (non-Javadoc)
	 * @see com.bradmcevoy.http.PropFindableResource#getCreateDate()
	 */
	@Override
	public Date getCreateDate() {
		return getMiltonSafeDate(fa.getCreation().getDate());
	}

	/* (non-Javadoc)
	 * @see org.kablink.teaming.webdav.FileAttachmentResource#getFileAttachment()
	 */
	@Override
	public FileAttachment getFileAttachment() {
		return fa;
	}

}
