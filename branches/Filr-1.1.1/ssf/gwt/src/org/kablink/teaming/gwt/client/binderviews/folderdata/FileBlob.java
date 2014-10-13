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
 * (c) 1998-2014 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2014 Novell, Inc. All Rights Reserved.
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
	private Long		m_uploadId;			//
	
	// The following pertain to blobs from the file as they're uploaded.
	private boolean		m_blobBase64;		//
	private byte[]		m_blobDataBytes;	//
	private long		m_blobSize;			//
	private long		m_blobStart;		//
	private ReadType 	m_readType;			//
	private String		m_blobDataString;	//
	private String		m_blobMD5Hash;		//
	
	// The following pertain to the file itself.
	private long		m_fileSize;			//
	private Long		m_fileUTCMS;		//
	private String		m_fileName;			//
	private String		m_fileUTC;			//
	
	// The following are used to recognize the base64 marking in a blob
	// string that was read using a ReadType of DATA_URL.
	private final static String	DATA_URL_B64_MARKER		= ";base64,";
	private final static int	DATA_URL_B64_MARKER_LEN	= DATA_URL_B64_MARKER.length();
	private final static String	EMPTY_DATA_URL			= "data:";

	/**
	 * Enumeration used to specify how the files were read files for
	 * streaming to the server.
	 */
	public enum ReadType implements IsSerializable {
		ARRAY_BUFFER,	// Data in m_blobDataBytes.
		BINARY_STRING,	// Data in m_blobDataString.
		DATA_URL,		//   "  "          "
		TEXT;			//   "  "          "

		/**
		 * Get'er methods.
		 * 
		 * @return
		 */
		public boolean isArrayBuffer()  {return this.equals(ARRAY_BUFFER );}
		public boolean isBinaryString() {return this.equals(BINARY_STRING);}
		public boolean isDataUrl()      {return this.equals(DATA_URL     );}
		public boolean isText()         {return this.equals(TEXT         );}
	}
	

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
	 * @param readType
	 * @param fileName
	 * @param fileUTC
	 * @param fileUTCMS
	 * @param fileSize
	 * @param uploadId
	 * @param base64
	 * @param blobSize
	 */
	public FileBlob(ReadType readType, String fileName, String fileUTC, Long fileUTCMS, long fileSize, Long uploadId, boolean base64, long blobSize) {
		// Initialize this object...
		this();

		// ...and store the parameters.
		setReadType(  readType );
		setUploadId(  uploadId );
		setFileName(  fileName );
		setFileUTC(   fileUTC  );
		setFileUTCMS( fileUTCMS);
		setFileSize(  fileSize );
		setBlobBase64(base64   );
		setBlobSize(  blobSize );
	}

	/**
	 * Get'er methods.
	 * 
	 * @return
	 */
	public Long     getUploadId()       {return m_uploadId;      }
	
	public boolean  isBlobBase64()      {return m_blobBase64;    }
	public byte[]   getBlobDataBytes()  {return m_blobDataBytes; }
	public long     getBlobSize()       {return m_blobSize;      }
	public long     getBlobStart()      {return m_blobStart;     }
	public ReadType getReadType()       {return m_readType;      }
	public String   getBlobDataString() {return m_blobDataString;}
	public String   getBlobMD5Hash()    {return m_blobMD5Hash;   }
	
	public long     getFileSize()       {return m_fileSize;      }
	public Long     getFileUTCMS()      {return m_fileUTCMS;     }
	public String   getFileName()       {return m_fileName;      }
	public String   getFileUTC()        {return m_fileUTC;       }
	
	/**
	 * Set'er methods.
	 * 
	 * @param
	 */
	public void setUploadId(      Long     uploadId)       {m_uploadId       = uploadId;      }
	
	public void setBlobBase64(    boolean  blobBase64)     {m_blobBase64     = blobBase64;    }
	public void setBlobDataBytes( byte[]   blobDataBytes)  {m_blobDataBytes  = blobDataBytes; }
	public void setBlobSize(      long     blobSize)       {m_blobSize       = blobSize;      }
	public void setBlobStart(     long     blobStart)      {m_blobStart      = blobStart;     }
	public void setReadType(      ReadType readType)       {m_readType       = readType;      }
	public void setBlobDataString(String   blobDataString) {m_blobDataString = blobDataString;}
	public void setBlobMD5Hash(   String   blobMD5Hash)    {m_blobMD5Hash    = blobMD5Hash;   }
	
	public void setFileSize(      long     fileSize)       {m_fileSize       = fileSize;      }
	public void setFileUTCMS(     Long     fileUTCMS)      {m_fileUTCMS      = fileUTCMS;     }
	public void setFileName(      String   fileName)       {m_fileName       = fileName;      }
	public void setFileUTC(       String   fileUTC)        {m_fileUTC        = fileUTC;       }

	/**
	 * Removes the base64 encoding marker from a data URL string.
	 * 
	 * @param dataUrl
	 * 
	 * @return
	 */
	public static String fixDataUrlString(String dataUrl) {
		String reply;
		if (null == dataUrl) {
			reply = null;
		}
		else {
			int b64Marker = dataUrl.indexOf(DATA_URL_B64_MARKER);
			if (0 < b64Marker) {
				reply = dataUrl.substring(b64Marker + DATA_URL_B64_MARKER_LEN);
			}
			else if (dataUrl.equals(EMPTY_DATA_URL)) {
				reply = "";
			}
			else {
				reply = dataUrl;
			}
		}
		return reply;
	}
	
	/**
	 * Increments the blobs starting point by the given value.
	 * 
	 * @param blobStartIncrement
	 */
	public void incrBlobStart(long blobStartIncrement) {
		m_blobStart += blobStartIncrement;
	}
}
