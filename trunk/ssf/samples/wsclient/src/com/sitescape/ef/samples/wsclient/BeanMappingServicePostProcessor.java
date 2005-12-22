package com.sitescape.ef.samples.wsclient;

import javax.xml.namespace.QName;
import javax.xml.rpc.Service;
import javax.xml.rpc.encoding.TypeMapping;
import javax.xml.rpc.encoding.TypeMappingRegistry;

import org.apache.axis.encoding.ser.BeanDeserializerFactory;
import org.apache.axis.encoding.ser.BeanSerializerFactory;
import org.springframework.remoting.jaxrpc.JaxRpcServicePostProcessor;

import com.sitescape.ef.remoting.api.Entry;

/**
 * Provides custom type mappings for Axis to use to serialize/deserialize
 * application objects. 
 * 
 * @author jong
 *
 */
public class BeanMappingServicePostProcessor implements JaxRpcServicePostProcessor {

	public void postProcessJaxRpcService(Service service) {
		TypeMappingRegistry registry = service.getTypeMappingRegistry();
		TypeMapping mapping = registry.createTypeMapping();
		registerBeanMapping(mapping, Entry.class, "Entry");
		registry.register("http://schemas.xmlsoap.org/soap/encoding/", mapping);
	}

	protected void registerBeanMapping(TypeMapping mapping, Class type, String name) {
		QName qName = new QName("urn:SSF", name);
		mapping.register(type, qName,
				new BeanSerializerFactory(type, qName),
				new BeanDeserializerFactory(type, qName));
	}
}
