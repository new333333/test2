package com.sitescape.ef.security.function;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * <code>Operation</code> class defines an operation that can be performed
 * to a <code>WorkArea</code>.
 * <p>
 * Operation definitions are global within a zone, which means that
 * there do not exist separate operation definitions for each work area.
 * <p>
 * <code>Operation</code> is extensible. 
 *
 * @author Jong Kim
 */
public class WorkAreaOperation {

    // It is critically important to have this map instantiated here
    // BEFORE pre-defined WorkAreOperation instances are created. 
    private static final Map Instances = new HashMap();

    //Workarea operations
    //  Important: do not create operations that control the viewing of an entry of folder.
    //  Such "view" operations should be controlled by the ACL.
    public final static WorkAreaOperation CREATE_ENTRIES = new WorkAreaOperation("createEntries");
    public final static WorkAreaOperation BINDER_ADMIN = new WorkAreaOperation("binderAdmin");    
    public final static WorkAreaOperation MODIFY_ENTRIES = new WorkAreaOperation("modifyEntries");    
    public final static WorkAreaOperation DELETE_ENTRIES = new WorkAreaOperation("deleteEntries");    
    public final static WorkAreaOperation READ_ENTRIES = new WorkAreaOperation("readEntries");
    public final static WorkAreaOperation ADD_REPLIES = new WorkAreaOperation("addReplies");
    public final static WorkAreaOperation GENERATE_REPORTS = new WorkAreaOperation("generateReports");
    public final static WorkAreaOperation SITE_ADMINISTRATION = new WorkAreaOperation("siteAdministration");
    public final static WorkAreaOperation CHANGE_ACCESS_CONTROL = new WorkAreaOperation("changeAccessControl");
    public final static WorkAreaOperation CREATE_FOLDERS = new WorkAreaOperation("createFolders");
    public final static WorkAreaOperation MANAGE_ENTRY_DEFINITIONS = new WorkAreaOperation("manageEntryDefinitions");
    public final static WorkAreaOperation MANAGE_WORKFLOW_DEFINITIONS = new WorkAreaOperation("manageWorkflowDefinitions");
    public final static WorkAreaOperation CREATOR_MODIFY = new WorkAreaOperation("creatorModifyEntries");
    public final static WorkAreaOperation CREATOR_DELETE = new WorkAreaOperation("creatorDeleteEntries");
    public final static WorkAreaOperation CREATOR_READ = new WorkAreaOperation("creatorReadEntries");
    
    private String name;
    
    private WorkAreaOperation(String name) {
        this.name = name;
        
        Instances.put(name, this);
    }
    
    public String getName() {
        return name;
    }
    
    public static WorkAreaOperation getInstance(String name) {
        WorkAreaOperation op = (WorkAreaOperation) Instances.get(name);
        if(op == null) {
            op = new WorkAreaOperation(name);
        }
        return op;
    }
    
    /*
    public boolean equals(Object obj) {
        if(obj instanceof WorkAreaOperation)
            return this.name.equals(((WorkAreaOperation) obj).name);
        else 
            return false;
    }
    
    public int hashCode() {
        return this.name.hashCode();
    }*/
    
    public String toString() {
        return getName();
    }
    
    /**
     * Returns an iterator over <code>WorkAreaOperation</code>s.
     * The returned datastructure should never be modified by the caller. 
     * 
     * @return
     */
    public static Iterator getWorkAreaOperations() {
    	return Instances.values().iterator();
    }

}
