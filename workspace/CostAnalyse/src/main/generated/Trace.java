//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2015.05.13 um 10:15:52 AM CEST 
//


package main.generated;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für anonymous complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="parameter" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="cloud" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="dependencies" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="historicaldata" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="categories">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="predictioncategory" maxOccurs="unbounded" minOccurs="0">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element name="shortestmakespan" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *                             &lt;element name="cheapestcost" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *                             &lt;element name="deadlinecategories">
 *                               &lt;complexType>
 *                                 &lt;complexContent>
 *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                                     &lt;sequence>
 *                                       &lt;element name="deadlinecategory" maxOccurs="unbounded" minOccurs="0">
 *                                         &lt;complexType>
 *                                           &lt;complexContent>
 *                                             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                                               &lt;sequence>
 *                                                 &lt;element name="plan">
 *                                                   &lt;complexType>
 *                                                     &lt;complexContent>
 *                                                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                                                         &lt;sequence>
 *                                                           &lt;element name="maxAlpha" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *                                                           &lt;element name="maxAlphaMakespan" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *                                                           &lt;element name="deadline" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *                                                           &lt;element name="plannedmakespan" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *                                                           &lt;element name="plannedcost" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *                                                           &lt;element name="cheapestplannedcost" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *                                                         &lt;/sequence>
 *                                                       &lt;/restriction>
 *                                                     &lt;/complexContent>
 *                                                   &lt;/complexType>
 *                                                 &lt;/element>
 *                                                 &lt;element name="runs">
 *                                                   &lt;complexType>
 *                                                     &lt;complexContent>
 *                                                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                                                         &lt;sequence>
 *                                                           &lt;element name="run" maxOccurs="unbounded" minOccurs="0">
 *                                                             &lt;complexType>
 *                                                               &lt;complexContent>
 *                                                                 &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                                                                   &lt;sequence>
 *                                                                     &lt;element name="static">
 *                                                                       &lt;complexType>
 *                                                                         &lt;complexContent>
 *                                                                           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                                                                             &lt;sequence>
 *                                                                               &lt;element name="cost" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *                                                                               &lt;element name="deadline" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *                                                                               &lt;element name="makespan" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *                                                                               &lt;element name="duration" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *                                                                             &lt;/sequence>
 *                                                                           &lt;/restriction>
 *                                                                         &lt;/complexContent>
 *                                                                       &lt;/complexType>
 *                                                                     &lt;/element>
 *                                                                     &lt;element name="dynamic">
 *                                                                       &lt;complexType>
 *                                                                         &lt;complexContent>
 *                                                                           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                                                                             &lt;sequence>
 *                                                                               &lt;element name="cost" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *                                                                               &lt;element name="deadline" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *                                                                               &lt;element name="makespan" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *                                                                               &lt;element name="duration" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *                                                                             &lt;/sequence>
 *                                                                           &lt;/restriction>
 *                                                                         &lt;/complexContent>
 *                                                                       &lt;/complexType>
 *                                                                     &lt;/element>
 *                                                                     &lt;element name="real">
 *                                                                       &lt;complexType>
 *                                                                         &lt;complexContent>
 *                                                                           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                                                                             &lt;sequence>
 *                                                                               &lt;element name="cheapestcost" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *                                                                               &lt;element name="fasttime" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *                                                                             &lt;/sequence>
 *                                                                           &lt;/restriction>
 *                                                                         &lt;/complexContent>
 *                                                                       &lt;/complexType>
 *                                                                     &lt;/element>
 *                                                                   &lt;/sequence>
 *                                                                   &lt;attribute name="exTimes" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                                                                 &lt;/restriction>
 *                                                               &lt;/complexContent>
 *                                                             &lt;/complexType>
 *                                                           &lt;/element>
 *                                                         &lt;/sequence>
 *                                                       &lt;/restriction>
 *                                                     &lt;/complexContent>
 *                                                   &lt;/complexType>
 *                                                 &lt;/element>
 *                                               &lt;/sequence>
 *                                               &lt;attribute name="deadlinefactor" use="required" type="{http://www.w3.org/2001/XMLSchema}double" />
 *                                             &lt;/restriction>
 *                                           &lt;/complexContent>
 *                                         &lt;/complexType>
 *                                       &lt;/element>
 *                                     &lt;/sequence>
 *                                   &lt;/restriction>
 *                                 &lt;/complexContent>
 *                               &lt;/complexType>
 *                             &lt;/element>
 *                           &lt;/sequence>
 *                           &lt;attribute name="method" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                           &lt;attribute name="value" use="required" type="{http://www.w3.org/2001/XMLSchema}double" />
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *       &lt;attribute name="runid" type="{http://www.w3.org/2001/XMLSchema}long" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "parameter",
    "cloud",
    "dependencies",
    "historicaldata",
    "categories"
})
@XmlRootElement(name = "trace")
public class Trace {

    @XmlElement(required = true)
    protected String parameter;
    @XmlElement(required = true)
    protected String cloud;
    @XmlElement(required = true)
    protected String dependencies;
    @XmlElement(required = true)
    protected String historicaldata;
    @XmlElement(required = true)
    protected Trace.Categories categories;
    @XmlAttribute(name = "runid")
    protected Long runid;

    /**
     * Ruft den Wert der parameter-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getParameter() {
        return parameter;
    }

    /**
     * Legt den Wert der parameter-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setParameter(String value) {
        this.parameter = value;
    }

    /**
     * Ruft den Wert der cloud-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCloud() {
        return cloud;
    }

    /**
     * Legt den Wert der cloud-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCloud(String value) {
        this.cloud = value;
    }

    /**
     * Ruft den Wert der dependencies-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDependencies() {
        return dependencies;
    }

    /**
     * Legt den Wert der dependencies-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDependencies(String value) {
        this.dependencies = value;
    }

    /**
     * Ruft den Wert der historicaldata-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHistoricaldata() {
        return historicaldata;
    }

    /**
     * Legt den Wert der historicaldata-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHistoricaldata(String value) {
        this.historicaldata = value;
    }

    /**
     * Ruft den Wert der categories-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Trace.Categories }
     *     
     */
    public Trace.Categories getCategories() {
        return categories;
    }

    /**
     * Legt den Wert der categories-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Trace.Categories }
     *     
     */
    public void setCategories(Trace.Categories value) {
        this.categories = value;
    }

