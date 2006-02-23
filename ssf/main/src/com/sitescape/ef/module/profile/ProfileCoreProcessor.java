package com.sitescape.ef.module.profile;

import java.util.List;
import java.util.Map;

import com.sitescape.ef.domain.Binder;
import com.sitescape.ef.domain.Principal;
import com.sitescape.ef.domain.Definition;
import com.sitescape.ef.module.binder.EntryProcessor;
import com.sitescape.ef.module.shared.InputDataAccessor;
public interface ProfileCoreProcessor extends EntryProcessor {
	public void syncEntry(Principal entry, InputDataAccessor inputData);
	public void syncEntries(Map entries);
	public List syncNewEntries(Binder binder, Definition definition, Class clazz, List inputAccessors); 
}
