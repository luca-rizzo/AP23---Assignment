package it.unipi.m598992.xmlSerializer.xmlModel;

/**
 * Represents an XML element that is not serializable, following the Composite design pattern
 * and implementing common XMLElement interface.
 * It serves as a Leaf in the Composite design pattern.
 */
public class XMLNoSerializableElement implements XMLElement {

    /**
     * Generates the XML representation of the XMLNoSerializableElement.
     *
     * @return XML representation as a string
     */
    @Override
    public String toXML() {
        return "<notXMLable/>\n";
    }
}