    /**
     * Ruft den Wert der runid-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getRunid() {
        return runid;
    }

    /**
     * Legt den Wert der runid-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setRunid(Long value) {
        this.runid = value;
    }


    /**
     * <p>Java-Klasse für anonymous complex type.
     * 
     * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element name="predictioncategory" maxOccurs="unbounded" minOccurs="0">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;sequence>
     *                   &lt;element name="shortestmakespan" type="{http://www.w3.org/2001/XMLSchema}double"/>
     *                   &lt;element name="cheapestcost" type="{http://www.w3.org/2001/XMLSchema}double"/>
     *                   &lt;element name="deadlinecategories">
     *                     &lt;complexType>
     *                       &lt;complexContent>
     *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                           &lt;sequence>
     *                             &lt;element name="deadlinecategory" maxOccurs="unbounded" minOccurs="0">
     *                               &lt;complexType>
     *                                 &lt;complexContent>
     *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                                     &lt;sequence>
     *                                       &lt;element name="plan">
     *                                         &lt;complexType>
     *                                           &lt;complexContent>
     *                                             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                                               &lt;sequence>
     *                                                 &lt;element name="maxAlpha" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
     *                                                 &lt;element name="maxAlphaMakespan" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
     *                                                 &lt;element name="deadline" type="{http://www.w3.org/2001/XMLSchema}double"/>
     *                                                 &lt;element name="plannedmakespan" type="{http://www.w3.org/2001/XMLSchema}double"/>
     *                                                 &lt;element name="plannedcost" type="{http://www.w3.org/2001/XMLSchema}double"/>
     *                                                 &lt;element name="cheapestplannedcost" type="{http://www.w3.org/2001/XMLSchema}double"/>
     *                                               &lt;/sequence>
     *                                             &lt;/restriction>
     *                                           &lt;/complexContent>
     *                                         &lt;/complexType>
     *                                       &lt;/element>
     *                                       &lt;element name="runs">
     *                                         &lt;complexType>
     *                                           &lt;complexContent>
     *                                             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                                               &lt;sequence>
     *                                                 &lt;element name="run" maxOccurs="unbounded" minOccurs="0">
     *                                                   &lt;complexType>
     *                                                     &lt;complexContent>
     *                                                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                                                         &lt;sequence>
     *                                                           &lt;element name="static">
     *                                                             &lt;complexType>
     *                                                               &lt;complexContent>
     *                                                                 &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                                                                   &lt;sequence>
     *                                                                     &lt;element name="cost" type="{http://www.w3.org/2001/XMLSchema}double"/>
     *                                                                     &lt;element name="deadline" type="{http://www.w3.org/2001/XMLSchema}double"/>
     *                                                                     &lt;element name="makespan" type="{http://www.w3.org/2001/XMLSchema}double"/>
     *                                                                     &lt;element name="duration" type="{http://www.w3.org/2001/XMLSchema}double"/>
     *                                                                   &lt;/sequence>
     *                                                                 &lt;/restriction>
     *                                                               &lt;/complexContent>
     *                                                             &lt;/complexType>
     *                                                           &lt;/element>
     *                                                           &lt;element name="dynamic">
     *                                                             &lt;complexType>
     *                                                               &lt;complexContent>
     *                                                                 &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                                                                   &lt;sequence>
     *                                                                     &lt;element name="cost" type="{http://www.w3.org/2001/XMLSchema}double"/>
     *                                                                     &lt;element name="deadline" type="{http://www.w3.org/2001/XMLSchema}double"/>
     *                                                                     &lt;element name="makespan" type="{http://www.w3.org/2001/XMLSchema}double"/>
     *                                                                     &lt;element name="duration" type="{http://www.w3.org/2001/XMLSchema}double"/>
     *                                                                   &lt;/sequence>
     *                                                                 &lt;/restriction>
     *                                                               &lt;/complexContent>
     *                                                             &lt;/complexType>
     *                                                           &lt;/element>
     *                                                           &lt;element name="real">
     *                                                             &lt;complexType>
     *                                                               &lt;complexContent>
     *                                                                 &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                                                                   &lt;sequence>
     *                                                                     &lt;element name="cheapestcost" type="{http://www.w3.org/2001/XMLSchema}double"/>
     *                                                                     &lt;element name="fasttime" type="{http://www.w3.org/2001/XMLSchema}double"/>
     *                                                                   &lt;/sequence>
     *                                                                 &lt;/restriction>
     *                                                               &lt;/complexContent>
     *                                                             &lt;/complexType>
     *                                                           &lt;/element>
     *                                                         &lt;/sequence>
     *                                                         &lt;attribute name="exTimes" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
     *                                                       &lt;/restriction>
     *                                                     &lt;/complexContent>
     *                                                   &lt;/complexType>
     *                                                 &lt;/element>
     *                                               &lt;/sequence>
     *                                             &lt;/restriction>
     *                                           &lt;/complexContent>
     *                                         &lt;/complexType>
     *                                       &lt;/element>
     *                                     &lt;/sequence>
     *                                     &lt;attribute name="deadlinefactor" use="required" type="{http://www.w3.org/2001/XMLSchema}double" />
     *                                   &lt;/restriction>
     *                                 &lt;/complexContent>
     *                               &lt;/complexType>
     *                             &lt;/element>
     *                           &lt;/sequence>
     *                         &lt;/restriction>
     *                       &lt;/complexContent>
     *                     &lt;/complexType>
     *                   &lt;/element>
     *                 &lt;/sequence>
     *                 &lt;attribute name="method" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
     *                 &lt;attribute name="value" use="required" type="{http://www.w3.org/2001/XMLSchema}double" />
     *               &lt;/restriction>
     *             &lt;/complexContent>
     *           &lt;/complexType>
     *         &lt;/element>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "predictioncategory"
    })
    public static class Categories {

        protected List<Trace.Categories.Predictioncategory> predictioncategory;

        /**
         * Gets the value of the predictioncategory property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the predictioncategory property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getPredictioncategory().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link Trace.Categories.Predictioncategory }
         * 
         * 
         */
        public List<Trace.Categories.Predictioncategory> getPredictioncategory() {
            if (predictioncategory == null) {
                predictioncategory = new ArrayList<Trace.Categories.Predictioncategory>();
            }
            return this.predictioncategory;
        }


