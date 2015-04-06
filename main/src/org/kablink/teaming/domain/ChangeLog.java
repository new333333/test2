/**
 * Copyright (c) 1998-2010 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2010 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2010 Novell, Inc. All Rights Reserved.
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

import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.util.XmlFileUtil;

/**
 * @hibernate.class table="SS_ChangeLogs" 
 * 
 * Log changes for reporting
 * We only update rows in this table if a binder moves
 *
 */
public class ChangeLog extends ZonedObject {
	//Operations
	public static final String ADDENTRY="addEntry";
	public static final String MODIFYENTRY="modifyEntry";
	public static final String DELETEENTRY="deleteEntry";
	public static final String PREDELETEENTRY="preDeleteEntry";
	public static final String RESTOREENTRY="restoreEntry";
	public static final String MOVEENTRY="moveEntry";
	public static final String STARTWORKFLOW="startWorkflow";
	public static final String MODIFYWORKFLOWSTATE="modifyWorkflowState";
	public static final String ADDWORKFLOWRESPONSE="addWorkflowResponse";
	public static final String WORKFLOWTIMEOUT="timeoutWorkflow";
	public static final String MODIFYWORKFLOWSTATEONREPLY="modifyWorkflowStateOnReply";
	public static final String ENDWORKFLOW="endWorkflow";
	public static final String ADDBINDER="addBinder";
	public static final String MODIFYBINDER="modifyBinder";
	public static final String DELETEBINDER="deleteBinder";
	public static final String PREDELETEBINDER="preDeleteBinder";
	public static final String RESTOREBINDER="restoreBinder";
	public static final String MOVEBINDER="moveBinder";
	public static final String FILERENAME="renameFile";
	public static final String FILEVERSIONDELETE="deleteVersion";
	public static final String FILEADD="addFile";
	public static final String FILEMODIFY="modifyFile";
	public static final String FILEMODIFY_INCR_MAJOR_VERSION="modifyFile.incrMajorVersion";
	public static final String FILEMODIFY_ENCRYPT="modifyFile.encrypt";
	public static final String FILEMODIFY_REVERT="modifyFile.revert";
	public static final String FILEMODIFY_SET_COMMENT="modifyFile.setComment";
	public static final String FILEMODIFY_SET_STATUS="modifyFile.setStatus";
	public static final String FILEDELETE="deleteFile";
	public static final String FILEMOVE="moveFile";
	public static final String ACCESSMODIFY="modifyAccess";
	public static final String CHANGETIMESTAMPS="changeTimestamps";
	public static final String UPDATEMODIFICATIONSTAMP="updateModificationStamp";
	public static final String SHARE_ADD="shareEntityAdd";
	public static final String SHARE_MODIFY="shareEntityModify";
	public static final String SHARE_DELETE="shareEntityDelete";
	
	protected static final Log logger = LogFactory.getLog(ChangeLog.class);
	
	protected String id;
	protected String operation;
	protected String userName;
	protected Long userId;
	protected Date operationDate;
	protected String xmlStrDeprecated=null; // This is old field that maps to uncompressed lob column in the database
	protected String xmlStr=null; // This is new field that maps to compressed blob column in the database
	protected Document document=null;
	protected Long entityId;
	protected String entityType;
	protected Long owningBinderId;
	protected Long version;
	protected String docNumber;
	protected String owningBinderKey;  //used for queries
	
	// For use by Hibernate only
	private ChangeLog() {
	}
	public ChangeLog(DefinableEntity entity, String operation) {
		this(entity, operation, (Principal)null);
	}
	
