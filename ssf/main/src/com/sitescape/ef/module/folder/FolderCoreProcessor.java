package com.sitescape.ef.module.folder;

import java.util.Map;

import org.dom4j.Document;

import com.sitescape.ef.domain.Folder;
import com.sitescape.ef.domain.Definition;
import com.sitescape.ef.domain.FolderEntry;
import com.sitescape.ef.module.binder.EntryProcessor;
import com.sitescape.ef.module.file.WriteFilesException;
import com.sitescape.ef.module.shared.DomTreeBuilder;
import com.sitescape.ef.module.shared.InputDataAccessor;
import com.sitescape.ef.security.AccessControlException;

/**
 * <code>ForumCoreProcessor</code> is a model processor for forum, which
 * defines a set of core operations around forum. 
 * 
 * @author Jong Kim
 */
public interface FolderCoreProcessor extends EntryProcessor {

    public Long addReply(FolderEntry parent, Definition def, InputDataAccessor inputData, Map fileItems) 
    	throws AccessControlException, WriteFilesException;
    public Map getEntryTree(Folder parentFolderId, FolderEntry entry) throws AccessControlException;
}
