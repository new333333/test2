package com.sitescape.ef.security.acl;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.sitescape.ef.domain.SSClobString;

/**
 * <code>AclSet</code> maintains a set of principal ids for each access type.
 * 
 * @author Jong Kim
 */
public class AclSet implements Cloneable {

    private static final String COMMA = ",";
    
    // The following fields are persisted. 
    private SSClobString readMembers;
    private SSClobString writeMembers;
    private SSClobString deleteMembers;
    private SSClobString changeAclMembers;
    
    // The following fields are not persisted.
    private Set readMemberIds;
    private Set writeMemberIds;
    private Set deleteMemberIds;
    private Set changeAclMemberIds;
    
    /**
     * @hibernate.property type="com.sitescape.ef.dao.util.SSClobStringType"
     * @hibernate.column name="readMembers"
     */
    private SSClobString getReadMembers() {
        if(readMembers == null && readMemberIds != null)
            readMembers = fromSet(readMemberIds);
        return readMembers;
    }
    private void setReadMembers(SSClobString members) {
        this.readMembers = members;
        this.readMemberIds = null;
    }
    
    /**
     * @hibernate.property type="com.sitescape.ef.dao.util.SSClobStringType"
     * @hibernate.column name="writeMembers"
     */
    private SSClobString getWriteMembers() {
        if(writeMembers == null && writeMemberIds != null)
            writeMembers = fromSet(writeMemberIds);
        return writeMembers;
    }
    private void setWriteMembers(SSClobString members) {
        this.writeMembers = members;
        this.writeMemberIds = null;
    }
    
    /**
     * @hibernate.property type="com.sitescape.ef.dao.util.SSClobStringType"
     * @hibernate.column name="deleteMembers"
     */
    private SSClobString getDeleteMembers() {
        if(deleteMembers == null && deleteMemberIds != null)
            deleteMembers = fromSet(deleteMemberIds);
        return deleteMembers;
    }
    private void setDeleteMembers(SSClobString members) {
        this.deleteMembers = members;
        this.deleteMemberIds = null;
    }
    
    /**
     * @hibernate.property type="com.sitescape.ef.dao.util.SSClobStringType"
     * @hibernate.column name="changeAclMembers"
     */
    private SSClobString getChangeAclMembers() {
        if(changeAclMembers == null && changeAclMemberIds != null)
            changeAclMembers = fromSet(changeAclMemberIds);
        return changeAclMembers;
    }
    private void setChangeAclMembers(SSClobString members) {
        this.changeAclMembers = members;
        this.changeAclMemberIds = null;
    }
    
    public Set getReadMemberIds() {
        if(readMemberIds == null && readMembers != null)
            readMemberIds = fromStr(readMembers);
        return readMemberIds;
    }
    public void setReadMemberIds(Set readMemberIds) {
        this.readMemberIds = readMemberIds;
        this.readMembers = null;
    }
    public void addReadMemberId(Long memberId) {
        this.getReadMemberIds().add(memberId);
    }
    public boolean removeReadMemberId(Long memberId) {
        return this.getReadMemberIds().remove(memberId);
    }
    
    public Set getWriteMemberIds() {
        if(writeMemberIds == null && writeMembers != null)
            writeMemberIds = fromStr(writeMembers);
        return writeMemberIds;
    }
    public void setWriteMemberIds(Set writeMemberIds) {
        this.writeMemberIds = writeMemberIds;
        this.writeMembers = null;
    }
    public void addWriteMemberId(Long memberId) {
        this.getWriteMemberIds().add(memberId);
    }
    public boolean removeWriteMemberId(Long memberId) {
        return this.getWriteMemberIds().remove(memberId);
    }
    
