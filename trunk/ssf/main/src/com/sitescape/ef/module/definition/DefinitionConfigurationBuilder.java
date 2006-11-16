package com.sitescape.ef.module.definition;

import com.sitescape.ef.util.DefaultMergeableXmlClassPathConfigFiles;

public class DefinitionConfigurationBuilder extends
		DefaultMergeableXmlClassPathConfigFiles {
    public void afterPropertiesSet() throws Exception {
        super.afterPropertiesSet();

        //TODO: add any caching we want
    }

}
