package com.sitescape.ef.domain;

import java.util.Date;

/**
 * @author Jong Kim
 *
 */
public class AuditTrail extends PersistentObject {
    // timestamp, who, forum id, type of transaction/operation, description
    // TODO to be defined
    
    private Date timestamp;
    private User who;
    private String forumId;
    private String objType; // type of the object to which operation is performed.
    private String objId; // id of the object to which operation is performed.
    private String transactionType; // type of transaction/operation
    private String description; // any additional description
    
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public String getForumId() {
        return forumId;
    }
    public void setForumId(String forumId) {
        this.forumId = forumId;
    }
    public String getObjId() {
        return objId;
    }
    public void setObjId(String objId) {
        this.objId = objId;
    }
    public String getObjType() {
        return objType;
    }
    public void setObjType(String objType) {
        this.objType = objType;
    }
    public Date getTimestamp() {
        return timestamp;
    }
    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
    public String getTransactionType() {
        return transactionType;
    }
    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }
    public User getWho() {
        // Since this class is only used internally and never passed back
        // to presentation tier, we don't have to worry about protecting 
        // this field.
        return who;
    }
    public void setWho(User who) {
        this.who = who;
    }
}