        /**
         * <p>Java-Klasse für anonymous complex type.
         * 
         * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
         * 
         * <pre>
         * &lt;complexType>
         *   &lt;complexContent>
         *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *       &lt;sequence>
         *         &lt;element name="shortestmakespan" type="{http://www.w3.org/2001/XMLSchema}double"/>
         *         &lt;element name="cheapestcost" type="{http://www.w3.org/2001/XMLSchema}double"/>
         *         &lt;element name="deadlinecategories">
         *           &lt;complexType>
         *             &lt;complexContent>
         *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *                 &lt;sequence>
         *                   &lt;element name="deadlinecategory" maxOccurs="unbounded" minOccurs="0">
         *                     &lt;complexType>
         *                       &lt;complexContent>
         *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *                           &lt;sequence>
         *                             &lt;element name="plan">
         *                               &lt;complexType>
         *                                 &lt;complexContent>
         *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *                                     &lt;sequence>
         *                                       &lt;element name="maxAlpha" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
         *                                       &lt;element name="maxAlphaMakespan" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
         *                                       &lt;element name="deadline" type="{http://www.w3.org/2001/XMLSchema}double"/>
         *                                       &lt;element name="plannedmakespan" type="{http://www.w3.org/2001/XMLSchema}double"/>
         *                                       &lt;element name="plannedcost" type="{http://www.w3.org/2001/XMLSchema}double"/>
         *                                       &lt;element name="cheapestplannedcost" type="{http://www.w3.org/2001/XMLSchema}double"/>
         *                                     &lt;/sequence>
         *                                   &lt;/restriction>
         *                                 &lt;/complexContent>
         *                               &lt;/complexType>
         *                             &lt;/element>
         *                             &lt;element name="runs">
         *                               &lt;complexType>
         *                                 &lt;complexContent>
         *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *                                     &lt;sequence>
         *                                       &lt;element name="run" maxOccurs="unbounded" minOccurs="0">
         *                                         &lt;complexType>
         *                                           &lt;complexContent>
         *                                             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *                                               &lt;sequence>
         *                                                 &lt;element name="static">
         *                                                   &lt;complexType>
         *                                                     &lt;complexContent>
         *                                                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *                                                         &lt;sequence>
         *                                                           &lt;element name="cost" type="{http://www.w3.org/2001/XMLSchema}double"/>
         *                                                           &lt;element name="deadline" type="{http://www.w3.org/2001/XMLSchema}double"/>
         *                                                           &lt;element name="makespan" type="{http://www.w3.org/2001/XMLSchema}double"/>
         *                                                           &lt;element name="duration" type="{http://www.w3.org/2001/XMLSchema}double"/>
         *                                                         &lt;/sequence>
         *                                                       &lt;/restriction>
         *                                                     &lt;/complexContent>
         *                                                   &lt;/complexType>
         *                                                 &lt;/element>
         *                                                 &lt;element name="dynamic">
         *                                                   &lt;complexType>
         *                                                     &lt;complexContent>
         *                                                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *                                                         &lt;sequence>
         *                                                           &lt;element name="cost" type="{http://www.w3.org/2001/XMLSchema}double"/>
         *                                                           &lt;element name="deadline" type="{http://www.w3.org/2001/XMLSchema}double"/>
         *                                                           &lt;element name="makespan" type="{http://www.w3.org/2001/XMLSchema}double"/>
         *                                                           &lt;element name="duration" type="{http://www.w3.org/2001/XMLSchema}double"/>
         *                                                         &lt;/sequence>
         *                                                       &lt;/restriction>
         *                                                     &lt;/complexContent>
         *                                                   &lt;/complexType>
         *                                                 &lt;/element>
         *                                                 &lt;element name="real">
         *                                                   &lt;complexType>
         *                                                     &lt;complexContent>
         *                                                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *                                                         &lt;sequence>
         *                                                           &lt;element name="cheapestcost" type="{http://www.w3.org/2001/XMLSchema}double"/>
         *                                                           &lt;element name="fasttime" type="{http://www.w3.org/2001/XMLSchema}double"/>
         *                                                         &lt;/sequence>
         *                                                       &lt;/restriction>
         *                                                     &lt;/complexContent>
         *                                                   &lt;/complexType>
         *                                                 &lt;/element>
         *                                               &lt;/sequence>
         *                                               &lt;attribute name="exTimes" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
         *                                             &lt;/restriction>
         *                                           &lt;/complexContent>
         *                                         &lt;/complexType>
         *                                       &lt;/element>
         *                                     &lt;/sequence>
         *                                   &lt;/restriction>
         *                                 &lt;/complexContent>
         *                               &lt;/complexType>
         *                             &lt;/element>
         *                           &lt;/sequence>
         *                           &lt;attribute name="deadlinefactor" use="required" type="{http://www.w3.org/2001/XMLSchema}double" />
         *                         &lt;/restriction>
         *                       &lt;/complexContent>
         *                     &lt;/complexType>
         *                   &lt;/element>
         *                 &lt;/sequence>
         *               &lt;/restriction>
         *             &lt;/complexContent>
         *           &lt;/complexType>
         *         &lt;/element>
         *       &lt;/sequence>
         *       &lt;attribute name="method" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
         *       &lt;attribute name="value" use="required" type="{http://www.w3.org/2001/XMLSchema}double" />
         *     &lt;/restriction>
         *   &lt;/complexContent>
         * &lt;/complexType>
         * </pre>
         * 
         * 
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "", propOrder = {
            "shortestmakespan",
            "cheapestcost",
            "deadlinecategories"
        })
        public static class Predictioncategory {

            protected double shortestmakespan;
            protected double cheapestcost;
            @XmlElement(required = true)
            protected Trace.Categories.Predictioncategory.Deadlinecategories deadlinecategories;
            @XmlAttribute(name = "method", required = true)
            protected String method;
            @XmlAttribute(name = "value", required = true)
            protected double value;

            /**
             * Ruft den Wert der shortestmakespan-Eigenschaft ab.
             * 
             */
            public double getShortestmakespan() {
                return shortestmakespan;
            }

            /**
             * Legt den Wert der shortestmakespan-Eigenschaft fest.
             * 
             */
            public void setShortestmakespan(double value) {
                this.shortestmakespan = value;
            }

            /**
             * Ruft den Wert der cheapestcost-Eigenschaft ab.
             * 
             */
            public double getCheapestcost() {
                return cheapestcost;
            }

            /**
             * Legt den Wert der cheapestcost-Eigenschaft fest.
             * 
             */
            public void setCheapestcost(double value) {
                this.cheapestcost = value;
            }

            /**
             * Ruft den Wert der deadlinecategories-Eigenschaft ab.
             * 
             * @return
             *     possible object is
             *     {@link Trace.Categories.Predictioncategory.Deadlinecategories }
             *     
             */
            public Trace.Categories.Predictioncategory.Deadlinecategories getDeadlinecategories() {
                return deadlinecategories;
            }

            /**
             * Legt den Wert der deadlinecategories-Eigenschaft fest.
             * 
             * @param value
             *     allowed object is
             *     {@link Trace.Categories.Predictioncategory.Deadlinecategories }
             *     
             */
            public void setDeadlinecategories(Trace.Categories.Predictioncategory.Deadlinecategories value) {
                this.deadlinecategories = value;
            }

            /**
             * Ruft den Wert der method-Eigenschaft ab.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getMethod() {
                return method;
            }

            /**
             * Legt den Wert der method-Eigenschaft fest.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setMethod(String value) {
                this.method = value;
            }

            /**
             * Ruft den Wert der value-Eigenschaft ab.
             * 
             */
            public double getValue() {
                return value;
            }

            /**
             * Legt den Wert der value-Eigenschaft fest.
             * 
             */
            public void setValue(double value) {
                this.value = value;
            }


