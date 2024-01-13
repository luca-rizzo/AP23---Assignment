package it.unipi.m598992.xmlSerializer.xmlModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Represents an XML element that can contain sub-elements, following the Composite design pattern
 * and implementing common XMLElement interface.
 * It serves as the Composite element in the pattern that contain other sub-elements, leaves or other containers.
 */
public class XMLClassElement implements XMLElement {

    private final List<XMLElement> subElements;
    private final String className;

    /**
     * Constructs an XMLClassElement with a given class name.
     *
     * @param className Name of the class
     */
    public XMLClassElement(String className) {
        this.className = className;
        subElements = new ArrayList<>();
    }

    /**
     * Adds a collection of sub-elements to this XMLClassElement.
     *
     * @param xmlElements Collection of sub-elements to add
     */
    public void addSubElements(Collection<XMLElement> xmlElements) {
        subElements.addAll(xmlElements);
    }

    /**
     * Generates the XML representation of the XMLClassElement and its sub-elements.
     *
     * @return XML representation as a string
     */
    @Override
    public String toXML() {
        // Using stream and collect to concatenate the XML representation of sub-elements
        String subElementsXml = subElements.stream()
                .map(subElement -> String.format("    %s", subElement.toXML()))
                .collect(Collectors.joining());

        // Creating the XML representation of the XMLClassElement
        return String.format("<%s>%n%s</%s>%n",
                className,
                subElementsXml,
                className);
    }
}
