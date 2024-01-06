package it.unipi.m598992.xmlSerializer.xmlModel;

public class XMLNoSerializableElement implements XMLElement {
    @Override
    public String toXML() {
        return "<notXMLable/>\n";
    }
}