            /**
             * <p>Java-Klasse für anonymous complex type.
             * 
             * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
             * 
             * <pre>
             * &lt;complexType>
             *   &lt;complexContent>
             *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
             *       &lt;sequence>
             *         &lt;element name="deadlinecategory" maxOccurs="unbounded" minOccurs="0">
             *           &lt;complexType>
             *             &lt;complexContent>
             *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
             *                 &lt;sequence>
             *                   &lt;element name="plan">
             *                     &lt;complexType>
             *                       &lt;complexContent>
             *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
             *                           &lt;sequence>
             *                             &lt;element name="maxAlpha" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
             *                             &lt;element name="maxAlphaMakespan" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
             *                             &lt;element name="deadline" type="{http://www.w3.org/2001/XMLSchema}double"/>
             *                             &lt;element name="plannedmakespan" type="{http://www.w3.org/2001/XMLSchema}double"/>
             *                             &lt;element name="plannedcost" type="{http://www.w3.org/2001/XMLSchema}double"/>
             *                             &lt;element name="cheapestplannedcost" type="{http://www.w3.org/2001/XMLSchema}double"/>
             *                           &lt;/sequence>
             *                         &lt;/restriction>
             *                       &lt;/complexContent>
             *                     &lt;/complexType>
             *                   &lt;/element>
             *                   &lt;element name="runs">
             *                     &lt;complexType>
             *                       &lt;complexContent>
             *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
             *                           &lt;sequence>
             *                             &lt;element name="run" maxOccurs="unbounded" minOccurs="0">
             *                               &lt;complexType>
             *                                 &lt;complexContent>
             *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
             *                                     &lt;sequence>
             *                                       &lt;element name="static">
             *                                         &lt;complexType>
             *                                           &lt;complexContent>
             *                                             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
             *                                               &lt;sequence>
             *                                                 &lt;element name="cost" type="{http://www.w3.org/2001/XMLSchema}double"/>
             *                                                 &lt;element name="deadline" type="{http://www.w3.org/2001/XMLSchema}double"/>
             *                                                 &lt;element name="makespan" type="{http://www.w3.org/2001/XMLSchema}double"/>
             *                                                 &lt;element name="duration" type="{http://www.w3.org/2001/XMLSchema}double"/>
             *                                               &lt;/sequence>
             *                                             &lt;/restriction>
             *                                           &lt;/complexContent>
             *                                         &lt;/complexType>
             *                                       &lt;/element>
             *                                       &lt;element name="dynamic">
             *                                         &lt;complexType>
             *                                           &lt;complexContent>
             *                                             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
             *                                               &lt;sequence>
             *                                                 &lt;element name="cost" type="{http://www.w3.org/2001/XMLSchema}double"/>
             *                                                 &lt;element name="deadline" type="{http://www.w3.org/2001/XMLSchema}double"/>
             *                                                 &lt;element name="makespan" type="{http://www.w3.org/2001/XMLSchema}double"/>
             *                                                 &lt;element name="duration" type="{http://www.w3.org/2001/XMLSchema}double"/>
             *                                               &lt;/sequence>
             *                                             &lt;/restriction>
             *                                           &lt;/complexContent>
             *                                         &lt;/complexType>
             *                                       &lt;/element>
             *                                       &lt;element name="real">
             *                                         &lt;complexType>
             *                                           &lt;complexContent>
             *                                             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
             *                                               &lt;sequence>
             *                                                 &lt;element name="cheapestcost" type="{http://www.w3.org/2001/XMLSchema}double"/>
             *                                                 &lt;element name="fasttime" type="{http://www.w3.org/2001/XMLSchema}double"/>
             *                                               &lt;/sequence>
             *                                             &lt;/restriction>
             *                                           &lt;/complexContent>
             *                                         &lt;/complexType>
             *                                       &lt;/element>
             *                                     &lt;/sequence>
             *                                     &lt;attribute name="exTimes" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
             *                                   &lt;/restriction>
             *                                 &lt;/complexContent>
             *                               &lt;/complexType>
             *                             &lt;/element>
             *                           &lt;/sequence>
             *                         &lt;/restriction>
             *                       &lt;/complexContent>
             *                     &lt;/complexType>
             *                   &lt;/element>
             *                 &lt;/sequence>
             *                 &lt;attribute name="deadlinefactor" use="required" type="{http://www.w3.org/2001/XMLSchema}double" />
             *               &lt;/restriction>
             *             &lt;/complexContent>
             *           &lt;/complexType>
             *         &lt;/element>
             *       &lt;/sequence>
             *     &lt;/restriction>
             *   &lt;/complexContent>
             * &lt;/complexType>
             * </pre>
             * 
             * 
             */
            @XmlAccessorType(XmlAccessType.FIELD)
            @XmlType(name = "", propOrder = {
                "deadlinecategory"
            })
            public static class Deadlinecategories {

                protected List<Trace.Categories.Predictioncategory.Deadlinecategories.Deadlinecategory> deadlinecategory;

                /**
                 * Gets the value of the deadlinecategory property.
                 * 
                 * <p>
                 * This accessor method returns a reference to the live list,
                 * not a snapshot. Therefore any modification you make to the
                 * returned list will be present inside the JAXB object.
                 * This is why there is not a <CODE>set</CODE> method for the deadlinecategory property.
                 * 
                 * <p>
                 * For example, to add a new item, do as follows:
                 * <pre>
                 *    getDeadlinecategory().add(newItem);
                 * </pre>
                 * 
                 * 
                 * <p>
                 * Objects of the following type(s) are allowed in the list
                 * {@link Trace.Categories.Predictioncategory.Deadlinecategories.Deadlinecategory }
                 * 
                 * 
                 */
                public List<Trace.Categories.Predictioncategory.Deadlinecategories.Deadlinecategory> getDeadlinecategory() {
                    if (deadlinecategory == null) {
                        deadlinecategory = new ArrayList<Trace.Categories.Predictioncategory.Deadlinecategories.Deadlinecategory>();
                    }
                    return this.deadlinecategory;
                }


