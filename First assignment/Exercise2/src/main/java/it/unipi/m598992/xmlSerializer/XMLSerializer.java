package it.unipi.m598992.xmlSerializer;

import it.unipi.m598992.xmlSerializer.exception.XMLSerializationException;
import it.unipi.m598992.xmlSerializer.xmlModel.XMLElement;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Set;

/**
 * Utility class for serializing an array of objects into XML format.
 */
public class XMLSerializer {

    // XML declaration for the start of the XML file
    private static final String XML_START_FILE = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
    // File extension for XML files
    public static final String XML_EXTENSION = ".xml";

    /**
     * Serializes an array of objects into an XML file.
     *
     * @param arr      Array of objects to be serialized
     * @param fileName Name of the XML file to be created
     * @throws XMLSerializationException if there is an issue during serialization
     */
    public static void serialize(Object[] arr, String fileName) throws XMLSerializationException {
        // Ensure the full file name has the XML extension
        String fullFileName = fileName.endsWith(XML_EXTENSION) ? fileName : fileName + XML_EXTENSION;
        try (PrintWriter printWriter = new PrintWriter(new FileWriter(fullFileName))) {
            // Write the XML declaration at the beginning of the file
            printWriter.println(XML_START_FILE);
            // Set to keep track of already deserialized classes
            Set<Class<?>> alreadyDeserialized = new HashSet<>();
            // Loop through each object in the array
            for (Object obj : arr) {
                Class<?> objClass = obj.getClass();
                // Skip if the class has already been deserialized
                if (alreadyDeserialized.contains(objClass)) {
                    continue;
                }
                alreadyDeserialized.add(objClass);
                // Parse the object into an XML element using the Composite design pattern
                // This approach allows transparent serialization of objects with non-primitive fields
                // By implementing another annotation, a non-primitive field could potentially become a sub-element of XMLClass
                XMLElement xmlElement = XMLElementParser.parseObjToXmlElement(obj);
                // The toXML() method can interact with a very complex object structure or a simple one through the same interface
                // This flexibility is provided by the Composite design pattern, allowing consistent handling of diverse object structures
                // Write the XML representation of the object to the file
                printWriter.print(xmlElement.toXML());
            }
        } catch (IOException e) {
            // Throw a custom exception if there is an issue with file I/O
            throw new XMLSerializationException(e.getMessage());
        }
    }
}
