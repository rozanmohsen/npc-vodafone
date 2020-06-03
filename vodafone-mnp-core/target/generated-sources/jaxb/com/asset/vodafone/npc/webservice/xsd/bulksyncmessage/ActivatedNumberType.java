//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.11 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2020.05.20 at 06:38:10 PM EET 
//


package com.asset.vodafone.npc.webservice.xsd.bulksyncmessage;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ActivatedNumberType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ActivatedNumberType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;all&gt;
 *         &lt;element name="IDNumber" type="{}IDNumber"/&gt;
 *         &lt;element name="NumberFrom" type="{}TelephoneNumber"/&gt;
 *         &lt;element name="NumberTo" type="{}TelephoneNumber"/&gt;
 *         &lt;element name="DonorId" type="{}ParticipantId" minOccurs="0"/&gt;
 *         &lt;element name="RecipientId" type="{}ParticipantId"/&gt;
 *         &lt;element name="NewRoute" type="{}NewRoute" minOccurs="0"/&gt;
 *         &lt;element name="Action" type="{}Action"/&gt;
 *       &lt;/all&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ActivatedNumberType", propOrder = {

})
public class ActivatedNumberType {

    @XmlElement(name = "IDNumber", required = true)
    protected String idNumber;
    @XmlElement(name = "NumberFrom", required = true)
    protected String numberFrom;
    @XmlElement(name = "NumberTo", required = true)
    protected String numberTo;
    @XmlElement(name = "DonorId")
    protected String donorId;
    @XmlElement(name = "RecipientId", required = true)
    protected String recipientId;
    @XmlElement(name = "NewRoute")
    protected String newRoute;
    @XmlElement(name = "Action", required = true)
    protected String action;

    /**
     * Gets the value of the idNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIDNumber() {
        return idNumber;
    }

    /**
     * Sets the value of the idNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIDNumber(String value) {
        this.idNumber = value;
    }

    /**
     * Gets the value of the numberFrom property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNumberFrom() {
        return numberFrom;
    }

    /**
     * Sets the value of the numberFrom property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNumberFrom(String value) {
        this.numberFrom = value;
    }

    /**
     * Gets the value of the numberTo property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNumberTo() {
        return numberTo;
    }

    /**
     * Sets the value of the numberTo property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNumberTo(String value) {
        this.numberTo = value;
    }

    /**
     * Gets the value of the donorId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDonorId() {
        return donorId;
    }

    /**
     * Sets the value of the donorId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDonorId(String value) {
        this.donorId = value;
    }

    /**
     * Gets the value of the recipientId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRecipientId() {
        return recipientId;
    }

    /**
     * Sets the value of the recipientId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRecipientId(String value) {
        this.recipientId = value;
    }

    /**
     * Gets the value of the newRoute property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNewRoute() {
        return newRoute;
    }

    /**
     * Sets the value of the newRoute property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNewRoute(String value) {
        this.newRoute = value;
    }

    /**
     * Gets the value of the action property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAction() {
        return action;
    }

    /**
     * Sets the value of the action property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAction(String value) {
        this.action = value;
    }

}