    public Set getDeleteMemberIds() {
        if(deleteMemberIds == null && deleteMembers != null)
            deleteMemberIds = fromStr(deleteMembers);
        return deleteMemberIds;
    }
    public void setDeleteMemberIds(Set deleteMemberIds) {
        this.deleteMemberIds = deleteMemberIds;
        this.deleteMembers = null;
    }
    public void addDeleteMemberId(Long memberId) {
        this.getDeleteMemberIds().add(memberId);
    }
    public boolean removeDeleteMemberId(Long memberId) {
        return this.getDeleteMemberIds().remove(memberId);
    }    

    public Set getChangeAclMemberIds() {
        if(changeAclMemberIds == null && changeAclMembers != null)
            changeAclMemberIds = fromStr(changeAclMembers);
        return changeAclMemberIds;
    }
    public void setChangeAclMemberIds(Set changeAclMemberIds) {
        this.changeAclMemberIds = changeAclMemberIds;
        this.changeAclMembers = null;
    }
    public void addChangeAclMemberId(Long memberId) {
        this.getChangeAclMemberIds().add(memberId);
    }
    public boolean removeChangeAclMemberId(Long memberId) {
        return this.getChangeAclMemberIds().remove(memberId);
    }
        
    public Set getMemberIds(AccessType accessType) {
        if(accessType == AccessType.READ)
            return getReadMemberIds();
        else if(accessType == AccessType.WRITE)
            return getWriteMemberIds();
        else if(accessType == AccessType.DELETE)
            return getDeleteMemberIds();
        else if(accessType == AccessType.CHANGE_ACL)
            return getChangeAclMemberIds();
        else
            throw new IllegalArgumentException("Illegal access type: " + accessType.toString());
    }
    
    public void addMemberId(AccessType accessType, Long memberId) {
        if(accessType == AccessType.READ)
            addReadMemberId(memberId);
        else if(accessType == AccessType.WRITE)
            addWriteMemberId(memberId);
        else if(accessType == AccessType.DELETE)
            addDeleteMemberId(memberId);
        else if(accessType == AccessType.CHANGE_ACL)
            addChangeAclMemberId(memberId);
        else
            throw new IllegalArgumentException("Illegal access type: " + accessType.toString());       
    }
    
    public boolean removeMemberId(AccessType accessType, Long memberId) {
        if(accessType == AccessType.READ)
            return removeReadMemberId(memberId);
        else if(accessType == AccessType.WRITE)
            return removeWriteMemberId(memberId);
        else if(accessType == AccessType.DELETE)
            return removeDeleteMemberId(memberId);
        else if(accessType == AccessType.CHANGE_ACL)
            return removeChangeAclMemberId(memberId);
        else
            throw new IllegalArgumentException("Illegal access type: " + accessType.toString());       
    }
    
    public Object clone() {
        AclSet copy = new AclSet();
        copy.setReadMembers(new SSClobString(getReadMembers().getText()));
        copy.setWriteMembers(new SSClobString(getWriteMembers().getText()));
        copy.setDeleteMembers(new SSClobString(getDeleteMembers().getText()));
        copy.setChangeAclMembers(new SSClobString(getChangeAclMembers().getText()));
        return copy;
    }
    
    public void clear() {
        readMembers = null;
        writeMembers = null;
        deleteMembers = null;
        changeAclMembers = null;
        
        readMemberIds = null;
        writeMemberIds = null;
        deleteMemberIds = null;
        changeAclMemberIds = null;
    }
    
    private SSClobString fromSet(Set members) {
        if(members == null)
            return null;
        
        StringBuffer sb = new StringBuffer(COMMA);
        for(Iterator i = members.iterator(); i.hasNext();) {
            sb.append(i.next().toString()).append(COMMA);
        }
        
        return new SSClobString(sb.toString());
    }
    
    private Set fromStr(SSClobString membersStr) {
        if(membersStr == null)
            return null;
        
        String[] tokens = membersStr.getText().split(COMMA);
        Set result = new HashSet();
        for(int i = 0; i < tokens.length; i++) {
            String token = tokens[i].trim();
            if(token.length() > 0) {
                result.add(Long.valueOf(token));
            }   
        }
        
        return result;
    }
}
