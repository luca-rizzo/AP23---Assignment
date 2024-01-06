package it.unipi.m598992.xmlSerializer.xmlModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class XMLClassElement implements XMLElement {

    private final List<XMLElement> subElement;
    private final String className;

    public XMLClassElement(String className) {
        this.className = className;
        subElement = new ArrayList<>();
    }

    public void addSubElement(XMLElement xmlElement) {
        subElement.add(xmlElement);
    }
    public void addSubElements(Collection<XMLElement> xmlElements) {
        subElement.addAll(xmlElements);
    }

    @Override
    public String toXML() {
        String subElementXml = subElement.stream()
                .map(subElement -> String.format("    %s", subElement.toXML()))
                .collect(Collectors.joining());
        return String.format("<%s>%n%s</%s>%n",
                className,
                subElementXml,
                className);
    }
}
