package com.sitescape.ef.domain;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.Date;

import com.sitescape.ef.ObjectKeys;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

public class ChangeLog {
	public static final String ADDENTRY="addEntry";
	public static final String MODIFYENTRY="modifyEntry";
	public static final String DELETEENTRY="deleteEntry";
	public static final String MOVEENTRY="moveEntry";
	public static final String STARTWORKFLOW="startWorkflow";
	public static final String MODIFYWORKFLOWSTATE="modifyWorkflowState";
	public static final String ADDWORKFLOWRESPONSE="addWorkflowResponse";
	public static final String WORKFLOWTIMEOUT="timeoutWorkflow";
	public static final String MODIFYWORKFLOWSTATEONREPLY="modifyWorkflowStateOnReply";
	public static final String ADDBINDER="addBinder";
	public static final String MODIFYBINDER="modifyBinder";
	public static final String DELETEBINDER="deleteBinder";
	public static final String MOVEBINDER="moveBinder";
	public static final String FILERENAME="renameFile";
	public static final String FILEVERSIONDELETE="deleteVersion";
	public static final String FILEADD="addFile";
	public static final String FILEMODIFY="modifyFile";
	public static final String FILEDELETE="deleteFile";
	public static final String FILEMOVE="moveFile";
	public static final String ACCESSDELETE="deleteAccess";
	public static final String ACCESSMODIFY="modifyAccess";

	
	protected String id;
	protected String operation;
	protected String userName;
	protected Long userId;
	protected Date operationDate;
	protected String xmlString=null;
	protected Document document=null;
	protected Long entityId;
	protected String entityType;
	protected Long zoneId;
	protected Long binderId;
	protected Long version;
	protected String docNumber;
	
	public ChangeLog() {
	}
	public ChangeLog(DefinableEntity entity, String operation) {
		if (entity instanceof Binder) {
			this.binderId = entity.getId();
		} else {
			this.binderId = entity.getParentBinder().getId();
			if (entity instanceof FolderEntry) 
				this.docNumber = ((FolderEntry)entity).getDocNumber();
		}
		this.operation = operation;
		if (operation.contains("Workflow") && entity instanceof WorkflowSupport) {
			WorkflowSupport wfEntry = (WorkflowSupport)entity;
			this.operationDate = wfEntry.getWorkflowChange().getDate();
			this.userName = wfEntry.getWorkflowChange().getPrincipal().getName();
			this.userId = wfEntry.getWorkflowChange().getPrincipal().getId();
			this.zoneId = wfEntry.getWorkflowChange().getPrincipal().getZoneId();
		} else { 
			this.operationDate = entity.getModification().getDate();
			this.userName = entity.getModification().getPrincipal().getName();
			this.userId = entity.getModification().getPrincipal().getId();
			this.zoneId = entity.getModification().getPrincipal().getZoneId();
		}

		this.entityId = entity.getEntityIdentifier().getEntityId();
		this.entityType = entity.getEntityIdentifier().getEntityType().name();
		this.version = entity.getLogVersion();
		this.document = DocumentHelper.createDocument();
		

	}
	/**
	 * @hibernate.id generator-class="uuid.hex" unsaved-value="null"
	 * @hibernate.column name="id" sql-type="char(32)"
	 */    
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
    /**
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
     * @hibernate.property not-null="true"
     */
    public Long getZoneId() {
    	return this.zoneId;
    }
    public void setZoneId(Long zoneId) {
    	this.zoneId = zoneId;
    }
    /**
     * @hibernate.property not-null="true"
     */
    public Long getBinderId() {
    	return this.binderId;
    }
    public void setBinderId(Long binderId) {
    	this.binderId = binderId;
    }
    /**
     * @hibernate.property length="512"
     */
    public String getDocNumber() {
    	return docNumber;
    }
    public void setDocNumber(String docNumber) {
    	this.docNumber = docNumber;
    }
    /**
     * @hibernate.property not-null="true"
     */
    public Long getVersion() {
    	return this.version;
    }
    public void setVersion(Long version) {
    	this.version = version;
    }
    /**
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
	public Element getEntityRoot() {
		Element root = getDocument().getRootElement();
		if (root == null) {
			root = getDocument().addElement(getEntityType());
			//add stuff in the database row, so xml is usefull on its own
			root.addAttribute(ObjectKeys.XTAG_ID, getEntityId().toString());
			root.addAttribute(ObjectKeys.XTAG_ENTITY_LOGVERSION, getVersion().toString());
			root.addAttribute(ObjectKeys.XTAG_OPERATION, getOperation());
			root.addAttribute(ObjectKeys.XTAG_MODIFIEDBY, getUserName());
			root.addAttribute(ObjectKeys.XTAG_MODIFIEDON, getOperationDate().toString());
		}
		return root;
	}
	
    /**
     * @hibernate.property type="org.springframework.orm.hibernate3.support.ClobStringType"
     */
    public String getXmlString() {
    	if ((xmlString == null) && (document != null)) {
    		try {
    			StringWriter baos = new StringWriter();
    			if (baos == null) return null;
    			XMLWriter xOut = new XMLWriter(baos, OutputFormat.createCompactFormat());
    			xOut.write(document);
    			xOut.close();
    			xmlString = baos.toString(); 
    		} catch (Exception fe) {
    			fe.printStackTrace();
    		}
    	}
    	return xmlString;
    }
 
    protected void setXmlString(String xmlString) {
        this.xmlString=xmlString;
    }
    public String getXmlNoHeader() {
    	String xml = null;
    	if (document == null) getDocument();
    	if (document != null) {
    		try {
    			StringWriter baos = new StringWriter();
    			if (baos == null) return null;
    			OutputFormat format = OutputFormat.createPrettyPrint();
    			format.setSuppressDeclaration(true);
    			XMLWriter xOut = new XMLWriter(baos, format);
    			xOut.write(document);
    			xOut.close();
    			xml = baos.toString(); 
    		} catch (Exception fe) {
    			fe.printStackTrace();
    		}
    	}
    	return xml;
    }
 
    public Document getDocument() {
    	if (document != null) return document;
    	if (xmlString == null) return null;
    	try {
    		StringReader ois = new StringReader(xmlString);
    		if (ois == null) return null;
    		SAXReader xIn = new SAXReader();
    		document = xIn.read(ois);   
    		ois.close();
    	} catch (Exception fe) {
    		fe.printStackTrace();
    	}
        return document;
    }


}
