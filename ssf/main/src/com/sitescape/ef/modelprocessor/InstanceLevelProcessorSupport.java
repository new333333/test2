package com.sitescape.ef.modelprocessor;

/**
 *
 * @author Jong Kim
 */
public interface InstanceLevelProcessorSupport {

    /**
     * 
     * @param processorKey
     * @return the class name of the processor associated with the instance.
     * may be <code>null</code>
     */
    public String getProcessorClassName(String processorKey);
    
    public void setProcessorClassName(String processorKey, String processorClassName);
}
