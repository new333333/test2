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

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.kablink.teaming.security.acl.AccessType;
import org.kablink.teaming.security.acl.AclSet;


/**
 * <code>AclSet</code> maintains a set of principal ids for each access type.
 * 
 * @author Jong Kim
 */
public class PersistentAclSet implements AclSet {

    private static final String COMMA = ",";
    
    // The following fields are persisted. 
    private SSClobString readMembers;
    private SSClobString writeMembers;
    private SSClobString deleteMembers;
    private SSClobString changeAclMembers;
    
    // The following fields are not persisted.
    protected Set readMemberIds;
    protected Set writeMemberIds;
    protected Set deleteMemberIds;
    protected Set changeAclMemberIds;
    
    /**
     * @hibernate.property type="org.kablink.teaming.dao.util.SSClobStringType"
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
     * @hibernate.property type="org.kablink.teaming.dao.util.SSClobStringType"
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
     * @hibernate.property type="org.kablink.teaming.dao.util.SSClobStringType"
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
     * @hibernate.property type="org.kablink.teaming.dao.util.SSClobStringType"
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
    public void addReadMemberId(Long memberId) {
        this.getReadMemberIds().add(memberId);
    }
    public void addReadMemberIds(Set memberIds) {
        this.getReadMemberIds().addAll(memberIds);
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
    public void addWriteMemberIds(Set memberIds) {
        this.getWriteMemberIds().addAll(memberIds);
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
    public void addDeleteMemberIds(Set memberIds) {
        this.getDeleteMemberIds().addAll(memberIds);
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
    public void addChangeAclMemberIds(Set memberIds) {
        this.getChangeAclMemberIds().addAll(memberIds);
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
        PersistentAclSet copy = new PersistentAclSet();
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
