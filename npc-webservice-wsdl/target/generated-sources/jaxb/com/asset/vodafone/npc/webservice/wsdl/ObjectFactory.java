//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.11 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2020.08.12 at 02:26:16 PM EET 
//


package com.asset.vodafone.npc.webservice.wsdl;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the com.asset.vodafone.npc.webservice.wsdl package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _ProcessNPCMsg_QNAME = new QName("http://npc.gizasystems.com/", "processNPCMsg");
    private final static QName _ProcessNPCMsgResponse_QNAME = new QName("http://npc.gizasystems.com/", "processNPCMsgResponse");
    private final static QName _ProcessNPCMsgArrayOfbyte2_QNAME = new QName("", "arrayOfbyte_2");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: com.asset.vodafone.npc.webservice.wsdl
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link ProcessNPCMsg }
     * 
     */
    public ProcessNPCMsg createProcessNPCMsg() {
        return new ProcessNPCMsg();
    }

    /**
     * Create an instance of {@link ProcessNPCMsgResponse }
     * 
     */
    public ProcessNPCMsgResponse createProcessNPCMsgResponse() {
        return new ProcessNPCMsgResponse();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ProcessNPCMsg }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://npc.gizasystems.com/", name = "processNPCMsg")
    public JAXBElement<ProcessNPCMsg> createProcessNPCMsg(ProcessNPCMsg value) {
        return new JAXBElement<ProcessNPCMsg>(_ProcessNPCMsg_QNAME, ProcessNPCMsg.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ProcessNPCMsgResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://npc.gizasystems.com/", name = "processNPCMsgResponse")
    public JAXBElement<ProcessNPCMsgResponse> createProcessNPCMsgResponse(ProcessNPCMsgResponse value) {
        return new JAXBElement<ProcessNPCMsgResponse>(_ProcessNPCMsgResponse_QNAME, ProcessNPCMsgResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link byte[]}{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "arrayOfbyte_2", scope = ProcessNPCMsg.class)
    public JAXBElement<byte[]> createProcessNPCMsgArrayOfbyte2(byte[] value) {
        return new JAXBElement<byte[]>(_ProcessNPCMsgArrayOfbyte2_QNAME, byte[].class, ProcessNPCMsg.class, ((byte[]) value));
    }

}
