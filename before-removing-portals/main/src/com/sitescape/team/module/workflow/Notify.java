/**
 * The contents of this file are governed by the terms of your license
 * with SiteScape, Inc., which includes disclaimers of warranties and
 * limitations on liability. You may not use this file except in accordance
 * with the terms of that license. See the license for the specific language
 * governing your rights and limitations under the license.
 *
 * Copyright (c) 2007 SiteScape, Inc.
 *
 */
package com.sitescape.team.module.workflow;

import org.jbpm.graph.exe.ExecutionContext;
/**
 * @deprecated
 * @author Janet
 *Move to EnterExitEvent so permailinks are valid after a move.  This
 *would run before the move and the permalink would be bad.
 */
public class Notify extends AbstractActionHandler {
	private static final long serialVersionUID = 1L;
	public void execute( ExecutionContext executionContext ) throws Exception {
	}
}
