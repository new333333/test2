package com.sitescape.ef.domain;

import java.util.ArrayList;
import java.util.List;

/**
 * @hibernate.class table="SS_EmailAlias" dynamic-update="true"
 * @hibernate.mapping auto-import="false"
 */
public class EmailAlias extends PersistentObject {
    private String aliasName;
    private String zoneName;
    private List postings;
	public EmailAlias() {
		
	}

    /**
     * Email alias name, eg. foo@mail.bar.com.
     * @hibernate.property length="256"
     */
    
    public String getAliasName() {
        return aliasName;
    }
    public void setAliasName(String aliasName) {
        this.aliasName = aliasName;
    }
    /**
     * @hibernate.property length="100" not-null="true"
     * @return
     */
    public String getZoneName() {
    	return zoneName;
    }
    public void setZoneName(String zoneName) {
    	this.zoneName = zoneName;
    }
    /**
     * @hibernate.bag  lazy="true" cascade="all,delete-orphan" inverse="true" optimistic-lock="false" 
     * @hibernate.key column="emailAlias" 
     * @hibernate.one-to-many class="com.sitescape.ef.domain.PostingDef" 
     * @return
     */
    public List getPostings() {
    	if (postings == null) return new ArrayList();
    	return postings;
    }
    public void setPostings(List postings) {
        this.postings = postings;
    }
    
}
