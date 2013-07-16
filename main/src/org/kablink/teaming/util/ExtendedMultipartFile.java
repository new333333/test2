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
package org.kablink.teaming.util;

import java.io.InputStream;
import java.util.Date;
import java.io.File;

public class ExtendedMultipartFile extends SimpleMultipartFile implements FileExtendedSupport {

	private Date modDate=null;
    private String expectedMd5=null;
	private Long modifierId=null;
	private String modifierName=null;
	private Long creatorId=null;
	private String creatorName=null;
	

	public ExtendedMultipartFile(String fileName, InputStream content) {
		super(fileName, content);
	}
	
	public ExtendedMultipartFile(String fileName, InputStream content, Long contentLength) {
		super(fileName, content, contentLength);
	}
	
	public ExtendedMultipartFile(String fileName, InputStream content, Date modificationDate) {
		super(fileName, content);
		this.modDate = modificationDate;
	}
	
	public ExtendedMultipartFile(String fileName, InputStream content, Date modificationDate, String expectedMd5) {
		super(fileName, content);
		this.modDate = modificationDate;
        this.expectedMd5 = expectedMd5;
	}

	public ExtendedMultipartFile(String fileName, InputStream content, Long contentLength, Date modificationDate, String expectedMd5) {
		super(fileName, content, contentLength);
		this.modDate = modificationDate;
        this.expectedMd5 = expectedMd5;
	}

	public ExtendedMultipartFile(String fileName, File file, boolean deleteOnClose, Date modificationDate) {
		super(fileName, file, deleteOnClose);
		this.modDate = modificationDate;
	}

	public ExtendedMultipartFile(String fileName, File file, boolean deleteOnClose, String modifierName, Date modificationDate) {
		super(fileName, file, deleteOnClose);
		this.modDate = modificationDate;
		this.modifierName = modifierName;
	}
	
	@Override
	public Date getModDate() {
		return modDate;
	}
	public void setModDate(Date modDate) {
		this.modDate = modDate;
	}

    public String getExpectedMd5() {
        return expectedMd5;
    }

    public void setExpectedMd5(String expectedMd5) {
        this.expectedMd5 = expectedMd5;
    }

    @Override
	public String getModifierName() {
		return modifierName;
	}
	public void setModifierName(String modifierName) {
		this.modifierName = modifierName;
	}

	@Override
	public Long getModifierId() {
		return modifierId;
	}
	public void setModifierId(Long modifierId) {
		this.modifierId = modifierId;
	}

	@Override
	public String getCreatorName() {
		return creatorName;
	}
	public void setCreatorName(String creatorName) {
		this.creatorName = creatorName;
	}

	@Override
	public Long getCreatorId() {
		return creatorId;
	}
	public void setCreatorId(Long creatorId) {
		this.creatorId = creatorId;
	}
}
