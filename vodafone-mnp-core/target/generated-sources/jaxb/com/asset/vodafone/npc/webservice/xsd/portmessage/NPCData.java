//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.11 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2020.05.20 at 06:38:10 PM EET 
//


package com.asset.vodafone.npc.webservice.xsd.portmessage;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="NPCMessages" type="{}NPCMessageType"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "npcMessages"
})
@XmlRootElement(name = "NPCData")
public class NPCData {

    @XmlElement(name = "NPCMessages", required = true)
    protected NPCMessageType npcMessages;

    /**
     * Gets the value of the npcMessages property.
     * 
     * @return
     *     possible object is
     *     {@link NPCMessageType }
     *     
     */
    public NPCMessageType getNPCMessages() {
        return npcMessages;
    }

    /**
     * Sets the value of the npcMessages property.
     * 
     * @param value
     *     allowed object is
     *     {@link NPCMessageType }
     *     
     */
    public void setNPCMessages(NPCMessageType value) {
        this.npcMessages = value;
    }

}
