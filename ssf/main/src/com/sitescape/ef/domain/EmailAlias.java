package com.sitescape.ef.domain;

/**
 * @author Jong Kim
 *
 */
public class EmailAlias {
    /**
     * Email alias name, eg. foo@mail.bar.com.
     */
    private String aliasName;
    
    public String getAliasName() {
        return aliasName;
    }
    public void setAliasName(String aliasName) {
        this.aliasName = aliasName;
    }
}
