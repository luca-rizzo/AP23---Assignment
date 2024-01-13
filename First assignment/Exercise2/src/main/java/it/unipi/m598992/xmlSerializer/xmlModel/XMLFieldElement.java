package it.unipi.m598992.xmlSerializer.xmlModel;

/**
 * Represents an XML field element, following the Composite design pattern
 * and implementing common XMLElement interface.
 * It serves as the Leaf in the Composite pattern that does not have sub-elements.
 */
public class XMLFieldElement implements XMLElement {
    private final String type;
    private final String name;
    private final String value;

    /**
     * Constructs an XMLFieldElement with a given type, name, and value.
     *
     * @param type  Type of the field
     * @param name  Name of the field
     * @param value Value of the field
     */
    public XMLFieldElement(String type, String name, String value) {
        this.type = type;
        this.name = name;
        this.value = value;
    }

    /**
     * Generates the XML representation of the XMLFieldElement.
     *
     * @return XML representation as a string
     */
    @Override
    public String toXML() {
        return String.format("<%s type=\"%s\">%s</%s>%n", name, type, value, name);
    }
}
