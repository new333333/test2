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

import com.bradmcevoy.http.Auth;
import com.bradmcevoy.http.DeletableResource;
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
public class EipFileResource extends WebdavResource implements PropFindableResource, GetableResource, LockableResource {

	/**
	 * @param factory
	 */
	protected EipFileResource(WebdavResourceFactory factory) {
		super(factory);
		// TODO Auto-generated constructor stub
	}

	private static final Log logger = LogFactory.getLog(EipFileResource.class);

	/* (non-Javadoc)
	 * @see com.bradmcevoy.http.Resource#getUniqueId()
	 */
	@Override
	public String getUniqueId() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.bradmcevoy.http.Resource#getName()
	 */
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.bradmcevoy.http.Resource#getModifiedDate()
	 */
	@Override
	public Date getModifiedDate() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.bradmcevoy.http.LockableResource#lock(com.bradmcevoy.http.LockTimeout, com.bradmcevoy.http.LockInfo)
	 */
	@Override
	public LockResult lock(LockTimeout timeout, LockInfo lockInfo)
			throws NotAuthorizedException, PreConditionFailedException,
			LockedException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.bradmcevoy.http.LockableResource#refreshLock(java.lang.String)
	 */
	@Override
	public LockResult refreshLock(String token) throws NotAuthorizedException,
			PreConditionFailedException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.bradmcevoy.http.LockableResource#unlock(java.lang.String)
	 */
	@Override
	public void unlock(String tokenId) throws NotAuthorizedException,
			PreConditionFailedException {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see com.bradmcevoy.http.LockableResource#getCurrentLock()
	 */
	@Override
	public LockToken getCurrentLock() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.bradmcevoy.http.GetableResource#sendContent(java.io.OutputStream, com.bradmcevoy.http.Range, java.util.Map, java.lang.String)
	 */
	@Override
	public void sendContent(OutputStream out, Range range,
			Map<String, String> params, String contentType) throws IOException,
			NotAuthorizedException, BadRequestException, NotFoundException {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see com.bradmcevoy.http.GetableResource#getMaxAgeSeconds(com.bradmcevoy.http.Auth)
	 */
	@Override
	public Long getMaxAgeSeconds(Auth auth) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.bradmcevoy.http.GetableResource#getContentType(java.lang.String)
	 */
	@Override
	public String getContentType(String accepts) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.bradmcevoy.http.GetableResource#getContentLength()
	 */
	@Override
	public Long getContentLength() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.bradmcevoy.http.PropFindableResource#getCreateDate()
	 */
	@Override
	public Date getCreateDate() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.kablink.teaming.webdav.WebdavResource#getWebdavPath()
	 */
	@Override
	public String getWebdavPath() {
		// TODO Auto-generated method stub
		return null;
	}
	
}
