package it.unipi.m598992.xmlSerializer.xmlModel;

public class XMLFieldElement implements XMLElement {
    private final String type;
    private final String name;
    private final String value;

    public XMLFieldElement(String type, String name, String value) {
        this.type = type;
        this.name = name;
        this.value = value;
    }

    @Override
    public String toXML() {
        return String.format("<%s type=\"%s\">%s</%s>%n", name, type, value, name);
    }
}
