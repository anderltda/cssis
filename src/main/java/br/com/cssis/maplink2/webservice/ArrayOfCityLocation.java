
package br.com.cssis.maplink2.webservice;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ArrayOfCityLocation complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ArrayOfCityLocation">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="CityLocation" type="{http://webservices.maplink2.com.br}CityLocation" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArrayOfCityLocation", propOrder = {
    "cityLocation"
})
public class ArrayOfCityLocation {

    @XmlElement(name = "CityLocation", required = true)
    protected List<CityLocation> cityLocation;

    /**
     * Gets the value of the cityLocation property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the cityLocation property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCityLocation().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link CityLocation }
     * 
     * 
     */
    public List<CityLocation> getCityLocation() {
        if (cityLocation == null) {
            cityLocation = new ArrayList<CityLocation>();
        }
        return this.cityLocation;
    }

}