                /**
                 * <p>Java-Klasse für anonymous complex type.
                 * 
                 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
                 * 
                 * <pre>
                 * &lt;complexType>
                 *   &lt;complexContent>
                 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
                 *       &lt;sequence>
                 *         &lt;element name="plan">
                 *           &lt;complexType>
                 *             &lt;complexContent>
                 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
                 *                 &lt;sequence>
                 *                   &lt;element name="maxAlpha" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
                 *                   &lt;element name="maxAlphaMakespan" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
                 *                   &lt;element name="deadline" type="{http://www.w3.org/2001/XMLSchema}double"/>
                 *                   &lt;element name="plannedmakespan" type="{http://www.w3.org/2001/XMLSchema}double"/>
                 *                   &lt;element name="plannedcost" type="{http://www.w3.org/2001/XMLSchema}double"/>
                 *                   &lt;element name="cheapestplannedcost" type="{http://www.w3.org/2001/XMLSchema}double"/>
                 *                 &lt;/sequence>
                 *               &lt;/restriction>
                 *             &lt;/complexContent>
                 *           &lt;/complexType>
                 *         &lt;/element>
                 *         &lt;element name="runs">
                 *           &lt;complexType>
                 *             &lt;complexContent>
                 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
                 *                 &lt;sequence>
                 *                   &lt;element name="run" maxOccurs="unbounded" minOccurs="0">
                 *                     &lt;complexType>
                 *                       &lt;complexContent>
                 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
                 *                           &lt;sequence>
                 *                             &lt;element name="static">
                 *                               &lt;complexType>
                 *                                 &lt;complexContent>
                 *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
                 *                                     &lt;sequence>
                 *                                       &lt;element name="cost" type="{http://www.w3.org/2001/XMLSchema}double"/>
                 *                                       &lt;element name="deadline" type="{http://www.w3.org/2001/XMLSchema}double"/>
                 *                                       &lt;element name="makespan" type="{http://www.w3.org/2001/XMLSchema}double"/>
                 *                                       &lt;element name="duration" type="{http://www.w3.org/2001/XMLSchema}double"/>
                 *                                     &lt;/sequence>
                 *                                   &lt;/restriction>
                 *                                 &lt;/complexContent>
                 *                               &lt;/complexType>
                 *                             &lt;/element>
                 *                             &lt;element name="dynamic">
                 *                               &lt;complexType>
                 *                                 &lt;complexContent>
                 *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
                 *                                     &lt;sequence>
                 *                                       &lt;element name="cost" type="{http://www.w3.org/2001/XMLSchema}double"/>
                 *                                       &lt;element name="deadline" type="{http://www.w3.org/2001/XMLSchema}double"/>
                 *                                       &lt;element name="makespan" type="{http://www.w3.org/2001/XMLSchema}double"/>
                 *                                       &lt;element name="duration" type="{http://www.w3.org/2001/XMLSchema}double"/>
                 *                                     &lt;/sequence>
                 *                                   &lt;/restriction>
                 *                                 &lt;/complexContent>
                 *                               &lt;/complexType>
                 *                             &lt;/element>
                 *                             &lt;element name="real">
                 *                               &lt;complexType>
                 *                                 &lt;complexContent>
                 *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
                 *                                     &lt;sequence>
                 *                                       &lt;element name="cheapestcost" type="{http://www.w3.org/2001/XMLSchema}double"/>
                 *                                       &lt;element name="fasttime" type="{http://www.w3.org/2001/XMLSchema}double"/>
                 *                                     &lt;/sequence>
                 *                                   &lt;/restriction>
                 *                                 &lt;/complexContent>
                 *                               &lt;/complexType>
                 *                             &lt;/element>
                 *                           &lt;/sequence>
                 *                           &lt;attribute name="exTimes" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
                 *                         &lt;/restriction>
                 *                       &lt;/complexContent>
                 *                     &lt;/complexType>
                 *                   &lt;/element>
                 *                 &lt;/sequence>
                 *               &lt;/restriction>
                 *             &lt;/complexContent>
                 *           &lt;/complexType>
                 *         &lt;/element>
                 *       &lt;/sequence>
                 *       &lt;attribute name="deadlinefactor" use="required" type="{http://www.w3.org/2001/XMLSchema}double" />
                 *     &lt;/restriction>
                 *   &lt;/complexContent>
                 * &lt;/complexType>
                 * </pre>
                 * 
                 * 
                 */
                @XmlAccessorType(XmlAccessType.FIELD)
                @XmlType(name = "", propOrder = {
                    "plan",
                    "runs"
                })
                public static class Deadlinecategory {

                    @XmlElement(required = true)
                    protected Trace.Categories.Predictioncategory.Deadlinecategories.Deadlinecategory.Plan plan;
                    @XmlElement(required = true)
                    protected Trace.Categories.Predictioncategory.Deadlinecategories.Deadlinecategory.Runs runs;
                    @XmlAttribute(name = "deadlinefactor", required = true)
                    protected double deadlinefactor;

                    /**
                     * Ruft den Wert der plan-Eigenschaft ab.
                     * 
                     * @return
                     *     possible object is
                     *     {@link Trace.Categories.Predictioncategory.Deadlinecategories.Deadlinecategory.Plan }
                     *     
                     */
                    public Trace.Categories.Predictioncategory.Deadlinecategories.Deadlinecategory.Plan getPlan() {
                        return plan;
                    }

                    /**
                     * Legt den Wert der plan-Eigenschaft fest.
                     * 
                     * @param value
                     *     allowed object is
                     *     {@link Trace.Categories.Predictioncategory.Deadlinecategories.Deadlinecategory.Plan }
                     *     
                     */
                    public void setPlan(Trace.Categories.Predictioncategory.Deadlinecategories.Deadlinecategory.Plan value) {
                        this.plan = value;
                    }

                    /**
                     * Ruft den Wert der runs-Eigenschaft ab.
                     * 
                     * @return
                     *     possible object is
                     *     {@link Trace.Categories.Predictioncategory.Deadlinecategories.Deadlinecategory.Runs }
                     *     
                     */
                    public Trace.Categories.Predictioncategory.Deadlinecategories.Deadlinecategory.Runs getRuns() {
                        return runs;
                    }

                    /**
                     * Legt den Wert der runs-Eigenschaft fest.
                     * 
                     * @param value
                     *     allowed object is
                     *     {@link Trace.Categories.Predictioncategory.Deadlinecategories.Deadlinecategory.Runs }
                     *     
                     */
                    public void setRuns(Trace.Categories.Predictioncategory.Deadlinecategories.Deadlinecategory.Runs value) {
                        this.runs = value;
                    }

                    /**
                     * Ruft den Wert der deadlinefactor-Eigenschaft ab.
                     * 
                     */
                    public double getDeadlinefactor() {
                        return deadlinefactor;
                    }

                    /**
                     * Legt den Wert der deadlinefactor-Eigenschaft fest.
                     * 
                     */
                    public void setDeadlinefactor(double value) {
                        this.deadlinefactor = value;
                    }


                    /**
                     * <p>Java-Klasse für anonymous complex type.
                     * 
                     * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
                     * 
                     * <pre>
                     * &lt;complexType>
                     *   &lt;complexContent>
                     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
                     *       &lt;sequence>
                     *         &lt;element name="maxAlpha" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
                     *         &lt;element name="maxAlphaMakespan" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
                     *         &lt;element name="deadline" type="{http://www.w3.org/2001/XMLSchema}double"/>
                     *         &lt;element name="plannedmakespan" type="{http://www.w3.org/2001/XMLSchema}double"/>
                     *         &lt;element name="plannedcost" type="{http://www.w3.org/2001/XMLSchema}double"/>
                     *         &lt;element name="cheapestplannedcost" type="{http://www.w3.org/2001/XMLSchema}double"/>
                     *       &lt;/sequence>
                     *     &lt;/restriction>
                     *   &lt;/complexContent>
                     * &lt;/complexType>
                     * </pre>
                     * 
                     * 
                     */
                    @XmlAccessorType(XmlAccessType.FIELD)
                    @XmlType(name = "", propOrder = {
                        "maxAlpha",
                        "maxAlphaMakespan",
                        "deadline",
                        "plannedmakespan",
                        "plannedcost",
                        "cheapestplannedcost"
                    })
                    public static class Plan {

