package it.unipi.m598992.xmlSerializer;

import it.unipi.m598992.xmlSerializer.annotations.XMLable;
import it.unipi.m598992.xmlSerializer.annotations.XMLfield;
import it.unipi.m598992.xmlSerializer.exception.XMLSerializationException;
import it.unipi.m598992.xmlSerializer.xmlModel.XMLClassElement;
import it.unipi.m598992.xmlSerializer.xmlModel.XMLElement;
import it.unipi.m598992.xmlSerializer.xmlModel.XMLFieldElement;
import it.unipi.m598992.xmlSerializer.xmlModel.XMLNoSerializableElement;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Class responsible for converting a Java object into an XML element representation.
 */
public class XMLElementParser {

    /**
     * Converts a Java object into an XML element using specific annotations.
     * It follows the composite design pattern, returning an element which can (eventually)
     * represent a complex tree structure of xml fields.
     *
     * @param obj Java object to convert
     * @return XMLElement corresponding to the object
     */
    public static XMLElement parseObjToXmlElement(Object obj) {
        Class<?> objClass = obj.getClass();
        XMLable classAnnotation = objClass.getAnnotation(XMLable.class);
        // If the annotation is present, serialize the object; otherwise, return an XMLNoSerializableElement
        return classAnnotation != null ?
                createXMLClass(obj, objClass) :
                new XMLNoSerializableElement();
    }

    /**
     * Creates an XML element representing the class of the Java object and its fields.
     *
     * @param obj      Java object to analyze
     * @param objClass Class of the Java object
     * @return XML element representing the class
     */
    private static XMLClassElement createXMLClass(Object obj, Class<?> objClass) {
        XMLClassElement xmlClass = new XMLClassElement(objClass.getSimpleName());
        // Extracts all XML fields of the class
        List<XMLElement> allXMLField = getAllXMLField(obj);
        // Adds them to the XML class element
        xmlClass.addSubElements(allXMLField);
        return xmlClass;
    }

    /**
     * Gets all XML elements representing the fields of the Java object.
     *
     * @param obj Java object to analyze
     * @return List of XML elements representing the fields
     */
    private static List<XMLElement> getAllXMLField(Object obj) {
        Class<?> objClass = obj.getClass();
        Field[] declaredFields = objClass.getDeclaredFields();
        // For each field, gets the XML field if present
        return Arrays.stream(declaredFields)
                .flatMap(field -> getXMLField(field, obj).stream())
                .toList();
    }

    /**
     * Gets an XML element representing the specified field of the Java object.
     *
     * @param field Java field to analyze
     * @param obj   Java object containing the field
     * @return Optional of XML element representing the field (empty if not present)
     */
    private static Optional<XMLElement> getXMLField(Field field, Object obj) {
        XMLfield fieldAnnotation = field.getAnnotation(XMLfield.class);
        // Returns Optional.empty() if the annotation is not present; otherwise, maps the Java Field to an XML Field representation
        return Optional.ofNullable(fieldAnnotation)
                .map(annotation -> createXMLField(field, obj, annotation));
    }

    /**
     * Creates an XML field element from the information extracted from the Java field.
     *
     * @param field      Java field to represent
     * @param obj        Java object containing the field
     * @param annotation XMLfield annotation associated with the field
     * @return XML field element
     */
    private static XMLFieldElement createXMLField(Field field, Object obj, XMLfield annotation) {
        Object fieldValue = getFieldValue(field, obj);
        String fieldName = getFieldName(field, annotation);
        // Creates a new XML field element from the extracted information
        return new XMLFieldElement(annotation.type(), fieldName, fieldValue.toString());
    }

    /**
     * Gets the value of the specified field from the Java object.
     *
     * @param field Java field to get the value from
     * @param obj   Java object containing the field
     * @return Value of the field
     * @throws XMLSerializationException if there is an issue accessing the field
     */
    private static Object getFieldValue(Field field, Object obj) {
        try {
            // Allows getting the value even for private fields
            field.setAccessible(true);
            return field.get(obj);
        } catch (IllegalAccessException e) {
            throw new XMLSerializationException(e.getMessage());
        }
    }

    /**
     * Gets the name of the specified field based on the XMLfield annotation.
     *
     * @param field           Java field to get the name from
     * @param fieldAnnotation XMLfield annotation associated with the field
     * @return Field name (either from the annotation or the field itself)
     */
    private static String getFieldName(Field field, XMLfield fieldAnnotation) {
        // If a name is present in the annotation, use it; otherwise, use the name of the field
        return fieldAnnotation.name().isEmpty() ? field.getName() : fieldAnnotation.name();
    }
}
