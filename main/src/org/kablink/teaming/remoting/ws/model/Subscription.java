package org.kablink.teaming.remoting.ws.model;
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
	public void addStyle(int style, String[]emailTypes) {
		subscriptionMap.put(Integer.valueOf(style), new SubscriptionStyle(style, emailTypes));
	}
	public void addStyle(SubscriptionStyle subscription) {
		subscriptionMap.put(Integer.valueOf(subscription.getStyle()), subscription);
	}
	public void removeStyle(int style) {
		subscriptionMap.remove(Integer.valueOf(style));
	}
	public SubscriptionStyle[] getStyles() {
		SubscriptionStyle[] array = new SubscriptionStyle[subscriptionMap.size()];
		return subscriptionMap.values().toArray(array);
	}
	public void setStyles(SubscriptionStyle[] subscriptions) {
		this.subscriptionMap = new HashMap<Integer,SubscriptionStyle>();
		if (subscriptions != null) {
			for(int i = 0; i < subscriptions.length; i++) {
				this.subscriptionMap.put(Integer.valueOf(subscriptions[i].getStyle()), subscriptions[i]);
			}
		}
	}
}
