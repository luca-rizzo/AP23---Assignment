package it.unipi.m598992.xmlSerializer;

import it.unipi.m598992.xmlSerializer.exception.XMLSerializationException;
import it.unipi.m598992.xmlSerializer.xmlModel.XMLElement;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

public class XMLSerializer {

    private static final String XML_START_FILE = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
    public static final String XML_EXTENSION = ".xml";

    public static void serialize(Object[] arr, String fileName) throws XMLSerializationException {
        String fullFileName = fileName.endsWith(XML_EXTENSION) ? fileName : fileName + XML_EXTENSION;
        try (PrintWriter printWriter = new PrintWriter(new FileWriter(fullFileName))) {
            printWriter.println(XML_START_FILE);
            Set<Class<?>> alreadyDeserialized = new HashSet<>();
            for (Object obj : arr) {
                Class<?> objClass = obj.getClass();
                if (alreadyDeserialized.contains(objClass)) {
                    continue;
                }
                alreadyDeserialized.add(objClass);
                XMLElement xmlElement = XMLElementParser.parseObjToXmlElement(obj);
                printWriter.print(xmlElement.toXML());
            }
        } catch (IOException e) {
            throw new XMLSerializationException(e.getMessage());
        }
    }

}
