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
package org.kablink.teaming.remoting.ws.model;

import java.io.Serializable;

public class AttachmentsField extends Field implements Serializable {

	private Attachment[] attachments;
	
	public AttachmentsField() {}
	
	public AttachmentsField(String name, String type, Attachment[] attachments) {
		super(name, type);
		setAttachments(attachments);
	}
	
	public Attachment[] getAttachments() {
		return attachments;
	}

	public void setAttachments(Attachment[] attachments) {
		this.attachments = attachments;
	}

	public static class Attachment {
		private String id;
		private String fileName;
		private Timestamp creation;
		private Timestamp modification;
		private long length; // The length, in bytes, of the attachment
		private String href;
		
		public Attachment() {}
		
		public Attachment(String id, String fileName, Timestamp creation, Timestamp modification, long length, String href) {
			this.id = id;
			this.fileName = fileName;
			this.creation = creation;
			this.modification = modification;
			this.length = length;
			this.href = href;
		}
	
		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getFileName() {
			return fileName;
		}
	
		public void setFileName(String fileName) {
			this.fileName = fileName;
		}
	
		public Timestamp getCreation() {
			return creation;
		}

		public void setCreation(Timestamp creation) {
			this.creation = creation;
		}

		public Timestamp getModification() {
			return modification;
		}

		public void setModification(Timestamp modification) {
			this.modification = modification;
		}

		public String getHref() {
			return href;
		}
	
		public void setHref(String href) {
			this.href = href;
		}

		public long getLength() {
			return length;
		}

		public void setLength(long length) {
			this.length = length;
		}
	}

}
