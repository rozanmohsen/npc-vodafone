package com.asset.vodafone.npc.webservice.client;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;


@Configuration
public class NPCWebserviceProxyConfiguration {
	 
	@Bean
	public Jaxb2Marshaller marshaller() {
		Jaxb2Marshaller jaxb2Marshaller = new Jaxb2Marshaller();
		jaxb2Marshaller.setContextPath("com.asset.vodafone.npc.webservice.wsdl");
		return jaxb2Marshaller;
	}

	@Bean
	public NPCWebserviceProxy npcWebserviceInterfaceProxy(Jaxb2Marshaller jaxb2Marshaller)  {
		
		NPCWebserviceProxy npcWebserviceProxy = new NPCWebserviceProxy();
		
		npcWebserviceProxy.setMarshaller(jaxb2Marshaller);
		npcWebserviceProxy.setUnmarshaller(jaxb2Marshaller);
		return npcWebserviceProxy;
	}

}
