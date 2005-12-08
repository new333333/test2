package com.sitescape.ef.security.function;

import java.util.Set;

/**
 * @hibernate.class table="SS_WorkAreaFunctionMemberships" lazy="false" dynamic-update="true"
 * @hibernate.cache usage="nonstrict-read-write"
 * @hibernate.query name="find-FunctionMemberships-ByCompanyAndWorkArea" query="from WorkAreaFunctionMembership functionMembership left join fetch functionMembership.memberIds where functionMembership.zoneName=:zoneName and functionMembership.workAreaId=:workAreaId and functionMembership.workAreaType=:workAreaType"
 * @hibernate.query name="check-WorkAreaFunctionMembership" query="select fm.id from com.sitescape.ef.security.function.Function function join function.operationNames operationName, com.sitescape.ef.security.function.WorkAreaFunctionMembership fm join fm.memberIds memberId where function.zoneName=:zoneName and fm.zoneName=:zoneName and fm.workAreaId=:workAreaId and fm.workAreaType=:workAreaType and operationName=:operationName and function.id=fm.functionId and memberId in (:principalIds)"
 * 
 * <code>FunctionMembership</code> defines the members of a function for 
 * a work area.
 * 
 * @author Jong Kim
 */
public class WorkAreaFunctionMembership {
    
    private static final String SPLIT_CHAR = ",";
    
    private Long id;
    private String zoneName;
    private Long workAreaId;
    private String workAreaType;
    private Long functionId;
    private Set memberIds; // A set of Long
    
    private long lockVersion; // Used for optimistic locking support
    
	/**
	 * @hibernate.id generator-class="native" type="long"  unsaved-value="null"
	 */    
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    
    /**
     * @hibernate.version type="long"
     */
    private long getLockVersion() {
        return this.lockVersion;
    }
    private void setLockVersion(long lockVersion) {
        this.lockVersion = lockVersion;
    }
    
    /**
     * @hibernate.set lazy="false" table="SS_WorkAreaFunctionMembers" cascade="all"
     * @hibernate.key column="workAreaFunctionMembershipId"
     * @hibernate.element type="long" column="memberId" not-null="true"
     * 
     */
    public Set getMemberIds() {
        return memberIds;
    }
    public void setMemberIds(Set memberIds) {
        this.memberIds = memberIds;
    }
    
	/**
	 * @hibernate.property column="workAreaId" type="long" not-null="true"
	 */    
    public Long getWorkAreaId() {
        return workAreaId;
    }
    public void setWorkAreaId(Long workAreaId) {
        this.workAreaId = workAreaId;
    }
    
    /**
     * @hibernate.property column="workAreaType" length="16" not-null="true"
     * 
     * @return
     */
    public String getWorkAreaType() {
        return workAreaType;
    }
    public void setWorkAreaType(String workAreaType) {
        this.workAreaType = workAreaType;
    }
    
	/**
	 * @hibernate.property length="100" not-null="true"
	 */    
    public String getZoneName() {
        return zoneName;
    }
    public void setZoneName(String zoneName) {
        this.zoneName = zoneName;
    }
    
    /**
     * @hibernate.property column="functionId" type="long" not-null="true"
     * 
     * @return
     */
    public Long getFunctionId() {
        return functionId;
    }
    public void setFunctionId(Long functionId) {
        this.functionId = functionId;
    }
}
