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
package com.sitescape.team.module.profile;

import java.util.List;
import java.util.Map;

import com.sitescape.team.domain.Binder;
import com.sitescape.team.domain.Definition;
import com.sitescape.team.domain.Principal;
import com.sitescape.team.module.binder.EntryProcessor;
import com.sitescape.team.module.shared.InputDataAccessor;
public interface ProfileCoreProcessor extends EntryProcessor {
	public void syncEntry(Principal entry, InputDataAccessor inputData);
	public void syncEntries(Map entries);
	public List syncNewEntries(Binder binder, Definition definition, Class clazz, List inputAccessors); 
}
