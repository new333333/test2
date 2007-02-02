package com.sitescape.ef.module.profile;

import java.util.List;
import java.util.Map;

import com.sitescape.ef.module.binder.EntryProcessor;
import com.sitescape.ef.module.shared.InputDataAccessor;
import com.sitescape.team.domain.Binder;
import com.sitescape.team.domain.Definition;
import com.sitescape.team.domain.Principal;
public interface ProfileCoreProcessor extends EntryProcessor {
	public void syncEntry(Principal entry, InputDataAccessor inputData);
	public void syncEntries(Map entries);
	public List syncNewEntries(Binder binder, Definition definition, Class clazz, List inputAccessors); 
}
