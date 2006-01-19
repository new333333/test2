package com.sitescape.ef.domain;

/**
 * @author Jong Kim
 * @hibernate.subclass discriminator-value="U" dynamic-update="true"
 * 
 * This object is created by users that are registering to receive email
 */
public class UserNotification extends Notification {
    public static final int DIGEST_STYLE_EMAIL_NOTIFICATION = 1;
    public static final int MESSAGE_STYLE_EMAIL_NOTIFICATION = 2;
   
    private int style=DIGEST_STYLE_EMAIL_NOTIFICATION;
    private boolean disabled=false;
    /**
     * @hibernate.property
     * @return
     */
    public int getStyle() {
        return style;
    }
    public void setStyle(int style) {
        this.style = style;
    }
    /**
     * This allows users to explicity turn off notification, which would be needed
     * if they are enabled by an admin.
     * @hibernate.property
     * @return
     */
    public boolean isDisabled() {
        return this.disabled;
    }
    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }
 
}