	protected ChangeLog(DefinableEntity entity, String operation, Principal principal) {	
		Binder binder;
		if (entity instanceof Binder) {
			binder = (Binder)entity;
		} else {
			binder = entity.getParentBinder();
			if (entity instanceof FolderEntry) 
				this.docNumber = ((FolderEntry)entity).getDocNumber();
		}
		this.owningBinderId = binder.getId();
		if(binder.getBinderKey() != null) // temporary workaround for issue #515
			this.owningBinderKey = binder.getBinderKey().getSortKey();
		this.operation = operation;
		if (operation.equals("addWorkflowResponse") && entity instanceof WorkflowSupport) {
			WorkflowSupport wfEntry = (WorkflowSupport)entity;
			User user = RequestContextHolder.getRequestContext().getUser();
			this.operationDate = new Date();
			this.userName = user.getName();
			this.userId = user.getId();
			this.zoneId = user.getZoneId();
		} else if (operation.contains("Workflow") && entity instanceof WorkflowSupport) {
			WorkflowSupport wfEntry = (WorkflowSupport)entity;
			this.operationDate = wfEntry.getWorkflowChange().getDate();
			this.userName = wfEntry.getWorkflowChange().getPrincipal().getName();
			this.userId = wfEntry.getWorkflowChange().getPrincipal().getId();
			this.zoneId = wfEntry.getWorkflowChange().getPrincipal().getZoneId();
		} else if(principal != null) {
			this.operationDate = new Date();
			this.userName = principal.getName();
			this.userId = principal.getId();
			this.zoneId = principal.getZoneId();
		} else { 
			this.operationDate = entity.getModification().getDate();
			this.userName = entity.getModification().getPrincipal().getName();
			this.userId = entity.getModification().getPrincipal().getId();
			this.zoneId = entity.getModification().getPrincipal().getZoneId();
		}

		this.entityId = entity.getEntityIdentifier().getEntityId();
		this.entityType = entity.getEntityType().name();
		this.version = entity.getLogVersion();
		this.document = DocumentHelper.createDocument();
		

	}
	/**
	 * Log id
	 * @hibernate.id generator-class="uuid.hex" unsaved-value="null"
	 * @hibernate.column name="id" sql-type="char(32)"
	 */    
	public String getId() {
		return id;
	}
	protected void setId(String id) {
		this.id = id;
	}
    /**
     * Return id of entity
     * @hibernate.property
     * @return
     */
    public Long getEntityId() {
    	return entityId;
    }
    public void setEntityId(Long entityId) {
    	this.entityId = entityId;
    }
    /**
     * Return string representation of entity type.  
     * @see org.kablink.teaming.domain.EntityIdentifier.EntityType
     * @hibernate.property length="16"
     * @return
     */
    public String getEntityType() {
    	return entityType;
    }
    public void setEntityType(String entityType) {
    	this.entityType = entityType;
    }
    /**
     * Return zone id
     * @hibernate.property 
     */
    public Long getZoneId() {
    	return this.zoneId;
    }
    public void setZoneId(Long zoneId) {
    	this.zoneId = zoneId;
    }
    /**
     * Return id of owning binder.  If the entity is a binder, return binder id.
     * @hibernate.property 
     */
    public Long getOwningBinderId() {
    	return this.owningBinderId;
    }
    public void setOwningBinderId(Long owningBinderId) {
    	this.owningBinderId = owningBinderId;
    }
    /**
     * Return the sort key of the owning binder.
     * @hibernate.property length="255" 
     * @return
     */
    public String getOwningBinderKey() {
        return owningBinderKey;
    }
    public void setOwningBinderKey(String owningBinderKey) {
        this.owningBinderKey = owningBinderKey;
    } 
    /**
     * Return the document number for <code>folderEntries</code>.
     * @hibernate.property length="512"
     */
    public String getDocNumber() {
    	return docNumber;
    }
    public void setDocNumber(String docNumber) {
    	this.docNumber = docNumber;
    }
    /**
     * Return entity version number when this log entry was created.
     * @hibernate.property not-null="true"
     */
    public Long getVersion() {
    	return this.version;
    }
    public void setVersion(Long version) {
    	this.version = version;
    }
    /**
     * Return the operation being logged
     * @hibernate.property length="32"
     * @return
     */
	public String getOperation() {
		return operation;
	}
	public void setOperation(String operation) {
		this.operation = operation;
	}
    /**
     * Return the name of the user that generated the log.
     * @hibernate.property length="82"
     * @return
     */
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
    /**
      * Return the id of the user that generated the log.
      * @hibernate.property 
     */
    public Long getUserId() {
    	return this.userId;
    }
    public void setUserId(Long userId) {
    	this.userId = userId;
    }
    /**
     * @hibernate.property type="timestamp"
     * @return
     */
	public Date getOperationDate() {
		return operationDate;
	}
	public void setOperationDate(Date operationDate) {
		this.operationDate = operationDate;
	}
	/**
	 * Return the root of the XML document.
	 * @return
	 */
	public Element getEntityRoot() {
		Element root = getDocument().getRootElement();
		if (root == null) {
			root = getDocument().addElement(getEntityType());
			//add stuff in the database row, so xml is usefull on its own
			root.addAttribute(ObjectKeys.XTAG_ATTRIBUTE_DATABASEID, getEntityId().toString());
			root.addAttribute(ObjectKeys.XTAG_ATTRIBUTE_LOGVERSION, getVersion().toString());
			root.addAttribute(ObjectKeys.XTAG_ATTRIBUTE_OPERATION, getOperation());
			root.addAttribute(ObjectKeys.XTAG_ATTRIBUTE_MODIFIEDBY, getUserName());
			root.addAttribute(ObjectKeys.XTAG_ATTRIBUTE_MODIFIEDON, getOperationDate().toString());
		}
		return root;
	}
	
	// For use by Hibernate only
    private String getXmlStrDeprecated() {
    	// Return the same value so that Hibernate won't unnecessarily try to update old change log records
    	// created prior to our recent change incorporating data compression.
    	return xmlStrDeprecated;
    }
 
	// For use by Hibernate only
    private String getXmlStr() {
    	// If XML document is present, serialize it into the new field.
    	if ((xmlStr == null) && (document != null)) {
    		try {
    			xmlStr = XmlFileUtil.writeString(document, OutputFormat.createCompactFormat());
             } catch (Exception ex) {
            	 throw new IllegalArgumentException(ex.getLocalizedMessage());
             }	
    	}
    	return xmlStr;
    }
    
	// For use by Hibernate only
    private void setXmlStrDeprecated(String xmlStrDeprecated) {
        this.xmlStrDeprecated=xmlStrDeprecated;
    }
    
	// For use by Hibernate only
    private void setXmlStr(String xmlStr) {
        this.xmlStr=xmlStr;
    }
    
    public String getXmlNoHeader() {
    	String xml = null;
    	if (document == null) getDocument();
    	if (document != null) {
    		OutputFormat format = OutputFormat.createPrettyPrint();
			format.setSuppressDeclaration(true);
   			try {
   				xml = XmlFileUtil.writeString(document, format);
   			} catch (Exception ex) {
   		       	throw new IllegalArgumentException(ex.getLocalizedMessage());
   			}
    	}
    	return xml;
    }
 
    // Used by application
    public Document getDocument() {
    	if (document != null) 
    		return document;
    	
    	if (xmlStr != null) {
	    	try {
	    		document = XmlFileUtil.generateXMLFromString(xmlStr);
	        } catch (Exception ex) {
	        	throw new IllegalArgumentException(ex.getLocalizedMessage());
	        }
    	}
    	else if (xmlStrDeprecated != null) {
	    	try {
	    		document = XmlFileUtil.generateXMLFromString(xmlStrDeprecated);
	        } catch (Exception ex) {
	        	throw new IllegalArgumentException(ex.getLocalizedMessage());
	        }
    	}
        return document;
    }

}
