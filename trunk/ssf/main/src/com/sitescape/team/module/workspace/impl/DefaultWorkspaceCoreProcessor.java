package com.sitescape.team.module.workspace.impl;

import com.sitescape.ef.NotSupportedException;
import com.sitescape.team.domain.Binder;
import com.sitescape.team.domain.Workspace;
import com.sitescape.team.module.binder.impl.AbstractBinderProcessor;
import com.sitescape.team.util.NLT;

public class DefaultWorkspaceCoreProcessor extends AbstractBinderProcessor {
    public void moveBinder(Binder source, Binder destination) {
    	if (!(destination instanceof Workspace))
        	throw new NotSupportedException(NLT.get("errorcode.notsupported.moveBinderDestination", new String[] {destination.getPathName()}));
    	super.moveBinder(source, destination);
     }

}
