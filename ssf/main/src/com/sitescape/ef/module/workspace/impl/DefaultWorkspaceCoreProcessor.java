package com.sitescape.ef.module.workspace.impl;

import com.sitescape.ef.InternalException;
import com.sitescape.ef.domain.Binder;
import com.sitescape.ef.domain.Workspace;
import com.sitescape.ef.module.binder.impl.AbstractBinderProcessor;

public class DefaultWorkspaceCoreProcessor extends AbstractBinderProcessor {
    public void moveBinder(Binder source, Binder destination) {
    	if (source.getParentBinder() == null)
    		throw new InternalException("Cannot move top workspace");
    	if (!(destination instanceof Workspace))
       		throw new InternalException("Workspaces can only be move to other workspaces");
    	super.moveBinder(source, destination);
     }

}
