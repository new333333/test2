package com.sitescape.ef.security.function;

import java.util.List;
import java.util.Set;
import java.util.Iterator;

import com.sitescape.ef.security.dao.SecurityDao;

/**
 *
 * @author Jong Kim
 */
public class WorkAreaFunctionMembershipManagerImpl implements WorkAreaFunctionMembershipManager {

    private SecurityDao securityDao;
    
    public SecurityDao getSecurityDao() {
        return securityDao;
    }
    public void setSecurityDao(SecurityDao securityDao) {
        this.securityDao = securityDao;
    }

    public void addWorkAreaFunctionMembership(WorkAreaFunctionMembership functionMembership) {
        getSecurityDao().save(functionMembership);
    }

    public void deleteWorkAreaFunctionMemberships(Long zoneId, WorkArea workArea) {
    	getSecurityDao().deleteWorkAreaFunctionMemberships(zoneId, workArea.getWorkAreaId(), workArea.getWorkAreaType());
    }
    public void deleteWorkAreaFunctionMembership(WorkAreaFunctionMembership functionMembership) {
        getSecurityDao().delete(functionMembership);
    }

    public void updateWorkAreaFunctionMembership(WorkAreaFunctionMembership functionMembership) {
        getSecurityDao().update(functionMembership);
    }

    public WorkAreaFunctionMembership getWorkAreaFunctionMembership(Long zoneId, WorkArea workArea, Long functionId) {
        return getSecurityDao().getWorkAreaFunctionMembership
    	(zoneId, workArea.getWorkAreaId(), workArea.getWorkAreaType(), functionId);
    }

    public List findWorkAreaFunctionMemberships(Long zoneId, WorkArea workArea) {
        return getSecurityDao().findWorkAreaFunctionMemberships
        	(zoneId, workArea.getWorkAreaId(), workArea.getWorkAreaType());
    }
    //Find workareas using a specific function.
    public List findWorkAreaFunctionMemberships(Long zoneId, Long functionId) {
        return getSecurityDao().findWorkAreaFunctionMemberships(zoneId, functionId);
    }
    //Find workareas assigning this user to a specific function.  Used to implement
    //what workspaces am I a team member of.
    public List findWorkAreaFunctionMemberships(Long zoneId, Set membersToLookup, Long functionId) {
        return getSecurityDao().findWorkAreaFunctionMemberships(zoneId, functionId, membersToLookup);
    }
    /*
    public boolean checkWorkAreaFunctionMembership(Long zoneName, WorkArea workArea, 
            Set membersToLookup, List functions) {
        // There are a number of different approaches we can take in implementing 
        // this method. This specific implementation relies on the query cache
        // and the observation that the number of different combinations of the
        // paramaters is relatively small and the most heavily sought-after 
        // results will remain in the cache. We will have to measure the 
        // efficiency of this approach to see if it is acceptable. 
        
        
        // TODO To be rewritten...
        
        List waFunctionMemberships = 
            getSecurityDao().findWorkAreaFunctionMembership
            (zoneName, workArea.getId(), functions);
        int size = waFunctionMemberships.size();
        for(int i = 0; i < size; i++) {
            Set memberIds = ((WorkAreaFunctionMembership) waFunctionMemberships.get(i)).getMemberIds();
            for(Iterator it = membersToLookup.iterator(); it.hasNext();) {
                if(memberIds.contains(it.next()))
                    return true;
            }
        }
        return false;
    }*/
    
    public boolean checkWorkAreaFunctionMembership(Long zoneId, WorkArea workArea, 
            WorkAreaOperation workAreaOperation, Set membersToLookup) {
        return getSecurityDao().checkWorkAreaFunctionMembership
        	(zoneId, workArea.getWorkAreaId(), workArea.getWorkAreaType(), 
        	        workAreaOperation.getName(), membersToLookup);
    }
    //see if user is a member of a role - don't care about rights given to role
    //Used to implement am I a member of this team?
    public boolean checkWorkAreaFunctionMembership(Long zoneId, WorkArea workArea, 
            Long functionId, Set membersToLookup) {
        WorkAreaFunctionMembership wfm = getWorkAreaFunctionMembership(zoneId, workArea, functionId);
        if (wfm == null) return false;
        Set<Long> ids = wfm.getMemberIds();
        for (Iterator iter=membersToLookup.iterator(); iter.hasNext();) {
        	Long id = (Long)iter.next();
        	if (ids.contains(id)) return true;
        }
        return false;
    }
}