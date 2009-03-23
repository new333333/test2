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
package org.kablink.teaming.domain;

import java.io.Serializable;
import java.util.Map;

public class IndexNode extends ZonedObject {

	/**
	 * Both read and write access is allowed for the node.
	 */
	public static final String ACCESS_MODE_READ_WRITE	= "readwrite";
	/**
	 * Only write access is allowed for the node.
	 */
	public static final String ACCESS_MODE_WRITE_ONLY	= "writeonly";
	/**
	 * Neither read nor write access is allowed for the node.
	 */
	public static final String ACCESS_MODE_NO_ACCESS	= "noaccess";
	
	private String id;
	private Name name;
	private String accessMode = "readwrite";
	private boolean inSynch = true;
	
	// The following two fields are here for convenience only, and not persistent.
	private String title;
	private Map<String,String> displayProperties;
	
	public IndexNode() {}
	
	public IndexNode(String nodeName, String indexName) {
		this.name = new Name(nodeName, indexName);
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getNodeName() {
		return (name != null)? name.getNodeName() : null;
	}

	public String getIndexName() {
		return (name != null)? name.getIndexName() : null;
	}

	public String getAccessMode() {
		return accessMode;
	}
	public void setAccessMode(String accessMode) {
		this.accessMode = accessMode;
	}
	
	public boolean isInSynch() {
		return inSynch;
	}

	public void setInSynch(boolean inSynch) {
		this.inSynch = inSynch;
	}

	public Map<String, String> getDisplayProperties() {
		return displayProperties;
	}

	public void setDisplayProperties(Map<String, String> displayProperties) {
		this.displayProperties = displayProperties;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	
	public static class Name implements Serializable {
		private String nodeName;
		private String indexName;
		
		public Name() {}
		public Name(String nodeName, String indexName) {
			this.nodeName = nodeName;
			this.indexName = indexName;
		}
		public String getIndexName() {
			return indexName;
		}
		public void setIndexName(String indexName) {
			this.indexName = indexName;
		}
		public String getNodeName() {
			return nodeName;
		}
		public void setNodeName(String nodeName) {
			this.nodeName = nodeName;
		}

		public boolean equals(Object obj) {
	        if(this == obj)
	            return true;

	        if ((obj == null) || !(obj instanceof IndexNode))
	            return false;
	            
	        Name idf = (Name) obj;
	        if(nodeName.equals(idf.nodeName) && indexName.equals(idf.indexName))
	        	return true;
	        else
	        	return false;
		}
		public int hashCode() {
	       	int hash = 7;
	    	hash = 31*hash + nodeName.hashCode();
	    	hash = 31*hash + indexName.hashCode();
	    	return hash;
		}
	}

}
