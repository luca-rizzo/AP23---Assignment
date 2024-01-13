package it.unipi.m598992.xmlSerializer.xmlModel;

/**
 * Interface representing an XML element, following the Composite design pattern.
 * It serves as the Component interface in the Composite design pattern.
 * It describes operations that are common to both simple and complex elements of the tree.
 */
public interface XMLElement {
    /**
     * Generates the XML representation of the element.
     *
     * @return XML representation as a string
     */
    String toXML();
}