                        protected Double maxAlpha;
                        protected Double maxAlphaMakespan;
                        protected double deadline;
                        protected double plannedmakespan;
                        protected double plannedcost;
                        protected double cheapestplannedcost;

                        /**
                         * Ruft den Wert der maxAlpha-Eigenschaft ab.
                         * 
                         * @return
                         *     possible object is
                         *     {@link Double }
                         *     
                         */
                        public Double getMaxAlpha() {
                            return maxAlpha;
                        }

                        /**
                         * Legt den Wert der maxAlpha-Eigenschaft fest.
                         * 
                         * @param value
                         *     allowed object is
                         *     {@link Double }
                         *     
                         */
                        public void setMaxAlpha(Double value) {
                            this.maxAlpha = value;
                        }

                        /**
                         * Ruft den Wert der maxAlphaMakespan-Eigenschaft ab.
                         * 
                         * @return
                         *     possible object is
                         *     {@link Double }
                         *     
                         */
                        public Double getMaxAlphaMakespan() {
                            return maxAlphaMakespan;
                        }

                        /**
                         * Legt den Wert der maxAlphaMakespan-Eigenschaft fest.
                         * 
                         * @param value
                         *     allowed object is
                         *     {@link Double }
                         *     
                         */
                        public void setMaxAlphaMakespan(Double value) {
                            this.maxAlphaMakespan = value;
                        }

                        /**
                         * Ruft den Wert der deadline-Eigenschaft ab.
                         * 
                         */
                        public double getDeadline() {
                            return deadline;
                        }

                        /**
                         * Legt den Wert der deadline-Eigenschaft fest.
                         * 
                         */
                        public void setDeadline(double value) {
                            this.deadline = value;
                        }

                        /**
                         * Ruft den Wert der plannedmakespan-Eigenschaft ab.
                         * 
                         */
                        public double getPlannedmakespan() {
                            return plannedmakespan;
                        }

                        /**
                         * Legt den Wert der plannedmakespan-Eigenschaft fest.
                         * 
                         */
                        public void setPlannedmakespan(double value) {
                            this.plannedmakespan = value;
                        }

                        /**
                         * Ruft den Wert der plannedcost-Eigenschaft ab.
                         * 
                         */
                        public double getPlannedcost() {
                            return plannedcost;
                        }

                        /**
                         * Legt den Wert der plannedcost-Eigenschaft fest.
                         * 
                         */
                        public void setPlannedcost(double value) {
                            this.plannedcost = value;
                        }

                        /**
                         * Ruft den Wert der cheapestplannedcost-Eigenschaft ab.
                         * 
                         */
                        public double getCheapestplannedcost() {
                            return cheapestplannedcost;
                        }

                        /**
                         * Legt den Wert der cheapestplannedcost-Eigenschaft fest.
                         * 
                         */
                        public void setCheapestplannedcost(double value) {
                            this.cheapestplannedcost = value;
                        }

                    }


                    /**
                     * <p>Java-Klasse für anonymous complex type.
                     * 
                     * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
                     * 
                     * <pre>
                     * &lt;complexType>
                     *   &lt;complexContent>
                     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
                     *       &lt;sequence>
                     *         &lt;element name="run" maxOccurs="unbounded" minOccurs="0">
                     *           &lt;complexType>
                     *             &lt;complexContent>
                     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
                     *                 &lt;sequence>
                     *                   &lt;element name="static">
                     *                     &lt;complexType>
                     *                       &lt;complexContent>
                     *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
                     *                           &lt;sequence>
                     *                             &lt;element name="cost" type="{http://www.w3.org/2001/XMLSchema}double"/>
                     *                             &lt;element name="deadline" type="{http://www.w3.org/2001/XMLSchema}double"/>
                     *                             &lt;element name="makespan" type="{http://www.w3.org/2001/XMLSchema}double"/>
                     *                             &lt;element name="duration" type="{http://www.w3.org/2001/XMLSchema}double"/>
                     *                           &lt;/sequence>
                     *                         &lt;/restriction>
                     *                       &lt;/complexContent>
                     *                     &lt;/complexType>
                     *                   &lt;/element>
                     *                   &lt;element name="dynamic">
                     *                     &lt;complexType>
                     *                       &lt;complexContent>
                     *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
                     *                           &lt;sequence>
                     *                             &lt;element name="cost" type="{http://www.w3.org/2001/XMLSchema}double"/>
                     *                             &lt;element name="deadline" type="{http://www.w3.org/2001/XMLSchema}double"/>
                     *                             &lt;element name="makespan" type="{http://www.w3.org/2001/XMLSchema}double"/>
                     *                             &lt;element name="duration" type="{http://www.w3.org/2001/XMLSchema}double"/>
                     *                           &lt;/sequence>
                     *                         &lt;/restriction>
                     *                       &lt;/complexContent>
                     *                     &lt;/complexType>
                     *                   &lt;/element>
                     *                   &lt;element name="real">
                     *                     &lt;complexType>
                     *                       &lt;complexContent>
                     *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
                     *                           &lt;sequence>
                     *                             &lt;element name="cheapestcost" type="{http://www.w3.org/2001/XMLSchema}double"/>
                     *                             &lt;element name="fasttime" type="{http://www.w3.org/2001/XMLSchema}double"/>
                     *                           &lt;/sequence>
                     *                         &lt;/restriction>
                     *                       &lt;/complexContent>
                     *                     &lt;/complexType>
                     *                   &lt;/element>
                     *                 &lt;/sequence>
                     *                 &lt;attribute name="exTimes" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
                     *               &lt;/restriction>
                     *             &lt;/complexContent>
                     *           &lt;/complexType>
                     *         &lt;/element>
                     *       &lt;/sequence>
                     *     &lt;/restriction>
                     *   &lt;/complexContent>
                     * &lt;/complexType>
                     * </pre>
                     * 
                     * 
                     */
                    @XmlAccessorType(XmlAccessType.FIELD)
                    @XmlType(name = "", propOrder = {
                        "run"
                    })
                    public static class Runs {

                        protected List<Trace.Categories.Predictioncategory.Deadlinecategories.Deadlinecategory.Runs.Run> run;

                        /**
                         * Gets the value of the run property.
                         * 
                         * <p>
                         * This accessor method returns a reference to the live list,
                         * not a snapshot. Therefore any modification you make to the
                         * returned list will be present inside the JAXB object.
                         * This is why there is not a <CODE>set</CODE> method for the run property.
                         * 
                         * <p>
                         * For example, to add a new item, do as follows:
                         * <pre>
                         *    getRun().add(newItem);
                         * </pre>
                         * 
                         * 
                         * <p>
                         * Objects of the following type(s) are allowed in the list
                         * {@link Trace.Categories.Predictioncategory.Deadlinecategories.Deadlinecategory.Runs.Run }
                         * 
                         * 
                         */
                        public List<Trace.Categories.Predictioncategory.Deadlinecategories.Deadlinecategory.Runs.Run> getRun() {
                            if (run == null) {
                                run = new ArrayList<Trace.Categories.Predictioncategory.Deadlinecategories.Deadlinecategory.Runs.Run>();
                            }
                            return this.run;
                        }


