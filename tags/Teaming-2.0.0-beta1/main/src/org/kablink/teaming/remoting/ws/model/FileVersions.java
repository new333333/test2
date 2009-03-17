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
package org.kablink.teaming.remoting.ws.model;

public class FileVersions {
	// Name of the file
	private String fileName;
	// An array of versions ordered by the version number with the highest version first.
	private FileVersion[] versions;
	
	public FileVersions() {}
	
	public FileVersions(String fileName, FileVersion[] versions) {
		this.fileName = fileName;
		this.versions = versions;
	}
	
	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public FileVersion[] getVersions() {
		return versions;
	}

	public void setVersions(FileVersion[] versions) {
		this.versions = versions;
	}
	
	public static class FileVersion {
		private String id;
		private int versionNumber;
		private Timestamp creation;
		private Timestamp modification;
		private long length; // length in bytes
		private String href;

		public FileVersion() {}
		
		public FileVersion(String id, int versionNumber, Timestamp creation, Timestamp modification, long length, String href) {
			this.id = id;
			this.versionNumber = versionNumber;
			this.creation = creation;
			this.modification = modification;
			this.length = length;
			this.href = href;
		}

		public Timestamp getCreation() {
			return creation;
		}

		public void setCreation(Timestamp creation) {
			this.creation = creation;
		}

		public String getHref() {
			return href;
		}

		public void setHref(String href) {
			this.href = href;
		}

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public int getVersionNumber() {
			return versionNumber;
		}

		public void setVersionNumber(int versionNumber) {
			this.versionNumber = versionNumber;
		}

		public long getLength() {
			return length;
		}

		public void setLength(long length) {
			this.length = length;
		}

		public Timestamp getModification() {
			return modification;
		}

		public void setModification(Timestamp modification) {
			this.modification = modification;
		}
		
	}


}
