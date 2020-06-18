//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.11 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2020.06.17 at 12:28:12 PM EET 
//


package com.asset.vodafone.npc.webservice.xsd.portmessage;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for BulkSyncMessageType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="BulkSyncMessageType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="MessageID" type="{}Len20Str"/&gt;
 *         &lt;element name="MessageCode" type="{}MessageCode"/&gt;
 *         &lt;element name="SyncID" type="{}IDNumber" minOccurs="0"/&gt;
 *         &lt;element name="StartDate" type="{}NPDateFormat" minOccurs="0"/&gt;
 *         &lt;element name="EndDate" type="{}NPDateFormat" minOccurs="0"/&gt;
 *         &lt;element name="Comments1" type="{}Comments" minOccurs="0"/&gt;
 *         &lt;element name="Comments2" type="{}Comments" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "BulkSyncMessageType", propOrder = {
    "messageID",
    "messageCode",
    "syncID",
    "startDate",
    "endDate",
    "comments1",
    "comments2"
})
public class BulkSyncMessageType {

    @XmlElement(name = "MessageID", required = true)
    protected String messageID;
    @XmlElement(name = "MessageCode", required = true)
    protected String messageCode;
    @XmlElement(name = "SyncID")
    protected String syncID;
    @XmlElement(name = "StartDate")
    protected String startDate;
    @XmlElement(name = "EndDate")
    protected String endDate;
    @XmlElement(name = "Comments1")
    protected String comments1;
    @XmlElement(name = "Comments2")
    protected String comments2;

    /**
     * Gets the value of the messageID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMessageID() {
        return messageID;
    }

    /**
     * Sets the value of the messageID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMessageID(String value) {
        this.messageID = value;
    }

    /**
     * Gets the value of the messageCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMessageCode() {
        return messageCode;
    }

    /**
     * Sets the value of the messageCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMessageCode(String value) {
        this.messageCode = value;
    }

    /**
     * Gets the value of the syncID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSyncID() {
        return syncID;
    }

    /**
     * Sets the value of the syncID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSyncID(String value) {
        this.syncID = value;
    }

    /**
     * Gets the value of the startDate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStartDate() {
        return startDate;
    }

    /**
     * Sets the value of the startDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStartDate(String value) {
        this.startDate = value;
    }

    /**
     * Gets the value of the endDate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEndDate() {
        return endDate;
    }

    /**
     * Sets the value of the endDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEndDate(String value) {
        this.endDate = value;
    }

    /**
     * Gets the value of the comments1 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getComments1() {
        return comments1;
    }

    /**
     * Sets the value of the comments1 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setComments1(String value) {
        this.comments1 = value;
    }

    /**
     * Gets the value of the comments2 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getComments2() {
        return comments2;
    }

    /**
     * Sets the value of the comments2 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setComments2(String value) {
        this.comments2 = value;
    }

}