                        /**
                         * <p>Java-Klasse für anonymous complex type.
                         * 
                         * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
                         * 
                         * <pre>
                         * &lt;complexType>
                         *   &lt;complexContent>
                         *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
                         *       &lt;sequence>
                         *         &lt;element name="static">
                         *           &lt;complexType>
                         *             &lt;complexContent>
                         *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
                         *                 &lt;sequence>
                         *                   &lt;element name="cost" type="{http://www.w3.org/2001/XMLSchema}double"/>
                         *                   &lt;element name="deadline" type="{http://www.w3.org/2001/XMLSchema}double"/>
                         *                   &lt;element name="makespan" type="{http://www.w3.org/2001/XMLSchema}double"/>
                         *                   &lt;element name="duration" type="{http://www.w3.org/2001/XMLSchema}double"/>
                         *                 &lt;/sequence>
                         *               &lt;/restriction>
                         *             &lt;/complexContent>
                         *           &lt;/complexType>
                         *         &lt;/element>
                         *         &lt;element name="dynamic">
                         *           &lt;complexType>
                         *             &lt;complexContent>
                         *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
                         *                 &lt;sequence>
                         *                   &lt;element name="cost" type="{http://www.w3.org/2001/XMLSchema}double"/>
                         *                   &lt;element name="deadline" type="{http://www.w3.org/2001/XMLSchema}double"/>
                         *                   &lt;element name="makespan" type="{http://www.w3.org/2001/XMLSchema}double"/>
                         *                   &lt;element name="duration" type="{http://www.w3.org/2001/XMLSchema}double"/>
                         *                 &lt;/sequence>
                         *               &lt;/restriction>
                         *             &lt;/complexContent>
                         *           &lt;/complexType>
                         *         &lt;/element>
                         *         &lt;element name="real">
                         *           &lt;complexType>
                         *             &lt;complexContent>
                         *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
                         *                 &lt;sequence>
                         *                   &lt;element name="cheapestcost" type="{http://www.w3.org/2001/XMLSchema}double"/>
                         *                   &lt;element name="fasttime" type="{http://www.w3.org/2001/XMLSchema}double"/>
                         *                 &lt;/sequence>
                         *               &lt;/restriction>
                         *             &lt;/complexContent>
                         *           &lt;/complexType>
                         *         &lt;/element>
                         *       &lt;/sequence>
                         *       &lt;attribute name="exTimes" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
                         *     &lt;/restriction>
                         *   &lt;/complexContent>
                         * &lt;/complexType>
                         * </pre>
                         * 
                         * 
                         */
                        @XmlAccessorType(XmlAccessType.FIELD)
                        @XmlType(name = "", propOrder = {
                            "_static",
                            "dynamic",
                            "real"
                        })
                        public static class Run {

                            @XmlElement(name = "static", required = true)
                            protected Trace.Categories.Predictioncategory.Deadlinecategories.Deadlinecategory.Runs.Run.Static _static;
                            @XmlElement(required = true)
                            protected Trace.Categories.Predictioncategory.Deadlinecategories.Deadlinecategory.Runs.Run.Dynamic dynamic;
                            @XmlElement(required = true)
                            protected Trace.Categories.Predictioncategory.Deadlinecategories.Deadlinecategory.Runs.Run.Real real;
                            @XmlAttribute(name = "exTimes", required = true)
                            protected String exTimes;

                            /**
                             * Ruft den Wert der static-Eigenschaft ab.
                             * 
                             * @return
                             *     possible object is
                             *     {@link Trace.Categories.Predictioncategory.Deadlinecategories.Deadlinecategory.Runs.Run.Static }
                             *     
                             */
                            public Trace.Categories.Predictioncategory.Deadlinecategories.Deadlinecategory.Runs.Run.Static getStatic() {
                                return _static;
                            }

                            /**
                             * Legt den Wert der static-Eigenschaft fest.
                             * 
                             * @param value
                             *     allowed object is
                             *     {@link Trace.Categories.Predictioncategory.Deadlinecategories.Deadlinecategory.Runs.Run.Static }
                             *     
                             */
                            public void setStatic(Trace.Categories.Predictioncategory.Deadlinecategories.Deadlinecategory.Runs.Run.Static value) {
                                this._static = value;
                            }

                            /**
                             * Ruft den Wert der dynamic-Eigenschaft ab.
                             * 
                             * @return
                             *     possible object is
                             *     {@link Trace.Categories.Predictioncategory.Deadlinecategories.Deadlinecategory.Runs.Run.Dynamic }
                             *     
                             */
                            public Trace.Categories.Predictioncategory.Deadlinecategories.Deadlinecategory.Runs.Run.Dynamic getDynamic() {
                                return dynamic;
                            }

                            /**
                             * Legt den Wert der dynamic-Eigenschaft fest.
                             * 
                             * @param value
                             *     allowed object is
                             *     {@link Trace.Categories.Predictioncategory.Deadlinecategories.Deadlinecategory.Runs.Run.Dynamic }
                             *     
                             */
                            public void setDynamic(Trace.Categories.Predictioncategory.Deadlinecategories.Deadlinecategory.Runs.Run.Dynamic value) {
                                this.dynamic = value;
                            }

                            /**
                             * Ruft den Wert der real-Eigenschaft ab.
                             * 
                             * @return
                             *     possible object is
                             *     {@link Trace.Categories.Predictioncategory.Deadlinecategories.Deadlinecategory.Runs.Run.Real }
                             *     
                             */
                            public Trace.Categories.Predictioncategory.Deadlinecategories.Deadlinecategory.Runs.Run.Real getReal() {
                                return real;
                            }

                            /**
                             * Legt den Wert der real-Eigenschaft fest.
                             * 
                             * @param value
                             *     allowed object is
                             *     {@link Trace.Categories.Predictioncategory.Deadlinecategories.Deadlinecategory.Runs.Run.Real }
                             *     
                             */
                            public void setReal(Trace.Categories.Predictioncategory.Deadlinecategories.Deadlinecategory.Runs.Run.Real value) {
                                this.real = value;
                            }

                            /**
                             * Ruft den Wert der exTimes-Eigenschaft ab.
                             * 
                             * @return
                             *     possible object is
                             *     {@link String }
                             *     
                             */
                            public String getExTimes() {
                                return exTimes;
                            }

                            /**
                             * Legt den Wert der exTimes-Eigenschaft fest.
                             * 
                             * @param value
                             *     allowed object is
                             *     {@link String }
                             *     
                             */
                            public void setExTimes(String value) {
                                this.exTimes = value;
                            }


