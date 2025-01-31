//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.11 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2020.08.19 at 03:35:43 PM EET 
//


package com.asset.vodafone.npc.webservice.xsd.portmessage;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for NPCMessageType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="NPCMessageType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;choice maxOccurs="100"&gt;
 *         &lt;element name="PortMessage" type="{}PortMessageType" minOccurs="0"/&gt;
 *         &lt;element name="BulkSyncMessage" type="{}BulkSyncMessageType" minOccurs="0"/&gt;
 *       &lt;/choice&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "NPCMessageType", propOrder = {
    "portMessageOrBulkSyncMessage"
})
public class NPCMessageType {

    @XmlElements({
        @XmlElement(name = "PortMessage", type = PortMessageType.class),
        @XmlElement(name = "BulkSyncMessage", type = BulkSyncMessageType.class)
    })
    protected List<Object> portMessageOrBulkSyncMessage;

    /**
     * Gets the value of the portMessageOrBulkSyncMessage property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the portMessageOrBulkSyncMessage property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPortMessageOrBulkSyncMessage().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link PortMessageType }
     * {@link BulkSyncMessageType }
     * 
     * 
     */
    public List<Object> getPortMessageOrBulkSyncMessage() {
        if (portMessageOrBulkSyncMessage == null) {
            portMessageOrBulkSyncMessage = new ArrayList<Object>();
        }
        return this.portMessageOrBulkSyncMessage;
    }

}
