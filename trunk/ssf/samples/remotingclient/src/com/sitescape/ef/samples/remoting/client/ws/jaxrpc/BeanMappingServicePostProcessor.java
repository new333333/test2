package com.sitescape.team.samples.remoting.client.ws.jaxrpc;

import javax.xml.namespace.QName;
import javax.xml.rpc.Service;
import javax.xml.rpc.encoding.TypeMapping;
import javax.xml.rpc.encoding.TypeMappingRegistry;

import org.apache.axis.encoding.ser.BeanDeserializerFactory;
import org.apache.axis.encoding.ser.BeanSerializerFactory;
import org.springframework.remoting.jaxrpc.JaxRpcServicePostProcessor;

import com.sitescape.team.remoting.api.Binder;
import com.sitescape.team.remoting.api.Folder;

/**
 * Provides custom type mappings for Axis to use to serialize/deserialize
 * application objects. 
 * <p>
 * The <code>JaxRpcServicePostProcessor</code> callback interface itself for 
 * post-processing a JAX-RPC Service does NOT depend upon specific tools such 
 * as Axis. However, this particular implementation utilizes the serializer/
 * deserializer functionality provided by Axis.
 *
 * @author jong
 *
 */
public class BeanMappingServicePostProcessor implements JaxRpcServicePostProcessor {

	// In our samples, this class is used in conjunction with Spring's jaxrpc 
	// proxy, and defined in clientContext-jaxrpc.xml.
	
	public void postProcessJaxRpcService(Service service) {
		TypeMappingRegistry registry = service.getTypeMappingRegistry();
		TypeMapping mapping = registry.createTypeMapping();
		registerBeanMapping(mapping, Binder.class, "Binder");
		registerBeanMapping(mapping, Folder.class, "Folder");
		registry.register("http://schemas.xmlsoap.org/soap/encoding/", mapping);
	}

	protected void registerBeanMapping(TypeMapping mapping, Class type, String name) {
		QName qName = new QName("urn:SSF", name);
		mapping.register(type, qName,
				new BeanSerializerFactory(type, qName),
				new BeanDeserializerFactory(type, qName));
	}
}