                            /**
                             * <p>Java-Klasse für anonymous complex type.
                             * 
                             * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
                             * 
                             * <pre>
                             * &lt;complexType>
                             *   &lt;complexContent>
                             *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
                             *       &lt;sequence>
                             *         &lt;element name="cost" type="{http://www.w3.org/2001/XMLSchema}double"/>
                             *         &lt;element name="deadline" type="{http://www.w3.org/2001/XMLSchema}double"/>
                             *         &lt;element name="makespan" type="{http://www.w3.org/2001/XMLSchema}double"/>
                             *         &lt;element name="duration" type="{http://www.w3.org/2001/XMLSchema}double"/>
                             *       &lt;/sequence>
                             *     &lt;/restriction>
                             *   &lt;/complexContent>
                             * &lt;/complexType>
                             * </pre>
                             * 
                             * 
                             */
                            @XmlAccessorType(XmlAccessType.FIELD)
                            @XmlType(name = "", propOrder = {
                                "cost",
                                "deadline",
                                "makespan",
                                "duration"
                            })
                            public static class Dynamic {

                                protected double cost;
                                protected double deadline;
                                protected double makespan;
                                protected double duration;

                                /**
                                 * Ruft den Wert der cost-Eigenschaft ab.
                                 * 
                                 */
                                public double getCost() {
                                    return cost;
                                }

                                /**
                                 * Legt den Wert der cost-Eigenschaft fest.
                                 * 
                                 */
                                public void setCost(double value) {
                                    this.cost = value;
                                }

                                /**
                                 * Ruft den Wert der deadline-Eigenschaft ab.
                                 * 
                                 */
                                public double getDeadline() {
                                    return deadline;
                                }

                                /**
                                 * Legt den Wert der deadline-Eigenschaft fest.
                                 * 
                                 */
                                public void setDeadline(double value) {
                                    this.deadline = value;
                                }

                                /**
                                 * Ruft den Wert der makespan-Eigenschaft ab.
                                 * 
                                 */
                                public double getMakespan() {
                                    return makespan;
                                }

                                /**
                                 * Legt den Wert der makespan-Eigenschaft fest.
                                 * 
                                 */
                                public void setMakespan(double value) {
                                    this.makespan = value;
                                }

                                /**
                                 * Ruft den Wert der duration-Eigenschaft ab.
                                 * 
                                 */
                                public double getDuration() {
                                    return duration;
                                }

                                /**
                                 * Legt den Wert der duration-Eigenschaft fest.
                                 * 
                                 */
                                public void setDuration(double value) {
                                    this.duration = value;
                                }

                            }


                            /**
                             * <p>Java-Klasse für anonymous complex type.
                             * 
                             * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
                             * 
                             * <pre>
                             * &lt;complexType>
                             *   &lt;complexContent>
                             *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
                             *       &lt;sequence>
                             *         &lt;element name="cheapestcost" type="{http://www.w3.org/2001/XMLSchema}double"/>
                             *         &lt;element name="fasttime" type="{http://www.w3.org/2001/XMLSchema}double"/>
                             *       &lt;/sequence>
                             *     &lt;/restriction>
                             *   &lt;/complexContent>
                             * &lt;/complexType>
                             * </pre>
                             * 
                             * 
                             */
                            @XmlAccessorType(XmlAccessType.FIELD)
                            @XmlType(name = "", propOrder = {
                                "cheapestcost",
                                "fasttime"
                            })
                            public static class Real {

                                protected double cheapestcost;
                                protected double fasttime;

                                /**
                                 * Ruft den Wert der cheapestcost-Eigenschaft ab.
                                 * 
                                 */
                                public double getCheapestcost() {
                                    return cheapestcost;
                                }

                                /**
                                 * Legt den Wert der cheapestcost-Eigenschaft fest.
                                 * 
                                 */
                                public void setCheapestcost(double value) {
                                    this.cheapestcost = value;
                                }

                                /**
                                 * Ruft den Wert der fasttime-Eigenschaft ab.
                                 * 
                                 */
                                public double getFasttime() {
                                    return fasttime;
                                }

                                /**
                                 * Legt den Wert der fasttime-Eigenschaft fest.
                                 * 
                                 */
                                public void setFasttime(double value) {
                                    this.fasttime = value;
                                }

                            }


                            /**
                             * <p>Java-Klasse für anonymous complex type.
                             * 
                             * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
                             * 
                             * <pre>
                             * &lt;complexType>
                             *   &lt;complexContent>
                             *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
                             *       &lt;sequence>
                             *         &lt;element name="cost" type="{http://www.w3.org/2001/XMLSchema}double"/>
                             *         &lt;element name="deadline" type="{http://www.w3.org/2001/XMLSchema}double"/>
                             *         &lt;element name="makespan" type="{http://www.w3.org/2001/XMLSchema}double"/>
                             *         &lt;element name="duration" type="{http://www.w3.org/2001/XMLSchema}double"/>
                             *       &lt;/sequence>
                             *     &lt;/restriction>
                             *   &lt;/complexContent>
                             * &lt;/complexType>
                             * </pre>
                             * 
                             * 
                             */
                            @XmlAccessorType(XmlAccessType.FIELD)
                            @XmlType(name = "", propOrder = {
                                "cost",
                                "deadline",
                                "makespan",
                                "duration"
                            })
                            public static class Static {

                                protected double cost;
                                protected double deadline;
                                protected double makespan;
                                protected double duration;

                                /**
                                 * Ruft den Wert der cost-Eigenschaft ab.
                                 * 
                                 */
                                public double getCost() {
                                    return cost;
                                }

                                /**
                                 * Legt den Wert der cost-Eigenschaft fest.
                                 * 
                                 */
                                public void setCost(double value) {
                                    this.cost = value;
                                }

                                /**
                                 * Ruft den Wert der deadline-Eigenschaft ab.
                                 * 
                                 */
                                public double getDeadline() {
                                    return deadline;
                                }

                                /**
                                 * Legt den Wert der deadline-Eigenschaft fest.
                                 * 
                                 */
                                public void setDeadline(double value) {
                                    this.deadline = value;
                                }

                                /**
                                 * Ruft den Wert der makespan-Eigenschaft ab.
                                 * 
                                 */
                                public double getMakespan() {
                                    return makespan;
                                }

                                /**
                                 * Legt den Wert der makespan-Eigenschaft fest.
                                 * 
                                 */
                                public void setMakespan(double value) {
                                    this.makespan = value;
                                }

                                /**
                                 * Ruft den Wert der duration-Eigenschaft ab.
                                 * 
                                 */
                                public double getDuration() {
                                    return duration;
                                }

                                /**
                                 * Legt den Wert der duration-Eigenschaft fest.
                                 * 
                                 */
                                public void setDuration(double value) {
                                    this.duration = value;
                                }

                            }

                        }

                    }

                }

            }

        }

    }

}
