//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.11 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2020.07.05 at 11:31:41 AM EET 
//


package com.asset.vodafone.npc.webservice.xsd.portmessage;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for NumberDataType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="NumberDataType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="NumberFrom" type="{}TelephoneNumber"/&gt;
 *         &lt;element name="NumberTo" type="{}TelephoneNumber" minOccurs="0"/&gt;
 *         &lt;element name="DataNumber" type="{}TelephoneNumber" minOccurs="0"/&gt;
 *         &lt;element name="FaxNumber" type="{}TelephoneNumber" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "NumberDataType", propOrder = {
    "numberFrom",
    "numberTo",
    "dataNumber",
    "faxNumber"
})
public class NumberDataType {

    @XmlElement(name = "NumberFrom", required = true)
    protected String numberFrom;
    @XmlElement(name = "NumberTo")
    protected String numberTo;
    @XmlElement(name = "DataNumber")
    protected String dataNumber;
    @XmlElement(name = "FaxNumber")
    protected String faxNumber;

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
     * Gets the value of the dataNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDataNumber() {
        return dataNumber;
    }

    /**
     * Sets the value of the dataNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDataNumber(String value) {
        this.dataNumber = value;
    }

    /**
     * Gets the value of the faxNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFaxNumber() {
        return faxNumber;
    }

    /**
     * Sets the value of the faxNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFaxNumber(String value) {
        this.faxNumber = value;
    }

}
