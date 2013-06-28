/**
 * Copyright (c) 1998-2013 Novell, Inc. and its licensors. All rights reserved.
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
package org.kablink.teaming.gwt.client.binderviews.folderdata;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * Class used to encapsulate information for streaming file blobs to
 * the server.
 * 
 * @author drfoster@novell.com
 */
public class FileBlob implements IsSerializable {
	// The following pertains to a single file being uploaded.
	private Long	m_uploadId;				//
	
	// The following pertain to blobs from the file as they're uploaded.
	private boolean	m_blobBase64Encoded;	//
	private byte[]	m_blobData;				//
	private long	m_blobSize;				//
	private long	m_blobStart;			//
	private String	m_blobMD5Hash;			//
	
	// The following pertain to the file itself.
	private long	m_fileSize;				//
	private Long	m_fileUTCMS;			//
	private String	m_fileName;				//
	private String	m_fileUTC;				//

	/**
	 * Constructor method.
	 * 
	 * Zero parameter constructor as per GWT serialization
	 * requirements.
	 */
	public FileBlob() {
		// Initialize the super class.
		super();
	}

	/**
	 * Constructor method.
	 * 
	 * @param fileName
	 * @param fileUTC
	 * @param fileUTCMS
	 * @param fileSize
	 * @param uploadId
	 * @param base64Encode
	 * @param blobSize
	 */
	public FileBlob(String fileName, String fileUTC, Long fileUTCMS, long fileSize, Long uploadId, boolean base64Encode, long blobSize) {
		// Initialize this object...
		this();

		// ...and store the parameters.
		setUploadId(         uploadId    );
		setFileName(         fileName    );
		setFileUTC(          fileUTC     );
		setFileUTCMS(        fileUTCMS   );
		setFileSize(         fileSize    );
		setBlobBase64Encoded(base64Encode);
		setBlobSize(         blobSize    );
	}

	/**
	 * Get'er methods.
	 * 
	 * @return
	 */
	public Long getUploadId() {return m_uploadId;}
	
	public boolean isBlobBase64Encoded() {return m_blobBase64Encoded;}
	public byte[]  getBlobData()         {return m_blobData;         }
	public long    getBlobSize()         {return m_blobSize;         }
	public long    getBlobStart()        {return m_blobStart;        }
	public String  getBlobMD5Hash()      {return m_blobMD5Hash;      }
	
	public long   getFileSize()  {return m_fileSize; }
	public Long   getFileUTCMS() {return m_fileUTCMS;}
	public String getFileName()  {return m_fileName; }
	public String getFileUTC()   {return m_fileUTC;  }
	
	/**
	 * Set'er methods.
	 * 
	 * @param
	 */
	public void setUploadId(Long uploadId) {m_uploadId = uploadId;}
	
	public void setBlobBase64Encoded(boolean blobBase64Encoded) {m_blobBase64Encoded = blobBase64Encoded;}
	public void setBlobData(         byte[]  blobData)          {m_blobData          = blobData;         }
	public void setBlobSize(         long    blobSize)          {m_blobSize          = blobSize;         }
	public void setBlobStart(        long    blobStart)         {m_blobStart         = blobStart;        }
	public void setBlobMD5Hash(      String  blobMD5Hash)       {m_blobMD5Hash       = blobMD5Hash;      }
	
	public void setFileSize(         long    fileSize)          {m_fileSize          = fileSize;         }
	public void setFileUTCMS(        Long    fileUTCMS)         {m_fileUTCMS         = fileUTCMS;        }
	public void setFileName(         String  fileName)          {m_fileName          = fileName;         }
	public void setFileUTC(          String  fileUTC)           {m_fileUTC           = fileUTC;          }
	
	/**
	 * Increments the blobs starting point by the given value.
	 * 
	 * @param blobStartIncrement
	 */
	public void incrBlobStart(long blobStartIncrement) {
		m_blobStart += blobStartIncrement;
	}
}
