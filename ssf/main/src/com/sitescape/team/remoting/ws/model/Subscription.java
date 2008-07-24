package com.sitescape.team.remoting.ws.model;
import java.io.Serializable;
import java.util.HashMap;

public class Subscription implements Serializable {
	//storage only, externally as an array
	private HashMap<Integer, SubscriptionStyle> subscriptionMap = new HashMap();
	private Long entityId;
	
	public Long getEntityId() {
		return entityId;
	}
	public void setEntityId(Long entityId) {
		this.entityId = entityId;
	}
	public SubscriptionStyle getStyle(Integer style) {
		return subscriptionMap.get(style);
	}
	public void addStyle(Integer style, String[]emailTypes) {
		subscriptionMap.put(style, new SubscriptionStyle(style, emailTypes));
	}
	public void addStyle(SubscriptionStyle subscription) {
		subscriptionMap.put(subscription.getStyle(), subscription);
	}
	public void removeStyle(Integer style) {
		subscriptionMap.remove(style);
	}
	public SubscriptionStyle[] getStyles() {
		SubscriptionStyle[] array = new SubscriptionStyle[subscriptionMap.size()];
		return subscriptionMap.values().toArray(array);
	}
	public void setStyles(SubscriptionStyle[] subscriptions) {
		this.subscriptionMap = new HashMap<Integer,SubscriptionStyle>();
		if (subscriptions != null) {
			for(int i = 0; i < subscriptions.length; i++) {
				this.subscriptionMap.put(subscriptions[i].getStyle(), subscriptions[i]);
			}
		}
	}
}
